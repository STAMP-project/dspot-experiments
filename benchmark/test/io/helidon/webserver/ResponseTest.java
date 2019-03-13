/**
 * Copyright (c) 2018, 2019 Oracle and/or its affiliates. All rights reserved.
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
package io.helidon.webserver;


import Flow.Subscription;
import Http.Header.CONTENT_TYPE;
import Http.ResponseStatus;
import MediaType.APPLICATION_JSON;
import MediaType.TEXT_HTML;
import MediaType.TEXT_PLAIN;
import io.helidon.common.http.DataChunk;
import io.helidon.common.http.Http;
import io.helidon.common.reactive.Flow;
import io.helidon.common.reactive.ReactiveStreamsAdapter;
import io.opentracing.SpanContext;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;


/**
 * Tests {@link Response}.
 */
public class ResponseTest {
    @Test
    public void headersAreClosedFirst() throws Exception {
        StringBuffer sb = new StringBuffer();
        ResponseTest.NoOpBareResponse br = new ResponseTest.NoOpBareResponse(sb);
        // Close all
        Response response = new ResponseTest.ResponseImpl(null, br);
        ResponseTest.close(response);
        MatcherAssert.assertThat(sb.toString(), CoreMatchers.is("h200c"));
        // Close first headers and then al
        sb.setLength(0);
        response = new ResponseTest.ResponseImpl(null, br);
        response.status(300);
        response.headers().send().toCompletableFuture().get();
        MatcherAssert.assertThat(sb.toString(), CoreMatchers.is("h300"));
        ResponseTest.close(response);
        MatcherAssert.assertThat(sb.toString(), CoreMatchers.is("h300c"));
    }

    @Test
    public void headersAreCaseInsensitive() throws Exception {
        StringBuffer sb = new StringBuffer();
        ResponseTest.NoOpBareResponse br = new ResponseTest.NoOpBareResponse(sb);
        Response response = new ResponseTest.ResponseImpl(null, br);
        ResponseHeaders headers = response.headers();
        headers.addCookie("cookie1", "cookie-value-1");
        headers.addCookie("cookie2", "cookie-value-2");
        headers.add("Header", "hv1");
        headers.add("header", "hv2");
        headers.add("heaDer", "hv3");
        ResponseTest.assertHeaders(headers, "Set-Cookie", "cookie1=cookie-value-1", "cookie2=cookie-value-2");
        ResponseTest.assertHeadersToMap(headers, "Set-Cookie", "cookie1=cookie-value-1", "cookie2=cookie-value-2");
        ResponseTest.assertHeaders(headers, "set-cookie", "cookie1=cookie-value-1", "cookie2=cookie-value-2");
        ResponseTest.assertHeadersToMap(headers, "set-cookie", "cookie1=cookie-value-1", "cookie2=cookie-value-2");
        ResponseTest.assertHeaders(headers, "SET-CooKIE", "cookie1=cookie-value-1", "cookie2=cookie-value-2");
        ResponseTest.assertHeadersToMap(headers, "SET-CooKIE", "cookie1=cookie-value-1", "cookie2=cookie-value-2");
        ResponseTest.assertHeaders(headers, "header", "hv1", "hv2", "hv3");
        ResponseTest.assertHeadersToMap(headers, "header", "hv1", "hv2", "hv3");
        ResponseTest.assertHeaders(headers, "Header", "hv1", "hv2", "hv3");
        ResponseTest.assertHeadersToMap(headers, "Header", "hv1", "hv2", "hv3");
        ResponseTest.assertHeaders(headers, "HEADer", "hv1", "hv2", "hv3");
        ResponseTest.assertHeadersToMap(headers, "HEADer", "hv1", "hv2", "hv3");
    }

