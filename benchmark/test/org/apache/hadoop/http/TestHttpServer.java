/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.http;


import CommonConfigurationKeys.HADOOP_SECURITY_AUTHORIZATION;
import CommonConfigurationKeys.HADOOP_SECURITY_GROUP_MAPPING;
import CommonConfigurationKeys.HADOOP_SECURITY_INSTRUMENTATION_REQUIRES_ADMIN;
import HttpServer2.ADMINS_ACL;
import HttpServer2.Builder;
import HttpServer2.CONF_CONTEXT_ATTRIBUTE;
import HttpServer2.FILTER_INITIALIZER_PROPERTY;
import HttpServer2.HTTP_ACCEPTOR_COUNT_KEY;
import HttpServer2.HTTP_IDLE_TIMEOUT_MS_KEY;
import HttpServer2.HTTP_MAX_THREADS_KEY;
import HttpServer2.HTTP_SELECTOR_COUNT_KEY;
import HttpServer2.HTTP_SOCKET_BACKLOG_SIZE_KEY;
import HttpServer2.XFrameOption.ALLOWFROM;
import HttpServer2.XFrameOption.DENY;
import HttpServer2.XFrameOption.SAMEORIGIN;
import HttpServer2.X_CONTENT_TYPE_OPTIONS;
import HttpServer2.X_XSS_PROTECTION;
import HttpServletResponse.SC_FORBIDDEN;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configuration.IntegerRanges;
import org.apache.hadoop.http.HttpServer2.QuotingInputFilter.RequestQuoter;
import org.apache.hadoop.http.resource.JerseyResource;
import org.apache.hadoop.net.NetUtils;
import org.apache.hadoop.net.ServerSocketUtil;
import org.apache.hadoop.security.Groups;
import org.apache.hadoop.security.ShellBasedUnixGroupsMapping;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.security.authorize.AccessControlList;
import org.apache.hadoop.test.Whitebox;
import org.eclipse.jetty.server.ServerConnector;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static HttpServer2.HTTP_HEADER_PREFIX;
import static JettyUtils.UTF_8;


public class TestHttpServer extends HttpServerFunctionalTest {
    static final Logger LOG = LoggerFactory.getLogger(TestHttpServer.class);

    private static HttpServer2 server;

