package com.tedu.util;

import com.tedu.anno.XiuEr;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FIleScanHandler {
    /**
     * 用过用户给的扫描路径，扫描路径下的所有文件
     * @param scanPath 扫描路径
     * @return
     */
    public static String[] fileScan(String scanPath){
        //获取扫描路径的file对象
        File filePath=new File(scanPath);
        //通过file对象下面的所有class文件
        String [] files =filePath.list();
        //该Map 包含类名称 和方法信息
        return files;
    }

    /**
     *
     * @param files 用户给定路径下的所有集合
     * @return
     */
    //所有有发布类的包名集合
   public  static List<String> packageNames=new ArrayList<>();

    public static  Map<String,String> filePackages(String scanPath,String[] files ) throws ClassNotFoundException {
        Map<String,String> classNames=getClassNames(scanPath,files);
        //此时的classNames为发布的类和包
        classNames=isClassIssue(classNames);
        return classNames;
    }

    /**
     *该方法返回通过物理路径
     *返回 所有的类与对应的包的map
     * @param scanPath  物理资源路径
     * @param files 物理资源路径下的文件数组
     * @return
     */
    //用来储存类名和对应的包的集合
    static Map<String,String> classNames=new HashMap<>();
    public static Map<String,String> getClassNames(String scanPath,String[] files){
        File file= null;
        for (String f : files) {
            //将路径与文件名进行拼接 为file文件
            file= new File(scanPath+"/"+f);
            String[] filenext=file.list();
            if (f.endsWith(".class")){
                //由物理资源路径截取 获取物理包路径 将 / 转为 .
               String packagePath= scanPath.substring(scanPath.indexOf("classes")+9).replace("/",".");
                classNames.put(f.substring(0,f.indexOf(".")),packagePath);
            }else if(filenext!=null){
                getClassNames(scanPath+"/"+f,filenext);
            }

        }
        return classNames;
    }
    public static Map<String,String> isClassIssue(Map<String,String> map) throws ClassNotFoundException {
        Map<String,String> issue=new HashMap<>();
        Class clazz=null;
       for (Map.Entry<String, String> entry : map.entrySet()){
           String className=entry.getKey();
           String packageName=entry.getValue();
           clazz=Class.forName(packageName+"."+className);
           if (clazz.isAnnotationPresent(XiuEr.class)){
               issue.put(className,packageName);
           }
       }
        return issue;
    }

}
