/**
 * Copyright 2015-2018 The OpenZipkin Authors
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
package zipkin2.autoconfigure.collector.scribe;


import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import zipkin2.collector.CollectorMetrics;
import zipkin2.collector.CollectorSampler;
import zipkin2.collector.scribe.ScribeCollector;
import zipkin2.storage.InMemoryStorage;
import zipkin2.storage.StorageComponent;


public class ZipkinScribeCollectorAutoConfigurationTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    AnnotationConfigApplicationContext context;

    @Test
    public void doesntProvidesCollectorComponent_byDefault() {
        context = new AnnotationConfigApplicationContext();
        context.register(PropertyPlaceholderAutoConfiguration.class, ZipkinScribeCollectorAutoConfiguration.class, ZipkinScribeCollectorAutoConfigurationTest.InMemoryConfiguration.class);
        context.refresh();
        thrown.expect(NoSuchBeanDefinitionException.class);
        context.getBean(ScribeCollector.class);
    }

    /**
     * Note: this will flake if you happen to be running a server on port 9410!
     */
    @Test
    public void providesCollectorComponent_whenEnabled() {
        context = new AnnotationConfigApplicationContext();
        TestPropertyValues.of("zipkin.collector.scribe.enabled:true").applyTo(context);
        context.register(PropertyPlaceholderAutoConfiguration.class, ZipkinScribeCollectorAutoConfiguration.class, ZipkinScribeCollectorAutoConfigurationTest.InMemoryConfiguration.class);
        context.refresh();
        assertThat(context.getBean(ScribeCollector.class)).isNotNull();
    }

    @Test
    public void canOverrideProperty_port() {
        context = new AnnotationConfigApplicationContext();
        TestPropertyValues.of("zipkin.collector.scribe.enabled:true", "zipkin.collector.scribe.port:9999").applyTo(context);
        context.register(PropertyPlaceholderAutoConfiguration.class, ZipkinScribeCollectorAutoConfiguration.class, ZipkinScribeCollectorAutoConfigurationTest.InMemoryConfiguration.class);
        context.refresh();
        assertThat(context.getBean(ZipkinScribeCollectorProperties.class).getPort()).isEqualTo(9999);
    }

    @Configuration
    static class InMemoryConfiguration {
        @Bean
        CollectorSampler sampler() {
            return CollectorSampler.ALWAYS_SAMPLE;
        }

        @Bean
        CollectorMetrics metrics() {
            return CollectorMetrics.NOOP_METRICS;
        }

        @Bean
        StorageComponent storage() {
            return InMemoryStorage.newBuilder().build();
        }
    }
}

