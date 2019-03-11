package com.baeldung.parameterpassing;


import org.junit.Assert;
import org.junit.Test;


public class NonPrimitivesUnitTest {
    @Test
    public void whenModifyingObjects_thenOriginalObjectChanged() {
        Foo a = new Foo(1);
        Foo b = new Foo(1);
        // Before Modification
        Assert.assertEquals(a.num, 1);
        Assert.assertEquals(b.num, 1);
        NonPrimitivesUnitTest.modify(a, b);
        // After Modification
        Assert.assertEquals(a.num, 2);
        Assert.assertEquals(b.num, 1);
    }
}

