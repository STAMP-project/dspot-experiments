/**
 * Copyright 2015, 2017 StreamEx contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package one.util.streamex;


import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import one.util.streamex.StreamExTest.Point;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static java.util.Map.Entry.comparingByKey;
import static java.util.Map.Entry.comparingByValue;


/**
 *
 *
 * @author Tagir Valeev
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class EntryStreamTest {
    @Test
    public void testCreate() {
        Assert.assertEquals(0, EntryStream.empty().count());
        Assert.assertEquals(0, EntryStream.empty().count());
        Map<String, Integer> data = EntryStreamTest.createMap();
        Assert.assertEquals(data, EntryStream.of(data).toMap());
        Assert.assertEquals(data, EntryStream.of(data.entrySet().stream()).toMap());
        Map<String, Integer> expected = new HashMap<>();
        expected.put("aaa", 3);
        expected.put("bbb", 3);
        expected.put("c", 1);
        Assert.assertEquals(expected, StreamEx.of("aaa", "bbb", "c").mapToEntry(String::length).toMap());
        Assert.assertEquals(expected, StreamEx.of("aaa", "bbb", "c").mapToEntry(( s) -> s, String::length).toMap());
        Assert.assertEquals(Collections.singletonMap("foo", 1), EntryStream.of("foo", 1).toMap());
        Assert.assertEquals(EntryStreamTest.createMap(), EntryStream.of("a", 1, "bb", 22, "ccc", 33).toMap());
        Assert.assertEquals(expected, StreamEx.of(Collections.singletonMap("aaa", 3), Collections.singletonMap("bbb", 3), Collections.singletonMap("c", 1), Collections.emptyMap()).flatMapToEntry(( m) -> m).toMap());
        EntryStream<String, Integer> stream = EntryStream.of(data);
        Assert.assertSame(stream.stream(), EntryStream.of(stream).stream());
        Assert.assertSame(stream.stream(), EntryStream.of(StreamEx.of(EntryStream.of(stream))).stream());
        Assert.assertEquals(Collections.singletonMap("aaa", 3), EntryStream.of(Collections.singletonMap("aaa", 3).entrySet().spliterator()).toMap());
        Assert.assertEquals(Collections.singletonMap("aaa", 3), EntryStream.of(Collections.singletonMap("aaa", 3).entrySet().iterator()).toMap());
    }

    @Test
    public void testCreateKeyValuePairs() {
        EntryStreamTest.checkAsString("a->1", EntryStream.of("a", 1));
        EntryStreamTest.checkAsString("a->1;b->2", EntryStream.of("a", 1, "b", 2));
        EntryStreamTest.checkAsString("a->1;b->2;c->3", EntryStream.of("a", 1, "b", 2, "c", 3));
        EntryStreamTest.checkAsString("a->1;b->2;c->3;d->4", EntryStream.of("a", 1, "b", 2, "c", 3, "d", 4));
        EntryStreamTest.checkAsString("a->1;b->2;c->3;d->4;e->5", EntryStream.of("a", 1, "b", 2, "c", 3, "d", 4, "e", 5));
        EntryStreamTest.checkAsString("a->1;b->2;c->3;d->4;e->5;f->6", EntryStream.of("a", 1, "b", 2, "c", 3, "d", 4, "e", 5, "f", 6));
        EntryStreamTest.checkAsString("a->1;b->2;c->3;d->4;e->5;f->6;g->7", EntryStream.of("a", 1, "b", 2, "c", 3, "d", 4, "e", 5, "f", 6, "g", 7));
        EntryStreamTest.checkAsString("a->1;b->2;c->3;d->4;e->5;f->6;g->7;h->8", EntryStream.of("a", 1, "b", 2, "c", 3, "d", 4, "e", 5, "f", 6, "g", 7, "h", 8));
        EntryStreamTest.checkAsString("a->1;b->2;c->3;d->4;e->5;f->6;g->7;h->8;i->9", EntryStream.of("a", 1, "b", 2, "c", 3, "d", 4, "e", 5, "f", 6, "g", 7, "h", 8, "i", 9));
        EntryStreamTest.checkAsString("a->1;b->2;c->3;d->4;e->5;f->6;g->7;h->8;i->9;j->10", EntryStream.of("a", 1, "b", 2, "c", 3, "d", 4, "e", 5, "f", 6, "g", 7, "h", 8, "i", 9, "j", 10));
    }

    @Test
    public void testGenerate() {
        TestHelpers.entryStream(() -> EntryStream.generate(() -> "a", () -> 1).limit(10), ( es) -> Assert.assertEquals("a-1,a-1,a-1,a-1,a-1,a-1,a-1,a-1,a-1,a-1", es.get().join("-").joining(",")));
    }

    @Test
    public void testSequential() {
        EntryStream<String, Integer> stream = EntryStream.of(EntryStreamTest.createMap());
        Assert.assertFalse(stream.isParallel());
        stream = stream.parallel();
        Assert.assertTrue(stream.isParallel());
        stream = stream.sequential();
        Assert.assertFalse(stream.isParallel());
    }

    @Test
    public void testZip() {
        Map<String, Integer> expected = new HashMap<>();
        expected.put("aaa", 3);
        expected.put("bbb", 3);
        expected.put("c", 1);
        Assert.assertEquals(expected, EntryStream.zip(Arrays.asList("aaa", "bbb", "c"), Arrays.asList(3, 3, 1)).toMap());
        Assert.assertEquals(expected, EntryStream.zip(new String[]{ "aaa", "bbb", "c" }, new Integer[]{ 3, 3, 1 }).toMap());
    }

    @Test
    public void testWithIndex() {
        Map<Integer, String> map = EntryStream.of(Arrays.asList("a", "bbb", "cc")).toMap();
        Assert.assertEquals(3, map.size());
        Assert.assertEquals("a", map.get(0));
        Assert.assertEquals("bbb", map.get(1));
        Assert.assertEquals("cc", map.get(2));
        Map<Integer, String> map2 = EntryStream.of(new String[]{ "a", "bbb", "cc" }).toMap();
        Assert.assertEquals(map, map2);
        Map<Integer, List<String>> grouping = EntryStream.of(new String[]{ "a", "bbb", "cc", null, null }).append(EntryStream.of(new String[]{ "bb", "bbb", "c", null, "e" })).distinct().grouping();
        Assert.assertEquals(Arrays.asList("a", "bb"), grouping.get(0));
        Assert.assertEquals(Arrays.asList("bbb"), grouping.get(1));
        Assert.assertEquals(Arrays.asList("cc", "c"), grouping.get(2));
        Assert.assertEquals(Collections.singletonList(null), grouping.get(3));
        Assert.assertEquals(Arrays.asList(null, "e"), grouping.get(4));
        Assert.assertEquals("0=a,1=bbb,2=cc", EntryStream.of(new String[]{ "a", "bbb", "cc" }).map(Object::toString).joining(","));
        Map.Entry<Integer, String> entry = EntryStream.of(Arrays.asList("a")).findFirst().get();
        // Test equals contract
        Assert.assertNotEquals(new Object(), entry);
        Assert.assertNotEquals(entry, new Object());
        Assert.assertEquals(entry, new AbstractMap.SimpleImmutableEntry<>(0, "a"));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testWithIndexModify() {
        EntryStream.of(Collections.singletonList("1")).forEach(( entry) -> entry.setValue("2"));
    }

    @Test
    public void testMap() {
        Assert.assertEquals(Arrays.asList("1a", "22bb", "33ccc"), EntryStream.of(EntryStreamTest.createMap()).map(( entry) -> (entry.getValue()) + (entry.getKey())).toList());
    }

    @Test
    public void testMapKeyValue() {
        Assert.assertEquals(Arrays.asList("1a", "22bb", "33ccc"), EntryStream.of(EntryStreamTest.createMap()).mapKeyValue(( k, v) -> v + k).toList());
    }

    @Test
    public void testFilter() {
        Assert.assertEquals(Collections.singletonMap("a", 1), EntryStream.of(EntryStreamTest.createMap()).filterKeys(( s) -> (s.length()) < 2).toMap());
        Assert.assertEquals(Collections.singletonMap("bb", 22), EntryStream.of(EntryStreamTest.createMap()).filterValues(( v) -> (v % 2) == 0).toMap());
        Assert.assertEquals(Collections.singletonMap("ccc", 33), EntryStream.of(EntryStreamTest.createMap()).filterKeyValue(( str, num) -> (!(str.equals("a"))) && (num != 22)).toMap());
    }

    @Test
    public void testPeek() {
        List<String> keys = new ArrayList<>();
        Assert.assertEquals(EntryStreamTest.createMap(), EntryStream.of(EntryStreamTest.createMap()).peekKeys(keys::add).toMap());
        Assert.assertEquals(Arrays.asList("a", "bb", "ccc"), keys);
        List<Integer> values = new ArrayList<>();
        Assert.assertEquals(EntryStreamTest.createMap(), EntryStream.of(EntryStreamTest.createMap()).peekValues(values::add).toMap());
        Assert.assertEquals(Arrays.asList(1, 22, 33), values);
        Map<String, Integer> map = new LinkedHashMap<>();
        Assert.assertEquals(EntryStreamTest.createMap(), EntryStream.of(EntryStreamTest.createMap()).peekKeyValue(map::put).toMap());
        Assert.assertEquals(EntryStreamTest.createMap(), map);
    }

    @Test
    public void testRemove() {
        Map<String, List<Integer>> data = new HashMap<>();
        data.put("aaa", Collections.emptyList());
        data.put("bbb", Collections.singletonList(1));
        Assert.assertEquals(Arrays.asList("bbb"), EntryStream.of(data).removeValues(List::isEmpty).keys().toList());
        Assert.assertEquals(Arrays.asList("aaa"), EntryStream.of(data).removeKeys(Pattern.compile("bbb").asPredicate()).keys().toList());
        Assert.assertEquals(EntryStream.of("a", 1, "bb", 22).toMap(), EntryStream.of(EntryStreamTest.createMap()).removeKeyValue(( str, num) -> (!(str.equals("a"))) && (num != 22)).toMap());
    }

    @Test
    public void testLimit() {
        Assert.assertEquals(Collections.singletonMap("a", 1), EntryStream.of(EntryStreamTest.createMap()).limit(1).toMap());
    }

    @Test
    public void testKeys() {
        Assert.assertEquals(new HashSet(Arrays.asList("a", "bb", "ccc")), EntryStream.of(EntryStreamTest.createMap()).keys().toSet());
    }

    @Test
    public void testValues() {
        Assert.assertEquals(new HashSet(Arrays.asList(1, 22, 33)), EntryStream.of(EntryStreamTest.createMap()).values().toSet());
    }

    @Test
    public void testMapKeys() {
        Map<Integer, Integer> expected = new HashMap<>();
        expected.put(1, 1);
        expected.put(2, 22);
        expected.put(3, 33);
        Map<Integer, Integer> result = EntryStream.of(EntryStreamTest.createMap()).mapKeys(String::length).toMap();
        Assert.assertEquals(expected, result);
    }

    @Test
    public void testMapValues() {
        Map<String, String> expected = new HashMap<>();
        expected.put("a", "1");
        expected.put("bb", "22");
        expected.put("ccc", "33");
        Map<String, String> result = EntryStream.of(EntryStreamTest.createMap()).mapValues(String::valueOf).toMap();
        Assert.assertEquals(expected, result);
    }

    @Test
    public void testMapToValue() {
        Map<String, Integer> expected = new HashMap<>();
        expected.put("a", 2);
        expected.put("bb", 24);
        expected.put("ccc", 36);
        Map<String, Integer> result = EntryStream.of(EntryStreamTest.createMap()).mapToValue(( str, num) -> (str.length()) + num).toMap();
        Assert.assertEquals(expected, result);
    }

    @Test
    public void testMapToKey() {
        Map<String, Integer> expected = new HashMap<>();
        expected.put("a:1", 1);
        expected.put("bb:22", 22);
        expected.put("ccc:33", 33);
        Map<String, Integer> result = EntryStream.of(EntryStreamTest.createMap()).mapToKey(( str, num) -> (str + ":") + num).toMap();
        Assert.assertEquals(expected, result);
    }

    @Test
    public void testAppend() {
        Assert.assertEquals(Arrays.asList(22, 33, 5, 22, 33), EntryStream.of(EntryStreamTest.createMap()).append("dddd", 5).append(EntryStreamTest.createMap()).filterKeys(( k) -> (k.length()) > 1).values().toList());
        Assert.assertEquals(EntryStream.of(EntryStreamTest.createMap()).toList(), EntryStream.empty().append("a", 1, "bb", 22, "ccc", 33).toList());
        EntryStreamTest.checkAsString("bb->22;a->1;ccc->33", EntryStream.of("bb", 22).append("a", 1, "ccc", 33));
        EntryStream<String, Integer> stream = EntryStream.of("a", 1, "b", 2);
        Assert.assertSame(stream, stream.append(Collections.emptyMap()));
        Assert.assertNotSame(stream, stream.append(new ConcurrentHashMap()));
    }

    @Test
    public void testPrepend() {
        Assert.assertEquals(Arrays.asList(5, 22, 33, 22, 33), EntryStream.of(EntryStreamTest.createMap()).prepend(EntryStreamTest.createMap()).prepend("dddd", 5).filterKeys(( k) -> (k.length()) > 1).values().toList());
        EntryStreamTest.checkAsString("a->1;ccc->33;bb->22", EntryStream.of("bb", 22).prepend("a", 1, "ccc", 33));
        EntryStreamTest.checkAsString("a->1;ccc->33;dddd->40;bb->22", EntryStream.of("bb", 22).prepend("a", 1, "ccc", 33, "dddd", 40));
        EntryStream<String, Integer> stream = EntryStream.of("a", 1, "b", 2);
        Assert.assertSame(stream, stream.prepend(Collections.emptyMap()));
        Assert.assertNotSame(stream, stream.prepend(new ConcurrentHashMap()));
    }

    @Test
    public void testToMap() {
        Map<String, Integer> base = IntStreamEx.range(100).mapToEntry(String::valueOf, Integer::valueOf).toMap();
        TestHelpers.entryStream(() -> EntryStream.of(base), ( supplier) -> {
            TreeMap<String, Integer> result = supplier.get().toCustomMap(TreeMap::new);
            Assert.assertEquals(base, result);
        });
        Map<Integer, String> expected = new HashMap<>();
        expected.put(3, "aaa");
        expected.put(2, "bbdd");
        Function<TestHelpers.StreamExSupplier<String>, EntryStream<Integer, String>> fn = ( supplier) -> supplier.get().mapToEntry(String::length, Function.identity());
        TestHelpers.streamEx(() -> StreamEx.of("aaa", "bb", "dd"), ( supplier) -> {
            HashMap<Integer, String> customMap = fn.apply(supplier).toCustomMap(String::concat, HashMap::new);
            Assert.assertEquals(expected, customMap);
            Map<Integer, String> map = fn.apply(supplier).toMap(String::concat);
            Assert.assertEquals(expected, map);
            SortedMap<Integer, String> sortedMap = fn.apply(supplier).toSortedMap(String::concat);
            Assert.assertEquals(expected, sortedMap);
            NavigableMap<Integer, String> navigableMap = fn.apply(supplier).toNavigableMap(String::concat);
            Assert.assertEquals(expected, navigableMap);
            TestHelpers.checkIllegalStateException(() -> fn.apply(supplier).toMap(), "2", "dd", "bb");
            TestHelpers.checkIllegalStateException(() -> fn.apply(supplier).toSortedMap(), "2", "dd", "bb");
            TestHelpers.checkIllegalStateException(() -> fn.apply(supplier).toNavigableMap(), "2", "dd", "bb");
            TestHelpers.checkIllegalStateException(() -> fn.apply(supplier).toCustomMap(HashMap::new), "2", "dd", "bb");
        });
        Assert.assertEquals(EntryStreamTest.createMap(), EntryStream.of(EntryStreamTest.createMap()).parallel().toMap());
        Assert.assertTrue(((EntryStream.of(EntryStreamTest.createMap()).parallel().toMap()) instanceof ConcurrentMap));
        SortedMap<String, Integer> sortedMap2 = EntryStream.of(EntryStreamTest.createMap()).toSortedMap();
        Assert.assertEquals(EntryStreamTest.createMap(), sortedMap2);
        Assert.assertFalse((sortedMap2 instanceof ConcurrentMap));
        sortedMap2 = EntryStream.of(EntryStreamTest.createMap()).parallel().toSortedMap();
        Assert.assertEquals(EntryStreamTest.createMap(), sortedMap2);
        Assert.assertTrue((sortedMap2 instanceof ConcurrentMap));
    }

    @Test
    public void testToMapAndThen() {
        Map<String, Integer> map = EntryStream.of(EntryStreamTest.createMap()).append("d", 4).toMapAndThen(EntryStream::of).append("e", 5).toMap();
        Map<String, Integer> expected = EntryStream.of("a", 1, "bb", 22, "ccc", 33, "d", 4, "e", 5).toMap();
        Assert.assertEquals(expected, map);
    }

    @Test
    public void testFlatMap() {
        Assert.assertEquals(Arrays.asList(((int) ('a')), ((int) ('b')), ((int) ('b')), ((int) ('c')), ((int) ('c')), ((int) ('c'))), EntryStream.of(EntryStreamTest.createMap()).flatMap(( entry) -> entry.getKey().chars().boxed()).toList());
        Assert.assertEquals(Arrays.asList("a", "b", "b", "c", "c", "c"), EntryStream.of(EntryStreamTest.createMap()).flatCollection(( entry) -> asList(entry.getKey().split(""))).toList());
        Assert.assertEquals(Arrays.asList("a", 1, "bb", 22, "ccc", 33), EntryStream.of(EntryStreamTest.createMap()).flatMapKeyValue(( str, num) -> Stream.of(str, num)).toList());
    }

    @Test
    public void testFlatMapKeys() {
        Map<String, List<Integer>> data = new HashMap<>();
        data.put("aaa", Arrays.asList(1, 2, 3));
        data.put("bb", Arrays.asList(2, 3, 4));
        Map<Integer, List<String>> result = EntryStream.of(data).invert().flatMapKeys(List::stream).grouping();
        Map<Integer, List<String>> expected = new HashMap<>();
        expected.put(1, Arrays.asList("aaa"));
        expected.put(2, Arrays.asList("aaa", "bb"));
        expected.put(3, Arrays.asList("aaa", "bb"));
        expected.put(4, Arrays.asList("bb"));
        Assert.assertEquals(expected, result);
        Assert.assertEquals(0, EntryStream.<Stream<String>, String>of(null, "a").flatMapKeys(Function.identity()).count());
    }

    @Test
    public void testFlatMapToValue() {
        EntryStreamTest.checkAsString("a->a;a->aa;a->aaa;b->b;b->bb", EntryStream.of("a", 3, "b", 2, "c", 0).flatMapToValue(( str, cnt) -> cnt == 0 ? null : IntStream.rangeClosed(1, cnt).mapToObj(( idx) -> StreamEx.constant(str, idx).joining())));
    }

    @Test
    public void testFlatMapToKey() {
        EntryStreamTest.checkAsString("a->3;aa->3;aaa->3;b->2;bb->2", EntryStream.of("a", 3, "c", 0, "b", 2).flatMapToKey(( str, cnt) -> cnt == 0 ? null : IntStream.rangeClosed(1, cnt).mapToObj(( idx) -> StreamEx.constant(str, idx).joining())));
    }

    @Test
    public void testFlatMapValues() {
        Map<String, List<Integer>> data1 = new HashMap<>();
        data1.put("aaa", Arrays.asList(1, 2, 3));
        data1.put("bb", Arrays.asList(4, 5, 6));
        Map<String, List<Integer>> data2 = new HashMap<>();
        data2.put("aaa", Arrays.asList(10));
        data2.put("bb", Arrays.asList(20));
        data2.put("cc", null);
        Map<String, List<Integer>> result = StreamEx.of(data1, data2, null).flatMapToEntry(( m) -> m).flatMapValues(( l) -> l == null ? null : l.stream()).grouping();
        Map<String, List<Integer>> expected = new HashMap<>();
        expected.put("aaa", Arrays.asList(1, 2, 3, 10));
        expected.put("bb", Arrays.asList(4, 5, 6, 20));
        Assert.assertEquals(expected, result);
        // Find the key which contains the biggest value in the list
        Assert.assertEquals("bb", EntryStream.of(data1).flatMapValues(List::stream).maxByInt(Map.Entry::getValue).map(Map.Entry::getKey).orElse(null));
    }

    @Test
    public void testMapToKeyPartial() {
        Map<Integer, Integer> original = new HashMap<>();
        original.put(1, 1);
        original.put(2, 5);
        original.put(3, 3);
        original.put(4, 4);
        Map<Integer, Integer> expected = new HashMap<>();
        expected.put(1, 1);
        expected.put(9, 3);
        expected.put(16, 4);
        Map<Integer, Integer> actual = EntryStream.of(original).mapToKeyPartial(( key, value) -> {
            if (key.equals(value)) {
                return Optional.of((key * value));
            }
            return Optional.empty();
        }).toMap();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testMapToValuePartial() {
        Map<Integer, Integer> original = new HashMap<>();
        original.put(1, 1);
        original.put(2, 5);
        original.put(3, 3);
        original.put(4, 4);
        Map<Integer, Integer> expected = new HashMap<>();
        expected.put(1, 1);
        expected.put(3, 9);
        expected.put(4, 16);
        Map<Integer, Integer> actual = EntryStream.of(original).mapToValuePartial(( key, value) -> {
            if (key.equals(value)) {
                return Optional.of((key * value));
            }
            return Optional.empty();
        }).toMap();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testMapKeyValuePartial() {
        Map<Integer, Integer> original = new HashMap<>();
        original.put(1, 1);
        original.put(2, 5);
        original.put(3, 3);
        original.put(4, 4);
        List<Integer> expected = Arrays.asList(1, 9, 16);
        List<Integer> actual = EntryStream.of(original).mapKeyValuePartial(( key, value) -> {
            if (key.equals(value)) {
                return Optional.of((key * value));
            }
            return Optional.empty();
        }).toList();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGrouping() {
        Map<String, Integer> data = new LinkedHashMap<>();
        data.put("ab", 1);
        data.put("ac", 2);
        data.put("ba", 3);
        data.put("bc", 4);
        Map<String, List<Integer>> expected = new LinkedHashMap<>();
        expected.put("a", Arrays.asList(1, 2));
        expected.put("b", Arrays.asList(3, 4));
        Supplier<EntryStream<String, Integer>> s = () -> EntryStream.of(data).mapKeys(( k) -> k.substring(0, 1));
        Map<String, List<Integer>> result = s.get().grouping();
        Assert.assertEquals(expected, result);
        TreeMap<String, List<Integer>> resultTree = s.get().grouping(TreeMap::new);
        Assert.assertEquals(expected, resultTree);
        result = s.get().parallel().grouping();
        Assert.assertEquals(expected, result);
        resultTree = s.get().parallel().grouping(TreeMap::new);
        Assert.assertEquals(expected, resultTree);
        TestHelpers.streamEx(() -> IntStreamEx.range(1000).boxed(), ( supplier) -> {
            Assert.assertEquals(EntryStream.of(0, 500, 1, 500).toMap(), supplier.get().mapToEntry(( i) -> i / 500, ( i) -> i).grouping(MoreCollectors.countingInt()));
            ConcurrentSkipListMap<Integer, Integer> map = supplier.get().mapToEntry(( i) -> i / 500, ( i) -> i).grouping(ConcurrentSkipListMap::new, MoreCollectors.countingInt());
            Assert.assertEquals(EntryStream.of(0, 500, 1, 500).toMap(), map);
        });
    }

    @Test
    public void testGroupingTo() {
        Map<String, Integer> data = new LinkedHashMap<>();
        data.put("ab", 1);
        data.put("ac", 2);
        data.put("ba", 3);
        data.put("bc", 4);
        Map<String, List<Integer>> expected = new LinkedHashMap<>();
        expected.put("a", Arrays.asList(1, 2));
        expected.put("b", Arrays.asList(3, 4));
        Supplier<EntryStream<String, Integer>> s = () -> EntryStream.of(data).mapKeys(( k) -> k.substring(0, 1));
        Map<String, List<Integer>> result = s.get().groupingTo(LinkedList::new);
        Assert.assertEquals(expected, result);
        Assert.assertTrue(((result.get("a")) instanceof LinkedList));
        result = s.get().parallel().groupingTo(LinkedList::new);
        Assert.assertEquals(expected, result);
        Assert.assertTrue(((result.get("a")) instanceof LinkedList));
        SortedMap<String, List<Integer>> resultTree = s.get().groupingTo(TreeMap::new, LinkedList::new);
        Assert.assertTrue(((result.get("a")) instanceof LinkedList));
        Assert.assertEquals(expected, resultTree);
        resultTree = s.get().parallel().groupingTo(TreeMap::new, LinkedList::new);
        Assert.assertTrue(((result.get("a")) instanceof LinkedList));
        Assert.assertEquals(expected, resultTree);
        resultTree = s.get().parallel().groupingTo(ConcurrentSkipListMap::new, LinkedList::new);
        Assert.assertTrue(((result.get("a")) instanceof LinkedList));
        Assert.assertEquals(expected, resultTree);
    }

    @Test
    public void testSorting() {
        Map<String, Integer> data = EntryStreamTest.createMap();
        LinkedHashMap<String, Integer> result = EntryStream.of(data).reverseSorted(comparingByValue()).toCustomMap(LinkedHashMap::new);
        Assert.assertEquals("{ccc=33, bb=22, a=1}", result.toString());
    }

    @Test
    public void testDistinct() {
        Map<String, List<Integer>> expected = new LinkedHashMap<>();
        expected.put("aaa", Arrays.asList(3));
        expected.put("bbb", Arrays.asList(3, 3));
        expected.put("cc", Arrays.asList(2));
        Assert.assertEquals(expected, StreamEx.of("aaa", "bbb", "bbb", "cc").mapToEntry(String::length).grouping());
        Map<String, List<Integer>> expectedDistinct = new LinkedHashMap<>();
        expectedDistinct.put("aaa", Arrays.asList(3));
        expectedDistinct.put("bbb", Arrays.asList(3));
        expectedDistinct.put("cc", Arrays.asList(2));
        Assert.assertEquals(expectedDistinct, StreamEx.of("aaa", "bbb", "bbb", "cc").mapToEntry(String::length).distinct().grouping());
    }

    @Test
    public void testNonNull() {
        Map<String, String> input = new LinkedHashMap<>();
        input.put("a", "b");
        input.put("b", null);
        input.put(null, "c");
        Assert.assertEquals(Arrays.asList("b", null), EntryStream.of(input).nonNullKeys().values().toList());
        Assert.assertEquals(Arrays.asList("a", null), EntryStream.of(input).nonNullValues().keys().toList());
        Assert.assertEquals(Collections.singletonMap("a", "b"), EntryStream.of(input).nonNullValues().nonNullKeys().toMap());
    }

    @Test
    public void testSelect() {
        Map<Object, Object> map = new LinkedHashMap<>();
        map.put("a", 1);
        map.put("b", "2");
        map.put(3, "c");
        Assert.assertEquals(Collections.singletonMap("a", 1), EntryStream.of(map).selectValues(Integer.class).toMap());
        Assert.assertEquals(Collections.singletonMap(3, "c"), EntryStream.of(map).selectKeys(Integer.class).toMap());
        // Weird way to create a map from the array. Don't do this in production
        // code!
        Object[] interleavingArray = new Object[]{ "a", 1, "bb", 22, "ccc", 33 };
        Map<String, Integer> result = EntryStream.of(StreamEx.of(interleavingArray).pairMap(AbstractMap.SimpleEntry::new)).selectKeys(String.class).selectValues(Integer.class).toMap();
        Assert.assertEquals(EntryStreamTest.createMap(), result);
    }

    @Test
    public void testInvert() {
        Map<Integer, String> result = EntryStream.of(EntryStreamTest.createMap()).invert().toMap();
        Map<Integer, String> expected = new LinkedHashMap<>();
        expected.put(1, "a");
        expected.put(22, "bb");
        expected.put(33, "ccc");
        Assert.assertEquals(expected, result);
    }

    @Test(expected = IllegalStateException.class)
    public void testCollision() {
        StreamEx.of("aa", "aa").mapToEntry(String::length).toCustomMap(LinkedHashMap::new);
    }

    @Test
    public void testForKeyValue() {
        Map<String, Integer> output = new HashMap<>();
        EntryStream.of(EntryStreamTest.createMap()).forKeyValue(output::put);
        Assert.assertEquals(output, EntryStreamTest.createMap());
    }

    @Test
    public void testJoin() {
        Assert.assertEquals("a = 1; bb = 22; ccc = 33", EntryStream.of(EntryStreamTest.createMap()).join(" = ").joining("; "));
        Assert.assertEquals("{[a = 1]; [bb = 22]; [ccc = 33]}", EntryStream.of(EntryStreamTest.createMap()).join(" = ", "[", "]").joining("; ", "{", "}"));
    }

    @Test
    public void testOfPairs() {
        TestHelpers.withRandom(( r) -> {
            StreamExTest.Point[] pts = StreamEx.generate(() -> new Point(r.nextDouble(), r.nextDouble())).limit(100).toArray(StreamExTest.Point[]::new);
            double expected = StreamEx.of(pts).cross(pts).mapKeyValue(StreamExTest.Point::distance).mapToDouble(Double::doubleValue).max().getAsDouble();
            TestHelpers.entryStream(() -> EntryStream.ofPairs(pts), ( supplier) -> Assert.assertEquals(expected, supplier.get().mapKeyValue(StreamExTest.Point::distance).mapToDouble(Double::doubleValue).max().getAsDouble(), 0.0));
        });
    }

    @Test
    public void testDistinctKeysValues() {
        TestHelpers.entryStream(() -> EntryStream.of(1, "a", 1, "b", 2, "b", 2, "c", 1, "c", 3, "c"), ( s) -> {
            EntryStreamTest.checkAsString("1->a;2->b;3->c", s.get().distinctKeys());
            EntryStreamTest.checkAsString("1->a;1->b;2->c", s.get().distinctValues());
        });
    }

    @Test
    public void testOfTree() {
        TestHelpers.entryStream(() -> EntryStream.ofTree("a", (Integer depth,String str) -> null), ( supplier) -> EntryStreamTest.checkAsString("0->a", supplier.get()));
        List<Object> input = Arrays.asList("aa", null, Arrays.asList(Arrays.asList("bbbb", "cc", null, Arrays.asList()), "ddd", Arrays.asList("e"), Arrays.asList("fff")), "ggg");
        @SuppressWarnings("unchecked")
        Supplier<Stream<Map.Entry<Integer, Object>>> base = () -> EntryStream.ofTree(input, List.class, ( depth, l) -> l.stream());
        TestHelpers.entryStream(base, ( supplier) -> Assert.assertEquals("{1=[aa, ggg], 2=[ddd], 3=[bbbb, cc, e, fff]}", supplier.get().selectValues(String.class).grouping(TreeMap::new).toString()));
        Set<Integer> set = new HashSet<>();
        try (EntryStream<Integer, String> stream = EntryStream.ofTree("", (Integer depth,String str) -> depth >= 3 ? null : Stream.of("a", "b").map(str::concat).onClose(() -> set.add(depth)))) {
            Assert.assertEquals(15, stream.count());
        }
        Assert.assertEquals(StreamEx.of(0, 1, 2).toSet(), set);
        boolean catched = false;
        try (EntryStream<Integer, String> stream = EntryStream.ofTree("", (Integer depth,String str) -> depth >= 3 ? null : Stream.of("a", "b").map(str::concat).onClose(() -> {
            throw new IllegalArgumentException(String.valueOf(depth));
        }))) {
            stream.count();
        } catch (IllegalArgumentException iae) {
            catched = true;
            Assert.assertEquals("2", iae.getMessage());
            Assert.assertEquals(2, iae.getSuppressed().length);
            Assert.assertEquals("1", iae.getSuppressed()[0].getMessage());
            Assert.assertEquals("0", iae.getSuppressed()[1].getMessage());
        }
        Assert.assertTrue(catched);
        TestHelpers.entryStream(() -> EntryStream.ofTree("", (Integer depth,String str) -> depth >= 3 ? null : Stream.of("a", "b").map(str::concat)), ( supplier) -> {
            Assert.assertEquals(Arrays.asList("", "a", "aa", "aaa", "aab", "ab", "aba", "abb", "b", "ba", "baa", "bab", "bb", "bba", "bbb"), supplier.get().values().toList());
            Assert.assertTrue(supplier.get().values().has("bbb"));
            Assert.assertFalse(supplier.get().values().has("ccc"));
            Assert.assertEquals(Arrays.asList("a", "b", "aa", "ab", "ba", "bb", "aaa", "aab", "aba", "abb", "baa", "bab", "bba", "bbb"), supplier.get().sorted(comparingByKey()).values().without("").toList());
        });
    }

    @Test
    public void testCollapseKeys() {
        TestHelpers.entryStream(() -> EntryStream.of(1, "a", 1, "b", 2, "c", 3, "d", 1, "e", 1, "f", 1, "g"), ( s) -> EntryStreamTest.checkAsString("1->[a, b];2->[c];3->[d];1->[e, f, g]", s.get().collapseKeys()));
        TestHelpers.entryStream(() -> EntryStream.of(1, "a", 1, "b", 2, "c", 3, "d", 1, "e", 1, "f", 1, "g"), ( s) -> EntryStreamTest.checkAsString("1->ab;2->c;3->d;1->efg", s.get().collapseKeys(String::concat)));
        TestHelpers.entryStream(() -> EntryStream.of(1, "a", 1, "b", 2, "c", 3, "d", 1, "e", 1, "f", 1, "g"), ( s) -> EntryStreamTest.checkAsString("1->a+b;2->c;3->d;1->e+f+g", s.get().collapseKeys(Collectors.joining("+"))));
        // batches by 3
        Stream<String> s = Stream.of("a", "b", "c", "d", "e", "f", "g", "h", "i", "j");
        Assert.assertEquals(Arrays.asList(Arrays.asList("a", "b", "c"), Arrays.asList("d", "e", "f"), Arrays.asList("g", "h", "i"), Arrays.asList("j")), IntStreamEx.ints().flatMapToObj(( i) -> StreamEx.constant(i, 3)).zipWith(s).collapseKeys().values().toList());
    }

    @Test
    public void testChain() {
        Assert.assertEquals(EntryStream.of(1, "a", 2, "b", 3, "c").toList(), EntryStream.of(1, "a", 2, "b", 2, "b", 3, "c").chain(StreamEx::of).collapse(Objects::equals).toList());
    }

    @Test
    public void testImmutableMap() {
        TestHelpers.repeat(4, ( n) -> {
            Map<Integer, Integer> expected = new HashMap<>();
            for (int i = n; i < 4; i++)
                expected.put(i, i);

            TestHelpers.streamEx(() -> IntStreamEx.range(4).atLeast(n).boxed(), ( s) -> {
                Map<Integer, Integer> map = s.get().mapToEntry(Function.identity()).toImmutableMap();
                Assert.assertEquals(expected, map);
                try {
                    map.put((-1), (-1));
                    Assert.fail("added");
                } catch (UnsupportedOperationException e) {
                    // expected
                }
            });
        });
    }

    @Test
    public void testInto() {
        for (AbstractMap<String, Integer> m : Arrays.<AbstractMap<String, Integer>>asList(new HashMap<>(), new TreeMap<>(), new ConcurrentHashMap<>())) {
            AbstractMap<String, Integer> res = EntryStream.of("a", 1, "b", 2, "c", 3).into(m);
            Assert.assertSame(m, res);
            Assert.assertEquals(EntryStream.of("a", 1, "b", 2, "c", 3).toMap(), m);
            Map<String, Integer> res2 = EntryStream.of("d", 4, "e", 5).parallel().into(m);
            Assert.assertSame(m, res2);
            Assert.assertEquals(EntryStream.of("a", 1, "b", 2, "c", 3, "d", 4, "e", 5).toMap(), m);
            TestHelpers.checkIllegalStateException(() -> EntryStream.of("x", 10, "c", 4).into(m), "c", "3", "4");
            TestHelpers.checkIllegalStateException(() -> EntryStream.of("y", 20, "d", 5).parallel().into(m), "d", "4", "5");
        }
    }

    @Test
    public void testPrefixKeys() {
        Map<String, Integer> map = EntryStream.of("a", 1, "b", 2, "c", 3, "d", 4).prefixValues(Integer::sum).toMap();
        Assert.assertEquals(EntryStream.of("a", 1, "b", 3, "c", 6, "d", 10).toMap(), map);
    }

    @Test
    public void testPrefixValues() {
        Map<String, Integer> map = EntryStream.of("a", 1, "b", 2, "c", 3, "d", 4).prefixKeys(String::concat).toMap();
        Assert.assertEquals(EntryStream.of("a", 1, "ab", 2, "abc", 3, "abcd", 4).toMap(), map);
    }
}

