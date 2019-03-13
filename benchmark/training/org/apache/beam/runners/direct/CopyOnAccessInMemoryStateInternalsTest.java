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


import TimestampCombiner.EARLIEST;
import org.apache.beam.runners.core.StateNamespace;
import org.apache.beam.runners.core.StateNamespaceForTest;
import org.apache.beam.runners.core.StateNamespaces;
import org.apache.beam.runners.core.StateTag;
import org.apache.beam.runners.core.StateTags;
import org.apache.beam.sdk.coders.CannotProvideCoderException;
import org.apache.beam.sdk.coders.CoderRegistry;
import org.apache.beam.sdk.coders.StringUtf8Coder;
import org.apache.beam.sdk.coders.VarIntCoder;
import org.apache.beam.sdk.state.BagState;
import org.apache.beam.sdk.state.CombiningState;
import org.apache.beam.sdk.state.GroupingState;
import org.apache.beam.sdk.state.MapState;
import org.apache.beam.sdk.state.SetState;
import org.apache.beam.sdk.state.ValueState;
import org.apache.beam.sdk.state.WatermarkHoldState;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.transforms.Combine.CombineFn;
import org.apache.beam.sdk.transforms.Sum;
import org.apache.beam.sdk.transforms.windowing.BoundedWindow;
import org.apache.beam.sdk.transforms.windowing.TimestampCombiner;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.Lists;
import org.hamcrest.Matchers;
import org.joda.time.Instant;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;


/**
 * Tests for {@link CopyOnAccessInMemoryStateInternals}.
 */
@RunWith(JUnit4.class)
public class CopyOnAccessInMemoryStateInternalsTest {
    @Rule
    public final TestPipeline pipeline = TestPipeline.create();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private String key = "foo";

    @Test
    public void testGetWithEmpty() {
        CopyOnAccessInMemoryStateInternals<String> internals = CopyOnAccessInMemoryStateInternals.withUnderlying(key, null);
        StateNamespace namespace = new StateNamespaceForTest("foo");
        StateTag<BagState<String>> bagTag = StateTags.bag("foo", StringUtf8Coder.of());
        BagState<String> stringBag = internals.state(namespace, bagTag);
        Assert.assertThat(stringBag.read(), Matchers.emptyIterable());
        stringBag.add("bar");
        stringBag.add("baz");
        Assert.assertThat(stringBag.read(), Matchers.containsInAnyOrder("baz", "bar"));
        BagState<String> reReadStringBag = internals.state(namespace, bagTag);
        Assert.assertThat(reReadStringBag.read(), Matchers.containsInAnyOrder("baz", "bar"));
    }

    @Test
    public void testGetWithAbsentInUnderlying() {
        CopyOnAccessInMemoryStateInternals<String> underlying = CopyOnAccessInMemoryStateInternals.withUnderlying(key, null);
        CopyOnAccessInMemoryStateInternals<String> internals = CopyOnAccessInMemoryStateInternals.withUnderlying(key, underlying);
        StateNamespace namespace = new StateNamespaceForTest("foo");
        StateTag<BagState<String>> bagTag = StateTags.bag("foo", StringUtf8Coder.of());
        BagState<String> stringBag = internals.state(namespace, bagTag);
        Assert.assertThat(stringBag.read(), Matchers.emptyIterable());
        stringBag.add("bar");
        stringBag.add("baz");
        Assert.assertThat(stringBag.read(), Matchers.containsInAnyOrder("baz", "bar"));
        BagState<String> reReadVoidBag = internals.state(namespace, bagTag);
        Assert.assertThat(reReadVoidBag.read(), Matchers.containsInAnyOrder("baz", "bar"));
        BagState<String> underlyingState = underlying.state(namespace, bagTag);
        Assert.assertThat(underlyingState.read(), Matchers.emptyIterable());
    }

