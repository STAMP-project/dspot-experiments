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


import HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS;
import HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS;
import HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS;
import HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN;
import HttpHeaders.ACCESS_CONTROL_MAX_AGE;
import HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS;
import HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD;
import HttpHeaders.ORIGIN;
import org.junit.Test;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;


/**
 * Integration tests for the MVC actuator endpoints' CORS support
 *
 * @author Andy Wilkinson
 * @see WebMvcEndpointManagementContextConfiguration
 */
public class WebMvcEndpointCorsIntegrationTests {
    private AnnotationConfigWebApplicationContext context;

    @Test
    public void corsIsDisabledByDefault() throws Exception {
        createMockMvc().perform(options("/actuator/beans").header("Origin", "foo.example.com").header(ACCESS_CONTROL_REQUEST_METHOD, "GET")).andExpect(header().doesNotExist(ACCESS_CONTROL_ALLOW_ORIGIN));
    }

    @Test
    public void settingAllowedOriginsEnablesCors() throws Exception {
        TestPropertyValues.of("management.endpoints.web.cors.allowed-origins:foo.example.com").applyTo(this.context);
        createMockMvc().perform(options("/actuator/beans").header("Origin", "bar.example.com").header(ACCESS_CONTROL_REQUEST_METHOD, "GET")).andExpect(status().isForbidden());
        performAcceptedCorsRequest();
    }

    @Test
    public void maxAgeDefaultsTo30Minutes() throws Exception {
        TestPropertyValues.of("management.endpoints.web.cors.allowed-origins:foo.example.com").applyTo(this.context);
        performAcceptedCorsRequest().andExpect(header().string(ACCESS_CONTROL_MAX_AGE, "1800"));
    }

    @Test
    public void maxAgeCanBeConfigured() throws Exception {
        TestPropertyValues.of("management.endpoints.web.cors.allowed-origins:foo.example.com", "management.endpoints.web.cors.max-age: 2400").applyTo(this.context);
        performAcceptedCorsRequest().andExpect(header().string(ACCESS_CONTROL_MAX_AGE, "2400"));
    }

    @Test
    public void requestsWithDisallowedHeadersAreRejected() throws Exception {
        TestPropertyValues.of("management.endpoints.web.cors.allowed-origins:foo.example.com").applyTo(this.context);
        createMockMvc().perform(options("/actuator/beans").header("Origin", "foo.example.com").header(ACCESS_CONTROL_REQUEST_METHOD, "GET").header(ACCESS_CONTROL_REQUEST_HEADERS, "Alpha")).andExpect(status().isForbidden());
    }

    @Test
    public void allowedHeadersCanBeConfigured() throws Exception {
        TestPropertyValues.of("management.endpoints.web.cors.allowed-origins:foo.example.com", "management.endpoints.web.cors.allowed-headers:Alpha,Bravo").applyTo(this.context);
        createMockMvc().perform(options("/actuator/beans").header("Origin", "foo.example.com").header(ACCESS_CONTROL_REQUEST_METHOD, "GET").header(ACCESS_CONTROL_REQUEST_HEADERS, "Alpha")).andExpect(status().isOk()).andExpect(header().string(ACCESS_CONTROL_ALLOW_HEADERS, "Alpha"));
    }

    @Test
    public void requestsWithDisallowedMethodsAreRejected() throws Exception {
        TestPropertyValues.of("management.endpoints.web.cors.allowed-origins:foo.example.com").applyTo(this.context);
        createMockMvc().perform(options("/actuator/health").header(ORIGIN, "foo.example.com").header(ACCESS_CONTROL_REQUEST_METHOD, "PATCH")).andExpect(status().isForbidden());
    }

    @Test
    public void allowedMethodsCanBeConfigured() throws Exception {
        TestPropertyValues.of("management.endpoints.web.cors.allowed-origins:foo.example.com", "management.endpoints.web.cors.allowed-methods:GET,HEAD").applyTo(this.context);
        createMockMvc().perform(options("/actuator/beans").header(ORIGIN, "foo.example.com").header(ACCESS_CONTROL_REQUEST_METHOD, "HEAD")).andExpect(status().isOk()).andExpect(header().string(ACCESS_CONTROL_ALLOW_METHODS, "GET,HEAD"));
    }

    @Test
    public void credentialsCanBeAllowed() throws Exception {
        TestPropertyValues.of("management.endpoints.web.cors.allowed-origins:foo.example.com", "management.endpoints.web.cors.allow-credentials:true").applyTo(this.context);
        performAcceptedCorsRequest().andExpect(header().string(ACCESS_CONTROL_ALLOW_CREDENTIALS, "true"));
    }

    @Test
    public void credentialsCanBeDisabled() throws Exception {
        TestPropertyValues.of("management.endpoints.web.cors.allowed-origins:foo.example.com", "management.endpoints.web.cors.allow-credentials:false").applyTo(this.context);
        performAcceptedCorsRequest().andExpect(header().doesNotExist(ACCESS_CONTROL_ALLOW_CREDENTIALS));
    }
}

