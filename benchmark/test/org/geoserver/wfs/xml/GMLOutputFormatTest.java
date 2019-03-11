/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wfs.xml;


import MockData.BASIC_POLYGONS;
import WFS.NAMESPACE;
import org.geoserver.wfs.WFSTestSupport;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;


public class GMLOutputFormatTest extends WFSTestSupport {
    private int defaultNumDecimals = -1;

    private boolean defaultForceDecimal = false;

    private boolean defaultPadWithZeros = false;

    @Test
    public void testGML2() throws Exception {
        Document dom = getAsDOM(((("wfs?request=getfeature&version=1.0.0&outputFormat=gml2&typename=" + (BASIC_POLYGONS.getPrefix())) + ":") + (BASIC_POLYGONS.getLocalPart())));
        Assert.assertEquals("FeatureCollection", dom.getDocumentElement().getLocalName());
        Assert.assertNotNull(getFirstElementByTagName(dom, "gml:outerBoundaryIs"));
        Assert.assertNull(getFirstElementByTagName(dom, "gml:exterior"));
        dom = getAsDOM(((("wfs?request=getfeature&version=1.1.0&outputFormat=gml2&typename=" + (BASIC_POLYGONS.getPrefix())) + ":") + (BASIC_POLYGONS.getLocalPart())));
        Assert.assertEquals("FeatureCollection", dom.getDocumentElement().getLocalName());
        Assert.assertNotNull(getFirstElementByTagName(dom, "gml:outerBoundaryIs"));
        Assert.assertNull(getFirstElementByTagName(dom, "gml:exterior"));
        dom = getAsDOM(((("wfs?request=getfeature&version=1.0.0&outputFormat=text/xml; subtype%3Dgml/2.1.2&typename=" + (BASIC_POLYGONS.getPrefix())) + ":") + (BASIC_POLYGONS.getLocalPart())));
        Assert.assertEquals("FeatureCollection", dom.getDocumentElement().getLocalName());
        Assert.assertNotNull(getFirstElementByTagName(dom, "gml:outerBoundaryIs"));
        Assert.assertNull(getFirstElementByTagName(dom, "gml:exterior"));
        dom = getAsDOM(((("wfs?request=getfeature&version=1.1.0&outputFormat=text/xml; subtype%3Dgml/2.1.2&typename=" + (BASIC_POLYGONS.getPrefix())) + ":") + (BASIC_POLYGONS.getLocalPart())));
        Assert.assertEquals("FeatureCollection", dom.getDocumentElement().getLocalName());
        Assert.assertNotNull(getFirstElementByTagName(dom, "gml:outerBoundaryIs"));
        Assert.assertNull(getFirstElementByTagName(dom, "gml:exterior"));
    }

    @Test
    public void testGML2CoordinatesFormatting() throws Exception {
        enableCoordinatesFormatting();
        Document dom = getAsDOM(((("wfs?request=getfeature&version=1.0.0&outputFormat=gml2&typename=" + (BASIC_POLYGONS.getPrefix())) + ":") + (BASIC_POLYGONS.getLocalPart())));
        Assert.assertEquals("-2.0000,-1.0000 2.0000,6.0000", dom.getElementsByTagName("gml:coordinates").item(0).getTextContent());
    }

    @Test
    public void testGML2GZIP() throws Exception {
        // InputStream input = get(
        // "wfs?request=getfeature&version=1.0.0&outputFormat=gml2-gzip&typename=" +
        // MockData.BASIC_POLYGONS.getPrefix() + ":" +
        // MockData.BASIC_POLYGONS.getLocalPart());
        // GZIPInputStream zipped = new GZIPInputStream( input );
        // 
        // Document dom = dom( zipped );
        // zipped.close();
        // 
        // assertEquals( "FeatureCollection", dom.getDocumentElement().getLocalName() );
        // assertNotNull( getFirstElementByTagName(dom, "gml:outerBoundaryIs"));
        // assertNull( getFirstElementByTagName(dom, "gml:exterior"));
    }