    /**
     * Tests that retrieving state with an underlying StateInternals with an existing value returns a
     * value that initially has equal value to the provided state but can be modified without
     * modifying the existing state.
     */
    @Test
    public void testGetWithPresentInUnderlying() {
        CopyOnAccessInMemoryStateInternals<String> underlying = CopyOnAccessInMemoryStateInternals.withUnderlying(key, null);
        StateNamespace namespace = new StateNamespaceForTest("foo");
        StateTag<ValueState<String>> valueTag = StateTags.value("foo", StringUtf8Coder.of());
        ValueState<String> underlyingValue = underlying.state(namespace, valueTag);
        Assert.assertThat(underlyingValue.read(), Matchers.nullValue(String.class));
        underlyingValue.write("bar");
        Assert.assertThat(underlyingValue.read(), Matchers.equalTo("bar"));
        CopyOnAccessInMemoryStateInternals<String> internals = CopyOnAccessInMemoryStateInternals.withUnderlying(key, underlying);
        ValueState<String> copyOnAccessState = internals.state(namespace, valueTag);
        Assert.assertThat(copyOnAccessState.read(), Matchers.equalTo("bar"));
        copyOnAccessState.write("baz");
        Assert.assertThat(copyOnAccessState.read(), Matchers.equalTo("baz"));
        Assert.assertThat(underlyingValue.read(), Matchers.equalTo("bar"));
        ValueState<String> reReadUnderlyingValue = underlying.state(namespace, valueTag);
        Assert.assertThat(underlyingValue.read(), Matchers.equalTo(reReadUnderlyingValue.read()));
    }

    @Test
    public void testBagStateWithUnderlying() {
        CopyOnAccessInMemoryStateInternals<String> underlying = CopyOnAccessInMemoryStateInternals.withUnderlying(key, null);
        StateNamespace namespace = new StateNamespaceForTest("foo");
        StateTag<BagState<Integer>> valueTag = StateTags.bag("foo", VarIntCoder.of());
        BagState<Integer> underlyingValue = underlying.state(namespace, valueTag);
        Assert.assertThat(underlyingValue.read(), Matchers.emptyIterable());
        underlyingValue.add(1);
        Assert.assertThat(underlyingValue.read(), Matchers.containsInAnyOrder(1));
        CopyOnAccessInMemoryStateInternals<String> internals = CopyOnAccessInMemoryStateInternals.withUnderlying(key, underlying);
        BagState<Integer> copyOnAccessState = internals.state(namespace, valueTag);
        Assert.assertThat(copyOnAccessState.read(), Matchers.containsInAnyOrder(1));
        copyOnAccessState.add(4);
        Assert.assertThat(copyOnAccessState.read(), Matchers.containsInAnyOrder(4, 1));
        Assert.assertThat(underlyingValue.read(), Matchers.containsInAnyOrder(1));
        BagState<Integer> reReadUnderlyingValue = underlying.state(namespace, valueTag);
        Assert.assertThat(Lists.newArrayList(underlyingValue.read()), Matchers.equalTo(Lists.newArrayList(reReadUnderlyingValue.read())));
    }

    @Test
    public void testSetStateWithUnderlying() {
        CopyOnAccessInMemoryStateInternals<String> underlying = CopyOnAccessInMemoryStateInternals.withUnderlying(key, null);
        StateNamespace namespace = new StateNamespaceForTest("foo");
        StateTag<SetState<Integer>> valueTag = StateTags.set("foo", VarIntCoder.of());
        SetState<Integer> underlyingValue = underlying.state(namespace, valueTag);
        Assert.assertThat(underlyingValue.read(), Matchers.emptyIterable());
        underlyingValue.add(1);
        Assert.assertThat(underlyingValue.read(), Matchers.containsInAnyOrder(1));
        CopyOnAccessInMemoryStateInternals<String> internals = CopyOnAccessInMemoryStateInternals.withUnderlying(key, underlying);
        SetState<Integer> copyOnAccessState = internals.state(namespace, valueTag);
        Assert.assertThat(copyOnAccessState.read(), Matchers.containsInAnyOrder(1));
        copyOnAccessState.add(4);
        Assert.assertThat(copyOnAccessState.read(), Matchers.containsInAnyOrder(4, 1));
        Assert.assertThat(underlyingValue.read(), Matchers.containsInAnyOrder(1));
        SetState<Integer> reReadUnderlyingValue = underlying.state(namespace, valueTag);
        Assert.assertThat(underlyingValue.read(), Matchers.equalTo(reReadUnderlyingValue.read()));
    }

