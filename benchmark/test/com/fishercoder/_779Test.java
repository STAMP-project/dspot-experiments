package com.fishercoder;


import _779.Solution1;
import _779.Solution2;
import org.junit.Assert;
import org.junit.Test;


public class _779Test {
    private static Solution1 solution1;

    private static Solution2 solution2;

    @Test
    public void test1() {
        Assert.assertEquals(0, _779Test.solution1.kthGrammar(1, 1));
        Assert.assertEquals(0, _779Test.solution2.kthGrammar(1, 1));
    }

    @Test
    public void test2() {
        Assert.assertEquals(0, _779Test.solution1.kthGrammar(2, 1));
        Assert.assertEquals(0, _779Test.solution2.kthGrammar(2, 1));
    }

    @Test
    public void test3() {
        Assert.assertEquals(1, _779Test.solution1.kthGrammar(2, 2));
        Assert.assertEquals(1, _779Test.solution2.kthGrammar(2, 2));
    }

    @Test
    public void test4() {
        Assert.assertEquals(1, _779Test.solution1.kthGrammar(4, 5));
        Assert.assertEquals(1, _779Test.solution2.kthGrammar(4, 5));
    }
}

