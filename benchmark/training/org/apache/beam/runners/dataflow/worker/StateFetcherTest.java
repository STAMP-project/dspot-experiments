/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.beam.runners.dataflow.worker;


import ByteString.EMPTY;
import ByteString.Output;
import Coder.Context.OUTER;
import GlobalWindow.INSTANCE;
import SideInputState.KNOWN_READY;
import SideInputState.UNKNOWN;
import StateFetcher.SideInputCacheEntry;
import StateFetcher.SideInputId;
import Windmill.GlobalDataRequest;
import java.io.Closeable;
import java.util.Arrays;
import java.util.List;
import org.apache.beam.runners.dataflow.worker.windmill.Windmill;
import org.apache.beam.sdk.coders.Coder;
import org.apache.beam.sdk.coders.ListCoder;
import org.apache.beam.sdk.coders.StringUtf8Coder;
import org.apache.beam.sdk.coders.VarLongCoder;
import org.apache.beam.sdk.coders.VoidCoder;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.transforms.Sum;
import org.apache.beam.sdk.transforms.View;
import org.apache.beam.sdk.values.PCollectionView;
import org.apache.beam.vendor.grpc.v1p13p1.com.google.protobuf.ByteString;
import org.apache.beam.vendor.guava.v20_0.com.google.common.base.Supplier;
import org.apache.beam.vendor.guava.v20_0.com.google.common.cache.Cache;
import org.apache.beam.vendor.guava.v20_0.com.google.common.cache.CacheBuilder;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Unit tests for {@link StateFetcher}.
 */
@RunWith(JUnit4.class)
public class StateFetcherTest {
    private static final String STATE_FAMILY = "state";

    @Mock
    MetricTrackingWindmillServerStub server;

    @Mock
    Supplier<Closeable> readStateSupplier;

    @Test
    public void testFetchGlobalDataBasic() throws Exception {
        StateFetcher fetcher = new StateFetcher(server);
        ByteString.Output stream = ByteString.newOutput();
        ListCoder.of(StringUtf8Coder.of()).encode(Arrays.asList("data"), stream, OUTER);
        ByteString encodedIterable = stream.toByteString();
        PCollectionView<String> view = TestPipeline.create().apply(Create.empty(StringUtf8Coder.of())).apply(View.<String>asSingleton());
        String tag = view.getTagInternal().getId();
        // Test three calls in a row. First, data is not ready, then data is ready,
        // then the data is already cached.
        Mockito.when(server.getSideInputData(ArgumentMatchers.any(GlobalDataRequest.class))).thenReturn(buildGlobalDataResponse(tag, EMPTY, false, null), buildGlobalDataResponse(tag, EMPTY, true, encodedIterable));
        Assert.assertEquals(null, fetcher.fetchSideInput(view, INSTANCE, StateFetcherTest.STATE_FAMILY, UNKNOWN, readStateSupplier));
        Assert.assertEquals(null, fetcher.fetchSideInput(view, INSTANCE, StateFetcherTest.STATE_FAMILY, UNKNOWN, readStateSupplier));
        Assert.assertEquals("data", fetcher.fetchSideInput(view, INSTANCE, StateFetcherTest.STATE_FAMILY, KNOWN_READY, readStateSupplier).orNull());
        Assert.assertEquals("data", fetcher.fetchSideInput(view, INSTANCE, StateFetcherTest.STATE_FAMILY, KNOWN_READY, readStateSupplier).orNull());
        Mockito.verify(server, Mockito.times(2)).getSideInputData(buildGlobalDataRequest(tag, EMPTY));
        Mockito.verifyNoMoreInteractions(server);
    }

    @Test
    public void testFetchGlobalDataNull() throws Exception {
        StateFetcher fetcher = new StateFetcher(server);
        ByteString.Output stream = ByteString.newOutput();
        ListCoder.of(VoidCoder.of()).encode(Arrays.asList(((Void) (null))), stream, OUTER);
        ByteString encodedIterable = stream.toByteString();
        PCollectionView<Void> view = TestPipeline.create().apply(Create.empty(VoidCoder.of())).apply(View.<Void>asSingleton());
        String tag = view.getTagInternal().getId();
        // Test three calls in a row. First, data is not ready, then data is ready,
        // then the data is already cached.
        Mockito.when(server.getSideInputData(ArgumentMatchers.any(GlobalDataRequest.class))).thenReturn(buildGlobalDataResponse(tag, EMPTY, false, null), buildGlobalDataResponse(tag, EMPTY, true, encodedIterable));
        Assert.assertEquals(null, fetcher.fetchSideInput(view, INSTANCE, StateFetcherTest.STATE_FAMILY, UNKNOWN, readStateSupplier));
        Assert.assertEquals(null, fetcher.fetchSideInput(view, INSTANCE, StateFetcherTest.STATE_FAMILY, UNKNOWN, readStateSupplier));
        Assert.assertEquals(null, fetcher.fetchSideInput(view, INSTANCE, StateFetcherTest.STATE_FAMILY, KNOWN_READY, readStateSupplier).orNull());
        Assert.assertEquals(null, fetcher.fetchSideInput(view, INSTANCE, StateFetcherTest.STATE_FAMILY, KNOWN_READY, readStateSupplier).orNull());
        Mockito.verify(server, Mockito.times(2)).getSideInputData(buildGlobalDataRequest(tag, EMPTY));
        Mockito.verifyNoMoreInteractions(server);
    }

