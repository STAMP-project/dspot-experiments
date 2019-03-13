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
package org.apache.hadoop.http;


import java.io.IOException;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.net.NetUtils;
import org.apache.hadoop.security.authentication.server.AuthenticationFilter;
import org.apache.hadoop.test.GenericTestUtils;
import org.eclipse.jetty.util.log.Log;
import org.junit.Assert;
import org.junit.Test;


public class TestAuthenticationSessionCookie {
    private static final String BASEDIR = GenericTestUtils.getTempPath(TestHttpCookieFlag.class.getSimpleName());

    private static boolean isCookiePersistent;

    private static final long TOKEN_VALIDITY_SEC = 1000;

    private static long expires;

    private static String keystoresDir;

    private static String sslConfDir;

    private static HttpServer2 server;

    public static class DummyAuthenticationFilter implements Filter {
        @Override
        public void init(FilterConfig filterConfig) throws ServletException {
            TestAuthenticationSessionCookie.isCookiePersistent = false;
        }

        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
            HttpServletResponse resp = ((HttpServletResponse) (response));
            AuthenticationFilter.createAuthCookie(resp, "token", null, null, TestAuthenticationSessionCookie.expires, TestAuthenticationSessionCookie.isCookiePersistent, true);
            chain.doFilter(request, resp);
        }

        @Override
        public void destroy() {
        }
    }

    public static class DummyFilterInitializer extends FilterInitializer {
        @Override
        public void initFilter(FilterContainer container, Configuration conf) {
            container.addFilter("DummyAuth", TestAuthenticationSessionCookie.DummyAuthenticationFilter.class.getName(), new HashMap());
        }
    }

    public static class Dummy2AuthenticationFilter extends TestAuthenticationSessionCookie.DummyAuthenticationFilter {
        @Override
        public void init(FilterConfig filterConfig) throws ServletException {
            TestAuthenticationSessionCookie.isCookiePersistent = true;
            TestAuthenticationSessionCookie.expires = (System.currentTimeMillis()) + (TestAuthenticationSessionCookie.TOKEN_VALIDITY_SEC);
        }

        @Override
        public void destroy() {
        }
    }

    public static class Dummy2FilterInitializer extends FilterInitializer {
        @Override
        public void initFilter(FilterContainer container, Configuration conf) {
            container.addFilter("Dummy2Auth", TestAuthenticationSessionCookie.Dummy2AuthenticationFilter.class.getName(), new HashMap());
        }
    }

    @Test
    public void testSessionCookie() throws IOException {
        try {
            startServer(true);
        } catch (Exception e) {
            // Auto-generated catch block
            e.printStackTrace();
        }
        URL base = new URL(("http://" + (NetUtils.getHostPortString(TestAuthenticationSessionCookie.server.getConnectorAddress(0)))));
        HttpURLConnection conn = ((HttpURLConnection) (new URL(base, "/echo").openConnection()));
        String header = conn.getHeaderField("Set-Cookie");
        List<HttpCookie> cookies = HttpCookie.parse(header);
        Assert.assertTrue((!(cookies.isEmpty())));
        Log.getLog().info(header);
        Assert.assertFalse(header.contains("; Expires="));
        Assert.assertTrue("token".equals(cookies.get(0).getValue()));
    }

    @Test
    public void testPersistentCookie() throws IOException {
        try {
            startServer(false);
        } catch (Exception e) {
            // Auto-generated catch block
            e.printStackTrace();
        }
        URL base = new URL(("http://" + (NetUtils.getHostPortString(TestAuthenticationSessionCookie.server.getConnectorAddress(0)))));
        HttpURLConnection conn = ((HttpURLConnection) (new URL(base, "/echo").openConnection()));
        String header = conn.getHeaderField("Set-Cookie");
        List<HttpCookie> cookies = HttpCookie.parse(header);
        Assert.assertTrue((!(cookies.isEmpty())));
        Log.getLog().info(header);
        Assert.assertTrue(header.contains("; Expires="));
        Assert.assertTrue("token".equals(cookies.get(0).getValue()));
    }
}

