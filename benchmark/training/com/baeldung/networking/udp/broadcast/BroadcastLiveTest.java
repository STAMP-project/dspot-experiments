package com.baeldung.networking.udp.broadcast;


import org.junit.Assert;
import org.junit.Test;


public class BroadcastLiveTest {
    private BroadcastingClient client;

    @Test
    public void whenBroadcasting_thenDiscoverExpectedServers() throws Exception {
        int expectedServers = 4;
        initializeForExpectedServers(expectedServers);
        int serversDiscovered = client.discoverServers("hello server");
        Assert.assertEquals(expectedServers, serversDiscovered);
    }
}

