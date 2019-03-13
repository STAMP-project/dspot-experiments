/**
 * __    __  __  __    __  ___
 * \  \  /  /    \  \  /  /  __/
 *  \  \/  /  /\  \  \/  /  /
 *   \____/__/  \__\____/__/
 *
 * Copyright 2014-2019 Vavr, http://vavr.io
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
package io.vavr.collection;


import io.vavr.API;
import io.vavr.Serializables;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.junit.Test;


public class TreeMapTest extends AbstractSortedMapTest {
    // -- bimap
    @Test
    public void shouldBiMapEmpty() {
        assertThat(TreeMap.empty().bimap(Function.identity(), Function.identity())).isEmpty();
    }

    @Test
    public void shouldBiMapNonEmpty() {
        final TreeMap<String, Integer> actual = TreeMap.of(1, "1", 2, "2").bimap(Comparators.naturalComparator(), String::valueOf, Integer::parseInt);
        final TreeMap<String, Integer> expected = TreeMap.of("1", 1, "2", 2);
        assertThat(actual).isEqualTo(expected);
    }

    // -- collector
    @Test
    public void shouldCollectFromJavaStream() {
        final TreeMap<Integer, String> actual = Stream.of(Tuple.of(1, "1"), Tuple.of(2, "2")).collect(TreeMap.collector(Comparators.naturalComparator()));
        final TreeMap<Integer, String> expected = TreeMap.of(1, "1", 2, "2");
        assertThat(actual).isEqualTo(expected);
    }

    // -- construct
    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void shouldConstructFromJavaStreamWithKeyMapperAndValueMapper() {
        final Stream javaStream = Stream.of(1, 2, 3);
        final TreeMap<Integer, String> actual = TreeMap.ofAll(Comparators.naturalComparator(), javaStream, Function.identity(), String::valueOf);
        final TreeMap<Integer, String> expected = TreeMap.of(1, "1", 2, "2", 3, "3");
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldConstructFromJavaStreamWithEntryMapper() {
        final Stream<Integer> javaStream = Stream.of(1, 2, 3);
        final Map<Integer, String> actual = TreeMap.ofAll(Comparators.naturalComparator(), javaStream, ( i) -> Tuple.of(i, String.valueOf(i)));
        final TreeMap<Integer, String> expected = TreeMap.of(1, "1", 2, "2", 3, "3");
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldConstructFromUtilEntries() {
        final TreeMap<Integer, String> actual = TreeMap.ofAll(Comparators.naturalComparator(), asJavaMap(AbstractMapTest.asJavaEntry(1, "1"), AbstractMapTest.asJavaEntry(2, "2"), AbstractMapTest.asJavaEntry(3, "3")));
        final TreeMap<Integer, String> expected = TreeMap.of(1, "1", 2, "2", 3, "3");
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldReturnSingletonFromTupleUsingComparator() {
        final TreeMap<Integer, String> actual = TreeMap.of(Comparators.naturalComparator(), Tuple.of(1, "1"));
        final java.util.Map<Integer, String> expected = asJavaMap(AbstractMapTest.asJavaEntry(1, "1"));
        assertThat(actual.toJavaMap()).isEqualTo(expected);
    }

    @Test
    public void shouldConstructFrom1EntryWithComparator() {
        final Map<Integer, String> actual = TreeMap.of(Comparators.naturalComparator(), 1, "1");
        final java.util.Map<Integer, String> expected = asJavaMap(AbstractMapTest.asJavaEntry(1, "1"));
        assertThat(actual.toJavaMap()).isEqualTo(expected);
    }

    @Test
    public void shouldConstructFrom2EntriesWithComparator() {
        final Map<Integer, String> actual = TreeMap.of(Comparators.naturalComparator(), 1, "1", 2, "2");
        final java.util.Map<Integer, String> expected = asJavaMap(AbstractMapTest.asJavaEntry(1, "1"), AbstractMapTest.asJavaEntry(2, "2"));
        assertThat(actual.toJavaMap()).isEqualTo(expected);
    }

    @Test
    public void shouldConstructFrom3EntriesWithComparator() {
        final Map<Integer, String> actual = TreeMap.of(Comparators.naturalComparator(), 1, "1", 2, "2", 3, "3");
        final java.util.Map<Integer, String> expected = asJavaMap(AbstractMapTest.asJavaEntry(1, "1"), AbstractMapTest.asJavaEntry(2, "2"), AbstractMapTest.asJavaEntry(3, "3"));
        assertThat(actual.toJavaMap()).isEqualTo(expected);
    }

    @Test
    public void shouldConstructFrom4EntriesWithComparator() {
        final Map<Integer, String> actual = TreeMap.of(Comparators.naturalComparator(), 1, "1", 2, "2", 3, "3", 4, "4");
        final java.util.Map<Integer, String> expected = asJavaMap(AbstractMapTest.asJavaEntry(1, "1"), AbstractMapTest.asJavaEntry(2, "2"), AbstractMapTest.asJavaEntry(3, "3"), AbstractMapTest.asJavaEntry(4, "4"));
        assertThat(actual.toJavaMap()).isEqualTo(expected);
    }

    @Test
    public void shouldConstructFrom5EntriesWithComparator() {
        final Map<Integer, String> actual = TreeMap.of(Comparators.naturalComparator(), 1, "1", 2, "2", 3, "3", 4, "4", 5, "5");
        final java.util.Map<Integer, String> expected = asJavaMap(AbstractMapTest.asJavaEntry(1, "1"), AbstractMapTest.asJavaEntry(2, "2"), AbstractMapTest.asJavaEntry(3, "3"), AbstractMapTest.asJavaEntry(4, "4"), AbstractMapTest.asJavaEntry(5, "5"));
        assertThat(actual.toJavaMap()).isEqualTo(expected);
    }

    @Test
    public void shouldConstructFrom6EntriesWithComparator() {
        final Map<Integer, String> actual = TreeMap.of(Comparators.naturalComparator(), 1, "1", 2, "2", 3, "3", 4, "4", 5, "5", 6, "6");
        final java.util.Map<Integer, String> expected = asJavaMap(AbstractMapTest.asJavaEntry(1, "1"), AbstractMapTest.asJavaEntry(2, "2"), AbstractMapTest.asJavaEntry(3, "3"), AbstractMapTest.asJavaEntry(4, "4"), AbstractMapTest.asJavaEntry(5, "5"), AbstractMapTest.asJavaEntry(6, "6"));
        assertThat(actual.toJavaMap()).isEqualTo(expected);
    }

    @Test
    public void shouldConstructFrom7EntriesWithComparator() {
        final Map<Integer, String> actual = TreeMap.of(Comparators.naturalComparator(), 1, "1", 2, "2", 3, "3", 4, "4", 5, "5", 6, "6", 7, "7");
        final java.util.Map<Integer, String> expected = asJavaMap(AbstractMapTest.asJavaEntry(1, "1"), AbstractMapTest.asJavaEntry(2, "2"), AbstractMapTest.asJavaEntry(3, "3"), AbstractMapTest.asJavaEntry(4, "4"), AbstractMapTest.asJavaEntry(5, "5"), AbstractMapTest.asJavaEntry(6, "6"), AbstractMapTest.asJavaEntry(7, "7"));
        assertThat(actual.toJavaMap()).isEqualTo(expected);
    }

    @Test
    public void shouldConstructFrom8EntriesWithComparator() {
        final Map<Integer, String> actual = TreeMap.of(Comparators.naturalComparator(), 1, "1", 2, "2", 3, "3", 4, "4", 5, "5", 6, "6", 7, "7", 8, "8");
        final java.util.Map<Integer, String> expected = asJavaMap(AbstractMapTest.asJavaEntry(1, "1"), AbstractMapTest.asJavaEntry(2, "2"), AbstractMapTest.asJavaEntry(3, "3"), AbstractMapTest.asJavaEntry(4, "4"), AbstractMapTest.asJavaEntry(5, "5"), AbstractMapTest.asJavaEntry(6, "6"), AbstractMapTest.asJavaEntry(7, "7"), AbstractMapTest.asJavaEntry(8, "8"));
        assertThat(actual.toJavaMap()).isEqualTo(expected);
    }

    @Test
    public void shouldConstructFrom9EntriesWithComparator() {
        final Map<Integer, String> actual = TreeMap.of(Comparators.naturalComparator(), 1, "1", 2, "2", 3, "3", 4, "4", 5, "5", 6, "6", 7, "7", 8, "8", 9, "9");
        final java.util.Map<Integer, String> expected = asJavaMap(AbstractMapTest.asJavaEntry(1, "1"), AbstractMapTest.asJavaEntry(2, "2"), AbstractMapTest.asJavaEntry(3, "3"), AbstractMapTest.asJavaEntry(4, "4"), AbstractMapTest.asJavaEntry(5, "5"), AbstractMapTest.asJavaEntry(6, "6"), AbstractMapTest.asJavaEntry(7, "7"), AbstractMapTest.asJavaEntry(8, "8"), AbstractMapTest.asJavaEntry(9, "9"));
        assertThat(actual.toJavaMap()).isEqualTo(expected);
    }

    @Test
    public void shouldConstructFrom10EntriesWithComparator() {
        final Map<Integer, String> actual = TreeMap.of(Comparators.naturalComparator(), 1, "1", 2, "2", 3, "3", 4, "4", 5, "5", 6, "6", 7, "7", 8, "8", 9, "9", 10, "10");
        final java.util.Map<Integer, String> expected = asJavaMap(AbstractMapTest.asJavaEntry(1, "1"), AbstractMapTest.asJavaEntry(2, "2"), AbstractMapTest.asJavaEntry(3, "3"), AbstractMapTest.asJavaEntry(4, "4"), AbstractMapTest.asJavaEntry(5, "5"), AbstractMapTest.asJavaEntry(6, "6"), AbstractMapTest.asJavaEntry(7, "7"), AbstractMapTest.asJavaEntry(8, "8"), AbstractMapTest.asJavaEntry(9, "9"), AbstractMapTest.asJavaEntry(10, "10"));
        assertThat(actual.toJavaMap()).isEqualTo(expected);
    }

    // -- static factories
    @Test
    public void shouldCreateOfEntriesUsingNoComparator() {
        final API.List<Tuple2<Integer, String>> expected = API.List(Tuple(1, "a"), Tuple(2, "b"));
        final TreeMap<Integer, String> actual = TreeMap.ofEntries(expected);
        assertThat(actual.toList()).isEqualTo(expected);
    }

    @Test
    public void shouldCreateOfEntriesUsingNaturalComparator() {
        final API.List<Tuple2<Integer, String>> expected = API.List(Tuple(1, "a"), Tuple(2, "b"));
        final TreeMap<Integer, String> actual = TreeMap.ofEntries(Comparators.naturalComparator(), expected);
        assertThat(actual.toList()).isEqualTo(expected);
    }

    @Test
    public void shouldCreateOfEntriesUsingKeyComparator() {
        final TreeMap<Integer, String> actual = TreeMap.ofEntries(Comparators.naturalComparator(), AbstractMapTest.asJavaEntry(1, "a"), AbstractMapTest.asJavaEntry(2, "b"));
        final API.List<Tuple2<Integer, String>> expected = API.List(Tuple(1, "a"), Tuple(2, "b"));
        assertThat(actual.toList()).isEqualTo(expected);
    }

    // -- static narrow
    @Test
    public void shouldNarrowTreeMap() {
        final TreeMap<Integer, Double> int2doubleMap = mapOf(1, 1.0);
        final TreeMap<Integer, Number> number2numberMap = TreeMap.narrow(int2doubleMap);
        final int actual = number2numberMap.put(2, new BigDecimal("2.0")).values().sum().intValue();
        assertThat(actual).isEqualTo(3);
    }

    @Test
    public void shouldScan() {
        final TreeMap<String, Integer> tm = TreeMap.ofEntries(Tuple.of("one", 1), Tuple.of("two", 2));
        final TreeMap<String, Integer> result = tm.scan(Tuple.of("z", 0), ( t1, t2) -> Tuple.of((t1._1 + t2._1), (t1._2 + t2._2)));
        assertThat(result).isEqualTo(TreeMap.ofEntries(Tuple.of("z", 0), Tuple.of("zone", 1), Tuple.of("zonetwo", 3)));
    }

    @Test
    public void shouldScanLeft() {
        final TreeMap<String, Integer> tm = TreeMap.ofEntries(Tuple.of("one", 1), Tuple.of("two", 2));
        final Seq<Tuple2<String, Integer>> result = tm.scanLeft(Tuple.of("z", 0), ( t1, t2) -> Tuple.of((t1._1 + t2._1), (t1._2 + t2._2)));
        assertThat(result).isEqualTo(API.List.of(Tuple.of("z", 0), Tuple.of("zone", 1), Tuple.of("zonetwo", 3)));
    }

    @Test
    public void shouldScanRight() {
        final TreeMap<String, Integer> tm = TreeMap.ofEntries(Tuple.of("one", 1), Tuple.of("two", 2));
        final Seq<String> result = tm.scanRight("z", ( t1, acc) -> acc + (CharSeq.of(t1._1).reverse()));
        assertThat(result).isEqualTo(API.List.of("zowteno", "zowt", "z"));
    }

    @Test
    public void shouldWrapMap() {
        final java.util.Map<Integer, Integer> source = new HashMap<>();
        source.put(1, 2);
        source.put(3, 4);
        assertThat(TreeMap.ofAll(source)).isEqualTo(put(3, 4));
    }

    // -- ofAll
    @Test
    public void shouldCreateKeyComparatorForJavaUtilMap() {
        final TreeMap<String, Integer> actual = TreeMap.ofAll(mapOfTuples(Tuple.of("c", 0), Tuple.of("a", 0), Tuple.of("b", 0)).toJavaMap());
        final API.List<String> expected = API.List.of("a", "b", "c");
        assertThat(actual.keySet().toList()).isEqualTo(expected);
    }

    @Test
    public void shouldSerializeDeserializeNonEmptyMap() {
        final Object expected = TreeMap.ofAll(Collections.singletonMap(0, 1));
        final Object actual = Serializables.deserialize(Serializables.serialize(expected));
        assertThat(actual).isEqualTo(expected);
    }

    // -- fill
    @Test
    public void shouldFillWithComparator() {
        final LinkedList<Integer> ints = new LinkedList<>(Arrays.asList(0, 0, 1, 1, 2, 2));
        final Supplier<Tuple2<Long, Float>> supplier = () -> Tuple.of(ints.remove().longValue(), ints.remove().floatValue());
        final TreeMap<Long, Float> actual = TreeMap.fill(Comparators.naturalComparator(), 3, supplier);
        final TreeMap<Long, Float> expected = TreeMap.of(0L, 0.0F, 1L, 1.0F, 2L, 2.0F);
        assertThat(actual).isEqualTo(expected);
    }

    // -- flatMap
    @Test
    public void shouldReturnATreeMapWithCorrectComparatorWhenFlatMappingToEmpty() {
        final TreeMap<Integer, String> testee = TreeMap.of(Comparator.naturalOrder(), 1, "1", 2, "2");
        assertThat(testee.head()).isEqualTo(Tuple(1, "1"));
        final TreeMap<Integer, String> actual = testee.flatMap(Comparator.reverseOrder(), ( k, v) -> API.List.empty());
        assertThat(actual).isEmpty();
        final TreeMap<Integer, String> actualSorted = actual.put(1, "1").put(2, "2");
        assertThat(actualSorted.head()).isEqualTo(Tuple(2, "2"));
    }

    // -- map
    @Test
    public void shouldReturnModifiedKeysMapWithNonUniqueMapperAndPredictableOrder() {
        final TreeMap<Integer, String> actual = TreeMap.of(3, "3", 1, "1", 2, "2").mapKeys(Integer::toHexString).mapKeys(String::length);
        final TreeMap<Integer, String> expected = TreeMap.of(1, "3");
        assertThat(actual).isEqualTo(expected);
    }

    // -- tabulate
    @Test
    public void shouldTabulateWithComparator() {
        final TreeMap<Integer, String> actual = TreeMap.tabulate(Comparators.naturalComparator(), 3, ( i) -> Tuple.of(i, String.valueOf(i)));
        final TreeMap<Integer, String> expected = TreeMap.of(0, "0", 1, "1", 2, "2");
        assertThat(actual).isEqualTo(expected);
    }
}

