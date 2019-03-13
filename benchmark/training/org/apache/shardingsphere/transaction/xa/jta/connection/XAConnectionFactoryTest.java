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
package org.apache.shardingsphere.transaction.xa.jta.connection;


import DatabaseType.H2;
import DatabaseType.MySQL;
import DatabaseType.Oracle;
import DatabaseType.PostgreSQL;
import java.sql.Connection;
import javax.sql.XADataSource;
import org.h2.jdbcx.JdbcXAConnection;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.postgresql.xa.PGXAConnection;


@RunWith(MockitoJUnitRunner.class)
public final class XAConnectionFactoryTest {
    @Mock
    private XADataSource xaDataSource;

    @Mock
    private Connection connection;

    // TODO assert fail
    @Test(expected = Exception.class)
    public void assertCreateMySQLXAConnection() {
        XAConnectionFactory.createXAConnection(MySQL, xaDataSource, connection);
    }

    @Test
    public void assertCreateH2XAConnection() {
        Assert.assertThat(XAConnectionFactory.createXAConnection(H2, xaDataSource, connection), CoreMatchers.instanceOf(JdbcXAConnection.class));
    }

    @Test
    public void assertCreatePostgreSQLXAConnection() {
        Assert.assertThat(XAConnectionFactory.createXAConnection(PostgreSQL, xaDataSource, connection), CoreMatchers.instanceOf(PGXAConnection.class));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void assertCreateUnknownXAConnectionThrowsUnsupportedOperationException() {
        XAConnectionFactory.createXAConnection(Oracle, xaDataSource, connection);
    }
}

