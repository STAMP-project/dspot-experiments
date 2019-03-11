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


import Combine.BinaryCombineIntegerFn;
import TimestampCombiner.EARLIEST;
import TimestampCombiner.LATEST;
import org.apache.beam.sdk.coders.BigEndianIntegerCoder;
import org.apache.beam.sdk.coders.Coder;
import org.apache.beam.sdk.coders.CoderRegistry;
import org.apache.beam.sdk.coders.StringUtf8Coder;
import org.apache.beam.sdk.coders.VarIntCoder;
import org.apache.beam.sdk.transforms.Combine;
import org.apache.beam.sdk.transforms.Max;
import org.apache.beam.sdk.transforms.Min;
import org.apache.beam.sdk.util.CombineFnUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link StateTag}.
 */
@RunWith(JUnit4.class)
public class StateTagTest {
    @Test
    public void testValueEquality() {
        StateTag<?> fooVarInt1 = StateTags.value("foo", VarIntCoder.of());
        StateTag<?> fooVarInt2 = StateTags.value("foo", VarIntCoder.of());
        StateTag<?> fooBigEndian = StateTags.value("foo", BigEndianIntegerCoder.of());
        StateTag<?> barVarInt = StateTags.value("bar", VarIntCoder.of());
        Assert.assertEquals(fooVarInt1, fooVarInt2);
        Assert.assertNotEquals(fooVarInt1, fooBigEndian);
        Assert.assertNotEquals(fooVarInt1, barVarInt);
    }

    @Test
    public void testBagEquality() {
        StateTag<?> fooVarInt1 = StateTags.bag("foo", VarIntCoder.of());
        StateTag<?> fooVarInt2 = StateTags.bag("foo", VarIntCoder.of());
        StateTag<?> fooBigEndian = StateTags.bag("foo", BigEndianIntegerCoder.of());
        StateTag<?> barVarInt = StateTags.bag("bar", VarIntCoder.of());
        Assert.assertEquals(fooVarInt1, fooVarInt2);
        Assert.assertNotEquals(fooVarInt1, fooBigEndian);
        Assert.assertNotEquals(fooVarInt1, barVarInt);
    }

    @Test
    public void testSetEquality() {
        StateTag<?> fooVarInt1 = StateTags.set("foo", VarIntCoder.of());
        StateTag<?> fooVarInt2 = StateTags.set("foo", VarIntCoder.of());
        StateTag<?> fooBigEndian = StateTags.set("foo", BigEndianIntegerCoder.of());
        StateTag<?> barVarInt = StateTags.set("bar", VarIntCoder.of());
        Assert.assertEquals(fooVarInt1, fooVarInt2);
        Assert.assertNotEquals(fooVarInt1, fooBigEndian);
        Assert.assertNotEquals(fooVarInt1, barVarInt);
    }

    @Test
    public void testMapEquality() {
        StateTag<?> fooStringVarInt1 = StateTags.map("foo", StringUtf8Coder.of(), VarIntCoder.of());
        StateTag<?> fooStringVarInt2 = StateTags.map("foo", StringUtf8Coder.of(), VarIntCoder.of());
        StateTag<?> fooStringBigEndian = StateTags.map("foo", StringUtf8Coder.of(), BigEndianIntegerCoder.of());
        StateTag<?> fooVarIntBigEndian = StateTags.map("foo", VarIntCoder.of(), BigEndianIntegerCoder.of());
        StateTag<?> barStringVarInt = StateTags.map("bar", StringUtf8Coder.of(), VarIntCoder.of());
        Assert.assertEquals(fooStringVarInt1, fooStringVarInt2);
        Assert.assertNotEquals(fooStringVarInt1, fooStringBigEndian);
        Assert.assertNotEquals(fooStringBigEndian, fooVarIntBigEndian);
        Assert.assertNotEquals(fooStringVarInt1, fooVarIntBigEndian);
        Assert.assertNotEquals(fooStringVarInt1, barStringVarInt);
    }

