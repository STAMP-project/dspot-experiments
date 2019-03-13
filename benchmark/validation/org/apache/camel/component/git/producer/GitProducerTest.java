/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.git.producer;


import GitConstants.GIT_COMMIT_ID;
import GitConstants.GIT_COMMIT_MESSAGE;
import GitConstants.GIT_FILE_NAME;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.apache.camel.CamelExecutionException;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.git.GitTestSupport;
import org.eclipse.jgit.api.CreateBranchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.RemoteAddCommand;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.URIish;
import org.junit.Test;

import static org.eclipse.jgit.api.CreateBranchCommand.SetupUpstreamMode.SET_UPSTREAM;


public class GitProducerTest extends GitTestSupport {
    @Test
    public void cloneTest() throws Exception {
        template.sendBody("direct:clone", "");
        File gitDir = new File(gitLocalRepo, ".git");
        assertEquals(gitDir.exists(), true);
    }

    @Test
    public void initTest() throws Exception {
        template.sendBody("direct:init", "");
        File gitDir = new File(gitLocalRepo, ".git");
        assertEquals(gitDir.exists(), true);
    }

    @Test
    public void checkoutTest() throws Exception {
        // Init
        Git git = getGitTestRepository();
        File gitDir = new File(gitLocalRepo, ".git");
        assertEquals(gitDir.exists(), true);
        File fileToAdd = new File(gitLocalRepo, filenameToAdd);
        fileToAdd.createNewFile();
        git.add().addFilepattern(filenameToAdd).call();
        Status status = git.status().call();
        assertTrue(status.getAdded().contains(filenameToAdd));
        git.commit().setMessage(commitMessage).call();
        // Test camel-git checkout
        template.sendBody("direct:checkout", "");
        // Check
        List<Ref> ref = git.branchList().call();
        boolean branchCreated = false;
        for (Ref refInternal : ref) {
            if (refInternal.getName().equals(("refs/heads/" + (branchTest)))) {
                branchCreated = true;
            }
        }
        assertEquals(branchCreated, true);
        git.close();
    }

    @Test
    public void checkoutSpecificTagTest() throws Exception {
        // Init
        Git git = getGitTestRepository();
        File fileToAdd = new File(gitLocalRepo, filenameToAdd);
        fileToAdd.createNewFile();
        git.add().addFilepattern(filenameToAdd).call();
        File gitDir = new File(gitLocalRepo, ".git");
        assertEquals(gitDir.exists(), true);
        Status status = git.status().call();
        assertTrue(status.getAdded().contains(filenameToAdd));
        git.commit().setMessage(commitMessage).call();
        // Test camel-git create tag
        template.sendBody("direct:create-tag", "");
        // Check
        List<Ref> ref = git.tagList().call();
        boolean tagCreated = false;
        for (Ref refInternal : ref) {
            if (refInternal.getName().equals(("refs/tags/" + (tagTest)))) {
                tagCreated = true;
            }
        }
        assertEquals(tagCreated, true);
        // Test camel-git create-branch
        template.sendBody("direct:checkout-specific-tag", "");
        // Check
        ref = git.branchList().call();
        boolean branchCreated = false;
        for (Ref refInternal : ref) {
            if (refInternal.getName().equals(("refs/heads/" + (branchTest)))) {
                branchCreated = true;
            }
        }
        assertEquals(branchCreated, true);
        git.close();
    }

    @Test(expected = CamelExecutionException.class)
    public void doubleCloneOperationTest() throws Exception {
        template.sendBody("direct:clone", "");
        template.sendBody("direct:clone", "");
        File gitDir = new File(gitLocalRepo, ".git");
        assertEquals(gitDir.exists(), true);
    }

    @Test
    public void pullTest() throws Exception {
        template.sendBody("direct:clone", "");
        File gitDir = new File(gitLocalRepo, ".git");
        assertEquals(gitDir.exists(), true);
        PullResult pr = template.requestBody("direct:pull", "", PullResult.class);
        assertTrue(pr.isSuccessful());
    }

