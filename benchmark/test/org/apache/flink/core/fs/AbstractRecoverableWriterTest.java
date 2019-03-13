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
package org.apache.flink.core.fs;


import RecoverableFsDataOutputStream.Committer;
import RecoverableWriter.CommitRecoverable;
import RecoverableWriter.ResumeRecoverable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Random;
import org.apache.flink.core.io.SimpleVersionedSerializer;
import org.apache.flink.util.TestLogger;
import org.junit.Assert;
import org.junit.Test;


/**
 * A base test-suite for the {@link RecoverableWriter}.
 * This should be subclassed to test each filesystem specific writer.
 */
public abstract class AbstractRecoverableWriterTest extends TestLogger {
    private static final Random RND = new Random();

    private static final String testData1 = "THIS IS A TEST 1.";

    private static final String testData2 = "THIS IS A TEST 2.";

    private static final String testData3 = "THIS IS A TEST 3.";

    private Path basePathForTest;

    private static FileSystem fileSystem;

    @Test
    public void testCloseWithNoData() throws Exception {
        final RecoverableWriter writer = getNewFileSystemWriter();
        final Path testDir = getBasePathForTest();
        final Path path = new Path(testDir, "part-0");
        final RecoverableFsDataOutputStream stream = writer.open(path);
        for (Map.Entry<Path, String> fileContents : getFileContentByPath(testDir).entrySet()) {
            Assert.assertTrue(fileContents.getKey().getName().startsWith(".part-0.inprogress."));
            Assert.assertTrue(fileContents.getValue().isEmpty());
        }
        stream.closeForCommit().commit();
        for (Map.Entry<Path, String> fileContents : getFileContentByPath(testDir).entrySet()) {
            Assert.assertEquals("part-0", fileContents.getKey().getName());
            Assert.assertTrue(fileContents.getValue().isEmpty());
        }
    }

    @Test
    public void testCommitAfterNormalClose() throws Exception {
        final RecoverableWriter writer = getNewFileSystemWriter();
        final Path testDir = getBasePathForTest();
        final Path path = new Path(testDir, "part-0");
        try (final RecoverableFsDataOutputStream stream = writer.open(path)) {
            stream.write(AbstractRecoverableWriterTest.testData1.getBytes(StandardCharsets.UTF_8));
            stream.closeForCommit().commit();
            for (Map.Entry<Path, String> fileContents : getFileContentByPath(testDir).entrySet()) {
                Assert.assertEquals("part-0", fileContents.getKey().getName());
                Assert.assertEquals(AbstractRecoverableWriterTest.testData1, fileContents.getValue());
            }
        }
    }

    @Test
    public void testCommitAfterPersist() throws Exception {
        final RecoverableWriter writer = getNewFileSystemWriter();
        final Path testDir = getBasePathForTest();
        final Path path = new Path(testDir, "part-0");
        try (final RecoverableFsDataOutputStream stream = writer.open(path)) {
            stream.write(AbstractRecoverableWriterTest.testData1.getBytes(StandardCharsets.UTF_8));
            stream.persist();
            stream.write(AbstractRecoverableWriterTest.testData2.getBytes(StandardCharsets.UTF_8));
            stream.closeForCommit().commit();
            for (Map.Entry<Path, String> fileContents : getFileContentByPath(testDir).entrySet()) {
                Assert.assertEquals("part-0", fileContents.getKey().getName());
                Assert.assertEquals(((AbstractRecoverableWriterTest.testData1) + (AbstractRecoverableWriterTest.testData2)), fileContents.getValue());
            }
        }
    }

    // TESTS FOR RECOVERY
    private static final String INIT_EMPTY_PERSIST = "EMPTY";

    private static final String INTERM_WITH_STATE_PERSIST = "INTERM-STATE";

    private static final String INTERM_WITH_NO_ADDITIONAL_STATE_PERSIST = "INTERM-IMEDIATE";

    private static final String FINAL_WITH_EXTRA_STATE = "FINAL";

    @Test
    public void testRecoverWithEmptyState() throws Exception {
        testResumeAfterMultiplePersist(AbstractRecoverableWriterTest.INIT_EMPTY_PERSIST, "", AbstractRecoverableWriterTest.testData3);
    }

    @Test
    public void testRecoverWithState() throws Exception {
        testResumeAfterMultiplePersist(AbstractRecoverableWriterTest.INTERM_WITH_STATE_PERSIST, AbstractRecoverableWriterTest.testData1, ((AbstractRecoverableWriterTest.testData1) + (AbstractRecoverableWriterTest.testData3)));
    }

    @Test
    public void testRecoverFromIntermWithoutAdditionalState() throws Exception {
        testResumeAfterMultiplePersist(AbstractRecoverableWriterTest.INTERM_WITH_NO_ADDITIONAL_STATE_PERSIST, AbstractRecoverableWriterTest.testData1, ((AbstractRecoverableWriterTest.testData1) + (AbstractRecoverableWriterTest.testData3)));
    }

