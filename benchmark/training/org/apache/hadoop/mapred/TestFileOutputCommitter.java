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
import java.net.URI;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RawLocalFileSystem;
import org.apache.hadoop.io.Text;
import org.junit.Test;


@SuppressWarnings("unchecked")
public class TestFileOutputCommitter {
    private static Path outDir = new Path(System.getProperty("test.build.data", "/tmp"), "output");

    // A random task attempt id for testing.
    private static String attempt = "attempt_200707121733_0001_m_000000_0";

    private static String partFile = "part-00000";

    private static TaskAttemptID taskID = TaskAttemptID.forName(TestFileOutputCommitter.attempt);

    private Text key1 = new Text("key1");

    private Text key2 = new Text("key2");

    private Text val1 = new Text("val1");

    private Text val2 = new Text("val2");

    @Test
    public void testRecoveryV1() throws Exception {
        testRecoveryInternal(1, 1);
    }

    @Test
    public void testRecoveryV2() throws Exception {
        testRecoveryInternal(2, 2);
    }

    @Test
    public void testRecoveryUpgradeV1V2() throws Exception {
        testRecoveryInternal(1, 2);
    }

    @Test
    public void testCommitterWithFailureV1() throws Exception {
        testCommitterWithFailureInternal(1, 1);
        testCommitterWithFailureInternal(1, 2);
    }

    @Test
    public void testCommitterWithFailureV2() throws Exception {
        testCommitterWithFailureInternal(2, 1);
        testCommitterWithFailureInternal(2, 2);
    }

    @Test
    public void testCommitterWithDuplicatedCommitV1() throws Exception {
        testCommitterWithDuplicatedCommitInternal(1);
    }

    @Test
    public void testCommitterWithDuplicatedCommitV2() throws Exception {
        testCommitterWithDuplicatedCommitInternal(2);
    }

    @Test
    public void testCommitterV1() throws Exception {
        testCommitterInternal(1);
    }

    @Test
    public void testCommitterV2() throws Exception {
        testCommitterInternal(2);
    }

    @Test
    public void testMapFileOutputCommitterV1() throws Exception {
        testMapFileOutputCommitterInternal(1);
    }

    @Test
    public void testMapFileOutputCommitterV2() throws Exception {
        testMapFileOutputCommitterInternal(2);
    }

    @Test
    public void testMapOnlyNoOutputV1() throws Exception {
        testMapOnlyNoOutputInternal(1);
    }

    @Test
    public void testMapOnlyNoOutputV2() throws Exception {
        testMapOnlyNoOutputInternal(2);
    }

    @Test
    public void testAbortV1() throws Exception {
        testAbortInternal(1);
    }

    @Test
    public void testAbortV2() throws Exception {
        testAbortInternal(2);
    }

    public static class FakeFileSystem extends RawLocalFileSystem {
        public FakeFileSystem() {
            super();
        }

        public URI getUri() {
            return URI.create("faildel:///");
        }

        @Override
        public boolean delete(Path p, boolean recursive) throws IOException {
            throw new IOException("fake delete failed");
        }
    }

    @Test
    public void testFailAbortV1() throws Exception {
        testFailAbortInternal(1);
    }

    @Test
    public void testFailAbortV2() throws Exception {
        testFailAbortInternal(2);
    }

    /**
     * The class provides a overrided implementation of commitJobInternal which
     * causes the commit failed for the first time then succeed.
     */
    public static class CommitterWithFailedThenSucceed extends FileOutputCommitter {
        boolean firstTimeFail = true;

        public CommitterWithFailedThenSucceed() throws IOException {
            super();
        }

        @Override
        public void commitJob(JobContext context) throws IOException {
            JobConf conf = context.getJobConf();
            org.apache.hadoop.mapreduce.lib.output.FileOutputCommitter wrapped = new TestFileOutputCommitter.CommitterFailedFirst(FileOutputFormat.getOutputPath(conf), context);
            wrapped.commitJob(context);
        }
    }

    public static class CommitterFailedFirst extends org.apache.hadoop.mapreduce.lib.output.FileOutputCommitter {
        boolean firstTimeFail = true;

        public CommitterFailedFirst(Path outputPath, JobContext context) throws IOException {
            super(outputPath, context);
        }

        @Override
        protected void commitJobInternal(org.apache.hadoop.mapreduce.JobContext context) throws IOException {
            super.commitJobInternal(context);
            if (firstTimeFail) {
                firstTimeFail = false;
                throw new IOException();
            } else {
                // succeed then, nothing to do
            }
        }
    }
}

