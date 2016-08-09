package com.pk.bson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by PavelK on 8/9/2016.
 */
public class Array extends ContainerElement{
    protected List<Element> list = new ArrayList<>();

    Array(MutableString name) {
        super(name);
    }

    @Override
    protected Element readElement(BSON.TYPE type, BsonStream stream) {
        stream.readKey(locator);
        MutableString elementName = new MutableString(locator);
        Element e = ElementFactory.createElement(type, elementName);
        list.add(e);
        e.read(stream);
        return e;
    }


}
