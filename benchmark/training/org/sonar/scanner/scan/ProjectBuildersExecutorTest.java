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
package org.sonar.scanner.scan;


import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.sonar.api.batch.bootstrap.ProjectBuilder;
import org.sonar.api.batch.bootstrap.ProjectBuilder.Context;
import org.sonar.api.batch.bootstrap.ProjectReactor;
import org.sonar.api.utils.MessageException;


public class ProjectBuildersExecutorTest {
    private ProjectReactor reactor;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void testProjectBuilderFailsWithToString() {
        ProjectBuilder builder = Mockito.mock(ProjectBuilder.class);
        Mockito.doThrow(new IllegalStateException()).when(builder).build(ArgumentMatchers.any(Context.class));
        ProjectBuilder[] projectBuilders = new ProjectBuilder[]{ builder };
        exception.expectMessage("Failed to execute project builder: Mock for ProjectBuilder");
        exception.expect(MessageException.class);
        new ProjectBuildersExecutor(projectBuilders).execute(reactor);
    }

    @Test
    public void testProjectBuilderFailsWithoutToString() {
        ProjectBuilder[] projectBuilders = new ProjectBuilder[]{ new ProjectBuildersExecutorTest.MyProjectBuilder() };
        exception.expectMessage("Failed to execute project builder: org.sonar.scanner.scan.ProjectBuildersExecutorTest$MyProjectBuilder");
        exception.expect(MessageException.class);
        new ProjectBuildersExecutor(projectBuilders).execute(reactor);
    }

    class MyProjectBuilder extends ProjectBuilder {
        @Override
        public void build(Context context) {
            throw new IllegalStateException();
        }
    }
}

