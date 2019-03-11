/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.ows.util;


import javax.servlet.http.HttpServletRequest;
import org.junit.Assert;
import org.junit.Test;


public class RequestUtilsTest {
    @Test
    public void testGetRemoteAddrNotForwarded() {
        HttpServletRequest req = RequestUtilsTest.request("192.168.1.1", null);
        Assert.assertEquals("192.168.1.1", RequestUtils.getRemoteAddr(req));
    }

    @Test
    public void testGetRemoteAddrSingleForwardedIP() {
        HttpServletRequest req = RequestUtilsTest.request("192.168.1.2", "192.168.1.1");
        Assert.assertEquals("192.168.1.1", RequestUtils.getRemoteAddr(req));
    }

    @Test
    public void testGetRemoteAddrMultipleForwardedIP() {
        HttpServletRequest req = RequestUtilsTest.request("192.168.1.4", "192.168.1.1, 192.168.1.2, 192.168.1.3");
        Assert.assertEquals("192.168.1.1", RequestUtils.getRemoteAddr(req));
    }
}

