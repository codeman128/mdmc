package com.pk.bson.elements;

import com.pk.bson.BsonStream;
import com.pk.bson.lang.ImmutableInteger;
import com.pk.bson.lang.MutableString;
import com.pk.bson.lang.StringDictionary;

/**
 * Created by PavelK on 8/10/2016.
 */
public class ContainerElement extends Element {
    protected final MutableString locator = new MutableString(200); //200 should be good enough
    protected final RecordLinkedList records;
    protected final StringDictionary dictionary;

    private ContainerElement(){
        super(null);
        records = null;
        dictionary = null;
    }

    public ContainerElement(RecordLinkedList.TYPE type, MutableString name, StringDictionary dictionary, RecordCache cache) {
        super(name);
        records = new RecordLinkedList(type, cache, dictionary);
        this.dictionary = dictionary;
    }


    protected void readElement(Element.TYPE type, BsonStream stream){
        stream.readKey(locator);
        int keyId = -1;
        if (records.getType()== RecordLinkedList.TYPE.OBJECT){
            ImmutableInteger key = dictionary.key2Id(locator);
            keyId = key.get();
        }
        Record record = records.add(type, keyId);
        try {
            record.read(stream, dictionary, records.getCache());
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void read(BsonStream stream) {
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
