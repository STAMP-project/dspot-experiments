/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.rest.catalog;


import CatalogFacade.NO_WORKSPACE;
import SLDHandler.MIMETYPE_10;
import SLDHandler.VERSION_10;
import SLDHandler.VERSION_11;
import SystemTestData.BASIC_POLYGONS;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URL;
import java.util.List;
import net.sf.json.JSON;
import net.sf.json.JSONObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.custommonkey.xmlunit.XMLUnit;
import org.custommonkey.xmlunit.XpathEngine;
import org.geoserver.data.test.TestData;
import org.geoserver.platform.GeoServerResourceLoader;
import org.geoserver.platform.resource.Resource;
import org.geoserver.rest.RestBaseController;
import org.geotools.styling.Style;
import org.geotools.util.URLs;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import static SLDHandler.MIMETYPE_10;


/**
 * Just ripped these tests from the existing rest test
 */
public class StyleControllerTest extends CatalogRESTTestSupport {
    @Test
    public void testGetAllAsXML() throws Exception {
        Document dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/styles.xml"));
        List<StyleInfo> styles = CatalogRESTTestSupport.catalog.getStyles();
        assertXpathEvaluatesTo(("" + (styles.size())), "count(//style)", dom);
    }

    @Test
    public void testGetAllASJSON() throws Exception {
        JSON json = getAsJSON(((RestBaseController.ROOT_PATH) + "/styles.json"));
        List<StyleInfo> styles = CatalogRESTTestSupport.catalog.getStyles();
        Assert.assertEquals(styles.size(), getJSONObject("styles").getJSONArray("style").size());
    }

    @Test
    public void testGetAllAsHTML() throws Exception {
        Document dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/styles.html"));
        print(dom);
        List<StyleInfo> styles = CatalogRESTTestSupport.catalog.getStylesByWorkspace(NO_WORKSPACE);
        NodeList links = CatalogRESTTestSupport.xp.getMatchingNodes("//html:a", dom);
        for (int i = 0; i < (styles.size()); i++) {
            StyleInfo s = styles.get(i);
            Element link = ((Element) (links.item(i)));
            final String href = link.getAttribute("href");
            Assert.assertTrue(((("Expected href to bed with " + (s.getName())) + ".html but was ") + href), href.endsWith(((s.getName()) + ".html")));
        }
    }

    @Test
    public void testGetAllFromWorkspace() throws Exception {
        Document dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/workspaces/gs/styles.xml"));
        Assert.assertEquals("styles", dom.getDocumentElement().getNodeName());
        assertXpathEvaluatesTo("0", "count(//style)", dom);
        addStyleToWorkspace("foo");
        dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/workspaces/gs/styles.xml"));
        Assert.assertEquals("styles", dom.getDocumentElement().getNodeName());
        assertXpathEvaluatesTo("1", "count(//style)", dom);
        assertXpathExists("//style/name[text() = 'foo']", dom);
    }

    @Test
    public void testGetAsXML() throws Exception {
        Document dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/styles/Ponds.xml"));
        Assert.assertEquals("style", dom.getDocumentElement().getNodeName());
        assertXpathEvaluatesTo("Ponds", "/style/name", dom);
        assertXpathEvaluatesTo("Ponds.sld", "/style/filename", dom);
    }

    @Test
    public void testGetAsJSON() throws Exception {
        JSON json = getAsJSON(((RestBaseController.ROOT_PATH) + "/styles/Ponds.json"));
        JSONObject style = ((JSONObject) (json)).getJSONObject("style");
        Assert.assertEquals("Ponds", style.get("name"));
        Assert.assertEquals("Ponds.sld", style.get("filename"));
    }

    @Test
    public void testGetWrongStyle() throws Exception {
        // Parameters for the request
        String ws = "gs";
        String style = "foooooo";
        // Request path
        String requestPath = (((RestBaseController.ROOT_PATH) + "/styles/") + style) + ".html";
        String requestPath2 = (((((RestBaseController.ROOT_PATH) + "/workspaces/") + ws) + "/styles/") + style) + ".html";
        // Exception path
        String exception = "No such style: " + style;
        String exception2 = (("No such style " + style) + " in workspace ") + ws;
        // CASE 1: No workspace set
        // First request should thrown an exception
        MockHttpServletResponse response = getAsServletResponse(requestPath);
        Assert.assertEquals(404, response.getStatus());
        Assert.assertTrue(response.getContentAsString().contains(exception));
        // Same request with ?quietOnNotFound should not throw an exception
        response = getAsServletResponse((requestPath + "?quietOnNotFound=true"));
        Assert.assertEquals(404, response.getStatus());
        Assert.assertFalse(response.getContentAsString().contains(exception));
        // No exception thrown
        Assert.assertTrue(response.getContentAsString().isEmpty());
        // CASE 2: workspace set
        // First request should thrown an exception
        response = getAsServletResponse(requestPath2);
        Assert.assertEquals(404, response.getStatus());
        Assert.assertTrue(response.getContentAsString().contains(exception2));
        // Same request with ?quietOnNotFound should not throw an exception
        response = getAsServletResponse((requestPath2 + "?quietOnNotFound=true"));
        Assert.assertEquals(404, response.getStatus());
        Assert.assertFalse(response.getContentAsString().contains(exception2));
        // No exception thrown
        Assert.assertTrue(response.getContentAsString().isEmpty());
    }

