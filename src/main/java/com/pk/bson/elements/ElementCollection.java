package com.pk.bson.elements;

import com.pk.bson.BsonStream;
import com.pk.bson.lang.ImmutableInteger;
import com.pk.bson.lang.ImmutableString;
import com.pk.bson.lang.StringDictionary;

/**
 * Created by pkapovski on 8/14/2016.
 */
public class ElementCollection {
    public enum TYPE {OBJECT, ARRAY}

    final private StringDictionary dictionary;
    final private ElementCache cache;
    private TYPE type;
    private Element first;
    private Element last;

    private ElementCollection(){
        cache = null;
        dictionary = null;
    }

    public ElementCollection(TYPE type, ElementCache cache, StringDictionary dictionary){
        this.type = type;
        this.cache = cache;
        this.dictionary = dictionary;
        first = null;
        last = null;
    }

    public ElementCache getCache(){
        return cache;
    }

    public TYPE getType(){
        return type;
    }

    public Element add(Element.TYPE type, int key){
        Element record = cache.acquier(type, key);
        if (last==null) {
            //first element, "first" also expected to be null
            first = record;
            last = record;
        } else {
            last.setNext(record);
            record.setPrevious(last);
            last = record;
        }
        return record;
    }

    public Element get(int key) {
        Element r = first;
        while (r!=null) {
            if (r.key==key) {
                return r;
            } else r= r.getNext();
        }
        return null;
    }

    protected void remove(Element record) {
        // if in the middle
        if (record.next!=null && record.previous!=null) {
           record.previous.next=record.next;
        } else
        // if last
        if (record.next == null) {
            record.previous.next = null;
        } else {
            // first
            first = null;
        }
        cache.release(record);
    }

    protected void readElement(Element.TYPE type, BsonStream stream){
        ImmutableString locator = stream.readKey();
        int keyId = -1;
        if (getType()== ElementCollection.TYPE.OBJECT){
            ImmutableInteger key = dictionary.key2Id(locator);
            keyId = key.get();
        }
        Element record = add(type, keyId);
        try {
            record.read(stream, dictionary, cache);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public void read(BsonStream stream) {
        int size = stream.getInt32();
        Element.TYPE type;
        while(true) {
            type = stream.readNextType();
            switch (type) {
                case EOO: return;
                case DOUBLE:
                case STRING:
                case INT32:
                case EMBEDDED:
                case ARRAY:
                case BOOLEAN:{
                    readElement(type, stream);
                    break;
                }
            }
        }
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        if (type==TYPE.OBJECT){
            sb.append("{");
        } else sb.append("[");


        boolean isFirst = true;
        Element r = first;
        while (r!=null) {

            if (isFirst) {
                isFirst = false;
            } else {
                sb.append(",");
            }

            if (type==TYPE.OBJECT) sb.append("\"").append(dictionary.getKey(r.key)).append("\":");
            sb.append(r.toString());
            r= r.getNext();
        }
        if (type==TYPE.OBJECT){
            sb.append("}");
        } else sb.append("]");
        return sb.toString();

    }

}
