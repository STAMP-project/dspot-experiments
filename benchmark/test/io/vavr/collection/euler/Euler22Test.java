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
 * <strong>Problem 22: Names scores</strong>
 * <p>
 * Using names.txt (right click and 'Save Link/Target As...'), a 46K text file
 * containing over five-thousand first names, begin by sorting it into
 * alphabetical order. Then working out the alphabetical value for each name,
 * multiply this value by its alphabetical position in the list to obtain a name
 * score.
 * <p>
 * For example, when the list is sorted into alphabetical order, COLIN, which is
 * worth 3 + 15 + 12 + 9 + 14 = 53, is the 938th name in the list. So, COLIN
 * would obtain a score of 938 ? 53 = 49714.
 * <p>
 * What is the total of all the name scores in the file?
 * <p>
 * See also <a href="https://projecteuler.net/problem=22">projecteuler.net
 * problem 22</a>.
 */
public class Euler22Test {
    @Test
    public void shouldSolveProblem22() {
        assertThat(Euler22Test.nameScore("COLIN", 938)).isEqualTo(49714);
        assertThat(Euler22Test.totalOfAllNameScores()).isEqualTo(871198282);
    }
}

