package com.pk.bson.core;

import com.pk.lang.ObjectCache;
import com.pk.lang.StringDictionary;

/**
 * Created by pkapovski on 8/15/2016.
 */
public class CollectionCache {
    private final ObjectCache<Collection> cache;
    private final ElementCache elementCache;
    private final StringDictionary dictionary;

    private CollectionCache(){
        cache = null;
        elementCache = null;
        dictionary = null;
    }

    public CollectionCache(int size, ElementCache elementCache, StringDictionary dictionary) {
        cache = new ObjectCache<>(size);
        this.elementCache = elementCache;
        this.dictionary = dictionary;
        for (int i=0; i<size; i++){
            cache.add(new Collection(this, Collection.TYPE.OBJECT));
        }
    }

    public Collection acquier(Collection.TYPE type){
        Collection c = cache.acquire();
        if (c==null) return null;
        c.init(type);
        return c;
    }

    public ElementCache getElementCache() {
        return elementCache;
    }

    public StringDictionary getDictionary() {
        return dictionary;
    }

    public void release(Collection collection){
        cache.release(collection);
    }

    public int getAvailableCount() {
        return cache.getAvailableCount();
    }
}
