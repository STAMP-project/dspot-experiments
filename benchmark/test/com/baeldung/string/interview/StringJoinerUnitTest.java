package com.baeldung.string.interview;


import java.util.StringJoiner;
import org.junit.Assert;
import org.junit.Test;


public class StringJoinerUnitTest {
    @Test
    public void whenUsingStringJoiner_thenStringsJoined() {
        StringJoiner joiner = new StringJoiner(",", "[", "]");
        joiner.add("Red").add("Green").add("Blue");
        Assert.assertEquals(joiner.toString(), "[Red,Green,Blue]");
    }
}

