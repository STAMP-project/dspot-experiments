/**
 * (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.web.wicket;


import org.geoserver.web.GeoServerWicketTestSupport;
import org.junit.Assert;
import org.junit.Test;


public class ConfirmationAjaxLinkTest extends GeoServerWicketTestSupport {
    @Test
    public void testMessageEscape() {
        GeoServerWicketTestSupport.tester.startPage(ConfirmationAjaxLinkTestPage.class);
        print(GeoServerWicketTestSupport.tester.getLastRenderedPage(), true, true);
        GeoServerWicketTestSupport.tester.executeAjaxEvent("form:confirmationLink", "click");
        String html = GeoServerWicketTestSupport.tester.getLastResponseAsString();
        // check the message has been escaped
        Assert.assertTrue(html.contains("\\\'confirmation\\\'"));
    }
}

