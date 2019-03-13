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


import HttpMethod.GET;
import java.time.ZoneId;
import java.util.Locale;
import java.util.TimeZone;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.MethodParameter;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.mock.http.server.reactive.test.MockServerHttpRequest;
import org.springframework.mock.web.test.server.MockServerWebExchange;
import org.springframework.web.method.ResolvableMethod;
import org.springframework.web.reactive.BindingContext;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;


/**
 * Unit tests for {@link ServerWebExchangeArgumentResolver}.
 *
 * @author Rossen Stoyanchev
 */
public class ServerWebExchangeArgumentResolverTests {
    private final ServerWebExchangeArgumentResolver resolver = new ServerWebExchangeArgumentResolver(ReactiveAdapterRegistry.getSharedInstance());

    private final MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("http://example.org:9999/path?q=foo"));

    private ResolvableMethod testMethod = ResolvableMethod.on(getClass()).named("handle").build();

    @Test
    public void supportsParameter() {
        Assert.assertTrue(this.resolver.supportsParameter(this.testMethod.arg(ServerWebExchange.class)));
        Assert.assertTrue(this.resolver.supportsParameter(this.testMethod.arg(ServerHttpRequest.class)));
        Assert.assertTrue(this.resolver.supportsParameter(this.testMethod.arg(ServerHttpResponse.class)));
        Assert.assertTrue(this.resolver.supportsParameter(this.testMethod.arg(HttpMethod.class)));
        Assert.assertTrue(this.resolver.supportsParameter(this.testMethod.arg(Locale.class)));
        Assert.assertTrue(this.resolver.supportsParameter(this.testMethod.arg(TimeZone.class)));
        Assert.assertTrue(this.resolver.supportsParameter(this.testMethod.arg(ZoneId.class)));
        Assert.assertTrue(this.resolver.supportsParameter(this.testMethod.arg(UriComponentsBuilder.class)));
        Assert.assertTrue(this.resolver.supportsParameter(this.testMethod.arg(UriBuilder.class)));
        Assert.assertFalse(this.resolver.supportsParameter(this.testMethod.arg(WebSession.class)));
        Assert.assertFalse(this.resolver.supportsParameter(this.testMethod.arg(String.class)));
        try {
            this.resolver.supportsParameter(this.testMethod.arg(Mono.class, ServerWebExchange.class));
            Assert.fail();
        } catch (IllegalStateException ex) {
            Assert.assertTrue(("Unexpected error message:\n" + (ex.getMessage())), ex.getMessage().startsWith("ServerWebExchangeArgumentResolver doesn't support reactive type wrapper"));
        }
    }

    @Test
    public void resolveArgument() {
        testResolveArgument(this.testMethod.arg(ServerWebExchange.class), this.exchange);
        testResolveArgument(this.testMethod.arg(ServerHttpRequest.class), this.exchange.getRequest());
        testResolveArgument(this.testMethod.arg(ServerHttpResponse.class), this.exchange.getResponse());
        testResolveArgument(this.testMethod.arg(HttpMethod.class), GET);
        testResolveArgument(this.testMethod.arg(TimeZone.class), TimeZone.getDefault());
        testResolveArgument(this.testMethod.arg(ZoneId.class), ZoneId.systemDefault());
    }

    @Test
    public void resolveUriComponentsBuilder() {
        MethodParameter param = this.testMethod.arg(UriComponentsBuilder.class);
        Object value = this.resolver.resolveArgument(param, new BindingContext(), this.exchange).block();
        Assert.assertNotNull(value);
        Assert.assertEquals(UriComponentsBuilder.class, value.getClass());
        Assert.assertEquals("http://example.org:9999/next", path("/next").toUriString());
    }
}

