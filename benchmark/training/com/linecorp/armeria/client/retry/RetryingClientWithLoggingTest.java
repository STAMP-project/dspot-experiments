/**
 * Copyright 2017 LINE Corporation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.linecorp.armeria.client.retry;


import HttpStatus.INTERNAL_SERVER_ERROR;
import HttpStatus.OK;
import MediaType.PLAIN_TEXT_UTF_8;
import com.linecorp.armeria.client.HttpClient;
import com.linecorp.armeria.common.HttpRequest;
import com.linecorp.armeria.common.HttpResponse;
import com.linecorp.armeria.common.logging.RequestLog;
import com.linecorp.armeria.common.logging.RequestLogListener;
import com.linecorp.armeria.server.AbstractHttpService;
import com.linecorp.armeria.server.ServerBuilder;
import com.linecorp.armeria.server.ServiceRequestContext;
import com.linecorp.armeria.testing.server.ServerRule;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Rule;
import org.junit.Test;


public class RetryingClientWithLoggingTest {
    @Rule
    public final ServerRule server = new ServerRule() {
        @Override
        protected void configure(ServerBuilder sb) throws Exception {
            sb.service("/hello", new AbstractHttpService() {
                final AtomicInteger reqCount = new AtomicInteger();

                @Override
                protected HttpResponse doGet(ServiceRequestContext ctx, HttpRequest req) throws Exception {
                    ctx.addAdditionalResponseTrailer(com.linecorp.armeria.common.HttpHeaderNames.of("foo"), "bar");
                    if ((reqCount.getAndIncrement()) < 2) {
                        return HttpResponse.of(INTERNAL_SERVER_ERROR);
                    } else {
                        return HttpResponse.of(OK, PLAIN_TEXT_UTF_8, "hello");
                    }
                }
            });
        }
    };

    private final RequestLogListener listener = new RequestLogListener() {
        @Override
        public void onRequestLog(RequestLog log) throws Exception {
            logResult.add(log);
            final int index = logIndex.getAndIncrement();
            if ((index % 2) == 0) {
                RetryingClientWithLoggingTest.assertRequestSideLog(log);
            } else {
                RetryingClientWithLoggingTest.assertResponseSideLog(log, (index == (successLogIndex)));
            }
        }
    };

    private final AtomicInteger logIndex = new AtomicInteger();

    private int successLogIndex;

    private final List<RequestLog> logResult = new ArrayList<>();

    // HttpClient -> RetryingClient -> LoggingClient -> HttpClientDelegate
    // In this case, all of the requests and responses are logged.
    @Test
    public void retryingThenLogging() {
        successLogIndex = 5;
        final HttpClient client = new com.linecorp.armeria.client.HttpClientBuilder(server.uri("/")).decorator(loggingDecorator()).decorator(new RetryingHttpClientBuilder(((RetryStrategyWithContent<HttpResponse>) (( ctx, response) -> response.aggregate().handle(( msg, cause) -> {
            if ("hello".equals(msg.contentUtf8())) {
                return null;
            }
            return Backoff.ofDefault();
        })))).newDecorator()).build();
        assertThat(client.get("/hello").aggregate().join().contentUtf8()).isEqualTo("hello");
        // wait until 6 logs(3 requests and 3 responses) are called back
        await().untilAsserted(() -> assertThat(logResult.size()).isEqualTo(((successLogIndex) + 1)));
    }

    // HttpClient -> LoggingClient -> RetryingClient -> HttpClientDelegate
    // In this case, only the first request and the last response are logged.
    @Test
    public void loggingThenRetrying() throws Exception {
        successLogIndex = 1;
        final HttpClient client = new com.linecorp.armeria.client.HttpClientBuilder(server.uri("/")).decorator(RetryingHttpClient.newDecorator(RetryStrategy.onServerErrorStatus())).decorator(loggingDecorator()).build();
        assertThat(client.get("/hello").aggregate().join().contentUtf8()).isEqualTo("hello");
        // wait until 2 logs are called back
        await().untilAsserted(() -> assertThat(logResult.size()).isEqualTo(((successLogIndex) + 1)));
        // toStringRequestOnly() is same in the request log and the response log
        assertThat(logResult.get(0).toStringRequestOnly()).isEqualTo(logResult.get(1).toStringRequestOnly());
    }
}

