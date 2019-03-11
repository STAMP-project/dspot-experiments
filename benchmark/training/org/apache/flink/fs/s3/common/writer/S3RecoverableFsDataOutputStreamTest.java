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
package org.apache.flink.fs.s3.common.writer;


import RecoverableFsDataOutputStream.Committer;
import RecoverableWriter.CommitRecoverable;
import RecoverableWriter.ResumeRecoverable;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import org.apache.flink.core.fs.RecoverableFsDataOutputStream;
import org.apache.flink.fs.s3.common.utils.RefCountedBufferingFileStream;
import org.apache.flink.fs.s3.common.utils.RefCountedFSOutputStream;
import org.apache.flink.fs.s3.common.utils.RefCountedFile;
import org.apache.flink.util.Preconditions;
import org.apache.flink.util.function.FunctionWithException;
import org.hamcrest.MatcherAssert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 * Tests for the {@link S3RecoverableFsDataOutputStream}.
 */
public class S3RecoverableFsDataOutputStreamTest {
    private static final long USER_DEFINED_MIN_PART_SIZE = 10L;

    private S3RecoverableFsDataOutputStreamTest.TestMultipartUpload multipartUploadUnderTest;

    private S3RecoverableFsDataOutputStreamTest.TestFileProvider fileProvider;

    private S3RecoverableFsDataOutputStream streamUnderTest;

    @ClassRule
    public static final TemporaryFolder TEMP_FOLDER = new TemporaryFolder();

    @Test
    public void simpleUsage() throws IOException {
        streamUnderTest.write(S3RecoverableFsDataOutputStreamTest.bytesOf("hello world"));
        RecoverableFsDataOutputStream.Committer committer = streamUnderTest.closeForCommit();
        committer.commit();
        MatcherAssert.assertThat(multipartUploadUnderTest, S3RecoverableFsDataOutputStreamTest.hasContent(S3RecoverableFsDataOutputStreamTest.bytesOf("hello world")));
    }

    @Test
    public void noWritesShouldResolveInAnEmptyFile() throws IOException {
        RecoverableFsDataOutputStream.Committer committer = streamUnderTest.closeForCommit();
        committer.commit();
        MatcherAssert.assertThat(multipartUploadUnderTest, S3RecoverableFsDataOutputStreamTest.hasContent(new byte[0]));
    }

    @Test
    public void closingWithoutCommittingDiscardsTheData() throws IOException {
        streamUnderTest.write(S3RecoverableFsDataOutputStreamTest.bytesOf("hello world"));
        streamUnderTest.close();
        MatcherAssert.assertThat(multipartUploadUnderTest, S3RecoverableFsDataOutputStreamTest.hasContent(S3RecoverableFsDataOutputStreamTest.bytesOf("")));
    }

    @Test
    public void twoWritesAreConcatenated() throws IOException {
        streamUnderTest.write(S3RecoverableFsDataOutputStreamTest.bytesOf("hello"));
        streamUnderTest.write(S3RecoverableFsDataOutputStreamTest.bytesOf(" "));
        streamUnderTest.write(S3RecoverableFsDataOutputStreamTest.bytesOf("world"));
        streamUnderTest.closeForCommit().commit();
        MatcherAssert.assertThat(multipartUploadUnderTest, S3RecoverableFsDataOutputStreamTest.hasContent(S3RecoverableFsDataOutputStreamTest.bytesOf("hello world")));
    }

    @Test
    public void writeLargeFile() throws IOException {
        List<byte[]> testDataBuffers = S3RecoverableFsDataOutputStreamTest.createRandomLargeTestDataBuffers();
        for (byte[] buffer : testDataBuffers) {
            streamUnderTest.write(buffer);
        }
        streamUnderTest.closeForCommit().commit();
        MatcherAssert.assertThat(multipartUploadUnderTest, S3RecoverableFsDataOutputStreamTest.hasContent(testDataBuffers));
    }

    @Test
    public void simpleRecovery() throws IOException {
        streamUnderTest.write(S3RecoverableFsDataOutputStreamTest.bytesOf("hello"));
        streamUnderTest.persist();
        streamUnderTest = reopenStreamUnderTestAfterRecovery();
        streamUnderTest.closeForCommit().commit();
        MatcherAssert.assertThat(multipartUploadUnderTest, S3RecoverableFsDataOutputStreamTest.hasContent(S3RecoverableFsDataOutputStreamTest.bytesOf("hello")));
    }

