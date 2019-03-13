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


import JobConf.MAPRED_MAP_TASK_JAVA_OPTS;
import JobConf.MAPRED_REDUCE_TASK_JAVA_OPTS;
import JobConf.MAPRED_TASK_JAVA_OPTS;
import MRJobConfig.JOB_LOCAL_DIR;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.mapreduce.MRJobConfig;
import org.apache.hadoop.mapreduce.v2.MiniMRYarnCluster;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static JobConf.MAPRED_MAP_TASK_JAVA_OPTS;
import static JobConf.MAPRED_REDUCE_TASK_JAVA_OPTS;
import static JobConf.MAPRED_TASK_JAVA_OPTS;


/**
 * Class to test mapred task's
 *   - temp directory
 *   - child env
 */
public class TestMiniMRChildTask {
    private static final Logger LOG = LoggerFactory.getLogger(TestMiniMRChildTask.class);

    private static final String OLD_CONFIGS = "test.old.configs";

    private static final String TASK_OPTS_VAL = "-Xmx200m";

    private static final String MAP_OPTS_VAL = "-Xmx200m";

    private static final String REDUCE_OPTS_VAL = "-Xmx300m";

    private static MiniMRYarnCluster mr;

    private static MiniDFSCluster dfs;

    private static FileSystem fileSys;

    private static Configuration conf = new Configuration();

    private static FileSystem localFs;

    static {
        try {
            TestMiniMRChildTask.localFs = FileSystem.getLocal(TestMiniMRChildTask.conf);
        } catch (IOException io) {
            throw new RuntimeException("problem getting local fs", io);
        }
    }

    private static Path TEST_ROOT_DIR = TestMiniMRChildTask.localFs.makeQualified(new Path("target", ((TestMiniMRChildTask.class.getName()) + "-tmpDir")));

    static Path APP_JAR = new Path(TestMiniMRChildTask.TEST_ROOT_DIR, "MRAppJar.jar");

    /**
     * Map class which checks whether temp directory exists
     * and check the value of java.io.tmpdir
     * Creates a tempfile and checks whether that is created in
     * temp directory specified.
     */
    public static class MapClass extends MapReduceBase implements Mapper<LongWritable, Text, Text, IntWritable> {
        Path tmpDir;

        public void map(LongWritable key, Text value, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {
            if (TestMiniMRChildTask.localFs.exists(tmpDir)) {
            } else {
                Assert.fail((("Temp directory " + (tmpDir)) + " doesnt exist."));
            }
            File tmpFile = File.createTempFile("test", ".tmp");
        }

        public void configure(JobConf job) {
            tmpDir = new Path(System.getProperty("java.io.tmpdir"));
            try {
                TestMiniMRChildTask.localFs = FileSystem.getLocal(job);
            } catch (IOException ioe) {
                ioe.printStackTrace();
                Assert.fail("IOException in getting localFS");
            }
        }
    }

    // Mappers that simply checks if the desired user env are present or not
    private static class EnvCheckMapper extends MapReduceBase implements Mapper<WritableComparable, Writable, WritableComparable, Writable> {
        @Override
        @SuppressWarnings("deprecation")
        public void configure(JobConf job) {
            boolean oldConfigs = job.getBoolean(TestMiniMRChildTask.OLD_CONFIGS, false);
            if (oldConfigs) {
                String javaOpts = job.get(MAPRED_TASK_JAVA_OPTS);
                Assert.assertNotNull(((MAPRED_TASK_JAVA_OPTS) + " is null!"), javaOpts);
                Assert.assertEquals((((MAPRED_TASK_JAVA_OPTS) + " has value of: ") + javaOpts), javaOpts, TestMiniMRChildTask.TASK_OPTS_VAL);
            } else {
                String mapJavaOpts = job.get(MAPRED_MAP_TASK_JAVA_OPTS);
                Assert.assertNotNull(((MAPRED_MAP_TASK_JAVA_OPTS) + " is null!"), mapJavaOpts);
                Assert.assertEquals((((MAPRED_MAP_TASK_JAVA_OPTS) + " has value of: ") + mapJavaOpts), mapJavaOpts, TestMiniMRChildTask.MAP_OPTS_VAL);
            }
            // check if X=y works for an already existing parameter
            TestMiniMRChildTask.checkEnv("LANG", "en_us_8859_1", "noappend");
            // check if X=/tmp for a new env variable
            TestMiniMRChildTask.checkEnv("MY_PATH", "/tmp", "noappend");
            // check if X=$X:/tmp works for a new env var and results into :/tmp
            TestMiniMRChildTask.checkEnv("NEW_PATH", ((File.pathSeparator) + "/tmp"), "noappend");
            String jobLocalDir = job.get(JOB_LOCAL_DIR);
            Assert.assertNotNull(((MRJobConfig.JOB_LOCAL_DIR) + " is null"), jobLocalDir);
        }

