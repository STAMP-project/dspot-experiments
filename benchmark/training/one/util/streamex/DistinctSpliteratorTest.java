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
import java.util.HashSet;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.IntUnaryOperator;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Tagir Valeev
 */
public class DistinctSpliteratorTest {
    @Test
    public void testSpliterator() {
        TestHelpers.checkSpliterator("Distinct2", Arrays.asList("b"), () -> new DistinctSpliterator(Arrays.asList("a", null, "b", "c", "b", null, "c", "b").spliterator(), 3));
        TestHelpers.checkSpliterator("Distinct34", Arrays.asList(0), () -> new DistinctSpliterator(IntStream.range(0, 100).map(( x) -> x % 3).boxed().spliterator(), 34));
        Assert.assertEquals(((Spliterator.DISTINCT) | (Spliterator.ORDERED)), new DistinctSpliterator(Arrays.asList("a", null, "b", "c", "b", null, "c", "b").spliterator(), 3).characteristics());
        Assert.assertEquals((((((Spliterator.DISTINCT) | (Spliterator.ORDERED)) | (Spliterator.IMMUTABLE)) | (Spliterator.SORTED)) | (Spliterator.NONNULL)), new DistinctSpliterator(IntStream.range(0, 100).spliterator(), 3).characteristics());
        Assert.assertEquals(100, new DistinctSpliterator(IntStream.range(0, 100).spliterator(), 3).estimateSize());
    }

    @Test
    public void testAdvanceSplit() {
        DistinctSpliterator<String> ds = new DistinctSpliterator(Arrays.asList("a", null, "b", "c", "b", null, "c", "b").spliterator(), 2);
        Set<String> result = new HashSet<>();
        Assert.assertTrue(ds.tryAdvance(result::add));
        Assert.assertTrue(ds.tryAdvance(result::add));
        Spliterator<String> prefix = ds.trySplit();
        prefix.forEachRemaining(result::add);
        ds.forEachRemaining(result::add);
        Assert.assertEquals(StreamEx.of(null, "b", "c").toSet(), result);
    }
}

