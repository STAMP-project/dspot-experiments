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


public class Euler15Test {
    /**
     * <strong>Problem 15: Lattice paths</strong>
     * <p>
     * Starting in the top left corner of a 2x2 grid, and only being able to move to the right and down,
     * there are exactly 6 routes to the bottom right corner.
     * <p>
     * How many such routes are there through a 20?20 grid?
     * <p>
     * See also <a href="https://projecteuler.net/problem=15">projecteuler.net problem 15</a>.
     */
    @Test
    public void shouldSolveProblem15() {
        assertThat(Euler15Test.solve(2)).isEqualTo(6);
        assertThat(Euler15Test.solve(20)).isEqualTo(137846528820L);
    }
}

