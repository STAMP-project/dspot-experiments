package org.osmdroid.util;


import junit.framework.Assert;
import org.junit.Test;


/**
 *
 *
 * @since 6.0.0
 * @author Fabrice Fontaine
 */
public class DistanceTest {
    private static final double mDelta = 1.0E-10;

    @Test
    public void test_getSquareDistanceToPoint() {
        final int xA = 100;
        final int yA = 200;
        final int deltaX = 10;
        final int deltaY = 20;
        Assert.assertEquals(0, Distance.getSquaredDistanceToPoint(xA, yA, xA, yA), DistanceTest.mDelta);
        Assert.assertEquals((deltaX * deltaX), Distance.getSquaredDistanceToPoint(xA, yA, (xA + deltaX), yA), DistanceTest.mDelta);
        Assert.assertEquals((deltaY * deltaY), Distance.getSquaredDistanceToPoint(xA, yA, xA, (yA + deltaY)), DistanceTest.mDelta);
        Assert.assertEquals(((deltaX * deltaX) + (deltaY * deltaY)), Distance.getSquaredDistanceToPoint(xA, yA, (xA + deltaX), (yA + deltaY)), DistanceTest.mDelta);
    }

    @Test
    public void test_getSquareDistanceToSegment() {
        final int xA = 100;
        final int yA = 200;
        Assert.assertEquals(0, Distance.getSquaredDistanceToSegment(xA, yA, xA, yA, xA, yA), DistanceTest.mDelta);
        Assert.assertEquals((10 * 10), Distance.getSquaredDistanceToSegment(xA, yA, (xA + 10), yA, (xA + 10), yA), DistanceTest.mDelta);
        Assert.assertEquals((20 * 20), Distance.getSquaredDistanceToSegment(xA, yA, xA, (yA + 20), xA, (yA + 20)), DistanceTest.mDelta);
        Assert.assertEquals((20 * 20), Distance.getSquaredDistanceToSegment(xA, (yA + 20), xA, yA, (xA + 100), yA), DistanceTest.mDelta);
        Assert.assertEquals(((10 * 10) + (30 * 30)), Distance.getSquaredDistanceToSegment((xA - 10), (yA - 30), xA, yA, (xA + 100), yA), DistanceTest.mDelta);
        Assert.assertEquals(((100 * 100) + (70 * 70)), Distance.getSquaredDistanceToSegment((xA + 200), (yA - 70), xA, yA, (xA + 100), yA), DistanceTest.mDelta);
        Assert.assertEquals((7000 * 7000), Distance.getSquaredDistanceToSegment((xA + 200), (yA - 7000), xA, yA, (xA + 200), yA), DistanceTest.mDelta);
        Assert.assertEquals((7000 * 7000), Distance.getSquaredDistanceToSegment((xA + 200), (yA - 7000), xA, yA, (xA + 1000), yA), DistanceTest.mDelta);
    }

    @Test
    public void test_getProjectionFactorToLine() {
        final int xA = 100;
        final int yA = 200;
        Assert.assertEquals(0, Distance.getProjectionFactorToLine(xA, yA, xA, yA, xA, yA), DistanceTest.mDelta);
        Assert.assertEquals(0, Distance.getProjectionFactorToLine(xA, yA, (xA + 10), yA, (xA + 10), yA), DistanceTest.mDelta);
        Assert.assertEquals(0, Distance.getProjectionFactorToLine(xA, yA, xA, (yA + 20), xA, (yA + 20)), DistanceTest.mDelta);
        Assert.assertEquals(0, Distance.getProjectionFactorToLine(xA, (yA + 20), xA, yA, (xA + 100), yA), DistanceTest.mDelta);
        // < 0
        Assert.assertEquals(((-10.0) / 100), Distance.getProjectionFactorToLine((xA - 10), (yA - 30), xA, yA, (xA + 100), yA), DistanceTest.mDelta);
        // > 1
        Assert.assertEquals(2, Distance.getProjectionFactorToLine((xA + 200), (yA - 70), xA, yA, (xA + 100), yA), DistanceTest.mDelta);
        // 1
        Assert.assertEquals(1, Distance.getProjectionFactorToLine((xA + 200), (yA - 7000), xA, yA, (xA + 200), yA), DistanceTest.mDelta);
        // ]0,1[
        Assert.assertEquals(0.2, Distance.getProjectionFactorToLine((xA + 200), (yA - 7000), xA, yA, (xA + 1000), yA), DistanceTest.mDelta);
    }

    @Test
    public void test_getSquareDistanceToLine() {
        final int xA = 100;
        final int yA = 200;
        final int deltaX = 10;
        final int deltaY = 20;
        Assert.assertEquals(0, Distance.getSquaredDistanceToLine(xA, yA, xA, yA, xA, yA), DistanceTest.mDelta);
        Assert.assertEquals((deltaX * deltaX), Distance.getSquaredDistanceToLine(xA, yA, (xA + deltaX), yA, (xA + deltaX), yA), DistanceTest.mDelta);
        Assert.assertEquals((deltaY * deltaY), Distance.getSquaredDistanceToLine(xA, yA, xA, (yA + deltaY), xA, (yA + deltaY)), DistanceTest.mDelta);
        Assert.assertEquals((20 * 20), Distance.getSquaredDistanceToLine(xA, (yA + 20), xA, yA, (xA + 100), yA), DistanceTest.mDelta);
        Assert.assertEquals((30 * 30), Distance.getSquaredDistanceToLine((xA - 10), (yA - 30), xA, yA, (xA + 100), yA), DistanceTest.mDelta);
        Assert.assertEquals((70 * 70), Distance.getSquaredDistanceToLine((xA + 200), (yA - 70), xA, yA, (xA + 100), yA), DistanceTest.mDelta);
        Assert.assertEquals((7000 * 7000), Distance.getSquaredDistanceToLine((xA + 200), (yA - 7000), xA, yA, (xA + 200), yA), DistanceTest.mDelta);
        Assert.assertEquals((7000 * 7000), Distance.getSquaredDistanceToLine((xA + 200), (yA - 7000), xA, yA, (xA + 1000), yA), DistanceTest.mDelta);
    }
}

