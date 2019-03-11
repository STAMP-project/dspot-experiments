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


import TimestampCombiner.EARLIEST;
import TimestampCombiner.END_OF_WINDOW;
import TimestampCombiner.LATEST;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.apache.beam.sdk.coders.Coder;
import org.apache.beam.sdk.coders.CoderException;
import org.apache.beam.sdk.coders.StringUtf8Coder;
import org.apache.beam.sdk.coders.VarIntCoder;
import org.apache.beam.sdk.state.BagState;
import org.apache.beam.sdk.state.CombiningState;
import org.apache.beam.sdk.state.GroupingState;
import org.apache.beam.sdk.state.MapState;
import org.apache.beam.sdk.state.ReadableState;
import org.apache.beam.sdk.state.SetState;
import org.apache.beam.sdk.state.ValueState;
import org.apache.beam.sdk.state.WatermarkHoldState;
import org.apache.beam.sdk.transforms.Sum;
import org.apache.beam.sdk.transforms.windowing.BoundedWindow;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.Iterables;
import org.hamcrest.Matchers;
import org.joda.time.Instant;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link StateInternals}.
 */
public abstract class StateInternalsTest {
    private static final BoundedWindow WINDOW_1 = new org.apache.beam.sdk.transforms.windowing.IntervalWindow(new Instant(0), new Instant(10));

    private static final StateNamespace NAMESPACE_1 = new StateNamespaceForTest("ns1");

    private static final StateNamespace NAMESPACE_2 = new StateNamespaceForTest("ns2");

    private static final StateNamespace NAMESPACE_3 = new StateNamespaceForTest("ns3");

    private static final StateTag<ValueState<String>> STRING_VALUE_ADDR = StateTags.value("stringValue", StringUtf8Coder.of());

    private static final StateTag<CombiningState<Integer, int[], Integer>> SUM_INTEGER_ADDR = StateTags.combiningValueFromInputInternal("sumInteger", VarIntCoder.of(), Sum.ofIntegers());

    private static final StateTag<BagState<String>> STRING_BAG_ADDR = StateTags.bag("stringBag", StringUtf8Coder.of());

    private static final StateTag<SetState<String>> STRING_SET_ADDR = StateTags.set("stringSet", StringUtf8Coder.of());

    private static final StateTag<MapState<String, Integer>> STRING_MAP_ADDR = StateTags.map("stringMap", StringUtf8Coder.of(), VarIntCoder.of());

    private static final StateTag<WatermarkHoldState> WATERMARK_EARLIEST_ADDR = StateTags.watermarkStateInternal("watermark", EARLIEST);

    private static final StateTag<WatermarkHoldState> WATERMARK_LATEST_ADDR = StateTags.watermarkStateInternal("watermark", LATEST);

    private static final StateTag<WatermarkHoldState> WATERMARK_EOW_ADDR = StateTags.watermarkStateInternal("watermark", END_OF_WINDOW);

    // Two distinct tags because they have non-equals() coders
    private static final StateTag<BagState<String>> STRING_BAG_ADDR1 = StateTags.bag("badStringBag", new StateInternalsTest.StringCoderWithIdentityEquality());

    private static final StateTag<BagState<String>> STRING_BAG_ADDR2 = StateTags.bag("badStringBag", new StateInternalsTest.StringCoderWithIdentityEquality());

    private StateInternals underTest;

