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
package org.springframework.boot.actuate.autoconfigure.influx;


import org.influxdb.InfluxDB;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.actuate.autoconfigure.health.HealthIndicatorAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Tests for {@link InfluxDbHealthIndicatorAutoConfiguration}.
 *
 * @author Edd? Mel?ndez
 */
public class InfluxDbHealthIndicatorAutoConfigurationTests {
    private ApplicationContextRunner contextRunner = new ApplicationContextRunner().withUserConfiguration(InfluxDbHealthIndicatorAutoConfigurationTests.InfluxDbConfiguration.class).withConfiguration(AutoConfigurations.of(InfluxDbHealthIndicatorAutoConfiguration.class, HealthIndicatorAutoConfiguration.class));

    @Test
    public void runShouldCreateIndicator() {
        this.contextRunner.run(( context) -> assertThat(context).hasSingleBean(.class).doesNotHaveBean(.class));
    }

    @Test
    public void runWhenDisabledShouldNotCreateIndicator() {
        this.contextRunner.withPropertyValues("management.health.influxdb.enabled:false").run(( context) -> assertThat(context).doesNotHaveBean(.class).hasSingleBean(.class));
    }

    @Configuration
    static class InfluxDbConfiguration {
        @Bean
        public InfluxDB influxdb() {
            return Mockito.mock(InfluxDB.class);
        }
    }
}

