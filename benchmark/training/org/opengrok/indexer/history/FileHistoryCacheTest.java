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
 * Copyright (c) 2014, 2018, Oracle and/or its affiliates. All rights reserved.
 * Portions Copyright (c) 2018, Chris Fraire <cfraire@me.com>.
 */
package org.opengrok.indexer.history;


import java.io.File;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.opengrok.indexer.condition.ConditionalRun;
import org.opengrok.indexer.condition.ConditionalRunRule;
import org.opengrok.indexer.condition.RepositoryInstalled;
import org.opengrok.indexer.condition.UnixPresent;
import org.opengrok.indexer.configuration.RuntimeEnvironment;
import org.opengrok.indexer.util.TestRepository;


/**
 * Test file based history cache with special focus on incremental reindex.
 *
 * @author Vladimir Kotal
 */
public class FileHistoryCacheTest {
    private TestRepository repositories;

    private FileHistoryCache cache;

    @Rule
    public ConditionalRunRule rule = new ConditionalRunRule();

    /**
     * Basic tests for the {@code store()} method on cache with disabled
     * handling of renamed files.
     */
    @ConditionalRun(RepositoryInstalled.MercurialInstalled.class)
    @Test
    public void testStoreAndGetNotRenamed() throws Exception {
        File reposRoot = new File(repositories.getSourceRoot(), "mercurial");
        Repository repo = RepositoryFactory.getRepository(reposRoot);
        History historyToStore = repo.getHistory(reposRoot);
        cache.store(historyToStore, repo);
        // This makes sure that the file which contains the latest revision
        // has indeed been created.
        Assert.assertEquals("9:8b340409b3a8", cache.getLatestCachedRevision(repo));
        // test reindex
        History historyNull = new History();
        cache.store(historyNull, repo);
        Assert.assertEquals("9:8b340409b3a8", cache.getLatestCachedRevision(repo));
    }

    /**
     * Test tagging by creating history cache for repository with one tag and
     * then importing couple of changesets which add both file changes and tags.
     * The last history entry before the import is important as it needs to be
     * retagged when old history is merged with the new one.
     */
    @ConditionalRun(RepositoryInstalled.MercurialInstalled.class)
    @Test
    public void testStoreAndGetIncrementalTags() throws Exception {
        // Enable tagging of history entries.
        RuntimeEnvironment.getInstance().setTagsEnabled(true);
        File reposRoot = new File(repositories.getSourceRoot(), "mercurial");
        Repository repo = RepositoryFactory.getRepository(reposRoot);
        History historyToStore = repo.getHistory(reposRoot);
        // Store the history.
        cache.store(historyToStore, repo);
        // Add bunch of changesets with file based changes and tags.
        MercurialRepositoryTest.runHgCommand(reposRoot, "import", Paths.get(getClass().getResource("/history/hg-export-tag.txt").toURI()).toString());
        // Perform incremental reindex.
        repo.createCache(cache, cache.getLatestCachedRevision(repo));
        // Check that the changesets were indeed applied and indexed.
        History updatedHistory = cache.get(reposRoot, repo, true);
        Assert.assertEquals("Unexpected number of history entries", 15, updatedHistory.getHistoryEntries().size());
        // Verify tags in fileHistory for main.c which is the most interesting
        // file from the repository from the perspective of tags.
        File main = new File(reposRoot, "main.c");
        Assert.assertTrue(main.exists());
        History retrievedHistoryMainC = cache.get(main, repo, true);
        List<HistoryEntry> entries = retrievedHistoryMainC.getHistoryEntries();
        Assert.assertEquals("Unexpected number of entries for main.c", 3, entries.size());
        HistoryEntry e0 = entries.get(0);
        Assert.assertEquals("Unexpected revision for entry 0", "13:3d386f6bd848", e0.getRevision());
        Assert.assertEquals("Invalid tag list for revision 13", "tag3", e0.getTags());
        HistoryEntry e1 = entries.get(1);
        Assert.assertEquals("Unexpected revision for entry 1", "2:585a1b3f2efb", e1.getRevision());
        Assert.assertEquals("Invalid tag list for revision 2", "tag2, tag1, start_of_novel", e1.getTags());
        HistoryEntry e2 = entries.get(2);
        Assert.assertEquals("Unexpected revision for entry 2", "1:f24a5fd7a85d", e2.getRevision());
        Assert.assertEquals("Invalid tag list for revision 1", null, e2.getTags());
        // Reindex from scratch.
        File dir = new File(cache.getRepositoryHistDataDirname(repo));
        Assert.assertTrue(dir.isDirectory());
        cache.clear(repo);
        // We cannot call cache.get() here since it would read the history anew.
        // Instead check that the data directory does not exist anymore.
        Assert.assertFalse(dir.exists());
        History freshHistory = repo.getHistory(reposRoot);
        cache.store(freshHistory, repo);
        History updatedHistoryFromScratch = cache.get(reposRoot, repo, true);
        Assert.assertEquals("Unexpected number of history entries", freshHistory.getHistoryEntries().size(), updatedHistoryFromScratch.getHistoryEntries().size());
        // Verify that the result for the directory is the same as incremental
        // reindex.
        assertSameEntries(updatedHistory.getHistoryEntries(), updatedHistoryFromScratch.getHistoryEntries(), true);
        // Do the same for main.c.
        History retrievedUpdatedHistoryMainC = cache.get(main, repo, true);
        assertSameEntries(retrievedHistoryMainC.getHistoryEntries(), retrievedUpdatedHistoryMainC.getHistoryEntries(), false);
    }

