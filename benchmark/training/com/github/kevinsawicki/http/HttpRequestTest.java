/**
 * Copyright (c) 2014 Kevin Sawicki <kevinsawicki@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to
 * deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 */
package com.github.kevinsawicki.http;


import HttpServletResponse.SC_NOT_FOUND;
import com.github.kevinsawicki.http.HttpRequest.ConnectionFactory;
import com.github.kevinsawicki.http.HttpRequest.HttpRequestException;
import com.github.kevinsawicki.http.HttpRequest.UploadProgress;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.zip.GZIPOutputStream;
import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.util.B64Code;
import org.junit.Assert;
import org.junit.Test;

import static java.util.Collections.emptyMap;


/**
 * Unit tests of {@link HttpRequest}
 */
public class HttpRequestTest extends ServerTestCase {
    private static String url;

    private static ServerTestCase.RequestHandler handler;

    /**
     * Create request with malformed URL
     */
    @Test(expected = HttpRequestException.class)
    public void malformedStringUrl() {
        HttpRequest.get("\\m/");
    }

    /**
     * Create request with malformed URL
     */
    @Test
    public void malformedStringUrlCause() {
        try {
            HttpRequest.delete("\\m/");
            Assert.fail("Exception not thrown");
        } catch (HttpRequestException e) {
            Assert.assertNotNull(e.getCause());
        }
    }

    /**
     * Set request buffer size to negative value
     */
    @Test(expected = IllegalArgumentException.class)
    public void negativeBufferSize() {
        HttpRequest.get("http://localhost").bufferSize((-1));
    }