    @Test
    public void testMapStateWithUnderlying() {
        CopyOnAccessInMemoryStateInternals<String> underlying = CopyOnAccessInMemoryStateInternals.withUnderlying(key, null);
        StateNamespace namespace = new StateNamespaceForTest("foo");
        StateTag<MapState<String, Integer>> valueTag = StateTags.map("foo", StringUtf8Coder.of(), VarIntCoder.of());
        MapState<String, Integer> underlyingValue = underlying.state(namespace, valueTag);
        Assert.assertThat(underlyingValue.entries().read(), Matchers.emptyIterable());
        underlyingValue.put("hello", 1);
        Assert.assertThat(underlyingValue.get("hello").read(), Matchers.equalTo(1));
        CopyOnAccessInMemoryStateInternals<String> internals = CopyOnAccessInMemoryStateInternals.withUnderlying(key, underlying);
        MapState<String, Integer> copyOnAccessState = internals.state(namespace, valueTag);
        Assert.assertThat(copyOnAccessState.get("hello").read(), Matchers.equalTo(1));
        copyOnAccessState.put("world", 4);
        Assert.assertThat(copyOnAccessState.get("hello").read(), Matchers.equalTo(1));
        Assert.assertThat(copyOnAccessState.get("world").read(), Matchers.equalTo(4));
        Assert.assertThat(underlyingValue.get("hello").read(), Matchers.equalTo(1));
        Assert.assertNull(underlyingValue.get("world").read());
        MapState<String, Integer> reReadUnderlyingValue = underlying.state(namespace, valueTag);
        Assert.assertThat(underlyingValue.entries().read(), Matchers.equalTo(reReadUnderlyingValue.entries().read()));
    }

    @Test
    public void testAccumulatorCombiningStateWithUnderlying() throws CannotProvideCoderException {
        CopyOnAccessInMemoryStateInternals<String> underlying = CopyOnAccessInMemoryStateInternals.withUnderlying(key, null);
        CombineFn<Long, long[], Long> sumLongFn = Sum.ofLongs();
        StateNamespace namespace = new StateNamespaceForTest("foo");
        CoderRegistry reg = pipeline.getCoderRegistry();
        StateTag<CombiningState<Long, long[], Long>> stateTag = StateTags.combiningValue("summer", sumLongFn.getAccumulatorCoder(reg, reg.getCoder(Long.class)), sumLongFn);
        GroupingState<Long, Long> underlyingValue = underlying.state(namespace, stateTag);
        Assert.assertThat(underlyingValue.read(), Matchers.equalTo(0L));
        underlyingValue.add(1L);
        Assert.assertThat(underlyingValue.read(), Matchers.equalTo(1L));
        CopyOnAccessInMemoryStateInternals<String> internals = CopyOnAccessInMemoryStateInternals.withUnderlying(key, underlying);
        GroupingState<Long, Long> copyOnAccessState = internals.state(namespace, stateTag);
        Assert.assertThat(copyOnAccessState.read(), Matchers.equalTo(1L));
        copyOnAccessState.add(4L);
        Assert.assertThat(copyOnAccessState.read(), Matchers.equalTo(5L));
        Assert.assertThat(underlyingValue.read(), Matchers.equalTo(1L));
        GroupingState<Long, Long> reReadUnderlyingValue = underlying.state(namespace, stateTag);
        Assert.assertThat(underlyingValue.read(), Matchers.equalTo(reReadUnderlyingValue.read()));
    }

    @Test
    public void testWatermarkHoldStateWithUnderlying() {
        CopyOnAccessInMemoryStateInternals<String> underlying = CopyOnAccessInMemoryStateInternals.withUnderlying(key, null);
        TimestampCombiner timestampCombiner = TimestampCombiner.EARLIEST;
        StateNamespace namespace = new StateNamespaceForTest("foo");
        StateTag<WatermarkHoldState> stateTag = StateTags.watermarkStateInternal("wmstate", timestampCombiner);
        WatermarkHoldState underlyingValue = underlying.state(namespace, stateTag);
        Assert.assertThat(underlyingValue.read(), Matchers.nullValue());
        underlyingValue.add(new Instant(250L));
        Assert.assertThat(underlyingValue.read(), Matchers.equalTo(new Instant(250L)));
        CopyOnAccessInMemoryStateInternals<String> internals = CopyOnAccessInMemoryStateInternals.withUnderlying(key, underlying);
        WatermarkHoldState copyOnAccessState = internals.state(namespace, stateTag);
        Assert.assertThat(copyOnAccessState.read(), Matchers.equalTo(new Instant(250L)));
        copyOnAccessState.add(new Instant(100L));
        Assert.assertThat(copyOnAccessState.read(), Matchers.equalTo(new Instant(100L)));
        Assert.assertThat(underlyingValue.read(), Matchers.equalTo(new Instant(250L)));
        copyOnAccessState.add(new Instant(500L));
        Assert.assertThat(copyOnAccessState.read(), Matchers.equalTo(new Instant(100L)));
        WatermarkHoldState reReadUnderlyingValue = underlying.state(namespace, stateTag);
        Assert.assertThat(underlyingValue.read(), Matchers.equalTo(reReadUnderlyingValue.read()));
    }