    @Test
    public void multiplePersistsDoesNotIntroduceJunk() throws IOException {
        streamUnderTest.write(S3RecoverableFsDataOutputStreamTest.bytesOf("hello"));
        streamUnderTest.persist();
        streamUnderTest.persist();
        streamUnderTest.persist();
        streamUnderTest.persist();
        streamUnderTest.write(S3RecoverableFsDataOutputStreamTest.bytesOf(" "));
        streamUnderTest.write(S3RecoverableFsDataOutputStreamTest.bytesOf("world"));
        streamUnderTest.closeForCommit().commit();
        MatcherAssert.assertThat(multipartUploadUnderTest, S3RecoverableFsDataOutputStreamTest.hasContent(S3RecoverableFsDataOutputStreamTest.bytesOf("hello world")));
    }

    @Test
    public void multipleWritesAndPersists() throws IOException {
        streamUnderTest.write(S3RecoverableFsDataOutputStreamTest.bytesOf("a"));
        streamUnderTest.persist();
        streamUnderTest.write(S3RecoverableFsDataOutputStreamTest.bytesOf("b"));
        streamUnderTest.persist();
        streamUnderTest.write(S3RecoverableFsDataOutputStreamTest.bytesOf("c"));
        streamUnderTest.persist();
        streamUnderTest.write(S3RecoverableFsDataOutputStreamTest.bytesOf("d"));
        streamUnderTest.persist();
        streamUnderTest.write(S3RecoverableFsDataOutputStreamTest.bytesOf("e"));
        streamUnderTest.closeForCommit().commit();
        MatcherAssert.assertThat(multipartUploadUnderTest, S3RecoverableFsDataOutputStreamTest.hasContent(S3RecoverableFsDataOutputStreamTest.bytesOf("abcde")));
    }

    @Test
    public void multipleWritesAndPersistsWithBigChunks() throws IOException {
        List<byte[]> testDataBuffers = S3RecoverableFsDataOutputStreamTest.createRandomLargeTestDataBuffers();
        for (byte[] buffer : testDataBuffers) {
            streamUnderTest.write(buffer);
            streamUnderTest.persist();
        }
        streamUnderTest.closeForCommit().commit();
        MatcherAssert.assertThat(multipartUploadUnderTest, S3RecoverableFsDataOutputStreamTest.hasContent(testDataBuffers));
    }

    @Test
    public void addDataAfterRecovery() throws IOException {
        streamUnderTest.write(S3RecoverableFsDataOutputStreamTest.bytesOf("hello"));
        streamUnderTest.persist();
        streamUnderTest = reopenStreamUnderTestAfterRecovery();
        streamUnderTest.write(S3RecoverableFsDataOutputStreamTest.bytesOf(" "));
        streamUnderTest.write(S3RecoverableFsDataOutputStreamTest.bytesOf("world"));
        streamUnderTest.closeForCommit().commit();
        MatcherAssert.assertThat(multipartUploadUnderTest, S3RecoverableFsDataOutputStreamTest.hasContent(S3RecoverableFsDataOutputStreamTest.bytesOf("hello world")));
    }

    @Test
    public void discardingUnpersistedNotYetUploadedData() throws IOException {
        streamUnderTest.write(S3RecoverableFsDataOutputStreamTest.bytesOf("hello"));
        streamUnderTest.persist();
        streamUnderTest.write(S3RecoverableFsDataOutputStreamTest.bytesOf("goodbye"));
        streamUnderTest = reopenStreamUnderTestAfterRecovery();
        streamUnderTest.write(S3RecoverableFsDataOutputStreamTest.bytesOf(" world"));
        streamUnderTest.closeForCommit().commit();
        MatcherAssert.assertThat(multipartUploadUnderTest, S3RecoverableFsDataOutputStreamTest.hasContent(S3RecoverableFsDataOutputStreamTest.bytesOf("hello world")));
    }

    @Test
    public void discardingUnpersistedUploadedData() throws IOException {
        streamUnderTest.write(S3RecoverableFsDataOutputStreamTest.bytesOf("hello"));
        streamUnderTest.persist();
        streamUnderTest.write(S3RecoverableFsDataOutputStreamTest.randomBuffer(((RefCountedBufferingFileStream.BUFFER_SIZE) + 1)));
        streamUnderTest = reopenStreamUnderTestAfterRecovery();
        streamUnderTest.write(S3RecoverableFsDataOutputStreamTest.bytesOf(" world"));
        streamUnderTest.closeForCommit().commit();
        MatcherAssert.assertThat(multipartUploadUnderTest, S3RecoverableFsDataOutputStreamTest.hasContent(S3RecoverableFsDataOutputStreamTest.bytesOf("hello world")));
    }

