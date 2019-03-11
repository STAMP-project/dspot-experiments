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
package com.thoughtworks.go.config.materials.svn;


import JobResult.Failed;
import JobResult.Passed;
import com.googlecode.junit.ext.JunitExtRunner;
import com.googlecode.junit.ext.RunIf;
import com.thoughtworks.go.buildsession.BuildSessionBasedTestCase;
import com.thoughtworks.go.domain.materials.svn.SubversionRevision;
import com.thoughtworks.go.helper.MaterialsMother;
import com.thoughtworks.go.helper.SvnTestRepo;
import com.thoughtworks.go.junitext.EnhancedOSChecker;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(JunitExtRunner.class)
public class SvnMaterialUpdaterTest extends BuildSessionBasedTestCase {
    private SvnTestRepo svnTestRepo;

    private SvnMaterial svnMaterial;

    SubversionRevision revision = new SubversionRevision("1");

    private File workingDir;

    @Test
    public void shouldNotUpdateIfCheckingOutAFreshCopy() throws IOException {
        updateTo(svnMaterial, new com.thoughtworks.go.domain.materials.RevisionContext(revision), Passed);
        Assert.assertThat(console.output(), Matchers.containsString("Checked out revision"));
        Assert.assertThat(console.output(), Matchers.not(Matchers.containsString("Updating")));
    }

    @Test
    @RunIf(value = EnhancedOSChecker.class, arguments = { DO_NOT_RUN_ON, WINDOWS })
    public void shouldUpdateIfNotCheckingOutFreshCopy() throws IOException {
        updateTo(svnMaterial, new com.thoughtworks.go.domain.materials.RevisionContext(revision), Passed);
        console.clear();
        updateTo(svnMaterial, new com.thoughtworks.go.domain.materials.RevisionContext(revision), Passed);
        Assert.assertThat(console.output(), Matchers.not(Matchers.containsString("Checked out revision")));
        Assert.assertThat(console.output(), Matchers.containsString("Updating"));
    }

    @Test
    public void shouldUpdateToDestinationFolder() throws Exception {
        svnMaterial.setFolder("dest");
        updateTo(svnMaterial, new com.thoughtworks.go.domain.materials.RevisionContext(revision), Passed);
        Assert.assertThat(new File(workingDir, "dest").exists(), Matchers.is(true));
        Assert.assertThat(new File(workingDir, "dest/.svn").exists(), Matchers.is(true));
    }

    @Test
    public void shouldDoAFreshCheckoutIfDestIsNotARepo() throws Exception {
        updateTo(svnMaterial, new com.thoughtworks.go.domain.materials.RevisionContext(revision), Passed);
        console.clear();
        FileUtils.deleteQuietly(new File(workingDir, "svnDir/.svn"));
        updateTo(svnMaterial, new com.thoughtworks.go.domain.materials.RevisionContext(revision), Passed);
        Assert.assertThat(console.output(), Matchers.containsString("Checked out revision"));
        Assert.assertThat(console.output(), Matchers.not(Matchers.containsString("Updating")));
    }

    @Test
    public void shouldDoFreshCheckoutIfUrlChanges() throws Exception {
        updateTo(svnMaterial, new com.thoughtworks.go.domain.materials.RevisionContext(revision), Passed);
        console.clear();
        File shouldBeRemoved = new File(workingDir, "svnDir/shouldBeRemoved");
        shouldBeRemoved.createNewFile();
        Assert.assertThat(shouldBeRemoved.exists(), Matchers.is(true));
        String repositoryUrl = new SvnTestRepo(temporaryFolder).projectRepositoryUrl();
        Assert.assertNotEquals(svnTestRepo.projectRepositoryUrl(), repositoryUrl);
        SvnMaterial material = MaterialsMother.svnMaterial(repositoryUrl);
        updateTo(material, new com.thoughtworks.go.domain.materials.RevisionContext(revision), Passed);
        Assert.assertThat(material.getUrl(), Matchers.is(repositoryUrl));
        Assert.assertThat(console.output(), Matchers.containsString("Checked out revision"));
        Assert.assertThat(console.output(), Matchers.not(Matchers.containsString("Updating")));
        Assert.assertThat(shouldBeRemoved.exists(), Matchers.is(false));
    }

    @Test
    public void shouldNotLeakPasswordInUrlIfCheckoutFails() throws Exception {
        SvnMaterial material = MaterialsMother.svnMaterial("https://foo:foopassword@thisdoesnotexist.io/repo");
        updateTo(material, new com.thoughtworks.go.domain.materials.RevisionContext(revision), Failed);
        Assert.assertThat(console.output(), Matchers.containsString("https://foo:******@thisdoesnotexist.io/repo"));
        Assert.assertThat(console.output(), Matchers.not(Matchers.containsString("foopassword")));
    }
}

