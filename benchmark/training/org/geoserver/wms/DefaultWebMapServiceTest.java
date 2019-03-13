/**
 * (c) 2014 - 2015 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wms;


import DefaultWebMapService.FORMAT;
import MockData.BASIC_POLYGONS;
import MockData.BRIDGES;
import MockData.STREAMS;
import java.util.ArrayList;
import java.util.List;
import org.geotools.referencing.CRS;
import org.junit.Assert;
import org.junit.Test;
import org.locationtech.jts.geom.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;


public class DefaultWebMapServiceTest extends WMSTestSupport {
    /**
     * This is just a very basic test, mostly testing defaults
     */
    @Test
    public void testBasic() throws Exception {
        GetMapRequest mockGMR = createGetMapRequest(BASIC_POLYGONS);
        /* Create a request */
        GetMapRequest request = new GetMapRequest();
        /* Create the reflector */
        DefaultWebMapService reflector = new DefaultWebMapService(getWMS());
        /* Run the reflector */
        request.setLayers(mockGMR.getLayers());
        request.setFormat(FORMAT);
        reflector.autoSetBoundsAndSize(request);
        CoordinateReferenceSystem crs = request.getCrs();
        String srs = request.getSRS();
        Envelope bbox = request.getBbox();
        String format = request.getFormat();
        int width = request.getWidth();
        int height = request.getHeight();
        String crsString = crs.getName().toString();
        Assert.assertTrue("EPSG:WGS 84".equalsIgnoreCase(crsString));
        Assert.assertTrue("EPSG:4326".equalsIgnoreCase(srs));
        // mockGMR.getBbox() actually returns (-180 , 90 , -90 , 180 ) <- foo
        Assert.assertTrue((((((bbox.getMinX()) == (-180.0)) && ((bbox.getMaxX()) == 180.0)) && ((bbox.getMinY()) == (-90.0))) && ((bbox.getMaxY()) == 90.0)));
        Assert.assertEquals("image/png", format);
        Assert.assertEquals(width, 768);
        Assert.assertEquals(height, 384);
    }

    /**
     * Tests basic reprojection
     */
    @Test
    public void testReprojection() throws Exception {
        GetMapRequest mockGMR = createGetMapRequest(BASIC_POLYGONS);
        /* Create a request */
        GetMapRequest request = new GetMapRequest();
        /* Create the reflector */
        DefaultWebMapService reflector = new DefaultWebMapService(getWMS());
        /* Run the reflector */
        request.setSRS("EPSG:41001");
        request.setCrs(CRS.decode("EPSG:41001"));
        request.setLayers(mockGMR.getLayers());
        request.setFormat("image/gif");
        reflector.autoSetBoundsAndSize(request);
        CoordinateReferenceSystem crs = request.getCrs();
        String srs = request.getSRS();
        Envelope bbox = request.getBbox();
        String format = request.getFormat();
        int width = request.getWidth();
        int height = request.getHeight();
        String crsString = crs.getName().toString();
        Assert.assertTrue("WGS84 / Simple Mercator".equalsIgnoreCase(crsString));
        Assert.assertTrue("EPSG:41001".equalsIgnoreCase(srs));
        // mockGMR.getBbox() actually returns (-180 , 90 , -90 , 180 ) <- foo
        Assert.assertTrue((((((Math.abs(((bbox.getMinX()) + 1.9236008009077676E7))) < 1.0E-4) && ((Math.abs(((bbox.getMinY()) + 2.2026354993694823E7))) < 1.0E-4)) && ((Math.abs(((bbox.getMaxX()) - 1.9236008009077676E7))) < 1.0E-4)) && ((Math.abs(((bbox.getMaxY()) - 2.2026354993694823E7))) < 1.0E-4)));
        Assert.assertEquals("image/gif", format);
        Assert.assertEquals(670, width);
        Assert.assertEquals(768, height);
    }

    /**
     * This test is incomplete because I (arneke) had trouble finding mock data with proper bounding
     * boxes
     */
    @Test
    public void testAutoSetWidthHeight() throws Exception {
        GetMapRequest mockStreams = createGetMapRequest(BRIDGES);
        GetMapRequest mockBridges = createGetMapRequest(STREAMS);
        List<MapLayerInfo> mls = new ArrayList<MapLayerInfo>(2);
        mls.add(mockBridges.getLayers().get(0));
        mls.add(mockStreams.getLayers().get(0));
        /* Create a request */
        GetMapRequest request = new GetMapRequest();
        /* Create the reflector */
        DefaultWebMapService reflector = new DefaultWebMapService(getWMS());
        /* Run the reflector */
        request.setSRS("EPSG:41001");
        request.setCrs(CRS.decode("EPSG:41001"));
        request.setLayers(mls);
        request.setFormat("image/gif");
        reflector.autoSetBoundsAndSize(request);
        CoordinateReferenceSystem crs = request.getCrs();
        String srs = request.getSRS();
        Envelope bbox = request.getBbox();
        String format = request.getFormat();
        int width = request.getWidth();
        int height = request.getHeight();
        String crsString = crs.getName().toString();
        Assert.assertTrue("WGS84 / Simple Mercator".equalsIgnoreCase(crsString));
        Assert.assertTrue("EPSG:41001".equalsIgnoreCase(srs));
        Assert.assertTrue((((((Math.abs(((bbox.getMinX()) + 1.9236008009077676E7))) < 1.0E-4) && ((Math.abs(((bbox.getMinY()) + 2.2026354993694823E7))) < 1.0E-4)) && ((Math.abs(((bbox.getMaxX()) - 1.9236008009077676E7))) < 1.0E-4)) && ((Math.abs(((bbox.getMaxY()) - 2.2026354993694823E7))) < 1.0E-4)));
        Assert.assertEquals("image/gif", format);
        Assert.assertEquals(670, width);
        Assert.assertEquals(768, height);
    }

    /**
     * This test is incomplete because I (arneke) had trouble finding mock data with proper bounding
     * boxes
     */
    @Test
    public void testAutoSetWidthHeightOL() throws Exception {
        DefaultWebMapService reflector = new DefaultWebMapService(getWMS());
        // request too stretched east/west
        GetMapRequest request = createGetMapRequest(BRIDGES);
        request.setBbox(new Envelope((-180), 180, 0, 10));
        request.setFormat("application/openlayers");
        reflector.autoSetBoundsAndSize(request);
        Assert.assertEquals(768, request.getWidth());
        Assert.assertEquals(330, request.getHeight());
        // request too stretched south/north
        request = createGetMapRequest(BRIDGES);
        request.setBbox(new Envelope((-0), 10, (-90), 90));
        request.setFormat("application/openlayers");
        reflector.autoSetBoundsAndSize(request);
        Assert.assertEquals(330, request.getWidth());
        Assert.assertEquals(768, request.getHeight());
    }

    @Test
    public void testAdvancedProjectionEnabled() {
        // Ensure that by default advanced projection handling is enabled
        Assert.assertTrue(getWMS().isAdvancedProjectionHandlingEnabled());
    }
}

