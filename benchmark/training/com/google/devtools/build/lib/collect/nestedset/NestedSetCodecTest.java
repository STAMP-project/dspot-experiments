/**
 * Copyright 2017 The Bazel Authors. All rights reserved.
 */
/**
 *
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
package com.google.devtools.build.lib.collect.nestedset;


import NestedSetStore.FingerprintComputationResult;
import Order.STABLE_ORDER;
import com.google.common.collect.ImmutableMap;
import com.google.common.testing.GcFinalization;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.SettableFuture;
import com.google.devtools.build.lib.collect.nestedset.NestedSetStore.InMemoryNestedSetStorageEndpoint;
import com.google.devtools.build.lib.collect.nestedset.NestedSetStore.NestedSetCache;
import com.google.devtools.build.lib.collect.nestedset.NestedSetStore.NestedSetStorageEndpoint;
import com.google.devtools.build.lib.skyframe.serialization.AutoRegistry;
import com.google.devtools.build.lib.skyframe.serialization.DeserializationContext;
import com.google.devtools.build.lib.skyframe.serialization.ObjectCodecs;
import com.google.devtools.build.lib.skyframe.serialization.SerializationContext;
import com.google.devtools.build.lib.skyframe.serialization.SerializationException;
import com.google.devtools.build.lib.skyframe.serialization.SerializationResult;
import com.google.protobuf.ByteString;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.charset.Charset;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static Order.STABLE_ORDER;


/**
 * Tests for {@link NestedSet} serialization.
 */
@RunWith(JUnit4.class)
public class NestedSetCodecTest {
    @Test
    public void testAutoCodecedCodec() throws Exception {
        ObjectCodecs objectCodecs = new ObjectCodecs(AutoRegistry.get().getBuilder().setAllowDefaultCodec(true).build(), ImmutableMap.of());
        NestedSetCodecTestUtils.checkCodec(objectCodecs, false, false);
    }

    @Test
    public void testCodecWithInMemoryNestedSetStore() throws Exception {
        ObjectCodecs objectCodecs = new ObjectCodecs(AutoRegistry.get().getBuilder().setAllowDefaultCodec(true).add(new NestedSetCodecWithStore(NestedSetStore.inMemory())).build(), ImmutableMap.of());
        NestedSetCodecTestUtils.checkCodec(objectCodecs, true, true);
    }

    /**
     * Tests that serialization of a {@code NestedSet<NestedSet<String>>} waits on the writes of the
     * inner NestedSets.
     */
    @Test
    public void testNestedNestedSetSerialization() throws Exception {
        NestedSetStorageEndpoint mockStorage = Mockito.mock(NestedSetStorageEndpoint.class);
        SettableFuture<Void> innerWrite = SettableFuture.create();
        SettableFuture<Void> outerWrite = SettableFuture.create();
        // The write of the outer NestedSet {{"a", "b"}, {"c", "d"}}
        // The write of the inner NestedSet {"c", "d"}
        // The write of the inner NestedSet {"a", "b"}
        Mockito.when(mockStorage.put(Mockito.any(), Mockito.any())).thenReturn(innerWrite).thenReturn(innerWrite).thenReturn(outerWrite);
        NestedSetStore nestedSetStore = new NestedSetStore(mockStorage);
        ObjectCodecs objectCodecs = new ObjectCodecs(AutoRegistry.get().getBuilder().setAllowDefaultCodec(true).add(new NestedSetCodecWithStore(nestedSetStore)).build(), ImmutableMap.of());
        NestedSet<NestedSet<String>> nestedNestedSet = NestedSetBuilder.create(STABLE_ORDER, NestedSetBuilder.create(STABLE_ORDER, "a", "b"), NestedSetBuilder.create(STABLE_ORDER, "c", "d"));
        SerializationResult<ByteString> result = objectCodecs.serializeMemoizedAndBlocking(nestedNestedSet);
        outerWrite.set(null);
        assertThat(result.getFutureToBlockWritesOn().isDone()).isFalse();
        innerWrite.set(null);
        assertThat(result.getFutureToBlockWritesOn().isDone()).isTrue();
    }