    @Test
    public void testCommitWithoutUnderlying() {
        CopyOnAccessInMemoryStateInternals<String> internals = CopyOnAccessInMemoryStateInternals.withUnderlying(key, null);
        StateNamespace namespace = new StateNamespaceForTest("foo");
        StateTag<BagState<String>> bagTag = StateTags.bag("foo", StringUtf8Coder.of());
        BagState<String> stringBag = internals.state(namespace, bagTag);
        Assert.assertThat(stringBag.read(), Matchers.emptyIterable());
        stringBag.add("bar");
        stringBag.add("baz");
        Assert.assertThat(stringBag.read(), Matchers.containsInAnyOrder("baz", "bar"));
        internals.commit();
        BagState<String> reReadStringBag = internals.state(namespace, bagTag);
        Assert.assertThat(reReadStringBag.read(), Matchers.containsInAnyOrder("baz", "bar"));
        Assert.assertThat(internals.isEmpty(), Matchers.is(false));
    }

    @Test
    public void testCommitWithUnderlying() {
        CopyOnAccessInMemoryStateInternals<String> underlying = CopyOnAccessInMemoryStateInternals.withUnderlying(key, null);
        CopyOnAccessInMemoryStateInternals<String> internals = CopyOnAccessInMemoryStateInternals.withUnderlying(key, underlying);
        StateNamespace namespace = new StateNamespaceForTest("foo");
        StateTag<BagState<String>> bagTag = StateTags.bag("foo", StringUtf8Coder.of());
        BagState<String> stringBag = underlying.state(namespace, bagTag);
        Assert.assertThat(stringBag.read(), Matchers.emptyIterable());
        stringBag.add("bar");
        stringBag.add("baz");
        internals.commit();
        BagState<String> reReadStringBag = internals.state(namespace, bagTag);
        Assert.assertThat(reReadStringBag.read(), Matchers.containsInAnyOrder("baz", "bar"));
        reReadStringBag.add("spam");
        BagState<String> underlyingState = underlying.state(namespace, bagTag);
        Assert.assertThat(underlyingState.read(), Matchers.containsInAnyOrder("spam", "bar", "baz"));
        Assert.assertThat(underlyingState, Matchers.is(Matchers.theInstance(stringBag)));
        Assert.assertThat(internals.isEmpty(), Matchers.is(false));
    }

    @Test
    public void testCommitWithClearedInUnderlying() {
        CopyOnAccessInMemoryStateInternals<String> underlying = CopyOnAccessInMemoryStateInternals.withUnderlying(key, null);
        CopyOnAccessInMemoryStateInternals<String> secondUnderlying = Mockito.spy(CopyOnAccessInMemoryStateInternals.withUnderlying(key, underlying));
        CopyOnAccessInMemoryStateInternals<String> internals = CopyOnAccessInMemoryStateInternals.withUnderlying(key, secondUnderlying);
        StateNamespace namespace = new StateNamespaceForTest("foo");
        StateTag<BagState<String>> bagTag = StateTags.bag("foo", StringUtf8Coder.of());
        BagState<String> stringBag = underlying.state(namespace, bagTag);
        Assert.assertThat(stringBag.read(), Matchers.emptyIterable());
        stringBag.add("bar");
        stringBag.add("baz");
        stringBag.clear();
        // We should not read through the cleared bag
        secondUnderlying.commit();
        // Should not be visible
        stringBag.add("foo");
        internals.commit();
        BagState<String> internalsStringBag = internals.state(namespace, bagTag);
        Assert.assertThat(internalsStringBag.read(), Matchers.emptyIterable());
        Mockito.verify(secondUnderlying, Mockito.never()).state(namespace, bagTag);
        Assert.assertThat(internals.isEmpty(), Matchers.is(false));
    }

