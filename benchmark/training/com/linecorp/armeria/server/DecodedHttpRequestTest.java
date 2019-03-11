/**
 * Copyright 2019 LINE Corporation
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


import HttpMethod.GET;
import MediaType.PLAIN_TEXT_UTF_8;
import com.linecorp.armeria.common.HttpData;
import com.linecorp.armeria.common.HttpHeaders;
import com.linecorp.armeria.common.HttpObject;
import com.linecorp.armeria.common.HttpRequest;
import com.linecorp.armeria.testing.common.EventLoopRule;
import io.netty.util.ReferenceCountUtil;
import java.util.List;
import org.junit.ClassRule;
import org.junit.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;


public class DecodedHttpRequestTest {
    @ClassRule
    public static final EventLoopRule eventLoop = new EventLoopRule();

    @Test
    public void dataOnly() throws Exception {
        final DecodedHttpRequest req = DecodedHttpRequestTest.decodedHttpRequest();
        assertThat(req.tryWrite(HttpData.ofUtf8("foo"))).isTrue();
        req.close();
        final List<HttpObject> drained = req.drainAll().join();
        assertThat(drained).containsExactly(HttpData.ofUtf8("foo"));
    }

    @Test
    public void trailersOnly() throws Exception {
        final DecodedHttpRequest req = DecodedHttpRequestTest.decodedHttpRequest();
        assertThat(req.tryWrite(HttpHeaders.of(com.linecorp.armeria.common.HttpHeaderNames.of("bar"), "baz"))).isTrue();
        req.close();
        final List<HttpObject> drained = req.drainAll().join();
        assertThat(drained).containsExactly(HttpHeaders.of(com.linecorp.armeria.common.HttpHeaderNames.of("bar"), "baz"));
    }

    @Test
    public void dataIsIgnoreAfterTrailers() throws Exception {
        final DecodedHttpRequest req = DecodedHttpRequestTest.decodedHttpRequest();
        assertThat(req.tryWrite(HttpHeaders.of(com.linecorp.armeria.common.HttpHeaderNames.of("bar"), "baz"))).isTrue();
        assertThat(req.tryWrite(HttpData.ofUtf8("foo"))).isFalse();
        req.close();
        final List<HttpObject> drained = req.drainAll().join();
        assertThat(drained).containsExactly(HttpHeaders.of(com.linecorp.armeria.common.HttpHeaderNames.of("bar"), "baz"));
    }

    @Test
    public void splitTrailersIsIgnored() throws Exception {
        final DecodedHttpRequest req = DecodedHttpRequestTest.decodedHttpRequest();
        assertThat(req.tryWrite(HttpHeaders.of(com.linecorp.armeria.common.HttpHeaderNames.of("bar"), "baz"))).isTrue();
        assertThat(req.tryWrite(HttpHeaders.of(com.linecorp.armeria.common.HttpHeaderNames.of("qux"), "quux"))).isFalse();
        req.close();
        final List<HttpObject> drained = req.drainAll().join();
        assertThat(drained).containsExactly(HttpHeaders.of(com.linecorp.armeria.common.HttpHeaderNames.of("bar"), "baz"));
    }

    @Test
    public void splitTrailersAfterDataIsIgnored() throws Exception {
        final DecodedHttpRequest req = DecodedHttpRequestTest.decodedHttpRequest();
        assertThat(req.tryWrite(HttpData.ofUtf8("foo"))).isTrue();
        assertThat(req.tryWrite(HttpHeaders.of(com.linecorp.armeria.common.HttpHeaderNames.of("bar"), "baz"))).isTrue();
        assertThat(req.tryWrite(HttpHeaders.of(com.linecorp.armeria.common.HttpHeaderNames.of("qux"), "quux"))).isFalse();
        req.close();
        final List<HttpObject> drained = req.drainAll().join();
        assertThat(drained).containsExactly(HttpData.ofUtf8("foo"), HttpHeaders.of(com.linecorp.armeria.common.HttpHeaderNames.of("bar"), "baz"));
    }

    @Test
    public void contentPreview() {
        final HttpHeaders headers = HttpHeaders.of(GET, "/").contentType(PLAIN_TEXT_UTF_8);
        final ServiceRequestContext sctx = ServiceRequestContextBuilder.of(HttpRequest.of(headers)).serverConfigurator(( sb) -> sb.contentPreview(100)).build();
        final DecodedHttpRequest req = DecodedHttpRequestTest.decodedHttpRequest(headers, sctx);
        req.completionFuture().handle(( ret, cause) -> {
            sctx.logBuilder().endRequest();
            return null;
        });
        req.subscribe(new DecodedHttpRequestTest.ImmediateReleaseSubscriber());
        assertThat(req.tryWrite(new com.linecorp.armeria.unsafe.ByteBufHttpData(DecodedHttpRequestTest.newBuffer("hello"), false))).isTrue();
        req.close();
        await().untilAsserted(() -> assertThat(sctx.log().requestContentPreview()).isEqualTo("hello"));
    }

    private static class ImmediateReleaseSubscriber implements Subscriber<HttpObject> {
        @Override
        public void onSubscribe(Subscription s) {
            s.request(Integer.MAX_VALUE);
        }

        @Override
        public void onNext(HttpObject obj) {
            ReferenceCountUtil.safeRelease(obj);
        }

        @Override
        public void onError(Throwable t) {
        }

        @Override
        public void onComplete() {
        }
    }
}

