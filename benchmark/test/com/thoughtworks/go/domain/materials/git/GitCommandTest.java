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
package com.thoughtworks.go.domain.materials.git;


import GitTestRepo.REVISION_0;
import GitTestRepo.REVISION_1;
import GitTestRepo.REVISION_2;
import GitTestRepo.REVISION_3;
import GitTestRepo.REVISION_4;
import ModifiedAction.modified;
import com.thoughtworks.go.config.materials.git.GitMaterialConfig;
import com.thoughtworks.go.domain.materials.Modification;
import com.thoughtworks.go.domain.materials.ModifiedFile;
import com.thoughtworks.go.domain.materials.TestSubprocessExecutionContext;
import com.thoughtworks.go.domain.materials.mercurial.StringRevision;
import com.thoughtworks.go.helper.GitSubmoduleRepos;
import com.thoughtworks.go.mail.SysOutStreamConsumer;
import com.thoughtworks.go.matchers.RegexMatcher;
import com.thoughtworks.go.util.ReflectionUtil;
import com.thoughtworks.go.util.command.CommandLine;
import com.thoughtworks.go.util.command.CommandLineException;
import com.thoughtworks.go.util.command.InMemoryStreamConsumer;
import com.thoughtworks.go.util.command.ProcessOutputStreamConsumer;
import com.thoughtworks.go.util.command.UrlArgument;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mock;

import static com.thoughtworks.go.domain.materials.git.GitTestRepo.GitTestRepo.GIT_SUBMODULE_REF_BUNDLE;
import static com.thoughtworks.go.util.DateUtils.parseRFC822;


public class GitCommandTest {
    private static final String BRANCH = "foo";

    private static final String SUBMODULE = "submodule-1";

    private GitCommand git;

    private String repoUrl;

    private File repoLocation;

    private static final Date THREE_DAYS_FROM_NOW = DateUtils.setMilliseconds(DateUtils.addDays(new Date(), 3), 0);

    private GitTestRepo.GitTestRepo gitRepo;

    private File gitLocalRepoDir;

    private GitTestRepo.GitTestRepo gitFooBranchBundle;

    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Mock
    private TestSubprocessExecutionContext testSubprocessExecutionContext;

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldDefaultToMasterIfNoBranchIsSpecified() {
        Assert.assertThat(ReflectionUtil.getField(new GitCommand(null, gitLocalRepoDir, null, false, new HashMap(), null), "branch"), is("master"));
        Assert.assertThat(ReflectionUtil.getField(new GitCommand(null, gitLocalRepoDir, " ", false, new HashMap(), null), "branch"), is("master"));
        Assert.assertThat(ReflectionUtil.getField(new GitCommand(null, gitLocalRepoDir, "master", false, new HashMap(), null), "branch"), is("master"));
        Assert.assertThat(ReflectionUtil.getField(new GitCommand(null, gitLocalRepoDir, "branch", false, new HashMap(), null), "branch"), is("branch"));
    }

    @Test
    public void shouldCloneFromMasterWhenNoBranchIsSpecified() {
        InMemoryStreamConsumer output = ProcessOutputStreamConsumer.inMemoryConsumer();
        git.clone(output, repoUrl);
        CommandLine commandLine = CommandLine.createCommandLine("git").withEncoding("UTF-8").withArg("branch").withWorkingDir(gitLocalRepoDir);
        commandLine.run(output, "");
        Assert.assertThat(output.getStdOut(), is("* master"));
    }

    @Test
    public void freshCloneDoesNotHaveWorkingCopy() {
        assertWorkingCopyNotCheckedOut();
    }

    @Test
    public void freshCloneOnAgentSideShouldHaveWorkingCopyCheckedOut() throws IOException {
        InMemoryStreamConsumer output = ProcessOutputStreamConsumer.inMemoryConsumer();
        File workingDir = createTempWorkingDirectory();
        GitCommand git = new GitCommand(null, workingDir, GitMaterialConfig.DEFAULT_BRANCH, false, new HashMap(), null);
        git.clone(output, repoUrl);
        assertWorkingCopyCheckedOut(workingDir);
    }

    @Test
    public void fullCloneIsNotShallow() {
        Assert.assertThat(git.isShallow(), is(false));
    }

