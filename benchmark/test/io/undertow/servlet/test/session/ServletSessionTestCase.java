/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2014 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package io.undertow.servlet.test.session;


import StatusCodes.OK;
import io.undertow.testutils.DefaultServer;
import io.undertow.testutils.HttpClientUtils;
import io.undertow.testutils.TestHttpClient;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.cookie.Cookie;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Stuart Douglas
 */
@RunWith(DefaultServer.class)
public class ServletSessionTestCase {
    @Test
    public void testSimpleSessionUsage() throws IOException {
        TestHttpClient client = new TestHttpClient();
        try {
            HttpGet get = new HttpGet(((DefaultServer.getDefaultServerURL()) + "/servletContext/aa/b"));
            HttpResponse result = client.execute(get);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            String response = HttpClientUtils.readResponse(result);
            Assert.assertEquals("1", response);
            result = client.execute(get);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            response = HttpClientUtils.readResponse(result);
            Assert.assertEquals("2", response);
            result = client.execute(get);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            response = HttpClientUtils.readResponse(result);
            Assert.assertEquals("3", response);
        } finally {
            client.getConnectionManager().shutdown();
        }
    }

    @Test
    public void testSessionCookieConfig() throws IOException {
        TestHttpClient client = new TestHttpClient();
        try {
            HttpGet get = new HttpGet(((DefaultServer.getDefaultServerURL()) + "/servletContext/aa/b"));
            HttpResponse result = client.execute(get);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            String response = HttpClientUtils.readResponse(result);
            Assert.assertEquals("1", response);
            String cookieValue = result.getHeaders("Set-Cookie")[0].getValue();
            Assert.assertTrue(cookieValue.contains("MySessionCookie"));
            Assert.assertTrue(cookieValue.contains("/servletContext/aa/"));
            result = client.execute(get);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            response = HttpClientUtils.readResponse(result);
            Assert.assertEquals("2", response);
            result = client.execute(get);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            response = HttpClientUtils.readResponse(result);
            Assert.assertEquals("3", response);
        } finally {
            client.getConnectionManager().shutdown();
        }
    }

    @Test
    public void testSessionConfigNoCookies() throws IOException {
        TestHttpClient client = new TestHttpClient();
        client.setCookieStore(new CookieStore() {
            @Override
            public void addCookie(Cookie cookie) {
            }

            @Override
            public List<Cookie> getCookies() {
                return Collections.EMPTY_LIST;
            }

            @Override
            public boolean clearExpired(Date date) {
                return false;
            }

            @Override
            public void clear() {
            }
        });
        try {
            HttpResponse result = client.execute(new HttpGet(((DefaultServer.getDefaultServerURL()) + "/servletContext/aa/b;foo=bar")));
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            String response = HttpClientUtils.readResponse(result);
            Assert.assertEquals("1", response);
            String url = result.getHeaders("url")[0].getValue();
            result = client.execute(new HttpGet(url));
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            response = HttpClientUtils.readResponse(result);
            url = result.getHeaders("url")[0].getValue();
            Assert.assertEquals("2", response);
            result = client.execute(new HttpGet(url));
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            response = HttpClientUtils.readResponse(result);
            Assert.assertEquals("3", response);
        } finally {
            client.getConnectionManager().shutdown();
        }
    }
}

