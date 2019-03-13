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
package org.sonar.scanner.scan.filesystem;


import java.io.IOException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.InputModule;
import org.sonar.api.batch.fs.internal.SensorStrategy;
import org.sonar.api.batch.fs.internal.TestInputFileBuilder;


public class ModuleInputComponentStoreTest {
    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    private InputComponentStore componentStore;

    private final String projectKey = "dummy key";

    @Test
    public void should_cache_files_by_filename() throws IOException {
        ModuleInputComponentStore store = newModuleInputComponentStore();
        String filename = "some name";
        InputFile inputFile1 = new TestInputFileBuilder(projectKey, ("some/path/" + filename)).build();
        store.doAdd(inputFile1);
        InputFile inputFile2 = new TestInputFileBuilder(projectKey, ("other/path/" + filename)).build();
        store.doAdd(inputFile2);
        InputFile dummyInputFile = new TestInputFileBuilder(projectKey, "some/path/Dummy.java").build();
        store.doAdd(dummyInputFile);
        assertThat(store.getFilesByName(filename)).containsExactlyInAnyOrder(inputFile1, inputFile2);
    }

    @Test
    public void should_cache_files_by_extension() throws IOException {
        ModuleInputComponentStore store = newModuleInputComponentStore();
        InputFile inputFile1 = new TestInputFileBuilder(projectKey, "some/path/Program.java").build();
        store.doAdd(inputFile1);
        InputFile inputFile2 = new TestInputFileBuilder(projectKey, "other/path/Utils.java").build();
        store.doAdd(inputFile2);
        InputFile dummyInputFile = new TestInputFileBuilder(projectKey, "some/path/NotJava.cpp").build();
        store.doAdd(dummyInputFile);
        assertThat(store.getFilesByExtension("java")).containsExactlyInAnyOrder(inputFile1, inputFile2);
    }

    @Test
    public void should_not_cache_duplicates() throws IOException {
        ModuleInputComponentStore store = newModuleInputComponentStore();
        String ext = "java";
        String filename = "Program." + ext;
        InputFile inputFile = new TestInputFileBuilder(projectKey, ("some/path/" + filename)).build();
        store.doAdd(inputFile);
        store.doAdd(inputFile);
        store.doAdd(inputFile);
        assertThat(store.getFilesByName(filename)).containsExactly(inputFile);
        assertThat(store.getFilesByExtension(ext)).containsExactly(inputFile);
    }

    @Test
    public void should_get_empty_iterable_on_cache_miss() {
        ModuleInputComponentStore store = newModuleInputComponentStore();
        String ext = "java";
        String filename = "Program." + ext;
        InputFile inputFile = new TestInputFileBuilder(projectKey, ("some/path/" + filename)).build();
        store.doAdd(inputFile);
        assertThat(store.getFilesByName("nonexistent")).isEmpty();
        assertThat(store.getFilesByExtension("nonexistent")).isEmpty();
    }

    @Test
    public void should_find_module_components_with_non_global_strategy() {
        InputComponentStore inputComponentStore = Mockito.mock(InputComponentStore.class);
        SensorStrategy strategy = new SensorStrategy();
        InputModule module = Mockito.mock(InputModule.class);
        Mockito.when(module.key()).thenReturn("foo");
        ModuleInputComponentStore store = new ModuleInputComponentStore(module, inputComponentStore, strategy);
        strategy.setGlobal(false);
        store.inputFiles();
        Mockito.verify(inputComponentStore).filesByModule("foo");
        String relativePath = "somepath";
        store.inputFile(relativePath);
        Mockito.verify(inputComponentStore).getFile(ArgumentMatchers.any(String.class), ArgumentMatchers.eq(relativePath));
        store.languages();
        Mockito.verify(inputComponentStore).languages(ArgumentMatchers.any(String.class));
    }

    @Test
    public void should_find_all_components_with_global_strategy() {
        InputComponentStore inputComponentStore = Mockito.mock(InputComponentStore.class);
        SensorStrategy strategy = new SensorStrategy();
        ModuleInputComponentStore store = new ModuleInputComponentStore(Mockito.mock(InputModule.class), inputComponentStore, strategy);
        strategy.setGlobal(true);
        store.inputFiles();
        Mockito.verify(inputComponentStore).inputFiles();
        String relativePath = "somepath";
        store.inputFile(relativePath);
        Mockito.verify(inputComponentStore).inputFile(relativePath);
        store.languages();
        Mockito.verify(inputComponentStore).languages();
    }
}

