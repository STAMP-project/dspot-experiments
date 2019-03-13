/**
 * Copyright 2017 Pivotal Software, Inc.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.micrometer.spring.autoconfigure;


import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.jvm.ClassLoaderMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.util.EnvironmentTestUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Tests for {@link JvmMetricsAutoConfiguration}.
 *
 * @author Andy Wilkinson
 * @author Stephane Nicoll
 * @author Johnny Lim
 */
public class JvmMetricsAutoConfigurationTest {
    private final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();

    @Test
    public void autoConfiguresJvmMetrics() {
        registerAndRefresh();
        assertThat(context.getBean(JvmGcMetrics.class)).isNotNull();
        assertThat(context.getBean(JvmMemoryMetrics.class)).isNotNull();
        assertThat(context.getBean(JvmThreadMetrics.class)).isNotNull();
        assertThat(context.getBean(ClassLoaderMetrics.class)).isNotNull();
    }

    @Test
    @Deprecated
    public void allowsJvmMetricsToBeDisabled() {
        EnvironmentTestUtils.addEnvironment(context, "management.metrics.binders.jvm.enabled=false");
        registerAndRefresh();
        assertThat(context.getBeansOfType(JvmGcMetrics.class)).isEmpty();
        assertThat(context.getBeansOfType(JvmMemoryMetrics.class)).isEmpty();
        assertThat(context.getBeansOfType(JvmThreadMetrics.class)).isEmpty();
        assertThat(context.getBeansOfType(ClassLoaderMetrics.class)).isEmpty();
    }

    @Test
    public void allowsCustomJvmGcMetricsToBeUsed() {
        registerAndRefresh(JvmMetricsAutoConfigurationTest.CustomJvmGcMetricsConfiguration.class);
        assertThat(context.getBean(JvmGcMetrics.class)).isEqualTo(context.getBean("customJvmGcMetrics"));
        assertThat(context.getBean(JvmMemoryMetrics.class)).isNotNull();
        assertThat(context.getBean(JvmThreadMetrics.class)).isNotNull();
        assertThat(context.getBean(ClassLoaderMetrics.class)).isNotNull();
    }

    @Test
    public void allowsCustomJvmMemoryMetricsToBeUsed() {
        registerAndRefresh(JvmMetricsAutoConfigurationTest.CustomJvmMemoryMetricsConfiguration.class);
        assertThat(context.getBean(JvmGcMetrics.class)).isNotNull();
        assertThat(context.getBean(JvmMemoryMetrics.class)).isEqualTo(context.getBean("customJvmMemoryMetrics"));
        assertThat(context.getBean(JvmThreadMetrics.class)).isNotNull();
        assertThat(context.getBean(ClassLoaderMetrics.class)).isNotNull();
    }

    @Test
    public void allowsCustomJvmThreadMetricsToBeUsed() {
        registerAndRefresh(JvmMetricsAutoConfigurationTest.CustomJvmThreadMetricsConfiguration.class);
        assertThat(context.getBean(JvmGcMetrics.class)).isNotNull();
        assertThat(context.getBean(JvmMemoryMetrics.class)).isNotNull();
        assertThat(context.getBean(JvmThreadMetrics.class)).isEqualTo(context.getBean("customJvmThreadMetrics"));
        assertThat(context.getBean(ClassLoaderMetrics.class)).isNotNull();
    }

    @Test
    public void allowsCustomClassLoaderMetricsToBeUsed() {
        registerAndRefresh(JvmMetricsAutoConfigurationTest.CustomClassLoaderMetricsConfiguration.class);
        assertThat(context.getBean(JvmGcMetrics.class)).isNotNull();
        assertThat(context.getBean(JvmMemoryMetrics.class)).isNotNull();
        assertThat(context.getBean(JvmThreadMetrics.class)).isNotNull();
        assertThat(context.getBean(ClassLoaderMetrics.class)).isEqualTo(context.getBean("customClassLoaderMetrics"));
    }

    @Configuration
    static class MeterRegistryConfiguration {
        @Bean
        public MeterRegistry meterRegistry() {
            return Mockito.mock(MeterRegistry.class);
        }
    }

    @Configuration
    static class CustomJvmGcMetricsConfiguration {
        @Bean
        public JvmGcMetrics customJvmGcMetrics() {
            return new JvmGcMetrics();
        }
    }

    @Configuration
    static class CustomJvmMemoryMetricsConfiguration {
        @Bean
        public JvmMemoryMetrics customJvmMemoryMetrics() {
            return new JvmMemoryMetrics();
        }
    }

    @Configuration
    static class CustomJvmThreadMetricsConfiguration {
        @Bean
        public JvmThreadMetrics customJvmThreadMetrics() {
            return new JvmThreadMetrics();
        }
    }

    @Configuration
    static class CustomClassLoaderMetricsConfiguration {
        @Bean
        public ClassLoaderMetrics customClassLoaderMetrics() {
            return new ClassLoaderMetrics();
        }
    }
}

