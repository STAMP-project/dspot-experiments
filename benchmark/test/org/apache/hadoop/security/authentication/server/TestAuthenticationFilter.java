/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. See accompanying LICENSE file.
 */
package org.apache.hadoop.security.authentication.server;


import AuthenticatedURL.AUTH_COOKIE;
import AuthenticationFilter.AUTH_TOKEN_VALIDITY;
import AuthenticationFilter.AUTH_TYPE;
import AuthenticationFilter.CONFIG_PREFIX;
import AuthenticationFilter.COOKIE_DOMAIN;
import AuthenticationFilter.COOKIE_PATH;
import AuthenticationFilter.SIGNATURE_SECRET;
import AuthenticationFilter.SIGNATURE_SECRET_FILE;
import AuthenticationFilter.SIGNER_SECRET_PROVIDER_ATTRIBUTE;
import HttpServletResponse.SC_ACCEPTED;
import HttpServletResponse.SC_FORBIDDEN;
import HttpServletResponse.SC_UNAUTHORIZED;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.hadoop.security.authentication.client.AuthenticatedURL;
import org.apache.hadoop.security.authentication.client.AuthenticationException;
import org.apache.hadoop.security.authentication.util.Signer;
import org.apache.hadoop.security.authentication.util.SignerSecretProvider;
import org.apache.hadoop.security.authentication.util.StringSignerSecretProviderCreator;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


public class TestAuthenticationFilter {
    private static final long TOKEN_VALIDITY_SEC = 1000;

    private static final long TOKEN_MAX_INACTIVE_INTERVAL = 1000;

    @Test
    public void testGetConfiguration() throws Exception {
        AuthenticationFilter filter = new AuthenticationFilter();
        FilterConfig config = Mockito.mock(FilterConfig.class);
        Mockito.when(config.getInitParameter(CONFIG_PREFIX)).thenReturn("");
        Mockito.when(config.getInitParameter("a")).thenReturn("A");
        Mockito.when(config.getInitParameterNames()).thenReturn(new Vector<String>(Arrays.asList("a")).elements());
        Properties props = filter.getConfiguration("", config);
        Assert.assertEquals("A", props.getProperty("a"));
        config = Mockito.mock(FilterConfig.class);
        Mockito.when(config.getInitParameter(CONFIG_PREFIX)).thenReturn("foo");
        Mockito.when(config.getInitParameter("foo.a")).thenReturn("A");
        Mockito.when(config.getInitParameterNames()).thenReturn(new Vector<String>(Arrays.asList("foo.a")).elements());
        props = filter.getConfiguration("foo.", config);
        Assert.assertEquals("A", props.getProperty("a"));
    }

    @Test
    public void testInitEmpty() throws Exception {
        AuthenticationFilter filter = new AuthenticationFilter();
        try {
            FilterConfig config = Mockito.mock(FilterConfig.class);
            Mockito.when(config.getInitParameterNames()).thenReturn(new Vector<String>().elements());
            filter.init(config);
            Assert.fail();
        } catch (ServletException ex) {
            // Expected
            Assert.assertEquals("Authentication type must be specified: simple|kerberos|<class>", ex.getMessage());
        } catch (Exception ex) {
            Assert.fail();
        } finally {
            filter.destroy();
        }
    }

    public static class DummyAuthenticationHandler implements AuthenticationHandler {
        public static boolean init;

        public static boolean managementOperationReturn;

        public static boolean destroy;

        public static boolean expired;

        public static final String TYPE = "dummy";

        public static void reset() {
            TestAuthenticationFilter.DummyAuthenticationHandler.init = false;
            TestAuthenticationFilter.DummyAuthenticationHandler.destroy = false;
        }

        @Override
        public void init(Properties config) throws ServletException {
            TestAuthenticationFilter.DummyAuthenticationHandler.init = true;
            TestAuthenticationFilter.DummyAuthenticationHandler.managementOperationReturn = config.getProperty("management.operation.return", "true").equals("true");
            TestAuthenticationFilter.DummyAuthenticationHandler.expired = config.getProperty("expired.token", "false").equals("true");
        }

        @Override
        public boolean managementOperation(AuthenticationToken token, HttpServletRequest request, HttpServletResponse response) throws IOException, AuthenticationException {
            if (!(TestAuthenticationFilter.DummyAuthenticationHandler.managementOperationReturn)) {
                response.setStatus(SC_ACCEPTED);
            }
            return TestAuthenticationFilter.DummyAuthenticationHandler.managementOperationReturn;
        }

        @Override
        public void destroy() {
            TestAuthenticationFilter.DummyAuthenticationHandler.destroy = true;
        }

        @Override
        public String getType() {
            return TestAuthenticationFilter.DummyAuthenticationHandler.TYPE;
        }

