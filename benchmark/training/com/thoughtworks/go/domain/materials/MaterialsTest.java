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


import ScmMaterialConfig.FOLDER;
import com.googlecode.junit.ext.JunitExtRunner;
import com.googlecode.junit.ext.RunIf;
import com.thoughtworks.go.config.CaseInsensitiveString;
import com.thoughtworks.go.config.ConfigSaveValidationContext;
import com.thoughtworks.go.config.CruiseConfig;
import com.thoughtworks.go.config.PipelineConfig;
import com.thoughtworks.go.config.materials.dependency.DependencyMaterial;
import com.thoughtworks.go.config.materials.git.GitMaterial;
import com.thoughtworks.go.config.materials.mercurial.HgMaterial;
import com.thoughtworks.go.config.materials.mercurial.HgMaterialConfig;
import com.thoughtworks.go.config.materials.perforce.P4Material;
import com.thoughtworks.go.config.materials.svn.SvnMaterial;
import com.thoughtworks.go.domain.BuildCommand;
import com.thoughtworks.go.helper.GoConfigMother;
import com.thoughtworks.go.helper.MaterialsMother;
import com.thoughtworks.go.junitext.EnhancedOSChecker;
import com.thoughtworks.go.util.command.ConsoleOutputStreamConsumer;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mockito;


@RunWith(JunitExtRunner.class)
public class MaterialsTest {
    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void shouldKnowModificationCheckInterval() {
        final Materials materials = new Materials(42, new ArrayList());
        Assert.assertThat(materials.interval(), Matchers.is(42));
    }

    @Test
    public void shouldGetMaterialByFolder() {
        Materials materials = new Materials();
        HgMaterial material1 = MaterialsMother.hgMaterial();
        material1.setFolder("folder1");
        HgMaterial material2 = MaterialsMother.hgMaterial();
        material2.setFolder("folder2");
        materials.add(material1);
        materials.add(material2);
        Assert.assertThat(materials.byFolder("folder1"), Matchers.is(material1));
    }

    @Test
    public void shouldNotGetDependencyMaterialWhenOneOtherScmMaterialWithNoFolder() {
        Materials materials = new Materials();
        Material material1 = new DependencyMaterial(new CaseInsensitiveString("foo"), new CaseInsensitiveString("bar"));
        Material material2 = new HgMaterial("", null);
        materials.add(material1);
        materials.add(material2);
        Assert.assertThat(materials.byFolder(null), Matchers.is(material2));
    }

    @Test
    public void shouldGetMaterialByFolderWhenHasOnlyOneMaterial() {
        Materials materials = new Materials();
        HgMaterial material1 = MaterialsMother.hgMaterial();
        materials.add(material1);
        Assert.assertThat(materials.byFolder(material1.getFolder()), Matchers.is(material1));
    }

    @Test
    public void shouldNotGetPackageMaterialWhenOneOtherScmMaterialWithNoFolder() {
        Materials materials = new Materials();
        Material material1 = new PackageMaterial("pid");
        Material material2 = new HgMaterial("", null);
        materials.add(material1);
        materials.add(material2);
        Assert.assertThat(materials.byFolder(null), Matchers.is(material2));
    }

    @Test
    public void shouldGetPluggableSCMMaterial_byFolder() {
        Materials materials = new Materials();
        PluggableSCMMaterial material1 = new PluggableSCMMaterial("scm-id");
        material1.setFolder("folder");
        Material material2 = new HgMaterial("", "folder");
        materials.add(material1);
        materials.add(material2);
        Assert.assertThat(materials.byFolder("folder"), Matchers.is(material1));
    }

    @Test
    public void shouldReturnMaterialMatchingTheGivenMaterial() {
        Materials materials = new Materials();
        HgMaterial material1 = MaterialsMother.hgMaterial();
        material1.setFilter(new Filter(new IgnoredFiles("patter")));
        SvnMaterial material2 = MaterialsMother.svnMaterial();
        materials.add(material1);
        materials.add(material2);
        Assert.assertThat(materials.get(MaterialsMother.hgMaterial()), Matchers.is(material1));
        try {
            materials.get(MaterialsMother.p4Material());
            Assert.fail("Must not have found the p4 material");
        } catch (Exception expected) {
        }
    }

