package org.osmdroid.util;


import junit.framework.Assert;
import org.junit.Test;


/**
 *
 *
 * @since 6.1.0
 * @author Fabrice Fontaine
 */
public class MyMathTest {
    private static final double DELTA = 1.0E-20;

    @Test
    public void testGetAngleDifference() {
        Assert.assertEquals(20, MyMath.getAngleDifference(10, 30, null), MyMathTest.DELTA);
        Assert.assertEquals(20, MyMath.getAngleDifference(10, 30, Boolean.TRUE), MyMathTest.DELTA);
        Assert.assertEquals((-340), MyMath.getAngleDifference(10, 30, Boolean.FALSE), MyMathTest.DELTA);
        Assert.assertEquals((-20), MyMath.getAngleDifference(30, 10, null), MyMathTest.DELTA);
        Assert.assertEquals(340, MyMath.getAngleDifference(30, 10, Boolean.TRUE), MyMathTest.DELTA);
        Assert.assertEquals((-20), MyMath.getAngleDifference(30, 10, Boolean.FALSE), MyMathTest.DELTA);
        Assert.assertEquals(2, MyMath.getAngleDifference(179, (-179), null), MyMathTest.DELTA);
        Assert.assertEquals(2, MyMath.getAngleDifference(179, (-179), Boolean.TRUE), MyMathTest.DELTA);
        Assert.assertEquals((-358), MyMath.getAngleDifference(179, (-179), Boolean.FALSE), MyMathTest.DELTA);
        Assert.assertEquals(2, MyMath.getAngleDifference(359, 1, null), MyMathTest.DELTA);
        Assert.assertEquals(2, MyMath.getAngleDifference(359, 1, Boolean.TRUE), MyMathTest.DELTA);
        Assert.assertEquals((-358), MyMath.getAngleDifference(359, 1, Boolean.FALSE), MyMathTest.DELTA);
    }
}

