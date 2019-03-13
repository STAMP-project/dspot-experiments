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
package org.springframework.boot.actuate.autoconfigure.cloudfoundry.servlet;


import AccessLevel.FULL;
import AccessLevel.RESTRICTED;
import HttpStatus.FORBIDDEN;
import HttpStatus.OK;
import MediaType.APPLICATION_JSON;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.function.Consumer;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.boot.actuate.autoconfigure.cloudfoundry.CloudFoundryAuthorizationException;
import org.springframework.boot.actuate.autoconfigure.cloudfoundry.CloudFoundryAuthorizationException.Reason;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.boot.actuate.endpoint.invoke.ParameterValueMapper;
import org.springframework.boot.actuate.endpoint.web.EndpointMapping;
import org.springframework.boot.actuate.endpoint.web.EndpointMediaTypes;
import org.springframework.boot.actuate.endpoint.web.annotation.WebEndpointDiscoverer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;


/**
 * Integration tests for web endpoints exposed using Spring MVC on CloudFoundry.
 *
 * @author Madhura Bhave
 */
public class CloudFoundryMvcWebEndpointIntegrationTests {
    private static TokenValidator tokenValidator = Mockito.mock(TokenValidator.class);

    private static CloudFoundrySecurityService securityService = Mockito.mock(CloudFoundrySecurityService.class);

    @Test
    public void operationWithSecurityInterceptorForbidden() {
        BDDMockito.given(CloudFoundryMvcWebEndpointIntegrationTests.securityService.getAccessLevel(ArgumentMatchers.any(), ArgumentMatchers.eq("app-id"))).willReturn(RESTRICTED);
        load(CloudFoundryMvcWebEndpointIntegrationTests.TestEndpointConfiguration.class, ( client) -> client.get().uri("/cfApplication/test").accept(APPLICATION_JSON).header("Authorization", ("bearer " + (mockAccessToken()))).exchange().expectStatus().isEqualTo(FORBIDDEN));
    }

    @Test
    public void operationWithSecurityInterceptorSuccess() {
        BDDMockito.given(CloudFoundryMvcWebEndpointIntegrationTests.securityService.getAccessLevel(ArgumentMatchers.any(), ArgumentMatchers.eq("app-id"))).willReturn(FULL);
        load(CloudFoundryMvcWebEndpointIntegrationTests.TestEndpointConfiguration.class, ( client) -> client.get().uri("/cfApplication/test").accept(APPLICATION_JSON).header("Authorization", ("bearer " + (mockAccessToken()))).exchange().expectStatus().isEqualTo(OK));
    }

    @Test
    public void responseToOptionsRequestIncludesCorsHeaders() {
        load(CloudFoundryMvcWebEndpointIntegrationTests.TestEndpointConfiguration.class, ( client) -> client.options().uri("/cfApplication/test").accept(APPLICATION_JSON).header("Access-Control-Request-Method", "POST").header("Origin", "http://example.com").exchange().expectStatus().isOk().expectHeader().valueEquals("Access-Control-Allow-Origin", "http://example.com").expectHeader().valueEquals("Access-Control-Allow-Methods", "GET,POST"));
    }

    @Test
    public void linksToOtherEndpointsWithFullAccess() {
        BDDMockito.given(CloudFoundryMvcWebEndpointIntegrationTests.securityService.getAccessLevel(ArgumentMatchers.any(), ArgumentMatchers.eq("app-id"))).willReturn(FULL);
        load(CloudFoundryMvcWebEndpointIntegrationTests.TestEndpointConfiguration.class, ( client) -> client.get().uri("/cfApplication").accept(APPLICATION_JSON).header("Authorization", ("bearer " + (mockAccessToken()))).exchange().expectStatus().isOk().expectBody().jsonPath("_links.length()").isEqualTo(5).jsonPath("_links.self.href").isNotEmpty().jsonPath("_links.self.templated").isEqualTo(false).jsonPath("_links.info.href").isNotEmpty().jsonPath("_links.info.templated").isEqualTo(false).jsonPath("_links.env.href").isNotEmpty().jsonPath("_links.env.templated").isEqualTo(false).jsonPath("_links.test.href").isNotEmpty().jsonPath("_links.test.templated").isEqualTo(false).jsonPath("_links.test-part.href").isNotEmpty().jsonPath("_links.test-part.templated").isEqualTo(true));
    }

    @Test
    public void linksToOtherEndpointsForbidden() {
        CloudFoundryAuthorizationException exception = new CloudFoundryAuthorizationException(Reason.INVALID_TOKEN, "invalid-token");
        BDDMockito.willThrow(exception).given(CloudFoundryMvcWebEndpointIntegrationTests.tokenValidator).validate(ArgumentMatchers.any());
        load(CloudFoundryMvcWebEndpointIntegrationTests.TestEndpointConfiguration.class, ( client) -> client.get().uri("/cfApplication").accept(APPLICATION_JSON).header("Authorization", ("bearer " + (mockAccessToken()))).exchange().expectStatus().isUnauthorized());
    }

