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
package org.springframework.boot.jdbc;


import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import org.apache.tomcat.jdbc.pool.DataSourceProxy;
import org.junit.Test;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;


/**
 * Tests for {@link DataSourceUnwrapper}.
 *
 * @author Stephane Nicoll
 */
public class DataSourceUnwrapperTests {
    @Test
    public void unwrapWithTarget() {
        DataSource dataSource = new HikariDataSource();
        assertThat(DataSourceUnwrapper.unwrap(dataSource, HikariDataSource.class)).isSameAs(dataSource);
    }

    @Test
    public void unwrapWithWrongTarget() {
        DataSource dataSource = new HikariDataSource();
        assertThat(DataSourceUnwrapper.unwrap(dataSource, SingleConnectionDataSource.class)).isNull();
    }

    @Test
    public void unwrapWithDelegate() {
        DataSource dataSource = new HikariDataSource();
        DataSource actual = wrapInDelegate(wrapInDelegate(dataSource));
        assertThat(DataSourceUnwrapper.unwrap(actual, HikariDataSource.class)).isSameAs(dataSource);
    }

    @Test
    public void unwrapWithProxy() {
        DataSource dataSource = new HikariDataSource();
        DataSource actual = wrapInProxy(wrapInProxy(dataSource));
        assertThat(DataSourceUnwrapper.unwrap(actual, HikariDataSource.class)).isSameAs(dataSource);
    }

    @Test
    public void unwrapWithProxyAndDelegate() {
        DataSource dataSource = new HikariDataSource();
        DataSource actual = wrapInProxy(wrapInDelegate(dataSource));
        assertThat(DataSourceUnwrapper.unwrap(actual, HikariDataSource.class)).isSameAs(dataSource);
    }

    @Test
    public void unwrapWithSeveralLevelOfWrapping() {
        DataSource dataSource = new HikariDataSource();
        DataSource actual = wrapInProxy(wrapInDelegate(wrapInDelegate(wrapInProxy(wrapInDelegate(dataSource)))));
        assertThat(DataSourceUnwrapper.unwrap(actual, HikariDataSource.class)).isSameAs(dataSource);
    }

    @Test
    public void unwrapDataSourceProxy() {
        org.apache.tomcat.jdbc.pool.DataSource dataSource = new org.apache.tomcat.jdbc.pool.DataSource();
        DataSource actual = wrapInDelegate(wrapInProxy(dataSource));
        assertThat(DataSourceUnwrapper.unwrap(actual, DataSourceProxy.class)).isSameAs(dataSource);
    }
}

