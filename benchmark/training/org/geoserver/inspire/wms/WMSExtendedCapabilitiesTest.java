/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.inspire.wms;


import CREATE_EXTENDED_CAPABILITIES.key;
import org.geoserver.catalog.MetadataMap;
import org.geoserver.config.ServiceInfo;
import org.geoserver.inspire.InspireSchema;
import org.geoserver.inspire.InspireTestSupport;
import org.geoserver.inspire.ViewServicesTestSupport;
import org.geoserver.wms.WMSInfo;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;


public class WMSExtendedCapabilitiesTest extends ViewServicesTestSupport {
    private static final String WMS_1_1_1_GETCAPREQUEST = "wms?request=GetCapabilities&service=WMS&version=1.1.1";

    private static final String WMS_1_3_0_GETCAPREQUEST = "wms?request=GetCapabilities&service=WMS&version=1.3.0";

    // There is an INSPIRE DTD for WMS 1.1.1 but not implementing this
    @Test
    public void testExtCaps111WithFullSettings() throws Exception {
        final ServiceInfo serviceInfo = getGeoServer().getService(WMSInfo.class);
        final MetadataMap metadata = serviceInfo.getMetadata();
        InspireTestSupport.clearInspireMetadata(metadata);
        metadata.put(key, true);
        metadata.put(SERVICE_METADATA_URL.key, "http://foo.com?bar=baz");
        metadata.put(SERVICE_METADATA_TYPE.key, "application/vnd.iso.19139+xml");
        metadata.put(LANGUAGE.key, "fre");
        getGeoServer().save(serviceInfo);
        final Document dom = getAsDOM(WMSExtendedCapabilitiesTest.WMS_1_1_1_GETCAPREQUEST);
        final NodeList nodeList = dom.getElementsByTagNameNS(InspireSchema.VS_NAMESPACE, "ExtendedCapabilities");
        Assert.assertEquals("Number of INSPIRE ExtendedCapabilities elements", 0, nodeList.getLength());
    }
}

