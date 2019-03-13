/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.rest.catalog;


import SLDHandler.VERSION_10;
import java.io.ByteArrayOutputStream;
import org.custommonkey.xmlunit.XpathEngine;
import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.SLDHandler;
import org.geoserver.catalog.StyleInfo;
import org.geoserver.catalog.Styles;
import org.geoserver.platform.GeoServerResourceLoader;
import org.geoserver.platform.resource.Resource;
import org.geoserver.rest.RestBaseController;
import org.geoserver.test.GeoServerSystemTestSupport;
import org.geotools.styling.Style;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;


public class YsldStyleControllerTest extends GeoServerSystemTestSupport {
    protected static Catalog catalog;

    protected static XpathEngine xp;

    @Test
    public void testRawPutYSLD() throws Exception {
        // step 1 create style info with correct format
        Catalog cat = getCatalog();
        Assert.assertNull("foo not available", cat.getStyleByName("foo"));
        String xml = "<style>" + ((("<name>foo</name>" + "<format>ysld</format>") + "<filename>foo.yaml</filename>") + "</style>");
        MockHttpServletResponse response = postAsServletResponse(((RestBaseController.ROOT_PATH) + "/styles"), xml);
        Assert.assertEquals(201, response.getStatus());
        Assert.assertNotNull(cat.getStyleByName("foo"));
        String content = newYSLD();
        response = putAsServletResponse("/rest/styles/foo?raw=true", content, YsldHandler.MIMETYPE);
        Assert.assertEquals(200, response.getStatus());
        GeoServerResourceLoader resources = YsldStyleControllerTest.catalog.getResourceLoader();
        Resource resource = resources.get("/styles/foo.yaml");
        String definition = new String(resource.getContents());
        Assert.assertTrue("is yaml", definition.contains("stroke-color: '#FF0000'"));
        StyleInfo styleInfo = YsldStyleControllerTest.catalog.getStyleByName("foo");
        Style s = styleInfo.getStyle();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        SLDHandler handler = new SLDHandler();
        handler.encode(Styles.sld(s), VERSION_10, false, out);
        content = new String(out.toByteArray());
        Assert.assertTrue(content.contains("<sld:Name>foo</sld:Name>"));
        YsldStyleControllerTest.catalog.remove(styleInfo);
    }

    @Test
    public void testPostYSLD() throws Exception {
        // step 1 create style info with correct format
        Catalog cat = getCatalog();
        Assert.assertNull("foo not available", cat.getStyleByName("foo"));
        String content = newYSLD();
        MockHttpServletResponse response = postAsServletResponse("/rest/styles?name=foo", content, YsldHandler.MIMETYPE);
        Assert.assertEquals(201, response.getStatus());
        GeoServerResourceLoader resources = YsldStyleControllerTest.catalog.getResourceLoader();
        Resource resource = resources.get("/styles/foo.yaml");
        String definition = new String(resource.getContents());
        Assert.assertTrue("is yaml", definition.contains("stroke-color: '#FF0000'"));
        StyleInfo styleInfo = YsldStyleControllerTest.catalog.getStyleByName("foo");
        Style s = styleInfo.getStyle();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        SLDHandler handler = new SLDHandler();
        handler.encode(Styles.sld(s), VERSION_10, false, out);
        content = new String(out.toByteArray());
        Assert.assertTrue(content.contains("<sld:Name>foo</sld:Name>"));
        YsldStyleControllerTest.catalog.remove(styleInfo);
    }

    @Test
    public void testPutYSLD() throws Exception {
        // step 1 create style info with correct format
        Catalog cat = getCatalog();
        Assert.assertNull(cat.getStyleByName("bar"));
        String xml = "<style>" + ((("<name>bar</name>" + "<format>ysld</format>") + "<filename>bar.yaml</filename>") + "</style>");
        MockHttpServletResponse response = postAsServletResponse(((RestBaseController.ROOT_PATH) + "/styles"), xml);
        Assert.assertEquals(201, response.getStatus());
        Assert.assertNotNull(cat.getStyleByName("bar"));
        String content = newYSLD();
        response = putAsServletResponse("/rest/styles/bar", content, YsldHandler.MIMETYPE);
        Assert.assertEquals(200, response.getStatus());
        GeoServerResourceLoader resources = YsldStyleControllerTest.catalog.getResourceLoader();
        Resource resource = resources.get("/styles/bar.yaml");
        String definition = new String(resource.getContents());
        Assert.assertTrue("is yaml", definition.contains("stroke-color: '#FF0000'"));
        StyleInfo styleInfo = YsldStyleControllerTest.catalog.getStyleByName("bar");
        Style s = styleInfo.getStyle();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        SLDHandler handler = new SLDHandler();
        handler.encode(Styles.sld(s), VERSION_10, false, out);
        content = new String(out.toByteArray());
        Assert.assertTrue(content.contains("<sld:Name>bar</sld:Name>"));
        YsldStyleControllerTest.catalog.remove(styleInfo);
    }
}

