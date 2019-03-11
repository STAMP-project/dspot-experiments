package com.fishercoder;


import _191.Solution1;
import _191.Solution2;
import _191.Solution3;
import _191.Solution4;
import junit.framework.TestCase;
import org.junit.Test;


public class _191Test {
    private static Solution1 solution1;

    private static Solution2 solution2;

    private static Solution3 solution3;

    private static Solution4 solution4;

    @Test
    public void test1() {
        TestCase.assertEquals(1, _191Test.solution1.hammingWeight(1));
        TestCase.assertEquals(1, _191Test.solution2.hammingWeight(1));
        TestCase.assertEquals(1, _191Test.solution3.hammingWeight(1));
        TestCase.assertEquals(1, _191Test.solution4.hammingWeight(1));
    }

    @Test
    public void test2() {
        // System.out.println(Integer.MAX_VALUE);
        // assertEquals(2147483648, Integer.MAX_VALUE);
    }
}

