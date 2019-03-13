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
package com.thoughtworks.go.service;


import ConfigFileFixture.SIMPLE_PIPELINE;
import ConfigFileHasChangedException.CONFIG_CHANGED_PLEASE_REFRESH;
import ConfigRepository.BRANCH_AT_HEAD;
import ConfigRepository.BRANCH_AT_REVISION;
import SystemEnvironment.GO_CONFIG_REPO_PERIODIC_GC;
import com.thoughtworks.go.GoConfigRevisions;
import com.thoughtworks.go.config.exceptions.ConfigMergeException;
import com.thoughtworks.go.domain.GoConfigRevision;
import com.thoughtworks.go.helper.ConfigFileFixture;
import com.thoughtworks.go.util.SystemEnvironment;
import com.thoughtworks.go.util.TimeProvider;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;


public class ConfigRepositoryTest {
    private ConfigRepository configRepo;

    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    private SystemEnvironment systemEnvironment;

    @Test
    public void shouldBeAbleToCheckin() throws Exception {
        configRepo.checkin(new GoConfigRevision("v1", "md5-v1", "user-name", "100.3.9", new TimeProvider()));
        configRepo.checkin(new GoConfigRevision("v1 v2", "md5-v2", "user-name", "100.9.8", new TimeProvider()));
        Assert.assertThat(configRepo.getRevision("md5-v1").getContent(), Matchers.is("v1"));
        Assert.assertThat(configRepo.getRevision("md5-v2").getContent(), Matchers.is("v1 v2"));
    }

