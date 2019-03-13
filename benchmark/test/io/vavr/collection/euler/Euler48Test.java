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


public class Euler48Test {
    private static final long MOD = 10000000000L;

    /**
     * <strong>Problem 48: Self powers</strong>
     * <p>
     * The series, 1<sup>1</sup> + 2<sup>2</sup> + 3<sup>3</sup> + ... + 10<sup>10</sup> = 10405071317.
     * <p>
     * Find the last ten digits of the series, 1<sup>1</sup> + 2<sup>2</sup> + 3<sup>3</sup> + ... + 1000<sup>1000</sup>.
     * <p>
     * See also <a href="https://projecteuler.net/problem=48">projecteuler.net problem 48</a>.
     */
    @Test
    public void shouldSolveProblem48() {
        assertThat(Euler48Test.sumPowers(10)).isEqualTo(405071317L);
        assertThat(Euler48Test.sumPowers(1000)).isEqualTo(9110846700L);
    }
}

