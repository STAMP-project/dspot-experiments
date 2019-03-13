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


import java.util.Arrays;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Supplier;
import org.junit.Test;


/**
 *
 *
 * @author Tagir Valeev
 */
public class RangeBasedSpliteratorTest {
    private static final List<Integer> list10 = IntStreamEx.range(10).boxed().toList();

    @Test
    public void testAsEntry() {
        List<Integer> list = IntStreamEx.range(10).boxed().toList();
        TestHelpers.checkSpliterator("asEntry", () -> new RangeBasedSpliterator.AsEntry<>(list));
    }

    @Test
    public void testOfSublists() {
        List<Integer> list = IntStreamEx.range(10).boxed().toList();
        TestHelpers.checkSpliterator("ofSubLists", Arrays.asList(Arrays.asList(0, 1), Arrays.asList(2, 3), Arrays.asList(4, 5), Arrays.asList(6, 7), Arrays.asList(8, 9)), () -> new RangeBasedSpliterator.OfSubLists<>(list, 2, 2));
        TestHelpers.checkSpliterator("ofSubLists", Arrays.asList(Arrays.asList(0, 1, 2), Arrays.asList(2, 3, 4), Arrays.asList(4, 5, 6), Arrays.asList(6, 7, 8), Arrays.asList(8, 9)), () -> new RangeBasedSpliterator.OfSubLists<>(list, 3, 2));
    }

    @Test
    public void testOfByte() {
        byte[] input = IntStreamEx.range(10).toByteArray();
        TestHelpers.checkSpliterator("ofByte", RangeBasedSpliteratorTest.list10, () -> new RangeBasedSpliterator.OfByte(0, 10, input));
    }

    @Test
    public void testOfChar() {
        char[] input = IntStreamEx.range(10).toCharArray();
        TestHelpers.checkSpliterator("ofChar", RangeBasedSpliteratorTest.list10, () -> new RangeBasedSpliterator.OfChar(0, 10, input));
    }

    @Test
    public void testOfShort() {
        short[] input = IntStreamEx.range(10).toShortArray();
        TestHelpers.checkSpliterator("ofShort", RangeBasedSpliteratorTest.list10, () -> new RangeBasedSpliterator.OfShort(0, 10, input));
    }

    @Test
    public void testOfFloat() {
        float[] input = IntStreamEx.range(10).asDoubleStream().toFloatArray();
        TestHelpers.checkSpliterator("ofFloat", () -> new RangeBasedSpliterator.OfFloat(0, 10, input));
    }

    @Test
    public void testZipRef() {
        List<Integer> l1 = IntStreamEx.range(10).boxed().toList();
        List<String> l2 = StreamEx.split("abcdefghij", "").toList();
        TestHelpers.checkSpliterator("zipRef", () -> new RangeBasedSpliterator.ZipRef<>(0, 10, ( i, s) -> i + s, l1, l2));
    }

    @Test
    public void testZipInt() {
        int[] a = IntStreamEx.range(10).toArray();
        int[] b = IntStreamEx.range(10, 20).toArray();
        TestHelpers.checkSpliterator("zipInt", () -> new RangeBasedSpliterator.ZipInt(0, 10, ( x, y) -> x * y, a, b));
    }

    @Test
    public void testZipLong() {
        long[] a = LongStreamEx.range(10).toArray();
        long[] b = LongStreamEx.range(10, 20).toArray();
        TestHelpers.checkSpliterator("zipLong", () -> new RangeBasedSpliterator.ZipLong(0, 10, ( x, y) -> x * y, a, b));
    }

    @Test
    public void testZipDouble() {
        double[] a = LongStreamEx.range(10).asDoubleStream().toArray();
        double[] b = LongStreamEx.range(10, 20).asDoubleStream().toArray();
        TestHelpers.checkSpliterator("zipDouble", () -> new RangeBasedSpliterator.ZipDouble(0, 10, ( x, y) -> x * y, a, b));
    }
}

