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
package org.apache.beam.runners.flink.translation.wrappers.streaming;


import DoFnOperator.FlinkTimerInternals;
import java.nio.ByteBuffer;
import org.apache.beam.runners.core.KeyedWorkItem;
import org.apache.beam.sdk.transforms.windowing.IntervalWindow;
import org.apache.beam.sdk.transforms.windowing.PaneInfo;
import org.apache.beam.sdk.util.WindowedValue;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.Iterables;
import org.apache.flink.runtime.checkpoint.OperatorSubtaskState;
import org.apache.flink.streaming.runtime.streamrecord.StreamRecord;
import org.apache.flink.streaming.util.KeyedOneInputStreamOperatorTestHarness;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link WindowDoFnOperator}.
 */
@RunWith(JUnit4.class)
public class WindowDoFnOperatorTest {
    @Test
    public void testRestore() throws Exception {
        // test harness
        KeyedOneInputStreamOperatorTestHarness<ByteBuffer, WindowedValue<KeyedWorkItem<Long, Long>>, WindowedValue<KV<Long, Long>>> testHarness = createTestHarness(getWindowDoFnOperator());
        testHarness.open();
        // process elements
        IntervalWindow window = new IntervalWindow(new Instant(0), Duration.millis(10000));
        testHarness.processWatermark(0L);
        testHarness.processElement(WindowDoFnOperatorTest.Item.builder().key(1L).timestamp(1L).value(100L).window(window).build().toStreamRecord());
        testHarness.processElement(WindowDoFnOperatorTest.Item.builder().key(1L).timestamp(2L).value(20L).window(window).build().toStreamRecord());
        testHarness.processElement(WindowDoFnOperatorTest.Item.builder().key(2L).timestamp(3L).value(77L).window(window).build().toStreamRecord());
        // create snapshot
        OperatorSubtaskState snapshot = testHarness.snapshot(0, 0);
        testHarness.close();
        // restore from the snapshot
        testHarness = createTestHarness(getWindowDoFnOperator());
        testHarness.initializeState(snapshot);
        testHarness.open();
        // close window
        testHarness.processWatermark(10000L);
        Iterable<WindowedValue<KV<Long, Long>>> output = StreamRecordStripper.stripStreamRecordFromWindowedValue(testHarness.getOutput());
        Assert.assertEquals(2, Iterables.size(output));
        MatcherAssert.assertThat(output, Matchers.containsInAnyOrder(WindowedValue.of(KV.of(1L, 120L), new Instant(9999), window, PaneInfo.createPane(true, true, ON_TIME)), WindowedValue.of(KV.of(2L, 77L), new Instant(9999), window, PaneInfo.createPane(true, true, ON_TIME))));
        // cleanup
        testHarness.close();
    }

    @Test
    public void testTimerCleanupOfPendingTimerList() throws Exception {
        // test harness
        WindowDoFnOperator<Long, Long, Long> windowDoFnOperator = getWindowDoFnOperator();
        KeyedOneInputStreamOperatorTestHarness<ByteBuffer, WindowedValue<KeyedWorkItem<Long, Long>>, WindowedValue<KV<Long, Long>>> testHarness = createTestHarness(windowDoFnOperator);
        testHarness.open();
        FlinkTimerInternals timerInternals = windowDoFnOperator.timerInternals;
        // process elements
        IntervalWindow window = new IntervalWindow(new Instant(0), Duration.millis(100));
        IntervalWindow window2 = new IntervalWindow(new Instant(100), Duration.millis(100));
        testHarness.processWatermark(0L);
        testHarness.processElement(WindowDoFnOperatorTest.Item.builder().key(1L).timestamp(1L).value(100L).window(window).build().toStreamRecord());
        testHarness.processElement(WindowDoFnOperatorTest.Item.builder().key(1L).timestamp(150L).value(150L).window(window2).build().toStreamRecord());
        MatcherAssert.assertThat(Iterables.size(timerInternals.pendingTimersById.keys()), Is.is(2));
        // close window
        testHarness.processWatermark(200L);
        MatcherAssert.assertThat(Iterables.size(timerInternals.pendingTimersById.keys()), Is.is(0));
        // cleanup
        testHarness.close();
    }

    private static class Item {
        static WindowDoFnOperatorTest.Item.ItemBuilder builder() {
            return new WindowDoFnOperatorTest.Item.ItemBuilder();
        }

        private long key;

        private long value;

        private long timestamp;

        private IntervalWindow window;

        StreamRecord<WindowedValue<KeyedWorkItem<Long, Long>>> toStreamRecord() {
            WindowedValue<Long> item = WindowedValue.of(value, new Instant(timestamp), window, NO_FIRING);
            WindowedValue<KeyedWorkItem<Long, Long>> keyedItem = WindowedValue.of(new SingletonKeyedWorkItem(key, item), new Instant(timestamp), window, NO_FIRING);
            return new StreamRecord(keyedItem);
        }

        private static final class ItemBuilder {
            private long key;

            private long value;

            private long timestamp;

            private IntervalWindow window;

            WindowDoFnOperatorTest.Item.ItemBuilder key(long key) {
                this.key = key;
                return this;
            }

            WindowDoFnOperatorTest.Item.ItemBuilder value(long value) {
                this.value = value;
                return this;
            }

            WindowDoFnOperatorTest.Item.ItemBuilder timestamp(long timestamp) {
                this.timestamp = timestamp;
                return this;
            }

            WindowDoFnOperatorTest.Item.ItemBuilder window(IntervalWindow window) {
                this.window = window;
                return this;
            }

            WindowDoFnOperatorTest.Item build() {
                WindowDoFnOperatorTest.Item item = new WindowDoFnOperatorTest.Item();
                item.key = this.key;
                item.value = this.value;
                item.window = this.window;
                item.timestamp = this.timestamp;
                return item;
            }
        }
    }
}

