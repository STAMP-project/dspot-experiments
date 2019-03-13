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


import io.reactivex.Maybe;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.method.ResolvableMethod;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import rx.Observable;
import rx.RxReactiveStreams;
import rx.Single;


/**
 * Unit tests for {@link RequestBodyArgumentResolver}. When adding a test also
 * consider whether the logic under test is in a parent class, then see:
 * {@link MessageReaderArgumentResolverTests}.
 *
 * @author Rossen Stoyanchev
 */
public class RequestBodyArgumentResolverTests {
    private RequestBodyArgumentResolver resolver;

    private ResolvableMethod testMethod = ResolvableMethod.on(getClass()).named("handle").build();

    @Test
    public void supports() throws Exception {
        MethodParameter param;
        param = this.testMethod.annot(requestBody()).arg(Mono.class, String.class);
        Assert.assertTrue(this.resolver.supportsParameter(param));
        param = this.testMethod.annotNotPresent(RequestBody.class).arg(String.class);
        Assert.assertFalse(this.resolver.supportsParameter(param));
    }

    @Test
    public void stringBody() throws Exception {
        String body = "line1";
        MethodParameter param = this.testMethod.annot(requestBody()).arg(String.class);
        String value = resolveValue(param, body);
        Assert.assertEquals(body, value);
    }

    @Test(expected = ServerWebInputException.class)
    public void emptyBodyWithString() throws Exception {
        MethodParameter param = this.testMethod.annot(requestBody()).arg(String.class);
        resolveValueWithEmptyBody(param);
    }

    @Test
    public void emptyBodyWithStringNotRequired() throws Exception {
        MethodParameter param = this.testMethod.annot(requestBody().notRequired()).arg(String.class);
        String body = resolveValueWithEmptyBody(param);
        Assert.assertNull(body);
    }

    // SPR-15758
    @Test
    public void emptyBodyWithoutContentType() throws Exception {
        MethodParameter param = this.testMethod.annot(requestBody().notRequired()).arg(Map.class);
        String body = resolveValueWithEmptyBody(param);
        Assert.assertNull(body);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void emptyBodyWithMono() throws Exception {
        MethodParameter param = this.testMethod.annot(requestBody()).arg(Mono.class, String.class);
        StepVerifier.create(((Mono<Void>) (resolveValueWithEmptyBody(param)))).expectNextCount(0).expectError(ServerWebInputException.class).verify();
        param = this.testMethod.annot(requestBody().notRequired()).arg(Mono.class, String.class);
        StepVerifier.create(((Mono<Void>) (resolveValueWithEmptyBody(param)))).expectNextCount(0).expectComplete().verify();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void emptyBodyWithFlux() throws Exception {
        MethodParameter param = this.testMethod.annot(requestBody()).arg(Flux.class, String.class);
        StepVerifier.create(((Flux<Void>) (resolveValueWithEmptyBody(param)))).expectNextCount(0).expectError(ServerWebInputException.class).verify();
        param = this.testMethod.annot(requestBody().notRequired()).arg(Flux.class, String.class);
        StepVerifier.create(((Flux<Void>) (resolveValueWithEmptyBody(param)))).expectNextCount(0).expectComplete().verify();
    }

    @Test
    public void emptyBodyWithSingle() throws Exception {
        MethodParameter param = this.testMethod.annot(requestBody()).arg(Single.class, String.class);
        Single<String> single = resolveValueWithEmptyBody(param);
        StepVerifier.create(RxReactiveStreams.toPublisher(single)).expectNextCount(0).expectError(ServerWebInputException.class).verify();
        param = this.testMethod.annot(requestBody().notRequired()).arg(Single.class, String.class);
        single = resolveValueWithEmptyBody(param);
        StepVerifier.create(RxReactiveStreams.toPublisher(single)).expectNextCount(0).expectError(ServerWebInputException.class).verify();
    }

    @Test
    public void emptyBodyWithMaybe() throws Exception {
        MethodParameter param = this.testMethod.annot(requestBody()).arg(Maybe.class, String.class);
        Maybe<String> maybe = resolveValueWithEmptyBody(param);
        StepVerifier.create(maybe.toFlowable()).expectNextCount(0).expectError(ServerWebInputException.class).verify();
        param = this.testMethod.annot(requestBody().notRequired()).arg(Maybe.class, String.class);
        maybe = resolveValueWithEmptyBody(param);
        StepVerifier.create(maybe.toFlowable()).expectNextCount(0).expectComplete().verify();
    }

    @Test
    public void emptyBodyWithObservable() throws Exception {
        MethodParameter param = this.testMethod.annot(requestBody()).arg(Observable.class, String.class);
        Observable<String> observable = resolveValueWithEmptyBody(param);
        StepVerifier.create(RxReactiveStreams.toPublisher(observable)).expectNextCount(0).expectError(ServerWebInputException.class).verify();
        param = this.testMethod.annot(requestBody().notRequired()).arg(Observable.class, String.class);
        observable = resolveValueWithEmptyBody(param);
        StepVerifier.create(RxReactiveStreams.toPublisher(observable)).expectNextCount(0).expectComplete().verify();
    }

    @Test
    public void emptyBodyWithCompletableFuture() throws Exception {
        MethodParameter param = this.testMethod.annot(requestBody()).arg(CompletableFuture.class, String.class);
        CompletableFuture<String> future = resolveValueWithEmptyBody(param);
        future.whenComplete(( text, ex) -> {
            Assert.assertNull(text);
            Assert.assertNotNull(ex);
        });
        param = this.testMethod.annot(requestBody().notRequired()).arg(CompletableFuture.class, String.class);
        future = resolveValueWithEmptyBody(param);
        future.whenComplete(( text, ex) -> {
            Assert.assertNotNull(text);
            Assert.assertNull(ex);
        });
    }
}

