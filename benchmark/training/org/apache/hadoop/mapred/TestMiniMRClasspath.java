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
package org.apache.hadoop.mapred;


import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.junit.Assert;
import org.junit.Test;


/**
 * A JUnit test to test Mini Map-Reduce Cluster with multiple directories
 * and check for correct classpath
 */
public class TestMiniMRClasspath {
    @Test
    public void testClassPath() throws IOException {
        String namenode = null;
        MiniDFSCluster dfs = null;
        MiniMRCluster mr = null;
        FileSystem fileSys = null;
        try {
            final int taskTrackers = 4;
            final int jobTrackerPort = 60050;
            Configuration conf = new Configuration();
            dfs = build();
            fileSys = dfs.getFileSystem();
            namenode = fileSys.getUri().toString();
            mr = new MiniMRCluster(taskTrackers, namenode, 3);
            JobConf jobConf = mr.createJobConf();
            String result;
            result = TestMiniMRClasspath.launchWordCount(fileSys.getUri(), jobConf, ("The quick brown fox\nhas many silly\n" + "red fox sox\n"), 3, 1);
            Assert.assertEquals(("The\t1\nbrown\t1\nfox\t2\nhas\t1\nmany\t1\n" + "quick\t1\nred\t1\nsilly\t1\nsox\t1\n"), result);
        } finally {
            if (dfs != null) {
                dfs.shutdown();
            }
            if (mr != null) {
                mr.shutdown();
            }
        }
    }

    @Test
    public void testExternalWritable() throws IOException {
        String namenode = null;
        MiniDFSCluster dfs = null;
        MiniMRCluster mr = null;
        FileSystem fileSys = null;
        try {
            final int taskTrackers = 4;
            Configuration conf = new Configuration();
            dfs = build();
            fileSys = dfs.getFileSystem();
            namenode = fileSys.getUri().toString();
            mr = new MiniMRCluster(taskTrackers, namenode, 3);
            JobConf jobConf = mr.createJobConf();
            String result;
            result = TestMiniMRClasspath.launchExternal(fileSys.getUri(), jobConf, "Dennis was here!\nDennis again!", 3, 1);
            Assert.assertEquals("Dennis again!\t1\nDennis was here!\t1\n", result);
        } finally {
            if (dfs != null) {
                dfs.shutdown();
            }
            if (mr != null) {
                mr.shutdown();
            }
        }
    }
}

