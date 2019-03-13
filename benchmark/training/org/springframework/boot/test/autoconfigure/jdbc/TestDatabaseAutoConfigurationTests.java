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
package org.springframework.boot.test.autoconfigure.jdbc;


import EmbeddedDatabaseType.HSQL;
import javax.sql.DataSource;
import org.junit.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;


/**
 * Tests for {@link TestDatabaseAutoConfiguration}.
 *
 * @author Stephane Nicoll
 * @author Andy Wilkinson
 */
public class TestDatabaseAutoConfigurationTests {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(TestDatabaseAutoConfiguration.class));

    @Test
    public void replaceWithNoDataSourceAvailable() {
        this.contextRunner.run(( context) -> assertThat(context).doesNotHaveBean(.class));
    }

    @Test
    public void replaceWithUniqueDatabase() {
        this.contextRunner.withUserConfiguration(TestDatabaseAutoConfigurationTests.ExistingDataSourceConfiguration.class).run(( context) -> {
            DataSource datasource = context.getBean(.class);
            JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
            jdbcTemplate.execute("create table example (id int, name varchar);");
            this.contextRunner.withUserConfiguration(.class).run(( secondContext) -> {
                DataSource anotherDatasource = secondContext.getBean(.class);
                JdbcTemplate anotherJdbcTemplate = new JdbcTemplate(anotherDatasource);
                anotherJdbcTemplate.execute("create table example (id int, name varchar);");
            });
        });
    }

    @Configuration
    static class ExistingDataSourceConfiguration {
        @Bean
        public DataSource dataSource() {
            return new EmbeddedDatabaseBuilder().generateUniqueName(true).setType(HSQL).build();
        }
    }
}

