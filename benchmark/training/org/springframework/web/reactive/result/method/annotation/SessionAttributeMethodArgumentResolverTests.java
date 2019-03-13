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


import java.lang.reflect.Method;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.core.MethodParameter;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.web.bind.support.ConfigurableWebBindingInitializer;
import org.springframework.web.reactive.BindingContext;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebInputException;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


/**
 * Unit tests for {@link SessionAttributeMethodArgumentResolver}.
 *
 * @author Rossen Stoyanchev
 */
public class SessionAttributeMethodArgumentResolverTests {
    private SessionAttributeMethodArgumentResolver resolver;

    private ServerWebExchange exchange;

    private WebSession session;

    private Method handleMethod;

    @Test
    public void supportsParameter() {
        Assert.assertTrue(this.resolver.supportsParameter(new MethodParameter(this.handleMethod, 0)));
        Assert.assertFalse(this.resolver.supportsParameter(new MethodParameter(this.handleMethod, 4)));
    }

    @Test
    public void resolve() {
        MethodParameter param = initMethodParameter(0);
        Mono<Object> mono = this.resolver.resolveArgument(param, new BindingContext(), this.exchange);
        StepVerifier.create(mono).expectError(ServerWebInputException.class).verify();
        SessionAttributeMethodArgumentResolverTests.Foo foo = new SessionAttributeMethodArgumentResolverTests.Foo();
        Mockito.when(this.session.getAttribute("foo")).thenReturn(foo);
        mono = this.resolver.resolveArgument(param, new BindingContext(), this.exchange);
        Assert.assertSame(foo, mono.block());
    }

    @Test
    public void resolveWithName() {
        MethodParameter param = initMethodParameter(1);
        SessionAttributeMethodArgumentResolverTests.Foo foo = new SessionAttributeMethodArgumentResolverTests.Foo();
        Mockito.when(this.session.getAttribute("specialFoo")).thenReturn(foo);
        Mono<Object> mono = this.resolver.resolveArgument(param, new BindingContext(), this.exchange);
        Assert.assertSame(foo, mono.block());
    }

    @Test
    public void resolveNotRequired() {
        MethodParameter param = initMethodParameter(2);
        Mono<Object> mono = this.resolver.resolveArgument(param, new BindingContext(), this.exchange);
        Assert.assertNull(mono.block());
        SessionAttributeMethodArgumentResolverTests.Foo foo = new SessionAttributeMethodArgumentResolverTests.Foo();
        Mockito.when(this.session.getAttribute("foo")).thenReturn(foo);
        mono = this.resolver.resolveArgument(param, new BindingContext(), this.exchange);
        Assert.assertSame(foo, mono.block());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void resolveOptional() {
        MethodParameter param = initMethodParameter(3);
        Optional<Object> actual = ((Optional<Object>) (this.resolver.resolveArgument(param, new BindingContext(), this.exchange).block()));
        Assert.assertNotNull(actual);
        Assert.assertFalse(actual.isPresent());
        ConfigurableWebBindingInitializer initializer = new ConfigurableWebBindingInitializer();
        initializer.setConversionService(new DefaultFormattingConversionService());
        BindingContext bindingContext = new BindingContext(initializer);
        SessionAttributeMethodArgumentResolverTests.Foo foo = new SessionAttributeMethodArgumentResolverTests.Foo();
        Mockito.when(this.session.getAttribute("foo")).thenReturn(foo);
        actual = ((Optional<Object>) (this.resolver.resolveArgument(param, bindingContext, this.exchange).block()));
        Assert.assertNotNull(actual);
        Assert.assertTrue(actual.isPresent());
        Assert.assertSame(foo, actual.get());
    }

    private static class Foo {}
}

