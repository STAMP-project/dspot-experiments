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


import JobConf.MAPRED_MAP_TASK_JAVA_OPTS;
import JobConf.MAPRED_MAP_TASK_LOG_LEVEL;
import JobConf.MAPRED_REDUCE_TASK_JAVA_OPTS;
import JobConf.MAPRED_REDUCE_TASK_LOG_LEVEL;
import JobConf.MAPRED_TASK_JAVA_OPTS;
import java.io.File;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.HadoopTestCase;
import org.apache.hadoop.mapred.JobConf;
import org.apache.log4j.Level;
import org.junit.Assert;
import org.junit.Test;


public class TestChild extends HadoopTestCase {
    private static String TEST_ROOT_DIR = new File(System.getProperty("test.build.data", "/tmp")).toURI().toString().replace(' ', '+');

    private final Path inDir = new Path(TestChild.TEST_ROOT_DIR, "./wc/input");

    private final Path outDir = new Path(TestChild.TEST_ROOT_DIR, "./wc/output");

    private static final String OLD_CONFIGS = "test.old.configs";

    private static final String TASK_OPTS_VAL = "-Xmx200m";

    private static final String MAP_OPTS_VAL = "-Xmx200m";

    private static final String REDUCE_OPTS_VAL = "-Xmx300m";

    public TestChild() throws IOException {
        super(HadoopTestCase.CLUSTER_MR, HadoopTestCase.LOCAL_FS, 2, 2);
    }

    static class MyMapper extends Mapper<LongWritable, Text, LongWritable, Text> {
        @Override
        protected void setup(Context context) throws IOException, InterruptedException {
            Configuration conf = context.getConfiguration();
            boolean oldConfigs = conf.getBoolean(TestChild.OLD_CONFIGS, false);
            if (oldConfigs) {
                String javaOpts = conf.get(MAPRED_TASK_JAVA_OPTS);
                Assert.assertNotNull(((JobConf.MAPRED_TASK_JAVA_OPTS) + " is null!"), javaOpts);
                Assert.assertEquals((((JobConf.MAPRED_TASK_JAVA_OPTS) + " has value of: ") + javaOpts), javaOpts, TestChild.TASK_OPTS_VAL);
            } else {
                String mapJavaOpts = conf.get(MAPRED_MAP_TASK_JAVA_OPTS);
                Assert.assertNotNull(((JobConf.MAPRED_MAP_TASK_JAVA_OPTS) + " is null!"), mapJavaOpts);
                Assert.assertEquals((((JobConf.MAPRED_MAP_TASK_JAVA_OPTS) + " has value of: ") + mapJavaOpts), mapJavaOpts, TestChild.MAP_OPTS_VAL);
            }
            Level logLevel = Level.toLevel(conf.get(MAPRED_MAP_TASK_LOG_LEVEL, Level.INFO.toString()));
            Assert.assertEquals((((JobConf.MAPRED_MAP_TASK_LOG_LEVEL) + "has value of ") + logLevel), logLevel, Level.OFF);
        }
    }

    static class MyReducer extends Reducer<LongWritable, Text, LongWritable, Text> {
        @Override
        protected void setup(Context context) throws IOException, InterruptedException {
            Configuration conf = context.getConfiguration();
            boolean oldConfigs = conf.getBoolean(TestChild.OLD_CONFIGS, false);
            if (oldConfigs) {
                String javaOpts = conf.get(MAPRED_TASK_JAVA_OPTS);
                Assert.assertNotNull(((JobConf.MAPRED_TASK_JAVA_OPTS) + " is null!"), javaOpts);
                Assert.assertEquals((((JobConf.MAPRED_TASK_JAVA_OPTS) + " has value of: ") + javaOpts), javaOpts, TestChild.TASK_OPTS_VAL);
            } else {
                String reduceJavaOpts = conf.get(MAPRED_REDUCE_TASK_JAVA_OPTS);
                Assert.assertNotNull(((JobConf.MAPRED_REDUCE_TASK_JAVA_OPTS) + " is null!"), reduceJavaOpts);
                Assert.assertEquals((((JobConf.MAPRED_REDUCE_TASK_JAVA_OPTS) + " has value of: ") + reduceJavaOpts), reduceJavaOpts, TestChild.REDUCE_OPTS_VAL);
            }
            Level logLevel = Level.toLevel(conf.get(MAPRED_REDUCE_TASK_LOG_LEVEL, Level.INFO.toString()));
            Assert.assertEquals((((JobConf.MAPRED_REDUCE_TASK_LOG_LEVEL) + "has value of ") + logLevel), logLevel, Level.OFF);
        }
    }

    @Test
    public void testChild() throws Exception {
        try {
            submitAndValidateJob(createJobConf(), 1, 1, true);
            submitAndValidateJob(createJobConf(), 1, 1, false);
        } finally {
            tearDown();
        }
    }

    private static class OutputFilter implements PathFilter {
        public boolean accept(Path path) {
            return !(path.getName().startsWith("_"));
        }
    }
}

