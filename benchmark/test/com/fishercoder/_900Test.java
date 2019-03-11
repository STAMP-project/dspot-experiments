package com.fishercoder;


import _900.Solution1.RLEIterator;
import org.junit.Assert;
import org.junit.Test;


public class _900Test {
    private static RLEIterator rleIterator;

    @Test
    public void test1() {
        _900Test.rleIterator = new RLEIterator(new int[]{ 3, 8, 0, 9, 2, 5 });
        Assert.assertEquals(8, _900Test.rleIterator.next(2));
        Assert.assertEquals(8, _900Test.rleIterator.next(1));
        Assert.assertEquals(5, _900Test.rleIterator.next(1));
        Assert.assertEquals((-1), _900Test.rleIterator.next(2));
    }

    @Test
    public void test2() {
        _900Test.rleIterator = new RLEIterator(new int[]{ 811, 903, 310, 730, 899, 684, 472, 100, 434, 611 });
        Assert.assertEquals(903, _900Test.rleIterator.next(358));
        Assert.assertEquals(903, _900Test.rleIterator.next(345));
        Assert.assertEquals(730, _900Test.rleIterator.next(154));
        Assert.assertEquals(684, _900Test.rleIterator.next(265));
        Assert.assertEquals(684, _900Test.rleIterator.next(73));
        Assert.assertEquals(684, _900Test.rleIterator.next(220));
        Assert.assertEquals(684, _900Test.rleIterator.next(138));
        Assert.assertEquals(684, _900Test.rleIterator.next(4));
        Assert.assertEquals(684, _900Test.rleIterator.next(170));
        Assert.assertEquals(684, _900Test.rleIterator.next(88));
    }
}

