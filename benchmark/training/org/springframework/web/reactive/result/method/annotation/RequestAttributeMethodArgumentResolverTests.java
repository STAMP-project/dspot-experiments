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


import io.reactivex.Single;
import java.time.Duration;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.MethodParameter;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.mock.http.server.reactive.test.MockServerHttpRequest;
import org.springframework.mock.web.test.server.MockServerWebExchange;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.support.ConfigurableWebBindingInitializer;
import org.springframework.web.method.ResolvableMethod;
import org.springframework.web.reactive.BindingContext;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


/**
 * Unit tests for {@link RequestAttributeMethodArgumentResolver}.
 *
 * @author Rossen Stoyanchev
 */
public class RequestAttributeMethodArgumentResolverTests {
    private RequestAttributeMethodArgumentResolver resolver;

    private final MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/"));

    private final ResolvableMethod testMethod = ResolvableMethod.on(getClass()).named("handleWithRequestAttribute").build();

    @Test
    public void supportsParameter() throws Exception {
        Assert.assertTrue(this.resolver.supportsParameter(this.testMethod.annot(requestAttribute().noName()).arg(RequestAttributeMethodArgumentResolverTests.Foo.class)));
        // SPR-16158
        Assert.assertTrue(this.resolver.supportsParameter(this.testMethod.annotPresent(RequestAttribute.class).arg(Mono.class, RequestAttributeMethodArgumentResolverTests.Foo.class)));
        Assert.assertFalse(this.resolver.supportsParameter(this.testMethod.annotNotPresent(RequestAttribute.class).arg()));
    }

    @Test
    public void resolve() throws Exception {
        MethodParameter param = this.testMethod.annot(requestAttribute().noName()).arg(RequestAttributeMethodArgumentResolverTests.Foo.class);
        Mono<Object> mono = this.resolver.resolveArgument(param, new BindingContext(), this.exchange);
        StepVerifier.create(mono).expectNextCount(0).expectError(ServerWebInputException.class).verify();
        RequestAttributeMethodArgumentResolverTests.Foo foo = new RequestAttributeMethodArgumentResolverTests.Foo();
        this.exchange.getAttributes().put("foo", foo);
        mono = this.resolver.resolveArgument(param, new BindingContext(), this.exchange);
        Assert.assertSame(foo, mono.block());
    }

    @Test
    public void resolveWithName() throws Exception {
        MethodParameter param = this.testMethod.annot(requestAttribute().name("specialFoo")).arg();
        RequestAttributeMethodArgumentResolverTests.Foo foo = new RequestAttributeMethodArgumentResolverTests.Foo();
        this.exchange.getAttributes().put("specialFoo", foo);
        Mono<Object> mono = this.resolver.resolveArgument(param, new BindingContext(), this.exchange);
        Assert.assertSame(foo, mono.block());
    }

    @Test
    public void resolveNotRequired() throws Exception {
        MethodParameter param = this.testMethod.annot(requestAttribute().name("foo").notRequired()).arg();
        Mono<Object> mono = this.resolver.resolveArgument(param, new BindingContext(), this.exchange);
        Assert.assertNull(mono.block());
        RequestAttributeMethodArgumentResolverTests.Foo foo = new RequestAttributeMethodArgumentResolverTests.Foo();
        this.exchange.getAttributes().put("foo", foo);
        mono = this.resolver.resolveArgument(param, new BindingContext(), this.exchange);
        Assert.assertSame(foo, mono.block());
    }

    @Test
    public void resolveOptional() throws Exception {
        MethodParameter param = this.testMethod.annot(requestAttribute().name("foo")).arg(Optional.class, RequestAttributeMethodArgumentResolverTests.Foo.class);
        Mono<Object> mono = this.resolver.resolveArgument(param, new BindingContext(), this.exchange);
        Assert.assertNotNull(mono.block());
        Assert.assertEquals(Optional.class, mono.block().getClass());
        Assert.assertFalse(((Optional<?>) (mono.block())).isPresent());
        ConfigurableWebBindingInitializer initializer = new ConfigurableWebBindingInitializer();
        initializer.setConversionService(new DefaultFormattingConversionService());
        BindingContext bindingContext = new BindingContext(initializer);
        RequestAttributeMethodArgumentResolverTests.Foo foo = new RequestAttributeMethodArgumentResolverTests.Foo();
        this.exchange.getAttributes().put("foo", foo);
        mono = this.resolver.resolveArgument(param, bindingContext, this.exchange);
        Assert.assertNotNull(mono.block());
        Assert.assertEquals(Optional.class, mono.block().getClass());
        Optional<?> optional = ((Optional<?>) (mono.block()));
        Assert.assertTrue(optional.isPresent());
        Assert.assertSame(foo, optional.get());
    }

    // SPR-16158
    @Test
    public void resolveMonoParameter() throws Exception {
        MethodParameter param = this.testMethod.annot(requestAttribute().noName()).arg(Mono.class, RequestAttributeMethodArgumentResolverTests.Foo.class);
        // Mono attribute
        RequestAttributeMethodArgumentResolverTests.Foo foo = new RequestAttributeMethodArgumentResolverTests.Foo();
        Mono<RequestAttributeMethodArgumentResolverTests.Foo> fooMono = Mono.just(foo);
        this.exchange.getAttributes().put("fooMono", fooMono);
        Mono<Object> mono = this.resolver.resolveArgument(param, new BindingContext(), this.exchange);
        Assert.assertSame(fooMono, mono.block(Duration.ZERO));
        // RxJava Single attribute
        Single<RequestAttributeMethodArgumentResolverTests.Foo> singleMono = Single.just(foo);
        this.exchange.getAttributes().clear();
        this.exchange.getAttributes().put("fooMono", singleMono);
        mono = this.resolver.resolveArgument(param, new BindingContext(), this.exchange);
        Object value = mono.block(Duration.ZERO);
        Assert.assertTrue((value instanceof Mono));
        Assert.assertSame(foo, ((Mono<?>) (value)).block(Duration.ZERO));
        // No attribute --> Mono.empty
        this.exchange.getAttributes().clear();
        mono = this.resolver.resolveArgument(param, new BindingContext(), this.exchange);
        Assert.assertSame(Mono.empty(), mono.block(Duration.ZERO));
    }

    private static class Foo {}
}

