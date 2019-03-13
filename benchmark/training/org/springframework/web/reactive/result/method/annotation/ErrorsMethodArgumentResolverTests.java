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


import java.time.Duration;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.core.MethodParameter;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.core.ResolvableType;
import org.springframework.mock.http.server.reactive.test.MockServerHttpRequest;
import org.springframework.mock.web.test.server.MockServerWebExchange;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.method.ResolvableMethod;
import org.springframework.web.reactive.BindingContext;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoProcessor;


/**
 * Unit tests for {@link ErrorsMethodArgumentResolver}.
 *
 * @author Rossen Stoyanchev
 */
public class ErrorsMethodArgumentResolverTests {
    private final ErrorsMethodArgumentResolver resolver = new ErrorsMethodArgumentResolver(ReactiveAdapterRegistry.getSharedInstance());

    private final BindingContext bindingContext = new BindingContext();

    private final MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.post("/path"));

    private final ResolvableMethod testMethod = ResolvableMethod.on(getClass()).named("handle").build();

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Test
    public void supports() {
        MethodParameter parameter = this.testMethod.arg(Errors.class);
        Assert.assertTrue(this.resolver.supportsParameter(parameter));
        parameter = this.testMethod.arg(BindingResult.class);
        Assert.assertTrue(this.resolver.supportsParameter(parameter));
        parameter = this.testMethod.arg(ResolvableType.forClassWithGenerics(Mono.class, Errors.class));
        Assert.assertTrue(this.resolver.supportsParameter(parameter));
        parameter = this.testMethod.arg(String.class);
        Assert.assertFalse(this.resolver.supportsParameter(parameter));
    }

    @Test
    public void resolve() {
        BindingResult bindingResult = createBindingResult(new ErrorsMethodArgumentResolverTests.Foo(), "foo");
        this.bindingContext.getModel().asMap().put(((BindingResult.MODEL_KEY_PREFIX) + "foo"), bindingResult);
        MethodParameter parameter = this.testMethod.arg(Errors.class);
        Object actual = this.resolver.resolveArgument(parameter, this.bindingContext, this.exchange).block(Duration.ofMillis(5000));
        Assert.assertSame(bindingResult, actual);
    }

    @Test
    public void resolveWithMono() {
        BindingResult bindingResult = createBindingResult(new ErrorsMethodArgumentResolverTests.Foo(), "foo");
        MonoProcessor<BindingResult> monoProcessor = MonoProcessor.create();
        monoProcessor.onNext(bindingResult);
        this.bindingContext.getModel().asMap().put(((BindingResult.MODEL_KEY_PREFIX) + "foo"), monoProcessor);
        MethodParameter parameter = this.testMethod.arg(Errors.class);
        Object actual = this.resolver.resolveArgument(parameter, this.bindingContext, this.exchange).block(Duration.ofMillis(5000));
        Assert.assertSame(bindingResult, actual);
    }

    @Test
    public void resolveWithMonoOnBindingResultAndModelAttribute() {
        this.expectedException.expectMessage(("An @ModelAttribute and an Errors/BindingResult argument " + "cannot both be declared with an async type wrapper."));
        MethodParameter parameter = this.testMethod.arg(BindingResult.class);
        this.resolver.resolveArgument(parameter, this.bindingContext, this.exchange).block(Duration.ofMillis(5000));
    }

    // SPR-16187
    @Test
    public void resolveWithBindingResultNotFound() {
        this.expectedException.expectMessage(("An Errors/BindingResult argument is expected " + "immediately after the @ModelAttribute argument"));
        MethodParameter parameter = this.testMethod.arg(Errors.class);
        this.resolver.resolveArgument(parameter, this.bindingContext, this.exchange).block(Duration.ofMillis(5000));
    }

    @SuppressWarnings("unused")
    private static class Foo {
        private String name;

        public Foo() {
        }

        public Foo(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}

