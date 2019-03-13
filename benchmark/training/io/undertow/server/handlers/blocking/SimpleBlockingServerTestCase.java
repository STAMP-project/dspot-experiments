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
package io.undertow.server.handlers.blocking;


import Headers.CONTENT_LENGTH_STRING;
import StatusCodes.OK;
import io.undertow.testutils.DefaultServer;
import io.undertow.testutils.HttpClientUtils;
import io.undertow.testutils.TestHttpClient;
import java.io.IOException;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Stuart Douglas
 */
@RunWith(DefaultServer.class)
public class SimpleBlockingServerTestCase {
    private static volatile String message;

    @Test
    public void sendHttpRequest() throws IOException {
        SimpleBlockingServerTestCase.message = "My HTTP Request!";
        TestHttpClient client = new TestHttpClient();
        try {
            HttpGet get = new HttpGet(((DefaultServer.getDefaultServerURL()) + "/path"));
            HttpResponse result = client.execute(get);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            Assert.assertEquals(SimpleBlockingServerTestCase.message, HttpClientUtils.readResponse(result));
        } finally {
            client.getConnectionManager().shutdown();
        }
    }

    @Test
    public void testHeadRequests() throws IOException {
        SimpleBlockingServerTestCase.message = "My HTTP Request!";
        TestHttpClient client = new TestHttpClient();
        HttpHead head = new HttpHead(((DefaultServer.getDefaultServerURL()) + "/path"));
        try {
            for (int i = 0; i < 3; ++i) {
                // WFLY-1540 run a few requests to make sure persistent re
                HttpResponse result = client.execute(head);
                Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
                Assert.assertEquals("", HttpClientUtils.readResponse(result));
                Assert.assertEquals(((SimpleBlockingServerTestCase.message.length()) + ""), result.getFirstHeader(CONTENT_LENGTH_STRING).getValue());
            }
        } finally {
            client.getConnectionManager().shutdown();
        }
    }

    @Test
    public void testDeleteRequests() throws IOException {
        SimpleBlockingServerTestCase.message = "My HTTP Request!";
        TestHttpClient client = new TestHttpClient();
        HttpDelete delete = new HttpDelete(((DefaultServer.getDefaultServerURL()) + "/path"));
        try {
            for (int i = 0; i < 3; ++i) {
                // WFLY-1540 run a few requests to make sure persistent re
                HttpResponse result = client.execute(delete);
                Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
                Assert.assertEquals(SimpleBlockingServerTestCase.message, HttpClientUtils.readResponse(result));
            }
        } finally {
            client.getConnectionManager().shutdown();
        }
    }

    @Test
    public void testLargeResponse() throws IOException {
        final StringBuilder messageBuilder = new StringBuilder(6919638);
        for (int i = 0; i < 6919638; ++i) {
            messageBuilder.append("*");
        }
        SimpleBlockingServerTestCase.message = messageBuilder.toString();
        TestHttpClient client = new TestHttpClient();
        try {
            HttpGet get = new HttpGet(((DefaultServer.getDefaultServerURL()) + "/path"));
            HttpResponse result = client.execute(get);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            String resultString = HttpClientUtils.readResponse(result);
            Assert.assertEquals(SimpleBlockingServerTestCase.message.length(), resultString.length());
            Assert.assertTrue(SimpleBlockingServerTestCase.message.equals(resultString));
            get = new HttpGet(((DefaultServer.getDefaultServerURL()) + "/path?useSender"));
            result = client.execute(get);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            String resultBody = HttpClientUtils.readResponse(result);
            Assert.assertTrue(SimpleBlockingServerTestCase.message.equals(resultBody));
            get = new HttpGet(((DefaultServer.getDefaultServerURL()) + "/path?useFragmentedSender"));
            result = client.execute(get);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            resultBody = HttpClientUtils.readResponse(result);
            Assert.assertTrue(SimpleBlockingServerTestCase.message.equals(resultBody));
        } finally {
            client.getConnectionManager().shutdown();
        }
    }

    @Test
    public void testSmallRequest() throws IOException {
        SimpleBlockingServerTestCase.message = null;
        TestHttpClient client = new TestHttpClient();
        try {
            HttpPost post = new HttpPost(((DefaultServer.getDefaultServerURL()) + "/path"));
            post.setEntity(new StringEntity("a"));
            HttpResponse result = client.execute(post);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            Assert.assertTrue("a".equals(HttpClientUtils.readResponse(result)));
        } finally {
            client.getConnectionManager().shutdown();
        }
    }

    @Test
    public void testLargeRequest() throws IOException {
        SimpleBlockingServerTestCase.message = null;
        final StringBuilder messageBuilder = new StringBuilder(6919638);
        for (int i = 0; i < 6919638; ++i) {
            messageBuilder.append("+");
        }
        TestHttpClient client = new TestHttpClient();
        try {
            HttpPost post = new HttpPost(((DefaultServer.getDefaultServerURL()) + "/path"));
            post.setEntity(new StringEntity(messageBuilder.toString()));
            HttpResponse result = client.execute(post);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            Assert.assertTrue(messageBuilder.toString().equals(HttpClientUtils.readResponse(result)));
        } finally {
            client.getConnectionManager().shutdown();
        }
    }
}