    @Test
    public void shouldOnlyCloneLimitedRevisionsIfDepthSpecified() throws Exception {
        FileUtils.deleteQuietly(this.gitLocalRepoDir);
        git.clone(ProcessOutputStreamConsumer.inMemoryConsumer(), repoUrl, 2);
        Assert.assertThat(git.isShallow(), is(true));
        Assert.assertThat(git.containsRevisionInBranch(REVISION_4), is(true));
        Assert.assertThat(git.containsRevisionInBranch(REVISION_3), is(true));
        // can not assert on revision_2, because on old version of git (1.7)
        // depth '2' actually clone 3 revisions
        Assert.assertThat(git.containsRevisionInBranch(REVISION_1), is(false));
        Assert.assertThat(git.containsRevisionInBranch(REVISION_0), is(false));
    }

    @Test
    public void unshallowALocalRepoWithArbitraryDepth() throws Exception {
        FileUtils.deleteQuietly(this.gitLocalRepoDir);
        git.clone(ProcessOutputStreamConsumer.inMemoryConsumer(), repoUrl, 2);
        git.unshallow(ProcessOutputStreamConsumer.inMemoryConsumer(), 3);
        Assert.assertThat(git.isShallow(), is(true));
        Assert.assertThat(git.containsRevisionInBranch(REVISION_2), is(true));
        // can not assert on revision_1, because on old version of git (1.7)
        // depth '3' actually clone 4 revisions
        Assert.assertThat(git.containsRevisionInBranch(REVISION_0), is(false));
        git.unshallow(ProcessOutputStreamConsumer.inMemoryConsumer(), Integer.MAX_VALUE);
        Assert.assertThat(git.isShallow(), is(false));
        Assert.assertThat(git.containsRevisionInBranch(REVISION_0), is(true));
    }

    @Test
    public void unshallowShouldNotResultInWorkingCopyCheckout() {
        FileUtils.deleteQuietly(this.gitLocalRepoDir);
        git.cloneWithNoCheckout(ProcessOutputStreamConsumer.inMemoryConsumer(), repoUrl);
        git.unshallow(ProcessOutputStreamConsumer.inMemoryConsumer(), 3);
        assertWorkingCopyNotCheckedOut();
    }

    @Test
    public void shouldCloneFromBranchWhenMaterialPointsToABranch() throws IOException {
        gitLocalRepoDir = createTempWorkingDirectory();
        git = new GitCommand(null, gitLocalRepoDir, GitCommandTest.BRANCH, false, new HashMap(), null);
        GitCommand branchedGit = new GitCommand(null, gitLocalRepoDir, GitCommandTest.BRANCH, false, new HashMap(), null);
        branchedGit.clone(ProcessOutputStreamConsumer.inMemoryConsumer(), gitFooBranchBundle.projectRepositoryUrl());
        InMemoryStreamConsumer output = ProcessOutputStreamConsumer.inMemoryConsumer();
        CommandLine.createCommandLine("git").withEncoding("UTF-8").withArg("branch").withWorkingDir(gitLocalRepoDir).run(output, "");
        Assert.assertThat(output.getStdOut(), is("* foo"));
    }

    @Test
    public void shouldGetTheCurrentBranchForTheCheckedOutRepo() throws IOException {
        gitLocalRepoDir = createTempWorkingDirectory();
        CommandLine gitCloneCommand = CommandLine.createCommandLine("git").withEncoding("UTF-8").withArg("clone");
        gitCloneCommand.withArg(("--branch=" + (GitCommandTest.BRANCH))).withArg(new UrlArgument(gitFooBranchBundle.projectRepositoryUrl())).withArg(gitLocalRepoDir.getAbsolutePath());
        gitCloneCommand.run(ProcessOutputStreamConsumer.inMemoryConsumer(), "");
        git = new GitCommand(null, gitLocalRepoDir, GitCommandTest.BRANCH, false, new HashMap(), null);
        Assert.assertThat(git.getCurrentBranch(), is(GitCommandTest.BRANCH));
    }

    @Test
    public void shouldBombForFetchFailure() throws IOException {
        executeOnGitRepo("git", "remote", "rm", "origin");
        executeOnGitRepo("git", "remote", "add", "origin", "git://user:secret@foo.bar/baz");
        try {
            InMemoryStreamConsumer output = new InMemoryStreamConsumer();
            git.fetch(output);
            Assert.fail(("should have failed for non 0 return code. Git output was:\n " + (output.getAllOutput())));
        } catch (Exception e) {
            Assert.assertThat(e.getMessage(), is("git fetch failed for [git://user:******@foo.bar/baz]"));
        }
    }

