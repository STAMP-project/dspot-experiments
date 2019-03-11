/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.druid.client.cache;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import org.apache.druid.java.util.common.concurrent.Execs;
import org.junit.Assert;
import org.junit.Test;


public class CachePopulatorTest {
    private final ExecutorService exec = Execs.multiThreaded(2, "cache-populator-test-%d");

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Cache cache = new MapCache(new ByteCountingLRUMap(Long.MAX_VALUE));

    private final CachePopulatorStats stats = new CachePopulatorStats();

    @Test
    public void testForegroundPopulator() {
        final CachePopulator populator = new ForegroundCachePopulator(objectMapper, stats, (-1));
        final List<String> strings = ImmutableList.of("foo", "bar");
        Assert.assertEquals(strings, wrapAndReturn(populator, CachePopulatorTest.makeKey(1), strings));
        Assert.assertEquals(strings, readFromCache(CachePopulatorTest.makeKey(1)));
        Assert.assertEquals(1, stats.snapshot().getNumOk());
        Assert.assertEquals(0, stats.snapshot().getNumError());
        Assert.assertEquals(0, stats.snapshot().getNumOversized());
    }

    @Test
    public void testForegroundPopulatorMaxEntrySize() {
        final CachePopulator populator = new ForegroundCachePopulator(objectMapper, stats, 30);
        final List<String> strings = ImmutableList.of("foo", "bar");
        final List<String> strings2 = ImmutableList.of("foo", "baralararararararaarararararaa");
        Assert.assertEquals(strings, wrapAndReturn(populator, CachePopulatorTest.makeKey(1), strings));
        Assert.assertEquals(strings, readFromCache(CachePopulatorTest.makeKey(1)));
        Assert.assertEquals(strings2, wrapAndReturn(populator, CachePopulatorTest.makeKey(2), strings2));
        Assert.assertNull(readFromCache(CachePopulatorTest.makeKey(2)));
        Assert.assertEquals(1, stats.snapshot().getNumOk());
        Assert.assertEquals(0, stats.snapshot().getNumError());
        Assert.assertEquals(1, stats.snapshot().getNumOversized());
    }

    @Test(timeout = 60000L)
    public void testBackgroundPopulator() throws InterruptedException {
        final CachePopulator populator = new BackgroundCachePopulator(exec, objectMapper, stats, (-1));
        final List<String> strings = ImmutableList.of("foo", "bar");
        Assert.assertEquals(strings, wrapAndReturn(populator, CachePopulatorTest.makeKey(1), strings));
        // Wait for background updates to happen.
        while ((cache.getStats().getNumEntries()) < 1) {
            Thread.sleep(100);
        } 
        Assert.assertEquals(strings, readFromCache(CachePopulatorTest.makeKey(1)));
        Assert.assertEquals(1, stats.snapshot().getNumOk());
        Assert.assertEquals(0, stats.snapshot().getNumError());
        Assert.assertEquals(0, stats.snapshot().getNumOversized());
    }

    @Test(timeout = 60000L)
    public void testBackgroundPopulatorMaxEntrySize() throws InterruptedException {
        final CachePopulator populator = new BackgroundCachePopulator(exec, objectMapper, stats, 30);
        final List<String> strings = ImmutableList.of("foo", "bar");
        final List<String> strings2 = ImmutableList.of("foo", "baralararararararaarararararaa");
        Assert.assertEquals(strings, wrapAndReturn(populator, CachePopulatorTest.makeKey(1), strings));
        Assert.assertEquals(strings2, wrapAndReturn(populator, CachePopulatorTest.makeKey(2), strings2));
        // Wait for background updates to happen.
        while (((cache.getStats().getNumEntries()) < 1) || ((stats.snapshot().getNumOversized()) < 1)) {
            Thread.sleep(100);
        } 
        Assert.assertEquals(strings, readFromCache(CachePopulatorTest.makeKey(1)));
        Assert.assertNull(readFromCache(CachePopulatorTest.makeKey(2)));
        Assert.assertEquals(1, stats.snapshot().getNumOk());
        Assert.assertEquals(0, stats.snapshot().getNumError());
        Assert.assertEquals(1, stats.snapshot().getNumOversized());
    }
}

