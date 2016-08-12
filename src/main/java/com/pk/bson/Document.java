package com.pk.bson;

import com.pk.bson.elements.ContainerElement;
import com.pk.bson.elements.Element;
import com.pk.bson.elements.InT32Element;
import com.pk.bson.elements.StringElement;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by pkapovski on 8/4/2016.
 */
public class Document extends ContainerElement {
    private final Map<MutableString, Element> elements = new HashMap<>();

    public Document(MutableString name) {
        super(name);
    }

    public Document(){
        super(null);
    }

    protected Element getElement(Element.TYPE type, MutableString key){
        Element e = elements.get(key);
        if (e==null) {
            MutableString elementName = new MutableString(key);
            e = Element.createElement(type, elementName);
            elements.put(elementName, e);
        }
        return e;
    }


    @Override
    protected void read(BsonStream stream) {
        super.read(stream);
    }

    public int getInt32(MutableString key){
        InT32Element e = (InT32Element)elements.get(key);
        if (e!=null) {
            return e.getValue();
        } else {
            return 0;
        }
    }

    public void setInt32(MutableString key, int value) {
        InT32Element e = (InT32Element) getElement(Element.TYPE.INT32, key);
        e.setValue(value);
    }

    public MutableString getString(MutableString key) {
        StringElement e = (StringElement)elements.get(key);
        if (e!=null) {
            return e.getValue();
        } else {
            return null;
        }
    }

    public void setString(MutableString key, MutableString value) {
        StringElement e = (StringElement) getElement(Element.TYPE.STRING, key);
        e.setValue(value);
    }


    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("{");

        boolean isFirst = true;
        for (Map.Entry<MutableString, Element> entry : elements.entrySet()) {

            if (isFirst) {
                isFirst = false;
            } else {
                sb.append(",");
            }

            MutableString key = entry.getKey();
            Object value = entry.getValue();
            sb.append("\"");
            sb.append(key);
            sb.append("\":");
            sb.append(value);
        }

        sb.append("}");
        return sb.toString();
    }


}