package com.fishercoder;


import _348.Solution1.TicTacToe;
import org.junit.Assert;
import org.junit.Test;


public class _348Test {
    @Test
    public void test1() {
        int n = 3;
        TicTacToe ticTacToe = new TicTacToe(n);
        Assert.assertEquals(0, ticTacToe.move(0, 0, 1));
        Assert.assertEquals(0, ticTacToe.move(0, 2, 2));
        Assert.assertEquals(0, ticTacToe.move(2, 2, 1));
        Assert.assertEquals(0, ticTacToe.move(1, 1, 2));
        Assert.assertEquals(0, ticTacToe.move(2, 0, 1));
        Assert.assertEquals(0, ticTacToe.move(1, 0, 2));
        Assert.assertEquals(1, ticTacToe.move(2, 1, 1));
    }

    @Test
    public void test2() {
        int n = 3;
        TicTacToe ticTacToe = new TicTacToe(n);
        Assert.assertEquals(0, ticTacToe.move(0, 0, 1));
        Assert.assertEquals(0, ticTacToe.move(1, 1, 1));
        Assert.assertEquals(1, ticTacToe.move(2, 2, 1));
    }

    @Test
    public void test3() {
        int n = 3;
        TicTacToe ticTacToe = new TicTacToe(n);
        Assert.assertEquals(0, ticTacToe.move(0, 2, 2));
        Assert.assertEquals(0, ticTacToe.move(1, 1, 2));
        Assert.assertEquals(2, ticTacToe.move(2, 0, 2));
    }
}

