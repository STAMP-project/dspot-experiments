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


import JnaUtils.S_IROTH;
import JnaUtils.S_IRWXG;
import JnaUtils.S_ISGID;
import SearchType.AUTHOR;
import SearchType.COMMIT;
import SearchType.COMMITTER;
import com.gitblit.Constants.SearchType;
import com.gitblit.models.GitNote;
import com.gitblit.models.PathModel;
import com.gitblit.models.PathModel.PathChangeModel;
import com.gitblit.models.RefModel;
import com.gitblit.utils.CompressionUtils;
import com.gitblit.utils.JGitUtils;
import com.gitblit.utils.JnaUtils;
import com.gitblit.utils.StringUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.FileMode;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryCache;
import org.eclipse.jgit.revplot.PlotCommit;
import org.eclipse.jgit.revplot.PlotCommitList;
import org.eclipse.jgit.revplot.PlotLane;
import org.eclipse.jgit.revplot.PlotWalk;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.util.FS;
import org.eclipse.jgit.util.FileUtils;
import org.junit.Assert;
import org.junit.Test;

import static org.eclipse.jgit.diff.DiffEntry.ChangeType.ADD;
import static org.eclipse.jgit.diff.DiffEntry.ChangeType.DELETE;
import static org.eclipse.jgit.lib.RepositoryCache.FileKey.resolve;


public class JGitUtilsTest extends GitblitUnitTest {
    @Test
    public void testDisplayName() throws Exception {
        Assert.assertEquals("Napoleon Bonaparte", JGitUtils.getDisplayName(new PersonIdent("Napoleon Bonaparte", "")));
        Assert.assertEquals("<someone@somewhere.com>", JGitUtils.getDisplayName(new PersonIdent("", "someone@somewhere.com")));
        Assert.assertEquals("Napoleon Bonaparte <someone@somewhere.com>", JGitUtils.getDisplayName(new PersonIdent("Napoleon Bonaparte", "someone@somewhere.com")));
    }

    @Test
    public void testFindRepositories() {
        List<String> list = JGitUtils.getRepositoryList(null, false, true, (-1), null);
        Assert.assertEquals(0, list.size());
        list.addAll(JGitUtils.getRepositoryList(new File("DoesNotExist"), true, true, (-1), null));
        Assert.assertEquals(0, list.size());
        list.addAll(JGitUtils.getRepositoryList(GitBlitSuite.REPOSITORIES, false, true, (-1), null));
        Assert.assertTrue(("No repositories found in " + (GitBlitSuite.REPOSITORIES)), ((list.size()) > 0));
    }

    @Test
    public void testFindExclusions() {
        List<String> list = JGitUtils.getRepositoryList(GitBlitSuite.REPOSITORIES, false, true, (-1), null);
        Assert.assertTrue("Missing jgit repository?!", list.contains("test/jgit.git"));
        list = JGitUtils.getRepositoryList(GitBlitSuite.REPOSITORIES, false, true, (-1), Arrays.asList("test/jgit\\.git"));
        Assert.assertFalse("Repository exclusion failed!", list.contains("test/jgit.git"));
        list = JGitUtils.getRepositoryList(GitBlitSuite.REPOSITORIES, false, true, (-1), Arrays.asList("test/*"));
        Assert.assertFalse("Repository exclusion failed!", list.contains("test/jgit.git"));
        list = JGitUtils.getRepositoryList(GitBlitSuite.REPOSITORIES, false, true, (-1), Arrays.asList(".*jgit.*"));
        Assert.assertFalse("Repository exclusion failed!", list.contains("test/jgit.git"));
        Assert.assertFalse("Repository exclusion failed!", list.contains("working/jgit"));
        Assert.assertFalse("Repository exclusion failed!", list.contains("working/jgit2"));
    }

    @Test
    public void testOpenRepository() throws Exception {
        Repository repository = GitBlitSuite.getHelloworldRepository();
        repository.close();
        Assert.assertNotNull("Could not find repository!", repository);
    }

    @Test
    public void testFirstCommit() throws Exception {
        Assert.assertEquals(new Date(0), JGitUtils.getFirstChange(null, null));
        Repository repository = GitBlitSuite.getHelloworldRepository();
        RevCommit commit = JGitUtils.getFirstCommit(repository, null);
        Date firstChange = JGitUtils.getFirstChange(repository, null);
        repository.close();
        Assert.assertNotNull("Could not get first commit!", commit);
        Assert.assertEquals("Incorrect first commit!", "f554664a346629dc2b839f7292d06bad2db4aece", commit.getName());
        Assert.assertTrue(firstChange.equals(new Date(((commit.getCommitTime()) * 1000L))));
    }

