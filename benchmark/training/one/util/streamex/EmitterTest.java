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


import java.math.BigInteger;
import java.util.Arrays;
import java.util.function.BinaryOperator;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
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
public class EmitterTest {
    @Test
    public void testEmitter() {
        Assert.assertEquals(Arrays.asList(17, 52, 26, 13, 40, 20, 10, 5, 16, 8, 4, 2, 1), EmitterTest.collatz(17).stream().toList());
        TestHelpers.checkSpliterator("collatz", Arrays.asList(17, 52, 26, 13, 40, 20, 10, 5, 16, 8, 4, 2, 1), EmitterTest.collatz(17)::spliterator);
        Assert.assertArrayEquals(new int[]{ 17, 52, 26, 13, 40, 20, 10, 5, 16, 8, 4, 2, 1 }, EmitterTest.collatzInt(17).stream().toArray());
        TestHelpers.checkSpliterator("collatzInt", Arrays.asList(17, 52, 26, 13, 40, 20, 10, 5, 16, 8, 4, 2, 1), EmitterTest.collatzInt(17)::spliterator);
        Assert.assertArrayEquals(new long[]{ 17, 52, 26, 13, 40, 20, 10, 5, 16, 8, 4, 2, 1 }, EmitterTest.collatzLong(17).stream().toArray());
        TestHelpers.checkSpliterator("collatzLong", Arrays.asList(17L, 52L, 26L, 13L, 40L, 20L, 10L, 5L, 16L, 8L, 4L, 2L, 1L), EmitterTest.collatzLong(17)::spliterator);
        Assert.assertTrue(EmitterTest.collatz(17).stream().has(1));
        Assert.assertTrue(EmitterTest.collatzInt(17).stream().has(1));
        Assert.assertFalse(EmitterTest.collatzLong(17).stream().has(0));
        Assert.assertEquals(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9), EmitterTest.iterate(1, ( x) -> x < 10, ( x) -> x + 1).stream().toList());
        // Extracting to variables is necessary to work-around javac <8u40 bug
        String expected = "354224848179261915075";
        String actual = EmitterTest.fibonacci().skip(99).findFirst().get().toString();
        Assert.assertEquals(expected, actual);
        Assert.assertEquals(Arrays.asList(1, 1, 2, 3, 5, 8, 13, 21, 34, 55), EmitterTest.fibonacci().map(BigInteger::intValueExact).limit(10).toList());
        Assert.assertEquals(Arrays.asList("aa", "aabbb", "aabbbc"), EmitterTest.scanLeft(Arrays.asList("aa", "bbb", "c").iterator(), "", String::concat).stream().toList());
        Assert.assertEquals(Arrays.asList(4, 4, 4, 4, 3, 3, 3, 2, 2, 1), EmitterTest.flatTest(4).stream().toList());
        Assert.assertEquals(Arrays.asList(4, 4, 4, 4, 3, 3, 3, 2, 2), EmitterTest.flatTest(4).stream().limit(9).toList());
        TestHelpers.checkSpliterator("flatTest", Arrays.asList(4, 4, 4, 4, 3, 3, 3, 2, 2, 1), EmitterTest.flatTest(4)::spliterator);
        TestHelpers.checkSpliterator("flatTest", Arrays.asList(4, 4, 4, 4, 3, 3, 3, 2, 2, 1), EmitterTest.flatTestInt(4)::spliterator);
        TestHelpers.checkSpliterator("flatTest", Arrays.asList(4L, 4L, 4L, 4L, 3L, 3L, 3L, 2L, 2L, 1L), EmitterTest.flatTestLong(4)::spliterator);
        TestHelpers.checkSpliterator("flatTest", Arrays.asList(4.0, 4.0, 4.0, 4.0, 3.0, 3.0, 3.0, 2.0, 2.0, 1.0), EmitterTest.flatTestDouble(4)::spliterator);
        Assert.assertEquals(7919L, EmitterTest.primes().skip(999).findFirst().getAsLong());
    }
}

