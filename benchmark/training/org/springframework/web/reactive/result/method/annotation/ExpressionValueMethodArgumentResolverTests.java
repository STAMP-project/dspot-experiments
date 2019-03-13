/**
 * Copyright 2002-2017 the original author or authors.
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
import org.springframework.mock.http.server.reactive.test.MockServerHttpRequest;
import org.springframework.mock.web.test.server.MockServerWebExchange;
import org.springframework.web.reactive.BindingContext;
import reactor.core.publisher.Mono;


/**
 * Unit tests for {@link ExpressionValueMethodArgumentResolver}.
 *
 * @author Rossen Stoyanchev
 */
public class ExpressionValueMethodArgumentResolverTests {
    private ExpressionValueMethodArgumentResolver resolver;

    private final MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/"));

    private MethodParameter paramSystemProperty;

    private MethodParameter paramNotSupported;

    private MethodParameter paramAlsoNotSupported;

    @Test
    public void supportsParameter() throws Exception {
        Assert.assertTrue(this.resolver.supportsParameter(this.paramSystemProperty));
    }

    @Test
    public void doesNotSupport() throws Exception {
        Assert.assertFalse(this.resolver.supportsParameter(this.paramNotSupported));
        try {
            this.resolver.supportsParameter(this.paramAlsoNotSupported);
            Assert.fail();
        } catch (IllegalStateException ex) {
            Assert.assertTrue(("Unexpected error message:\n" + (ex.getMessage())), ex.getMessage().startsWith("ExpressionValueMethodArgumentResolver doesn't support reactive type wrapper"));
        }
    }

    @Test
    public void resolveSystemProperty() throws Exception {
        System.setProperty("systemProperty", "22");
        try {
            Mono<Object> mono = this.resolver.resolveArgument(this.paramSystemProperty, new BindingContext(), this.exchange);
            Object value = mono.block();
            Assert.assertEquals(22, value);
        } finally {
            System.clearProperty("systemProperty");
        }
    }
}

