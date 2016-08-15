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

    Element get(ImmutableString key){
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

    protected void remove(Element record) {
        // if in the middle
        if (record.next!=null && record.previous!=null) {
           record.previous.next=record.next; // fix forward link
           record.next.previous = record.previous; // fix backwards link
        } else
        // if last
        if (record.next == null) {
            if (record.previous==null){
                // single record
                first = null;
                last = null;
            } else {
                // last
                record.previous.next = null;
                last = record.previous;
            }
        } else {
            // first
            if (record.next==null) { //todo can we get here???
                first = null;
                last = null;
            } else {
                first = record.next;
            }
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
        Element e = last;
        Element previous;
        while (e!=null) {
            previous = e.previous;
            e.releaseReference();
            cache.getElementCache().release(e);
            e = previous;
        }
        first = null;
        last = null;
        cache.release(this);
    }

    //----------------------------------------------------------------------------------------------------------------
    /**
     * Remove element by key
     **/
    public boolean remove(ImmutableString key){
        Element e = get(key);
        if (e!= null){
            remove(e);
            return true;
        } else return false;
    }

    /**
     * Remove element by key id
     **/
    public boolean remove(int key){
        Element e = get(key);
        if (e!=null){
            remove(e);
            return true;
        } else return false;
    }

    /**
     * Updates (or creates if didn't exists) element with specified key with provided double value
     * @param key key of the element
     * @param value new value
     **/
    public void setDouble(ImmutableString key, double value) {
        int keyId = cache.getDictionary().key2Id(key).get();
        setDouble(keyId, value);
    }
    /**
     * Updates (or creates if didn't exists) element with specified key id with provided double value
     * @param key key id of the element
     * @param value new value
     **/
    public void setDouble(int key, double value) {
        Element e = get(key);
        if (e==null) {
            e = add(Element.TYPE.DOUBLE, key);
        }
        e.setDouble(value);
    }

    /**
     * Retrieves double value of specified element by key id.
     * @param key key of the element
     * @return double value.  If element doesn't exists returns 0.
     **/
    public double getDouble(int key) throws NoSuchFieldException {
        Element e = get(key);
        if (e!=null) {
            return e.getDouble();
        } else {
            return 0;//todo or NoSuchFieldException?
        }
    }

    /**
     * Updates (or creates if didn't exists) element with specified key with provided int value
     * @param key key of the element
     * @param value new value
     **/
    public void setInt(ImmutableString key, int value) {
        int keyId = cache.getDictionary().key2Id(key).get();
        setInt(keyId, value);
    }
    /**
     * Updates (or creates if didn't exists) element with specified key id with provided int value
     * @param key key id of the element
     * @param value new value
     **/
    public void setInt(int key, int value) {
        Element e = get(key);
        if (e==null) {
            e = add(Element.TYPE.INT32, key);
        }
        e.setInt(value);
    }

    /**
     * Retrieves int value of specified element by key id.
     * @param key key of the element
     * @return int value.  If element doesn't exists returns 0.
     **/
    public int getInt(int key) throws NoSuchFieldException {
        Element e = get(key);
        if (e!=null) {
            return e.getInt();
        } else {
            return 0;//todo or NoSuchFieldException?
        }
    }

}
