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
package org.springframework.boot.actuate.context.properties;


import org.junit.Test;
import org.springframework.boot.actuate.context.properties.ConfigurationPropertiesReportEndpoint.ApplicationConfigurationProperties;
import org.springframework.boot.actuate.context.properties.ConfigurationPropertiesReportEndpoint.ConfigurationPropertiesBeanDescriptor;
import org.springframework.boot.actuate.context.properties.ConfigurationPropertiesReportEndpoint.ContextConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Tests for {@link ConfigurationPropertiesReportEndpoint} when used with bean methods.
 *
 * @author Dave Syer
 * @author Andy Wilkinson
 */
public class ConfigurationPropertiesReportEndpointMethodAnnotationsTests {
    @Test
    public void testNaming() {
        ApplicationContextRunner contextRunner = new ApplicationContextRunner().withUserConfiguration(ConfigurationPropertiesReportEndpointMethodAnnotationsTests.Config.class).withPropertyValues("other.name:foo", "first.name:bar");
        contextRunner.run(( context) -> {
            ConfigurationPropertiesReportEndpoint endpoint = context.getBean(.class);
            ApplicationConfigurationProperties applicationProperties = endpoint.configurationProperties();
            assertThat(applicationProperties.getContexts()).containsOnlyKeys(context.getId());
            ContextConfigurationProperties contextProperties = applicationProperties.getContexts().get(context.getId());
            ConfigurationPropertiesBeanDescriptor other = contextProperties.getBeans().get("other");
            assertThat(other).isNotNull();
            assertThat(other.getPrefix()).isEqualTo("other");
            assertThat(other.getProperties()).isNotNull();
            assertThat(other.getProperties()).isNotEmpty();
        });
    }

    @Test
    public void prefixFromBeanMethodConfigurationPropertiesCanOverridePrefixOnClass() {
        ApplicationContextRunner contextRunner = new ApplicationContextRunner().withUserConfiguration(ConfigurationPropertiesReportEndpointMethodAnnotationsTests.OverriddenPrefix.class).withPropertyValues("other.name:foo");
        contextRunner.run(( context) -> {
            ConfigurationPropertiesReportEndpoint endpoint = context.getBean(.class);
            ApplicationConfigurationProperties applicationProperties = endpoint.configurationProperties();
            assertThat(applicationProperties.getContexts()).containsOnlyKeys(context.getId());
            ContextConfigurationProperties contextProperties = applicationProperties.getContexts().get(context.getId());
            ConfigurationPropertiesBeanDescriptor bar = contextProperties.getBeans().get("bar");
            assertThat(bar).isNotNull();
            assertThat(bar.getPrefix()).isEqualTo("other");
            assertThat(bar.getProperties()).isNotNull();
            assertThat(bar.getProperties()).isNotEmpty();
        });
    }

    @Configuration
    @EnableConfigurationProperties
    public static class Config {
        @Bean
        public ConfigurationPropertiesReportEndpoint endpoint() {
            return new ConfigurationPropertiesReportEndpoint();
        }

        @Bean
        @ConfigurationProperties(prefix = "first")
        public ConfigurationPropertiesReportEndpointMethodAnnotationsTests.Foo foo() {
            return new ConfigurationPropertiesReportEndpointMethodAnnotationsTests.Foo();
        }

        @Bean
        @ConfigurationProperties(prefix = "other")
        public ConfigurationPropertiesReportEndpointMethodAnnotationsTests.Foo other() {
            return new ConfigurationPropertiesReportEndpointMethodAnnotationsTests.Foo();
        }
    }

    @Configuration
    @EnableConfigurationProperties
    public static class OverriddenPrefix {
        @Bean
        public ConfigurationPropertiesReportEndpoint endpoint() {
            return new ConfigurationPropertiesReportEndpoint();
        }

        @Bean
        @ConfigurationProperties(prefix = "other")
        public ConfigurationPropertiesReportEndpointMethodAnnotationsTests.Bar bar() {
            return new ConfigurationPropertiesReportEndpointMethodAnnotationsTests.Bar();
        }
    }

    public static class Foo {
        private String name = "654321";

        public String getName() {
            return this.name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    @ConfigurationProperties(prefix = "test")
    public static class Bar {
        private String name = "654321";

        public String getName() {
            return this.name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}