    @Test
    public void testCommitWithOverwrittenUnderlying() {
        CopyOnAccessInMemoryStateInternals<String> underlying = CopyOnAccessInMemoryStateInternals.withUnderlying(key, null);
        CopyOnAccessInMemoryStateInternals<String> internals = CopyOnAccessInMemoryStateInternals.withUnderlying(key, underlying);
        StateNamespace namespace = new StateNamespaceForTest("foo");
        StateTag<BagState<String>> bagTag = StateTags.bag("foo", StringUtf8Coder.of());
        BagState<String> stringBag = underlying.state(namespace, bagTag);
        Assert.assertThat(stringBag.read(), Matchers.emptyIterable());
        stringBag.add("bar");
        stringBag.add("baz");
        BagState<String> internalsState = internals.state(namespace, bagTag);
        internalsState.add("eggs");
        internalsState.add("ham");
        internalsState.add("0x00ff00");
        internalsState.add("&");
        internals.commit();
        BagState<String> reReadInternalState = internals.state(namespace, bagTag);
        Assert.assertThat(reReadInternalState.read(), Matchers.containsInAnyOrder("bar", "baz", "0x00ff00", "eggs", "&", "ham"));
        BagState<String> reReadUnderlyingState = underlying.state(namespace, bagTag);
        Assert.assertThat(reReadUnderlyingState.read(), Matchers.containsInAnyOrder("bar", "baz"));
    }

    @Test
    public void testCommitWithAddedUnderlying() {
        CopyOnAccessInMemoryStateInternals<String> underlying = CopyOnAccessInMemoryStateInternals.withUnderlying(key, null);
        CopyOnAccessInMemoryStateInternals<String> internals = CopyOnAccessInMemoryStateInternals.withUnderlying(key, underlying);
        internals.commit();
        StateNamespace namespace = new StateNamespaceForTest("foo");
        StateTag<BagState<String>> bagTag = StateTags.bag("foo", StringUtf8Coder.of());
        BagState<String> stringBag = underlying.state(namespace, bagTag);
        Assert.assertThat(stringBag.read(), Matchers.emptyIterable());
        stringBag.add("bar");
        stringBag.add("baz");
        BagState<String> internalState = internals.state(namespace, bagTag);
        Assert.assertThat(internalState.read(), Matchers.emptyIterable());
        BagState<String> reReadUnderlyingState = underlying.state(namespace, bagTag);
        Assert.assertThat(reReadUnderlyingState.read(), Matchers.containsInAnyOrder("bar", "baz"));
    }

    @Test
    public void testCommitWithEmptyTableIsEmpty() {
        CopyOnAccessInMemoryStateInternals<String> internals = CopyOnAccessInMemoryStateInternals.withUnderlying(key, null);
        internals.commit();
        Assert.assertThat(internals.isEmpty(), Matchers.is(true));
    }

    @Test
    public void testCommitWithOnlyClearedValuesIsEmpty() {
        CopyOnAccessInMemoryStateInternals<String> internals = CopyOnAccessInMemoryStateInternals.withUnderlying(key, null);
        StateNamespace namespace = new StateNamespaceForTest("foo");
        StateTag<BagState<String>> bagTag = StateTags.bag("foo", StringUtf8Coder.of());
        BagState<String> stringBag = internals.state(namespace, bagTag);
        Assert.assertThat(stringBag.read(), Matchers.emptyIterable());
        stringBag.add("foo");
        stringBag.clear();
        internals.commit();
        Assert.assertThat(internals.isEmpty(), Matchers.is(true));
    }

    @Test
    public void testCommitWithEmptyNewAndFullUnderlyingIsNotEmpty() {
        CopyOnAccessInMemoryStateInternals<String> underlying = CopyOnAccessInMemoryStateInternals.withUnderlying(key, null);
        CopyOnAccessInMemoryStateInternals<String> internals = CopyOnAccessInMemoryStateInternals.withUnderlying(key, underlying);
        StateNamespace namespace = new StateNamespaceForTest("foo");
        StateTag<BagState<String>> bagTag = StateTags.bag("foo", StringUtf8Coder.of());
        BagState<String> stringBag = underlying.state(namespace, bagTag);
        Assert.assertThat(stringBag.read(), Matchers.emptyIterable());
        stringBag.add("bar");
        stringBag.add("baz");
        internals.commit();
        Assert.assertThat(internals.isEmpty(), Matchers.is(false));
    }

