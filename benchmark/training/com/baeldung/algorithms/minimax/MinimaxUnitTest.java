package com.baeldung.algorithms.minimax;


import org.junit.Assert;
import org.junit.Test;


public class MinimaxUnitTest {
    private Tree gameTree;

    private MiniMax miniMax;

    @Test
    public void givenMiniMax_whenConstructTree_thenNotNullTree() {
        Assert.assertNull(gameTree);
        miniMax.constructTree(6);
        gameTree = miniMax.getTree();
        Assert.assertNotNull(gameTree);
    }

    @Test
    public void givenMiniMax_whenCheckWin_thenComputeOptimal() {
        miniMax.constructTree(6);
        boolean result = miniMax.checkWin();
        Assert.assertTrue(result);
        miniMax.constructTree(8);
        result = miniMax.checkWin();
        Assert.assertFalse(result);
    }
}

