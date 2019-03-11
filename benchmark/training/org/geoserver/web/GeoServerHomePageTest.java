/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.web;


import java.util.Collections;
import java.util.List;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.junit.Assert;
import org.junit.Test;


public class GeoServerHomePageTest extends GeoServerWicketTestSupport {
    @Test
    public void testProvidedGetCapabilities() {
        GeoServerWicketTestSupport.tester.startPage(GeoServerHomePage.class);
        GeoServerWicketTestSupport.tester.assertListView("providedCaps", Collections.singletonList(getGeoServerApplication().getBeanOfType(CapabilitiesHomePageLinkProvider.class)));
    }

    @Test
    public void testProvidedCentralBodyContent() {
        GeoServerWicketTestSupport.tester.startPage(GeoServerHomePage.class);
        GeoServerApplication geoServerApplication = getGeoServerApplication();
        List<GeoServerHomePageContentProvider> providers;
        providers = geoServerApplication.getBeansOfType(GeoServerHomePageContentProvider.class);
        Assert.assertTrue(((providers.size()) > 0));
        GeoServerWicketTestSupport.tester.assertListView("contributedContent", providers);
    }

    @Test
    public void testEmailIfNull() {
        GeoServerApplication geoServerApplication = getGeoServerApplication();
        String contactEmail = geoServerApplication.getGeoServer().getGlobal().getSettings().getContact().getContactEmail();
        Assert.assertEquals("andrea@geoserver.org", (contactEmail == null ? "andrea@geoserver.org" : contactEmail));
    }

    public static class MockHomePageContentProvider implements GeoServerHomePageContentProvider {
        public Component getPageBodyComponent(final String id) {
            return new Label(id, "MockHomePageContentProvider");
        }
    }
}

