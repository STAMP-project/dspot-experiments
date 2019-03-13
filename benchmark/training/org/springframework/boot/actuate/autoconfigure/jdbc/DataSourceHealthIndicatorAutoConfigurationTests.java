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
package org.springframework.boot.actuate.autoconfigure.jdbc;


import javax.sql.DataSource;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.actuate.autoconfigure.health.HealthIndicatorAutoConfiguration;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.jdbc.DataSourceHealthIndicator;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.EmbeddedDataSourceConfiguration;
import org.springframework.boot.autoconfigure.jdbc.metadata.DataSourcePoolMetadataProvidersConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;


/**
 * Tests for {@link DataSourceHealthIndicatorAutoConfiguration}.
 *
 * @author Phillip Webb
 */
public class DataSourceHealthIndicatorAutoConfigurationTests {
    private ApplicationContextRunner contextRunner = new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(DataSourceAutoConfiguration.class, HealthIndicatorAutoConfiguration.class, DataSourceHealthIndicatorAutoConfiguration.class)).withPropertyValues("spring.datasource.initialization-mode=never");

    @Test
    public void runShouldCreateIndicator() {
        this.contextRunner.run(( context) -> {
            context.getBean(.class);
            assertThat(context).hasSingleBean(.class).doesNotHaveBean(.class);
        });
    }

    @Test
    public void runWhenMultipleDataSourceBeansShouldCreateCompositeIndicator() {
        this.contextRunner.withUserConfiguration(EmbeddedDataSourceConfiguration.class, DataSourceHealthIndicatorAutoConfigurationTests.DataSourceConfig.class).run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            HealthIndicator indicator = context.getBean(.class);
            assertThat(indicator.health().getDetails()).containsOnlyKeys("dataSource", "testDataSource");
        });
    }

    @Test
    public void runShouldFilterRoutingDataSource() {
        this.contextRunner.withUserConfiguration(EmbeddedDataSourceConfiguration.class, DataSourceHealthIndicatorAutoConfigurationTests.RoutingDatasourceConfig.class).run(( context) -> assertThat(context).hasSingleBean(.class).doesNotHaveBean(.class));
    }

    @Test
    public void runWithValidationQueryPropertyShouldUseCustomQuery() {
        this.contextRunner.withUserConfiguration(DataSourceHealthIndicatorAutoConfigurationTests.DataSourceConfig.class, DataSourcePoolMetadataProvidersConfiguration.class).withPropertyValues("spring.datasource.test.validation-query:SELECT from FOOBAR").run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            DataSourceHealthIndicator indicator = context.getBean(.class);
            assertThat(indicator.getQuery()).isEqualTo("SELECT from FOOBAR");
        });
    }

    @Test
    public void runWhenDisabledShouldNotCreateIndicator() {
        this.contextRunner.withUserConfiguration(EmbeddedDataSourceConfiguration.class).withPropertyValues("management.health.db.enabled:false").run(( context) -> assertThat(context).doesNotHaveBean(.class).doesNotHaveBean(.class).hasSingleBean(.class));
    }

    @Configuration
    @EnableConfigurationProperties
    protected static class DataSourceConfig {
        @Bean
        @ConfigurationProperties(prefix = "spring.datasource.test")
        public DataSource testDataSource() {
            return DataSourceBuilder.create().type(DataSource.class).driverClassName("org.hsqldb.jdbc.JDBCDriver").url("jdbc:hsqldb:mem:test").username("sa").build();
        }
    }

    @Configuration
    protected static class RoutingDatasourceConfig {
        @Bean
        AbstractRoutingDataSource routingDataSource() {
            return Mockito.mock(AbstractRoutingDataSource.class);
        }
    }
}