    @Test
    public void testGetAsSLD() throws Exception {
        Document dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/styles/Ponds.sld"));
        Assert.assertEquals("StyledLayerDescriptor", dom.getDocumentElement().getNodeName());
    }

    @Test
    public void testGetFromWorkspace() throws Exception {
        MockHttpServletResponse resp = getAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/gs/styles/foo.xml"));
        Assert.assertEquals(404, resp.getStatus());
        addStyleToWorkspace("foo");
        resp = getAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/gs/styles/foo.xml"));
        Assert.assertEquals(200, resp.getStatus());
        Document dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/workspaces/gs/styles/foo.xml"));
        assertXpathEvaluatesTo("foo", "/style/name", dom);
        assertXpathEvaluatesTo("gs", "/style/workspace/name", dom);
    }

    // GEOS-8080
    @Test
    public void testGetGlobalWithDuplicateInDefaultWorkspace() throws Exception {
        Catalog cat = getCatalog();
        String styleName = "foo";
        String wsName = cat.getDefaultWorkspace().getName();
        // Add a workspace style
        StyleInfo s = cat.getFactory().createStyle();
        s.setName(styleName);
        s.setFilename((styleName + ".sld"));
        s.setWorkspace(cat.getDefaultWorkspace());
        cat.add(s);
        // Verify this style cannot retrieved by a non-workspaced GET
        MockHttpServletResponse resp = getAsServletResponse(((RestBaseController.ROOT_PATH) + "/styles/foo.xml"));
        Assert.assertEquals(404, resp.getStatus());
        // Add a global style
        s = cat.getFactory().createStyle();
        s.setName(styleName);
        s.setFilename((styleName + ".sld"));
        cat.add(s);
        // Verify the global style is returned by a non-workspaced GET
        Document dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/styles/foo.xml"));
        assertXpathEvaluatesTo("foo", "/style/name", dom);
        assertXpathEvaluatesTo("", "/style/workspace/name", dom);
        // Verify the workspaced style is returned by a workspaced GET
        dom = getAsDOM(((((RestBaseController.ROOT_PATH) + "/workspaces/") + wsName) + "/styles/foo.xml"));
        assertXpathEvaluatesTo("foo", "/style/name", dom);
        assertXpathEvaluatesTo(wsName, "/style/workspace/name", dom);
    }

    @Test
    public void testPostAsSLD() throws Exception {
        String xml = newSLDXML();
        MockHttpServletResponse response = postAsServletResponse(((RestBaseController.ROOT_PATH) + "/styles"), xml, MIMETYPE_10);
        Assert.assertEquals(201, response.getStatus());
        Assert.assertNotNull(response.getHeader("Location"));
        Assert.assertTrue(response.getHeader("Location").endsWith("/styles/foo"));
        Assert.assertNotNull(CatalogRESTTestSupport.catalog.getStyleByName("foo"));
    }

    @Test
    public void testPostAsSLD11() throws Exception {
        String xml = newSLD11XML();
        MockHttpServletResponse response = postAsServletResponse(((RestBaseController.ROOT_PATH) + "/styles"), xml, SLDHandler.MIMETYPE_11);
        Assert.assertEquals(201, response.getStatus());
        Assert.assertNotNull(response.getHeader("Location"));
        Assert.assertTrue(response.getHeader("Location").endsWith("/styles/foo"));
        final StyleInfo style = CatalogRESTTestSupport.catalog.getStyleByName("foo");
        Assert.assertNotNull(style);
        Assert.assertNotNull(style.getSLD());
    }

    @Test
    public void testPostExternalEntityAsSLD() throws Exception {
        String xml = IOUtils.toString(TestData.class.getResource("externalEntities.sld"), "UTF-8");
        MockHttpServletResponse response = postAsServletResponse(((RestBaseController.ROOT_PATH) + "/styles"), xml, MIMETYPE_10);
        Assert.assertEquals(500, response.getStatus());
        String message = response.getContentAsString();
        Assert.assertThat(message, CoreMatchers.containsString("Entity resolution disallowed"));
        Assert.assertThat(message, CoreMatchers.containsString("/this/file/does/not/exist"));
    }

    @Test
    public void testPostAsSLDToWorkspace() throws Exception {
        Assert.assertNull(CatalogRESTTestSupport.catalog.getStyleByName("gs", "foo"));
        String xml = newSLDXML();
        MockHttpServletResponse response = postAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/gs/styles"), xml, MIMETYPE_10);
        Assert.assertEquals(201, response.getStatus());
        Assert.assertNotNull(response.getHeader("Location"));
        Assert.assertTrue(response.getHeader("Location").endsWith("/workspaces/gs/styles/foo"));
        Assert.assertNotNull(CatalogRESTTestSupport.catalog.getStyleByName("gs", "foo"));
        GeoServerResourceLoader rl = getResourceLoader();
        Assert.assertNotNull(rl.find("workspaces", "gs", "styles", "foo.sld"));
    }

