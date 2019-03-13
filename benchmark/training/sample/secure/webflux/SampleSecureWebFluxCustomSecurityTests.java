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
package sample.secure.webflux;


import HttpStatus.UNAUTHORIZED;
import MediaType.APPLICATION_JSON;
import SpringBootTest.WebEnvironment;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.security.reactive.EndpointRequest;
import org.springframework.boot.actuate.web.mappings.MappingsEndpoint;
import org.springframework.boot.autoconfigure.security.reactive.PathRequest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;


/**
 * Integration tests for a secure reactive application with custom security.
 *
 * @author Madhura Bhave
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = { SampleSecureWebFluxCustomSecurityTests.SecurityConfiguration.class, SampleSecureWebFluxApplication.class })
public class SampleSecureWebFluxCustomSecurityTests {
    @Autowired
    private WebTestClient webClient;

    @Test
    public void userDefinedMappingsSecure() {
        this.webClient.get().uri("/").accept(APPLICATION_JSON).exchange().expectStatus().isEqualTo(UNAUTHORIZED);
    }

    @Test
    public void healthAndInfoDoNotRequireAuthentication() {
        this.webClient.get().uri("/actuator/health").accept(APPLICATION_JSON).exchange().expectStatus().isOk();
        this.webClient.get().uri("/actuator/info").accept(APPLICATION_JSON).exchange().expectStatus().isOk();
    }

    @Test
    public void actuatorsSecuredByRole() {
        this.webClient.get().uri("/actuator/env").accept(APPLICATION_JSON).header("Authorization", ("basic " + (getBasicAuth()))).exchange().expectStatus().isForbidden();
    }

    @Test
    public void actuatorsAccessibleOnCorrectLogin() {
        this.webClient.get().uri("/actuator/env").accept(APPLICATION_JSON).header("Authorization", ("basic " + (getBasicAuthForAdmin()))).exchange().expectStatus().isOk();
    }

    @Test
    public void actuatorExcludedFromEndpointRequestMatcher() {
        this.webClient.get().uri("/actuator/mappings").accept(APPLICATION_JSON).header("Authorization", ("basic " + (getBasicAuth()))).exchange().expectStatus().isOk();
    }

    @Test
    public void staticResourceShouldBeAccessible() {
        this.webClient.get().uri("/css/bootstrap.min.css").accept(APPLICATION_JSON).exchange().expectStatus().isOk();
    }

    @Test
    public void actuatorLinksIsSecure() {
        this.webClient.get().uri("/actuator").accept(APPLICATION_JSON).exchange().expectStatus().isUnauthorized();
        this.webClient.get().uri("/actuator").accept(APPLICATION_JSON).header("Authorization", ("basic " + (getBasicAuthForAdmin()))).exchange().expectStatus().isOk();
    }

    @Configuration
    static class SecurityConfiguration {
        @SuppressWarnings("deprecation")
        @Bean
        public MapReactiveUserDetailsService userDetailsService() {
            return new MapReactiveUserDetailsService(User.withDefaultPasswordEncoder().username("user").password("password").authorities("ROLE_USER").build(), User.withDefaultPasswordEncoder().username("admin").password("admin").authorities("ROLE_ACTUATOR", "ROLE_USER").build());
        }

        @Bean
        public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
            return http.authorizeExchange().matchers(EndpointRequest.to("health", "info")).permitAll().matchers(EndpointRequest.toAnyEndpoint().excluding(MappingsEndpoint.class)).hasRole("ACTUATOR").matchers(PathRequest.toStaticResources().atCommonLocations()).permitAll().pathMatchers("/login").permitAll().anyExchange().authenticated().and().httpBasic().and().build();
        }
    }
}

