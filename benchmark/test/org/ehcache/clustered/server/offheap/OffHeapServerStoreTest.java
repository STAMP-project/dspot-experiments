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


import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;
import org.ehcache.clustered.ChainUtils;
import org.ehcache.clustered.common.internal.store.Chain;
import org.ehcache.clustered.server.KeySegmentMapper;
import org.ehcache.clustered.server.store.ServerStoreTest;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.terracotta.offheapstore.buffersource.OffHeapBufferSource;
import org.terracotta.offheapstore.exceptions.OversizeMappingException;
import org.terracotta.offheapstore.paging.UpfrontAllocatingPageSource;
import org.terracotta.offheapstore.util.MemoryUnit;


public class OffHeapServerStoreTest extends ServerStoreTest {
    private static final KeySegmentMapper DEFAULT_MAPPER = new KeySegmentMapper(16);

    @Test
    public void testGetMaxSize() {
        MatcherAssert.assertThat(OffHeapServerStore.getMaxSize(MemoryUnit.MEGABYTES.toBytes(2)), Is.is(64L));
        MatcherAssert.assertThat(OffHeapServerStore.getMaxSize(MemoryUnit.MEGABYTES.toBytes(4)), Is.is(128L));
        MatcherAssert.assertThat(OffHeapServerStore.getMaxSize(MemoryUnit.MEGABYTES.toBytes(16)), Is.is(512L));
        MatcherAssert.assertThat(OffHeapServerStore.getMaxSize(MemoryUnit.MEGABYTES.toBytes(64)), Is.is(2048L));
        MatcherAssert.assertThat(OffHeapServerStore.getMaxSize(MemoryUnit.MEGABYTES.toBytes(128)), Is.is(4096L));
        MatcherAssert.assertThat(OffHeapServerStore.getMaxSize(MemoryUnit.MEGABYTES.toBytes(256)), Is.is(8192L));
        MatcherAssert.assertThat(OffHeapServerStore.getMaxSize(MemoryUnit.MEGABYTES.toBytes(512)), Is.is(8192L));
        MatcherAssert.assertThat(OffHeapServerStore.getMaxSize(MemoryUnit.GIGABYTES.toBytes(2)), Is.is(8192L));
    }

    @Test
    public void put_worked_the_first_time_test() throws Exception {
        OffHeapChainMap<Long> offheapChainMap = getOffHeapChainMapLongMock();
        ChainStorageEngine<Long> storageEngine = getChainStorageEngineLongMock();
        Mockito.when(offheapChainMap.getStorageEngine()).thenReturn(storageEngine);
        Mockito.doNothing().when(offheapChainMap).put(ArgumentMatchers.anyLong(), ArgumentMatchers.any(Chain.class));
        OffHeapServerStore offHeapServerStore = new OffHeapServerStore(Collections.singletonList(offheapChainMap), Mockito.mock(KeySegmentMapper.class));
        offHeapServerStore.put(43L, Mockito.mock(Chain.class));
    }

    @Test(expected = OversizeMappingException.class)
    public void put_should_throw_when_underlying_put_always_throw_test() throws Exception {
        OffHeapChainMap<Long> offheapChainMap = getOffHeapChainMapLongMock();
        ChainStorageEngine<Long> storageEngine = getChainStorageEngineLongMock();
        Mockito.when(offheapChainMap.getStorageEngine()).thenReturn(storageEngine);
        Mockito.when(offheapChainMap.writeLock()).thenReturn(new ReentrantLock());
        Mockito.doThrow(new OversizeMappingException()).when(offheapChainMap).put(ArgumentMatchers.anyLong(), ArgumentMatchers.any(Chain.class));
        OffHeapServerStore offHeapServerStore = new OffHeapServerStore(Collections.singletonList(offheapChainMap), Mockito.mock(KeySegmentMapper.class));
        offHeapServerStore.put(43L, Mockito.mock(Chain.class));
    }

    @Test
    public void put_should_return_when_underlying_put_does_not_throw_test() throws Exception {
        OffHeapChainMap<Long> offheapChainMap = getOffHeapChainMapLongMock();
        ChainStorageEngine<Long> storageEngine = getChainStorageEngineLongMock();
        Mockito.when(offheapChainMap.getStorageEngine()).thenReturn(storageEngine);
        Mockito.when(offheapChainMap.writeLock()).thenReturn(new ReentrantLock());
        // throw once, then ok
        Mockito.doThrow(new OversizeMappingException()).doNothing().when(offheapChainMap).put(ArgumentMatchers.anyLong(), ArgumentMatchers.any(Chain.class));
        OffHeapServerStore offHeapServerStore = new OffHeapServerStore(Collections.singletonList(offheapChainMap), Mockito.mock(KeySegmentMapper.class));
        offHeapServerStore.put(43L, Mockito.mock(Chain.class));
    }

