/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wms.wms_1_1_1;


import ResourceErrorHandling.SKIP_MISCONFIGURED_LAYERS;
import org.custommonkey.xmlunit.XMLAssert;
import org.geoserver.config.GeoServerInfo;
import org.geoserver.test.GeoServerSystemTestSupport;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.w3c.dom.Document;


public class CapabilitiesModifyingTest extends GeoServerSystemTestSupport {
    @Test
    public void testMisconfiguredLayerGeneratesErrorDocumentInDefaultConfig() throws Exception {
        MockHttpServletResponse response = getAsServletResponse("wms?service=WMS&request=GetCapabilities&version=1.1.1");
        Assert.assertTrue(("Response does not contain ServiceExceptionReport: " + (response.getContentAsString())), response.getContentAsString().endsWith("</ServiceExceptionReport>"));
    }

    @Test
    public void testMisconfiguredLayerIsSkippedWhenWMSServiceIsConfiguredThatWay() throws Exception {
        GeoServerInfo global = getGeoServer().getGlobal();
        global.setResourceErrorHandling(SKIP_MISCONFIGURED_LAYERS);
        getGeoServer().save(global);
        Document caps = getAsDOM("wms?service=WMS&request=GetCapabilities&version=1.1.1");
        Assert.assertEquals("WMT_MS_Capabilities", caps.getDocumentElement().getTagName());
        // we misconfigured all the layers in the server, so there should be no named layers now.
        XMLAssert.assertXpathEvaluatesTo("", "//Layer/Name/text()", caps);
    }

    @Test
    public void testMisconfiguredLayerGeneratesErrorDocumentInDefaultConfig_1_3_0() throws Exception {
        MockHttpServletResponse response = getAsServletResponse("wms?service=WMS&request=GetCapabilities&version=1.3.0");
        Assert.assertTrue(("Response does not contain ServiceExceptionReport: " + (response.getContentAsString())), response.getContentAsString().endsWith("</ServiceExceptionReport>"));
    }

    @Test
    public void testMisconfiguredLayerIsSkippedWhenWMSServiceIsConfiguredThatWay_1_3_0() throws Exception {
        GeoServerInfo global = getGeoServer().getGlobal();
        global.setResourceErrorHandling(SKIP_MISCONFIGURED_LAYERS);
        getGeoServer().save(global);
        Document caps = getAsDOM("wms?service=WMS&request=GetCapabilities&version=1.3.0");
        Assert.assertEquals("WMS_Capabilities", caps.getDocumentElement().getTagName());
        // we misconfigured all the layers in the server, so there should be no named layers now.
        XMLAssert.assertXpathEvaluatesTo("", "//Layer/Name/text()", caps);
    }
}

