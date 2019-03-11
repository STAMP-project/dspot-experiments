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


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.AbstractList;
import java.util.AbstractMap;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.SortedMap;
import java.util.Spliterator;
import java.util.TreeSet;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runners.MethodSorters;


/**
 *
 *
 * @author Tagir Valeev
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class StreamExTest {
    @Rule
    public final TemporaryFolder tmp = new TemporaryFolder();

    @Test
    public void testCreate() {
        Assert.assertEquals(Arrays.asList(), StreamEx.empty().toList());
        // double test is intended
        Assert.assertEquals(Arrays.asList(), StreamEx.empty().toList());
        Assert.assertEquals(Arrays.asList("a"), StreamEx.of("a").toList());
        Assert.assertEquals(Arrays.asList("a"), StreamEx.of(Optional.of("a")).toList());
        Assert.assertEquals(Arrays.asList(), StreamEx.of(Optional.ofNullable(null)).toList());
        Assert.assertEquals(Arrays.asList(), StreamEx.ofNullable(null).toList());
        Assert.assertEquals(Arrays.asList("a"), StreamEx.ofNullable("a").toList());
        Assert.assertEquals(Arrays.asList(((String) (null))), StreamEx.of(((String) (null))).toList());
        Assert.assertEquals(Arrays.asList("a", "b"), StreamEx.of("a", "b").toList());
        Assert.assertEquals(Arrays.asList("a", "b"), StreamEx.of(Arrays.asList("a", "b")).toList());
        Assert.assertEquals(Arrays.asList("a", "b"), StreamEx.of(Stream.of("a", "b")).toList());
        Assert.assertEquals(Arrays.asList("a", "b"), StreamEx.split("a,b", ",").toList());
        Assert.assertEquals(Arrays.asList("a", "c", "d"), StreamEx.split("abcBd", Pattern.compile("b", Pattern.CASE_INSENSITIVE)).toList());
        Assert.assertEquals(Arrays.asList("a", "b"), StreamEx.ofLines(new StringReader("a\nb")).toList());
        Assert.assertEquals(Arrays.asList("a", "b"), StreamEx.ofLines(new BufferedReader(new StringReader("a\nb"))).toList());
        Assert.assertEquals(Arrays.asList("a", "b"), StreamEx.ofLines(StreamExTest.getReader()).toList());
        Assert.assertEquals(Arrays.asList("a", "a", "a", "a"), StreamEx.generate(() -> "a").limit(4).toList());
        Assert.assertEquals(Arrays.asList("a", "a", "a", "a"), StreamEx.constant("a", 4).toList());
        Assert.assertEquals(Arrays.asList("c", "d", "e"), StreamEx.of("abcdef".split(""), 2, 5).toList());
        StreamEx<String> stream = StreamEx.of("foo", "bar");
        Assert.assertSame(stream.stream(), StreamEx.of(stream).stream());
        Assert.assertEquals(Arrays.asList("a1", "b2", "c3"), StreamEx.zip(Arrays.asList("a", "b", "c"), Arrays.asList(1, 2, 3), ( s, i) -> s + i).toList());
        Assert.assertEquals(Arrays.asList("a1", "b2", "c3"), StreamEx.zip(new String[]{ "a", "b", "c" }, new Integer[]{ 1, 2, 3 }, ( s, i) -> s + i).toList());
        Assert.assertEquals(Arrays.asList("a", "b"), StreamEx.of(Arrays.asList("a", "b").spliterator()).toList());
        Assert.assertEquals(Arrays.asList("a", "b"), StreamEx.of(Arrays.asList("a", "b").iterator()).toList());
        Assert.assertEquals(Arrays.asList(), StreamEx.of(Arrays.asList().iterator()).toList());
        Assert.assertEquals(Arrays.asList(), StreamEx.of(Arrays.asList().iterator()).parallel().toList());
        Assert.assertEquals(Arrays.asList("a", "b"), StreamEx.of(new Vector<>(Arrays.asList("a", "b")).elements()).toList());
        Assert.assertEquals(Arrays.asList("a", "b", "c", "d"), StreamEx.ofReversed(Arrays.asList("d", "c", "b", "a")).toList());
        Assert.assertEquals(Arrays.asList("a", "b", "c", "d"), StreamEx.ofReversed(new String[]{ "d", "c", "b", "a" }).toList());
    }

    @Test
    public void testIterate() {
        Assert.assertEquals(Arrays.asList("a", "aa", "aaa", "aaaa"), StreamEx.iterate("a", ( x) -> x + "a").limit(4).toList());
        Assert.assertEquals(Arrays.asList("a", "aa", "aaa", "aaaa"), StreamEx.iterate("a", ( x) -> (x.length()) <= 4, ( x) -> x + "a").toList());
        Assert.assertFalse(StreamEx.iterate("a", ( x) -> (x.length()) <= 10, ( x) -> x + "a").has("b"));
        Assert.assertEquals(0, StreamEx.iterate("", ( x) -> !(x.isEmpty()), ( x) -> x.substring(1)).count());
        TestHelpers.checkSpliterator("iterate", () -> StreamEx.iterate(1, ( x) -> x < 100, ( x) -> x * 2).spliterator());
    }

    @Test
    public void testCreateFromFile() throws IOException {
        File f = tmp.newFile();
        List<String> input = Arrays.asList("Some", "Test", "Lines");
        Files.write(f.toPath(), input);
        Assert.assertEquals(input, StreamEx.ofLines(f.toPath()).toList());
        Files.write(f.toPath(), input, StandardCharsets.UTF_16);
        Assert.assertEquals(input, StreamEx.ofLines(f.toPath(), StandardCharsets.UTF_16).toList());
    }

    @Test
    public void testReader() {
        String input = IntStreamEx.range(5000).joining("\n");
        List<String> expectedList = IntStreamEx.range(1, 5000).mapToObj(String::valueOf).toList();
        Set<String> expectedSet = IntStreamEx.range(1, 5000).mapToObj(String::valueOf).toSet();
        // Saving actual to separate variable helps to work-around
        // javac <8u40 issue JDK-8056984
        List<String> actualList = StreamEx.ofLines(new StringReader(input)).skip(1).parallel().toList();
        Assert.assertEquals(expectedList, actualList);
        actualList = StreamEx.ofLines(new StringReader(input)).pairMap(( a, b) -> b).parallel().toList();
        Assert.assertEquals(expectedList, actualList);
        Set<String> actualSet = StreamEx.ofLines(new StringReader(input)).pairMap(( a, b) -> b).parallel().toSet();
        Assert.assertEquals(expectedSet, actualSet);
        actualSet = StreamEx.ofLines(new StringReader(input)).skip(1).parallel().toCollection(HashSet::new);
        Assert.assertEquals(expectedSet, actualSet);
        actualSet = StreamEx.ofLines(new StringReader(input)).parallel().skipOrdered(1).toSet();
        Assert.assertEquals(expectedSet, actualSet);
        actualSet = StreamEx.ofLines(new StringReader(input)).skipOrdered(1).parallel().toSet();
        Assert.assertEquals(expectedSet, actualSet);
        Assert.assertFalse(StreamEx.ofLines(new StringReader(input)).spliterator().getClass().getSimpleName().endsWith("IteratorSpliterator"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testZipThrows() {
        StreamEx.zip(Arrays.asList("A"), Arrays.asList("b", "c"), String::concat);
    }

    @Test
    public void testBasics() {
        Assert.assertFalse(StreamEx.of("a").isParallel());
        Assert.assertTrue(StreamEx.of("a").parallel().isParallel());
        Assert.assertFalse(StreamEx.of("a").parallel().sequential().isParallel());
        AtomicInteger i = new AtomicInteger();
        try (Stream<String> s = StreamEx.of("a").onClose(i::incrementAndGet)) {
            Assert.assertEquals(1, s.count());
        }
        Assert.assertEquals(1, i.get());
        Assert.assertEquals(Arrays.asList(1, 2), StreamEx.of("a", "bb").map(String::length).toList());
        Assert.assertFalse(StreamEx.empty().findAny().isPresent());
        Assert.assertEquals("a", StreamEx.of("a").findAny().get());
        Assert.assertFalse(StreamEx.empty().findFirst().isPresent());
        Assert.assertEquals("a", StreamEx.of("a", "b").findFirst().get());
        Assert.assertEquals(Arrays.asList("b", "c"), StreamEx.of("a", "b", "c").skip(1).toList());
        AtomicBoolean b = new AtomicBoolean(false);
        try (Stream<String> stream = StreamEx.of("a").onClose(() -> b.set(true))) {
            Assert.assertFalse(b.get());
            Assert.assertEquals(1, stream.count());
            Assert.assertFalse(b.get());
        }
        Assert.assertTrue(b.get());
        Assert.assertTrue(StreamEx.of("a", "b").anyMatch("a"::equals));
        Assert.assertFalse(StreamEx.of("a", "b").anyMatch("c"::equals));
        Assert.assertFalse(StreamEx.of("a", "b").allMatch("a"::equals));
        Assert.assertFalse(StreamEx.of("a", "b").allMatch("c"::equals));
        Assert.assertFalse(StreamEx.of("a", "b").noneMatch("a"::equals));
        Assert.assertTrue(StreamEx.of("a", "b").noneMatch("c"::equals));
        Assert.assertTrue(StreamEx.of().noneMatch("a"::equals));
        Assert.assertTrue(StreamEx.of().allMatch("a"::equals));
        Assert.assertFalse(StreamEx.of().anyMatch("a"::equals));
        Assert.assertEquals("abbccc", StreamEx.of("a", "bb", "ccc").collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString());
        Assert.assertArrayEquals(new String[]{ "a", "b", "c" }, StreamEx.of("a", "b", "c").toArray(String[]::new));
        Assert.assertArrayEquals(new Object[]{ "a", "b", "c" }, StreamEx.of("a", "b", "c").toArray());
        Assert.assertEquals(3, StreamEx.of("a", "b", "c").spliterator().getExactSizeIfKnown());
        Assert.assertTrue(StreamEx.of("a", "b", "c").spliterator().hasCharacteristics(Spliterator.ORDERED));
        Assert.assertFalse(StreamEx.of("a", "b", "c").unordered().spliterator().hasCharacteristics(Spliterator.ORDERED));
    }

    @Test
    public void testCovariance() {
        StreamEx<Number> stream = StreamEx.of(1, 2, 3);
        List<Number> list = stream.toList();
        Assert.assertEquals(Arrays.asList(1, 2, 3), list);
        StreamEx<Object> objStream = StreamEx.of(list.spliterator());
        List<Object> objList = objStream.toList();
        Assert.assertEquals(Arrays.asList(1, 2, 3), objList);
    }

    @Test
    public void testToList() {
        List<Integer> list = StreamEx.of(1, 2, 3).toList();
        // Test that returned list is mutable
        List<Integer> list2 = StreamEx.of(4, 5, 6).parallel().toList();
        list2.add(7);
        list.addAll(list2);
        Assert.assertEquals(Arrays.asList(1, 2, 3, 4, 5, 6, 7), list);
    }

    @Test
    public void testToArray() {
        Number[] numbers = StreamEx.of(1, 2, 3).toArray(Number.class);
        Assert.assertArrayEquals(new Number[]{ 1, 2, 3 }, numbers);
        Assert.assertEquals(Number.class, numbers.getClass().getComponentType());
        Integer[] emptyArray = new Integer[]{  };
        Assert.assertSame(emptyArray, StreamEx.of(1, 2, 3).filter(( x) -> x > 3).toArray(emptyArray));
        Assert.assertArrayEquals(new Integer[]{ 1, 2, 3 }, StreamEx.of(1, 2, 3).remove(( x) -> x > 3).toArray(emptyArray));
    }

    @Test
    public void testForEach() {
        List<Integer> list = new ArrayList<>();
        StreamEx.of(1, 2, 3).forEach(list::add);
        Assert.assertEquals(Arrays.asList(1, 2, 3), list);
        StreamEx.of(1, 2, 3).forEachOrdered(list::add);
        Assert.assertEquals(Arrays.asList(1, 2, 3, 1, 2, 3), list);
        StreamEx.of(1, 2, 3).parallel().forEachOrdered(list::add);
        Assert.assertEquals(Arrays.asList(1, 2, 3, 1, 2, 3, 1, 2, 3), list);
    }

    @Test
    public void testFlatMap() {
        Assert.assertArrayEquals(new int[]{ 0, 0, 1, 0, 0, 1, 0, 0 }, StreamEx.of("111", "222", "333").flatMapToInt(( s) -> s.chars().map(( ch) -> ch - '0')).pairMap(( a, b) -> b - a).toArray());
        Assert.assertArrayEquals(new long[]{ 0, 0, 1, 0, 0, 1, 0, 0 }, StreamEx.of("111", "222", "333").flatMapToLong(( s) -> s.chars().mapToLong(( ch) -> ch - '0')).pairMap(( a, b) -> b - a).toArray());
        Assert.assertArrayEquals(new double[]{ 0, 0, 1, 0, 0, 1, 0, 0 }, StreamEx.of("111", "222", "333").flatMapToDouble(( s) -> s.chars().mapToDouble(( ch) -> ch - '0')).pairMap(( a, b) -> b - a).toArray(), 0.0);
    }

    @Test
    public void testAndThen() {
        HashSet<String> set = StreamEx.of("a", "bb", "ccc").toListAndThen(HashSet::new);
        Assert.assertEquals(3, set.size());
        Assert.assertTrue(set.contains("bb"));
        ArrayList<String> list = StreamEx.of("a", "bb", "ccc").toSetAndThen(ArrayList::new);
        Assert.assertEquals(3, list.size());
        Assert.assertTrue(list.contains("bb"));
    }

    @Test
    public void testToMap() {
        Map<String, Integer> expected = new HashMap<>();
        expected.put("a", 1);
        expected.put("bb", 2);
        expected.put("ccc", 3);
        Map<Integer, String> expected2 = new HashMap<>();
        expected2.put(1, "a");
        expected2.put(2, "bb");
        expected2.put(3, "ccc");
        TestHelpers.streamEx(() -> Stream.of("a", "bb", "ccc"), ( supplier) -> {
            Map<String, Integer> map = supplier.get().toMap(String::length);
            Assert.assertEquals(supplier.get().isParallel(), (map instanceof ConcurrentMap));
            Assert.assertEquals(expected, map);
            Map<Integer, String> map2 = supplier.get().toMap(String::length, Function.identity());
            Assert.assertEquals(supplier.get().isParallel(), (map2 instanceof ConcurrentMap));
            Assert.assertEquals(expected2, map2);
        });
        Map<Integer, String> expected3 = new HashMap<>();
        expected3.put(1, "a");
        expected3.put(2, "bbdd");
        expected3.put(3, "ccc");
        TestHelpers.streamEx(() -> Stream.of("a", "bb", "ccc", "dd"), ( supplier) -> {
            Map<Integer, String> seqMap3 = supplier.get().toMap(String::length, Function.identity(), String::concat);
            Assert.assertEquals(expected3, seqMap3);
            TestHelpers.checkIllegalStateException(() -> supplier.get().toMap(String::length, Function.identity()), "2", "dd", "bb");
        });
    }

    @Test
    public void testToSortedMap() {
        Map<String, Integer> expected = new HashMap<>();
        expected.put("a", 1);
        expected.put("bb", 2);
        expected.put("ccc", 3);
        Map<Integer, String> expected2 = new HashMap<>();
        expected2.put(1, "a");
        expected2.put(2, "bb");
        expected2.put(3, "ccc");
        TestHelpers.streamEx(() -> Stream.of("a", "bb", "ccc"), ( supplier) -> {
            SortedMap<String, Integer> map = supplier.get().toSortedMap(String::length);
            Assert.assertEquals(supplier.get().isParallel(), (map instanceof ConcurrentMap));
            Assert.assertEquals(expected, map);
            SortedMap<Integer, String> map2 = supplier.get().toSortedMap(String::length, Function.identity());
            Assert.assertEquals(supplier.get().isParallel(), (map2 instanceof ConcurrentMap));
            Assert.assertEquals(expected2, map2);
        });
        Map<Integer, String> expected3 = new HashMap<>();
        expected3.put(1, "a");
        expected3.put(2, "bbdd");
        expected3.put(3, "ccc");
        TestHelpers.streamEx(() -> Stream.of("a", "bb", "ccc", "dd"), ( supplier) -> {
            SortedMap<Integer, String> seqMap3 = supplier.get().toSortedMap(String::length, Function.identity(), String::concat);
            Assert.assertEquals(supplier.toString(), expected3, seqMap3);
            TestHelpers.checkIllegalStateException(() -> supplier.get().toSortedMap(String::length, Function.identity()), "2", "dd", "bb");
        });
    }

    @Test
    public void testToNavigableMap() {
        Map<String, Integer> expected = new HashMap<>();
        expected.put("a", 1);
        expected.put("bb", 2);
        expected.put("ccc", 3);
        Map<Integer, String> expected2 = new HashMap<>();
        expected2.put(1, "a");
        expected2.put(2, "bb");
        expected2.put(3, "ccc");
        TestHelpers.streamEx(() -> Stream.of("a", "bb", "ccc"), ( supplier) -> {
            NavigableMap<String, Integer> map = supplier.get().toNavigableMap(String::length);
            Assert.assertEquals(supplier.get().isParallel(), (map instanceof ConcurrentMap));
            Assert.assertEquals(expected, map);
            NavigableMap<Integer, String> map2 = supplier.get().toNavigableMap(String::length, Function.identity());
            Assert.assertEquals(supplier.get().isParallel(), (map2 instanceof ConcurrentMap));
            Assert.assertEquals(expected2, map2);
        });
        Map<Integer, String> expected3 = new HashMap<>();
        expected3.put(1, "a");
        expected3.put(2, "bbdd");
        expected3.put(3, "ccc");
        TestHelpers.streamEx(() -> Stream.of("a", "bb", "ccc", "dd"), ( supplier) -> {
            NavigableMap<Integer, String> seqMap3 = supplier.get().toNavigableMap(String::length, Function.identity(), String::concat);
            Assert.assertEquals(supplier.toString(), expected3, seqMap3);
            TestHelpers.checkIllegalStateException(() -> supplier.get().toNavigableMap(String::length, Function.identity()), "2", "dd", "bb");
        });
    }

    @Test
    public void testGroupingBy() {
        Map<Integer, List<String>> expected = new HashMap<>();
        expected.put(1, Arrays.asList("a"));
        expected.put(2, Arrays.asList("bb", "dd"));
        expected.put(3, Arrays.asList("ccc"));
        Map<Integer, Set<String>> expectedMapSet = new HashMap<>();
        expectedMapSet.put(1, new HashSet<>(Arrays.asList("a")));
        expectedMapSet.put(2, new HashSet<>(Arrays.asList("bb", "dd")));
        expectedMapSet.put(3, new HashSet<>(Arrays.asList("ccc")));
        TestHelpers.streamEx(() -> StreamEx.of("a", "bb", "dd", "ccc"), ( supplier) -> {
            Assert.assertEquals(expected, supplier.get().groupingBy(String::length));
            Map<Integer, List<String>> map = supplier.get().groupingTo(String::length, LinkedList::new);
            Assert.assertEquals(expected, map);
            Assert.assertTrue(((map.get(1)) instanceof LinkedList));
            Assert.assertEquals(expectedMapSet, supplier.get().groupingBy(String::length, Collectors.toSet()));
            Assert.assertEquals(expectedMapSet, supplier.get().groupingBy(String::length, HashMap::new, Collectors.toSet()));
            ConcurrentHashMap<Integer, Set<String>> chm = supplier.get().groupingBy(String::length, ConcurrentHashMap::new, Collectors.toSet());
            Assert.assertEquals(expectedMapSet, chm);
            chm = supplier.get().groupingTo(String::length, ConcurrentHashMap::new, TreeSet::new);
            Assert.assertTrue(((chm.get(1)) instanceof TreeSet));
        });
    }

    @Test
    public void testPartitioning() {
        Map<Boolean, List<String>> map = StreamEx.of("a", "bb", "c", "dd").partitioningBy(( s) -> (s.length()) > 1);
        Assert.assertEquals(Arrays.asList("bb", "dd"), map.get(true));
        Assert.assertEquals(Arrays.asList("a", "c"), map.get(false));
        Map<Boolean, Long> counts = StreamEx.of("a", "bb", "c", "dd", "eee").partitioningBy(( s) -> (s.length()) > 1, Collectors.counting());
        Assert.assertEquals(3L, ((long) (counts.get(true))));
        Assert.assertEquals(2L, ((long) (counts.get(false))));
        Map<Boolean, List<String>> mapLinked = StreamEx.of("a", "bb", "c", "dd").partitioningTo(( s) -> (s.length()) > 1, LinkedList::new);
        Assert.assertEquals(Arrays.asList("bb", "dd"), mapLinked.get(true));
        Assert.assertEquals(Arrays.asList("a", "c"), mapLinked.get(false));
        Assert.assertTrue(((mapLinked.get(true)) instanceof LinkedList));
    }

    @Test
    public void testIterable() {
        List<String> result = new ArrayList<>();
        for (String s : StreamEx.of("a", "b", "cc").filter(( s) -> (s.length()) < 2)) {
            result.add(s);
        }
        Assert.assertEquals(Arrays.asList("a", "b"), result);
    }

    @Test
    public void testCreateFromMap() {
        Map<String, Integer> data = new LinkedHashMap<>();
        data.put("aaa", 10);
        data.put("bb", 25);
        data.put("c", 37);
        Assert.assertEquals(Arrays.asList("aaa", "bb", "c"), StreamEx.ofKeys(data).toList());
        Assert.assertEquals(Arrays.asList("aaa"), StreamEx.ofKeys(data, ( x) -> (x % 2) == 0).toList());
        Assert.assertEquals(Arrays.asList(10, 25, 37), StreamEx.ofValues(data).toList());
        Assert.assertEquals(Arrays.asList(10, 25), StreamEx.ofValues(data, ( s) -> (s.length()) > 1).toList());
    }

    @Test
    public void testSelect() {
        Assert.assertEquals(Arrays.asList("a", "b"), StreamEx.of(1, "a", 2, "b", 3, "cc").select(String.class).filter(( s) -> (s.length()) == 1).toList());
        StringBuilder sb = new StringBuilder();
        StringBuffer sbb = new StringBuffer();
        StreamEx.<CharSequence>of("test", sb, sbb).select(Appendable.class).forEach(( a) -> {
            try {
                a.append("b");
            } catch ( e) {
                throw new <e>UncheckedIOException();
            }
        });
        Assert.assertEquals("b", sb.toString());
        Assert.assertEquals("b", sbb.toString());
    }

    @Test
    public void testFlatCollection() {
        Map<Integer, List<String>> data = new LinkedHashMap<>();
        data.put(1, Arrays.asList("a", "b"));
        data.put(2, Arrays.asList("c", "d"));
        data.put(3, null);
        Assert.assertEquals(Arrays.asList("a", "b", "c", "d"), StreamEx.of(data.entrySet()).flatCollection(Map.Entry::getValue).toList());
    }

    @Test
    public void testFlatArray() {
        Map<Integer, String[]> data = new LinkedHashMap<>();
        data.put(1, new String[]{ "a", "b" });
        data.put(2, new String[]{ "c", "d" });
        data.put(3, null);
        Assert.assertEquals(Arrays.asList("a", "b", "c", "d"), StreamEx.of(data.entrySet()).flatArray(Map.Entry::getValue).toList());
    }

    @Test
    public void testMapPartial() {
        Function<Integer, Optional<String>> literalOf = ( num) -> num == 1 ? Optional.of("one") : Optional.empty();
        List<Integer> original = Arrays.asList(1, 2, 3, 4);
        List<String> expected = Arrays.asList("one");
        TestHelpers.streamEx(original::stream, ( s) -> Assert.assertEquals(expected, s.get().mapPartial(literalOf).toList()));
    }

    @Test
    public void testAppend() {
        Assert.assertEquals(Arrays.asList("a", "b", "c", "d", "e"), StreamEx.of("a", "b", "c", "dd").remove(( s) -> (s.length()) > 1).append("d", "e").toList());
        Assert.assertEquals(Arrays.asList("a", "b", "c", "d", "e"), StreamEx.of("a", "b", "c").append(Stream.of("d", "e")).toList());
        Assert.assertEquals(Arrays.asList("a", "b", "c", "d", "e"), StreamEx.of("a", "b", "c").append(Arrays.asList("d", "e")).toList());
        List<Integer> list = Arrays.asList(1, 2, 3, 4);
        Assert.assertEquals(Arrays.asList(1.0, 2, 3L, 1, 2, 3, 4), StreamEx.of(1.0, 2, 3L).append(list).toList());
        StreamEx<Integer> s = StreamEx.of(1, 2, 3);
        Assert.assertSame(s, s.append());
        Assert.assertSame(s, s.append(Collections.emptyList()));
        Assert.assertSame(s, s.append(new ArrayList()));
        Assert.assertSame(s, s.append(Stream.empty()));
        Assert.assertNotSame(s, s.append(new ConcurrentLinkedQueue()));
    }

    @Test
    public void testPrepend() {
        Supplier<Stream<String>> sized = () -> StreamEx.of("a", "b", "c", "dd");
        Supplier<Stream<String>> notSized = () -> StreamEx.of(StreamEx.of("a", "b", "c", "dd").iterator());
        for (Supplier<Stream<String>> supplier : Arrays.asList(sized, notSized)) {
            TestHelpers.streamEx(supplier, ( s) -> {
                Assert.assertEquals(Arrays.asList("d", "e", "a", "b", "c"), s.get().remove(( str) -> (str.length()) > 1).prepend("d", "e").toList());
                Assert.assertEquals(Arrays.asList("d", "e", "a", "b", "c", "dd"), s.get().prepend(Stream.of("d", "e")).toList());
                Assert.assertEquals(Arrays.asList("d", "e", "a", "b", "c", "dd"), s.get().prepend(Arrays.asList("d", "e")).toList());
            });
        }
        Assert.assertArrayEquals(new Object[]{ 1, 2, 3, 1, 1 }, StreamEx.constant(1, ((Long.MAX_VALUE) - 1)).prepend(1, 2, 3).limit(5).toArray());
        Assert.assertEquals(Arrays.asList(4, 3, 2, 1), StreamEx.of(1).prepend(2).prepend(StreamEx.of(3).prepend(4)).toList());
        StreamEx<Integer> s = StreamEx.of(1, 2, 3);
        Assert.assertSame(s, s.prepend());
        Assert.assertSame(s, s.prepend(Collections.emptyList()));
        Assert.assertSame(s, s.prepend(new ArrayList()));
        Assert.assertSame(s, s.prepend(Stream.empty()));
        Assert.assertNotSame(s, s.prepend(new ConcurrentLinkedQueue()));
        Assert.assertTrue(StreamEx.of("a", "b").prepend(Stream.of("c").parallel()).isParallel());
        Assert.assertTrue(StreamEx.of("a", "b").parallel().prepend(Stream.of("c").parallel()).isParallel());
        Assert.assertTrue(StreamEx.of("a", "b").parallel().prepend(Stream.of("c")).isParallel());
        Assert.assertFalse(StreamEx.of("a", "b").prepend(Stream.of("c")).isParallel());
    }

    @Test
    public void testPrependTSO() {
        List<Integer> expected = IntStreamEx.rangeClosed(19999, 0, (-1)).boxed().toList();
        Assert.assertEquals(expected, IntStreamEx.range(20000).mapToObj(StreamEx::of).reduce(StreamEx::prepend).get().toList());
        Assert.assertEquals(expected, IntStreamEx.range(20000).parallel().mapToObj(StreamEx::of).reduce(StreamEx::prepend).get().toList());
    }

    @Test
    public void testNonNull() {
        List<String> data = Arrays.asList("a", null, "b");
        Assert.assertEquals(Arrays.asList("a", null, "b"), StreamEx.of(data).toList());
        Assert.assertEquals(Arrays.asList("a", "b"), StreamEx.of(data).nonNull().toList());
    }

    @Test
    public void testSorting() {
        Assert.assertEquals(Arrays.asList("a", "b", "c", "d"), StreamEx.of("b", "c", "a", "d").sorted().toList());
        Assert.assertEquals(Arrays.asList("d", "c", "b", "a"), StreamEx.of("b", "c", "a", "d").reverseSorted().toList());
        List<String> data = Arrays.asList("a", "bbb", "cc");
        Assert.assertEquals(Arrays.asList("a", "cc", "bbb"), StreamEx.of(data).sorted(Comparator.comparingInt(String::length)).toList());
        Assert.assertEquals(Arrays.asList("bbb", "cc", "a"), StreamEx.of(data).reverseSorted(Comparator.comparingInt(String::length)).toList());
        Assert.assertEquals(Arrays.asList("a", "cc", "bbb"), StreamEx.of(data).sortedByInt(String::length).toList());
        Assert.assertEquals(Arrays.asList("a", "cc", "bbb"), StreamEx.of(data).sortedByLong(String::length).toList());
        Assert.assertEquals(Arrays.asList("a", "cc", "bbb"), StreamEx.of(data).sortedByDouble(String::length).toList());
        Assert.assertEquals(Arrays.asList("a", "cc", "bbb"), StreamEx.of(data).sortedBy(String::length).toList());
    }

    @Test
    public void testMinMax() {
        TestHelpers.withRandom(( random) -> {
            List<String> data = IntStreamEx.of(random, 1000, 1, 100).mapToObj(( len) -> IntStreamEx.constant(((random.nextInt((('z' - 'a') + 1))) + 'a'), len).charsToString()).toList();
            String minStr = Collections.min(data, Comparator.comparingInt(String::length));
            String maxStr = Collections.max(data, Comparator.comparingInt(String::length));
            TestHelpers.streamEx(data::stream, ( supplier) -> {
                Assert.assertEquals(supplier.toString(), maxStr, supplier.get().max(Comparator.comparingInt(String::length)).get());
                Assert.assertEquals(supplier.toString(), maxStr, supplier.get().maxByInt(String::length).get());
                Assert.assertEquals(supplier.toString(), maxStr, supplier.get().maxByLong(String::length).get());
                Assert.assertEquals(supplier.toString(), maxStr, supplier.get().maxByDouble(String::length).get());
                Assert.assertEquals(supplier.toString(), maxStr, supplier.get().maxBy(String::length).get());
                Assert.assertEquals(supplier.toString(), maxStr, supplier.get().min(Comparator.comparingInt(String::length).reversed()).get());
                Assert.assertEquals(supplier.toString(), minStr, supplier.get().minByInt(String::length).get());
                Assert.assertEquals(supplier.toString(), minStr, supplier.get().minByLong(String::length).get());
                Assert.assertEquals(supplier.toString(), minStr, supplier.get().minByDouble(String::length).get());
                Assert.assertEquals(supplier.toString(), minStr, supplier.get().minBy(String::length).get());
            });
            Assert.assertFalse(StreamEx.<String>empty().minByInt(String::length).isPresent());
        });
    }

    @Test
    public void testFind() {
        Assert.assertEquals("bb", StreamEx.of("a", "bb", "c").findFirst(( s) -> (s.length()) == 2).get());
        Assert.assertFalse(StreamEx.of("a", "bb", "c").findFirst(( s) -> (s.length()) == 3).isPresent());
    }

    @Test
    public void testHas() {
        Assert.assertTrue(StreamEx.of("a", "bb", "c").has("bb"));
        Assert.assertFalse(StreamEx.of("a", "bb", "c").has("cc"));
        Assert.assertFalse(StreamEx.of("a", "bb", "c").has(null));
        Assert.assertTrue(StreamEx.of("a", "bb", null, "c").has(null));
    }

    @Test
    public void testWithout() {
        Assert.assertEquals(Arrays.asList("a", "bb", null), StreamEx.of("a", "bb", null, "c").without("c").toList());
        String s = null;
        Assert.assertEquals(Arrays.asList("a", "bb", "c"), StreamEx.of("a", "bb", null, "c", null).without(s).toList());
        Assert.assertTrue(StreamEx.of("bb", "bb", "bb").without("bb").toList().isEmpty());
        Assert.assertEquals(Arrays.asList("bb", "bb", "bb"), StreamEx.of("bb", "bb", "bb").without(s).toList());
        StreamEx<String> stream = StreamEx.of("a", "b", "c");
        Assert.assertSame(stream, stream.without());
        Assert.assertEquals(Arrays.asList("a", "b", "c"), StreamEx.of("a", "b", null, "c").without(new String[]{ null }).toList());
        Assert.assertEquals(Arrays.asList(), StreamEx.of("a", "b", null, "c").without("c", null, "b", "a").toList());
    }

    @Test
    public void testJoining() {
        Assert.assertEquals("abc", StreamEx.of("a", "b", "c").joining());
        Assert.assertEquals("a,b,c", StreamEx.of("a", "b", "c").joining(","));
        Assert.assertEquals("[1;2;3]", StreamEx.of(1, 2, 3).joining(";", "[", "]"));
        TestHelpers.withRandom(( r) -> {
            List<Integer> input1 = IntStreamEx.of(r, 1000, 0, 1000).boxed().toList();
            List<String> input2 = IntStreamEx.of(r, 1000, 0, 1000).mapToObj(String::valueOf).toList();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < (input1.size()); i++) {
                if ((sb.length()) > 0)
                    sb.append(',');

                sb.append(input1.get(i)).append(':').append(input2.get(i));
            }
            String expected = sb.toString();
            Assert.assertEquals(expected, StreamEx.zip(input1, input2, ( i, s) -> (i + ":") + s).joining(","));
            Assert.assertEquals(expected, StreamEx.zip(input1, input2, ( i, s) -> (i + ":") + s).parallel().joining(","));
        });
    }

    @Test
    public void testFoldLeft() {
        List<String> input = Arrays.asList("a", "bb", "ccc");
        TestHelpers.streamEx(input::stream, ( supplier) -> {
            Assert.assertEquals("ccc;bb;a;", supplier.get().foldLeft("", ( u, v) -> (v + ";") + u));
            // Removing types here causes internal error in Javac compiler
            // java.lang.AssertionError: attribution shouldn't be happening here
            // Bug appears in javac 1.8.0.20 and javac 1.8.0.45
            // javac 1.9.0b55 and ecj compiles normally
            // Probably this ticket:
            // https://bugs.openjdk.java.net/browse/JDK-8068399
            Assert.assertTrue(supplier.get().foldLeft(false, (Boolean acc,String s) -> acc || (s.equals("bb"))));
            Assert.assertFalse(supplier.get().foldLeft(false, (Boolean acc,String s) -> acc || (s.equals("d"))));
            Assert.assertEquals(6, ((int) (supplier.get().foldLeft(0, ( acc, v) -> acc + (v.length())))));
            Assert.assertEquals("{ccc={bb={a={}}}}", supplier.get().foldLeft(Collections.emptyMap(), (Map<String, Object> acc,String v) -> Collections.singletonMap(v, acc)).toString());
        });
    }

    @Test
    public void testFoldLeftOptional() {
        // non-associative
        BinaryOperator<Integer> accumulator = ( x, y) -> (x + y) * (x + y);
        TestHelpers.streamEx(() -> StreamEx.constant(3, 4), ( supplier) -> Assert.assertEquals(2322576, ((int) (supplier.get().foldLeft(accumulator).orElse((-1))))));
        TestHelpers.streamEx(() -> StreamEx.of(1, 2, 3), ( supplier) -> Assert.assertEquals(144, ((int) (supplier.get().foldLeft(accumulator).orElse((-1))))));
        TestHelpers.emptyStreamEx(Integer.class, ( supplier) -> Assert.assertFalse(supplier.get().foldLeft(accumulator).isPresent()));
    }

    @Test
    public void testFoldRight() {
        Assert.assertEquals(";c;b;a", StreamEx.of("a", "b", "c").parallel().foldRight("", ( u, v) -> (v + ";") + u));
        Assert.assertEquals("{a={bb={ccc={}}}}", StreamEx.of("a", "bb", "ccc").foldRight(Collections.emptyMap(), ((BiFunction<String, Map<String, Object>, Map<String, Object>>) (Collections::singletonMap))).toString());
        Assert.assertEquals("{a={bb={ccc={}}}}", StreamEx.of("a", "bb", "ccc").parallel().foldRight(Collections.emptyMap(), ((BiFunction<String, Map<String, Object>, Map<String, Object>>) (Collections::singletonMap))).toString());
    }

    @Test
    public void testFoldRightOptional() {
        // non-associative
        BinaryOperator<Integer> accumulator = ( x, y) -> (x + y) * (x + y);
        TestHelpers.streamEx(() -> StreamEx.constant(3, 4), ( supplier) -> Assert.assertEquals(2322576, ((int) (supplier.get().foldRight(accumulator).orElse((-1))))));
        TestHelpers.streamEx(() -> StreamEx.of(1, 2, 3, 0), ( supplier) -> Assert.assertEquals(14884, ((int) (supplier.get().foldRight(accumulator).orElse((-1))))));
        TestHelpers.emptyStreamEx(Integer.class, ( supplier) -> Assert.assertFalse(supplier.get().foldRight(accumulator).isPresent()));
    }

    @Test
    public void testDistinctAtLeast() {
        Assert.assertEquals(0, StreamEx.of("a", "b", "c").distinct(2).count());
        Assert.assertEquals(StreamEx.of("a", "b", "c").distinct().toList(), StreamEx.of("a", "b", "c").distinct(1).toList());
        Assert.assertEquals(Arrays.asList("b"), StreamEx.of("a", "b", "c", "b", null).distinct(2).toList());
        Assert.assertEquals(Arrays.asList("b", null), StreamEx.of("a", "b", null, "c", "b", null).distinct(2).toList());
        Assert.assertEquals(Arrays.asList(null, "b"), StreamEx.of("a", "b", null, "c", null, "b", null, "b").distinct(2).toList());
        TestHelpers.streamEx(() -> IntStreamEx.range(0, 1000).map(( x) -> x / 3).boxed(), ( supplier) -> {
            Assert.assertEquals(334, supplier.get().distinct().count());
            Assert.assertEquals(333, supplier.get().distinct(2).count());
            Assert.assertEquals(333, supplier.get().distinct(3).count());
            Assert.assertEquals(0, supplier.get().distinct(4).count());
            List<Integer> distinct3List = supplier.get().distinct(3).toList();
            Assert.assertEquals(333, distinct3List.size());
            Map<Integer, Long> map = supplier.get().collect(Collectors.groupingBy(Function.identity(), LinkedHashMap::new, Collectors.counting()));
            List<Integer> expectedList = StreamEx.ofKeys(map, ( val) -> val >= 3).toList();
            Assert.assertEquals(333, expectedList.size());
            Assert.assertEquals(distinct3List, expectedList);
        });
        Assert.assertEquals(0, StreamEx.of("a", "b", "c").parallel().distinct(2).count());
        Assert.assertEquals(StreamEx.of("a", "b", "c").parallel().distinct().toList(), StreamEx.of("a", "b", "c").parallel().distinct(1).toList());
        Assert.assertEquals(Arrays.asList("b"), StreamEx.of("a", "b", "c", "b").parallel().distinct(2).toList());
        Assert.assertEquals(new HashSet(Arrays.asList("b", null)), StreamEx.of("a", "b", null, "c", "b", null).parallel().distinct(2).toSet());
        Assert.assertEquals(new HashSet(Arrays.asList(null, "b")), StreamEx.of("a", "b", null, "c", null, "b", null, "b").parallel().distinct(2).toSet());
        for (int i = 1; i < 1000; i += 100) {
            List<Integer> input = IntStreamEx.of(new Random(1), i, 1, 100).boxed().toList();
            for (int n : IntStreamEx.range(2, 10).boxed()) {
                Set<Integer> expected = input.stream().collect(Collectors.collectingAndThen(Collectors.groupingBy(Function.identity(), Collectors.counting()), ( m) -> {
                    m.values().removeIf(( l) -> l < n);
                    return m.keySet();
                }));
                Assert.assertEquals(expected, StreamEx.of(input).distinct(n).toSet());
                Assert.assertEquals(expected, StreamEx.of(input).parallel().distinct(n).toSet());
                Assert.assertEquals(0, StreamEx.of(expected).distinct(2).toSet().size());
            }
        }
        Assert.assertEquals(IntStreamEx.range(10).boxed().toList(), IntStreamEx.range(100).mapToObj(( x) -> x / 10).sorted().distinct(3).sorted().toList());
        // Saving actual to separate variable helps to work-around
        // javac <8u40 issue JDK-8056984
        List<String> actual = StreamEx.split("a,b,a,c,d,b,a", ",").parallel().distinct(2).sorted().toList();
        Assert.assertEquals(Arrays.asList("a", "b"), actual);
    }

    @Test
    public void testDistinctAtLeastPairMap() {
        int last = -1;
        int cur = -1;
        int count = 0;
        List<Integer> expected = new ArrayList<>();
        for (int i : IntStreamEx.of(new Random(1), 1000, 0, 100).sorted().boxed()) {
            if (i == cur) {
                count++;
                if (count == 15) {
                    if (last >= 0) {
                        expected.add((cur - last));
                    }
                    last = cur;
                }
            } else {
                count = 1;
                cur = i;
            }
        }
        TestHelpers.streamEx(() -> new Random(1).ints(1000, 0, 100).sorted().boxed(), ( supplier) -> Assert.assertEquals(expected, supplier.get().distinct(15).pairMap(( a, b) -> b - a).toList()));
    }

    static class Point {
        final double x;

        final double y;

        Point(double x, double y) {
            this.x = x;
            this.y = y;
        }

        double distance(StreamExTest.Point o) {
            return Math.sqrt(((((x) - (o.x)) * ((x) - (o.x))) + (((y) - (o.y)) * ((y) - (o.y)))));
        }
    }

    @Test
    public void testPairMap() {
        Assert.assertEquals(0, StreamEx.<String>empty().pairMap(String::concat).count());
        Assert.assertArrayEquals(new Object[0], StreamEx.<String>empty().pairMap(String::concat).toArray());
        Assert.assertEquals(0, StreamEx.of("a").pairMap(String::concat).count());
        Assert.assertEquals(Arrays.asList("aa", "aa", "aa"), StreamEx.generate(() -> "a").pairMap(String::concat).limit(3).toList());
        AtomicBoolean flag = new AtomicBoolean();
        Assert.assertFalse(flag.get());
        StreamEx<String> stream = StreamEx.of("a", "b").onClose(() -> flag.set(true)).pairMap(String::concat);
        stream.close();
        Assert.assertTrue(flag.get());
        Assert.assertEquals(Collections.singletonMap(1, 1999L), IntStreamEx.range(2000).boxed().pairMap(( a, b) -> b - a).groupingBy(Function.identity(), Collectors.counting()));
        Assert.assertEquals(Collections.singletonMap(1, 1999L), IntStreamEx.range(2000).parallel().boxed().pairMap(( a, b) -> b - a).groupingBy(Function.identity(), Collectors.counting()));
        TestHelpers.withRandom(( r) -> {
            Integer[] data = r.ints(1000, 1, 1000).boxed().toArray(Integer[]::new);
            Double[] expected = new Double[(data.length) - 1];
            for (int i = 0; i < (expected.length); i++)
                expected[i] = ((data[(i + 1)]) - (data[i])) * 1.23;

            Double[] result = StreamEx.of(data).parallel().pairMap(( a, b) -> (b - a) * 1.23).toArray(Double[]::new);
            Assert.assertArrayEquals(expected, result);
            result = StreamEx.of(data).pairMap(( a, b) -> (b - a) * 1.23).toArray(Double[]::new);
            Assert.assertArrayEquals(expected, result);
        });
        // Find all numbers where the integer preceded a larger value.
        Collection<Integer> numbers = Arrays.asList(10, 1, 15, 30, 2, 6);
        List<Integer> res = StreamEx.of(numbers).pairMap(( a, b) -> a < b ? a : null).nonNull().toList();
        Assert.assertEquals(Arrays.asList(1, 15, 2), res);
        // Check whether stream is sorted
        Assert.assertTrue(StreamExTest.isSorted(Arrays.asList("a", "bb", "bb", "c")));
        Assert.assertFalse(StreamExTest.isSorted(Arrays.asList("a", "bb", "bb", "bba", "bb", "c")));
        TestHelpers.withRandom(( r) -> Assert.assertTrue(StreamExTest.isSorted(IntStreamEx.of(r).boxed().distinct().limit(1000).toCollection(TreeSet::new))));
        // Find first element which violates the sorting
        Assert.assertEquals("bba", StreamExTest.firstMisplaced(Arrays.asList("a", "bb", "bb", "bba", "bb", "c")).get());
        Assert.assertFalse(StreamExTest.firstMisplaced(Arrays.asList("a", "bb", "bb", "bb", "c")).isPresent());
        Assert.assertFalse(StreamExTest.firstMisplaced(Arrays.<String>asList()).isPresent());
        Assert.assertFalse(IntStreamEx.range(1000).greater(2000).boxed().parallel().pairMap(( a, b) -> a).findFirst().isPresent());
    }

    @Test
    public void testPairMapCornerCase() {
        TestHelpers.repeat(100, ( iter) -> TestHelpers.streamEx(() -> IntStreamEx.range(1000).filter(( x) -> (x == 0) || (x == 999)).boxed(), ( supplier) -> Assert.assertEquals(Collections.singletonList((-999)), supplier.get().pairMap(( a, b) -> a - b).toList())));
    }

    @Test
    public void testPairMapFlatMapBug() {
        Integer[][] input = new Integer[][]{ new Integer[]{ 1 }, new Integer[]{ 2, 3 }, new Integer[]{ 4, 5, 6 }, new Integer[]{ 7, 8 }, new Integer[]{ 9 } };
        TestHelpers.streamEx(() -> StreamEx.of(input).flatMap(Arrays::stream), ( supplier) -> Assert.assertEquals(1L, supplier.get().pairMap(( a, b) -> b - a).distinct().count()));
    }

    @Test
    public void testPairMapInterpolation() {
        StreamExTest.Point[] points = IntStreamEx.range(1000).mapToObj(( i) -> new one.util.streamex.Point(i, ((i % 2) == 0 ? 1 : 0))).toArray(StreamExTest.Point[]::new);
        Assert.assertEquals(1, StreamExTest.interpolate(points, 10), 0.0);
        Assert.assertEquals(0, StreamExTest.interpolate(points, 999), 0.0);
        Assert.assertTrue(Double.isNaN(StreamExTest.interpolate(points, (-10))));
        Assert.assertEquals(0.4, StreamExTest.interpolate(points, 100.6), 1.0E-6);
    }

    @Test
    public void testScanLeftPairMap() {
        TestHelpers.withRandom(( r) -> {
            int[] random = IntStreamEx.of(r, 1000).toArray();
            List<Integer> scanLeft = IntStreamEx.of(random).boxed().parallel().scanLeft(0, Integer::sum);
            Assert.assertArrayEquals(random, IntStreamEx.of(scanLeft).parallel().pairMap(( a, b) -> b - a).toArray());
        });
    }

    @Test
    public void testPairMapCapitalization() {
        Assert.assertEquals("Test Capitalization Stream", IntStreamEx.ofChars("test caPiTaliZation streaM").parallel().prepend(0).mapToObj(( c) -> ((char) (c))).pairMap(( c1, c2) -> (!(Character.isLetter(c1))) && (Character.isLetter(c2)) ? Character.toTitleCase(c2) : Character.toLowerCase(c2)).joining());
    }

    @Test
    public void testPairMapAddHeaders() {
        List<String> result = StreamEx.of("aaa", "abc", "bar", "foo", "baz", "argh").sorted().prepend("").pairMap(( a, b) -> (a.isEmpty()) || ((a.charAt(0)) != (b.charAt(0))) ? Stream.of((("=== " + (b.substring(0, 1).toUpperCase())) + " ==="), b) : Stream.of(b)).flatMap(Function.identity()).toList();
        List<String> expected = Arrays.asList("=== A ===", "aaa", "abc", "argh", "=== B ===", "bar", "baz", "=== F ===", "foo");
        Assert.assertEquals(expected, result);
    }

    static class Node {
        StreamExTest.Node parent;

        final String name;

        public Node(String name) {
            this.name = name;
        }

        public void link(StreamExTest.Node parent) {
            this.parent = parent;
        }

        @Override
        public String toString() {
            return (parent) == null ? name : ((parent) + ":") + (name);
        }
    }

    @Test
    public void testForPairs() {
        List<StreamExTest.Node> nodes = StreamEx.of("one", "two", "three", "four").map(StreamExTest.Node::new).toList();
        StreamEx.of(nodes).forPairs(StreamExTest.Node::link);
        Assert.assertEquals("four:three:two:one", nodes.get(0).toString());
        nodes = StreamEx.of("one", "two", "three", "four").map(StreamExTest.Node::new).toList();
        StreamEx.of(nodes).parallel().forPairs(StreamExTest.Node::link);
        Assert.assertEquals("four:three:two:one", nodes.get(0).toString());
    }

    @Test
    public void testScanLeft() {
        TestHelpers.streamEx(() -> IntStreamEx.rangeClosed(1, 4).boxed(), ( supplier) -> {
            Assert.assertEquals(Arrays.asList(0, 1, 3, 6, 10), supplier.get().scanLeft(0, Integer::sum));
            Assert.assertEquals(Arrays.asList(1, 3, 6, 10), supplier.get().scanLeft(Integer::sum));
        });
        TestHelpers.emptyStreamEx(Integer.class, ( supplier) -> {
            Assert.assertTrue(supplier.get().scanLeft(Integer::sum).isEmpty());
            Assert.assertEquals(Arrays.asList(0), supplier.get().scanLeft(0, Integer::sum));
        });
        Assert.assertEquals(167167000, IntStreamEx.rangeClosed(1, 1000).boxed().parallel().scanLeft(0, Integer::sum).stream().mapToLong(( x) -> x).sum());
    }

    @Test
    public void testScanRight() {
        TestHelpers.streamEx(() -> IntStreamEx.rangeClosed(1, 4).boxed(), ( supplier) -> {
            Assert.assertEquals(Arrays.asList(10, 9, 7, 4, 0), supplier.get().scanRight(0, Integer::sum));
            Assert.assertEquals(Arrays.asList(10, 9, 7, 4), supplier.get().scanRight(Integer::sum));
        });
        TestHelpers.emptyStreamEx(Integer.class, ( supplier) -> {
            Assert.assertTrue(supplier.get().scanRight(Integer::sum).isEmpty());
            Assert.assertEquals(Arrays.asList(0), supplier.get().scanRight(0, Integer::sum));
        });
        Assert.assertEquals(333833500, IntStreamEx.rangeClosed(1, 1000).boxed().parallel().scanRight(0, Integer::sum).stream().mapToLong(( x) -> x).sum());
    }

    @Test
    public void testPermutations() {
        Assert.assertEquals("[]", StreamEx.ofPermutations(0).map(Arrays::toString).joining(";"));
        Assert.assertEquals("[0, 1, 2];[0, 2, 1];[1, 0, 2];[1, 2, 0];[2, 0, 1];[2, 1, 0]", StreamEx.ofPermutations(3).map(Arrays::toString).joining(";"));
        Assert.assertEquals(720, StreamEx.ofPermutations(7).parallel().filter(( i) -> (i[3]) == 5).count());
    }

    static class TreeNode {
        final String title;

        public TreeNode(String title) {
            this.title = title;
        }

        @Override
        public String toString() {
            return title;
        }

        public StreamEx<StreamExTest.TreeNode> flatStream() {
            return StreamEx.ofTree(this, StreamExTest.CompositeNode.class, StreamExTest.CompositeNode::elements);
        }
    }

    static class CompositeNode extends StreamExTest.TreeNode {
        final List<StreamExTest.TreeNode> nodes = new ArrayList<>();

        public CompositeNode(String title) {
            super(title);
        }

        public StreamExTest.CompositeNode add(StreamExTest.TreeNode node) {
            nodes.add(node);
            return this;
        }

        public Stream<StreamExTest.TreeNode> elements() {
            return nodes.stream();
        }

        static StreamExTest.CompositeNode createTestData() {
            StreamExTest.CompositeNode r = new StreamExTest.CompositeNode("root");
            r.add(new StreamExTest.CompositeNode("childA").add(new StreamExTest.TreeNode("grandA1")).add(new StreamExTest.TreeNode("grandA2")));
            r.add(new StreamExTest.CompositeNode("childB").add(new StreamExTest.TreeNode("grandB1")));
            r.add(new StreamExTest.TreeNode("childC"));
            return r;
        }
    }

    @Test
    public void testOfTree() {
        String inputSimple = "bbb";
        List<Object> input = Arrays.asList("aa", null, Arrays.asList(Arrays.asList("bbbb", "cc", null, Arrays.asList()), "ddd", Arrays.asList("e"), Arrays.asList("fff")), "ggg");
        @SuppressWarnings("unchecked")
        Function<Object, Stream<Object>> generator = ( o) -> o instanceof List ? ((List<Object>) (o)).stream() : null;
        Assert.assertEquals("bbb", StreamEx.ofTree(inputSimple, generator).select(String.class).joining(","));
        StreamEx<Object> ofTree = StreamEx.ofTree(input, generator);
        Assert.assertEquals("aa,bbbb,cc,ddd,e,fff,ggg", ofTree.select(String.class).joining(","));
        Assert.assertEquals(14, StreamEx.ofTree(input, generator).select(List.class).mapToInt(List::size).sum());
        StreamExTest.CompositeNode r = StreamExTest.CompositeNode.createTestData();
        TestHelpers.streamEx(r::flatStream, ( s) -> {
            Assert.assertEquals("root,childA,grandA1,grandA2,childB,grandB1,childC", s.get().joining(","));
            Assert.assertEquals(Optional.of("grandB1"), s.get().findFirst(( tn) -> tn.title.contains("B1")).map(( tn) -> tn.title));
            Assert.assertEquals(Optional.empty(), s.get().findFirst(( tn) -> tn.title.contains("C1")).map(( tn) -> tn.title));
        });
        TestHelpers.streamEx(() -> StreamEx.ofTree("", (String str) -> (str.length()) >= 3 ? null : Stream.of("a", "b").map(str::concat)), ( supplier) -> {
            Assert.assertEquals(Arrays.asList("", "a", "aa", "aaa", "aab", "ab", "aba", "abb", "b", "ba", "baa", "bab", "bb", "bba", "bbb"), supplier.get().toList());
            Assert.assertEquals(Arrays.asList("a", "b", "aa", "ab", "ba", "bb", "aaa", "aab", "aba", "abb", "baa", "bab", "bba", "bbb"), supplier.get().sortedByInt(String::length).without("").toList());
        });
        Assert.assertEquals(1000001, StreamEx.ofTree("x", ( s) -> s.equals("x") ? IntStreamEx.range(1000000).mapToObj(String::valueOf) : null).parallel().count());
    }

    @Test
    public void testOfTreeClose() {
        StreamExTest.CompositeNode r = StreamExTest.CompositeNode.createTestData();
        r.flatStream().close();// should not fail

        List<Consumer<StreamEx<StreamExTest.TreeNode>>> tests = Arrays.asList(( stream) -> Assert.assertEquals(Optional.empty(), stream.findFirst(( tn) -> tn.title.contains("abc"))), ( stream) -> Assert.assertEquals(Optional.of("grandB1"), stream.findFirst(( tn) -> tn.title.contains("B1")).map(( tn) -> tn.title)), ( stream) -> Assert.assertEquals(7, stream.count()));
        for (Consumer<StreamEx<StreamExTest.TreeNode>> test : tests) {
            Set<String> set = new HashSet<>();
            try (StreamEx<StreamExTest.TreeNode> closableTree = StreamEx.ofTree(r, StreamExTest.CompositeNode.class, ( cn) -> cn.elements().onClose(() -> set.add(cn.title)))) {
                test.accept(closableTree);
            }
            Assert.assertEquals(set, StreamEx.of("root", "childA", "childB").toSet());
            boolean catched = false;
            try (StreamEx<StreamExTest.TreeNode> closableTree = StreamEx.ofTree(r, StreamExTest.CompositeNode.class, ( cn) -> cn.elements().onClose(() -> {
                if (!(cn.title.equals("childA")))
                    throw new IllegalArgumentException(cn.title);

            }))) {
                test.accept(closableTree);
            } catch (IllegalArgumentException ex) {
                catched = true;
                Assert.assertEquals("childB", ex.getMessage());
                Assert.assertEquals(1, ex.getSuppressed().length);
                Assert.assertTrue(((ex.getSuppressed()[0]) instanceof IllegalArgumentException));
                Assert.assertEquals("root", ex.getSuppressed()[0].getMessage());
            }
            Assert.assertTrue(catched);
            catched = false;
            try (StreamEx<StreamExTest.TreeNode> closableTree = StreamEx.ofTree(r, StreamExTest.CompositeNode.class, ( cn) -> cn.elements().onClose(() -> {
                if (!(cn.title.equals("childA")))
                    throw new InternalError(cn.title);

            }))) {
                test.accept(closableTree);
            } catch (InternalError ex) {
                catched = true;
                Assert.assertEquals("childB", ex.getMessage());
                Assert.assertEquals(1, ex.getSuppressed().length);
                Assert.assertTrue(((ex.getSuppressed()[0]) instanceof InternalError));
                Assert.assertEquals("root", ex.getSuppressed()[0].getMessage());
            }
            Assert.assertTrue(catched);
            catched = false;
            try (StreamEx<StreamExTest.TreeNode> closableTree = StreamEx.ofTree(r, StreamExTest.CompositeNode.class, ( cn) -> cn.elements().onClose(() -> {
                if (!(cn.title.equals("childA")))
                    throw new InternalError(cn.title);

            }))) {
                test.accept(closableTree.parallel());
            } catch (InternalError ex) {
                catched = true;
                Assert.assertEquals(1, ex.getSuppressed().length);
                Assert.assertTrue(((ex.getSuppressed()[0]) instanceof InternalError));
                Set<String> msgSet = StreamEx.of(ex.getMessage(), ex.getSuppressed()[0].getMessage()).toSet();
                Assert.assertTrue(msgSet.contains("root"));
                Assert.assertTrue(((msgSet.contains("childB")) || (msgSet.contains("java.lang.InternalError: childB"))));
            }
            Assert.assertTrue(catched);
        }
    }

    @Test
    public void testCross() {
        Assert.assertEquals("a-1, a-2, a-3, b-1, b-2, b-3, c-1, c-2, c-3", StreamEx.of("a", "b", "c").cross(1, 2, 3).join("-").joining(", "));
        Assert.assertEquals("a-1, b-1, c-1", StreamEx.of("a", "b", "c").cross(1).join("-").joining(", "));
        Assert.assertEquals("", StreamEx.of("a", "b", "c").cross().join("-").joining(", "));
        List<String> inputs = Arrays.asList("i", "j", "k");
        List<String> outputs = Arrays.asList("x", "y", "z");
        Assert.assertEquals("i->x, i->y, i->z, j->x, j->y, j->z, k->x, k->y, k->z", StreamEx.of(inputs).cross(outputs).mapKeyValue(( input, output) -> (input + "->") + output).joining(", "));
        Assert.assertEquals("", StreamEx.of(inputs).cross(Collections.emptyList()).join("->").joining(", "));
        Assert.assertEquals("i-i, j-j, k-k", StreamEx.of(inputs).cross(Stream::of).join("-").joining(", "));
        Assert.assertEquals("j-j, k-k", StreamEx.of(inputs).cross(( x) -> x.equals("i") ? null : Stream.of(x)).join("-").joining(", "));
    }

    @Test
    public void testCollapse() {
        TestHelpers.streamEx(() -> StreamEx.constant(1, 1000), ( supplier) -> Assert.assertEquals(Collections.singletonList(1), supplier.get().collapse(Objects::equals).toList()));
    }

    @Test
    public void testCollapseEmptyLines() {
        TestHelpers.withRandom(( r) -> TestHelpers.repeat(100, ( i) -> {
            List<String> input = IntStreamEx.range(r.nextInt((i + 1))).mapToObj(( n) -> r.nextBoolean() ? "" : String.valueOf(n)).toList();
            List<String> resultSpliterator = StreamEx.of(input).collapse(( str1, str2) -> (str1.isEmpty()) && (str2.isEmpty())).toList();
            List<String> resultSpliteratorParallel = StreamEx.of(input).parallel().collapse(( str1, str2) -> (str1.isEmpty()) && (str2.isEmpty())).toList();
            List<String> expected = new ArrayList<>();
            boolean lastSpace = false;
            for (String str : input) {
                if (str.isEmpty()) {
                    if (!lastSpace) {
                        expected.add(str);
                    }
                    lastSpace = true;
                } else {
                    expected.add(str);
                    lastSpace = false;
                }
            }
            Assert.assertEquals(expected, resultSpliterator);
            Assert.assertEquals(expected, resultSpliteratorParallel);
        }));
    }

    static class Interval {
        final int from;

        final int to;

        public Interval(int from) {
            this(from, from);
        }

        public Interval(int from, int to) {
            this.from = from;
            this.to = to;
        }

        public StreamExTest.Interval merge(StreamExTest.Interval other) {
            return new StreamExTest.Interval(this.from, other.to);
        }

        public boolean adjacent(StreamExTest.Interval other) {
            return (other.from) == ((this.to) + 1);
        }

        @Override
        public String toString() {
            return (from) == (to) ? ("{" + (from)) + "}" : ((("[" + (from)) + "..") + (to)) + "]";
        }
    }

    @Test
    public void testCollapseIntervals() {
        TestHelpers.withRandom(( r) -> TestHelpers.repeat(100, ( i) -> {
            int size = r.nextInt(((i * 5) + 1));
            int[] input = IntStreamEx.of(r, size, 0, (((size * 3) / 2) + 2)).toArray();
            String result = IntStreamEx.of(input).sorted().boxed().distinct().map(StreamExTest.Interval::new).collapse(StreamExTest.Interval::adjacent, StreamExTest.Interval::merge).joining(" & ");
            String resultIntervalMap = IntStreamEx.of(input).sorted().boxed().distinct().intervalMap(( a, b) -> (b - a) == 1, StreamExTest.Interval::new).joining(" & ");
            String resultIntervalMapParallel = IntStreamEx.of(input).sorted().boxed().distinct().intervalMap(( a, b) -> (b - a) == 1, StreamExTest.Interval::new).parallel().joining(" & ");
            String resultParallel = IntStreamEx.of(input).parallel().sorted().boxed().distinct().map(StreamExTest.Interval::new).collapse(StreamExTest.Interval::adjacent, StreamExTest.Interval::merge).joining(" & ");
            String resultParallel2 = IntStreamEx.of(input).sorted().boxed().distinct().map(StreamExTest.Interval::new).collapse(StreamExTest.Interval::adjacent, StreamExTest.Interval::merge).parallel().joining(" & ");
            String resultCollector = IntStreamEx.of(input).sorted().boxed().distinct().map(StreamExTest.Interval::new).collapse(StreamExTest.Interval::adjacent, Collectors.reducing(StreamExTest.Interval::merge)).map(Optional::get).parallel().joining(" & ");
            int[] sorted = Arrays.copyOf(input, input.length);
            Arrays.sort(sorted);
            List<String> expected = new ArrayList<>();
            StreamExTest.Interval last = null;
            for (int num : sorted) {
                if (last != null) {
                    if ((last.to) == num)
                        continue;

                    if ((last.to) == (num - 1)) {
                        last = new StreamExTest.Interval(last.from, num);
                        continue;
                    }
                    expected.add(last.toString());
                }
                last = new StreamExTest.Interval(num);
            }
            if (last != null)
                expected.add(last.toString());

            String expectedStr = String.join(" & ", expected);
            Assert.assertEquals(expectedStr, result);
            Assert.assertEquals(expectedStr, resultParallel);
            Assert.assertEquals(expectedStr, resultParallel2);
            Assert.assertEquals(expectedStr, resultIntervalMap);
            Assert.assertEquals(expectedStr, resultIntervalMapParallel);
            Assert.assertEquals(expectedStr, resultCollector);
        }));
    }

    @Test
    public void testCollapseDistinct() {
        TestHelpers.withRandom(( r) -> TestHelpers.repeat(100, ( i) -> {
            int size = r.nextInt(((i * 5) - 4));
            List<Integer> input = IntStreamEx.of(r, size, 0, (((size * 3) / 2) + 2)).boxed().sorted().toList();
            List<Integer> distinct = StreamEx.of(input).collapse(Integer::equals).toList();
            List<Integer> distinctCollector = StreamEx.of(input).collapse(Integer::equals, MoreCollectors.first()).map(Optional::get).toList();
            List<Integer> distinctParallel = StreamEx.of(input).parallel().collapse(Integer::equals).toList();
            List<Integer> distinctCollectorParallel = StreamEx.of(input).parallel().collapse(Integer::equals, MoreCollectors.first()).map(Optional::get).toList();
            List<Integer> expected = input.stream().distinct().collect(Collectors.toList());
            Assert.assertEquals(expected, distinct);
            Assert.assertEquals(expected, distinctParallel);
            Assert.assertEquals(expected, distinctCollector);
            Assert.assertEquals(expected, distinctCollectorParallel);
        }));
    }

    @Test
    public void testCollapsePairMap() {
        int[] input = new int[]{ 0, 0, 1, 1, 1, 1, 4, 6, 6, 3, 3, 10 };
        List<Integer> expected = IntStreamEx.of(input).pairMap(( a, b) -> b - a).without(0).boxed().toList();
        TestHelpers.streamEx(() -> IntStreamEx.of(input).boxed(), ( supplier) -> Assert.assertEquals(expected, supplier.get().collapse(Integer::equals).pairMap(( a, b) -> b - a).toList()));
    }

    @Test
    public void testStreamOfSentences() {
        List<String> lines = Arrays.asList("This is the", "first sentence.  This is the", "second sentence. Third sentence. Fourth", "sentence. Fifth sentence.", "The last");
        TestHelpers.streamEx(lines::stream, ( supplier) -> Assert.assertEquals(Arrays.asList("This is the first sentence.", "This is the second sentence.", "Third sentence.", "Fourth sentence.", "Fifth sentence.", "The last"), StreamExTest.sentences(supplier.get()).toList()));
    }

    @Test
    public void testCollapseCollector() {
        List<String> input = Arrays.asList("aaa", "bb", "baz", "bar", "foo", "fee", "abc");
        TestHelpers.streamEx(input::stream, ( supplier) -> Assert.assertEquals(Arrays.asList("aaa", "bb:baz:bar", "foo:fee", "abc"), supplier.get().collapse(( a, b) -> (a.charAt(0)) == (b.charAt(0)), Collectors.joining(":")).toList()));
    }

    @Test
    public void testLongestSeries() {
        List<Integer> input = Arrays.asList(1, 2, 2, 3, 3, 2, 2, 2, 3, 4, 4, 4, 4, 4, 2);
        TestHelpers.streamEx(input::stream, ( supplier) -> Assert.assertEquals(5L, ((long) (supplier.get().collapse(Object::equals, Collectors.counting()).maxBy(Function.identity()).get()))));
    }

    @Test
    public void testGroupRuns() {
        List<String> input = Arrays.asList("aaa", "bb", "baz", "bar", "foo", "fee", "abc");
        List<List<String>> expected = Arrays.asList(Arrays.asList("aaa"), Arrays.asList("bb", "baz", "bar"), Arrays.asList("foo", "fee"), Arrays.asList("abc"));
        TestHelpers.streamEx(input::stream, ( supplier) -> {
            Assert.assertEquals(expected, supplier.get().groupRuns(( a, b) -> (a.charAt(0)) == (b.charAt(0))).toList());
            Assert.assertEquals(expected, supplier.get().collapse(( a, b) -> (a.charAt(0)) == (b.charAt(0)), Collectors.toList()).toList());
        });
    }

    @Test
    public void testGroupRunsRandom() {
        TestHelpers.withRandom(( r) -> {
            List<Integer> input = IntStreamEx.of(r, 1000, 1, 100).sorted().boxed().toList();
            List<List<Integer>> res1 = StreamEx.of(input).groupRuns(Integer::equals).toList();
            List<List<Integer>> res1p = StreamEx.of(input).parallel().groupRuns(Integer::equals).toList();
            List<List<Integer>> expected = new ArrayList<>();
            List<Integer> last = null;
            for (Integer num : input) {
                if (last != null) {
                    if (last.get(((last.size()) - 1)).equals(num)) {
                        last.add(num);
                        continue;
                    }
                    expected.add(last);
                }
                last = new ArrayList<>();
                last.add(num);
            }
            if (last != null)
                expected.add(last);

            Assert.assertEquals(expected, res1);
            Assert.assertEquals(expected, res1p);
        });
    }

    @Test
    public void testGroupRunsSeparated() {
        TestHelpers.streamEx(Arrays.asList("a", "b", null, "c", null, "d", "e")::stream, ( supplier) -> Assert.assertEquals(Arrays.asList(Arrays.asList("a", "b"), Arrays.asList("c"), Arrays.asList("d", "e")), supplier.get().groupRuns(( a, b) -> (a != null) && (b != null)).remove(( list) -> (list.get(0)) == null).toList()));
    }

    @Test
    public void testGroupRunsByStart() {
        List<String> input = Arrays.asList("str1", "str2", "START: str3", "str4", "START: str5", "START: str6", "START: str7", "str8", "str9");
        Pattern start = Pattern.compile("^START:");
        TestHelpers.streamEx(input::stream, ( supplier) -> Assert.assertEquals(Arrays.asList(Arrays.asList("str1", "str2"), Arrays.asList("START: str3", "str4"), Arrays.asList("START: str5"), Arrays.asList("START: str6"), Arrays.asList("START: str7", "str8", "str9")), supplier.get().groupRuns(( a, b) -> !(start.matcher(b).find())).toList()));
    }

    @Test
    public void testIntervalMapString() {
        int[] input = new int[]{ 1, 5, 2, 10, 8, 11, 7, 15, 6, 5 };
        String expected = StreamExTest.formatNaive(input);
        Assert.assertEquals(expected, StreamExTest.format(IntStreamEx.of(input).boxed()));
        for (int i = 0; i < 100; i++)
            Assert.assertEquals(expected, StreamExTest.format(IntStreamEx.of(input).boxed().parallel()));

        input = IntStreamEx.range(3, 100).prepend(1).toArray();
        Assert.assertEquals("1,3..99", StreamExTest.format(IntStreamEx.of(input).boxed()));
        for (int i = 0; i < 100; i++)
            Assert.assertEquals("1,3..99", StreamExTest.format(IntStreamEx.of(input).boxed().parallel()));

        input = IntStreamEx.of(new Random(1), 1000, 0, 2000).toArray();
        expected = StreamExTest.formatNaive(input);
        Assert.assertEquals(expected, StreamExTest.format(IntStreamEx.of(input).boxed()));
        Assert.assertEquals(expected, StreamExTest.format(IntStreamEx.of(input).boxed().parallel()));
    }

    @Test
    public void testRunLenghts() {
        Integer[] input = new Integer[]{ 1, 2, 2, 4, 2, null, null, 1, 1, 1, null, null };
        TestHelpers.streamEx(() -> StreamEx.of(input), ( s) -> {
            Assert.assertEquals("1: 1, 2: 2, 4: 1, 2: 1, null: 2, 1: 3, null: 2", s.get().runLengths().join(": ").joining(", "));
            Assert.assertEquals("1=1, 2=2, 4=1, 2=1, null=2, 1=3", s.get().runLengths().distinct().map(String::valueOf).joining(", "));
        });
        Map.Entry<Integer, Long> entry = StreamEx.of(input).runLengths().findFirst().get();
        // Test qeuals contract for custom entry
        Assert.assertNotEquals(entry, new Object());
        Assert.assertNotEquals(new Object(), entry);
        Assert.assertEquals(entry, new AbstractMap.SimpleImmutableEntry<>(1, 1L));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testRunLengthsModify() {
        StreamEx.of("1", "1", "1").runLengths().forEach(( e) -> e.setValue(5L));
    }

    @Test
    public void testRunLengthsSorted() {
        TestHelpers.withRandom(( r) -> {
            int[] input = IntStreamEx.of(r, 1000, 1, 20).sorted().toArray();
            Map<Integer, Long> expected = new HashMap<>();
            long len = 1;
            for (int i = 0; i < ((input.length) - 1); i++) {
                if ((input[i]) == (input[(i + 1)])) {
                    len++;
                } else {
                    expected.put(input[i], len);
                    len = 1;
                }
            }
            expected.put(input[((input.length) - 1)], len);
            Assert.assertEquals(expected, IntStreamEx.of(input).sorted().boxed().runLengths().toMap());
            Assert.assertEquals(expected, IntStreamEx.of(input).parallel().sorted().boxed().runLengths().toMap());
        });
    }

    @Test
    public void testSegmentLength() {
        Consumer<int[]> test = ( input) -> {
            // get maximal count of consecutive positive numbers
            long res = StreamExTest.segmentLength(IntStreamEx.of(input), ( x) -> x > 0);
            long resParallel = StreamExTest.segmentLength(IntStreamEx.of(input).parallel(), ( x) -> x > 0);
            long expected = 0;
            long cur = ((input[0]) > 0) ? 1 : 0;
            for (int i = 0; i < ((input.length) - 1); i++) {
                if (((input[i]) > 0) && ((input[(i + 1)]) > 0))
                    cur++;
                else {
                    if (cur > expected)
                        expected = cur;

                    cur = 1;
                }
            }
            if (cur > expected)
                expected = cur;

            Assert.assertEquals(expected, res);
            Assert.assertEquals(expected, resParallel);
        };
        TestHelpers.withRandom(( r) -> TestHelpers.repeat(100, ( n) -> test.accept(IntStreamEx.of(r, 1000, (-10), 100).toArray())));
        test.accept(new int[]{ 1, 2, 3, -1 });
        test.accept(new int[]{ -1, 1, 2, -1, 1, 2, 3 });
    }

    private static final class SeqList extends AbstractList<Integer> {
        final int size;

        SeqList(int size) {
            this.size = size;
        }

        @Override
        public Integer get(int index) {
            return index;
        }

        @Override
        public int size() {
            return size;
        }
    }

    @Test
    public void testSubLists() {
        List<Integer> input = IntStreamEx.range(12).boxed().toList();
        Assert.assertEquals("[0, 1, 2, 3, 4]-[5, 6, 7, 8, 9]-[10, 11]", StreamEx.ofSubLists(input, 5).joining("-"));
        Assert.assertEquals("[0, 1, 2, 3]-[4, 5, 6, 7]-[8, 9, 10, 11]", StreamEx.ofSubLists(input, 4).joining("-"));
        Assert.assertEquals("[0]-[1]-[2]-[3]-[4]-[5]-[6]-[7]-[8]-[9]-[10]-[11]", StreamEx.ofSubLists(input, 1).joining("-"));
        Assert.assertEquals("[0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11]", StreamEx.ofSubLists(input, 12).joining("-"));
        Assert.assertEquals("[0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11]", StreamEx.ofSubLists(input, Integer.MAX_VALUE).joining("-"));
        Assert.assertEquals("", StreamEx.ofSubLists(Collections.emptyList(), 1).joining("-"));
        Assert.assertEquals("", StreamEx.ofSubLists(Collections.emptyList(), Integer.MAX_VALUE).joining("-"));
        List<Integer> myList = new StreamExTest.SeqList(((Integer.MAX_VALUE) - 2));
        Assert.assertEquals(1, StreamEx.ofSubLists(myList, ((Integer.MAX_VALUE) - 1)).count());
        Assert.assertEquals(((Integer.MAX_VALUE) - 2), StreamEx.ofSubLists(myList, ((Integer.MAX_VALUE) - 1)).findFirst().get().size());
        Assert.assertEquals(1, StreamEx.ofSubLists(myList, ((Integer.MAX_VALUE) - 2)).count());
        Assert.assertEquals(1, StreamEx.ofSubLists(myList, ((Integer.MAX_VALUE) - 3)).skip(1).findFirst().get().size());
    }

    @Test
    public void testSubListsStep() {
        List<Integer> input = IntStreamEx.range(12).boxed().toList();
        Assert.assertEquals("[0, 1, 2, 3, 4]", StreamEx.ofSubLists(input, 5, Integer.MAX_VALUE).joining("-"));
        Assert.assertEquals("[0, 1, 2, 3, 4]", StreamEx.ofSubLists(input, 5, 12).joining("-"));
        Assert.assertEquals("[0, 1, 2, 3, 4]-[11]", StreamEx.ofSubLists(input, 5, 11).joining("-"));
        Assert.assertEquals("[0, 1, 2, 3, 4]-[9, 10, 11]", StreamEx.ofSubLists(input, 5, 9).joining("-"));
        Assert.assertEquals("[0, 1, 2, 3, 4]-[8, 9, 10, 11]", StreamEx.ofSubLists(input, 5, 8).joining("-"));
        Assert.assertEquals("[0, 1, 2, 3, 4]-[7, 8, 9, 10, 11]", StreamEx.ofSubLists(input, 5, 7).joining("-"));
        Assert.assertEquals("[0, 1, 2, 3, 4]-[6, 7, 8, 9, 10]", StreamEx.ofSubLists(input, 5, 6).joining("-"));
        Assert.assertEquals("[0, 1, 2, 3, 4]-[4, 5, 6, 7, 8]-[8, 9, 10, 11]", StreamEx.ofSubLists(input, 5, 4).joining("-"));
        Assert.assertEquals("[0, 1, 2, 3, 4]-[3, 4, 5, 6, 7]-[6, 7, 8, 9, 10]-[9, 10, 11]", StreamEx.ofSubLists(input, 5, 3).joining("-"));
        Assert.assertEquals("[0, 1, 2, 3, 4]-[2, 3, 4, 5, 6]-[4, 5, 6, 7, 8]-[6, 7, 8, 9, 10]-[8, 9, 10, 11]", StreamEx.ofSubLists(input, 5, 2).joining("-"));
        Assert.assertEquals(("[0, 1, 2, 3, 4]-[1, 2, 3, 4, 5]-[2, 3, 4, 5, 6]-[3, 4, 5, 6, 7]-" + "[4, 5, 6, 7, 8]-[5, 6, 7, 8, 9]-[6, 7, 8, 9, 10]-[7, 8, 9, 10, 11]"), StreamEx.ofSubLists(input, 5, 1).joining("-"));
        List<Integer> myList = new StreamExTest.SeqList(((Integer.MAX_VALUE) - 2));
        Assert.assertEquals(1, StreamEx.ofSubLists(myList, ((Integer.MAX_VALUE) - 1), 1).count());
        Assert.assertEquals("[0]", StreamEx.ofSubLists(myList, 1, ((Integer.MAX_VALUE) - 1)).joining());
        Assert.assertEquals("[0]", StreamEx.ofSubLists(myList, 1, ((Integer.MAX_VALUE) - 2)).joining());
        Assert.assertEquals("[0][2147483644]", StreamEx.ofSubLists(myList, 1, ((Integer.MAX_VALUE) - 3)).joining());
        Assert.assertEquals("[0, 1]", StreamEx.ofSubLists(myList, 2, ((Integer.MAX_VALUE) - 1)).joining());
        Assert.assertEquals("[0, 1]", StreamEx.ofSubLists(myList, 2, ((Integer.MAX_VALUE) - 2)).joining());
        Assert.assertEquals("[0, 1][2147483644]", StreamEx.ofSubLists(myList, 2, ((Integer.MAX_VALUE) - 3)).joining());
        Assert.assertEquals(((Integer.MAX_VALUE) - 2), StreamEx.ofSubLists(myList, ((Integer.MAX_VALUE) - 1), 1).findFirst().get().size());
        Assert.assertEquals(1, StreamEx.ofSubLists(myList, ((Integer.MAX_VALUE) - 2), 1).count());
        Assert.assertEquals(998, StreamEx.ofSubLists(myList, ((Integer.MAX_VALUE) - 3), ((Integer.MAX_VALUE) - 1000)).skip(1).findFirst().get().size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSubListsArg() {
        StreamEx.ofSubLists(Collections.emptyList(), 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSubListsStepArg() {
        StreamEx.ofSubLists(Collections.emptyList(), 1, 0);
    }

    @Test
    public void testTakeWhile() {
        TestHelpers.streamEx(Arrays.asList("aaa", "b", "cccc")::stream, ( s) -> {
            Assert.assertEquals(Arrays.asList("aaa"), s.get().takeWhile(( x) -> (x.length()) > 1).toList());
            Assert.assertEquals(Arrays.asList("aaa"), s.get().sorted().takeWhile(( x) -> (x.length()) > 1).toList());
            Assert.assertEquals(Arrays.asList("aaa", "b", "cccc"), s.get().takeWhile(( x) -> (x.length()) > 0).toList());
            Assert.assertEquals(Collections.emptyList(), s.get().takeWhile(( x) -> (x.length()) > 5).toList());
        });
    }

    @Test
    public void testTakeWhileInclusive() {
        TestHelpers.streamEx(Arrays.asList("aaa", "b", "cccc")::stream, ( s) -> {
            Assert.assertEquals(Arrays.asList("aaa", "b"), s.get().takeWhileInclusive(( x) -> (x.length()) > 1).toList());
            Assert.assertEquals(Arrays.asList("aaa", "b", "cccc"), s.get().takeWhileInclusive(( x) -> (x.length()) > 0).toList());
            Assert.assertEquals(Arrays.asList("aaa"), s.get().takeWhileInclusive(( x) -> (x.length()) > 5).toList());
        });
    }

    @Test
    public void testDropWhile() {
        TestHelpers.streamEx(Arrays.asList("aaa", "b", "cccc")::stream, ( s) -> {
            Assert.assertEquals(Arrays.asList("b", "cccc"), s.get().dropWhile(( x) -> (x.length()) > 1).toList());
            Assert.assertEquals(Arrays.asList(), s.get().dropWhile(( x) -> (x.length()) > 0).toList());
            Assert.assertEquals(Arrays.asList("aaa", "b", "cccc"), s.get().dropWhile(( x) -> (x.length()) > 5).toList());
            // Saving to Optional is necessary as javac <8u40 fails to compile
            // this without intermediate variable
            Optional<String> opt1 = s.get().dropWhile(( x) -> (x.length()) > 1).findFirst();
            Assert.assertEquals(Optional.of("b"), opt1);
            Optional<String> opt0 = s.get().dropWhile(( x) -> (x.length()) > 0).findFirst();
            Assert.assertEquals(Optional.empty(), opt0);
            Optional<String> opt5 = s.get().dropWhile(( x) -> (x.length()) > 5).findFirst();
            Assert.assertEquals(Optional.of("aaa"), opt5);
        });
        // Test that in JDK9 operation is propagated to JDK dropWhile method.
        boolean hasDropWhile = true;
        try {
            Stream.class.getDeclaredMethod("dropWhile", Predicate.class);
        } catch (NoSuchMethodException e) {
            hasDropWhile = false;
        }
        Spliterator<String> spliterator = StreamEx.of("aaa", "b", "cccc").dropWhile(( x) -> (x.length()) > 1).spliterator();
        Assert.assertEquals(hasDropWhile, (!(spliterator.getClass().getSimpleName().equals("TDOfRef"))));
    }

    @Test
    public void testTakeDropUnordered() {
        TestHelpers.repeat(10, ( n) -> TestHelpers.withRandom(( rnd) -> {
            List<Boolean> data = IntStreamEx.of(rnd, (n * 100), 0, ((rnd.nextInt(10)) + 2)).mapToObj(( x) -> x != 0).toList();
            List<Boolean> sorted = StreamEx.of(data).sorted().toList();
            TestHelpers.streamEx(() -> data.stream().unordered(), ( s) -> {
                Assert.assertFalse(StreamEx.of(s.get().takeWhile(( b) -> b).toList()).has(false));
                Assert.assertEquals(1L, StreamEx.of(s.get().takeWhileInclusive(( b) -> b).toList()).without(true).count());
                Assert.assertEquals(0L, s.get().dropWhile(( b) -> true).count());
                Assert.assertEquals(0L, s.get().takeWhile(( b) -> false).count());
                Assert.assertEquals(1L, s.get().takeWhileInclusive(( b) -> false).count());
                List<Boolean> dropNone = s.get().dropWhile(( b) -> false).sorted().toList();
                Assert.assertEquals(sorted, dropNone);
                List<Boolean> takeAll = s.get().takeWhileInclusive(( b) -> true).sorted().toList();
                Assert.assertEquals(sorted, takeAll);
            });
        }));
    }

    @Test
    public void testOfPairs() {
        TestHelpers.withRandom(( r) -> {
            StreamExTest.Point[] pts = StreamEx.generate(() -> new one.util.streamex.Point(r.nextDouble(), r.nextDouble())).limit(100).toArray(StreamExTest.Point[]::new);
            double expected = StreamEx.of(pts).cross(pts).mapKeyValue(StreamExTest.Point::distance).mapToDouble(Double::doubleValue).max().getAsDouble();
            double[] allDist = IntStreamEx.ofIndices(pts).flatMapToDouble(( i1) -> StreamEx.of(pts, (i1 + 1), pts.length).mapToDouble(( pt) -> pt.distance(pts[i1]))).toArray();
            TestHelpers.streamEx(() -> StreamEx.ofPairs(pts, StreamExTest.Point::distance), ( supplier) -> {
                Assert.assertEquals(expected, supplier.get().mapToDouble(Double::doubleValue).max().getAsDouble(), 0.0);
                Assert.assertArrayEquals(allDist, supplier.get().mapToDouble(Double::doubleValue).toArray(), 0.0);
            });
        });
    }

    @Test
    public void testToFlatCollection() {
        List<List<String>> strings = IntStreamEx.range(100).mapToObj(String::valueOf).groupRuns(( a, b) -> (a.charAt(0)) == (b.charAt(0))).toList();
        Set<String> expected = IntStreamEx.range(100).mapToObj(String::valueOf).toSet();
        List<String> expectedList = IntStreamEx.range(100).mapToObj(String::valueOf).toList();
        TestHelpers.streamEx(strings::stream, ( supplier) -> {
            Assert.assertEquals(expected, supplier.get().toFlatCollection(( x) -> x, HashSet::new));
            Assert.assertEquals(expectedList, supplier.get().toFlatList(( x) -> x));
        });
    }

    @Test
    public void testCartesian() {
        List<List<Integer>> expected = IntStreamEx.range(32).mapToObj(( i) -> IntStreamEx.range(5).mapToObj(( n) -> (i >> (4 - n)) & 1).toList()).toList();
        TestHelpers.streamEx(() -> StreamEx.cartesianPower(5, Arrays.asList(0, 1)), ( supplier) -> Assert.assertEquals(expected, supplier.get().toList()));
        List<List<Integer>> input2 = Arrays.asList(Arrays.asList(1, 2, 3), Arrays.asList(), Arrays.asList(4, 5, 6));
        TestHelpers.streamEx(() -> StreamEx.cartesianProduct(input2), ( supplier) -> Assert.assertFalse(supplier.get().findAny().isPresent()));
        List<List<Integer>> input3 = Arrays.asList(Arrays.asList(1, 2), Arrays.asList(3), Arrays.asList(4, 5));
        TestHelpers.streamEx(() -> StreamEx.cartesianProduct(input3), ( supplier) -> Assert.assertEquals("[1, 3, 4],[1, 3, 5],[2, 3, 4],[2, 3, 5]", supplier.get().joining(",")));
        Set<Integer> input4 = IntStreamEx.range(10).boxed().toCollection(TreeSet::new);
        TestHelpers.streamEx(() -> StreamEx.cartesianPower(3, input4), ( supplier) -> Assert.assertEquals(IntStreamEx.range(1000).boxed().toList(), supplier.get().map(( list) -> (((list.get(0)) * 100) + ((list.get(1)) * 10)) + (list.get(2))).toList()));
        Assert.assertEquals(Arrays.asList(Collections.emptyList()), StreamEx.cartesianProduct(Collections.emptyList()).toList());
        Assert.assertEquals(Arrays.asList(Collections.emptyList()), StreamEx.cartesianPower(0, Arrays.asList(1, 2, 3)).toList());
    }

    @Test
    public void testCartesianReduce() {
        List<String> expected = IntStreamEx.range(32).mapToObj(( i) -> IntStreamEx.range(5).mapToObj(( n) -> (i >> (4 - n)) & 1).joining()).toList();
        TestHelpers.streamEx(() -> StreamEx.cartesianPower(5, Arrays.asList(0, 1), "", ( a, b) -> a + b), ( supplier) -> Assert.assertEquals(expected, supplier.get().toList()));
        List<List<Integer>> input2 = Arrays.asList(Arrays.asList(1, 2, 3), Arrays.asList(), Arrays.asList(4, 5, 6));
        TestHelpers.streamEx(() -> StreamEx.cartesianProduct(input2, "", ( a, b) -> a + b), ( supplier) -> Assert.assertFalse(supplier.get().findAny().isPresent()));
        List<List<Integer>> input3 = Arrays.asList(Arrays.asList(1, 2), Arrays.asList(3), Arrays.asList(4, 5));
        TestHelpers.streamEx(() -> StreamEx.cartesianProduct(input3, "", ( a, b) -> a + b), ( supplier) -> Assert.assertEquals("134,135,234,235", supplier.get().joining(",")));
        Assert.assertEquals(Arrays.asList(""), StreamEx.cartesianProduct(Collections.<List<String>>emptyList(), "", String::concat).toList());
        Assert.assertEquals(Arrays.asList(""), StreamEx.cartesianPower(0, Arrays.asList(1, 2, 3), "", ( a, b) -> a + b).toList());
    }

    @Test
    public void testDistinct() {
        List<String> input = Arrays.asList("str", "a", "foo", "", "bbbb", null, "abcd", "s");
        TestHelpers.streamEx(input::stream, ( supplier) -> {
            Assert.assertEquals(input, supplier.get().distinct(( x) -> x).toList());
            Assert.assertEquals(Arrays.asList("str", "a", "", "bbbb"), supplier.get().distinct(( x) -> x == null ? 0 : x.length()).toList());
        });
    }

    @Test
    public void testIndexOf() {
        List<Integer> input = IntStreamEx.range(100).append(IntStreamEx.range(100)).boxed().toList();
        AtomicInteger counter = new AtomicInteger();
        Assert.assertEquals(10, StreamEx.of(input).peek(( t) -> counter.incrementAndGet()).indexOf(10).getAsLong());
        Assert.assertEquals(11, counter.get());
        for (int i = 0; i < 100; i++) {
            Assert.assertEquals(99, StreamEx.of(input).parallel().prepend().peek(( t) -> counter.incrementAndGet()).indexOf(99).getAsLong());
        }
        TestHelpers.streamEx(input::stream, ( supplier) -> {
            for (int i : new int[]{ 0, 1, 10, 50, 78, 99 }) {
                Assert.assertEquals(("#" + i), i, supplier.get().peek(( t) -> counter.incrementAndGet()).indexOf(( x) -> x == i).getAsLong());
            }
            Assert.assertFalse(supplier.get().indexOf(( x) -> false).isPresent());
        });
    }

    @Test
    public void testIndexOfSimple() {
        List<Integer> input = IntStreamEx.range(10).boxed().toList();
        for (int i = 0; i < 100; i++) {
            Assert.assertEquals(9, StreamEx.of(input).parallel().indexOf(9).getAsLong());
        }
    }

    @Test
    public void testMapFirstLast() {
        TestHelpers.streamEx(() -> StreamEx.of(0, 343, 999), ( s) -> Assert.assertEquals(Arrays.asList(2, 343, 997), s.get().mapFirst(( x) -> x + 2).mapLast(( x) -> x - 2).toList()));
        TestHelpers.streamEx(() -> IntStreamEx.range(1000).boxed(), ( s) -> {
            Assert.assertEquals(Arrays.asList(2, 343, 997), s.get().filter(( x) -> ((x == 0) || (x == 343)) || (x == 999)).mapFirst(( x) -> x + 2).mapLast(( x) -> x - 2).toList());
            Assert.assertEquals(Arrays.asList(4, 343, 997), s.get().filter(( x) -> ((x == 0) || (x == 343)) || (x == 999)).mapFirst(( x) -> x + 2).mapFirst(( x) -> x + 2).mapLast(( x) -> x - 2).toList());
        });
        Supplier<Stream<Integer>> base = () -> IntStreamEx.rangeClosed(49, 0, (-1)).boxed().foldLeft(StreamEx.of(0), ( stream, i) -> stream.prepend(i).mapFirst(( x) -> x + 2));
        TestHelpers.streamEx(base, ( s) -> Assert.assertEquals(IntStreamEx.range(2, 52).boxed().append(0).toList(), s.get().toList()));
        base = () -> IntStreamEx.range(50).boxed().foldLeft(StreamEx.of(0), ( stream, i) -> stream.append(i).mapLast(( x) -> x + 2));
        TestHelpers.streamEx(base, ( s) -> Assert.assertEquals(IntStreamEx.range(2, 52).boxed().prepend(0).toList(), s.get().toList()));
        TestHelpers.streamEx(Arrays.asList("red", "green", "blue", "orange")::stream, ( s) -> Assert.assertEquals("red, green, blue, or orange", s.get().mapLast("or "::concat).joining(", ")));
    }

    @Test
    public void testMapFirstOrElse() {
        TestHelpers.streamEx(() -> StreamEx.split("testString", ""), ( s) -> Assert.assertEquals("Teststring", s.get().mapFirstOrElse(String::toUpperCase, String::toLowerCase).joining()));
    }

    @Test
    public void testMapLastOrElse() {
        TestHelpers.streamEx(Arrays.asList("red", "green", "blue", "orange")::stream, ( s) -> Assert.assertEquals("red, green, blue, or orange!", s.get().mapLastOrElse(( str) -> str + ", ", ( str) -> ("or " + str) + "!").joining()));
        TestHelpers.streamEx(Arrays.asList("red", "green", "blue", "orange")::stream, ( s) -> Assert.assertEquals("|- red\n|- green\n|- blue\n\\- orange", s.get().mapLastOrElse("|- "::concat, "\\- "::concat).joining("\n")));
    }

    @Test
    public void testPeekFirst() {
        List<String> input = Arrays.asList("A", "B", "C", "D");
        TestHelpers.streamEx(input::stream, ( s) -> {
            AtomicReference<String> firstElement = new AtomicReference<>();
            Assert.assertEquals(Arrays.asList("B", "C", "D"), s.get().peekFirst(firstElement::set).skip(1).toList());
            Assert.assertEquals("A", firstElement.get());
            Assert.assertEquals(Arrays.asList("B", "C", "D"), s.get().skip(1).peekFirst(firstElement::set).toList());
            Assert.assertEquals("B", firstElement.get());
            firstElement.set(null);
            Assert.assertEquals(Arrays.asList(), s.get().skip(4).peekFirst(firstElement::set).toList());
            Assert.assertNull(firstElement.get());
        });
    }

    @Test
    public void testPeekLast() {
        List<String> input = Arrays.asList("A", "B", "C", "D");
        AtomicReference<String> lastElement = new AtomicReference<>();
        Assert.assertEquals(Arrays.asList("A", "B", "C"), StreamEx.of(input).peekLast(lastElement::set).limit(3).toList());
        Assert.assertNull(lastElement.get());
        Assert.assertEquals(input, StreamEx.of(input).peekLast(lastElement::set).limit(4).toList());
        Assert.assertEquals("D", lastElement.get());
        Assert.assertEquals(Arrays.asList("A", "B", "C"), StreamEx.of(input).limit(3).peekLast(lastElement::set).toList());
        Assert.assertEquals("C", lastElement.get());
        lastElement.set(null);
        Assert.assertEquals(2L, StreamEx.of(input).peekLast(lastElement::set).indexOf("C").getAsLong());
        Assert.assertNull(lastElement.get());
        Assert.assertEquals(3L, StreamEx.of(input).peekLast(lastElement::set).indexOf("D").getAsLong());
        Assert.assertEquals("D", lastElement.get());
    }

    @Test
    public void testSplit() {
        Assert.assertFalse(StreamEx.split("str", "abcd").spliterator().getClass().getSimpleName().endsWith("IteratorSpliterator"));
        Assert.assertFalse(StreamEx.split("str", Pattern.compile("abcd")).spliterator().getClass().getSimpleName().endsWith("IteratorSpliterator"));
        TestHelpers.streamEx(() -> StreamEx.split("", "abcd"), ( s) -> Assert.assertEquals(1, s.get().count()));
        TestHelpers.streamEx(() -> StreamEx.split("", Pattern.compile("abcd")), ( s) -> Assert.assertEquals(1, s.get().count()));
        TestHelpers.streamEx(() -> StreamEx.split("ab.cd...", '.'), ( s) -> Assert.assertEquals("ab|cd", s.get().joining("|")));
        TestHelpers.streamEx(() -> StreamEx.split("ab.cd...", "."), ( s) -> Assert.assertEquals(0, s.get().count()));
        TestHelpers.streamEx(() -> StreamEx.split("ab.cd...", "\\."), ( s) -> Assert.assertEquals("ab|cd", s.get().joining("|")));
        TestHelpers.streamEx(() -> StreamEx.split("ab.cd...", "cd"), ( s) -> Assert.assertEquals("ab.|...", s.get().joining("|")));
        TestHelpers.streamEx(() -> StreamEx.split("ab.cd...", "\\w"), ( s) -> Assert.assertEquals("||.||...", s.get().joining("|")));
        TestHelpers.streamEx(() -> StreamEx.split("ab.cd...", "\\W"), ( s) -> Assert.assertEquals("ab|cd", s.get().joining("|")));
        TestHelpers.streamEx(() -> StreamEx.split("ab|cd|e", "\\|"), ( s) -> Assert.assertEquals("ab,cd,e", s.get().joining(",")));
    }

    @Test
    public void testSplitChar() {
        TestHelpers.streamEx(() -> StreamEx.split("abcd,e,f,gh,,,i,j,kl,,,,,,", ','), ( s) -> Assert.assertEquals("abcd|e|f|gh|||i|j|kl", s.get().joining("|")));
        TestHelpers.streamEx(() -> StreamEx.split("abcd,e,f,gh,,,i,j,kl,,,,,,x", ','), ( s) -> Assert.assertEquals("abcd|e|f|gh|||i|j|kl||||||x", s.get().joining("|")));
        TestHelpers.streamEx(() -> StreamEx.split("abcd", ','), ( s) -> Assert.assertEquals("abcd", s.get().joining("|")));
        TestHelpers.streamEx(() -> StreamEx.split("", ','), ( s) -> Assert.assertEquals(Arrays.asList(""), s.get().toList()));
        TestHelpers.streamEx(() -> StreamEx.split(",,,,,,,,,", ','), ( s) -> Assert.assertEquals(0, s.get().count()));
        TestHelpers.withRandom(( r) -> TestHelpers.repeat(10, ( iter) -> {
            StringBuilder source = new StringBuilder(IntStreamEx.of(r, 0, 3).limit(r.nextInt(10000)).elements(new int[]{ ',', 'a', 'b' }).charsToString());
            String[] expected = source.toString().split(",");
            String[] expectedFull = source.toString().split(",", (-1));
            TestHelpers.streamEx(() -> StreamEx.split(source, ','), ( s) -> Assert.assertArrayEquals(expected, s.get().toArray(String[]::new)));
            TestHelpers.streamEx(() -> StreamEx.split(source, ',', false), ( s) -> Assert.assertArrayEquals(expectedFull, s.get().toArray(String[]::new)));
        }));
    }

    @Test
    public void testWithFirst() {
        TestHelpers.repeat(10, ( i) -> {
            TestHelpers.streamEx(() -> StreamEx.of(0, 2, 4), ( s) -> Assert.assertEquals(Arrays.asList("0|0", "0|1", "0|2", "0|3", "0|4", "0|5"), s.get().flatMap(( x) -> Stream.of(x, (x + 1))).withFirst().mapKeyValue(( a, b) -> (a + "|") + b).toList()));
            // Check exception-friendliness: short-circuiting collectors use Exceptions for control flow
            TestHelpers.streamEx(() -> IntStreamEx.range(100).boxed(), ( s) -> {
                Assert.assertEquals("0|0, 0|1, 0|2, ...", s.get().withFirst(( a, b) -> (a + "|") + b).collect(Joining.with(", ").maxChars(18)));
                Assert.assertEquals(Arrays.asList(), s.get().withFirst(( a, b) -> (a + "|") + b).collect(MoreCollectors.head(0)));
            });
            TestHelpers.streamEx(() -> StreamEx.of("a", "b", "c", "d"), ( s) -> Assert.assertEquals(Collections.singletonMap("a", Arrays.asList("a", "b", "c", "d")), s.get().withFirst().grouping()));
            TestHelpers.streamEx(() -> StreamEx.of("a", "b", "c", "d"), ( s) -> Assert.assertEquals(Arrays.asList("aa", "ab", "ac", "ad"), s.get().withFirst(String::concat).toList()));
            // Header mapping
            String input = "name,type,value\nID,int,5\nSurname,string,Smith\nGiven name,string,John";
            List<Map<String, String>> expected = Arrays.asList(EntryStream.of("name", "ID", "type", "int", "value", "5").toMap(), EntryStream.of("name", "Surname", "type", "string", "value", "Smith").toMap(), EntryStream.of("name", "Given name", "type", "string", "value", "John").toMap());
            TestHelpers.streamEx(() -> StreamEx.ofLines(new StringReader(input)), ( s) -> {
                Assert.assertEquals(expected, s.get().map(( str) -> str.split(",")).withFirst().skip(1).mapKeyValue(( header, row) -> EntryStream.zip(header, row).toMap()).toList());
                Assert.assertEquals(expected, s.get().map(( str) -> str.split(",")).withFirst(( header, row) -> EntryStream.zip(header, row).toMap()).skip(1).toList());
            });
        });
        Map<Integer, List<Integer>> expected = Collections.singletonMap(0, IntStreamEx.range(0, 10000).boxed().toList());
        TestHelpers.streamEx(() -> IntStreamEx.range(10000).boxed(), ( s) -> Assert.assertEquals(expected, s.get().withFirst().grouping()));
        TestHelpers.streamEx(() -> StreamEx.of(5, 10, 13, 12, 11), ( s) -> Assert.assertEquals(Arrays.asList("5+0", "5+5", "5+8", "5+7", "5+6"), s.get().withFirst(( a, b) -> (a + "+") + (b - a)).toList()));
    }

    @Test
    public void testZipWith() {
        List<String> input = Arrays.asList("John", "Mary", "Jane", "Jimmy");
        Spliterator<String> spliterator = StreamEx.of(input).zipWith(IntStreamEx.range(1, Integer.MAX_VALUE).boxed(), ( name, idx) -> (idx + ". ") + name).spliterator();
        Assert.assertEquals(4, spliterator.getExactSizeIfKnown());
        List<String> expected = Arrays.asList("1. John", "2. Mary", "3. Jane", "4. Jimmy");
        TestHelpers.streamEx(input::stream, ( s) -> Assert.assertEquals(expected, s.get().zipWith(IntStream.range(1, Integer.MAX_VALUE).boxed(), ( name, idx) -> (idx + ". ") + name).toList()));
        TestHelpers.streamEx(input::stream, ( s) -> Assert.assertEquals(expected, s.get().zipWith(IntStream.range(1, Integer.MAX_VALUE).boxed()).mapKeyValue(( name, idx) -> (idx + ". ") + name).toList()));
        TestHelpers.streamEx(() -> IntStream.range(1, Integer.MAX_VALUE).boxed(), ( s) -> Assert.assertEquals(expected, s.get().zipWith(input.stream(), ( idx, name) -> (idx + ". ") + name).toList()));
        TestHelpers.streamEx(input::stream, ( s) -> Assert.assertEquals(expected, s.get().zipWith(IntStreamEx.ints()).mapKeyValue(( name, idx) -> ((idx + 1) + ". ") + name).toList()));
        TestHelpers.streamEx(input::stream, ( s) -> Assert.assertEquals(expected, s.get().zipWith(IntStreamEx.ints(), ( name, idx) -> ((idx + 1) + ". ") + name).toList()));
    }

    @Test
    public void testProduce() {
        Assert.assertEquals(Arrays.asList(4, 4, 4, 4, 4), StreamExTest.generate(() -> 4).limit(5).toList());
        Assert.assertEquals(Arrays.asList("foo", "bar", "baz"), StreamExTest.fromSpliterator(Arrays.asList("foo", "bar", "baz").spliterator()).toList());
        Assert.assertEquals(Arrays.asList("foo", "bar", "baz"), StreamExTest.fromIterator(Arrays.asList("foo", "bar", "baz").iterator()).toList());
        Assert.assertEquals(Arrays.asList("123", "543", "111", "5432"), StreamExTest.matches(Pattern.compile("\\d+").matcher("123 543,111:5432")).toList());
        Queue<String> queue = new ArrayDeque<>(Arrays.asList("one", "two", "STOP", "three", "four", "five", "STOP", "STOP", "six"));
        Assert.assertEquals(Arrays.asList("one", "two"), StreamExTest.fromQueue(queue, "STOP").toList());
        Assert.assertEquals(Arrays.asList("three", "four", "five"), StreamExTest.fromQueue(queue, "STOP").toList());
        Assert.assertEquals(Arrays.asList(), StreamExTest.fromQueue(queue, "STOP").toList());
        Assert.assertEquals(Arrays.asList("six"), StreamExTest.fromQueue(queue, "STOP").toList());
        Assert.assertEquals(Arrays.asList(), StreamExTest.fromQueue(queue, "STOP").toList());
    }

    @Test
    public void testPrefix() {
        List<String> input = Arrays.asList("a", "b", "c", "d", "e");
        TestHelpers.streamEx(input::stream, ( s) -> Assert.assertEquals(Arrays.asList("a", "ab", "abc", "abcd", "abcde"), s.get().prefix(String::concat).toList()));
        TestHelpers.streamEx(input::stream, ( s) -> Assert.assertEquals(Optional.of("abcd"), s.get().prefix(String::concat).findFirst(( str) -> (str.length()) > 3)));
        TestHelpers.streamEx(() -> StreamEx.constant("a", 5), ( s) -> Assert.assertEquals(new HashSet(Arrays.asList("a", "aa", "aaa", "aaaa", "aaaaa")), s.get().prefix(String::concat).toSet()));
        TestHelpers.streamEx(() -> StreamEx.constant("a", 5), ( s) -> Assert.assertEquals(Optional.of("aaaaa"), s.get().prefix(String::concat).findFirst(( str) -> (str.length()) > 4)));
        TestHelpers.streamEx(() -> StreamEx.constant(100L, 10000), ( s) -> Assert.assertEquals(5000500000L, ((long) (s.get().prefix(Long::sum).reduce(0L, Long::sum)))));
        TestHelpers.streamEx(() -> IntStreamEx.range(10000).boxed().unordered(), ( s) -> Assert.assertEquals(49995000, s.get().prefix(Integer::sum).mapToInt(Integer::intValue).max().getAsInt()));
    }

    @Test
    public void testMaxWithStop() {
        // Infinite stream, stop is reached
        Assert.assertEquals(Optional.of(1000), StreamExTest.maxWithStop(IntStreamEx.of(new Random(1), 0, 1001).boxed(), Comparator.naturalOrder(), 1000));
        // Finite stream, stop is not reached
        Assert.assertEquals(Optional.of(999), StreamExTest.maxWithStop(IntStreamEx.of(new Random(1), 10000, 0, 1000).boxed(), Comparator.naturalOrder(), 1001));
    }

    @Test
    public void testToImmutableList() {
        List<Integer> expected = Arrays.asList(1, 2, 3);
        TestHelpers.repeat(4, ( n) -> TestHelpers.streamEx(() -> IntStreamEx.range(4).atLeast(n).boxed(), ( s) -> {
            List<Integer> list = s.get().toImmutableList();
            Assert.assertEquals(expected.subList((n - 1), expected.size()), list);
            try {
                list.add(0);
                Assert.fail("added");
            } catch (UnsupportedOperationException e) {
                // expected
            }
            try {
                list.set(0, 0);
                Assert.fail("set");
            } catch (UnsupportedOperationException e) {
                // expected
            }
        }));
    }

    @Test
    public void testToImmutableSet() {
        List<Integer> expected = Arrays.asList(1, 2, 3);
        TestHelpers.repeat(4, ( n) -> TestHelpers.streamEx(() -> IntStreamEx.range(4).atLeast(n).boxed(), ( s) -> {
            Set<Integer> set = s.get().toImmutableSet();
            Assert.assertEquals(new HashSet<>(expected.subList((n - 1), expected.size())), set);
            try {
                set.add((-1));
                Assert.fail("added");
            } catch (UnsupportedOperationException e) {
                // expected
            }
        }));
    }

    @Test
    public void testInto() {
        for (List<Number> list : Arrays.asList(new ArrayList<Number>(), new LinkedList<Number>())) {
            List<Number> res = StreamEx.of(1, 2, 3, 4).filter(( x) -> x < 4).into(list);
            Assert.assertSame(list, res);
            Assert.assertEquals(Arrays.asList(1, 2, 3), list);
            Assert.assertSame(list, StreamEx.of(4, 5, 6).into(list));
            Assert.assertEquals(Arrays.asList(1, 2, 3, 4, 5, 6), list);
            Assert.assertSame(list, StreamEx.of(7, 8, 9, 10).parallel().into(list));
            Assert.assertEquals(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), list);
        }
        // a kind of mock object
        Collection<String> c = new ArrayList<String>() {
            private static final long serialVersionUID = 1L;

            int size = (Integer.MAX_VALUE) - 10;

            @Override
            public boolean add(String e) {
                Assert.assertEquals("a", e);
                (size)++;
                return true;
            }

            @Override
            public int size() {
                return size;
            }
        };
        Assert.assertSame(c, StreamEx.constant("a", 20).into(c));
        // noinspection NumericOverflow
        Assert.assertEquals(((Integer.MAX_VALUE) + 10), c.size());
    }

    @Test
    public void testFilterBy() {
        Assert.assertEquals(3, StreamEx.of("a", "bb", "c", "e", "ddd").filterBy(String::length, 1).count());
        Assert.assertEquals(2, StreamEx.of("a", "bb", "c", "e", "ddd").filterBy(( x) -> (x.length()) > 1 ? null : x, null).count());
    }

    @Test
    public void testRemoveBy() {
        Assert.assertEquals(2, StreamEx.of("a", "bb", "c", "e", "ddd").removeBy(String::length, 1).count());
        Assert.assertEquals(3, StreamEx.of("a", "bb", "c", "e", "ddd").removeBy(( x) -> (x.length()) > 1 ? null : x, null).count());
    }

    @Test
    public void testIntersperse() {
        List<String> expected = Arrays.asList("a", "--", "b", "--", "c", "--", "d", "--", "e");
        TestHelpers.streamEx(Arrays.asList("a", "b", "c", "d", "e")::stream, ( s) -> Assert.assertEquals(expected, s.get().intersperse("--").toList()));
        Assert.assertEquals(Collections.emptyList(), StreamEx.empty().intersperse("xyz").toList());
    }

    @Test
    public void testIfEmpty() {
        TestHelpers.repeat(10, ( n) -> TestHelpers.streamEx(Arrays.asList(1, 2, 3, 4, 5, 6)::stream, ( s) -> {
            Assert.assertEquals("123456", s.get().ifEmpty(7, 8, 9).joining());
            Assert.assertEquals("123456", s.get().filter(( x) -> x > 0).ifEmpty(7, 8, 9).joining());
            Assert.assertEquals("6", s.get().filter(( x) -> x > 5).ifEmpty(7, 8, 9).joining());
            Assert.assertEquals("789", s.get().filter(( x) -> x < 0).ifEmpty(7, 8, 9).joining());
            Assert.assertEquals("123456", s.get().ifEmpty(s.get()).joining());
            Assert.assertEquals("123456", s.get().filter(( x) -> x > 0).ifEmpty(s.get().filter(( x) -> (x % 2) == 1)).joining());
            Assert.assertEquals("135", s.get().filter(( x) -> x < 0).ifEmpty(s.get().filter(( x) -> (x % 2) == 1)).joining());
            Assert.assertEquals("", s.get().filter(( x) -> x < 0).ifEmpty(s.get().filter(( x) -> x < 0)).joining());
            Assert.assertEquals(Optional.of(1), s.get().ifEmpty(7, 8, 9).findFirst());
            Assert.assertEquals(Optional.of(1), s.get().filter(( x) -> x > 0).ifEmpty(7, 8, 9).findFirst());
            Assert.assertEquals(Optional.of(6), s.get().filter(( x) -> x > 5).ifEmpty(7, 8, 9).findFirst());
            Assert.assertEquals(Optional.of(7), s.get().filter(( x) -> x < 0).ifEmpty(7, 8, 9).findFirst());
            Assert.assertEquals(Optional.of(1), s.get().ifEmpty(s.get()).findFirst());
            Assert.assertEquals(Optional.of(1), s.get().filter(( x) -> x > 0).ifEmpty(s.get().filter(( x) -> (x % 2) == 1)).findFirst());
            Assert.assertEquals(Optional.of(1), s.get().filter(( x) -> x < 0).ifEmpty(s.get().filter(( x) -> (x % 2) == 1)).findFirst());
            Assert.assertEquals(Optional.empty(), s.get().filter(( x) -> x < 0).ifEmpty(s.get().filter(( x) -> x < 0)).findFirst());
            Assert.assertEquals(Optional.of(1), s.get().ifEmpty().findFirst());
            Assert.assertEquals(Optional.of(1), StreamEx.empty().ifEmpty(s.get()).findFirst());
        }));
    }

    @Test
    public void testOfCombinations() {
        List<String> expectedN5K3 = Arrays.asList("[0, 1, 2]", "[0, 1, 3]", "[0, 1, 4]", "[0, 2, 3]", "[0, 2, 4]", "[0, 3, 4]", "[1, 2, 3]", "[1, 2, 4]", "[1, 3, 4]", "[2, 3, 4]");
        List<String> expectedN5K2 = Arrays.asList("[0, 1]", "[0, 2]", "[0, 3]", "[0, 4]", "[1, 2]", "[1, 3]", "[1, 4]", "[2, 3]", "[2, 4]", "[3, 4]");
        TestHelpers.streamEx(() -> StreamEx.ofCombinations(5, 3), ( s) -> Assert.assertEquals(expectedN5K3, s.get().map(Arrays::toString).collect(Collectors.toList())));
        TestHelpers.streamEx(() -> StreamEx.ofCombinations(20, 7), ( s) -> Assert.assertEquals(Optional.empty(), s.get().map(Arrays::toString).distinct(2).findFirst()));
        TestHelpers.streamEx(() -> StreamEx.ofCombinations(7, 20), ( s) -> Assert.assertEquals(Optional.empty(), s.get().map(Arrays::toString).findFirst()));
        TestHelpers.streamEx(() -> StreamEx.ofCombinations(5, 2), ( s) -> Assert.assertEquals(expectedN5K2, s.get().map(Arrays::toString).collect(Collectors.toList())));
        TestHelpers.streamEx(() -> StreamEx.ofCombinations(5, 0), ( s) -> Assert.assertEquals(Arrays.asList("[]"), s.get().map(Arrays::toString).collect(Collectors.toList())));
        TestHelpers.streamEx(() -> StreamEx.ofCombinations(5, 5), ( s) -> Assert.assertEquals(Arrays.asList("[0, 1, 2, 3, 4]"), s.get().map(Arrays::toString).collect(Collectors.toList())));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testOfCombinationsNegativeN() {
        StreamEx.ofCombinations((-1), 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testOfCombinationsNegativeK() {
        StreamEx.ofCombinations(0, (-1));
    }
}

