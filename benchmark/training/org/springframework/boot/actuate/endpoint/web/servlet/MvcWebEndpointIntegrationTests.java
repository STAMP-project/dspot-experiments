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
package org.springframework.boot.actuate.endpoint.web.servlet;


import HttpStatus.PARTIAL_CONTENT;
import MediaType.APPLICATION_JSON;
import MediaType.APPLICATION_OCTET_STREAM;
import java.io.IOException;
import java.util.Arrays;
import java.util.function.Consumer;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Test;
import org.springframework.boot.actuate.endpoint.web.EndpointMediaTypes;
import org.springframework.boot.actuate.endpoint.web.annotation.AbstractWebEndpointIntegrationTests;
import org.springframework.boot.actuate.endpoint.web.annotation.WebEndpointDiscoverer;
import org.springframework.boot.actuate.endpoint.web.annotation.WebTestClient;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.filter.OncePerRequestFilter;


/**
 * Integration tests for web endpoints exposed using Spring MVC.
 *
 * @author Andy Wilkinson
 * @see WebMvcEndpointHandlerMapping
 */
public class MvcWebEndpointIntegrationTests extends AbstractWebEndpointIntegrationTests<AnnotationConfigServletWebServerApplicationContext> {
    public MvcWebEndpointIntegrationTests() {
        super(MvcWebEndpointIntegrationTests::createApplicationContext, MvcWebEndpointIntegrationTests::applyAuthenticatedConfiguration);
    }

    @Test
    public void responseToOptionsRequestIncludesCorsHeaders() {
        load(AbstractWebEndpointIntegrationTests.TestEndpointConfiguration.class, ( client) -> client.options().uri("/test").accept(APPLICATION_JSON).header("Access-Control-Request-Method", "POST").header("Origin", "http://example.com").exchange().expectStatus().isOk().expectHeader().valueEquals("Access-Control-Allow-Origin", "http://example.com").expectHeader().valueEquals("Access-Control-Allow-Methods", "GET,POST"));
    }

    @Test
    public void readOperationsThatReturnAResourceSupportRangeRequests() {
        load(AbstractWebEndpointIntegrationTests.ResourceEndpointConfiguration.class, ( client) -> {
            byte[] responseBody = client.get().uri("/resource").header("Range", "bytes=0-3").exchange().expectStatus().isEqualTo(PARTIAL_CONTENT).expectHeader().contentType(APPLICATION_OCTET_STREAM).returnResult(byte[].class).getResponseBodyContent();
            assertThat(responseBody).containsExactly(0, 1, 2, 3);
        });
    }

    @Test
    public void matchWhenRequestHasTrailingSlashShouldNotBeNull() {
        assertThat(getMatchResult("/spring/")).isNotNull();
    }

    @Test
    public void matchWhenRequestHasSuffixShouldBeNull() {
        assertThat(getMatchResult("/spring.do")).isNull();
    }

    @Configuration
    @ImportAutoConfiguration({ JacksonAutoConfiguration.class, HttpMessageConvertersAutoConfiguration.class, ServletWebServerFactoryAutoConfiguration.class, WebMvcAutoConfiguration.class, DispatcherServletAutoConfiguration.class, ErrorMvcAutoConfiguration.class })
    static class WebMvcConfiguration {
        @Bean
        public TomcatServletWebServerFactory tomcat() {
            return new TomcatServletWebServerFactory(0);
        }

        @Bean
        public WebMvcEndpointHandlerMapping webEndpointHandlerMapping(Environment environment, WebEndpointDiscoverer endpointDiscoverer, EndpointMediaTypes endpointMediaTypes) {
            CorsConfiguration corsConfiguration = new CorsConfiguration();
            corsConfiguration.setAllowedOrigins(Arrays.asList("http://example.com"));
            corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST"));
            return new WebMvcEndpointHandlerMapping(new org.springframework.boot.actuate.endpoint.web.EndpointMapping(environment.getProperty("endpointPath")), endpointDiscoverer.getEndpoints(), endpointMediaTypes, corsConfiguration, new org.springframework.boot.actuate.endpoint.web.EndpointLinksResolver(endpointDiscoverer.getEndpoints()));
        }
    }

    @Configuration
    static class AuthenticatedConfiguration {
        @Bean
        public Filter securityFilter() {
            return new OncePerRequestFilter() {
                @Override
                protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
                    SecurityContext context = SecurityContextHolder.createEmptyContext();
                    context.setAuthentication(new org.springframework.security.authentication.UsernamePasswordAuthenticationToken("Alice", "secret", Arrays.asList(new SimpleGrantedAuthority("ROLE_ACTUATOR"))));
                    SecurityContextHolder.setContext(context);
                    try {
                        filterChain.doFilter(new org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper(request, "ROLE_"), response);
                    } finally {
                        SecurityContextHolder.clearContext();
                    }
                }
            };
        }
    }
}

