/**
 * (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.geofence;


import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URISyntaxException;
import junit.framework.TestCase;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.util.tester.FormTester;
import org.geoserver.geofence.config.GeoFencePropertyPlaceholderConfigurer;
import org.geoserver.geofence.utils.GeofenceTestUtils;
import org.geoserver.geofence.web.GeofencePage;
import org.geoserver.web.GeoServerHomePage;
import org.geoserver.web.GeoServerWicketTestSupport;
import org.junit.Test;


public class GeofencePageTest extends GeoServerWicketTestSupport {
    static GeoFencePropertyPlaceholderConfigurer configurer;

    /**
     *
     *
     * @unknown This test fails in 2.6
     */
    @Test
    public void testSave() throws IOException, URISyntaxException {
        GeofenceTestUtils.emptyFile("test-config.properties");
        FormTester ft = tester.newFormTester("form");
        ft.submit("submit");
        tester.assertRenderedPage(GeoServerHomePage.class);
        File configFile = GeofencePageTest.configurer.getConfigFile().file();
        LOGGER.info(("Config file is " + configFile));
        TestCase.assertTrue(((GeofenceTestUtils.readConfig(configFile).length()) > 0));
    }

    /**
     *
     *
     * @unknown This test fails in 2.6
     */
    @Test
    public void testCancel() throws IOException, URISyntaxException {
        GeofenceTestUtils.emptyFile("test-config.properties");
        // GeofenceTestUtils.emptyFile("test-cache-config.properties");
        FormTester ft = tester.newFormTester("form");
        ft.submit("cancel");
        tester.assertRenderedPage(GeoServerHomePage.class);
        TestCase.assertTrue(((GeofenceTestUtils.readConfig("test-config.properties").length()) == 0));
        // assertTrue(GeofenceTestUtils.readConfig("test-cache-config.properties").length() == 0);
    }

    @Test
    public void testErrorEmptyInstance() {
        FormTester ft = tester.newFormTester("form");
        ft.setValue("instanceName", "");
        ft.submit("submit");
        tester.assertRenderedPage(GeofencePage.class);
        tester.assertContains("is required");
    }

    @Test
    public void testErrorEmptyURL() {
        FormTester ft = tester.newFormTester("form");
        ft.setValue("servicesUrl", "");
        ft.submit("submit");
        tester.assertRenderedPage(GeofencePage.class);
        tester.assertContains("is required");
    }

    @Test
    public void testErrorWrongURL() {
        @SuppressWarnings("unchecked")
        TextField<String> servicesUrl = ((TextField<String>) (tester.getComponentFromLastRenderedPage("form:servicesUrl")));
        servicesUrl.setDefaultModel(new org.apache.wicket.model.Model<String>("fakeurl"));
        tester.clickLink("form:test", true);
        tester.assertContains("RemoteAccessException");
    }

    @Test
    public void testErrorEmptyCacheSize() {
        FormTester ft = tester.newFormTester("form");
        ft.setValue("cacheSize", "");
        ft.submit("submit");
        tester.assertRenderedPage(GeofencePage.class);
        tester.assertContains("is required");
    }

    @Test
    public void testErrorWrongCacheSize() {
        FormTester ft = tester.newFormTester("form");
        ft.setValue("cacheSize", "A");
        ft.submit("submit");
        tester.assertRenderedPage(GeofencePage.class);
        tester.assertContains("long");
    }

    @Test
    public void testInvalidateCache() {
        tester.clickLink("form:invalidate", true);
        String success = new StringResourceModel(((GeofencePage.class.getSimpleName()) + ".cacheInvalidated")).getObject();
        tester.assertInfoMessages(((Serializable[]) (new String[]{ success })));
    }
}

