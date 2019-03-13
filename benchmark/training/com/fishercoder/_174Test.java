package com.fishercoder;


import _174.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _174Test {
    private static Solution1 solution1;

    private int[][] dungeon;

    @Test
    public void test1() {
        dungeon = new int[][]{ new int[]{ 0 } };
        Assert.assertEquals(1, _174Test.solution1.calculateMinimumHP(dungeon));
    }

    @Test
    public void test2() {
        dungeon = new int[][]{ new int[]{ -200 } };
        Assert.assertEquals(201, _174Test.solution1.calculateMinimumHP(dungeon));
    }
}

