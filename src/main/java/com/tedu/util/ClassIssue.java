package com.tedu.util;

import com.tedu.entry.MethodEntry;
import com.tedu.RPCEntry;
import com.tedu.anno.XiuEr;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassIssue {
    public static Object[] params;
    /**
     * 第一个String 类名 第二个方法名 最后一个list方法参数类型
     */
    static List<Map<String,List<Map<String,List>>>> list=new ArrayList<>();

    /**
     * 首先获取当前的类的物理路径
     * 通过用户给的包路径获取包下面要发布的接口（类）
     * @param packagePath
     */
    public static List doScan(String packagePath) throws ClassNotFoundException {
        //将用户给出的包名替换成/ 然后在物理路径扫描类
        String packageName=packagePath.replace(".","//");

        //得到要扫描包的路径
        String scanPath=ClassIssue.class.getResource("/")+packageName;
        System.out.println(ClassIssue.class.getResource("/")+packageName);
        //将scanPath 字符串中的file：/  去除
        scanPath=scanPath.substring(scanPath.indexOf("/"));
        //获取扫描路径的file对象集
        String[] files=FIleScanHandler.fileScan(scanPath);
        //todo 用来记录 类对应的方法信息
        Map<String,List<Map<String,List>>> classMap=new HashMap<>();
        for (String file : files) {
            //判断该类是否发布 ，如果不是方法中会退出
            classMap=isClassTo(file,packageName,classMap);
        }
        list.add(classMap);
        return list;
    }

    /**
     * 判断客户端调用的类是否允许发布
     * @param obj 客户端 发送的对象
     * @return
     */
    public static Class isPRCEntry(RPCEntry obj) throws ClassNotFoundException {
        String className=obj.getClassName();
        String methodName=obj.getMethodName();
        String packageName=obj.getPackageName();
        /////////
        List list=doScan(packageName);
        if(list==null){
            return null;
        }
        for (Object o : list) {
            Map map=null;
            map= (Map) o;
            List methodlist=null;
            methodlist= (List) map.get(className);
            if(methodlist==null){

                System.out.println("请求的类不存在");
                return null;
            }
            for (Object objMethod : methodlist) {
                map= (Map) objMethod;
                methodlist= (List) map.get(methodName);
                if(methodlist==null){
                    System.out.println("请求的方法不存在");
                    return null;
                }
            }

        }
        return Class.forName(packageName+"."+className);
    }

    /**
     * 返回数组类型的名称数组
     * @param Object 用户传来的方法参数
     * @return
     */
    public static String[] getMethodParamTypes(Object[] Object){
        String[] paramTypeNames=new String[Object.length];
        int index=0;
        for (Object o:Object) {
            //获取方法参数的类型并存入数组
            paramTypeNames[index]=o.getClass().getTypeName().substring(o.getClass().getTypeName().lastIndexOf(".")+1);
            index++;
        }
        return paramTypeNames;
    }
    /**
     * 该方法根据下标返回map的value
     */
    public static Object[] getMapIndexValue(Object[] object){
        Object[] params=new Object[object.length];
        int index=0;
        for (Object value : object) {
            params[index]=value;
            index++;
        }
        return params;
    }

    /**
     * 该方法返回方法参数类型的名字
     * @param parameterTypes
     * @return
     */
    public static String[] methodParaTypeNames( Class[] parameterTypes){
        String[] names=new String[parameterTypes.length];
        int index = 0;
        for (Class parameterType : parameterTypes) {
            names[index]=parameterType.getTypeName().substring(parameterType.getTypeName().lastIndexOf(".")+1);
            index++;
        }
        return names;
    }
    public static boolean isEqualMethodNames(String[] s1,String[] s2){
        //对用户请求的参数类型 与 service的参数类型进行对比
        for(int i=0;i<s1.length;i++){
            if (!(s1[i].equals(s2[i]))){
                return false;
            }
        }
        return true;
    }

    /**
     * 用来返回用户请求的方法对象
     * @param clazz
     * @return
     */
    public static Method isGetMethod(Class clazz, MethodEntry methodEntry){
        //请求类的方法S
        Method[] methods=clazz.getDeclaredMethods();
        for (Method method1 : methods) {
            if (method1.getName().equals(methodEntry.getMethodName())){
                //判断方法参数个数是否相等
                Class[] parameterTypes = method1.getParameterTypes();
                if (parameterTypes.length==methodEntry.getObjects().length){
                    //参数类型名称数组
                    String[] paraTypeNames=getMethodParamTypes(methodEntry.getObjects());
                    //方法中参数名称的数组
                    String[] methodParaTypeNames=methodParaTypeNames(parameterTypes);
                    //判断类型位置是否对应
                    if(!isEqualMethodNames(paraTypeNames,methodParaTypeNames)){
                        System.out.println("参数位置不对应");
                        return null;
                    }

                    //为params初始化
                    params=new Object[parameterTypes.length];
                    //如果走到此循环证明参数类型位置一致，
                    //将客户端请求的方法参数放入数组中
                    params=ClassIssue.getMapIndexValue(methodEntry.getObjects());
                    //将当前正确的方法赋值给成员方法
                    return method1;
                }
                System.out.println("方法参数不正确");
                return null;
            }
        }
        return null;
    }

    /**
     * 用作全局扫描

     * @param file
     * @param packagePath
     * @param classMap
     * @return
     * @throws ClassNotFoundException
     */
    public static Map isClassTo(
            String file,String packagePath,
            Map<String,List<Map<String,List>>> classMap) throws ClassNotFoundException {
        //截取 xxx.class 前的类名
        String className=file.substring(0,file.indexOf("."));
        packagePath=packagePath.replace("//",".");
        Class clazz=Class.forName(packagePath+"."+className);
        System.out.println(clazz.getPackage().getName()+"................");
        //该list包含多个方法信息
        List<Map<String,List>> listMethod=new ArrayList<>();
        //该map包含了 方法名称   参数列表
        Map<String,List> methodNameMap=new HashMap<>();
        //判断当前类是否要被发布
        if(clazz.isAnnotationPresent(XiuEr.class)){

            //获取当前类下面的所有方法
            Method[] methods=clazz.getDeclaredMethods();

            for (Method method : methods) {
                String methodName=method.getName();
                //获取该方法的所有参数类型
                Class[] paramTypes=method.getParameterTypes();
                //参数列表
                List paramList=new ArrayList();
                for (Class paramType : paramTypes) {
                    //获取参数类型的名字
                    String paramTypeName=paramType.getTypeName().
                            substring(paramType.getTypeName().lastIndexOf(".")+1);
                    paramList.add(paramTypeName);
                }
                methodNameMap.put(methodName,paramList);
            }
            listMethod.add(methodNameMap);
            classMap.put(className,listMethod);
        }
        return classMap;
    }

    /**
     * 根据给定的Class对象 返回方法的 名字。参数。返回值
     * @param clazz
     * @return
     */
    public static Map getMethodXin(Class clazz){
        //方法名 参数 返回值
        Map<String,Map<List,String>> map=new HashMap<>();
        //参数 返回值
        Map<List,String> m;
        Method[] methods=clazz.getDeclaredMethods();

        for (Method method1 : methods) {
            m=new HashMap<>();
            List typeNames=new ArrayList();
            Class<?>[] parameterTypes = method1.getParameterTypes();
            int index=0;
            //获取参数名字
            for (Class<?> parameterType : parameterTypes) {
                typeNames.add(parameterType.getTypeName().substring(parameterType.getTypeName().lastIndexOf(".")+1));
                index++;
            }

            //获取返回值类型
            String returnType= method1.getReturnType().getTypeName();

            //放进map
            m.put(typeNames,returnType.substring(returnType.lastIndexOf(".")+1));
            map.put(method1.getName(),m);
        }
      return map;
    }

}
