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
package org.apache.beam.runners.direct;


import BoundedWindow.TIMESTAMP_MIN_VALUE;
import GlobalWindow.INSTANCE;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.annotation.Nullable;
import org.apache.beam.runners.direct.UnboundedReadDeduplicator.NeverDeduplicator;
import org.apache.beam.runners.direct.UnboundedReadEvaluatorFactory.UnboundedSourceShard;
import org.apache.beam.sdk.coders.AtomicCoder;
import org.apache.beam.sdk.coders.BigEndianLongCoder;
import org.apache.beam.sdk.coders.Coder;
import org.apache.beam.sdk.coders.CoderException;
import org.apache.beam.sdk.coders.StringUtf8Coder;
import org.apache.beam.sdk.coders.VarLongCoder;
import org.apache.beam.sdk.io.Read;
import org.apache.beam.sdk.io.UnboundedSource;
import org.apache.beam.sdk.io.UnboundedSource.CheckpointMark;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.runners.AppliedPTransform;
import org.apache.beam.sdk.testing.SourceTestUtils;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.transforms.SerializableFunction;
import org.apache.beam.sdk.transforms.windowing.BoundedWindow;
import org.apache.beam.sdk.util.CoderUtils;
import org.apache.beam.sdk.util.VarInt;
import org.apache.beam.sdk.util.WindowedValue;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ContiguousSet;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.DiscreteDomain;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableList;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.Iterables;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.Range;
import org.hamcrest.Matchers;
import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Tests for {@link UnboundedReadEvaluatorFactory}.
 */
@RunWith(JUnit4.class)
public class UnboundedReadEvaluatorFactoryTest {
    private PCollection<Long> longs;

    private UnboundedReadEvaluatorFactory factory;

    private EvaluationContext context;

    private UncommittedBundle<Long> output;

    private BundleFactory bundleFactory = ImmutableListBundleFactory.create();

    private UnboundedSource<Long, ?> source;

    private DirectGraph graph;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Rule
    public TestPipeline p = TestPipeline.create().enableAbandonedNodeEnforcement(false);

    private PipelineOptions options;

    @Test
    public void generatesInitialSplits() throws Exception {
        Mockito.when(context.createRootBundle()).thenAnswer(( invocation) -> bundleFactory.createRootBundle());
        int numSplits = 5;
        Collection<CommittedBundle<?>> initialInputs = new UnboundedReadEvaluatorFactory.InputProvider(context, options).getInitialInputs(graph.getProducer(longs), numSplits);
        // CountingSource.unbounded has very good splitting behavior
        Assert.assertThat(initialInputs, Matchers.hasSize(numSplits));
        int readPerSplit = 100;
        int totalSize = numSplits * readPerSplit;
        Set<Long> expectedOutputs = ContiguousSet.create(Range.closedOpen(0L, ((long) (totalSize))), DiscreteDomain.longs());
        Collection<Long> readItems = new ArrayList<>(totalSize);
        for (CommittedBundle<?> initialInput : initialInputs) {
            CommittedBundle<UnboundedSourceShard<Long, ?>> shardBundle = ((CommittedBundle<UnboundedSourceShard<Long, ?>>) (initialInput));
            WindowedValue<UnboundedSourceShard<Long, ?>> shard = Iterables.getOnlyElement(shardBundle.getElements());
            Assert.assertThat(shard.getTimestamp(), Matchers.equalTo(TIMESTAMP_MIN_VALUE));
            Assert.assertThat(shard.getWindows(), Matchers.contains(INSTANCE));
            UnboundedSource<Long, ?> shardSource = shard.getValue().getSource();
            readItems.addAll(SourceTestUtils.readNItemsFromUnstartedReader(/* No starting checkpoint */
            shardSource.createReader(PipelineOptionsFactory.create(), null), readPerSplit));
        }
        Assert.assertThat(readItems, Matchers.containsInAnyOrder(expectedOutputs.toArray(new Long[0])));
    }

