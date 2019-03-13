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


import java.io.StringReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Spliterator;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests/examples for StreamEx.headTail() method
 *
 * @author Tagir Valeev
 */
public class StreamExHeadTailTest {
    // ///////////////////////
    // Tests
    @Test
    public void testHeadTailRecursive() {
        TestHelpers.streamEx(() -> StreamEx.iterate(2, ( x) -> x + 1), ( s) -> Assert.assertEquals(Arrays.asList(2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97), s.get().chain(StreamExHeadTailTest::sieve).takeWhile(( x) -> x < 100).toList()));
        TestHelpers.emptyStreamEx(Integer.class, ( s) -> {
            Assert.assertEquals(0L, s.get().headTail(( first, head) -> {
                throw new IllegalStateException();
            }).count());
            Assert.assertFalse(s.get().headTail(( first, head) -> {
                throw new IllegalStateException();
            }).findFirst().isPresent());
        });
        TestHelpers.streamEx(() -> IntStreamEx.range(100).boxed(), ( s) -> Assert.assertEquals(IntStreamEx.rangeClosed(99, 0, (-1)).boxed().toList(), StreamExHeadTailTest.reverse(s.get()).toList()));
        TestHelpers.streamEx(() -> IntStreamEx.range(100).boxed(), ( s) -> Assert.assertEquals(IntStreamEx.rangeClosed(99, 0, (-1)).boxed().toList(), StreamExHeadTailTest.reverseTSO(s.get()).toList()));
        TestHelpers.streamEx(() -> StreamEx.of(1, 2), ( s) -> Assert.assertEquals(0, s.get().headTail(( head, stream) -> null).count()));
        TestHelpers.streamEx(() -> StreamEx.iterate(1, ( x) -> x + 1), ( s) -> Assert.assertEquals(Arrays.asList(1, 3, 6, 10, 15, 21, 28, 36, 45, 55, 66, 78, 91), s.get().chain(StreamExHeadTailTest.scanLeft(Integer::sum)).takeWhile(( x) -> x < 100).toList()));
        TestHelpers.streamEx(() -> StreamEx.iterate(1, ( x) -> x + 1), ( s) -> Assert.assertEquals(IntStreamEx.range(1, 100).boxed().toList(), StreamExHeadTailTest.takeWhile(s.get(), ( x) -> x < 100).toList()));
        TestHelpers.streamEx(() -> StreamEx.iterate(1, ( x) -> x + 1), ( s) -> Assert.assertEquals(IntStreamEx.rangeClosed(1, 100).boxed().toList(), s.get().chain(( str) -> takeWhileClosed(str, ( x) -> x < 100)).toList()));
        TestHelpers.streamEx(() -> IntStreamEx.range(1000).boxed(), ( s) -> Assert.assertEquals(IntStreamEx.range(0, 1000, 20).boxed().toList(), StreamExHeadTailTest.every(s.get(), 20).toList()));
        // http://stackoverflow.com/q/34395943/4856258
        int[] input = new int[]{ 1, 2, 3, -1, 3, -10, 9, 100, 1, 100, 0 };
        AtomicInteger counter = new AtomicInteger();
        Assert.assertEquals(5, IntStreamEx.of(input).peek(( x) -> counter.incrementAndGet()).boxed().chain(StreamExHeadTailTest.scanLeft(Integer::sum)).indexOf(( x) -> x < 0).getAsLong());
        Assert.assertEquals(6, counter.get());
        Assert.assertEquals(4, ((int) (StreamExHeadTailTest.firstMatchingOrFirst(StreamEx.of(1, 2, 3, 4, 5), ( x) -> x > 3))));
        Assert.assertEquals(1, ((int) (StreamExHeadTailTest.firstMatchingOrFirst(StreamEx.of(1, 2, 3, 4, 5), ( x) -> x > 5))));
        Assert.assertEquals(1, ((int) (StreamExHeadTailTest.firstMatchingOrFirst(StreamEx.of(1, 2, 3, 4, 5), ( x) -> x > 0))));
        Assert.assertEquals(Arrays.asList(1, 2, 3, 4, 5, 1, 2, 3, 4, 5, 1, 2, 3, 4, 5, 1, 2, 3), StreamExHeadTailTest.cycle(StreamEx.of(1, 2, 3, 4, 5)).limit(18).toList());
        Assert.assertEquals(Arrays.asList(1, 2, 3, 4, 5, 1, 2, 3, 4, 5, 1, 2, 3, 4, 5), StreamExHeadTailTest.cycleTSO(StreamEx.of(1, 2, 3, 4, 5), 3).toList());
        Assert.assertEquals(Arrays.asList(1, 2, 3, 4, 5, 5, 4, 3, 2, 1), StreamExHeadTailTest.mirror(StreamEx.of(1, 2, 3, 4, 5)).toList());
        Assert.assertEquals(Arrays.asList(9, 13, 17), StreamEx.of(1, 3, 5, 7, 9).headTail(( head, tail) -> tail.pairMap(( a, b) -> (a + b) + head)).toList());
        Assert.assertEquals("[[0, 1, 2, 3], [4, 5, 6, 7], [8, 9, 10, 11], [12, 13, 14, 15], [16, 17, 18, 19]]", StreamExHeadTailTest.batches(IntStreamEx.range(20).boxed(), 4).toList().toString());
        Assert.assertEquals("[[0, 1, 2, 3], [4, 5, 6, 7], [8, 9, 10, 11], [12, 13, 14, 15], [16, 17, 18, 19], [20]]", StreamExHeadTailTest.batches(IntStreamEx.range(21).boxed(), 4).toList().toString());
        Assert.assertEquals("[[0, 1, 2, 3], [1, 2, 3, 4], [2, 3, 4, 5], [3, 4, 5, 6], [4, 5, 6, 7], [5, 6, 7, 8], [6, 7, 8, 9]]", StreamExHeadTailTest.sliding(IntStreamEx.range(10).mapToObj(Collections::singletonList), 4).toList().toString());
        Assert.assertEquals(IntStreamEx.range(50, 100).boxed().toList(), StreamExHeadTailTest.dropWhile(IntStreamEx.range(100).boxed(), ( i) -> i != 50).toList());
        Assert.assertEquals(Arrays.asList(1, 3, 4, 7, 10), StreamExHeadTailTest.dominators(StreamEx.of(1, 3, 4, 2, 1, 7, 5, 3, 4, 0, 4, 6, 7, 10, 4, 3, 2, 1), ( a, b) -> a >= b).toList());
        Assert.assertEquals(Arrays.asList(1, 3, 7, 5, 10), StreamExHeadTailTest.distinct(StreamEx.of(1, 1, 3, 1, 3, 7, 1, 3, 1, 7, 3, 5, 1, 3, 5, 5, 7, 7, 7, 10, 5, 3, 7, 1)).toList());
        Assert.assertEquals(Arrays.asList("key1=1", "key2=2", "key3=3"), StreamExHeadTailTest.couples(StreamEx.of("key1", 1, "key2", 2, "key3", 3), ( k, v) -> (k + "=") + v).toList());
        Assert.assertEquals(Arrays.asList("key1=1", "1=key2", "key2=2", "2=key3", "key3=3"), StreamExHeadTailTest.pairs(StreamEx.of("key1", 1, "key2", 2, "key3", 3), ( k, v) -> (k + "=") + v).toList());
        Assert.assertEquals(Arrays.asList("1. Foo", "2. Bar", "3. Baz"), StreamExHeadTailTest.withIndices(StreamEx.of("Foo", "Bar", "Baz"), ( idx, e) -> ((idx + 1) + ". ") + e).toList());
        Assert.assertEquals(Arrays.asList(1, 2, 3, 4, 10), StreamExHeadTailTest.appendSum(StreamEx.of(1, 2, 3, 4)).toList());
        Assert.assertFalse(StreamExHeadTailTest.appendSum(StreamEx.of(1, 2, 3, 4)).has(11));
        Assert.assertEquals(Arrays.asList(0, 3, 6, 9, 12, 15, 18, 0, 4, 8, 12, 16), StreamExHeadTailTest.twoFilters(IntStreamEx.range(20).boxed(), ( x) -> (x % 3) == 0, ( x) -> (x % 4) == 0).toList());
        Assert.assertEquals(Arrays.asList(5, 10, 1, 6, 7), StreamExHeadTailTest.skipLast(Stream.of(5, 10, 1, 6, 7, 15, (-1), 10), 3).toList());
        Assert.assertEquals(Arrays.asList(0, 3, 6, 9, 12, 15, 18), StreamExHeadTailTest.every3(IntStreamEx.range(20).boxed()).toList());
        // noinspection RedundantTypeArguments -- necessary for Javac 8u92
        Assert.assertEquals(Arrays.asList(0, 1, 2, 3, 3), StreamEx.of(0, 1, 4, 2, 10, 3, 5, 10, 3, 15).chain(StreamExHeadTailTest.limitSorted(Comparator.<Integer>naturalOrder(), 5)).toList());
        Assert.assertEquals(Arrays.asList(1, 3, 7, 9, 2, 4, 11, 17, 5, 10), StreamEx.of(1, 3, 5, 7, 9, 2, 4, 10, 11, 17).chain(StreamExHeadTailTest.moveToEnd(( x) -> (x % 5) == 0)).toList());
        Assert.assertEquals(Arrays.asList(2, 4, 8, 10, 3, 5, 12, 18, 5, 10), StreamEx.of(1, 3, 5, 7, 9, 2, 4, 10, 11, 17).chain(StreamExHeadTailTest.moveToEndOrMap(( x) -> (x % 5) == 0, ( x) -> x + 1)).toList());
        Assert.assertEquals(Arrays.asList(11, 21, 41, 51, 30), StreamEx.of(10, 20, 30, 40, 50).chain(StreamExHeadTailTest.moveToEndOrMap(( x) -> x == 30, ( x) -> x + 1)).toList());
    }

