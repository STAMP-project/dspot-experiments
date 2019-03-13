/**
 * Copyright 2012-2019 the original author or authors.
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
package org.springframework.boot.actuate.autoconfigure.web.servlet;


import org.junit.Test;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.filter.RequestContextFilter;


/**
 * Tests for {@link WebMvcEndpointChildContextConfiguration}.
 *
 * @author Madhura Bhave
 */
public class WebMvcEndpointChildContextConfigurationTests {
    private WebApplicationContextRunner contextRunner = new WebApplicationContextRunner();

    @Test
    public void contextShouldConfigureRequestContextFilter() {
        this.contextRunner.withUserConfiguration(WebMvcEndpointChildContextConfiguration.class).run(( context) -> assertThat(context).hasSingleBean(.class));
    }

    @Test
    public void contextShouldNotConfigureRequestContextFilterWhenPresent() {
        this.contextRunner.withUserConfiguration(WebMvcEndpointChildContextConfigurationTests.ExistingConfig.class, WebMvcEndpointChildContextConfiguration.class).run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            assertThat(context).hasBean("testRequestContextFilter");
        });
    }

    @Test
    public void contextShouldNotConfigureRequestContextFilterWhenRequestContextListenerPresent() {
        this.contextRunner.withUserConfiguration(WebMvcEndpointChildContextConfigurationTests.RequestContextListenerConfig.class, WebMvcEndpointChildContextConfiguration.class).run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            assertThat(context).doesNotHaveBean(.class);
        });
    }

    @Test
    public void contextShouldConfigureDispatcherServletPathWithRootPath() {
        this.contextRunner.withUserConfiguration(WebMvcEndpointChildContextConfiguration.class).run(( context) -> assertThat(context.getBean(.class).getPath()).isEqualTo("/"));
    }

    @Configuration
    static class ExistingConfig {
        @Bean
        public RequestContextFilter testRequestContextFilter() {
            return new RequestContextFilter();
        }
    }

    @Configuration
    static class RequestContextListenerConfig {
        @Bean
        public RequestContextListener testRequestContextListener() {
            return new RequestContextListener();
        }
    }
}

