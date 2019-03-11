package com.fishercoder;


import _890.Solution1;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


public class _890Test {
    private static Solution1 solution1;

    @Test
    public void test1() {
        Assert.assertEquals(Arrays.asList("mee", "aqq"), _890Test.solution1.findAndReplacePattern(new String[]{ "abc", "deq", "mee", "aqq", "dkd", "ccc" }, "abb"));
    }
}

