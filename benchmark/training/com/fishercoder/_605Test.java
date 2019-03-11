package com.fishercoder;


import com.fishercoder.solutions._605;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by stevesun on 6/4/17.
 */
public class _605Test {
    private static _605 test;

    private static int[] flowerbed;

    private static int n;

    @Test
    public void test1() {
        _605Test.flowerbed = new int[]{ 1, 0, 0, 0, 1 };
        _605Test.n = 1;
        Assert.assertEquals(true, _605Test.test.canPlaceFlowers(_605Test.flowerbed, _605Test.n));
    }

    @Test
    public void test2() {
        _605Test.flowerbed = new int[]{ 1, 0, 0, 0, 1 };
        _605Test.n = 2;
        Assert.assertEquals(false, _605Test.test.canPlaceFlowers(_605Test.flowerbed, _605Test.n));
    }

    @Test
    public void test3() {
        _605Test.flowerbed = new int[]{ 1, 0, 0, 0, 0, 1 };
        _605Test.n = 2;
        Assert.assertEquals(false, _605Test.test.canPlaceFlowers(_605Test.flowerbed, _605Test.n));
    }

    @Test
    public void test4() {
        _605Test.flowerbed = new int[]{ 1, 0, 1, 0, 1, 0, 1 };
        _605Test.n = 1;
        Assert.assertEquals(false, _605Test.test.canPlaceFlowers(_605Test.flowerbed, _605Test.n));
    }

    @Test
    public void test5() {
        _605Test.flowerbed = new int[]{ 0, 0, 1, 0, 1 };
        _605Test.n = 1;
        Assert.assertEquals(true, _605Test.test.canPlaceFlowers(_605Test.flowerbed, _605Test.n));
    }

    @Test
    public void test6() {
        _605Test.flowerbed = new int[]{ 1, 0, 0, 0, 1, 0, 0 };
        _605Test.n = 2;
        Assert.assertEquals(true, _605Test.test.canPlaceFlowers(_605Test.flowerbed, _605Test.n));
    }

    @Test
    public void test7() {
        _605Test.flowerbed = new int[]{ 0, 0, 1, 0, 0 };
        _605Test.n = 2;
        Assert.assertEquals(true, _605Test.test.canPlaceFlowers(_605Test.flowerbed, _605Test.n));
    }

    @Test
    public void test8() {
        _605Test.flowerbed = new int[]{ 1 };
        _605Test.n = 0;
        Assert.assertEquals(true, _605Test.test.canPlaceFlowers(_605Test.flowerbed, _605Test.n));
    }

    @Test
    public void test9() {
        _605Test.flowerbed = new int[]{ 0 };
        _605Test.n = 0;
        Assert.assertEquals(true, _605Test.test.canPlaceFlowers(_605Test.flowerbed, _605Test.n));
    }

    @Test
    public void test10() {
        _605Test.flowerbed = new int[]{ 0 };
        _605Test.n = 1;
        Assert.assertEquals(true, _605Test.test.canPlaceFlowers(_605Test.flowerbed, _605Test.n));
    }
}

