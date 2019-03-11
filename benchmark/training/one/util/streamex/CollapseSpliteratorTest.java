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
import java.util.Random;
import java.util.Spliterator;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Tagir Valeev
 */
public class CollapseSpliteratorTest {
    @Test
    public void testSimpleSplit() {
        List<Integer> input = Arrays.asList(1, 1, 1, 2, 2, 2, 2, 2);
        CollapseSpliteratorTest.splitEquals(input.spliterator(), ( left, right) -> {
            List<Integer> result = new ArrayList<>();
            left.forEachRemaining(result::add);
            right.forEachRemaining(result::add);
            Assert.assertEquals(Arrays.asList(1, 2), result);
        });
        CollapseSpliteratorTest.splitEquals(input.spliterator(), ( left, right) -> {
            List<Integer> result = new ArrayList<>();
            List<Integer> resultRight = new ArrayList<>();
            right.forEachRemaining(resultRight::add);
            left.forEachRemaining(result::add);
            result.addAll(resultRight);
            Assert.assertEquals(Arrays.asList(1, 2), result);
        });
        input = IntStreamEx.of(new Random(1), 100, 1, 10).sorted().boxed().toList();
        CollapseSpliteratorTest.splitEquals(input.spliterator(), ( left, right) -> {
            List<Integer> result = new ArrayList<>();
            List<Integer> resultRight = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                left.tryAdvance(result::add);
                right.tryAdvance(resultRight::add);
            }
            result.addAll(resultRight);
            Assert.assertEquals(IntStreamEx.range(1, 10).boxed().toList(), result);
        });
        input = IntStreamEx.constant(100, 100).append(2).prepend(1).boxed().toList();
        CollapseSpliteratorTest.splitEquals(StreamEx.of(input).without(100).parallel().spliterator(), ( left, right) -> {
            List<Integer> result = new ArrayList<>();
            left.forEachRemaining(result::add);
            right.forEachRemaining(result::add);
            assertEquals(Arrays.asList(1, 2), result);
        });
        input = Arrays.asList(0, 0, 1, 1, 1, 1, 4, 6, 6, 3, 3, 10);
        CollapseSpliteratorTest.splitEquals(Stream.concat(Stream.empty(), input.parallelStream()).spliterator(), ( left, right) -> {
            List<Integer> result = new ArrayList<>();
            left.forEachRemaining(result::add);
            right.forEachRemaining(result::add);
            Assert.assertEquals(Arrays.asList(0, 1, 4, 6, 3, 10), result);
        });
    }

    @Test
    public void testNonIdentity() {
        CollapseSpliteratorTest.checkNonIdentity(Arrays.asList(1, 2, 5, 6, 7, 8, 10, 11, 15));
        CollapseSpliteratorTest.checkNonIdentity(IntStreamEx.range(3, 100).prepend(1).boxed().toList());
    }

    @Test
    public void testMultiSplit() {
        List<Integer> input = Arrays.asList(0, 0, 1, 1, 1, 1, 4, 6, 6, 3, 3, 10);
        CollapseSpliteratorTest.multiSplit(input::spliterator);
        CollapseSpliteratorTest.multiSplit(() -> Stream.concat(Stream.empty(), input.parallelStream()).spliterator());
    }
}

