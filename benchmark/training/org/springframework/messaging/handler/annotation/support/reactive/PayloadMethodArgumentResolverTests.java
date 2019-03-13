/**
 * Copyright 2002-2019 the original author or authors.
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
package org.springframework.messaging.handler.annotation.support.reactive;


import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.Decoder;
import org.springframework.lang.Nullable;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.messaging.handler.invocation.ResolvableMethod;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


/**
 * Unit tests for {@link PayloadMethodArgumentResolver}.
 *
 * @author Rossen Stoyanchev
 */
public class PayloadMethodArgumentResolverTests {
    private final List<Decoder<?>> decoders = new ArrayList<>();

    private final ResolvableMethod testMethod = ResolvableMethod.on(getClass()).named("handle").build();

    @Test
    public void supportsParameter() {
        boolean useDefaultResolution = true;
        PayloadMethodArgumentResolver resolver = createResolver(null, useDefaultResolution);
        Assert.assertTrue(resolver.supportsParameter(this.testMethod.annotPresent(Payload.class).arg()));
        Assert.assertTrue(resolver.supportsParameter(this.testMethod.annotNotPresent(Payload.class).arg(String.class)));
        useDefaultResolution = false;
        resolver = createResolver(null, useDefaultResolution);
        Assert.assertTrue(resolver.supportsParameter(this.testMethod.annotPresent(Payload.class).arg()));
        Assert.assertFalse(resolver.supportsParameter(this.testMethod.annotNotPresent(Payload.class).arg(String.class)));
    }

    @Test
    public void emptyBodyWhenRequired() {
        MethodParameter param = this.testMethod.arg(ResolvableType.forClassWithGenerics(Mono.class, String.class));
        Mono<Object> mono = resolveValue(param, Mono.empty(), null);
        StepVerifier.create(mono).consumeErrorWith(( ex) -> {
            assertEquals(.class, ex.getClass());
            assertTrue(ex.getMessage(), ex.getMessage().contains("Payload content is missing"));
        }).verify();
    }

    @Test
    public void emptyBodyWhenNotRequired() {
        MethodParameter param = this.testMethod.annotPresent(Payload.class).arg();
        Assert.assertNull(resolveValue(param, Mono.empty(), null));
    }

    @Test
    public void stringMono() {
        String body = "foo";
        MethodParameter param = this.testMethod.arg(ResolvableType.forClassWithGenerics(Mono.class, String.class));
        Mono<Object> mono = resolveValue(param, Mono.delay(Duration.ofMillis(10)).map(( aLong) -> toDataBuffer(body)), null);
        Assert.assertEquals(body, mono.block());
    }

    @Test
    public void stringFlux() {
        List<String> body = Arrays.asList("foo", "bar");
        ResolvableType type = ResolvableType.forClassWithGenerics(Flux.class, String.class);
        MethodParameter param = this.testMethod.arg(type);
        Flux<Object> flux = resolveValue(param, Flux.fromIterable(body).delayElements(Duration.ofMillis(10)).map(this::toDataBuffer), null);
        Assert.assertEquals(body, flux.collectList().block());
    }

    @Test
    public void string() {
        String body = "foo";
        MethodParameter param = this.testMethod.annotNotPresent(Payload.class).arg(String.class);
        Object value = resolveValue(param, Mono.just(toDataBuffer(body)), null);
        Assert.assertEquals(body, value);
    }

    @Test
    public void validateStringMono() {
        ResolvableType type = ResolvableType.forClassWithGenerics(Mono.class, String.class);
        MethodParameter param = this.testMethod.arg(type);
        Mono<Object> mono = resolveValue(param, Mono.just(toDataBuffer("12345")), new PayloadMethodArgumentResolverTests.TestValidator());
        StepVerifier.create(mono).expectNextCount(0).expectError(MethodArgumentNotValidException.class).verify();
    }

    @Test
    public void validateStringFlux() {
        ResolvableType type = ResolvableType.forClassWithGenerics(Flux.class, String.class);
        MethodParameter param = this.testMethod.arg(type);
        Flux<Object> flux = resolveValue(param, Mono.just(toDataBuffer("12345678\n12345")), new PayloadMethodArgumentResolverTests.TestValidator());
        StepVerifier.create(flux).expectNext("12345678").expectError(MethodArgumentNotValidException.class).verify();
    }

    private static class TestValidator implements Validator {
        @Override
        public boolean supports(Class<?> clazz) {
            return clazz.equals(String.class);
        }

        @Override
        public void validate(@Nullable
        Object target, Errors errors) {
            if ((target instanceof String) && ((((String) (target)).length()) < 8)) {
                errors.reject("Invalid length");
            }
        }
    }
}