    @Test
    public void testPostAsSLD11ToWorkspace() throws Exception {
        Assert.assertNull(CatalogRESTTestSupport.catalog.getStyleByName("gs", "foo"));
        String xml = newSLD11XML();
        MockHttpServletResponse response = postAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/gs/styles"), xml, SLDHandler.MIMETYPE_11);
        Assert.assertEquals(201, response.getStatus());
        Assert.assertNotNull(response.getHeader("Location"));
        Assert.assertTrue(response.getHeader("Location").endsWith("/workspaces/gs/styles/foo"));
        Assert.assertNotNull(CatalogRESTTestSupport.catalog.getStyleByName("gs", "foo"));
        GeoServerResourceLoader rl = getResourceLoader();
        Assert.assertNotNull(rl.find("workspaces", "gs", "styles", "foo.sld"));
        final StyleInfo styleInfo = CatalogRESTTestSupport.catalog.getStyleByName("gs:foo");
        Assert.assertNotNull(styleInfo);
        Assert.assertNotNull(styleInfo.getSLD());
    }

    @Test
    public void testPostAsSLDWithName() throws Exception {
        String xml = newSLDXML();
        MockHttpServletResponse response = postAsServletResponse(((RestBaseController.ROOT_PATH) + "/styles?name=bar"), xml, MIMETYPE_10);
        Assert.assertEquals(201, response.getStatus());
        Assert.assertNotNull(response.getHeader("Location"));
        Assert.assertTrue(response.getHeader("Location").endsWith("/styles/bar"));
        Assert.assertNotNull(CatalogRESTTestSupport.catalog.getStyleByName("bar"));
    }

    @Test
    public void testStyleWithSpaceInName() throws Exception {
        String xml = newSLDXML();
        MockHttpServletResponse response = postAsServletResponse(((RestBaseController.ROOT_PATH) + "/styles?name=Default%20Styler"), xml, MIMETYPE_10);
        Assert.assertEquals(201, response.getStatus());
        Assert.assertNotNull(response.getHeader("Location"));
        Assert.assertThat(response.getHeader("Location"), Matchers.endsWith("/styles/Default%20Styler"));
        Assert.assertNotNull(CatalogRESTTestSupport.catalog.getStyleByName("Default Styler"));
        // now delete it, using a + instead of %20, the old code supported it
        response = deleteAsServletResponse(((RestBaseController.ROOT_PATH) + "/styles/Default+Styler"));
        Assert.assertEquals(200, response.getStatus());
    }

    @Test
    public void testPostToWorkspace() throws Exception {
        Catalog cat = getCatalog();
        Assert.assertNull(cat.getStyleByName("gs", "foo"));
        String xml = "<style>" + (("<name>foo</name>" + "<filename>foo.sld</filename>") + "</style>");
        MockHttpServletResponse response = postAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/gs/styles"), xml);
        Assert.assertEquals(201, response.getStatus());
        Assert.assertNotNull(cat.getStyleByName("gs", "foo"));
    }

    @Test
    public void testPut() throws Exception {
        StyleInfo style = CatalogRESTTestSupport.catalog.getStyleByName("Ponds");
        Assert.assertEquals("Ponds.sld", style.getFilename());
        String xml = "<style>" + (("<name>Ponds</name>" + "<filename>Forests.sld</filename>") + "</style>");
        MockHttpServletResponse response = putAsServletResponse(((RestBaseController.ROOT_PATH) + "/styles/Ponds"), xml.getBytes(), "text/xml");
        Assert.assertEquals(200, response.getStatus());
        style = CatalogRESTTestSupport.catalog.getStyleByName("Ponds");
        Assert.assertEquals("Forests.sld", style.getFilename());
    }

    @Test
    public void testPutAsSLD() throws Exception {
        String xml = newSLDXML();
        MockHttpServletResponse response = putAsServletResponse(((RestBaseController.ROOT_PATH) + "/styles/Ponds"), xml, MIMETYPE_10);
        Assert.assertEquals(200, response.getStatus());
        Style s = CatalogRESTTestSupport.catalog.getStyleByName("Ponds").getStyle();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        SLDHandler handler = new SLDHandler();
        handler.encode(Styles.sld(s), VERSION_10, false, out);
        xml = new String(out.toByteArray());
        Assert.assertTrue(xml.contains("<sld:Name>foo</sld:Name>"));
    }

    @Test
    public void testPutAsSLDWithCharset() throws Exception {
        String xml = newSLDXML();
        MockHttpServletResponse response = putAsServletResponse(((RestBaseController.ROOT_PATH) + "/styles/Ponds"), xml, ((MIMETYPE_10) + "; charset=utf-8"));
        Assert.assertEquals(200, response.getStatus());
        Style s = CatalogRESTTestSupport.catalog.getStyleByName("Ponds").getStyle();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        SLDHandler handler = new SLDHandler();
        handler.encode(Styles.sld(s), VERSION_10, false, out);
        xml = new String(out.toByteArray());
        Assert.assertTrue(xml.contains("<sld:Name>foo</sld:Name>"));
    }

