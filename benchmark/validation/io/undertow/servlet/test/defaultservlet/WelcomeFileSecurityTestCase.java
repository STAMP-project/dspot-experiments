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
package io.undertow.servlet.test.defaultservlet;


import StatusCodes.OK;
import StatusCodes.UNAUTHORIZED;
import io.undertow.testutils.DefaultServer;
import io.undertow.testutils.HttpClientUtils;
import io.undertow.testutils.TestHttpClient;
import io.undertow.util.FlexBase64;
import java.io.IOException;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Stuart Douglas
 */
@RunWith(DefaultServer.class)
public class WelcomeFileSecurityTestCase {
    @Test
    public void testWelcomeFileRedirect() throws IOException {
        TestHttpClient client = new TestHttpClient();
        try {
            HttpGet get = new HttpGet(((DefaultServer.getDefaultServerURL()) + "/servletContext/"));
            HttpResponse result = client.execute(get);
            Assert.assertEquals(UNAUTHORIZED, result.getStatusLine().getStatusCode());
            Header[] values = result.getHeaders(WWW_AUTHENTICATE.toString());
            Assert.assertEquals(1, values.length);
            Assert.assertEquals(((BASIC) + " realm=\"Test Realm\""), values[0].getValue());
            HttpClientUtils.readResponse(result);
            get = new HttpGet(((DefaultServer.getDefaultServerURL()) + "/servletContext/"));
            get.addHeader(AUTHORIZATION.toString(), (((BASIC) + " ") + (FlexBase64.encodeString("user1:password1".getBytes(), false))));
            result = client.execute(get);
            String response = HttpClientUtils.readResponse(result);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            Assert.assertTrue(response.contains("Redirected home page"));
        } finally {
            client.getConnectionManager().shutdown();
        }
    }

    @Test
    public void testWelcomeServletRedirect() throws IOException {
        TestHttpClient client = new TestHttpClient();
        try {
            HttpGet get = new HttpGet(((DefaultServer.getDefaultServerURL()) + "/servletContext/path?a=b"));
            HttpResponse result = client.execute(get);
            Assert.assertEquals(UNAUTHORIZED, result.getStatusLine().getStatusCode());
            Header[] values = result.getHeaders(WWW_AUTHENTICATE.toString());
            Assert.assertEquals(1, values.length);
            Assert.assertEquals(((BASIC) + " realm=\"Test Realm\""), values[0].getValue());
            HttpClientUtils.readResponse(result);
            get = new HttpGet(((DefaultServer.getDefaultServerURL()) + "/servletContext/path?a=b"));
            get.addHeader(AUTHORIZATION.toString(), (((BASIC) + " ") + (FlexBase64.encodeString("user1:password1".getBytes(), false))));
            result = client.execute(get);
            String response = HttpClientUtils.readResponse(result);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            Assert.assertEquals("pathInfo:null queryString:a=b servletPath:/path/default requestUri:/servletContext/path/", response);
        } finally {
            client.getConnectionManager().shutdown();
        }
    }
}

