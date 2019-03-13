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
package org.apache.beam.sdk.io.gcp.spanner;


import SpannerIO.FailureMode.REPORT_FAILURES;
import SpannerIO.Write;
import com.google.cloud.spanner.Key;
import com.google.cloud.spanner.KeyRange;
import com.google.cloud.spanner.KeySet;
import com.google.cloud.spanner.Mutation;
import com.google.cloud.spanner.SpannerExceptionFactory;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.beam.sdk.coders.SerializableCoder;
import org.apache.beam.sdk.io.gcp.spanner.SpannerIO.BatchFn;
import org.apache.beam.sdk.io.gcp.spanner.SpannerIO.BatchableMutationFilterFn;
import org.apache.beam.sdk.io.gcp.spanner.SpannerIO.GatherBundleAndSortFn;
import org.apache.beam.sdk.io.gcp.spanner.SpannerIO.WriteGrouped;
import org.apache.beam.sdk.testing.NeedsRunner;
import org.apache.beam.sdk.testing.PAssert;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.testing.TestStream;
import org.apache.beam.sdk.testing.UsesTestStream;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.transforms.DoFn.FinishBundleContext;
import org.apache.beam.sdk.transforms.DoFn.ProcessContext;
import org.apache.beam.sdk.transforms.display.DisplayData;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.vendor.guava.v20_0.com.google.common.base.Preconditions;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.Iterables;
import org.hamcrest.Matchers;
import org.joda.time.Duration;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mockito;


/**
 * Unit tests for {@link SpannerIO}.
 *
 * <p>Note that because batching and sorting work on Bundles, and the TestPipeline does not bundle
 * small numbers of elements, the batching and sorting DoFns need to be unit tested outside of the
 * pipeline.
 */
@RunWith(JUnit4.class)
public class SpannerIOWriteTest implements Serializable {
    private static final long CELLS_PER_KEY = 7;

    @Rule
    public transient TestPipeline pipeline = TestPipeline.create();

    @Rule
    public transient ExpectedException thrown = ExpectedException.none();

    @Captor
    public transient ArgumentCaptor<Iterable<Mutation>> mutationBatchesCaptor;

    @Captor
    public transient ArgumentCaptor<Iterable<MutationGroup>> mutationGroupListCaptor;

    @Captor
    public transient ArgumentCaptor<MutationGroup> mutationGroupCaptor;

    @Captor
    public transient ArgumentCaptor<List<KV<byte[], byte[]>>> byteArrayKvListCaptor;

    private FakeServiceFactory serviceFactory;

    @Test
    public void emptyTransform() throws Exception {
        SpannerIO.Write write = SpannerIO.write();
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("requires instance id to be set with");
        write.expand(null);
    }

    @Test
    public void emptyInstanceId() throws Exception {
        SpannerIO.Write write = SpannerIO.write().withDatabaseId("123");
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("requires instance id to be set with");
        write.expand(null);
    }

    @Test
    public void emptyDatabaseId() throws Exception {
        SpannerIO.Write write = SpannerIO.write().withInstanceId("123");
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("requires database id to be set with");
        write.expand(null);
    }

    @Test
    @Category(NeedsRunner.class)
    public void singleMutationPipeline() throws Exception {
        Mutation mutation = SpannerIOWriteTest.m(2L);
        PCollection<Mutation> mutations = pipeline.apply(Create.of(mutation));
        mutations.apply(SpannerIO.write().withProjectId("test-project").withInstanceId("test-instance").withDatabaseId("test-database").withServiceFactory(serviceFactory));
        pipeline.run();
        verifyBatches(SpannerIOWriteTest.batch(SpannerIOWriteTest.m(2L)));
    }

    @Test
    @Category(NeedsRunner.class)
    public void singleMutationGroupPipeline() throws Exception {
        PCollection<MutationGroup> mutations = pipeline.apply(Create.<MutationGroup>of(SpannerIOWriteTest.g(SpannerIOWriteTest.m(1L), SpannerIOWriteTest.m(2L), SpannerIOWriteTest.m(3L))));
        mutations.apply(SpannerIO.write().withProjectId("test-project").withInstanceId("test-instance").withDatabaseId("test-database").withServiceFactory(serviceFactory).grouped());
        pipeline.run();
        verifyBatches(SpannerIOWriteTest.batch(SpannerIOWriteTest.m(1L), SpannerIOWriteTest.m(2L), SpannerIOWriteTest.m(3L)));
    }

