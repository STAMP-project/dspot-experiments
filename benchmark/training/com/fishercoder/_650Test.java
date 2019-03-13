package com.fishercoder;


import com.fishercoder.solutions._650;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by stevesun on 7/30/17.
 */
public class _650Test {
    private static _650 test;

    @Test
    public void test1() {
        Assert.assertEquals(3, _650Test.test.minSteps(3));
    }

    @Test
    public void test2() {
        Assert.assertEquals(9, _650Test.test.minSteps(20));
    }

    @Test
    public void test3() {
        Assert.assertEquals(19, _650Test.test.minSteps(19));
    }

    @Test
    public void test4() {
        Assert.assertEquals(0, _650Test.test.minSteps(1));
    }

    @Test
    public void test5() {
        Assert.assertEquals(35, _650Test.test.minSteps(741));
    }
}

