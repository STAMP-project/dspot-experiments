/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.web.publish;


import MockData.BASIC_POLYGONS;
import org.apache.wicket.Component;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.util.tester.FormTester;
import org.geoserver.catalog.LayerInfo;
import org.geoserver.web.ComponentBuilder;
import org.geoserver.web.FormTestPage;
import org.geoserver.web.GeoServerWicketTestSupport;
import org.junit.Assert;
import org.junit.Test;


public class HTTPLayerConfigTest extends GeoServerWicketTestSupport {
    LayerInfo polygons;

    FormTestPage page;

    @Test
    public void testDefaults() {
        GeoServerWicketTestSupport.tester.assertRenderedPage(FormTestPage.class);
        FormTester ft = GeoServerWicketTestSupport.tester.newFormTester("form");
        ft.submit();
        Assert.assertEquals(0, getSession().getFeedbackMessages().messages(new org.apache.wicket.feedback.ErrorLevelFeedbackMessageFilter(FeedbackMessage.ERROR)).size());
    }

    @Test
    public void testInvalid() {
        final LayerInfo polygons = getCatalog().getLayerByName(BASIC_POLYGONS.getLocalPart());
        FormTestPage page = new FormTestPage(new ComponentBuilder() {
            public Component buildComponent(String id) {
                return new HTTPLayerConfig(id, new org.apache.wicket.model.Model(polygons));
            }
        });
        GeoServerWicketTestSupport.tester.startPage(page);
        GeoServerWicketTestSupport.tester.assertComponent("form:panel:cacheAgeMax", TextField.class);
        GeoServerWicketTestSupport.tester.assertRenderedPage(FormTestPage.class);
        FormTester ft = GeoServerWicketTestSupport.tester.newFormTester("form");
        ft.setValue("panel:cacheAgeMax", "-20");
        ft.submit();
        GeoServerWicketTestSupport.tester.assertErrorMessages("The value of 'cacheAgeMax' must be between 0 and 9223372036854775807.");
    }

    @Test
    public void testValid() {
        FormTester ft = GeoServerWicketTestSupport.tester.newFormTester("form");
        ft.setValue("panel:cacheAgeMax", "3600");
        ft.submit();
        // System.out.println(page.getSession().getFeedbackMessages());
        Assert.assertEquals(0, getSession().getFeedbackMessages().messages(new org.apache.wicket.feedback.ErrorLevelFeedbackMessageFilter(FeedbackMessage.ERROR)).size());
        // System.out.println(polygons.getResource().getMetadata().get("cacheAgeMax").getClass());
        Assert.assertEquals(Integer.valueOf(3600), polygons.getResource().getMetadata().get("cacheAgeMax", Integer.class));
    }
}

