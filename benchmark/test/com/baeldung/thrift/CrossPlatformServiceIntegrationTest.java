package com.baeldung.thrift;


import org.junit.Assert;
import org.junit.Test;


public class CrossPlatformServiceIntegrationTest {
    private CrossPlatformServiceServer server = new CrossPlatformServiceServer();

    @Test
    public void ping() {
        CrossPlatformServiceClient client = new CrossPlatformServiceClient();
        Assert.assertTrue(client.ping());
    }
}

