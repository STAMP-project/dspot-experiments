package org.osmdroid.util;


import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.osmdroid.api.IGeoPoint;


/**
 * Created by alex on 9/11/16.
 */
public class BoundBoxTest {
    private static final TileSystem tileSystem = new TileSystemWebMercator();

    @Test
    public void testBoundingBox() throws Exception {
        List<IGeoPoint> partialPolyLine = new ArrayList<>();
        partialPolyLine.add(new GeoPoint(1.0, 1.0));
        partialPolyLine.add(new GeoPoint(1.0, (-1.0)));
        partialPolyLine.add(new GeoPoint((-1.0), 1.0));
        partialPolyLine.add(new GeoPoint((-1.0), (-1.0)));
        partialPolyLine.add(new GeoPoint(0.0, 0.0));
        BoundingBox fromGeoPoints = BoundingBox.fromGeoPoints(partialPolyLine);
        Assert.assertEquals(fromGeoPoints.getCenter().getLatitude(), 0.0, 1.0E-6);
        Assert.assertEquals(fromGeoPoints.getCenter().getLongitude(), 0.0, 1.0E-6);
        Assert.assertEquals(fromGeoPoints.getLatNorth(), 1.0, 1.0E-6);
        Assert.assertEquals(fromGeoPoints.getLatSouth(), (-1.0), 1.0E-6);
        Assert.assertEquals(fromGeoPoints.getLonEast(), 1.0, 1.0E-6);
        Assert.assertEquals(fromGeoPoints.getLonWest(), (-1.0), 1.0E-6);
    }

    @Test
    public void testBoundingBoxMax() throws Exception {
        List<IGeoPoint> partialPolyLine = new ArrayList<>();
        partialPolyLine.add(new GeoPoint(BoundBoxTest.tileSystem.getMaxLatitude(), 180.0));
        partialPolyLine.add(new GeoPoint(BoundBoxTest.tileSystem.getMinLatitude(), (-180.0)));
        BoundingBox fromGeoPoints = BoundingBox.fromGeoPoints(partialPolyLine);
        Assert.assertEquals(fromGeoPoints.getCenter().getLatitude(), 0.0, 1.0E-6);
        Assert.assertEquals(fromGeoPoints.getCenter().getLongitude(), 0.0, 1.0E-6);
        Assert.assertEquals(fromGeoPoints.getLatNorth(), BoundBoxTest.tileSystem.getMaxLatitude(), 1.0E-6);
        Assert.assertEquals(fromGeoPoints.getLatSouth(), BoundBoxTest.tileSystem.getMinLatitude(), 1.0E-6);
        Assert.assertEquals(fromGeoPoints.getLonEast(), 180.0, 1.0E-6);
        Assert.assertEquals(fromGeoPoints.getLonWest(), (-180.0), 1.0E-6);
    }

    @Test
    public void testBoundingBoxAllNegs() throws Exception {
        List<IGeoPoint> partialPolyLine = new ArrayList<>();
        partialPolyLine.add(new GeoPoint((-46.0), (-46.0)));
        partialPolyLine.add(new GeoPoint((-45.0), (-45.0)));
        BoundingBox fromGeoPoints = BoundingBox.fromGeoPoints(partialPolyLine);
        Assert.assertEquals(fromGeoPoints.getCenter().getLatitude(), (-45.5), 1.0E-6);
        Assert.assertEquals(fromGeoPoints.getCenter().getLongitude(), (-45.5), 1.0E-6);
        Assert.assertEquals(fromGeoPoints.getLatNorth(), (-45.0), 1.0E-6);
        Assert.assertEquals(fromGeoPoints.getLatSouth(), (-46.0), 1.0E-6);
        Assert.assertEquals(fromGeoPoints.getLonEast(), (-45.0), 1.0E-6);
        Assert.assertEquals(fromGeoPoints.getLonWest(), (-46.0), 1.0E-6);
    }
}

