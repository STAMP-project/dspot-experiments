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


import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.nfs.conf.NfsConfiguration;
import org.apache.hadoop.hdfs.protocol.HdfsFileStatus;
import org.apache.hadoop.hdfs.server.namenode.NameNode;
import org.apache.hadoop.nfs.nfs3.FileHandle;
import org.apache.hadoop.nfs.nfs3.response.READDIR3Response;
import org.apache.hadoop.nfs.nfs3.response.READDIR3Response.Entry3;
import org.apache.hadoop.nfs.nfs3.response.READDIRPLUS3Response;
import org.apache.hadoop.nfs.nfs3.response.READDIRPLUS3Response.EntryPlus3;
import org.apache.hadoop.oncrpc.XDR;
import org.apache.hadoop.oncrpc.security.SecurityHandler;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test READDIR and READDIRPLUS request with zero, nonzero cookies
 */
public class TestReaddir {
    static NfsConfiguration config = new NfsConfiguration();

    static MiniDFSCluster cluster = null;

    static DistributedFileSystem hdfs;

    static NameNode nn;

    static RpcProgramNfs3 nfsd;

    static String testdir = "/tmp";

    static SecurityHandler securityHandler;

    @Test
    public void testReaddirBasic() throws IOException {
        // Get inodeId of /tmp
        HdfsFileStatus status = TestReaddir.nn.getRpcServer().getFileInfo(TestReaddir.testdir);
        long dirId = status.getFileId();
        int namenodeId = Nfs3Utils.getNamenodeId(TestReaddir.config);
        // Create related part of the XDR request
        XDR xdr_req = new XDR();
        FileHandle handle = new FileHandle(dirId, namenodeId);
        handle.serialize(xdr_req);
        xdr_req.writeLongAsHyper(0);// cookie

        xdr_req.writeLongAsHyper(0);// verifier

        xdr_req.writeInt(100);// count

        READDIR3Response response = TestReaddir.nfsd.readdir(xdr_req.asReadOnlyWrap(), TestReaddir.securityHandler, new InetSocketAddress("localhost", 1234));
        List<Entry3> dirents = response.getDirList().getEntries();
        Assert.assertTrue(((dirents.size()) == 5));// inculding dot, dotdot

        // Test start listing from f2
        status = TestReaddir.nn.getRpcServer().getFileInfo(((TestReaddir.testdir) + "/f2"));
        long f2Id = status.getFileId();
        // Create related part of the XDR request
        xdr_req = new XDR();
        handle = new FileHandle(dirId, namenodeId);
        handle.serialize(xdr_req);
        xdr_req.writeLongAsHyper(f2Id);// cookie

        xdr_req.writeLongAsHyper(0);// verifier

        xdr_req.writeInt(100);// count

        response = TestReaddir.nfsd.readdir(xdr_req.asReadOnlyWrap(), TestReaddir.securityHandler, new InetSocketAddress("localhost", 1234));
        dirents = response.getDirList().getEntries();
        Assert.assertTrue(((dirents.size()) == 1));
        Entry3 entry = dirents.get(0);
        Assert.assertTrue(entry.getName().equals("f3"));
        // When the cookie is deleted, list starts over no including dot, dotdot
        TestReaddir.hdfs.delete(new Path(((TestReaddir.testdir) + "/f2")), false);
        response = TestReaddir.nfsd.readdir(xdr_req.asReadOnlyWrap(), TestReaddir.securityHandler, new InetSocketAddress("localhost", 1234));
        dirents = response.getDirList().getEntries();
        Assert.assertTrue(((dirents.size()) == 2));// No dot, dotdot

    }

    // Test readdirplus
    @Test
    public void testReaddirPlus() throws IOException {
        // Get inodeId of /tmp
        HdfsFileStatus status = TestReaddir.nn.getRpcServer().getFileInfo(TestReaddir.testdir);
        long dirId = status.getFileId();
        int namenodeId = Nfs3Utils.getNamenodeId(TestReaddir.config);
        // Create related part of the XDR request
        XDR xdr_req = new XDR();
        FileHandle handle = new FileHandle(dirId, namenodeId);
        handle.serialize(xdr_req);
        xdr_req.writeLongAsHyper(0);// cookie

        xdr_req.writeLongAsHyper(0);// verifier

        xdr_req.writeInt(100);// dirCount

        xdr_req.writeInt(1000);// maxCount

        READDIRPLUS3Response responsePlus = TestReaddir.nfsd.readdirplus(xdr_req.asReadOnlyWrap(), TestReaddir.securityHandler, new InetSocketAddress("localhost", 1234));
        List<EntryPlus3> direntPlus = responsePlus.getDirListPlus().getEntries();
        Assert.assertTrue(((direntPlus.size()) == 5));// including dot, dotdot

        // Test start listing from f2
        status = TestReaddir.nn.getRpcServer().getFileInfo(((TestReaddir.testdir) + "/f2"));
        long f2Id = status.getFileId();
        // Create related part of the XDR request
        xdr_req = new XDR();
        handle = new FileHandle(dirId, namenodeId);
        handle.serialize(xdr_req);
        xdr_req.writeLongAsHyper(f2Id);// cookie

        xdr_req.writeLongAsHyper(0);// verifier

        xdr_req.writeInt(100);// dirCount

        xdr_req.writeInt(1000);// maxCount

        responsePlus = TestReaddir.nfsd.readdirplus(xdr_req.asReadOnlyWrap(), TestReaddir.securityHandler, new InetSocketAddress("localhost", 1234));
        direntPlus = responsePlus.getDirListPlus().getEntries();
        Assert.assertTrue(((direntPlus.size()) == 1));
        EntryPlus3 entryPlus = direntPlus.get(0);
        Assert.assertTrue(entryPlus.getName().equals("f3"));
        // When the cookie is deleted, list starts over no including dot, dotdot
        TestReaddir.hdfs.delete(new Path(((TestReaddir.testdir) + "/f2")), false);
        responsePlus = TestReaddir.nfsd.readdirplus(xdr_req.asReadOnlyWrap(), TestReaddir.securityHandler, new InetSocketAddress("localhost", 1234));
        direntPlus = responsePlus.getDirListPlus().getEntries();
        Assert.assertTrue(((direntPlus.size()) == 2));// No dot, dotdot

    }
}

