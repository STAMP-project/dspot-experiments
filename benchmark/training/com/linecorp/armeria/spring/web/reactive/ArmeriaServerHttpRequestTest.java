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
package com.linecorp.armeria.spring.web.reactive;


import HttpHeaderNames.COOKIE;
import HttpMethod.POST;
import com.linecorp.armeria.common.HttpHeaders;
import com.linecorp.armeria.common.HttpRequest;
import com.linecorp.armeria.common.stream.CancelledSubscriptionException;
import com.linecorp.armeria.server.ServiceRequestContext;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.junit.Test;
import org.springframework.http.HttpCookie;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;


public class ArmeriaServerHttpRequestTest {
    @Test
    public void readBodyStream() throws Exception {
        final HttpRequest httpRequest = HttpRequest.of(HttpHeaders.of(POST, "/"), Flux.just("a", "b", "c", "d", "e").map(HttpData::ofUtf8));
        final ServiceRequestContext ctx = ArmeriaServerHttpRequestTest.newRequestContext(httpRequest);
        final ArmeriaServerHttpRequest req = ArmeriaServerHttpRequestTest.request(ctx);
        assertThat(req.getMethodValue()).isEqualTo(POST.name());
        assertThat(req.<Object>getNativeRequest()).isInstanceOf(HttpRequest.class).isEqualTo(httpRequest);
        assertThat(httpRequest.completionFuture().isDone()).isFalse();
        final Flux<String> body = req.getBody().map(TestUtil::bufferToString);
        StepVerifier.create(body, 1).expectNext("a").thenRequest(1).expectNext("b").thenRequest(1).expectNext("c").thenRequest(1).expectNext("d").thenRequest(1).expectNext("e").thenRequest(1).expectComplete().verify();
        await().until(() -> httpRequest.completionFuture().isDone());
    }

    @Test
    public void getCookies() {
        final HttpRequest httpRequest = HttpRequest.of(HttpHeaders.of(POST, "/").add(COOKIE, "a=1;b=2"));
        final ServiceRequestContext ctx = ArmeriaServerHttpRequestTest.newRequestContext(httpRequest);
        final ArmeriaServerHttpRequest req = ArmeriaServerHttpRequestTest.request(ctx);
        // Check cached.
        final MultiValueMap<String, HttpCookie> cookie1 = req.getCookies();
        final MultiValueMap<String, HttpCookie> cookie2 = req.getCookies();
        assertThat((cookie1 == cookie2)).isTrue();
        assertThat(cookie1.get("a")).containsExactly(new HttpCookie("a", "1"));
        assertThat(cookie1.get("b")).containsExactly(new HttpCookie("b", "2"));
    }

    @Test
    public void cancel() {
        final HttpRequest httpRequest = HttpRequest.of(HttpHeaders.of(POST, "/"), Flux.just("a", "b", "c", "d", "e").map(HttpData::ofUtf8));
        final ServiceRequestContext ctx = ArmeriaServerHttpRequestTest.newRequestContext(httpRequest);
        final ArmeriaServerHttpRequest req = ArmeriaServerHttpRequestTest.request(ctx);
        assertThat(httpRequest.completionFuture().isDone()).isFalse();
        final Flux<String> body = req.getBody().map(TestUtil::bufferToString);
        StepVerifier.create(body, 1).expectNext("a").thenRequest(1).expectNext("b").thenRequest(1).thenCancel().verify();
        final CompletableFuture<Void> f = httpRequest.completionFuture();
        assertThat(f.isDone()).isTrue();
        assertThat(f.isCompletedExceptionally()).isTrue();
        assertThatThrownBy(f::get).isInstanceOf(ExecutionException.class).hasCauseInstanceOf(CancelledSubscriptionException.class);
    }
}