    /**
     * Make a GET request with an empty body response
     *
     * @throws Exception
     * 		
     */
    @Test
    public void getEmpty() throws Exception {
        final AtomicReference<String> method = new AtomicReference<String>();
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                method.set(request.getMethod());
                response.setStatus(HttpURLConnection.HTTP_OK);
            }
        };
        HttpRequest request = HttpRequest.get(HttpRequestTest.url);
        Assert.assertNotNull(request.getConnection());
        Assert.assertEquals(30000, request.readTimeout(30000).getConnection().getReadTimeout());
        Assert.assertEquals(50000, request.connectTimeout(50000).getConnection().getConnectTimeout());
        Assert.assertEquals(2500, request.bufferSize(2500).bufferSize());
        Assert.assertFalse(request.ignoreCloseExceptions(false).ignoreCloseExceptions());
        Assert.assertFalse(request.useCaches(false).getConnection().getUseCaches());
        int code = request.code();
        Assert.assertTrue(request.ok());
        Assert.assertFalse(request.created());
        Assert.assertFalse(request.badRequest());
        Assert.assertFalse(request.serverError());
        Assert.assertFalse(request.notFound());
        Assert.assertFalse(request.notModified());
        Assert.assertEquals("GET", method.get());
        Assert.assertEquals("OK", request.message());
        Assert.assertEquals(HttpURLConnection.HTTP_OK, code);
        Assert.assertEquals("", request.body());
        Assert.assertNotNull(request.toString());
        Assert.assertFalse(((request.toString().length()) == 0));
        Assert.assertEquals(request, request.disconnect());
        Assert.assertTrue(request.isBodyEmpty());
        Assert.assertEquals(request.url().toString(), HttpRequestTest.url);
        Assert.assertEquals("GET", request.method());
    }

    /**
     * Make a GET request with an empty body response
     *
     * @throws Exception
     * 		
     */
    @Test
    public void getUrlEmpty() throws Exception {
        final AtomicReference<String> method = new AtomicReference<String>();
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                method.set(request.getMethod());
                response.setStatus(HttpURLConnection.HTTP_OK);
            }
        };
        HttpRequest request = HttpRequest.get(new URL(HttpRequestTest.url));
        Assert.assertNotNull(request.getConnection());
        int code = request.code();
        Assert.assertTrue(request.ok());
        Assert.assertFalse(request.created());
        Assert.assertFalse(request.noContent());
        Assert.assertFalse(request.badRequest());
        Assert.assertFalse(request.serverError());
        Assert.assertFalse(request.notFound());
        Assert.assertEquals("GET", method.get());
        Assert.assertEquals("OK", request.message());
        Assert.assertEquals(HttpURLConnection.HTTP_OK, code);
        Assert.assertEquals("", request.body());
    }

    /**
     * Make a GET request with an empty body response
     *
     * @throws Exception
     * 		
     */
    @Test
    public void getNoContent() throws Exception {
        final AtomicReference<String> method = new AtomicReference<String>();
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                method.set(request.getMethod());
                response.setStatus(HttpURLConnection.HTTP_NO_CONTENT);
            }
        };
        HttpRequest request = HttpRequest.get(new URL(HttpRequestTest.url));
        Assert.assertNotNull(request.getConnection());
        int code = request.code();
        Assert.assertFalse(request.ok());
        Assert.assertFalse(request.created());
        Assert.assertTrue(request.noContent());
        Assert.assertFalse(request.badRequest());
        Assert.assertFalse(request.serverError());
        Assert.assertFalse(request.notFound());
        Assert.assertEquals("GET", method.get());
        Assert.assertEquals("No Content", request.message());
        Assert.assertEquals(HttpURLConnection.HTTP_NO_CONTENT, code);
        Assert.assertEquals("", request.body());
    }

    /**
     * Make a GET request with a URL that needs encoding
     *
     * @throws Exception
     * 		
     */
    @Test
    public void getUrlEncodedWithSpace() throws Exception {
        String unencoded = "/a resource";
        final AtomicReference<String> path = new AtomicReference<String>();
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                path.set(request.getPathInfo());
                response.setStatus(HttpURLConnection.HTTP_OK);
            }
        };
        HttpRequest request = HttpRequest.get(HttpRequest.encode(((HttpRequestTest.url) + unencoded)));
        Assert.assertTrue(request.ok());
        Assert.assertEquals(unencoded, path.get());
    }

    /**
     * Make a GET request with a URL that needs encoding
     *
     * @throws Exception
     * 		
     */
    @Test
    public void getUrlEncodedWithUnicode() throws Exception {
        String unencoded = "/\u00df";
        final AtomicReference<String> path = new AtomicReference<String>();
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                path.set(request.getPathInfo());
                response.setStatus(HttpURLConnection.HTTP_OK);
            }
        };
        HttpRequest request = HttpRequest.get(HttpRequest.encode(((HttpRequestTest.url) + unencoded)));
        Assert.assertTrue(request.ok());
        Assert.assertEquals(unencoded, path.get());
    }

    /**
     * Make a GET request with a URL that needs encoding
     *
     * @throws Exception
     * 		
     */
    @Test
    public void getUrlEncodedWithPercent() throws Exception {
        String unencoded = "/%";
        final AtomicReference<String> path = new AtomicReference<String>();
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                path.set(request.getPathInfo());
                response.setStatus(HttpURLConnection.HTTP_OK);
            }
        };
        HttpRequest request = HttpRequest.get(HttpRequest.encode(((HttpRequestTest.url) + unencoded)));
        Assert.assertTrue(request.ok());
        Assert.assertEquals(unencoded, path.get());
    }

    /**
     * Make a DELETE request with an empty body response
     *
     * @throws Exception
     * 		
     */
    @Test
    public void deleteEmpty() throws Exception {
        final AtomicReference<String> method = new AtomicReference<String>();
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                method.set(request.getMethod());
                response.setStatus(HttpURLConnection.HTTP_OK);
            }
        };
        HttpRequest request = HttpRequest.delete(HttpRequestTest.url);
        Assert.assertNotNull(request.getConnection());
        Assert.assertTrue(request.ok());
        Assert.assertFalse(request.notFound());
        Assert.assertEquals("DELETE", method.get());
        Assert.assertEquals("", request.body());
        Assert.assertEquals("DELETE", request.method());
    }

    /**
     * Make a DELETE request with an empty body response
     *
     * @throws Exception
     * 		
     */
    @Test
    public void deleteUrlEmpty() throws Exception {
        final AtomicReference<String> method = new AtomicReference<String>();
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                method.set(request.getMethod());
                response.setStatus(HttpURLConnection.HTTP_OK);
            }
        };
        HttpRequest request = HttpRequest.delete(new URL(HttpRequestTest.url));
        Assert.assertNotNull(request.getConnection());
        Assert.assertTrue(request.ok());
        Assert.assertFalse(request.notFound());
        Assert.assertEquals("DELETE", method.get());
        Assert.assertEquals("", request.body());
    }

    /**
     * Make an OPTIONS request with an empty body response
     *
     * @throws Exception
     * 		
     */
    @Test
    public void optionsEmpty() throws Exception {
        final AtomicReference<String> method = new AtomicReference<String>();
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                method.set(request.getMethod());
                response.setStatus(HttpURLConnection.HTTP_OK);
            }
        };
        HttpRequest request = HttpRequest.options(HttpRequestTest.url);
        Assert.assertNotNull(request.getConnection());
        Assert.assertTrue(request.ok());
        Assert.assertFalse(request.notFound());
        Assert.assertEquals("OPTIONS", method.get());
        Assert.assertEquals("", request.body());
    }

    /**
     * Make an OPTIONS request with an empty body response
     *
     * @throws Exception
     * 		
     */
    @Test
    public void optionsUrlEmpty() throws Exception {
        final AtomicReference<String> method = new AtomicReference<String>();
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                method.set(request.getMethod());
                response.setStatus(HttpURLConnection.HTTP_OK);
            }
        };
        HttpRequest request = HttpRequest.options(new URL(HttpRequestTest.url));
        Assert.assertNotNull(request.getConnection());
        Assert.assertTrue(request.ok());
        Assert.assertFalse(request.notFound());
        Assert.assertEquals("OPTIONS", method.get());
        Assert.assertEquals("", request.body());
    }

    /**
     * Make a HEAD request with an empty body response
     *
     * @throws Exception
     * 		
     */
    @Test
    public void headEmpty() throws Exception {
        final AtomicReference<String> method = new AtomicReference<String>();
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                method.set(request.getMethod());
                response.setStatus(HttpURLConnection.HTTP_OK);
            }
        };
        HttpRequest request = HttpRequest.head(HttpRequestTest.url);
        Assert.assertNotNull(request.getConnection());
        Assert.assertTrue(request.ok());
        Assert.assertFalse(request.notFound());
        Assert.assertEquals("HEAD", method.get());
        Assert.assertEquals("", request.body());
    }

    /**
     * Make a HEAD request with an empty body response
     *
     * @throws Exception
     * 		
     */
    @Test
    public void headUrlEmpty() throws Exception {
        final AtomicReference<String> method = new AtomicReference<String>();
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                method.set(request.getMethod());
                response.setStatus(HttpURLConnection.HTTP_OK);
            }
        };
        HttpRequest request = HttpRequest.head(new URL(HttpRequestTest.url));
        Assert.assertNotNull(request.getConnection());
        Assert.assertTrue(request.ok());
        Assert.assertFalse(request.notFound());
        Assert.assertEquals("HEAD", method.get());
        Assert.assertEquals("", request.body());
    }

    /**
     * Make a PUT request with an empty body response
     *
     * @throws Exception
     * 		
     */
    @Test
    public void putEmpty() throws Exception {
        final AtomicReference<String> method = new AtomicReference<String>();
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                method.set(request.getMethod());
                response.setStatus(HttpURLConnection.HTTP_OK);
            }
        };
        HttpRequest request = HttpRequest.put(HttpRequestTest.url);
        Assert.assertNotNull(request.getConnection());
        Assert.assertTrue(request.ok());
        Assert.assertFalse(request.notFound());
        Assert.assertEquals("PUT", method.get());
        Assert.assertEquals("", request.body());
    }

    /**
     * Make a PUT request with an empty body response
     *
     * @throws Exception
     * 		
     */
    @Test
    public void putUrlEmpty() throws Exception {
        final AtomicReference<String> method = new AtomicReference<String>();
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                method.set(request.getMethod());
                response.setStatus(HttpURLConnection.HTTP_OK);
            }
        };
        HttpRequest request = HttpRequest.put(new URL(HttpRequestTest.url));
        Assert.assertNotNull(request.getConnection());
        Assert.assertTrue(request.ok());
        Assert.assertFalse(request.notFound());
        Assert.assertEquals("PUT", method.get());
        Assert.assertEquals("", request.body());
    }

    /**
     * Make a PUT request with an empty body response
     *
     * @throws Exception
     * 		
     */
    @Test
    public void traceEmpty() throws Exception {
        final AtomicReference<String> method = new AtomicReference<String>();
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                method.set(request.getMethod());
                response.setStatus(HttpURLConnection.HTTP_OK);
            }
        };
        HttpRequest request = HttpRequest.trace(HttpRequestTest.url);
        Assert.assertNotNull(request.getConnection());
        Assert.assertTrue(request.ok());
        Assert.assertFalse(request.notFound());
        Assert.assertEquals("TRACE", method.get());
        Assert.assertEquals("", request.body());
    }

    /**
     * Make a TRACE request with an empty body response
     *
     * @throws Exception
     * 		
     */
    @Test
    public void traceUrlEmpty() throws Exception {
        final AtomicReference<String> method = new AtomicReference<String>();
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                method.set(request.getMethod());
                response.setStatus(HttpURLConnection.HTTP_OK);
            }
        };
        HttpRequest request = HttpRequest.trace(new URL(HttpRequestTest.url));
        Assert.assertNotNull(request.getConnection());
        Assert.assertTrue(request.ok());
        Assert.assertFalse(request.notFound());
        Assert.assertEquals("TRACE", method.get());
        Assert.assertEquals("", request.body());
    }

    /**
     * Make a POST request with an empty request body
     *
     * @throws Exception
     * 		
     */
    @Test
    public void postEmpty() throws Exception {
        final AtomicReference<String> method = new AtomicReference<String>();
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                method.set(request.getMethod());
                response.setStatus(HttpURLConnection.HTTP_CREATED);
            }
        };
        HttpRequest request = HttpRequest.post(HttpRequestTest.url);
        int code = request.code();
        Assert.assertEquals("POST", method.get());
        Assert.assertFalse(request.ok());
        Assert.assertTrue(request.created());
        Assert.assertEquals(HttpURLConnection.HTTP_CREATED, code);
    }

    /**
     * Make a POST request with an empty request body
     *
     * @throws Exception
     * 		
     */
    @Test
    public void postUrlEmpty() throws Exception {
        final AtomicReference<String> method = new AtomicReference<String>();
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                method.set(request.getMethod());
                response.setStatus(HttpURLConnection.HTTP_CREATED);
            }
        };
        HttpRequest request = HttpRequest.post(new URL(HttpRequestTest.url));
        int code = request.code();
        Assert.assertEquals("POST", method.get());
        Assert.assertFalse(request.ok());
        Assert.assertTrue(request.created());
        Assert.assertEquals(HttpURLConnection.HTTP_CREATED, code);
    }

    /**
     * Make a POST request with a non-empty request body
     *
     * @throws Exception
     * 		
     */
    @Test
    public void postNonEmptyString() throws Exception {
        final AtomicReference<String> body = new AtomicReference<String>();
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                body.set(new String(read()));
                response.setStatus(HttpURLConnection.HTTP_OK);
            }
        };
        int code = HttpRequest.post(HttpRequestTest.url).send("hello").code();
        Assert.assertEquals(HttpURLConnection.HTTP_OK, code);
        Assert.assertEquals("hello", body.get());
    }

    /**
     * Make a POST request with a non-empty request body
     *
     * @throws Exception
     * 		
     */
    @Test
    public void postNonEmptyFile() throws Exception {
        final AtomicReference<String> body = new AtomicReference<String>();
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                body.set(new String(read()));
                response.setStatus(HttpURLConnection.HTTP_OK);
            }
        };
        File file = File.createTempFile("post", ".txt");
        new FileWriter(file).append("hello").close();
        int code = HttpRequest.post(HttpRequestTest.url).send(file).code();
        Assert.assertEquals(HttpURLConnection.HTTP_OK, code);
        Assert.assertEquals("hello", body.get());
    }

    /**
     * Make a POST request with multiple files in the body
     *
     * @throws Exception
     * 		
     */
    @Test
    public void postMultipleFiles() throws Exception {
        final AtomicReference<String> body = new AtomicReference<String>();
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                body.set(new String(read()));
                response.setStatus(HttpURLConnection.HTTP_OK);
            }
        };
        File file1 = File.createTempFile("post", ".txt");
        new FileWriter(file1).append("hello").close();
        File file2 = File.createTempFile("post", ".txt");
        new FileWriter(file2).append(" world").close();
        int code = HttpRequest.post(HttpRequestTest.url).send(file1).send(file2).code();
        Assert.assertEquals(HttpURLConnection.HTTP_OK, code);
        Assert.assertEquals("hello world", body.get());
    }

    /**
     * Make a POST request with a non-empty request body
     *
     * @throws Exception
     * 		
     */
    @Test
    public void postNonEmptyReader() throws Exception {
        final AtomicReference<String> body = new AtomicReference<String>();
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                body.set(new String(read()));
                response.setStatus(HttpURLConnection.HTTP_OK);
            }
        };
        File file = File.createTempFile("post", ".txt");
        new FileWriter(file).append("hello").close();
        int code = HttpRequest.post(HttpRequestTest.url).send(new FileReader(file)).code();
        Assert.assertEquals(HttpURLConnection.HTTP_OK, code);
        Assert.assertEquals("hello", body.get());
    }

    /**
     * Make a POST request with a non-empty request body
     *
     * @throws Exception
     * 		
     */
    @Test
    public void postNonEmptyByteArray() throws Exception {
        final AtomicReference<String> body = new AtomicReference<String>();
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                body.set(new String(read()));
                response.setStatus(HttpURLConnection.HTTP_OK);
            }
        };
        byte[] bytes = "hello".getBytes(HttpRequest.CHARSET_UTF8);
        int code = HttpRequest.post(HttpRequestTest.url).contentLength(Integer.toString(bytes.length)).send(bytes).code();
        Assert.assertEquals(HttpURLConnection.HTTP_OK, code);
        Assert.assertEquals("hello", body.get());
    }

    /**
     * Make a post with an explicit set of the content length
     *
     * @throws Exception
     * 		
     */
    @Test
    public void postWithLength() throws Exception {
        final AtomicReference<String> body = new AtomicReference<String>();
        final AtomicReference<Integer> length = new AtomicReference<Integer>();
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                body.set(new String(read()));
                length.set(request.getContentLength());
                response.setStatus(HttpURLConnection.HTTP_OK);
            }
        };
        String data = "hello";
        int sent = data.getBytes().length;
        int code = HttpRequest.post(HttpRequestTest.url).contentLength(sent).send(data).code();
        Assert.assertEquals(HttpURLConnection.HTTP_OK, code);
        Assert.assertEquals(sent, length.get().intValue());
        Assert.assertEquals(data, body.get());
    }

    /**
     * Make a post of form data
     *
     * @throws Exception
     * 		
     */
    @Test
    public void postForm() throws Exception {
        final AtomicReference<String> body = new AtomicReference<String>();
        final AtomicReference<String> contentType = new AtomicReference<String>();
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                body.set(new String(read()));
                contentType.set(request.getContentType());
                response.setStatus(HttpURLConnection.HTTP_OK);
            }
        };
        Map<String, String> data = new LinkedHashMap<String, String>();
        data.put("name", "user");
        data.put("number", "100");
        int code = HttpRequest.post(HttpRequestTest.url).form(data).form("zip", "12345").code();
        Assert.assertEquals(HttpURLConnection.HTTP_OK, code);
        Assert.assertEquals("name=user&number=100&zip=12345", body.get());
        Assert.assertEquals("application/x-www-form-urlencoded; charset=UTF-8", contentType.get());
    }

    /**
     * Make a post of form data
     *
     * @throws Exception
     * 		
     */
    @Test
    public void postFormWithNoCharset() throws Exception {
        final AtomicReference<String> body = new AtomicReference<String>();
        final AtomicReference<String> contentType = new AtomicReference<String>();
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                body.set(new String(read()));
                contentType.set(request.getContentType());
                response.setStatus(HttpURLConnection.HTTP_OK);
            }
        };
        Map<String, String> data = new LinkedHashMap<String, String>();
        data.put("name", "user");
        data.put("number", "100");
        int code = HttpRequest.post(HttpRequestTest.url).form(data, null).form("zip", "12345").code();
        Assert.assertEquals(HttpURLConnection.HTTP_OK, code);
        Assert.assertEquals("name=user&number=100&zip=12345", body.get());
        Assert.assertEquals("application/x-www-form-urlencoded", contentType.get());
    }

    /**
     * Make a post with an empty form data map
     *
     * @throws Exception
     * 		
     */
    @Test
    public void postEmptyForm() throws Exception {
        final AtomicReference<String> body = new AtomicReference<String>();
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                body.set(new String(read()));
                response.setStatus(HttpURLConnection.HTTP_OK);
            }
        };
        int code = HttpRequest.post(HttpRequestTest.url).form(new HashMap<String, String>()).code();
        Assert.assertEquals(HttpURLConnection.HTTP_OK, code);
        Assert.assertEquals("", body.get());
    }

    /**
     * Make a post in chunked mode
     *
     * @throws Exception
     * 		
     */
    @Test
    public void chunkPost() throws Exception {
        final AtomicReference<String> body = new AtomicReference<String>();
        final AtomicReference<String> encoding = new AtomicReference<String>();
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                body.set(new String(read()));
                response.setStatus(HttpURLConnection.HTTP_OK);
                encoding.set(request.getHeader("Transfer-Encoding"));
            }
        };
        String data = "hello";
        int code = HttpRequest.post(HttpRequestTest.url).chunk(2).send(data).code();
        Assert.assertEquals(HttpURLConnection.HTTP_OK, code);
        Assert.assertEquals(data, body.get());
        Assert.assertEquals("chunked", encoding.get());
    }

    /**
     * Make a GET request for a non-empty response body
     *
     * @throws Exception
     * 		
     */
    @Test
    public void getNonEmptyString() throws Exception {
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                response.setStatus(HttpURLConnection.HTTP_OK);
                write("hello");
            }
        };
        HttpRequest request = HttpRequest.get(HttpRequestTest.url);
        Assert.assertEquals(HttpURLConnection.HTTP_OK, request.code());
        Assert.assertEquals("hello", request.body());
        Assert.assertEquals("hello".getBytes().length, request.contentLength());
        Assert.assertFalse(request.isBodyEmpty());
    }

    /**
     * Make a GET request with a response that includes a charset parameter
     *
     * @throws Exception
     * 		
     */
    @Test
    public void getWithResponseCharset() throws Exception {
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                response.setStatus(HttpURLConnection.HTTP_OK);
                response.setContentType("text/html; charset=UTF-8");
            }
        };
        HttpRequest request = HttpRequest.get(HttpRequestTest.url);
        Assert.assertEquals(HttpURLConnection.HTTP_OK, request.code());
        Assert.assertEquals(HttpRequest.CHARSET_UTF8, request.charset());
    }

    /**
     * Make a GET request with a response that includes a charset parameter
     *
     * @throws Exception
     * 		
     */
    @Test
    public void getWithResponseCharsetAsSecondParam() throws Exception {
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                response.setStatus(HttpURLConnection.HTTP_OK);
                response.setContentType("text/html; param1=val1; charset=UTF-8");
            }
        };
        HttpRequest request = HttpRequest.get(HttpRequestTest.url);
        Assert.assertEquals(HttpURLConnection.HTTP_OK, request.code());
        Assert.assertEquals(HttpRequest.CHARSET_UTF8, request.charset());
    }

    /**
     * Make a GET request with basic authentication specified
     *
     * @throws Exception
     * 		
     */
    @Test
    public void basicAuthentication() throws Exception {
        final AtomicReference<String> user = new AtomicReference<String>();
        final AtomicReference<String> password = new AtomicReference<String>();
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                String auth = request.getHeader("Authorization");
                auth = auth.substring(((auth.indexOf(' ')) + 1));
                try {
                    auth = B64Code.decode(auth, HttpRequest.CHARSET_UTF8);
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
                int colon = auth.indexOf(':');
                user.set(auth.substring(0, colon));
                password.set(auth.substring((colon + 1)));
                response.setStatus(HttpURLConnection.HTTP_OK);
            }
        };
        Assert.assertTrue(HttpRequest.get(HttpRequestTest.url).basic("user", "p4ssw0rd").ok());
        Assert.assertEquals("user", user.get());
        Assert.assertEquals("p4ssw0rd", password.get());
    }

    /**
     * Make a GET request with basic proxy authentication specified
     *
     * @throws Exception
     * 		
     */
    @Test
    public void basicProxyAuthentication() throws Exception {
        final AtomicBoolean finalHostReached = new AtomicBoolean(false);
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                finalHostReached.set(true);
                response.setStatus(HttpURLConnection.HTTP_OK);
            }
        };
        Assert.assertTrue(HttpRequest.get(HttpRequestTest.url).useProxy("localhost", ServerTestCase.proxyPort).proxyBasic("user", "p4ssw0rd").ok());
        Assert.assertEquals("user", ServerTestCase.proxyUser.get());
        Assert.assertEquals("p4ssw0rd", ServerTestCase.proxyPassword.get());
        Assert.assertEquals(true, finalHostReached.get());
        Assert.assertEquals(1, ServerTestCase.proxyHitCount.get());
    }

    /**
     * Make a GET and get response as a input stream reader
     *
     * @throws Exception
     * 		
     */
    @Test
    public void getReader() throws Exception {
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                response.setStatus(HttpURLConnection.HTTP_OK);
                write("hello");
            }
        };
        HttpRequest request = HttpRequest.get(HttpRequestTest.url);
        Assert.assertTrue(request.ok());
        BufferedReader reader = new BufferedReader(request.reader());
        Assert.assertEquals("hello", reader.readLine());
        reader.close();
    }

    /**
     * Make a POST and send request using a writer
     *
     * @throws Exception
     * 		
     */
    @Test
    public void sendWithWriter() throws Exception {
        final AtomicReference<String> body = new AtomicReference<String>();
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                body.set(new String(read()));
                response.setStatus(HttpURLConnection.HTTP_OK);
            }
        };
        HttpRequest request = HttpRequest.post(HttpRequestTest.url);
        request.writer().append("hello").close();
        Assert.assertTrue(request.ok());
        Assert.assertEquals("hello", body.get());
    }

    /**
     * Make a GET and get response as a buffered reader
     *
     * @throws Exception
     * 		
     */
    @Test
    public void getBufferedReader() throws Exception {
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                response.setStatus(HttpURLConnection.HTTP_OK);
                write("hello");
            }
        };
        HttpRequest request = HttpRequest.get(HttpRequestTest.url);
        Assert.assertTrue(request.ok());
        BufferedReader reader = request.bufferedReader();
        Assert.assertEquals("hello", reader.readLine());
        reader.close();
    }

    /**
     * Make a GET and get response as a input stream reader
     *
     * @throws Exception
     * 		
     */
    @Test
    public void getReaderWithCharset() throws Exception {
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                response.setStatus(HttpURLConnection.HTTP_OK);
                write("hello");
            }
        };
        HttpRequest request = HttpRequest.get(HttpRequestTest.url);
        Assert.assertTrue(request.ok());
        BufferedReader reader = new BufferedReader(request.reader(HttpRequest.CHARSET_UTF8));
        Assert.assertEquals("hello", reader.readLine());
        reader.close();
    }

    /**
     * Make a GET and get response body as byte array
     *
     * @throws Exception
     * 		
     */
    @Test
    public void getBytes() throws Exception {
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                response.setStatus(HttpURLConnection.HTTP_OK);
                write("hello");
            }
        };
        HttpRequest request = HttpRequest.get(HttpRequestTest.url);
        Assert.assertTrue(request.ok());
        Assert.assertTrue(Arrays.equals("hello".getBytes(), request.bytes()));
    }

    /**
     * Make a GET request that returns an error string
     *
     * @throws Exception
     * 		
     */
    @Test
    public void getError() throws Exception {
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                response.setStatus(SC_NOT_FOUND);
                write("error");
            }
        };
        HttpRequest request = HttpRequest.get(HttpRequestTest.url);
        Assert.assertTrue(request.notFound());
        Assert.assertEquals("error", request.body());
    }

    /**
     * Make a GET request that returns an empty error string
     *
     * @throws Exception
     * 		
     */
    @Test
    public void noError() throws Exception {
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                response.setStatus(HttpURLConnection.HTTP_OK);
            }
        };
        HttpRequest request = HttpRequest.get(HttpRequestTest.url);
        Assert.assertTrue(request.ok());
        Assert.assertEquals("", request.body());
    }

    /**
     * Verify 'Server' header
     *
     * @throws Exception
     * 		
     */
    @Test
    public void serverHeader() throws Exception {
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                response.setStatus(HttpURLConnection.HTTP_OK);
                response.setHeader("Server", "aserver");
            }
        };
        Assert.assertEquals("aserver", HttpRequest.get(HttpRequestTest.url).server());
    }

    /**
     * Verify 'Expires' header
     *
     * @throws Exception
     * 		
     */
    @Test
    public void expiresHeader() throws Exception {
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                response.setStatus(HttpURLConnection.HTTP_OK);
                response.setDateHeader("Expires", 1234000);
            }
        };
        Assert.assertEquals(1234000, HttpRequest.get(HttpRequestTest.url).expires());
    }

    /**
     * Verify 'Last-Modified' header
     *
     * @throws Exception
     * 		
     */
    @Test
    public void lastModifiedHeader() throws Exception {
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                response.setStatus(HttpURLConnection.HTTP_OK);
                response.setDateHeader("Last-Modified", 555000);
            }
        };
        Assert.assertEquals(555000, HttpRequest.get(HttpRequestTest.url).lastModified());
    }

    /**
     * Verify 'Date' header
     *
     * @throws Exception
     * 		
     */
    @Test
    public void dateHeader() throws Exception {
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                response.setStatus(HttpURLConnection.HTTP_OK);
                response.setDateHeader("Date", 66000);
            }
        };
        Assert.assertEquals(66000, HttpRequest.get(HttpRequestTest.url).date());
    }

    /**
     * Verify 'ETag' header
     *
     * @throws Exception
     * 		
     */
    @Test
    public void eTagHeader() throws Exception {
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                response.setStatus(HttpURLConnection.HTTP_OK);
                response.setHeader("ETag", "abcd");
            }
        };
        Assert.assertEquals("abcd", HttpRequest.get(HttpRequestTest.url).eTag());
    }

    /**
     * Verify 'Location' header
     *
     * @throws Exception
     * 		
     */
    @Test
    public void locationHeader() throws Exception {
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                response.setStatus(HttpURLConnection.HTTP_OK);
                response.setHeader("Location", "http://nowhere");
            }
        };
        Assert.assertEquals("http://nowhere", HttpRequest.get(HttpRequestTest.url).location());
    }

    /**
     * Verify 'Content-Encoding' header
     *
     * @throws Exception
     * 		
     */
    @Test
    public void contentEncodingHeader() throws Exception {
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                response.setStatus(HttpURLConnection.HTTP_OK);
                response.setHeader("Content-Encoding", "gzip");
            }
        };
        Assert.assertEquals("gzip", HttpRequest.get(HttpRequestTest.url).contentEncoding());
    }

    /**
     * Verify 'Content-Type' header
     *
     * @throws Exception
     * 		
     */
    @Test
    public void contentTypeHeader() throws Exception {
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                response.setStatus(HttpURLConnection.HTTP_OK);
                response.setHeader("Content-Type", "text/html");
            }
        };
        Assert.assertEquals("text/html", HttpRequest.get(HttpRequestTest.url).contentType());
    }

    /**
     * Verify 'Content-Type' header
     *
     * @throws Exception
     * 		
     */
    @Test
    public void requestContentType() throws Exception {
        final AtomicReference<String> contentType = new AtomicReference<String>();
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                contentType.set(request.getContentType());
                response.setStatus(HttpURLConnection.HTTP_OK);
            }
        };
        Assert.assertTrue(HttpRequest.post(HttpRequestTest.url).contentType("text/html", "UTF-8").ok());
        Assert.assertEquals("text/html; charset=UTF-8", contentType.get());
    }

    /**
     * Verify 'Content-Type' header
     *
     * @throws Exception
     * 		
     */
    @Test
    public void requestContentTypeNullCharset() throws Exception {
        final AtomicReference<String> contentType = new AtomicReference<String>();
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                contentType.set(request.getContentType());
                response.setStatus(HttpURLConnection.HTTP_OK);
            }
        };
        Assert.assertTrue(HttpRequest.post(HttpRequestTest.url).contentType("text/html", null).ok());
        Assert.assertEquals("text/html", contentType.get());
    }

    /**
     * Verify 'Content-Type' header
     *
     * @throws Exception
     * 		
     */
    @Test
    public void requestContentTypeEmptyCharset() throws Exception {
        final AtomicReference<String> contentType = new AtomicReference<String>();
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                contentType.set(request.getContentType());
                response.setStatus(HttpURLConnection.HTTP_OK);
            }
        };
        Assert.assertTrue(HttpRequest.post(HttpRequestTest.url).contentType("text/html", "").ok());
        Assert.assertEquals("text/html", contentType.get());
    }

    /**
     * Verify 'Cache-Control' header
     *
     * @throws Exception
     * 		
     */
    @Test
    public void cacheControlHeader() throws Exception {
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                response.setStatus(HttpURLConnection.HTTP_OK);
                response.setHeader("Cache-Control", "no-cache");
            }
        };
        Assert.assertEquals("no-cache", HttpRequest.get(HttpRequestTest.url).cacheControl());
    }

    /**
     * Verify setting headers
     *
     * @throws Exception
     * 		
     */
    @Test
    public void headers() throws Exception {
        final AtomicReference<String> h1 = new AtomicReference<String>();
        final AtomicReference<String> h2 = new AtomicReference<String>();
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                response.setStatus(HttpURLConnection.HTTP_OK);
                h1.set(request.getHeader("h1"));
                h2.set(request.getHeader("h2"));
            }
        };
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("h1", "v1");
        headers.put("h2", "v2");
        Assert.assertTrue(HttpRequest.get(HttpRequestTest.url).headers(headers).ok());
        Assert.assertEquals("v1", h1.get());
        Assert.assertEquals("v2", h2.get());
    }

    /**
     * Verify setting headers
     *
     * @throws Exception
     * 		
     */
    @Test
    public void emptyHeaders() throws Exception {
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                response.setStatus(HttpURLConnection.HTTP_OK);
            }
        };
        Assert.assertTrue(HttpRequest.get(HttpRequestTest.url).headers(<String, String>emptyMap()).ok());
    }

    /**
     * Verify getting all headers
     *
     * @throws Exception
     * 		
     */
    @Test
    public void getAllHeaders() throws Exception {
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                response.setStatus(HttpURLConnection.HTTP_OK);
                response.setHeader("a", "a");
                response.setHeader("b", "b");
                response.addHeader("a", "another");
            }
        };
        Map<String, List<String>> headers = HttpRequest.get(HttpRequestTest.url).headers();
        Assert.assertEquals(headers.size(), 5);
        Assert.assertEquals(headers.get("a").size(), 2);
        Assert.assertTrue(headers.get("b").get(0).equals("b"));
    }

    /**
     * Verify setting number header
     *
     * @throws Exception
     * 		
     */
    @Test
    public void numberHeader() throws Exception {
        final AtomicReference<String> h1 = new AtomicReference<String>();
        final AtomicReference<String> h2 = new AtomicReference<String>();
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                response.setStatus(HttpURLConnection.HTTP_OK);
                h1.set(request.getHeader("h1"));
                h2.set(request.getHeader("h2"));
            }
        };
        Assert.assertTrue(HttpRequest.get(HttpRequestTest.url).header("h1", 5).header("h2", ((Number) (null))).ok());
        Assert.assertEquals("5", h1.get());
        Assert.assertEquals("", h2.get());
    }

    /**
     * Verify 'User-Agent' request header
     *
     * @throws Exception
     * 		
     */
    @Test
    public void userAgentHeader() throws Exception {
        final AtomicReference<String> header = new AtomicReference<String>();
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                response.setStatus(HttpURLConnection.HTTP_OK);
                header.set(request.getHeader("User-Agent"));
            }
        };
        Assert.assertTrue(HttpRequest.get(HttpRequestTest.url).userAgent("browser 1.0").ok());
        Assert.assertEquals("browser 1.0", header.get());
    }

    /**
     * Verify 'Accept' request header
     *
     * @throws Exception
     * 		
     */
    @Test
    public void acceptHeader() throws Exception {
        final AtomicReference<String> header = new AtomicReference<String>();
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                response.setStatus(HttpURLConnection.HTTP_OK);
                header.set(request.getHeader("Accept"));
            }
        };
        Assert.assertTrue(HttpRequest.get(HttpRequestTest.url).accept("application/json").ok());
        Assert.assertEquals("application/json", header.get());
    }

    /**
     * Verify 'Accept' request header when calling
     * {@link HttpRequest#acceptJson()}
     *
     * @throws Exception
     * 		
     */
    @Test
    public void acceptJson() throws Exception {
        final AtomicReference<String> header = new AtomicReference<String>();
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                response.setStatus(HttpURLConnection.HTTP_OK);
                header.set(request.getHeader("Accept"));
            }
        };
        Assert.assertTrue(HttpRequest.get(HttpRequestTest.url).acceptJson().ok());
        Assert.assertEquals("application/json", header.get());
    }

    /**
     * Verify 'If-None-Match' request header
     *
     * @throws Exception
     * 		
     */
    @Test
    public void ifNoneMatchHeader() throws Exception {
        final AtomicReference<String> header = new AtomicReference<String>();
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                response.setStatus(HttpURLConnection.HTTP_OK);
                header.set(request.getHeader("If-None-Match"));
            }
        };
        Assert.assertTrue(HttpRequest.get(HttpRequestTest.url).ifNoneMatch("eid").ok());
        Assert.assertEquals("eid", header.get());
    }

    /**
     * Verify 'Accept-Charset' request header
     *
     * @throws Exception
     * 		
     */
    @Test
    public void acceptCharsetHeader() throws Exception {
        final AtomicReference<String> header = new AtomicReference<String>();
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                response.setStatus(HttpURLConnection.HTTP_OK);
                header.set(request.getHeader("Accept-Charset"));
            }
        };
        Assert.assertTrue(HttpRequest.get(HttpRequestTest.url).acceptCharset(HttpRequest.CHARSET_UTF8).ok());
        Assert.assertEquals(HttpRequest.CHARSET_UTF8, header.get());
    }

    /**
     * Verify 'Accept-Encoding' request header
     *
     * @throws Exception
     * 		
     */
    @Test
    public void acceptEncodingHeader() throws Exception {
        final AtomicReference<String> header = new AtomicReference<String>();
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                response.setStatus(HttpURLConnection.HTTP_OK);
                header.set(request.getHeader("Accept-Encoding"));
            }
        };
        Assert.assertTrue(HttpRequest.get(HttpRequestTest.url).acceptEncoding("compress").ok());
        Assert.assertEquals("compress", header.get());
    }

    /**
     * Verify 'If-Modified-Since' request header
     *
     * @throws Exception
     * 		
     */
    @Test
    public void ifModifiedSinceHeader() throws Exception {
        final AtomicLong header = new AtomicLong();
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                response.setStatus(HttpURLConnection.HTTP_OK);
                header.set(request.getDateHeader("If-Modified-Since"));
            }
        };
        Assert.assertTrue(HttpRequest.get(HttpRequestTest.url).ifModifiedSince(5000).ok());
        Assert.assertEquals(5000, header.get());
    }

    /**
     * Verify 'Referer' header
     *
     * @throws Exception
     * 		
     */
    @Test
    public void refererHeader() throws Exception {
        final AtomicReference<String> referer = new AtomicReference<String>();
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                referer.set(request.getHeader("Referer"));
                response.setStatus(HttpURLConnection.HTTP_OK);
            }
        };
        Assert.assertTrue(HttpRequest.post(HttpRequestTest.url).referer("http://heroku.com").ok());
        Assert.assertEquals("http://heroku.com", referer.get());
    }

    /**
     * Verify multipart with file, stream, number, and string parameters
     *
     * @throws Exception
     * 		
     */
    @Test
    public void postMultipart() throws Exception {
        final StringBuilder body = new StringBuilder();
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                response.setStatus(HttpURLConnection.HTTP_OK);
                char[] buffer = new char[8192];
                int read;
                try {
                    while ((read = request.getReader().read(buffer)) != (-1))
                        body.append(buffer, 0, read);

                } catch (IOException e) {
                    Assert.fail();
                }
            }
        };
        File file = File.createTempFile("body", ".txt");
        File file2 = File.createTempFile("body", ".txt");
        new FileWriter(file).append("content1").close();
        new FileWriter(file2).append("content4").close();
        HttpRequest request = HttpRequest.post(HttpRequestTest.url);
        request.part("description", "content2");
        request.part("size", file.length());
        request.part("body", file.getName(), file);
        request.part("file", file2);
        request.part("stream", new ByteArrayInputStream("content3".getBytes()));
        Assert.assertTrue(request.ok());
        Assert.assertTrue(body.toString().contains("content1\r\n"));
        Assert.assertTrue(body.toString().contains("content2\r\n"));
        Assert.assertTrue(body.toString().contains("content3\r\n"));
        Assert.assertTrue(body.toString().contains("content4\r\n"));
        Assert.assertTrue(body.toString().contains(((Long.toString(file.length())) + "\r\n")));
    }

    /**
     * Verify multipart with content type part header
     *
     * @throws Exception
     * 		
     */
    @Test
    public void postMultipartWithContentType() throws Exception {
        final AtomicReference<String> body = new AtomicReference<String>();
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                response.setStatus(HttpURLConnection.HTTP_OK);
                body.set(new String(read()));
            }
        };
        HttpRequest request = HttpRequest.post(HttpRequestTest.url);
        request.part("body", null, "application/json", "contents");
        Assert.assertTrue(request.ok());
        Assert.assertTrue(body.toString().contains("Content-Type: application/json"));
        Assert.assertTrue(body.toString().contains("contents\r\n"));
    }

    /**
     * Verify response in {@link Appendable}
     *
     * @throws Exception
     * 		
     */
    @Test
    public void receiveAppendable() throws Exception {
        final StringBuilder body = new StringBuilder();
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                response.setStatus(HttpURLConnection.HTTP_OK);
                try {
                    response.getWriter().print("content");
                } catch (IOException e) {
                    Assert.fail();
                }
            }
        };
        Assert.assertTrue(HttpRequest.post(HttpRequestTest.url).receive(body).ok());
        Assert.assertEquals("content", body.toString());
    }

    /**
     * Verify response in {@link Writer}
     *
     * @throws Exception
     * 		
     */
    @Test
    public void receiveWriter() throws Exception {
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                response.setStatus(HttpURLConnection.HTTP_OK);
                try {
                    response.getWriter().print("content");
                } catch (IOException e) {
                    Assert.fail();
                }
            }
        };
        StringWriter writer = new StringWriter();
        Assert.assertTrue(HttpRequest.post(HttpRequestTest.url).receive(writer).ok());
        Assert.assertEquals("content", writer.toString());
    }

    /**
     * Verify response via a {@link PrintStream}
     *
     * @throws Exception
     * 		
     */
    @Test
    public void receivePrintStream() throws Exception {
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                response.setStatus(HttpURLConnection.HTTP_OK);
                try {
                    response.getWriter().print("content");
                } catch (IOException e) {
                    Assert.fail();
                }
            }
        };
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream stream = new PrintStream(output, true, HttpRequest.CHARSET_UTF8);
        Assert.assertTrue(HttpRequest.post(HttpRequestTest.url).receive(stream).ok());
        stream.close();
        Assert.assertEquals("content", output.toString(HttpRequest.CHARSET_UTF8));
    }

    /**
     * Verify response in {@link File}
     *
     * @throws Exception
     * 		
     */
    @Test
    public void receiveFile() throws Exception {
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                response.setStatus(HttpURLConnection.HTTP_OK);
                try {
                    response.getWriter().print("content");
                } catch (IOException e) {
                    Assert.fail();
                }
            }
        };
        File output = File.createTempFile("output", ".txt");
        Assert.assertTrue(HttpRequest.post(HttpRequestTest.url).receive(output).ok());
        StringBuilder buffer = new StringBuilder();
        BufferedReader reader = new BufferedReader(new FileReader(output));
        int read;
        while ((read = reader.read()) != (-1))
            buffer.append(((char) (read)));

        reader.close();
        Assert.assertEquals("content", buffer.toString());
    }

    /**
     * Verify certificate and host helpers on HTTPS connection
     *
     * @throws Exception
     * 		
     */
    @Test
    public void httpsTrust() throws Exception {
        Assert.assertNotNull(HttpRequest.get("https://localhost").trustAllCerts().trustAllHosts());
    }

    /**
     * Verify certificate and host helpers ignore non-HTTPS connection
     *
     * @throws Exception
     * 		
     */
    @Test
    public void httpTrust() throws Exception {
        Assert.assertNotNull(HttpRequest.get("http://localhost").trustAllCerts().trustAllHosts());
    }

    /**
     * Verify hostname verifier is set and accepts all
     */
    @Test
    public void verifierAccepts() {
        HttpRequest request = HttpRequest.get("https://localhost");
        HttpsURLConnection connection = ((HttpsURLConnection) (request.getConnection()));
        request.trustAllHosts();
        Assert.assertNotNull(connection.getHostnameVerifier());
        Assert.assertTrue(connection.getHostnameVerifier().verify(null, null));
    }

    /**
     * Verify single hostname verifier is created across all calls
     */
    @Test
    public void singleVerifier() {
        HttpRequest request1 = HttpRequest.get("https://localhost").trustAllHosts();
        HttpRequest request2 = HttpRequest.get("https://localhost").trustAllHosts();
        Assert.assertNotNull(((HttpsURLConnection) (request1.getConnection())).getHostnameVerifier());
        Assert.assertNotNull(((HttpsURLConnection) (request2.getConnection())).getHostnameVerifier());
        Assert.assertEquals(((HttpsURLConnection) (request1.getConnection())).getHostnameVerifier(), ((HttpsURLConnection) (request2.getConnection())).getHostnameVerifier());
    }

    /**
     * Verify single SSL socket factory is created across all calls
     */
    @Test
    public void singleSslSocketFactory() {
        HttpRequest request1 = HttpRequest.get("https://localhost").trustAllCerts();
        HttpRequest request2 = HttpRequest.get("https://localhost").trustAllCerts();
        Assert.assertNotNull(((HttpsURLConnection) (request1.getConnection())).getSSLSocketFactory());
        Assert.assertNotNull(((HttpsURLConnection) (request2.getConnection())).getSSLSocketFactory());
        Assert.assertEquals(((HttpsURLConnection) (request1.getConnection())).getSSLSocketFactory(), ((HttpsURLConnection) (request2.getConnection())).getSSLSocketFactory());
    }

    /**
     * Send a stream that throws an exception when read from
     *
     * @throws Exception
     * 		
     */
    @Test
    public void sendErrorReadStream() throws Exception {
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                response.setStatus(HttpURLConnection.HTTP_OK);
                try {
                    response.getWriter().print("content");
                } catch (IOException e) {
                    Assert.fail();
                }
            }
        };
        final IOException readCause = new IOException();
        final IOException closeCause = new IOException();
        InputStream stream = new InputStream() {
            public int read() throws IOException {
                throw readCause;
            }

            public void close() throws IOException {
                throw closeCause;
            }
        };
        try {
            HttpRequest.post(HttpRequestTest.url).send(stream);
            Assert.fail("Exception not thrown");
        } catch (HttpRequestException e) {
            Assert.assertEquals(readCause, e.getCause());
        }
    }

    /**
     * Send a stream that throws an exception when read from
     *
     * @throws Exception
     * 		
     */
    @Test
    public void sendErrorCloseStream() throws Exception {
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                response.setStatus(HttpURLConnection.HTTP_OK);
                try {
                    response.getWriter().print("content");
                } catch (IOException e) {
                    Assert.fail();
                }
            }
        };
        final IOException closeCause = new IOException();
        InputStream stream = new InputStream() {
            public int read() throws IOException {
                return -1;
            }

            public void close() throws IOException {
                throw closeCause;
            }
        };
        try {
            HttpRequest.post(HttpRequestTest.url).ignoreCloseExceptions(false).send(stream);
            Assert.fail("Exception not thrown");
        } catch (HttpRequestException e) {
            Assert.assertEquals(closeCause, e.getCause());
        }
    }

    /**
     * Make a GET request and get the code using an {@link AtomicInteger}
     *
     * @throws Exception
     * 		
     */
    @Test
    public void getToOutputCode() throws Exception {
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                response.setStatus(HttpURLConnection.HTTP_OK);
            }
        };
        AtomicInteger code = new AtomicInteger(0);
        HttpRequest.get(HttpRequestTest.url).code(code);
        Assert.assertEquals(HttpURLConnection.HTTP_OK, code.get());
    }

    /**
     * Make a GET request and get the body using an {@link AtomicReference}
     *
     * @throws Exception
     * 		
     */
    @Test
    public void getToOutputBody() throws Exception {
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                response.setStatus(HttpURLConnection.HTTP_OK);
                try {
                    response.getWriter().print("hello world");
                } catch (IOException e) {
                    Assert.fail();
                }
            }
        };
        AtomicReference<String> body = new AtomicReference<String>(null);
        HttpRequest.get(HttpRequestTest.url).body(body);
        Assert.assertEquals("hello world", body.get());
    }

    /**
     * Make a GET request and get the body using an {@link AtomicReference}
     *
     * @throws Exception
     * 		
     */
    @Test
    public void getToOutputBodyWithCharset() throws Exception {
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                response.setStatus(HttpURLConnection.HTTP_OK);
                try {
                    response.getWriter().print("hello world");
                } catch (IOException e) {
                    Assert.fail();
                }
            }
        };
        AtomicReference<String> body = new AtomicReference<String>(null);
        HttpRequest.get(HttpRequestTest.url).body(body, HttpRequest.CHARSET_UTF8);
        Assert.assertEquals("hello world", body.get());
    }

    /**
     * Make a GET request that should be compressed
     *
     * @throws Exception
     * 		
     */
    @Test
    public void getGzipped() throws Exception {
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                response.setStatus(HttpURLConnection.HTTP_OK);
                if (!("gzip".equals(request.getHeader("Accept-Encoding"))))
                    return;

                response.setHeader("Content-Encoding", "gzip");
                GZIPOutputStream output;
                try {
                    output = new GZIPOutputStream(response.getOutputStream());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                try {
                    output.write("hello compressed".getBytes(HttpRequest.CHARSET_UTF8));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } finally {
                    try {
                        output.close();
                    } catch (IOException ignored) {
                        // Ignored
                    }
                }
            }
        };
        HttpRequest request = HttpRequest.get(HttpRequestTest.url).acceptGzipEncoding().uncompress(true);
        Assert.assertTrue(request.ok());
        Assert.assertEquals("hello compressed", request.body(HttpRequest.CHARSET_UTF8));
    }

    /**
     * Make a GET request that should be compressed but isn't
     *
     * @throws Exception
     * 		
     */
    @Test
    public void getNonGzippedWithUncompressEnabled() throws Exception {
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                response.setStatus(HttpURLConnection.HTTP_OK);
                if (!("gzip".equals(request.getHeader("Accept-Encoding"))))
                    return;

                write("hello not compressed");
            }
        };
        HttpRequest request = HttpRequest.get(HttpRequestTest.url).acceptGzipEncoding().uncompress(true);
        Assert.assertTrue(request.ok());
        Assert.assertEquals("hello not compressed", request.body(HttpRequest.CHARSET_UTF8));
    }

    /**
     * Get header with multiple response values
     *
     * @throws Exception
     * 		
     */
    @Test
    public void getHeaders() throws Exception {
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                response.setStatus(HttpURLConnection.HTTP_OK);
                response.addHeader("a", "1");
                response.addHeader("a", "2");
            }
        };
        HttpRequest request = HttpRequest.get(HttpRequestTest.url);
        Assert.assertTrue(request.ok());
        String[] values = request.headers("a");
        Assert.assertNotNull(values);
        Assert.assertEquals(2, values.length);
        Assert.assertTrue(Arrays.asList(values).contains("1"));
        Assert.assertTrue(Arrays.asList(values).contains("2"));
    }

    /**
     * Get header values when not set in response
     *
     * @throws Exception
     * 		
     */
    @Test
    public void getEmptyHeaders() throws Exception {
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                response.setStatus(HttpURLConnection.HTTP_OK);
            }
        };
        HttpRequest request = HttpRequest.get(HttpRequestTest.url);
        Assert.assertTrue(request.ok());
        String[] values = request.headers("a");
        Assert.assertNotNull(values);
        Assert.assertEquals(0, values.length);
    }

    /**
     * Get header parameter value
     *
     * @throws Exception
     * 		
     */
    @Test
    public void getSingleParameter() throws Exception {
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                response.setStatus(HttpURLConnection.HTTP_OK);
                response.setHeader("a", "b;c=d");
            }
        };
        HttpRequest request = HttpRequest.get(HttpRequestTest.url);
        Assert.assertTrue(request.ok());
        Assert.assertEquals("d", request.parameter("a", "c"));
    }

    /**
     * Get header parameter value
     *
     * @throws Exception
     * 		
     */
    @Test
    public void getMultipleParameters() throws Exception {
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                response.setStatus(HttpURLConnection.HTTP_OK);
                response.setHeader("a", "b;c=d;e=f");
            }
        };
        HttpRequest request = HttpRequest.get(HttpRequestTest.url);
        Assert.assertTrue(request.ok());
        Assert.assertEquals("d", request.parameter("a", "c"));
        Assert.assertEquals("f", request.parameter("a", "e"));
    }

    /**
     * Get header parameter value
     *
     * @throws Exception
     * 		
     */
    @Test
    public void getSingleParameterQuoted() throws Exception {
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                response.setStatus(HttpURLConnection.HTTP_OK);
                response.setHeader("a", "b;c=\"d\"");
            }
        };
        HttpRequest request = HttpRequest.get(HttpRequestTest.url);
        Assert.assertTrue(request.ok());
        Assert.assertEquals("d", request.parameter("a", "c"));
    }

    /**
     * Get header parameter value
     *
     * @throws Exception
     * 		
     */
    @Test
    public void getMultipleParametersQuoted() throws Exception {
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                response.setStatus(HttpURLConnection.HTTP_OK);
                response.setHeader("a", "b;c=\"d\";e=\"f\"");
            }
        };
        HttpRequest request = HttpRequest.get(HttpRequestTest.url);
        Assert.assertTrue(request.ok());
        Assert.assertEquals("d", request.parameter("a", "c"));
        Assert.assertEquals("f", request.parameter("a", "e"));
    }

    /**
     * Get header parameter value
     *
     * @throws Exception
     * 		
     */
    @Test
    public void getMissingParameter() throws Exception {
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                response.setStatus(HttpURLConnection.HTTP_OK);
                response.setHeader("a", "b;c=d");
            }
        };
        HttpRequest request = HttpRequest.get(HttpRequestTest.url);
        Assert.assertTrue(request.ok());
        Assert.assertNull(request.parameter("a", "e"));
    }

    /**
     * Get header parameter value
     *
     * @throws Exception
     * 		
     */
    @Test
    public void getParameterFromMissingHeader() throws Exception {
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                response.setStatus(HttpURLConnection.HTTP_OK);
                response.setHeader("a", "b;c=d");
            }
        };
        HttpRequest request = HttpRequest.get(HttpRequestTest.url);
        Assert.assertTrue(request.ok());
        Assert.assertNull(request.parameter("b", "c"));
        Assert.assertTrue(request.parameters("b").isEmpty());
    }

    /**
     * Get header parameter value
     *
     * @throws Exception
     * 		
     */
    @Test
    public void getEmptyParameter() throws Exception {
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                response.setStatus(HttpURLConnection.HTTP_OK);
                response.setHeader("a", "b;c=");
            }
        };
        HttpRequest request = HttpRequest.get(HttpRequestTest.url);
        Assert.assertTrue(request.ok());
        Assert.assertNull(request.parameter("a", "c"));
        Assert.assertTrue(request.parameters("a").isEmpty());
    }

    /**
     * Get header parameter value
     *
     * @throws Exception
     * 		
     */
    @Test
    public void getEmptyParameters() throws Exception {
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                response.setStatus(HttpURLConnection.HTTP_OK);
                response.setHeader("a", "b;");
            }
        };
        HttpRequest request = HttpRequest.get(HttpRequestTest.url);
        Assert.assertTrue(request.ok());
        Assert.assertNull(request.parameter("a", "c"));
        Assert.assertTrue(request.parameters("a").isEmpty());
    }

    /**
     * Get header parameter values
     *
     * @throws Exception
     * 		
     */
    @Test
    public void getParameters() throws Exception {
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                response.setStatus(HttpURLConnection.HTTP_OK);
                response.setHeader("a", "value;b=c;d=e");
            }
        };
        HttpRequest request = HttpRequest.get(HttpRequestTest.url);
        Assert.assertTrue(request.ok());
        Map<String, String> params = request.parameters("a");
        Assert.assertNotNull(params);
        Assert.assertEquals(2, params.size());
        Assert.assertEquals("c", params.get("b"));
        Assert.assertEquals("e", params.get("d"));
    }

    /**
     * Get header parameter values
     *
     * @throws Exception
     * 		
     */
    @Test
    public void getQuotedParameters() throws Exception {
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                response.setStatus(HttpURLConnection.HTTP_OK);
                response.setHeader("a", "value;b=\"c\";d=\"e\"");
            }
        };
        HttpRequest request = HttpRequest.get(HttpRequestTest.url);
        Assert.assertTrue(request.ok());
        Map<String, String> params = request.parameters("a");
        Assert.assertNotNull(params);
        Assert.assertEquals(2, params.size());
        Assert.assertEquals("c", params.get("b"));
        Assert.assertEquals("e", params.get("d"));
    }

    /**
     * Get header parameter values
     *
     * @throws Exception
     * 		
     */
    @Test
    public void getMixQuotedParameters() throws Exception {
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                response.setStatus(HttpURLConnection.HTTP_OK);
                response.setHeader("a", "value; b=c; d=\"e\"");
            }
        };
        HttpRequest request = HttpRequest.get(HttpRequestTest.url);
        Assert.assertTrue(request.ok());
        Map<String, String> params = request.parameters("a");
        Assert.assertNotNull(params);
        Assert.assertEquals(2, params.size());
        Assert.assertEquals("c", params.get("b"));
        Assert.assertEquals("e", params.get("d"));
    }

    /**
     * Verify getting date header with default value
     *
     * @throws Exception
     * 		
     */
    @Test
    public void missingDateHeader() throws Exception {
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                response.setStatus(HttpURLConnection.HTTP_OK);
            }
        };
        Assert.assertEquals(1234L, HttpRequest.get(HttpRequestTest.url).dateHeader("missing", 1234L));
    }

    /**
     * Verify getting date header with default value
     *
     * @throws Exception
     * 		
     */
    @Test
    public void malformedDateHeader() throws Exception {
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                response.setStatus(HttpURLConnection.HTTP_OK);
                response.setHeader("malformed", "not a date");
            }
        };
        Assert.assertEquals(1234L, HttpRequest.get(HttpRequestTest.url).dateHeader("malformed", 1234L));
    }

    /**
     * Verify getting int header with default value
     *
     * @throws Exception
     * 		
     */
    @Test
    public void missingIntHeader() throws Exception {
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                response.setStatus(HttpURLConnection.HTTP_OK);
            }
        };
        Assert.assertEquals(4321, HttpRequest.get(HttpRequestTest.url).intHeader("missing", 4321));
    }

    /**
     * Verify getting int header with default value
     *
     * @throws Exception
     * 		
     */
    @Test
    public void malformedIntHeader() throws Exception {
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                response.setStatus(HttpURLConnection.HTTP_OK);
                response.setHeader("malformed", "not an integer");
            }
        };
        Assert.assertEquals(4321, HttpRequest.get(HttpRequestTest.url).intHeader("malformed", 4321));
    }

    /**
     * Verify sending form data as a sequence of {@link Entry} objects
     *
     * @throws Exception
     * 		
     */
    @Test
    public void postFormAsEntries() throws Exception {
        final AtomicReference<String> body = new AtomicReference<String>();
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                body.set(new String(read()));
                response.setStatus(HttpURLConnection.HTTP_OK);
            }
        };
        Map<String, String> data = new LinkedHashMap<String, String>();
        data.put("name", "user");
        data.put("number", "100");
        HttpRequest request = HttpRequest.post(HttpRequestTest.url);
        for (Map.Entry<String, String> entry : data.entrySet())
            request.form(entry);

        int code = request.code();
        Assert.assertEquals(HttpURLConnection.HTTP_OK, code);
        Assert.assertEquals("name=user&number=100", body.get());
    }

    /**
     * Verify sending form data where entry value is null
     *
     * @throws Exception
     * 		
     */
    @Test
    public void postFormEntryWithNullValue() throws Exception {
        final AtomicReference<String> body = new AtomicReference<String>();
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                body.set(new String(read()));
                response.setStatus(HttpURLConnection.HTTP_OK);
            }
        };
        Map<String, String> data = new LinkedHashMap<String, String>();
        data.put("name", null);
        HttpRequest request = HttpRequest.post(HttpRequestTest.url);
        for (Map.Entry<String, String> entry : data.entrySet())
            request.form(entry);

        int code = request.code();
        Assert.assertEquals(HttpURLConnection.HTTP_OK, code);
        Assert.assertEquals("name=", body.get());
    }

    /**
     * Verify POST with query parameters
     *
     * @throws Exception
     * 		
     */
    @Test
    public void postWithMappedQueryParams() throws Exception {
        Map<String, String> inputParams = new HashMap<String, String>();
        inputParams.put("name", "user");
        inputParams.put("number", "100");
        final Map<String, String> outputParams = new HashMap<String, String>();
        final AtomicReference<String> method = new AtomicReference<String>();
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                method.set(request.getMethod());
                outputParams.put("name", request.getParameter("name"));
                outputParams.put("number", request.getParameter("number"));
                response.setStatus(HttpURLConnection.HTTP_OK);
            }
        };
        HttpRequest request = HttpRequest.post(HttpRequestTest.url, inputParams, false);
        Assert.assertTrue(request.ok());
        Assert.assertEquals("POST", method.get());
        Assert.assertEquals("user", outputParams.get("name"));
        Assert.assertEquals("100", outputParams.get("number"));
    }

    /**
     * Verify POST with query parameters
     *
     * @throws Exception
     * 		
     */
    @Test
    public void postWithVaragsQueryParams() throws Exception {
        final Map<String, String> outputParams = new HashMap<String, String>();
        final AtomicReference<String> method = new AtomicReference<String>();
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                method.set(request.getMethod());
                outputParams.put("name", request.getParameter("name"));
                outputParams.put("number", request.getParameter("number"));
                response.setStatus(HttpURLConnection.HTTP_OK);
            }
        };
        HttpRequest request = HttpRequest.post(HttpRequestTest.url, false, "name", "user", "number", "100");
        Assert.assertTrue(request.ok());
        Assert.assertEquals("POST", method.get());
        Assert.assertEquals("user", outputParams.get("name"));
        Assert.assertEquals("100", outputParams.get("number"));
    }

    /**
     * Verify POST with escaped query parameters
     *
     * @throws Exception
     * 		
     */
    @Test
    public void postWithEscapedMappedQueryParams() throws Exception {
        Map<String, String> inputParams = new HashMap<String, String>();
        inputParams.put("name", "us er");
        inputParams.put("number", "100");
        final Map<String, String> outputParams = new HashMap<String, String>();
        final AtomicReference<String> method = new AtomicReference<String>();
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                method.set(request.getMethod());
                outputParams.put("name", request.getParameter("name"));
                outputParams.put("number", request.getParameter("number"));
                response.setStatus(HttpURLConnection.HTTP_OK);
            }
        };
        HttpRequest request = HttpRequest.post(HttpRequestTest.url, inputParams, true);
        Assert.assertTrue(request.ok());
        Assert.assertEquals("POST", method.get());
        Assert.assertEquals("us er", outputParams.get("name"));
        Assert.assertEquals("100", outputParams.get("number"));
    }

    /**
     * Verify POST with escaped query parameters
     *
     * @throws Exception
     * 		
     */
    @Test
    public void postWithEscapedVarargsQueryParams() throws Exception {
        final Map<String, String> outputParams = new HashMap<String, String>();
        final AtomicReference<String> method = new AtomicReference<String>();
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                method.set(request.getMethod());
                outputParams.put("name", request.getParameter("name"));
                outputParams.put("number", request.getParameter("number"));
                response.setStatus(HttpURLConnection.HTTP_OK);
            }
        };
        HttpRequest request = HttpRequest.post(HttpRequestTest.url, true, "name", "us er", "number", "100");
        Assert.assertTrue(request.ok());
        Assert.assertEquals("POST", method.get());
        Assert.assertEquals("us er", outputParams.get("name"));
        Assert.assertEquals("100", outputParams.get("number"));
    }

    /**
     * Verify POST with numeric query parameters
     *
     * @throws Exception
     * 		
     */
    @Test
    public void postWithNumericQueryParams() throws Exception {
        Map<Object, Object> inputParams = new HashMap<Object, Object>();
        inputParams.put(1, 2);
        inputParams.put(3, 4);
        final Map<String, String> outputParams = new HashMap<String, String>();
        final AtomicReference<String> method = new AtomicReference<String>();
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                method.set(request.getMethod());
                outputParams.put("1", request.getParameter("1"));
                outputParams.put("3", request.getParameter("3"));
                response.setStatus(HttpURLConnection.HTTP_OK);
            }
        };
        HttpRequest request = HttpRequest.post(HttpRequestTest.url, inputParams, false);
        Assert.assertTrue(request.ok());
        Assert.assertEquals("POST", method.get());
        Assert.assertEquals("2", outputParams.get("1"));
        Assert.assertEquals("4", outputParams.get("3"));
    }

    /**
     * Verify GET with query parameters
     *
     * @throws Exception
     * 		
     */
    @Test
    public void getWithMappedQueryParams() throws Exception {
        Map<String, String> inputParams = new HashMap<String, String>();
        inputParams.put("name", "user");
        inputParams.put("number", "100");
        final Map<String, String> outputParams = new HashMap<String, String>();
        final AtomicReference<String> method = new AtomicReference<String>();
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                method.set(request.getMethod());
                outputParams.put("name", request.getParameter("name"));
                outputParams.put("number", request.getParameter("number"));
                response.setStatus(HttpURLConnection.HTTP_OK);
            }
        };
        HttpRequest request = HttpRequest.get(HttpRequestTest.url, inputParams, false);
        Assert.assertTrue(request.ok());
        Assert.assertEquals("GET", method.get());
        Assert.assertEquals("user", outputParams.get("name"));
        Assert.assertEquals("100", outputParams.get("number"));
    }

    /**
     * Verify GET with query parameters
     *
     * @throws Exception
     * 		
     */
    @Test
    public void getWithVarargsQueryParams() throws Exception {
        final Map<String, String> outputParams = new HashMap<String, String>();
        final AtomicReference<String> method = new AtomicReference<String>();
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                method.set(request.getMethod());
                outputParams.put("name", request.getParameter("name"));
                outputParams.put("number", request.getParameter("number"));
                response.setStatus(HttpURLConnection.HTTP_OK);
            }
        };
        HttpRequest request = HttpRequest.get(HttpRequestTest.url, false, "name", "user", "number", "100");
        Assert.assertTrue(request.ok());
        Assert.assertEquals("GET", method.get());
        Assert.assertEquals("user", outputParams.get("name"));
        Assert.assertEquals("100", outputParams.get("number"));
    }

    /**
     * Verify GET with escaped query parameters
     *
     * @throws Exception
     * 		
     */
    @Test
    public void getWithEscapedMappedQueryParams() throws Exception {
        Map<String, String> inputParams = new HashMap<String, String>();
        inputParams.put("name", "us er");
        inputParams.put("number", "100");
        final Map<String, String> outputParams = new HashMap<String, String>();
        final AtomicReference<String> method = new AtomicReference<String>();
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                method.set(request.getMethod());
                outputParams.put("name", request.getParameter("name"));
                outputParams.put("number", request.getParameter("number"));
                response.setStatus(HttpURLConnection.HTTP_OK);
            }
        };
        HttpRequest request = HttpRequest.get(HttpRequestTest.url, inputParams, true);
        Assert.assertTrue(request.ok());
        Assert.assertEquals("GET", method.get());
        Assert.assertEquals("us er", outputParams.get("name"));
        Assert.assertEquals("100", outputParams.get("number"));
    }

    /**
     * Verify GET with escaped query parameters
     *
     * @throws Exception
     * 		
     */
    @Test
    public void getWithEscapedVarargsQueryParams() throws Exception {
        final Map<String, String> outputParams = new HashMap<String, String>();
        final AtomicReference<String> method = new AtomicReference<String>();
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                method.set(request.getMethod());
                outputParams.put("name", request.getParameter("name"));
                outputParams.put("number", request.getParameter("number"));
                response.setStatus(HttpURLConnection.HTTP_OK);
            }
        };
        HttpRequest request = HttpRequest.get(HttpRequestTest.url, true, "name", "us er", "number", "100");
        Assert.assertTrue(request.ok());
        Assert.assertEquals("GET", method.get());
        Assert.assertEquals("us er", outputParams.get("name"));
        Assert.assertEquals("100", outputParams.get("number"));
    }

    /**
     * Verify DELETE with query parameters
     *
     * @throws Exception
     * 		
     */
    @Test
    public void deleteWithMappedQueryParams() throws Exception {
        Map<String, String> inputParams = new HashMap<String, String>();
        inputParams.put("name", "user");
        inputParams.put("number", "100");
        final Map<String, String> outputParams = new HashMap<String, String>();
        final AtomicReference<String> method = new AtomicReference<String>();
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                method.set(request.getMethod());
                outputParams.put("name", request.getParameter("name"));
                outputParams.put("number", request.getParameter("number"));
                response.setStatus(HttpURLConnection.HTTP_OK);
            }
        };
        HttpRequest request = HttpRequest.delete(HttpRequestTest.url, inputParams, false);
        Assert.assertTrue(request.ok());
        Assert.assertEquals("DELETE", method.get());
        Assert.assertEquals("user", outputParams.get("name"));
        Assert.assertEquals("100", outputParams.get("number"));
    }

    /**
     * Verify DELETE with query parameters
     *
     * @throws Exception
     * 		
     */
    @Test
    public void deleteWithVarargsQueryParams() throws Exception {
        final Map<String, String> outputParams = new HashMap<String, String>();
        final AtomicReference<String> method = new AtomicReference<String>();
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                method.set(request.getMethod());
                outputParams.put("name", request.getParameter("name"));
                outputParams.put("number", request.getParameter("number"));
                response.setStatus(HttpURLConnection.HTTP_OK);
            }
        };
        HttpRequest request = HttpRequest.delete(HttpRequestTest.url, false, "name", "user", "number", "100");
        Assert.assertTrue(request.ok());
        Assert.assertEquals("DELETE", method.get());
        Assert.assertEquals("user", outputParams.get("name"));
        Assert.assertEquals("100", outputParams.get("number"));
    }

    /**
     * Verify DELETE with escaped query parameters
     *
     * @throws Exception
     * 		
     */
    @Test
    public void deleteWithEscapedMappedQueryParams() throws Exception {
        Map<String, String> inputParams = new HashMap<String, String>();
        inputParams.put("name", "us er");
        inputParams.put("number", "100");
        final Map<String, String> outputParams = new HashMap<String, String>();
        final AtomicReference<String> method = new AtomicReference<String>();
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                method.set(request.getMethod());
                outputParams.put("name", request.getParameter("name"));
                outputParams.put("number", request.getParameter("number"));
                response.setStatus(HttpURLConnection.HTTP_OK);
            }
        };
        HttpRequest request = HttpRequest.delete(HttpRequestTest.url, inputParams, true);
        Assert.assertTrue(request.ok());
        Assert.assertEquals("DELETE", method.get());
        Assert.assertEquals("us er", outputParams.get("name"));
        Assert.assertEquals("100", outputParams.get("number"));
    }

    /**
     * Verify DELETE with escaped query parameters
     *
     * @throws Exception
     * 		
     */
    @Test
    public void deleteWithEscapedVarargsQueryParams() throws Exception {
        final Map<String, String> outputParams = new HashMap<String, String>();
        final AtomicReference<String> method = new AtomicReference<String>();
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                method.set(request.getMethod());
                outputParams.put("name", request.getParameter("name"));
                outputParams.put("number", request.getParameter("number"));
                response.setStatus(HttpURLConnection.HTTP_OK);
            }
        };
        HttpRequest request = HttpRequest.delete(HttpRequestTest.url, true, "name", "us er", "number", "100");
        Assert.assertTrue(request.ok());
        Assert.assertEquals("DELETE", method.get());
        Assert.assertEquals("us er", outputParams.get("name"));
        Assert.assertEquals("100", outputParams.get("number"));
    }

    /**
     * Verify PUT with query parameters
     *
     * @throws Exception
     * 		
     */
    @Test
    public void putWithMappedQueryParams() throws Exception {
        Map<String, String> inputParams = new HashMap<String, String>();
        inputParams.put("name", "user");
        inputParams.put("number", "100");
        final Map<String, String> outputParams = new HashMap<String, String>();
        final AtomicReference<String> method = new AtomicReference<String>();
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                method.set(request.getMethod());
                outputParams.put("name", request.getParameter("name"));
                outputParams.put("number", request.getParameter("number"));
                response.setStatus(HttpURLConnection.HTTP_OK);
            }
        };
        HttpRequest request = HttpRequest.put(HttpRequestTest.url, inputParams, false);
        Assert.assertTrue(request.ok());
        Assert.assertEquals("PUT", method.get());
        Assert.assertEquals("user", outputParams.get("name"));
        Assert.assertEquals("100", outputParams.get("number"));
    }

    /**
     * Verify PUT with query parameters
     *
     * @throws Exception
     * 		
     */
    @Test
    public void putWithVarargsQueryParams() throws Exception {
        final Map<String, String> outputParams = new HashMap<String, String>();
        final AtomicReference<String> method = new AtomicReference<String>();
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                method.set(request.getMethod());
                outputParams.put("name", request.getParameter("name"));
                outputParams.put("number", request.getParameter("number"));
                response.setStatus(HttpURLConnection.HTTP_OK);
            }
        };
        HttpRequest request = HttpRequest.put(HttpRequestTest.url, false, "name", "user", "number", "100");
        Assert.assertTrue(request.ok());
        Assert.assertEquals("PUT", method.get());
        Assert.assertEquals("user", outputParams.get("name"));
        Assert.assertEquals("100", outputParams.get("number"));
    }

    /**
     * Verify PUT with escaped query parameters
     *
     * @throws Exception
     * 		
     */
    @Test
    public void putWithEscapedMappedQueryParams() throws Exception {
        Map<String, String> inputParams = new HashMap<String, String>();
        inputParams.put("name", "us er");
        inputParams.put("number", "100");
        final Map<String, String> outputParams = new HashMap<String, String>();
        final AtomicReference<String> method = new AtomicReference<String>();
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                method.set(request.getMethod());
                outputParams.put("name", request.getParameter("name"));
                outputParams.put("number", request.getParameter("number"));
                response.setStatus(HttpURLConnection.HTTP_OK);
            }
        };
        HttpRequest request = HttpRequest.put(HttpRequestTest.url, inputParams, true);
        Assert.assertTrue(request.ok());
        Assert.assertEquals("PUT", method.get());
        Assert.assertEquals("us er", outputParams.get("name"));
        Assert.assertEquals("100", outputParams.get("number"));
    }

    /**
     * Verify PUT with escaped query parameters
     *
     * @throws Exception
     * 		
     */
    @Test
    public void putWithEscapedVarargsQueryParams() throws Exception {
        final Map<String, String> outputParams = new HashMap<String, String>();
        final AtomicReference<String> method = new AtomicReference<String>();
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                method.set(request.getMethod());
                outputParams.put("name", request.getParameter("name"));
                outputParams.put("number", request.getParameter("number"));
                response.setStatus(HttpURLConnection.HTTP_OK);
            }
        };
        HttpRequest request = HttpRequest.put(HttpRequestTest.url, true, "name", "us er", "number", "100");
        Assert.assertTrue(request.ok());
        Assert.assertEquals("PUT", method.get());
        Assert.assertEquals("us er", outputParams.get("name"));
        Assert.assertEquals("100", outputParams.get("number"));
    }

    /**
     * Verify HEAD with query parameters
     *
     * @throws Exception
     * 		
     */
    @Test
    public void headWithMappedQueryParams() throws Exception {
        Map<String, String> inputParams = new HashMap<String, String>();
        inputParams.put("name", "user");
        inputParams.put("number", "100");
        final Map<String, String> outputParams = new HashMap<String, String>();
        final AtomicReference<String> method = new AtomicReference<String>();
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                method.set(request.getMethod());
                outputParams.put("name", request.getParameter("name"));
                outputParams.put("number", request.getParameter("number"));
                response.setStatus(HttpURLConnection.HTTP_OK);
            }
        };
        HttpRequest request = HttpRequest.head(HttpRequestTest.url, inputParams, false);
        Assert.assertTrue(request.ok());
        Assert.assertEquals("HEAD", method.get());
        Assert.assertEquals("user", outputParams.get("name"));
        Assert.assertEquals("100", outputParams.get("number"));
    }

    /**
     * Verify HEAD with query parameters
     *
     * @throws Exception
     * 		
     */
    @Test
    public void headWithVaragsQueryParams() throws Exception {
        final Map<String, String> outputParams = new HashMap<String, String>();
        final AtomicReference<String> method = new AtomicReference<String>();
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                method.set(request.getMethod());
                outputParams.put("name", request.getParameter("name"));
                outputParams.put("number", request.getParameter("number"));
                response.setStatus(HttpURLConnection.HTTP_OK);
            }
        };
        HttpRequest request = HttpRequest.head(HttpRequestTest.url, false, "name", "user", "number", "100");
        Assert.assertTrue(request.ok());
        Assert.assertEquals("HEAD", method.get());
        Assert.assertEquals("user", outputParams.get("name"));
        Assert.assertEquals("100", outputParams.get("number"));
    }

    /**
     * Verify HEAD with escaped query parameters
     *
     * @throws Exception
     * 		
     */
    @Test
    public void headWithEscapedMappedQueryParams() throws Exception {
        Map<String, String> inputParams = new HashMap<String, String>();
        inputParams.put("name", "us er");
        inputParams.put("number", "100");
        final Map<String, String> outputParams = new HashMap<String, String>();
        final AtomicReference<String> method = new AtomicReference<String>();
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                method.set(request.getMethod());
                outputParams.put("name", request.getParameter("name"));
                outputParams.put("number", request.getParameter("number"));
                response.setStatus(HttpURLConnection.HTTP_OK);
            }
        };
        HttpRequest request = HttpRequest.head(HttpRequestTest.url, inputParams, true);
        Assert.assertTrue(request.ok());
        Assert.assertEquals("HEAD", method.get());
        Assert.assertEquals("us er", outputParams.get("name"));
        Assert.assertEquals("100", outputParams.get("number"));
    }

    /**
     * Verify HEAD with escaped query parameters
     *
     * @throws Exception
     * 		
     */
    @Test
    public void headWithEscapedVarargsQueryParams() throws Exception {
        final Map<String, String> outputParams = new HashMap<String, String>();
        final AtomicReference<String> method = new AtomicReference<String>();
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                method.set(request.getMethod());
                outputParams.put("name", request.getParameter("name"));
                outputParams.put("number", request.getParameter("number"));
                response.setStatus(HttpURLConnection.HTTP_OK);
            }
        };
        HttpRequest request = HttpRequest.head(HttpRequestTest.url, true, "name", "us er", "number", "100");
        Assert.assertTrue(request.ok());
        Assert.assertEquals("HEAD", method.get());
        Assert.assertEquals("us er", outputParams.get("name"));
        Assert.assertEquals("100", outputParams.get("number"));
    }

    /**
     * Append with base URL with no path
     *
     * @throws Exception
     * 		
     */
    @Test
    public void appendMappedQueryParamsWithNoPath() throws Exception {
        Assert.assertEquals("http://test.com/?a=b", HttpRequest.append("http://test.com", Collections.singletonMap("a", "b")));
    }

    /**
     * Append with base URL with no path
     *
     * @throws Exception
     * 		
     */
    @Test
    public void appendVarargsQueryParmasWithNoPath() throws Exception {
        Assert.assertEquals("http://test.com/?a=b", HttpRequest.append("http://test.com", "a", "b"));
    }

    /**
     * Append with base URL with path
     *
     * @throws Exception
     * 		
     */
    @Test
    public void appendMappedQueryParamsWithPath() throws Exception {
        Assert.assertEquals("http://test.com/segment1?a=b", HttpRequest.append("http://test.com/segment1", Collections.singletonMap("a", "b")));
        Assert.assertEquals("http://test.com/?a=b", HttpRequest.append("http://test.com/", Collections.singletonMap("a", "b")));
    }

    /**
     * Append with base URL with path
     *
     * @throws Exception
     * 		
     */
    @Test
    public void appendVarargsQueryParamsWithPath() throws Exception {
        Assert.assertEquals("http://test.com/segment1?a=b", HttpRequest.append("http://test.com/segment1", "a", "b"));
        Assert.assertEquals("http://test.com/?a=b", HttpRequest.append("http://test.com/", "a", "b"));
    }

    /**
     * Append multiple params
     *
     * @throws Exception
     * 		
     */
    @Test
    public void appendMultipleMappedQueryParams() throws Exception {
        Map<String, Object> params = new LinkedHashMap<String, Object>();
        params.put("a", "b");
        params.put("c", "d");
        Assert.assertEquals("http://test.com/1?a=b&c=d", HttpRequest.append("http://test.com/1", params));
    }

    /**
     * Append multiple params
     *
     * @throws Exception
     * 		
     */
    @Test
    public void appendMultipleVarargsQueryParams() throws Exception {
        Assert.assertEquals("http://test.com/1?a=b&c=d", HttpRequest.append("http://test.com/1", "a", "b", "c", "d"));
    }

    /**
     * Append null params
     *
     * @throws Exception
     * 		
     */
    @Test
    public void appendNullMappedQueryParams() throws Exception {
        Assert.assertEquals("http://test.com/1", HttpRequest.append("http://test.com/1", ((Map<?, ?>) (null))));
    }

    /**
     * Append null params
     *
     * @throws Exception
     * 		
     */
    @Test
    public void appendNullVaragsQueryParams() throws Exception {
        Assert.assertEquals("http://test.com/1", HttpRequest.append("http://test.com/1", ((Object[]) (null))));
    }

    /**
     * Append empty params
     *
     * @throws Exception
     * 		
     */
    @Test
    public void appendEmptyMappedQueryParams() throws Exception {
        Assert.assertEquals("http://test.com/1", HttpRequest.append("http://test.com/1", <String, String>emptyMap()));
    }

    /**
     * Append empty params
     *
     * @throws Exception
     * 		
     */
    @Test
    public void appendEmptyVarargsQueryParams() throws Exception {
        Assert.assertEquals("http://test.com/1", HttpRequest.append("http://test.com/1", new Object[0]));
    }

    /**
     * Append params with null values
     *
     * @throws Exception
     * 		
     */
    @Test
    public void appendWithNullMappedQueryParamValues() throws Exception {
        Map<String, Object> params = new LinkedHashMap<String, Object>();
        params.put("a", null);
        params.put("b", null);
        Assert.assertEquals("http://test.com/1?a=&b=", HttpRequest.append("http://test.com/1", params));
    }

    /**
     * Append params with null values
     *
     * @throws Exception
     * 		
     */
    @Test
    public void appendWithNullVaragsQueryParamValues() throws Exception {
        Assert.assertEquals("http://test.com/1?a=&b=", HttpRequest.append("http://test.com/1", "a", null, "b", null));
    }

    /**
     * Try to append with wrong number of arguments
     */
    @Test(expected = IllegalArgumentException.class)
    public void appendOddNumberOfParams() {
        HttpRequest.append("http://test.com", "1");
    }

    /**
     * Append with base URL already containing a '?'
     */
    @Test
    public void appendMappedQueryParamsWithExistingQueryStart() {
        Assert.assertEquals("http://test.com/1?a=b", HttpRequest.append("http://test.com/1?", Collections.singletonMap("a", "b")));
    }

    /**
     * Append with base URL already containing a '?'
     */
    @Test
    public void appendVarargsQueryParamsWithExistingQueryStart() {
        Assert.assertEquals("http://test.com/1?a=b", HttpRequest.append("http://test.com/1?", "a", "b"));
    }

    /**
     * Append with base URL already containing a '?'
     */
    @Test
    public void appendMappedQueryParamsWithExistingParams() {
        Assert.assertEquals("http://test.com/1?a=b&c=d", HttpRequest.append("http://test.com/1?a=b", Collections.singletonMap("c", "d")));
        Assert.assertEquals("http://test.com/1?a=b&c=d", HttpRequest.append("http://test.com/1?a=b&", Collections.singletonMap("c", "d")));
    }

    /**
     * Append with base URL already containing a '?'
     */
    @Test
    public void appendWithVarargsQueryParamsWithExistingParams() {
        Assert.assertEquals("http://test.com/1?a=b&c=d", HttpRequest.append("http://test.com/1?a=b", "c", "d"));
        Assert.assertEquals("http://test.com/1?a=b&c=d", HttpRequest.append("http://test.com/1?a=b&", "c", "d"));
    }

    /**
     * Append array parameter
     *
     * @throws Exception
     * 		
     */
    @Test
    public void appendArrayQueryParams() throws Exception {
        Assert.assertEquals("http://test.com/?foo[]=bar&foo[]=baz", HttpRequest.append("http://test.com", Collections.singletonMap("foo", new String[]{ "bar", "baz" })));
        Assert.assertEquals("http://test.com/?a[]=1&a[]=2", HttpRequest.append("http://test.com", Collections.singletonMap("a", new int[]{ 1, 2 })));
        Assert.assertEquals("http://test.com/?a[]=1", HttpRequest.append("http://test.com", Collections.singletonMap("a", new int[]{ 1 })));
        Assert.assertEquals("http://test.com/?", HttpRequest.append("http://test.com", Collections.singletonMap("a", new int[]{  })));
        Assert.assertEquals("http://test.com/?foo[]=bar&foo[]=baz&a[]=1&a[]=2", HttpRequest.append("http://test.com", "foo", new String[]{ "bar", "baz" }, "a", new int[]{ 1, 2 }));
    }

    /**
     * Append list parameter
     *
     * @throws Exception
     * 		
     */
    @Test
    public void appendListQueryParams() throws Exception {
        Assert.assertEquals("http://test.com/?foo[]=bar&foo[]=baz", HttpRequest.append("http://test.com", Collections.singletonMap("foo", Arrays.asList(new String[]{ "bar", "baz" }))));
        Assert.assertEquals("http://test.com/?a[]=1&a[]=2", HttpRequest.append("http://test.com", Collections.singletonMap("a", Arrays.asList(new Integer[]{ 1, 2 }))));
        Assert.assertEquals("http://test.com/?a[]=1", HttpRequest.append("http://test.com", Collections.singletonMap("a", Arrays.asList(new Integer[]{ 1 }))));
        Assert.assertEquals("http://test.com/?", HttpRequest.append("http://test.com", Collections.singletonMap("a", Arrays.asList(new Integer[]{  }))));
        Assert.assertEquals("http://test.com/?foo[]=bar&foo[]=baz&a[]=1&a[]=2", HttpRequest.append("http://test.com", "foo", Arrays.asList(new String[]{ "bar", "baz" }), "a", Arrays.asList(new Integer[]{ 1, 2 })));
    }

    /**
     * Get a 500
     *
     * @throws Exception
     * 		
     */
    @Test
    public void serverErrorCode() throws Exception {
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                response.setStatus(HttpURLConnection.HTTP_INTERNAL_ERROR);
            }
        };
        HttpRequest request = HttpRequest.get(HttpRequestTest.url);
        Assert.assertNotNull(request);
        Assert.assertTrue(request.serverError());
    }

    /**
     * Get a 400
     *
     * @throws Exception
     * 		
     */
    @Test
    public void badRequestCode() throws Exception {
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                response.setStatus(HttpURLConnection.HTTP_BAD_REQUEST);
            }
        };
        HttpRequest request = HttpRequest.get(HttpRequestTest.url);
        Assert.assertNotNull(request);
        Assert.assertTrue(request.badRequest());
    }

    /**
     * Get a 304
     *
     * @throws Exception
     * 		
     */
    @Test
    public void notModifiedCode() throws Exception {
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                response.setStatus(HttpURLConnection.HTTP_NOT_MODIFIED);
            }
        };
        HttpRequest request = HttpRequest.get(HttpRequestTest.url);
        Assert.assertNotNull(request);
        Assert.assertTrue(request.notModified());
    }

    /**
     * Verify data is sent when receiving response without first calling
     * {@link HttpRequest#code()}
     *
     * @throws Exception
     * 		
     */
    @Test
    public void sendReceiveWithoutCode() throws Exception {
        final AtomicReference<String> body = new AtomicReference<String>();
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                body.set(new String(read()));
                try {
                    response.getWriter().write("world");
                } catch (IOException ignored) {
                    // Ignored
                }
                response.setStatus(HttpURLConnection.HTTP_OK);
            }
        };
        HttpRequest request = HttpRequest.post(HttpRequestTest.url).ignoreCloseExceptions(false);
        Assert.assertEquals("world", request.send("hello").body());
        Assert.assertEquals("hello", body.get());
    }

    /**
     * Verify data is send when receiving response headers without first calling
     * {@link HttpRequest#code()}
     *
     * @throws Exception
     * 		
     */
    @Test
    public void sendHeadersWithoutCode() throws Exception {
        final AtomicReference<String> body = new AtomicReference<String>();
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                body.set(new String(read()));
                response.setHeader("h1", "v1");
                response.setHeader("h2", "v2");
                response.setStatus(HttpURLConnection.HTTP_OK);
            }
        };
        HttpRequest request = HttpRequest.post(HttpRequestTest.url).ignoreCloseExceptions(false);
        Map<String, List<String>> headers = request.send("hello").headers();
        Assert.assertEquals("v1", headers.get("h1").get(0));
        Assert.assertEquals("v2", headers.get("h2").get(0));
        Assert.assertEquals("hello", body.get());
    }

    /**
     * Verify data is send when receiving response date header without first
     * calling {@link HttpRequest#code()}
     *
     * @throws Exception
     * 		
     */
    @Test
    public void sendDateHeaderWithoutCode() throws Exception {
        final AtomicReference<String> body = new AtomicReference<String>();
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                body.set(new String(read()));
                response.setDateHeader("Date", 1000);
                response.setStatus(HttpURLConnection.HTTP_OK);
            }
        };
        HttpRequest request = HttpRequest.post(HttpRequestTest.url).ignoreCloseExceptions(false);
        Assert.assertEquals(1000, request.send("hello").date());
        Assert.assertEquals("hello", body.get());
    }

    /**
     * Verify data is send when receiving response integer header without first
     * calling {@link HttpRequest#code()}
     *
     * @throws Exception
     * 		
     */
    @Test
    public void sendIntHeaderWithoutCode() throws Exception {
        final AtomicReference<String> body = new AtomicReference<String>();
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                body.set(new String(read()));
                response.setIntHeader("Width", 9876);
                response.setStatus(HttpURLConnection.HTTP_OK);
            }
        };
        HttpRequest request = HttpRequest.post(HttpRequestTest.url).ignoreCloseExceptions(false);
        Assert.assertEquals(9876, request.send("hello").intHeader("Width"));
        Assert.assertEquals("hello", body.get());
    }

    /**
     * Verify custom connection factory
     */
    @Test
    public void customConnectionFactory() throws Exception {
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                response.setStatus(HttpURLConnection.HTTP_OK);
            }
        };
        ConnectionFactory factory = new ConnectionFactory() {
            public HttpURLConnection create(URL otherUrl) throws IOException {
                return ((HttpURLConnection) (new URL(HttpRequestTest.url).openConnection()));
            }

            public HttpURLConnection create(URL url, Proxy proxy) throws IOException {
                throw new IOException();
            }
        };
        HttpRequest.setConnectionFactory(factory);
        int code = HttpRequest.get("http://not/a/real/url").code();
        Assert.assertEquals(200, code);
    }

    /**
     * Verify setting a null connection factory restores to the default one
     */
    @Test
    public void nullConnectionFactory() throws Exception {
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                response.setStatus(HttpURLConnection.HTTP_OK);
            }
        };
        HttpRequest.setConnectionFactory(null);
        int code = HttpRequest.get(HttpRequestTest.url).code();
        Assert.assertEquals(200, code);
    }

    /**
     * Verify reading response body for empty 200
     *
     * @throws Exception
     * 		
     */
    @Test
    public void streamOfEmptyOkResponse() throws Exception {
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                response.setStatus(200);
            }
        };
        Assert.assertEquals("", HttpRequest.get(HttpRequestTest.url).body());
    }

    /**
     * Verify reading response body for empty 400
     *
     * @throws Exception
     * 		
     */
    @Test
    public void bodyOfEmptyErrorResponse() throws Exception {
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                response.setStatus(HttpURLConnection.HTTP_BAD_REQUEST);
            }
        };
        Assert.assertEquals("", HttpRequest.get(HttpRequestTest.url).body());
    }

    /**
     * Verify reading response body for non-empty 400
     *
     * @throws Exception
     * 		
     */
    @Test
    public void bodyOfNonEmptyErrorResponse() throws Exception {
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                response.setStatus(HttpURLConnection.HTTP_BAD_REQUEST);
                try {
                    response.getWriter().write("error");
                } catch (IOException ignored) {
                    // Ignored
                }
            }
        };
        Assert.assertEquals("error", HttpRequest.get(HttpRequestTest.url).body());
    }

    /**
     * Verify progress callback when sending a file
     *
     * @throws Exception
     * 		
     */
    @Test
    public void uploadProgressSend() throws Exception {
        final AtomicReference<String> body = new AtomicReference<String>();
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                body.set(new String(read()));
                response.setStatus(HttpURLConnection.HTTP_OK);
            }
        };
        final File file = File.createTempFile("post", ".txt");
        new FileWriter(file).append("hello").close();
        final AtomicLong tx = new AtomicLong(0);
        UploadProgress progress = new UploadProgress() {
            public void onUpload(long transferred, long total) {
                Assert.assertEquals(file.length(), total);
                Assert.assertEquals(tx.incrementAndGet(), transferred);
            }
        };
        HttpRequest.post(HttpRequestTest.url).bufferSize(1).progress(progress).send(file).code();
        Assert.assertEquals(file.length(), tx.get());
    }

    /**
     * Verify progress callback when sending from an InputStream
     *
     * @throws Exception
     * 		
     */
    @Test
    public void uploadProgressSendInputStream() throws Exception {
        final AtomicReference<String> body = new AtomicReference<String>();
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                body.set(new String(read()));
                response.setStatus(HttpURLConnection.HTTP_OK);
            }
        };
        File file = File.createTempFile("post", ".txt");
        new FileWriter(file).append("hello").close();
        InputStream input = new FileInputStream(file);
        final AtomicLong tx = new AtomicLong(0);
        UploadProgress progress = new UploadProgress() {
            public void onUpload(long transferred, long total) {
                Assert.assertEquals((-1), total);
                Assert.assertEquals(tx.incrementAndGet(), transferred);
            }
        };
        HttpRequest.post(HttpRequestTest.url).bufferSize(1).progress(progress).send(input).code();
        Assert.assertEquals(file.length(), tx.get());
    }

    /**
     * Verify progress callback when sending from a byte array
     *
     * @throws Exception
     * 		
     */
    @Test
    public void uploadProgressSendByteArray() throws Exception {
        final AtomicReference<String> body = new AtomicReference<String>();
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                body.set(new String(read()));
                response.setStatus(HttpURLConnection.HTTP_OK);
            }
        };
        final byte[] bytes = "hello".getBytes(HttpRequest.CHARSET_UTF8);
        final AtomicLong tx = new AtomicLong(0);
        UploadProgress progress = new UploadProgress() {
            public void onUpload(long transferred, long total) {
                Assert.assertEquals(bytes.length, total);
                Assert.assertEquals(tx.incrementAndGet(), transferred);
            }
        };
        HttpRequest.post(HttpRequestTest.url).bufferSize(1).progress(progress).send(bytes).code();
        Assert.assertEquals(bytes.length, tx.get());
    }

    /**
     * Verify progress callback when sending from a Reader
     *
     * @throws Exception
     * 		
     */
    @Test
    public void uploadProgressSendReader() throws Exception {
        final AtomicReference<String> body = new AtomicReference<String>();
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                body.set(new String(read()));
                response.setStatus(HttpURLConnection.HTTP_OK);
            }
        };
        final AtomicLong tx = new AtomicLong(0);
        UploadProgress progress = new UploadProgress() {
            public void onUpload(long transferred, long total) {
                Assert.assertEquals((-1), total);
                Assert.assertEquals(tx.incrementAndGet(), transferred);
            }
        };
        File file = File.createTempFile("post", ".txt");
        new FileWriter(file).append("hello").close();
        HttpRequest.post(HttpRequestTest.url).progress(progress).bufferSize(1).send(new FileReader(file)).code();
        Assert.assertEquals(file.length(), tx.get());
    }

    /**
     * Verify progress callback doesn't cause an exception when it's null
     *
     * @throws Exception
     * 		
     */
    @Test
    public void nullUploadProgress() throws Exception {
        final AtomicReference<String> body = new AtomicReference<String>();
        HttpRequestTest.handler = new ServerTestCase.RequestHandler() {
            @Override
            public void handle(Request request, HttpServletResponse response) {
                body.set(new String(read()));
                response.setStatus(HttpURLConnection.HTTP_OK);
            }
        };
        File file = File.createTempFile("post", ".txt");
        new FileWriter(file).append("hello").close();
        int code = HttpRequest.post(HttpRequestTest.url).progress(null).send(file).code();
        Assert.assertEquals(HttpURLConnection.HTTP_OK, code);
        Assert.assertEquals("hello", body.get());
    }
}

