package com.baeldung.socket;


import org.junit.Assert;
import org.junit.Test;


public class SocketEchoMultiIntegrationTest {
    private static int port;

    @Test
    public void givenClient1_whenServerResponds_thenCorrect() {
        EchoClient client = new EchoClient();
        client.startConnection("127.0.0.1", SocketEchoMultiIntegrationTest.port);
        String msg1 = client.sendMessage("hello");
        String msg2 = client.sendMessage("world");
        String terminate = client.sendMessage(".");
        Assert.assertEquals(msg1, "hello");
        Assert.assertEquals(msg2, "world");
        Assert.assertEquals(terminate, "bye");
        client.stopConnection();
    }

    @Test
    public void givenClient2_whenServerResponds_thenCorrect() {
        EchoClient client = new EchoClient();
        client.startConnection("127.0.0.1", SocketEchoMultiIntegrationTest.port);
        String msg1 = client.sendMessage("hello");
        String msg2 = client.sendMessage("world");
        String terminate = client.sendMessage(".");
        Assert.assertEquals(msg1, "hello");
        Assert.assertEquals(msg2, "world");
        Assert.assertEquals(terminate, "bye");
        client.stopConnection();
    }

    @Test
    public void givenClient3_whenServerResponds_thenCorrect() {
        EchoClient client = new EchoClient();
        client.startConnection("127.0.0.1", SocketEchoMultiIntegrationTest.port);
        String msg1 = client.sendMessage("hello");
        String msg2 = client.sendMessage("world");
        String terminate = client.sendMessage(".");
        Assert.assertEquals(msg1, "hello");
        Assert.assertEquals(msg2, "world");
        Assert.assertEquals(terminate, "bye");
        client.stopConnection();
    }
}

