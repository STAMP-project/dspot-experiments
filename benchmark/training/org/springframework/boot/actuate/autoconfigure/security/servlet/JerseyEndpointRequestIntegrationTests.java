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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.ws.rs.core.MediaType.APPLICATION_JSON;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.model.Resource;
import org.junit.Test;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties;
import org.springframework.boot.actuate.endpoint.invoke.convert.ConversionServiceParameterValueMapper;
import org.springframework.boot.actuate.endpoint.web.EndpointMapping;
import org.springframework.boot.actuate.endpoint.web.EndpointMediaTypes;
import org.springframework.boot.actuate.endpoint.web.annotation.WebEndpointDiscoverer;
import org.springframework.boot.actuate.endpoint.web.jersey.JerseyEndpointResourceFactory;
import org.springframework.boot.autoconfigure.jersey.ResourceConfigCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.web.reactive.server.WebTestClient;


/**
 * Integration tests for {@link EndpointRequest} with Jersey.
 *
 * @author Madhura Bhave
 */
public class JerseyEndpointRequestIntegrationTests extends AbstractEndpointRequestIntegrationTests {
    @Test
    public void toLinksWhenApplicationPathSetShouldMatch() {
        getContextRunner().withPropertyValues("spring.jersey.application-path=/admin").run(( context) -> {
            WebTestClient webTestClient = getWebTestClient(context);
            webTestClient.get().uri("/admin/actuator/").exchange().expectStatus().isOk();
            webTestClient.get().uri("/admin/actuator").exchange().expectStatus().isOk();
        });
    }

    @Test
    public void toEndpointWhenApplicationPathSetShouldMatch() {
        getContextRunner().withPropertyValues("spring.jersey.application-path=/admin").run(( context) -> {
            WebTestClient webTestClient = getWebTestClient(context);
            webTestClient.get().uri("/admin/actuator/e1").exchange().expectStatus().isOk();
        });
    }

    @Test
    public void toAnyEndpointWhenApplicationPathSetShouldMatch() {
        getContextRunner().withPropertyValues("spring.jersey.application-path=/admin", "spring.security.user.password=password").run(( context) -> {
            WebTestClient webTestClient = getWebTestClient(context);
            webTestClient.get().uri("/admin/actuator/e2").exchange().expectStatus().isUnauthorized();
            webTestClient.get().uri("/admin/actuator/e2").header("Authorization", getBasicAuth()).exchange().expectStatus().isOk();
        });
    }

    @Configuration
    @EnableConfigurationProperties(WebEndpointProperties.class)
    static class JerseyEndpointConfiguration {
        private final ApplicationContext applicationContext;

        JerseyEndpointConfiguration(ApplicationContext applicationContext) {
            this.applicationContext = applicationContext;
        }

        @Bean
        public TomcatServletWebServerFactory tomcat() {
            return new TomcatServletWebServerFactory(0);
        }

        @Bean
        public ResourceConfig resourceConfig() {
            return new ResourceConfig();
        }

        @Bean
        public ResourceConfigCustomizer webEndpointRegistrar() {
            return this::customize;
        }

        private void customize(ResourceConfig config) {
            List<String> mediaTypes = Arrays.asList(APPLICATION_JSON, V2_JSON);
            EndpointMediaTypes endpointMediaTypes = new EndpointMediaTypes(mediaTypes, mediaTypes);
            WebEndpointDiscoverer discoverer = new WebEndpointDiscoverer(this.applicationContext, new ConversionServiceParameterValueMapper(), endpointMediaTypes, Arrays.asList(EndpointId::toString), Collections.emptyList(), Collections.emptyList());
            Collection<Resource> resources = new JerseyEndpointResourceFactory().createEndpointResources(new EndpointMapping("/actuator"), discoverer.getEndpoints(), endpointMediaTypes, new org.springframework.boot.actuate.endpoint.web.EndpointLinksResolver(discoverer.getEndpoints()));
            config.registerResources(new java.util.HashSet(resources));
        }
    }
}

