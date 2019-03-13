package com.querydsl.sql.spatial;


import java.util.List;
import oracle.spatial.geometry.JGeometry;
import org.geolatte.geom.codec.Wkt;
import org.junit.Assert;
import org.junit.Test;


public class JGeometryConverterTest extends AbstractConverterTest {
    @Test
    public void roundTrip() {
        List<Geometry> geometries = getGeometries();
        for (Geometry geometry : geometries) {
            if (geometry instanceof MultiPolygon) {
                continue;
            }
            if (geometry instanceof GeometryCollection) {
                continue;
            }
            System.err.println(Wkt.toWkt(geometry));
            JGeometry converted = JGeometryConverter.convert(geometry);
            Geometry back = JGeometryConverter.convert(converted);
            Assert.assertEquals(geometry, back);
        }
    }

    @Test
    public void polygon() {
        Polygon polygon = ((org.geolatte.geom.Polygon) (Wkt.fromWkt(("POLYGON (" + ("(30 10, 40 40, 20 40, 10 20, 30 10), " + "(20 30, 35 35, 30 20, 20 30))")))));
        JGeometry geo = JGeometryConverter.convert(polygon);
        double[] extRing = new double[]{ 30, 10, 40, 40, 20, 40, 10, 20, 30, 10 };
        double[] intRing = new double[]{ 20, 30, 35, 35, 30, 20, 20, 30 };
        JGeometry geo2 = JGeometry.createLinearPolygon(new Object[]{ extRing, intRing }, polygon.getCoordinateDimension(), polygon.getSRID());
        Assert.assertEquals(geo2, geo);
    }

    @Test
    public void multiLineString() {
        MultiLineString multiLineString = ((org.geolatte.geom.MultiLineString) (Wkt.fromWkt(("MULTILINESTRING (" + ("(30 10, 40 40, 20 40, 10 20, 30 10), " + "(20 30, 35 35, 30 20, 20 30))")))));
        JGeometry geo = JGeometryConverter.convert(multiLineString);
        double[] line1 = new double[]{ 30, 10, 40, 40, 20, 40, 10, 20, 30, 10 };
        double[] line2 = new double[]{ 20, 30, 35, 35, 30, 20, 20, 30 };
        JGeometry geo2 = JGeometry.createLinearMultiLineString(new Object[]{ line1, line2 }, multiLineString.getCoordinateDimension(), multiLineString.getSRID());
        // System.err.println(Arrays.toString(geo.getElemInfo()));
        // System.err.println(Arrays.toString(geo.getOrdinatesArray()));
        // System.err.println(Arrays.toString(geo2.getElemInfo()));
        // System.err.println(Arrays.toString(geo2.getOrdinatesArray()));
        Assert.assertEquals(geo2, geo);
    }
}