    @Test
    public void unboundedSourceInMemoryTransformEvaluatorProducesElements() throws Exception {
        Mockito.when(context.createRootBundle()).thenReturn(bundleFactory.createRootBundle());
        Collection<CommittedBundle<?>> initialInputs = new UnboundedReadEvaluatorFactory.InputProvider(context, options).getInitialInputs(graph.getProducer(longs), 1);
        CommittedBundle<?> inputShards = Iterables.getOnlyElement(initialInputs);
        UnboundedSourceShard<Long, ?> inputShard = ((UnboundedSourceShard<Long, ?>) (Iterables.getOnlyElement(inputShards.getElements()).getValue()));
        TransformEvaluator<? super UnboundedSourceShard<Long, ?>> evaluator = factory.forApplication(graph.getProducer(longs), inputShards);
        evaluator.processElement(((WindowedValue) (Iterables.getOnlyElement(inputShards.getElements()))));
        TransformResult<? super UnboundedSourceShard<Long, ?>> result = evaluator.finishBundle();
        WindowedValue<? super UnboundedSourceShard<Long, ?>> residual = Iterables.getOnlyElement(result.getUnprocessedElements());
        Assert.assertThat(residual.getTimestamp(), Matchers.lessThan(DateTime.now().toInstant()));
        UnboundedSourceShard<Long, ?> residualShard = ((UnboundedSourceShard<Long, ?>) (residual.getValue()));
        Assert.assertThat(residualShard.getSource(), Matchers.equalTo(inputShard.getSource()));
        Assert.assertThat(residualShard.getCheckpoint(), Matchers.not(Matchers.nullValue()));
        Assert.assertThat(output.commit(Instant.now()).getElements(), Matchers.containsInAnyOrder(UnboundedReadEvaluatorFactoryTest.tgw(1L), UnboundedReadEvaluatorFactoryTest.tgw(2L), UnboundedReadEvaluatorFactoryTest.tgw(4L), UnboundedReadEvaluatorFactoryTest.tgw(8L), UnboundedReadEvaluatorFactoryTest.tgw(9L), UnboundedReadEvaluatorFactoryTest.tgw(7L), UnboundedReadEvaluatorFactoryTest.tgw(6L), UnboundedReadEvaluatorFactoryTest.tgw(5L), UnboundedReadEvaluatorFactoryTest.tgw(3L), UnboundedReadEvaluatorFactoryTest.tgw(0L)));
    }

    @Test
    public void unboundedSourceWithDuplicatesMultipleCalls() throws Exception {
        Long[] outputs = new Long[20];
        for (long i = 0L; i < 20L; i++) {
            outputs[((int) (i))] = i % 5L;
        }
        UnboundedReadEvaluatorFactoryTest.TestUnboundedSource<Long> source = new UnboundedReadEvaluatorFactoryTest.TestUnboundedSource(BigEndianLongCoder.of(), outputs);
        source.dedupes = true;
        PCollection<Long> pcollection = p.apply(Read.from(source));
        AppliedPTransform<?, ?, ?> sourceTransform = DirectGraphs.getProducer(pcollection);
        Mockito.when(context.createRootBundle()).thenReturn(bundleFactory.createRootBundle());
        Collection<CommittedBundle<?>> initialInputs = new UnboundedReadEvaluatorFactory.InputProvider(context, options).getInitialInputs(sourceTransform, 1);
        UncommittedBundle<Long> output = bundleFactory.createBundle(pcollection);
        Mockito.when(context.createBundle(pcollection)).thenReturn(output);
        CommittedBundle<?> inputBundle = Iterables.getOnlyElement(initialInputs);
        TransformEvaluator<UnboundedSourceShard<Long, UnboundedReadEvaluatorFactoryTest.TestCheckpointMark>> evaluator = factory.forApplication(sourceTransform, inputBundle);
        for (WindowedValue<?> value : inputBundle.getElements()) {
            evaluator.processElement(((WindowedValue<UnboundedSourceShard<Long, UnboundedReadEvaluatorFactoryTest.TestCheckpointMark>>) (value)));
        }
        TransformResult<UnboundedSourceShard<Long, UnboundedReadEvaluatorFactoryTest.TestCheckpointMark>> result = evaluator.finishBundle();
        Assert.assertThat(output.commit(Instant.now()).getElements(), Matchers.containsInAnyOrder(UnboundedReadEvaluatorFactoryTest.tgw(1L), UnboundedReadEvaluatorFactoryTest.tgw(2L), UnboundedReadEvaluatorFactoryTest.tgw(4L), UnboundedReadEvaluatorFactoryTest.tgw(3L), UnboundedReadEvaluatorFactoryTest.tgw(0L)));
        UncommittedBundle<Long> secondOutput = bundleFactory.createBundle(longs);
        Mockito.when(context.createBundle(longs)).thenReturn(secondOutput);
        TransformEvaluator<UnboundedSourceShard<Long, UnboundedReadEvaluatorFactoryTest.TestCheckpointMark>> secondEvaluator = factory.forApplication(sourceTransform, inputBundle);
        WindowedValue<UnboundedSourceShard<Long, UnboundedReadEvaluatorFactoryTest.TestCheckpointMark>> residual = ((WindowedValue<UnboundedSourceShard<Long, UnboundedReadEvaluatorFactoryTest.TestCheckpointMark>>) (Iterables.getOnlyElement(result.getUnprocessedElements())));
        secondEvaluator.processElement(residual);
        secondEvaluator.finishBundle();
        Assert.assertThat(secondOutput.commit(Instant.now()).getElements(), Matchers.emptyIterable());
    }

