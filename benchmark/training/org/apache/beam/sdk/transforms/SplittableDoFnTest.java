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
package org.apache.beam.sdk.transforms;


import IsBounded.BOUNDED;
import IsBounded.UNBOUNDED;
import java.io.Serializable;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.range.OffsetRange;
import org.apache.beam.sdk.testing.DataflowPortabilityApiUnsupported;
import org.apache.beam.sdk.testing.NeedsRunner;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.testing.UsesBoundedSplittableParDo;
import org.apache.beam.sdk.testing.UsesParDoLifecycle;
import org.apache.beam.sdk.testing.UsesSideInputs;
import org.apache.beam.sdk.testing.UsesSplittableParDoWithWindowedSideInputs;
import org.apache.beam.sdk.testing.UsesUnboundedSplittableParDo;
import org.apache.beam.sdk.testing.ValidatesRunner;
import org.apache.beam.sdk.transforms.DoFn.BoundedPerElement;
import org.apache.beam.sdk.transforms.DoFn.UnboundedPerElement;
import org.apache.beam.sdk.transforms.splittabledofn.OffsetRangeTracker;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.PCollectionView;
import org.apache.beam.sdk.values.TupleTag;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static ProcessContinuation.resume;
import static ProcessContinuation.stop;


/**
 * Tests for <a href="https://s.apache.org/splittable-do-fn>splittable</a> {@link DoFn} behavior.
 */
// TODO (https://issues.apache.org/jira/browse/BEAM-988): Test that Splittable DoFn
// emits output immediately (i.e. has a pass-through trigger) regardless of input's
// windowing/triggering strategy.
@RunWith(JUnit4.class)
public class SplittableDoFnTest implements Serializable {
    static class PairStringWithIndexToLengthBase extends DoFn<String, KV<String, Integer>> {
        @ProcessElement
        public ProcessContinuation process(ProcessContext c, OffsetRangeTracker tracker) {
            for (long i = tracker.currentRestriction().getFrom(), numIterations = 0; tracker.tryClaim(i); ++i , ++numIterations) {
                c.output(KV.of(c.element(), ((int) (i))));
                if ((numIterations % 3) == 0) {
                    return resume();
                }
            }
            return stop();
        }

        @GetInitialRestriction
        public OffsetRange getInitialRange(String element) {
            return new OffsetRange(0, element.length());
        }

        @SplitRestriction
        public void splitRange(String element, OffsetRange range, OutputReceiver<OffsetRange> receiver) {
            receiver.output(new OffsetRange(range.getFrom(), (((range.getFrom()) + (range.getTo())) / 2)));
            receiver.output(new OffsetRange((((range.getFrom()) + (range.getTo())) / 2), range.getTo()));
        }
    }

    @BoundedPerElement
    static class PairStringWithIndexToLengthBounded extends SplittableDoFnTest.PairStringWithIndexToLengthBase {}

    @UnboundedPerElement
    static class PairStringWithIndexToLengthUnbounded extends SplittableDoFnTest.PairStringWithIndexToLengthBase {}

    @Rule
    public final transient TestPipeline p = TestPipeline.create();

    @Test
    @Category({ ValidatesRunner.class, UsesBoundedSplittableParDo.class })
    public void testPairWithIndexBasicBounded() {
        testPairWithIndexBasic(BOUNDED);
    }

    @Test
    @Category({ ValidatesRunner.class, UsesUnboundedSplittableParDo.class })
    public void testPairWithIndexBasicUnbounded() {
        testPairWithIndexBasic(UNBOUNDED);
    }

    @Test
    @Category({ ValidatesRunner.class, UsesBoundedSplittableParDo.class })
    public void testPairWithIndexWindowedTimestampedBounded() {
        testPairWithIndexWindowedTimestamped(BOUNDED);
    }

    @Test
    @Category({ ValidatesRunner.class, UsesUnboundedSplittableParDo.class })
    public void testPairWithIndexWindowedTimestampedUnbounded() {
        testPairWithIndexWindowedTimestamped(UNBOUNDED);
    }

    private static class SDFWithMultipleOutputsPerBlockBase extends DoFn<String, Integer> {
        private static final int MAX_INDEX = 98765;

        private final int numClaimsPerCall;

        private SDFWithMultipleOutputsPerBlockBase(int numClaimsPerCall) {
            this.numClaimsPerCall = numClaimsPerCall;
        }

        private static int snapToNextBlock(int index, int[] blockStarts) {
            for (int i = 1; i < (blockStarts.length); ++i) {
                if ((index > (blockStarts[(i - 1)])) && (index <= (blockStarts[i]))) {
                    return i;
                }
            }
            throw new IllegalStateException("Shouldn't get here");
        }

