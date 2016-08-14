package com.pk.bson.elements;

import com.pk.bson.lang.StringDictionary;
import com.pk.publisher.core.Message;

import java.util.Dictionary;

/**
 * Created by pkapovski on 8/14/2016.
 */
public class RecordLinkedList {
    public enum TYPE {OBJECT, ARRAY}

    final private StringDictionary dictionary;
    final private RecordCache cache;
    private TYPE type;
    private Record first;
    private Record last;

    private RecordLinkedList(){
        cache = null;
        dictionary = null;
    }

    public RecordLinkedList(TYPE type, RecordCache cache, StringDictionary dictionary){
        this.type = type;
        this.cache = cache;
        this.dictionary = dictionary;
        first = null;
        last = null;
    }

    public RecordCache getCache(){
        return cache;
    }

    public TYPE getType(){
        return type;
    }

    public Record add(Element.TYPE type, int key){
        Record record = cache.acquier(type, key);
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

    public Record get(int key) {
        Record r = first;
        while (r!=null) {
            if (r.key==key) {
                return r;
            } else r= r.getNext();
        }
        return null;
    }

    protected void remove(Record record) {
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
        Record r = first;
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
