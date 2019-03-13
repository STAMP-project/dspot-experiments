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
package org.sonar.ce.task.projectanalysis.duplication;


import Component.Type.FILE;
import com.google.common.collect.ImmutableSet;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.sonar.ce.task.projectanalysis.component.ReportComponent;


public class DuplicationTest {
    private static final TextBlock SOME_ORIGINAL_TEXTBLOCK = new TextBlock(1, 2);

    private static final TextBlock TEXT_BLOCK_1 = new TextBlock(2, 2);

    private static final TextBlock TEXT_BLOCK_2 = new TextBlock(2, 3);

    private static final ReportComponent FILE_COMPONENT_1 = ReportComponent.builder(FILE, 1).build();

    private static final ReportComponent FILE_COMPONENT_2 = ReportComponent.builder(FILE, 2).build();

    private static final String FILE_KEY_1 = "1";

    private static final String FILE_KEY_2 = "2";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void constructor_throws_NPE_if_original_is_null() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("original TextBlock can not be null");
        new Duplication(null, Collections.emptySet());
    }

    @Test
    public void constructor_throws_NPE_if_duplicates_is_null() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("duplicates can not be null");
        new Duplication(DuplicationTest.SOME_ORIGINAL_TEXTBLOCK, null);
    }

    @Test
    public void constructor_throws_IAE_if_duplicates_is_empty() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("duplicates can not be empty");
        new Duplication(DuplicationTest.SOME_ORIGINAL_TEXTBLOCK, Collections.emptySet());
    }

    @Test
    public void constructor_throws_NPE_if_duplicates_contains_null() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("duplicates can not contain null");
        new Duplication(DuplicationTest.SOME_ORIGINAL_TEXTBLOCK, new HashSet(Arrays.asList(Mockito.mock(Duplicate.class), null, Mockito.mock(Duplicate.class))));
    }

    @Test
    public void constructor_throws_IAE_if_duplicates_contains_InnerDuplicate_of_original() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("TextBlock of an InnerDuplicate can not be the original TextBlock");
        new Duplication(DuplicationTest.SOME_ORIGINAL_TEXTBLOCK, new HashSet(Arrays.asList(Mockito.mock(Duplicate.class), new InnerDuplicate(DuplicationTest.SOME_ORIGINAL_TEXTBLOCK), Mockito.mock(Duplicate.class))));
    }

    @Test
    public void constructor_throws_IAE_when_attempting_to_sort_Duplicate_of_unkown_type() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(("Unsupported type of Duplicate " + (DuplicationTest.MyDuplicate.class.getName())));
        new Duplication(DuplicationTest.SOME_ORIGINAL_TEXTBLOCK, ImmutableSet.of(new DuplicationTest.MyDuplicate(), new DuplicationTest.MyDuplicate()));
    }

    private static final class MyDuplicate implements Duplicate {
        @Override
        public TextBlock getTextBlock() {
            throw new UnsupportedOperationException("getTextBlock not implemented");
        }
    }

    @Test
    public void getOriginal_returns_original() {
        assertThat(getOriginal()).isSameAs(DuplicationTest.SOME_ORIGINAL_TEXTBLOCK);
    }

    @Test
    public void getDuplicates_sorts_duplicates_by_Inner_then_InProject_then_CrossProject() {
        CrossProjectDuplicate crossProjectDuplicate = new CrossProjectDuplicate("some key", DuplicationTest.TEXT_BLOCK_1);
        InProjectDuplicate inProjectDuplicate = new InProjectDuplicate(DuplicationTest.FILE_COMPONENT_1, DuplicationTest.TEXT_BLOCK_1);
        InnerDuplicate innerDuplicate = new InnerDuplicate(DuplicationTest.TEXT_BLOCK_1);
        Duplication duplication = new Duplication(DuplicationTest.SOME_ORIGINAL_TEXTBLOCK, DuplicationTest.shuffledList(crossProjectDuplicate, inProjectDuplicate, innerDuplicate));
        assertThat(duplication.getDuplicates()).containsExactly(innerDuplicate, inProjectDuplicate, crossProjectDuplicate);
    }

    @Test
    public void getDuplicates_sorts_duplicates_of_InnerDuplicate_by_TextBlock() {
        InnerDuplicate innerDuplicate1 = new InnerDuplicate(DuplicationTest.TEXT_BLOCK_2);
        InnerDuplicate innerDuplicate2 = new InnerDuplicate(new TextBlock(3, 3));
        InnerDuplicate innerDuplicate3 = new InnerDuplicate(new TextBlock(3, 4));
        InnerDuplicate innerDuplicate4 = new InnerDuplicate(new TextBlock(4, 4));
        assertGetDuplicatesSorting(innerDuplicate1, innerDuplicate2, innerDuplicate3, innerDuplicate4);
    }

    @Test
    public void getDuplicates_sorts_duplicates_of_InProjectDuplicate_by_component_then_TextBlock() {
        InProjectDuplicate innerDuplicate1 = new InProjectDuplicate(DuplicationTest.FILE_COMPONENT_1, DuplicationTest.TEXT_BLOCK_1);
        InProjectDuplicate innerDuplicate2 = new InProjectDuplicate(DuplicationTest.FILE_COMPONENT_1, DuplicationTest.TEXT_BLOCK_2);
        InProjectDuplicate innerDuplicate3 = new InProjectDuplicate(DuplicationTest.FILE_COMPONENT_2, DuplicationTest.TEXT_BLOCK_1);
        InProjectDuplicate innerDuplicate4 = new InProjectDuplicate(DuplicationTest.FILE_COMPONENT_2, DuplicationTest.TEXT_BLOCK_2);
        assertGetDuplicatesSorting(innerDuplicate1, innerDuplicate2, innerDuplicate3, innerDuplicate4);
    }

    @Test
    public void getDuplicates_sorts_duplicates_of_CrossProjectDuplicate_by_fileKey_then_TextBlock() {
        CrossProjectDuplicate innerDuplicate1 = new CrossProjectDuplicate(DuplicationTest.FILE_KEY_1, DuplicationTest.TEXT_BLOCK_1);
        CrossProjectDuplicate innerDuplicate2 = new CrossProjectDuplicate(DuplicationTest.FILE_KEY_1, DuplicationTest.TEXT_BLOCK_2);
        CrossProjectDuplicate innerDuplicate3 = new CrossProjectDuplicate(DuplicationTest.FILE_KEY_2, DuplicationTest.TEXT_BLOCK_1);
        CrossProjectDuplicate innerDuplicate4 = new CrossProjectDuplicate(DuplicationTest.FILE_KEY_2, DuplicationTest.TEXT_BLOCK_2);
        assertGetDuplicatesSorting(innerDuplicate1, innerDuplicate2, innerDuplicate3, innerDuplicate4);
    }

    @Test
    public void equals_compares_on_original_and_duplicates() {
        Duplication duplication = new Duplication(DuplicationTest.SOME_ORIGINAL_TEXTBLOCK, Arrays.asList(new InnerDuplicate(DuplicationTest.TEXT_BLOCK_1)));
        assertThat(duplication).isEqualTo(duplication);
        assertThat(duplication).isEqualTo(new Duplication(DuplicationTest.SOME_ORIGINAL_TEXTBLOCK, Arrays.asList(new InnerDuplicate(DuplicationTest.TEXT_BLOCK_1))));
        assertThat(duplication).isNotEqualTo(new Duplication(DuplicationTest.SOME_ORIGINAL_TEXTBLOCK, Arrays.asList(new InnerDuplicate(DuplicationTest.TEXT_BLOCK_2))));
        assertThat(duplication).isNotEqualTo(new Duplication(DuplicationTest.TEXT_BLOCK_1, Arrays.asList(new InnerDuplicate(DuplicationTest.SOME_ORIGINAL_TEXTBLOCK))));
    }

    @Test
    public void hashcode_is_based_on_original_only() {
        Duplication duplication = new Duplication(DuplicationTest.SOME_ORIGINAL_TEXTBLOCK, Arrays.asList(new InnerDuplicate(DuplicationTest.TEXT_BLOCK_1)));
        assertThat(duplication.hashCode()).isEqualTo(new Duplication(DuplicationTest.SOME_ORIGINAL_TEXTBLOCK, Arrays.asList(new InnerDuplicate(DuplicationTest.TEXT_BLOCK_1))).hashCode());
        assertThat(duplication.hashCode()).isNotEqualTo(new Duplication(DuplicationTest.SOME_ORIGINAL_TEXTBLOCK, Arrays.asList(new InnerDuplicate(DuplicationTest.TEXT_BLOCK_2))).hashCode());
        assertThat(duplication.hashCode()).isNotEqualTo(new Duplication(DuplicationTest.TEXT_BLOCK_1, Arrays.asList(new InnerDuplicate(DuplicationTest.SOME_ORIGINAL_TEXTBLOCK))).hashCode());
    }

    @Test
    public void verify_toString() {
        Duplication duplication = new Duplication(DuplicationTest.SOME_ORIGINAL_TEXTBLOCK, Arrays.asList(new InnerDuplicate(DuplicationTest.TEXT_BLOCK_1)));
        assertThat(duplication.toString()).isEqualTo("Duplication{original=TextBlock{start=1, end=2}, duplicates=[InnerDuplicate{textBlock=TextBlock{start=2, end=2}}]}");
    }
}

