/**
 * Copyright 2017 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.thoughtworks.go.domain.materials;


import com.thoughtworks.go.util.command.InMemoryStreamConsumer;
import java.io.File;
import java.io.IOException;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class DirectoryCleanerTest {
    private File baseFolder;

    private DirectoryCleaner cleaner;

    private InMemoryStreamConsumer consumer;

    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void shouldDoNothingIfDirectoryIsEmpty() {
        cleaner.allowed("non-existent");
        cleaner.clean();
        Assert.assertThat(baseFolder.exists(), Matchers.is(true));
    }

    @Test
    public void shouldNotCleanSvnDestIfExternalIsEnabled() {
        File svnDest = new File(baseFolder, "test1");
        File shouldExist = new File(svnDest, "shouldExist");
        shouldExist.mkdirs();
        File svnExternal = new File(baseFolder, "test1/external");
        svnExternal.mkdirs();
        cleaner.allowed("test1", "test1/subdir");
        cleaner.clean();
        Assert.assertThat(svnDest.exists(), Matchers.is(true));
        Assert.assertThat(shouldExist.exists(), Matchers.is(true));
    }

    @Test
    public void shouldKeepMaterialFolderIfItContainsOtherMaterials() {
        File material1 = mkdirDir(baseFolder, "material1");
        File dirOfMaterial1 = mkdirDir(material1, "dirOfMaterial1");
        File material2 = mkdirDir(material1, "material2");
        File oldMaterial3 = mkdirDir(baseFolder, "oldMaterial3");
        cleaner.allowed("material1", "material1/material2");
        cleaner.clean();
        Assert.assertThat(material1.exists(), Matchers.is(true));
        Assert.assertThat(dirOfMaterial1.exists(), Matchers.is(true));
        Assert.assertThat(material2.exists(), Matchers.is(true));
        Assert.assertThat(oldMaterial3.exists(), Matchers.is(false));
    }

    @Test
    public void shouldRemoveExtraDirectoriesInRootFolder() {
        File notAllowed = new File(baseFolder, "notAllowed");
        notAllowed.mkdirs();
        cleaner.allowed("allowed");
        cleaner.clean();
        Assert.assertThat(baseFolder.exists(), Matchers.is(true));
        Assert.assertThat(notAllowed.exists(), Matchers.is(false));
    }

    @Test
    public void shouldNotRemoveAllowedDirectoriesInRootFolder() {
        File allowedFolder = new File(baseFolder, "allowed");
        allowedFolder.mkdir();
        cleaner.allowed("allowed");
        cleaner.clean();
        Assert.assertThat(baseFolder.exists(), Matchers.is(true));
        Assert.assertThat(allowedFolder.exists(), Matchers.is(true));
    }

    @Test
    public void shouldNotRemoveAllowedDirectoriesInSubfolder() {
        File allowedFolder = new File(baseFolder, "subfolder/allowed");
        allowedFolder.mkdirs();
        cleaner.allowed("subfolder/allowed");
        cleaner.clean();
        Assert.assertThat(baseFolder.exists(), Matchers.is(true));
        Assert.assertThat(allowedFolder.getParentFile().exists(), Matchers.is(true));
        Assert.assertThat(allowedFolder.exists(), Matchers.is(true));
    }

    @Test
    public void shouldRemoveNotAllowedDirectoriesInSubfolder() {
        File allowedFolder = new File(baseFolder, "subfolder/allowed");
        allowedFolder.mkdirs();
        File notAllowedFolder = new File(baseFolder, "subfolder/notAllowed");
        notAllowedFolder.mkdirs();
        cleaner.allowed("subfolder/allowed");
        cleaner.clean();
        Assert.assertThat(baseFolder.exists(), Matchers.is(true));
        Assert.assertThat(allowedFolder.getParentFile().exists(), Matchers.is(true));
        Assert.assertThat(notAllowedFolder.exists(), Matchers.is(false));
    }

    @Test
    public void shouldDoNothingIfSubdirectoryDoesNotExist() {
        File allowedFolder = new File(baseFolder, "subfolder/allowed");
        cleaner.allowed("subfolder/allowed");
        cleaner.clean();
        Assert.assertThat(baseFolder.exists(), Matchers.is(true));
        Assert.assertThat(allowedFolder.exists(), Matchers.is(false));
    }

    @Test
    public void shouldNotRemoveAnythingIfNoAllowedWasSet() {
        File allowedFolder = new File(baseFolder, "subfolder/allowed");
        allowedFolder.mkdirs();
        cleaner.clean();
        Assert.assertThat(baseFolder.exists(), Matchers.is(true));
        Assert.assertThat(allowedFolder.exists(), Matchers.is(true));
    }

    @Test
    public void shouldNotProcessFilesOutsideTheBaseFolder() {
        try {
            cleaner.allowed("/../..");
            Assert.fail("Should not allow file outside the baseDirectory");
        } catch (Exception e) {
            Assert.assertThat(e.getMessage(), Matchers.containsString((("Folder " + (new File(baseFolder, "/../..").getAbsolutePath())) + " is outside the base folder")));
        }
    }

    @Test
    public void shouldReportDeletingFiles() throws IOException {
        File allowedFolder = new File(baseFolder, "subfolder/allowed");
        allowedFolder.mkdirs();
        File notAllowedFolder = new File(baseFolder, "subfolder/notallowed");
        notAllowedFolder.mkdirs();
        cleaner.allowed("subfolder/allowed");
        cleaner.clean();
        Assert.assertThat(consumer.getStdOut(), Matchers.containsString(("Deleting folder " + (notAllowedFolder.getPath()))));
        Assert.assertThat(consumer.getStdOut(), Matchers.containsString(("Keeping folder " + (allowedFolder.getPath()))));
    }
}

