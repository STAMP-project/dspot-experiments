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


import java.util.Arrays;
import org.junit.Test;
import org.springframework.boot.actuate.autoconfigure.endpoint.EndpointAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.health.HealthEndpointAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.health.HealthIndicatorAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.web.server.ManagementContextAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.web.servlet.ServletManagementContextAutoConfiguration;
import org.springframework.boot.actuate.endpoint.EndpointId;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.web.EndpointMapping;
import org.springframework.boot.actuate.endpoint.web.ExposableWebEndpoint;
import org.springframework.boot.actuate.endpoint.web.WebOperation;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;


/**
 * Tests for {@link CloudFoundryActuatorAutoConfiguration}.
 *
 * @author Madhura Bhave
 */
public class CloudFoundryActuatorAutoConfigurationTests {
    private final WebApplicationContextRunner contextRunner = new WebApplicationContextRunner().withConfiguration(AutoConfigurations.of(SecurityAutoConfiguration.class, WebMvcAutoConfiguration.class, JacksonAutoConfiguration.class, DispatcherServletAutoConfiguration.class, HttpMessageConvertersAutoConfiguration.class, PropertyPlaceholderAutoConfiguration.class, RestTemplateAutoConfiguration.class, ManagementContextAutoConfiguration.class, ServletManagementContextAutoConfiguration.class, EndpointAutoConfiguration.class, WebEndpointAutoConfiguration.class, CloudFoundryActuatorAutoConfiguration.class));

    @Test
    public void cloudFoundryPlatformActive() {
        this.contextRunner.withPropertyValues("VCAP_APPLICATION:---", "vcap.application.application_id:my-app-id", "vcap.application.cf_api:http://my-cloud-controller.com").run(( context) -> {
            CloudFoundryWebEndpointServletHandlerMapping handlerMapping = getHandlerMapping(context);
            EndpointMapping endpointMapping = ((EndpointMapping) (ReflectionTestUtils.getField(handlerMapping, "endpointMapping")));
            assertThat(endpointMapping.getPath()).isEqualTo("/cloudfoundryapplication");
            CorsConfiguration corsConfiguration = ((CorsConfiguration) (ReflectionTestUtils.getField(handlerMapping, "corsConfiguration")));
            assertThat(corsConfiguration.getAllowedOrigins()).contains("*");
            assertThat(corsConfiguration.getAllowedMethods()).containsAll(Arrays.asList(HttpMethod.GET.name(), HttpMethod.POST.name()));
            assertThat(corsConfiguration.getAllowedHeaders()).containsAll(Arrays.asList("Authorization", "X-Cf-App-Instance", "Content-Type"));
        });
    }

    @Test
    public void cloudfoundryapplicationProducesActuatorMediaType() throws Exception {
        this.contextRunner.withPropertyValues("VCAP_APPLICATION:---", "vcap.application.application_id:my-app-id", "vcap.application.cf_api:http://my-cloud-controller.com").run(( context) -> {
            MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
            mockMvc.perform(get("/cloudfoundryapplication")).andExpect(header().string("Content-Type", (ActuatorMediaType.V2_JSON + ";charset=UTF-8")));
        });
    }

    @Test
    public void cloudFoundryPlatformActiveSetsApplicationId() {
        this.contextRunner.withPropertyValues("VCAP_APPLICATION:---", "vcap.application.application_id:my-app-id", "vcap.application.cf_api:http://my-cloud-controller.com").run(( context) -> {
            CloudFoundryWebEndpointServletHandlerMapping handlerMapping = getHandlerMapping(context);
            Object interceptor = ReflectionTestUtils.getField(handlerMapping, "securityInterceptor");
            String applicationId = ((String) (ReflectionTestUtils.getField(interceptor, "applicationId")));
            assertThat(applicationId).isEqualTo("my-app-id");
        });
    }

    @Test
    public void cloudFoundryPlatformActiveSetsCloudControllerUrl() {
        this.contextRunner.withPropertyValues("VCAP_APPLICATION:---", "vcap.application.application_id:my-app-id", "vcap.application.cf_api:http://my-cloud-controller.com").run(( context) -> {
            CloudFoundryWebEndpointServletHandlerMapping handlerMapping = getHandlerMapping(context);
            Object interceptor = ReflectionTestUtils.getField(handlerMapping, "securityInterceptor");
            Object interceptorSecurityService = ReflectionTestUtils.getField(interceptor, "cloudFoundrySecurityService");
            String cloudControllerUrl = ((String) (ReflectionTestUtils.getField(interceptorSecurityService, "cloudControllerUrl")));
            assertThat(cloudControllerUrl).isEqualTo("http://my-cloud-controller.com");
        });
    }

    @Test
    public void skipSslValidation() {
        this.contextRunner.withPropertyValues("VCAP_APPLICATION:---", "vcap.application.application_id:my-app-id", "vcap.application.cf_api:http://my-cloud-controller.com", "management.cloudfoundry.skip-ssl-validation:true").run(( context) -> {
            CloudFoundryWebEndpointServletHandlerMapping handlerMapping = getHandlerMapping(context);
            Object interceptor = ReflectionTestUtils.getField(handlerMapping, "securityInterceptor");
            Object interceptorSecurityService = ReflectionTestUtils.getField(interceptor, "cloudFoundrySecurityService");
            RestTemplate restTemplate = ((RestTemplate) (ReflectionTestUtils.getField(interceptorSecurityService, "restTemplate")));
            assertThat(restTemplate.getRequestFactory()).isInstanceOf(.class);
        });
    }

