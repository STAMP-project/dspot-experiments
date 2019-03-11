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


import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;


public class TransactionAwareSetFactoryTests {
    private TransactionTemplate transactionTemplate = new TransactionTemplate(new ResourcelessTransactionManager());

    private Set<String> set;

    @Test
    public void testAdd() {
        Assert.assertEquals(3, set.size());
        set.add("bucket");
        Assert.assertTrue(set.contains("bucket"));
    }

    @Test
    public void testRemove() {
        Assert.assertEquals(3, set.size());
        Assert.assertTrue(set.contains("spam"));
        set.remove("spam");
        Assert.assertFalse(set.contains("spam"));
    }

    @Test
    public void testClear() {
        Assert.assertEquals(3, set.size());
        set.clear();
        Assert.assertEquals(0, set.size());
    }

    @Test
    public void testTransactionalAdd() throws Exception {
        transactionTemplate.execute(new org.springframework.transaction.support.TransactionCallback<Void>() {
            @Override
            public Void doInTransaction(TransactionStatus status) {
                testAdd();
                return null;
            }
        });
        Assert.assertEquals(4, set.size());
    }

    @Test
    public void testTransactionalRemove() throws Exception {
        transactionTemplate.execute(new org.springframework.transaction.support.TransactionCallback<Void>() {
            @Override
            public Void doInTransaction(TransactionStatus status) {
                testRemove();
                return null;
            }
        });
        Assert.assertEquals(2, set.size());
    }

    @Test
    public void testTransactionalClear() throws Exception {
        transactionTemplate.execute(new org.springframework.transaction.support.TransactionCallback<Void>() {
            @Override
            public Void doInTransaction(TransactionStatus status) {
                testClear();
                return null;
            }
        });
        Assert.assertEquals(0, set.size());
    }

    @Test
    public void testTransactionalAddWithRollback() throws Exception {
        try {
            transactionTemplate.execute(new org.springframework.transaction.support.TransactionCallback<Void>() {
                @Override
                public Void doInTransaction(TransactionStatus status) {
                    testAdd();
                    throw new RuntimeException("Rollback!");
                }
            });
            Assert.fail("Expected RuntimeException");
        } catch (RuntimeException e) {
            Assert.assertEquals("Rollback!", e.getMessage());
        }
        Assert.assertEquals(3, set.size());
    }

    @Test
    public void testTransactionalRemoveWithRollback() throws Exception {
        try {
            transactionTemplate.execute(new org.springframework.transaction.support.TransactionCallback<Void>() {
                @Override
                public Void doInTransaction(TransactionStatus status) {
                    testRemove();
                    throw new RuntimeException("Rollback!");
                }
            });
            Assert.fail("Expected RuntimeException");
        } catch (RuntimeException e) {
            Assert.assertEquals("Rollback!", e.getMessage());
        }
        Assert.assertEquals(3, set.size());
    }

    @Test
    public void testTransactionalClearWithRollback() throws Exception {
        try {
            transactionTemplate.execute(new org.springframework.transaction.support.TransactionCallback<Void>() {
                @Override
                public Void doInTransaction(TransactionStatus status) {
                    testClear();
                    throw new RuntimeException("Rollback!");
                }
            });
            Assert.fail("Expected RuntimeException");
        } catch (RuntimeException e) {
            Assert.assertEquals("Rollback!", e.getMessage());
        }
        Assert.assertEquals(3, set.size());
    }
}

