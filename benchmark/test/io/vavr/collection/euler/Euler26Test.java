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
 * <strong>Problem 26: Reciprocal cycles</strong>
 * <p>
 * A unit fraction contains 1 in the numerator. The decimal representation of
 * the unit fractions with denominators 2 to 10 are given:
 * <pre>
 * 1/2	= 0.5
 * 1/3	= 0.(3)
 * 1/4	= 0.25
 * 1/5	= 0.2
 * 1/6	= 0.1(6)
 * 1/7	= 0.(142857)
 * 1/8	= 0.125
 * 1/9	= 0.(1)
 * 1/10	= 0.1
 * </pre> Where 0.1(6) means 0.166666..., and has a 1-digit recurring cycle. It
 * can be seen that 1/7 has a 6-digit recurring cycle.
 * <p>
 * Find the value of d < 1000 for which 1/d contains the longest recurring cycle
 * in its decimal fraction part. <p>
 * See also
 * <a href="https://projecteuler.net/problem=26">projecteuler.net problem 26
 * </a>.
 */
public class Euler26Test {
    @Test
    public void shouldSolveProblem26() {
        assertThat(Euler26Test.recurringCycleLengthForDivisionOf1(2)._2).isEqualTo(0);
        assertThat(Euler26Test.recurringCycleLengthForDivisionOf1(3)._2).isEqualTo(1);
        assertThat(Euler26Test.recurringCycleLengthForDivisionOf1(4)._2).isEqualTo(0);
        assertThat(Euler26Test.recurringCycleLengthForDivisionOf1(5)._2).isEqualTo(0);
        assertThat(Euler26Test.recurringCycleLengthForDivisionOf1(6)._2).isEqualTo(1);
        assertThat(Euler26Test.recurringCycleLengthForDivisionOf1(7)._2).isEqualTo(6);
        assertThat(Euler26Test.recurringCycleLengthForDivisionOf1(8)._2).isEqualTo(0);
        assertThat(Euler26Test.recurringCycleLengthForDivisionOf1(9)._2).isEqualTo(1);
        assertThat(Euler26Test.recurringCycleLengthForDivisionOf1(10)._2).isEqualTo(0);
        assertThat(Euler26Test.denominatorBelow1000WithTheLongetsRecurringCycleOfDecimalFractions()).isEqualTo(983);
    }
}

