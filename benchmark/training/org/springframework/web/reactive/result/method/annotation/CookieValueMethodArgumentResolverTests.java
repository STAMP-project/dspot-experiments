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


import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpCookie;
import org.springframework.mock.http.server.reactive.test.MockServerHttpRequest;
import org.springframework.mock.web.test.server.MockServerWebExchange;
import org.springframework.web.reactive.BindingContext;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


/**
 * Test fixture with {@link CookieValueMethodArgumentResolver}.
 *
 * @author Rossen Stoyanchev
 */
public class CookieValueMethodArgumentResolverTests {
    private CookieValueMethodArgumentResolver resolver;

    private BindingContext bindingContext;

    private MethodParameter cookieParameter;

    private MethodParameter cookieStringParameter;

    private MethodParameter stringParameter;

    private MethodParameter cookieMonoParameter;

    @Test
    public void supportsParameter() {
        Assert.assertTrue(this.resolver.supportsParameter(this.cookieParameter));
        Assert.assertTrue(this.resolver.supportsParameter(this.cookieStringParameter));
    }

    @Test
    public void doesNotSupportParameter() {
        Assert.assertFalse(this.resolver.supportsParameter(this.stringParameter));
        try {
            this.resolver.supportsParameter(this.cookieMonoParameter);
            Assert.fail();
        } catch (IllegalStateException ex) {
            Assert.assertTrue(("Unexpected error message:\n" + (ex.getMessage())), ex.getMessage().startsWith("CookieValueMethodArgumentResolver doesn't support reactive type wrapper"));
        }
    }

    @Test
    public void resolveCookieArgument() {
        HttpCookie expected = new HttpCookie("name", "foo");
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/").cookie(expected));
        Mono<Object> mono = this.resolver.resolveArgument(this.cookieParameter, this.bindingContext, exchange);
        Assert.assertEquals(expected, mono.block());
    }

    @Test
    public void resolveCookieStringArgument() {
        HttpCookie cookie = new HttpCookie("name", "foo");
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/").cookie(cookie));
        Mono<Object> mono = this.resolver.resolveArgument(this.cookieStringParameter, this.bindingContext, exchange);
        Assert.assertEquals("Invalid result", cookie.getValue(), mono.block());
    }

    @Test
    public void resolveCookieDefaultValue() {
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/"));
        Object result = this.resolver.resolveArgument(this.cookieStringParameter, this.bindingContext, exchange).block();
        Assert.assertTrue((result instanceof String));
        Assert.assertEquals("bar", result);
    }

    @Test
    public void notFound() {
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/"));
        Mono<Object> mono = resolver.resolveArgument(this.cookieParameter, this.bindingContext, exchange);
        StepVerifier.create(mono).expectNextCount(0).expectError(ServerWebInputException.class).verify();
    }
}

