/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.security;


import java.util.function.Function;
import org.geoserver.security.config.BruteForcePreventionConfig;
import org.geoserver.security.config.SecurityManagerConfig;
import org.geoserver.test.GeoServerSystemTestSupport;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


// "Too many failed logins waiting on delay already";
public class BruteForceAttackTest extends GeoServerSystemTestSupport {
    private static final String HELLO_GET_REQUEST = "ows?service=hello&request=hello&message=Hello_World";

    @Test
    public void testLoginDelay() throws Exception {
        // successful login, no wait (cannot actually test it)
        setRequestAuth("admin", "geoserver");
        Assert.assertEquals(200, getAsServletResponse(BruteForceAttackTest.HELLO_GET_REQUEST).getStatus());
        // failing login, at least one second wait
        setRequestAuth("admin", "foobar");
        long start = System.currentTimeMillis();
        Assert.assertEquals(401, getAsServletResponse(BruteForceAttackTest.HELLO_GET_REQUEST).getStatus());
        long end = System.currentTimeMillis();
        Assert.assertThat((end - start), Matchers.greaterThan(1000L));
    }

    @Test
    public void testParallelLogin() throws Exception {
        testParallelLogin("Unauthorized", ( i) -> "foo");
    }

    @Test
    public void testTooManyBlockedThreads() throws Exception {
        // configure it to allow only one thread in the wait list
        GeoServerSecurityManager manager = applicationContext.getBean(GeoServerSecurityManager.class);
        final SecurityManagerConfig securityConfig = manager.getSecurityConfig();
        BruteForcePreventionConfig bruteForceConfig = securityConfig.getBruteForcePrevention();
        bruteForceConfig.setMaxBlockedThreads(1);
        manager.saveSecurityConfig(securityConfig);
        // hit with many different users
        testParallelLogin("Unauthorized", ( i) -> "foo" + i);
    }
}