    @Test
    public void testPutAsSLD11Raw() throws Exception {
        String xml = newSLD11XML();
        // check it's version 1.0 before
        StyleInfo infoBefore = CatalogRESTTestSupport.catalog.getStyleByName("Ponds");
        Assert.assertThat(infoBefore.getFormatVersion(), CoreMatchers.equalTo(VERSION_10));
        // put a SLD 1.1 in raw mode (the content type is ignored in raw mode, but mimicking was
        // gsconfig does here)
        MockHttpServletResponse response = putAsServletResponse(((RestBaseController.ROOT_PATH) + "/styles/Ponds?raw=true"), xml, "application/vnd.ogc.se+xml");
        Assert.assertEquals(200, response.getStatus());
        // now it should have been "upgraded" to 1.1
        StyleInfo infoAfter = CatalogRESTTestSupport.catalog.getStyleByName("Ponds");
        Assert.assertThat(infoAfter.getFormatVersion(), CoreMatchers.equalTo(VERSION_11));
    }

    @Test
    public void testPutVersionBackAndForth() throws Exception {
        // go from 1.0 to 1.1
        testPutAsSLD11Raw();
        String xml = newSLDXML();
        // put a SLD 1.0 in raw mode (the content type is ignored in raw mode, but mimicking was
        // gsconfig does here)
        MockHttpServletResponse response = putAsServletResponse(((RestBaseController.ROOT_PATH) + "/styles/Ponds?raw=true"), xml, "application/vnd.ogc.sld+xml");
        Assert.assertEquals(200, response.getStatus());
        // now it should have been modified back to 1.0
        StyleInfo infoAfter = CatalogRESTTestSupport.catalog.getStyleByName("Ponds");
        Assert.assertThat(infoAfter.getFormatVersion(), CoreMatchers.equalTo(VERSION_10));
    }

    @Test
    public void testPutAsSLDWithExtension() throws Exception {
        String xml = newSLDXML();
        MockHttpServletResponse response = putAsServletResponse(((RestBaseController.ROOT_PATH) + "/styles/Ponds.sld"), xml, MIMETYPE_10);
        Assert.assertEquals(200, response.getStatus());
        Style s = CatalogRESTTestSupport.catalog.getStyleByName("Ponds").getStyle();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        SLDHandler handler = new SLDHandler();
        handler.encode(Styles.sld(s), VERSION_10, false, out);
        xml = new String(out.toByteArray());
        Assert.assertTrue(xml.contains("<sld:Name>foo</sld:Name>"));
    }

    @Test
    public void testRawPutAsSLD() throws Exception {
        String xml = newSLDXML();
        MockHttpServletResponse response = putAsServletResponse(((RestBaseController.ROOT_PATH) + "/styles/Ponds?raw=true"), xml, MIMETYPE_10);
        Assert.assertEquals(200, response.getStatus());
        Style s = CatalogRESTTestSupport.catalog.getStyleByName("Ponds").getStyle();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        SLDHandler handler = new SLDHandler();
        handler.encode(Styles.sld(s), VERSION_10, false, out);
        xml = new String(out.toByteArray());
        Assert.assertTrue(xml.contains("<sld:Name>foo</sld:Name>"));
    }

    @Test
    public void testRawPutAsInvalidSLD() throws Exception {
        String xml = "This is not valid SLD";
        MockHttpServletResponse response = putAsServletResponse(((RestBaseController.ROOT_PATH) + "/styles/Ponds?raw=true"), xml, MIMETYPE_10);
        Assert.assertEquals(200, response.getStatus());
        StyleInfo styleInfo = CatalogRESTTestSupport.catalog.getStyleByName("Ponds");
        String fileName = styleInfo.getFilename();
        GeoServerResourceLoader resources = getResourceLoader();
        Resource resource = resources.get(("styles/" + fileName));
        String content = new String(resource.getContents());
        Assert.assertFalse("replaced", content.contains("<sld:Name>foo</sld:Name>"));
        Assert.assertTrue("replaced", content.contains("not valid"));
    }

    @Test
    public void testPutToWorkspace() throws Exception {
        testPostToWorkspace();
        Catalog cat = getCatalog();
        Assert.assertEquals("foo.sld", cat.getStyleByName("gs", "foo").getFilename());
        String xml = "<style>" + ("<filename>bar.sld</filename>" + "</style>");
        MockHttpServletResponse response = putAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/gs/styles/foo"), xml, "application/xml");
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("bar.sld", cat.getStyleByName("gs", "foo").getFilename());
    }

