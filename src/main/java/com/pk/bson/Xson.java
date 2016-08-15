package com.pk.bson;

import com.pk.bson.elements.CollectionCache;
import com.pk.bson.elements.ElementCache;
import com.pk.bson.elements.Collection;
import com.pk.bson.lang.ObjectCache;
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



    public Collection readBson(ByteBuffer byteBuffer){
        bsonStream.init(byteBuffer);
        Collection collection = new Collection(collectionCache, Collection.TYPE.OBJECT);
        collection.read(bsonStream);
        return collection;

    }

}
