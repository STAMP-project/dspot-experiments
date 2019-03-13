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
package org.springframework.boot.actuate.autoconfigure.metrics.web.tomcat;


import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.tomcat.TomcatMetrics;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.util.Collections;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.metrics.web.tomcat.TomcatMetricsBinder;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.web.reactive.ReactiveWebServerFactoryAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.boot.web.embedded.tomcat.TomcatReactiveWebServerFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.reactive.HttpHandler;


/**
 * Tests for {@link TomcatMetricsAutoConfiguration}.
 *
 * @author Andy Wilkinson
 */
public class TomcatMetricsAutoConfigurationTests {
    @Test
    public void autoConfiguresTomcatMetricsWithEmbeddedServletTomcat() {
        new WebApplicationContextRunner(AnnotationConfigServletWebServerApplicationContext::new).withConfiguration(AutoConfigurations.of(TomcatMetricsAutoConfiguration.class, ServletWebServerFactoryAutoConfiguration.class)).withUserConfiguration(TomcatMetricsAutoConfigurationTests.ServletWebServerConfiguration.class, TomcatMetricsAutoConfigurationTests.MeterRegistryConfiguration.class).run(( context) -> {
            context.publishEvent(new ApplicationStartedEvent(new SpringApplication(), null, context.getSourceApplicationContext()));
            assertThat(context).hasSingleBean(.class);
            SimpleMeterRegistry registry = context.getBean(.class);
            assertThat(registry.find("tomcat.sessions.active.max").meter()).isNotNull();
            assertThat(registry.find("tomcat.threads.current").meter()).isNotNull();
        });
    }

    @Test
    public void autoConfiguresTomcatMetricsWithEmbeddedReactiveTomcat() {
        new org.springframework.boot.test.context.runner.ReactiveWebApplicationContextRunner(AnnotationConfigReactiveWebServerApplicationContext::new).withConfiguration(AutoConfigurations.of(TomcatMetricsAutoConfiguration.class, ReactiveWebServerFactoryAutoConfiguration.class)).withUserConfiguration(TomcatMetricsAutoConfigurationTests.ReactiveWebServerConfiguration.class, TomcatMetricsAutoConfigurationTests.MeterRegistryConfiguration.class).run(( context) -> {
            context.publishEvent(new ApplicationStartedEvent(new SpringApplication(), null, context.getSourceApplicationContext()));
            SimpleMeterRegistry registry = context.getBean(.class);
            assertThat(registry.find("tomcat.sessions.active.max").meter()).isNotNull();
            assertThat(registry.find("tomcat.threads.current").meter()).isNotNull();
        });
    }

    @Test
    public void autoConfiguresTomcatMetricsWithStandaloneTomcat() {
        new WebApplicationContextRunner().withConfiguration(AutoConfigurations.of(TomcatMetricsAutoConfiguration.class)).withUserConfiguration(TomcatMetricsAutoConfigurationTests.MeterRegistryConfiguration.class).run(( context) -> assertThat(context).hasSingleBean(.class));
    }

    @Test
    public void allowsCustomTomcatMetricsBinderToBeUsed() {
        new WebApplicationContextRunner().withConfiguration(AutoConfigurations.of(TomcatMetricsAutoConfiguration.class)).withUserConfiguration(TomcatMetricsAutoConfigurationTests.MeterRegistryConfiguration.class, TomcatMetricsAutoConfigurationTests.CustomTomcatMetricsBinder.class).run(( context) -> assertThat(context).hasSingleBean(.class).hasBean("customTomcatMetricsBinder"));
    }

    @Test
    public void allowsCustomTomcatMetricsToBeUsed() {
        new WebApplicationContextRunner().withConfiguration(AutoConfigurations.of(TomcatMetricsAutoConfiguration.class)).withUserConfiguration(TomcatMetricsAutoConfigurationTests.MeterRegistryConfiguration.class, TomcatMetricsAutoConfigurationTests.CustomTomcatMetrics.class).run(( context) -> assertThat(context).doesNotHaveBean(.class).hasBean("customTomcatMetrics"));
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
        public TomcatServletWebServerFactory tomcatFactory() {
            return new TomcatServletWebServerFactory(0);
        }
    }

    @Configuration
    static class ReactiveWebServerConfiguration {
        @Bean
        public TomcatReactiveWebServerFactory tomcatFactory() {
            return new TomcatReactiveWebServerFactory(0);
        }

        @Bean
        public HttpHandler httpHandler() {
            return Mockito.mock(HttpHandler.class);
        }
    }

    @Configuration
    static class CustomTomcatMetrics {
        @Bean
        public TomcatMetrics customTomcatMetrics() {
            return new TomcatMetrics(null, Collections.emptyList());
        }
    }

    @Configuration
    static class CustomTomcatMetricsBinder {
        @Bean
        public TomcatMetricsBinder customTomcatMetricsBinder(MeterRegistry meterRegistry) {
            return new TomcatMetricsBinder(meterRegistry);
        }
    }
}

