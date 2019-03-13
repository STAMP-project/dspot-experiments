/**
 * (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.grib;


import org.junit.Assert;
import org.junit.Test;


public class GribStatusTest {
    @Test
    public void testNetCDFStatus() {
        GribStatus status = new GribStatus();
        // these should always return "something"
        Assert.assertTrue(((status.getModule().length()) > 0));
        Assert.assertTrue(((status.getName().length()) > 0));
        Assert.assertTrue(((status.getComponent().get().length()) > 0));
        Assert.assertTrue(((status.getMessage().get().length()) > 0));
        Assert.assertTrue(status.getVersion().isPresent());
        Assert.assertTrue(status.getMessage().get().contains("isGribAvailable"));
    }
}

