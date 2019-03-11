package com.querydsl.spatial.jts;


import org.junit.Assert;
import org.junit.Test;


public class JTSGeometryPathTest {
    @Test
    public void convert() {
        JTSGeometryPath<Geometry> geometry = new JTSGeometryPath<Geometry>("geometry");
        Assert.assertEquals(new JTSGeometryCollectionPath<GeometryCollection>("geometry"), geometry.asCollection());
        Assert.assertEquals(new JTSLinearRingPath<LinearRing>("geometry"), geometry.asLinearRing());
        Assert.assertEquals(new JTSLineStringPath<LineString>("geometry"), geometry.asLineString());
        Assert.assertEquals(new JTSMultiLineStringPath<MultiLineString>("geometry"), geometry.asMultiLineString());
        Assert.assertEquals(new JTSMultiPointPath<MultiPoint>("geometry"), geometry.asMultiPoint());
        Assert.assertEquals(new JTSMultiPolygonPath<MultiPolygon>("geometry"), geometry.asMultiPolygon());
        Assert.assertEquals(new JTSPointPath<Point>("geometry"), geometry.asPoint());
        Assert.assertEquals(new JTSPolygonPath<Polygon>("geometry"), geometry.asPolygon());
    }
}