    @Test
    public void shouldBombForResettingFailure() throws IOException {
        try {
            git.resetWorkingDir(new SysOutStreamConsumer(), new StringRevision("abcdef"));
            Assert.fail("should have failed for non 0 return code");
        } catch (Exception e) {
            Assert.assertThat(e.getMessage(), is(String.format("git reset failed for [%s]", gitLocalRepoDir)));
        }
    }

    @Test
    public void shouldOutputSubmoduleRevisionsAfterUpdate() throws Exception {
        GitSubmoduleRepos submoduleRepos = new GitSubmoduleRepos(temporaryFolder);
        submoduleRepos.addSubmodule(GitCommandTest.SUBMODULE, "sub1");
        GitCommand gitWithSubmodule = new GitCommand(null, createTempWorkingDirectory(), GitMaterialConfig.DEFAULT_BRANCH, false, new HashMap(), null);
        gitWithSubmodule.clone(ProcessOutputStreamConsumer.inMemoryConsumer(), submoduleRepos.mainRepo().getUrl());
        InMemoryStreamConsumer outConsumer = new InMemoryStreamConsumer();
        gitWithSubmodule.resetWorkingDir(outConsumer, new StringRevision("HEAD"));
        Matcher matcher = Pattern.compile(".*^\\s[a-f0-9A-F]{40} sub1 \\(heads/master\\)$.*", ((Pattern.MULTILINE) | (Pattern.DOTALL))).matcher(outConsumer.getAllOutput());
        Assert.assertThat(matcher.matches(), is(true));
    }

    @Test
    public void shouldBombForResetWorkingDirWhenSubmoduleUpdateFails() throws Exception {
        GitSubmoduleRepos submoduleRepos = new GitSubmoduleRepos(temporaryFolder);
        File submoduleFolder = submoduleRepos.addSubmodule(GitCommandTest.SUBMODULE, "sub1");
        GitCommand gitWithSubmodule = new GitCommand(null, createTempWorkingDirectory(), GitMaterialConfig.DEFAULT_BRANCH, false, new HashMap(), null);
        gitWithSubmodule.clone(ProcessOutputStreamConsumer.inMemoryConsumer(), submoduleRepos.mainRepo().getUrl());
        FileUtils.deleteDirectory(submoduleFolder);
        Assert.assertThat(submoduleFolder.exists(), is(false));
        try {
            gitWithSubmodule.resetWorkingDir(new SysOutStreamConsumer(), new StringRevision("HEAD"));
            Assert.fail("should have failed for non 0 return code");
        } catch (Exception e) {
            Assert.assertThat(e.getMessage(), new RegexMatcher(String.format("[Cc]lone of \'%s\' into submodule path \'((.*)[\\/])?sub1\' failed", Pattern.quote(submoduleFolder.getAbsolutePath()))));
        }
    }

    @Test
    public void shouldRetrieveLatestModification() throws Exception {
        Modification mod = git.latestModification().get(0);
        Assert.assertThat(mod.getUserName(), is("Chris Turner <cturner@thoughtworks.com>"));
        Assert.assertThat(mod.getComment(), is("Added 'run-till-file-exists' ant target"));
        Assert.assertThat(mod.getModifiedTime(), is(parseRFC822("Fri, 12 Feb 2010 16:12:04 -0800")));
        Assert.assertThat(mod.getRevision(), is("5def073a425dfe239aabd4bf8039ffe3b0e8856b"));
        List<ModifiedFile> files = mod.getModifiedFiles();
        Assert.assertThat(files.size(), is(1));
        Assert.assertThat(files.get(0).getFileName(), is("build.xml"));
        Assert.assertThat(files.get(0).getAction(), Matchers.is(modified));
    }

    @Test
    public void shouldRetrieveLatestModificationWhenColoringIsSetToAlways() throws Exception {
        setColoring();
        Modification mod = git.latestModification().get(0);
        Assert.assertThat(mod.getUserName(), is("Chris Turner <cturner@thoughtworks.com>"));
        Assert.assertThat(mod.getComment(), is("Added 'run-till-file-exists' ant target"));
        Assert.assertThat(mod.getModifiedTime(), is(parseRFC822("Fri, 12 Feb 2010 16:12:04 -0800")));
        Assert.assertThat(mod.getRevision(), is("5def073a425dfe239aabd4bf8039ffe3b0e8856b"));
        List<ModifiedFile> files = mod.getModifiedFiles();
        Assert.assertThat(files.size(), is(1));
        Assert.assertThat(files.get(0).getFileName(), is("build.xml"));
        Assert.assertThat(files.get(0).getAction(), Matchers.is(modified));
    }

