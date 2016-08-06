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
        //path = Paths.get("D:\\data\\gd\\workspace\\depot\\MarketData\\mis\\mdmc_ssh\\src\\test\\bson\\test.bson");
        path = Paths.get("E:\\gdrive\\projects\\git\\mdmc\\src\\test\\bson\\test.bson");

        byte[] buffer = Files.readAllBytes(path);

        ByteBuffer bb = ByteBuffer.wrap(buffer, 0, buffer.length);
        BsonStream bbs = new BsonStream(bb);

        Document doc = new Document(null);
        doc.read(bbs);
        System.out.println(doc.toString());

        bb.position(0);
        doc.read(bbs);
        System.out.println(doc.toString());
    }


}
