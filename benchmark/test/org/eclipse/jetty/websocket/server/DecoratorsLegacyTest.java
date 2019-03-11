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
package org.eclipse.jetty.websocket.server;


import DecoratedObjectFactory.ATTR;
import Timeouts.CONNECT;
import Timeouts.CONNECT_UNIT;
import Timeouts.POLL_EVENT;
import Timeouts.POLL_EVENT_UNIT;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import javax.servlet.ServletContext;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.DecoratedObjectFactory;
import org.eclipse.jetty.util.Decorator;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.eclipse.jetty.websocket.common.WebSocketFrame;
import org.eclipse.jetty.websocket.common.frames.TextFrame;
import org.eclipse.jetty.websocket.common.test.BlockheadClient;
import org.eclipse.jetty.websocket.common.test.BlockheadClientRequest;
import org.eclipse.jetty.websocket.common.test.BlockheadConnection;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeRequest;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeResponse;
import org.eclipse.jetty.websocket.servlet.WebSocketCreator;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;


@Disabled("Unstable - see Issue #1815")
public class DecoratorsLegacyTest {
    private static class DecoratorsSocket extends WebSocketAdapter {
        private final DecoratedObjectFactory objFactory;

        public DecoratorsSocket(DecoratedObjectFactory objFactory) {
            this.objFactory = objFactory;
        }

        @Override
        public void onWebSocketText(String message) {
            StringWriter str = new StringWriter();
            PrintWriter out = new PrintWriter(str);
            if ((objFactory) != null) {
                out.printf("Object is a DecoratedObjectFactory%n");
                List<Decorator> decorators = objFactory.getDecorators();
                out.printf("Decorators.size = [%d]%n", decorators.size());
                for (Decorator decorator : decorators) {
                    out.printf(" decorator[] = %s%n", decorator.getClass().getName());
                }
            } else {
                out.printf("DecoratedObjectFactory is NULL%n");
            }
            getRemote().sendStringByFuture(str.toString());
        }
    }

    private static class DecoratorsCreator implements WebSocketCreator {
        @Override
        public Object createWebSocket(ServletUpgradeRequest req, ServletUpgradeResponse resp) {
            ServletContext servletContext = req.getHttpServletRequest().getServletContext();
            DecoratedObjectFactory objFactory = ((DecoratedObjectFactory) (servletContext.getAttribute(ATTR)));
            return new DecoratorsLegacyTest.DecoratorsSocket(objFactory);
        }
    }

    public static class DecoratorsRequestServlet extends WebSocketServlet {
        private static final long serialVersionUID = 1L;

        private final WebSocketCreator creator;

        public DecoratorsRequestServlet(WebSocketCreator creator) {
            this.creator = creator;
        }

        @Override
        public void configure(WebSocketServletFactory factory) {
            factory.setCreator(this.creator);
        }
    }

    @SuppressWarnings("deprecation")
    private static class DummyLegacyDecorator implements ServletContextHandler.Decorator {
        @Override
        public <T> T decorate(T o) {
            return o;
        }

        @Override
        public void destroy(Object o) {
        }
    }

    private static BlockheadClient client;

    private static SimpleServletServer server;

    private static DecoratorsLegacyTest.DecoratorsCreator decoratorsCreator;

    @Test
    public void testAccessRequestCookies() throws Exception {
        BlockheadClientRequest request = DecoratorsLegacyTest.client.newWsRequest(DecoratorsLegacyTest.server.getServerUri());
        request.idleTimeout(1, TimeUnit.SECONDS);
        Future<BlockheadConnection> connFut = request.sendAsync();
        try (BlockheadConnection clientConn = connFut.get(CONNECT, CONNECT_UNIT)) {
            clientConn.write(new TextFrame().setPayload("info"));
            LinkedBlockingQueue<WebSocketFrame> frames = clientConn.getFrameQueue();
            WebSocketFrame resp = frames.poll(POLL_EVENT, POLL_EVENT_UNIT);
            String textMsg = resp.getPayloadAsUTF8();
            MatcherAssert.assertThat("DecoratedObjectFactory", textMsg, Matchers.containsString("Object is a DecoratedObjectFactory"));
            MatcherAssert.assertThat("decorators.size", textMsg, Matchers.containsString("Decorators.size = [1]"));
            MatcherAssert.assertThat("decorator type", textMsg, Matchers.containsString(("decorator[] = " + (DecoratorsLegacyTest.DummyLegacyDecorator.class.getName()))));
        }
    }
}

