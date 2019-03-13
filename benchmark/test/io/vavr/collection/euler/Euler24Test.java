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
package io.vavr.collection.euler;


import io.vavr.Function1;
import io.vavr.collection.List;
import org.junit.Test;


/**
 * <strong>Problem 24: Lexicographic permutations</strong>
 * <p>
 * A permutation is an ordered arrangement of objects. For example, 3124 is one
 * possible permutation of the digits 1, 2, 3 and 4. If all of the permutations
 * are listed numerically or alphabetically, we call it lexicographic order. The
 * lexicographic permutations of 0, 1 and 2 are:
 * <p>
 * 012 021 102 120 201 210
 * <p>
 * What is the millionth lexicographic permutation of the digits 0, 1, 2, 3, 4,
 * 5, 6, 7, 8 and 9?
 * <p>
 * See also <a href="https://projecteuler.net/problem=24">projecteuler.net
 * problem 24</a>.
 */
public class Euler24Test {
    @Test
    public void shouldSolveProblem24() {
        List.of("012", "021", "102", "120", "201", "210").zipWithIndex().forEach(( p) -> {
            assertThat(lexicographicPermutationNaive(List.of("1", "0", "2"), (p._2 + 1))).isEqualTo(p._1);
            assertThat(lexicographicPermutation(List.of("1", "0", "2"), (p._2 + 1))).isEqualTo(p._1);
        });
        assertThat(Euler24Test.lexicographicPermutation(List.of("0", "1", "2", "3", "4", "5", "6", "7", "8", "9"), 1000000)).isEqualTo("2783915460");
    }

    private static final Function1<Integer, Integer> memoizedFactorial = Function1.of((Integer i) -> factorial(i).intValue()).memoized();
}