    @Test
    public void retrieveLatestModificationShouldNotResultInWorkingCopyCheckOut() throws Exception {
        git.latestModification();
        assertWorkingCopyNotCheckedOut();
    }

    @Test
    public void getModificationsSinceShouldNotResultInWorkingCopyCheckOut() throws Exception {
        git.modificationsSince(REVISION_2);
        assertWorkingCopyNotCheckedOut();
    }

    @Test
    public void shouldReturnNothingForModificationsSinceIfARebasedCommitSHAIsPassed() throws IOException {
        GitTestRepo.GitTestRepo remoteRepo = new GitTestRepo.GitTestRepo(temporaryFolder);
        executeOnGitRepo("git", "remote", "rm", "origin");
        executeOnGitRepo("git", "remote", "add", "origin", remoteRepo.projectRepositoryUrl());
        GitCommand command = new GitCommand(remoteRepo.createMaterial().getFingerprint(), gitLocalRepoDir, "master", false, new HashMap(), null);
        Modification modification = remoteRepo.addFileAndAmend("foo", "amendedCommit").get(0);
        Assert.assertThat(command.modificationsSince(new StringRevision(modification.getRevision())).isEmpty(), is(true));
    }

    @Test
    public void shouldReturnTheRebasedCommitForModificationsSinceTheRevisionBeforeRebase() throws IOException {
        GitTestRepo.GitTestRepo remoteRepo = new GitTestRepo.GitTestRepo(temporaryFolder);
        executeOnGitRepo("git", "remote", "rm", "origin");
        executeOnGitRepo("git", "remote", "add", "origin", remoteRepo.projectRepositoryUrl());
        GitCommand command = new GitCommand(remoteRepo.createMaterial().getFingerprint(), gitLocalRepoDir, "master", false, new HashMap(), null);
        Modification modification = remoteRepo.addFileAndAmend("foo", "amendedCommit").get(0);
        Assert.assertThat(command.modificationsSince(REVISION_4).get(0), is(modification));
    }

    @Test
    public void shouldReturnTheRebasedCommitForModificationsSinceTheRevisionBeforeRebaseWithColoringIsSetToAlways() throws IOException {
        GitTestRepo.GitTestRepo remoteRepo = new GitTestRepo.GitTestRepo(temporaryFolder);
        executeOnGitRepo("git", "remote", "rm", "origin");
        executeOnGitRepo("git", "remote", "add", "origin", remoteRepo.projectRepositoryUrl());
        GitCommand command = new GitCommand(remoteRepo.createMaterial().getFingerprint(), gitLocalRepoDir, "master", false, new HashMap(), null);
        Modification modification = remoteRepo.addFileAndAmend("foo", "amendedCommit").get(0);
        setColoring();
        Assert.assertThat(command.modificationsSince(REVISION_4).get(0), is(modification));
    }

    @Test(expected = CommandLineException.class)
    public void shouldBombIfCheckedForModificationsSinceWithASHAThatNoLongerExists() throws IOException {
        GitTestRepo.GitTestRepo remoteRepo = new GitTestRepo.GitTestRepo(temporaryFolder);
        executeOnGitRepo("git", "remote", "rm", "origin");
        executeOnGitRepo("git", "remote", "add", "origin", remoteRepo.projectRepositoryUrl());
        GitCommand command = new GitCommand(remoteRepo.createMaterial().getFingerprint(), gitLocalRepoDir, "master", false, new HashMap(), null);
        Modification modification = remoteRepo.checkInOneFile("foo", "Adding a commit").get(0);
        remoteRepo.addFileAndAmend("bar", "amendedCommit");
        command.modificationsSince(new StringRevision(modification.getRevision()));
    }

    @Test(expected = CommandLineException.class)
    public void shouldBombIfCheckedForModificationsSinceWithANonExistentRef() throws IOException {
        GitTestRepo.GitTestRepo remoteRepo = new GitTestRepo.GitTestRepo(temporaryFolder);
        executeOnGitRepo("git", "remote", "rm", "origin");
        executeOnGitRepo("git", "remote", "add", "origin", remoteRepo.projectRepositoryUrl());
        GitCommand command = new GitCommand(remoteRepo.createMaterial().getFingerprint(), gitLocalRepoDir, "non-existent-branch", false, new HashMap(), null);
        Modification modification = remoteRepo.checkInOneFile("foo", "Adding a commit").get(0);
        command.modificationsSince(new StringRevision(modification.getRevision()));
    }

