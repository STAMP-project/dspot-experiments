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
package org.apache.hadoop.hdfs.server.namenode.ha;


import java.net.URI;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.HdfsConfiguration;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.MiniDFSNNTopology;
import org.junit.Test;


public class TestHarFileSystemWithHA {
    private static final Path TEST_HAR_PATH = new Path("/input.har");

    /**
     * Test that the HarFileSystem works with underlying HDFS URIs that have no
     * port specified, as is often the case with an HA setup.
     */
    @Test
    public void testHarUriWithHaUriWithNoPort() throws Exception {
        Configuration conf = new HdfsConfiguration();
        MiniDFSCluster cluster = null;
        try {
            cluster = new MiniDFSCluster.Builder(conf).numDataNodes(1).nnTopology(MiniDFSNNTopology.simpleHATopology()).build();
            cluster.transitionToActive(0);
            HATestUtil.setFailoverConfigurations(cluster, conf);
            TestHarFileSystemWithHA.createEmptyHarArchive(HATestUtil.configureFailoverFs(cluster, conf), TestHarFileSystemWithHA.TEST_HAR_PATH);
            URI failoverUri = FileSystem.getDefaultUri(conf);
            Path p = new Path((("har://hdfs-" + (failoverUri.getAuthority())) + (TestHarFileSystemWithHA.TEST_HAR_PATH)));
            p.getFileSystem(conf);
        } finally {
            cluster.shutdown();
        }
    }
}

