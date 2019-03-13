/**
 * -\-\-
 * Spotify Apollo Jetty HTTP Server Module
 * --
 * Copyright (C) 2013 - 2015 Spotify AB
 * --
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
 * -/-/-
 */
package com.spotify.apollo.http.server;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.spotify.apollo.request.OngoingRequest;
import com.spotify.apollo.request.RequestHandler;
import com.spotify.apollo.request.RequestMetadataImpl;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Optional;
import javax.servlet.AsyncContext;
import javax.servlet.ServletOutputStream;
import okio.ByteString;
import org.eclipse.jetty.server.HttpChannel;
import org.eclipse.jetty.server.HttpInput;
import org.eclipse.jetty.server.Request;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;


public class ApolloRequestHandlerTest {
    private ApolloRequestHandler requestHandler;

    private ApolloRequestHandlerTest.FakeRequestHandler delegate;

    private MockHttpServletRequest httpServletRequest;

    private MockHttpServletResponse response;

    private Duration requestTimeout;

    private Request baseRequest;

    @Mock
    AsyncContext asyncContext;

    @Mock
    ServletOutputStream outputStream;

    @Mock
    private HttpChannel httpChannel;

    @Mock
    private HttpInput httpInput;

    @Mock
    private RequestOutcomeConsumer logger;

    @Test
    public void shouldForwardRequestsToDelegate() throws Exception {
        requestHandler.handle("/floop", baseRequest, httpServletRequest, response);
        Assert.assertThat(delegate.requests, CoreMatchers.hasItem(CoreMatchers.instanceOf(OngoingRequest.class)));
        Assert.assertThat(delegate.requests, Matchers.hasSize(1));
    }

    @Test
    public void shouldExtractCorrectMethod() throws Exception {
        Assert.assertThat(requestHandler.asApolloRequest(httpServletRequest).method(), CoreMatchers.is("PUT"));
    }

    @Test
    public void shouldExtractCompleteUri() throws Exception {
        Assert.assertThat(requestHandler.asApolloRequest(httpServletRequest).uri(), CoreMatchers.is("http://somehost/a/b?q=abc&b=adf&q=def"));
    }

    @Test
    public void shouldExtractFirstQueryParameter() throws Exception {
        Assert.assertThat(requestHandler.asApolloRequest(httpServletRequest).parameter("q"), CoreMatchers.is(Optional.of("abc")));
    }

    @Test
    public void shouldExtractAllParameters() throws Exception {
        Assert.assertThat(requestHandler.asApolloRequest(httpServletRequest).parameters().get("q"), CoreMatchers.is(ImmutableList.of("abc", "def")));
    }

    @Test
    public void shouldReturnEmptyForMissingParameter() throws Exception {
        Assert.assertThat(requestHandler.asApolloRequest(httpServletRequest).parameter("p"), CoreMatchers.is(Optional.<String>empty()));
    }

    @Test
    public void shouldReturnEmptyPayloadWhenMissing() throws Exception {
        Assert.assertThat(requestHandler.asApolloRequest(httpServletRequest).payload(), CoreMatchers.is(Optional.empty()));
    }

    @Test
    public void shouldReturnPayloadWhenPresent() throws Exception {
        httpServletRequest.setContent("hi there".getBytes(StandardCharsets.UTF_8));
        Assert.assertThat(requestHandler.asApolloRequest(httpServletRequest).payload(), CoreMatchers.is(Optional.of(ByteString.encodeUtf8("hi there"))));
    }

    @Test
    public void shouldReturnPayloadWhenPresentOnChunkedRequest() throws Exception {
        ApolloRequestHandlerTest.ChunkedMockRequest chunkedMockRequest = new ApolloRequestHandlerTest.ChunkedMockRequest("POST", "http://somehost/a/b?q=abc&b=adf&q=def");
        Assert.assertThat(requestHandler.asApolloRequest(chunkedMockRequest).payload(), CoreMatchers.is(Optional.of(ByteString.encodeUtf8("hi there"))));
    }