    @Test
    public void testLastCommit() throws Exception {
        Assert.assertEquals(new Date(0), JGitUtils.getLastChange(null).when);
        Repository repository = GitBlitSuite.getHelloworldRepository();
        Assert.assertTrue(((JGitUtils.getCommit(repository, null)) != null));
        Date date = JGitUtils.getLastChange(repository).when;
        repository.close();
        Assert.assertNotNull("Could not get last repository change date!", date);
    }

    @Test
    public void testCreateRepository() throws Exception {
        String[] repositories = new String[]{ "NewTestRepository.git", "NewTestRepository" };
        for (String repositoryName : repositories) {
            Repository repository = JGitUtils.createRepository(GitBlitSuite.REPOSITORIES, repositoryName);
            File folder = resolve(new File(GitBlitSuite.REPOSITORIES, repositoryName), FS.DETECTED);
            Assert.assertNotNull(repository);
            Assert.assertFalse(JGitUtils.hasCommits(repository));
            Assert.assertNull(JGitUtils.getFirstCommit(repository, null));
            Assert.assertEquals(folder.lastModified(), JGitUtils.getFirstChange(repository, null).getTime());
            Assert.assertEquals(folder.lastModified(), JGitUtils.getLastChange(repository).when.getTime());
            Assert.assertNull(JGitUtils.getCommit(repository, null));
            repository.close();
            RepositoryCache.close(repository);
            FileUtils.delete(repository.getDirectory(), FileUtils.RECURSIVE);
        }
    }

    @Test
    public void testCreateRepositoryShared() throws Exception {
        String[] repositories = new String[]{ "NewSharedTestRepository.git" };
        for (String repositoryName : repositories) {
            Repository repository = JGitUtils.createRepository(GitBlitSuite.REPOSITORIES, repositoryName, "group");
            File folder = resolve(new File(GitBlitSuite.REPOSITORIES, repositoryName), FS.DETECTED);
            Assert.assertNotNull(repository);
            Assert.assertFalse(JGitUtils.hasCommits(repository));
            Assert.assertNull(JGitUtils.getFirstCommit(repository, null));
            Assert.assertEquals("1", repository.getConfig().getString("core", null, "sharedRepository"));
            Assert.assertTrue(folder.exists());
            if (!(JnaUtils.isWindows())) {
                int mode = JnaUtils.getFilemode(folder);
                Assert.assertEquals(S_ISGID, (mode & (JnaUtils.S_ISGID)));
                Assert.assertEquals(S_IRWXG, (mode & (JnaUtils.S_IRWXG)));
                mode = JnaUtils.getFilemode(((folder.getAbsolutePath()) + "/HEAD"));
                Assert.assertEquals(((JnaUtils.S_IRGRP) | (JnaUtils.S_IWGRP)), (mode & (JnaUtils.S_IRWXG)));
                mode = JnaUtils.getFilemode(((folder.getAbsolutePath()) + "/config"));
                Assert.assertEquals(((JnaUtils.S_IRGRP) | (JnaUtils.S_IWGRP)), (mode & (JnaUtils.S_IRWXG)));
            }
            repository.close();
            RepositoryCache.close(repository);
            FileUtils.delete(repository.getDirectory(), FileUtils.RECURSIVE);
        }
    }

