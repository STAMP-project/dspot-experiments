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
package org.apache.flink.fs.s3hadoop;


import RecoverableWriter.ResumeRecoverable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import org.apache.flink.core.fs.Path;
import org.apache.flink.core.fs.RecoverableFsDataOutputStream;
import org.apache.flink.core.fs.RecoverableWriter;
import org.apache.flink.fs.s3.common.FlinkS3FileSystem;
import org.apache.flink.util.TestLogger;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 * Tests for exception throwing in the
 * {@link org.apache.flink.fs.s3.common.writer.S3RecoverableWriter S3RecoverableWriter}.
 */
public class HadoopS3RecoverableWriterExceptionITCase extends TestLogger {
    // ----------------------- S3 general configuration -----------------------
    private static final long PART_UPLOAD_MIN_SIZE_VALUE = 7L << 20;

    private static final int MAX_CONCURRENT_UPLOADS_VALUE = 2;

    // ----------------------- Test Specific configuration -----------------------
    private static final Random RND = new Random();

    private static Path basePath;

    private static FlinkS3FileSystem fileSystem;

    // this is set for every test @Before
    private Path basePathForTest;

    // ----------------------- Test Data to be used -----------------------
    private static final String testData1 = "THIS IS A TEST 1.";

    private static final String testData2 = "THIS IS A TEST 2.";

    private static final String testData3 = "THIS IS A TEST 3.";

    private static boolean skipped = true;

    @ClassRule
    public static final TemporaryFolder TEMP_FOLDER = new TemporaryFolder();

    @Test(expected = IOException.class)
    public void testExceptionWritingAfterCloseForCommit() throws Exception {
        final Path path = new Path(basePathForTest, "part-0");
        final RecoverableFsDataOutputStream stream = HadoopS3RecoverableWriterExceptionITCase.getFileSystem().createRecoverableWriter().open(path);
        stream.write(HadoopS3RecoverableWriterExceptionITCase.testData1.getBytes(StandardCharsets.UTF_8));
        stream.closeForCommit().getRecoverable();
        stream.write(HadoopS3RecoverableWriterExceptionITCase.testData2.getBytes(StandardCharsets.UTF_8));
    }

    // IMPORTANT FOR THE FOLLOWING TWO TESTS:
    // These tests illustrate a difference in the user-perceived behavior of the different writers.
    // In HDFS this will fail when trying to recover the stream while here is will fail at "commit", i.e.
    // when we try to "publish" the multipart upload and we realize that the MPU is no longer active.
    @Test(expected = IOException.class)
    public void testResumeAfterCommit() throws Exception {
        final RecoverableWriter writer = HadoopS3RecoverableWriterExceptionITCase.getFileSystem().createRecoverableWriter();
        final Path path = new Path(basePathForTest, "part-0");
        final RecoverableFsDataOutputStream stream = writer.open(path);
        stream.write(HadoopS3RecoverableWriterExceptionITCase.testData1.getBytes(StandardCharsets.UTF_8));
        final RecoverableWriter.ResumeRecoverable recoverable = stream.persist();
        stream.write(HadoopS3RecoverableWriterExceptionITCase.testData2.getBytes(StandardCharsets.UTF_8));
        stream.closeForCommit().commit();
        final RecoverableFsDataOutputStream recoveredStream = writer.recover(recoverable);
        recoveredStream.closeForCommit().commit();
    }

    @Test(expected = IOException.class)
    public void testResumeWithWrongOffset() throws Exception {
        // this is a rather unrealistic scenario, but it is to trigger
        // truncation of the file and try to resume with missing data.
        final RecoverableWriter writer = HadoopS3RecoverableWriterExceptionITCase.getFileSystem().createRecoverableWriter();
        final Path path = new Path(basePathForTest, "part-0");
        final RecoverableFsDataOutputStream stream = writer.open(path);
        stream.write(HadoopS3RecoverableWriterExceptionITCase.testData1.getBytes(StandardCharsets.UTF_8));
        final RecoverableWriter.ResumeRecoverable recoverable1 = stream.persist();
        stream.write(HadoopS3RecoverableWriterExceptionITCase.testData2.getBytes(StandardCharsets.UTF_8));
        final RecoverableWriter.ResumeRecoverable recoverable2 = stream.persist();
        stream.write(HadoopS3RecoverableWriterExceptionITCase.testData3.getBytes(StandardCharsets.UTF_8));
        final RecoverableFsDataOutputStream recoveredStream = writer.recover(recoverable1);
        recoveredStream.closeForCommit().commit();
        // this should throw an exception
        final RecoverableFsDataOutputStream newRecoveredStream = writer.recover(recoverable2);
        newRecoveredStream.closeForCommit().commit();
    }
}

