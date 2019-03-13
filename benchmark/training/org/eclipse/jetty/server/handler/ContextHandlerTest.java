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
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.LocalConnector;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class ContextHandlerTest {
    @Test
    public void testGetResourcePathsWhenSuppliedPathEndsInSlash() throws Exception {
        checkResourcePathsForExampleWebApp("/WEB-INF/");
    }

    @Test
    public void testGetResourcePathsWhenSuppliedPathDoesNotEndInSlash() throws Exception {
        checkResourcePathsForExampleWebApp("/WEB-INF");
    }

    @Test
    public void testVirtualHostNormalization() throws Exception {
        Server server = new Server();
        LocalConnector connector = new LocalConnector(server);
        server.setConnectors(new Connector[]{ connector });
        ContextHandler contextA = new ContextHandler("/");
        contextA.setVirtualHosts(new String[]{ "www.example.com" });
        ContextHandlerTest.IsHandledHandler handlerA = new ContextHandlerTest.IsHandledHandler();
        contextA.setHandler(handlerA);
        ContextHandler contextB = new ContextHandler("/");
        ContextHandlerTest.IsHandledHandler handlerB = new ContextHandlerTest.IsHandledHandler();
        contextB.setHandler(handlerB);
        contextB.setVirtualHosts(new String[]{ "www.example2.com." });
        ContextHandler contextC = new ContextHandler("/");
        ContextHandlerTest.IsHandledHandler handlerC = new ContextHandlerTest.IsHandledHandler();
        contextC.setHandler(handlerC);
        HandlerCollection c = new HandlerCollection();
        c.addHandler(contextA);
        c.addHandler(contextB);
        c.addHandler(contextC);
        server.setHandler(c);
        try {
            server.start();
            connector.getResponse(("GET / HTTP/1.0\n" + "Host: www.example.com.\n\n"));
            Assertions.assertTrue(handlerA.isHandled());
            Assertions.assertFalse(handlerB.isHandled());
            Assertions.assertFalse(handlerC.isHandled());
            handlerA.reset();
            handlerB.reset();
            handlerC.reset();
            connector.getResponse(("GET / HTTP/1.0\n" + "Host: www.example2.com\n\n"));
            Assertions.assertFalse(handlerA.isHandled());
            Assertions.assertTrue(handlerB.isHandled());
            Assertions.assertFalse(handlerC.isHandled());
        } finally {
            server.stop();
        }
    }

    @Test
    public void testNamedConnector() throws Exception {
        Server server = new Server();
        LocalConnector connector = new LocalConnector(server);
        LocalConnector connectorN = new LocalConnector(server);
        connectorN.setName("name");
        server.setConnectors(new Connector[]{ connector, connectorN });
        ContextHandler contextA = new ContextHandler("/");
        contextA.setDisplayName("A");
        contextA.setVirtualHosts(new String[]{ "www.example.com" });
        ContextHandlerTest.IsHandledHandler handlerA = new ContextHandlerTest.IsHandledHandler();
        contextA.setHandler(handlerA);
        ContextHandler contextB = new ContextHandler("/");
        contextB.setDisplayName("B");
        ContextHandlerTest.IsHandledHandler handlerB = new ContextHandlerTest.IsHandledHandler();
        contextB.setHandler(handlerB);
        contextB.setVirtualHosts(new String[]{ "@name" });
        ContextHandler contextC = new ContextHandler("/");
        contextC.setDisplayName("C");
        ContextHandlerTest.IsHandledHandler handlerC = new ContextHandlerTest.IsHandledHandler();
        contextC.setHandler(handlerC);
        ContextHandler contextD = new ContextHandler("/");
        contextD.setDisplayName("D");
        ContextHandlerTest.IsHandledHandler handlerD = new ContextHandlerTest.IsHandledHandler();
        contextD.setHandler(handlerD);
        contextD.setVirtualHosts(new String[]{ "www.example.com@name" });
        ContextHandler contextE = new ContextHandler("/");
        contextE.setDisplayName("E");
        ContextHandlerTest.IsHandledHandler handlerE = new ContextHandlerTest.IsHandledHandler();
        contextE.setHandler(handlerE);
        contextE.setVirtualHosts(new String[]{ "*.example.com" });
        ContextHandler contextF = new ContextHandler("/");
        contextF.setDisplayName("F");
        ContextHandlerTest.IsHandledHandler handlerF = new ContextHandlerTest.IsHandledHandler();
        contextF.setHandler(handlerF);
        contextF.setVirtualHosts(new String[]{ "*.example.com@name" });
        ContextHandler contextG = new ContextHandler("/");
        contextG.setDisplayName("G");
        ContextHandlerTest.IsHandledHandler handlerG = new ContextHandlerTest.IsHandledHandler();
        contextG.setHandler(handlerG);
        contextG.setVirtualHosts(new String[]{ "*.com@name" });
        ContextHandler contextH = new ContextHandler("/");
        contextH.setDisplayName("H");
        ContextHandlerTest.IsHandledHandler handlerH = new ContextHandlerTest.IsHandledHandler();
        contextH.setHandler(handlerH);
        contextH.setVirtualHosts(new String[]{ "*.com" });
        HandlerCollection c = new HandlerCollection();
        c.addHandler(contextA);
        c.addHandler(contextB);
        c.addHandler(contextC);
        c.addHandler(contextD);
        c.addHandler(contextE);
        c.addHandler(contextF);
        c.addHandler(contextG);
        c.addHandler(contextH);
        server.setHandler(c);
        server.start();
        try {
            connector.getResponse(("GET / HTTP/1.0\n" + "Host: www.example.com.\n\n"));
            Assertions.assertTrue(handlerA.isHandled());
            Assertions.assertFalse(handlerB.isHandled());
            Assertions.assertFalse(handlerC.isHandled());
            Assertions.assertFalse(handlerD.isHandled());
            Assertions.assertFalse(handlerE.isHandled());
            Assertions.assertFalse(handlerF.isHandled());
            Assertions.assertFalse(handlerG.isHandled());
            Assertions.assertFalse(handlerH.isHandled());
            handlerA.reset();
            handlerB.reset();
            handlerC.reset();
            handlerD.reset();
            handlerE.reset();
            handlerF.reset();
            handlerG.reset();
            handlerH.reset();
            connector.getResponse(("GET / HTTP/1.0\n" + "Host: localhost\n\n"));
            Assertions.assertFalse(handlerA.isHandled());
            Assertions.assertFalse(handlerB.isHandled());
            Assertions.assertTrue(handlerC.isHandled());
            Assertions.assertFalse(handlerD.isHandled());
            Assertions.assertFalse(handlerE.isHandled());
            Assertions.assertFalse(handlerF.isHandled());
            Assertions.assertFalse(handlerG.isHandled());
            Assertions.assertFalse(handlerH.isHandled());
            handlerA.reset();
            handlerB.reset();
            handlerC.reset();
            handlerD.reset();
            handlerE.reset();
            handlerF.reset();
            handlerG.reset();
            handlerH.reset();
            connectorN.getResponse(("GET / HTTP/1.0\n" + "Host: www.example.com.\n\n"));
            Assertions.assertTrue(handlerA.isHandled());
            Assertions.assertFalse(handlerB.isHandled());
            Assertions.assertFalse(handlerC.isHandled());
            Assertions.assertFalse(handlerD.isHandled());
            Assertions.assertFalse(handlerE.isHandled());
            Assertions.assertFalse(handlerF.isHandled());
            Assertions.assertFalse(handlerG.isHandled());
            Assertions.assertFalse(handlerH.isHandled());
            handlerA.reset();
            handlerB.reset();
            handlerC.reset();
            handlerD.reset();
            handlerE.reset();
            handlerF.reset();
            handlerG.reset();
            handlerH.reset();
            connectorN.getResponse(("GET / HTTP/1.0\n" + "Host: localhost\n\n"));
            Assertions.assertFalse(handlerA.isHandled());
            Assertions.assertTrue(handlerB.isHandled());
            Assertions.assertFalse(handlerC.isHandled());
            Assertions.assertFalse(handlerD.isHandled());
            Assertions.assertFalse(handlerE.isHandled());
            Assertions.assertFalse(handlerF.isHandled());
            Assertions.assertFalse(handlerG.isHandled());
            Assertions.assertFalse(handlerH.isHandled());
            handlerA.reset();
            handlerB.reset();
            handlerC.reset();
            handlerD.reset();
            handlerE.reset();
            handlerF.reset();
            handlerG.reset();
            handlerH.reset();
        } finally {
            server.stop();
        }
        // Reversed order to check priority when multiple matches
        HandlerCollection d = new HandlerCollection();
        d.addHandler(contextH);
        d.addHandler(contextG);
        d.addHandler(contextF);
        d.addHandler(contextE);
        d.addHandler(contextD);
        d.addHandler(contextC);
        d.addHandler(contextB);
        d.addHandler(contextA);
        server.setHandler(d);
        server.start();
        try {
            connector.getResponse(("GET / HTTP/1.0\n" + "Host: www.example.com.\n\n"));
            Assertions.assertFalse(handlerA.isHandled());
            Assertions.assertFalse(handlerB.isHandled());
            Assertions.assertFalse(handlerC.isHandled());
            Assertions.assertFalse(handlerD.isHandled());
            Assertions.assertTrue(handlerE.isHandled());
            Assertions.assertFalse(handlerF.isHandled());
            Assertions.assertFalse(handlerG.isHandled());
            Assertions.assertFalse(handlerH.isHandled());
            handlerA.reset();
            handlerB.reset();
            handlerC.reset();
            handlerD.reset();
            handlerE.reset();
            handlerF.reset();
            handlerG.reset();
            handlerH.reset();
            connector.getResponse(("GET / HTTP/1.0\n" + "Host: localhost\n\n"));
            Assertions.assertFalse(handlerA.isHandled());
            Assertions.assertFalse(handlerB.isHandled());
            Assertions.assertTrue(handlerC.isHandled());
            Assertions.assertFalse(handlerD.isHandled());
            Assertions.assertFalse(handlerE.isHandled());
            Assertions.assertFalse(handlerF.isHandled());
            Assertions.assertFalse(handlerG.isHandled());
            Assertions.assertFalse(handlerH.isHandled());
            handlerA.reset();
            handlerB.reset();
            handlerC.reset();
            handlerD.reset();
            handlerE.reset();
            handlerF.reset();
            handlerG.reset();
            handlerH.reset();
            connectorN.getResponse(("GET / HTTP/1.0\n" + "Host: www.example.com.\n\n"));
            Assertions.assertFalse(handlerA.isHandled());
            Assertions.assertFalse(handlerB.isHandled());
            Assertions.assertFalse(handlerC.isHandled());
            Assertions.assertFalse(handlerD.isHandled());
            Assertions.assertFalse(handlerE.isHandled());
            Assertions.assertTrue(handlerF.isHandled());
            Assertions.assertFalse(handlerG.isHandled());
            Assertions.assertFalse(handlerH.isHandled());
            handlerA.reset();
            handlerB.reset();
            handlerC.reset();
            handlerD.reset();
            handlerE.reset();
            handlerF.reset();
            handlerG.reset();
            handlerH.reset();
            connectorN.getResponse(("GET / HTTP/1.0\n" + "Host: localhost\n\n"));
            Assertions.assertFalse(handlerA.isHandled());
            Assertions.assertFalse(handlerB.isHandled());
            Assertions.assertTrue(handlerC.isHandled());
            Assertions.assertFalse(handlerD.isHandled());
            Assertions.assertFalse(handlerE.isHandled());
            Assertions.assertFalse(handlerF.isHandled());
            Assertions.assertFalse(handlerG.isHandled());
            Assertions.assertFalse(handlerH.isHandled());
            handlerA.reset();
            handlerB.reset();
            handlerC.reset();
            handlerD.reset();
            handlerE.reset();
            handlerF.reset();
            handlerG.reset();
            handlerH.reset();
        } finally {
            server.stop();
        }
    }

    @Test
    public void testContextGetContext() throws Exception {
        Server server = new Server();
        LocalConnector connector = new LocalConnector(server);
        server.setConnectors(new Connector[]{ connector });
        ContextHandlerCollection contexts = new ContextHandlerCollection();
        server.setHandler(contexts);
        ContextHandler rootA = new ContextHandler(contexts, "/");
        ContextHandler fooA = new ContextHandler(contexts, "/foo");
        ContextHandler foobarA = new ContextHandler(contexts, "/foo/bar");
        server.start();
        // System.err.println(server.dump());
        Assertions.assertEquals(rootA._scontext, rootA._scontext.getContext("/"));
        Assertions.assertEquals(fooA._scontext, rootA._scontext.getContext("/foo"));
        Assertions.assertEquals(foobarA._scontext, rootA._scontext.getContext("/foo/bar"));
        Assertions.assertEquals(foobarA._scontext, rootA._scontext.getContext("/foo/bar/bob.jsp"));
        Assertions.assertEquals(rootA._scontext, rootA._scontext.getContext("/other"));
        Assertions.assertEquals(fooA._scontext, rootA._scontext.getContext("/foo/other"));
        Assertions.assertEquals(rootA._scontext, foobarA._scontext.getContext("/"));
        Assertions.assertEquals(fooA._scontext, foobarA._scontext.getContext("/foo"));
        Assertions.assertEquals(foobarA._scontext, foobarA._scontext.getContext("/foo/bar"));
        Assertions.assertEquals(foobarA._scontext, foobarA._scontext.getContext("/foo/bar/bob.jsp"));
        Assertions.assertEquals(rootA._scontext, foobarA._scontext.getContext("/other"));
        Assertions.assertEquals(fooA._scontext, foobarA._scontext.getContext("/foo/other"));
    }

    @Test
    public void testLifeCycle() throws Exception {
        Server server = new Server();
        LocalConnector connector = new LocalConnector(server);
        server.setConnectors(new Connector[]{ connector });
        ContextHandlerCollection contexts = new ContextHandlerCollection();
        server.setHandler(contexts);
        ContextHandler root = new ContextHandler(contexts, "/");
        root.setHandler(new ContextHandlerTest.ContextPathHandler());
        ContextHandler foo = new ContextHandler(contexts, "/foo");
        foo.setHandler(new ContextHandlerTest.ContextPathHandler());
        ContextHandler foobar = new ContextHandler(contexts, "/foo/bar");
        foobar.setHandler(new ContextHandlerTest.ContextPathHandler());
        // check that all contexts start normally
        server.start();
        MatcherAssert.assertThat(connector.getResponse("GET / HTTP/1.0\n\n"), Matchers.containsString("ctx=''"));
        MatcherAssert.assertThat(connector.getResponse("GET /foo/xxx HTTP/1.0\n\n"), Matchers.containsString("ctx='/foo'"));
        MatcherAssert.assertThat(connector.getResponse("GET /foo/bar/xxx HTTP/1.0\n\n"), Matchers.containsString("ctx='/foo/bar'"));
        // If we stop foobar, then requests will be handled by foo
        foobar.stop();
        MatcherAssert.assertThat(connector.getResponse("GET / HTTP/1.0\n\n"), Matchers.containsString("ctx=''"));
        MatcherAssert.assertThat(connector.getResponse("GET /foo/xxx HTTP/1.0\n\n"), Matchers.containsString("ctx='/foo'"));
        MatcherAssert.assertThat(connector.getResponse("GET /foo/bar/xxx HTTP/1.0\n\n"), Matchers.containsString("ctx='/foo'"));
        // If we shutdown foo then requests will be 503'd
        foo.shutdown().get();
        MatcherAssert.assertThat(connector.getResponse("GET / HTTP/1.0\n\n"), Matchers.containsString("ctx=''"));
        MatcherAssert.assertThat(connector.getResponse("GET /foo/xxx HTTP/1.0\n\n"), Matchers.containsString("503"));
        MatcherAssert.assertThat(connector.getResponse("GET /foo/bar/xxx HTTP/1.0\n\n"), Matchers.containsString("503"));
        // If we stop foo then requests will be handled by root
        foo.stop();
        MatcherAssert.assertThat(connector.getResponse("GET / HTTP/1.0\n\n"), Matchers.containsString("ctx=''"));
        MatcherAssert.assertThat(connector.getResponse("GET /foo/xxx HTTP/1.0\n\n"), Matchers.containsString("ctx=''"));
        MatcherAssert.assertThat(connector.getResponse("GET /foo/bar/xxx HTTP/1.0\n\n"), Matchers.containsString("ctx=''"));
        // If we start foo then foobar requests will be handled by foo
        foo.start();
        MatcherAssert.assertThat(connector.getResponse("GET / HTTP/1.0\n\n"), Matchers.containsString("ctx=''"));
        MatcherAssert.assertThat(connector.getResponse("GET /foo/xxx HTTP/1.0\n\n"), Matchers.containsString("ctx='/foo'"));
        MatcherAssert.assertThat(connector.getResponse("GET /foo/bar/xxx HTTP/1.0\n\n"), Matchers.containsString("ctx='/foo'"));
        // If we start foobar then foobar requests will be handled by foobar
        foobar.start();
        MatcherAssert.assertThat(connector.getResponse("GET / HTTP/1.0\n\n"), Matchers.containsString("ctx=''"));
        MatcherAssert.assertThat(connector.getResponse("GET /foo/xxx HTTP/1.0\n\n"), Matchers.containsString("ctx='/foo'"));
        MatcherAssert.assertThat(connector.getResponse("GET /foo/bar/xxx HTTP/1.0\n\n"), Matchers.containsString("ctx='/foo/bar'"));
    }

    @Test
    public void testContextVirtualGetContext() throws Exception {
        Server server = new Server();
        LocalConnector connector = new LocalConnector(server);
        server.setConnectors(new Connector[]{ connector });
        ContextHandlerCollection contexts = new ContextHandlerCollection();
        server.setHandler(contexts);
        ContextHandler rootA = new ContextHandler(contexts, "/");
        rootA.setVirtualHosts(new String[]{ "a.com" });
        ContextHandler rootB = new ContextHandler(contexts, "/");
        rootB.setVirtualHosts(new String[]{ "b.com" });
        ContextHandler rootC = new ContextHandler(contexts, "/");
        rootC.setVirtualHosts(new String[]{ "c.com" });
        ContextHandler fooA = new ContextHandler(contexts, "/foo");
        fooA.setVirtualHosts(new String[]{ "a.com" });
        ContextHandler fooB = new ContextHandler(contexts, "/foo");
        fooB.setVirtualHosts(new String[]{ "b.com" });
        ContextHandler foobarA = new ContextHandler(contexts, "/foo/bar");
        foobarA.setVirtualHosts(new String[]{ "a.com" });
        server.start();
        // System.err.println(server.dump());
        Assertions.assertEquals(rootA._scontext, rootA._scontext.getContext("/"));
        Assertions.assertEquals(fooA._scontext, rootA._scontext.getContext("/foo"));
        Assertions.assertEquals(foobarA._scontext, rootA._scontext.getContext("/foo/bar"));
        Assertions.assertEquals(foobarA._scontext, rootA._scontext.getContext("/foo/bar/bob"));
        Assertions.assertEquals(rootA._scontext, rootA._scontext.getContext("/other"));
        Assertions.assertEquals(rootB._scontext, rootB._scontext.getContext("/other"));
        Assertions.assertEquals(rootC._scontext, rootC._scontext.getContext("/other"));
        Assertions.assertEquals(fooB._scontext, rootB._scontext.getContext("/foo/other"));
        Assertions.assertEquals(rootC._scontext, rootC._scontext.getContext("/foo/other"));
    }

    @Test
    public void testVirtualHostWildcard() throws Exception {
        Server server = new Server();
        LocalConnector connector = new LocalConnector(server);
        server.setConnectors(new Connector[]{ connector });
        ContextHandler context = new ContextHandler("/");
        ContextHandlerTest.IsHandledHandler handler = new ContextHandlerTest.IsHandledHandler();
        context.setHandler(handler);
        server.setHandler(context);
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
    public void testVirtualHostManagement() throws Exception {
        ContextHandler context = new ContextHandler("/");
        // test singular
        context.setVirtualHosts(new String[]{ "www.example.com" });
        Assertions.assertEquals(1, context.getVirtualHosts().length);
        // test adding two more
        context.addVirtualHosts(new String[]{ "foo.com@connector1", "*.example2.com" });
        Assertions.assertEquals(3, context.getVirtualHosts().length);
        // test adding existing context
        context.addVirtualHosts(new String[]{ "www.example.com" });
        Assertions.assertEquals(3, context.getVirtualHosts().length);
        // test removing existing
        context.removeVirtualHosts(new String[]{ "*.example2.com" });
        Assertions.assertEquals(2, context.getVirtualHosts().length);
        // test removing non-existent
        context.removeVirtualHosts(new String[]{ "www.example3.com" });
        Assertions.assertEquals(2, context.getVirtualHosts().length);
        // test removing all remaining and resets to null
        context.removeVirtualHosts(new String[]{ "www.example.com", "foo.com@connector1" });
        Assertions.assertArrayEquals(null, context.getVirtualHosts());
    }

    @Test
    public void testAttributes() throws Exception {
        ContextHandler handler = new ContextHandler();
        handler.setServer(new Server());
        handler.setAttribute("aaa", "111");
        Assertions.assertEquals("111", handler.getServletContext().getAttribute("aaa"));
        Assertions.assertEquals(null, handler.getAttribute("bbb"));
        handler.start();
        handler.getServletContext().setAttribute("aaa", "000");
        handler.setAttribute("ccc", "333");
        handler.getServletContext().setAttribute("ddd", "444");
        Assertions.assertEquals("111", handler.getServletContext().getAttribute("aaa"));
        Assertions.assertEquals(null, handler.getServletContext().getAttribute("bbb"));
        handler.getServletContext().setAttribute("bbb", "222");
        Assertions.assertEquals("333", handler.getServletContext().getAttribute("ccc"));
        Assertions.assertEquals("444", handler.getServletContext().getAttribute("ddd"));
        Assertions.assertEquals("111", handler.getAttribute("aaa"));
        Assertions.assertEquals(null, handler.getAttribute("bbb"));
        Assertions.assertEquals("333", handler.getAttribute("ccc"));
        Assertions.assertEquals(null, handler.getAttribute("ddd"));
        handler.stop();
        Assertions.assertEquals("111", handler.getServletContext().getAttribute("aaa"));
        Assertions.assertEquals(null, handler.getServletContext().getAttribute("bbb"));
        Assertions.assertEquals("333", handler.getServletContext().getAttribute("ccc"));
        Assertions.assertEquals(null, handler.getServletContext().getAttribute("ddd"));
    }

    @Test
    public void testProtected() throws Exception {
        ContextHandler handler = new ContextHandler();
        String[] protectedTargets = new String[]{ "/foo-inf", "/bar-inf" };
        handler.setProtectedTargets(protectedTargets);
        Assertions.assertTrue(handler.isProtectedTarget("/foo-inf/x/y/z"));
        Assertions.assertFalse(handler.isProtectedTarget("/foo/x/y/z"));
        Assertions.assertTrue(handler.isProtectedTarget("/foo-inf?x=y&z=1"));
        Assertions.assertFalse(handler.isProtectedTarget("/foo-inf-bar"));
        protectedTargets = new String[4];
        System.arraycopy(handler.getProtectedTargets(), 0, protectedTargets, 0, 2);
        protectedTargets[2] = "/abc";
        protectedTargets[3] = "/def";
        handler.setProtectedTargets(protectedTargets);
        Assertions.assertTrue(handler.isProtectedTarget("/foo-inf/x/y/z"));
        Assertions.assertFalse(handler.isProtectedTarget("/foo/x/y/z"));
        Assertions.assertTrue(handler.isProtectedTarget("/foo-inf?x=y&z=1"));
        Assertions.assertTrue(handler.isProtectedTarget("/abc/124"));
        Assertions.assertTrue(handler.isProtectedTarget("//def"));
        Assertions.assertTrue(handler.isProtectedTarget("/ABC/7777"));
    }

    @Test
    public void testIsShutdown() {
        ContextHandler handler = new ContextHandler();
        Assertions.assertEquals(false, handler.isShutdown());
    }

    @Test
    public void testLogNameFromDisplayName() throws Exception {
        ContextHandler handler = new ContextHandler();
        handler.setServer(new Server());
        handler.setDisplayName("An Interesting Project: app.tast.ic");
        try {
            handler.start();
            MatcherAssert.assertThat("handler.get", handler.getLogger().getName(), CoreMatchers.is(((ContextHandler.class.getName()) + ".An_Interesting_Project__app_tast_ic")));
        } finally {
            handler.stop();
        }
    }

    @Test
    public void testLogNameFromContextPath_Deep() throws Exception {
        ContextHandler handler = new ContextHandler();
        handler.setServer(new Server());
        handler.setContextPath("/app/tast/ic");
        try {
            handler.start();
            MatcherAssert.assertThat("handler.get", handler.getLogger().getName(), CoreMatchers.is(((ContextHandler.class.getName()) + ".app_tast_ic")));
        } finally {
            handler.stop();
        }
    }

    @Test
    public void testLogNameFromContextPath_Root() throws Exception {
        ContextHandler handler = new ContextHandler();
        handler.setServer(new Server());
        handler.setContextPath("");
        try {
            handler.start();
            MatcherAssert.assertThat("handler.get", handler.getLogger().getName(), CoreMatchers.is(((ContextHandler.class.getName()) + ".ROOT")));
        } finally {
            handler.stop();
        }
    }

    @Test
    public void testLogNameFromContextPath_Undefined() throws Exception {
        ContextHandler handler = new ContextHandler();
        handler.setServer(new Server());
        try {
            handler.start();
            MatcherAssert.assertThat("handler.get", handler.getLogger().getName(), CoreMatchers.is(((ContextHandler.class.getName()) + ".ROOT")));
        } finally {
            handler.stop();
        }
    }

    @Test
    public void testLogNameFromContextPath_Empty() throws Exception {
        ContextHandler handler = new ContextHandler();
        handler.setServer(new Server());
        handler.setContextPath("");
        try {
            handler.start();
            MatcherAssert.assertThat("handler.get", handler.getLogger().getName(), CoreMatchers.is(((ContextHandler.class.getName()) + ".ROOT")));
        } finally {
            handler.stop();
        }
    }

    private static final class IsHandledHandler extends AbstractHandler {
        private boolean handled;

        public boolean isHandled() {
            return handled;
        }

        @Override
        public void handle(String s, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            baseRequest.setHandled(true);
            this.handled = true;
        }

        public void reset() {
            handled = false;
        }
    }

    private static final class ContextPathHandler extends AbstractHandler {
        @Override
        public void handle(String s, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            baseRequest.setHandled(true);
            response.setStatus(200);
            response.setContentType("text/plain; charset=utf-8");
            response.setHeader("Connection", "close");
            PrintWriter writer = response.getWriter();
            writer.println((("ctx='" + (request.getContextPath())) + "'"));
        }
    }
}

