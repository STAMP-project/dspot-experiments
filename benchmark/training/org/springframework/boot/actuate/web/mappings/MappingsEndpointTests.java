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
package org.springframework.boot.actuate.web.mappings;


import java.util.Collection;
import java.util.function.Supplier;
import javax.servlet.ServletException;
import org.junit.Test;
import org.springframework.boot.actuate.web.mappings.MappingsEndpoint.ContextMappings;
import org.springframework.boot.actuate.web.mappings.reactive.DispatcherHandlerMappingDescription;
import org.springframework.boot.actuate.web.mappings.reactive.DispatcherHandlersMappingDescriptionProvider;
import org.springframework.boot.actuate.web.mappings.servlet.DispatcherServletMappingDescription;
import org.springframework.boot.actuate.web.mappings.servlet.DispatcherServletsMappingDescriptionProvider;
import org.springframework.boot.actuate.web.mappings.servlet.FilterRegistrationMappingDescription;
import org.springframework.boot.actuate.web.mappings.servlet.FiltersMappingDescriptionProvider;
import org.springframework.boot.actuate.web.mappings.servlet.ServletRegistrationMappingDescription;
import org.springframework.boot.actuate.web.mappings.servlet.ServletsMappingDescriptionProvider;
import org.springframework.boot.test.context.runner.ReactiveWebApplicationContextRunner;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mock.web.MockServletConfig;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;


/**
 * Tests for {@link MappingsEndpoint}.
 *
 * @author Andy Wilkinson
 * @author Stephane Nicoll
 */
public class MappingsEndpointTests {
    @Test
    public void servletWebMappings() {
        Supplier<ConfigurableWebApplicationContext> contextSupplier = prepareContextSupplier();
        new org.springframework.boot.test.context.runner.WebApplicationContextRunner(contextSupplier).withUserConfiguration(MappingsEndpointTests.EndpointConfiguration.class, MappingsEndpointTests.ServletWebConfiguration.class).run(( context) -> {
            ContextMappings contextMappings = contextMappings(context);
            assertThat(contextMappings.getParentId()).isNull();
            assertThat(contextMappings.getMappings()).containsOnlyKeys("dispatcherServlets", "servletFilters", "servlets");
            Map<String, List<DispatcherServletMappingDescription>> dispatcherServlets = mappings(contextMappings, "dispatcherServlets");
            assertThat(dispatcherServlets).containsOnlyKeys("dispatcherServlet");
            List<DispatcherServletMappingDescription> handlerMappings = dispatcherServlets.get("dispatcherServlet");
            assertThat(handlerMappings).hasSize(1);
            List<ServletRegistrationMappingDescription> servlets = mappings(contextMappings, "servlets");
            assertThat(servlets).hasSize(1);
            List<FilterRegistrationMappingDescription> filters = mappings(contextMappings, "servletFilters");
            assertThat(filters).hasSize(1);
        });
    }

    @Test
    public void servletWebMappingsWithAdditionalDispatcherServlets() {
        Supplier<ConfigurableWebApplicationContext> contextSupplier = prepareContextSupplier();
        withUserConfiguration(MappingsEndpointTests.EndpointConfiguration.class, MappingsEndpointTests.ServletWebConfiguration.class, MappingsEndpointTests.CustomDispatcherServletConfiguration.class).run(( context) -> {
            ContextMappings contextMappings = contextMappings(context);
            Map<String, List<DispatcherServletMappingDescription>> dispatcherServlets = mappings(contextMappings, "dispatcherServlets");
            assertThat(dispatcherServlets).containsOnlyKeys("dispatcherServlet", "customDispatcherServletRegistration", "anotherDispatcherServletRegistration");
            assertThat(dispatcherServlets.get("dispatcherServlet")).hasSize(1);
            assertThat(dispatcherServlets.get("customDispatcherServletRegistration")).hasSize(1);
            assertThat(dispatcherServlets.get("anotherDispatcherServletRegistration")).hasSize(1);
        });
    }

