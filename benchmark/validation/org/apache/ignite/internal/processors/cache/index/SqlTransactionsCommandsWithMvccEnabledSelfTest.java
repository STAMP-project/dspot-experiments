/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ignite.internal.processors.cache.index;


import TransactionState.ACTIVE;
import TransactionState.COMMITTED;
import TransactionState.ROLLED_BACK;
import javax.cache.processor.EntryProcessor;
import javax.cache.processor.EntryProcessorException;
import javax.cache.processor.MutableEntry;
import org.apache.ignite.cache.CacheEntryProcessor;
import org.apache.ignite.transactions.Transaction;
import org.junit.Test;


/**
 * Tests to check behavior regarding transactions started via SQL.
 */
public class SqlTransactionsCommandsWithMvccEnabledSelfTest extends AbstractSchemaSelfTest {
    /**
     * Test that BEGIN opens a transaction.
     */
    @Test
    public void testBegin() {
        execute(node(), "BEGIN");
        assertTxPresent();
        SqlTransactionsCommandsWithMvccEnabledSelfTest.assertTxState(tx(), ACTIVE);
    }

    /**
     * Test that COMMIT commits a transaction.
     */
    @Test
    public void testCommit() {
        execute(node(), "BEGIN WORK");
        assertTxPresent();
        Transaction tx = tx();
        SqlTransactionsCommandsWithMvccEnabledSelfTest.assertTxState(tx, ACTIVE);
        execute(node(), "COMMIT TRANSACTION");
        SqlTransactionsCommandsWithMvccEnabledSelfTest.assertTxState(tx, COMMITTED);
        assertSqlTxNotPresent();
    }

    /**
     * Test that COMMIT without a transaction yields nothing.
     */
    @Test
    public void testCommitNoTransaction() {
        execute(node(), "COMMIT");
    }

    /**
     * Test that ROLLBACK without a transaction yields nothing.
     */
    @Test
    public void testRollbackNoTransaction() {
        execute(node(), "ROLLBACK");
    }

    /**
     * Test that ROLLBACK rolls back a transaction.
     */
    @Test
    public void testRollback() {
        execute(node(), "BEGIN TRANSACTION");
        assertTxPresent();
        Transaction tx = tx();
        SqlTransactionsCommandsWithMvccEnabledSelfTest.assertTxState(tx, ACTIVE);
        execute(node(), "ROLLBACK TRANSACTION");
        SqlTransactionsCommandsWithMvccEnabledSelfTest.assertTxState(tx, ROLLED_BACK);
        assertSqlTxNotPresent();
    }

    /**
     *
     */
    private static final EntryProcessor<Integer, Integer, Object> ENTRY_PROC = new EntryProcessor<Integer, Integer, Object>() {
        @Override
        public Object process(MutableEntry<Integer, Integer> entry, Object... arguments) throws EntryProcessorException {
            return null;
        }
    };

    /**
     *
     */
    private static final CacheEntryProcessor<Integer, Integer, Object> CACHE_ENTRY_PROC = new CacheEntryProcessor<Integer, Integer, Object>() {
        @Override
        public Object process(MutableEntry<Integer, Integer> entry, Object... arguments) throws EntryProcessorException {
            return null;
        }
    };
}

