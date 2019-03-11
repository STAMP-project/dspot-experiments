/**
 * Copyright Terracotta, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ehcache.clustered.server.offheap;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.ehcache.clustered.ChainUtils;
import org.ehcache.clustered.common.internal.store.Chain;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.terracotta.offheapstore.ReadWriteLockedOffHeapClockCache;
import org.terracotta.offheapstore.buffersource.OffHeapBufferSource;
import org.terracotta.offheapstore.paging.UnlimitedPageSource;
import org.terracotta.offheapstore.paging.UpfrontAllocatingPageSource;
import org.terracotta.offheapstore.storage.portability.StringPortability;
import org.terracotta.offheapstore.util.MemoryUnit;


// To replace by @SafeVarargs in JDK7
@RunWith(Parameterized.class)
@SuppressWarnings("unchecked")
public class ChainMapTest {
    @Parameterized.Parameter(0)
    public boolean steal;

    @Parameterized.Parameter(1)
    public int minPageSize;

    @Parameterized.Parameter(2)
    public int maxPageSize;

    @Test
    public void testInitiallyEmptyChain() {
        OffHeapChainMap<String> map = new OffHeapChainMap(new UnlimitedPageSource(new OffHeapBufferSource()), StringPortability.INSTANCE, minPageSize, maxPageSize, steal);
        Assert.assertThat(map.get("foo"), emptyIterable());
    }

    @Test
    public void testAppendToEmptyChain() {
        OffHeapChainMap<String> map = new OffHeapChainMap(new UnlimitedPageSource(new OffHeapBufferSource()), StringPortability.INSTANCE, minPageSize, maxPageSize, steal);
        map.append("foo", ChainMapTest.buffer(1));
        Assert.assertThat(map.get("foo"), contains(ChainMapTest.element(1)));
    }

    @Test
    public void testGetAndAppendToEmptyChain() {
        OffHeapChainMap<String> map = new OffHeapChainMap(new UnlimitedPageSource(new OffHeapBufferSource()), StringPortability.INSTANCE, minPageSize, maxPageSize, steal);
        Assert.assertThat(map.getAndAppend("foo", ChainMapTest.buffer(1)), emptyIterable());
        Assert.assertThat(map.get("foo"), contains(ChainMapTest.element(1)));
    }

    @Test
    public void testAppendToSingletonChain() {
        OffHeapChainMap<String> map = new OffHeapChainMap(new UnlimitedPageSource(new OffHeapBufferSource()), StringPortability.INSTANCE, minPageSize, maxPageSize, steal);
        map.append("foo", ChainMapTest.buffer(1));
        map.append("foo", ChainMapTest.buffer(2));
        Assert.assertThat(map.get("foo"), contains(ChainMapTest.element(1), ChainMapTest.element(2)));
    }

    @Test
    public void testGetAndAppendToSingletonChain() {
        OffHeapChainMap<String> map = new OffHeapChainMap(new UnlimitedPageSource(new OffHeapBufferSource()), StringPortability.INSTANCE, minPageSize, maxPageSize, steal);
        map.append("foo", ChainMapTest.buffer(1));
        Assert.assertThat(map.getAndAppend("foo", ChainMapTest.buffer(2)), contains(ChainMapTest.element(1)));
        Assert.assertThat(map.get("foo"), contains(ChainMapTest.element(1), ChainMapTest.element(2)));
    }

    @Test
    public void testAppendToDoubleChain() {
        OffHeapChainMap<String> map = new OffHeapChainMap(new UnlimitedPageSource(new OffHeapBufferSource()), StringPortability.INSTANCE, minPageSize, maxPageSize, steal);
        map.append("foo", ChainMapTest.buffer(1));
        map.append("foo", ChainMapTest.buffer(2));
        map.append("foo", ChainMapTest.buffer(3));
        Assert.assertThat(map.get("foo"), contains(ChainMapTest.element(1), ChainMapTest.element(2), ChainMapTest.element(3)));
    }

    @Test
    public void testGetAndAppendToDoubleChain() {
        OffHeapChainMap<String> map = new OffHeapChainMap(new UnlimitedPageSource(new OffHeapBufferSource()), StringPortability.INSTANCE, minPageSize, maxPageSize, steal);
        map.append("foo", ChainMapTest.buffer(1));
        map.append("foo", ChainMapTest.buffer(2));
        Assert.assertThat(map.getAndAppend("foo", ChainMapTest.buffer(3)), contains(ChainMapTest.element(1), ChainMapTest.element(2)));
        Assert.assertThat(map.get("foo"), contains(ChainMapTest.element(1), ChainMapTest.element(2), ChainMapTest.element(3)));
    }

    @Test
    public void testAppendToTripleChain() {
        OffHeapChainMap<String> map = new OffHeapChainMap(new UnlimitedPageSource(new OffHeapBufferSource()), StringPortability.INSTANCE, minPageSize, maxPageSize, steal);
        map.append("foo", ChainMapTest.buffer(1));
        map.append("foo", ChainMapTest.buffer(2));
        map.append("foo", ChainMapTest.buffer(3));
        map.append("foo", ChainMapTest.buffer(4));
        Assert.assertThat(map.get("foo"), contains(ChainMapTest.element(1), ChainMapTest.element(2), ChainMapTest.element(3), ChainMapTest.element(4)));
    }

    @Test
    public void testGetAndAppendToTripleChain() {
        OffHeapChainMap<String> map = new OffHeapChainMap(new UnlimitedPageSource(new OffHeapBufferSource()), StringPortability.INSTANCE, minPageSize, maxPageSize, steal);
        map.append("foo", ChainMapTest.buffer(1));
        map.append("foo", ChainMapTest.buffer(2));
        map.append("foo", ChainMapTest.buffer(3));
        Assert.assertThat(map.getAndAppend("foo", ChainMapTest.buffer(4)), contains(ChainMapTest.element(1), ChainMapTest.element(2), ChainMapTest.element(3)));
        Assert.assertThat(map.get("foo"), contains(ChainMapTest.element(1), ChainMapTest.element(2), ChainMapTest.element(3), ChainMapTest.element(4)));
    }

    @Test
    public void testReplaceEmptyChainAtHeadOnEmptyChainFails() {
        OffHeapChainMap<String> map = new OffHeapChainMap(new UnlimitedPageSource(new OffHeapBufferSource()), StringPortability.INSTANCE, minPageSize, maxPageSize, steal);
        try {
            map.replaceAtHead("foo", ChainUtils.chainOf(), ChainUtils.chainOf(ChainMapTest.buffer(1)));
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void testReplaceEmptyChainAtHeadOnNonEmptyChain() {
        OffHeapChainMap<String> map = new OffHeapChainMap(new UnlimitedPageSource(new OffHeapBufferSource()), StringPortability.INSTANCE, minPageSize, maxPageSize, steal);
        map.append("foo", ChainMapTest.buffer(1));
        try {
            map.replaceAtHead("foo", ChainUtils.chainOf(), ChainUtils.chainOf(ChainMapTest.buffer(2)));
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void testMismatchingReplaceSingletonChainAtHeadOnSingletonChain() {
        OffHeapChainMap<String> map = new OffHeapChainMap(new UnlimitedPageSource(new OffHeapBufferSource()), StringPortability.INSTANCE, minPageSize, maxPageSize, steal);
        map.append("foo", ChainMapTest.buffer(1));
        map.replaceAtHead("foo", ChainUtils.chainOf(ChainMapTest.buffer(2)), ChainUtils.chainOf(ChainMapTest.buffer(42)));
        Assert.assertThat(map.get("foo"), contains(ChainMapTest.element(1)));
    }

    @Test
    public void testReplaceSingletonChainAtHeadOnSingletonChain() {
        OffHeapChainMap<String> map = new OffHeapChainMap(new UnlimitedPageSource(new OffHeapBufferSource()), StringPortability.INSTANCE, minPageSize, maxPageSize, steal);
        map.append("foo", ChainMapTest.buffer(1));
        map.replaceAtHead("foo", ChainUtils.chainOf(ChainMapTest.buffer(1)), ChainUtils.chainOf(ChainMapTest.buffer(42)));
        Assert.assertThat(map.get("foo"), contains(ChainMapTest.element(42)));
    }

    @Test
    public void testReplaceSingletonChainAtHeadOnDoubleChain() {
        OffHeapChainMap<String> map = new OffHeapChainMap(new UnlimitedPageSource(new OffHeapBufferSource()), StringPortability.INSTANCE, minPageSize, maxPageSize, steal);
        map.append("foo", ChainMapTest.buffer(1));
        map.append("foo", ChainMapTest.buffer(2));
        map.replaceAtHead("foo", ChainUtils.chainOf(ChainMapTest.buffer(1)), ChainUtils.chainOf(ChainMapTest.buffer(42)));
        Assert.assertThat(map.get("foo"), contains(ChainMapTest.element(42), ChainMapTest.element(2)));
    }

    @Test
    public void testReplaceSingletonChainAtHeadOnTripleChain() {
        OffHeapChainMap<String> map = new OffHeapChainMap(new UnlimitedPageSource(new OffHeapBufferSource()), StringPortability.INSTANCE, minPageSize, maxPageSize, steal);
        map.append("foo", ChainMapTest.buffer(1));
        map.append("foo", ChainMapTest.buffer(2));
        map.append("foo", ChainMapTest.buffer(3));
        map.replaceAtHead("foo", ChainUtils.chainOf(ChainMapTest.buffer(1)), ChainUtils.chainOf(ChainMapTest.buffer(42)));
        Assert.assertThat(map.get("foo"), contains(ChainMapTest.element(42), ChainMapTest.element(2), ChainMapTest.element(3)));
    }

    @Test
    public void testMismatchingReplacePluralChainAtHead() {
        OffHeapChainMap<String> map = new OffHeapChainMap(new UnlimitedPageSource(new OffHeapBufferSource()), StringPortability.INSTANCE, minPageSize, maxPageSize, steal);
        map.append("foo", ChainMapTest.buffer(1));
        map.append("foo", ChainMapTest.buffer(2));
        map.replaceAtHead("foo", ChainUtils.chainOf(ChainMapTest.buffer(1), ChainMapTest.buffer(3)), ChainUtils.chainOf(ChainMapTest.buffer(42)));
        Assert.assertThat(map.get("foo"), contains(ChainMapTest.element(1), ChainMapTest.element(2)));
    }

    @Test
    public void testReplacePluralChainAtHeadOnDoubleChain() {
        OffHeapChainMap<String> map = new OffHeapChainMap(new UnlimitedPageSource(new OffHeapBufferSource()), StringPortability.INSTANCE, minPageSize, maxPageSize, steal);
        map.append("foo", ChainMapTest.buffer(1));
        map.append("foo", ChainMapTest.buffer(2));
        map.replaceAtHead("foo", ChainUtils.chainOf(ChainMapTest.buffer(1), ChainMapTest.buffer(2)), ChainUtils.chainOf(ChainMapTest.buffer(42)));
        Assert.assertThat(map.get("foo"), contains(ChainMapTest.element(42)));
    }

    @Test
    public void testReplacePluralChainAtHeadOnTripleChain() {
        OffHeapChainMap<String> map = new OffHeapChainMap(new UnlimitedPageSource(new OffHeapBufferSource()), StringPortability.INSTANCE, minPageSize, maxPageSize, steal);
        map.append("foo", ChainMapTest.buffer(1));
        map.append("foo", ChainMapTest.buffer(2));
        map.append("foo", ChainMapTest.buffer(3));
        map.replaceAtHead("foo", ChainUtils.chainOf(ChainMapTest.buffer(1), ChainMapTest.buffer(2)), ChainUtils.chainOf(ChainMapTest.buffer(42)));
        Assert.assertThat(map.get("foo"), contains(ChainMapTest.element(42), ChainMapTest.element(3)));
    }

    @Test
    public void testReplacePluralChainAtHeadWithEmpty() {
        OffHeapChainMap<String> map = new OffHeapChainMap(new UnlimitedPageSource(new OffHeapBufferSource()), StringPortability.INSTANCE, minPageSize, maxPageSize, steal);
        map.append("foo", ChainMapTest.buffer(1));
        map.append("foo", ChainMapTest.buffer(2));
        map.append("foo", ChainMapTest.buffer(3));
        long before = map.getDataOccupiedMemory();
        map.replaceAtHead("foo", ChainUtils.chainOf(ChainMapTest.buffer(1), ChainMapTest.buffer(2)), ChainUtils.chainOf());
        Assert.assertThat(map.getDataOccupiedMemory(), Matchers.lessThan(before));
        Assert.assertThat(map.get("foo"), contains(ChainMapTest.element(3)));
    }

    @Test
    public void testSequenceBasedChainComparison() {
        OffHeapChainMap<String> map = new OffHeapChainMap(new UnlimitedPageSource(new OffHeapBufferSource()), StringPortability.INSTANCE, minPageSize, maxPageSize, steal);
        map.append("foo", ChainMapTest.buffer(1));
        map.append("foo", ChainMapTest.buffer(2));
        map.append("foo", ChainMapTest.buffer(3));
        map.replaceAtHead("foo", map.get("foo"), ChainUtils.chainOf());
        Assert.assertThat(map.get("foo"), emptyIterable());
    }

    @Test
    public void testReplaceFullPluralChainAtHeadWithEmpty() {
        OffHeapChainMap<String> map = new OffHeapChainMap(new UnlimitedPageSource(new OffHeapBufferSource()), StringPortability.INSTANCE, minPageSize, maxPageSize, steal);
        map.append("foo", ChainMapTest.buffer(1));
        map.append("foo", ChainMapTest.buffer(2));
        map.append("foo", ChainMapTest.buffer(3));
        Assert.assertThat(map.getDataOccupiedMemory(), Matchers.greaterThan(0L));
        map.replaceAtHead("foo", ChainUtils.chainOf(ChainMapTest.buffer(1), ChainMapTest.buffer(2), ChainMapTest.buffer(3)), ChainUtils.chainOf());
        Assert.assertThat(map.getDataOccupiedMemory(), Is.is(0L));
        Assert.assertThat(map.get("foo"), emptyIterable());
    }

    @Test
    public void testContinualAppendCausingEvictionIsStable() {
        UpfrontAllocatingPageSource pageSource = new UpfrontAllocatingPageSource(new OffHeapBufferSource(), MemoryUnit.KILOBYTES.toBytes(1024L), MemoryUnit.KILOBYTES.toBytes(1024));
        if (steal) {
            OffHeapChainMap<String> mapA = new OffHeapChainMap(pageSource, StringPortability.INSTANCE, minPageSize, maxPageSize, true);
            OffHeapChainMap<String> mapB = new OffHeapChainMap(pageSource, StringPortability.INSTANCE, minPageSize, maxPageSize, true);
            for (int c = 0; ; c++) {
                long before = mapA.getOccupiedMemory();
                for (int i = 0; i < 100; i++) {
                    mapA.append(Integer.toString(i), ChainMapTest.buffer(2));
                    mapB.append(Integer.toString(i), ChainMapTest.buffer(2));
                }
                if ((mapA.getOccupiedMemory()) <= before) {
                    while ((c--) > 0) {
                        for (int i = 0; i < 100; i++) {
                            mapA.append(Integer.toString(i), ChainMapTest.buffer(2));
                            mapB.append(Integer.toString(i), ChainMapTest.buffer(2));
                        }
                    } 
                    break;
                }
            }
        } else {
            OffHeapChainMap<String> map = new OffHeapChainMap(pageSource, StringPortability.INSTANCE, minPageSize, maxPageSize, false);
            for (int c = 0; ; c++) {
                long before = map.getOccupiedMemory();
                for (int i = 0; i < 100; i++) {
                    map.append(Integer.toString(i), ChainMapTest.buffer(2));
                }
                if ((map.getOccupiedMemory()) <= before) {
                    while ((c--) > 0) {
                        for (int i = 0; i < 100; i++) {
                            map.append(Integer.toString(i), ChainMapTest.buffer(2));
                        }
                    } 
                    break;
                }
            }
        }
    }

    @Test
    public void testPutWhenKeyIsNotNull() {
        OffHeapChainMap<String> map = new OffHeapChainMap(new UnlimitedPageSource(new OffHeapBufferSource()), StringPortability.INSTANCE, minPageSize, maxPageSize, steal);
        map.append("key", ChainMapTest.buffer(3));
        map.put("key", ChainUtils.chainOf(ChainMapTest.buffer(1), ChainMapTest.buffer(2)));
        Assert.assertThat(map.get("key"), contains(ChainMapTest.element(1), ChainMapTest.element(2)));
    }

    @Test
    public void testPutWhenKeyIsNull() {
        OffHeapChainMap<String> map = new OffHeapChainMap(new UnlimitedPageSource(new OffHeapBufferSource()), StringPortability.INSTANCE, minPageSize, maxPageSize, steal);
        map.put("key", ChainUtils.chainOf(ChainMapTest.buffer(1), ChainMapTest.buffer(2)));
        Assert.assertThat(map.get("key"), contains(ChainMapTest.element(1), ChainMapTest.element(2)));
    }

    @Test
    public void testActiveChainsThreadSafety() throws InterruptedException, ExecutionException {
        UnlimitedPageSource source = new UnlimitedPageSource(new OffHeapBufferSource());
        OffHeapChainStorageEngine<String> chainStorage = new OffHeapChainStorageEngine(source, StringPortability.INSTANCE, minPageSize, maxPageSize, steal, steal);
        ReadWriteLockedOffHeapClockCache<String, InternalChain> heads = new org.terracotta.offheapstore.eviction.EvictionListeningReadWriteLockedOffHeapClockCache(( callable) -> {
        }, source, chainStorage);
        OffHeapChainMap<String> map = new OffHeapChainMap(heads, chainStorage);
        map.put("key", ChainUtils.chainOf(ChainMapTest.buffer(1), ChainMapTest.buffer(2)));
        int nThreads = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(nThreads);
        List<Future<Chain>> futures = new ArrayList<>();
        for (int i = 0; i < nThreads; i++) {
            futures.add(executorService.submit(() -> map.get("key")));
        }
        for (Future<Chain> f : futures) {
            f.get();
        }
        Assert.assertThat(chainStorage.getActiveChains().size(), Is.is(0));
    }

    @Test
    public void testPutDoesNotLeakWhenMappingIsNotNull() {
        UnlimitedPageSource source = new UnlimitedPageSource(new OffHeapBufferSource());
        OffHeapChainStorageEngine<String> chainStorage = new OffHeapChainStorageEngine(source, StringPortability.INSTANCE, minPageSize, maxPageSize, steal, steal);
        ReadWriteLockedOffHeapClockCache<String, InternalChain> heads = new org.terracotta.offheapstore.eviction.EvictionListeningReadWriteLockedOffHeapClockCache(( callable) -> {
        }, source, chainStorage);
        OffHeapChainMap<String> map = new OffHeapChainMap(heads, chainStorage);
        map.put("key", ChainUtils.chainOf(ChainMapTest.buffer(1)));
        map.put("key", ChainUtils.chainOf(ChainMapTest.buffer(2)));
        Assert.assertThat(chainStorage.getActiveChains().size(), Is.is(0));
    }

    @Test
    public void testRemoveMissingKey() {
        OffHeapChainMap<String> map = new OffHeapChainMap(new UnlimitedPageSource(new OffHeapBufferSource()), StringPortability.INSTANCE, minPageSize, maxPageSize, steal);
        map.remove("foo");
        Assert.assertThat(map.get("foo").isEmpty(), Is.is(true));
    }

    @Test
    public void testRemoveSingleChain() {
        OffHeapChainMap<String> map = new OffHeapChainMap(new UnlimitedPageSource(new OffHeapBufferSource()), StringPortability.INSTANCE, minPageSize, maxPageSize, steal);
        map.append("foo", ChainMapTest.buffer(1));
        map.append("bar", ChainMapTest.buffer(2));
        Assert.assertThat(map.get("foo"), contains(ChainMapTest.element(1)));
        Assert.assertThat(map.get("bar"), contains(ChainMapTest.element(2)));
        map.remove("foo");
        Assert.assertThat(map.get("foo").isEmpty(), Is.is(true));
        Assert.assertThat(map.get("bar"), contains(ChainMapTest.element(2)));
    }

    @Test
    public void testRemoveDoubleChain() {
        OffHeapChainMap<String> map = new OffHeapChainMap(new UnlimitedPageSource(new OffHeapBufferSource()), StringPortability.INSTANCE, minPageSize, maxPageSize, steal);
        map.append("foo", ChainMapTest.buffer(1));
        map.append("foo", ChainMapTest.buffer(2));
        Assert.assertThat(map.get("foo"), contains(ChainMapTest.element(1), ChainMapTest.element(2)));
        map.remove("foo");
        Assert.assertThat(map.get("foo").isEmpty(), Is.is(true));
    }
}