        public void map(WritableComparable key, Writable value, OutputCollector<WritableComparable, Writable> out, Reporter reporter) throws IOException {
        }
    }

    private static class EnvCheckReducer extends MapReduceBase implements Reducer<WritableComparable, Writable, WritableComparable, Writable> {
        @Override
        @SuppressWarnings("deprecation")
        public void configure(JobConf job) {
            boolean oldConfigs = job.getBoolean(TestMiniMRChildTask.OLD_CONFIGS, false);
            if (oldConfigs) {
                String javaOpts = job.get(MAPRED_TASK_JAVA_OPTS);
                Assert.assertNotNull(((JobConf.MAPRED_TASK_JAVA_OPTS) + " is null!"), javaOpts);
                Assert.assertEquals((((JobConf.MAPRED_TASK_JAVA_OPTS) + " has value of: ") + javaOpts), javaOpts, TestMiniMRChildTask.TASK_OPTS_VAL);
            } else {
                String reduceJavaOpts = job.get(MAPRED_REDUCE_TASK_JAVA_OPTS);
                Assert.assertNotNull(((MAPRED_REDUCE_TASK_JAVA_OPTS) + " is null!"), reduceJavaOpts);
                Assert.assertEquals((((MAPRED_REDUCE_TASK_JAVA_OPTS) + " has value of: ") + reduceJavaOpts), reduceJavaOpts, TestMiniMRChildTask.REDUCE_OPTS_VAL);
            }
            // check if X=y works for an already existing parameter
            TestMiniMRChildTask.checkEnv("LANG", "en_us_8859_1", "noappend");
            // check if X=/tmp for a new env variable
            TestMiniMRChildTask.checkEnv("MY_PATH", "/tmp", "noappend");
            // check if X=$X:/tmp works for a new env var and results into :/tmp
            TestMiniMRChildTask.checkEnv("NEW_PATH", ((File.pathSeparator) + "/tmp"), "noappend");
        }

        @Override
        public void reduce(WritableComparable key, Iterator<Writable> values, OutputCollector<WritableComparable, Writable> output, Reporter reporter) throws IOException {
        }
    }

    /**
     * Test to test if the user set env variables reflect in the child
     * processes. Mainly
     *   - x=y (x can be a already existing env variable or a new variable)
     */
    @Test
    public void testTaskEnv() {
        try {
            JobConf conf = new JobConf(getConfig());
            String baseDir = System.getProperty("test.build.data", "build/test/data");
            // initialize input, output directories
            Path inDir = new Path((baseDir + "/testing/wc/input1"));
            Path outDir = new Path((baseDir + "/testing/wc/output1"));
            FileSystem outFs = outDir.getFileSystem(conf);
            runTestTaskEnv(conf, inDir, outDir, false);
            outFs.delete(outDir, true);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Exception in testing child env");
            TestMiniMRChildTask.tearDown();
        }
    }

    /**
     * Test to test if the user set *old* env variables reflect in the child
     * processes. Mainly
     *   - x=y (x can be a already existing env variable or a new variable)
     */
    @Test
    public void testTaskOldEnv() {
        try {
            JobConf conf = new JobConf(getConfig());
            String baseDir = System.getProperty("test.build.data", "build/test/data");
            // initialize input, output directories
            Path inDir = new Path((baseDir + "/testing/wc/input1"));
            Path outDir = new Path((baseDir + "/testing/wc/output1"));
            FileSystem outFs = outDir.getFileSystem(conf);
            runTestTaskEnv(conf, inDir, outDir, true);
            outFs.delete(outDir, true);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Exception in testing child env");
            TestMiniMRChildTask.tearDown();
        }
    }
}

