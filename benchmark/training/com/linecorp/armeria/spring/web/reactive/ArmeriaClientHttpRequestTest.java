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


import DataBufferFactoryWrapper.DEFAULT;
import HttpHeaderNames.ACCEPT;
import HttpHeaderNames.COOKIE;
import HttpHeaderNames.USER_AGENT;
import HttpMethod.GET;
import com.linecorp.armeria.client.HttpClient;
import com.linecorp.armeria.common.HttpData;
import com.linecorp.armeria.common.HttpHeaders;
import com.linecorp.armeria.common.HttpRequest;
import org.junit.Test;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpCookie;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


public class ArmeriaClientHttpRequestTest {
    private static final String TEST_PATH_AND_QUERY = "/index.html?q=1";

    static HttpClient httpClient;

    @Test
    public void completeWithoutBody() {
        final ArmeriaClientHttpRequest request = ArmeriaClientHttpRequestTest.request();
        request.setComplete().subscribe();
        // Wait until calling HttpClient#execute.
        await().until(() -> request.future().isDone());
        // Consume from Armeria HttpRequest.
        final HttpRequest httpRequest = request.request();
        assertThat(httpRequest).isNotNull();
        assertThat(httpRequest.completionFuture().isDone()).isFalse();
        // Completed when a subscriber subscribed.
        StepVerifier.create(httpRequest).expectComplete().verify();
        await().until(() -> httpRequest.completionFuture().isDone());
    }

    @Test
    public void writeWithPublisher() {
        final ArmeriaClientHttpRequest request = ArmeriaClientHttpRequestTest.request();
        final Flux<DataBuffer> body = Flux.just("a", "b", "c", "d", "e").map(String::getBytes).map(DEFAULT.delegate()::wrap);
        assertThat(request.getMethod()).isEqualTo(GET);
        request.getHeaders().add(USER_AGENT.toString(), "spring/armeria");
        request.getCookies().add("a", new HttpCookie("a", "1"));
        request.writeWith(body).then(Mono.defer(request::setComplete)).subscribe();
        // Wait until calling HttpClient#execute.
        await().until(() -> request.future().isDone());
        // Consume from Armeria HttpRequest.
        final HttpRequest httpRequest = request.request();
        assertThat(httpRequest).isNotNull();
        // Check the headers.
        final HttpHeaders headers = httpRequest.headers();
        assertThat(headers.method()).isEqualTo(com.linecorp.armeria.common.HttpMethod.GET);
        assertThat(headers.path()).isEqualTo(ArmeriaClientHttpRequestTest.TEST_PATH_AND_QUERY);
        assertThat(headers.get(ACCEPT)).isEqualTo("*/*");
        assertThat(headers.get(USER_AGENT)).isEqualTo("spring/armeria");
        assertThat(headers.get(COOKIE)).isEqualTo("a=1");
        assertThat(httpRequest.completionFuture().isDone()).isFalse();
        // Armeria HttpRequest produces http body only.
        final Flux<String> requestBody = Flux.from(httpRequest).cast(HttpData.class).map(HttpData::toStringUtf8);
        StepVerifier.create(requestBody, 1).expectNext("a").thenRequest(1).expectNext("b").thenRequest(1).expectNext("c").thenRequest(1).expectNext("d").thenRequest(1).expectNext("e").thenRequest(1).expectComplete().verify();
        await().until(() -> httpRequest.completionFuture().isDone());
    }

    @Test
    public void writeAndFlushWithMultiplePublisher() {
        final ArmeriaClientHttpRequest request = ArmeriaClientHttpRequestTest.request();
        final Flux<Flux<DataBuffer>> body = Flux.just(Flux.just("a", "b", "c", "d", "e").map(String::getBytes).map(DEFAULT.delegate()::wrap), Flux.just("1", "2", "3", "4", "5").map(String::getBytes).map(DEFAULT.delegate()::wrap));
        request.writeAndFlushWith(body).then(Mono.defer(request::setComplete)).subscribe();
        // Wait until calling HttpClient#execute.
        await().until(() -> request.future().isDone());
        // Consume from Armeria HttpRequest.
        final HttpRequest httpRequest = request.request();
        assertThat(httpRequest).isNotNull();
        // Check the headers.
        final HttpHeaders headers = httpRequest.headers();
        assertThat(headers.method()).isEqualTo(com.linecorp.armeria.common.HttpMethod.GET);
        assertThat(headers.path()).isEqualTo(ArmeriaClientHttpRequestTest.TEST_PATH_AND_QUERY);
        assertThat(headers.get(ACCEPT)).isEqualTo("*/*");
        // Armeria HttpRequest produces http body only.
        final Flux<String> requestBody = Flux.from(httpRequest).cast(HttpData.class).map(HttpData::toStringUtf8);
        StepVerifier.create(requestBody, 1).expectNext("a").thenRequest(1).expectNext("b").thenRequest(1).expectNext("c").thenRequest(1).expectNext("d").thenRequest(1).expectNext("e").thenRequest(1).expectNext("1").thenRequest(1).expectNext("2").thenRequest(1).expectNext("3").thenRequest(1).expectNext("4").thenRequest(1).expectNext("5").thenRequest(1).expectComplete().verify();
        await().until(() -> httpRequest.completionFuture().isDone());
    }
}

