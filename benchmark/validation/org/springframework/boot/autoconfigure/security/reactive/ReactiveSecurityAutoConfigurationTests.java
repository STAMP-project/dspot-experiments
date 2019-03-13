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
package org.springframework.boot.autoconfigure.security.reactive;


import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ReactiveWebApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.server.WebFilterChainProxy;


/**
 * Tests for {@link ReactiveSecurityAutoConfiguration}.
 *
 * @author Madhura Bhave
 */
public class ReactiveSecurityAutoConfigurationTests {
    private ReactiveWebApplicationContextRunner contextRunner = new ReactiveWebApplicationContextRunner();

    @Test
    public void backsOffWhenWebFilterChainProxyBeanPresent() {
        this.contextRunner.withConfiguration(AutoConfigurations.of(ReactiveSecurityAutoConfiguration.class)).withUserConfiguration(ReactiveSecurityAutoConfigurationTests.WebFilterChainProxyConfiguration.class).run(( context) -> assertThat(context).hasSingleBean(.class));
    }

    @Test
    public void enablesWebFluxSecurity() {
        this.contextRunner.withConfiguration(AutoConfigurations.of(ReactiveSecurityAutoConfiguration.class, ReactiveUserDetailsServiceAutoConfiguration.class)).run(( context) -> assertThat(context).getBean(.class).isNotNull());
    }

    @Configuration
    static class WebFilterChainProxyConfiguration {
        @Bean
        public WebFilterChainProxy webFilterChainProxy() {
            return Mockito.mock(WebFilterChainProxy.class);
        }
    }
}

