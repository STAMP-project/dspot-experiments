/**
 * SonarQube
 * Copyright (C) 2009-2019 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.core.issue.tracking;


import org.junit.Test;


public class BlockRecognizerTest {
    @Test
    public void lengthOfMaximalBlock() {
        /**
         * - line 4 of first sequence is "d"
         * - line 4 of second sequence is "d"
         * - in each sequence, the 3 lines before and the line after are similar -> block size is 5
         */
        assertThat(compute(BlockRecognizerTest.seq("abcde"), BlockRecognizerTest.seq("abcde"), 4, 4)).isEqualTo(5);
        assertThat(compute(BlockRecognizerTest.seq("abcde"), BlockRecognizerTest.seq("abcd"), 4, 4)).isEqualTo(4);
        assertThat(compute(BlockRecognizerTest.seq("bcde"), BlockRecognizerTest.seq("abcde"), 4, 4)).isEqualTo(0);
        assertThat(compute(BlockRecognizerTest.seq("bcde"), BlockRecognizerTest.seq("abcde"), 3, 4)).isEqualTo(4);
    }
}

