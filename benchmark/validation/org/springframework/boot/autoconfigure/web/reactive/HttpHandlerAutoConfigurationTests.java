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
package org.springframework.boot.autoconfigure.web.reactive;


import org.junit.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ReactiveWebApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;


/**
 * Tests for {@link HttpHandlerAutoConfiguration}.
 *
 * @author Brian Clozel
 * @author Stephane Nicoll
 * @author Andy Wilkinson
 */
public class HttpHandlerAutoConfigurationTests {
    private final ReactiveWebApplicationContextRunner contextRunner = new ReactiveWebApplicationContextRunner().withConfiguration(AutoConfigurations.of(HttpHandlerAutoConfiguration.class));

    @Test
    public void shouldNotProcessIfExistingHttpHandler() {
        this.contextRunner.withUserConfiguration(HttpHandlerAutoConfigurationTests.CustomHttpHandler.class).run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            assertThat(context).getBean(.class).isSameAs(context.getBean("customHttpHandler"));
        });
    }

    @Test
    public void shouldConfigureHttpHandlerAnnotation() {
        this.contextRunner.withConfiguration(AutoConfigurations.of(WebFluxAutoConfiguration.class)).run(( context) -> assertThat(context).hasSingleBean(.class));
    }

    @Configuration
    protected static class CustomHttpHandler {
        @Bean
        public HttpHandler customHttpHandler() {
            return ( serverHttpRequest, serverHttpResponse) -> null;
        }

        @Bean
        public RouterFunction<ServerResponse> routerFunction() {
            return route(GET("/test"), ( serverRequest) -> null);
        }
    }
}