    @Test
    public void testValue() throws Exception {
        ValueState<String> value = underTest.state(StateInternalsTest.NAMESPACE_1, StateInternalsTest.STRING_VALUE_ADDR);
        // State instances are cached, but depend on the namespace.
        Assert.assertThat(underTest.state(StateInternalsTest.NAMESPACE_1, StateInternalsTest.STRING_VALUE_ADDR), Matchers.equalTo(value));
        Assert.assertThat(underTest.state(StateInternalsTest.NAMESPACE_2, StateInternalsTest.STRING_VALUE_ADDR), Matchers.not(Matchers.equalTo(value)));
        Assert.assertThat(value.read(), Matchers.nullValue());
        value.write("hello");
        Assert.assertThat(value.read(), Matchers.equalTo("hello"));
        value.write("world");
        Assert.assertThat(value.read(), Matchers.equalTo("world"));
        value.clear();
        Assert.assertThat(value.read(), Matchers.nullValue());
        Assert.assertThat(underTest.state(StateInternalsTest.NAMESPACE_1, StateInternalsTest.STRING_VALUE_ADDR), Matchers.equalTo(value));
    }

    @Test
    public void testBag() throws Exception {
        BagState<String> value = underTest.state(StateInternalsTest.NAMESPACE_1, StateInternalsTest.STRING_BAG_ADDR);
        // State instances are cached, but depend on the namespace.
        Assert.assertThat(value, Matchers.equalTo(underTest.state(StateInternalsTest.NAMESPACE_1, StateInternalsTest.STRING_BAG_ADDR)));
        Assert.assertThat(value, Matchers.not(Matchers.equalTo(underTest.state(StateInternalsTest.NAMESPACE_2, StateInternalsTest.STRING_BAG_ADDR))));
        Assert.assertThat(value.read(), Matchers.emptyIterable());
        value.add("hello");
        Assert.assertThat(value.read(), Matchers.containsInAnyOrder("hello"));
        value.add("world");
        Assert.assertThat(value.read(), Matchers.containsInAnyOrder("hello", "world"));
        value.clear();
        Assert.assertThat(value.read(), Matchers.emptyIterable());
        Assert.assertThat(underTest.state(StateInternalsTest.NAMESPACE_1, StateInternalsTest.STRING_BAG_ADDR), Matchers.equalTo(value));
    }

    @Test
    public void testBagIsEmpty() throws Exception {
        BagState<String> value = underTest.state(StateInternalsTest.NAMESPACE_1, StateInternalsTest.STRING_BAG_ADDR);
        Assert.assertThat(value.isEmpty().read(), Matchers.is(true));
        ReadableState<Boolean> readFuture = value.isEmpty();
        value.add("hello");
        Assert.assertThat(readFuture.read(), Matchers.is(false));
        value.clear();
        Assert.assertThat(readFuture.read(), Matchers.is(true));
    }

    @Test
    public void testMergeBagIntoSource() throws Exception {
        BagState<String> bag1 = underTest.state(StateInternalsTest.NAMESPACE_1, StateInternalsTest.STRING_BAG_ADDR);
        BagState<String> bag2 = underTest.state(StateInternalsTest.NAMESPACE_2, StateInternalsTest.STRING_BAG_ADDR);
        bag1.add("Hello");
        bag2.add("World");
        bag1.add("!");
        StateMerging.mergeBags(Arrays.asList(bag1, bag2), bag1);
        // Reading the merged bag gets both the contents
        Assert.assertThat(bag1.read(), Matchers.containsInAnyOrder("Hello", "World", "!"));
        Assert.assertThat(bag2.read(), Matchers.emptyIterable());
    }

    @Test
    public void testMergeBagIntoNewNamespace() throws Exception {
        BagState<String> bag1 = underTest.state(StateInternalsTest.NAMESPACE_1, StateInternalsTest.STRING_BAG_ADDR);
        BagState<String> bag2 = underTest.state(StateInternalsTest.NAMESPACE_2, StateInternalsTest.STRING_BAG_ADDR);
        BagState<String> bag3 = underTest.state(StateInternalsTest.NAMESPACE_3, StateInternalsTest.STRING_BAG_ADDR);
        bag1.add("Hello");
        bag2.add("World");
        bag1.add("!");
        StateMerging.mergeBags(Arrays.asList(bag1, bag2, bag3), bag3);
        // Reading the merged bag gets both the contents
        Assert.assertThat(bag3.read(), Matchers.containsInAnyOrder("Hello", "World", "!"));
        Assert.assertThat(bag1.read(), Matchers.emptyIterable());
        Assert.assertThat(bag2.read(), Matchers.emptyIterable());
    }