    @Test
    public void testPutToWorkspaceChangeWorkspace() throws Exception {
        testPostToWorkspace();
        String xml = "<style>" + ("<workspace>cite</workspace>" + "</style>");
        MockHttpServletResponse response = putAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/gs/styles/foo"), xml, "application/xml");
        Assert.assertEquals(403, response.getStatus());
    }

    @Test
    public void testPutRenameDefault() throws Exception {
        StyleInfo style = CatalogRESTTestSupport.catalog.getStyleByName("Ponds");
        Assert.assertEquals("Ponds.sld", style.getFilename());
        String xml = "<style>" + (("<name>Ponds</name>" + "<filename>Forests.sld</filename>") + "</style>");
        MockHttpServletResponse response = putAsServletResponse(((RestBaseController.ROOT_PATH) + "/styles/line"), xml.getBytes(), "text/xml");
        Assert.assertEquals(500, response.getStatus());
    }

    @Test
    public void testPutAsSLDNamedLayer() throws Exception {
        String xml = "<StyledLayerDescriptor version='1.0.0' " + (((((((((" xsi:schemaLocation='http://www.opengis.net/sld StyledLayerDescriptor.xsd' " + " xmlns='http://www.opengis.net/sld' ") + " xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'>") + "  <NamedLayer>\n") + "    <Name>Streams</Name>\n") + // Reference the Streams layer
        "  </NamedLayer>\n") + "  <NamedLayer>\n") + "    <Name>RoadSegments</Name>\n") + // 2nd, valid layer
        "  </NamedLayer>\n") + "</StyledLayerDescriptor>");
        MockHttpServletResponse response = putAsServletResponse(((RestBaseController.ROOT_PATH) + "/styles/Ponds"), xml, MIMETYPE_10);
        Assert.assertEquals(200, response.getStatus());
        Assert.assertNotNull(CatalogRESTTestSupport.catalog.getStyleByName("Ponds"));
    }

    @Test
    public void testPutAsSLDNamedLayerInvalid() throws Exception {
        String xml = "<StyledLayerDescriptor version='1.0.0' " + (((((((((" xsi:schemaLocation='http://www.opengis.net/sld StyledLayerDescriptor.xsd' " + " xmlns='http://www.opengis.net/sld' ") + " xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'>") + "  <NamedLayer>\n") + "    <Name>Stream</Name>\n") + // invalid layer
        "  </NamedLayer>\n") + "  <NamedLayer>\n") + "    <Name>Streams</Name>\n") + // valid layer
        "  </NamedLayer>\n") + "</StyledLayerDescriptor>");
        MockHttpServletResponse response = putAsServletResponse(((RestBaseController.ROOT_PATH) + "/styles/Ponds"), xml, MIMETYPE_10);
        Assert.assertEquals(400, response.getStatus());
        Assert.assertEquals("Invalid style:No layer or layer group named 'Stream' found in the catalog", response.getContentAsString());
    }

    @Test
    public void testStyleNotFoundGloballyWhenInWorkspace() throws Exception {
        testPostToWorkspace();
        Catalog cat = getCatalog();
        Assert.assertEquals("foo.sld", cat.getStyleByName("gs", "foo").getFilename());
        String xml = "<style>" + ("<filename>bar.sld</filename>" + "</style>");
        MockHttpServletResponse response = putAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/gs/styles/foo"), xml, "application/xml");
        Assert.assertEquals(200, response.getStatus());
        response = putAsServletResponse(((RestBaseController.ROOT_PATH) + "/styles/gs:foo"), xml, "application/xml");
        Assert.assertEquals(500, response.getStatus());
    }

    @Test
    public void testDelete() throws Exception {
        String xml = "<style>" + (("<name>dummy</name>" + "<filename>dummy.sld</filename>") + "</style>");
        post(((RestBaseController.ROOT_PATH) + "/styles"), xml, "text/xml");
        Assert.assertNotNull(CatalogRESTTestSupport.catalog.getStyleByName("dummy"));
        MockHttpServletResponse response = deleteAsServletResponse(((RestBaseController.ROOT_PATH) + "/styles/dummy"));
        Assert.assertEquals(200, response.getStatus());
        Assert.assertNull(CatalogRESTTestSupport.catalog.getStyleByName("dummy"));
    }

    @Test
    public void testDeleteDefault() throws Exception {
        MockHttpServletResponse response = deleteAsServletResponse(((RestBaseController.ROOT_PATH) + "/styles/line"));
        Assert.assertEquals(500, response.getStatus());
    }

    @Test
    public void testDeleteWithLayerReference() throws Exception {
        Assert.assertNotNull(CatalogRESTTestSupport.catalog.getStyleByName("Ponds"));
        MockHttpServletResponse response = deleteAsServletResponse(((RestBaseController.ROOT_PATH) + "/styles/Ponds"));
        Assert.assertEquals(403, response.getStatus());
        Assert.assertNotNull(CatalogRESTTestSupport.catalog.getStyleByName("Ponds"));
    }

