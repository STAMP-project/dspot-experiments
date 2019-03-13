package com.fishercoder;


import _294.Solution1;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by stevesun on 5/29/17.
 */
public class _294Test {
    private static Solution1 solution1;

    @Test
    public void test1() {
        Assert.assertEquals(true, _294Test.solution1.canWin("++++"));
    }
}

