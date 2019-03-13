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
package org.apache.shardingsphere.shardingjdbc.spring.boot.util;


import com.zaxxer.hikari.HikariDataSource;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.shardingsphere.shardingjdbc.orchestration.spring.boot.util.DataSourceUtil;
import org.h2.Driver;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public final class DataSourceUtilTest {
    @Test
    public void assertDataSourceForDBCPAndCamel() throws ReflectiveOperationException {
        BasicDataSource actual = ((BasicDataSource) (DataSourceUtil.getDataSource(BasicDataSource.class.getName(), getDataSourcePoolProperties("driverClassName", "url", "username"))));
        Assert.assertThat(actual.getDriverClassName(), CoreMatchers.is(Driver.class.getName()));
        Assert.assertThat(actual.getUrl(), CoreMatchers.is("jdbc:h2:mem:%s;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=false;MODE=MySQL"));
        Assert.assertThat(actual.getUsername(), CoreMatchers.is("sa"));
    }

    @Test
    public void assertDataSourceForDBCPAndHyphen() throws ReflectiveOperationException {
        BasicDataSource actual = ((BasicDataSource) (DataSourceUtil.getDataSource(BasicDataSource.class.getName(), getDataSourcePoolProperties("driver-class-name", "url", "username"))));
        Assert.assertThat(actual.getDriverClassName(), CoreMatchers.is(Driver.class.getName()));
        Assert.assertThat(actual.getUrl(), CoreMatchers.is("jdbc:h2:mem:%s;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=false;MODE=MySQL"));
        Assert.assertThat(actual.getUsername(), CoreMatchers.is("sa"));
    }

    @Test
    public void assertDataSourceForHikariCPAndCamel() throws ReflectiveOperationException {
        HikariDataSource actual = ((HikariDataSource) (DataSourceUtil.getDataSource(HikariDataSource.class.getName(), getDataSourcePoolProperties("driverClassName", "jdbcUrl", "username"))));
        Assert.assertThat(actual.getDriverClassName(), CoreMatchers.is(Driver.class.getName()));
        Assert.assertThat(actual.getJdbcUrl(), CoreMatchers.is("jdbc:h2:mem:%s;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=false;MODE=MySQL"));
        Assert.assertThat(actual.getUsername(), CoreMatchers.is("sa"));
    }

    @Test
    public void assertDataSourceForHikariCPAndHyphen() throws ReflectiveOperationException {
        HikariDataSource actual = ((HikariDataSource) (DataSourceUtil.getDataSource(HikariDataSource.class.getName(), getDataSourcePoolProperties("driver-class-name", "jdbc-url", "username"))));
        Assert.assertThat(actual.getDriverClassName(), CoreMatchers.is(Driver.class.getName()));
        Assert.assertThat(actual.getJdbcUrl(), CoreMatchers.is("jdbc:h2:mem:%s;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=false;MODE=MySQL"));
        Assert.assertThat(actual.getUsername(), CoreMatchers.is("sa"));
    }

    @Test
    public void assertDataSourceForBooleanValue() throws ReflectiveOperationException {
        Map<String, Object> dataSourceProperties = new HashMap<>(7, 1);
        dataSourceProperties.put("defaultAutoCommit", true);
        dataSourceProperties.put("defaultReadOnly", false);
        dataSourceProperties.put("poolPreparedStatements", Boolean.TRUE);
        dataSourceProperties.put("testOnBorrow", Boolean.FALSE);
        dataSourceProperties.put("testOnReturn", true);
        dataSourceProperties.put("testWhileIdle", false);
        dataSourceProperties.put("accessToUnderlyingConnectionAllowed", Boolean.TRUE);
        BasicDataSource actual = ((BasicDataSource) (DataSourceUtil.getDataSource(BasicDataSource.class.getName(), dataSourceProperties)));
        Assert.assertThat(actual.getDefaultAutoCommit(), CoreMatchers.is(true));
        Assert.assertThat(actual.getDefaultReadOnly(), CoreMatchers.is(false));
        Assert.assertThat(actual.isPoolPreparedStatements(), CoreMatchers.is(Boolean.TRUE));
        Assert.assertThat(actual.getTestOnBorrow(), CoreMatchers.is(Boolean.FALSE));
        Assert.assertThat(actual.getTestOnReturn(), CoreMatchers.is(Boolean.TRUE));
        Assert.assertThat(actual.getTestWhileIdle(), CoreMatchers.is(Boolean.FALSE));
        Assert.assertThat(actual.isAccessToUnderlyingConnectionAllowed(), CoreMatchers.is(true));
    }

    @Test
    public void assertDataSourceForIntValue() throws ReflectiveOperationException {
        Map<String, Object> dataSourceProperties = new HashMap<>(7, 1);
        dataSourceProperties.put("defaultTransactionIsolation", (-13));
        dataSourceProperties.put("maxTotal", 16);
        dataSourceProperties.put("maxIdle", 4);
        dataSourceProperties.put("minIdle", 16);
        dataSourceProperties.put("initialSize", 7);
        dataSourceProperties.put("maxOpenPreparedStatements", 128);
        dataSourceProperties.put("numTestsPerEvictionRun", 13);
        BasicDataSource actual = ((BasicDataSource) (DataSourceUtil.getDataSource(BasicDataSource.class.getName(), dataSourceProperties)));
        Assert.assertThat(actual.getDefaultTransactionIsolation(), CoreMatchers.is((-13)));
        Assert.assertThat(actual.getMaxTotal(), CoreMatchers.is(16));
        Assert.assertThat(actual.getMaxIdle(), CoreMatchers.is(4));
        Assert.assertThat(actual.getMinIdle(), CoreMatchers.is(16));
        Assert.assertThat(actual.getInitialSize(), CoreMatchers.is(7));
        Assert.assertThat(actual.getMaxOpenPreparedStatements(), CoreMatchers.is(128));
        Assert.assertThat(actual.getNumTestsPerEvictionRun(), CoreMatchers.is(13));
    }

    @Test
    public void assertDataSourceForLongValue() throws ReflectiveOperationException {
        Map<String, Object> dataSourceProperties = new HashMap<>(3, 1);
        dataSourceProperties.put("timeBetweenEvictionRunsMillis", 16L);
        dataSourceProperties.put("minEvictableIdleTimeMillis", 4000L);
        BasicDataSource actual = ((BasicDataSource) (DataSourceUtil.getDataSource(BasicDataSource.class.getName(), dataSourceProperties)));
        Assert.assertThat(actual.getTimeBetweenEvictionRunsMillis(), CoreMatchers.is(16L));
        Assert.assertThat(actual.getMinEvictableIdleTimeMillis(), CoreMatchers.is(4000L));
    }
}

