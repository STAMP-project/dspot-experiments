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
package org.springframework.boot.autoconfigure.condition;


import org.junit.Test;
import org.springframework.boot.autoconfigure.web.reactive.MockReactiveWebServerFactory;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.test.context.runner.ReactiveWebApplicationContextRunner;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.boot.web.reactive.server.ReactiveWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.reactive.HttpHandler;
import reactor.core.publisher.Mono;


/**
 * Tests for {@link ConditionalOnNotWebApplication}.
 *
 * @author Dave Syer
 * @author Stephane Nicoll
 */
public class ConditionalOnNotWebApplicationTests {
    @Test
    public void testNotWebApplicationWithServletContext() {
        new WebApplicationContextRunner().withUserConfiguration(ConditionalOnNotWebApplicationTests.NotWebApplicationConfiguration.class).run(( context) -> assertThat(context).doesNotHaveBean(.class));
    }

    @Test
    public void testNotWebApplicationWithReactiveContext() {
        new ReactiveWebApplicationContextRunner().withUserConfiguration(ConditionalOnNotWebApplicationTests.ReactiveApplicationConfig.class, ConditionalOnNotWebApplicationTests.NotWebApplicationConfiguration.class).run(( context) -> assertThat(context).doesNotHaveBean(.class));
    }

    @Test
    public void testNotWebApplication() {
        new ApplicationContextRunner().withUserConfiguration(ConditionalOnNotWebApplicationTests.NotWebApplicationConfiguration.class).run(( context) -> assertThat(context).getBeans(.class).containsExactly(entry("none", "none")));
    }

    @Configuration
    protected static class ReactiveApplicationConfig {
        @Bean
        public ReactiveWebServerFactory reactiveWebServerFactory() {
            return new MockReactiveWebServerFactory();
        }

        @Bean
        public HttpHandler httpHandler() {
            return ( request, response) -> Mono.empty();
        }
    }

    @Configuration
    @ConditionalOnNotWebApplication
    protected static class NotWebApplicationConfiguration {
        @Bean
        public String none() {
            return "none";
        }
    }
}

