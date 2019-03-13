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
package org.ehcache.clustered.client.internal.loaderwriter;


import java.nio.ByteBuffer;
import org.ehcache.clustered.ChainUtils;
import org.ehcache.clustered.client.internal.store.ServerStoreProxy;
import org.ehcache.clustered.client.internal.store.lock.LockingServerStoreProxy;
import org.ehcache.clustered.client.internal.store.operations.EternalChainResolver;
import org.ehcache.clustered.common.internal.store.Chain;
import org.ehcache.clustered.common.internal.store.operations.PutOperation;
import org.ehcache.clustered.common.internal.store.operations.codecs.OperationsCodec;
import org.ehcache.clustered.loaderWriter.TestCacheLoaderWriter;
import org.ehcache.core.spi.store.Store;
import org.ehcache.core.spi.time.TimeSource;
import org.ehcache.impl.serialization.LongSerializer;
import org.ehcache.impl.serialization.StringSerializer;
import org.ehcache.spi.loaderwriter.CacheLoaderWriter;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import static org.ehcache.core.spi.store.Store.PutStatus.PUT;
import static org.ehcache.core.spi.store.Store.RemoveStatus.KEY_MISSING;
import static org.ehcache.core.spi.store.Store.RemoveStatus.KEY_PRESENT;
import static org.ehcache.core.spi.store.Store.RemoveStatus.REMOVED;
import static org.ehcache.core.spi.store.Store.ReplaceStatus.HIT;
import static org.ehcache.core.spi.store.Store.ReplaceStatus.MISS_NOT_PRESENT;
import static org.ehcache.core.spi.store.Store.ReplaceStatus.MISS_PRESENT;


public class ClusteredLoaderWriterStoreTest {
    @SuppressWarnings("unchecked")
    private Store.Configuration<Long, String> configuration = Mockito.mock(Store.Configuration.class);

    private OperationsCodec<Long, String> codec = new OperationsCodec(new LongSerializer(), new StringSerializer());

    private EternalChainResolver<Long, String> resolver = new EternalChainResolver(codec);

    private TimeSource timeSource = Mockito.mock(TimeSource.class);