    @Test
    public void shouldBombWhileRetrievingLatestModificationFromANonExistentRef() throws IOException {
        expectedException.expect(CommandLineException.class);
        expectedException.expectMessage("ambiguous argument 'origin/non-existent-branch': unknown revision or path not in the working tree.");
        GitTestRepo.GitTestRepo remoteRepo = new GitTestRepo.GitTestRepo(temporaryFolder);
        executeOnGitRepo("git", "remote", "rm", "origin");
        executeOnGitRepo("git", "remote", "add", "origin", remoteRepo.projectRepositoryUrl());
        GitCommand command = new GitCommand(remoteRepo.createMaterial().getFingerprint(), gitLocalRepoDir, "non-existent-branch", false, new HashMap(), null);
        command.latestModification();
    }

    @Test
    public void shouldReturnTrueIfTheGivenBranchContainsTheRevision() {
        Assert.assertThat(git.containsRevisionInBranch(REVISION_4), is(true));
    }

    @Test
    public void shouldReturnFalseIfTheGivenBranchDoesNotContainTheRevision() {
        Assert.assertThat(git.containsRevisionInBranch(NON_EXISTENT_REVISION), is(false));
    }

    @Test
    public void shouldRetrieveFilenameForInitialRevision() throws IOException {
        GitTestRepo.GitTestRepo testRepo = new GitTestRepo.GitTestRepo(GIT_SUBMODULE_REF_BUNDLE, temporaryFolder);
        GitCommand gitCommand = new GitCommand(null, testRepo.gitRepository(), GitMaterialConfig.DEFAULT_BRANCH, false, new HashMap(), null);
        Modification modification = gitCommand.latestModification().get(0);
        Assert.assertThat(modification.getModifiedFiles().size(), is(1));
        Assert.assertThat(modification.getModifiedFiles().get(0).getFileName(), is("remote.txt"));
    }

    @Test
    public void shouldRetrieveLatestModificationFromBranch() throws Exception {
        GitTestRepo.GitTestRepo branchedRepo = GitTestRepo.GitTestRepo.testRepoAtBranch(GIT_FOO_BRANCH_BUNDLE, GitCommandTest.BRANCH, temporaryFolder);
        GitCommand branchedGit = new GitCommand(null, createTempWorkingDirectory(), GitCommandTest.BRANCH, false, new HashMap(), null);
        branchedGit.clone(ProcessOutputStreamConsumer.inMemoryConsumer(), branchedRepo.projectRepositoryUrl());
        Modification mod = branchedGit.latestModification().get(0);
        Assert.assertThat(mod.getUserName(), is("Chris Turner <cturner@thoughtworks.com>"));
        Assert.assertThat(mod.getComment(), is("Started foo branch"));
        Assert.assertThat(mod.getModifiedTime(), is(parseRFC822("Tue, 05 Feb 2009 14:28:08 -0800")));
        Assert.assertThat(mod.getRevision(), is("b4fa7271c3cef91822f7fa502b999b2eab2a380d"));
        List<ModifiedFile> files = mod.getModifiedFiles();
        Assert.assertThat(files.size(), is(1));
        Assert.assertThat(files.get(0).getFileName(), is("first.txt"));
        Assert.assertThat(files.get(0).getAction(), is(modified));
    }

    @Test
    public void shouldRetrieveListOfSubmoduleFolders() throws Exception {
        GitSubmoduleRepos submoduleRepos = new GitSubmoduleRepos(temporaryFolder);
        submoduleRepos.addSubmodule(GitCommandTest.SUBMODULE, "sub1");
        GitCommand gitWithSubmodule = new GitCommand(null, createTempWorkingDirectory(), GitMaterialConfig.DEFAULT_BRANCH, false, new HashMap(), null);
        InMemoryStreamConsumer outputStreamConsumer = ProcessOutputStreamConsumer.inMemoryConsumer();
        gitWithSubmodule.clone(outputStreamConsumer, submoduleRepos.mainRepo().getUrl());
        gitWithSubmodule.fetchAndResetToHead(outputStreamConsumer);
        gitWithSubmodule.updateSubmoduleWithInit(outputStreamConsumer);
        List<String> folders = gitWithSubmodule.submoduleFolders();
        Assert.assertThat(folders.size(), is(1));
        Assert.assertThat(folders.get(0), is("sub1"));
    }

