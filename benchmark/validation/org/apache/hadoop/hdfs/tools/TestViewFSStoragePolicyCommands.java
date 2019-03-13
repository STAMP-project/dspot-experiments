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
package org.apache.hadoop.hdfs.tools;


import java.net.InetSocketAddress;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DFSTestUtil;
import org.junit.Test;


/**
 * Test StoragePolicyAdmin commands with ViewFileSystem.
 */
public class TestViewFSStoragePolicyCommands extends TestStoragePolicyCommands {
    /**
     * Storage policy operation on the viewfs root should fail.
     */
    @Test
    public void testStoragePolicyRoot() throws Exception {
        final StoragePolicyAdmin admin = new StoragePolicyAdmin(TestStoragePolicyCommands.conf);
        DFSTestUtil.toolRun(admin, "-getStoragePolicy -path /", 2, "is not supported for filesystem viewfs on path /");
    }

    @Test
    public void testStoragePolicyCommandPathWithSchema() throws Exception {
        Path base1 = new Path("/user1");
        final Path bar = new Path(base1, "bar");
        DFSTestUtil.createFile(TestStoragePolicyCommands.cluster.getFileSystem(0), bar, 1024, ((short) (1)), 0);
        // Test with hdfs:// schema
        String pathHdfsSchema = (("hdfs://" + (TestStoragePolicyCommands.cluster.getNameNode(0).getClientNamenodeAddress())) + "/") + (bar.toString());
        checkCommandsWithUriPath(pathHdfsSchema);
        // Test with webhdfs:// schema
        InetSocketAddress httpAddress = TestStoragePolicyCommands.cluster.getNameNode(0).getHttpAddress();
        String pathWebhdfsSchema = (((("webhdfs://" + (httpAddress.getHostName())) + ":") + (httpAddress.getPort())) + "/") + (bar.toString());
        checkCommandsWithUriPath(pathWebhdfsSchema);
    }
}

