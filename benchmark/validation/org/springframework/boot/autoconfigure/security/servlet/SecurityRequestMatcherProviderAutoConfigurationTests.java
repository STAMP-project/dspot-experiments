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
package org.springframework.boot.autoconfigure.security.servlet;


import org.junit.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.web.servlet.JerseyApplicationPath;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;


/**
 * Tests for {@link SecurityRequestMatcherProviderAutoConfiguration}.
 *
 * @author Madhura Bhave
 */
public class SecurityRequestMatcherProviderAutoConfigurationTests {
    private WebApplicationContextRunner contextRunner = new WebApplicationContextRunner().withConfiguration(AutoConfigurations.of(SecurityRequestMatcherProviderAutoConfiguration.class));

    @Test
    public void configurationConditionalOnWebApplication() {
        new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(SecurityRequestMatcherProviderAutoConfiguration.class)).withUserConfiguration(SecurityRequestMatcherProviderAutoConfigurationTests.TestMvcConfiguration.class).run(( context) -> assertThat(context).doesNotHaveBean(.class));
    }

    @Test
    public void configurationConditionalOnRequestMatcherClass() {
        this.contextRunner.withClassLoader(new FilteredClassLoader("org.springframework.security.web.util.matcher.RequestMatcher")).run(( context) -> assertThat(context).doesNotHaveBean(.class));
    }

    @Test
    public void registersMvcRequestMatcherProviderIfMvcPresent() {
        this.contextRunner.withUserConfiguration(SecurityRequestMatcherProviderAutoConfigurationTests.TestMvcConfiguration.class).run(( context) -> assertThat(context).getBean(.class).isInstanceOf(.class));
    }

    @Test
    public void registersRequestMatcherForJerseyProviderIfJerseyPresentAndMvcAbsent() {
        this.contextRunner.withClassLoader(new FilteredClassLoader("org.springframework.web.servlet.DispatcherServlet")).withUserConfiguration(SecurityRequestMatcherProviderAutoConfigurationTests.TestJerseyConfiguration.class).run(( context) -> assertThat(context).getBean(.class).isInstanceOf(.class));
    }

    @Test
    public void mvcRequestMatcherProviderConditionalOnDispatcherServletClass() {
        this.contextRunner.withClassLoader(new FilteredClassLoader("org.springframework.web.servlet.DispatcherServlet")).run(( context) -> assertThat(context).doesNotHaveBean(.class));
    }

    @Test
    public void jerseyRequestMatcherProviderConditionalOnResourceConfigClass() {
        this.contextRunner.withClassLoader(new FilteredClassLoader("org.glassfish.jersey.server.ResourceConfig")).run(( context) -> assertThat(context).doesNotHaveBean(.class));
    }

    @Test
    public void mvcRequestMatcherProviderConditionalOnHandlerMappingIntrospectorBean() {
        new WebApplicationContextRunner().withConfiguration(AutoConfigurations.of(SecurityRequestMatcherProviderAutoConfiguration.class)).run(( context) -> assertThat(context).doesNotHaveBean(.class));
    }

    @Test
    public void jerseyRequestMatcherProviderConditionalOnJerseyApplicationPathBean() {
        new WebApplicationContextRunner().withConfiguration(AutoConfigurations.of(SecurityRequestMatcherProviderAutoConfiguration.class)).withClassLoader(new FilteredClassLoader("org.springframework.web.servlet.DispatcherServlet")).run(( context) -> assertThat(context).doesNotHaveBean(.class));
    }

    @Configuration
    static class TestMvcConfiguration {
        @Bean
        public HandlerMappingIntrospector introspector() {
            return new HandlerMappingIntrospector();
        }
    }

    @Configuration
    static class TestJerseyConfiguration {
        @Bean
        public JerseyApplicationPath jerseyApplicationPath() {
            return () -> "/admin";
        }
    }
}