    @Test
    public void testSet() throws Exception {
        SetState<String> value = underTest.state(StateInternalsTest.NAMESPACE_1, StateInternalsTest.STRING_SET_ADDR);
        // State instances are cached, but depend on the namespace.
        Assert.assertThat(value, Matchers.equalTo(underTest.state(StateInternalsTest.NAMESPACE_1, StateInternalsTest.STRING_SET_ADDR)));
        Assert.assertThat(value, Matchers.not(Matchers.equalTo(underTest.state(StateInternalsTest.NAMESPACE_2, StateInternalsTest.STRING_SET_ADDR))));
        // empty
        Assert.assertThat(value.read(), Matchers.emptyIterable());
        Assert.assertFalse(value.contains("A").read());
        // add
        value.add("A");
        value.add("B");
        value.add("A");
        Assert.assertFalse(value.addIfAbsent("B").read());
        Assert.assertThat(value.read(), Matchers.containsInAnyOrder("A", "B"));
        // remove
        value.remove("A");
        Assert.assertThat(value.read(), Matchers.containsInAnyOrder("B"));
        value.remove("C");
        Assert.assertThat(value.read(), Matchers.containsInAnyOrder("B"));
        // contains
        Assert.assertFalse(value.contains("A").read());
        Assert.assertTrue(value.contains("B").read());
        value.add("C");
        value.add("D");
        // readLater
        Assert.assertThat(value.readLater().read(), Matchers.containsInAnyOrder("B", "C", "D"));
        SetState<String> later = value.readLater();
        Assert.assertThat(later.read(), Matchers.hasItems("C", "D"));
        Assert.assertFalse(later.contains("A").read());
        // clear
        value.clear();
        Assert.assertThat(value.read(), Matchers.emptyIterable());
        Assert.assertThat(underTest.state(StateInternalsTest.NAMESPACE_1, StateInternalsTest.STRING_SET_ADDR), Matchers.equalTo(value));
    }

    @Test
    public void testSetIsEmpty() throws Exception {
        SetState<String> value = underTest.state(StateInternalsTest.NAMESPACE_1, StateInternalsTest.STRING_SET_ADDR);
        Assert.assertThat(value.isEmpty().read(), Matchers.is(true));
        ReadableState<Boolean> readFuture = value.isEmpty();
        value.add("hello");
        Assert.assertThat(readFuture.read(), Matchers.is(false));
        value.clear();
        Assert.assertThat(readFuture.read(), Matchers.is(true));
    }

    @Test
    public void testMergeSetIntoSource() throws Exception {
        SetState<String> set1 = underTest.state(StateInternalsTest.NAMESPACE_1, StateInternalsTest.STRING_SET_ADDR);
        SetState<String> set2 = underTest.state(StateInternalsTest.NAMESPACE_2, StateInternalsTest.STRING_SET_ADDR);
        set1.add("Hello");
        set2.add("Hello");
        set2.add("World");
        set1.add("!");
        StateMerging.mergeSets(Arrays.asList(set1, set2), set1);
        // Reading the merged set gets both the contents
        Assert.assertThat(set1.read(), Matchers.containsInAnyOrder("Hello", "World", "!"));
        Assert.assertThat(set2.read(), Matchers.emptyIterable());
    }

