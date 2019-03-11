/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.web;


import org.geoserver.test.GeoServerSystemTestSupport;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;


/**
 * Test the JVM argument GEOSERVER_CONSOLE_DISABLED actually disables the GeoServer web console when
 * set
 */
public class GeoServerConsoleFlagTest extends GeoServerSystemTestSupport {
    private static final String CONSOLE_DISABLED_PUBLISHER = "filePublisher";

    private static final String CONSOLE_ENABLED_PUBLISHER = "wicket";

    private static final String WEB_MAPPING = "/web";

    private static final String WEB_WILDCARD_MAPPING = "/web/**";

    private static final String WEB_RESOURCES_WILDCARD_MAPPING = "/web/resources/**";

    @Test
    public void testGeoServerConsoleDisabledTrue() throws Exception {
        // when the console is disabled the filePublisher method is used -- this
        // fetches files instead of delegating requests to wicket
        SimpleUrlHandlerMapping mapping = getWebDispatcherMapping(true);
        Assert.assertEquals(mapping.getUrlMap().get(GeoServerConsoleFlagTest.WEB_MAPPING), GeoServerConsoleFlagTest.CONSOLE_DISABLED_PUBLISHER);
        Assert.assertEquals(mapping.getUrlMap().get(GeoServerConsoleFlagTest.WEB_WILDCARD_MAPPING), GeoServerConsoleFlagTest.CONSOLE_DISABLED_PUBLISHER);
        Assert.assertEquals(mapping.getUrlMap().get(GeoServerConsoleFlagTest.WEB_RESOURCES_WILDCARD_MAPPING), GeoServerConsoleFlagTest.CONSOLE_DISABLED_PUBLISHER);
    }

    @Test
    public void testGeoserverConsoleDisabledFalse() throws Exception {
        // with the console enabled (default) requests are passed to wicket and
        // the gui is displayed
        SimpleUrlHandlerMapping mapping = getWebDispatcherMapping(false);
        Assert.assertEquals(mapping.getUrlMap().get(GeoServerConsoleFlagTest.WEB_MAPPING), GeoServerConsoleFlagTest.CONSOLE_ENABLED_PUBLISHER);
        Assert.assertEquals(mapping.getUrlMap().get(GeoServerConsoleFlagTest.WEB_WILDCARD_MAPPING), GeoServerConsoleFlagTest.CONSOLE_ENABLED_PUBLISHER);
        Assert.assertEquals(mapping.getUrlMap().get(GeoServerConsoleFlagTest.WEB_RESOURCES_WILDCARD_MAPPING), GeoServerConsoleFlagTest.CONSOLE_ENABLED_PUBLISHER);
    }
}