    /**
     * Basic tests for the {@code store()} and {@code get()} methods.
     */
    @ConditionalRun(UnixPresent.class)
    @ConditionalRun(RepositoryInstalled.MercurialInstalled.class)
    @Test
    public void testStoreAndGet() throws Exception {
        File reposRoot = new File(repositories.getSourceRoot(), "mercurial");
        // The test expects support for renamed files.
        RuntimeEnvironment.getInstance().setHandleHistoryOfRenamedFiles(true);
        Repository repo = RepositoryFactory.getRepository(reposRoot);
        History historyToStore = repo.getHistory(reposRoot);
        cache.store(historyToStore, repo);
        // test reindex
        History historyNull = new History();
        cache.store(historyNull, repo);
        // test get history for single file
        File makefile = new File(reposRoot, "Makefile");
        Assert.assertTrue(makefile.exists());
        History retrievedHistory = cache.get(makefile, repo, true);
        List<HistoryEntry> entries = retrievedHistory.getHistoryEntries();
        Assert.assertEquals("Unexpected number of entries", 2, entries.size());
        final String TROND = "Trond Norbye <trond.norbye@sun.com>";
        Iterator<HistoryEntry> entryIt = entries.iterator();
        HistoryEntry e1 = entryIt.next();
        Assert.assertEquals(TROND, e1.getAuthor());
        Assert.assertEquals("2:585a1b3f2efb", e1.getRevision());
        Assert.assertEquals(0, e1.getFiles().size());
        HistoryEntry e2 = entryIt.next();
        Assert.assertEquals(TROND, e2.getAuthor());
        Assert.assertEquals("1:f24a5fd7a85d", e2.getRevision());
        Assert.assertEquals(0, e2.getFiles().size());
        Assert.assertFalse(entryIt.hasNext());
        // test get history for renamed file
        File novel = new File(reposRoot, "novel.txt");
        Assert.assertTrue(novel.exists());
        retrievedHistory = cache.get(novel, repo, true);
        entries = retrievedHistory.getHistoryEntries();
        Assert.assertEquals("Unexpected number of entries", 6, entries.size());
        // test get history for directory
        // Need to refresh history to store since the file lists were stripped
        // from it in the call to cache.store() above.
        historyToStore = repo.getHistory(reposRoot);
        History dirHistory = cache.get(reposRoot, repo, true);
        assertSameEntries(historyToStore.getHistoryEntries(), dirHistory.getHistoryEntries(), true);
        // test incremental update
        MercurialRepositoryTest.runHgCommand(reposRoot, "import", Paths.get(getClass().getResource("/history/hg-export.txt").toURI()).toString());
        repo.createCache(cache, cache.getLatestCachedRevision(repo));
        History updatedHistory = cache.get(reposRoot, repo, true);
        HistoryEntry newEntry1 = // whole minutes only
        new HistoryEntry("10:1e392ef0b0ed", new Date((((1245446973L / 60) * 60) * 1000)), "xyz", null, "Return failure when executed with no arguments", true);
        newEntry1.addFile("/mercurial/main.c");
        HistoryEntry newEntry2 = // whole minutes only
        new HistoryEntry("11:bbb3ce75e1b8", new Date((((1245447973L / 60) * 60) * 1000)), "xyz", null, "Do something else", true);
        newEntry2.addFile("/mercurial/main.c");
        LinkedList<HistoryEntry> updatedEntries = new LinkedList(updatedHistory.getHistoryEntries());
        // The history for retrieved for the whole directory so it will contain
        // lists of files so we need to set isdir to true.
        assertSameEntry(newEntry2, updatedEntries.removeFirst(), true);
        assertSameEntry(newEntry1, updatedEntries.removeFirst(), true);
        assertSameEntries(historyToStore.getHistoryEntries(), updatedEntries, true);
        // test clearing of cache
        File dir = new File(cache.getRepositoryHistDataDirname(repo));
        Assert.assertTrue(dir.isDirectory());
        cache.clear(repo);
        // We cannot call cache.get() here since it would read the history anew.
        // Instead check that the data directory does not exist anymore.
        Assert.assertFalse(dir.exists());
        cache.store(historyToStore, repo);
        // check that the data directory is non-empty
        Assert.assertEquals(true, ((dir.list().length) > 0));
        updatedHistory = cache.get(reposRoot, repo, true);
        assertSameEntries(updatedHistory.getHistoryEntries(), cache.get(reposRoot, repo, true).getHistoryEntries(), true);
    }

