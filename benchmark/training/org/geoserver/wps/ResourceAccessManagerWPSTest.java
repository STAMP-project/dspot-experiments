/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wps;


import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;


public class ResourceAccessManagerWPSTest extends WPSTestSupport {
    @Test
    public void testDenyAccess() throws Exception {
        Document dom = runBuildingsRequest();
        // print(dom);
        Assert.assertEquals("1", WPSTestSupport.xp.evaluate("count(//wps:ProcessFailed)", dom));
        Assert.assertEquals("0", WPSTestSupport.xp.evaluate("count(//wps:ProcessSucceded)", dom));
    }

    @Test
    public void testAllowAccess() throws Exception {
        setRequestAuth("cite", "cite");
        Document dom = runBuildingsRequest();
        // print(dom);
        Assert.assertEquals("0", WPSTestSupport.xp.evaluate("count(//wps:ProcessFailed)", dom));
        Assert.assertEquals("1", WPSTestSupport.xp.evaluate("count(//wps:ProcessSucceeded)", dom));
        String[] lc = WPSTestSupport.xp.evaluate("//wps:Output[ows:Identifier = 'bounds']/wps:Data/wps:BoundingBoxData/ows:LowerCorner", dom).split("\\s+");
        Assert.assertEquals(8.0E-4, Double.parseDouble(lc[0]), 0.0);
        Assert.assertEquals(5.0E-4, Double.parseDouble(lc[1]), 0.0);
        String[] uc = WPSTestSupport.xp.evaluate("//wps:Output[ows:Identifier = 'bounds']/wps:Data/wps:BoundingBoxData/ows:UpperCorner", dom).split("\\s+");
        Assert.assertEquals(0.0024, Double.parseDouble(uc[0]), 0.0);
        Assert.assertEquals(0.001, Double.parseDouble(uc[1]), 0.0);
    }
}