    @Test
    public void testFetchGlobalDataCacheOverflow() throws Exception {
        Coder<List<String>> coder = ListCoder.of(StringUtf8Coder.of());
        ByteString.Output stream = ByteString.newOutput();
        coder.encode(Arrays.asList("data1"), stream, OUTER);
        ByteString encodedIterable1 = stream.toByteString();
        stream = ByteString.newOutput();
        coder.encode(Arrays.asList("data2"), stream, OUTER);
        ByteString encodedIterable2 = stream.toByteString();
        Cache<StateFetcher.SideInputId, StateFetcher.SideInputCacheEntry> cache = CacheBuilder.newBuilder().build();
        StateFetcher fetcher = new StateFetcher(server, cache);
        PCollectionView<String> view1 = TestPipeline.create().apply(Create.empty(StringUtf8Coder.of())).apply(View.<String>asSingleton());
        PCollectionView<String> view2 = TestPipeline.create().apply(Create.empty(StringUtf8Coder.of())).apply(View.<String>asSingleton());
        String tag1 = view1.getTagInternal().getId();
        String tag2 = view2.getTagInternal().getId();
        // Test four calls in a row. First, fetch view1, then view2 (which evicts view1 from the cache),
        // then view 1 again twice.
        Mockito.when(server.getSideInputData(ArgumentMatchers.any(GlobalDataRequest.class))).thenReturn(buildGlobalDataResponse(tag1, EMPTY, true, encodedIterable1), buildGlobalDataResponse(tag2, EMPTY, true, encodedIterable2), buildGlobalDataResponse(tag1, EMPTY, true, encodedIterable1));
        Assert.assertEquals("data1", fetcher.fetchSideInput(view1, INSTANCE, StateFetcherTest.STATE_FAMILY, UNKNOWN, readStateSupplier).orNull());
        Assert.assertEquals("data2", fetcher.fetchSideInput(view2, INSTANCE, StateFetcherTest.STATE_FAMILY, UNKNOWN, readStateSupplier).orNull());
        cache.invalidateAll();
        Assert.assertEquals("data1", fetcher.fetchSideInput(view1, INSTANCE, StateFetcherTest.STATE_FAMILY, UNKNOWN, readStateSupplier).orNull());
        Assert.assertEquals("data1", fetcher.fetchSideInput(view1, INSTANCE, StateFetcherTest.STATE_FAMILY, UNKNOWN, readStateSupplier).orNull());
        ArgumentCaptor<Windmill.GlobalDataRequest> captor = ArgumentCaptor.forClass(GlobalDataRequest.class);
        Mockito.verify(server, Mockito.times(3)).getSideInputData(captor.capture());
        Mockito.verifyNoMoreInteractions(server);
        Assert.assertThat(captor.getAllValues(), Matchers.contains(buildGlobalDataRequest(tag1, EMPTY), buildGlobalDataRequest(tag2, EMPTY), buildGlobalDataRequest(tag1, EMPTY)));
    }

    @Test
    public void testEmptyFetchGlobalData() throws Exception {
        StateFetcher fetcher = new StateFetcher(server);
        ByteString encodedIterable = ByteString.EMPTY;
        PCollectionView<Long> view = TestPipeline.create().apply(Create.empty(VarLongCoder.of())).apply(Sum.longsGlobally().asSingletonView());
        String tag = view.getTagInternal().getId();
        // Test three calls in a row. First, data is not ready, then data is ready,
        // then the data is already cached.
        Mockito.when(server.getSideInputData(ArgumentMatchers.any(GlobalDataRequest.class))).thenReturn(buildGlobalDataResponse(tag, EMPTY, true, encodedIterable));
        Assert.assertEquals(0L, ((long) (fetcher.fetchSideInput(view, INSTANCE, StateFetcherTest.STATE_FAMILY, UNKNOWN, readStateSupplier).orNull())));
        Mockito.verify(server).getSideInputData(buildGlobalDataRequest(tag, EMPTY));
        Mockito.verifyNoMoreInteractions(server);
    }
}

