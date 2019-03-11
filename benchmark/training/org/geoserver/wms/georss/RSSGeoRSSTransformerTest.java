/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wms.georss;


import AtomGeoRSSTransformer.GeometryEncoding.LATLONG;
import GeoRSSTransformerBase.GeometryEncoding.SIMPLE;
import MockData.BASIC_POLYGONS;
import MockData.BUILDINGS;
import MockData.LINES;
import Query.ALL;
import java.io.File;
import java.io.FileOutputStream;
import org.geoserver.wms.WMSMapContent;
import org.geoserver.wms.WMSTestSupport;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.map.FeatureLayer;
import org.geotools.util.factory.GeoTools;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


public class RSSGeoRSSTransformerTest extends WMSTestSupport {
    FilterFactory ff = CommonFactoryFinder.getFilterFactory(GeoTools.getDefaultHints());

    @Test
    public void testChannelDescription() throws Exception {
        WMSMapContent map = new WMSMapContent(createGetMapRequest(BASIC_POLYGONS));
        map.addLayer(createMapLayer(BASIC_POLYGONS));
        map.layers().get(0).getUserData().put("abstract", "Test Abstract");
        Document document;
        try {
            document = getRSSResponse(map, LATLONG);
        } finally {
            map.dispose();
        }
        Element element = document.getDocumentElement();
        Assert.assertEquals("rss", element.getNodeName());
        Element channel = ((Element) (element.getElementsByTagName("channel").item(0)));
        NodeList description = channel.getElementsByTagName("description");
        Assert.assertEquals("Test Abstract", description.item(0).getChildNodes().item(0).getNodeValue());
    }

    @Test
    public void testLinkTemplate() throws Exception {
        WMSMapContent map = new WMSMapContent(createGetMapRequest(BASIC_POLYGONS));
        map.addLayer(createMapLayer(BASIC_POLYGONS));
        try {
            File linkFile = new File(((testData.getDataDirectoryRoot().getAbsolutePath()) + "/workspaces/cite/cite/BasicPolygons/link.ftl"));
            FileOutputStream out = new FileOutputStream(linkFile);
            out.write("http://dummp.com".getBytes());
            out.close();
        } catch (Exception e) {
            System.out.println(("Error writing link.ftl: " + e));
        }
        Document document;
        try {
            document = getRSSResponse(map, LATLONG);
        } finally {
            map.dispose();
        }
        Element element = document.getDocumentElement();
        Assert.assertEquals("rss", element.getNodeName());
        NodeList items = element.getElementsByTagName("item");
        int n = getFeatureSource(BASIC_POLYGONS).getCount(ALL);
        Assert.assertEquals(n, items.getLength());
        for (int i = 0; i < (items.getLength()); i++) {
            Element item = ((Element) (items.item(i)));
            Assert.assertThat(item.getElementsByTagName("link").item(0).getTextContent(), Matchers.containsString("http://dummp.com"));
        }
    }

    @Test
    public void testLatLongInternal() throws Exception {
        WMSMapContent map = new WMSMapContent(createGetMapRequest(BASIC_POLYGONS));
        map.addLayer(createMapLayer(BASIC_POLYGONS));
        Document document;
        try {
            document = getRSSResponse(map, LATLONG);
        } finally {
            map.dispose();
        }
        Element element = document.getDocumentElement();
        Assert.assertEquals("rss", element.getNodeName());
        NodeList items = element.getElementsByTagName("item");
        int n = getFeatureSource(BASIC_POLYGONS).getCount(ALL);
        Assert.assertEquals(n, items.getLength());
        for (int i = 0; i < (items.getLength()); i++) {
            Element item = ((Element) (items.item(i)));
            Assert.assertEquals(1, item.getElementsByTagName("geo:lat").getLength());
            Assert.assertEquals(1, item.getElementsByTagName("geo:long").getLength());
        }
    }

    @Test
    public void testLatLongWMS() throws Exception {
        Document document = getAsDOM(((("wms/reflect?format_options=encoding:latlong&format=application/rss+xml&layers=" + (BASIC_POLYGONS.getPrefix())) + ":") + (BASIC_POLYGONS.getLocalPart())));
        Element element = document.getDocumentElement();
        Assert.assertEquals("rss", element.getNodeName());
        NodeList items = element.getElementsByTagName("item");
        int n = getFeatureSource(BASIC_POLYGONS).getCount(ALL);
        Assert.assertEquals(n, items.getLength());
        for (int i = 0; i < (items.getLength()); i++) {
            Element item = ((Element) (items.item(i)));
            Assert.assertEquals(1, item.getElementsByTagName("geo:lat").getLength());
            Assert.assertEquals(1, item.getElementsByTagName("geo:long").getLength());
        }
    }

