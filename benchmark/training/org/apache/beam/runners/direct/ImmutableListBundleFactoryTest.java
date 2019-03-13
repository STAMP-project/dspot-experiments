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


import BoundedWindow.TIMESTAMP_MAX_VALUE;
import PaneInfo.NO_FIRING;
import java.util.Collections;
import org.apache.beam.runners.local.StructuralKey;
import org.apache.beam.sdk.coders.ByteArrayCoder;
import org.apache.beam.sdk.coders.StringUtf8Coder;
import org.apache.beam.sdk.coders.VarIntCoder;
import org.apache.beam.sdk.coders.VoidCoder;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.transforms.windowing.BoundedWindow;
import org.apache.beam.sdk.util.WindowedValue;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableList;
import org.hamcrest.Matchers;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link ImmutableListBundleFactory}.
 */
@RunWith(JUnit4.class)
public class ImmutableListBundleFactoryTest {
    @Rule
    public final TestPipeline p = TestPipeline.create().enableAbandonedNodeEnforcement(false);

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private ImmutableListBundleFactory bundleFactory = ImmutableListBundleFactory.create();

    private PCollection<Integer> created;

    private PCollection<KV<String, Integer>> downstream;

    @Test
    public void keyedWithNullKeyShouldCreateKeyedBundle() throws Exception {
        createKeyedBundle(VoidCoder.of(), null);
    }

    @Test
    public void keyedWithStringKeyShouldCreateKeyedBundle() throws Exception {
        createKeyedBundle(StringUtf8Coder.of(), "foo");
    }

    @Test
    public void keyedWithVarIntKeyShouldCreateKeyedBundle() throws Exception {
        createKeyedBundle(VarIntCoder.of(), 1234);
    }

    @Test
    public void keyedWithByteArrayKeyShouldCreateKeyedBundle() throws Exception {
        createKeyedBundle(ByteArrayCoder.of(), new byte[]{ 0, 2, 4, 99 });
    }

    @Test
    public void getElementsBeforeAddShouldReturnEmptyIterable() {
        afterCommitGetElementsShouldHaveAddedElements(Collections.<WindowedValue<Integer>>emptyList());
    }

    @Test
    public void getElementsAfterAddShouldReturnAddedElements() {
        WindowedValue<Integer> firstValue = WindowedValue.valueInGlobalWindow(1);
        WindowedValue<Integer> secondValue = WindowedValue.timestampedValueInGlobalWindow(2, new Instant(1000L));
        afterCommitGetElementsShouldHaveAddedElements(ImmutableList.of(firstValue, secondValue));
    }

    @Test
    public void addElementsAtEndOfTimeThrows() {
        Instant timestamp = BoundedWindow.TIMESTAMP_MAX_VALUE;
        WindowedValue<Integer> value = WindowedValue.timestampedValueInGlobalWindow(1, timestamp);
        UncommittedBundle<Integer> bundle = bundleFactory.createRootBundle();
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(timestamp.toString());
        bundle.add(value);
    }

    @Test
    public void addElementsPastEndOfTimeThrows() {
        Instant timestamp = TIMESTAMP_MAX_VALUE.plus(Duration.standardMinutes(2));
        WindowedValue<Integer> value = WindowedValue.timestampedValueInGlobalWindow(1, timestamp);
        UncommittedBundle<Integer> bundle = bundleFactory.createRootBundle();
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(timestamp.toString());
        bundle.add(value);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void withElementsShouldReturnIndependentBundle() {
        WindowedValue<Integer> firstValue = WindowedValue.valueInGlobalWindow(1);
        WindowedValue<Integer> secondValue = WindowedValue.timestampedValueInGlobalWindow(2, new Instant(1000L));
        CommittedBundle<Integer> committed = afterCommitGetElementsShouldHaveAddedElements(ImmutableList.of(firstValue, secondValue));
        WindowedValue<Integer> firstReplacement = WindowedValue.of(9, new Instant(2048L), new org.apache.beam.sdk.transforms.windowing.IntervalWindow(new Instant(2044L), Instant.now()), NO_FIRING);
        WindowedValue<Integer> secondReplacement = WindowedValue.timestampedValueInGlobalWindow((-1), Instant.now());
        CommittedBundle<Integer> withed = committed.withElements(ImmutableList.of(firstReplacement, secondReplacement));
        Assert.assertThat(withed.getElements(), Matchers.containsInAnyOrder(firstReplacement, secondReplacement));
        Assert.assertThat(committed.getElements(), Matchers.containsInAnyOrder(firstValue, secondValue));
        Assert.assertThat(withed.getKey(), Matchers.equalTo(committed.getKey()));
        Assert.assertThat(withed.getPCollection(), Matchers.equalTo(committed.getPCollection()));
        Assert.assertThat(withed.getSynchronizedProcessingOutputWatermark(), Matchers.equalTo(committed.getSynchronizedProcessingOutputWatermark()));
        Assert.assertThat(withed.getMinimumTimestamp(), Matchers.equalTo(new Instant(2048L)));
    }

    @Test
    public void addAfterCommitShouldThrowException() {
        UncommittedBundle<Integer> bundle = bundleFactory.createRootBundle();
        bundle.add(WindowedValue.valueInGlobalWindow(1));
        CommittedBundle<Integer> firstCommit = bundle.commit(Instant.now());
        Assert.assertThat(firstCommit.getElements(), Matchers.containsInAnyOrder(WindowedValue.valueInGlobalWindow(1)));
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("3");
        thrown.expectMessage("committed");
        bundle.add(WindowedValue.valueInGlobalWindow(3));
    }

    @Test
    public void commitAfterCommitShouldThrowException() {
        UncommittedBundle<Integer> bundle = bundleFactory.createRootBundle();
        bundle.add(WindowedValue.valueInGlobalWindow(1));
        CommittedBundle<Integer> firstCommit = bundle.commit(Instant.now());
        Assert.assertThat(firstCommit.getElements(), Matchers.containsInAnyOrder(WindowedValue.valueInGlobalWindow(1)));
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("committed");
        bundle.commit(Instant.now());
    }

    @Test
    public void createKeyedBundleKeyed() {
        CommittedBundle<KV<String, Integer>> keyedBundle = bundleFactory.createKeyedBundle(StructuralKey.of("foo", StringUtf8Coder.of()), downstream).commit(Instant.now());
        Assert.assertThat(keyedBundle.getKey().getKey(), Matchers.equalTo("foo"));
    }
}

