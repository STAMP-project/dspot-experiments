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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cache.affinity.Affinity;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.ScanQuery;
import org.apache.ignite.cluster.ClusterNode;
import org.apache.ignite.lang.IgniteBiPredicate;
import org.apache.ignite.lang.IgnitePredicate;
import org.apache.ignite.testframework.junits.IgniteCacheConfigVariationsAbstractTest;
import org.junit.Test;


/**
 * Config Variations query tests.
 */
public class IgniteCacheConfigVariationsQueryTest extends IgniteCacheConfigVariationsAbstractTest {
    /**
     *
     */
    public static final int CNT = 50;

    /**
     *
     */
    private Map<Object, Object> evtMap;

    /**
     *
     */
    private CountDownLatch readEvtLatch;

    /**
     *
     */
    private CountDownLatch execEvtLatch;

    /**
     *
     */
    private IgnitePredicate[] objReadLsnrs;

    /**
     *
     */
    private IgnitePredicate[] qryExecLsnrs;

    /**
     *
     */
    private Map<Object, Object> expMap;

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @SuppressWarnings("serial")
    @Test
    public void testScanQuery() throws Exception {
        runInAllDataModes(new TestRunnable() {
            @Override
            public void run() throws Exception {
                try {
                    IgniteCache<Object, Object> cache = jcache();
                    Map<Object, Object> map = new HashMap<Object, Object>() {
                        {
                            for (int i = 0; i < (IgniteCacheConfigVariationsQueryTest.CNT); i++)
                                put(key(i), value(i));

                        }
                    };
                    registerEventListeners(map);
                    for (Map.Entry<Object, Object> e : map.entrySet())
                        cache.put(e.getKey(), e.getValue());

                    // Scan query.
                    QueryCursor<Entry<Object, Object>> qry = cache.query(new ScanQuery());
                    checkQueryResults(map, qry);
                } finally {
                    stopListeners();
                }
            }
        });
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testScanPartitionQuery() throws Exception {
        runInAllDataModes(new TestRunnable() {
            @Override
            public void run() throws Exception {
                IgniteCache<Object, Object> cache = jcache();
                GridCacheContext cctx = context();
                Map<Integer, Map<Object, Object>> entries = new HashMap<>();
                for (int i = 0; i < (IgniteCacheConfigVariationsQueryTest.CNT); i++) {
                    Object key = key(i);
                    Object val = value(i);
                    cache.put(key, val);
                    int part = cctx.affinity().partition(key);
                    Map<Object, Object> partEntries = entries.get(part);
                    if (partEntries == null)
                        entries.put(part, (partEntries = new HashMap<>()));

                    partEntries.put(key, val);
                }
                for (int i = 0; i < (cctx.affinity().partitions()); i++) {
                    try {
                        Map<Object, Object> exp = entries.get(i);
                        if (exp == null)
                            System.out.println();

                        registerEventListeners(exp);
                        ScanQuery<Object, Object> scan = new ScanQuery(i);
                        Collection<Entry<Object, Object>> actual = cache.query(scan).getAll();
                        assertEquals(("Failed for partition: " + i), (exp == null ? 0 : exp.size()), actual.size());
                        if (exp != null) {
                            for (Entry<Object, Object> entry : actual)
                                assertTrue(entry.getValue().equals(exp.get(entry.getKey())));

                        }
                        checkEvents();
                    } finally {
                        stopListeners();
                    }
                }
            }
        });
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testScanFilters() throws Exception {
        runInAllDataModes(new TestRunnable() {
            @Override
            public void run() throws Exception {
                try {
                    IgniteCache<Object, Object> cache = jcache();
                    IgniteBiPredicate<Object, Object> p = new IgniteBiPredicate<Object, Object>() {
                        @Override
                        public boolean apply(Object k, Object v) {
                            assertNotNull(k);
                            assertNotNull(v);
                            return ((valueOf(k)) >= 20) && ((valueOf(v)) < 40);
                        }
                    };
                    Map<Object, Object> exp = new HashMap<>();
                    for (int i = 0; i < (IgniteCacheConfigVariationsQueryTest.CNT); i++) {
                        Object key = key(i);
                        Object val = value(i);
                        cache.put(key, val);
                        if (p.apply(key, val))
                            exp.put(key, val);

                    }
                    registerEventListeners(exp, true);
                    QueryCursor<Entry<Object, Object>> q = cache.query(new ScanQuery(p));
                    checkQueryResults(exp, q);
                } finally {
                    stopListeners();
                }
            }
        });
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testLocalScanQuery() throws Exception {
        runInAllDataModes(new TestRunnable() {
            @Override
            public void run() throws Exception {
                try {
                    IgniteCache<Object, Object> cache = jcache();
                    ClusterNode locNode = testedGrid().cluster().localNode();
                    Affinity<Object> affinity = testedGrid().affinity(cacheName());
                    Map<Object, Object> map = new HashMap<>();
                    for (int i = 0; i < (IgniteCacheConfigVariationsQueryTest.CNT); i++) {
                        Object key = key(i);
                        Object val = value(i);
                        cache.put(key, val);
                        if ((!(isClientMode())) && (((cacheMode()) == (CacheMode.REPLICATED)) || (affinity.isPrimary(locNode, key))))
                            map.put(key, val);

                    }
                    registerEventListeners(map);
                    QueryCursor<Entry<Object, Object>> q = cache.query(new ScanQuery().setLocal(true));
                    checkQueryResults(map, q);
                } finally {
                    stopListeners();
                }
            }
        });
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testScanQueryLocalFilter() throws Exception {
        runInAllDataModes(new TestRunnable() {
            @Override
            public void run() throws Exception {
                try {
                    IgniteCache<Object, Object> cache = jcache();
                    ClusterNode locNode = testedGrid().cluster().localNode();
                    Map<Object, Object> map = new HashMap<>();
                    IgniteBiPredicate<Object, Object> filter = new IgniteBiPredicate<Object, Object>() {
                        @Override
                        public boolean apply(Object k, Object v) {
                            assertNotNull(k);
                            assertNotNull(v);
                            return ((valueOf(k)) >= 20) && ((valueOf(v)) < 40);
                        }
                    };
                    for (int i = 0; i < (IgniteCacheConfigVariationsQueryTest.CNT); i++) {
                        Object key = key(i);
                        Object val = value(i);
                        cache.put(key, val);
                        if (((!(isClientMode())) && (((cacheMode()) == (CacheMode.REPLICATED)) || (testedGrid().affinity(cacheName()).isPrimary(locNode, key)))) && (filter.apply(key, val)))
                            map.put(key, val);

                    }
                    registerEventListeners(map, true);
                    QueryCursor<Entry<Object, Object>> q = cache.query(new ScanQuery(filter).setLocal(true));
                    checkQueryResults(map, q);
                } finally {
                    stopListeners();
                }
            }
        });
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testScanQueryPartitionFilter() throws Exception {
        runInAllDataModes(new TestRunnable() {
            @Override
            public void run() throws Exception {
                IgniteCache<Object, Object> cache = jcache();
                Affinity<Object> affinity = testedGrid().affinity(cacheName());
                Map<Integer, Map<Object, Object>> partMap = new HashMap<>();
                IgniteBiPredicate<Object, Object> filter = new IgniteBiPredicate<Object, Object>() {
                    @Override
                    public boolean apply(Object k, Object v) {
                        assertNotNull(k);
                        assertNotNull(v);
                        return ((valueOf(k)) >= 20) && ((valueOf(v)) < 40);
                    }
                };
                for (int i = 0; i < (IgniteCacheConfigVariationsQueryTest.CNT); i++) {
                    Object key = key(i);
                    Object val = value(i);
                    cache.put(key, val);
                    if (filter.apply(key, val)) {
                        int part = affinity.partition(key);
                        Map<Object, Object> map = partMap.get(part);
                        if (map == null)
                            partMap.put(part, (map = new HashMap<>()));

                        map.put(key, val);
                    }
                }
                for (int part = 0; part < (affinity.partitions()); part++) {
                    try {
                        Map<Object, Object> expMap = partMap.get(part);
                        expMap = (expMap == null) ? Collections.emptyMap() : expMap;
                        registerEventListeners(expMap, true);
                        QueryCursor<Entry<Object, Object>> q = cache.query(new ScanQuery(part, filter));
                        checkQueryResults(expMap, q);
                    } finally {
                        stopListeners();
                    }
                }
            }
        });
    }
}

