package com.pk.bson;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by pkapovski on 8/4/2016.
 */
public class Document extends Element{
    private final Map<MutableString, Element> elements = new HashMap<>();
    private final MutableString locator = new MutableString(200); //200 should be good enough

    Document(MutableString name) {
        super(name);
    }


    private Element readElement(BSON.TYPE type, BsonStream stream){
        stream.readKey(locator);
        Element e = elements.get(locator);
        if (e==null) {
            MutableString elementName = new MutableString(locator);
            e = ElementFactory.createElement(type, elementName);
            elements.put(elementName, e);
        }
        e.read(stream);
        return e;
    }

    void read(BsonStream stream) {
        int size = stream.getINT32();
        //System.out.println("Start reading Object: size:"+size+ " at position "+stream.position());
        BSON.TYPE type;
        while(true) {
            type = stream.getType();
            switch (type) {
                case EOO: {
                    return;
                }
                case STRING:
                case INT32:
                case EMBEDDED: {
                    readElement(type, stream);
                    break;
                }
            }
        }
    }

    public int getInt32(MutableString key){
        INT32Element e = (INT32Element)elements.get(key);
        if (e!=null) {
            return e.getValue();
        } else {
            return 0;
        }
    }

    public  MutableString getString(MutableString key) {
        StringElement e = (StringElement)elements.get(key);
        if (e!=null) {
            return e.getValue();
        } else {
            return null;
        }
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
