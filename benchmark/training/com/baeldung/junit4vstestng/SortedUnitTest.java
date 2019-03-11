package com.baeldung.junit4vstestng;


import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SortedUnitTest {
    @Test
    public void a_givenString_whenChangedtoInt_thenTrue() {
        Assert.assertTrue(((Integer.valueOf("10")) instanceof Integer));
    }

    @Test
    public void b_givenInt_whenChangedtoString_thenTrue() {
        Assert.assertTrue(((String.valueOf(10)) instanceof String));
    }
}

