package com.pk.bson;


import com.pk.bson.elements.Collection;
import com.pk.bson.lang.ImmutableString;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;

/**
 * Created by pkapovski on 8/4/2016.
 */


public class Test {

    public static void main(String[] args) throws IOException, NoSuchFieldException {
        ImmutableString KEY_double = new ImmutableString("double");
        ImmutableString KEY_double8 = new ImmutableString("double8");
        ImmutableString KEY_int32_8 = new ImmutableString("int32_8");

        //TestDictionary();

        Path path;
        path = Paths.get("D:\\data\\gd\\workspace\\depot\\MarketData\\mis\\mdmc_ssh\\src\\test\\bson\\test5.bson");
        //path = Paths.get("E:\\gdrive\\projects\\git\\mdmc\\src\\test\\bson\\test5.bson");
        byte[] buffer = Files.readAllBytes(path);

        ByteBuffer bb = ByteBuffer.wrap(buffer, 0, buffer.length);
        Xson xson = new Xson(1024, 128);
        Collection doc = xson.readBson(bb);

        System.out.println("finished: " + doc.toString());

        doc.setDouble(KEY_double8, 1.5556);
        doc.setInt(KEY_int32_8, 15557);
        System.out.println("finished: " + doc.toString());

        //doc.remove(KEY_double8);
        doc.remove(KEY_int32_8);
        doc.remove(KEY_double8);


        System.out.println("finished: " + doc.toString());


//        Element e;

//        e =  doc.get(KEY_double);
//        System.out.println("read \""+KEY_double+"\" " + doc.get(KEY_double).getDouble());
//        e.setInt(123);
//        System.out.println("finished: " + doc.toString());
//        doc.remove(KEY_double);
//        System.out.println("finished: " + doc.toString());

    }


}
