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
package org.springframework.boot.actuate.autoconfigure.metrics.export.influx;


import io.micrometer.core.instrument.Clock;
import io.micrometer.influx.InfluxConfig;
import io.micrometer.influx.InfluxMeterRegistry;
import org.junit.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


/**
 * Tests for {@link InfluxMetricsExportAutoConfiguration}.
 *
 * @author Andy Wilkinson
 */
public class InfluxMetricsExportAutoConfigurationTests {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(InfluxMetricsExportAutoConfiguration.class));

    @Test
    public void backsOffWithoutAClock() {
        this.contextRunner.run(( context) -> assertThat(context).doesNotHaveBean(.class));
    }

    @Test
    public void autoConfiguresItsConfigAndMeterRegistry() {
        this.contextRunner.withUserConfiguration(InfluxMetricsExportAutoConfigurationTests.BaseConfiguration.class).run(( context) -> assertThat(context).hasSingleBean(.class).hasSingleBean(.class));
    }

    @Test
    public void autoConfigurationCanBeDisabled() {
        this.contextRunner.withUserConfiguration(InfluxMetricsExportAutoConfigurationTests.BaseConfiguration.class).withPropertyValues("management.metrics.export.influx.enabled=false").run(( context) -> assertThat(context).doesNotHaveBean(.class).doesNotHaveBean(.class));
    }

    @Test
    public void allowsCustomConfigToBeUsed() {
        this.contextRunner.withUserConfiguration(InfluxMetricsExportAutoConfigurationTests.CustomConfigConfiguration.class).run(( context) -> assertThat(context).hasSingleBean(.class).hasSingleBean(.class).hasBean("customConfig"));
    }

    @Test
    public void allowsCustomRegistryToBeUsed() {
        this.contextRunner.withUserConfiguration(InfluxMetricsExportAutoConfigurationTests.CustomRegistryConfiguration.class).run(( context) -> assertThat(context).hasSingleBean(.class).hasBean("customRegistry").hasSingleBean(.class));
    }

    @Test
    public void stopsMeterRegistryWhenContextIsClosed() {
        this.contextRunner.withUserConfiguration(InfluxMetricsExportAutoConfigurationTests.BaseConfiguration.class).run(( context) -> {
            InfluxMeterRegistry registry = context.getBean(.class);
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
    @Import(InfluxMetricsExportAutoConfigurationTests.BaseConfiguration.class)
    static class CustomConfigConfiguration {
        @Bean
        public InfluxConfig customConfig() {
            return ( key) -> null;
        }
    }

    @Configuration
    @Import(InfluxMetricsExportAutoConfigurationTests.BaseConfiguration.class)
    static class CustomRegistryConfiguration {
        @Bean
        public InfluxMeterRegistry customRegistry(InfluxConfig config, Clock clock) {
            return new InfluxMeterRegistry(config, clock);
        }
    }
}

