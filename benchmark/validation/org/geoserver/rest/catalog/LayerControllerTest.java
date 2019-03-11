/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.rest.catalog;


import LayerInfo.WMSInterpolation.Nearest;
import net.sf.json.JSONObject;
import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.LayerInfo;
import org.geoserver.catalog.ResourceInfo;
import org.geoserver.catalog.StyleInfo;
import org.geoserver.rest.RestBaseController;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.w3c.dom.Document;


public class LayerControllerTest extends CatalogRESTTestSupport {
    @Test
    public void testGetListAsXML() throws Exception {
        Document dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/layers.xml"), 200);
        Assert.assertEquals("layers", dom.getDocumentElement().getNodeName());
        // verify layer name and links for cite:Buildings
        assertXpathExists("//layer[name='cite:Buildings']", dom);
        Assert.assertThat(CatalogRESTTestSupport.xp.evaluate("//layer[name='cite:Buildings']/atom:link/@href", dom), Matchers.endsWith(((RestBaseController.ROOT_PATH) + "/layers/cite%3ABuildings.xml")));
    }

    @Test
    public void testGetListInWorkspaceAsXML() throws Exception {
        Document dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/workspaces/cite/layers.xml"), 200);
        Assert.assertEquals("layers", dom.getDocumentElement().getNodeName());
        print(dom);
        // verify layer name and links for cite:Buildings
        assertXpathExists("//layer[name='Buildings']", dom);
        Assert.assertThat(CatalogRESTTestSupport.xp.evaluate("//layer[name='Buildings']/atom:link/@href", dom), Matchers.endsWith(((RestBaseController.ROOT_PATH) + "/workspaces/cite/layers/Buildings.xml")));
    }

    @Test
    public void testGetAsXML() throws Exception {
        Document dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/layers/cite:Buildings.xml"), 200);
        Assert.assertEquals("layer", dom.getDocumentElement().getNodeName());
        assertXpathEvaluatesTo("Buildings", "/layer/name", dom);
        // check the layer name is actually the first child (GEOS-3336 risked modifying
        // the order)
        assertXpathEvaluatesTo("Buildings", "/layer/*[1]", dom);
        assertXpathEvaluatesTo((("http://localhost:8080/geoserver" + (RestBaseController.ROOT_PATH)) + "/styles/Buildings.xml"), "/layer/defaultStyle/atom:link/attribute::href", dom);
    }

    @Test
    public void testGetInWorkspaceAsXML() throws Exception {
        Document dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/workspaces/cite/layers/Buildings.xml"), 200);
        Assert.assertEquals("layer", dom.getDocumentElement().getNodeName());
        assertXpathEvaluatesTo("Buildings", "/layer/name", dom);
        // check the layer name is actually the first child (GEOS-3336 risked modifying
        // the order)
        assertXpathEvaluatesTo("Buildings", "/layer/*[1]", dom);
        assertXpathEvaluatesTo((("http://localhost:8080/geoserver" + (RestBaseController.ROOT_PATH)) + "/styles/Buildings.xml"), "/layer/defaultStyle/atom:link/attribute::href", dom);
    }

    @Test
    public void testGetAsHTML() throws Exception {
        getAsDOM(((RestBaseController.ROOT_PATH) + "/layers/cite:Buildings.html"), 200);
    }

    @Test
    public void testGetWrongLayer() throws Exception {
        // Parameters for the request
        String layer = "cite:Buildingssssss";
        // Request path
        String requestPath = (((RestBaseController.ROOT_PATH) + "/layers/") + layer) + ".html";
        // Exception path
        String exception = "No such layer: " + layer;
        // First request should thrown an exception
        MockHttpServletResponse response = getAsServletResponse(requestPath);
        Assert.assertEquals(404, response.getStatus());
        Assert.assertTrue(response.getContentAsString().contains(exception));
        // Same request with ?quietOnNotFound should not throw an exception
        response = getAsServletResponse((requestPath + "?quietOnNotFound=true"));
        Assert.assertEquals(404, response.getStatus());
        String message = response.getContentAsString();
        Assert.assertFalse(message, message.contains(exception));
        // No exception thrown
        Assert.assertTrue(response.getContentAsString().isEmpty());
    }

