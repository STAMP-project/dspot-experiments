package com.fishercoder;


import com.fishercoder.solutions._476;
import junit.framework.Assert;
import org.junit.Test;


/**
 * Created by fishercoder on 1/14/17.
 */
public class _476Test {
    private static _476 test;

    private static int expected;

    private static int actual;

    private static int input;

    @Test
    public void test1() {
        _476Test.input = 5;
        _476Test.expected = 2;
        _476Test.actual = _476Test.test.findComplement(_476Test.input);
        Assert.assertEquals(_476Test.expected, _476Test.actual);
    }

    @Test
    public void test2() {
        _476Test.input = 5;
        _476Test.expected = 2;
        _476Test.actual = _476Test.test.findComplement_oneLiner(_476Test.input);
        Assert.assertEquals(_476Test.expected, _476Test.actual);
    }
}