    @Test
    public void testCreateRepositorySharedCustom() throws Exception {
        String[] repositories = new String[]{ "NewSharedTestRepository.git" };
        for (String repositoryName : repositories) {
            Repository repository = JGitUtils.createRepository(GitBlitSuite.REPOSITORIES, repositoryName, "660");
            File folder = resolve(new File(GitBlitSuite.REPOSITORIES, repositoryName), FS.DETECTED);
            Assert.assertNotNull(repository);
            Assert.assertFalse(JGitUtils.hasCommits(repository));
            Assert.assertNull(JGitUtils.getFirstCommit(repository, null));
            Assert.assertEquals("0660", repository.getConfig().getString("core", null, "sharedRepository"));
            Assert.assertTrue(folder.exists());
            if (!(JnaUtils.isWindows())) {
                int mode = JnaUtils.getFilemode(folder);
                Assert.assertEquals(S_ISGID, (mode & (JnaUtils.S_ISGID)));
                Assert.assertEquals(S_IRWXG, (mode & (JnaUtils.S_IRWXG)));
                Assert.assertEquals(0, (mode & (JnaUtils.S_IRWXO)));
                mode = JnaUtils.getFilemode(((folder.getAbsolutePath()) + "/HEAD"));
                Assert.assertEquals(((JnaUtils.S_IRGRP) | (JnaUtils.S_IWGRP)), (mode & (JnaUtils.S_IRWXG)));
                Assert.assertEquals(0, (mode & (JnaUtils.S_IRWXO)));
                mode = JnaUtils.getFilemode(((folder.getAbsolutePath()) + "/config"));
                Assert.assertEquals(((JnaUtils.S_IRGRP) | (JnaUtils.S_IWGRP)), (mode & (JnaUtils.S_IRWXG)));
                Assert.assertEquals(0, (mode & (JnaUtils.S_IRWXO)));
            }
            repository.close();
            RepositoryCache.close(repository);
            FileUtils.delete(repository.getDirectory(), FileUtils.RECURSIVE);
        }
    }

    @Test
    public void testCreateRepositorySharedSgidParent() throws Exception {
        if (!(JnaUtils.isWindows())) {
            String repositoryAll = "NewTestRepositoryAll.git";
            String repositoryUmask = "NewTestRepositoryUmask.git";
            String sgidParent = "sgid";
            File parent = new File(GitBlitSuite.REPOSITORIES, sgidParent);
            File folder = null;
            boolean parentExisted = parent.exists();
            try {
                if (!parentExisted) {
                    Assert.assertTrue("Could not create SGID parent folder.", parent.mkdir());
                }
                int mode = JnaUtils.getFilemode(parent);
                Assert.assertTrue((mode > 0));
                Assert.assertEquals(0, JnaUtils.setFilemode(parent, ((mode | (JnaUtils.S_ISGID)) | (JnaUtils.S_IWGRP))));
                Repository repository = JGitUtils.createRepository(parent, repositoryAll, "all");
                folder = resolve(new File(parent, repositoryAll), FS.DETECTED);
                Assert.assertNotNull(repository);
                Assert.assertEquals("2", repository.getConfig().getString("core", null, "sharedRepository"));
                Assert.assertTrue(folder.exists());
                mode = JnaUtils.getFilemode(folder);
                Assert.assertEquals(S_ISGID, (mode & (JnaUtils.S_ISGID)));
                mode = JnaUtils.getFilemode(((folder.getAbsolutePath()) + "/HEAD"));
                Assert.assertEquals(((JnaUtils.S_IRGRP) | (JnaUtils.S_IWGRP)), (mode & (JnaUtils.S_IRWXG)));
                Assert.assertEquals(S_IROTH, (mode & (JnaUtils.S_IRWXO)));
                mode = JnaUtils.getFilemode(((folder.getAbsolutePath()) + "/config"));
                Assert.assertEquals(((JnaUtils.S_IRGRP) | (JnaUtils.S_IWGRP)), (mode & (JnaUtils.S_IRWXG)));
                Assert.assertEquals(S_IROTH, (mode & (JnaUtils.S_IRWXO)));
                repository.close();
                RepositoryCache.close(repository);
                repository = JGitUtils.createRepository(parent, repositoryUmask, "umask");
                folder = resolve(new File(parent, repositoryUmask), FS.DETECTED);
                Assert.assertNotNull(repository);
                Assert.assertEquals(null, repository.getConfig().getString("core", null, "sharedRepository"));
                Assert.assertTrue(folder.exists());
                mode = JnaUtils.getFilemode(folder);
                Assert.assertEquals(S_ISGID, (mode & (JnaUtils.S_ISGID)));
                repository.close();
                RepositoryCache.close(repository);
            } finally {
                FileUtils.delete(new File(parent, repositoryAll), ((FileUtils.RECURSIVE) | (FileUtils.IGNORE_ERRORS)));
                FileUtils.delete(new File(parent, repositoryUmask), ((FileUtils.RECURSIVE) | (FileUtils.IGNORE_ERRORS)));
                if (!parentExisted) {
                    FileUtils.delete(parent, ((FileUtils.RECURSIVE) | (FileUtils.IGNORE_ERRORS)));
                }
            }
        }
    }

