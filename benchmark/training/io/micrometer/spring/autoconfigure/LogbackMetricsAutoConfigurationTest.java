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
import io.micrometer.core.instrument.binder.logging.LogbackMetrics;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.util.EnvironmentTestUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Tests for {@link LogbackMetricsAutoConfiguration}.
 *
 * @author Andy Wilkinson
 * @author Stephane Nicoll
 * @author Johnny Lim
 */
public class LogbackMetricsAutoConfigurationTest {
    private final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();

    @Test
    public void autoConfiguresLogbackMetrics() {
        registerAndRefresh();
        assertThat(context.getBean(LogbackMetrics.class)).isNotNull();
    }

    @Test
    @Deprecated
    public void allowsLogbackMetricsToBeDisabled() {
        EnvironmentTestUtils.addEnvironment(context, "management.metrics.binders.logback.enabled=false");
        registerAndRefresh();
        assertThat(context.getBeansOfType(LogbackMetrics.class)).isEmpty();
    }

    @Test
    public void allowsCustomLogbackMetricsToBeUsed() {
        registerAndRefresh(LogbackMetricsAutoConfigurationTest.CustomLogbackMetricsConfiguration.class);
        assertThat(context.getBean(LogbackMetrics.class)).isEqualTo(context.getBean("customLogbackMetrics"));
    }

    @Configuration
    static class MeterRegistryConfiguration {
        @Bean
        public MeterRegistry meterRegistry() {
            return Mockito.mock(MeterRegistry.class);
        }
    }

    @Configuration
    static class CustomLogbackMetricsConfiguration {
        @Bean
        public LogbackMetrics customLogbackMetrics() {
            return new LogbackMetrics();
        }
    }
}

