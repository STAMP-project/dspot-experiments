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
 * Portions Copyright (c) 2017-2018, Chris Fraire <cfraire@me.com>.
 */
package org.opengrok.indexer.index;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.opengrok.indexer.condition.ConditionalRun;
import org.opengrok.indexer.condition.ConditionalRunRule;
import org.opengrok.indexer.condition.CtagsInstalled;
import org.opengrok.indexer.condition.RepositoryInstalled;
import org.opengrok.indexer.configuration.RuntimeEnvironment;
import org.opengrok.indexer.history.HistoryException;
import org.opengrok.indexer.history.HistoryGuru;
import org.opengrok.indexer.history.MercurialRepositoryTest;
import org.opengrok.indexer.history.RepositoryInfo;
import org.opengrok.indexer.util.IOUtils;
import org.opengrok.indexer.util.TestRepository;


/**
 * Test indexer w.r.t. repositories.
 *
 * @author Vladimir Kotal
 */
@ConditionalRun(CtagsInstalled.class)
public class IndexerRepoTest {
    @Rule
    public ConditionalRunRule rule = new ConditionalRunRule();

    private TestRepository repository;

    /**
     * Test it is possible to disable history per project.
     */
    @ConditionalRun(RepositoryInstalled.MercurialInstalled.class)
    @ConditionalRun(RepositoryInstalled.GitInstalled.class)
    @Test
    public void testPerProjectHistoryGlobalOn() throws IOException, HistoryException, IndexerException {
        testPerProjectHistory(true);
    }

    /**
     * Test it is possible to enable history per project.
     */
    @ConditionalRun(RepositoryInstalled.MercurialInstalled.class)
    @ConditionalRun(RepositoryInstalled.GitInstalled.class)
    @Test
    public void testPerProjectHistoryGlobalOff() throws IOException, HistoryException, IndexerException {
        testPerProjectHistory(false);
    }

    /**
     * Test that symlinked directories from source root get their relative
     * path set correctly in RepositoryInfo objects.
     */
    @ConditionalRun(RepositoryInstalled.MercurialInstalled.class)
    @Test
    public void testSymlinks() throws IOException, IndexerException {
        final String SYMLINK = "symlink";
        RuntimeEnvironment env = RuntimeEnvironment.getInstance();
        // Set source root to pristine directory so that there is only one
        // repository to deal with (which makes this faster and easier to write)
        // and clone the mercurial repository outside of the source root.
        Path realSource = Files.createTempDirectory("real");
        Path sourceRoot = Files.createTempDirectory("src");
        MercurialRepositoryTest.runHgCommand(sourceRoot.toFile(), "clone", (((repository.getSourceRoot()) + (File.separator)) + "mercurial"), realSource.toString());
        // Create symlink from source root to the real repository.
        String symlinkPath = ((sourceRoot.toString()) + (File.separator)) + SYMLINK;
        Files.createSymbolicLink(Paths.get(symlinkPath), realSource);
        // Use alternative source root.
        env.setSourceRoot(sourceRoot.toString());
        // Need to have history cache enabled in order to perform scan of repositories.
        env.setHistoryEnabled(true);
        // Normally the Indexer would add the symlink automatically.
        env.setAllowedSymlinks(new HashSet(Arrays.asList(symlinkPath)));
        // Do a rescan of the projects, and only that (we don't care about
        // the other aspects of indexing in this test case).
        // search for repositories
        // scan and add projects
        // don't create dictionary
        // subFiles - not needed since we don't list files
        Indexer.getInstance().prepareIndexer(env, true, true, false, null, null);// repositories - not needed when not refreshing history

        // Check the repository paths.
        List<RepositoryInfo> repos = env.getRepositories();
        Assert.assertEquals(repos.size(), 1);
        RepositoryInfo repo = repos.get(0);
        Assert.assertEquals(((File.separator) + SYMLINK), repo.getDirectoryNameRelative());
        String epath = ((sourceRoot.toString()) + (File.separator)) + SYMLINK;
        String apath = repo.getDirectoryName();
        Assert.assertTrue(((("Should match (with macOS leeway):\n" + epath) + "\nv.\n") + apath), ((epath.equals(apath)) || (apath.equals(("/private" + epath)))));
        // Check that history exists for a file in the repository.
        File repoRoot = new File(env.getSourceRootFile(), SYMLINK);
        File fileInRepo = new File(repoRoot, "main.c");
        Assert.assertTrue(fileInRepo.exists());
        Assert.assertTrue(HistoryGuru.getInstance().hasHistory(fileInRepo));
        Assert.assertTrue(HistoryGuru.getInstance().hasCacheForFile(fileInRepo));
        // cleanup
        IOUtils.removeRecursive(realSource);
        IOUtils.removeRecursive(sourceRoot);
    }

    /**
     * Test cleanup of renamed thread pool after indexing with -H.
     */
    @Test
    public void testMainWithH() {
        System.out.println("Generate index by using command line options with -H");
        RuntimeEnvironment env = RuntimeEnvironment.getInstance();
        String[] argv = new String[]{ "-S", "-H", "-s", repository.getSourceRoot(), "-d", repository.getDataRoot(), "-v", "-c", env.getCtags() };
        Indexer.main(argv);
        checkNumberOfThreads();
    }

    /**
     * Test cleanup of renamed thread pool after indexing without -H.
     */
    @Test
    public void testMainWithoutH() {
        System.out.println("Generate index by using command line options without -H");
        RuntimeEnvironment env = RuntimeEnvironment.getInstance();
        String[] argv = new String[]{ "-S", "-P", "-s", repository.getSourceRoot(), "-d", repository.getDataRoot(), "-v", "-c", env.getCtags() };
        Indexer.main(argv);
        checkNumberOfThreads();
    }
}

