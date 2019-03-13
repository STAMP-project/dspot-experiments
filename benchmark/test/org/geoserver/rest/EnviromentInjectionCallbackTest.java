/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.rest;


import org.geoserver.test.GeoServerSystemTestSupport;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;

import static RestBaseController.ROOT_PATH;


public class EnviromentInjectionCallbackTest extends GeoServerSystemTestSupport {
    @Test
    public void testNoUser() throws Exception {
        MockHttpServletResponse r = getAsServletResponse(((ROOT_PATH) + "/gsuser"));
        Assert.assertEquals(200, r.getStatus());
        Assert.assertTrue(r.getContentType().startsWith("text/plain"));
        Assert.assertEquals("USER_NOT_FOUND", r.getContentAsString());
    }

    @Test
    public void testUser() throws Exception {
        login("testUser", "testPassword");
        MockHttpServletResponse r = getAsServletResponse(((ROOT_PATH) + "/gsuser"));
        Assert.assertEquals(200, r.getStatus());
        String contentType = r.getContentType();
        Assert.assertTrue(contentType.startsWith("text/plain"));
        Assert.assertEquals("testUser", r.getContentAsString());
    }
}

