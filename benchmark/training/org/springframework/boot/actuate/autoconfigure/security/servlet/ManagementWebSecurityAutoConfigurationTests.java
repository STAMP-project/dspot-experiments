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
package org.springframework.boot.actuate.autoconfigure.security.servlet;


import org.junit.Test;
import org.springframework.boot.actuate.autoconfigure.endpoint.EndpointAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.env.EnvironmentEndpointAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.health.HealthEndpointAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.health.HealthIndicatorAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.info.InfoEndpointAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;


/**
 * Tests for {@link ManagementWebSecurityAutoConfiguration}.
 *
 * @author Madhura Bhave
 */
public class ManagementWebSecurityAutoConfigurationTests {
    private WebApplicationContextRunner contextRunner = new WebApplicationContextRunner().withConfiguration(AutoConfigurations.of(HealthIndicatorAutoConfiguration.class, HealthEndpointAutoConfiguration.class, InfoEndpointAutoConfiguration.class, EnvironmentEndpointAutoConfiguration.class, EndpointAutoConfiguration.class, WebEndpointAutoConfiguration.class, SecurityAutoConfiguration.class, ManagementWebSecurityAutoConfiguration.class));

    @Test
    public void permitAllForHealth() {
        this.contextRunner.run(( context) -> {
            HttpStatus status = getResponseStatus(context, "/actuator/health");
            assertThat(status).isEqualTo(HttpStatus.OK);
        });
    }

    @Test
    public void permitAllForInfo() {
        this.contextRunner.run(( context) -> {
            HttpStatus status = getResponseStatus(context, "/actuator/info");
            assertThat(status).isEqualTo(HttpStatus.OK);
        });
    }

    @Test
    public void securesEverythingElse() {
        this.contextRunner.run(( context) -> {
            HttpStatus status = getResponseStatus(context, "/actuator");
            assertThat(status).isEqualTo(HttpStatus.UNAUTHORIZED);
            status = getResponseStatus(context, "/foo");
            assertThat(status).isEqualTo(HttpStatus.UNAUTHORIZED);
        });
    }

    @Test
    public void usesMatchersBasedOffConfiguredActuatorBasePath() {
        this.contextRunner.withPropertyValues("management.endpoints.web.base-path=/").run(( context) -> {
            HttpStatus status = getResponseStatus(context, "/health");
            assertThat(status).isEqualTo(HttpStatus.OK);
        });
    }

    @Test
    public void backOffIfCustomSecurityIsAdded() {
        this.contextRunner.withUserConfiguration(ManagementWebSecurityAutoConfigurationTests.CustomSecurityConfiguration.class).run(( context) -> {
            HttpStatus status = getResponseStatus(context, "/actuator/health");
            assertThat(status).isEqualTo(HttpStatus.UNAUTHORIZED);
            status = getResponseStatus(context, "/foo");
            assertThat(status).isEqualTo(HttpStatus.OK);
        });
    }

    @Test
    public void backOffIfOAuth2ResourceServerAutoConfigurationPresent() {
        this.contextRunner.withConfiguration(AutoConfigurations.of(OAuth2ResourceServerAutoConfiguration.class)).withPropertyValues("spring.security.oauth2.resourceserver.jwt.jwk-set-uri=http://authserver").run(( context) -> assertThat(context).doesNotHaveBean(.class));
    }

    @Configuration
    static class CustomSecurityConfiguration extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.authorizeRequests().antMatchers("/foo").permitAll().anyRequest().authenticated().and().formLogin().and().httpBasic();
        }
    }
}

