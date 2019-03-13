package com.baeldung.networking.udp.multicast;


import org.junit.Assert;
import org.junit.Test;


public class MulticastLiveTest {
    private MulticastingClient client;

    @Test
    public void whenBroadcasting_thenDiscoverExpectedServers() throws Exception {
        int expectedServers = 4;
        initializeForExpectedServers(expectedServers);
        int serversDiscovered = client.discoverServers("hello server");
        Assert.assertEquals(expectedServers, serversDiscovered);
    }
}

