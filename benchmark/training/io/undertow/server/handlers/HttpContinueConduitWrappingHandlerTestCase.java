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
package io.undertow.server.handlers;


import StatusCodes.EXPECTATION_FAILED;
import StatusCodes.OK;
import io.undertow.testutils.DefaultServer;
import io.undertow.testutils.HttpClientUtils;
import io.undertow.testutils.TestHttpClient;
import java.io.IOException;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Stuart Douglas
 */
@RunWith(DefaultServer.class)
public class HttpContinueConduitWrappingHandlerTestCase {
    private static volatile boolean accept = false;

    @Test
    public void testHttpContinueRejected() throws IOException {
        HttpContinueConduitWrappingHandlerTestCase.accept = false;
        String message = "My HTTP Request!";
        HttpParams httpParams = new BasicHttpParams();
        httpParams.setParameter("http.protocol.wait-for-continue", Integer.MAX_VALUE);
        TestHttpClient client = new TestHttpClient();
        client.setParams(httpParams);
        try {
            HttpPost post = new HttpPost(((DefaultServer.getDefaultServerURL()) + "/path"));
            post.addHeader("Expect", "100-continue");
            post.setEntity(new StringEntity(message));
            HttpResponse result = client.execute(post);
            Assert.assertEquals(EXPECTATION_FAILED, result.getStatusLine().getStatusCode());
        } finally {
            client.getConnectionManager().shutdown();
        }
    }

    @Test
    public void testHttpContinueAccepted() throws IOException {
        HttpContinueConduitWrappingHandlerTestCase.accept = true;
        String message = "My HTTP Request!";
        HttpParams httpParams = new BasicHttpParams();
        httpParams.setParameter("http.protocol.wait-for-continue", Integer.MAX_VALUE);
        TestHttpClient client = new TestHttpClient();
        client.setParams(httpParams);
        try {
            HttpPost post = new HttpPost(((DefaultServer.getDefaultServerURL()) + "/path"));
            post.addHeader("Expect", "100-continue");
            post.setEntity(new StringEntity(message));
            HttpResponse result = client.execute(post);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            Assert.assertEquals(message, HttpClientUtils.readResponse(result));
        } finally {
            client.getConnectionManager().shutdown();
        }
    }

    // UNDERTOW-162
    @Test
    public void testHttpContinueAcceptedWithChunkedRequest() throws IOException {
        HttpContinueConduitWrappingHandlerTestCase.accept = true;
        String message = "My HTTP Request!";
        HttpParams httpParams = new BasicHttpParams();
        httpParams.setParameter("http.protocol.wait-for-continue", Integer.MAX_VALUE);
        TestHttpClient client = new TestHttpClient();
        client.setParams(httpParams);
        try {
            HttpPost post = new HttpPost(((DefaultServer.getDefaultServerURL()) + "/path"));
            post.addHeader("Expect", "100-continue");
            post.setEntity(new StringEntity(message) {
                @Override
                public long getContentLength() {
                    return -1;
                }
            });
            HttpResponse result = client.execute(post);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            Assert.assertEquals(message, HttpClientUtils.readResponse(result));
        } finally {
            client.getConnectionManager().shutdown();
        }
    }
}

