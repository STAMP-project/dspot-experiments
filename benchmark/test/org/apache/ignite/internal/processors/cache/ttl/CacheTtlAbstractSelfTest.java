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
package org.apache.ignite.internal.processors.cache.ttl;


import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.cache.expiry.Duration;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteDataStreamer;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Test;


/**
 * TTL test.
 */
public abstract class CacheTtlAbstractSelfTest extends GridCommonAbstractTest {
    /**
     *
     */
    private static final int MAX_CACHE_SIZE = 5;

    /**
     *
     */
    private static final int SIZE = 11;

    /**
     *
     */
    private static final long DEFAULT_TIME_TO_LIVE = 2000;

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testDefaultTimeToLiveLoadCache() throws Exception {
        IgniteCache<Integer, Integer> cache = jcache(0);
        cache.loadCache(null);
        checkSizeBeforeLive(CacheTtlAbstractSelfTest.SIZE);
        Thread.sleep(((CacheTtlAbstractSelfTest.DEFAULT_TIME_TO_LIVE) + 500));
        checkSizeAfterLive();
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testDefaultTimeToLiveLoadAll() throws Exception {
        defaultTimeToLiveLoadAll(false);
        defaultTimeToLiveLoadAll(true);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testDefaultTimeToLiveStreamerAdd() throws Exception {
        try (IgniteDataStreamer<Integer, Integer> streamer = ignite(0).dataStreamer(DEFAULT_CACHE_NAME)) {
            for (int i = 0; i < (CacheTtlAbstractSelfTest.SIZE); i++)
                streamer.addData(i, i);

        }
        checkSizeBeforeLive(CacheTtlAbstractSelfTest.SIZE);
        Thread.sleep(((CacheTtlAbstractSelfTest.DEFAULT_TIME_TO_LIVE) + 500));
        checkSizeAfterLive();
        try (IgniteDataStreamer<Integer, Integer> streamer = ignite(0).dataStreamer(DEFAULT_CACHE_NAME)) {
            streamer.allowOverwrite(true);
            for (int i = 0; i < (CacheTtlAbstractSelfTest.SIZE); i++)
                streamer.addData(i, i);

        }
        checkSizeBeforeLive(CacheTtlAbstractSelfTest.SIZE);
        Thread.sleep(((CacheTtlAbstractSelfTest.DEFAULT_TIME_TO_LIVE) + 500));
        checkSizeAfterLive();
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testDefaultTimeToLivePut() throws Exception {
        IgniteCache<Integer, Integer> cache = jcache(0);
        Integer key = 0;
        cache.put(key, 1);
        checkSizeBeforeLive(1);
        Thread.sleep(((CacheTtlAbstractSelfTest.DEFAULT_TIME_TO_LIVE) + 500));
        checkSizeAfterLive();
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testDefaultTimeToLivePutAll() throws Exception {
        IgniteCache<Integer, Integer> cache = jcache(0);
        Map<Integer, Integer> entries = new HashMap<>();
        for (int i = 0; i < (CacheTtlAbstractSelfTest.SIZE); ++i)
            entries.put(i, i);

        cache.putAll(entries);
        checkSizeBeforeLive(CacheTtlAbstractSelfTest.SIZE);
        Thread.sleep(((CacheTtlAbstractSelfTest.DEFAULT_TIME_TO_LIVE) + 500));
        checkSizeAfterLive();
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testDefaultTimeToLivePreload() throws Exception {
        if ((cacheMode()) == (CacheMode.LOCAL))
            return;

        IgniteCache<Integer, Integer> cache = jcache(0);
        Map<Integer, Integer> entries = new HashMap<>();
        for (int i = 0; i < (CacheTtlAbstractSelfTest.SIZE); ++i)
            entries.put(i, i);

        cache.putAll(entries);
        startGrid(gridCount());
        checkSizeBeforeLive(CacheTtlAbstractSelfTest.SIZE, ((gridCount()) + 1));
        Thread.sleep(((CacheTtlAbstractSelfTest.DEFAULT_TIME_TO_LIVE) + 500));
        checkSizeAfterLive(((gridCount()) + 1));
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testTimeToLiveTtl() throws Exception {
        long time = (CacheTtlAbstractSelfTest.DEFAULT_TIME_TO_LIVE) + 2000;
        IgniteCache<Integer, Integer> cache = this.<Integer, Integer>jcache(0).withExpiryPolicy(new javax.cache.expiry.TouchedExpiryPolicy(new Duration(TimeUnit.MILLISECONDS, time)));
        for (int i = 0; i < (CacheTtlAbstractSelfTest.SIZE); i++)
            cache.put(i, i);

        checkSizeBeforeLive(CacheTtlAbstractSelfTest.SIZE);
        Thread.sleep(((CacheTtlAbstractSelfTest.DEFAULT_TIME_TO_LIVE) + 500));
        checkSizeBeforeLive(CacheTtlAbstractSelfTest.SIZE);
        Thread.sleep(((time - (CacheTtlAbstractSelfTest.DEFAULT_TIME_TO_LIVE)) + 500));
        checkSizeAfterLive();
    }
}

