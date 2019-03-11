package com.netflix.eureka;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Nitesh Kant
 */
public class RemoteRegionSoftDependencyTest extends AbstractTester {
    @Test
    public void testSoftDepRemoteDown() throws Exception {
        Assert.assertTrue("Registry access disallowed when remote region is down.", registry.shouldAllowAccess(false));
        Assert.assertFalse("Registry access allowed when remote region is down.", registry.shouldAllowAccess(true));
    }
}