    @Test
    public void shouldGetCommitsCorrectly() throws Exception {
        configRepo.checkin(new GoConfigRevision("v1", "md5-v1", "user-name", "100.3.9", new TimeProvider()));
        configRepo.checkin(new GoConfigRevision("v2", "md5-v2", "user-name", "100.3.9", new TimeProvider()));
        configRepo.checkin(new GoConfigRevision("v3", "md5-v3", "user-name", "100.3.9", new TimeProvider()));
        configRepo.checkin(new GoConfigRevision("v4", "md5-v4", "user-name", "100.3.9", new TimeProvider()));
        GoConfigRevisions goConfigRevisions = configRepo.getCommits(3, 0);
        Assert.assertThat(goConfigRevisions.size(), Matchers.is(3));
        Assert.assertThat(goConfigRevisions.get(0).getContent(), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(goConfigRevisions.get(0).getMd5(), Matchers.is("md5-v4"));
        Assert.assertThat(goConfigRevisions.get(1).getMd5(), Matchers.is("md5-v3"));
        Assert.assertThat(goConfigRevisions.get(2).getMd5(), Matchers.is("md5-v2"));
        goConfigRevisions = configRepo.getCommits(3, 3);
        Assert.assertThat(goConfigRevisions.size(), Matchers.is(1));
        Assert.assertThat(goConfigRevisions.get(0).getMd5(), Matchers.is("md5-v1"));
    }

    @Test
    public void shouldFailWhenDoesNotFindARev() throws Exception {
        configRepo.checkin(new GoConfigRevision("v1", "md5-v1", "user-name", "100.3.9", new TimeProvider()));
        try {
            configRepo.getRevision("some-random-revision");
            Assert.fail("should have failed as revision does not exist");
        } catch (RuntimeException e) {
            Assert.assertThat(e.getMessage(), Matchers.is("There is no config version corresponding to md5: 'some-random-revision'"));
        }
    }

    @Test
    public void shouldUnderstandRevision_current_asLatestRevision() throws Exception {
        configRepo.checkin(new GoConfigRevision("v1", "md5-v1", "user-name", "100.3.9", new TimeProvider()));
        configRepo.checkin(new GoConfigRevision("v1 v2", "md5-v2", "user-name", "100.9.8", new TimeProvider()));
        Assert.assertThat(configRepo.getRevision("current").getMd5(), Matchers.is("md5-v2"));
    }

    @Test
    public void shouldReturnNullWhenThereAreNoCheckIns() throws IOException, GitAPIException {
        Assert.assertThat(configRepo.getRevision("current"), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void shouldNotCommitWhenNothingChanged() throws Exception {
        configRepo.checkin(new GoConfigRevision("v1", "md5-v1", "user-name", "100.3.9", new TimeProvider()));
        configRepo.checkin(new GoConfigRevision("v1 v2", "md5-v1", "loser-name", "501.9.8", new TimeProvider()));// md5 is solely trusted

        Iterator<RevCommit> commitIterator = configRepo.revisions().iterator();
        int size = 0;
        while (commitIterator.hasNext()) {
            size++;
            commitIterator.next();
        } 
        Assert.assertThat(size, Matchers.is(1));
    }

    @Test
    public void shouldShowDiffBetweenTwoConsecutiveGitRevisions() throws Exception {
        configRepo.checkin(goConfigRevision(ConfigFileFixture.configWithPipeline(SIMPLE_PIPELINE, 33), "md5-1"));
        RevCommit previousCommit = configRepo.revisions().iterator().next();
        configRepo.checkin(new GoConfigRevision(ConfigFileFixture.configWithPipeline(SIMPLE_PIPELINE, 60), "md5-2", "user-2", "13.2", new TimeProvider()));
        RevCommit latestCommit = configRepo.revisions().iterator().next();
        String configChangesLine1 = "-<cruise schemaVersion='33'>";
        String configChangesLine2 = "+<cruise schemaVersion='60'>";
        String actual = configRepo.findDiffBetweenTwoRevisions(latestCommit, previousCommit);
        Assert.assertThat(actual, Matchers.containsString(configChangesLine1));
        Assert.assertThat(actual, Matchers.containsString(configChangesLine2));
    }

    @Test
    public void shouldShowDiffBetweenAnyTwoGitRevisionsGivenTheirMd5s() throws Exception {
        configRepo.checkin(goConfigRevision(ConfigFileFixture.configWithPipeline(SIMPLE_PIPELINE, 33), "md5-1"));
        configRepo.checkin(new GoConfigRevision(ConfigFileFixture.configWithPipeline(SIMPLE_PIPELINE, 60), "md5-2", "user-2", "13.2", new TimeProvider()));
        configRepo.checkin(new GoConfigRevision(ConfigFileFixture.configWithPipeline(SIMPLE_PIPELINE, 55), "md5-3", "user-1", "13.2", new TimeProvider()));
        String configChangesLine1 = "-<cruise schemaVersion='33'>";
        String configChangesLine2 = "+<cruise schemaVersion='55'>";
        String actual = configRepo.findDiffBetweenTwoRevisions(configRepo.getRevCommitForMd5("md5-3"), configRepo.getRevCommitForMd5("md5-1"));
        Assert.assertThat(actual, Matchers.containsString(configChangesLine1));
        Assert.assertThat(actual, Matchers.containsString(configChangesLine2));
    }

    @Test
    public void shouldReturnNullForFirstCommit() throws Exception {
        configRepo.checkin(goConfigRevision("something", "md5-1"));
        RevCommit firstCommit = configRepo.revisions().iterator().next();
        String actual = configRepo.findDiffBetweenTwoRevisions(firstCommit, null);
        Assert.assertThat(actual, Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void shouldShowDiffForAnyTwoConfigMd5s() throws Exception {
        configRepo.checkin(goConfigRevision(ConfigFileFixture.configWithPipeline(SIMPLE_PIPELINE, 33), "md5-1"));
        configRepo.checkin(new GoConfigRevision(ConfigFileFixture.configWithPipeline(SIMPLE_PIPELINE, 60), "md5-2", "user-2", "13.2", new TimeProvider()));
        configRepo.checkin(new GoConfigRevision(ConfigFileFixture.configWithPipeline(SIMPLE_PIPELINE, 55), "md5-3", "user-2", "13.2", new TimeProvider()));
        String configChangesLine1 = "-<cruise schemaVersion='33'>";
        String configChangesLine2 = "+<cruise schemaVersion='60'>";
        String configChangesLine3 = "+<cruise schemaVersion='55'>";
        String actual = configRepo.configChangesFor("md5-2", "md5-1");
        Assert.assertThat(actual, Matchers.containsString(configChangesLine1));
        Assert.assertThat(actual, Matchers.containsString(configChangesLine2));
        actual = configRepo.configChangesFor("md5-3", "md5-1");
        Assert.assertThat(actual, Matchers.containsString(configChangesLine1));
        Assert.assertThat(actual, Matchers.containsString(configChangesLine3));
    }

    @Test
    public void shouldShowDiffForAnyTwoCommitSHAs() throws Exception {
        configRepo.checkin(goConfigRevision(ConfigFileFixture.configWithPipeline(SIMPLE_PIPELINE, 33), "md5-1"));
        configRepo.checkin(new GoConfigRevision(ConfigFileFixture.configWithPipeline(SIMPLE_PIPELINE, 60), "md5-2", "user-2", "13.2", new TimeProvider()));
        configRepo.checkin(new GoConfigRevision(ConfigFileFixture.configWithPipeline(SIMPLE_PIPELINE, 55), "md5-3", "user-2", "13.2", new TimeProvider()));
        GoConfigRevisions commits = configRepo.getCommits(10, 0);
        String firstCommitSHA = commits.get(2).getCommitSHA();
        String secondCommitSHA = commits.get(1).getCommitSHA();
        String thirdCommitSHA = commits.get(0).getCommitSHA();
        String configChangesLine1 = "-<cruise schemaVersion='33'>";
        String configChangesLine2 = "+<cruise schemaVersion='60'>";
        String configChangesLine3 = "+<cruise schemaVersion='55'>";
        String actual = configRepo.configChangesForCommits(secondCommitSHA, firstCommitSHA);
        Assert.assertThat(actual, Matchers.containsString(configChangesLine1));
        Assert.assertThat(actual, Matchers.containsString(configChangesLine2));
        actual = configRepo.configChangesForCommits(thirdCommitSHA, firstCommitSHA);
        Assert.assertThat(actual, Matchers.containsString(configChangesLine1));
        Assert.assertThat(actual, Matchers.containsString(configChangesLine3));
    }

    @Test
    public void shouldRemoveUnwantedDataFromDiff() throws Exception {
        configRepo.checkin(goConfigRevision(ConfigFileFixture.configWithPipeline(SIMPLE_PIPELINE, 33), "md5-1"));
        String configXml = ConfigFileFixture.configWithPipeline(SIMPLE_PIPELINE, 60);
        configRepo.checkin(new GoConfigRevision(configXml, "md5-2", "user-2", "13.2", new TimeProvider()));
        String configChangesLine1 = "-<cruise schemaVersion='33'>";
        String configChangesLine2 = "+<cruise schemaVersion='60'>";
        String actual = configRepo.configChangesFor("md5-2", "md5-1");
        Assert.assertThat(actual, Matchers.containsString(configChangesLine1));
        Assert.assertThat(actual, Matchers.containsString(configChangesLine2));
        Assert.assertThat(actual, Matchers.not(Matchers.containsString("--- a/cruise-config.xml")));
        Assert.assertThat(actual, Matchers.not(Matchers.containsString("+++ b/cruise-config.xml")));
    }

    @Test
    public void shouldThrowExceptionIfRevisionNotFound() throws Exception {
        configRepo.checkin(goConfigRevision("v1", "md5-1"));
        try {
            configRepo.configChangesFor("md5-1", "md5-not-found");
            Assert.fail("Should have failed");
        } catch (IllegalArgumentException e) {
            Assert.assertThat(e.getMessage(), Matchers.is("There is no config version corresponding to md5: 'md5-not-found'"));
        }
        try {
            configRepo.configChangesFor("md5-not-found", "md5-1");
            Assert.fail("Should have failed");
        } catch (IllegalArgumentException e) {
            Assert.assertThat(e.getMessage(), Matchers.is("There is no config version corresponding to md5: 'md5-not-found'"));
        }
    }

    @Test
    public void shouldCreateBranchForARevCommit() throws Exception {
        configRepo.checkin(goConfigRevision("something", "md5-1"));
        RevCommit revCommit = configRepo.getCurrentRevCommit();
        configRepo.createBranch("branch1", revCommit);
        Ref branch = getBranch("branch1");
        Assert.assertThat(branch, Matchers.is(Matchers.notNullValue()));
        Assert.assertThat(branch.getObjectId(), Matchers.is(revCommit.getId()));
    }

    @Test
    public void shouldCommitIntoGivenBranch() throws Exception {
        configRepo.checkin(goConfigRevision("something", "md5-1"));
        RevCommit revCommitOnMaster = configRepo.getCurrentRevCommit();
        String branchName = "branch1";
        configRepo.createBranch(branchName, revCommitOnMaster);
        String newConfigXML = "config-xml";
        GoConfigRevision configRevision = new GoConfigRevision(newConfigXML, "md5", "user", "version", new TimeProvider());
        RevCommit branchRevCommit = configRepo.checkinToBranch(branchName, configRevision);
        Assert.assertThat(branchRevCommit, Matchers.is(Matchers.notNullValue()));
        Assert.assertThat(getLatestConfigAt(branchName), Matchers.is(newConfigXML));
        Assert.assertThat(configRepo.getCurrentRevCommit(), Matchers.is(revCommitOnMaster));
    }

    @Test
    public void shouldMergeNewCommitOnBranchWithHeadWhenThereIsNoConflict() throws Exception {
        String original = "first\nsecond\n";
        String changeOnBranch = "first\nsecond\nthird\n";
        String changeOnMaster = "1st\nsecond\n";
        String oldMd5 = "md5-1";
        configRepo.checkin(goConfigRevision(original, oldMd5));
        configRepo.checkin(goConfigRevision(changeOnMaster, "md5-2"));
        String mergedConfig = configRepo.getConfigMergedWithLatestRevision(goConfigRevision(changeOnBranch, "md5-3"), oldMd5);
        Assert.assertThat(mergedConfig, Matchers.is("1st\nsecond\nthird\n"));
    }

    @Test
    public void shouldThrowExceptionWhenThereIsMergeConflict() throws Exception {
        String original = "first\nsecond\n";
        String nextUpdate = "1st\nsecond\n";
        String latestUpdate = "2nd\nsecond\n";
        configRepo.checkin(goConfigRevision(original, "md5-1"));
        configRepo.checkin(goConfigRevision(nextUpdate, "md5-2"));
        RevCommit currentRevCommitOnMaster = configRepo.getCurrentRevCommit();
        try {
            configRepo.getConfigMergedWithLatestRevision(goConfigRevision(latestUpdate, "md5-3"), "md5-1");
            Assert.fail("should have bombed for merge conflict");
        } catch (ConfigMergeException e) {
            Assert.assertThat(e.getMessage(), Matchers.is(CONFIG_CHANGED_PLEASE_REFRESH));
        }
        List<Ref> branches = getAllBranches();
        Assert.assertThat(branches.size(), Matchers.is(1));
        Assert.assertThat(branches.get(0).getName().endsWith("master"), Matchers.is(true));
        Assert.assertThat(configRepo.getCurrentRevCommit(), Matchers.is(currentRevCommitOnMaster));
    }

    @Test
    public void shouldBeOnMasterAndTemporaryBranchesDeletedAfterGettingMergeConfig() throws Exception {
        String original = "first\nsecond\n";
        String nextUpdate = "1st\nsecond\n";
        String latestUpdate = "first\nsecond\nthird\n";
        configRepo.checkin(goConfigRevision(original, "md5-1"));
        configRepo.checkin(goConfigRevision(nextUpdate, "md5-2"));
        RevCommit currentRevCommitOnMaster = configRepo.getCurrentRevCommit();
        String mergedConfig = configRepo.getConfigMergedWithLatestRevision(goConfigRevision(latestUpdate, "md5-3"), "md5-1");
        Assert.assertThat(mergedConfig, Matchers.is("1st\nsecond\nthird\n"));
        List<Ref> branches = getAllBranches();
        Assert.assertThat(branches.size(), Matchers.is(1));
        Assert.assertThat(branches.get(0).getName().endsWith("master"), Matchers.is(true));
        Assert.assertThat(configRepo.getCurrentRevCommit(), Matchers.is(currentRevCommitOnMaster));
    }

    @Test
    public void shouldSwitchToMasterAndDeleteTempBranches() throws Exception, GitAPIException {
        configRepo.checkin(goConfigRevision("v1", "md5-1"));
        configRepo.createBranch(BRANCH_AT_HEAD, configRepo.getCurrentRevCommit());
        configRepo.createBranch(BRANCH_AT_REVISION, configRepo.getCurrentRevCommit());
        configRepo.git().checkout().setName(BRANCH_AT_REVISION).call();
        Assert.assertThat(configRepo.git().getRepository().getBranch(), Matchers.is(BRANCH_AT_REVISION));
        Assert.assertThat(configRepo.git().branchList().call().size(), Matchers.is(3));
        configRepo.cleanAndResetToMaster();
        Assert.assertThat(configRepo.git().getRepository().getBranch(), Matchers.is("master"));
        Assert.assertThat(configRepo.git().branchList().call().size(), Matchers.is(1));
    }

    @Test
    public void shouldCleanAndResetToMasterDuringInitialization() throws Exception {
        configRepo.checkin(goConfigRevision("v1", "md5-1"));
        configRepo.createBranch(BRANCH_AT_REVISION, configRepo.getCurrentRevCommit());
        configRepo.git().checkout().setName(BRANCH_AT_REVISION).call();
        Assert.assertThat(configRepo.git().getRepository().getBranch(), Matchers.is(BRANCH_AT_REVISION));
        initialize();
        Assert.assertThat(configRepo.git().getRepository().getBranch(), Matchers.is("master"));
        Assert.assertThat(configRepo.git().branchList().call().size(), Matchers.is(1));
    }

    @Test
    public void shouldCleanAndResetToMasterOnceMergeFlowIsComplete() throws Exception {
        String original = "first\nsecond\n";
        String changeOnBranch = "first\nsecond\nthird\n";
        String changeOnMaster = "1st\nsecond\n";
        String oldMd5 = "md5-1";
        configRepo.checkin(goConfigRevision(original, oldMd5));
        configRepo.checkin(goConfigRevision(changeOnMaster, "md5-2"));
        configRepo.getConfigMergedWithLatestRevision(goConfigRevision(changeOnBranch, "md5-3"), oldMd5);
        Assert.assertThat(configRepo.git().getRepository().getBranch(), Matchers.is("master"));
        Assert.assertThat(configRepo.git().branchList().call().size(), Matchers.is(1));
    }

    @Test
    public void shouldPerformGC() throws Exception {
        configRepo.checkin(goConfigRevision("v1", "md5-1"));
        Long numberOfLooseObjects = ((Long) (configRepo.git().gc().getStatistics().get("sizeOfLooseObjects")));
        Assert.assertThat((numberOfLooseObjects > 0L), Matchers.is(true));
        configRepo.garbageCollect();
        numberOfLooseObjects = ((Long) (configRepo.git().gc().getStatistics().get("sizeOfLooseObjects")));
        Assert.assertThat(numberOfLooseObjects, Matchers.is(0L));
    }

    @Test
    public void shouldNotPerformGCWhenPeriodicGCIsTurnedOff() throws Exception {
        Mockito.when(systemEnvironment.get(GO_CONFIG_REPO_PERIODIC_GC)).thenReturn(false);
        configRepo.checkin(goConfigRevision("v1", "md5-1"));
        Long numberOfLooseObjectsOld = ((Long) (configRepo.git().gc().getStatistics().get("sizeOfLooseObjects")));
        configRepo.garbageCollect();
        Long numberOfLooseObjectsNow = ((Long) (configRepo.git().gc().getStatistics().get("sizeOfLooseObjects")));
        Assert.assertThat(numberOfLooseObjectsNow, Matchers.is(numberOfLooseObjectsOld));
    }

    @Test
    public void shouldGetLooseObjectCount() throws Exception {
        configRepo.checkin(goConfigRevision("v1", "md5-1"));
        Long numberOfLooseObjects = ((Long) (configRepo.git().gc().getStatistics().get("numberOfLooseObjects")));
        Assert.assertThat(configRepo.getLooseObjectCount(), Matchers.is(numberOfLooseObjects));
    }

    @Test
    public void shouldReturnNumberOfCommitsOnMaster() throws Exception {
        configRepo.checkin(goConfigRevision("v1", "md5-1"));
        configRepo.checkin(goConfigRevision("v2", "md5-2"));
        Assert.assertThat(configRepo.commitCountOnMaster(), Matchers.is(2L));
    }
}

