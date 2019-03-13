/**
 * (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.geofence.rest;


import org.geoserver.catalog.Catalog;
import org.geoserver.rest.RestBaseController;
import org.geoserver.test.GeoServerSystemTestSupport;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;


/**
 *
 *
 * @author Emanuele Tajariol (etj at geo-solutions.it)
 */
public class CacheControllerTest extends GeoServerSystemTestSupport {
    protected static Catalog catalog;

    @Test
    public void testGetInfo() throws Exception {
        MockHttpServletResponse response = getAsServletResponse(((RestBaseController.ROOT_PATH) + "/geofence/ruleCache/info"));
        Assert.assertEquals(200, response.getStatus());
        assertContentType(MediaType.TEXT_PLAIN_VALUE, response);
        String content = response.getContentAsString();
        Assert.assertTrue("Missing RuleStats", content.contains("RuleStats["));
        Assert.assertTrue("Missing AdminAuthStats", content.contains("AdminAuthStats["));
        Assert.assertTrue("Missing UserStats", content.contains("UserStats["));
    }

    @Test
    public void testGetInfoLegacy() throws Exception {
        MockHttpServletResponse response = getAsServletResponse(((RestBaseController.ROOT_PATH) + "/ruleCache/info"));
        Assert.assertEquals(200, response.getStatus());
        assertContentType(MediaType.TEXT_PLAIN_VALUE, response);
        String content = response.getContentAsString();
        Assert.assertTrue("Missing RuleStats", content.contains("RuleStats["));
        Assert.assertTrue("Missing AdminAuthStats", content.contains("AdminAuthStats["));
        Assert.assertTrue("Missing UserStats", content.contains("UserStats["));
    }

    @Test
    public void testInvalidate() throws Exception {
        MockHttpServletResponse response = putAsServletResponse(((RestBaseController.ROOT_PATH) + "/geofence/ruleCache/invalidate"));
        Assert.assertEquals(200, response.getStatus());
        assertContentType(MediaType.TEXT_PLAIN_VALUE, response);
        String content = response.getContentAsString();
        Assert.assertEquals("OK", content);
    }
}

