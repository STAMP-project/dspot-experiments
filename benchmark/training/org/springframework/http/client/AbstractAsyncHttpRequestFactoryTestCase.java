/**
 * Copyright 2002-2016 the original author or authors.
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
package org.springframework.http.client;


import HttpMethod.DELETE;
import HttpMethod.GET;
import HttpMethod.HEAD;
import HttpMethod.OPTIONS;
import HttpMethod.POST;
import HttpMethod.PUT;
import HttpStatus.NOT_FOUND;
import HttpStatus.OK;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.concurrent.Future;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.StreamingHttpOutputMessage;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StreamUtils;
import org.springframework.util.concurrent.ListenableFuture;


@SuppressWarnings("deprecation")
public abstract class AbstractAsyncHttpRequestFactoryTestCase extends AbstractMockWebServerTestCase {
    protected AsyncClientHttpRequestFactory factory;

    @Test
    public void status() throws Exception {
        URI uri = new URI(((baseUrl) + "/status/notfound"));
        AsyncClientHttpRequest request = this.factory.createAsyncRequest(uri, GET);
        Assert.assertEquals("Invalid HTTP method", GET, request.getMethod());
        Assert.assertEquals("Invalid HTTP URI", uri, request.getURI());
        Future<ClientHttpResponse> futureResponse = request.executeAsync();
        ClientHttpResponse response = futureResponse.get();
        try {
            Assert.assertEquals("Invalid status code", NOT_FOUND, response.getStatusCode());
        } finally {
            response.close();
        }
    }

    @Test
    public void statusCallback() throws Exception {
        URI uri = new URI(((baseUrl) + "/status/notfound"));
        AsyncClientHttpRequest request = this.factory.createAsyncRequest(uri, GET);
        Assert.assertEquals("Invalid HTTP method", GET, request.getMethod());
        Assert.assertEquals("Invalid HTTP URI", uri, request.getURI());
        ListenableFuture<ClientHttpResponse> listenableFuture = request.executeAsync();
        listenableFuture.addCallback(new org.springframework.util.concurrent.ListenableFutureCallback<ClientHttpResponse>() {
            @Override
            public void onSuccess(ClientHttpResponse result) {
                try {
                    Assert.assertEquals("Invalid status code", NOT_FOUND, result.getStatusCode());
                } catch (IOException ex) {
                    Assert.fail(ex.getMessage());
                }
            }

            @Override
            public void onFailure(Throwable ex) {
                Assert.fail(ex.getMessage());
            }
        });
        ClientHttpResponse response = listenableFuture.get();
        try {
            Assert.assertEquals("Invalid status code", NOT_FOUND, response.getStatusCode());
        } finally {
            response.close();
        }
    }

    @Test
    public void echo() throws Exception {
        AsyncClientHttpRequest request = this.factory.createAsyncRequest(new URI(((baseUrl) + "/echo")), PUT);
        Assert.assertEquals("Invalid HTTP method", PUT, request.getMethod());
        String headerName = "MyHeader";
        String headerValue1 = "value1";
        request.getHeaders().add(headerName, headerValue1);
        String headerValue2 = "value2";
        request.getHeaders().add(headerName, headerValue2);
        final byte[] body = "Hello World".getBytes("UTF-8");
        request.getHeaders().setContentLength(body.length);
        if (request instanceof StreamingHttpOutputMessage) {
            StreamingHttpOutputMessage streamingRequest = ((StreamingHttpOutputMessage) (request));
            streamingRequest.setBody(( outputStream) -> StreamUtils.copy(body, outputStream));
        } else {
            StreamUtils.copy(body, request.getBody());
        }
        Future<ClientHttpResponse> futureResponse = request.executeAsync();
        ClientHttpResponse response = futureResponse.get();
        try {
            Assert.assertEquals("Invalid status code", OK, response.getStatusCode());
            Assert.assertTrue("Header not found", response.getHeaders().containsKey(headerName));
            Assert.assertEquals("Header value not found", Arrays.asList(headerValue1, headerValue2), response.getHeaders().get(headerName));
            byte[] result = FileCopyUtils.copyToByteArray(response.getBody());
            Assert.assertTrue("Invalid body", Arrays.equals(body, result));
        } finally {
            response.close();
        }
    }

    @Test
    public void multipleWrites() throws Exception {
        AsyncClientHttpRequest request = this.factory.createAsyncRequest(new URI(((baseUrl) + "/echo")), POST);
        final byte[] body = "Hello World".getBytes("UTF-8");
        if (request instanceof StreamingHttpOutputMessage) {
            StreamingHttpOutputMessage streamingRequest = ((StreamingHttpOutputMessage) (request));
            streamingRequest.setBody(( outputStream) -> StreamUtils.copy(body, outputStream));
        } else {
            StreamUtils.copy(body, request.getBody());
        }
        Future<ClientHttpResponse> futureResponse = request.executeAsync();
        ClientHttpResponse response = futureResponse.get();
        try {
            FileCopyUtils.copy(body, request.getBody());
            Assert.fail("IllegalStateException expected");
        } catch (IllegalStateException ex) {
            // expected
        } finally {
            response.close();
        }
    }

    @Test
    public void headersAfterExecute() throws Exception {
        AsyncClientHttpRequest request = this.factory.createAsyncRequest(new URI(((baseUrl) + "/echo")), POST);
        request.getHeaders().add("MyHeader", "value");
        byte[] body = "Hello World".getBytes("UTF-8");
        FileCopyUtils.copy(body, request.getBody());
        Future<ClientHttpResponse> futureResponse = request.executeAsync();
        ClientHttpResponse response = futureResponse.get();
        try {
            request.getHeaders().add("MyHeader", "value");
            Assert.fail("UnsupportedOperationException expected");
        } catch (UnsupportedOperationException ex) {
            // expected
        } finally {
            response.close();
        }
    }

    @Test
    public void httpMethods() throws Exception {
        assertHttpMethod("get", GET);
        assertHttpMethod("head", HEAD);
        assertHttpMethod("post", POST);
        assertHttpMethod("put", PUT);
        assertHttpMethod("options", OPTIONS);
        assertHttpMethod("delete", DELETE);
    }

    @Test
    public void cancel() throws Exception {
        URI uri = new URI(((baseUrl) + "/status/notfound"));
        AsyncClientHttpRequest request = this.factory.createAsyncRequest(uri, GET);
        Future<ClientHttpResponse> futureResponse = request.executeAsync();
        futureResponse.cancel(true);
        Assert.assertTrue(futureResponse.isCancelled());
    }
}

