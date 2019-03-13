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
package com.thoughtworks.go.config.materials.mercurial;


import JobResult.Failed;
import JobResult.Passed;
import com.thoughtworks.go.buildsession.BuildSessionBasedTestCase;
import com.thoughtworks.go.domain.materials.Modification;
import com.thoughtworks.go.domain.materials.TestSubprocessExecutionContext;
import com.thoughtworks.go.domain.materials.mercurial.StringRevision;
import com.thoughtworks.go.domain.materials.svn.MaterialUrl;
import com.thoughtworks.go.helper.HgTestRepo;
import com.thoughtworks.go.helper.MaterialsMother;
import java.io.File;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class HgMaterialUpdaterTest extends BuildSessionBasedTestCase {
    private HgMaterial hgMaterial;

    private HgTestRepo hgTestRepo;

    private File workingFolder;

    @Test
    public void shouldUpdateToSpecificRevision() throws Exception {
        updateTo(hgMaterial, new com.thoughtworks.go.domain.materials.RevisionContext(HgTestRepo.REVISION_0), Passed);
        File end2endFolder = new File(workingFolder, "end2end");
        Assert.assertThat(end2endFolder.listFiles().length, Matchers.is(3));
        updateTo(hgMaterial, new com.thoughtworks.go.domain.materials.RevisionContext(HgTestRepo.REVISION_1), Passed);
        Assert.assertThat(end2endFolder.listFiles().length, Matchers.is(4));
    }

    @Test
    public void shouldUpdateToDestinationFolder() throws Exception {
        hgMaterial.setFolder("dest");
        updateTo(hgMaterial, new com.thoughtworks.go.domain.materials.RevisionContext(HgTestRepo.REVISION_0), Passed);
        File end2endFolder = new File(workingFolder, "dest/end2end");
        Assert.assertThat(new File(workingFolder, "dest").exists(), Matchers.is(true));
        Assert.assertThat(end2endFolder.exists(), Matchers.is(true));
    }

    @Test
    public void shouldLogRepoInfoToConsoleOutWithoutFolder() throws Exception {
        updateTo(hgMaterial, new com.thoughtworks.go.domain.materials.RevisionContext(new StringRevision("0")), Passed);
        Assert.assertThat(console.output(), Matchers.containsString(String.format("Start updating %s at revision %s from %s", "files", "0", hgMaterial.getUrl())));
    }

    @Test
    public void failureCommandShouldNotLeakPasswordOnUrl() throws Exception {
        HgMaterial material = MaterialsMother.hgMaterial("https://foo:foopassword@this.is.absolute.not.exists");
        updateTo(material, new com.thoughtworks.go.domain.materials.RevisionContext(HgTestRepo.REVISION_1), Failed);
        Assert.assertThat(console.output(), Matchers.containsString("https://foo:******@this.is.absolute.not.exists"));
        Assert.assertThat(console.output(), Matchers.not(Matchers.containsString("foopassword")));
    }

    @Test
    public void shouldCreateBuildCommandUpdateToSpecificRevision() throws Exception {
        File newFile = new File(workingFolder, "end2end/revision2.txt");
        updateTo(hgMaterial, new com.thoughtworks.go.domain.materials.RevisionContext(HgTestRepo.REVISION_0), Passed);
        Assert.assertThat(console.output(), Matchers.containsString(("Start updating files at revision " + (HgTestRepo.REVISION_0.getRevision()))));
        Assert.assertThat(newFile.exists(), Matchers.is(false));
        console.clear();
        updateTo(hgMaterial, new com.thoughtworks.go.domain.materials.RevisionContext(HgTestRepo.REVISION_2, HgTestRepo.REVISION_1, 2), Passed);
        Assert.assertThat(console.output(), Matchers.containsString(("Start updating files at revision " + (HgTestRepo.REVISION_2.getRevision()))));
        Assert.assertThat(newFile.exists(), Matchers.is(true));
    }

    @Test
    public void shouldNotDeleteAndRecheckoutDirectoryUnlessUrlChanges() throws Exception {
        String repositoryUrl = new HgTestRepo(temporaryFolder).projectRepositoryUrl();
        HgMaterial material = MaterialsMother.hgMaterial(repositoryUrl);
        updateTo(material, new com.thoughtworks.go.domain.materials.RevisionContext(HgTestRepo.REVISION_0), Passed);
        File shouldNotBeRemoved = new File(workingFolder, "shouldBeRemoved");
        shouldNotBeRemoved.createNewFile();
        Assert.assertThat(shouldNotBeRemoved.exists(), Matchers.is(true));
        updateTo(material, new com.thoughtworks.go.domain.materials.RevisionContext(HgTestRepo.REVISION_2), Passed);
        assert MaterialUrl.sameUrl(material.getUrl(), repositoryUrl);
        Assert.assertThat(shouldNotBeRemoved.exists(), Matchers.is(true));
    }

    @Test
    public void shouldDeleteAndRecheckoutDirectoryWhenUrlChanges() throws Exception {
        updateTo(hgMaterial, new com.thoughtworks.go.domain.materials.RevisionContext(HgTestRepo.REVISION_0), Passed);
        File shouldBeRemoved = new File(workingFolder, "shouldBeRemoved");
        shouldBeRemoved.createNewFile();
        Assert.assertThat(shouldBeRemoved.exists(), Matchers.is(true));
        String repositoryUrl = new HgTestRepo(temporaryFolder).projectRepositoryUrl();
        HgMaterial material = MaterialsMother.hgMaterial(repositoryUrl);
        updateTo(material, new com.thoughtworks.go.domain.materials.RevisionContext(HgTestRepo.REVISION_2), Passed);
        Assert.assertThat(material.getUrl(), Matchers.not(hgMaterial.getUrl()));
        assert MaterialUrl.sameUrl(material.getUrl(), repositoryUrl);
        Assert.assertThat(shouldBeRemoved.exists(), Matchers.is(false));
    }

    @Test
    public void shouldPullNewChangesFromRemoteBeforeUpdating() throws Exception {
        File newWorkingFolder = temporaryFolder.newFolder("newWorkingFolder");
        updateTo(hgMaterial, new com.thoughtworks.go.domain.materials.RevisionContext(HgTestRepo.REVISION_0), Passed);
        String repositoryUrl = hgTestRepo.projectRepositoryUrl();
        HgMaterial material = MaterialsMother.hgMaterial(repositoryUrl);
        Assert.assertThat(material.getUrl(), Matchers.is(hgMaterial.getUrl()));
        updateTo(material, new com.thoughtworks.go.domain.materials.RevisionContext(HgTestRepo.REVISION_0), Passed, newWorkingFolder);
        hgTestRepo.commitAndPushFile("SomeDocumentation.txt", "whatever");
        List<Modification> modification = hgMaterial.latestModification(workingFolder, new TestSubprocessExecutionContext());
        StringRevision revision = new StringRevision(modification.get(0).getRevision());
        updateTo(material, new com.thoughtworks.go.domain.materials.RevisionContext(revision), Passed, newWorkingFolder);
        Assert.assertThat(console.output(), Matchers.containsString(("Start updating files at revision " + (revision.getRevision()))));
    }
}