        @Override
        public AuthenticationToken authenticate(HttpServletRequest request, HttpServletResponse response) throws IOException, AuthenticationException {
            AuthenticationToken token = null;
            String param = request.getParameter("authenticated");
            if ((param != null) && (param.equals("true"))) {
                token = new AuthenticationToken("u", "p", "t");
                token.setExpires((TestAuthenticationFilter.DummyAuthenticationHandler.expired ? 0 : (System.currentTimeMillis()) + (TestAuthenticationFilter.TOKEN_VALIDITY_SEC)));
            } else {
                if ((request.getHeader("WWW-Authenticate")) == null) {
                    response.setHeader("WWW-Authenticate", "dummyauth");
                } else {
                    throw new AuthenticationException("AUTH FAILED");
                }
            }
            return token;
        }
    }

    @Test
    public void testFallbackToRandomSecretProvider() throws Exception {
        // minimal configuration & simple auth handler (Pseudo)
        AuthenticationFilter filter = new AuthenticationFilter();
        try {
            FilterConfig config = Mockito.mock(FilterConfig.class);
            Mockito.when(config.getInitParameter(AUTH_TYPE)).thenReturn("simple");
            Mockito.when(config.getInitParameter(AUTH_TOKEN_VALIDITY)).thenReturn(new Long(TestAuthenticationFilter.TOKEN_VALIDITY_SEC).toString());
            Mockito.when(config.getInitParameterNames()).thenReturn(new Vector(Arrays.asList(AUTH_TYPE, AUTH_TOKEN_VALIDITY)).elements());
            ServletContext context = Mockito.mock(ServletContext.class);
            Mockito.when(context.getAttribute(SIGNER_SECRET_PROVIDER_ATTRIBUTE)).thenReturn(null);
            Mockito.when(config.getServletContext()).thenReturn(context);
            filter.init(config);
            Assert.assertEquals(PseudoAuthenticationHandler.class, filter.getAuthenticationHandler().getClass());
            Assert.assertTrue(filter.isRandomSecret());
            Assert.assertFalse(filter.isCustomSignerSecretProvider());
            Assert.assertNull(filter.getCookieDomain());
            Assert.assertNull(filter.getCookiePath());
            Assert.assertEquals(TestAuthenticationFilter.TOKEN_VALIDITY_SEC, filter.getValidity());
        } finally {
            filter.destroy();
        }
    }

    @Test
    public void testInit() throws Exception {
        // custom secret as inline
        AuthenticationFilter filter = new AuthenticationFilter();
        try {
            FilterConfig config = Mockito.mock(FilterConfig.class);
            Mockito.when(config.getInitParameter(AUTH_TYPE)).thenReturn("simple");
            Mockito.when(config.getInitParameterNames()).thenReturn(new Vector(Arrays.asList(AUTH_TYPE)).elements());
            ServletContext context = Mockito.mock(ServletContext.class);
            Mockito.when(context.getAttribute(SIGNER_SECRET_PROVIDER_ATTRIBUTE)).thenReturn(new SignerSecretProvider() {
                @Override
                public void init(Properties config, ServletContext servletContext, long tokenValidity) {
                }

                @Override
                public byte[] getCurrentSecret() {
                    return null;
                }

                @Override
                public byte[][] getAllSecrets() {
                    return null;
                }
            });
            Mockito.when(config.getServletContext()).thenReturn(context);
            filter.init(config);
            Assert.assertFalse(filter.isRandomSecret());
            Assert.assertTrue(filter.isCustomSignerSecretProvider());
        } finally {
            filter.destroy();
        }
        // custom secret by file
        File testDir = new File(System.getProperty("test.build.data", "target/test-dir"));
        testDir.mkdirs();
        String secretValue = "hadoop";
        File secretFile = new File(testDir, "http-secret.txt");
        Writer writer = new FileWriter(secretFile);
        writer.write(secretValue);
        writer.close();
        filter = new AuthenticationFilter();
        try {
            FilterConfig config = Mockito.mock(FilterConfig.class);
            Mockito.when(config.getInitParameter(AUTH_TYPE)).thenReturn("simple");
            Mockito.when(config.getInitParameter(SIGNATURE_SECRET_FILE)).thenReturn(secretFile.getAbsolutePath());
            Mockito.when(config.getInitParameterNames()).thenReturn(new Vector<String>(Arrays.asList(AUTH_TYPE, SIGNATURE_SECRET_FILE)).elements());
            ServletContext context = Mockito.mock(ServletContext.class);
            Mockito.when(context.getAttribute(SIGNER_SECRET_PROVIDER_ATTRIBUTE)).thenReturn(null);
            Mockito.when(config.getServletContext()).thenReturn(context);
            filter.init(config);
            Assert.assertFalse(filter.isRandomSecret());
            Assert.assertFalse(filter.isCustomSignerSecretProvider());
        } finally {
            filter.destroy();
        }
        // custom cookie domain and cookie path
        filter = new AuthenticationFilter();
        try {
            FilterConfig config = Mockito.mock(FilterConfig.class);
            Mockito.when(config.getInitParameter(AUTH_TYPE)).thenReturn("simple");
            Mockito.when(config.getInitParameter(COOKIE_DOMAIN)).thenReturn(".foo.com");
            Mockito.when(config.getInitParameter(COOKIE_PATH)).thenReturn("/bar");
            Mockito.when(config.getInitParameterNames()).thenReturn(new Vector<String>(Arrays.asList(AUTH_TYPE, COOKIE_DOMAIN, COOKIE_PATH)).elements());
            TestAuthenticationFilter.getMockedServletContextWithStringSigner(config);
            filter.init(config);
            Assert.assertEquals(".foo.com", filter.getCookieDomain());
            Assert.assertEquals("/bar", filter.getCookiePath());
        } finally {
            filter.destroy();
        }
        // authentication handler lifecycle, and custom impl
        TestAuthenticationFilter.DummyAuthenticationHandler.reset();
        filter = new AuthenticationFilter();
        try {
            FilterConfig config = Mockito.mock(FilterConfig.class);
            Mockito.when(config.getInitParameter("management.operation.return")).thenReturn("true");
            Mockito.when(config.getInitParameter(AUTH_TYPE)).thenReturn(TestAuthenticationFilter.DummyAuthenticationHandler.class.getName());
            Mockito.when(config.getInitParameterNames()).thenReturn(new Vector<String>(Arrays.asList(AUTH_TYPE, "management.operation.return")).elements());
            TestAuthenticationFilter.getMockedServletContextWithStringSigner(config);
            filter.init(config);
            Assert.assertTrue(TestAuthenticationFilter.DummyAuthenticationHandler.init);
        } finally {
            filter.destroy();
            Assert.assertTrue(TestAuthenticationFilter.DummyAuthenticationHandler.destroy);
        }
        // kerberos auth handler
        filter = new AuthenticationFilter();
        try {
            FilterConfig config = Mockito.mock(FilterConfig.class);
            ServletContext sc = Mockito.mock(ServletContext.class);
            Mockito.when(config.getServletContext()).thenReturn(sc);
            Mockito.when(config.getInitParameter(AUTH_TYPE)).thenReturn("kerberos");
            Mockito.when(config.getInitParameterNames()).thenReturn(new Vector<String>(Arrays.asList(AUTH_TYPE)).elements());
            filter.init(config);
        } catch (ServletException ex) {
            // Expected
        } finally {
            Assert.assertEquals(KerberosAuthenticationHandler.class, filter.getAuthenticationHandler().getClass());
            filter.destroy();
        }
    }

