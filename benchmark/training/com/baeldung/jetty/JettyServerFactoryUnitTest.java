package com.baeldung.jetty;


import org.eclipse.jetty.server.Server;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test for {@link JettyServerFactory}.
 *
 * @author Donato Rimenti
 */
public class JettyServerFactoryUnitTest {
    /**
     * Tests that when a base server is provided a request returns a status 404.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void givenBaseServer_whenHttpRequest_thenStatus404() throws Exception {
        Server server = JettyServerFactory.createBaseServer();
        server.start();
        int statusCode = sendGetRequest();
        Assert.assertEquals(404, statusCode);
        server.stop();
    }

    /**
     * Tests that when a web app server is provided a request returns a status
     * 200.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void givenWebAppServer_whenHttpRequest_thenStatus200() throws Exception {
        Server server = JettyServerFactory.createWebAppServer();
        server.start();
        int statusCode = sendGetRequest();
        Assert.assertEquals(200, statusCode);
        server.stop();
    }

    /**
     * Tests that when a multi handler server is provided a request returns a
     * status 200.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void givenMultiHandlerServerServer_whenHttpRequest_thenStatus200() throws Exception {
        Server server = JettyServerFactory.createMultiHandlerServer();
        server.start();
        int statusCode = sendGetRequest();
        Assert.assertEquals(200, statusCode);
        server.stop();
    }
}

