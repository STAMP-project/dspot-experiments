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
package org.springframework.boot.actuate.autoconfigure.metrics.export.dynatrace;


import io.micrometer.core.instrument.Clock;
import io.micrometer.dynatrace.DynatraceConfig;
import io.micrometer.dynatrace.DynatraceMeterRegistry;
import org.junit.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


/**
 * Tests for {@link DynatraceMetricsExportAutoConfiguration}.
 *
 * @author Andy Wilkinson
 * @author Stephane Nicoll
 */
public class DynatraceMetricsExportAutoConfigurationTests {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(DynatraceMetricsExportAutoConfiguration.class));

    @Test
    public void backsOffWithoutAClock() {
        this.contextRunner.run(( context) -> assertThat(context).doesNotHaveBean(.class));
    }

    @Test
    public void failsWithoutAUri() {
        this.contextRunner.withUserConfiguration(DynatraceMetricsExportAutoConfigurationTests.BaseConfiguration.class).run(( context) -> assertThat(context).hasFailed());
    }

    @Test
    public void autoConfiguresConfigAndMeterRegistry() {
        this.contextRunner.withUserConfiguration(DynatraceMetricsExportAutoConfigurationTests.BaseConfiguration.class).with(mandatoryProperties()).run(( context) -> assertThat(context).hasSingleBean(.class).hasSingleBean(.class));
    }

    @Test
    public void autoConfigurationCanBeDisabled() {
        this.contextRunner.withUserConfiguration(DynatraceMetricsExportAutoConfigurationTests.BaseConfiguration.class).withPropertyValues("management.metrics.export.dynatrace.enabled=false").run(( context) -> assertThat(context).doesNotHaveBean(.class).doesNotHaveBean(.class));
    }

    @Test
    public void allowsCustomConfigToBeUsed() {
        this.contextRunner.withUserConfiguration(DynatraceMetricsExportAutoConfigurationTests.CustomConfigConfiguration.class).run(( context) -> assertThat(context).hasSingleBean(.class).hasSingleBean(.class).hasBean("customConfig"));
    }

    @Test
    public void allowsCustomRegistryToBeUsed() {
        this.contextRunner.withUserConfiguration(DynatraceMetricsExportAutoConfigurationTests.CustomRegistryConfiguration.class).with(mandatoryProperties()).run(( context) -> assertThat(context).hasSingleBean(.class).hasBean("customRegistry").hasSingleBean(.class));
    }

    @Test
    public void stopsMeterRegistryWhenContextIsClosed() {
        this.contextRunner.withUserConfiguration(DynatraceMetricsExportAutoConfigurationTests.BaseConfiguration.class).with(mandatoryProperties()).run(( context) -> {
            DynatraceMeterRegistry registry = context.getBean(.class);
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
    @Import(DynatraceMetricsExportAutoConfigurationTests.BaseConfiguration.class)
    static class CustomConfigConfiguration {
        @Bean
        public DynatraceConfig customConfig() {
            return ( key) -> {
                if ("dynatrace.uri".equals(key)) {
                    return "https://dynatrace.example.com";
                }
                if ("dynatrace.apiToken".equals(key)) {
                    return "abcde";
                }
                if ("dynatrace.deviceId".equals(key)) {
                    return "test";
                }
                return null;
            };
        }
    }

    @Configuration
    @Import(DynatraceMetricsExportAutoConfigurationTests.BaseConfiguration.class)
    static class CustomRegistryConfiguration {
        @Bean
        public DynatraceMeterRegistry customRegistry(DynatraceConfig config, Clock clock) {
            return new DynatraceMeterRegistry(config, clock);
        }
    }
}

