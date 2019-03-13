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


import Multimap.ContainerType;
import io.vavr.PartialFunction;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.Spliterator;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public abstract class AbstractMultimapTest extends AbstractTraversableTest {
    @Parameterized.Parameter
    public ContainerType containerType;

    // -- construction
    @Test
    public void shouldBeTheSame() {
        assertThat(mapOf(1, 2)).isEqualTo(emptyIntInt().put(1, 2));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldConstructFromEntries() {
        final Multimap<String, Integer> map = mapOfEntries(AbstractMultimapTest.entry("1", 1), AbstractMultimapTest.entry("2", 2), AbstractMultimapTest.entry("3", 3));
        assertThat(map).isEqualTo(this.<String, Integer>emptyMap().put("1", 1).put("2", 2).put("3", 3));
    }

    @Test
    public void shouldConstructFromPairs() {
        final Multimap<String, Integer> map = mapOfPairs("1", 1, "2", 2, "3", 3);
        assertThat(map).isEqualTo(this.<String, Integer>emptyMap().put("1", 1).put("2", 2).put("3", 3));
    }

    @Test
    public void shouldConstructFromJavaStream() {
        final Stream<Integer> javaStream = Stream.of(1, 2, 3);
        final Multimap<String, Integer> map = mapOf(javaStream, String::valueOf, Function.identity());
        assertThat(map).isEqualTo(this.<String, Integer>emptyMap().put("1", 1).put("2", 2).put("3", 3));
    }

    @Test
    public void shouldConstructFromJavaStreamEntries() {
        final Stream<Integer> javaStream = Stream.of(1, 2, 3);
        final Multimap<String, Integer> map = mapOf(javaStream, ( i) -> Tuple.of(String.valueOf(i), i));
        assertThat(map).isEqualTo(this.<String, Integer>emptyMap().put("1", 1).put("2", 2).put("3", 3));
    }

    @Test
    public void shouldConstructFromJavaMap() {
        final Map<String, Integer> source = new HashMap<>();
        source.put("1", 2);
        source.put("3", 4);
        final Multimap<String, Integer> map = mapOf(source);
        assertThat(map).isEqualTo(this.<String, Integer>emptyMap().put("1", 2).put("3", 4));
    }

    // -- asPartialFunction
    @Test
    public void shouldApplyExistingKey() {
        assertThat(emptyIntInt().put(1, 2).asPartialFunction().apply(1)).isEqualTo(io.vavr.collection.HashSet.of(2));
    }

    @Test(expected = NoSuchElementException.class)
    public void shouldApplyNonExistingKey() {
        emptyIntInt().put(1, 2).asPartialFunction().apply(3);
    }

    @Test
    public void shouldImplementPartialFunction() {
        PartialFunction<Integer, Traversable<String>> f = mapOf(1, "1").asPartialFunction();
        assertThat(f.isDefinedAt(1)).isTrue();
        assertThat(f.apply(1).contains("1")).isTrue();
        assertThat(f.isDefinedAt(2)).isFalse();
    }

    // -- asMap
    @Test
    public void shouldConvertToMap() {
        Multimap<Integer, Integer> mm = emptyIntInt().put(1, 2).put(1, 3);
        assertThat(mm.asMap().get(1).get()).isEqualTo(HashSet.of(HashSet, 2, 3));
    }

    // -- biMap
    @Test
    public void shouldBiMapEmpty() {
        assertThat(emptyInt().bimap(( i) -> i + 1, ( o) -> o)).isEqualTo(io.vavr.collection.Vector.empty());
    }

    @Test
    public void shouldBiMapNonEmpty() {
        final Seq<Tuple2<Integer, String>> expected = Stream.of(Tuple.of(2, "1!"), Tuple.of(3, "2!"));
        final Seq<Tuple2<Integer, String>> actual = emptyInt().put(1, "1").put(2, "2").bimap(( i) -> i + 1, ( s) -> s + "!").toStream();
        assertThat(actual).isEqualTo(expected);
    }

    // -- contains
    @Test
    public void shouldFindKey() {
        assertThat(emptyIntInt().put(1, 2).containsKey(1)).isTrue();
        assertThat(emptyIntInt().put(1, 2).containsKey(2)).isFalse();
    }

    @Test
    public void shouldFindValue() {
        assertThat(emptyIntInt().put(1, 2).containsValue(2)).isTrue();
        assertThat(emptyIntInt().put(1, 2).containsValue(1)).isFalse();
    }

    @Test
    public void shouldRecognizeNotContainedKeyValuePair() {
        final Multimap<String, Integer> testee = mapOf("one", 1);
        assertThat(testee.contains(Tuple.of("one", 0))).isFalse();
    }

    @Test
    public void shouldRecognizeContainedKeyValuePair() {
        final Multimap<String, Integer> testee = mapOf("one", 1);
        assertThat(testee.contains(Tuple.of("one", 1))).isTrue();
    }

    // -- equality
    @Test
    public void shouldObeyEqualityConstraints() {
        // sequential collections
        assertThat(emptyMap().equals(HashMultimap.withSeq().empty())).isTrue();
        assertThat(mapOf(1, "a").equals(HashMultimap.withSeq().of(1, "a"))).isTrue();
        assertThat(mapOfPairs(1, "a", 2, "b", 3, "c").equals(HashMultimap.withSeq().of(1, "a", 2, "b", 3, "c"))).isTrue();
        assertThat(mapOfPairs(1, "a", 2, "b", 3, "c").equals(HashMultimap.withSeq().of(3, "c", 2, "b", 1, "a"))).isTrue();
        // other classes
        assertThat(empty().equals(io.vavr.collection.List.empty())).isFalse();
        assertThat(empty().equals(HashMap.empty(HashMap))).isFalse();
        assertThat(empty().equals(io.vavr.collection.HashSet.empty())).isFalse();
        assertThat(empty().equals(LinkedHashMap.empty(LinkedHashMap))).isFalse();
        assertThat(empty().equals(io.vavr.collection.LinkedHashSet.empty())).isFalse();
        assertThat(empty().equals(TreeMap.empty(TreeMap))).isFalse();
        assertThat(empty().equals(TreeSet.empty(TreeSet))).isFalse();
    }

    @Test
    public void shouldIgnoreOrderOfEntriesWhenComparingForEquality() {
        final Multimap<?, ?> map1 = emptyInt().put(1, 'a').put(2, 'b').put(3, 'c');
        final Multimap<?, ?> map2 = emptyInt().put(3, 'c').put(2, 'b').put(1, 'a').remove(2).put(2, 'b');
        assertThat(map1).isEqualTo(map2);
    }

    @Test
    public void shouldHoldEqualsElements() {
        Multimap<Integer, String> multimap = emptyMap();
        multimap = multimap.put(1, "a").put(1, "b").put(1, "b");
        if ((containerType) == (ContainerType.SEQ)) {
            assertThat(multimap.toString()).isEqualTo(((className()) + "((1, a), (1, b), (1, b))"));
        } else {
            assertThat(multimap.toString()).isEqualTo(((className()) + "((1, a), (1, b))"));
        }
    }

    // -- filter
    @Test
    public void shouldBiFilterWork() throws Exception {
        final Multimap<Integer, String> src = mapTabulate(20, ( n) -> Tuple.of((n % 10), Integer.toHexString(n)));
        final Pattern isDigits = Pattern.compile("^\\d+$");
        final Multimap<Integer, String> dst = src.filter(( k, v) -> ((k % 2) == 0) && (isDigits.matcher(v).matches()));
        assertThat(dst).isEqualTo(emptyIntString().put(0, "0").put(2, "2").put(4, "4").put(6, "6").put(6, "10").put(8, "8").put(8, "12"));
    }

    @Test
    public void shouldKeyFilterWork() throws Exception {
        final Multimap<Integer, String> src = mapTabulate(20, ( n) -> Tuple.of((n % 10), Integer.toHexString(n)));
        final Multimap<Integer, String> dst = src.filterKeys(( k) -> (k % 2) == 0);
        assertThat(dst).isEqualTo(emptyIntString().put(0, "0").put(0, "a").put(2, "2").put(2, "c").put(4, "4").put(4, "e").put(6, "6").put(6, "10").put(8, "8").put(8, "12"));
    }

    @Test
    public void shouldValueFilterWork() throws Exception {
        final Multimap<Integer, String> src = mapTabulate(10, ( n) -> Tuple.of((n % 5), Integer.toHexString(n)));
        final Pattern isDigits = Pattern.compile("^\\d+$");
        final Multimap<Integer, String> dst = src.filterValues(( v) -> isDigits.matcher(v).matches());
        assertThat(dst).isEqualTo(emptyIntString().put(0, "0").put(0, "5").put(1, "1").put(1, "6").put(2, "2").put(2, "7").put(3, "3").put(3, "8").put(4, "4").put(4, "9"));
    }

    // -- reject
    @SuppressWarnings("deprecation")
    @Test
    public void shouldBiRejectWork() throws Exception {
        final Multimap<Integer, String> src = mapTabulate(20, ( n) -> Tuple.of((n % 10), Integer.toHexString(n)));
        final Pattern isDigits = Pattern.compile("^\\d+$");
        final Multimap<Integer, String> dst = src.reject(( k, v) -> ((k % 2) == 0) && (isDigits.matcher(v).matches()));
        assertThat(dst).isEqualTo(emptyIntString().put(0, "a").put(1, "1").put(1, "b").put(2, "c").put(3, "3").put(3, "d").put(4, "e").put(5, "5").put(5, "f").put(7, "7").put(7, "11").put(9, "9").put(9, "13"));
    }

    @SuppressWarnings("deprecation")
    @Test
    public void shouldKeyRejectWork() throws Exception {
        final Multimap<Integer, String> src = mapTabulate(20, ( n) -> Tuple.of((n % 10), Integer.toHexString(n)));
        final Multimap<Integer, String> dst = src.rejectKeys(( k) -> (k % 2) == 0);
        assertThat(dst).isEqualTo(emptyIntString().put(1, "1").put(1, "b").put(3, "3").put(3, "d").put(5, "5").put(5, "f").put(7, "7").put(7, "11").put(9, "9").put(9, "13"));
    }

    @SuppressWarnings("deprecation")
    @Test
    public void shouldValueRejectWork() throws Exception {
        final Multimap<Integer, String> src = mapTabulate(20, ( n) -> Tuple.of((n % 10), Integer.toHexString(n)));
        final Pattern isDigits = Pattern.compile("^\\d+$");
        final Multimap<Integer, String> dst = src.rejectValues(( v) -> isDigits.matcher(v).matches());
        assertThat(dst).isEqualTo(emptyIntString().put(0, "a").put(1, "b").put(2, "c").put(3, "d").put(4, "e").put(5, "f"));
    }

    // -- flatMap
    @SuppressWarnings("unchecked")
    @Test
    public void shouldFlatMapUsingBiFunction() {
        final Multimap<Integer, Integer> testee = mapOfTuples(Tuple.of(1, 11), Tuple.of(2, 22), Tuple.of(3, 33));
        final Multimap<String, String> actual = testee.flatMap(( k, v) -> io.vavr.collection.List.of(Tuple.of(String.valueOf(k), String.valueOf(v)), Tuple.of(String.valueOf((k * 10)), String.valueOf((v * 10)))));
        final Multimap<String, String> expected = mapOfTuples(Tuple.of("1", "11"), Tuple.of("10", "110"), Tuple.of("2", "22"), Tuple.of("20", "220"), Tuple.of("3", "33"), Tuple.of("30", "330"));
        assertThat(actual).isEqualTo(expected);
    }

    // -- forEach
    @Test
    public void forEachByKeyValue() {
        final Multimap<Integer, Integer> map = mapOf(1, 2).put(3, 4);
        final int[] result = new int[]{ 0 };
        map.forEach(( k, v) -> result[0] += k + v);
        assertThat(result[0]).isEqualTo(10);
    }

    @Test
    public void forEachByTuple() {
        final Multimap<Integer, Integer> map = mapOf(1, 2).put(3, 4);
        final int[] result = new int[]{ 0 };
        map.forEach(( t) -> result[0] += t._1 + t._2);
        assertThat(result[0]).isEqualTo(10);
    }

    // -- getOrElse
    @Test
    public void shouldReturnDefaultValue() {
        final Multimap<String, String> map = mapOf("1", "a").put("2", "b");
        assertThat(map.getOrElse("3", io.vavr.collection.List.of("3"))).isEqualTo(io.vavr.collection.List.of("3"));
    }

    // -- groupBy
    @Override
    @Test
    public void shouldNonNilGroupByIdentity() {
        final Map<?, ?> actual = of('a', 'b', 'c').groupBy(Function.identity());
        final Map<?, ?> expected = LinkedHashMap.empty(LinkedHashMap).put('a', mapOf(0, 'a')).put('b', mapOf(1, 'b')).put('c', mapOf(2, 'c'));
        assertThat(actual).isEqualTo(expected);
    }

    // -- iterator
    @Test
    public void shouldReturnListWithMappedValues() {
        assertThat(emptyIntInt().put(1, 1).put(2, 2).iterator(( a, b) -> a + b)).isEqualTo(io.vavr.collection.List.of(2, 4));
    }

    // -- keySet
    @SuppressWarnings("unchecked")
    @Test
    public void shouldReturnsKeySet() {
        final Set<Integer> actual = mapOfTuples(Tuple.of(1, 11), Tuple.of(2, 22), Tuple.of(3, 33)).keySet();
        assertThat(actual).isEqualTo(io.vavr.collection.HashSet.of(1, 2, 3));
    }

    // -- map
    @Test
    public void shouldMapEmpty() {
        assertThat(emptyInt().map(Tuple2::_1)).isEqualTo(io.vavr.collection.Vector.empty());
    }

    @Test
    public void shouldMapNonEmpty() {
        final Seq<Integer> expected = io.vavr.collection.Vector.of(1, 2);
        final Seq<Integer> actual = emptyInt().put(1, "1").put(2, "2").map(Tuple2::_1);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldMapComparableValues() {
        final Multimap<Integer, String> map = this.<Integer, String>emptyMap().put(1, "1").put(1, "2").put(2, "3");
        assertThat(map.map(( v) -> v)).isEqualTo(io.vavr.collection.List.of(Tuple.of(1, "1"), Tuple.of(1, "2"), Tuple.of(2, "3")));
    }

    @Test
    public void shouldMapIncomparableValues() {
        final Multimap<Integer, AbstractMultimapTest.Incomparable> map = this.<Integer, AbstractMultimapTest.Incomparable>emptyMap(Comparator.comparing(AbstractMultimapTest.Incomparable::getS)).put(1, new AbstractMultimapTest.Incomparable("1")).put(1, new AbstractMultimapTest.Incomparable("2")).put(2, new AbstractMultimapTest.Incomparable("3"));
        assertThat(map.map(( v) -> v)).isEqualTo(io.vavr.collection.List.of(Tuple.of(1, new AbstractMultimapTest.Incomparable("1")), Tuple.of(1, new AbstractMultimapTest.Incomparable("2")), Tuple.of(2, new AbstractMultimapTest.Incomparable("3"))));
    }

    private static final class Incomparable {
        private String s;

        Incomparable(String s) {
            this.s = s;
        }

        public String getS() {
            return s;
        }

        @Override
        public boolean equals(Object o) {
            return (o == (this)) || ((o instanceof AbstractMultimapTest.Incomparable) && (Objects.equals(s, ((AbstractMultimapTest.Incomparable) (o)).s)));
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(s);
        }
    }

    // -- mapFill
    @SuppressWarnings("unchecked")
    @Test
    public void shouldFillTheSeqCallingTheSupplierInTheRightOrder() {
        final LinkedList<Integer> ints = new LinkedList<>(Arrays.asList(0, 0, 1, 1, 2, 2));
        final Supplier<Tuple2<Long, Float>> s = () -> new Tuple2(ints.remove().longValue(), ints.remove().floatValue());
        final Multimap<Long, Float> actual = mapFill(3, s);
        assertThat(actual).isEqualTo(mapOfTuples(new Tuple2(0L, 0.0F), new Tuple2(1L, 1.0F), new Tuple2(2L, 2.0F)));
    }

    @Test
    public void shouldFillTheSeqWith0Elements() {
        assertThat(mapFill(0, () -> new Tuple2(1, 1))).isEqualTo(empty());
    }

    @Test
    public void shouldFillTheSeqWith0ElementsWhenNIsNegative() {
        assertThat(mapFill((-1), () -> new Tuple2(1, 1))).isEqualTo(empty());
    }

    // -- fill(int, Supplier)
    @Test
    public void shouldReturnManyMapAfterFillWithConstantSupplier() {
        AtomicInteger value = new AtomicInteger(83);
        assertThat(mapFill(17, () -> Tuple.of(7, value.getAndIncrement()))).hasSize(17);
    }

    // -- fill(int, T)
    @Test
    public void shouldReturnEmptyAfterFillWithZeroCount() {
        assertThat(mapFill(0, Tuple.of(7, 83))).isEqualTo(empty());
    }

    @Test
    public void shouldReturnEmptyAfterFillWithNegativeCount() {
        assertThat(mapFill((-1), Tuple.of(7, 83))).isEqualTo(empty());
    }

    @Test
    public void shouldReturnManyMapAfterFillWithConstant() {
        assertThat(mapFill(17, Tuple.of(7, 83))).hasSize(((containerType) == (ContainerType.SEQ) ? 17 : 1));
    }

    // -- mapTabulate
    @SuppressWarnings("unchecked")
    @Test
    public void shouldTabulateTheSeq() {
        final Function<Number, Tuple2<Long, Float>> f = ( i) -> new Tuple2(i.longValue(), i.floatValue());
        final Multimap<Long, Float> map = mapTabulate(3, f);
        assertThat(map).isEqualTo(mapOfTuples(new Tuple2(0L, 0.0F), new Tuple2(1L, 1.0F), new Tuple2(2L, 2.0F)));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldTabulateTheSeqCallingTheFunctionInTheRightOrder() {
        final LinkedList<Integer> ints = new LinkedList<>(Arrays.asList(0, 0, 1, 1, 2, 2));
        final Function<Integer, Tuple2<Long, Float>> f = ( i) -> new Tuple2(ints.remove().longValue(), ints.remove().floatValue());
        final Multimap<Long, Float> map = mapTabulate(3, f);
        assertThat(map).isEqualTo(mapOfTuples(new Tuple2(0L, 0.0F), new Tuple2(1L, 1.0F), new Tuple2(2L, 2.0F)));
    }

    @Test
    public void shouldTabulateTheSeqWith0Elements() {
        assertThat(mapTabulate(0, ( i) -> new Tuple2(i, i))).isEqualTo(empty());
    }

    @Test
    public void shouldTabulateTheSeqWith0ElementsWhenNIsNegative() {
        assertThat(mapTabulate((-1), ( i) -> new Tuple2(i, i))).isEqualTo(empty());
    }

    // -- mapValues
    @Test
    public void shouldReturnModifiedValuesMap() {
        assertThat(emptyIntString().put(1, "1").put(2, "2").mapValues(Integer::parseInt)).isEqualTo(emptyIntInt().put(1, 1).put(2, 2));
    }

    // -- merge
    @Test
    public void shouldMerge() {
        final Multimap<Integer, Integer> m1 = emptyIntInt().put(1, 1).put(2, 2);
        final Multimap<Integer, Integer> m2 = emptyIntInt().put(1, 1).put(4, 4);
        final Multimap<Integer, Integer> m3 = emptyIntInt().put(3, 3).put(4, 4);
        assertThat(emptyIntInt().merge(m2)).isEqualTo(m2);
        assertThat(m2.merge(emptyIntInt())).isEqualTo(m2);
        if ((containerType) == (ContainerType.SEQ)) {
            assertThat(m1.merge(m2)).isEqualTo(emptyIntInt().put(1, 1).put(1, 1).put(2, 2).put(4, 4));
            assertThat(m1.merge(m3)).isEqualTo(emptyIntInt().put(1, 1).put(2, 2).put(3, 3).put(4, 4));
        } else {
            assertThat(m1.merge(m2)).isEqualTo(emptyIntInt().put(1, 1).put(2, 2).put(4, 4));
            assertThat(m1.merge(m3)).isEqualTo(emptyIntInt().put(1, 1).put(2, 2).put(3, 3).put(4, 4));
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldMergeCollisions() {
        final Multimap<Integer, Integer> m1 = emptyIntInt().put(1, 1).put(2, 2);
        final Multimap<Integer, Integer> m2 = emptyIntInt().put(1, 2).put(4, 4);
        final Multimap<Integer, Integer> m3 = emptyIntInt().put(3, 3).put(4, 4);
        assertThat(emptyIntInt().merge(m2, ( s1, s2) -> io.vavr.collection.Iterator.concat(s1, s2))).isEqualTo(m2);
        assertThat(m2.merge(emptyIntInt(), ( s1, s2) -> io.vavr.collection.Iterator.concat(s1, s2))).isEqualTo(m2);
        assertThat(m1.merge(m2, ( s1, s2) -> io.vavr.collection.Iterator.concat(s1, s2))).isEqualTo(emptyIntInt().put(1, 1).put(1, 2).put(2, 2).put(4, 4));
        assertThat(m1.merge(m3, ( s1, s2) -> io.vavr.collection.Iterator.concat(s1, s2))).isEqualTo(emptyIntInt().put(1, 1).put(2, 2).put(3, 3).put(4, 4));
    }

    // -- orElse
    // DEV-Note: IntMultimap converts `other` to multimap
    @Override
    @Test
    public void shouldCaclEmptyOrElseSameOther() {
        Iterable<Integer> other = of(42);
        assertThat(empty().orElse(other)).isEqualTo(other);
    }

    @Test
    public void shouldCaclEmptyOrElseSameSupplier() {
        Iterable<Integer> other = of(42);
        Supplier<Iterable<Integer>> supplier = () -> other;
        assertThat(empty().orElse(supplier)).isEqualTo(other);
    }

    // -- partition
    @Override
    @Test
    @SuppressWarnings("unchecked")
    public void shouldPartitionIntsInOddAndEvenHavingOddAndEvenNumbers() {
        assertThat(of(1, 2, 3, 4).partition(( i) -> (i % 2) != 0)).isEqualTo(Tuple.of(mapOfTuples(Tuple.of(0, 1), Tuple.of(2, 3)), mapOfTuples(Tuple.of(1, 2), Tuple.of(3, 4))));
    }

    // -- put
    @Test
    public void shouldPutTuple() {
        assertThat(emptyIntInt().put(Tuple.of(1, 2))).isEqualTo(emptyIntInt().put(1, 2));
    }

    // -- remove
    @Test
    public void shouldRemoveKey() {
        final Multimap<Integer, Object> src = emptyInt().put(1, 'a').put(2, 'b').put(3, 'c');
        assertThat(src.remove(2)).isEqualTo(emptyInt().put(1, 'a').put(3, 'c'));
        assertThat(src.remove(33)).isSameAs(src);
    }

    // -- replace
    @Test
    public void shouldReplaceEntity() {
        final Multimap<Integer, Object> actual = emptyInt().put(1, "a").put(1, "b").replace(Tuple.of(1, "a"), Tuple.of(1, "c"));
        final Multimap<Integer, Object> expected = emptyInt().put(1, "c").put(1, "b");
        assertThat(actual).isEqualTo(expected);
    }

    // -- removeAll
    @Test
    public void shouldRemoveAllKeys() {
        final Multimap<Integer, Object> src = emptyInt().put(1, 'a').put(2, 'b').put(3, 'c');
        assertThat(src.removeAll(io.vavr.collection.List.of(1, 3))).isEqualTo(emptyInt().put(2, 'b'));
        assertThat(src.removeAll(io.vavr.collection.List.of(33))).isSameAs(src);
        assertThat(src.removeAll(io.vavr.collection.List.empty())).isSameAs(src);
    }

    // -- remove by filter
    @SuppressWarnings("deprecation")
    @Test
    public void shouldBiRemoveWork() throws Exception {
        final Multimap<Integer, String> src = mapTabulate(20, ( n) -> Tuple.of((n % 10), Integer.toHexString(n)));
        final Pattern isDigits = Pattern.compile("^\\d+$");
        final Multimap<Integer, String> dst = src.removeAll(( k, v) -> ((k % 2) == 0) && (isDigits.matcher(v).matches()));
        assertThat(dst).isEqualTo(emptyIntString().put(0, "a").put(1, "1").put(1, "b").put(2, "c").put(3, "3").put(3, "d").put(4, "e").put(5, "5").put(5, "f").put(7, "7").put(7, "11").put(9, "9").put(9, "13"));
    }

    @SuppressWarnings("deprecation")
    @Test
    public void shouldKeyRemoveWork() throws Exception {
        final Multimap<Integer, String> src = mapTabulate(20, ( n) -> Tuple.of((n % 10), Integer.toHexString(n)));
        final Multimap<Integer, String> dst = src.removeKeys(( k) -> (k % 2) == 0);
        assertThat(dst).isEqualTo(emptyIntString().put(1, "1").put(1, "b").put(3, "3").put(3, "d").put(5, "5").put(5, "f").put(7, "7").put(7, "11").put(9, "9").put(9, "13"));
    }

    @SuppressWarnings("deprecation")
    @Test
    public void shouldValueRemoveWork() throws Exception {
        final Multimap<Integer, String> src = mapTabulate(20, ( n) -> Tuple.of((n % 10), Integer.toHexString(n)));
        final Pattern isDigits = Pattern.compile("^\\d+$");
        final Multimap<Integer, String> dst = src.removeValues(( v) -> isDigits.matcher(v).matches());
        assertThat(dst).isEqualTo(emptyIntString().put(0, "a").put(1, "b").put(2, "c").put(3, "d").put(4, "e").put(5, "f"));
    }

    // -- replaceValue
    @Test
    public void shouldReturnSameInstanceIfReplacingCurrentValueWithNonExistingKey() {
        final Multimap<Integer, String> map = mapOf(1, "a").put(2, "b");
        final Multimap<Integer, String> actual = map.replaceValue(3, "?");
        assertThat(actual).isSameAs(map);
    }

    @Test
    public void shouldReplaceCurrentValueForExistingKey() {
        final Multimap<Integer, String> map = mapOf(1, "a").put(2, "b");
        final Multimap<Integer, String> actual = map.replaceValue(2, "c");
        final Multimap<Integer, String> expected = mapOf(1, "a").put(2, "c");
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldReplaceValuesWithNewValueForExistingKey() {
        final Multimap<Integer, String> map = mapOf(1, "a").put(2, "b").put(2, "c");
        final Multimap<Integer, String> actual = map.replaceValue(2, "c");
        final Multimap<Integer, String> expected = mapOf(1, "a").put(2, "c");
        assertThat(actual).isEqualTo(expected);
    }

    // -- replace
    @Test
    public void shouldReplaceCurrentValueForExistingKeyAndEqualOldValue() {
        final Multimap<Integer, String> map = mapOf(1, "a").put(2, "b");
        final Multimap<Integer, String> actual = map.replace(2, "b", "c");
        final Multimap<Integer, String> expected = mapOf(1, "a").put(2, "c");
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldReplaceCurrentValueForKeyWithMultipleValuesAndEqualOldValue() {
        final Multimap<Integer, String> map = mapOf(1, "a").put(2, "b").put(2, "d");
        final Multimap<Integer, String> actual = map.replace(2, "b", "c");
        final Multimap<Integer, String> expected = mapOf(1, "a").put(2, "c").put(2, "d");
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldReturnSameInstanceForExistingKeyAndNonEqualOldValue() {
        final Multimap<Integer, String> map = mapOf(1, "a").put(2, "b");
        final Multimap<Integer, String> actual = map.replace(2, "d", "c");
        assertThat(actual).isSameAs(map);
    }

    @Test
    public void shouldReturnSameInstanceIfReplacingCurrentValueWithOldValueWithNonExistingKey() {
        final Multimap<Integer, String> map = mapOf(1, "a").put(2, "b");
        final Multimap<Integer, String> actual = map.replace(3, "?", "!");
        assertThat(actual).isSameAs(map);
    }

    // - replaceAll
    @Test
    public void shouldReplaceAllValuesWithFunctionResult() {
        final Multimap<Integer, String> map = mapOf(1, "a").put(2, "b").put(2, "c");
        final Multimap<Integer, String> actual = map.replaceAll(( integer, s) -> s + integer);
        final Multimap<Integer, String> expected = mapOf(1, "a1").put(2, "b2").put(2, "c2");
        assertThat(actual).isEqualTo(expected);
    }

    // -- span
    @Override
    @Test
    @SuppressWarnings("unchecked")
    public void shouldSpanNonNil() {
        assertThat(of(0, 1, 2, 3).span(( i) -> i < 2)).isEqualTo(Tuple.of(mapOfTuples(Tuple.of(0, 0), Tuple.of(1, 1)), mapOfTuples(Tuple.of(2, 2), Tuple.of(3, 3))));
    }

    @Override
    @Test
    @SuppressWarnings("unchecked")
    public void shouldSpanAndNotTruncate() {
        assertThat(of(1, 1, 2, 2, 3, 3).span(( x) -> (x % 2) == 1)).isEqualTo(Tuple.of(mapOfTuples(Tuple.of(0, 1), Tuple.of(1, 1)), mapOfTuples(Tuple.of(2, 2), Tuple.of(3, 2), Tuple.of(4, 3), Tuple.of(5, 3))));
        assertThat(of(1, 1, 2, 2, 4, 4).span(( x) -> x == 1)).isEqualTo(Tuple.of(mapOfTuples(Tuple.of(0, 1), Tuple.of(1, 1)), mapOfTuples(Tuple.of(2, 2), Tuple.of(3, 2), Tuple.of(4, 4), Tuple.of(5, 4))));
    }

    // -- spliterator
    @Test
    public void shouldHaveSizedSpliterator() {
        assertThat(of(1, 2, 3).spliterator().hasCharacteristics(((Spliterator.SIZED) | (Spliterator.SUBSIZED)))).isTrue();
    }

    @Test
    public void shouldHaveDistinctSpliterator() {
        assertThat(of(1, 2, 3).spliterator().hasCharacteristics(Spliterator.DISTINCT)).isTrue();
    }

    @Test
    public void shouldReturnExactSizeIfKnownOfSpliterator() {
        assertThat(of(1, 2, 3).spliterator().getExactSizeIfKnown()).isEqualTo(3);
    }

    // -- toJavaMap
    @Test
    public void shouldConvertToJavaMap() {
        final Multimap<String, Integer> vavr = mapOfPairs("1", 1, "2", 2, "3", 3);
        final Map<String, Collection<Integer>> java = javaEmptyMap();
        java.put("1", javaListOf(1));
        java.put("2", javaListOf(2));
        java.put("3", javaListOf(3));
        assertThat(vavr.toJavaMap()).isEqualTo(java);
    }

    // -- toSet
    @Test
    public void shouldReturnEmptySetWhenAskedForTuple2SetOfAnEmptyMap() {
        assertThat(emptyMap().toSet()).isEqualTo(io.vavr.collection.HashSet.empty());
    }

    @Test
    public void shouldReturnTuple2SetOfANonEmptyMap() {
        assertThat(emptyInt().put(1, "1").put(2, "2").toSet()).isEqualTo(io.vavr.collection.HashSet.of(Tuple.of(1, "1"), Tuple.of(2, "2")));
    }

    // -- toString
    @Test
    public void shouldMakeString() {
        assertThat(emptyMap().toString()).isEqualTo(((className()) + "()"));
        assertThat(emptyIntInt().put(1, 2).toString()).isEqualTo(((((className()) + "(") + (Tuple.of(1, 2))) + ")"));
    }

    // -- transform
    @Test
    public void shouldTransform() {
        final Multimap<?, ?> actual = emptyIntInt().put(1, 11).transform(( map) -> map.put(2, 22));
        assertThat(actual).isEqualTo(emptyIntInt().put(1, 11).put(2, 22));
    }

    // -- unzip
    @Test
    public void shouldUnzipNil() {
        assertThat(emptyMap().unzip(( x) -> Tuple.of(x, x))).isEqualTo(Tuple.of(Stream.empty(), Stream.empty()));
        assertThat(emptyMap().unzip(( k, v) -> Tuple.of(Tuple.of(k, v), Tuple.of(k, v)))).isEqualTo(Tuple.of(Stream.empty(), Stream.empty()));
    }

    @Test
    public void shouldUnzipNonNil() {
        final Multimap<Integer, Integer> map = emptyIntInt().put(0, 0).put(1, 1);
        final Tuple actual = map.unzip(( entry) -> Tuple.of(entry._1, (entry._2 + 1)));
        final Tuple expected = Tuple.of(Stream.of(0, 1), Stream.of(1, 2));
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldUnzip3Nil() {
        assertThat(emptyMap().unzip3(( x) -> Tuple.of(x, x, x))).isEqualTo(Tuple.of(Stream.empty(), Stream.empty(), Stream.empty()));
        assertThat(emptyMap().unzip3(( k, v) -> Tuple.of(Tuple.of(k, v), Tuple.of(k, v), Tuple.of(k, v)))).isEqualTo(Tuple.of(Stream.empty(), Stream.empty(), Stream.empty()));
    }

    @Test
    public void shouldUnzip3NonNil() {
        final Multimap<Integer, Integer> map = emptyIntInt().put(0, 0).put(1, 1);
        final Tuple actual = map.unzip3(( entry) -> Tuple.of(entry._1, (entry._2 + 1), (entry._2 + 5)));
        final Tuple expected = Tuple.of(Stream.of(0, 1), Stream.of(1, 2), Stream.of(5, 6));
        assertThat(actual).isEqualTo(expected);
    }

    // -- zip
    @Test
    public void shouldZipNils() {
        final Seq<Tuple2<Tuple2<Integer, Integer>, Object>> actual = emptyIntInt().zip(io.vavr.collection.List.empty());
        assertThat(actual).isEqualTo(Stream.empty());
    }

    @Test
    public void shouldZipEmptyAndNonNil() {
        final Seq<Tuple2<Tuple2<Integer, Integer>, Integer>> actual = emptyIntInt().zip(io.vavr.collection.List.of(1));
        assertThat(actual).isEqualTo(Stream.empty());
    }

    @Test
    public void shouldZipNonEmptyAndNil() {
        final Seq<Tuple2<Tuple2<Integer, Integer>, Object>> actual = emptyIntInt().put(0, 1).zip(io.vavr.collection.List.empty());
        assertThat(actual).isEqualTo(Stream.empty());
    }

    @Test
    public void shouldZipNonNilsIfThisIsSmaller() {
        final Seq<Tuple2<Tuple2<Integer, Integer>, Integer>> actual = emptyIntInt().put(0, 0).put(1, 1).zip(io.vavr.collection.List.of(5, 6, 7));
        assertThat(actual).isEqualTo(Stream.of(Tuple.of(Tuple.of(0, 0), 5), Tuple.of(Tuple.of(1, 1), 6)));
    }

    @Test
    public void shouldZipNonNilsIfThatIsSmaller() {
        final Seq<Tuple2<Tuple2<Integer, Integer>, Integer>> actual = emptyIntInt().put(0, 0).put(1, 1).put(2, 2).zip(io.vavr.collection.List.of(5, 6));
        assertThat(actual).isEqualTo(Stream.of(Tuple.of(Tuple.of(0, 0), 5), Tuple.of(Tuple.of(1, 1), 6)));
    }

    @Test
    public void shouldZipNonNilsOfSameSize() {
        final Seq<Tuple2<Tuple2<Integer, Integer>, Integer>> actual = emptyIntInt().put(0, 0).put(1, 1).put(2, 2).zip(io.vavr.collection.List.of(5, 6, 7));
        assertThat(actual).isEqualTo(Stream.of(Tuple.of(Tuple.of(0, 0), 5), Tuple.of(Tuple.of(1, 1), 6), Tuple.of(Tuple.of(2, 2), 7)));
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowIfZipWithThatIsNull() {
        emptyMap().zip(null);
    }

    // -- zipWithIndex
    @Test
    public void shouldZipNilWithIndex() {
        assertThat(emptyMap().zipWithIndex()).isEqualTo(Stream.empty());
    }

    @Test
    public void shouldZipNonNilWithIndex() {
        final Seq<Tuple2<Tuple2<Integer, Integer>, Integer>> actual = emptyIntInt().put(0, 0).put(1, 1).put(2, 2).zipWithIndex();
        assertThat(actual).isEqualTo(Stream.of(Tuple.of(Tuple.of(0, 0), 0), Tuple.of(Tuple.of(1, 1), 1), Tuple.of(Tuple.of(2, 2), 2)));
    }

    // -- zipAll
    @Test
    public void shouldZipAllNils() {
        final Seq<Tuple2<Tuple2<Integer, Integer>, Object>> actual = emptyIntInt().zipAll(empty(), null, null);
        assertThat(actual).isEqualTo(Stream.empty());
    }

    @Test
    public void shouldZipAllEmptyAndNonNil() {
        final Seq<Tuple2<Tuple2<Integer, Integer>, Object>> actual = emptyIntInt().zipAll(io.vavr.collection.List.of(1), null, null);
        assertThat(actual).isEqualTo(Stream.of(Tuple.of(null, 1)));
    }

    @Test
    public void shouldZipAllNonEmptyAndNil() {
        final Seq<Tuple2<Tuple2<Integer, Integer>, Object>> actual = emptyIntInt().put(0, 1).zipAll(empty(), null, null);
        assertThat(actual).isEqualTo(Stream.of(Tuple.of(Tuple.of(0, 1), null)));
    }

    @Test
    public void shouldZipAllNonNilsIfThisIsSmaller() {
        final Seq<Tuple2<Tuple2<Integer, Object>, String>> actual = this.<Integer, Object>emptyMap().put(1, 1).put(2, 2).zipAll(of("a", "b", "c"), Tuple.of(9, 10), "z");
        final Seq<Tuple2<Tuple2<Object, Object>, String>> expected = Stream.of(Tuple.of(Tuple.of(1, 1), "a"), Tuple.of(Tuple.of(2, 2), "b"), Tuple.of(Tuple.of(9, 10), "c"));
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldZipAllNonNilsIfThisIsMoreSmaller() {
        final Seq<Tuple2<Tuple2<Integer, Object>, String>> actual = this.<Integer, Object>emptyMap().put(1, 1).put(2, 2).zipAll(of("a", "b", "c", "d"), Tuple.of(9, 10), "z");
        final Seq<Tuple2<Tuple2<Object, Object>, String>> expected = Stream.of(Tuple.of(Tuple.of(1, 1), "a"), Tuple.of(Tuple.of(2, 2), "b"), Tuple.of(Tuple.of(9, 10), "c"), Tuple.of(Tuple.of(9, 10), "d"));
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldZipAllNonNilsIfThatIsSmaller() {
        final Seq<Tuple2<Tuple2<Integer, Object>, String>> actual = this.<Integer, Object>emptyMap().put(1, 1).put(2, 2).put(3, 3).zipAll(this.of("a", "b"), Tuple.of(9, 10), "z");
        final Seq<Tuple2<Tuple2<Object, Object>, String>> expected = Stream.of(Tuple.of(Tuple.of(1, 1), "a"), Tuple.of(Tuple.of(2, 2), "b"), Tuple.of(Tuple.of(3, 3), "z"));
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldZipAllNonNilsIfThatIsMoreSmaller() {
        final Seq<Tuple2<Tuple2<Integer, Object>, String>> actual = this.<Integer, Object>emptyMap().put(1, 1).put(2, 2).put(3, 3).put(4, 4).zipAll(of("a", "b"), Tuple.of(9, 10), "z");
        final Seq<Tuple2<Tuple2<Object, Object>, String>> expected = Stream.of(Tuple.of(Tuple.of(1, 1), "a"), Tuple.of(Tuple.of(2, 2), "b"), Tuple.of(Tuple.of(3, 3), "z"), Tuple.of(Tuple.of(4, 4), "z"));
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldZipAllNonNilsOfSameSize() {
        final Seq<Tuple2<Tuple2<Integer, Object>, String>> actual = this.<Integer, Object>emptyMap().put(1, 1).put(2, 2).put(3, 3).zipAll(of("a", "b", "c"), Tuple.of(9, 10), "z");
        final Seq<Tuple2<Tuple2<Object, Object>, String>> expected = Stream.of(Tuple.of(Tuple.of(1, 1), "a"), Tuple.of(Tuple.of(2, 2), "b"), Tuple.of(Tuple.of(3, 3), "c"));
        assertThat(actual).isEqualTo(expected);
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowIfZipAllWithThatIsNull() {
        emptyMap().zipAll(null, null, null);
    }

    @Override
    @Test
    public void shouldCreateSeqOfSeqUsingCons() {
        // this Traversable test is not suited for Multimaps:
        // io.vavr.collection.List$Nil cannot be cast to java.lang.Comparable
    }

    @Override
    @Test
    public void shouldConvertToJavaArrayWithTypeHintPrimitiveVoid() {
        // this Value test is not suited for Multimaps:
        // java.lang.NullPointerException at io.vavr.collection.Comparators.lambda$naturalComparator
    }

    @Override
    @Test
    public void shouldCollectUsingMultimap() {
        // this Traversable test is not suited for Multimaps:
        // java.lang.ClassCastException: io.vavr.collection.List$Cons cannot be cast to java.lang.Comparable
    }
}

