/**
 * Copyright 2002-2018 the original author or authors.
 *
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
 */
package org.springframework.web.reactive.result.method.annotation;


import BackpressureStrategy.BUFFER;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import java.util.concurrent.CompletableFuture;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.ResolvableType;
import org.springframework.http.HttpEntity;
import org.springframework.http.RequestEntity;
import org.springframework.web.method.ResolvableMethod;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import rx.Observable;
import rx.RxReactiveStreams;
import rx.Single;


/**
 * Unit tests for {@link HttpEntityArgumentResolver}.When adding a test also
 * consider whether the logic under test is in a parent class, then see:
 * {@link MessageReaderArgumentResolverTests}.
 *
 * @author Rossen Stoyanchev
 * @author Sebastien Deleuze
 */
public class HttpEntityArgumentResolverTests {
    private final HttpEntityArgumentResolver resolver = createResolver();

    private final ResolvableMethod testMethod = ResolvableMethod.on(getClass()).named("handle").build();

    @Test
    public void supports() throws Exception {
        testSupports(this.testMethod.arg(httpEntityType(String.class)));
        testSupports(this.testMethod.arg(httpEntityType(Mono.class, String.class)));
        testSupports(this.testMethod.arg(httpEntityType(Single.class, String.class)));
        testSupports(this.testMethod.arg(httpEntityType(Single.class, String.class)));
        testSupports(this.testMethod.arg(httpEntityType(Maybe.class, String.class)));
        testSupports(this.testMethod.arg(httpEntityType(CompletableFuture.class, String.class)));
        testSupports(this.testMethod.arg(httpEntityType(Flux.class, String.class)));
        testSupports(this.testMethod.arg(httpEntityType(Observable.class, String.class)));
        testSupports(this.testMethod.arg(httpEntityType(Observable.class, String.class)));
        testSupports(this.testMethod.arg(httpEntityType(Flowable.class, String.class)));
        testSupports(this.testMethod.arg(forClassWithGenerics(RequestEntity.class, String.class)));
    }

    @Test
    public void doesNotSupport() throws Exception {
        Assert.assertFalse(this.resolver.supportsParameter(this.testMethod.arg(Mono.class, String.class)));
        Assert.assertFalse(this.resolver.supportsParameter(this.testMethod.arg(String.class)));
        try {
            this.resolver.supportsParameter(this.testMethod.arg(Mono.class, httpEntityType(String.class)));
            Assert.fail();
        } catch (IllegalStateException ex) {
            Assert.assertTrue(("Unexpected error message:\n" + (ex.getMessage())), ex.getMessage().startsWith("HttpEntityArgumentResolver doesn't support reactive type wrapper"));
        }
    }

    @Test
    public void emptyBodyWithString() throws Exception {
        ResolvableType type = httpEntityType(String.class);
        HttpEntity<Object> entity = resolveValueWithEmptyBody(type);
        Assert.assertNull(entity.getBody());
    }

    @Test
    public void emptyBodyWithMono() throws Exception {
        ResolvableType type = httpEntityType(Mono.class, String.class);
        HttpEntity<Mono<String>> entity = resolveValueWithEmptyBody(type);
        StepVerifier.create(entity.getBody()).expectNextCount(0).expectComplete().verify();
    }

    @Test
    public void emptyBodyWithFlux() throws Exception {
        ResolvableType type = httpEntityType(Flux.class, String.class);
        HttpEntity<Flux<String>> entity = resolveValueWithEmptyBody(type);
        StepVerifier.create(entity.getBody()).expectNextCount(0).expectComplete().verify();
    }

    @Test
    public void emptyBodyWithSingle() throws Exception {
        ResolvableType type = httpEntityType(Single.class, String.class);
        HttpEntity<Single<String>> entity = resolveValueWithEmptyBody(type);
        StepVerifier.create(RxReactiveStreams.toPublisher(entity.getBody())).expectNextCount(0).expectError(ServerWebInputException.class).verify();
    }

    @Test
    public void emptyBodyWithRxJava2Single() throws Exception {
        ResolvableType type = httpEntityType(Single.class, String.class);
        HttpEntity<io.reactivex.Single<String>> entity = resolveValueWithEmptyBody(type);
        StepVerifier.create(entity.getBody().toFlowable()).expectNextCount(0).expectError(ServerWebInputException.class).verify();
    }

    @Test
    public void emptyBodyWithRxJava2Maybe() throws Exception {
        ResolvableType type = httpEntityType(Maybe.class, String.class);
        HttpEntity<Maybe<String>> entity = resolveValueWithEmptyBody(type);
        StepVerifier.create(entity.getBody().toFlowable()).expectNextCount(0).expectComplete().verify();
    }

    @Test
    public void emptyBodyWithObservable() throws Exception {
        ResolvableType type = httpEntityType(Observable.class, String.class);
        HttpEntity<Observable<String>> entity = resolveValueWithEmptyBody(type);
        StepVerifier.create(RxReactiveStreams.toPublisher(entity.getBody())).expectNextCount(0).expectComplete().verify();
    }

