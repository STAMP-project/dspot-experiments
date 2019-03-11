/**
 * (c) 2014 - 2015 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wfs;


import org.geoserver.catalog.FeatureTypeInfo;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import si.uom.SI;


public class GetFeatureCurvesTest extends WFSCurvesTestSupport {
    @Test
    public void testLinearizeWFS10() throws Exception {
        Document dom = getAsDOM(("wfs?service=wfs&version=1.0&request=GetFeature&typeName=" + (getLayerId(CURVELINES))));
        // print(dom);
        Assert.assertEquals("1", xpath.evaluate("count(//gml:featureMember/cite:curvelines[@fid='cp.1']/cite:geom/gml:LineString)", dom));
        Assert.assertEquals("1", xpath.evaluate("count(//gml:featureMember/cite:curvelines[@fid='cp.2']/cite:geom/gml:LineString)", dom));
        Assert.assertEquals("1", xpath.evaluate("count(//gml:featureMember/cite:curvelines[@fid='cp.3']/cite:geom/gml:LineString)", dom));
        // compute number of coordinates
        String coords = xpath.evaluate("//gml:featureMember/cite:curvelines[@fid='cp.2']/cite:geom/gml:LineString/gml:coordinates", dom);
        int coordCountDefault = coords.split("\\s+").length;
        // now alter the feature type and set a linearization tolerance
        FeatureTypeInfo ft = getCatalog().getFeatureTypeByName(getLayerId(CURVELINES));
        ft.setCircularArcPresent(true);
        ft.setLinearizationTolerance(new org.geotools.measure.Measure(1, SI.METRE));
        getCatalog().save(ft);
        dom = getAsDOM(("wfs?service=wfs&version=1.0&request=GetFeature&typeName=" + (getLayerId(CURVELINES))));
        // print(dom);
        int coordCount100m = countCoordinates(dom, xpath, "//gml:featureMember/cite:curvelines[@fid='cp.2']/cite:geom/gml:LineString/gml:coordinates");
        Assert.assertTrue((coordCount100m > coordCountDefault));
    }

    @Test
    public void testCurveLineWFS11() throws Exception {
        Document dom = getAsDOM(("wfs?service=wfs&version=1.1&request=GetFeature&typeName=" + (getLayerId(CURVELINES))));
        // print(dom);
        // check the compound curve
        Assert.assertEquals("1", xpath.evaluate("count(//cite:curvelines[@gml:id='cp.1']/cite:geom/gml:Curve/gml:segments/gml:ArcString)", dom));
        Assert.assertEquals(10, countCoordinates(dom, xpath, "//cite:curvelines[@gml:id='cp.1']/cite:geom/gml:Curve/gml:segments/gml:ArcString/gml:posList"));
        Assert.assertEquals("1", xpath.evaluate("count(//cite:curvelines[@gml:id='cp.1']/cite:geom/gml:Curve/gml:segments/gml:LineStringSegment)", dom));
        Assert.assertEquals(8, countCoordinates(dom, xpath, "//cite:curvelines[@gml:id='cp.1']/cite:geom/gml:Curve/gml:segments/gml:LineStringSegment/gml:posList"));
        // check the circle
        Assert.assertEquals("1", xpath.evaluate("count(//cite:curvelines[@gml:id='cp.2']/cite:geom/gml:Curve/gml:segments/gml:ArcString)", dom));
        Assert.assertEquals(10, countCoordinates(dom, xpath, "//cite:curvelines[@gml:id='cp.2']/cite:geom/gml:Curve/gml:segments/gml:ArcString/gml:posList"));
        // check the wave
        Assert.assertEquals("1", xpath.evaluate("count(//cite:curvelines[@gml:id='cp.3']/cite:geom/gml:Curve/gml:segments/gml:ArcString)", dom));
        Assert.assertEquals(10, countCoordinates(dom, xpath, "//cite:curvelines[@gml:id='cp.3']/cite:geom/gml:Curve/gml:segments/gml:ArcString/gml:posList"));
    }

    @Test
    public void testCurveMultiLineWFS11() throws Exception {
        Document dom = getAsDOM((("wfs?service=wfs&version=1.1&request=GetFeature&typeName=" + (getLayerId(CURVEMULTILINES))) + "&featureid=cp.1"));
        // print(dom);
        // check the compound curve
        Assert.assertEquals("1", xpath.evaluate("count(//cite:curvemultilines[@gml:id='cp.1']/cite:geom/gml:MultiCurve/gml:curveMember/gml:LineString)", dom));
        Assert.assertEquals("1", xpath.evaluate("count(//cite:curvemultilines[@gml:id='cp.1']/cite:geom/gml:MultiCurve/gml:curveMember/gml:Curve)", dom));
        Assert.assertEquals("1", xpath.evaluate("count(//cite:curvemultilines[@gml:id='cp.1']/cite:geom/gml:MultiCurve/gml:curveMember/gml:Curve/gml:segments/gml:ArcString)", dom));
        Assert.assertEquals("1", xpath.evaluate("count(//cite:curvemultilines[@gml:id='cp.1']/cite:geom/gml:MultiCurve/gml:curveMember/gml:Curve/gml:segments/gml:ArcString)", dom));
    }

    @Test
    public void testCurvePolygons() throws Exception {
        Document dom = getAsDOM((("wfs?service=wfs&version=1.1&request=GetFeature&typeName=" + (getLayerId(CURVEPOLYGONS))) + "&featureid=cp.1"));
        // print(dom);
        // check the compound curve
        Assert.assertEquals("1", xpath.evaluate("count(//cite:curvepolygons[@gml:id='cp.1']/cite:geom/gml:Polygon/gml:exterior/gml:Ring/gml:curveMember/gml:Curve/gml:segments/gml:ArcString)", dom));
        Assert.assertEquals("1", xpath.evaluate("count(//cite:curvepolygons[@gml:id='cp.1']/cite:geom/gml:Polygon/gml:exterior/gml:Ring/gml:curveMember/gml:LineString)", dom));
        Assert.assertEquals("1", xpath.evaluate("count(//cite:curvepolygons[@gml:id='cp.1']/cite:geom/gml:Polygon/gml:interior/gml:Ring/gml:curveMember/gml:Curve/gml:segments/gml:ArcString)", dom));
    }
}

