package com.pk.bson.elements;

import com.pk.bson.lang.ImmutableString;

/**
 * Created by pkapovski on 8/14/2016.
 */
public class Collection implements IObject, IArray {

    public enum TYPE {OBJECT, ARRAY}
    final private CollectionCache cache;
    private TYPE type;
    private Element head;
    private Element tail;

    private Collection(){
        cache = null;
    }

    public Collection(CollectionCache collectionCache, TYPE type){
        this.type = type;
        this.cache = collectionCache;
        head = null;
        tail = null;
    }

    public TYPE getType(){
        return type;
    }

    protected int key2id(ImmutableString key){
        return cache.getDictionary().key2Id(key).get();
    }

    Element add(Element.TYPE type, int key){
        Element e = cache.getElementCache().acquier(type, key);
        switch (type) {
            case EMBEDDED: {
                e.reference = cache.acquier(TYPE.OBJECT);
                break;
            }
            case ARRAY: {
                e.reference = cache.acquier(TYPE.ARRAY);
                break;
            }
        }

        if (tail ==null) {
            //first element, "head" also expected to be null
            head = e;
            tail = e;
        } else {
            tail.setNext(e);
            e.setPrevious(tail);
            tail = e;
        }
        return e;
    }

    Element getElement(ImmutableString key){
        return getElement(key2id(key));
    }

    public Element getElement(int key) {
        Element r = head;
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
                head = null;
                tail = null;
            } else {
                // last
                record.previous.next = null;
                tail = record.previous;
            }
        } else {
            // head
            if (record.next==null) { //todo can we get here???
                head = null;
                tail = null;
            } else {
                head = record.next;
            }
        }
        record.releaseReference();
        cache.getElementCache().release(record);
    }

    public Element addElement(Element.TYPE type, BsonStream stream){ // set package level
        ImmutableString locator = stream.readKey();
        int keyId = -1;
        if (getType()== Collection.TYPE.OBJECT){
            keyId = key2id(locator);
        }
        return add(type, keyId);
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        if (type==TYPE.OBJECT){
            sb.append("{");
        } else sb.append("[");


        boolean isFirst = true;
        Element e = head;
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
        Element e = tail;
        Element previous;
        while (e!=null) {
            previous = e.previous;
            e.releaseReference();
            cache.getElementCache().release(e);
            e = previous;
        }
        head = null;
        this.type = type;
    }

    public void recycle() {
        Element e = tail;
        Element previous;
        while (e!=null) {
            previous = e.previous;
            e.releaseReference();
            cache.getElementCache().release(e);
            e = previous;
        }
        head = null;
        tail = null;
        cache.release(this);
    }

    //----------------------------------------------------------------------------------------------------------------

    @Override
    public boolean remove(ImmutableString key){
        Element e = getElement(key);
        if (e!= null){
            remove(e);
            return true;
        } else return false;
    }

    @Override
    public boolean remove(int key){
        Element e = getElement(key);
        if (e!=null){
            remove(e);
            return true;
        } else return false;
    }

    @Override
    public void setDouble(ImmutableString key, double value) {
        setDouble(key2id(key), value);
    }

    @Override
    public void setDouble(int key, double value) {
        Element e = getElement(key);
        if (e==null) {
            e = add(Element.TYPE.DOUBLE, key);
        }
        e.setDouble(value);
    }

    @Override
    public double getDouble(int key) throws NoSuchFieldException {
        Element e = getElement(key);
        if (e!=null) {
            return e.getDouble();
        } else {
            return 0;//todo or NoSuchFieldException?
        }
    }

    @Override
    public double getDouble(ImmutableString key) throws NoSuchFieldException {
        return getDouble(key2id(key));
    }

    @Override
    public void setInt(ImmutableString key, int value) {
        setInt(key2id(key), value);
    }

    @Override
    public void setInt(int key, int value) {
        Element e = getElement(key);
        if (e==null) {
            e = add(Element.TYPE.INT32, key);
        }
        e.setInt(value);
    }

    @Override
    public int getInt(int key) throws NoSuchFieldException {
        Element e = getElement(key);
        if (e!=null) {
            return e.getInt();
        } else {
            return 0;//todo or NoSuchFieldException?
        }
    }

    @Override
    public int getInt(ImmutableString key) throws NoSuchFieldException {
        return getInt(key2id(key));
    }

    @Override
    public void setBoolean(ImmutableString key, boolean value) {
        setBoolean(key2id(key), value);
    }

    @Override
    public void setBoolean(int key, boolean value) {
        Element e = getElement(key);
        if (e==null) {
            e = add(Element.TYPE.BOOLEAN, key);
        }
        e.setBoolean(value);
    }

    @Override
    public boolean getBoolean(int key) throws NoSuchFieldException {
        Element e = getElement(key);
        return e != null && e.getBoolean(); //todo or NoSuchFieldException?
    }

    @Override
    public boolean getBoolean(ImmutableString key) throws NoSuchFieldException {
        return getBoolean(key2id(key));
    }

    @Override
    public IObject setObject(int key) {
        Element e = getElement(key);
        if (e==null) {
            e = add(Element.TYPE.EMBEDDED, key);
        }
        return  e.setObject(cache);
    }

    @Override
    public IObject setObject(ImmutableString key) {
        return setObject(key2id(key));
    }

    @Override
    public IObject getObject(int key) throws NoSuchFieldException {
        Element e = getElement(key);
        if (e!=null) {
            return e.getObject();
        } else {
            return null;
        }
    }

    @Override
    public IObject getObject(ImmutableString key) throws NoSuchFieldException {
        return getObject(key2id(key));
    }

    @Override
    public IArray setArray(int key) {
        Element e = getElement(key);
        if (e==null) {
            e = add(Element.TYPE.ARRAY, key);
        }
        return  e.setArray(cache);
    }

    @Override
    public IArray setArray(ImmutableString key) {
        return setArray(key2id(key));
    }

    @Override
    public IArray getArray(int key) throws NoSuchFieldException {
        Element e = getElement(key);
        if (e!=null) {
            return e.getArray();
        } else {
            return null;
        }
    }

    @Override
    public IArray getArray(ImmutableString key) throws NoSuchFieldException {
        return getArray(key2id(key));
    }

    @Override
    public void addInt(int value) {
        add(Element.TYPE.INT32, -1).setInt(value);
    }

    @Override
    public void addBoolean(boolean value) {
        add(Element.TYPE.BOOLEAN, -1).setBoolean(value);
    }

    @Override
    public void addDouble(double value) {
        add(Element.TYPE.DOUBLE, -1).setDouble(value);
    }

    @Override
    public IObject addObject() {
        return (IObject)add(Element.TYPE.EMBEDDED, -1).reference;
    }

    @Override
    public IArray addArray() {
        return (IArray)add(Element.TYPE.ARRAY, -1).reference;
    }

}
