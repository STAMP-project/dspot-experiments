/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
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
import org.geoserver.catalog.WMSLayerInfo;
import org.geoserver.catalog.WMSStoreInfo;
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


public class WMSStoreTest extends CatalogRESTTestSupport {
    @ClassRule
    public static TestHttpClientRule clientMocker = new TestHttpClientRule();

    private static String capabilities;

    @Test
    public void testBeanPresent() throws Exception {
        Assert.assertThat(GeoServerExtensions.extensions(RestBaseController.class), Matchers.hasItem(Matchers.instanceOf(WMSStoreController.class)));
    }

    @Test
    public void testGetAllAsXML() throws Exception {
        Document dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/workspaces/sf/wmsstores.xml"));
        Assert.assertEquals("wmsStores", dom.getDocumentElement().getNodeName());
        Assert.assertEquals(size(), dom.getElementsByTagName("wmsStore").getLength());
    }

    @Test
    public void testGetAllAsJSON() throws Exception {
        JSON json = getAsJSON(((RestBaseController.ROOT_PATH) + "/workspaces/sf/wmsstores.json"));
        Assert.assertTrue((json instanceof JSONObject));
        Object stores = getJSONObject("wmsStores").get("wmsStore");
        Assert.assertNotNull(stores);
        if (stores instanceof JSONArray) {
            Assert.assertEquals(size(), size());
        } else {
            Assert.assertEquals(1, size());
        }
    }

    @Test
    public void testGetAllAsHTML() throws Exception {
        Document dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/workspaces/sf/wmsstores.html"));
        List<WMSStoreInfo> stores = CatalogRESTTestSupport.catalog.getStoresByWorkspace("sf", WMSStoreInfo.class);
        NodeList links = CatalogRESTTestSupport.xp.getMatchingNodes("//html:a", dom);
        Assert.assertEquals(stores.size(), links.getLength());
        for (int i = 0; i < (stores.size()); i++) {
            WMSStoreInfo store = stores.get(i);
            Element link = ((Element) (links.item(i)));
            Assert.assertTrue(link.getAttribute("href").endsWith(((store.getName()) + ".html")));
        }
    }

