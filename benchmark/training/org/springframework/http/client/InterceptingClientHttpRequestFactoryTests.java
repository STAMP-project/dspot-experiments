/**
 * Copyright 2002-2017 the original author or authors.
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


import HttpMethod.GET;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.support.HttpRequestWrapper;


/**
 *
 *
 * @author Arjen Poutsma
 * @author Juergen Hoeller
 */
public class InterceptingClientHttpRequestFactoryTests {
    private InterceptingClientHttpRequestFactoryTests.RequestFactoryMock requestFactoryMock = new InterceptingClientHttpRequestFactoryTests.RequestFactoryMock();

    private InterceptingClientHttpRequestFactoryTests.RequestMock requestMock = new InterceptingClientHttpRequestFactoryTests.RequestMock();

    private InterceptingClientHttpRequestFactoryTests.ResponseMock responseMock = new InterceptingClientHttpRequestFactoryTests.ResponseMock();

    private InterceptingClientHttpRequestFactory requestFactory;

    @Test
    public void basic() throws Exception {
        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
        interceptors.add(new InterceptingClientHttpRequestFactoryTests.NoOpInterceptor());
        interceptors.add(new InterceptingClientHttpRequestFactoryTests.NoOpInterceptor());
        interceptors.add(new InterceptingClientHttpRequestFactoryTests.NoOpInterceptor());
        requestFactory = new InterceptingClientHttpRequestFactory(requestFactoryMock, interceptors);
        ClientHttpRequest request = requestFactory.createRequest(new URI("http://example.com"), GET);
        ClientHttpResponse response = request.execute();
        Assert.assertTrue(((InterceptingClientHttpRequestFactoryTests.NoOpInterceptor) (interceptors.get(0))).invoked);
        Assert.assertTrue(((InterceptingClientHttpRequestFactoryTests.NoOpInterceptor) (interceptors.get(1))).invoked);
        Assert.assertTrue(((InterceptingClientHttpRequestFactoryTests.NoOpInterceptor) (interceptors.get(2))).invoked);
        Assert.assertTrue(requestMock.executed);
        Assert.assertSame(responseMock, response);
    }

