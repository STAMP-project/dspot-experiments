/**
 * (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.web;


import WebUIMode.DEFAULT;
import WebUIMode.DO_NOT_REDIRECT;
import WebUIMode.REDIRECT;
import org.apache.wicket.util.tester.WicketTester;
import org.geoserver.config.GeoServerInfo;
import org.geoserver.security.GeoServerSecurityTestSupport;
import org.geoserver.web.admin.StatusPage;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Niels Charlier
 */
public class GeoServerRedirectTest extends GeoServerSecurityTestSupport {
    @Test
    public void testRedirect() throws Exception {
        GeoServerInfo global = getGeoServer().getGlobal();
        global.setWebUIMode(REDIRECT);
        getGeoServer().save(global);
        GeoServerApplication app = getGeoServerApplication();
        app.init();
        WicketTester tester = new WicketTester(app, false);
        tester.startPage(StatusPage.class);
        Assert.assertEquals(2, tester.getPreviousResponses().size());
        Assert.assertEquals(302, tester.getPreviousResponses().get(0).getStatus());
    }

    @Test
    public void testDoNotRedirect() throws Exception {
        GeoServerInfo global = getGeoServer().getGlobal();
        global.setWebUIMode(DO_NOT_REDIRECT);
        getGeoServer().save(global);
        GeoServerApplication app = getGeoServerApplication();
        app.init();
        WicketTester tester = new WicketTester(app, false);
        tester.startPage(StatusPage.class);
        Assert.assertEquals(1, tester.getPreviousResponses().size());
        Assert.assertEquals(200, tester.getPreviousResponses().get(0).getStatus());
    }

    @Test
    public void testDefaultRedirect() throws Exception {
        GeoServerInfo global = getGeoServer().getGlobal();
        global.setWebUIMode(DEFAULT);
        getGeoServer().save(global);
        GeoServerApplication app = getGeoServerApplication();
        app.setDefaultIsRedirect(true);
        app.init();
        WicketTester tester = new WicketTester(app, false);
        tester.startPage(StatusPage.class);
        Assert.assertEquals(2, tester.getPreviousResponses().size());
        Assert.assertEquals(302, tester.getPreviousResponses().get(0).getStatus());
    }

    @Test
    public void testDefaultDoNotRedirect() throws Exception {
        GeoServerInfo global = getGeoServer().getGlobal();
        global.setWebUIMode(DEFAULT);
        getGeoServer().save(global);
        GeoServerApplication app = getGeoServerApplication();
        app.setDefaultIsRedirect(false);
        app.init();
        WicketTester tester = new WicketTester(app, false);
        tester.startPage(StatusPage.class);
        Assert.assertEquals(1, tester.getPreviousResponses().size());
        Assert.assertEquals(200, tester.getPreviousResponses().get(0).getStatus());
    }
}

