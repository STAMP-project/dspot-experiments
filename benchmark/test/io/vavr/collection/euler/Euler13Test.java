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


public class Euler13Test {
    /**
     * <strong>Problem 13: Large sum</strong>
     * <p>
     * Work out the first ten digits of the sum of the following one-hundred 50-digit numbers.
     * <p>
     * See also <a href="https://projecteuler.net/problem=13">projecteuler.net problem 13</a>.
     */
    @Test
    public void shouldSolveProblem13() {
        assertThat(Euler13Test.solve()).isEqualTo("5537376230");
    }
}

