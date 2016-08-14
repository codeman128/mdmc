package com.pk.bson.elements;

import com.pk.bson.lang.ObjectCache;

/**
 * Created by pkapovski on 8/14/2016.
 */
public class RecordCache {
    protected final ObjectCache<Record> cache;

    private RecordCache(){
        cache = null;
    }

    public RecordCache(int size) {
        cache = new ObjectCache<Record>(size);
        for (int i=0; i<size; i++){
            cache.add(new Record());
        }
    }

    public Record acquier(Record.TYPE type, int key){
        Record record = cache.acquire();
        if (record==null) return null;
        record.init(type, key);
        return record;
    }

    public void release(Record record){
        cache.release(record);
    }

}
