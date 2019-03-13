/**
 * Copyright 2002-2018 the original author or authors.
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


import TransactionDefinition.ISOLATION_REPEATABLE_READ;
import TransactionDefinition.PROPAGATION_MANDATORY;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.transaction.support.DefaultTransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import static TransactionDefinition.ISOLATION_REPEATABLE_READ;
import static TransactionDefinition.ISOLATION_SERIALIZABLE;
import static TransactionDefinition.PROPAGATION_MANDATORY;
import static TransactionDefinition.PROPAGATION_REQUIRED;
import static TransactionDefinition.PROPAGATION_SUPPORTS;


/**
 *
 *
 * @author Juergen Hoeller
 * @since 29.04.2003
 */
public class TransactionSupportTests {
    @Test
    public void noExistingTransaction() {
        PlatformTransactionManager tm = new TestTransactionManager(false, true);
        DefaultTransactionStatus status1 = ((DefaultTransactionStatus) (tm.getTransaction(new org.springframework.transaction.support.DefaultTransactionDefinition(PROPAGATION_SUPPORTS))));
        Assert.assertFalse("Must not have transaction", status1.hasTransaction());
        DefaultTransactionStatus status2 = ((DefaultTransactionStatus) (tm.getTransaction(new org.springframework.transaction.support.DefaultTransactionDefinition(PROPAGATION_REQUIRED))));
        Assert.assertTrue("Must have transaction", status2.hasTransaction());
        Assert.assertTrue("Must be new transaction", status2.isNewTransaction());
        try {
            tm.getTransaction(new org.springframework.transaction.support.DefaultTransactionDefinition(PROPAGATION_MANDATORY));
            Assert.fail("Should not have thrown NoTransactionException");
        } catch (IllegalTransactionStateException ex) {
            // expected
        }
    }

    @Test
    public void existingTransaction() {
        PlatformTransactionManager tm = new TestTransactionManager(true, true);
        DefaultTransactionStatus status1 = ((DefaultTransactionStatus) (tm.getTransaction(new org.springframework.transaction.support.DefaultTransactionDefinition(PROPAGATION_SUPPORTS))));
        Assert.assertTrue("Must have transaction", ((status1.getTransaction()) != null));
        Assert.assertTrue("Must not be new transaction", (!(status1.isNewTransaction())));
        DefaultTransactionStatus status2 = ((DefaultTransactionStatus) (tm.getTransaction(new org.springframework.transaction.support.DefaultTransactionDefinition(PROPAGATION_REQUIRED))));
        Assert.assertTrue("Must have transaction", ((status2.getTransaction()) != null));
        Assert.assertTrue("Must not be new transaction", (!(status2.isNewTransaction())));
        try {
            DefaultTransactionStatus status3 = ((DefaultTransactionStatus) (tm.getTransaction(new org.springframework.transaction.support.DefaultTransactionDefinition(PROPAGATION_MANDATORY))));
            Assert.assertTrue("Must have transaction", ((status3.getTransaction()) != null));
            Assert.assertTrue("Must not be new transaction", (!(status3.isNewTransaction())));
        } catch (NoTransactionException ex) {
            Assert.fail("Should not have thrown NoTransactionException");
        }
    }

    @Test
    public void commitWithoutExistingTransaction() {
        TestTransactionManager tm = new TestTransactionManager(false, true);
        TransactionStatus status = getTransaction(null);
        tm.commit(status);
        Assert.assertTrue("triggered begin", tm.begin);
        Assert.assertTrue("triggered commit", tm.commit);
        Assert.assertTrue("no rollback", (!(tm.rollback)));
        Assert.assertTrue("no rollbackOnly", (!(tm.rollbackOnly)));
    }

