/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
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
import org.geoserver.catalog.TestHttpClientRule;
import org.geoserver.catalog.WMTSLayerInfo;
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


public class WMTSLayerTest extends CatalogRESTTestSupport {
    private static final String LAYER_NAME = "AMSR2_Snow_Water_Equivalent";

    private static final String ANOTHER_LAYER_NAME = "AMSR2_Soil_Moisture_NPD_Day";

    private static final String ANOTHER_LOCAL_NAME = "this_is_the_local_name";

    @Rule
    public TestHttpClientRule clientMocker = new TestHttpClientRule();

    @Test
    public void testBeanPresent() throws Exception {
        Assert.assertThat(GeoServerExtensions.extensions(RestBaseController.class), Matchers.hasItem(Matchers.instanceOf(WMTSLayerController.class)));
    }

    @Test
    public void testGetAllByWorkspace() throws Exception {
        MockHttpServletResponse response = getAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/sf/wmtslayers.xml"));
        Assert.assertThat(response, HttpTestUtils.hasStatus(OK));
        Document dom = dom(HttpTestUtils.istream(response));
        Assert.assertEquals(CatalogRESTTestSupport.catalog.getResourcesByNamespace(CatalogRESTTestSupport.catalog.getNamespaceByPrefix("sf"), WMTSLayerInfo.class).size(), dom.getElementsByTagName("wmtsLayer").getLength());
    }

    @Test
    public void testGetAllByWMTSStore() throws Exception {
        MockHttpServletResponse response = getAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/sf/wmtsstores/demo/layers.xml"));
        Assert.assertThat(response, HttpTestUtils.hasStatus(OK));
        Document dom = dom(HttpTestUtils.istream(response));
        Assert.assertEquals(1, dom.getElementsByTagName("wmtsLayer").getLength());
        assertXpathEvaluatesTo("1", (("count(//wmtsLayer/name[text()='" + (WMTSLayerTest.LAYER_NAME)) + "'])"), dom);
    }

