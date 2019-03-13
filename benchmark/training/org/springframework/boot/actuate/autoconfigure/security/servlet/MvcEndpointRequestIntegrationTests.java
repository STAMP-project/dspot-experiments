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
package org.springframework.boot.actuate.autoconfigure.security.servlet;


import ActuatorMediaType.V2_JSON;
import MediaType.APPLICATION_JSON_VALUE;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties;
import org.springframework.boot.actuate.endpoint.invoke.convert.ConversionServiceParameterValueMapper;
import org.springframework.boot.actuate.endpoint.web.EndpointMapping;
import org.springframework.boot.actuate.endpoint.web.EndpointMediaTypes;
import org.springframework.boot.actuate.endpoint.web.annotation.WebEndpointDiscoverer;
import org.springframework.boot.actuate.endpoint.web.servlet.WebMvcEndpointHandlerMapping;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.cors.CorsConfiguration;


/**
 * Integration tests for {@link EndpointRequest} with Spring MVC.
 *
 * @author Madhura Bhave
 */
public class MvcEndpointRequestIntegrationTests extends AbstractEndpointRequestIntegrationTests {
    @Test
    public void toLinksWhenServletPathSetShouldMatch() {
        getContextRunner().withPropertyValues("spring.mvc.servlet.path=/admin").run(( context) -> {
            WebTestClient webTestClient = getWebTestClient(context);
            webTestClient.get().uri("/admin/actuator/").exchange().expectStatus().isOk();
            webTestClient.get().uri("/admin/actuator").exchange().expectStatus().isOk();
        });
    }

    @Test
    public void toEndpointWhenServletPathSetShouldMatch() {
        getContextRunner().withPropertyValues("spring.mvc.servlet.path=/admin").run(( context) -> {
            WebTestClient webTestClient = getWebTestClient(context);
            webTestClient.get().uri("/admin/actuator/e1").exchange().expectStatus().isOk();
        });
    }

    @Test
    public void toAnyEndpointWhenServletPathSetShouldMatch() {
        getContextRunner().withPropertyValues("spring.mvc.servlet.path=/admin", "spring.security.user.password=password").run(( context) -> {
            WebTestClient webTestClient = getWebTestClient(context);
            webTestClient.get().uri("/admin/actuator/e2").exchange().expectStatus().isUnauthorized();
            webTestClient.get().uri("/admin/actuator/e2").header("Authorization", getBasicAuth()).exchange().expectStatus().isOk();
        });
    }

    @Configuration
    @EnableConfigurationProperties(WebEndpointProperties.class)
    static class WebMvcEndpointConfiguration {
        private final ApplicationContext applicationContext;

        WebMvcEndpointConfiguration(ApplicationContext applicationContext) {
            this.applicationContext = applicationContext;
        }

        @Bean
        public TomcatServletWebServerFactory tomcat() {
            return new TomcatServletWebServerFactory(0);
        }

        @Bean
        public WebMvcEndpointHandlerMapping webEndpointServletHandlerMapping() {
            List<String> mediaTypes = Arrays.asList(APPLICATION_JSON_VALUE, V2_JSON);
            EndpointMediaTypes endpointMediaTypes = new EndpointMediaTypes(mediaTypes, mediaTypes);
            WebEndpointDiscoverer discoverer = new WebEndpointDiscoverer(this.applicationContext, new ConversionServiceParameterValueMapper(), endpointMediaTypes, Arrays.asList(EndpointId::toString), Collections.emptyList(), Collections.emptyList());
            return new WebMvcEndpointHandlerMapping(new EndpointMapping("/actuator"), discoverer.getEndpoints(), endpointMediaTypes, new CorsConfiguration(), new org.springframework.boot.actuate.endpoint.web.EndpointLinksResolver(discoverer.getEndpoints()));
        }
    }
}

