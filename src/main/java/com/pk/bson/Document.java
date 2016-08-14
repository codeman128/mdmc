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


    public Document(MutableString name, StringDictionary dictionary, RecordCache cache) {
        super(RecordLinkedList.TYPE.OBJECT, name, dictionary, cache);
    }





}