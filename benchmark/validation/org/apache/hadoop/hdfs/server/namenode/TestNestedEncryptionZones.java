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


import HdfsConstants.SafeModeAction.SAFEMODE_ENTER;
import HdfsConstants.SafeModeAction.SAFEMODE_LEAVE;
import java.io.File;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FsShell;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.util.ToolRunner;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test the behavior of nested encryption zones.
 */
public class TestNestedEncryptionZones {
    private File testRootDir;

    private final String TOP_EZ_KEY = "topezkey";

    private final String NESTED_EZ_KEY = "nestedezkey";

    private MiniDFSCluster cluster;

    protected DistributedFileSystem fs;

    private final Path rootDir = new Path("/");

    private final Path rawDir = new Path("/.reserved/raw/");

    private Path nestedEZBaseFile = new Path(rootDir, "nestedEZBaseFile");

    private Path topEZBaseFile = new Path(rootDir, "topEZBaseFile");

    private Path topEZDir;

    private Path nestedEZDir;

    private Path topEZFile;

    private Path nestedEZFile;

    private Path topEZRawFile;

    private Path nestedEZRawFile;

    // File length
    private final int len = 8196;

    @Test(timeout = 60000)
    public void testNestedEncryptionZones() throws Exception {
        initTopEZDirAndNestedEZDir(new Path(rootDir, "topEZ"));
        verifyEncryption();
        // Restart NameNode to test if nested EZs can be loaded from edit logs
        cluster.restartNameNodes();
        cluster.waitActive();
        fs = cluster.getFileSystem();
        verifyEncryption();
        // Checkpoint and restart NameNode, to test if nested EZs can be loaded
        // from fsimage
        fs.setSafeMode(SAFEMODE_ENTER);
        fs.saveNamespace();
        fs.setSafeMode(SAFEMODE_LEAVE);
        cluster.restartNameNodes();
        cluster.waitActive();
        fs = cluster.getFileSystem();
        verifyEncryption();
        renameChildrenOfEZ();
        // Verify that a non-nested EZ cannot be moved into another EZ
        Path topEZ2Dir = new Path(rootDir, "topEZ2");
        fs.mkdir(topEZ2Dir, FsPermission.getDirDefault());
        fs.createEncryptionZone(topEZ2Dir, TOP_EZ_KEY);
        try {
            fs.rename(topEZ2Dir, new Path(topEZDir, "topEZ2"));
            Assert.fail(("Shouldn't be able to move a non-nested EZ into another " + "existing EZ."));
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("can't be moved into an encryption zone"));
        }
        // Should be able to rename the root dir of an EZ.
        fs.rename(topEZDir, new Path(rootDir, "newTopEZ"));
        // Should be able to rename the nested EZ dir within the same top EZ.
        fs.rename(new Path(rootDir, "newTopEZ/nestedEZ"), new Path(rootDir, "newTopEZ/newNestedEZ"));
    }

    @Test(timeout = 60000)
    public void testNestedEZWithRoot() throws Exception {
        initTopEZDirAndNestedEZDir(rootDir);
        verifyEncryption();
        // test rename file
        renameChildrenOfEZ();
        final String currentUser = UserGroupInformation.getCurrentUser().getShortUserName();
        final Path suffixTrashPath = new Path(FileSystem.TRASH_PREFIX, currentUser);
        final Path rootTrash = fs.getTrashRoot(rootDir);
        final Path topEZTrash = fs.getTrashRoot(topEZFile);
        final Path nestedEZTrash = fs.getTrashRoot(nestedEZFile);
        final Path expectedTopEZTrash = fs.makeQualified(new Path(topEZDir, suffixTrashPath));
        final Path expectedNestedEZTrash = fs.makeQualified(new Path(nestedEZDir, suffixTrashPath));
        Assert.assertEquals(("Top ez trash should be " + expectedTopEZTrash), expectedTopEZTrash, topEZTrash);
        Assert.assertEquals("Root trash should be equal with TopEZFile trash", topEZTrash, rootTrash);
        Assert.assertEquals(("Nested ez Trash should be " + expectedNestedEZTrash), expectedNestedEZTrash, nestedEZTrash);
        // delete rename file and test trash
        FsShell shell = new FsShell(fs.getConf());
        final Path topTrashFile = new Path((((shell.getCurrentTrashDir(topEZFile)) + "/") + (topEZFile)));
        final Path nestedTrashFile = new Path((((shell.getCurrentTrashDir(nestedEZFile)) + "/") + (nestedEZFile)));
        ToolRunner.run(shell, new String[]{ "-rm", topEZFile.toString() });
        ToolRunner.run(shell, new String[]{ "-rm", nestedEZFile.toString() });
        Assert.assertTrue(("File not in trash : " + topTrashFile), fs.exists(topTrashFile));
        Assert.assertTrue(("File not in trash : " + nestedTrashFile), fs.exists(nestedTrashFile));
    }
}

