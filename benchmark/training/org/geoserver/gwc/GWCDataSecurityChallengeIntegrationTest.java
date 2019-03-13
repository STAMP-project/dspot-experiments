/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.gwc;


import HttpServletResponse.SC_FORBIDDEN;
import HttpServletResponse.SC_OK;
import HttpServletResponse.SC_UNAUTHORIZED;
import org.geoserver.wms.WMSTestSupport;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;


public class GWCDataSecurityChallengeIntegrationTest extends WMSTestSupport {
    @Test
    public void testDirectWMSIntegration() throws Exception {
        String path = ((("wms?service=WMS&request=GetMap&version=1.1.1&format=image/png" + "&layers=") + (getLayerId(MockData.LAKES))) + "&srs=EPSG:4326") + "&width=256&height=256&styles=&bbox=-180.0,-90.0,0.0,90.0&tiled=true";
        MockHttpServletResponse response;
        // Try first as anonymous user, which should be disallowed.
        setRequestAuth(null, null);
        response = getAsServletResponse(path);
        Assert.assertEquals(SC_UNAUTHORIZED, response.getStatus());
        // Make initial authorized request to cache the item.
        setRequestAuth("cite", "cite");
        response = getAsServletResponse(path);
        Assert.assertEquals(SC_OK, response.getStatus());
        Assert.assertEquals("image/png", response.getContentType());
        Assert.assertThat(response.getHeader("geowebcache-cache-result"), Matchers.equalToIgnoringCase("MISS"));
        // Make second authorized request to ensure the item was cached.
        response = getAsServletResponse(path);
        Assert.assertEquals(SC_OK, response.getStatus());
        Assert.assertEquals("image/png", response.getContentType());
        Assert.assertThat(response.getHeader("geowebcache-cache-result"), Matchers.equalToIgnoringCase("HIT"));
        // Ensure other unauthorized users can't access the cached tile.
        setRequestAuth("other", "other");
        response = getAsServletResponse(path);
        Assert.assertEquals(SC_FORBIDDEN, response.getStatus());
        // Ensure anonymous users can't access the cached tile.
        setRequestAuth(null, null);
        response = getAsServletResponse(path);
        Assert.assertEquals(SC_UNAUTHORIZED, response.getStatus());
    }
}

