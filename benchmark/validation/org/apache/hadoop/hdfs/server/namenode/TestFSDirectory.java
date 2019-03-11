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
package org.apache.hadoop.hdfs.server.namenode;


import DirOp.READ;
import DirOp.WRITE;
import INodeDirectory.DUMPTREE_EXCEPT_LAST_ITEM;
import INodeDirectory.DUMPTREE_LAST_ITEM;
import XAttr.NameSpace.RAW;
import XAttr.NameSpace.SYSTEM;
import XAttr.NameSpace.TRUSTED;
import XAttr.NameSpace.USER;
import XAttrSetFlag.CREATE;
import XAttrSetFlag.REPLACE;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.ParentNotDirectoryException;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.XAttr;
import org.apache.hadoop.hdfs.DFSTestUtil;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.protocol.NSQuotaExceededException;
import org.apache.hadoop.security.AccessControlException;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test {@link FSDirectory}, the in-memory namespace tree.
 */
public class TestFSDirectory {
    public static final Logger LOG = LoggerFactory.getLogger(TestFSDirectory.class);

    private static final long seed = 0;

    private static final short REPLICATION = 3;

    private final Path dir = new Path(("/" + (getClass().getSimpleName())));

    private final Path sub1 = new Path(dir, "sub1");

    private final Path file1 = new Path(sub1, "file1");

    private final Path file2 = new Path(sub1, "file2");

    private final Path sub11 = new Path(sub1, "sub11");

    private final Path file3 = new Path(sub11, "file3");

    private final Path file5 = new Path(sub1, "z_file5");

    private final Path sub2 = new Path(dir, "sub2");

    private final Path file6 = new Path(sub2, "file6");

    private Configuration conf;

    private MiniDFSCluster cluster;

    private FSNamesystem fsn;

    private FSDirectory fsdir;

    private DistributedFileSystem hdfs;

    private static final int numGeneratedXAttrs = 256;

    private static final ImmutableList<XAttr> generatedXAttrs = ImmutableList.copyOf(TestFSDirectory.generateXAttrs(TestFSDirectory.numGeneratedXAttrs));

    /**
     * Dump the tree, make some changes, and then dump the tree again.
     */
    @Test
    public void testDumpTree() throws Exception {
        final INode root = fsdir.getINode("/");
        TestFSDirectory.LOG.info("Original tree");
        final StringBuffer b1 = root.dumpTreeRecursively();
        System.out.println(("b1=" + b1));
        final BufferedReader in = new BufferedReader(new StringReader(b1.toString()));
        String line = in.readLine();
        TestFSDirectory.checkClassName(line);
        for (; (line = in.readLine()) != null;) {
            line = line.trim();
            if ((!(line.isEmpty())) && (!(line.contains("snapshot")))) {
                Assert.assertTrue(("line=" + line), ((line.startsWith(DUMPTREE_LAST_ITEM)) || (line.startsWith(DUMPTREE_EXCEPT_LAST_ITEM))));
                TestFSDirectory.checkClassName(line);
            }
        }
    }

    @Test
    public void testSkipQuotaCheck() throws Exception {
        try {
            // set quota. nsQuota of 1 means no files can be created
            // under this directory.
            hdfs.setQuota(sub2, 1, Long.MAX_VALUE);
            // create a file
            try {
                // this should fail
                DFSTestUtil.createFile(hdfs, file6, 1024, TestFSDirectory.REPLICATION, TestFSDirectory.seed);
                throw new IOException("The create should have failed.");
            } catch (NSQuotaExceededException qe) {
                // ignored
            }
            // disable the quota check and retry. this should succeed.
            fsdir.disableQuotaChecks();
            DFSTestUtil.createFile(hdfs, file6, 1024, TestFSDirectory.REPLICATION, TestFSDirectory.seed);
            // trying again after re-enabling the check.
            hdfs.delete(file6, false);// cleanup

            fsdir.enableQuotaChecks();
            try {
                // this should fail
                DFSTestUtil.createFile(hdfs, file6, 1024, TestFSDirectory.REPLICATION, TestFSDirectory.seed);
                throw new IOException("The create should have failed.");
            } catch (NSQuotaExceededException qe) {
                // ignored
            }
        } finally {
            hdfs.delete(file6, false);// cleanup, in case the test failed in the middle.

            hdfs.setQuota(sub2, Long.MAX_VALUE, Long.MAX_VALUE);
        }
    }

