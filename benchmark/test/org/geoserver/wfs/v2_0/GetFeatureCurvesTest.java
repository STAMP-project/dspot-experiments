/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wfs.v2_0;


import javax.xml.namespace.QName;
import org.custommonkey.xmlunit.XpathEngine;
import org.geoserver.data.test.MockData;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;


public class GetFeatureCurvesTest extends WFS20TestSupport {
    QName CURVELINES = new QName(MockData.CITE_URI, "curvelines", MockData.CITE_PREFIX);

    QName CURVEMULTILINES = new QName(MockData.CITE_URI, "curvemultilines", MockData.CITE_PREFIX);

    QName CURVEPOLYGONS = new QName(MockData.CITE_URI, "curvepolygons", MockData.CITE_PREFIX);

    XpathEngine xpath;

    @Test
    public void testCurveLine() throws Exception {
        Document dom = getAsDOM(("wfs?service=wfs&version=2.0&request=GetFeature&typeName=" + (getLayerId(CURVELINES))));
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
    public void testCurveMultiLine() throws Exception {
        Document dom = getAsDOM((("wfs?service=wfs&version=2.0&request=GetFeature&typeName=" + (getLayerId(CURVEMULTILINES))) + "&featureid=cp.1"));
        // print(dom);
        // check the compound curve
        Assert.assertEquals("1", xpath.evaluate("count(//cite:curvemultilines[@gml:id='cp.1']/cite:geom/gml:MultiCurve/gml:curveMember/gml:LineString)", dom));
        Assert.assertEquals("1", xpath.evaluate("count(//cite:curvemultilines[@gml:id='cp.1']/cite:geom/gml:MultiCurve/gml:curveMember/gml:Curve)", dom));
        Assert.assertEquals("1", xpath.evaluate("count(//cite:curvemultilines[@gml:id='cp.1']/cite:geom/gml:MultiCurve/gml:curveMember/gml:Curve/gml:segments/gml:ArcString)", dom));
        Assert.assertEquals("1", xpath.evaluate("count(//cite:curvemultilines[@gml:id='cp.1']/cite:geom/gml:MultiCurve/gml:curveMember/gml:Curve/gml:segments/gml:ArcString)", dom));
    }

    @Test
    public void testCurvePolygons() throws Exception {
        Document dom = getAsDOM((("wfs?service=wfs&version=2.0&request=GetFeature&typeName=" + (getLayerId(CURVEPOLYGONS))) + "&featureid=cp.1"));
        // print(dom);
        // check the compound curve
        xpath.evaluate("count(//cite:curvepolygons[@gml:id='cp.1']/cite:geom/gml:Polygon/gml:exterior/gml:Ring/gml:curveMember/gml:Curve/gml:segments/gml:ArcString)", dom);
        xpath.evaluate("count(//cite:curvepolygons[@gml:id='cp.1']/cite:geom/gml:Polygon/gml:exterior/gml:Ring/gml:curveMember/gml:LineString)", dom);
        xpath.evaluate("count(//cite:curvepolygons[@gml:id='cp.1']/cite:geom/gml:Polygon/gml:interior/gml:Ring/gml:curveMember/gml:Curve/gml:segments/gml:ArcString)", dom);
    }
}

