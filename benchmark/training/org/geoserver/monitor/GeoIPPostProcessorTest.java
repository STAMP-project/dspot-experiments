/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.monitor;


import com.maxmind.geoip.Location;
import com.maxmind.geoip.LookupService;
import org.junit.Assert;
import org.junit.Test;


public class GeoIPPostProcessorTest {
    static LookupService geoipLookup;

    @Test
    public void testLookup() throws Exception {
        if ((GeoIPPostProcessorTest.geoipLookup) == null) {
            return;
        }
        Location loc = GeoIPPostProcessorTest.geoipLookup.getLocation("64.147.114.82");
        Assert.assertEquals("United States", loc.countryName);
        Assert.assertEquals("New York", loc.city);
        loc = GeoIPPostProcessorTest.geoipLookup.getLocation("192.168.1.103");
        Assert.assertNull(loc);
    }
}

