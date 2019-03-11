/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.rest.catalog;


import java.io.File;
import java.util.List;
import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.CoverageInfo;
import org.geoserver.catalog.CoverageStoreInfo;
import org.geoserver.catalog.LayerInfo;
import org.geoserver.rest.RestBaseController;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


public class CoverageStoreControllerTest extends CatalogRESTTestSupport {
    @Test
    public void testGetAllOnMissingWorkspace() throws Exception {
        MockHttpServletResponse response = getAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/abcde/coveragestores.xml"));
        Assert.assertEquals(404, response.getStatus());
        Assert.assertThat(response.getContentAsString(), CoreMatchers.containsString("abcde"));
    }

    @Test
    public void testGetAllAsXML() throws Exception {
        Document dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/workspaces/wcs/coveragestores.xml"));
        Assert.assertEquals(size(), dom.getElementsByTagName("coverageStore").getLength());
    }

    @Test
    public void testGetAllAsJSON() throws Exception {
        JSON json = getAsJSON(((RestBaseController.ROOT_PATH) + "/workspaces/wcs/coveragestores.json"));
        Assert.assertTrue((json instanceof JSONObject));
        Object coveragestores = getJSONObject("coverageStores").get("coverageStore");
        Assert.assertNotNull(coveragestores);
        if (coveragestores instanceof JSONArray) {
            Assert.assertEquals(size(), size());
        } else {
            Assert.assertEquals(1, size());
        }
    }

    @Test
    public void testGetAllAsHTML() throws Exception {
        Document dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/workspaces/wcs/coveragestores.html"));
        List<CoverageStoreInfo> coveragestores = CatalogRESTTestSupport.catalog.getCoverageStoresByWorkspace("wcs");
        NodeList links = CatalogRESTTestSupport.xp.getMatchingNodes("//html:a", dom);
        Assert.assertEquals(coveragestores.size(), links.getLength());
        for (int i = 0; i < (coveragestores.size()); i++) {
            CoverageStoreInfo cs = coveragestores.get(i);
            Element link = ((Element) (links.item(i)));
            Assert.assertTrue(link.getAttribute("href").endsWith(((cs.getName()) + ".html")));
        }
    }

