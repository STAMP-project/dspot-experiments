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
package org.apache.hadoop.security.token.delegation.web;


import DelegationTokenAuthenticatedURL.Token;
import DelegationTokenAuthenticationFilter.DELEGATION_TOKEN_SECRET_MANAGER_ATTR;
import DelegationTokenAuthenticator.DELEGATION_TOKEN_HEADER;
import DispatcherType.REQUEST;
import HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import HttpServletResponse.SC_OK;
import HttpServletResponse.SC_UNAUTHORIZED;
import KerberosAuthenticationHandler.KEYTAB;
import KerberosAuthenticationHandler.PRINCIPAL;
import KerberosAuthenticator.WWW_AUTHENTICATE;
import PseudoAuthenticationHandler.TYPE;
import UserGroupInformation.AuthenticationMethod;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.PrivilegedExceptionAction;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;
import javax.security.auth.login.org.apache.hadoop.conf.Configuration;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.security.authentication.client.AuthenticationException;
import org.apache.hadoop.security.authentication.server.AuthenticationFilter;
import org.apache.hadoop.security.authentication.server.AuthenticationHandler;
import org.apache.hadoop.security.authentication.server.AuthenticationToken;
import org.apache.hadoop.security.authentication.util.KerberosUtil;
import org.apache.hadoop.security.token.delegation.AbstractDelegationTokenSecretManager;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.junit.Assert;
import org.junit.Test;

import static DelegationTokenAuthenticator.DELEGATION_PARAM;
import static javax.security.auth.login.AppConfigurationEntry.LoginModuleControlFlag.REQUIRED;


public class TestWebDelegationToken {
    private static final String OK_USER = "ok-user";

    private static final String FAIL_USER = "fail-user";

    private static final String FOO_USER = "foo";

    private Server jetty;

    public static class DummyAuthenticationHandler implements AuthenticationHandler {
        @Override
        public String getType() {
            return "dummy";
        }

        @Override
        public void init(Properties config) throws ServletException {
        }

        @Override
        public void destroy() {
        }

        @Override
        public boolean managementOperation(AuthenticationToken token, HttpServletRequest request, HttpServletResponse response) throws IOException, AuthenticationException {
            return false;
        }

        @Override
        public AuthenticationToken authenticate(HttpServletRequest request, HttpServletResponse response) throws IOException, AuthenticationException {
            AuthenticationToken token = null;
            if ((request.getParameter("authenticated")) != null) {
                token = new AuthenticationToken(request.getParameter("authenticated"), "U", "test");
            } else {
                response.setStatus(SC_UNAUTHORIZED);
                response.setHeader(WWW_AUTHENTICATE, "dummy");
            }
            return token;
        }
    }

    public static class DummyDelegationTokenAuthenticationHandler extends DelegationTokenAuthenticationHandler {
        public DummyDelegationTokenAuthenticationHandler() {
            super(new TestWebDelegationToken.DummyAuthenticationHandler());
        }

        @Override
        public void init(Properties config) throws ServletException {
            Properties conf = new Properties(config);
            conf.setProperty(TOKEN_KIND, "token-kind");
            initTokenManager(conf);
        }
    }

    public static class AFilter extends DelegationTokenAuthenticationFilter {
        @Override
        protected Properties getConfiguration(String configPrefix, FilterConfig filterConfig) {
            Properties conf = new Properties();
            conf.setProperty(AUTH_TYPE, TestWebDelegationToken.DummyDelegationTokenAuthenticationHandler.class.getName());
            return conf;
        }
    }