    /**
     * Check how incremental reindex behaves when indexing changesets that
     * rename+change file.
     *
     * The scenario goes as follows:
     * - create Mercurial repository
     * - perform full reindex
     * - add changesets which renamed and modify a file
     * - perform incremental reindex
     * - change+rename the file again
     * - incremental reindex
     */
    @ConditionalRun(UnixPresent.class)
    @ConditionalRun(RepositoryInstalled.MercurialInstalled.class)
    @Test
    public void testRenameFileThenDoIncrementalReindex() throws Exception {
        File reposRoot = new File(repositories.getSourceRoot(), "mercurial");
        History updatedHistory;
        // The test expects support for renamed files.
        RuntimeEnvironment.getInstance().setHandleHistoryOfRenamedFiles(true);
        // Use tags for better coverage.
        RuntimeEnvironment.getInstance().setTagsEnabled(true);
        // Generate history index.
        // It is necessary to call getRepository() only after tags were enabled
        // to produce list of tags.
        Repository repo = RepositoryFactory.getRepository(reposRoot);
        History historyToStore = repo.getHistory(reposRoot);
        cache.store(historyToStore, repo);
        // Import changesets which rename one of the files in the repository.
        MercurialRepositoryTest.runHgCommand(reposRoot, "import", Paths.get(getClass().getResource("/history/hg-export-renamed.txt").toURI()).toString());
        // Perform incremental reindex.
        repo.createCache(cache, cache.getLatestCachedRevision(repo));
        // Verify size of complete history for the directory.
        updatedHistory = cache.get(reposRoot, repo, true);
        Assert.assertEquals(14, updatedHistory.getHistoryEntries().size());
        // Check changesets for the renames and changes of single file.
        File main2File = new File((((reposRoot.toString()) + (File.separatorChar)) + "main2.c"));
        updatedHistory = cache.get(main2File, repo, false);
        // Changesets e0-e3 were brought in by the import done above.
        HistoryEntry e0 = // whole minutes only
        new HistoryEntry("13:e55a793086da", new Date((((1245447973L / 60) * 60) * 1000)), "xyz", null, "Do something else", true);
        HistoryEntry e1 = // whole minutes only
        new HistoryEntry("12:97b5392fec0d", new Date((((1393515253L / 60) * 60) * 1000)), "Vladimir Kotal <Vladimir.Kotal@oracle.com>", null, "rename2", true);
        HistoryEntry e2 = // whole minutes only
        new HistoryEntry("11:5c203a0bc12b", new Date((((1393515291L / 60) * 60) * 1000)), "Vladimir Kotal <Vladimir.Kotal@oracle.com>", null, "rename1", true);
        HistoryEntry e3 = // whole minutes only
        new HistoryEntry("10:1e392ef0b0ed", new Date((((1245446973L / 60) * 60) * 1000)), "xyz", null, "Return failure when executed with no arguments", true);
        HistoryEntry e4 = // whole minutes only
        new HistoryEntry("2:585a1b3f2efb", new Date((((1218571989L / 60) * 60) * 1000)), "Trond Norbye <trond.norbye@sun.com>", "start_of_novel", "Add lint make target and fix lint warnings", true);
        HistoryEntry e5 = // whole minutes only
        new HistoryEntry("1:f24a5fd7a85d", new Date((((1218571413L / 60) * 60) * 1000)), "Trond Norbye <trond.norbye@sun.com>", null, "Created a small dummy program", true);
        History histConstruct = new History();
        LinkedList<HistoryEntry> entriesConstruct = new LinkedList<>();
        entriesConstruct.add(e0);
        entriesConstruct.add(e1);
        entriesConstruct.add(e2);
        entriesConstruct.add(e3);
        entriesConstruct.add(e4);
        entriesConstruct.add(e5);
        histConstruct.setHistoryEntries(entriesConstruct);
        Assert.assertEquals(6, updatedHistory.getHistoryEntries().size());
        assertSameEntries(histConstruct.getHistoryEntries(), updatedHistory.getHistoryEntries(), false);
        // Add some changes and rename the file again.
        MercurialRepositoryTest.runHgCommand(reposRoot, "import", Paths.get(getClass().getResource("/history/hg-export-renamed-again.txt").toURI()).toString());
        // Perform incremental reindex.
        repo.createCache(cache, cache.getLatestCachedRevision(repo));
        HistoryEntry e6 = // whole minutes only
        new HistoryEntry("14:55c41cd4b348", new Date((((1489505558L / 60) * 60) * 1000)), "Vladimir Kotal <Vladimir.Kotal@oracle.com>", null, "rename + cstyle", true);
        entriesConstruct = new LinkedList();
        entriesConstruct.add(e6);
        entriesConstruct.add(e0);
        entriesConstruct.add(e1);
        entriesConstruct.add(e2);
        entriesConstruct.add(e3);
        entriesConstruct.add(e4);
        entriesConstruct.add(e5);
        histConstruct.setHistoryEntries(entriesConstruct);
        // Check changesets for the renames and changes of single file.
        File main3File = new File((((reposRoot.toString()) + (File.separatorChar)) + "main3.c"));
        updatedHistory = cache.get(main3File, repo, false);
        Assert.assertEquals(7, updatedHistory.getHistoryEntries().size());
        assertSameEntries(histConstruct.getHistoryEntries(), updatedHistory.getHistoryEntries(), false);
    }

