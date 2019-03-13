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
package org.apache.hadoop.hbase.http;


import CommonConfigurationKeys.HADOOP_SECURITY_AUTHORIZATION;
import CommonConfigurationKeys.HADOOP_SECURITY_INSTRUMENTATION_REQUIRES_ADMIN;
import HttpServer.ADMINS_ACL;
import HttpServer.CONF_CONTEXT_ATTRIBUTE;
import HttpServletResponse.SC_OK;
import HttpServletResponse.SC_UNAUTHORIZED;
import java.io.IOException;
import java.io.PrintWriter;
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
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.http.HttpServer.QuotingInputFilter.RequestQuoter;
import org.apache.hadoop.hbase.http.resource.JerseyResource;
import org.apache.hadoop.hbase.testclassification.MiscTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hadoop.net.NetUtils;
import org.apache.hadoop.security.ShellBasedUnixGroupsMapping;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.security.authorize.AccessControlList;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ MiscTests.class, SmallTests.class })
public class TestHttpServer extends HttpServerFunctionalTest {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestHttpServer.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestHttpServer.class);

    private static HttpServer server;

    private static URL baseUrl;

    // jetty 9.4.x needs this many threads to start, even in the small.
    static final int MAX_THREADS = 16;

    @SuppressWarnings("serial")
    public static class EchoMapServlet extends HttpServlet {
        @Override
        public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            PrintWriter out = response.getWriter();
            Map<String, String[]> params = request.getParameterMap();
            SortedSet<String> keys = new TreeSet<>(params.keySet());
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
        @Override
        public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            PrintWriter out = response.getWriter();
            SortedSet<String> sortedKeys = new TreeSet<>();
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
    public static class LongHeaderServlet extends HttpServlet {
        @Override
        public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            Assert.assertEquals((63 * 1024), request.getHeader("longheader").length());
            response.setStatus(SC_OK);
        }
    }

    @SuppressWarnings("serial")
    public static class HtmlContentServlet extends HttpServlet {
        @Override
        public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            response.setContentType("text/html");
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
                        Assert.assertEquals("a:b\nc:d\n", HttpServerFunctionalTest.readOutput(new URL(TestHttpServer.baseUrl, "/echo?a=b&c=d")));
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

    @Test
    public void testEcho() throws Exception {
        Assert.assertEquals("a:b\nc:d\n", HttpServerFunctionalTest.readOutput(new URL(TestHttpServer.baseUrl, "/echo?a=b&c=d")));
        Assert.assertEquals("a:b\nc&lt;:d\ne:&gt;\n", HttpServerFunctionalTest.readOutput(new URL(TestHttpServer.baseUrl, "/echo?a=b&c<=d&e=>")));
    }

    /**
     * Test the echo map servlet that uses getParameterMap.
     */
    @Test
    public void testEchoMap() throws Exception {
        Assert.assertEquals("a:b\nc:d\n", HttpServerFunctionalTest.readOutput(new URL(TestHttpServer.baseUrl, "/echomap?a=b&c=d")));
        Assert.assertEquals("a:b,&gt;\nc&lt;:d\n", HttpServerFunctionalTest.readOutput(new URL(TestHttpServer.baseUrl, "/echomap?a=b&c<=d&a=>")));
    }

    /**
     * Test that verifies headers can be up to 64K long.
     *  The test adds a 63K header leaving 1K for other headers.
     *  This is because the header buffer setting is for ALL headers,
     *  names and values included.
     */
    @Test
    public void testLongHeader() throws Exception {
        URL url = new URL(TestHttpServer.baseUrl, "/longheader");
        HttpURLConnection conn = ((HttpURLConnection) (url.openConnection()));
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < (63 * 1024); i++) {
            sb.append("a");
        }
        conn.setRequestProperty("longheader", sb.toString());
        Assert.assertEquals(HttpURLConnection.HTTP_OK, conn.getResponseCode());
    }

    @Test
    public void testContentTypes() throws Exception {
        // Static CSS files should have text/css
        URL cssUrl = new URL(TestHttpServer.baseUrl, "/static/test.css");
        HttpURLConnection conn = ((HttpURLConnection) (cssUrl.openConnection()));
        conn.connect();
        Assert.assertEquals(200, conn.getResponseCode());
        Assert.assertEquals("text/css", conn.getContentType());
        // Servlets should have text/plain with proper encoding by default
        URL servletUrl = new URL(TestHttpServer.baseUrl, "/echo?a=b");
        conn = ((HttpURLConnection) (servletUrl.openConnection()));
        conn.connect();
        Assert.assertEquals(200, conn.getResponseCode());
        Assert.assertEquals("text/plain;charset=utf-8", conn.getContentType());
        // We should ignore parameters for mime types - ie a parameter
        // ending in .css should not change mime type
        servletUrl = new URL(TestHttpServer.baseUrl, "/echo?a=b.css");
        conn = ((HttpURLConnection) (servletUrl.openConnection()));
        conn.connect();
        Assert.assertEquals(200, conn.getResponseCode());
        Assert.assertEquals("text/plain;charset=utf-8", conn.getContentType());
        // Servlets that specify text/html should get that content type
        servletUrl = new URL(TestHttpServer.baseUrl, "/htmlcontent");
        conn = ((HttpURLConnection) (servletUrl.openConnection()));
        conn.connect();
        Assert.assertEquals(200, conn.getResponseCode());
        Assert.assertEquals("text/html;charset=utf-8", conn.getContentType());
        // JSPs should default to text/html with utf8
        // JSPs do not work from unit tests
        // servletUrl = new URL(baseUrl, "/testjsp.jsp");
        // conn = (HttpURLConnection)servletUrl.openConnection();
        // conn.connect();
        // assertEquals(200, conn.getResponseCode());
        // assertEquals("text/html; charset=utf-8", conn.getContentType());
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
        static Map<String, List<String>> mapping = new HashMap<>();

        static void clearMapping() {
            TestHttpServer.MyGroupsProvider.mapping.clear();
        }

        @Override
        public List<String> getGroups(String user) throws IOException {
            return TestHttpServer.MyGroupsProvider.mapping.get(user);
        }
    }

    @Test
    public void testRequestQuoterWithNull() throws Exception {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.doReturn(null).when(request).getParameterValues("dummy");
        RequestQuoter requestQuoter = new RequestQuoter(request);
        String[] parameterValues = requestQuoter.getParameterValues("dummy");
        Assert.assertEquals(("It should return null " + "when there are no values for the parameter"), null, parameterValues);
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
        final String js = HttpServerFunctionalTest.readOutput(new URL(TestHttpServer.baseUrl, "/jersey/foo?op=bar"));
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
        Assert.assertTrue(HttpServer.hasAdministratorAccess(context, request, response));
        // authorization ON & user NULL
        response = Mockito.mock(HttpServletResponse.class);
        conf.setBoolean(HADOOP_SECURITY_AUTHORIZATION, true);
        Assert.assertFalse(HttpServer.hasAdministratorAccess(context, request, response));
        Mockito.verify(response).sendError(Mockito.eq(SC_UNAUTHORIZED), Mockito.anyString());
        // authorization ON & user NOT NULL & ACLs NULL
        response = Mockito.mock(HttpServletResponse.class);
        Mockito.when(request.getRemoteUser()).thenReturn("foo");
        Assert.assertTrue(HttpServer.hasAdministratorAccess(context, request, response));
        // authorization ON & user NOT NULL & ACLs NOT NULL & user not in ACLs
        response = Mockito.mock(HttpServletResponse.class);
        AccessControlList acls = Mockito.mock(AccessControlList.class);
        Mockito.when(acls.isUserAllowed(Mockito.<UserGroupInformation>any())).thenReturn(false);
        Mockito.when(context.getAttribute(ADMINS_ACL)).thenReturn(acls);
        Assert.assertFalse(HttpServer.hasAdministratorAccess(context, request, response));
        Mockito.verify(response).sendError(Mockito.eq(SC_UNAUTHORIZED), Mockito.anyString());
        // authorization ON & user NOT NULL & ACLs NOT NULL & user in in ACLs
        response = Mockito.mock(HttpServletResponse.class);
        Mockito.when(acls.isUserAllowed(Mockito.<UserGroupInformation>any())).thenReturn(true);
        Mockito.when(context.getAttribute(ADMINS_ACL)).thenReturn(acls);
        Assert.assertTrue(HttpServer.hasAdministratorAccess(context, request, response));
    }

    @Test
    public void testRequiresAuthorizationAccess() throws Exception {
        Configuration conf = new Configuration();
        ServletContext context = Mockito.mock(ServletContext.class);
        Mockito.when(context.getAttribute(CONF_CONTEXT_ATTRIBUTE)).thenReturn(conf);
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        // requires admin access to instrumentation, FALSE by default
        Assert.assertTrue(HttpServer.isInstrumentationAccessAllowed(context, request, response));
        // requires admin access to instrumentation, TRUE
        conf.setBoolean(HADOOP_SECURITY_INSTRUMENTATION_REQUIRES_ADMIN, true);
        conf.setBoolean(HADOOP_SECURITY_AUTHORIZATION, true);
        AccessControlList acls = Mockito.mock(AccessControlList.class);
        Mockito.when(acls.isUserAllowed(Mockito.<UserGroupInformation>any())).thenReturn(false);
        Mockito.when(context.getAttribute(ADMINS_ACL)).thenReturn(acls);
        Assert.assertFalse(HttpServer.isInstrumentationAccessAllowed(context, request, response));
    }

    @Test
    public void testBindAddress() throws Exception {
        checkBindAddress("localhost", 0, false).stop();
        // hang onto this one for a bit more testing
        HttpServer myServer = checkBindAddress("localhost", 0, false);
        HttpServer myServer2 = null;
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
    public void testXFrameHeaderSameOrigin() throws Exception {
        Configuration conf = new Configuration();
        conf.set("hbase.http.filter.xframeoptions.mode", "SAMEORIGIN");
        HttpServer myServer = new HttpServer.Builder().setName("test").addEndpoint(new URI("http://localhost:0")).setFindPort(true).setConf(conf).build();
        myServer.setAttribute(CONF_CONTEXT_ATTRIBUTE, conf);
        myServer.addServlet("echo", "/echo", TestHttpServer.EchoServlet.class);
        myServer.start();
        String serverURL = "http://" + (NetUtils.getHostPortString(myServer.getConnectorAddress(0)));
        URL url = new URL(new URL(serverURL), "/echo?a=b&c=d");
        HttpURLConnection conn = ((HttpURLConnection) (url.openConnection()));
        Assert.assertEquals(HttpURLConnection.HTTP_OK, conn.getResponseCode());
        Assert.assertEquals("SAMEORIGIN", conn.getHeaderField("X-Frame-Options"));
        myServer.stop();
    }

    @Test
    public void testNoCacheHeader() throws Exception {
        URL url = new URL(TestHttpServer.baseUrl, "/echo?a=b&c=d");
        HttpURLConnection conn = ((HttpURLConnection) (url.openConnection()));
        Assert.assertEquals(HttpURLConnection.HTTP_OK, conn.getResponseCode());
        Assert.assertEquals("no-cache", conn.getHeaderField("Cache-Control"));
        Assert.assertEquals("no-cache", conn.getHeaderField("Pragma"));
        Assert.assertNotNull(conn.getHeaderField("Expires"));
        Assert.assertNotNull(conn.getHeaderField("Date"));
        Assert.assertEquals(conn.getHeaderField("Expires"), conn.getHeaderField("Date"));
        Assert.assertEquals("DENY", conn.getHeaderField("X-Frame-Options"));
    }

    @Test
    public void testHttpMethods() throws Exception {
        // HTTP TRACE method should be disabled for security
        // See https://www.owasp.org/index.php/Cross_Site_Tracing
        URL url = new URL(TestHttpServer.baseUrl, "/echo?a=b");
        HttpURLConnection conn = ((HttpURLConnection) (url.openConnection()));
        conn.setRequestMethod("TRACE");
        conn.connect();
        Assert.assertEquals(HttpURLConnection.HTTP_FORBIDDEN, conn.getResponseCode());
    }
}

