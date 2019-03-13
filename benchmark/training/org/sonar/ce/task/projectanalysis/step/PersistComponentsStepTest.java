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
package org.sonar.ce.task.projectanalysis.step;


import java.util.Collections;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.sonar.api.utils.System2;
import org.sonar.ce.task.projectanalysis.analysis.AnalysisMetadataHolder;
import org.sonar.ce.task.projectanalysis.component.BranchPersister;
import org.sonar.ce.task.projectanalysis.component.Component;
import org.sonar.ce.task.projectanalysis.component.MutableDbIdsRepository;
import org.sonar.ce.task.projectanalysis.component.MutableDisabledComponentsHolder;
import org.sonar.ce.task.projectanalysis.component.TreeRootHolder;
import org.sonar.ce.task.step.TestComputationStepContext;
import org.sonar.db.DbClient;
import org.sonar.db.DbSession;
import org.sonar.db.component.ComponentDao;


public class PersistComponentsStepTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void should_fail_if_project_is_not_stored_in_database_yet() {
        TreeRootHolder treeRootHolder = Mockito.mock(TreeRootHolder.class);
        Component component = Mockito.mock(Component.class);
        DbClient dbClient = Mockito.mock(DbClient.class);
        ComponentDao componentDao = Mockito.mock(ComponentDao.class);
        String projectKey = randomAlphabetic(20);
        Mockito.doReturn(component).when(treeRootHolder).getRoot();
        Mockito.doReturn(projectKey).when(component).getDbKey();
        Mockito.doReturn(componentDao).when(dbClient).componentDao();
        Mockito.doReturn(Collections.emptyList()).when(componentDao).selectAllComponentsFromProjectKey(ArgumentMatchers.any(DbSession.class), ArgumentMatchers.eq(projectKey));
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage((("The project '" + projectKey) + "' is not stored in the database, during a project analysis"));
        new PersistComponentsStep(dbClient, treeRootHolder, Mockito.mock(MutableDbIdsRepository.class), System2.INSTANCE, Mockito.mock(MutableDisabledComponentsHolder.class), Mockito.mock(AnalysisMetadataHolder.class), Mockito.mock(BranchPersister.class)).execute(new TestComputationStepContext());
    }
}

