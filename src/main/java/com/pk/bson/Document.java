package com.pk.bson;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by pkapovski on 8/4/2016.
 */
public class Document extends Element{
    private final Map<ElementName, Element> elements = new HashMap<>();

    Document(ElementName name) {
        super(name);
    }

    public Map<ElementName, Element> getElements(){
        return elements;
    }

    public INT32Element addInt32(byte[] name, int nameOffset, int nameLength, int value){
        ElementName elementName = new ElementName(name, nameOffset, nameLength);
        INT32Element element = new INT32Element(elementName, value);
        elements.put(elementName, element);
        return element;
    }

    public StringElement addString(byte[] name, int nameOffset, int nameLength) {
        ElementName elementName = new ElementName(name, nameOffset, nameLength);
        StringElement element = new StringElement(elementName);
        elements.put(elementName, element);
        return element;
    }

    public Document addDocument(byte[] name, int nameOffset, int nameLength) {
        ElementName elementName = new ElementName(name, nameOffset, nameLength);
        Document doc = new Document(elementName);
        elements.put(elementName, doc);
        return doc;
    }

    void read(BsonStream stream) {
        int size = stream.getINT32();
        while(true) {
            switch (stream.getType()) {
                case EOO: {
                    return;
                }
                case STRING: {
                    StringElement se = addString(stream.getBuffer(), 0, stream.readKey());
                    se.setValue(stream.getBuffer(), 0, stream.readString());
                    break;
                }
                case  EMBEDDED: {
                    Document doc = addDocument(stream.getBuffer(), 0, stream.readKey());
                    doc.read(stream);
                    break;
                }
                case INT32: {
                    addInt32(stream.getBuffer(), 0, stream.readKey(), stream.getINT32());
                    break;
                }
            }
        }

    }

}