    @Test
    public void testInitCaseSensitivity() throws Exception {
        // minimal configuration & simple auth handler (Pseudo)
        AuthenticationFilter filter = new AuthenticationFilter();
        try {
            FilterConfig config = Mockito.mock(FilterConfig.class);
            Mockito.when(config.getInitParameter(AUTH_TYPE)).thenReturn("SimPle");
            Mockito.when(config.getInitParameter(AUTH_TOKEN_VALIDITY)).thenReturn(new Long(TestAuthenticationFilter.TOKEN_VALIDITY_SEC).toString());
            Mockito.when(config.getInitParameterNames()).thenReturn(new Vector<String>(Arrays.asList(AUTH_TYPE, AUTH_TOKEN_VALIDITY)).elements());
            TestAuthenticationFilter.getMockedServletContextWithStringSigner(config);
            filter.init(config);
            Assert.assertEquals(PseudoAuthenticationHandler.class, filter.getAuthenticationHandler().getClass());
        } finally {
            filter.destroy();
        }
    }

    @Test
    public void testGetRequestURL() throws Exception {
        AuthenticationFilter filter = new AuthenticationFilter();
        try {
            FilterConfig config = Mockito.mock(FilterConfig.class);
            Mockito.when(config.getInitParameter("management.operation.return")).thenReturn("true");
            Mockito.when(config.getInitParameter(AUTH_TYPE)).thenReturn(TestAuthenticationFilter.DummyAuthenticationHandler.class.getName());
            Mockito.when(config.getInitParameterNames()).thenReturn(new Vector<String>(Arrays.asList(AUTH_TYPE, "management.operation.return")).elements());
            TestAuthenticationFilter.getMockedServletContextWithStringSigner(config);
            filter.init(config);
            HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
            Mockito.when(request.getRequestURL()).thenReturn(new StringBuffer("http://foo:8080/bar"));
            Mockito.when(request.getQueryString()).thenReturn("a=A&b=B");
            Assert.assertEquals("http://foo:8080/bar?a=A&b=B", filter.getRequestURL(request));
        } finally {
            filter.destroy();
        }
    }

