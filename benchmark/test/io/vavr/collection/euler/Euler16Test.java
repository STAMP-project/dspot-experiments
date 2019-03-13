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


public class Euler16Test {
    /**
     * <strong>Problem 16: Power digit sum</strong>
     * <p>
     * 2<sup>15</sup> = 32768 and the sum of its digits is 3 + 2 + 7 + 6 + 8 = 26.
     * <p>
     * What is the sum of the digits of the number 2<sup>1000</sup>?
     * <p>
     * See also <a href="https://projecteuler.net/problem=16">projecteuler.net problem 16</a>.
     */
    @Test
    public void shouldSolveProblem16() {
        assertThat(Euler16Test.solve(15)).isEqualTo(26);
        assertThat(Euler16Test.solve(1000)).isEqualTo(1366);
    }
}

