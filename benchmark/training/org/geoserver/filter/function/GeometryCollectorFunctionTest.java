/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.filter.function;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.geoserver.test.GeoServerSystemTestSupport;
import org.geotools.factory.CommonFactoryFinder;
import org.junit.Assert;
import org.junit.Test;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.io.WKTReader;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.expression.Function;


public class GeometryCollectorFunctionTest extends GeoServerSystemTestSupport {
    static final FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2();

    @Test
    public void testCollectNull() {
        Function function = GeometryCollectorFunctionTest.ff.function("collectGeometries", GeometryCollectorFunctionTest.ff.literal(null));
        GeometryCollection result = ((GeometryCollection) (function.evaluate(null)));
        Assert.assertNotNull(result);
        Assert.assertEquals(0, result.getNumGeometries());
    }

    @Test
    public void testCollectNone() {
        Function function = GeometryCollectorFunctionTest.ff.function("collectGeometries", GeometryCollectorFunctionTest.ff.literal(Collections.emptyList()));
        GeometryCollection result = ((GeometryCollection) (function.evaluate(null)));
        Assert.assertNotNull(result);
        Assert.assertEquals(0, result.getNumGeometries());
    }

    @Test
    public void testTwo() throws Exception {
        WKTReader reader = new WKTReader();
        List<Geometry> geometries = new ArrayList<Geometry>();
        final Geometry p0 = reader.read("POINT(0 0)");
        geometries.add(p0);
        final Geometry p1 = reader.read("POINT(1 1)");
        geometries.add(p1);
        Function function = GeometryCollectorFunctionTest.ff.function("collectGeometries", GeometryCollectorFunctionTest.ff.literal(geometries));
        GeometryCollection result = ((GeometryCollection) (function.evaluate(null)));
        Assert.assertEquals(2, result.getNumGeometries());
        Assert.assertSame(p0, result.getGeometryN(0));
        Assert.assertSame(p1, result.getGeometryN(1));
    }
}

