/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wfs.response;


import Ogr2OgrOutputFormat.formats;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.geoserver.test.GeoServerSystemTestSupport;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.w3c.dom.Document;


public class Ogr2OgrWfsTest extends GeoServerSystemTestSupport {
    @Test
    public void testCapabilities() throws Exception {
        String request = "wfs?request=GetCapabilities&version=1.0.0";
        Document dom = getAsDOM(request);
        // print(dom);
        // while we cannot know what formats are available, the other tests won't pass if KML is not
        // there
        assertXpathEvaluatesTo("1", "count(//wfs:GetFeature/wfs:ResultFormat/wfs:OGR-KML)", dom);
    }

    @Test
    public void testEmptyCapabilities() throws Exception {
        Ogr2OgrOutputFormat of = applicationContext.getBean(Ogr2OgrOutputFormat.class);
        of.clearFormats();
        String request = "wfs?request=GetCapabilities&version=1.0.0";
        Document dom = getAsDOM(request);
        // print(dom);
        // this used to NPE
        assertXpathEvaluatesTo("0", "count(//wfs:GetFeature/wfs:ResultFormat/wfs:OGR-KML)", dom);
        assertXpathEvaluatesTo("1", "count(//wfs:GetFeature/wfs:ResultFormat/wfs:SHAPE-ZIP)", dom);
    }

    @Test
    public void testSimpleRequest() throws Exception {
        String request = ("wfs?request=GetFeature&typename=" + (getLayerId(MockData.BUILDINGS))) + "&version=1.0.0&service=wfs&outputFormat=OGR-KML";
        MockHttpServletResponse resp = getAsServletResponse(request);
        // check content type
        Assert.assertEquals("application/vnd.google-earth.kml", resp.getContentType());
        Assert.assertEquals("inline; filename=Buildings.kml", resp.getHeader("Content-Disposition"));
        // read back
        Document dom = dom(getBinaryInputStream(resp));
        // print(dom);
        // some very light assumptions on the contents, since we
        // cannot control how ogr encodes the kml... let's just assess
        // it's kml with the proper number of features
        Assert.assertEquals("kml", dom.getDocumentElement().getTagName());
        Assert.assertEquals(2, dom.getElementsByTagName("Placemark").getLength());
    }

    @Test
    public void testSimpleRequestGeopackage() throws Exception {
        Assume.assumeTrue(formats.containsKey("OGR-GPKG"));
        String request = ("wfs?request=GetFeature&typename=" + (getLayerId(MockData.BUILDINGS))) + "&version=1.0.0&service=wfs&outputFormat=OGR-GPKG";
        MockHttpServletResponse resp = getAsServletResponse(request);
        // check content type
        Assert.assertEquals("application/octet-stream", resp.getContentType());
        Assert.assertEquals("attachment; filename=Buildings.db", resp.getHeader("Content-Disposition"));
    }

    @Test
    public void testSimpleRequest20() throws Exception {
        String request = ("wfs?request=GetFeature&typename=" + (getLayerId(MockData.BUILDINGS))) + "&version=2.0.0&service=wfs&outputFormat=OGR-KML&srsName=EPSG:4326";
        MockHttpServletResponse resp = getAsServletResponse(request);
        // check content type
        Assert.assertEquals("application/vnd.google-earth.kml", resp.getContentType());
        // read back
        Document dom = dom(getBinaryInputStream(resp));
        // print(dom);
        // some very light assumptions on the contents, since we
        // cannot control how ogr encodes the kml... let's just assess
        // it's kml with the proper number of features
        Assert.assertEquals("kml", dom.getDocumentElement().getTagName());
        Assert.assertEquals(2, dom.getElementsByTagName("Placemark").getLength());
    }

    @Test
    public void testDoubleRequest() throws Exception {
        String request = ((("wfs?request=GetFeature&typename=" + (getLayerId(MockData.BUILDINGS))) + ",") + (getLayerId(MockData.BRIDGES))) + "&version=1.0.0&service=wfs&outputFormat=OGR-KML";
        MockHttpServletResponse resp = getAsServletResponse(request);
        // check content type
        Assert.assertEquals("application/zip", resp.getContentType());
        // check content disposition
        Assert.assertEquals("attachment; filename=Buildings.zip", resp.getHeader("Content-Disposition"));
        // read back
        ZipInputStream zis = new ZipInputStream(getBinaryInputStream(resp));
        // get buildings entry
        ZipEntry entry = null;
        entry = zis.getNextEntry();
        while (entry != null) {
            if (entry.getName().equals("Buildings.kml")) {
                break;
            }
            entry = zis.getNextEntry();
        } 
        Assert.assertNotNull(entry);
        Assert.assertEquals("Buildings.kml", entry.getName());
        // parse the kml to check it's really xml...
        Document dom = dom(zis);
        // print(dom);
        // some very light assumptions on the contents, since we
        // cannot control how ogr encodes the kml... let's just assess
        // it's kml with the proper number of features
        Assert.assertEquals("kml", dom.getDocumentElement().getTagName());
        Assert.assertEquals(2, dom.getElementsByTagName("Placemark").getLength());
    }
}