    @Test
    public void testMergeSetIntoNewNamespace() throws Exception {
        SetState<String> set1 = underTest.state(StateInternalsTest.NAMESPACE_1, StateInternalsTest.STRING_SET_ADDR);
        SetState<String> set2 = underTest.state(StateInternalsTest.NAMESPACE_2, StateInternalsTest.STRING_SET_ADDR);
        SetState<String> set3 = underTest.state(StateInternalsTest.NAMESPACE_3, StateInternalsTest.STRING_SET_ADDR);
        set1.add("Hello");
        set2.add("Hello");
        set2.add("World");
        set1.add("!");
        StateMerging.mergeSets(Arrays.asList(set1, set2, set3), set3);
        // Reading the merged set gets both the contents
        Assert.assertThat(set3.read(), Matchers.containsInAnyOrder("Hello", "World", "!"));
        Assert.assertThat(set1.read(), Matchers.emptyIterable());
        Assert.assertThat(set2.read(), Matchers.emptyIterable());
    }

    // for testMap
    private static class MapEntry<K, V> implements Map.Entry<K, V> {
        private K key;

        private V value;

        private MapEntry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        static <K, V> Map.Entry<K, V> of(K k, V v) {
            return new StateInternalsTest.MapEntry<>(k, v);
        }

        @Override
        public final K getKey() {
            return key;
        }

        @Override
        public final V getValue() {
            return value;
        }

        @Override
        public final String toString() {
            return ((key) + "=") + (value);
        }

        @Override
        public final int hashCode() {
            return (Objects.hashCode(key)) ^ (Objects.hashCode(value));
        }

        @Override
        public final V setValue(V newValue) {
            V oldValue = value;
            value = newValue;
            return oldValue;
        }

        @Override
        public final boolean equals(Object o) {
            if (o == (this)) {
                return true;
            }
            if (o instanceof Map.Entry) {
                Map.Entry<?, ?> e = ((Map.Entry<?, ?>) (o));
                if ((Objects.equals(key, e.getKey())) && (Objects.equals(value, e.getValue()))) {
                    return true;
                }
            }
            return false;
        }
    }

    @Test
    public void testMap() throws Exception {
        MapState<String, Integer> value = underTest.state(StateInternalsTest.NAMESPACE_1, StateInternalsTest.STRING_MAP_ADDR);
        // State instances are cached, but depend on the namespace.
        Assert.assertThat(value, Matchers.equalTo(underTest.state(StateInternalsTest.NAMESPACE_1, StateInternalsTest.STRING_MAP_ADDR)));
        Assert.assertThat(value, Matchers.not(Matchers.equalTo(underTest.state(StateInternalsTest.NAMESPACE_2, StateInternalsTest.STRING_MAP_ADDR))));
        // put
        Assert.assertThat(value.entries().read(), Matchers.emptyIterable());
        value.put("A", 1);
        value.put("B", 2);
        value.put("A", 11);
        Assert.assertThat(value.putIfAbsent("B", 22).read(), Matchers.equalTo(2));
        Assert.assertThat(value.entries().read(), Matchers.containsInAnyOrder(StateInternalsTest.MapEntry.of("A", 11), StateInternalsTest.MapEntry.of("B", 2)));
        // remove
        value.remove("A");
        Assert.assertThat(value.entries().read(), Matchers.containsInAnyOrder(StateInternalsTest.MapEntry.of("B", 2)));
        value.remove("C");
        Assert.assertThat(value.entries().read(), Matchers.containsInAnyOrder(StateInternalsTest.MapEntry.of("B", 2)));
        // get
        Assert.assertNull(value.get("A").read());
        Assert.assertThat(value.get("B").read(), Matchers.equalTo(2));
        value.put("C", 3);
        value.put("D", 4);
        Assert.assertThat(value.get("C").read(), Matchers.equalTo(3));
        // iterate
        value.put("E", 5);
        value.remove("C");
        Assert.assertThat(value.keys().read(), Matchers.containsInAnyOrder("B", "D", "E"));
        Assert.assertThat(value.values().read(), Matchers.containsInAnyOrder(2, 4, 5));
        Assert.assertThat(value.entries().read(), Matchers.containsInAnyOrder(StateInternalsTest.MapEntry.of("B", 2), StateInternalsTest.MapEntry.of("D", 4), StateInternalsTest.MapEntry.of("E", 5)));
        // readLater
        Assert.assertThat(value.get("B").readLater().read(), Matchers.equalTo(2));
        Assert.assertNull(value.get("A").readLater().read());
        Assert.assertThat(value.entries().readLater().read(), Matchers.containsInAnyOrder(StateInternalsTest.MapEntry.of("B", 2), StateInternalsTest.MapEntry.of("D", 4), StateInternalsTest.MapEntry.of("E", 5)));
        // clear
        value.clear();
        Assert.assertThat(value.entries().read(), Matchers.emptyIterable());
        Assert.assertThat(underTest.state(StateInternalsTest.NAMESPACE_1, StateInternalsTest.STRING_MAP_ADDR), Matchers.equalTo(value));
    }

