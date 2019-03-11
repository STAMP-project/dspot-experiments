/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.rest.catalog;


import SystemTestData.PRIMITIVEGEOFEATURE;
import java.io.File;
import java.util.List;
import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.geoserver.catalog.DataStoreInfo;
import org.geoserver.catalog.FeatureTypeInfo;
import org.geoserver.rest.RestBaseController;
import org.geotools.data.DataStore;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


public class DataStoreControllerTest extends CatalogRESTTestSupport {
    @Test
    public void testGetAllAsXML() throws Exception {
        Document dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/workspaces/sf/datastores.xml"));
        Assert.assertEquals(size(), dom.getElementsByTagName("dataStore").getLength());
    }

    @Test
    public void testGetAsJSON() throws Exception {
        JSON json = getAsJSON(((RestBaseController.ROOT_PATH) + "/workspaces/sf/datastores/sf.json"));
        JSONObject dataStore = getJSONObject("dataStore");
        Assert.assertNotNull(dataStore);
        Assert.assertEquals("sf", dataStore.get("name"));
        Assert.assertEquals("sf", dataStore.getJSONObject("workspace").get("name"));
        Assert.assertNotNull(dataStore.get("connectionParameters"));
    }

    @Test
    public void testGetAllAsJSON() throws Exception {
        JSON json = getAsJSON(((RestBaseController.ROOT_PATH) + "/workspaces/sf/datastores.json"));
        Assert.assertTrue((json instanceof JSONObject));
        Object datastores = ((JSONObject) (json)).getJSONObject("dataStores").get("dataStore");
        Assert.assertNotNull(datastores);
        if (datastores instanceof JSONArray) {
            Assert.assertEquals(size(), size());
        } else {
            Assert.assertEquals(1, size());
        }
    }

    @Test
    public void testGetAllAsHTML() throws Exception {
        Document dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/workspaces/sf/datastores.html"));
        List<DataStoreInfo> datastores = CatalogRESTTestSupport.catalog.getDataStoresByWorkspace("sf");
        NodeList links = CatalogRESTTestSupport.xp.getMatchingNodes("//html:a", dom);
        Assert.assertEquals(datastores.size(), links.getLength());
        for (int i = 0; i < (datastores.size()); i++) {
            DataStoreInfo ds = datastores.get(i);
            Element link = ((Element) (links.item(i)));
            Assert.assertTrue(link.getAttribute("href").endsWith(((ds.getName()) + ".html")));
        }
    }

