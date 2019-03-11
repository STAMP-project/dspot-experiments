package com.baeldung.pairs;


import java.util.AbstractMap;
import org.junit.Assert;
import org.junit.Test;


public class CoreJavaSimpleEntryUnitTest {
    @Test
    public void givenSimpleEntry_whenGetValue_thenOk() {
        AbstractMap.SimpleEntry<Integer, String> entry = new AbstractMap.SimpleEntry<Integer, String>(1, "one");
        Integer key = entry.getKey();
        String value = entry.getValue();
        Assert.assertEquals(key.intValue(), 1);
        Assert.assertEquals(value, "one");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void givenSimpleImmutableEntry_whenSetValue_thenException() {
        AbstractMap.SimpleImmutableEntry<Integer, String> entry = new AbstractMap.SimpleImmutableEntry<Integer, String>(1, "one");
        entry.setValue("two");
    }

    @Test
    public void givenSimpleImmutableEntry_whenGetValue_thenOk() {
        AbstractMap.SimpleImmutableEntry<Integer, String> entry = new AbstractMap.SimpleImmutableEntry<Integer, String>(1, "one");
        Integer key = entry.getKey();
        String value = entry.getValue();
        Assert.assertEquals(key.intValue(), 1);
        Assert.assertEquals(value, "one");
    }
}

