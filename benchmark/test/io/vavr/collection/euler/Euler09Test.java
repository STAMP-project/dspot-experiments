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


public class Euler09Test {
    /**
     * <strong>Problem 9: Special Pythagorean triplet</strong>
     * <p>
     * A Pythagorean triplet is a set of three natural numbers, a < b < c, for which,
     * a<sup>2</sup> + b<sup>2</sup> = c<sup>2</sup>
     * <p>
     * For example, 3<sup>2</sup> + 4<sup>2</sup> = 9 + 16 = 25 = 5<sup>2</sup>.
     * <p>
     * There exists exactly one Pythagorean triplet for which a + b + c = 1000.
     * Find the product abc.
     * <p>
     * See also <a href="https://projecteuler.net/problem=9">projecteuler.net problem 9</a>.
     */
    @Test
    public void shouldSolveProblem9() {
        assertThat(abc(1000)).isEqualTo(31875000);
    }
}