    @Test
    public void shouldReturnMaterialBasedOnPiplineUniqueFingerPrint() {
        Materials materials = new Materials();
        HgMaterial expectedMaterial = MaterialsMother.hgMaterial();
        materials.add(expectedMaterial);
        materials.add(MaterialsMother.gitMaterial("url"));
        materials.add(MaterialsMother.svnMaterial("url", "folder"));
        Material actualMaterial = materials.getByFingerPrint(expectedMaterial.getPipelineUniqueFingerprint());
        Assert.assertThat(actualMaterial, Matchers.is(expectedMaterial));
    }

    @Test
    @RunIf(value = EnhancedOSChecker.class, arguments = { DO_NOT_RUN_ON, WINDOWS })
    public void shouldFailIfMultipleMaterialsHaveSameFolderNameSet_CaseInSensitive() {
        HgMaterialConfig materialOne = new HgMaterialConfig("http://url1", null);
        materialOne.setConfigAttributes(Collections.singletonMap(FOLDER, "folder"));
        HgMaterialConfig materialTwo = new HgMaterialConfig("http://url2", null);
        materialTwo.setConfigAttributes(Collections.singletonMap(FOLDER, "foLder"));
        CruiseConfig config = GoConfigMother.configWithPipelines("one");
        PipelineConfig pipelineOne = config.pipelineConfigByName(new CaseInsensitiveString("one"));
        pipelineOne.setMaterialConfigs(new MaterialConfigs(materialOne, materialTwo));
        MaterialConfigs materials = pipelineOne.materialConfigs();
        materials.validate(ConfigSaveValidationContext.forChain(config));
        Assert.assertThat(materials.get(0).errors().isEmpty(), Matchers.is(false));
        Assert.assertThat(materials.get(1).errors().isEmpty(), Matchers.is(false));
        Assert.assertThat(materials.get(0).errors().on(FOLDER), Matchers.is("The destination directory must be unique across materials."));
        Assert.assertThat(materials.get(1).errors().on(FOLDER), Matchers.is("The destination directory must be unique across materials."));
    }

    @Test
    public void shouldReturnTrueIfScmMaterialHasNoDestinationFolderSet() {
        Materials materials = new Materials();
        SvnMaterial material1 = new SvnMaterial("url", "user", "pass", false);
        DependencyMaterial material2 = new DependencyMaterial(new CaseInsensitiveString("pipelineName"), new CaseInsensitiveString("stageName"));
        SvnMaterial material3 = new SvnMaterial("url", "user", "pass", false);
        material3.setFolder("foo");
        materials.add(material1);
        materials.add(material2);
        Assert.assertThat(materials.scmMaterialsHaveDestination(), Matchers.is(false));
    }

    @Test
    public void shouldReturnANewSvnMaterialIfTheMaterialsCollectionDoesNotHaveASvnMaterial() {
        Assert.assertThat(new Materials().getSvnMaterial(), Matchers.is(new SvnMaterial("", "", "", false)));
    }

    @Test
    public void shouldReturnExistingSvnMaterialFromMaterialsIfItContainsOne() {
        Materials materials = new Materials();
        SvnMaterial existingMaterial = new SvnMaterial("foo", "bar", "blah", true);
        materials.add(existingMaterial);
        Assert.assertThat(materials.getSvnMaterial(), Matchers.is(Matchers.sameInstance(existingMaterial)));
    }

    @Test
    public void shouldReturnANewGitMaterialIfTheMaterialsCollectionDoesNotHaveAGitMaterial() {
        Assert.assertThat(new Materials().getGitMaterial(), Matchers.is(new GitMaterial("")));
    }

    @Test
    public void shouldReturnExistingGitMaterialFromMaterialsIfItContainsOne() {
        Materials materials = new Materials();
        GitMaterial existingMaterial = new GitMaterial("foo");
        materials.add(existingMaterial);
        Assert.assertThat(materials.getGitMaterial(), Matchers.is(Matchers.sameInstance(existingMaterial)));
    }

    @Test
    public void shouldReturnAP4SvnMaterialIfTheMaterialsCollectionDoesNotHaveAP4Material() {
        Assert.assertThat(new Materials().getP4Material(), Matchers.is(new P4Material("", "")));
    }

