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
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Spliterator;
import java.util.function.Supplier;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Tagir Valeev
 */
public class PairPermutationSpliteratorTest {
    @Test
    public void testSqrt() {
        for (int rev = 0; rev < 1000; rev++) {
            int row = ((int) ((Math.sqrt(((8 * rev) + 1))) - 1)) / 2;
            int row2 = PairPermutationSpliterator.isqrt(rev);
            Assert.assertEquals(row, row2);
        }
        for (int row : new int[]{ 1000000000, 2000000000, (Integer.MAX_VALUE) - 1, Integer.MAX_VALUE }) {
            Assert.assertEquals(row, PairPermutationSpliterator.isqrt(((row * (row + 1L)) / 2)));
            Assert.assertEquals((row - 1), PairPermutationSpliterator.isqrt((((row * (row + 1L)) / 2) - 1)));
        }
    }

    @Test
    public void testCharacteristics() {
        PairPermutationSpliterator<Integer, Integer> spltr = new PairPermutationSpliterator(Arrays.asList(1, 2, 3), Integer::sum);
        Assert.assertTrue(spltr.hasCharacteristics(Spliterator.ORDERED));
        Assert.assertTrue(spltr.hasCharacteristics(Spliterator.SIZED));
        Assert.assertTrue(spltr.hasCharacteristics(Spliterator.SUBSIZED));
        Assert.assertEquals(3, spltr.getExactSizeIfKnown());
    }

    @Test
    public void testSpliterator() {
        for (int i : IntStreamEx.rangeClosed(2, 13).boxed()) {
            List<Integer> input = IntStreamEx.range(i).boxed().toList();
            List<Map.Entry<Integer, Integer>> expected = IntStreamEx.range(i).<Map.Entry<Integer, Integer>>flatMapToObj(( a) -> IntStreamEx.range((a + 1), i).mapToObj(( b) -> new SimpleEntry<>(a, b))).toList();
            TestHelpers.checkSpliterator(("#" + i), expected, () -> new PairPermutationSpliterator(input, AbstractMap.SimpleEntry::new));
        }
    }
}

