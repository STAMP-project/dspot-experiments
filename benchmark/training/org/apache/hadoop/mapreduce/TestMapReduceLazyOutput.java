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
package org.apache.hadoop.mapreduce;


import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MiniMRCluster;
import org.apache.hadoop.mapred.Utils;
import org.junit.Assert;
import org.junit.Test;


/**
 * A JUnit test to test the Map-Reduce framework's feature to create part
 * files only if there is an explicit output.collect. This helps in preventing
 * 0 byte files
 */
public class TestMapReduceLazyOutput {
    private static final int NUM_HADOOP_WORKERS = 3;

    private static final int NUM_MAPS_PER_NODE = 2;

    private static final Path INPUTPATH = new Path("/testlazy/input");

    private static final List<String> INPUTLIST = Arrays.asList("All", "Roads", "Lead", "To", "Hadoop");

    /**
     * Test mapper.
     */
    public static class TestMapper extends Mapper<LongWritable, Text, LongWritable, Text> {
        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String id = context.getTaskAttemptID().toString();
            // Mapper 0 does not output anything
            if (!(id.endsWith("0_0"))) {
                context.write(key, value);
            }
        }
    }

    /**
     * Test Reducer.
     */
    public static class TestReducer extends Reducer<LongWritable, Text, LongWritable, Text> {
        public void reduce(LongWritable key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            String id = context.getTaskAttemptID().toString();
            // Reducer 0 does not output anything
            if (!(id.endsWith("0_0"))) {
                for (Text val : values) {
                    context.write(key, val);
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
            dfs = numDataNodes(TestMapReduceLazyOutput.NUM_HADOOP_WORKERS).build();
            fileSys = dfs.getFileSystem();
            mr = new MiniMRCluster(TestMapReduceLazyOutput.NUM_HADOOP_WORKERS, fileSys.getUri().toString(), 1);
            int numReducers = 2;
            int numMappers = (TestMapReduceLazyOutput.NUM_HADOOP_WORKERS) * (TestMapReduceLazyOutput.NUM_MAPS_PER_NODE);
            createInput(fileSys, numMappers);
            Path output1 = new Path("/testlazy/output1");
            // Test 1.
            TestMapReduceLazyOutput.runTestLazyOutput(mr.createJobConf(), output1, numReducers, true);
            Path[] fileList = FileUtil.stat2Paths(fileSys.listStatus(output1, new Utils.OutputFileUtils.OutputFilesFilter()));
            for (int i = 0; i < (fileList.length); ++i) {
                System.out.println((((("Test1 File list[" + i) + "]") + ": ") + (fileList[i])));
            }
            Assert.assertTrue(((fileList.length) == (numReducers - 1)));
            // Test 2. 0 Reducers, maps directly write to the output files
            Path output2 = new Path("/testlazy/output2");
            TestMapReduceLazyOutput.runTestLazyOutput(mr.createJobConf(), output2, 0, true);
            fileList = FileUtil.stat2Paths(fileSys.listStatus(output2, new Utils.OutputFileUtils.OutputFilesFilter()));
            for (int i = 0; i < (fileList.length); ++i) {
                System.out.println((((("Test2 File list[" + i) + "]") + ": ") + (fileList[i])));
            }
            Assert.assertTrue(((fileList.length) == (numMappers - 1)));
            // Test 3. 0 Reducers, but flag is turned off
            Path output3 = new Path("/testlazy/output3");
            TestMapReduceLazyOutput.runTestLazyOutput(mr.createJobConf(), output3, 0, false);
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

