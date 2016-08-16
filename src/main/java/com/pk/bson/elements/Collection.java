package com.pk.bson.elements;

import com.pk.bson.BsonStream;
import com.pk.bson.lang.ImmutableInteger;
import com.pk.bson.lang.ImmutableString;

/**
 * Created by pkapovski on 8/14/2016.
 */
public class Collection implements IObject, IArray {

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

    @Override
    public boolean remove(ImmutableString key){
        Element e = get(key);
        if (e!= null){
            remove(e);
            return true;
        } else return false;
    }

    @Override
    public boolean remove(int key){
        Element e = get(key);
        if (e!=null){
            remove(e);
            return true;
        } else return false;
    }

    @Override
    public void setDouble(ImmutableString key, double value) {
        int keyId = cache.getDictionary().key2Id(key).get();
        setDouble(keyId, value);
    }

    @Override
    public void setDouble(int key, double value) {
        Element e = get(key);
        if (e==null) {
            e = add(Element.TYPE.DOUBLE, key);
        }
        e.setDouble(value);
    }

    @Override
    public double getDouble(int key) throws NoSuchFieldException {
        Element e = get(key);
        if (e!=null) {
            return e.getDouble();
        } else {
            return 0;//todo or NoSuchFieldException?
        }
    }

    @Override
    public double getDouble(ImmutableString key) throws NoSuchFieldException {
        int keyId = cache.getDictionary().key2Id(key).get();
        return getDouble(keyId);
    }

    @Override
    public void setInt(ImmutableString key, int value) {
        int keyId = cache.getDictionary().key2Id(key).get();
        setInt(keyId, value);
    }

    @Override
    public void setInt(int key, int value) {
        Element e = get(key);
        if (e==null) {
            e = add(Element.TYPE.INT32, key);
        }
        e.setInt(value);
    }

    @Override
    public int getInt(int key) throws NoSuchFieldException {
        Element e = get(key);
        if (e!=null) {
            return e.getInt();
        } else {
            return 0;//todo or NoSuchFieldException?
        }
    }

    @Override
    public int getInt(ImmutableString key) throws NoSuchFieldException {
        int keyId = cache.getDictionary().key2Id(key).get();
        return getInt(keyId);
    }

    @Override
    public void setBoolean(ImmutableString key, boolean value) {
        int keyId = cache.getDictionary().key2Id(key).get();
        setBoolean(keyId, value);
    }

    @Override
    public void setBoolean(int key, boolean value) {
        Element e = get(key);
        if (e==null) {
            e = add(Element.TYPE.BOOLEAN, key);
        }
        e.setBoolean(value);
    }

    @Override
    public boolean getBoolean(int key) throws NoSuchFieldException {
        Element e = get(key);
        if (e!=null) {
            return e.getBoolean();
        } else {
            return false;//todo or NoSuchFieldException?
        }
    }

    @Override
    public boolean getBoolean(ImmutableString key) throws NoSuchFieldException {
        int keyId = cache.getDictionary().key2Id(key).get();
        return getBoolean(keyId);
    }

    @Override
    public IObject getObject(int key) throws NoSuchFieldException {
        Element e = get(key);
        if (e!=null) {
            return e.getObject();
        } else {
            return null;
        }
    }

    @Override
    public IObject getObject(ImmutableString key) throws NoSuchFieldException {
        int keyId = cache.getDictionary().key2Id(key).get();
        return getObject(keyId);
    }

    @Override
    public IArray getArray(int key) throws NoSuchFieldException {
        Element e = get(key);
        if (e!=null) {
            return e.getArray();
        } else {
            return null;
        }
    }

    @Override
    public IArray getArray(ImmutableString key) throws NoSuchFieldException {
        int keyId = cache.getDictionary().key2Id(key).get();
        return getArray(keyId);
    }

}