    @Test
    public void rollbackWithoutExistingTransaction() {
        TestTransactionManager tm = new TestTransactionManager(false, true);
        TransactionStatus status = getTransaction(null);
        tm.rollback(status);
        Assert.assertTrue("triggered begin", tm.begin);
        Assert.assertTrue("no commit", (!(tm.commit)));
        Assert.assertTrue("triggered rollback", tm.rollback);
        Assert.assertTrue("no rollbackOnly", (!(tm.rollbackOnly)));
    }

    @Test
    public void rollbackOnlyWithoutExistingTransaction() {
        TestTransactionManager tm = new TestTransactionManager(false, true);
        TransactionStatus status = getTransaction(null);
        status.setRollbackOnly();
        tm.commit(status);
        Assert.assertTrue("triggered begin", tm.begin);
        Assert.assertTrue("no commit", (!(tm.commit)));
        Assert.assertTrue("triggered rollback", tm.rollback);
        Assert.assertTrue("no rollbackOnly", (!(tm.rollbackOnly)));
    }

    @Test
    public void commitWithExistingTransaction() {
        TestTransactionManager tm = new TestTransactionManager(true, true);
        TransactionStatus status = getTransaction(null);
        tm.commit(status);
        Assert.assertTrue("no begin", (!(tm.begin)));
        Assert.assertTrue("no commit", (!(tm.commit)));
        Assert.assertTrue("no rollback", (!(tm.rollback)));
        Assert.assertTrue("no rollbackOnly", (!(tm.rollbackOnly)));
    }

    @Test
    public void rollbackWithExistingTransaction() {
        TestTransactionManager tm = new TestTransactionManager(true, true);
        TransactionStatus status = getTransaction(null);
        tm.rollback(status);
        Assert.assertTrue("no begin", (!(tm.begin)));
        Assert.assertTrue("no commit", (!(tm.commit)));
        Assert.assertTrue("no rollback", (!(tm.rollback)));
        Assert.assertTrue("triggered rollbackOnly", tm.rollbackOnly);
    }

    @Test
    public void rollbackOnlyWithExistingTransaction() {
        TestTransactionManager tm = new TestTransactionManager(true, true);
        TransactionStatus status = getTransaction(null);
        status.setRollbackOnly();
        tm.commit(status);
        Assert.assertTrue("no begin", (!(tm.begin)));
        Assert.assertTrue("no commit", (!(tm.commit)));
        Assert.assertTrue("no rollback", (!(tm.rollback)));
        Assert.assertTrue("triggered rollbackOnly", tm.rollbackOnly);
    }

