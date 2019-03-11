/**
 * Copyright 2015 LINE Corporation
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


import HttpHeaderNames.ACCEPT;
import HttpHeaderNames.AUTHORITY;
import HttpHeaderNames.CACHE_CONTROL;
import HttpHeaderNames.CONTENT_ENCODING;
import HttpHeaderNames.USER_AGENT;
import HttpMethod.GET;
import HttpMethod.POST;
import HttpStatus.NOT_FOUND;
import HttpStatus.OK;
import MediaType.PLAIN_TEXT_UTF_8;
import com.linecorp.armeria.client.encoding.DeflateStreamDecoderFactory;
import com.linecorp.armeria.client.encoding.HttpDecodingClient;
import com.linecorp.armeria.client.endpoint.EndpointGroup;
import com.linecorp.armeria.common.AggregatedHttpMessage;
import com.linecorp.armeria.common.HttpData;
import com.linecorp.armeria.common.HttpHeaders;
import com.linecorp.armeria.common.HttpObject;
import com.linecorp.armeria.common.HttpRequest;
import com.linecorp.armeria.common.HttpRequestWriter;
import com.linecorp.armeria.common.HttpResponse;
import com.linecorp.armeria.common.HttpResponseWriter;
import com.linecorp.armeria.common.util.Exceptions;
import com.linecorp.armeria.server.AbstractHttpService;
import com.linecorp.armeria.server.ServerBuilder;
import com.linecorp.armeria.server.Service;
import com.linecorp.armeria.server.ServiceRequestContext;
import com.linecorp.armeria.server.SimpleDecoratingService;
import com.linecorp.armeria.server.encoding.HttpEncodingService;
import com.linecorp.armeria.testing.server.ServerRule;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.util.ReferenceCountUtil;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.IntFunction;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.reactivestreams.Subscription;

import static ClientFactory.DEFAULT;
import static HttpHeaderUtil.USER_AGENT;


public class HttpClientIntegrationTest {
    private static final AtomicReference<ByteBuf> releasedByteBuf = new AtomicReference<>();

    // Used to communicate with test when the response can't be used.
    private static final AtomicReference<Boolean> completed = new AtomicReference<>();

    private static final class PoolUnawareDecorator extends SimpleDecoratingService<HttpRequest, HttpResponse> {
        private PoolUnawareDecorator(Service<HttpRequest, HttpResponse> delegate) {
            super(delegate);
        }

        @Override
        public HttpResponse serve(ServiceRequestContext ctx, HttpRequest req) throws Exception {
            final HttpResponse res = delegate().serve(ctx, req);
            final HttpResponseWriter decorated = HttpResponse.streaming();
            res.subscribe(new org.reactivestreams.Subscriber<HttpObject>() {
                @Override
                public void onSubscribe(Subscription s) {
                    s.request(Long.MAX_VALUE);
                }

                @Override
                public void onNext(HttpObject httpObject) {
                    decorated.write(httpObject);
                }

                @Override
                public void onError(Throwable t) {
                    decorated.close(t);
                }

                @Override
                public void onComplete() {
                    decorated.close();
                }
            });
            return decorated;
        }
    }

    private static final class PoolAwareDecorator extends SimpleDecoratingService<HttpRequest, HttpResponse> {
        private PoolAwareDecorator(Service<HttpRequest, HttpResponse> delegate) {
            super(delegate);
        }

        @Override
        public HttpResponse serve(ServiceRequestContext ctx, HttpRequest req) throws Exception {
            final HttpResponse res = delegate().serve(ctx, req);
            final HttpResponseWriter decorated = HttpResponse.streaming();
            res.subscribe(new org.reactivestreams.Subscriber<HttpObject>() {
                @Override
                public void onSubscribe(Subscription s) {
                    s.request(Long.MAX_VALUE);
                }

                @Override
                public void onNext(HttpObject httpObject) {
                    if (httpObject instanceof ByteBufHolder) {
                        try {
                            decorated.write(HttpData.of(content()));
                        } finally {
                            ReferenceCountUtil.safeRelease(httpObject);
                        }
                    } else {
                        decorated.write(httpObject);
                    }
                }

                @Override
                public void onError(Throwable t) {
                    decorated.close(t);
                }

                @Override
                public void onComplete() {
                    decorated.close();
                }
            }, true);
            return decorated;
        }
    }

    private static class PooledContentService extends AbstractHttpService {
        @Override
        protected HttpResponse doGet(ServiceRequestContext ctx, HttpRequest req) throws Exception {
            final ByteBuf buf = ctx.alloc().buffer();
            buf.writeCharSequence("pooled content", StandardCharsets.UTF_8);
            HttpClientIntegrationTest.releasedByteBuf.set(buf);
            return HttpResponse.of(OK, PLAIN_TEXT_UTF_8, new com.linecorp.armeria.unsafe.ByteBufHttpData(buf, false));
        }
    }

    @ClassRule
    public static final ServerRule server = new ServerRule() {
        @Override
        protected void configure(ServerBuilder sb) throws Exception {
            sb.service("/httptestbody", new AbstractHttpService() {
                @Override
                protected HttpResponse doGet(ServiceRequestContext ctx, HttpRequest req) {
                    return doGetOrPost(req);
                }

                @Override
                protected HttpResponse doPost(ServiceRequestContext ctx, HttpRequest req) {
                    return doGetOrPost(req);
                }

                private HttpResponse doGetOrPost(HttpRequest req) {
                    final com.linecorp.armeria.common.MediaType contentType = req.contentType();
                    if (contentType != null) {
                        throw new IllegalArgumentException(("Serialization format is none, so content type should not be set: " + contentType));
                    }
                    final String accept = req.headers().get(ACCEPT);
                    if (!("utf-8".equals(accept))) {
                        throw new IllegalArgumentException(("Serialization format is none, so accept should not be overridden: " + accept));
                    }
                    return HttpResponse.from(req.aggregate().handle(( aReq, cause) -> {
                        if (cause != null) {
                            return HttpResponse.of(HttpStatus.INTERNAL_SERVER_ERROR, MediaType.PLAIN_TEXT_UTF_8, Exceptions.traceText(cause));
                        }
                        return HttpResponse.of(HttpHeaders.of(HttpStatus.OK).set(HttpHeaderNames.CACHE_CONTROL, "alwayscache"), HttpData.ofUtf8("METHOD: %s|ACCEPT: %s|BODY: %s", req.method().name(), accept, aReq.contentUtf8()));
                    }).exceptionally(CompletionActions::log));
                }
            });
            sb.service("/not200", new AbstractHttpService() {
                @Override
                protected HttpResponse doGet(ServiceRequestContext ctx, HttpRequest req) {
                    return HttpResponse.of(NOT_FOUND);
                }
            });
            sb.service("/useragent", new AbstractHttpService() {
                @Override
                protected HttpResponse doGet(ServiceRequestContext ctx, HttpRequest req) {
                    final String ua = req.headers().get(USER_AGENT, "undefined");
                    return HttpResponse.of(OK, PLAIN_TEXT_UTF_8, ua);
                }
            });
            sb.service("/authority", new AbstractHttpService() {
                @Override
                protected HttpResponse doGet(ServiceRequestContext ctx, HttpRequest req) {
                    final String ua = req.headers().get(AUTHORITY, "undefined");
                    return HttpResponse.of(OK, PLAIN_TEXT_UTF_8, ua);
                }
            });
            sb.service("/hello/world", new AbstractHttpService() {
                @Override
                protected HttpResponse doGet(ServiceRequestContext ctx, HttpRequest req) {
                    return HttpResponse.of(OK, PLAIN_TEXT_UTF_8, "success");
                }
            });
            sb.service("/encoding", new AbstractHttpService() {
                @Override
                protected HttpResponse doGet(ServiceRequestContext ctx, HttpRequest req) throws Exception {
                    return HttpResponse.of(HttpHeaders.of(OK), HttpData.ofUtf8("some content to compress "), HttpData.ofUtf8("more content to compress"));
                }
            }.decorate(HttpEncodingService.class));
            sb.service("/encoding-toosmall", new AbstractHttpService() {
                @Override
                protected HttpResponse doGet(ServiceRequestContext ctx, HttpRequest req) throws Exception {
                    return HttpResponse.of(OK, PLAIN_TEXT_UTF_8, "small content");
                }
            }.decorate(HttpEncodingService.class));
            sb.service("/pooled", new HttpClientIntegrationTest.PooledContentService());
            sb.service("/pooled-aware", decorate(HttpClientIntegrationTest.PoolAwareDecorator::new));
            sb.service("/pooled-unaware", decorate(HttpClientIntegrationTest.PoolUnawareDecorator::new));
            sb.service("/stream-closed", ( ctx, req) -> {
                ctx.setRequestTimeout(Duration.ZERO);
                final HttpResponseWriter res = HttpResponse.streaming();
                res.write(HttpHeaders.of(HttpStatus.OK));
                req.subscribe(new Subscriber<HttpObject>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        s.request(Long.MAX_VALUE);
                    }

                    @Override
                    public void onNext(HttpObject httpObject) {
                    }

                    @Override
                    public void onError(Throwable t) {
                        HttpClientIntegrationTest.completed.set(true);
                    }

                    @Override
                    public void onComplete() {
                    }
                }, ctx.eventLoop());
                return res;
            });
            sb.service("glob:/oneparam/**", ( ctx, req) -> {
                // The client was able to send a request with an escaped path param. Armeria servers always
                // decode the path so ctx.path == '/oneparam/foo/bar' here.
                if ((req.headers().path().equals("/oneparam/foo%2Fbar")) && (ctx.path().equals("/oneparam/foo/bar"))) {
                    return HttpResponse.of("routed");
                }
                return HttpResponse.of(HttpStatus.INTERNAL_SERVER_ERROR);
            });
        }
    };

    private static final ClientFactory clientFactory = DEFAULT;

    /**
     * When the content of a request is empty, the encoded request should never have 'content-length' or
     * 'transfer-encoding' header.
     */
    @Test
    public void testRequestNoBodyWithoutExtraHeaders() throws Exception {
        HttpClientIntegrationTest.testSocketOutput("/foo", ( port) -> ((((("GET /foo HTTP/1.1\r\n" + "host: 127.0.0.1:") + port) + "\r\n") + "user-agent: ") + (USER_AGENT)) + "\r\n\r\n");
    }

    @Test
    public void testRequestNoBody() throws Exception {
        final HttpClient client = HttpClient.of(HttpClientIntegrationTest.server.uri("/"));
        final AggregatedHttpMessage response = client.execute(HttpHeaders.of(GET, "/httptestbody").set(ACCEPT, "utf-8")).aggregate().get();
        Assert.assertEquals(OK, response.status());
        Assert.assertEquals("alwayscache", response.headers().get(CACHE_CONTROL));
        Assert.assertEquals("METHOD: GET|ACCEPT: utf-8|BODY: ", response.contentUtf8());
    }

    @Test
    public void testRequestWithBody() throws Exception {
        final HttpClient client = HttpClient.of(HttpClientIntegrationTest.server.uri("/"));
        final AggregatedHttpMessage response = client.execute(HttpHeaders.of(POST, "/httptestbody").set(ACCEPT, "utf-8"), "requestbody???").aggregate().get();
        Assert.assertEquals(OK, response.status());
        Assert.assertEquals("alwayscache", response.headers().get(CACHE_CONTROL));
        Assert.assertEquals("METHOD: POST|ACCEPT: utf-8|BODY: requestbody???", response.contentUtf8());
    }

    @Test
    public void testResolvedEndpointWithAlternateAuthority() throws Exception {
        final EndpointGroup group = new com.linecorp.armeria.client.endpoint.StaticEndpointGroup(Endpoint.of("localhost", HttpClientIntegrationTest.server.httpPort()).withIpAddr("127.0.0.1"));
        HttpClientIntegrationTest.testEndpointWithAlternateAuthority(group);
    }

    @Test
    public void testUnresolvedEndpointWithAlternateAuthority() throws Exception {
        final EndpointGroup group = new com.linecorp.armeria.client.endpoint.StaticEndpointGroup(Endpoint.of("localhost", HttpClientIntegrationTest.server.httpPort()));
        HttpClientIntegrationTest.testEndpointWithAlternateAuthority(group);
    }

    @Test
    public void testNot200() throws Exception {
        final HttpClient client = HttpClient.of(HttpClientIntegrationTest.server.uri("/"));
        final AggregatedHttpMessage response = client.get("/not200").aggregate().get();
        Assert.assertEquals(NOT_FOUND, response.status());
    }

    /**
     * :authority header should be overridden by ClientOption.HTTP_HEADER
     */
    @Test
    public void testAuthorityOverridableByClientOption() throws Exception {
        HttpClientIntegrationTest.testHeaderOverridableByClientOption("/authority", AUTHORITY, "foo:8080");
    }

    @Test
    public void testAuthorityOverridableByRequestHeader() throws Exception {
        HttpClientIntegrationTest.testHeaderOverridableByRequestHeader("/authority", AUTHORITY, "bar:8080");
    }

    /**
     * User-agent header should be overridden by ClientOption.HTTP_HEADER
     */
    @Test
    public void testUserAgentOverridableByClientOption() throws Exception {
        HttpClientIntegrationTest.testHeaderOverridableByClientOption("/useragent", USER_AGENT, "foo-agent");
    }

    @Test
    public void testUserAgentOverridableByRequestHeader() throws Exception {
        HttpClientIntegrationTest.testHeaderOverridableByRequestHeader("/useragent", USER_AGENT, "bar-agent");
    }

    @Test
    public void httpDecoding() throws Exception {
        final HttpClient client = new HttpClientBuilder(HttpClientIntegrationTest.server.uri("/")).factory(HttpClientIntegrationTest.clientFactory).decorator(HttpDecodingClient.newDecorator()).build();
        final AggregatedHttpMessage response = client.execute(HttpHeaders.of(GET, "/encoding")).aggregate().get();
        assertThat(response.headers().get(CONTENT_ENCODING)).isEqualTo("gzip");
        assertThat(response.contentUtf8()).isEqualTo("some content to compress more content to compress");
    }

    @Test
    public void httpDecoding_deflate() throws Exception {
        final HttpClient client = new HttpClientBuilder(HttpClientIntegrationTest.server.uri("/")).factory(HttpClientIntegrationTest.clientFactory).decorator(HttpDecodingClient.newDecorator(new DeflateStreamDecoderFactory())).build();
        final AggregatedHttpMessage response = client.execute(HttpHeaders.of(GET, "/encoding")).aggregate().get();
        assertThat(response.headers().get(CONTENT_ENCODING)).isEqualTo("deflate");
        assertThat(response.contentUtf8()).isEqualTo("some content to compress more content to compress");
    }

    @Test
    public void httpDecoding_noEncodingApplied() throws Exception {
        final HttpClient client = new HttpClientBuilder(HttpClientIntegrationTest.server.uri("/")).factory(HttpClientIntegrationTest.clientFactory).decorator(HttpDecodingClient.newDecorator(new DeflateStreamDecoderFactory())).build();
        final AggregatedHttpMessage response = client.execute(HttpHeaders.of(GET, "/encoding-toosmall")).aggregate().get();
        assertThat(response.headers().get(CONTENT_ENCODING)).isNull();
        assertThat(response.contentUtf8()).isEqualTo("small content");
    }

    @Test
    public void givenHttpClientUriPathAndRequestPath_whenGet_thenRequestToConcatenatedPath() throws Exception {
        final HttpClient client = HttpClient.of(HttpClientIntegrationTest.server.uri("/hello"));
        final AggregatedHttpMessage response = client.get("/world").aggregate().get();
        Assert.assertEquals("success", response.contentUtf8());
    }

    @Test
    public void givenRequestPath_whenGet_thenRequestToPath() throws Exception {
        final HttpClient client = HttpClient.of(HttpClientIntegrationTest.server.uri("/"));
        final AggregatedHttpMessage response = client.get("/hello/world").aggregate().get();
        Assert.assertEquals("success", response.contentUtf8());
    }

    @Test
    public void testPooledResponseDefaultSubscriber() throws Exception {
        final HttpClient client = HttpClient.of(HttpClientIntegrationTest.server.uri("/"));
        final AggregatedHttpMessage response = client.execute(HttpHeaders.of(GET, "/pooled")).aggregate().get();
        Assert.assertEquals(OK, response.status());
        assertThat(response.contentUtf8()).isEqualTo("pooled content");
        await().untilAsserted(() -> assertThat(releasedByteBuf.get().refCnt()).isZero());
    }

    @Test
    public void testPooledResponsePooledSubscriber() throws Exception {
        final HttpClient client = HttpClient.of(HttpClientIntegrationTest.server.uri("/"));
        final AggregatedHttpMessage response = client.execute(HttpHeaders.of(GET, "/pooled-aware")).aggregate().get();
        Assert.assertEquals(OK, response.status());
        assertThat(response.contentUtf8()).isEqualTo("pooled content");
        await().untilAsserted(() -> assertThat(releasedByteBuf.get().refCnt()).isZero());
    }

    @Test
    public void testUnpooledResponsePooledSubscriber() throws Exception {
        final HttpClient client = HttpClient.of(HttpClientIntegrationTest.server.uri("/"));
        final AggregatedHttpMessage response = client.execute(HttpHeaders.of(GET, "/pooled-unaware")).aggregate().get();
        Assert.assertEquals(OK, response.status());
        assertThat(response.contentUtf8()).isEqualTo("pooled content");
        await().untilAsserted(() -> assertThat(releasedByteBuf.get().refCnt()).isZero());
    }

    @Test
    public void testCloseClientFactory() throws Exception {
        final ClientFactory factory = new ClientFactoryBuilder().build();
        final HttpClient client = factory.newClient(("none+" + (HttpClientIntegrationTest.server.uri("/"))), HttpClient.class);
        final HttpRequestWriter req = HttpRequest.streaming(HttpHeaders.of(GET, "/stream-closed"));
        final HttpResponse res = client.execute(req);
        final AtomicReference<HttpObject> obj = new AtomicReference<>();
        res.subscribe(new org.reactivestreams.Subscriber<HttpObject>() {
            @Override
            public void onSubscribe(Subscription s) {
                s.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(HttpObject httpObject) {
                obj.set(httpObject);
            }

            @Override
            public void onError(Throwable t) {
            }

            @Override
            public void onComplete() {
            }
        });
        req.write(HttpData.ofUtf8("not finishing this stream, sorry."));
        await().untilAsserted(() -> assertThat(obj).hasValue(HttpHeaders.of(HttpStatus.OK)));
        factory.close();
        await().untilAsserted(() -> assertThat(completed).hasValue(true));
    }

    @Test
    public void testEscapedPathParam() throws Exception {
        final HttpClient client = HttpClient.of(HttpClientIntegrationTest.server.uri("/"));
        final AggregatedHttpMessage response = client.get("/oneparam/foo%2Fbar").aggregate().get();
        Assert.assertEquals("routed", response.contentUtf8());
    }
}