    @Test
    public void testRefs() throws Exception {
        Repository repository = GitBlitSuite.getJGitRepository();
        Map<ObjectId, List<RefModel>> map = JGitUtils.getAllRefs(repository);
        repository.close();
        Assert.assertTrue(((map.size()) > 0));
        for (Map.Entry<ObjectId, List<RefModel>> entry : map.entrySet()) {
            List<RefModel> list = entry.getValue();
            for (RefModel ref : list) {
                if (ref.displayName.equals("refs/tags/spearce-gpg-pub")) {
                    Assert.assertEquals("refs/tags/spearce-gpg-pub", ref.toString());
                    Assert.assertEquals("8bbde7aacf771a9afb6992434f1ae413e010c6d8", ref.getObjectId().getName());
                    Assert.assertEquals("spearce@spearce.org", ref.getAuthorIdent().getEmailAddress());
                    Assert.assertTrue(ref.getShortMessage().startsWith("GPG key"));
                    Assert.assertTrue(ref.getFullMessage().startsWith("GPG key"));
                    Assert.assertEquals(Constants.OBJ_BLOB, ref.getReferencedObjectType());
                } else
                    if (ref.displayName.equals("refs/tags/v0.12.1")) {
                        Assert.assertTrue(ref.isAnnotatedTag());
                    }

            }
        }
    }

    @Test
    public void testBranches() throws Exception {
        Repository repository = GitBlitSuite.getJGitRepository();
        Assert.assertTrue(((JGitUtils.getLocalBranches(repository, true, 0).size()) == 0));
        for (RefModel model : JGitUtils.getLocalBranches(repository, true, (-1))) {
            Assert.assertTrue(model.getName().startsWith(Constants.R_HEADS));
            Assert.assertTrue(model.equals(model));
            Assert.assertFalse(model.equals(""));
            Assert.assertTrue(((model.hashCode()) == ((model.getReferencedObjectId().hashCode()) + (model.getName().hashCode()))));
            Assert.assertTrue(model.getShortMessage().equals(model.getShortMessage()));
        }
        for (RefModel model : JGitUtils.getRemoteBranches(repository, true, (-1))) {
            Assert.assertTrue(model.getName().startsWith(Constants.R_REMOTES));
            Assert.assertTrue(model.equals(model));
            Assert.assertFalse(model.equals(""));
            Assert.assertTrue(((model.hashCode()) == ((model.getReferencedObjectId().hashCode()) + (model.getName().hashCode()))));
            Assert.assertTrue(model.getShortMessage().equals(model.getShortMessage()));
        }
        Assert.assertTrue(((JGitUtils.getRemoteBranches(repository, true, 8).size()) == 8));
        repository.close();
    }

    @Test
    public void testTags() throws Exception {
        Repository repository = GitBlitSuite.getJGitRepository();
        Assert.assertTrue(((JGitUtils.getTags(repository, true, 5).size()) == 5));
        for (RefModel model : JGitUtils.getTags(repository, true, (-1))) {
            if (model.getObjectId().getName().equals("d28091fb2977077471138fe97da1440e0e8ae0da")) {
                Assert.assertTrue("Not an annotated tag!", model.isAnnotatedTag());
            }
            Assert.assertTrue(model.getName().startsWith(Constants.R_TAGS));
            Assert.assertTrue(model.equals(model));
            Assert.assertFalse(model.equals(""));
            Assert.assertTrue(((model.hashCode()) == ((model.getReferencedObjectId().hashCode()) + (model.getName().hashCode()))));
        }
        repository.close();
        repository = GitBlitSuite.getGitectiveRepository();
        for (RefModel model : JGitUtils.getTags(repository, true, (-1))) {
            if (model.getObjectId().getName().equals("035254295a9bba11f72b1f9d6791a6b957abee7b")) {
                Assert.assertFalse(model.isAnnotatedTag());
                Assert.assertTrue(model.getAuthorIdent().getEmailAddress().equals("kevinsawicki@gmail.com"));
                Assert.assertEquals("Add scm and issue tracker elements to pom.xml\n", model.getFullMessage());
            }
        }
        repository.close();
    }