    @Test
    public void noElementsAvailableReaderIncludedInResidual() throws Exception {
        // Read with a very slow rate so by the second read there are no more elements
        PCollection<Long> pcollection = p.apply(Read.from(new UnboundedReadEvaluatorFactoryTest.TestUnboundedSource(VarLongCoder.of(), 1L)));
        AppliedPTransform<?, ?, ?> sourceTransform = DirectGraphs.getProducer(pcollection);
        Mockito.when(context.createRootBundle()).thenReturn(bundleFactory.createRootBundle());
        Collection<CommittedBundle<?>> initialInputs = new UnboundedReadEvaluatorFactory.InputProvider(context, options).getInitialInputs(sourceTransform, 1);
        // Process the initial shard. This might produce some output, and will produce a residual shard
        // which should produce no output when read from within the following day.
        Mockito.when(context.createBundle(pcollection)).thenReturn(bundleFactory.createBundle(pcollection));
        CommittedBundle<?> inputBundle = Iterables.getOnlyElement(initialInputs);
        TransformEvaluator<UnboundedSourceShard<Long, UnboundedReadEvaluatorFactoryTest.TestCheckpointMark>> evaluator = factory.forApplication(sourceTransform, inputBundle);
        for (WindowedValue<?> value : inputBundle.getElements()) {
            evaluator.processElement(((WindowedValue<UnboundedSourceShard<Long, UnboundedReadEvaluatorFactoryTest.TestCheckpointMark>>) (value)));
        }
        TransformResult<UnboundedSourceShard<Long, UnboundedReadEvaluatorFactoryTest.TestCheckpointMark>> result = evaluator.finishBundle();
        // Read from the residual of the first read. This should not produce any output, but should
        // include a residual shard in the result.
        UncommittedBundle<Long> secondOutput = bundleFactory.createBundle(longs);
        Mockito.when(context.createBundle(longs)).thenReturn(secondOutput);
        TransformEvaluator<UnboundedSourceShard<Long, UnboundedReadEvaluatorFactoryTest.TestCheckpointMark>> secondEvaluator = factory.forApplication(sourceTransform, inputBundle);
        WindowedValue<UnboundedSourceShard<Long, UnboundedReadEvaluatorFactoryTest.TestCheckpointMark>> residual = ((WindowedValue<UnboundedSourceShard<Long, UnboundedReadEvaluatorFactoryTest.TestCheckpointMark>>) (Iterables.getOnlyElement(result.getUnprocessedElements())));
        secondEvaluator.processElement(residual);
        TransformResult<UnboundedSourceShard<Long, UnboundedReadEvaluatorFactoryTest.TestCheckpointMark>> secondResult = secondEvaluator.finishBundle();
        // Sanity check that nothing was output (The test would have to run for more than a day to do
        // so correctly.)
        Assert.assertThat(secondOutput.commit(Instant.now()).getElements(), Matchers.emptyIterable());
        // Test that even though the reader produced no outputs, there is still a residual shard with
        // the updated watermark.
        WindowedValue<UnboundedSourceShard<Long, UnboundedReadEvaluatorFactoryTest.TestCheckpointMark>> unprocessed = ((WindowedValue<UnboundedSourceShard<Long, UnboundedReadEvaluatorFactoryTest.TestCheckpointMark>>) (Iterables.getOnlyElement(secondResult.getUnprocessedElements())));
        Assert.assertThat(unprocessed.getTimestamp(), Matchers.greaterThan(residual.getTimestamp()));
        Assert.assertThat(unprocessed.getValue().getExistingReader(), Matchers.not(Matchers.nullValue()));
    }

