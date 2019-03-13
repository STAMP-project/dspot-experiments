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


import HdfsClientConfigKeys.DFS_USER_HOME_DIR_PREFIX_KEY;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.client.HdfsClientConfigKeys;
import org.junit.Assert;
import org.junit.Test;


/**
 * This class tests the DFS class via the FileSystem interface in a single node
 * mini-cluster.
 */
public class TestLocalDFS {
    /**
     * Tests get/set working directory in DFS.
     */
    @Test(timeout = 20000)
    public void testWorkingDirectory() throws IOException {
        Configuration conf = new HdfsConfiguration();
        MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).build();
        FileSystem fileSys = cluster.getFileSystem();
        try {
            Path orig_path = fileSys.getWorkingDirectory();
            Assert.assertTrue(orig_path.isAbsolute());
            Path file1 = new Path("somewhat/random.txt");
            writeFile(fileSys, file1);
            Assert.assertTrue(fileSys.exists(new Path(orig_path, file1.toString())));
            fileSys.delete(file1, true);
            Path subdir1 = new Path("/somewhere");
            fileSys.setWorkingDirectory(subdir1);
            writeFile(fileSys, file1);
            cleanupFile(fileSys, new Path(subdir1, file1.toString()));
            Path subdir2 = new Path("else");
            fileSys.setWorkingDirectory(subdir2);
            writeFile(fileSys, file1);
            readFile(fileSys, file1);
            cleanupFile(fileSys, new Path(new Path(subdir1, subdir2.toString()), file1.toString()));
            // test home directory
            Path home = fileSys.makeQualified(new Path((((HdfsClientConfigKeys.DFS_USER_HOME_DIR_PREFIX_DEFAULT) + "/") + (TestLocalDFS.getUserName(fileSys)))));
            Path fsHome = fileSys.getHomeDirectory();
            Assert.assertEquals(home, fsHome);
        } finally {
            fileSys.close();
            cluster.shutdown();
        }
    }

    /**
     * Tests get/set working directory in DFS.
     */
    @Test(timeout = 30000)
    public void testHomeDirectory() throws IOException {
        final String[] homeBases = new String[]{ "/home", "/home/user" };
        Configuration conf = new HdfsConfiguration();
        for (final String homeBase : homeBases) {
            conf.set(DFS_USER_HOME_DIR_PREFIX_KEY, homeBase);
            MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).build();
            FileSystem fileSys = cluster.getFileSystem();
            try {
                // test home directory
                Path home = fileSys.makeQualified(new Path(((homeBase + "/") + (TestLocalDFS.getUserName(fileSys)))));
                Path fsHome = fileSys.getHomeDirectory();
                Assert.assertEquals(home, fsHome);
            } finally {
                fileSys.close();
                cluster.shutdown();
            }
        }
    }
}

