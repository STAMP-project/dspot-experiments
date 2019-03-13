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


import ConnectionStatus.TERMINATED;
import ConnectionStatus.TRANSACTION;
import TransactionType.LOCAL;
import TransactionType.XA;
import java.sql.SQLException;
import org.apache.shardingsphere.shardingproxy.backend.schema.LogicSchema;
import org.apache.shardingsphere.transaction.spi.ShardingTransactionManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public final class BackendTransactionManagerTest {
    @Mock
    private LogicSchema logicSchema;

    @Mock
    private BackendConnection backendConnection;

    @Mock
    private ConnectionStateHandler stateHandler;

    @Mock
    private LocalTransactionManager localTransactionManager;

    @Mock
    private ShardingTransactionManager shardingTransactionManager;

    private BackendTransactionManager backendTransactionManager;

    @Test
    public void assertBeginForLocalTransaction() {
        newBackendTransactionManager(LOCAL, false);
        backendTransactionManager.begin();
        Mockito.verify(stateHandler).setStatus(TRANSACTION);
        Mockito.verify(backendConnection).releaseConnections(false);
        Mockito.verify(localTransactionManager).begin();
    }

    @Test
    public void assertBeginForDistributedTransaction() {
        newBackendTransactionManager(XA, true);
        backendTransactionManager.begin();
        Mockito.verify(stateHandler, Mockito.times(0)).setStatus(TRANSACTION);
        Mockito.verify(backendConnection, Mockito.times(0)).releaseConnections(false);
        Mockito.verify(shardingTransactionManager).begin();
    }

    @Test
    public void assertCommitForLocalTransaction() throws SQLException {
        newBackendTransactionManager(LOCAL, true);
        backendTransactionManager.commit();
        Mockito.verify(stateHandler).setStatus(TERMINATED);
        Mockito.verify(localTransactionManager).commit();
    }

    @Test
    public void assertCommitForDistributedTransaction() throws SQLException {
        newBackendTransactionManager(XA, true);
        backendTransactionManager.commit();
        Mockito.verify(stateHandler).setStatus(TERMINATED);
        Mockito.verify(shardingTransactionManager).commit();
    }

    @Test
    public void assertCommitWithoutTransaction() throws SQLException {
        newBackendTransactionManager(LOCAL, false);
        backendTransactionManager.commit();
        Mockito.verify(stateHandler, Mockito.times(0)).setStatus(TERMINATED);
        Mockito.verify(localTransactionManager, Mockito.times(0)).commit();
        Mockito.verify(shardingTransactionManager, Mockito.times(0)).commit();
    }

    @Test
    public void assertRollbackForLocalTransaction() throws SQLException {
        newBackendTransactionManager(LOCAL, true);
        backendTransactionManager.rollback();
        Mockito.verify(stateHandler).setStatus(TERMINATED);
        Mockito.verify(localTransactionManager).rollback();
    }

    @Test
    public void assertRollbackForDistributedTransaction() throws SQLException {
        newBackendTransactionManager(XA, true);
        backendTransactionManager.rollback();
        Mockito.verify(stateHandler).setStatus(TERMINATED);
        Mockito.verify(shardingTransactionManager).rollback();
    }

    @Test
    public void assertRollbackWithoutTransaction() throws SQLException {
        newBackendTransactionManager(LOCAL, false);
        backendTransactionManager.rollback();
        Mockito.verify(stateHandler, Mockito.times(0)).setStatus(TERMINATED);
        Mockito.verify(localTransactionManager, Mockito.times(0)).rollback();
        Mockito.verify(shardingTransactionManager, Mockito.times(0)).rollback();
    }
}