    @Test
    public void testCombiningValue() throws Exception {
        GroupingState<Integer, Integer> value = underTest.state(StateInternalsTest.NAMESPACE_1, StateInternalsTest.SUM_INTEGER_ADDR);
        // State instances are cached, but depend on the namespace.
        Assert.assertEquals(value, underTest.state(StateInternalsTest.NAMESPACE_1, StateInternalsTest.SUM_INTEGER_ADDR));
        Assert.assertFalse(value.equals(underTest.state(StateInternalsTest.NAMESPACE_2, StateInternalsTest.SUM_INTEGER_ADDR)));
        Assert.assertThat(value.read(), Matchers.equalTo(0));
        value.add(2);
        Assert.assertThat(value.read(), Matchers.equalTo(2));
        value.add(3);
        Assert.assertThat(value.read(), Matchers.equalTo(5));
        value.clear();
        Assert.assertThat(value.read(), Matchers.equalTo(0));
        Assert.assertThat(underTest.state(StateInternalsTest.NAMESPACE_1, StateInternalsTest.SUM_INTEGER_ADDR), Matchers.equalTo(value));
    }

    @Test
    public void testCombiningIsEmpty() throws Exception {
        GroupingState<Integer, Integer> value = underTest.state(StateInternalsTest.NAMESPACE_1, StateInternalsTest.SUM_INTEGER_ADDR);
        Assert.assertThat(value.isEmpty().read(), Matchers.is(true));
        ReadableState<Boolean> readFuture = value.isEmpty();
        value.add(5);
        Assert.assertThat(readFuture.read(), Matchers.is(false));
        value.clear();
        Assert.assertThat(readFuture.read(), Matchers.is(true));
    }

    @Test
    public void testMergeCombiningValueIntoSource() throws Exception {
        CombiningState<Integer, int[], Integer> value1 = underTest.state(StateInternalsTest.NAMESPACE_1, StateInternalsTest.SUM_INTEGER_ADDR);
        CombiningState<Integer, int[], Integer> value2 = underTest.state(StateInternalsTest.NAMESPACE_2, StateInternalsTest.SUM_INTEGER_ADDR);
        value1.add(5);
        value2.add(10);
        value1.add(6);
        Assert.assertThat(value1.read(), Matchers.equalTo(11));
        Assert.assertThat(value2.read(), Matchers.equalTo(10));
        // Merging clears the old values and updates the result value.
        StateMerging.mergeCombiningValues(Arrays.asList(value1, value2), value1);
        Assert.assertThat(value1.read(), Matchers.equalTo(21));
        Assert.assertThat(value2.read(), Matchers.equalTo(0));
    }

    @Test
    public void testMergeCombiningValueIntoNewNamespace() throws Exception {
        CombiningState<Integer, int[], Integer> value1 = underTest.state(StateInternalsTest.NAMESPACE_1, StateInternalsTest.SUM_INTEGER_ADDR);
        CombiningState<Integer, int[], Integer> value2 = underTest.state(StateInternalsTest.NAMESPACE_2, StateInternalsTest.SUM_INTEGER_ADDR);
        CombiningState<Integer, int[], Integer> value3 = underTest.state(StateInternalsTest.NAMESPACE_3, StateInternalsTest.SUM_INTEGER_ADDR);
        value1.add(5);
        value2.add(10);
        value1.add(6);
        StateMerging.mergeCombiningValues(Arrays.asList(value1, value2), value3);
        // Merging clears the old values and updates the result value.
        Assert.assertThat(value1.read(), Matchers.equalTo(0));
        Assert.assertThat(value2.read(), Matchers.equalTo(0));
        Assert.assertThat(value3.read(), Matchers.equalTo(21));
    }

