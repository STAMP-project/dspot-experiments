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
package org.apache.shardingsphere.transaction.xa.jta.connection.dialect;


import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.XAConnection;
import javax.sql.XADataSource;
import javax.transaction.xa.XAResource;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public final class MySQLXAConnectionWrapperTest {
    private XADataSource xaDataSource;

    @Mock
    private Connection connection;

    @Test
    public void assertCreateMySQLConnection() throws SQLException {
        XAConnection actual = new MySQLXAConnectionWrapper().wrap(xaDataSource, connection);
        Assert.assertThat(actual.getXAResource(), CoreMatchers.instanceOf(XAResource.class));
        Assert.assertThat(actual.getConnection(), CoreMatchers.instanceOf(Connection.class));
    }
}