    @Test
    public void linksToOtherEndpointsWithRestrictedAccess() {
        BDDMockito.given(CloudFoundryMvcWebEndpointIntegrationTests.securityService.getAccessLevel(ArgumentMatchers.any(), ArgumentMatchers.eq("app-id"))).willReturn(RESTRICTED);
        load(CloudFoundryMvcWebEndpointIntegrationTests.TestEndpointConfiguration.class, ( client) -> client.get().uri("/cfApplication").accept(APPLICATION_JSON).header("Authorization", ("bearer " + (mockAccessToken()))).exchange().expectStatus().isOk().expectBody().jsonPath("_links.length()").isEqualTo(2).jsonPath("_links.self.href").isNotEmpty().jsonPath("_links.self.templated").isEqualTo(false).jsonPath("_links.info.href").isNotEmpty().jsonPath("_links.info.templated").isEqualTo(false).jsonPath("_links.env").doesNotExist().jsonPath("_links.test").doesNotExist().jsonPath("_links.test-part").doesNotExist());
    }

    @Configuration
    @EnableWebMvc
    static class CloudFoundryMvcConfiguration {
        @Bean
        public CloudFoundrySecurityInterceptor interceptor() {
            return new CloudFoundrySecurityInterceptor(CloudFoundryMvcWebEndpointIntegrationTests.tokenValidator, CloudFoundryMvcWebEndpointIntegrationTests.securityService, "app-id");
        }

        @Bean
        public EndpointMediaTypes EndpointMediaTypes() {
            return new EndpointMediaTypes(Collections.singletonList("application/json"), Collections.singletonList("application/json"));
        }

        @Bean
        public CloudFoundryWebEndpointServletHandlerMapping cloudFoundryWebEndpointServletHandlerMapping(WebEndpointDiscoverer webEndpointDiscoverer, EndpointMediaTypes endpointMediaTypes, CloudFoundrySecurityInterceptor interceptor) {
            CorsConfiguration corsConfiguration = new CorsConfiguration();
            corsConfiguration.setAllowedOrigins(Arrays.asList("http://example.com"));
            corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST"));
            return new CloudFoundryWebEndpointServletHandlerMapping(new EndpointMapping("/cfApplication"), webEndpointDiscoverer.getEndpoints(), endpointMediaTypes, corsConfiguration, interceptor, new org.springframework.boot.actuate.endpoint.web.EndpointLinksResolver(webEndpointDiscoverer.getEndpoints()));
        }

        @Bean
        public WebEndpointDiscoverer webEndpointDiscoverer(ApplicationContext applicationContext, EndpointMediaTypes endpointMediaTypes) {
            ParameterValueMapper parameterMapper = new org.springframework.boot.actuate.endpoint.invoke.convert.ConversionServiceParameterValueMapper(DefaultConversionService.getSharedInstance());
            return new WebEndpointDiscoverer(applicationContext, parameterMapper, endpointMediaTypes, null, Collections.emptyList(), Collections.emptyList());
        }

        @Bean
        public CloudFoundryMvcWebEndpointIntegrationTests.EndpointDelegate endpointDelegate() {
            return Mockito.mock(CloudFoundryMvcWebEndpointIntegrationTests.EndpointDelegate.class);
        }

        @Bean
        public TomcatServletWebServerFactory tomcat() {
            return new TomcatServletWebServerFactory(0);
        }

        @Bean
        public DispatcherServlet dispatcherServlet() {
            return new DispatcherServlet();
        }
    }

    @Endpoint(id = "test")
    static class TestEndpoint {
        private final CloudFoundryMvcWebEndpointIntegrationTests.EndpointDelegate endpointDelegate;

        TestEndpoint(CloudFoundryMvcWebEndpointIntegrationTests.EndpointDelegate endpointDelegate) {
            this.endpointDelegate = endpointDelegate;
        }

        @ReadOperation
        public Map<String, Object> readAll() {
            return Collections.singletonMap("All", true);
        }

        @ReadOperation
        public Map<String, Object> readPart(@Selector
        String part) {
            return Collections.singletonMap("part", part);
        }

        @WriteOperation
        public void write(String foo, String bar) {
            this.endpointDelegate.write(foo, bar);
        }
    }

    @Endpoint(id = "env")
    static class TestEnvEndpoint {
        @ReadOperation
        public Map<String, Object> readAll() {
            return Collections.singletonMap("All", true);
        }
    }

    @Endpoint(id = "info")
    static class TestInfoEndpoint {
        @ReadOperation
        public Map<String, Object> readAll() {
            return Collections.singletonMap("All", true);
        }
    }

    @Configuration
    @Import(CloudFoundryMvcWebEndpointIntegrationTests.CloudFoundryMvcConfiguration.class)
    protected static class TestEndpointConfiguration {
        @Bean
        public CloudFoundryMvcWebEndpointIntegrationTests.TestEndpoint testEndpoint(CloudFoundryMvcWebEndpointIntegrationTests.EndpointDelegate endpointDelegate) {
            return new CloudFoundryMvcWebEndpointIntegrationTests.TestEndpoint(endpointDelegate);
        }

        @Bean
        public CloudFoundryMvcWebEndpointIntegrationTests.TestInfoEndpoint testInfoEnvEndpoint() {
            return new CloudFoundryMvcWebEndpointIntegrationTests.TestInfoEndpoint();
        }

        @Bean
        public CloudFoundryMvcWebEndpointIntegrationTests.TestEnvEndpoint testEnvEndpoint() {
            return new CloudFoundryMvcWebEndpointIntegrationTests.TestEnvEndpoint();
        }
    }

    public interface EndpointDelegate {
        void write();

        void write(String foo, String bar);
    }
}

