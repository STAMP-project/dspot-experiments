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
package org.springframework.boot.actuate.autoconfigure.endpoint.web;


import java.util.Collections;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Test;
import org.springframework.boot.actuate.endpoint.web.ServletEndpointRegistrar;
import org.springframework.boot.actuate.endpoint.web.annotation.ServletEndpointsSupplier;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletPath;
import org.springframework.boot.autoconfigure.web.servlet.JerseyApplicationPath;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.DispatcherServlet;


/**
 * Tests for {@link ServletEndpointManagementContextConfiguration}.
 *
 * @author Phillip Webb
 * @author Madhura Bhave
 */
public class ServletEndpointManagementContextConfigurationTests {
    private WebApplicationContextRunner contextRunner = new WebApplicationContextRunner().withUserConfiguration(ServletEndpointManagementContextConfigurationTests.TestConfig.class);

    @Test
    public void contextShouldContainServletEndpointRegistrar() {
        FilteredClassLoader classLoader = new FilteredClassLoader(ResourceConfig.class);
        this.contextRunner.withClassLoader(classLoader).run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            ServletEndpointRegistrar bean = context.getBean(.class);
            assertThat(bean).hasFieldOrPropertyWithValue("basePath", "/test/actuator");
        });
    }

    @Test
    public void contextWhenJerseyShouldContainServletEndpointRegistrar() {
        FilteredClassLoader classLoader = new FilteredClassLoader(DispatcherServlet.class);
        this.contextRunner.withClassLoader(classLoader).run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            ServletEndpointRegistrar bean = context.getBean(.class);
            assertThat(bean).hasFieldOrPropertyWithValue("basePath", "/jersey/actuator");
        });
    }

    @Test
    public void contextWhenNoServletBasedShouldNotContainServletEndpointRegistrar() {
        new ApplicationContextRunner().withUserConfiguration(ServletEndpointManagementContextConfigurationTests.TestConfig.class).run(( context) -> assertThat(context).doesNotHaveBean(.class));
    }

    @Configuration
    @Import(ServletEndpointManagementContextConfiguration.class)
    @EnableConfigurationProperties(WebEndpointProperties.class)
    static class TestConfig {
        @Bean
        public ServletEndpointsSupplier servletEndpointsSupplier() {
            return Collections::emptyList;
        }

        @Bean
        public DispatcherServletPath dispatcherServletPath() {
            return () -> "/test";
        }

        @Bean
        public JerseyApplicationPath jerseyApplicationPath() {
            return () -> "/jersey";
        }
    }
}

