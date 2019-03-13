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


public class Euler04Test {
    /**
     * <strong>Problem 4: Largest palindrome product</strong>
     * <p>
     * A palindromic number reads the same both ways. The largest palindrome made
     * from the product of two 2-digit numbers is 9009 = 91 ? 99.
     * <p>
     * Find the largest palindrome made from the product of two 3-digit numbers.
     * <p>
     * See also <a href="https://projecteuler.net/problem=4">projecteuler.net problem 4</a>.
     */
    @Test
    public void shouldSolveProblem4() {
        assertThat(Euler04Test.largestPalindromeOfProductsFromFactorsInRange(10, 99)).isEqualTo(9009);
        assertThat(Euler04Test.largestPalindromeOfProductsFromFactorsInRange(100, 999)).isEqualTo(906609);
    }
}