    @Test
    public void testNestedNestedSetsWithCommonDependencyWaitOnSameInnerFuture() throws Exception {
        NestedSetStorageEndpoint mockStorage = Mockito.mock(NestedSetStorageEndpoint.class);
        SettableFuture<Void> sharedInnerWrite = SettableFuture.create();
        SettableFuture<Void> outerWrite = SettableFuture.create();
        // The write of the inner NestedSet {"e", "f"}
        // The write of the outer NestedSet {{"a", "b"}, {"c", "d"}}
        // The write of the inner NestedSet {"c", "d"}
        // The write of the shared inner NestedSet {"a", "b"}
        Mockito.when(mockStorage.put(Mockito.any(), Mockito.any())).thenReturn(sharedInnerWrite).thenReturn(Futures.immediateFuture(null)).thenReturn(outerWrite).thenReturn(Futures.immediateFuture(null));
        NestedSetStore nestedSetStore = new NestedSetStore(mockStorage);
        ObjectCodecs objectCodecs = new ObjectCodecs(AutoRegistry.get().getBuilder().setAllowDefaultCodec(true).add(new NestedSetCodecWithStore(nestedSetStore)).build(), ImmutableMap.of());
        NestedSet<String> sharedInnerNestedSet = NestedSetBuilder.create(STABLE_ORDER, "a", "b");
        NestedSet<NestedSet<String>> nestedNestedSet1 = NestedSetBuilder.create(STABLE_ORDER, sharedInnerNestedSet, NestedSetBuilder.create(STABLE_ORDER, "c", "d"));
        NestedSet<NestedSet<String>> nestedNestedSet2 = NestedSetBuilder.create(STABLE_ORDER, sharedInnerNestedSet, NestedSetBuilder.create(STABLE_ORDER, "e", "f"));
        SerializationResult<ByteString> result1 = objectCodecs.serializeMemoizedAndBlocking(nestedNestedSet1);
        SerializationResult<ByteString> result2 = objectCodecs.serializeMemoizedAndBlocking(nestedNestedSet2);
        outerWrite.set(null);
        assertThat(result1.getFutureToBlockWritesOn().isDone()).isFalse();
        assertThat(result2.getFutureToBlockWritesOn().isDone()).isFalse();
        sharedInnerWrite.set(null);
        assertThat(result1.getFutureToBlockWritesOn().isDone()).isTrue();
        assertThat(result2.getFutureToBlockWritesOn().isDone()).isTrue();
    }

    @Test
    public void testSingletonNestedSetSerializedWithoutStore() throws Exception {
        NestedSetStore mockNestedSetStore = Mockito.mock(NestedSetStore.class);
        Mockito.when(mockNestedSetStore.computeFingerprintAndStore(Mockito.any(), Mockito.any())).thenThrow(new AssertionError("NestedSetStore should not have been used"));
        ObjectCodecs objectCodecs = new ObjectCodecs(AutoRegistry.get().getBuilder().setAllowDefaultCodec(true).add(new NestedSetCodecWithStore(mockNestedSetStore)).build());
        NestedSet<String> singletonNestedSet = new NestedSetBuilder<String>(STABLE_ORDER).add("a").build();
        objectCodecs.serialize(singletonNestedSet);
    }

    @Test
    public void cacheEntryHasLifetimeOfContents() {
        NestedSetCache cache = new NestedSetCache();
        Object[] contents = new Object[0];
        ByteString fingerprint = ByteString.copyFrom(new byte[2]);
        cache.put(FingerprintComputationResult.create(fingerprint, Futures.immediateFuture(null)), contents);
        GcFinalization.awaitFullGc();
        assertThat(cache.putIfAbsent(fingerprint, Futures.immediateFuture(null))).isEqualTo(contents);
        WeakReference<Object[]> weakRef = new WeakReference<>(contents);
        contents = null;
        fingerprint = null;
        GcFinalization.awaitClear(weakRef);
    }

