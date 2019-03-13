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
 * Copyright (c) 2010, 2018, Oracle and/or its affiliates. All rights reserved.
 * Portions Copyright (c) 2018, Chris Fraire <cfraire@me.com>.
 */
package org.opengrok.indexer.index;


import QueryBuilder.PATH;
import java.io.File;
import java.io.IOException;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.ScoreDoc;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.opengrok.indexer.analysis.Definitions;
import org.opengrok.indexer.condition.ConditionalRun;
import org.opengrok.indexer.condition.ConditionalRunRule;
import org.opengrok.indexer.condition.CtagsInstalled;
import org.opengrok.indexer.configuration.Project;
import org.opengrok.indexer.search.SearchEngine;
import org.opengrok.indexer.util.TestRepository;


/**
 * Unit tests for the {@code IndexDatabase} class.
 */
@ConditionalRun(CtagsInstalled.class)
public class IndexDatabaseTest {
    private static TestRepository repository;

    @ClassRule
    public static ConditionalRunRule rule = new ConditionalRunRule();

    @Test
    public void testGetDefinitions() throws Exception {
        // Test that we can get definitions for one of the files in the
        // repository.
        File f1 = new File(((IndexDatabaseTest.repository.getSourceRoot()) + "/git/main.c"));
        Definitions defs1 = IndexDatabase.getDefinitions(f1);
        Assert.assertNotNull(defs1);
        Assert.assertTrue(defs1.hasSymbol("main"));
        Assert.assertTrue(defs1.hasSymbol("argv"));
        Assert.assertFalse(defs1.hasSymbol("b"));
        Assert.assertTrue(defs1.hasDefinitionAt("main", 3, new String[1]));
        // same for windows delimiters
        f1 = new File(((IndexDatabaseTest.repository.getSourceRoot()) + "\\git\\main.c"));
        defs1 = IndexDatabase.getDefinitions(f1);
        Assert.assertNotNull(defs1);
        Assert.assertTrue(defs1.hasSymbol("main"));
        Assert.assertTrue(defs1.hasSymbol("argv"));
        Assert.assertFalse(defs1.hasSymbol("b"));
        Assert.assertTrue(defs1.hasDefinitionAt("main", 3, new String[1]));
        // Test that we get null back if we request definitions for a file
        // that's not in the repository.
        File f2 = new File(((IndexDatabaseTest.repository.getSourceRoot()) + "/git/foobar.d"));
        Definitions defs2 = IndexDatabase.getDefinitions(f2);
        Assert.assertNull(defs2);
    }

    /**
     * Test removal of IndexDatabase. xrefs and history index entries after
     * file has been removed from a repository.
     */
    @Test
    public void testCleanupAfterIndexRemoval() throws Exception {
        final int origNumFiles;
        String projectName = "git";
        String ppath = "/" + projectName;
        Project project = new Project(projectName, ppath);
        IndexDatabase idb = new IndexDatabase(project);
        Assert.assertNotNull(idb);
        // Note that the file to remove has to be different than the one used
        // in {@code testGetDefinitions} because it shares the same index
        // and this test is going to remove the file and therefore related
        // definitions.
        String fileName = "header.h";
        File gitRoot = new File(IndexDatabaseTest.repository.getSourceRoot(), projectName);
        Assert.assertTrue(new File(gitRoot, fileName).exists());
        // Check that the file was indexed successfully in terms of generated data.
        checkDataExistence(((projectName + (File.separator)) + fileName), true);
        origNumFiles = idb.getNumFiles();
        Assert.assertEquals(7, origNumFiles);
        // Remove the file and reindex using IndexDatabase directly.
        File file = new File(IndexDatabaseTest.repository.getSourceRoot(), ((projectName + (File.separator)) + fileName));
        file.delete();
        Assert.assertFalse((("file " + fileName) + " not removed"), file.exists());
        idb.update();
        // Check that the data for the file has been removed.
        checkDataExistence(((projectName + (File.separator)) + fileName), false);
        Assert.assertEquals((origNumFiles - 1), idb.getNumFiles());
    }

    /**
     * This is a test of {@code populateDocument} so it should be rather in {@code AnalyzerGuruTest}
     * however it lacks the pre-requisite indexing phase.
     */
    @Test
    public void testIndexPath() throws IOException {
        SearchEngine instance = new SearchEngine();
        // Use as broad search as possible.
        instance.setFile("c");
        instance.search();
        ScoreDoc[] scoredocs = instance.scoreDocs();
        Assert.assertTrue("need some search hits to perform the check", ((scoredocs.length) > 0));
        for (ScoreDoc sd : scoredocs) {
            Document doc = instance.doc(sd.doc);
            Assert.assertFalse("PATH field should not contain backslash characters", doc.getField(PATH).stringValue().contains("\\"));
        }
    }
}

