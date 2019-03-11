package com.sun.btrace;


import org.junit.Assert;
import org.junit.Test;


public class ArgsMapTest {
    private static final String KEY1 = "key1";

    private static final String KEY2 = "key2";

    private static final String VALUE1 = "value1";

    private static final String VALUE2 = "value2";

    private ArgsMap instance;

    @Test
    public void templateExisting() {
        String value = instance.template(((((ArgsMapTest.KEY1) + "=${") + (ArgsMapTest.KEY1)) + "}"));
        Assert.assertEquals((((ArgsMapTest.KEY1) + "=") + (ArgsMapTest.VALUE1)), value);
    }

    @Test
    public void templateNonExisting() {
        String orig = (ArgsMapTest.KEY1) + "=${key3}";
        String value = instance.template(orig);
        Assert.assertEquals(orig, value);
    }

    @Test
    public void templateTrailing$() {
        String orig = (ArgsMapTest.KEY1) + "$";
        String value = instance.template(orig);
        Assert.assertEquals(orig, value);
    }

    @Test
    public void templateUnclosedPlaceholder() {
        String orig = (ArgsMapTest.KEY1) + "${";
        String value = instance.template(orig);
        Assert.assertEquals(orig, value);
    }

    @Test
    public void templateSingle$() {
        String orig = ((ArgsMapTest.KEY1) + "$") + (ArgsMapTest.KEY2);
        String value = instance.template(orig);
        Assert.assertEquals(orig, value);
    }
}

