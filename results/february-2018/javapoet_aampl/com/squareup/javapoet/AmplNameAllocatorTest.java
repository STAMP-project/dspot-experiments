package com.squareup.javapoet;


import org.junit.Assert;
import org.junit.Test;


public final class AmplNameAllocatorTest {
    @Test(timeout = 10000)
    public void tagReuseForbidden() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbidden__3 = nameAllocator.newName("foo", 1);
        try {
            nameAllocator.newName("bar", 1);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("foo", o_tagReuseForbidden__3);
    }
}

