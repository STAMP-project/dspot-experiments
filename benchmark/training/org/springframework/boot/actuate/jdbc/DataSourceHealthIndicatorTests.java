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
package org.springframework.boot.actuate.jdbc;


import DatabaseDriver.HSQLDB;
import Status.DOWN;
import Status.UP;
import java.sql.Connection;
import javax.sql.DataSource;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.boot.actuate.health.Health;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;


/**
 * Tests for {@link DataSourceHealthIndicator}.
 *
 * @author Dave Syer
 * @author Stephane Nicoll
 */
public class DataSourceHealthIndicatorTests {
    private final DataSourceHealthIndicator indicator = new DataSourceHealthIndicator();

    private SingleConnectionDataSource dataSource;

    @Test
    public void healthIndicatorWithDefaultSettings() {
        this.indicator.setDataSource(this.dataSource);
        Health health = this.indicator.health();
        assertThat(health.getStatus()).isEqualTo(UP);
        assertThat(health.getDetails()).containsOnly(entry("database", "HSQL Database Engine"), entry("result", 1L), entry("validationQuery", HSQLDB.getValidationQuery()));
    }

    @Test
    public void healthIndicatorWithCustomValidationQuery() {
        String customValidationQuery = "SELECT COUNT(*) from FOO";
        execute("CREATE TABLE FOO (id INTEGER IDENTITY PRIMARY KEY)");
        this.indicator.setDataSource(this.dataSource);
        this.indicator.setQuery(customValidationQuery);
        Health health = this.indicator.health();
        assertThat(health.getStatus()).isEqualTo(UP);
        assertThat(health.getDetails()).containsOnly(entry("database", "HSQL Database Engine"), entry("result", 0L), entry("validationQuery", customValidationQuery));
    }

    @Test
    public void healthIndicatorWithInvalidValidationQuery() {
        String invalidValidationQuery = "SELECT COUNT(*) from BAR";
        this.indicator.setDataSource(this.dataSource);
        this.indicator.setQuery(invalidValidationQuery);
        Health health = this.indicator.health();
        assertThat(health.getStatus()).isEqualTo(DOWN);
        assertThat(health.getDetails()).contains(entry("database", "HSQL Database Engine"), entry("validationQuery", invalidValidationQuery));
        assertThat(health.getDetails()).containsOnlyKeys("database", "error", "validationQuery");
    }

    @Test
    public void healthIndicatorCloseConnection() throws Exception {
        DataSource dataSource = Mockito.mock(DataSource.class);
        Connection connection = Mockito.mock(Connection.class);
        BDDMockito.given(connection.getMetaData()).willReturn(this.dataSource.getConnection().getMetaData());
        BDDMockito.given(dataSource.getConnection()).willReturn(connection);
        this.indicator.setDataSource(dataSource);
        Health health = this.indicator.health();
        assertThat(health.getDetails().get("database")).isNotNull();
        Mockito.verify(connection, Mockito.times(2)).close();
    }
}

