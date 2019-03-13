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


import com.amazonaws.services.s3.model.CompleteMultipartUploadResult;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PartETag;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.UploadPartResult;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.flink.fs.s3.common.utils.RefCountedBufferingFileStream;
import org.apache.flink.util.MathUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 * Tests for the {@link RecoverableMultiPartUploadImpl}.
 */
public class RecoverableMultiPartUploadImplTest {
    private static final int BUFFER_SIZE = 10;

    private static final String TEST_OBJECT_NAME = "TEST-OBJECT";

    private RecoverableMultiPartUploadImplTest.StubMultiPartUploader stubMultiPartUploader;

    private RecoverableMultiPartUploadImpl multiPartUploadUnderTest;

    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void singlePartUploadShouldBeIncluded() throws IOException {
        final byte[] part = RecoverableMultiPartUploadImplTest.bytesOf("hello world");
        uploadPart(part);
        MatcherAssert.assertThat(stubMultiPartUploader, RecoverableMultiPartUploadImplTest.hasMultiPartUploadWithPart(1, part));
    }

    @Test
    public void incompletePartShouldBeUploadedAsIndividualObject() throws IOException {
        final byte[] incompletePart = RecoverableMultiPartUploadImplTest.bytesOf("Hi!");
        uploadObject(incompletePart);
        MatcherAssert.assertThat(stubMultiPartUploader, RecoverableMultiPartUploadImplTest.hasUploadedObject(incompletePart));
    }

    @Test
    public void multiplePartAndObjectUploadsShouldBeIncluded() throws IOException {
        final byte[] firstCompletePart = RecoverableMultiPartUploadImplTest.bytesOf("hello world");
        final byte[] secondCompletePart = RecoverableMultiPartUploadImplTest.bytesOf("hello again");
        final byte[] thirdIncompletePart = RecoverableMultiPartUploadImplTest.bytesOf("!!!");
        uploadPart(firstCompletePart);
        uploadPart(secondCompletePart);
        uploadObject(thirdIncompletePart);
        MatcherAssert.assertThat(stubMultiPartUploader, Matchers.allOf(RecoverableMultiPartUploadImplTest.hasMultiPartUploadWithPart(1, firstCompletePart), RecoverableMultiPartUploadImplTest.hasMultiPartUploadWithPart(2, secondCompletePart), RecoverableMultiPartUploadImplTest.hasUploadedObject(thirdIncompletePart)));
    }

    @Test
    public void multiplePartAndObjectUploadsShouldBeReflectedInRecoverable() throws IOException {
        final byte[] firstCompletePart = RecoverableMultiPartUploadImplTest.bytesOf("hello world");
        final byte[] secondCompletePart = RecoverableMultiPartUploadImplTest.bytesOf("hello again");
        final byte[] thirdIncompletePart = RecoverableMultiPartUploadImplTest.bytesOf("!!!");
        uploadPart(firstCompletePart);
        uploadPart(secondCompletePart);
        final S3Recoverable recoverable = uploadObject(thirdIncompletePart);
        MatcherAssert.assertThat(recoverable, RecoverableMultiPartUploadImplTest.isEqualTo(thirdIncompletePart, firstCompletePart, secondCompletePart));
    }

    @Test
    public void s3RecoverableReflectsTheLatestPartialObject() throws IOException {
        final byte[] incompletePartOne = RecoverableMultiPartUploadImplTest.bytesOf("AB");
        final byte[] incompletePartTwo = RecoverableMultiPartUploadImplTest.bytesOf("ABC");
        S3Recoverable recoverableOne = uploadObject(incompletePartOne);
        S3Recoverable recoverableTwo = uploadObject(incompletePartTwo);
        MatcherAssert.assertThat(recoverableTwo.incompleteObjectName(), Matchers.not(Matchers.equalTo(recoverableOne.incompleteObjectName())));
    }

    @Test(expected = IllegalStateException.class)
    public void uploadingNonClosedFileAsCompleteShouldThroughException() throws IOException {
        final byte[] incompletePart = RecoverableMultiPartUploadImplTest.bytesOf("!!!");
        final RefCountedBufferingFileStream incompletePartFile = writeContent(incompletePart);
        multiPartUploadUnderTest.uploadPart(incompletePartFile);
    }

    // ---------------------------------- Test Classes -------------------------------------------
    /**
     * A simple executor that executes the runnable on the main thread.
     */
    private static class MainThreadExecutor implements Executor {
        @Override
        public void execute(Runnable command) {
            command.run();
        }
    }

