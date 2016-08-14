package com.pk.bson;


import com.pk.bson.elements.ContainerElement;

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
        //TestDictionary();

        Path path;
        path = Paths.get("D:\\data\\gd\\workspace\\depot\\MarketData\\mis\\mdmc_ssh\\src\\test\\bson\\test5.bson");
        path = Paths.get("E:\\gdrive\\projects\\git\\mdmc\\src\\test\\bson\\test5.bson");
        byte[] buffer = Files.readAllBytes(path);


        ByteBuffer bb = ByteBuffer.wrap(buffer, 0, buffer.length);
        Xson xson = new Xson(1024);
        ContainerElement doc = xson.readBson(bb);

        System.out.println("finished: " + doc.toString());


    }


}
