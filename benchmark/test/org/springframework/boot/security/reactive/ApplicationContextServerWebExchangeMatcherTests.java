/**
 * Copyright 2012-2018 the original author or authors.
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
package org.springframework.boot.security.reactive;


import java.util.function.Supplier;
import org.junit.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebHandler;
import org.springframework.web.server.adapter.HttpWebHandlerAdapter;
import reactor.core.publisher.Mono;


/**
 * Tests for {@link ApplicationContextServerWebExchangeMatcher}.
 *
 * @author Madhura Bhave
 */
public class ApplicationContextServerWebExchangeMatcherTests {
    @Test
    public void createWhenContextClassIsNullShouldThrowException() {
        assertThatIllegalArgumentException().isThrownBy(() -> new TestApplicationContextServerWebExchangeMatcher<>(null)).withMessageContaining("Context class must not be null");
    }

    @Test
    public void matchesWhenContextClassIsApplicationContextShouldProvideContext() {
        ServerWebExchange exchange = createExchange();
        StaticApplicationContext context = ((StaticApplicationContext) (exchange.getApplicationContext()));
        assertThat(new ApplicationContextServerWebExchangeMatcherTests.TestApplicationContextServerWebExchangeMatcher<>(ApplicationContext.class).callMatchesAndReturnProvidedContext(exchange).get()).isEqualTo(context);
    }

    @Test
    public void matchesWhenContextClassIsExistingBeanShouldProvideBean() {
        ServerWebExchange exchange = createExchange();
        StaticApplicationContext context = ((StaticApplicationContext) (exchange.getApplicationContext()));
        context.registerSingleton("existingBean", ApplicationContextServerWebExchangeMatcherTests.ExistingBean.class);
        assertThat(new ApplicationContextServerWebExchangeMatcherTests.TestApplicationContextServerWebExchangeMatcher<>(ApplicationContextServerWebExchangeMatcherTests.ExistingBean.class).callMatchesAndReturnProvidedContext(exchange).get()).isEqualTo(context.getBean(ApplicationContextServerWebExchangeMatcherTests.ExistingBean.class));
    }

    @Test
    public void matchesWhenContextClassIsMissingBeanShouldProvideException() {
        ServerWebExchange exchange = createExchange();
        Supplier<ApplicationContextServerWebExchangeMatcherTests.ExistingBean> supplier = new ApplicationContextServerWebExchangeMatcherTests.TestApplicationContextServerWebExchangeMatcher<>(ApplicationContextServerWebExchangeMatcherTests.ExistingBean.class).callMatchesAndReturnProvidedContext(exchange);
        assertThatExceptionOfType(NoSuchBeanDefinitionException.class).isThrownBy(supplier::get);
    }

    @Test
    public void matchesWhenContextIsNull() {
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/path").build());
        assertThatIllegalStateException().isThrownBy(() -> new TestApplicationContextServerWebExchangeMatcher<>(.class).callMatchesAndReturnProvidedContext(exchange)).withMessageContaining("No ApplicationContext found on ServerWebExchange.");
    }

    static class TestHttpWebHandlerAdapter extends HttpWebHandlerAdapter {
        TestHttpWebHandlerAdapter(WebHandler delegate) {
            super(delegate);
        }

        @Override
        protected ServerWebExchange createExchange(ServerHttpRequest request, ServerHttpResponse response) {
            return super.createExchange(request, response);
        }
    }

    static class ExistingBean {}

    static class NewBean {
        private final ApplicationContextServerWebExchangeMatcherTests.ExistingBean bean;

        NewBean(ApplicationContextServerWebExchangeMatcherTests.ExistingBean bean) {
            this.bean = bean;
        }

        public ApplicationContextServerWebExchangeMatcherTests.ExistingBean getBean() {
            return this.bean;
        }
    }

    static class TestApplicationContextServerWebExchangeMatcher<C> extends ApplicationContextServerWebExchangeMatcher<C> {
        private Supplier<C> providedContext;

        TestApplicationContextServerWebExchangeMatcher(Class<? extends C> context) {
            super(context);
        }

        Supplier<C> callMatchesAndReturnProvidedContext(ServerWebExchange exchange) {
            matches(exchange);
            return getProvidedContext();
        }

        @Override
        protected Mono<MatchResult> matches(ServerWebExchange exchange, Supplier<C> context) {
            this.providedContext = context;
            return MatchResult.match();
        }

        Supplier<C> getProvidedContext() {
            return this.providedContext;
        }
    }
}