    @Test
    public void testDeserializationInParallel() throws Exception {
        NestedSetStorageEndpoint nestedSetStorageEndpoint = Mockito.spy(new InMemoryNestedSetStorageEndpoint());
        NestedSetCache emptyNestedSetCache = Mockito.mock(NestedSetCache.class);
        NestedSetStore nestedSetStore = new NestedSetStore(nestedSetStorageEndpoint, emptyNestedSetCache, MoreExecutors.directExecutor());
        ObjectCodecs objectCodecs = new ObjectCodecs(AutoRegistry.get().getBuilder().setAllowDefaultCodec(true).add(new NestedSetCodecWithStore(nestedSetStore)).build());
        NestedSet<String> subset1 = new NestedSetBuilder<String>(STABLE_ORDER).add("a").add("b").build();
        SettableFuture<byte[]> subset1Future = SettableFuture.create();
        NestedSet<String> subset2 = new NestedSetBuilder<String>(STABLE_ORDER).add("c").add("d").build();
        SettableFuture<byte[]> subset2Future = SettableFuture.create();
        NestedSet<String> set = new NestedSetBuilder<String>(STABLE_ORDER).addTransitive(subset1).addTransitive(subset2).build();
        // We capture the arguments to #put() during serialization, so as to correctly mock results for
        // #get()
        ArgumentCaptor<ByteString> fingerprintCaptor = ArgumentCaptor.forClass(ByteString.class);
        ByteString fingerprint = nestedSetStore.computeFingerprintAndStore(((Object[]) (set.getChildren())), objectCodecs.getSerializationContext()).fingerprint();
        Mockito.verify(nestedSetStorageEndpoint, Mockito.times(3)).put(fingerprintCaptor.capture(), Mockito.any());
        Mockito.doReturn(subset1Future).when(nestedSetStorageEndpoint).get(fingerprintCaptor.getAllValues().get(0));
        Mockito.doReturn(subset2Future).when(nestedSetStorageEndpoint).get(fingerprintCaptor.getAllValues().get(1));
        Mockito.when(emptyNestedSetCache.putIfAbsent(Mockito.any(), Mockito.any())).thenAnswer(( invocation) -> null);
        @SuppressWarnings("unchecked")
        ListenableFuture<Object[]> deserializationFuture = ((ListenableFuture<Object[]>) (nestedSetStore.getContentsAndDeserialize(fingerprint, objectCodecs.getDeserializationContext())));
        // At this point, we expect deserializationFuture to be waiting on both of the underlying
        // fetches, which should have both been started.
        assertThat(deserializationFuture.isDone()).isFalse();
        Mockito.verify(nestedSetStorageEndpoint, Mockito.times(3)).get(Mockito.any());
        // Once the underlying fetches complete, we expect deserialization to complete.
        subset1Future.set(ByteString.copyFrom("mock bytes", Charset.defaultCharset()).toByteArray());
        subset2Future.set(ByteString.copyFrom("mock bytes", Charset.defaultCharset()).toByteArray());
        assertThat(deserializationFuture.isDone()).isTrue();
    }

