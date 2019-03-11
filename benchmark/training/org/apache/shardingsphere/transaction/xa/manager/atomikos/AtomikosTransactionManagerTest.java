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
package org.apache.shardingsphere.transaction.xa.manager.atomikos;


import com.atomikos.icatch.config.UserTransactionService;
import com.atomikos.icatch.jta.UserTransactionManager;
import javax.sql.XADataSource;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import org.apache.shardingsphere.transaction.xa.spi.SingleXAResource;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public final class AtomikosTransactionManagerTest {
    private AtomikosTransactionManager atomikosTransactionManager = new AtomikosTransactionManager();

    @Mock
    private UserTransactionManager userTransactionManager;

    @Mock
    private UserTransactionService userTransactionService;

    @Mock
    private XADataSource xaDataSource;

    @Test
    public void assertInit() {
        atomikosTransactionManager.init();
        Mockito.verify(userTransactionService).init();
    }

    @Test
    public void assertRegisterRecoveryResource() {
        atomikosTransactionManager.registerRecoveryResource("ds1", xaDataSource);
        Mockito.verify(userTransactionService).registerResource(ArgumentMatchers.any(AtomikosXARecoverableResource.class));
    }

    @Test
    public void assertRemoveRecoveryResource() {
        atomikosTransactionManager.removeRecoveryResource("ds1", xaDataSource);
        Mockito.verify(userTransactionService).removeResource(ArgumentMatchers.any(AtomikosXARecoverableResource.class));
    }

    @Test
    public void assertEnListResource() throws Exception {
        SingleXAResource singleXAResource = Mockito.mock(SingleXAResource.class);
        Transaction transaction = Mockito.mock(Transaction.class);
        Mockito.when(userTransactionManager.getTransaction()).thenReturn(transaction);
        atomikosTransactionManager.enlistResource(singleXAResource);
        Mockito.verify(transaction).enlistResource(singleXAResource);
    }

    @Test
    public void assertTransactionManager() {
        Assert.assertThat(atomikosTransactionManager.getTransactionManager(), CoreMatchers.<TransactionManager>is(userTransactionManager));
    }

    @Test
    public void assertClose() {
        atomikosTransactionManager.close();
        Mockito.verify(userTransactionService).shutdown(true);
    }
}

