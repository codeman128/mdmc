package com.pk.bson;


import com.pk.bson.core.IArray;
import com.pk.bson.core.IObject;
import com.pk.bson.lang.ImmutableInteger;
import com.pk.bson.lang.ImmutableString;
import com.pk.bson.lang.MutableString;

import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;

/**
 * Created by pkapovski on 8/4/2016.
 */


public class Test {

    public static void main(String[] args) throws Exception {

        byte[] intBuffer = new byte[100];
        int size;

        size = ImmutableInteger.intToString(intBuffer, 0, Integer.MAX_VALUE);
        System.out.println(new String(intBuffer, 0, size));

        size = ImmutableInteger.intToString(intBuffer, 0, -1237779);
        System.out.println(new String(intBuffer, 0, size));

        MutableString ms = new MutableString(100);
        ms.append(7777);
        ms.append(123);
        ms.append(-8765);
        System.out.println("["+ms+"]");

        ImmutableString KEY_double = new ImmutableString("double");
        ImmutableString KEY_double8 = new ImmutableString("double8");
        ImmutableString KEY_int32_8 = new ImmutableString("int32_8");
        ImmutableString KEY_array = new ImmutableString("array");

        ImmutableString KEY_boolean_1 = new ImmutableString("boolean_1");
        ImmutableString KEY_boolean_2 = new ImmutableString("boolean_2");

        ImmutableString KEY_dealCount = new ImmutableString("dealCount");
        ImmutableString KEY_new_obj = new ImmutableString("new_obj");

        //TestDictionary();

        String s;
        s = "D:\\data\\gd\\workspace\\depot\\MarketData\\mis\\mdmc_ssh\\src\\test\\bson\\";
        s = "E:\\gdrive\\projects\\git\\mdmc\\src\\test\\bson\\";


        Path bsonPath = Paths.get(s+"test5.bson");
        byte[] bsonBuffer = Files.readAllBytes(bsonPath);
        ByteBuffer bsonBB = ByteBuffer.wrap(bsonBuffer, 0, bsonBuffer.length);

        Path jsonPath = Paths.get(s+"test4.json");
        byte[] jsonBuffer = Files.readAllBytes(jsonPath);
        ByteBuffer jsonBB = ByteBuffer.wrap(jsonBuffer, 0, jsonBuffer.length);
        System.out.println(new String(jsonBuffer));


        Xson xson = new Xson(1024, 128);

        xson.DEBUG_ShowStats();

        IObject json = xson.readJson(jsonBB);
        System.out.println(json);

        IArray array = json.getArray(KEY_array);
        System.out.println(array.getSize());
        System.out.println(array.getElementAt(2));

        System.exit(-1);

        IObject doc = xson.readBson(bsonBB);

        System.out.println(doc.toString());
        xson.DEBUG_ShowStats();

        //doc.recycle();
        //xson.DEBUG_ShowStats();
        //System.exit(1);

        doc.setDouble(KEY_double8, 1.5556);
        doc.setInt(KEY_int32_8, 15557);
        System.out.println(doc.toString());
        xson.DEBUG_ShowStats();

        //doc.remove(KEY_double8);
        doc.remove(KEY_int32_8);
        doc.remove(KEY_double8);
        System.out.println(doc.toString());
        xson.DEBUG_ShowStats();

        doc.remove(KEY_boolean_2);
        doc.remove(KEY_double);
        doc.remove(KEY_boolean_1);
        System.out.println(doc.toString());
        xson.DEBUG_ShowStats();


        doc.remove(KEY_array);
        System.out.println(doc.toString());
        xson.DEBUG_ShowStats();

        doc.remove(1);
        System.out.println(doc.toString());
        doc.recycle();
        xson.DEBUG_ShowStats();


        bsonBB.position(0);
        doc = xson.readBson(bsonBB);
        System.out.println(doc.toString());
        xson.DEBUG_ShowStats();

        System.out.println(doc.getObject(KEY_dealCount));
        System.out.println(doc.getArray(KEY_array));

        System.out.println("-------------");
        IObject o = doc.setObject(KEY_new_obj);
        o.setBoolean(KEY_boolean_1, true);
        o.setBoolean(KEY_boolean_2, false);
        System.out.println(o);
        System.out.println(doc.toString());
        xson.DEBUG_ShowStats();

        doc.remove(KEY_new_obj);
        System.out.println(doc.toString());
        xson.DEBUG_ShowStats();

        IArray a = doc.setArray(KEY_new_obj);
        a.addInt(777);
        a.addBoolean(false);
        a.addDouble(123.456);
        a.addObject().setBoolean(KEY_boolean_1, true);
        a.addArray().addBoolean(true);
        System.out.println(doc.toString());
        xson.DEBUG_ShowStats();

        doc.remove(KEY_new_obj);
        System.out.println(doc.toString());
        xson.DEBUG_ShowStats();

        doc.recycle();
        xson.DEBUG_ShowStats();
    }


}
