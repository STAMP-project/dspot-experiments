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


import JTConfig.JT_SYSTEM_DIR;
import java.io.IOException;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A JUnit test to test Job System Directory with Mini-DFS.
 */
public class TestJobSysDirWithDFS {
    private static final Logger LOG = LoggerFactory.getLogger(TestJobSysDirWithDFS.class);

    static final int NUM_MAPS = 10;

    static final int NUM_SAMPLES = 100000;

    public static class TestResult {
        public String output;

        public RunningJob job;

        TestResult(RunningJob job, String output) {
            this.job = job;
            this.output = output;
        }
    }

    @Test
    public void testWithDFS() throws IOException {
        MiniDFSCluster dfs = null;
        MiniMRCluster mr = null;
        FileSystem fileSys = null;
        try {
            final int taskTrackers = 4;
            JobConf conf = new JobConf();
            conf.set(JT_SYSTEM_DIR, "/tmp/custom/mapred/system");
            dfs = numDataNodes(4).build();
            fileSys = dfs.getFileSystem();
            mr = new MiniMRCluster(taskTrackers, fileSys.getUri().toString(), 1, null, null, conf);
            TestJobSysDirWithDFS.runWordCount(mr, mr.createJobConf(), conf.get("mapred.system.dir"));
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

