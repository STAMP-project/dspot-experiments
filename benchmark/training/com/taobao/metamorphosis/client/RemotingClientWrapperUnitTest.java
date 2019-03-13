package com.taobao.metamorphosis.client;


import com.sun.org.apache.xerces.internal.util.URI;
import com.taobao.metamorphosis.network.RemotingUtils;
import org.junit.Assert;
import org.junit.Test;


public class RemotingClientWrapperUnitTest {
    @Test
    public void testTryGetLoopbackURL() throws Exception {
        RemotingUtils.setLocalHost("192.168.1.100");
        try {
            String url = "meta://192.168.1.100:8123";
            String loopbackURL = "meta://localhost:8123";
            Assert.assertEquals(loopbackURL, RemotingClientWrapper.tryGetLoopbackURL(url));
            Assert.assertEquals("localhost", new URI(loopbackURL).getHost());
        } finally {
            RemotingUtils.setLocalHost(null);
        }
    }
}

