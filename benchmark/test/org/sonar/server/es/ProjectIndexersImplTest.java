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
package org.sonar.server.es;


import Cause.PROJECT_CREATION;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.db.DbSession;
import org.sonar.db.component.ComponentDto;
import org.sonar.db.component.ComponentTesting;
import org.sonar.db.organization.OrganizationDto;
import org.sonar.db.organization.OrganizationTesting;
import org.sonar.server.es.ProjectIndexer.Cause;


public class ProjectIndexersImplTest {
    @Test
    public void commitAndIndex_indexes_project() {
        OrganizationDto organization = OrganizationTesting.newOrganizationDto();
        ComponentDto project = ComponentTesting.newPublicProjectDto(organization);
        ProjectIndexersImplTest.FakeIndexers underTest = new ProjectIndexersImplTest.FakeIndexers();
        underTest.commitAndIndex(Mockito.mock(DbSession.class), Collections.singletonList(project), PROJECT_CREATION);
        assertThat(underTest.calls).containsExactly(project.uuid());
    }

    @Test
    public void commitAndIndex_of_module_indexes_the_project() {
        OrganizationDto organization = OrganizationTesting.newOrganizationDto();
        ComponentDto project = ComponentTesting.newPublicProjectDto(organization);
        ComponentDto module = ComponentTesting.newModuleDto(project);
        ProjectIndexersImplTest.FakeIndexers underTest = new ProjectIndexersImplTest.FakeIndexers();
        underTest.commitAndIndex(Mockito.mock(DbSession.class), Collections.singletonList(module), PROJECT_CREATION);
        assertThat(underTest.calls).containsExactly(project.uuid());
    }

    private static class FakeIndexers implements ProjectIndexers {
        private final List<String> calls = new ArrayList<>();

        @Override
        public void commitAndIndexByProjectUuids(DbSession dbSession, Collection<String> projectUuids, Cause cause) {
            calls.addAll(projectUuids);
        }
    }
}