    public static class PingServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
            resp.setStatus(SC_OK);
            resp.getWriter().write("ping");
            if ((req.getHeader(DELEGATION_TOKEN_HEADER)) != null) {
                resp.setHeader("UsingHeader", "true");
            }
            if (((req.getQueryString()) != null) && (req.getQueryString().contains(((DELEGATION_PARAM) + "=")))) {
                resp.setHeader("UsingQueryString", "true");
            }
        }

        @Override
        protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
            Writer writer = resp.getWriter();
            writer.write("ping: ");
            IOUtils.copy(req.getReader(), writer);
            resp.setStatus(SC_OK);
        }
    }

    @Test
    public void testRawHttpCalls() throws Exception {
        final Server jetty = createJettyServer();
        ServletContextHandler context = new ServletContextHandler();
        context.setContextPath("/foo");
        jetty.setHandler(context);
        context.addFilter(new FilterHolder(TestWebDelegationToken.AFilter.class), "/*", EnumSet.of(REQUEST));
        context.addServlet(new ServletHolder(TestWebDelegationToken.PingServlet.class), "/bar");
        try {
            jetty.start();
            URL nonAuthURL = new URL(((getJettyURL()) + "/foo/bar"));
            URL authURL = new URL(((getJettyURL()) + "/foo/bar?authenticated=foo"));
            // unauthenticated access to URL
            HttpURLConnection conn = ((HttpURLConnection) (nonAuthURL.openConnection()));
            Assert.assertEquals(HttpURLConnection.HTTP_UNAUTHORIZED, conn.getResponseCode());
            // authenticated access to URL
            conn = ((HttpURLConnection) (authURL.openConnection()));
            Assert.assertEquals(HttpURLConnection.HTTP_OK, conn.getResponseCode());
            // unauthenticated access to get delegation token
            URL url = new URL(((nonAuthURL.toExternalForm()) + "?op=GETDELEGATIONTOKEN"));
            conn = ((HttpURLConnection) (url.openConnection()));
            Assert.assertEquals(HttpURLConnection.HTTP_UNAUTHORIZED, conn.getResponseCode());
            // authenticated access to get delegation token
            url = new URL(((authURL.toExternalForm()) + "&op=GETDELEGATIONTOKEN&renewer=foo"));
            conn = ((HttpURLConnection) (url.openConnection()));
            Assert.assertEquals(HttpURLConnection.HTTP_OK, conn.getResponseCode());
            ObjectMapper mapper = new ObjectMapper();
            Map map = mapper.readValue(conn.getInputStream(), Map.class);
            String dt = ((String) (((Map) (map.get("Token"))).get("urlString")));
            Assert.assertNotNull(dt);
            // delegation token access to URL
            url = new URL((((nonAuthURL.toExternalForm()) + "?delegation=") + dt));
            conn = ((HttpURLConnection) (url.openConnection()));
            Assert.assertEquals(HttpURLConnection.HTTP_OK, conn.getResponseCode());
            // delegation token and authenticated access to URL
            url = new URL((((authURL.toExternalForm()) + "&delegation=") + dt));
            conn = ((HttpURLConnection) (url.openConnection()));
            Assert.assertEquals(HttpURLConnection.HTTP_OK, conn.getResponseCode());
            // renewew delegation token, unauthenticated access to URL
            url = new URL((((nonAuthURL.toExternalForm()) + "?op=RENEWDELEGATIONTOKEN&token=") + dt));
            conn = ((HttpURLConnection) (url.openConnection()));
            conn.setRequestMethod("PUT");
            Assert.assertEquals(HttpURLConnection.HTTP_UNAUTHORIZED, conn.getResponseCode());
            // renewew delegation token, authenticated access to URL
            url = new URL((((authURL.toExternalForm()) + "&op=RENEWDELEGATIONTOKEN&token=") + dt));
            conn = ((HttpURLConnection) (url.openConnection()));
            conn.setRequestMethod("PUT");
            Assert.assertEquals(HttpURLConnection.HTTP_OK, conn.getResponseCode());
            // renewew delegation token, authenticated access to URL, not renewer
            url = new URL((((getJettyURL()) + "/foo/bar?authenticated=bar&op=RENEWDELEGATIONTOKEN&token=") + dt));
            conn = ((HttpURLConnection) (url.openConnection()));
            conn.setRequestMethod("PUT");
            Assert.assertEquals(HttpURLConnection.HTTP_FORBIDDEN, conn.getResponseCode());
            // cancel delegation token, nonauthenticated access to URL
            url = new URL((((nonAuthURL.toExternalForm()) + "?op=CANCELDELEGATIONTOKEN&token=") + dt));
            conn = ((HttpURLConnection) (url.openConnection()));
            conn.setRequestMethod("PUT");
            Assert.assertEquals(HttpURLConnection.HTTP_OK, conn.getResponseCode());
            // cancel canceled delegation token, nonauthenticated access to URL
            url = new URL((((nonAuthURL.toExternalForm()) + "?op=CANCELDELEGATIONTOKEN&token=") + dt));
            conn = ((HttpURLConnection) (url.openConnection()));
            conn.setRequestMethod("PUT");
            Assert.assertEquals(HttpURLConnection.HTTP_NOT_FOUND, conn.getResponseCode());
            // get new delegation token
            url = new URL(((authURL.toExternalForm()) + "&op=GETDELEGATIONTOKEN&renewer=foo"));
            conn = ((HttpURLConnection) (url.openConnection()));
            Assert.assertEquals(HttpURLConnection.HTTP_OK, conn.getResponseCode());
            mapper = new ObjectMapper();
            map = mapper.readValue(conn.getInputStream(), Map.class);
            dt = ((String) (((Map) (map.get("Token"))).get("urlString")));
            Assert.assertNotNull(dt);
            // cancel delegation token, authenticated access to URL
            url = new URL((((authURL.toExternalForm()) + "&op=CANCELDELEGATIONTOKEN&token=") + dt));
            conn = ((HttpURLConnection) (url.openConnection()));
            conn.setRequestMethod("PUT");
            Assert.assertEquals(HttpURLConnection.HTTP_OK, conn.getResponseCode());
        } finally {
            jetty.stop();
        }
    }

    @Test
    public void testDelegationTokenAuthenticatorCallsWithHeader() throws Exception {
        testDelegationTokenAuthenticatorCalls(false);
    }

    @Test
    public void testDelegationTokenAuthenticatorCallsWithQueryString() throws Exception {
        testDelegationTokenAuthenticatorCalls(true);
    }

    private static class DummyDelegationTokenSecretManager extends AbstractDelegationTokenSecretManager<DelegationTokenIdentifier> {
        public DummyDelegationTokenSecretManager() {
            super(10000, 10000, 10000, 10000);
        }

        @Override
        public DelegationTokenIdentifier createIdentifier() {
            return new DelegationTokenIdentifier(new Text("fooKind"));
        }
    }

    @Test
    public void testExternalDelegationTokenSecretManager() throws Exception {
        TestWebDelegationToken.DummyDelegationTokenSecretManager secretMgr = new TestWebDelegationToken.DummyDelegationTokenSecretManager();
        final Server jetty = createJettyServer();
        ServletContextHandler context = new ServletContextHandler();
        context.setContextPath("/foo");
        jetty.setHandler(context);
        context.addFilter(new FilterHolder(TestWebDelegationToken.AFilter.class), "/*", EnumSet.of(REQUEST));
        context.addServlet(new ServletHolder(TestWebDelegationToken.PingServlet.class), "/bar");
        try {
            startThreads();
            context.setAttribute(DELEGATION_TOKEN_SECRET_MANAGER_ATTR, secretMgr);
            jetty.start();
            URL authURL = new URL(((getJettyURL()) + "/foo/bar?authenticated=foo"));
            DelegationTokenAuthenticatedURL.Token token = new DelegationTokenAuthenticatedURL.Token();
            DelegationTokenAuthenticatedURL aUrl = new DelegationTokenAuthenticatedURL();
            aUrl.getDelegationToken(authURL, token, TestWebDelegationToken.FOO_USER);
            Assert.assertNotNull(token.getDelegationToken());
            Assert.assertEquals(new Text("fooKind"), token.getDelegationToken().getKind());
        } finally {
            jetty.stop();
            stopThreads();
        }
    }

    public static class NoDTFilter extends AuthenticationFilter {
        @Override
        protected Properties getConfiguration(String configPrefix, FilterConfig filterConfig) {
            Properties conf = new Properties();
            conf.setProperty(AUTH_TYPE, TYPE);
            return conf;
        }
    }

    public static class NoDTHandlerDTAFilter extends DelegationTokenAuthenticationFilter {
        @Override
        protected Properties getConfiguration(String configPrefix, FilterConfig filterConfig) {
            Properties conf = new Properties();
            conf.setProperty(AUTH_TYPE, TYPE);
            return conf;
        }
    }

    public static class UserServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
            resp.setStatus(SC_OK);
            resp.getWriter().write(req.getUserPrincipal().getName());
        }
    }

    @Test
    public void testDelegationTokenAuthenticationURLWithNoDTFilter() throws Exception {
        testDelegationTokenAuthenticatedURLWithNoDT(TestWebDelegationToken.NoDTFilter.class);
    }

    @Test
    public void testDelegationTokenAuthenticationURLWithNoDTHandler() throws Exception {
        testDelegationTokenAuthenticatedURLWithNoDT(TestWebDelegationToken.NoDTHandlerDTAFilter.class);
    }

    public static class PseudoDTAFilter extends DelegationTokenAuthenticationFilter {
        @Override
        protected Properties getConfiguration(String configPrefix, FilterConfig filterConfig) {
            Properties conf = new Properties();
            conf.setProperty(AUTH_TYPE, PseudoDelegationTokenAuthenticationHandler.class.getName());
            conf.setProperty(DelegationTokenAuthenticationHandler.TOKEN_KIND, "token-kind");
            return conf;
        }

        @Override
        protected Configuration getProxyuserConfiguration(FilterConfig filterConfig) throws ServletException {
            org.apache.hadoop.conf.Configuration conf = new org.apache.hadoop.conf.Configuration(false);
            conf.set("proxyuser.foo.users", TestWebDelegationToken.OK_USER);
            conf.set("proxyuser.foo.hosts", "localhost");
            return conf;
        }
    }

    @Test
    public void testFallbackToPseudoDelegationTokenAuthenticator() throws Exception {
        final Server jetty = createJettyServer();
        ServletContextHandler context = new ServletContextHandler();
        context.setContextPath("/foo");
        jetty.setHandler(context);
        context.addFilter(new FilterHolder(TestWebDelegationToken.PseudoDTAFilter.class), "/*", EnumSet.of(REQUEST));
        context.addServlet(new ServletHolder(TestWebDelegationToken.UserServlet.class), "/bar");
        try {
            jetty.start();
            final URL url = new URL(((getJettyURL()) + "/foo/bar"));
            UserGroupInformation ugi = UserGroupInformation.createRemoteUser(TestWebDelegationToken.FOO_USER);
            ugi.doAs(new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws Exception {
                    DelegationTokenAuthenticatedURL.Token token = new DelegationTokenAuthenticatedURL.Token();
                    DelegationTokenAuthenticatedURL aUrl = new DelegationTokenAuthenticatedURL();
                    HttpURLConnection conn = aUrl.openConnection(url, token);
                    Assert.assertEquals(HttpURLConnection.HTTP_OK, conn.getResponseCode());
                    List<String> ret = IOUtils.readLines(conn.getInputStream());
                    Assert.assertEquals(1, ret.size());
                    Assert.assertEquals(TestWebDelegationToken.FOO_USER, ret.get(0));
                    aUrl.getDelegationToken(url, token, TestWebDelegationToken.FOO_USER);
                    Assert.assertNotNull(token.getDelegationToken());
                    Assert.assertEquals(new Text("token-kind"), token.getDelegationToken().getKind());
                    return null;
                }
            });
        } finally {
            jetty.stop();
        }
    }

    public static class KDTAFilter extends DelegationTokenAuthenticationFilter {
        static String keytabFile;

        @Override
        protected Properties getConfiguration(String configPrefix, FilterConfig filterConfig) {
            Properties conf = new Properties();
            conf.setProperty(AUTH_TYPE, KerberosDelegationTokenAuthenticationHandler.class.getName());
            conf.setProperty(KEYTAB, TestWebDelegationToken.KDTAFilter.keytabFile);
            conf.setProperty(PRINCIPAL, "HTTP/localhost");
            conf.setProperty(KerberosDelegationTokenAuthenticationHandler.TOKEN_KIND, "token-kind");
            return conf;
        }

        @Override
        protected Configuration getProxyuserConfiguration(FilterConfig filterConfig) throws ServletException {
            org.apache.hadoop.conf.Configuration conf = new org.apache.hadoop.conf.Configuration(false);
            conf.set("proxyuser.client.users", TestWebDelegationToken.OK_USER);
            conf.set("proxyuser.client.hosts", "127.0.0.1");
            return conf;
        }
    }

    private static class KerberosConfiguration extends Configuration {
        private String principal;

        private String keytab;

        public KerberosConfiguration(String principal, String keytab) {
            this.principal = principal;
            this.keytab = keytab;
        }

        @Override
        public AppConfigurationEntry[] getAppConfigurationEntry(String name) {
            Map<String, String> options = new HashMap<String, String>();
            options.put("principal", principal);
            options.put("keyTab", keytab);
            options.put("useKeyTab", "true");
            options.put("storeKey", "true");
            options.put("doNotPrompt", "true");
            options.put("useTicketCache", "true");
            options.put("renewTGT", "true");
            options.put("refreshKrb5Config", "true");
            options.put("isInitiator", "true");
            String ticketCache = System.getenv("KRB5CCNAME");
            if (ticketCache != null) {
                options.put("ticketCache", ticketCache);
            }
            options.put("debug", "true");
            return new AppConfigurationEntry[]{ new AppConfigurationEntry(KerberosUtil.getKrb5LoginModuleName(), REQUIRED, options) };
        }
    }

    @Test
    public void testKerberosDelegationTokenAuthenticator() throws Exception {
        testKerberosDelegationTokenAuthenticator(false);
    }

    @Test
    public void testKerberosDelegationTokenAuthenticatorWithDoAs() throws Exception {
        testKerberosDelegationTokenAuthenticator(true);
    }

    @Test
    public void testProxyUser() throws Exception {
        final Server jetty = createJettyServer();
        ServletContextHandler context = new ServletContextHandler();
        context.setContextPath("/foo");
        jetty.setHandler(context);
        context.addFilter(new FilterHolder(TestWebDelegationToken.PseudoDTAFilter.class), "/*", EnumSet.of(REQUEST));
        context.addServlet(new ServletHolder(TestWebDelegationToken.UserServlet.class), "/bar");
        try {
            jetty.start();
            final URL url = new URL(((getJettyURL()) + "/foo/bar"));
            // proxyuser using raw HTTP, verifying doAs is case insensitive
            String strUrl = String.format("%s?user.name=%s&doas=%s", url.toExternalForm(), TestWebDelegationToken.FOO_USER, TestWebDelegationToken.OK_USER);
            HttpURLConnection conn = ((HttpURLConnection) (new URL(strUrl).openConnection()));
            Assert.assertEquals(HttpURLConnection.HTTP_OK, conn.getResponseCode());
            List<String> ret = IOUtils.readLines(conn.getInputStream());
            Assert.assertEquals(1, ret.size());
            Assert.assertEquals(TestWebDelegationToken.OK_USER, ret.get(0));
            strUrl = String.format("%s?user.name=%s&DOAS=%s", url.toExternalForm(), TestWebDelegationToken.FOO_USER, TestWebDelegationToken.OK_USER);
            conn = ((HttpURLConnection) (new URL(strUrl).openConnection()));
            Assert.assertEquals(HttpURLConnection.HTTP_OK, conn.getResponseCode());
            ret = IOUtils.readLines(conn.getInputStream());
            Assert.assertEquals(1, ret.size());
            Assert.assertEquals(TestWebDelegationToken.OK_USER, ret.get(0));
            UserGroupInformation ugi = UserGroupInformation.createRemoteUser(TestWebDelegationToken.FOO_USER);
            ugi.doAs(new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws Exception {
                    DelegationTokenAuthenticatedURL.Token token = new DelegationTokenAuthenticatedURL.Token();
                    DelegationTokenAuthenticatedURL aUrl = new DelegationTokenAuthenticatedURL();
                    // proxyuser using authentication handler authentication
                    HttpURLConnection conn = aUrl.openConnection(url, token, TestWebDelegationToken.OK_USER);
                    Assert.assertEquals(HttpURLConnection.HTTP_OK, conn.getResponseCode());
                    List<String> ret = IOUtils.readLines(conn.getInputStream());
                    Assert.assertEquals(1, ret.size());
                    Assert.assertEquals(TestWebDelegationToken.OK_USER, ret.get(0));
                    // unauthorized proxy user using authentication handler authentication
                    conn = aUrl.openConnection(url, token, TestWebDelegationToken.FAIL_USER);
                    Assert.assertEquals(HttpURLConnection.HTTP_FORBIDDEN, conn.getResponseCode());
                    // proxy using delegation token authentication
                    aUrl.getDelegationToken(url, token, TestWebDelegationToken.FOO_USER);
                    UserGroupInformation ugi = UserGroupInformation.getCurrentUser();
                    ugi.addToken(token.getDelegationToken());
                    token = new DelegationTokenAuthenticatedURL.Token();
                    // requests using delegation token as auth do not honor doAs
                    conn = aUrl.openConnection(url, token, TestWebDelegationToken.OK_USER);
                    Assert.assertEquals(HttpURLConnection.HTTP_OK, conn.getResponseCode());
                    ret = IOUtils.readLines(conn.getInputStream());
                    Assert.assertEquals(1, ret.size());
                    Assert.assertEquals(TestWebDelegationToken.FOO_USER, ret.get(0));
                    return null;
                }
            });
        } finally {
            jetty.stop();
        }
    }

    public static class UGIServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
            UserGroupInformation ugi = HttpUserGroupInformation.get();
            if (ugi != null) {
                String ret = (("remoteuser=" + (req.getRemoteUser())) + ":ugi=") + (ugi.getShortUserName());
                if ((ugi.getAuthenticationMethod()) == (AuthenticationMethod.PROXY)) {
                    ret = (("realugi=" + (ugi.getRealUser().getShortUserName())) + ":") + ret;
                }
                resp.setStatus(SC_OK);
                resp.getWriter().write(ret);
            } else {
                resp.setStatus(SC_INTERNAL_SERVER_ERROR);
            }
        }
    }

    @Test
    public void testHttpUGI() throws Exception {
        final Server jetty = createJettyServer();
        ServletContextHandler context = new ServletContextHandler();
        context.setContextPath("/foo");
        jetty.setHandler(context);
        context.addFilter(new FilterHolder(TestWebDelegationToken.PseudoDTAFilter.class), "/*", EnumSet.of(REQUEST));
        context.addServlet(new ServletHolder(TestWebDelegationToken.UGIServlet.class), "/bar");
        try {
            jetty.start();
            final URL url = new URL(((getJettyURL()) + "/foo/bar"));
            UserGroupInformation ugi = UserGroupInformation.createRemoteUser(TestWebDelegationToken.FOO_USER);
            ugi.doAs(new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws Exception {
                    DelegationTokenAuthenticatedURL.Token token = new DelegationTokenAuthenticatedURL.Token();
                    DelegationTokenAuthenticatedURL aUrl = new DelegationTokenAuthenticatedURL();
                    // user foo
                    HttpURLConnection conn = aUrl.openConnection(url, token);
                    Assert.assertEquals(HttpURLConnection.HTTP_OK, conn.getResponseCode());
                    List<String> ret = IOUtils.readLines(conn.getInputStream());
                    Assert.assertEquals(1, ret.size());
                    Assert.assertEquals(((("remoteuser=" + (TestWebDelegationToken.FOO_USER)) + ":ugi=") + (TestWebDelegationToken.FOO_USER)), ret.get(0));
                    // user ok-user via proxyuser foo
                    conn = aUrl.openConnection(url, token, TestWebDelegationToken.OK_USER);
                    Assert.assertEquals(HttpURLConnection.HTTP_OK, conn.getResponseCode());
                    ret = IOUtils.readLines(conn.getInputStream());
                    Assert.assertEquals(1, ret.size());
                    Assert.assertEquals(((((("realugi=" + (TestWebDelegationToken.FOO_USER)) + ":remoteuser=") + (TestWebDelegationToken.OK_USER)) + ":ugi=") + (TestWebDelegationToken.OK_USER)), ret.get(0));
                    return null;
                }
            });
        } finally {
            jetty.stop();
        }
    }

    public static class IpAddressBasedPseudoDTAFilter extends TestWebDelegationToken.PseudoDTAFilter {
        @Override
        protected Configuration getProxyuserConfiguration(FilterConfig filterConfig) throws ServletException {
            org.apache.hadoop.conf.Configuration configuration = super.getProxyuserConfiguration(filterConfig);
            configuration.set("proxyuser.foo.hosts", "127.0.0.1");
            return configuration;
        }
    }

    @Test
    public void testIpaddressCheck() throws Exception {
        final Server jetty = createJettyServer();
        ServletContextHandler context = new ServletContextHandler();
        context.setContextPath("/foo");
        jetty.setHandler(context);
        context.addFilter(new FilterHolder(TestWebDelegationToken.IpAddressBasedPseudoDTAFilter.class), "/*", EnumSet.of(REQUEST));
        context.addServlet(new ServletHolder(TestWebDelegationToken.UGIServlet.class), "/bar");
        try {
            jetty.start();
            final URL url = new URL(((getJettyURL()) + "/foo/bar"));
            UserGroupInformation ugi = UserGroupInformation.createRemoteUser(TestWebDelegationToken.FOO_USER);
            ugi.doAs(new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws Exception {
                    DelegationTokenAuthenticatedURL.Token token = new DelegationTokenAuthenticatedURL.Token();
                    DelegationTokenAuthenticatedURL aUrl = new DelegationTokenAuthenticatedURL();
                    // user ok-user via proxyuser foo
                    HttpURLConnection conn = aUrl.openConnection(url, token, TestWebDelegationToken.OK_USER);
                    Assert.assertEquals(HttpURLConnection.HTTP_OK, conn.getResponseCode());
                    List<String> ret = IOUtils.readLines(conn.getInputStream());
                    Assert.assertEquals(1, ret.size());
                    Assert.assertEquals(((((("realugi=" + (TestWebDelegationToken.FOO_USER)) + ":remoteuser=") + (TestWebDelegationToken.OK_USER)) + ":ugi=") + (TestWebDelegationToken.OK_USER)), ret.get(0));
                    return null;
                }
            });
        } finally {
            jetty.stop();
        }
    }
}

