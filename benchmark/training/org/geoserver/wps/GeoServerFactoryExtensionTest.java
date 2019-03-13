/**
 * (c) 2014 - 2015Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wps;


import org.junit.Assert;
import org.junit.Test;


public class GeoServerFactoryExtensionTest extends WPSTestSupport {
    @Test
    public void testWPSFactoryExtension() {
        WPSInfo info = getGeoServer().getFactory().create(WPSInfo.class);
        Assert.assertNotNull(info);
    }
}

