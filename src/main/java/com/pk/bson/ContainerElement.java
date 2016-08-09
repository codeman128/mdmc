package com.pk.bson;

/**
 * Created by PavelK on 8/10/2016.
 */
public abstract class ContainerElement extends Element{
    protected final MutableString locator = new MutableString(200); //200 should be good enough


    ContainerElement(MutableString name) {
        super(name);
    }


    protected abstract Element readElement(BSON.TYPE type, BsonStream stream);

    void read(BsonStream stream) {
        int size = stream.getInt32();
        BSON.TYPE type;
        while(true) {
            type = stream.getType();
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
