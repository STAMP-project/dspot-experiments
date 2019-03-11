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
package org.springframework.batch.item.support;


import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;


public class TransactionAwareListItemReaderTests extends TestCase {
    private ListItemReader<String> reader;

    public void testNext() throws Exception {
        TestCase.assertEquals("a", reader.read());
        TestCase.assertEquals("b", reader.read());
        TestCase.assertEquals("c", reader.read());
        TestCase.assertEquals(null, reader.read());
    }

    public void testCommit() throws Exception {
        PlatformTransactionManager transactionManager = new ResourcelessTransactionManager();
        final List<Object> taken = new ArrayList<>();
        try {
            new org.springframework.transaction.support.TransactionTemplate(transactionManager).execute(new org.springframework.transaction.support.TransactionCallback<Void>() {
                @Override
                public Void doInTransaction(TransactionStatus status) {
                    taken.add(reader.read());
                    return null;
                }
            });
        } catch (RuntimeException e) {
            TestCase.fail("Unexpected RuntimeException");
            TestCase.assertEquals("Rollback!", e.getMessage());
        }
        TestCase.assertEquals(1, taken.size());
        TestCase.assertEquals("a", taken.get(0));
        taken.clear();
        Object next = reader.read();
        while (next != null) {
            taken.add(next);
            next = reader.read();
        } 
        // System.err.println(taken);
        TestCase.assertFalse(taken.contains("a"));
    }

    public void testTransactionalExhausted() throws Exception {
        PlatformTransactionManager transactionManager = new ResourcelessTransactionManager();
        final List<Object> taken = new ArrayList<>();
        new org.springframework.transaction.support.TransactionTemplate(transactionManager).execute(new org.springframework.transaction.support.TransactionCallback<Void>() {
            @Override
            public Void doInTransaction(TransactionStatus status) {
                Object next = reader.read();
                while (next != null) {
                    taken.add(next);
                    next = reader.read();
                } 
                return null;
            }
        });
        TestCase.assertEquals(3, taken.size());
        TestCase.assertEquals("a", taken.get(0));
    }

    public void testRollback() throws Exception {
        PlatformTransactionManager transactionManager = new ResourcelessTransactionManager();
        final List<Object> taken = new ArrayList<>();
        try {
            execute(new org.springframework.transaction.support.TransactionCallback<Void>() {
                @Override
                public Void doInTransaction(TransactionStatus status) {
                    taken.add(reader.read());
                    throw new RuntimeException("Rollback!");
                }
            });
            TestCase.fail("Expected RuntimeException");
        } catch (RuntimeException e) {
            TestCase.assertEquals("Rollback!", e.getMessage());
        }
        TestCase.assertEquals(1, taken.size());
        TestCase.assertEquals("a", taken.get(0));
        taken.clear();
        Object next = reader.read();
        while (next != null) {
            taken.add(next);
            next = reader.read();
        } 
        System.err.println(taken);
        TestCase.assertTrue(taken.contains("a"));
    }
}

