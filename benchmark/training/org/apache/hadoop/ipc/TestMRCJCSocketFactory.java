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
package org.apache.hadoop.ipc;


import MRConfig.FRAMEWORK_NAME;
import MRConfig.YARN_FRAMEWORK_NAME;
import YarnConfiguration.RM_ADDRESS;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.JobStatus;
import org.apache.hadoop.mapreduce.v2.MiniMRYarnCluster;
import org.junit.Assert;
import org.junit.Test;


/**
 * This class checks that RPCs can use specialized socket factories.
 */
public class TestMRCJCSocketFactory {
    /**
     * Check that we can reach a NameNode or Resource Manager using a specific
     * socket factory
     */
    @Test
    public void testSocketFactory() throws IOException {
        // Create a standard mini-cluster
        Configuration sconf = new Configuration();
        MiniDFSCluster cluster = numDataNodes(1).build();
        final int nameNodePort = cluster.getNameNodePort();
        // Get a reference to its DFS directly
        FileSystem fs = cluster.getFileSystem();
        Assert.assertTrue((fs instanceof DistributedFileSystem));
        DistributedFileSystem directDfs = ((DistributedFileSystem) (fs));
        Configuration cconf = getCustomSocketConfigs(nameNodePort);
        fs = FileSystem.get(cconf);
        Assert.assertTrue((fs instanceof DistributedFileSystem));
        DistributedFileSystem dfs = ((DistributedFileSystem) (fs));
        JobClient client = null;
        MiniMRYarnCluster miniMRYarnCluster = null;
        try {
            // This will test RPC to the NameNode only.
            // could we test Client-DataNode connections?
            Path filePath = new Path("/dir");
            Assert.assertFalse(directDfs.exists(filePath));
            Assert.assertFalse(dfs.exists(filePath));
            directDfs.mkdirs(filePath);
            Assert.assertTrue(directDfs.exists(filePath));
            Assert.assertTrue(dfs.exists(filePath));
            // This will test RPC to a Resource Manager
            fs = FileSystem.get(sconf);
            JobConf jobConf = new JobConf();
            FileSystem.setDefaultUri(jobConf, fs.getUri().toString());
            miniMRYarnCluster = initAndStartMiniMRYarnCluster(jobConf);
            JobConf jconf = new JobConf(getConfig());
            jconf.set("hadoop.rpc.socket.factory.class.default", "org.apache.hadoop.ipc.DummySocketFactory");
            jconf.set(FRAMEWORK_NAME, YARN_FRAMEWORK_NAME);
            String rmAddress = jconf.get(RM_ADDRESS);
            String[] split = rmAddress.split(":");
            jconf.set(RM_ADDRESS, (((split[0]) + ':') + ((Integer.parseInt(split[1])) + 10)));
            client = new JobClient(jconf);
            JobStatus[] jobs = client.jobsToComplete();
            Assert.assertTrue(((jobs.length) == 0));
        } finally {
            closeClient(client);
            closeDfs(dfs);
            closeDfs(directDfs);
            stopMiniMRYarnCluster(miniMRYarnCluster);
            shutdownDFSCluster(cluster);
        }
    }
}

