package com.spartajet.shardingboot;

import java.io.*;

public class Test {

    static final int a=1;
    static final StringBuffer sbr=new StringBuffer("aaa");


    public static void main(String[] args) throws IOException, ClassNotFoundException {
        //testSerializabile();

        //sbr.append("ccc");
    }

    private static void testSerializabile() {
    /* SerializabileBean serializabileTest=new SerializabileBean();
     serializabileTest.setId(1);
     serializabileTest.setName("张张");
     serializabileTest.setSex("女");

     //objectSerializabile(serializabileTest);
     onObjectSerializabile();*/
    }

    /**
     * 对象反序列化
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private static void onObjectSerializabile() throws IOException, ClassNotFoundException {
        ObjectInputStream objectInputStream=new ObjectInputStream(new FileInputStream(new File("F:\\SerializabileBean.txt")));
        SerializabileBean serializabileBean=(SerializabileBean)objectInputStream.readObject();
        System.out.println("对象被反序列化成功！"+serializabileBean.toString());
        objectInputStream.close();
    }

    /**
     * 对象序列化
     * @param serializabileTest
     * @throws IOException
     */
    private static void objectSerializabile(SerializabileBean serializabileTest) throws IOException {
        ObjectOutputStream objectOutputStream=new ObjectOutputStream(new FileOutputStream(new File("F:\\SerializabileBean.txt")));
        objectOutputStream.writeObject(serializabileTest);
        System.out.println("对象被序列化成功！");
        objectOutputStream.close();
    }
}
