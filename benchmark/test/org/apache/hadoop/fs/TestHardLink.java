/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.fs;


import java.io.File;
import java.io.IOException;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Test;


/**
 * This testing is fairly lightweight.  Assumes HardLink routines will
 * only be called when permissions etc are okay; no negative testing is
 * provided.
 *
 * These tests all use
 * "src" as the source directory,
 * "tgt_one" as the target directory for single-file hardlinking, and
 * "tgt_mult" as the target directory for multi-file hardlinking.
 *
 * Contents of them are/will be:
 * dir:src:
 *   files: x1, x2, x3
 * dir:tgt_one:
 *   files: x1 (linked to src/x1), y (linked to src/x2),
 *          x3 (linked to src/x3), x11 (also linked to src/x1)
 * dir:tgt_mult:
 *   files: x1, x2, x3 (all linked to same name in src/)
 *
 * NOTICE: This test class only tests the functionality of the OS
 * upon which the test is run! (although you're pretty safe with the
 * unix-like OS's, unless a typo sneaks in.)
 */
public class TestHardLink {
    private static final File TEST_DIR = GenericTestUtils.getTestDir("test/hl");

    private static String DIR = "dir_";

    // define source and target directories
    private static File src = new File(TestHardLink.TEST_DIR, ((TestHardLink.DIR) + "src"));

    private static File tgt_mult = new File(TestHardLink.TEST_DIR, ((TestHardLink.DIR) + "tgt_mult"));

    private static File tgt_one = new File(TestHardLink.TEST_DIR, ((TestHardLink.DIR) + "tgt_one"));

    // define source files
    private static File x1 = new File(TestHardLink.src, "x1");

    private static File x2 = new File(TestHardLink.src, "x2");

    private static File x3 = new File(TestHardLink.src, "x3");

    // define File objects for the target hardlinks
    private static File x1_one = new File(TestHardLink.tgt_one, "x1");

    private static File y_one = new File(TestHardLink.tgt_one, "y");

    private static File x3_one = new File(TestHardLink.tgt_one, "x3");

    private static File x11_one = new File(TestHardLink.tgt_one, "x11");

    private static File x1_mult = new File(TestHardLink.tgt_mult, "x1");

    private static File x2_mult = new File(TestHardLink.tgt_mult, "x2");

    private static File x3_mult = new File(TestHardLink.tgt_mult, "x3");

    // content strings for file content testing
    private static String str1 = "11111";

    private static String str2 = "22222";

    private static String str3 = "33333";

    /**
     * Sanity check the simplest case of HardLink.getLinkCount()
     * to make sure we get back "1" for ordinary single-linked files.
     * Tests with multiply-linked files are in later test cases.
     */
    @Test
    public void testGetLinkCount() throws IOException {
        // at beginning of world, check that source files have link count "1"
        // since they haven't been hardlinked yet
        Assert.assertEquals(1, getLinkCount(TestHardLink.x1));
        Assert.assertEquals(1, getLinkCount(TestHardLink.x2));
        Assert.assertEquals(1, getLinkCount(TestHardLink.x3));
    }

