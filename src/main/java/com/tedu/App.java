package com.tedu;

import com.tedu.servcie.RPCService;
import com.tedu.util.XmlWrite;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


/**
 * Hello world!
 *
 */
public class App {

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        startWeb(9999);
    }
    public static void  startWeb(int port) throws IOException, ClassNotFoundException {
        XmlWrite xmlWrite=new XmlWrite();
        xmlWrite.writeXml();
        Executor executor= Executors.newFixedThreadPool(1);
        ServerSocket serverSocket = new ServerSocket(port);
        while (true){
            System.out.println("wait socket......");
            Socket accept = serverSocket.accept();
            System.out.println("ok one Thread....");
            executor.execute(new RPCService(accept));
        }
    }
}