    @Test
    public void emptyBodyWithRxJava2Observable() throws Exception {
        ResolvableType type = httpEntityType(Observable.class, String.class);
        HttpEntity<io.reactivex.Observable<String>> entity = resolveValueWithEmptyBody(type);
        StepVerifier.create(entity.getBody().toFlowable(BUFFER)).expectNextCount(0).expectComplete().verify();
    }

    @Test
    public void emptyBodyWithFlowable() throws Exception {
        ResolvableType type = httpEntityType(Flowable.class, String.class);
        HttpEntity<Flowable<String>> entity = resolveValueWithEmptyBody(type);
        StepVerifier.create(entity.getBody()).expectNextCount(0).expectComplete().verify();
    }

    @Test
    public void emptyBodyWithCompletableFuture() throws Exception {
        ResolvableType type = httpEntityType(CompletableFuture.class, String.class);
        HttpEntity<CompletableFuture<String>> entity = resolveValueWithEmptyBody(type);
        entity.getBody().whenComplete(( body, ex) -> {
            assertNull(body);
            assertNull(ex);
        });
    }

    @Test
    public void httpEntityWithStringBody() throws Exception {
        ServerWebExchange exchange = postExchange("line1");
        ResolvableType type = httpEntityType(String.class);
        HttpEntity<String> httpEntity = resolveValue(exchange, type);
        Assert.assertEquals(exchange.getRequest().getHeaders(), httpEntity.getHeaders());
        Assert.assertEquals("line1", httpEntity.getBody());
    }

    @Test
    public void httpEntityWithMonoBody() throws Exception {
        ServerWebExchange exchange = postExchange("line1");
        ResolvableType type = httpEntityType(Mono.class, String.class);
        HttpEntity<Mono<String>> httpEntity = resolveValue(exchange, type);
        Assert.assertEquals(exchange.getRequest().getHeaders(), httpEntity.getHeaders());
        Assert.assertEquals("line1", httpEntity.getBody().block());
    }

    @Test
    public void httpEntityWithSingleBody() throws Exception {
        ServerWebExchange exchange = postExchange("line1");
        ResolvableType type = httpEntityType(Single.class, String.class);
        HttpEntity<Single<String>> httpEntity = resolveValue(exchange, type);
        Assert.assertEquals(exchange.getRequest().getHeaders(), httpEntity.getHeaders());
        Assert.assertEquals("line1", httpEntity.getBody().toBlocking().value());
    }

    @Test
    public void httpEntityWithRxJava2SingleBody() throws Exception {
        ServerWebExchange exchange = postExchange("line1");
        ResolvableType type = httpEntityType(Single.class, String.class);
        HttpEntity<io.reactivex.Single<String>> httpEntity = resolveValue(exchange, type);
        Assert.assertEquals(exchange.getRequest().getHeaders(), httpEntity.getHeaders());
        Assert.assertEquals("line1", httpEntity.getBody().blockingGet());
    }

    @Test
    public void httpEntityWithRxJava2MaybeBody() throws Exception {
        ServerWebExchange exchange = postExchange("line1");
        ResolvableType type = httpEntityType(Maybe.class, String.class);
        HttpEntity<Maybe<String>> httpEntity = resolveValue(exchange, type);
        Assert.assertEquals(exchange.getRequest().getHeaders(), httpEntity.getHeaders());
        Assert.assertEquals("line1", httpEntity.getBody().blockingGet());
    }

    @Test
    public void httpEntityWithCompletableFutureBody() throws Exception {
        ServerWebExchange exchange = postExchange("line1");
        ResolvableType type = httpEntityType(CompletableFuture.class, String.class);
        HttpEntity<CompletableFuture<String>> httpEntity = resolveValue(exchange, type);
        Assert.assertEquals(exchange.getRequest().getHeaders(), httpEntity.getHeaders());
        Assert.assertEquals("line1", httpEntity.getBody().get());
    }

    @Test
    public void httpEntityWithFluxBody() throws Exception {
        ServerWebExchange exchange = postExchange("line1\nline2\nline3\n");
        ResolvableType type = httpEntityType(Flux.class, String.class);
        HttpEntity<Flux<String>> httpEntity = resolveValue(exchange, type);
        Assert.assertEquals(exchange.getRequest().getHeaders(), httpEntity.getHeaders());
        StepVerifier.create(httpEntity.getBody()).expectNext("line1").expectNext("line2").expectNext("line3").expectComplete().verify();
    }

    @Test
    public void requestEntity() throws Exception {
        ServerWebExchange exchange = postExchange("line1");
        ResolvableType type = forClassWithGenerics(RequestEntity.class, String.class);
        RequestEntity<String> requestEntity = resolveValue(exchange, type);
        Assert.assertEquals(exchange.getRequest().getMethod(), requestEntity.getMethod());
        Assert.assertEquals(exchange.getRequest().getURI(), requestEntity.getUrl());
        Assert.assertEquals(exchange.getRequest().getHeaders(), requestEntity.getHeaders());
        Assert.assertEquals("line1", requestEntity.getBody());
    }
}

