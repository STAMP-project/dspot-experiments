/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.zeppelin.notebook.repo;


import NotebookRepoWithVersionControl.Revision;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import org.apache.zeppelin.conf.ZeppelinConfiguration;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This tests the remote Git tracking for notebooks. The tests uses two local Git repositories created locally
 * to handle the tracking of Git actions (pushes and pulls). The repositories are:
 * 1. The first repository is considered as a remote that mimics a remote GitHub directory
 * 2. The second repository is considered as the local notebook repository
 */
public class GitHubNotebookRepoTest {
    private static final Logger LOG = LoggerFactory.getLogger(GitHubNotebookRepoTest.class);

    private static final String TEST_NOTE_ID = "2A94M5J1Z";

    private static final String TEST_NOTE_PATH = "/my_project/my_note1";

    private File remoteZeppelinDir;

    private File localZeppelinDir;

    private String localNotebooksDir;

    private String remoteNotebooksDir;

    private ZeppelinConfiguration conf;

    private GitHubNotebookRepo gitHubNotebookRepo;

    private RevCommit firstCommitRevision;

    private Git remoteGit;

    /**
     * Test the case when the Notebook repository is created, it pulls the latest changes from the remote repository
     */
    @Test
    public void pullChangesFromRemoteRepositoryOnLoadingNotebook() throws IOException, GitAPIException {
        NotebookRepoWithVersionControl.Revision firstHistoryRevision = gitHubNotebookRepo.revisionHistory(GitHubNotebookRepoTest.TEST_NOTE_ID, GitHubNotebookRepoTest.TEST_NOTE_PATH, null).get(0);
        assert this.firstCommitRevision.getName().equals(firstHistoryRevision.id);
    }

    /**
     * Test the case when the check-pointing (add new files and commit) it also pulls the latest changes from the
     * remote repository
     */
    @Test
    public void pullChangesFromRemoteRepositoryOnCheckpointing() throws IOException, GitAPIException {
        // Create a new commit in the remote repository
        RevCommit secondCommitRevision = remoteGit.commit().setMessage("Second commit from remote repository").call();
        // Add a new paragraph to the local repository
        addParagraphToNotebook();
        // Commit and push the changes to remote repository
        NotebookRepoWithVersionControl.Revision thirdCommitRevision = gitHubNotebookRepo.checkpoint(GitHubNotebookRepoTest.TEST_NOTE_ID, GitHubNotebookRepoTest.TEST_NOTE_PATH, "Third commit from local repository", null);
        // Check all the commits as seen from the local repository. The commits are ordered chronologically. The last
        // commit is the first in the commit logs.
        Iterator<RevCommit> revisions = gitHubNotebookRepo.getGit().log().all().call().iterator();
        revisions.next();// The Merge `master` commit after pushing to the remote repository

        assert thirdCommitRevision.id.equals(revisions.next().getName());// The local commit after adding the paragraph

        // The second commit done on the remote repository
        assert secondCommitRevision.getName().equals(revisions.next().getName());
        // The first commit done on the remote repository
        assert firstCommitRevision.getName().equals(revisions.next().getName());
    }

    /**
     * Test the case when the check-pointing (add new files and commit) it pushes the local commits to the remote
     * repository
     */
    @Test
    public void pushLocalChangesToRemoteRepositoryOnCheckpointing() throws IOException, GitAPIException {
        // Add a new paragraph to the local repository
        addParagraphToNotebook();
        // Commit and push the changes to remote repository
        NotebookRepoWithVersionControl.Revision secondCommitRevision = gitHubNotebookRepo.checkpoint(GitHubNotebookRepoTest.TEST_NOTE_ID, GitHubNotebookRepoTest.TEST_NOTE_PATH, "Second commit from local repository", null);
        // Check all the commits as seen from the remote repository. The commits are ordered chronologically. The last
        // commit is the first in the commit logs.
        Iterator<RevCommit> revisions = remoteGit.log().all().call().iterator();
        assert secondCommitRevision.id.equals(revisions.next().getName());// The local commit after adding the paragraph

        // The first commit done on the remote repository
        assert firstCommitRevision.getName().equals(revisions.next().getName());
    }
}

