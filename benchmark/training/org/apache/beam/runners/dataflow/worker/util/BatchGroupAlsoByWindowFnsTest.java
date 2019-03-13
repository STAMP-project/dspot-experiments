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
package org.apache.beam.runners.dataflow.worker.util;


import org.apache.beam.runners.core.InMemoryStateInternals;
import org.apache.beam.runners.core.StateInternals;
import org.apache.beam.runners.core.StateInternalsFactory;
import org.apache.beam.sdk.coders.Coder;
import org.apache.beam.sdk.coders.CoderRegistry;
import org.apache.beam.sdk.coders.KvCoder;
import org.apache.beam.sdk.coders.StringUtf8Coder;
import org.apache.beam.sdk.coders.VarLongCoder;
import org.apache.beam.sdk.transforms.Sum;
import org.apache.beam.sdk.transforms.windowing.AfterPane;
import org.apache.beam.sdk.transforms.windowing.FixedWindows;
import org.apache.beam.sdk.transforms.windowing.IntervalWindow;
import org.apache.beam.sdk.transforms.windowing.Sessions;
import org.apache.beam.sdk.util.AppliedCombineFn;
import org.apache.beam.sdk.values.WindowingStrategy;
import org.hamcrest.Matchers;
import org.joda.time.Duration;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Unit tests for the static factory methods in the factory class {@link BatchGroupAlsoByWindowsDoFns}.
 */
@RunWith(JUnit4.class)
public class BatchGroupAlsoByWindowFnsTest {
    private static class InMemoryStateInternalsFactory<K> implements StateInternalsFactory<K> {
        @Override
        public StateInternals stateInternalsForKey(K key) {
            return InMemoryStateInternals.forKey(key);
        }
    }

    @Test
    public void testCreateNoncombiningNonmerging() throws Exception {
        Coder<Long> inputCoder = VarLongCoder.of();
        WindowingStrategy<?, IntervalWindow> windowingStrategy = WindowingStrategy.of(FixedWindows.of(Duration.millis(10)));
        Assert.assertThat(BatchGroupAlsoByWindowsDoFns.createForIterable(windowingStrategy, new BatchGroupAlsoByWindowFnsTest.InMemoryStateInternalsFactory(), inputCoder), Matchers.instanceOf(BatchGroupAlsoByWindowViaIteratorsFn.class));
    }

    @Test
    public void testCreateNoncombiningMerging() throws Exception {
        Coder<Long> inputCoder = VarLongCoder.of();
        WindowingStrategy<?, IntervalWindow> windowingStrategy = WindowingStrategy.of(Sessions.withGapDuration(Duration.millis(10)));
        Assert.assertThat(BatchGroupAlsoByWindowsDoFns.createForIterable(windowingStrategy, new BatchGroupAlsoByWindowFnsTest.InMemoryStateInternalsFactory(), inputCoder), Matchers.instanceOf(BatchGroupAlsoByWindowViaOutputBufferFn.class));
    }

    @Test
    public void testCreateNoncombiningWithTrigger() throws Exception {
        Coder<Long> inputCoder = VarLongCoder.of();
        WindowingStrategy<?, IntervalWindow> windowingStrategy = WindowingStrategy.of(FixedWindows.of(Duration.millis(10))).withTrigger(AfterPane.elementCountAtLeast(1));
        Assert.assertThat(BatchGroupAlsoByWindowsDoFns.createForIterable(windowingStrategy, new BatchGroupAlsoByWindowFnsTest.InMemoryStateInternalsFactory(), inputCoder), Matchers.instanceOf(BatchGroupAlsoByWindowViaIteratorsFn.class));
    }

    @Test
    public void testCreateCombiningNonmerging() throws Exception {
        AppliedCombineFn<String, Long, ?, Long> appliedFn = AppliedCombineFn.withInputCoder(Sum.ofLongs(), CoderRegistry.createDefault(), KvCoder.of(StringUtf8Coder.of(), VarLongCoder.of()));
        WindowingStrategy<?, IntervalWindow> windowingStrategy = WindowingStrategy.of(FixedWindows.of(Duration.millis(10)));
        Assert.assertThat(BatchGroupAlsoByWindowsDoFns.create(windowingStrategy, appliedFn), Matchers.instanceOf(BatchGroupAlsoByWindowAndCombineFn.class));
    }

    @Test
    public void testCreateCombiningMerging() throws Exception {
        AppliedCombineFn<String, Long, ?, Long> appliedFn = AppliedCombineFn.withInputCoder(Sum.ofLongs(), CoderRegistry.createDefault(), KvCoder.of(StringUtf8Coder.of(), VarLongCoder.of()));
        WindowingStrategy<?, IntervalWindow> windowingStrategy = WindowingStrategy.of(Sessions.withGapDuration(Duration.millis(10)));
        Assert.assertThat(BatchGroupAlsoByWindowsDoFns.create(windowingStrategy, appliedFn), Matchers.instanceOf(BatchGroupAlsoByWindowAndCombineFn.class));
    }

    @Test
    public void testCreateCombiningWithTrigger() throws Exception {
        AppliedCombineFn<String, Long, ?, Long> appliedFn = AppliedCombineFn.withInputCoder(Sum.ofLongs(), CoderRegistry.createDefault(), KvCoder.of(StringUtf8Coder.of(), VarLongCoder.of()));
        WindowingStrategy<?, IntervalWindow> windowingStrategy = WindowingStrategy.of(Sessions.withGapDuration(Duration.millis(10))).withTrigger(AfterPane.elementCountAtLeast(1));
        Assert.assertThat(BatchGroupAlsoByWindowsDoFns.create(windowingStrategy, appliedFn), Matchers.instanceOf(BatchGroupAlsoByWindowAndCombineFn.class));
    }

    @Test
    public void testCreateNoncombiningReshuffle() throws Exception {
        Coder<Long> inputCoder = VarLongCoder.of();
        WindowingStrategy<?, IntervalWindow> windowingStrategy = WindowingStrategy.of(FixedWindows.of(Duration.millis(10))).withTrigger(new org.apache.beam.sdk.transforms.windowing.ReshuffleTrigger());
        Assert.assertThat(BatchGroupAlsoByWindowsDoFns.createForIterable(windowingStrategy, new BatchGroupAlsoByWindowFnsTest.InMemoryStateInternalsFactory<Long>(), inputCoder), Matchers.instanceOf(BatchGroupAlsoByWindowReshuffleFn.class));
    }
}

