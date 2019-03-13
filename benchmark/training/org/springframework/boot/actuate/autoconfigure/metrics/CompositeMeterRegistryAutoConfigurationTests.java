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


import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;


/**
 * Tests for {@link CompositeMeterRegistryAutoConfiguration}.
 *
 * @author Phillip Webb
 * @author Andy Wilkinson
 */
public class CompositeMeterRegistryAutoConfigurationTests {
    private static final String COMPOSITE_NAME = "compositeMeterRegistry";

    private ApplicationContextRunner contextRunner = new ApplicationContextRunner().withUserConfiguration(CompositeMeterRegistryAutoConfigurationTests.BaseConfig.class).withConfiguration(AutoConfigurations.of(CompositeMeterRegistryAutoConfiguration.class));

    @Test
    public void registerWhenHasNoMeterRegistryShouldRegisterEmptyNoOpComposite() {
        this.contextRunner.withUserConfiguration(CompositeMeterRegistryAutoConfigurationTests.NoMeterRegistryConfig.class).run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            CompositeMeterRegistry registry = context.getBean("noOpMeterRegistry", .class);
            assertThat(registry.getRegistries()).isEmpty();
        });
    }

    @Test
    public void registerWhenHasSingleMeterRegistryShouldDoNothing() {
        this.contextRunner.withUserConfiguration(CompositeMeterRegistryAutoConfigurationTests.SingleMeterRegistryConfig.class).run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            MeterRegistry registry = context.getBean(.class);
            assertThat(registry).isInstanceOf(.class);
        });
    }

    @Test
    public void registerWhenHasMultipleMeterRegistriesShouldAddPrimaryComposite() {
        this.contextRunner.withUserConfiguration(CompositeMeterRegistryAutoConfigurationTests.MultipleMeterRegistriesConfig.class).run(( context) -> {
            assertThat(context.getBeansOfType(.class)).hasSize(3).containsKeys("meterRegistryOne", "meterRegistryTwo", COMPOSITE_NAME);
            MeterRegistry primary = context.getBean(.class);
            assertThat(primary).isInstanceOf(.class);
            assertThat(((CompositeMeterRegistry) (primary)).getRegistries()).hasSize(2);
            assertThat(primary.config().clock()).isNotNull();
        });
    }

    @Test
    public void registerWhenHasMultipleRegistriesAndOneIsPrimaryShouldDoNothing() {
        this.contextRunner.withUserConfiguration(CompositeMeterRegistryAutoConfigurationTests.MultipleMeterRegistriesWithOnePrimaryConfig.class).run(( context) -> {
            assertThat(context.getBeansOfType(.class)).hasSize(2).containsKeys("meterRegistryOne", "meterRegistryTwo");
            MeterRegistry primary = context.getBean(.class);
            assertThat(primary).isInstanceOf(.class);
        });
    }

    @Configuration
    static class BaseConfig {
        @Bean
        @ConditionalOnMissingBean
        public Clock micrometerClock() {
            return Clock.SYSTEM;
        }
    }

    @Configuration
    static class NoMeterRegistryConfig {}

    @Configuration
    static class SingleMeterRegistryConfig {
        @Bean
        public MeterRegistry meterRegistry() {
            return new CompositeMeterRegistryAutoConfigurationTests.TestMeterRegistry();
        }
    }

    @Configuration
    static class MultipleMeterRegistriesConfig {
        @Bean
        public MeterRegistry meterRegistryOne() {
            return new CompositeMeterRegistryAutoConfigurationTests.TestMeterRegistry();
        }

        @Bean
        public MeterRegistry meterRegistryTwo() {
            return new SimpleMeterRegistry();
        }
    }

    @Configuration
    static class MultipleMeterRegistriesWithOnePrimaryConfig {
        @Bean
        @Primary
        public MeterRegistry meterRegistryOne() {
            return new CompositeMeterRegistryAutoConfigurationTests.TestMeterRegistry();
        }

        @Bean
        public MeterRegistry meterRegistryTwo() {
            return new SimpleMeterRegistry();
        }
    }

    static class TestMeterRegistry extends SimpleMeterRegistry {}
}

