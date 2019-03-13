package com.fishercoder;


import com.fishercoder.solutions._421;
import junit.framework.Assert;
import org.junit.Test;


/**
 * Created by fishercoder on 4/28/17.
 */
public class _421Test {
    private static _421 test;

    private static int expected;

    private static int actual;

    private static int[] nums;

    @Test
    public void test1() {
        _421Test.nums = new int[]{ 3, 10, 5, 25, 2, 8 };
        _421Test.expected = 28;
        _421Test.actual = _421Test.test.findMaximumXOR(_421Test.nums);
        Assert.assertEquals(_421Test.expected, _421Test.actual);
    }
}

