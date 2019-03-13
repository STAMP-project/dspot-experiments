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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.MethodParameter;
import org.springframework.mock.http.server.reactive.test.MockServerHttpRequest;
import org.springframework.mock.web.test.server.MockServerWebExchange;
import org.springframework.web.reactive.BindingContext;
import reactor.core.publisher.Mono;


/**
 * Unit tests for {@link PathVariableMapMethodArgumentResolver}.
 *
 * @author Rossen Stoyanchev
 */
public class PathVariableMapMethodArgumentResolverTests {
    private PathVariableMapMethodArgumentResolver resolver;

    private final MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/"));

    private MethodParameter paramMap;

    private MethodParameter paramNamedMap;

    private MethodParameter paramMapNoAnnot;

    private MethodParameter paramMonoMap;

    @Test
    public void supportsParameter() {
        Assert.assertTrue(resolver.supportsParameter(paramMap));
        Assert.assertFalse(resolver.supportsParameter(paramNamedMap));
        Assert.assertFalse(resolver.supportsParameter(paramMapNoAnnot));
        try {
            this.resolver.supportsParameter(this.paramMonoMap);
            Assert.fail();
        } catch (IllegalStateException ex) {
            Assert.assertTrue(("Unexpected error message:\n" + (ex.getMessage())), ex.getMessage().startsWith("PathVariableMapMethodArgumentResolver doesn't support reactive type wrapper"));
        }
    }

    @Test
    public void resolveArgument() throws Exception {
        Map<String, String> uriTemplateVars = new HashMap<>();
        uriTemplateVars.put("name1", "value1");
        uriTemplateVars.put("name2", "value2");
        this.exchange.getAttributes().put(URI_TEMPLATE_VARIABLES_ATTRIBUTE, uriTemplateVars);
        Mono<Object> mono = this.resolver.resolveArgument(this.paramMap, new BindingContext(), this.exchange);
        Object result = mono.block();
        Assert.assertEquals(uriTemplateVars, result);
    }

    @Test
    public void resolveArgumentNoUriVars() throws Exception {
        Mono<Object> mono = this.resolver.resolveArgument(this.paramMap, new BindingContext(), this.exchange);
        Object result = mono.block();
        Assert.assertEquals(Collections.emptyMap(), result);
    }
}