    @Test
    public void testWatermarkEarliestState() throws Exception {
        WatermarkHoldState value = underTest.state(StateInternalsTest.NAMESPACE_1, StateInternalsTest.WATERMARK_EARLIEST_ADDR);
        // State instances are cached, but depend on the namespace.
        Assert.assertEquals(value, underTest.state(StateInternalsTest.NAMESPACE_1, StateInternalsTest.WATERMARK_EARLIEST_ADDR));
        Assert.assertFalse(value.equals(underTest.state(StateInternalsTest.NAMESPACE_2, StateInternalsTest.WATERMARK_EARLIEST_ADDR)));
        Assert.assertThat(value.read(), Matchers.nullValue());
        value.add(new Instant(2000));
        Assert.assertThat(value.read(), Matchers.equalTo(new Instant(2000)));
        value.add(new Instant(3000));
        Assert.assertThat(value.read(), Matchers.equalTo(new Instant(2000)));
        value.add(new Instant(1000));
        Assert.assertThat(value.read(), Matchers.equalTo(new Instant(1000)));
        value.clear();
        Assert.assertThat(value.read(), Matchers.equalTo(null));
        Assert.assertThat(underTest.state(StateInternalsTest.NAMESPACE_1, StateInternalsTest.WATERMARK_EARLIEST_ADDR), Matchers.equalTo(value));
    }

    @Test
    public void testWatermarkLatestState() throws Exception {
        WatermarkHoldState value = underTest.state(StateInternalsTest.NAMESPACE_1, StateInternalsTest.WATERMARK_LATEST_ADDR);
        // State instances are cached, but depend on the namespace.
        Assert.assertEquals(value, underTest.state(StateInternalsTest.NAMESPACE_1, StateInternalsTest.WATERMARK_LATEST_ADDR));
        Assert.assertFalse(value.equals(underTest.state(StateInternalsTest.NAMESPACE_2, StateInternalsTest.WATERMARK_LATEST_ADDR)));
        Assert.assertThat(value.read(), Matchers.nullValue());
        value.add(new Instant(2000));
        Assert.assertThat(value.read(), Matchers.equalTo(new Instant(2000)));
        value.add(new Instant(3000));
        Assert.assertThat(value.read(), Matchers.equalTo(new Instant(3000)));
        value.add(new Instant(1000));
        Assert.assertThat(value.read(), Matchers.equalTo(new Instant(3000)));
        value.clear();
        Assert.assertThat(value.read(), Matchers.equalTo(null));
        Assert.assertThat(underTest.state(StateInternalsTest.NAMESPACE_1, StateInternalsTest.WATERMARK_LATEST_ADDR), Matchers.equalTo(value));
    }

    @Test
    public void testWatermarkEndOfWindowState() throws Exception {
        WatermarkHoldState value = underTest.state(StateInternalsTest.NAMESPACE_1, StateInternalsTest.WATERMARK_EOW_ADDR);
        // State instances are cached, but depend on the namespace.
        Assert.assertEquals(value, underTest.state(StateInternalsTest.NAMESPACE_1, StateInternalsTest.WATERMARK_EOW_ADDR));
        Assert.assertFalse(value.equals(underTest.state(StateInternalsTest.NAMESPACE_2, StateInternalsTest.WATERMARK_EOW_ADDR)));
        Assert.assertThat(value.read(), Matchers.nullValue());
        value.add(new Instant(2000));
        Assert.assertThat(value.read(), Matchers.equalTo(new Instant(2000)));
        value.clear();
        Assert.assertThat(value.read(), Matchers.equalTo(null));
        Assert.assertThat(underTest.state(StateInternalsTest.NAMESPACE_1, StateInternalsTest.WATERMARK_EOW_ADDR), Matchers.equalTo(value));
    }

