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


import io.vavr.Tuple;
import org.junit.Test;


public class Euler33Test {
    /**
     * <strong>Problem 33: Digit cancelling fractions</strong>
     * <p>
     * The fraction 49/98 is a curious fraction, as an inexperienced
     * mathematician in attempting to simplify it may incorrectly believe that
     * 49/98 = 4/8, which is correct, is obtained by cancelling the 9s.
     * <p>
     * We shall consider fractions like, 30/50 = 3/5, to be trivial examples.
     * <p>
     * There are exactly four non-trivial examples of this type of fraction,
     * less than one in value, and containing two digits in the numerator and
     * denominator.
     * <p>
     * If the product of these four fractions is given in its lowest common
     * terms, find the value of the denominator.
     * <p>
     * See also <a href="https://projecteuler.net/problem=33">projecteuler.net
     * problem 33</a>.
     */
    @Test
    public void shouldSolveProblem33() {
        assertThat(Euler33Test.isNonTrivialDigitCancellingFraction(Tuple.of(49, 98))).isTrue();
        assertThat(Euler33Test.isNonTrivialDigitCancellingFraction(Tuple.of(30, 50))).isFalse();
        assertThat(Euler33Test.isNonTrivialDigitCancellingFraction(Tuple.of(21, 22))).isFalse();
        assertThat(Euler33Test.lowestCommonDenominatorOfProductOfTheFourNonTrivialdigitCancellingFractions()).isEqualTo(100);
    }
}

