package com.fishercoder;


import com.fishercoder.solutions._664;
import org.junit.Assert;
import org.junit.Test;


public class _664Test {
    private static _664 test;

    @Test
    public void test1() {
        Assert.assertEquals(2, _664Test.test.strangePrinter("aaabbb"));
    }

    @Test
    public void test2() {
        Assert.assertEquals(2, _664Test.test.strangePrinter("aba"));
    }
}

