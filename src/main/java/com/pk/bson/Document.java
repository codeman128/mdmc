package com.pk.bson;

import com.pk.bson.elements.*;
import com.pk.bson.lang.MutableString;
import com.pk.bson.lang.StringDictionary;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by pkapovski on 8/4/2016.
 */
public class Document extends ContainerElement {
    private final Map<MutableString, Element> elements = new HashMap<>();

    public Document(MutableString name, StringDictionary dictionary, RecordCache cache) {
        super(name, dictionary, cache);
    }

    private Document(){
        super(null, null, null);
    }

    @Override
    protected RecordLinkedList.TYPE getContainerType() {
        return RecordLinkedList.TYPE.OBJECT;
    }



    @Override
    public void read(BsonStream stream) {
        super.read(stream);
    }



}