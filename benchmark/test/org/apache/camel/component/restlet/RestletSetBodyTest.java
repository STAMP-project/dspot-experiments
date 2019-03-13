/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.restlet;


import java.io.InputStream;
import org.apache.camel.test.AvailablePortFinder;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.hamcrest.CoreMatchers;
import org.junit.Assume;
import org.junit.Test;


public class RestletSetBodyTest extends RestletTestSupport {
    protected static int portNum2 = AvailablePortFinder.getNextAvailable(4000);

    @Test
    public void testSetBody() throws Exception {
        String response = template.requestBody((("restlet:http://localhost:" + (RestletTestSupport.portNum)) + "/stock/ORCL?restletMethod=get"), null, String.class);
        assertEquals("110", response);
    }

    @Test
    public void testSetBodyRepresentation() throws Exception {
        HttpGet get = new HttpGet((("http://localhost:" + (RestletTestSupport.portNum)) + "/images/123"));
        try (CloseableHttpClient httpclient = HttpClientBuilder.create().build()) {
            HttpResponse response = httpclient.execute(get);
            assertEquals(200, response.getStatusLine().getStatusCode());
            assertEquals("image/png", response.getEntity().getContentType().getValue());
            assertEquals("Get wrong available size", 256, response.getEntity().getContentLength());
            try (InputStream is = response.getEntity().getContent()) {
                byte[] buffer = new byte[256];
                Assume.assumeThat("Should read all data", is.read(buffer), CoreMatchers.equalTo(256));
                assertThat("Data should match", buffer, CoreMatchers.equalTo(RestletSetBodyTest.getAllBytes()));
            }
        }
    }

    @Test
    public void consumerShouldReturnByteArray() throws Exception {
        HttpGet get = new HttpGet((("http://localhost:" + (RestletTestSupport.portNum)) + "/music/123"));
        try (CloseableHttpClient httpclient = HttpClientBuilder.create().build()) {
            HttpResponse response = httpclient.execute(get);
            assertEquals(200, response.getStatusLine().getStatusCode());
            assertEquals("audio/mpeg", response.getEntity().getContentType().getValue());
            assertEquals("Content length should match returned data", 256, response.getEntity().getContentLength());
            try (InputStream is = response.getEntity().getContent()) {
                byte[] buffer = new byte[256];
                Assume.assumeThat("Should read all data", is.read(buffer), CoreMatchers.equalTo(256));
                assertThat("Binary content should match", buffer, CoreMatchers.equalTo(RestletSetBodyTest.getAllBytes()));
            }
        }
    }

    @Test
    public void consumerShouldReturnInputStream() throws Exception {
        HttpGet get = new HttpGet((("http://localhost:" + (RestletTestSupport.portNum)) + "/video/123"));
        try (CloseableHttpClient httpclient = HttpClientBuilder.create().build()) {
            HttpResponse response = httpclient.execute(get);
            assertEquals(200, response.getStatusLine().getStatusCode());
            assertEquals("video/mp4", response.getEntity().getContentType().getValue());
            assertTrue("Content should be streamed", response.getEntity().isChunked());
            assertEquals("Content length should be unknown", (-1), response.getEntity().getContentLength());
            try (InputStream is = response.getEntity().getContent()) {
                byte[] buffer = new byte[256];
                Assume.assumeThat("Should read all data", is.read(buffer), CoreMatchers.equalTo(256));
                assertThat("Binary content should match", buffer, CoreMatchers.equalTo(RestletSetBodyTest.getAllBytes()));
            }
        }
    }

    @Test
    public void testGzipEntity() {
        String response = template.requestBody((("restlet:http://localhost:" + (RestletTestSupport.portNum)) + "/gzip/data?restletMethod=get"), null, String.class);
        assertEquals("Hello World!", response);
    }
}

