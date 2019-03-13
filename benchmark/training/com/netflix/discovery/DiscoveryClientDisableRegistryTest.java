package com.netflix.discovery;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Nitesh Kant
 */
public class DiscoveryClientDisableRegistryTest {
    private EurekaClient client;

    private MockRemoteEurekaServer mockLocalEurekaServer;

    @Test
    public void testDisableFetchRegistry() throws Exception {
        Assert.assertFalse("Registry fetch disabled but eureka server recieved a registry fetch.", mockLocalEurekaServer.isSentRegistry());
    }
}

