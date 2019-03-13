/**
 * (c) 2014 - 2015 Open Source Geospatial Foundation - all rights reserved
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


public class RESTDispatcherCallbackTest extends GeoServerSystemTestSupport {
    DispatcherCallback callback;

    @Test
    public void testCallback() throws Exception {
        callback.init(anyObject(), anyObject());
        expectLastCall();
        callback.dispatched(anyObject(), anyObject(), anyObject());
        expectLastCall();
        callback.finished(anyObject(), anyObject());
        expectLastCall();
        replay(callback);
        MockHttpServletResponse response = getAsServletResponse(((ROOT_PATH) + "/index.html"));
        Assert.assertEquals(200, response.getStatus());
        verify(callback);
    }

    @Test
    public void testCallbackException() throws Exception {
        callback.init(anyObject(), anyObject());
        expectLastCall();
        callback.dispatched(anyObject(), anyObject(), anyObject());
        expectLastCall();
        callback.exception(anyObject(), anyObject(), anyObject());
        expectLastCall();
        callback.finished(anyObject(), anyObject());
        expectLastCall();
        replay(callback);
        getAsServletResponse(((ROOT_PATH) + "/exception?code=400&message=error"));
        verify(callback);
    }
}

