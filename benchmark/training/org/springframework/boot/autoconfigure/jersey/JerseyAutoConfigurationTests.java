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
package org.springframework.boot.autoconfigure.jersey;


import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.RequestContextFilter;


/**
 * Tests for {@link JerseyAutoConfiguration}.
 *
 * @author Andy Wilkinson
 */
public class JerseyAutoConfigurationTests {
    private final WebApplicationContextRunner contextRunner = new WebApplicationContextRunner().withConfiguration(AutoConfigurations.of(JerseyAutoConfiguration.class)).withInitializer(new org.springframework.boot.autoconfigure.logging.ConditionEvaluationReportLoggingListener(LogLevel.INFO)).withUserConfiguration(JerseyAutoConfigurationTests.ResourceConfigConfiguration.class);

    @Test
    public void requestContextFilterRegistrationIsAutoConfigured() {
        this.contextRunner.run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            FilterRegistrationBean<?> registration = context.getBean(.class);
            assertThat(registration.getFilter()).isInstanceOf(.class);
        });
    }

    @Test
    public void whenUserDefinesARequestContextFilterTheAutoConfiguredRegistrationBacksOff() {
        this.contextRunner.withUserConfiguration(JerseyAutoConfigurationTests.RequestContextFilterConfiguration.class).run(( context) -> {
            assertThat(context).doesNotHaveBean(.class);
            assertThat(context).hasSingleBean(.class);
        });
    }

    @Test
    public void whenUserDefinesARequestContextFilterRegistrationTheAutoConfiguredRegistrationBacksOff() {
        this.contextRunner.withUserConfiguration(JerseyAutoConfigurationTests.RequestContextFilterRegistrationConfiguration.class).run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            assertThat(context).hasBean("customRequestContextFilterRegistration");
        });
    }

    @Configuration
    static class ResourceConfigConfiguration {
        @Bean
        public ResourceConfig resourceConfig() {
            return new ResourceConfig();
        }
    }

    @Configuration
    static class RequestContextFilterConfiguration {
        @Bean
        public RequestContextFilter requestContextFilter() {
            return new RequestContextFilter();
        }
    }

    @Configuration
    static class RequestContextFilterRegistrationConfiguration {
        @Bean
        public org.springframework.boot.web.servlet.FilterRegistrationBean<RequestContextFilter> customRequestContextFilterRegistration() {
            return new org.springframework.boot.web.servlet.FilterRegistrationBean<RequestContextFilter>(new RequestContextFilter());
        }
    }
}