    @Test
    public void testGetToken() throws Exception {
        AuthenticationFilter filter = new AuthenticationFilter();
        try {
            FilterConfig config = Mockito.mock(FilterConfig.class);
            Mockito.when(config.getInitParameter("management.operation.return")).thenReturn("true");
            Mockito.when(config.getInitParameter(AUTH_TYPE)).thenReturn(TestAuthenticationFilter.DummyAuthenticationHandler.class.getName());
            Mockito.when(config.getInitParameter(SIGNATURE_SECRET)).thenReturn("secret");
            Mockito.when(config.getInitParameterNames()).thenReturn(new Vector<String>(Arrays.asList(AUTH_TYPE, SIGNATURE_SECRET, "management.operation.return")).elements());
            SignerSecretProvider secretProvider = TestAuthenticationFilter.getMockedServletContextWithStringSigner(config);
            filter.init(config);
            AuthenticationToken token = new AuthenticationToken("u", "p", TestAuthenticationFilter.DummyAuthenticationHandler.TYPE);
            token.setExpires(((System.currentTimeMillis()) + (TestAuthenticationFilter.TOKEN_VALIDITY_SEC)));
            Signer signer = new Signer(secretProvider);
            String tokenSigned = signer.sign(token.toString());
            Cookie cookie = new Cookie(AuthenticatedURL.AUTH_COOKIE, tokenSigned);
            HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
            Mockito.when(request.getCookies()).thenReturn(new Cookie[]{ cookie });
            AuthenticationToken newToken = filter.getToken(request);
            Assert.assertEquals(token.toString(), newToken.toString());
        } finally {
            filter.destroy();
        }
    }

    @Test
    public void testGetTokenExpired() throws Exception {
        AuthenticationFilter filter = new AuthenticationFilter();
        try {
            FilterConfig config = Mockito.mock(FilterConfig.class);
            Mockito.when(config.getInitParameter("management.operation.return")).thenReturn("true");
            Mockito.when(config.getInitParameter(AUTH_TYPE)).thenReturn(TestAuthenticationFilter.DummyAuthenticationHandler.class.getName());
            Mockito.when(config.getInitParameter(SIGNATURE_SECRET)).thenReturn("secret");
            Mockito.when(config.getInitParameterNames()).thenReturn(new Vector<String>(Arrays.asList(AUTH_TYPE, SIGNATURE_SECRET, "management.operation.return")).elements());
            TestAuthenticationFilter.getMockedServletContextWithStringSigner(config);
            filter.init(config);
            AuthenticationToken token = new AuthenticationToken("u", "p", TestAuthenticationFilter.DummyAuthenticationHandler.TYPE);
            token.setExpires(((System.currentTimeMillis()) - (TestAuthenticationFilter.TOKEN_VALIDITY_SEC)));
            SignerSecretProvider secretProvider = StringSignerSecretProviderCreator.newStringSignerSecretProvider();
            Properties secretProviderProps = new Properties();
            secretProviderProps.setProperty(SIGNATURE_SECRET, "secret");
            secretProvider.init(secretProviderProps, null, TestAuthenticationFilter.TOKEN_VALIDITY_SEC);
            Signer signer = new Signer(secretProvider);
            String tokenSigned = signer.sign(token.toString());
            Cookie cookie = new Cookie(AuthenticatedURL.AUTH_COOKIE, tokenSigned);
            HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
            Mockito.when(request.getCookies()).thenReturn(new Cookie[]{ cookie });
            boolean failed = false;
            try {
                filter.getToken(request);
            } catch (AuthenticationException ex) {
                Assert.assertEquals("AuthenticationToken expired", ex.getMessage());
                failed = true;
            } finally {
                Assert.assertTrue("token not expired", failed);
            }
        } finally {
            filter.destroy();
        }
    }

    @Test
    public void testGetTokenInvalidType() throws Exception {
        AuthenticationFilter filter = new AuthenticationFilter();
        try {
            FilterConfig config = Mockito.mock(FilterConfig.class);
            Mockito.when(config.getInitParameter("management.operation.return")).thenReturn("true");
            Mockito.when(config.getInitParameter(AUTH_TYPE)).thenReturn(TestAuthenticationFilter.DummyAuthenticationHandler.class.getName());
            Mockito.when(config.getInitParameter(SIGNATURE_SECRET)).thenReturn("secret");
            Mockito.when(config.getInitParameterNames()).thenReturn(new Vector<String>(Arrays.asList(AUTH_TYPE, SIGNATURE_SECRET, "management.operation.return")).elements());
            TestAuthenticationFilter.getMockedServletContextWithStringSigner(config);
            filter.init(config);
            AuthenticationToken token = new AuthenticationToken("u", "p", "invalidtype");
            token.setExpires(((System.currentTimeMillis()) + (TestAuthenticationFilter.TOKEN_VALIDITY_SEC)));
            SignerSecretProvider secretProvider = StringSignerSecretProviderCreator.newStringSignerSecretProvider();
            Properties secretProviderProps = new Properties();
            secretProviderProps.setProperty(SIGNATURE_SECRET, "secret");
            secretProvider.init(secretProviderProps, null, TestAuthenticationFilter.TOKEN_VALIDITY_SEC);
            Signer signer = new Signer(secretProvider);
            String tokenSigned = signer.sign(token.toString());
            Cookie cookie = new Cookie(AuthenticatedURL.AUTH_COOKIE, tokenSigned);
            HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
            Mockito.when(request.getCookies()).thenReturn(new Cookie[]{ cookie });
            boolean failed = false;
            try {
                filter.getToken(request);
            } catch (AuthenticationException ex) {
                Assert.assertEquals("Invalid AuthenticationToken type", ex.getMessage());
                failed = true;
            } finally {
                Assert.assertTrue("token not invalid type", failed);
            }
        } finally {
            filter.destroy();
        }
    }