    @Test
    @Category(NeedsRunner.class)
    public void noBatching() throws Exception {
        PCollection<MutationGroup> mutations = pipeline.apply(Create.of(SpannerIOWriteTest.g(SpannerIOWriteTest.m(1L)), SpannerIOWriteTest.g(SpannerIOWriteTest.m(2L))));
        mutations.apply(SpannerIO.write().withProjectId("test-project").withInstanceId("test-instance").withDatabaseId("test-database").withServiceFactory(serviceFactory).withBatchSizeBytes(1).grouped());
        pipeline.run();
        verifyBatches(SpannerIOWriteTest.batch(SpannerIOWriteTest.m(1L)), SpannerIOWriteTest.batch(SpannerIOWriteTest.m(2L)));
    }

    @Test
    @Category({ NeedsRunner.class, UsesTestStream.class })
    public void streamingWrites() throws Exception {
        TestStream<Mutation> testStream = TestStream.create(SerializableCoder.of(Mutation.class)).addElements(SpannerIOWriteTest.m(1L), SpannerIOWriteTest.m(2L)).advanceProcessingTime(Duration.standardMinutes(1)).addElements(SpannerIOWriteTest.m(3L), SpannerIOWriteTest.m(4L)).advanceProcessingTime(Duration.standardMinutes(1)).addElements(SpannerIOWriteTest.m(5L), SpannerIOWriteTest.m(6L)).advanceWatermarkToInfinity();
        pipeline.apply(testStream).apply(SpannerIO.write().withProjectId("test-project").withInstanceId("test-instance").withDatabaseId("test-database").withServiceFactory(serviceFactory));
        pipeline.run();
        verifyBatches(SpannerIOWriteTest.batch(SpannerIOWriteTest.m(1L), SpannerIOWriteTest.m(2L)), SpannerIOWriteTest.batch(SpannerIOWriteTest.m(3L), SpannerIOWriteTest.m(4L)), SpannerIOWriteTest.batch(SpannerIOWriteTest.m(5L), SpannerIOWriteTest.m(6L)));
    }

    @Test
    @Category(NeedsRunner.class)
    public void reportFailures() throws Exception {
        MutationGroup[] mutationGroups = new MutationGroup[10];
        for (int i = 0; i < (mutationGroups.length); i++) {
            mutationGroups[i] = SpannerIOWriteTest.g(SpannerIOWriteTest.m(((long) (i))));
        }
        List<MutationGroup> mutationGroupList = Arrays.asList(mutationGroups);
        Mockito.when(serviceFactory.mockDatabaseClient().writeAtLeastOnce(ArgumentMatchers.any())).thenAnswer(( invocationOnMock) -> {
            Preconditions.checkNotNull(invocationOnMock.getArguments()[0]);
            throw SpannerExceptionFactory.newSpannerException(ErrorCode.ALREADY_EXISTS, "oops");
        });
        SpannerWriteResult result = pipeline.apply(Create.of(mutationGroupList)).apply(SpannerIO.write().withProjectId("test-project").withInstanceId("test-instance").withDatabaseId("test-database").withServiceFactory(serviceFactory).withBatchSizeBytes(0).withFailureMode(REPORT_FAILURES).grouped());
        PAssert.that(result.getFailedMutations()).satisfies(( m) -> {
            assertEquals(mutationGroups.length, Iterables.size(m));
            return null;
        });
        PAssert.that(result.getFailedMutations()).containsInAnyOrder(mutationGroupList);
        pipeline.run().waitUntilFinish();
        // writeAtLeastOnce called once for the batch of mutations
        // (which as they are unbatched = each mutation group) then again for the individual retry.
        Mockito.verify(serviceFactory.mockDatabaseClient(), Mockito.times(20)).writeAtLeastOnce(ArgumentMatchers.any());
    }

