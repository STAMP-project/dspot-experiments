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


import java.util.Arrays;
import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.api.batch.bootstrap.ProjectDefinition;
import org.sonar.api.batch.fs.internal.DefaultInputModule;
import org.sonar.api.batch.fs.internal.DefaultInputProject;
import org.sonar.scanner.scan.filesystem.InputComponentStore;


public class ModuleIndexerTest {
    private ModuleIndexer indexer;

    private DefaultInputModuleHierarchy moduleHierarchy;

    private InputComponentStore componentStore;

    @Test
    public void testIndex() {
        ProjectDefinition rootDef = Mockito.mock(ProjectDefinition.class);
        ProjectDefinition def = Mockito.mock(ProjectDefinition.class);
        Mockito.when(rootDef.getParent()).thenReturn(null);
        Mockito.when(def.getParent()).thenReturn(rootDef);
        DefaultInputModule root = Mockito.mock(DefaultInputModule.class);
        DefaultInputModule mod1 = Mockito.mock(DefaultInputModule.class);
        DefaultInputModule mod2 = Mockito.mock(DefaultInputModule.class);
        DefaultInputModule mod3 = Mockito.mock(DefaultInputModule.class);
        Mockito.when(root.key()).thenReturn("root");
        Mockito.when(mod1.key()).thenReturn("mod1");
        Mockito.when(mod2.key()).thenReturn("mod2");
        Mockito.when(mod3.key()).thenReturn("mod3");
        Mockito.when(root.getKeyWithBranch()).thenReturn("root");
        Mockito.when(mod1.getKeyWithBranch()).thenReturn("mod1");
        Mockito.when(mod2.getKeyWithBranch()).thenReturn("mod2");
        Mockito.when(mod3.getKeyWithBranch()).thenReturn("mod3");
        Mockito.when(root.definition()).thenReturn(rootDef);
        Mockito.when(mod1.definition()).thenReturn(def);
        Mockito.when(mod2.definition()).thenReturn(def);
        Mockito.when(mod3.definition()).thenReturn(def);
        createIndexer(Mockito.mock(DefaultInputProject.class));
        Mockito.when(moduleHierarchy.root()).thenReturn(root);
        Mockito.when(moduleHierarchy.children(root)).thenReturn(Arrays.asList(mod1, mod2, mod3));
        indexer.start();
        DefaultInputModule rootModule = moduleHierarchy.root();
        assertThat(rootModule).isNotNull();
        assertThat(moduleHierarchy.children(rootModule)).hasSize(3);
    }
}

