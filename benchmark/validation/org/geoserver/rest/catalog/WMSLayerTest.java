/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.rest.catalog;


import HttpStatus.CREATED;
import HttpStatus.NOT_FOUND;
import HttpStatus.OK;
import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.custommonkey.xmlunit.XMLUnit;
import org.custommonkey.xmlunit.XpathEngine;
import org.geoserver.catalog.TestHttpClientRule;
import org.geoserver.catalog.WMSLayerInfo;
import org.geoserver.platform.GeoServerExtensions;
import org.geoserver.rest.RestBaseController;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.w3c.dom.Document;


public class WMSLayerTest extends CatalogRESTTestSupport {
    @Rule
    public TestHttpClientRule clientMocker = new TestHttpClientRule();

    @Test
    public void testBeanPresent() throws Exception {
        Assert.assertThat(GeoServerExtensions.extensions(RestBaseController.class), Matchers.hasItem(Matchers.instanceOf(WMSLayerController.class)));
    }

    @Test
    public void testGetAllByWorkspace() throws Exception {
        MockHttpServletResponse response = getAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/sf/wmslayers.xml"));
        Assert.assertThat(response, HttpTestUtils.hasStatus(OK));
        Document dom = dom(HttpTestUtils.istream(response));
        Assert.assertEquals(CatalogRESTTestSupport.catalog.getResourcesByNamespace(CatalogRESTTestSupport.catalog.getNamespaceByPrefix("sf"), WMSLayerInfo.class).size(), dom.getElementsByTagName("wmsLayer").getLength());
    }

    @Test
    public void testGetAllByWMSStore() throws Exception {
        MockHttpServletResponse response = getAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/sf/wmsstores/demo/wmslayers.xml"));
        Assert.assertThat(response, HttpTestUtils.hasStatus(OK));
        Document dom = dom(HttpTestUtils.istream(response));
        Assert.assertEquals(1, dom.getElementsByTagName("wmsLayer").getLength());
        assertXpathEvaluatesTo("1", "count(//wmsLayer/name[text()='states'])", dom);
    }

