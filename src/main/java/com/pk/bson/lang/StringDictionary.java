package com.pk.bson.lang;


import com.pk.bson.lang.ImmutableInteger;
import com.pk.bson.lang.ImmutableString;
import com.pk.bson.lang.MutableInteger;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by PavelK on 8/12/2016.
 */
public class StringDictionary {
    private final Map<ImmutableString, ImmutableInteger> key2id = new HashMap<>();
    private final Map<ImmutableInteger, ImmutableString> id2key = new HashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);
    /* this is not thread safe !! */
    private final MutableInteger locator = new MutableInteger(0);

    public int key2Id(String key){
        return key2Id(new ImmutableString(key)).get();
    }

    public ImmutableInteger key2Id(ImmutableString key) {
        ImmutableInteger id = isExists(key);
        if (id==null){
            id = new ImmutableInteger(counter.incrementAndGet());
            MutableString newKey = new MutableString(key);
            key2id.put(newKey, id);
            id2key.put(id, newKey);
        }
        return id;
    }

    public ImmutableInteger isExists(ImmutableString key) {
        System.out.println("isExists: "+key+" "+key2id.get(key)+" ["+key.hash+"]");
        return key2id.get(key);
    }

    public ImmutableString getKey(int id) {
        locator.set(id);
        return id2key.get(locator);
    }

    public int getCount(){
        return counter.get();
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(counter.get()).append(" entries:\n");
        if (getCount()>0) {
            for (int i=1; i<=counter.get(); i++) {
                sb.append(i).append(" : [").append(getKey(i)).append("] ["+getKey(i).hash+"]\n");
            }
        }

//        Collection c = key2id.keySet();
//        Iterator itr = c.iterator();
//        while (itr.hasNext()) {
//            Object o = itr.next();
//            System.out.println(o+" - ["+o.hashCode()+"]");
//        }

        return sb.toString();
    }

}
