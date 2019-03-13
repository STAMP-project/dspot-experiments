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


import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;
import java.util.UUID;
import javax.sql.DataSource;
import org.junit.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;


/**
 * Tests for {@link DataSourceInitializerInvoker}.
 *
 * @author Dave Syer
 * @author Stephane Nicoll
 */
public class DataSourceInitializerInvokerTests {
    private ApplicationContextRunner contextRunner = new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(DataSourceAutoConfiguration.class)).withPropertyValues("spring.datasource.initialization-mode=never", ("spring.datasource.url:jdbc:hsqldb:mem:init-" + (UUID.randomUUID())));

    @Test
    public void dataSourceInitialized() {
        this.contextRunner.withPropertyValues("spring.datasource.initialization-mode:always").run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            DataSource dataSource = context.getBean(.class);
            assertThat(dataSource).isInstanceOf(.class);
            assertDataSourceIsInitialized(dataSource);
        });
    }

    @Test
    public void initializationAppliesToCustomDataSource() {
        this.contextRunner.withUserConfiguration(DataSourceInitializerInvokerTests.OneDataSource.class).withPropertyValues("spring.datasource.initialization-mode:always").run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            assertDataSourceIsInitialized(context.getBean(.class));
        });
    }

    @Test
    public void dataSourceInitializedWithExplicitScript() {
        this.contextRunner.withPropertyValues("spring.datasource.initialization-mode:always", ("spring.datasource.schema:" + (getRelativeLocationFor("schema.sql"))), ("spring.datasource.data:" + (getRelativeLocationFor("data.sql")))).run(( context) -> {
            DataSource dataSource = context.getBean(.class);
            assertThat(dataSource).isInstanceOf(.class);
            assertThat(dataSource).isNotNull();
            JdbcOperations template = new JdbcTemplate(dataSource);
            assertThat(template.queryForObject("SELECT COUNT(*) from FOO", .class)).isEqualTo(1);
        });
    }

    @Test
    public void dataSourceInitializedWithMultipleScripts() {
        this.contextRunner.withPropertyValues("spring.datasource.initialization-mode:always", ((("spring.datasource.schema:" + (getRelativeLocationFor("schema.sql"))) + ",") + (getRelativeLocationFor("another.sql"))), ("spring.datasource.data:" + (getRelativeLocationFor("data.sql")))).run(( context) -> {
            DataSource dataSource = context.getBean(.class);
            assertThat(dataSource).isInstanceOf(.class);
            assertThat(dataSource).isNotNull();
            JdbcOperations template = new JdbcTemplate(dataSource);
            assertThat(template.queryForObject("SELECT COUNT(*) from FOO", .class)).isEqualTo(1);
            assertThat(template.queryForObject("SELECT COUNT(*) from SPAM", .class)).isEqualTo(0);
        });
    }

    @Test
    public void dataSourceInitializedWithExplicitSqlScriptEncoding() {
        this.contextRunner.withPropertyValues("spring.datasource.initialization-mode:always", "spring.datasource.sqlScriptEncoding:UTF-8", ("spring.datasource.schema:" + (getRelativeLocationFor("encoding-schema.sql"))), ("spring.datasource.data:" + (getRelativeLocationFor("encoding-data.sql")))).run(( context) -> {
            DataSource dataSource = context.getBean(.class);
            assertThat(dataSource).isInstanceOf(.class);
            assertThat(dataSource).isNotNull();
            JdbcOperations template = new JdbcTemplate(dataSource);
            assertThat(template.queryForObject("SELECT COUNT(*) from BAR", .class)).isEqualTo(2);
            assertThat(template.queryForObject("SELECT name from BAR WHERE id=1", .class)).isEqualTo("bar");
            assertThat(template.queryForObject("SELECT name from BAR WHERE id=2", .class)).isEqualTo("??");
        });
    }

    @Test
    public void initializationDisabled() {
        this.contextRunner.run(assertInitializationIsDisabled());
    }

    @Test
    public void initializationDoesNotApplyWithSeveralDataSources() {
        this.contextRunner.withUserConfiguration(DataSourceInitializerInvokerTests.TwoDataSources.class).withPropertyValues("spring.datasource.initialization-mode:always").run(( context) -> {
            assertThat(context.getBeanNamesForType(.class)).hasSize(2);
            assertDataSourceNotInitialized(context.getBean("oneDataSource", .class));
            assertDataSourceNotInitialized(context.getBean("twoDataSource", .class));
        });
    }

    @Test
    public void dataSourceInitializedWithSchemaCredentials() {
        this.contextRunner.withPropertyValues("spring.datasource.initialization-mode:always", "spring.datasource.sqlScriptEncoding:UTF-8", ("spring.datasource.schema:" + (getRelativeLocationFor("encoding-schema.sql"))), ("spring.datasource.data:" + (getRelativeLocationFor("encoding-data.sql"))), "spring.datasource.schema-username:admin", "spring.datasource.schema-password:admin").run(( context) -> {
            assertThat(context).hasFailed();
            assertThat(context.getStartupFailure()).isInstanceOf(.class);
        });
    }

    @Test
    public void dataSourceInitializedWithDataCredentials() {
        this.contextRunner.withPropertyValues("spring.datasource.initialization-mode:always", "spring.datasource.sqlScriptEncoding:UTF-8", ("spring.datasource.schema:" + (getRelativeLocationFor("encoding-schema.sql"))), ("spring.datasource.data:" + (getRelativeLocationFor("encoding-data.sql"))), "spring.datasource.data-username:admin", "spring.datasource.data-password:admin").run(( context) -> {
            assertThat(context).hasFailed();
            assertThat(context.getStartupFailure()).isInstanceOf(.class);
        });
    }

    @Test
    public void multipleScriptsAppliedInLexicalOrder() {
        new ApplicationContextRunner(() -> {
            AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
            context.setResourceLoader(new org.springframework.boot.autoconfigure.jdbc.ReverseOrderResourceLoader(new DefaultResourceLoader()));
            return context;
        }).withConfiguration(AutoConfigurations.of(DataSourceAutoConfiguration.class)).withPropertyValues("spring.datasource.initialization-mode=always", ("spring.datasource.url:jdbc:hsqldb:mem:testdb-" + (new Random().nextInt())), ("spring.datasource.schema:classpath*:" + (getRelativeLocationFor("lexical-schema-*.sql"))), ("spring.datasource.data:classpath*:" + (getRelativeLocationFor("data.sql")))).run(( context) -> {
            DataSource dataSource = context.getBean(.class);
            assertThat(dataSource).isInstanceOf(.class);
            assertThat(dataSource).isNotNull();
            JdbcOperations template = new JdbcTemplate(dataSource);
            assertThat(template.queryForObject("SELECT COUNT(*) from FOO", .class)).isEqualTo(1);
        });
    }

    @Test
    public void testDataSourceInitializedWithInvalidSchemaResource() {
        this.contextRunner.withPropertyValues("spring.datasource.initialization-mode:always", "spring.datasource.schema:classpath:does/not/exist.sql").run(( context) -> {
            assertThat(context).hasFailed();
            assertThat(context.getStartupFailure()).isInstanceOf(.class);
            assertThat(context.getStartupFailure()).hasMessageContaining("does/not/exist.sql");
            assertThat(context.getStartupFailure()).hasMessageContaining("spring.datasource.schema");
        });
    }

    @Test
    public void dataSourceInitializedWithInvalidDataResource() {
        this.contextRunner.withPropertyValues("spring.datasource.initialization-mode:always", ("spring.datasource.schema:" + (getRelativeLocationFor("schema.sql"))), "spring.datasource.data:classpath:does/not/exist.sql").run(( context) -> {
            assertThat(context).hasFailed();
            assertThat(context.getStartupFailure()).isInstanceOf(.class);
            assertThat(context.getStartupFailure()).hasMessageContaining("does/not/exist.sql");
            assertThat(context.getStartupFailure()).hasMessageContaining("spring.datasource.data");
        });
    }

    @Configuration
    protected static class OneDataSource {
        @Bean
        public DataSource oneDataSource() {
            return new TestDataSource();
        }
    }

    @Configuration
    protected static class TwoDataSources extends DataSourceInitializerInvokerTests.OneDataSource {
        @Bean
        public DataSource twoDataSource() {
            return new TestDataSource();
        }
    }

    /**
     * {@link ResourcePatternResolver} used to ensure consistently wrong resource
     * ordering.
     */
    private static class ReverseOrderResourceLoader implements ResourcePatternResolver {
        private final ResourcePatternResolver resolver;

        ReverseOrderResourceLoader(ResourceLoader loader) {
            this.resolver = ResourcePatternUtils.getResourcePatternResolver(loader);
        }

        @Override
        public Resource getResource(String location) {
            return this.resolver.getResource(location);
        }

        @Override
        public ClassLoader getClassLoader() {
            return this.resolver.getClassLoader();
        }

        @Override
        public Resource[] getResources(String locationPattern) throws IOException {
            Resource[] resources = this.resolver.getResources(locationPattern);
            Arrays.sort(resources, Comparator.comparing(Resource::getFilename).reversed());
            return resources;
        }
    }
}

