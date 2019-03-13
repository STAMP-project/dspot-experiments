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
import io.vavr.collection.Stream;
import org.assertj.core.api.Assertions;
import org.junit.Test;


public class Euler42Test {
    /**
     * <strong>Problem 42 Coded triangle numbers</strong>
     * <p>
     * The <i>n</i><sup>th</sup> term of the sequence of triangle numbers is
     * given by, <i>t</i><sub>n</sub> = ?<i>n</i>(<i>n</i>+1); so the first ten
     * triangle numbers are:
     * <pre>
     * 1, 3, 6, 10, 15, 21, 28, 36, 45, 55, ...
     * </pre>
     * <p>
     * By converting each letter in a word to a number corresponding to its
     * alphabetical position and adding these values we form a word value. For
     * example, the word value for SKY is 19 + 11 + 25 = 55 = t<sub>10</sub>. If
     * the word value is a triangle number then we shall call the word a
     * triangle word.
     * <p>
     * Using p042_words.txt, a 16K text file containing nearly two-thousand
     * common English words, how many are triangle words?
     * <p>
     * See also <a href="https://projecteuler.net/problem=42">projecteuler.net
     * problem 42</a>.
     */
    @Test
    public void shouldSolveProblem42() {
        assertThat(Euler42Test.isTriangleWord("SKY")).isTrue();
        assertThat(Euler42Test.sumOfAlphabeticalPositions("SKY")).isEqualTo(55);
        assertThat(Euler42Test.alphabeticalPosition('S')).isEqualTo(19);
        assertThat(Euler42Test.alphabeticalPosition('K')).isEqualTo(11);
        assertThat(Euler42Test.alphabeticalPosition('Y')).isEqualTo(25);
        assertThat(Euler42Test.TRIANGLE_NUMBERS.take(10)).containsExactly(1, 3, 6, 10, 15, 21, 28, 36, 45, 55);
        List.rangeClosed(1, 60).forEach(( n) -> Assertions.assertThat(isTriangleNumberMemoized.apply(n)).isEqualTo(List.of(1, 3, 6, 10, 15, 21, 28, 36, 45, 55).contains(n)));
        assertThat(Euler42Test.numberOfTriangleNumbersInFile()).isEqualTo(162);
    }

    private static final Function1<Integer, Boolean> isTriangleNumberMemoized = Function1.of(Euler42Test::isTriangleNumber).memoized();

    private static final Stream<Integer> TRIANGLE_NUMBERS = Stream.from(1).map(( n) -> (0.5 * n) * (n + 1)).map(Double::intValue);
}