    @Test
    public void testINodeXAttrsLimit() throws Exception {
        List<XAttr> existingXAttrs = Lists.newArrayListWithCapacity(2);
        XAttr xAttr1 = new XAttr.Builder().setNameSpace(USER).setName("a1").setValue(new byte[]{ 49, 50, 51 }).build();
        XAttr xAttr2 = new XAttr.Builder().setNameSpace(USER).setName("a2").setValue(new byte[]{ 49, 49, 49 }).build();
        existingXAttrs.add(xAttr1);
        existingXAttrs.add(xAttr2);
        // Adding system and raw namespace xAttrs aren't affected by inode
        // xAttrs limit.
        XAttr newSystemXAttr = new XAttr.Builder().setNameSpace(SYSTEM).setName("a3").setValue(new byte[]{ 51, 51, 51 }).build();
        XAttr newRawXAttr = new XAttr.Builder().setNameSpace(RAW).setName("a3").setValue(new byte[]{ 51, 51, 51 }).build();
        List<XAttr> newXAttrs = Lists.newArrayListWithCapacity(2);
        newXAttrs.add(newSystemXAttr);
        newXAttrs.add(newRawXAttr);
        List<XAttr> xAttrs = FSDirXAttrOp.setINodeXAttrs(fsdir, existingXAttrs, newXAttrs, EnumSet.of(CREATE, REPLACE));
        Assert.assertEquals(xAttrs.size(), 4);
        // Adding a trusted namespace xAttr, is affected by inode xAttrs limit.
        XAttr newXAttr1 = new XAttr.Builder().setNameSpace(TRUSTED).setName("a4").setValue(new byte[]{ 52, 52, 52 }).build();
        newXAttrs.set(0, newXAttr1);
        try {
            FSDirXAttrOp.setINodeXAttrs(fsdir, existingXAttrs, newXAttrs, EnumSet.of(CREATE, REPLACE));
            Assert.fail(("Setting user visible xattr on inode should fail if " + "reaching limit."));
        } catch (IOException e) {
            GenericTestUtils.assertExceptionContains(("Cannot add additional XAttr " + "to inode, would exceed limit"), e);
        }
    }

