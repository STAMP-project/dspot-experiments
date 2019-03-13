/**
 * (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wfs.v1_1;


import Filter.INCLUDE;
import javax.xml.namespace.QName;
import org.custommonkey.xmlunit.XpathEngine;
import org.geoserver.data.test.SystemTestData;
import org.geoserver.wfs.WFSTestSupport;
import org.geotools.factory.CommonFactoryFinder;
import org.junit.Assert;
import org.junit.Test;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.WKTReader;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.FilterFactory2;
import org.w3c.dom.Document;


public class Transaction3DTest extends WFSTestSupport {
    static final QName FULL3D = new QName(SystemTestData.CITE_URI, "full3d", SystemTestData.CITE_PREFIX);

    static final FilterFactory2 FF = CommonFactoryFinder.getFilterFactory2();

    private XpathEngine xpath;

    private WKTReader wkt = new WKTReader();

    @Test
    public void testInsert3DPoint() throws Exception {
        Document insertDom = postRequest("insertPoint3d.xml");
        print(insertDom);
        String fid = assertSuccesfulInsert(insertDom);
        Document featureDom = getFeature(fid);
        print(featureDom);
        Assert.assertEquals("New point", xpath.evaluate("//cite:full3d/gml:name", featureDom));
        Assert.assertEquals("3", xpath.evaluate("//cite:full3d/cite:geom/gml:Point/@srsDimension", featureDom));
        Assert.assertEquals("204330 491816 16", xpath.evaluate("//cite:full3d/cite:geom/gml:Point/gml:pos", featureDom));
        // check it's actually 3d as a geometry
        SimpleFeature feature = getFeatureFromStore(fid);
        Geometry g = ((Geometry) (feature.getDefaultGeometry()));
        assertEqualND(g, wkt.read("POINT(204330 491816 16)"));
    }

    @Test
    public void testInsert3DLinestring() throws Exception {
        Document insertDom = postRequest("insertLinestring3d.xml");
        // print(insertDom);
        String fid = assertSuccesfulInsert(insertDom);
        Document featureDom = getFeature(fid);
        Assert.assertEquals("New line", xpath.evaluate("//cite:full3d/gml:name", featureDom));
        Assert.assertEquals("3", xpath.evaluate("//cite:full3d/cite:geom/gml:LineString/@srsDimension", featureDom));
        Assert.assertEquals("204330 491816 16 204319 491814 16", xpath.evaluate("//cite:full3d/cite:geom/gml:LineString/gml:posList", featureDom));
        // check it's actually 3d as a geometry
        SimpleFeature feature = getFeatureFromStore(fid);
        Geometry g = ((Geometry) (feature.getDefaultGeometry()));
        assertEqualND(g, wkt.read("LINESTRING(204330 491816 16, 204319 491814 16)"));
    }

    @Test
    public void testInsert3DPolygon() throws Exception {
        Document insertDom = postRequest("insertPolygon3d.xml");
        // print(insertDom);
        String fid = assertSuccesfulInsert(insertDom);
        Document featureDom = getFeature(fid);
        // print(featureDom);
        Assert.assertEquals("New polygon", xpath.evaluate("//cite:full3d/gml:name", featureDom));
        Assert.assertEquals("3", xpath.evaluate("//cite:full3d/cite:geom/gml:Polygon/@srsDimension", featureDom));
        Assert.assertEquals("94000 471000 10 94001 471000 11 94001 471001 12 94000 471001 13 94000 471000 10", xpath.evaluate("//cite:full3d/cite:geom/gml:Polygon/gml:exterior/gml:LinearRing/gml:posList", featureDom));
        // check it's actually 3d as a geometry
        SimpleFeature feature = getFeatureFromStore(fid);
        Geometry g = ((Geometry) (feature.getDefaultGeometry()));
        assertEqualND(g, wkt.read("POLYGON((94000 471000 10, 94001 471000 11, 94001 471001 12, 94000 471001 13, 94000 471000 10))"));
    }

    @Test
    public void testDelete3DPoint() throws Exception {
        Document deleteDom = postRequest("delete3d.xml", "${id}", "full3d.point");
        // print(deleteDom);
        assertSuccesfulDelete(deleteDom);
        Assert.assertNull(getFeatureFromStore("full3d.point"));
        Assert.assertEquals(2, getCountFromStore(INCLUDE));
    }

    @Test
    public void testDelete3DLineString() throws Exception {
        Document deleteDom = postRequest("delete3d.xml", "${id}", "full3d.ls");
        // print(deleteDom);
        assertSuccesfulDelete(deleteDom);
        Assert.assertNull(getFeatureFromStore("full3d.ls"));
        Assert.assertEquals(2, getCountFromStore(INCLUDE));
    }

    @Test
    public void testDelete3DPolygon() throws Exception {
        Document deleteDom = postRequest("delete3d.xml", "${id}", "full3d.poly");
        // print(deleteDom);
        assertSuccesfulDelete(deleteDom);
        Assert.assertNull(getFeatureFromStore("full3d.poly"));
        Assert.assertEquals(2, getCountFromStore(INCLUDE));
    }

    @Test
    public void testUpdate3DPoint() throws Exception {
        Document updateDom = postRequest("updatePoint3d.xml");
        // print(deleteDom);
        assertSuccesfulUpdate(updateDom);
        SimpleFeature feature = getFeatureFromStore("full3d.point");
        Geometry g = ((Geometry) (feature.getDefaultGeometry()));
        assertEqualND(g, wkt.read("POINT(204330 491816 16)"));
        Assert.assertEquals(3, getCountFromStore(INCLUDE));
    }

    @Test
    public void testUpdate3DLinestring() throws Exception {
        Document updateDom = postRequest("updateLinestring3d.xml");
        // print(deleteDom);
        assertSuccesfulUpdate(updateDom);
        SimpleFeature feature = getFeatureFromStore("full3d.ls");
        Geometry g = ((Geometry) (feature.getDefaultGeometry()));
        assertEqualND(g, wkt.read("LINESTRING(204330 491816 16, 204319 491814 16)"));
        Assert.assertEquals(3, getCountFromStore(INCLUDE));
    }

    @Test
    public void testUpdate3DPolygon() throws Exception {
        Document updateDom = postRequest("updatePolygon3d.xml");
        // print(deleteDom);
        assertSuccesfulUpdate(updateDom);
        SimpleFeature feature = getFeatureFromStore("full3d.poly");
        Geometry g = ((Geometry) (feature.getDefaultGeometry()));
        assertEqualND(g, wkt.read("POLYGON((94000 471000 10, 94001 471000 11, 94001 471001 12, 94000 471001 13, 94000 471000 10))"));
        Assert.assertEquals(3, getCountFromStore(INCLUDE));
    }
}

