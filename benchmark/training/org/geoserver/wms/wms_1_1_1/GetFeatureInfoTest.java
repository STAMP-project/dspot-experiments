/**
 * (c) 2014 - 2017 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2014 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wms.wms_1_1_1;


import GML2FeatureInfoOutputFormat.FORMAT;
import MockData.CITE_PREFIX;
import MockData.CITE_URI;
import MockData.FORESTS;
import ProjectionPolicy.NONE;
import ProjectionPolicy.REPROJECT_TO_DECLARED;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.logging.Level;
import javax.xml.namespace.QName;
import org.custommonkey.xmlunit.XMLAssert;
import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.CatalogFactory;
import org.geoserver.catalog.CoverageInfo;
import org.geoserver.catalog.FeatureTypeInfo;
import org.geoserver.catalog.LayerGroupInfo;
import org.geoserver.catalog.LayerInfo;
import org.geoserver.catalog.PublishedInfo;
import org.geoserver.catalog.StyleInfo;
import org.geoserver.catalog.WorkspaceInfo;
import org.geoserver.data.test.MockData;
import org.geoserver.test.RemoteOWSTestSupport;
import org.geoserver.wms.WMSInfo;
import org.geoserver.wms.WMSTestSupport;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.Rule;
import org.geotools.styling.Style;
import org.geotools.styling.StyleFactory;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.springframework.mock.web.MockHttpServletResponse;
import org.w3c.dom.Document;

import static GML3FeatureInfoOutputFormat.FORMAT;


public class GetFeatureInfoTest extends WMSTestSupport {
    public static String WCS_PREFIX = "wcs";

    public static String WCS_URI = "http://www.opengis.net/wcs/1.1.1";

    public static QName TASMANIA_BM = new QName(GetFeatureInfoTest.WCS_URI, "BlueMarble", GetFeatureInfoTest.WCS_PREFIX);

    public static QName SQUARES = new QName(MockData.CITE_URI, "squares", MockData.CITE_PREFIX);

    public static QName CUSTOM = new QName(MockData.CITE_URI, "custom", MockData.CITE_PREFIX);

    public static QName POINT_TEST_2D = new QName(MockData.CITE_URI, "point_test_2d", MockData.CITE_PREFIX);

    public static QName POINT_TEST_3D = new QName(MockData.CITE_URI, "point_test_3d", MockData.CITE_PREFIX);

    public static QName STATES = new QName(MockData.SF_URI, "states", MockData.SF_PREFIX);

    /**
     * Test GetFeatureInfo with 3D content, and the result returns the expected point.
     */
    @Test
    public void testPoint3d() throws Exception {
        FeatureTypeInfo info = getCatalog().getFeatureTypeByName(CITE_URI, "point_test_3d");
        ReferencedEnvelope b = info.getLatLonBoundingBox();
        String bbox = (((((((b.getMinX()) + ",") + (b.getMinY())) + ",") + (b.getMaxX())) + ",") + (b.getMaxY())) + "&srs=EPSG:4326";
        // first request against 2D dataset with the stacker transformation
        String layer2d = getLayerId(GetFeatureInfoTest.POINT_TEST_2D);
        String base2d = ((((("wms?version=1.1.1&format=png&info_format=text/html&request=GetFeatureInfo&layers=" + layer2d) + "&query_layers=") + layer2d) + "&styles=point_test&bbox=") + bbox) + "&feature_count=10";
        Document dom2d = getAsDOM(((((((((base2d + "&width=") + 10) + "&height=") + 10) + "&x=") + 5) + "&y=") + 5));
        // print(dom2d);
        XMLAssert.assertXpathEvaluatesTo("11", "count(/html/body/table/tr)", dom2d);
        // second request against 3D dataset
        String layer3d = getLayerId(GetFeatureInfoTest.POINT_TEST_3D);
        String base3d = ((((("wms?version=1.1.1&format=png&info_format=text/html&request=GetFeatureInfo&layers=" + layer3d) + "&query_layers=") + layer3d) + "&styles=point_test&bbox=") + bbox) + "&feature_count=10";
        Document dom3d = getAsDOM(((((((((base3d + "&width=") + 10) + "&height=") + 10) + "&x=") + 5) + "&y=") + 5));
        // print(dom3d);
        XMLAssert.assertXpathEvaluatesTo("11", "count(/html/body/table/tr)", dom3d);
    }

    @Test
    public void testPointStacker() throws Exception {
        String layerName = getLayerId(MockData.BRIDGES);
        // first request against 2D dataset
        String base2d = ((("wms?version=1.1.1&format=png&info_format=text/html&request=GetFeatureInfo&layers=" + layerName) + "&query_layers=") + layerName) + "&styles=stacker&bbox=-1,-1,1,1&srs=EPSG:4326&feature_count=10";
        Document dom2d = getAsDOM(((((((((base2d + "&width=") + 100) + "&height=") + 100) + "&x=") + 50) + "&y=") + 50));
        // print(dom2d);
        // used to throw an exception and fail
        XMLAssert.assertXpathEvaluatesTo("2", "count(/html/body/table/tr)", dom2d);
    }

    /**
     * Tests GML output does not break when asking for an area that has no data with GML feature
     * bounding enabled. This method tests GML 2 with Content-Type: <code>application/vnd.ogc.gml
     * </code>.
     */
    @Test
    public void testGMLNoData() throws Exception {
        this.testGMLNoData(FORMAT);
    }

    /**
     * Tests GML output does not break when asking for an area that has no data with GML feature
     * bounding enabled. This method tests GML 2 with Content-Type: <code>text/xml</code>.
     */
    @Test
    public void testXMLNoData() throws Exception {
        this.testGMLNoData(XML2FeatureInfoOutputFormat.FORMAT);
    }

    /**
     * Tests GML output does not break when asking for an area that has no data with GML feature
     * bounding enabled. This method tests GML 3.1.1 with Content-Type: <code>
     * text/xml; subtype=gml/3.1.1</code>.
     */
    @Test
    public void testXML311NoData() throws Exception {
        this.testGMLNoData(XML311FeatureInfoOutputFormat.FORMAT);
    }

    /**
     * Tests GML outside of expected polygon
     */
    @Test
    public void testSimple() throws Exception {
        String layer = getLayerId(FORESTS);
        String request = (((("wms?version=1.1.1&bbox=-0.002,-0.002,0.002,0.002&styles=&format=jpeg" + "&info_format=text/plain&request=GetFeatureInfo&layers=") + layer) + "&query_layers=") + layer) + "&width=20&height=20&x=10&y=10";
        String result = getAsString(request);
        // System.out.println(result);
        Assert.assertNotNull(result);
        Assert.assertTrue(((result.indexOf("Green Forest")) > 0));
    }

    @Test
    public void testAllowedMimeTypes() throws Exception {
        WMSInfo wms = getWMS().getServiceInfo();
        GetFeatureInfoOutputFormat format = new TextFeatureInfoOutputFormat(getWMS());
        wms.getGetFeatureInfoMimeTypes().add(format.getContentType());
        wms.setGetFeatureInfoMimeTypeCheckingEnabled(true);
        getGeoServer().save(wms);
        // check mime type allowed
        String layer = getLayerId(FORESTS);
        String request = (((("wms?version=1.1.1&bbox=-0.002,-0.002,0.002,0.002&styles=&format=jpeg" + "&info_format=text/plain&request=GetFeatureInfo&layers=") + layer) + "&query_layers=") + layer) + "&width=20&height=20&x=10&y=10";
        String result = getAsString(request);
        // System.out.println(result);
        Assert.assertNotNull(result);
        Assert.assertTrue(((result.indexOf("Green Forest")) > 0));
        // check mime type not allowed
        request = (((((("wms?version=1.1.1&bbox=-0.002,-0.002,0.002,0.002&styles=&format=jpeg" + "&info_format=") + (FORMAT)) + "&request=GetFeatureInfo&layers=") + layer) + "&query_layers=") + layer) + "&width=20&height=20&x=10&y=10";
        result = getAsString(request);
        Assert.assertTrue(((result.indexOf("ForbiddenFormat")) > 0));
        wms.getGetFeatureInfoMimeTypes().clear();
        wms.setGetFeatureInfoMimeTypeCheckingEnabled(false);
        getGeoServer().save(wms);
        request = (((((("wms?version=1.1.1&bbox=-0.002,-0.002,0.002,0.002&styles=&format=jpeg" + "&info_format=") + (FORMAT)) + "&request=GetFeatureInfo&layers=") + layer) + "&query_layers=") + layer) + "&width=20&height=20&x=10&y=10";
        result = getAsString(request);
        Assert.assertTrue(((result.indexOf("Green Forest")) > 0));
    }

    /**
     * Tests property selection expected polygon
     */
    @Test
    public void testSelectPropertiesVector() throws Exception {
        String layer = getLayerId(FORESTS);
        String request = (((("wms?version=1.1.1&bbox=-0.002,-0.002,0.002,0.002&styles=&format=jpeg&service=wms" + "&info_format=text/plain&request=GetFeatureInfo&layers=") + layer) + "&query_layers=") + layer) + "&width=20&height=20&x=10&y=10&propertyName=NAME,FID";
        String result = getAsString(request);
        // System.out.println(result);
        Assert.assertNotNull(result);
        int idxGeom = result.indexOf("the_geom");
        int idxFid = result.indexOf("FID");
        int idxName = result.indexOf("NAME");
        Assert.assertEquals((-1), idxGeom);// geometry filtered out

        Assert.assertTrue((idxFid > 0));
        Assert.assertTrue((idxName > 0));
        Assert.assertTrue((idxName < idxFid));// properties got reordered as expected

    }

    /**
     * Tests a simple GetFeatureInfo works, and that the result contains the expected polygon
     */
    @Test
    public void testSimpleHtml() throws Exception {
        String layer = getLayerId(FORESTS);
        String request = (((("wms?version=1.1.1&bbox=-0.002,-0.002,0.002,0.002&styles=&format=jpeg" + "&info_format=text/html&request=GetFeatureInfo&layers=") + layer) + "&query_layers=") + layer) + "&width=20&height=20&x=10&y=10";
        Document dom = getAsDOM(request);
        // count lines that do contain a forest reference
        XMLAssert.assertXpathEvaluatesTo("1", "count(/html/body/table/tr/td[starts-with(.,'Forests.')])", dom);
        MockHttpServletResponse response = getAsServletResponse(request, "");
        // Check if the character encoding is the one expected
        Assert.assertTrue("UTF-8".equals(response.getCharacterEncoding()));
    }

    /**
     * Tests GetFeatureInfo with a buffer specified works, and that the result contains the expected
     * polygon
     */
    @Test
    public void testBuffer() throws Exception {
        // to setup the request and the buffer I rendered BASIC_POLYGONS using GeoServer, then
        // played
        // against the image coordinates
        String layer = getLayerId(MockData.BASIC_POLYGONS);
        String base = (((("wms?version=1.1.1&bbox=-4.5,-2.,4.5,7&styles=&format=jpeg&info_format=text/html" + "&request=GetFeatureInfo&layers=") + layer) + "&query_layers=") + layer) + "&width=300&height=300";
        Document dom = getAsDOM((base + "&x=85&y=230"));
        // make sure the document is empty, as we chose an area with no features inside
        XMLAssert.assertXpathEvaluatesTo("0", "count(/html/body/table/tr)", dom);
        // another request that will catch one feature due to the extended buffer, make sure it's in
        dom = getAsDOM((base + "&x=85&y=230&buffer=40"));
        XMLAssert.assertXpathEvaluatesTo("1", "count(/html/body/table/tr/td[starts-with(.,'BasicPolygons.')])", dom);
        XMLAssert.assertXpathEvaluatesTo("1", "count(/html/body/table/tr/td[. = 'BasicPolygons.1107531493630'])", dom);
        // this one would end up catching everything (3 features) if it wasn't that we say the max
        // buffer at 50
        // in the WMS configuration
        dom = getAsDOM((base + "&x=85&y=230&buffer=300"));
        XMLAssert.assertXpathEvaluatesTo("1", "count(/html/body/table/tr/td[starts-with(.,'BasicPolygons.')])", dom);
        XMLAssert.assertXpathEvaluatesTo("1", "count(/html/body/table/tr/td[. = 'BasicPolygons.1107531493630'])", dom);
    }

    /**
     * Tests GetFeatureInfo with a buffer specified works, and that the result contains the expected
     * polygon
     */
    @Test
    public void testAutoBuffer() throws Exception {
        String layer = getLayerId(MockData.BASIC_POLYGONS);
        String base = (((("wms?version=1.1.1&bbox=-4.5,-2.,4.5,7&format=jpeg&info_format=text/html" + "&request=GetFeatureInfo&layers=") + layer) + "&query_layers=") + layer) + "&width=300&height=300&x=111&y=229";
        String url = base + "&styles=";
        Document dom = getAsDOM(url);
        // print(dom);
        // make sure the document is empty, the style we chose has thin lines
        XMLAssert.assertXpathEvaluatesTo("0", "count(/html/body/table/tr)", dom);
        // another request that will catch one feature due to the style with a thick stroke, make
        // sure it's in
        dom = getAsDOM((base + "&styles=thickStroke"));
        XMLAssert.assertXpathEvaluatesTo("1", "count(/html/body/table/tr/td[starts-with(.,'BasicPolygons.')])", dom);
        XMLAssert.assertXpathEvaluatesTo("1", "count(/html/body/table/tr/td[. = 'BasicPolygons.1107531493630'])", dom);
    }

    /**
     * Tests GetFeatureInfo uses the env params
     */
    @Test
    public void testParameterizedStyle() throws Exception {
        String layer = getLayerId(MockData.BASIC_POLYGONS);
        String base = (((("wms?version=1.1.1&bbox=-4.5,-2.,4.5,7&format=jpeg&info_format=text/html" + "&request=GetFeatureInfo&layers=") + layer) + "&query_layers=") + layer) + "&width=300&height=300&x=111&y=229&styles=paramStroke";
        Document dom = getAsDOM(base);
        // make sure the document is empty, the style we chose has thin lines
        XMLAssert.assertXpathEvaluatesTo("0", "count(/html/body/table/tr)", dom);
        // another request that will catch one feature due to the style with a thick stroke, make
        // sure it's in
        dom = getAsDOM((base + "&env=thickness:12"));
        // print(dom);
        XMLAssert.assertXpathEvaluatesTo("1", "count(/html/body/table/tr/td[starts-with(.,'BasicPolygons.')])", dom);
        XMLAssert.assertXpathEvaluatesTo("1", "count(/html/body/table/tr/td[. = 'BasicPolygons.1107531493630'])", dom);
    }

    /**
     * Tests GetFeatureInfo with a buffer specified works, and that the result contains the expected
     * polygon
     */
    @Test
    public void testBufferScales() throws Exception {
        String layer = getLayerId(GetFeatureInfoTest.SQUARES);
        String base = ((("wms?version=1.1.1&format=png&info_format=text/html&request=GetFeatureInfo&layers=" + layer) + "&query_layers=") + layer) + "&styles=squares&bbox=0,0,10000,10000&feature_count=10&srs=EPSG:32632";
        // first request, should provide no result, scale is 1:100
        int w = ((int) ((100.0 / 0.28) * 1000));// dpi compensation

        Document dom = getAsDOM(((((((base + "&width=") + w) + "&height=") + w) + "&x=20&y=") + (w - 20)));
        // print(dom);
        // make sure the document is empty, the style we chose has thin lines
        XMLAssert.assertXpathEvaluatesTo("0", "count(/html/body/table/tr)", dom);
        // second request, should provide oe result, scale is 1:50
        w = ((int) ((200.0 / 0.28) * 1000));// dpi compensation

        dom = getAsDOM(((((((base + "&width=") + w) + "&height=") + w) + "&x=20&y=") + (w - 20)));
        // print(dom);
        XMLAssert.assertXpathEvaluatesTo("1", "count(/html/body/table/tr/td[starts-with(.,'squares.')])", dom);
        XMLAssert.assertXpathEvaluatesTo("1", "count(/html/body/table/tr/td[. = 'squares.1'])", dom);
        // third request, should provide two result, scale is 1:10
        w = ((int) ((1000.0 / 0.28) * 1000));// dpi compensation

        dom = getAsDOM(((((((base + "&width=") + w) + "&height=") + w) + "&x=20&y=") + (w - 20)));
        // print(dom);
        XMLAssert.assertXpathEvaluatesTo("2", "count(/html/body/table/tr/td[starts-with(.,'squares.')])", dom);
        XMLAssert.assertXpathEvaluatesTo("1", "count(/html/body/table/tr/td[. = 'squares.1'])", dom);
        XMLAssert.assertXpathEvaluatesTo("1", "count(/html/body/table/tr/td[. = 'squares.2'])", dom);
    }

    /**
     * Tests a GetFeatureInfo again works, and that the result contains the expected polygon
     */
    @Test
    public void testTwoLayers() throws Exception {
        String layer = ((getLayerId(FORESTS)) + ",") + (getLayerId(MockData.LAKES));
        String request = (((("wms?version=1.1.1&bbox=-0.002,-0.002,0.002,0.002&styles=&format=jpeg" + "&info_format=text/html&request=GetFeatureInfo&layers=") + layer) + "&query_layers=") + layer) + "&width=20&height=20&x=10&y=10";
        String result = getAsString(request);
        Assert.assertNotNull(result);
        Assert.assertTrue(((result.indexOf("Green Forest")) > 0));
        // GEOS-2603 GetFeatureInfo returns html tables without css style if more than one layer is
        // selected
        Assert.assertTrue(((result.indexOf("<style type=\"text/css\">")) > 0));
    }

    /**
     * Tests a GetFeatureInfo again works, and that the result contains the expected polygon
     */
    @Test
    public void testSelectPropertiesTwoVectorLayers() throws Exception {
        String layer = ((getLayerId(FORESTS)) + ",") + (getLayerId(MockData.LAKES));
        String request = ((((("wms?version=1.1.1&bbox=-0.002,-0.002,0.002,0.002&styles=&format=jpeg" + "&info_format=text/plain&request=GetFeatureInfo&layers=") + layer) + "&query_layers=") + layer) + "&width=20&height=20&x=10&y=10&buffer=10&service=wms") + "&feature_count=2&propertyName=(FID)(NAME)";
        String result = getAsString(request);
        Assert.assertNotNull(result);
        int idxGeom = result.indexOf("the_geom");
        int idxLakes = result.indexOf("Lakes");
        int idxFid = result.indexOf("FID");
        int idxName = result.indexOf("NAME");
        Assert.assertEquals((-1), idxGeom);// geometry filtered out

        Assert.assertTrue((idxFid > 0));
        Assert.assertTrue((idxName > 0));
        Assert.assertTrue((idxLakes > 0));
        Assert.assertTrue((idxFid < idxLakes));// fid only for the first features

        Assert.assertTrue((idxName > idxLakes));// name only for the second features

    }

    /**
     * Tests a GetFeatureInfo again works, and that the result contains the expected polygon
     */
    @Test
    public void testSelectPropertiesTwoVectorLayersOneList() throws Exception {
        String layer = ((getLayerId(FORESTS)) + ",") + (getLayerId(MockData.LAKES));
        String request = ((((("wms?version=1.1.1&bbox=-0.002,-0.002,0.002,0.002&styles=&format=jpeg" + "&info_format=text/plain&request=GetFeatureInfo&layers=") + layer) + "&query_layers=") + layer) + "&width=20&height=20&x=10&y=10&buffer=10&service=wms") + "&feature_count=2&propertyName=NAME";
        String result = getAsString(request);
        // System.out.println(result);
        Assert.assertNotNull(result);
        int idxGeom = result.indexOf("the_geom");
        int idxLakes = result.indexOf("Lakes");
        int idxName1 = result.indexOf("NAME");
        int idxName2 = result.indexOf("NAME", idxLakes);
        Assert.assertEquals((-1), idxGeom);// geometry filtered out

        Assert.assertTrue((idxName1 > 0));
        Assert.assertTrue((idxName2 > 0));
        Assert.assertTrue((idxLakes > 0));
        // name in both features
        Assert.assertTrue((idxName1 < idxLakes));
        Assert.assertTrue((idxName2 > idxLakes));
    }

    /**
     * Tests that FEATURE_COUNT is respected globally, not just per layer
     */
    @Test
    public void testTwoLayersFeatureCount() throws Exception {
        // this request hits on two overlapping features, a lake and a forest
        String layer = ((getLayerId(FORESTS)) + ",") + (getLayerId(MockData.LAKES));
        String request = ((((("wms?REQUEST=GetFeatureInfo&EXCEPTIONS=application%2Fvnd.ogc.se_xml&" + (("BBOX=-0.002356%2C-0.004819%2C0.005631%2C0.004781&SERVICE=WMS&VERSION=1.1.0&X=267&Y=325" + "&INFO_FORMAT=application/vnd.ogc.gml") + "&QUERY_LAYERS=")) + layer) + "&Layers=") + layer) + " &Styles=&WIDTH=426&HEIGHT=512") + "&format=image%2Fpng&srs=EPSG%3A4326";
        // no feature count, just one should be returned
        Document dom = getAsDOM(request);
        XMLAssert.assertXpathEvaluatesTo("1", "count(//gml:featureMember)", dom);
        XMLAssert.assertXpathEvaluatesTo("1", "count(//cite:Forests)", dom);
        // feature count set to 2, both features should be there
        dom = getAsDOM((request + "&FEATURE_COUNT=2"));
        // print(dom);
        XMLAssert.assertXpathEvaluatesTo("2", "count(//gml:featureMember)", dom);
        XMLAssert.assertXpathEvaluatesTo("1", "count(//cite:Forests)", dom);
        XMLAssert.assertXpathEvaluatesTo("1", "count(//cite:Lakes)", dom);
    }

    /**
     * Check GetFeatureInfo returns an error if the format is not known, instead of returning the
     * text format as in https://osgeo-org.atlassian.net/browse/GEOS-1924
     */
    @Test
    public void testUknownFormat() throws Exception {
        String layer = ((FORESTS.getPrefix()) + ":") + (FORESTS.getLocalPart());
        String request = ((("wms?version=1.1.1&bbox=-0.002,-0.002,0.002,0.002&styles=&format=jpeg&info_format=unknown/format&request=GetFeatureInfo&layers=" + layer) + "&query_layers=") + layer) + "&width=20&height=20&x=10&y=10";
        Document doc = dom(get(request), true);
        // print(doc);
        XMLAssert.assertXpathEvaluatesTo("1", "count(//ServiceExceptionReport/ServiceException)", doc);
        XMLAssert.assertXpathEvaluatesTo("InvalidFormat", "/ServiceExceptionReport/ServiceException/@code", doc);
        XMLAssert.assertXpathEvaluatesTo("info_format", "/ServiceExceptionReport/ServiceException/@locator", doc);
    }

    @Test
    public void testCoverage() throws Exception {
        // https://osgeo-org.atlassian.net/browse/GEOS-2574
        String layer = getLayerId(GetFeatureInfoTest.TASMANIA_BM);
        String request = ((((("wms?service=wms&request=GetFeatureInfo&version=1.1.1" + "&layers=") + layer) + "&styles=&bbox=146.5,-44.5,148,-43&width=600&height=600") + "&info_format=text/html&query_layers=") + layer) + "&x=300&y=300&srs=EPSG:4326";
        Document dom = getAsDOM(request);
        // we also have the charset which may be platf. dep.
        XMLAssert.assertXpathEvaluatesTo("1", "count(/html/body/table/tr/th[. = 'RED_BAND'])", dom);
        XMLAssert.assertXpathEvaluatesTo("1", "count(/html/body/table/tr/th[. = 'GREEN_BAND'])", dom);
        XMLAssert.assertXpathEvaluatesTo("1", "count(/html/body/table/tr/th[. = 'BLUE_BAND'])", dom);
    }

    @Test
    public void testCoveragePropertySelection() throws Exception {
        // https://osgeo-org.atlassian.net/browse/GEOS-2574
        String layer = getLayerId(GetFeatureInfoTest.TASMANIA_BM);
        String request = ((((("wms?service=wms&request=GetFeatureInfo&version=1.1.1" + "&layers=") + layer) + "&styles=&bbox=146.5,-44.5,148,-43&width=600&height=600") + "&info_format=text/html&query_layers=") + layer) + "&x=300&y=300&srs=EPSG:4326&propertyName=RED_BAND";
        Document dom = getAsDOM(request);
        // we also have the charset which may be platf. dep.
        XMLAssert.assertXpathEvaluatesTo("1", "count(/html/body/table/tr/th[. = 'RED_BAND'])", dom);
        XMLAssert.assertXpathEvaluatesTo("0", "count(/html/body/table/tr/th[. = 'GREEN_BAND'])", dom);
        XMLAssert.assertXpathEvaluatesTo("0", "count(/html/body/table/tr/th[. = 'BLUE_BAND'])", dom);
    }

    @Test
    public void testCoverageGML() throws Exception {
        // https://osgeo-org.atlassian.net/browse/GEOS-3996
        String layer = getLayerId(GetFeatureInfoTest.TASMANIA_BM);
        String request = ((((("wms?service=wms&request=GetFeatureInfo&version=1.1.1" + "&layers=") + layer) + "&styles=&bbox=146.5,-44.5,148,-43&width=600&height=600") + "&info_format=application/vnd.ogc.gml&query_layers=") + layer) + "&x=300&y=300&srs=EPSG:4326";
        Document dom = getAsDOM(request);
        // print(dom);
        XMLAssert.assertXpathEvaluatesTo("26.0", "//wfs:FeatureCollection/gml:featureMember/wcs:BlueMarble/wcs:RED_BAND", dom);
        XMLAssert.assertXpathEvaluatesTo("70.0", "//wfs:FeatureCollection/gml:featureMember/wcs:BlueMarble/wcs:GREEN_BAND", dom);
        XMLAssert.assertXpathEvaluatesTo("126.0", "//wfs:FeatureCollection/gml:featureMember/wcs:BlueMarble/wcs:BLUE_BAND", dom);
    }

    @Test
    public void testCoverageScales() throws Exception {
        String layer = getLayerId(GetFeatureInfoTest.TASMANIA_BM);
        String request = ((((("wms?service=wms&request=GetFeatureInfo&version=1.1.1" + "&layers=") + layer) + "&styles=rasterScales&bbox=146.5,-44.5,148,-43") + "&info_format=text/html&query_layers=") + layer) + "&x=300&y=300&srs=EPSG:4326";
        // this one should be blank
        Document dom = getAsDOM((request + "&width=300&height=300"));
        XMLAssert.assertXpathEvaluatesTo("0", "count(/html/body/table/tr/th)", dom);
        // this one should draw the coverage
        dom = getAsDOM((request + "&width=600&height=600"));
        // we also have the charset which may be platf. dep.
        XMLAssert.assertXpathEvaluatesTo("1", "count(/html/body/table/tr/th[. = 'RED_BAND'])", dom);
        XMLAssert.assertXpathEvaluatesTo("1", "count(/html/body/table/tr/th[. = 'GREEN_BAND'])", dom);
        XMLAssert.assertXpathEvaluatesTo("1", "count(/html/body/table/tr/th[. = 'BLUE_BAND'])", dom);
    }

    @Test
    public void testOutsideCoverage() throws Exception {
        // a request which is way large on the west side, lots of blank space
        String layer = getLayerId(GetFeatureInfoTest.TASMANIA_BM);
        String request = ((((("wms?service=wms&request=GetFeatureInfo&version=1.1.1" + "&layers=") + layer) + "&styles=raster&bbox=0,-90,148,-43") + "&info_format=text/html&query_layers=") + layer) + "&width=300&height=300&x=10&y=150&srs=EPSG:4326";
        // this one should be blank, but not be a service exception
        Document dom = getAsDOM((request + ""));
        XMLAssert.assertXpathEvaluatesTo("1", "count(/html)", dom);
        XMLAssert.assertXpathEvaluatesTo("0", "count(/html/body/table/tr/th)", dom);
    }

    /**
     * Check we report back an exception when query_layer contains layers not part of LAYERS
     */
    @Test
    public void testUnkonwnQueryLayer() throws Exception {
        String layers1 = ((getLayerId(FORESTS)) + ",") + (getLayerId(MockData.LAKES));
        String layers2 = ((getLayerId(FORESTS)) + ",") + (getLayerId(MockData.BRIDGES));
        String request = ((("wms?version=1.1.1&bbox=-0.002,-0.002,0.002,0.002&styles=&format=jpeg&info_format=text/html&request=GetFeatureInfo&layers=" + layers1) + "&query_layers=") + layers2) + "&width=20&height=20&x=10&y=10&info";
        Document dom = getAsDOM((request + ""));
        XMLAssert.assertXpathEvaluatesTo("1", "count(/ServiceExceptionReport)", dom);
    }

    @Test
    public void testLayerQualified() throws Exception {
        String layer = "Forests";
        String q = (((("?version=1.1.1&bbox=-0.002,-0.002,0.002,0.002&styles=&format=jpeg" + "&info_format=text/plain&request=GetFeatureInfo&layers=") + layer) + "&query_layers=") + layer) + "&width=20&height=20&x=10&y=10";
        String request = "cite/Ponds/wms" + q;
        Document dom = getAsDOM(request);
        Assert.assertEquals("ServiceExceptionReport", dom.getDocumentElement().getNodeName());
        request = "cite/Forests/wms" + q;
        String result = getAsString(request);
        // System.out.println(result);
        Assert.assertNotNull(result);
        Assert.assertTrue(((result.indexOf("Green Forest")) > 0));
    }

    @Test
    public void testGroupWorkspaceQualified() throws Exception {
        // check the group works without workspace qualification
        String url = "wms?service=wms&version=1.1.1" + (("&layers=nature&width=100&height=100&format=image/png" + "&srs=epsg:4326&bbox=-0.002,-0.003,0.005,0.002&info_format=text/plain") + "&request=GetFeatureInfo&query_layers=nature&x=50&y=50&feature_count=2");
        String result = getAsString(url);
        Assert.assertTrue(((result.indexOf("Blue Lake")) > 0));
        Assert.assertTrue(((result.indexOf("Green Forest")) > 0));
        // check that it still works when workspace qualified
        result = getAsString(("cite/" + url));
        Assert.assertTrue(((result.indexOf("Blue Lake")) > 0));
        Assert.assertTrue(((result.indexOf("Green Forest")) > 0));
        // but we have nothing if the workspace
        Document dom = getAsDOM(("cdf/" + url));
        Assert.assertEquals("ServiceExceptionReport", dom.getDocumentElement().getNodeName());
    }

    @Test
    public void testNonExactVersion() throws Exception {
        String layer = getLayerId(FORESTS);
        String request = (((("wms?version=1.0.0&bbox=-0.002,-0.002,0.002,0.002&styles=&format=jpeg" + "&info_format=text/plain&request=GetFeatureInfo&layers=") + layer) + "&query_layers=") + layer) + "&width=20&height=20&x=10&y=10";
        String result = getAsString(request);
        Assert.assertNotNull(result);
        Assert.assertTrue(((result.indexOf("Green Forest")) > 0));
        request = (((("wms?version=1.1.0&bbox=-0.002,-0.002,0.002,0.002&styles=&format=jpeg" + "&info_format=text/plain&request=GetFeatureInfo&layers=") + layer) + "&query_layers=") + layer) + "&width=20&height=20&x=10&y=10";
        result = getAsString(request);
        Assert.assertNotNull(result);
        Assert.assertTrue(((result.indexOf("Green Forest")) > 0));
    }

    @Test
    public void testRasterFilterRed() throws Exception {
        String response = getAsString(("wms?bgcolor=0x000000&LAYERS=sf:mosaic&STYLES=" + ((("&FORMAT=image/png&SERVICE=WMS&VERSION=1.1.1" + "&REQUEST=GetFeatureInfo&SRS=EPSG:4326&BBOX=0,0,1,1&WIDTH=150&HEIGHT=150") + "&transparent=false&CQL_FILTER=location like 'red%25' + ") + "&query_layers=sf:mosaic&x=10&y=10")));
        Assert.assertTrue(((response.indexOf("RED_BAND = 255.0")) > 0));
        Assert.assertTrue(((response.indexOf("GREEN_BAND = 0.0")) > 0));
        Assert.assertTrue(((response.indexOf("BLUE_BAND = 0.0")) > 0));
    }

    @Test
    public void testRasterFilterGreen() throws Exception {
        String response = getAsString(("wms?bgcolor=0x000000&LAYERS=sf:mosaic&STYLES=" + ((("&FORMAT=image/png&SERVICE=WMS&VERSION=1.1.1" + "&REQUEST=GetFeatureInfo&SRS=EPSG:4326&BBOX=0,0,1,1&WIDTH=150&HEIGHT=150") + "&transparent=false&CQL_FILTER=location like 'green%25' + ") + "&query_layers=sf:mosaic&x=10&y=10")));
        Assert.assertTrue(((response.indexOf("RED_BAND = 0.0")) > 0));
        Assert.assertTrue(((response.indexOf("GREEN_BAND = 255.0")) > 0));
        Assert.assertTrue(((response.indexOf("BLUE_BAND = 0.0")) > 0));
    }

    @Test
    public void testPropertySelectionWmsCascade() throws Exception {
        if (!(RemoteOWSTestSupport.isRemoteWMSStatesAvailable(LOGGER))) {
            LOGGER.log(Level.WARNING, "Skipping testPropertySelectionWmsCascade");
            return;
        }
        String result = getAsString(("wms?REQUEST=GetFeatureInfo" + ((("&BBOX=-132.835937%2C21.132813%2C-64.867187%2C55.117188" + "&SERVICE=WMS&INFO_FORMAT=text/plain") + "&QUERY_LAYERS=rstates&FEATURE_COUNT=50&Layers=rstates&WIDTH=300&HEIGHT=150") + "&format=image%2Fpng&styles=&srs=EPSG%3A4326&version=1.1.1&x=149&y=70&propertyName=STATE_ABBR,STATE_NAME")));
        // System.out.println(result);
        int idxGeom = result.indexOf("the_geom");
        int idxName = result.indexOf("STATE_NAME");
        int idxFips = result.indexOf("STATE_FIPS");
        int idxAbbr = result.indexOf("STATE_ABBR");
        Assert.assertEquals((-1), idxGeom);
        Assert.assertEquals((-1), idxFips);
        Assert.assertTrue((idxAbbr > 0));
        Assert.assertTrue((idxName > 0));
        Assert.assertTrue((idxAbbr < idxName));
    }

    @Test
    public void testRasterKeepNative() throws Exception {
        // force it to "keep native"
        CoverageInfo ci = getCatalog().getCoverageByName(getLayerId(GetFeatureInfoTest.CUSTOM));
        ci.setProjectionPolicy(NONE);
        getCatalog().save(ci);
        // make a first reprojected request on a pixel that's black (0)
        String result = getAsString(("wms?REQUEST=GetFeatureInfo&EXCEPTIONS=application%2Fvnd.ogc.se_xml" + (("&BBOX=-887430.34934%2C4467316.30601%2C-885862.361705%2C4468893.535223&SERVICE=WMS" + "&INFO_FORMAT=text%2Fplain&QUERY_LAYERS=cite%3Acustom&FEATURE_COUNT=50&Layers=custom") + "&WIDTH=509&HEIGHT=512&format=image%2Fjpeg&styles=&srs=epsg%3A900913&version=1.1.1&x=177&y=225")));
        Assert.assertTrue(result.contains("0.0"));
        // and now one with actual data, 2
        result = getAsString(("wms?REQUEST=GetFeatureInfo&EXCEPTIONS=application%2Fvnd.ogc.se_xml" + (("&BBOX=-887430.34934%2C4467316.30601%2C-885862.361705%2C4468893.535223&SERVICE=WMS" + "&INFO_FORMAT=text%2Fplain&QUERY_LAYERS=cite%3Acustom&FEATURE_COUNT=50&Layers=custom") + "&WIDTH=509&HEIGHT=512&format=image%2Fjpeg&styles=&srs=epsg%3A900913&version=1.1.1&x=135&y=223")));
        Assert.assertTrue(result.contains("2.0"));
    }

    @Test
    public void testRasterReprojectToDeclared() throws Exception {
        // force it to "reproject to declared"
        CoverageInfo ci = getCatalog().getCoverageByName(getLayerId(GetFeatureInfoTest.CUSTOM));
        ci.setProjectionPolicy(REPROJECT_TO_DECLARED);
        ci.setSRS("EPSG:900913");
        getCatalog().save(ci);
        // make a first reprojected request on a pixel that's black (0)
        String result = getAsString(("wms?REQUEST=GetFeatureInfo&EXCEPTIONS=application%2Fvnd.ogc.se_xml" + (("&BBOX=-887430.34934%2C4467316.30601%2C-885862.361705%2C4468893.535223&SERVICE=WMS" + "&INFO_FORMAT=text%2Fplain&QUERY_LAYERS=cite%3Acustom&FEATURE_COUNT=50&Layers=custom") + "&WIDTH=509&HEIGHT=512&format=image%2Fjpeg&styles=&srs=epsg%3A900913&version=1.1.1&x=177&y=225")));
        Assert.assertTrue(result.contains("0.0"));
        // and now one with actual data, 2
        result = getAsString(("wms?REQUEST=GetFeatureInfo&EXCEPTIONS=application%2Fvnd.ogc.se_xml" + (("&BBOX=-887430.34934%2C4467316.30601%2C-885862.361705%2C4468893.535223&SERVICE=WMS" + "&INFO_FORMAT=text%2Fplain&QUERY_LAYERS=cite%3Acustom&FEATURE_COUNT=50&Layers=custom") + "&WIDTH=509&HEIGHT=512&format=image%2Fjpeg&styles=&srs=epsg%3A900913&version=1.1.1&x=135&y=223")));
        Assert.assertTrue(result.contains("2.0"));
    }

    @Test
    public void testGMLWithPostFilter() throws Exception {
        // we need to create a situation where a post filter is setup, simple way is to change the
        // style so that its filter is an or with more than 20 children
        Catalog cat = getCatalog();
        LayerInfo l = cat.getLayerByName(getLayerId(MockData.NAMED_PLACES));
        StyleInfo style = l.getDefaultStyle();
        Style s = style.getStyle();
        FeatureTypeStyle fts = s.featureTypeStyles().get(0);
        FilterFactory ff = CommonFactoryFinder.getFilterFactory(null);
        StyleFactory sf = CommonFactoryFinder.getStyleFactory();
        for (int i = 0; i < 21; i++) {
            Filter f = ff.equals(ff.literal(1), ff.literal(1));
            Rule r = sf.createRule();
            r.setFilter(f);
            r.symbolizers().add(sf.createPolygonSymbolizer());
            fts.rules().add(r);
        }
        cat.getResourcePool().writeStyle(style, s);
        cat.save(style);
        String layer = getLayerId(MockData.NAMED_PLACES);
        String request = ((((("wms?service=wms&request=GetFeatureInfo&version=1.1.1" + "&layers=") + layer) + "&styles=&bbox=0.000004,-0.00285,0.005596,0.00415&width=409&height=512") + "&info_format=application/vnd.ogc.gml&query_layers=") + layer) + "&x=194&y=229&srs=EPSG:4326";
        Document dom = getAsDOM(request);
        Assert.assertEquals("wfs:FeatureCollection", dom.getDocumentElement().getNodeName());
    }

    /**
     * The rendering engine has a 10-6 tolerance when evaluating rule scale activation,
     * GetFeatureInfo did not
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testScaleTolerance() throws Exception {
        String layer = getLayerId(MockData.BASIC_POLYGONS);
        String getMap = (((("wms?version=1.1.1&bbox=-10000,20000,10000,40000&srs=EPSG:900913&styles=scaleBased&format=image/png&info_format=text/html" + "&request=GetMap&layers=") + layer) + "&query_layers=") + layer) + "&width=2041&height=2041";
        BufferedImage image = getAsImage(getMap, "image/png");
        // ImageIO.write(image, "png", new File("/tmp/test.png"));
        assertPixel(image, 150, 150, Color.BLUE);
    }

    /**
     * Test GetFeatureInfo on a group layer with some no-queryable layers
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testGroupLayerWithNotQueryableLayers() throws Exception {
        Catalog catalog = getCatalog();
        CatalogFactory factory = catalog.getFactory();
        WorkspaceInfo workspace = catalog.getWorkspaceByName(CITE_PREFIX);
        String groupLayer = "glqueryable";
        // Only last layer will be queryable.
        LayerInfo buildingsLayer = catalog.getLayerByName(getLayerId(MockData.BUILDINGS));
        buildingsLayer.setQueryable(false);
        catalog.save(buildingsLayer);
        LayerInfo bridgesLayer = catalog.getLayerByName(getLayerId(MockData.BRIDGES));
        bridgesLayer.setQueryable(false);
        catalog.save(bridgesLayer);
        LayerInfo forestLayer = catalog.getLayerByName(getLayerId(FORESTS));
        forestLayer.setQueryable(true);
        catalog.save(forestLayer);
        LayerGroupInfo layerGroup = factory.createLayerGroup();
        layerGroup.setName(groupLayer);
        layerGroup.setWorkspace(workspace);
        layerGroup.getLayers().add(buildingsLayer);
        layerGroup.getLayers().add(bridgesLayer);
        layerGroup.getLayers().add(forestLayer);
        layerGroup.getStyles().add(null);
        layerGroup.getStyles().add(null);
        layerGroup.getStyles().add(null);
        new org.geoserver.catalog.CatalogBuilder(catalog).calculateLayerGroupBounds(layerGroup);
        catalog.add(layerGroup);
        String name = ((MockData.CITE_PREFIX) + ":") + groupLayer;
        String request = (((("wms?bbox=-0.002,-0.002,0.002,0.002&styles=&format=jpeg" + "&info_format=text/plain&request=GetFeatureInfo&layers=") + name) + "&query_layers=") + name) + "&width=20&height=20&x=10&y=10";
        String result = getAsString(request);
        Assert.assertNotNull(result);
        Assert.assertTrue(((result.indexOf("Green Forest")) > 0));
        buildingsLayer.setQueryable(true);
        catalog.save(buildingsLayer);
        bridgesLayer.setQueryable(true);
        catalog.save(bridgesLayer);
        catalog.remove(layerGroup);
    }

    /**
     * Test GetFeatureInfo on a group layer with no-queryable flag activated
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testNotQueryableGroupLayer() throws Exception {
        Catalog catalog = getCatalog();
        CatalogFactory factory = catalog.getFactory();
        WorkspaceInfo workspace = catalog.getWorkspaceByName(CITE_PREFIX);
        String groupLayer = "glnotqueryable";
        LayerGroupInfo layerGroup = factory.createLayerGroup();
        layerGroup.setName(groupLayer);
        layerGroup.setWorkspace(workspace);
        layerGroup.getLayers().add(catalog.getLayerByName(getLayerId(FORESTS)));
        layerGroup.getStyles().add(null);
        new org.geoserver.catalog.CatalogBuilder(catalog).calculateLayerGroupBounds(layerGroup);
        catalog.add(layerGroup);
        String name = ((MockData.CITE_PREFIX) + ":") + groupLayer;
        String request = (((("wms?bbox=-0.002,-0.002,0.002,0.002&styles=&format=jpeg" + "&info_format=text/plain&request=GetFeatureInfo&layers=") + name) + "&query_layers=") + name) + "&width=20&height=20&x=10&y=10";
        String result = getAsString(request);
        Assert.assertNotNull(result);
        Assert.assertTrue(((result.indexOf("Green Forest")) > 0));
        // Test no-queryable flag activated
        layerGroup.setQueryDisabled(true);
        result = getAsString(request);
        Assert.assertNotNull(result);
        Assert.assertTrue(((result.indexOf("no layer was queryable")) > 0));
        catalog.remove(layerGroup);
    }

    @Test
    public void testGetFeatureInfoOpaqueGroup() throws Exception {
        String url = ((((("wms?LAYERS=" + (WMSTestSupport.OPAQUE_GROUP)) + "&STYLES=&FORMAT=image%2Fpng") + "&SERVICE=WMS&VERSION=1.1.1&REQUEST=GetFeatureInfo&SRS=EPSG%3A4326&WIDTH=256&HEIGHT=256&BBOX=-0.0043,-0.0025,0.0043,0.0025") + "&info_format=text/plain&request=GetFeatureInfo&&query_layers=") + (WMSTestSupport.OPAQUE_GROUP)) + "&x=105&y=107";
        String response = getAsString(url);
        Assert.assertThat(response, CoreMatchers.containsString("FID = 102"));
    }

    @Test
    public void testFeatureInfoLayersInOpaqueGroup() throws Exception {
        LayerGroupInfo group = getCatalog().getLayerGroupByName(WMSTestSupport.OPAQUE_GROUP);
        for (PublishedInfo pi : group.layers()) {
            final String layerName = pi.prefixedName();
            String url = ((((("wms?LAYERS=" + layerName) + "&STYLES=&FORMAT=image%2Fpng") + "&SERVICE=WMS&VERSION=1.1.1&REQUEST=GetFeatureInfo&SRS=EPSG%3A4326&WIDTH=256&HEIGHT=256&BBOX=-0.0043,-0.0025,0.0043,0.0025") + "&info_format=text/plain&request=GetFeatureInfo&&query_layers=") + layerName) + "&x=105&y=107";
            Document dom = getAsDOM(url);
            // print(dom);
            // should not be found
            XMLAssert.assertXpathEvaluatesTo("1", "count(/ServiceExceptionReport)", dom);
            XMLAssert.assertXpathEvaluatesTo("layers", "//ServiceException/@locator", dom);
            XMLAssert.assertXpathEvaluatesTo("LayerNotDefined", "//ServiceException/@code", dom);
        }
    }

    @Test
    public void testQueryableAndNonQueryableLayersWithStyles() throws Exception {
        String states = getLayerId(GetFeatureInfoTest.STATES);
        String forests = getLayerId(FORESTS);
        String request = (((((((("wms?version=1.1.1&bbox=-0.002,-0.002,0.002,0.002&format=jpeg" + ("&info_format=text/plain&request=GetFeatureInfo&width=20&height=20&x=10&y=10" + "&layers=")) + states) + ",") + forests) + "&query_layers=") + states) + ",") + forests) + "&styles=Population,Forests";
        String result = getAsString(request);
        // System.out.println(result);
        Assert.assertNotNull(result);
        Assert.assertTrue(((result.indexOf("Green Forest")) > 0));
    }

    @Test
    public void testQueryableAndNonQueryableLayersWithCqlFilter() throws Exception {
        String states = getLayerId(GetFeatureInfoTest.STATES);
        String forests = getLayerId(FORESTS);
        String request = (((((((("wms?version=1.1.1&bbox=-0.002,-0.002,0.002,0.002&format=jpeg" + ("&info_format=text/plain&request=GetFeatureInfo&width=20&height=20&x=10&y=10" + "&layers=")) + states) + ",") + forests) + "&query_layers=") + states) + ",") + forests) + "&styles=&cql_filter=PERSONS>25000000;NAME='Green Forest'";
        String result = getAsString(request);
        // System.out.println(result);
        Assert.assertNotNull(result);
        Assert.assertTrue(((result.indexOf("Green Forest")) > 0));
    }

    @Test
    public void testQueryableAndNonQueryableLayersWithFilter() throws Exception {
        String states = getLayerId(GetFeatureInfoTest.STATES);
        String forests = getLayerId(FORESTS);
        String request = (((((((((("wms?version=1.1.1&bbox=-0.002,-0.002,0.002,0.002&format=jpeg" + ("&info_format=text/plain&request=GetFeatureInfo&width=20&height=20&x=10&y=10" + "&layers=")) + states) + ",") + forests) + "&query_layers=") + states) + ",") + forests) + "&styles=&filter=") + "(%3CFilter%3E%3CPropertyIsGreaterThan%3E%3CPropertyName%3EPERSONS%3C/PropertyName%3E%3CLiteral%3E25000000%3C/Literal%3E%3C/PropertyIsGreaterThan%3E%3C/Filter%3E)") + "(%3CFilter%3E%3CPropertyIsEqualTo%3E%3CPropertyName%3ENAME%3C/PropertyName%3E%3CLiteral%3EGreen%20Forest%3C/Literal%3E%3C/PropertyIsEqualTo%3E%3C/Filter%3E)";
        String result = getAsString(request);
        // System.out.println(result);
        Assert.assertNotNull(result);
        Assert.assertTrue(((result.indexOf("Green Forest")) > 0));
    }
}

