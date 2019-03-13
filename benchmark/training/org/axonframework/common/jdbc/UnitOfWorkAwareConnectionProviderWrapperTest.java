/**
 * Copyright (c) 2010-2018. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.axonframework.common.jdbc;


import java.sql.Connection;
import java.sql.SQLException;
import org.axonframework.messaging.Message;
import org.axonframework.messaging.unitofwork.DefaultUnitOfWork;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;


public class UnitOfWorkAwareConnectionProviderWrapperTest {
    private ConnectionProvider mockConnectionProvider;

    private Connection mockConnection;

    private UnitOfWorkAwareConnectionProviderWrapper testSubject;

    @Test
    public void testConnectionReturnedImmediatelyWhenNoActiveUnitOfWork() throws SQLException {
        Connection actual = testSubject.getConnection();
        Assert.assertSame(actual, mockConnection);
    }

    @Test
    public void testConnectionIsWrappedWhenUnitOfWorkIsActive() throws SQLException {
        DefaultUnitOfWork<Message<?>> uow = DefaultUnitOfWork.startAndGet(null);
        Connection actual = testSubject.getConnection();
        Assert.assertNotSame(actual, mockConnection);
        actual.close();
        Mockito.verify(mockConnection, Mockito.never()).close();
        uow.commit();
        Mockito.verify(mockConnection).close();
    }

    @Test
    public void testWrappedConnectionBlocksCommitCallsUntilUnitOfWorkCommit() throws SQLException {
        DefaultUnitOfWork<Message<?>> uow = DefaultUnitOfWork.startAndGet(null);
        Mockito.when(mockConnection.getAutoCommit()).thenReturn(false);
        Mockito.when(mockConnection.isClosed()).thenReturn(false);
        Connection actual = testSubject.getConnection();
        actual.commit();
        Mockito.verify(mockConnection, Mockito.never()).commit();
        Mockito.verify(mockConnection, Mockito.never()).close();
        uow.commit();
        InOrder inOrder = Mockito.inOrder(mockConnection);
        inOrder.verify(mockConnection).commit();
        inOrder.verify(mockConnection).close();
    }

    @Test
    public void testWrappedConnectionRollsBackCallsWhenUnitOfWorkRollback() throws SQLException {
        DefaultUnitOfWork<Message<?>> uow = DefaultUnitOfWork.startAndGet(null);
        Mockito.when(mockConnection.getAutoCommit()).thenReturn(false);
        Mockito.when(mockConnection.isClosed()).thenReturn(false);
        Connection actual = testSubject.getConnection();
        actual.close();
        actual.commit();
        Mockito.verify(mockConnection, Mockito.never()).commit();
        Mockito.verify(mockConnection, Mockito.never()).close();
        uow.rollback();
        InOrder inOrder = Mockito.inOrder(mockConnection);
        inOrder.verify(mockConnection).rollback();
        inOrder.verify(mockConnection).close();
    }

    @Test
    public void testInnerUnitOfWorkCommitDoesNotCloseConnection() throws SQLException {
        Mockito.when(mockConnection.getAutoCommit()).thenReturn(false);
        Mockito.when(mockConnection.isClosed()).thenReturn(false);
        DefaultUnitOfWork<Message<?>> uow = DefaultUnitOfWork.startAndGet(null);
        Connection actualOuter = testSubject.getConnection();
        Mockito.verify(mockConnectionProvider, Mockito.times(1)).getConnection();
        DefaultUnitOfWork<Message<?>> innerUow = DefaultUnitOfWork.startAndGet(null);
        Connection actualInner = testSubject.getConnection();
        Mockito.verify(mockConnectionProvider, Mockito.times(1)).getConnection();
        Assert.assertSame(actualOuter, actualInner);
        actualInner.close();
        actualInner.commit();
        Mockito.verify(mockConnection, Mockito.never()).commit();
        Mockito.verify(mockConnection, Mockito.never()).close();
        innerUow.commit();
        Mockito.verify(mockConnection, Mockito.never()).commit();
        Mockito.verify(mockConnection, Mockito.never()).close();
        uow.commit();
        Mockito.verify(mockConnection).commit();
        Mockito.verify(mockConnection).close();
    }
}