    @Test
    public void testGetAllAvailable() throws Exception {
        Document dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/workspaces/sf/wmsstores/demo/wmslayers.xml?list=available"), 200);
        // print(dom)
        // can't control the demo server enough to check the type names, but it should have
        // something
        // more than just topp:states
        assertXpathExists("/list/wmsLayerName[text() = 'world4326']", dom);
        assertXpathExists("/list/wmsLayerName[text() = 'anotherLayer']", dom);
        assertXpathNotExists("/list/wmsLayerName[text() = 'topp:states']", dom);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void testGetAllAvailableJSON() throws Exception {
        MockHttpServletResponse response = getAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/sf/wmsstores/demo/wmslayers.json?list=available"));
        Assert.assertThat(response, HttpTestUtils.hasStatus(OK));
        JSON json = json(response);
        JSONArray names = ((JSONArray) (get("string")));
        Assert.assertThat(names, ((Matcher) (Matchers.containsInAnyOrder(Matchers.equalTo("world4326"), Matchers.equalTo("anotherLayer")))));
    }

    @Test
    public void testPutAllUnauthorized() throws Exception {
        Assert.assertEquals(405, putAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/sf/wmsstores/demo/wmslayers")).getStatus());
    }

    @Test
    public void testDeleteAllUnauthorized() throws Exception {
        Assert.assertEquals(405, deleteAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/sf/wmsstores/demo/wmslayers")).getStatus());
    }

    @Test
    public void testPostAsXML() throws Exception {
        Assert.assertThat(CatalogRESTTestSupport.catalog.getResourceByName("sf", "bugsites", WMSLayerInfo.class), Matchers.nullValue());
        String xml = "<wmsLayer>" + ((((("<name>bugsites</name>" + "<nativeName>world4326</nativeName>") + "<srs>EPSG:4326</srs>") + "<nativeCRS>EPSG:4326</nativeCRS>") + "<store>demo</store>") + "</wmsLayer>");
        MockHttpServletResponse response = postAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/sf/wmsstores/demo/wmslayers/"), xml, "text/xml");
        Assert.assertThat(response, HttpTestUtils.hasStatus(CREATED));
        Assert.assertThat(response, HttpTestUtils.hasHeader("Location", Matchers.endsWith("/workspaces/sf/wmsstores/demo/wmslayers/bugsites")));
        WMSLayerInfo layer = CatalogRESTTestSupport.catalog.getResourceByName("sf", "bugsites", WMSLayerInfo.class);
        Assert.assertThat(layer, Matchers.hasProperty("nativeBoundingBox", Matchers.notNullValue()));
    }

    @Test
    public void testPostAsXMLNoWorkspace() throws Exception {
        Assert.assertThat(CatalogRESTTestSupport.catalog.getResourceByName("sf", "bugsites", WMSLayerInfo.class), Matchers.nullValue());
        String xml = "<wmsLayer>" + ((((("<name>bugsites</name>" + "<nativeName>world4326</nativeName>") + "<srs>EPSG:4326</srs>") + "<nativeCRS>EPSG:4326</nativeCRS>") + "<store>demo</store>") + "</wmsLayer>");
        MockHttpServletResponse response = postAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/sf/wmslayers/"), xml, "text/xml");
        Assert.assertThat(response, HttpTestUtils.hasStatus(CREATED));
        Assert.assertThat(response, HttpTestUtils.hasHeader("Location", Matchers.endsWith("/workspaces/sf/wmslayers/bugsites")));
        WMSLayerInfo layer = CatalogRESTTestSupport.catalog.getResourceByName("sf", "bugsites", WMSLayerInfo.class);
        Assert.assertThat(layer, Matchers.hasProperty("nativeBoundingBox", Matchers.notNullValue()));
    }

    @Test
    public void testPostAsJSON() throws Exception {
        Assert.assertThat(CatalogRESTTestSupport.catalog.getResourceByName("sf", "bugsites", WMSLayerInfo.class), Matchers.nullValue());
        String json = "{" + ((((((("'wmsLayer':{" + "'name':'bugsites',") + "'nativeName':'world4326',") + "'srs':'EPSG:4326',") + "'nativeCRS':'EPSG:4326',") + "'store':'demo'") + "}") + "}");
        MockHttpServletResponse response = postAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/sf/wmsstores/demo/wmslayers/"), json, "text/json");
        Assert.assertThat(response, HttpTestUtils.hasStatus(CREATED));
        Assert.assertThat(response, HttpTestUtils.hasHeader("Location", Matchers.endsWith("/workspaces/sf/wmsstores/demo/wmslayers/bugsites")));
        WMSLayerInfo layer = CatalogRESTTestSupport.catalog.getResourceByName("sf", "bugsites", WMSLayerInfo.class);
        Assert.assertThat(layer, Matchers.hasProperty("nativeBoundingBox", Matchers.notNullValue()));
    }

    @Test
    public void testPostToResource() throws Exception {
        String xml = "<wmsLayer>" + ("<name>og:restricted</name>" + "</wmsLayer>");
        MockHttpServletResponse response = postAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/sf/wmsstores/demo/wmslayers/states"), xml, "text/xml");
        Assert.assertEquals(405, response.getStatus());
    }

    @Test
    public void testGetAsXML() throws Exception {
        MockHttpServletResponse response = getAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/sf/wmslayers/states.xml"));
        Assert.assertThat(response, HttpTestUtils.hasStatus(OK));
        Document dom = dom(HttpTestUtils.istream(response));
        Assert.assertEquals("wmsLayer", dom.getDocumentElement().getNodeName());
        assertXpathEvaluatesTo("states", "/wmsLayer/name", dom);
        assertXpathEvaluatesTo("EPSG:4326", "/wmsLayer/srs", dom);
        Assert.assertEquals(CRS.decode("EPSG:4326").toWKT(), CatalogRESTTestSupport.xp.evaluate("/wmsLayer/nativeCRS", dom));
        WMSLayerInfo wml = CatalogRESTTestSupport.catalog.getResourceByName("sf", "states", WMSLayerInfo.class);
        ReferencedEnvelope re = wml.getLatLonBoundingBox();
        assertXpathEvaluatesTo(((re.getMinX()) + ""), "/wmsLayer/latLonBoundingBox/minx", dom);
        assertXpathEvaluatesTo(((re.getMaxX()) + ""), "/wmsLayer/latLonBoundingBox/maxx", dom);
        assertXpathEvaluatesTo(((re.getMinY()) + ""), "/wmsLayer/latLonBoundingBox/miny", dom);
        assertXpathEvaluatesTo(((re.getMaxY()) + ""), "/wmsLayer/latLonBoundingBox/maxy", dom);
    }

    @Test
    public void testGetAsJSON() throws Exception {
        JSON json = getAsJSON(((RestBaseController.ROOT_PATH) + "/workspaces/sf/wmslayers/states.json"));
        JSONObject featureType = getJSONObject("wmsLayer");
        Assert.assertNotNull(featureType);
        Assert.assertEquals("states", featureType.get("name"));
        Assert.assertEquals(CRS.decode("EPSG:4326").toWKT(), featureType.get("nativeCRS"));
        Assert.assertEquals("EPSG:4326", featureType.get("srs"));
    }

    @Test
    public void testGetAsHTML() throws Exception {
        Document dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/workspaces/sf/wmslayers/states.html"));
        // print(dom);
    }

    @Test
    public void testGetWrongWMSLayer() throws Exception {
        // Parameters for the request
        String ws = "sf";
        String wms = "demo";
        String wl = "statessssss";
        // Request path
        String requestPath = (((((RestBaseController.ROOT_PATH) + "/workspaces/") + ws) + "/wmslayers/") + wl) + ".html";
        String requestPath2 = (((((((RestBaseController.ROOT_PATH) + "/workspaces/") + ws) + "/wmsstores/") + wms) + "/wmslayers/") + wl) + ".html";
        // Exception path
        String exception = (("No such cascaded wms: " + ws) + ",") + wl;
        String exception2 = (((("No such cascaded wms layer: " + ws) + ",") + wms) + ",") + wl;
        // CASE 1: No wmsstore set
        // First request should thrown an exception
        MockHttpServletResponse response = getAsServletResponse(requestPath);
        Assert.assertThat(response, HttpTestUtils.hasStatus(NOT_FOUND));
        Assert.assertThat(response.getContentAsString(), Matchers.containsString(exception));
        // Same request with ?quietOnNotFound should not throw an exception
        response = getAsServletResponse((requestPath + "?quietOnNotFound=true"));
        Assert.assertThat(response, HttpTestUtils.hasStatus(NOT_FOUND));
        Assert.assertThat(response.getContentAsString(), Matchers.not(Matchers.containsString(exception)));
        // No exception thrown
        Assert.assertThat(response.getContentAsString(), Matchers.isEmptyString());
        // CASE 2: wmsstore set
        // First request should thrown an exception
        response = getAsServletResponse(requestPath2);
        Assert.assertThat(response, HttpTestUtils.hasStatus(NOT_FOUND));
        Assert.assertThat(response.getContentAsString(), Matchers.containsString(exception));
        // Same request with ?quietOnNotFound should not throw an exception
        response = getAsServletResponse((requestPath2 + "?quietOnNotFound=true"));
        Assert.assertThat(response, HttpTestUtils.hasStatus(NOT_FOUND));
        Assert.assertThat(response.getContentAsString(), Matchers.not(Matchers.containsString(exception)));
        // No exception thrown
        Assert.assertThat(response.getContentAsString(), Matchers.isEmptyString());
    }

    @Test
    public void testPut() throws Exception {
        String xml = "<wmsLayer>" + ("<title>Lots of states here</title>" + "</wmsLayer>");
        MockHttpServletResponse response = putAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/sf/wmsstores/demo/wmslayers/states"), xml, "text/xml");
        Assert.assertThat(response, HttpTestUtils.hasStatus(OK));
        Document dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/workspaces/sf/wmsstores/demo/wmslayers/states.xml"));
        assertXpathEvaluatesTo("Lots of states here", "/wmsLayer/title", dom);
        WMSLayerInfo wli = CatalogRESTTestSupport.catalog.getResourceByName("sf", "states", WMSLayerInfo.class);
        Assert.assertEquals("Lots of states here", wli.getTitle());
    }

    @Test
    public void testPutNonDestructive() throws Exception {
        WMSLayerInfo wli = CatalogRESTTestSupport.catalog.getResourceByName("sf", "states", WMSLayerInfo.class);
        wli.setEnabled(true);
        CatalogRESTTestSupport.catalog.save(wli);
        wli = CatalogRESTTestSupport.catalog.getResourceByName("sf", "states", WMSLayerInfo.class);
        Assert.assertTrue(wli.isEnabled());
        boolean isAdvertised = wli.isAdvertised();
        String xml = "<wmsLayer>" + ("<title>Lots of states here</title>" + "</wmsLayer>");
        MockHttpServletResponse response = putAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/sf/wmsstores/demo/wmslayers/states"), xml, "text/xml");
        Assert.assertEquals(200, response.getStatus());
        wli = CatalogRESTTestSupport.catalog.getResourceByName("sf", "states", WMSLayerInfo.class);
        Assert.assertTrue(wli.isEnabled());
        Assert.assertEquals(isAdvertised, wli.isAdvertised());
    }

    @Test
    public void testPutNonExistant() throws Exception {
        String xml = "<wmsLayer>" + ("<title>new title</title>" + "</wmsLayer>");
        MockHttpServletResponse response = putAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/sf/wmsstores/demo/wmslayers/bugsites"), xml, "text/xml");
        Assert.assertEquals(404, response.getStatus());
    }

    @Test
    public void testDelete() throws Exception {
        Assert.assertNotNull(CatalogRESTTestSupport.catalog.getResourceByName("sf", "states", WMSLayerInfo.class));
        Assert.assertThat(deleteAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/sf/wmsstores/demo/wmslayers/states")), HttpTestUtils.hasStatus(OK));
        Assert.assertNull(CatalogRESTTestSupport.catalog.getResourceByName("sf", "states", WMSLayerInfo.class));
    }

    @Test
    public void testDeleteNonExistant() throws Exception {
        Assert.assertEquals(404, deleteAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/sf/wmsstores/demo/wmslayers/NonExistent")).getStatus());
    }

    @Test
    public void testDeleteNonRecursive() throws Exception {
        addLayer();
        Assert.assertNotNull(CatalogRESTTestSupport.catalog.getResourceByName("sf", "states", WMSLayerInfo.class));
        Assert.assertEquals(403, deleteAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/sf/wmsstores/demo/wmslayers/states")).getStatus());
    }

    @Test
    public void testDeleteRecursive() throws Exception {
        addLayer();
        Assert.assertNotNull(CatalogRESTTestSupport.catalog.getLayerByName("sf:states"));
        Assert.assertNotNull(CatalogRESTTestSupport.catalog.getResourceByName("sf", "states", WMSLayerInfo.class));
        Assert.assertEquals(200, deleteAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/sf/wmsstores/demo/wmslayers/states?recurse=true")).getStatus());
        Assert.assertNull(CatalogRESTTestSupport.catalog.getLayerByName("sf:states"));
        Assert.assertNull(CatalogRESTTestSupport.catalog.getResourceByName("sf", "states", WMSLayerInfo.class));
    }

    @Test
    public void testResourceLink() throws Exception {
        addLayer();
        Document doc = getAsDOM(((RestBaseController.ROOT_PATH) + "/layers/states.xml"));
        XpathEngine xpath = XMLUnit.newXpathEngine();
        String resourceUrl = xpath.evaluate("//resource/atom:link/@href", doc);
        resourceUrl = resourceUrl.substring(resourceUrl.indexOf("/rest"));
        doc = getAsDOM(resourceUrl);
        assertXpathEvaluatesTo("states", "/wmsLayer/name", doc);
    }
}

