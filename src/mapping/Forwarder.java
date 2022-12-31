package mapping;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class Forwarder extends Thread{
    public DataInputStream inputStream;
    public DataOutputStream outputStream;
    public IExceptionListener exceptionListener;
    public Forwarder(DataInputStream inputStream, DataOutputStream outputStream,IExceptionListener exceptionListener) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.exceptionListener=exceptionListener;
    }

    @Override
    public void run(){
        super.run();
        try {
            byte[] buffer=new byte[1024];
            int len;
            while ((len=inputStream.read(buffer))!=-1){
                outputStream.write(buffer,0,len);
            }
        }catch (Exception e){
            this.exceptionListener.onException(this,e);
        }
    }

    public void close(){
        try {
            inputStream.close();
            outputStream.close();
        }catch (Exception e){
            this.exceptionListener.onException(this,e);
        }
    }
}
