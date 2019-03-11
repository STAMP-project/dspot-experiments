/**
 * (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.geofence.web;


import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.util.tester.FormTester;
import org.geoserver.geofence.config.GeoFencePropertyPlaceholderConfigurer;
import org.geoserver.geofence.services.dto.ShortAdminRule;
import org.geoserver.web.GeoServerWicketTestSupport;
import org.geoserver.web.wicket.GeoServerTablePanel;
import org.junit.Assert;
import org.junit.Test;


public class GeofenceServerAdminPageTest extends GeoServerWicketTestSupport {
    static GeoFencePropertyPlaceholderConfigurer configurer;

    @Test
    public void testAddNewRuleLink() {
        tester.assertRenderedPage(GeofenceServerAdminPage.class);
        tester.assertComponent("addNew", AjaxLink.class);
        tester.clickLink("addNew");
        tester.assertRenderedPage(GeofenceAdminRulePage.class);
        // submit a new rule
        FormTester form = tester.newFormTester("form");
        form.submit("save");
        tester.assertRenderedPage(GeofenceServerAdminPage.class);
        // check the rules model
        GeoServerTablePanel<ShortAdminRule> rulesPanel = ((GeoServerTablePanel<ShortAdminRule>) (tester.getComponentFromLastRenderedPage("rulesPanel")));
        Assert.assertEquals(1, rulesPanel.getDataProvider().size());
    }
}

