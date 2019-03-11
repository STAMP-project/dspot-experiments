package com.fishercoder;


import _727.Solution1;
import _727.Solution2;
import org.junit.Assert;
import org.junit.Test;


public class _727Test {
    private static Solution1 solution1;

    private static Solution2 solution2;

    private static String S;

    private static String T;

    @Test
    public void test1() {
        _727Test.S = "abcdebdde";
        _727Test.T = "bde";
        Assert.assertEquals("bcde", _727Test.solution1.minWindow(_727Test.S, _727Test.T));
        Assert.assertEquals("bcde", _727Test.solution2.minWindow(_727Test.S, _727Test.T));
    }

    @Test
    public void test2() {
        String S = "jmeqksfrsdcmsiwvaovztaqenprpvnbstl";
        String T = "l";
        Assert.assertEquals("l", _727Test.solution1.minWindow(S, T));
        Assert.assertEquals("l", _727Test.solution2.minWindow(S, T));
    }
}

