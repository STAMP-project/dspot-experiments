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
package org.springframework.boot.autoconfigure.data.jdbc;


import org.junit.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.TestAutoConfigurationPackage;
import org.springframework.boot.autoconfigure.data.empty.EmptyDataPackage;
import org.springframework.boot.autoconfigure.data.jdbc.city.City;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.EmbeddedDataSourceConfiguration;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;


/**
 * Tests for {@link JdbcRepositoriesAutoConfiguration}.
 *
 * @author Andy Wilkinson
 */
public class JdbcRepositoriesAutoConfigurationTests {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(JdbcRepositoriesAutoConfiguration.class));

    @Test
    public void backsOffWithNoDataSource() {
        this.contextRunner.withUserConfiguration(JdbcRepositoriesAutoConfigurationTests.TestConfiguration.class).run(( context) -> assertThat(context).doesNotHaveBean(.class));
    }

    @Test
    public void backsOffWithNoJdbcOperations() {
        this.contextRunner.withUserConfiguration(EmbeddedDataSourceConfiguration.class, JdbcRepositoriesAutoConfigurationTests.TestConfiguration.class).run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            assertThat(context).doesNotHaveBean(.class);
        });
    }

    @Test
    public void basicAutoConfiguration() {
        this.contextRunner.withConfiguration(AutoConfigurations.of(JdbcTemplateAutoConfiguration.class, DataSourceAutoConfiguration.class)).withUserConfiguration(JdbcRepositoriesAutoConfigurationTests.TestConfiguration.class, EmbeddedDataSourceConfiguration.class).withPropertyValues("spring.datasource.schema=classpath:data-jdbc-schema.sql", "spring.datasource.data=classpath:city.sql", "spring.datasource.generate-unique-name:true").run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            assertThat(context).hasSingleBean(.class);
            assertThat(context.getBean(.class).findById(2000L)).isPresent();
        });
    }

    @Test
    public void autoConfigurationWithNoRepositories() {
        this.contextRunner.withConfiguration(AutoConfigurations.of(JdbcTemplateAutoConfiguration.class)).withUserConfiguration(EmbeddedDataSourceConfiguration.class, JdbcRepositoriesAutoConfigurationTests.EmptyConfiguration.class).run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            assertThat(context).doesNotHaveBean(.class);
        });
    }

    @Test
    public void honoursUsersEnableJdbcRepositoriesConfiguration() {
        this.contextRunner.withConfiguration(AutoConfigurations.of(JdbcTemplateAutoConfiguration.class, DataSourceAutoConfiguration.class)).withUserConfiguration(JdbcRepositoriesAutoConfigurationTests.EnableRepositoriesConfiguration.class, EmbeddedDataSourceConfiguration.class).withPropertyValues("spring.datasource.schema=classpath:data-jdbc-schema.sql", "spring.datasource.data=classpath:city.sql", "spring.datasource.generate-unique-name:true").run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            assertThat(context).hasSingleBean(.class);
            assertThat(context.getBean(.class).findById(2000L)).isPresent();
        });
    }

    @TestAutoConfigurationPackage(City.class)
    private static class TestConfiguration {}

    @Configuration
    @TestAutoConfigurationPackage(EmptyDataPackage.class)
    protected static class EmptyConfiguration {}

    @TestAutoConfigurationPackage(EmptyDataPackage.class)
    @EnableJdbcRepositories(basePackageClasses = City.class)
    private static class EnableRepositoriesConfiguration {}
}

