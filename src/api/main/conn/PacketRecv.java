package api.main.conn;

import java.io.DataInputStream;
import java.io.IOException;

public class PacketRecv {
    DataInputStream dataInputStream;
    byte[] packet;
    int id=0;
    byte[] data;
    int length;

    private int index=0;
    public PacketRecv(DataInputStream dataInputStream)throws IOException {
        this.dataInputStream=dataInputStream;
        length=readVarInt();
        packet=new byte[length];
        for(int i=0;i<length;i++){
            packet[i]=dataInputStream.readByte();
        }
        id=popVarInt();
    }
    public byte popByte(){
        return packet[index++];
    }
    public int pop(byte[] b){
        return pop(b,0, packet.length-index);
    }
    public int pop(byte[] b,int off,int len){
        for (int i=0;i<len;i++){
            b[i]=packet[index+i+off];
        }
        return len;
    }
    public int popVarInt(){

        int numRead = 0;
        int result = 0;
        byte read;
        do {
            read = popByte();
            int value = (read & 0b01111111);
            result |= (value << (7 * numRead));

            numRead++;
            if (numRead > 5) {
                throw new RuntimeException("VarInt is too big");
            }
        } while ((read & 0b10000000) != 0);

        return result;
    }

    public String popString(){
        int len=popVarInt();
        byte[] b=new byte[len];
        pop(b,0,len);
        return new String(b);
    }



    public int readVarInt()throws IOException {
        int numRead = 0;
        int result = 0;
        byte read;
        do {
            read = dataInputStream.readByte();
            int value = (read & 0b01111111);
            result |= (value << (7 * numRead));

            numRead++;
            if (numRead > 5) {
                throw new RuntimeException("VarInt is too big");
            }
        } while ((read & 0b10000000) != 0);

        return result;
    }

}
