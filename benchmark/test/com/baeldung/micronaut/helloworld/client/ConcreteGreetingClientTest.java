package com.baeldung.micronaut.helloworld.client;


import io.micronaut.runtime.server.EmbeddedServer;
import junit.framework.TestCase;
import org.junit.Test;


public class ConcreteGreetingClientTest {
    private EmbeddedServer server;

    private ConcreteGreetingClient client;

    @Test
    public void testGreeting() {
        TestCase.assertEquals(client.greet("Mike"), "Hello Mike");
    }

    @Test
    public void testGreetingAsync() {
        TestCase.assertEquals(client.greetAsync("Mike").blockingGet(), "Hello Mike");
    }
}