    @Test
    public void evaluatorReusesReaderAndClosesAtTheEnd() throws Exception {
        int numElements = 1000;
        ContiguousSet<Long> elems = ContiguousSet.create(Range.openClosed(0L, ((long) (numElements))), DiscreteDomain.longs());
        UnboundedReadEvaluatorFactoryTest.TestUnboundedSource<Long> source = new UnboundedReadEvaluatorFactoryTest.TestUnboundedSource(BigEndianLongCoder.of(), elems.toArray(new Long[0]));
        source.advanceWatermarkToInfinity = true;
        PCollection<Long> pcollection = p.apply(Read.from(source));
        DirectGraph graph = DirectGraphs.getGraph(p);
        AppliedPTransform<?, ?, ?> sourceTransform = graph.getProducer(pcollection);
        Mockito.when(context.createRootBundle()).thenReturn(bundleFactory.createRootBundle());
        UncommittedBundle<Long> output = Mockito.mock(UncommittedBundle.class);
        Mockito.when(context.createBundle(pcollection)).thenReturn(output);
        WindowedValue<UnboundedSourceShard<Long, UnboundedReadEvaluatorFactoryTest.TestCheckpointMark>> shard = WindowedValue.valueInGlobalWindow(UnboundedSourceShard.unstarted(source, NeverDeduplicator.create()));
        CommittedBundle<UnboundedSourceShard<Long, UnboundedReadEvaluatorFactoryTest.TestCheckpointMark>> inputBundle = bundleFactory.<UnboundedSourceShard<Long, UnboundedReadEvaluatorFactoryTest.TestCheckpointMark>>createRootBundle().add(shard).commit(Instant.now());
        UnboundedReadEvaluatorFactory factory = /* Always reuse */
        new UnboundedReadEvaluatorFactory(context, options, 1.0);
        new UnboundedReadEvaluatorFactory.InputProvider(context, options).getInitialInputs(sourceTransform, 1);
        CommittedBundle<UnboundedSourceShard<Long, UnboundedReadEvaluatorFactoryTest.TestCheckpointMark>> residual = inputBundle;
        do {
            TransformEvaluator<UnboundedSourceShard<Long, UnboundedReadEvaluatorFactoryTest.TestCheckpointMark>> evaluator = factory.forApplication(sourceTransform, residual);
            evaluator.processElement(Iterables.getOnlyElement(residual.getElements()));
            TransformResult<UnboundedSourceShard<Long, UnboundedReadEvaluatorFactoryTest.TestCheckpointMark>> result = evaluator.finishBundle();
            residual = inputBundle.withElements(((Iterable<WindowedValue<UnboundedSourceShard<Long, UnboundedReadEvaluatorFactoryTest.TestCheckpointMark>>>) (result.getUnprocessedElements())));
        } while (!(Iterables.isEmpty(residual.getElements())) );
        Mockito.verify(output, Mockito.times(numElements)).add(ArgumentMatchers.any());
        Assert.assertThat(UnboundedReadEvaluatorFactoryTest.TestUnboundedSource.readerCreatedCount, Matchers.equalTo(1));
        Assert.assertThat(UnboundedReadEvaluatorFactoryTest.TestUnboundedSource.readerClosedCount, Matchers.equalTo(1));
    }

