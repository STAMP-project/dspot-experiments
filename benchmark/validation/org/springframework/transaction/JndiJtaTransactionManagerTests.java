/**
 * Copyright 2002-2015 the original author or authors.
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
package org.springframework.transaction;


import JtaTransactionManager.SYNCHRONIZATION_ALWAYS;
import Status.STATUS_ACTIVE;
import Status.STATUS_NO_TRANSACTION;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.tests.mock.jndi.ExpectedLookupTemplate;
import org.springframework.transaction.jta.JtaTransactionManager;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;


/**
 *
 *
 * @author Juergen Hoeller
 * @since 05.08.2005
 */
public class JndiJtaTransactionManagerTests {
    @Test
    public void jtaTransactionManagerWithDefaultJndiLookups1() throws Exception {
        doTestJtaTransactionManagerWithDefaultJndiLookups("java:comp/TransactionManager", true, true);
    }

    @Test
    public void jtaTransactionManagerWithDefaultJndiLookups2() throws Exception {
        doTestJtaTransactionManagerWithDefaultJndiLookups("java:/TransactionManager", true, true);
    }

    @Test
    public void jtaTransactionManagerWithDefaultJndiLookupsAndNoTmFound() throws Exception {
        doTestJtaTransactionManagerWithDefaultJndiLookups("java:/tm", false, true);
    }

    @Test
    public void jtaTransactionManagerWithDefaultJndiLookupsAndNoUtFound() throws Exception {
        doTestJtaTransactionManagerWithDefaultJndiLookups("java:/TransactionManager", true, false);
    }

    @Test
    public void jtaTransactionManagerWithCustomJndiLookups() throws Exception {
        UserTransaction ut = Mockito.mock(UserTransaction.class);
        BDDMockito.given(ut.getStatus()).willReturn(STATUS_NO_TRANSACTION, STATUS_ACTIVE, STATUS_ACTIVE);
        TransactionManager tm = Mockito.mock(TransactionManager.class);
        JtaTransactionManager ptm = new JtaTransactionManager();
        ptm.setUserTransactionName("jndi-ut");
        ptm.setTransactionManagerName("jndi-tm");
        ExpectedLookupTemplate jndiTemplate = new ExpectedLookupTemplate();
        jndiTemplate.addObject("jndi-ut", ut);
        jndiTemplate.addObject("jndi-tm", tm);
        ptm.setJndiTemplate(jndiTemplate);
        ptm.afterPropertiesSet();
        Assert.assertEquals(ut, ptm.getUserTransaction());
        Assert.assertEquals(tm, ptm.getTransactionManager());
        TransactionTemplate tt = new TransactionTemplate(ptm);
        Assert.assertTrue((!(TransactionSynchronizationManager.isSynchronizationActive())));
        Assert.assertFalse(TransactionSynchronizationManager.isCurrentTransactionReadOnly());
        tt.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                // something transactional
                Assert.assertTrue(TransactionSynchronizationManager.isSynchronizationActive());
                Assert.assertFalse(TransactionSynchronizationManager.isCurrentTransactionReadOnly());
            }
        });
        Assert.assertTrue((!(TransactionSynchronizationManager.isSynchronizationActive())));
        Assert.assertFalse(TransactionSynchronizationManager.isCurrentTransactionReadOnly());
        Mockito.verify(ut).begin();
        Mockito.verify(ut).commit();
    }

    @Test
    public void jtaTransactionManagerWithNotCacheUserTransaction() throws Exception {
        UserTransaction ut = Mockito.mock(UserTransaction.class);
        BDDMockito.given(ut.getStatus()).willReturn(STATUS_NO_TRANSACTION, STATUS_ACTIVE, STATUS_ACTIVE);
        UserTransaction ut2 = Mockito.mock(UserTransaction.class);
        BDDMockito.given(ut2.getStatus()).willReturn(STATUS_NO_TRANSACTION, STATUS_ACTIVE, STATUS_ACTIVE);
        JtaTransactionManager ptm = new JtaTransactionManager();
        ptm.setJndiTemplate(new ExpectedLookupTemplate("java:comp/UserTransaction", ut));
        ptm.setCacheUserTransaction(false);
        ptm.afterPropertiesSet();
        Assert.assertEquals(ut, ptm.getUserTransaction());
        TransactionTemplate tt = new TransactionTemplate(ptm);
        Assert.assertEquals(SYNCHRONIZATION_ALWAYS, ptm.getTransactionSynchronization());
        Assert.assertTrue((!(TransactionSynchronizationManager.isSynchronizationActive())));
        Assert.assertFalse(TransactionSynchronizationManager.isCurrentTransactionReadOnly());
        tt.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                // something transactional
                Assert.assertTrue(TransactionSynchronizationManager.isSynchronizationActive());
                Assert.assertFalse(TransactionSynchronizationManager.isCurrentTransactionReadOnly());
            }
        });
        ptm.setJndiTemplate(new ExpectedLookupTemplate("java:comp/UserTransaction", ut2));
        tt.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                // something transactional
                Assert.assertTrue(TransactionSynchronizationManager.isSynchronizationActive());
                Assert.assertFalse(TransactionSynchronizationManager.isCurrentTransactionReadOnly());
            }
        });
        Assert.assertTrue((!(TransactionSynchronizationManager.isSynchronizationActive())));
        Assert.assertFalse(TransactionSynchronizationManager.isCurrentTransactionReadOnly());
        Mockito.verify(ut).begin();
        Mockito.verify(ut).commit();
        Mockito.verify(ut2).begin();
        Mockito.verify(ut2).commit();
    }
}

