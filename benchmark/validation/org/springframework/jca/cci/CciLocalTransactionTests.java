/**
 * Copyright 2002-2014 the original author or authors.
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
package org.springframework.jca.cci;


import javax.resource.ResourceException;
import javax.resource.cci.Connection;
import javax.resource.cci.ConnectionFactory;
import javax.resource.cci.Interaction;
import javax.resource.cci.InteractionSpec;
import javax.resource.cci.LocalTransaction;
import javax.resource.cci.Record;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jca.cci.connection.CciLocalTransactionManager;
import org.springframework.jca.cci.core.CciTemplate;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;


/**
 *
 *
 * @author Thierry Templier
 * @author Chris Beams
 */
public class CciLocalTransactionTests {
    /**
     * Test if a transaction ( begin / commit ) is executed on the
     * LocalTransaction when CciLocalTransactionManager is specified as
     * transaction manager.
     */
    @Test
    public void testLocalTransactionCommit() throws ResourceException {
        final ConnectionFactory connectionFactory = Mockito.mock(ConnectionFactory.class);
        Connection connection = Mockito.mock(Connection.class);
        Interaction interaction = Mockito.mock(Interaction.class);
        LocalTransaction localTransaction = Mockito.mock(LocalTransaction.class);
        final Record record = Mockito.mock(Record.class);
        final InteractionSpec interactionSpec = Mockito.mock(InteractionSpec.class);
        BDDMockito.given(connectionFactory.getConnection()).willReturn(connection);
        BDDMockito.given(connection.getLocalTransaction()).willReturn(localTransaction);
        BDDMockito.given(connection.createInteraction()).willReturn(interaction);
        BDDMockito.given(interaction.execute(interactionSpec, record, record)).willReturn(true);
        BDDMockito.given(connection.getLocalTransaction()).willReturn(localTransaction);
        CciLocalTransactionManager tm = new CciLocalTransactionManager();
        tm.setConnectionFactory(connectionFactory);
        TransactionTemplate tt = new TransactionTemplate(tm);
        tt.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                Assert.assertTrue("Has thread connection", TransactionSynchronizationManager.hasResource(connectionFactory));
                CciTemplate ct = new CciTemplate(connectionFactory);
                ct.execute(interactionSpec, record, record);
            }
        });
        Mockito.verify(localTransaction).begin();
        Mockito.verify(interaction).close();
        Mockito.verify(localTransaction).commit();
        Mockito.verify(connection).close();
    }

    /**
     * Test if a transaction ( begin / rollback ) is executed on the
     * LocalTransaction when CciLocalTransactionManager is specified as
     * transaction manager and a non-checked exception is thrown.
     */
    @Test
    public void testLocalTransactionRollback() throws ResourceException {
        final ConnectionFactory connectionFactory = Mockito.mock(ConnectionFactory.class);
        Connection connection = Mockito.mock(Connection.class);
        Interaction interaction = Mockito.mock(Interaction.class);
        LocalTransaction localTransaction = Mockito.mock(LocalTransaction.class);
        final Record record = Mockito.mock(Record.class);
        final InteractionSpec interactionSpec = Mockito.mock(InteractionSpec.class);
        BDDMockito.given(connectionFactory.getConnection()).willReturn(connection);
        BDDMockito.given(connection.getLocalTransaction()).willReturn(localTransaction);
        BDDMockito.given(connection.createInteraction()).willReturn(interaction);
        BDDMockito.given(interaction.execute(interactionSpec, record, record)).willReturn(true);
        BDDMockito.given(connection.getLocalTransaction()).willReturn(localTransaction);
        CciLocalTransactionManager tm = new CciLocalTransactionManager();
        tm.setConnectionFactory(connectionFactory);
        TransactionTemplate tt = new TransactionTemplate(tm);
        try {
            tt.execute(new org.springframework.transaction.support.TransactionCallback<Void>() {
                @Override
                public Void doInTransaction(TransactionStatus status) {
                    Assert.assertTrue("Has thread connection", TransactionSynchronizationManager.hasResource(connectionFactory));
                    CciTemplate ct = new CciTemplate(connectionFactory);
                    ct.execute(interactionSpec, record, record);
                    throw new DataRetrievalFailureException("error");
                }
            });
        } catch (Exception ex) {
        }
        Mockito.verify(localTransaction).begin();
        Mockito.verify(interaction).close();
        Mockito.verify(localTransaction).rollback();
        Mockito.verify(connection).close();
    }
}

