/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.security;


import org.geoserver.test.GeoServerSystemTestSupport;
import org.geoserver.test.SystemTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category(SystemTest.class)
public class GeoServerCustomSecurityProviderTest extends GeoServerSystemTestSupport {
    public static class SecurityProvider extends GeoServerSecurityProvider {
        static boolean initCalled = false;

        static boolean destroyCalled = false;

        @Override
        public void init(GeoServerSecurityManager manager) {
            GeoServerCustomSecurityProviderTest.SecurityProvider.initCalled = true;
        }

        @Override
        public void destroy(GeoServerSecurityManager manager) {
            GeoServerCustomSecurityProviderTest.SecurityProvider.destroyCalled = true;
        }
    }

    @Test
    public void testThatInitIsCalled() {
        Assert.assertTrue("The Security provider's init method should be called", GeoServerCustomSecurityProviderTest.SecurityProvider.initCalled);
    }

    @Test
    public void testThatDestroyIsCalled() throws Exception {
        destroyGeoServer();
        Assert.assertTrue("The Security provider's destroy method should be called", GeoServerCustomSecurityProviderTest.SecurityProvider.destroyCalled);
    }
}

