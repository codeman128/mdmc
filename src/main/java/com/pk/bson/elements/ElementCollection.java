package com.pk.bson.elements;

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
