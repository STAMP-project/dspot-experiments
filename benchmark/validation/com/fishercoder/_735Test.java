package com.fishercoder;


import _735.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _735Test {
    private static Solution1 solution1;

    private static int[] asteroids;

    @Test
    public void test1() {
        _735Test.asteroids = new int[]{ 5, 10, -5 };
        _735Test.asteroids = _735Test.solution1.asteroidCollision(_735Test.asteroids);
        Assert.assertArrayEquals(new int[]{ 5, 10 }, _735Test.asteroids);
    }

    @Test
    public void test2() {
        _735Test.asteroids = new int[]{ 8, -8 };
        _735Test.asteroids = _735Test.solution1.asteroidCollision(_735Test.asteroids);
        Assert.assertArrayEquals(new int[]{  }, _735Test.asteroids);
    }

    @Test
    public void test3() {
        _735Test.asteroids = new int[]{ 10, 2, -5 };
        _735Test.asteroids = _735Test.solution1.asteroidCollision(_735Test.asteroids);
        Assert.assertArrayEquals(new int[]{ 10 }, _735Test.asteroids);
    }

    @Test
    public void test4() {
        _735Test.asteroids = new int[]{ -2, 1, 2, -2 };
        _735Test.asteroids = _735Test.solution1.asteroidCollision(_735Test.asteroids);
        Assert.assertArrayEquals(new int[]{ -2, 1 }, _735Test.asteroids);
    }

    @Test
    public void test5() {
        _735Test.asteroids = new int[]{ -2, -2, -2, 1 };
        _735Test.asteroids = _735Test.solution1.asteroidCollision(_735Test.asteroids);
        Assert.assertArrayEquals(new int[]{ -2, -2, -2, 1 }, _735Test.asteroids);
    }

    @Test
    public void test6() {
        _735Test.asteroids = new int[]{ -2, -1, 1, 2 };
        _735Test.asteroids = _735Test.solution1.asteroidCollision(_735Test.asteroids);
        Assert.assertArrayEquals(new int[]{ -2, -1, 1, 2 }, _735Test.asteroids);
    }

    @Test
    public void test7() {
        _735Test.asteroids = new int[]{ -2, -2, 1, -2 };
        _735Test.asteroids = _735Test.solution1.asteroidCollision(_735Test.asteroids);
        Assert.assertArrayEquals(new int[]{ -2, -2, -2 }, _735Test.asteroids);
    }

    @Test
    public void test8() {
        _735Test.asteroids = new int[]{ -4, -1, 10, 2, -1, 8, -9, -6, 5, 2 };
        _735Test.asteroids = _735Test.solution1.asteroidCollision(_735Test.asteroids);
        Assert.assertArrayEquals(new int[]{ -4, -1, 10, 5, 2 }, _735Test.asteroids);
    }
}

