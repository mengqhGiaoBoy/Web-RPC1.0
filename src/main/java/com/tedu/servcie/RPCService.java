package com.tedu.servcie;

import com.tedu.entry.MethodEntry;
import com.tedu.RPCEntry;
import com.tedu.util.ClassIssue;

import java.io.*;
import java.lang.reflect.Method;
import java.net.Socket;

public class RPCService implements Runnable {
    Socket accept=null;

    public RPCService(Socket socket) {
        this.accept=socket;
    }
    @Override
    public void run() {

        //获取客户端发送到字节流
        InputStream inputStream = null;
        OutputStream outputStream=null;
        try {
            inputStream = accept.getInputStream();
            Socket socket=new Socket("127.0.0.1",9998);
            outputStream=socket.getOutputStream();
            //将客户端的字节流转为对象流
            ObjectInputStream objectInputStream=new ObjectInputStream(inputStream);
            ObjectOutputStream objectOutputStream=new ObjectOutputStream(outputStream);
            //将对象流转为对象并强转为RPCEntry对象
            RPCEntry obj =(RPCEntry) objectInputStream.readObject();
            //获取方法名字
            String methodName=obj.getMethodName();
            //获取方法参数
            Object[] object= obj.getObject();
            //方法参数数组
            //判断客户端调用的类是否允许发布
            Class  clazz= ClassIssue.isPRCEntry(obj);
            if (clazz==null){
                return;
            }
            MethodEntry methodEntry=new MethodEntry();
            methodEntry.setMethodName(methodName);
            methodEntry.setObjects(object);
            //需要执行的方法
            Method method=ClassIssue.isGetMethod(clazz,methodEntry);
            if (method==null){
                System.out.println("您输入的有误");
                return;
            }
            // 用户请求的参数列表
            //这个变量的赋值一定要在method之后
            Object[] params=ClassIssue.params;
            //实例化class对象
            Object objectNew= clazz.newInstance();
            //获取返回对象
            Object objectAll=method.invoke(objectNew,params);
            System.out.println(objectAll);
            //将结果通过流写回去
            objectOutputStream.writeObject(objectAll);

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                inputStream.close();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
