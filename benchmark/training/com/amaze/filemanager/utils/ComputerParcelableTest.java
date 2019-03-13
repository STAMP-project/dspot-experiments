package com.amaze.filemanager.utils;


import org.junit.Assert;
import org.junit.Test;


public class ComputerParcelableTest {
    /**
     * Purpose: check computerParcelable and object are the equal.
     * Input: computerParcelable.equals(object) ComputerParcelable == Object
     * Expected:
     * result is true
     */
    @Test
    public void testObjectEquals() {
        ComputerParcelable computerParcelable = new ComputerParcelable("com1", "1");
        Object object = new ComputerParcelable("com1", "1");
        Assert.assertTrue(computerParcelable.equals(object));
    }

    /**
     * Purpose: when computerParcelable's name and object's name are not the same, confirm that the two are different.
     * Input: computerParcelable.equals(object) only ComputerParcelable.addr == Object.addr
     * Expected:
     * result is false
     */
    @Test
    public void testObjectNotEqualsName() {
        ComputerParcelable computerParcelable = new ComputerParcelable("com1", "1");
        Object object = new ComputerParcelable("com2", "1");
        Assert.assertFalse(computerParcelable.equals(object));
    }

    /**
     * Purpose: when computerParcelable's address and object's address are not the same, confirm that the two are different.
     * Input: computerParcelable.equals(object) only ComputerParcelable.name == Object.name
     * Expected:
     * result is false
     */
    @Test
    public void testObjectNotEqualsAddr() {
        ComputerParcelable computerParcelable = new ComputerParcelable("com1", "1");
        Object object = new ComputerParcelable("com1", "2");
        Assert.assertFalse(computerParcelable.equals(object));
    }

    /**
     * Purpose: when computerParcelable's name/address and object's name/address are not the same, confirm that the two are different.
     * Input: computerParcelable.equals(object) ComputerParcelable and Object not same(name, address)
     * Expected:
     * result is false
     */
    @Test
    public void testObjectNotEqualsTwo() {
        ComputerParcelable computerParcelable = new ComputerParcelable("com1", "1");
        Object object = new ComputerParcelable("com2", "2");
        Assert.assertFalse(computerParcelable.equals(object));
    }
}