    @Test
    public void transactionTemplate() {
        TestTransactionManager tm = new TestTransactionManager(false, true);
        TransactionTemplate template = new TransactionTemplate(tm);
        template.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
            }
        });
        Assert.assertTrue("triggered begin", tm.begin);
        Assert.assertTrue("triggered commit", tm.commit);
        Assert.assertTrue("no rollback", (!(tm.rollback)));
        Assert.assertTrue("no rollbackOnly", (!(tm.rollbackOnly)));
    }

    @Test
    public void transactionTemplateWithCallbackPreference() {
        MockCallbackPreferringTransactionManager ptm = new MockCallbackPreferringTransactionManager();
        TransactionTemplate template = new TransactionTemplate(ptm);
        template.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
            }
        });
        Assert.assertSame(template, ptm.getDefinition());
        Assert.assertFalse(ptm.getStatus().isRollbackOnly());
    }

    @Test
    public void transactionTemplateWithException() {
        TestTransactionManager tm = new TestTransactionManager(false, true);
        TransactionTemplate template = new TransactionTemplate(tm);
        final RuntimeException ex = new RuntimeException("Some application exception");
        try {
            template.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus status) {
                    throw ex;
                }
            });
            Assert.fail("Should have propagated RuntimeException");
        } catch (RuntimeException caught) {
            // expected
            Assert.assertTrue("Correct exception", (caught == ex));
            Assert.assertTrue("triggered begin", tm.begin);
            Assert.assertTrue("no commit", (!(tm.commit)));
            Assert.assertTrue("triggered rollback", tm.rollback);
            Assert.assertTrue("no rollbackOnly", (!(tm.rollbackOnly)));
        }
    }

    @SuppressWarnings("serial")
    @Test
    public void transactionTemplateWithRollbackException() {
        final TransactionSystemException tex = new TransactionSystemException("system exception");
        TestTransactionManager tm = new TestTransactionManager(false, true) {
            @Override
            protected void doRollback(DefaultTransactionStatus status) {
                super.doRollback(status);
                throw tex;
            }
        };
        TransactionTemplate template = new TransactionTemplate(tm);
        final RuntimeException ex = new RuntimeException("Some application exception");
        try {
            template.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus status) {
                    throw ex;
                }
            });
            Assert.fail("Should have propagated RuntimeException");
        } catch (RuntimeException caught) {
            // expected
            Assert.assertTrue("Correct exception", (caught == tex));
            Assert.assertTrue("triggered begin", tm.begin);
            Assert.assertTrue("no commit", (!(tm.commit)));
            Assert.assertTrue("triggered rollback", tm.rollback);
            Assert.assertTrue("no rollbackOnly", (!(tm.rollbackOnly)));
        }
    }

    @Test
    public void transactionTemplateWithError() {
        TestTransactionManager tm = new TestTransactionManager(false, true);
        TransactionTemplate template = new TransactionTemplate(tm);
        try {
            template.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus status) {
                    throw new Error("Some application error");
                }
            });
            Assert.fail("Should have propagated Error");
        } catch (Error err) {
            // expected
            Assert.assertTrue("triggered begin", tm.begin);
            Assert.assertTrue("no commit", (!(tm.commit)));
            Assert.assertTrue("triggered rollback", tm.rollback);
            Assert.assertTrue("no rollbackOnly", (!(tm.rollbackOnly)));
        }
    }

    @Test
    public void transactionTemplateInitialization() {
        TestTransactionManager tm = new TestTransactionManager(false, true);
        TransactionTemplate template = new TransactionTemplate();
        template.setTransactionManager(tm);
        Assert.assertTrue("correct transaction manager set", ((template.getTransactionManager()) == tm));
        try {
            template.setPropagationBehaviorName("TIMEOUT_DEFAULT");
            Assert.fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // expected
        }
        template.setPropagationBehaviorName("PROPAGATION_SUPPORTS");
        Assert.assertTrue("Correct propagation behavior set", ((template.getPropagationBehavior()) == (PROPAGATION_SUPPORTS)));
        try {
            template.setPropagationBehavior(999);
            Assert.fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // expected
        }
        template.setPropagationBehavior(PROPAGATION_MANDATORY);
        Assert.assertTrue("Correct propagation behavior set", ((template.getPropagationBehavior()) == (PROPAGATION_MANDATORY)));
        try {
            template.setIsolationLevelName("TIMEOUT_DEFAULT");
            Assert.fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // expected
        }
        template.setIsolationLevelName("ISOLATION_SERIALIZABLE");
        Assert.assertTrue("Correct isolation level set", ((template.getIsolationLevel()) == (ISOLATION_SERIALIZABLE)));
        try {
            template.setIsolationLevel(999);
            Assert.fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // expected
        }
        template.setIsolationLevel(ISOLATION_REPEATABLE_READ);
        Assert.assertTrue("Correct isolation level set", ((template.getIsolationLevel()) == (ISOLATION_REPEATABLE_READ)));
    }

    @Test
    public void transactionTemplateEquality() {
        TestTransactionManager tm1 = new TestTransactionManager(false, true);
        TestTransactionManager tm2 = new TestTransactionManager(false, true);
        TransactionTemplate template1 = new TransactionTemplate(tm1);
        TransactionTemplate template2 = new TransactionTemplate(tm2);
        TransactionTemplate template3 = new TransactionTemplate(tm2);
        Assert.assertNotEquals(template1, template2);
        Assert.assertNotEquals(template1, template3);
        Assert.assertEquals(template2, template3);
    }
}

