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
package zipkin2.autoconfigure.collector.rabbitmq;


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
import zipkin2.collector.rabbitmq.RabbitMQCollector;
import zipkin2.storage.InMemoryStorage;
import zipkin2.storage.StorageComponent;


public class ZipkinRabbitMQCollectorAutoConfigurationTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private AnnotationConfigApplicationContext context;

    @Test
    public void doesNotProvideCollectorComponent_whenAddressAndUriNotSet() {
        context = new AnnotationConfigApplicationContext();
        context.register(PropertyPlaceholderAutoConfiguration.class, ZipkinRabbitMQCollectorAutoConfiguration.class, ZipkinRabbitMQCollectorAutoConfigurationTest.InMemoryConfiguration.class);
        context.refresh();
        thrown.expect(NoSuchBeanDefinitionException.class);
        context.getBean(RabbitMQCollector.class);
    }

    @Test
    public void doesNotProvideCollectorComponent_whenAddressesAndUriIsEmptyString() {
        context = new AnnotationConfigApplicationContext();
        TestPropertyValues.of("zipkin.collector.rabbitmq.addresses:", "zipkin.collector.rabbitmq.uri:").applyTo(context);
        context.register(PropertyPlaceholderAutoConfiguration.class, ZipkinRabbitMQCollectorAutoConfiguration.class, ZipkinRabbitMQCollectorAutoConfigurationTest.InMemoryConfiguration.class);
        context.refresh();
        thrown.expect(NoSuchBeanDefinitionException.class);
        context.getBean(RabbitMQCollector.class);
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

