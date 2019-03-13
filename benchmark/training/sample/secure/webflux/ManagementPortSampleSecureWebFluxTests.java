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


import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.security.reactive.EndpointRequest;
import org.springframework.boot.actuate.autoconfigure.web.server.LocalManagementPort;
import org.springframework.boot.actuate.web.mappings.MappingsEndpoint;
import org.springframework.boot.autoconfigure.security.reactive.PathRequest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;


/**
 * Integration tests for separate management and main service ports.
 *
 * @author Madhura Bhave
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, properties = { "management.server.port=0" }, classes = { ManagementPortSampleSecureWebFluxTests.SecurityConfiguration.class, SampleSecureWebFluxApplication.class })
public class ManagementPortSampleSecureWebFluxTests {
    @LocalServerPort
    private int port = 9010;

    @LocalManagementPort
    private int managementPort = 9011;

    @Autowired
    private WebTestClient webClient;

    @Test
    public void testHome() {
        this.webClient.get().uri(("http://localhost:" + (this.port)), String.class).header("Authorization", ("basic " + (getBasicAuth()))).exchange().expectStatus().isOk().expectBody(String.class).isEqualTo("Hello user");
    }

    @Test
    public void actuatorPathOnMainPortShouldNotMatch() {
        this.webClient.get().uri((("http://localhost:" + (this.port)) + "/actuator"), String.class).exchange().expectStatus().isUnauthorized();
        this.webClient.get().uri((("http://localhost:" + (this.port)) + "/actuator/health"), String.class).exchange().expectStatus().isUnauthorized();
    }

    @Test
    public void testSecureActuator() {
        this.webClient.get().uri((("http://localhost:" + (this.managementPort)) + "/actuator/env"), String.class).exchange().expectStatus().isUnauthorized();
    }

    @Test
    public void testInsecureActuator() {
        String responseBody = this.webClient.get().uri((("http://localhost:" + (this.managementPort)) + "/actuator/health"), String.class).exchange().expectStatus().isOk().expectBody(String.class).returnResult().getResponseBody();
        Assertions.assertThat(responseBody).contains("\"status\":\"UP\"");
    }

    @Configuration
    static class SecurityConfiguration {
        @Bean
        public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
            return http.authorizeExchange().matchers(EndpointRequest.to("health", "info")).permitAll().matchers(EndpointRequest.toAnyEndpoint().excluding(MappingsEndpoint.class)).hasRole("ACTUATOR").matchers(PathRequest.toStaticResources().atCommonLocations()).permitAll().pathMatchers("/login").permitAll().anyExchange().authenticated().and().httpBasic().and().build();
        }
    }
}

