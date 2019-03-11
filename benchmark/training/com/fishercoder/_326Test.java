package com.fishercoder;


import _326.Solution1;
import _326.Solution2;
import _326.Solution3;
import junit.framework.Assert;
import org.junit.Test;


public class _326Test {
    private static Solution1 solution1;

    private static Solution2 solution2;

    private static Solution3 solution3;

    @Test
    public void test1() {
        Assert.assertEquals(false, _326Test.solution1.isPowerOfThree(12));
        Assert.assertEquals(false, _326Test.solution2.isPowerOfThree(12));
        Assert.assertEquals(false, _326Test.solution3.isPowerOfThree(12));
    }
}

