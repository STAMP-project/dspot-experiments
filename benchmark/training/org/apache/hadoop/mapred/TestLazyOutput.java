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


import JobContext.TASK_ATTEMPT_ID;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.junit.Assert;
import org.junit.Test;


/**
 * A JUnit test to test the Map-Reduce framework's feature to create part
 * files only if there is an explicit output.collect. This helps in preventing
 * 0 byte files
 */
public class TestLazyOutput {
    private static final int NUM_HADOOP_WORKERS = 3;

    private static final int NUM_MAPS_PER_NODE = 2;

    private static final Path INPUTPATH = new Path("/testlazy/input");

    private static final List<String> INPUTLIST = Arrays.asList("All", "Roads", "Lead", "To", "Hadoop");

    static class TestMapper extends MapReduceBase implements Mapper<LongWritable, Text, LongWritable, Text> {
        private String id;

        public void configure(JobConf job) {
            id = job.get(TASK_ATTEMPT_ID);
        }

        public void map(LongWritable key, Text val, OutputCollector<LongWritable, Text> output, Reporter reporter) throws IOException {
            // Everybody other than id 0 outputs
            if (!(id.endsWith("0_0"))) {
                output.collect(key, val);
            }
        }
    }

    static class TestReducer extends MapReduceBase implements Reducer<LongWritable, Text, LongWritable, Text> {
        private String id;

        public void configure(JobConf job) {
            id = job.get(TASK_ATTEMPT_ID);
        }

        /**
         * Writes all keys and values directly to output.
         */
        public void reduce(LongWritable key, Iterator<Text> values, OutputCollector<LongWritable, Text> output, Reporter reporter) throws IOException {
            while (values.hasNext()) {
                Text v = values.next();
                // Reducer 0 skips collect
                if (!(id.endsWith("0_0"))) {
                    output.collect(key, v);
                }
            } 
        }
    }

    @Test
    public void testLazyOutput() throws Exception {
        MiniDFSCluster dfs = null;
        MiniMRCluster mr = null;
        FileSystem fileSys = null;
        try {
            Configuration conf = new Configuration();
            // Start the mini-MR and mini-DFS clusters
            dfs = numDataNodes(TestLazyOutput.NUM_HADOOP_WORKERS).build();
            fileSys = dfs.getFileSystem();
            mr = new MiniMRCluster(TestLazyOutput.NUM_HADOOP_WORKERS, fileSys.getUri().toString(), 1);
            int numReducers = 2;
            int numMappers = (TestLazyOutput.NUM_HADOOP_WORKERS) * (TestLazyOutput.NUM_MAPS_PER_NODE);
            createInput(fileSys, numMappers);
            Path output1 = new Path("/testlazy/output1");
            // Test 1.
            TestLazyOutput.runTestLazyOutput(mr.createJobConf(), output1, numReducers, true);
            Path[] fileList = FileUtil.stat2Paths(fileSys.listStatus(output1, new Utils.OutputFileUtils.OutputFilesFilter()));
            for (int i = 0; i < (fileList.length); ++i) {
                System.out.println((((("Test1 File list[" + i) + "]") + ": ") + (fileList[i])));
            }
            Assert.assertTrue(((fileList.length) == (numReducers - 1)));
            // Test 2. 0 Reducers, maps directly write to the output files
            Path output2 = new Path("/testlazy/output2");
            TestLazyOutput.runTestLazyOutput(mr.createJobConf(), output2, 0, true);
            fileList = FileUtil.stat2Paths(fileSys.listStatus(output2, new Utils.OutputFileUtils.OutputFilesFilter()));
            for (int i = 0; i < (fileList.length); ++i) {
                System.out.println((((("Test2 File list[" + i) + "]") + ": ") + (fileList[i])));
            }
            Assert.assertTrue(((fileList.length) == (numMappers - 1)));
            // Test 3. 0 Reducers, but flag is turned off
            Path output3 = new Path("/testlazy/output3");
            TestLazyOutput.runTestLazyOutput(mr.createJobConf(), output3, 0, false);
            fileList = FileUtil.stat2Paths(fileSys.listStatus(output3, new Utils.OutputFileUtils.OutputFilesFilter()));
            for (int i = 0; i < (fileList.length); ++i) {
                System.out.println((((("Test3 File list[" + i) + "]") + ": ") + (fileList[i])));
            }
            Assert.assertTrue(((fileList.length) == numMappers));
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