    @Test
    public void displayData() throws Exception {
        SpannerIO.Write write = SpannerIO.write().withProjectId("test-project").withInstanceId("test-instance").withDatabaseId("test-database").withBatchSizeBytes(123);
        DisplayData data = DisplayData.from(write);
        Assert.assertThat(data.items(), Matchers.hasSize(4));
        Assert.assertThat(data, hasDisplayItem("projectId", "test-project"));
        Assert.assertThat(data, hasDisplayItem("instanceId", "test-instance"));
        Assert.assertThat(data, hasDisplayItem("databaseId", "test-database"));
        Assert.assertThat(data, hasDisplayItem("batchSizeBytes", 123));
    }

    @Test
    public void testBatchableMutationFilterFn_cells() {
        Mutation all = Mutation.delete("test", KeySet.all());
        Mutation prefix = Mutation.delete("test", KeySet.prefixRange(Key.of(1L)));
        Mutation range = Mutation.delete("test", KeySet.range(KeyRange.openOpen(Key.of(1L), Key.newBuilder().build())));
        MutationGroup[] mutationGroups = new MutationGroup[]{ SpannerIOWriteTest.g(SpannerIOWriteTest.m(1L)), SpannerIOWriteTest.g(SpannerIOWriteTest.m(2L), SpannerIOWriteTest.m(3L)), SpannerIOWriteTest.g(SpannerIOWriteTest.m(2L), SpannerIOWriteTest.m(3L), SpannerIOWriteTest.m(4L), SpannerIOWriteTest.m(5L))// not batchable - too big.
        , SpannerIOWriteTest.g(SpannerIOWriteTest.del(1L)), SpannerIOWriteTest.g(SpannerIOWriteTest.del(5L, 6L))// not point delete.
        , SpannerIOWriteTest.g(all), SpannerIOWriteTest.g(prefix), SpannerIOWriteTest.g(range) };
        BatchableMutationFilterFn testFn = new BatchableMutationFilterFn(null, null, 10000000, (3 * (SpannerIOWriteTest.CELLS_PER_KEY)));
        ProcessContext mockProcessContext = Mockito.mock(ProcessContext.class);
        Mockito.when(mockProcessContext.sideInput(ArgumentMatchers.any())).thenReturn(getSchema());
        // Capture the outputs.
        Mockito.doNothing().when(mockProcessContext).output(mutationGroupCaptor.capture());
        Mockito.doNothing().when(mockProcessContext).output(ArgumentMatchers.any(), mutationGroupListCaptor.capture());
        // Process all elements.
        for (MutationGroup m : mutationGroups) {
            Mockito.when(mockProcessContext.element()).thenReturn(m);
            testFn.processElement(mockProcessContext);
        }
        // Verify captured batchable elements.
        Assert.assertThat(mutationGroupCaptor.getAllValues(), Matchers.containsInAnyOrder(SpannerIOWriteTest.g(SpannerIOWriteTest.m(1L)), SpannerIOWriteTest.g(SpannerIOWriteTest.m(2L), SpannerIOWriteTest.m(3L)), SpannerIOWriteTest.g(SpannerIOWriteTest.del(1L))));
        // Verify captured unbatchable mutations
        Iterable<MutationGroup> unbatchableMutations = Iterables.concat(mutationGroupListCaptor.getAllValues());
        Assert.assertThat(unbatchableMutations, // not batchable - too big.
        // not point delete.
        Matchers.containsInAnyOrder(SpannerIOWriteTest.g(SpannerIOWriteTest.m(2L), SpannerIOWriteTest.m(3L), SpannerIOWriteTest.m(4L), SpannerIOWriteTest.m(5L)), SpannerIOWriteTest.g(SpannerIOWriteTest.del(5L, 6L)), SpannerIOWriteTest.g(all), SpannerIOWriteTest.g(prefix), SpannerIOWriteTest.g(range)));
    }

