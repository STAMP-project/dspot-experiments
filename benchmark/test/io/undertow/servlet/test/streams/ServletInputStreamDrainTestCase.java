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
package io.undertow.servlet.test.streams;


import StatusCodes.OK;
import io.undertow.testutils.DefaultServer;
import io.undertow.testutils.HttpClientUtils;
import io.undertow.testutils.TestHttpClient;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Tests calling close on the input stream before all data has been read.
 *
 * @author Stuart Douglas
 */
@RunWith(DefaultServer.class)
public class ServletInputStreamDrainTestCase {
    public static final String SERVLET = "servlet";

    private static final String HELLO_WORLD = "Hello World";

    @Test
    public void testServletInputStreamEarlyClose() throws Exception {
        StringBuilder builder = new StringBuilder((1000 * (ServletInputStreamDrainTestCase.HELLO_WORLD.length())));
        for (int i = 0; i < 10; ++i) {
            try {
                for (int j = 0; j < 1000; ++j) {
                    builder.append(ServletInputStreamDrainTestCase.HELLO_WORLD);
                }
            } catch (Throwable e) {
                throw new RuntimeException(("test failed with i equal to " + i), e);
            }
        }
        String message = builder.toString();
        TestHttpClient client = new TestHttpClient();
        try {
            String uri = ((DefaultServer.getDefaultServerURL()) + "/servletContext/") + (ServletInputStreamDrainTestCase.SERVLET);
            HttpPost post = new HttpPost(uri);
            post.setEntity(new StringEntity(message));
            HttpResponse result = client.execute(post);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            Assert.assertEquals("close", HttpClientUtils.readResponse(result));
            result = client.execute(post);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            Assert.assertEquals("close", HttpClientUtils.readResponse(result));
            result = client.execute(post);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            Assert.assertEquals("close", HttpClientUtils.readResponse(result));
        } finally {
            client.getConnectionManager().shutdown();
        }
    }
}

