package com.querydsl.spatial;


import org.junit.Assert;
import org.junit.Test;


public class GeometryPathTest {
    @Test
    public void convert() {
        GeometryPath<Geometry> geometry = new GeometryPath<Geometry>("geometry");
        Assert.assertEquals(new GeometryCollectionPath<GeometryCollection>("geometry"), geometry.asCollection());
        Assert.assertEquals(new LinearRingPath<LinearRing>("geometry"), geometry.asLinearRing());
        Assert.assertEquals(new LineStringPath<LineString>("geometry"), geometry.asLineString());
        Assert.assertEquals(new MultiLineStringPath<MultiLineString>("geometry"), geometry.asMultiLineString());
        Assert.assertEquals(new MultiPointPath<MultiPoint>("geometry"), geometry.asMultiPoint());
        Assert.assertEquals(new MultiPolygonPath<MultiPolygon>("geometry"), geometry.asMultiPolygon());
        Assert.assertEquals(new PointPath<Point>("geometry"), geometry.asPoint());
        Assert.assertEquals(new PolygonPath<Polygon>("geometry"), geometry.asPolygon());
        Assert.assertEquals(new PolyhedralSurfacePath<PolyHedralSurface>("geometry"), geometry.asPolyHedralSurface());
    }
}

