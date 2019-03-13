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


public class Euler34Test {
    /**
     * <strong>Problem 34 Digit factorials</strong>
     * <p>
     * 145 is a curious number, as 1! + 4! + 5! = 1 + 24 + 120 = 145.
     * <p>
     * Find the sum of all numbers which are equal to the sum of the factorial
     * of their digits.
     * <p>
     * F
     * Note: as 1! = 1 and 2! = 2 are not sums they are not included.
     * <p>
     * See also <a href="https://projecteuler.net/problem=34">projecteuler.net
     * problem 34</a>.
     */
    @Test
    public void shouldSolveProblem34() {
        assertThat(Euler34Test.sumOfDigitFactorial(145)).isEqualTo(145);
        assertThat(Euler34Test.sumOfOfAllNumbersWhichAreEqualToSumOfDigitFactorial()).isEqualTo(40730);
    }
}

