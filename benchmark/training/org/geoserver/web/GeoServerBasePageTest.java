/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.web;


import DefaultGeoServerNodeInfo.GEOSERVER_NODE_OPTS;
import org.apache.wicket.core.util.string.ComponentRenderer;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.util.tester.TagTester;
import org.junit.Assert;
import org.junit.Test;


public class GeoServerBasePageTest extends GeoServerWicketTestSupport {
    @Test
    public void testLoginFormShowsWhenLoggedOut() throws Exception {
        logout();
        GeoServerWicketTestSupport.tester.startPage(GeoServerHomePage.class);
        GeoServerWicketTestSupport.tester.assertVisible("loginforms");
        GeoServerWicketTestSupport.tester.assertVisible("logoutforms");
        GeoServerWicketTestSupport.tester.assertVisible("loginforms:0:loginform");
        GeoServerWicketTestSupport.tester.assertInvisible("logoutforms:0:logoutform");
        ListView logoutforms = ((ListView) (GeoServerWicketTestSupport.tester.getLastRenderedPage().get("logoutforms")));
        Assert.assertEquals(1, logoutforms.getList().size());
        ListView loginForms = ((ListView) (GeoServerWicketTestSupport.tester.getLastRenderedPage().get("loginforms")));
        String responseTxt = ComponentRenderer.renderComponent(loginForms).toString();
        TagTester tagTester = TagTester.createTagByAttribute(responseTxt, "form");
        Assert.assertEquals("../j_spring_security_check", tagTester.getAttribute("action"));
    }

    @Test
    public void testLogoutFormShowsWhenLoggedIn() throws Exception {
        login();
        GeoServerWicketTestSupport.tester.startPage(GeoServerHomePage.class);
        GeoServerWicketTestSupport.tester.assertVisible("loginforms");
        GeoServerWicketTestSupport.tester.assertVisible("logoutforms");
        GeoServerWicketTestSupport.tester.assertInvisible("loginforms:0:loginform");
        GeoServerWicketTestSupport.tester.assertVisible("logoutforms:0:logoutform");
        ListView loginForms = ((ListView) (GeoServerWicketTestSupport.tester.getLastRenderedPage().get("loginforms")));
        Assert.assertEquals(1, loginForms.getList().size());
        ListView logoutforms = ((ListView) (GeoServerWicketTestSupport.tester.getLastRenderedPage().get("logoutforms")));
        String responseTxt = ComponentRenderer.renderComponent(logoutforms).toString();
        TagTester tagTester = TagTester.createTagByAttribute(responseTxt, "form");
        Assert.assertEquals("../j_spring_security_logout", tagTester.getAttribute("action"));
    }

    @Test
    public void testDefaultNodeInfoLoggedOut() throws Exception {
        logout();
        System.setProperty(GEOSERVER_NODE_OPTS, "id=test");
        DefaultGeoServerNodeInfo.initializeFromEnviroment();
        GeoServerWicketTestSupport.tester.startPage(GeoServerHomePage.class);
        GeoServerWicketTestSupport.tester.assertInvisible("nodeIdContainer");
    }

    @Test
    public void testDefaultNodeInfoLoggedIn() throws Exception {
        login();
        System.setProperty(GEOSERVER_NODE_OPTS, "id:test;background:red;color:black");
        DefaultGeoServerNodeInfo.initializeFromEnviroment();
        GeoServerWicketTestSupport.tester.startPage(GeoServerHomePage.class);
        GeoServerWicketTestSupport.tester.assertVisible("nodeIdContainer");
        GeoServerWicketTestSupport.tester.assertModelValue("nodeIdContainer:nodeId", "test");
        // this does not work, damn wicket tester...
        // TagTester tags = tester.getTagByWicketId("nodeIdContainer");
        // String style = tags.getAttribute("style");
        // assertTrue(style.contains("background:red;"));
        // assertTrue(style.contains("color:black;"));
    }
}