    private static final int MAX_THREADS = 10;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @SuppressWarnings("serial")
    public static class EchoMapServlet extends HttpServlet {
        @SuppressWarnings("unchecked")
        @Override
        public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            response.setContentType((((MediaType.TEXT_PLAIN) + "; ") + (UTF_8)));
            PrintWriter out = response.getWriter();
            Map<String, String[]> params = request.getParameterMap();
            SortedSet<String> keys = new TreeSet<String>(params.keySet());
            for (String key : keys) {
                out.print(key);
                out.print(':');
                String[] values = params.get(key);
                if ((values.length) > 0) {
                    out.print(values[0]);
                    for (int i = 1; i < (values.length); ++i) {
                        out.print(',');
                        out.print(values[i]);
                    }
                }
                out.print('\n');
            }
            out.close();
        }
    }

    @SuppressWarnings("serial")
    public static class EchoServlet extends HttpServlet {
        @SuppressWarnings("unchecked")
        @Override
        public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            response.setContentType((((MediaType.TEXT_PLAIN) + "; ") + (JettyUtils.UTF_8)));
            PrintWriter out = response.getWriter();
            SortedSet<String> sortedKeys = new TreeSet<String>();
            Enumeration<String> keys = request.getParameterNames();
            while (keys.hasMoreElements()) {
                sortedKeys.add(keys.nextElement());
            } 
            for (String key : sortedKeys) {
                out.print(key);
                out.print(':');
                out.print(request.getParameter(key));
                out.print('\n');
            }
            out.close();
        }
    }

    @SuppressWarnings("serial")
    public static class HtmlContentServlet extends HttpServlet {
        @Override
        public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            response.setContentType((((MediaType.TEXT_HTML) + "; ") + (JettyUtils.UTF_8)));
            PrintWriter out = response.getWriter();
            out.print("hello world");
            out.close();
        }
    }

    /**
     * Test the maximum number of threads cannot be exceeded.
     */
    @Test
    public void testMaxThreads() throws Exception {
        int clientThreads = (TestHttpServer.MAX_THREADS) * 10;
        Executor executor = Executors.newFixedThreadPool(clientThreads);
        // Run many clients to make server reach its maximum number of threads
        final CountDownLatch ready = new CountDownLatch(clientThreads);
        final CountDownLatch start = new CountDownLatch(1);
        for (int i = 0; i < clientThreads; i++) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    ready.countDown();
                    try {
                        start.await();
                        Assert.assertEquals("a:b\nc:d\n", HttpServerFunctionalTest.readOutput(new URL(HttpServerFunctionalTest.baseUrl, "/echo?a=b&c=d")));
                        int serverThreads = TestHttpServer.server.webServer.getThreadPool().getThreads();
                        Assert.assertTrue(("More threads are started than expected, Server Threads count: " + serverThreads), (serverThreads <= (TestHttpServer.MAX_THREADS)));
                        System.out.println(((("Number of threads = " + serverThreads) + " which is less or equal than the max = ") + (TestHttpServer.MAX_THREADS)));
                    } catch (Exception e) {
                        // do nothing
                    }
                }
            });
        }
        // Start the client threads when they are all ready
        ready.await();
        start.countDown();
    }

    /**
     * Test that the number of acceptors and selectors can be configured by
     * trying to configure more of them than would be allowed based on the
     * maximum thread count.
     */
    @Test
    public void testAcceptorSelectorConfigurability() throws Exception {
        Configuration conf = new Configuration();
        conf.setInt(HTTP_MAX_THREADS_KEY, TestHttpServer.MAX_THREADS);
        conf.setInt(HTTP_ACCEPTOR_COUNT_KEY, ((TestHttpServer.MAX_THREADS) - 2));
        conf.setInt(HTTP_SELECTOR_COUNT_KEY, ((TestHttpServer.MAX_THREADS) - 2));
        HttpServer2 badserver = HttpServerFunctionalTest.createTestServer(conf);
        try {
            badserver.start();
            // Should not succeed
            Assert.fail();
        } catch (IOException ioe) {
            Assert.assertTrue(((ioe.getCause()) instanceof IllegalStateException));
        }
    }

    @Test
    public void testEcho() throws Exception {
        Assert.assertEquals("a:b\nc:d\n", HttpServerFunctionalTest.readOutput(new URL(HttpServerFunctionalTest.baseUrl, "/echo?a=b&c=d")));
        Assert.assertEquals("a:b\nc&lt;:d\ne:&gt;\n", HttpServerFunctionalTest.readOutput(new URL(HttpServerFunctionalTest.baseUrl, "/echo?a=b&c<=d&e=>")));
    }

    /**
     * Test the echo map servlet that uses getParameterMap.
     */
    @Test
    public void testEchoMap() throws Exception {
        Assert.assertEquals("a:b\nc:d\n", HttpServerFunctionalTest.readOutput(new URL(HttpServerFunctionalTest.baseUrl, "/echomap?a=b&c=d")));
        Assert.assertEquals("a:b,&gt;\nc&lt;:d\n", HttpServerFunctionalTest.readOutput(new URL(HttpServerFunctionalTest.baseUrl, "/echomap?a=b&c<=d&a=>")));
    }

    @Test
    public void testLongHeader() throws Exception {
        URL url = new URL(HttpServerFunctionalTest.baseUrl, "/longheader");
        HttpURLConnection conn = ((HttpURLConnection) (url.openConnection()));
        testLongHeader(conn);
    }

    @Test
    public void testContentTypes() throws Exception {
        // Static CSS files should have text/css
        URL cssUrl = new URL(HttpServerFunctionalTest.baseUrl, "/static/test.css");
        HttpURLConnection conn = ((HttpURLConnection) (cssUrl.openConnection()));
        conn.connect();
        Assert.assertEquals(200, conn.getResponseCode());
        Assert.assertEquals("text/css", conn.getContentType());
        // Servlets should have text/plain with proper encoding by default
        URL servletUrl = new URL(HttpServerFunctionalTest.baseUrl, "/echo?a=b");
        conn = ((HttpURLConnection) (servletUrl.openConnection()));
        conn.connect();
        Assert.assertEquals(200, conn.getResponseCode());
        Assert.assertEquals((((MediaType.TEXT_PLAIN) + ";") + (JettyUtils.UTF_8)), conn.getContentType());
        // We should ignore parameters for mime types - ie a parameter
        // ending in .css should not change mime type
        servletUrl = new URL(HttpServerFunctionalTest.baseUrl, "/echo?a=b.css");
        conn = ((HttpURLConnection) (servletUrl.openConnection()));
        conn.connect();
        Assert.assertEquals(200, conn.getResponseCode());
        Assert.assertEquals((((MediaType.TEXT_PLAIN) + ";") + (JettyUtils.UTF_8)), conn.getContentType());
        // Servlets that specify text/html should get that content type
        servletUrl = new URL(HttpServerFunctionalTest.baseUrl, "/htmlcontent");
        conn = ((HttpURLConnection) (servletUrl.openConnection()));
        conn.connect();
        Assert.assertEquals(200, conn.getResponseCode());
        Assert.assertEquals((((MediaType.TEXT_HTML) + ";") + (JettyUtils.UTF_8)), conn.getContentType());
    }

    @Test
    public void testHttpResonseContainsXFrameOptions() throws Exception {
        validateXFrameOption(SAMEORIGIN);
    }

    @Test
    public void testHttpResonseContainsDeny() throws Exception {
        validateXFrameOption(DENY);
    }

    @Test
    public void testHttpResonseContainsAllowFrom() throws Exception {
        validateXFrameOption(ALLOWFROM);
    }

    @Test
    public void testHttpResonseDoesNotContainXFrameOptions() throws Exception {
        Configuration conf = new Configuration();
        boolean xFrameEnabled = false;
        HttpServer2 httpServer = HttpServerFunctionalTest.createServer(xFrameEnabled, SAMEORIGIN.toString(), conf);
        try {
            HttpURLConnection conn = getHttpURLConnection(httpServer);
            String xfoHeader = conn.getHeaderField("X-FRAME-OPTIONS");
            Assert.assertTrue("Unexpected X-FRAME-OPTIONS in header", (xfoHeader == null));
        } finally {
            httpServer.stop();
        }
    }

    @Test
    public void testHttpResonseInvalidValueType() throws Exception {
        Configuration conf = new Configuration();
        boolean xFrameEnabled = true;
        exception.expect(IllegalArgumentException.class);
        HttpServerFunctionalTest.createServer(xFrameEnabled, "Hadoop", conf);
    }

    /**
     * Dummy filter that mimics as an authentication filter. Obtains user identity
     * from the request parameter user.name. Wraps around the request so that
     * request.getRemoteUser() returns the user identity.
     */
    public static class DummyServletFilter implements Filter {
        @Override
        public void destroy() {
        }

        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
            final String userName = request.getParameter("user.name");
            ServletRequest requestModified = new HttpServletRequestWrapper(((HttpServletRequest) (request))) {
                @Override
                public String getRemoteUser() {
                    return userName;
                }
            };
            filterChain.doFilter(requestModified, response);
        }

        @Override
        public void init(FilterConfig arg0) throws ServletException {
        }
    }

    /**
     * FilterInitializer that initialized the DummyFilter.
     */
    public static class DummyFilterInitializer extends FilterInitializer {
        public DummyFilterInitializer() {
        }

        @Override
        public void initFilter(FilterContainer container, Configuration conf) {
            container.addFilter("DummyFilter", TestHttpServer.DummyServletFilter.class.getName(), null);
        }
    }

    /**
     * Custom user->group mapping service.
     */
    public static class MyGroupsProvider extends ShellBasedUnixGroupsMapping {
        static Map<String, List<String>> mapping = new HashMap<String, List<String>>();

        static void clearMapping() {
            TestHttpServer.MyGroupsProvider.mapping.clear();
        }

        @Override
        public List<String> getGroups(String user) throws IOException {
            return TestHttpServer.MyGroupsProvider.mapping.get(user);
        }
    }

    /**
     * Verify the access for /logs, /stacks, /conf, and /logLevel
     * servlets, when authentication filters are set, but authorization is not
     * enabled.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testDisabledAuthorizationOfDefaultServlets() throws Exception {
        Configuration conf = new Configuration();
        // Authorization is disabled by default
        conf.set(FILTER_INITIALIZER_PROPERTY, TestHttpServer.DummyFilterInitializer.class.getName());
        conf.set(HADOOP_SECURITY_GROUP_MAPPING, TestHttpServer.MyGroupsProvider.class.getName());
        Groups.getUserToGroupsMappingService(conf);
        TestHttpServer.MyGroupsProvider.clearMapping();
        TestHttpServer.MyGroupsProvider.mapping.put("userA", Arrays.asList("groupA"));
        TestHttpServer.MyGroupsProvider.mapping.put("userB", Arrays.asList("groupB"));
        HttpServer2 myServer = new HttpServer2.Builder().setName("test").addEndpoint(new URI("http://localhost:0")).setFindPort(true).build();
        myServer.setAttribute(CONF_CONTEXT_ATTRIBUTE, conf);
        myServer.start();
        String serverURL = ("http://" + (NetUtils.getHostPortString(myServer.getConnectorAddress(0)))) + "/";
        for (String servlet : new String[]{ "conf", "logs", "stacks", "logLevel" }) {
            for (String user : new String[]{ "userA", "userB" }) {
                Assert.assertEquals(HttpURLConnection.HTTP_OK, TestHttpServer.getHttpStatusCode((serverURL + servlet), user));
            }
        }
        myServer.stop();
    }

    /**
     * Verify the administrator access for /logs, /stacks, /conf, and /logLevel
     * servlets.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testAuthorizationOfDefaultServlets() throws Exception {
        Configuration conf = new Configuration();
        conf.setBoolean(HADOOP_SECURITY_AUTHORIZATION, true);
        conf.setBoolean(HADOOP_SECURITY_INSTRUMENTATION_REQUIRES_ADMIN, true);
        conf.set(FILTER_INITIALIZER_PROPERTY, TestHttpServer.DummyFilterInitializer.class.getName());
        conf.set(HADOOP_SECURITY_GROUP_MAPPING, TestHttpServer.MyGroupsProvider.class.getName());
        Groups.getUserToGroupsMappingService(conf);
        TestHttpServer.MyGroupsProvider.clearMapping();
        TestHttpServer.MyGroupsProvider.mapping.put("userA", Arrays.asList("groupA"));
        TestHttpServer.MyGroupsProvider.mapping.put("userB", Arrays.asList("groupB"));
        TestHttpServer.MyGroupsProvider.mapping.put("userC", Arrays.asList("groupC"));
        TestHttpServer.MyGroupsProvider.mapping.put("userD", Arrays.asList("groupD"));
        TestHttpServer.MyGroupsProvider.mapping.put("userE", Arrays.asList("groupE"));
        HttpServer2 myServer = new HttpServer2.Builder().setName("test").addEndpoint(new URI("http://localhost:0")).setFindPort(true).setConf(conf).setACL(new AccessControlList("userA,userB groupC,groupD")).build();
        myServer.setAttribute(CONF_CONTEXT_ATTRIBUTE, conf);
        myServer.start();
        String serverURL = ("http://" + (NetUtils.getHostPortString(myServer.getConnectorAddress(0)))) + "/";
        for (String servlet : new String[]{ "conf", "logs", "stacks", "logLevel" }) {
            for (String user : new String[]{ "userA", "userB", "userC", "userD" }) {
                Assert.assertEquals(HttpURLConnection.HTTP_OK, TestHttpServer.getHttpStatusCode((serverURL + servlet), user));
            }
            Assert.assertEquals(HttpURLConnection.HTTP_FORBIDDEN, TestHttpServer.getHttpStatusCode((serverURL + servlet), "userE"));
        }
        myServer.stop();
    }

    @Test
    public void testRequestQuoterWithNull() throws Exception {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.doReturn(null).when(request).getParameterValues("dummy");
        RequestQuoter requestQuoter = new RequestQuoter(request);
        String[] parameterValues = requestQuoter.getParameterValues("dummy");
        Assert.assertNull(("It should return null " + "when there are no values for the parameter"), parameterValues);
    }

    @Test
    public void testRequestQuoterWithNotNull() throws Exception {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        String[] values = new String[]{ "abc", "def" };
        Mockito.doReturn(values).when(request).getParameterValues("dummy");
        RequestQuoter requestQuoter = new RequestQuoter(request);
        String[] parameterValues = requestQuoter.getParameterValues("dummy");
        Assert.assertTrue("It should return Parameter Values", Arrays.equals(values, parameterValues));
    }

    @Test
    public void testJersey() throws Exception {
        TestHttpServer.LOG.info("BEGIN testJersey()");
        final String js = HttpServerFunctionalTest.readOutput(new URL(HttpServerFunctionalTest.baseUrl, "/jersey/foo?op=bar"));
        final Map<String, Object> m = TestHttpServer.parse(js);
        TestHttpServer.LOG.info(("m=" + m));
        Assert.assertEquals("foo", m.get(JerseyResource.PATH));
        Assert.assertEquals("bar", m.get(JerseyResource.OP));
        TestHttpServer.LOG.info("END testJersey()");
    }

    @Test
    public void testHasAdministratorAccess() throws Exception {
        Configuration conf = new Configuration();
        conf.setBoolean(HADOOP_SECURITY_AUTHORIZATION, false);
        ServletContext context = Mockito.mock(ServletContext.class);
        Mockito.when(context.getAttribute(CONF_CONTEXT_ATTRIBUTE)).thenReturn(conf);
        Mockito.when(context.getAttribute(ADMINS_ACL)).thenReturn(null);
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getRemoteUser()).thenReturn(null);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        // authorization OFF
        Assert.assertTrue(HttpServer2.hasAdministratorAccess(context, request, response));
        // authorization ON & user NULL
        response = Mockito.mock(HttpServletResponse.class);
        conf.setBoolean(HADOOP_SECURITY_AUTHORIZATION, true);
        Assert.assertFalse(HttpServer2.hasAdministratorAccess(context, request, response));
        Mockito.verify(response).sendError(Mockito.eq(SC_FORBIDDEN), Mockito.anyString());
        // authorization ON & user NOT NULL & ACLs NULL
        response = Mockito.mock(HttpServletResponse.class);
        Mockito.when(request.getRemoteUser()).thenReturn("foo");
        Assert.assertTrue(HttpServer2.hasAdministratorAccess(context, request, response));
        // authorization ON & user NOT NULL & ACLs NOT NULL & user not in ACLs
        response = Mockito.mock(HttpServletResponse.class);
        AccessControlList acls = Mockito.mock(AccessControlList.class);
        Mockito.when(acls.isUserAllowed(Mockito.<UserGroupInformation>any())).thenReturn(false);
        Mockito.when(context.getAttribute(ADMINS_ACL)).thenReturn(acls);
        Assert.assertFalse(HttpServer2.hasAdministratorAccess(context, request, response));
        Mockito.verify(response).sendError(Mockito.eq(SC_FORBIDDEN), Mockito.anyString());
        // authorization ON & user NOT NULL & ACLs NOT NULL & user in in ACLs
        response = Mockito.mock(HttpServletResponse.class);
        Mockito.when(acls.isUserAllowed(Mockito.<UserGroupInformation>any())).thenReturn(true);
        Mockito.when(context.getAttribute(ADMINS_ACL)).thenReturn(acls);
        Assert.assertTrue(HttpServer2.hasAdministratorAccess(context, request, response));
    }

    @Test
    public void testRequiresAuthorizationAccess() throws Exception {
        Configuration conf = new Configuration();
        ServletContext context = Mockito.mock(ServletContext.class);
        Mockito.when(context.getAttribute(CONF_CONTEXT_ATTRIBUTE)).thenReturn(conf);
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        // requires admin access to instrumentation, FALSE by default
        Assert.assertTrue(HttpServer2.isInstrumentationAccessAllowed(context, request, response));
        // requires admin access to instrumentation, TRUE
        conf.setBoolean(HADOOP_SECURITY_INSTRUMENTATION_REQUIRES_ADMIN, true);
        conf.setBoolean(HADOOP_SECURITY_AUTHORIZATION, true);
        AccessControlList acls = Mockito.mock(AccessControlList.class);
        Mockito.when(acls.isUserAllowed(Mockito.<UserGroupInformation>any())).thenReturn(false);
        Mockito.when(context.getAttribute(ADMINS_ACL)).thenReturn(acls);
        Assert.assertFalse(HttpServer2.isInstrumentationAccessAllowed(context, request, response));
    }

    @Test
    public void testBindAddress() throws Exception {
        checkBindAddress("localhost", 0, false).stop();
        // hang onto this one for a bit more testing
        HttpServer2 myServer = checkBindAddress("localhost", 0, false);
        HttpServer2 myServer2 = null;
        try {
            int port = myServer.getConnectorAddress(0).getPort();
            // it's already in use, true = expect a higher port
            myServer2 = checkBindAddress("localhost", port, true);
            // try to reuse the port
            port = myServer2.getConnectorAddress(0).getPort();
            myServer2.stop();
            Assert.assertNull(myServer2.getConnectorAddress(0));// not bound

            myServer2.openListeners();
            Assert.assertEquals(port, myServer2.getConnectorAddress(0).getPort());// expect same port

        } finally {
            myServer.stop();
            if (myServer2 != null) {
                myServer2.stop();
            }
        }
    }

    @Test
    public void testNoCacheHeader() throws Exception {
        URL url = new URL(HttpServerFunctionalTest.baseUrl, "/echo?a=b&c=d");
        HttpURLConnection conn = ((HttpURLConnection) (url.openConnection()));
        Assert.assertEquals(HttpURLConnection.HTTP_OK, conn.getResponseCode());
        Assert.assertEquals("no-cache", conn.getHeaderField("Cache-Control"));
        Assert.assertEquals("no-cache", conn.getHeaderField("Pragma"));
        Assert.assertNotNull(conn.getHeaderField("Expires"));
        Assert.assertNotNull(conn.getHeaderField("Date"));
        Assert.assertEquals(conn.getHeaderField("Expires"), conn.getHeaderField("Date"));
    }

    @Test
    public void testPortRanges() throws Exception {
        Configuration conf = new Configuration();
        int port = ServerSocketUtil.waitForPort(49000, 60);
        int endPort = 49500;
        conf.set("abc", "49000-49500");
        HttpServer2.Builder builder = new HttpServer2.Builder().setName("test").setConf(new Configuration()).setFindPort(false);
        IntegerRanges ranges = conf.getRange("abc", "");
        int startPort = 0;
        if ((ranges != null) && (!(ranges.isEmpty()))) {
            startPort = ranges.getRangeStart();
            builder.setPortRanges(ranges);
        }
        builder.addEndpoint(URI.create(("http://localhost:" + startPort)));
        HttpServer2 myServer = builder.build();
        HttpServer2 myServer2 = null;
        try {
            myServer.start();
            Assert.assertEquals(port, myServer.getConnectorAddress(0).getPort());
            myServer2 = builder.build();
            myServer2.start();
            Assert.assertTrue((((myServer2.getConnectorAddress(0).getPort()) > port) && ((myServer2.getConnectorAddress(0).getPort()) <= endPort)));
        } finally {
            TestHttpServer.stopHttpServer(myServer);
            TestHttpServer.stopHttpServer(myServer2);
        }
    }

    @Test
    public void testBacklogSize() throws Exception {
        final int backlogSize = 2048;
        Configuration conf = new Configuration();
        conf.setInt(HTTP_SOCKET_BACKLOG_SIZE_KEY, backlogSize);
        HttpServer2 srv = HttpServerFunctionalTest.createServer("test", conf);
        List<?> listeners = ((List<?>) (Whitebox.getInternalState(srv, "listeners")));
        ServerConnector listener = ((ServerConnector) (listeners.get(0)));
        Assert.assertEquals(backlogSize, listener.getAcceptQueueSize());
    }

    @Test
    public void testIdleTimeout() throws Exception {
        final int idleTimeout = 1000;
        Configuration conf = new Configuration();
        conf.setInt(HTTP_IDLE_TIMEOUT_MS_KEY, idleTimeout);
        HttpServer2 srv = HttpServerFunctionalTest.createServer("test", conf);
        Field f = HttpServer2.class.getDeclaredField("listeners");
        f.setAccessible(true);
        List<?> listeners = ((List<?>) (f.get(srv)));
        ServerConnector listener = ((ServerConnector) (listeners.get(0)));
        Assert.assertEquals(idleTimeout, listener.getIdleTimeout());
    }

    @Test
    public void testHttpResponseDefaultHeaders() throws Exception {
        Configuration conf = new Configuration();
        HttpServer2 httpServer = HttpServerFunctionalTest.createTestServer(conf);
        try {
            HttpURLConnection conn = getHttpURLConnection(httpServer);
            Assert.assertEquals(X_XSS_PROTECTION.split(":")[1], conn.getHeaderField(X_XSS_PROTECTION.split(":")[0]));
            Assert.assertEquals(X_CONTENT_TYPE_OPTIONS.split(":")[1], conn.getHeaderField(X_CONTENT_TYPE_OPTIONS.split(":")[0]));
        } finally {
            httpServer.stop();
        }
    }

    @Test
    public void testHttpResponseOverrideDefaultHeaders() throws Exception {
        Configuration conf = new Configuration();
        conf.set(((HTTP_HEADER_PREFIX) + (X_XSS_PROTECTION.split(":")[0])), "customXssValue");
        HttpServer2 httpServer = HttpServerFunctionalTest.createTestServer(conf);
        try {
            HttpURLConnection conn = getHttpURLConnection(httpServer);
            Assert.assertEquals("customXssValue", conn.getHeaderField(X_XSS_PROTECTION.split(":")[0]));
            Assert.assertEquals(X_CONTENT_TYPE_OPTIONS.split(":")[1], conn.getHeaderField(X_CONTENT_TYPE_OPTIONS.split(":")[0]));
        } finally {
            httpServer.stop();
        }
    }

    @Test
    public void testHttpResponseCustomHeaders() throws Exception {
        Configuration conf = new Configuration();
        String key = "customKey";
        String value = "customValue";
        conf.set(((HTTP_HEADER_PREFIX) + key), value);
        HttpServer2 httpServer = HttpServerFunctionalTest.createTestServer(conf);
        try {
            HttpURLConnection conn = getHttpURLConnection(httpServer);
            Assert.assertEquals(X_XSS_PROTECTION.split(":")[1], conn.getHeaderField(X_XSS_PROTECTION.split(":")[0]));
            Assert.assertEquals(X_CONTENT_TYPE_OPTIONS.split(":")[1], conn.getHeaderField(X_CONTENT_TYPE_OPTIONS.split(":")[0]));
            Assert.assertEquals(value, conn.getHeaderField(key));
        } finally {
            httpServer.stop();
        }
    }
}

