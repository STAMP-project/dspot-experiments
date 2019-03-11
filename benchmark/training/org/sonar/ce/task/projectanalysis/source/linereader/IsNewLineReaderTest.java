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
package org.sonar.ce.task.projectanalysis.source.linereader;


import DbFileSources.Line.Builder;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.ce.task.projectanalysis.component.Component;
import org.sonar.ce.task.projectanalysis.source.NewLinesRepository;
import org.sonar.db.protobuf.DbFileSources;


public class IsNewLineReaderTest {
    private NewLinesRepository repository = Mockito.mock(NewLinesRepository.class);

    private Component component = Mockito.mock(Component.class);

    @Test
    public void should_set_isNewLines_in_builder() {
        Set<Integer> newLines = new HashSet<>(Arrays.asList(1, 3));
        Mockito.when(repository.getNewLines(component)).thenReturn(Optional.of(newLines));
        IsNewLineReader reader = new IsNewLineReader(repository, component);
        DbFileSources[] builders = runReader(reader);
        assertThat(builders[0].getIsNewLine()).isTrue();
        assertThat(builders[1].getIsNewLine()).isFalse();
        assertThat(builders[2].getIsNewLine()).isTrue();
    }

    @Test
    public void should_set_isNewLines_false_if_no_new_lines_available() {
        Mockito.when(repository.getNewLines(component)).thenReturn(Optional.empty());
        IsNewLineReader reader = new IsNewLineReader(repository, component);
        DbFileSources[] builders = runReader(reader);
        assertThat(builders[0].getIsNewLine()).isFalse();
        assertThat(builders[1].getIsNewLine()).isFalse();
        assertThat(builders[2].getIsNewLine()).isFalse();
    }
}

