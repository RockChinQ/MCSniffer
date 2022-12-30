package api.main.conn;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Provides a convenient way to make a packet and send it to provided peer.
 * Supports chain-style creating methods.
 * e.g. packetObj.addVarInt(0).addString("exampleString").addVarInt(2).write(definedDataOutputStreamObj);
 *
 * @author Rock Chin
 */
public class PacketSend {
    private ArrayList<Byte> dataBuf=new ArrayList<>();
    private int packetID=0;
    public int getPacketID(){
        return packetID;
    }
    public PacketSend(int packetID){
        this.packetID=packetID;
        addVarInt(packetID);
    }
    public void write(DataOutputStream dataOutputStream)throws IOException{
        int size=dataBuf.size();
        writeVarInt(size,dataOutputStream);
        byte[] temp=new byte[size];
        for (int i=0;i<size;i++){
            temp[i]=dataBuf.get(i);
        }
        dataOutputStream.write(temp);
        dataOutputStream.flush();
    }

    private void writeVarInt(int value,DataOutputStream dataOutputStream)throws IOException{
        do {
            byte temp = (byte)(value & 0b01111111);
            // Note: >>> means that the sign bit is shifted with the rest of the number rather than being left alone
            value >>>= 7;
            if (value != 0) {
                temp |= 0b10000000;
            }
            dataOutputStream.write(temp);
            dataOutputStream.flush();
        } while (value != 0);
    }
    private void append(byte b){
        dataBuf.add(b);
    }
    private void append(byte[] bArr){
        for (byte b:bArr){
            append(b);
        }
    }
    public PacketSend addVarInt(int i){
        do {
            byte temp = (byte)(i & 0b01111111);
            // Note: >>> means that the sign bit is shifted with the rest of the number rather than being left alone
            i >>>= 7;
            if (i != 0) {
                temp |= 0b10000000;
            }
            append(temp);
        } while (i != 0);
        return this;
    }
    public PacketSend addString(String s){
        byte[] b=s.getBytes();
        addVarInt(b.length);
        append(b);
        return this;
    }
    public PacketSend addShort(int s){
        append((byte) ((s >>> 8) & 0xFF));
        append((byte) ((s >>> 0) & 0xFF));
        return this;
    }
}
