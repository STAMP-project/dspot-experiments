/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.rest;


import org.geoserver.config.impl.GeoServerLifecycleHandler;
import org.geoserver.test.GeoServerSystemTestSupport;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;


public class CatalogReloadControllerTest extends GeoServerSystemTestSupport {
    static CatalogReloadControllerTest.ReloadResetWatcher watcher = new CatalogReloadControllerTest.ReloadResetWatcher();

    @Test
    public synchronized void testPutReload() throws Exception {
        reset();
        MockHttpServletResponse response = putAsServletResponse(((RestBaseController.ROOT_PATH) + "/reload"), ((String) (null)), null);
        Assert.assertEquals(200, response.getStatus());
        Assert.assertTrue(CatalogReloadControllerTest.watcher.didReload);
        Assert.assertTrue(CatalogReloadControllerTest.watcher.didReset);
    }

    @Test
    public synchronized void testPostReload() throws Exception {
        reset();
        MockHttpServletResponse response = postAsServletResponse(((RestBaseController.ROOT_PATH) + "/reload"), "", null);
        Assert.assertEquals(200, response.getStatus());
        Assert.assertTrue(CatalogReloadControllerTest.watcher.didReload);
        Assert.assertTrue(CatalogReloadControllerTest.watcher.didReset);
    }

    @Test
    public synchronized void testPutReset() throws Exception {
        reset();
        MockHttpServletResponse response = putAsServletResponse(((RestBaseController.ROOT_PATH) + "/reset"), ((String) (null)), null);
        Assert.assertEquals(200, response.getStatus());
        Assert.assertFalse(CatalogReloadControllerTest.watcher.didReload);
        Assert.assertTrue(CatalogReloadControllerTest.watcher.didReset);
    }

    @Test
    public synchronized void testPostReset() throws Exception {
        reset();
        MockHttpServletResponse response = postAsServletResponse(((RestBaseController.ROOT_PATH) + "/reset"), "", null);
        Assert.assertEquals(200, response.getStatus());
        Assert.assertFalse(CatalogReloadControllerTest.watcher.didReload);
        Assert.assertTrue(CatalogReloadControllerTest.watcher.didReset);
    }

    private static class ReloadResetWatcher implements GeoServerLifecycleHandler {
        boolean didReload;

        boolean didReset;

        @Override
        public void onReset() {
            didReset = true;
        }

        @Override
        public void onReload() {
            didReload = true;
        }

        @Override
        public void onDispose() {
        }

        @Override
        public void beforeReload() {
        }
    }
}