    @Test
    public void racingDeserialization() throws Exception {
        NestedSetStorageEndpoint nestedSetStorageEndpoint = Mockito.mock(NestedSetStorageEndpoint.class);
        NestedSetCache nestedSetCache = Mockito.spy(new NestedSetCache());
        NestedSetStore nestedSetStore = new NestedSetStore(nestedSetStorageEndpoint, nestedSetCache, MoreExecutors.directExecutor());
        DeserializationContext deserializationContext = Mockito.mock(DeserializationContext.class);
        ByteString fingerprint = ByteString.copyFromUtf8("fingerprint");
        // Future never completes, so we don't have to exercise that code in NestedSetStore.
        SettableFuture<byte[]> storageFuture = SettableFuture.create();
        Mockito.when(nestedSetStorageEndpoint.get(fingerprint)).thenReturn(storageFuture);
        CountDownLatch fingerprintRequested = new CountDownLatch(2);
        Mockito.doAnswer(( invocation) -> {
            fingerprintRequested.countDown();
            @SuppressWarnings("unchecked")
            ListenableFuture<Object[]> result = ((ListenableFuture<Object[]>) (invocation.callRealMethod()));
            fingerprintRequested.await();
            return result;
        }).when(nestedSetCache).putIfAbsent(Mockito.eq(fingerprint), Mockito.any());
        AtomicReference<ListenableFuture<Object[]>> asyncResult = new AtomicReference<>();
        Thread asyncThread = new Thread(() -> {
            try {
                @SuppressWarnings("unchecked")
                ListenableFuture<Object[]> asyncContents = ((ListenableFuture<Object[]>) (nestedSetStore.getContentsAndDeserialize(fingerprint, deserializationContext)));
                asyncResult.set(asyncContents);
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        });
        asyncThread.start();
        @SuppressWarnings("unchecked")
        ListenableFuture<Object[]> result = ((ListenableFuture<Object[]>) (nestedSetStore.getContentsAndDeserialize(fingerprint, deserializationContext)));
        asyncThread.join();
        Mockito.verify(nestedSetStorageEndpoint, Mockito.times(1)).get(Mockito.eq(fingerprint));
        assertThat(result).isSameAs(asyncResult.get());
        assertThat(result.isDone()).isFalse();
    }

    @Test
    public void bugInRacingSerialization() throws Exception {
        NestedSetStorageEndpoint nestedSetStorageEndpoint = Mockito.mock(NestedSetStorageEndpoint.class);
        NestedSetCache nestedSetCache = Mockito.spy(new NestedSetCache());
        NestedSetStore nestedSetStore = new NestedSetStore(nestedSetStorageEndpoint, nestedSetCache, MoreExecutors.directExecutor());
        SerializationContext serializationContext = Mockito.mock(SerializationContext.class);
        Object[] contents = new Object[]{ new Object() };
        Mockito.when(serializationContext.getNewMemoizingContext()).thenReturn(serializationContext);
        Mockito.when(nestedSetStorageEndpoint.put(Mockito.any(), Mockito.any())).thenAnswer(( invocation) -> SettableFuture.create());
        CountDownLatch fingerprintRequested = new CountDownLatch(2);
        Mockito.doAnswer(( invocation) -> {
            fingerprintRequested.countDown();
            NestedSetStore.FingerprintComputationResult result = ((NestedSetStore.FingerprintComputationResult) (invocation.callRealMethod()));
            assertThat(result).isNull();
            fingerprintRequested.await();
            return null;
        }).when(nestedSetCache).fingerprintForContents(contents);
        AtomicReference<NestedSetStore.FingerprintComputationResult> asyncResult = new AtomicReference<>();
        Thread asyncThread = new Thread(() -> {
            try {
                asyncResult.set(nestedSetStore.computeFingerprintAndStore(contents, serializationContext));
            } catch (IOException | SerializationException e) {
                throw new IllegalStateException(e);
            }
        });
        asyncThread.start();
        NestedSetStore.FingerprintComputationResult result = nestedSetStore.computeFingerprintAndStore(contents, serializationContext);
        asyncThread.join();
        // TODO(janakr): This should be one fetch, but we currently do two.
        Mockito.verify(nestedSetStorageEndpoint, Mockito.times(2)).put(Mockito.any(), Mockito.any());
        // TODO(janakr): These should be the same element.
        assertThat(result).isNotEqualTo(asyncResult.get());
    }
}

