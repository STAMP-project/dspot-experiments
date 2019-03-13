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
package org.apache.hadoop.tools.mapred;


import DistCpConstants.CONF_LABEL_TARGET_FINAL_PATH;
import DistCpConstants.CONF_LABEL_TARGET_WORK_PATH;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static TaskType.MAP;


public class TestCopyOutputFormat {
    private static final Logger LOG = LoggerFactory.getLogger(TestCopyOutputFormat.class);

    @Test
    public void testSetCommitDirectory() {
        try {
            Job job = Job.getInstance(new Configuration());
            Assert.assertEquals(null, CopyOutputFormat.getCommitDirectory(job));
            job.getConfiguration().set(CONF_LABEL_TARGET_FINAL_PATH, "");
            Assert.assertEquals(null, CopyOutputFormat.getCommitDirectory(job));
            Path directory = new Path("/tmp/test");
            CopyOutputFormat.setCommitDirectory(job, directory);
            Assert.assertEquals(directory, CopyOutputFormat.getCommitDirectory(job));
            Assert.assertEquals(directory.toString(), job.getConfiguration().get(CONF_LABEL_TARGET_FINAL_PATH));
        } catch (IOException e) {
            TestCopyOutputFormat.LOG.error("Exception encountered while running test", e);
            Assert.fail("Failed while testing for set Commit Directory");
        }
    }

    @Test
    public void testSetWorkingDirectory() {
        try {
            Job job = Job.getInstance(new Configuration());
            Assert.assertEquals(null, CopyOutputFormat.getWorkingDirectory(job));
            job.getConfiguration().set(CONF_LABEL_TARGET_WORK_PATH, "");
            Assert.assertEquals(null, CopyOutputFormat.getWorkingDirectory(job));
            Path directory = new Path("/tmp/test");
            CopyOutputFormat.setWorkingDirectory(job, directory);
            Assert.assertEquals(directory, CopyOutputFormat.getWorkingDirectory(job));
            Assert.assertEquals(directory.toString(), job.getConfiguration().get(CONF_LABEL_TARGET_WORK_PATH));
        } catch (IOException e) {
            TestCopyOutputFormat.LOG.error("Exception encountered while running test", e);
            Assert.fail("Failed while testing for set Working Directory");
        }
    }

    @Test
    public void testGetOutputCommitter() {
        try {
            TaskAttemptContext context = new org.apache.hadoop.mapreduce.task.TaskAttemptContextImpl(new Configuration(), new TaskAttemptID("200707121733", 1, MAP, 1, 1));
            context.getConfiguration().set("mapred.output.dir", "/out");
            Assert.assertTrue(((new CopyOutputFormat().getOutputCommitter(context)) instanceof CopyCommitter));
        } catch (IOException e) {
            TestCopyOutputFormat.LOG.error("Exception encountered ", e);
            Assert.fail("Unable to get output committer");
        }
    }

    @Test
    public void testCheckOutputSpecs() {
        try {
            OutputFormat outputFormat = new CopyOutputFormat();
            Job job = Job.getInstance(new Configuration());
            JobID jobID = new JobID("200707121733", 1);
            try {
                JobContext context = new org.apache.hadoop.mapreduce.task.JobContextImpl(job.getConfiguration(), jobID);
                outputFormat.checkOutputSpecs(context);
                Assert.fail("No checking for invalid work/commit path");
            } catch (IllegalStateException ignore) {
            }
            CopyOutputFormat.setWorkingDirectory(job, new Path("/tmp/work"));
            try {
                JobContext context = new org.apache.hadoop.mapreduce.task.JobContextImpl(job.getConfiguration(), jobID);
                outputFormat.checkOutputSpecs(context);
                Assert.fail("No checking for invalid commit path");
            } catch (IllegalStateException ignore) {
            }
            job.getConfiguration().set(CONF_LABEL_TARGET_WORK_PATH, "");
            CopyOutputFormat.setCommitDirectory(job, new Path("/tmp/commit"));
            try {
                JobContext context = new org.apache.hadoop.mapreduce.task.JobContextImpl(job.getConfiguration(), jobID);
                outputFormat.checkOutputSpecs(context);
                Assert.fail("No checking for invalid work path");
            } catch (IllegalStateException ignore) {
            }
            CopyOutputFormat.setWorkingDirectory(job, new Path("/tmp/work"));
            CopyOutputFormat.setCommitDirectory(job, new Path("/tmp/commit"));
            try {
                JobContext context = new org.apache.hadoop.mapreduce.task.JobContextImpl(job.getConfiguration(), jobID);
                outputFormat.checkOutputSpecs(context);
            } catch (IllegalStateException ignore) {
                Assert.fail("Output spec check failed.");
            }
        } catch (IOException e) {
            TestCopyOutputFormat.LOG.error("Exception encountered while testing checkoutput specs", e);
            Assert.fail("Checkoutput Spec failure");
        } catch (InterruptedException e) {
            TestCopyOutputFormat.LOG.error("Exception encountered while testing checkoutput specs", e);
            Assert.fail("Checkoutput Spec failure");
        }
    }
}