    /**
     * Make sure generating incremental history index in branched repository
     * with renamed file produces correct history for the renamed file
     * (i.e. there should not be history entries from the default branch made
     * there after the branch was created).
     */
    @ConditionalRun(UnixPresent.class)
    @ConditionalRun(RepositoryInstalled.MercurialInstalled.class)
    @Test
    public void testRenamedFilePlusChangesBranched() throws Exception {
        File reposRoot = new File(repositories.getSourceRoot(), "mercurial");
        History updatedHistory;
        // The test expects support for renamed files.
        RuntimeEnvironment.getInstance().setHandleHistoryOfRenamedFiles(true);
        // Use tags for better coverage.
        RuntimeEnvironment.getInstance().setTagsEnabled(true);
        // Branch the repo and add one changeset.
        MercurialRepositoryTest.runHgCommand(reposRoot, "unbundle", Paths.get(getClass().getResource("/history/hg-branch.bundle").toURI()).toString());
        // Import changesets which rename one of the files in the default branch.
        MercurialRepositoryTest.runHgCommand(reposRoot, "import", Paths.get(getClass().getResource("/history/hg-export-renamed.txt").toURI()).toString());
        // Switch to the newly created branch.
        MercurialRepositoryTest.runHgCommand(reposRoot, "update", "mybranch");
        // Generate history index.
        // It is necessary to call getRepository() only after tags were enabled
        // to produce list of tags.
        Repository repo = RepositoryFactory.getRepository(reposRoot);
        History historyToStore = repo.getHistory(reposRoot);
        cache.store(historyToStore, repo);
        /* quick sanity check */
        updatedHistory = cache.get(reposRoot, repo, true);
        Assert.assertEquals(11, updatedHistory.getHistoryEntries().size());
        // Import changesets which rename the file in the new branch.
        MercurialRepositoryTest.runHgCommand(reposRoot, "import", Paths.get(getClass().getResource("/history/hg-export-renamed-branched.txt").toURI()).toString());
        // Perform incremental reindex.
        repo.createCache(cache, cache.getLatestCachedRevision(repo));
        /* overall history check */
        updatedHistory = cache.get(reposRoot, repo, false);
        Assert.assertEquals(12, updatedHistory.getHistoryEntries().size());
        // Check complete list of history entries for the renamed file.
        File testFile = new File((((reposRoot.toString()) + (File.separatorChar)) + "blog.txt"));
        updatedHistory = cache.get(testFile, repo, false);
        HistoryEntry e0 = // whole minutes only
        new HistoryEntry("15:709c7a27f9fa", new Date((((1489160275L / 60) * 60) * 1000)), "Vladimir Kotal <Vladimir.Kotal@oracle.com>", null, "novels are so last century. Let's write a blog !", true);
        HistoryEntry e1 = // whole minutes only
        new HistoryEntry("10:c4518ca0c841", new Date((((1415483555L / 60) * 60) * 1000)), "Vladimir Kotal <Vladimir.Kotal@oracle.com>", null, "branched", true);
        HistoryEntry e2 = // whole minutes only
        new HistoryEntry("8:6a8c423f5624", new Date((((1362586899L / 60) * 60) * 1000)), "Vladimir Kotal <vlada@devnull.cz>", null, "first words of the novel", true);
        HistoryEntry e3 = // whole minutes only
        new HistoryEntry("7:db1394c05268", new Date((((1362586862L / 60) * 60) * 1000)), "Vladimir Kotal <vlada@devnull.cz>", "start_of_novel", "book sounds too boring, let's do a novel !", true);
        HistoryEntry e4 = // whole minutes only
        new HistoryEntry("6:e386b51ddbcc", new Date((((1362586839L / 60) * 60) * 1000)), "Vladimir Kotal <vlada@devnull.cz>", null, "stub of chapter 1", true);
        HistoryEntry e5 = // whole minutes only
        new HistoryEntry("5:8706402863c6", new Date((((1362586805L / 60) * 60) * 1000)), "Vladimir Kotal <vlada@devnull.cz>", null, "I decided to actually start writing a book based on the first plaintext file.", true);
        HistoryEntry e6 = // whole minutes only
        new HistoryEntry("4:e494d67af12f", new Date((((1362586747L / 60) * 60) * 1000)), "Vladimir Kotal <vlada@devnull.cz>", null, "first change", true);
        HistoryEntry e7 = // whole minutes only
        new HistoryEntry("3:2058725c1470", new Date((((1362586483L / 60) * 60) * 1000)), "Vladimir Kotal <vlada@devnull.cz>", null, "initial checking of text files", true);
        History histConstruct = new History();
        LinkedList<HistoryEntry> entriesConstruct = new LinkedList<>();
        entriesConstruct.add(e0);
        entriesConstruct.add(e1);
        entriesConstruct.add(e2);
        entriesConstruct.add(e3);
        entriesConstruct.add(e4);
        entriesConstruct.add(e5);
        entriesConstruct.add(e6);
        entriesConstruct.add(e7);
        histConstruct.setHistoryEntries(entriesConstruct);
        assertSameEntries(histConstruct.getHistoryEntries(), updatedHistory.getHistoryEntries(), false);
    }

    /* Functional test for the FetchHistoryWhenNotInCache configuration option. */
    @ConditionalRun(RepositoryInstalled.MercurialInstalled.class)
    @ConditionalRun(RepositoryInstalled.SCCSInstalled.class)
    @Test
    public void testNoHistoryFetch() throws Exception {
        // Do not create history cache for files which do not have it cached.
        RuntimeEnvironment.getInstance().setFetchHistoryWhenNotInCache(false);
        // Make cache.get() predictable. Normally when the retrieval of
        // history of given file is faster than the limit, the history of this
        // file is not stored. For the sake of this test we want the history
        // to be always stored.
        RuntimeEnvironment.getInstance().setHistoryReaderTimeLimit(0);
        // Pretend we are done with first phase of indexing.
        cache.setHistoryIndexDone();
        // First try repo with ability to fetch history for directories.
        checkNoHistoryFetchRepo("mercurial", "main.c", false, false);
        // Second try repo which can fetch history of individual files only.
        checkNoHistoryFetchRepo("teamware", "header.h", true, true);
    }
}

