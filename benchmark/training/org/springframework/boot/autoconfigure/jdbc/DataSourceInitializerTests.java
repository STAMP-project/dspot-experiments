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


import DataSourceInitializationMode.ALWAYS;
import DataSourceInitializationMode.NEVER;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.jdbc.core.JdbcTemplate;


/**
 * Tests for {@link DataSourceInitializer}.
 *
 * @author Stephane Nicoll
 */
public class DataSourceInitializerTests {
    @Test
    public void initializeEmbeddedByDefault() {
        try (HikariDataSource dataSource = createDataSource()) {
            DataSourceInitializer initializer = new DataSourceInitializer(dataSource, new DataSourceProperties());
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            assertThat(initializer.createSchema()).isTrue();
            assertNumberOfRows(jdbcTemplate, 0);
            initializer.initSchema();
            assertNumberOfRows(jdbcTemplate, 1);
        }
    }

    @Test
    public void initializeWithModeAlways() {
        try (HikariDataSource dataSource = createDataSource()) {
            DataSourceProperties properties = new DataSourceProperties();
            properties.setInitializationMode(ALWAYS);
            DataSourceInitializer initializer = new DataSourceInitializer(dataSource, properties);
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            assertThat(initializer.createSchema()).isTrue();
            assertNumberOfRows(jdbcTemplate, 0);
            initializer.initSchema();
            assertNumberOfRows(jdbcTemplate, 1);
        }
    }

    @Test
    public void initializeWithModeNever() {
        try (HikariDataSource dataSource = createDataSource()) {
            DataSourceProperties properties = new DataSourceProperties();
            properties.setInitializationMode(NEVER);
            DataSourceInitializer initializer = new DataSourceInitializer(dataSource, properties);
            assertThat(initializer.createSchema()).isFalse();
        }
    }

    @Test
    public void initializeOnlyEmbeddedByDefault() throws SQLException {
        DatabaseMetaData metadata = Mockito.mock(DatabaseMetaData.class);
        BDDMockito.given(metadata.getDatabaseProductName()).willReturn("MySQL");
        Connection connection = Mockito.mock(Connection.class);
        BDDMockito.given(connection.getMetaData()).willReturn(metadata);
        DataSource dataSource = Mockito.mock(DataSource.class);
        BDDMockito.given(dataSource.getConnection()).willReturn(connection);
        DataSourceInitializer initializer = new DataSourceInitializer(dataSource, new DataSourceProperties());
        assertThat(initializer.createSchema()).isFalse();
        Mockito.verify(dataSource).getConnection();
    }
}