    @Test
    public void testDoFilterNotAuthenticated() throws Exception {
        AuthenticationFilter filter = new AuthenticationFilter();
        try {
            FilterConfig config = Mockito.mock(FilterConfig.class);
            Mockito.when(config.getInitParameter("management.operation.return")).thenReturn("true");
            Mockito.when(config.getInitParameter(AUTH_TYPE)).thenReturn(TestAuthenticationFilter.DummyAuthenticationHandler.class.getName());
            Mockito.when(config.getInitParameterNames()).thenReturn(new Vector<String>(Arrays.asList(AUTH_TYPE, "management.operation.return")).elements());
            TestAuthenticationFilter.getMockedServletContextWithStringSigner(config);
            filter.init(config);
            HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
            Mockito.when(request.getRequestURL()).thenReturn(new StringBuffer("http://foo:8080/bar"));
            HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
            FilterChain chain = Mockito.mock(FilterChain.class);
            Mockito.doAnswer(new Answer<Object>() {
                @Override
                public Object answer(InvocationOnMock invocation) throws Throwable {
                    Assert.fail();
                    return null;
                }
            }).when(chain).doFilter(ArgumentMatchers.any(), ArgumentMatchers.any());
            Mockito.when(response.containsHeader("WWW-Authenticate")).thenReturn(true);
            filter.doFilter(request, response, chain);
            Mockito.verify(response).sendError(SC_UNAUTHORIZED, "Authentication required");
        } finally {
            filter.destroy();
        }
    }

    @Test
    public void testDoFilterAuthentication() throws Exception {
        _testDoFilterAuthentication(false, false, false);
    }

    @Test
    public void testDoFilterAuthenticationImmediateExpiration() throws Exception {
        _testDoFilterAuthentication(false, false, true);
    }

    @Test
    public void testDoFilterAuthenticationWithInvalidToken() throws Exception {
        _testDoFilterAuthentication(false, true, false);
    }

    @Test
    public void testDoFilterAuthenticationWithDomainPath() throws Exception {
        _testDoFilterAuthentication(true, false, false);
    }

    @Test
    public void testDoFilterAuthenticated() throws Exception {
        AuthenticationFilter filter = new AuthenticationFilter();
        try {
            FilterConfig config = Mockito.mock(FilterConfig.class);
            Mockito.when(config.getInitParameter("management.operation.return")).thenReturn("true");
            Mockito.when(config.getInitParameter(AUTH_TYPE)).thenReturn(TestAuthenticationFilter.DummyAuthenticationHandler.class.getName());
            Mockito.when(config.getInitParameterNames()).thenReturn(new Vector<String>(Arrays.asList(AUTH_TYPE, "management.operation.return")).elements());
            TestAuthenticationFilter.getMockedServletContextWithStringSigner(config);
            filter.init(config);
            HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
            Mockito.when(request.getRequestURL()).thenReturn(new StringBuffer("http://foo:8080/bar"));
            AuthenticationToken token = new AuthenticationToken("u", "p", "t");
            token.setExpires(((System.currentTimeMillis()) + (TestAuthenticationFilter.TOKEN_VALIDITY_SEC)));
            SignerSecretProvider secretProvider = StringSignerSecretProviderCreator.newStringSignerSecretProvider();
            Properties secretProviderProps = new Properties();
            secretProviderProps.setProperty(SIGNATURE_SECRET, "secret");
            secretProvider.init(secretProviderProps, null, TestAuthenticationFilter.TOKEN_VALIDITY_SEC);
            Signer signer = new Signer(secretProvider);
            String tokenSigned = signer.sign(token.toString());
            Cookie cookie = new Cookie(AuthenticatedURL.AUTH_COOKIE, tokenSigned);
            Mockito.when(request.getCookies()).thenReturn(new Cookie[]{ cookie });
            HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
            FilterChain chain = Mockito.mock(FilterChain.class);
            Mockito.doAnswer(new Answer<Object>() {
                @Override
                public Object answer(InvocationOnMock invocation) throws Throwable {
                    Object[] args = invocation.getArguments();
                    HttpServletRequest request = ((HttpServletRequest) (args[0]));
                    Assert.assertEquals("u", request.getRemoteUser());
                    Assert.assertEquals("p", request.getUserPrincipal().getName());
                    return null;
                }
            }).when(chain).doFilter(ArgumentMatchers.any(), ArgumentMatchers.any());
            filter.doFilter(request, response, chain);
        } finally {
            filter.destroy();
        }
    }

