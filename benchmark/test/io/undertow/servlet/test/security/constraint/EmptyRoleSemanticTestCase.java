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
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * A test case for the three supported {@link EmptyRoleSemantic} values.
 *
 * @author <a href="mailto:darran.lofthouse@jboss.com">Darran Lofthouse</a>
 */
@RunWith(DefaultServer.class)
public class EmptyRoleSemanticTestCase {
    public static final String HELLO_WORLD = "Hello World";

    @Test
    public void testPermit() throws Exception {
        TestHttpClient client = new TestHttpClient();
        final String url = (DefaultServer.getDefaultServerURL()) + "/servletContext/permit";
        try {
            HttpGet initialGet = new HttpGet(url);
            initialGet.addHeader("ExpectedMechanism", "None");
            initialGet.addHeader("ExpectedUser", "None");
            HttpResponse result = client.execute(initialGet);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            final String response = HttpClientUtils.readResponse(result);
            Assert.assertEquals(EmptyRoleSemanticTestCase.HELLO_WORLD, response);
        } finally {
            client.getConnectionManager().shutdown();
        }
    }

    @Test
    public void testDeny() throws Exception {
        TestHttpClient client = new TestHttpClient();
        final String url = (DefaultServer.getDefaultServerURL()) + "/servletContext/deny";
        try {
            HttpGet initialGet = new HttpGet(url);
            initialGet.addHeader("ExpectedMechanism", "None");
            initialGet.addHeader("ExpectedUser", "None");
            HttpResponse result = client.execute(initialGet);
            Assert.assertEquals(FORBIDDEN, result.getStatusLine().getStatusCode());
        } finally {
            client.getConnectionManager().shutdown();
        }
    }

    @Test
    public void testAuthenticate() throws Exception {
        TestHttpClient client = new TestHttpClient();
        final String url = (DefaultServer.getDefaultServerURL()) + "/servletContext/authenticate";
        try {
            HttpGet get = new HttpGet(url);
            HttpResponse result = client.execute(get);
            Assert.assertEquals(UNAUTHORIZED, result.getStatusLine().getStatusCode());
            Header[] values = result.getHeaders(WWW_AUTHENTICATE.toString());
            Assert.assertEquals(1, values.length);
            Assert.assertEquals(((BASIC) + " realm=\"Test Realm\""), values[0].getValue());
            HttpClientUtils.readResponse(result);
            get = new HttpGet(url);
            get.addHeader("ExpectedMechanism", "BASIC");
            get.addHeader("ExpectedUser", "user1");
            get.addHeader(AUTHORIZATION.toString(), (((BASIC) + " ") + (FlexBase64.encodeString("user1:password1".getBytes(), false))));
            result = client.execute(get);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            final String response = HttpClientUtils.readResponse(result);
            Assert.assertEquals(EmptyRoleSemanticTestCase.HELLO_WORLD, response);
        } finally {
            client.getConnectionManager().shutdown();
        }
    }
}