    @Test
    public void shouldNotThrowErrorWhenConfigRemoveSectionFails() throws Exception {
        GitSubmoduleRepos submoduleRepos = new GitSubmoduleRepos(temporaryFolder);
        submoduleRepos.addSubmodule(GitCommandTest.SUBMODULE, "sub1");
        GitCommand gitWithSubmodule = new GitCommand(null, createTempWorkingDirectory(), GitMaterialConfig.DEFAULT_BRANCH, false, new HashMap(), null) {
            // hack to reproduce synchronization issue
            @Override
            public Map<String, String> submoduleUrls() {
                return Collections.singletonMap("submodule", "submodule");
            }
        };
        InMemoryStreamConsumer outputStreamConsumer = ProcessOutputStreamConsumer.inMemoryConsumer();
        gitWithSubmodule.clone(outputStreamConsumer, submoduleRepos.mainRepo().getUrl());
        gitWithSubmodule.updateSubmoduleWithInit(outputStreamConsumer);
    }

    @Test
    public void shouldNotFailIfUnableToRemoveSubmoduleEntryFromConfig() throws Exception {
        GitSubmoduleRepos submoduleRepos = new GitSubmoduleRepos(temporaryFolder);
        submoduleRepos.addSubmodule(GitCommandTest.SUBMODULE, "sub1");
        GitCommand gitWithSubmodule = new GitCommand(null, createTempWorkingDirectory(), GitMaterialConfig.DEFAULT_BRANCH, false, new HashMap(), null);
        InMemoryStreamConsumer outputStreamConsumer = ProcessOutputStreamConsumer.inMemoryConsumer();
        gitWithSubmodule.clone(outputStreamConsumer, submoduleRepos.mainRepo().getUrl());
        gitWithSubmodule.fetchAndResetToHead(outputStreamConsumer);
        gitWithSubmodule.updateSubmoduleWithInit(outputStreamConsumer);
        List<String> folders = gitWithSubmodule.submoduleFolders();
        Assert.assertThat(folders.size(), is(1));
        Assert.assertThat(folders.get(0), is("sub1"));
    }

    @Test
    public void shouldRetrieveSubmoduleUrls() throws Exception {
        GitSubmoduleRepos submoduleRepos = new GitSubmoduleRepos(temporaryFolder);
        submoduleRepos.addSubmodule(GitCommandTest.SUBMODULE, "sub1");
        GitCommand gitWithSubmodule = new GitCommand(null, createTempWorkingDirectory(), GitMaterialConfig.DEFAULT_BRANCH, false, new HashMap(), null);
        InMemoryStreamConsumer outputStreamConsumer = ProcessOutputStreamConsumer.inMemoryConsumer();
        gitWithSubmodule.clone(outputStreamConsumer, submoduleRepos.mainRepo().getUrl());
        gitWithSubmodule.fetchAndResetToHead(outputStreamConsumer);
        gitWithSubmodule.updateSubmoduleWithInit(outputStreamConsumer);
        Map<String, String> urls = gitWithSubmodule.submoduleUrls();
        Assert.assertThat(urls.size(), is(1));
        Assert.assertThat(urls.containsKey("sub1"), is(true));
        Assert.assertThat(urls.get("sub1"), endsWith(GitCommandTest.SUBMODULE));
    }

    @Test
    public void shouldRetrieveZeroSubmoduleUrlsIfTheyAreNotConfigured() throws Exception {
        Map<String, String> submoduleUrls = git.submoduleUrls();
        Assert.assertThat(submoduleUrls.size(), is(0));
    }

    @Test
    public void shouldRetrieveRemoteRepoValue() throws Exception {
        Assert.assertThat(git.workingRepositoryUrl().forCommandline(), startsWith(repoUrl));
    }

    @Test
    public void shouldCheckIfRemoteRepoExists() throws Exception {
        GitCommand gitCommand = new GitCommand(null, null, null, false, null, null);
        final TestSubprocessExecutionContext executionContext = new TestSubprocessExecutionContext();
        gitCommand.checkConnection(git.workingRepositoryUrl(), "master", executionContext.getDefaultEnvironmentVariables());
    }