    @Test
    public void classRelatedWriters() throws Exception {
        StringBuilder sb = new StringBuilder();
        Response response = new ResponseTest.ResponseImpl(null, new ResponseTest.NoOpBareResponse(null));
        MatcherAssert.assertThat(response.createPublisherUsingWriter("foo"), CoreMatchers.notNullValue());// Default

        MatcherAssert.assertThat(response.createPublisherUsingWriter("foo".getBytes()), CoreMatchers.notNullValue());// Default

        MatcherAssert.assertThat(response.createPublisherUsingWriter(Duration.of(1, ChronoUnit.MINUTES)), CoreMatchers.nullValue());
        response.registerWriter(CharSequence.class, ( o) -> {
            sb.append("1");
            return ReactiveStreamsAdapter.publisherToFlow(Mono.empty());
        });
        MatcherAssert.assertThat(response.createPublisherUsingWriter("foo"), CoreMatchers.notNullValue());
        MatcherAssert.assertThat(sb.toString(), CoreMatchers.is("1"));
        sb.setLength(0);
        MatcherAssert.assertThat(response.createPublisherUsingWriter(null), CoreMatchers.notNullValue());
        MatcherAssert.assertThat(sb.toString(), CoreMatchers.is(""));
        sb.setLength(0);
        response.registerWriter(String.class, ( o) -> {
            sb.append("2");
            return ReactiveStreamsAdapter.publisherToFlow(Mono.empty());
        });
        MatcherAssert.assertThat(response.createPublisherUsingWriter("foo"), CoreMatchers.notNullValue());
        MatcherAssert.assertThat(sb.toString(), CoreMatchers.is("2"));
        sb.setLength(0);
        MatcherAssert.assertThat(response.createPublisherUsingWriter(new StringBuilder()), CoreMatchers.notNullValue());
        MatcherAssert.assertThat(sb.toString(), CoreMatchers.is("1"));
        sb.setLength(0);
        response.registerWriter(((Class<Object>) (null)), ( o) -> {
            sb.append("3");
            return ReactiveStreamsAdapter.publisherToFlow(Mono.empty());
        });
        MatcherAssert.assertThat(response.createPublisherUsingWriter(1), CoreMatchers.notNullValue());
        MatcherAssert.assertThat(sb.toString(), CoreMatchers.is("3"));
    }

    @Test
    public void writerByPredicate() throws Exception {
        StringBuilder sb = new StringBuilder();
        Response response = new ResponseTest.ResponseImpl(null, new ResponseTest.NoOpBareResponse(null));
        response.registerWriter(( o) -> "1".equals(String.valueOf(o)), ( o) -> {
            sb.append("1");
            return ReactiveStreamsAdapter.publisherToFlow(Mono.empty());
        });
        response.registerWriter(( o) -> "2".equals(String.valueOf(o)), ( o) -> {
            sb.append("2");
            return ReactiveStreamsAdapter.publisherToFlow(Mono.empty());
        });
        MatcherAssert.assertThat(response.createPublisherUsingWriter(1), CoreMatchers.notNullValue());
        MatcherAssert.assertThat(sb.toString(), CoreMatchers.is("1"));
        sb.setLength(0);
        MatcherAssert.assertThat(response.createPublisherUsingWriter(2), CoreMatchers.notNullValue());
        MatcherAssert.assertThat(sb.toString(), CoreMatchers.is("2"));
        sb.setLength(0);
        MatcherAssert.assertThat(response.createPublisherUsingWriter(3), CoreMatchers.nullValue());
        MatcherAssert.assertThat(sb.toString(), CoreMatchers.is(""));
    }