    /**
     * Test the single-file method HardLink.createHardLink().
     * Also tests getLinkCount() with values greater than one.
     */
    @Test
    public void testCreateHardLink() throws IOException {
        // hardlink a single file and confirm expected result
        createHardLink(TestHardLink.x1, TestHardLink.x1_one);
        Assert.assertTrue(TestHardLink.x1_one.exists());
        Assert.assertEquals(2, getLinkCount(TestHardLink.x1));// x1 and x1_one are linked now

        Assert.assertEquals(2, getLinkCount(TestHardLink.x1_one));// so they both have count "2"

        // confirm that x2, which we didn't change, still shows count "1"
        Assert.assertEquals(1, getLinkCount(TestHardLink.x2));
        // now do a few more
        createHardLink(TestHardLink.x2, TestHardLink.y_one);
        createHardLink(TestHardLink.x3, TestHardLink.x3_one);
        Assert.assertEquals(2, getLinkCount(TestHardLink.x2));
        Assert.assertEquals(2, getLinkCount(TestHardLink.x3));
        // create another link to a file that already has count 2
        createHardLink(TestHardLink.x1, TestHardLink.x11_one);
        Assert.assertEquals(3, getLinkCount(TestHardLink.x1));
        // x1, x1_one, and x11_one
        Assert.assertEquals(3, getLinkCount(TestHardLink.x1_one));// are all linked, so they

        Assert.assertEquals(3, getLinkCount(TestHardLink.x11_one));// should all have count "3"

        // validate by contents
        validateTgtOne();
        // validate that change of content is reflected in the other linked files
        appendToFile(TestHardLink.x1_one, TestHardLink.str3);
        Assert.assertTrue(fetchFileContents(TestHardLink.x1_one).equals(((TestHardLink.str1) + (TestHardLink.str3))));
        Assert.assertTrue(fetchFileContents(TestHardLink.x11_one).equals(((TestHardLink.str1) + (TestHardLink.str3))));
        Assert.assertTrue(fetchFileContents(TestHardLink.x1).equals(((TestHardLink.str1) + (TestHardLink.str3))));
    }

    /* Test the multi-file method HardLink.createHardLinkMult(),
    multiple files within a directory into one target directory
     */
    @Test
    public void testCreateHardLinkMult() throws IOException {
        // hardlink a whole list of three files at once
        String[] fileNames = TestHardLink.src.list();
        createHardLinkMult(TestHardLink.src, fileNames, TestHardLink.tgt_mult);
        // validate by link count - each file has been linked once,
        // so each count is "2"
        Assert.assertEquals(2, getLinkCount(TestHardLink.x1));
        Assert.assertEquals(2, getLinkCount(TestHardLink.x2));
        Assert.assertEquals(2, getLinkCount(TestHardLink.x3));
        Assert.assertEquals(2, getLinkCount(TestHardLink.x1_mult));
        Assert.assertEquals(2, getLinkCount(TestHardLink.x2_mult));
        Assert.assertEquals(2, getLinkCount(TestHardLink.x3_mult));
        // validate by contents
        validateTgtMult();
        // validate that change of content is reflected in the other linked files
        appendToFile(TestHardLink.x1_mult, TestHardLink.str3);
        Assert.assertTrue(fetchFileContents(TestHardLink.x1_mult).equals(((TestHardLink.str1) + (TestHardLink.str3))));
        Assert.assertTrue(fetchFileContents(TestHardLink.x1).equals(((TestHardLink.str1) + (TestHardLink.str3))));
    }

    /**
     * Test createHardLinkMult() with empty list of files.
     * We use an extended version of the method call, that
     * returns the number of System exec calls made, which should
     * be zero in this case.
     */
    @Test
    public void testCreateHardLinkMultEmptyList() throws IOException {
        String[] emptyList = new String[]{  };
        // test the case of empty file list
        createHardLinkMult(TestHardLink.src, emptyList, TestHardLink.tgt_mult);
        // check nothing changed in the directory tree
        validateSetup();
    }

    /* Assume that this test won't usually be run on a Windows box.
    This test case allows testing of the correct syntax of the Windows
    commands, even though they don't actually get executed on a non-Win box.
    The basic idea is to have enough here that substantive changes will
    fail and the author will fix and add to this test as appropriate.

    Depends on the HardLinkCGWin class and member fields being accessible
    from this test method.
     */
    @Test
    public void testWindowsSyntax() {
        class win extends HardLinkCGWin {}
        // basic checks on array lengths
        Assert.assertEquals(4, getLinkCountCommand);
        // make sure "%f" was not munged
        Assert.assertEquals(2, "%f".length());
        // make sure "\\%f" was munged correctly
        Assert.assertEquals(3, "\\%f".length());
        Assert.assertEquals("hardlink", getLinkCountCommand[1]);
        // make sure "-c%h" was not munged
        Assert.assertEquals(4, "-c%h".length());
    }
}

