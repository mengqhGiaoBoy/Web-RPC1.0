package com.tedu.issue;

import com.tedu.anno.XiuEr;

import javax.xml.crypto.Data;
import java.util.Date;

@XiuEr
public class OrderService {
    public Date modelOne(String name){
        System.out.println(name);
        return new Date();
    }
    public Date modelTwo(Integer age, String two){
        System.out.println(age+",,,"+two+"...");
        return new Date();
    }
}
