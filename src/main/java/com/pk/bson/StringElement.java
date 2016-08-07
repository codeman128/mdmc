package com.pk.bson;

/**
 * Created by pkapovski on 8/4/2016.
 */
public class StringElement extends Element {
    private MutableString mStr = new MutableString(200);

    StringElement(MutableString name) {
        super(name);
    }

    @Override
    void read(BsonStream stream) {
        stream.readString(mStr);
    }

    public MutableString getValue() {
        return mStr;
    }

    public String toString(){
        return mStr.toString();
    }

}