    @Test
    public void testWatermarkBagEquality() {
        StateTag<?> foo1 = StateTags.watermarkStateInternal("foo", EARLIEST);
        StateTag<?> foo2 = StateTags.watermarkStateInternal("foo", EARLIEST);
        StateTag<?> bar = StateTags.watermarkStateInternal("bar", EARLIEST);
        StateTag<?> bar2 = StateTags.watermarkStateInternal("bar", LATEST);
        // Same id, same fn.
        Assert.assertEquals(foo1, foo2);
        // Different id, same fn.
        Assert.assertNotEquals(foo1, bar);
        // Same id, different fn.
        Assert.assertEquals(bar, bar2);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void testCombiningValueEquality() {
        Combine.BinaryCombineIntegerFn maxFn = Max.ofIntegers();
        Coder<Integer> input1 = VarIntCoder.of();
        Coder<Integer> input2 = BigEndianIntegerCoder.of();
        Combine.BinaryCombineIntegerFn minFn = Min.ofIntegers();
        StateTag<?> fooCoder1Max1 = StateTags.combiningValueFromInputInternal("foo", input1, maxFn);
        StateTag<?> fooCoder1Max2 = StateTags.combiningValueFromInputInternal("foo", input1, maxFn);
        StateTag<?> fooCoder1Min = StateTags.combiningValueFromInputInternal("foo", input1, minFn);
        StateTag<?> fooCoder2Max = StateTags.combiningValueFromInputInternal("foo", input2, maxFn);
        StateTag<?> barCoder1Max = StateTags.combiningValueFromInputInternal("bar", input1, maxFn);
        // Same name, coder and combineFn
        Assert.assertEquals(fooCoder1Max1, fooCoder1Max2);
        Assert.assertEquals(StateTags.convertToBagTagInternal(((StateTag) (fooCoder1Max1))), StateTags.convertToBagTagInternal(((StateTag) (fooCoder1Max2))));
        // Different combineFn, but we treat them as equal since we only serialize the bits.
        Assert.assertEquals(fooCoder1Max1, fooCoder1Min);
        Assert.assertEquals(StateTags.convertToBagTagInternal(((StateTag) (fooCoder1Max1))), StateTags.convertToBagTagInternal(((StateTag) (fooCoder1Min))));
        // Different input coder coder.
        Assert.assertNotEquals(fooCoder1Max1, fooCoder2Max);
        Assert.assertNotEquals(StateTags.convertToBagTagInternal(((StateTag) (fooCoder1Max1))), StateTags.convertToBagTagInternal(((StateTag) (fooCoder2Max))));
        // These StateTags have different IDs.
        Assert.assertNotEquals(fooCoder1Max1, barCoder1Max);
        Assert.assertNotEquals(StateTags.convertToBagTagInternal(((StateTag) (fooCoder1Max1))), StateTags.convertToBagTagInternal(((StateTag) (barCoder1Max))));
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void testCombiningValueWithContextEquality() {
        CoderRegistry registry = CoderRegistry.createDefault();
        Combine.BinaryCombineIntegerFn maxFn = Max.ofIntegers();
        Combine.BinaryCombineIntegerFn minFn = Min.ofIntegers();
        Coder<int[]> accum1 = maxFn.getAccumulatorCoder(registry, VarIntCoder.of());
        Coder<int[]> accum2 = minFn.getAccumulatorCoder(registry, BigEndianIntegerCoder.of());
        StateTag<?> fooCoder1Max1 = StateTags.combiningValueWithContext("foo", accum1, CombineFnUtil.toFnWithContext(maxFn));
        StateTag<?> fooCoder1Max2 = StateTags.combiningValueWithContext("foo", accum1, CombineFnUtil.toFnWithContext(maxFn));
        StateTag<?> fooCoder1Min = StateTags.combiningValueWithContext("foo", accum1, CombineFnUtil.toFnWithContext(minFn));
        StateTag<?> fooCoder2Max = StateTags.combiningValueWithContext("foo", accum2, CombineFnUtil.toFnWithContext(maxFn));
        StateTag<?> barCoder1Max = StateTags.combiningValueWithContext("bar", accum1, CombineFnUtil.toFnWithContext(maxFn));
        // Same name, coder and combineFn
        Assert.assertEquals(fooCoder1Max1, fooCoder1Max2);
        Assert.assertEquals(StateTags.convertToBagTagInternal(((StateTag) (fooCoder1Max1))), StateTags.convertToBagTagInternal(((StateTag) (fooCoder1Max2))));
        // Different combineFn, but we treat them as equal since we only serialize the bits.
        Assert.assertEquals(fooCoder1Max1, fooCoder1Min);
        Assert.assertEquals(StateTags.convertToBagTagInternal(((StateTag) (fooCoder1Max1))), StateTags.convertToBagTagInternal(((StateTag) (fooCoder1Min))));
        // Different input coder coder.
        Assert.assertNotEquals(fooCoder1Max1, fooCoder2Max);
        Assert.assertNotEquals(StateTags.convertToBagTagInternal(((StateTag) (fooCoder1Max1))), StateTags.convertToBagTagInternal(((StateTag) (fooCoder2Max))));
        // These StateTags have different IDs.
        Assert.assertNotEquals(fooCoder1Max1, barCoder1Max);
        Assert.assertNotEquals(StateTags.convertToBagTagInternal(((StateTag) (fooCoder1Max1))), StateTags.convertToBagTagInternal(((StateTag) (barCoder1Max))));
    }
}

