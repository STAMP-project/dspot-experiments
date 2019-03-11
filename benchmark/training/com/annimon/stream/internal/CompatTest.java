package com.annimon.stream.internal;


import org.junit.Assert;
import org.junit.Test;


public final class CompatTest {
    @Test
    public void testNewArrayCompat() {
        String[] strings = new String[]{ "abc", "def", "fff" };
        String[] copy = Compat.newArrayCompat(strings, 5);
        Assert.assertEquals(5, copy.length);
        Assert.assertEquals("abc", copy[0]);
        Assert.assertNull(copy[3]);
        String[] empty = new String[0];
        String[] emptyCopy = Compat.newArrayCompat(empty, 3);
        Assert.assertEquals(3, emptyCopy.length);
        emptyCopy = Compat.newArrayCompat(empty, 0);
        Assert.assertEquals(0, emptyCopy.length);
    }
}

