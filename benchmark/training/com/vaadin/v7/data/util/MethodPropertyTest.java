package com.vaadin.v7.data.util;


import org.junit.Assert;
import org.junit.Test;


public class MethodPropertyTest {
    private NestedMethodPropertyTest.Address testObject;

    @Test
    public void getValue() {
        MethodProperty<String> mp = new MethodProperty<String>(testObject, "street");
        Assert.assertEquals("some street", mp.getValue());
    }

    @Test
    public void getValueAfterBeanUpdate() {
        MethodProperty<String> mp = new MethodProperty<String>(testObject, "street");
        testObject.setStreet("Foo street");
        Assert.assertEquals("Foo street", mp.getValue());
    }

    @Test
    public void setValue() {
        MethodProperty<String> mp = new MethodProperty<String>(testObject, "street");
        mp.setValue("Foo street");
        Assert.assertEquals("Foo street", testObject.getStreet());
    }

    @Test
    public void changeInstance() {
        MethodProperty<String> mp = new MethodProperty<String>(testObject, "street");
        NestedMethodPropertyTest.Address newStreet = new NestedMethodPropertyTest.Address("new street", 999);
        mp.setInstance(newStreet);
        Assert.assertEquals("new street", mp.getValue());
        Assert.assertEquals("some street", testObject.getStreet());
    }

    @Test(expected = IllegalArgumentException.class)
    public void changeInstanceToIncompatible() {
        MethodProperty<String> mp = new MethodProperty<String>(testObject, "street");
        mp.setInstance("foobar");
    }
}