    @Test
    public void addTest() throws Exception {
        Git git = getGitTestRepository();
        File fileToAdd = new File(gitLocalRepo, filenameToAdd);
        fileToAdd.createNewFile();
        // Test camel-git add
        template.sendBodyAndHeader("direct:add", "", GIT_FILE_NAME, filenameToAdd);
        File gitDir = new File(gitLocalRepo, ".git");
        assertEquals(gitDir.exists(), true);
        Status status = git.status().call();
        assertTrue(status.getAdded().contains(filenameToAdd));
        git.close();
    }

    @Test
    public void removeTest() throws Exception {
        // Init
        Git git = getGitTestRepository();
        File gitDir = new File(gitLocalRepo, ".git");
        assertEquals(gitDir.exists(), true);
        File fileToAdd = new File(gitLocalRepo, filenameToAdd);
        fileToAdd.createNewFile();
        git.add().addFilepattern(filenameToAdd).call();
        Status status = git.status().call();
        assertTrue(status.getAdded().contains(filenameToAdd));
        // Test camel-git remove
        template.sendBodyAndHeader("direct:remove", "", GIT_FILE_NAME, filenameToAdd);
        // Check
        gitDir = new File(gitLocalRepo, ".git");
        assertEquals(gitDir.exists(), true);
        git.commit().setMessage(commitMessage).call();
        validateGitLogs(git, commitMessage);
        status = git.status().call();
        assertFalse(status.getAdded().contains(filenameToAdd));
        git.close();
    }

    @Test
    public void commitTest() throws Exception {
        // Init
        Git git = getGitTestRepository();
        File fileToAdd = new File(gitLocalRepo, filenameToAdd);
        fileToAdd.createNewFile();
        git.add().addFilepattern(filenameToAdd).call();
        Status status = git.status().call();
        assertTrue(status.getAdded().contains(filenameToAdd));
        // Test camel-git commit
        template.sendBodyAndHeader("direct:commit", "", GIT_COMMIT_MESSAGE, commitMessage);
        // Check
        validateGitLogs(git, commitMessage);
        git.close();
    }

    @Test
    public void commitTestEmpty() throws Exception {
        // Init
        Git git = getGitTestRepository();
        File gitDir = new File(gitLocalRepo, ".git");
        assertEquals(gitDir.exists(), true);
        File fileToAdd = new File(gitLocalRepo, filenameToAdd);
        fileToAdd.createNewFile();
        git.add().addFilepattern(filenameToAdd).call();
        Status status = git.status().call();
        assertTrue(status.getAdded().contains(filenameToAdd));
        git.commit().setMessage(commitMessage).call();
        // Test camel-git commit (with no changes)
        template.requestBodyAndHeader("direct:commit", "", GIT_COMMIT_MESSAGE, commitMessage);
        // Check that it has been commited twice
        validateGitLogs(git, commitMessage, commitMessage);
        git.close();
    }

    @Test(expected = CamelExecutionException.class)
    public void commitTestAllowEmptyFalse() throws Exception {
        // Init
        Git git = getGitTestRepository();
        File gitDir = new File(gitLocalRepo, ".git");
        assertEquals(gitDir.exists(), true);
        File fileToAdd = new File(gitLocalRepo, filenameToAdd);
        fileToAdd.createNewFile();
        git.add().addFilepattern(filenameToAdd).call();
        Status status = git.status().call();
        assertTrue(status.getAdded().contains(filenameToAdd));
        git.commit().setMessage(commitMessage).call();
        // Test camel-git commit (with allowEmpty set to false)
        Map<String, Object> headers = new HashMap<>();
        headers.put(GIT_COMMIT_MESSAGE, commitMessage);
        template.requestBodyAndHeaders("direct:commit-not-allow-empty", "", headers);
        // Check : An exception should have been raised
    }

