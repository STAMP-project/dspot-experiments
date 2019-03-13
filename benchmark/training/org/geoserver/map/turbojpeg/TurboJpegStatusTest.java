/**
 * (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.map.turbojpeg;


import org.junit.Assert;
import org.junit.Test;


public class TurboJpegStatusTest {
    @Test
    public void testTurboJpegStatus() {
        TurboJpegStatus status = new TurboJpegStatus();
        // these should always return "something"
        Assert.assertTrue(((status.getModule().length()) > 0));
        Assert.assertTrue(((status.getName().length()) > 0));
        Assert.assertTrue(((status.getComponent().get().length()) > 0));
        Assert.assertTrue(((status.getMessage().get().length()) > 0));
        if (!(status.isAvailable())) {
            return;// skip test

        }
        Assert.assertTrue(status.getMessage().get().contains("Wrapper Version"));
    }
}

