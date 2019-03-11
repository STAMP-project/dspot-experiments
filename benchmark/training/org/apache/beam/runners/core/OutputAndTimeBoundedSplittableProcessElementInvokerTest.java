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
package org.apache.beam.runners.core;


import SplittableProcessElementInvoker.Result;
import java.util.concurrent.TimeUnit;
import org.apache.beam.sdk.io.range.OffsetRange;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.splittabledofn.OffsetRangeTracker;
import org.apache.beam.vendor.guava.v20_0.com.google.common.util.concurrent.Uninterruptibles;
import org.hamcrest.Matchers;
import org.joda.time.Duration;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 * Tests for {@link OutputAndTimeBoundedSplittableProcessElementInvoker}.
 */
public class OutputAndTimeBoundedSplittableProcessElementInvokerTest {
    @Rule
    public transient ExpectedException e = ExpectedException.none();

    private static class SomeFn extends DoFn<Void, String> {
        private final Duration sleepBeforeFirstClaim;

        private final int numOutputsPerProcessCall;

        private final Duration sleepBeforeEachOutput;

        private SomeFn(Duration sleepBeforeFirstClaim, int numOutputsPerProcessCall, Duration sleepBeforeEachOutput) {
            this.sleepBeforeFirstClaim = sleepBeforeFirstClaim;
            this.numOutputsPerProcessCall = numOutputsPerProcessCall;
            this.sleepBeforeEachOutput = sleepBeforeEachOutput;
        }

        @ProcessElement
        public ProcessContinuation process(ProcessContext context, OffsetRangeTracker tracker) {
            Uninterruptibles.sleepUninterruptibly(sleepBeforeFirstClaim.getMillis(), TimeUnit.MILLISECONDS);
            for (long i = tracker.currentRestriction().getFrom(), numIterations = 1; tracker.tryClaim(i); ++i , ++numIterations) {
                Uninterruptibles.sleepUninterruptibly(sleepBeforeEachOutput.getMillis(), TimeUnit.MILLISECONDS);
                context.output(("" + i));
                if (numIterations == (numOutputsPerProcessCall)) {
                    return resume();
                }
            }
            return stop();
        }

        @GetInitialRestriction
        public OffsetRange getInitialRestriction(Void element) {
            throw new UnsupportedOperationException("Should not be called in this test");
        }
    }

    @Test
    public void testInvokeProcessElementOutputBounded() throws Exception {
        Result res = runTest(10000, Duration.ZERO, Integer.MAX_VALUE, Duration.ZERO);
        Assert.assertFalse(res.getContinuation().shouldResume());
        OffsetRange residualRange = res.getResidualRestriction();
        // Should process the first 100 elements.
        Assert.assertEquals(1000, residualRange.getFrom());
        Assert.assertEquals(10000, residualRange.getTo());
    }

    @Test
    public void testInvokeProcessElementTimeBounded() throws Exception {
        Result res = runTest(10000, Duration.ZERO, Integer.MAX_VALUE, Duration.millis(100));
        Assert.assertFalse(res.getContinuation().shouldResume());
        OffsetRange residualRange = res.getResidualRestriction();
        // Should process ideally around 30 elements - but due to timing flakiness, we can't enforce
        // that precisely. Just test that it's not egregiously off.
        Assert.assertThat(residualRange.getFrom(), Matchers.greaterThan(10L));
        Assert.assertThat(residualRange.getFrom(), Matchers.lessThan(100L));
        Assert.assertEquals(10000, residualRange.getTo());
    }

    @Test
    public void testInvokeProcessElementTimeBoundedWithStartupDelay() throws Exception {
        Result res = runTest(10000, Duration.standardSeconds(3), Integer.MAX_VALUE, Duration.millis(100));
        Assert.assertFalse(res.getContinuation().shouldResume());
        OffsetRange residualRange = res.getResidualRestriction();
        // Same as above, but this time it counts from the time of the first tryClaim() call
        Assert.assertThat(residualRange.getFrom(), Matchers.greaterThan(10L));
        Assert.assertThat(residualRange.getFrom(), Matchers.lessThan(100L));
        Assert.assertEquals(10000, residualRange.getTo());
    }

    @Test
    public void testInvokeProcessElementVoluntaryReturnStop() throws Exception {
        Result res = runTest(5, Duration.ZERO, Integer.MAX_VALUE, Duration.millis(100));
        Assert.assertFalse(res.getContinuation().shouldResume());
        Assert.assertNull(res.getResidualRestriction());
    }

    @Test
    public void testInvokeProcessElementVoluntaryReturnResume() throws Exception {
        Result res = runTest(10, Duration.ZERO, 5, Duration.millis(100));
        Assert.assertTrue(res.getContinuation().shouldResume());
        Assert.assertEquals(new OffsetRange(5, 10), res.getResidualRestriction());
    }

    @Test
    public void testInvokeProcessElementOutputDisallowedBeforeTryClaim() throws Exception {
        DoFn<Void, String> brokenFn = new DoFn<Void, String>() {
            @ProcessElement
            public void process(ProcessContext c, OffsetRangeTracker tracker) {
                c.output("foo");
            }

            @GetInitialRestriction
            public OffsetRange getInitialRestriction(Void element) {
                throw new UnsupportedOperationException("Should not be called in this test");
            }
        };
        e.expectMessage("Output is not allowed before tryClaim()");
        runTest(brokenFn, new OffsetRange(0, 5));
    }

    @Test
    public void testInvokeProcessElementOutputDisallowedAfterFailedTryClaim() throws Exception {
        DoFn<Void, String> brokenFn = new DoFn<Void, String>() {
            @ProcessElement
            public void process(ProcessContext c, OffsetRangeTracker tracker) {
                Assert.assertFalse(tracker.tryClaim(6L));
                c.output("foo");
            }

            @GetInitialRestriction
            public OffsetRange getInitialRestriction(Void element) {
                throw new UnsupportedOperationException("Should not be called in this test");
            }
        };
        e.expectMessage("Output is not allowed after a failed tryClaim()");
        runTest(brokenFn, new OffsetRange(0, 5));
    }
}

