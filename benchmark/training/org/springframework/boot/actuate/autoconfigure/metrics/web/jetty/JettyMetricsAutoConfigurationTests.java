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
package org.springframework.boot.actuate.autoconfigure.metrics.web.jetty;


import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.metrics.web.jetty.JettyServerThreadPoolMetricsBinder;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.web.reactive.ReactiveWebServerFactoryAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.boot.web.embedded.jetty.JettyReactiveWebServerFactory;
import org.springframework.boot.web.embedded.jetty.JettyServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.reactive.HttpHandler;


/**
 * Tests for {@link JettyMetricsAutoConfiguration}.
 *
 * @author Andy Wilkinson
 */
public class JettyMetricsAutoConfigurationTests {
    @Test
    public void autoConfiguresThreadPoolMetricsWithEmbeddedServletJetty() {
        new WebApplicationContextRunner(AnnotationConfigServletWebServerApplicationContext::new).withConfiguration(AutoConfigurations.of(JettyMetricsAutoConfiguration.class, ServletWebServerFactoryAutoConfiguration.class)).withUserConfiguration(JettyMetricsAutoConfigurationTests.ServletWebServerConfiguration.class, JettyMetricsAutoConfigurationTests.MeterRegistryConfiguration.class).run(( context) -> {
            context.publishEvent(new ApplicationStartedEvent(new SpringApplication(), null, context.getSourceApplicationContext()));
            assertThat(context).hasSingleBean(.class);
            SimpleMeterRegistry registry = context.getBean(.class);
            assertThat(registry.find("jetty.threads.config.min").meter()).isNotNull();
        });
    }

    @Test
    public void autoConfiguresThreadPoolMetricsWithEmbeddedReactiveJetty() {
        new org.springframework.boot.test.context.runner.ReactiveWebApplicationContextRunner(AnnotationConfigReactiveWebServerApplicationContext::new).withConfiguration(AutoConfigurations.of(JettyMetricsAutoConfiguration.class, ReactiveWebServerFactoryAutoConfiguration.class)).withUserConfiguration(JettyMetricsAutoConfigurationTests.ReactiveWebServerConfiguration.class, JettyMetricsAutoConfigurationTests.MeterRegistryConfiguration.class).run(( context) -> {
            context.publishEvent(new ApplicationStartedEvent(new SpringApplication(), null, context.getSourceApplicationContext()));
            SimpleMeterRegistry registry = context.getBean(.class);
            assertThat(registry.find("jetty.threads.config.min").meter()).isNotNull();
        });
    }

    @Test
    public void allowsCustomJettyServerThreadPoolMetricsBinderToBeUsed() {
        new WebApplicationContextRunner().withConfiguration(AutoConfigurations.of(JettyMetricsAutoConfiguration.class)).withUserConfiguration(JettyMetricsAutoConfigurationTests.CustomJettyServerThreadPoolMetricsBinder.class, JettyMetricsAutoConfigurationTests.MeterRegistryConfiguration.class).run(( context) -> assertThat(context).hasSingleBean(.class).hasBean("customJettyServerThreadPoolMetricsBinder"));
    }

    @Configuration
    static class MeterRegistryConfiguration {
        @Bean
        public SimpleMeterRegistry meterRegistry() {
            return new SimpleMeterRegistry();
        }
    }

    @Configuration
    static class ServletWebServerConfiguration {
        @Bean
        public JettyServletWebServerFactory jettyFactory() {
            return new JettyServletWebServerFactory(0);
        }
    }

    @Configuration
    static class ReactiveWebServerConfiguration {
        @Bean
        public JettyReactiveWebServerFactory jettyFactory() {
            return new JettyReactiveWebServerFactory(0);
        }

        @Bean
        public HttpHandler httpHandler() {
            return Mockito.mock(HttpHandler.class);
        }
    }

    @Configuration
    static class CustomJettyServerThreadPoolMetricsBinder {
        @Bean
        public JettyServerThreadPoolMetricsBinder customJettyServerThreadPoolMetricsBinder(MeterRegistry meterRegistry) {
            return new JettyServerThreadPoolMetricsBinder(meterRegistry);
        }
    }
}