    @Test
    public void testHeadTailTCO() {
        Assert.assertTrue(StreamExHeadTailTest.couples(IntStreamEx.range(20000).boxed(), ( a, b) -> b - a).allMatch(( x) -> x == 1));
        Assert.assertTrue(StreamExHeadTailTest.pairs(IntStreamEx.range(20000).boxed(), ( a, b) -> b - a).allMatch(( x) -> x == 1));
        // 20001+20002+...+40000
        Assert.assertEquals(600010000, StreamExHeadTailTest.limit(StreamExHeadTailTest.skip(StreamEx.iterate(1, ( x) -> x + 1), 20000), 20000).mapToInt(Integer::intValue).sum());
        // 1+3+5+...+39999
        Assert.assertEquals(400000000, StreamExHeadTailTest.limit(StreamExHeadTailTest.every(StreamEx.iterate(1, ( x) -> x + 1), 2), 20000).mapToInt(Integer::intValue).sum());
        Assert.assertEquals(400000000, StreamExHeadTailTest.limit(StreamExHeadTailTest.filter(StreamEx.iterate(1, ( x) -> x + 1), ( x) -> (x % 2) != 0), 20000).mapToInt(Integer::intValue).sum());
        // 1+2+...+10000
        Assert.assertEquals(50005000, ((int) (StreamExHeadTailTest.limit(StreamExHeadTailTest.scanLeft(StreamEx.iterate(1, ( x) -> x + 1), Integer::sum), 10000).reduce(( a, b) -> b).get())));
        Assert.assertEquals(50005000, ((int) (StreamExHeadTailTest.limit(StreamExHeadTailTest.flatMap(StreamEx.iterate(1, ( x) -> x + 3), (Integer x) -> StreamEx.of(x, (x + 1), (x + 2))), 10000).reduce(Integer::sum).get())));
        Assert.assertEquals(Arrays.asList(50005000), StreamExHeadTailTest.skip(StreamExHeadTailTest.appendReduction(IntStreamEx.rangeClosed(1, 10000).boxed(), 0, Integer::sum), 10000).toList());
        Assert.assertEquals(499501, StreamExHeadTailTest.mapFirst(IntStreamEx.range(1000).boxed(), ( x) -> x + 1).mapToInt(( x) -> x).sum());
        AtomicInteger sum = new AtomicInteger();
        Assert.assertEquals(10000, StreamExHeadTailTest.peek(IntStreamEx.rangeClosed(1, 10000).boxed(), sum::addAndGet).count());
        Assert.assertEquals(50005000, sum.get());
        Assert.assertEquals(400020000, ((int) (StreamExHeadTailTest.limit(StreamExHeadTailTest.map(StreamEx.iterate(1, ( x) -> x + 1), ( x) -> x * 2), 20000).reduce(Integer::sum).get())));
        Assert.assertEquals(19999, StreamExHeadTailTest.takeWhile(StreamEx.iterate(1, ( x) -> x + 1), ( x) -> x < 20000).count());
        Assert.assertTrue(StreamExHeadTailTest.takeWhile(StreamEx.iterate(1, ( x) -> x + 1), ( x) -> x < 20000).has(19999));
        Assert.assertEquals(20000, StreamExHeadTailTest.takeWhileClosed(StreamEx.iterate(1, ( x) -> x + 1), ( x) -> x < 20000).count());
        Assert.assertTrue(StreamExHeadTailTest.takeWhileClosed(StreamEx.iterate(1, ( x) -> x + 1), ( x) -> x < 20000).has(20000));
        Assert.assertEquals(IntStreamEx.range(20000, 40000).boxed().toList(), StreamExHeadTailTest.dropWhile(IntStreamEx.range(40000).boxed(), ( i) -> i != 20000).toList());
        Assert.assertEquals(5000, StreamExHeadTailTest.batches(IntStreamEx.range(20000).boxed(), 4).count());
        Assert.assertEquals(4, StreamExHeadTailTest.batches(IntStreamEx.range(20000).boxed(), 5000).count());
        Assert.assertEquals(19997, StreamExHeadTailTest.sliding(IntStreamEx.range(20000).mapToObj(Collections::singletonList), 4).count());
        Assert.assertEquals(IntStreamEx.range(40000).boxed().toList(), StreamExHeadTailTest.dominators(IntStreamEx.range(40000).boxed(), ( a, b) -> a >= b).toList());
        Assert.assertEquals(15, StreamExHeadTailTest.dominators(IntStreamEx.of(new Random(1)).boxed(), ( a, b) -> a >= b).takeWhile(( x) -> x < (Integer.MAX_VALUE - 100000)).count());
        Assert.assertEquals(IntStreamEx.of(new Random(1), 10000).boxed().sorted().toList(), StreamExHeadTailTest.sorted(IntStreamEx.of(new Random(1), 10000).boxed()).toList());
        Assert.assertEquals(10000, StreamExHeadTailTest.withIndices(IntStreamEx.of(new Random(1), 10000).boxed(), ( idx, e) -> (idx + ": ") + e).count());
        Assert.assertEquals(10000, StreamExHeadTailTest.limit(StreamExHeadTailTest.distinctTSO(IntStreamEx.of(new Random(1)).boxed()), 10000).toSet().size());
        Assert.assertEquals(IntStreamEx.range(10000).boxed().toList(), StreamExHeadTailTest.sorted(StreamExHeadTailTest.limit(StreamExHeadTailTest.distinctTSO(IntStreamEx.of(new Random(1), 0, 10000).boxed()), 10000)).toList());
        Assert.assertEquals(IntStreamEx.rangeClosed(9999, 0, (-1)).boxed().toList(), StreamExHeadTailTest.reverseTSO(IntStreamEx.range(10000).boxed()).toList());
        Assert.assertEquals(IntStreamEx.range(10000).append(IntStreamEx.rangeClosed(9999, 0, (-1))).boxed().toList(), StreamExHeadTailTest.mirrorTSO(IntStreamEx.range(10000).boxed()).toList());
    }