    /**
     * Test setting and removing multiple xattrs via single operations
     */
    @Test(timeout = 300000)
    public void testXAttrMultiSetRemove() throws Exception {
        List<XAttr> existingXAttrs = Lists.newArrayListWithCapacity(0);
        // Keep adding a random number of xattrs and verifying until exhausted
        final Random rand = new Random(1044186);
        int numExpectedXAttrs = 0;
        while (numExpectedXAttrs < (TestFSDirectory.numGeneratedXAttrs)) {
            TestFSDirectory.LOG.info((("Currently have " + numExpectedXAttrs) + " xattrs"));
            final int numToAdd = (rand.nextInt(5)) + 1;
            List<XAttr> toAdd = Lists.newArrayListWithCapacity(numToAdd);
            for (int i = 0; i < numToAdd; i++) {
                if (numExpectedXAttrs >= (TestFSDirectory.numGeneratedXAttrs)) {
                    break;
                }
                toAdd.add(TestFSDirectory.generatedXAttrs.get(numExpectedXAttrs));
                numExpectedXAttrs++;
            }
            TestFSDirectory.LOG.info((("Attempting to add " + (toAdd.size())) + " XAttrs"));
            for (int i = 0; i < (toAdd.size()); i++) {
                TestFSDirectory.LOG.info(("Will add XAttr " + (toAdd.get(i))));
            }
            List<XAttr> newXAttrs = FSDirXAttrOp.setINodeXAttrs(fsdir, existingXAttrs, toAdd, EnumSet.of(CREATE));
            TestFSDirectory.verifyXAttrsPresent(newXAttrs, numExpectedXAttrs);
            existingXAttrs = newXAttrs;
        } 
        // Keep removing a random number of xattrs and verifying until all gone
        while (numExpectedXAttrs > 0) {
            TestFSDirectory.LOG.info((("Currently have " + numExpectedXAttrs) + " xattrs"));
            final int numToRemove = (rand.nextInt(5)) + 1;
            List<XAttr> toRemove = Lists.newArrayListWithCapacity(numToRemove);
            for (int i = 0; i < numToRemove; i++) {
                if (numExpectedXAttrs == 0) {
                    break;
                }
                toRemove.add(TestFSDirectory.generatedXAttrs.get((numExpectedXAttrs - 1)));
                numExpectedXAttrs--;
            }
            final int expectedNumToRemove = toRemove.size();
            TestFSDirectory.LOG.info((("Attempting to remove " + expectedNumToRemove) + " XAttrs"));
            List<XAttr> removedXAttrs = Lists.newArrayList();
            List<XAttr> newXAttrs = FSDirXAttrOp.filterINodeXAttrs(existingXAttrs, toRemove, removedXAttrs);
            Assert.assertEquals("Unexpected number of removed XAttrs", expectedNumToRemove, removedXAttrs.size());
            TestFSDirectory.verifyXAttrsPresent(newXAttrs, numExpectedXAttrs);
            existingXAttrs = newXAttrs;
        } 
    }

    @Test(timeout = 300000)
    public void testXAttrMultiAddRemoveErrors() throws Exception {
        // Test that the same XAttr can not be multiset twice
        List<XAttr> existingXAttrs = Lists.newArrayList();
        List<XAttr> toAdd = Lists.newArrayList();
        toAdd.add(TestFSDirectory.generatedXAttrs.get(0));
        toAdd.add(TestFSDirectory.generatedXAttrs.get(1));
        toAdd.add(TestFSDirectory.generatedXAttrs.get(2));
        toAdd.add(TestFSDirectory.generatedXAttrs.get(0));
        try {
            FSDirXAttrOp.setINodeXAttrs(fsdir, existingXAttrs, toAdd, EnumSet.of(CREATE));
            Assert.fail("Specified the same xattr to be set twice");
        } catch (IOException e) {
            GenericTestUtils.assertExceptionContains(("Cannot specify the same " + "XAttr to be set"), e);
        }
        // Test that CREATE and REPLACE flags are obeyed
        toAdd.remove(TestFSDirectory.generatedXAttrs.get(0));
        existingXAttrs.add(TestFSDirectory.generatedXAttrs.get(0));
        try {
            FSDirXAttrOp.setINodeXAttrs(fsdir, existingXAttrs, toAdd, EnumSet.of(CREATE));
            Assert.fail("Set XAttr that is already set without REPLACE flag");
        } catch (IOException e) {
            GenericTestUtils.assertExceptionContains("already exists", e);
        }
        try {
            FSDirXAttrOp.setINodeXAttrs(fsdir, existingXAttrs, toAdd, EnumSet.of(REPLACE));
            Assert.fail("Set XAttr that does not exist without the CREATE flag");
        } catch (IOException e) {
            GenericTestUtils.assertExceptionContains("does not exist", e);
        }
        // Sanity test for CREATE
        toAdd.remove(TestFSDirectory.generatedXAttrs.get(0));
        List<XAttr> newXAttrs = FSDirXAttrOp.setINodeXAttrs(fsdir, existingXAttrs, toAdd, EnumSet.of(CREATE));
        Assert.assertEquals("Unexpected toAdd size", 2, toAdd.size());
        for (XAttr x : toAdd) {
            Assert.assertTrue(("Did not find added XAttr " + x), newXAttrs.contains(x));
        }
        existingXAttrs = newXAttrs;
        // Sanity test for REPLACE
        toAdd = Lists.newArrayList();
        for (int i = 0; i < 3; i++) {
            XAttr xAttr = new XAttr.Builder().setNameSpace(SYSTEM).setName(("a" + i)).setValue(new byte[]{ ((byte) (i * 2)) }).build();
            toAdd.add(xAttr);
        }
        newXAttrs = FSDirXAttrOp.setINodeXAttrs(fsdir, existingXAttrs, toAdd, EnumSet.of(REPLACE));
        Assert.assertEquals("Unexpected number of new XAttrs", 3, newXAttrs.size());
        for (int i = 0; i < 3; i++) {
            Assert.assertArrayEquals("Unexpected XAttr value", new byte[]{ ((byte) (i * 2)) }, newXAttrs.get(i).getValue());
        }
        existingXAttrs = newXAttrs;
        // Sanity test for CREATE+REPLACE
        toAdd = Lists.newArrayList();
        for (int i = 0; i < 4; i++) {
            toAdd.add(TestFSDirectory.generatedXAttrs.get(i));
        }
        newXAttrs = FSDirXAttrOp.setINodeXAttrs(fsdir, existingXAttrs, toAdd, EnumSet.of(CREATE, REPLACE));
        TestFSDirectory.verifyXAttrsPresent(newXAttrs, 4);
    }

