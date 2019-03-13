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


public class LocalWCSSettingsControllerTest extends CatalogRESTTestSupport {
    @Test
    public void testGetAsJSON() throws Exception {
        JSON json = getAsJSON(((RestBaseController.ROOT_PATH) + "/services/wcs/workspaces/sf/settings.json"));
        JSONObject jsonObject = ((JSONObject) (json));
        Assert.assertNotNull(jsonObject);
        JSONObject wcsinfo = ((JSONObject) (jsonObject.get("wcs")));
        Assert.assertEquals("WCS", wcsinfo.get("name"));
        JSONObject workspace = ((JSONObject) (wcsinfo.get("workspace")));
        Assert.assertNotNull(workspace);
        Assert.assertEquals("sf", workspace.get("name"));
        Assert.assertEquals("false", wcsinfo.get("verbose").toString().trim());
    }

    @Test
    public void testGetAsXML() throws Exception {
        Document dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/services/wcs/workspaces/sf/settings.xml"));
        Assert.assertEquals("wcs", dom.getDocumentElement().getLocalName());
        assertXpathEvaluatesTo("true", "/wcs/enabled", dom);
        assertXpathEvaluatesTo("sf", "/wcs/workspace/name", dom);
        assertXpathEvaluatesTo("WCS", "/wcs/name", dom);
        assertXpathEvaluatesTo("false", "/wcs/verbose", dom);
    }

    @Test
    public void testGetAsHTML() throws Exception {
        getAsDOM(((RestBaseController.ROOT_PATH) + "/services/wcs/workspaces/sf/settings.html"));
    }

    @Test
    public void testCreateAsJSON() throws Exception {
        removeLocalWorkspace();
        String input = "{'wcs': {'id' : 'wcs', 'name' : 'WCS', 'workspace': {'name': 'sf'},'enabled': 'true'}}";
        MockHttpServletResponse response = putAsServletResponse(((RestBaseController.ROOT_PATH) + "/services/wcs/workspaces/sf/settings/"), input, "text/json");
        Assert.assertEquals(200, response.getStatus());
        JSON json = getAsJSON(((RestBaseController.ROOT_PATH) + "/services/wcs/workspaces/sf/settings.json"));
        JSONObject jsonObject = ((JSONObject) (json));
        Assert.assertNotNull(jsonObject);
        JSONObject wmsinfo = ((JSONObject) (jsonObject.get("wcs")));
        Assert.assertEquals("WCS", wmsinfo.get("name"));
        JSONObject workspace = ((JSONObject) (wmsinfo.get("workspace")));
        Assert.assertNotNull(workspace);
        Assert.assertEquals("sf", workspace.get("name"));
    }

    @Test
    public void testCreateAsXML() throws Exception {
        removeLocalWorkspace();
        String xml = "<wcs>" + (((((("<id>wcs</id>" + "<workspace>") + "<name>sf</name>") + "</workspace>") + "<name>OGC:WCS</name>") + "<enabled>false</enabled>") + "</wcs>");
        MockHttpServletResponse response = putAsServletResponse(((RestBaseController.ROOT_PATH) + "/services/wcs/workspaces/sf/settings"), xml, "text/xml");
        Assert.assertEquals(200, response.getStatus());
        Document dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/services/wcs/workspaces/sf/settings.xml"));
        Assert.assertEquals("wcs", dom.getDocumentElement().getLocalName());
        assertXpathEvaluatesTo("false", "/wcs/enabled", dom);
        assertXpathEvaluatesTo("sf", "/wcs/workspace/name", dom);
        assertXpathEvaluatesTo("OGC:WCS", "/wcs/name", dom);
    }

    @Test
    public void testPutAsJSON() throws Exception {
        String json = "{'wcs': {'id':'wcs','workspace':{'name':'sf'},'enabled':'false','name':'WCS'}}";
        MockHttpServletResponse response = putAsServletResponse(((RestBaseController.ROOT_PATH) + "/services/wcs/workspaces/sf/settings/"), json, "text/json");
        Assert.assertEquals(200, response.getStatus());
        JSON jsonMod = getAsJSON(((RestBaseController.ROOT_PATH) + "/services/wcs/workspaces/sf/settings.json"));
        JSONObject jsonObject = ((JSONObject) (jsonMod));
        Assert.assertNotNull(jsonObject);
        JSONObject wcsinfo = ((JSONObject) (jsonObject.get("wcs")));
        Assert.assertEquals("false", wcsinfo.get("enabled").toString().trim());
    }

    @Test
    public void testPutAsXML() throws Exception {
        String xml = "<wcs>" + ((((("<id>wcs</id>" + "<workspace>") + "<name>sf</name>") + "</workspace>") + "<enabled>false</enabled>") + "</wcs>");
        MockHttpServletResponse response = putAsServletResponse(((RestBaseController.ROOT_PATH) + "/services/wcs/workspaces/sf/settings"), xml, "text/xml");
        Assert.assertEquals(200, response.getStatus());
        Document dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/services/wcs/workspaces/sf/settings.xml"));
        assertXpathEvaluatesTo("false", "/wcs/enabled", dom);
    }

    @Test
    public void testPutFullAsXML() throws Exception {
        String xml = IOUtils.toString(LocalWCSSettingsControllerTest.class.getResourceAsStream("wcs.xml"));
        MockHttpServletResponse response = putAsServletResponse(((RestBaseController.ROOT_PATH) + "/services/wcs/workspaces/sf/settings"), xml, "text/xml");
        Assert.assertEquals(200, response.getStatus());
        Document dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/services/wcs/workspaces/sf/settings.xml"));
        assertXpathEvaluatesTo("true", "/wcs/enabled", dom);
    }

    @Test
    public void testDelete() throws Exception {
        Assert.assertEquals(200, deleteAsServletResponse(((RestBaseController.ROOT_PATH) + "/services/wcs/workspaces/sf/settings")).getStatus());
        boolean thrown = false;
        try {
            JSON json = getAsJSON(((RestBaseController.ROOT_PATH) + "/services/wcs/sf/settings.json"));
        } catch (JSONException e) {
            thrown = true;
        }
        Assert.assertEquals(true, thrown);
    }
}