    @Test
    public void testDeleteWithLayerReferenceAndRecurse() throws Exception {
        Assert.assertNotNull(CatalogRESTTestSupport.catalog.getStyleByName("Ponds"));
        MockHttpServletResponse response = deleteAsServletResponse(((RestBaseController.ROOT_PATH) + "/styles/Ponds?recurse=true"));
        Assert.assertEquals(200, response.getStatus());
        Assert.assertNull(CatalogRESTTestSupport.catalog.getStyleByName("Ponds"));
    }

    @Test
    public void testDeleteWithoutPurge() throws Exception {
        String xml = newSLDXML();
        MockHttpServletResponse response = postAsServletResponse(((RestBaseController.ROOT_PATH) + "/styles"), xml, MIMETYPE_10);
        Assert.assertNotNull(CatalogRESTTestSupport.catalog.getStyleByName("foo"));
        // ensure the style not deleted on disk
        Assert.assertTrue(new File(getDataDirectory().findStyleDir(), "foo.sld").exists());
        response = deleteAsServletResponse(((RestBaseController.ROOT_PATH) + "/styles/foo"));
        Assert.assertEquals(200, response.getStatus());
        // ensure the style deleted on disk but backed up
        Assert.assertFalse(new File(getDataDirectory().findStyleDir(), "foo.sld").exists());
        Assert.assertTrue(new File(getDataDirectory().findStyleDir(), "foo.sld.bak").exists());
    }

    @Test
    public void testDeleteWithPurge() throws Exception {
        String xml = newSLDXML();
        MockHttpServletResponse response = postAsServletResponse(((RestBaseController.ROOT_PATH) + "/styles"), xml, MIMETYPE_10);
        Assert.assertNotNull(CatalogRESTTestSupport.catalog.getStyleByName("foo"));
        // ensure the style not deleted on disk
        Assert.assertTrue(new File(getDataDirectory().findStyleDir(), "foo.sld").exists());
        response = deleteAsServletResponse(((RestBaseController.ROOT_PATH) + "/styles/foo?purge=true"));
        Assert.assertEquals(200, response.getStatus());
        // ensure the style not deleted on disk
        Assert.assertFalse(new File(getDataDirectory().findStyleDir(), "foo.sld").exists());
    }

    @Test
    public void testDeleteFromWorkspace() throws Exception {
        testPostToWorkspace();
        Catalog cat = getCatalog();
        Assert.assertNotNull(cat.getStyleByName("gs", "foo"));
        MockHttpServletResponse response = deleteAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/gs/styles/foo.xml"));
        Assert.assertEquals(200, response.getStatus());
        Assert.assertNull(cat.getStyleByName("gs", "foo"));
    }

    @Test
    public void testDeleteFromWorkspaceWithPurge() throws Exception {
        testPostAsSLDToWorkspace();
        Catalog cat = getCatalog();
        Assert.assertNotNull(cat.getStyleByName("gs", "foo"));
        GeoServerResourceLoader rl = getResourceLoader();
        Assert.assertNotNull(rl.find("workspaces", "gs", "styles", "foo.sld"));
        MockHttpServletResponse response = deleteAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/gs/styles/foo?purge=true"));
        Assert.assertEquals(200, response.getStatus());
        Assert.assertNull(cat.getStyleByName("gs", "foo"));
        Assert.assertNull(rl.find("workspaces", "gs", "styles", "foo.sld"));
    }

    @Test
    public void testGetAllByLayer() throws Exception {
        Document dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/layers/cite:BasicPolygons/styles.xml"));
        LayerInfo layer = CatalogRESTTestSupport.catalog.getLayerByName("cite:BasicPolygons");
        assertXpathEvaluatesTo(((layer.getStyles().size()) + ""), "count(//style)", dom);
    }

    @Test
    public void testPostByLayer() throws Exception {
        LayerInfo l = CatalogRESTTestSupport.catalog.getLayerByName("cite:BasicPolygons");
        int nstyles = l.getStyles().size();
        String xml = "<style>" + ("<name>Ponds</name>" + "</style>");
        MockHttpServletResponse response = postAsServletResponse(((RestBaseController.ROOT_PATH) + "/layers/cite:BasicPolygons/styles"), xml, "text/xml");
        Assert.assertEquals(201, response.getStatus());
        LayerInfo l2 = CatalogRESTTestSupport.catalog.getLayerByName("cite:BasicPolygons");
        Assert.assertEquals((nstyles + 1), l2.getStyles().size());
        Assert.assertTrue(l2.getStyles().contains(CatalogRESTTestSupport.catalog.getStyleByName("Ponds")));
    }

    @Test
    public void testPostByLayerWithDefault() throws Exception {
        getTestData().addVectorLayer(BASIC_POLYGONS, getCatalog());
        LayerInfo l = CatalogRESTTestSupport.catalog.getLayerByName("cite:BasicPolygons");
        int nstyles = l.getStyles().size();
        String xml = "<style>" + ("<name>Ponds</name>" + "</style>");
        MockHttpServletResponse response = postAsServletResponse(((RestBaseController.ROOT_PATH) + "/layers/cite:BasicPolygons/styles?default=true"), xml, "text/xml");
        Assert.assertEquals(201, response.getStatus());
        LayerInfo l2 = CatalogRESTTestSupport.catalog.getLayerByName("cite:BasicPolygons");
        Assert.assertEquals((nstyles + 1), l2.getStyles().size());
        Assert.assertEquals(CatalogRESTTestSupport.catalog.getStyleByName("Ponds"), l2.getDefaultStyle());
    }

