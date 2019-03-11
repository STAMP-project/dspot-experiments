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


import AbstractMaterialConfig.MATERIAL_TYPE;
import DependencyMaterialConfig.ORIGIN;
import DependencyMaterialConfig.PIPELINE_STAGE_NAME;
import GitMaterialConfig.BRANCH;
import P4MaterialConfig.SERVER_AND_PORT;
import P4MaterialConfig.VIEW;
import PackageMaterialConfig.PACKAGE_ID;
import PluggableSCMMaterialConfig.SCM_ID;
import ScmMaterialConfig.AUTO_UPDATE;
import ScmMaterialConfig.FOLDER;
import SvnMaterialConfig.CHECK_EXTERNALS;
import SvnMaterialConfig.PASSWORD;
import SvnMaterialConfig.TYPE;
import SvnMaterialConfig.URL;
import SvnMaterialConfig.USERNAME;
import TfsMaterialConfig.DOMAIN;
import TfsMaterialConfig.MATERIAL_NAME;
import TfsMaterialConfig.PROJECT_PATH;
import com.thoughtworks.go.config.materials.dependency.DependencyMaterialConfig;
import com.thoughtworks.go.config.materials.git.GitMaterialConfig;
import com.thoughtworks.go.config.materials.mercurial.HgMaterialConfig;
import com.thoughtworks.go.config.materials.perforce.P4MaterialConfig;
import com.thoughtworks.go.config.materials.svn.SvnMaterialConfig;
import com.thoughtworks.go.config.materials.tfs.TfsMaterialConfig;
import com.thoughtworks.go.config.remote.FileConfigOrigin;
import com.thoughtworks.go.config.remote.RepoConfigOrigin;
import com.thoughtworks.go.domain.ConfigErrors;
import com.thoughtworks.go.domain.materials.MaterialConfig;
import com.thoughtworks.go.domain.packagerepository.PackageDefinitionMother;
import com.thoughtworks.go.domain.scm.SCMMother;
import com.thoughtworks.go.helper.GoConfigMother;
import com.thoughtworks.go.helper.MaterialConfigsMother;
import com.thoughtworks.go.security.GoCipher;
import com.thoughtworks.go.util.TestUtils;
import com.thoughtworks.go.util.command.UrlArgument;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class MaterialConfigsTest {
    private GoConfigMother goConfigMother;

    @Test
    public void shouldNotAllowMoreThanOneDependencyWithSameName() throws Exception {
        CruiseConfig config = GoConfigMother.configWithPipelines("pipeline1", "pipeline2", "pipeline3", "go");
        DependencyMaterialConfig one = new DependencyMaterialConfig(new CaseInsensitiveString("sameName"), new CaseInsensitiveString("pipeline2"), new CaseInsensitiveString("stage"));
        DependencyMaterialConfig another = new DependencyMaterialConfig(new CaseInsensitiveString("sameName"), new CaseInsensitiveString("pipeline3"), new CaseInsensitiveString("stage"));
        MaterialConfigs materialConfigs = new MaterialConfigs(one, another);
        ValidationContext validationContext = ConfigSaveValidationContext.forChain(config);
        materialConfigs.validate(validationContext);
        Assert.assertThat(one.errors().isEmpty(), Matchers.is(false));
        Assert.assertThat(one.errors().on("materialName"), Matchers.containsString("You have defined multiple materials called 'sameName'. Material names are case-insensitive and must be unique."));
        Assert.assertThat(another.errors().isEmpty(), Matchers.is(false));
        Assert.assertThat(another.errors().on("materialName"), Matchers.containsString("You have defined multiple materials called 'sameName'. Material names are case-insensitive and must be unique."));
    }

    /* Name
    Pipeline X    - Material1           - pipeline1
    - Material2           - someSvn
    - DepMaterial1        -  ""       dependant on a pipeline named "pipeline1"
    Above scenario allowed
     */
    @Test
    public void shouldNotAllowAnEmptyDepMaterialWhenOtherMaterialsUseThatPipelineName() throws Exception {
        CruiseConfig config = GoConfigMother.configWithPipelines("pipeline1", "pipeline2", "pipeline3", "go");
        SvnMaterialConfig one = new SvnMaterialConfig("svn://abc", "", "", false);
        one.setName(new CaseInsensitiveString("pipeline2"));
        DependencyMaterialConfig invalidOne = new DependencyMaterialConfig(new CaseInsensitiveString("pipeline2"), new CaseInsensitiveString("stage"));
        MaterialConfigs materials = new MaterialConfigs(one, invalidOne);
        ValidationContext validationContext = ConfigSaveValidationContext.forChain(config);
        materials.validate(validationContext);
        Assert.assertThat(invalidOne.errors().isEmpty(), Matchers.is(false));
        Assert.assertThat(invalidOne.errors().on("materialName"), Matchers.is(("You have defined multiple materials called 'pipeline2'." + (" Material names are case-insensitive and must be unique. Note that for dependency materials the default materialName is the name of the upstream pipeline. " + "You can override this by setting the materialName explicitly for the upstream pipeline."))));
    }

    @Test
    public void shouldReturnValidWhenThereIsNoCycle() throws Exception {
        CruiseConfig cruiseConfig = new BasicCruiseConfig();
        PipelineConfig pipeline1 = goConfigMother.addPipeline(cruiseConfig, "pipeline1", "stage", "build");
        PipelineConfig pipeline2 = goConfigMother.addPipeline(cruiseConfig, "pipeline2", "stage", "build");
        goConfigMother.setDependencyOn(cruiseConfig, pipeline2, "pipeline1", "stage");
        pipeline1.materialConfigs().validate(ConfigSaveValidationContext.forChain(cruiseConfig));
        Assert.assertThat(pipeline1.materialConfigs().errors().isEmpty(), Matchers.is(true));
        pipeline2.materialConfigs().validate(ConfigSaveValidationContext.forChain(cruiseConfig));
        Assert.assertThat(pipeline2.materialConfigs().errors().isEmpty(), Matchers.is(true));
    }

    @Test
    public void shouldNotAllowToDependOnPipelineDefinedInConfigRepository_WhenDownstreamInFile() throws Exception {
        CruiseConfig cruiseConfig = new BasicCruiseConfig();
        PipelineConfig pipeline1 = goConfigMother.addPipeline(cruiseConfig, "pipeline1", "stage", "build");
        PipelineConfig pipeline2 = goConfigMother.addPipeline(cruiseConfig, "pipeline2", "stage", "build");
        goConfigMother.setDependencyOn(cruiseConfig, pipeline2, "pipeline1", "stage");
        pipeline1.setOrigin(new RepoConfigOrigin());
        pipeline2.setOrigin(new FileConfigOrigin());
        pipeline1.materialConfigs().validate(ConfigSaveValidationContext.forChain(cruiseConfig, new BasicPipelineConfigs(), pipeline1));
        Assert.assertThat(pipeline1.materialConfigs().errors().isEmpty(), Matchers.is(true));
        pipeline2.materialConfigs().validate(ConfigSaveValidationContext.forChain(cruiseConfig, new BasicPipelineConfigs(), pipeline2));
        DependencyMaterialConfig invalidDependency = pipeline2.materialConfigs().findDependencyMaterial(new CaseInsensitiveString("pipeline1"));
        Assert.assertThat(invalidDependency.errors().isEmpty(), Matchers.is(false));
        Assert.assertThat(invalidDependency.errors().on(ORIGIN), Matchers.startsWith("Dependency from pipeline defined in"));
    }

    @Test
    public void shouldAllowToDependOnPipelineDefinedInConfigRepository_WhenInConfigRepository() throws Exception {
        CruiseConfig cruiseConfig = new BasicCruiseConfig();
        PipelineConfig pipeline1 = goConfigMother.addPipeline(cruiseConfig, "pipeline1", "stage", "build");
        PipelineConfig pipeline2 = goConfigMother.addPipeline(cruiseConfig, "pipeline2", "stage", "build");
        goConfigMother.setDependencyOn(cruiseConfig, pipeline2, "pipeline1", "stage");
        pipeline1.setOrigin(new RepoConfigOrigin(new com.thoughtworks.go.config.remote.ConfigRepoConfig(new SvnMaterialConfig("http://mysvn", false), "myplugin"), "123"));
        pipeline2.setOrigin(new RepoConfigOrigin(new com.thoughtworks.go.config.remote.ConfigRepoConfig(new SvnMaterialConfig("http://othersvn", false), "myplugin"), "2222"));
        pipeline1.materialConfigs().validate(ConfigSaveValidationContext.forChain(cruiseConfig, new BasicPipelineConfigs(), pipeline1));
        Assert.assertThat(pipeline1.materialConfigs().errors().isEmpty(), Matchers.is(true));
        pipeline2.materialConfigs().validate(ConfigSaveValidationContext.forChain(cruiseConfig, new BasicPipelineConfigs(), pipeline2));
        DependencyMaterialConfig dep = pipeline2.materialConfigs().findDependencyMaterial(new CaseInsensitiveString("pipeline1"));
        Assert.assertThat(dep.errors().isEmpty(), Matchers.is(true));
    }

    @Test
    public void shouldAllowToDependOnPipelineDefinedInFile_WhenInFile() throws Exception {
        CruiseConfig cruiseConfig = new BasicCruiseConfig();
        PipelineConfig pipeline1 = goConfigMother.addPipeline(cruiseConfig, "pipeline1", "stage", "build");
        PipelineConfig pipeline2 = goConfigMother.addPipeline(cruiseConfig, "pipeline2", "stage", "build");
        goConfigMother.setDependencyOn(cruiseConfig, pipeline2, "pipeline1", "stage");
        pipeline1.setOrigin(new FileConfigOrigin());
        pipeline2.setOrigin(new FileConfigOrigin());
        pipeline1.materialConfigs().validate(ConfigSaveValidationContext.forChain(cruiseConfig, new BasicPipelineConfigs(), pipeline1));
        Assert.assertThat(pipeline1.materialConfigs().errors().isEmpty(), Matchers.is(true));
        pipeline2.materialConfigs().validate(ConfigSaveValidationContext.forChain(cruiseConfig, new BasicPipelineConfigs(), pipeline2));
        DependencyMaterialConfig dep = pipeline2.materialConfigs().findDependencyMaterial(new CaseInsensitiveString("pipeline1"));
        Assert.assertThat(dep.errors().isEmpty(), Matchers.is(true));
    }

    @Test
    public void shouldNotAllowMultipleDependenciesForTheSamePipelines() throws Exception {
        CruiseConfig config = GoConfigMother.configWithPipelines("pipeline1", "pipeline2", "pipeline3", "go");
        DependencyMaterialConfig dependencyMaterial = new DependencyMaterialConfig(new CaseInsensitiveString("pipeline2"), new CaseInsensitiveString("stage"));
        DependencyMaterialConfig duplicateDependencyMaterial = new DependencyMaterialConfig(new CaseInsensitiveString("pipeline2"), new CaseInsensitiveString("stage"));
        MaterialConfigs materialConfigs = new MaterialConfigs(dependencyMaterial, duplicateDependencyMaterial);
        ValidationContext validationContext = ConfigSaveValidationContext.forChain(config);
        materialConfigs.validate(validationContext);
        ConfigErrors errors = duplicateDependencyMaterial.errors();
        Assert.assertThat(errors.isEmpty(), Matchers.is(false));
        Assert.assertThat(errors.on("pipelineStageName"), Matchers.is("A pipeline can depend on each upstream pipeline only once. Remove one of the occurrences of 'pipeline2' from the current pipeline dependencies."));
    }

    @Test
    public void shouldIgnorePipelineWithEmptyNameInUniquenessCheck() throws Exception {
        CruiseConfig config = GoConfigMother.configWithPipelines("pipeline1", "pipeline2", "pipeline3", "go");
        DependencyMaterialConfig one = new DependencyMaterialConfig(new CaseInsensitiveString(""), new CaseInsensitiveString("pipeline2"), new CaseInsensitiveString("stage"));
        DependencyMaterialConfig another = new DependencyMaterialConfig(new CaseInsensitiveString(""), new CaseInsensitiveString("pipeline3"), new CaseInsensitiveString("stage"));
        MaterialConfigs materials = new MaterialConfigs(one, another);
        ValidationContext validationContext = ConfigSaveValidationContext.forChain(config);
        materials.validate(validationContext);
        Assert.assertThat(one.errors().isEmpty(), Matchers.is(true));
        Assert.assertThat(another.errors().isEmpty(), Matchers.is(true));
    }

    @Test
    public void shouldReturnTrueWhenDependencyPipelineDoesNotExist() throws Exception {
        CruiseConfig cruiseConfig = new BasicCruiseConfig();
        PipelineConfig pipelineConfig = goConfigMother.addPipeline(cruiseConfig, "pipeline1", "stage", "build");
        goConfigMother.setDependencyOn(cruiseConfig, pipelineConfig, "pipeline2", "stage");
        pipelineConfig.materialConfigs().validate(ConfigSaveValidationContext.forChain(cruiseConfig, new BasicPipelineConfigs(), pipelineConfig));
        Assert.assertThat(pipelineConfig.materialConfigs().errors().isEmpty(), Matchers.is(true));
    }

    @Test
    public void shouldFailIfAllScmMaterialsInAPipelineHaveSameFolders() throws IOException {
        HgMaterialConfig materialOne = new HgMaterialConfig("http://url1", null);
        materialOne.setConfigAttributes(Collections.singletonMap(FOLDER, "folder1"));
        HgMaterialConfig materialTwo = new HgMaterialConfig("http://url2", null);
        materialTwo.setConfigAttributes(Collections.singletonMap(FOLDER, "folder1"));
        PluggableSCMMaterialConfig materialThree = new PluggableSCMMaterialConfig(null, SCMMother.create("scm-id"), "folder1", null);
        CruiseConfig config = GoConfigMother.configWithPipelines("one");
        PipelineConfig pipelineOne = config.pipelineConfigByName(new CaseInsensitiveString("one"));
        pipelineOne.setMaterialConfigs(new MaterialConfigs(materialOne, materialTwo, materialThree));
        pipelineOne.materialConfigs().validate(ConfigSaveValidationContext.forChain(config));
        String conflictingDirMessage = "Invalid Destination Directory. Every material needs a different destination directory and the directories should not be nested.";
        Assert.assertThat(pipelineOne.materialConfigs().get(0).errors().on(FOLDER), Matchers.is(conflictingDirMessage));
        Assert.assertThat(pipelineOne.materialConfigs().get(1).errors().on(FOLDER), Matchers.is(conflictingDirMessage));
        Assert.assertThat(pipelineOne.materialConfigs().get(2).errors().on(PluggableSCMMaterialConfig.FOLDER), Matchers.is(conflictingDirMessage));
    }

    @Test
    public void shouldNotFailIfAllScmMaterialsInAPipelineHaveDifferentFolders() {
        HgMaterialConfig materialOne = new HgMaterialConfig("http://url1", null);
        materialOne.setConfigAttributes(Collections.singletonMap(FOLDER, "folder1"));
        HgMaterialConfig materialTwo = new HgMaterialConfig("http://url2", null);
        materialTwo.setConfigAttributes(Collections.singletonMap(FOLDER, "folder2"));
        CruiseConfig config = GoConfigMother.configWithPipelines("one");
        PipelineConfig pipelineOne = config.pipelineConfigByName(new CaseInsensitiveString("one"));
        pipelineOne.setMaterialConfigs(new MaterialConfigs(materialOne, materialTwo));
        pipelineOne.materialConfigs().validate(ConfigSaveValidationContext.forChain(config));
        Assert.assertThat(pipelineOne.materialConfigs().get(0).errors().isEmpty(), Matchers.is(true));
        Assert.assertThat(pipelineOne.materialConfigs().get(1).errors().isEmpty(), Matchers.is(true));
    }

    @Test
    public void shouldReturnNullWhenMaterialNotFoundForTheGivenFingerPrint() {
        CruiseConfig cruiseConfig = new BasicCruiseConfig();
        PipelineConfig pipeline = goConfigMother.addPipeline(cruiseConfig, "pipeline1", "stage", "build");
        Assert.assertThat(pipeline.materialConfigs().getByFingerPrint("invalid"), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void shouldFailIfMultipleMaterialsDoNotHaveDestinationFolderSet() {
        HgMaterialConfig materialConfigOne = new HgMaterialConfig("http://url1", null);
        materialConfigOne.setConfigAttributes(Collections.singletonMap(FOLDER, "folder"));
        HgMaterialConfig materialConfigTwo = new HgMaterialConfig("http://url2", null);
        PluggableSCMMaterialConfig materialConfigThree = new PluggableSCMMaterialConfig(null, SCMMother.create("scm-id"), null, null);
        CruiseConfig config = GoConfigMother.configWithPipelines("one");
        PipelineConfig pipelineOne = config.pipelineConfigByName(new CaseInsensitiveString("one"));
        pipelineOne.setMaterialConfigs(new MaterialConfigs(materialConfigOne, materialConfigTwo, materialConfigThree));
        pipelineOne.materialConfigs().validate(ConfigSaveValidationContext.forChain(config));
        Assert.assertThat(pipelineOne.materialConfigs().get(0).errors().isEmpty(), Matchers.is(true));
        Assert.assertThat(pipelineOne.materialConfigs().get(1).errors().on(FOLDER), Matchers.is("Destination directory is required when specifying multiple scm materials"));
        Assert.assertThat(pipelineOne.materialConfigs().get(2).errors().on(PluggableSCMMaterialConfig.FOLDER), Matchers.is("Destination directory is required when specifying multiple scm materials"));
    }

    @Test
    public void shouldAddErrorWhenMatchingScmConfigDoesNotExist() {
        PluggableSCMMaterialConfig scmMaterialConfig = new PluggableSCMMaterialConfig(null, SCMMother.create("scm-id"), null, null);
        PackageMaterialConfig packageMaterialConfig = new PackageMaterialConfig(new CaseInsensitiveString("package-name"), "package-id", PackageDefinitionMother.create("package-id"));
        CruiseConfig config = GoConfigMother.configWithPipelines("one");
        PipelineConfig pipelineConfig = config.pipelineConfigByName(new CaseInsensitiveString("one"));
        MaterialConfigs materialConfigs = new MaterialConfigs(scmMaterialConfig, packageMaterialConfig);
        pipelineConfig.setMaterialConfigs(materialConfigs);
        materialConfigs.validateTree(PipelineConfigSaveValidationContext.forChain(true, "group", config));
        Assert.assertThat(pipelineConfig.materialConfigs().get(0).errors().on(scmMaterialConfig.SCM_ID), Matchers.is("Could not find SCM for given scm-id: [scm-id]."));
    }

    @Test
    public void shouldAddErrorWhenMatchingPackageIDDoesNotExist() {
        PackageMaterialConfig packageMaterialConfig = new PackageMaterialConfig(new CaseInsensitiveString("package-name"), "package-id", PackageDefinitionMother.create("package-id"));
        CruiseConfig config = GoConfigMother.configWithPipelines("one");
        PipelineConfig pipelineConfig = config.pipelineConfigByName(new CaseInsensitiveString("one"));
        MaterialConfigs materialConfigs = new MaterialConfigs(packageMaterialConfig);
        pipelineConfig.setMaterialConfigs(materialConfigs);
        materialConfigs.validateTree(PipelineConfigSaveValidationContext.forChain(true, "group", config));
        Assert.assertThat(pipelineConfig.materialConfigs().get(0).errors().on(packageMaterialConfig.PACKAGE_ID), Matchers.is("Could not find repository for given package id:[package-id]"));
    }

    @Test
    public void shouldNotFailIfMultipleMaterialsHaveUniqueDestinationFolderSet() {
        HgMaterialConfig materialOne = new HgMaterialConfig("http://url1", null);
        materialOne.setConfigAttributes(Collections.singletonMap(FOLDER, "folder"));
        HgMaterialConfig materialTwo = new HgMaterialConfig("http://url2", null);
        materialTwo.setConfigAttributes(Collections.singletonMap(FOLDER, "folder2"));
        CruiseConfig config = GoConfigMother.configWithPipelines("one");
        PipelineConfig pipelineOne = config.pipelineConfigByName(new CaseInsensitiveString("one"));
        pipelineOne.setMaterialConfigs(new MaterialConfigs(materialOne, materialTwo));
        pipelineOne.materialConfigs().validate(ConfigSaveValidationContext.forChain(config));
        Assert.assertThat(pipelineOne.materialConfigs().get(0).errors().isEmpty(), Matchers.is(true));
        Assert.assertThat(pipelineOne.materialConfigs().get(1).errors().isEmpty(), Matchers.is(true));
    }

    @Test
    public void shouldCheckSCMMaterialsHaveDestinationCorrectly() {
        HgMaterialConfig materialConfigOne = new HgMaterialConfig("http://url1", null);
        materialConfigOne.setConfigAttributes(Collections.singletonMap(FOLDER, "folder"));
        CruiseConfig config = GoConfigMother.configWithPipelines("one");
        PipelineConfig pipelineOne = config.pipelineConfigByName(new CaseInsensitiveString("one"));
        pipelineOne.setMaterialConfigs(new MaterialConfigs(materialConfigOne));
        Assert.assertThat(pipelineOne.materialConfigs().scmMaterialsHaveDestination(), Matchers.is(true));
        PluggableSCMMaterialConfig materialConfigTwo = new PluggableSCMMaterialConfig(null, SCMMother.create("scm-id"), null, null);
        pipelineOne.materialConfigs().add(materialConfigTwo);
        Assert.assertThat(pipelineOne.materialConfigs().scmMaterialsHaveDestination(), Matchers.is(false));
    }

    @Test
    public void shouldShowAutoUpdateMismatchErrorTwiceWhenMaterialIsAddedToSamePipeline() throws Exception {
        HgMaterialConfig materialOne = new HgMaterialConfig("http://url1", null);
        materialOne.setConfigAttributes(Collections.singletonMap(FOLDER, "some-folder"));
        materialOne.setAutoUpdate(true);
        HgMaterialConfig materialTwo = new HgMaterialConfig("http://url1", null);
        materialTwo.setConfigAttributes(Collections.singletonMap(FOLDER, "some-folder-2"));
        materialTwo.setAutoUpdate(false);
        CruiseConfig config = GoConfigMother.configWithPipelines("one");
        PipelineConfig pipelineOne = config.pipelineConfigByName(new CaseInsensitiveString("one"));
        pipelineOne.setMaterialConfigs(new MaterialConfigs(materialOne, materialTwo));
        pipelineOne.materialConfigs().validate(ConfigSaveValidationContext.forChain(config));
        Assert.assertThat(pipelineOne.materialConfigs().get(0).errors().getAll().size(), Matchers.is(1));
        Assert.assertThat(pipelineOne.materialConfigs().get(1).errors().getAll().size(), Matchers.is(1));
    }

    @Test
    public void shouldNotThrowUpOnMaterialIfAutoUpdateValuesAreCorrect() throws Exception {
        HgMaterialConfig materialOne = new HgMaterialConfig("http://url1", null);
        materialOne.setAutoUpdate(true);
        HgMaterialConfig materialTwo = new HgMaterialConfig("http://url1", null);
        materialTwo.setAutoUpdate(true);
        CruiseConfig config = GoConfigMother.configWithPipelines("one", "two", "three");
        PipelineConfig pipelineOne = config.pipelineConfigByName(new CaseInsensitiveString("one"));
        pipelineOne.setMaterialConfigs(new MaterialConfigs(materialOne));
        PipelineConfig pipelineTwo = config.pipelineConfigByName(new CaseInsensitiveString("two"));
        pipelineTwo.setMaterialConfigs(new MaterialConfigs(materialTwo));
        pipelineOne.materialConfigs().validate(ConfigSaveValidationContext.forChain(config));
        Assert.assertThat(pipelineOne.materialConfigs().get(0).errors().isEmpty(), Matchers.is(true));
        Assert.assertThat(pipelineTwo.materialConfigs().get(0).errors().isEmpty(), Matchers.is(true));
    }

    @Test
    public void shouldReturnMaterialBasedOnPiplineUniqueFingerPrint() {
        CruiseConfig cruiseConfig = new BasicCruiseConfig();
        PipelineConfig pipeline1 = goConfigMother.addPipeline(cruiseConfig, "pipeline1", "stage", "build");
        HgMaterialConfig expectedMaterial = MaterialConfigsMother.hgMaterialConfig();
        pipeline1.addMaterialConfig(expectedMaterial);
        pipeline1.addMaterialConfig(MaterialConfigsMother.gitMaterialConfig("url"));
        pipeline1.addMaterialConfig(MaterialConfigsMother.svnMaterialConfig("url", "folder"));
        MaterialConfig actualMaterialConfig = pipeline1.materialConfigs().getByFingerPrint(expectedMaterial.getPipelineUniqueFingerprint());
        Assert.assertThat(actualMaterialConfig, Matchers.is(expectedMaterial));
    }

    @Test
    public void shouldReturnTrueWhenNoDependencyDefined() throws Exception {
        CruiseConfig cruiseConfig = new BasicCruiseConfig();
        PipelineConfig pipelineConfig = goConfigMother.addPipeline(cruiseConfig, "pipeline1", "stage", "build");
        goConfigMother.addPipeline(cruiseConfig, "pipeline2", "stage", "build");
        pipelineConfig.materialConfigs().validate(ConfigSaveValidationContext.forChain(cruiseConfig));
        Assert.assertThat(pipelineConfig.materialConfigs().errors().isEmpty(), Matchers.is(true));
    }

    @Test
    public void shouldAddErrorOnMaterialIfAutoUpdateDoesNotMatchAcrossFingerPrint() throws Exception {
        HgMaterialConfig materialOne = new HgMaterialConfig("http://url1", null);
        materialOne.setAutoUpdate(false);
        HgMaterialConfig materialTwo = new HgMaterialConfig("http://url1", null);
        materialTwo.setAutoUpdate(true);
        CruiseConfig config = GoConfigMother.configWithPipelines("one", "two");
        PipelineConfig pipelineOne = config.pipelineConfigByName(new CaseInsensitiveString("one"));
        pipelineOne.setMaterialConfigs(new MaterialConfigs(materialOne));
        PipelineConfig pipelineTwo = config.pipelineConfigByName(new CaseInsensitiveString("two"));
        pipelineTwo.setMaterialConfigs(new MaterialConfigs(materialTwo));
        pipelineOne.materialConfigs().validate(ConfigSaveValidationContext.forChain(config));
        Assert.assertThat(pipelineOne.materialConfigs().get(0).errors().on(AUTO_UPDATE), Matchers.is("Material of type Mercurial (http://url1) is specified more than once in the configuration with different values for the autoUpdate attribute. All copies of this material must have the same value for this attribute."));
    }

    @Test
    public void shouldAllowModifyingTheAutoUpdateFieldOfMaterials() throws Exception {
        GitMaterialConfig gitMaterial = new GitMaterialConfig("https://url", "master");
        gitMaterial.setAutoUpdate(true);
        GitMaterialConfig modifiedGitMaterial = new GitMaterialConfig("https://url", "master");
        modifiedGitMaterial.setAutoUpdate(false);
        MaterialConfigs configs = new MaterialConfigs();
        configs.add(gitMaterial);
        CruiseConfig config = GoConfigMother.configWithPipelines("one");
        PipelineConfig pipelineOne = config.pipelineConfigByName(new CaseInsensitiveString("one"));
        pipelineOne.setMaterialConfigs(new MaterialConfigs(modifiedGitMaterial));
        configs.validate(ConfigSaveValidationContext.forChain(config));
        Assert.assertTrue(gitMaterial.errors().isEmpty());
    }

    @Test
    public void shouldNotRunMultipleMaterialsValidationIfPipelineContainsOnlyOneMaterial() {
        CruiseConfig config = GoConfigMother.configWithPipelines("one");
        PipelineConfig pipelineOne = config.pipelineConfigByName(new CaseInsensitiveString("one"));
        SvnMaterialConfig svnMaterialConfig = MaterialConfigsMother.svnMaterialConfig();
        svnMaterialConfig.setFolder(null);
        pipelineOne.setMaterialConfigs(new MaterialConfigs(svnMaterialConfig));
        pipelineOne.materialConfigs().validate(ConfigSaveValidationContext.forChain(config));
        Assert.assertThat(svnMaterialConfig.errors().toString(), svnMaterialConfig.errors().getAll().size(), Matchers.is(0));
    }

    @Test
    public void shouldSetSvnConfigAttributesForMaterial() {
        MaterialConfigs materialConfigs = new MaterialConfigs();
        Map<String, Object> svnAttrMap = new HashMap<>();
        svnAttrMap.put(URL, "foo");
        svnAttrMap.put(USERNAME, "bar");
        svnAttrMap.put(PASSWORD, "baz");
        svnAttrMap.put(CHECK_EXTERNALS, false);
        Map<String, Object> attributeMap = new HashMap<>();
        attributeMap.put(MATERIAL_TYPE, TYPE);
        attributeMap.put(TYPE, svnAttrMap);
        materialConfigs.setConfigAttributes(attributeMap);
        Assert.assertThat(materialConfigs.first(), Matchers.is(new SvnMaterialConfig("foo", "bar", "baz", false)));
    }

    @Test
    public void shouldSetTfsConfigAttributesForMaterial() {
        MaterialConfigs materialConfigs = new MaterialConfigs();
        Map<String, String> tfsAttrMap = new HashMap<>();
        tfsAttrMap.put(TfsMaterialConfig.URL, "foo");
        tfsAttrMap.put(TfsMaterialConfig.USERNAME, "bar");
        tfsAttrMap.put(TfsMaterialConfig.PASSWORD, "baz");
        tfsAttrMap.put(PROJECT_PATH, "to_hell");
        tfsAttrMap.put(MATERIAL_NAME, "crapy_material");
        tfsAttrMap.put(DOMAIN, "CORPORATE");
        Map<String, Object> attributeMap = new HashMap<>();
        attributeMap.put(MATERIAL_TYPE, TfsMaterialConfig.TYPE);
        attributeMap.put(TfsMaterialConfig.TYPE, tfsAttrMap);
        materialConfigs.setConfigAttributes(attributeMap);
        TfsMaterialConfig tfsMaterialConfig = new TfsMaterialConfig(new GoCipher(), new UrlArgument("foo"), "bar", "CORPORATE", "baz", "to_hell");
        tfsMaterialConfig.setName(new CaseInsensitiveString("crapy_material"));
        Assert.assertThat(materialConfigs.first(), Matchers.is(tfsMaterialConfig));
        Assert.assertThat(tfsMaterialConfig.getPassword(), Matchers.is("baz"));
    }

    @Test
    public void shouldClearExistingAndSetHgConfigAttributesForMaterial() {
        MaterialConfigs materialConfigs = new MaterialConfigs();
        materialConfigs.add(new HgMaterialConfig("", null));
        materialConfigs.add(new SvnMaterialConfig("", "", "", false));
        Map<String, String> hashMap = new HashMap<>();
        hashMap.put(HgMaterialConfig.URL, "foo");
        Map<String, Object> attributeMap = new HashMap<>();
        attributeMap.put(MATERIAL_TYPE, HgMaterialConfig.TYPE);
        attributeMap.put(HgMaterialConfig.TYPE, hashMap);
        materialConfigs.setConfigAttributes(attributeMap);
        Assert.assertThat(materialConfigs.size(), Matchers.is(1));
        Assert.assertThat(materialConfigs.first(), Matchers.is(new HgMaterialConfig("foo", null)));
    }

    @Test
    public void shouldSetGitConfigAttributesForMaterial() {
        MaterialConfigs materialConfigs = new MaterialConfigs();
        Map<String, String> hashMap = new HashMap<>();
        hashMap.put(GitMaterialConfig.URL, "foo");
        hashMap.put(BRANCH, "master");
        HashMap<String, Object> attributeMap = new HashMap<>();
        attributeMap.put(MATERIAL_TYPE, GitMaterialConfig.TYPE);
        attributeMap.put(GitMaterialConfig.TYPE, hashMap);
        materialConfigs.setConfigAttributes(attributeMap);
        Assert.assertThat(materialConfigs.size(), Matchers.is(1));
        GitMaterialConfig expected = new GitMaterialConfig("foo");
        expected.setConfigAttributes(Collections.singletonMap(BRANCH, "master"));
        Assert.assertThat(materialConfigs.first(), Matchers.is(expected));
    }

    @Test
    public void shouldSetP4ConfigAttributesForMaterial() {
        MaterialConfigs materialConfigs = new MaterialConfigs();
        Map<String, String> hashMap = new HashMap<>();
        hashMap.put(SERVER_AND_PORT, "localhost:1666");
        hashMap.put(P4MaterialConfig.USERNAME, "username");
        hashMap.put(P4MaterialConfig.PASSWORD, "password");
        hashMap.put(VIEW, "foo");
        Map<String, Object> attributeMap = new HashMap<>();
        attributeMap.put(MATERIAL_TYPE, P4MaterialConfig.TYPE);
        attributeMap.put(P4MaterialConfig.TYPE, hashMap);
        materialConfigs.setConfigAttributes(attributeMap);
        Assert.assertThat(materialConfigs.size(), Matchers.is(1));
        P4MaterialConfig expected = new P4MaterialConfig("localhost:1666", "foo", "username");
        expected.setPassword("password");
        Assert.assertThat(materialConfigs.first(), Matchers.is(expected));
    }

    @Test
    public void shouldSetDependencyMaterialConfigAttributesForMaterial() {
        MaterialConfigs materialConfigs = new MaterialConfigs();
        Map<String, String> hashMap = new HashMap<>();
        hashMap.put(PIPELINE_STAGE_NAME, "blah [foo]");
        Map<String, Object> attributeMap = new HashMap<>();
        attributeMap.put(MATERIAL_TYPE, DependencyMaterialConfig.TYPE);
        attributeMap.put(DependencyMaterialConfig.TYPE, hashMap);
        materialConfigs.setConfigAttributes(attributeMap);
        Assert.assertThat(materialConfigs.size(), Matchers.is(1));
        DependencyMaterialConfig expected = new DependencyMaterialConfig(new CaseInsensitiveString("blah"), new CaseInsensitiveString("foo"));
        Assert.assertThat(materialConfigs.first(), Matchers.is(expected));
    }

    @Test
    public void shouldSetPackageMaterialConfigAttributesForMaterial() {
        Map<String, String> hashMap = new HashMap<>();
        String packageId = "some-id";
        hashMap.put(PACKAGE_ID, packageId);
        Map<String, Object> attributeMap = new HashMap<>();
        attributeMap.put(MATERIAL_TYPE, PackageMaterialConfig.TYPE);
        attributeMap.put(PackageMaterialConfig.TYPE, hashMap);
        MaterialConfigs materialConfigs = new MaterialConfigs();
        materialConfigs.setConfigAttributes(attributeMap);
        Assert.assertThat(materialConfigs.size(), Matchers.is(1));
        Assert.assertThat(getPackageId(), Matchers.is(packageId));
    }

    @Test
    public void shouldGetExistingOrDefaultMaterialCorrectly() {
        SvnMaterialConfig svn = new SvnMaterialConfig("http://test.com", false);
        PackageMaterialConfig p1 = new PackageMaterialConfig("p1");
        PackageMaterialConfig p2 = new PackageMaterialConfig("p2");
        Assert.assertThat(getPackageId(), Matchers.is("p2"));
        Assert.assertThat(getPackageId(), Matchers.is("p1"));
    }

    @Test
    public void shouldSetPluggableSCMMaterialConfigAttributesForMaterial() {
        String scmId = "scm-id";
        Map<String, String> hashMap = new HashMap<>();
        hashMap.put(SCM_ID, scmId);
        Map<String, Object> attributeMap = new HashMap<>();
        attributeMap.put(MATERIAL_TYPE, PluggableSCMMaterialConfig.TYPE);
        attributeMap.put(PluggableSCMMaterialConfig.TYPE, hashMap);
        MaterialConfigs materialConfigs = new MaterialConfigs();
        materialConfigs.setConfigAttributes(attributeMap);
        Assert.assertThat(materialConfigs.size(), Matchers.is(1));
        Assert.assertThat(getScmId(), Matchers.is(scmId));
    }

    @Test
    public void shouldGetExistingOrDefaultPluggableSCMMaterialCorrectly() {
        SvnMaterialConfig svn = new SvnMaterialConfig("http://test.com", false);
        PluggableSCMMaterialConfig pluggableSCMMaterialOne = new PluggableSCMMaterialConfig("scm-id-1");
        PluggableSCMMaterialConfig pluggableSCMMaterialTwo = new PluggableSCMMaterialConfig("scm-id-2");
        Assert.assertThat(getScmId(), Matchers.is("scm-id-2"));
        Assert.assertThat(getScmId(), Matchers.is("scm-id-1"));
    }

    @Test
    public void shouldValidateTree() {
        GitMaterialConfig git = new GitMaterialConfig();
        git.setName(new CaseInsensitiveString("mat-name"));
        SvnMaterialConfig svn = new SvnMaterialConfig("url", true);
        svn.setName(new CaseInsensitiveString("mat-name"));
        P4MaterialConfig p4 = new P4MaterialConfig();
        TfsMaterialConfig tfs = new TfsMaterialConfig();
        HgMaterialConfig hg = new HgMaterialConfig();
        MaterialConfigs materialConfigs = new MaterialConfigs(git, svn, p4, tfs, hg);
        PipelineConfig pipelineConfig = new PipelineConfig(new CaseInsensitiveString("p1"), new MaterialConfigs(svn));
        materialConfigs.validateTree(PipelineConfigSaveValidationContext.forChain(true, "group", new BasicCruiseConfig(new BasicPipelineConfigs(pipelineConfig)), pipelineConfig));
        Assert.assertThat(git.errors().on(GitMaterialConfig.MATERIAL_NAME), TestUtils.contains("You have defined multiple materials called 'mat-name'"));
        Assert.assertThat(git.errors().on(GitMaterialConfig.URL), Matchers.is("URL cannot be blank"));
        Assert.assertThat(svn.errors().on(SvnMaterialConfig.MATERIAL_NAME), TestUtils.contains("You have defined multiple materials called 'mat-name'"));
        Assert.assertThat(p4.errors().on(VIEW), TestUtils.contains("P4 view cannot be empty."));
        Assert.assertThat(tfs.errors().on(TfsMaterialConfig.URL), TestUtils.contains("URL cannot be blank"));
        Assert.assertThat(hg.errors().on(HgMaterialConfig.URL), Matchers.is("URL cannot be blank"));
    }

    @Test
    public void shouldFailValidationInNoMaterialPresent() {
        MaterialConfigs materialConfigs = new MaterialConfigs();
        Assert.assertThat(materialConfigs.validateTree(PipelineConfigSaveValidationContext.forChain(true, "group", new PipelineConfig())), Matchers.is(false));
        Assert.assertThat(materialConfigs.errors().firstError(), Matchers.is("A pipeline must have at least one material"));
    }

    @Test
    public void shouldTellWhetherItHasDependencyOnSpecifiedPipeline() throws Exception {
        CruiseConfig cruiseConfig = new BasicCruiseConfig();
        PipelineConfig pipeline1 = goConfigMother.addPipeline(cruiseConfig, "pipeline1", "stage", "build");
        PipelineConfig pipeline2 = goConfigMother.addPipeline(cruiseConfig, "pipeline2", "stage", "build");
        goConfigMother.setDependencyOn(cruiseConfig, pipeline2, "pipeline1", "stage");
        Assert.assertTrue(pipeline2.materialConfigs().hasDependencyMaterial(pipeline1));
        Assert.assertFalse(pipeline1.materialConfigs().hasDependencyMaterial(pipeline2));
    }
}

