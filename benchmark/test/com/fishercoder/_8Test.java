package com.fishercoder;


import _8.Solution1;
import _8.Solution2;
import org.junit.Assert;
import org.junit.Test;


public class _8Test {
    private static Solution1 solution1;

    private static Solution2 solution2;

    @Test
    public void test1() {
        Assert.assertEquals(2147483647, _8Test.solution1.myAtoi("2147483648"));
        Assert.assertEquals(2147483647, _8Test.solution2.myAtoi("2147483648"));
    }

    @Test
    public void test2() {
        Assert.assertEquals(0, _8Test.solution1.myAtoi("+-2"));
        Assert.assertEquals(0, _8Test.solution2.myAtoi("+-2"));
    }

    @Test
    public void test3() {
        Assert.assertEquals(0, _8Test.solution1.myAtoi("+"));
        Assert.assertEquals(0, _8Test.solution2.myAtoi("+"));
    }

    @Test
    public void test4() {
        Assert.assertEquals(0, _8Test.solution1.myAtoi("abc"));
        Assert.assertEquals(0, _8Test.solution2.myAtoi("abc"));
    }

    @Test
    public void test5() {
        Assert.assertEquals(1, _8Test.solution1.myAtoi("1"));
        Assert.assertEquals(1, _8Test.solution2.myAtoi("1"));
    }

    @Test
    public void test6() {
        Assert.assertEquals(-2147483648, _8Test.solution1.myAtoi("-2147483648"));
        Assert.assertEquals(-2147483648, _8Test.solution2.myAtoi("-2147483648"));
    }

    @Test
    public void test7() {
        Assert.assertEquals(0, _8Test.solution1.myAtoi("++1"));
        Assert.assertEquals(0, _8Test.solution2.myAtoi("++1"));
    }

    @Test
    public void test8() {
        Assert.assertEquals(-2147483648, _8Test.solution1.myAtoi("-2147483649"));
        Assert.assertEquals(-2147483648, _8Test.solution2.myAtoi("-2147483649"));
    }

    @Test
    public void test9() {
        Assert.assertEquals(2147483647, _8Test.solution1.myAtoi("9223372036854775809"));
        Assert.assertEquals(2147483647, _8Test.solution2.myAtoi("9223372036854775809"));
    }
}

