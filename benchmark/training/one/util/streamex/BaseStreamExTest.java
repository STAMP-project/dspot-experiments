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


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Spliterator;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Stream;
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
public class BaseStreamExTest {
    @Test
    public void testSpliterator() {
        Spliterator<Integer> spltr = Arrays.spliterator(new Integer[]{ 1, 2, 3 });
        StreamEx<Integer> s = StreamEx.of(spltr);
        Assert.assertFalse(s.isParallel());
        Assert.assertSame(spltr, s.spliterator());
    }

    @Test(expected = IllegalStateException.class)
    public void testSpliteratorConsumed() {
        StreamEx<Integer> s = StreamEx.of(1, 2, 3);
        Assert.assertNotNull(s.spliterator());
        s.spliterator();
    }

    @Test(expected = IllegalStateException.class)
    public void testStreamConsumed() {
        StreamEx<Integer> s = StreamEx.of(1, 2, 3);
        Assert.assertNotNull(s.spliterator());
        s.count();
    }

    @Test
    public void testClose() {
        List<String> closeHandlers = new ArrayList<>();
        StreamEx<Integer> stream = StreamEx.of(Stream.of(1, 2, 3).onClose(() -> closeHandlers.add("Orig stream"))).onClose(() -> closeHandlers.add("StreamEx")).map(( x) -> x * 2).onClose(() -> closeHandlers.add("After map")).pairMap(( a, b) -> a + b).onClose(() -> closeHandlers.add("After pairMap")).append(4).onClose(() -> closeHandlers.add("After append")).prepend(Stream.of(5).onClose(() -> closeHandlers.add("Prepended Stream"))).prepend(StreamEx.of(6).onClose(() -> closeHandlers.add("Prepended StreamEx")));
        Assert.assertEquals(Arrays.asList(6, 5, 6, 10, 4), stream.toList());
        Assert.assertTrue(closeHandlers.isEmpty());
        stream.close();
        Assert.assertEquals(Arrays.asList("Orig stream", "StreamEx", "After map", "After pairMap", "After append", "Prepended Stream", "Prepended StreamEx"), closeHandlers);
        closeHandlers.clear();
        stream.close();
        Assert.assertTrue(closeHandlers.isEmpty());
    }

    @Test
    public void testCloseException() {
        AtomicBoolean flag = new AtomicBoolean();
        Function<String, Runnable> ex = ( str) -> () -> {
            throw new IllegalStateException(str);
        };
        StreamEx<Integer> stream = StreamEx.of(Stream.of(1, 2, 3).onClose(ex.apply("Orig stream"))).onClose(ex.apply("StreamEx")).map(( x) -> x * 2).onClose(() -> flag.set(true)).pairMap(( a, b) -> a + b).onClose(ex.apply("After pairMap")).append(4).onClose(ex.apply("After append")).prepend(Stream.of(5).onClose(ex.apply("Prepended Stream"))).prepend(StreamEx.of(6).onClose(ex.apply("Prepended StreamEx")));
        Assert.assertEquals(Arrays.asList(6, 5, 6, 10, 4), stream.toList());
        try {
            stream.close();
        } catch (IllegalStateException e) {
            Assert.assertEquals("Orig stream", e.getMessage());
            Assert.assertEquals(Arrays.asList("StreamEx", "After pairMap", "After append", "Prepended Stream", "Prepended StreamEx"), StreamEx.of(e.getSuppressed()).map(IllegalStateException.class::cast).map(Throwable::getMessage).toList());
            Assert.assertTrue(flag.get());
            return;
        }
        Assert.fail("No exception");
    }

    @Test
    public void testChain() {
        Assert.assertArrayEquals(new double[]{ 2, 2, 6, 2, 6 }, IntStreamEx.of(3, 4, 5).chain(IntStreamEx::boxed).chain(( s) -> s.flatMapToLong(LongStreamEx::range)).chain(( s) -> s.filter(( n) -> (n % 2) != 0).asDoubleStream()).chain(( s) -> s.map(( x) -> x * 2)).toArray(), 0.0);
    }
}

