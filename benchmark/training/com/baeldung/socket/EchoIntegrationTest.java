package com.baeldung.socket;


import org.junit.Assert;
import org.junit.Test;


public class EchoIntegrationTest {
    private static int port;

    private EchoClient client = new EchoClient();

    // 
    @Test
    public void givenClient_whenServerEchosMessage_thenCorrect() {
        String resp1 = client.sendMessage("hello");
        String resp2 = client.sendMessage("world");
        String resp3 = client.sendMessage("!");
        String resp4 = client.sendMessage(".");
        Assert.assertEquals("hello", resp1);
        Assert.assertEquals("world", resp2);
        Assert.assertEquals("!", resp3);
        Assert.assertEquals("good bye", resp4);
    }
}

