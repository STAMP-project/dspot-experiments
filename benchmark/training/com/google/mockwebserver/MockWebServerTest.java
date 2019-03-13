/**
 * Copyright (C) 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.mockwebserver;


import SocketPolicy.DISCONNECT_AT_START;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import junit.framework.TestCase;


public final class MockWebServerTest extends TestCase {
    private MockWebServer server = new MockWebServer();

    public void testRecordedRequestAccessors() {
        List<String> headers = Arrays.asList("User-Agent: okhttp", "Cookie: s=square", "Cookie: a=android", "X-Whitespace:  left", "X-Whitespace:right  ", "X-Whitespace:  both  ");
        List<Integer> chunkSizes = Collections.emptyList();
        byte[] body = new byte[]{ 'A', 'B', 'C' };
        String requestLine = "GET / HTTP/1.1";
        RecordedRequest request = new RecordedRequest(requestLine, headers, chunkSizes, body.length, body, 0, null);
        TestCase.assertEquals("s=square", request.getHeader("cookie"));
        TestCase.assertEquals(Arrays.asList("s=square", "a=android"), request.getHeaders("cookie"));
        TestCase.assertEquals("left", request.getHeader("x-whitespace"));
        TestCase.assertEquals(Arrays.asList("left", "right", "both"), request.getHeaders("x-whitespace"));
        TestCase.assertEquals("ABC", request.getUtf8Body());
    }

    public void testDefaultMockResponse() {
        MockResponse response = new MockResponse();
        TestCase.assertEquals(Arrays.asList("Content-Length: 0"), response.getHeaders());
        TestCase.assertEquals("HTTP/1.1 200 OK", response.getStatus());
    }

    public void testSetBodyAdjustsHeaders() throws IOException {
        MockResponse response = new MockResponse().setBody("ABC");
        TestCase.assertEquals(Arrays.asList("Content-Length: 3"), response.getHeaders());
        InputStream in = response.getBodyStream();
        TestCase.assertEquals('A', in.read());
        TestCase.assertEquals('B', in.read());
        TestCase.assertEquals('C', in.read());
        TestCase.assertEquals((-1), in.read());
        TestCase.assertEquals("HTTP/1.1 200 OK", response.getStatus());
    }

    public void testMockResponseAddHeader() {
        MockResponse response = new MockResponse().clearHeaders().addHeader("Cookie: s=square").addHeader("Cookie", "a=android");
        TestCase.assertEquals(Arrays.asList("Cookie: s=square", "Cookie: a=android"), response.getHeaders());
    }

    public void testMockResponseSetHeader() {
        MockResponse response = new MockResponse().clearHeaders().addHeader("Cookie: s=square").addHeader("Cookie: a=android").addHeader("Cookies: delicious");
        response.setHeader("cookie", "r=robot");
        TestCase.assertEquals(Arrays.asList("Cookies: delicious", "cookie: r=robot"), response.getHeaders());
    }

    public void testRegularResponse() throws Exception {
        server.enqueue(new MockResponse().setBody("hello world"));
        server.play();
        URL url = server.getUrl("/");
        HttpURLConnection connection = ((HttpURLConnection) (url.openConnection()));
        connection.setRequestProperty("Accept-Language", "en-US");
        InputStream in = connection.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        TestCase.assertEquals(HttpURLConnection.HTTP_OK, connection.getResponseCode());
        TestCase.assertEquals("hello world", reader.readLine());
        RecordedRequest request = server.takeRequest();
        TestCase.assertEquals("GET / HTTP/1.1", request.getRequestLine());
        TestCase.assertTrue(request.getHeaders().contains("Accept-Language: en-US"));
    }

    public void testRedirect() throws Exception {
        server.play();
        server.enqueue(new MockResponse().setResponseCode(HttpURLConnection.HTTP_MOVED_TEMP).addHeader(("Location: " + (server.getUrl("/new-path")))).setBody("This page has moved!"));
        server.enqueue(new MockResponse().setBody("This is the new location!"));
        URLConnection connection = server.getUrl("/").openConnection();
        InputStream in = connection.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        TestCase.assertEquals("This is the new location!", reader.readLine());
        RecordedRequest first = server.takeRequest();
        TestCase.assertEquals("GET / HTTP/1.1", first.getRequestLine());
        RecordedRequest redirect = server.takeRequest();
        TestCase.assertEquals("GET /new-path HTTP/1.1", redirect.getRequestLine());
    }

    /**
     * Test that MockWebServer blocks for a call to enqueue() if a request
     * is made before a mock response is ready.
     */
    public void testDispatchBlocksWaitingForEnqueue() throws Exception {
        server.play();
        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) {
                }
                server.enqueue(new MockResponse().setBody("enqueued in the background"));
            }
        }.start();
        URLConnection connection = server.getUrl("/").openConnection();
        InputStream in = connection.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        TestCase.assertEquals("enqueued in the background", reader.readLine());
    }

    public void testNonHexadecimalChunkSize() throws Exception {
        server.enqueue(new MockResponse().setBody("G\r\nxxxxxxxxxxxxxxxx\r\n0\r\n\r\n").clearHeaders().addHeader("Transfer-encoding: chunked"));
        server.play();
        URLConnection connection = server.getUrl("/").openConnection();
        InputStream in = connection.getInputStream();
        try {
            in.read();
            TestCase.fail();
        } catch (IOException expected) {
        }
    }

    public void testResponseTimeout() throws Exception {
        server.enqueue(new MockResponse().setBody("ABC").clearHeaders().addHeader("Content-Length: 4"));
        server.enqueue(new MockResponse().setBody("DEF"));
        server.play();
        URLConnection urlConnection = server.getUrl("/").openConnection();
        urlConnection.setReadTimeout(1000);
        InputStream in = urlConnection.getInputStream();
        TestCase.assertEquals('A', in.read());
        TestCase.assertEquals('B', in.read());
        TestCase.assertEquals('C', in.read());
        try {
            in.read();// if Content-Length was accurate, this would return -1 immediately

            TestCase.fail();
        } catch (SocketTimeoutException expected) {
        }
        URLConnection urlConnection2 = server.getUrl("/").openConnection();
        InputStream in2 = urlConnection2.getInputStream();
        TestCase.assertEquals('D', in2.read());
        TestCase.assertEquals('E', in2.read());
        TestCase.assertEquals('F', in2.read());
        TestCase.assertEquals((-1), in2.read());
        TestCase.assertEquals(0, server.takeRequest().getSequenceNumber());
        TestCase.assertEquals(0, server.takeRequest().getSequenceNumber());
    }

    public void testDisconnectAtStart() throws Exception {
        server.enqueue(new MockResponse().setSocketPolicy(DISCONNECT_AT_START));
        server.enqueue(new MockResponse());// The jdk's HttpUrlConnection is a bastard.

        server.enqueue(new MockResponse());
        server.play();
        try {
            server.getUrl("/a").openConnection().getInputStream();
        } catch (IOException e) {
            // Expected.
        }
        server.getUrl("/b").openConnection().getInputStream();// Should succeed.

    }

    public void testStreamingResponseBody() throws Exception {
        InputStream responseBody = new ByteArrayInputStream("ABC".getBytes("UTF-8"));
        server.enqueue(new MockResponse().setBody(responseBody, 3));
        server.play();
        InputStream in = server.getUrl("/").openConnection().getInputStream();
        TestCase.assertEquals('A', in.read());
        TestCase.assertEquals('B', in.read());
        TestCase.assertEquals('C', in.read());
        TestCase.assertEquals((-1), responseBody.read());// The body is exhausted.

    }
}