    @Test
    public void testPutAllUnauthorized() throws Exception {
        Assert.assertEquals(405, putAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/sf/datastores")).getStatus());
    }

    @Test
    public void testDeleteAllUnauthorized() throws Exception {
        Assert.assertEquals(405, deleteAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/sf/datastores")).getStatus());
    }

    @Test
    public void testGetAsXML() throws Exception {
        Document dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/workspaces/sf/datastores/sf.xml"));
        print(dom);
        Assert.assertEquals("dataStore", dom.getDocumentElement().getNodeName());
        Assert.assertEquals("sf", CatalogRESTTestSupport.xp.evaluate("/dataStore/name", dom));
        Assert.assertEquals("sf", CatalogRESTTestSupport.xp.evaluate("/dataStore/workspace/name", dom));
        assertXpathExists("/dataStore/connectionParameters", dom);
        Assert.assertThat(CatalogRESTTestSupport.xp.evaluate("/dataStore/featureTypes/atom:link/@href", dom), Matchers.endsWith(((RestBaseController.ROOT_PATH) + "/workspaces/sf/datastores/sf/featuretypes.xml")));
    }

    @Test
    public void testRoundTripGetAsXML() throws Exception {
        Document dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/workspaces/sf/datastores/sf.xml"));
        Assert.assertEquals("dataStore", dom.getDocumentElement().getNodeName());
        Assert.assertEquals("sf", CatalogRESTTestSupport.xp.evaluate("/dataStore/name", dom));
        Assert.assertEquals("sf", CatalogRESTTestSupport.xp.evaluate("/dataStore/workspace/name", dom));
        assertXpathExists("/dataStore/connectionParameters", dom);
        Document dom2 = getAsDOM(((RestBaseController.ROOT_PATH) + "/workspaces/sf/datastores/sf.xml"));
        assertXpathEvaluatesTo("true", "/dataStore/enabled", dom);
        String xml = "<dataStore>" + (("<name>sf</name>" + "<enabled>false</enabled>") + "</dataStore>");
        MockHttpServletResponse response = putAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/sf/datastores/sf"), xml, "text/xml");
        Assert.assertEquals(200, response.getStatus());
        dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/workspaces/sf/datastores/sf.xml"));
        assertXpathEvaluatesTo("false", "/dataStore/enabled", dom);
        Assert.assertFalse(CatalogRESTTestSupport.catalog.getDataStoreByName("sf", "sf").isEnabled());
    }

    @Test
    public void testGetAsHTML() throws Exception {
        Document dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/workspaces/sf/datastores/sf.html"));
        DataStoreInfo ds = CatalogRESTTestSupport.catalog.getDataStoreByName("sf");
        List<FeatureTypeInfo> featureTypes = CatalogRESTTestSupport.catalog.getFeatureTypesByDataStore(ds);
        NodeList links = CatalogRESTTestSupport.xp.getMatchingNodes("//html:a", dom);
        Assert.assertEquals(featureTypes.size(), links.getLength());
        for (int i = 0; i < (featureTypes.size()); i++) {
            FeatureTypeInfo ft = featureTypes.get(i);
            Element link = ((Element) (links.item(i)));
            Assert.assertTrue(link.getAttribute("href").endsWith(((ft.getName()) + ".html")));
        }
    }

    @Test
    public void testGetWrongDataStore() throws Exception {
        // Parameters for the request
        String ws = "sf";
        String ds = "sfssssss";
        // Request path
        String requestPath = (((((RestBaseController.ROOT_PATH) + "/workspaces/") + ws) + "/datastores/") + ds) + ".html";
        // Exception path
        String exception = (("No such datastore: " + ws) + ",") + ds;
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
        File dir = setupNewDataStore();
        String xml = (((((("<dataStore>" + (((("<name>newDataStore</name>" + "<connectionParameters>") + "<namespace><string>sf</string></namespace>") + "<directory>") + "<string>")) + (dir.getAbsolutePath())) + "</string>") + "</directory>") + "</connectionParameters>") + "<workspace>sf</workspace>") + "</dataStore>";
        MockHttpServletResponse response = postAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/sf/datastores"), xml, "text/xml");
        Assert.assertEquals(201, response.getStatus());
        Assert.assertNotNull(response.getHeader("Location"));
        Assert.assertTrue(response.getHeader("Location").endsWith("/workspaces/sf/datastores/newDataStore"));
        DataStoreInfo newDataStore = CatalogRESTTestSupport.catalog.getDataStoreByName("newDataStore");
        Assert.assertNotNull(newDataStore);
        DataStore ds = ((DataStore) (newDataStore.getDataStore(null)));
        Assert.assertNotNull(ds);
    }

    @Test
    public void testPostAsXMLNoWorkspace() throws Exception {
        File dir = setupNewDataStore();
        String xml = ((((("<dataStore>" + (((("<name>newDataStore</name>" + "<connectionParameters>") + "<namespace><string>sf</string></namespace>") + "<directory>") + "<string>")) + (dir.getAbsolutePath())) + "</string>") + "</directory>") + "</connectionParameters>") + "</dataStore>";
        MockHttpServletResponse response = postAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/sf/datastores"), xml, "text/xml");
        Assert.assertEquals(201, response.getStatus());
        Assert.assertNotNull(response.getHeader("Location"));
        Assert.assertTrue(response.getHeader("Location").endsWith("/workspaces/sf/datastores/newDataStore"));
        DataStoreInfo newDataStore = CatalogRESTTestSupport.catalog.getDataStoreByName("newDataStore");
        Assert.assertNotNull(newDataStore);
        DataStore ds = ((DataStore) (newDataStore.getDataStore(null)));
        Assert.assertNotNull(ds);
    }

    @Test
    public void testPostAsJSON() throws Exception {
        removeStore("sf", "newDataStore");
        File dir = setupNewDataStore();
        String json = ((((((("{'dataStore':{" + (("'connectionParameters': {" + "'namespace': {'string':'sf'},") + "'directory': {'string':'")) + (dir.getAbsolutePath().replace('\\', '/'))) + "'}") + "},") + "'workspace':'sf',") + "'name':'newDataStore',") + "}") + "}";
        MockHttpServletResponse response = postAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/sf/datastores"), json, "text/json");
        Assert.assertEquals(201, response.getStatus());
        Assert.assertNotNull(response.getHeader("Location"));
        Assert.assertTrue(response.getHeader("Location").endsWith("/workspaces/sf/datastores/newDataStore"));
        DataStoreInfo newDataStore = CatalogRESTTestSupport.catalog.getDataStoreByName("newDataStore");
        Assert.assertNotNull(newDataStore);
        DataStore ds = ((DataStore) (newDataStore.getDataStore(null)));
        Assert.assertNotNull(ds);
    }

    @Test
    public void testPostToResource() throws Exception {
        String xml = "<dataStore>" + (("<name>sf</name>" + "<enabled>false</enabled>") + "</dataStore>");
        MockHttpServletResponse response = postAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/sf/datastores/sf"), xml, "text/xml");
        Assert.assertEquals(405, response.getStatus());
    }

    @Test
    public void testPut() throws Exception {
        Document dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/workspaces/sf/datastores/sf.xml"));
        assertXpathEvaluatesTo("true", "/dataStore/enabled", dom);
        String xml = "<dataStore>" + (("<name>sf</name>" + "<enabled>false</enabled>") + "</dataStore>");
        MockHttpServletResponse response = putAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/sf/datastores/sf"), xml, "text/xml");
        Assert.assertEquals(200, response.getStatus());
        dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/workspaces/sf/datastores/sf.xml"));
        assertXpathEvaluatesTo("false", "/dataStore/enabled", dom);
        Assert.assertFalse(CatalogRESTTestSupport.catalog.getDataStoreByName("sf", "sf").isEnabled());
    }

    @Test
    public void testPut2() throws Exception {
        Document dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/workspaces/sf/datastores/sf.xml"));
        assertXpathEvaluatesTo("2", "count(//dataStore/connectionParameters/*)", dom);
        String xml = "<dataStore>" + ((((((((("<name>sf</name>" + "<connectionParameters>") + "<one>") + "<string>1</string>") + "</one>") + "<two>") + "<string>2</string>") + "</two>") + "</connectionParameters>") + "</dataStore>");
        MockHttpServletResponse response = putAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/sf/datastores/sf"), xml, "text/xml");
        Assert.assertEquals(200, response.getStatus());
        DataStoreInfo ds = CatalogRESTTestSupport.catalog.getDataStoreByName("sf", "sf");
        Assert.assertEquals(2, size());
        Assert.assertTrue(ds.getConnectionParameters().containsKey("one"));
        Assert.assertTrue(ds.getConnectionParameters().containsKey("two"));
    }

    @Test
    public void testPutNonDestructive() throws Exception {
        DataStoreInfo ds = CatalogRESTTestSupport.catalog.getDataStoreByName("sf", "sf");
        Assert.assertTrue(ds.isEnabled());
        String xml = "<dataStore>" + ("<name>sf</name>" + "</dataStore>");
        MockHttpServletResponse response = putAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/sf/datastores/sf"), xml, "text/xml");
        Assert.assertEquals(200, response.getStatus());
        Assert.assertTrue(ds.isEnabled());
    }

    @Test
    public void testPutNonExistant() throws Exception {
        String xml = "<dataStore>" + ("<name>changed</name>" + "</dataStore>");
        MockHttpServletResponse response = putAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/sf/datastores/nonExistant"), xml, "text/xml");
        Assert.assertEquals(404, response.getStatus());
    }

    @Test
    public void testDeleteNonExistant() throws Exception {
        Assert.assertEquals(404, deleteAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/sf/datastores/nonExistant")).getStatus());
    }

    @Test
    public void testDelete() throws Exception {
        removeStore("sf", "newDataStore");
        File dir = setupNewDataStore();
        String xml = (((((("<dataStore>" + (((((((("<name>newDataStore</name>" + "<connectionParameters>") + "<entry>") + "<string>namespace</string>") + "<string>sf</string>") + "</entry>") + "<entry>") + "<string>directory</string>") + "<string>")) + (dir.getAbsolutePath())) + "</string>") + "</entry>") + "</connectionParameters>") + "<workspace>sf</workspace>") + "</dataStore>";
        MockHttpServletResponse response = postAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/sf/datastores"), xml, "text/xml");
        Assert.assertEquals(201, response.getStatus());
        Assert.assertNotNull(CatalogRESTTestSupport.catalog.getDataStoreByName("sf", "newDataStore"));
        Assert.assertEquals(200, deleteAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/sf/datastores/newDataStore")).getStatus());
        Assert.assertNull(CatalogRESTTestSupport.catalog.getDataStoreByName("sf", "newDataStore"));
    }

    @Test
    public void testDeleteNonEmptyForbidden() throws Exception {
        Assert.assertEquals(403, deleteAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/sf/datastores/sf")).getStatus());
    }

    @Test
    public void testDeleteRecursive() throws Exception {
        Assert.assertNotNull(CatalogRESTTestSupport.catalog.getDataStoreByName("sf", "sf"));
        MockHttpServletResponse response = deleteAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/sf/datastores/sf?recurse=true"));
        Assert.assertEquals(200, response.getStatus());
        Assert.assertNull(CatalogRESTTestSupport.catalog.getDataStoreByName("sf", "sf"));
        for (FeatureTypeInfo ft : CatalogRESTTestSupport.catalog.getFeatureTypes()) {
            if (ft.getStore().getName().equals("sf")) {
                Assert.fail();
            }
        }
    }

    @Test
    public void testPutNameChangeForbidden() throws Exception {
        getTestData().addVectorLayer(PRIMITIVEGEOFEATURE, getCatalog());
        String xml = "<dataStore>" + ("<name>newName</name>" + "</dataStore>");
        Assert.assertEquals(403, putAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/sf/datastores/sf"), xml, "text/xml").getStatus());
    }

    @Test
    public void testPutWorkspaceChangeForbidden() throws Exception {
        String xml = "<dataStore>" + ("<workspace>gs</workspace>" + "</dataStore>");
        Assert.assertEquals(403, putAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/sf/datastores/sf"), xml, "text/xml").getStatus());
    }
}

