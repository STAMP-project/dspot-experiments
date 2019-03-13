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
package org.apache.shardingsphere.shardingproxy.backend.communication.jdbc.connection;


import ConnectionMode.MEMORY_STRICTLY;
import ConnectionStatus.INIT;
import ConnectionStatus.RELEASE;
import ConnectionStatus.TERMINATED;
import ConnectionStatus.TRANSACTION;
import TransactionType.LOCAL;
import TransactionType.XA;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import lombok.SneakyThrows;
import org.apache.shardingsphere.core.constant.ConnectionMode;
import org.apache.shardingsphere.core.exception.ShardingException;
import org.apache.shardingsphere.shardingproxy.backend.communication.jdbc.datasource.JDBCBackendDataSource;
import org.apache.shardingsphere.transaction.core.TransactionType;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public final class BackendConnectionTest {
    @Mock
    private JDBCBackendDataSource backendDataSource;

    private BackendConnection backendConnection = new BackendConnection(TransactionType.LOCAL);

    @Test
    public void assertGetConnectionCacheIsEmpty() throws SQLException {
        backendConnection.getStateHandler().setStatus(TRANSACTION);
        Mockito.when(backendDataSource.getConnections(((ConnectionMode) (ArgumentMatchers.any())), ArgumentMatchers.anyString(), ArgumentMatchers.eq(2), ArgumentMatchers.eq(LOCAL))).thenReturn(MockConnectionUtil.mockNewConnections(2));
        List<Connection> actualConnections = backendConnection.getConnections(MEMORY_STRICTLY, "ds1", 2);
        Assert.assertThat(actualConnections.size(), CoreMatchers.is(2));
        Assert.assertThat(backendConnection.getConnectionSize(), CoreMatchers.is(2));
        Assert.assertThat(backendConnection.getStateHandler().getStatus(), CoreMatchers.is(TRANSACTION));
    }

    @Test
    public void assertGetConnectionSizeLessThanCache() throws SQLException {
        backendConnection.getStateHandler().setStatus(TRANSACTION);
        MockConnectionUtil.setCachedConnections(backendConnection, "ds1", 10);
        List<Connection> actualConnections = backendConnection.getConnections(MEMORY_STRICTLY, "ds1", 2);
        Assert.assertThat(actualConnections.size(), CoreMatchers.is(2));
        Assert.assertThat(backendConnection.getConnectionSize(), CoreMatchers.is(10));
        Assert.assertThat(backendConnection.getStateHandler().getStatus(), CoreMatchers.is(TRANSACTION));
    }

    @Test
    public void assertGetConnectionSizeGreaterThanCache() throws SQLException {
        backendConnection.getStateHandler().setStatus(TRANSACTION);
        MockConnectionUtil.setCachedConnections(backendConnection, "ds1", 10);
        Mockito.when(backendDataSource.getConnections(((ConnectionMode) (ArgumentMatchers.any())), ArgumentMatchers.anyString(), ArgumentMatchers.eq(2), ArgumentMatchers.eq(LOCAL))).thenReturn(MockConnectionUtil.mockNewConnections(2));
        List<Connection> actualConnections = backendConnection.getConnections(MEMORY_STRICTLY, "ds1", 12);
        Assert.assertThat(actualConnections.size(), CoreMatchers.is(12));
        Assert.assertThat(backendConnection.getConnectionSize(), CoreMatchers.is(12));
        Assert.assertThat(backendConnection.getStateHandler().getStatus(), CoreMatchers.is(TRANSACTION));
    }

    @Test
    public void assertGetConnectionWithMethodInvocation() throws SQLException {
        backendConnection.getStateHandler().setStatus(TRANSACTION);
        Mockito.when(backendDataSource.getConnections(((ConnectionMode) (ArgumentMatchers.any())), ArgumentMatchers.anyString(), ArgumentMatchers.eq(2), ArgumentMatchers.eq(LOCAL))).thenReturn(MockConnectionUtil.mockNewConnections(2));
        setMethodInvocation();
        List<Connection> actualConnections = backendConnection.getConnections(MEMORY_STRICTLY, "ds1", 2);
        Mockito.verify(backendConnection.getMethodInvocations().iterator().next(), Mockito.times(2)).invoke(ArgumentMatchers.any());
        Assert.assertThat(actualConnections.size(), CoreMatchers.is(2));
        Assert.assertThat(backendConnection.getStateHandler().getStatus(), CoreMatchers.is(TRANSACTION));
    }

    @Test
    @SneakyThrows
    public void assertMultiThreadGetConnection() {
        MockConnectionUtil.setCachedConnections(backendConnection, "ds1", 10);
        Mockito.when(backendDataSource.getConnections(((ConnectionMode) (ArgumentMatchers.any())), ArgumentMatchers.anyString(), ArgumentMatchers.eq(2), ArgumentMatchers.eq(LOCAL))).thenReturn(MockConnectionUtil.mockNewConnections(2));
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                assertOneThreadResult();
            }
        });
        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                assertOneThreadResult();
            }
        });
        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();
    }

    @Test
    public void assertAutoCloseConnectionWithoutTransaction() throws SQLException {
        BackendConnection actual;
        try (BackendConnection backendConnection = new BackendConnection(TransactionType.LOCAL)) {
            backendConnection.setCurrentSchema("schema_0");
            Mockito.when(backendDataSource.getConnections(((ConnectionMode) (ArgumentMatchers.any())), ArgumentMatchers.anyString(), ArgumentMatchers.eq(12), ArgumentMatchers.eq(LOCAL))).thenReturn(MockConnectionUtil.mockNewConnections(12));
            backendConnection.getConnections(MEMORY_STRICTLY, "ds1", 12);
            Assert.assertThat(backendConnection.getStateHandler().getStatus(), CoreMatchers.is(INIT));
            backendConnection.getStateHandler().setRunningStatusIfNecessary();
            mockResultSetAndStatement(backendConnection);
            actual = backendConnection;
        }
        Assert.assertThat(actual.getConnectionSize(), CoreMatchers.is(0));
        Assert.assertTrue(actual.getCachedConnections().isEmpty());
        Assert.assertTrue(actual.getCachedResultSets().isEmpty());
        Assert.assertTrue(actual.getCachedStatements().isEmpty());
        Assert.assertThat(actual.getStateHandler().getStatus(), CoreMatchers.is(RELEASE));
    }

    @Test
    public void assertAutoCloseConnectionWithTransaction() throws SQLException {
        BackendConnection actual;
        try (BackendConnection backendConnection = new BackendConnection(TransactionType.LOCAL)) {
            backendConnection.setCurrentSchema("schema_0");
            MockConnectionUtil.setCachedConnections(backendConnection, "ds1", 10);
            Mockito.when(backendDataSource.getConnections(((ConnectionMode) (ArgumentMatchers.any())), ArgumentMatchers.anyString(), ArgumentMatchers.eq(2), ArgumentMatchers.eq(LOCAL))).thenReturn(MockConnectionUtil.mockNewConnections(2));
            backendConnection.getStateHandler().setStatus(TRANSACTION);
            backendConnection.getConnections(MEMORY_STRICTLY, "ds1", 12);
            mockResultSetAndStatement(backendConnection);
            actual = backendConnection;
        }
        Assert.assertThat(actual.getConnectionSize(), CoreMatchers.is(12));
        Assert.assertThat(actual.getCachedConnections().get("ds1").size(), CoreMatchers.is(12));
        Assert.assertTrue(actual.getCachedResultSets().isEmpty());
        Assert.assertTrue(actual.getCachedStatements().isEmpty());
    }

    @Test
    public void assertAutoCloseConnectionWithException() {
        BackendConnection actual = null;
        try (BackendConnection backendConnection = new BackendConnection(TransactionType.LOCAL)) {
            backendConnection.setCurrentSchema("schema_0");
            backendConnection.setTransactionType(XA);
            backendConnection.getStateHandler().setStatus(TRANSACTION);
            MockConnectionUtil.setCachedConnections(backendConnection, "ds1", 10);
            Mockito.when(backendDataSource.getConnections(((ConnectionMode) (ArgumentMatchers.any())), ArgumentMatchers.anyString(), ArgumentMatchers.eq(2), ArgumentMatchers.eq(XA))).thenReturn(MockConnectionUtil.mockNewConnections(2));
            backendConnection.getConnections(MEMORY_STRICTLY, "ds1", 12);
            backendConnection.getStateHandler().setStatus(TERMINATED);
            mockResultSetAndStatement(backendConnection);
            mockResultSetAndStatementException(backendConnection);
            actual = backendConnection;
        } catch (final SQLException ex) {
            Assert.assertThat(ex.getNextException().getNextException(), CoreMatchers.instanceOf(SQLException.class));
        }
        assert actual != null;
        Assert.assertThat(actual.getConnectionSize(), CoreMatchers.is(0));
        Assert.assertTrue(actual.getCachedConnections().isEmpty());
        Assert.assertTrue(actual.getCachedResultSets().isEmpty());
        Assert.assertTrue(actual.getCachedStatements().isEmpty());
    }

    @Test(expected = ShardingException.class)
    public void assertFailedSwitchTransactionTypeWhileBegin() {
        BackendTransactionManager transactionManager = new BackendTransactionManager(backendConnection);
        transactionManager.begin();
        backendConnection.setTransactionType(XA);
    }

    @Test(expected = ShardingException.class)
    public void assertFailedSwitchLogicSchemaWhileBegin() {
        BackendTransactionManager transactionManager = new BackendTransactionManager(backendConnection);
        transactionManager.begin();
        backendConnection.setCurrentSchema("newSchema");
    }
}