    @Test
    public void testVerifyParentDir() throws Exception {
        hdfs.mkdirs(new Path("/dir1/dir2"));
        hdfs.createNewFile(new Path("/dir1/file"));
        hdfs.createNewFile(new Path("/dir1/dir2/file"));
        INodesInPath iip = fsdir.resolvePath(null, "/", READ);
        fsdir.verifyParentDir(iip);
        iip = fsdir.resolvePath(null, "/dir1", READ);
        fsdir.verifyParentDir(iip);
        iip = fsdir.resolvePath(null, "/dir1/file", READ);
        fsdir.verifyParentDir(iip);
        iip = fsdir.resolvePath(null, "/dir-nonexist/file", READ);
        try {
            fsdir.verifyParentDir(iip);
            Assert.fail("expected FNF");
        } catch (FileNotFoundException fnf) {
            // expected.
        }
        iip = fsdir.resolvePath(null, "/dir1/dir2", READ);
        fsdir.verifyParentDir(iip);
        iip = fsdir.resolvePath(null, "/dir1/dir2/file", READ);
        fsdir.verifyParentDir(iip);
        iip = fsdir.resolvePath(null, "/dir1/dir-nonexist/file", READ);
        try {
            fsdir.verifyParentDir(iip);
            Assert.fail("expected FNF");
        } catch (FileNotFoundException fnf) {
            // expected.
        }
        try {
            iip = fsdir.resolvePath(null, "/dir1/file/fail", READ);
            Assert.fail("expected ACE");
        } catch (AccessControlException ace) {
            Assert.assertTrue(ace.getMessage().contains("is not a directory"));
        }
        try {
            iip = fsdir.resolvePath(null, "/dir1/file/fail", WRITE);
            Assert.fail("expected ACE");
        } catch (AccessControlException ace) {
            Assert.assertTrue(ace.getMessage().contains("is not a directory"));
        }
        try {
            iip = fsdir.resolvePath(null, "/dir1/file/fail", DirOp.CREATE);
            Assert.fail("expected PNDE");
        } catch (ParentNotDirectoryException pnde) {
            Assert.assertTrue(pnde.getMessage().contains("is not a directory"));
        }
    }
}

