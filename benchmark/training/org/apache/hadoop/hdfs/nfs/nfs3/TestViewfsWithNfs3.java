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
package org.apache.hadoop.hdfs.nfs.nfs3;


import Nfs3Status.NFS3ERR_INVAL;
import Nfs3Status.NFS3ERR_IO;
import Nfs3Status.NFS3_OK;
import java.io.File;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileSystemTestHelper;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DFSTestUtil;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.client.HdfsAdmin;
import org.apache.hadoop.hdfs.nfs.conf.NfsConfiguration;
import org.apache.hadoop.hdfs.nfs.mount.RpcProgramMountd;
import org.apache.hadoop.hdfs.protocol.HdfsFileStatus;
import org.apache.hadoop.hdfs.server.namenode.NameNode;
import org.apache.hadoop.nfs.nfs3.FileHandle;
import org.apache.hadoop.oncrpc.security.SecurityHandler;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link RpcProgramNfs3} with
 * {@link org.apache.hadoop.fs.viewfs.ViewFileSystem}.
 */
public class TestViewfsWithNfs3 {
    private static DistributedFileSystem hdfs1;

    private static DistributedFileSystem hdfs2;

    private static MiniDFSCluster cluster = null;

    private static NfsConfiguration config = new NfsConfiguration();

    private static HdfsAdmin dfsAdmin1;

    private static HdfsAdmin dfsAdmin2;

    private static FileSystem viewFs;

    private static NameNode nn1;

    private static NameNode nn2;

    private static Nfs3 nfs;

    private static RpcProgramNfs3 nfsd;

    private static RpcProgramMountd mountd;

    private static SecurityHandler securityHandler;

    private static FileSystemTestHelper fsHelper;

    private static File testRootDir;

    @Test
    public void testNumExports() throws Exception {
        Assert.assertEquals(TestViewfsWithNfs3.mountd.getExports().size(), TestViewfsWithNfs3.viewFs.getChildFileSystems().length);
    }

    @Test
    public void testPaths() throws Exception {
        Assert.assertEquals(TestViewfsWithNfs3.hdfs1.resolvePath(new Path("/user1/file1")), TestViewfsWithNfs3.viewFs.resolvePath(new Path("/hdfs1/file1")));
        Assert.assertEquals(TestViewfsWithNfs3.hdfs1.resolvePath(new Path("/user1/file2")), TestViewfsWithNfs3.viewFs.resolvePath(new Path("/hdfs1/file2")));
        Assert.assertEquals(TestViewfsWithNfs3.hdfs2.resolvePath(new Path("/user2/dir2")), TestViewfsWithNfs3.viewFs.resolvePath(new Path("/hdfs2/dir2")));
    }

    @Test
    public void testFileStatus() throws Exception {
        HdfsFileStatus status = TestViewfsWithNfs3.nn1.getRpcServer().getFileInfo("/user1/file1");
        FileStatus st = TestViewfsWithNfs3.viewFs.getFileStatus(new Path("/hdfs1/file1"));
        Assert.assertEquals(st.isDirectory(), status.isDirectory());
        HdfsFileStatus status2 = TestViewfsWithNfs3.nn2.getRpcServer().getFileInfo("/user2/dir2");
        FileStatus st2 = TestViewfsWithNfs3.viewFs.getFileStatus(new Path("/hdfs2/dir2"));
        Assert.assertEquals(st2.isDirectory(), status2.isDirectory());
    }

    @Test(timeout = 60000)
    public void testNfsAccessNN1() throws Exception {
        HdfsFileStatus status = TestViewfsWithNfs3.nn1.getRpcServer().getFileInfo("/user1/file1");
        int namenodeId = Nfs3Utils.getNamenodeId(TestViewfsWithNfs3.config, TestViewfsWithNfs3.hdfs1.getUri());
        testNfsGetAttrResponse(status.getFileId(), namenodeId, NFS3_OK);
    }

    @Test(timeout = 60000)
    public void testNfsAccessNN2() throws Exception {
        HdfsFileStatus status = TestViewfsWithNfs3.nn2.getRpcServer().getFileInfo("/user2/dir2");
        int namenodeId = Nfs3Utils.getNamenodeId(TestViewfsWithNfs3.config, TestViewfsWithNfs3.hdfs2.getUri());
        testNfsGetAttrResponse(status.getFileId(), namenodeId, NFS3_OK);
    }

