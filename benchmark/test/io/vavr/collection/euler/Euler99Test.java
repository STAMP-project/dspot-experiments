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


public class Euler99Test {
    /**
     * <strong>Problem 99: Largest exponential</strong>
     * <p>
     * Comparing two numbers written in index form like 2<sup>11</sup> and 3<sup>7</sup> is not difficult,
     * as any calculator would confirm that 2<sup>11</sup> = 2048 &lt; 3<sup>7</sup> = 2187.
     * <p>
     * However, confirming that 632382<sup>518061</sup> &gt; 519432<sup>525806</sup> would be much more difficult,
     * as both numbers contain over three million digits.
     * <p>
     * Using p099_base_exp.txt, a 22K text file containing one thousand lines with a base/exponent pair on each line,
     * determine which line number has the greatest numerical value.
     * <p>
     * See also <a href="https://projecteuler.net/problem=99">projecteuler.net problem 99</a>.
     */
    @Test
    public void shouldSolveProblem99() {
        assertThat(Euler99Test.solve()).isEqualTo(709);
    }
}

