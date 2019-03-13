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
package com.linecorp.armeria.server;


import HttpHeaderNames.CONTENT_LENGTH;
import HttpMethod.POST;
import HttpStatus.INTERNAL_SERVER_ERROR;
import HttpStatus.NOT_FOUND;
import HttpStatus.OK;
import HttpStatus.REQUEST_ENTITY_TOO_LARGE;
import MediaType.PLAIN_TEXT_UTF_8;
import com.linecorp.armeria.client.ClientFactory;
import com.linecorp.armeria.client.ClientFactoryBuilder;
import com.linecorp.armeria.client.HttpClient;
import com.linecorp.armeria.common.AggregatedHttpMessage;
import com.linecorp.armeria.common.HttpData;
import com.linecorp.armeria.common.HttpHeaders;
import com.linecorp.armeria.common.HttpObject;
import com.linecorp.armeria.common.HttpRequest;
import com.linecorp.armeria.common.HttpRequestWriter;
import com.linecorp.armeria.common.HttpResponse;
import com.linecorp.armeria.common.HttpResponseWriter;
import com.linecorp.armeria.common.util.EventLoopGroups;
import com.linecorp.armeria.common.util.Exceptions;
import com.linecorp.armeria.internal.InboundTrafficController;
import com.linecorp.armeria.testing.server.ServerRule;
import io.netty.channel.EventLoopGroup;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RunWith(Parameterized.class)
public class HttpServerStreamingTest {
    private static final Logger logger = LoggerFactory.getLogger(HttpServerStreamingTest.class);

    private static final EventLoopGroup workerGroup = EventLoopGroups.newEventLoopGroup(1);

    private static final ClientFactory clientFactory = // Will be shut down by the Server.
    new ClientFactoryBuilder().workerGroup(HttpServerStreamingTest.workerGroup, false).idleTimeout(Duration.ofSeconds(3)).sslContextCustomizer(( b) -> b.trustManager(InsecureTrustManagerFactory.INSTANCE)).build();

    // Stream as much as twice of the heap.
    private static final long STREAMING_CONTENT_LENGTH = (Runtime.getRuntime().maxMemory()) * 2;

    private static final int STREAMING_CONTENT_CHUNK_LENGTH = ((int) (Math.min(Integer.MAX_VALUE, ((HttpServerStreamingTest.STREAMING_CONTENT_LENGTH) / 8))));

    private static volatile long serverMaxRequestLength;

    @ClassRule
    public static final ServerRule server = new ServerRule() {
        @Override
        protected void configure(ServerBuilder sb) throws Exception {
            sb.workerGroup(HttpServerStreamingTest.workerGroup, true);
            sb.http(0);
            sb.https(0);
            sb.tlsSelfSigned();
            sb.service("/count", new HttpServerStreamingTest.CountingService(false));
            sb.service("/slow_count", new HttpServerStreamingTest.CountingService(true));
            sb.serviceUnder("/zeroes", new AbstractHttpService() {
                @Override
                protected HttpResponse doGet(ServiceRequestContext ctx, HttpRequest req) {
                    final long length = Long.parseLong(ctx.mappedPath().substring(1));
                    final HttpResponseWriter res = HttpResponse.streaming();
                    res.write(HttpHeaders.of(OK).setLong(CONTENT_LENGTH, length));
                    HttpServerStreamingTest.stream(res, length, HttpServerStreamingTest.STREAMING_CONTENT_CHUNK_LENGTH);
                    return res;
                }
            });
            final Function<Service<HttpRequest, HttpResponse>, Service<HttpRequest, HttpResponse>> decorator = ( s) -> new SimpleDecoratingService<HttpRequest, HttpResponse>(s) {
                @Override
                public HttpResponse serve(ServiceRequestContext ctx, HttpRequest req) throws Exception {
                    ctx.setMaxRequestLength(HttpServerStreamingTest.serverMaxRequestLength);
                    return delegate().serve(ctx, req);
                }
            };
            sb.decorator(decorator);
            sb.defaultMaxRequestLength(0);
            sb.defaultRequestTimeoutMillis(0);
            sb.idleTimeout(Duration.ofSeconds(5));
        }
    };

    private final com.linecorp.armeria.common.SessionProtocol protocol;

    private HttpClient client;

    public HttpServerStreamingTest(com.linecorp.armeria.common.SessionProtocol protocol) {
        this.protocol = protocol;
    }

