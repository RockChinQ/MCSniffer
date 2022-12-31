package mapping;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;

public class MappingTunnel extends Thread{
    public Socket clientSocket;
    public Socket remoteSocket;
    public MappingRule mappingRule;
    public IExceptionListener exceptionListener;
    public Proxy proxy;

    public Forwarder clientToRemoteForwarder;
    public Forwarder remoteToClientForwarder;
    public MappingTunnel(Socket clientSocket, MappingRule mappingRule,
                         IExceptionListener exceptionListener, Proxy proxy) {
        this.clientSocket = clientSocket;
        this.mappingRule = mappingRule;
        this.exceptionListener = exceptionListener;
        this.proxy = proxy;
    }

    @Override
    public void run() {
        super.run();
        try {
            //连接远程
            Socket remoteSocket;
            if (proxy!=null){
                remoteSocket=new Socket(proxy);
            }else {
                remoteSocket=new Socket();
            }
            remoteSocket.connect(new InetSocketAddress(mappingRule.remoteHost,mappingRule.remotePort),5000);
            this.remoteSocket=remoteSocket;
            //开始转发

            DataInputStream clientInputStream=new DataInputStream(clientSocket.getInputStream());
            DataInputStream remoteInputStream=new DataInputStream(remoteSocket.getInputStream());

            DataOutputStream clientOutputStream=new DataOutputStream(clientSocket.getOutputStream());
            DataOutputStream remoteOutputStream=new DataOutputStream(remoteSocket.getOutputStream());

            clientToRemoteForwarder=new Forwarder(clientInputStream,remoteOutputStream,exceptionListener);
            remoteToClientForwarder=new Forwarder(remoteInputStream,clientOutputStream,exceptionListener);

            clientToRemoteForwarder.start();
            remoteToClientForwarder.start();
        } catch (Exception e) {
            exceptionListener.onException(this,e);
        }
    }

    public void close(){
        clientToRemoteForwarder.close();
        remoteToClientForwarder.close();
    }
}
