/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wcs2_0.kvp;


import WcsExceptionCode.InvalidParameterValue;
import org.geoserver.wcs.WCSInfo;
import org.geoserver.wcs2_0.WCSTestSupport;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import static junit.framework.Assert.assertEquals;


/**
 * Test for {@link GetCapabilities}
 *
 * @author Simone Giannecchini, GeoSolutions
 */
public class GetCapabilitiesTest extends WCSTestSupport {
    @Test
    public void testBasicKVP() throws Exception {
        Document dom = getAsDOM("wcs?request=GetCapabilities&service=WCS");
        // print(dom);
        checkFullCapabilitiesDocument(dom);
    }

    @Test
    public void testCase() throws Exception {
        Document dom = getAsDOM("wcs?request=GetCapabilities&service=wCS");
        // print(dom);
        // check that we have the crs extension
        assertXpathEvaluatesTo("1", "count(//ows:ExceptionReport)", dom);
        assertXpathEvaluatesTo("1", "count(//ows:ExceptionReport//ows:Exception)", dom);
        assertXpathEvaluatesTo("1", "count(//ows:ExceptionReport//ows:Exception[@exceptionCode='InvalidParameterValue'])", dom);
        assertXpathEvaluatesTo("1", "count(//ows:ExceptionReport//ows:Exception[@locator='wCS'])", dom);
    }

    @Test
    public void testLimitedSRS() throws Exception {
        // check we support a lot of SRS by default
        Document dom = getAsDOM("wcs?request=GetCapabilities&service=WCS");
        // print(dom);
        NodeList list = WCSTestSupport.xpath.getMatchingNodes("//wcs:ServiceMetadata/wcs:Extension/wcscrs:crsSupported", dom);
        Assert.assertTrue(((list.getLength()) > 1000));
        // setup limited list
        WCSInfo service = getGeoServer().getService(WCSInfo.class);
        service.getSRS().add("4326");
        service.getSRS().add("32632");
        getGeoServer().save(service);
        dom = getAsDOM("wcs?request=GetCapabilities&service=WCS");
        // print(dom);
        list = WCSTestSupport.xpath.getMatchingNodes("//wcs:ServiceMetadata/wcs:Extension/wcscrs:crsSupported", dom);
        junit.framework.Assert.assertEquals(2, list.getLength());
    }

    @Test
    public void testSectionsBogus() throws Exception {
        Document dom = getAsDOM("wcs?request=GetCapabilities&service=WCS&sections=Bogus");
        Element root = dom.getDocumentElement();
        assertEquals("ows:ExceptionReport", root.getNodeName());
        assertEquals("2.0.0", root.getAttribute("version"));
        assertEquals("http://www.opengis.net/ows/2.0", root.getAttribute("xmlns:ows"));
        assertXpathEvaluatesTo(InvalidParameterValue.toString(), "/ows:ExceptionReport/ows:Exception/@exceptionCode", dom);
    }

    @Test
    public void testSectionsAll() throws Exception {
        Document dom = getAsDOM("wcs?request=GetCapabilities&service=WCS&sections=All");
        assertXpathEvaluatesTo("1", "count(//ows:ServiceIdentification)", dom);
        assertXpathEvaluatesTo("1", "count(//ows:ServiceProvider)", dom);
        assertXpathEvaluatesTo("1", "count(//ows:OperationsMetadata)", dom);
        assertXpathEvaluatesTo("1", "count(//wcs:Contents)", dom);
    }

    @Test
    public void testAcceptVersions() throws Exception {
        Document dom = getAsDOM("wcs?request=GetCapabilities&service=WCS&acceptversions=2.0.1");
        // make sure no exception is thrown
        assertXpathEvaluatesTo("0", "count(//ows:ExceptionReport)", dom);
        assertXpathEvaluatesTo("0", "count(//ows:ExceptionReport//ows:Exception)", dom);
        assertXpathEvaluatesTo("0", "count(//ows:ExceptionReport//ows:Exception[@exceptionCode='InvalidParameterValue'])", dom);
        assertXpathEvaluatesTo("0", "count(//ows:ExceptionReport//ows:Exception[@locator='wCS'])", dom);
    }
}

