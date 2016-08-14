package com.pk.bson.elements;

import com.pk.bson.BsonStream;
import com.pk.bson.lang.ImmutableInteger;
import com.pk.bson.lang.MutableString;
import com.pk.bson.lang.StringDictionary;

/**
 * Created by PavelK on 8/10/2016.
 */
public abstract class ContainerElement extends Element {
    protected final MutableString locator = new MutableString(200); //200 should be good enough
    protected final RecordLinkedList records;
    protected final StringDictionary dictionary;

    private ContainerElement(){
        super(null);
        records = null;
        dictionary = null;
    }

    protected abstract RecordLinkedList.TYPE getContainerType();

    protected ContainerElement(MutableString name, StringDictionary dictionary, RecordCache cache) {
        super(name);
        records = new RecordLinkedList(getContainerType(), cache, dictionary);
        this.dictionary = dictionary;
    }


    protected void readElement(Element.TYPE type, BsonStream stream){
        stream.readKey(locator);
        ImmutableInteger key = dictionary.key2Id(locator);
        Record record = records.add(type, key.get());
        try {
            record.read(stream, dictionary, records.getCache());
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void read(BsonStream stream) {
        int size = stream.getInt32();
        Element.TYPE type;
        while(true) {
            type = stream.readNextType();
            switch (type) {
                case EOO: {
                    //System.out.println("Exited at "+stream.position()+" of "+size);
                    return;
                }
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
        return records.toString();
    }


}
