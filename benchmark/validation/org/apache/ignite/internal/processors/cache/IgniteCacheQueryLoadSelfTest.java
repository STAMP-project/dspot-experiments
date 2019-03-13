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
package org.apache.ignite.internal.processors.cache;


import Cache.Entry;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.cache.integration.CompletionListenerFuture;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.query.SqlQuery;
import org.apache.ignite.cache.query.annotations.QuerySqlField;
import org.apache.ignite.cache.store.CacheStoreAdapter;
import org.apache.ignite.internal.util.typedef.F;
import org.apache.ignite.internal.util.typedef.internal.S;
import org.apache.ignite.lang.IgniteBiInClosure;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;


/**
 * Test that entries are indexed on load/reload methods.
 */
public class IgniteCacheQueryLoadSelfTest extends GridCommonAbstractTest {
    /**
     * Puts count.
     */
    private static final int PUT_CNT = 10;

    /**
     * Store map.
     */
    private static final Map<Integer, IgniteCacheQueryLoadSelfTest.ValueObject> STORE_MAP = new HashMap<>();

    /**
     *
     */
    public IgniteCacheQueryLoadSelfTest() {
        super(true);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testLoadCache() throws Exception {
        IgniteCache<Integer, IgniteCacheQueryLoadSelfTest.ValueObject> cache = grid().cache(DEFAULT_CACHE_NAME);
        cache.loadCache(null);
        assertEquals(IgniteCacheQueryLoadSelfTest.PUT_CNT, cache.size());
        Collection<Entry<Integer, IgniteCacheQueryLoadSelfTest.ValueObject>> res = cache.query(new SqlQuery(IgniteCacheQueryLoadSelfTest.ValueObject.class, "val >= 0")).getAll();
        assertNotNull(res);
        assertEquals(IgniteCacheQueryLoadSelfTest.PUT_CNT, res.size());
        assertEquals(IgniteCacheQueryLoadSelfTest.PUT_CNT, size(IgniteCacheQueryLoadSelfTest.ValueObject.class));
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testLoadCacheAsync() throws Exception {
        IgniteCache<Integer, IgniteCacheQueryLoadSelfTest.ValueObject> cache = grid().cache(DEFAULT_CACHE_NAME);
        cache.loadCacheAsync(null, 0).get();
        assert (cache.size()) == (IgniteCacheQueryLoadSelfTest.PUT_CNT);
        Collection<Entry<Integer, IgniteCacheQueryLoadSelfTest.ValueObject>> res = cache.query(new SqlQuery(IgniteCacheQueryLoadSelfTest.ValueObject.class, "val >= 0")).getAll();
        assert res != null;
        assert (res.size()) == (IgniteCacheQueryLoadSelfTest.PUT_CNT);
        assert (size(IgniteCacheQueryLoadSelfTest.ValueObject.class)) == (IgniteCacheQueryLoadSelfTest.PUT_CNT);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testLoadCacheFiltered() throws Exception {
        IgniteCache<Integer, IgniteCacheQueryLoadSelfTest.ValueObject> cache = grid().cache(DEFAULT_CACHE_NAME);
        cache.loadCache(new org.apache.ignite.internal.util.typedef.P2<Integer, IgniteCacheQueryLoadSelfTest.ValueObject>() {
            @Override
            public boolean apply(Integer key, IgniteCacheQueryLoadSelfTest.ValueObject val) {
                return key >= 5;
            }
        });
        assert (cache.size()) == ((IgniteCacheQueryLoadSelfTest.PUT_CNT) - 5);
        Collection<Entry<Integer, IgniteCacheQueryLoadSelfTest.ValueObject>> res = cache.query(new SqlQuery(IgniteCacheQueryLoadSelfTest.ValueObject.class, "val >= 0")).getAll();
        assert res != null;
        assert (res.size()) == ((IgniteCacheQueryLoadSelfTest.PUT_CNT) - 5);
        assert (size(IgniteCacheQueryLoadSelfTest.ValueObject.class)) == ((IgniteCacheQueryLoadSelfTest.PUT_CNT) - 5);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testLoadCacheAsyncFiltered() throws Exception {
        IgniteCache<Integer, IgniteCacheQueryLoadSelfTest.ValueObject> cache = grid().cache(DEFAULT_CACHE_NAME);
        cache.loadCacheAsync(new org.apache.ignite.internal.util.typedef.P2<Integer, IgniteCacheQueryLoadSelfTest.ValueObject>() {
            @Override
            public boolean apply(Integer key, IgniteCacheQueryLoadSelfTest.ValueObject val) {
                return key >= 5;
            }
        }, 0).get();
        assert (cache.localSize()) == ((IgniteCacheQueryLoadSelfTest.PUT_CNT) - 5);
        Collection<Entry<Integer, IgniteCacheQueryLoadSelfTest.ValueObject>> res = cache.query(new SqlQuery(IgniteCacheQueryLoadSelfTest.ValueObject.class, "val >= 0")).getAll();
        assert res != null;
        assert (res.size()) == ((IgniteCacheQueryLoadSelfTest.PUT_CNT) - 5);
        assert (size(IgniteCacheQueryLoadSelfTest.ValueObject.class)) == ((IgniteCacheQueryLoadSelfTest.PUT_CNT) - 5);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testReloadAsync() throws Exception {
        IgniteCacheQueryLoadSelfTest.STORE_MAP.put(1, new IgniteCacheQueryLoadSelfTest.ValueObject(1));
        IgniteCache<Integer, IgniteCacheQueryLoadSelfTest.ValueObject> cache = jcache();
        assert (cache.getAsync(1).get().value()) == 1;
        assert (cache.size()) == 1;
        Collection<Entry<Integer, IgniteCacheQueryLoadSelfTest.ValueObject>> res = cache.query(new SqlQuery(IgniteCacheQueryLoadSelfTest.ValueObject.class, "val >= 0")).getAll();
        assert res != null;
        assert (res.size()) == 1;
        assert (size(IgniteCacheQueryLoadSelfTest.ValueObject.class)) == 1;
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testReloadAll() throws Exception {
        for (int i = 0; i < (IgniteCacheQueryLoadSelfTest.PUT_CNT); i++)
            IgniteCacheQueryLoadSelfTest.STORE_MAP.put(i, new IgniteCacheQueryLoadSelfTest.ValueObject(i));

        IgniteCache<Integer, IgniteCacheQueryLoadSelfTest.ValueObject> cache = jcache();
        Integer[] keys = new Integer[(IgniteCacheQueryLoadSelfTest.PUT_CNT) - 5];
        for (int i = 0; i < ((IgniteCacheQueryLoadSelfTest.PUT_CNT) - 5); i++)
            keys[i] = i + 5;

        CompletionListenerFuture fut = new CompletionListenerFuture();
        grid().<Integer, Integer>cache(DEFAULT_CACHE_NAME).loadAll(F.asSet(keys), true, fut);
        fut.get();
        assert (cache.size()) == ((IgniteCacheQueryLoadSelfTest.PUT_CNT) - 5);
        Collection<Entry<Integer, IgniteCacheQueryLoadSelfTest.ValueObject>> res = cache.query(new SqlQuery(IgniteCacheQueryLoadSelfTest.ValueObject.class, "val >= 0")).getAll();
        assert res != null;
        assert (res.size()) == ((IgniteCacheQueryLoadSelfTest.PUT_CNT) - 5);
        assert (size(IgniteCacheQueryLoadSelfTest.ValueObject.class)) == ((IgniteCacheQueryLoadSelfTest.PUT_CNT) - 5);
        cache.clear();
        assert (cache.size()) == 0;
        assertEquals(0, cache.size());
        fut = new CompletionListenerFuture();
        grid().<Integer, Integer>cache(DEFAULT_CACHE_NAME).loadAll(F.asSet(keys), true, fut);
        fut.get();
        assertEquals(((IgniteCacheQueryLoadSelfTest.PUT_CNT) - 5), cache.size());
        res = cache.query(new SqlQuery(IgniteCacheQueryLoadSelfTest.ValueObject.class, "val >= 0")).getAll();
        assert res != null;
        assert (res.size()) == ((IgniteCacheQueryLoadSelfTest.PUT_CNT) - 5);
        assert (size(IgniteCacheQueryLoadSelfTest.ValueObject.class)) == ((IgniteCacheQueryLoadSelfTest.PUT_CNT) - 5);
    }

    /**
     * Test store.
     */
    private static class TestStore extends CacheStoreAdapter<Integer, IgniteCacheQueryLoadSelfTest.ValueObject> {
        /**
         * {@inheritDoc }
         */
        @Override
        public void loadCache(IgniteBiInClosure<Integer, IgniteCacheQueryLoadSelfTest.ValueObject> clo, @Nullable
        Object... args) {
            assert clo != null;
            for (int i = 0; i < (IgniteCacheQueryLoadSelfTest.PUT_CNT); i++)
                clo.apply(i, new IgniteCacheQueryLoadSelfTest.ValueObject(i));

        }

        /**
         * {@inheritDoc }
         */
        @Override
        public IgniteCacheQueryLoadSelfTest.ValueObject load(Integer key) {
            assert key != null;
            return IgniteCacheQueryLoadSelfTest.STORE_MAP.get(key);
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public void write(javax.cache.Cache.Entry<? extends Integer, ? extends IgniteCacheQueryLoadSelfTest.ValueObject> e) {
            assert e != null;
            assert (e.getKey()) != null;
            assert (e.getValue()) != null;
            IgniteCacheQueryLoadSelfTest.STORE_MAP.put(e.getKey(), e.getValue());
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public void delete(Object key) {
            assert key != null;
            IgniteCacheQueryLoadSelfTest.STORE_MAP.remove(key);
        }
    }

    /**
     * Value object class.
     */
    private static class ValueObject implements Serializable {
        /**
         * Value.
         */
        @QuerySqlField
        private final int val;

        /**
         *
         *
         * @param val
         * 		Value.
         */
        ValueObject(int val) {
            this.val = val;
        }

        /**
         *
         *
         * @return Value.
         */
        int value() {
            return val;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public String toString() {
            return S.toString(IgniteCacheQueryLoadSelfTest.ValueObject.class, this);
        }
    }
}