    @Test
    public void commitEmptyStreamShouldBeSuccessful() throws IOException {
        streamUnderTest.closeForCommit().commit();
    }

    @Test(expected = IOException.class)
    public void closeForCommitOnClosedStreamShouldFail() throws IOException {
        streamUnderTest.closeForCommit().commit();
        streamUnderTest.closeForCommit().commit();
    }

    // ------------------------------------------------------------------------------------------------------------
    // Test Classes
    // ------------------------------------------------------------------------------------------------------------
    private static class TestMultipartUpload implements RecoverableMultiPartUpload {
        private final S3RecoverableFsDataOutputStreamTest.TestFileProvider fileProvider;

        private List<byte[]> uploadedContent = new ArrayList<>();

        private int lastPersistedIndex;

        private int numParts;

        private long numBytes;

        private byte[] published;

        private final ByteArrayOutputStream publishedContents = new ByteArrayOutputStream();

        private Optional<byte[]> uncompleted = Optional.empty();

        TestMultipartUpload(S3RecoverableFsDataOutputStreamTest.TestFileProvider fileProvider) {
            this.published = new byte[0];
            this.lastPersistedIndex = 0;
            this.fileProvider = fileProvider;
        }

        public void discardUnpersistedData() {
            uploadedContent = uploadedContent.subList(0, lastPersistedIndex);
        }

        @Override
        public Optional<File> getIncompletePart() {
            if (!(uncompleted.isPresent())) {
                return Optional.empty();
            }
            byte[] uncompletedBytes = uncompleted.get();
            try {
                File uncompletedTempFile = fileProvider.apply(null).getFile();
                Files.write(uncompletedTempFile.toPath(), uncompletedBytes);
                return Optional.of(uncompletedTempFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public Committer snapshotAndGetCommitter() throws IOException {
            lastPersistedIndex = uploadedContent.size();
            return new RecoverableFsDataOutputStream.Committer() {
                @Override
                public void commit() throws IOException {
                    published = getPublishedContents();
                    uploadedContent.clear();
                    lastPersistedIndex = 0;
                }

                @Override
                public void commitAfterRecovery() throws IOException {
                    if ((published.length) == 0) {
                        commit();
                    }
                }

                @Override
                public CommitRecoverable getRecoverable() {
                    return null;
                }
            };
        }

        @Override
        public ResumeRecoverable snapshotAndGetRecoverable(RefCountedFSOutputStream incompletePartFile) throws IOException {
            lastPersistedIndex = uploadedContent.size();
            if ((incompletePartFile.getPos()) >= 0L) {
                byte[] bytes = S3RecoverableFsDataOutputStreamTest.readFileContents(incompletePartFile);
                uncompleted = Optional.of(bytes);
            }
            return null;
        }

        @Override
        public void uploadPart(RefCountedFSOutputStream file) throws IOException {
            (numParts)++;
            numBytes += file.getPos();
            uploadedContent.add(S3RecoverableFsDataOutputStreamTest.readFileContents(file));
        }

        public byte[] getPublishedContents() {
            for (int i = 0; i < (lastPersistedIndex); i++) {
                try {
                    publishedContents.write(uploadedContent.get(i));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            return publishedContents.toByteArray();
        }

        @Override
        public String toString() {
            return (("TestMultipartUpload{" + "contents=") + (Arrays.toString(published))) + '}';
        }
    }

    private static class TestFileProvider implements FunctionWithException<File, RefCountedFile, IOException> {
        private final TemporaryFolder folder;

        TestFileProvider(TemporaryFolder folder) {
            this.folder = Preconditions.checkNotNull(folder);
        }

        @Override
        public RefCountedFile apply(@Nullable
        File file) throws IOException {
            while (true) {
                try {
                    if (file == null) {
                        final File newFile = new File(folder.getRoot(), (".tmp_" + (UUID.randomUUID())));
                        final OutputStream out = Files.newOutputStream(newFile.toPath(), StandardOpenOption.CREATE_NEW);
                        return RefCountedFile.newFile(newFile, out);
                    } else {
                        final OutputStream out = Files.newOutputStream(file.toPath(), StandardOpenOption.APPEND);
                        return RefCountedFile.restoredFile(file, out, file.length());
                    }
                } catch (FileAlreadyExistsException e) {
                    // fall through the loop and retry
                }
            } 
        }
    }
}

