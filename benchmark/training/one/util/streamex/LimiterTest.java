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
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.function.IntFunction;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.Test;


/**
 *
 *
 * @author Tagir Valeev
 */
public class LimiterTest {
    @Test
    public void testLimiter() {
        Comparator<String> cmp = Comparator.nullsFirst(Comparator.comparingInt(String::length));
        LimiterTest.exerciseLimiter("str", Arrays.asList("abc", "abgdc", "abd", "a", "fgssdfg", "sfsvsx", null, "wrffvs", "xcvbxvcb", "sffg", "abe", "adf", "abh"), cmp);
        for (int i : new int[]{ 10, 100, 1000, 10000, 100000 }) {
            List<Integer> ascending = IntStream.range(0, i).boxed().collect(Collectors.toList());
            LimiterTest.exerciseLimiter(("asc, nat, " + i), ascending, Comparator.naturalOrder());
            LimiterTest.exerciseLimiter(("asc, dec, " + i), ascending, Comparator.comparingInt(( x) -> x / 10));
            List<Integer> descending = IntStream.range(0, i).mapToObj(( x) -> ~x).collect(Collectors.toList());
            LimiterTest.exerciseLimiter(("desc, nat, " + i), descending, Comparator.naturalOrder());
            LimiterTest.exerciseLimiter(("desc, dec, " + i), descending, Comparator.comparingInt(( x) -> x / 10));
            List<Integer> random = new Random(1).ints(i).boxed().collect(Collectors.toList());
            LimiterTest.exerciseLimiter(("rnd, nat, " + i), random, Comparator.naturalOrder());
            LimiterTest.exerciseLimiter(("rnd, dec, " + i), random, Comparator.comparingInt(( x) -> x / 10));
            List<Integer> randomRange = new Random(1).ints(i, (-1000), 1000).boxed().collect(Collectors.toList());
            LimiterTest.exerciseLimiter(("rnd2, nat, " + i), randomRange, Comparator.naturalOrder());
            LimiterTest.exerciseLimiter(("rnd2, dec, " + i), randomRange, Comparator.comparingInt(( x) -> x / 10));
        }
        List<Integer> list = IntStreamEx.range(100000).boxed().toList();
        LimiterTest.exerciseLimiter("big", list, list, 50000, Comparator.naturalOrder());
        LimiterTest.exerciseLimiter("big", list, list, 49999, Comparator.naturalOrder());
        LimiterTest.exerciseLimiter("big", list, list, 10000, Comparator.naturalOrder());
        LimiterTest.exerciseLimiter("big", list, list, ((Integer.MAX_VALUE) / 3), Comparator.naturalOrder());
        LimiterTest.exerciseLimiter("big", list, list, ((Integer.MAX_VALUE) / 2), Comparator.naturalOrder());
    }
}

