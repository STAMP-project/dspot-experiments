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


import io.vavr.collection.List;
import org.junit.Test;


/**
 * <strong>Problem 35: Circular primes</strong>
 * <p>The number, 197, is called a circular prime because all rotations of the digits: 197, 971, and 719, are themselves prime.</p>
 * <p>There are thirteen such primes below 100: 2, 3, 5, 7, 11, 13, 17, 31, 37, 71, 73, 79, and 97.</p>
 * <p>How many circular primes are there below one million?</p>
 * See also <a href="https://projecteuler.net/problem=35">projecteuler.net problem 35</a>.
 */
public class Euler35Test {
    @Test
    public void shouldSolveProblem35() {
        assertThat(Euler35Test.rotations(197)).isEqualTo(List.of(197, 971, 719));
        assertThat(Euler35Test.circularPrimes(100)).isEqualTo(13);
        assertThat(Euler35Test.circularPrimes(1000000)).isEqualTo(55);
    }
}