    @Test
    public void evaluatorClosesReaderAndResumesFromCheckpoint() throws Exception {
        ContiguousSet<Long> elems = ContiguousSet.create(Range.closed(0L, 20L), DiscreteDomain.longs());
        UnboundedReadEvaluatorFactoryTest.TestUnboundedSource<Long> source = new UnboundedReadEvaluatorFactoryTest.TestUnboundedSource(BigEndianLongCoder.of(), elems.toArray(new Long[0]));
        PCollection<Long> pcollection = p.apply(Read.from(source));
        AppliedPTransform<?, ?, ?> sourceTransform = DirectGraphs.getGraph(p).getProducer(pcollection);
        Mockito.when(context.createRootBundle()).thenReturn(bundleFactory.createRootBundle());
        UncommittedBundle<Long> output = bundleFactory.createBundle(pcollection);
        Mockito.when(context.createBundle(pcollection)).thenReturn(output);
        WindowedValue<UnboundedSourceShard<Long, UnboundedReadEvaluatorFactoryTest.TestCheckpointMark>> shard = WindowedValue.valueInGlobalWindow(UnboundedSourceShard.unstarted(source, NeverDeduplicator.create()));
        CommittedBundle<UnboundedSourceShard<Long, UnboundedReadEvaluatorFactoryTest.TestCheckpointMark>> inputBundle = bundleFactory.<UnboundedSourceShard<Long, UnboundedReadEvaluatorFactoryTest.TestCheckpointMark>>createRootBundle().add(shard).commit(Instant.now());
        UnboundedReadEvaluatorFactory factory = /* never reuse */
        new UnboundedReadEvaluatorFactory(context, options, 0.0);
        TransformEvaluator<UnboundedSourceShard<Long, UnboundedReadEvaluatorFactoryTest.TestCheckpointMark>> evaluator = factory.forApplication(sourceTransform, inputBundle);
        evaluator.processElement(shard);
        TransformResult<UnboundedSourceShard<Long, UnboundedReadEvaluatorFactoryTest.TestCheckpointMark>> result = evaluator.finishBundle();
        CommittedBundle<UnboundedSourceShard<Long, UnboundedReadEvaluatorFactoryTest.TestCheckpointMark>> residual = inputBundle.withElements(((Iterable<WindowedValue<UnboundedSourceShard<Long, UnboundedReadEvaluatorFactoryTest.TestCheckpointMark>>>) (result.getUnprocessedElements())));
        TransformEvaluator<UnboundedSourceShard<Long, UnboundedReadEvaluatorFactoryTest.TestCheckpointMark>> secondEvaluator = factory.forApplication(sourceTransform, residual);
        secondEvaluator.processElement(Iterables.getOnlyElement(residual.getElements()));
        secondEvaluator.finishBundle();
        Assert.assertThat(UnboundedReadEvaluatorFactoryTest.TestUnboundedSource.readerClosedCount, Matchers.equalTo(2));
        Assert.assertThat(Iterables.getOnlyElement(residual.getElements()).getValue().getCheckpoint().isFinalized(), Matchers.is(true));
    }

    @Test
    public void evaluatorThrowsInCloseRethrows() throws Exception {
        ContiguousSet<Long> elems = ContiguousSet.create(Range.closed(0L, 20L), DiscreteDomain.longs());
        UnboundedReadEvaluatorFactoryTest.TestUnboundedSource<Long> source = new UnboundedReadEvaluatorFactoryTest.TestUnboundedSource(BigEndianLongCoder.of(), elems.toArray(new Long[0])).throwsOnClose();
        PCollection<Long> pcollection = p.apply(Read.from(source));
        AppliedPTransform<?, ?, ?> sourceTransform = DirectGraphs.getGraph(p).getProducer(pcollection);
        Mockito.when(context.createRootBundle()).thenReturn(bundleFactory.createRootBundle());
        UncommittedBundle<Long> output = bundleFactory.createBundle(pcollection);
        Mockito.when(context.createBundle(pcollection)).thenReturn(output);
        WindowedValue<UnboundedSourceShard<Long, UnboundedReadEvaluatorFactoryTest.TestCheckpointMark>> shard = WindowedValue.valueInGlobalWindow(UnboundedSourceShard.unstarted(source, NeverDeduplicator.create()));
        CommittedBundle<UnboundedSourceShard<Long, UnboundedReadEvaluatorFactoryTest.TestCheckpointMark>> inputBundle = bundleFactory.<UnboundedSourceShard<Long, UnboundedReadEvaluatorFactoryTest.TestCheckpointMark>>createRootBundle().add(shard).commit(Instant.now());
        UnboundedReadEvaluatorFactory factory = /* never reuse */
        new UnboundedReadEvaluatorFactory(context, options, 0.0);
        TransformEvaluator<UnboundedSourceShard<Long, UnboundedReadEvaluatorFactoryTest.TestCheckpointMark>> evaluator = factory.forApplication(sourceTransform, inputBundle);
        thrown.expect(IOException.class);
        thrown.expectMessage("throws on close");
        evaluator.processElement(shard);
    }

