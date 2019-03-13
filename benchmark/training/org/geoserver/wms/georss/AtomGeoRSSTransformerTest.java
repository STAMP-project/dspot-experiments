/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wms.georss;


import AtomGeoRSSTransformer.GeometryEncoding.LATLONG;
import AtomGeoRSSTransformer.GeometryEncoding.SIMPLE;
import MockData.BASIC_POLYGONS;
import Query.ALL;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.geoserver.wms.WMSMapContent;
import org.geoserver.wms.WMSTestSupport;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


public class AtomGeoRSSTransformerTest extends WMSTestSupport {
    static WMSMapContent map;

    @Test
    public void testLatLongInternal() throws Exception {
        AtomGeoRSSTransformer tx = new AtomGeoRSSTransformer(getWMS());
        tx.setGeometryEncoding(LATLONG);
        tx.setIndentation(2);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        tx.transform(AtomGeoRSSTransformerTest.map, output);
        DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = docBuilder.parse(new ByteArrayInputStream(output.toByteArray()));
        Element element = document.getDocumentElement();
        Assert.assertEquals("feed", element.getNodeName());
        NodeList entries = element.getElementsByTagName("entry");
        int n = getFeatureSource(BASIC_POLYGONS).getCount(ALL);
        Assert.assertEquals(n, entries.getLength());
        for (int i = 0; i < (entries.getLength()); i++) {
            Element entry = ((Element) (entries.item(i)));
            Assert.assertEquals(1, entry.getElementsByTagName("geo:lat").getLength());
            Assert.assertEquals(1, entry.getElementsByTagName("geo:long").getLength());
        }
    }

    @Test
    public void testLatLongWMS() throws Exception {
        Document document = getAsDOM(((("wms/reflect?format_options=encoding:latlong&format=application/atom+xml&layers=" + (BASIC_POLYGONS.getPrefix())) + ":") + (BASIC_POLYGONS.getLocalPart())));
        Element element = document.getDocumentElement();
        Assert.assertEquals("feed", element.getNodeName());
        NodeList entries = element.getElementsByTagName("entry");
        int n = getFeatureSource(BASIC_POLYGONS).getCount(ALL);
        Assert.assertEquals(n, entries.getLength());
        for (int i = 0; i < (entries.getLength()); i++) {
            Element entry = ((Element) (entries.item(i)));
            Assert.assertEquals(1, entry.getElementsByTagName("geo:lat").getLength());
            Assert.assertEquals(1, entry.getElementsByTagName("geo:long").getLength());
        }
    }

    @Test
    public void testSimpleInternal() throws Exception {
        AtomGeoRSSTransformer tx = new AtomGeoRSSTransformer(getWMS());
        tx.setGeometryEncoding(SIMPLE);
        tx.setIndentation(2);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        tx.transform(AtomGeoRSSTransformerTest.map, output);
        DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = docBuilder.parse(new ByteArrayInputStream(output.toByteArray()));
        Element element = document.getDocumentElement();
        Assert.assertEquals("feed", element.getNodeName());
        NodeList entries = element.getElementsByTagName("entry");
        int n = getFeatureSource(BASIC_POLYGONS).getCount(ALL);
        Assert.assertEquals(n, entries.getLength());
        for (int i = 0; i < (entries.getLength()); i++) {
            Element entry = ((Element) (entries.item(i)));
            Assert.assertEquals(1, entry.getElementsByTagName("georss:where").getLength());
            Assert.assertEquals(1, entry.getElementsByTagName("georss:polygon").getLength());
        }
    }

    @Test
    public void testSimpleWMS() throws Exception {
        Document document = getAsDOM(((("wms/reflect?format_options=encoding:simple&format=application/atom+xml&layers=" + (BASIC_POLYGONS.getPrefix())) + ":") + (BASIC_POLYGONS.getLocalPart())));
        Element element = document.getDocumentElement();
        Assert.assertEquals("feed", element.getNodeName());
        NodeList entries = element.getElementsByTagName("entry");
        int n = getFeatureSource(BASIC_POLYGONS).getCount(ALL);
        Assert.assertEquals(n, entries.getLength());
        for (int i = 0; i < (entries.getLength()); i++) {
            Element entry = ((Element) (entries.item(i)));
            Assert.assertEquals(1, entry.getElementsByTagName("georss:where").getLength());
            Assert.assertEquals(1, entry.getElementsByTagName("georss:polygon").getLength());
        }
    }

    @Test
    public void testGmlWMS() throws Exception {
        Document document = getAsDOM(((("wms/reflect?format_options=encoding:gml&format=application/atom+xml&layers=" + (BASIC_POLYGONS.getPrefix())) + ":") + (BASIC_POLYGONS.getLocalPart())));
        Element element = document.getDocumentElement();
        Assert.assertEquals("feed", element.getNodeName());
        NodeList entries = element.getElementsByTagName("entry");
        int n = getFeatureSource(BASIC_POLYGONS).getCount(ALL);
        Assert.assertEquals(n, entries.getLength());
        for (int i = 0; i < (entries.getLength()); i++) {
            Element entry = ((Element) (entries.item(i)));
            Assert.assertEquals(1, entry.getElementsByTagName("georss:where").getLength());
            Assert.assertEquals(1, entry.getElementsByTagName("gml:Polygon").getLength());
        }
    }
}

