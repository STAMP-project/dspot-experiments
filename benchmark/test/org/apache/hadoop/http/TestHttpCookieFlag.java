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
import java.security.GeneralSecurityException;
import java.util.List;
import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.HttpServletResponse;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.net.NetUtils;
import org.apache.hadoop.security.authentication.server.AuthenticationFilter;
import org.apache.hadoop.security.ssl.SSLFactory;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Test;


public class TestHttpCookieFlag {
    private static final String BASEDIR = GenericTestUtils.getTempPath(TestHttpCookieFlag.class.getSimpleName());

    private static String keystoresDir;

    private static String sslConfDir;

    private static SSLFactory clientSslFactory;

    private static HttpServer2 server;

    public static class DummyAuthenticationFilter implements Filter {
        @Override
        public void init(FilterConfig filterConfig) throws ServletException {
        }

        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
            HttpServletResponse resp = ((HttpServletResponse) (response));
            boolean isHttps = "https".equals(request.getScheme());
            AuthenticationFilter.createAuthCookie(resp, "token", null, null, (-1), true, isHttps);
            chain.doFilter(request, resp);
        }

        @Override
        public void destroy() {
        }
    }

    public static class DummyFilterInitializer extends FilterInitializer {
        @Override
        public void initFilter(FilterContainer container, Configuration conf) {
            container.addFilter("DummyAuth", TestHttpCookieFlag.DummyAuthenticationFilter.class.getName(), null);
        }
    }

    @Test
    public void testHttpCookie() throws IOException {
        URL base = new URL(("http://" + (NetUtils.getHostPortString(TestHttpCookieFlag.server.getConnectorAddress(0)))));
        HttpURLConnection conn = ((HttpURLConnection) (new URL(base, "/echo").openConnection()));
        String header = conn.getHeaderField("Set-Cookie");
        List<HttpCookie> cookies = HttpCookie.parse(header);
        Assert.assertTrue((!(cookies.isEmpty())));
        Assert.assertTrue(header.contains("; HttpOnly"));
        Assert.assertTrue("token".equals(cookies.get(0).getValue()));
    }

    @Test
    public void testHttpsCookie() throws IOException, GeneralSecurityException {
        URL base = new URL(("https://" + (NetUtils.getHostPortString(TestHttpCookieFlag.server.getConnectorAddress(1)))));
        HttpsURLConnection conn = ((HttpsURLConnection) (new URL(base, "/echo").openConnection()));
        conn.setSSLSocketFactory(TestHttpCookieFlag.clientSslFactory.createSSLSocketFactory());
        String header = conn.getHeaderField("Set-Cookie");
        List<HttpCookie> cookies = HttpCookie.parse(header);
        Assert.assertTrue((!(cookies.isEmpty())));
        Assert.assertTrue(header.contains("; HttpOnly"));
        Assert.assertTrue(cookies.get(0).getSecure());
        Assert.assertTrue("token".equals(cookies.get(0).getValue()));
    }
}

