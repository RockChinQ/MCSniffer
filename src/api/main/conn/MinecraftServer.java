package api.main.conn;


import com.google.gson.Gson;
import api.main.api.IServerInfo;
import sun.misc.BASE64Decoder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A MinecraftServer instance provides methods to create connection to server and communicate with peer.
 * This is the basic instance and any info-getting function is base on this class.
 *
 * @author Rock Chin
 */
public class MinecraftServer implements IServerInfo {
    private Socket socket;
    private DataOutputStream dataOutputStream;
    private DataInputStream dataInputStream;
    private String host;
    private int port=25565;
    private Response response=null;
    private boolean available=false;
    private String jsonStr;
    private boolean debug=false;

    public MinecraftServer(String host,int port,boolean debugMode,int timeout)throws Exception{
        debug=debugMode;
        long start=new Date().getTime();
        initTimeControl(host,port,timeout);
        debugMsg("Spent:"+(new Date().getTime()-start)+"ms");
    }
    public MinecraftServer(String host,int port,boolean debugMode)throws Exception{
        debug=debugMode;
        long start=new Date().getTime();
        initTimeControl(host,port,10000);
        debugMsg("Spent:"+(new Date().getTime()-start)+"ms");
    }
    public MinecraftServer(String host,int port)throws Exception{
        debug=false;
        long start=new Date().getTime();
        initTimeControl(host,port,10000);
        debugMsg("Spent:"+(new Date().getTime()-start)+"ms");
    }


    private void debugMsg(String str){
        if (debug){
            System.out.println("[APIDebug]"+str);
        }
    }

    private void initTimeControl(String host,int port,int timeout)throws Exception{
        AtomicReference<Exception> e=new AtomicReference<>();
        final Boolean timeLock=false;
        Thread work=new Thread(()->{
            try {
                init(host,port,timeout);
            }catch (Exception e1){
                e.set(e1);
            }finally {
                synchronized (timeLock){
                    timeLock.notify();
                }
            }
        });

        Thread timeControl=new Thread(()->{
            try{
                Thread.sleep(timeout);
                if (work.isAlive()){
                    work.stop();
                    synchronized (timeLock){
                        timeLock.notify();
                    }
                }
            }catch (Exception ignored){}
        });

        work.start();
        timeControl.start();

        synchronized (timeLock){
            timeLock.wait();
        }
        try{
            debugMsg("Socket closed");
            work.stop();
            timeControl.stop();
            socket.close();
            dataInputStream.close();
            dataOutputStream.close();
        }catch (Exception ignored){}
        if (e.get()!=null){
            throw e.get();
        }
    }

    private void init(String host,int port,int timeout)throws Exception {
        debugMsg("MakingSocket...");
        socket=new Socket();
        socket.connect(new InetSocketAddress(host,port),timeout);
        this.host=host;
        this.port=port;
        dataInputStream=new DataInputStream(socket.getInputStream());
        dataOutputStream=new DataOutputStream(socket.getOutputStream());
        debugMsg("SocketMadeSuccessfully.");
        new PacketSend(0).addVarInt(755)
                .addString(host)
                .addShort(port)
                .addVarInt(1).write(dataOutputStream);
        new PacketSend(0).write(dataOutputStream);
        debugMsg("WroteRequestPacket.");
        try {
            jsonStr=new PacketRecv(dataInputStream).popString();
            debugMsg("ReadJSONData:"+jsonStr);
            response = new Gson().fromJson(jsonStr.endsWith("}")?jsonStr:jsonStr+"}", Response.class);
            if (response==null){
                available=false;
                debugMsg("ResponseIsNull.");
                throw new EOFException("Invalid server response.");
            }
            debugMsg("done.");
            available=true;
        }catch (EOFException e){//To change protocol.
            debugMsg("LegacyServer,protocolChanged.");
            socket=new Socket();
            socket.connect(new InetSocketAddress(host,port),timeout);
            dataInputStream=new DataInputStream(socket.getInputStream());
            dataOutputStream=new DataOutputStream(socket.getOutputStream());

            dataOutputStream.write(0xFE);
            dataOutputStream.write(0x01);
            dataOutputStream.flush();

            if (dataInputStream.readByte()==-1){
                dataInputStream.readByte();
                dataInputStream.readByte();
                debugMsg("ReadingResponseFromALegacyServer.");
                byte[] b=new byte[512];
                dataInputStream.read(b);
                ByteBase bbase= new ByteBase(b);
                byte[] end=new byte[]{0,0};
                if (new String(bbase.pop(end),StandardCharsets.UTF_16BE).equals("§1")){
                    response=new Response();
                    response.version=new Response.version();
                    response.version.protocol=Integer.parseInt
                            (new String(bbase.pop(end),StandardCharsets.UTF_16BE));
                    response.version.name=new String(bbase.pop(end),StandardCharsets.UTF_16BE);
                    response.description=new Response.Description();
                    response.description.text=new String(bbase.pop(end),StandardCharsets.UTF_16BE);
                    response.players=new Response.Players();
                    response.players.online=Integer.parseInt
                            (new String(bbase.pop(end),StandardCharsets.UTF_16BE));
                    response.players.max=Integer.parseInt
                            (new String(bbase.pop(end),StandardCharsets.UTF_16BE));
                    debugMsg("done.");
                    available=true;
                }
            }
            if (!available){//version lower then 1.4
                debugMsg("LowerServer,protocolChanged.");
                socket=new Socket();
                socket.connect(new InetSocketAddress(host,port),timeout);
                dataInputStream=new DataInputStream(socket.getInputStream());
                dataOutputStream=new DataOutputStream(socket.getOutputStream());

                dataOutputStream.write(0xFE);
                dataOutputStream.flush();

                if (dataInputStream.readByte()==-1){
                    dataInputStream.readByte();
                    dataInputStream.readByte();
                    debugMsg("ReadingResponseFromAVeryLowServer");
                    byte[] b=new byte[512];
                    dataInputStream.read(b);
                    ByteBase bbase= new ByteBase(b);
                    byte[] end=new byte[]{0, (byte) 0xa7};
                    byte[] zeroEnd=new byte[]{0, 0};

                    response=new Response();
                    response.version=new Response.version();
                    response.version.name="<1.4";
                    response.version.protocol=-1;

                    response.description=new Response.Description();
                    response.description.text=new String(bbase.pop(end),StandardCharsets.UTF_16BE);

                    response.players=new Response.Players();
                    response.players.online=Integer.parseInt(new String(bbase.pop(end),StandardCharsets.UTF_16BE));
                    response.players.max=Integer.parseInt(new String(bbase.pop(zeroEnd),StandardCharsets.UTF_16BE));
                    debugMsg("done.");
                    available=true;
                }
            }
        }
    }
    private short len(String l){
        String utf16be=new String(l.getBytes(StandardCharsets.UTF_8),StandardCharsets.UTF_16BE);
        return (short)utf16be.length();
    }
    private static class ByteBase{
        byte[] arr;
        int index=0;
        ByteBase(byte[] byteArr){
            this.arr=byteArr;
//            System.out.print("list:");
//            for (byte b:byteArr){
//                System.out.print(b+" ");
//            }
//            System.out.println();
        }
        public byte[] pop(byte[] end){
            ArrayList<Byte> byteArrayList=new ArrayList<>();
            byte[] b=new byte[2];
            while(true){
                if (index>=arr.length-1)
                    break;
                b=pop(2);
                if (b[0]==end[0]&&b[1]==end[1]){
                    break;
                }
                byteArrayList.add(b[0]);
                byteArrayList.add(b[1]);
            }
            int len=byteArrayList.size();
            byte[] result=new byte[len];
            for (int i=0;i<len;i++){
                result[i]=byteArrayList.get(i);
            }
            return result;
        }
        public byte pop(){
            return arr[index++];
        }
        public byte[] pop(int len){
            byte[] result=new byte[len];
            for (int i=0;i<len;i++){
                result[i]=pop();
            }
            return result;
        }
    }

