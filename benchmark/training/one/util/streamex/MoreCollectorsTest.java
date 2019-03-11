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


import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static java.util.stream.Collector.Characteristics.UNORDERED;


/**
 *
 *
 * @author Tagir Valeev
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MoreCollectorsTest {
    static class MyNumber implements Comparable<MoreCollectorsTest.MyNumber> {
        final int value;

        MyNumber(int value) {
            this.value = value;
        }

        @Override
        public int hashCode() {
            return value;
        }

        @Override
        public boolean equals(Object obj) {
            return (obj instanceof MoreCollectorsTest.MyNumber) && ((((MoreCollectorsTest.MyNumber) (obj)).value) == (value));
        }

        @Override
        public int compareTo(MoreCollectorsTest.MyNumber o) {
            return Integer.compare(value, o.value);
        }
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testInstantiate() throws Throwable {
        Constructor<MoreCollectors> constructor = MoreCollectors.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        try {
            constructor.newInstance();
        } catch (InvocationTargetException e) {
            throw e.getCause();
        }
    }

    @Test
    public void testToArray() {
        List<String> input = Arrays.asList("a", "bb", "c", "", "cc", "eee", "bb", "ddd");
        TestHelpers.streamEx(input::stream, ( supplier) -> {
            Map<Integer, String[]> result = supplier.get().groupingBy(String::length, HashMap::new, MoreCollectors.toArray(String[]::new));
            Assert.assertArrayEquals(new String[]{ "" }, result.get(0));
            Assert.assertArrayEquals(new String[]{ "a", "c" }, result.get(1));
            Assert.assertArrayEquals(new String[]{ "bb", "cc", "bb" }, result.get(2));
            Assert.assertArrayEquals(new String[]{ "eee", "ddd" }, result.get(3));
        });
    }

    @Test
    public void testEmpty() {
        List<Integer> list = Stream.of(1, 2, 3).collect(MoreCollectors.head(0));
        Assert.assertTrue(list.isEmpty());
    }

    @Test
    public void testDistinctCount() {
        List<String> input = Arrays.asList("a", "bb", "c", "cc", "eee", "bb", "bc", "ddd");
        TestHelpers.streamEx(input::stream, ( supplier) -> {
            Map<String, Integer> result = supplier.get().groupingBy(( s) -> s.substring(0, 1), HashMap::new, MoreCollectors.distinctCount(String::length));
            Assert.assertEquals(1, ((int) (result.get("a"))));
            Assert.assertEquals(1, ((int) (result.get("b"))));
            Assert.assertEquals(2, ((int) (result.get("c"))));
            Assert.assertEquals(1, ((int) (result.get("d"))));
            Assert.assertEquals(1, ((int) (result.get("e"))));
        });
    }

    @Test
    public void testDistinctBy() {
        List<String> input = Arrays.asList("a", "bb", "c", "cc", "eee", "bb", "bc", "ddd", "ca", "ce", "cf", "ded", "dump");
        TestHelpers.streamEx(input::stream, ( supplier) -> {
            Map<String, List<String>> result = supplier.get().groupingBy(( s) -> s.substring(0, 1), HashMap::new, MoreCollectors.distinctBy(String::length));
            Assert.assertEquals(Arrays.asList("a"), result.get("a"));
            Assert.assertEquals(Arrays.asList("bb"), result.get("b"));
            Assert.assertEquals(Arrays.asList("c", "cc"), result.get("c"));
            Assert.assertEquals(Arrays.asList("ddd", "dump"), result.get("d"));
            Assert.assertEquals(Arrays.asList("eee"), result.get("e"));
        });
    }

    @Test
    public void testMaxAll() {
        List<String> input = Arrays.asList("a", "bb", "c", "", "cc", "eee", "bb", "ddd");
        TestHelpers.checkCollector("maxAll", Arrays.asList("eee", "ddd"), input::stream, MoreCollectors.maxAll(Comparator.comparingInt(String::length)));
        Collector<String, ?, String> maxAllJoin = MoreCollectors.maxAll(Comparator.comparingInt(String::length), Collectors.joining(","));
        TestHelpers.checkCollector("maxAllJoin", "eee,ddd", input::stream, maxAllJoin);
        TestHelpers.checkCollector("minAll", 1L, input::stream, MoreCollectors.minAll(Comparator.comparingInt(String::length), Collectors.counting()));
        TestHelpers.checkCollector("minAllEmpty", Arrays.asList(""), input::stream, MoreCollectors.minAll(Comparator.comparingInt(String::length)));
        TestHelpers.checkCollectorEmpty("maxAll", Collections.emptyList(), MoreCollectors.maxAll(Comparator.comparingInt(String::length)));
        TestHelpers.checkCollectorEmpty("maxAllJoin", "", maxAllJoin);
        TestHelpers.withRandom(( r) -> {
            List<Integer> ints = IntStreamEx.of(r, 10000, 1, 1000).boxed().toList();
            List<Integer> expectedMax = MoreCollectorsTest.getMaxAll(ints, Comparator.naturalOrder());
            List<Integer> expectedMin = MoreCollectorsTest.getMaxAll(ints, Comparator.reverseOrder());
            Collector<Integer, ?, AbstractMap.SimpleEntry<Integer, Long>> downstream = MoreCollectors.pairing(MoreCollectors.first(), Collectors.counting(), ( opt, cnt) -> new AbstractMap.SimpleEntry<>(opt.get(), cnt));
            TestHelpers.checkCollector("maxAll", expectedMax, ints::stream, MoreCollectors.maxAll(Integer::compare));
            TestHelpers.checkCollector("minAll", expectedMin, ints::stream, MoreCollectors.minAll());
            TestHelpers.checkCollector("entry", new AbstractMap.SimpleEntry(expectedMax.get(0), ((long) (expectedMax.size()))), ints::stream, MoreCollectors.maxAll(downstream));
            TestHelpers.checkCollector("entry", new AbstractMap.SimpleEntry(expectedMin.get(0), ((long) (expectedMin.size()))), ints::stream, MoreCollectors.minAll(downstream));
        });
        MoreCollectorsTest.MyNumber a = new MoreCollectorsTest.MyNumber(1);
        MoreCollectorsTest.MyNumber b = new MoreCollectorsTest.MyNumber(1);
        MoreCollectorsTest.MyNumber c = new MoreCollectorsTest.MyNumber(1000);
        MoreCollectorsTest.MyNumber d = new MoreCollectorsTest.MyNumber(1000);
        List<MoreCollectorsTest.MyNumber> nums = IntStreamEx.range(10, 100).mapToObj(MoreCollectorsTest.MyNumber::new).append(a, c).prepend(b, d).toList();
        TestHelpers.streamEx(nums::stream, ( supplier) -> {
            List<MoreCollectorsTest.MyNumber> list = supplier.get().collect(MoreCollectors.maxAll());
            Assert.assertEquals(2, list.size());
            Assert.assertSame(d, list.get(0));
            Assert.assertSame(c, list.get(1));
            list = supplier.get().collect(MoreCollectors.minAll());
            Assert.assertEquals(2, list.size());
            Assert.assertSame(b, list.get(0));
            Assert.assertSame(a, list.get(1));
        });
    }

    @Test
    public void testFirstLast() {
        Supplier<Stream<Integer>> s = () -> IntStreamEx.range(1000).boxed();
        TestHelpers.checkShortCircuitCollector("first", Optional.of(0), 1, s, MoreCollectors.first());
        TestHelpers.checkShortCircuitCollector("firstLong", Optional.of(0), 1, () -> Stream.of(1).flatMap(( x) -> IntStream.range(0, 1000000000).boxed()), MoreCollectors.first(), true);
        TestHelpers.checkShortCircuitCollector("first", Optional.of(1), 1, () -> Stream.iterate(1, ( x) -> x + 1), MoreCollectors.first(), true);
        Assert.assertEquals(1, ((int) (StreamEx.iterate(1, ( x) -> x + 1).parallel().collect(MoreCollectors.first()).get())));
        TestHelpers.checkCollector("last", Optional.of(999), s, MoreCollectors.last());
        TestHelpers.checkCollectorEmpty("first", Optional.empty(), MoreCollectors.first());
        TestHelpers.checkCollectorEmpty("last", Optional.empty(), MoreCollectors.last());
    }

    @Test
    public void testHeadParallel() {
        List<Integer> expected = IntStreamEx.range(0, 2000, 2).boxed().toList();
        List<Integer> expectedShort = Arrays.asList(0, 1);
        for (int i = 0; i < 1000; i++) {
            Assert.assertEquals(("#" + i), expectedShort, IntStreamEx.range(1000).boxed().parallel().collect(MoreCollectors.head(2)));
            Assert.assertEquals(("#" + i), expected, IntStreamEx.range(10000).boxed().parallel().filter(( x) -> (x % 2) == 0).collect(MoreCollectors.head(1000)));
        }
        Assert.assertEquals(expectedShort, StreamEx.iterate(0, ( x) -> x + 1).parallel().collect(MoreCollectors.head(2)));
    }

    @Test
    public void testHeadTail() {
        List<Integer> ints = IntStreamEx.range(1000).boxed().toList();
        TestHelpers.checkShortCircuitCollector("tail(0)", Arrays.asList(), 0, ints::stream, MoreCollectors.tail(0));
        TestHelpers.checkCollector("tail(1)", Arrays.asList(999), ints::stream, MoreCollectors.tail(1));
        TestHelpers.checkCollector("tail(2)", Arrays.asList(998, 999), ints::stream, MoreCollectors.tail(2));
        TestHelpers.checkCollector("tail(500)", ints.subList(500, 1000), ints::stream, MoreCollectors.tail(500));
        TestHelpers.checkCollector("tail(999)", ints.subList(1, 1000), ints::stream, MoreCollectors.tail(999));
        TestHelpers.checkCollector("tail(1000)", ints, ints::stream, MoreCollectors.tail(1000));
        TestHelpers.checkCollector("tail(MAX)", ints, ints::stream, MoreCollectors.tail(Integer.MAX_VALUE));
        TestHelpers.checkShortCircuitCollector("head(0)", Arrays.asList(), 0, ints::stream, MoreCollectors.head(0));
        TestHelpers.checkShortCircuitCollector("head(1)", Arrays.asList(0), 1, ints::stream, MoreCollectors.head(1));
        TestHelpers.checkShortCircuitCollector("head(2)", Arrays.asList(0, 1), 2, ints::stream, MoreCollectors.head(2));
        TestHelpers.checkShortCircuitCollector("head(500)", ints.subList(0, 500), 500, ints::stream, MoreCollectors.head(500));
        TestHelpers.checkShortCircuitCollector("head(999)", ints.subList(0, 999), 999, ints::stream, MoreCollectors.head(999));
        TestHelpers.checkShortCircuitCollector("head(1000)", ints, 1000, ints::stream, MoreCollectors.head(1000));
        TestHelpers.checkShortCircuitCollector("head(MAX)", ints, 1000, ints::stream, MoreCollectors.head(Integer.MAX_VALUE));
        TestHelpers.checkShortCircuitCollector("head(10000)", IntStreamEx.rangeClosed(1, 10000).boxed().toList(), 10000, () -> Stream.iterate(1, ( x) -> x + 1), MoreCollectors.head(10000), true);
        for (int size : new int[]{ 1, 10, 20, 40, 60, 80, 90, 98, 99, 100 }) {
            TestHelpers.checkShortCircuitCollector(("head-unordered-" + size), Collections.nCopies(size, "test"), size, () -> StreamEx.constant("test", 100), MoreCollectors.head(size));
        }
    }

    @Test
    public void testGreatest() {
        TestHelpers.withRandom(( r) -> {
            List<Integer> ints = IntStreamEx.of(r, 1000, 1, 1000).boxed().toList();
            List<Integer> sorted = StreamEx.of(ints).sorted().toList();
            List<Integer> revSorted = StreamEx.of(ints).reverseSorted().toList();
            Comparator<Integer> byString = Comparator.comparing(String::valueOf);
            TestHelpers.checkShortCircuitCollector("least(0)", Collections.emptyList(), 0, ints::stream, MoreCollectors.least(0));
            TestHelpers.checkCollector("least(5)", sorted.subList(0, 5), ints::stream, MoreCollectors.least(5));
            TestHelpers.checkCollector("least(20)", sorted.subList(0, 20), ints::stream, MoreCollectors.least(20));
            TestHelpers.checkCollector("least(MAX)", sorted, ints::stream, MoreCollectors.least(Integer.MAX_VALUE));
            TestHelpers.checkCollector("least(byString, 20)", StreamEx.of(ints).sorted(byString).limit(20).toList(), ints::stream, MoreCollectors.least(byString, 20));
            TestHelpers.checkShortCircuitCollector("greatest(0)", Collections.emptyList(), 0, ints::stream, MoreCollectors.greatest(0));
            TestHelpers.checkCollector("greatest(5)", revSorted.subList(0, 5), ints::stream, MoreCollectors.greatest(5));
            TestHelpers.checkCollector("greatest(20)", revSorted.subList(0, 20), ints::stream, MoreCollectors.greatest(20));
            TestHelpers.checkCollector("greatest(MAX)", revSorted, ints::stream, MoreCollectors.greatest(Integer.MAX_VALUE));
            TestHelpers.checkCollector("greatest(byString, 20)", StreamEx.of(ints).reverseSorted(byString).limit(20).toList(), ints::stream, MoreCollectors.greatest(byString, 20));
            TestHelpers.checkCollector("greatest(byString, 30)", StreamEx.of(ints).reverseSorted(byString).limit(30).toList(), ints::stream, MoreCollectors.greatest(byString, 30));
        });
        Supplier<Stream<Integer>> s = () -> IntStreamEx.range(100).boxed();
        TestHelpers.checkCollector("1", IntStreamEx.range(1).boxed().toList(), s, MoreCollectors.least(1));
        TestHelpers.checkCollector("2", IntStreamEx.range(2).boxed().toList(), s, MoreCollectors.least(2));
        TestHelpers.checkCollector("10", IntStreamEx.range(10).boxed().toList(), s, MoreCollectors.least(10));
        TestHelpers.checkCollector("100", IntStreamEx.range(100).boxed().toList(), s, MoreCollectors.least(100));
        TestHelpers.checkCollector("200", IntStreamEx.range(100).boxed().toList(), s, MoreCollectors.least(200));
    }

    @Test
    public void testCountingInt() {
        TestHelpers.checkCollector("counting", 1000, () -> IntStreamEx.range(1000).boxed(), MoreCollectors.countingInt());
        TestHelpers.checkCollectorEmpty("counting", 0, MoreCollectors.countingInt());
    }

    @Test
    public void testMinIndex() {
        TestHelpers.withRandom(( r) -> {
            List<Integer> ints = IntStreamEx.of(r, 1000, 5, 47).boxed().toList();
            long expectedMin = IntStreamEx.ofIndices(ints).minBy(ints::get).getAsInt();
            long expectedMax = IntStreamEx.ofIndices(ints).maxBy(ints::get).getAsInt();
            long expectedMinString = IntStreamEx.ofIndices(ints).minBy(( i) -> String.valueOf(ints.get(i))).getAsInt();
            long expectedMaxString = IntStreamEx.ofIndices(ints).maxBy(( i) -> String.valueOf(ints.get(i))).getAsInt();
            Comparator<Integer> cmp = Comparator.comparing(String::valueOf);
            TestHelpers.checkCollector("minIndex", OptionalLong.of(expectedMin), ints::stream, MoreCollectors.minIndex());
            TestHelpers.checkCollector("maxIndex", OptionalLong.of(expectedMax), ints::stream, MoreCollectors.maxIndex());
            TestHelpers.checkCollector("minIndex", OptionalLong.of(expectedMinString), ints::stream, MoreCollectors.minIndex(cmp));
            TestHelpers.checkCollector("maxIndex", OptionalLong.of(expectedMaxString), ints::stream, MoreCollectors.maxIndex(cmp));
            Supplier<Stream<String>> supplier = () -> ints.stream().map(Object::toString);
            TestHelpers.checkCollector("minIndex", OptionalLong.of(expectedMinString), supplier, MoreCollectors.minIndex());
            TestHelpers.checkCollector("maxIndex", OptionalLong.of(expectedMaxString), supplier, MoreCollectors.maxIndex());
            TestHelpers.checkCollectorEmpty("minIndex", OptionalLong.empty(), MoreCollectors.<String>minIndex());
            TestHelpers.checkCollectorEmpty("maxIndex", OptionalLong.empty(), MoreCollectors.<String>maxIndex());
        });
    }

    @Test
    public void testGroupingByEnum() {
        EnumMap<TimeUnit, Long> expected = new EnumMap<>(TimeUnit.class);
        EnumSet.allOf(TimeUnit.class).forEach(( tu) -> expected.put(tu, 0L));
        expected.put(TimeUnit.SECONDS, 1L);
        expected.put(TimeUnit.DAYS, 2L);
        expected.put(TimeUnit.NANOSECONDS, 1L);
        TestHelpers.checkCollector("groupingByEnum", expected, () -> Stream.of(TimeUnit.SECONDS, TimeUnit.DAYS, TimeUnit.DAYS, TimeUnit.NANOSECONDS), MoreCollectors.groupingByEnum(TimeUnit.class, Function.identity(), Collectors.counting()));
    }

    @Test(expected = IllegalStateException.class)
    public void testGroupingByWithDomainException() {
        List<Integer> list = Arrays.asList(1, 2, 20, 3, 31, 4);
        Collector<Integer, ?, Map<Integer, List<Integer>>> c = MoreCollectors.groupingBy(( i) -> i % 10, StreamEx.of(0, 1, 2, 3).toSet(), Collectors.toList());
        Map<Integer, List<Integer>> map = list.stream().collect(c);
        System.out.println(map);
    }

    @Test
    public void testGroupingByWithDomain() {
        List<String> data = Arrays.asList("a", "foo", "test", "ququq", "bar", "blahblah");
        Collector<String, ?, String> collector = MoreCollectors.collectingAndThen(MoreCollectors.groupingBy(String::length, IntStreamEx.range(10).boxed().toSet(), TreeMap::new, MoreCollectors.first()), Object::toString);
        TestHelpers.checkShortCircuitCollector("groupingWithDomain", ("{0=Optional.empty, 1=Optional[a], 2=Optional.empty, 3=Optional[foo], 4=Optional[test], 5=Optional[ququq], " + "6=Optional.empty, 7=Optional.empty, 8=Optional[blahblah], 9=Optional.empty}"), data.size(), data::stream, collector);
        Map<String, String> name2sex = new LinkedHashMap<>();
        name2sex.put("Mary", "Girl");
        name2sex.put("John", "Boy");
        name2sex.put("James", "Boy");
        name2sex.put("Lucie", "Girl");
        name2sex.put("Fred", "Boy");
        name2sex.put("Thomas", "Boy");
        name2sex.put("Jane", "Girl");
        name2sex.put("Ruth", "Girl");
        name2sex.put("Melanie", "Girl");
        Collector<Map.Entry<String, String>, ?, Map<String, List<String>>> groupingBy = MoreCollectors.groupingBy(Map.Entry::getValue, StreamEx.of("Girl", "Boy").toSet(), MoreCollectors.mapping(Map.Entry::getKey, MoreCollectors.head(2)));
        AtomicInteger counter = new AtomicInteger();
        Map<String, List<String>> map = EntryStream.of(name2sex).peek(( c) -> counter.incrementAndGet()).collect(groupingBy);
        Assert.assertEquals(Arrays.asList("Mary", "Lucie"), map.get("Girl"));
        Assert.assertEquals(Arrays.asList("John", "James"), map.get("Boy"));
        Assert.assertEquals(4, counter.get());
        Collector<Map.Entry<String, String>, ?, Map<String, String>> groupingByJoin = MoreCollectors.groupingBy(Map.Entry::getValue, StreamEx.of("Girl", "Boy").toSet(), MoreCollectors.mapping(Map.Entry::getKey, Joining.with(", ").maxChars(16).cutAfterDelimiter()));
        counter.set(0);
        Map<String, String> mapJoin = EntryStream.of(name2sex).peek(( c) -> counter.incrementAndGet()).collect(groupingByJoin);
        Assert.assertEquals("Mary, Lucie, ...", mapJoin.get("Girl"));
        Assert.assertEquals("John, James, ...", mapJoin.get("Boy"));
        Assert.assertEquals(7, counter.get());
    }

    @Test
    public void testToBooleanArray() {
        TestHelpers.withRandom(( r) -> {
            List<Integer> input = IntStreamEx.of(r, 1000, 1, 100).boxed().toList();
            boolean[] expected = new boolean[input.size()];
            for (int i = 0; i < (expected.length); i++)
                expected[i] = (input.get(i)) > 50;

            TestHelpers.streamEx(input::stream, ( supplier) -> Assert.assertArrayEquals(expected, supplier.get().collect(MoreCollectors.toBooleanArray(( x) -> x > 50))));
        });
    }

    @Test
    public void testPartitioningBy() {
        Collector<Integer, ?, Map<Boolean, Optional<Integer>>> by20 = MoreCollectors.partitioningBy(( x) -> (x % 20) == 0, MoreCollectors.first());
        Collector<Integer, ?, Map<Boolean, Optional<Integer>>> by200 = MoreCollectors.partitioningBy(( x) -> (x % 200) == 0, MoreCollectors.first());
        Supplier<Stream<Integer>> supplier = () -> IntStreamEx.range(1, 100).boxed();
        TestHelpers.checkShortCircuitCollector("by20", new one.util.streamex.StreamExInternals.BooleanMap(Optional.of(20), Optional.of(1)), 20, supplier, by20);
        TestHelpers.checkShortCircuitCollector("by200", new one.util.streamex.StreamExInternals.BooleanMap(Optional.empty(), Optional.of(1)), 99, supplier, by200);
    }

    @Test
    public void testMapping() {
        List<String> input = Arrays.asList("Capital", "lower", "Foo", "bar");
        Collector<String, ?, Map<Boolean, Optional<Integer>>> collector = MoreCollectors.partitioningBy(( str) -> Character.isUpperCase(str.charAt(0)), MoreCollectors.mapping(String::length, MoreCollectors.first()));
        TestHelpers.checkShortCircuitCollector("mapping", new one.util.streamex.StreamExInternals.BooleanMap(Optional.of(7), Optional.of(5)), 2, input::stream, collector);
        Collector<String, ?, Map<Boolean, Optional<Integer>>> collectorLast = MoreCollectors.partitioningBy(( str) -> Character.isUpperCase(str.charAt(0)), MoreCollectors.mapping(String::length, MoreCollectors.last()));
        TestHelpers.checkCollector("last", new one.util.streamex.StreamExInternals.BooleanMap(Optional.of(3), Optional.of(3)), input::stream, collectorLast);
        input = Arrays.asList("Abc", "Bac", "Aac", "Abv", "Bbc", "Bgd", "Atc", "Bpv");
        Map<Character, List<String>> expected = EntryStream.of('A', Arrays.asList("Abc", "Aac"), 'B', Arrays.asList("Bac", "Bbc")).toMap();
        AtomicInteger cnt = new AtomicInteger();
        Collector<String, ?, Map<Character, List<String>>> groupMap = Collectors.groupingBy(( s) -> s.charAt(0), MoreCollectors.mapping(( x) -> {
            cnt.incrementAndGet();
            return x;
        }, MoreCollectors.head(2)));
        TestHelpers.checkCollector("groupMap", expected, input::stream, groupMap);
        cnt.set(0);
        Assert.assertEquals(expected, input.stream().collect(groupMap));
        Assert.assertEquals(4, cnt.get());
        TestHelpers.checkCollector("mapping-toList", Arrays.asList("a", "b", "c"), Arrays.asList("a1", "b2", "c3")::stream, MoreCollectors.mapping(( str) -> str.substring(0, 1)));
    }

    @Test
    public void testIntersecting() {
        for (int i = 0; i < 5; i++) {
            List<List<String>> input = Arrays.asList(Arrays.asList("aa", "bb", "cc"), Arrays.asList("cc", "bb", "dd"), Arrays.asList("ee", "dd"), Arrays.asList("aa", "bb", "dd"));
            TestHelpers.checkShortCircuitCollector(("#" + i), Collections.emptySet(), 3, input::stream, MoreCollectors.intersecting());
            List<List<Integer>> copies = new ArrayList<>(Collections.nCopies(100, Arrays.asList(1, 2)));
            TestHelpers.checkShortCircuitCollector(("#" + i), StreamEx.of(1, 2).toSet(), 100, copies::stream, MoreCollectors.intersecting());
            copies.addAll(Collections.nCopies(100, Arrays.asList(3)));
            TestHelpers.checkShortCircuitCollector(("#" + i), Collections.emptySet(), 101, copies::stream, MoreCollectors.intersecting());
            TestHelpers.checkCollectorEmpty(("#" + i), Collections.emptySet(), MoreCollectors.intersecting());
        }
    }

    @Test
    public void testAndInt() {
        List<Integer> ints = Arrays.asList(12, 6, 46, 243);
        Collector<Integer, ?, OptionalInt> collector = MoreCollectors.andingInt(Integer::intValue);
        TestHelpers.checkShortCircuitCollector("andInt", OptionalInt.of(0), 4, ints::stream, collector);
        TestHelpers.checkCollectorEmpty("andIntEmpty", OptionalInt.empty(), collector);
        Assert.assertEquals(OptionalInt.of(0), IntStreamEx.iterate(16384, ( i) -> i + 1).parallel().boxed().collect(collector));
        Assert.assertEquals(OptionalInt.of(16384), IntStreamEx.iterate(16384, ( i) -> i + 1).parallel().limit(16383).boxed().collect(collector));
        Collector<Integer, ?, Integer> unwrapped = MoreCollectors.collectingAndThen(MoreCollectors.andingInt(Integer::intValue), OptionalInt::getAsInt);
        Assert.assertTrue(unwrapped.characteristics().contains(UNORDERED));
        TestHelpers.checkShortCircuitCollector("andIntUnwrapped", 0, 4, ints::stream, unwrapped);
        TestHelpers.checkShortCircuitCollector("andIntUnwrapped", 0, 2, Arrays.asList(1, 16, 256)::stream, unwrapped);
    }

    @Test
    public void testAndLong() {
        List<Long> longs = Arrays.asList(-1L, -4294967296L, 281474976645120L);
        TestHelpers.checkShortCircuitCollector("andLong", OptionalLong.of(281470681743360L), 3, longs::stream, MoreCollectors.andingLong(Long::longValue));
        longs = Arrays.asList(1L, 2L, 3L, 4L);
        TestHelpers.checkShortCircuitCollector("andLong", OptionalLong.of(0), 2, longs::stream, MoreCollectors.andingLong(Long::longValue));
        TestHelpers.checkCollectorEmpty("andLongEmpty", OptionalLong.empty(), MoreCollectors.andingLong(Long::longValue));
    }

    @Test
    public void testAndLongFlatMap() {
        TestHelpers.checkShortCircuitCollector("andLongFlat", OptionalLong.of(0), 2, () -> LongStreamEx.of(0).flatMap(( x) -> LongStream.range(1, 100000000)).boxed(), MoreCollectors.andingLong(Long::longValue), true);
    }

    @Test
    public void testFiltering() {
        Collector<Integer, ?, Optional<Integer>> firstEven = MoreCollectors.filtering(( x) -> (x % 2) == 0, MoreCollectors.first());
        Collector<Integer, ?, Optional<Integer>> firstOdd = MoreCollectors.filtering(( x) -> (x % 2) != 0, MoreCollectors.first());
        Collector<Integer, ?, Integer> sumOddEven = MoreCollectors.pairing(firstEven, firstOdd, ( e, o) -> (e.get()) + (o.get()));
        List<Integer> ints = Arrays.asList(1, 3, 5, 7, 9, 10, 8, 6, 4, 2, 3, 7, 11);
        TestHelpers.checkShortCircuitCollector("sumOddEven", 11, 6, ints::stream, sumOddEven);
        Collector<Integer, ?, Long> countEven = MoreCollectors.filtering(( x) -> (x % 2) == 0, Collectors.counting());
        TestHelpers.checkCollector("filtering", 5L, ints::stream, countEven);
        TestHelpers.checkCollector("filtering-toList", Arrays.asList(1, 5, 3, 7), Arrays.asList(1, 2, 4, 5, 4, 3, 0, 7, 8, 10)::stream, MoreCollectors.filtering(( x) -> (x % 2) == 1));
    }

    @Test
    public void testOnlyOne() {
        List<Integer> ints = IntStreamEx.rangeClosed(1, 100).boxed().toList();
        TestHelpers.checkShortCircuitCollector("One", Optional.empty(), 2, ints::stream, MoreCollectors.onlyOne());
        TestHelpers.checkShortCircuitCollector("FilterSeveral", Optional.empty(), 2, () -> ints.stream().filter(( x) -> (x % 20) == 0), MoreCollectors.onlyOne());
        TestHelpers.checkShortCircuitCollector("FilterSeveral2", Optional.empty(), 40, ints::stream, MoreCollectors.filtering(( x) -> (x % 20) == 0, MoreCollectors.onlyOne()));
        TestHelpers.checkShortCircuitCollector("FilterOne", Optional.of(60), 1, () -> ints.stream().filter(( x) -> (x % 60) == 0), MoreCollectors.onlyOne());
        TestHelpers.checkShortCircuitCollector("FilterNone", Optional.empty(), 0, () -> ints.stream().filter(( x) -> (x % 110) == 0), MoreCollectors.onlyOne());
        TestHelpers.checkShortCircuitCollector("FilterSeveral", Optional.empty(), 40, ints::stream, MoreCollectors.onlyOne(( x) -> (x % 20) == 0));
        TestHelpers.checkShortCircuitCollector("FilterOne", Optional.of(60), 100, ints::stream, MoreCollectors.onlyOne(( x) -> (x % 60) == 0));
        TestHelpers.checkShortCircuitCollector("FilterNone", Optional.empty(), 100, ints::stream, MoreCollectors.onlyOne(( x) -> (x % 110) == 0));
    }

    @Test
    public void testToEnumSet() {
        TimeUnit[] vals = TimeUnit.values();
        List<TimeUnit> enumValues = IntStreamEx.range(100).map(( x) -> x % vals.length).elements(vals).toList();
        TestHelpers.checkShortCircuitCollector("toEnumSet", EnumSet.allOf(TimeUnit.class), vals.length, enumValues::stream, MoreCollectors.toEnumSet(TimeUnit.class));
        enumValues = IntStreamEx.range(100).map(( x) -> x % (vals.length - 1)).elements(vals).toList();
        EnumSet<TimeUnit> expected = EnumSet.allOf(TimeUnit.class);
        expected.remove(vals[((vals.length) - 1)]);
        TestHelpers.checkShortCircuitCollector("toEnumSet", expected, 100, enumValues::stream, MoreCollectors.toEnumSet(TimeUnit.class));
        TestHelpers.checkCollectorEmpty("Empty", EnumSet.noneOf(TimeUnit.class), MoreCollectors.toEnumSet(TimeUnit.class));
    }

    @Test
    public void testFlatMapping() {
        {
            Map<Integer, List<Integer>> expected = IntStreamEx.rangeClosed(1, 100).boxed().toMap(( x) -> IntStreamEx.rangeClosed(1, x).boxed().toList());
            Collector<Integer, ?, Map<Integer, List<Integer>>> groupingBy = Collectors.groupingBy(Function.identity(), MoreCollectors.flatMapping(( x) -> IntStream.rangeClosed(1, x).boxed(), Collectors.toList()));
            TestHelpers.checkCollector("flatMappingSimple", expected, () -> IntStreamEx.rangeClosed(1, 100).boxed(), groupingBy);
        }
        Function<Map.Entry<String, List<String>>, Stream<String>> valuesStream = ( e) -> (e.getValue()) == null ? null : e.getValue().stream();
        List<Map.Entry<String, List<String>>> list = EntryStream.of("a", Arrays.asList("bb", "cc", "dd"), "b", Arrays.asList("ee", "ff"), "c", null).append("c", Arrays.asList("gg"), "b", null, "a", Arrays.asList("hh")).toList();
        {
            Map<String, List<String>> expected = EntryStream.of(list.stream()).flatMapValues(( l) -> l == null ? null : l.stream()).grouping();
            TestHelpers.checkCollector("flatMappingCombine", expected, list::stream, Collectors.groupingBy(Map.Entry::getKey, MoreCollectors.flatMapping(valuesStream, Collectors.toList())));
            AtomicInteger openClose = new AtomicInteger();
            Collector<Map.Entry<String, List<String>>, ?, Map<String, List<String>>> groupingBy = Collectors.groupingBy(Map.Entry::getKey, MoreCollectors.flatMapping(valuesStream.andThen(( s) -> {
                if (s == null)
                    return null;

                openClose.incrementAndGet();
                return s.onClose(openClose::decrementAndGet);
            }), Collectors.toList()));
            TestHelpers.checkCollector("flatMappingCombineClosed", expected, list::stream, MoreCollectors.collectingAndThen(groupingBy, ( res) -> {
                assertEquals(0, openClose.get());
                return res;
            }));
            boolean catched = false;
            try {
                Collector<Map.Entry<String, List<String>>, ?, Map<String, List<String>>> groupingByException = Collectors.groupingBy(Map.Entry::getKey, MoreCollectors.flatMapping(valuesStream.andThen(( s) -> {
                    if (s == null)
                        return null;

                    openClose.incrementAndGet();
                    return s.onClose(openClose::decrementAndGet).peek(( e) -> {
                        if (e.equals("gg"))
                            throw new IllegalArgumentException(e);

                    });
                }), Collectors.toList()));
                list.stream().collect(MoreCollectors.collectingAndThen(groupingByException, ( res) -> {
                    assertEquals(0, openClose.get());
                    return res;
                }));
            } catch (IllegalArgumentException e1) {
                Assert.assertEquals("gg", e1.getMessage());
                catched = true;
            }
            Assert.assertTrue(catched);
        }
        {
            Map<String, List<String>> expected = EntryStream.of("a", Arrays.asList("bb"), "b", Arrays.asList("ee"), "c", Arrays.asList("gg")).toMap();
            Collector<Map.Entry<String, List<String>>, ?, List<String>> headOne = MoreCollectors.flatMapping(valuesStream, MoreCollectors.head(1));
            TestHelpers.checkCollector("flatMappingSubShort", expected, list::stream, Collectors.groupingBy(Map.Entry::getKey, headOne));
            TestHelpers.checkShortCircuitCollector("flatMappingShort", expected, 4, list::stream, MoreCollectors.groupingBy(Map.Entry::getKey, StreamEx.of("a", "b", "c").toSet(), headOne));
            AtomicInteger cnt = new AtomicInteger();
            Collector<Map.Entry<String, List<String>>, ?, List<String>> headPeek = MoreCollectors.flatMapping(valuesStream.andThen(( s) -> s == null ? null : s.peek(( x) -> cnt.incrementAndGet())), MoreCollectors.head(1));
            Assert.assertEquals(expected, StreamEx.of(list).collect(Collectors.groupingBy(Map.Entry::getKey, headPeek)));
            Assert.assertEquals(3, cnt.get());
            cnt.set(0);
            Assert.assertEquals(expected, StreamEx.of(list).collect(MoreCollectors.groupingBy(Map.Entry::getKey, StreamEx.of("a", "b", "c").toSet(), headPeek)));
            Assert.assertEquals(3, cnt.get());
        }
        {
            Map<String, List<String>> expected = EntryStream.of("a", Arrays.asList("bb", "cc"), "b", Arrays.asList("ee", "ff"), "c", Arrays.asList("gg")).toMap();
            Collector<Map.Entry<String, List<String>>, ?, List<String>> headTwo = MoreCollectors.flatMapping(valuesStream, MoreCollectors.head(2));
            TestHelpers.checkCollector("flatMappingSubShort", expected, list::stream, Collectors.groupingBy(Map.Entry::getKey, headTwo));
            AtomicInteger openClose = new AtomicInteger();
            boolean catched = false;
            try {
                Collector<Map.Entry<String, List<String>>, ?, Map<String, List<String>>> groupingByException = Collectors.groupingBy(Map.Entry::getKey, MoreCollectors.flatMapping(valuesStream.andThen(( s) -> {
                    if (s == null)
                        return null;

                    openClose.incrementAndGet();
                    return s.onClose(openClose::decrementAndGet).peek(( e) -> {
                        if (e.equals("gg"))
                            throw new IllegalArgumentException(e);

                    });
                }), MoreCollectors.head(2)));
                list.stream().collect(MoreCollectors.collectingAndThen(groupingByException, ( res) -> {
                    assertEquals(0, openClose.get());
                    return res;
                }));
            } catch (IllegalArgumentException e1) {
                Assert.assertEquals("gg", e1.getMessage());
                catched = true;
            }
            Assert.assertTrue(catched);
        }
        TestHelpers.checkCollector("flatMapping-toList", Arrays.asList(0, 1, 2, 3, 0, 1, 2, 0, 1, 2, 3, 4), Arrays.asList(4, 3, 5)::stream, MoreCollectors.flatMapping(( x) -> IntStreamEx.range(x).boxed()));
    }

    @Test(expected = IllegalStateException.class)
    public void testFlatMappingExceptional() {
        Stream.of(1, 2, 3).collect(MoreCollectors.flatMapping(( x) -> Stream.of(1, x).onClose(() -> {
            if (x == 3)
                throw new IllegalStateException();

        }), Collectors.toList()));
    }

    @Test(expected = IllegalStateException.class)
    public void testFlatMappingShortCircuitExceptional() {
        Stream.of(1, 2, 3).collect(MoreCollectors.flatMapping(( x) -> Stream.of(1, x).onClose(() -> {
            if (x == 3)
                throw new IllegalStateException();

        }), MoreCollectors.head(10)));
    }

    @Test
    public void testFlatMappingExceptionalSuppressed() {
        List<Collector<Integer, ?, List<Integer>>> downstreams = Arrays.asList(MoreCollectors.head(10), Collectors.toList());
        for (Collector<Integer, ?, List<Integer>> downstream : downstreams) {
            try {
                Stream.of(1, 2, 3).collect(MoreCollectors.flatMapping(( x) -> Stream.of(1, x).peek(( y) -> {
                    throw new IllegalArgumentException();
                }).onClose(() -> {
                    throw new IllegalStateException();
                }), downstream));
            } catch (Exception e) {
                Assert.assertTrue((e instanceof IllegalArgumentException));
                Assert.assertTrue(((e.getSuppressed()[0]) instanceof IllegalStateException));
                continue;
            }
            Assert.fail("No exception");
        }
    }

    @Test
    public void testCommonPrefix() {
        TestHelpers.checkCollectorEmpty("prefix", "", MoreCollectors.commonPrefix());
        List<String> input = Arrays.asList("abcdef", "abcdefg", "abcdfgfg", "abcefgh", "abcdfg");
        TestHelpers.checkShortCircuitCollector("prefix", "abc", input.size(), input::stream, MoreCollectors.commonPrefix());
        List<CharSequence> inputSeq = Arrays.asList(new StringBuffer("abcdef"), "abcdefg", "abcdfgfg", "abcefgh", new StringBuilder("abcdfg"));
        TestHelpers.checkShortCircuitCollector("prefix", "abc", inputSeq.size(), inputSeq::stream, MoreCollectors.commonPrefix());
        List<String> input2 = Arrays.asList("abcdef", "abcdefg", "dabcdfgfg", "abcefgh", "abcdfg");
        TestHelpers.checkShortCircuitCollector("prefix", "", 3, input2::stream, MoreCollectors.commonPrefix());
        List<String> inputHalf = new ArrayList<>();
        inputHalf.addAll(Collections.nCopies(1000, "abc"));
        inputHalf.addAll(Collections.nCopies(1000, "def"));
        TestHelpers.checkShortCircuitCollector("prefix", "", 1001, inputHalf::stream, MoreCollectors.commonPrefix());
        List<String> inputSurrogate = Arrays.asList("abc\ud801\udc2f", "abc\ud801\udc2f", "abc\ud801\udc14");
        TestHelpers.checkShortCircuitCollector("prefix", "abc", inputSurrogate.size(), inputSurrogate::stream, MoreCollectors.commonPrefix());
        List<String> inputSurrogateBad = Arrays.asList("abc\ud801x", "abc\ud801y", "abc\ud801z");
        TestHelpers.checkShortCircuitCollector("prefix", "abc\ud801", inputSurrogateBad.size(), inputSurrogateBad::stream, MoreCollectors.commonPrefix());
        List<String> inputSurrogateMix = Arrays.asList("abc\ud801\udc2f", "abc\ud801x", "abc\ud801\udc14");
        TestHelpers.checkShortCircuitCollector("prefix", "abc", inputSurrogateMix.size(), inputSurrogateMix::stream, MoreCollectors.commonPrefix());
    }

    @Test
    public void testCommonSuffix() {
        TestHelpers.checkCollectorEmpty("suffix", "", MoreCollectors.commonSuffix());
        List<String> input = Arrays.asList("defabc", "degfabc", "dfgfgabc", "efghabc", "dfgabc");
        TestHelpers.checkShortCircuitCollector("suffix", "abc", input.size(), input::stream, MoreCollectors.commonSuffix());
        List<CharSequence> inputSeq = Arrays.asList(new StringBuffer("degfabc"), "dfgfgabc", new StringBuilder("efghabc"), "defabc", "dfgabc");
        TestHelpers.checkShortCircuitCollector("suffix", "abc", inputSeq.size(), inputSeq::stream, MoreCollectors.commonSuffix());
        List<String> input2 = Arrays.asList("defabc", "defgabc", "dabcdfgfg", "efghabc", "dfgabc");
        TestHelpers.checkShortCircuitCollector("suffix", "", 3, input2::stream, MoreCollectors.commonSuffix());
        List<String> inputHalf = new ArrayList<>();
        inputHalf.addAll(Collections.nCopies(1000, "abc"));
        inputHalf.addAll(Collections.nCopies(1000, "def"));
        TestHelpers.checkShortCircuitCollector("suffix", "", 1001, inputHalf::stream, MoreCollectors.commonSuffix());
        List<String> inputSurrogate = Arrays.asList("\ud801\udc2fabc", "\ud802\udc2fabc", "\ud803\udc2fabc");
        TestHelpers.checkShortCircuitCollector("suffix", "abc", inputSurrogate.size(), inputSurrogate::stream, MoreCollectors.commonSuffix());
        List<String> inputSurrogateBad = Arrays.asList("x\udc2fabc", "y\udc2fabc", "z\udc2fabc");
        TestHelpers.checkShortCircuitCollector("suffix", "\udc2fabc", inputSurrogateBad.size(), inputSurrogateBad::stream, MoreCollectors.commonSuffix());
        List<String> inputSurrogateMix = Arrays.asList("\ud801\udc2fabc", "x\udc2fabc", "\ud801\udc14abc");
        TestHelpers.checkShortCircuitCollector("suffix", "abc", inputSurrogateMix.size(), inputSurrogateMix::stream, MoreCollectors.commonSuffix());
    }

    @Test
    public void testDominators() {
        List<String> input = Arrays.asList("a/", "a/b/c/", "b/c/", "b/d/", "c/a/", "d/a/b/", "c/a/b/", "c/b/", "b/c/d/");
        List<String> expected = Arrays.asList("a/", "b/c/", "b/d/", "c/a/", "c/b/", "d/a/b/");
        TestHelpers.checkCollector("dominators", expected, () -> input.stream().sorted(), MoreCollectors.dominators(( a, b) -> b.startsWith(a)));
        TestHelpers.withRandom(( r) -> {
            List<String> longInput = StreamEx.generate(() -> IntStreamEx.of(r, ((r.nextInt(10)) + 3), 'a', 'z').mapToObj(( ch) -> ((char) (ch))).joining("/", "", "/")).limit(1000).toList();
            List<String> tmp = StreamEx.of(longInput).sorted().toList();
            List<String> result = new ArrayList<>();
            String curr;
            String last;
            curr = last = null;
            for (String next : tmp) {
                String oldLast = last;
                last = curr;
                curr = next;
                if ((last != null) && (curr.startsWith(last))) {
                    curr = last;
                    last = oldLast;
                } else
                    result.add(curr);

            }
            TestHelpers.checkCollector("dominatorsLong", result, () -> longInput.stream().sorted(), MoreCollectors.dominators(( a, b) -> b.startsWith(a)));
        });
    }

    @Test
    public void testIncreasingDominators() {
        int[] input = new int[]{ 1, 3, 4, 2, 1, 7, 5, 3, 4, 0, 4, 6, 7, 10, 4, 3, 2, 1 };
        List<Integer> result = Arrays.asList(1, 3, 4, 7, 10);
        TestHelpers.checkCollector("increasing", result, () -> IntStreamEx.of(input).boxed(), MoreCollectors.dominators(( a, b) -> a >= b));
        TestHelpers.withRandom(( r) -> {
            int[] longInput = r.ints(10000, 0, 1000000).toArray();
            List<Integer> longResult = new ArrayList<>();
            int curMax = -1;
            for (int val : longInput) {
                if (val > curMax) {
                    curMax = val;
                    longResult.add(curMax);
                }
            }
            TestHelpers.checkCollector("increasingLong", longResult, () -> IntStreamEx.of(longInput).boxed(), MoreCollectors.dominators(( a, b) -> a >= b));
        });
    }

    @Test
    public void testMinMax() {
        List<String> input = Arrays.asList("abc", "a", "asdf", "gdasa", "gffsd", "sfgs", "b", "c", "dsgs");
        TestHelpers.checkCollector("minMax", Optional.of("agdasa"), input::stream, MoreCollectors.minMax(Comparator.comparingInt(String::length), String::concat));
        Collector<String, ?, Optional<Object>> collector = MoreCollectors.minMax(Comparator.naturalOrder(), ( min, max) -> {
            throw new IllegalStateException("Should not be called");
        });
        TestHelpers.checkCollectorEmpty("minMax", Optional.empty(), collector);
    }

    @Test
    public void testIfAllMatch() {
        Supplier<Stream<Integer>> five = () -> IntStreamEx.range(5).boxed();
        TestHelpers.checkShortCircuitCollector("ifAllMatch: all match", Optional.of(Arrays.asList(0, 1, 2, 3, 4)), 5, five, MoreCollectors.ifAllMatch(( i) -> true, Collectors.toList()));
        Supplier<Stream<Integer>> ints = () -> IntStreamEx.ints().boxed();
        TestHelpers.checkShortCircuitCollector("ifAllMatch: shirtCircuit downstream", Optional.of(Arrays.asList(0, 1, 2)), 3, ints, MoreCollectors.ifAllMatch(( i) -> true, MoreCollectors.head(3)), true);
        TestHelpers.checkShortCircuitCollector("ifAllMatch: some match", Optional.empty(), 11, ints, MoreCollectors.ifAllMatch(( i) -> i < 10, Collectors.toList()), true);
        TestHelpers.checkShortCircuitCollector("ifAllMatch: empty stream", Optional.of(Collections.emptyList()), 0, Stream::empty, MoreCollectors.ifAllMatch(( i) -> true, Collectors.toList()));
    }
}

