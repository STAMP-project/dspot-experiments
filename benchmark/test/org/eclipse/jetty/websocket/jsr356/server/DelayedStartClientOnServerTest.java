/**
 *
 */
/**
 * ========================================================================
 */
/**
 * Copyright (c) 1995-2019 Mort Bay Consulting Pty. Ltd.
 */
/**
 * ------------------------------------------------------------------------
 */
/**
 * All rights reserved. This program and the accompanying materials
 */
/**
 * are made available under the terms of the Eclipse Public License v1.0
 */
/**
 * and Apache License v2.0 which accompanies this distribution.
 */
/**
 *
 */
/**
 * The Eclipse Public License is available at
 */
/**
 * http://www.eclipse.org/legal/epl-v10.html
 */
/**
 *
 */
/**
 * The Apache License v2.0 is available at
 */
/**
 * http://www.opensource.org/licenses/apache2.0.php
 */
/**
 *
 */
/**
 * You may elect to redistribute this code under either of these licenses.
 */
/**
 * ========================================================================
 */
/**
 *
 */
package org.eclipse.jetty.websocket.jsr356.server;


import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.ContainerProvider;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.OnMessage;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import javax.websocket.server.ServerContainer;
import javax.websocket.server.ServerEndpoint;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.component.ContainerLifeCycle;
import org.eclipse.jetty.websocket.api.util.WSURI;
import org.eclipse.jetty.websocket.jsr356.ClientContainer;
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


public class DelayedStartClientOnServerTest {
    @ServerEndpoint("/echo")
    public static class EchoSocket {
        @OnMessage
        public String echo(String msg) {
            return msg;
        }
    }