    @Test
    public void shouldReturnExistingP4MaterialFromMaterialsIfItContainsOne() {
        Materials materials = new Materials();
        P4Material existingMaterial = new P4Material("foo", "bar");
        materials.add(existingMaterial);
        Assert.assertThat(materials.getP4Material(), Matchers.is(Matchers.sameInstance(existingMaterial)));
    }

    @Test
    public void shouldReturnANewHgMaterialIfTheMaterialsCollectionDoesNotHaveAHgMaterial() {
        Assert.assertThat(new Materials().getHgMaterial(), Matchers.is(new HgMaterial("", null)));
    }

    @Test
    public void shouldReturnExistingHgMaterialFromMaterialsIfItContainsOne() {
        Materials materials = new Materials();
        HgMaterial existingMaterial = new HgMaterial("foo", null);
        materials.add(existingMaterial);
        Assert.assertThat(materials.getHgMaterial(), Matchers.is(Matchers.sameInstance(existingMaterial)));
    }

    @Test
    public void shouldReturnANewDependencyMaterialIfTheMaterialsCollectionDoesNotHaveAHgMaterial() {
        Assert.assertThat(new Materials().getDependencyMaterial(), Matchers.is(new DependencyMaterial(new CaseInsensitiveString(""), new CaseInsensitiveString(""))));
    }

    @Test
    public void shouldReturnExistingDependencyMaterialFromMaterialsIfItContainsOne() {
        Materials materials = new Materials();
        DependencyMaterial existingMaterial = new DependencyMaterial(new CaseInsensitiveString("foo"), new CaseInsensitiveString("bar"));
        materials.add(existingMaterial);
        Assert.assertThat(materials.getDependencyMaterial(), Matchers.is(Matchers.sameInstance(existingMaterial)));
    }

    @Test
    public void shouldRemoveJunkFoldersWhenCleanUpIsCalled_hasOneMaterialUseBaseFolderReturnsFalse() throws Exception {
        File junkFolder = temporaryFolder.newFolder("junk-folder");
        Materials materials = new Materials();
        GitMaterial gitMaterial = new GitMaterial("http://some-url.com", "some-branch", "some-folder");
        materials.add(gitMaterial);
        materials.cleanUp(temporaryFolder.getRoot(), Mockito.mock(ConsoleOutputStreamConsumer.class));
        Assert.assertThat(junkFolder.exists(), Matchers.is(false));
        temporaryFolder.delete();
    }

    @Test
    public void shouldNotRemoveJunkFoldersWhenCleanUpIsCalled_hasOneMaterialUseBaseFolderReturnsTrue() throws Exception {
        File junkFolder = temporaryFolder.newFolder("junk-folder");
        Materials materials = new Materials();
        GitMaterial gitMaterial = new GitMaterial("http://some-url.com", "some-branch");
        materials.add(gitMaterial);
        materials.cleanUp(temporaryFolder.getRoot(), Mockito.mock(ConsoleOutputStreamConsumer.class));
        Assert.assertThat(junkFolder.exists(), Matchers.is(true));
        temporaryFolder.delete();
    }

    @Test
    public void shouldGenerateCleanupCommandForRemovingJunkFoldersWhenCleanUpIsCalled_hasOneMaterialUseBaseFolderReturnsFalse() throws Exception {
        Materials materials = new Materials();
        GitMaterial gitMaterial = new GitMaterial("http://some-url.com", "some-branch", "some-folder");
        materials.add(gitMaterial);
        BuildCommand command = materials.cleanUpCommand("basedir");
        Assert.assertThat(command.getName(), Matchers.is("cleandir"));
        Assert.assertThat(command.getStringArg("path"), Matchers.is("basedir"));
        Assert.assertThat(command.getArrayArg("allowed"), Matchers.is(new String[]{ "some-folder", "cruise-output" }));
    }

    @Test
    public void shouldGenerateNoopCommandWhenCleanUpIsCalled_hasOneMaterialUseBaseFolderReturnsTrue() throws Exception {
        Materials materials = new Materials();
        materials.add(new GitMaterial("http://some-url.com", "some-branch"));
        Assert.assertThat(materials.cleanUpCommand("foo"), Matchers.is(BuildCommand.noop()));
    }
}