    /* When using Transfer-Encoding: chunked (see RFC 7230, section 3.3.1: Transfer-Encoding), a request may return -1
    when getting its content length since it is unknown, even though there is content being transmitted as a series
    of chunks. This means that we cannot conclude whether the request bears a payload or not purely on the content
    length check.

    MockHttpServletRequest does not provide support to test Transfer-Encoding. The following class emulates this
    behaviour by setting a content and returning a -1 as its length, so that we can test this functionality.
     */
    private class ChunkedMockRequest extends MockHttpServletRequest {
        ChunkedMockRequest(String method, String path) {
            super(method, path);
            setContent("hi there".getBytes(StandardCharsets.UTF_8));
            addHeader("Transfer-Encoding", "chunked");
        }

        @Override
        public int getContentLength() {
            return -1;
        }
    }

    @Test
    public void shouldExtractCallingServiceFromHeader() throws Exception {
        httpServletRequest = mockRequest("PUT", "http://somehost/a/b?q=abc&b=adf&q=def", ImmutableMap.of("X-Calling-Service", "testservice"));
        Assert.assertThat(requestHandler.asApolloRequest(httpServletRequest).service(), CoreMatchers.is(Optional.of("testservice")));
    }

    @Test
    public void shouldExtractCallingServiceFromHeaderLowerCase() throws Exception {
        httpServletRequest = mockRequest("PUT", "http://somehost/a/b?q=abc&b=adf&q=def", ImmutableMap.of("x-calling-service", "testservice"));
        Assert.assertThat(requestHandler.asApolloRequest(httpServletRequest).service(), CoreMatchers.is(Optional.of("testservice")));
    }

    @Test
    public void shouldNotExtractCallingServiceFromEmptyHeader() throws Exception {
        httpServletRequest = mockRequest("PUT", "http://somehost/a/b?q=abc&b=adf&q=def", ImmutableMap.of("X-Calling-Service", ""));
        Assert.assertThat(requestHandler.asApolloRequest(httpServletRequest).service(), CoreMatchers.is(Optional.empty()));
    }

    @Test
    public void shouldHandleMissingCallingServiceHeader() throws Exception {
        httpServletRequest = mockRequest("PUT", "http://somehost/a/b?q=abc&b=adf&q=def", Collections.emptyMap());
        Assert.assertThat(requestHandler.asApolloRequest(httpServletRequest).service(), CoreMatchers.is(Optional.empty()));
    }

    // I would prefer to test this in a less implementation-dependent way (validating that a timeout
    // is actually sent, rather than that a particular listener is registered), but the servlet APIs
    // aren't designed that way.
    @Test
    public void shouldRegisterTimeoutListenerWithContext() throws Exception {
        requestHandler.handle("/floop", baseRequest, httpServletRequest, response);
        Mockito.verify(asyncContext).addListener(ArgumentMatchers.any(TimeoutListener.class));
    }

    // I would prefer to test this in a less implementation-dependent way (validating that a timeout
    // is actually sent, rather than that a particular timeout value is set), but the servlet APIs
    // aren't designed that way.
    @Test
    public void shouldSetTimeout() throws Exception {
        requestHandler.handle("/floop", baseRequest, httpServletRequest, response);
        Mockito.verify(asyncContext).setTimeout(requestTimeout.toMillis());
    }

    @Test
    public void shouldAddProtocolToOngoingRequest() throws Exception {
        requestHandler.handle("/floop", baseRequest, httpServletRequest, response);
        Assert.assertThat(requestMetadata().httpVersion(), CoreMatchers.is("HTTP/1.1"));
    }

    @Test
    public void shouldAddRemoteAddressToOngoingRequest() throws Exception {
        requestHandler.handle("/floop", baseRequest, httpServletRequest, response);
        Assert.assertThat(requestMetadata().remoteAddress(), CoreMatchers.is(Optional.of(RequestMetadataImpl.hostAndPort("123.45.67.89", 8734))));
    }

    private static class FakeRequestHandler implements RequestHandler {
        private final LinkedList<OngoingRequest> requests = new LinkedList<>();

        @Override
        public void handle(OngoingRequest ongoingRequest) {
            requests.add(ongoingRequest);
        }
    }
}

