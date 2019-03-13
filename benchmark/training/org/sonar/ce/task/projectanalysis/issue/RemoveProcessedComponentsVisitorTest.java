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


import com.google.common.base.Optional;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.sonar.ce.task.projectanalysis.component.Component;
import org.sonar.ce.task.projectanalysis.filemove.MovedFilesRepository;
import org.sonar.ce.task.projectanalysis.filemove.MovedFilesRepository.OriginalFile;


public class RemoveProcessedComponentsVisitorTest {
    private static final String UUID = "uuid";

    private ComponentsWithUnprocessedIssues componentsWithUnprocessedIssues = Mockito.mock(ComponentsWithUnprocessedIssues.class);

    private MovedFilesRepository movedFilesRepository = Mockito.mock(MovedFilesRepository.class);

    private Component component = Mockito.mock(Component.class);

    private RemoveProcessedComponentsVisitor underTest = new RemoveProcessedComponentsVisitor(componentsWithUnprocessedIssues, movedFilesRepository);

    @Test
    public void remove_processed_files() {
        Mockito.when(movedFilesRepository.getOriginalFile(ArgumentMatchers.any(Component.class))).thenReturn(Optional.absent());
        underTest.afterComponent(component);
        Mockito.verify(movedFilesRepository).getOriginalFile(component);
        Mockito.verify(componentsWithUnprocessedIssues).remove(RemoveProcessedComponentsVisitorTest.UUID);
        Mockito.verifyNoMoreInteractions(componentsWithUnprocessedIssues);
    }

    @Test
    public void also_remove_moved_files() {
        String uuid2 = "uuid2";
        OriginalFile movedFile = new OriginalFile(0, uuid2, "key");
        Mockito.when(movedFilesRepository.getOriginalFile(ArgumentMatchers.any(Component.class))).thenReturn(Optional.of(movedFile));
        underTest.afterComponent(component);
        Mockito.verify(movedFilesRepository).getOriginalFile(component);
        Mockito.verify(componentsWithUnprocessedIssues).remove(RemoveProcessedComponentsVisitorTest.UUID);
        Mockito.verify(componentsWithUnprocessedIssues).remove(uuid2);
        Mockito.verifyNoMoreInteractions(componentsWithUnprocessedIssues);
    }
}