    @Test
    public void writerWithMediaType() throws Exception {
        StringBuilder sb = new StringBuilder();
        Response response = new ResponseTest.ResponseImpl(null, new ResponseTest.NoOpBareResponse(null));
        response.registerWriter(CharSequence.class, TEXT_PLAIN, ( o) -> {
            sb.append("A");
            return ReactiveStreamsAdapter.publisherToFlow(Mono.empty());
        });
        response.registerWriter(( o) -> o instanceof Number, APPLICATION_JSON, ( o) -> {
            sb.append("B");
            return ReactiveStreamsAdapter.publisherToFlow(Mono.empty());
        });
        MatcherAssert.assertThat(response.createPublisherUsingWriter("foo"), CoreMatchers.notNullValue());
        MatcherAssert.assertThat(sb.toString(), CoreMatchers.is("A"));
        MatcherAssert.assertThat(response.headers().contentType().orElse(null), CoreMatchers.is(TEXT_PLAIN));
        sb.setLength(0);
        response.headers().remove(CONTENT_TYPE);
        MatcherAssert.assertThat(response.createPublisherUsingWriter(1), CoreMatchers.notNullValue());
        MatcherAssert.assertThat(sb.toString(), CoreMatchers.is("B"));
        MatcherAssert.assertThat(response.headers().contentType().orElse(null), CoreMatchers.is(APPLICATION_JSON));
        sb.setLength(0);
        response.headers().put(CONTENT_TYPE, APPLICATION_JSON.toString());
        MatcherAssert.assertThat(response.createPublisherUsingWriter(1), CoreMatchers.notNullValue());
        MatcherAssert.assertThat(sb.toString(), CoreMatchers.is("B"));
        MatcherAssert.assertThat(response.headers().contentType().orElse(null), CoreMatchers.is(APPLICATION_JSON));
        sb.setLength(0);
        response.headers().put(CONTENT_TYPE, TEXT_HTML.toString());
        MatcherAssert.assertThat(response.createPublisherUsingWriter(1), CoreMatchers.nullValue());
        MatcherAssert.assertThat(sb.toString(), CoreMatchers.is(""));
        MatcherAssert.assertThat(response.headers().contentType().orElse(null), CoreMatchers.is(TEXT_HTML));
    }

    @Test
    public void filters() throws Exception {
        StringBuilder sb = new StringBuilder();
        Response response = new ResponseTest.ResponseImpl(null, new ResponseTest.NoOpBareResponse(null));
        response.registerFilter(( p) -> {
            sb.append("A");
            return p;
        });
        response.registerFilter(( p) -> {
            sb.append("B");
            return null;
        });
        response.registerFilter(( p) -> {
            sb.append("C");
            return p;
        });
        MatcherAssert.assertThat(response.applyFilters(ReactiveStreamsAdapter.publisherToFlow(Mono.empty()), null), CoreMatchers.notNullValue());
        MatcherAssert.assertThat(sb.toString(), CoreMatchers.is("ABC"));
    }

    static class ResponseImpl extends Response {
        public ResponseImpl(WebServer webServer, BareResponse bareResponse) {
            super(webServer, bareResponse);
        }

        @Override
        SpanContext spanContext() {
            return null;
        }
    }

    static class NoOpBareResponse implements BareResponse {
        private final StringBuffer sb;

        private final CompletableFuture<BareResponse> closeFuture = new CompletableFuture<>();

        NoOpBareResponse(StringBuffer sb) {
            this.sb = (sb == null) ? new StringBuffer() : sb;
        }

        @Override
        public void writeStatusAndHeaders(Http.ResponseStatus status, Map<String, List<String>> headers) {
            sb.append("h").append(status.code());
        }

        @Override
        public CompletionStage<BareResponse> whenCompleted() {
            return closeFuture;
        }

        @Override
        public CompletionStage<BareResponse> whenHeadersCompleted() {
            return closeFuture;
        }

        @Override
        public void onSubscribe(Flow.Subscription subscription) {
            subscription.request(Long.MAX_VALUE);
        }

        @Override
        public void onNext(DataChunk data) {
            sb.append("d");
        }

        @Override
        public void onError(Throwable thr) {
            sb.append("e");
        }

        @Override
        public void onComplete() {
            sb.append("c");
            closeFuture.complete(this);
        }

        @Override
        public long requestId() {
            return 0;
        }
    }
}

