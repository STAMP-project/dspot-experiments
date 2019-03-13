/**
 * (c) 2015 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wfs;


import java.util.List;
import org.apache.commons.io.IOUtils;
import org.geotools.geometry.jts.CircularArc;
import org.geotools.geometry.jts.CircularRing;
import org.geotools.geometry.jts.CircularString;
import org.geotools.geometry.jts.CompoundCurvedGeometry;
import org.geotools.geometry.jts.CurvedGeometries;
import org.geotools.geometry.jts.SingleCurvedGeometry;
import org.junit.Assert;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.Polygon;
import org.opengis.feature.simple.SimpleFeature;
import org.w3c.dom.Document;


/**
 * Base class for WFS-T curve test, it expects to find the test requests in the same package as its
 * implementing subclasses
 *
 * @author Andrea Aime - GeoSolutions
 */
public abstract class AbstractTransactionCurveTest extends WFSCurvesTestSupport {
    @Test
    public void testInsertArc() throws Exception {
        String xml = IOUtils.toString(getClass().getResourceAsStream("insertArc.xml"));
        Document dom = postAsDOM("wfs", xml);
        // print(dom);
        checkSuccesfulTransaction(dom, 1, 0, 0);
        SimpleFeature first = getSingleFeature(CURVELINES, "Arc far away");
        Geometry g = ((Geometry) (first.getDefaultGeometry()));
        Assert.assertNotNull(g);
        Assert.assertTrue((g instanceof SingleCurvedGeometry));
        SingleCurvedGeometry<?> curved = ((SingleCurvedGeometry<?>) (g));
        double[] cp = curved.getControlPoints();
        Assert.assertArrayEquals(new double[]{ 10, 15, 15, 20, 20, 15 }, cp, 0.0);
    }

    @Test
    public void testUpdateCompoundCurve() throws Exception {
        String xml = IOUtils.toString(getClass().getResourceAsStream("updateCompoundCurve.xml"));
        Document dom = postAsDOM("wfs", xml);
        // print(dom);
        checkSuccesfulTransaction(dom, 0, 1, 0);
        SimpleFeature first = getSingleFeature(CURVELINES, "Compound");
        Geometry g = ((Geometry) (first.getDefaultGeometry()));
        Assert.assertNotNull(g);
        Assert.assertTrue((g instanceof CompoundCurvedGeometry<?>));
        CompoundCurvedGeometry<?> compound = ((CompoundCurvedGeometry<?>) (g));
        List<LineString> components = compound.getComponents();
        Assert.assertEquals(3, components.size());
        LineString ls1 = components.get(0);
        Assert.assertEquals(2, ls1.getNumPoints());
        Assert.assertEquals(new Coordinate(10, 45), ls1.getCoordinateN(0));
        Assert.assertEquals(new Coordinate(20, 45), ls1.getCoordinateN(1));
        CircularString cs = ((CircularString) (components.get(1)));
        Assert.assertArrayEquals(new double[]{ 20.0, 45.0, 23.0, 48.0, 20.0, 51.0 }, cs.getControlPoints(), 0.0);
        LineString ls2 = components.get(2);
        Assert.assertEquals(2, ls2.getNumPoints());
        Assert.assertEquals(new Coordinate(20, 51), ls2.getCoordinateN(0));
        Assert.assertEquals(new Coordinate(10, 51), ls2.getCoordinateN(1));
    }

    @Test
    public void testInsertCurvePolygon() throws Exception {
        String xml = IOUtils.toString(getClass().getResourceAsStream("insertCurvePolygon.xml"));
        Document dom = postAsDOM("wfs", xml);
        // print(dom);
        checkSuccesfulTransaction(dom, 1, 0, 0);
        SimpleFeature first = getSingleFeature(CURVEPOLYGONS, "Circle2");
        Geometry g = ((Geometry) (first.getDefaultGeometry()));
        Assert.assertNotNull(g);
        Assert.assertTrue((g instanceof Polygon));
        Polygon p = ((Polygon) (g));
        Assert.assertEquals(0, p.getNumInteriorRing());
        // exterior ring checks
        Assert.assertTrue(((p.getExteriorRing()) instanceof CircularRing));
        CircularRing shell = ((CircularRing) (p.getExteriorRing()));
        Assert.assertTrue(CurvedGeometries.isCircle(shell));
        CircularArc arc = shell.getArcN(0);
        Assert.assertEquals(5, arc.getRadius(), 0.0);
        Assert.assertEquals(new Coordinate(15, 50), arc.getCenter());
    }

    @Test
    public void testInsertMultiCurve() throws Exception {
        String xml = IOUtils.toString(getClass().getResourceAsStream("insertMultiCurve.xml"));
        Document dom = postAsDOM("wfs", xml);
        // print(dom);
        checkSuccesfulTransaction(dom, 1, 0, 0);
        SimpleFeature first = getSingleFeature(CURVEMULTILINES, "MNew");
        Geometry g = ((Geometry) (first.getDefaultGeometry()));
        Assert.assertTrue((g instanceof MultiLineString));
        MultiLineString mc = ((MultiLineString) (g));
        LineString ls = ((LineString) (mc.getGeometryN(0)));
        Assert.assertEquals(2, ls.getNumPoints());
        Assert.assertEquals(new Coordinate(0, 0), ls.getCoordinateN(0));
        Assert.assertEquals(new Coordinate(5, 5), ls.getCoordinateN(1));
        CircularString cs = ((CircularString) (mc.getGeometryN(1)));
        Assert.assertArrayEquals(new double[]{ 4, 0, 4, 4, 8, 4 }, cs.getControlPoints(), 0.0);
    }
}

