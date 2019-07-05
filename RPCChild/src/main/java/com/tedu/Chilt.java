package com.tedu;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Chilt {
    public Object chileSocket(String address,int prot,RPCEntry rpcEntry) throws IOException, ClassNotFoundException {
        Socket socket=new Socket("127.0.0.1",9999);
        OutputStream outputStream = socket.getOutputStream();

        rpcEntry=new RPCEntry();
        rpcEntry.setClassName("OrderService");
        rpcEntry.setMethodName("modelOne");
        rpcEntry.setPackageName("com.tedu.issue");
        Object[] obj=new Object[1];
        obj[0]="66";

        rpcEntry.setObject(obj);
        ObjectOutputStream objectOutputStream=new ObjectOutputStream(outputStream);
        objectOutputStream.writeObject(rpcEntry);
        ServerSocket serverSocket=new ServerSocket(9998);
        socket= serverSocket.accept();
        InputStream inputStream=socket.getInputStream();
        ObjectInputStream objectInputStream=new ObjectInputStream(inputStream);
        return objectInputStream.readObject();
    }
}
