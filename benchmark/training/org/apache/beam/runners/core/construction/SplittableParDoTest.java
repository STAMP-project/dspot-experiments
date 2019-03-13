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
package org.apache.beam.runners.core.construction;


import PCollection.IsBounded.BOUNDED;
import PCollection.IsBounded.UNBOUNDED;
import java.io.Serializable;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.splittabledofn.HasDefaultTracker;
import org.apache.beam.sdk.transforms.splittabledofn.RestrictionTracker;
import org.apache.beam.sdk.values.TupleTag;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link SplittableParDo}.
 */
@RunWith(JUnit4.class)
public class SplittableParDoTest {
    // ----------------- Tests for whether the transform sets boundedness correctly --------------
    private static class SomeRestriction implements Serializable , HasDefaultTracker<SplittableParDoTest.SomeRestriction, SplittableParDoTest.SomeRestrictionTracker> {
        @Override
        public SplittableParDoTest.SomeRestrictionTracker newTracker() {
            return new SplittableParDoTest.SomeRestrictionTracker(this);
        }
    }

    private static class SomeRestrictionTracker extends RestrictionTracker<SplittableParDoTest.SomeRestriction, Void> {
        private final SplittableParDoTest.SomeRestriction someRestriction;

        public SomeRestrictionTracker(SplittableParDoTest.SomeRestriction someRestriction) {
            this.someRestriction = someRestriction;
        }

        @Override
        protected boolean tryClaimImpl(Void position) {
            return false;
        }

        @Override
        public SplittableParDoTest.SomeRestriction currentRestriction() {
            return someRestriction;
        }

        @Override
        public SplittableParDoTest.SomeRestriction checkpoint() {
            return someRestriction;
        }

        @Override
        public void checkDone() {
        }
    }

    private static class BoundedFakeFn extends DoFn<Integer, String> {
        @ProcessElement
        public void processElement(ProcessContext context, SplittableParDoTest.SomeRestrictionTracker tracker) {
        }

        @GetInitialRestriction
        public SplittableParDoTest.SomeRestriction getInitialRestriction(Integer element) {
            return null;
        }
    }

    private static class UnboundedFakeFn extends DoFn<Integer, String> {
        @ProcessElement
        public ProcessContinuation processElement(ProcessContext context, SplittableParDoTest.SomeRestrictionTracker tracker) {
            return stop();
        }

        @GetInitialRestriction
        public SplittableParDoTest.SomeRestriction getInitialRestriction(Integer element) {
            return null;
        }
    }

    private static final TupleTag<String> MAIN_OUTPUT_TAG = new TupleTag<String>() {};

    @Rule
    public TestPipeline pipeline = TestPipeline.create();

    @Test
    public void testBoundednessForBoundedFn() {
        pipeline.enableAbandonedNodeEnforcement(false);
        DoFn<Integer, String> boundedFn = new SplittableParDoTest.BoundedFakeFn();
        Assert.assertEquals("Applying a bounded SDF to a bounded collection produces a bounded collection", BOUNDED, applySplittableParDo("bounded to bounded", SplittableParDoTest.makeBoundedCollection(pipeline), boundedFn).isBounded());
        Assert.assertEquals("Applying a bounded SDF to an unbounded collection produces an unbounded collection", UNBOUNDED, applySplittableParDo("bounded to unbounded", SplittableParDoTest.makeUnboundedCollection(pipeline), boundedFn).isBounded());
    }

    @Test
    public void testBoundednessForUnboundedFn() {
        pipeline.enableAbandonedNodeEnforcement(false);
        DoFn<Integer, String> unboundedFn = new SplittableParDoTest.UnboundedFakeFn();
        Assert.assertEquals("Applying an unbounded SDF to a bounded collection produces a bounded collection", UNBOUNDED, applySplittableParDo("unbounded to bounded", SplittableParDoTest.makeBoundedCollection(pipeline), unboundedFn).isBounded());
        Assert.assertEquals("Applying an unbounded SDF to an unbounded collection produces an unbounded collection", UNBOUNDED, applySplittableParDo("unbounded to unbounded", SplittableParDoTest.makeUnboundedCollection(pipeline), unboundedFn).isBounded());
    }
}

