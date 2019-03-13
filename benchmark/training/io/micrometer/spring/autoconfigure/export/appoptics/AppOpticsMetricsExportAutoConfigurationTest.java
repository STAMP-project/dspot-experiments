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
package io.micrometer.spring.autoconfigure.export.appoptics;


import io.micrometer.appoptics.AppOpticsConfig;
import io.micrometer.appoptics.AppOpticsMeterRegistry;
import io.micrometer.core.instrument.Clock;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


/**
 * Tests for {@link AppOpticsMetricsExportAutoConfiguration}.
 *
 * @author Stephane Nicoll
 * @author Johnny Lim
 */
class AppOpticsMetricsExportAutoConfigurationTest {
    private final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();

    @Test
    public void stopsMeterRegistryWhenContextIsClosed() {
        registerAndRefresh(AppOpticsMetricsExportAutoConfigurationTest.BaseConfiguration.class);
        AppOpticsMeterRegistry registry = context.getBean(AppOpticsMeterRegistry.class);
        assertThat(registry.isClosed()).isFalse();
        context.close();
        assertThat(registry.isClosed()).isTrue();
    }

    @Configuration
    static class BaseConfiguration {
        @Bean
        public Clock clock() {
            return Clock.SYSTEM;
        }
    }

    @Configuration
    @Import(AppOpticsMetricsExportAutoConfigurationTest.BaseConfiguration.class)
    static class CustomConfigConfiguration {
        @Bean
        public AppOpticsConfig customConfig() {
            return ( key) -> null;
        }
    }

    @Configuration
    @Import(AppOpticsMetricsExportAutoConfigurationTest.BaseConfiguration.class)
    static class CustomRegistryConfiguration {
        @Bean
        public AppOpticsMeterRegistry customRegistry(AppOpticsConfig config, Clock clock) {
            return new AppOpticsMeterRegistry(config, clock);
        }
    }
}

