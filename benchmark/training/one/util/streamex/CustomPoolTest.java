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
import java.util.BitSet;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;


/**
 *
 *
 * @author Tagir Valeev
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CustomPoolTest {
    final ForkJoinPool pool = new ForkJoinPool(3);

    @Test(expected = IllegalStateException.class)
    public void testCheckThreadSequential() {
        StreamEx.of("a", "b").peek(this::checkThread).joining();
    }

    @Test(expected = IllegalStateException.class)
    public void testCheckThreadParallel() {
        StreamEx.of("a", "b").parallel().peek(this::checkThread).joining();
    }

    @Test
    public void testStreamEx() {
        StreamEx.of("a", "b", "c").parallel(pool).forEach(this::checkThread);
        Assert.assertEquals(Arrays.asList(1, 2), StreamEx.of("a", "bb").parallel(pool).peek(this::checkThread).map(String::length).toList());
        Assert.assertEquals("a", StreamEx.of("a").parallel(pool).peek(this::checkThread).findAny().get());
        Assert.assertEquals("a", StreamEx.of("a", "b").parallel(pool).peek(this::checkThread).findFirst().get());
        Assert.assertTrue(StreamEx.of("a", "b").parallel(pool).peek(this::checkThread).anyMatch("a"::equals));
        Assert.assertFalse(StreamEx.of("a", "b").parallel(pool).peek(this::checkThread).allMatch("a"::equals));
        Assert.assertFalse(StreamEx.of("a", "b").parallel(pool).peek(this::checkThread).noneMatch("a"::equals));
        Assert.assertEquals(Arrays.asList("b", "c"), StreamEx.of("a", "b", "c").parallel(pool).peek(this::checkThread).skip(1).collect(Collectors.toList()));
        Assert.assertEquals(6, StreamEx.of("a", "bb", "ccc").parallel(pool).peek(this::checkThread).collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).length());
        Assert.assertArrayEquals(new String[]{ "a", "b", "c" }, StreamEx.of("a", "b", "c").parallel(pool).peek(this::checkThread).toArray(String[]::new));
        Assert.assertArrayEquals(new Object[]{ "a", "b", "c" }, StreamEx.of("a", "b", "c").parallel(pool).peek(this::checkThread).toArray());
        Assert.assertEquals("{ccc={bb={a={}}}}", StreamEx.of("a", "bb", "ccc").parallel(pool).peek(this::checkThread).foldLeft(Collections.emptyMap(), (Map<String, Object> acc,String v) -> Collections.singletonMap(v, acc)).toString());
        Assert.assertEquals(1000, IntStreamEx.constant(1, 1000).boxed().parallel(pool).peek(this::checkThread).foldLeft(0, Integer::sum).intValue());
        Assert.assertEquals(2, StreamEx.of("aa", "bbb", "cccc").parallel(pool).peek(this::checkThread).filter(( x) -> (x.length()) > 2).count());
        Assert.assertEquals("bbbcccc", StreamEx.of("aa", "bbb", "cccc").parallel(pool).peek(this::checkThread).filter(( x) -> (x.length()) > 2).reduce(String::concat).get());
        Assert.assertEquals("bbbcccc", StreamEx.of("aa", "bbb", "cccc").parallel(pool).peek(this::checkThread).filter(( x) -> (x.length()) > 2).reduce("", String::concat));
        Assert.assertEquals(7, ((int) (StreamEx.of("aa", "bbb", "cccc").parallel(pool).peek(this::checkThread).filter(( x) -> (x.length()) > 2).reduce(0, ( x, s) -> x + (s.length()), Integer::sum))));
        Assert.assertEquals("aabbbcccc", StreamEx.of("aa", "bbb", "cccc").parallel(pool).peek(this::checkThread).foldLeft("", String::concat));
        Assert.assertEquals(Arrays.asList(1, 2, 3), StreamEx.of(1, 2, 3).parallel(pool).peek(this::checkThread).toListAndThen(( list) -> {
            this.checkThread(list);
            return list;
        }));
        Assert.assertEquals(new HashSet(Arrays.asList(1, 2, 3)), StreamEx.of(1, 2, 3).parallel(pool).peek(this::checkThread).toSetAndThen(( list) -> {
            this.checkThread(list);
            return list;
        }));
        Assert.assertEquals(Collections.singletonMap(1, 3L), StreamEx.of(1, 1, 1).parallel(pool).peek(this::checkThread).runLengths().toMap());
    }

    @Test
    public void testEntryStream() {
        EntryStream.of("a", 1).parallel(pool).forEach(this::checkThread);
        Assert.assertEquals(Integer.valueOf(1), EntryStream.of("a", 1).parallel(pool).peek(this::checkThread).toMap().get("a"));
        Assert.assertEquals(Integer.valueOf(1), EntryStream.of("a", 1).parallel(pool).peek(this::checkThread).findAny(( e) -> e.getKey().equals("a")).get().getValue());
        Assert.assertEquals(Integer.valueOf(1), EntryStream.of("a", 1).parallel(pool).peek(this::checkThread).findFirst(( e) -> e.getKey().equals("a")).get().getValue());
        Assert.assertTrue(EntryStream.of("a", 1).parallel(pool).peek(this::checkThread).anyMatch(( e) -> e.getKey().equals("a")));
        Assert.assertTrue(EntryStream.of("a", 1).parallel(pool).peek(this::checkThread).allMatch(( e) -> e.getKey().equals("a")));
        Assert.assertFalse(EntryStream.of("a", 1).parallel(pool).peek(this::checkThread).noneMatch(( e) -> e.getKey().equals("a")));
        Assert.assertEquals(2, EntryStream.of("a", 1, "b", 2, "c", 3).parallel(pool).peek(this::checkThread).filterValues(( v) -> v > 1).count());
        List<Integer> res = new ArrayList<>();
        EntryStream.of("a", 1, "b", 2, "c", 3).parallel(pool).peek(this::checkThread).filterValues(( v) -> v > 1).forEachOrdered(( entry) -> res.add(entry.getValue()));
        Assert.assertEquals(Arrays.asList(2, 3), res);
        Assert.assertEquals(2L, ((long) (EntryStream.of("a", 1, "b", 2, "c", 3).parallel(pool).peek(this::checkThread).filterValues(( v) -> v > 1).collect(Collectors.counting()))));
        Assert.assertEquals(6, ((int) (EntryStream.of("a", 1, "b", 2, "c", 3).parallel(pool).peek(this::checkThread).reduce(0, ( sum, e) -> sum + (e.getValue()), Integer::sum))));
        Assert.assertEquals(Arrays.asList(1, 2, 3), EntryStream.of("a", 1, "b", 2, "c", 3).parallel(pool).peek(this::checkThread).collect(ArrayList::new, (List<Integer> list,Map.Entry<String, Integer> e) -> list.add(e.getValue()), List::addAll));
        @SuppressWarnings("unchecked")
        Map.Entry<String, Integer>[] array = EntryStream.of("a", 1, "b", 2, "c", 3).parallel(pool).peek(this::checkThread).filterValues(( v) -> v > 1).toArray(Map.Entry[]::new);
        Assert.assertEquals(2, array.length);
        Assert.assertEquals(new AbstractMap.SimpleEntry<>("b", 2), array[0]);
        Assert.assertEquals(new AbstractMap.SimpleEntry<>("c", 3), array[1]);
        List<Map.Entry<String, Integer>> list = EntryStream.of("a", 1, "b", 2, "c", 3).parallel(pool).peek(this::checkThread).filterValues(( v) -> v > 1).toListAndThen(( l) -> {
            this.checkThread(l);
            return l;
        });
        Assert.assertEquals(Arrays.asList(array), list);
        Map<String, Integer> map = EntryStream.of("a", 1, "b", 2, "c", 3).parallel(pool).peek(this::checkThread).filterValues(( v) -> v > 1).toMapAndThen(( m) -> {
            this.checkThread(m);
            return m;
        });
        Assert.assertEquals(EntryStream.of("b", 2, "c", 3).toMap(), map);
        Assert.assertEquals(new AbstractMap.SimpleEntry("abc", 6), EntryStream.of("a", 1, "b", 2, "c", 3).parallel(pool).peek(this::checkThread).reduce(( e1, e2) -> new SimpleEntry<>(((e1.getKey()) + (e2.getKey())), ((e1.getValue()) + (e2.getValue())))).orElse(null));
        Assert.assertEquals(new AbstractMap.SimpleEntry("abc", 6), EntryStream.of("a", 1, "b", 2, "c", 3).parallel(pool).peek(this::checkThread).reduce(new AbstractMap.SimpleEntry("", 0), ( e1, e2) -> new SimpleEntry<>(((e1.getKey()) + (e2.getKey())), ((e1.getValue()) + (e2.getValue())))));
    }

    @Test
    public void testIntStreamEx() {
        IntStreamEx.range(0, 4).parallel(pool).forEach(this::checkThread);
        Assert.assertEquals(6, IntStreamEx.range(0, 4).parallel(pool).peek(this::checkThread).sum());
        Assert.assertEquals(3, IntStreamEx.range(0, 4).parallel(pool).peek(this::checkThread).max().getAsInt());
        Assert.assertEquals(0, IntStreamEx.range(0, 4).parallel(pool).peek(this::checkThread).min().getAsInt());
        Assert.assertEquals(1.5, IntStreamEx.range(0, 4).parallel(pool).peek(this::checkThread).average().getAsDouble(), 1.0E-6);
        Assert.assertEquals(4, IntStreamEx.range(0, 4).parallel(pool).peek(this::checkThread).summaryStatistics().getCount());
        Assert.assertArrayEquals(new int[]{ 1, 2, 3 }, IntStreamEx.range(0, 5).parallel(pool).peek(this::checkThread).skip(1).limit(3).toArray());
        Assert.assertEquals(6, IntStreamEx.of(1, 2, 3).parallel(pool).peek(this::checkThread).reduce(Integer::sum).getAsInt());
        Assert.assertTrue(IntStreamEx.of(1, 2, 3).parallel(pool).peek(this::checkThread).has(1));
        Assert.assertTrue(IntStreamEx.of(1, 2, 3).parallel(pool).peek(this::checkThread).anyMatch(( x) -> x == 2));
        Assert.assertFalse(IntStreamEx.of(1, 2, 3).parallel(pool).peek(this::checkThread).allMatch(( x) -> x == 2));
        Assert.assertFalse(IntStreamEx.of(1, 2, 3).parallel(pool).peek(this::checkThread).noneMatch(( x) -> x == 2));
        Assert.assertEquals(6, IntStreamEx.of(1, 2, 3).parallel(pool).peek(this::checkThread).reduce(1, ( a, b) -> a * b));
        Assert.assertEquals(2, IntStreamEx.of(1, 2, 3).parallel(pool).peek(this::checkThread).atLeast(2).count());
        Assert.assertEquals(2, IntStreamEx.of(1, 2, 3).parallel(pool).peek(this::checkThread).findAny(( x) -> (x % 2) == 0).getAsInt());
        Assert.assertEquals(2, IntStreamEx.of(1, 2, 3).parallel(pool).peek(this::checkThread).findFirst(( x) -> (x % 2) == 0).getAsInt());
        List<Integer> res = new ArrayList<>();
        IntStreamEx.of(1, 5, 10, Integer.MAX_VALUE).parallel(pool).peek(this::checkThread).map(( x) -> x * 2).forEachOrdered(res::add);
        // noinspection NumericOverflow
        Assert.assertEquals(Arrays.asList(2, 10, 20, ((Integer.MAX_VALUE) * 2)), res);
        Assert.assertArrayEquals(new int[]{ 1, 3, 6, 10 }, IntStreamEx.of(1, 2, 3, 4).parallel(pool).peek(this::checkThread).scanLeft(( a, b) -> {
            checkThread(b);
            return a + b;
        }));
    }

    @Test
    public void testLongStreamEx() {
        LongStreamEx.range(0, 4).parallel(pool).forEach(this::checkThread);
        Assert.assertEquals(999999000000L, IntStreamEx.range(1000000).parallel(pool).peek(this::checkThread).asLongStream().map(( x) -> x * 2).sum());
        Assert.assertEquals(6, LongStreamEx.range(0, 4).parallel(pool).peek(this::checkThread).sum());
        Assert.assertEquals(3, LongStreamEx.range(0, 4).parallel(pool).peek(this::checkThread).max().getAsLong());
        Assert.assertEquals(0, LongStreamEx.range(0, 4).parallel(pool).peek(this::checkThread).min().getAsLong());
        Assert.assertEquals(1.5, LongStreamEx.range(0, 4).parallel(pool).peek(this::checkThread).average().getAsDouble(), 1.0E-6);
        Assert.assertEquals(4, LongStreamEx.range(0, 4).parallel(pool).peek(this::checkThread).summaryStatistics().getCount());
        Assert.assertArrayEquals(new long[]{ 1, 2, 3 }, LongStreamEx.range(0, 5).parallel(pool).peek(this::checkThread).skip(1).limit(3).toArray());
        Assert.assertEquals(6, LongStreamEx.of(1, 2, 3).parallel(pool).peek(this::checkThread).reduce(Long::sum).getAsLong());
        Assert.assertTrue(LongStreamEx.of(1, 2, 3).parallel(pool).peek(this::checkThread).has(1));
        Assert.assertTrue(LongStreamEx.of(1, 2, 3).parallel(pool).peek(this::checkThread).anyMatch(( x) -> x == 2));
        Assert.assertFalse(LongStreamEx.of(1, 2, 3).parallel(pool).peek(this::checkThread).allMatch(( x) -> x == 2));
        Assert.assertFalse(LongStreamEx.of(1, 2, 3).parallel(pool).peek(this::checkThread).noneMatch(( x) -> x == 2));
        Assert.assertEquals(6, LongStreamEx.of(1, 2, 3).parallel(pool).peek(this::checkThread).reduce(1, ( a, b) -> a * b));
        Assert.assertEquals(2, LongStreamEx.of(1, 2, 3).parallel(pool).peek(this::checkThread).atLeast(2).count());
        Assert.assertEquals(2, LongStreamEx.of(1, 2, 3).parallel(pool).peek(this::checkThread).findAny(( x) -> (x % 2) == 0).getAsLong());
        Assert.assertEquals(2, LongStreamEx.of(1, 2, 3).parallel(pool).peek(this::checkThread).findFirst(( x) -> (x % 2) == 0).getAsLong());
        List<Long> res = new ArrayList<>();
        LongStreamEx.of(1, 5, 10, Integer.MAX_VALUE).parallel(pool).peek(this::checkThread).map(( x) -> x * 2).forEachOrdered(res::add);
        Assert.assertEquals(Arrays.asList(2L, 10L, 20L, ((Integer.MAX_VALUE) * 2L)), res);
        Assert.assertArrayEquals(new long[]{ 1, 3, 6, 10 }, LongStreamEx.of(1, 2, 3, 4).parallel(pool).peek(this::checkThread).scanLeft(( a, b) -> {
            checkThread(b);
            return a + b;
        }));
    }

    @Test
    public void testDoubleStreamEx() {
        LongStreamEx.range(0, 4).asDoubleStream().parallel(pool).forEach(this::checkThread);
        Assert.assertEquals(6, IntStreamEx.range(0, 4).parallel(pool).peek(this::checkThread).asDoubleStream().sum(), 0);
        Assert.assertEquals(3, IntStreamEx.range(0, 4).parallel(pool).peek(this::checkThread).asDoubleStream().max().getAsDouble(), 0);
        Assert.assertEquals(0, IntStreamEx.range(0, 4).parallel(pool).peek(this::checkThread).asDoubleStream().min().getAsDouble(), 0);
        Assert.assertEquals(1.5, IntStreamEx.range(0, 4).parallel(pool).peek(this::checkThread).asDoubleStream().average().getAsDouble(), 1.0E-6);
        Assert.assertEquals(4, IntStreamEx.range(0, 4).parallel(pool).peek(this::checkThread).asDoubleStream().summaryStatistics().getCount());
        Assert.assertArrayEquals(new double[]{ 1, 2, 3 }, IntStreamEx.range(0, 5).asDoubleStream().skip(1).limit(3).parallel(pool).peek(this::checkThread).toArray(), 0.0);
        Assert.assertEquals(6.0, DoubleStreamEx.of(1.0, 2.0, 3.0).parallel(pool).peek(this::checkThread).reduce(Double::sum).getAsDouble(), 0.0);
        Assert.assertTrue(DoubleStreamEx.of(1, 2, 3).parallel(pool).peek(this::checkThread).anyMatch(( x) -> x == 2));
        Assert.assertFalse(DoubleStreamEx.of(1, 2, 3).parallel(pool).peek(this::checkThread).allMatch(( x) -> x == 2));
        Assert.assertFalse(DoubleStreamEx.of(1, 2, 3).parallel(pool).peek(this::checkThread).noneMatch(( x) -> x == 2));
        Assert.assertEquals(6.0, DoubleStreamEx.of(1, 2, 3).parallel(pool).peek(this::checkThread).reduce(1, ( a, b) -> a * b), 0.0);
        Assert.assertEquals(2, DoubleStreamEx.of(1, 2, 3).parallel(pool).peek(this::checkThread).atLeast(2.0).count());
        Assert.assertEquals(2.0, DoubleStreamEx.of(1, 2, 3).parallel(pool).peek(this::checkThread).findAny(( x) -> (x % 2) == 0).getAsDouble(), 0.0);
        Assert.assertEquals(2.0, DoubleStreamEx.of(1, 2, 3).parallel(pool).peek(this::checkThread).findFirst(( x) -> (x % 2) == 0).getAsDouble(), 0.0);
        List<Double> res = new ArrayList<>();
        DoubleStreamEx.of(1.0, 2.0, 3.5, 4.5).parallel(pool).peek(this::checkThread).map(( x) -> x * 2).forEachOrdered(res::add);
        Assert.assertEquals(Arrays.asList(2.0, 4.0, 7.0, 9.0), res);
        Assert.assertArrayEquals(new double[]{ 1, 3, 6, 10 }, DoubleStreamEx.of(1, 2, 3, 4).parallel(pool).peek(this::checkThread).scanLeft(( a, b) -> {
            checkThread(b);
            return a + b;
        }), 0.0);
    }

    @Test
    public void testPairMap() {
        BitSet bits = IntStreamEx.range(3, 199).toBitSet();
        IntStreamEx.range(200).parallel(pool).filter(( i) -> {
            checkThread(i);
            return i > 2;
        }).boxed().pairMap(AbstractMap.SimpleEntry::new).forEach(( p) -> {
            checkThread(p);
            assertEquals(1, ((p.getValue()) - (p.getKey())));
            assertTrue(p.getKey().toString(), bits.get(p.getKey()));
            bits.clear(p.getKey());
        });
    }

    @Test
    public void testShortCircuit() {
        AtomicInteger counter = new AtomicInteger(0);
        Assert.assertEquals(Optional.empty(), IntStreamEx.range(0, 10000).boxed().parallel(pool).peek(this::checkThread).peek(( t) -> counter.incrementAndGet()).collect(MoreCollectors.onlyOne()));
        Assert.assertTrue(((counter.get()) < 10000));
        counter.set(0);
        Assert.assertEquals(Optional.empty(), IntStreamEx.range(0, 10000).boxed().mapToEntry(( x) -> x).parallel(pool).peek(this::checkThread).peek(( t) -> counter.incrementAndGet()).collect(MoreCollectors.onlyOne()));
        Assert.assertTrue(((counter.get()) < 10000));
    }
}

