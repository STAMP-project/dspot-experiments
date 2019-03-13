/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.web.admin;


import org.apache.wicket.markup.html.form.CheckBox;
import org.geoserver.config.GeoServerInfo;
import org.geoserver.web.GeoServerWicketTestSupport;
import org.junit.Test;


public class GlobalSettingsPageTest extends GeoServerWicketTestSupport {
    @Test
    public void testValues() {
        GeoServerInfo info = getGeoServerApplication().getGeoServer().getGlobal();
        login();
        GeoServerWicketTestSupport.tester.startPage(GlobalSettingsPage.class);
        GeoServerWicketTestSupport.tester.assertComponent("form:verbose", CheckBox.class);
        GeoServerWicketTestSupport.tester.assertModelValue("form:verbose", info.isVerbose());
    }
}

