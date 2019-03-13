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
package io.undertow.servlet.test.security.custom;


import StatusCodes.OK;
import io.undertow.testutils.DefaultServer;
import io.undertow.testutils.HttpClientUtils;
import io.undertow.testutils.TestHttpClient;
import io.undertow.util.StatusCodes;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Test case that validates the use of the DeploymentManagerImpl authMechanism override
 *
 * @author Stuart Douglas
 * @author Anil Saldhana
 */
@RunWith(DefaultServer.class)
public class ServletCustomAuthTestCase {
    public static final String HELLO_WORLD = "Hello World";

    @Test
    public void testServletCustomFormAuth() throws IOException {
        TestHttpClient client = new TestHttpClient();
        client.setRedirectStrategy(new DefaultRedirectStrategy() {
            @Override
            public boolean isRedirected(final HttpRequest request, final HttpResponse response, final HttpContext context) throws ProtocolException {
                if ((response.getStatusLine().getStatusCode()) == (StatusCodes.FOUND)) {
                    return true;
                }
                return super.isRedirected(request, response, context);
            }
        });
        try {
            final String uri = (DefaultServer.getDefaultServerURL()) + "/servletContext/secured/test";
            HttpGet get = new HttpGet(uri);
            HttpResponse result = client.execute(get);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            String response = HttpClientUtils.readResponse(result);
            Assert.assertTrue(response.startsWith("j_security_check"));
            BasicNameValuePair[] pairs = new BasicNameValuePair[]{ new BasicNameValuePair("j_username", "user1"), new BasicNameValuePair("j_password", "password1") };
            final List<NameValuePair> data = new ArrayList<>();
            data.addAll(Arrays.asList(pairs));
            HttpPost post = new HttpPost((((DefaultServer.getDefaultServerURL()) + "/servletContext/") + (CustomAuthenticationMechanism.POST_LOCATION)));
            post.setEntity(new UrlEncodedFormEntity(data));
            result = client.execute(post);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            response = HttpClientUtils.readResponse(result);
            Assert.assertEquals("user1", response);
        } finally {
            client.getConnectionManager().shutdown();
        }
    }
}

