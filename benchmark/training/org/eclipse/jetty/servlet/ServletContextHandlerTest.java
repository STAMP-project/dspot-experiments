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
package org.eclipse.jetty.servlet;


import ContextHandler.Context;
import DecoratedObjectFactory.ATTR;
import HttpServletResponse.SC_OK;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import javax.servlet.Servlet;
import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.security.SecurityHandler;
import org.eclipse.jetty.server.LocalConnector;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.AbstractHandlerContainer;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.HandlerWrapper;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.util.DecoratedObjectFactory;
import org.eclipse.jetty.util.Decorator;
import org.eclipse.jetty.util.component.AbstractLifeCycle;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static ServletContextHandler.GZIP;
import static ServletContextHandler.SESSIONS;


public class ServletContextHandlerTest {
    private Server _server;

    private LocalConnector _connector;

    private static final AtomicInteger __testServlets = new AtomicInteger();

    public static class MySCI implements ServletContainerInitializer {
        @Override
        public void onStartup(Set<Class<?>> c, ServletContext ctx) throws ServletException {
            // add a programmatic listener
            if ((ctx.getAttribute("MySCI.startup")) != null)
                throw new IllegalStateException("MySCI already called");

            ctx.setAttribute("MySCI.startup", Boolean.TRUE);
            ctx.addListener(new ServletContextHandlerTest.MyContextListener());
        }
    }

    public static class MySCIStarter extends AbstractLifeCycle implements ServletContextHandler.ServletContainerInitializerCaller {
        ServletContextHandlerTest.MySCI _sci = new ServletContextHandlerTest.MySCI();

        Context _ctx;

        MySCIStarter(ContextHandler.Context ctx) {
            _ctx = ctx;
        }

        @Override
        protected void doStart() throws Exception {
            super.doStart();
            // call the SCI
            try {
                _ctx.setExtendedListenerTypes(true);
                _sci.onStartup(Collections.emptySet(), _ctx);
            } finally {
            }
        }
    }

    public static class MyContextListener implements ServletContextListener {
        @Override
        public void contextInitialized(ServletContextEvent sce) {
            Assertions.assertNull(sce.getServletContext().getAttribute("MyContextListener.contextInitialized"));
            sce.getServletContext().setAttribute("MyContextListener.contextInitialized", Boolean.TRUE);
        }

        @Override
        public void contextDestroyed(ServletContextEvent sce) {
            Assertions.assertNull(sce.getServletContext().getAttribute("MyContextListener.contextDestroyed"));
            sce.getServletContext().setAttribute("MyContextListener.contextDestroyed", Boolean.TRUE);
        }
    }

    public static class MySessionHandler extends SessionHandler {
        public void checkSessionListeners(int size) {
            Assertions.assertNotNull(_sessionListeners);
            Assertions.assertEquals(size, _sessionListeners.size());
        }

        public void checkSessionAttributeListeners(int size) {
            Assertions.assertNotNull(_sessionAttributeListeners);
            Assertions.assertEquals(size, _sessionAttributeListeners.size());
        }

        public void checkSessionIdListeners(int size) {
            Assertions.assertNotNull(_sessionIdListeners);
            Assertions.assertEquals(size, _sessionIdListeners.size());
        }
    }

    public static class MyTestSessionListener implements HttpSessionAttributeListener , HttpSessionListener {
        @Override
        public void sessionCreated(HttpSessionEvent se) {
        }

        @Override
        public void sessionDestroyed(HttpSessionEvent se) {
        }

        @Override
        public void attributeAdded(HttpSessionBindingEvent event) {
        }

        @Override
        public void attributeRemoved(HttpSessionBindingEvent event) {
        }

        @Override
        public void attributeReplaced(HttpSessionBindingEvent event) {
        }
    }