    @Test(expected = Exception.class)
    public void shouldThrowExceptionWhenRepoNotExist() throws Exception {
        GitCommand gitCommand = new GitCommand(null, null, null, false, null, null);
        final TestSubprocessExecutionContext executionContext = new TestSubprocessExecutionContext();
        gitCommand.checkConnection(new UrlArgument("git://somewhere.is.not.exist"), "master", executionContext.getDefaultEnvironmentVariables());
    }

    @Test(expected = Exception.class)
    public void shouldThrowExceptionWhenRemoteBranchDoesNotExist() throws Exception {
        GitCommand gitCommand = new GitCommand(null, null, null, false, null, null);
        gitCommand.checkConnection(new UrlArgument(gitRepo.projectRepositoryUrl()), "Invalid_Branch", testSubprocessExecutionContext.getDefaultEnvironmentVariables());
    }

    @Test
    public void shouldIncludeNewChangesInModificationCheck() throws Exception {
        String originalNode = git.latestModification().get(0).getRevision();
        File testingFile = checkInNewRemoteFile();
        Modification modification = git.latestModification().get(0);
        Assert.assertThat(modification.getRevision(), is(not(originalNode)));
        Assert.assertThat(modification.getComment(), is(("New checkin of " + (testingFile.getName()))));
        Assert.assertThat(modification.getModifiedFiles().size(), is(1));
        Assert.assertThat(modification.getModifiedFiles().get(0).getFileName(), is(testingFile.getName()));
    }

    @Test
    public void shouldIncludeChangesFromTheFutureInModificationCheck() throws Exception {
        String originalNode = git.latestModification().get(0).getRevision();
        File testingFile = checkInNewRemoteFileInFuture(GitCommandTest.THREE_DAYS_FROM_NOW);
        Modification modification = git.latestModification().get(0);
        Assert.assertThat(modification.getRevision(), is(not(originalNode)));
        Assert.assertThat(modification.getComment(), is(("New checkin of " + (testingFile.getName()))));
        Assert.assertThat(modification.getModifiedTime(), is(GitCommandTest.THREE_DAYS_FROM_NOW));
    }

    @Test
    public void shouldThrowExceptionIfRepoCanNotConnectWhenModificationCheck() throws Exception {
        FileUtils.deleteQuietly(repoLocation);
        try {
            git.latestModification();
            Assert.fail("Should throw exception when repo cannot connected");
        } catch (Exception e) {
            Assert.assertThat(e.getMessage(), anyOf(containsString("The remote end hung up unexpectedly"), containsString("Could not read from remote repository")));
        }
    }

    @Test
    public void shouldParseGitOutputCorrectly() throws IOException {
        List<String> stringList;
        try (InputStream resourceAsStream = getClass().getResourceAsStream("git_sample_output.text")) {
            stringList = IOUtils.readLines(resourceAsStream, StandardCharsets.UTF_8);
        }
        GitModificationParser parser = new GitModificationParser();
        List<Modification> mods = parser.parse(stringList);
        Assert.assertThat(mods.size(), is(3));
        Modification mod = mods.get(2);
        Assert.assertThat(mod.getRevision(), is("46cceff864c830bbeab0a7aaa31707ae2302762f"));
        Assert.assertThat(mod.getModifiedTime(), is(com.thoughtworks.go.util.DateUtils.parseISO8601("2009-08-11 12:37:09 -0700")));
        Assert.assertThat(mod.getUserDisplayName(), is("Cruise Developer <cruise@cruise-sf3.(none)>"));
        Assert.assertThat(mod.getComment(), is(("author:cruise <cceuser@CceDev01.(none)>\n" + ((((((("node:ecfab84dd4953105e3301c5992528c2d381c1b8a\n" + "date:2008-12-31 14:32:40 +0800\n") + "description:Moving rakefile to build subdirectory for #2266\n") + "\n") + "author:CceUser <cceuser@CceDev01.(none)>\n") + "node:fd16efeb70fcdbe63338c49995ce9ff7659e6e77\n") + "date:2008-12-31 14:17:06 +0800\n") + "description:Adding rakefile"))));
    }