    @Test(timeout = 60000)
    public void testWrongNfsAccess() throws Exception {
        DFSTestUtil.createFile(TestViewfsWithNfs3.viewFs, new Path("/hdfs1/file3"), 0, ((short) (1)), 0);
        HdfsFileStatus status = TestViewfsWithNfs3.nn1.getRpcServer().getFileInfo("/user1/file3");
        int namenodeId = Nfs3Utils.getNamenodeId(TestViewfsWithNfs3.config, TestViewfsWithNfs3.hdfs2.getUri());
        testNfsGetAttrResponse(status.getFileId(), namenodeId, NFS3ERR_IO);
    }

    @Test(timeout = 60000)
    public void testNfsWriteNN1() throws Exception {
        HdfsFileStatus status = TestViewfsWithNfs3.nn1.getRpcServer().getFileInfo("/user1/write1");
        int namenodeId = Nfs3Utils.getNamenodeId(TestViewfsWithNfs3.config, TestViewfsWithNfs3.hdfs1.getUri());
        testNfsWriteResponse(status.getFileId(), namenodeId);
    }

    @Test(timeout = 60000)
    public void testNfsWriteNN2() throws Exception {
        HdfsFileStatus status = TestViewfsWithNfs3.nn2.getRpcServer().getFileInfo("/user2/write2");
        int namenodeId = Nfs3Utils.getNamenodeId(TestViewfsWithNfs3.config, TestViewfsWithNfs3.hdfs2.getUri());
        testNfsWriteResponse(status.getFileId(), namenodeId);
    }

    @Test(timeout = 60000)
    public void testNfsRenameMultiNN() throws Exception {
        HdfsFileStatus fromFileStatus = TestViewfsWithNfs3.nn1.getRpcServer().getFileInfo("/user1");
        int fromNNId = Nfs3Utils.getNamenodeId(TestViewfsWithNfs3.config, TestViewfsWithNfs3.hdfs1.getUri());
        FileHandle fromHandle = new FileHandle(fromFileStatus.getFileId(), fromNNId);
        HdfsFileStatus toFileStatus = TestViewfsWithNfs3.nn2.getRpcServer().getFileInfo("/user2");
        int toNNId = Nfs3Utils.getNamenodeId(TestViewfsWithNfs3.config, TestViewfsWithNfs3.hdfs2.getUri());
        FileHandle toHandle = new FileHandle(toFileStatus.getFileId(), toNNId);
        HdfsFileStatus statusBeforeRename = TestViewfsWithNfs3.nn1.getRpcServer().getFileInfo("/user1/renameMultiNN");
        Assert.assertEquals(statusBeforeRename.isDirectory(), false);
        testNfsRename(fromHandle, "renameMultiNN", toHandle, "renameMultiNNFail", NFS3ERR_INVAL);
        HdfsFileStatus statusAfterRename = TestViewfsWithNfs3.nn2.getRpcServer().getFileInfo("/user2/renameMultiNNFail");
        Assert.assertEquals(statusAfterRename, null);
        statusAfterRename = TestViewfsWithNfs3.nn1.getRpcServer().getFileInfo("/user1/renameMultiNN");
        Assert.assertEquals(statusAfterRename.isDirectory(), false);
    }

    @Test(timeout = 60000)
    public void testNfsRenameSingleNN() throws Exception {
        HdfsFileStatus fromFileStatus = TestViewfsWithNfs3.nn1.getRpcServer().getFileInfo("/user1");
        int fromNNId = Nfs3Utils.getNamenodeId(TestViewfsWithNfs3.config, TestViewfsWithNfs3.hdfs1.getUri());
        FileHandle fromHandle = new FileHandle(fromFileStatus.getFileId(), fromNNId);
        HdfsFileStatus statusBeforeRename = TestViewfsWithNfs3.nn1.getRpcServer().getFileInfo("/user1/renameSingleNN");
        Assert.assertEquals(statusBeforeRename.isDirectory(), false);
        testNfsRename(fromHandle, "renameSingleNN", fromHandle, "renameSingleNNSucess", NFS3_OK);
        HdfsFileStatus statusAfterRename = TestViewfsWithNfs3.nn1.getRpcServer().getFileInfo("/user1/renameSingleNNSucess");
        Assert.assertEquals(statusAfterRename.isDirectory(), false);
        statusAfterRename = TestViewfsWithNfs3.nn1.getRpcServer().getFileInfo("/user1/renameSingleNN");
        Assert.assertEquals(statusAfterRename, null);
    }
}

