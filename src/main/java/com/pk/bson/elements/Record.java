package com.pk.bson.elements;

import com.pk.bson.BsonStream;
import com.pk.bson.Document;
import com.pk.bson.lang.MutableString;
import com.pk.bson.lang.StringDictionary;

/**
 * Created by pkapovski on 8/14/2016.
 */
public class Record {
    protected int key;   // reference to the dictionary
    protected Element.TYPE type;
    protected long data;
    Object reference;  // to string or container
    protected Record previous;
    protected Record next;


    Record() {
        type = Element.TYPE.UNDEFINED;
    }

     final void init(Element.TYPE type, int key) {
        this.type = type;
        this.key = key;
        this.next = null;
    }

    public final int getKey(){
        return key;
    }

    public final Element.TYPE getType(){
        return type;
    }

    public Record getPrevious() {
        return previous;
    }

    public void setPrevious(Record previous) {
        this.previous = previous;
    }

    public Record getNext() {
        return next;
    }

    public void setNext(Record next) {
        this.next = next;
    }

    public void read(BsonStream stream, StringDictionary dictionary, RecordCache cache) throws NoSuchFieldException {
        switch (type) {
            case INT32: {
                setInt(stream.getInt32());
                return;
            }
            case BOOLEAN: {
                setBoolean(stream.readBoolean());
                return;
            }
            case DOUBLE: {
                setDouble(stream.getDouble());
                return;
            }
            case STRING: {
                MutableString str = new MutableString(200);
                stream.readString(str);
                reference = str;
                return;
            }
            case EMBEDDED : {
                Document doc = new Document(null/*key*/, dictionary, cache);//todo fix
                doc.read(stream);
                reference = doc;
                return;
            }
            case ARRAY: {
                ArrayElement array = new ArrayElement(null /*key*/, dictionary, cache); //todo fix
                array.read(stream);
                reference = array;
                return;
            }
        }
    }


    public void setDouble(double value) throws NoSuchFieldException {
        if (type!= Element.TYPE.DOUBLE) throw new NoSuchFieldException();
        data = Double.doubleToRawLongBits(value);
    }

    public double getDouble()throws NoSuchFieldException {
        if (type!= Element.TYPE.DOUBLE) throw new NoSuchFieldException();
        return Double.longBitsToDouble(data);
    }

    public void setBoolean(boolean value) throws NoSuchFieldException {
        if (type!= Element.TYPE.BOOLEAN) throw new NoSuchFieldException();
        data = (value) ? 1 : 0;
    }

    public boolean getBoolean() throws NoSuchFieldException {
        if (type != Element.TYPE.BOOLEAN) throw new NoSuchFieldException();
        return (data == 1);
    }

    public void setInt(int value) throws NoSuchFieldException {
        if (type!= Element.TYPE.INT32) throw new NoSuchFieldException();
        data = (long)value;
    }

    public int getInt() throws NoSuchFieldException {
        if (type != Element.TYPE.INT32) throw new NoSuchFieldException();
        return (int)data;
    }


    public void setObject(Document value)throws NoSuchFieldException {
        if (type != Element.TYPE.EMBEDDED) throw new NoSuchFieldException();
        reference = value;
    }

    public Document getObject()throws NoSuchFieldException {
        if (type != Element.TYPE.EMBEDDED) throw new NoSuchFieldException();
        return  (Document)reference;
    }

    public void setArray(ArrayElement value)throws NoSuchFieldException {
        if (type != Element.TYPE.ARRAY) throw new NoSuchFieldException();
        reference = value;
    }

    public ArrayElement getArray()throws NoSuchFieldException {
        if (type != Element.TYPE.ARRAY) throw new NoSuchFieldException();
        return  (ArrayElement)reference;
    }
}
