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
package io.undertow.servlet.test.security.login;


import StatusCodes.OK;
import StatusCodes.UNAUTHORIZED;
import io.undertow.testutils.DefaultServer;
import io.undertow.testutils.HttpClientUtils;
import io.undertow.testutils.TestHttpClient;
import java.io.IOException;
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
public class ServletLoginTestCase {
    @Test
    public void testHttpMethod() throws IOException {
        TestHttpClient client = new TestHttpClient();
        final String url = (DefaultServer.getDefaultServerURL()) + "/servletContext/login";
        try {
            HttpGet get = new HttpGet(url);
            get.addHeader("username", "bob");
            get.addHeader("password", "bogus");
            HttpResponse result = client.execute(get);
            Assert.assertEquals(UNAUTHORIZED, result.getStatusLine().getStatusCode());
            HttpClientUtils.readResponse(result);
            get = new HttpGet(url);
            get.addHeader("username", "user1");
            get.addHeader("password", "password1");
            result = client.execute(get);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            String response = HttpClientUtils.readResponse(result);
            Assert.assertEquals("user1", response);
            get = new HttpGet(url);
            result = client.execute(get);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            response = HttpClientUtils.readResponse(result);
            Assert.assertEquals("user1", response);
        } finally {
            client.getConnectionManager().shutdown();
        }
    }
}

