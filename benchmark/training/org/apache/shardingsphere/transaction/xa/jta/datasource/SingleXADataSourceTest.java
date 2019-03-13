/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.shardingsphere.transaction.xa.jta.datasource;


import DatabaseType.H2;
import DatabaseType.MySQL;
import com.alibaba.druid.pool.xa.DruidXADataSource;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import javax.sql.DataSource;
import javax.sql.XADataSource;
import lombok.SneakyThrows;
import org.apache.shardingsphere.core.constant.DatabaseType;
import org.apache.shardingsphere.transaction.xa.fixture.DataSourceUtils;
import org.apache.shardingsphere.transaction.xa.jta.connection.SingleXAConnection;
import org.h2.jdbcx.JdbcDataSource;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public final class SingleXADataSourceTest {
    @Test
    public void assertBuildSingleXADataSourceOfXA() {
        DataSource dataSource = DataSourceUtils.build(DruidXADataSource.class, MySQL, "ds1");
        SingleXADataSource actual = new SingleXADataSource(DatabaseType.MySQL, "ds1", dataSource);
        Assert.assertThat(actual.getResourceName(), CoreMatchers.is("ds1"));
        Assert.assertThat(actual.getXaDataSource(), CoreMatchers.is(((XADataSource) (dataSource))));
    }

    @Test
    public void assertBuildSingleXADataSourceOfNoneXA() {
        DataSource dataSource = DataSourceUtils.build(HikariDataSource.class, H2, "ds1");
        SingleXADataSource actual = new SingleXADataSource(DatabaseType.H2, "ds1", dataSource);
        Assert.assertThat(actual.getResourceName(), CoreMatchers.is("ds1"));
        Assert.assertThat(actual.getXaDataSource(), CoreMatchers.instanceOf(JdbcDataSource.class));
        JdbcDataSource jdbcDataSource = ((JdbcDataSource) (actual.getXaDataSource()));
        Assert.assertThat(jdbcDataSource.getUser(), CoreMatchers.is("root"));
        Assert.assertThat(jdbcDataSource.getPassword(), CoreMatchers.is("root"));
    }

    @Test
    @SneakyThrows
    public void assertGetXAConnectionOfXA() {
        DataSource dataSource = DataSourceUtils.build(DruidXADataSource.class, H2, "ds1");
        SingleXADataSource shardingXADataSource = new SingleXADataSource(DatabaseType.H2, "ds1", dataSource);
        SingleXAConnection actual = shardingXADataSource.getXAConnection();
        Assert.assertThat(actual.getConnection(), CoreMatchers.instanceOf(Connection.class));
    }

    @Test
    @SneakyThrows
    public void assertGetXAConnectionOfNoneXA() {
        DataSource dataSource = DataSourceUtils.build(HikariDataSource.class, H2, "ds1");
        SingleXADataSource shardingXADataSource = new SingleXADataSource(DatabaseType.H2, "ds1", dataSource);
        SingleXAConnection actual = shardingXADataSource.getXAConnection();
        Assert.assertThat(actual.getConnection(), CoreMatchers.instanceOf(Connection.class));
    }

    @Test(expected = SQLFeatureNotSupportedException.class)
    public void assertGetLoginTimeout() throws SQLException {
        DataSource dataSource = DataSourceUtils.build(DruidXADataSource.class, H2, "ds1");
        SingleXADataSource shardingXADataSource = new SingleXADataSource(DatabaseType.H2, "ds1", dataSource);
        shardingXADataSource.getLoginTimeout();
    }

    @Test(expected = SQLFeatureNotSupportedException.class)
    public void assertSetLogWriter() throws SQLException {
        DataSource dataSource = DataSourceUtils.build(DruidXADataSource.class, H2, "ds1");
        SingleXADataSource shardingXADataSource = new SingleXADataSource(DatabaseType.H2, "ds1", dataSource);
        shardingXADataSource.setLogWriter(null);
    }

    @Test(expected = SQLFeatureNotSupportedException.class)
    public void assertSetLoginTimeout() throws SQLException {
        DataSource dataSource = DataSourceUtils.build(DruidXADataSource.class, H2, "ds1");
        SingleXADataSource shardingXADataSource = new SingleXADataSource(DatabaseType.H2, "ds1", dataSource);
        shardingXADataSource.setLoginTimeout(10);
    }

    @Test(expected = SQLFeatureNotSupportedException.class)
    public void assertGetParentLogger() throws SQLException {
        DataSource dataSource = DataSourceUtils.build(DruidXADataSource.class, H2, "ds1");
        SingleXADataSource shardingXADataSource = new SingleXADataSource(DatabaseType.H2, "ds1", dataSource);
        shardingXADataSource.getParentLogger();
    }

    @Test(expected = SQLFeatureNotSupportedException.class)
    public void assertGetLogWriter() throws SQLException {
        DataSource dataSource = DataSourceUtils.build(DruidXADataSource.class, H2, "ds1");
        SingleXADataSource shardingXADataSource = new SingleXADataSource(DatabaseType.H2, "ds1", dataSource);
        shardingXADataSource.getLogWriter();
    }

    @Test(expected = SQLFeatureNotSupportedException.class)
    public void assertGetXAConnectionByUserAndPassword() throws SQLException {
        DataSource dataSource = DataSourceUtils.build(DruidXADataSource.class, H2, "ds1");
        SingleXADataSource shardingXADataSource = new SingleXADataSource(DatabaseType.H2, "ds1", dataSource);
        shardingXADataSource.getXAConnection("root", "root");
    }
}