    @Test
    public void testDoFilterAuthenticationFailure() throws Exception {
        AuthenticationFilter filter = new AuthenticationFilter();
        try {
            FilterConfig config = Mockito.mock(FilterConfig.class);
            Mockito.when(config.getInitParameter("management.operation.return")).thenReturn("true");
            Mockito.when(config.getInitParameter(AUTH_TYPE)).thenReturn(TestAuthenticationFilter.DummyAuthenticationHandler.class.getName());
            Mockito.when(config.getInitParameterNames()).thenReturn(new Vector<String>(Arrays.asList(AUTH_TYPE, "management.operation.return")).elements());
            TestAuthenticationFilter.getMockedServletContextWithStringSigner(config);
            filter.init(config);
            HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
            Mockito.when(request.getRequestURL()).thenReturn(new StringBuffer("http://foo:8080/bar"));
            Mockito.when(request.getCookies()).thenReturn(new Cookie[]{  });
            Mockito.when(request.getHeader("WWW-Authenticate")).thenReturn("dummyauth");
            HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
            FilterChain chain = Mockito.mock(FilterChain.class);
            final Map<String, String> cookieMap = new HashMap<String, String>();
            Mockito.doAnswer(new Answer<Object>() {
                @Override
                public Object answer(InvocationOnMock invocation) throws Throwable {
                    Object[] args = invocation.getArguments();
                    TestAuthenticationFilter.parseCookieMap(((String) (args[1])), cookieMap);
                    return null;
                }
            }).when(response).addHeader(Mockito.eq("Set-Cookie"), Mockito.anyString());
            Mockito.doAnswer(new Answer<Object>() {
                @Override
                public Object answer(InvocationOnMock invocation) throws Throwable {
                    Assert.fail("shouldn't get here");
                    return null;
                }
            }).when(chain).doFilter(ArgumentMatchers.any(), ArgumentMatchers.any());
            filter.doFilter(request, response, chain);
            Mockito.verify(response).sendError(SC_FORBIDDEN, "AUTH FAILED");
            Mockito.verify(response, Mockito.never()).setHeader(Mockito.eq("WWW-Authenticate"), Mockito.anyString());
            String value = cookieMap.get(AUTH_COOKIE);
            Assert.assertNotNull("cookie missing", value);
            Assert.assertEquals("", value);
        } finally {
            filter.destroy();
        }
    }

    @Test
    public void testDoFilterAuthenticatedExpired() throws Exception {
        String secret = "secret";
        AuthenticationFilter filter = new AuthenticationFilter();
        try {
            FilterConfig config = Mockito.mock(FilterConfig.class);
            Mockito.when(config.getInitParameter("management.operation.return")).thenReturn("true");
            Mockito.when(config.getInitParameter(AUTH_TYPE)).thenReturn(TestAuthenticationFilter.DummyAuthenticationHandler.class.getName());
            Mockito.when(config.getInitParameter(SIGNATURE_SECRET)).thenReturn(secret);
            Mockito.when(config.getInitParameterNames()).thenReturn(new Vector<String>(Arrays.asList(AUTH_TYPE, SIGNATURE_SECRET, "management.operation.return")).elements());
            TestAuthenticationFilter.getMockedServletContextWithStringSigner(config);
            filter.init(config);
            HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
            Mockito.when(request.getRequestURL()).thenReturn(new StringBuffer("http://foo:8080/bar"));
            AuthenticationToken token = new AuthenticationToken("u", "p", TestAuthenticationFilter.DummyAuthenticationHandler.TYPE);
            token.setExpires(((System.currentTimeMillis()) - (TestAuthenticationFilter.TOKEN_VALIDITY_SEC)));
            SignerSecretProvider secretProvider = StringSignerSecretProviderCreator.newStringSignerSecretProvider();
            Properties secretProviderProps = new Properties();
            secretProviderProps.setProperty(SIGNATURE_SECRET, secret);
            secretProvider.init(secretProviderProps, null, TestAuthenticationFilter.TOKEN_VALIDITY_SEC);
            Signer signer = new Signer(secretProvider);
            String tokenSigned = signer.sign(token.toString());
            Cookie cookie = new Cookie(AuthenticatedURL.AUTH_COOKIE, tokenSigned);
            Mockito.when(request.getCookies()).thenReturn(new Cookie[]{ cookie });
            HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
            Mockito.when(response.containsHeader("WWW-Authenticate")).thenReturn(true);
            FilterChain chain = Mockito.mock(FilterChain.class);
            TestAuthenticationFilter.verifyUnauthorized(filter, request, response, chain);
        } finally {
            filter.destroy();
        }
    }

    @Test
    public void testDoFilterAuthenticationAuthorized() throws Exception {
        // Both expired period and MaxInActiveInterval are not reached.
        long maxInactives = (System.currentTimeMillis()) + (TestAuthenticationFilter.TOKEN_MAX_INACTIVE_INTERVAL);
        long expires = (System.currentTimeMillis()) + (TestAuthenticationFilter.TOKEN_VALIDITY_SEC);
        boolean authorized = true;
        _testDoFilterAuthenticationMaxInactiveInterval(maxInactives, expires, authorized);
    }

