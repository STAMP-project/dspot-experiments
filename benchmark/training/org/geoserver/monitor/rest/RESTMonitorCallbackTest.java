/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.monitor.rest;


import org.geoserver.catalog.Catalog;
import org.geoserver.monitor.Monitor;
import org.geoserver.monitor.RequestData;
import org.geoserver.rest.RestBaseController;
import org.geoserver.test.GeoServerSystemTestSupport;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;


public class RESTMonitorCallbackTest extends GeoServerSystemTestSupport {
    static Monitor monitor;

    RESTMonitorCallback callback;

    RequestData data;

    static Catalog catalog;

    @Test
    public void testURLEncodedRequestPathInfo() throws Exception {
        MockHttpServletResponse response = getAsServletResponse(((RestBaseController.ROOT_PATH) + "/layers/foo"));
        Assert.assertEquals(404, response.getStatus());
        Assert.assertEquals("foo", data.getResources().get(1));
        response = getAsServletResponse(((RestBaseController.ROOT_PATH) + "/layers/acme:foo"));
        Assert.assertEquals(404, response.getStatus());
        Assert.assertEquals("acme:foo", data.getResources().get(2));
        response = getAsServletResponse(((RestBaseController.ROOT_PATH) + "acme:foo"));
        Assert.assertEquals(404, response.getStatus());
        Assert.assertEquals("acme:foo", data.getResources().get(3));
    }
}

