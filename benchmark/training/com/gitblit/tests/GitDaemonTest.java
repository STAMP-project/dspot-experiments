/**
 * Copyright 2013 gitblit.com.
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
package com.gitblit.tests;


import com.gitblit.Constants.AccessRestrictionType;
import com.gitblit.Constants.AuthorizationControl;
import com.gitblit.models.RepositoryModel;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.MessageFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.transport.PushResult;
import org.eclipse.jgit.transport.RemoteRefUpdate;
import org.eclipse.jgit.util.FileUtils;
import org.junit.Assert;
import org.junit.Test;

import static org.eclipse.jgit.transport.RemoteRefUpdate.Status.OK;
import static org.eclipse.jgit.transport.RemoteRefUpdate.Status.REJECTED_OTHER_REASON;


public class GitDaemonTest extends GitblitUnitTest {
    static File ticgitFolder = new File(GitBlitSuite.REPOSITORIES, "working/ticgit");

    static File ticgit2Folder = new File(GitBlitSuite.REPOSITORIES, "working/ticgit2");

    static File jgitFolder = new File(GitBlitSuite.REPOSITORIES, "working/jgit");

    static File jgit2Folder = new File(GitBlitSuite.REPOSITORIES, "working/jgit2");

    String url = GitBlitSuite.gitDaemonUrl;

    private static final AtomicBoolean started = new AtomicBoolean(false);

    @Test
    public void testAnonymousClone() throws Exception {
        GitBlitSuite.close(GitDaemonTest.ticgitFolder);
        if (GitDaemonTest.ticgitFolder.exists()) {
            FileUtils.delete(GitDaemonTest.ticgitFolder, ((FileUtils.RECURSIVE) | (FileUtils.RETRY)));
        }
        // set push restriction
        RepositoryModel model = GitblitUnitTest.repositories().getRepositoryModel("ticgit.git");
        model.accessRestriction = AccessRestrictionType.PUSH;
        model.authorizationControl = AuthorizationControl.NAMED;
        GitblitUnitTest.repositories().updateRepositoryModel(model.name, model, false);
        CloneCommand clone = Git.cloneRepository();
        clone.setURI(MessageFormat.format("{0}/ticgit.git", url));
        clone.setDirectory(GitDaemonTest.ticgitFolder);
        clone.setBare(false);
        clone.setCloneAllBranches(true);
        GitBlitSuite.close(clone.call());
        Assert.assertTrue(true);
        // restore anonymous repository access
        model.accessRestriction = AccessRestrictionType.NONE;
        model.authorizationControl = AuthorizationControl.NAMED;
        GitblitUnitTest.repositories().updateRepositoryModel(model.name, model, false);
    }

    @Test
    public void testCloneRestrictedRepo() throws Exception {
        GitBlitSuite.close(GitDaemonTest.ticgit2Folder);
        if (GitDaemonTest.ticgit2Folder.exists()) {
            FileUtils.delete(GitDaemonTest.ticgit2Folder, FileUtils.RECURSIVE);
        }
        // restrict repository access
        RepositoryModel model = GitblitUnitTest.repositories().getRepositoryModel("ticgit.git");
        model.accessRestriction = AccessRestrictionType.CLONE;
        model.authorizationControl = AuthorizationControl.NAMED;
        GitblitUnitTest.repositories().updateRepositoryModel(model.name, model, false);
        // delete any existing working folder
        boolean cloned = false;
        try {
            CloneCommand clone = Git.cloneRepository();
            clone.setURI(MessageFormat.format("{0}/ticgit.git", url));
            clone.setDirectory(GitDaemonTest.ticgit2Folder);
            clone.setBare(false);
            clone.setCloneAllBranches(true);
            GitBlitSuite.close(clone.call());
            cloned = true;
        } catch (Exception e) {
            // swallow the exception which we expect
        }
        Assert.assertFalse("Anonymous was able to clone the repository?!", cloned);
        FileUtils.delete(GitDaemonTest.ticgit2Folder, FileUtils.RECURSIVE);
        // restore anonymous repository access
        model.accessRestriction = AccessRestrictionType.NONE;
        model.authorizationControl = AuthorizationControl.NAMED;
        GitblitUnitTest.repositories().updateRepositoryModel(model.name, model, false);
    }

    @Test
    public void testAnonymousPush() throws Exception {
        GitBlitSuite.close(GitDaemonTest.ticgitFolder);
        if (GitDaemonTest.ticgitFolder.exists()) {
            FileUtils.delete(GitDaemonTest.ticgitFolder, ((FileUtils.RECURSIVE) | (FileUtils.RETRY)));
        }
        // restore anonymous repository access
        RepositoryModel model = GitblitUnitTest.repositories().getRepositoryModel("ticgit.git");
        model.accessRestriction = AccessRestrictionType.NONE;
        model.authorizationControl = AuthorizationControl.NAMED;
        GitblitUnitTest.repositories().updateRepositoryModel(model.name, model, false);
        CloneCommand clone = Git.cloneRepository();
        clone.setURI(MessageFormat.format("{0}/ticgit.git", url));
        clone.setDirectory(GitDaemonTest.ticgitFolder);
        clone.setBare(false);
        clone.setCloneAllBranches(true);
        GitBlitSuite.close(clone.call());
        Assert.assertTrue(true);
        Git git = Git.open(GitDaemonTest.ticgitFolder);
        File file = new File(GitDaemonTest.ticgitFolder, "TODO");
        OutputStreamWriter os = new OutputStreamWriter(new FileOutputStream(file, true), Constants.CHARSET);
        BufferedWriter w = new BufferedWriter(os);
        w.write((("// hellol?? " + (new Date().toString())) + "\n"));
        w.close();
        git.add().addFilepattern(file.getName()).call();
        git.commit().setMessage("test commit").call();
        Iterable<PushResult> results = git.push().setPushAll().call();
        GitBlitSuite.close(git);
        for (PushResult result : results) {
            for (RemoteRefUpdate update : result.getRemoteUpdates()) {
                Assert.assertEquals(OK, update.getStatus());
            }
        }
    }

    @Test
    public void testPushRestrictedRepo() throws Exception {
        GitBlitSuite.close(GitDaemonTest.ticgitFolder);
        if (GitDaemonTest.ticgitFolder.exists()) {
            FileUtils.delete(GitDaemonTest.ticgitFolder, ((FileUtils.RECURSIVE) | (FileUtils.RETRY)));
        }
        // restore anonymous repository access
        RepositoryModel model = GitblitUnitTest.repositories().getRepositoryModel("ticgit.git");
        model.accessRestriction = AccessRestrictionType.PUSH;
        model.authorizationControl = AuthorizationControl.NAMED;
        GitblitUnitTest.repositories().updateRepositoryModel(model.name, model, false);
        CloneCommand clone = Git.cloneRepository();
        clone.setURI(MessageFormat.format("{0}/ticgit.git", url));
        clone.setDirectory(GitDaemonTest.ticgitFolder);
        clone.setBare(false);
        clone.setCloneAllBranches(true);
        GitBlitSuite.close(clone.call());
        Assert.assertTrue(true);
        Git git = Git.open(GitDaemonTest.ticgitFolder);
        File file = new File(GitDaemonTest.ticgitFolder, "TODO");
        OutputStreamWriter os = new OutputStreamWriter(new FileOutputStream(file, true), Constants.CHARSET);
        BufferedWriter w = new BufferedWriter(os);
        w.write((("// hellol?? " + (new Date().toString())) + "\n"));
        w.close();
        git.add().addFilepattern(file.getName()).call();
        git.commit().setMessage("test commit").call();
        Iterable<PushResult> results = git.push().setPushAll().call();
        GitBlitSuite.close(git);
        for (PushResult result : results) {
            for (RemoteRefUpdate update : result.getRemoteUpdates()) {
                Assert.assertEquals(REJECTED_OTHER_REASON, update.getStatus());
            }
        }
    }

    @Test
    public void testPushToFrozenRepo() throws Exception {
        GitBlitSuite.close(GitDaemonTest.jgitFolder);
        if (GitDaemonTest.jgitFolder.exists()) {
            FileUtils.delete(GitDaemonTest.jgitFolder, ((FileUtils.RECURSIVE) | (FileUtils.RETRY)));
        }
        CloneCommand clone = Git.cloneRepository();
        clone.setURI(MessageFormat.format("{0}/test/jgit.git", url));
        clone.setDirectory(GitDaemonTest.jgitFolder);
        clone.setBare(false);
        clone.setCloneAllBranches(true);
        GitBlitSuite.close(clone.call());
        Assert.assertTrue(true);
        // freeze repo
        RepositoryModel model = GitblitUnitTest.repositories().getRepositoryModel("test/jgit.git");
        model.isFrozen = true;
        GitblitUnitTest.repositories().updateRepositoryModel(model.name, model, false);
        Git git = Git.open(GitDaemonTest.jgitFolder);
        File file = new File(GitDaemonTest.jgitFolder, "TODO");
        OutputStreamWriter os = new OutputStreamWriter(new FileOutputStream(file, true), Constants.CHARSET);
        BufferedWriter w = new BufferedWriter(os);
        w.write((("// " + (new Date().toString())) + "\n"));
        w.close();
        git.add().addFilepattern(file.getName()).call();
        git.commit().setMessage("test commit").call();
        Iterable<PushResult> results = git.push().call();
        for (PushResult result : results) {
            for (RemoteRefUpdate update : result.getRemoteUpdates()) {
                Assert.assertEquals(REJECTED_OTHER_REASON, update.getStatus());
            }
        }
        // unfreeze repo
        model.isFrozen = false;
        GitblitUnitTest.repositories().updateRepositoryModel(model.name, model, false);
        results = git.push().setPushAll().call();
        GitBlitSuite.close(git);
        for (PushResult result : results) {
            for (RemoteRefUpdate update : result.getRemoteUpdates()) {
                Assert.assertEquals(OK, update.getStatus());
            }
        }
    }

    @Test
    public void testPushToNonBareRepository() throws Exception {
        GitBlitSuite.close(GitDaemonTest.jgit2Folder);
        if (GitDaemonTest.jgit2Folder.exists()) {
            FileUtils.delete(GitDaemonTest.jgit2Folder, ((FileUtils.RECURSIVE) | (FileUtils.RETRY)));
        }
        CloneCommand clone = Git.cloneRepository();
        clone.setURI(MessageFormat.format("{0}/working/jgit", url));
        clone.setDirectory(GitDaemonTest.jgit2Folder);
        clone.setBare(false);
        clone.setCloneAllBranches(true);
        GitBlitSuite.close(clone.call());
        Assert.assertTrue(true);
        Git git = Git.open(GitDaemonTest.jgit2Folder);
        File file = new File(GitDaemonTest.jgit2Folder, "NONBARE");
        OutputStreamWriter os = new OutputStreamWriter(new FileOutputStream(file, true), Constants.CHARSET);
        BufferedWriter w = new BufferedWriter(os);
        w.write((("// " + (new Date().toString())) + "\n"));
        w.close();
        git.add().addFilepattern(file.getName()).call();
        git.commit().setMessage("test commit followed by push to non-bare repository").call();
        Iterable<PushResult> results = git.push().setPushAll().call();
        GitBlitSuite.close(git);
        for (PushResult result : results) {
            for (RemoteRefUpdate update : result.getRemoteUpdates()) {
                Assert.assertEquals(REJECTED_OTHER_REASON, update.getStatus());
            }
        }
    }
}

