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
package org.apache.shardingsphere.transaction.handler;


import TransactionType.XA;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;
import org.apache.shardingsphere.core.exception.ShardingException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.jdbc.datasource.ConnectionHolder;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.support.TransactionSynchronizationManager;


@RunWith(MockitoJUnitRunner.class)
public final class DataSourceTransactionManagerHandlerTest {
    @Mock
    private DataSource dataSource;

    @Mock
    private DataSourceTransactionManager transactionManager;

    private DataSourceTransactionManagerHandler dataSourceTransactionManagerHandler;

    @Test
    public void assertSwitchTransactionTypeSuccess() throws SQLException {
        Connection connection = Mockito.mock(Connection.class);
        Statement statement = Mockito.mock(Statement.class);
        Mockito.when(dataSource.getConnection()).thenReturn(connection);
        Mockito.when(connection.createStatement()).thenReturn(statement);
        dataSourceTransactionManagerHandler.switchTransactionType(XA);
        Mockito.verify(statement).execute(ArgumentMatchers.anyString());
        TransactionSynchronizationManager.unbindResourceIfPossible(dataSource);
    }

    @Test(expected = ShardingException.class)
    public void assertSwitchTransactionTypeFailExecute() throws SQLException {
        Connection connection = Mockito.mock(Connection.class);
        Statement statement = Mockito.mock(Statement.class);
        Mockito.when(dataSource.getConnection()).thenReturn(connection);
        Mockito.when(connection.createStatement()).thenReturn(statement);
        Mockito.when(statement.execute(ArgumentMatchers.anyString())).thenThrow(new SQLException("Mock send switch transaction type SQL failed"));
        try {
            dataSourceTransactionManagerHandler.switchTransactionType(XA);
        } finally {
            TransactionSynchronizationManager.unbindResourceIfPossible(dataSource);
        }
    }

    @Test(expected = ShardingException.class)
    public void assertSwitchTransactionTypeFailGetConnection() throws SQLException {
        Mockito.when(dataSource.getConnection()).thenThrow(new SQLException("Mock get connection failed"));
        try {
            dataSourceTransactionManagerHandler.switchTransactionType(XA);
        } finally {
            TransactionSynchronizationManager.unbindResourceIfPossible(dataSource);
        }
    }

    @Test
    public void assertUnbindResource() {
        ConnectionHolder holder = Mockito.mock(ConnectionHolder.class);
        Connection connection = Mockito.mock(Connection.class);
        Mockito.when(holder.getConnection()).thenReturn(connection);
        TransactionSynchronizationManager.bindResource(dataSource, holder);
        dataSourceTransactionManagerHandler.unbindResource();
        Assert.assertNull(TransactionSynchronizationManager.getResource(dataSource));
    }
}