    @Test
    public void testPutAllUnauthorized() throws Exception {
        Assert.assertEquals(405, putAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/sf/wmsstores")).getStatus());
    }

    @Test
    public void testDeleteAllUnauthorized() throws Exception {
        Assert.assertEquals(405, deleteAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/sf/wmsstores")).getStatus());
    }

    @Test
    public void testGetAsXML() throws Exception {
        Document dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/workspaces/sf/wmsstores/demo.xml"));
        Assert.assertEquals("wmsStore", dom.getDocumentElement().getNodeName());
        Assert.assertEquals("demo", CatalogRESTTestSupport.xp.evaluate("/wmsStore/name", dom));
        Assert.assertEquals("sf", CatalogRESTTestSupport.xp.evaluate("/wmsStore/workspace/name", dom));
        assertXpathExists("/wmsStore/capabilitiesURL", dom);
    }

    @Test
    public void testGetAsHTML() throws Exception {
        WMSStoreInfo store = CatalogRESTTestSupport.catalog.getStoreByName("sf", "demo", WMSStoreInfo.class);
        Assert.assertThat(store, Matchers.notNullValue());
        List<WMSLayerInfo> resources = CatalogRESTTestSupport.catalog.getResourcesByStore(store, WMSLayerInfo.class);
        Assert.assertThat(resources, Matchers.not(Matchers.empty()));
        Document dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/workspaces/sf/wmsstores/demo.html"));
        WMSStoreInfo wms = CatalogRESTTestSupport.catalog.getStoreByName("demo", WMSStoreInfo.class);
        List<WMSLayerInfo> wmsLayers = CatalogRESTTestSupport.catalog.getResourcesByStore(wms, WMSLayerInfo.class);
        NodeList links = CatalogRESTTestSupport.xp.getMatchingNodes("//html:a", dom);
        Assert.assertEquals(wmsLayers.size(), links.getLength());
        for (int i = 0; i < (wmsLayers.size()); i++) {
            WMSLayerInfo wl = wmsLayers.get(i);
            Element link = ((Element) (links.item(i)));
            Assert.assertTrue(link.getAttribute("href").endsWith(((wl.getName()) + ".html")));
        }
    }

    @Test
    public void testGetWrongWMSStore() throws Exception {
        // Parameters for the request
        String ws = "sf";
        String wms = "sfssssss";
        // Request path
        String requestPath = (((((RestBaseController.ROOT_PATH) + "/workspaces/") + ws) + "/wmsstores/") + wms) + ".html";
        // Exception path
        String exception = (("No such wms store: " + ws) + ",") + wms;
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
        String xml = "<wmsStore>" + ((("<name>newWMSStore</name>" + "<capabilitiesURL>http://somehost/wms?</capabilitiesURL>") + "<workspace>sf</workspace>") + "</wmsStore>");
        MockHttpServletResponse response = postAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/sf/wmsstores"), xml, "text/xml");
        Assert.assertThat(response, HttpTestUtils.hasStatus(CREATED));
        Assert.assertThat(response, HttpTestUtils.hasHeader("Location", Matchers.endsWith("/workspaces/sf/wmsstores/newWMSStore")));
        WMSStoreInfo newStore = CatalogRESTTestSupport.catalog.getStoreByName("newWMSStore", WMSStoreInfo.class);
        Assert.assertNotNull(newStore);
        Assert.assertEquals("http://somehost/wms?", newStore.getCapabilitiesURL());
    }

    @Test
    public void testPostAsXMLNoWorkspace() throws Exception {
        String xml = "<wmsStore>" + (("<name>newWMSStore</name>" + "<capabilitiesURL>http://somehost/wms?</capabilitiesURL>") + "</wmsStore>");
        MockHttpServletResponse response = postAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/sf/wmsstores"), xml, "text/xml");
        Assert.assertThat(response, HttpTestUtils.hasStatus(CREATED));
        Assert.assertThat(response, HttpTestUtils.hasHeader("Location", Matchers.endsWith("/workspaces/sf/wmsstores/newWMSStore")));
        WMSStoreInfo newStore = CatalogRESTTestSupport.catalog.getStoreByName("newWMSStore", WMSStoreInfo.class);
        Assert.assertNotNull(newStore);
        Assert.assertEquals("http://somehost/wms?", newStore.getCapabilitiesURL());
    }

    @Test
    public void testGetAsJSON() throws Exception {
        JSON json = getAsJSON(((RestBaseController.ROOT_PATH) + "/workspaces/sf/wmsstores/demo.json"));
        JSONObject store = ((JSONObject) (json)).getJSONObject("wmsStore");
        Assert.assertNotNull(store);
        Assert.assertEquals("demo", store.get("name"));
        Assert.assertEquals("sf", store.getJSONObject("workspace").get("name"));
        Assert.assertEquals(WMSStoreTest.capabilities, store.getString("capabilitiesURL"));
    }

    @Test
    public void testPostAsJSON() throws Exception {
        removeStore("sf", "newWMSStore");
        String json = "{'wmsStore':{" + (((("'capabilitiesURL': 'http://somehost/wms?'," + "'workspace':'sf',") + "'name':'newWMSStore',") + "}") + "}");
        MockHttpServletResponse response = postAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/sf/wmsstores"), json, "text/json");
        Assert.assertEquals(201, response.getStatus());
        Assert.assertNotNull(response.getHeader("Location"));
        Assert.assertTrue(response.getHeader("Location").endsWith("/workspaces/sf/wmsstores/newWMSStore"));
        WMSStoreInfo newStore = CatalogRESTTestSupport.catalog.getStoreByName("newWMSStore", WMSStoreInfo.class);
        Assert.assertNotNull(newStore);
        Assert.assertEquals("http://somehost/wms?", newStore.getCapabilitiesURL());
    }

    @Test
    public void testPostToResource() throws Exception {
        String xml = "<wmsStore>" + (("<name>demo</name>" + "<enabled>false</enabled>") + "</wmsStore>");
        MockHttpServletResponse response = postAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/sf/wmsstores/demo"), xml, "text/xml");
        Assert.assertEquals(405, response.getStatus());
    }

    @Test
    public void testPut() throws Exception {
        Document dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/workspaces/sf/wmsstores/demo.xml"));
        assertXpathEvaluatesTo("true", "/wmsStore/enabled", dom);
        String xml = "<wmsStore>" + (("<name>demo</name>" + "<enabled>false</enabled>") + "</wmsStore>");
        MockHttpServletResponse response = putAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/sf/wmsstores/demo"), xml, "text/xml");
        Assert.assertEquals(200, response.getStatus());
        dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/workspaces/sf/wmsstores/demo.xml"));
        assertXpathEvaluatesTo("false", "/wmsStore/enabled", dom);
        Assert.assertFalse(CatalogRESTTestSupport.catalog.getStoreByName("sf", "demo", WMSStoreInfo.class).isEnabled());
    }

    @Test
    public void testPutNonDestructive() throws Exception {
        WMSStoreInfo wsi = CatalogRESTTestSupport.catalog.getStoreByName("sf", "demo", WMSStoreInfo.class);
        wsi.setEnabled(true);
        CatalogRESTTestSupport.catalog.save(wsi);
        Assert.assertTrue(wsi.isEnabled());
        int maxConnections = wsi.getMaxConnections();
        int readTimeout = wsi.getReadTimeout();
        int connectTimeout = wsi.getConnectTimeout();
        boolean useConnectionPooling = wsi.isUseConnectionPooling();
        String xml = "<wmsStore>" + ("<name>demo</name>" + "</wmsStore>");
        MockHttpServletResponse response = putAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/sf/wmsstores/demo"), xml, "text/xml");
        Assert.assertEquals(200, response.getStatus());
        wsi = CatalogRESTTestSupport.catalog.getStoreByName("sf", "demo", WMSStoreInfo.class);
        Assert.assertTrue(wsi.isEnabled());
        Assert.assertEquals(maxConnections, wsi.getMaxConnections());
        Assert.assertEquals(readTimeout, wsi.getReadTimeout());
        Assert.assertEquals(connectTimeout, wsi.getConnectTimeout());
        Assert.assertEquals(useConnectionPooling, wsi.isUseConnectionPooling());
    }

    @Test
    public void testPutNonExistant() throws Exception {
        String xml = "<wmsStore>" + ("<name>changed</name>" + "</wmsStore>");
        MockHttpServletResponse response = putAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/sf/wmsstores/nonExistant"), xml, "text/xml");
        Assert.assertEquals(404, response.getStatus());
    }

    @Test
    public void testDeleteNonExistant() throws Exception {
        Assert.assertEquals(404, deleteAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/sf/datastores/nonExistant")).getStatus());
    }

    @Test
    public void testDelete() throws Exception {
        removeStore("sf", "newWMSStore");
        testPostAsXML();
        Assert.assertNotNull(CatalogRESTTestSupport.catalog.getStoreByName("sf", "newWMSStore", WMSStoreInfo.class));
        Assert.assertEquals(200, deleteAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/sf/wmsstores/newWMSStore")).getStatus());
        Assert.assertNull(CatalogRESTTestSupport.catalog.getStoreByName("sf", "newWMSStore", WMSStoreInfo.class));
    }

    // public void testDeleteNonEmptyForbidden() throws Exception {
    // assertEquals( 403,
    // deleteAsServletResponse(RestBaseController.ROOT_PATH+"/workspaces/sf/datastores/sf").getStatusCode());
    // }
    @Test
    public void testPutNameChangeForbidden() throws Exception {
        String xml = "<wmsStore>" + ("<name>newName</name>" + "</wmsStore>");
        Assert.assertEquals(403, putAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/sf/wmsstores/demo"), xml, "text/xml").getStatus());
    }

    @Test
    public void testPutWorkspaceChangeForbidden() throws Exception {
        String xml = "<wmsStore>" + ("<workspace>gs</workspace>" + "</wmsStore>");
        MockHttpServletResponse response = putAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/sf/wmsstores/demo"), xml, "text/xml");
        Assert.assertThat(response, HttpTestUtils.hasStatus(FORBIDDEN));
    }
}

