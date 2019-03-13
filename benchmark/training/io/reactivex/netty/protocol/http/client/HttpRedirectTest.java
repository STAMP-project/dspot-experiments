/**
 * Copyright 2016 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.reactivex.netty.protocol.http.client;


import HttpMethod.POST;
import HttpResponseStatus.OK;
import HttpResponseStatus.SEE_OTHER;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.reactivex.netty.client.pool.PoolConfig;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import rx.observers.TestSubscriber;


public class HttpRedirectTest {
    @Rule
    public final HttpClientRule clientRule = new HttpClientRule();

    @Test(timeout = 60000)
    public void testNoLocation() throws Exception {
        final String requestUri = "/";
        TestSubscriber<HttpClientResponse<ByteBuf>> subscriber = sendRequest(requestUri);
        assertRequestWritten(requestUri);
        HttpResponse response = new io.netty.handler.codec.http.DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.SEE_OTHER);
        clientRule.feedResponseAndComplete(response);
        subscriber.awaitTerminalEvent();
        MatcherAssert.assertThat("Unexpected error notifications count.", subscriber.getOnErrorEvents(), hasSize(1));
        MatcherAssert.assertThat("Unexpected error.", subscriber.getOnErrorEvents().get(0), is(instanceOf(HttpRedirectException.class)));
    }

    @Test(timeout = 60000)
    public void testInvalidRedirectLocation() throws Exception {
        final String requestUri = "/";
        TestSubscriber<HttpClientResponse<ByteBuf>> subscriber = sendRequest(requestUri);
        assertRequestWritten(requestUri);
        sendRedirects(" ");// blank is an invalid URI

        subscriber.awaitTerminalEvent();
        MatcherAssert.assertThat("Unexpected error notifications count.", subscriber.getOnErrorEvents(), hasSize(1));
        MatcherAssert.assertThat("Unexpected error.", subscriber.getOnErrorEvents().get(0), is(instanceOf(HttpRedirectException.class)));
    }

    @Test(timeout = 60000)
    public void testTooManyRedirect() throws Throwable {
        final String requestUri = "/";
        TestSubscriber<HttpClientResponse<ByteBuf>> subscriber = sendRequest(requestUri);
        assertRequestWritten(requestUri);
        sendRedirects("/blah", "/blah");
        subscriber.awaitTerminalEvent();
        MatcherAssert.assertThat("Unexpected error notifications count.", subscriber.getOnErrorEvents(), hasSize(1));
        MatcherAssert.assertThat("Unexpected error.", subscriber.getOnErrorEvents().get(0), is(instanceOf(HttpRedirectException.class)));
    }

    @Test(timeout = 60000)
    public void testRedirectLoop() throws Throwable {
        final String requestUri = "/blah";
        TestSubscriber<HttpClientResponse<ByteBuf>> subscriber = sendRequest(requestUri);
        assertRequestWritten(requestUri);
        sendRedirects(requestUri);
        subscriber.awaitTerminalEvent();
        MatcherAssert.assertThat("Unexpected error notifications count.", subscriber.getOnErrorEvents(), hasSize(1));
        MatcherAssert.assertThat("Unexpected error.", subscriber.getOnErrorEvents().get(0), is(instanceOf(HttpRedirectException.class)));
    }

    @Test(timeout = 60000)
    public void testAbsoluteRedirect() throws Throwable {
        final String requestUri = "/blah";
        TestSubscriber<HttpClientResponse<ByteBuf>> subscriber = sendRequest(requestUri);
        assertRequestWritten(requestUri);
        sendRedirects("http://localhost:8888/blah");
        subscriber.awaitTerminalEvent();
        subscriber.assertNoErrors();
        MatcherAssert.assertThat("Unexpected onNext notifications count.", subscriber.getOnNextEvents(), hasSize(1));
        HttpClientResponse<ByteBuf> response = subscriber.getOnNextEvents().get(0);
        MatcherAssert.assertThat("Unexpected response.", response, is(notNullValue()));
        MatcherAssert.assertThat("Unexpected response status.", response.getStatus().code(), is(SEE_OTHER.code()));
    }

    @Test(timeout = 60000)
    public void testRedirectNoConnPool() throws Throwable {
        final String requestUri = "/";
        HttpClient<ByteBuf, ByteBuf> client = clientRule.getHttpClient().followRedirects(1);
        TestSubscriber<HttpClientResponse<ByteBuf>> subscriber = HttpRedirectTest.sendRequest(client, requestUri);
        assertRequestWritten(requestUri);
        sendRedirects("/blah", "/blah");
        subscriber.awaitTerminalEvent();
        MatcherAssert.assertThat("Unexpected error notifications count.", subscriber.getOnErrorEvents(), hasSize(1));
        MatcherAssert.assertThat("Unexpected error.", subscriber.getOnErrorEvents().get(0), is(instanceOf(HttpRedirectException.class)));
    }

    @Test(timeout = 60000)
    public void testRedirectWithConnPool() throws Throwable {
        PoolConfig<ByteBuf, ByteBuf> pConfig = new PoolConfig<ByteBuf, ByteBuf>().maxConnections(10);
        clientRule.setupPooledConnectionFactory(pConfig);// sets the client et al.

        HttpClient<ByteBuf, ByteBuf> client = clientRule.getHttpClient().followRedirects(1);
        final String requestUri = "/";
        TestSubscriber<HttpClientResponse<ByteBuf>> subscriber = HttpRedirectTest.sendRequest(client, requestUri);
        assertRequestWritten(requestUri);
        sendRedirects("/blah", "blah");
        subscriber.awaitTerminalEvent();
        MatcherAssert.assertThat("Unexpected error notifications count.", subscriber.getOnErrorEvents(), hasSize(1));
        MatcherAssert.assertThat("Unexpected error.", subscriber.getOnErrorEvents().get(0), is(instanceOf(HttpRedirectException.class)));
    }

    @Test(timeout = 60000)
    public void testNoRedirect() {
        HttpClient<ByteBuf, ByteBuf> client = clientRule.getHttpClient().followRedirects(false);
        final String requestUri = "/";
        TestSubscriber<HttpClientResponse<ByteBuf>> subscriber = HttpRedirectTest.sendRequest(client, requestUri);
        assertRequestWritten(requestUri);
        sendRedirects("/blah2");
        subscriber.awaitTerminalEvent();
        subscriber.assertNoErrors();
        MatcherAssert.assertThat("Unexpected onNext notifications count.", subscriber.getOnNextEvents(), hasSize(1));
        HttpClientResponse<ByteBuf> response = subscriber.getOnNextEvents().get(0);
        MatcherAssert.assertThat("Unexpected response.", response, is(notNullValue()));
        MatcherAssert.assertThat("Unexpected response status.", response.getStatus().code(), is(SEE_OTHER.code()));
    }

    @Test(timeout = 60000)
    public void testRedirectPost() throws Throwable {
        final String requestUri = "/";
        TestSubscriber<HttpClientResponse<ByteBuf>> subscriber = HttpRedirectTest.sendRequest(POST, requestUri);
        final HttpResponseStatus responseStatus = HttpResponseStatus.FOUND;
        clientRule.assertRequestHeadersWritten(POST, requestUri);
        sendRedirects(responseStatus, "/blah");
        subscriber.awaitTerminalEvent();
        subscriber.assertNoErrors();
        MatcherAssert.assertThat("Unexpected onNext notifications count.", subscriber.getOnNextEvents(), hasSize(1));
        HttpClientResponse<ByteBuf> response = subscriber.getOnNextEvents().get(0);
        MatcherAssert.assertThat("Unexpected response.", response, is(notNullValue()));
        MatcherAssert.assertThat("Unexpected response status.", response.getStatus().code(), is(responseStatus.code()));
    }

    @Test(timeout = 60000)
    public void testRedirectPostWith303() throws Throwable {
        final String requestUri = "/";
        TestSubscriber<HttpClientResponse<ByteBuf>> subscriber = HttpRedirectTest.sendRequest(POST, requestUri);
        clientRule.assertRequestHeadersWritten(POST, requestUri);
        sendRedirects(SEE_OTHER, "/blah");
        sendResponse(OK);
        subscriber.awaitTerminalEvent();
        subscriber.assertNoErrors();
        MatcherAssert.assertThat("Unexpected onNext notifications count.", subscriber.getOnNextEvents(), hasSize(1));
        HttpClientResponse<ByteBuf> response = subscriber.getOnNextEvents().get(0);
        MatcherAssert.assertThat("Unexpected response.", response, is(notNullValue()));
        MatcherAssert.assertThat("Unexpected response status.", response.getStatus().code(), is(OK.code()));
    }
}

