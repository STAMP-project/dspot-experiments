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
package org.apache.beam.sdk.io;


import java.io.IOException;
import java.util.List;
import org.apache.beam.sdk.io.BoundedSource.BoundedReader;
import org.apache.beam.sdk.io.CountingSource.CounterMark;
import org.apache.beam.sdk.io.CountingSource.UnboundedCountingSource;
import org.apache.beam.sdk.io.UnboundedSource.UnboundedReader;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.testing.DataflowPortabilityApiUnsupported;
import org.apache.beam.sdk.testing.NeedsRunner;
import org.apache.beam.sdk.testing.PAssert;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.testing.UsesStatefulParDo;
import org.apache.beam.sdk.testing.ValidatesRunner;
import org.apache.beam.sdk.transforms.Distinct;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.Flatten;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.transforms.SerializableFunction;
import org.apache.beam.sdk.util.CoderUtils;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.PCollectionList;
import org.hamcrest.Matchers;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests of {@link CountingSource}.
 */
@RunWith(JUnit4.class)
public class CountingSourceTest {
    @Rule
    public TestPipeline p = TestPipeline.create();

    @Test
    @Category(NeedsRunner.class)
    public void testBoundedSource() {
        long numElements = 1000;
        PCollection<Long> input = p.apply(Read.from(CountingSource.upTo(numElements)));
        CountingSourceTest.addCountingAsserts(input, numElements);
        p.run();
    }

    @Test
    @Category(NeedsRunner.class)
    public void testEmptyBoundedSource() {
        PCollection<Long> input = p.apply(Read.from(CountingSource.upTo(0)));
        PAssert.that(input).empty();
        p.run();
    }

    @Test
    @Category({ ValidatesRunner.class, UsesStatefulParDo.class// This test fails if State is unsupported despite no direct usage.
    , DataflowPortabilityApiUnsupported.class })
    public void testBoundedSourceSplits() throws Exception {
        long numElements = 1000;
        long numSplits = 10;
        long splitSizeBytes = (numElements * 8) / numSplits;// 8 bytes per long element.

        BoundedSource<Long> initial = CountingSource.upTo(numElements);
        List<? extends BoundedSource<Long>> splits = initial.split(splitSizeBytes, p.getOptions());
        Assert.assertEquals("Expected exact splitting", numSplits, splits.size());
        // Assemble all the splits into one flattened PCollection, also verify their sizes.
        PCollectionList<Long> pcollections = PCollectionList.empty(p);
        for (int i = 0; i < (splits.size()); ++i) {
            BoundedSource<Long> split = splits.get(i);
            pcollections = pcollections.and(p.apply(("split" + i), Read.from(split)));
            Assert.assertEquals("Expected even splitting", splitSizeBytes, split.getEstimatedSizeBytes(p.getOptions()));
        }
        PCollection<Long> input = pcollections.apply(Flatten.pCollections());
        CountingSourceTest.addCountingAsserts(input, numElements);
        p.run();
    }

    @Test
    public void testProgress() throws IOException {
        final int numRecords = 5;
        // testing CountingSource
        @SuppressWarnings("deprecation")
        BoundedSource<Long> source = CountingSource.upTo(numRecords);
        try (BoundedReader<Long> reader = source.createReader(PipelineOptionsFactory.create())) {
            // Check preconditions before starting. Note that CountingReader can always give an accurate
            // remaining parallelism.
            Assert.assertEquals(0.0, reader.getFractionConsumed(), 1.0E-6);
            Assert.assertEquals(0, reader.getSplitPointsConsumed());
            Assert.assertEquals(numRecords, reader.getSplitPointsRemaining());
            Assert.assertTrue(reader.start());
            int i = 0;
            do {
                Assert.assertEquals(i, reader.getSplitPointsConsumed());
                Assert.assertEquals((numRecords - i), reader.getSplitPointsRemaining());
                ++i;
            } while (reader.advance() );
            Assert.assertEquals(numRecords, i);// exactly numRecords calls to advance()

            Assert.assertEquals(1.0, reader.getFractionConsumed(), 1.0E-6);
            Assert.assertEquals(numRecords, reader.getSplitPointsConsumed());
            Assert.assertEquals(0, reader.getSplitPointsRemaining());
        }
    }

    @Test
    @Category(NeedsRunner.class)
    public void testUnboundedSource() {
        long numElements = 1000;
        PCollection<Long> input = p.apply(Read.from(CountingSource.unbounded()).withMaxNumRecords(numElements));
        CountingSourceTest.addCountingAsserts(input, numElements);
        p.run();
    }

    private static class ElementValueDiff extends DoFn<Long, Long> {
        @ProcessElement
        public void processElement(ProcessContext c) throws Exception {
            c.output(((c.element()) - (c.timestamp().getMillis())));
        }
    }

    @Test
    @Category(NeedsRunner.class)
    public void testUnboundedSourceTimestamps() {
        long numElements = 1000;
        PCollection<Long> input = p.apply(Read.from(CountingSource.unboundedWithTimestampFn(new CountingSourceTest.ValueAsTimestampFn())).withMaxNumRecords(numElements));
        CountingSourceTest.addCountingAsserts(input, numElements);
        PCollection<Long> diffs = input.apply("TimestampDiff", ParDo.of(new CountingSourceTest.ElementValueDiff())).apply("DistinctTimestamps", Distinct.create());
        // This assert also confirms that diffs only has one unique value.
        PAssert.thatSingleton(diffs).isEqualTo(0L);
        p.run();
    }

