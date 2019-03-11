/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wms.web.data;


import FeedbackMessage.ERROR;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.apache.wicket.util.tester.FormTester;
import org.geoserver.web.GeoServerHomePage;
import org.geoserver.web.GeoServerWicketTestSupport;
import org.geoserver.wms.WMSInfo;
import org.geoserver.wms.web.WMSAdminPage;
import org.junit.Assert;
import org.junit.Test;


public class WMSAdminPageTest extends GeoServerWicketTestSupport {
    private WMSInfo wms;

    @Test
    public void testValues() throws Exception {
        tester.startPage(WMSAdminPage.class);
        tester.assertModelValue("form:keywords", wms.getKeywords());
        tester.assertModelValue("form:srs", new ArrayList<String>());
    }

    @Test
    public void testFormSubmit() throws Exception {
        tester.startPage(WMSAdminPage.class);
        FormTester ft = tester.newFormTester("form");
        ft.submit("submit");
        tester.assertNoErrorMessage();
        tester.assertRenderedPage(GeoServerHomePage.class);
    }

    @Test
    public void testWatermarkLocalFile() throws Exception {
        File f = new File(getClass().getResource("GeoServer_75.png").toURI());
        tester.startPage(WMSAdminPage.class);
        FormTester ft = tester.newFormTester("form");
        ft.setValue("watermark.uRL", f.getAbsolutePath());
        ft.submit("submit");
        tester.assertNoErrorMessage();
        tester.assertRenderedPage(GeoServerHomePage.class);
    }

    @Test
    public void testFormInvalid() throws Exception {
        tester.startPage(WMSAdminPage.class);
        FormTester ft = tester.newFormTester("form");
        ft.setValue("srs", "bla");
        ft.submit("submit");
        List errors = tester.getMessages(ERROR);
        Assert.assertEquals(1, errors.size());
        Assert.assertTrue(getMessage().toString().contains("bla"));
        tester.assertRenderedPage(WMSAdminPage.class);
    }

    @Test
    public void testBBOXForEachCRS() throws Exception {
        Assert.assertFalse(wms.isBBOXForEachCRS());
        tester.startPage(WMSAdminPage.class);
        FormTester ft = tester.newFormTester("form");
        ft.setValue("bBOXForEachCRS", true);
        ft.submit("submit");
        Assert.assertTrue(wms.isBBOXForEachCRS());
    }

    @Test
    public void testRootLayerTitle() throws Exception {
        tester.startPage(WMSAdminPage.class);
        FormTester ft = tester.newFormTester("form");
        ft.setValue("rootLayerTitle", "test");
        ft.setValue("rootLayerAbstract", "abstract test");
        ft.submit("submit");
        tester.assertNoErrorMessage();
        Assert.assertEquals(wms.getRootLayerTitle(), "test");
        Assert.assertEquals(wms.getRootLayerAbstract(), "abstract test");
    }

    @Test
    public void testDynamicStylingDisabled() throws Exception {
        Assert.assertFalse(wms.isDynamicStylingDisabled());
        tester.startPage(WMSAdminPage.class);
        FormTester ft = tester.newFormTester("form");
        ft.setValue("dynamicStyling.disabled", true);
        ft.submit("submit");
        Assert.assertTrue(wms.isDynamicStylingDisabled());
    }

    @Test
    public void testCacheConfiguration() throws Exception {
        Assert.assertFalse(wms.getCacheConfiguration().isEnabled());
        tester.startPage(WMSAdminPage.class);
        FormTester ft = tester.newFormTester("form");
        ft.setValue("cacheConfiguration.enabled", true);
        ft.submit("submit");
        Assert.assertTrue(wms.getCacheConfiguration().isEnabled());
    }

    @Test
    public void testFeaturesReprojectionDisabled() throws Exception {
        Assert.assertFalse(wms.isFeaturesReprojectionDisabled());
        tester.startPage(WMSAdminPage.class);
        FormTester ft = tester.newFormTester("form");
        ft.setValue("disableFeaturesReproject", true);
        ft.submit("submit");
        Assert.assertTrue(wms.isFeaturesReprojectionDisabled());
    }
}