    @Test
    public void noExecution() throws Exception {
        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
        interceptors.add(new ClientHttpRequestInterceptor() {
            @Override
            public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
                return responseMock;
            }
        });
        interceptors.add(new InterceptingClientHttpRequestFactoryTests.NoOpInterceptor());
        requestFactory = new InterceptingClientHttpRequestFactory(requestFactoryMock, interceptors);
        ClientHttpRequest request = requestFactory.createRequest(new URI("http://example.com"), GET);
        ClientHttpResponse response = request.execute();
        Assert.assertFalse(((InterceptingClientHttpRequestFactoryTests.NoOpInterceptor) (interceptors.get(1))).invoked);
        Assert.assertFalse(requestMock.executed);
        Assert.assertSame(responseMock, response);
    }

    @Test
    public void changeHeaders() throws Exception {
        final String headerName = "Foo";
        final String headerValue = "Bar";
        final String otherValue = "Baz";
        ClientHttpRequestInterceptor interceptor = new ClientHttpRequestInterceptor() {
            @Override
            public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
                HttpRequestWrapper wrapper = new HttpRequestWrapper(request);
                wrapper.getHeaders().add(headerName, otherValue);
                return execution.execute(wrapper, body);
            }
        };
        requestMock = new InterceptingClientHttpRequestFactoryTests.RequestMock() {
            @Override
            public ClientHttpResponse execute() throws IOException {
                List<String> headerValues = getHeaders().get(headerName);
                Assert.assertEquals(2, headerValues.size());
                Assert.assertEquals(headerValue, headerValues.get(0));
                Assert.assertEquals(otherValue, headerValues.get(1));
                return super.execute();
            }
        };
        requestMock.getHeaders().add(headerName, headerValue);
        requestFactory = new InterceptingClientHttpRequestFactory(requestFactoryMock, Collections.singletonList(interceptor));
        ClientHttpRequest request = requestFactory.createRequest(new URI("http://example.com"), GET);
        request.execute();
    }

    @Test
    public void changeURI() throws Exception {
        final URI changedUri = new URI("http://example.com/2");
        ClientHttpRequestInterceptor interceptor = new ClientHttpRequestInterceptor() {
            @Override
            public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
                return execution.execute(new HttpRequestWrapper(request) {
                    @Override
                    public URI getURI() {
                        return changedUri;
                    }
                }, body);
            }
        };
        requestFactoryMock = new InterceptingClientHttpRequestFactoryTests.RequestFactoryMock() {
            @Override
            public ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod) throws IOException {
                Assert.assertEquals(changedUri, uri);
                return super.createRequest(uri, httpMethod);
            }
        };
        requestFactory = new InterceptingClientHttpRequestFactory(requestFactoryMock, Collections.singletonList(interceptor));
        ClientHttpRequest request = requestFactory.createRequest(new URI("http://example.com"), GET);
        request.execute();
    }

    @Test
    public void changeMethod() throws Exception {
        final HttpMethod changedMethod = HttpMethod.POST;
        ClientHttpRequestInterceptor interceptor = new ClientHttpRequestInterceptor() {
            @Override
            public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
                return execution.execute(new HttpRequestWrapper(request) {
                    @Override
                    public HttpMethod getMethod() {
                        return changedMethod;
                    }
                }, body);
            }
        };
        requestFactoryMock = new InterceptingClientHttpRequestFactoryTests.RequestFactoryMock() {
            @Override
            public ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod) throws IOException {
                Assert.assertEquals(changedMethod, httpMethod);
                return super.createRequest(uri, httpMethod);
            }
        };
        requestFactory = new InterceptingClientHttpRequestFactory(requestFactoryMock, Collections.singletonList(interceptor));
        ClientHttpRequest request = requestFactory.createRequest(new URI("http://example.com"), GET);
        request.execute();
    }

    @Test
    public void changeBody() throws Exception {
        final byte[] changedBody = "Foo".getBytes();
        ClientHttpRequestInterceptor interceptor = new ClientHttpRequestInterceptor() {
            @Override
            public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
                return execution.execute(request, changedBody);
            }
        };
        requestFactory = new InterceptingClientHttpRequestFactory(requestFactoryMock, Collections.singletonList(interceptor));
        ClientHttpRequest request = requestFactory.createRequest(new URI("http://example.com"), GET);
        request.execute();
        Assert.assertTrue(Arrays.equals(changedBody, requestMock.body.toByteArray()));
    }

    private static class NoOpInterceptor implements ClientHttpRequestInterceptor {
        private boolean invoked = false;

        @Override
        public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
            invoked = true;
            return execution.execute(request, body);
        }
    }

    private class RequestFactoryMock implements ClientHttpRequestFactory {
        @Override
        public ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod) throws IOException {
            requestMock.setURI(uri);
            requestMock.setMethod(httpMethod);
            return requestMock;
        }
    }

    private class RequestMock implements ClientHttpRequest {
        private URI uri;

        private HttpMethod method;

        private org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();

        private ByteArrayOutputStream body = new ByteArrayOutputStream();

        private boolean executed = false;

        private RequestMock() {
        }

        @Override
        public URI getURI() {
            return uri;
        }

        public void setURI(URI uri) {
            this.uri = uri;
        }

        @Override
        public HttpMethod getMethod() {
            return method;
        }

        @Override
        public String getMethodValue() {
            return method.name();
        }

        public void setMethod(HttpMethod method) {
            this.method = method;
        }

        @Override
        public org.springframework.http.HttpHeaders getHeaders() {
            return headers;
        }

        @Override
        public OutputStream getBody() throws IOException {
            return body;
        }

        @Override
        public ClientHttpResponse execute() throws IOException {
            executed = true;
            return responseMock;
        }
    }

    private static class ResponseMock implements ClientHttpResponse {
        private HttpStatus statusCode = HttpStatus.OK;

        private String statusText = "";

        private org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();

        @Override
        public HttpStatus getStatusCode() throws IOException {
            return statusCode;
        }

        @Override
        public int getRawStatusCode() throws IOException {
            return statusCode.value();
        }

        @Override
        public String getStatusText() throws IOException {
            return statusText;
        }

        @Override
        public org.springframework.http.HttpHeaders getHeaders() {
            return headers;
        }

        @Override
        public InputStream getBody() throws IOException {
            return null;
        }

        @Override
        public void close() {
        }
    }
}

