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
package org.apache.hadoop.mapreduce.v2;


import java.io.IOException;
import java.util.Iterator;
import org.apache.hadoop.CustomOutputCommitter;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.TextInputFormat;
import org.apache.hadoop.mapred.lib.IdentityMapper;
import org.apache.hadoop.mapred.lib.IdentityReducer;
import org.apache.hadoop.mapreduce.filecache.DistributedCache;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@SuppressWarnings("deprecation")
public class TestMRAppWithCombiner {
    protected static MiniMRYarnCluster mrCluster;

    private static Configuration conf = new Configuration();

    private static FileSystem localFs;

    private static final Logger LOG = LoggerFactory.getLogger(TestMRAppWithCombiner.class);

    static {
        try {
            TestMRAppWithCombiner.localFs = FileSystem.getLocal(TestMRAppWithCombiner.conf);
        } catch (IOException io) {
            throw new RuntimeException("problem getting local fs", io);
        }
    }

    @Test
    public void testCombinerShouldUpdateTheReporter() throws Exception {
        JobConf conf = new JobConf(getConfig());
        int numMaps = 5;
        int numReds = 2;
        Path in = new Path(getTestWorkDir().getAbsolutePath(), "testCombinerShouldUpdateTheReporter-in");
        Path out = new Path(getTestWorkDir().getAbsolutePath(), "testCombinerShouldUpdateTheReporter-out");
        TestMRAppWithCombiner.createInputOutPutFolder(in, out, numMaps);
        conf.setJobName("test-job-with-combiner");
        conf.setMapperClass(IdentityMapper.class);
        conf.setCombinerClass(TestMRAppWithCombiner.MyCombinerToCheckReporter.class);
        // conf.setJarByClass(MyCombinerToCheckReporter.class);
        conf.setReducerClass(IdentityReducer.class);
        DistributedCache.addFileToClassPath(TestMRJobs.APP_JAR, conf);
        conf.setOutputCommitter(CustomOutputCommitter.class);
        conf.setInputFormat(TextInputFormat.class);
        conf.setOutputKeyClass(LongWritable.class);
        conf.setOutputValueClass(Text.class);
        FileInputFormat.setInputPaths(conf, in);
        FileOutputFormat.setOutputPath(conf, out);
        conf.setNumMapTasks(numMaps);
        conf.setNumReduceTasks(numReds);
        TestMRAppWithCombiner.runJob(conf);
    }

    class MyCombinerToCheckReporter<K, V> extends IdentityReducer<K, V> {
        public void reduce(K key, Iterator<V> values, OutputCollector<K, V> output, Reporter reporter) throws IOException {
            if ((Reporter.NULL) == reporter) {
                Assert.fail("A valid Reporter should have been used but, Reporter.NULL is used");
            }
        }
    }
}