        @ProcessElement
        public ProcessContinuation processElement(ProcessContext c, OffsetRangeTracker tracker) {
            int[] blockStarts = new int[]{ -1, 0, 12, 123, 1234, 12345, 34567, SplittableDoFnTest.SDFWithMultipleOutputsPerBlockBase.MAX_INDEX };
            int trueStart = SplittableDoFnTest.SDFWithMultipleOutputsPerBlockBase.snapToNextBlock(((int) (tracker.currentRestriction().getFrom())), blockStarts);
            for (int i = trueStart, numIterations = 1; tracker.tryClaim(((long) (blockStarts[i]))); ++i , ++numIterations) {
                for (int index = blockStarts[i]; index < (blockStarts[(i + 1)]); ++index) {
                    c.output(index);
                }
                if (numIterations == (numClaimsPerCall)) {
                    return resume();
                }
            }
            return stop();
        }

        @GetInitialRestriction
        public OffsetRange getInitialRange(String element) {
            return new OffsetRange(0, SplittableDoFnTest.SDFWithMultipleOutputsPerBlockBase.MAX_INDEX);
        }
    }

    @BoundedPerElement
    private static class SDFWithMultipleOutputsPerBlockBounded extends SplittableDoFnTest.SDFWithMultipleOutputsPerBlockBase {
        SDFWithMultipleOutputsPerBlockBounded(int numClaimsPerCall) {
            super(numClaimsPerCall);
        }
    }

    @UnboundedPerElement
    private static class SDFWithMultipleOutputsPerBlockUnbounded extends SplittableDoFnTest.SDFWithMultipleOutputsPerBlockBase {
        SDFWithMultipleOutputsPerBlockUnbounded(int numClaimsPerCall) {
            super(numClaimsPerCall);
        }
    }

    @Test
    @Category({ ValidatesRunner.class, UsesBoundedSplittableParDo.class, DataflowPortabilityApiUnsupported.class })
    public void testOutputAfterCheckpointBounded() {
        testOutputAfterCheckpoint(BOUNDED);
    }

    @Test
    @Category({ ValidatesRunner.class, UsesUnboundedSplittableParDo.class })
    public void testOutputAfterCheckpointUnbounded() {
        testOutputAfterCheckpoint(UNBOUNDED);
    }

    private static class SDFWithSideInputBase extends DoFn<Integer, String> {
        private final PCollectionView<String> sideInput;

        private SDFWithSideInputBase(PCollectionView<String> sideInput) {
            this.sideInput = sideInput;
        }

        @ProcessElement
        public void process(ProcessContext c, OffsetRangeTracker tracker) {
            checkState(tracker.tryClaim(tracker.currentRestriction().getFrom()));
            String side = c.sideInput(sideInput);
            c.output(((side + ":") + (c.element())));
        }

        @GetInitialRestriction
        public OffsetRange getInitialRestriction(Integer value) {
            return new OffsetRange(0, 1);
        }
    }

    @BoundedPerElement
    private static class SDFWithSideInputBounded extends SplittableDoFnTest.SDFWithSideInputBase {
        private SDFWithSideInputBounded(PCollectionView<String> sideInput) {
            super(sideInput);
        }
    }

    @UnboundedPerElement
    private static class SDFWithSideInputUnbounded extends SplittableDoFnTest.SDFWithSideInputBase {
        private SDFWithSideInputUnbounded(PCollectionView<String> sideInput) {
            super(sideInput);
        }
    }

    @Test
    @Category({ ValidatesRunner.class, UsesBoundedSplittableParDo.class, UsesSideInputs.class })
    public void testSideInputBounded() {
        testSideInput(BOUNDED);
    }

    @Test
    @Category({ ValidatesRunner.class, UsesUnboundedSplittableParDo.class })
    public void testSideInputUnbounded() {
        testSideInput(UNBOUNDED);
    }

    @Test
    @Category({ ValidatesRunner.class, UsesBoundedSplittableParDo.class, UsesSplittableParDoWithWindowedSideInputs.class })
    public void testWindowedSideInputBounded() {
        testWindowedSideInput(BOUNDED);
    }

    @Test
    @Category({ ValidatesRunner.class, UsesUnboundedSplittableParDo.class, UsesSplittableParDoWithWindowedSideInputs.class })
    public void testWindowedSideInputUnbounded() {
        testWindowedSideInput(UNBOUNDED);
    }

    private static class SDFWithMultipleOutputsPerBlockAndSideInputBase extends DoFn<Integer, KV<String, Integer>> {
        private static final int MAX_INDEX = 98765;

        private final PCollectionView<String> sideInput;