    /**
     * Using the Client specific techniques of JSR356, connect to the echo socket
     * and perform an echo request.
     */
    public static class ClientConnectServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
            // Client specific technique
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            try {
                URI wsURI = WSURI.toWebsocket(req.getRequestURL()).resolve("/echo");
                Session session = container.connectToServer(new Endpoint() {
                    @Override
                    public void onOpen(Session session, EndpointConfig config) {
                        /* do nothing */
                    }
                }, wsURI);
                // don't care about the data sent, just the connect itself.
                session.getBasicRemote().sendText("Hello");
                session.close();
                resp.setContentType("text/plain");
                resp.getWriter().println(("Connected to " + wsURI));
            } catch (Throwable t) {
                throw new ServletException(t);
            }
        }
    }

    /**
     * Using the Server specific techniques of JSR356, connect to the echo socket
     * and perform an echo request.
     */
    public static class ServerConnectServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
            // Server specific technique
            ServerContainer container = ((ServerContainer) (req.getServletContext().getAttribute("javax.websocket.server.ServerContainer")));
            try {
                URI wsURI = WSURI.toWebsocket(req.getRequestURL()).resolve("/echo");
                Session session = container.connectToServer(new Endpoint() {
                    @Override
                    public void onOpen(Session session, EndpointConfig config) {
                        /* do nothing */
                    }
                }, wsURI);
                // don't care about the data sent, just the connect itself.
                session.getBasicRemote().sendText("Hello");
                session.close();
                resp.setContentType("text/plain");
                resp.getWriter().println(("Connected to " + wsURI));
            } catch (Throwable t) {
                throw new ServletException(t);
            }
        }
    }

    /**
     * Using the Client specific techniques of JSR356, configure the WebSocketContainer.
     */
    public static class ClientConfigureServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
            // Client specific technique
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            try {
                container.setAsyncSendTimeout(5000);
                container.setDefaultMaxTextMessageBufferSize(1000);
                resp.setContentType("text/plain");
                resp.getWriter().printf("Configured %s - %s%n", container.getClass().getName(), container);
            } catch (Throwable t) {
                throw new ServletException(t);
            }
        }
    }

    /**
     * Using the Server specific techniques of JSR356, configure the WebSocketContainer.
     */
    public static class ServerConfigureServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
            // Server specific technique
            ServerContainer container = ((ServerContainer) (req.getServletContext().getAttribute("javax.websocket.server.ServerContainer")));
            try {
                container.setAsyncSendTimeout(5000);
                container.setDefaultMaxTextMessageBufferSize(1000);
                resp.setContentType("text/plain");
                resp.getWriter().printf("Configured %s - %s%n", container.getClass().getName(), container);
            } catch (Throwable t) {
                throw new ServletException(t);
            }
        }
    }

    @Test
    public void testNoExtraHttpClientThreads() throws Exception {
        Server server = new Server(0);
        ServletContextHandler contextHandler = new ServletContextHandler();
        server.setHandler(contextHandler);
        try {
            server.start();
            List<String> threadNames = DelayedStartClientOnServerTest.getThreadNames(server);
            assertNoHttpClientPoolThreads(threadNames);
            MatcherAssert.assertThat("Threads", threadNames, CoreMatchers.not(CoreMatchers.hasItem(CoreMatchers.containsString("WebSocketContainer@"))));
            MatcherAssert.assertThat("Threads", threadNames, CoreMatchers.not(CoreMatchers.hasItem(CoreMatchers.containsString("WebSocketClient@"))));
        } finally {
            server.stop();
        }
    }

    @Test
    public void testHttpClientThreads_AfterClientConnectTo() throws Exception {
        Server server = new Server(0);
        ServletContextHandler contextHandler = new ServletContextHandler();
        server.setHandler(contextHandler);
        // Using JSR356 Client Techniques to connectToServer()
        contextHandler.addServlet(DelayedStartClientOnServerTest.ClientConnectServlet.class, "/connect");
        ServerContainer container = WebSocketServerContainerInitializer.configureContext(contextHandler);
        container.addEndpoint(DelayedStartClientOnServerTest.EchoSocket.class);
        try {
            server.start();
            String response = GET(server.getURI().resolve("/connect"));
            MatcherAssert.assertThat("Response", response, CoreMatchers.startsWith("Connected to ws://"));
            List<String> threadNames = DelayedStartClientOnServerTest.getThreadNames(server);
            assertNoHttpClientPoolThreads(threadNames);
            MatcherAssert.assertThat("Threads", threadNames, CoreMatchers.hasItem(CoreMatchers.containsString("WebSocketClient@")));
        } finally {
            server.stop();
        }
    }

    @Test
    public void testHttpClientThreads_AfterServerConnectTo() throws Exception {
        Assertions.assertTimeoutPreemptively(Duration.ofSeconds(5), () -> {
            Server server = new Server(0);
            ServletContextHandler contextHandler = new ServletContextHandler();
            server.setHandler(contextHandler);
            // Using JSR356 Server Techniques to connectToServer()
            contextHandler.addServlet(DelayedStartClientOnServerTest.ServerConnectServlet.class, "/connect");
            ServerContainer container = WebSocketServerContainerInitializer.configureContext(contextHandler);
            container.addEndpoint(DelayedStartClientOnServerTest.EchoSocket.class);
            try {
                server.start();
                String response = GET(server.getURI().resolve("/connect"));
                MatcherAssert.assertThat("Response", response, CoreMatchers.startsWith("Connected to ws://"));
                List<String> threadNames = DelayedStartClientOnServerTest.getThreadNames(((ContainerLifeCycle) (container)), server);
                assertNoHttpClientPoolThreads(threadNames);
                MatcherAssert.assertThat("Threads", threadNames, CoreMatchers.hasItem(CoreMatchers.containsString("WebSocketClient@")));
            } finally {
                server.stop();
            }
        });
    }

    @Test
    public void testHttpClientThreads_AfterClientConfigure() throws Exception {
        Server server = new Server(0);
        ServletContextHandler contextHandler = new ServletContextHandler();
        server.setHandler(contextHandler);
        // Using JSR356 Client Techniques to configure WebSocketContainer
        contextHandler.addServlet(DelayedStartClientOnServerTest.ClientConfigureServlet.class, "/configure");
        ServerContainer container = WebSocketServerContainerInitializer.configureContext(contextHandler);
        container.addEndpoint(DelayedStartClientOnServerTest.EchoSocket.class);
        try {
            server.start();
            String response = GET(server.getURI().resolve("/configure"));
            MatcherAssert.assertThat("Response", response, CoreMatchers.startsWith(("Configured " + (ClientContainer.class.getName()))));
            List<String> threadNames = DelayedStartClientOnServerTest.getThreadNames(((ContainerLifeCycle) (container)), server);
            assertNoHttpClientPoolThreads(threadNames);
            MatcherAssert.assertThat("Threads", threadNames, CoreMatchers.not(CoreMatchers.hasItem(CoreMatchers.containsString("WebSocketContainer@"))));
            MatcherAssert.assertThat("Threads", threadNames, CoreMatchers.not(CoreMatchers.hasItem(CoreMatchers.containsString("WebSocketClient@"))));
        } finally {
            server.stop();
        }
    }

    @Test
    public void testHttpClientThreads_AfterServerConfigure() throws Exception {
        Server server = new Server(0);
        ServletContextHandler contextHandler = new ServletContextHandler();
        server.setHandler(contextHandler);
        // Using JSR356 Server Techniques to configure WebSocketContainer
        contextHandler.addServlet(DelayedStartClientOnServerTest.ServerConfigureServlet.class, "/configure");
        ServerContainer container = WebSocketServerContainerInitializer.configureContext(contextHandler);
        container.addEndpoint(DelayedStartClientOnServerTest.EchoSocket.class);
        try {
            server.start();
            String response = GET(server.getURI().resolve("/configure"));
            MatcherAssert.assertThat("Response", response, CoreMatchers.startsWith(("Configured " + (ServerContainer.class.getName()))));
            List<String> threadNames = DelayedStartClientOnServerTest.getThreadNames(((ContainerLifeCycle) (container)), server);
            assertNoHttpClientPoolThreads(threadNames);
            MatcherAssert.assertThat("Threads", threadNames, CoreMatchers.not(CoreMatchers.hasItem(CoreMatchers.containsString("WebSocketContainer@"))));
        } finally {
            server.stop();
        }
    }
}

