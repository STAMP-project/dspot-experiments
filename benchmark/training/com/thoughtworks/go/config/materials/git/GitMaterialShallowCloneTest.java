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
package com.thoughtworks.go.config.materials.git;


import SystemEnvironment.GO_SERVER_SHALLOW_CLONE;
import com.thoughtworks.go.domain.materials.Modification;
import com.thoughtworks.go.domain.materials.TestSubprocessExecutionContext;
import com.thoughtworks.go.domain.materials.git.GitTestRepo;
import com.thoughtworks.go.domain.materials.mercurial.StringRevision;
import com.thoughtworks.go.helper.TestRepo;
import com.thoughtworks.go.util.SystemEnvironment;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

import static GitMaterialConfig.DEFAULT_BRANCH;


public class GitMaterialShallowCloneTest {
    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    private GitTestRepo repo;

    private File workingDir;

    @Test
    public void defaultShallowFlagIsOff() throws Exception {
        Assert.assertThat(new GitMaterial(repo.projectRepositoryUrl()).isShallowClone(), Matchers.is(false));
        Assert.assertThat(new GitMaterial(repo.projectRepositoryUrl(), null).isShallowClone(), Matchers.is(false));
        Assert.assertThat(new GitMaterial(repo.projectRepositoryUrl(), true).isShallowClone(), Matchers.is(true));
        Assert.assertThat(isShallowClone(), Matchers.is(false));
        Assert.assertThat(new GitMaterial(new GitMaterialConfig(repo.projectRepositoryUrl(), DEFAULT_BRANCH, true)).isShallowClone(), Matchers.is(true));
        Assert.assertThat(new GitMaterial(new GitMaterialConfig(repo.projectRepositoryUrl(), DEFAULT_BRANCH, false)).isShallowClone(), Matchers.is(false));
        TestRepo.internalTearDown();
    }

    @Test
    public void shouldGetLatestModificationWithShallowClone() throws IOException {
        GitMaterial material = new GitMaterial(repo.projectRepositoryUrl(), true);
        List<Modification> mods = material.latestModification(workingDir, context());
        Assert.assertThat(mods.size(), Matchers.is(1));
        Assert.assertThat(mods.get(0).getComment(), Matchers.is("Added 'run-till-file-exists' ant target"));
        Assert.assertThat(localRepoFor(material).isShallow(), Matchers.is(true));
        Assert.assertThat(localRepoFor(material).containsRevisionInBranch(GitTestRepo.REVISION_0), Matchers.is(false));
        Assert.assertThat(localRepoFor(material).currentRevision(), Matchers.is(GitTestRepo.REVISION_4.getRevision()));
    }

    @Test
    public void shouldGetModificationSinceANotInitiallyClonedRevision() {
        GitMaterial material = new GitMaterial(repo.projectRepositoryUrl(), true);
        List<Modification> modifications = material.modificationsSince(workingDir, GitTestRepo.REVISION_0, context());
        Assert.assertThat(modifications.size(), Matchers.is(4));
        Assert.assertThat(modifications.get(0).getRevision(), Matchers.is(GitTestRepo.REVISION_4.getRevision()));
        Assert.assertThat(modifications.get(0).getComment(), Matchers.is("Added 'run-till-file-exists' ant target"));
        Assert.assertThat(modifications.get(1).getRevision(), Matchers.is(GitTestRepo.REVISION_3.getRevision()));
        Assert.assertThat(modifications.get(1).getComment(), Matchers.is("adding build.xml"));
        Assert.assertThat(modifications.get(2).getRevision(), Matchers.is(GitTestRepo.REVISION_2.getRevision()));
        Assert.assertThat(modifications.get(2).getComment(), Matchers.is("Created second.txt from first.txt"));
        Assert.assertThat(modifications.get(3).getRevision(), Matchers.is(GitTestRepo.REVISION_1.getRevision()));
        Assert.assertThat(modifications.get(3).getComment(), Matchers.is("Added second line"));
    }

    @Test
    public void shouldBeAbleToUpdateToRevisionNotFetched() {
        GitMaterial material = new GitMaterial(repo.projectRepositoryUrl(), true);
        material.updateTo(inMemoryConsumer(), workingDir, new com.thoughtworks.go.domain.materials.RevisionContext(GitTestRepo.REVISION_3, GitTestRepo.REVISION_2, 2), context());
        Assert.assertThat(localRepoFor(material).currentRevision(), Matchers.is(GitTestRepo.REVISION_3.getRevision()));
        Assert.assertThat(localRepoFor(material).containsRevisionInBranch(GitTestRepo.REVISION_2), Matchers.is(true));
        Assert.assertThat(localRepoFor(material).containsRevisionInBranch(GitTestRepo.REVISION_3), Matchers.is(true));
    }

