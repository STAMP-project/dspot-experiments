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


import org.junit.Test;


/**
 * <strong>Problem 27: Quadratic primes</strong>
 * <p>
 * Euler discovered the remarkable quadratic formula:
 * <p>
 * n? + n + 41
 * <p>
 * It turns out that the formula will produce 40 primes for the consecutive
 * values n = 0 to 39. However, when n = 40, 40^2 + 40 + 41 = 40(40 + 1) + 41 is
 * divisible by 41, and certainly when n = 41, 41? + 41 + 41 is clearly
 * divisible by 41.
 * <p>
 * The incredible formula n? ? 79n + 1601 was discovered, which produces 80
 * primes for the consecutive values n = 0 to 79. The product of the
 * coefficients, ?79 and 1601, is ?126479.
 * <p>
 * Considering quadratics of the form:
 * <p>
 * n? + an + b, where |a| < 1000 and |b| < 1000 <p>
 * where |n| is the modulus/absolute value of n e.g. |11| = 11 and |?4| = 4
 * <p>
 * Find the product of the coefficients, a and b, for the quadratic expression
 * that produces the maximum number of primes for consecutive values of n,
 * starting with n = 0.
 * <p>
 * See also
 * <a href="https://projecteuler.net/problem=27">projecteuler.net problem 27
 * </a>.
 */
public class Euler27Test {
    @Test
    public void shouldSolveProblem27() {
        assertThat(Euler27Test.numberOfConsecutivePrimesProducedByFormulaWithCoefficients(1, 41)).isEqualTo(40);
        assertThat(Euler27Test.numberOfConsecutivePrimesProducedByFormulaWithCoefficients((-79), 1601)).isEqualTo(80);
        assertThat(Euler27Test.productOfCoefficientsWithMostConsecutivePrimes((-999), 999)).isEqualTo((-59231));
    }
}