    // before this was throwing a NPE
    @Test
    public void emptySource() throws Exception {
        UnboundedReadEvaluatorFactoryTest.TestUnboundedSource.readerClosedCount = 0;
        final UnboundedReadEvaluatorFactoryTest.TestUnboundedSource<String> source = new UnboundedReadEvaluatorFactoryTest.TestUnboundedSource(StringUtf8Coder.of());
        source.advanceWatermarkToInfinity = true;
        processElement(source);
        Assert.assertEquals(1, UnboundedReadEvaluatorFactoryTest.TestUnboundedSource.readerClosedCount);
        UnboundedReadEvaluatorFactoryTest.TestUnboundedSource.readerClosedCount = 0;// reset

    }

    @Test(expected = IOException.class)
    public void sourceThrowingException() throws Exception {
        final UnboundedReadEvaluatorFactoryTest.TestUnboundedSource<String> source = new UnboundedReadEvaluatorFactoryTest.TestUnboundedSource(StringUtf8Coder.of());
        source.advanceWatermarkToInfinity = true;
        source.throwOnClose = true;
        processElement(source);
    }

    private static class LongToInstantFn implements SerializableFunction<Long, Instant> {
        @Override
        public Instant apply(Long input) {
            return new Instant(input);
        }
    }

    private static class TestUnboundedSource<T> extends UnboundedSource<T, UnboundedReadEvaluatorFactoryTest.TestCheckpointMark> {
        private static int getWatermarkCalls = 0;

        static int readerCreatedCount;

        static int readerClosedCount;

        static int readerAdvancedCount;

        private final Coder<T> coder;

        private final List<T> elems;

        private boolean dedupes = false;

        private boolean advanceWatermarkToInfinity = false;// After reaching end of input.


        private boolean throwOnClose;

        public TestUnboundedSource(Coder<T> coder, T... elems) {
            this(coder, false, Arrays.asList(elems));
        }

        private TestUnboundedSource(Coder<T> coder, boolean throwOnClose, List<T> elems) {
            UnboundedReadEvaluatorFactoryTest.TestUnboundedSource.readerCreatedCount = 0;
            UnboundedReadEvaluatorFactoryTest.TestUnboundedSource.readerClosedCount = 0;
            UnboundedReadEvaluatorFactoryTest.TestUnboundedSource.readerAdvancedCount = 0;
            this.coder = coder;
            this.elems = elems;
            this.throwOnClose = throwOnClose;
        }

        @Override
        public List<? extends UnboundedSource<T, UnboundedReadEvaluatorFactoryTest.TestCheckpointMark>> split(int desiredNumSplits, PipelineOptions options) throws Exception {
            return ImmutableList.of(this);
        }

        @Override
        public UnboundedSource.UnboundedReader<T> createReader(PipelineOptions options, @Nullable
        UnboundedReadEvaluatorFactoryTest.TestCheckpointMark checkpointMark) {
            UnboundedReadEvaluatorFactoryTest.TestUnboundedSource.checkState(((checkpointMark == null) || (checkpointMark.decoded)), "Cannot resume from a checkpoint that has not been decoded");
            (UnboundedReadEvaluatorFactoryTest.TestUnboundedSource.readerCreatedCount)++;
            return new TestUnboundedReader(elems, (checkpointMark == null ? -1 : checkpointMark.index));
        }

        @Override
        @Nullable
        public Coder<UnboundedReadEvaluatorFactoryTest.TestCheckpointMark> getCheckpointMarkCoder() {
            return new UnboundedReadEvaluatorFactoryTest.TestCheckpointMark.Coder();
        }

        @Override
        public boolean requiresDeduping() {
            return dedupes;
        }

