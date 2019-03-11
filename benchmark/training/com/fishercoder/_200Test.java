package com.fishercoder;


import _200.Solution1;
import _200.Solution2;
import org.junit.Assert;
import org.junit.Test;


public class _200Test {
    private static Solution1 solution1;

    private static Solution2 solution2;

    private static char[][] grid;

    @Test
    public void test1() {
        _200Test.grid = new char[][]{ new char[]{ '1', '1', '1' }, new char[]{ '0', '1', '0' }, new char[]{ '1', '1', '1' } };
        Assert.assertEquals(1, _200Test.solution1.numIslands(_200Test.grid));
        _200Test.grid = new char[][]{ new char[]{ '1', '1', '1' }, new char[]{ '0', '1', '0' }, new char[]{ '1', '1', '1' } };
        Assert.assertEquals(1, _200Test.solution2.numIslands(_200Test.grid));
    }

    @Test
    public void test2() {
        _200Test.grid = new char[][]{ new char[]{ '1', '1', '1', '1', '0' }, new char[]{ '1', '1', '0', '1', '0' }, new char[]{ '1', '1', '0', '0', '0' }, new char[]{ '0', '0', '0', '0', '0' } };
        Assert.assertEquals(1, _200Test.solution1.numIslands(_200Test.grid));
        _200Test.grid = new char[][]{ new char[]{ '1', '1', '1', '1', '0' }, new char[]{ '1', '1', '0', '1', '0' }, new char[]{ '1', '1', '0', '0', '0' }, new char[]{ '0', '0', '0', '0', '0' } };
        Assert.assertEquals(1, _200Test.solution2.numIslands(_200Test.grid));
    }

    @Test
    public void test3() {
        _200Test.grid = new char[][]{ new char[]{ '1', '1', '0', '0', '0' }, new char[]{ '1', '1', '0', '0', '0' }, new char[]{ '0', '0', '1', '0', '0' }, new char[]{ '0', '0', '0', '1', '1' } };
        Assert.assertEquals(3, _200Test.solution1.numIslands(_200Test.grid));
        _200Test.grid = new char[][]{ new char[]{ '1', '1', '0', '0', '0' }, new char[]{ '1', '1', '0', '0', '0' }, new char[]{ '0', '0', '1', '0', '0' }, new char[]{ '0', '0', '0', '1', '1' } };
        Assert.assertEquals(3, _200Test.solution2.numIslands(_200Test.grid));
    }
}