    /**
     * A {@link S3AccessHelper} that simulates uploading part files to S3 by
     * simply putting complete and incomplete part files in lists for further validation.
     */
    private static class StubMultiPartUploader implements S3AccessHelper {
        private final List<RecoverableMultiPartUploadImplTest.TestUploadPartResult> completePartsUploaded = new ArrayList<>();

        private final List<RecoverableMultiPartUploadImplTest.TestPutObjectResult> incompletePartsUploaded = new ArrayList<>();

        List<RecoverableMultiPartUploadImplTest.TestUploadPartResult> getCompletePartsUploaded() {
            return completePartsUploaded;
        }

        List<RecoverableMultiPartUploadImplTest.TestPutObjectResult> getIncompletePartsUploaded() {
            return incompletePartsUploaded;
        }

        @Override
        public String startMultiPartUpload(String key) throws IOException {
            return RecoverableMultiPartUploadImplTest.createMPUploadId(key);
        }

        @Override
        public UploadPartResult uploadPart(String key, String uploadId, int partNumber, File inputFile, long length) throws IOException {
            final byte[] content = getFileContentBytes(inputFile, MathUtils.checkedDownCast(length));
            return storeAndGetUploadPartResult(key, partNumber, content);
        }

        @Override
        public PutObjectResult putObject(String key, File inputFile) throws IOException {
            final byte[] content = getFileContentBytes(inputFile, MathUtils.checkedDownCast(inputFile.length()));
            return storeAndGetPutObjectResult(key, content);
        }

        @Override
        public boolean deleteObject(String key) throws IOException {
            return false;
        }

        @Override
        public long getObject(String key, File targetLocation) throws IOException {
            return 0;
        }

        @Override
        public CompleteMultipartUploadResult commitMultiPartUpload(String key, String uploadId, List<PartETag> partETags, long length, AtomicInteger errorCount) throws IOException {
            return null;
        }

        @Override
        public ObjectMetadata getObjectMetadata(String key) throws IOException {
            return null;
        }

        private byte[] getFileContentBytes(File file, int length) throws IOException {
            final byte[] content = new byte[length];
            new FileInputStream(file).read(content, 0, length);
            return content;
        }

        private RecoverableMultiPartUploadImplTest.TestUploadPartResult storeAndGetUploadPartResult(String key, int number, byte[] payload) {
            final RecoverableMultiPartUploadImplTest.TestUploadPartResult result = RecoverableMultiPartUploadImplTest.createUploadPartResult(key, number, payload);
            completePartsUploaded.add(result);
            return result;
        }

        private RecoverableMultiPartUploadImplTest.TestPutObjectResult storeAndGetPutObjectResult(String key, byte[] payload) {
            final RecoverableMultiPartUploadImplTest.TestPutObjectResult result = RecoverableMultiPartUploadImplTest.createPutObjectResult(key, payload);
            incompletePartsUploaded.add(result);
            return result;
        }
    }

    /**
     * A {@link PutObjectResult} that also contains the actual content of the uploaded part.
     */
    private static class TestPutObjectResult extends PutObjectResult {
        private static final long serialVersionUID = 1L;

        private byte[] content;

        void setContent(byte[] payload) {
            this.content = payload;
        }

        public byte[] getContent() {
            return content;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            final RecoverableMultiPartUploadImplTest.TestPutObjectResult that = ((RecoverableMultiPartUploadImplTest.TestPutObjectResult) (o));
            // we ignore the etag as it contains randomness
            return Arrays.equals(getContent(), that.getContent());
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(getContent());
        }

        @Override
        public String toString() {
            return (((('{' + " eTag=") + (getETag())) + ", payload=") + (Arrays.toString(content))) + '}';
        }
    }

    /**
     * A {@link UploadPartResult} that also contains the actual content of the uploaded part.
     */
    private static class TestUploadPartResult extends UploadPartResult {
        private static final long serialVersionUID = 1L;

        private byte[] content;

        void setContent(byte[] content) {
            this.content = content;
        }

        public byte[] getContent() {
            return content;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            final RecoverableMultiPartUploadImplTest.TestUploadPartResult that = ((RecoverableMultiPartUploadImplTest.TestUploadPartResult) (o));
            return ((getETag().equals(getETag())) && ((getPartNumber()) == (getPartNumber()))) && (Arrays.equals(content, that.content));
        }

        @Override
        public int hashCode() {
            return (31 * (Objects.hash(getETag(), getPartNumber()))) + (Arrays.hashCode(getContent()));
        }

        @Override
        public String toString() {
            return (((((('{' + "etag=") + (getETag())) + ", partNo=") + (getPartNumber())) + ", content=") + (Arrays.toString(content))) + '}';
        }
    }
}

