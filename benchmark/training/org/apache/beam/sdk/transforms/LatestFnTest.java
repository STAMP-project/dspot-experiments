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


import TimestampedValue.TimestampedValueCoder;
import java.util.ArrayList;
import org.apache.beam.sdk.coders.CannotProvideCoderException;
import org.apache.beam.sdk.coders.CoderRegistry;
import org.apache.beam.sdk.coders.NullableCoder;
import org.apache.beam.sdk.coders.VarLongCoder;
import org.apache.beam.sdk.values.TimestampedValue;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.Lists;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.joda.time.Instant;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Unit tests for {@link Latest.LatestFn}.
 */
@RunWith(JUnit4.class)
public class LatestFnTest {
    private static final Instant INSTANT = new Instant(100);

    private static final long VALUE = 100 * (LatestFnTest.INSTANT.getMillis());

    private static final TimestampedValue<Long> TV = TimestampedValue.of(LatestFnTest.VALUE, LatestFnTest.INSTANT);

    private static final TimestampedValue<Long> TV_MINUS_TEN = TimestampedValue.of(((LatestFnTest.VALUE) - 10), LatestFnTest.INSTANT.minus(10));

    private static final TimestampedValue<Long> TV_PLUS_TEN = TimestampedValue.of(((LatestFnTest.VALUE) + 10), LatestFnTest.INSTANT.plus(10));

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    private final Latest.LatestFn<Long> fn = new Latest.LatestFn<>();

    private final Instant baseTimestamp = Instant.now();

    @Test
    public void testDefaultValue() {
        MatcherAssert.assertThat(fn.defaultValue(), Matchers.nullValue());
    }

    @Test
    public void testCreateAccumulator() {
        Assert.assertEquals(TimestampedValue.atMinimumTimestamp(null), fn.createAccumulator());
    }

    @Test
    public void testAddInputInitialAdd() {
        TimestampedValue<Long> input = LatestFnTest.TV;
        Assert.assertEquals(input, fn.addInput(fn.createAccumulator(), input));
    }

    @Test
    public void testAddInputMinTimestamp() {
        TimestampedValue<Long> input = TimestampedValue.atMinimumTimestamp(1234L);
        Assert.assertEquals(input, fn.addInput(fn.createAccumulator(), input));
    }

    @Test
    public void testAddInputEarlierValue() {
        Assert.assertEquals(LatestFnTest.TV, fn.addInput(LatestFnTest.TV, LatestFnTest.TV_MINUS_TEN));
    }

    @Test
    public void testAddInputLaterValue() {
        Assert.assertEquals(LatestFnTest.TV_PLUS_TEN, fn.addInput(LatestFnTest.TV, LatestFnTest.TV_PLUS_TEN));
    }

    @Test
    public void testAddInputSameTimestamp() {
        TimestampedValue<Long> accum = TimestampedValue.of(100L, LatestFnTest.INSTANT);
        TimestampedValue<Long> input = TimestampedValue.of(200L, LatestFnTest.INSTANT);
        MatcherAssert.assertThat("Latest for values with the same timestamp is chosen arbitrarily", fn.addInput(accum, input), Matchers.isOneOf(accum, input));
    }

    @Test
    public void testAddInputNullAccumulator() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("accumulator");
        fn.addInput(null, LatestFnTest.TV);
    }

    @Test
    public void testAddInputNullInput() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("input");
        fn.addInput(LatestFnTest.TV, null);
    }

    @Test
    public void testAddInputNullValue() {
        TimestampedValue<Long> input = TimestampedValue.of(null, LatestFnTest.INSTANT.plus(10));
        Assert.assertEquals("Null values are allowed", input, fn.addInput(LatestFnTest.TV, input));
    }

    @Test
    public void testMergeAccumulatorsMultipleValues() {
        Iterable<TimestampedValue<Long>> accums = Lists.newArrayList(LatestFnTest.TV, LatestFnTest.TV_PLUS_TEN, LatestFnTest.TV_MINUS_TEN);
        Assert.assertEquals(LatestFnTest.TV_PLUS_TEN, fn.mergeAccumulators(accums));
    }

    @Test
    public void testMergeAccumulatorsSingleValue() {
        Assert.assertEquals(LatestFnTest.TV, fn.mergeAccumulators(Lists.newArrayList(LatestFnTest.TV)));
    }

    @Test
    public void testMergeAccumulatorsEmptyIterable() {
        ArrayList<TimestampedValue<Long>> emptyAccums = Lists.newArrayList();
        Assert.assertEquals(TimestampedValue.atMinimumTimestamp(null), fn.mergeAccumulators(emptyAccums));
    }

    @Test
    public void testMergeAccumulatorsDefaultAccumulator() {
        TimestampedValue<Long> defaultAccum = fn.createAccumulator();
        Assert.assertEquals(LatestFnTest.TV, fn.mergeAccumulators(Lists.newArrayList(LatestFnTest.TV, defaultAccum)));
    }

    @Test
    public void testMergeAccumulatorsAllDefaultAccumulators() {
        TimestampedValue<Long> defaultAccum = fn.createAccumulator();
        Assert.assertEquals(defaultAccum, fn.mergeAccumulators(Lists.newArrayList(defaultAccum, defaultAccum)));
    }

    @Test
    public void testMergeAccumulatorsNullIterable() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("accumulators");
        fn.mergeAccumulators(null);
    }

    @Test
    public void testExtractOutput() {
        Assert.assertEquals(LatestFnTest.TV.getValue(), fn.extractOutput(LatestFnTest.TV));
    }

    @Test
    public void testExtractOutputDefaultAccumulator() {
        TimestampedValue<Long> accum = fn.createAccumulator();
        MatcherAssert.assertThat(fn.extractOutput(accum), Matchers.nullValue());
    }

    @Test
    public void testExtractOutputNullValue() {
        TimestampedValue<Long> accum = TimestampedValue.of(null, baseTimestamp);
        Assert.assertEquals(null, fn.extractOutput(accum));
    }

    @Test
    public void testDefaultCoderHandlesNull() throws CannotProvideCoderException {
        Latest.LatestFn<Long> fn = new Latest.LatestFn<>();
        CoderRegistry registry = CoderRegistry.createDefault();
        TimestampedValue.TimestampedValueCoder<Long> inputCoder = TimestampedValueCoder.of(VarLongCoder.of());
        MatcherAssert.assertThat("Default output coder should handle null values", fn.getDefaultOutputCoder(registry, inputCoder), Matchers.instanceOf(NullableCoder.class));
        MatcherAssert.assertThat("Default accumulator coder should handle null values", fn.getAccumulatorCoder(registry, inputCoder), Matchers.instanceOf(NullableCoder.class));
    }
}

