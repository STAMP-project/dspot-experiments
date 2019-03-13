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
package org.sonar.duplications.detector.original;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.duplications.index.CloneGroup;


public class FilterTest {
    /**
     * Given:
     * <pre>
     * c1: a[1-1]
     * c2: a[1-1]
     * </pre>
     * Expected:
     * reflexive - c1 in c1,
     * antisymmetric - c1 in c2, c2 in c1, because c1 = c2
     */
    @Test
    public void reflexive_and_antisymmetric() {
        CloneGroup c1 = newCloneGroup(1, newClonePart("a", 1));
        CloneGroup c2 = newCloneGroup(1, newClonePart("a", 1));
        Assert.assertThat(Filter.containsIn(c1, c1), CoreMatchers.is(true));
        Assert.assertThat(Filter.containsIn(c1, c2), CoreMatchers.is(true));
        Assert.assertThat(Filter.containsIn(c2, c1), CoreMatchers.is(true));
    }

    /**
     * Given:
     * <pre>
     * c1: a[1-1]
     * c2: a[2-2]
     * </pre>
     * Expected: c1 not in c2, c2 not in c1
     */
    @Test
    public void start_index_in_C1_less_than_in_C2() {
        CloneGroup c1 = newCloneGroup(1, newClonePart("a", 1));
        CloneGroup c2 = newCloneGroup(1, newClonePart("a", 2));
        Assert.assertThat(Filter.containsIn(c1, c2), CoreMatchers.is(false));
    }

    /**
     * Given:
     * <pre>
     * c1: a[0-0], a[2-2], b[0-0], b[2-2]
     * c2: a[0-2], b[0-2]
     * </pre>
     * Expected:
     * <pre>
     * c1 in c2 (all parts of c1 covered by parts of c2 and all resources the same)
     * c2 not in c1 (not all parts of c2 covered by parts of c1 and all resources the same)
     * </pre>
     */
    @Test
    public void one_part_of_C2_covers_two_parts_of_C1() {
        // Note that line numbers don't matter for method which we test.
        CloneGroup c1 = newCloneGroup(1, newClonePart("a", 0), newClonePart("a", 2), newClonePart("b", 0), newClonePart("b", 2));
        CloneGroup c2 = newCloneGroup(3, newClonePart("a", 0), newClonePart("b", 0));
        Assert.assertThat(Filter.containsIn(c1, c2), CoreMatchers.is(true));
        Assert.assertThat(Filter.containsIn(c2, c1), CoreMatchers.is(false));
    }

    /**
     * Given:
     * <pre>
     * c1: a[0-0], a[2-2]
     * c2: a[0-2], b[0-2]
     * </pre>
     * Expected:
     * <pre>
     * c1 not in c2 (all parts of c1 covered by parts of c2, but different resources)
     * c2 not in c1 (not all parts of c2 covered by parts of c1 and different resources)
     * </pre>
     */
    @Test
    public void different_resources() {
        CloneGroup c1 = newCloneGroup(1, newClonePart("a", 0), newClonePart("a", 2));
        CloneGroup c2 = newCloneGroup(3, newClonePart("a", 0), newClonePart("b", 0));
        Assert.assertThat(Filter.containsIn(c1, c2), CoreMatchers.is(false));
        Assert.assertThat(Filter.containsIn(c2, c1), CoreMatchers.is(false));
    }

    /**
     * Given:
     * <pre>
     * c1: a[2-2]
     * c2: a[0-1], a[2-3]
     * </pre>
     * Expected:
     * <pre>
     * c1 in c2
     * c2 not in c1
     * </pre>
     */
    @Test
    public void second_part_of_C2_covers_first_part_of_C1() {
        CloneGroup c1 = newCloneGroup(1, newClonePart("a", 2));
        CloneGroup c2 = newCloneGroup(2, newClonePart("a", 0), newClonePart("a", 2));
        Assert.assertThat(Filter.containsIn(c1, c2), CoreMatchers.is(true));
        Assert.assertThat(Filter.containsIn(c2, c1), CoreMatchers.is(false));
    }

    /**
     * Given:
     * <pre>
     * c1: a[0-2]
     * c2: a[0-0]
     * </pre>
     * Expected:
     * <pre>
     * c1 not in c2
     * </pre>
     */
    @Test
    public void length_of_C1_bigger_than_length_of_C2() {
        CloneGroup c1 = Mockito.spy(newCloneGroup(3, newClonePart("a", 0)));
        CloneGroup c2 = Mockito.spy(newCloneGroup(1, newClonePart("a", 0)));
        Assert.assertThat(Filter.containsIn(c1, c2), CoreMatchers.is(false));
        // containsIn method should check only origin and length - no need to compare all parts
        Mockito.verify(c1).getCloneUnitLength();
        Mockito.verify(c2).getCloneUnitLength();
        Mockito.verifyNoMoreInteractions(c1);
        Mockito.verifyNoMoreInteractions(c2);
    }
}

