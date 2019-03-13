/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.web.admin;


import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.util.tester.FormTester;
import org.geoserver.config.ContactInfo;
import org.geoserver.web.GeoServerHomePage;
import org.geoserver.web.GeoServerWicketTestSupport;
import org.junit.Assert;
import org.junit.Test;


public class ContactPageTest extends GeoServerWicketTestSupport {
    @Test
    public void testValues() {
        ContactInfo info = getGeoServerApplication().getGeoServer().getGlobal().getContact();
        login();
        GeoServerWicketTestSupport.tester.startPage(ContactPage.class);
        GeoServerWicketTestSupport.tester.assertComponent("form:contact:address", TextField.class);
        GeoServerWicketTestSupport.tester.assertModelValue("form:contact:address", info.getAddress());
    }

    @Test
    public void testSave() {
        login();
        GeoServerWicketTestSupport.tester.startPage(ContactPage.class);
        FormTester ft = GeoServerWicketTestSupport.tester.newFormTester("form");
        ft.setValue("contact:address", "newAddress");
        ft.submit("submit");
        GeoServerWicketTestSupport.tester.assertRenderedPage(GeoServerHomePage.class);
        ContactInfo info = getGeoServerApplication().getGeoServer().getGlobal().getContact();
        Assert.assertEquals("newAddress", info.getAddress());
    }
}

