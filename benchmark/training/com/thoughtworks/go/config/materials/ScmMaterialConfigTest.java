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
package com.thoughtworks.go.config.materials;


import ScmMaterialConfig.FILTER;
import com.thoughtworks.go.config.ConfigSaveValidationContext;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class ScmMaterialConfigTest {
    private DummyMaterialConfig material;

    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void shouldSetFilterToNullWhenBlank() {
        setFilter(new Filter(new IgnoredFiles("*.*")));
        material.setConfigAttributes(Collections.singletonMap(FILTER, ""));
        Assert.assertThat(filter(), Matchers.is(new Filter()));
        Assert.assertThat(getFilterAsString(), Matchers.is(""));
    }

    @Test
    public void shouldReturnFilterForDisplay() {
        setFilter(new Filter(new IgnoredFiles("/foo/**.*"), new IgnoredFiles("/another/**.*"), new IgnoredFiles("bar")));
        Assert.assertThat(getFilterAsString(), Matchers.is("/foo/**.*,/another/**.*,bar"));
    }

    @Test
    public void shouldNotValidateEmptyDestinationFolder() {
        material.setConfigAttributes(Collections.singletonMap(ScmMaterialConfig.FOLDER, ""));
        material.validate(new ConfigSaveValidationContext(null));
        Assert.assertThat(material.errors.isEmpty(), Matchers.is(true));
    }

    @Test
    public void shouldSetFolderToNullWhenBlank() {
        material.setConfigAttributes(Collections.singletonMap(ScmMaterialConfig.FOLDER, "foo"));
        Assert.assertThat(getFolder(), Matchers.is(Matchers.not(Matchers.nullValue())));
        material.setConfigAttributes(Collections.singletonMap(ScmMaterialConfig.FOLDER, ""));
        Assert.assertThat(getFolder(), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void shouldUpdateAutoUpdateFieldFromConfigAttributes() {
        material.setConfigAttributes(Collections.singletonMap(ScmMaterialConfig.AUTO_UPDATE, "false"));
        Assert.assertThat(isAutoUpdate(), Matchers.is(false));
        material.setConfigAttributes(Collections.singletonMap(ScmMaterialConfig.AUTO_UPDATE, null));
        Assert.assertThat(isAutoUpdate(), Matchers.is(false));
        material.setConfigAttributes(Collections.singletonMap(ScmMaterialConfig.AUTO_UPDATE, "true"));
        Assert.assertThat(isAutoUpdate(), Matchers.is(true));
        setConfigAttributes(new HashMap());
        Assert.assertThat(isAutoUpdate(), Matchers.is(false));
        material.setConfigAttributes(Collections.singletonMap(ScmMaterialConfig.AUTO_UPDATE, null));
        Assert.assertThat(isAutoUpdate(), Matchers.is(false));
        material.setConfigAttributes(Collections.singletonMap(ScmMaterialConfig.AUTO_UPDATE, "random-stuff"));
        Assert.assertThat(isAutoUpdate(), Matchers.is(false));
    }

    @Test
    public void shouldFailValidationIfDestinationDirectoryIsNested() {
        setFolder("f1");
        validateNotSubdirectoryOf("f1/f2");
        Assert.assertFalse(errors().isEmpty());
        Assert.assertThat(errors().on(ScmMaterialConfig.FOLDER), Matchers.is("Invalid Destination Directory. Every material needs a different destination directory and the directories should not be nested."));
    }

    @Test
    public void shouldNotFailValidationIfDestinationDirectoryIsMultilevelButNotNested() {
        setFolder("f1/f2/f3");
        validateNotSubdirectoryOf("f1/f2/f");
        Assert.assertNull(errors().getAllOn(ScmMaterialConfig.FOLDER));
    }

    @Test
    public void shouldFailValidationIfDestinationDirectoryIsOutsideCurrentWorkingDirectoryAfterNormalization() {
        setFolder("f1/../../f3");
        validateConcreteMaterial(null);
        Assert.assertThat(errors().on(ScmMaterialConfig.FOLDER), Matchers.is("Dest folder 'f1/../../f3' is not valid. It must be a sub-directory of the working folder."));
    }

    @Test
    public void shouldFailValidationIfDestinationDirectoryIsNestedAfterNormalization() {
        setFolder("f1/f2/../../f3");
        validateNotSubdirectoryOf("f3/f4");
        Assert.assertThat(errors().on(ScmMaterialConfig.FOLDER), Matchers.is("Invalid Destination Directory. Every material needs a different destination directory and the directories should not be nested."));
    }

    @Test
    public void shouldNotValidateNestingOfMaterialDirectoriesBasedOnServerSideFileSystem() throws IOException {
        final File workingDir = temporaryFolder.newFolder("go-working-dir");
        final File material1 = new File(workingDir, "material1");
        material1.mkdirs();
        final Path material2 = Files.createSymbolicLink(Paths.get(new File(workingDir, "material2").getPath()), Paths.get(material1.getPath()));
        setFolder(material1.getAbsolutePath());
        validateNotSubdirectoryOf(material2.toAbsolutePath().toString());
        Assert.assertNull(errors().getAllOn(ScmMaterialConfig.FOLDER));
    }
}

