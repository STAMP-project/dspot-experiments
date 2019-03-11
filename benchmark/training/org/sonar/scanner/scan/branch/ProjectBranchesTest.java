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
package org.sonar.scanner.scan.branch;


import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;

import static BranchType.LONG;
import static BranchType.PULL_REQUEST;
import static BranchType.SHORT;


@RunWith(DataProviderRunner.class)
public class ProjectBranchesTest {
    private static final BranchInfo mainBranch = new BranchInfo("main", LONG, true, null);

    private static final BranchInfo shortBranch = new BranchInfo("short", SHORT, false, null);

    private static final BranchInfo longBranch = new BranchInfo("long", LONG, false, null);

    private static final BranchInfo pullRequest = new BranchInfo("pull-request", PULL_REQUEST, false, null);

    private static final List<BranchInfo> nonMainBranches = Arrays.asList(ProjectBranchesTest.shortBranch, ProjectBranchesTest.longBranch, ProjectBranchesTest.pullRequest);

    private static final List<BranchInfo> allBranches = Arrays.asList(ProjectBranchesTest.shortBranch, ProjectBranchesTest.longBranch, ProjectBranchesTest.pullRequest, ProjectBranchesTest.mainBranch);

    private final ProjectBranches underTest = new ProjectBranches(ProjectBranchesTest.allBranches);

    @Test
    public void defaultBranchName() {
        for (int i = 0; i <= (ProjectBranchesTest.nonMainBranches.size()); i++) {
            List<BranchInfo> branches = new java.util.ArrayList(ProjectBranchesTest.nonMainBranches);
            branches.add(i, ProjectBranchesTest.mainBranch);
            assertThat(new ProjectBranches(branches).defaultBranchName()).isEqualTo(ProjectBranchesTest.mainBranch.name());
        }
    }

    @Test
    public void isEmpty() {
        assertThat(underTest.isEmpty()).isFalse();
        assertThat(new ProjectBranches(Collections.emptyList()).isEmpty()).isTrue();
    }
}

