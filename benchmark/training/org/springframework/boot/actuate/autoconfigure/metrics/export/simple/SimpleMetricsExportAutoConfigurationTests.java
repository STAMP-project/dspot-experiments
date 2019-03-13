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
package org.springframework.boot.actuate.autoconfigure.metrics.export.simple;


import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleConfig;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


/**
 * Tests for {@link SimpleMetricsExportAutoConfiguration}.
 *
 * @author Andy Wilkinson
 */
public class SimpleMetricsExportAutoConfigurationTests {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(SimpleMetricsExportAutoConfiguration.class));

    @Test
    public void autoConfiguresConfigAndMeterRegistry() {
        this.contextRunner.withUserConfiguration(SimpleMetricsExportAutoConfigurationTests.BaseConfiguration.class).run(( context) -> assertThat(context).hasSingleBean(.class).hasSingleBean(.class).hasSingleBean(.class));
    }

    @Test
    public void backsOffWhenSpecificallyDisabled() {
        this.contextRunner.withUserConfiguration(SimpleMetricsExportAutoConfigurationTests.BaseConfiguration.class).withPropertyValues("management.metrics.export.simple.enabled=false").run(( context) -> assertThat(context).doesNotHaveBean(.class).doesNotHaveBean(.class));
    }

    @Test
    public void allowsConfigToBeCustomized() {
        this.contextRunner.withUserConfiguration(SimpleMetricsExportAutoConfigurationTests.CustomConfigConfiguration.class).run(( context) -> assertThat(context).hasSingleBean(.class).hasBean("customConfig"));
    }

    @Test
    public void backsOffEntirelyWithCustomMeterRegistry() {
        this.contextRunner.withUserConfiguration(SimpleMetricsExportAutoConfigurationTests.CustomRegistryConfiguration.class).run(( context) -> assertThat(context).hasSingleBean(.class).hasBean("customRegistry").doesNotHaveBean(.class));
    }

    @Configuration
    static class BaseConfiguration {
        @Bean
        public Clock clock() {
            return Clock.SYSTEM;
        }
    }

    @Configuration
    @Import(SimpleMetricsExportAutoConfigurationTests.BaseConfiguration.class)
    static class CustomConfigConfiguration {
        @Bean
        public SimpleConfig customConfig() {
            return ( key) -> null;
        }
    }

    @Configuration
    @Import(SimpleMetricsExportAutoConfigurationTests.BaseConfiguration.class)
    static class CustomRegistryConfiguration {
        @Bean
        public MeterRegistry customRegistry() {
            return Mockito.mock(MeterRegistry.class);
        }
    }
}

