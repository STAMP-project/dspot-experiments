package com.fishercoder;


import com.fishercoder.solutions._43;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by fishercoder on 5/28/17.
 */
public class _43Test {
    private static _43 test;

    @Test
    public void test1() {
        Assert.assertEquals("5535", _43Test.test.multiply("123", "45"));
    }

    @Test
    public void test2() {
        Assert.assertEquals("0", _43Test.test.multiply("9133", "0"));
    }

    @Test
    public void test3() {
        Assert.assertEquals("491555843274052692", _43Test.test.multiply("6913259244", "71103343"));
    }

    @Test
    public void test4() {
        Assert.assertEquals("67143675422804947379429215144664313370120390398055713625298709447", _43Test.test.multiply("401716832807512840963", "167141802233061013023557397451289113296441069"));
    }
}

