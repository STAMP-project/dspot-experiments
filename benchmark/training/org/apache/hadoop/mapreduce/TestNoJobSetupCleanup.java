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


import java.io.File;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileAlreadyExistsException;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;
import org.apache.hadoop.mapred.HadoopTestCase;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.junit.Ignore;
import org.junit.Test;

import static TaskType.JOB_SETUP;


@Ignore
public class TestNoJobSetupCleanup extends HadoopTestCase {
    private static String TEST_ROOT_DIR = new File(System.getProperty("test.build.data", "/tmp")).toURI().toString().replace(' ', '+');

    private final Path inDir = new Path(TestNoJobSetupCleanup.TEST_ROOT_DIR, "./wc/input");

    private final Path outDir = new Path(TestNoJobSetupCleanup.TEST_ROOT_DIR, "./wc/output");

    public TestNoJobSetupCleanup() throws IOException {
        super(HadoopTestCase.CLUSTER_MR, HadoopTestCase.LOCAL_FS, 2, 2);
    }

    @Test
    public void testNoJobSetupCleanup() throws Exception {
        try {
            Configuration conf = createJobConf();
            // run a job without job-setup and cleanup
            submitAndValidateJob(conf, 1, 1);
            // run a map only job.
            submitAndValidateJob(conf, 1, 0);
            // run empty job without job setup and cleanup
            submitAndValidateJob(conf, 0, 0);
            // run empty job without job setup and cleanup, with non-zero reduces
            submitAndValidateJob(conf, 0, 1);
        } finally {
            tearDown();
        }
    }

    public static class MyOutputFormat extends TextOutputFormat {
        public void checkOutputSpecs(JobContext job) throws IOException, FileAlreadyExistsException {
            super.checkOutputSpecs(job);
            // creating dummy TaskAttemptID
            TaskAttemptID tid = new TaskAttemptID("jt", 1, JOB_SETUP, 0, 0);
            getOutputCommitter(new org.apache.hadoop.mapreduce.task.TaskAttemptContextImpl(job.getConfiguration(), tid)).setupJob(job);
        }
    }

    private static class OutputFilter implements PathFilter {
        public boolean accept(Path path) {
            return !(path.getName().startsWith("_"));
        }
    }
}

