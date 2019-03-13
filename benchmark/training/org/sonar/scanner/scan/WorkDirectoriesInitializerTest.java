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


import DirectoryLock.LOCK_FILE_NAME;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;
import org.sonar.api.batch.fs.internal.DefaultInputModule;
import org.sonar.api.batch.fs.internal.InputModuleHierarchy;


public class WorkDirectoriesInitializerTest {
    private WorkDirectoriesInitializer initializer;

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    private File rootWorkDir;

    private File lock;

    private InputModuleHierarchy hierarchy;

    private DefaultInputModule root;

    @Test
    public void testNonExisting() {
        temp.delete();
        initializer.execute();
    }

    @Test
    public void testClean() {
        initializer.execute();
        assertThat(rootWorkDir).exists();
        assertThat(lock).exists();
        assertThat(rootWorkDir.list()).containsOnly(LOCK_FILE_NAME);
    }

    @Test
    public void cleaningRootModuleShouldNotDeleteChildrenWorkDir() throws IOException {
        DefaultInputModule moduleA = Mockito.mock(DefaultInputModule.class);
        Mockito.when(hierarchy.children(root)).thenReturn(Arrays.asList(moduleA));
        File moduleAWorkdir = new File(rootWorkDir, "moduleA");
        Mockito.when(moduleA.getWorkDir()).thenReturn(moduleAWorkdir.toPath());
        moduleAWorkdir.mkdir();
        new File(moduleAWorkdir, "fooA.txt").createNewFile();
        initializer.execute();
        assertThat(rootWorkDir).exists();
        assertThat(lock).exists();
        assertThat(rootWorkDir.list()).containsOnly(LOCK_FILE_NAME, "moduleA");
        assertThat(moduleAWorkdir).exists();
    }
}