    @Test
    public void testSimpleInternal() throws Exception {
        WMSMapContent map = new WMSMapContent(createGetMapRequest(BASIC_POLYGONS));
        map.addLayer(createMapLayer(BASIC_POLYGONS));
        Document document;
        try {
            // print(document);
            document = getRSSResponse(map, SIMPLE);
        } finally {
            map.dispose();
        }
        Element element = document.getDocumentElement();
        Assert.assertEquals("rss", element.getNodeName());
        NodeList entries = element.getElementsByTagName("item");
        int n = getFeatureSource(BASIC_POLYGONS).getCount(ALL);
        Assert.assertEquals(n, entries.getLength());
        for (int i = 0; i < (entries.getLength()); i++) {
            Element entry = ((Element) (entries.item(i)));
            Assert.assertEquals(1, entry.getElementsByTagName("georss:polygon").getLength());
        }
    }

    @Test
    public void testSimpleWMS() throws Exception {
        Document document = getAsDOM(((("wms/reflect?format_options=encoding:simple&format=application/rss+xml&layers=" + (BASIC_POLYGONS.getPrefix())) + ":") + (BASIC_POLYGONS.getLocalPart())));
        Element element = document.getDocumentElement();
        Assert.assertEquals("rss", element.getNodeName());
        NodeList entries = element.getElementsByTagName("item");
        int n = getFeatureSource(BASIC_POLYGONS).getCount(ALL);
        Assert.assertEquals(n, entries.getLength());
        for (int i = 0; i < (entries.getLength()); i++) {
            Element entry = ((Element) (entries.item(i)));
            Assert.assertEquals(1, entry.getElementsByTagName("georss:polygon").getLength());
        }
    }

    @Test
    public void testGmlWMS() throws Exception {
        Document document = getAsDOM(((("wms/reflect?format_options=encoding:gml&format=application/rss+xml&layers=" + (BASIC_POLYGONS.getPrefix())) + ":") + (BASIC_POLYGONS.getLocalPart())));
        Element element = document.getDocumentElement();
        Assert.assertEquals("rss", element.getNodeName());
        NodeList entries = element.getElementsByTagName("item");
        int n = getFeatureSource(BASIC_POLYGONS).getCount(ALL);
        Assert.assertEquals(n, entries.getLength());
        for (int i = 0; i < (entries.getLength()); i++) {
            Element entry = ((Element) (entries.item(i)));
            Assert.assertEquals(1, entry.getElementsByTagName("gml:Polygon").getLength());
        }
    }

    @Test
    public void testFilter() throws Exception {
        // Set up a map context with a filtered layer
        WMSMapContent map = new WMSMapContent(createGetMapRequest(BUILDINGS));
        Document document;
        try {
            FeatureLayer layer = ((FeatureLayer) (createMapLayer(BUILDINGS)));
            Filter f = ff.equals(ff.property("ADDRESS"), ff.literal("215 Main Street"));
            layer.setQuery(new org.geotools.data.Query(BUILDINGS.getLocalPart(), f));
            map.addLayer(layer);
            document = getRSSResponse(map, LATLONG);
        } finally {
            map.dispose();
        }
        NodeList items = document.getDocumentElement().getElementsByTagName("item");
        Assert.assertEquals(1, items.getLength());
    }

    @Test
    public void testReproject() throws Exception {
        // Set up a map context with a projected layer
        WMSMapContent map = new WMSMapContent(createGetMapRequest(LINES));
        map.addLayer(createMapLayer(LINES));
        Document document;
        try {
            document = getRSSResponse(map, LATLONG);
        } finally {
            map.dispose();
        }
        NodeList items = document.getDocumentElement().getElementsByTagName("item");
        // check all items are there
        Assert.assertEquals(1, items.getLength());
        // check coordinates are in wgs84, originals aren't
        for (int i = 0; i < (items.getLength()); i++) {
            Element item = ((Element) (items.item(i)));
            double lat = Double.parseDouble(getOrdinate(item, "geo:lat"));
            double lon = Double.parseDouble(getOrdinate(item, "geo:long"));
            Assert.assertTrue(("Expected valid latitude value: " + lat), ((lat >= (-90)) && (lat <= 90)));
            Assert.assertTrue(("Expected valid longitude value: " + lon), ((lon >= (-180)) && (lon <= 180)));
        }
    }
}

