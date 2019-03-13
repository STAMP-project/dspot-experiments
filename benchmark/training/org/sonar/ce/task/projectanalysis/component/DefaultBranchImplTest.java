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
package org.sonar.ce.task.projectanalysis.component;


import BranchDto.DEFAULT_MAIN_BRANCH_NAME;
import BranchType.LONG;
import ComponentType.FILE;
import ScannerReport.Component;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class DefaultBranchImplTest {
    private static final String PROJECT_KEY = "P";

    private static final Component FILE = Component.newBuilder().setType(ComponentType.FILE).setProjectRelativePath("src/Foo.js").build();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void throw_ME_if_name_contains_invalid_characters() {
        assertThatNameIsCorrect("master");
        assertThatNameIsCorrect("feature/foo");
        assertThatNameIsCorrect("feature_foo");
        assertThatNameIsNotCorrect("feature foo");
        assertThatNameIsNotCorrect("feature#foo");
    }

    @Test
    public void default_branch_represents_the_project() {
        DefaultBranchImpl branch = new DefaultBranchImpl();
        assertThat(branch.isMain()).isTrue();
        assertThat(branch.getType()).isEqualTo(LONG);
        assertThat(branch.getName()).isEqualTo(DEFAULT_MAIN_BRANCH_NAME);
        assertThat(branch.supportsCrossProjectCpd()).isTrue();
        assertThat(branch.generateKey(DefaultBranchImplTest.PROJECT_KEY, null)).isEqualTo("P");
        assertThat(branch.generateKey(DefaultBranchImplTest.PROJECT_KEY, DefaultBranchImplTest.FILE.getProjectRelativePath())).isEqualTo("P:src/Foo.js");
    }

    @Test
    public void branch_represents_a_forked_project_with_different_key() {
        DefaultBranchImpl branch = new DefaultBranchImpl("bar");
        // not a real branch. Parameter sonar.branch forks project.
        assertThat(branch.isMain()).isTrue();
        assertThat(branch.getType()).isEqualTo(LONG);
        assertThat(branch.getName()).isEqualTo("bar");
        assertThat(branch.supportsCrossProjectCpd()).isFalse();
        assertThat(branch.generateKey(DefaultBranchImplTest.PROJECT_KEY, null)).isEqualTo("P:bar");
        assertThat(branch.generateKey(DefaultBranchImplTest.PROJECT_KEY, DefaultBranchImplTest.FILE.getProjectRelativePath())).isEqualTo("P:bar:src/Foo.js");
    }
}