    @Test
    @Category(NeedsRunner.class)
    public void testUnboundedSourceWithRate() {
        Duration period = Duration.millis(5);
        long numElements = 1000L;
        PCollection<Long> input = p.apply(Read.from(CountingSource.createUnboundedFrom(0).withTimestampFn(new CountingSourceTest.ValueAsTimestampFn()).withRate(1, period)).withMaxNumRecords(numElements));
        CountingSourceTest.addCountingAsserts(input, numElements);
        PCollection<Long> diffs = input.apply("TimestampDiff", ParDo.of(new CountingSourceTest.ElementValueDiff())).apply("DistinctTimestamps", Distinct.create());
        // This assert also confirms that diffs only has one unique value.
        PAssert.thatSingleton(diffs).isEqualTo(0L);
        Instant started = Instant.now();
        p.run();
        Instant finished = Instant.now();
        Duration expectedDuration = period.multipliedBy(((int) (numElements)));
        Assert.assertThat(started.plus(expectedDuration).isBefore(finished), Matchers.is(true));
    }

    @Test
    @Category({ ValidatesRunner.class, UsesStatefulParDo.class// This test fails if State is unsupported despite no direct usage.
    , DataflowPortabilityApiUnsupported.class })
    public void testUnboundedSourceSplits() throws Exception {
        long numElements = 1000;
        int numSplits = 10;
        UnboundedSource<Long, ?> initial = CountingSource.unbounded();
        List<? extends UnboundedSource<Long, ?>> splits = initial.split(numSplits, p.getOptions());
        Assert.assertEquals("Expected exact splitting", numSplits, splits.size());
        long elementsPerSplit = numElements / numSplits;
        Assert.assertEquals("Expected even splits", numElements, (elementsPerSplit * numSplits));
        PCollectionList<Long> pcollections = PCollectionList.empty(p);
        for (int i = 0; i < (splits.size()); ++i) {
            pcollections = pcollections.and(p.apply(("split" + i), Read.from(splits.get(i)).withMaxNumRecords(elementsPerSplit)));
        }
        PCollection<Long> input = pcollections.apply(Flatten.pCollections());
        CountingSourceTest.addCountingAsserts(input, numElements);
        p.run();
    }

    @Test
    @Category(NeedsRunner.class)
    public void testUnboundedSourceRateSplits() throws Exception {
        int elementsPerPeriod = 10;
        Duration period = Duration.millis(5);
        long numElements = 1000;
        int numSplits = 10;
        UnboundedCountingSource initial = CountingSource.createUnboundedFrom(0).withRate(elementsPerPeriod, period);
        List<? extends UnboundedSource<Long, ?>> splits = initial.split(numSplits, p.getOptions());
        Assert.assertEquals("Expected exact splitting", numSplits, splits.size());
        long elementsPerSplit = numElements / numSplits;
        Assert.assertEquals("Expected even splits", numElements, (elementsPerSplit * numSplits));
        PCollectionList<Long> pcollections = PCollectionList.empty(p);
        for (int i = 0; i < (splits.size()); ++i) {
            pcollections = pcollections.and(p.apply(("split" + i), Read.from(splits.get(i)).withMaxNumRecords(elementsPerSplit)));
        }
        PCollection<Long> input = pcollections.apply(Flatten.pCollections());
        CountingSourceTest.addCountingAsserts(input, numElements);
        Instant startTime = Instant.now();
        p.run();
        Instant endTime = Instant.now();
        // 500 ms if the readers are all initialized in parallel; 5000 ms if they are evaluated serially
        long expectedMinimumMillis = (numElements * (period.getMillis())) / elementsPerPeriod;
        Assert.assertThat(expectedMinimumMillis, Matchers.lessThan(((endTime.getMillis()) - (startTime.getMillis()))));
    }

    /**
     * A timestamp function that uses the given value as the timestamp. Because the input values will
     * not wrap, this function is non-decreasing and meets the timestamp function criteria laid out in
     * {@link CountingSource#unboundedWithTimestampFn(SerializableFunction)}.
     */
    private static class ValueAsTimestampFn implements SerializableFunction<Long, Instant> {
        @Override
        public Instant apply(Long input) {
            return new Instant(input);
        }
    }

    @Test
    public void testUnboundedSourceCheckpointMark() throws Exception {
        UnboundedSource<Long, CounterMark> source = CountingSource.unboundedWithTimestampFn(new CountingSourceTest.ValueAsTimestampFn());
        UnboundedReader<Long> reader = source.createReader(null, null);
        final long numToSkip = 3;
        Assert.assertTrue(reader.start());
        // Advance the source numToSkip elements and manually save state.
        for (long l = 0; l < numToSkip; ++l) {
            reader.advance();
        }
        // Confirm that we get the expected element in sequence before checkpointing.
        Assert.assertEquals(numToSkip, ((long) (reader.getCurrent())));
        Assert.assertEquals(numToSkip, reader.getCurrentTimestamp().getMillis());
        // Checkpoint and restart, and confirm that the source continues correctly.
        CounterMark mark = CoderUtils.clone(source.getCheckpointMarkCoder(), ((CounterMark) (reader.getCheckpointMark())));
        reader = source.createReader(null, mark);
        Assert.assertTrue(reader.start());
        // Confirm that we get the next element in sequence.
        Assert.assertEquals((numToSkip + 1), ((long) (reader.getCurrent())));
        Assert.assertEquals((numToSkip + 1), reader.getCurrentTimestamp().getMillis());
    }
}

