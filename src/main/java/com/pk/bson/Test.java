package com.pk.bson;


import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;

/**
 * Created by pkapovski on 8/4/2016.
 */


public class Test {

    public static void main(String[] args) throws IOException {
        Path path;
        path = Paths.get("D:\\data\\gd\\workspace\\depot\\MarketData\\mis\\mdmc_ssh\\src\\test\\bson\\test.bson");
        //path = Paths.get("E:\\gdrive\\projects\\git\\mdmc\\src\\test\\bson\\test.bson");


        byte[] buffer = Files.readAllBytes(path);

//        MutableString mStr;
//        mStr = new MutableString("number");
//        System.out.println(mStr +" "+mStr.hashCode());
//        mStr = new MutableString("object");
//        System.out.println(mStr +" "+mStr.hashCode());
//
//        System.exit(-1);

//        Map<MutableString, String> map = new HashMap<>();
//
//        map.put(new MutableString("KEY1"), "This Key 1");
//        map.put(new MutableString("KEY2"), "This Key 2");
//        map.put(new MutableString("KEY3"), "This Key 3");
//
//        System.out.println(map.get(new MutableString("KEY3")));
//
//        System.exit(-1);

        ByteBuffer bb = ByteBuffer.wrap(buffer, 0, buffer.length);
        BsonStream bbs = new BsonStream(bb);

        Document doc = new Document(null);
        doc.read(bbs);
        System.out.println("finished: " + doc.toString());
        System.out.println("get int [number]: " + doc.getInt32(new MutableString("number")));
        System.out.println("get string [string2]: "+doc.getString(new MutableString("string2")));

        System.out.printf("\n\n\n");
        bb.position(0);
        doc.read(bbs);
        System.out.println("finished: "+doc.toString());
    }


}
