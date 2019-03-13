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
import X.EMPTY_OBJECT_ARRAY;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import javax.cache.processor.EntryProcessor;
import javax.cache.processor.EntryProcessorException;
import javax.cache.processor.MutableEntry;
import org.apache.ignite.cache.CacheEntryProcessor;
import org.apache.ignite.transactions.Transaction;
import org.junit.Test;


/**
 * Tests to check behavior regarding transactions started via SQL.
 */
public class SqlTransactionsSelfTest extends AbstractSchemaSelfTest {
    /**
     * Test that BEGIN opens a transaction.
     */
    @Test
    public void testBegin() {
        execute(node(), "BEGIN");
        assertTxPresent();
        SqlTransactionsSelfTest.assertTxState(tx(), ACTIVE);
    }

    /**
     * Test that COMMIT commits a transaction.
     */
    @Test
    public void testCommit() {
        execute(node(), "BEGIN WORK");
        assertTxPresent();
        Transaction tx = tx();
        SqlTransactionsSelfTest.assertTxState(tx, ACTIVE);
        execute(node(), "COMMIT TRANSACTION");
        SqlTransactionsSelfTest.assertTxState(tx, COMMITTED);
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
        SqlTransactionsSelfTest.assertTxState(tx, ACTIVE);
        execute(node(), "ROLLBACK TRANSACTION");
        SqlTransactionsSelfTest.assertTxState(tx, ROLLED_BACK);
        assertSqlTxNotPresent();
    }

    /**
     * Test that attempting to perform various SQL operations within non SQL transaction yields an exception.
     */
    @Test
    public void testSqlOperationsWithinNonSqlTransaction() {
        assertSqlOperationWithinNonSqlTransactionThrows("COMMIT");
        assertSqlOperationWithinNonSqlTransactionThrows("ROLLBACK");
        assertSqlOperationWithinNonSqlTransactionThrows("SELECT * from ints");
        assertSqlOperationWithinNonSqlTransactionThrows("DELETE from ints");
        assertSqlOperationWithinNonSqlTransactionThrows("INSERT INTO ints(k, v) values(10, 15)");
        assertSqlOperationWithinNonSqlTransactionThrows("MERGE INTO ints(k, v) values(10, 15)");
        assertSqlOperationWithinNonSqlTransactionThrows("UPDATE ints SET v = 100 WHERE k = 5");
        assertSqlOperationWithinNonSqlTransactionThrows("create index idx on ints(v)");
        assertSqlOperationWithinNonSqlTransactionThrows("CREATE TABLE T(k int primary key, v int)");
    }

    /**
     * Test that attempting to perform a cache PUT operation from within an SQL transaction fails.
     */
    @Test
    public void testCacheOperationsFromSqlTransaction() {
        checkCacheOperationThrows("get", 1);
        checkCacheOperationThrows("getAsync", 1);
        checkCacheOperationThrows("getEntry", 1);
        checkCacheOperationThrows("getEntryAsync", 1);
        checkCacheOperationThrows("getAndPut", 1, 1);
        checkCacheOperationThrows("getAndPutAsync", 1, 1);
        checkCacheOperationThrows("getAndPutIfAbsent", 1, 1);
        checkCacheOperationThrows("getAndPutIfAbsentAsync", 1, 1);
        checkCacheOperationThrows("getAndReplace", 1, 1);
        checkCacheOperationThrows("getAndReplaceAsync", 1, 1);
        checkCacheOperationThrows("getAndRemove", 1);
        checkCacheOperationThrows("getAndRemoveAsync", 1);
        checkCacheOperationThrows("containsKey", 1);
        checkCacheOperationThrows("containsKeyAsync", 1);
        checkCacheOperationThrows("put", 1, 1);
        checkCacheOperationThrows("putAsync", 1, 1);
        checkCacheOperationThrows("putIfAbsent", 1, 1);
        checkCacheOperationThrows("putIfAbsentAsync", 1, 1);
        checkCacheOperationThrows("remove", 1);
        checkCacheOperationThrows("removeAsync", 1);
        checkCacheOperationThrows("remove", 1, 1);
        checkCacheOperationThrows("removeAsync", 1, 1);
        checkCacheOperationThrows("replace", 1, 1);
        checkCacheOperationThrows("replaceAsync", 1, 1);
        checkCacheOperationThrows("replace", 1, 1, 1);
        checkCacheOperationThrows("replaceAsync", 1, 1, 1);
        checkCacheOperationThrows("getAll", new HashSet<>(Arrays.asList(1, 2)));
        checkCacheOperationThrows("containsKeys", new HashSet<>(Arrays.asList(1, 2)));
        checkCacheOperationThrows("getEntries", new HashSet<>(Arrays.asList(1, 2)));
        checkCacheOperationThrows("putAll", Collections.singletonMap(1, 1));
        checkCacheOperationThrows("removeAll", new HashSet<>(Arrays.asList(1, 2)));
        checkCacheOperationThrows("getAllAsync", new HashSet<>(Arrays.asList(1, 2)));
        checkCacheOperationThrows("containsKeysAsync", new HashSet<>(Arrays.asList(1, 2)));
        checkCacheOperationThrows("getEntriesAsync", new HashSet<>(Arrays.asList(1, 2)));
        checkCacheOperationThrows("putAllAsync", Collections.singletonMap(1, 1));
        checkCacheOperationThrows("removeAllAsync", new HashSet<>(Arrays.asList(1, 2)));
        checkCacheOperationThrows("invoke", 1, SqlTransactionsSelfTest.ENTRY_PROC, EMPTY_OBJECT_ARRAY);
        checkCacheOperationThrows("invoke", 1, SqlTransactionsSelfTest.CACHE_ENTRY_PROC, EMPTY_OBJECT_ARRAY);
        checkCacheOperationThrows("invokeAsync", 1, SqlTransactionsSelfTest.ENTRY_PROC, EMPTY_OBJECT_ARRAY);
        checkCacheOperationThrows("invokeAsync", 1, SqlTransactionsSelfTest.CACHE_ENTRY_PROC, EMPTY_OBJECT_ARRAY);
        checkCacheOperationThrows("invokeAll", Collections.singletonMap(1, SqlTransactionsSelfTest.CACHE_ENTRY_PROC), EMPTY_OBJECT_ARRAY);
        checkCacheOperationThrows("invokeAll", Collections.singleton(1), SqlTransactionsSelfTest.CACHE_ENTRY_PROC, EMPTY_OBJECT_ARRAY);
        checkCacheOperationThrows("invokeAll", Collections.singleton(1), SqlTransactionsSelfTest.ENTRY_PROC, EMPTY_OBJECT_ARRAY);
        checkCacheOperationThrows("invokeAllAsync", Collections.singletonMap(1, SqlTransactionsSelfTest.CACHE_ENTRY_PROC), EMPTY_OBJECT_ARRAY);
        checkCacheOperationThrows("invokeAllAsync", Collections.singleton(1), SqlTransactionsSelfTest.CACHE_ENTRY_PROC, EMPTY_OBJECT_ARRAY);
        checkCacheOperationThrows("invokeAllAsync", Collections.singleton(1), SqlTransactionsSelfTest.ENTRY_PROC, EMPTY_OBJECT_ARRAY);
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

