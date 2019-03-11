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
import java.sql.SQLException;
import java.sql.Statement;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.apache.shardingsphere.core.exception.ShardingException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.orm.jpa.EntityManagerHolder;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.support.TransactionSynchronizationManager;


@RunWith(MockitoJUnitRunner.class)
public final class JpaTransactionManagerHandlerTest {
    @Mock
    private JpaTransactionManager transactionManager;

    @Mock
    private Statement statement;

    @Mock
    private EntityManagerFactory entityManagerFactory;

    private JpaTransactionManagerHandler jpaTransactionManagerHandler;

    @Test
    public void assertSwitchTransactionTypeSuccess() throws SQLException {
        jpaTransactionManagerHandler.switchTransactionType(XA);
        Mockito.verify(statement).execute(ArgumentMatchers.anyString());
        TransactionSynchronizationManager.unbindResourceIfPossible(entityManagerFactory);
    }

    @Test(expected = ShardingException.class)
    public void assertSwitchTransactionTypeFailExecute() throws SQLException {
        Mockito.when(statement.execute(ArgumentMatchers.anyString())).thenThrow(new SQLException("Mock send switch transaction type SQL failed"));
        try {
            jpaTransactionManagerHandler.switchTransactionType(XA);
        } finally {
            TransactionSynchronizationManager.unbindResourceIfPossible(entityManagerFactory);
        }
    }

    @Test
    public void assertUnbindResource() {
        EntityManagerHolder holder = Mockito.mock(EntityManagerHolder.class);
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        Mockito.when(holder.getEntityManager()).thenReturn(entityManager);
        TransactionSynchronizationManager.bindResource(entityManagerFactory, holder);
        jpaTransactionManagerHandler.unbindResource();
        Assert.assertNull(TransactionSynchronizationManager.getResource(entityManagerFactory));
    }
}