    @Test
    public void configShouldIncludesShallowFlag() {
        GitMaterialConfig shallowConfig = ((GitMaterialConfig) (new GitMaterial(repo.projectRepositoryUrl(), true).config()));
        Assert.assertThat(shallowConfig.isShallowClone(), Matchers.is(true));
        GitMaterialConfig normalConfig = ((GitMaterialConfig) (new GitMaterial(repo.projectRepositoryUrl(), null).config()));
        Assert.assertThat(normalConfig.isShallowClone(), Matchers.is(false));
    }

    @Test
    public void xmlAttributesShouldIncludesShallowFlag() {
        GitMaterial material = new GitMaterial(repo.projectRepositoryUrl(), true);
        Assert.assertThat(material.getAttributesForXml().get("shallowClone"), Matchers.is(true));
    }

    @Test
    public void attributesShouldIncludeShallowFlag() {
        GitMaterial material = new GitMaterial(repo.projectRepositoryUrl(), true);
        Map gitConfig = ((Map) (material.getAttributes(false).get("git-configuration")));
        Assert.assertThat(gitConfig.get("shallow-clone"), Matchers.is(true));
    }

    @Test
    public void shouldConvertExistingRepoToFullRepoWhenShallowCloneIsOff() {
        GitMaterial material = new GitMaterial(repo.projectRepositoryUrl(), true);
        material.latestModification(workingDir, context());
        Assert.assertThat(localRepoFor(material).isShallow(), Matchers.is(true));
        material = new GitMaterial(repo.projectRepositoryUrl(), false);
        material.latestModification(workingDir, context());
        Assert.assertThat(localRepoFor(material).isShallow(), Matchers.is(false));
    }

    @Test
    public void withShallowCloneShouldGenerateANewMaterialWithOverriddenShallowConfig() {
        GitMaterial original = new GitMaterial(repo.projectRepositoryUrl(), false);
        Assert.assertThat(isShallowClone(), Matchers.is(true));
        Assert.assertThat(isShallowClone(), Matchers.is(false));
        Assert.assertThat(original.isShallowClone(), Matchers.is(false));
    }

    @Test
    public void updateToANewRevisionShouldNotResultInUnshallowing() throws IOException {
        GitMaterial material = new GitMaterial(repo.projectRepositoryUrl(), true);
        material.updateTo(inMemoryConsumer(), workingDir, new com.thoughtworks.go.domain.materials.RevisionContext(GitTestRepo.REVISION_4, GitTestRepo.REVISION_4, 1), context());
        Assert.assertThat(localRepoFor(material).isShallow(), Matchers.is(true));
        List<Modification> modifications = repo.addFileAndPush("newfile", "add new file");
        StringRevision newRevision = new StringRevision(modifications.get(0).getRevision());
        material.updateTo(inMemoryConsumer(), workingDir, new com.thoughtworks.go.domain.materials.RevisionContext(newRevision, newRevision, 1), context());
        Assert.assertThat(new File(workingDir, "newfile").exists(), Matchers.is(true));
        Assert.assertThat(localRepoFor(material).isShallow(), Matchers.is(true));
    }

    @Test
    public void shouldUnshallowServerSideRepoCompletelyOnRetrievingModificationsSincePreviousRevision() {
        SystemEnvironment mockSystemEnvironment = Mockito.mock(SystemEnvironment.class);
        GitMaterial material = new GitMaterial(repo.projectRepositoryUrl(), true);
        Mockito.when(mockSystemEnvironment.get(GO_SERVER_SHALLOW_CLONE)).thenReturn(false);
        material.modificationsSince(workingDir, GitTestRepo.REVISION_4, new TestSubprocessExecutionContext(mockSystemEnvironment, true));
        Assert.assertThat(localRepoFor(material).isShallow(), Matchers.is(false));
    }

    @Test
    public void shouldNotUnshallowOnServerSideIfShallowClonePropertyIsOnAndRepoIsAlreadyShallow() {
        SystemEnvironment mockSystemEnvironment = Mockito.mock(SystemEnvironment.class);
        GitMaterial material = new GitMaterial(repo.projectRepositoryUrl(), true);
        Mockito.when(mockSystemEnvironment.get(GO_SERVER_SHALLOW_CLONE)).thenReturn(true);
        material.modificationsSince(workingDir, GitTestRepo.REVISION_4, new TestSubprocessExecutionContext(mockSystemEnvironment, false));
        Assert.assertThat(localRepoFor(material).isShallow(), Matchers.is(true));
    }
}

