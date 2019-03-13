/**
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License (the "License").
 * You may not use this file except in compliance with the License.
 *
 * See LICENSE.txt included in this distribution for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at LICENSE.txt.
 * If applicable, add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your own identifying
 * information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 */
/**
 * Copyright (c) 2009, 2018, Oracle and/or its affiliates. All rights reserved.
 * Portions Copyright (c) 2017, Chris Fraire <cfraire@me.com>.
 */
package org.opengrok.indexer.history;


import java.io.File;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.opengrok.indexer.condition.ConditionalRun;
import org.opengrok.indexer.condition.ConditionalRunRule;
import org.opengrok.indexer.condition.RepositoryInstalled;
import org.opengrok.indexer.util.TestRepository;


/**
 * Tests for MercurialRepository.
 */
@ConditionalRun(RepositoryInstalled.MercurialInstalled.class)
public class MercurialRepositoryTest {
    @Rule
    public ConditionalRunRule rule = new ConditionalRunRule();

    /**
     * Revision numbers present in the Mercurial test repository, in the order
     * they are supposed to be returned from getHistory(), that is latest
     * changeset first.
     */
    private static final String[] REVISIONS = new String[]{ "9:8b340409b3a8", "8:6a8c423f5624", "7:db1394c05268", "6:e386b51ddbcc", "5:8706402863c6", "4:e494d67af12f", "3:2058725c1470", "2:585a1b3f2efb", "1:f24a5fd7a85d", "0:816b6279ae9c" };

    // extra revisions for branch test
    private static final String[] REVISIONS_extra_branch = new String[]{ "10:c4518ca0c841" };

    // novel.txt (or its ancestors) existed only since revision 3
    private static final String[] REVISIONS_novel = new String[]{ "9:8b340409b3a8", "8:6a8c423f5624", "7:db1394c05268", "6:e386b51ddbcc", "5:8706402863c6", "4:e494d67af12f", "3:2058725c1470" };

    private TestRepository repository;

    @Test
    public void testGetHistory() throws Exception {
        setUpTestRepository();
        File root = new File(repository.getSourceRoot(), "mercurial");
        MercurialRepository mr = ((MercurialRepository) (RepositoryFactory.getRepository(root)));
        History hist = mr.getHistory(root);
        List<HistoryEntry> entries = hist.getHistoryEntries();
        Assert.assertEquals(MercurialRepositoryTest.REVISIONS.length, entries.size());
        for (int i = 0; i < (entries.size()); i++) {
            HistoryEntry e = entries.get(i);
            Assert.assertEquals(MercurialRepositoryTest.REVISIONS[i], e.getRevision());
            Assert.assertNotNull(e.getAuthor());
            Assert.assertNotNull(e.getDate());
            Assert.assertNotNull(e.getFiles());
            Assert.assertNotNull(e.getMessage());
        }
    }

    @Test
    public void testGetHistorySubdir() throws Exception {
        setUpTestRepository();
        File root = new File(repository.getSourceRoot(), "mercurial");
        // Add a subdirectory with some history.
        MercurialRepositoryTest.runHgCommand(root, "import", Paths.get(getClass().getResource("/history/hg-export-subdir.txt").toURI()).toString());
        MercurialRepository mr = ((MercurialRepository) (RepositoryFactory.getRepository(root)));
        History hist = mr.getHistory(new File(root, "subdir"));
        List<HistoryEntry> entries = hist.getHistoryEntries();
        Assert.assertEquals(1, entries.size());
    }

