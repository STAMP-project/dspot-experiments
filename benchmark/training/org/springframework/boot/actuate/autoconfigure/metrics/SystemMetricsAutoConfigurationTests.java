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
package org.springframework.boot.actuate.autoconfigure.metrics;


import io.micrometer.core.instrument.binder.system.FileDescriptorMetrics;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;
import io.micrometer.core.instrument.binder.system.UptimeMetrics;
import org.junit.Test;
import org.springframework.boot.actuate.autoconfigure.metrics.test.MetricsRun;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Tests for {@link SystemMetricsAutoConfiguration}.
 *
 * @author Andy Wilkinson
 * @author Stephane Nicoll
 */
public class SystemMetricsAutoConfigurationTests {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner().with(MetricsRun.simple()).withConfiguration(AutoConfigurations.of(SystemMetricsAutoConfiguration.class));

    @Test
    public void autoConfiguresUptimeMetrics() {
        this.contextRunner.run(( context) -> assertThat(context).hasSingleBean(.class));
    }

    @Test
    public void allowsCustomUptimeMetricsToBeUsed() {
        this.contextRunner.withUserConfiguration(SystemMetricsAutoConfigurationTests.CustomUptimeMetricsConfiguration.class).run(( context) -> assertThat(context).hasSingleBean(.class).hasBean("customUptimeMetrics"));
    }

    @Test
    public void autoConfiguresProcessorMetrics() {
        this.contextRunner.run(( context) -> assertThat(context).hasSingleBean(.class));
    }

    @Test
    public void allowsCustomProcessorMetricsToBeUsed() {
        this.contextRunner.withUserConfiguration(SystemMetricsAutoConfigurationTests.CustomProcessorMetricsConfiguration.class).run(( context) -> assertThat(context).hasSingleBean(.class).hasBean("customProcessorMetrics"));
    }

    @Test
    public void autoConfiguresFileDescriptorMetrics() {
        this.contextRunner.run(( context) -> assertThat(context).hasSingleBean(.class));
    }

    @Test
    public void allowsCustomFileDescriptorMetricsToBeUsed() {
        this.contextRunner.withUserConfiguration(SystemMetricsAutoConfigurationTests.CustomFileDescriptorMetricsConfiguration.class).run(( context) -> assertThat(context).hasSingleBean(.class).hasBean("customFileDescriptorMetrics"));
    }

    @Configuration
    static class CustomUptimeMetricsConfiguration {
        @Bean
        public UptimeMetrics customUptimeMetrics() {
            return new UptimeMetrics();
        }
    }

    @Configuration
    static class CustomProcessorMetricsConfiguration {
        @Bean
        public ProcessorMetrics customProcessorMetrics() {
            return new ProcessorMetrics();
        }
    }

    @Configuration
    static class CustomFileDescriptorMetricsConfiguration {
        @Bean
        public FileDescriptorMetrics customFileDescriptorMetrics() {
            return new FileDescriptorMetrics();
        }
    }
}