    @Test
    public void cloudFoundryPlatformActiveAndCloudControllerUrlNotPresent() {
        this.contextRunner.withPropertyValues("VCAP_APPLICATION:---", "vcap.application.application_id:my-app-id").run(( context) -> {
            CloudFoundryWebEndpointServletHandlerMapping handlerMapping = getHandlerMapping(context);
            Object securityInterceptor = ReflectionTestUtils.getField(handlerMapping, "securityInterceptor");
            Object interceptorSecurityService = ReflectionTestUtils.getField(securityInterceptor, "cloudFoundrySecurityService");
            assertThat(interceptorSecurityService).isNull();
        });
    }

    @Test
    public void cloudFoundryPathsIgnoredBySpringSecurity() {
        this.contextRunner.withPropertyValues("VCAP_APPLICATION:---", "vcap.application.application_id:my-app-id").run(( context) -> {
            FilterChainProxy securityFilterChain = ((FilterChainProxy) (context.getBean(BeanIds.SPRING_SECURITY_FILTER_CHAIN)));
            SecurityFilterChain chain = securityFilterChain.getFilterChains().get(0);
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setServletPath("/cloudfoundryapplication/my-path");
            assertThat(chain.getFilters()).isEmpty();
            assertThat(chain.matches(request)).isTrue();
            request.setServletPath("/some-other-path");
            assertThat(chain.matches(request)).isFalse();
        });
    }

    @Test
    public void cloudFoundryPlatformInactive() {
        this.contextRunner.withPropertyValues().run(( context) -> assertThat(context.containsBean("cloudFoundryWebEndpointServletHandlerMapping")).isFalse());
    }

    @Test
    public void cloudFoundryManagementEndpointsDisabled() {
        this.contextRunner.withPropertyValues("VCAP_APPLICATION=---", "management.cloudfoundry.enabled:false").run(( context) -> assertThat(context.containsBean("cloudFoundryEndpointHandlerMapping")).isFalse());
    }

    @Test
    public void allEndpointsAvailableUnderCloudFoundryWithoutExposeAllOnWeb() {
        this.contextRunner.withUserConfiguration(CloudFoundryActuatorAutoConfigurationTests.TestConfiguration.class).withPropertyValues("VCAP_APPLICATION:---", "vcap.application.application_id:my-app-id", "vcap.application.cf_api:http://my-cloud-controller.com").run(( context) -> {
            CloudFoundryWebEndpointServletHandlerMapping handlerMapping = getHandlerMapping(context);
            Collection<ExposableWebEndpoint> endpoints = handlerMapping.getEndpoints();
            assertThat(endpoints.stream().filter(( candidate) -> EndpointId.of("test").equals(candidate.getEndpointId())).findFirst()).isNotEmpty();
        });
    }

    @Test
    public void endpointPathCustomizationIsNotApplied() {
        this.contextRunner.withPropertyValues("VCAP_APPLICATION:---", "vcap.application.application_id:my-app-id", "vcap.application.cf_api:http://my-cloud-controller.com", "management.endpoints.web.path-mapping.test=custom").withUserConfiguration(CloudFoundryActuatorAutoConfigurationTests.TestConfiguration.class).run(( context) -> {
            CloudFoundryWebEndpointServletHandlerMapping handlerMapping = getHandlerMapping(context);
            Collection<ExposableWebEndpoint> endpoints = handlerMapping.getEndpoints();
            ExposableWebEndpoint endpoint = endpoints.stream().filter(( candidate) -> EndpointId.of("test").equals(candidate.getEndpointId())).findFirst().get();
            Collection<WebOperation> operations = endpoint.getOperations();
            assertThat(operations).hasSize(1);
            assertThat(operations.iterator().next().getRequestPredicate().getPath()).isEqualTo("test");
        });
    }

    @Test
    public void healthEndpointInvokerShouldBeCloudFoundryWebExtension() {
        this.contextRunner.withPropertyValues("VCAP_APPLICATION:---", "vcap.application.application_id:my-app-id", "vcap.application.cf_api:http://my-cloud-controller.com").withConfiguration(AutoConfigurations.of(HealthIndicatorAutoConfiguration.class, HealthEndpointAutoConfiguration.class)).run(( context) -> {
            Collection<ExposableWebEndpoint> endpoints = context.getBean("cloudFoundryWebEndpointServletHandlerMapping", .class).getEndpoints();
            ExposableWebEndpoint endpoint = endpoints.iterator().next();
            assertThat(endpoint.getOperations()).hasSize(3);
            WebOperation webOperation = findOperationWithRequestPath(endpoint, "health");
            Object invoker = ReflectionTestUtils.getField(webOperation, "invoker");
            assertThat(ReflectionTestUtils.getField(invoker, "target")).isInstanceOf(.class);
        });
    }

    @Configuration
    static class TestConfiguration {
        @Bean
        public CloudFoundryActuatorAutoConfigurationTests.TestEndpoint testEndpoint() {
            return new CloudFoundryActuatorAutoConfigurationTests.TestEndpoint();
        }
    }

    @Endpoint(id = "test")
    static class TestEndpoint {
        @ReadOperation
        public String hello() {
            return "hello world";
        }
    }
}