    @Test(timeout = 10000)
    public void testTooLargeContent() throws Exception {
        final int maxContentLength = 65536;
        HttpServerStreamingTest.serverMaxRequestLength = maxContentLength;
        final HttpRequestWriter req = HttpRequest.streaming(POST, "/count");
        final CompletableFuture<AggregatedHttpMessage> f = client().execute(req).aggregate();
        HttpServerStreamingTest.stream(req, (maxContentLength + 1), 1024);
        final AggregatedHttpMessage res = f.get();
        assertThat(res.status()).isEqualTo(REQUEST_ENTITY_TOO_LARGE);
        assertThat(res.contentType()).isEqualTo(PLAIN_TEXT_UTF_8);
        assertThat(res.contentUtf8()).isEqualTo("413 Request Entity Too Large");
    }

    @Test(timeout = 10000)
    public void testTooLargeContentToNonExistentService() throws Exception {
        final int maxContentLength = 65536;
        HttpServerStreamingTest.serverMaxRequestLength = maxContentLength;
        final byte[] content = new byte[maxContentLength + 1];
        final AggregatedHttpMessage res = client().post("/non-existent", content).aggregate().get();
        assertThat(res.status()).isEqualTo(NOT_FOUND);
        assertThat(res.contentUtf8()).isEqualTo("404 Not Found");
    }

    @Test(timeout = 60000)
    public void testStreamingRequest() throws Exception {
        runStreamingRequestTest("/count");
    }

    @Test(timeout = 120000)
    public void testStreamingRequestWithSlowService() throws Exception {
        final int oldNumDeferredReads = InboundTrafficController.numDeferredReads();
        runStreamingRequestTest("/slow_count");
        // The connection's inbound traffic must be suspended due to overwhelming traffic from client.
        // If the number of deferred reads did not increase and the testStreaming() above did not fail,
        // it probably means the client failed to produce enough amount of traffic.
        assertThat(InboundTrafficController.numDeferredReads()).isGreaterThan(oldNumDeferredReads);
    }

    @Test(timeout = 60000)
    public void testStreamingResponse() throws Exception {
        runStreamingResponseTest(false);
    }

    @Test(timeout = 120000)
    public void testStreamingResponseWithSlowClient() throws Exception {
        final int oldNumDeferredReads = InboundTrafficController.numDeferredReads();
        runStreamingResponseTest(true);
        // The connection's inbound traffic must be suspended due to overwhelming traffic from client.
        // If the number of deferred reads did not increase and the testStreaming() above did not fail,
        // it probably means the client failed to produce enough amount of traffic.
        assertThat(InboundTrafficController.numDeferredReads()).isGreaterThan(oldNumDeferredReads);
    }

    private static class CountingService extends AbstractHttpService {
        private final boolean slow;

        CountingService(boolean slow) {
            this.slow = slow;
        }

        @Override
        protected HttpResponse doPost(ServiceRequestContext ctx, HttpRequest req) {
            final CompletableFuture<HttpResponse> responseFuture = new CompletableFuture<>();
            final HttpResponse res = HttpResponse.from(responseFuture);
            req.subscribe(new HttpServerStreamingTest.StreamConsumer(ctx.eventLoop(), slow) {
                @Override
                public void onError(Throwable cause) {
                    responseFuture.complete(HttpResponse.of(INTERNAL_SERVER_ERROR, PLAIN_TEXT_UTF_8, Exceptions.traceText(cause)));
                }

                @Override
                public void onComplete() {
                    responseFuture.complete(HttpResponse.of(OK, PLAIN_TEXT_UTF_8, "%d", numReceivedBytes()));
                }
            });
            return res;
        }
    }

    private abstract static class StreamConsumer implements Subscriber<HttpObject> {
        private final ScheduledExecutorService executor;

        private final boolean slow;

        private Subscription subscription;

        private long numReceivedBytes;

        private int numReceivedChunks;

        protected StreamConsumer(ScheduledExecutorService executor, boolean slow) {
            this.executor = executor;
            this.slow = slow;
        }

        protected long numReceivedBytes() {
            return numReceivedBytes;
        }

        @Override
        public void onSubscribe(Subscription subscription) {
            this.subscription = subscription;
            subscription.request(1);
        }

        @Override
        public void onNext(HttpObject obj) {
            if (obj instanceof HttpData) {
                numReceivedBytes += length();
            }
            if ((numReceivedBytes) >= (((numReceivedChunks) + 1L) * (HttpServerStreamingTest.STREAMING_CONTENT_CHUNK_LENGTH))) {
                (numReceivedChunks)++;
                if (slow) {
                    // Add 1 second delay for every chunk received.
                    executor.schedule(() -> subscription.request(1), 1, TimeUnit.SECONDS);
                } else {
                    subscription.request(1);
                }
                HttpServerStreamingTest.logger.debug("{} bytes received", numReceivedBytes);
            } else {
                subscription.request(1);
            }
        }
    }
}

