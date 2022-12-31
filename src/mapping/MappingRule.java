package mapping;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class MappingRule extends Thread{
    public int localPort=25565;
    public String remoteHost="localhost";
    public int remotePort=25565;

    public String proxyURL=null;
    public Proxy proxy=null;

    public ServerSocket serverSocket;
    public IExceptionListener exceptionListener;

    public ArrayList<MappingTunnel> mappingTunnels=new ArrayList<>();
    public MappingRule(int localPort, String remoteHost, int remotePort,String proxyURL,IExceptionListener exceptionListener) throws Exception {
        this.localPort = localPort;
        this.remoteHost = remoteHost;
        this.remotePort = remotePort;
        this.proxyURL=proxyURL;

        if (proxyURL!=null&&proxyURL.trim().length()>0){
            String[] proxyURLSplit=proxyURL.split(":");
            this.proxy=new Proxy(Proxy.Type.HTTP,new InetSocketAddress(proxyURLSplit[0],Integer.parseInt(proxyURLSplit[1])));
        }
        testConfig();
        this.exceptionListener=exceptionListener;
        if (exceptionListener==null){
            this.exceptionListener=new IExceptionListener() {
                @Override
                public void onException(MappingRule mappingRule, Exception e) {
                    e.printStackTrace();
                }

                @Override
                public void onException(MappingTunnel mappingTunnel, Exception e) {
                    e.printStackTrace();
                }

                @Override
                public void onException(Forwarder forwarder, Exception e) {
                    e.printStackTrace();
                }
            };
        }
    }

    public void testConfig() throws IOException {
        //测试远程是否可以连接
        Socket socket;
        if (proxy!=null){
            socket=new Socket(proxy);
        }else {
            socket=new Socket();
        }

        socket.connect(new InetSocketAddress(remoteHost,remotePort),5000);

        socket.close();
        //测试本地是否可以监听
        ServerSocket serverSocket=new ServerSocket(localPort);
        serverSocket.close();
    }

    @Override
    public void run(){
        try {
            serverSocket = new ServerSocket(localPort);
            while (true){
                Socket socket=serverSocket.accept();

                MappingTunnel mappingTunnel=new MappingTunnel(socket,this,exceptionListener,proxy);
                mappingTunnels.add(mappingTunnel);
                mappingTunnel.start();
            }
        }catch (IOException e) {
            this.exceptionListener.onException(this,e);
        }
    }

    public void close(){
        try {
            serverSocket.close();
        } catch (Exception e) {
            this.exceptionListener.onException(this,e);
        }

        for (MappingTunnel mappingTunnel:mappingTunnels){
            mappingTunnel.close();
        }

        super.stop();
    }
}