    @Test
    public void testBatchableMutationFilterFn_size() {
        Mutation all = Mutation.delete("test", KeySet.all());
        Mutation prefix = Mutation.delete("test", KeySet.prefixRange(Key.of(1L)));
        Mutation range = Mutation.delete("test", KeySet.range(KeyRange.openOpen(Key.of(1L), Key.newBuilder().build())));
        MutationGroup[] mutationGroups = new MutationGroup[]{ SpannerIOWriteTest.g(SpannerIOWriteTest.m(1L)), SpannerIOWriteTest.g(SpannerIOWriteTest.m(2L), SpannerIOWriteTest.m(3L)), SpannerIOWriteTest.g(SpannerIOWriteTest.m(1L), SpannerIOWriteTest.m(3L), SpannerIOWriteTest.m(4L), SpannerIOWriteTest.m(5L))// not batchable - too big.
        , SpannerIOWriteTest.g(SpannerIOWriteTest.del(1L)), SpannerIOWriteTest.g(SpannerIOWriteTest.del(5L, 6L))// not point delete.
        , SpannerIOWriteTest.g(all), SpannerIOWriteTest.g(prefix), SpannerIOWriteTest.g(range) };
        long mutationSize = MutationSizeEstimator.sizeOf(SpannerIOWriteTest.m(1L));
        BatchableMutationFilterFn testFn = new BatchableMutationFilterFn(null, null, (mutationSize * 3), 1000);
        ProcessContext mockProcessContext = Mockito.mock(ProcessContext.class);
        Mockito.when(mockProcessContext.sideInput(ArgumentMatchers.any())).thenReturn(getSchema());
        // Capture the outputs.
        Mockito.doNothing().when(mockProcessContext).output(mutationGroupCaptor.capture());
        Mockito.doNothing().when(mockProcessContext).output(ArgumentMatchers.any(), mutationGroupListCaptor.capture());
        // Process all elements.
        for (MutationGroup m : mutationGroups) {
            Mockito.when(mockProcessContext.element()).thenReturn(m);
            testFn.processElement(mockProcessContext);
        }
        // Verify captured batchable elements.
        Assert.assertThat(mutationGroupCaptor.getAllValues(), Matchers.containsInAnyOrder(SpannerIOWriteTest.g(SpannerIOWriteTest.m(1L)), SpannerIOWriteTest.g(SpannerIOWriteTest.m(2L), SpannerIOWriteTest.m(3L)), SpannerIOWriteTest.g(SpannerIOWriteTest.del(1L))));
        // Verify captured unbatchable mutations
        Iterable<MutationGroup> unbatchableMutations = Iterables.concat(mutationGroupListCaptor.getAllValues());
        Assert.assertThat(unbatchableMutations, // not batchable - too big.
        // not point delete.
        Matchers.containsInAnyOrder(SpannerIOWriteTest.g(SpannerIOWriteTest.m(1L), SpannerIOWriteTest.m(3L), SpannerIOWriteTest.m(4L), SpannerIOWriteTest.m(5L)), SpannerIOWriteTest.g(SpannerIOWriteTest.del(5L, 6L)), SpannerIOWriteTest.g(all), SpannerIOWriteTest.g(prefix), SpannerIOWriteTest.g(range)));
    }

    @Test
    public void testBatchableMutationFilterFn_batchingDisabled() {
        MutationGroup[] mutationGroups = new MutationGroup[]{ SpannerIOWriteTest.g(SpannerIOWriteTest.m(1L)), SpannerIOWriteTest.g(SpannerIOWriteTest.m(2L)), SpannerIOWriteTest.g(SpannerIOWriteTest.del(1L)), SpannerIOWriteTest.g(SpannerIOWriteTest.del(5L, 6L)) };
        BatchableMutationFilterFn testFn = new BatchableMutationFilterFn(null, null, 0, 0);
        ProcessContext mockProcessContext = Mockito.mock(ProcessContext.class);
        Mockito.when(mockProcessContext.sideInput(ArgumentMatchers.any())).thenReturn(getSchema());
        // Capture the outputs.
        Mockito.doNothing().when(mockProcessContext).output(mutationGroupCaptor.capture());
        Mockito.doNothing().when(mockProcessContext).output(ArgumentMatchers.any(), mutationGroupListCaptor.capture());
        // Process all elements.
        for (MutationGroup m : mutationGroups) {
            Mockito.when(mockProcessContext.element()).thenReturn(m);
            testFn.processElement(mockProcessContext);
        }
        // Verify captured batchable elements.
        Assert.assertTrue(mutationGroupCaptor.getAllValues().isEmpty());
        // Verify captured unbatchable mutations
        Iterable<MutationGroup> unbatchableMutations = Iterables.concat(mutationGroupListCaptor.getAllValues());
        Assert.assertThat(unbatchableMutations, Matchers.containsInAnyOrder(mutationGroups));
    }

