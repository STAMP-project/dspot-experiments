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
package org.sonar.duplications.utils;


import java.util.Arrays;
import java.util.Comparator;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class SortedListsUtilsTest {
    @Test
    public void testContains() {
        Assert.assertThat(SortedListsUtilsTest.contains(Arrays.asList(1, 2, 3), Arrays.asList(1, 2)), CoreMatchers.is(true));
        Assert.assertThat(SortedListsUtilsTest.contains(Arrays.asList(1, 2), Arrays.asList(1, 2, 3)), CoreMatchers.is(false));
        Assert.assertThat(SortedListsUtilsTest.contains(Arrays.asList(1, 2, 3), Arrays.asList(1, 3)), CoreMatchers.is(true));
        Assert.assertThat(SortedListsUtilsTest.contains(Arrays.asList(1, 3), Arrays.asList(1, 2, 3)), CoreMatchers.is(false));
        Assert.assertThat(SortedListsUtilsTest.contains(Arrays.asList(1, 2, 3), Arrays.asList(1, 2, 2, 3)), CoreMatchers.is(true));
        Assert.assertThat(SortedListsUtilsTest.contains(Arrays.asList(1, 2, 2, 3), Arrays.asList(1, 2, 3)), CoreMatchers.is(true));
    }

    private static class IntegerComparator implements Comparator<Integer> {
        public static final SortedListsUtilsTest.IntegerComparator INSTANCE = new SortedListsUtilsTest.IntegerComparator();

        public int compare(Integer o1, Integer o2) {
            return o1 - o2;
        }
    }
}