    @Test
    public void testPostByLayerExistingWithDefault() throws Exception {
        getTestData().addVectorLayer(BASIC_POLYGONS, getCatalog());
        testPostByLayer();
        LayerInfo l = CatalogRESTTestSupport.catalog.getLayerByName("cite:BasicPolygons");
        int nstyles = l.getStyles().size();
        String xml = "<style>" + ("<name>Ponds</name>" + "</style>");
        MockHttpServletResponse response = postAsServletResponse(((RestBaseController.ROOT_PATH) + "/layers/cite:BasicPolygons/styles?default=true"), xml, "text/xml");
        Assert.assertEquals(201, response.getStatus());
        LayerInfo l2 = CatalogRESTTestSupport.catalog.getLayerByName("cite:BasicPolygons");
        Assert.assertEquals(nstyles, l2.getStyles().size());
        Assert.assertEquals(CatalogRESTTestSupport.catalog.getStyleByName("Ponds"), l2.getDefaultStyle());
    }

    @Test
    public void testPostAsSE() throws Exception {
        String xml = "<StyledLayerDescriptor xmlns=\"http://www.opengis.net/sld\" " + ((((((((((((((("       xmlns:se=\"http://www.opengis.net/se\" version=\"1.1.0\"> " + " <NamedLayer> ") + "  <UserStyle> ") + "   <se:Name>UserSelection</se:Name> ") + "   <se:FeatureTypeStyle> ") + "    <se:Rule> ") + "     <se:PolygonSymbolizer> ") + "      <se:Fill> ") + "       <se:SvgParameter name=\"fill\">#FF0000</se:SvgParameter> ") + "      </se:Fill> ") + "     </se:PolygonSymbolizer> ") + "    </se:Rule> ") + "   </se:FeatureTypeStyle> ") + "  </UserStyle> ") + " </NamedLayer> ") + "</StyledLayerDescriptor>");
        MockHttpServletResponse response = postAsServletResponse(((RestBaseController.ROOT_PATH) + "/styles?name=foo"), xml, SLDHandler.MIMETYPE_11);
        Assert.assertEquals(201, response.getStatus());
        Assert.assertNotNull(response.getHeader("Location"));
        Assert.assertTrue(response.getHeader("Location").endsWith("/styles/foo"));
        StyleInfo style = CatalogRESTTestSupport.catalog.getStyleByName("foo");
        Assert.assertNotNull(style);
        Assert.assertEquals("sld", style.getFormat());
        Assert.assertEquals(VERSION_11, style.getFormatVersion());
    }

    @Test
    public void testPostToWorkspaceSLDPackage() throws Exception {
        Catalog cat = getCatalog();
        Assert.assertNull(cat.getStyleByName("gs", "foo"));
        URL zip = getClass().getResource("test-data/foo.zip");
        byte[] bytes = FileUtils.readFileToByteArray(URLs.urlToFile(zip));
        MockHttpServletResponse response = postAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/gs/styles"), bytes, "application/zip");
        Assert.assertEquals(201, response.getStatus());
        Assert.assertNotNull(cat.getStyleByName("gs", "foo"));
        Document d = getAsDOM(((RestBaseController.ROOT_PATH) + "/workspaces/gs/styles/foo.sld"));
        Assert.assertEquals("StyledLayerDescriptor", d.getDocumentElement().getNodeName());
        XpathEngine engine = XMLUnit.newXpathEngine();
        NodeList list = engine.getMatchingNodes("//sld:StyledLayerDescriptor/sld:NamedLayer/sld:UserStyle/sld:FeatureTypeStyle/sld:Rule/sld:PointSymbolizer/sld:Graphic/sld:ExternalGraphic/sld:OnlineResource", d);
        Assert.assertEquals(1, list.getLength());
        Element onlineResource = ((Element) (list.item(0)));
        Assert.assertEquals("gear.png", onlineResource.getAttribute("xlink:href"));
        Assert.assertNotNull(getCatalog().getResourceLoader().find("workspaces/gs/styles/gear.png"));
        Assert.assertNotNull(getCatalog().getResourceLoader().find("workspaces/gs/styles/foo.sld"));
    }

    @Test
    public void testPostWithExternalEntities() throws Exception {
        URL zip = getClass().getResource("test-data/externalEntities.zip");
        byte[] bytes = FileUtils.readFileToByteArray(URLs.urlToFile(zip));
        MockHttpServletResponse response = postAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/gs/styles"), bytes, "application/zip");
        // expecting a failure with explanation
        Assert.assertEquals(400, response.getStatus());
        final String content = response.getContentAsString();
        Assert.assertThat(content, CoreMatchers.containsString("Entity resolution disallowed"));
        Assert.assertThat(content, CoreMatchers.containsString("/this/file/does/not/exist"));
    }

