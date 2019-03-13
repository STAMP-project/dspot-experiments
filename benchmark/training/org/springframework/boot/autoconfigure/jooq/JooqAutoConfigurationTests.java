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
package org.springframework.boot.autoconfigure.jooq;


import java.util.concurrent.Executor;
import javax.sql.DataSource;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.ExecuteListener;
import org.jooq.ExecuteListenerProvider;
import org.jooq.ExecutorProvider;
import org.jooq.Record;
import org.jooq.RecordListener;
import org.jooq.RecordListenerProvider;
import org.jooq.RecordMapper;
import org.jooq.RecordMapperProvider;
import org.jooq.RecordType;
import org.jooq.RecordUnmapper;
import org.jooq.RecordUnmapperProvider;
import org.jooq.TransactionListener;
import org.jooq.TransactionListenerProvider;
import org.jooq.TransactionalRunnable;
import org.jooq.VisitListener;
import org.jooq.VisitListenerProvider;
import org.junit.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;


/**
 * Tests for {@link JooqAutoConfiguration}.
 *
 * @author Andreas Ahlenstorf
 * @author Phillip Webb
 * @author Andy Wilkinson
 * @author Stephane Nicoll
 * @author Dmytro Nosan
 */
public class JooqAutoConfigurationTests {
    private ApplicationContextRunner contextRunner = new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(JooqAutoConfiguration.class)).withPropertyValues("spring.datasource.name:jooqtest");

    @Test
    public void noDataSource() {
        this.contextRunner.run(( context) -> assertThat(context.getBeansOfType(.class)).isEmpty());
    }

    @Test
    public void jooqWithoutTx() {
        this.contextRunner.withUserConfiguration(JooqAutoConfigurationTests.JooqDataSourceConfiguration.class).run(( context) -> {
            assertThat(context).doesNotHaveBean(.class);
            assertThat(context).doesNotHaveBean(.class);
            DSLContext dsl = context.getBean(.class);
            dsl.execute("create table jooqtest (name varchar(255) primary key);");
            dsl.transaction(new org.springframework.boot.autoconfigure.jooq.AssertFetch(dsl, "select count(*) as total from jooqtest;", "0"));
            dsl.transaction(new org.springframework.boot.autoconfigure.jooq.ExecuteSql(dsl, "insert into jooqtest (name) values ('foo');"));
            dsl.transaction(new org.springframework.boot.autoconfigure.jooq.AssertFetch(dsl, "select count(*) as total from jooqtest;", "1"));
            assertThatExceptionOfType(.class).isThrownBy(() -> dsl.transaction(new org.springframework.boot.autoconfigure.jooq.ExecuteSql(dsl, "insert into jooqtest (name) values ('bar');", "insert into jooqtest (name) values ('foo');")));
            dsl.transaction(new org.springframework.boot.autoconfigure.jooq.AssertFetch(dsl, "select count(*) as total from jooqtest;", "2"));
        });
    }

    @Test
    public void jooqWithTx() {
        this.contextRunner.withUserConfiguration(JooqAutoConfigurationTests.JooqDataSourceConfiguration.class, JooqAutoConfigurationTests.TxManagerConfiguration.class).run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            DSLContext dsl = context.getBean(.class);
            assertThat(dsl.configuration().dialect()).isEqualTo(SQLDialect.HSQLDB);
            dsl.execute("create table jooqtest_tx (name varchar(255) primary key);");
            dsl.transaction(new org.springframework.boot.autoconfigure.jooq.AssertFetch(dsl, "select count(*) as total from jooqtest_tx;", "0"));
            dsl.transaction(new org.springframework.boot.autoconfigure.jooq.ExecuteSql(dsl, "insert into jooqtest_tx (name) values ('foo');"));
            dsl.transaction(new org.springframework.boot.autoconfigure.jooq.AssertFetch(dsl, "select count(*) as total from jooqtest_tx;", "1"));
            assertThatExceptionOfType(.class).isThrownBy(() -> dsl.transaction(new org.springframework.boot.autoconfigure.jooq.ExecuteSql(dsl, "insert into jooqtest (name) values ('bar');", "insert into jooqtest (name) values ('foo');")));
            dsl.transaction(new org.springframework.boot.autoconfigure.jooq.AssertFetch(dsl, "select count(*) as total from jooqtest_tx;", "1"));
        });
    }

    @Test
    public void customProvidersArePickedUp() {
        this.contextRunner.withUserConfiguration(JooqAutoConfigurationTests.JooqDataSourceConfiguration.class, JooqAutoConfigurationTests.TxManagerConfiguration.class, JooqAutoConfigurationTests.TestRecordMapperProvider.class, JooqAutoConfigurationTests.TestRecordUnmapperProvider.class, JooqAutoConfigurationTests.TestRecordListenerProvider.class, JooqAutoConfigurationTests.TestExecuteListenerProvider.class, JooqAutoConfigurationTests.TestVisitListenerProvider.class, JooqAutoConfigurationTests.TestTransactionListenerProvider.class, JooqAutoConfigurationTests.TestExecutorProvider.class).run(( context) -> {
            DSLContext dsl = context.getBean(.class);
            assertThat(dsl.configuration().recordMapperProvider().getClass()).isEqualTo(.class);
            assertThat(dsl.configuration().recordUnmapperProvider().getClass()).isEqualTo(.class);
            assertThat(dsl.configuration().executorProvider().getClass()).isEqualTo(.class);
            assertThat(dsl.configuration().recordListenerProviders().length).isEqualTo(1);
            ExecuteListenerProvider[] executeListenerProviders = dsl.configuration().executeListenerProviders();
            assertThat(executeListenerProviders.length).isEqualTo(2);
            assertThat(executeListenerProviders[0]).isInstanceOf(.class);
            assertThat(executeListenerProviders[1]).isInstanceOf(.class);
            assertThat(dsl.configuration().visitListenerProviders().length).isEqualTo(1);
            assertThat(dsl.configuration().transactionListenerProviders().length).isEqualTo(1);
        });
    }

    @Test
    public void relaxedBindingOfSqlDialect() {
        this.contextRunner.withUserConfiguration(JooqAutoConfigurationTests.JooqDataSourceConfiguration.class).withPropertyValues("spring.jooq.sql-dialect:PoSTGrES").run(( context) -> assertThat(context.getBean(.class).dialect()).isEqualTo(SQLDialect.POSTGRES));
    }

    private static class AssertFetch implements TransactionalRunnable {
        private final DSLContext dsl;

        private final String sql;

        private final String expected;

        AssertFetch(DSLContext dsl, String sql, String expected) {
            this.dsl = dsl;
            this.sql = sql;
            this.expected = expected;
        }

        @Override
        public void run(Configuration configuration) {
            assertThat(this.dsl.fetch(this.sql).getValue(0, 0).toString()).isEqualTo(this.expected);
        }
    }

    private static class ExecuteSql implements TransactionalRunnable {
        private final DSLContext dsl;

        private final String[] sql;

        ExecuteSql(DSLContext dsl, String... sql) {
            this.dsl = dsl;
            this.sql = sql;
        }

        @Override
        public void run(Configuration configuration) {
            for (String statement : this.sql) {
                this.dsl.execute(statement);
            }
        }
    }

    @org.springframework.context.annotation.Configuration
    protected static class JooqDataSourceConfiguration {
        @Bean
        public DataSource jooqDataSource() {
            return DataSourceBuilder.create().url("jdbc:hsqldb:mem:jooqtest").username("sa").build();
        }
    }

    @org.springframework.context.annotation.Configuration
    protected static class TxManagerConfiguration {
        @Bean
        public PlatformTransactionManager transactionManager(DataSource dataSource) {
            return new DataSourceTransactionManager(dataSource);
        }
    }

    protected static class TestRecordMapperProvider implements RecordMapperProvider {
        @Override
        public <R extends Record, E> RecordMapper<R, E> provide(RecordType<R> recordType, Class<? extends E> aClass) {
            return null;
        }
    }

    protected static class TestRecordUnmapperProvider implements RecordUnmapperProvider {
        @Override
        public <E, R extends Record> RecordUnmapper<E, R> provide(Class<? extends E> aClass, RecordType<R> recordType) {
            return null;
        }
    }

    protected static class TestRecordListenerProvider implements RecordListenerProvider {
        @Override
        public RecordListener provide() {
            return null;
        }
    }

    @Order(100)
    protected static class TestExecuteListenerProvider implements ExecuteListenerProvider {
        @Override
        public ExecuteListener provide() {
            return null;
        }
    }

    protected static class TestVisitListenerProvider implements VisitListenerProvider {
        @Override
        public VisitListener provide() {
            return null;
        }
    }

    protected static class TestTransactionListenerProvider implements TransactionListenerProvider {
        @Override
        public TransactionListener provide() {
            return null;
        }
    }

    protected static class TestExecutorProvider implements ExecutorProvider {
        @Override
        public Executor provide() {
            return null;
        }
    }
}