    @Test
    public void addAndStatusAndCommitTest() throws Exception {
        // Initialize repository using JGit
        Repository repository = getTestRepository();
        File gitDir = new File(gitLocalRepo, ".git");
        assertEquals(gitDir.exists(), true);
        Git git = new Git(repository);
        File fileToAdd = new File(gitLocalRepo, filenameToAdd);
        fileToAdd.createNewFile();
        // Checking camel route
        Map<String, Object> headers = new HashMap<>();
        headers.put(GIT_FILE_NAME, filenameToAdd);
        headers.put(GIT_COMMIT_MESSAGE, commitMessage);
        template.requestBodyAndHeaders("direct:add-status-commit", "", headers);
        validateGitLogs(git, commitMessage);
        git.close();
    }

    @Test
    public void commitBranchTest() throws Exception {
        // Init
        Git git = getGitTestRepository();
        File gitDir = new File(gitLocalRepo, ".git");
        assertEquals(gitDir.exists(), true);
        File fileToAdd = new File(gitLocalRepo, filenameToAdd);
        fileToAdd.createNewFile();
        git.add().addFilepattern(filenameToAdd).call();
        Status status = git.status().call();
        assertTrue(status.getAdded().contains(filenameToAdd));
        git.commit().setMessage(commitMessage).call();
        validateGitLogs(git, commitMessage);
        git.checkout().setCreateBranch(true).setName(branchTest).setUpstreamMode(SET_UPSTREAM).call();
        // Test camel-git commit (with branch)
        template.send("direct:commit-branch", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(GIT_COMMIT_MESSAGE, commitMessageBranch);
            }
        });
        validateGitLogs(git, commitMessageBranch, commitMessage);
        git.close();
    }

    @Test
    public void commitAllTest() throws Exception {
        Git git = getGitTestRepository();
        File fileToAdd = new File(gitLocalRepo, filenameToAdd);
        fileToAdd.createNewFile();
        git.add().addFilepattern(filenameToAdd).call();
        template.send("direct:commit-all", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(GIT_COMMIT_MESSAGE, commitMessageAll);
            }
        });
        validateGitLogs(git, commitMessageAll);
        git.close();
    }

    @Test
    public void commitAllDifferentBranchTest() throws Exception {
        // Init
        Git git = getGitTestRepository();
        File gitDir = new File(gitLocalRepo, ".git");
        assertEquals(gitDir.exists(), true);
        File fileToAdd = new File(gitLocalRepo, filenameToAdd);
        fileToAdd.createNewFile();
        git.add().addFilepattern(filenameToAdd).call();
        Status status = git.status().call();
        assertTrue(status.getAdded().contains(filenameToAdd));
        git.commit().setMessage(commitMessage).call();
        validateGitLogs(git, commitMessage);
        git.checkout().setCreateBranch(true).setName(branchTest).setUpstreamMode(SET_UPSTREAM).call();
        File fileToAdd1 = new File(gitLocalRepo, filenameBranchToAdd);
        fileToAdd1.createNewFile();
        // Test camel-git add and commit (different branches)
        template.send("direct:add-on-branch", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(GIT_FILE_NAME, filenameBranchToAdd);
            }
        });
        template.send("direct:commit-all-branch", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(GIT_COMMIT_MESSAGE, commitMessageAll);
            }
        });
        // Check
        validateGitLogs(git, commitMessageAll, commitMessage);
        git.close();
    }

    @Test
    public void removeFileBranchTest() throws Exception {
        // Init
        Git git = getGitTestRepository();
        File gitDir = new File(gitLocalRepo, ".git");
        assertEquals(gitDir.exists(), true);
        File fileToAdd = new File(gitLocalRepo, filenameToAdd);
        fileToAdd.createNewFile();
        git.add().addFilepattern(filenameToAdd).call();
        Status status = git.status().call();
        assertTrue(status.getAdded().contains(filenameToAdd));
        git.commit().setMessage(commitMessage).call();
        validateGitLogs(git, commitMessage);
        git.checkout().setCreateBranch(true).setName(branchTest).setUpstreamMode(SET_UPSTREAM).call();
        File fileToAdd1 = new File(gitLocalRepo, filenameBranchToAdd);
        fileToAdd1.createNewFile();
        git.add().addFilepattern(filenameBranchToAdd).call();
        git.commit().setMessage(commitMessageAll).call();
        validateGitLogs(git, commitMessageAll, commitMessage);
        // Test camel-git remove
        template.send("direct:remove-on-branch", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(GIT_FILE_NAME, filenameToAdd);
            }
        });
        // Check
        git.checkout().setCreateBranch(false).setName(branchTest).call();
        status = git.status().call();
        assertFalse(status.getAdded().contains(filenameToAdd));
        git.close();
    }

    @Test
    public void createBranchTest() throws Exception {
        // Init
        Git git = getGitTestRepository();
        File gitDir = new File(gitLocalRepo, ".git");
        assertEquals(gitDir.exists(), true);
        File fileToAdd = new File(gitLocalRepo, filenameToAdd);
        fileToAdd.createNewFile();
        git.add().addFilepattern(filenameToAdd).call();
        Status status = git.status().call();
        assertTrue(status.getAdded().contains(filenameToAdd));
        git.commit().setMessage(commitMessage).call();
        // Test camel-git create-branch
        template.sendBody("direct:create-branch", "");
        // Check
        List<Ref> ref = git.branchList().call();
        boolean branchCreated = false;
        for (Ref refInternal : ref) {
            if (refInternal.getName().equals(("refs/heads/" + (branchTest)))) {
                branchCreated = true;
            }
        }
        assertEquals(branchCreated, true);
        git.close();
    }

    @Test
    public void deleteBranchTest() throws Exception {
        // Init
        Git git = getGitTestRepository();
        File fileToAdd = new File(gitLocalRepo, filenameToAdd);
        fileToAdd.createNewFile();
        git.add().addFilepattern(filenameToAdd).call();
        File gitDir = new File(gitLocalRepo, ".git");
        assertEquals(gitDir.exists(), true);
        Status status = git.status().call();
        assertTrue(status.getAdded().contains(filenameToAdd));
        git.commit().setMessage(commitMessage).call();
        git.branchCreate().setName(branchTest).call();
        List<Ref> ref = git.branchList().call();
        boolean branchCreated = false;
        for (Ref refInternal : ref) {
            if (refInternal.getName().equals(("refs/heads/" + (branchTest)))) {
                branchCreated = true;
            }
        }
        assertEquals(branchCreated, true);
        // Test camel-git delete-branch
        template.sendBody("direct:delete-branch", "");
        ref = git.branchList().call();
        branchCreated = false;
        for (Ref refInternal : ref) {
            if (refInternal.getName().equals(("refs/heads/" + (branchTest)))) {
                branchCreated = true;
            }
        }
        assertEquals(branchCreated, false);
        git.close();
    }

    @Test
    public void statusTest() throws Exception {
        // Init
        Git git = getGitTestRepository();
        File fileToAdd = new File(gitLocalRepo, filenameToAdd);
        fileToAdd.createNewFile();
        git.add().addFilepattern(filenameToAdd).call();
        File gitDir = new File(gitLocalRepo, ".git");
        assertEquals(gitDir.exists(), true);
        // Test camel-git status
        Status status = template.requestBody("direct:status", "", Status.class);
        // Check
        assertTrue(status.getAdded().contains(filenameToAdd));
        Status gitStatus = git.status().call();
        assertEquals(gitStatus.getAdded(), status.getAdded());
        git.close();
    }

    @Test
    public void statusBranchTest() throws Exception {
        // Init
        Git git = getGitTestRepository();
        File fileToAdd = new File(gitLocalRepo, filenameToAdd);
        fileToAdd.createNewFile();
        git.add().addFilepattern(filenameToAdd).call();
        File gitDir = new File(gitLocalRepo, ".git");
        assertEquals(gitDir.exists(), true);
        Status status = git.status().call();
        assertTrue(status.getAdded().contains(filenameToAdd));
        git.commit().setMessage(commitMessage).call();
        git.branchCreate().setName(branchTest).call();
        List<Ref> ref = git.branchList().call();
        boolean branchCreated = false;
        for (Ref refInternal : ref) {
            if (refInternal.getName().equals(("refs/heads/" + (branchTest)))) {
                branchCreated = true;
            }
        }
        assertEquals(branchCreated, true);
        File fileToAddDifferent = new File(gitLocalRepo, filenameBranchToAdd);
        fileToAddDifferent.createNewFile();
        git.add().addFilepattern(filenameBranchToAdd).call();
        // Test camel-git status branch
        status = template.requestBody("direct:status-branch", "", Status.class);
        // Check
        assertTrue(status.getAdded().contains(filenameBranchToAdd));
        Status gitStatus = git.status().call();
        assertEquals(gitStatus.getAdded(), status.getAdded());
        git.close();
    }

    @Test
    public void logTest() throws Exception {
        // Init
        Git git = getGitTestRepository();
        File fileToAdd = new File(gitLocalRepo, filenameToAdd);
        fileToAdd.createNewFile();
        git.add().addFilepattern(filenameToAdd).call();
        File gitDir = new File(gitLocalRepo, ".git");
        assertEquals(gitDir.exists(), true);
        Status status = git.status().call();
        assertTrue(status.getAdded().contains(filenameToAdd));
        git.commit().setMessage(commitMessage).call();
        // Test camel-git log
        Iterable<RevCommit> revCommits = template.requestBody("direct:log", "", Iterable.class);
        // Check
        Iterator<RevCommit> gitLogs = git.log().call().iterator();
        for (RevCommit rev : revCommits) {
            RevCommit gitRevCommit = gitLogs.next();
            assertEquals(gitRevCommit.getName(), rev.getName());
        }
        git.close();
    }

    @Test
    public void logBranchTest() throws Exception {
        // Init
        Git git = getGitTestRepository();
        File fileToAdd = new File(gitLocalRepo, filenameToAdd);
        fileToAdd.createNewFile();
        git.add().addFilepattern(filenameToAdd).call();
        File gitDir = new File(gitLocalRepo, ".git");
        assertEquals(gitDir.exists(), true);
        Status status = git.status().call();
        assertTrue(status.getAdded().contains(filenameToAdd));
        git.commit().setMessage(commitMessage).call();
        git.branchCreate().setName(branchTest).call();
        List<Ref> ref = git.branchList().call();
        boolean branchCreated = false;
        for (Ref refInternal : ref) {
            if (refInternal.getName().equals(("refs/heads/" + (branchTest)))) {
                branchCreated = true;
            }
        }
        assertEquals(branchCreated, true);
        File fileToAddDifferent = new File(gitLocalRepo, filenameBranchToAdd);
        fileToAddDifferent.createNewFile();
        git.add().addFilepattern(filenameBranchToAdd).call();
        git.commit().setMessage(commitMessageAll).call();
        // Test camel-git log (with branches)
        Iterable<RevCommit> revCommits = template.requestBody("direct:log-branch", "", Iterable.class);
        // Check
        Iterator<RevCommit> gitLogs = git.log().call().iterator();
        for (RevCommit rev : revCommits) {
            RevCommit gitRevCommit = gitLogs.next();
            assertEquals(gitRevCommit.getName(), rev.getName());
            assertEquals(gitRevCommit.getShortMessage(), rev.getShortMessage());
        }
        git.close();
    }

    @Test
    public void createTagTest() throws Exception {
        // Init
        Git git = getGitTestRepository();
        File fileToAdd = new File(gitLocalRepo, filenameToAdd);
        fileToAdd.createNewFile();
        git.add().addFilepattern(filenameToAdd).call();
        File gitDir = new File(gitLocalRepo, ".git");
        assertEquals(gitDir.exists(), true);
        Status status = git.status().call();
        assertTrue(status.getAdded().contains(filenameToAdd));
        git.commit().setMessage(commitMessage).call();
        // Test camel-git create tag
        template.sendBody("direct:create-tag", "");
        // Check
        List<Ref> ref = git.tagList().call();
        boolean tagCreated = false;
        for (Ref refInternal : ref) {
            if (refInternal.getName().equals(("refs/tags/" + (tagTest)))) {
                tagCreated = true;
            }
        }
        assertEquals(tagCreated, true);
        git.close();
    }

    @Test
    public void deleteTagTest() throws Exception {
        // Init
        Git git = getGitTestRepository();
        File fileToAdd = new File(gitLocalRepo, filenameToAdd);
        fileToAdd.createNewFile();
        git.add().addFilepattern(filenameToAdd).call();
        File gitDir = new File(gitLocalRepo, ".git");
        assertEquals(gitDir.exists(), true);
        Status status = git.status().call();
        assertTrue(status.getAdded().contains(filenameToAdd));
        git.commit().setMessage(commitMessage).call();
        git.tag().setName(tagTest).call();
        List<Ref> ref = git.tagList().call();
        boolean tagCreated = false;
        for (Ref refInternal : ref) {
            if (refInternal.getName().equals(("refs/tags/" + (tagTest)))) {
                tagCreated = true;
            }
        }
        assertEquals(tagCreated, true);
        // Test camel-git delete-tag
        template.sendBody("direct:delete-tag", "");
        // Check
        ref = git.tagList().call();
        boolean tagExists = false;
        for (Ref refInternal : ref) {
            if (refInternal.getName().equals(("refs/tags/" + (tagTest)))) {
                tagExists = true;
            }
        }
        assertEquals(tagExists, false);
        git.close();
    }

    @Test
    public void showBranchesTest() throws Exception {
        // Init
        Git git = getGitTestRepository();
        File fileToAdd = new File(gitLocalRepo, filenameToAdd);
        fileToAdd.createNewFile();
        git.add().addFilepattern(filenameToAdd).call();
        File gitDir = new File(gitLocalRepo, ".git");
        assertEquals(gitDir.exists(), true);
        Status status = git.status().call();
        assertTrue(status.getAdded().contains(filenameToAdd));
        git.commit().setMessage(commitMessage).call();
        git.branchCreate().setName(branchTest).call();
        // Test camel-git show-branches
        List<Ref> branches = template.requestBody("direct:show-branches", "", List.class);
        // Check
        Boolean branchExists = false;
        for (Ref reference : branches) {
            if (("refs/heads/" + (branchTest)).equals(reference.getName())) {
                branchExists = true;
            }
        }
        assertTrue(branchExists);
        git.close();
    }

    @Test
    public void cherryPickTest() throws Exception {
        // Init
        Git git = getGitTestRepository();
        File fileToAdd = new File(gitLocalRepo, filenameToAdd);
        fileToAdd.createNewFile();
        git.add().addFilepattern(filenameToAdd).call();
        File gitDir = new File(gitLocalRepo, ".git");
        assertEquals(gitDir.exists(), true);
        Status status = git.status().call();
        assertTrue(status.getAdded().contains(filenameToAdd));
        git.commit().setMessage(commitMessage).call();
        git.branchCreate().setName(branchTest).call();
        List<Ref> branches = git.branchList().call();
        Boolean branchExists = false;
        for (Ref reference : branches) {
            if (("refs/heads/" + (branchTest)).equals(reference.getName())) {
                branchExists = true;
            }
        }
        assertTrue(branchExists);
        String fileToAdd1Name = "filetest1test.txt";
        File fileToAdd1 = new File(gitLocalRepo, fileToAdd1Name);
        fileToAdd1.createNewFile();
        git.add().addFilepattern(fileToAdd1Name).call();
        status = git.status().call();
        assertTrue(status.getAdded().contains(fileToAdd1Name));
        git.commit().setMessage("Test second commit").call();
        Iterable<RevCommit> logs = git.log().call();
        validateGitLogs(git, "Test second commit", commitMessage);
        String id = logs.iterator().next().getName();
        // Test camel-git cherry-pick
        template.sendBodyAndHeader("direct:cherrypick", "", GIT_COMMIT_ID, id);
        // Check
        validateGitLogs(git, "Test second commit", commitMessage);
        git.close();
    }

    @Test
    public void cherryPickBranchToMasterTest() throws Exception {
        // Init
        Git git = getGitTestRepository();
        File fileToAdd = new File(gitLocalRepo, filenameToAdd);
        fileToAdd.createNewFile();
        git.add().addFilepattern(filenameToAdd).call();
        File gitDir = new File(gitLocalRepo, ".git");
        assertEquals(gitDir.exists(), true);
        Status status = git.status().call();
        assertTrue(status.getAdded().contains(filenameToAdd));
        git.commit().setMessage(commitMessage).call();
        git.branchCreate().setName(branchTest).call();
        List<Ref> branches = git.branchList().call();
        Boolean branchExists = false;
        for (Ref reference : branches) {
            if (("refs/heads/" + (branchTest)).equals(reference.getName())) {
                branchExists = true;
            }
        }
        assertTrue(branchExists);
        String fileToAdd1Name = "filetest1test.txt";
        File fileToAdd1 = new File(gitLocalRepo, fileToAdd1Name);
        fileToAdd1.createNewFile();
        git.add().addFilepattern(fileToAdd1Name).call();
        status = git.status().call();
        assertTrue(status.getAdded().contains(fileToAdd1Name));
        git.commit().setMessage("Test second commit").call();
        Iterable<RevCommit> logs = git.log().call();
        validateGitLogs(git, "Test second commit", commitMessage);
        String id = logs.iterator().next().getName();
        // Test camel-git cherry-pick (on master)
        template.sendBodyAndHeader("direct:cherrypick-master", "", GIT_COMMIT_ID, id);
        // Check
        git.checkout().setCreateBranch(false).setName("refs/heads/master").call();
        validateGitLogs(git, "Test second commit", commitMessage);
        git.close();
    }

    @Test
    public void remoteAddTest() throws Exception {
        Repository repository = getTestRepository();
        File gitDir = new File(gitLocalRepo, ".git");
        assertEquals(gitDir.exists(), true);
        Git git = new Git(repository);
        List<RemoteConfig> remoteConfigList = remoteList().call();
        assertTrue(((remoteConfigList.size()) == 0));
        Object result = template.requestBody("direct:remoteAdd", "");
        assertTrue((result instanceof RemoteConfig));
        RemoteConfig remoteConfig = ((RemoteConfig) (result));
        remoteConfigList = remoteList().call();
        assertTrue(((remoteConfigList.size()) == 1));
        assertEquals(remoteConfigList.get(0).getName(), remoteConfig.getName());
        assertEquals(remoteConfigList.get(0).getURIs(), remoteConfig.getURIs());
        git.close();
    }

    @Test
    public void remoteListTest() throws Exception {
        Repository repository = getTestRepository();
        File gitDir = new File(gitLocalRepo, ".git");
        assertEquals(gitDir.exists(), true);
        Git git = new Git(repository);
        RemoteAddCommand remoteAddCommand = remoteAdd();
        remoteAddCommand.setName("origin");
        remoteAddCommand.setUri(new URIish(remoteUriTest));
        remoteAddCommand.call();
        List<RemoteConfig> gitRemoteConfigs = remoteList().call();
        Object result = template.requestBody("direct:remoteList", "");
        assertTrue((result instanceof List));
        List<RemoteConfig> remoteConfigs = ((List<RemoteConfig>) (result));
        assertEquals(gitRemoteConfigs.size(), remoteConfigs.size());
        assertEquals(gitRemoteConfigs.get(0).getName(), remoteConfigs.get(0).getName());
        assertEquals(gitRemoteConfigs.get(0).getURIs(), remoteConfigs.get(0).getURIs());
        git.close();
    }

    @Test
    public void cleanTest() throws Exception {
        Git git = getGitTestRepository();
        File fileToAdd = new File(gitLocalRepo, filenameToAdd);
        fileToAdd.createNewFile();
        // Test camel-git add
        Set<String> cleaned = template.requestBodyAndHeader("direct:clean", "", GIT_FILE_NAME, filenameToAdd, Set.class);
        File gitDir = new File(gitLocalRepo, ".git");
        assertEquals(gitDir.exists(), true);
        assertTrue(cleaned.contains(filenameToAdd));
        git.close();
    }

    @Test
    public void gcTest() throws Exception {
        Git git = getGitTestRepository();
        File gitDir = new File(gitLocalRepo, ".git");
        assertEquals(gitDir.exists(), true);
        File fileToAdd = new File(gitLocalRepo, filenameToAdd);
        fileToAdd.createNewFile();
        git.add().addFilepattern(filenameToAdd).call();
        Status status = git.status().call();
        assertTrue(status.getAdded().contains(filenameToAdd));
        git.commit().setMessage(commitMessage).call();
        // Test camel-git commit (with no changes)
        template.requestBodyAndHeader("direct:commit", "", GIT_COMMIT_MESSAGE, commitMessage);
        // Check that it has been commited twice
        validateGitLogs(git, commitMessage, commitMessage);
        // Test camel-git add
        Properties gcResult = template.requestBodyAndHeader("direct:gc", "", GIT_FILE_NAME, filenameToAdd, Properties.class);
        assertNotNull(gcResult);
        git.close();
    }

    @Test
    public void mergeTest() throws Exception {
        // Init
        Git git = getGitTestRepository();
        File gitDir = new File(gitLocalRepo, ".git");
        assertEquals(gitDir.exists(), true);
        File fileToAdd = new File(gitLocalRepo, filenameToAdd);
        fileToAdd.createNewFile();
        git.add().addFilepattern(filenameToAdd).call();
        Status status = git.status().call();
        assertTrue(status.getAdded().contains(filenameToAdd));
        git.commit().setMessage(commitMessage).call();
        validateGitLogs(git, commitMessage);
        git.checkout().setCreateBranch(true).setName(branchTest).setUpstreamMode(SET_UPSTREAM).call();
        // Test camel-git commit (with branch)
        template.send("direct:commit-branch", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(GIT_COMMIT_MESSAGE, commitMessageBranch);
            }
        });
        validateGitLogs(git, commitMessageBranch, commitMessage);
        // Test camel-git commit (with branch)
        MergeResult result = template.requestBody("direct:merge", "", MergeResult.class);
        assertEquals(result.getMergeStatus().toString(), "Fast-forward");
        git.close();
    }

    @Test
    public void showTagsTest() throws Exception {
        // Init
        Git git = getGitTestRepository();
        File fileToAdd = new File(gitLocalRepo, filenameToAdd);
        fileToAdd.createNewFile();
        git.add().addFilepattern(filenameToAdd).call();
        File gitDir = new File(gitLocalRepo, ".git");
        assertEquals(gitDir.exists(), true);
        Status status = git.status().call();
        assertTrue(status.getAdded().contains(filenameToAdd));
        git.commit().setMessage(commitMessage).call();
        // Test camel-git create tag
        template.sendBody("direct:create-tag", "");
        // Check
        List<Ref> result = template.requestBody("direct:show-tags", "", List.class);
        boolean tagCreated = false;
        for (Ref refInternal : result) {
            if (refInternal.getName().equals(("refs/tags/" + (tagTest)))) {
                tagCreated = true;
            }
        }
        assertEquals(true, tagCreated);
        git.close();
    }
}

