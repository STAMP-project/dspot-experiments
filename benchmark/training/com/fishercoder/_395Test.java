package com.fishercoder;


import _395.Solution1;
import junit.framework.Assert;
import org.junit.Test;


/**
 * Created by fishercoder on 12/31/16.
 */
public class _395Test {
    private static Solution1 solution1;

    @Test
    public void test1() {
        Assert.assertEquals(5, _395Test.solution1.longestSubstring("ababbc", 2));
    }

    @Test
    public void test2() {
        Assert.assertEquals(3, _395Test.solution1.longestSubstring("aaabb", 3));
    }
}

