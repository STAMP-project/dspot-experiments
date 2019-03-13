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


import RecoverableFsDataOutputStream.Committer;
import RecoverableWriter.CommitRecoverable;
import java.io.FileNotFoundException;
import java.util.Random;
import org.apache.flink.core.fs.Path;
import org.apache.flink.core.fs.RecoverableFsDataOutputStream;
import org.apache.flink.core.fs.RecoverableWriter;
import org.apache.flink.core.io.SimpleVersionedSerializer;
import org.apache.flink.fs.s3.common.FlinkS3FileSystem;
import org.apache.flink.fs.s3.common.writer.S3Recoverable;
import org.apache.flink.util.TestLogger;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 * Tests for the {@link org.apache.flink.fs.s3.common.writer.S3RecoverableWriter S3RecoverableWriter}.
 */
public class HadoopS3RecoverableWriterITCase extends TestLogger {
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

    private static final String bigDataChunk = HadoopS3RecoverableWriterITCase.createBigDataChunk(HadoopS3RecoverableWriterITCase.testData1, HadoopS3RecoverableWriterITCase.PART_UPLOAD_MIN_SIZE_VALUE);

    // ----------------------- Test Lifecycle -----------------------
    private static boolean skipped = true;

    @ClassRule
    public static final TemporaryFolder TEMP_FOLDER = new TemporaryFolder();

    // ----------------------- Test Normal Execution -----------------------
    @Test
    public void testCloseWithNoData() throws Exception {
        final RecoverableWriter writer = getRecoverableWriter();
        final Path path = new Path(basePathForTest, "part-0");
        final RecoverableFsDataOutputStream stream = writer.open(path);
        stream.closeForCommit().commit();
    }

    @Test
    public void testCommitAfterNormalClose() throws Exception {
        final RecoverableWriter writer = getRecoverableWriter();
        final Path path = new Path(basePathForTest, "part-0");
        final RecoverableFsDataOutputStream stream = writer.open(path);
        stream.write(HadoopS3RecoverableWriterITCase.bytesOf(HadoopS3RecoverableWriterITCase.testData1));
        stream.closeForCommit().commit();
        Assert.assertEquals(HadoopS3RecoverableWriterITCase.testData1, getContentsOfFile(path));
    }

    @Test
    public void testCommitAfterPersist() throws Exception {
        final RecoverableWriter writer = getRecoverableWriter();
        final Path path = new Path(basePathForTest, "part-0");
        final RecoverableFsDataOutputStream stream = writer.open(path);
        stream.write(HadoopS3RecoverableWriterITCase.bytesOf(HadoopS3RecoverableWriterITCase.testData1));
        stream.persist();
        stream.write(HadoopS3RecoverableWriterITCase.bytesOf(HadoopS3RecoverableWriterITCase.testData2));
        stream.closeForCommit().commit();
        Assert.assertEquals(((HadoopS3RecoverableWriterITCase.testData1) + (HadoopS3RecoverableWriterITCase.testData2)), getContentsOfFile(path));
    }

    @Test(expected = FileNotFoundException.class)
    public void testCleanupRecoverableState() throws Exception {
        final RecoverableWriter writer = getRecoverableWriter();
        final Path path = new Path(basePathForTest, "part-0");
        final RecoverableFsDataOutputStream stream = writer.open(path);
        stream.write(HadoopS3RecoverableWriterITCase.bytesOf(HadoopS3RecoverableWriterITCase.testData1));
        S3Recoverable recoverable = ((S3Recoverable) (stream.persist()));
        stream.closeForCommit().commit();
        // still the data is there as we have not deleted them from the tmp object
        final String content = getContentsOfFile(new Path(('/' + (recoverable.incompleteObjectName()))));
        Assert.assertEquals(HadoopS3RecoverableWriterITCase.testData1, content);
        boolean successfullyDeletedState = writer.cleanupRecoverableState(recoverable);
        Assert.assertTrue(successfullyDeletedState);
        // this should throw the exception as we deleted the file.
        getContentsOfFile(new Path(('/' + (recoverable.incompleteObjectName()))));
    }

    @Test
    public void testCallingDeleteObjectTwiceDoesNotThroughException() throws Exception {
        final RecoverableWriter writer = getRecoverableWriter();
        final Path path = new Path(basePathForTest, "part-0");
        final RecoverableFsDataOutputStream stream = writer.open(path);
        stream.write(HadoopS3RecoverableWriterITCase.bytesOf(HadoopS3RecoverableWriterITCase.testData1));
        S3Recoverable recoverable = ((S3Recoverable) (stream.persist()));
        stream.closeForCommit().commit();
        // still the data is there as we have not deleted them from the tmp object
        final String content = getContentsOfFile(new Path(('/' + (recoverable.incompleteObjectName()))));
        Assert.assertEquals(HadoopS3RecoverableWriterITCase.testData1, content);
        boolean successfullyDeletedState = writer.cleanupRecoverableState(recoverable);
        Assert.assertTrue(successfullyDeletedState);
        boolean unsuccessfulDeletion = writer.cleanupRecoverableState(recoverable);
        Assert.assertFalse(unsuccessfulDeletion);
    }