    @Test
    public void testHeadTailClose() {
        AtomicBoolean origClosed = new AtomicBoolean();
        AtomicBoolean internalClosed = new AtomicBoolean();
        AtomicBoolean finalClosed = new AtomicBoolean();
        StreamEx<Integer> res = StreamEx.of(1, 2, 3).onClose(() -> origClosed.set(true)).headTail(( head, stream) -> stream.onClose(() -> internalClosed.set(true)).map(( x) -> x + head)).onClose(() -> finalClosed.set(true));
        Assert.assertEquals(Arrays.asList(3, 4), res.toList());
        res.close();
        Assert.assertTrue(origClosed.get());
        Assert.assertTrue(internalClosed.get());
        Assert.assertTrue(finalClosed.get());
        res = StreamEx.<Integer>empty().headTail(( head, tail) -> tail);
        Assert.assertEquals(0, res.count());
        res.close();
    }

    // Test simple non-recursive scenarios
    @Test
    public void testHeadTailSimple() {
        TestHelpers.repeat(10, ( i) -> {
            // Header mapping
            String input = "name,type,value\nID,int,5\nSurname,string,Smith\nGiven name,string,John";
            List<Map<String, String>> expected = Arrays.asList(EntryStream.of("name", "ID", "type", "int", "value", "5").toMap(), EntryStream.of("name", "Surname", "type", "string", "value", "Smith").toMap(), EntryStream.of("name", "Given name", "type", "string", "value", "John").toMap());
            TestHelpers.streamEx(() -> StreamEx.ofLines(new StringReader(input)), ( s) -> Assert.assertEquals(expected, s.get().map(( str) -> str.split(",")).headTail(( header, stream) -> stream.map(( row) -> EntryStream.zip(header, row).toMap())).toList()));
        });
        TestHelpers.streamEx(() -> StreamEx.of("a", "b", "c", "d"), ( s) -> Assert.assertEquals(Collections.singletonMap("a", Arrays.asList("b", "c", "d")), s.get().headTail(( x, str) -> str.mapToEntry(( e) -> x, ( e) -> e)).mapToEntry(Map.Entry::getKey, Map.Entry::getValue).grouping()));
        Assert.assertEquals(Arrays.asList("b:c", "c:d"), StreamEx.of(":", "b", "c", "d").headTail(( head, tail) -> tail.pairMap(( left, right) -> (left + head) + right)).toList());
        Assert.assertEquals(Arrays.asList("b:", "c", "d"), StreamEx.of(":", "b", "c", "d").headTail(( head, tail) -> tail.mapFirst(( first) -> first + head)).toList());
    }

    @Test
    public void testSpliterator() {
        Spliterator<Integer> spltr = StreamExHeadTailTest.map(StreamEx.of(1, 2, 3, 4), ( x) -> x * 2).spliterator();
        Assert.assertTrue(spltr.hasCharacteristics(Spliterator.ORDERED));
        Assert.assertEquals(4, spltr.estimateSize());
        Assert.assertTrue(spltr.tryAdvance(( x) -> Assert.assertEquals(2, ((int) (x)))));
        Assert.assertEquals(3, spltr.estimateSize());
        Assert.assertTrue(spltr.tryAdvance(( x) -> Assert.assertEquals(4, ((int) (x)))));
        Assert.assertEquals(2, spltr.estimateSize());
        Assert.assertTrue(spltr.tryAdvance(( x) -> Assert.assertEquals(6, ((int) (x)))));
        Assert.assertEquals(1, spltr.estimateSize());
        Assert.assertTrue(spltr.tryAdvance(( x) -> Assert.assertEquals(8, ((int) (x)))));
        Assert.assertFalse(spltr.tryAdvance(( x) -> Assert.fail("Should not be called")));
        Assert.assertEquals(0, spltr.estimateSize());
    }
}

