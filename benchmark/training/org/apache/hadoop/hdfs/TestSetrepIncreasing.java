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
package org.apache.hadoop.hdfs;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FsShell;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.protocol.ClientProtocol;
import org.junit.Assert;
import org.junit.Test;


public class TestSetrepIncreasing {
    @Test(timeout = 120000)
    public void testSetrepIncreasing() throws IOException {
        TestSetrepIncreasing.setrep(3, 7, false);
    }

    @Test(timeout = 120000)
    public void testSetrepIncreasingSimulatedStorage() throws IOException {
        TestSetrepIncreasing.setrep(3, 7, true);
    }

    @Test
    public void testSetRepWithStoragePolicyOnEmptyFile() throws Exception {
        Configuration conf = new HdfsConfiguration();
        MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).numDataNodes(1).build();
        DistributedFileSystem dfs = cluster.getFileSystem();
        try {
            Path d = new Path("/tmp");
            dfs.mkdirs(d);
            dfs.setStoragePolicy(d, "HOT");
            Path f = new Path(d, "foo");
            dfs.createNewFile(f);
            dfs.setReplication(f, ((short) (4)));
        } finally {
            dfs.close();
            cluster.shutdown();
        }
    }

    @Test
    public void testSetRepOnECFile() throws Exception {
        ClientProtocol client;
        Configuration conf = new HdfsConfiguration();
        MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).numDataNodes(1).build();
        cluster.waitActive();
        client = NameNodeProxies.createProxy(conf, cluster.getFileSystem(0).getUri(), ClientProtocol.class).getProxy();
        client.enableErasureCodingPolicy(StripedFileTestUtil.getDefaultECPolicy().getName());
        client.setErasureCodingPolicy("/", StripedFileTestUtil.getDefaultECPolicy().getName());
        FileSystem dfs = cluster.getFileSystem();
        try {
            Path d = new Path("/tmp");
            dfs.mkdirs(d);
            Path f = new Path(d, "foo");
            dfs.createNewFile(f);
            FileStatus file = dfs.getFileStatus(f);
            Assert.assertTrue(file.isErasureCoded());
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            System.setOut(new PrintStream(out));
            String[] args = new String[]{ "-setrep", "2", "" + f };
            FsShell shell = new FsShell();
            shell.setConf(conf);
            Assert.assertEquals(0, shell.run(args));
            Assert.assertTrue(out.toString().contains("Did not set replication for: /tmp/foo"));
            // verify the replication factor of the EC file
            file = dfs.getFileStatus(f);
            Assert.assertEquals(1, file.getReplication());
        } finally {
            dfs.close();
            cluster.shutdown();
        }
    }
}

