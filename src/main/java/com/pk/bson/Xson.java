package com.pk.bson;

import com.pk.bson.elements.ContainerElement;
import com.pk.bson.elements.RecordCache;
import com.pk.bson.elements.RecordLinkedList;
import com.pk.bson.lang.StringDictionary;

import java.nio.ByteBuffer;

/**
 * Created by PavelK on 8/14/2016.
 */
public class Xson {
    private final StringDictionary dictionary = new StringDictionary();
    private final RecordCache recordCache;
    private final BsonStream bsonStream = new BsonStream();

    private Xson(){
        recordCache = null;
    }

    public Xson(int maxElements){
        recordCache = new RecordCache(maxElements);
    }

    public ContainerElement readBson(ByteBuffer byteBuffer){
        bsonStream.init(byteBuffer);
        ContainerElement doc = new ContainerElement(RecordLinkedList.TYPE.OBJECT, dictionary, recordCache);
        doc.read(bsonStream);
        return doc;

    }

}
