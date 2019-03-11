package com.fishercoder;


import _443.Solution1;
import junit.framework.Assert;
import org.junit.Test;


public class _443Test {
    private static Solution1 solution1;

    private static char[] chars;

    @Test
    public void test1() {
        _443Test.chars = new char[]{ 'a', 'a', 'b', 'b', 'c', 'c', 'c' };
        Assert.assertEquals(6, _443Test.solution1.compress(_443Test.chars));
    }

    @Test
    public void test2() {
        _443Test.chars = new char[]{ 'a' };
        Assert.assertEquals(1, _443Test.solution1.compress(_443Test.chars));
    }

    @Test
    public void test3() {
        _443Test.chars = new char[]{ 'a', 'b' };
        Assert.assertEquals(2, _443Test.solution1.compress(_443Test.chars));
    }

    @Test
    public void test4() {
        _443Test.chars = new char[]{ 'a', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b' };
        Assert.assertEquals(4, _443Test.solution1.compress(_443Test.chars));
    }
}

