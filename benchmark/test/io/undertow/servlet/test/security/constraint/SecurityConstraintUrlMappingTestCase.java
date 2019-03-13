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
package io.undertow.servlet.test.security.constraint;


import StatusCodes.FORBIDDEN;
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
import org.apache.http.client.methods.HttpPost;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Stuart Douglas
 */
@RunWith(DefaultServer.class)
public class SecurityConstraintUrlMappingTestCase {
    public static final String HELLO_WORLD = "Hello World";

    @Test
    public void testExactMatch() throws IOException {
        runSimpleUrlTest(((DefaultServer.getDefaultServerURL()) + "/servletContext/role1"), "user2:password2", "user1:password1");
    }

    @Test
    public void testPatternMatch() throws IOException {
        runSimpleUrlTest(((DefaultServer.getDefaultServerURL()) + "/servletContext/secured/role2/aa"), "user1:password1", "user2:password2");
    }

    @Test
    public void testStartStar() throws IOException {
        runSimpleUrlTest(((DefaultServer.getDefaultServerURL()) + "/servletContext/starstar"), null, "user2:password2");
    }

    @Test
    public void testStartStar2() throws IOException {
        runSimpleUrlTest(((DefaultServer.getDefaultServerURL()) + "/star/starstar"), "user1:password1", "user2:password2");
    }

    @Test
    public void testExtensionMatch() throws IOException {
        runSimpleUrlTest(((DefaultServer.getDefaultServerURL()) + "/servletContext/extension/a.html"), "user1:password1", "user2:password2");
        TestHttpClient client = new TestHttpClient();
        try {
            HttpGet get = new HttpGet(((DefaultServer.getDefaultServerURL()) + "/servletContext/public/a.html"));
            get.addHeader("ExpectedMechanism", "None");
            get.addHeader("ExpectedUser", "None");
            HttpResponse result = client.execute(get);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            HttpClientUtils.readResponse(result);
        } finally {
            client.getConnectionManager().shutdown();
        }
    }

    @Test
    public void testAggregatedRoles() throws IOException {
        runSimpleUrlTest(((DefaultServer.getDefaultServerURL()) + "/servletContext/secured/1/2/aa"), "user4:password4", "user3:password3");
        runSimpleUrlTest(((DefaultServer.getDefaultServerURL()) + "/servletContext/secured/1/2/aa"), "user1:password1", "user2:password2");
    }

    @Test
    public void testHttpMethod() throws IOException {
        TestHttpClient client = new TestHttpClient();
        final String url = (DefaultServer.getDefaultServerURL()) + "/servletContext/public/postSecured/a";
        try {
            HttpGet initialGet = new HttpGet(url);
            initialGet.addHeader("ExpectedMechanism", "None");
            initialGet.addHeader("ExpectedUser", "None");
            HttpResponse result = client.execute(initialGet);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            HttpClientUtils.readResponse(result);
            HttpPost post = new HttpPost(url);
            result = client.execute(post);
            Assert.assertEquals(UNAUTHORIZED, result.getStatusLine().getStatusCode());
            Header[] values = result.getHeaders(WWW_AUTHENTICATE.toString());
            Assert.assertEquals(1, values.length);
            Assert.assertEquals(((BASIC) + " realm=\"Test Realm\""), values[0].getValue());
            HttpClientUtils.readResponse(result);
            post = new HttpPost(url);
            post.addHeader(AUTHORIZATION.toString(), (((BASIC) + " ") + (FlexBase64.encodeString("user2:password2".getBytes(), false))));
            result = client.execute(post);
            Assert.assertEquals(FORBIDDEN, result.getStatusLine().getStatusCode());
            HttpClientUtils.readResponse(result);
            post = new HttpPost(url);
            post.addHeader(AUTHORIZATION.toString(), (((BASIC) + " ") + (FlexBase64.encodeString("user1:password1".getBytes(), false))));
            post.addHeader("ExpectedMechanism", "BASIC");
            post.addHeader("ExpectedUser", "user1");
            result = client.execute(post);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            final String response = HttpClientUtils.readResponse(result);
            Assert.assertEquals(SecurityConstraintUrlMappingTestCase.HELLO_WORLD, response);
        } finally {
            client.getConnectionManager().shutdown();
        }
    }
}

