/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.web;


import GeoServerSecuredPage.SAVED_REQUEST;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import org.apache.wicket.protocol.http.WebSession;
import org.geoserver.web.data.layer.LayerPage;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.web.savedrequest.SavedRequest;


public class GeoServerSecuredPageTest extends GeoServerWicketTestSupport {
    @Test
    public void testSecuredPageGivesRedirectWhenLoggedOut() throws UnsupportedEncodingException {
        logout();
        GeoServerWicketTestSupport.tester.startPage(LayerPage.class);
        GeoServerWicketTestSupport.tester.assertRenderedPage(GeoServerLoginPage.class);
        // make sure the spring security emulation is properly setup
        SavedRequest sr = ((SavedRequest) (GeoServerWicketTestSupport.tester.getHttpSession().getAttribute(SAVED_REQUEST)));
        Assert.assertNotNull(sr);
        String redirectUrl = new URLDecoder().decode(sr.getRedirectUrl(), "UTF8");
        Assert.assertTrue(redirectUrl.contains("wicket/bookmarkable/org.geoserver.web.data.layer.LayerPage"));
    }

    @Test
    public void testSecuredPageAllowsAccessWhenLoggedIn() {
        login();
        GeoServerWicketTestSupport.tester.startPage(LayerPage.class);
        GeoServerWicketTestSupport.tester.assertRenderedPage(LayerPage.class);
    }

    @Test
    public void testSessionFixationAvoidance() throws Exception {
        GeoServerWicketTestSupport.tester.startPage(GeoServerHomePage.class);
        final WebSession session = WebSession.get();
        session.bind();// fore session creation

        session.setAttribute("test", "whatever");
        // login, this will invalidate the session
        GeoServerWicketTestSupport.tester.startPage(GeoServerHomePage.class);
        MockHttpServletRequest request = createRequest("login");
        request.setMethod("POST");
        request.setParameter("username", "admin");
        request.setParameter("password", "geoserver");
        dispatch(request);
        // the session in wicket tester mock does not disappear, the only
        // way to see if it has been invalidated is to check that the attributes are gone...
        Assert.assertNull(session.getAttribute("test"));
    }
}