    @Test
    public void testGetAllAvailable() throws Exception {
        Document dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/workspaces/sf/wmtsstores/demo/layers.xml?list=available"), 200);
        print(dom);
        // can't control the demo server enough to check the type names, but it should have
        // something
        // more than just topp:states
        assertXpathExists((("/list/wmtsLayerName[text() = '" + (WMTSLayerTest.LAYER_NAME)) + "']"), dom);
        assertXpathExists((("/list/wmtsLayerName[text() = '" + (WMTSLayerTest.ANOTHER_LAYER_NAME)) + "']"), dom);
        assertXpathNotExists((("/list/wmtsLayerName[text() = 'topp:" + (WMTSLayerTest.LAYER_NAME)) + "']"), dom);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void testGetAllAvailableJSON() throws Exception {
        MockHttpServletResponse response = getAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/sf/wmtsstores/demo/layers.json?list=available"));
        Assert.assertThat(response, HttpTestUtils.hasStatus(OK));
        JSON json = json(response);
        JSONArray names = ((JSONArray) (get("string")));
        Assert.assertThat(names, ((Matcher) (Matchers.hasItems(Matchers.equalTo(WMTSLayerTest.LAYER_NAME), Matchers.equalTo(WMTSLayerTest.ANOTHER_LAYER_NAME)))));
    }

    @Test
    public void testPutAllUnauthorized() throws Exception {
        Assert.assertEquals(405, putAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/sf/wmtsstores/demo/layers")).getStatus());
    }

    @Test
    public void testDeleteAllUnauthorized() throws Exception {
        Assert.assertEquals(405, deleteAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/sf/wmtsstores/demo/layers")).getStatus());
    }

    @Test
    public void testPostAsXML() throws Exception {
        Assert.assertThat(CatalogRESTTestSupport.catalog.getResourceByName("sf", WMTSLayerTest.ANOTHER_LOCAL_NAME, WMTSLayerInfo.class), Matchers.nullValue());
        String xml = ((((((((("<wmtsLayer>" + "<name>") + (WMTSLayerTest.ANOTHER_LOCAL_NAME)) + "</name>") + "<nativeName>") + (WMTSLayerTest.ANOTHER_LAYER_NAME)) + "</nativeName>") + "<srs>EPSG:4326</srs>") + "<nativeCRS>EPSG:4326</nativeCRS>") + "<store>demo</store>") + "</wmtsLayer>";
        MockHttpServletResponse response = postAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/sf/wmtsstores/demo/layers/"), xml, "text/xml");
        Assert.assertThat(response, HttpTestUtils.hasStatus(CREATED));
        Assert.assertThat(response, HttpTestUtils.hasHeader("Location", Matchers.endsWith(("/workspaces/sf/wmtsstores/demo/layers/" + (WMTSLayerTest.ANOTHER_LOCAL_NAME)))));
        WMTSLayerInfo layer = CatalogRESTTestSupport.catalog.getResourceByName("sf", WMTSLayerTest.ANOTHER_LOCAL_NAME, WMTSLayerInfo.class);
        Assert.assertThat(layer, Matchers.hasProperty("nativeBoundingBox", Matchers.notNullValue()));
    }

    @Test
    public void testPostAsXMLNoWorkspace() throws Exception {
        Assert.assertThat(CatalogRESTTestSupport.catalog.getResourceByName("sf", WMTSLayerTest.ANOTHER_LOCAL_NAME, WMTSLayerInfo.class), Matchers.nullValue());
        String xml = ((((((((("<wmtsLayer>" + "<name>") + (WMTSLayerTest.ANOTHER_LOCAL_NAME)) + "</name>") + "<nativeName>") + (WMTSLayerTest.ANOTHER_LAYER_NAME)) + "</nativeName>") + "<srs>EPSG:4326</srs>") + "<nativeCRS>EPSG:4326</nativeCRS>") + "<store>demo</store>") + "</wmtsLayer>";
        MockHttpServletResponse response = postAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/sf/wmtslayers/"), xml, "text/xml");
        Assert.assertThat(response, HttpTestUtils.hasStatus(CREATED));
        Assert.assertThat(response, HttpTestUtils.hasHeader("Location", Matchers.endsWith(("/workspaces/sf/wmtslayers/" + (WMTSLayerTest.ANOTHER_LOCAL_NAME)))));
        WMTSLayerInfo layer = CatalogRESTTestSupport.catalog.getResourceByName("sf", WMTSLayerTest.ANOTHER_LOCAL_NAME, WMTSLayerInfo.class);
        Assert.assertThat(layer, Matchers.hasProperty("nativeBoundingBox", Matchers.notNullValue()));
    }

    @Test
    public void testPostAsJSON() throws Exception {
        Assert.assertThat(CatalogRESTTestSupport.catalog.getResourceByName("sf", WMTSLayerTest.ANOTHER_LOCAL_NAME, WMTSLayerInfo.class), Matchers.nullValue());
        String json = (((((((((("{" + ("'wmtsLayer':{" + "'name':'")) + (WMTSLayerTest.ANOTHER_LOCAL_NAME)) + "',") + "'nativeName':'") + (WMTSLayerTest.ANOTHER_LAYER_NAME)) + "',") + "'srs':'EPSG:4326',") + "'nativeCRS':'EPSG:4326',") + "'store':'demo'") + "}") + "}";
        MockHttpServletResponse response = postAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/sf/wmtsstores/demo/layers/"), json, "text/json");
        Assert.assertThat(response, HttpTestUtils.hasStatus(CREATED));
        Assert.assertThat(response, HttpTestUtils.hasHeader("Location", Matchers.endsWith(("/workspaces/sf/wmtsstores/demo/layers/" + (WMTSLayerTest.ANOTHER_LOCAL_NAME)))));
        WMTSLayerInfo layer = CatalogRESTTestSupport.catalog.getResourceByName("sf", WMTSLayerTest.ANOTHER_LOCAL_NAME, WMTSLayerInfo.class);
        Assert.assertThat(layer, Matchers.hasProperty("nativeBoundingBox", Matchers.notNullValue()));
    }

    @Test
    public void testPostToResource() throws Exception {
        String xml = "<wmtsLayer>" + ("<name>og:restricted</name>" + "</wmtsLayer>");
        MockHttpServletResponse response = postAsServletResponse((((RestBaseController.ROOT_PATH) + "/workspaces/sf/wmtsstores/demo/layers/") + (WMTSLayerTest.LAYER_NAME)), xml, "text/xml");
        Assert.assertEquals(405, response.getStatus());
    }

    @Test
    public void testGetAsXML() throws Exception {
        MockHttpServletResponse response = getAsServletResponse(((((RestBaseController.ROOT_PATH) + "/workspaces/sf/wmtslayers/") + (WMTSLayerTest.LAYER_NAME)) + ".xml"));
        Assert.assertThat(response, HttpTestUtils.hasStatus(OK));
        Document dom = dom(HttpTestUtils.istream(response));
        Assert.assertEquals("wmtsLayer", dom.getDocumentElement().getNodeName());
        assertXpathEvaluatesTo(WMTSLayerTest.LAYER_NAME, "/wmtsLayer/name", dom);
        assertXpathEvaluatesTo("EPSG:4326", "/wmtsLayer/srs", dom);
        Assert.assertEquals(CRS.decode("EPSG:4326").toWKT(), CatalogRESTTestSupport.xp.evaluate("/wmtsLayer/nativeCRS", dom));
        WMTSLayerInfo wml = CatalogRESTTestSupport.catalog.getResourceByName("sf", WMTSLayerTest.LAYER_NAME, WMTSLayerInfo.class);
        ReferencedEnvelope re = wml.getLatLonBoundingBox();
        assertXpathEvaluatesTo(((re.getMinX()) + ""), "/wmtsLayer/latLonBoundingBox/minx", dom);
        assertXpathEvaluatesTo(((re.getMaxX()) + ""), "/wmtsLayer/latLonBoundingBox/maxx", dom);
        assertXpathEvaluatesTo(((re.getMinY()) + ""), "/wmtsLayer/latLonBoundingBox/miny", dom);
        assertXpathEvaluatesTo(((re.getMaxY()) + ""), "/wmtsLayer/latLonBoundingBox/maxy", dom);
    }

    @Test
    public void testGetAsJSON() throws Exception {
        JSON json = getAsJSON(((((RestBaseController.ROOT_PATH) + "/workspaces/sf/wmtslayers/") + (WMTSLayerTest.LAYER_NAME)) + ".json"));
        JSONObject featureType = getJSONObject("wmtsLayer");
        Assert.assertNotNull(featureType);
        Assert.assertEquals(WMTSLayerTest.LAYER_NAME, featureType.get("name"));
        Assert.assertEquals(CRS.decode("EPSG:4326").toWKT(), featureType.get("nativeCRS"));
        Assert.assertEquals("EPSG:4326", featureType.get("srs"));
    }

    @Test
    public void testGetAsHTML() throws Exception {
        MockHttpServletResponse response = getAsServletResponse(((((RestBaseController.ROOT_PATH) + "/workspaces/sf/wmtslayers/") + (WMTSLayerTest.LAYER_NAME)) + ".html"));
        Assert.assertThat(response, HttpTestUtils.hasStatus(OK));
    }

    @Test
    public void testGetWrongWMTSLayer() throws Exception {
        // Parameters for the request
        String ws = "sf";
        String wmts = "demo";
        String wl = "statessssss";
        // Request path
        String requestPath = (((((RestBaseController.ROOT_PATH) + "/workspaces/") + ws) + "/wmtslayers/") + wl) + ".html";
        String requestPath2 = (((((((RestBaseController.ROOT_PATH) + "/workspaces/") + ws) + "/wmtsstores/") + wmts) + "/layers/") + wl) + ".html";
        // Exception path
        String exception = (("No such cascaded wmts: " + ws) + ",") + wl;
        String exception2 = (((("No such cascaded wmts layer: " + ws) + ",") + wmts) + ",") + wl;
        // CASE 1: No wmtsstore set
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
        // CASE 2: wmtsstore set
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
        String xml = "<wmtsLayer>" + ("<title>Lots of states here</title>" + "</wmtsLayer>");
        MockHttpServletResponse response = putAsServletResponse((((RestBaseController.ROOT_PATH) + "/workspaces/sf/wmtsstores/demo/layers/") + (WMTSLayerTest.LAYER_NAME)), xml, "text/xml");
        Assert.assertThat(response, HttpTestUtils.hasStatus(OK));
        Document dom = getAsDOM(((((RestBaseController.ROOT_PATH) + "/workspaces/sf/wmtsstores/demo/layers/") + (WMTSLayerTest.LAYER_NAME)) + ".xml"));
        assertXpathEvaluatesTo("Lots of states here", "/wmtsLayer/title", dom);
        WMTSLayerInfo wli = CatalogRESTTestSupport.catalog.getResourceByName("sf", WMTSLayerTest.LAYER_NAME, WMTSLayerInfo.class);
        Assert.assertEquals("Lots of states here", wli.getTitle());
    }

    @Test
    public void testPutNonDestructive() throws Exception {
        WMTSLayerInfo wli = CatalogRESTTestSupport.catalog.getResourceByName("sf", WMTSLayerTest.LAYER_NAME, WMTSLayerInfo.class);
        wli.setEnabled(true);
        CatalogRESTTestSupport.catalog.save(wli);
        wli = CatalogRESTTestSupport.catalog.getResourceByName("sf", WMTSLayerTest.LAYER_NAME, WMTSLayerInfo.class);
        Assert.assertTrue(wli.isEnabled());
        boolean isAdvertised = wli.isAdvertised();
        String xml = "<wmtsLayer>" + ("<title>Lots of states here</title>" + "</wmtsLayer>");
        MockHttpServletResponse response = putAsServletResponse((((RestBaseController.ROOT_PATH) + "/workspaces/sf/wmtsstores/demo/layers/") + (WMTSLayerTest.LAYER_NAME)), xml, "text/xml");
        Assert.assertEquals(200, response.getStatus());
        wli = CatalogRESTTestSupport.catalog.getResourceByName("sf", WMTSLayerTest.LAYER_NAME, WMTSLayerInfo.class);
        Assert.assertTrue(wli.isEnabled());
        Assert.assertEquals(isAdvertised, wli.isAdvertised());
    }

    @Test
    public void testPutNonExistant() throws Exception {
        String xml = "<wmtsLayer>" + ("<title>new title</title>" + "</wmtsLayer>");
        MockHttpServletResponse response = putAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/sf/wmtsstores/demo/layers/bugsites"), xml, "text/xml");
        Assert.assertEquals(404, response.getStatus());
    }

    @Test
    public void testDelete() throws Exception {
        Assert.assertNotNull(CatalogRESTTestSupport.catalog.getResourceByName("sf", WMTSLayerTest.LAYER_NAME, WMTSLayerInfo.class));
        Assert.assertThat(deleteAsServletResponse((((RestBaseController.ROOT_PATH) + "/workspaces/sf/wmtsstores/demo/layers/") + (WMTSLayerTest.LAYER_NAME))), HttpTestUtils.hasStatus(OK));
        Assert.assertNull(CatalogRESTTestSupport.catalog.getResourceByName("sf", WMTSLayerTest.LAYER_NAME, WMTSLayerInfo.class));
    }

    @Test
    public void testDeleteNonExistant() throws Exception {
        Assert.assertEquals(404, deleteAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/sf/wmtsstores/demo/layers/NonExistent")).getStatus());
    }

    @Test
    public void testDeleteNonRecursive() throws Exception {
        addLayer();
        Assert.assertNotNull(CatalogRESTTestSupport.catalog.getResourceByName("sf", WMTSLayerTest.LAYER_NAME, WMTSLayerInfo.class));
        Assert.assertEquals(403, deleteAsServletResponse((((RestBaseController.ROOT_PATH) + "/workspaces/sf/wmtsstores/demo/layers/") + (WMTSLayerTest.LAYER_NAME))).getStatus());
    }

    @Test
    public void testDeleteRecursive() throws Exception {
        addLayer();
        Assert.assertNotNull(CatalogRESTTestSupport.catalog.getLayerByName(("sf:" + (WMTSLayerTest.LAYER_NAME))));
        Assert.assertNotNull(CatalogRESTTestSupport.catalog.getResourceByName("sf", WMTSLayerTest.LAYER_NAME, WMTSLayerInfo.class));
        Assert.assertEquals(200, deleteAsServletResponse(((((RestBaseController.ROOT_PATH) + "/workspaces/sf/wmtsstores/demo/layers/") + (WMTSLayerTest.LAYER_NAME)) + "?recurse=true")).getStatus());
        Assert.assertNull(CatalogRESTTestSupport.catalog.getLayerByName(("sf:" + (WMTSLayerTest.LAYER_NAME))));
        Assert.assertNull(CatalogRESTTestSupport.catalog.getResourceByName("sf", WMTSLayerTest.LAYER_NAME, WMTSLayerInfo.class));
    }
}

