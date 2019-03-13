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
 * <strong>Problem 28: Number spiral diagonals</strong>
 * <p>
 * Starting with the number 1 and moving to the right in a clockwise direction a
 * 5 by 5 spiral is formed as follows:
 * <pre>
 *             21 22 23 24 25
 *             20  7  8  9 10
 *             19  6  1  2 11
 *             18  5  4  3 12
 *             17 16 15 14 13
 * </pre>
 *
 * It can be verified that the sum of the numbers on the diagonals is 101.
 * <p>
 * What is the sum of the numbers on the diagonals in a 1001 by 1001 spiral
 * formed in the same way?
 * <p>
 * See also
 * <a href="https://projecteuler.net/problem=28">projecteuler.net problem 28
 * </a>.
 */
public class Euler28Test {
    @Test
    public void shouldSolveProblem28() {
        assertThat(Euler28Test.sumOfDiagonalInSpiralWithSide(5)).isEqualTo(101);
        assertThat(Euler28Test.sumOfDiagonalInSpiralWithSide(1001)).isEqualTo(669171001);
    }
}

