/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.security;


import MockData.BASIC_POLYGONS;
import org.custommonkey.xmlunit.XMLUnit;
import org.custommonkey.xmlunit.XpathEngine;
import org.geoserver.test.GeoServerSystemTestSupport;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.w3c.dom.Document;


public class AuthencationKeyOWSTest extends GeoServerSystemTestSupport {
    private static String adminKey;

    private static String citeKey;

    @Test
    public void testAnonymousCapabilities() throws Exception {
        Document doc = getAsDOM("wms?request=GetCapabilities&version=1.1.0");
        // print(doc);
        // check we have the sf layers, but not the cite ones not the cdf ones
        XpathEngine engine = XMLUnit.newXpathEngine();
        Assert.assertTrue(((engine.getMatchingNodes("//Layer/Name[starts-with(text(), 'sf:')]", doc).getLength()) > 1));
        Assert.assertEquals(0, engine.getMatchingNodes("//Layer/Name[starts-with(text(), 'cite:')]", doc).getLength());
        Assert.assertEquals(0, engine.getMatchingNodes("//Layer/Name[starts-with(text(), 'cdf:')]", doc).getLength());
    }

    @Test
    public void testAdminCapabilities() throws Exception {
        Document doc = getAsDOM(("wms?request=GetCapabilities&version=1.1.0&authkey=" + (AuthencationKeyOWSTest.adminKey)));
        // print(doc);
        // check we have all the layers
        XpathEngine engine = XMLUnit.newXpathEngine();
        Assert.assertTrue(((engine.getMatchingNodes("//Layer/Name[starts-with(text(), 'sf:')]", doc).getLength()) > 1));
        Assert.assertTrue(((engine.getMatchingNodes("//Layer/Name[starts-with(text(), 'cdf:')]", doc).getLength()) > 1));
        Assert.assertTrue(((engine.getMatchingNodes("//Layer/Name[starts-with(text(), 'cite:')]", doc).getLength()) > 1));
        // check the authentication key has been propagated
        String url = engine.evaluate("//GetMap/DCPType/HTTP/Get/OnlineResource/@xlink:href", doc);
        Assert.assertTrue(url.contains(("&authkey=" + (AuthencationKeyOWSTest.adminKey))));
    }

    @Test
    public void testCiteCapabilities() throws Exception {
        Document doc = getAsDOM(("wms?request=GetCapabilities&version=1.1.0&authkey=" + (AuthencationKeyOWSTest.citeKey)));
        // print(doc);
        // check we have the sf and cite layers, but not cdf
        XpathEngine engine = XMLUnit.newXpathEngine();
        Assert.assertTrue(((engine.getMatchingNodes("//Layer/Name[starts-with(text(), 'sf:')]", doc).getLength()) > 1));
        Assert.assertTrue(((engine.getMatchingNodes("//Layer/Name[starts-with(text(), 'cite:')]", doc).getLength()) > 1));
        Assert.assertEquals(0, engine.getMatchingNodes("//Layer/Name[starts-with(text(), 'cdf:')]", doc).getLength());
        // check the authentication key has been propagated
        String url = engine.evaluate("//GetMap/DCPType/HTTP/Get/OnlineResource/@xlink:href", doc);
        Assert.assertTrue(url.contains(("&authkey=" + (AuthencationKeyOWSTest.citeKey))));
    }

    @Test
    public void testAnonymousGetFeature() throws Exception {
        Document doc = getAsDOM(("wfs?service=WFS&version=1.0.0&request=GetFeature&typeName=" + (getLayerId(MockData.PONDS))));
        Assert.assertEquals("ServiceExceptionReport", doc.getDocumentElement().getLocalName());
    }

    @Test
    public void testAdminGetFeature() throws Exception {
        Document doc = getAsDOM(((("wfs?service=WFS&version=1.0.0&request=GetFeature&typeName=" + (getLayerId(MockData.PONDS))) + "&authkey=") + (AuthencationKeyOWSTest.adminKey)));
        // print(doc);
        assertXpathEvaluatesTo("1", "count(//wfs:FeatureCollection)", doc);
        XpathEngine engine = XMLUnit.newXpathEngine();
        String url = engine.evaluate("//wfs:FeatureCollection/@xsi:schemaLocation", doc);
        Assert.assertTrue(url.contains(("&authkey=" + (AuthencationKeyOWSTest.adminKey))));
    }

    @Test
    public void testCiteGetFeature() throws Exception {
        Document doc = getAsDOM(((("wfs?service=WFS&version=1.0.0&request=GetFeature&typeName=" + (getLayerId(MockData.PONDS))) + "&authkey=") + (AuthencationKeyOWSTest.citeKey)));
        // print(doc);
        assertXpathEvaluatesTo("1", "count(//wfs:FeatureCollection)", doc);
        XpathEngine engine = XMLUnit.newXpathEngine();
        String url = engine.evaluate("//wfs:FeatureCollection/@xsi:schemaLocation", doc);
        Assert.assertTrue(url.contains(("&authkey=" + (AuthencationKeyOWSTest.citeKey))));
    }

    @Test
    public void testCiteGetFeatureCaseInsensitive() throws Exception {
        Document doc = getAsDOM(((("wfs?service=WFS&version=1.0.0&request=GetFeature&typeName=" + (getLayerId(MockData.PONDS))) + "&AUTHKEY=") + (AuthencationKeyOWSTest.citeKey)));
        // print(doc);
        assertXpathEvaluatesTo("1", "count(//wfs:FeatureCollection)", doc);
        XpathEngine engine = XMLUnit.newXpathEngine();
        String url = engine.evaluate("//wfs:FeatureCollection/@xsi:schemaLocation", doc);
        Assert.assertTrue(url.contains(("&authkey=" + (AuthencationKeyOWSTest.citeKey))));
    }

    /* Tests that URLs in the OpenLayers Map are correctly generated (see: GEOS-7295) */
    @Test
    public void testOpenLayersMapOutput() throws Exception {
        MockHttpServletResponse response = getAsServletResponse(((((((((((("cite/wms?service=WMS&" + ((("version=1.1.0&" + "request=GetMap&") + "bbox=-2.0,2.0,-1.0,6.0&") + "layers=")) + (BASIC_POLYGONS.getPrefix())) + ":") + (BASIC_POLYGONS.getLocalPart())) + "&") + "width=300&") + "height=300&") + "srs=EPSG:4326&") + "format=application/openlayers") + "&authkey=") + (AuthencationKeyOWSTest.citeKey)));
        byte[] responseContent = getBinary(response);
        String htmlDoc = new String(responseContent, "UTF-8");
        Assert.assertTrue(((htmlDoc.indexOf(("http://localhost:8080/geoserver/cite/wms?authkey=" + (AuthencationKeyOWSTest.citeKey)))) > 0));
    }
}

