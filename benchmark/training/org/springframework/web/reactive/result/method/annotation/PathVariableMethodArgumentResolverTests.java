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


import HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.MethodParameter;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.mock.http.server.reactive.test.MockServerHttpRequest;
import org.springframework.mock.web.test.server.MockServerWebExchange;
import org.springframework.web.bind.support.ConfigurableWebBindingInitializer;
import org.springframework.web.reactive.BindingContext;
import org.springframework.web.server.ServerErrorException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


/**
 * Unit tests for {@link PathVariableMethodArgumentResolver}.
 *
 * @author Rossen Stoyanchev
 * @author Juergen Hoeller
 */
public class PathVariableMethodArgumentResolverTests {
    private PathVariableMethodArgumentResolver resolver;

    private final MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/"));

    private MethodParameter paramNamedString;

    private MethodParameter paramString;

    private MethodParameter paramNotRequired;

    private MethodParameter paramOptional;

    private MethodParameter paramMono;

    @Test
    public void supportsParameter() {
        Assert.assertTrue(this.resolver.supportsParameter(this.paramNamedString));
        Assert.assertFalse(this.resolver.supportsParameter(this.paramString));
        try {
            this.resolver.supportsParameter(this.paramMono);
            Assert.fail();
        } catch (IllegalStateException ex) {
            Assert.assertTrue(("Unexpected error message:\n" + (ex.getMessage())), ex.getMessage().startsWith("PathVariableMethodArgumentResolver doesn't support reactive type wrapper"));
        }
    }

    @Test
    public void resolveArgument() throws Exception {
        Map<String, String> uriTemplateVars = new HashMap<>();
        uriTemplateVars.put("name", "value");
        this.exchange.getAttributes().put(URI_TEMPLATE_VARIABLES_ATTRIBUTE, uriTemplateVars);
        BindingContext bindingContext = new BindingContext();
        Mono<Object> mono = this.resolver.resolveArgument(this.paramNamedString, bindingContext, this.exchange);
        Object result = mono.block();
        Assert.assertEquals("value", result);
    }

    @Test
    public void resolveArgumentNotRequired() throws Exception {
        Map<String, String> uriTemplateVars = new HashMap<>();
        uriTemplateVars.put("name", "value");
        this.exchange.getAttributes().put(URI_TEMPLATE_VARIABLES_ATTRIBUTE, uriTemplateVars);
        BindingContext bindingContext = new BindingContext();
        Mono<Object> mono = this.resolver.resolveArgument(this.paramNotRequired, bindingContext, this.exchange);
        Object result = mono.block();
        Assert.assertEquals("value", result);
    }

    @Test
    public void resolveArgumentWrappedAsOptional() throws Exception {
        Map<String, String> uriTemplateVars = new HashMap<>();
        uriTemplateVars.put("name", "value");
        this.exchange.getAttributes().put(URI_TEMPLATE_VARIABLES_ATTRIBUTE, uriTemplateVars);
        ConfigurableWebBindingInitializer initializer = new ConfigurableWebBindingInitializer();
        initializer.setConversionService(new DefaultFormattingConversionService());
        BindingContext bindingContext = new BindingContext(initializer);
        Mono<Object> mono = this.resolver.resolveArgument(this.paramOptional, bindingContext, this.exchange);
        Object result = mono.block();
        Assert.assertEquals(Optional.of("value"), result);
    }

    @Test
    public void handleMissingValue() throws Exception {
        BindingContext bindingContext = new BindingContext();
        Mono<Object> mono = this.resolver.resolveArgument(this.paramNamedString, bindingContext, this.exchange);
        StepVerifier.create(mono).expectNextCount(0).expectError(ServerErrorException.class).verify();
    }

    @Test
    public void nullIfNotRequired() throws Exception {
        BindingContext bindingContext = new BindingContext();
        Mono<Object> mono = this.resolver.resolveArgument(this.paramNotRequired, bindingContext, this.exchange);
        StepVerifier.create(mono).expectNextCount(0).expectComplete().verify();
    }

    @Test
    public void wrapEmptyWithOptional() throws Exception {
        BindingContext bindingContext = new BindingContext();
        Mono<Object> mono = this.resolver.resolveArgument(this.paramOptional, bindingContext, this.exchange);
        StepVerifier.create(mono).consumeNextWith(( value) -> {
            assertTrue((value instanceof Optional));
            assertFalse(((Optional<?>) (value)).isPresent());
        }).expectComplete().verify();
    }
}

