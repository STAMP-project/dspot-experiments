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
package org.sonar.duplications.detector;


import java.util.Comparator;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.sonar.duplications.index.ClonePart;

import static ContainsInComparator.CLONEPART_COMPARATOR;
import static ContainsInComparator.RESOURCE_ID_COMPARATOR;


public class ContainsInComparatorTest {
    @Test
    public void shouldCompareByResourceId() {
        Comparator<ClonePart> comparator = RESOURCE_ID_COMPARATOR;
        Assert.assertThat(comparator.compare(ContainsInComparatorTest.newClonePart("a", 0), ContainsInComparatorTest.newClonePart("b", 0)), CoreMatchers.is((-1)));
        Assert.assertThat(comparator.compare(ContainsInComparatorTest.newClonePart("b", 0), ContainsInComparatorTest.newClonePart("a", 0)), CoreMatchers.is(1));
        Assert.assertThat(comparator.compare(ContainsInComparatorTest.newClonePart("a", 0), ContainsInComparatorTest.newClonePart("a", 0)), CoreMatchers.is(0));
    }

    @Test
    public void shouldCompareByResourceIdAndUnitStart() {
        Comparator<ClonePart> comparator = CLONEPART_COMPARATOR;
        Assert.assertThat(comparator.compare(ContainsInComparatorTest.newClonePart("a", 0), ContainsInComparatorTest.newClonePart("b", 0)), CoreMatchers.is((-1)));
        Assert.assertThat(comparator.compare(ContainsInComparatorTest.newClonePart("b", 0), ContainsInComparatorTest.newClonePart("a", 0)), CoreMatchers.is(1));
        Assert.assertThat(comparator.compare(ContainsInComparatorTest.newClonePart("a", 0), ContainsInComparatorTest.newClonePart("a", 0)), CoreMatchers.is(0));
        Assert.assertThat(comparator.compare(ContainsInComparatorTest.newClonePart("a", 0), ContainsInComparatorTest.newClonePart("a", 1)), CoreMatchers.is((-1)));
        Assert.assertThat(comparator.compare(ContainsInComparatorTest.newClonePart("a", 1), ContainsInComparatorTest.newClonePart("a", 0)), CoreMatchers.is(1));
    }

    @Test
    public void shouldCompare() {
        Assert.assertThat(ContainsInComparatorTest.compare("a", 0, 0, "b", 0, 0), CoreMatchers.is((-1)));
        Assert.assertThat(ContainsInComparatorTest.compare("b", 0, 0, "a", 0, 0), CoreMatchers.is(1));
        Assert.assertThat(ContainsInComparatorTest.compare("a", 0, 0, "a", 0, 0), CoreMatchers.is(0));
        Assert.assertThat(ContainsInComparatorTest.compare("a", 1, 0, "a", 0, 0), CoreMatchers.is(1));
        Assert.assertThat(ContainsInComparatorTest.compare("a", 0, 0, "a", 0, 1), CoreMatchers.is((-1)));
    }
}

