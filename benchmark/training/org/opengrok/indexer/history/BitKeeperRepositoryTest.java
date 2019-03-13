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
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 */
package org.opengrok.indexer.history;


import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.opengrok.indexer.condition.ConditionalRun;
import org.opengrok.indexer.condition.ConditionalRunRule;
import org.opengrok.indexer.condition.RepositoryInstalled;
import org.opengrok.indexer.util.TestRepository;


/**
 * Tests for BitKeeperRepository.
 *
 * @author James Service &lt;jas2701@googlemail.com&gt;
 */
@ConditionalRun(RepositoryInstalled.BitKeeperInstalled.class)
public class BitKeeperRepositoryTest {
    @Rule
    public ConditionalRunRule rule = new ConditionalRunRule();

    private TestRepository testRepo;

    private BitKeeperRepository bkRepo;

    private List<String> bkFiles;

    private class BitKeeperFilenameFilter implements FilenameFilter {
        @Override
        public boolean accept(File dir, String name) {
            return !((name.equals("BitKeeper")) || (name.equals(".bk")));
        }
    }

    @Test
    public void testGetHistory() throws Exception {
        Assert.assertNotNull("Couldn't read bitkeeper test repository.", bkRepo);
        for (final String bkFile : bkFiles) {
            final File file = new File(bkRepo.getDirectoryName(), bkFile);
            final History fullHistory = bkRepo.getHistory(file);
            final History partHistory = bkRepo.getHistory(file, "1.2");
            // I made sure that each file had a 1.2
            BitKeeperRepositoryTest.validateHistory(fullHistory);
            BitKeeperRepositoryTest.validateHistory(partHistory);
            // Passing 1.2 to get History should remove 1.1 and 1.2
            // revisions from each file, so check number of entries.
            Assert.assertEquals("Partial file history is wrong size", fullHistory.getHistoryEntries().size(), ((partHistory.getHistoryEntries().size()) + 2));
        }
    }

    @Test
    public void testGetHistoryInvalid() throws Exception {
        Assert.assertNotNull("Couldn't read bitkeeper test repository.", bkRepo);
        final File file = new File(bkRepo.getDirectoryName(), "fakename.cpp");
        boolean caughtFull = false;
        try {
            final History fullHistory = bkRepo.getHistory(file);
        } catch (final HistoryException e) {
            caughtFull = true;
        }
        Assert.assertTrue("No exception thrown by getHistory with fake file", caughtFull);
        boolean caughtPart = false;
        try {
            final History partHistory = bkRepo.getHistory(file, "1.2");
        } catch (final HistoryException e) {
            caughtPart = true;
        }
        Assert.assertTrue("No exception thrown by getHistory with fake file", caughtPart);
    }

    @Test
    public void testGetHistoryGet() throws Exception {
        Assert.assertNotNull("Couldn't read bitkeeper test repository.", bkRepo);
        for (final String bkFile : bkFiles) {
            final String currentVersion = BitKeeperRepositoryTest.readStream(bkRepo.getHistoryGet(bkRepo.getDirectoryName(), bkFile, "+"));
            final String firstVersion = BitKeeperRepositoryTest.readStream(bkRepo.getHistoryGet(bkRepo.getDirectoryName(), bkFile, "1.1"));
            Assert.assertNotNull("Nothing returned by getHistoryGet.", currentVersion);
            Assert.assertNotNull("Nothing returned by getHistoryGet.", firstVersion);
            Assert.assertThat("Files returned by getHistoryGet are incorrect.", currentVersion, CoreMatchers.not(CoreMatchers.equalTo(firstVersion)));
        }
    }

    @Test
    public void testGetHistoryGetInvalid() throws Exception {
        Assert.assertNotNull("Couldn't read bitkeeper test repository.", bkRepo);
        Assert.assertNull("Something returned by getHistoryGet with fake file", bkRepo.getHistoryGet(bkRepo.getDirectoryName(), "fakename.cpp", "+"));
        Assert.assertNull("Something returned by getHistoryGet with fake file", bkRepo.getHistoryGet(bkRepo.getDirectoryName(), "fakename.cpp", "1.1"));
    }

    @Test
    public void testAnnotation() throws Exception {
        Assert.assertNotNull("Couldn't read bitkeeper test repository.", bkRepo);
        for (final String bkFile : bkFiles) {
            final File file = new File(bkRepo.getDirectoryName(), bkFile);
            final Annotation currentVersion = bkRepo.annotate(file, "+");
            final Annotation firstVersion = bkRepo.annotate(file, "1.1");
            Assert.assertEquals("Wrong file returned by annotate.", currentVersion.getFilename(), file.getName());
            Assert.assertEquals("Wrong file returned by annotate.", firstVersion.getFilename(), file.getName());
            Assert.assertTrue("Incorrect revisions returned by annotate.", ((currentVersion.getRevisions().size()) > 1));
            Assert.assertTrue("Incorrect revisions returned by annotate.", ((firstVersion.getRevisions().size()) == 1));
        }
    }

    @Test
    public void testAnnotationInvalid() throws Exception {
        Assert.assertNotNull("Couldn't read bitkeeper test repository.", bkRepo);
        final File file = new File(bkRepo.getDirectoryName(), "fakename.cpp");
        boolean caughtCurrent = false;
        try {
            final Annotation currentVersion = bkRepo.annotate(file, "+");
        } catch (final IOException e) {
            caughtCurrent = true;
        }
        Assert.assertTrue("No exception thrown by annotate with fake file", caughtCurrent);
        boolean caughtFirst = false;
        try {
            final Annotation firstVersion = bkRepo.annotate(file, "1.1");
        } catch (final IOException e) {
            caughtFirst = true;
        }
        Assert.assertTrue("No exception thrown by annotate with fake file", caughtFirst);
    }
}

