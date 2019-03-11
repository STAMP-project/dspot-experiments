package com.fishercoder;


import _600.DPSolution;
import com.fishercoder.solutions._600;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by fishercoder on 5/28/17.
 */
public class _600Test {
    private static _600 test;

    private static DPSolution dpSolution;

    @Test
    public void test1() {
        Assert.assertEquals(5, _600Test.dpSolution.findIntegers(5));
        Assert.assertEquals(514229, _600Test.dpSolution.findIntegers(100000000));
        Assert.assertEquals(5, _600Test.test.findIntegers(5));
        Assert.assertEquals(514229, _600Test.test.findIntegers(100000000));// this takes too long when using brute force

    }
}

