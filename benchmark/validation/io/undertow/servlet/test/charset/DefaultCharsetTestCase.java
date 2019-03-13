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
package io.undertow.servlet.test.charset;


import StatusCodes.OK;
import io.undertow.testutils.DefaultServer;
import io.undertow.testutils.HttpClientUtils;
import io.undertow.testutils.TestHttpClient;
import java.io.IOException;
import java.util.Collections;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Stuart Douglas
 */
@RunWith(DefaultServer.class)
public class DefaultCharsetTestCase {
    private static final byte[] UTF8 = DefaultCharsetTestCase.toByteArray(new int[]{ 65, 194, 169, 195, 169, 204, 129, 224, 165, 129, 240, 157, 148, 138 });

    @Test
    public void testCharacterEncodingWriter() throws IOException {
        TestHttpClient client = new TestHttpClient();
        try {
            HttpGet get = new HttpGet(((DefaultServer.getDefaultServerURL()) + "/servletContext/writer"));
            HttpResponse result = client.execute(get);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            byte[] response = HttpClientUtils.readRawResponse(result);
            Assert.assertArrayEquals(DefaultCharsetTestCase.UTF8, response);
            get = new HttpGet(((DefaultServer.getDefaultServerURL()) + "/servletContext/writer?array=true"));
            result = client.execute(get);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            response = HttpClientUtils.readRawResponse(result);
            Assert.assertArrayEquals(DefaultCharsetTestCase.UTF8, response);
        } finally {
            client.getConnectionManager().shutdown();
        }
    }

    @Test
    public void testCharacterEncodingFormParser() throws IOException {
        TestHttpClient client = new TestHttpClient();
        try {
            HttpPost post = new HttpPost(((DefaultServer.getDefaultServerURL()) + "/servletContext/form"));
            post.setEntity(new UrlEncodedFormEntity(Collections.singletonList(new BasicNameValuePair("A\u00a9\u00e9\u0301\u0941\ud835\udd0a", "A\u00a9\u00e9\u0301\u0941\ud835\udd0a")), "UTF-8"));
            HttpResponse result = client.execute(post);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            byte[] response = HttpClientUtils.readRawResponse(result);
            Assert.assertArrayEquals(DefaultCharsetTestCase.UTF8, response);
        } finally {
            client.getConnectionManager().shutdown();
        }
    }
}

