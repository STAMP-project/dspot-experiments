package org.osmdroid.util;


import junit.framework.Assert;
import org.junit.Test;
import org.osmdroid.views.MapView;


/**
 *
 *
 * @since 6.0.0
 * @author Fabrice Fontaine
 * @author Andreas Schildbach
 */
public class BoundingBoxTest {
    private static final double TOLERANCE = 1.0E-5;

    @Test
    public void testGetCenterLongitude() {
        Assert.assertEquals(1.5, BoundingBox.getCenterLongitude(1, 2), BoundingBoxTest.TOLERANCE);
        Assert.assertEquals((-178.5), BoundingBox.getCenterLongitude(2, 1), BoundingBoxTest.TOLERANCE);
    }

    @Test
    public void getSpansWithoutDateLine() {
        BoundingBox bb = new BoundingBox(10, 10, (-10), (-10));
        Assert.assertEquals(20, bb.getLongitudeSpanWithDateLine(), BoundingBoxTest.TOLERANCE);
        Assert.assertEquals(20, bb.getLongitudeSpan(), BoundingBoxTest.TOLERANCE);
        Assert.assertEquals(20, bb.getLatitudeSpan(), BoundingBoxTest.TOLERANCE);
    }

    @Test
    public void getSpansWithDateLine() {
        BoundingBox bb = new BoundingBox(10, (-170), (-10), 170);
        Assert.assertEquals(20, bb.getLongitudeSpanWithDateLine(), BoundingBoxTest.TOLERANCE);
        Assert.assertEquals(20, bb.getLatitudeSpan(), BoundingBoxTest.TOLERANCE);
        bb = new BoundingBox(10, (-10), (-10), 10);
        Assert.assertEquals(340, bb.getLongitudeSpanWithDateLine(), BoundingBoxTest.TOLERANCE);
    }

    @Test
    public void increaseByScale() {
        BoundingBox bb = new BoundingBox(10, 20, 0, 0).increaseByScale(1.2F);
        Assert.assertEquals(11, bb.getLatNorth(), BoundingBoxTest.TOLERANCE);
        Assert.assertEquals(22, bb.getLonEast(), BoundingBoxTest.TOLERANCE);
        Assert.assertEquals((-1), bb.getLatSouth(), BoundingBoxTest.TOLERANCE);
        Assert.assertEquals((-2), bb.getLonWest(), BoundingBoxTest.TOLERANCE);
    }

    @Test
    public void increaseByScale_onDateLine() {
        BoundingBox bb = new BoundingBox(10, (-170), (-10), 170).increaseByScale(1.2F);
        Assert.assertEquals(12, bb.getLatNorth(), BoundingBoxTest.TOLERANCE);
        Assert.assertEquals((-168), bb.getLonEast(), BoundingBoxTest.TOLERANCE);
        Assert.assertEquals((-12), bb.getLatSouth(), BoundingBoxTest.TOLERANCE);
        Assert.assertEquals(168, bb.getLonWest(), BoundingBoxTest.TOLERANCE);
    }

    @Test
    public void increaseByScale_clipNorth() {
        BoundingBox bb = new BoundingBox(80, 20, 0, (-20)).increaseByScale(1.2F);
        Assert.assertEquals(MapView.getTileSystem().getMaxLatitude(), bb.getLatNorth(), BoundingBoxTest.TOLERANCE);
        Assert.assertEquals((-8), bb.getLatSouth(), BoundingBoxTest.TOLERANCE);
    }

    @Test
    public void increaseByScale_clipSouth() {
        BoundingBox bb = new BoundingBox(0, 20, (-80), (-20)).increaseByScale(1.2F);
        Assert.assertEquals(8, bb.getLatNorth(), BoundingBoxTest.TOLERANCE);
        Assert.assertEquals(MapView.getTileSystem().getMinLatitude(), bb.getLatSouth(), BoundingBoxTest.TOLERANCE);
    }

    @Test
    public void increaseByScale_wrapEast() {
        BoundingBox bb = new BoundingBox(20, 175, (-20), 75).increaseByScale(1.2F);
        Assert.assertEquals((-175), bb.getLonEast(), BoundingBoxTest.TOLERANCE);
        Assert.assertEquals(65, bb.getLonWest(), BoundingBoxTest.TOLERANCE);
    }

    @Test
    public void increaseByScale_wrapWest() {
        BoundingBox bb = new BoundingBox(20, (-75), (-20), (-175)).increaseByScale(1.2F);
        Assert.assertEquals((-65), bb.getLonEast(), BoundingBoxTest.TOLERANCE);
        Assert.assertEquals(175, bb.getLonWest(), BoundingBoxTest.TOLERANCE);
    }
}