        @Override
        public Coder<T> getOutputCoder() {
            return coder;
        }

        public UnboundedReadEvaluatorFactoryTest.TestUnboundedSource<T> throwsOnClose() {
            return new UnboundedReadEvaluatorFactoryTest.TestUnboundedSource(coder, true, elems);
        }

        private class TestUnboundedReader extends UnboundedReader<T> {
            private final List<T> elems;

            private int index;

            private boolean closed = false;

            public TestUnboundedReader(List<T> elems, int startIndex) {
                this.elems = elems;
                this.index = startIndex;
            }

            @Override
            public boolean start() throws IOException {
                return advance();
            }

            @Override
            public boolean advance() throws IOException {
                (UnboundedReadEvaluatorFactoryTest.TestUnboundedSource.readerAdvancedCount)++;
                if (((index) + 1) < (elems.size())) {
                    (index)++;
                    return true;
                }
                return false;
            }

            @Override
            public Instant getWatermark() {
                (UnboundedReadEvaluatorFactoryTest.TestUnboundedSource.getWatermarkCalls)++;
                if ((((index) + 1) == (elems.size())) && (UnboundedReadEvaluatorFactoryTest.TestUnboundedSource.this.advanceWatermarkToInfinity)) {
                    return BoundedWindow.TIMESTAMP_MAX_VALUE;
                } else {
                    return new Instant(((index) + (UnboundedReadEvaluatorFactoryTest.TestUnboundedSource.getWatermarkCalls)));
                }
            }

            @Override
            public CheckpointMark getCheckpointMark() {
                return new UnboundedReadEvaluatorFactoryTest.TestCheckpointMark(index);
            }

            @Override
            public UnboundedSource<T, ?> getCurrentSource() {
                return UnboundedReadEvaluatorFactoryTest.TestUnboundedSource.this;
            }

            @Override
            public T getCurrent() throws NoSuchElementException {
                return elems.get(index);
            }

            @Override
            public Instant getCurrentTimestamp() throws NoSuchElementException {
                return new Instant(index);
            }

            @Override
            public byte[] getCurrentRecordId() {
                try {
                    return CoderUtils.encodeToByteArray(coder, getCurrent());
                } catch (CoderException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void close() throws IOException {
                try {
                    (UnboundedReadEvaluatorFactoryTest.TestUnboundedSource.readerClosedCount)++;
                    // Enforce the AutoCloseable contract. Close is not idempotent.
                    Assert.assertThat(closed, UnboundedReadEvaluatorFactoryTest.TestUnboundedSource.TestUnboundedReader.is(false));
                    if (throwOnClose) {
                        throw new IOException(String.format("%s throws on close", UnboundedReadEvaluatorFactoryTest.TestUnboundedSource.this));
                    }
                } finally {
                    closed = true;
                }
            }
        }
    }

    private static class TestCheckpointMark implements CheckpointMark {
        final int index;

        private boolean finalized = false;

        private boolean decoded = false;

        private TestCheckpointMark(int index) {
            this.index = index;
        }

        @Override
        public void finalizeCheckpoint() throws IOException {
            checkState((!(finalized)), "%s was finalized more than once", UnboundedReadEvaluatorFactoryTest.TestCheckpointMark.class.getSimpleName());
            checkState((!(decoded)), "%s was finalized after being decoded", UnboundedReadEvaluatorFactoryTest.TestCheckpointMark.class.getSimpleName());
            finalized = true;
        }

        boolean isFinalized() {
            return finalized;
        }

        public static class Coder extends AtomicCoder<UnboundedReadEvaluatorFactoryTest.TestCheckpointMark> {
            @Override
            public void encode(UnboundedReadEvaluatorFactoryTest.TestCheckpointMark value, OutputStream outStream) throws IOException {
                VarInt.encode(value.index, outStream);
            }

            @Override
            public UnboundedReadEvaluatorFactoryTest.TestCheckpointMark decode(InputStream inStream) throws IOException {
                UnboundedReadEvaluatorFactoryTest.TestCheckpointMark decoded = new UnboundedReadEvaluatorFactoryTest.TestCheckpointMark(VarInt.decodeInt(inStream));
                decoded.decoded = true;
                return decoded;
            }
        }
    }
}