    @Test
    public void reactiveWebMappings() {
        new ReactiveWebApplicationContextRunner().withUserConfiguration(MappingsEndpointTests.EndpointConfiguration.class, MappingsEndpointTests.ReactiveWebConfiguration.class).run(( context) -> {
            ContextMappings contextMappings = contextMappings(context);
            assertThat(contextMappings.getParentId()).isNull();
            assertThat(contextMappings.getMappings()).containsOnlyKeys("dispatcherHandlers");
            Map<String, List<DispatcherHandlerMappingDescription>> dispatcherHandlers = mappings(contextMappings, "dispatcherHandlers");
            assertThat(dispatcherHandlers).containsOnlyKeys("webHandler");
            List<DispatcherHandlerMappingDescription> handlerMappings = dispatcherHandlers.get("webHandler");
            assertThat(handlerMappings).hasSize(3);
        });
    }

    @Configuration
    static class EndpointConfiguration {
        @Bean
        public MappingsEndpoint mappingsEndpoint(Collection<MappingDescriptionProvider> descriptionProviders, ApplicationContext context) {
            return new MappingsEndpoint(descriptionProviders, context);
        }
    }

    @Configuration
    @EnableWebFlux
    @Controller
    static class ReactiveWebConfiguration {
        @Bean
        public DispatcherHandlersMappingDescriptionProvider dispatcherHandlersMappingDescriptionProvider() {
            return new DispatcherHandlersMappingDescriptionProvider();
        }

        @Bean
        public RouterFunction<ServerResponse> routerFunction() {
            return route(GET("/one"), ( request) -> ServerResponse.ok().build()).andRoute(POST("/two"), ( request) -> ServerResponse.ok().build());
        }

        @RequestMapping("/three")
        public void three() {
        }
    }

    @Configuration
    @EnableWebMvc
    @Controller
    static class ServletWebConfiguration {
        @Bean
        public DispatcherServletsMappingDescriptionProvider dispatcherServletsMappingDescriptionProvider() {
            return new DispatcherServletsMappingDescriptionProvider();
        }

        @Bean
        public ServletsMappingDescriptionProvider servletsMappingDescriptionProvider() {
            return new ServletsMappingDescriptionProvider();
        }

        @Bean
        public FiltersMappingDescriptionProvider filtersMappingDescriptionProvider() {
            return new FiltersMappingDescriptionProvider();
        }

        @Bean
        public DispatcherServlet dispatcherServlet(WebApplicationContext context) throws ServletException {
            DispatcherServlet dispatcherServlet = new DispatcherServlet(context);
            dispatcherServlet.init(new MockServletConfig());
            return dispatcherServlet;
        }

        @RequestMapping("/three")
        public void three() {
        }
    }

    @Configuration
    static class CustomDispatcherServletConfiguration {
        @Bean
        public ServletRegistrationBean<DispatcherServlet> customDispatcherServletRegistration(WebApplicationContext context) {
            ServletRegistrationBean<DispatcherServlet> registration = new ServletRegistrationBean(createTestDispatcherServlet(context));
            registration.setName("customDispatcherServletRegistration");
            return registration;
        }

        @Bean
        public DispatcherServlet anotherDispatcherServlet(WebApplicationContext context) {
            return createTestDispatcherServlet(context);
        }

        @Bean
        public ServletRegistrationBean<DispatcherServlet> anotherDispatcherServletRegistration(WebApplicationContext context) {
            ServletRegistrationBean<DispatcherServlet> registrationBean = new ServletRegistrationBean(anotherDispatcherServlet(context));
            registrationBean.setName("anotherDispatcherServletRegistration");
            return registrationBean;
        }

        private DispatcherServlet createTestDispatcherServlet(WebApplicationContext context) {
            try {
                DispatcherServlet dispatcherServlet = new DispatcherServlet(context);
                dispatcherServlet.init(new MockServletConfig());
                return dispatcherServlet;
            } catch (ServletException ex) {
                throw new IllegalStateException(ex);
            }
        }
    }
}

