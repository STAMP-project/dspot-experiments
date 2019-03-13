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
package org.springframework.boot.autoconfigure.jdbc;


import DatabaseDriver.HSQLDB;
import com.zaxxer.hikari.HikariDataSource;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverPropertyInfo;
import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;
import java.util.Random;
import java.util.function.Consumer;
import java.util.logging.Logger;
import javax.sql.DataSource;
import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;


/**
 * Tests for {@link DataSourceAutoConfiguration}.
 *
 * @author Dave Syer
 * @author Stephane Nicoll
 */
public class DataSourceAutoConfigurationTests {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(DataSourceAutoConfiguration.class)).withPropertyValues("spring.datasource.initialization-mode=never", ("spring.datasource.url:jdbc:hsqldb:mem:testdb-" + (new Random().nextInt())));

    @Test
    public void testDefaultDataSourceExists() {
        this.contextRunner.run(( context) -> assertThat(context).hasSingleBean(.class));
    }

    @Test
    public void testDataSourceHasEmbeddedDefault() {
        this.contextRunner.run(( context) -> {
            HikariDataSource dataSource = context.getBean(.class);
            assertThat(dataSource.getJdbcUrl()).isNotNull();
            assertThat(dataSource.getDriverClassName()).isNotNull();
        });
    }

    @Test
    public void testBadUrl() {
        this.contextRunner.withPropertyValues("spring.datasource.url:jdbc:not-going-to-work").withClassLoader(new DataSourceAutoConfigurationTests.DisableEmbeddedDatabaseClassLoader()).run(( context) -> assertThat(context).getFailure().isInstanceOf(.class));
    }

    @Test
    public void testBadDriverClass() {
        this.contextRunner.withPropertyValues("spring.datasource.driverClassName:org.none.jdbcDriver").run(( context) -> assertThat(context).getFailure().isInstanceOf(.class).hasMessageContaining("org.none.jdbcDriver"));
    }

    @Test
    public void hikariValidatesConnectionByDefault() {
        assertDataSource(HikariDataSource.class, Collections.singletonList("org.apache.tomcat"), ( dataSource) -> assertThat(getConnectionTestQuery()).isNull());
    }

    @Test
    public void tomcatIsFallback() {
        assertDataSource(DataSource.class, Collections.singletonList("com.zaxxer.hikari"), ( dataSource) -> assertThat(getUrl()).startsWith("jdbc:hsqldb:mem:testdb"));
    }

    @Test
    public void tomcatValidatesConnectionByDefault() {
        assertDataSource(DataSource.class, Collections.singletonList("com.zaxxer.hikari"), ( dataSource) -> {
            assertThat(isTestOnBorrow()).isTrue();
            assertThat(getValidationQuery()).isEqualTo(HSQLDB.getValidationQuery());
        });
    }

    @Test
    public void commonsDbcp2IsFallback() {
        assertDataSource(BasicDataSource.class, Arrays.asList("com.zaxxer.hikari", "org.apache.tomcat"), ( dataSource) -> assertThat(getUrl()).startsWith("jdbc:hsqldb:mem:testdb"));
    }

    @Test
    public void commonsDbcp2ValidatesConnectionByDefault() {
        assertDataSource(BasicDataSource.class, Arrays.asList("com.zaxxer.hikari", "org.apache.tomcat"), ( dataSource) -> {
            assertThat(getTestOnBorrow()).isTrue();
            assertThat(getValidationQuery()).isNull();// Use

            // Connection#isValid()
        });
    }

    @Test
    @SuppressWarnings("resource")
    public void testEmbeddedTypeDefaultsUsername() {
        this.contextRunner.withPropertyValues("spring.datasource.driverClassName:org.hsqldb.jdbcDriver", "spring.datasource.url:jdbc:hsqldb:mem:testdb").run(( context) -> {
            DataSource bean = context.getBean(.class);
            HikariDataSource pool = ((HikariDataSource) (bean));
            assertThat(pool.getDriverClassName()).isEqualTo("org.hsqldb.jdbcDriver");
            assertThat(pool.getUsername()).isEqualTo("sa");
        });
    }

    /**
     * This test makes sure that if no supported data source is present, a datasource is
     * still created if "spring.datasource.type" is present.
     */
    @Test
    public void explicitTypeNoSupportedDataSource() {
        this.contextRunner.withClassLoader(new FilteredClassLoader("org.apache.tomcat", "com.zaxxer.hikari", "org.apache.commons.dbcp", "org.apache.commons.dbcp2")).withPropertyValues("spring.datasource.driverClassName:org.hsqldb.jdbcDriver", "spring.datasource.url:jdbc:hsqldb:mem:testdb", ("spring.datasource.type:" + (SimpleDriverDataSource.class.getName()))).run(this::containsOnlySimpleDriverDataSource);
    }

    @Test
    public void explicitTypeSupportedDataSource() {
        this.contextRunner.withPropertyValues("spring.datasource.driverClassName:org.hsqldb.jdbcDriver", "spring.datasource.url:jdbc:hsqldb:mem:testdb", ("spring.datasource.type:" + (SimpleDriverDataSource.class.getName()))).run(this::containsOnlySimpleDriverDataSource);
    }

    @Test
    public void testExplicitDriverClassClearsUsername() {
        this.contextRunner.withPropertyValues(("spring.datasource.driverClassName:" + (DataSourceAutoConfigurationTests.DatabaseTestDriver.class.getName())), "spring.datasource.url:jdbc:foo://localhost").run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            HikariDataSource dataSource = context.getBean(.class);
            assertThat(dataSource.getDriverClassName()).isEqualTo(.class.getName());
            assertThat(dataSource.getUsername()).isNull();
        });
    }

    @Test
    public void testDefaultDataSourceCanBeOverridden() {
        this.contextRunner.withUserConfiguration(DataSourceAutoConfigurationTests.TestDataSourceConfiguration.class).run(( context) -> assertThat(context).getBean(.class).isInstanceOf(.class));
    }

    @Test
    public void testDataSourceIsInitializedEarly() {
        this.contextRunner.withUserConfiguration(DataSourceAutoConfigurationTests.TestInitializedDataSourceConfiguration.class).withPropertyValues("spring.datasource.initialization-mode=always").run(( context) -> assertThat(context.getBean(.class).called).isTrue());
    }

    @Configuration
    static class TestDataSourceConfiguration {
        private BasicDataSource pool;

        @Bean
        public DataSource dataSource() {
            this.pool = new BasicDataSource();
            this.pool.setDriverClassName("org.hsqldb.jdbcDriver");
            this.pool.setUrl("jdbc:hsqldb:mem:overridedb");
            this.pool.setUsername("sa");
            return this.pool;
        }
    }

    @Configuration
    static class TestInitializedDataSourceConfiguration {
        private boolean called;

        @Autowired
        public void validateDataSourceIsInitialized(DataSource dataSource) {
            // Inject the datasource to validate it is initialized at the injection point
            JdbcTemplate template = new JdbcTemplate(dataSource);
            assertThat(template.queryForObject("SELECT COUNT(*) from BAR", Integer.class)).isEqualTo(1);
            this.called = true;
        }
    }

    // see testExplicitDriverClassClearsUsername
    public static class DatabaseTestDriver implements Driver {
        @Override
        public Connection connect(String url, Properties info) {
            return Mockito.mock(Connection.class);
        }

        @Override
        public boolean acceptsURL(String url) {
            return true;
        }

        @Override
        public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) {
            return new DriverPropertyInfo[0];
        }

        @Override
        public int getMajorVersion() {
            return 1;
        }

        @Override
        public int getMinorVersion() {
            return 0;
        }

        @Override
        public boolean jdbcCompliant() {
            return false;
        }

        @Override
        public Logger getParentLogger() {
            return Mockito.mock(Logger.class);
        }
    }

    private static class DisableEmbeddedDatabaseClassLoader extends URLClassLoader {
        DisableEmbeddedDatabaseClassLoader() {
            super(new URL[0], DataSourceAutoConfigurationTests.DisableEmbeddedDatabaseClassLoader.class.getClassLoader());
        }

        @Override
        protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
            for (EmbeddedDatabaseConnection candidate : EmbeddedDatabaseConnection.values()) {
                if (name.equals(candidate.getDriverClassName())) {
                    throw new ClassNotFoundException();
                }
            }
            return super.loadClass(name, resolve);
        }
    }
}

