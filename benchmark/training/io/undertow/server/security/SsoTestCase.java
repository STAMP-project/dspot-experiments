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
package io.undertow.server.security;


import SecurityNotification.EventType.AUTHENTICATED;
import SecurityNotification.EventType.LOGGED_OUT;
import StatusCodes.OK;
import StatusCodes.UNAUTHORIZED;
import io.undertow.testutils.DefaultServer;
import io.undertow.testutils.HttpClientUtils;
import io.undertow.testutils.TestHttpClient;
import io.undertow.util.FlexBase64;
import io.undertow.util.Headers;
import java.io.IOException;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCookieStore;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Stuart Douglas
 */
@RunWith(DefaultServer.class)
public class SsoTestCase extends AuthenticationTestBase {
    @Test
    public void testSsoSuccess() throws IOException {
        TestHttpClient client = new TestHttpClient();
        client.setCookieStore(new BasicCookieStore());
        HttpGet get = new HttpGet(((DefaultServer.getDefaultServerURL()) + "/test1"));
        HttpResponse result = client.execute(get);
        Assert.assertEquals(UNAUTHORIZED, result.getStatusLine().getStatusCode());
        Header[] values = result.getHeaders(Headers.WWW_AUTHENTICATE.toString());
        String header = AuthenticationTestBase.getAuthHeader(Headers.BASIC, values);
        Assert.assertEquals(((Headers.BASIC) + " realm=\"Test Realm\""), header);
        HttpClientUtils.readResponse(result);
        get = new HttpGet(((DefaultServer.getDefaultServerURL()) + "/test1"));
        get.addHeader(Headers.AUTHORIZATION.toString(), (((Headers.BASIC) + " ") + (FlexBase64.encodeString("userOne:passwordOne".getBytes(), false))));
        result = client.execute(get);
        Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
        values = result.getHeaders("ProcessedBy");
        Assert.assertEquals(1, values.length);
        Assert.assertEquals("ResponseHandler", values[0].getValue());
        HttpClientUtils.readResponse(result);
        AuthenticationTestBase.assertSingleNotificationType(AUTHENTICATED);
        get = new HttpGet(((DefaultServer.getDefaultServerURL()) + "/test2"));
        result = client.execute(get);
        Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
        values = result.getHeaders("ProcessedBy");
        Assert.assertEquals(1, values.length);
        Assert.assertEquals("ResponseHandler", values[0].getValue());
        HttpClientUtils.readResponse(result);
        AuthenticationTestBase.assertSingleNotificationType(AUTHENTICATED);
        // now test that logout will invalidate the SSO session
        get = new HttpGet(((DefaultServer.getDefaultServerURL()) + "/test1?logout=true"));
        get.addHeader(Headers.AUTHORIZATION.toString(), (((Headers.BASIC) + " ") + (FlexBase64.encodeString("userOne:passwordOne".getBytes(), false))));
        result = client.execute(get);
        Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
        values = result.getHeaders("ProcessedBy");
        Assert.assertEquals(1, values.length);
        Assert.assertEquals("ResponseHandler", values[0].getValue());
        HttpClientUtils.readResponse(result);
        AuthenticationTestBase.assertNotifiactions(AUTHENTICATED, LOGGED_OUT);
        get = new HttpGet(((DefaultServer.getDefaultServerURL()) + "/test2"));
        result = client.execute(get);
        Assert.assertEquals(UNAUTHORIZED, result.getStatusLine().getStatusCode());
    }
}

