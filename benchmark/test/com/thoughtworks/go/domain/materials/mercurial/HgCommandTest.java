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
package com.thoughtworks.go.domain.materials.mercurial;


import com.thoughtworks.go.domain.materials.Modification;
import com.thoughtworks.go.domain.materials.Revision;
import com.thoughtworks.go.util.command.InMemoryStreamConsumer;
import com.thoughtworks.go.util.command.ProcessOutputStreamConsumer;
import com.thoughtworks.go.util.command.UrlArgument;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class HgCommandTest {
    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    private File serverRepo;

    private File clientRepo;

    private HgCommand hgCommand;

    private InMemoryStreamConsumer outputStreamConsumer = ProcessOutputStreamConsumer.inMemoryConsumer();

    private File workingDirectory;

    private static final String REVISION_0 = "b61d12de515d82d3a377ae3aae6e8abe516a2651";

    private static final String REVISION_1 = "35ff2159f303ecf986b3650fc4299a6ffe5a14e1";

    private static final String REVISION_2 = "ca3ebb67f527c0ad7ed26b789056823d8b9af23f";

    private File secondBranchWorkingCopy;

    @Test
    public void shouldCloneFromRemoteRepo() {
        Assert.assertThat(((clientRepo.listFiles().length) > 0), Matchers.is(true));
    }

    @Test
    public void shouldGetLatestModifications() throws Exception {
        List<Modification> actual = hgCommand.latestOneModificationAsModifications();
        Assert.assertThat(actual.size(), Matchers.is(1));
        final Modification modification = actual.get(0);
        Assert.assertThat(modification.getComment(), Matchers.is("test"));
        Assert.assertThat(modification.getUserName(), Matchers.is("cruise"));
        Assert.assertThat(modification.getModifiedFiles().size(), Matchers.is(1));
    }

    @Test
    public void shouldNotIncludeCommitFromAnotherBranchInGetLatestModifications() throws Exception {
        Modification lastCommit = hgCommand.latestOneModificationAsModifications().get(0);
        makeACommitToSecondBranch();
        hg(workingDirectory, "pull").runOrBomb(null);
        Modification actual = hgCommand.latestOneModificationAsModifications().get(0);
        Assert.assertThat(actual, Matchers.is(lastCommit));
        Assert.assertThat(actual.getComment(), Matchers.is(lastCommit.getComment()));
    }

    @Test
    public void shouldGetModifications() throws Exception {
        List<Modification> actual = hgCommand.modificationsSince(new StringRevision(HgCommandTest.REVISION_0));
        Assert.assertThat(actual.size(), Matchers.is(2));
        Assert.assertThat(actual.get(0).getRevision(), Matchers.is(HgCommandTest.REVISION_2));
        Assert.assertThat(actual.get(1).getRevision(), Matchers.is(HgCommandTest.REVISION_1));
    }

    @Test
    public void shouldNotGetModificationsFromOtherBranches() throws Exception {
        makeACommitToSecondBranch();
        hg(workingDirectory, "pull").runOrBomb(null);
        List<Modification> actual = hgCommand.modificationsSince(new StringRevision(HgCommandTest.REVISION_0));
        Assert.assertThat(actual.size(), Matchers.is(2));
        Assert.assertThat(actual.get(0).getRevision(), Matchers.is(HgCommandTest.REVISION_2));
        Assert.assertThat(actual.get(1).getRevision(), Matchers.is(HgCommandTest.REVISION_1));
    }

    @Test
    public void shouldUpdateToSpecificRevision() {
        InMemoryStreamConsumer output = ProcessOutputStreamConsumer.inMemoryConsumer();
        Assert.assertThat(output.getStdOut(), Matchers.is(""));
        File newFile = new File(clientRepo, "test.txt");
        Assert.assertThat(newFile.exists(), Matchers.is(false));
        Revision revision = createNewFileAndCheckIn(serverRepo);
        hgCommand.updateTo(revision, output);
        Assert.assertThat(output.getStdOut(), Matchers.is(Matchers.not("")));
        Assert.assertThat(newFile.exists(), Matchers.is(true));
    }

    @Test
    public void shouldUpdateToSpecificRevisionOnGivenBranch() {
        makeACommitToSecondBranch();
        InMemoryStreamConsumer output = ProcessOutputStreamConsumer.inMemoryConsumer();
        File newFile = new File(workingDirectory, "test.txt");
        hgCommand.updateTo(new StringRevision("tip"), output);
        Assert.assertThat(newFile.exists(), Matchers.is(false));
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowExceptionIfUpdateFails() throws Exception {
        InMemoryStreamConsumer output = ProcessOutputStreamConsumer.inMemoryConsumer();
        // delete repository in order to fail the hg pull command
        Assert.assertThat(FileUtils.deleteQuietly(serverRepo), Matchers.is(true));
        // now hg pull will fail and throw an exception
        hgCommand.updateTo(new StringRevision("tip"), output);
    }

    @Test
    public void shouldGetWorkingUrl() {
        String workingUrl = hgCommand.workingRepositoryUrl().outputAsString();
        Assert.assertThat(workingUrl, Matchers.is(serverRepo.getAbsolutePath()));
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowExceptionForBadConnection() throws Exception {
        String url = "http://not-exists";
        HgCommand hgCommand = new HgCommand(null, null, null, null, null);
        hgCommand.checkConnection(new UrlArgument(url));
    }

    @Test
    public void shouldCloneOnlyTheSpecifiedBranchAndPointToIt() {
        String branchName = "second";
        HgCommand hg = new HgCommand(null, secondBranchWorkingCopy, branchName, serverRepo.getAbsolutePath(), null);
        hg.clone(outputStreamConsumer, new UrlArgument((((serverRepo.getAbsolutePath()) + "#") + branchName)));
        String currentBranch = hg(secondBranchWorkingCopy, "branch").runOrBomb(null).outputAsString();
        Assert.assertThat(currentBranch, Matchers.is(branchName));
        List<String> branches = hg(secondBranchWorkingCopy, "branches").runOrBomb(null).output();
        ArrayList<String> branchNames = new ArrayList<>();
        for (String branchDetails : branches) {
            branchNames.add(StringUtils.split(branchDetails, " ")[0]);
        }
        Assert.assertThat(branchNames.size(), Matchers.is(2));
        Assert.assertThat(branchNames.contains(branchName), Matchers.is(true));
        Assert.assertThat(branchNames.contains("default"), Matchers.is(true));
    }
}

