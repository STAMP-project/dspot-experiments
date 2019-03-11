package com.baeldung.java.nio.selector;


import org.junit.Assert;
import org.junit.Test;


public class NioEchoLiveTest {
    private Process server;

    private EchoClient client;

    @Test
    public void givenServerClient_whenServerEchosMessage_thenCorrect() {
        String resp1 = client.sendMessage("hello");
        String resp2 = client.sendMessage("world");
        Assert.assertEquals("hello", resp1);
        Assert.assertEquals("world", resp2);
    }
}

