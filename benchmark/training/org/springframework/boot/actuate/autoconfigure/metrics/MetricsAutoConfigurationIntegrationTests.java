/**
 * Copyright 2012-2018 the original author or authors.
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
package org.springframework.boot.actuate.autoconfigure.metrics;


import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.MockClock;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import io.micrometer.core.instrument.simple.SimpleConfig;
import org.junit.Test;
import org.springframework.boot.actuate.autoconfigure.metrics.export.graphite.GraphiteMetricsExportAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.export.jmx.JmxMetricsExportAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.test.MetricsRun;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;


/**
 * Integration tests for metrics auto-configuration.
 *
 * @author Stephane Nicoll
 */
public class MetricsAutoConfigurationIntegrationTests {
    private ApplicationContextRunner contextRunner = new ApplicationContextRunner().with(MetricsRun.simple());

    @Test
    public void propertyBasedMeterFilteringIsAutoConfigured() {
        this.contextRunner.withPropertyValues("management.metrics.enable.my.org=false").run(( context) -> {
            MeterRegistry registry = context.getBean(.class);
            registry.timer("my.org.timer");
            assertThat(registry.find("my.org.timer").timer()).isNull();
        });
    }

    @Test
    public void propertyBasedCommonTagsIsAutoConfigured() {
        this.contextRunner.withPropertyValues("management.metrics.tags.region=test", "management.metrics.tags.origin=local").run(( context) -> {
            MeterRegistry registry = context.getBean(.class);
            registry.counter("my.counter", "env", "qa");
            assertThat(registry.find("my.counter").tags("env", "qa").tags("region", "test").tags("origin", "local").counter()).isNotNull();
        });
    }

    @Test
    public void simpleMeterRegistryIsUsedAsAFallback() {
        this.contextRunner.run(( context) -> assertThat(context.getBean(.class)).isInstanceOf(.class));
    }

    @Test
    public void emptyCompositeIsCreatedWhenNoMeterRegistriesAreAutoConfigured() {
        new ApplicationContextRunner().with(MetricsRun.limitedTo()).run(( context) -> {
            MeterRegistry registry = context.getBean(.class);
            assertThat(registry).isInstanceOf(.class);
            assertThat(((CompositeMeterRegistry) (registry)).getRegistries()).isEmpty();
        });
    }

    @Test
    public void noCompositeIsCreatedWhenASingleMeterRegistryIsAutoConfigured() {
        new ApplicationContextRunner().with(MetricsRun.limitedTo(GraphiteMetricsExportAutoConfiguration.class)).run(( context) -> assertThat(context.getBean(.class)).isInstanceOf(.class));
    }

    @Test
    public void noCompositeIsCreatedWithMultipleRegistriesAndOneThatIsPrimary() {
        new ApplicationContextRunner().with(MetricsRun.limitedTo(GraphiteMetricsExportAutoConfiguration.class, JmxMetricsExportAutoConfiguration.class)).withUserConfiguration(MetricsAutoConfigurationIntegrationTests.PrimaryMeterRegistryConfiguration.class).run(( context) -> assertThat(context.getBean(.class)).isInstanceOf(.class));
    }

    @Test
    public void compositeCreatedWithMultipleRegistries() {
        new ApplicationContextRunner().with(MetricsRun.limitedTo(GraphiteMetricsExportAutoConfiguration.class, JmxMetricsExportAutoConfiguration.class)).run(( context) -> {
            MeterRegistry registry = context.getBean(.class);
            assertThat(registry).isInstanceOf(.class);
            assertThat(((CompositeMeterRegistry) (registry)).getRegistries()).hasAtLeastOneElementOfType(.class).hasAtLeastOneElementOfType(.class);
        });
    }

    @Configuration
    static class PrimaryMeterRegistryConfiguration {
        @Primary
        @Bean
        public MeterRegistry simpleMeterRegistry() {
            return new io.micrometer.core.instrument.simple.SimpleMeterRegistry(SimpleConfig.DEFAULT, new MockClock());
        }
    }
}

