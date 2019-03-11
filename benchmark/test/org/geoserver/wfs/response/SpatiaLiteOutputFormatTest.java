/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wfs.response;


import java.io.ByteArrayInputStream;
import org.geoserver.wfs.WFSTestSupport;
import org.geotools.data.DataStore;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.junit.Assert;
import org.junit.Test;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.Point;
import org.springframework.mock.web.MockHttpServletResponse;


/**
 * Test the SpatiaLiteOutputFormat WFS extension.
 *
 * @author Pablo Velazquez, Geotekne, info@geotekne.com
 * @author Jose Macchi, Geotekne, jmacchi@geotekne.com
 */
public class SpatiaLiteOutputFormatTest extends WFSTestSupport {
    static Boolean SKIPPED = null;

    /**
     * Test a request with multiple layers.
     */
    @Test
    public void testMultiResponse() throws Exception {
        MockHttpServletResponse resp = getAsServletResponse("wfs?request=GetFeature&typeName=Points,MPoints&outputFormat=spatialite");
        DataStore ds = loadData(resp);
        Assert.assertEquals(2, ds.getTypeNames().length);
        Assert.assertEquals(Point.class, ds.getSchema("Points").getGeometryDescriptor().getType().getBinding());
        Assert.assertEquals(MultiPoint.class, ds.getSchema("MPoints").getGeometryDescriptor().getType().getBinding());
    }

    /**
     * Test SPATIALITE Mime format.
     */
    @Test
    public void testMIMEOutput() throws Exception {
        MockHttpServletResponse resp = getAsServletResponse("wfs?request=GetFeature&typeName=Points&outputFormat=spatialite");
        Assert.assertEquals("application/zip", resp.getContentType());
    }

    /**
     * Test the content disposition
     */
    @Test
    public void testContentDisposition() throws Exception {
        MockHttpServletResponse resp = getAsServletResponse("wfs?request=GetFeature&typeName=Points&outputFormat=spatialite");
        Assert.assertEquals("attachment; filename=Points.db.zip", resp.getHeader("Content-Disposition"));
    }

    /**
     * Test if exist WFS Error, checking for Mime Type. If Mime Type is "application/xml", then an
     * error has occurred
     */
    @Test
    public void testWFSError() throws Exception {
        MockHttpServletResponse resp = getAsServletResponse("wfs?request=GetFeature&typeName=Points&outputFormat=spatialite");
        Assert.assertNotSame("application/xml", resp.getContentType());
    }

    /**
     * Test not null content.
     */
    @Test
    public void testContentNotNull() throws Exception {
        MockHttpServletResponse resp = getAsServletResponse("wfs?request=GetFeature&typeName=Points&outputFormat=spatialite");
        ByteArrayInputStream sResponse = getBinaryInputStream(resp);
        int dataLengh = sResponse.available();
        boolean contentNull = true;
        byte[] data = new byte[dataLengh];
        sResponse.read(data);
        for (byte aByte : data)
            if (aByte != 0) {
                contentNull = false;
                break;
            }

        Assert.assertFalse(contentNull);
    }

    /**
     * Test a Point geometry.
     */
    @Test
    public void testPoints() throws Exception {
        MockHttpServletResponse resp = getAsServletResponse("wfs?request=GetFeature&typeName=Points&outputFormat=spatialite");
        DataStore ds = loadData(resp);
        try {
            SimpleFeatureCollection fc = ds.getFeatureSource("Points").getFeatures();
            checkFeatures(getFeatureSource(MockData.POINTS).getFeatures(), fc);
        } finally {
            ds.dispose();
        }
    }

    // for some odd reason this test does not work... TODO: further investigate
    // /**
    // * Test a MultiPoint geometry.
    // //     */
    // public void testMultiPoints() throws Exception {
    // MockHttpServletResponse resp = getAsServletResponse(
    // "wfs?request=GetFeature&typeName=MPoints&outputFormat=spatialite");
    // DataStore ds = loadData(resp);
    // try {
    // SimpleFeatureCollection fc = ds.getFeatureSource("MPoints").getFeatures();
    // checkFeatures(getFeatureSource(MockData.MPOINTS).getFeatures(), fc);
    // }
    // finally {
    // ds.dispose();
    // }
    // }
    /**
     * Test a LineString geometry.
     */
    @Test
    public void testLines() throws Exception {
        MockHttpServletResponse resp = getAsServletResponse("wfs?request=GetFeature&typeName=Lines&outputFormat=spatialite");
        DataStore ds = loadData(resp);
        try {
            SimpleFeatureCollection fc = ds.getFeatureSource("Lines").getFeatures();
            checkFeatures(getFeatureSource(MockData.LINES).getFeatures(), fc);
        } finally {
            ds.dispose();
        }
    }

    /**
     * Test a MultiLineString geometry.
     */
    @Test
    public void testMultiLines() throws Exception {
        MockHttpServletResponse resp = getAsServletResponse("wfs?request=GetFeature&typeName=MLines&outputFormat=spatialite");
        DataStore ds = loadData(resp);
        try {
            SimpleFeatureCollection fc = ds.getFeatureSource("MLines").getFeatures();
            checkFeatures(getFeatureSource(MockData.MLINES).getFeatures(), fc);
        } finally {
            ds.dispose();
        }
    }

    /**
     * Test a Polygon geometry.
     */
    @Test
    public void testPolygons() throws Exception {
        MockHttpServletResponse resp = getAsServletResponse("wfs?request=GetFeature&typeName=Polygons&outputFormat=spatialite");
        DataStore ds = loadData(resp);
        try {
            SimpleFeatureCollection fc = ds.getFeatureSource("Polygons").getFeatures();
            checkFeatures(getFeatureSource(MockData.POLYGONS).getFeatures(), fc);
        } finally {
            ds.dispose();
        }
    }

    /**
     * Test a MultiPolygon geometry.
     */
    @Test
    public void testMultiPolygons() throws Exception {
        MockHttpServletResponse resp = getAsServletResponse("wfs?request=GetFeature&typeName=MPolygons&outputFormat=spatialite");
        DataStore ds = loadData(resp);
        try {
            SimpleFeatureCollection fc = ds.getFeatureSource("MPolygons").getFeatures();
            checkFeatures(getFeatureSource(MockData.MPOLYGONS).getFeatures(), fc);
        } finally {
            ds.dispose();
        }
    }

    /**
     * Test format option FILENAME.
     */
    @Test
    public void testCustomFileName() throws Exception {
        MockHttpServletResponse resp = getAsServletResponse("wfs?request=GetFeature&format_options=FILENAME:customName.db&typeName=Points&outputFormat=spatialite");
        String cd = resp.getHeader("Content-Disposition");
        Assert.assertTrue(cd.contains("filename=customName.db"));
    }
}

