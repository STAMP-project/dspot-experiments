/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.rest.catalog;


import MBStyleHandler.MIME_TYPE;
import MediaType.TEXT_HTML_VALUE;
import SLDHandler.MIMETYPE_10;
import SLDHandler.VERSION_10;
import java.io.ByteArrayOutputStream;
import org.apache.commons.io.IOUtils;
import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.SLDHandler;
import org.geoserver.catalog.StyleInfo;
import org.geoserver.catalog.Styles;
import org.geoserver.community.mbstyle.MBStyleHandler;
import org.geoserver.platform.GeoServerResourceLoader;
import org.geoserver.platform.resource.Resource;
import org.geoserver.test.GeoServerSystemTestSupport;
import org.geotools.styling.Style;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.util.xml.SimpleNamespaceContext;


public class MBStyleControllerTest extends GeoServerSystemTestSupport {
    protected static Catalog catalog;

    private static SimpleNamespaceContext namespaceContext;

    @Test
    public void getBodyAsJsonUsingAcceptHeader() throws Exception {
        MockHttpServletRequest request = createRequest("/rest/styles/teststyle");
        request.setMethod("GET");
        request.addHeader("Accept", MIME_TYPE);
        MockHttpServletResponse response = dispatch(request);
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals(MIME_TYPE, response.getContentType());
        String responseContent = response.getContentAsString();
        String expected = IOUtils.toString(this.getClass().getResourceAsStream("teststyle.json"));
        Assert.assertEquals(expected, responseContent);
    }

    @Test
    public void getInfoAsJsonUsingExtension() throws Exception {
        MockHttpServletResponse response = getAsServletResponse("/rest/styles/teststyle.json");
        Assert.assertEquals(200, response.getStatus());
        String responseContent = response.getContentAsString();
        Assert.assertEquals("application/json", response.getContentType());
        // Assert that the response contains the style info as json
        Assert.assertEquals(("{\"style\":{\"name\":\"teststyle\"," + (("\"format\":\"mbstyle\"," + "\"languageVersion\":{\"version\":\"1.0.0\"},") + "\"filename\":\"teststyle.json\"}}")), responseContent);
    }

    @Test
    public void getBodyAsSLDUsingAcceptHeader() throws Exception {
        MockHttpServletRequest request = createRequest("/rest/styles/teststyle");
        request.setMethod("GET");
        request.addHeader("Accept", MIMETYPE_10);
        MockHttpServletResponse response = dispatch(request);
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals(MIMETYPE_10, response.getContentType());
        String content = response.getContentAsString();
        Assert.assertTrue(content.contains("<sld:Name>test-layer</sld:Name>"));
    }

    @Test
    public void getBodyAsSLDUsingExtension() throws Exception {
        MockHttpServletResponse response = getAsServletResponse("/rest/styles/teststyle.sld");
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals(MIMETYPE_10, response.getContentType());
        String content = response.getContentAsString();
        Assert.assertTrue(content.contains("<sld:Name>test-layer</sld:Name>"));
    }

    @Test
    public void getAsHTML() throws Exception {
        MockHttpServletResponse response = getAsServletResponse("/rest/styles/teststyle.html");
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals(TEXT_HTML_VALUE, response.getContentType());
        String content = response.getContentAsString();
        Assert.assertTrue(content.contains("<a href=\"http://localhost:8080/geoserver/rest/styles/teststyle"));
    }

    @Test
    public void testRawPutJson() throws Exception {
        String jsonBody = newMbStyle();
        Catalog cat = getCatalog();
        Assert.assertNull("foo not available", cat.getStyleByName("foo"));
        String xml = (((("<style>" + ("<name>foo</name>" + "<format>")) + (MBStyleHandler.FORMAT)) + "</format>") + "<filename>foo.json</filename>") + "</style>";
        MockHttpServletResponse response = postAsServletResponse("/rest/styles", xml);
        Assert.assertEquals(201, response.getStatus());
        Assert.assertNotNull(cat.getStyleByName("foo"));
        // step 2 define mbstyle json
        response = putAsServletResponse("/rest/styles/foo?raw=true", jsonBody, MIME_TYPE);
        Assert.assertEquals(200, response.getStatus());
        GeoServerResourceLoader resources = MBStyleControllerTest.catalog.getResourceLoader();
        Resource resource = resources.get("/styles/foo.json");
        String definition = new String(resource.getContents());
        Assert.assertTrue("is json", definition.contains("\"circle-color\": \"#FFFFFF\""));
        StyleInfo styleInfo = MBStyleControllerTest.catalog.getStyleByName("foo");
        Style s = styleInfo.getStyle();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        SLDHandler handler = new SLDHandler();
        handler.encode(Styles.sld(s), VERSION_10, false, out);
        String contentOut = new String(out.toByteArray());
        Assert.assertTrue(contentOut.contains("<sld:Name>foo</sld:Name>"));
        MBStyleControllerTest.catalog.remove(styleInfo);
    }

    @Test
    public void testPostJson() throws Exception {
        String jsonBody = newMbStyle();
        Catalog cat = getCatalog();
        Assert.assertNull("foo not available", cat.getStyleByName("foo"));
        MockHttpServletResponse response = postAsServletResponse("/rest/styles?name=foo", jsonBody, MIME_TYPE);
        Assert.assertEquals(201, response.getStatus());
        GeoServerResourceLoader resources = MBStyleControllerTest.catalog.getResourceLoader();
        Resource resource = resources.get("/styles/foo.json");
        String definition = new String(resource.getContents());
        Assert.assertTrue("is json", definition.contains("\"circle-color\": \"#FFFFFF\""));
        StyleInfo styleInfo = MBStyleControllerTest.catalog.getStyleByName("foo");
        Style s = styleInfo.getStyle();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        SLDHandler handler = new SLDHandler();
        handler.encode(Styles.sld(s), VERSION_10, false, out);
        String contentOut = new String(out.toByteArray());
        Assert.assertTrue(contentOut.contains("<sld:Name>foo</sld:Name>"));
        MBStyleControllerTest.catalog.remove(styleInfo);
    }
}

