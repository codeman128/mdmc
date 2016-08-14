package com.pk.bson.elements;

import com.pk.bson.lang.ObjectCache;

/**
 * Created by pkapovski on 8/14/2016.
 */
public class ElementCache {
    protected final ObjectCache<Element> cache;

    private ElementCache(){
        cache = null;
    }

    public ElementCache(int size) {
        cache = new ObjectCache<Element>(size);
        for (int i=0; i<size; i++){
            cache.add(new Element());
        }
    }

    public Element acquier(Element.TYPE type, int key){
        Element record = cache.acquire();
        if (record==null) return null;
        record.init(type, key);
        return record;
    }

    public void release(Element record){
        cache.release(record);
    }

}