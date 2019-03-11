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


import Nfs3Status.NFS3ERR_ACCES;
import java.net.InetSocketAddress;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.nfs.conf.NfsConfiguration;
import org.apache.hadoop.hdfs.protocol.HdfsFileStatus;
import org.apache.hadoop.hdfs.server.namenode.NameNode;
import org.apache.hadoop.nfs.nfs3.FileHandle;
import org.apache.hadoop.nfs.nfs3.response.REMOVE3Response;
import org.apache.hadoop.oncrpc.XDR;
import org.apache.hadoop.oncrpc.security.SecurityHandler;
import org.junit.Assert;
import org.junit.Test;


public class TestClientAccessPrivilege {
    static MiniDFSCluster cluster = null;

    static NfsConfiguration config = new NfsConfiguration();

    static DistributedFileSystem hdfs;

    static NameNode nn;

    static String testdir = "/tmp";

    static SecurityHandler securityHandler;

    @Test(timeout = 60000)
    public void testClientAccessPrivilegeForRemove() throws Exception {
        // Configure ro access for nfs1 service
        TestClientAccessPrivilege.config.set("dfs.nfs.exports.allowed.hosts", "* ro");
        // Start nfs
        Nfs3 nfs = new Nfs3(TestClientAccessPrivilege.config);
        nfs.startServiceInternal(false);
        RpcProgramNfs3 nfsd = ((RpcProgramNfs3) (nfs.getRpcProgram()));
        // Create a remove request
        HdfsFileStatus status = TestClientAccessPrivilege.nn.getRpcServer().getFileInfo(TestClientAccessPrivilege.testdir);
        long dirId = status.getFileId();
        int namenodeId = Nfs3Utils.getNamenodeId(TestClientAccessPrivilege.config);
        XDR xdr_req = new XDR();
        FileHandle handle = new FileHandle(dirId, namenodeId);
        handle.serialize(xdr_req);
        xdr_req.writeString("f1");
        // Remove operation
        REMOVE3Response response = nfsd.remove(xdr_req.asReadOnlyWrap(), TestClientAccessPrivilege.securityHandler, new InetSocketAddress("localhost", 1234));
        // Assert on return code
        Assert.assertEquals("Incorrect return code", NFS3ERR_ACCES, response.getStatus());
    }
}

