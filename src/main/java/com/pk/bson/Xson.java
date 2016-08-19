package com.pk.bson;

import com.pk.bson.core.*;
import com.pk.bson.lang.StringDictionary;

import java.nio.ByteBuffer;

/**
 * Created by PavelK on 8/14/2016.
 */
public class Xson {
    private final StringDictionary dictionary = new StringDictionary();
    private final ElementCache elementCache;
    private final CollectionCache collectionCache;
    private final BsonStream bsonStream = new BsonStream();

    private Xson(){
        elementCache = null;
        collectionCache = null;
    }

    public Xson(int maxElements, int maxCollections){
        elementCache = new ElementCache(maxElements);
        collectionCache = new CollectionCache(maxCollections, elementCache, dictionary);
    }

    public IObject readBson(ByteBuffer byteBuffer){
        bsonStream.init(byteBuffer);
        Collection collection = collectionCache.acquier(Collection.TYPE.OBJECT);
        bsonStream.readCollectionFromBSON(collection);
        return collection;

    }
    public void DEBUG_ShowStats() {
        System.out.println("-- STATS ------------------------------------------");
        //System.out.println(dictionary);
        System.out.println("    Elements cache:\t\t" + elementCache.getAvailableCount());
        System.out.println("    Collection cache:\t"+collectionCache.getAvailableCount());
        System.out.println("---------------------------------------------------\n");
    }

}
