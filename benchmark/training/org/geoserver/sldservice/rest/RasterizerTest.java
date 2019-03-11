/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * Copyright (C) 2007-2008-2009 GeoSolutions S.A.S.
 *  http://www.geo-solutions.it
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.sldservice.rest;


import java.io.ByteArrayOutputStream;
import org.geoserver.catalog.LayerInfo;
import org.geoserver.rest.RestBaseController;
import org.geotools.styling.ColorMap;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.mock.web.MockHttpServletResponse;
import org.w3c.dom.Document;


public class RasterizerTest extends SLDServiceBaseTest {
    ClassPathXmlApplicationContext appContext;

    @Test
    public void testRasterizeWithNoLayer() throws Exception {
        final String restPath = (((RestBaseController.ROOT_PATH) + "/sldservice//") + (getServiceUrl())) + ".xml";
        MockHttpServletResponse response = getAsServletResponse(restPath);
        Assert.assertTrue(((response.getStatus()) == 404));
    }

    @Test
    public void testRasterizeWithNoParams() throws Exception {
        LayerInfo l = getCatalog().getLayerByName("wcs:World");
        Assert.assertEquals("raster", l.getDefaultStyle().getName());
        final String restPath = (((RestBaseController.ROOT_PATH) + "/sldservice/wcs:World/") + (getServiceUrl())) + ".xml";
        MockHttpServletResponse response = getAsServletResponse(restPath);
        Assert.assertTrue(((response.getStatus()) == 200));
        Document dom = getAsDOM(restPath, 200);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        print(dom, baos);
        Assert.assertTrue(((baos.toString().indexOf("<sld:ColorMap>")) > 0));
        checkColorMap(baos.toString(), 100);
    }

    @Test
    public void testRasterizeOptions() throws Exception {
        LayerInfo l = getCatalog().getLayerByName("wcs:World");
        Assert.assertEquals("raster", l.getDefaultStyle().getName());
        final String restPath = ((((RestBaseController.ROOT_PATH) + "/sldservice/wcs:World/") + (getServiceUrl())) + ".xml?") + "classes=5&min=10.0&max=50.0&digits=1&ramp=custom&startColor=0xFF0000&endColor=0x0000FF";
        MockHttpServletResponse response = getAsServletResponse(restPath);
        Assert.assertTrue(((response.getStatus()) == 200));
        Document dom = getAsDOM(restPath, 200);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        print(dom, baos);
        Assert.assertTrue(((baos.toString().indexOf("<sld:ColorMap>")) > 0));
        ColorMap map = checkColorMap(baos.toString(), 5);
        checkColorEntry(map.getColorMapEntries()[1], "#FF0000", "10.0", "1.0");
        checkColorEntry(map.getColorMapEntries()[5], "#0000FF", "50.0", "1.0");
    }

    @Test
    public void testRasterizeOptions2() throws Exception {
        LayerInfo l = getCatalog().getLayerByName("wcs:World");
        Assert.assertEquals("raster", l.getDefaultStyle().getName());
        final String restPath = ((((RestBaseController.ROOT_PATH) + "/sldservice/wcs:World/") + (getServiceUrl())) + ".xml?") + "classes=5&min=10.0&max=50.0&digits=2&ramp=custom&startColor=0xFF0000&endColor=0x0000FF";
        MockHttpServletResponse response = getAsServletResponse(restPath);
        Assert.assertTrue(((response.getStatus()) == 200));
        Document dom = getAsDOM(restPath, 200);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        print(dom, baos);
        Assert.assertTrue(((baos.toString().indexOf("<sld:ColorMap>")) > 0));
        ColorMap map = checkColorMap(baos.toString(), 5);
        checkColorEntry(map.getColorMapEntries()[1], "#FF0000", "10.00", "1.0");
        checkColorEntry(map.getColorMapEntries()[5], "#0000FF", "50.00", "1.0");
    }
}

