package com.pk.bson.elements;

import com.pk.bson.BsonStream;
import com.pk.bson.MutableString;

/**
 * Created by PavelK on 8/10/2016.
 */
public abstract class ContainerElement extends Element {
    protected final MutableString locator = new MutableString(200); //200 should be good enough


    protected ContainerElement(MutableString name) {
        super(name);
    }

    protected abstract Element getElement(Element.TYPE type, MutableString key);

    protected Element readElement(Element.TYPE type, BsonStream stream){
        stream.readKey(locator);
        Element e = getElement(type, locator);
        e.read(stream);
        return e;
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
                case STRING:
                case INT32:
                case EMBEDDED:
                case ARRAY: {
                    readElement(type, stream);
                    break;
                }
            }
        }
    }

}