    /**
     * Test that subset of changesets can be extracted based on penultimate
     * revision number. This works for directories only.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testGetHistoryPartial() throws Exception {
        setUpTestRepository();
        File root = new File(repository.getSourceRoot(), "mercurial");
        MercurialRepository mr = ((MercurialRepository) (RepositoryFactory.getRepository(root)));
        // Get all but the oldest revision.
        History hist = mr.getHistory(root, MercurialRepositoryTest.REVISIONS[((MercurialRepositoryTest.REVISIONS.length) - 1)]);
        List<HistoryEntry> entries = hist.getHistoryEntries();
        Assert.assertEquals(((MercurialRepositoryTest.REVISIONS.length) - 1), entries.size());
        for (int i = 0; i < (entries.size()); i++) {
            HistoryEntry e = entries.get(i);
            Assert.assertEquals(MercurialRepositoryTest.REVISIONS[i], e.getRevision());
            Assert.assertNotNull(e.getAuthor());
            Assert.assertNotNull(e.getDate());
            Assert.assertNotNull(e.getFiles());
            Assert.assertNotNull(e.getMessage());
        }
    }

    /**
     * Test that history of branched repository contains changesets of the
     * default branch as well.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testGetHistoryBranch() throws Exception {
        setUpTestRepository();
        File root = new File(repository.getSourceRoot(), "mercurial");
        // Branch the repo and add one changeset.
        MercurialRepositoryTest.runHgCommand(root, "unbundle", Paths.get(getClass().getResource("/history/hg-branch.bundle").toURI()).toString());
        // Switch to the branch.
        MercurialRepositoryTest.runHgCommand(root, "update", "mybranch");
        // Since the above hg commands change the active branch the repository
        // needs to be initialized here so that its branch matches.
        MercurialRepository mr = ((MercurialRepository) (RepositoryFactory.getRepository(root)));
        // Get all revisions.
        History hist = mr.getHistory(root);
        List<HistoryEntry> entries = hist.getHistoryEntries();
        List<String> both = new ArrayList<>(((MercurialRepositoryTest.REVISIONS.length) + (MercurialRepositoryTest.REVISIONS_extra_branch.length)));
        Collections.addAll(both, MercurialRepositoryTest.REVISIONS_extra_branch);
        Collections.addAll(both, MercurialRepositoryTest.REVISIONS);
        String[] revs = both.toArray(new String[both.size()]);
        Assert.assertEquals(revs.length, entries.size());
        // Ideally we should check that the last revision is branched but
        // there is currently no provision for that in HistoryEntry object.
        for (int i = 0; i < (entries.size()); i++) {
            HistoryEntry e = entries.get(i);
            Assert.assertEquals(revs[i], e.getRevision());
            Assert.assertNotNull(e.getAuthor());
            Assert.assertNotNull(e.getDate());
            Assert.assertNotNull(e.getFiles());
            Assert.assertNotNull(e.getMessage());
        }
        // Get revisions starting with given changeset before the repo was branched.
        hist = mr.getHistory(root, "8:6a8c423f5624");
        entries = hist.getHistoryEntries();
        Assert.assertEquals(2, entries.size());
        Assert.assertEquals(MercurialRepositoryTest.REVISIONS_extra_branch[0], entries.get(0).getRevision());
        Assert.assertEquals(MercurialRepositoryTest.REVISIONS[0], entries.get(1).getRevision());
    }

    /**
     * Test that contents of last revision of a text file match expected content.
     *
     * @throws java.lang.Exception
     * 		
     */
    @Test
    public void testGetHistoryGet() throws Exception {
        setUpTestRepository();
        File root = new File(repository.getSourceRoot(), "mercurial");
        MercurialRepository mr = ((MercurialRepository) (RepositoryFactory.getRepository(root)));
        String exp_str = "This will be a first novel of mine.\n" + ((((("\n" + "Chapter 1.\n") + "\n") + "Let\'s write some words. It began like this:\n") + "\n") + "...\n");
        byte[] buffer = new byte[1024];
        InputStream input = mr.getHistoryGet(root.getCanonicalPath(), "novel.txt", MercurialRepositoryTest.REVISIONS[0]);
        Assert.assertNotNull(input);
        String str = "";
        int len;
        while ((len = input.read(buffer)) > 0) {
            str += new String(buffer, 0, len);
        } 
        Assert.assertNotSame(str.length(), 0);
        Assert.assertEquals(exp_str, str);
    }

    /**
     * Test that it is possible to get contents of multiple revisions of a file.
     *
     * @throws java.lang.Exception
     * 		
     */
    @Test
    public void testgetHistoryGetForAll() throws Exception {
        setUpTestRepository();
        File root = new File(repository.getSourceRoot(), "mercurial");
        MercurialRepository mr = ((MercurialRepository) (RepositoryFactory.getRepository(root)));
        for (String rev : MercurialRepositoryTest.REVISIONS_novel) {
            InputStream input = mr.getHistoryGet(root.getCanonicalPath(), "novel.txt", rev);
            Assert.assertNotNull(input);
        }
    }

    /**
     * Test that {@code getHistoryGet()} returns historical contents of renamed
     * file.
     *
     * @throws java.lang.Exception
     * 		
     */
    @Test
    public void testGetHistoryGetRenamed() throws Exception {
        setUpTestRepository();
        File root = new File(repository.getSourceRoot(), "mercurial");
        MercurialRepository mr = ((MercurialRepository) (RepositoryFactory.getRepository(root)));
        String exp_str = "This is totally plaintext file.\n";
        byte[] buffer = new byte[1024];
        /* In our test repository the file was renamed twice since 
        revision 3.
         */
        InputStream input = mr.getHistoryGet(root.getCanonicalPath(), "novel.txt", "3");
        assert input != null;
        int len = input.read(buffer);
        assert len != (-1);
        String str = new String(buffer, 0, len);
        assert (str.compareTo(exp_str)) == 0;
    }

    /**
     * Test that {@code getHistory()} throws an exception if the revision
     * argument doesn't match any of the revisions in the history.
     *
     * @throws java.lang.Exception
     * 		
     */
    @Test
    public void testGetHistoryWithNoSuchRevision() throws Exception {
        setUpTestRepository();
        File root = new File(repository.getSourceRoot(), "mercurial");
        MercurialRepository mr = ((MercurialRepository) (RepositoryFactory.getRepository(root)));
        // Get the sequence number and the hash from one of the revisions.
        String[] revisionParts = MercurialRepositoryTest.REVISIONS[1].split(":");
        Assert.assertEquals(2, revisionParts.length);
        int number = Integer.parseInt(revisionParts[0]);
        String hash = revisionParts[1];
        // Construct a revision identifier that doesn't exist.
        String constructedRevision = ((number + 1) + ":") + hash;
        try {
            mr.getHistory(root, constructedRevision);
            Assert.fail("getHistory() should have failed");
        } catch (HistoryException he) {
            String msg = he.getMessage();
            if ((msg != null) && (msg.contains("not found in the repository"))) {
                // expected exception, do nothing
            } else {
                // unexpected exception, rethrow it
                throw he;
            }
        }
    }
}

