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


import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.MethodParameter;
import org.springframework.mock.http.server.reactive.test.MockServerHttpRequest;
import org.springframework.mock.web.test.server.MockServerWebExchange;
import org.springframework.web.reactive.BindingContext;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


/**
 * Unit tests for {@link RequestHeaderMethodArgumentResolver}.
 *
 * @author Rossen Stoyanchev
 */
public class RequestHeaderMethodArgumentResolverTests {
    private RequestHeaderMethodArgumentResolver resolver;

    private BindingContext bindingContext;

    private MethodParameter paramNamedDefaultValueStringHeader;

    private MethodParameter paramNamedValueStringArray;

    private MethodParameter paramSystemProperty;

    private MethodParameter paramResolvedNameWithExpression;

    private MethodParameter paramResolvedNameWithPlaceholder;

    private MethodParameter paramNamedValueMap;

    private MethodParameter paramDate;

    private MethodParameter paramInstant;

    private MethodParameter paramMono;

    @Test
    public void supportsParameter() {
        Assert.assertTrue("String parameter not supported", resolver.supportsParameter(paramNamedDefaultValueStringHeader));
        Assert.assertTrue("String array parameter not supported", resolver.supportsParameter(paramNamedValueStringArray));
        Assert.assertFalse("non-@RequestParam parameter supported", resolver.supportsParameter(paramNamedValueMap));
        try {
            this.resolver.supportsParameter(this.paramMono);
            Assert.fail();
        } catch (IllegalStateException ex) {
            Assert.assertTrue(("Unexpected error message:\n" + (ex.getMessage())), ex.getMessage().startsWith("RequestHeaderMethodArgumentResolver doesn't support reactive type wrapper"));
        }
    }

    @Test
    public void resolveStringArgument() throws Exception {
        String expected = "foo";
        ServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/").header("name", expected));
        Mono<Object> mono = this.resolver.resolveArgument(this.paramNamedDefaultValueStringHeader, this.bindingContext, exchange);
        Object result = mono.block();
        Assert.assertTrue((result instanceof String));
        Assert.assertEquals(expected, result);
    }

    @Test
    public void resolveStringArrayArgument() throws Exception {
        MockServerHttpRequest request = MockServerHttpRequest.get("/").header("name", "foo", "bar").build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);
        Mono<Object> mono = this.resolver.resolveArgument(this.paramNamedValueStringArray, this.bindingContext, exchange);
        Object result = mono.block();
        Assert.assertTrue((result instanceof String[]));
        Assert.assertArrayEquals(new String[]{ "foo", "bar" }, ((String[]) (result)));
    }

    @Test
    public void resolveDefaultValue() throws Exception {
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/"));
        Mono<Object> mono = this.resolver.resolveArgument(this.paramNamedDefaultValueStringHeader, this.bindingContext, exchange);
        Object result = mono.block();
        Assert.assertTrue((result instanceof String));
        Assert.assertEquals("bar", result);
    }

    @Test
    public void resolveDefaultValueFromSystemProperty() throws Exception {
        System.setProperty("systemProperty", "bar");
        try {
            Mono<Object> mono = this.resolver.resolveArgument(this.paramSystemProperty, this.bindingContext, MockServerWebExchange.from(MockServerHttpRequest.get("/")));
            Object result = mono.block();
            Assert.assertTrue((result instanceof String));
            Assert.assertEquals("bar", result);
        } finally {
            System.clearProperty("systemProperty");
        }
    }

    @Test
    public void resolveNameFromSystemPropertyThroughExpression() throws Exception {
        String expected = "foo";
        MockServerHttpRequest request = MockServerHttpRequest.get("/").header("bar", expected).build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);
        System.setProperty("systemProperty", "bar");
        try {
            Mono<Object> mono = this.resolver.resolveArgument(this.paramResolvedNameWithExpression, this.bindingContext, exchange);
            Object result = mono.block();
            Assert.assertTrue((result instanceof String));
            Assert.assertEquals(expected, result);
        } finally {
            System.clearProperty("systemProperty");
        }
    }

    @Test
    public void resolveNameFromSystemPropertyThroughPlaceholder() throws Exception {
        String expected = "foo";
        MockServerHttpRequest request = MockServerHttpRequest.get("/").header("bar", expected).build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);
        System.setProperty("systemProperty", "bar");
        try {
            Mono<Object> mono = this.resolver.resolveArgument(this.paramResolvedNameWithPlaceholder, this.bindingContext, exchange);
            Object result = mono.block();
            Assert.assertTrue((result instanceof String));
            Assert.assertEquals(expected, result);
        } finally {
            System.clearProperty("systemProperty");
        }
    }

    @Test
    public void notFound() throws Exception {
        Mono<Object> mono = resolver.resolveArgument(this.paramNamedValueStringArray, this.bindingContext, MockServerWebExchange.from(MockServerHttpRequest.get("/")));
        StepVerifier.create(mono).expectNextCount(0).expectError(ServerWebInputException.class).verify();
    }

    @Test
    @SuppressWarnings("deprecation")
    public void dateConversion() throws Exception {
        String rfc1123val = "Thu, 21 Apr 2016 17:11:08 +0100";
        MockServerHttpRequest request = MockServerHttpRequest.get("/").header("name", rfc1123val).build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);
        Mono<Object> mono = this.resolver.resolveArgument(this.paramDate, this.bindingContext, exchange);
        Object result = mono.block();
        Assert.assertTrue((result instanceof Date));
        Assert.assertEquals(new Date(rfc1123val), result);
    }

    @Test
    public void instantConversion() throws Exception {
        String rfc1123val = "Thu, 21 Apr 2016 17:11:08 +0100";
        MockServerHttpRequest request = MockServerHttpRequest.get("/").header("name", rfc1123val).build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);
        Mono<Object> mono = this.resolver.resolveArgument(this.paramInstant, this.bindingContext, exchange);
        Object result = mono.block();
        Assert.assertTrue((result instanceof Instant));
        Assert.assertEquals(Instant.from(DateTimeFormatter.RFC_1123_DATE_TIME.parse(rfc1123val)), result);
    }
}