    @Test
    public void testAddSessionListener() throws Exception {
        ContextHandlerCollection contexts = new ContextHandlerCollection();
        _server.setHandler(contexts);
        ServletContextHandler root = new ServletContextHandler(contexts, "/", SESSIONS);
        ServletContextHandlerTest.MySessionHandler sessions = new ServletContextHandlerTest.MySessionHandler();
        root.setSessionHandler(sessions);
        Assertions.assertNotNull(sessions);
        root.addEventListener(new ServletContextHandlerTest.MyTestSessionListener());
        sessions.checkSessionAttributeListeners(1);
        sessions.checkSessionIdListeners(0);
        sessions.checkSessionListeners(1);
    }

    @Test
    public void testListenerFromSCI() throws Exception {
        ContextHandlerCollection contexts = new ContextHandlerCollection();
        _server.setHandler(contexts);
        ServletContextHandler root = new ServletContextHandler(contexts, "/");
        root.addBean(new ServletContextHandlerTest.MySCIStarter(root.getServletContext()), true);
        _server.start();
        Assertions.assertTrue(((Boolean) (root.getServletContext().getAttribute("MySCI.startup"))));
        Assertions.assertTrue(((Boolean) (root.getServletContext().getAttribute("MyContextListener.contextInitialized"))));
    }

    @Test
    public void testFindContainer() throws Exception {
        ContextHandlerCollection contexts = new ContextHandlerCollection();
        _server.setHandler(contexts);
        ServletContextHandler root = new ServletContextHandler(contexts, "/", SESSIONS);
        SessionHandler session = root.getSessionHandler();
        ServletHandler servlet = root.getServletHandler();
        SecurityHandler security = new ConstraintSecurityHandler();
        root.setSecurityHandler(security);
        _server.start();
        Assertions.assertEquals(root, AbstractHandlerContainer.findContainerOf(_server, ContextHandler.class, session));
        Assertions.assertEquals(root, AbstractHandlerContainer.findContainerOf(_server, ContextHandler.class, security));
        Assertions.assertEquals(root, AbstractHandlerContainer.findContainerOf(_server, ContextHandler.class, servlet));
    }

    @Test
    public void testInitOrder() throws Exception {
        ServletContextHandler context = new ServletContextHandler();
        ServletHolder holder0 = context.addServlet(ServletContextHandlerTest.TestServlet.class, "/test0");
        ServletHolder holder1 = context.addServlet(ServletContextHandlerTest.TestServlet.class, "/test1");
        ServletHolder holder2 = context.addServlet(ServletContextHandlerTest.TestServlet.class, "/test2");
        holder1.setInitOrder(1);
        holder2.setInitOrder(2);
        context.setContextPath("/");
        _server.setHandler(context);
        _server.start();
        Assertions.assertEquals(2, ServletContextHandlerTest.__testServlets.get());
        String response = _connector.getResponse("GET /test1 HTTP/1.0\r\n\r\n");
        MatcherAssert.assertThat(response, Matchers.containsString("200 OK"));
        Assertions.assertEquals(2, ServletContextHandlerTest.__testServlets.get());
        response = _connector.getResponse("GET /test2 HTTP/1.0\r\n\r\n");
        MatcherAssert.assertThat(response, Matchers.containsString("200 OK"));
        Assertions.assertEquals(2, ServletContextHandlerTest.__testServlets.get());
        MatcherAssert.assertThat(holder0.getServletInstance(), Matchers.nullValue());
        response = _connector.getResponse("GET /test0 HTTP/1.0\r\n\r\n");
        MatcherAssert.assertThat(response, Matchers.containsString("200 OK"));
        Assertions.assertEquals(3, ServletContextHandlerTest.__testServlets.get());
        MatcherAssert.assertThat(holder0.getServletInstance(), Matchers.notNullValue(Servlet.class));
        _server.stop();
        Assertions.assertEquals(0, ServletContextHandlerTest.__testServlets.get());
        holder0.setInitOrder(0);
        _server.start();
        Assertions.assertEquals(3, ServletContextHandlerTest.__testServlets.get());
        MatcherAssert.assertThat(holder0.getServletInstance(), Matchers.notNullValue(Servlet.class));
        _server.stop();
        Assertions.assertEquals(0, ServletContextHandlerTest.__testServlets.get());
    }

