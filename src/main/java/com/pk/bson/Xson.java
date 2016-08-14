package com.pk.bson;

import com.pk.bson.elements.ElementCache;
import com.pk.bson.elements.ElementCollection;
import com.pk.bson.lang.StringDictionary;

import java.nio.ByteBuffer;

/**
 * Created by PavelK on 8/14/2016.
 */
public class Xson {
    private final StringDictionary dictionary = new StringDictionary();
    private final ElementCache recordCache;
    private final BsonStream bsonStream = new BsonStream();

    private Xson(){
        recordCache = null;
    }

    public Xson(int maxElements){
        recordCache = new ElementCache(maxElements);
    }



    public ElementCollection readBson(ByteBuffer byteBuffer){
        bsonStream.init(byteBuffer);
        ElementCollection doc = new ElementCollection(ElementCollection.TYPE.OBJECT, recordCache, dictionary);
        doc.read(bsonStream);
        return doc;

    }

}
