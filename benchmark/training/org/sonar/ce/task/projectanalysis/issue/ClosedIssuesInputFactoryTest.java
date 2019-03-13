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
package org.sonar.ce.task.projectanalysis.issue;


import Component.Type.FILE;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import java.util.List;
import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.ce.task.projectanalysis.component.ReportComponent;
import org.sonar.ce.task.projectanalysis.filemove.MovedFilesRepository;
import org.sonar.core.issue.DefaultIssue;
import org.sonar.core.issue.tracking.Input;
import org.sonar.db.DbClient;


public class ClosedIssuesInputFactoryTest {
    private ComponentIssuesLoader issuesLoader = Mockito.mock(ComponentIssuesLoader.class);

    private DbClient dbClient = Mockito.mock(DbClient.class);

    private MovedFilesRepository movedFilesRepository = Mockito.mock(MovedFilesRepository.class);

    private ClosedIssuesInputFactory underTest = new ClosedIssuesInputFactory(issuesLoader, dbClient, movedFilesRepository);

    @Test
    public void underTest_returns_inputFactory_loading_closed_issues_only_when_getIssues_is_called() {
        String componentUuid = randomAlphanumeric(12);
        ReportComponent component = ReportComponent.builder(FILE, 1).setUuid(componentUuid).build();
        Mockito.when(movedFilesRepository.getOriginalFile(component)).thenReturn(Optional.absent());
        Input<DefaultIssue> input = underTest.create(component);
        Mockito.verifyZeroInteractions(dbClient, issuesLoader);
        List<DefaultIssue> issues = ImmutableList.of(new DefaultIssue(), new DefaultIssue());
        Mockito.when(issuesLoader.loadClosedIssues(componentUuid)).thenReturn(issues);
        assertThat(input.getIssues()).isSameAs(issues);
    }

    @Test
    public void underTest_returns_inputFactory_loading_closed_issues_from_moved_component_when_present() {
        String componentUuid = randomAlphanumeric(12);
        String originalComponentUuid = randomAlphanumeric(12);
        ReportComponent component = ReportComponent.builder(FILE, 1).setUuid(componentUuid).build();
        Mockito.when(movedFilesRepository.getOriginalFile(component)).thenReturn(Optional.of(new MovedFilesRepository.OriginalFile(1, originalComponentUuid, randomAlphanumeric(2))));
        Input<DefaultIssue> input = underTest.create(component);
        Mockito.verifyZeroInteractions(dbClient, issuesLoader);
        List<DefaultIssue> issues = ImmutableList.of();
        Mockito.when(issuesLoader.loadClosedIssues(originalComponentUuid)).thenReturn(issues);
        assertThat(input.getIssues()).isSameAs(issues);
    }

    @Test
    public void underTest_returns_inputFactory_which_caches_loaded_issues() {
        String componentUuid = randomAlphanumeric(12);
        ReportComponent component = ReportComponent.builder(FILE, 1).setUuid(componentUuid).build();
        Mockito.when(movedFilesRepository.getOriginalFile(component)).thenReturn(Optional.absent());
        Input<DefaultIssue> input = underTest.create(component);
        Mockito.verifyZeroInteractions(dbClient, issuesLoader);
        List<DefaultIssue> issues = ImmutableList.of(new DefaultIssue());
        Mockito.when(issuesLoader.loadClosedIssues(componentUuid)).thenReturn(issues);
        assertThat(input.getIssues()).isSameAs(issues);
        Mockito.reset(issuesLoader);
        assertThat(input.getIssues()).isSameAs(issues);
        Mockito.verifyZeroInteractions(issuesLoader);
    }
}

