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
import DatabaseType.PostgreSQL;
import DatabaseType.SQLServer;
import com.microsoft.sqlserver.jdbc.SQLServerXADataSource;
import javax.sql.XADataSource;
import org.h2.jdbcx.JdbcDataSource;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.postgresql.xa.PGXADataSource;


public final class XADataSourceFactoryTest {
    @Test
    public void assertCreateH2XADataSource() {
        XADataSource xaDataSource = XADataSourceFactory.build(H2);
        Assert.assertThat(xaDataSource, CoreMatchers.instanceOf(JdbcDataSource.class));
    }

    @Test
    public void assertCreatePGXADataSource() {
        XADataSource xaDataSource = XADataSourceFactory.build(PostgreSQL);
        Assert.assertThat(xaDataSource, CoreMatchers.instanceOf(PGXADataSource.class));
    }

    @Test
    public void assertCreateMSXADataSource() {
        XADataSource xaDataSource = XADataSourceFactory.build(SQLServer);
        Assert.assertThat(xaDataSource, CoreMatchers.instanceOf(SQLServerXADataSource.class));
    }
}