    @Test
    public void testWatermarkStateIsEmpty() throws Exception {
        WatermarkHoldState value = underTest.state(StateInternalsTest.NAMESPACE_1, StateInternalsTest.WATERMARK_EARLIEST_ADDR);
        Assert.assertThat(value.isEmpty().read(), Matchers.is(true));
        ReadableState<Boolean> readFuture = value.isEmpty();
        value.add(new Instant(1000));
        Assert.assertThat(readFuture.read(), Matchers.is(false));
        value.clear();
        Assert.assertThat(readFuture.read(), Matchers.is(true));
    }

    @Test
    public void testSetReadable() throws Exception {
        SetState<String> value = underTest.state(StateInternalsTest.NAMESPACE_1, StateInternalsTest.STRING_SET_ADDR);
        // test contains
        ReadableState<Boolean> readable = value.contains("A");
        value.add("A");
        Assert.assertFalse(readable.read());
        // test addIfAbsent
        value.addIfAbsent("B");
        Assert.assertTrue(value.contains("B").read());
    }

    @Test
    public void testMapReadable() throws Exception {
        MapState<String, Integer> value = underTest.state(StateInternalsTest.NAMESPACE_1, StateInternalsTest.STRING_MAP_ADDR);
        // test iterable, should just return a iterable view of the values contained in this map.
        // The iterable is backed by the map, so changes to the map are reflected in the iterable.
        ReadableState<Iterable<String>> keys = value.keys();
        ReadableState<Iterable<Integer>> values = value.values();
        ReadableState<Iterable<Map.Entry<String, Integer>>> entries = value.entries();
        value.put("A", 1);
        Assert.assertFalse(Iterables.isEmpty(keys.read()));
        Assert.assertFalse(Iterables.isEmpty(values.read()));
        Assert.assertFalse(Iterables.isEmpty(entries.read()));
        // test get
        ReadableState<Integer> get = value.get("B");
        value.put("B", 2);
        Assert.assertNull(get.read());
        // test addIfAbsent
        value.putIfAbsent("C", 3);
        Assert.assertThat(value.get("C").read(), Matchers.equalTo(3));
    }

    @Test
    public void testBagWithBadCoderEquality() throws Exception {
        // Ensure two instances of the bad coder are distinct; models user who fails to
        // override equals() or inherit from CustomCoder for StructuredCoder
        Assert.assertThat(new StateInternalsTest.StringCoderWithIdentityEquality(), Matchers.not(Matchers.equalTo(new StateInternalsTest.StringCoderWithIdentityEquality())));
        BagState<String> state1 = underTest.state(StateInternalsTest.NAMESPACE_1, StateInternalsTest.STRING_BAG_ADDR1);
        state1.add("hello");
        BagState<String> state2 = underTest.state(StateInternalsTest.NAMESPACE_1, StateInternalsTest.STRING_BAG_ADDR2);
        Assert.assertThat(state2.read(), Matchers.containsInAnyOrder("hello"));
    }

    private static class StringCoderWithIdentityEquality extends Coder<String> {
        private final StringUtf8Coder realCoder = StringUtf8Coder.of();

        @Override
        public void encode(String value, OutputStream outStream) throws IOException, CoderException {
            realCoder.encode(value, outStream);
        }

        @Override
        public String decode(InputStream inStream) throws IOException, CoderException {
            return realCoder.decode(inStream);
        }

        @Override
        public List<? extends Coder<?>> getCoderArguments() {
            return null;
        }

        @Override
        public void verifyDeterministic() throws NonDeterministicException {
        }

        @Override
        public boolean equals(Object other) {
            return other == (this);
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }
    }
}

