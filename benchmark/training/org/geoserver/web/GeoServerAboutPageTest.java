/**
 * (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.web;


import org.apache.wicket.util.tester.TagTester;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class GeoServerAboutPageTest extends GeoServerWicketTestSupport {
    @Test
    public void testLoginFormAction() throws Exception {
        logout();
        GeoServerWicketTestSupport.tester.executeUrl("./wicket/bookmarkable/org.geoserver.web.AboutGeoServerPage");
        Assert.assertThat(GeoServerWicketTestSupport.tester.getLastRenderedPage(), CoreMatchers.instanceOf(AboutGeoServerPage.class));
        String responseTxt = GeoServerWicketTestSupport.tester.getLastResponse().getDocument();
        // System.out.println(responseTxt);
        TagTester tagTester = TagTester.createTagByAttribute(responseTxt, "form");
        Assert.assertEquals("../../../j_spring_security_check", tagTester.getAttribute("action"));
    }
}

