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
package org.springframework.boot.autoconfigure.liquibase;


import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.function.Consumer;
import javax.sql.DataSource;
import liquibase.integration.spring.SpringLiquibase;
import liquibase.logging.core.Slf4jLogger;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.jdbc.EmbeddedDataSourceConfiguration;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.testsupport.Assume;
import org.springframework.boot.testsupport.rule.OutputCapture;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.util.ReflectionTestUtils;


/**
 * Tests for {@link LiquibaseAutoConfiguration}.
 *
 * @author Marcel Overdijk
 * @author Edd? Mel?ndez
 * @author Andy Wilkinson
 * @author Stephane Nicoll
 * @author Dominic Gunn
 */
public class LiquibaseAutoConfigurationTests {
    @Rule
    public final TemporaryFolder temp = new TemporaryFolder();

    @Rule
    public final OutputCapture output = new OutputCapture();

    private ApplicationContextRunner contextRunner = new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(LiquibaseAutoConfiguration.class)).withPropertyValues("spring.datasource.generate-unique-name=true");

    @Test
    public void noDataSource() {
        this.contextRunner.run(( context) -> assertThat(context).doesNotHaveBean(.class));
    }

    @Test
    public void defaultSpringLiquibase() {
        this.contextRunner.withUserConfiguration(EmbeddedDataSourceConfiguration.class).run(assertLiquibase(( liquibase) -> {
            assertThat(liquibase.getChangeLog()).isEqualTo("classpath:/db/changelog/db.changelog-master.yaml");
            assertThat(liquibase.getContexts()).isNull();
            assertThat(liquibase.getDefaultSchema()).isNull();
            assertThat(liquibase.isDropFirst()).isFalse();
        }));
    }

    @Test
    public void changelogXml() {
        this.contextRunner.withUserConfiguration(EmbeddedDataSourceConfiguration.class).withPropertyValues("spring.liquibase.change-log:classpath:/db/changelog/db.changelog-override.xml").run(assertLiquibase(( liquibase) -> assertThat(liquibase.getChangeLog()).isEqualTo("classpath:/db/changelog/db.changelog-override.xml")));
    }

    @Test
    public void changelogJson() {
        this.contextRunner.withUserConfiguration(EmbeddedDataSourceConfiguration.class).withPropertyValues("spring.liquibase.change-log:classpath:/db/changelog/db.changelog-override.json").run(assertLiquibase(( liquibase) -> assertThat(liquibase.getChangeLog()).isEqualTo("classpath:/db/changelog/db.changelog-override.json")));
    }

    @Test
    public void changelogSql() {
        Assume.javaEight();
        this.contextRunner.withUserConfiguration(EmbeddedDataSourceConfiguration.class).withPropertyValues("spring.liquibase.change-log:classpath:/db/changelog/db.changelog-override.sql").run(assertLiquibase(( liquibase) -> assertThat(liquibase.getChangeLog()).isEqualTo("classpath:/db/changelog/db.changelog-override.sql")));
    }

    @Test
    public void defaultValues() {
        this.contextRunner.withUserConfiguration(EmbeddedDataSourceConfiguration.class).run(assertLiquibase(( liquibase) -> {
            LiquibaseProperties properties = new LiquibaseProperties();
            assertThat(liquibase.getDatabaseChangeLogTable()).isEqualTo(properties.getDatabaseChangeLogTable());
            assertThat(liquibase.getDatabaseChangeLogLockTable()).isEqualTo(properties.getDatabaseChangeLogLockTable());
            assertThat(liquibase.isDropFirst()).isEqualTo(properties.isDropFirst());
            assertThat(liquibase.isTestRollbackOnUpdate()).isEqualTo(properties.isTestRollbackOnUpdate());
        }));
    }

    @Test
    public void overrideContexts() {
        this.contextRunner.withUserConfiguration(EmbeddedDataSourceConfiguration.class).withPropertyValues("spring.liquibase.contexts:test, production").run(assertLiquibase(( liquibase) -> assertThat(liquibase.getContexts()).isEqualTo("test, production")));
    }

    @Test
    public void overrideDefaultSchema() {
        this.contextRunner.withUserConfiguration(EmbeddedDataSourceConfiguration.class).withPropertyValues("spring.liquibase.default-schema:public").run(assertLiquibase(( liquibase) -> assertThat(liquibase.getDefaultSchema()).isEqualTo("public")));
    }

    @Test
    public void overrideLiquibaseInfrastructure() {
        this.contextRunner.withUserConfiguration(EmbeddedDataSourceConfiguration.class).withPropertyValues("spring.liquibase.liquibase-schema:public", "spring.liquibase.liquibase-tablespace:infra", "spring.liquibase.database-change-log-table:LIQUI_LOG", "spring.liquibase.database-change-log-lock-table:LIQUI_LOCK").run(( context) -> {
            SpringLiquibase liquibase = context.getBean(.class);
            assertThat(liquibase.getLiquibaseSchema()).isEqualTo("public");
            assertThat(liquibase.getLiquibaseTablespace()).isEqualTo("infra");
            assertThat(liquibase.getDatabaseChangeLogTable()).isEqualTo("LIQUI_LOG");
            assertThat(liquibase.getDatabaseChangeLogLockTable()).isEqualTo("LIQUI_LOCK");
            JdbcTemplate jdbcTemplate = new JdbcTemplate(context.getBean(.class));
            assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM public.LIQUI_LOG", .class)).isEqualTo(1);
            assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM public.LIQUI_LOCK", .class)).isEqualTo(1);
        });
    }

    @Test
    public void overrideDropFirst() {
        this.contextRunner.withUserConfiguration(EmbeddedDataSourceConfiguration.class).withPropertyValues("spring.liquibase.drop-first:true").run(assertLiquibase(( liquibase) -> assertThat(liquibase.isDropFirst()).isTrue()));
    }

    @Test
    public void overrideDataSource() {
        this.contextRunner.withUserConfiguration(EmbeddedDataSourceConfiguration.class).withPropertyValues("spring.liquibase.url:jdbc:hsqldb:mem:liquibase").run(assertLiquibase(( liquibase) -> {
            DataSource dataSource = liquibase.getDataSource();
            assertThat(isClosed()).isTrue();
            assertThat(getJdbcUrl()).isEqualTo("jdbc:hsqldb:mem:liquibase");
        }));
    }

    @Test
    public void overrideUser() {
        String jdbcUrl = "jdbc:hsqldb:mem:normal";
        this.contextRunner.withUserConfiguration(EmbeddedDataSourceConfiguration.class).withPropertyValues(("spring.datasource.url:" + jdbcUrl), "spring.datasource.username:not-sa", "spring.liquibase.user:sa").run(assertLiquibase(( liquibase) -> {
            DataSource dataSource = liquibase.getDataSource();
            assertThat(isClosed()).isTrue();
            assertThat(getJdbcUrl()).isEqualTo(jdbcUrl);
            assertThat(getUsername()).isEqualTo("sa");
        }));
    }

    @Test
    public void overrideTestRollbackOnUpdate() {
        this.contextRunner.withUserConfiguration(EmbeddedDataSourceConfiguration.class).withPropertyValues("spring.liquibase.test-rollback-on-update:true").run(( context) -> {
            SpringLiquibase liquibase = context.getBean(.class);
            assertThat(liquibase.isTestRollbackOnUpdate()).isTrue();
        });
    }

    @Test
    public void changeLogDoesNotExist() {
        this.contextRunner.withUserConfiguration(EmbeddedDataSourceConfiguration.class).withPropertyValues("spring.liquibase.change-log:classpath:/no-such-changelog.yaml").run(( context) -> {
            assertThat(context).hasFailed();
            assertThat(context).getFailure().isInstanceOf(.class);
        });
    }

    @Test
    public void logging() {
        this.contextRunner.withUserConfiguration(EmbeddedDataSourceConfiguration.class).run(assertLiquibase(( liquibase) -> {
            Object log = ReflectionTestUtils.getField(liquibase, "log");
            assertThat(log).isInstanceOf(Slf4jLogger.class);
            assertThat(this.output.toString()).doesNotContain(": liquibase:");
        }));
    }

    @Test
    public void overrideLabels() {
        this.contextRunner.withUserConfiguration(EmbeddedDataSourceConfiguration.class).withPropertyValues("spring.liquibase.labels:test, production").run(assertLiquibase(( liquibase) -> assertThat(liquibase.getLabels()).isEqualTo("test, production")));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testOverrideParameters() {
        this.contextRunner.withUserConfiguration(EmbeddedDataSourceConfiguration.class).withPropertyValues("spring.liquibase.parameters.foo:bar").run(assertLiquibase(( liquibase) -> {
            Map<String, String> parameters = ((Map<String, String>) (ReflectionTestUtils.getField(liquibase, "parameters")));
            assertThat(parameters.containsKey("foo")).isTrue();
            assertThat(parameters.get("foo")).isEqualTo("bar");
        }));
    }

    @Test
    public void rollbackFile() throws IOException {
        File file = this.temp.newFile("rollback-file.sql");
        this.contextRunner.withUserConfiguration(EmbeddedDataSourceConfiguration.class).withPropertyValues(("spring.liquibase.rollbackFile:" + (file.getAbsolutePath()))).run(( context) -> {
            SpringLiquibase liquibase = context.getBean(.class);
            File actualFile = ((File) (ReflectionTestUtils.getField(liquibase, "rollbackFile")));
            assertThat(actualFile).isEqualTo(file).exists();
            assertThat(contentOf(file)).contains("DROP TABLE PUBLIC.customer;");
        });
    }

    @Test
    public void liquibaseDataSource() {
        this.contextRunner.withUserConfiguration(LiquibaseAutoConfigurationTests.LiquibaseDataSourceConfiguration.class, EmbeddedDataSourceConfiguration.class).run(( context) -> {
            SpringLiquibase liquibase = context.getBean(.class);
            assertThat(liquibase.getDataSource()).isEqualTo(context.getBean("liquibaseDataSource"));
        });
    }

    @Test
    public void liquibaseDataSourceWithoutDataSourceAutoConfiguration() {
        this.contextRunner.withUserConfiguration(LiquibaseAutoConfigurationTests.LiquibaseDataSourceConfiguration.class).run(( context) -> {
            SpringLiquibase liquibase = context.getBean(.class);
            assertThat(liquibase.getDataSource()).isEqualTo(context.getBean("liquibaseDataSource"));
        });
    }

    @Configuration
    static class LiquibaseDataSourceConfiguration {
        @Bean
        @Primary
        public DataSource normalDataSource() {
            return DataSourceBuilder.create().url("jdbc:hsqldb:mem:normal").username("sa").build();
        }

        @LiquibaseDataSource
        @Bean
        public DataSource liquibaseDataSource() {
            return DataSourceBuilder.create().url("jdbc:hsqldb:mem:liquibasetest").username("sa").build();
        }
    }
}

