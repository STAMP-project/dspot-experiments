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
package org.springframework.boot.autoconfigure.jdbc;


import java.util.Collections;
import javax.sql.DataSource;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;


/**
 * Tests for {@link JdbcTemplateAutoConfiguration}.
 *
 * @author Dave Syer
 * @author Stephane Nicoll
 * @author Kazuki Shimizu
 * @author Dan Zheng
 */
public class JdbcTemplateAutoConfigurationTests {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner().withPropertyValues("spring.datasource.initialization-mode=never", "spring.datasource.generate-unique-name=true").withConfiguration(AutoConfigurations.of(DataSourceAutoConfiguration.class, JdbcTemplateAutoConfiguration.class));

    @Test
    public void testJdbcTemplateExists() {
        this.contextRunner.run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            JdbcTemplate jdbcTemplate = context.getBean(.class);
            assertThat(jdbcTemplate.getDataSource()).isEqualTo(context.getBean(.class));
            assertThat(jdbcTemplate.getFetchSize()).isEqualTo((-1));
            assertThat(jdbcTemplate.getQueryTimeout()).isEqualTo((-1));
            assertThat(jdbcTemplate.getMaxRows()).isEqualTo((-1));
        });
    }

    @Test
    public void testJdbcTemplateWithCustomProperties() {
        this.contextRunner.withPropertyValues("spring.jdbc.template.fetch-size:100", "spring.jdbc.template.query-timeout:60", "spring.jdbc.template.max-rows:1000").run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            JdbcTemplate jdbcTemplate = context.getBean(.class);
            assertThat(jdbcTemplate.getDataSource()).isNotNull();
            assertThat(jdbcTemplate.getFetchSize()).isEqualTo(100);
            assertThat(jdbcTemplate.getQueryTimeout()).isEqualTo(60);
            assertThat(jdbcTemplate.getMaxRows()).isEqualTo(1000);
        });
    }

    @Test
    public void testJdbcTemplateExistsWithCustomDataSource() {
        this.contextRunner.withUserConfiguration(JdbcTemplateAutoConfigurationTests.TestDataSourceConfiguration.class).run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            JdbcTemplate jdbcTemplate = context.getBean(.class);
            assertThat(jdbcTemplate.getDataSource()).isEqualTo(context.getBean("customDataSource"));
        });
    }

    @Test
    public void testNamedParameterJdbcTemplateExists() {
        this.contextRunner.run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            NamedParameterJdbcTemplate namedParameterJdbcTemplate = context.getBean(.class);
            assertThat(namedParameterJdbcTemplate.getJdbcOperations()).isEqualTo(context.getBean(.class));
        });
    }

    @Test
    public void testMultiDataSource() {
        this.contextRunner.withUserConfiguration(MultiDataSourceConfiguration.class).run(( context) -> {
            assertThat(context).doesNotHaveBean(.class);
            assertThat(context).doesNotHaveBean(.class);
        });
    }

    @Test
    public void testMultiJdbcTemplate() {
        this.contextRunner.withUserConfiguration(JdbcTemplateAutoConfigurationTests.MultiJdbcTemplateConfiguration.class).run(( context) -> assertThat(context).doesNotHaveBean(.class));
    }

    @Test
    public void testMultiDataSourceUsingPrimary() {
        this.contextRunner.withUserConfiguration(MultiDataSourceUsingPrimaryConfiguration.class).run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            assertThat(context).hasSingleBean(.class);
            assertThat(context.getBean(.class).getDataSource()).isEqualTo(context.getBean("test1DataSource"));
        });
    }

    @Test
    public void testMultiJdbcTemplateUsingPrimary() {
        this.contextRunner.withUserConfiguration(JdbcTemplateAutoConfigurationTests.MultiJdbcTemplateUsingPrimaryConfiguration.class).run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            assertThat(context.getBean(.class).getJdbcOperations()).isEqualTo(context.getBean("test1Template"));
        });
    }

    @Test
    public void testExistingCustomJdbcTemplate() {
        this.contextRunner.withUserConfiguration(JdbcTemplateAutoConfigurationTests.CustomConfiguration.class).run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            assertThat(context.getBean(.class)).isEqualTo(context.getBean("customJdbcOperations"));
        });
    }

    @Test
    public void testExistingCustomNamedParameterJdbcTemplate() {
        this.contextRunner.withUserConfiguration(JdbcTemplateAutoConfigurationTests.CustomConfiguration.class).run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            assertThat(context.getBean(.class)).isEqualTo(context.getBean("customNamedParameterJdbcOperations"));
        });
    }

    @Test
    public void testDependencyToDataSourceInitialization() {
        this.contextRunner.withUserConfiguration(JdbcTemplateAutoConfigurationTests.DataSourceInitializationValidator.class).withPropertyValues("spring.datasource.initialization-mode=always").run(( context) -> {
            assertThat(context).hasNotFailed();
            assertThat(context.getBean(.class).count).isEqualTo(1);
        });
    }

    @Test
    public void testDependencyToFlyway() {
        this.contextRunner.withUserConfiguration(JdbcTemplateAutoConfigurationTests.DataSourceMigrationValidator.class).withPropertyValues("spring.flyway.locations:classpath:db/city").withConfiguration(AutoConfigurations.of(FlywayAutoConfiguration.class)).run(( context) -> {
            assertThat(context).hasNotFailed();
            assertThat(context.getBean(.class).count).isEqualTo(0);
        });
    }

    @Test
    public void testDependencyToFlywayWithJdbcTemplateMixed() {
        this.contextRunner.withUserConfiguration(JdbcTemplateAutoConfigurationTests.NamedParameterDataSourceMigrationValidator.class).withPropertyValues("spring.flyway.locations:classpath:db/city").withConfiguration(AutoConfigurations.of(FlywayAutoConfiguration.class)).run(( context) -> {
            assertThat(context).hasNotFailed();
            assertThat(context.getBean(.class)).isNotNull();
            assertThat(context.getBean(.class).count).isEqualTo(0);
        });
    }

    @Test
    public void testDependencyToLiquibase() {
        this.contextRunner.withUserConfiguration(JdbcTemplateAutoConfigurationTests.DataSourceMigrationValidator.class).withPropertyValues("spring.liquibase.changeLog:classpath:db/changelog/db.changelog-city.yaml").withConfiguration(AutoConfigurations.of(LiquibaseAutoConfiguration.class)).run(( context) -> {
            assertThat(context).hasNotFailed();
            assertThat(context.getBean(.class).count).isEqualTo(0);
        });
    }

    @Test
    public void testDependencyToLiquibaseWithJdbcTemplateMixed() {
        this.contextRunner.withUserConfiguration(JdbcTemplateAutoConfigurationTests.NamedParameterDataSourceMigrationValidator.class).withPropertyValues("spring.liquibase.changeLog:classpath:db/changelog/db.changelog-city.yaml").withConfiguration(AutoConfigurations.of(LiquibaseAutoConfiguration.class)).run(( context) -> {
            assertThat(context).hasNotFailed();
            assertThat(context.getBean(.class)).isNotNull();
            assertThat(context.getBean(.class).count).isEqualTo(0);
        });
    }

    @Configuration
    static class CustomConfiguration {
        @Bean
        public JdbcOperations customJdbcOperations(DataSource dataSource) {
            return new JdbcTemplate(dataSource);
        }

        @Bean
        public NamedParameterJdbcOperations customNamedParameterJdbcOperations(DataSource dataSource) {
            return new NamedParameterJdbcTemplate(dataSource);
        }
    }

    @Configuration
    static class TestDataSourceConfiguration {
        @Bean
        public DataSource customDataSource() {
            return new TestDataSource();
        }
    }

    @Configuration
    static class MultiJdbcTemplateConfiguration {
        @Bean
        public JdbcTemplate test1Template() {
            return Mockito.mock(JdbcTemplate.class);
        }

        @Bean
        public JdbcTemplate test2Template() {
            return Mockito.mock(JdbcTemplate.class);
        }
    }

    @Configuration
    static class MultiJdbcTemplateUsingPrimaryConfiguration {
        @Bean
        @Primary
        public JdbcTemplate test1Template() {
            return Mockito.mock(JdbcTemplate.class);
        }

        @Bean
        public JdbcTemplate test2Template() {
            return Mockito.mock(JdbcTemplate.class);
        }
    }

    static class DataSourceInitializationValidator {
        private final Integer count;

        DataSourceInitializationValidator(JdbcTemplate jdbcTemplate) {
            this.count = jdbcTemplate.queryForObject("SELECT COUNT(*) from BAR", Integer.class);
        }
    }

    static class DataSourceMigrationValidator {
        private final Integer count;

        DataSourceMigrationValidator(JdbcTemplate jdbcTemplate) {
            this.count = jdbcTemplate.queryForObject("SELECT COUNT(*) from CITY", Integer.class);
        }
    }

    static class NamedParameterDataSourceMigrationValidator {
        private final Integer count;

        NamedParameterDataSourceMigrationValidator(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
            this.count = namedParameterJdbcTemplate.queryForObject("SELECT COUNT(*) from CITY", Collections.emptyMap(), Integer.class);
        }
    }
}

