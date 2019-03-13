/**
 * (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.qos.wms;


import org.geoserver.test.GeoServerSystemTestSupport;
import org.junit.Test;


public class QosWMSCapabilitiesProviderTest extends GeoServerSystemTestSupport {
    private static final String WMS_1_1_1_GETCAPREQUEST = "wms?request=GetCapabilities&service=WMS&version=1.1.1";

    private static final String WMS_1_3_0_GETCAPREQUEST = "wms?request=GetCapabilities&service=WMS&version=1.3.0";

    public QosWMSCapabilitiesProviderTest() {
    }

    @Test
    public void testWorkspaceQosMetadata() throws Exception {
        // getGeoServer().getCatalog().setDefaultWorkspace(getWorkspaceInfo());
        // setupWSQosData();
        // final Document dom = getAsDOM(getGetCapabilitiesRequestPath());
        // final NodeList nodeList =
        // dom.getElementsByTagNameNS(QosSchema.QOS_WMS_NAMESPACE,
        // "ExtendedCapabilities");
        // assertEquals("Number of INSPIRE ExtendedCapabilities elements", 0,
        // nodeList.getLength());
    }
}

