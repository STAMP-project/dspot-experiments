/**
 * Copyright 2002-2018 the original author or authors.
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
import java.net.URI;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.StreamingHttpOutputMessage;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StreamUtils;


/**
 *
 *
 * @author Arjen Poutsma
 */
public abstract class AbstractHttpRequestFactoryTestCase extends AbstractMockWebServerTestCase {
    protected ClientHttpRequestFactory factory;

    @Test
    public void status() throws Exception {
        URI uri = new URI(((baseUrl) + "/status/notfound"));
        ClientHttpRequest request = factory.createRequest(uri, GET);
        Assert.assertEquals("Invalid HTTP method", GET, request.getMethod());
        Assert.assertEquals("Invalid HTTP URI", uri, request.getURI());
        ClientHttpResponse response = request.execute();
        try {
            Assert.assertEquals("Invalid status code", NOT_FOUND, response.getStatusCode());
        } finally {
            response.close();
        }
    }

    @Test
    public void echo() throws Exception {
        ClientHttpRequest request = factory.createRequest(new URI(((baseUrl) + "/echo")), PUT);
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
        ClientHttpResponse response = request.execute();
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

    @Test(expected = IllegalStateException.class)
    public void multipleWrites() throws Exception {
        ClientHttpRequest request = factory.createRequest(new URI(((baseUrl) + "/echo")), POST);
        final byte[] body = "Hello World".getBytes("UTF-8");
        if (request instanceof StreamingHttpOutputMessage) {
            StreamingHttpOutputMessage streamingRequest = ((StreamingHttpOutputMessage) (request));
            streamingRequest.setBody(( outputStream) -> {
                StreamUtils.copy(body, outputStream);
                outputStream.flush();
                outputStream.close();
            });
        } else {
            StreamUtils.copy(body, request.getBody());
        }
        request.execute();
        FileCopyUtils.copy(body, request.getBody());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void headersAfterExecute() throws Exception {
        ClientHttpRequest request = factory.createRequest(new URI(((baseUrl) + "/status/ok")), POST);
        request.getHeaders().add("MyHeader", "value");
        byte[] body = "Hello World".getBytes("UTF-8");
        FileCopyUtils.copy(body, request.getBody());
        ClientHttpResponse response = request.execute();
        try {
            request.getHeaders().add("MyHeader", "value");
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
    public void queryParameters() throws Exception {
        URI uri = new URI(((baseUrl) + "/params?param1=value&param2=value1&param2=value2"));
        ClientHttpRequest request = factory.createRequest(uri, GET);
        ClientHttpResponse response = request.execute();
        try {
            Assert.assertEquals("Invalid status code", OK, response.getStatusCode());
        } finally {
            response.close();
        }
    }
}

