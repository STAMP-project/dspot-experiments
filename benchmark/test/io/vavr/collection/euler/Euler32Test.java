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


import io.vavr.collection.CharSeq;
import org.junit.Test;


public class Euler32Test {
    /**
     * <strong>Problem 23 Pandigital products</strong>
     * <p>
     * We shall say that an n-digit number is pandigital if it makes use of all
     * the digits 1 to n exactly once; for example, the 5-digit number, 15234,
     * is 1 through 5 pandigital.
     * <p>
     * The product 7254 is unusual, as the identity, 39 ? 186 = 7254, containing
     * multiplicand, multiplier, and product is 1 through 9 pandigital.
     * <p>
     * Find the sum of all products whose multiplicand/multiplier/product
     * identity can be written as a 1 through 9 pandigital.
     * <p>
     * HINT: Some products can be obtained in more than one way so be sure to
     * only include it once in your sum.
     * <p>
     * See also <a href="https://projecteuler.net/problem=32">projecteuler.net
     * problem 32</a>.
     */
    @Test
    public void shouldSolveProblem32() {
        assertThat(Euler32Test.isPandigital(1, 5, "15234")).isTrue();
        assertThat(Euler32Test.isPandigital(1, 9, ("39" + ("186" + "7254")))).isTrue();
        assertThat(Euler32Test.isPandigital(1, 5, "55555")).isFalse();
        assertThat(Euler32Test.isPandigital(1, 5, "12340")).isFalse();
        assertThat(Euler32Test.sumOfAllProductsPandigital1Through9()).isEqualTo(45228);
    }

    private static final CharSeq DIGITS_1_9 = CharSeq.of("123456789");
}

