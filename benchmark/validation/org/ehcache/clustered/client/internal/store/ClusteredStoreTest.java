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
package org.ehcache.clustered.client.internal.store;


import java.net.URI;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;
import org.ehcache.Cache;
import org.ehcache.clustered.client.TestTimeSource;
import org.ehcache.clustered.client.internal.store.operations.EternalChainResolver;
import org.ehcache.clustered.common.internal.store.Chain;
import org.ehcache.clustered.common.internal.store.operations.Result;
import org.ehcache.clustered.common.internal.store.operations.codecs.OperationsCodec;
import org.ehcache.clustered.util.StatisticsTestUtils;
import org.ehcache.config.EvictionAdvisor;
import org.ehcache.config.ResourcePools;
import org.ehcache.core.Ehcache;
import org.ehcache.core.spi.store.Store;
import org.ehcache.core.spi.time.TimeSource;
import org.ehcache.core.statistics.StoreOperationOutcomes;
import org.ehcache.expiry.ExpiryPolicy;
import org.ehcache.impl.serialization.LongSerializer;
import org.ehcache.impl.serialization.StringSerializer;
import org.ehcache.impl.store.HashUtils;
import org.ehcache.spi.loaderwriter.CacheLoaderWriter;
import org.ehcache.spi.resilience.StoreAccessException;
import org.ehcache.spi.serialization.Serializer;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.hamcrest.core.CombinableMatcher;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import static org.ehcache.core.spi.store.Store.PutStatus.PUT;
import static org.ehcache.core.spi.store.Store.RemoveStatus.KEY_MISSING;
import static org.ehcache.core.spi.store.Store.RemoveStatus.KEY_PRESENT;
import static org.ehcache.core.spi.store.Store.ReplaceStatus.MISS_NOT_PRESENT;
import static org.ehcache.core.spi.store.Store.ReplaceStatus.MISS_PRESENT;
import static org.ehcache.core.spi.store.Store.ValueHolder.NO_EXPIRE;
import static org.ehcache.core.statistics.StoreOperationOutcomes.GetOutcome.HIT;
import static org.ehcache.core.statistics.StoreOperationOutcomes.GetOutcome.MISS;
import static org.ehcache.core.statistics.StoreOperationOutcomes.GetOutcome.TIMEOUT;
import static org.ehcache.core.statistics.StoreOperationOutcomes.RemoveOutcome.REMOVED;
import static org.ehcache.core.statistics.StoreOperationOutcomes.ReplaceOutcome.REPLACED;


public class ClusteredStoreTest {
    private static final String CACHE_IDENTIFIER = "testCache";

    private static final URI CLUSTER_URI = URI.create("terracotta://localhost");

    private ClusteredStore<Long, String> store;

    private final Store.Configuration<Long, String> config = new Store.Configuration<Long, String>() {
        @Override
        public Class<Long> getKeyType() {
            return Long.class;
        }

        @Override
        public Class<String> getValueType() {
            return String.class;
        }

        @Override
        public EvictionAdvisor<? super Long, ? super String> getEvictionAdvisor() {
            return null;
        }

        @Override
        public ClassLoader getClassLoader() {
            return null;
        }

        @Override
        public ExpiryPolicy<? super Long, ? super String> getExpiry() {
            return null;
        }

        @Override
        public ResourcePools getResourcePools() {
            return null;
        }

        @Override
        public Serializer<Long> getKeySerializer() {
            return null;
        }

        @Override
        public Serializer<String> getValueSerializer() {
            return null;
        }

        @Override
        public int getDispatcherConcurrency() {
            return 0;
        }

        @Override
        public CacheLoaderWriter<? super Long, String> getCacheLoaderWriter() {
            return null;
        }
    };

