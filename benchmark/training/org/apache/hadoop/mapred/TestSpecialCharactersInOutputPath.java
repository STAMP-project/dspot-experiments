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
import org.apache.hadoop.util.Progressable;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A JUnit test to test that jobs' output filenames are not HTML-encoded (cf HADOOP-1795).
 */
public class TestSpecialCharactersInOutputPath {
    private static final Logger LOG = LoggerFactory.getLogger(TestSpecialCharactersInOutputPath.class);

    private static final String OUTPUT_FILENAME = "result[0]";

    @Test
    public void testJobWithDFS() throws IOException {
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
            mr = new MiniMRCluster(taskTrackers, namenode, 2);
            JobConf jobConf = new JobConf();
            boolean result;
            result = TestSpecialCharactersInOutputPath.launchJob(fileSys.getUri(), jobConf, 3, 1);
            Assert.assertTrue(result);
        } finally {
            if (dfs != null) {
                dfs.shutdown();
            }
            if (mr != null) {
                mr.shutdown();
            }
        }
    }

    /**
     * generates output filenames with special characters
     */
    static class SpecialTextOutputFormat<K, V> extends TextOutputFormat<K, V> {
        @Override
        public RecordWriter<K, V> getRecordWriter(FileSystem ignored, JobConf job, String name, Progressable progress) throws IOException {
            return super.getRecordWriter(ignored, job, TestSpecialCharactersInOutputPath.OUTPUT_FILENAME, progress);
        }
    }
}

