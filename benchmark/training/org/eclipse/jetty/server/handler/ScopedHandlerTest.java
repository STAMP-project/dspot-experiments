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
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.server.Server;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class ScopedHandlerTest {
    private StringBuilder _history = new StringBuilder();

    @Test
    public void testSingle() throws Exception {
        ScopedHandlerTest.TestHandler handler0 = new ScopedHandlerTest.TestHandler("0");
        handler0.setServer(new Server());
        start();
        handle("target", null, null, null);
        stop();
        String history = _history.toString();
        Assertions.assertEquals(">S0>W0<W0<S0", history);
    }

    @Test
    public void testSimpleDouble() throws Exception {
        ScopedHandlerTest.TestHandler handler0 = new ScopedHandlerTest.TestHandler("0");
        ScopedHandlerTest.TestHandler handler1 = new ScopedHandlerTest.TestHandler("1");
        handler0.setServer(new Server());
        handler1.setServer(getServer());
        handler0.setHandler(handler1);
        start();
        handle("target", null, null, null);
        stop();
        String history = _history.toString();
        Assertions.assertEquals(">S0>S1>W0>W1<W1<W0<S1<S0", history);
    }

    @Test
    public void testSimpleTriple() throws Exception {
        ScopedHandlerTest.TestHandler handler0 = new ScopedHandlerTest.TestHandler("0");
        ScopedHandlerTest.TestHandler handler1 = new ScopedHandlerTest.TestHandler("1");
        ScopedHandlerTest.TestHandler handler2 = new ScopedHandlerTest.TestHandler("2");
        handler0.setServer(new Server());
        handler1.setServer(getServer());
        handler2.setServer(getServer());
        handler0.setHandler(handler1);
        handler1.setHandler(handler2);
        start();
        handle("target", null, null, null);
        stop();
        String history = _history.toString();
        Assertions.assertEquals(">S0>S1>S2>W0>W1>W2<W2<W1<W0<S2<S1<S0", history);
    }

    @Test
    public void testDouble() throws Exception {
        Request request = new Request(null, null);
        Response response = new Response(null, null);
        ScopedHandlerTest.TestHandler handler0 = new ScopedHandlerTest.TestHandler("0");
        ScopedHandlerTest.OtherHandler handlerA = new ScopedHandlerTest.OtherHandler("A");
        ScopedHandlerTest.TestHandler handler1 = new ScopedHandlerTest.TestHandler("1");
        ScopedHandlerTest.OtherHandler handlerB = new ScopedHandlerTest.OtherHandler("B");
        handler0.setServer(new Server());
        handlerA.setServer(getServer());
        handler1.setServer(getServer());
        handlerB.setServer(getServer());
        setHandler(handlerA);
        handlerA.setHandler(handler1);
        setHandler(handlerB);
        start();
        handler0.handle("target", request, request, response);
        stop();
        String history = _history.toString();
        Assertions.assertEquals(">S0>S1>W0>HA>W1>HB<HB<W1<HA<W0<S1<S0", history);
    }

    @Test
    public void testTriple() throws Exception {
        Request request = new Request(null, null);
        Response response = new Response(null, null);
        ScopedHandlerTest.TestHandler handler0 = new ScopedHandlerTest.TestHandler("0");
        ScopedHandlerTest.OtherHandler handlerA = new ScopedHandlerTest.OtherHandler("A");
        ScopedHandlerTest.TestHandler handler1 = new ScopedHandlerTest.TestHandler("1");
        ScopedHandlerTest.OtherHandler handlerB = new ScopedHandlerTest.OtherHandler("B");
        ScopedHandlerTest.TestHandler handler2 = new ScopedHandlerTest.TestHandler("2");
        ScopedHandlerTest.OtherHandler handlerC = new ScopedHandlerTest.OtherHandler("C");
        handler0.setServer(new Server());
        handlerA.setServer(getServer());
        handler1.setServer(getServer());
        handlerB.setServer(getServer());
        handler2.setServer(getServer());
        handlerC.setServer(getServer());
        setHandler(handlerA);
        handlerA.setHandler(handler1);
        setHandler(handlerB);
        handlerB.setHandler(handler2);
        setHandler(handlerC);
        start();
        handler0.handle("target", request, request, response);
        stop();
        String history = _history.toString();
        Assertions.assertEquals(">S0>S1>S2>W0>HA>W1>HB>W2>HC<HC<W2<HB<W1<HA<W0<S2<S1<S0", history);
    }

    private class TestHandler extends ScopedHandler {
        private final String _name;

        private TestHandler(String name) {
            _name = name;
        }

        @Override
        public void doScope(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            try {
                _history.append(">S").append(_name);
                super.nextScope(target, baseRequest, request, response);
            } finally {
                _history.append("<S").append(_name);
            }
        }

        @Override
        public void doHandle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            try {
                _history.append(">W").append(_name);
                super.nextHandle(target, baseRequest, request, response);
            } finally {
                _history.append("<W").append(_name);
            }
        }
    }

    private class OtherHandler extends HandlerWrapper {
        private final String _name;

        private OtherHandler(String name) {
            _name = name;
        }

        @Override
        public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            try {
                _history.append(">H").append(_name);
                super.handle(target, baseRequest, request, response);
            } finally {
                _history.append("<H").append(_name);
            }
        }
    }
}

