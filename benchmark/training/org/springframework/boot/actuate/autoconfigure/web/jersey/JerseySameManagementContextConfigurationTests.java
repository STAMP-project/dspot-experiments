/**
 * Copyright 2012-2019 the original author or authors.
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
package org.springframework.boot.actuate.autoconfigure.web.jersey;


import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.jersey.ResourceConfigCustomizer;
import org.springframework.boot.autoconfigure.web.servlet.JerseyApplicationPath;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.boot.testsupport.runner.classpath.ClassPathExclusions;
import org.springframework.boot.testsupport.runner.classpath.ModifiedClassPathRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Tests for {@link JerseySameManagementContextConfiguration}.
 *
 * @author Madhura Bhave
 */
@RunWith(ModifiedClassPathRunner.class)
@ClassPathExclusions("spring-webmvc-*")
public class JerseySameManagementContextConfigurationTests {
    private final WebApplicationContextRunner contextRunner = new WebApplicationContextRunner().withConfiguration(AutoConfigurations.of(JerseySameManagementContextConfiguration.class));

    @Test
    public void autoConfigurationIsConditionalOnServletWebApplication() {
        ApplicationContextRunner contextRunner = new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(JerseySameManagementContextConfiguration.class));
        contextRunner.run(( context) -> assertThat(context).doesNotHaveBean(.class));
    }

    @Test
    public void autoConfigurationIsConditionalOnClassResourceConfig() {
        this.contextRunner.withClassLoader(new FilteredClassLoader(ResourceConfig.class)).run(( context) -> assertThat(context).doesNotHaveBean(.class));
    }

    @Test
    public void resourceConfigIsCustomizedWithResourceConfigCustomizerBean() {
        this.contextRunner.withUserConfiguration(JerseySameManagementContextConfigurationTests.CustomizerConfiguration.class).run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            ResourceConfig config = context.getBean(.class);
            ResourceConfigCustomizer customizer = context.getBean(.class);
            verify(customizer).customize(config);
        });
    }

    @Test
    public void jerseyApplicationPathIsAutoConfiguredWhenNeeded() {
        this.contextRunner.run(( context) -> assertThat(context).hasSingleBean(.class));
    }

    @Test
    public void jerseyApplicationPathIsConditionalOnMissingBean() {
        this.contextRunner.withUserConfiguration(JerseySameManagementContextConfigurationTests.ConfigWithJerseyApplicationPath.class).run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            assertThat(context).hasBean("testJerseyApplicationPath");
        });
    }

    @Test
    public void existingResourceConfigBeanShouldNotAutoConfigureRelatedBeans() {
        this.contextRunner.withUserConfiguration(JerseySameManagementContextConfigurationTests.ConfigWithResourceConfig.class).run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            assertThat(context).doesNotHaveBean(.class);
            assertThat(context).doesNotHaveBean(.class);
            assertThat(context).hasBean("customResourceConfig");
        });
    }

    @Test
    @SuppressWarnings("unchecked")
    public void servletRegistrationBeanIsAutoConfiguredWhenNeeded() {
        this.contextRunner.withPropertyValues("spring.jersey.application-path=/jersey").run(( context) -> {
            ServletRegistrationBean<ServletContainer> bean = context.getBean(.class);
            assertThat(bean.getUrlMappings()).containsExactly("/jersey/*");
        });
    }

    @Configuration
    static class ConfigWithJerseyApplicationPath {
        @Bean
        public JerseyApplicationPath testJerseyApplicationPath() {
            return Mockito.mock(JerseyApplicationPath.class);
        }
    }

    @Configuration
    static class ConfigWithResourceConfig {
        @Bean
        public ResourceConfig customResourceConfig() {
            return new ResourceConfig();
        }
    }

    @Configuration
    static class CustomizerConfiguration {
        @Bean
        ResourceConfigCustomizer resourceConfigCustomizer() {
            return Mockito.mock(ResourceConfigCustomizer.class);
        }
    }
}

