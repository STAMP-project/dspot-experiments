/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wms.wms_1_1_1;


import org.geoserver.wms.WMSCascadeTestSupport;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.mock.web.MockHttpServletResponse;
import org.w3c.dom.Document;


@RunWith(Parameterized.class)
public class WMSCascadeTest extends WMSCascadeTestSupport {
    private final boolean aphEnabled;

    public WMSCascadeTest(boolean aphEnabled) {
        this.aphEnabled = aphEnabled;
    }

    @Test
    public void testCascadeGetMapOnto130() throws Exception {
        MockHttpServletResponse response = getAsServletResponse((((("wms?bbox=-180,-90,180,90" + "&styles=&layers=") + (WMSCascadeTestSupport.WORLD4326_130)) + "&Format=image/png&request=GetMap") + "&width=180&height=90&srs=EPSG:4326"));
        // we'll get a service exception if the requests are not the ones expected
        checkImage(response, "image/png", 180, 90);
    }

    @Test
    public void testCascadeGetMapOnto110() throws Exception {
        MockHttpServletResponse response = getAsServletResponse((((("wms?bbox=-180,-90,180,90" + "&styles=&layers=") + (WMSCascadeTestSupport.WORLD4326_110)) + "&Format=image/png&request=GetMap") + "&width=180&height=90&srs=EPSG:4326"));
        // we'll get a service exception if the requests are not the ones expected
        checkImage(response, "image/png", 180, 90);
    }

    @Test
    public void testCascadeCapabilitiesClientNoGetFeatureInfo() throws Exception {
        Document dom = getAsDOM("wms?request=GetCapabilities&version=1.1.0&service=wms");
        // print(dom);
        xpath.evaluate((("//Layer[name='" + (WMSCascadeTestSupport.WORLD4326_110_NFI)) + "']"), dom);
    }
}