    @Test
    public void testGML3() throws Exception {
        Document dom = getAsDOM(((("wfs?request=getfeature&version=1.0.0&outputFormat=gml3&typename=" + (BASIC_POLYGONS.getPrefix())) + ":") + (BASIC_POLYGONS.getLocalPart())));
        Assert.assertEquals("FeatureCollection", dom.getDocumentElement().getLocalName());
        Assert.assertNull(getFirstElementByTagName(dom, "gml:outerBoundaryIs"));
        Assert.assertNotNull(getFirstElementByTagName(dom, "gml:exterior"));
        dom = getAsDOM(((("wfs?request=getfeature&version=1.1.0&outputFormat=gml3&typename=" + (BASIC_POLYGONS.getPrefix())) + ":") + (BASIC_POLYGONS.getLocalPart())));
        Assert.assertEquals("FeatureCollection", dom.getDocumentElement().getLocalName());
        Assert.assertNull(getFirstElementByTagName(dom, "gml:outerBoundaryIs"));
        Assert.assertNotNull(getFirstElementByTagName(dom, "gml:exterior"));
        dom = getAsDOM(((("wfs?request=getfeature&version=1.0.0&outputFormat=text/xml; subtype%3Dgml/3.1.1&typename=" + (BASIC_POLYGONS.getPrefix())) + ":") + (BASIC_POLYGONS.getLocalPart())));
        Assert.assertEquals("FeatureCollection", dom.getDocumentElement().getLocalName());
        Assert.assertNull(getFirstElementByTagName(dom, "gml:outerBoundaryIs"));
        Assert.assertNotNull(getFirstElementByTagName(dom, "gml:exterior"));
        dom = getAsDOM(((("wfs?request=getfeature&version=1.1.0&outputFormat=text/xml; subtype%3Dgml/3.1.1&typename=" + (BASIC_POLYGONS.getPrefix())) + ":") + (BASIC_POLYGONS.getLocalPart())));
        Assert.assertEquals("FeatureCollection", dom.getDocumentElement().getLocalName());
        Assert.assertNull(getFirstElementByTagName(dom, "gml:outerBoundaryIs"));
        Assert.assertNotNull(getFirstElementByTagName(dom, "gml:exterior"));
    }

    @Test
    public void testGML3CoordinatesFormatting() throws Exception {
        enableCoordinatesFormatting();
        Document dom = getAsDOM(((("wfs?request=getfeature&version=1.0.0&outputFormat=gml3&typename=" + (BASIC_POLYGONS.getPrefix())) + ":") + (BASIC_POLYGONS.getLocalPart())));
        Assert.assertEquals("-1.0000 0.0000 0.0000 1.0000 1.0000 0.0000 0.0000 -1.0000 -1.0000 0.0000", dom.getElementsByTagName("gml:posList").item(0).getTextContent());
    }

    @Test
    public void testGML32() throws Exception {
        Document dom = getAsDOM(((("wfs?request=getfeature&version=2.0.0&outputFormat=gml32&typename=" + (BASIC_POLYGONS.getPrefix())) + ":") + (BASIC_POLYGONS.getLocalPart())));
        Assert.assertEquals(NAMESPACE, dom.getDocumentElement().getNamespaceURI());
        Assert.assertEquals("FeatureCollection", dom.getDocumentElement().getLocalName());
    }

    @Test
    public void testGML32CoordinatesFormatting() throws Exception {
        enableCoordinatesFormatting();
        Document dom = getAsDOM(((("wfs?request=getfeature&version=2.0.0&outputFormat=gml32&typename=" + (BASIC_POLYGONS.getPrefix())) + ":") + (BASIC_POLYGONS.getLocalPart())));
        Assert.assertEquals("0.0000 -1.0000 1.0000 0.0000 0.0000 1.0000 -1.0000 0.0000 0.0000 -1.0000", dom.getElementsByTagName("gml:posList").item(0).getTextContent());
    }
}

