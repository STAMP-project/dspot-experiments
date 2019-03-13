/**
 * Copyright 2015-2019 The OpenZipkin Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package zipkin2.server.internal;


import brave.Tracing;
import com.linecorp.armeria.spring.actuate.ArmeriaSpringActuatorAutoConfiguration;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.prometheus.PrometheusConfig;
import org.junit.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.actuate.autoconfigure.endpoint.EndpointAutoConfiguration;
import org.springframework.boot.actuate.health.HealthAggregator;
import org.springframework.boot.actuate.health.OrderedHealthAggregator;
import org.springframework.boot.actuate.metrics.export.prometheus.PrometheusScrapeEndpoint;
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import zipkin2.server.internal.brave.TracingConfiguration;
import zipkin2.storage.StorageComponent;


public class ZipkinServerConfigurationTest {
    AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();

    @Test
    public void httpCollector_enabledByDefault() {
        context.register(ArmeriaSpringActuatorAutoConfiguration.class, ZipkinServerConfigurationTest.PrometheusScrapeEndpointConfiguration.class, EndpointAutoConfiguration.class, PropertyPlaceholderAutoConfiguration.class, ZipkinServerConfigurationTest.Config.class, ZipkinServerConfiguration.class, ZipkinHttpCollector.class);
        context.refresh();
        assertThat(context.getBean(ZipkinHttpCollector.class)).isNotNull();
    }

    // TODO: Remove when removing workaround for https://github.com/line/armeria/issues/1637
    @Deprecated
    static class PrometheusScrapeEndpointConfiguration {
        @Bean
        PrometheusScrapeEndpoint prometheusEndpoint() {
            return new PrometheusScrapeEndpoint(null);
        }
    }

    @Test(expected = NoSuchBeanDefinitionException.class)
    public void httpCollector_canDisable() {
        TestPropertyValues.of("zipkin.collector.http.enabled:false").applyTo(context);
        context.register(ArmeriaSpringActuatorAutoConfiguration.class, ZipkinServerConfigurationTest.PrometheusScrapeEndpointConfiguration.class, EndpointAutoConfiguration.class, PropertyPlaceholderAutoConfiguration.class, ZipkinServerConfigurationTest.Config.class, ZipkinServerConfiguration.class, ZipkinHttpCollector.class);
        context.refresh();
        context.getBean(ZipkinHttpCollector.class);
    }

    @Test
    public void query_enabledByDefault() {
        context.register(ArmeriaSpringActuatorAutoConfiguration.class, ZipkinServerConfigurationTest.PrometheusScrapeEndpointConfiguration.class, EndpointAutoConfiguration.class, PropertyPlaceholderAutoConfiguration.class, ZipkinServerConfigurationTest.Config.class, ZipkinServerConfiguration.class, ZipkinQueryApiV2.class);
        context.refresh();
        assertThat(context.getBean(ZipkinQueryApiV2.class)).isNotNull();
    }

    @Test
    public void query_canDisable() {
        TestPropertyValues.of("zipkin.query.enabled:false").applyTo(context);
        context.register(ArmeriaSpringActuatorAutoConfiguration.class, ZipkinServerConfigurationTest.PrometheusScrapeEndpointConfiguration.class, EndpointAutoConfiguration.class, PropertyPlaceholderAutoConfiguration.class, ZipkinServerConfigurationTest.Config.class, ZipkinServerConfiguration.class, ZipkinQueryApiV2.class);
        context.refresh();
        try {
            context.getBean(ZipkinQueryApiV2.class);
            failBecauseExceptionWasNotThrown(NoSuchBeanDefinitionException.class);
        } catch (NoSuchBeanDefinitionException e) {
        }
    }

    @Test
    public void selfTracing_canEnable() {
        TestPropertyValues.of("zipkin.self-tracing.enabled:true").applyTo(context);
        context.register(ArmeriaSpringActuatorAutoConfiguration.class, ZipkinServerConfigurationTest.PrometheusScrapeEndpointConfiguration.class, EndpointAutoConfiguration.class, PropertyPlaceholderAutoConfiguration.class, ZipkinServerConfigurationTest.Config.class, ZipkinServerConfiguration.class, TracingConfiguration.class);
        context.refresh();
        context.getBean(Tracing.class).close();
    }

    @Test
    public void search_canDisable() {
        TestPropertyValues.of("zipkin.storage.search-enabled:false").applyTo(context);
        context.register(ArmeriaSpringActuatorAutoConfiguration.class, ZipkinServerConfigurationTest.PrometheusScrapeEndpointConfiguration.class, EndpointAutoConfiguration.class, PropertyPlaceholderAutoConfiguration.class, ZipkinServerConfigurationTest.Config.class, ZipkinServerConfiguration.class);
        context.refresh();
        StorageComponent v2Storage = context.getBean(StorageComponent.class);
        assertThat(v2Storage).extracting("searchEnabled").containsExactly(false);
    }

    @Configuration
    public static class Config {
        @Bean
        public HealthAggregator healthAggregator() {
            return new OrderedHealthAggregator();
        }

        @Bean
        MeterRegistry registry() {
            return new io.micrometer.prometheus.PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
        }
    }
}

