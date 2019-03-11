/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.rest.catalog;


import HttpStatus.CREATED;
import HttpStatus.FORBIDDEN;
import java.util.List;
import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.geoserver.catalog.TestHttpClientRule;
import org.geoserver.catalog.WMTSLayerInfo;
import org.geoserver.catalog.WMTSStoreInfo;
import org.geoserver.platform.GeoServerExtensions;
import org.geoserver.rest.RestBaseController;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


public class WMTSStoreTest extends CatalogRESTTestSupport {
    private static final String LAYER_NAME = "AMSR2_Snow_Water_Equivalent";

    @ClassRule
    public static TestHttpClientRule clientMocker = new TestHttpClientRule();

    private static String capabilities;

    @Test
    public void testBeanPresent() throws Exception {
        Assert.assertThat(GeoServerExtensions.extensions(RestBaseController.class), Matchers.hasItem(Matchers.instanceOf(WMTSStoreController.class)));
    }

    @Test
    public void testGetAllAsXML() throws Exception {
        Document dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/workspaces/sf/wmtsstores.xml"));
        Assert.assertEquals("wmtsStores", dom.getDocumentElement().getNodeName());
        Assert.assertEquals(size(), dom.getElementsByTagName("wmtsStore").getLength());
    }

    @Test
    public void testGetAllAsJSON() throws Exception {
        JSON json = getAsJSON(((RestBaseController.ROOT_PATH) + "/workspaces/sf/wmtsstores.json"));
        Assert.assertTrue((json instanceof JSONObject));
        Object stores = getJSONObject("wmtsStores").get("wmtsStore");
        Assert.assertNotNull(stores);
        if (stores instanceof JSONArray) {
            Assert.assertEquals(size(), size());
        } else {
            Assert.assertEquals(1, size());
        }
    }

    @Test
    public void testGetAllAsHTML() throws Exception {
        Document dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/workspaces/sf/wmtsstores.html"));
        List<WMTSStoreInfo> stores = CatalogRESTTestSupport.catalog.getStoresByWorkspace("sf", WMTSStoreInfo.class);
        NodeList links = CatalogRESTTestSupport.xp.getMatchingNodes("//html:a", dom);
        Assert.assertEquals(stores.size(), links.getLength());
        for (int i = 0; i < (stores.size()); i++) {
            WMTSStoreInfo store = stores.get(i);
            Element link = ((Element) (links.item(i)));
            Assert.assertTrue(link.getAttribute("href").endsWith(((store.getName()) + ".html")));
        }
    }