        private final int numClaimsPerCall;

        SDFWithMultipleOutputsPerBlockAndSideInputBase(PCollectionView<String> sideInput, int numClaimsPerCall) {
            this.sideInput = sideInput;
            this.numClaimsPerCall = numClaimsPerCall;
        }

        private static int snapToNextBlock(int index, int[] blockStarts) {
            for (int i = 1; i < (blockStarts.length); ++i) {
                if ((index > (blockStarts[(i - 1)])) && (index <= (blockStarts[i]))) {
                    return i;
                }
            }
            throw new IllegalStateException("Shouldn't get here");
        }

        @ProcessElement
        public ProcessContinuation processElement(ProcessContext c, OffsetRangeTracker tracker) {
            int[] blockStarts = new int[]{ -1, 0, 12, 123, 1234, 12345, 34567, SplittableDoFnTest.SDFWithMultipleOutputsPerBlockAndSideInputBase.MAX_INDEX };
            int trueStart = SplittableDoFnTest.SDFWithMultipleOutputsPerBlockAndSideInputBase.snapToNextBlock(((int) (tracker.currentRestriction().getFrom())), blockStarts);
            for (int i = trueStart, numIterations = 1; tracker.tryClaim(((long) (blockStarts[i]))); ++i , ++numIterations) {
                for (int index = blockStarts[i]; index < (blockStarts[(i + 1)]); ++index) {
                    c.output(KV.of((((c.sideInput(sideInput)) + ":") + (c.element())), index));
                }
                if (numIterations == (numClaimsPerCall)) {
                    return resume();
                }
            }
            return stop();
        }

        @GetInitialRestriction
        public OffsetRange getInitialRange(Integer element) {
            return new OffsetRange(0, SplittableDoFnTest.SDFWithMultipleOutputsPerBlockAndSideInputBase.MAX_INDEX);
        }
    }

    @BoundedPerElement
    private static class SDFWithMultipleOutputsPerBlockAndSideInputBounded extends SplittableDoFnTest.SDFWithMultipleOutputsPerBlockAndSideInputBase {
        private SDFWithMultipleOutputsPerBlockAndSideInputBounded(PCollectionView<String> sideInput, int numClaimsPerCall) {
            super(sideInput, numClaimsPerCall);
        }
    }

    @UnboundedPerElement
    private static class SDFWithMultipleOutputsPerBlockAndSideInputUnbounded extends SplittableDoFnTest.SDFWithMultipleOutputsPerBlockAndSideInputBase {
        private SDFWithMultipleOutputsPerBlockAndSideInputUnbounded(PCollectionView<String> sideInput, int numClaimsPerCall) {
            super(sideInput, numClaimsPerCall);
        }
    }

    @Test
    @Category({ ValidatesRunner.class, UsesBoundedSplittableParDo.class, UsesSplittableParDoWithWindowedSideInputs.class })
    public void testWindowedSideInputWithCheckpointsBounded() {
        testWindowedSideInputWithCheckpoints(BOUNDED);
    }

    @Test
    @Category({ ValidatesRunner.class, UsesUnboundedSplittableParDo.class, UsesSplittableParDoWithWindowedSideInputs.class })
    public void testWindowedSideInputWithCheckpointsUnbounded() {
        testWindowedSideInputWithCheckpoints(UNBOUNDED);
    }

    private static class SDFWithAdditionalOutputBase extends DoFn<Integer, String> {
        private final TupleTag<String> additionalOutput;

        private SDFWithAdditionalOutputBase(TupleTag<String> additionalOutput) {
            this.additionalOutput = additionalOutput;
        }

        @ProcessElement
        public void process(ProcessContext c, OffsetRangeTracker tracker) {
            checkState(tracker.tryClaim(tracker.currentRestriction().getFrom()));
            c.output(("main:" + (c.element())));
            c.output(additionalOutput, ("additional:" + (c.element())));
        }

        @GetInitialRestriction
        public OffsetRange getInitialRestriction(Integer value) {
            return new OffsetRange(0, 1);
        }
    }

    @BoundedPerElement
    private static class SDFWithAdditionalOutputBounded extends SplittableDoFnTest.SDFWithAdditionalOutputBase {
        private SDFWithAdditionalOutputBounded(TupleTag<String> additionalOutput) {
            super(additionalOutput);
        }
    }

    @UnboundedPerElement
    private static class SDFWithAdditionalOutputUnbounded extends SplittableDoFnTest.SDFWithAdditionalOutputBase {
        private SDFWithAdditionalOutputUnbounded(TupleTag<String> additionalOutput) {
            super(additionalOutput);
        }
    }

