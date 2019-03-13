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
package org.springframework.boot.actuate.autoconfigure.security.reactive;


import java.util.Collections;
import java.util.List;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.BeansException;
import org.springframework.boot.actuate.autoconfigure.endpoint.EndpointAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.env.EnvironmentEndpointAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.health.HealthEndpointAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.health.HealthIndicatorAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.info.InfoEndpointAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.security.oauth2.resource.reactive.ReactiveOAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveUserDetailsServiceAutoConfiguration;
import org.springframework.boot.test.context.runner.ReactiveWebApplicationContextRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.WebFilterChainProxy;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebHandler;
import org.springframework.web.server.adapter.HttpWebHandlerAdapter;


/**
 * Tests for {@link ReactiveManagementWebSecurityAutoConfiguration}.
 *
 * @author Madhura Bhave
 */
public class ReactiveManagementWebSecurityAutoConfigurationTests {
    private ReactiveWebApplicationContextRunner contextRunner = new ReactiveWebApplicationContextRunner().withConfiguration(AutoConfigurations.of(HealthIndicatorAutoConfiguration.class, HealthEndpointAutoConfiguration.class, InfoEndpointAutoConfiguration.class, EnvironmentEndpointAutoConfiguration.class, EndpointAutoConfiguration.class, WebEndpointAutoConfiguration.class, ReactiveSecurityAutoConfiguration.class, ReactiveUserDetailsServiceAutoConfiguration.class, ReactiveManagementWebSecurityAutoConfiguration.class));

    @Test
    public void permitAllForHealth() {
        this.contextRunner.run(( context) -> assertThat(getAuthenticateHeader(context, "/actuator/health")).isNull());
    }

    @Test
    public void permitAllForInfo() {
        this.contextRunner.run(( context) -> assertThat(getAuthenticateHeader(context, "/actuator/info")).isNull());
    }

    @Test
    public void securesEverythingElse() {
        this.contextRunner.run(( context) -> {
            assertThat(getAuthenticateHeader(context, "/actuator").get(0)).contains("Basic realm=");
            assertThat(getAuthenticateHeader(context, "/foo").toString()).contains("Basic realm=");
        });
    }

    @Test
    public void usesMatchersBasedOffConfiguredActuatorBasePath() {
        this.contextRunner.withPropertyValues("management.endpoints.web.base-path=/").run(( context) -> {
            assertThat(getAuthenticateHeader(context, "/health")).isNull();
            assertThat(getAuthenticateHeader(context, "/foo").get(0)).contains("Basic realm=");
        });
    }

    @Test
    public void backsOffIfCustomSecurityIsAdded() {
        this.contextRunner.withUserConfiguration(ReactiveManagementWebSecurityAutoConfigurationTests.CustomSecurityConfiguration.class).run(( context) -> {
            assertThat(getLocationHeader(context, "/actuator/health").toString()).contains("/login");
            assertThat(getLocationHeader(context, "/foo")).isNull();
        });
    }

    @Test
    public void backOffIfReactiveOAuth2ResourceServerAutoConfigurationPresent() {
        this.contextRunner.withConfiguration(AutoConfigurations.of(ReactiveOAuth2ResourceServerAutoConfiguration.class)).withPropertyValues("spring.security.oauth2.resourceserver.jwt.jwk-set-uri=http://authserver").run(( context) -> assertThat(context).doesNotHaveBean(.class));
    }

    @Test
    public void backsOffWhenWebFilterChainProxyBeanPresent() {
        this.contextRunner.withUserConfiguration(ReactiveManagementWebSecurityAutoConfigurationTests.WebFilterChainProxyConfiguration.class).run(( context) -> {
            assertThat(getLocationHeader(context, "/actuator/health").toString()).contains("/login");
            assertThat(getLocationHeader(context, "/foo").toString()).contains("/login");
        });
    }

    private static class TestHttpWebHandlerAdapter extends HttpWebHandlerAdapter {
        TestHttpWebHandlerAdapter(WebHandler delegate) {
            super(delegate);
        }

        @Override
        protected ServerWebExchange createExchange(ServerHttpRequest request, ServerHttpResponse response) {
            return super.createExchange(request, response);
        }
    }

    @Configuration
    static class CustomSecurityConfiguration {
        @Bean
        public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
            return http.authorizeExchange().pathMatchers("/foo").permitAll().anyExchange().authenticated().and().formLogin().and().build();
        }
    }

    @Configuration
    static class WebFilterChainProxyConfiguration {
        @Bean
        public ReactiveAuthenticationManager authenticationManager() {
            return Mockito.mock(ReactiveAuthenticationManager.class);
        }

        @Bean
        public WebFilterChainProxy webFilterChainProxy(ServerHttpSecurity http) {
            return new WebFilterChainProxy(getFilterChains(http));
        }

        @Bean
        public ReactiveManagementWebSecurityAutoConfigurationTests.WebFilterChainProxyConfiguration.TestServerHttpSecurity http(ReactiveAuthenticationManager authenticationManager) {
            ReactiveManagementWebSecurityAutoConfigurationTests.WebFilterChainProxyConfiguration.TestServerHttpSecurity httpSecurity = new ReactiveManagementWebSecurityAutoConfigurationTests.WebFilterChainProxyConfiguration.TestServerHttpSecurity();
            httpSecurity.authenticationManager(authenticationManager);
            return httpSecurity;
        }

        private List<SecurityWebFilterChain> getFilterChains(ServerHttpSecurity http) {
            return Collections.singletonList(http.authorizeExchange().anyExchange().authenticated().and().formLogin().and().build());
        }

        private static class TestServerHttpSecurity extends ServerHttpSecurity implements ApplicationContextAware {
            @Override
            public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
                super.setApplicationContext(applicationContext);
            }
        }
    }
}