    public static class Response{
        static class Description {
            String text;
            String color;
            Description[] extra;
        }
        Description description;
        static class Players {
            int max;
            int online;
            class player{
                String name;
                String id;
            }
            player[] sample;
        }
        Players players;
        static class version{
            String name;
            int protocol;
        }
        version version;
        String favicon;
        ModInfo modinfo;

        ModPackData modpackData;
        ForgeData forgeData;
    }

    @Override
    public boolean isAvailable(){
        return available;
    }

    @Override
    public String getVersionName() {
        if (response!=null&&response.version!=null) {
            return response.version.name;
        }else {
            return null;
        }
    }

    @Override
    public int getVersionProtocol() {
        if (response!=null&&response.version!=null) {
            return response.version.protocol;
        }else {
            return -1;
        }
    }

    @Override
    public int getMaxPlayer() {
        if (response!=null&&response.players!=null) {
            return response.players.max;
        }else {
            return -1;
        }
    }

    @Override
    public int getOnlinePlayer() {
        if (response!=null&&response.players!=null) {
            return response.players.online;
        }else {
            return -1;
        }
    }

    @Override
    public Player[] getPlayerList() {
        if (response!=null&&response.players!=null&&response.players.sample!=null) {
            int len = response.players.sample.length;
            Player[] players = new Player[len];
            for (int i = 0; i < len; i++) {
                players[i] = new Player();
                players[i].name = response.players.sample[i].name;
                players[i].id = response.players.sample[i].id;
            }
            return players;
        }else {
            return new Player[0];
        }
    }

    @Override
    public String getDefaultDescriptionText() {
        if (response!=null&&response.description!=null) {
            return response.description.text;
        }else {
            return null;
        }
    }
    @Override
    public String getDefaultDescriptionColor(){
        if (response!=null&&response.description!=null) {
            return response.description.color;
        }else {
            return null;
        }
    }
    @Override
    public ExtraDescr[] getExtraDescription(){
        if (response!=null&&response.description!=null&&response.description.extra!=null) {
            ExtraDescr[] extraDescrs = new ExtraDescr[response.description.extra.length];
            for (int i = 0; i < response.description.extra.length; i++) {
                extraDescrs[i] = new ExtraDescr();
                extraDescrs[i].color = response.description.extra[i].color;
                extraDescrs[i].text = response.description.extra[i].text;
            }
            return extraDescrs;
        }else {
            return new ExtraDescr[0];
        }
    }
    @Override
    public String getFaviconBase64() {
        if (response!=null) {
            return response.favicon;
        }else {
            return null;
        }
    }
    @Override
    public BufferedImage getFaviconImage(){
        try{
            if (getFaviconBase64()!=null)
                return base64ToBufferedImage(getFaviconBase64().split(",")[1]);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    @Override
    public String getRawJSONString(){
        return jsonStr;
    }
    private  static BufferedImage base64ToBufferedImage(String base64)throws IOException {
        BASE64Decoder decoder = new BASE64Decoder();
        byte[] bytes1 = decoder.decodeBuffer(base64);
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes1);
        return ImageIO.read(bais);
    }

    @Override
    public ModInfo getModInfo() {
        return response.modinfo;
    }

    @Override
    public ModPackData getModPackData() {
        return response.modpackData;
    }

    @Override
    public ForgeData getForgeData() {
        return response.forgeData;
    }
}
