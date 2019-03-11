/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wcs;


import org.geoserver.wcs.test.WCSTestSupport;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;


public class WCSDisabledTest extends WCSTestSupport {
    @Test
    public void testDisabledServiceResponse() throws Exception {
        WCSInfo wcs = getGeoServer().getService(WCSInfo.class);
        wcs.setEnabled(false);
        getGeoServer().save(wcs);
        Document doc = getAsDOM("wcs?service=WCS&request=getCapabilities");
        Assert.assertEquals("ows:ExceptionReport", doc.getDocumentElement().getNodeName());
    }

    @Test
    public void testEnabledServiceResponse() throws Exception {
        WCSInfo wcs = getGeoServer().getService(WCSInfo.class);
        wcs.setEnabled(true);
        getGeoServer().save(wcs);
        Document doc = getAsDOM("wcs?service=WCS&request=getCapabilities");
        Assert.assertEquals("wcs:WCS_Capabilities", doc.getDocumentElement().getNodeName());
    }
}

