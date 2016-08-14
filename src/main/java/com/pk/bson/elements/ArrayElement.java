package com.pk.bson.elements;

import com.pk.bson.BsonStream;
import com.pk.bson.lang.MutableString;
import com.pk.bson.lang.StringDictionary;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by PavelK on 8/9/2016.
 */
public class ArrayElement extends ContainerElement {
    protected List<Element> list = new ArrayList<>();

    ArrayElement(MutableString name, StringDictionary dictionary, RecordCache cache) {
        super(name, dictionary, cache);
    }

    @Override
    protected Element getElement(TYPE type, MutableString key) {
        return null;
    }

//    @Override
//    protected Element readElement(Element.TYPE type, BsonStream stream) {
//        stream.readKey(locator);
//        //MutableString elementName = new MutableString(locator);   no need to store it..
//        Element e = Element.createElement(type, null/*elementName*/, dictionary, records.getCache());
//        list.add(e);
//        e.read(stream);
//        return e;
//    }



}
