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
import org.junit.Test;
import org.springframework.boot.actuate.autoconfigure.audit.AuditAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.beans.BeansEndpointAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.endpoint.EndpointAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.web.server.ManagementContextAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.web.servlet.ServletManagementContextAutoConfiguration;
import org.springframework.boot.actuate.endpoint.web.annotation.RestControllerEndpoint;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.test.context.TestSecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;


/**
 * Integration tests for the Actuator's MVC {@link ControllerEndpoint controller
 * endpoints}.
 *
 * @author Phillip Webb
 * @author Andy Wilkinson
 */
public class ControllerEndpointWebMvcIntegrationTests {
    private AnnotationConfigWebApplicationContext context;

    @Test
    public void endpointsAreSecureByDefault() throws Exception {
        this.context = new AnnotationConfigWebApplicationContext();
        this.context.register(ControllerEndpointWebMvcIntegrationTests.SecureConfiguration.class, ControllerEndpointWebMvcIntegrationTests.ExampleController.class);
        MockMvc mockMvc = createSecureMockMvc();
        mockMvc.perform(get("/actuator/example").accept(APPLICATION_JSON)).andExpect(status().isUnauthorized());
    }

    @Test
    public void endpointsCanBeAccessed() throws Exception {
        TestSecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken("user", "N/A", "ROLE_ACTUATOR"));
        this.context = new AnnotationConfigWebApplicationContext();
        this.context.register(ControllerEndpointWebMvcIntegrationTests.SecureConfiguration.class, ControllerEndpointWebMvcIntegrationTests.ExampleController.class);
        TestPropertyValues.of("management.endpoints.web.base-path:/management", "management.endpoints.web.exposure.include=*").applyTo(this.context);
        MockMvc mockMvc = createSecureMockMvc();
        mockMvc.perform(get("/management/example")).andExpect(status().isOk());
    }

    @ImportAutoConfiguration({ JacksonAutoConfiguration.class, HttpMessageConvertersAutoConfiguration.class, EndpointAutoConfiguration.class, WebEndpointAutoConfiguration.class, ServletManagementContextAutoConfiguration.class, AuditAutoConfiguration.class, PropertyPlaceholderAutoConfiguration.class, WebMvcAutoConfiguration.class, ManagementContextAutoConfiguration.class, DispatcherServletAutoConfiguration.class, BeansEndpointAutoConfiguration.class })
    static class DefaultConfiguration {}

    @Import(ControllerEndpointWebMvcIntegrationTests.DefaultConfiguration.class)
    @ImportAutoConfiguration({ SecurityAutoConfiguration.class })
    static class SecureConfiguration {}

    @RestControllerEndpoint(id = "example")
    static class ExampleController {
        @GetMapping("/")
        public String example() {
            return "Example";
        }
    }
}