    @Test
    public void testAddServletAfterStart() throws Exception {
        ServletContextHandler context = new ServletContextHandler();
        context.addServlet(ServletContextHandlerTest.TestServlet.class, "/test");
        context.setContextPath("/");
        _server.setHandler(context);
        _server.start();
        StringBuffer request = new StringBuffer();
        request.append("GET /test HTTP/1.0\n");
        request.append("Host: localhost\n");
        request.append("\n");
        String response = _connector.getResponse(request.toString());
        int result;
        MatcherAssert.assertThat("Response", response, Matchers.containsString("Test"));
        context.addServlet(ServletContextHandlerTest.HelloServlet.class, "/hello");
        request = new StringBuffer();
        request.append("GET /hello HTTP/1.0\n");
        request.append("Host: localhost\n");
        request.append("\n");
        response = _connector.getResponse(request.toString());
        MatcherAssert.assertThat("Response", response, Matchers.containsString("Hello World"));
    }

    @Test
    public void testHandlerBeforeServletHandler() throws Exception {
        ServletContextHandler context = new ServletContextHandler(SESSIONS);
        HandlerWrapper extra = new HandlerWrapper();
        context.getSessionHandler().insertHandler(extra);
        context.addServlet(ServletContextHandlerTest.TestServlet.class, "/test");
        context.setContextPath("/");
        _server.setHandler(context);
        _server.start();
        StringBuffer request = new StringBuffer();
        request.append("GET /test HTTP/1.0\n");
        request.append("Host: localhost\n");
        request.append("\n");
        String response = _connector.getResponse(request.toString());
        int result;
        MatcherAssert.assertThat("Response", response, Matchers.containsString("Test"));
        Assertions.assertEquals(extra, context.getSessionHandler().getHandler());
    }

    @Test
    public void testGzipHandlerOption() throws Exception {
        ServletContextHandler context = new ServletContextHandler(((SESSIONS) | (GZIP)));
        GzipHandler gzip = context.getGzipHandler();
        _server.start();
        Assertions.assertEquals(context.getSessionHandler(), context.getHandler());
        Assertions.assertEquals(gzip, context.getSessionHandler().getHandler());
        Assertions.assertEquals(context.getServletHandler(), gzip.getHandler());
    }

    @Test
    public void testGzipHandlerSet() throws Exception {
        ServletContextHandler context = new ServletContextHandler();
        context.setSessionHandler(new SessionHandler());
        context.setGzipHandler(new GzipHandler());
        GzipHandler gzip = context.getGzipHandler();
        _server.start();
        Assertions.assertEquals(context.getSessionHandler(), context.getHandler());
        Assertions.assertEquals(gzip, context.getSessionHandler().getHandler());
        Assertions.assertEquals(context.getServletHandler(), gzip.getHandler());
    }

    @Test
    public void testReplaceServletHandlerWithServlet() throws Exception {
        ServletContextHandler context = new ServletContextHandler();
        context.addServlet(ServletContextHandlerTest.TestServlet.class, "/test");
        context.setContextPath("/");
        _server.setHandler(context);
        _server.start();
        StringBuffer request = new StringBuffer();
        request.append("GET /test HTTP/1.0\n");
        request.append("Host: localhost\n");
        request.append("\n");
        String response = _connector.getResponse(request.toString());
        int result;
        MatcherAssert.assertThat("Response", response, Matchers.containsString("Test"));
        context.stop();
        ServletHandler srvHnd = new ServletHandler();
        srvHnd.addServletWithMapping(ServletContextHandlerTest.HelloServlet.class, "/hello");
        context.setServletHandler(srvHnd);
        context.start();
        request = new StringBuffer();
        request.append("GET /hello HTTP/1.0\n");
        request.append("Host: localhost\n");
        request.append("\n");
        response = _connector.getResponse(request.toString());
        MatcherAssert.assertThat("Response", response, Matchers.containsString("Hello World"));
    }