    @Test
    public void put_should_return_when_underlying_put_does_not_throw_with_keymapper_test() throws Exception {
        long theKey = 43L;
        ChainStorageEngine<Long> storageEngine = getChainStorageEngineLongMock();
        OffHeapChainMap<Long> offheapChainMap = getOffHeapChainMapLongMock();
        OffHeapChainMap<Long> otherOffheapChainMap = getOffHeapChainMapLongMock();
        Mockito.when(offheapChainMap.shrink()).thenReturn(true);
        Mockito.when(offheapChainMap.getStorageEngine()).thenReturn(storageEngine);
        Mockito.when(offheapChainMap.writeLock()).thenReturn(new ReentrantLock());
        Mockito.when(otherOffheapChainMap.writeLock()).thenReturn(new ReentrantLock());
        // throw twice, then OK
        Mockito.doThrow(new OversizeMappingException()).doThrow(new OversizeMappingException()).doNothing().when(otherOffheapChainMap).put(ArgumentMatchers.anyLong(), ArgumentMatchers.any(Chain.class));
        KeySegmentMapper keySegmentMapper = Mockito.mock(KeySegmentMapper.class);
        Mockito.when(keySegmentMapper.getSegmentForKey(theKey)).thenReturn(1);
        OffHeapServerStore offHeapServerStore = new OffHeapServerStore(Arrays.asList(offheapChainMap, otherOffheapChainMap), keySegmentMapper);
        offHeapServerStore.put(theKey, Mockito.mock(Chain.class));
        // getSegmentForKey was called 4 times : segmentFor, handleOversizeMappingException, segmentFor, segmentFor
        Mockito.verify(keySegmentMapper, Mockito.times(4)).getSegmentForKey(theKey);
    }

