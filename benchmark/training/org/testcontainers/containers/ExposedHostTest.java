package org.testcontainers.containers;


import com.sun.net.httpserver.HttpServer;
import org.junit.Test;


public class ExposedHostTest {
    private static HttpServer server;

    @Test
    public void testExposedHost() throws Exception {
        assertResponse(new GenericContainer().withCommand("top"));
    }

    @Test
    public void testExposedHostWithNetwork() throws Exception {
        try (Network network = Network.newNetwork()) {
            assertResponse(new GenericContainer().withNetwork(network).withCommand("top"));
        }
    }
}

