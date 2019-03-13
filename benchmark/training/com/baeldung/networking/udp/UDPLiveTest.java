package com.baeldung.networking.udp;


import org.junit.Assert;
import org.junit.Test;


public class UDPLiveTest {
    private EchoClient client;

    @Test
    public void whenCanSendAndReceivePacket_thenCorrect1() {
        String echo = client.sendEcho("hello server");
        Assert.assertEquals("hello server", echo);
        echo = client.sendEcho("server is working");
        Assert.assertFalse(echo.equals("hello server"));
    }
}