    @Test
    public void testCommitNotes() throws Exception {
        Repository repository = GitBlitSuite.getJGitRepository();
        RevCommit commit = JGitUtils.getCommit(repository, "690c268c793bfc218982130fbfc25870f292295e");
        List<GitNote> list = JGitUtils.getNotesOnCommit(repository, commit);
        repository.close();
        Assert.assertTrue(((list.size()) > 0));
        Assert.assertEquals("183474d554e6f68478a02d9d7888b67a9338cdff", list.get(0).notesRef.getReferencedObjectId().getName());
    }

    @Test
    public void testRelinkHEAD() throws Exception {
        Repository repository = GitBlitSuite.getJGitRepository();
        // confirm HEAD is master
        String currentRef = JGitUtils.getHEADRef(repository);
        Assert.assertEquals("refs/heads/master", currentRef);
        List<String> availableHeads = JGitUtils.getAvailableHeadTargets(repository);
        Assert.assertTrue(((availableHeads.size()) > 0));
        // set HEAD to stable-1.2
        JGitUtils.setHEADtoRef(repository, "refs/heads/stable-1.2");
        currentRef = JGitUtils.getHEADRef(repository);
        Assert.assertEquals("refs/heads/stable-1.2", currentRef);
        // restore HEAD to master
        JGitUtils.setHEADtoRef(repository, "refs/heads/master");
        currentRef = JGitUtils.getHEADRef(repository);
        Assert.assertEquals("refs/heads/master", currentRef);
        repository.close();
    }

    @Test
    public void testRelinkBranch() throws Exception {
        Repository repository = GitBlitSuite.getJGitRepository();
        // create/set the branch
        JGitUtils.setBranchRef(repository, "refs/heads/reftest", "3b358ce514ec655d3ff67de1430994d8428cdb04");
        Assert.assertEquals(1, JGitUtils.getAllRefs(repository).get(ObjectId.fromString("3b358ce514ec655d3ff67de1430994d8428cdb04")).size());
        Assert.assertEquals(null, JGitUtils.getAllRefs(repository).get(ObjectId.fromString("755dfdb40948f5c1ec79e06bde3b0a78c352f27f")));
        // reset the branch
        JGitUtils.setBranchRef(repository, "refs/heads/reftest", "755dfdb40948f5c1ec79e06bde3b0a78c352f27f");
        Assert.assertEquals(null, JGitUtils.getAllRefs(repository).get(ObjectId.fromString("3b358ce514ec655d3ff67de1430994d8428cdb04")));
        Assert.assertEquals(1, JGitUtils.getAllRefs(repository).get(ObjectId.fromString("755dfdb40948f5c1ec79e06bde3b0a78c352f27f")).size());
        // delete the branch
        Assert.assertTrue(JGitUtils.deleteBranchRef(repository, "refs/heads/reftest"));
        repository.close();
    }

    @Test
    public void testCreateOrphanedBranch() throws Exception {
        Repository repository = JGitUtils.createRepository(GitBlitSuite.REPOSITORIES, "orphantest");
        Assert.assertTrue(JGitUtils.createOrphanBranch(repository, ("x" + (Long.toHexString(System.currentTimeMillis()).toUpperCase())), null));
        FileUtils.delete(repository.getDirectory(), FileUtils.RECURSIVE);
    }

    @Test
    public void testStringContent() throws Exception {
        Repository repository = GitBlitSuite.getHelloworldRepository();
        String contentA = JGitUtils.getStringContent(repository, ((RevTree) (null)), "java.java");
        RevCommit commit = JGitUtils.getCommit(repository, Constants.HEAD);
        String contentB = JGitUtils.getStringContent(repository, commit.getTree(), "java.java");
        Assert.assertTrue("ContentA is null!", ((contentA != null) && ((contentA.length()) > 0)));
        Assert.assertTrue("ContentB is null!", ((contentB != null) && ((contentB.length()) > 0)));
        Assert.assertTrue(contentA.equals(contentB));
        String contentC = JGitUtils.getStringContent(repository, commit.getTree(), "missing.txt");
        // manually construct a blob, calculate the hash, lookup the hash in git
        StringBuilder sb = new StringBuilder();
        sb.append("blob ").append(contentA.length()).append('\u0000');
        sb.append(contentA);
        String sha1 = StringUtils.getSHA1(sb.toString());
        String contentD = JGitUtils.getStringContent(repository, sha1);
        repository.close();
        Assert.assertNull(contentC);
        Assert.assertTrue(contentA.equals(contentD));
    }