    @Test
    public void testGetAllAsXML() throws Exception {
        Document dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/layers.xml"), 200);
        assertXpathEvaluatesTo(((CatalogRESTTestSupport.catalog.getLayers().size()) + ""), "count(//layer)", dom);
    }

    @Test
    public void testGetAllInWorkspaceAsXML() throws Exception {
        Document dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/workspaces/cite/layers.xml"), 200);
        int count = CatalogRESTTestSupport.catalog.getResourcesByNamespace("cite", ResourceInfo.class).stream().mapToInt(( info) -> CatalogRESTTestSupport.catalog.getLayers(info).size()).sum();
        assertXpathEvaluatesTo((count + ""), "count(//layer)", dom);
    }

    @Test
    public void testGetAllAsHTML() throws Exception {
        getAsDOM(((RestBaseController.ROOT_PATH) + "/layers.html"), 200);
    }

    @Test
    public void testPut() throws Exception {
        LayerInfo l = CatalogRESTTestSupport.catalog.getLayerByName("cite:Buildings");
        Assert.assertEquals("Buildings", l.getDefaultStyle().getName());
        String xml = "<layer>" + (((("<defaultStyle>Forests</defaultStyle>" + "<styles>") + "<style>Ponds</style>") + "</styles>") + "</layer>");
        MockHttpServletResponse response = putAsServletResponse(((RestBaseController.ROOT_PATH) + "/layers/cite:Buildings"), xml, "text/xml");
        Assert.assertEquals(200, response.getStatus());
        l = CatalogRESTTestSupport.catalog.getLayerByName("cite:Buildings");
        Assert.assertEquals("Forests", l.getDefaultStyle().getName());
    }

    @Test
    public void testPutInWorkspace() throws Exception {
        LayerInfo l = CatalogRESTTestSupport.catalog.getLayerByName("cite:Buildings");
        Assert.assertEquals("Buildings", l.getDefaultStyle().getName());
        String xml = "<layer>" + (((("<defaultStyle>Forests</defaultStyle>" + "<styles>") + "<style>Ponds</style>") + "</styles>") + "</layer>");
        MockHttpServletResponse response = putAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/cite/layers/Buildings"), xml, "text/xml");
        Assert.assertEquals(200, response.getStatus());
        l = CatalogRESTTestSupport.catalog.getLayerByName("cite:Buildings");
        Assert.assertEquals("Forests", l.getDefaultStyle().getName());
    }

    @Test
    public void testPutNonDestructive() throws Exception {
        LayerInfo l = CatalogRESTTestSupport.catalog.getLayerByName("cite:Buildings");
        Assert.assertTrue(l.isEnabled());
        boolean isAdvertised = l.isAdvertised();
        boolean isOpaque = l.isOpaque();
        boolean isQueryable = l.isQueryable();
        String xml = "<layer>" + (((("<defaultStyle>Forests</defaultStyle>" + "<styles>") + "<style>Ponds</style>") + "</styles>") + "</layer>");
        MockHttpServletResponse response = putAsServletResponse(((RestBaseController.ROOT_PATH) + "/layers/cite:Buildings"), xml, "text/xml");
        Assert.assertEquals(200, response.getStatus());
        l = CatalogRESTTestSupport.catalog.getLayerByName("cite:Buildings");
        Assert.assertTrue(l.isEnabled());
        Assert.assertEquals(isAdvertised, l.isAdvertised());
        Assert.assertEquals(isOpaque, l.isOpaque());
        Assert.assertEquals(isQueryable, l.isQueryable());
    }

