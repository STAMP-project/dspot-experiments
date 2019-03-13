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


import io.vavr.collection.CharSeq;
import org.junit.Test;


public class Euler38Test {
    /**
     * <strong>Problem 38 Pandigital multiples</strong>
     * <p>
     * Take the number 192 and multiply it by each of 1, 2, and 3:
     * <pre>
     * 192 ? 1 = 192
     * 192 ? 2 = 384
     * 192 ? 3 = 576
     * </pre>
     * <p>
     * By concatenating each product we get the 1 to 9 pandigital, 192384576. We
     * will call 192384576 the concatenated product of 192 and (1,2,3)
     * <p>
     * The same can be achieved by starting with 9 and multiplying by 1, 2, 3,
     * 4, and 5, giving the pandigital, 918273645, which is the concatenated
     * product of 9 and (1,2,3,4,5).
     * <p>
     * What is the largest 1 to 9 pandigital 9-digit number that can be formed
     * as the concatenated product of an integer with (1,2, ... , n) where n >
     * 1?
     * <p>
     * See also <a href="https://projecteuler.net/problem=38">projecteuler.net
     * problem 38</a>.
     */
    @Test
    public void shouldSolveProblem38() {
        assertThat(Euler38Test.isPandigitalMultiple(CharSeq.of("192384576"))).isTrue();
        assertThat(Euler38Test.isPandigitalMultiple(CharSeq.of("918273645"))).isTrue();
        assertThat(Euler38Test.largest1To9PandigitalMultiple().mkString()).isEqualTo("932718654");
    }
}

