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
import io.micrometer.core.instrument.binder.logging.Log4j2Metrics;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.util.EnvironmentTestUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Tests for {@link Log4J2MetricsAutoConfiguration}.
 *
 * @author Andy Wilkinson
 * @author Johnny Lim
 */
public class Log4J2MetricsAutoConfigurationTest {
    private final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();

    @Test
    public void autoConfiguresLog4J2Metrics() {
        registerAndRefresh();
        assertThat(context.getBean(Log4j2Metrics.class)).isNotNull();
    }

    @Test
    @Deprecated
    public void allowsLogbackMetricsToBeDisabled() {
        EnvironmentTestUtils.addEnvironment(context, "management.metrics.binders.log4j2.enabled=false");
        registerAndRefresh();
        assertThat(context.getBeansOfType(Log4j2Metrics.class)).isEmpty();
    }

    @Test
    public void allowsCustomLog4J2MetricsToBeUsed() {
        registerAndRefresh(Log4J2MetricsAutoConfigurationTest.CustomLog4J2MetricsConfiguration.class);
        assertThat(context.getBean(Log4j2Metrics.class)).isEqualTo(context.getBean("customLog4J2Metrics"));
    }

    @Configuration
    static class MeterRegistryConfiguration {
        @Bean
        public MeterRegistry meterRegistry() {
            return Mockito.mock(MeterRegistry.class);
        }
    }

    @Configuration
    static class CustomLog4J2MetricsConfiguration {
        @Bean
        public Log4j2Metrics customLog4J2Metrics() {
            return new Log4j2Metrics();
        }
    }
}

