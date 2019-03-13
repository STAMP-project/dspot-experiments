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
package org.eclipse.jetty.server.handler;


import java.io.IOException;
import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.LocalConnector;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class ContextHandlerCollectionTest {
    @Test
    public void testVirtualHosts() throws Exception {
        Server server = new Server();
        LocalConnector connector0 = new LocalConnector(server);
        LocalConnector connector1 = new LocalConnector(server);
        connector1.setName("connector1");
        server.setConnectors(new Connector[]{ connector0, connector1 });
        ContextHandler contextA = new ContextHandler("/ctx");
        contextA.setVirtualHosts(new String[]{ "www.example.com", "alias.example.com" });
        ContextHandlerCollectionTest.IsHandledHandler handlerA = new ContextHandlerCollectionTest.IsHandledHandler("A");
        contextA.setHandler(handlerA);
        contextA.setAllowNullPathInfo(true);
        ContextHandler contextB = new ContextHandler("/ctx");
        ContextHandlerCollectionTest.IsHandledHandler handlerB = new ContextHandlerCollectionTest.IsHandledHandler("B");
        contextB.setHandler(handlerB);
        contextB.setVirtualHosts(new String[]{ "*.other.com", "@connector1" });
        ContextHandler contextC = new ContextHandler("/ctx");
        ContextHandlerCollectionTest.IsHandledHandler handlerC = new ContextHandlerCollectionTest.IsHandledHandler("C");
        contextC.setHandler(handlerC);
        ContextHandler contextD = new ContextHandler("/");
        ContextHandlerCollectionTest.IsHandledHandler handlerD = new ContextHandlerCollectionTest.IsHandledHandler("D");
        contextD.setHandler(handlerD);
        ContextHandler contextE = new ContextHandler("/ctx/foo");
        ContextHandlerCollectionTest.IsHandledHandler handlerE = new ContextHandlerCollectionTest.IsHandledHandler("E");
        contextE.setHandler(handlerE);
        ContextHandler contextF = new ContextHandler("/ctxlong");
        ContextHandlerCollectionTest.IsHandledHandler handlerF = new ContextHandlerCollectionTest.IsHandledHandler("F");
        contextF.setHandler(handlerF);
        ContextHandlerCollection c = new ContextHandlerCollection();
        c.addHandler(contextA);
        c.addHandler(contextB);
        c.addHandler(contextC);
        HandlerList list = new HandlerList();
        list.addHandler(contextE);
        list.addHandler(contextF);
        list.addHandler(contextD);
        c.addHandler(list);
        server.setHandler(c);
        try {
            server.start();
            Object[][] tests = new Object[][]{ new Object[]{ connector0, "www.example.com.", "/ctx", handlerA }, new Object[]{ connector0, "www.example.com.", "/ctx/", handlerA }, new Object[]{ connector0, "www.example.com.", "/ctx/info", handlerA }, new Object[]{ connector0, "www.example.com", "/ctx/info", handlerA }, new Object[]{ connector0, "alias.example.com", "/ctx/info", handlerA }, new Object[]{ connector1, "www.example.com.", "/ctx/info", handlerA }, new Object[]{ connector1, "www.example.com", "/ctx/info", handlerA }, new Object[]{ connector1, "alias.example.com", "/ctx/info", handlerA }, new Object[]{ connector1, "www.other.com", "/ctx", null }, new Object[]{ connector1, "www.other.com", "/ctx/", handlerB }, new Object[]{ connector1, "www.other.com", "/ctx/info", handlerB }, new Object[]{ connector0, "www.other.com", "/ctx/info", handlerC }, new Object[]{ connector0, "www.example.com", "/ctxinfo", handlerD }, new Object[]{ connector1, "unknown.com", "/unknown", handlerD }, new Object[]{ connector0, "alias.example.com", "/ctx/foo/info", handlerE }, new Object[]{ connector0, "alias.example.com", "/ctxlong/info", handlerF } };
            for (int i = 0; i < (tests.length); i++) {
                Object[] test = tests[i];
                LocalConnector connector = ((LocalConnector) (test[0]));
                String host = ((String) (test[1]));
                String uri = ((String) (test[2]));
                ContextHandlerCollectionTest.IsHandledHandler handler = ((ContextHandlerCollectionTest.IsHandledHandler) (test[3]));
                handlerA.reset();
                handlerB.reset();
                handlerC.reset();
                handlerD.reset();
                handlerE.reset();
                handlerF.reset();
                String t = String.format("test   %d %s@%s --> %s | %s%n", i, uri, host, connector.getName(), handler);
                String response = connector.getResponse((((("GET " + uri) + " HTTP/1.0\nHost: ") + host) + "\n\n"));
                if (handler == null) {
                    MatcherAssert.assertThat(t, response, Matchers.containsString(" 302 "));
                } else {
                    MatcherAssert.assertThat(t, response, Matchers.endsWith(handler.toString()));
                    if (!(handler.isHandled())) {
                        Assertions.fail(((("FAILED " + t) + "\n") + response));
                    }
                }
            }
        } finally {
            server.stop();
        }
    }

    @Test
    public void testVirtualHostWildcard() throws Exception {
        Server server = new Server();
        LocalConnector connector = new LocalConnector(server);
        server.setConnectors(new Connector[]{ connector });
        ContextHandler context = new ContextHandler("/");
        ContextHandlerCollectionTest.IsHandledHandler handler = new ContextHandlerCollectionTest.IsHandledHandler("H");
        context.setHandler(handler);
        ContextHandlerCollection c = new ContextHandlerCollection();
        c.addHandler(context);
        server.setHandler(c);
        try {
            server.start();
            checkWildcardHost(true, server, null, new String[]{ "example.com", ".example.com", "vhost.example.com" });
            checkWildcardHost(false, server, new String[]{ null }, new String[]{ "example.com", ".example.com", "vhost.example.com" });
            checkWildcardHost(true, server, new String[]{ "example.com", "*.example.com" }, new String[]{ "example.com", ".example.com", "vhost.example.com" });
            checkWildcardHost(false, server, new String[]{ "example.com", "*.example.com" }, new String[]{ "badexample.com", ".badexample.com", "vhost.badexample.com" });
            checkWildcardHost(false, server, new String[]{ "*." }, new String[]{ "anything.anything" });
            checkWildcardHost(true, server, new String[]{ "*.example.com" }, new String[]{ "vhost.example.com", ".example.com" });
            checkWildcardHost(false, server, new String[]{ "*.example.com" }, new String[]{ "vhost.www.example.com", "example.com", "www.vhost.example.com" });
            checkWildcardHost(true, server, new String[]{ "*.sub.example.com" }, new String[]{ "vhost.sub.example.com", ".sub.example.com" });
            checkWildcardHost(false, server, new String[]{ "*.sub.example.com" }, new String[]{ ".example.com", "sub.example.com", "vhost.example.com" });
            checkWildcardHost(false, server, new String[]{ "example.*.com", "example.com.*" }, new String[]{ "example.vhost.com", "example.com.vhost", "example.com" });
        } finally {
            server.stop();
        }
    }

    @Test
    public void testFindContainer() throws Exception {
        Server server = new Server();
        ContextHandler contextA = new ContextHandler("/a");
        ContextHandlerCollectionTest.IsHandledHandler handlerA = new ContextHandlerCollectionTest.IsHandledHandler("A");
        contextA.setHandler(handlerA);
        ContextHandler contextB = new ContextHandler("/b");
        ContextHandlerCollectionTest.IsHandledHandler handlerB = new ContextHandlerCollectionTest.IsHandledHandler("B");
        HandlerWrapper wrapperB = new HandlerWrapper();
        wrapperB.setHandler(handlerB);
        contextB.setHandler(wrapperB);
        ContextHandler contextC = new ContextHandler("/c");
        ContextHandlerCollectionTest.IsHandledHandler handlerC = new ContextHandlerCollectionTest.IsHandledHandler("C");
        contextC.setHandler(handlerC);
        ContextHandlerCollection collection = new ContextHandlerCollection();
        collection.addHandler(contextA);
        collection.addHandler(contextB);
        collection.addHandler(contextC);
        HandlerWrapper wrapper = new HandlerWrapper();
        wrapper.setHandler(collection);
        server.setHandler(wrapper);
        Assertions.assertEquals(wrapper, AbstractHandlerContainer.findContainerOf(server, HandlerWrapper.class, handlerA));
        Assertions.assertEquals(contextA, AbstractHandlerContainer.findContainerOf(server, ContextHandler.class, handlerA));
        Assertions.assertEquals(contextB, AbstractHandlerContainer.findContainerOf(server, ContextHandler.class, handlerB));
        Assertions.assertEquals(wrapper, AbstractHandlerContainer.findContainerOf(server, HandlerWrapper.class, handlerB));
        Assertions.assertEquals(contextB, AbstractHandlerContainer.findContainerOf(collection, HandlerWrapper.class, handlerB));
        Assertions.assertEquals(wrapperB, AbstractHandlerContainer.findContainerOf(contextB, HandlerWrapper.class, handlerB));
    }

    @Test
    public void testWrappedContext() throws Exception {
        Server server = new Server();
        LocalConnector connector = new LocalConnector(server);
        server.setConnectors(new Connector[]{ connector });
        ContextHandler root = new ContextHandler("/");
        root.setHandler(new ContextHandlerCollectionTest.IsHandledHandler("root"));
        ContextHandler left = new ContextHandler("/left");
        left.setHandler(new ContextHandlerCollectionTest.IsHandledHandler("left"));
        HandlerList centre = new HandlerList();
        ContextHandler centreLeft = new ContextHandler("/leftcentre");
        centreLeft.setHandler(new ContextHandlerCollectionTest.IsHandledHandler("left of centre"));
        ContextHandler centreRight = new ContextHandler("/rightcentre");
        centreRight.setHandler(new ContextHandlerCollectionTest.IsHandledHandler("right of centre"));
        centre.setHandlers(new Handler[]{ centreLeft, new ContextHandlerCollectionTest.WrappedHandler(centreRight) });
        ContextHandler right = new ContextHandler("/right");
        right.setHandler(new ContextHandlerCollectionTest.IsHandledHandler("right"));
        ContextHandlerCollection contexts = new ContextHandlerCollection();
        contexts.setHandlers(new Handler[]{ root, left, centre, new ContextHandlerCollectionTest.WrappedHandler(right) });
        server.setHandler(contexts);
        server.start();
        String response = connector.getResponse("GET / HTTP/1.0\r\n\r\n");
        MatcherAssert.assertThat(response, Matchers.startsWith("HTTP/1.1 200 OK"));
        MatcherAssert.assertThat(response, Matchers.endsWith("root"));
        MatcherAssert.assertThat(response, Matchers.not(Matchers.containsString("Wrapped: TRUE")));
        response = connector.getResponse("GET /foobar/info HTTP/1.0\r\n\r\n");
        MatcherAssert.assertThat(response, Matchers.startsWith("HTTP/1.1 200 OK"));
        MatcherAssert.assertThat(response, Matchers.endsWith("root"));
        MatcherAssert.assertThat(response, Matchers.not(Matchers.containsString("Wrapped: TRUE")));
        response = connector.getResponse("GET /left/info HTTP/1.0\r\n\r\n");
        MatcherAssert.assertThat(response, Matchers.startsWith("HTTP/1.1 200 OK"));
        MatcherAssert.assertThat(response, Matchers.endsWith("left"));
        MatcherAssert.assertThat(response, Matchers.not(Matchers.containsString("Wrapped: TRUE")));
        response = connector.getResponse("GET /leftcentre/info HTTP/1.0\r\n\r\n");
        MatcherAssert.assertThat(response, Matchers.startsWith("HTTP/1.1 200 OK"));
        MatcherAssert.assertThat(response, Matchers.endsWith("left of centre"));
        MatcherAssert.assertThat(response, Matchers.not(Matchers.containsString("Wrapped: TRUE")));
        response = connector.getResponse("GET /rightcentre/info HTTP/1.0\r\n\r\n");
        MatcherAssert.assertThat(response, Matchers.startsWith("HTTP/1.1 200 OK"));
        MatcherAssert.assertThat(response, Matchers.endsWith("right of centre"));
        MatcherAssert.assertThat(response, Matchers.containsString("Wrapped: TRUE"));
        response = connector.getResponse("GET /right/info HTTP/1.0\r\n\r\n");
        MatcherAssert.assertThat(response, Matchers.startsWith("HTTP/1.1 200 OK"));
        MatcherAssert.assertThat(response, Matchers.endsWith("right"));
        MatcherAssert.assertThat(response, Matchers.containsString("Wrapped: TRUE"));
    }

    @Test
    public void testAsyncWrappedContext() throws Exception {
        Server server = new Server();
        LocalConnector connector = new LocalConnector(server);
        server.setConnectors(new Connector[]{ connector });
        ContextHandler root = new ContextHandler("/");
        root.setHandler(new ContextHandlerCollectionTest.AsyncHandler("root"));
        ContextHandler left = new ContextHandler("/left");
        left.setHandler(new ContextHandlerCollectionTest.AsyncHandler("left"));
        HandlerList centre = new HandlerList();
        ContextHandler centreLeft = new ContextHandler("/leftcentre");
        centreLeft.setHandler(new ContextHandlerCollectionTest.AsyncHandler("left of centre"));
        ContextHandler centreRight = new ContextHandler("/rightcentre");
        centreRight.setHandler(new ContextHandlerCollectionTest.AsyncHandler("right of centre"));
        centre.setHandlers(new Handler[]{ centreLeft, new ContextHandlerCollectionTest.WrappedHandler(centreRight) });
        ContextHandler right = new ContextHandler("/right");
        right.setHandler(new ContextHandlerCollectionTest.AsyncHandler("right"));
        ContextHandlerCollection contexts = new ContextHandlerCollection();
        contexts.setHandlers(new Handler[]{ root, left, centre, new ContextHandlerCollectionTest.WrappedHandler(right) });
        server.setHandler(contexts);
        server.start();
        String response = connector.getResponse("GET / HTTP/1.0\r\n\r\n");
        MatcherAssert.assertThat(response, Matchers.startsWith("HTTP/1.1 200 OK"));
        MatcherAssert.assertThat(response, Matchers.endsWith("root"));
        MatcherAssert.assertThat(response, Matchers.not(Matchers.containsString("Wrapped: TRUE")));
        response = connector.getResponse("GET /foobar/info HTTP/1.0\r\n\r\n");
        MatcherAssert.assertThat(response, Matchers.startsWith("HTTP/1.1 200 OK"));
        MatcherAssert.assertThat(response, Matchers.endsWith("root"));
        MatcherAssert.assertThat(response, Matchers.not(Matchers.containsString("Wrapped: TRUE")));
        response = connector.getResponse("GET /left/info HTTP/1.0\r\n\r\n");
        MatcherAssert.assertThat(response, Matchers.startsWith("HTTP/1.1 200 OK"));
        MatcherAssert.assertThat(response, Matchers.endsWith("left"));
        MatcherAssert.assertThat(response, Matchers.not(Matchers.containsString("Wrapped: TRUE")));
        response = connector.getResponse("GET /leftcentre/info HTTP/1.0\r\n\r\n");
        MatcherAssert.assertThat(response, Matchers.startsWith("HTTP/1.1 200 OK"));
        MatcherAssert.assertThat(response, Matchers.endsWith("left of centre"));
        MatcherAssert.assertThat(response, Matchers.not(Matchers.containsString("Wrapped: TRUE")));
        response = connector.getResponse("GET /rightcentre/info HTTP/1.0\r\n\r\n");
        MatcherAssert.assertThat(response, Matchers.startsWith("HTTP/1.1 200 OK"));
        MatcherAssert.assertThat(response, Matchers.endsWith("right of centre"));
        MatcherAssert.assertThat(response, Matchers.containsString("Wrapped: ASYNC"));
        response = connector.getResponse("GET /right/info HTTP/1.0\r\n\r\n");
        MatcherAssert.assertThat(response, Matchers.startsWith("HTTP/1.1 200 OK"));
        MatcherAssert.assertThat(response, Matchers.endsWith("right"));
        MatcherAssert.assertThat(response, Matchers.containsString("Wrapped: ASYNC"));
    }

    private static final class WrappedHandler extends HandlerWrapper {
        WrappedHandler(Handler handler) {
            setHandler(handler);
        }

        @Override
        public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            if (response.containsHeader("Wrapped"))
                response.setHeader("Wrapped", "ASYNC");
            else
                response.setHeader("Wrapped", "TRUE");

            super.handle(target, baseRequest, request, response);
        }
    }

    private static final class IsHandledHandler extends AbstractHandler {
        private boolean handled;

        private final String name;

        public IsHandledHandler(String string) {
            name = string;
        }

        public boolean isHandled() {
            return handled;
        }

        @Override
        public void handle(String s, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            baseRequest.setHandled(true);
            this.handled = true;
            response.getWriter().print(name);
        }

        public void reset() {
            handled = false;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private static final class AsyncHandler extends AbstractHandler {
        private final String name;

        public AsyncHandler(String string) {
            name = string;
        }

        @Override
        public void handle(String s, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            baseRequest.setHandled(true);
            String n = ((String) (baseRequest.getAttribute("async")));
            if (n == null) {
                AsyncContext async = baseRequest.startAsync();
                async.setTimeout(1000);
                baseRequest.setAttribute("async", name);
                async.dispatch();
            } else {
                response.getWriter().print(n);
            }
        }

        @Override
        public String toString() {
            return name;
        }
    }
}

