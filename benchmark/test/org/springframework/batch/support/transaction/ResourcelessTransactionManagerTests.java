/**
 * Copyright 2006-2007 the original author or authors.
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
package org.springframework.batch.support.transaction;


import TransactionSynchronization.STATUS_COMMITTED;
import TransactionSynchronization.STATUS_ROLLED_BACK;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;


public class ResourcelessTransactionManagerTests {
    private ResourcelessTransactionManager transactionManager = new ResourcelessTransactionManager();

    private int txStatus = Integer.MIN_VALUE;

    private int count = 0;

    @Test
    public void testCommit() throws Exception {
        new TransactionTemplate(transactionManager).execute(new org.springframework.transaction.support.TransactionCallback<Void>() {
            @Override
            public Void doInTransaction(TransactionStatus status) {
                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                    @Override
                    public void afterCompletion(int status) {
                        super.afterCompletion(status);
                        txStatus = status;
                    }
                });
                return null;
            }
        });
        Assert.assertEquals(STATUS_COMMITTED, txStatus);
    }

    @Test
    public void testCommitTwice() throws Exception {
        testCommit();
        txStatus = -1;
        new TransactionTemplate(transactionManager).execute(new org.springframework.transaction.support.TransactionCallback<Void>() {
            @Override
            public Void doInTransaction(TransactionStatus status) {
                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                    @Override
                    public void afterCompletion(int status) {
                        super.afterCompletion(status);
                        txStatus = status;
                    }
                });
                return null;
            }
        });
        Assert.assertEquals(STATUS_COMMITTED, txStatus);
    }

    @Test
    public void testCommitNested() throws Exception {
        final TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.execute(new org.springframework.transaction.support.TransactionCallback<Void>() {
            @Override
            public Void doInTransaction(TransactionStatus status) {
                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                    @Override
                    public void afterCompletion(int status) {
                        super.afterCompletion(status);
                        txStatus = status;
                        (count)++;
                    }
                });
                transactionTemplate.execute(new org.springframework.transaction.support.TransactionCallback<Void>() {
                    @Override
                    public Void doInTransaction(TransactionStatus status) {
                        Assert.assertEquals(0, count);
                        (count)++;
                        return null;
                    }
                });
                Assert.assertEquals(1, count);
                return null;
            }
        });
        Assert.assertEquals(STATUS_COMMITTED, txStatus);
        Assert.assertEquals(2, count);
    }

    @Test
    public void testCommitNestedTwice() throws Exception {
        testCommitNested();
        count = 0;
        txStatus = -1;
        final TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.execute(new org.springframework.transaction.support.TransactionCallback<Void>() {
            @Override
            public Void doInTransaction(TransactionStatus status) {
                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                    @Override
                    public void afterCompletion(int status) {
                        super.afterCompletion(status);
                        txStatus = status;
                        (count)++;
                    }
                });
                transactionTemplate.execute(new org.springframework.transaction.support.TransactionCallback<Void>() {
                    @Override
                    public Void doInTransaction(TransactionStatus status) {
                        Assert.assertEquals(0, count);
                        (count)++;
                        return null;
                    }
                });
                Assert.assertEquals(1, count);
                return null;
            }
        });
        Assert.assertEquals(STATUS_COMMITTED, txStatus);
        Assert.assertEquals(2, count);
    }

    @Test
    public void testRollback() throws Exception {
        try {
            execute(new org.springframework.transaction.support.TransactionCallback<Void>() {
                @Override
                public Void doInTransaction(TransactionStatus status) {
                    TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                        @Override
                        public void afterCompletion(int status) {
                            super.afterCompletion(status);
                            txStatus = status;
                        }
                    });
                    throw new RuntimeException("Rollback!");
                }
            });
            Assert.fail("Expected RuntimeException");
        } catch (RuntimeException e) {
            Assert.assertEquals("Rollback!", e.getMessage());
        }
        Assert.assertEquals(STATUS_ROLLED_BACK, txStatus);
    }

    @Test
    public void testRollbackNestedInner() throws Exception {
        final TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        try {
            transactionTemplate.execute(new org.springframework.transaction.support.TransactionCallback<Void>() {
                @Override
                public Void doInTransaction(TransactionStatus status) {
                    TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                        @Override
                        public void afterCompletion(int status) {
                            super.afterCompletion(status);
                            txStatus = status;
                            (count)++;
                        }
                    });
                    transactionTemplate.execute(new org.springframework.transaction.support.TransactionCallback<Void>() {
                        @Override
                        public Void doInTransaction(TransactionStatus status) {
                            Assert.assertEquals(0, count);
                            (count)++;
                            throw new RuntimeException("Rollback!");
                        }
                    });
                    Assert.assertEquals(1, count);
                    return null;
                }
            });
            Assert.fail("Expected RuntimeException");
        } catch (RuntimeException e) {
            Assert.assertEquals("Rollback!", e.getMessage());
        }
        Assert.assertEquals(STATUS_ROLLED_BACK, txStatus);
        Assert.assertEquals(2, count);
    }

    @Test
    public void testRollbackNestedOuter() throws Exception {
        final TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        try {
            transactionTemplate.execute(new org.springframework.transaction.support.TransactionCallback<Void>() {
                @Override
                public Void doInTransaction(TransactionStatus status) {
                    TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                        @Override
                        public void afterCompletion(int status) {
                            super.afterCompletion(status);
                            txStatus = status;
                            (count)++;
                        }
                    });
                    transactionTemplate.execute(new org.springframework.transaction.support.TransactionCallback<Void>() {
                        @Override
                        public Void doInTransaction(TransactionStatus status) {
                            Assert.assertEquals(0, count);
                            (count)++;
                            return null;
                        }
                    });
                    Assert.assertEquals(1, count);
                    throw new RuntimeException("Rollback!");
                }
            });
            Assert.fail("Expected RuntimeException");
        } catch (RuntimeException e) {
            Assert.assertEquals("Rollback!", e.getMessage());
        }
        Assert.assertEquals(STATUS_ROLLED_BACK, txStatus);
        Assert.assertEquals(2, count);
    }
}

