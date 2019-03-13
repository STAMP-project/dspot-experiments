package com.baeldung.pmd;


import org.junit.Assert;
import org.junit.Test;


public class CntUnitTest {
    private Cnt service;

    @Test
    public void whenSecondParamIsZeroShouldReturnIntMax() {
        service = new Cnt();
        int answer = service.d(100, 0);
        Assert.assertEquals(Integer.MAX_VALUE, answer);
    }
}