    @Test
    public void testReplaceServletHandlerWithoutServlet() throws Exception {
        ServletContextHandler context = new ServletContextHandler();
        context.addServlet(ServletContextHandlerTest.TestServlet.class, "/test");
        context.setContextPath("/");
        _server.setHandler(context);
        _server.start();
        StringBuffer request = new StringBuffer();
        request.append("GET /test HTTP/1.0\n");
        request.append("Host: localhost\n");
        request.append("\n");
        String response = _connector.getResponse(request.toString());
        int result;
        MatcherAssert.assertThat("Response", response, Matchers.containsString("Test"));
        context.stop();
        ServletHandler srvHnd = new ServletHandler();
        context.setServletHandler(srvHnd);
        context.start();
        context.addServlet(ServletContextHandlerTest.HelloServlet.class, "/hello");
        request = new StringBuffer();
        request.append("GET /hello HTTP/1.0\n");
        request.append("Host: localhost\n");
        request.append("\n");
        response = _connector.getResponse(request.toString());
        MatcherAssert.assertThat("Response", response, Matchers.containsString("Hello World"));
    }

    @Test
    public void testReplaceHandler() throws Exception {
        ServletContextHandler servletContextHandler = new ServletContextHandler();
        ServletHolder sh = new ServletHolder(new ServletContextHandlerTest.TestServlet());
        servletContextHandler.addServlet(sh, "/foo");
        final AtomicBoolean contextInit = new AtomicBoolean(false);
        final AtomicBoolean contextDestroy = new AtomicBoolean(false);
        servletContextHandler.addEventListener(new ServletContextListener() {
            @Override
            public void contextInitialized(ServletContextEvent sce) {
                if ((sce.getServletContext()) != null)
                    contextInit.set(true);

            }

            @Override
            public void contextDestroyed(ServletContextEvent sce) {
                if ((sce.getServletContext()) != null)
                    contextDestroy.set(true);

            }
        });
        ServletHandler shandler = servletContextHandler.getServletHandler();
        ResourceHandler rh = new ResourceHandler();
        servletContextHandler.insertHandler(rh);
        Assertions.assertEquals(shandler, servletContextHandler.getServletHandler());
        Assertions.assertEquals(rh, servletContextHandler.getHandler());
        Assertions.assertEquals(rh.getHandler(), shandler);
        _server.setHandler(servletContextHandler);
        _server.start();
        Assertions.assertTrue(contextInit.get());
        _server.stop();
        Assertions.assertTrue(contextDestroy.get());
    }