    @Test
    public void testGatherBundleAndSortFn() throws Exception {
        GatherBundleAndSortFn testFn = new GatherBundleAndSortFn(10000000, 10, 100, null);
        ProcessContext mockProcessContext = Mockito.mock(ProcessContext.class);
        FinishBundleContext mockFinishBundleContext = Mockito.mock(FinishBundleContext.class);
        Mockito.when(mockProcessContext.sideInput(ArgumentMatchers.any())).thenReturn(getSchema());
        // Capture the outputs.
        Mockito.doNothing().when(mockProcessContext).output(byteArrayKvListCaptor.capture());
        // Capture the outputs.
        Mockito.doNothing().when(mockFinishBundleContext).output(byteArrayKvListCaptor.capture(), ArgumentMatchers.any(), ArgumentMatchers.any());
        MutationGroup[] mutationGroups = new MutationGroup[]{ SpannerIOWriteTest.g(SpannerIOWriteTest.m(4L)), SpannerIOWriteTest.g(SpannerIOWriteTest.m(1L)), SpannerIOWriteTest.g(SpannerIOWriteTest.m(5L), SpannerIOWriteTest.m(6L), SpannerIOWriteTest.m(7L), SpannerIOWriteTest.m(8L), SpannerIOWriteTest.m(9L)), SpannerIOWriteTest.g(SpannerIOWriteTest.del(2L)), SpannerIOWriteTest.g(SpannerIOWriteTest.m(3L)) };
        // Process all elements as one bundle.
        testFn.startBundle();
        for (MutationGroup m : mutationGroups) {
            Mockito.when(mockProcessContext.element()).thenReturn(m);
            testFn.processElement(mockProcessContext);
        }
        testFn.finishBundle(mockFinishBundleContext);
        Mockito.verify(mockProcessContext, Mockito.never()).output(ArgumentMatchers.any());
        Mockito.verify(mockFinishBundleContext, Mockito.times(1)).output(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        // Verify sorted output... first decode it...
        List<MutationGroup> sorted = byteArrayKvListCaptor.getValue().stream().map(( kv) -> WriteGrouped.decode(kv.getValue())).collect(Collectors.toList());
        Assert.assertThat(sorted, Matchers.contains(SpannerIOWriteTest.g(SpannerIOWriteTest.m(1L)), SpannerIOWriteTest.g(SpannerIOWriteTest.del(2L)), SpannerIOWriteTest.g(SpannerIOWriteTest.m(3L)), SpannerIOWriteTest.g(SpannerIOWriteTest.m(4L)), SpannerIOWriteTest.g(SpannerIOWriteTest.m(5L), SpannerIOWriteTest.m(6L), SpannerIOWriteTest.m(7L), SpannerIOWriteTest.m(8L), SpannerIOWriteTest.m(9L))));
    }

    @Test
    public void testGatherBundleAndSortFn_flushOversizedBundle() throws Exception {
        // Setup class to bundle every 3 mutations
        GatherBundleAndSortFn testFn = new GatherBundleAndSortFn(10000000, SpannerIOWriteTest.CELLS_PER_KEY, 3, null);
        ProcessContext mockProcessContext = Mockito.mock(ProcessContext.class);
        FinishBundleContext mockFinishBundleContext = Mockito.mock(FinishBundleContext.class);
        Mockito.when(mockProcessContext.sideInput(ArgumentMatchers.any())).thenReturn(getSchema());
        // Capture the outputs.
        Mockito.doNothing().when(mockProcessContext).output(byteArrayKvListCaptor.capture());
        // Capture the outputs.
        Mockito.doNothing().when(mockFinishBundleContext).output(byteArrayKvListCaptor.capture(), ArgumentMatchers.any(), ArgumentMatchers.any());
        MutationGroup[] mutationGroups = new MutationGroup[]{ SpannerIOWriteTest.g(SpannerIOWriteTest.m(4L)), SpannerIOWriteTest.g(SpannerIOWriteTest.m(1L)), // end group
        SpannerIOWriteTest.g(SpannerIOWriteTest.m(5L), SpannerIOWriteTest.m(6L), SpannerIOWriteTest.m(7L), SpannerIOWriteTest.m(8L), SpannerIOWriteTest.m(9L)), // end group
        SpannerIOWriteTest.g(SpannerIOWriteTest.m(10L)), SpannerIOWriteTest.g(SpannerIOWriteTest.m(3L)), SpannerIOWriteTest.g(SpannerIOWriteTest.m(11L)), // end group.
        SpannerIOWriteTest.g(SpannerIOWriteTest.m(2L)) };
        // Process all elements as one bundle.
        testFn.startBundle();
        for (MutationGroup m : mutationGroups) {
            Mockito.when(mockProcessContext.element()).thenReturn(m);
            testFn.processElement(mockProcessContext);
        }
        testFn.finishBundle(mockFinishBundleContext);
        Mockito.verify(mockProcessContext, Mockito.times(3)).output(ArgumentMatchers.any());
        Mockito.verify(mockFinishBundleContext, Mockito.times(1)).output(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        // verify sorted output... needs decoding...
        List<List<KV<byte[], byte[]>>> kvGroups = byteArrayKvListCaptor.getAllValues();
        Assert.assertEquals(4, kvGroups.size());
        // decode list of lists of KV to a list of lists of MutationGroup.
        List<List<MutationGroup>> mgListGroups = kvGroups.stream().map(( l) -> l.stream().map(( kv) -> WriteGrouped.decode(kv.getValue())).collect(Collectors.toList())).collect(Collectors.toList());
        // verify contents of 4 sorted groups.
        Assert.assertThat(mgListGroups.get(0), Matchers.contains(SpannerIOWriteTest.g(SpannerIOWriteTest.m(1L)), SpannerIOWriteTest.g(SpannerIOWriteTest.m(4L))));
        Assert.assertThat(mgListGroups.get(1), Matchers.contains(SpannerIOWriteTest.g(SpannerIOWriteTest.m(5L), SpannerIOWriteTest.m(6L), SpannerIOWriteTest.m(7L), SpannerIOWriteTest.m(8L), SpannerIOWriteTest.m(9L))));
        Assert.assertThat(mgListGroups.get(2), Matchers.contains(SpannerIOWriteTest.g(SpannerIOWriteTest.m(3L)), SpannerIOWriteTest.g(SpannerIOWriteTest.m(10L)), SpannerIOWriteTest.g(SpannerIOWriteTest.m(11L))));
        Assert.assertThat(mgListGroups.get(3), Matchers.contains(SpannerIOWriteTest.g(SpannerIOWriteTest.m(2L))));
    }

    @Test
    public void testBatchFn_cells() throws Exception {
        // Setup class to bundle every 3 mutations (3xCELLS_PER_KEY cell mutations)
        BatchFn testFn = new BatchFn(10000000, (3 * (SpannerIOWriteTest.CELLS_PER_KEY)), null);
        ProcessContext mockProcessContext = Mockito.mock(ProcessContext.class);
        Mockito.when(mockProcessContext.sideInput(ArgumentMatchers.any())).thenReturn(getSchema());
        // Capture the outputs.
        Mockito.doNothing().when(mockProcessContext).output(mutationGroupListCaptor.capture());
        List<MutationGroup> mutationGroups = Arrays.asList(SpannerIOWriteTest.g(SpannerIOWriteTest.m(1L)), SpannerIOWriteTest.g(SpannerIOWriteTest.m(4L)), SpannerIOWriteTest.g(SpannerIOWriteTest.m(5L), SpannerIOWriteTest.m(6L), SpannerIOWriteTest.m(7L), SpannerIOWriteTest.m(8L), SpannerIOWriteTest.m(9L)), SpannerIOWriteTest.g(SpannerIOWriteTest.m(3L)), SpannerIOWriteTest.g(SpannerIOWriteTest.m(10L)), SpannerIOWriteTest.g(SpannerIOWriteTest.m(11L)), SpannerIOWriteTest.g(SpannerIOWriteTest.m(2L)));
        List<KV<byte[], byte[]>> encodedInput = mutationGroups.stream().map(( mg) -> KV.of(((byte[]) (null)), WriteGrouped.encode(mg))).collect(Collectors.toList());
        // Process elements.
        Mockito.when(mockProcessContext.element()).thenReturn(encodedInput);
        testFn.processElement(mockProcessContext);
        Mockito.verify(mockProcessContext, Mockito.times(4)).output(ArgumentMatchers.any());
        List<Iterable<MutationGroup>> batches = mutationGroupListCaptor.getAllValues();
        Assert.assertEquals(4, batches.size());
        // verify contents of 4 batches.
        Assert.assertThat(batches.get(0), Matchers.contains(SpannerIOWriteTest.g(SpannerIOWriteTest.m(1L)), SpannerIOWriteTest.g(SpannerIOWriteTest.m(4L))));
        Assert.assertThat(batches.get(1), Matchers.contains(SpannerIOWriteTest.g(SpannerIOWriteTest.m(5L), SpannerIOWriteTest.m(6L), SpannerIOWriteTest.m(7L), SpannerIOWriteTest.m(8L), SpannerIOWriteTest.m(9L))));
        Assert.assertThat(batches.get(2), Matchers.contains(SpannerIOWriteTest.g(SpannerIOWriteTest.m(3L)), SpannerIOWriteTest.g(SpannerIOWriteTest.m(10L)), SpannerIOWriteTest.g(SpannerIOWriteTest.m(11L))));
        Assert.assertThat(batches.get(3), Matchers.contains(SpannerIOWriteTest.g(SpannerIOWriteTest.m(2L))));
    }

    @Test
    public void testBatchFn_size() throws Exception {
        long mutationSize = MutationSizeEstimator.sizeOf(SpannerIOWriteTest.m(1L));
        // Setup class to bundle every 3 mutations by size)
        BatchFn testFn = new BatchFn((mutationSize * 3), 1000, null);
        ProcessContext mockProcessContext = Mockito.mock(ProcessContext.class);
        Mockito.when(mockProcessContext.sideInput(ArgumentMatchers.any())).thenReturn(getSchema());
        // Capture the outputs.
        Mockito.doNothing().when(mockProcessContext).output(mutationGroupListCaptor.capture());
        List<MutationGroup> mutationGroups = Arrays.asList(SpannerIOWriteTest.g(SpannerIOWriteTest.m(1L)), SpannerIOWriteTest.g(SpannerIOWriteTest.m(4L)), SpannerIOWriteTest.g(SpannerIOWriteTest.m(5L), SpannerIOWriteTest.m(6L), SpannerIOWriteTest.m(7L), SpannerIOWriteTest.m(8L), SpannerIOWriteTest.m(9L)), SpannerIOWriteTest.g(SpannerIOWriteTest.m(3L)), SpannerIOWriteTest.g(SpannerIOWriteTest.m(10L)), SpannerIOWriteTest.g(SpannerIOWriteTest.m(11L)), SpannerIOWriteTest.g(SpannerIOWriteTest.m(2L)));
        List<KV<byte[], byte[]>> encodedInput = mutationGroups.stream().map(( mg) -> KV.of(((byte[]) (null)), WriteGrouped.encode(mg))).collect(Collectors.toList());
        // Process elements.
        Mockito.when(mockProcessContext.element()).thenReturn(encodedInput);
        testFn.processElement(mockProcessContext);
        Mockito.verify(mockProcessContext, Mockito.times(4)).output(ArgumentMatchers.any());
        List<Iterable<MutationGroup>> batches = mutationGroupListCaptor.getAllValues();
        Assert.assertEquals(4, batches.size());
        // verify contents of 4 batches.
        Assert.assertThat(batches.get(0), Matchers.contains(SpannerIOWriteTest.g(SpannerIOWriteTest.m(1L)), SpannerIOWriteTest.g(SpannerIOWriteTest.m(4L))));
        Assert.assertThat(batches.get(1), Matchers.contains(SpannerIOWriteTest.g(SpannerIOWriteTest.m(5L), SpannerIOWriteTest.m(6L), SpannerIOWriteTest.m(7L), SpannerIOWriteTest.m(8L), SpannerIOWriteTest.m(9L))));
        Assert.assertThat(batches.get(2), Matchers.contains(SpannerIOWriteTest.g(SpannerIOWriteTest.m(3L)), SpannerIOWriteTest.g(SpannerIOWriteTest.m(10L)), SpannerIOWriteTest.g(SpannerIOWriteTest.m(11L))));
        Assert.assertThat(batches.get(3), Matchers.contains(SpannerIOWriteTest.g(SpannerIOWriteTest.m(2L))));
    }
}

