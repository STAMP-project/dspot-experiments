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
package org.springframework.boot.actuate.autoconfigure.metrics.export.datadog;


import io.micrometer.core.instrument.Clock;
import io.micrometer.datadog.DatadogConfig;
import io.micrometer.datadog.DatadogMeterRegistry;
import org.junit.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


/**
 * Tests for {@link DatadogMetricsExportAutoConfiguration}.
 *
 * @author Andy Wilkinson
 */
public class DatadogMetricsExportAutoConfigurationTests {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(DatadogMetricsExportAutoConfiguration.class));

    @Test
    public void backsOffWithoutAClock() {
        this.contextRunner.run(( context) -> assertThat(context).doesNotHaveBean(.class));
    }

    @Test
    public void failsWithoutAnApiKey() {
        this.contextRunner.withUserConfiguration(DatadogMetricsExportAutoConfigurationTests.BaseConfiguration.class).run(( context) -> assertThat(context).hasFailed());
    }

    @Test
    public void autoConfiguresConfigAndMeterRegistry() {
        this.contextRunner.withUserConfiguration(DatadogMetricsExportAutoConfigurationTests.BaseConfiguration.class).withPropertyValues("management.metrics.export.datadog.api-key=abcde").run(( context) -> assertThat(context).hasSingleBean(.class).hasSingleBean(.class));
    }

    @Test
    public void autoConfigurationCanBeDisabled() {
        this.contextRunner.withUserConfiguration(DatadogMetricsExportAutoConfigurationTests.BaseConfiguration.class).withPropertyValues("management.metrics.export.datadog.enabled=false").run(( context) -> assertThat(context).doesNotHaveBean(.class).doesNotHaveBean(.class));
    }

    @Test
    public void allowsCustomConfigToBeUsed() {
        this.contextRunner.withUserConfiguration(DatadogMetricsExportAutoConfigurationTests.CustomConfigConfiguration.class).run(( context) -> assertThat(context).hasSingleBean(.class).hasSingleBean(.class).hasBean("customConfig"));
    }

    @Test
    public void allowsCustomRegistryToBeUsed() {
        this.contextRunner.withUserConfiguration(DatadogMetricsExportAutoConfigurationTests.CustomRegistryConfiguration.class).withPropertyValues("management.metrics.export.datadog.api-key=abcde").run(( context) -> assertThat(context).hasSingleBean(.class).hasBean("customRegistry").hasSingleBean(.class));
    }

    @Test
    public void stopsMeterRegistryWhenContextIsClosed() {
        this.contextRunner.withUserConfiguration(DatadogMetricsExportAutoConfigurationTests.BaseConfiguration.class).withPropertyValues("management.metrics.export.datadog.api-key=abcde").run(( context) -> {
            DatadogMeterRegistry registry = context.getBean(.class);
            assertThat(registry.isClosed()).isFalse();
            context.close();
            assertThat(registry.isClosed()).isTrue();
        });
    }

    @Configuration
    static class BaseConfiguration {
        @Bean
        public Clock clock() {
            return Clock.SYSTEM;
        }
    }

    @Configuration
    @Import(DatadogMetricsExportAutoConfigurationTests.BaseConfiguration.class)
    static class CustomConfigConfiguration {
        @Bean
        public DatadogConfig customConfig() {
            return ( key) -> {
                if ("datadog.apiKey".equals(key)) {
                    return "12345";
                }
                return null;
            };
        }
    }

    @Configuration
    @Import(DatadogMetricsExportAutoConfigurationTests.BaseConfiguration.class)
    static class CustomRegistryConfiguration {
        @Bean
        public DatadogMeterRegistry customRegistry(DatadogConfig config, Clock clock) {
            return new DatadogMeterRegistry(config, clock);
        }
    }
}

