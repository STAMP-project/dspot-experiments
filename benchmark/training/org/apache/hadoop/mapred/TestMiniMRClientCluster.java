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


import JHAdminConfig.MR_HISTORY_ADDRESS;
import JHAdminConfig.MR_HISTORY_WEBAPP_ADDRESS;
import YarnConfiguration.RM_ADDRESS;
import YarnConfiguration.RM_ADMIN_ADDRESS;
import YarnConfiguration.RM_RESOURCE_TRACKER_ADDRESS;
import YarnConfiguration.RM_SCHEDULER_ADDRESS;
import YarnConfiguration.RM_WEBAPP_ADDRESS;
import java.io.IOException;
import java.util.StringTokenizer;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.junit.Assert;
import org.junit.Test;


/**
 * Basic testing for the MiniMRClientCluster. This test shows an example class
 * that can be used in MR1 or MR2, without any change to the test. The test will
 * use MiniMRYarnCluster in MR2, and MiniMRCluster in MR1.
 */
public class TestMiniMRClientCluster {
    private static Path inDir = null;

    private static Path outDir = null;

    private static Path testdir = null;

    private static Path[] inFiles = new Path[5];

    private static MiniMRClientCluster mrCluster;

    private class InternalClass {}

    @Test
    public void testRestart() throws Exception {
        String rmAddress1 = TestMiniMRClientCluster.mrCluster.getConfig().get(RM_ADDRESS);
        String rmAdminAddress1 = TestMiniMRClientCluster.mrCluster.getConfig().get(RM_ADMIN_ADDRESS);
        String rmSchedAddress1 = TestMiniMRClientCluster.mrCluster.getConfig().get(RM_SCHEDULER_ADDRESS);
        String rmRstrackerAddress1 = TestMiniMRClientCluster.mrCluster.getConfig().get(RM_RESOURCE_TRACKER_ADDRESS);
        String rmWebAppAddress1 = TestMiniMRClientCluster.mrCluster.getConfig().get(RM_WEBAPP_ADDRESS);
        String mrHistAddress1 = TestMiniMRClientCluster.mrCluster.getConfig().get(MR_HISTORY_ADDRESS);
        String mrHistWebAppAddress1 = TestMiniMRClientCluster.mrCluster.getConfig().get(MR_HISTORY_WEBAPP_ADDRESS);
        TestMiniMRClientCluster.mrCluster.restart();
        String rmAddress2 = TestMiniMRClientCluster.mrCluster.getConfig().get(RM_ADDRESS);
        String rmAdminAddress2 = TestMiniMRClientCluster.mrCluster.getConfig().get(RM_ADMIN_ADDRESS);
        String rmSchedAddress2 = TestMiniMRClientCluster.mrCluster.getConfig().get(RM_SCHEDULER_ADDRESS);
        String rmRstrackerAddress2 = TestMiniMRClientCluster.mrCluster.getConfig().get(RM_RESOURCE_TRACKER_ADDRESS);
        String rmWebAppAddress2 = TestMiniMRClientCluster.mrCluster.getConfig().get(RM_WEBAPP_ADDRESS);
        String mrHistAddress2 = TestMiniMRClientCluster.mrCluster.getConfig().get(MR_HISTORY_ADDRESS);
        String mrHistWebAppAddress2 = TestMiniMRClientCluster.mrCluster.getConfig().get(MR_HISTORY_WEBAPP_ADDRESS);
        Assert.assertEquals(((("Address before restart: " + rmAddress1) + " is different from new address: ") + rmAddress2), rmAddress1, rmAddress2);
        Assert.assertEquals(((("Address before restart: " + rmAdminAddress1) + " is different from new address: ") + rmAdminAddress2), rmAdminAddress1, rmAdminAddress2);
        Assert.assertEquals(((("Address before restart: " + rmSchedAddress1) + " is different from new address: ") + rmSchedAddress2), rmSchedAddress1, rmSchedAddress2);
        Assert.assertEquals(((("Address before restart: " + rmRstrackerAddress1) + " is different from new address: ") + rmRstrackerAddress2), rmRstrackerAddress1, rmRstrackerAddress2);
        Assert.assertEquals(((("Address before restart: " + rmWebAppAddress1) + " is different from new address: ") + rmWebAppAddress2), rmWebAppAddress1, rmWebAppAddress2);
        Assert.assertEquals(((("Address before restart: " + mrHistAddress1) + " is different from new address: ") + mrHistAddress2), mrHistAddress1, mrHistAddress2);
        Assert.assertEquals(((("Address before restart: " + mrHistWebAppAddress1) + " is different from new address: ") + mrHistWebAppAddress2), mrHistWebAppAddress1, mrHistWebAppAddress2);
    }

    @Test
    public void testJob() throws Exception {
        final Job job = TestMiniMRClientCluster.createJob();
        org.apache.hadoop.mapreduce.lib.input.FileInputFormat.setInputPaths(job, TestMiniMRClientCluster.inDir);
        org.apache.hadoop.mapreduce.lib.output.FileOutputFormat.setOutputPath(job, new Path(TestMiniMRClientCluster.outDir, "testJob"));
        Assert.assertTrue(job.waitForCompletion(true));
        validateCounters(job.getCounters(), 5, 25, 5, 5);
    }

    public static class MyMapper extends org.apache.hadoop.mapreduce.Mapper<Object, Text, Text, IntWritable> {
        private static final IntWritable one = new IntWritable(1);

        private Text word = new Text();

        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            context.getCounter("MyCounterGroup", "MAP_INPUT_RECORDS").increment(1);
            StringTokenizer iter = new StringTokenizer(value.toString());
            while (iter.hasMoreTokens()) {
                word.set(iter.nextToken());
                context.write(word, TestMiniMRClientCluster.MyMapper.one);
                context.getCounter("MyCounterGroup", "MAP_OUTPUT_RECORDS").increment(1);
            } 
        }
    }

    public static class MyReducer extends org.apache.hadoop.mapreduce.Reducer<Text, IntWritable, Text, IntWritable> {
        private IntWritable result = new IntWritable();

        public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            context.getCounter("MyCounterGroup", "REDUCE_INPUT_GROUPS").increment(1);
            int sum = 0;
            for (IntWritable val : values) {
                sum += val.get();
            }
            result.set(sum);
            context.write(key, result);
            context.getCounter("MyCounterGroup", "REDUCE_OUTPUT_RECORDS").increment(1);
        }
    }
}

