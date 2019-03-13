/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wms.wms_1_3;


import MockData.PRIMITIVEGEOFEATURE;
import org.geoserver.wms.WMSTestSupport;
import org.junit.Test;
import org.w3c.dom.Document;


public class CapabilitiesBBOXForEachCRSTest extends WMSTestSupport {
    @Test
    public void testBBOXForEachCRS() throws Exception {
        Document doc = getAsDOM("sf/PrimitiveGeoFeature/wms?service=WMS&request=getCapabilities&version=1.3.0", true);
        String layer = PRIMITIVEGEOFEATURE.getLocalPart();
        assertXpathExists((("//wms:Layer[wms:Name='" + layer) + "']/wms:BoundingBox[@CRS = 'EPSG:4326']"), doc);
        assertXpathNotExists((("//wms:Layer[wms:Name='" + layer) + "']/wms:BoundingBox[@CRS = 'EPSG:3005']"), doc);
        assertXpathNotExists((("//wms:Layer[wms:Name='" + layer) + "']/wms:BoundingBox[@CRS = 'EPSG:3857']"), doc);
        addSRSAndSetFlag();
        doc = getAsDOM("sf/PrimitiveGeoFeature/wms?service=WMS&request=getCapabilities&version=1.3.0", true);
        assertXpathExists((("//wms:Layer[wms:Name='" + layer) + "']/wms:BoundingBox[@CRS = 'EPSG:4326']"), doc);
        assertXpathExists((("//wms:Layer[wms:Name='" + layer) + "']/wms:BoundingBox[@CRS = 'EPSG:3005']"), doc);
        assertXpathExists((("//wms:Layer[wms:Name='" + layer) + "']/wms:BoundingBox[@CRS = 'EPSG:3857']"), doc);
    }

    @Test
    public void testRootLayer() throws Exception {
        Document doc = getAsDOM("sf/PrimitiveGeoFeature/wms?service=WMS&request=getCapabilities&version=1.3.0", true);
        assertXpathNotExists("/wms:WMS_Capabilities/wms:Capability/wms:Layer/wms:BoundingBox[@CRS = 'EPSG:4326']", doc);
        assertXpathNotExists("/wms:WMS_Capabilities/wms:Capability/wms:Layer/wms:BoundingBox[@CRS = 'EPSG:3005']", doc);
        assertXpathNotExists("/wms:WMS_Capabilities/wms:Capability/wms:Layer/wms:BoundingBox[@CRS = 'EPSG:3857']", doc);
        addSRSAndSetFlag();
        doc = getAsDOM("sf/PrimitiveGeoFeature/wms?service=WMS&request=getCapabilities&version=1.3.0", true);
        assertXpathExists("/wms:WMS_Capabilities/wms:Capability/wms:Layer/wms:BoundingBox[@CRS = 'EPSG:4326']", doc);
        assertXpathExists("/wms:WMS_Capabilities/wms:Capability/wms:Layer/wms:BoundingBox[@CRS = 'EPSG:3005']", doc);
        assertXpathExists("/wms:WMS_Capabilities/wms:Capability/wms:Layer/wms:BoundingBox[@CRS = 'EPSG:3857']", doc);
    }
}

