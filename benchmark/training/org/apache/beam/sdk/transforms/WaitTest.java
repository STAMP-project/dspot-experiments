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


import Duration.ZERO;
import java.io.Serializable;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.beam.sdk.testing.NeedsRunner;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.testing.UsesTestStream;
import org.apache.beam.sdk.transforms.windowing.FixedWindows;
import org.apache.beam.sdk.transforms.windowing.GlobalWindows;
import org.apache.beam.sdk.transforms.windowing.SlidingWindows;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.TimestampedValue;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link Wait}.
 */
@RunWith(JUnit4.class)
public class WaitTest implements Serializable {
    @Rule
    public transient TestPipeline p = TestPipeline.create();

    private static class Event<T> {
        private final Instant processingTime;

        private final TimestampedValue<T> element;

        private final Instant watermarkUpdate;

        private Event(Instant processingTime, TimestampedValue<T> element) {
            this.processingTime = processingTime;
            this.element = element;
            this.watermarkUpdate = null;
        }

        private Event(Instant processingTime, Instant watermarkUpdate) {
            this.processingTime = processingTime;
            this.element = null;
            this.watermarkUpdate = watermarkUpdate;
        }

        @Override
        public String toString() {
            return org.apache.beam.vendor.guava.v20_0.com.google.common.base.MoreObjects.toStringHelper(this).add("processingTime", processingTime).add("element", element).add("watermarkUpdate", watermarkUpdate).toString();
        }
    }

    private static final AtomicReference<Instant> TEST_WAIT_MAX_MAIN_TIMESTAMP = new AtomicReference<>();

    @Test
    @Category({ NeedsRunner.class, UsesTestStream.class })
    public void testWaitWithSameFixedWindows() {
        /* duration */
        /* lateness */
        /* numMainElements */
        /* numSignalElements */
        testWaitWithParameters(Duration.standardMinutes(1), Duration.standardSeconds(15), 20, FixedWindows.of(Duration.standardSeconds(15)), 20, FixedWindows.of(Duration.standardSeconds(15)));
    }

    @Test
    @Category({ NeedsRunner.class, UsesTestStream.class })
    public void testWaitWithDifferentFixedWindows() {
        /* duration */
        /* lateness */
        /* numMainElements */
        /* numSignalElements */
        testWaitWithParameters(Duration.standardMinutes(1), Duration.standardSeconds(15), 20, FixedWindows.of(Duration.standardSeconds(15)), 20, FixedWindows.of(Duration.standardSeconds(7)));
    }

    @Test
    @Category({ NeedsRunner.class, UsesTestStream.class })
    public void testWaitWithSignalInSlidingWindows() {
        /* duration */
        /* lateness */
        /* numMainElements */
        /* numSignalElements */
        testWaitWithParameters(Duration.standardMinutes(1), Duration.standardSeconds(15), 20, FixedWindows.of(Duration.standardSeconds(15)), 20, SlidingWindows.of(Duration.standardSeconds(7)).every(Duration.standardSeconds(1)));
    }

    @Test
    @Category({ NeedsRunner.class, UsesTestStream.class })
    public void testWaitInGlobalWindow() {
        /* duration */
        /* lateness */
        /* numMainElements */
        /* numSignalElements */
        testWaitWithParameters(Duration.standardMinutes(1), Duration.standardSeconds(15), 20, new GlobalWindows(), 20, new GlobalWindows());
    }

    @Test
    @Category({ NeedsRunner.class, UsesTestStream.class })
    public void testWaitBoundedInDefaultWindow() {
        /* duration */
        /* lateness */
        /* numMainElements */
        /* numSignalElements */
        testWaitWithParameters(Duration.standardMinutes(1), Duration.standardSeconds(15), 20, null, 20, null);
    }

    @Test
    @Category({ NeedsRunner.class, UsesTestStream.class })
    public void testWaitWithSomeSignalWindowsEmpty() {
        /* duration */
        /* lateness */
        /* numMainElements */
        /* numSignalElements */
        testWaitWithParameters(Duration.standardMinutes(1), ZERO, 20, FixedWindows.of(Duration.standardSeconds(1)), 10, FixedWindows.of(Duration.standardSeconds(1)));
    }

    private static class Fire<T> extends PTransform<PCollection<T>, PCollection<T>> {
        @Override
        public PCollection<T> expand(PCollection<T> input) {
            return input.apply(WithKeys.of("")).apply(GroupByKey.create()).apply(Values.create()).apply(Flatten.iterables());
        }
    }
}

