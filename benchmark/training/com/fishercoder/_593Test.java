package com.fishercoder;


import com.fishercoder.solutions._593;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by fishercoder on 5/22/17.
 */
public class _593Test {
    private static _593 test;

    private static int[] p1;

    private static int[] p2;

    private static int[] p3;

    private static int[] p4;

    @Test
    public void test1() {
        _593Test.p1 = new int[]{ 0, 0 };
        _593Test.p2 = new int[]{ 1, 1 };
        _593Test.p3 = new int[]{ 1, 0 };
        _593Test.p4 = new int[]{ 0, 1 };
        Assert.assertEquals(true, _593Test.test.validSquare(_593Test.p1, _593Test.p2, _593Test.p3, _593Test.p4));
    }

    @Test
    public void test2() {
        _593Test.p1 = new int[]{ 1, 1 };
        _593Test.p2 = new int[]{ 5, 3 };
        _593Test.p3 = new int[]{ 3, 5 };
        _593Test.p4 = new int[]{ 7, 7 };
        Assert.assertEquals(false, _593Test.test.validSquare(_593Test.p1, _593Test.p2, _593Test.p3, _593Test.p4));
    }

    @Test
    public void test3() {
        _593Test.p1 = new int[]{ 0, 0 };
        _593Test.p2 = new int[]{ 0, 0 };
        _593Test.p3 = new int[]{ 0, 0 };
        _593Test.p4 = new int[]{ 0, 0 };
        System.out.println(_593Test.test.noDuplicate(_593Test.p1, _593Test.p2, _593Test.p3, _593Test.p4));
        Assert.assertEquals(false, _593Test.test.validSquare(_593Test.p1, _593Test.p2, _593Test.p3, _593Test.p4));
    }
}