    @Test
    public void testFallThrough() throws Exception {
        HandlerList list = new HandlerList();
        _server.setHandler(list);
        ServletContextHandler root = new ServletContextHandler(list, "/", SESSIONS);
        ServletHandler servlet = root.getServletHandler();
        servlet.setEnsureDefaultServlet(false);
        servlet.addServletWithMapping(ServletContextHandlerTest.HelloServlet.class, "/hello/*");
        list.addHandler(new AbstractHandler() {
            @Override
            public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
                response.sendError(404, "Fell Through");
            }
        });
        _server.start();
        String response = _connector.getResponse("GET /hello HTTP/1.0\r\n\r\n");
        MatcherAssert.assertThat(response, Matchers.containsString("200 OK"));
        response = _connector.getResponse("GET /other HTTP/1.0\r\n\r\n");
        MatcherAssert.assertThat(response, Matchers.containsString("404 Fell Through"));
    }

    /**
     * Test behavior of legacy ServletContextHandler.Decorator, with
     * new DecoratedObjectFactory class
     *
     * @throws Exception
     * 		on test failure
     */
    @SuppressWarnings("deprecation")
    @Test
    public void testLegacyDecorator() throws Exception {
        ServletContextHandler context = new ServletContextHandler();
        context.addDecorator(new ServletContextHandlerTest.DummyLegacyDecorator());
        _server.setHandler(context);
        context.addServlet(ServletContextHandlerTest.DecoratedObjectFactoryServlet.class, "/objfactory/*");
        _server.start();
        String response = _connector.getResponse("GET /objfactory/ HTTP/1.0\r\n\r\n");
        MatcherAssert.assertThat("Response status code", response, Matchers.containsString("200 OK"));
        String expected = String.format("Attribute[%s] = %s", ATTR, DecoratedObjectFactory.class.getName());
        MatcherAssert.assertThat("Has context attribute", response, Matchers.containsString(expected));
        MatcherAssert.assertThat("Decorators size", response, Matchers.containsString("Decorators.size = [2]"));
        expected = String.format("decorator[] = %s", ServletContextHandlerTest.DummyLegacyDecorator.class.getName());
        MatcherAssert.assertThat("Specific Legacy Decorator", response, Matchers.containsString(expected));
    }

    /**
     * Test behavior of new {@link org.eclipse.jetty.util.Decorator}, with
     * new DecoratedObjectFactory class
     *
     * @throws Exception
     * 		on test failure
     */
    @Test
    public void testUtilDecorator() throws Exception {
        ServletContextHandler context = new ServletContextHandler();
        context.getObjectFactory().addDecorator(new ServletContextHandlerTest.DummyUtilDecorator());
        _server.setHandler(context);
        context.addServlet(ServletContextHandlerTest.DecoratedObjectFactoryServlet.class, "/objfactory/*");
        _server.start();
        String response = _connector.getResponse("GET /objfactory/ HTTP/1.0\r\n\r\n");
        MatcherAssert.assertThat("Response status code", response, Matchers.containsString("200 OK"));
        String expected = String.format("Attribute[%s] = %s", ATTR, DecoratedObjectFactory.class.getName());
        MatcherAssert.assertThat("Has context attribute", response, Matchers.containsString(expected));
        MatcherAssert.assertThat("Decorators size", response, Matchers.containsString("Decorators.size = [2]"));
        expected = String.format("decorator[] = %s", ServletContextHandlerTest.DummyUtilDecorator.class.getName());
        MatcherAssert.assertThat("Specific Legacy Decorator", response, Matchers.containsString(expected));
    }

    public static class HelloServlet extends HttpServlet {
        private static final long serialVersionUID = 1L;

        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
            resp.setStatus(SC_OK);
            PrintWriter writer = resp.getWriter();
            writer.write("Hello World");
        }
    }

    public static class DummyUtilDecorator implements Decorator {
        @Override
        public <T> T decorate(T o) {
            return o;
        }

        @Override
        public void destroy(Object o) {
        }
    }

    public static class DummyLegacyDecorator implements ServletContextHandler.Decorator {
        @Override
        public <T> T decorate(T o) {
            return o;
        }

        @Override
        public void destroy(Object o) {
        }
    }

    public static class DecoratedObjectFactoryServlet extends HttpServlet {
        private static final long serialVersionUID = 1L;

        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
            resp.setContentType("text/plain");
            resp.setStatus(SC_OK);
            PrintWriter out = resp.getWriter();
            Object obj = req.getServletContext().getAttribute(ATTR);
            out.printf("Attribute[%s] = %s%n", ATTR, obj.getClass().getName());
            if (obj instanceof DecoratedObjectFactory) {
                out.printf("Object is a DecoratedObjectFactory%n");
                DecoratedObjectFactory objFactory = ((DecoratedObjectFactory) (obj));
                List<Decorator> decorators = objFactory.getDecorators();
                out.printf("Decorators.size = [%d]%n", decorators.size());
                for (Decorator decorator : decorators) {
                    out.printf(" decorator[] = %s%n", decorator.getClass().getName());
                }
            } else {
                out.printf("Object is NOT a DecoratedObjectFactory%n");
            }
        }
    }

    public static class TestServlet extends HttpServlet {
        private static final long serialVersionUID = 1L;

        @Override
        public void destroy() {
            super.destroy();
            ServletContextHandlerTest.__testServlets.decrementAndGet();
        }

        @Override
        public void init() throws ServletException {
            ServletContextHandlerTest.__testServlets.incrementAndGet();
            super.init();
        }

        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
            resp.setStatus(SC_OK);
            PrintWriter writer = resp.getWriter();
            writer.write("Test");
        }
    }
}