    @Test
    public void testDoFilterAuthenticationUnauthorizedExpired() throws Exception {
        // Expired period is reached, MaxInActiveInterval is not reached.
        long maxInactives = (System.currentTimeMillis()) + (TestAuthenticationFilter.TOKEN_MAX_INACTIVE_INTERVAL);
        long expires = (System.currentTimeMillis()) - (TestAuthenticationFilter.TOKEN_VALIDITY_SEC);
        boolean authorized = false;
        _testDoFilterAuthenticationMaxInactiveInterval(maxInactives, expires, authorized);
    }

    @Test
    public void testDoFilterAuthenticationUnauthorizedInactived() throws Exception {
        // Expired period is not reached, MaxInActiveInterval is reached.
        long maxInactives = (System.currentTimeMillis()) - (TestAuthenticationFilter.TOKEN_MAX_INACTIVE_INTERVAL);
        long expires = (System.currentTimeMillis()) + (TestAuthenticationFilter.TOKEN_VALIDITY_SEC);
        boolean authorized = false;
        _testDoFilterAuthenticationMaxInactiveInterval(maxInactives, expires, authorized);
    }

    @Test
    public void testDoFilterAuthenticationUnauthorizedInactivedExpired() throws Exception {
        // Both expired period and MaxInActiveInterval is reached.
        long maxInactives = (System.currentTimeMillis()) - (TestAuthenticationFilter.TOKEN_MAX_INACTIVE_INTERVAL);
        long expires = (System.currentTimeMillis()) - (TestAuthenticationFilter.TOKEN_VALIDITY_SEC);
        boolean authorized = false;
        _testDoFilterAuthenticationMaxInactiveInterval(maxInactives, expires, authorized);
    }

    @Test
    public void testTokenWithValidActivityInterval() throws Exception {
        // Provide token containing valid maxInactive value.
        // The token is active.
        // The server has maxInactiveInterval configured to -1.(disabled)
        // The server shall authorize the access, but should not drop a new cookie
        long maxInactives = (System.currentTimeMillis()) + (TestAuthenticationFilter.TOKEN_MAX_INACTIVE_INTERVAL);
        long expires = (System.currentTimeMillis()) + (TestAuthenticationFilter.TOKEN_VALIDITY_SEC);
        // authorized
        // newCookie
        _testDoFilterAuthenticationMaxInactiveInterval(maxInactives, (-1), expires, true, false);
        // Provide token containing valid maxInactive value.
        // The token is active.
        // The server has maxInactiveInterval configured to value
        // greater than 0.(enabled)
        // The server shall authorize the access and drop a new cookie
        // with renewed activity interval
        maxInactives = (System.currentTimeMillis()) + (TestAuthenticationFilter.TOKEN_MAX_INACTIVE_INTERVAL);
        expires = (System.currentTimeMillis()) + (TestAuthenticationFilter.TOKEN_VALIDITY_SEC);
        // authorized
        // newCookie
        _testDoFilterAuthenticationMaxInactiveInterval(maxInactives, TestAuthenticationFilter.TOKEN_MAX_INACTIVE_INTERVAL, expires, true, true);
    }

    @Test
    public void testTokenWithExpiredActivityIntervaln() throws Exception {
        // Provide token containing invalid maxInactive value.
        // The token is inactive.
        // The server has maxInactiveInterval configured to -1.(disabled)
        // The server should deny access and expire the token.
        long maxInactives = (System.currentTimeMillis()) - (TestAuthenticationFilter.TOKEN_MAX_INACTIVE_INTERVAL);
        long expires = (System.currentTimeMillis()) + (TestAuthenticationFilter.TOKEN_VALIDITY_SEC);
        // authorized
        // newCookie
        _testDoFilterAuthenticationMaxInactiveInterval(maxInactives, (-1), expires, false, false);
        // Provide token containing invalid maxInactive value.
        // The token is inactive.
        // The server has maxInactiveInterval configured to value
        // greater than 0.(enabled)
        // The server should deny access and expire the token.
        maxInactives = (System.currentTimeMillis()) + (TestAuthenticationFilter.TOKEN_MAX_INACTIVE_INTERVAL);
        expires = (System.currentTimeMillis()) + (TestAuthenticationFilter.TOKEN_VALIDITY_SEC);
        // authorized
        // newCookie
        _testDoFilterAuthenticationMaxInactiveInterval(maxInactives, (-1), expires, true, false);
    }

    @Test
    public void testTokenWithNoActivityIntervals() throws Exception {
        // Provide token which does not contain maxInactive value.
        // The server has maxInactiveInterval configured to -1.
        // The server shall authorize the access, but should not drop a new cookie
        long expires = (System.currentTimeMillis()) + (TestAuthenticationFilter.TOKEN_VALIDITY_SEC);
        // authorized
        // newCookie
        _testDoFilterAuthenticationMaxInactiveInterval((-1), (-1), expires, true, false);
        // Provide token which does not contain  maxInactive value.
        // The server has maxInactiveInterval to some value
        // The server shall authorize the access and drop a new cookie
        // with renewed activity interval
        expires = (System.currentTimeMillis()) + (TestAuthenticationFilter.TOKEN_VALIDITY_SEC);
        // authorized
        // newCookie
        _testDoFilterAuthenticationMaxInactiveInterval((-1), TestAuthenticationFilter.TOKEN_MAX_INACTIVE_INTERVAL, expires, true, true);
    }

