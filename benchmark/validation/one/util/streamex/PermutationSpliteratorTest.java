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
import java.util.List;
import java.util.Random;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import org.junit.Assert;
import org.junit.Test;


public class PermutationSpliteratorTest {
    private static final String PERMUTATIONS_4 = "0123,0132,0213,0231,0312,0321," + (("1023,1032,1203,1230,1302,1320," + "2013,2031,2103,2130,2301,2310,") + "3012,3021,3102,3120,3201,3210");

    private static final String PERMUTATIONS_3 = "012,021,102,120,201,210";

    @Test(expected = IllegalArgumentException.class)
    public void testOverflow() {
        new PermutationSpliterator(21);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUnderflow() {
        new PermutationSpliterator((-1));
    }

    @Test
    public void testAdvance3() {
        Spliterator<int[]> spliterator = new PermutationSpliterator(3);
        Assert.assertEquals(PermutationSpliteratorTest.PERMUTATIONS_3, String.join(",", PermutationSpliteratorTest.collect(spliterator)));
    }

    @Test
    public void testAdvance4() {
        Spliterator<int[]> spliterator = new PermutationSpliterator(4);
        Assert.assertEquals(PermutationSpliteratorTest.PERMUTATIONS_4, String.join(",", PermutationSpliteratorTest.collect(spliterator)));
    }

    @Test
    public void testSplit3() {
        Spliterator<int[]> spliterator = new PermutationSpliterator(3);
        Spliterator<int[]> prefix = spliterator.trySplit();
        Assert.assertNotNull(prefix);
        Assert.assertEquals(3, spliterator.getExactSizeIfKnown());
        Assert.assertEquals(3, prefix.getExactSizeIfKnown());
        List<String> strings = PermutationSpliteratorTest.collect(prefix);
        Assert.assertEquals(3, strings.size());
        strings.addAll(PermutationSpliteratorTest.collect(spliterator));
        Assert.assertEquals(PermutationSpliteratorTest.PERMUTATIONS_3, String.join(",", strings));
    }

    @Test
    public void testSplit3Random() {
        TestHelpers.withRandom(( r) -> TestHelpers.repeat(100, ( i) -> {
            List<String> strings = new ArrayList<>();
            PermutationSpliteratorTest.collectRandomSplit(new PermutationSpliterator(3), r, strings);
            Assert.assertEquals(String.valueOf(i), PermutationSpliteratorTest.PERMUTATIONS_3, String.join(",", strings));
        }));
    }

    @Test
    public void testSplit4() {
        Spliterator<int[]> spliterator = new PermutationSpliterator(4);
        Spliterator<int[]> prefix = spliterator.trySplit();
        Assert.assertNotNull(prefix);
        Assert.assertEquals(12, spliterator.getExactSizeIfKnown());
        Assert.assertEquals(12, prefix.getExactSizeIfKnown());
        List<String> strings = PermutationSpliteratorTest.collect(prefix);
        Assert.assertEquals(12, strings.size());
        strings.addAll(PermutationSpliteratorTest.collect(spliterator));
        Assert.assertEquals(PermutationSpliteratorTest.PERMUTATIONS_4, String.join(",", strings));
    }

    @Test
    public void testSplit4Random() {
        TestHelpers.withRandom(( r) -> TestHelpers.repeat(100, ( i) -> {
            List<String> strings = new ArrayList<>();
            PermutationSpliteratorTest.collectRandomSplit(new PermutationSpliterator(4), r, strings);
            Assert.assertEquals(String.valueOf(i), PermutationSpliteratorTest.PERMUTATIONS_4, String.join(",", strings));
        }));
    }
}

