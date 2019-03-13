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
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.core.MethodParameter;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.mock.http.server.reactive.test.MockServerHttpRequest;
import org.springframework.web.method.ResolvableMethod;
import org.springframework.web.reactive.BindingContext;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;


/**
 * Unit tests for {@link WebSessionArgumentResolver}.
 *
 * @author Rossen Stoyanchev
 */
public class WebSessionArgumentResolverTests {
    private final WebSessionArgumentResolver resolver = new WebSessionArgumentResolver(ReactiveAdapterRegistry.getSharedInstance());

    private ResolvableMethod testMethod = ResolvableMethod.on(getClass()).named("handle").build();

    @Test
    public void supportsParameter() {
        Assert.assertTrue(this.resolver.supportsParameter(this.testMethod.arg(WebSession.class)));
        Assert.assertTrue(this.resolver.supportsParameter(this.testMethod.arg(Mono.class, WebSession.class)));
        Assert.assertTrue(this.resolver.supportsParameter(this.testMethod.arg(Single.class, WebSession.class)));
    }

    @Test
    public void resolverArgument() {
        BindingContext context = new BindingContext();
        WebSession session = Mockito.mock(WebSession.class);
        MockServerHttpRequest request = MockServerHttpRequest.get("/").build();
        ServerWebExchange exchange = org.springframework.mock.web.test.server.MockServerWebExchange.builder(request).session(session).build();
        MethodParameter param = this.testMethod.arg(WebSession.class);
        Object actual = this.resolver.resolveArgument(param, context, exchange).block();
        Assert.assertSame(session, actual);
        param = this.testMethod.arg(Mono.class, WebSession.class);
        actual = this.resolver.resolveArgument(param, context, exchange).block();
        Assert.assertNotNull(actual);
        Assert.assertTrue(Mono.class.isAssignableFrom(actual.getClass()));
        Assert.assertSame(session, ((Mono<?>) (actual)).block());
        param = this.testMethod.arg(Single.class, WebSession.class);
        actual = this.resolver.resolveArgument(param, context, exchange).block();
        Assert.assertNotNull(actual);
        Assert.assertTrue(Single.class.isAssignableFrom(actual.getClass()));
        Assert.assertSame(session, ((Single<?>) (actual)).blockingGet());
    }
}

