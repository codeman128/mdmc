package com.pk.bson.elements;

import com.pk.bson.lang.ImmutableString;

/**
 * Created by pkapovski on 8/16/2016.
 */
public interface IObject extends ICollection {

    /**
     * Updates (or creates if didn't exists) element with specified key with provided double value
     * @param key key of the element
     * @param value new value
     **/
    void setDouble(ImmutableString key, double value);

    /**
     * Updates (or creates if didn't exists) element with specified key id with provided double value
     * @param key key id of the element
     * @param value new value
     **/
    void setDouble(int key, double value);

    /**
     * Retrieves double value of specified element by key id.
     * @param key key of the element
     * @return double value.  If element doesn't exists returns 0.
     **/
    double getDouble(int key) throws NoSuchFieldException;


    /**
     * Retrieves double value of specified element by key.
     * @param key key of the element
     * @return double value.  If element doesn't exists returns 0.
     **/
    double getDouble(ImmutableString key) throws NoSuchFieldException;

    /**
     * Updates (or creates if didn't exists) element with specified key with provided int value
     * @param key key of the element
     * @param value new value
     **/
    void setInt(ImmutableString key, int value);

    /**
     * Updates (or creates if didn't exists) element with specified key id with provided int value
     * @param key key id of the element
     * @param value new value
     **/
    void setInt(int key, int value);

    /**
     * Retrieves int value of specified element by key id.
     * @param key key of the element
     * @return int value.  If element doesn't exists returns 0.
     **/
    int getInt(int key) throws NoSuchFieldException;

    /**
     * Retrieves int value of specified element by key.
     * @param key key of the element
     * @return int value.  If element doesn't exists returns 0.
     **/
    int getInt(ImmutableString key) throws NoSuchFieldException;


    /**
     * Updates (or creates if didn't exists) boolean element with specified key with provided int value
     * @param key key of the element
     * @param value new value
     **/
    void setBoolean(ImmutableString key, boolean value);

    /**
     * Updates (or creates if didn't exists) boolean element with specified key id with provided int value
     * @param key key id of the element
     * @param value new value
     **/
    void setBoolean(int key, boolean value);

    /**
     * Retrieves boolean value of specified element by key id.
     * @param key key of the element
     * @return boolean value.  If element doesn't exists returns false.
     **/
    boolean getBoolean(int key) throws NoSuchFieldException;

    /**
     * Retrieves boolean value of specified element by key.
     * @param key key of the element
     * @return boolean value.  If element doesn't exists returns false.
     **/
    boolean getBoolean(ImmutableString key) throws NoSuchFieldException;


    IObject getObject(int key) throws NoSuchFieldException;
    IObject getObject(ImmutableString key) throws NoSuchFieldException;

    IArray getArray(int key) throws NoSuchFieldException;
    IArray getArray(ImmutableString key) throws NoSuchFieldException;
}
