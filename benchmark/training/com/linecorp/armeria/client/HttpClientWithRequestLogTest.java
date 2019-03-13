/**
 * Copyright 2018 LINE Corporation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.linecorp.armeria.client;


import HttpMethod.GET;
import RequestLogAvailability.REQUEST_END;
import RequestLogAvailability.RESPONSE_END;
import com.linecorp.armeria.common.HttpRequest;
import com.linecorp.armeria.common.HttpResponse;
import com.linecorp.armeria.common.logging.RequestLog;
import com.linecorp.armeria.testing.internal.AnticipatedException;
import java.net.ConnectException;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Test;


public class HttpClientWithRequestLogTest {
    private static final String LOCAL_HOST = "http://127.0.0.1/";

    private static final AtomicReference<Throwable> requestCauseHolder = new AtomicReference<>();

    private static final AtomicReference<Throwable> responseCauseHolder = new AtomicReference<>();

    @Test
    public void exceptionRaisedInDecorator() {
        final HttpClient client = new HttpClientBuilder(HttpClientWithRequestLogTest.LOCAL_HOST).decorator(( delegate, ctx, req1) -> {
            throw new AnticipatedException();
        }).decorator(new HttpClientWithRequestLogTest.ExceptionHoldingDecorator()).build();
        final HttpRequest req = HttpRequest.of(GET, "/");
        assertThatThrownBy(() -> client.execute(req).aggregate().get()).hasCauseExactlyInstanceOf(AnticipatedException.class);
        // If the RequestLog has requestCause and responseCause, the RequestLog is complete.
        // The RequestLog should be complete so that ReleasableHolder#release() is called in UserClient
        // to decrease the active request count of EventLoop.
        await().untilAsserted(() -> assertThat(requestCauseHolder.get()).isExactlyInstanceOf(.class));
        await().untilAsserted(() -> assertThat(responseCauseHolder.get()).isExactlyInstanceOf(.class));
        await().untilAsserted(() -> assertThat(req.isComplete()).isTrue());
    }

    @Test
    public void invalidPath() {
        final HttpClient client = new HttpClientBuilder(HttpClientWithRequestLogTest.LOCAL_HOST).decorator(( delegate, ctx, req) -> {
            req.headers().path("/%");
            return delegate.execute(ctx, req);
        }).decorator(new HttpClientWithRequestLogTest.ExceptionHoldingDecorator()).build();
        final HttpRequest req = HttpRequest.of(GET, "/");
        assertThatThrownBy(() -> client.execute(req).aggregate().get()).hasCauseExactlyInstanceOf(IllegalArgumentException.class).hasMessageContaining("invalid path");
        await().untilAsserted(() -> assertThat(requestCauseHolder.get()).isExactlyInstanceOf(.class));
        await().untilAsserted(() -> assertThat(responseCauseHolder.get()).isExactlyInstanceOf(.class));
        await().untilAsserted(() -> assertThat(req.isComplete()).isTrue());
    }

    @Test
    public void unresolvedUri() {
        final HttpClient client = new HttpClientBuilder("http://unresolved.armeria.com").decorator(new HttpClientWithRequestLogTest.ExceptionHoldingDecorator()).build();
        final HttpRequest req = HttpRequest.of(GET, "/");
        assertThatThrownBy(() -> client.execute(req).aggregate().get()).isInstanceOf(Exception.class);
        await().untilAsserted(() -> assertThat(requestCauseHolder.get()).isNotNull());
        await().untilAsserted(() -> assertThat(responseCauseHolder.get()).isNotNull());
        await().untilAsserted(() -> assertThat(req.isComplete()).isTrue());
    }

    @Test
    public void connectionError() {
        // According to rfc7805, TCP port number 1 is not used so a connection error always happens.
        final HttpClient client = new HttpClientBuilder("http://127.0.0.1:1").decorator(new HttpClientWithRequestLogTest.ExceptionHoldingDecorator()).build();
        final HttpRequest req = HttpRequest.of(GET, "/");
        assertThatThrownBy(() -> client.execute(req).aggregate().get()).hasCauseInstanceOf(ConnectException.class);
        await().untilAsserted(() -> assertThat(requestCauseHolder.get()).hasCauseInstanceOf(.class));
        await().untilAsserted(() -> assertThat(responseCauseHolder.get()).hasCauseInstanceOf(.class));
        await().untilAsserted(() -> assertThat(req.isComplete()).isTrue());
    }

    private static class ExceptionHoldingDecorator implements DecoratingClientFunction<HttpRequest, HttpResponse> {
        @Override
        public HttpResponse execute(Client<HttpRequest, HttpResponse> delegate, ClientRequestContext ctx, HttpRequest req) throws Exception {
            final RequestLog requestLog = ctx.log();
            requestLog.addListener(( log) -> com.linecorp.armeria.client.requestCauseHolder.set(log.requestCause()), REQUEST_END);
            requestLog.addListener(( log) -> com.linecorp.armeria.client.responseCauseHolder.set(log.responseCause()), RESPONSE_END);
            return delegate.execute(ctx, req);
        }
    }
}