    @Test
    public void testPut() throws Exception {
        Assert.assertThat(store.put(1L, "one"), CoreMatchers.is(PUT));
        StatisticsTestUtils.validateStats(store, EnumSet.of(StoreOperationOutcomes.PutOutcome.PUT));
        Assert.assertThat(store.put(1L, "another one"), CoreMatchers.is(PUT));
        Assert.assertThat(store.put(1L, "yet another one"), CoreMatchers.is(PUT));
        StatisticsTestUtils.validateStat(store, StoreOperationOutcomes.PutOutcome.PUT, 3);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testPutTimeout() throws Exception {
        ServerStoreProxy proxy = Mockito.mock(ServerStoreProxy.class);
        OperationsCodec<Long, String> codec = Mockito.mock(OperationsCodec.class);
        TimeSource timeSource = Mockito.mock(TimeSource.class);
        Mockito.doThrow(TimeoutException.class).when(proxy).append(ArgumentMatchers.anyLong(), ArgumentMatchers.isNull());
        ClusteredStore<Long, String> store = new ClusteredStore(config, codec, null, proxy, timeSource);
        assertTimeoutOccurred(() -> store.put(1L, "one"));
    }

    @Test
    public void testGet() throws Exception {
        Assert.assertThat(store.get(1L), CoreMatchers.nullValue());
        StatisticsTestUtils.validateStats(store, EnumSet.of(MISS));
        store.put(1L, "one");
        Assert.assertThat(store.get(1L).get(), CoreMatchers.is("one"));
        StatisticsTestUtils.validateStats(store, EnumSet.of(MISS, HIT));
    }

    @Test(expected = StoreAccessException.class)
    public void testGetThrowsOnlySAE() throws Exception {
        @SuppressWarnings("unchecked")
        OperationsCodec<Long, String> codec = Mockito.mock(OperationsCodec.class);
        @SuppressWarnings("unchecked")
        EternalChainResolver<Long, String> chainResolver = Mockito.mock(EternalChainResolver.class);
        ServerStoreProxy serverStoreProxy = Mockito.mock(ServerStoreProxy.class);
        Mockito.when(serverStoreProxy.get(ArgumentMatchers.anyLong())).thenThrow(new RuntimeException());
        TestTimeSource testTimeSource = Mockito.mock(TestTimeSource.class);
        ClusteredStore<Long, String> store = new ClusteredStore(config, codec, chainResolver, serverStoreProxy, testTimeSource);
        store.get(1L);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetTimeout() throws Exception {
        ServerStoreProxy proxy = Mockito.mock(ServerStoreProxy.class);
        long longKey = HashUtils.intHashToLong(new Long(1L).hashCode());
        Mockito.when(proxy.get(longKey)).thenThrow(TimeoutException.class);
        ClusteredStore<Long, String> store = new ClusteredStore(config, null, null, proxy, null);
        Assert.assertThat(store.get(1L), CoreMatchers.nullValue());
        StatisticsTestUtils.validateStats(store, EnumSet.of(TIMEOUT));
    }

    @Test
    public void testGetThatCompactsInvokesReplace() throws Exception {
        TestTimeSource timeSource = new TestTimeSource();
        timeSource.advanceTime(134556L);
        long now = timeSource.getTimeMillis();
        @SuppressWarnings("unchecked")
        OperationsCodec<Long, String> operationsCodec = new OperationsCodec(new LongSerializer(), new StringSerializer());
        @SuppressWarnings("unchecked")
        EternalChainResolver<Long, String> chainResolver = Mockito.mock(EternalChainResolver.class);
        @SuppressWarnings("unchecked")
        ResolvedChain<Long, String> resolvedChain = Mockito.mock(ResolvedChain.class);
        Mockito.when(resolvedChain.isCompacted()).thenReturn(true);
        Mockito.when(chainResolver.resolve(ArgumentMatchers.any(Chain.class), ArgumentMatchers.eq(42L), ArgumentMatchers.eq(now))).thenReturn(resolvedChain);
        ServerStoreProxy serverStoreProxy = Mockito.mock(ServerStoreProxy.class);
        Chain chain = Mockito.mock(Chain.class);
        Mockito.when(chain.isEmpty()).thenReturn(false);
        long longKey = HashUtils.intHashToLong(new Long(42L).hashCode());
        Mockito.when(serverStoreProxy.get(longKey)).thenReturn(chain);
        ClusteredStore<Long, String> clusteredStore = new ClusteredStore(config, operationsCodec, chainResolver, serverStoreProxy, timeSource);
        clusteredStore.get(42L);
        Mockito.verify(serverStoreProxy).replaceAtHead(ArgumentMatchers.eq(longKey), ArgumentMatchers.eq(chain), ArgumentMatchers.isNull());
    }

    @Test
    public void testGetThatDoesNotCompactsInvokesReplace() throws Exception {
        TestTimeSource timeSource = new TestTimeSource();
        timeSource.advanceTime(134556L);
        long now = timeSource.getTimeMillis();
        OperationsCodec<Long, String> operationsCodec = new OperationsCodec(new LongSerializer(), new StringSerializer());
        @SuppressWarnings("unchecked")
        EternalChainResolver<Long, String> chainResolver = Mockito.mock(EternalChainResolver.class);
        @SuppressWarnings("unchecked")
        ResolvedChain<Long, String> resolvedChain = Mockito.mock(ResolvedChain.class);
        Mockito.when(resolvedChain.isCompacted()).thenReturn(false);
        Mockito.when(chainResolver.resolve(ArgumentMatchers.any(Chain.class), ArgumentMatchers.eq(42L), ArgumentMatchers.eq(now))).thenReturn(resolvedChain);
        ServerStoreProxy serverStoreProxy = Mockito.mock(ServerStoreProxy.class);
        Chain chain = Mockito.mock(Chain.class);
        Mockito.when(chain.isEmpty()).thenReturn(false);
        long longKey = HashUtils.intHashToLong(new Long(42L).hashCode());
        Mockito.when(serverStoreProxy.get(longKey)).thenReturn(chain);
        ClusteredStore<Long, String> clusteredStore = new ClusteredStore(config, operationsCodec, chainResolver, serverStoreProxy, timeSource);
        clusteredStore.get(42L);
        Mockito.verify(serverStoreProxy, Mockito.never()).replaceAtHead(ArgumentMatchers.eq(longKey), ArgumentMatchers.eq(chain), ArgumentMatchers.any(Chain.class));
    }

    @Test
    public void testContainsKey() throws Exception {
        Assert.assertThat(store.containsKey(1L), CoreMatchers.is(false));
        store.put(1L, "one");
        Assert.assertThat(store.containsKey(1L), CoreMatchers.is(true));
        StatisticsTestUtils.validateStat(store, HIT, 0);
        StatisticsTestUtils.validateStat(store, MISS, 0);
    }

    @Test(expected = StoreAccessException.class)
    public void testContainsKeyThrowsOnlySAE() throws Exception {
        @SuppressWarnings("unchecked")
        OperationsCodec<Long, String> codec = Mockito.mock(OperationsCodec.class);
        @SuppressWarnings("unchecked")
        EternalChainResolver<Long, String> chainResolver = Mockito.mock(EternalChainResolver.class);
        ServerStoreProxy serverStoreProxy = Mockito.mock(ServerStoreProxy.class);
        Mockito.when(serverStoreProxy.get(ArgumentMatchers.anyLong())).thenThrow(new RuntimeException());
        TestTimeSource testTimeSource = Mockito.mock(TestTimeSource.class);
        ClusteredStore<Long, String> store = new ClusteredStore(config, codec, chainResolver, serverStoreProxy, testTimeSource);
        store.containsKey(1L);
    }

    @Test
    public void testRemove() throws Exception {
        Assert.assertThat(store.remove(1L), CoreMatchers.is(false));
        StatisticsTestUtils.validateStats(store, EnumSet.of(StoreOperationOutcomes.RemoveOutcome.MISS));
        store.put(1L, "one");
        Assert.assertThat(store.remove(1L), CoreMatchers.is(true));
        Assert.assertThat(store.containsKey(1L), CoreMatchers.is(false));
        StatisticsTestUtils.validateStats(store, EnumSet.of(StoreOperationOutcomes.RemoveOutcome.MISS, REMOVED));
    }

    @Test
    public void testRemoveThrowsOnlySAE() throws Exception {
        @SuppressWarnings("unchecked")
        OperationsCodec<Long, String> codec = Mockito.mock(OperationsCodec.class);
        @SuppressWarnings("unchecked")
        EternalChainResolver<Long, String> chainResolver = Mockito.mock(EternalChainResolver.class);
        ServerStoreProxy serverStoreProxy = Mockito.mock(ServerStoreProxy.class);
        RuntimeException theException = new RuntimeException();
        Mockito.when(serverStoreProxy.getAndAppend(ArgumentMatchers.anyLong(), ArgumentMatchers.any())).thenThrow(theException);
        TestTimeSource testTimeSource = new TestTimeSource();
        ClusteredStore<Long, String> store = new ClusteredStore(config, codec, chainResolver, serverStoreProxy, testTimeSource);
        assertThatExceptionOfType(StoreAccessException.class).isThrownBy(() -> store.remove(1L)).withCause(theException);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRemoveTimeout() throws Exception {
        ServerStoreProxy proxy = Mockito.mock(ServerStoreProxy.class);
        OperationsCodec<Long, String> codec = Mockito.mock(OperationsCodec.class);
        TimeSource timeSource = Mockito.mock(TimeSource.class);
        Mockito.when(proxy.getAndAppend(ArgumentMatchers.anyLong(), ArgumentMatchers.isNull())).thenThrow(TimeoutException.class);
        ClusteredStore<Long, String> store = new ClusteredStore(config, codec, null, proxy, timeSource);
        assertTimeoutOccurred(() -> store.remove(1L));
    }

    @Test
    public void testClear() throws Exception {
        Assert.assertThat(store.containsKey(1L), CoreMatchers.is(false));
        store.clear();
        Assert.assertThat(store.containsKey(1L), CoreMatchers.is(false));
        store.put(1L, "one");
        store.put(2L, "two");
        store.put(3L, "three");
        Assert.assertThat(store.containsKey(1L), CoreMatchers.is(true));
        store.clear();
        Assert.assertThat(store.containsKey(1L), CoreMatchers.is(false));
        Assert.assertThat(store.containsKey(2L), CoreMatchers.is(false));
        Assert.assertThat(store.containsKey(3L), CoreMatchers.is(false));
    }

    @Test(expected = StoreAccessException.class)
    public void testClearThrowsOnlySAE() throws Exception {
        @SuppressWarnings("unchecked")
        OperationsCodec<Long, String> codec = Mockito.mock(OperationsCodec.class);
        @SuppressWarnings("unchecked")
        EternalChainResolver<Long, String> chainResolver = Mockito.mock(EternalChainResolver.class);
        ServerStoreProxy serverStoreProxy = Mockito.mock(ServerStoreProxy.class);
        Mockito.doThrow(new RuntimeException()).when(serverStoreProxy).clear();
        TestTimeSource testTimeSource = Mockito.mock(TestTimeSource.class);
        ClusteredStore<Long, String> store = new ClusteredStore(config, codec, chainResolver, serverStoreProxy, testTimeSource);
        store.clear();
    }

    @Test
    public void testClearTimeout() throws Exception {
        ServerStoreProxy proxy = Mockito.mock(ServerStoreProxy.class);
        @SuppressWarnings("unchecked")
        OperationsCodec<Long, String> codec = Mockito.mock(OperationsCodec.class);
        TimeSource timeSource = Mockito.mock(TimeSource.class);
        Mockito.doThrow(TimeoutException.class).when(proxy).clear();
        ClusteredStore<Long, String> store = new ClusteredStore(config, codec, null, proxy, timeSource);
        assertTimeoutOccurred(() -> store.clear());
    }

    @Test
    public void testPutIfAbsent() throws Exception {
        Assert.assertThat(store.putIfAbsent(1L, "one", ( b) -> {
        }), CoreMatchers.nullValue());
        StatisticsTestUtils.validateStats(store, EnumSet.of(StoreOperationOutcomes.PutIfAbsentOutcome.PUT));
        Assert.assertThat(store.putIfAbsent(1L, "another one", ( b) -> {
        }).get(), CoreMatchers.is("one"));
        StatisticsTestUtils.validateStats(store, EnumSet.of(StoreOperationOutcomes.PutIfAbsentOutcome.PUT, StoreOperationOutcomes.PutIfAbsentOutcome.HIT));
    }

    @Test(expected = StoreAccessException.class)
    public void testPutIfAbsentThrowsOnlySAE() throws Exception {
        @SuppressWarnings("unchecked")
        OperationsCodec<Long, String> codec = Mockito.mock(OperationsCodec.class);
        @SuppressWarnings("unchecked")
        EternalChainResolver<Long, String> chainResolver = Mockito.mock(EternalChainResolver.class);
        ServerStoreProxy serverStoreProxy = Mockito.mock(ServerStoreProxy.class);
        Mockito.when(serverStoreProxy.get(ArgumentMatchers.anyLong())).thenThrow(new RuntimeException());
        TestTimeSource testTimeSource = Mockito.mock(TestTimeSource.class);
        ClusteredStore<Long, String> store = new ClusteredStore(config, codec, chainResolver, serverStoreProxy, testTimeSource);
        store.putIfAbsent(1L, "one", ( b) -> {
        });
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testPutIfAbsentTimeout() throws Exception {
        ServerStoreProxy proxy = Mockito.mock(ServerStoreProxy.class);
        OperationsCodec<Long, String> codec = Mockito.mock(OperationsCodec.class);
        TimeSource timeSource = Mockito.mock(TimeSource.class);
        Mockito.when(proxy.getAndAppend(ArgumentMatchers.anyLong(), ArgumentMatchers.isNull())).thenThrow(TimeoutException.class);
        ClusteredStore<Long, String> store = new ClusteredStore(config, codec, null, proxy, timeSource);
        assertTimeoutOccurred(() -> store.putIfAbsent(1L, "one", ( b) -> {
        }));
    }

    @Test
    public void testConditionalRemove() throws Exception {
        Assert.assertThat(store.remove(1L, "one"), CoreMatchers.is(KEY_MISSING));
        StatisticsTestUtils.validateStats(store, EnumSet.of(StoreOperationOutcomes.ConditionalRemoveOutcome.MISS));
        store.put(1L, "one");
        Assert.assertThat(store.remove(1L, "one"), CoreMatchers.is(Store.RemoveStatus.REMOVED));
        StatisticsTestUtils.validateStats(store, EnumSet.of(StoreOperationOutcomes.ConditionalRemoveOutcome.MISS, StoreOperationOutcomes.ConditionalRemoveOutcome.REMOVED));
        store.put(1L, "another one");
        Assert.assertThat(store.remove(1L, "one"), CoreMatchers.is(KEY_PRESENT));
        StatisticsTestUtils.validateStat(store, StoreOperationOutcomes.ConditionalRemoveOutcome.MISS, 2);
    }

    @Test(expected = StoreAccessException.class)
    public void testConditionalRemoveThrowsOnlySAE() throws Exception {
        @SuppressWarnings("unchecked")
        OperationsCodec<Long, String> codec = Mockito.mock(OperationsCodec.class);
        @SuppressWarnings("unchecked")
        EternalChainResolver<Long, String> chainResolver = Mockito.mock(EternalChainResolver.class);
        ServerStoreProxy serverStoreProxy = Mockito.mock(ServerStoreProxy.class);
        Mockito.when(serverStoreProxy.get(ArgumentMatchers.anyLong())).thenThrow(new RuntimeException());
        TestTimeSource testTimeSource = Mockito.mock(TestTimeSource.class);
        ClusteredStore<Long, String> store = new ClusteredStore(config, codec, chainResolver, serverStoreProxy, testTimeSource);
        store.remove(1L, "one");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testConditionalRemoveTimeout() throws Exception {
        ServerStoreProxy proxy = Mockito.mock(ServerStoreProxy.class);
        OperationsCodec<Long, String> codec = Mockito.mock(OperationsCodec.class);
        TimeSource timeSource = Mockito.mock(TimeSource.class);
        Mockito.when(proxy.getAndAppend(ArgumentMatchers.anyLong(), ArgumentMatchers.isNull())).thenThrow(TimeoutException.class);
        ClusteredStore<Long, String> store = new ClusteredStore(config, codec, null, proxy, timeSource);
        assertTimeoutOccurred(() -> store.remove(1L, "one"));
    }

    @Test
    public void testReplace() throws Exception {
        Assert.assertThat(store.replace(1L, "one"), CoreMatchers.nullValue());
        StatisticsTestUtils.validateStats(store, EnumSet.of(StoreOperationOutcomes.ReplaceOutcome.MISS));
        store.put(1L, "one");
        Assert.assertThat(store.replace(1L, "another one").get(), CoreMatchers.is("one"));
        StatisticsTestUtils.validateStats(store, EnumSet.of(StoreOperationOutcomes.ReplaceOutcome.MISS, REPLACED));
    }

    @Test(expected = StoreAccessException.class)
    public void testReplaceThrowsOnlySAE() throws Exception {
        @SuppressWarnings("unchecked")
        OperationsCodec<Long, String> codec = Mockito.mock(OperationsCodec.class);
        @SuppressWarnings("unchecked")
        EternalChainResolver<Long, String> chainResolver = Mockito.mock(EternalChainResolver.class);
        ServerStoreProxy serverStoreProxy = Mockito.mock(ServerStoreProxy.class);
        Mockito.when(serverStoreProxy.get(ArgumentMatchers.anyLong())).thenThrow(new RuntimeException());
        TestTimeSource testTimeSource = Mockito.mock(TestTimeSource.class);
        ClusteredStore<Long, String> store = new ClusteredStore(config, codec, chainResolver, serverStoreProxy, testTimeSource);
        store.replace(1L, "one");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testReplaceTimeout() throws Exception {
        ServerStoreProxy proxy = Mockito.mock(ServerStoreProxy.class);
        OperationsCodec<Long, String> codec = Mockito.mock(OperationsCodec.class);
        TimeSource timeSource = Mockito.mock(TimeSource.class);
        Mockito.when(proxy.getAndAppend(ArgumentMatchers.anyLong(), ArgumentMatchers.isNull())).thenThrow(TimeoutException.class);
        ClusteredStore<Long, String> store = new ClusteredStore(config, codec, null, proxy, timeSource);
        assertTimeoutOccurred(() -> store.replace(1L, "one"));
    }

    @Test
    public void testConditionalReplace() throws Exception {
        Assert.assertThat(store.replace(1L, "one", "another one"), CoreMatchers.is(MISS_NOT_PRESENT));
        StatisticsTestUtils.validateStats(store, EnumSet.of(StoreOperationOutcomes.ConditionalReplaceOutcome.MISS));
        store.put(1L, "some other one");
        Assert.assertThat(store.replace(1L, "one", "another one"), CoreMatchers.is(MISS_PRESENT));
        StatisticsTestUtils.validateStat(store, StoreOperationOutcomes.ConditionalReplaceOutcome.MISS, 2);
        StatisticsTestUtils.validateStat(store, StoreOperationOutcomes.ConditionalReplaceOutcome.REPLACED, 0);
        Assert.assertThat(store.replace(1L, "some other one", "another one"), CoreMatchers.is(Store.ReplaceStatus.HIT));
        StatisticsTestUtils.validateStat(store, StoreOperationOutcomes.ConditionalReplaceOutcome.REPLACED, 1);
        StatisticsTestUtils.validateStat(store, StoreOperationOutcomes.ConditionalReplaceOutcome.MISS, 2);
    }

    @Test(expected = StoreAccessException.class)
    public void testConditionalReplaceThrowsOnlySAE() throws Exception {
        @SuppressWarnings("unchecked")
        OperationsCodec<Long, String> codec = Mockito.mock(OperationsCodec.class);
        @SuppressWarnings("unchecked")
        EternalChainResolver<Long, String> chainResolver = Mockito.mock(EternalChainResolver.class);
        ServerStoreProxy serverStoreProxy = Mockito.mock(ServerStoreProxy.class);
        Mockito.when(serverStoreProxy.get(ArgumentMatchers.anyLong())).thenThrow(new RuntimeException());
        TestTimeSource testTimeSource = Mockito.mock(TestTimeSource.class);
        ClusteredStore<Long, String> store = new ClusteredStore(config, codec, chainResolver, serverStoreProxy, testTimeSource);
        store.replace(1L, "one", "another one");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testConditionalReplaceTimeout() throws Exception {
        ServerStoreProxy proxy = Mockito.mock(ServerStoreProxy.class);
        OperationsCodec<Long, String> codec = Mockito.mock(OperationsCodec.class);
        TimeSource timeSource = Mockito.mock(TimeSource.class);
        Mockito.when(proxy.getAndAppend(ArgumentMatchers.anyLong(), ArgumentMatchers.isNull())).thenThrow(TimeoutException.class);
        ClusteredStore<Long, String> store = new ClusteredStore(config, codec, null, proxy, timeSource);
        assertTimeoutOccurred(() -> store.replace(1L, "one", "another one"));
    }

    @Test
    public void testBulkComputePutAll() throws Exception {
        store.put(1L, "another one");
        Map<Long, String> map = new HashMap<>();
        map.put(1L, "one");
        map.put(2L, "two");
        Ehcache.PutAllFunction<Long, String> putAllFunction = new Ehcache.PutAllFunction<>(null, map, null);
        Map<Long, Store.ValueHolder<String>> valueHolderMap = store.bulkCompute(new HashSet(Arrays.asList(1L, 2L)), putAllFunction);
        Assert.assertThat(valueHolderMap.get(1L).get(), CoreMatchers.is(map.get(1L)));
        Assert.assertThat(store.get(1L).get(), CoreMatchers.is(map.get(1L)));
        Assert.assertThat(valueHolderMap.get(2L).get(), CoreMatchers.is(map.get(2L)));
        Assert.assertThat(store.get(2L).get(), CoreMatchers.is(map.get(2L)));
        Assert.assertThat(putAllFunction.getActualPutCount().get(), CoreMatchers.is(2));
        StatisticsTestUtils.validateStats(store, EnumSet.of(StoreOperationOutcomes.PutOutcome.PUT));// outcome of the initial store put

    }

    @Test
    public void testBulkComputeRemoveAll() throws Exception {
        store.put(1L, "one");
        store.put(2L, "two");
        store.put(3L, "three");
        Ehcache.RemoveAllFunction<Long, String> removeAllFunction = new Ehcache.RemoveAllFunction<>();
        Map<Long, Store.ValueHolder<String>> valueHolderMap = store.bulkCompute(new HashSet(Arrays.asList(1L, 2L, 4L)), removeAllFunction);
        Assert.assertThat(valueHolderMap.get(1L), CoreMatchers.nullValue());
        Assert.assertThat(store.get(1L), CoreMatchers.nullValue());
        Assert.assertThat(valueHolderMap.get(2L), CoreMatchers.nullValue());
        Assert.assertThat(store.get(2L), CoreMatchers.nullValue());
        Assert.assertThat(valueHolderMap.get(4L), CoreMatchers.nullValue());
        Assert.assertThat(store.get(4L), CoreMatchers.nullValue());
        StatisticsTestUtils.validateStats(store, EnumSet.noneOf(StoreOperationOutcomes.RemoveOutcome.class));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testBulkComputeThrowsForGenericFunction() throws Exception {
        @SuppressWarnings("unchecked")
        Function<Iterable<? extends Map.Entry<? extends Long, ? extends String>>, Iterable<? extends Map.Entry<? extends Long, ? extends String>>> remappingFunction = Mockito.mock(Function.class);
        store.bulkCompute(new HashSet(Arrays.asList(1L, 2L)), remappingFunction);
    }

    @Test
    public void testBulkComputeIfAbsentGetAll() throws Exception {
        store.put(1L, "one");
        store.put(2L, "two");
        Ehcache.GetAllFunction<Long, String> getAllAllFunction = new Ehcache.GetAllFunction<>();
        Map<Long, Store.ValueHolder<String>> valueHolderMap = store.bulkComputeIfAbsent(new HashSet(Arrays.asList(1L, 2L)), getAllAllFunction);
        Assert.assertThat(valueHolderMap.get(1L).get(), CoreMatchers.is("one"));
        Assert.assertThat(store.get(1L).get(), CoreMatchers.is("one"));
        Assert.assertThat(valueHolderMap.get(2L).get(), CoreMatchers.is("two"));
        Assert.assertThat(store.get(2L).get(), CoreMatchers.is("two"));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testBulkComputeIfAbsentThrowsForGenericFunction() throws Exception {
        @SuppressWarnings("unchecked")
        Function<Iterable<? extends Long>, Iterable<? extends Map.Entry<? extends Long, ? extends String>>> mappingFunction = Mockito.mock(Function.class);
        store.bulkComputeIfAbsent(new HashSet(Arrays.asList(1L, 2L)), mappingFunction);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testPutIfAbsentReplacesChainOnlyOnCompressionThreshold() throws Exception {
        Result<Long, String> result = Mockito.mock(Result.class);
        Mockito.when(result.getValue()).thenReturn("one");
        ResolvedChain<Long, String> resolvedChain = Mockito.mock(ResolvedChain.class);
        Mockito.when(resolvedChain.getResolvedResult(ArgumentMatchers.anyLong())).thenReturn(result);
        Mockito.when(resolvedChain.getCompactedChain()).thenReturn(Mockito.mock(Chain.class));
        ServerStoreProxy proxy = Mockito.mock(ServerStoreProxy.class);
        Mockito.when(proxy.getAndAppend(ArgumentMatchers.anyLong(), ArgumentMatchers.any(ByteBuffer.class))).thenReturn(Mockito.mock(Chain.class));
        EternalChainResolver<Long, String> resolver = Mockito.mock(EternalChainResolver.class);
        Mockito.when(resolver.resolve(ArgumentMatchers.any(Chain.class), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong())).thenReturn(resolvedChain);
        OperationsCodec<Long, String> codec = Mockito.mock(OperationsCodec.class);
        Mockito.when(codec.encode(ArgumentMatchers.any())).thenReturn(ByteBuffer.allocate(0));
        TimeSource timeSource = Mockito.mock(TimeSource.class);
        ClusteredStore<Long, String> store = new ClusteredStore(config, codec, resolver, proxy, timeSource);
        Mockito.when(resolvedChain.getCompactionCount()).thenReturn(((ClusteredStore.DEFAULT_CHAIN_COMPACTION_THRESHOLD) - 1));// less than the default threshold

        store.putIfAbsent(1L, "one", ( b) -> {
        });
        Mockito.verify(proxy, Mockito.never()).replaceAtHead(ArgumentMatchers.anyLong(), ArgumentMatchers.any(Chain.class), ArgumentMatchers.any(Chain.class));
        Mockito.when(resolvedChain.getCompactionCount()).thenReturn(ClusteredStore.DEFAULT_CHAIN_COMPACTION_THRESHOLD);// equal to the default threshold

        store.putIfAbsent(1L, "one", ( b) -> {
        });
        Mockito.verify(proxy, Mockito.never()).replaceAtHead(ArgumentMatchers.anyLong(), ArgumentMatchers.any(Chain.class), ArgumentMatchers.any(Chain.class));
        Mockito.when(resolvedChain.getCompactionCount()).thenReturn(((ClusteredStore.DEFAULT_CHAIN_COMPACTION_THRESHOLD) + 1));// greater than the default threshold

        store.putIfAbsent(1L, "one", ( b) -> {
        });
        Mockito.verify(proxy).replaceAtHead(ArgumentMatchers.anyLong(), ArgumentMatchers.any(Chain.class), ArgumentMatchers.any(Chain.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testReplaceReplacesChainOnlyOnCompressionThreshold() throws Exception {
        Result<Long, String> result = Mockito.mock(Result.class);
        Mockito.when(result.getValue()).thenReturn("one");
        ResolvedChain<Long, String> resolvedChain = Mockito.mock(ResolvedChain.class);
        Mockito.when(resolvedChain.getResolvedResult(ArgumentMatchers.anyLong())).thenReturn(result);
        Mockito.when(resolvedChain.getCompactedChain()).thenReturn(Mockito.mock(Chain.class));
        ServerStoreProxy proxy = Mockito.mock(ServerStoreProxy.class);
        Mockito.when(proxy.getAndAppend(ArgumentMatchers.anyLong(), ArgumentMatchers.any(ByteBuffer.class))).thenReturn(Mockito.mock(Chain.class));
        EternalChainResolver<Long, String> resolver = Mockito.mock(EternalChainResolver.class);
        Mockito.when(resolver.resolve(ArgumentMatchers.any(Chain.class), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong())).thenReturn(resolvedChain);
        OperationsCodec<Long, String> codec = Mockito.mock(OperationsCodec.class);
        Mockito.when(codec.encode(ArgumentMatchers.any())).thenReturn(ByteBuffer.allocate(0));
        TimeSource timeSource = Mockito.mock(TimeSource.class);
        ClusteredStore<Long, String> store = new ClusteredStore(config, codec, resolver, proxy, timeSource);
        Mockito.when(resolvedChain.getCompactionCount()).thenReturn(((ClusteredStore.DEFAULT_CHAIN_COMPACTION_THRESHOLD) - 1));// less than the default threshold

        store.replace(1L, "one");
        Mockito.verify(proxy, Mockito.never()).replaceAtHead(ArgumentMatchers.anyLong(), ArgumentMatchers.any(Chain.class), ArgumentMatchers.any(Chain.class));
        Mockito.when(resolvedChain.getCompactionCount()).thenReturn(ClusteredStore.DEFAULT_CHAIN_COMPACTION_THRESHOLD);// equal to the default threshold

        store.replace(1L, "one");
        Mockito.verify(proxy, Mockito.never()).replaceAtHead(ArgumentMatchers.anyLong(), ArgumentMatchers.any(Chain.class), ArgumentMatchers.any(Chain.class));
        Mockito.when(resolvedChain.getCompactionCount()).thenReturn(((ClusteredStore.DEFAULT_CHAIN_COMPACTION_THRESHOLD) + 1));// greater than the default threshold

        store.replace(1L, "one");
        Mockito.verify(proxy).replaceAtHead(ArgumentMatchers.anyLong(), ArgumentMatchers.any(Chain.class), ArgumentMatchers.any(Chain.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testConditionalReplaceReplacesChainOnlyOnCompressionThreshold() throws Exception {
        ResolvedChain<Long, String> resolvedChain = Mockito.mock(ResolvedChain.class);
        Mockito.when(resolvedChain.getResolvedResult(ArgumentMatchers.anyLong())).thenReturn(Mockito.mock(Result.class));
        Mockito.when(resolvedChain.getCompactedChain()).thenReturn(Mockito.mock(Chain.class));
        ServerStoreProxy proxy = Mockito.mock(ServerStoreProxy.class);
        Mockito.when(proxy.getAndAppend(ArgumentMatchers.anyLong(), ArgumentMatchers.any(ByteBuffer.class))).thenReturn(Mockito.mock(Chain.class));
        EternalChainResolver<Long, String> resolver = Mockito.mock(EternalChainResolver.class);
        Mockito.when(resolver.resolve(ArgumentMatchers.any(Chain.class), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong())).thenReturn(resolvedChain);
        OperationsCodec<Long, String> codec = Mockito.mock(OperationsCodec.class);
        Mockito.when(codec.encode(ArgumentMatchers.any())).thenReturn(ByteBuffer.allocate(0));
        TimeSource timeSource = Mockito.mock(TimeSource.class);
        ClusteredStore<Long, String> store = new ClusteredStore(config, codec, resolver, proxy, timeSource);
        Mockito.when(resolvedChain.getCompactionCount()).thenReturn(((ClusteredStore.DEFAULT_CHAIN_COMPACTION_THRESHOLD) - 1));// less than the default threshold

        store.replace(1L, "one", "anotherOne");
        Mockito.verify(proxy, Mockito.never()).replaceAtHead(ArgumentMatchers.anyLong(), ArgumentMatchers.any(Chain.class), ArgumentMatchers.any(Chain.class));
        Mockito.when(resolvedChain.getCompactionCount()).thenReturn(ClusteredStore.DEFAULT_CHAIN_COMPACTION_THRESHOLD);// equal to the default threshold

        store.replace(1L, "one", "anotherOne");
        Mockito.verify(proxy, Mockito.never()).replaceAtHead(ArgumentMatchers.anyLong(), ArgumentMatchers.any(Chain.class), ArgumentMatchers.any(Chain.class));
        Mockito.when(resolvedChain.getCompactionCount()).thenReturn(((ClusteredStore.DEFAULT_CHAIN_COMPACTION_THRESHOLD) + 1));// greater than the default threshold

        store.replace(1L, "one", "anotherOne");
        Mockito.verify(proxy).replaceAtHead(ArgumentMatchers.anyLong(), ArgumentMatchers.any(Chain.class), ArgumentMatchers.any(Chain.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCustomCompressionThreshold() throws Exception {
        int customThreshold = 4;
        try {
            System.setProperty(ClusteredStore.CHAIN_COMPACTION_THRESHOLD_PROP, String.valueOf(customThreshold));
            Result<Long, String> result = Mockito.mock(Result.class);
            Mockito.when(result.getValue()).thenReturn("one");
            ResolvedChain<Long, String> resolvedChain = Mockito.mock(ResolvedChain.class);
            Mockito.when(resolvedChain.getResolvedResult(ArgumentMatchers.anyLong())).thenReturn(result);
            Mockito.when(resolvedChain.getCompactedChain()).thenReturn(Mockito.mock(Chain.class));
            ServerStoreProxy proxy = Mockito.mock(ServerStoreProxy.class);
            Mockito.when(proxy.getAndAppend(ArgumentMatchers.anyLong(), ArgumentMatchers.any(ByteBuffer.class))).thenReturn(Mockito.mock(Chain.class));
            EternalChainResolver<Long, String> resolver = Mockito.mock(EternalChainResolver.class);
            Mockito.when(resolver.resolve(ArgumentMatchers.any(Chain.class), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong())).thenReturn(resolvedChain);
            OperationsCodec<Long, String> codec = Mockito.mock(OperationsCodec.class);
            Mockito.when(codec.encode(ArgumentMatchers.any())).thenReturn(ByteBuffer.allocate(0));
            TimeSource timeSource = Mockito.mock(TimeSource.class);
            ClusteredStore<Long, String> store = new ClusteredStore(config, codec, resolver, proxy, timeSource);
            Mockito.when(resolvedChain.getCompactionCount()).thenReturn((customThreshold - 1));// less than the custom threshold

            store.putIfAbsent(1L, "one", ( b) -> {
            });
            Mockito.verify(proxy, Mockito.never()).replaceAtHead(ArgumentMatchers.anyLong(), ArgumentMatchers.any(Chain.class), ArgumentMatchers.any(Chain.class));
            Mockito.when(resolvedChain.getCompactionCount()).thenReturn(customThreshold);// equal to the custom threshold

            store.replace(1L, "one");
            Mockito.verify(proxy, Mockito.never()).replaceAtHead(ArgumentMatchers.anyLong(), ArgumentMatchers.any(Chain.class), ArgumentMatchers.any(Chain.class));
            Mockito.when(resolvedChain.getCompactionCount()).thenReturn((customThreshold + 1));// greater than the custom threshold

            store.replace(1L, "one");
            Mockito.verify(proxy).replaceAtHead(ArgumentMatchers.anyLong(), ArgumentMatchers.any(Chain.class), ArgumentMatchers.any(Chain.class));
        } finally {
            System.clearProperty(ClusteredStore.CHAIN_COMPACTION_THRESHOLD_PROP);
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRemoveReplacesChainOnHits() throws Exception {
        ResolvedChain<Long, String> resolvedChain = Mockito.mock(ResolvedChain.class);
        Mockito.when(resolvedChain.getCompactedChain()).thenReturn(Mockito.mock(Chain.class));
        Mockito.when(resolvedChain.getResolvedResult(ArgumentMatchers.anyLong())).thenReturn(Mockito.mock(Result.class));// simulate a key hit on chain resolution

        Mockito.when(resolvedChain.getCompactionCount()).thenReturn(1);
        ServerStoreProxy proxy = Mockito.mock(ServerStoreProxy.class);
        Mockito.when(proxy.getAndAppend(ArgumentMatchers.anyLong(), ArgumentMatchers.any(ByteBuffer.class))).thenReturn(Mockito.mock(Chain.class));
        EternalChainResolver<Long, String> resolver = Mockito.mock(EternalChainResolver.class);
        Mockito.when(resolver.resolve(ArgumentMatchers.any(Chain.class), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong())).thenReturn(resolvedChain);
        OperationsCodec<Long, String> codec = Mockito.mock(OperationsCodec.class);
        Mockito.when(codec.encode(ArgumentMatchers.any())).thenReturn(ByteBuffer.allocate(0));
        TimeSource timeSource = Mockito.mock(TimeSource.class);
        ClusteredStore<Long, String> store = new ClusteredStore(config, codec, resolver, proxy, timeSource);
        store.remove(1L);
        Mockito.verify(proxy).replaceAtHead(ArgumentMatchers.anyLong(), ArgumentMatchers.any(Chain.class), ArgumentMatchers.any(Chain.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRemoveDoesNotReplaceChainOnMisses() throws Exception {
        ResolvedChain<Long, String> resolvedChain = Mockito.mock(ResolvedChain.class);
        Mockito.when(resolvedChain.getResolvedResult(ArgumentMatchers.anyLong())).thenReturn(null);// simulate a key miss on chain resolution

        EternalChainResolver<Long, String> resolver = Mockito.mock(EternalChainResolver.class);
        Mockito.when(resolver.resolve(ArgumentMatchers.any(Chain.class), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong())).thenReturn(resolvedChain);
        OperationsCodec<Long, String> codec = Mockito.mock(OperationsCodec.class);
        Mockito.when(codec.encode(ArgumentMatchers.any())).thenReturn(ByteBuffer.allocate(0));
        ServerStoreProxy proxy = Mockito.mock(ServerStoreProxy.class);
        Mockito.when(proxy.getAndAppend(ArgumentMatchers.anyLong(), ArgumentMatchers.any(ByteBuffer.class))).thenReturn(Mockito.mock(Chain.class));
        TimeSource timeSource = Mockito.mock(TimeSource.class);
        ClusteredStore<Long, String> store = new ClusteredStore(config, codec, resolver, proxy, timeSource);
        store.remove(1L);
        Mockito.verify(proxy, Mockito.never()).replaceAtHead(ArgumentMatchers.anyLong(), ArgumentMatchers.any(Chain.class), ArgumentMatchers.any(Chain.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testConditionalRemoveReplacesChainOnHits() throws Exception {
        Result<Long, String> result = Mockito.mock(Result.class);
        Mockito.when(result.getValue()).thenReturn("foo");
        ResolvedChain<Long, String> resolvedChain = Mockito.mock(ResolvedChain.class);
        Mockito.when(resolvedChain.getCompactedChain()).thenReturn(Mockito.mock(Chain.class));
        Mockito.when(resolvedChain.getResolvedResult(ArgumentMatchers.anyLong())).thenReturn(result);// simulate a key hit on chain resolution

        Mockito.when(resolvedChain.getCompactionCount()).thenReturn(1);
        ServerStoreProxy proxy = Mockito.mock(ServerStoreProxy.class);
        Mockito.when(proxy.getAndAppend(ArgumentMatchers.anyLong(), ArgumentMatchers.any(ByteBuffer.class))).thenReturn(Mockito.mock(Chain.class));
        EternalChainResolver<Long, String> resolver = Mockito.mock(EternalChainResolver.class);
        Mockito.when(resolver.resolve(ArgumentMatchers.any(Chain.class), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong())).thenReturn(resolvedChain);
        OperationsCodec<Long, String> codec = Mockito.mock(OperationsCodec.class);
        Mockito.when(codec.encode(ArgumentMatchers.any())).thenReturn(ByteBuffer.allocate(0));
        TimeSource timeSource = Mockito.mock(TimeSource.class);
        ClusteredStore<Long, String> store = new ClusteredStore(config, codec, resolver, proxy, timeSource);
        store.remove(1L, "foo");
        Mockito.verify(proxy).replaceAtHead(ArgumentMatchers.anyLong(), ArgumentMatchers.any(Chain.class), ArgumentMatchers.any(Chain.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testConditionalRemoveDoesNotReplaceChainOnKeyMiss() throws Exception {
        ResolvedChain<Long, String> resolvedChain = Mockito.mock(ResolvedChain.class);
        Mockito.when(resolvedChain.getResolvedResult(ArgumentMatchers.anyLong())).thenReturn(null);// simulate a key miss on chain resolution

        EternalChainResolver<Long, String> resolver = Mockito.mock(EternalChainResolver.class);
        Mockito.when(resolver.resolve(ArgumentMatchers.any(Chain.class), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong())).thenReturn(resolvedChain);
        OperationsCodec<Long, String> codec = Mockito.mock(OperationsCodec.class);
        Mockito.when(codec.encode(ArgumentMatchers.any())).thenReturn(ByteBuffer.allocate(0));
        ServerStoreProxy proxy = Mockito.mock(ServerStoreProxy.class);
        Mockito.when(proxy.getAndAppend(ArgumentMatchers.anyLong(), ArgumentMatchers.any(ByteBuffer.class))).thenReturn(Mockito.mock(Chain.class));
        TimeSource timeSource = Mockito.mock(TimeSource.class);
        ClusteredStore<Long, String> store = new ClusteredStore(config, codec, resolver, proxy, timeSource);
        store.remove(1L, "foo");
        Mockito.verify(proxy, Mockito.never()).replaceAtHead(ArgumentMatchers.anyLong(), ArgumentMatchers.any(Chain.class), ArgumentMatchers.any(Chain.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testConditionalRemoveDoesNotReplaceChainOnKeyHitValueMiss() throws Exception {
        Result<Long, String> result = Mockito.mock(Result.class);
        ResolvedChain<Long, String> resolvedChain = Mockito.mock(ResolvedChain.class);
        Mockito.when(resolvedChain.getResolvedResult(ArgumentMatchers.anyLong())).thenReturn(result);// simulate a key kit

        Mockito.when(result.getValue()).thenReturn("bar");// but a value miss

        EternalChainResolver<Long, String> resolver = Mockito.mock(EternalChainResolver.class);
        Mockito.when(resolver.resolve(ArgumentMatchers.any(Chain.class), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong())).thenReturn(resolvedChain);
        OperationsCodec<Long, String> codec = Mockito.mock(OperationsCodec.class);
        Mockito.when(codec.encode(ArgumentMatchers.any())).thenReturn(ByteBuffer.allocate(0));
        ServerStoreProxy proxy = Mockito.mock(ServerStoreProxy.class);
        Mockito.when(proxy.getAndAppend(ArgumentMatchers.anyLong(), ArgumentMatchers.any(ByteBuffer.class))).thenReturn(Mockito.mock(Chain.class));
        TimeSource timeSource = Mockito.mock(TimeSource.class);
        ClusteredStore<Long, String> store = new ClusteredStore(config, codec, resolver, proxy, timeSource);
        store.remove(1L, "foo");
        Mockito.verify(proxy, Mockito.never()).replaceAtHead(ArgumentMatchers.anyLong(), ArgumentMatchers.any(Chain.class), ArgumentMatchers.any(Chain.class));
    }

    @Test
    public void testExpirationIsSentToHigherTiers() throws Exception {
        @SuppressWarnings("unchecked")
        Result<Long, String> result = Mockito.mock(Result.class);
        Mockito.when(result.getValue()).thenReturn("bar");
        @SuppressWarnings("unchecked")
        ResolvedChain<Long, String> resolvedChain = Mockito.mock(ResolvedChain.class);
        Mockito.when(resolvedChain.getResolvedResult(ArgumentMatchers.anyLong())).thenReturn(result);
        Mockito.when(resolvedChain.getExpirationTime()).thenReturn(1000L);
        @SuppressWarnings("unchecked")
        EternalChainResolver<Long, String> resolver = Mockito.mock(EternalChainResolver.class);
        Mockito.when(resolver.resolve(ArgumentMatchers.any(Chain.class), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong())).thenReturn(resolvedChain);
        ServerStoreProxy proxy = Mockito.mock(ServerStoreProxy.class);
        Mockito.when(proxy.get(ArgumentMatchers.anyLong())).thenReturn(Mockito.mock(Chain.class));
        @SuppressWarnings("unchecked")
        OperationsCodec<Long, String> codec = Mockito.mock(OperationsCodec.class);
        TimeSource timeSource = Mockito.mock(TimeSource.class);
        ClusteredStore<Long, String> store = new ClusteredStore(config, codec, resolver, proxy, timeSource);
        Store.ValueHolder<?> vh = store.get(1L);
        long expirationTime = vh.expirationTime();
        Assert.assertThat(expirationTime, CoreMatchers.is(1000L));
    }

    @Test
    public void testNoExpireIsSentToHigherTiers() throws Exception {
        @SuppressWarnings("unchecked")
        Result<Long, String> result = Mockito.mock(Result.class);
        Mockito.when(result.getValue()).thenReturn("bar");
        @SuppressWarnings("unchecked")
        ResolvedChain<Long, String> resolvedChain = Mockito.mock(ResolvedChain.class);
        Mockito.when(resolvedChain.getResolvedResult(ArgumentMatchers.anyLong())).thenReturn(result);
        Mockito.when(resolvedChain.getExpirationTime()).thenReturn(Long.MAX_VALUE);// no expire

        @SuppressWarnings("unchecked")
        EternalChainResolver<Long, String> resolver = Mockito.mock(EternalChainResolver.class);
        Mockito.when(resolver.resolve(ArgumentMatchers.any(Chain.class), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong())).thenReturn(resolvedChain);
        ServerStoreProxy proxy = Mockito.mock(ServerStoreProxy.class);
        Mockito.when(proxy.get(ArgumentMatchers.anyLong())).thenReturn(Mockito.mock(Chain.class));
        @SuppressWarnings("unchecked")
        OperationsCodec<Long, String> codec = Mockito.mock(OperationsCodec.class);
        TimeSource timeSource = Mockito.mock(TimeSource.class);
        ClusteredStore<Long, String> store = new ClusteredStore(config, codec, resolver, proxy, timeSource);
        Store.ValueHolder<?> vh = store.get(1L);
        long expirationTime = vh.expirationTime();
        Assert.assertThat(expirationTime, CoreMatchers.is(NO_EXPIRE));
    }

    @Test
    public void testEmptyChainIteratorIsEmpty() throws StoreAccessException {
        Store.Iterator<Cache.Entry<Long, Store.ValueHolder<String>>> iterator = store.iterator();
        Assert.assertThat(iterator.hasNext(), CoreMatchers.is(false));
        try {
            iterator.next();
            Assert.fail("Expected NoSuchElementException");
        } catch (NoSuchElementException e) {
            // expected
        }
    }

    @Test
    public void testSingleChainSingleValue() throws StoreAccessException {
        store.put(1L, "foo");
        Store.Iterator<Cache.Entry<Long, Store.ValueHolder<String>>> iterator = store.iterator();
        Assert.assertThat(iterator.hasNext(), CoreMatchers.is(true));
        Assert.assertThat(iterator.next(), isEntry(1L, "foo"));
        Assert.assertThat(iterator.hasNext(), CoreMatchers.is(false));
        try {
            iterator.next();
            Assert.fail("Expected NoSuchElementException");
        } catch (NoSuchElementException e) {
            // expected
        }
    }

    @Test
    public void testSingleChainMultipleValues() throws StoreAccessException {
        Assert.assertThat(Long.hashCode(1L), CoreMatchers.is(Long.hashCode((~1L))));
        store.put(1L, "foo");
        store.put((~1L), "bar");
        Store.Iterator<Cache.Entry<Long, Store.ValueHolder<String>>> iterator = store.iterator();
        Matcher<Cache.Entry<Long, Store.ValueHolder<String>>> entryOne = isEntry(1L, "foo");
        Matcher<Cache.Entry<Long, Store.ValueHolder<String>>> entryTwo = isEntry((~1L), "bar");
        Assert.assertThat(iterator.hasNext(), CoreMatchers.is(true));
        Cache.Entry<Long, Store.ValueHolder<String>> next = iterator.next();
        Assert.assertThat(next, CombinableMatcher.either(entryOne).or(entryTwo));
        if (entryOne.matches(next)) {
            Assert.assertThat(iterator.hasNext(), CoreMatchers.is(true));
            Assert.assertThat(iterator.next(), CoreMatchers.is(entryTwo));
            Assert.assertThat(iterator.hasNext(), CoreMatchers.is(false));
        } else {
            Assert.assertThat(iterator.hasNext(), CoreMatchers.is(true));
            Assert.assertThat(iterator.next(), CoreMatchers.is(entryOne));
            Assert.assertThat(iterator.hasNext(), CoreMatchers.is(false));
        }
        try {
            iterator.next();
            Assert.fail("Expected NoSuchElementException");
        } catch (NoSuchElementException e) {
            // expected
        }
    }

    @Test
    public void testSingleChainRequiresResolution() throws StoreAccessException {
        store.put((~1L), "bar");
        store.put(1L, "foo");
        store.remove((~1L));
        Store.Iterator<Cache.Entry<Long, Store.ValueHolder<String>>> iterator = store.iterator();
        Assert.assertThat(iterator.hasNext(), CoreMatchers.is(true));
        Assert.assertThat(iterator.next(), isEntry(1L, "foo"));
        Assert.assertThat(iterator.hasNext(), CoreMatchers.is(false));
        try {
            iterator.next();
            Assert.fail("Expected NoSuchElementException");
        } catch (NoSuchElementException e) {
            // expected
        }
    }

    @Test
    public void testMultipleChains() throws StoreAccessException {
        store.put(1L, "foo");
        store.put(2L, "bar");
        Store.Iterator<Cache.Entry<Long, Store.ValueHolder<String>>> iterator = store.iterator();
        Matcher<Cache.Entry<Long, Store.ValueHolder<String>>> entryOne = isEntry(1L, "foo");
        Matcher<Cache.Entry<Long, Store.ValueHolder<String>>> entryTwo = isEntry(2L, "bar");
        Assert.assertThat(iterator.hasNext(), CoreMatchers.is(true));
        Cache.Entry<Long, Store.ValueHolder<String>> next = iterator.next();
        Assert.assertThat(next, CombinableMatcher.either(entryOne).or(entryTwo));
        if (entryOne.matches(next)) {
            Assert.assertThat(iterator.hasNext(), CoreMatchers.is(true));
            Assert.assertThat(iterator.next(), CoreMatchers.is(entryTwo));
            Assert.assertThat(iterator.hasNext(), CoreMatchers.is(false));
        } else {
            Assert.assertThat(iterator.hasNext(), CoreMatchers.is(true));
            Assert.assertThat(iterator.next(), CoreMatchers.is(entryOne));
            Assert.assertThat(iterator.hasNext(), CoreMatchers.is(false));
        }
        try {
            iterator.next();
            Assert.fail("Expected NoSuchElementException");
        } catch (NoSuchElementException e) {
            // expected
        }
    }
}