    @Test
    public void test_append_doesNotConsumeBuffer_evenWhenOversizeMappingException() throws Exception {
        OffHeapServerStore store = ((OffHeapServerStore) (Mockito.spy(newStore())));
        final OffHeapChainMap<Object> offHeapChainMap = getOffHeapChainMapMock();
        Mockito.doThrow(OversizeMappingException.class).when(offHeapChainMap).append(ArgumentMatchers.any(Object.class), ArgumentMatchers.any(ByteBuffer.class));
        Mockito.when(store.segmentFor(ArgumentMatchers.anyLong())).then(new Answer<Object>() {
            int invocations = 0;

            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                if (((invocations)++) < 10) {
                    return offHeapChainMap;
                } else {
                    return invocation.callRealMethod();
                }
            }
        });
        Mockito.when(store.tryShrinkOthers(ArgumentMatchers.anyLong())).thenReturn(true);
        ByteBuffer payload = ChainUtils.createPayload(1L);
        store.append(1L, payload);
        MatcherAssert.assertThat(payload.remaining(), Is.is(8));
    }

    @Test
    public void test_getAndAppend_doesNotConsumeBuffer_evenWhenOversizeMappingException() throws Exception {
        OffHeapServerStore store = ((OffHeapServerStore) (Mockito.spy(newStore())));
        final OffHeapChainMap<Object> offHeapChainMap = getOffHeapChainMapMock();
        Mockito.doThrow(OversizeMappingException.class).when(offHeapChainMap).getAndAppend(ArgumentMatchers.any(), ArgumentMatchers.any(ByteBuffer.class));
        Mockito.when(store.segmentFor(ArgumentMatchers.anyLong())).then(new Answer<Object>() {
            int invocations = 0;

            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                if (((invocations)++) < 10) {
                    return offHeapChainMap;
                } else {
                    return invocation.callRealMethod();
                }
            }
        });
        Mockito.when(store.tryShrinkOthers(ArgumentMatchers.anyLong())).thenReturn(true);
        ByteBuffer payload = ChainUtils.createPayload(1L);
        store.getAndAppend(1L, payload);
        MatcherAssert.assertThat(payload.remaining(), Is.is(8));
        Chain expected = newChainBuilder().build(newElementBuilder().build(payload), newElementBuilder().build(payload));
        Chain update = newChainBuilder().build(newElementBuilder().build(payload));
        store.replaceAtHead(1L, expected, update);
        MatcherAssert.assertThat(payload.remaining(), Is.is(8));
    }

    @Test
    public void test_replaceAtHead_doesNotConsumeBuffer_evenWhenOversizeMappingException() throws Exception {
        OffHeapServerStore store = ((OffHeapServerStore) (Mockito.spy(newStore())));
        final OffHeapChainMap<Object> offHeapChainMap = getOffHeapChainMapMock();
        Mockito.doThrow(OversizeMappingException.class).when(offHeapChainMap).replaceAtHead(ArgumentMatchers.any(), ArgumentMatchers.any(Chain.class), ArgumentMatchers.any(Chain.class));
        Mockito.when(store.segmentFor(ArgumentMatchers.anyLong())).then(new Answer<Object>() {
            int invocations = 0;

            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                if (((invocations)++) < 10) {
                    return offHeapChainMap;
                } else {
                    return invocation.callRealMethod();
                }
            }
        });
        Mockito.when(store.tryShrinkOthers(ArgumentMatchers.anyLong())).thenReturn(true);
        ByteBuffer payload = ChainUtils.createPayload(1L);
        Chain expected = newChainBuilder().build(newElementBuilder().build(payload), newElementBuilder().build(payload));
        Chain update = newChainBuilder().build(newElementBuilder().build(payload));
        store.replaceAtHead(1L, expected, update);
        MatcherAssert.assertThat(payload.remaining(), Is.is(8));
    }

    @Test
    public void testCrossSegmentShrinking() {
        long seed = System.nanoTime();
        Random random = new Random(seed);
        try {
            OffHeapServerStore store = new OffHeapServerStore(new UpfrontAllocatingPageSource(new OffHeapBufferSource(), MemoryUnit.MEGABYTES.toBytes(1L), MemoryUnit.MEGABYTES.toBytes(1)), OffHeapServerStoreTest.DEFAULT_MAPPER, false);
            ByteBuffer smallValue = ByteBuffer.allocate(1024);
            for (int i = 0; i < 10000; i++) {
                try {
                    store.getAndAppend(random.nextInt(500), smallValue.duplicate());
                } catch (OversizeMappingException e) {
                    // ignore
                }
            }
            ByteBuffer largeValue = ByteBuffer.allocate((100 * 1024));
            for (int i = 0; i < 10000; i++) {
                try {
                    store.getAndAppend(random.nextInt(500), largeValue.duplicate());
                } catch (OversizeMappingException e) {
                    // ignore
                }
            }
        } catch (Throwable t) {
            throw ((AssertionError) (new AssertionError(("Failed with seed " + seed)).initCause(t)));
        }
    }

    @Test
    public void testServerSideUsageStats() {
        long maxBytes = MemoryUnit.MEGABYTES.toBytes(1);
        OffHeapServerStore store = new OffHeapServerStore(new UpfrontAllocatingPageSource(new OffHeapBufferSource(), maxBytes, MemoryUnit.MEGABYTES.toBytes(1)), new KeySegmentMapper(16), false);
        int oneKb = 1024;
        long smallLoopCount = 5;
        ByteBuffer smallValue = ByteBuffer.allocate(oneKb);
        for (long i = 0; i < smallLoopCount; i++) {
            store.getAndAppend(i, smallValue.duplicate());
        }
        Assert.assertThat(store.getAllocatedMemory(), Matchers.lessThanOrEqualTo(maxBytes));
        Assert.assertThat(store.getAllocatedMemory(), Matchers.greaterThanOrEqualTo((smallLoopCount * oneKb)));
        Assert.assertThat(store.getAllocatedMemory(), Matchers.greaterThanOrEqualTo(store.getOccupiedMemory()));
        // asserts above already guarantee that occupiedMemory <= maxBytes and that occupiedMemory <= allocatedMemory
        Assert.assertThat(store.getOccupiedMemory(), Matchers.greaterThanOrEqualTo((smallLoopCount * oneKb)));
        Assert.assertThat(store.getSize(), Is.is(smallLoopCount));
        int multiplier = 100;
        long largeLoopCount = 5 + smallLoopCount;
        ByteBuffer largeValue = ByteBuffer.allocate((multiplier * oneKb));
        for (long i = smallLoopCount; i < largeLoopCount; i++) {
            store.getAndAppend(i, largeValue.duplicate());
        }
        Assert.assertThat(store.getAllocatedMemory(), Matchers.lessThanOrEqualTo(maxBytes));
        Assert.assertThat(store.getAllocatedMemory(), Matchers.greaterThanOrEqualTo(((smallLoopCount * oneKb) + (((largeLoopCount - smallLoopCount) * oneKb) * multiplier))));
        Assert.assertThat(store.getAllocatedMemory(), Matchers.greaterThanOrEqualTo(store.getOccupiedMemory()));
        // asserts above already guarantee that occupiedMemory <= maxBytes and that occupiedMemory <= allocatedMemory
        Assert.assertThat(store.getOccupiedMemory(), Matchers.greaterThanOrEqualTo((smallLoopCount * oneKb)));
        Assert.assertThat(store.getSize(), Is.is((smallLoopCount + (largeLoopCount - smallLoopCount))));
    }
}

