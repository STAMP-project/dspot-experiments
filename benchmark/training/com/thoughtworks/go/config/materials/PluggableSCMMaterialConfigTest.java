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


import PluggableSCMMaterialConfig.FILTER;
import PluggableSCMMaterialConfig.FOLDER;
import PluggableSCMMaterialConfig.SCM_ID;
import com.thoughtworks.go.config.CaseInsensitiveString;
import com.thoughtworks.go.config.ConfigSaveValidationContext;
import com.thoughtworks.go.config.PipelineConfigSaveValidationContext;
import com.thoughtworks.go.config.materials.git.GitMaterialConfig;
import com.thoughtworks.go.domain.packagerepository.ConfigurationPropertyMother;
import com.thoughtworks.go.domain.scm.SCM;
import com.thoughtworks.go.domain.scm.SCMMother;
import com.thoughtworks.go.plugin.access.scm.SCMMetadataStore;
import com.thoughtworks.go.plugin.access.scm.SCMView;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class PluggableSCMMaterialConfigTest {
    private PluggableSCMMaterialConfig pluggableSCMMaterialConfig;

    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void shouldAddErrorIfMaterialDoesNotHaveASCMId() throws Exception {
        pluggableSCMMaterialConfig.setScmId(null);
        pluggableSCMMaterialConfig.validateConcreteMaterial(new ConfigSaveValidationContext(null, null));
        Assert.assertThat(pluggableSCMMaterialConfig.errors().getAll().size(), Matchers.is(1));
        Assert.assertThat(pluggableSCMMaterialConfig.errors().on(SCM_ID), Matchers.is("Please select a SCM"));
    }

    @Test
    public void shouldAddErrorIfSCMNameUniquenessValidationFails() throws Exception {
        Map<CaseInsensitiveString, AbstractMaterialConfig> nameToMaterialMap = new HashMap<>();
        PluggableSCMMaterialConfig existingMaterial = new PluggableSCMMaterialConfig("scm-id");
        nameToMaterialMap.put(new CaseInsensitiveString("scm-id"), existingMaterial);
        nameToMaterialMap.put(new CaseInsensitiveString("foo"), new GitMaterialConfig("url"));
        pluggableSCMMaterialConfig.validateNameUniqueness(nameToMaterialMap);
        Assert.assertThat(pluggableSCMMaterialConfig.errors().getAll().size(), Matchers.is(1));
        Assert.assertThat(pluggableSCMMaterialConfig.errors().on(SCM_ID), Matchers.is("Duplicate SCM material detected!"));
        Assert.assertThat(existingMaterial.errors().getAll().size(), Matchers.is(1));
        Assert.assertThat(existingMaterial.errors().on(SCM_ID), Matchers.is("Duplicate SCM material detected!"));
        Assert.assertThat(nameToMaterialMap.size(), Matchers.is(2));
    }

    @Test
    public void shouldPassMaterialUniquenessIfIfNoDuplicateSCMFound() throws Exception {
        Map<CaseInsensitiveString, AbstractMaterialConfig> nameToMaterialMap = new HashMap<>();
        nameToMaterialMap.put(new CaseInsensitiveString("scm-id-new"), new PluggableSCMMaterialConfig("scm-id-new"));
        nameToMaterialMap.put(new CaseInsensitiveString("foo"), new GitMaterialConfig("url"));
        pluggableSCMMaterialConfig.validateNameUniqueness(nameToMaterialMap);
        Assert.assertThat(pluggableSCMMaterialConfig.errors().getAll().size(), Matchers.is(0));
        Assert.assertThat(nameToMaterialMap.size(), Matchers.is(3));
    }

    @Test
    public void shouldNotAddErrorDuringUniquenessValidationIfSCMNameIsEmpty() throws Exception {
        pluggableSCMMaterialConfig.setScmId("");
        Map<CaseInsensitiveString, AbstractMaterialConfig> nameToMaterialMap = new HashMap<>();
        pluggableSCMMaterialConfig.validateNameUniqueness(nameToMaterialMap);
        Assert.assertThat(pluggableSCMMaterialConfig.errors().getAll().size(), Matchers.is(0));
        Assert.assertThat(nameToMaterialMap.size(), Matchers.is(0));
    }

    @Test
    public void shouldAddErrorIDestinationIsNotValid() throws Exception {
        ConfigSaveValidationContext configSaveValidationContext = Mockito.mock(ConfigSaveValidationContext.class);
        SCM scmConfig = Mockito.mock(SCM.class);
        Mockito.when(configSaveValidationContext.findScmById(ArgumentMatchers.anyString())).thenReturn(scmConfig);
        Mockito.when(scmConfig.doesPluginExist()).thenReturn(true);
        PluggableSCMMaterialConfig pluggableSCMMaterialConfig = new PluggableSCMMaterialConfig(null, scmConfig, "/usr/home", null);
        pluggableSCMMaterialConfig.setScmId("scm-id");
        pluggableSCMMaterialConfig.validateConcreteMaterial(configSaveValidationContext);
        Assert.assertThat(pluggableSCMMaterialConfig.errors().getAll().size(), Matchers.is(1));
        Assert.assertThat(pluggableSCMMaterialConfig.errors().on(FOLDER), Matchers.is("Dest folder '/usr/home' is not valid. It must be a sub-directory of the working folder."));
        pluggableSCMMaterialConfig = new PluggableSCMMaterialConfig(null, scmConfig, "./../crap", null);
        pluggableSCMMaterialConfig.setScmId("scm-id");
        pluggableSCMMaterialConfig.validateConcreteMaterial(configSaveValidationContext);
        Assert.assertThat(pluggableSCMMaterialConfig.errors().getAll().size(), Matchers.is(2));
        Assert.assertThat(pluggableSCMMaterialConfig.errors().on(FOLDER), Matchers.is("Invalid directory name './../crap'. It should be a valid relative path."));
    }

    @Test
    public void shouldAddErrorWhenMatchingScmConfigDoesNotExist() throws Exception {
        PipelineConfigSaveValidationContext validationContext = Mockito.mock(PipelineConfigSaveValidationContext.class);
        Mockito.when(validationContext.findScmById(ArgumentMatchers.anyString())).thenReturn(null);
        SCM scmConfig = Mockito.mock(SCM.class);
        Mockito.when(scmConfig.doesPluginExist()).thenReturn(true);
        PluggableSCMMaterialConfig pluggableSCMMaterialConfig = new PluggableSCMMaterialConfig(null, scmConfig, "usr/home", null);
        pluggableSCMMaterialConfig.setScmId("scm-id");
        pluggableSCMMaterialConfig.validateTree(validationContext);
        Assert.assertThat(pluggableSCMMaterialConfig.errors().getAll().size(), Matchers.is(1));
        Assert.assertThat(pluggableSCMMaterialConfig.errors().on(SCM_ID), Matchers.is("Could not find SCM for given scm-id: [scm-id]."));
    }

    @Test
    public void shouldAddErrorWhenAssociatedSCMPluginIsMissing() throws Exception {
        PipelineConfigSaveValidationContext configSaveValidationContext = Mockito.mock(PipelineConfigSaveValidationContext.class);
        Mockito.when(configSaveValidationContext.findScmById(ArgumentMatchers.anyString())).thenReturn(Mockito.mock(SCM.class));
        SCM scmConfig = Mockito.mock(SCM.class);
        Mockito.when(scmConfig.doesPluginExist()).thenReturn(false);
        PluggableSCMMaterialConfig pluggableSCMMaterialConfig = new PluggableSCMMaterialConfig(null, scmConfig, "usr/home", null);
        pluggableSCMMaterialConfig.setScmId("scm-id");
        pluggableSCMMaterialConfig.validateTree(configSaveValidationContext);
        Assert.assertThat(pluggableSCMMaterialConfig.errors().getAll().size(), Matchers.is(1));
        Assert.assertThat(pluggableSCMMaterialConfig.errors().on(SCM_ID), Matchers.is("Could not find plugin for scm-id: [scm-id]."));
    }

    @Test
    public void shouldSetConfigAttributesForSCMMaterial() throws Exception {
        Map<String, String> attributes = new HashMap<>();
        attributes.put(SCM_ID, "scm-id");
        attributes.put(FOLDER, "dest");
        attributes.put(FILTER, "/foo/**.*,/another/**.*,bar");
        PluggableSCMMaterialConfig pluggableSCMMaterialConfig = new PluggableSCMMaterialConfig();
        pluggableSCMMaterialConfig.setConfigAttributes(attributes);
        Assert.assertThat(pluggableSCMMaterialConfig.getScmId(), Matchers.is("scm-id"));
        Assert.assertThat(pluggableSCMMaterialConfig.getFolder(), Matchers.is("dest"));
        Assert.assertThat(pluggableSCMMaterialConfig.filter(), Matchers.is(new Filter(new IgnoredFiles("/foo/**.*"), new IgnoredFiles("/another/**.*"), new IgnoredFiles("bar"))));
    }

    @Test
    public void shouldSetConfigAttributesForSCMMaterialWhenDataIsEmpty() throws Exception {
        Map<String, String> attributes = new HashMap<>();
        attributes.put(SCM_ID, "scm-id");
        attributes.put(FOLDER, "");
        attributes.put(FILTER, "");
        PluggableSCMMaterialConfig pluggableSCMMaterialConfig = new PluggableSCMMaterialConfig();
        pluggableSCMMaterialConfig.setFolder("dest");
        pluggableSCMMaterialConfig.setFilter(new Filter(new IgnoredFiles("/foo/**.*")));
        pluggableSCMMaterialConfig.setConfigAttributes(attributes);
        Assert.assertThat(pluggableSCMMaterialConfig.getScmId(), Matchers.is("scm-id"));
        Assert.assertThat(pluggableSCMMaterialConfig.getFolder(), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(pluggableSCMMaterialConfig.filter(), Matchers.is(new Filter()));
    }

    @Test
    public void shouldGetFilterAsString() {
        PluggableSCMMaterialConfig pluggableSCMMaterialConfig = new PluggableSCMMaterialConfig();
        pluggableSCMMaterialConfig.setFilter(new Filter(new IgnoredFiles("/foo/**.*"), new IgnoredFiles("/another/**.*"), new IgnoredFiles("bar")));
        Assert.assertThat(pluggableSCMMaterialConfig.getFilterAsString(), Matchers.is("/foo/**.*,/another/**.*,bar"));
        pluggableSCMMaterialConfig.setFilter(new Filter());
        Assert.assertThat(pluggableSCMMaterialConfig.getFilterAsString(), Matchers.is(""));
    }

    @Test
    public void shouldSetSCMIdToNullIfConfigAttributesForSCMMaterialDoesNotContainSCMId() throws Exception {
        Map<String, String> attributes = new HashMap<>();
        pluggableSCMMaterialConfig.setConfigAttributes(attributes);
        Assert.assertThat(pluggableSCMMaterialConfig.getScmId(), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void shouldSetSCMIdAsNullIfSCMConfigIsNull() {
        pluggableSCMMaterialConfig.setSCMConfig(null);
        Assert.assertThat(pluggableSCMMaterialConfig.getScmId(), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(pluggableSCMMaterialConfig.getSCMConfig(), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void shouldGetNameFromSCMName() {
        PluggableSCMMaterialConfig pluggableSCMMaterialConfig = new PluggableSCMMaterialConfig();
        SCM scmConfig = SCMMother.create("scm-id", "scm-name", "plugin-id", "1.0", new com.thoughtworks.go.domain.config.Configuration(ConfigurationPropertyMother.create("k1", false, "v1")));
        pluggableSCMMaterialConfig.setSCMConfig(scmConfig);
        Assert.assertThat(pluggableSCMMaterialConfig.getName().toString(), Matchers.is("scm-name"));
        pluggableSCMMaterialConfig.setSCMConfig(null);
        Assert.assertThat(pluggableSCMMaterialConfig.getName(), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void shouldCheckEquals() throws Exception {
        SCM scmConfig = SCMMother.create("scm-id", "scm-name", "plugin-id", "1.0", new com.thoughtworks.go.domain.config.Configuration(ConfigurationPropertyMother.create("k1", false, "v1")));
        // same fingerprint
        PluggableSCMMaterialConfig p1 = new PluggableSCMMaterialConfig();
        p1.setSCMConfig(scmConfig);
        PluggableSCMMaterialConfig p2 = new PluggableSCMMaterialConfig();
        p2.setSCMConfig(scmConfig);
        Assert.assertThat(p1.equals(p2), Matchers.is(true));
        // folder
        p2.setFolder("dest");
        Assert.assertThat(p1.equals(p2), Matchers.is(false));
        // scmConfig null
        p1 = new PluggableSCMMaterialConfig();
        p2 = new PluggableSCMMaterialConfig();
        Assert.assertThat(p1.equals(p2), Matchers.is(true));
        p2.setSCMConfig(scmConfig);
        Assert.assertThat(p1.equals(p2), Matchers.is(false));
        p1.setSCMConfig(scmConfig);
        p2 = new PluggableSCMMaterialConfig();
        Assert.assertThat(p1.equals(p2), Matchers.is(false));
        p2.setSCMConfig(scmConfig);
        Assert.assertThat(p1.equals(p2), Matchers.is(true));
        // null comparison
        Assert.assertThat(p1.equals(null), Matchers.is(false));
    }

    @Test
    public void shouldDelegateToSCMConfigForAutoUpdate() throws Exception {
        SCM scm = Mockito.mock(SCM.class);
        Mockito.when(scm.isAutoUpdate()).thenReturn(false);
        PluggableSCMMaterialConfig pluggableSCMMaterialConfig = new PluggableSCMMaterialConfig(new CaseInsensitiveString("scm-name"), scm, null, null);
        Assert.assertThat(pluggableSCMMaterialConfig.isAutoUpdate(), Matchers.is(false));
        Mockito.verify(scm).isAutoUpdate();
    }

    @Test
    public void shouldCorrectlyGet_Name_DisplayName_Description_LongDescription_UriForDisplay() {
        SCM scmConfig = Mockito.mock(SCM.class);
        Mockito.when(scmConfig.getName()).thenReturn("scm-name");
        Mockito.when(scmConfig.getConfigForDisplay()).thenReturn("k1:v1");
        PluggableSCMMaterialConfig pluggableSCMMaterialConfig = new PluggableSCMMaterialConfig(null, scmConfig, null, null);
        Assert.assertThat(pluggableSCMMaterialConfig.getName(), Matchers.is(new CaseInsensitiveString("scm-name")));
        Assert.assertThat(pluggableSCMMaterialConfig.getDisplayName(), Matchers.is("scm-name"));
        Assert.assertThat(pluggableSCMMaterialConfig.getLongDescription(), Matchers.is("k1:v1"));
        Assert.assertThat(pluggableSCMMaterialConfig.getUriForDisplay(), Matchers.is("k1:v1"));
        Mockito.when(scmConfig.getName()).thenReturn(null);
        pluggableSCMMaterialConfig = new PluggableSCMMaterialConfig(null, scmConfig, null, null);
        Assert.assertThat(pluggableSCMMaterialConfig.getName(), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(pluggableSCMMaterialConfig.getDisplayName(), Matchers.is("k1:v1"));
    }

    @Test
    public void shouldCorrectlyGetTypeDisplay() {
        Assert.assertThat(pluggableSCMMaterialConfig.getTypeForDisplay(), Matchers.is("SCM"));
        pluggableSCMMaterialConfig.setSCMConfig(SCMMother.create("scm-id"));
        Assert.assertThat(pluggableSCMMaterialConfig.getTypeForDisplay(), Matchers.is("SCM"));
        SCMMetadataStore.getInstance().addMetadataFor("plugin", null, null);
        Assert.assertThat(pluggableSCMMaterialConfig.getTypeForDisplay(), Matchers.is("SCM"));
        SCMView scmView = Mockito.mock(SCMView.class);
        Mockito.when(scmView.displayValue()).thenReturn("scm-name");
        SCMMetadataStore.getInstance().addMetadataFor("plugin", null, scmView);
        Assert.assertThat(pluggableSCMMaterialConfig.getTypeForDisplay(), Matchers.is("scm-name"));
    }

    @Test
    public void shouldFailValidationIfDestinationDirectoryIsNested() {
        pluggableSCMMaterialConfig.setFolder("f1");
        pluggableSCMMaterialConfig.validateNotSubdirectoryOf("f1/f2");
        Assert.assertFalse(pluggableSCMMaterialConfig.errors().isEmpty());
        Assert.assertThat(pluggableSCMMaterialConfig.errors().on(ScmMaterialConfig.FOLDER), Matchers.is("Invalid Destination Directory. Every material needs a different destination directory and the directories should not be nested."));
    }

    @Test
    public void shouldNotFailValidationIfDestinationDirectoryIsMultilevelButNotNested() {
        pluggableSCMMaterialConfig.setFolder("f1/f2/f3");
        pluggableSCMMaterialConfig.validateNotSubdirectoryOf("f1/f2/f");
        Assert.assertNull(pluggableSCMMaterialConfig.errors().getAllOn(ScmMaterialConfig.FOLDER));
    }

    @Test
    public void shouldFailValidationIfDestinationDirectoryIsOutsideCurrentWorkingDirectoryAfterNormalization() {
        pluggableSCMMaterialConfig.setFolder("f1/../../f3");
        pluggableSCMMaterialConfig.validateConcreteMaterial(null);
        Assert.assertThat(pluggableSCMMaterialConfig.errors().on(ScmMaterialConfig.FOLDER), Matchers.is("Dest folder 'f1/../../f3' is not valid. It must be a sub-directory of the working folder."));
    }

    @Test
    public void shouldFailValidationIfDestinationDirectoryIsNestedAfterNormalization() {
        pluggableSCMMaterialConfig.setFolder("f1/f2/../../f3");
        pluggableSCMMaterialConfig.validateNotSubdirectoryOf("f3/f4");
        Assert.assertThat(pluggableSCMMaterialConfig.errors().on(ScmMaterialConfig.FOLDER), Matchers.is("Invalid Destination Directory. Every material needs a different destination directory and the directories should not be nested."));
    }

    @Test
    public void shouldNotValidateNestingOfMaterialDirectoriesBasedOnServerSideFileSystem() throws IOException {
        final File workingDir = temporaryFolder.newFolder("go-working-dir");
        final File material1 = new File(workingDir, "material1");
        material1.mkdirs();
        final Path material2 = Files.createSymbolicLink(Paths.get(new File(workingDir, "material2").getPath()), Paths.get(material1.getPath()));
        pluggableSCMMaterialConfig.setFolder(material1.getAbsolutePath());
        pluggableSCMMaterialConfig.validateNotSubdirectoryOf(material2.toAbsolutePath().toString());
        Assert.assertNull(pluggableSCMMaterialConfig.errors().getAllOn(ScmMaterialConfig.FOLDER));
    }
}

