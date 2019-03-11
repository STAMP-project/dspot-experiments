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


import java.util.List;
import org.apache.beam.runners.dataflow.worker.GroupAlsoByWindowParDoFnFactory.MergingCombineFn;
import org.apache.beam.sdk.coders.VarLongCoder;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.transforms.Combine.BinaryCombineLongFn;
import org.apache.beam.sdk.transforms.Sum;
import org.apache.beam.sdk.transforms.windowing.FixedWindows;
import org.apache.beam.sdk.transforms.windowing.WindowFn;
import org.apache.beam.sdk.values.WindowingStrategy;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableList;
import org.hamcrest.Matchers;
import org.joda.time.Duration;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link GroupAlsoByWindowParDoFnFactory}.
 */
@RunWith(JUnit4.class)
public class GroupAlsoByWindowParDoFnFactoryTest {
    @Rule
    public TestPipeline p = TestPipeline.create().enableAbandonedNodeEnforcement(false);

    @Test
    public void testJavaWindowingStrategyDeserialization() throws Exception {
        WindowFn windowFn = FixedWindows.of(Duration.millis(17));
        WindowingStrategy windowingStrategy = WindowingStrategy.of(windowFn);
        Assert.assertThat(windowingStrategy.getWindowFn(), Matchers.equalTo(windowFn));
    }

    @Test
    public void testMultipleAccumulatorsInMergingCombineFn() {
        BinaryCombineLongFn originalFn = Sum.ofLongs();
        MergingCombineFn<Void, long[]> fn = new MergingCombineFn(originalFn, originalFn.getAccumulatorCoder(p.getCoderRegistry(), VarLongCoder.of()));
        long[] inputAccum = originalFn.createAccumulator();
        inputAccum = originalFn.addInput(inputAccum, 1L);
        inputAccum = originalFn.addInput(inputAccum, 2L);
        Assert.assertThat(inputAccum[0], Matchers.equalTo(3L));
        long[] otherAccum = originalFn.createAccumulator();
        otherAccum = originalFn.addInput(otherAccum, 4L);
        Assert.assertThat(otherAccum[0], Matchers.equalTo(4L));
        List<long[]> first = fn.createAccumulator();
        first = fn.addInput(first, inputAccum);
        Assert.assertThat(first, Matchers.hasItem(inputAccum));
        Assert.assertThat(inputAccum.length, Matchers.equalTo(1));
        Assert.assertThat(inputAccum[0], Matchers.equalTo(3L));
        List<long[]> second = fn.createAccumulator();
        second = fn.addInput(second, inputAccum);
        Assert.assertThat(second, Matchers.hasItem(inputAccum));
        Assert.assertThat(inputAccum.length, Matchers.equalTo(1));
        Assert.assertThat(inputAccum[0], Matchers.equalTo(3L));
        List<long[]> mergeToSecond = fn.createAccumulator();
        mergeToSecond = fn.addInput(mergeToSecond, otherAccum);
        Assert.assertThat(mergeToSecond, Matchers.hasItem(otherAccum));
        Assert.assertThat(otherAccum.length, Matchers.equalTo(1));
        Assert.assertThat(otherAccum[0], Matchers.equalTo(4L));
        List<long[]> firstSelfMerged = fn.mergeAccumulators(ImmutableList.of(first));
        List<long[]> compactedFirst = fn.compact(firstSelfMerged);
        Assert.assertThat(firstSelfMerged, Matchers.equalTo(compactedFirst));
        Assert.assertThat(firstSelfMerged, Matchers.hasSize(1));
        Assert.assertThat(firstSelfMerged.get(0)[0], Matchers.equalTo(3L));
        List<long[]> secondMerged = fn.mergeAccumulators(ImmutableList.of(second, mergeToSecond));
        List<long[]> secondCompacted = fn.compact(secondMerged);
        Assert.assertThat(secondCompacted, Matchers.hasSize(1));
        Assert.assertThat(secondCompacted.get(0)[0], Matchers.equalTo(7L));
        Assert.assertThat(firstSelfMerged, Matchers.equalTo(compactedFirst));
        Assert.assertThat(firstSelfMerged, Matchers.hasSize(1));
        Assert.assertThat(firstSelfMerged.get(0)[0], Matchers.equalTo(3L));
    }
}