    @Test
    public void testUpdateStyleJSON() throws Exception {
        LayerInfo l = CatalogRESTTestSupport.catalog.getLayerByName("cite:Buildings");
        Assert.assertEquals("Buildings", l.getDefaultStyle().getName());
        JSONObject json = ((JSONObject) (getAsJSON(((RestBaseController.ROOT_PATH) + "/layers/cite:Buildings.json"))));
        // print(json);
        JSONObject layer = ((JSONObject) (json.get("layer")));
        JSONObject style = ((JSONObject) (layer.get("defaultStyle")));
        style.put("name", "polygon");
        style.put("href", "http://localhost:8080/geoserver/rest/styles/polygon.json");
        String updatedJson = json.toString();
        MockHttpServletResponse response = putAsServletResponse(((RestBaseController.ROOT_PATH) + "/layers/cite:Buildings"), updatedJson, "application/json");
        Assert.assertEquals(200, response.getStatus());
        l = CatalogRESTTestSupport.catalog.getLayerByName("cite:Buildings");
        Assert.assertEquals("polygon", l.getDefaultStyle().getName());
    }

    @Test
    public void testDelete() throws Exception {
        Assert.assertNotNull(CatalogRESTTestSupport.catalog.getLayerByName("cite:Buildings"));
        Assert.assertEquals(200, deleteAsServletResponse(((RestBaseController.ROOT_PATH) + "/layers/cite:Buildings")).getStatus());
        Assert.assertNull(CatalogRESTTestSupport.catalog.getLayerByName("cite:Buildings"));
    }

