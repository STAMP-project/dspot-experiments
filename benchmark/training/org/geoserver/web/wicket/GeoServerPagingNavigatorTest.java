/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.web.wicket;


import org.geoserver.web.GeoServerWicketTestSupport;
import org.junit.Test;


public class GeoServerPagingNavigatorTest extends GeoServerWicketTestSupport {
    @Test
    public void testPageLoads() throws Exception {
        GeoServerWicketTestSupport.tester.startPage(GeoServerPagingNavigatorTestPage.class);
    }
}

