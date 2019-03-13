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
package org.springframework.orm.jpa;


import TransactionDefinition.ISOLATION_SERIALIZABLE;
import TransactionDefinition.PROPAGATION_REQUIRES_NEW;
import TransactionDefinition.PROPAGATION_SUPPORTS;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.RollbackException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.transaction.InvalidIsolationLevelException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;


/**
 *
 *
 * @author Costin Leau
 * @author Juergen Hoeller
 * @author Phillip Webb
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class JpaTransactionManagerTests {
    private EntityManagerFactory factory;

    private EntityManager manager;

    private EntityTransaction tx;

    private JpaTransactionManager tm;

    private TransactionTemplate tt;

    @Test
    public void testTransactionCommit() {
        BDDMockito.given(manager.getTransaction()).willReturn(tx);
        final List<String> l = new ArrayList<>();
        l.add("test");
        Assert.assertTrue((!(TransactionSynchronizationManager.hasResource(factory))));
        Assert.assertTrue((!(TransactionSynchronizationManager.isSynchronizationActive())));
        Object result = tt.execute(new TransactionCallback() {
            @Override
            public Object doInTransaction(TransactionStatus status) {
                Assert.assertTrue(TransactionSynchronizationManager.hasResource(factory));
                EntityManagerFactoryUtils.getTransactionalEntityManager(factory).flush();
                return l;
            }
        });
        Assert.assertSame(l, result);
        Assert.assertTrue((!(TransactionSynchronizationManager.hasResource(factory))));
        Assert.assertTrue((!(TransactionSynchronizationManager.isSynchronizationActive())));
        Mockito.verify(tx).commit();
        Mockito.verify(manager).flush();
        Mockito.verify(manager).close();
    }

    @Test
    public void testTransactionCommitWithRollbackException() {
        BDDMockito.given(manager.getTransaction()).willReturn(tx);
        BDDMockito.given(tx.getRollbackOnly()).willReturn(true);
        BDDMockito.willThrow(new RollbackException()).given(tx).commit();
        final List<String> l = new ArrayList<>();
        l.add("test");
        Assert.assertTrue((!(TransactionSynchronizationManager.hasResource(factory))));
        Assert.assertTrue((!(TransactionSynchronizationManager.isSynchronizationActive())));
        try {
            Object result = tt.execute(new TransactionCallback() {
                @Override
                public Object doInTransaction(TransactionStatus status) {
                    Assert.assertTrue(TransactionSynchronizationManager.hasResource(factory));
                    EntityManagerFactoryUtils.getTransactionalEntityManager(factory).flush();
                    return l;
                }
            });
            Assert.assertSame(l, result);
        } catch (TransactionSystemException tse) {
            // expected
            Assert.assertTrue(((tse.getCause()) instanceof RollbackException));
        }
        Assert.assertTrue((!(TransactionSynchronizationManager.hasResource(factory))));
        Assert.assertTrue((!(TransactionSynchronizationManager.isSynchronizationActive())));
        Mockito.verify(manager).flush();
        Mockito.verify(manager).close();
    }

    @Test
    public void testTransactionRollback() {
        BDDMockito.given(manager.getTransaction()).willReturn(tx);
        BDDMockito.given(tx.isActive()).willReturn(true);
        final List<String> l = new ArrayList<>();
        l.add("test");
        Assert.assertTrue((!(TransactionSynchronizationManager.hasResource(factory))));
        Assert.assertTrue((!(TransactionSynchronizationManager.isSynchronizationActive())));
        try {
            tt.execute(new TransactionCallback() {
                @Override
                public Object doInTransaction(TransactionStatus status) {
                    Assert.assertTrue(TransactionSynchronizationManager.hasResource(factory));
                    EntityManagerFactoryUtils.getTransactionalEntityManager(factory);
                    throw new RuntimeException("some exception");
                }
            });
            Assert.fail("Should have propagated RuntimeException");
        } catch (RuntimeException ex) {
            // expected
            Assert.assertEquals("some exception", ex.getMessage());
        }
        Assert.assertTrue((!(TransactionSynchronizationManager.hasResource(factory))));
        Assert.assertTrue((!(TransactionSynchronizationManager.isSynchronizationActive())));
        Mockito.verify(tx).rollback();
        Mockito.verify(manager).close();
    }

    @Test
    public void testTransactionRollbackWithAlreadyRolledBack() {
        BDDMockito.given(manager.getTransaction()).willReturn(tx);
        final List<String> l = new ArrayList<>();
        l.add("test");
        Assert.assertTrue((!(TransactionSynchronizationManager.hasResource(factory))));
        Assert.assertTrue((!(TransactionSynchronizationManager.isSynchronizationActive())));
        try {
            tt.execute(new TransactionCallback() {
                @Override
                public Object doInTransaction(TransactionStatus status) {
                    Assert.assertTrue(TransactionSynchronizationManager.hasResource(factory));
                    EntityManagerFactoryUtils.getTransactionalEntityManager(factory);
                    throw new RuntimeException("some exception");
                }
            });
            Assert.fail("Should have propagated RuntimeException");
        } catch (RuntimeException ex) {
            // expected
        }
        Assert.assertTrue((!(TransactionSynchronizationManager.hasResource(factory))));
        Assert.assertTrue((!(TransactionSynchronizationManager.isSynchronizationActive())));
        Mockito.verify(manager).close();
    }

    @Test
    public void testTransactionRollbackOnly() {
        BDDMockito.given(manager.getTransaction()).willReturn(tx);
        BDDMockito.given(tx.isActive()).willReturn(true);
        final List<String> l = new ArrayList<>();
        l.add("test");
        Assert.assertTrue((!(TransactionSynchronizationManager.hasResource(factory))));
        Assert.assertTrue((!(TransactionSynchronizationManager.isSynchronizationActive())));
        tt.execute(new TransactionCallback() {
            @Override
            public Object doInTransaction(TransactionStatus status) {
                Assert.assertTrue(TransactionSynchronizationManager.hasResource(factory));
                EntityManagerFactoryUtils.getTransactionalEntityManager(factory).flush();
                status.setRollbackOnly();
                return l;
            }
        });
        Assert.assertTrue((!(TransactionSynchronizationManager.hasResource(factory))));
        Assert.assertTrue((!(TransactionSynchronizationManager.isSynchronizationActive())));
        Mockito.verify(manager).flush();
        Mockito.verify(tx).rollback();
        Mockito.verify(manager).close();
    }

    @Test
    public void testParticipatingTransactionWithCommit() {
        BDDMockito.given(manager.getTransaction()).willReturn(tx);
        final List<String> l = new ArrayList<>();
        l.add("test");
        Assert.assertTrue((!(TransactionSynchronizationManager.hasResource(factory))));
        Assert.assertTrue((!(TransactionSynchronizationManager.isSynchronizationActive())));
        tt.execute(new TransactionCallback() {
            @Override
            public Object doInTransaction(TransactionStatus status) {
                Assert.assertTrue(TransactionSynchronizationManager.hasResource(factory));
                return tt.execute(new TransactionCallback() {
                    @Override
                    public Object doInTransaction(TransactionStatus status) {
                        EntityManagerFactoryUtils.getTransactionalEntityManager(factory).flush();
                        return l;
                    }
                });
            }
        });
        Assert.assertTrue((!(TransactionSynchronizationManager.hasResource(factory))));
        Assert.assertTrue((!(TransactionSynchronizationManager.isSynchronizationActive())));
        Mockito.verify(manager).flush();
        Mockito.verify(tx).commit();
        Mockito.verify(manager).close();
    }

    @Test
    public void testParticipatingTransactionWithRollback() {
        BDDMockito.given(manager.getTransaction()).willReturn(tx);
        BDDMockito.given(tx.isActive()).willReturn(true);
        final List<String> l = new ArrayList<>();
        l.add("test");
        Assert.assertTrue((!(TransactionSynchronizationManager.hasResource(factory))));
        Assert.assertTrue((!(TransactionSynchronizationManager.isSynchronizationActive())));
        try {
            tt.execute(new TransactionCallback() {
                @Override
                public Object doInTransaction(TransactionStatus status) {
                    Assert.assertTrue(TransactionSynchronizationManager.hasResource(factory));
                    return tt.execute(new TransactionCallback() {
                        @Override
                        public Object doInTransaction(TransactionStatus status) {
                            EntityManagerFactoryUtils.getTransactionalEntityManager(factory);
                            throw new RuntimeException("some exception");
                        }
                    });
                }
            });
            Assert.fail("Should have propagated RuntimeException");
        } catch (RuntimeException ex) {
            // expected
        }
        Assert.assertTrue((!(TransactionSynchronizationManager.hasResource(factory))));
        Assert.assertTrue((!(TransactionSynchronizationManager.isSynchronizationActive())));
        Mockito.verify(tx).setRollbackOnly();
        Mockito.verify(tx).rollback();
        Mockito.verify(manager).close();
    }

    @Test
    public void testParticipatingTransactionWithRollbackOnly() {
        BDDMockito.given(manager.getTransaction()).willReturn(tx);
        BDDMockito.given(tx.isActive()).willReturn(true);
        BDDMockito.given(tx.getRollbackOnly()).willReturn(true);
        BDDMockito.willThrow(new RollbackException()).given(tx).commit();
        final List<String> l = new ArrayList<>();
        l.add("test");
        Assert.assertTrue((!(TransactionSynchronizationManager.hasResource(factory))));
        Assert.assertTrue((!(TransactionSynchronizationManager.isSynchronizationActive())));
        try {
            tt.execute(new TransactionCallback() {
                @Override
                public Object doInTransaction(TransactionStatus status) {
                    Assert.assertTrue(TransactionSynchronizationManager.hasResource(factory));
                    return tt.execute(new TransactionCallback() {
                        @Override
                        public Object doInTransaction(TransactionStatus status) {
                            EntityManagerFactoryUtils.getTransactionalEntityManager(factory).flush();
                            status.setRollbackOnly();
                            return null;
                        }
                    });
                }
            });
            Assert.fail("Should have thrown TransactionSystemException");
        } catch (TransactionSystemException tse) {
            // expected
            Assert.assertTrue(((tse.getCause()) instanceof RollbackException));
        }
        Assert.assertTrue((!(TransactionSynchronizationManager.hasResource(factory))));
        Assert.assertTrue((!(TransactionSynchronizationManager.isSynchronizationActive())));
        Mockito.verify(manager).flush();
        Mockito.verify(tx).setRollbackOnly();
        Mockito.verify(manager).close();
    }

    @Test
    public void testParticipatingTransactionWithRequiresNew() {
        tt.setPropagationBehavior(PROPAGATION_REQUIRES_NEW);
        BDDMockito.given(factory.createEntityManager()).willReturn(manager);
        BDDMockito.given(manager.getTransaction()).willReturn(tx);
        BDDMockito.given(manager.isOpen()).willReturn(true);
        final List<String> l = new ArrayList<>();
        l.add("test");
        Assert.assertTrue((!(TransactionSynchronizationManager.hasResource(factory))));
        Assert.assertTrue((!(TransactionSynchronizationManager.isSynchronizationActive())));
        Object result = tt.execute(new TransactionCallback() {
            @Override
            public Object doInTransaction(TransactionStatus status) {
                Assert.assertTrue(TransactionSynchronizationManager.hasResource(factory));
                return tt.execute(new TransactionCallback() {
                    @Override
                    public Object doInTransaction(TransactionStatus status) {
                        EntityManagerFactoryUtils.getTransactionalEntityManager(factory).flush();
                        return l;
                    }
                });
            }
        });
        Assert.assertSame(l, result);
        Assert.assertTrue((!(TransactionSynchronizationManager.hasResource(factory))));
        Assert.assertTrue((!(TransactionSynchronizationManager.isSynchronizationActive())));
        Mockito.verify(manager).flush();
        Mockito.verify(manager, Mockito.times(2)).close();
        Mockito.verify(tx, Mockito.times(2)).begin();
    }

    @Test
    public void testParticipatingTransactionWithRequiresNewAndPrebound() {
        tt.setPropagationBehavior(PROPAGATION_REQUIRES_NEW);
        BDDMockito.given(manager.getTransaction()).willReturn(tx);
        final List<String> l = new ArrayList<>();
        l.add("test");
        Assert.assertTrue((!(TransactionSynchronizationManager.hasResource(factory))));
        Assert.assertTrue((!(TransactionSynchronizationManager.isSynchronizationActive())));
        TransactionSynchronizationManager.bindResource(factory, new EntityManagerHolder(manager));
        try {
            Object result = tt.execute(new TransactionCallback() {
                @Override
                public Object doInTransaction(TransactionStatus status) {
                    EntityManagerFactoryUtils.getTransactionalEntityManager(factory);
                    Assert.assertTrue(TransactionSynchronizationManager.hasResource(factory));
                    return tt.execute(new TransactionCallback() {
                        @Override
                        public Object doInTransaction(TransactionStatus status) {
                            EntityManagerFactoryUtils.getTransactionalEntityManager(factory).flush();
                            return l;
                        }
                    });
                }
            });
            Assert.assertSame(l, result);
        } finally {
            TransactionSynchronizationManager.unbindResource(factory);
        }
        Assert.assertTrue((!(TransactionSynchronizationManager.hasResource(factory))));
        Assert.assertTrue((!(TransactionSynchronizationManager.isSynchronizationActive())));
        Mockito.verify(tx, Mockito.times(2)).begin();
        Mockito.verify(tx, Mockito.times(2)).commit();
        Mockito.verify(manager).flush();
        Mockito.verify(manager).close();
    }

    @Test
    public void testPropagationSupportsAndRequiresNew() {
        tt.setPropagationBehavior(PROPAGATION_SUPPORTS);
        BDDMockito.given(manager.getTransaction()).willReturn(tx);
        final List<String> l = new ArrayList<>();
        l.add("test");
        Assert.assertTrue((!(TransactionSynchronizationManager.hasResource(factory))));
        Assert.assertTrue((!(TransactionSynchronizationManager.isSynchronizationActive())));
        Object result = tt.execute(new TransactionCallback() {
            @Override
            public Object doInTransaction(TransactionStatus status) {
                Assert.assertFalse(TransactionSynchronizationManager.hasResource(factory));
                TransactionTemplate tt2 = new TransactionTemplate(tm);
                tt2.setPropagationBehavior(PROPAGATION_REQUIRES_NEW);
                return tt2.execute(new TransactionCallback() {
                    @Override
                    public Object doInTransaction(TransactionStatus status) {
                        EntityManagerFactoryUtils.getTransactionalEntityManager(factory).flush();
                        return l;
                    }
                });
            }
        });
        Assert.assertSame(l, result);
        Assert.assertTrue((!(TransactionSynchronizationManager.hasResource(factory))));
        Assert.assertTrue((!(TransactionSynchronizationManager.isSynchronizationActive())));
        Mockito.verify(tx).commit();
        Mockito.verify(manager).flush();
        Mockito.verify(manager).close();
    }

    @Test
    public void testPropagationSupportsAndRequiresNewAndEarlyAccess() {
        tt.setPropagationBehavior(PROPAGATION_SUPPORTS);
        BDDMockito.given(factory.createEntityManager()).willReturn(manager);
        BDDMockito.given(manager.getTransaction()).willReturn(tx);
        BDDMockito.given(manager.isOpen()).willReturn(true);
        final List<String> l = new ArrayList<>();
        l.add("test");
        Assert.assertTrue((!(TransactionSynchronizationManager.hasResource(factory))));
        Assert.assertTrue((!(TransactionSynchronizationManager.isSynchronizationActive())));
        Object result = tt.execute(new TransactionCallback() {
            @Override
            public Object doInTransaction(TransactionStatus status) {
                EntityManagerFactoryUtils.getTransactionalEntityManager(factory);
                Assert.assertTrue(TransactionSynchronizationManager.hasResource(factory));
                TransactionTemplate tt2 = new TransactionTemplate(tm);
                tt2.setPropagationBehavior(PROPAGATION_REQUIRES_NEW);
                return tt2.execute(new TransactionCallback() {
                    @Override
                    public Object doInTransaction(TransactionStatus status) {
                        EntityManagerFactoryUtils.getTransactionalEntityManager(factory).flush();
                        return l;
                    }
                });
            }
        });
        Assert.assertSame(l, result);
        Assert.assertTrue((!(TransactionSynchronizationManager.hasResource(factory))));
        Assert.assertTrue((!(TransactionSynchronizationManager.isSynchronizationActive())));
        Mockito.verify(tx).commit();
        Mockito.verify(manager).flush();
        Mockito.verify(manager, Mockito.times(2)).close();
    }

    @Test
    public void testTransactionWithRequiresNewInAfterCompletion() {
        tt.setPropagationBehavior(PROPAGATION_REQUIRES_NEW);
        EntityManager manager2 = Mockito.mock(EntityManager.class);
        EntityTransaction tx2 = Mockito.mock(EntityTransaction.class);
        BDDMockito.given(manager.getTransaction()).willReturn(tx);
        BDDMockito.given(factory.createEntityManager()).willReturn(manager, manager2);
        BDDMockito.given(manager2.getTransaction()).willReturn(tx2);
        BDDMockito.given(manager2.isOpen()).willReturn(true);
        Assert.assertTrue((!(TransactionSynchronizationManager.hasResource(factory))));
        Assert.assertTrue((!(TransactionSynchronizationManager.isSynchronizationActive())));
        tt.execute(new TransactionCallback() {
            @Override
            public Object doInTransaction(TransactionStatus status) {
                EntityManagerFactoryUtils.getTransactionalEntityManager(factory).flush();
                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                    @Override
                    public void afterCompletion(int status) {
                        tt.execute(new TransactionCallback() {
                            @Override
                            public Object doInTransaction(TransactionStatus status) {
                                EntityManagerFactoryUtils.getTransactionalEntityManager(factory).flush();
                                return null;
                            }
                        });
                    }
                });
                return null;
            }
        });
        Assert.assertTrue((!(TransactionSynchronizationManager.hasResource(factory))));
        Assert.assertTrue((!(TransactionSynchronizationManager.isSynchronizationActive())));
        Mockito.verify(tx).commit();
        Mockito.verify(tx2).begin();
        Mockito.verify(tx2).commit();
        Mockito.verify(manager).flush();
        Mockito.verify(manager).close();
        Mockito.verify(manager2).flush();
        Mockito.verify(manager2).close();
    }

    @Test
    public void testTransactionCommitWithPropagationSupports() {
        BDDMockito.given(manager.isOpen()).willReturn(true);
        final List<String> l = new ArrayList<>();
        l.add("test");
        tt.setPropagationBehavior(PROPAGATION_SUPPORTS);
        Assert.assertTrue((!(TransactionSynchronizationManager.hasResource(factory))));
        Assert.assertTrue((!(TransactionSynchronizationManager.isSynchronizationActive())));
        Object result = tt.execute(new TransactionCallback() {
            @Override
            public Object doInTransaction(TransactionStatus status) {
                Assert.assertTrue((!(TransactionSynchronizationManager.hasResource(factory))));
                Assert.assertTrue(TransactionSynchronizationManager.isSynchronizationActive());
                Assert.assertTrue((!(status.isNewTransaction())));
                EntityManagerFactoryUtils.getTransactionalEntityManager(factory).flush();
                return l;
            }
        });
        Assert.assertSame(l, result);
        Assert.assertTrue((!(TransactionSynchronizationManager.hasResource(factory))));
        Assert.assertTrue((!(TransactionSynchronizationManager.isSynchronizationActive())));
        Mockito.verify(manager).flush();
        Mockito.verify(manager).close();
    }

    @Test
    public void testTransactionRollbackWithPropagationSupports() {
        BDDMockito.given(manager.isOpen()).willReturn(true);
        tt.setPropagationBehavior(PROPAGATION_SUPPORTS);
        Assert.assertTrue((!(TransactionSynchronizationManager.hasResource(factory))));
        Assert.assertTrue((!(TransactionSynchronizationManager.isSynchronizationActive())));
        tt.execute(new TransactionCallback() {
            @Override
            public Object doInTransaction(TransactionStatus status) {
                Assert.assertTrue((!(TransactionSynchronizationManager.hasResource(factory))));
                Assert.assertTrue(TransactionSynchronizationManager.isSynchronizationActive());
                Assert.assertTrue((!(status.isNewTransaction())));
                EntityManagerFactoryUtils.getTransactionalEntityManager(factory).flush();
                status.setRollbackOnly();
                return null;
            }
        });
        Assert.assertTrue((!(TransactionSynchronizationManager.hasResource(factory))));
        Assert.assertTrue((!(TransactionSynchronizationManager.isSynchronizationActive())));
        Mockito.verify(manager).flush();
        Mockito.verify(manager).close();
    }

    @Test
    public void testTransactionCommitWithPrebound() {
        BDDMockito.given(manager.getTransaction()).willReturn(tx);
        final List<String> l = new ArrayList<>();
        l.add("test");
        Assert.assertTrue((!(TransactionSynchronizationManager.hasResource(factory))));
        Assert.assertTrue((!(TransactionSynchronizationManager.isSynchronizationActive())));
        TransactionSynchronizationManager.bindResource(factory, new EntityManagerHolder(manager));
        try {
            Object result = tt.execute(new TransactionCallback() {
                @Override
                public Object doInTransaction(TransactionStatus status) {
                    Assert.assertTrue(TransactionSynchronizationManager.hasResource(factory));
                    Assert.assertTrue(TransactionSynchronizationManager.isSynchronizationActive());
                    EntityManagerFactoryUtils.getTransactionalEntityManager(factory);
                    return l;
                }
            });
            Assert.assertSame(l, result);
            Assert.assertTrue(TransactionSynchronizationManager.hasResource(factory));
            Assert.assertTrue((!(TransactionSynchronizationManager.isSynchronizationActive())));
        } finally {
            TransactionSynchronizationManager.unbindResource(factory);
        }
        Mockito.verify(tx).begin();
        Mockito.verify(tx).commit();
    }

    @Test
    public void testTransactionRollbackWithPrebound() {
        BDDMockito.given(manager.getTransaction()).willReturn(tx);
        BDDMockito.given(tx.isActive()).willReturn(true);
        Assert.assertTrue((!(TransactionSynchronizationManager.hasResource(factory))));
        Assert.assertTrue((!(TransactionSynchronizationManager.isSynchronizationActive())));
        TransactionSynchronizationManager.bindResource(factory, new EntityManagerHolder(manager));
        try {
            tt.execute(new TransactionCallback() {
                @Override
                public Object doInTransaction(TransactionStatus status) {
                    Assert.assertTrue(TransactionSynchronizationManager.hasResource(factory));
                    Assert.assertTrue(TransactionSynchronizationManager.isSynchronizationActive());
                    EntityManagerFactoryUtils.getTransactionalEntityManager(factory);
                    status.setRollbackOnly();
                    return null;
                }
            });
            Assert.assertTrue(TransactionSynchronizationManager.hasResource(factory));
            Assert.assertTrue((!(TransactionSynchronizationManager.isSynchronizationActive())));
        } finally {
            TransactionSynchronizationManager.unbindResource(factory);
        }
        Mockito.verify(tx).begin();
        Mockito.verify(tx).rollback();
        Mockito.verify(manager).clear();
    }

    @Test
    public void testTransactionCommitWithPreboundAndPropagationSupports() {
        final List<String> l = new ArrayList<>();
        l.add("test");
        tt.setPropagationBehavior(PROPAGATION_SUPPORTS);
        Assert.assertTrue((!(TransactionSynchronizationManager.hasResource(factory))));
        Assert.assertTrue((!(TransactionSynchronizationManager.isSynchronizationActive())));
        TransactionSynchronizationManager.bindResource(factory, new EntityManagerHolder(manager));
        try {
            Object result = tt.execute(new TransactionCallback() {
                @Override
                public Object doInTransaction(TransactionStatus status) {
                    Assert.assertTrue(TransactionSynchronizationManager.hasResource(factory));
                    Assert.assertTrue(TransactionSynchronizationManager.isSynchronizationActive());
                    Assert.assertTrue((!(status.isNewTransaction())));
                    EntityManagerFactoryUtils.getTransactionalEntityManager(factory).flush();
                    return l;
                }
            });
            Assert.assertSame(l, result);
            Assert.assertTrue(TransactionSynchronizationManager.hasResource(factory));
            Assert.assertTrue((!(TransactionSynchronizationManager.isSynchronizationActive())));
        } finally {
            TransactionSynchronizationManager.unbindResource(factory);
        }
        Mockito.verify(manager).flush();
    }

    @Test
    public void testTransactionRollbackWithPreboundAndPropagationSupports() {
        tt.setPropagationBehavior(PROPAGATION_SUPPORTS);
        Assert.assertTrue((!(TransactionSynchronizationManager.hasResource(factory))));
        Assert.assertTrue((!(TransactionSynchronizationManager.isSynchronizationActive())));
        TransactionSynchronizationManager.bindResource(factory, new EntityManagerHolder(manager));
        try {
            tt.execute(new TransactionCallback() {
                @Override
                public Object doInTransaction(TransactionStatus status) {
                    Assert.assertTrue(TransactionSynchronizationManager.hasResource(factory));
                    Assert.assertTrue(TransactionSynchronizationManager.isSynchronizationActive());
                    Assert.assertTrue((!(status.isNewTransaction())));
                    EntityManagerFactoryUtils.getTransactionalEntityManager(factory).flush();
                    status.setRollbackOnly();
                    return null;
                }
            });
            Assert.assertTrue(TransactionSynchronizationManager.hasResource(factory));
            Assert.assertTrue((!(TransactionSynchronizationManager.isSynchronizationActive())));
        } finally {
            TransactionSynchronizationManager.unbindResource(factory);
        }
        Mockito.verify(manager).flush();
        Mockito.verify(manager).clear();
    }

    @Test
    public void testInvalidIsolation() {
        tt.setIsolationLevel(ISOLATION_SERIALIZABLE);
        BDDMockito.given(manager.isOpen()).willReturn(true);
        try {
            tt.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus status) {
                }
            });
            Assert.fail("Should have thrown InvalidIsolationLevelException");
        } catch (InvalidIsolationLevelException ex) {
            // expected
        }
        Mockito.verify(manager).close();
    }

    @Test
    public void testTransactionFlush() {
        BDDMockito.given(manager.getTransaction()).willReturn(tx);
        Assert.assertTrue((!(TransactionSynchronizationManager.hasResource(factory))));
        Assert.assertTrue((!(TransactionSynchronizationManager.isSynchronizationActive())));
        tt.execute(new TransactionCallbackWithoutResult() {
            @Override
            public void doInTransactionWithoutResult(TransactionStatus status) {
                Assert.assertTrue(TransactionSynchronizationManager.hasResource(factory));
                status.flush();
            }
        });
        Assert.assertTrue((!(TransactionSynchronizationManager.hasResource(factory))));
        Assert.assertTrue((!(TransactionSynchronizationManager.isSynchronizationActive())));
        Mockito.verify(tx).commit();
        Mockito.verify(manager).flush();
        Mockito.verify(manager).close();
    }
}

