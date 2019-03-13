package com.baeldung.socket;


import org.junit.Assert;
import org.junit.Test;


public class GreetServerIntegrationTest {
    private GreetClient client;

    private static int port;

    @Test
    public void givenGreetingClient_whenServerRespondsWhenStarted_thenCorrect() {
        String response = client.sendMessage("hello server");
        Assert.assertEquals("hello client", response);
    }
}

