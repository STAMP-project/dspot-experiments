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


import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.MethodParameter;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.mock.http.server.reactive.test.MockServerHttpRequest;
import org.springframework.mock.web.test.server.MockServerWebExchange;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.method.ResolvableMethod;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


/**
 * Unit tests for {@link RequestParamMapMethodArgumentResolver}.
 *
 * @author Rossen Stoyanchev
 */
public class RequestParamMapMethodArgumentResolverTests {
    private final RequestParamMapMethodArgumentResolver resolver = new RequestParamMapMethodArgumentResolver(ReactiveAdapterRegistry.getSharedInstance());

    private ResolvableMethod testMethod = ResolvableMethod.on(getClass()).named("handle").build();

    @Test
    public void supportsParameter() {
        MethodParameter param = this.testMethod.annot(requestParam().name("")).arg(Map.class);
        Assert.assertTrue(this.resolver.supportsParameter(param));
        param = this.testMethod.annotPresent(RequestParam.class).arg(MultiValueMap.class);
        Assert.assertTrue(this.resolver.supportsParameter(param));
        param = this.testMethod.annot(requestParam().name("name")).arg(Map.class);
        Assert.assertFalse(this.resolver.supportsParameter(param));
        param = this.testMethod.annotNotPresent(RequestParam.class).arg(Map.class);
        Assert.assertFalse(this.resolver.supportsParameter(param));
        try {
            param = this.testMethod.annot(requestParam()).arg(Mono.class, Map.class);
            this.resolver.supportsParameter(param);
            Assert.fail();
        } catch (IllegalStateException ex) {
            Assert.assertTrue(("Unexpected error message:\n" + (ex.getMessage())), ex.getMessage().startsWith("RequestParamMapMethodArgumentResolver doesn't support reactive type wrapper"));
        }
    }

    @Test
    public void resolveMapArgumentWithQueryString() throws Exception {
        MethodParameter param = this.testMethod.annot(requestParam().name("")).arg(Map.class);
        Object result = resolve(param, MockServerWebExchange.from(MockServerHttpRequest.get("/path?foo=bar")));
        Assert.assertTrue((result instanceof Map));
        Assert.assertEquals(Collections.singletonMap("foo", "bar"), result);
    }

    @Test
    public void resolveMultiValueMapArgument() throws Exception {
        MethodParameter param = this.testMethod.annotPresent(RequestParam.class).arg(MultiValueMap.class);
        ServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/path?foo=bar&foo=baz"));
        Object result = resolve(param, exchange);
        Assert.assertTrue((result instanceof MultiValueMap));
        Assert.assertEquals(Collections.singletonMap("foo", Arrays.asList("bar", "baz")), result);
    }
}