    @Test
    public void testDoFilterAuthenticatedInvalidType() throws Exception {
        String secret = "secret";
        AuthenticationFilter filter = new AuthenticationFilter();
        try {
            FilterConfig config = Mockito.mock(FilterConfig.class);
            Mockito.when(config.getInitParameter("management.operation.return")).thenReturn("true");
            Mockito.when(config.getInitParameter(AUTH_TYPE)).thenReturn(TestAuthenticationFilter.DummyAuthenticationHandler.class.getName());
            Mockito.when(config.getInitParameter(SIGNATURE_SECRET)).thenReturn(secret);
            Mockito.when(config.getInitParameterNames()).thenReturn(new Vector<String>(Arrays.asList(AUTH_TYPE, SIGNATURE_SECRET, "management.operation.return")).elements());
            TestAuthenticationFilter.getMockedServletContextWithStringSigner(config);
            filter.init(config);
            HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
            Mockito.when(request.getRequestURL()).thenReturn(new StringBuffer("http://foo:8080/bar"));
            AuthenticationToken token = new AuthenticationToken("u", "p", "invalidtype");
            token.setExpires(((System.currentTimeMillis()) + (TestAuthenticationFilter.TOKEN_VALIDITY_SEC)));
            SignerSecretProvider secretProvider = StringSignerSecretProviderCreator.newStringSignerSecretProvider();
            Properties secretProviderProps = new Properties();
            secretProviderProps.setProperty(SIGNATURE_SECRET, secret);
            secretProvider.init(secretProviderProps, null, TestAuthenticationFilter.TOKEN_VALIDITY_SEC);
            Signer signer = new Signer(secretProvider);
            String tokenSigned = signer.sign(token.toString());
            Cookie cookie = new Cookie(AuthenticatedURL.AUTH_COOKIE, tokenSigned);
            Mockito.when(request.getCookies()).thenReturn(new Cookie[]{ cookie });
            HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
            Mockito.when(response.containsHeader("WWW-Authenticate")).thenReturn(true);
            FilterChain chain = Mockito.mock(FilterChain.class);
            TestAuthenticationFilter.verifyUnauthorized(filter, request, response, chain);
        } finally {
            filter.destroy();
        }
    }

    @Test
    public void testManagementOperation() throws Exception {
        AuthenticationFilter filter = new AuthenticationFilter();
        try {
            FilterConfig config = Mockito.mock(FilterConfig.class);
            Mockito.when(config.getInitParameter("management.operation.return")).thenReturn("false");
            Mockito.when(config.getInitParameter(AUTH_TYPE)).thenReturn(TestAuthenticationFilter.DummyAuthenticationHandler.class.getName());
            Mockito.when(config.getInitParameterNames()).thenReturn(new Vector<String>(Arrays.asList(AUTH_TYPE, "management.operation.return")).elements());
            TestAuthenticationFilter.getMockedServletContextWithStringSigner(config);
            filter.init(config);
            HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
            Mockito.when(request.getRequestURL()).thenReturn(new StringBuffer("http://foo:8080/bar"));
            HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
            FilterChain chain = Mockito.mock(FilterChain.class);
            filter.doFilter(request, response, chain);
            Mockito.verify(response).setStatus(SC_ACCEPTED);
            Mockito.verifyNoMoreInteractions(response);
            Mockito.reset(request);
            Mockito.reset(response);
            AuthenticationToken token = new AuthenticationToken("u", "p", "t");
            token.setExpires(((System.currentTimeMillis()) + (TestAuthenticationFilter.TOKEN_VALIDITY_SEC)));
            SignerSecretProvider secretProvider = StringSignerSecretProviderCreator.newStringSignerSecretProvider();
            Properties secretProviderProps = new Properties();
            secretProviderProps.setProperty(SIGNATURE_SECRET, "secret");
            secretProvider.init(secretProviderProps, null, TestAuthenticationFilter.TOKEN_VALIDITY_SEC);
            Signer signer = new Signer(secretProvider);
            String tokenSigned = signer.sign(token.toString());
            Cookie cookie = new Cookie(AuthenticatedURL.AUTH_COOKIE, tokenSigned);
            Mockito.when(request.getCookies()).thenReturn(new Cookie[]{ cookie });
            Mockito.when(request.getRequestURL()).thenReturn(new StringBuffer());
            filter.doFilter(request, response, chain);
            Mockito.verify(response).setStatus(SC_ACCEPTED);
            Mockito.verifyNoMoreInteractions(response);
        } finally {
            filter.destroy();
        }
    }
}

