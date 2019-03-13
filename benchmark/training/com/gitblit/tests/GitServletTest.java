/**
 * Copyright 2011 gitblit.com.
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


import AccessPermission.CLONE;
import AccessPermission.CREATE;
import AccessPermission.DELETE;
import AccessPermission.PUSH;
import AccessPermission.REWIND;
import AccessPermission.VIEW;
import com.gitblit.Constants.AccessRestrictionType;
import com.gitblit.Constants.AuthorizationControl;
import com.gitblit.models.RefLogEntry;
import com.gitblit.models.RepositoryModel;
import com.gitblit.models.UserModel;
import com.gitblit.utils.RefLogUtils;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.PushResult;
import org.eclipse.jgit.transport.RemoteRefUpdate;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.util.FileUtils;
import org.junit.Assert;
import org.junit.Test;

import static org.eclipse.jgit.transport.RemoteRefUpdate.Status.OK;
import static org.eclipse.jgit.transport.RemoteRefUpdate.Status.REJECTED_OTHER_REASON;


public class GitServletTest extends GitblitUnitTest {
    static File ticgitFolder = new File(GitBlitSuite.REPOSITORIES, "working/ticgit");

    static File ticgit2Folder = new File(GitBlitSuite.REPOSITORIES, "working/ticgit2");

    static File jgitFolder = new File(GitBlitSuite.REPOSITORIES, "working/jgit");

    static File jgit2Folder = new File(GitBlitSuite.REPOSITORIES, "working/jgit2");

    String url = GitBlitSuite.gitServletUrl;

    String account = GitBlitSuite.account;

    String password = GitBlitSuite.password;

    private static final AtomicBoolean started = new AtomicBoolean(false);

    @Test
    public void testClone() throws Exception {
        GitBlitSuite.close(GitServletTest.ticgitFolder);
        if (GitServletTest.ticgitFolder.exists()) {
            FileUtils.delete(GitServletTest.ticgitFolder, ((FileUtils.RECURSIVE) | (FileUtils.RETRY)));
        }
        CloneCommand clone = Git.cloneRepository();
        clone.setURI(MessageFormat.format("{0}/ticgit.git", url));
        clone.setDirectory(GitServletTest.ticgitFolder);
        clone.setBare(false);
        clone.setCloneAllBranches(true);
        clone.setCredentialsProvider(new UsernamePasswordCredentialsProvider(account, password));
        GitBlitSuite.close(clone.call());
        Assert.assertTrue(true);
    }

    @Test
    public void testBogusLoginClone() throws Exception {
        // restrict repository access
        RepositoryModel model = GitblitUnitTest.repositories().getRepositoryModel("ticgit.git");
        model.accessRestriction = AccessRestrictionType.CLONE;
        GitblitUnitTest.repositories().updateRepositoryModel(model.name, model, false);
        // delete any existing working folder
        boolean cloned = false;
        try {
            CloneCommand clone = Git.cloneRepository();
            clone.setURI(MessageFormat.format("{0}/ticgit.git", url));
            clone.setDirectory(GitServletTest.ticgit2Folder);
            clone.setBare(false);
            clone.setCloneAllBranches(true);
            clone.setCredentialsProvider(new UsernamePasswordCredentialsProvider("bogus", "bogus"));
            GitBlitSuite.close(clone.call());
            cloned = true;
        } catch (Exception e) {
            // swallow the exception which we expect
        }
        // restore anonymous repository access
        model.accessRestriction = AccessRestrictionType.NONE;
        GitblitUnitTest.repositories().updateRepositoryModel(model.name, model, false);
        Assert.assertFalse("Bogus login cloned a repository?!", cloned);
    }

    @Test
    public void testUnauthorizedLoginClone() throws Exception {
        // restrict repository access
        RepositoryModel model = GitblitUnitTest.repositories().getRepositoryModel("ticgit.git");
        model.accessRestriction = AccessRestrictionType.CLONE;
        model.authorizationControl = AuthorizationControl.NAMED;
        UserModel user = new UserModel("james");
        user.password = "james";
        GitblitUnitTest.gitblit().addUser(user);
        GitblitUnitTest.repositories().updateRepositoryModel(model.name, model, false);
        FileUtils.delete(GitServletTest.ticgit2Folder, FileUtils.RECURSIVE);
        // delete any existing working folder
        boolean cloned = false;
        try {
            CloneCommand clone = Git.cloneRepository();
            clone.setURI(MessageFormat.format("{0}/ticgit.git", url));
            clone.setDirectory(GitServletTest.ticgit2Folder);
            clone.setBare(false);
            clone.setCloneAllBranches(true);
            clone.setCredentialsProvider(new UsernamePasswordCredentialsProvider(user.username, user.password));
            GitBlitSuite.close(clone.call());
            cloned = true;
        } catch (Exception e) {
            // swallow the exception which we expect
        }
        Assert.assertFalse("Unauthorized login cloned a repository?!", cloned);
        FileUtils.delete(GitServletTest.ticgit2Folder, FileUtils.RECURSIVE);
        // switch to authenticated
        model.authorizationControl = AuthorizationControl.AUTHENTICATED;
        GitblitUnitTest.repositories().updateRepositoryModel(model.name, model, false);
        // try clone again
        cloned = false;
        CloneCommand clone = Git.cloneRepository();
        clone.setURI(MessageFormat.format("{0}/ticgit.git", url));
        clone.setDirectory(GitServletTest.ticgit2Folder);
        clone.setBare(false);
        clone.setCloneAllBranches(true);
        clone.setCredentialsProvider(new UsernamePasswordCredentialsProvider(user.username, user.password));
        GitBlitSuite.close(clone.call());
        cloned = true;
        Assert.assertTrue("Authenticated login could not clone!", cloned);
        FileUtils.delete(GitServletTest.ticgit2Folder, FileUtils.RECURSIVE);
        // restore anonymous repository access
        model.accessRestriction = AccessRestrictionType.NONE;
        model.authorizationControl = AuthorizationControl.NAMED;
        GitblitUnitTest.repositories().updateRepositoryModel(model.name, model, false);
        GitServletTest.delete(user);
    }

    @Test
    public void testAnonymousPush() throws Exception {
        GitBlitSuite.close(GitServletTest.ticgitFolder);
        if (GitServletTest.ticgitFolder.exists()) {
            FileUtils.delete(GitServletTest.ticgitFolder, ((FileUtils.RECURSIVE) | (FileUtils.RETRY)));
        }
        RepositoryModel model = GitblitUnitTest.repositories().getRepositoryModel("ticgit.git");
        model.accessRestriction = AccessRestrictionType.NONE;
        GitblitUnitTest.repositories().updateRepositoryModel(model.name, model, false);
        CloneCommand clone = Git.cloneRepository();
        clone.setURI(MessageFormat.format("{0}/ticgit.git", url));
        clone.setDirectory(GitServletTest.ticgitFolder);
        clone.setBare(false);
        clone.setCloneAllBranches(true);
        clone.setCredentialsProvider(new UsernamePasswordCredentialsProvider(account, password));
        GitBlitSuite.close(clone.call());
        Assert.assertTrue(true);
        Git git = Git.open(GitServletTest.ticgitFolder);
        File file = new File(GitServletTest.ticgitFolder, "TODO");
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
    public void testSubfolderPush() throws Exception {
        GitBlitSuite.close(GitServletTest.jgitFolder);
        if (GitServletTest.jgitFolder.exists()) {
            FileUtils.delete(GitServletTest.jgitFolder, ((FileUtils.RECURSIVE) | (FileUtils.RETRY)));
        }
        CloneCommand clone = Git.cloneRepository();
        clone.setURI(MessageFormat.format("{0}/test/jgit.git", url));
        clone.setDirectory(GitServletTest.jgitFolder);
        clone.setBare(false);
        clone.setCloneAllBranches(true);
        clone.setCredentialsProvider(new UsernamePasswordCredentialsProvider(account, password));
        GitBlitSuite.close(clone.call());
        Assert.assertTrue(true);
        Git git = Git.open(GitServletTest.jgitFolder);
        File file = new File(GitServletTest.jgitFolder, "TODO");
        OutputStreamWriter os = new OutputStreamWriter(new FileOutputStream(file, true), Constants.CHARSET);
        BufferedWriter w = new BufferedWriter(os);
        w.write((("// " + (new Date().toString())) + "\n"));
        w.close();
        git.add().addFilepattern(file.getName()).call();
        git.commit().setMessage("test commit").call();
        Iterable<PushResult> results = git.push().setPushAll().setCredentialsProvider(new UsernamePasswordCredentialsProvider(account, password)).call();
        GitBlitSuite.close(git);
        for (PushResult result : results) {
            for (RemoteRefUpdate update : result.getRemoteUpdates()) {
                Assert.assertEquals(OK, update.getStatus());
            }
        }
    }

    @Test
    public void testPushToFrozenRepo() throws Exception {
        GitBlitSuite.close(GitServletTest.jgitFolder);
        if (GitServletTest.jgitFolder.exists()) {
            FileUtils.delete(GitServletTest.jgitFolder, ((FileUtils.RECURSIVE) | (FileUtils.RETRY)));
        }
        CloneCommand clone = Git.cloneRepository();
        clone.setURI(MessageFormat.format("{0}/test/jgit.git", url));
        clone.setDirectory(GitServletTest.jgitFolder);
        clone.setBare(false);
        clone.setCloneAllBranches(true);
        clone.setCredentialsProvider(new UsernamePasswordCredentialsProvider(account, password));
        GitBlitSuite.close(clone.call());
        Assert.assertTrue(true);
        // freeze repo
        RepositoryModel model = GitblitUnitTest.repositories().getRepositoryModel("test/jgit.git");
        model.isFrozen = true;
        GitblitUnitTest.repositories().updateRepositoryModel(model.name, model, false);
        Git git = Git.open(GitServletTest.jgitFolder);
        File file = new File(GitServletTest.jgitFolder, "TODO");
        OutputStreamWriter os = new OutputStreamWriter(new FileOutputStream(file, true), Constants.CHARSET);
        BufferedWriter w = new BufferedWriter(os);
        w.write((("// " + (new Date().toString())) + "\n"));
        w.close();
        git.add().addFilepattern(file.getName()).call();
        git.commit().setMessage("test commit").call();
        Iterable<PushResult> results = git.push().setPushAll().setCredentialsProvider(new UsernamePasswordCredentialsProvider(account, password)).call();
        for (PushResult result : results) {
            for (RemoteRefUpdate update : result.getRemoteUpdates()) {
                Assert.assertEquals(REJECTED_OTHER_REASON, update.getStatus());
            }
        }
        // unfreeze repo
        model.isFrozen = false;
        GitblitUnitTest.repositories().updateRepositoryModel(model.name, model, false);
        results = git.push().setPushAll().setCredentialsProvider(new UsernamePasswordCredentialsProvider(account, password)).call();
        GitBlitSuite.close(git);
        for (PushResult result : results) {
            for (RemoteRefUpdate update : result.getRemoteUpdates()) {
                Assert.assertEquals(OK, update.getStatus());
            }
        }
    }

    @Test
    public void testPushToNonBareRepository() throws Exception {
        CloneCommand clone = Git.cloneRepository();
        clone.setURI(MessageFormat.format("{0}/working/jgit", url));
        clone.setDirectory(GitServletTest.jgit2Folder);
        clone.setBare(false);
        clone.setCloneAllBranches(true);
        clone.setCredentialsProvider(new UsernamePasswordCredentialsProvider(account, password));
        GitBlitSuite.close(clone.call());
        Assert.assertTrue(true);
        Git git = Git.open(GitServletTest.jgit2Folder);
        File file = new File(GitServletTest.jgit2Folder, "NONBARE");
        OutputStreamWriter os = new OutputStreamWriter(new FileOutputStream(file, true), Constants.CHARSET);
        BufferedWriter w = new BufferedWriter(os);
        w.write((("// " + (new Date().toString())) + "\n"));
        w.close();
        git.add().addFilepattern(file.getName()).call();
        git.commit().setMessage("test commit followed by push to non-bare repository").call();
        Iterable<PushResult> results = git.push().setPushAll().setCredentialsProvider(new UsernamePasswordCredentialsProvider(account, password)).call();
        GitBlitSuite.close(git);
        for (PushResult result : results) {
            for (RemoteRefUpdate update : result.getRemoteUpdates()) {
                Assert.assertEquals(REJECTED_OTHER_REASON, update.getStatus());
            }
        }
    }

    @Test
    public void testCommitterVerification() throws Exception {
        UserModel user = GitServletTest.getUser();
        testCommitterVerification(user, "joe", null, false);
        testCommitterVerification(user, "joe", user.emailAddress, false);
        testCommitterVerification(user, user.username, null, false);
        testCommitterVerification(user, user.username, user.emailAddress, true);
        user.displayName = "James Moger";
        testCommitterVerification(user, user.displayName, null, false);
        testCommitterVerification(user, user.displayName, "something", false);
        testCommitterVerification(user, user.displayName, user.emailAddress, true);
    }

    @Test
    public void testMergeCommitterVerification() throws Exception {
        testMergeCommitterVerification(false);
        testMergeCommitterVerification(true);
    }

    @Test
    public void testBlockClone() throws Exception {
        testRefChange(VIEW, null, null, null);
    }

    @Test
    public void testBlockPush() throws Exception {
        testRefChange(CLONE, null, null, null);
    }

    @Test
    public void testBlockBranchCreation() throws Exception {
        testRefChange(PUSH, REJECTED_OTHER_REASON, null, null);
    }

    @Test
    public void testBlockBranchDeletion() throws Exception {
        testRefChange(CREATE, OK, REJECTED_OTHER_REASON, null);
    }

    @Test
    public void testBlockBranchRewind() throws Exception {
        testRefChange(DELETE, OK, OK, REJECTED_OTHER_REASON);
    }

    @Test
    public void testBranchRewind() throws Exception {
        testRefChange(REWIND, OK, OK, OK);
    }

    @Test
    public void testCreateOnPush() throws Exception {
        testCreateOnPush(false, false);
        testCreateOnPush(true, false);
        testCreateOnPush(false, true);
    }

    @Test
    public void testPushLog() throws IOException {
        String name = "refchecks/ticgit.git";
        File refChecks = new File(GitBlitSuite.REPOSITORIES, name);
        Repository repository = new FileRepositoryBuilder().setGitDir(refChecks).build();
        List<RefLogEntry> pushes = RefLogUtils.getRefLog(name, repository);
        GitBlitSuite.close(repository);
        Assert.assertTrue("Repository has an empty push log!", ((pushes.size()) > 0));
    }
}