    @Test
    public void shouldCleanUnversionedFilesInsideSubmodulesBeforeUpdating() throws Exception {
        GitSubmoduleRepos submoduleRepos = new GitSubmoduleRepos(temporaryFolder);
        String submoduleDirectoryName = "local-submodule";
        submoduleRepos.addSubmodule(GitCommandTest.SUBMODULE, submoduleDirectoryName);
        File cloneDirectory = createTempWorkingDirectory();
        GitCommand clonedCopy = new GitCommand(null, cloneDirectory, GitMaterialConfig.DEFAULT_BRANCH, false, new HashMap(), null);
        InMemoryStreamConsumer outputStreamConsumer = ProcessOutputStreamConsumer.inMemoryConsumer();
        clonedCopy.clone(outputStreamConsumer, submoduleRepos.mainRepo().getUrl());// Clone repository without submodules

        clonedCopy.resetWorkingDir(outputStreamConsumer, new StringRevision("HEAD"));// Pull submodules to working copy - Pipeline counter 1

        File unversionedFile = new File(new File(cloneDirectory, submoduleDirectoryName), "unversioned_file.txt");
        FileUtils.writeStringToFile(unversionedFile, "this is an unversioned file. lets see you deleting me.. come on.. I dare you!!!!", StandardCharsets.UTF_8);
        clonedCopy.resetWorkingDir(outputStreamConsumer, new StringRevision("HEAD"));// Should clean unversioned file on next fetch - Pipeline counter 2

        Assert.assertThat(unversionedFile.exists(), is(false));
    }

    @Test
    public void shouldRemoveChangesToModifiedFilesInsideSubmodulesBeforeUpdating() throws Exception {
        InMemoryStreamConsumer outputStreamConsumer = ProcessOutputStreamConsumer.inMemoryConsumer();
        GitSubmoduleRepos submoduleRepos = new GitSubmoduleRepos(temporaryFolder);
        String submoduleDirectoryName = "local-submodule";
        File cloneDirectory = createTempWorkingDirectory();
        File remoteSubmoduleLocation = submoduleRepos.addSubmodule(GitCommandTest.SUBMODULE, submoduleDirectoryName);
        /* Simulate an agent checkout of code. */
        GitCommand clonedCopy = new GitCommand(null, cloneDirectory, GitMaterialConfig.DEFAULT_BRANCH, false, new HashMap(), null);
        clonedCopy.clone(outputStreamConsumer, submoduleRepos.mainRepo().getUrl());
        clonedCopy.resetWorkingDir(outputStreamConsumer, new StringRevision("HEAD"));
        /* Simulate a local modification of file inside submodule, on agent side. */
        File fileInSubmodule = allFilesIn(new File(cloneDirectory, submoduleDirectoryName), "file-").get(0);
        FileUtils.writeStringToFile(fileInSubmodule, "Some other new content.", StandardCharsets.UTF_8);
        /* Commit a change to the file on the repo. */
        List<Modification> modifications = submoduleRepos.modifyOneFileInSubmoduleAndUpdateMainRepo(remoteSubmoduleLocation, submoduleDirectoryName, fileInSubmodule.getName(), "NEW CONTENT OF FILE");
        /* Simulate start of a new build on agent. */
        clonedCopy.fetch(outputStreamConsumer);
        clonedCopy.resetWorkingDir(outputStreamConsumer, new StringRevision(modifications.get(0).getRevision()));
        Assert.assertThat(FileUtils.readFileToString(fileInSubmodule, StandardCharsets.UTF_8), is("NEW CONTENT OF FILE"));
    }

    @Test
    public void shouldAllowSubmoduleUrlstoChange() throws Exception {
        InMemoryStreamConsumer outputStreamConsumer = ProcessOutputStreamConsumer.inMemoryConsumer();
        GitSubmoduleRepos submoduleRepos = new GitSubmoduleRepos(temporaryFolder);
        String submoduleDirectoryName = "local-submodule";
        File cloneDirectory = createTempWorkingDirectory();
        File remoteSubmoduleLocation = submoduleRepos.addSubmodule(GitCommandTest.SUBMODULE, submoduleDirectoryName);
        GitCommand clonedCopy = new GitCommand(null, cloneDirectory, GitMaterialConfig.DEFAULT_BRANCH, false, new HashMap(), null);
        clonedCopy.clone(outputStreamConsumer, submoduleRepos.mainRepo().getUrl());
        clonedCopy.fetchAndResetToHead(outputStreamConsumer);
        submoduleRepos.changeSubmoduleUrl(submoduleDirectoryName);
        clonedCopy.fetchAndResetToHead(outputStreamConsumer);
    }
}

