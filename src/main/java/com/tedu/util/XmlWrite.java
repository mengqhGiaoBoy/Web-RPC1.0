package com.tedu.util;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.junit.Test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.ListResourceBundle;
import java.util.Map;

public class XmlWrite {
    //获取物理资源路径
    String scanPath=null;
    String[] files=null;
    Map<String,Map<String,Map<List,String>>> classXin=new HashMap<>();
    //发布的类名与包
    static Map<String,String> classAndPackageNames= new HashMap<>();
   public  XmlWrite(){
       scanPath= XmlWrite.class.getResource("/") + "";
       //将scanPath 字符串中的file：/  去除
       scanPath=scanPath.substring(scanPath.indexOf("/"));
       //获取扫描路径的file对象集
       files=FIleScanHandler.fileScan(scanPath);
    }

    public void writeXml() throws ClassNotFoundException, IOException {
        //获取发布的类名与包
        classAndPackageNames=FIleScanHandler.filePackages(scanPath,files);
        Class clazz;
        //将class信息放到一起
        for (Map.Entry<String, String> stringStringEntry : classAndPackageNames.entrySet()) {
            String className=stringStringEntry.getKey();
            String packageName=stringStringEntry.getValue();
            clazz=Class.forName(packageName+"."+className);
            //获取类的所有信息
            Map<String,Map<List,String>> map=ClassIssue.getMethodXin(clazz);
            classXin.put(className,map);
        }
        Document document= DocumentHelper.createDocument();
        Element root = DocumentHelper.createElement("root");
        document.setRootElement(root);

        //为xml添加文件 key 类名， value 包名
        for (Map.Entry<String, String> stringStringEntry : classAndPackageNames.entrySet()) {
           Element packageEm= root.addElement("package");
           packageEm.addAttribute("name",stringStringEntry.getValue());
           Element classEm=packageEm.addElement("className");
           classEm.addAttribute("name",stringStringEntry.getKey());
           //当前类对应的map
            Map<String, Map<List, String>> stringMapMap = classXin.get(stringStringEntry.getKey());
            for (Map.Entry<String, Map<List, String>> stringMapEntry : stringMapMap.entrySet()) {
               Element method= classEm.addElement("methodName");
               method.addAttribute("name",stringMapEntry.getKey());

               //key  参数   value  返回值
                for (Map.Entry<List, String> stringEntry : stringMapEntry.getValue().entrySet()) {
                    for (Object params : stringEntry.getKey()) {
                        Element param = method.addElement("param");
                        param.setText((String) params);
                    }
                    Element returnParam = method.addElement("returnParam");
                    returnParam.setText(stringEntry.getValue());
                }
            }
        }
//写出输出流的格式，先空4格，再换行
        OutputFormat format = new OutputFormat("    ", true);
        //第一种
        XMLWriter xmlWriter2 = new XMLWriter(new FileOutputStream("XinXi.xml"), format);
        xmlWriter2.write(document);

        xmlWriter2.flush();
    }

    public static void main(String[] args) throws ClassNotFoundException, IOException {
        XmlWrite xmlWrite=new XmlWrite();
        xmlWrite.writeXml();
    }


}
