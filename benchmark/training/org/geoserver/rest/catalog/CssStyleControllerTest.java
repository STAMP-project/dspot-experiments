/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.rest.catalog;


import CssHandler.MIME_TYPE;
import MediaType.TEXT_HTML_VALUE;
import SLDHandler.MIMETYPE_10;
import SLDHandler.VERSION_10;
import java.io.ByteArrayOutputStream;
import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.SLDHandler;
import org.geoserver.catalog.StyleInfo;
import org.geoserver.catalog.Styles;
import org.geoserver.platform.GeoServerResourceLoader;
import org.geoserver.platform.resource.Resource;
import org.geoserver.rest.RestBaseController;
import org.geoserver.test.GeoServerSystemTestSupport;
import org.geotools.styling.Style;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.util.xml.SimpleNamespaceContext;
import org.w3c.dom.Document;


public class CssStyleControllerTest extends GeoServerSystemTestSupport {
    protected static Catalog catalog;

    private static SimpleNamespaceContext namespaceContext;

    @Test
    public void getGetAsCSS() throws Exception {
        MockHttpServletResponse response = getAsServletResponse(((((RestBaseController.ROOT_PATH) + "/workspaces/") + (CssStyleControllerTest.catalog.getDefaultWorkspace().getName())) + "/styles/test.css"));
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals(MIME_TYPE, response.getContentType());
        String content = response.getContentAsString();
        Assert.assertEquals("* {stroke: red}", content);
    }

    @Test
    public void getGetAsSLD10() throws Exception {
        MockHttpServletResponse response = getAsServletResponse(((((RestBaseController.ROOT_PATH) + "/workspaces/") + (CssStyleControllerTest.catalog.getDefaultWorkspace().getName())) + "/styles/test.sld"));
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals(MIMETYPE_10, response.getContentType());
        Document dom = dom(new java.io.ByteArrayInputStream(response.getContentAsByteArray()));
        Assert.assertThat(dom, Matchers.hasXPath("//sld:LineSymbolizer/sld:Stroke/sld:CssParameter", CssStyleControllerTest.namespaceContext, Matchers.equalTo("#ff0000")));
    }

    @Test
    public void getGetAsHTML() throws Exception {
        MockHttpServletResponse response = getAsServletResponse(((((RestBaseController.ROOT_PATH) + "/workspaces/") + (CssStyleControllerTest.catalog.getDefaultWorkspace().getName())) + "/styles/test.html"));
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals(TEXT_HTML_VALUE, response.getContentType());
        String content = response.getContentAsString();
        Assert.assertThat(content, CoreMatchers.containsString((("<a href=\"http://localhost:8080/geoserver/rest/workspaces/" + (CssStyleControllerTest.catalog.getDefaultWorkspace().getName())) + "/styles/test.css\">test.css</a>")));
    }

    @Test
    public void testRawPutCSS() throws Exception {
        // step 1 create style info with correct format
        Catalog cat = getCatalog();
        Assert.assertNull("foo not available", cat.getStyleByName("foo"));
        String xml = "<style>" + ((("<name>foo</name>" + "<format>css</format>") + "<filename>foo.css</filename>") + "</style>");
        MockHttpServletResponse response = postAsServletResponse(((RestBaseController.ROOT_PATH) + "/styles"), xml);
        Assert.assertEquals(201, response.getStatus());
        Assert.assertNotNull(cat.getStyleByName("foo"));
        // step 2 define css
        String content = newCSS();
        response = putAsServletResponse("/rest/styles/foo?raw=true", content, MIME_TYPE);
        Assert.assertEquals(200, response.getStatus());
        GeoServerResourceLoader resources = CssStyleControllerTest.catalog.getResourceLoader();
        Resource resource = resources.get("/styles/foo.css");
        String definition = new String(resource.getContents());
        Assert.assertTrue("is css", definition.contains("stroke: red"));
        StyleInfo styleInfo = CssStyleControllerTest.catalog.getStyleByName("foo");
        Style s = styleInfo.getStyle();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        SLDHandler handler = new SLDHandler();
        handler.encode(Styles.sld(s), VERSION_10, false, out);
        content = new String(out.toByteArray());
        Assert.assertTrue(content.contains("<sld:Name>foo</sld:Name>"));
        CssStyleControllerTest.catalog.remove(styleInfo);
    }

    @Test
    public void testPostCSS() throws Exception {
        Catalog cat = getCatalog();
        Assert.assertNull("foo not available", cat.getStyleByName("foo"));
        String content = newCSS();
        MockHttpServletResponse response = postAsServletResponse("/rest/styles?name=foo", content, MIME_TYPE);
        Assert.assertEquals(201, response.getStatus());
        GeoServerResourceLoader resources = CssStyleControllerTest.catalog.getResourceLoader();
        Resource resource = resources.get("/styles/foo.css");
        String definition = new String(resource.getContents());
        Assert.assertTrue("is css", definition.contains("stroke: red"));
        StyleInfo styleInfo = CssStyleControllerTest.catalog.getStyleByName("foo");
        Style s = styleInfo.getStyle();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        SLDHandler handler = new SLDHandler();
        handler.encode(Styles.sld(s), VERSION_10, false, out);
        content = new String(out.toByteArray());
        Assert.assertTrue(content.contains("<sld:Name>foo</sld:Name>"));
        CssStyleControllerTest.catalog.remove(styleInfo);
    }

    @Test
    public void testPutCSS() throws Exception {
        // step 1 create style info with correct format
        Catalog cat = getCatalog();
        Assert.assertNull(cat.getStyleByName("bar"));
        String xml = "<style>" + ((("<name>bar</name>" + "<format>css</format>") + "<filename>bar.css</filename>") + "</style>");
        MockHttpServletResponse response = postAsServletResponse(((RestBaseController.ROOT_PATH) + "/styles"), xml);
        Assert.assertEquals(201, response.getStatus());
        Assert.assertNotNull(cat.getStyleByName("bar"));
        // step 2 define css
        String content = newCSS();
        response = putAsServletResponse("/rest/styles/bar", content, MIME_TYPE);
        Assert.assertEquals(200, response.getStatus());
        GeoServerResourceLoader resources = CssStyleControllerTest.catalog.getResourceLoader();
        Resource resource = resources.get("/styles/bar.css");
        String definition = new String(resource.getContents());
        Assert.assertTrue("is css", definition.contains("stroke: red"));
        StyleInfo styleInfo = CssStyleControllerTest.catalog.getStyleByName("bar");
        Style s = styleInfo.getStyle();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        SLDHandler handler = new SLDHandler();
        handler.encode(Styles.sld(s), VERSION_10, false, out);
        content = new String(out.toByteArray());
        Assert.assertTrue(content.contains("<sld:Name>bar</sld:Name>"));
        // step 3 validate css
        content = "* { outline: red}";
        response = putAsServletResponse("/rest/styles/bar", content, MIME_TYPE);
        Assert.assertEquals(400, response.getStatus());
        CssStyleControllerTest.catalog.remove(styleInfo);
    }
}

