package com.pk.bson.elements;

import com.pk.bson.BsonStream;
import com.pk.bson.lang.ImmutableInteger;
import com.pk.bson.lang.MutableString;
import com.pk.bson.lang.StringDictionary;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by PavelK on 8/9/2016.
 */
public class ArrayElement extends ContainerElement {

    @Override
    protected RecordLinkedList.TYPE getContainerType() {
        return RecordLinkedList.TYPE.ARRAY;
    }

    ArrayElement(MutableString name, StringDictionary dictionary, RecordCache cache) {
        super(name, dictionary, cache);
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


}