    @Test
    public void testGetValueAbsentInSOR() throws Exception {
        ServerStoreProxy storeProxy = Mockito.mock(LockingServerStoreProxy.class);
        CacheLoaderWriter<Long, String> loaderWriter = new TestCacheLoaderWriter();
        Mockito.when(storeProxy.get(ArgumentMatchers.eq(1L))).thenReturn(ChainUtils.chainOf());
        ClusteredLoaderWriterStore<Long, String> store = new ClusteredLoaderWriterStore(configuration, codec, resolver, storeProxy, timeSource, loaderWriter);
        Assert.assertThat(store.get(1L), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void testGetValuePresentInSOR() throws Exception {
        ServerStoreProxy storeProxy = Mockito.mock(LockingServerStoreProxy.class);
        TestCacheLoaderWriter loaderWriter = new TestCacheLoaderWriter();
        loaderWriter.storeMap.put(1L, "one");
        Mockito.when(storeProxy.get(ArgumentMatchers.eq(1L))).thenReturn(ChainUtils.chainOf());
        ClusteredLoaderWriterStore<Long, String> store = new ClusteredLoaderWriterStore(configuration, codec, resolver, storeProxy, timeSource, loaderWriter);
        Assert.assertThat(store.get(1L).get(), Matchers.equalTo("one"));
    }

    @Test
    public void testGetValuePresentInCache() throws Exception {
        ServerStoreProxy storeProxy = Mockito.mock(LockingServerStoreProxy.class);
        @SuppressWarnings("unchecked")
        CacheLoaderWriter<Long, String> loaderWriter = Mockito.mock(CacheLoaderWriter.class);
        PutOperation<Long, String> operation = new PutOperation(1L, "one", System.currentTimeMillis());
        Chain toReturn = ChainUtils.chainOf(codec.encode(operation));
        Mockito.when(storeProxy.get(ArgumentMatchers.anyLong())).thenReturn(toReturn);
        ClusteredLoaderWriterStore<Long, String> store = new ClusteredLoaderWriterStore(configuration, codec, resolver, storeProxy, timeSource, loaderWriter);
        Assert.assertThat(store.get(1L).get(), Matchers.equalTo("one"));
        Mockito.verify(loaderWriter, Mockito.times(0)).load(ArgumentMatchers.anyLong());
        Mockito.verifyZeroInteractions(loaderWriter);
    }

    @Test
    public void testPut() throws Exception {
        ServerStoreProxy storeProxy = Mockito.mock(LockingServerStoreProxy.class);
        TestCacheLoaderWriter loaderWriter = new TestCacheLoaderWriter();
        ClusteredLoaderWriterStore<Long, String> store = new ClusteredLoaderWriterStore(configuration, codec, resolver, storeProxy, timeSource, loaderWriter);
        Assert.assertThat(loaderWriter.storeMap.containsKey(1L), Matchers.is(false));
        Assert.assertThat(store.put(1L, "one"), Matchers.is(PUT));
        Assert.assertThat(loaderWriter.storeMap.containsKey(1L), Matchers.is(true));
    }

    @Test
    public void testRemoveValueAbsentInCachePresentInSOR() throws Exception {
        LockingServerStoreProxy storeProxy = Mockito.mock(LockingServerStoreProxy.class);
        TestCacheLoaderWriter loaderWriter = new TestCacheLoaderWriter();
        Mockito.when(storeProxy.lock(ArgumentMatchers.anyLong())).thenReturn(ChainUtils.chainOf());
        ClusteredLoaderWriterStore<Long, String> store = new ClusteredLoaderWriterStore(configuration, codec, resolver, storeProxy, timeSource, loaderWriter);
        loaderWriter.storeMap.put(1L, "one");
        Assert.assertThat(store.remove(1L), Matchers.is(false));
        Assert.assertThat(loaderWriter.storeMap.containsKey(1L), Matchers.is(false));
    }

    @Test
    public void testRemoveValuePresentInCachePresentInSOR() throws Exception {
        LockingServerStoreProxy storeProxy = Mockito.mock(LockingServerStoreProxy.class);
        TestCacheLoaderWriter loaderWriter = new TestCacheLoaderWriter();
        PutOperation<Long, String> operation = new PutOperation(1L, "one", System.currentTimeMillis());
        Chain toReturn = ChainUtils.chainOf(codec.encode(operation));
        Mockito.when(storeProxy.lock(ArgumentMatchers.anyLong())).thenReturn(toReturn);
        Mockito.when(storeProxy.get(ArgumentMatchers.anyLong())).thenReturn(toReturn);
        ClusteredLoaderWriterStore<Long, String> store = new ClusteredLoaderWriterStore(configuration, codec, resolver, storeProxy, timeSource, loaderWriter);
        loaderWriter.storeMap.put(1L, "one");
        Assert.assertThat(store.get(1L).get(), Matchers.equalTo("one"));
        Assert.assertThat(store.remove(1L), Matchers.is(true));
        Assert.assertThat(loaderWriter.storeMap.containsKey(1L), Matchers.is(false));
    }

    @Test
    public void testRemoveValueAbsentInCacheAbsentInSOR() throws Exception {
        LockingServerStoreProxy storeProxy = Mockito.mock(LockingServerStoreProxy.class);
        @SuppressWarnings("unchecked")
        CacheLoaderWriter<Long, String> loaderWriter = Mockito.mock(CacheLoaderWriter.class);
        Mockito.when(storeProxy.lock(ArgumentMatchers.anyLong())).thenReturn(ChainUtils.chainOf());
        ClusteredLoaderWriterStore<Long, String> store = new ClusteredLoaderWriterStore(configuration, codec, resolver, storeProxy, timeSource, loaderWriter);
        Assert.assertThat(store.remove(1L), Matchers.is(false));
        Mockito.verify(loaderWriter, Mockito.times(1)).delete(ArgumentMatchers.anyLong());
    }

    @Test
    public void testPufIfAbsentValueAbsentInCacheAbsentInSOR() throws Exception {
        LockingServerStoreProxy storeProxy = Mockito.mock(LockingServerStoreProxy.class);
        TestCacheLoaderWriter loaderWriter = new TestCacheLoaderWriter();
        Mockito.when(storeProxy.lock(ArgumentMatchers.anyLong())).thenReturn(ChainUtils.chainOf());
        ClusteredLoaderWriterStore<Long, String> store = new ClusteredLoaderWriterStore(configuration, codec, resolver, storeProxy, timeSource, loaderWriter);
        Assert.assertThat(loaderWriter.storeMap.isEmpty(), Matchers.is(true));
        Assert.assertThat(store.putIfAbsent(1L, "one", null), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(loaderWriter.storeMap.get(1L), Matchers.equalTo("one"));
    }

    @Test
    public void testPufIfAbsentValueAbsentInCachePresentInSOR() throws Exception {
        LockingServerStoreProxy storeProxy = Mockito.mock(LockingServerStoreProxy.class);
        TestCacheLoaderWriter loaderWriter = new TestCacheLoaderWriter();
        Mockito.when(storeProxy.lock(ArgumentMatchers.anyLong())).thenReturn(ChainUtils.chainOf());
        ClusteredLoaderWriterStore<Long, String> store = new ClusteredLoaderWriterStore(configuration, codec, resolver, storeProxy, timeSource, loaderWriter);
        loaderWriter.storeMap.put(1L, "one");
        Assert.assertThat(store.putIfAbsent(1L, "Again", null).get(), Matchers.equalTo("one"));
        Mockito.verify(storeProxy, Mockito.times(0)).append(ArgumentMatchers.anyLong(), ArgumentMatchers.any(ByteBuffer.class));
        Assert.assertThat(loaderWriter.storeMap.get(1L), Matchers.equalTo("one"));
    }

    @Test
    public void testPufIfAbsentValuePresentInCachePresentInSOR() throws Exception {
        LockingServerStoreProxy storeProxy = Mockito.mock(LockingServerStoreProxy.class);
        TestCacheLoaderWriter loaderWriter = new TestCacheLoaderWriter();
        PutOperation<Long, String> operation = new PutOperation(1L, "one", System.currentTimeMillis());
        Chain toReturn = ChainUtils.chainOf(codec.encode(operation));
        Mockito.when(storeProxy.lock(ArgumentMatchers.anyLong())).thenReturn(toReturn);
        ClusteredLoaderWriterStore<Long, String> store = new ClusteredLoaderWriterStore(configuration, codec, resolver, storeProxy, timeSource, loaderWriter);
        loaderWriter.storeMap.put(1L, "one");
        Assert.assertThat(store.putIfAbsent(1L, "Again", null).get(), Matchers.equalTo("one"));
        Mockito.verify(storeProxy, Mockito.times(0)).append(ArgumentMatchers.anyLong(), ArgumentMatchers.any(ByteBuffer.class));
        Assert.assertThat(loaderWriter.storeMap.get(1L), Matchers.equalTo("one"));
    }

    @Test
    public void testReplaceValueAbsentInCacheAbsentInSOR() throws Exception {
        LockingServerStoreProxy storeProxy = Mockito.mock(LockingServerStoreProxy.class);
        TestCacheLoaderWriter loaderWriter = new TestCacheLoaderWriter();
        Mockito.when(storeProxy.lock(ArgumentMatchers.anyLong())).thenReturn(ChainUtils.chainOf());
        ClusteredLoaderWriterStore<Long, String> store = new ClusteredLoaderWriterStore(configuration, codec, resolver, storeProxy, timeSource, loaderWriter);
        Assert.assertThat(loaderWriter.storeMap.isEmpty(), Matchers.is(true));
        Assert.assertThat(store.replace(1L, "one"), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(loaderWriter.storeMap.isEmpty(), Matchers.is(true));
        Mockito.verify(storeProxy, Mockito.times(0)).append(ArgumentMatchers.anyLong(), ArgumentMatchers.any(ByteBuffer.class));
    }

    @Test
    public void testReplaceValueAbsentInCachePresentInSOR() throws Exception {
        LockingServerStoreProxy storeProxy = Mockito.mock(LockingServerStoreProxy.class);
        TestCacheLoaderWriter loaderWriter = new TestCacheLoaderWriter();
        Mockito.when(storeProxy.lock(ArgumentMatchers.anyLong())).thenReturn(ChainUtils.chainOf());
        ClusteredLoaderWriterStore<Long, String> store = new ClusteredLoaderWriterStore(configuration, codec, resolver, storeProxy, timeSource, loaderWriter);
        loaderWriter.storeMap.put(1L, "one");
        Assert.assertThat(store.replace(1L, "Again").get(), Matchers.equalTo("one"));
        Mockito.verify(storeProxy, Mockito.times(1)).append(ArgumentMatchers.anyLong(), ArgumentMatchers.any(ByteBuffer.class));
        Assert.assertThat(loaderWriter.storeMap.get(1L), Matchers.equalTo("Again"));
    }

    @Test
    public void testReplaceValuePresentInCachePresentInSOR() throws Exception {
        LockingServerStoreProxy storeProxy = Mockito.mock(LockingServerStoreProxy.class);
        TestCacheLoaderWriter loaderWriter = new TestCacheLoaderWriter();
        PutOperation<Long, String> operation = new PutOperation(1L, "one", System.currentTimeMillis());
        Chain toReturn = ChainUtils.chainOf(codec.encode(operation));
        Mockito.when(storeProxy.lock(ArgumentMatchers.anyLong())).thenReturn(toReturn);
        ClusteredLoaderWriterStore<Long, String> store = new ClusteredLoaderWriterStore(configuration, codec, resolver, storeProxy, timeSource, loaderWriter);
        loaderWriter.storeMap.put(1L, "one");
        Assert.assertThat(store.replace(1L, "Again").get(), Matchers.equalTo("one"));
        Mockito.verify(storeProxy, Mockito.times(1)).append(ArgumentMatchers.anyLong(), ArgumentMatchers.any(ByteBuffer.class));
        Assert.assertThat(loaderWriter.storeMap.get(1L), Matchers.equalTo("Again"));
    }

    @Test
    public void testRemove2ArgsValueAbsentInCacheAbsentInSOR() throws Exception {
        LockingServerStoreProxy storeProxy = Mockito.mock(LockingServerStoreProxy.class);
        @SuppressWarnings("unchecked")
        CacheLoaderWriter<Long, String> loaderWriter = Mockito.mock(CacheLoaderWriter.class);
        Mockito.when(storeProxy.lock(ArgumentMatchers.anyLong())).thenReturn(ChainUtils.chainOf());
        ClusteredLoaderWriterStore<Long, String> store = new ClusteredLoaderWriterStore(configuration, codec, resolver, storeProxy, timeSource, loaderWriter);
        Assert.assertThat(store.remove(1L, "one"), Matchers.is(KEY_MISSING));
        Mockito.verify(storeProxy, Mockito.times(0)).append(ArgumentMatchers.anyLong(), ArgumentMatchers.any(ByteBuffer.class));
    }

    @Test
    public void testRemove2ArgsValueAbsentInCachePresentInSOR() throws Exception {
        LockingServerStoreProxy storeProxy = Mockito.mock(LockingServerStoreProxy.class);
        TestCacheLoaderWriter loaderWriter = new TestCacheLoaderWriter();
        Mockito.when(storeProxy.lock(ArgumentMatchers.anyLong())).thenReturn(ChainUtils.chainOf());
        ClusteredLoaderWriterStore<Long, String> store = new ClusteredLoaderWriterStore(configuration, codec, resolver, storeProxy, timeSource, loaderWriter);
        loaderWriter.storeMap.put(1L, "one");
        Assert.assertThat(store.remove(1L, "one"), Matchers.is(REMOVED));
        Mockito.verify(storeProxy, Mockito.times(1)).append(ArgumentMatchers.anyLong(), ArgumentMatchers.any(ByteBuffer.class));
        Assert.assertThat(loaderWriter.storeMap.isEmpty(), Matchers.is(true));
    }

    @Test
    public void testRemove2ArgsValuePresentInCachePresentInSOR() throws Exception {
        LockingServerStoreProxy storeProxy = Mockito.mock(LockingServerStoreProxy.class);
        @SuppressWarnings("unchecked")
        CacheLoaderWriter<Long, String> loaderWriter = Mockito.mock(CacheLoaderWriter.class);
        PutOperation<Long, String> operation = new PutOperation(1L, "one", System.currentTimeMillis());
        Chain toReturn = ChainUtils.chainOf(codec.encode(operation));
        Mockito.when(storeProxy.lock(ArgumentMatchers.anyLong())).thenReturn(toReturn);
        ClusteredLoaderWriterStore<Long, String> store = new ClusteredLoaderWriterStore(configuration, codec, resolver, storeProxy, timeSource, loaderWriter);
        Assert.assertThat(store.remove(1L, "one"), Matchers.is(REMOVED));
        Mockito.verify(storeProxy, Mockito.times(1)).append(ArgumentMatchers.anyLong(), ArgumentMatchers.any(ByteBuffer.class));
        Mockito.verify(loaderWriter, Mockito.times(0)).load(ArgumentMatchers.anyLong());
        Mockito.verify(loaderWriter, Mockito.times(1)).delete(ArgumentMatchers.anyLong());
    }

    @Test
    public void testRemove2ArgsValueAbsentInCacheDiffValuePresentInSOR() throws Exception {
        LockingServerStoreProxy storeProxy = Mockito.mock(LockingServerStoreProxy.class);
        TestCacheLoaderWriter loaderWriter = new TestCacheLoaderWriter();
        Mockito.when(storeProxy.lock(ArgumentMatchers.anyLong())).thenReturn(ChainUtils.chainOf());
        ClusteredLoaderWriterStore<Long, String> store = new ClusteredLoaderWriterStore(configuration, codec, resolver, storeProxy, timeSource, loaderWriter);
        loaderWriter.storeMap.put(1L, "one");
        Assert.assertThat(store.remove(1L, "Again"), Matchers.is(KEY_PRESENT));
        Mockito.verify(storeProxy, Mockito.times(0)).append(ArgumentMatchers.anyLong(), ArgumentMatchers.any(ByteBuffer.class));
        Assert.assertThat(loaderWriter.storeMap.get(1L), Matchers.equalTo("one"));
    }

    @Test
    public void testReplace2ArgsValueAbsentInCacheAbsentInSOR() throws Exception {
        LockingServerStoreProxy storeProxy = Mockito.mock(LockingServerStoreProxy.class);
        @SuppressWarnings("unchecked")
        CacheLoaderWriter<Long, String> loaderWriter = Mockito.mock(CacheLoaderWriter.class);
        Mockito.when(storeProxy.lock(ArgumentMatchers.anyLong())).thenReturn(ChainUtils.chainOf());
        ClusteredLoaderWriterStore<Long, String> store = new ClusteredLoaderWriterStore(configuration, codec, resolver, storeProxy, timeSource, loaderWriter);
        Assert.assertThat(store.replace(1L, "one", "Again"), Matchers.is(MISS_NOT_PRESENT));
        Mockito.verify(storeProxy, Mockito.times(0)).append(ArgumentMatchers.anyLong(), ArgumentMatchers.any(ByteBuffer.class));
        Mockito.verify(loaderWriter, Mockito.times(1)).load(ArgumentMatchers.anyLong());
        Mockito.verify(loaderWriter, Mockito.times(0)).write(ArgumentMatchers.anyLong(), ArgumentMatchers.anyString());
    }

    @Test
    public void testReplace2ArgsValueAbsentInCachePresentInSOR() throws Exception {
        LockingServerStoreProxy storeProxy = Mockito.mock(LockingServerStoreProxy.class);
        TestCacheLoaderWriter loaderWriter = new TestCacheLoaderWriter();
        Mockito.when(storeProxy.lock(ArgumentMatchers.anyLong())).thenReturn(ChainUtils.chainOf());
        ClusteredLoaderWriterStore<Long, String> store = new ClusteredLoaderWriterStore(configuration, codec, resolver, storeProxy, timeSource, loaderWriter);
        loaderWriter.storeMap.put(1L, "one");
        Assert.assertThat(store.replace(1L, "one", "Again"), Matchers.is(HIT));
        Mockito.verify(storeProxy, Mockito.times(1)).append(ArgumentMatchers.anyLong(), ArgumentMatchers.any(ByteBuffer.class));
        Assert.assertThat(loaderWriter.storeMap.get(1L), Matchers.equalTo("Again"));
    }

    @Test
    public void testReplace2ArgsValuePresentInCachePresentInSOR() throws Exception {
        LockingServerStoreProxy storeProxy = Mockito.mock(LockingServerStoreProxy.class);
        @SuppressWarnings("unchecked")
        CacheLoaderWriter<Long, String> loaderWriter = Mockito.mock(CacheLoaderWriter.class);
        PutOperation<Long, String> operation = new PutOperation(1L, "one", System.currentTimeMillis());
        Chain toReturn = ChainUtils.chainOf(codec.encode(operation));
        Mockito.when(storeProxy.lock(ArgumentMatchers.anyLong())).thenReturn(toReturn);
        ClusteredLoaderWriterStore<Long, String> store = new ClusteredLoaderWriterStore(configuration, codec, resolver, storeProxy, timeSource, loaderWriter);
        Assert.assertThat(store.replace(1L, "one", "Again"), Matchers.is(HIT));
        Mockito.verify(storeProxy, Mockito.times(1)).append(ArgumentMatchers.anyLong(), ArgumentMatchers.any(ByteBuffer.class));
        Mockito.verify(loaderWriter, Mockito.times(0)).load(ArgumentMatchers.anyLong());
        Mockito.verify(loaderWriter, Mockito.times(1)).write(ArgumentMatchers.anyLong(), ArgumentMatchers.anyString());
    }

    @Test
    public void testReplace2ArgsValueAbsentInCacheDiffValueInSOR() throws Exception {
        LockingServerStoreProxy storeProxy = Mockito.mock(LockingServerStoreProxy.class);
        TestCacheLoaderWriter loaderWriter = new TestCacheLoaderWriter();
        Mockito.when(storeProxy.lock(ArgumentMatchers.anyLong())).thenReturn(ChainUtils.chainOf());
        ClusteredLoaderWriterStore<Long, String> store = new ClusteredLoaderWriterStore(configuration, codec, resolver, storeProxy, timeSource, loaderWriter);
        loaderWriter.storeMap.put(1L, "one");
        Assert.assertThat(store.replace(1L, "Again", "one"), Matchers.is(MISS_PRESENT));
        Mockito.verify(storeProxy, Mockito.times(0)).append(ArgumentMatchers.anyLong(), ArgumentMatchers.any(ByteBuffer.class));
        Assert.assertThat(loaderWriter.storeMap.get(1L), Matchers.equalTo("one"));
    }
}

