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
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import org.assertj.core.api.Assertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.sonar.ce.task.projectanalysis.component.Component;
import org.sonar.ce.task.projectanalysis.component.ReportComponent;


@RunWith(DataProviderRunner.class)
public class DuplicationRepositoryImplTest {
    private static final Component FILE_COMPONENT_1 = ReportComponent.builder(FILE, 1).build();

    private static final Component FILE_COMPONENT_2 = ReportComponent.builder(FILE, 2).build();

    private static final Duplication SOME_DUPLICATION = DuplicationRepositoryImplTest.createDuplication(1, 2);

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private DuplicationRepository underTest = new DuplicationRepositoryImpl();

    @Test
    public void getDuplications_throws_NPE_if_Component_argument_is_null() {
        expectFileArgumentNPE();
        underTest.getDuplications(null);
    }

    @Test
    public void getDuplications_returns_empty_set_when_repository_is_empty() {
        assertNoDuplication(DuplicationRepositoryImplTest.FILE_COMPONENT_1);
    }

    @Test
    public void add_throws_NPE_if_file_argument_is_null() {
        expectFileArgumentNPE();
        underTest.add(null, DuplicationRepositoryImplTest.SOME_DUPLICATION);
    }

    @Test
    public void addDuplication_inner_throws_NPE_if_duplication_argument_is_null() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("duplication can not be null");
        underTest.add(DuplicationRepositoryImplTest.FILE_COMPONENT_1, null);
    }

    @Test
    public void added_duplication_is_returned_as_is_by_getDuplications() {
        underTest.add(DuplicationRepositoryImplTest.FILE_COMPONENT_1, DuplicationRepositoryImplTest.SOME_DUPLICATION);
        Iterable<Duplication> duplications = underTest.getDuplications(DuplicationRepositoryImplTest.FILE_COMPONENT_1);
        Assertions.assertThat(duplications).hasSize(1);
        assertThat(duplications.iterator().next()).isSameAs(DuplicationRepositoryImplTest.SOME_DUPLICATION);
        assertNoDuplication(DuplicationRepositoryImplTest.FILE_COMPONENT_2);
    }

    @Test
    public void added_duplication_does_not_avoid_same_duplication_inserted_twice_but_only_one_is_returned() {
        underTest.add(DuplicationRepositoryImplTest.FILE_COMPONENT_1, DuplicationRepositoryImplTest.SOME_DUPLICATION);
        underTest.add(DuplicationRepositoryImplTest.FILE_COMPONENT_1, DuplicationRepositoryImplTest.SOME_DUPLICATION);
        Iterable<Duplication> duplications = underTest.getDuplications(DuplicationRepositoryImplTest.FILE_COMPONENT_1);
        Assertions.assertThat(duplications).hasSize(1);
        assertThat(duplications.iterator().next()).isSameAs(DuplicationRepositoryImplTest.SOME_DUPLICATION);
        assertNoDuplication(DuplicationRepositoryImplTest.FILE_COMPONENT_2);
    }

    @Test
    public void added_duplications_are_returned_in_any_order_and_associated_to_the_right_file() {
        underTest.add(DuplicationRepositoryImplTest.FILE_COMPONENT_1, DuplicationRepositoryImplTest.SOME_DUPLICATION);
        underTest.add(DuplicationRepositoryImplTest.FILE_COMPONENT_1, DuplicationRepositoryImplTest.createDuplication(2, 4));
        underTest.add(DuplicationRepositoryImplTest.FILE_COMPONENT_1, DuplicationRepositoryImplTest.createDuplication(2, 3));
        underTest.add(DuplicationRepositoryImplTest.FILE_COMPONENT_2, DuplicationRepositoryImplTest.createDuplication(2, 3));
        underTest.add(DuplicationRepositoryImplTest.FILE_COMPONENT_2, DuplicationRepositoryImplTest.createDuplication(1, 2));
        assertThat(underTest.getDuplications(DuplicationRepositoryImplTest.FILE_COMPONENT_1)).containsOnly(DuplicationRepositoryImplTest.SOME_DUPLICATION, DuplicationRepositoryImplTest.createDuplication(2, 3), DuplicationRepositoryImplTest.createDuplication(2, 4));
        assertThat(underTest.getDuplications(DuplicationRepositoryImplTest.FILE_COMPONENT_2)).containsOnly(DuplicationRepositoryImplTest.createDuplication(1, 2), DuplicationRepositoryImplTest.createDuplication(2, 3));
    }
}