    @Test
    public void testDeleteInWorkspace() throws Exception {
        Assert.assertNotNull(CatalogRESTTestSupport.catalog.getLayerByName("cite:Buildings"));
        Assert.assertEquals(200, deleteAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/cite/layers/Buildings")).getStatus());
        Assert.assertNull(CatalogRESTTestSupport.catalog.getLayerByName("cite:Buildings"));
    }

    @Test
    public void testDeleteRecursive() throws Exception {
        Assert.assertNotNull(CatalogRESTTestSupport.catalog.getLayerByName("cite:Buildings"));
        Assert.assertNotNull(CatalogRESTTestSupport.catalog.getFeatureTypeByName("cite", "Buildings"));
        Assert.assertEquals(200, deleteAsServletResponse(((RestBaseController.ROOT_PATH) + "/layers/cite:Buildings")).getStatus());
        Assert.assertNull(CatalogRESTTestSupport.catalog.getLayerByName("cite:Buildings"));
        Assert.assertNotNull(CatalogRESTTestSupport.catalog.getFeatureTypeByName("cite", "Buildings"));
        Assert.assertNotNull(CatalogRESTTestSupport.catalog.getLayerByName("cite:Bridges"));
        Assert.assertNotNull(CatalogRESTTestSupport.catalog.getFeatureTypeByName("cite", "Bridges"));
        Assert.assertEquals(200, deleteAsServletResponse(((RestBaseController.ROOT_PATH) + "/layers/cite:Bridges?recurse=true")).getStatus());
        Assert.assertNull(CatalogRESTTestSupport.catalog.getLayerByName("cite:Bridges"));
        Assert.assertNull(CatalogRESTTestSupport.catalog.getFeatureTypeByName("cite", "Bridges"));
    }

    @Test
    public void testPutWorkspaceStyle() throws Exception {
        Catalog cat = getCatalog();
        Assert.assertNull(cat.getStyleByName("foo"));
        Assert.assertNull(cat.getStyleByName("cite", "foo"));
        String xml = "<style>" + (("<name>foo</name>" + "<filename>foo.sld</filename>") + "</style>");
        MockHttpServletResponse response = postAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/cite/styles"), xml);
        System.out.println(response.getContentAsString());
        Assert.assertEquals(201, response.getStatus());
        Assert.assertNotNull(cat.getStyleByName("cite", "foo"));
        xml = "<layer>" + ((((("<defaultStyle>" + "<name>foo</name>") + "<workspace>cite</workspace>") + "</defaultStyle>") + "<enabled>true</enabled>") + "</layer>");
        response = putAsServletResponse(((RestBaseController.ROOT_PATH) + "/layers/cite:Buildings"), xml, "application/xml");
        Assert.assertEquals(200, response.getStatus());
        LayerInfo l = cat.getLayerByName("cite:Buildings");
        Assert.assertNotNull(l.getDefaultStyle());
        Assert.assertEquals("foo", l.getDefaultStyle().getName());
        Assert.assertNotNull(l.getDefaultStyle().getWorkspace());
        Document dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/layers/cite:Buildings.xml"), 200);
        assertXpathExists("/layer/defaultStyle/name[text() = 'cite:foo']", dom);
        assertXpathExists("/layer/defaultStyle/workspace[text() = 'cite']", dom);
        assertXpathEvaluatesTo((("http://localhost:8080/geoserver" + (RestBaseController.ROOT_PATH)) + "/workspaces/cite/styles/foo.xml"), "//defaultStyle/atom:link/@href", dom);
    }

    @Test
    public void testPutWorkspaceAlternateStyle() throws Exception {
        Catalog cat = getCatalog();
        Assert.assertNull(cat.getStyleByName("foo"));
        Assert.assertNull(cat.getStyleByName("cite", "foo"));
        String xml = "<style>" + (("<name>foo</name>" + "<filename>foo.sld</filename>") + "</style>");
        MockHttpServletResponse response = postAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/cite/styles"), xml);
        Assert.assertEquals(201, response.getStatus());
        Assert.assertNotNull(cat.getStyleByName("cite", "foo"));
        xml = "<layer>" + ((((((("<styles>" + "<style>") + "<name>foo</name>") + "<workspace>cite</workspace>") + "</style>") + "</styles>") + "<enabled>true</enabled>") + "</layer>");
        response = putAsServletResponse(((RestBaseController.ROOT_PATH) + "/layers/cite:Buildings"), xml, "application/xml");
        Assert.assertEquals(200, response.getStatus());
        LayerInfo l = cat.getLayerByName("cite:Buildings");
        Assert.assertNotNull(l.getDefaultStyle());
        StyleInfo style = l.getStyles().iterator().next();
        Assert.assertEquals("foo", style.getName());
        Assert.assertNotNull(style.getWorkspace());
        Document dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/layers/cite:Buildings.xml"), 200);
        assertXpathExists("/layer/styles/style/name[text() = 'cite:foo']", dom);
        assertXpathExists("/layer/styles/style/workspace[text() = 'cite']", dom);
        assertXpathEvaluatesTo((("http://localhost:8080/geoserver" + (RestBaseController.ROOT_PATH)) + "/workspaces/cite/styles/foo.xml"), "//styles/style/atom:link/@href", dom);
    }

    @Test
    public void testPutDefaultWMSInterpolationMethod() throws Exception {
        Catalog cat = getCatalog();
        LayerInfo l = cat.getLayerByName("cite:Buildings");
        Assert.assertNotNull(l);
        Assert.assertNull(l.getDefaultWMSInterpolationMethod());
        Document dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/layers/cite:Buildings.xml"), 200);
        Assert.assertEquals("layer", dom.getDocumentElement().getNodeName());
        assertXpathNotExists("/layer/defaultWMSInterpolationMethod", dom);
        String xml = "<layer>" + (((("<defaultWMSInterpolationMethod>" + "Nearest") + "</defaultWMSInterpolationMethod>") + "<enabled>true</enabled>") + "</layer>");
        MockHttpServletResponse response = putAsServletResponse(((RestBaseController.ROOT_PATH) + "/layers/cite:Buildings"), xml, "application/xml");
        Assert.assertEquals(200, response.getStatus());
        l = cat.getLayerByName("cite:Buildings");
        Assert.assertNotNull(l.getDefaultWMSInterpolationMethod());
        Assert.assertEquals(Nearest, l.getDefaultWMSInterpolationMethod());
        dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/layers/cite:Buildings.xml"), 200);
        assertXpathEvaluatesTo("1", "count(/layer/defaultWMSInterpolationMethod)", dom);
        assertXpathExists("/layer/defaultWMSInterpolationMethod[text() = 'Nearest']", dom);
    }
}

