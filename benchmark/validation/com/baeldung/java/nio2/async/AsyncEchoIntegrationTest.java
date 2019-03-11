package com.baeldung.java.nio2.async;


import org.junit.Assert;
import org.junit.Test;


public class AsyncEchoIntegrationTest {
    Process server;

    AsyncEchoClient client;

    @Test
    public void givenServerClient_whenServerEchosMessage_thenCorrect() throws Exception {
        String resp1 = client.sendMessage("hello");
        String resp2 = client.sendMessage("world");
        Assert.assertEquals("hello", resp1);
        Assert.assertEquals("world", resp2);
    }
}

