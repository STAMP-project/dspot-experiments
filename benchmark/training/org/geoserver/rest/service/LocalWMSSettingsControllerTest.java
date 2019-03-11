/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.rest.service;


import net.sf.json.JSON;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.apache.commons.io.IOUtils;
import org.geoserver.rest.RestBaseController;
import org.geoserver.rest.catalog.CatalogRESTTestSupport;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.w3c.dom.Document;


public class LocalWMSSettingsControllerTest extends CatalogRESTTestSupport {
    @Test
    public void testGetAsJSON() throws Exception {
        JSON json = getAsJSON(((RestBaseController.ROOT_PATH) + "/services/wms/workspaces/sf/settings.json"));
        JSONObject jsonObject = ((JSONObject) (json));
        Assert.assertNotNull(jsonObject);
        JSONObject wmsinfo = ((JSONObject) (jsonObject.get("wms")));
        // assertEquals("wms", wmsinfo.get("id"));
        Assert.assertEquals("WMS", wmsinfo.get("name"));
        JSONObject workspace = ((JSONObject) (wmsinfo.get("workspace")));
        Assert.assertNotNull(workspace);
        Assert.assertEquals("sf", workspace.get("name"));
        JSONObject watermark = ((JSONObject) (wmsinfo.get("watermark")));
        Assert.assertEquals("false", watermark.get("enabled").toString().trim());
        Assert.assertEquals("Nearest", wmsinfo.get("interpolation"));
    }

    @Test
    public void testGetAsXML() throws Exception {
        Document dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/services/wms/workspaces/sf/settings.xml"));
        Assert.assertEquals("wms", dom.getDocumentElement().getLocalName());
        assertXpathEvaluatesTo("true", "/wms/enabled", dom);
        assertXpathEvaluatesTo("sf", "/wms/workspace/name", dom);
        assertXpathEvaluatesTo("WMS", "/wms/name", dom);
        assertXpathEvaluatesTo("false", "/wms/watermark/enabled", dom);
        assertXpathEvaluatesTo("Nearest", "/wms/interpolation", dom);
    }

    @Test
    public void testGetAsHTML() throws Exception {
        getAsDOM(((RestBaseController.ROOT_PATH) + "/services/wms/workspaces/sf/settings.html"));
    }

    @Test
    public void testCreateAsJSON() throws Exception {
        removeLocalWorkspace();
        String input = "{'wms': {'id' : 'wms_sf', 'workspace':{'name':'sf'},'name' : 'WMS', 'enabled': 'true'}}";
        MockHttpServletResponse response = putAsServletResponse(((RestBaseController.ROOT_PATH) + "/services/wms/workspaces/sf/settings/"), input, "text/json");
        Assert.assertEquals(200, response.getStatus());
        JSON json = getAsJSON(((RestBaseController.ROOT_PATH) + "/services/wms/workspaces/sf/settings.json"));
        JSONObject jsonObject = ((JSONObject) (json));
        Assert.assertNotNull(jsonObject);
        JSONObject wmsinfo = ((JSONObject) (jsonObject.get("wms")));
        Assert.assertEquals("WMS", wmsinfo.get("name"));
        Assert.assertEquals("true", wmsinfo.get("enabled").toString().trim());
        JSONObject workspace = ((JSONObject) (wmsinfo.get("workspace")));
        Assert.assertNotNull(workspace);
        Assert.assertEquals("sf", workspace.get("name"));
    }

    @Test
    public void testCreateAsXML() throws Exception {
        removeLocalWorkspace();
        String xml = "<wms>" + ((((((("<id>wms_sf</id>" + "<workspace>") + "<name>sf</name>") + "</workspace>") + "<name>OGC:WMS</name>") + "<enabled>false</enabled>") + "<interpolation>Nearest</interpolation>") + "</wms>");
        MockHttpServletResponse response = putAsServletResponse(((RestBaseController.ROOT_PATH) + "/services/wms/workspaces/sf/settings"), xml, "text/xml");
        Assert.assertEquals(200, response.getStatus());
        Document dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/services/wms/workspaces/sf/settings.xml"));
        Assert.assertEquals("wms", dom.getDocumentElement().getLocalName());
        assertXpathEvaluatesTo("false", "/wms/enabled", dom);
        assertXpathEvaluatesTo("sf", "/wms/workspace/name", dom);
        assertXpathEvaluatesTo("OGC:WMS", "/wms/name", dom);
        assertXpathEvaluatesTo("false", "/wms/enabled", dom);
        assertXpathEvaluatesTo("Nearest", "/wms/interpolation", dom);
    }

    @Test
    public void testPutAsJSON() throws Exception {
        String json = "{'wms': {'id':'wms','workspace':{'name':'sf'},'enabled':'false','name':'WMS'}}";
        MockHttpServletResponse response = putAsServletResponse(((RestBaseController.ROOT_PATH) + "/services/wms/workspaces/sf/settings/"), json, "text/json");
        Assert.assertEquals(200, response.getStatus());
        JSON jsonMod = getAsJSON(((RestBaseController.ROOT_PATH) + "/services/wms/workspaces/sf/settings.json"));
        JSONObject jsonObject = ((JSONObject) (jsonMod));
        Assert.assertNotNull(jsonObject);
        JSONObject wmsinfo = ((JSONObject) (jsonObject.get("wms")));
        // assertEquals("wms", wmsinfo.get("id"));
        Assert.assertEquals("false", wmsinfo.get("enabled").toString().trim());
    }

    @Test
    public void testPutAsXML() throws Exception {
        String xml = "<wms>" + ((((("<id>wms</id>" + "<workspace>") + "<name>sf</name>") + "</workspace>") + "<enabled>false</enabled>") + "</wms>");
        MockHttpServletResponse response = putAsServletResponse(((RestBaseController.ROOT_PATH) + "/services/wms/workspaces/sf/settings"), xml, "text/xml");
        Assert.assertEquals(200, response.getStatus());
        Document dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/services/wms/workspaces/sf/settings.xml"));
        assertXpathEvaluatesTo("false", "/wms/enabled", dom);
    }

    @Test
    public void testPutFullAsXML() throws Exception {
        String xml = IOUtils.toString(LocalWMSSettingsControllerTest.class.getResourceAsStream("wms.xml"));
        MockHttpServletResponse response = putAsServletResponse(((RestBaseController.ROOT_PATH) + "/services/wms/workspaces/sf/settings"), xml, "text/xml");
        Assert.assertEquals(200, response.getStatus());
        Document dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/services/wms/workspaces/sf/settings.xml"));
        assertXpathEvaluatesTo("true", "/wms/enabled", dom);
    }

    @Test
    public void testDelete() throws Exception {
        Assert.assertEquals(200, deleteAsServletResponse(((RestBaseController.ROOT_PATH) + "/services/wms/workspaces/sf/settings")).getStatus());
        boolean thrown = false;
        try {
            JSON json = getAsJSON(((RestBaseController.ROOT_PATH) + "/services/wms/workspaces/sf/settings.json"));
        } catch (JSONException e) {
            thrown = true;
        }
        Assert.assertEquals(true, thrown);
    }
}