    @Test
    public void testPutAllUnauthorized() throws Exception {
        Assert.assertEquals(405, putAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/sf/wmtsstores")).getStatus());
    }

    @Test
    public void testDeleteAllUnauthorized() throws Exception {
        Assert.assertEquals(405, deleteAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/sf/wmtsstores")).getStatus());
    }

    @Test
    public void testGetAsXML() throws Exception {
        Document dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/workspaces/sf/wmtsstores/demo.xml"));
        Assert.assertEquals("wmtsStore", dom.getDocumentElement().getNodeName());
        Assert.assertEquals("demo", CatalogRESTTestSupport.xp.evaluate("/wmtsStore/name", dom));
        Assert.assertEquals("sf", CatalogRESTTestSupport.xp.evaluate("/wmtsStore/workspace/name", dom));
        assertXpathExists("/wmtsStore/capabilitiesURL", dom);
    }

    @Test
    public void testGetAsHTML() throws Exception {
        WMTSStoreInfo store = CatalogRESTTestSupport.catalog.getStoreByName("sf", "demo", WMTSStoreInfo.class);
        Assert.assertThat(store, Matchers.notNullValue());
        List<WMTSLayerInfo> resources = CatalogRESTTestSupport.catalog.getResourcesByStore(store, WMTSLayerInfo.class);
        Assert.assertThat(resources, Matchers.not(Matchers.empty()));
        Document dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/workspaces/sf/wmtsstores/demo.html"));
        WMTSStoreInfo wmts = CatalogRESTTestSupport.catalog.getStoreByName("demo", WMTSStoreInfo.class);
        List<WMTSLayerInfo> wmtsLayers = CatalogRESTTestSupport.catalog.getResourcesByStore(wmts, WMTSLayerInfo.class);
        NodeList links = CatalogRESTTestSupport.xp.getMatchingNodes("//html:a", dom);
        Assert.assertEquals(wmtsLayers.size(), links.getLength());
        for (int i = 0; i < (wmtsLayers.size()); i++) {
            WMTSLayerInfo wl = wmtsLayers.get(i);
            Element link = ((Element) (links.item(i)));
            Assert.assertTrue(link.getAttribute("href").endsWith(((wl.getName()) + ".html")));
        }
    }

    @Test
    public void testGetWrongWMTSStore() throws Exception {
        // Parameters for the request
        String ws = "sf";
        String wmts = "sfssssss";
        // Request path
        String requestPath = (((((RestBaseController.ROOT_PATH) + "/workspaces/") + ws) + "/wmtsstores/") + wmts) + ".html";
        // Exception path
        String exception = (("No such wmts store: " + ws) + ",") + wmts;
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
    }

    @Test
    public void testPostAsXML() throws Exception {
        String xml = "<wmtsStore>" + ((("<name>newWMTSStore</name>" + "<capabilitiesURL>http://somehost/wmts?</capabilitiesURL>") + "<workspace>sf</workspace>") + "</wmtsStore>");
        MockHttpServletResponse response = postAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/sf/wmtsstores"), xml, "text/xml");
        Assert.assertThat(response, HttpTestUtils.hasStatus(CREATED));
        Assert.assertThat(response, HttpTestUtils.hasHeader("Location", Matchers.endsWith("/workspaces/sf/wmtsstores/newWMTSStore")));
        WMTSStoreInfo newStore = CatalogRESTTestSupport.catalog.getStoreByName("newWMTSStore", WMTSStoreInfo.class);
        Assert.assertNotNull(newStore);
        Assert.assertEquals("http://somehost/wmts?", newStore.getCapabilitiesURL());
    }

    @Test
    public void testPostAsXMLNoWorkspace() throws Exception {
        String xml = "<wmtsStore>" + (("<name>newWMTSStore</name>" + "<capabilitiesURL>http://somehost/wmts?</capabilitiesURL>") + "</wmtsStore>");
        MockHttpServletResponse response = postAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/sf/wmtsstores"), xml, "text/xml");
        Assert.assertThat(response, HttpTestUtils.hasStatus(CREATED));
        Assert.assertThat(response, HttpTestUtils.hasHeader("Location", Matchers.endsWith("/workspaces/sf/wmtsstores/newWMTSStore")));
        WMTSStoreInfo newStore = CatalogRESTTestSupport.catalog.getStoreByName("newWMTSStore", WMTSStoreInfo.class);
        Assert.assertNotNull(newStore);
        Assert.assertEquals("http://somehost/wmts?", newStore.getCapabilitiesURL());
    }

    @Test
    public void testGetAsJSON() throws Exception {
        JSON json = getAsJSON(((RestBaseController.ROOT_PATH) + "/workspaces/sf/wmtsstores/demo.json"));
        JSONObject store = ((JSONObject) (json)).getJSONObject("wmtsStore");
        Assert.assertNotNull(store);
        Assert.assertEquals("demo", store.get("name"));
        Assert.assertEquals("sf", store.getJSONObject("workspace").get("name"));
        Assert.assertEquals(WMTSStoreTest.capabilities, store.getString("capabilitiesURL"));
    }

    @Test
    public void testPostAsJSON() throws Exception {
        removeStore("sf", "newWMTSStore");
        String json = "{'wmtsStore':{" + (((("'capabilitiesURL': 'http://somehost/wmts?'," + "'workspace':'sf',") + "'name':'newWMTSStore',") + "}") + "}");
        MockHttpServletResponse response = postAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/sf/wmtsstores"), json, "text/json");
        Assert.assertEquals(201, response.getStatus());
        Assert.assertNotNull(response.getHeader("Location"));
        Assert.assertTrue(response.getHeader("Location").endsWith("/workspaces/sf/wmtsstores/newWMTSStore"));
        WMTSStoreInfo newStore = CatalogRESTTestSupport.catalog.getStoreByName("newWMTSStore", WMTSStoreInfo.class);
        Assert.assertNotNull(newStore);
        Assert.assertEquals("http://somehost/wmts?", newStore.getCapabilitiesURL());
    }

    @Test
    public void testPostToResource() throws Exception {
        String xml = "<wmtsStore>" + (("<name>demo</name>" + "<enabled>false</enabled>") + "</wmtsStore>");
        MockHttpServletResponse response = postAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/sf/wmtsstores/demo"), xml, "text/xml");
        Assert.assertEquals(405, response.getStatus());
    }

    @Test
    public void testPut() throws Exception {
        Document dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/workspaces/sf/wmtsstores/demo.xml"));
        assertXpathEvaluatesTo("true", "/wmtsStore/enabled", dom);
        String xml = "<wmtsStore>" + (("<name>demo</name>" + "<enabled>false</enabled>") + "</wmtsStore>");
        MockHttpServletResponse response = putAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/sf/wmtsstores/demo"), xml, "text/xml");
        Assert.assertEquals(200, response.getStatus());
        dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/workspaces/sf/wmtsstores/demo.xml"));
        assertXpathEvaluatesTo("false", "/wmtsStore/enabled", dom);
        Assert.assertFalse(CatalogRESTTestSupport.catalog.getStoreByName("sf", "demo", WMTSStoreInfo.class).isEnabled());
    }

    @Test
    public void testPutNonDestructive() throws Exception {
        WMTSStoreInfo wsi = CatalogRESTTestSupport.catalog.getStoreByName("sf", "demo", WMTSStoreInfo.class);
        wsi.setEnabled(true);
        CatalogRESTTestSupport.catalog.save(wsi);
        Assert.assertTrue(wsi.isEnabled());
        int maxConnections = wsi.getMaxConnections();
        int readTimeout = wsi.getReadTimeout();
        int connectTimeout = wsi.getConnectTimeout();
        boolean useConnectionPooling = wsi.isUseConnectionPooling();
        String xml = "<wmtsStore>" + ("<name>demo</name>" + "</wmtsStore>");
        MockHttpServletResponse response = putAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/sf/wmtsstores/demo"), xml, "text/xml");
        Assert.assertEquals(200, response.getStatus());
        wsi = CatalogRESTTestSupport.catalog.getStoreByName("sf", "demo", WMTSStoreInfo.class);
        Assert.assertTrue(wsi.isEnabled());
        Assert.assertEquals(maxConnections, wsi.getMaxConnections());
        Assert.assertEquals(readTimeout, wsi.getReadTimeout());
        Assert.assertEquals(connectTimeout, wsi.getConnectTimeout());
        Assert.assertEquals(useConnectionPooling, wsi.isUseConnectionPooling());
    }

    @Test
    public void testPutNonExistant() throws Exception {
        String xml = "<wmtsStore>" + ("<name>changed</name>" + "</wmtsStore>");
        MockHttpServletResponse response = putAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/sf/wmtsstores/nonExistant"), xml, "text/xml");
        Assert.assertEquals(404, response.getStatus());
    }

    @Test
    public void testDeleteNonExistant() throws Exception {
        Assert.assertEquals(404, deleteAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/sf/datastores/nonExistant")).getStatus());
    }

    @Test
    public void testDelete() throws Exception {
        removeStore("sf", "newWMTSStore");
        testPostAsXML();
        Assert.assertNotNull(CatalogRESTTestSupport.catalog.getStoreByName("sf", "newWMTSStore", WMTSStoreInfo.class));
        Assert.assertEquals(200, deleteAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/sf/wmtsstores/newWMTSStore")).getStatus());
        Assert.assertNull(CatalogRESTTestSupport.catalog.getStoreByName("sf", "newWMTSStore", WMTSStoreInfo.class));
    }

    @Test
    public void testPutNameChangeForbidden() throws Exception {
        String xml = "<wmtsStore>" + ("<name>newName</name>" + "</wmtsStore>");
        Assert.assertEquals(403, putAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/sf/wmtsstores/demo"), xml, "text/xml").getStatus());
    }

    @Test
    public void testPutWorkspaceChangeForbidden() throws Exception {
        String xml = "<wmtsStore>" + ("<workspace>gs</workspace>" + "</wmtsStore>");
        MockHttpServletResponse response = putAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/sf/wmtsstores/demo"), xml, "text/xml");
        Assert.assertThat(response, HttpTestUtils.hasStatus(FORBIDDEN));
    }
}