    // ----------------------- Test Recovery -----------------------
    @Test
    public void testCommitAfterRecovery() throws Exception {
        final Path path = new Path(basePathForTest, "part-0");
        final RecoverableWriter initWriter = getRecoverableWriter();
        final RecoverableFsDataOutputStream stream = initWriter.open(path);
        stream.write(HadoopS3RecoverableWriterITCase.bytesOf(HadoopS3RecoverableWriterITCase.testData1));
        stream.persist();
        stream.persist();
        // and write some more data
        stream.write(HadoopS3RecoverableWriterITCase.bytesOf(HadoopS3RecoverableWriterITCase.testData2));
        final RecoverableWriter.CommitRecoverable recoverable = stream.closeForCommit().getRecoverable();
        final byte[] serializedRecoverable = initWriter.getCommitRecoverableSerializer().serialize(recoverable);
        // get a new serializer from a new writer to make sure that no pre-initialized state leaks in.
        final RecoverableWriter newWriter = getRecoverableWriter();
        final SimpleVersionedSerializer<RecoverableWriter.CommitRecoverable> deserializer = newWriter.getCommitRecoverableSerializer();
        final RecoverableWriter.CommitRecoverable recoveredRecoverable = deserializer.deserialize(deserializer.getVersion(), serializedRecoverable);
        final RecoverableFsDataOutputStream.Committer committer = newWriter.recoverForCommit(recoveredRecoverable);
        committer.commitAfterRecovery();
        Assert.assertEquals(((HadoopS3RecoverableWriterITCase.testData1) + (HadoopS3RecoverableWriterITCase.testData2)), getContentsOfFile(path));
    }

    private static final String INIT_EMPTY_PERSIST = "EMPTY";

    private static final String INTERM_WITH_STATE_PERSIST = "INTERM-STATE";

    private static final String INTERM_WITH_NO_ADDITIONAL_STATE_PERSIST = "INTERM-IMEDIATE";

    private static final String FINAL_WITH_EXTRA_STATE = "FINAL";

    @Test
    public void testRecoverWithEmptyState() throws Exception {
        testResumeAfterMultiplePersistWithSmallData(HadoopS3RecoverableWriterITCase.INIT_EMPTY_PERSIST, HadoopS3RecoverableWriterITCase.testData3);
    }

    @Test
    public void testRecoverWithState() throws Exception {
        testResumeAfterMultiplePersistWithSmallData(HadoopS3RecoverableWriterITCase.INTERM_WITH_STATE_PERSIST, ((HadoopS3RecoverableWriterITCase.testData1) + (HadoopS3RecoverableWriterITCase.testData3)));
    }

    @Test
    public void testRecoverFromIntermWithoutAdditionalState() throws Exception {
        testResumeAfterMultiplePersistWithSmallData(HadoopS3RecoverableWriterITCase.INTERM_WITH_NO_ADDITIONAL_STATE_PERSIST, ((HadoopS3RecoverableWriterITCase.testData1) + (HadoopS3RecoverableWriterITCase.testData3)));
    }

    @Test
    public void testRecoverAfterMultiplePersistsState() throws Exception {
        testResumeAfterMultiplePersistWithSmallData(HadoopS3RecoverableWriterITCase.FINAL_WITH_EXTRA_STATE, (((HadoopS3RecoverableWriterITCase.testData1) + (HadoopS3RecoverableWriterITCase.testData2)) + (HadoopS3RecoverableWriterITCase.testData3)));
    }

    @Test
    public void testRecoverWithStateWithMultiPart() throws Exception {
        testResumeAfterMultiplePersistWithMultiPartUploads(HadoopS3RecoverableWriterITCase.INTERM_WITH_STATE_PERSIST, ((HadoopS3RecoverableWriterITCase.bigDataChunk) + (HadoopS3RecoverableWriterITCase.bigDataChunk)));
    }

    @Test
    public void testRecoverFromIntermWithoutAdditionalStateWithMultiPart() throws Exception {
        testResumeAfterMultiplePersistWithMultiPartUploads(HadoopS3RecoverableWriterITCase.INTERM_WITH_NO_ADDITIONAL_STATE_PERSIST, ((HadoopS3RecoverableWriterITCase.bigDataChunk) + (HadoopS3RecoverableWriterITCase.bigDataChunk)));
    }

    @Test
    public void testRecoverAfterMultiplePersistsStateWithMultiPart() throws Exception {
        testResumeAfterMultiplePersistWithMultiPartUploads(HadoopS3RecoverableWriterITCase.FINAL_WITH_EXTRA_STATE, (((HadoopS3RecoverableWriterITCase.bigDataChunk) + (HadoopS3RecoverableWriterITCase.bigDataChunk)) + (HadoopS3RecoverableWriterITCase.bigDataChunk)));
    }
}