    @Test
    public void testRecoverAfterMultiplePersistsState() throws Exception {
        testResumeAfterMultiplePersist(AbstractRecoverableWriterTest.FINAL_WITH_EXTRA_STATE, ((AbstractRecoverableWriterTest.testData1) + (AbstractRecoverableWriterTest.testData2)), (((AbstractRecoverableWriterTest.testData1) + (AbstractRecoverableWriterTest.testData2)) + (AbstractRecoverableWriterTest.testData3)));
    }

    @Test
    public void testCommitAfterRecovery() throws Exception {
        final Path testDir = getBasePathForTest();
        final Path path = new Path(testDir, "part-0");
        final RecoverableWriter initWriter = getNewFileSystemWriter();
        final RecoverableWriter.CommitRecoverable recoverable;
        try (final RecoverableFsDataOutputStream stream = initWriter.open(path)) {
            stream.write(AbstractRecoverableWriterTest.testData1.getBytes(StandardCharsets.UTF_8));
            stream.persist();
            stream.persist();
            // and write some more data
            stream.write(AbstractRecoverableWriterTest.testData2.getBytes(StandardCharsets.UTF_8));
            recoverable = stream.closeForCommit().getRecoverable();
        }
        final byte[] serializedRecoverable = initWriter.getCommitRecoverableSerializer().serialize(recoverable);
        // get a new serializer from a new writer to make sure that no pre-initialized state leaks in.
        final RecoverableWriter newWriter = getNewFileSystemWriter();
        final SimpleVersionedSerializer<RecoverableWriter.CommitRecoverable> deserializer = newWriter.getCommitRecoverableSerializer();
        final RecoverableWriter.CommitRecoverable recoveredRecoverable = deserializer.deserialize(deserializer.getVersion(), serializedRecoverable);
        final RecoverableFsDataOutputStream.Committer committer = newWriter.recoverForCommit(recoveredRecoverable);
        committer.commitAfterRecovery();
        Map<Path, String> files = getFileContentByPath(testDir);
        Assert.assertEquals(1L, files.size());
        for (Map.Entry<Path, String> fileContents : files.entrySet()) {
            Assert.assertEquals("part-0", fileContents.getKey().getName());
            Assert.assertEquals(((AbstractRecoverableWriterTest.testData1) + (AbstractRecoverableWriterTest.testData2)), fileContents.getValue());
        }
    }

    // TESTS FOR EXCEPTIONS
    @Test(expected = IOException.class)
    public void testExceptionWritingAfterCloseForCommit() throws Exception {
        final Path testDir = getBasePathForTest();
        final RecoverableWriter writer = getNewFileSystemWriter();
        final Path path = new Path(testDir, "part-0");
        try (final RecoverableFsDataOutputStream stream = writer.open(path)) {
            stream.write(AbstractRecoverableWriterTest.testData1.getBytes(StandardCharsets.UTF_8));
            stream.closeForCommit().getRecoverable();
            stream.write(AbstractRecoverableWriterTest.testData2.getBytes(StandardCharsets.UTF_8));
            Assert.fail();
        }
    }

    @Test(expected = IOException.class)
    public void testResumeAfterCommit() throws Exception {
        final Path testDir = getBasePathForTest();
        final RecoverableWriter writer = getNewFileSystemWriter();
        final Path path = new Path(testDir, "part-0");
        RecoverableWriter.ResumeRecoverable recoverable;
        try (final RecoverableFsDataOutputStream stream = writer.open(path)) {
            stream.write(AbstractRecoverableWriterTest.testData1.getBytes(StandardCharsets.UTF_8));
            recoverable = stream.persist();
            stream.write(AbstractRecoverableWriterTest.testData2.getBytes(StandardCharsets.UTF_8));
            stream.closeForCommit().commit();
        }
        // this should throw an exception as the file is already committed
        writer.recover(recoverable);
        Assert.fail();
    }

    @Test
    public void testResumeWithWrongOffset() throws Exception {
        // this is a rather unrealistic scenario, but it is to trigger
        // truncation of the file and try to resume with missing data.
        final Path testDir = getBasePathForTest();
        final RecoverableWriter writer = getNewFileSystemWriter();
        final Path path = new Path(testDir, "part-0");
        final RecoverableWriter.ResumeRecoverable recoverable1;
        final RecoverableWriter.ResumeRecoverable recoverable2;
        try (final RecoverableFsDataOutputStream stream = writer.open(path)) {
            stream.write(AbstractRecoverableWriterTest.testData1.getBytes(StandardCharsets.UTF_8));
            recoverable1 = stream.persist();
            stream.write(AbstractRecoverableWriterTest.testData2.getBytes(StandardCharsets.UTF_8));
            recoverable2 = stream.persist();
            stream.write(AbstractRecoverableWriterTest.testData3.getBytes(StandardCharsets.UTF_8));
        }
        try (RecoverableFsDataOutputStream ignored = writer.recover(recoverable1)) {
            // this should work fine
        } catch (Exception e) {
            Assert.fail();
        }
        // this should throw an exception
        try (RecoverableFsDataOutputStream ignored = writer.recover(recoverable2)) {
            Assert.fail();
        } catch (IOException e) {
            // we expect this
            return;
        }
        Assert.fail();
    }
}