    @Test
    public void testGetEarliestWatermarkHoldAfterCommit() {
        BoundedWindow first = new BoundedWindow() {
            @Override
            public Instant maxTimestamp() {
                return new Instant(2048L);
            }
        };
        BoundedWindow second = new BoundedWindow() {
            @Override
            public Instant maxTimestamp() {
                return new Instant(689743L);
            }
        };
        CopyOnAccessInMemoryStateInternals<String> internals = CopyOnAccessInMemoryStateInternals.withUnderlying("foo", null);
        StateTag<WatermarkHoldState> firstHoldAddress = StateTags.watermarkStateInternal("foo", EARLIEST);
        WatermarkHoldState firstHold = internals.state(StateNamespaces.window(null, first), firstHoldAddress);
        firstHold.add(new Instant(22L));
        StateTag<WatermarkHoldState> secondHoldAddress = StateTags.watermarkStateInternal("foo", EARLIEST);
        WatermarkHoldState secondHold = internals.state(StateNamespaces.window(null, second), secondHoldAddress);
        secondHold.add(new Instant(2L));
        internals.commit();
        Assert.assertThat(internals.getEarliestWatermarkHold(), Matchers.equalTo(new Instant(2L)));
    }

    @Test
    public void testGetEarliestWatermarkHoldWithEarliestInUnderlyingTable() {
        BoundedWindow first = new BoundedWindow() {
            @Override
            public Instant maxTimestamp() {
                return new Instant(2048L);
            }
        };
        BoundedWindow second = new BoundedWindow() {
            @Override
            public Instant maxTimestamp() {
                return new Instant(689743L);
            }
        };
        CopyOnAccessInMemoryStateInternals<String> underlying = CopyOnAccessInMemoryStateInternals.withUnderlying("foo", null);
        StateTag<WatermarkHoldState> firstHoldAddress = StateTags.watermarkStateInternal("foo", EARLIEST);
        WatermarkHoldState firstHold = underlying.state(StateNamespaces.window(null, first), firstHoldAddress);
        firstHold.add(new Instant(22L));
        CopyOnAccessInMemoryStateInternals<String> internals = CopyOnAccessInMemoryStateInternals.withUnderlying("foo", underlying.commit());
        StateTag<WatermarkHoldState> secondHoldAddress = StateTags.watermarkStateInternal("foo", EARLIEST);
        WatermarkHoldState secondHold = internals.state(StateNamespaces.window(null, second), secondHoldAddress);
        secondHold.add(new Instant(244L));
        internals.commit();
        Assert.assertThat(internals.getEarliestWatermarkHold(), Matchers.equalTo(new Instant(22L)));
    }

    @Test
    public void testGetEarliestWatermarkHoldWithEarliestInNewTable() {
        BoundedWindow first = new BoundedWindow() {
            @Override
            public Instant maxTimestamp() {
                return new Instant(2048L);
            }
        };
        BoundedWindow second = new BoundedWindow() {
            @Override
            public Instant maxTimestamp() {
                return new Instant(689743L);
            }
        };
        CopyOnAccessInMemoryStateInternals<String> underlying = CopyOnAccessInMemoryStateInternals.withUnderlying("foo", null);
        StateTag<WatermarkHoldState> firstHoldAddress = StateTags.watermarkStateInternal("foo", EARLIEST);
        WatermarkHoldState firstHold = underlying.state(StateNamespaces.window(null, first), firstHoldAddress);
        firstHold.add(new Instant(224L));
        CopyOnAccessInMemoryStateInternals<String> internals = CopyOnAccessInMemoryStateInternals.withUnderlying("foo", underlying.commit());
        StateTag<WatermarkHoldState> secondHoldAddress = StateTags.watermarkStateInternal("foo", EARLIEST);
        WatermarkHoldState secondHold = internals.state(StateNamespaces.window(null, second), secondHoldAddress);
        secondHold.add(new Instant(24L));
        internals.commit();
        Assert.assertThat(internals.getEarliestWatermarkHold(), Matchers.equalTo(new Instant(24L)));
    }

    @Test
    public void testGetEarliestHoldBeforeCommit() {
        CopyOnAccessInMemoryStateInternals<String> internals = CopyOnAccessInMemoryStateInternals.withUnderlying(key, null);
        internals.state(StateNamespaces.global(), StateTags.watermarkStateInternal("foo", EARLIEST)).add(new Instant(1234L));
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage(CopyOnAccessInMemoryStateInternals.class.getSimpleName());
        thrown.expectMessage("Can't get the earliest watermark hold");
        thrown.expectMessage("before it is committed");
        internals.getEarliestWatermarkHold();
    }
}

