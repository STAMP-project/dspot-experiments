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


import JobResult.Failed;
import JobResult.Passed;
import com.thoughtworks.go.buildsession.BuildSessionBasedTestCase;
import com.thoughtworks.go.domain.materials.Modification;
import com.thoughtworks.go.domain.materials.RevisionContext;
import com.thoughtworks.go.domain.materials.git.GitTestRepo;
import com.thoughtworks.go.domain.materials.mercurial.StringRevision;
import com.thoughtworks.go.helper.GitSubmoduleRepos;
import com.thoughtworks.go.matchers.RegexMatcher;
import com.thoughtworks.go.util.command.CommandLine;
import com.thoughtworks.go.util.command.InMemoryStreamConsumer;
import com.thoughtworks.go.util.command.ProcessOutputStreamConsumer;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import junit.framework.TestCase;
import org.apache.commons.io.FileUtils;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class GitMaterialUpdaterTest extends BuildSessionBasedTestCase {
    private static final String SUBMODULE = "submodule-1";

    private File workingDir;

    @Test
    public void shouldCreateBuildCommandUpdateToSpecificRevision() throws Exception {
        GitMaterial material = new GitMaterial(new GitTestRepo(temporaryFolder).projectRepositoryUrl(), true);
        File newFile = new File(workingDir, "second.txt");
        updateTo(material, new RevisionContext(REVISION_1, REVISION_0, 2), Passed);
        Assert.assertThat(console.output(), containsString(("Start updating files at revision " + (REVISION_1.getRevision()))));
        Assert.assertThat(newFile.exists(), is(false));
        console.clear();
        updateTo(material, new RevisionContext(REVISION_2, REVISION_1, 2), Passed);
        Assert.assertThat(console.output(), containsString(("Start updating files at revision " + (REVISION_2.getRevision()))));
        Assert.assertThat(newFile.exists(), is(true));
    }

    @Test
    public void shouldRemoveSubmoduleFolderFromWorkingDirWhenSubmoduleIsRemovedFromRepo() throws Exception {
        GitSubmoduleRepos submoduleRepos = new GitSubmoduleRepos(temporaryFolder);
        submoduleRepos.addSubmodule(GitMaterialUpdaterTest.SUBMODULE, "sub1");
        GitMaterial gitMaterial = new GitMaterial(submoduleRepos.mainRepo().getUrl(), true);
        StringRevision revision = new StringRevision("origin/master");
        updateTo(gitMaterial, new RevisionContext(revision), Passed);
        Assert.assertThat(new File(workingDir, "sub1"), exists());
        submoduleRepos.removeSubmodule("sub1");
        updateTo(gitMaterial, new RevisionContext(revision), Passed);
        Assert.assertThat(new File(workingDir, "sub1"), not(exists()));
    }

    @Test
    public void shouldDeleteAndRecheckoutDirectoryWhenUrlChanges() throws Exception {
        updateTo(new GitMaterial(new GitTestRepo(temporaryFolder).projectRepositoryUrl(), true), new RevisionContext(new StringRevision("origin/master")), Passed);
        File shouldBeRemoved = new File(workingDir, "shouldBeRemoved");
        shouldBeRemoved.createNewFile();
        Assert.assertThat(shouldBeRemoved.exists(), is(true));
        String repositoryUrl = new GitTestRepo(temporaryFolder).projectRepositoryUrl();
        GitMaterial material = new GitMaterial(repositoryUrl, true);
        updateTo(material, new RevisionContext(REVISION_4), Passed);
        Assert.assertThat(localRepoFor(material).workingRepositoryUrl().forCommandline(), is(repositoryUrl));
        Assert.assertThat(shouldBeRemoved.exists(), is(false));
    }

    @Test
    public void shouldNotDeleteAndRecheckoutDirectoryWhenUrlSame() throws Exception {
        GitMaterial material = new GitMaterial(new GitTestRepo(temporaryFolder).projectRepositoryUrl(), true);
        updateTo(material, new RevisionContext(new StringRevision("origin/master")), Passed);
        File shouldNotBeRemoved = new File(new File(workingDir, ".git"), "shouldNotBeRemoved");
        FileUtils.writeStringToFile(shouldNotBeRemoved, "gundi", StandardCharsets.UTF_8);
        Assert.assertThat(shouldNotBeRemoved.exists(), is(true));
        updateTo(material, new RevisionContext(new StringRevision("origin/master")), Passed);
        Assert.assertThat("Should not have deleted whole folder", shouldNotBeRemoved.exists(), is(true));
    }

    /* This is to test the functionality of the private method isRepositoryChanged() */
    @Test
    public void shouldNotDeleteAndRecheckoutDirectoryWhenBranchIsBlank() throws Exception {
        String repositoryUrl = new GitTestRepo(temporaryFolder).projectRepositoryUrl();
        GitMaterial material = new GitMaterial(repositoryUrl, false);
        updateTo(material, new RevisionContext(new StringRevision("origin/master")), Passed);
        File shouldNotBeRemoved = new File(new File(workingDir, ".git"), "shouldNotBeRemoved");
        FileUtils.writeStringToFile(shouldNotBeRemoved, "Text file", StandardCharsets.UTF_8);
        GitMaterial material1 = new GitMaterial(repositoryUrl, " ");
        updateTo(material1, new RevisionContext(new StringRevision("origin/master")), Passed);
        Assert.assertThat("Should not have deleted whole folder", shouldNotBeRemoved.exists(), is(true));
    }

    @Test
    public void shouldDeleteAndRecheckoutDirectoryWhenBranchChanges() throws Exception {
        GitTestRepo repoWithBranch = GitTestRepo.testRepoAtBranch(GIT_FOO_BRANCH_BUNDLE, "foo", temporaryFolder);
        GitMaterial material = new GitMaterial(repoWithBranch.projectRepositoryUrl(), true);
        updateTo(material, new RevisionContext(new StringRevision("origin/master")), Passed);
        InMemoryStreamConsumer output = ProcessOutputStreamConsumer.inMemoryConsumer();
        CommandLine.createCommandLine("git").withEncoding("UTF-8").withArg("branch").withWorkingDir(workingDir).run(output, "");
        Assert.assertThat(output.getStdOut(), is("* master"));
        GitMaterial material1 = new GitMaterial(repoWithBranch.projectRepositoryUrl(), "foo", null, true);
        updateTo(material1, new RevisionContext(new StringRevision("origin/foo")), Passed);
        output = ProcessOutputStreamConsumer.inMemoryConsumer();
        CommandLine.createCommandLine("git").withEncoding("UTF-8").withArg("branch").withWorkingDir(workingDir).run(output, "");
        Assert.assertThat(output.getStdOut(), is("* foo"));
    }

    @Test
    public void shouldLogRepoInfoToConsoleOutWithoutFolder() throws Exception {
        String repositoryUrl = new GitTestRepo(temporaryFolder).projectRepositoryUrl();
        GitMaterial material = new GitMaterial(repositoryUrl, false);
        updateTo(material, new RevisionContext(REVISION_1), Passed);
        Assert.assertThat(console.output(), containsString(String.format("Start updating %s at revision %s from %s", "files", REVISION_1.getRevision(), repositoryUrl)));
    }

    @Test
    public void shouldConvertExistingRepoToFullRepoWhenShallowCloneIsOff() throws IOException {
        String repositoryUrl = new GitTestRepo(temporaryFolder).projectRepositoryUrl();
        GitMaterial shallowMaterial = new GitMaterial(repositoryUrl, true);
        updateTo(shallowMaterial, new RevisionContext(REVISION_3), Passed);
        Assert.assertThat(localRepoFor(shallowMaterial).isShallow(), is(true));
        GitMaterial fullMaterial = new GitMaterial(repositoryUrl, false);
        updateTo(fullMaterial, new RevisionContext(REVISION_4), Passed);
        Assert.assertThat(localRepoFor(fullMaterial).isShallow(), is(false));
    }

    @Test
    public void shouldCleanDirtyFilesUponUpdate() throws IOException {
        String repositoryUrl = new GitTestRepo(temporaryFolder).projectRepositoryUrl();
        GitMaterial material = new GitMaterial(repositoryUrl, true);
        updateTo(material, new RevisionContext(REVISION_4), Passed);
        File shouldBeRemoved = new File(workingDir, "shouldBeRemoved");
        TestCase.assertTrue(shouldBeRemoved.createNewFile());
        updateTo(material, new RevisionContext(REVISION_4), Passed);
        Assert.assertThat(shouldBeRemoved.exists(), is(false));
    }

    @Test
    public void cloneWithDeepWorkingDir() throws Exception {
        GitMaterial material = new GitMaterial(new GitTestRepo(temporaryFolder).projectRepositoryUrl(), "", "foo/bar/baz", true);
        updateTo(material, new RevisionContext(REVISION_4), Passed);
        Assert.assertThat(new File(workingDir, "foo/bar/baz/build.xml").exists(), is(true));
    }

    @Test
    public void failureCommandShouldNotLeakPasswordOnUrl() throws Exception {
        GitMaterial material = new GitMaterial("https://foo:foopassword@this.is.absolute.not.exists", true);
        updateTo(material, new RevisionContext(new StringRevision("origin/master")), Failed);
        Assert.assertThat(console.output(), containsString("https://foo:******@this.is.absolute.not.exists/"));
        Assert.assertThat(console.output(), not(containsString("foopassword")));
    }

    @Test
    public void shouldCleanUnversionedFilesInsideSubmodulesBeforeUpdating() throws Exception {
        GitSubmoduleRepos submoduleRepos = new GitSubmoduleRepos(temporaryFolder);
        String submoduleDirectoryName = "local-submodule";
        submoduleRepos.addSubmodule(GitMaterialUpdaterTest.SUBMODULE, submoduleDirectoryName);
        GitMaterial material = new GitMaterial(submoduleRepos.projectRepositoryUrl(), true);
        updateTo(material, new RevisionContext(new StringRevision("origin/HEAD")), Passed);
        File unversionedFile = new File(new File(workingDir, submoduleDirectoryName), "unversioned_file.txt");
        FileUtils.writeStringToFile(unversionedFile, "this is an unversioned file. lets see you deleting me.. come on.. I dare you!!!!", StandardCharsets.UTF_8);
        updateTo(material, new RevisionContext(new StringRevision("origin/HEAD")), Passed);
        Assert.assertThat(unversionedFile.exists(), Matchers.is(false));
    }

    @Test
    public void shouldRemoveChangesToModifiedFilesInsideSubmodulesBeforeUpdating() throws Exception {
        GitSubmoduleRepos submoduleRepos = new GitSubmoduleRepos(temporaryFolder);
        String submoduleDirectoryName = "local-submodule";
        File remoteSubmoduleLocation = submoduleRepos.addSubmodule(GitMaterialUpdaterTest.SUBMODULE, submoduleDirectoryName);
        GitMaterial material = new GitMaterial(submoduleRepos.projectRepositoryUrl(), true);
        updateTo(material, new RevisionContext(new StringRevision("origin/HEAD")), Passed);
        /* Simulate a local modification of file inside submodule, on agent side. */
        File fileInSubmodule = allFilesIn(new File(workingDir, submoduleDirectoryName), "file-").get(0);
        FileUtils.writeStringToFile(fileInSubmodule, "Some other new content.", StandardCharsets.UTF_8);
        /* Commit a change to the file on the repo. */
        List<Modification> modifications = submoduleRepos.modifyOneFileInSubmoduleAndUpdateMainRepo(remoteSubmoduleLocation, submoduleDirectoryName, fileInSubmodule.getName(), "NEW CONTENT OF FILE");
        updateTo(material, new RevisionContext(new StringRevision(modifications.get(0).getRevision())), Passed);
        Assert.assertThat(FileUtils.readFileToString(fileInSubmodule, StandardCharsets.UTF_8), Matchers.is("NEW CONTENT OF FILE"));
    }

    @Test
    public void shouldAllowSubmoduleUrlstoChange() throws Exception {
        GitSubmoduleRepos submoduleRepos = new GitSubmoduleRepos(temporaryFolder);
        String submoduleDirectoryName = "local-submodule";
        submoduleRepos.addSubmodule(GitMaterialUpdaterTest.SUBMODULE, submoduleDirectoryName);
        GitMaterial material = new GitMaterial(submoduleRepos.projectRepositoryUrl(), true);
        updateTo(material, new RevisionContext(new StringRevision("origin/HEAD")), Passed);
        submoduleRepos.changeSubmoduleUrl(submoduleDirectoryName);
        updateTo(material, new RevisionContext(new StringRevision("origin/HEAD")), Passed);
        Assert.assertThat(console.output(), containsString("Synchronizing submodule url for 'local-submodule'"));
    }

    @Test
    public void shouldOutputSubmoduleRevisionsAfterUpdate() throws Exception {
        GitSubmoduleRepos submoduleRepos = new GitSubmoduleRepos(temporaryFolder);
        submoduleRepos.addSubmodule(GitMaterialUpdaterTest.SUBMODULE, "sub1");
        GitMaterial material = new GitMaterial(submoduleRepos.projectRepositoryUrl(), true);
        updateTo(material, new RevisionContext(new StringRevision("origin/HEAD")), Passed);
        Matcher matcher = Pattern.compile(".*^\\s[a-f0-9A-F]{40} sub1 \\(heads/master\\)$.*", ((Pattern.MULTILINE) | (Pattern.DOTALL))).matcher(console.output());
        Assert.assertThat(matcher.matches(), Matchers.is(true));
    }

    @Test
    public void shouldBombForFetchAndResetWhenSubmoduleUpdateFails() throws Exception {
        GitSubmoduleRepos submoduleRepos = new GitSubmoduleRepos(temporaryFolder);
        File submoduleFolder = submoduleRepos.addSubmodule(GitMaterialUpdaterTest.SUBMODULE, "sub1");
        GitMaterial material = new GitMaterial(submoduleRepos.projectRepositoryUrl(), true);
        FileUtils.deleteDirectory(submoduleFolder);
        Assert.assertThat(submoduleFolder.exists(), Matchers.is(false));
        updateTo(material, new RevisionContext(new StringRevision("origin/HEAD")), Failed);
        // different versions of git use different messages
        // git on windows prints full submodule paths
        Assert.assertThat(console.output(), new RegexMatcher(String.format("[Cc]lone of \'%s\' into submodule path \'((.*)[\\/])?sub1\' failed", Pattern.quote(submoduleFolder.getAbsolutePath()))));
    }
}

