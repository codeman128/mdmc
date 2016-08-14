package com.pk.bson.elements;

/**
 * Created by pkapovski on 8/14/2016.
 */
public class RecordLinkedList {

    final private RecordCache cache;
    private Record first;
    private Record last;

    private RecordLinkedList(){
        cache = null;
    }

    public RecordLinkedList(RecordCache cache){
        this.cache = cache;
        first = null;
        last = null;
    }

    public RecordCache getCache(){
        return cache;
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

}
