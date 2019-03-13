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
package org.apache.ignite.internal.processors.cache.mvcc;


import WriteMode.INVOKE;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.cache.processor.EntryProcessorException;
import javax.cache.processor.MutableEntry;
import org.apache.ignite.Ignite;
import org.apache.ignite.cache.CacheEntryProcessor;
import org.apache.ignite.internal.util.typedef.F;
import org.junit.Test;


/**
 * Test basic mvcc bulk cache operations.
 */
public class MvccRepeatableReadBulkOpsTest extends CacheMvccAbstractTest {
    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testRepeatableReadIsolationGetPut() throws Exception {
        checkOperations(ReadMode.GET, ReadMode.GET, WriteMode.PUT, true);
        checkOperations(ReadMode.GET, ReadMode.GET, WriteMode.PUT, false);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testRepeatableReadIsolationInvoke() throws Exception {
        checkOperations(ReadMode.GET, ReadMode.GET, INVOKE, true);
        checkOperations(ReadMode.GET, ReadMode.GET, INVOKE, false);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testRepeatableReadIsolationSqlPut() throws Exception {
        checkOperations(ReadMode.SQL, ReadMode.SQL, WriteMode.PUT, true);
        checkOperations(ReadMode.SQL, ReadMode.SQL, WriteMode.PUT, false);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testRepeatableReadIsolationSqlInvoke() throws Exception {
        checkOperations(ReadMode.SQL, ReadMode.SQL, INVOKE, true);
        checkOperations(ReadMode.SQL, ReadMode.SQL, INVOKE, false);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testRepeatableReadIsolationSqlDml() throws Exception {
        checkOperations(ReadMode.SQL, ReadMode.SQL, WriteMode.DML, true);
        checkOperations(ReadMode.SQL, ReadMode.SQL, WriteMode.DML, false);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testRepeatableReadIsolationGetDml() throws Exception {
        checkOperations(ReadMode.GET, ReadMode.GET, WriteMode.DML, true);
        checkOperations(ReadMode.GET, ReadMode.GET, WriteMode.DML, false);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testRepeatableReadIsolationMixedPut() throws Exception {
        checkOperations(ReadMode.SQL, ReadMode.GET, WriteMode.PUT, false);
        checkOperations(ReadMode.SQL, ReadMode.GET, WriteMode.PUT, true);
        checkOperations(ReadMode.SQL, ReadMode.GET, INVOKE, false);
        checkOperations(ReadMode.SQL, ReadMode.GET, INVOKE, true);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testRepeatableReadIsolationMixedPut2() throws Exception {
        checkOperations(ReadMode.GET, ReadMode.SQL, WriteMode.PUT, false);
        checkOperations(ReadMode.GET, ReadMode.SQL, WriteMode.PUT, true);
        checkOperations(ReadMode.GET, ReadMode.SQL, INVOKE, false);
        checkOperations(ReadMode.GET, ReadMode.SQL, INVOKE, true);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testRepeatableReadIsolationMixedDml() throws Exception {
        checkOperations(ReadMode.SQL, ReadMode.GET, WriteMode.DML, false);
        checkOperations(ReadMode.SQL, ReadMode.GET, WriteMode.DML, true);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testRepeatableReadIsolationMixedDml2() throws Exception {
        checkOperations(ReadMode.GET, ReadMode.SQL, WriteMode.DML, false);
        checkOperations(ReadMode.GET, ReadMode.SQL, WriteMode.DML, true);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testOperationConsistency() throws Exception {
        checkOperationsConsistency(WriteMode.PUT, false);
        checkOperationsConsistency(WriteMode.DML, false);
        checkOperationsConsistency(INVOKE, false);
        checkOperationsConsistency(WriteMode.PUT, true);
        checkOperationsConsistency(WriteMode.DML, true);
        checkOperationsConsistency(INVOKE, true);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testInvokeConsistency() throws Exception {
        Ignite node = /* requestFromClient ? nodesCount() - 1 : */
        grid(0);
        TestCache<Integer, MvccTestAccount> cache = new TestCache(node.cache(DEFAULT_CACHE_NAME));
        final Set<Integer> keys1 = new HashSet<>(3);
        final Set<Integer> keys2 = new HashSet<>(3);
        Set<Integer> allKeys = generateKeySet(cache.cache, keys1, keys2);
        final Map<Integer, MvccTestAccount> map1 = keys1.stream().collect(Collectors.toMap(( k) -> k, ( k) -> new MvccTestAccount(k, 1)));
        final Map<Integer, MvccTestAccount> map2 = keys2.stream().collect(Collectors.toMap(( k) -> k, ( k) -> new MvccTestAccount(k, 1)));
        assertEquals(0, cache.cache.size());
        updateEntries(cache, map1, INVOKE);
        assertEquals(3, cache.cache.size());
        updateEntries(cache, map1, INVOKE);
        assertEquals(3, cache.cache.size());
        getEntries(cache, allKeys, ReadMode.INVOKE);
        assertEquals(3, cache.cache.size());
        updateEntries(cache, map2, INVOKE);
        assertEquals(6, cache.cache.size());
        getEntries(cache, keys2, ReadMode.INVOKE);
        assertEquals(6, cache.cache.size());
        removeEntries(cache, keys1, INVOKE);
        assertEquals(3, cache.cache.size());
        removeEntries(cache, keys1, INVOKE);
        assertEquals(3, cache.cache.size());
        getEntries(cache, allKeys, ReadMode.INVOKE);
        assertEquals(3, cache.cache.size());
        updateEntries(cache, map1, INVOKE);
        assertEquals(6, cache.cache.size());
        removeEntries(cache, allKeys, INVOKE);
        assertEquals(0, cache.cache.size());
        getEntries(cache, allKeys, ReadMode.INVOKE);
        assertEquals(0, cache.cache.size());
    }

    /**
     * Applies get operation.
     */
    static class GetEntryProcessor<K, V> implements CacheEntryProcessor<K, V, V> {
        /**
         * {@inheritDoc }
         */
        @Override
        public V process(MutableEntry<K, V> entry, Object... arguments) throws EntryProcessorException {
            return entry.getValue();
        }
    }

    /**
     * Applies remove operation.
     */
    static class RemoveEntryProcessor<K, V, R> implements CacheEntryProcessor<K, V, R> {
        /**
         * {@inheritDoc }
         */
        @Override
        public R process(MutableEntry<K, V> entry, Object... arguments) throws EntryProcessorException {
            entry.remove();
            return null;
        }
    }

    /**
     * Applies get and put operation.
     */
    static class GetAndPutEntryProcessor<K, V> implements CacheEntryProcessor<K, V, V> {
        /**
         * {@inheritDoc }
         */
        @Override
        public V process(MutableEntry<K, V> entry, Object... args) throws EntryProcessorException {
            V newVal = (!(F.isEmpty(args))) ? ((V) (args[0])) : newValForKey(entry.getKey());
            V oldVal = entry.getValue();
            entry.setValue(newVal);
            return oldVal;
        }

        /**
         *
         *
         * @param key
         * 		Key.
         * @return New value.
         */
        V newValForKey(K key) {
            throw new UnsupportedOperationException();
        }
    }
}

