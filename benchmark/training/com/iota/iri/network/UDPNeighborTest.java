package com.iota.iri.network;


import java.net.SocketAddress;
import org.junit.Assert;
import org.junit.Test;


public class UDPNeighborTest {
    private final UDPNeighbor neighbor = new UDPNeighbor(address("localhost", 42), null, false);

    @Test
    public void sameIpWhenMatchesThenTrue() {
        Assert.assertTrue("expected match", neighbor.matches(address("localhost", 42)));
        Assert.assertTrue("expected match", neighbor.matches(address("localhost", 666)));
        Assert.assertTrue("expected match", neighbor.matches(address("127.0.0.1", 42)));
        Assert.assertTrue("expected match", neighbor.matches(address("127.0.0.1", 666)));
    }

    @Test
    public void differentIpWhenMatchesThenFalse() {
        Assert.assertFalse("expected no match", neighbor.matches(address("foo.bar.com", 42)));
        Assert.assertFalse("expected no match", neighbor.matches(address("8.8.8.8", 42)));
        Assert.assertFalse("expected no match", neighbor.matches(null));
        Assert.assertFalse("expected no match", neighbor.matches(new SocketAddress() {}));
    }
}

