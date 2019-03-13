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
package org.apache.shardingsphere.core.config;


import com.zaxxer.hikari.HikariDataSource;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public final class DataSourceConfigurationTest {
    @Test
    public void assertGetDataSourceConfiguration() throws SQLException {
        HikariDataSource actualDataSource = new HikariDataSource();
        actualDataSource.setDriverClassName("org.h2.Driver");
        actualDataSource.setJdbcUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=false;MODE=MySQL");
        actualDataSource.setUsername("root");
        actualDataSource.setPassword("root");
        actualDataSource.setLoginTimeout(1);
        DataSourceConfiguration actual = DataSourceConfiguration.getDataSourceConfiguration(actualDataSource);
        Assert.assertThat(actual.getDataSourceClassName(), CoreMatchers.is(HikariDataSource.class.getName()));
        Assert.assertThat(actual.getProperties().get("driverClassName").toString(), CoreMatchers.is("org.h2.Driver"));
        Assert.assertThat(actual.getProperties().get("jdbcUrl").toString(), CoreMatchers.is("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=false;MODE=MySQL"));
        Assert.assertThat(actual.getProperties().get("username").toString(), CoreMatchers.is("root"));
        Assert.assertThat(actual.getProperties().get("password").toString(), CoreMatchers.is("root"));
        Assert.assertNull(actual.getProperties().get("loginTimeout"));
    }

    @Test
    public void assertCreateDataSource() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("driverClassName", "org.h2.Driver");
        properties.put("jdbcUrl", "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=false;MODE=MySQL");
        properties.put("username", "root");
        properties.put("password", "root");
        properties.put("loginTimeout", "5000");
        properties.put("test", "test");
        DataSourceConfiguration dataSourceConfig = new DataSourceConfiguration(HikariDataSource.class.getName());
        dataSourceConfig.getProperties().putAll(properties);
        HikariDataSource actual = ((HikariDataSource) (dataSourceConfig.createDataSource()));
        Assert.assertThat(actual.getDriverClassName(), CoreMatchers.is("org.h2.Driver"));
        Assert.assertThat(actual.getJdbcUrl(), CoreMatchers.is("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=false;MODE=MySQL"));
        Assert.assertThat(actual.getUsername(), CoreMatchers.is("root"));
        Assert.assertThat(actual.getPassword(), CoreMatchers.is("root"));
    }
}