    @Test
    @Category({ ValidatesRunner.class, UsesBoundedSplittableParDo.class })
    public void testAdditionalOutputBounded() {
        testAdditionalOutput(BOUNDED);
    }

    @Test
    @Category({ ValidatesRunner.class, UsesUnboundedSplittableParDo.class })
    public void testAdditionalOutputUnbounded() {
        testAdditionalOutput(UNBOUNDED);
    }

    private static class SDFWithLifecycleBase extends DoFn<String, String> {
        private enum State {

            OUTSIDE_BUNDLE,
            INSIDE_BUNDLE,
            TORN_DOWN;}

        private transient SplittableDoFnTest.SDFWithLifecycleBase.State state;

        @GetInitialRestriction
        public OffsetRange getInitialRestriction(String value) {
            Assert.assertEquals(SplittableDoFnTest.SDFWithLifecycleBase.State.OUTSIDE_BUNDLE, state);
            return new OffsetRange(0, 1);
        }

        @SplitRestriction
        public void splitRestriction(String value, OffsetRange range, OutputReceiver<OffsetRange> receiver) {
            Assert.assertEquals(SplittableDoFnTest.SDFWithLifecycleBase.State.OUTSIDE_BUNDLE, state);
            receiver.output(range);
        }

        @Setup
        public void setUp() {
            Assert.assertEquals(null, state);
            state = SplittableDoFnTest.SDFWithLifecycleBase.State.OUTSIDE_BUNDLE;
        }

        @StartBundle
        public void startBundle() {
            Assert.assertEquals(SplittableDoFnTest.SDFWithLifecycleBase.State.OUTSIDE_BUNDLE, state);
            state = SplittableDoFnTest.SDFWithLifecycleBase.State.INSIDE_BUNDLE;
        }

        @ProcessElement
        public void processElement(ProcessContext c, OffsetRangeTracker tracker) {
            Assert.assertEquals(SplittableDoFnTest.SDFWithLifecycleBase.State.INSIDE_BUNDLE, state);
            Assert.assertTrue(tracker.tryClaim(0L));
            c.output(c.element());
        }

        @FinishBundle
        public void finishBundle() {
            Assert.assertEquals(SplittableDoFnTest.SDFWithLifecycleBase.State.INSIDE_BUNDLE, state);
            state = SplittableDoFnTest.SDFWithLifecycleBase.State.OUTSIDE_BUNDLE;
        }

        @Teardown
        public void tearDown() {
            Assert.assertEquals(SplittableDoFnTest.SDFWithLifecycleBase.State.OUTSIDE_BUNDLE, state);
            state = SplittableDoFnTest.SDFWithLifecycleBase.State.TORN_DOWN;
        }
    }

    @BoundedPerElement
    private static class SDFWithLifecycleBounded extends SplittableDoFnTest.SDFWithLifecycleBase {}

    @UnboundedPerElement
    private static class SDFWithLifecycleUnbounded extends SplittableDoFnTest.SDFWithLifecycleBase {}

    @Test
    @Category({ ValidatesRunner.class, UsesParDoLifecycle.class, UsesBoundedSplittableParDo.class })
    public void testLifecycleMethodsBounded() {
        testLifecycleMethods(BOUNDED);
    }

    @Test
    @Category({ ValidatesRunner.class, UsesParDoLifecycle.class, UsesUnboundedSplittableParDo.class })
    public void testLifecycleMethodsUnbounded() {
        testLifecycleMethods(UNBOUNDED);
    }

    @Test
    @Category(NeedsRunner.class)
    public void testBoundedness() {
        // use TestPipeline.create() because we assert without p.run();
        Pipeline p = TestPipeline.create();
        PCollection<String> foo = p.apply(Create.of("foo"));
        {
            PCollection<String> res = foo.apply(ParDo.of(new DoFn<String, String>() {
                @ProcessElement
                public void process(@Element
                String element, OffsetRangeTracker tracker) {
                    // Doesn't matter
                }

                @GetInitialRestriction
                public OffsetRange getInitialRestriction(String element) {
                    return new OffsetRange(0, 1);
                }
            }));
            Assert.assertEquals(PCollection.IsBounded.BOUNDED, res.isBounded());
        }
        {
            PCollection<String> res = foo.apply(ParDo.of(new DoFn<String, String>() {
                @ProcessElement
                public ProcessContinuation process(@Element
                String element, OffsetRangeTracker tracker) {
                    return stop();
                }

                @GetInitialRestriction
                public OffsetRange getInitialRestriction(String element) {
                    return new OffsetRange(0, 1);
                }
            }));
            Assert.assertEquals(PCollection.IsBounded.UNBOUNDED, res.isBounded());
        }
    }
}

