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
import org.junit.Test;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.boot.web.reactive.context.AnnotationConfigReactiveWebApplicationContext;


/**
 * Integration tests for the WebFlux actuator endpoints' CORS support
 *
 * @author Brian Clozel
 * @see WebFluxEndpointManagementContextConfiguration
 */
public class WebFluxEndpointCorsIntegrationTests {
    private AnnotationConfigReactiveWebApplicationContext context;

    @Test
    public void corsIsDisabledByDefault() {
        createWebTestClient().options().uri("/actuator/beans").header("Origin", "spring.example.org").header(ACCESS_CONTROL_REQUEST_METHOD, "GET").exchange().expectStatus().isForbidden().expectHeader().doesNotExist(ACCESS_CONTROL_ALLOW_ORIGIN);
    }

    @Test
    public void settingAllowedOriginsEnablesCors() {
        TestPropertyValues.of("management.endpoints.web.cors.allowed-origins:spring.example.org").applyTo(this.context);
        createWebTestClient().options().uri("/actuator/beans").header("Origin", "test.example.org").header(ACCESS_CONTROL_REQUEST_METHOD, "GET").exchange().expectStatus().isForbidden();
        performAcceptedCorsRequest("/actuator/beans");
    }

    @Test
    public void maxAgeDefaultsTo30Minutes() {
        TestPropertyValues.of("management.endpoints.web.cors.allowed-origins:spring.example.org").applyTo(this.context);
        performAcceptedCorsRequest("/actuator/beans").expectHeader().valueEquals(ACCESS_CONTROL_MAX_AGE, "1800");
    }

    @Test
    public void maxAgeCanBeConfigured() {
        TestPropertyValues.of("management.endpoints.web.cors.allowed-origins:spring.example.org", "management.endpoints.web.cors.max-age: 2400").applyTo(this.context);
        performAcceptedCorsRequest("/actuator/beans").expectHeader().valueEquals(ACCESS_CONTROL_MAX_AGE, "2400");
    }

    @Test
    public void requestsWithDisallowedHeadersAreRejected() {
        TestPropertyValues.of("management.endpoints.web.cors.allowed-origins:spring.example.org").applyTo(this.context);
        createWebTestClient().options().uri("/actuator/beans").header("Origin", "spring.example.org").header(ACCESS_CONTROL_REQUEST_METHOD, "GET").header(ACCESS_CONTROL_REQUEST_HEADERS, "Alpha").exchange().expectStatus().isForbidden();
    }

    @Test
    public void allowedHeadersCanBeConfigured() {
        TestPropertyValues.of("management.endpoints.web.cors.allowed-origins:spring.example.org", "management.endpoints.web.cors.allowed-headers:Alpha,Bravo").applyTo(this.context);
        createWebTestClient().options().uri("/actuator/beans").header("Origin", "spring.example.org").header(ACCESS_CONTROL_REQUEST_METHOD, "GET").header(ACCESS_CONTROL_REQUEST_HEADERS, "Alpha").exchange().expectStatus().isOk().expectHeader().valueEquals(ACCESS_CONTROL_ALLOW_HEADERS, "Alpha");
    }

    @Test
    public void requestsWithDisallowedMethodsAreRejected() {
        TestPropertyValues.of("management.endpoints.web.cors.allowed-origins:spring.example.org").applyTo(this.context);
        createWebTestClient().options().uri("/actuator/beans").header("Origin", "spring.example.org").header(ACCESS_CONTROL_REQUEST_METHOD, "PATCH").exchange().expectStatus().isForbidden();
    }

    @Test
    public void allowedMethodsCanBeConfigured() {
        TestPropertyValues.of("management.endpoints.web.cors.allowed-origins:spring.example.org", "management.endpoints.web.cors.allowed-methods:GET,HEAD").applyTo(this.context);
        createWebTestClient().options().uri("/actuator/beans").header("Origin", "spring.example.org").header(ACCESS_CONTROL_REQUEST_METHOD, "HEAD").exchange().expectStatus().isOk().expectHeader().valueEquals(ACCESS_CONTROL_ALLOW_METHODS, "GET,HEAD");
    }

    @Test
    public void credentialsCanBeAllowed() {
        TestPropertyValues.of("management.endpoints.web.cors.allowed-origins:spring.example.org", "management.endpoints.web.cors.allow-credentials:true").applyTo(this.context);
        performAcceptedCorsRequest("/actuator/beans").expectHeader().valueEquals(ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
    }

    @Test
    public void credentialsCanBeDisabled() {
        TestPropertyValues.of("management.endpoints.web.cors.allowed-origins:spring.example.org", "management.endpoints.web.cors.allow-credentials:false").applyTo(this.context);
        performAcceptedCorsRequest("/actuator/beans").expectHeader().doesNotExist(ACCESS_CONTROL_ALLOW_CREDENTIALS);
    }
}

