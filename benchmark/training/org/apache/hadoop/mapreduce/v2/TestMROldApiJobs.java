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


import java.io.File;
import java.io.IOException;
import org.apache.hadoop.CustomOutputCommitter;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapred.JobConf;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestMROldApiJobs {
    private static final Logger LOG = LoggerFactory.getLogger(TestMROldApiJobs.class);

    protected static MiniMRYarnCluster mrCluster;

    private static Configuration conf = new Configuration();

    private static FileSystem localFs;

    static {
        try {
            TestMROldApiJobs.localFs = FileSystem.getLocal(TestMROldApiJobs.conf);
        } catch (IOException io) {
            throw new RuntimeException("problem getting local fs", io);
        }
    }

    @Test
    public void testJobSucceed() throws IOException, ClassNotFoundException, InterruptedException {
        TestMROldApiJobs.LOG.info("\n\n\nStarting testJobSucceed().");
        if (!(new File(MiniMRYarnCluster.APPJAR).exists())) {
            TestMROldApiJobs.LOG.info((("MRAppJar " + (MiniMRYarnCluster.APPJAR)) + " not found. Not running test."));
            return;
        }
        JobConf conf = new JobConf(getConfig());
        Path in = new Path(getTestWorkDir().getAbsolutePath(), "in");
        Path out = new Path(getTestWorkDir().getAbsolutePath(), "out");
        TestMROldApiJobs.runJobSucceed(conf, in, out);
        FileSystem fs = FileSystem.get(conf);
        Assert.assertTrue(fs.exists(new Path(out, CustomOutputCommitter.JOB_SETUP_FILE_NAME)));
        Assert.assertFalse(fs.exists(new Path(out, CustomOutputCommitter.JOB_ABORT_FILE_NAME)));
        Assert.assertTrue(fs.exists(new Path(out, CustomOutputCommitter.JOB_COMMIT_FILE_NAME)));
        Assert.assertTrue(fs.exists(new Path(out, CustomOutputCommitter.TASK_SETUP_FILE_NAME)));
        Assert.assertFalse(fs.exists(new Path(out, CustomOutputCommitter.TASK_ABORT_FILE_NAME)));
        Assert.assertTrue(fs.exists(new Path(out, CustomOutputCommitter.TASK_COMMIT_FILE_NAME)));
    }

    @Test
    public void testJobFail() throws IOException, ClassNotFoundException, InterruptedException {
        TestMROldApiJobs.LOG.info("\n\n\nStarting testJobFail().");
        if (!(new File(MiniMRYarnCluster.APPJAR).exists())) {
            TestMROldApiJobs.LOG.info((("MRAppJar " + (MiniMRYarnCluster.APPJAR)) + " not found. Not running test."));
            return;
        }
        JobConf conf = new JobConf(getConfig());
        Path in = new Path(getTestWorkDir().getAbsolutePath(), "fail-in");
        Path out = new Path(getTestWorkDir().getAbsolutePath(), "fail-out");
        TestMROldApiJobs.runJobFail(conf, in, out);
        FileSystem fs = FileSystem.get(conf);
        Assert.assertTrue(fs.exists(new Path(out, CustomOutputCommitter.JOB_SETUP_FILE_NAME)));
        Assert.assertTrue(fs.exists(new Path(out, CustomOutputCommitter.JOB_ABORT_FILE_NAME)));
        Assert.assertFalse(fs.exists(new Path(out, CustomOutputCommitter.JOB_COMMIT_FILE_NAME)));
        Assert.assertTrue(fs.exists(new Path(out, CustomOutputCommitter.TASK_SETUP_FILE_NAME)));
        Assert.assertTrue(fs.exists(new Path(out, CustomOutputCommitter.TASK_ABORT_FILE_NAME)));
        Assert.assertFalse(fs.exists(new Path(out, CustomOutputCommitter.TASK_COMMIT_FILE_NAME)));
    }
}

