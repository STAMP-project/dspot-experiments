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
package org.springframework.boot.actuate.autoconfigure.integrationtest;


import MediaType.APPLICATION_JSON;
import java.util.function.Supplier;
import javax.servlet.http.HttpServlet;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.springframework.boot.actuate.autoconfigure.audit.AuditAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.beans.BeansEndpointAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.endpoint.EndpointAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.web.server.ManagementContextAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.web.servlet.ServletManagementContextAutoConfiguration;
import org.springframework.boot.actuate.endpoint.web.EndpointServlet;
import org.springframework.boot.actuate.endpoint.web.annotation.ControllerEndpoint;
import org.springframework.boot.actuate.endpoint.web.annotation.RestControllerEndpoint;
import org.springframework.boot.actuate.endpoint.web.annotation.ServletEndpoint;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.autoconfigure.data.rest.RepositoryRestMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.hateoas.HypermediaAutoConfiguration;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.test.context.TestSecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;


/**
 * Integration tests for the Actuator's MVC endpoints.
 *
 * @author Andy Wilkinson
 */
public class WebMvcEndpointIntegrationTests {
    private AnnotationConfigWebApplicationContext context;

    @Test
    public void endpointsAreSecureByDefault() throws Exception {
        this.context = new AnnotationConfigWebApplicationContext();
        this.context.register(WebMvcEndpointIntegrationTests.SecureConfiguration.class);
        MockMvc mockMvc = createSecureMockMvc();
        mockMvc.perform(get("/actuator/beans").accept(APPLICATION_JSON)).andExpect(status().isUnauthorized());
    }

    @Test
    public void endpointsAreSecureByDefaultWithCustomBasePath() throws Exception {
        this.context = new AnnotationConfigWebApplicationContext();
        this.context.register(WebMvcEndpointIntegrationTests.SecureConfiguration.class);
        TestPropertyValues.of("management.endpoints.web.base-path:/management").applyTo(this.context);
        MockMvc mockMvc = createSecureMockMvc();
        mockMvc.perform(get("/management/beans").accept(APPLICATION_JSON)).andExpect(status().isUnauthorized());
    }

    @Test
    public void endpointsAreSecureWithActuatorRoleWithCustomBasePath() throws Exception {
        TestSecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken("user", "N/A", "ROLE_ACTUATOR"));
        this.context = new AnnotationConfigWebApplicationContext();
        this.context.register(WebMvcEndpointIntegrationTests.SecureConfiguration.class);
        TestPropertyValues.of("management.endpoints.web.base-path:/management", "management.endpoints.web.exposure.include=*").applyTo(this.context);
        MockMvc mockMvc = createSecureMockMvc();
        mockMvc.perform(get("/management/beans")).andExpect(status().isOk());
    }

    @Test
    public void linksAreProvidedToAllEndpointTypes() throws Exception {
        this.context = new AnnotationConfigWebApplicationContext();
        this.context.register(WebMvcEndpointIntegrationTests.DefaultConfiguration.class, WebMvcEndpointIntegrationTests.EndpointsConfiguration.class);
        TestPropertyValues.of("management.endpoints.web.exposure.include=*").applyTo(this.context);
        MockMvc mockMvc = doCreateMockMvc();
        mockMvc.perform(get("/actuator").accept("*/*")).andExpect(status().isOk()).andExpect(jsonPath("_links", Matchers.both(Matchers.hasKey("beans")).and(Matchers.hasKey("servlet")).and(Matchers.hasKey("restcontroller")).and(Matchers.hasKey("controller"))));
    }

    @ImportAutoConfiguration({ JacksonAutoConfiguration.class, HttpMessageConvertersAutoConfiguration.class, EndpointAutoConfiguration.class, WebEndpointAutoConfiguration.class, ServletManagementContextAutoConfiguration.class, AuditAutoConfiguration.class, PropertyPlaceholderAutoConfiguration.class, WebMvcAutoConfiguration.class, ManagementContextAutoConfiguration.class, AuditAutoConfiguration.class, DispatcherServletAutoConfiguration.class, BeansEndpointAutoConfiguration.class })
    static class DefaultConfiguration {}

    @Import(WebMvcEndpointIntegrationTests.SecureConfiguration.class)
    @ImportAutoConfiguration({ HypermediaAutoConfiguration.class })
    static class SpringHateoasConfiguration {}

    @Import(WebMvcEndpointIntegrationTests.SecureConfiguration.class)
    @ImportAutoConfiguration({ HypermediaAutoConfiguration.class, RepositoryRestMvcAutoConfiguration.class })
    static class SpringDataRestConfiguration {}

    @Import(WebMvcEndpointIntegrationTests.DefaultConfiguration.class)
    @ImportAutoConfiguration({ SecurityAutoConfiguration.class })
    static class SecureConfiguration {}

    @ServletEndpoint(id = "servlet")
    static class TestServletEndpoint implements Supplier<EndpointServlet> {
        @Override
        public EndpointServlet get() {
            return new EndpointServlet(new HttpServlet() {});
        }
    }

    @ControllerEndpoint(id = "controller")
    static class TestControllerEndpoint {}

    @RestControllerEndpoint(id = "restcontroller")
    static class TestRestControllerEndpoint {}

    @Configuration
    static class EndpointsConfiguration {
        @Bean
        WebMvcEndpointIntegrationTests.TestServletEndpoint testServletEndpoint() {
            return new WebMvcEndpointIntegrationTests.TestServletEndpoint();
        }

        @Bean
        WebMvcEndpointIntegrationTests.TestControllerEndpoint testControllerEndpoint() {
            return new WebMvcEndpointIntegrationTests.TestControllerEndpoint();
        }

        @Bean
        WebMvcEndpointIntegrationTests.TestRestControllerEndpoint testRestControllerEndpoint() {
            return new WebMvcEndpointIntegrationTests.TestRestControllerEndpoint();
        }
    }
}

