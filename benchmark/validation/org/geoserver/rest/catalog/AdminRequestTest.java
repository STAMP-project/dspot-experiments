/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.rest.catalog;


import org.geoserver.config.GeoServerInfo;
import org.geoserver.config.SettingsInfo;
import org.geoserver.rest.RestBaseController;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.w3c.dom.Document;


public class AdminRequestTest extends CatalogRESTTestSupport {
    @Test
    public void testWorkspaces() throws Exception {
        Assert.assertEquals(200, getAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces.xml")).getStatus());
        Document dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/workspaces.xml"));
        Assert.assertEquals(0, dom.getElementsByTagName("workspace").getLength());
        super.login();
        dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/workspaces.xml"));
        Assert.assertEquals(getCatalog().getWorkspaces().size(), dom.getElementsByTagName("workspace").getLength());
        loginAsCite();
        Assert.assertEquals(200, getAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces.xml")).getStatus());
        dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/workspaces.xml"));
        Assert.assertEquals(1, dom.getElementsByTagName("workspace").getLength());
    }

    @Test
    public void testWorkspacesWithProxyHeaders() throws Exception {
        GeoServerInfo ginfo = getGeoServer().getGlobal();
        SettingsInfo settings = getGeoServer().getGlobal().getSettings();
        ginfo.setUseHeadersProxyURL(true);
        settings.setProxyBaseUrl("${X-Forwarded-Proto}://${X-Forwarded-Host}/${X-Forwarded-Path} ${X-Forwarded-Proto}://${X-Forwarded-Host}");
        ginfo.setSettings(settings);
        getGeoServer().save(ginfo);
        Assert.assertEquals(200, getAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces.xml")).getStatus());
        Document dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/workspaces.xml"));
        Assert.assertEquals(0, dom.getElementsByTagName("workspace").getLength());
        super.login();
        dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/workspaces.xml"));
        Assert.assertEquals(getCatalog().getWorkspaces().size(), dom.getElementsByTagName("workspace").getLength());
        Assert.assertEquals(200, getAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces.xml")).getStatus());
        dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/workspaces.xml"));
        Assert.assertEquals(getCatalog().getWorkspaces().size(), dom.getElementsByTagName("workspace").getLength());
    }

    @Test
    public void testWorkspace() throws Exception {
        Assert.assertEquals(404, getAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/sf.xml")).getStatus());
        Assert.assertEquals(404, getAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/cite.xml")).getStatus());
        loginAsCite();
        Assert.assertEquals(404, getAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/sf.xml")).getStatus());
        Assert.assertEquals(200, getAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/cite.xml")).getStatus());
    }

    @Test
    public void testGlobalLayerGroupReadOnly() throws Exception {
        loginAsSf();
        Document dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/layergroups.xml"));
        Assert.assertEquals(1, dom.getElementsByTagName("layerGroup").getLength());
        assertXpathEvaluatesTo("global", "//layerGroup/name", dom);
        dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/layergroups/global.xml"));
        Assert.assertEquals("layerGroup", dom.getDocumentElement().getNodeName());
        String xml = "<layerGroup>" + (((("<styles>" + "<style>polygon</style>") + "<style>line</style>") + "</styles>") + "</layerGroup>");
        MockHttpServletResponse response = putAsServletResponse(((RestBaseController.ROOT_PATH) + "/layergroups/global"), xml, "text/xml");
        Assert.assertEquals(405, response.getStatus());
        xml = "<layerGroup>" + ((((((((("<name>newLayerGroup</name>" + "<layers>") + "<layer>Ponds</layer>") + "<layer>Forests</layer>") + "</layers>") + "<styles>") + "<style>polygon</style>") + "<style>point</style>") + "</styles>") + "</layerGroup>");
        response = postAsServletResponse(((RestBaseController.ROOT_PATH) + "/layergroups"), xml, "text/xml");
        Assert.assertEquals(405, response.getStatus());
    }

    @Test
    public void testLocalLayerGroupHidden() throws Exception {
        loginAsSf();
        Document dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/layergroups.xml"));
        print(dom);
        Assert.assertEquals(1, dom.getElementsByTagName("layerGroup").getLength());
        assertXpathEvaluatesTo("global", "//layerGroup/name", dom);
        MockHttpServletResponse response = getAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/cite/layergroups.xml"));
        Assert.assertEquals(404, response.getStatus());
        response = getAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/cite/layergroups.xml"));
        Assert.assertEquals(404, response.getStatus());
        dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/layergroups.xml"));
        Assert.assertEquals(1, dom.getElementsByTagName("layerGroup").getLength());
        assertXpathEvaluatesTo("global", "//layerGroup/name", dom);
        dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/workspaces/sf/layergroups.xml"));
        Assert.assertEquals(1, dom.getElementsByTagName("layerGroup").getLength());
        assertXpathEvaluatesTo("local", "//layerGroup/name", dom);
    }

    @Test
    public void testGlobalStyleReadOnly() throws Exception {
        loginAsSf();
        Document dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/styles.xml"));
        assertXpathNotExists("//style/name[text() = 'sf_style']", dom);
        assertXpathNotExists("//style/name[text() = 'cite_style']", dom);
        dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/styles/point.xml"));
        Assert.assertEquals("style", dom.getDocumentElement().getNodeName());
        String xml = "<style>" + ("<filename>foo.sld</filename>" + "</style>");
        MockHttpServletResponse response = putAsServletResponse(((RestBaseController.ROOT_PATH) + "/styles/point"), xml, "text/xml");
        Assert.assertEquals(405, response.getStatus());
        xml = "<style>" + (("<name>foo</name>" + "<filename>foo.sld</filename>") + "</style>");
        response = postAsServletResponse(((RestBaseController.ROOT_PATH) + "/styles"), xml, "text/xml");
        Assert.assertEquals(405, response.getStatus());
    }

    @Test
    public void testLocalStyleHidden() throws Exception {
        loginAsCite();
        Document dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/styles.xml"));
        assertXpathNotExists("//style/name[text() = 'cite_style']", dom);
        assertXpathNotExists("//style/name[text() = 'sf_style']", dom);
        MockHttpServletResponse response = getAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/sf/styles.xml"));
        Assert.assertEquals(404, response.getStatus());
        loginAsSf();
        dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/styles.xml"));
        assertXpathNotExists("//style/name[text() = 'cite_style']", dom);
        assertXpathNotExists("//style/name[text() = 'sf_style']", dom);
        dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/workspaces/sf/styles.xml"));
        Assert.assertEquals(1, dom.getElementsByTagName("style").getLength());
        assertXpathEvaluatesTo("sf_style", "//style/name", dom);
    }
}

