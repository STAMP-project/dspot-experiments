/**
 * (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wms;


import org.geoserver.catalog.LayerInfo;
import org.geoserver.test.GeoServerMockTestSupport;
import org.junit.Test;


public class WMSValidatorTest extends GeoServerMockTestSupport {
    @Test
    public void testGeometryCheckLegacyDataDir() {
        // used to NPE
        LayerInfo layer = getCatalog().getLayerByName("Buildings");
        new WMSValidator().validate(layer, false);
    }
}

