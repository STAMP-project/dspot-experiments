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


import MRJobConfig.INDEX_CACHE_MEMORY_LIMIT;
import MRJobConfig.IO_SORT_MB;
import MRJobConfig.MAP_SORT_SPILL_PERCENT;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.security.UserGroupInformation;
import org.junit.Test;


/**
 * A JUnit test to test Mini Map-Reduce Cluster with Mini-DFS.
 */
public class TestMiniMRWithDFSWithDistinctUsers {
    static final UserGroupInformation DFS_UGI = TestMiniMRWithDFSWithDistinctUsers.createUGI("dfs", true);

    static final UserGroupInformation ALICE_UGI = TestMiniMRWithDFSWithDistinctUsers.createUGI("alice", false);

    static final UserGroupInformation BOB_UGI = TestMiniMRWithDFSWithDistinctUsers.createUGI("bob", false);

    MiniMRCluster mr = null;

    MiniDFSCluster dfs = null;

    FileSystem fs = null;

    Configuration conf = new Configuration();

    @Test
    public void testDistinctUsers() throws Exception {
        JobConf job1 = mr.createJobConf();
        String input = "The quick brown fox\nhas many silly\n" + "red fox sox\n";
        Path inDir = new Path("/testing/distinct/input");
        Path outDir = new Path("/user/alice/output");
        TestMiniMRClasspath.configureWordCount(fs, job1, input, 2, 1, inDir, outDir);
        runJobAsUser(job1, TestMiniMRWithDFSWithDistinctUsers.ALICE_UGI);
        JobConf job2 = mr.createJobConf();
        Path inDir2 = new Path("/testing/distinct/input2");
        Path outDir2 = new Path("/user/bob/output2");
        TestMiniMRClasspath.configureWordCount(fs, job2, input, 2, 1, inDir2, outDir2);
        runJobAsUser(job2, TestMiniMRWithDFSWithDistinctUsers.BOB_UGI);
    }

    /**
     * Regression test for MAPREDUCE-2327. Verifies that, even if a map
     * task makes lots of spills (more than fit in the spill index cache)
     * that it will succeed.
     */
    @Test
    public void testMultipleSpills() throws Exception {
        JobConf job1 = mr.createJobConf();
        // Make sure it spills twice
        job1.setFloat(MAP_SORT_SPILL_PERCENT, 1.0E-4F);
        job1.setInt(IO_SORT_MB, 1);
        // Make sure the spill records don't fit in index cache
        job1.setInt(INDEX_CACHE_MEMORY_LIMIT, 0);
        String input = "The quick brown fox\nhas many silly\n" + "red fox sox\n";
        Path inDir = new Path("/testing/distinct/input");
        Path outDir = new Path("/user/alice/output");
        TestMiniMRClasspath.configureWordCount(fs, job1, input, 2, 1, inDir, outDir);
        runJobAsUser(job1, TestMiniMRWithDFSWithDistinctUsers.ALICE_UGI);
    }
}

