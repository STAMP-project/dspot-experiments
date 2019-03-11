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
package io.undertow.servlet.test.security.basic;


import StatusCodes.UNAUTHORIZED;
import io.undertow.testutils.DefaultServer;
import io.undertow.testutils.HttpClientUtils;
import io.undertow.testutils.TestHttpClient;
import java.nio.charset.StandardCharsets;
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
public class ServletBasicAuthTestCase {
    private static final String REALM_NAME = "Servlet_Realm";

    @Test
    public void testChallengeSent() throws Exception {
        TestHttpClient client = new TestHttpClient();
        String url = (DefaultServer.getDefaultServerURL()) + "/servletContext/secured/username";
        HttpGet get = new HttpGet(url);
        HttpResponse result = client.execute(get);
        HttpClientUtils.readResponse(result);
        Assert.assertEquals(UNAUTHORIZED, result.getStatusLine().getStatusCode());
        Header[] values = result.getHeaders(WWW_AUTHENTICATE.toString());
        Assert.assertEquals(1, values.length);
        String value = values[0].getValue();
        Assert.assertTrue(value.startsWith("Basic"));
    }

    @Test
    public void testUserName() throws Exception {
        testCall("username", "user1", StandardCharsets.UTF_8, "Chrome", "user1", "password1", 200);
    }

    @Test
    public void testAuthType() throws Exception {
        testCall("authType", "BASIC", StandardCharsets.UTF_8, "Chrome", "user1", "password1", 200);
    }

    @Test
    public void testBasicAuthNonAscii() throws Exception {
        testCall("authType", "BASIC", StandardCharsets.UTF_8, "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36", "charsetUser", "password-?", 200);
        testCall("authType", "BASIC", StandardCharsets.ISO_8859_1, "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36", "charsetUser", "password-?", 401);
        testCall("authType", "BASIC", StandardCharsets.ISO_8859_1, "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:40.0) Gecko/20100101 Firefox/40.1", "charsetUser", "password-?", 200);
        testCall("authType", "BASIC", StandardCharsets.UTF_8, "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:40.0) Gecko/20100101 Firefox/40.1", "charsetUser", "password-?", 401);
    }
}

