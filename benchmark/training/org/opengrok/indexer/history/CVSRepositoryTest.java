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
 * Copyright (c) 2008, 2018, Oracle and/or its affiliates. All rights reserved.
 * Portions Copyright (c) 2018, Chris Fraire <cfraire@me.com>.
 */
package org.opengrok.indexer.history;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.nio.channels.FileChannel;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.opengrok.indexer.condition.ConditionalRun;
import org.opengrok.indexer.condition.ConditionalRunRule;
import org.opengrok.indexer.condition.RepositoryInstalled;
import org.opengrok.indexer.util.TestRepository;


/**
 *
 *
 * @author austvik
 */
@ConditionalRun(RepositoryInstalled.CvsInstalled.class)
public class CVSRepositoryTest {
    @Rule
    public ConditionalRunRule rule = new ConditionalRunRule();

    CVSRepository instance;

    private TestRepository repository;

    /**
     * Get the CVS repository, test that getBranch() returns null if there is
     * no branch.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testGetBranchNoBranch() throws Exception {
        setUpTestRepository();
        File root = new File(repository.getSourceRoot(), "cvs_test/cvsrepo");
        CVSRepository cvsrepo = ((CVSRepository) (RepositoryFactory.getRepository(root)));
        Assert.assertEquals(null, cvsrepo.getBranch());
    }

    /**
     * Get the CVS repository, create new branch, change a file and verify that
     * getBranch() returns the branch and check newly added commits annotate
     * with branch revision numbers.
     * Last, check that history entries of the file follow through before the
     * branch was created.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testNewBranch() throws Exception {
        setUpTestRepository();
        File root = new File(repository.getSourceRoot(), "cvs_test/cvsrepo");
        // Create new branch and switch to it.
        CVSRepositoryTest.runCvsCommand(root, "tag", "-b", "mybranch");
        // Note that the 'update' command will change the entries in 'cvsroot' directory.
        CVSRepositoryTest.runCvsCommand(root, "update", "-r", "mybranch");
        // Now the repository object can be instantiated so that determineBranch()
        // will be called.
        CVSRepository cvsrepo = ((CVSRepository) (RepositoryFactory.getRepository(root)));
        Assert.assertEquals("mybranch", cvsrepo.getBranch());
        // Change the content and commit.
        File mainC = new File(root, "main.c");
        FileChannel outChan = new FileOutputStream(mainC, true).getChannel();
        outChan.truncate(0);
        outChan.close();
        FileWriter fw = new FileWriter(mainC);
        fw.write("#include <foo.h>\n");
        fw.close();
        CVSRepositoryTest.runCvsCommand(root, "commit", "-m", "change on a branch", "main.c");
        // Check that annotation for the changed line has branch revision.
        Annotation annotation = cvsrepo.annotate(mainC, null);
        Assert.assertEquals("1.2.2.1", annotation.getRevision(1));
        History mainCHistory = cvsrepo.getHistory(mainC);
        Assert.assertEquals(3, mainCHistory.getHistoryEntries().size());
        Assert.assertEquals("1.2.2.1", mainCHistory.getHistoryEntries().get(0).getRevision());
        Assert.assertEquals("1.2", mainCHistory.getHistoryEntries().get(1).getRevision());
        Assert.assertEquals("1.1", mainCHistory.getHistoryEntries().get(2).getRevision());
    }

    /**
     * Test of fileHasAnnotation method, of class CVSRepository.
     */
    @Test
    public void testFileHasAnnotation() {
        boolean result = instance.fileHasAnnotation(null);
        Assert.assertTrue(result);
    }

    /**
     * Test of fileHasHistory method, of class CVSRepository.
     */
    @Test
    public void testFileHasHistory() {
        boolean result = instance.fileHasHistory(null);
        Assert.assertTrue(result);
    }

    /**
     * Test of parseAnnotation method, of class CVSRepository.
     *
     * @throws java.lang.Exception
     * 		
     */
    @Test
    public void testParseAnnotation() throws Exception {
        String revId1 = "1.1";
        String revId2 = "1.2.3";
        String revId3 = "1.0";
        String author1 = "author1";
        String author2 = "author_long2";
        String author3 = "author3";
        String output = ((((((((((("just jibberish in output\n\n" + revId1) + "     (") + author1) + " 01-Mar-07) \n") + revId2) + "    (") + author2) + " 02-Mar-08)   if (some code)\n") + revId3) + "       (") + author3) + " 30-Apr-07)           call_function(i);\n";
        String fileName = "something.ext";
        CVSAnnotationParser parser = new CVSAnnotationParser(fileName);
        parser.processStream(new ByteArrayInputStream(output.getBytes()));
        Annotation result = parser.getAnnotation();
        Assert.assertNotNull(result);
        Assert.assertEquals(3, result.size());
        for (int i = 1; i <= 3; i++) {
            Assert.assertEquals(true, result.isEnabled(i));
        }
        Assert.assertEquals(revId1, result.getRevision(1));
        Assert.assertEquals(revId2, result.getRevision(2));
        Assert.assertEquals(revId3, result.getRevision(3));
        Assert.assertEquals(author1, result.getAuthor(1));
        Assert.assertEquals(author2, result.getAuthor(2));
        Assert.assertEquals(author3, result.getAuthor(3));
        Assert.assertEquals(author2.length(), result.getWidestAuthor());
        Assert.assertEquals(revId2.length(), result.getWidestRevision());
        Assert.assertEquals(fileName, result.getFilename());
    }
}

