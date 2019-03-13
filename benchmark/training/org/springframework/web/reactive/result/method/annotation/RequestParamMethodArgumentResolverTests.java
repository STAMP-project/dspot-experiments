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


import java.util.Map;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.MethodParameter;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.mock.http.server.reactive.test.MockServerHttpRequest;
import org.springframework.mock.web.test.server.MockServerWebExchange;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.method.ResolvableMethod;
import org.springframework.web.reactive.BindingContext;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


/**
 * Unit tests for {@link RequestParamMethodArgumentResolver}.
 *
 * @author Rossen Stoyanchev
 */
public class RequestParamMethodArgumentResolverTests {
    private RequestParamMethodArgumentResolver resolver;

    private BindingContext bindContext;

    private ResolvableMethod testMethod = ResolvableMethod.on(getClass()).named("handle").build();

    @Test
    public void supportsParameter() {
        MethodParameter param = this.testMethod.annot(requestParam().notRequired("bar")).arg(String.class);
        Assert.assertTrue(this.resolver.supportsParameter(param));
        param = this.testMethod.annotPresent(RequestParam.class).arg(String[].class);
        Assert.assertTrue(this.resolver.supportsParameter(param));
        param = this.testMethod.annot(requestParam().name("name")).arg(Map.class);
        Assert.assertTrue(this.resolver.supportsParameter(param));
        param = this.testMethod.annot(requestParam().name("")).arg(Map.class);
        Assert.assertFalse(this.resolver.supportsParameter(param));
        param = this.testMethod.annotNotPresent(RequestParam.class).arg(String.class);
        Assert.assertTrue(this.resolver.supportsParameter(param));
        param = this.testMethod.annot(requestParam()).arg(String.class);
        Assert.assertTrue(this.resolver.supportsParameter(param));
        param = this.testMethod.annot(requestParam().notRequired()).arg(String.class);
        Assert.assertTrue(this.resolver.supportsParameter(param));
    }

    @Test
    public void doesNotSupportParameterWithDefaultResolutionTurnedOff() {
        ReactiveAdapterRegistry adapterRegistry = ReactiveAdapterRegistry.getSharedInstance();
        this.resolver = new RequestParamMethodArgumentResolver(null, adapterRegistry, false);
        MethodParameter param = this.testMethod.annotNotPresent(RequestParam.class).arg(String.class);
        Assert.assertFalse(this.resolver.supportsParameter(param));
    }

    @Test
    public void doesNotSupportReactiveWrapper() {
        MethodParameter param;
        try {
            param = this.testMethod.annot(requestParam()).arg(Mono.class, String.class);
            this.resolver.supportsParameter(param);
            Assert.fail();
        } catch (IllegalStateException ex) {
            Assert.assertTrue(("Unexpected error message:\n" + (ex.getMessage())), ex.getMessage().startsWith("RequestParamMethodArgumentResolver doesn't support reactive type wrapper"));
        }
        try {
            param = this.testMethod.annotNotPresent(RequestParam.class).arg(Mono.class, String.class);
            this.resolver.supportsParameter(param);
            Assert.fail();
        } catch (IllegalStateException ex) {
            Assert.assertTrue(("Unexpected error message:\n" + (ex.getMessage())), ex.getMessage().startsWith("RequestParamMethodArgumentResolver doesn't support reactive type wrapper"));
        }
    }

    @Test
    public void resolveWithQueryString() {
        MethodParameter param = this.testMethod.annot(requestParam().notRequired("bar")).arg(String.class);
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/path?name=foo"));
        Assert.assertEquals("foo", resolve(param, exchange));
    }

    @Test
    public void resolveStringArray() {
        MethodParameter param = this.testMethod.annotPresent(RequestParam.class).arg(String[].class);
        MockServerHttpRequest request = MockServerHttpRequest.get("/path?name=foo&name=bar").build();
        Object result = resolve(param, MockServerWebExchange.from(request));
        Assert.assertTrue((result instanceof String[]));
        Assert.assertArrayEquals(new String[]{ "foo", "bar" }, ((String[]) (result)));
    }

    @Test
    public void resolveDefaultValue() {
        MethodParameter param = this.testMethod.annot(requestParam().notRequired("bar")).arg(String.class);
        Assert.assertEquals("bar", resolve(param, MockServerWebExchange.from(MockServerHttpRequest.get("/"))));
    }

    // SPR-17050
    @Test
    public void resolveAndConvertNullValue() {
        MethodParameter param = this.testMethod.annot(requestParam().notRequired()).arg(Integer.class);
        Assert.assertNull(resolve(param, MockServerWebExchange.from(MockServerHttpRequest.get("/?nullParam="))));
    }

    @Test
    public void missingRequestParam() {
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/"));
        MethodParameter param = this.testMethod.annotPresent(RequestParam.class).arg(String[].class);
        Mono<Object> mono = this.resolver.resolveArgument(param, this.bindContext, exchange);
        StepVerifier.create(mono).expectNextCount(0).expectError(ServerWebInputException.class).verify();
    }

    @Test
    public void resolveSimpleTypeParam() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/path?stringNotAnnot=plainValue").build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);
        MethodParameter param = this.testMethod.annotNotPresent(RequestParam.class).arg(String.class);
        Object result = resolve(param, exchange);
        Assert.assertEquals("plainValue", result);
    }

    // SPR-8561
    @Test
    public void resolveSimpleTypeParamToNull() {
        MethodParameter param = this.testMethod.annotNotPresent(RequestParam.class).arg(String.class);
        Assert.assertNull(resolve(param, MockServerWebExchange.from(MockServerHttpRequest.get("/"))));
    }

    // SPR-10180
    @Test
    public void resolveEmptyValueToDefault() {
        ServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/path?name="));
        MethodParameter param = this.testMethod.annot(requestParam().notRequired("bar")).arg(String.class);
        Object result = resolve(param, exchange);
        Assert.assertEquals("bar", result);
    }

    @Test
    public void resolveEmptyValueWithoutDefault() {
        MethodParameter param = this.testMethod.annotNotPresent(RequestParam.class).arg(String.class);
        MockServerHttpRequest request = MockServerHttpRequest.get("/path?stringNotAnnot=").build();
        Assert.assertEquals("", resolve(param, MockServerWebExchange.from(request)));
    }

    @Test
    public void resolveEmptyValueRequiredWithoutDefault() {
        MethodParameter param = this.testMethod.annot(requestParam()).arg(String.class);
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/path?name="));
        Assert.assertEquals("", resolve(param, exchange));
    }

    @Test
    public void resolveOptionalParamValue() {
        ServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/"));
        MethodParameter param = this.testMethod.arg(forClassWithGenerics(Optional.class, Integer.class));
        Object result = resolve(param, exchange);
        Assert.assertEquals(Optional.empty(), result);
        exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/path?name=123"));
        result = resolve(param, exchange);
        Assert.assertEquals(Optional.class, result.getClass());
        Optional<?> value = ((Optional<?>) (result));
        Assert.assertTrue(value.isPresent());
        Assert.assertEquals(123, value.get());
    }
}

