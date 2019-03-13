package com.baeldung.parameterpassing;


import org.junit.Assert;
import org.junit.Test;


public class PrimitivesUnitTest {
    @Test
    public void whenModifyingPrimitives_thenOriginalValuesNotModified() {
        int x = 1;
        int y = 2;
        // Before Modification
        Assert.assertEquals(x, 1);
        Assert.assertEquals(y, 2);
        PrimitivesUnitTest.modify(x, y);
        // After Modification
        Assert.assertEquals(x, 1);
        Assert.assertEquals(y, 2);
    }
}

