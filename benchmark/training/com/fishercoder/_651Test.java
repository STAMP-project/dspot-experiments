package com.fishercoder;


import com.fishercoder.solutions._651;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by stevesun on 7/30/17.
 */
public class _651Test {
    private static _651 test;

    @Test
    public void test1() {
        Assert.assertEquals(3, _651Test.test.maxA(3));
    }

    @Test
    public void test2() {
        Assert.assertEquals(324, _651Test.test.maxA(20));
    }

    @Test
    public void test3() {
        Assert.assertEquals(256, _651Test.test.maxA(19));
    }

    @Test
    public void test4() {
        Assert.assertEquals(1, _651Test.test.maxA(1));
    }

    @Test
    public void test5() {
        Assert.assertEquals(1327104, _651Test.test.maxA(50));
    }

    @Test
    public void test6() {
        Assert.assertEquals(9, _651Test.test.maxA(7));
    }
}