    @Test
    public void testFilesInCommit() throws Exception {
        Repository repository = GitBlitSuite.getHelloworldRepository();
        RevCommit commit = JGitUtils.getCommit(repository, "1d0c2933a4ae69c362f76797d42d6bd182d05176");
        List<PathChangeModel> paths = JGitUtils.getFilesInCommit(repository, commit);
        commit = JGitUtils.getCommit(repository, "af0e9b2891fda85afc119f04a69acf7348922830");
        List<PathChangeModel> deletions = JGitUtils.getFilesInCommit(repository, commit);
        commit = JGitUtils.getFirstCommit(repository, null);
        List<PathChangeModel> additions = JGitUtils.getFilesInCommit(repository, commit);
        List<PathChangeModel> latestChanges = JGitUtils.getFilesInCommit(repository, null);
        repository.close();
        Assert.assertTrue("No changed paths found!", ((paths.size()) == 1));
        for (PathChangeModel path : paths) {
            Assert.assertTrue("PathChangeModel hashcode incorrect!", ((path.hashCode()) == ((path.commitId.hashCode()) + (path.path.hashCode()))));
            Assert.assertTrue("PathChangeModel equals itself failed!", path.equals(path));
            Assert.assertFalse("PathChangeModel equals string failed!", path.equals(""));
        }
        Assert.assertEquals(DELETE, deletions.get(0).changeType);
        Assert.assertEquals(ADD, additions.get(0).changeType);
        Assert.assertTrue(((latestChanges.size()) > 0));
    }

    @Test
    public void testFilesInPath() throws Exception {
        Assert.assertEquals(0, JGitUtils.getFilesInPath(null, null, null).size());
        Repository repository = GitBlitSuite.getHelloworldRepository();
        List<PathModel> files = JGitUtils.getFilesInPath(repository, null, null);
        repository.close();
        Assert.assertTrue(((files.size()) > 10));
    }

    @Test
    public void testFilesInPath2() throws Exception {
        Assert.assertEquals(0, JGitUtils.getFilesInPath2(null, null, null).size());
        Repository repository = GitBlitSuite.getHelloworldRepository();
        List<PathModel> files = JGitUtils.getFilesInPath2(repository, null, null);
        repository.close();
        Assert.assertTrue(((files.size()) > 10));
    }

    @Test
    public void testDocuments() throws Exception {
        Repository repository = GitBlitSuite.getTicgitRepository();
        List<String> extensions = Arrays.asList(new String[]{ ".mkd", ".md" });
        List<PathModel> markdownDocs = JGitUtils.getDocuments(repository, extensions);
        List<PathModel> allFiles = JGitUtils.getDocuments(repository, null);
        repository.close();
        Assert.assertTrue(((markdownDocs.size()) > 0));
        Assert.assertTrue(((allFiles.size()) > (markdownDocs.size())));
    }

    @Test
    public void testFileModes() throws Exception {
        Assert.assertEquals("drwxr-xr-x", JGitUtils.getPermissionsFromMode(FileMode.TREE.getBits()));
        Assert.assertEquals("-rw-r--r--", JGitUtils.getPermissionsFromMode(FileMode.REGULAR_FILE.getBits()));
        Assert.assertEquals("-rwxr-xr-x", JGitUtils.getPermissionsFromMode(FileMode.EXECUTABLE_FILE.getBits()));
        Assert.assertEquals("symlink", JGitUtils.getPermissionsFromMode(FileMode.SYMLINK.getBits()));
        Assert.assertEquals("submodule", JGitUtils.getPermissionsFromMode(FileMode.GITLINK.getBits()));
        Assert.assertEquals("missing", JGitUtils.getPermissionsFromMode(FileMode.MISSING.getBits()));
    }

