package com.pk.bson.elements;

import com.pk.bson.BsonStream;
import com.pk.bson.lang.ImmutableInteger;
import com.pk.bson.lang.ImmutableString;
import com.pk.bson.lang.StringDictionary;

/**
 * Created by pkapovski on 8/14/2016.
 */
public class Collection {

    public enum TYPE {OBJECT, ARRAY}
    final private CollectionCache cache;
    private TYPE type;
    private Element first;
    private Element last;

    private Collection(){
        cache = null;
    }

    public Collection(CollectionCache collectionCache, TYPE type){
        this.type = type;
        this.cache = collectionCache;
        first = null;
        last = null;
    }

    public TYPE getType(){
        return type;
    }

     Element add(Element.TYPE type, int key){
        Element record = cache.getElementCache().acquier(type, key);
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

    public Element get(ImmutableString key){
        int keyId = cache.getDictionary().key2Id(key).get();
        return get(keyId);
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

    public boolean remove(ImmutableString key){
        Element e = get(key);
        if (e!= null){
            remove(e);
            return true;
        } else return false;
    }

    public boolean remove(int key){
        Element e = get(key);
        if (e!=null){
            remove(e);
            return true;
        } else return false;
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
        record.releaseReference();
        cache.getElementCache().release(record);
    }

    protected void readElement(Element.TYPE type, BsonStream stream){
        ImmutableString locator = stream.readKey();
        int keyId = -1;
        if (getType()== Collection.TYPE.OBJECT){
            ImmutableInteger key = cache.getDictionary().key2Id(locator);
            keyId = key.get();
        }
        Element record = add(type, keyId);
        try {
            record.read(stream, cache, cache.getDictionary(), cache.getElementCache());
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
        Element e = first;
        while (e!=null) {

            if (isFirst) {
                isFirst = false;
            } else {
                sb.append(",");
            }

            if (type==TYPE.OBJECT) sb.append("\"").append(cache.getDictionary().getKey(e.key)).append("\":");
            sb.append(e.toString());
            e = e.getNext();
        }
        if (type==TYPE.OBJECT){
            sb.append("}");
        } else sb.append("]");
        return sb.toString();
    }

    public void init(TYPE type){
        Element e = last;
        Element previous;
        while (e!=null) {
            previous = e.previous;
            e.releaseReference();
            cache.getElementCache().release(e);
            e = previous;
        }
        first = null;
        this.type = type;
    }

    public void release() {
        cache.release(this);
    }

    //-----------------------------------------------------------------------------------------------------------

}
