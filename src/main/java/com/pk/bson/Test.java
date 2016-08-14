package com.pk.bson;


import com.pk.bson.elements.RecordCache;
import com.pk.bson.lang.MutableString;
import com.pk.bson.lang.StringDictionary;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;

/**
 * Created by pkapovski on 8/4/2016.
 */


public class Test {

    public static void TestDictionary(){
        System.out.println("Test Dictionary:");
        StringDictionary dictionary = new StringDictionary();
        dictionary.key2Id("one");
        dictionary.key2Id("two");
        dictionary.key2Id("three");
        dictionary.key2Id("one");
        dictionary.key2Id("two");
        dictionary.key2Id("test1");
        dictionary.key2Id("test2");

        dictionary.key2Id(new MutableString("one"));
        dictionary.key2Id(new MutableString("two"));

        System.out.println(dictionary + "\n");
    }

    public static void main(String[] args) throws IOException {
        TestDictionary();


        StringDictionary dictionary = new StringDictionary();
        RecordCache cache = new RecordCache(1000);

        Path path;
        path = Paths.get("D:\\data\\gd\\workspace\\depot\\MarketData\\mis\\mdmc_ssh\\src\\test\\bson\\test5.bson");
        path = Paths.get("E:\\gdrive\\projects\\git\\mdmc\\src\\test\\bson\\test5.bson");
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

        Document doc = new Document(null, dictionary, cache);
        doc.read(bbs);
        System.out.println("finished: " + doc.toString());
        System.out.println("get int [number]: " + doc.getInt32(new MutableString("number")));
        System.out.println("get string [string2]: " + doc.getString(new MutableString("string2")));
        doc.setInt32(new MutableString("number-3"), 557);
        doc.setString(new MutableString("name"), new MutableString("test324"));

        System.out.println(dictionary);
        //System.out.printf("\n\n\n");
        //bb.position(0);
        //doc.read(bbs);
        //System.out.println("finished: "+doc.toString());
    }


}
