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
package org.eclipse.jetty.webapp;


import Configuration.ATTR;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.GenericServlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import org.eclipse.jetty.server.LocalConnector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.HotSwapHandler;
import org.eclipse.jetty.servlet.ErrorPageErrorHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.toolchain.test.MavenTestingUtils;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.resource.PathResource;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.resource.ResourceCollection;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class WebAppContextTest {
    public class MySessionListener implements HttpSessionListener {
        @Override
        public void sessionCreated(HttpSessionEvent se) {
            // TODO Auto-generated method stub
        }

        @Override
        public void sessionDestroyed(HttpSessionEvent se) {
            // TODO Auto-generated method stub
        }
    }

    @Test
    public void testSessionListeners() throws Exception {
        Server server = new Server();
        WebAppContext wac = new WebAppContext();
        wac.setServer(server);
        server.setHandler(wac);
        wac.addEventListener(new WebAppContextTest.MySessionListener());
        Collection<WebAppContextTest.MySessionListener> listeners = wac.getSessionHandler().getBeans(WebAppContextTest.MySessionListener.class);
        Assertions.assertNotNull(listeners);
        Assertions.assertEquals(1, listeners.size());
    }

    @Test
    public void testConfigurationClassesFromDefault() {
        Server server = new Server();
        // test if no classnames set, its the defaults
        WebAppContext wac = new WebAppContext();
        Assertions.assertEquals(0, wac.getConfigurations().length);
        String[] classNames = wac.getConfigurationClasses();
        Assertions.assertNotNull(classNames);
        // test if no classname set, and none from server its the defaults
        wac.setServer(server);
        Assertions.assertTrue(Arrays.equals(classNames, wac.getConfigurationClasses()));
    }

    @Test
    public void testConfigurationClassesExplicit() {
        String[] classNames = new String[]{ "x.y.z" };
        Server server = new Server();
        server.setAttribute(ATTR, classNames);
        // test an explicitly set classnames list overrides that from the server
        WebAppContext wac = new WebAppContext();
        String[] myClassNames = new String[]{ "a.b.c", "d.e.f" };
        wac.setConfigurationClasses(myClassNames);
        wac.setServer(server);
        String[] names = wac.getConfigurationClasses();
        Assertions.assertTrue(Arrays.equals(myClassNames, names));
        // test if no explicit classnames, they come from the server
        WebAppContext wac2 = new WebAppContext();
        wac2.setServer(server);
        try {
            wac2.loadConfigurations();
        } catch (Exception e) {
            Log.getRootLogger().ignore(e);
        }
        Assertions.assertTrue(Arrays.equals(classNames, wac2.getConfigurationClasses()));
    }

    @Test
    public void testConfigurationInstances() {
        Configuration[] configs = new Configuration[]{ new WebInfConfiguration() };
        WebAppContext wac = new WebAppContext();
        wac.setConfigurations(configs);
        Assertions.assertTrue(Arrays.equals(configs, wac.getConfigurations()));
        // test that explicit config instances override any from server
        String[] classNames = new String[]{ "x.y.z" };
        Server server = new Server();
        server.setAttribute(ATTR, classNames);
        wac.setServer(server);
        Assertions.assertTrue(Arrays.equals(configs, wac.getConfigurations()));
    }

    @Test
    public void testRealPathDoesNotExist() throws Exception {
        Server server = new Server(0);
        WebAppContext context = new WebAppContext(".", "/");
        server.setHandler(context);
        server.start();
        ServletContext ctx = context.getServletContext();
        Assertions.assertNotNull(ctx.getRealPath("/doesnotexist"));
        Assertions.assertNotNull(ctx.getRealPath("/doesnotexist/"));
    }

    /**
     * tests that the servlet context white list works
     *
     * @throws Exception
     * 		on test failure
     */
    @Test
    public void testContextWhiteList() throws Exception {
        Server server = new Server(0);
        HandlerList handlers = new HandlerList();
        WebAppContext contextA = new WebAppContext(".", "/A");
        contextA.addServlet(WebAppContextTest.ServletA.class, "/s");
        handlers.addHandler(contextA);
        WebAppContext contextB = new WebAppContext(".", "/B");
        contextB.addServlet(WebAppContextTest.ServletB.class, "/s");
        contextB.setContextWhiteList(new String[]{ "/doesnotexist", "/B/s" });
        handlers.addHandler(contextB);
        server.setHandler(handlers);
        server.start();
        // context A should be able to get both A and B servlet contexts
        Assertions.assertNotNull(getServletContext().getContext("/A/s"));
        Assertions.assertNotNull(getServletContext().getContext("/B/s"));
        // context B has a contextWhiteList set and should only be able to get ones that are approved
        Assertions.assertNull(getServletContext().getContext("/A/s"));
        Assertions.assertNotNull(getServletContext().getContext("/B/s"));
    }

    @Test
    public void testAlias() throws Exception {
        File dir = File.createTempFile("dir", null);
        dir.delete();
        dir.mkdir();
        dir.deleteOnExit();
        File webinf = new File(dir, "WEB-INF");
        webinf.mkdir();
        File classes = new File(dir, "classes");
        classes.mkdir();
        File someclass = new File(classes, "SomeClass.class");
        someclass.createNewFile();
        WebAppContext context = new WebAppContext();
        context.setBaseResource(new ResourceCollection(dir.getAbsolutePath()));
        context.setResourceAlias("/WEB-INF/classes/", "/classes/");
        Assertions.assertTrue(Resource.newResource(context.getServletContext().getResource("/WEB-INF/classes/SomeClass.class")).exists());
        Assertions.assertTrue(Resource.newResource(context.getServletContext().getResource("/classes/SomeClass.class")).exists());
    }

    @Test
    public void testIsProtected() throws Exception {
        WebAppContext context = new WebAppContext();
        Assertions.assertTrue(context.isProtectedTarget("/web-inf/lib/foo.jar"));
        Assertions.assertTrue(context.isProtectedTarget("/meta-inf/readme.txt"));
        Assertions.assertFalse(context.isProtectedTarget("/something-else/web-inf"));
    }

    @Test
    public void testNullPath() throws Exception {
        Server server = new Server(0);
        HandlerList handlers = new HandlerList();
        ContextHandlerCollection contexts = new ContextHandlerCollection();
        WebAppContext context = new WebAppContext();
        context.setBaseResource(Resource.newResource("./src/test/webapp"));
        context.setContextPath("/");
        server.setHandler(handlers);
        handlers.addHandler(contexts);
        contexts.addHandler(context);
        LocalConnector connector = new LocalConnector(server);
        server.addConnector(connector);
        server.start();
        try {
            String response = connector.getResponse("GET http://localhost:8080 HTTP/1.1\r\nHost: localhost:8080\r\nConnection: close\r\n\r\n");
            Assertions.assertTrue(((response.indexOf("200 OK")) >= 0));
        } finally {
            server.stop();
        }
    }

    @Test
    public void testNullSessionAndSecurityHandler() throws Exception {
        Server server = new Server(0);
        HandlerList handlers = new HandlerList();
        ContextHandlerCollection contexts = new ContextHandlerCollection();
        WebAppContext context = new WebAppContext(null, null, null, null, null, new ErrorPageErrorHandler(), ((ServletContextHandler.NO_SESSIONS) | (ServletContextHandler.NO_SECURITY)));
        context.setContextPath("/");
        context.setBaseResource(Resource.newResource("./src/test/webapp"));
        server.setHandler(handlers);
        handlers.addHandler(contexts);
        contexts.addHandler(context);
        LocalConnector connector = new LocalConnector(server);
        server.addConnector(connector);
        try {
            server.start();
            Assertions.assertTrue(context.isAvailable());
        } finally {
            server.stop();
        }
    }

    class ServletA extends GenericServlet {
        @Override
        public void service(ServletRequest req, ServletResponse res) throws IOException, ServletException {
            getServletContext().getContext("/A/s");
        }
    }

    class ServletB extends GenericServlet {
        @Override
        public void service(ServletRequest req, ServletResponse res) throws IOException, ServletException {
            getServletContext().getContext("/B/s");
        }
    }

    @Test
    public void testServletContextListener() throws Exception {
        Server server = new Server();
        HotSwapHandler swap = new HotSwapHandler();
        server.setHandler(swap);
        server.start();
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        context.setResourceBase(System.getProperty("java.io.tmpdir"));
        final List<String> history = new ArrayList<>();
        context.addEventListener(new ServletContextListener() {
            @Override
            public void contextInitialized(ServletContextEvent servletContextEvent) {
                history.add("I0");
            }

            @Override
            public void contextDestroyed(ServletContextEvent servletContextEvent) {
                history.add("D0");
            }
        });
        context.addEventListener(new ServletContextListener() {
            @Override
            public void contextInitialized(ServletContextEvent servletContextEvent) {
                history.add("I1");
            }

            @Override
            public void contextDestroyed(ServletContextEvent servletContextEvent) {
                history.add("D1");
                throw new RuntimeException("Listener1 destroy broken");
            }
        });
        context.addEventListener(new ServletContextListener() {
            @Override
            public void contextInitialized(ServletContextEvent servletContextEvent) {
                history.add("I2");
                throw new RuntimeException("Listener2 init broken");
            }

            @Override
            public void contextDestroyed(ServletContextEvent servletContextEvent) {
                history.add("D2");
            }
        });
        context.addEventListener(new ServletContextListener() {
            @Override
            public void contextInitialized(ServletContextEvent servletContextEvent) {
                history.add("I3");
            }

            @Override
            public void contextDestroyed(ServletContextEvent servletContextEvent) {
                history.add("D3");
            }
        });
        try {
            swap.setHandler(context);
            context.start();
        } catch (Exception e) {
            history.add(e.getMessage());
        } finally {
            try {
                swap.setHandler(null);
            } catch (Exception e) {
                while ((e.getCause()) instanceof Exception)
                    e = ((Exception) (e.getCause()));

                history.add(e.getMessage());
            } finally {
            }
        }
        MatcherAssert.assertThat(history, Matchers.contains("I0", "I1", "I2", "Listener2 init broken", "D1", "D0", "Listener1 destroy broken"));
        server.stop();
    }

    @Test
    public void ordering() throws Exception {
        Path testWebappDir = MavenTestingUtils.getProjectDirPath("src/test/webapp");
        Resource webapp = new PathResource(testWebappDir);
        WebAppContext context = new WebAppContext();
        context.setBaseResource(webapp);
        context.setContextPath("/test");
        new WebInfConfiguration().preConfigure(context);
        Assertions.assertEquals(Arrays.asList("acme.jar", "alpha.jar", "omega.jar"), context.getMetaData().getWebInfJars().stream().map(( r) -> r.getURI().toString().replaceFirst(".+/", "")).collect(Collectors.toList()));
    }
}

