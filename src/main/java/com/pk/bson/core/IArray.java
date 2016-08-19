package com.pk.bson.core;

/**
 * Created by pkapovski on 8/16/2016.
 */
public interface IArray extends ICollection {
    void addInt(int value);
    void addBoolean(boolean value);
    void addDouble(double value);
    IObject addObject();
    IArray addArray();
}
