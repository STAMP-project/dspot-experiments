package com.baeldung.micronaut.helloworld.client;


import io.micronaut.runtime.server.EmbeddedServer;
import junit.framework.TestCase;
import org.junit.Test;


public class GreetingClientTest {
    private EmbeddedServer server;

    private GreetingClient client;

    @Test
    public void testGreeting() {
        TestCase.assertEquals(client.greet("Mike"), "Hello Mike");
    }
}