    @Test
    public void testRevlog() throws Exception {
        Assert.assertTrue(((JGitUtils.getRevLog(null, 0).size()) == 0));
        List<RevCommit> commits = JGitUtils.getRevLog(null, 10);
        Assert.assertEquals(0, commits.size());
        Repository repository = GitBlitSuite.getHelloworldRepository();
        // get most recent 10 commits
        commits = JGitUtils.getRevLog(repository, 10);
        Assert.assertEquals(10, commits.size());
        // test paging and offset by getting the 10th most recent commit
        RevCommit lastCommit = JGitUtils.getRevLog(repository, null, 9, 1).get(0);
        Assert.assertEquals(lastCommit, commits.get(9));
        // grab the two most recent commits to java.java
        commits = JGitUtils.getRevLog(repository, null, "java.java", 0, 2);
        Assert.assertEquals(2, commits.size());
        // grab the commits since 2008-07-15
        commits = JGitUtils.getRevLog(repository, null, new SimpleDateFormat("yyyy-MM-dd").parse("2008-07-15"));
        Assert.assertEquals(12, commits.size());
        repository.close();
    }

    @Test
    public void testRevLogRange() throws Exception {
        Repository repository = GitBlitSuite.getHelloworldRepository();
        List<RevCommit> commits = JGitUtils.getRevLog(repository, "fbd14fa6d1a01d4aefa1fca725792683800fc67e", "85a0e4087b8439c0aa6b1f4f9e08c26052ab7e87");
        repository.close();
        Assert.assertEquals(14, commits.size());
    }

    @Test
    public void testSearchTypes() throws Exception {
        Assert.assertEquals(COMMIT, SearchType.forName("commit"));
        Assert.assertEquals(COMMITTER, SearchType.forName("committer"));
        Assert.assertEquals(AUTHOR, SearchType.forName("author"));
        Assert.assertEquals(COMMIT, SearchType.forName("unknown"));
        Assert.assertEquals("commit", COMMIT.toString());
        Assert.assertEquals("committer", COMMITTER.toString());
        Assert.assertEquals("author", AUTHOR.toString());
    }

    @Test
    public void testSearchRevlogs() throws Exception {
        Assert.assertEquals(0, JGitUtils.searchRevlogs(null, null, "java", COMMIT, 0, 0).size());
        List<RevCommit> results = JGitUtils.searchRevlogs(null, null, "java", COMMIT, 0, 3);
        Assert.assertEquals(0, results.size());
        // test commit message search
        Repository repository = GitBlitSuite.getHelloworldRepository();
        results = JGitUtils.searchRevlogs(repository, null, "java", COMMIT, 0, 3);
        Assert.assertEquals(3, results.size());
        // test author search
        results = JGitUtils.searchRevlogs(repository, null, "timothy", AUTHOR, 0, (-1));
        Assert.assertEquals(1, results.size());
        // test committer search
        results = JGitUtils.searchRevlogs(repository, null, "mike", COMMITTER, 0, 10);
        Assert.assertEquals(10, results.size());
        // test paging and offset
        RevCommit commit = JGitUtils.searchRevlogs(repository, null, "mike", COMMITTER, 9, 1).get(0);
        Assert.assertEquals(results.get(9), commit);
        repository.close();
    }

    @Test
    public void testZip() throws Exception {
        Assert.assertFalse(CompressionUtils.zip(null, null, null, null, null));
        Repository repository = GitBlitSuite.getHelloworldRepository();
        File zipFileA = new File(GitBlitSuite.REPOSITORIES, "helloworld.zip");
        FileOutputStream fosA = new FileOutputStream(zipFileA);
        boolean successA = CompressionUtils.zip(repository, null, null, Constants.HEAD, fosA);
        fosA.close();
        File zipFileB = new File(GitBlitSuite.REPOSITORIES, "helloworld-java.zip");
        FileOutputStream fosB = new FileOutputStream(zipFileB);
        boolean successB = CompressionUtils.zip(repository, null, "java.java", Constants.HEAD, fosB);
        fosB.close();
        repository.close();
        Assert.assertTrue("Failed to generate zip file!", successA);
        Assert.assertTrue(((zipFileA.length()) > 0));
        zipFileA.delete();
        Assert.assertTrue("Failed to generate zip file!", successB);
        Assert.assertTrue(((zipFileB.length()) > 0));
        zipFileB.delete();
    }

    @Test
    public void testPlots() throws Exception {
        Repository repository = GitBlitSuite.getTicgitRepository();
        PlotWalk pw = new PlotWalk(repository);
        PlotCommitList<PlotLane> commits = new PlotCommitList<PlotLane>();
        commits.source(pw);
        commits.fillTo(25);
        for (PlotCommit<PlotLane> commit : commits) {
            System.out.println(commit);
        }
        repository.close();
    }
}