    @Test
    public void testPutToWorkspaceSLDPackage() throws Exception {
        testPostAsSLDToWorkspace();
        Catalog cat = getCatalog();
        Assert.assertNotNull(cat.getStyleByName("gs", "foo"));
        URL zip = getClass().getResource("test-data/foo.zip");
        byte[] bytes = FileUtils.readFileToByteArray(URLs.urlToFile(zip));
        MockHttpServletResponse response = putAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/gs/styles/foo"), bytes, "application/zip");
        Assert.assertEquals(200, response.getStatus());
        Assert.assertNotNull(cat.getStyleByName("gs", "foo"));
        Document d = getAsDOM(((RestBaseController.ROOT_PATH) + "/workspaces/gs/styles/foo.sld"));
        Assert.assertEquals("StyledLayerDescriptor", d.getDocumentElement().getNodeName());
        XpathEngine engine = XMLUnit.newXpathEngine();
        NodeList list = engine.getMatchingNodes("//sld:StyledLayerDescriptor/sld:NamedLayer/sld:UserStyle/sld:FeatureTypeStyle/sld:Rule/sld:PointSymbolizer/sld:Graphic/sld:ExternalGraphic/sld:OnlineResource", d);
        Assert.assertEquals(1, list.getLength());
        Element onlineResource = ((Element) (list.item(0)));
        Assert.assertEquals("gear.png", onlineResource.getAttribute("xlink:href"));
        Assert.assertNotNull(getCatalog().getResourceLoader().find("workspaces/gs/styles/gear.png"));
        Assert.assertNotNull(getCatalog().getResourceLoader().find("workspaces/gs/styles/foo.sld"));
    }

    @Test
    public void testPostSLDPackage() throws Exception {
        Catalog cat = getCatalog();
        Assert.assertNull(cat.getStyleByName("foo"));
        URL zip = getClass().getResource("test-data/foo.zip");
        byte[] bytes = FileUtils.readFileToByteArray(URLs.urlToFile(zip));
        MockHttpServletResponse response = postAsServletResponse(((RestBaseController.ROOT_PATH) + "/styles"), bytes, "application/zip");
        Assert.assertEquals(201, response.getStatus());
        Assert.assertNotNull(cat.getStyleByName("foo"));
        Document d = getAsDOM(((RestBaseController.ROOT_PATH) + "/styles/foo.sld"));
        Assert.assertEquals("StyledLayerDescriptor", d.getDocumentElement().getNodeName());
        XpathEngine engine = XMLUnit.newXpathEngine();
        NodeList list = engine.getMatchingNodes("//sld:StyledLayerDescriptor/sld:NamedLayer/sld:UserStyle/sld:FeatureTypeStyle/sld:Rule/sld:PointSymbolizer/sld:Graphic/sld:ExternalGraphic/sld:OnlineResource", d);
        Assert.assertEquals(1, list.getLength());
        Element onlineResource = ((Element) (list.item(0)));
        Assert.assertEquals("gear.png", onlineResource.getAttribute("xlink:href"));
        Assert.assertNotNull(getCatalog().getResourceLoader().find("styles/gear.png"));
        Assert.assertNotNull(getCatalog().getResourceLoader().find("styles/foo.sld"));
    }

    @Test
    public void testPutSLDPackage() throws Exception {
        testPostAsSLD();
        Catalog cat = getCatalog();
        Assert.assertNotNull(cat.getStyleByName("foo"));
        URL zip = getClass().getResource("test-data/foo.zip");
        byte[] bytes = FileUtils.readFileToByteArray(URLs.urlToFile(zip));
        // @TODO i had to change this from foo.zip to just foo. see the long comments below
        MockHttpServletResponse response = putAsServletResponse(((RestBaseController.ROOT_PATH) + "/styles/foo"), bytes, "application/zip");
        Assert.assertEquals(200, response.getStatus());
        Assert.assertNotNull(cat.getStyleByName("foo"));
        Document d = getAsDOM(((RestBaseController.ROOT_PATH) + "/styles/foo.sld"));
        Assert.assertEquals("StyledLayerDescriptor", d.getDocumentElement().getNodeName());
        XpathEngine engine = XMLUnit.newXpathEngine();
        NodeList list = engine.getMatchingNodes("//sld:StyledLayerDescriptor/sld:NamedLayer/sld:UserStyle/sld:FeatureTypeStyle/sld:Rule/sld:PointSymbolizer/sld:Graphic/sld:ExternalGraphic/sld:OnlineResource", d);
        Assert.assertEquals(1, list.getLength());
        Element onlineResource = ((Element) (list.item(0)));
        Assert.assertEquals("gear.png", onlineResource.getAttribute("xlink:href"));
        Assert.assertNotNull(getCatalog().getResourceLoader().find("styles/gear.png"));
        Assert.assertNotNull(getCatalog().getResourceLoader().find("styles/foo.sld"));
    }
}