    @Test
    public void testPutAllUnauthorized() throws Exception {
        Assert.assertEquals(405, putAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/wcs/coveragestores")).getStatus());
    }

    @Test
    public void testDeleteAllUnauthorized() throws Exception {
        Assert.assertEquals(405, deleteAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/wcs/coveragestores")).getStatus());
    }

    @Test
    public void testGetAsXML() throws Exception {
        Document dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/workspaces/wcs/coveragestores/BlueMarble.xml"));
        // print(dom);
        Assert.assertEquals("coverageStore", dom.getDocumentElement().getNodeName());
        Assert.assertEquals("BlueMarble", CatalogRESTTestSupport.xp.evaluate("/coverageStore/name", dom));
        Assert.assertEquals("wcs", CatalogRESTTestSupport.xp.evaluate("/coverageStore/workspace/name", dom));
        Assert.assertEquals((("http://localhost:8080/geoserver" + (RestBaseController.ROOT_PATH)) + "/workspaces/wcs.xml"), CatalogRESTTestSupport.xp.evaluate("/coverageStore/workspace/atom:link/@href", dom));
        Assert.assertEquals((("http://localhost:8080/geoserver" + (RestBaseController.ROOT_PATH)) + "/workspaces/wcs/coveragestores/BlueMarble/coverages.xml"), CatalogRESTTestSupport.xp.evaluate("/coverageStore/coverages/atom:link/@href", dom));
    }

    @Test
    public void testGetAsHTML() throws Exception {
        // rename to test
        Catalog catalog = getCatalog();
        CoverageInfo coverage = catalog.getCoverageByName(getLayerId(MockData.TASMANIA_BM));
        String oldName = coverage.getName();
        coverage.setName("fooBar");
        catalog.save(coverage);
        try {
            Document dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/workspaces/wcs/coveragestores/BlueMarble.html"));
            CoverageStoreInfo cs = CatalogRESTTestSupport.catalog.getCoverageStoreByName("wcs", "BlueMarble");
            List<CoverageInfo> coverages = CatalogRESTTestSupport.catalog.getCoveragesByCoverageStore(cs);
            NodeList links = CatalogRESTTestSupport.xp.getMatchingNodes("//html:a", dom);
            Assert.assertEquals(coverages.size(), links.getLength());
            for (int i = 0; i < (coverages.size()); i++) {
                CoverageInfo cov = coverages.get(i);
                Element link = ((Element) (links.item(i)));
                Assert.assertTrue(link.getAttribute("href").endsWith((("coverages/" + (cov.getName())) + ".html")));
            }
        } finally {
            // revert change
            coverage.setName(oldName);
            catalog.save(coverage);
        }
    }

    @Test
    public void testGetWrongCoverageStore() throws Exception {
        // Parameters for the request
        String ws = "wcs";
        String cs = "BlueMarblesssss";
        // Request path
        String requestPath = (((((RestBaseController.ROOT_PATH) + "/workspaces/") + ws) + "/coveragestores/") + cs) + ".html";
        // Exception path
        String exception = (("No such coverage store: " + ws) + ",") + cs;
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
        removeStore("wcs", "newCoverageStore");
        File f = setupNewCoverageStore();
        String xml = (((("<coverageStore>" + (("<name>newCoverageStore</name>" + "<type>WorldImage</type>") + "<url>file://")) + (f.getAbsolutePath())) + "</url>") + "<workspace>wcs</workspace>") + "</coverageStore>";
        MockHttpServletResponse response = postAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/wcs/coveragestores"), xml, "text/xml");
        Assert.assertEquals(201, response.getStatus());
        Assert.assertNotNull(response.getHeader("Location"));
        Assert.assertTrue(response.getHeader("Location").endsWith("/workspaces/wcs/coveragestores/newCoverageStore"));
        CoverageStoreInfo newCoverageStore = CatalogRESTTestSupport.catalog.getCoverageStoreByName("newCoverageStore");
        Assert.assertNotNull(newCoverageStore);
        Assert.assertNotNull(newCoverageStore.getFormat());
    }

    @Test
    public void testRoundTripCoverageStoreXML() throws Exception {
        CoverageInfo before = CatalogRESTTestSupport.catalog.getCoverageByName(getLayerId(MockData.TASMANIA_BM));
        // get and re-write, does not go boom
        String xml = getAsString(((RestBaseController.ROOT_PATH) + "/workspaces/wcs/coveragestores/BlueMarble.xml"));
        MockHttpServletResponse response = putAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/wcs/coveragestores/BlueMarble"), xml, "text/xml");
        Assert.assertEquals(200, response.getStatus());
        // check nothing actually changed
        CoverageInfo after = CatalogRESTTestSupport.catalog.getCoverageByName(getLayerId(MockData.TASMANIA_BM));
        Assert.assertEquals(before, after);
    }

    @Test
    public void testGetAsJSON() throws Exception {
        JSON json = getAsJSON(((RestBaseController.ROOT_PATH) + "/workspaces/wcs/coveragestores/BlueMarble.json"));
        // print(json);
        JSONObject coverageStore = ((JSONObject) (json)).getJSONObject("coverageStore");
        Assert.assertNotNull(coverageStore);
        Assert.assertEquals("BlueMarble", coverageStore.get("name"));
        Assert.assertEquals("wcs", coverageStore.getJSONObject("workspace").get("name"));
        Assert.assertNotNull(coverageStore.get("type"));
        Assert.assertNotNull(coverageStore.get("url"));
        Assert.assertEquals((("http://localhost:8080/geoserver" + (RestBaseController.ROOT_PATH)) + "/workspaces/wcs.json"), coverageStore.getJSONObject("workspace").get("href"));
        Assert.assertEquals((("http://localhost:8080/geoserver" + (RestBaseController.ROOT_PATH)) + "/workspaces/wcs/coveragestores/BlueMarble/coverages.json"), coverageStore.get("coverages"));
    }

    @Test
    public void testPostAsJSON() throws Exception {
        removeStore("wcs", "newCoverageStore");
        File f = setupNewCoverageStore();
        String json = ((((("{'coverageStore':{" + (("'name':'newCoverageStore'," + "'type': 'WorldImage',") + "'url':'")) + (f.getAbsolutePath().replace('\\', '/'))) + "',") + "'workspace':'wcs',") + "}") + "}";
        MockHttpServletResponse response = postAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/wcs/coveragestores"), json, "text/json");
        Assert.assertEquals(201, response.getStatus());
        Assert.assertNotNull(response.getHeader("Location"));
        Assert.assertTrue(response.getHeader("Location").endsWith("/workspaces/wcs/coveragestores/newCoverageStore"));
        CoverageStoreInfo newCoverageStore = CatalogRESTTestSupport.catalog.getCoverageStoreByName("newCoverageStore");
        Assert.assertNotNull(newCoverageStore);
        Assert.assertNotNull(newCoverageStore.getFormat());
    }

    @Test
    public void testRoundTripCoverageStoreJSON() throws Exception {
        CoverageInfo before = CatalogRESTTestSupport.catalog.getCoverageByName(getLayerId(MockData.TASMANIA_BM));
        // get and re-write, does not go boom
        String xml = getAsString(((RestBaseController.ROOT_PATH) + "/workspaces/wcs/coveragestores/BlueMarble.json"));
        MockHttpServletResponse response = putAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/wcs/coveragestores/BlueMarble"), xml, "application/json");
        Assert.assertEquals(200, response.getStatus());
        // check nothing actually changed
        CoverageInfo after = CatalogRESTTestSupport.catalog.getCoverageByName(getLayerId(MockData.TASMANIA_BM));
        Assert.assertEquals(before, after);
    }

    @Test
    public void testPostToResource() throws Exception {
        String xml = "<coverageStore>" + (("<name>BlueMarble</name>" + "<enabled>false</enabled>") + "</coverageStore>");
        MockHttpServletResponse response = postAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/wcs/coveragestores/BlueMarble"), xml, "text/xml");
        Assert.assertEquals(405, response.getStatus());
    }

    @Test
    public void testPut() throws Exception {
        Document dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/workspaces/wcs/coveragestores/BlueMarble.xml"));
        assertXpathEvaluatesTo("true", "/coverageStore/enabled", dom);
        String xml = "<coverageStore>" + (("<name>BlueMarble</name>" + "<enabled>false</enabled>") + "</coverageStore>");
        MockHttpServletResponse response = putAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/wcs/coveragestores/BlueMarble"), xml, "text/xml");
        Assert.assertEquals(200, response.getStatus());
        dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/workspaces/wcs/coveragestores/BlueMarble.xml"));
        assertXpathEvaluatesTo("false", "/coverageStore/enabled", dom);
        Assert.assertFalse(CatalogRESTTestSupport.catalog.getCoverageStoreByName("wcs", "BlueMarble").isEnabled());
    }

    @Test
    public void testPutNonDestructive() throws Exception {
        CoverageStoreInfo cs = CatalogRESTTestSupport.catalog.getCoverageStoreByName("wcs", "BlueMarble");
        Assert.assertTrue(cs.isEnabled());
        String xml = "<coverageStore>" + ("<name>BlueMarble</name>" + "</coverageStore>");
        MockHttpServletResponse response = putAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/wcs/coveragestores/BlueMarble"), xml, "text/xml");
        Assert.assertEquals(200, response.getStatus());
        cs = CatalogRESTTestSupport.catalog.getCoverageStoreByName("wcs", "BlueMarble");
        Assert.assertTrue(cs.isEnabled());
    }

    @Test
    public void testDeletePurgeMetadataAfterConfigure() throws Exception {
        purgeRequest("metadata", 1);
    }

    @Test
    public void testDeletePurgeAllAfterConfigure() throws Exception {
        purgeRequest("all", 0);
    }

    @Test
    public void testPut2() throws Exception {
        Document dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/workspaces/wcs/coveragestores/BlueMarble.xml"));
        assertXpathEvaluatesTo("GeoTIFF", "/coverageStore/type", dom);
        String xml = "<coverageStore>" + (("<name>BlueMarble</name>" + "<type>WorldImage</type>") + "</coverageStore>");
        MockHttpServletResponse response = putAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/wcs/coveragestores/BlueMarble"), xml, "text/xml");
        Assert.assertEquals(200, response.getStatus());
        CoverageStoreInfo cs = CatalogRESTTestSupport.catalog.getCoverageStoreByName("wcs", "BlueMarble");
        Assert.assertEquals("WorldImage", cs.getType());
    }

    @Test
    public void testPutNonExistant() throws Exception {
        String xml = "<coverageStore>" + ("<name>changed</name>" + "</coverageStore>");
        MockHttpServletResponse response = putAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/wcs/coveragestores/nonExistant"), xml, "text/xml");
        Assert.assertEquals(404, response.getStatus());
    }

    @Test
    public void testDeleteNonExistant() throws Exception {
        Assert.assertEquals(404, deleteAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/wcs/coveragestores/nonExistant")).getStatus());
    }

    @Test
    public void testDelete() throws Exception {
        CoverageStoreInfo cs = CatalogRESTTestSupport.catalog.getCoverageStoreByName("wcs", "BlueMarble");
        List<CoverageInfo> coverages = CatalogRESTTestSupport.catalog.getCoveragesByCoverageStore(cs);
        for (CoverageInfo c : coverages) {
            for (LayerInfo l : CatalogRESTTestSupport.catalog.getLayers(c)) {
                CatalogRESTTestSupport.catalog.remove(l);
            }
            CatalogRESTTestSupport.catalog.remove(c);
        }
        Assert.assertEquals(200, deleteAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/wcs/coveragestores/BlueMarble")).getStatus());
        Assert.assertNull(CatalogRESTTestSupport.catalog.getCoverageStoreByName("wcs", "BlueMarble"));
    }

    @Test
    public void testDeleteNonEmpty() throws Exception {
        Assert.assertEquals(401, deleteAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/wcs/coveragestores/BlueMarble")).getStatus());
    }

    @Test
    public void testDeleteRecursive() throws Exception {
        Assert.assertNotNull(CatalogRESTTestSupport.catalog.getCoverageStoreByName("wcs", "BlueMarble"));
        MockHttpServletResponse response = deleteAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/wcs/coveragestores/BlueMarble?recurse=true"));
        Assert.assertEquals(200, response.getStatus());
        Assert.assertNull(CatalogRESTTestSupport.catalog.getCoverageStoreByName("wcs", "BlueMarble"));
        for (CoverageInfo c : CatalogRESTTestSupport.catalog.getCoverages()) {
            if (c.getStore().getName().equals("BlueMarble")) {
                Assert.fail();
            }
        }
    }
}

