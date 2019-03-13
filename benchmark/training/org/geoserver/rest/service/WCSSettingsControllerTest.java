/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.rest.service;


import net.sf.json.JSON;
import net.sf.json.JSONObject;
import org.geoserver.config.GeoServer;
import org.geoserver.rest.RestBaseController;
import org.geoserver.rest.catalog.CatalogRESTTestSupport;
import org.geoserver.wcs.WCSInfo;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.w3c.dom.Document;


public class WCSSettingsControllerTest extends CatalogRESTTestSupport {
    @Test
    public void testGetASJSON() throws Exception {
        JSON json = getAsJSON(((RestBaseController.ROOT_PATH) + "/services/wcs/settings.json"));
        JSONObject jsonObject = ((JSONObject) (json));
        Assert.assertNotNull(jsonObject);
        JSONObject wcsinfo = ((JSONObject) (jsonObject.get("wcs")));
        print(wcsinfo);
        Assert.assertEquals("WCS", wcsinfo.get("name"));
        Assert.assertEquals("true", wcsinfo.get("enabled").toString().trim());
        Assert.assertEquals("false", wcsinfo.get("verbose").toString().trim());
    }

    @Test
    public void testGetAsXML() throws Exception {
        Document dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/services/wcs/settings.xml"));
        Assert.assertEquals("wcs", dom.getDocumentElement().getLocalName());
        Assert.assertEquals(1, dom.getElementsByTagName("name").getLength());
        assertXpathEvaluatesTo("true", "/wcs/enabled", dom);
        assertXpathEvaluatesTo("WCS", "/wcs/name", dom);
        assertXpathEvaluatesTo("false", "/wcs/verbose", dom);
    }

    @Test
    public void testGetAsHTML() throws Exception {
        getAsDOM(((RestBaseController.ROOT_PATH) + "/services/wcs/settings.html"));
    }

    @Test
    public void testPutAsJSON() throws Exception {
        String json = "{'wcs': {'id':'wcs','enabled':'false','name':'WCS'}}";
        MockHttpServletResponse response = putAsServletResponse(((RestBaseController.ROOT_PATH) + "/services/wcs/settings/"), json, "text/json");
        Assert.assertEquals(200, response.getStatus());
        JSON jsonMod = getAsJSON(((RestBaseController.ROOT_PATH) + "/services/wcs/settings.json"));
        JSONObject jsonObject = ((JSONObject) (jsonMod));
        Assert.assertNotNull(jsonObject);
        JSONObject wcsinfo = ((JSONObject) (jsonObject.get("wcs")));
        Assert.assertEquals("false", wcsinfo.get("enabled").toString().trim());
        Assert.assertEquals("WCS", wcsinfo.get("name"));
    }

    @Test
    public void testPutAsXML() throws Exception {
        String xml = "<wcs>" + (((("<id>wcs</id>" + "<enabled>false</enabled>") + "<name>WCS</name><title>GeoServer Web Coverage Service</title>") + "<maintainer>http://geoserver.org/comm</maintainer>") + "</wcs>");
        MockHttpServletResponse response = putAsServletResponse(((RestBaseController.ROOT_PATH) + "/services/wcs/settings"), xml, "text/xml");
        Assert.assertEquals(200, response.getStatus());
        Document dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/services/wcs/settings.xml"));
        assertXpathEvaluatesTo("false", "/wcs/enabled", dom);
        assertXpathEvaluatesTo("WCS", "/wcs/name", dom);
    }

    @Test
    public void testPutNonDestructive() throws Exception {
        GeoServer geoServer = getGeoServer();
        WCSInfo i = geoServer.getService(WCSInfo.class);
        i.setEnabled(true);
        geoServer.save(i);
        String xml = "<wcs>" + ((("<id>wcs</id>" + "<name>WCS</name><title>GeoServer Web Coverage Service</title>") + "<maintainer>http://geoserver.org/comm</maintainer>") + "</wcs>");
        MockHttpServletResponse response = putAsServletResponse(((RestBaseController.ROOT_PATH) + "/services/wcs/settings"), xml, "text/xml");
        Assert.assertEquals(200, response.getStatus());
        Document dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/services/wcs/settings.xml"));
        assertXpathEvaluatesTo("true", "/wcs/enabled", dom);
        assertXpathEvaluatesTo("WCS", "/wcs/name", dom);
        i = geoServer.getService(WCSInfo.class);
        Assert.assertTrue(i.isEnabled());
    }

    @Test
    public void testDelete() throws Exception {
        Assert.assertEquals(405, deleteAsServletResponse(((RestBaseController.ROOT_PATH) + "/services/wcs/settings")).getStatus());
    }
}

