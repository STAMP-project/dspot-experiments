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
package org.apache.beam.fn.harness;


import java.util.Collections;
import org.apache.beam.sdk.function.ThrowingFunction;
import org.apache.beam.sdk.transforms.windowing.BoundedWindow;
import org.apache.beam.sdk.transforms.windowing.GlobalWindows;
import org.apache.beam.sdk.transforms.windowing.Sessions;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableList;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.Iterables;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.Sets;
import org.hamcrest.Matchers;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link WindowMergingFnRunner}.
 */
@RunWith(JUnit4.class)
public class WindowMergingFnRunnerTest {
    @Test
    public void testWindowMergingWithNonMergingWindowFn() throws Exception {
        ThrowingFunction<KV<Object, Iterable<BoundedWindow>>, KV<Object, KV<Iterable<BoundedWindow>, Iterable<KV<BoundedWindow, Iterable<BoundedWindow>>>>>> mapFunction = WindowMergingFnRunner.createMapFunctionForPTransform("ptransformId", WindowMergingFnRunnerTest.createMergeTransformForWindowFn(new GlobalWindows()));
        KV<Object, Iterable<BoundedWindow>> input = KV.of("abc", ImmutableList.of(new org.apache.beam.sdk.transforms.windowing.IntervalWindow(Instant.now(), Duration.standardMinutes(1))));
        Assert.assertEquals(KV.of(input.getKey(), KV.of(input.getValue(), Collections.emptyList())), mapFunction.apply(input));
    }

    @Test
    public void testWindowMergingWithMergingWindowFn() throws Exception {
        ThrowingFunction<KV<Object, Iterable<BoundedWindow>>, KV<Object, KV<Iterable<BoundedWindow>, Iterable<KV<BoundedWindow, Iterable<BoundedWindow>>>>>> mapFunction = WindowMergingFnRunner.createMapFunctionForPTransform("ptransformId", WindowMergingFnRunnerTest.createMergeTransformForWindowFn(Sessions.withGapDuration(Duration.millis(5L))));
        // 7, 8 and 10 should all be merged. 1 and 20 should remain in the original set.
        BoundedWindow[] expectedToBeMerged = new BoundedWindow[]{ new org.apache.beam.sdk.transforms.windowing.IntervalWindow(new Instant(9L), new Instant(11L)), new org.apache.beam.sdk.transforms.windowing.IntervalWindow(new Instant(10L), new Instant(10L)), new org.apache.beam.sdk.transforms.windowing.IntervalWindow(new Instant(7L), new Instant(10L)) };
        Iterable<BoundedWindow> expectedToBeUnmerged = Sets.newHashSet(new org.apache.beam.sdk.transforms.windowing.IntervalWindow(new Instant(1L), new Instant(1L)), new org.apache.beam.sdk.transforms.windowing.IntervalWindow(new Instant(20L), new Instant(20L)));
        KV<Object, Iterable<BoundedWindow>> input = KV.of("abc", ImmutableList.<BoundedWindow>builder().add(expectedToBeMerged).addAll(expectedToBeUnmerged).build());
        KV<Object, KV<Iterable<BoundedWindow>, Iterable<KV<BoundedWindow, Iterable<BoundedWindow>>>>> output = mapFunction.apply(input);
        Assert.assertEquals(input.getKey(), output.getKey());
        Assert.assertEquals(expectedToBeUnmerged, output.getValue().getKey());
        KV<BoundedWindow, Iterable<BoundedWindow>> mergedOutput = Iterables.getOnlyElement(output.getValue().getValue());
        Assert.assertEquals(new org.apache.beam.sdk.transforms.windowing.IntervalWindow(new Instant(7L), new Instant(11L)), mergedOutput.getKey());
        Assert.assertThat(mergedOutput.getValue(), Matchers.containsInAnyOrder(expectedToBeMerged));
    }
}

