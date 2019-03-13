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
package org.springframework.boot.actuate.autoconfigure.metrics.export.newrelic;


import io.micrometer.core.instrument.Clock;
import io.micrometer.newrelic.NewRelicConfig;
import io.micrometer.newrelic.NewRelicMeterRegistry;
import org.junit.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


/**
 * Tests for {@link NewRelicMetricsExportAutoConfiguration}.
 *
 * @author Andy Wilkinson
 */
public class NewRelicMetricsExportAutoConfigurationTests {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(NewRelicMetricsExportAutoConfiguration.class));

    @Test
    public void backsOffWithoutAClock() {
        this.contextRunner.run(( context) -> assertThat(context).doesNotHaveBean(.class));
    }

    @Test
    public void failsWithoutAnApiKey() {
        this.contextRunner.withUserConfiguration(NewRelicMetricsExportAutoConfigurationTests.BaseConfiguration.class).withPropertyValues("management.metrics.export.newrelic.account-id=12345").run(( context) -> assertThat(context).hasFailed());
    }

    @Test
    public void failsWithoutAnAccountId() {
        this.contextRunner.withUserConfiguration(NewRelicMetricsExportAutoConfigurationTests.BaseConfiguration.class).withPropertyValues("management.metrics.export.newrelic.api-key=abcde").run(( context) -> assertThat(context).hasFailed());
    }

    @Test
    public void autoConfiguresWithAccountIdAndApiKey() {
        this.contextRunner.withUserConfiguration(NewRelicMetricsExportAutoConfigurationTests.BaseConfiguration.class).withPropertyValues("management.metrics.export.newrelic.api-key=abcde", "management.metrics.export.newrelic.account-id=12345").run(( context) -> assertThat(context).hasSingleBean(.class).hasSingleBean(.class).hasSingleBean(.class));
    }

    @Test
    public void autoConfigurationCanBeDisabled() {
        this.contextRunner.withUserConfiguration(NewRelicMetricsExportAutoConfigurationTests.BaseConfiguration.class).withPropertyValues("management.metrics.export.newrelic.enabled=false").run(( context) -> assertThat(context).doesNotHaveBean(.class).doesNotHaveBean(.class));
    }

    @Test
    public void allowsConfigToBeCustomized() {
        this.contextRunner.withUserConfiguration(NewRelicMetricsExportAutoConfigurationTests.CustomConfigConfiguration.class).withPropertyValues("management.metrics.export.newrelic.api-key=abcde", "management.metrics.export.newrelic.account-id=12345").run(( context) -> assertThat(context).hasSingleBean(.class).hasBean("customConfig"));
    }

    @Test
    public void allowsRegistryToBeCustomized() {
        this.contextRunner.withUserConfiguration(NewRelicMetricsExportAutoConfigurationTests.CustomRegistryConfiguration.class).withPropertyValues("management.metrics.export.newrelic.api-key=abcde", "management.metrics.export.newrelic.account-id=12345").run(( context) -> assertThat(context).hasSingleBean(.class).hasBean("customRegistry"));
    }

    @Test
    public void stopsMeterRegistryWhenContextIsClosed() {
        this.contextRunner.withPropertyValues("management.metrics.export.newrelic.api-key=abcde", "management.metrics.export.newrelic.account-id=abcde").withUserConfiguration(NewRelicMetricsExportAutoConfigurationTests.BaseConfiguration.class).run(( context) -> {
            NewRelicMeterRegistry registry = context.getBean(.class);
            assertThat(registry.isClosed()).isFalse();
            context.close();
            assertThat(registry.isClosed()).isTrue();
        });
    }

    @Configuration
    static class BaseConfiguration {
        @Bean
        public Clock customClock() {
            return Clock.SYSTEM;
        }
    }

    @Configuration
    @Import(NewRelicMetricsExportAutoConfigurationTests.BaseConfiguration.class)
    static class CustomConfigConfiguration {
        @Bean
        public NewRelicConfig customConfig() {
            return ( key) -> {
                if ("newrelic.accountId".equals(key)) {
                    return "abcde";
                }
                if ("newrelic.apiKey".equals(key)) {
                    return "12345";
                }
                return null;
            };
        }
    }

    @Configuration
    @Import(NewRelicMetricsExportAutoConfigurationTests.BaseConfiguration.class)
    static class CustomRegistryConfiguration {
        @Bean
        public NewRelicMeterRegistry customRegistry(NewRelicConfig config, Clock clock) {
            return new NewRelicMeterRegistry(config, clock);
        }
    }
}

