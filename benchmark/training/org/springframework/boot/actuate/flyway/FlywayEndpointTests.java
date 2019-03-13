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
package org.springframework.boot.actuate.flyway;


import org.junit.Test;
import org.springframework.boot.actuate.flyway.FlywayEndpoint.FlywayDescriptor;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.boot.autoconfigure.jdbc.EmbeddedDataSourceConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


/**
 * Tests for {@link FlywayEndpoint}.
 *
 * @author Edd? Mel?ndez
 * @author Andy Wilkinson
 * @author Phillip Webb
 */
public class FlywayEndpointTests {
    @Test
    public void flywayReportIsProduced() {
        new ApplicationContextRunner().withUserConfiguration(FlywayEndpointTests.Config.class).run(( context) -> {
            Map<String, FlywayDescriptor> flywayBeans = context.getBean(.class).flywayBeans().getContexts().get(context.getId()).getFlywayBeans();
            assertThat(flywayBeans).hasSize(1);
            assertThat(flywayBeans.values().iterator().next().getMigrations()).hasSize(3);
        });
    }

    @Test
    public void whenFlywayHasBeenBaselinedFlywayReportIsProduced() {
        new ApplicationContextRunner().withUserConfiguration(FlywayEndpointTests.BaselinedFlywayConfig.class, FlywayEndpointTests.Config.class).run(( context) -> {
            Map<String, FlywayDescriptor> flywayBeans = context.getBean(.class).flywayBeans().getContexts().get(context.getId()).getFlywayBeans();
            assertThat(flywayBeans).hasSize(1);
            assertThat(flywayBeans.values().iterator().next().getMigrations()).hasSize(3);
        });
    }

    @Configuration
    @Import({ EmbeddedDataSourceConfiguration.class, FlywayAutoConfiguration.class })
    public static class Config {
        @Bean
        public FlywayEndpoint endpoint(ApplicationContext context) {
            return new FlywayEndpoint(context);
        }
    }

    @Configuration
    public static class BaselinedFlywayConfig {
        @SuppressWarnings("deprecation")
        @Bean
        public FlywayMigrationStrategy baseliningMigrationStrategy() {
            return ( flyway) -> {
                flyway.setBaselineVersionAsString("2");
                flyway.baseline();
                flyway.migrate();
            };
        }
    }
}

