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


import java.util.Optional;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.ce.task.projectanalysis.analysis.AnalysisMetadataHolderRule;
import org.sonar.ce.task.projectanalysis.analysis.Branch;
import org.sonar.db.DbTester;
import org.sonar.db.component.ComponentDto;


public class MergeBranchComponentUuidsTest {
    @Rule
    public AnalysisMetadataHolderRule analysisMetadataHolder = new AnalysisMetadataHolderRule();

    @Rule
    public DbTester db = DbTester.create();

    private MergeBranchComponentUuids underTest;

    private Branch branch = Mockito.mock(Branch.class);

    private ComponentDto mergeBranch;

    private ComponentDto mergeBranchFile;

    private ComponentDto branchFile;

    @Test
    public void should_support_db_key() {
        Mockito.when(branch.getMergeBranchUuid()).thenReturn(Optional.of(mergeBranch.uuid()));
        assertThat(underTest.getUuid(branchFile.getDbKey())).isEqualTo(mergeBranchFile.uuid());
    }

    @Test
    public void should_support_key() {
        Mockito.when(branch.getMergeBranchUuid()).thenReturn(Optional.of(mergeBranch.uuid()));
        assertThat(underTest.getUuid(branchFile.getKey())).isEqualTo(mergeBranchFile.uuid());
    }

    @Test
    public void return_null_if_file_doesnt_exist() {
        Mockito.when(branch.getMergeBranchUuid()).thenReturn(Optional.of(mergeBranch.uuid()));
        assertThat(underTest.getUuid("doesnt exist")).isNull();
    }
}

