/**
 * Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.hadoop.fs.contract;


import com.google.common.base.Charsets;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.hadoop.fs.BBUploadHandle;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.MultipartUploader;
import org.apache.hadoop.fs.MultipartUploaderFactory;
import org.apache.hadoop.fs.PartHandle;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathHandle;
import org.apache.hadoop.fs.UploadHandle;
import org.apache.hadoop.test.LambdaTestUtils;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class AbstractContractMultipartUploaderTest extends AbstractFSContractTestBase {
    protected static final Logger LOG = LoggerFactory.getLogger(AbstractContractMultipartUploaderTest.class);

    /**
     * Size of very small uploads.
     * Enough to be non empty, not big enough to cause delays on uploads.
     */
    protected static final int SMALL_FILE = 100;

    private MultipartUploader mpu;

    private MultipartUploader mpu2;

    private final Random random = new Random();

    private UploadHandle activeUpload;

    private Path activeUploadPath;

    /**
     * Assert that a multipart upload is successful.
     *
     * @throws Exception
     * 		failure
     */
    @Test
    public void testSingleUpload() throws Exception {
        Path file = methodPath();
        UploadHandle uploadHandle = initializeUpload(file);
        Map<Integer, PartHandle> partHandles = new HashMap<>();
        MessageDigest origDigest = DigestUtils.getMd5Digest();
        int size = AbstractContractMultipartUploaderTest.SMALL_FILE;
        byte[] payload = generatePayload(1, size);
        origDigest.update(payload);
        PartHandle partHandle = putPart(file, uploadHandle, 1, payload);
        partHandles.put(1, partHandle);
        PathHandle fd = completeUpload(file, uploadHandle, partHandles, origDigest, size);
        if (finalizeConsumesUploadIdImmediately()) {
            LambdaTestUtils.intercept(FileNotFoundException.class, () -> mpu.complete(file, partHandles, uploadHandle));
        } else {
            PathHandle fd2 = mpu.complete(file, partHandles, uploadHandle);
            Assert.assertArrayEquals("Path handles differ", fd.toByteArray(), fd2.toByteArray());
        }
    }

    /**
     * Assert that a multipart upload is successful.
     *
     * @throws Exception
     * 		failure
     */
    @Test
    public void testMultipartUpload() throws Exception {
        Path file = methodPath();
        UploadHandle uploadHandle = initializeUpload(file);
        Map<Integer, PartHandle> partHandles = new HashMap<>();
        MessageDigest origDigest = DigestUtils.getMd5Digest();
        final int payloadCount = getTestPayloadCount();
        for (int i = 1; i <= payloadCount; ++i) {
            PartHandle partHandle = buildAndPutPart(file, uploadHandle, i, origDigest);
            partHandles.put(i, partHandle);
        }
        completeUpload(file, uploadHandle, partHandles, origDigest, (payloadCount * (partSizeInBytes())));
    }

    /**
     * Assert that a multipart upload is successful when a single empty part is
     * uploaded.
     *
     * @throws Exception
     * 		failure
     */
    @Test
    public void testMultipartUploadEmptyPart() throws Exception {
        FileSystem fs = getFileSystem();
        Path file = path("testMultipartUpload");
        try (MultipartUploader uploader = MultipartUploaderFactory.get(fs, null)) {
            UploadHandle uploadHandle = uploader.initialize(file);
            Map<Integer, PartHandle> partHandles = new HashMap<>();
            MessageDigest origDigest = DigestUtils.getMd5Digest();
            byte[] payload = new byte[0];
            origDigest.update(payload);
            InputStream is = new ByteArrayInputStream(payload);
            PartHandle partHandle = uploader.putPart(file, is, 1, uploadHandle, payload.length);
            partHandles.put(1, partHandle);
            completeUpload(file, uploadHandle, partHandles, origDigest, 0);
        }
    }

    /**
     * Assert that a multipart upload is successful.
     *
     * @throws Exception
     * 		failure
     */
    @Test
    public void testUploadEmptyBlock() throws Exception {
        Path file = methodPath();
        UploadHandle uploadHandle = initializeUpload(file);
        Map<Integer, PartHandle> partHandles = new HashMap<>();
        partHandles.put(1, putPart(file, uploadHandle, 1, new byte[0]));
        completeUpload(file, uploadHandle, partHandles, null, 0);
    }

    /**
     * Assert that a multipart upload is successful even when the parts are
     * given in the reverse order.
     */
    @Test
    public void testMultipartUploadReverseOrder() throws Exception {
        Path file = methodPath();
        UploadHandle uploadHandle = initializeUpload(file);
        Map<Integer, PartHandle> partHandles = new HashMap<>();
        MessageDigest origDigest = DigestUtils.getMd5Digest();
        final int payloadCount = getTestPayloadCount();
        for (int i = 1; i <= payloadCount; ++i) {
            byte[] payload = generatePayload(i);
            origDigest.update(payload);
        }
        for (int i = payloadCount; i > 0; --i) {
            partHandles.put(i, buildAndPutPart(file, uploadHandle, i, null));
        }
        completeUpload(file, uploadHandle, partHandles, origDigest, (payloadCount * (partSizeInBytes())));
    }

    /**
     * Assert that a multipart upload is successful even when the parts are
     * given in reverse order and the part numbers are not contiguous.
     */
    @Test
    public void testMultipartUploadReverseOrderNonContiguousPartNumbers() throws Exception {
        describe("Upload in reverse order and the part numbers are not contiguous");
        Path file = methodPath();
        UploadHandle uploadHandle = initializeUpload(file);
        MessageDigest origDigest = DigestUtils.getMd5Digest();
        int payloadCount = 2 * (getTestPayloadCount());
        for (int i = 2; i <= payloadCount; i += 2) {
            byte[] payload = generatePayload(i);
            origDigest.update(payload);
        }
        Map<Integer, PartHandle> partHandles = new HashMap<>();
        for (int i = payloadCount; i > 0; i -= 2) {
            partHandles.put(i, buildAndPutPart(file, uploadHandle, i, null));
        }
        completeUpload(file, uploadHandle, partHandles, origDigest, ((getTestPayloadCount()) * (partSizeInBytes())));
    }

    /**
     * Assert that when we abort a multipart upload, the resulting file does
     * not show up.
     */
    @Test
    public void testMultipartUploadAbort() throws Exception {
        describe("Upload and then abort it before completing");
        Path file = methodPath();
        UploadHandle uploadHandle = initializeUpload(file);
        int end = 10;
        Map<Integer, PartHandle> partHandles = new HashMap<>();
        for (int i = 12; i > 10; i--) {
            partHandles.put(i, buildAndPutPart(file, uploadHandle, i, null));
        }
        abortUpload(file, uploadHandle);
        String contents = "ThisIsPart49\n";
        int len = contents.getBytes(Charsets.UTF_8).length;
        InputStream is = IOUtils.toInputStream(contents, "UTF-8");
        LambdaTestUtils.intercept(IOException.class, () -> mpu.putPart(file, is, 49, uploadHandle, len));
        LambdaTestUtils.intercept(IOException.class, () -> mpu.complete(file, partHandles, uploadHandle));
        assertPathDoesNotExist("Uploaded file should not exist", file);
        // A second abort should be an FileNotFoundException if the UploadHandle is
        // consumed by finalization operations (complete, abort).
        if (finalizeConsumesUploadIdImmediately()) {
            LambdaTestUtils.intercept(FileNotFoundException.class, () -> abortUpload(file, uploadHandle));
        } else {
            abortUpload(file, uploadHandle);
        }
    }

    /**
     * Trying to abort from an invalid handle must fail.
     */
    @Test
    public void testAbortUnknownUpload() throws Exception {
        Path file = methodPath();
        ByteBuffer byteBuffer = ByteBuffer.wrap("invalid-handle".getBytes(Charsets.UTF_8));
        UploadHandle uploadHandle = BBUploadHandle.from(byteBuffer);
        LambdaTestUtils.intercept(FileNotFoundException.class, () -> abortUpload(file, uploadHandle));
    }

    /**
     * Trying to abort with a handle of size 0 must fail.
     */
    @Test
    public void testAbortEmptyUpload() throws Exception {
        describe("initialize upload and abort before uploading data");
        Path file = methodPath();
        abortUpload(file, initializeUpload(file));
        assertPathDoesNotExist("Uploaded file should not exist", file);
    }

    /**
     * Trying to abort with a handle of size 0 must fail.
     */
    @Test
    public void testAbortEmptyUploadHandle() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[0]);
        UploadHandle uploadHandle = BBUploadHandle.from(byteBuffer);
        LambdaTestUtils.intercept(IllegalArgumentException.class, () -> abortUpload(methodPath(), uploadHandle));
    }

    /**
     * When we complete with no parts provided, it must fail.
     */
    @Test
    public void testCompleteEmptyUpload() throws Exception {
        describe("Expect an empty MPU to fail, but still be abortable");
        Path dest = methodPath();
        UploadHandle handle = initializeUpload(dest);
        LambdaTestUtils.intercept(IllegalArgumentException.class, () -> mpu.complete(dest, new HashMap(), handle));
        abortUpload(dest, handle);
    }

    /**
     * When we pass empty uploadID, putPart throws IllegalArgumentException.
     */
    @Test
    public void testPutPartEmptyUploadID() throws Exception {
        describe("Expect IllegalArgumentException when putPart uploadID is empty");
        Path dest = methodPath();
        UploadHandle emptyHandle = BBUploadHandle.from(ByteBuffer.wrap(new byte[0]));
        byte[] payload = generatePayload(1);
        InputStream is = new ByteArrayInputStream(payload);
        LambdaTestUtils.intercept(IllegalArgumentException.class, () -> mpu.putPart(dest, is, 1, emptyHandle, payload.length));
    }

    /**
     * When we pass empty uploadID, complete throws IllegalArgumentException.
     */
    @Test
    public void testCompleteEmptyUploadID() throws Exception {
        describe("Expect IllegalArgumentException when complete uploadID is empty");
        Path dest = methodPath();
        UploadHandle realHandle = initializeUpload(dest);
        UploadHandle emptyHandle = BBUploadHandle.from(ByteBuffer.wrap(new byte[0]));
        Map<Integer, PartHandle> partHandles = new HashMap<>();
        PartHandle partHandle = putPart(dest, realHandle, 1, generatePayload(1, AbstractContractMultipartUploaderTest.SMALL_FILE));
        partHandles.put(1, partHandle);
        LambdaTestUtils.intercept(IllegalArgumentException.class, () -> mpu.complete(dest, partHandles, emptyHandle));
        // and, while things are setup, attempt to complete with
        // a part index of 0
        partHandles.clear();
        partHandles.put(0, partHandle);
        LambdaTestUtils.intercept(IllegalArgumentException.class, () -> mpu.complete(dest, partHandles, realHandle));
    }

    /**
     * Assert that upon completion, a directory in the way of the file will
     * result in a failure. This test only applies to backing stores with a
     * concept of directories.
     *
     * @throws Exception
     * 		failure
     */
    @Test
    public void testDirectoryInTheWay() throws Exception {
        FileSystem fs = getFileSystem();
        Path file = methodPath();
        UploadHandle uploadHandle = initializeUpload(file);
        Map<Integer, PartHandle> partHandles = new HashMap<>();
        int size = AbstractContractMultipartUploaderTest.SMALL_FILE;
        PartHandle partHandle = putPart(file, uploadHandle, 1, generatePayload(1, size));
        partHandles.put(1, partHandle);
        fs.mkdirs(file);
        LambdaTestUtils.intercept(IOException.class, () -> completeUpload(file, uploadHandle, partHandles, null, size));
        // abort should still work
        abortUpload(file, uploadHandle);
    }

    @Test
    public void testConcurrentUploads() throws Throwable {
        // if the FS doesn't support concurrent uploads, this test is
        // required to fail during the second initialization.
        final boolean concurrent = supportsConcurrentUploadsToSamePath();
        describe(("testing concurrent uploads, MPU support for this is " + concurrent));
        final FileSystem fs = getFileSystem();
        final Path file = methodPath();
        final int size1 = AbstractContractMultipartUploaderTest.SMALL_FILE;
        final int partId1 = 1;
        final byte[] payload1 = generatePayload(partId1, size1);
        final MessageDigest digest1 = DigestUtils.getMd5Digest();
        digest1.update(payload1);
        final UploadHandle upload1 = initializeUpload(file);
        final Map<Integer, PartHandle> partHandles1 = new HashMap<>();
        // initiate part 2
        // by using a different size, it's straightforward to see which
        // version is visible, before reading/digesting the contents
        final int size2 = size1 * 2;
        final int partId2 = 2;
        final byte[] payload2 = generatePayload(partId1, size2);
        final MessageDigest digest2 = DigestUtils.getMd5Digest();
        digest2.update(payload2);
        final UploadHandle upload2;
        try {
            upload2 = initializeUpload(file);
            Assume.assumeTrue("The Filesystem is unexpectedly supporting concurrent uploads", concurrent);
        } catch (IOException e) {
            if (!concurrent) {
                // this is expected, so end the test
                AbstractContractMultipartUploaderTest.LOG.debug("Expected exception raised on concurrent uploads {}", e);
                return;
            } else {
                throw e;
            }
        }
        final Map<Integer, PartHandle> partHandles2 = new HashMap<>();
        Assert.assertNotEquals("Upload handles match", upload1, upload2);
        // put part 1
        partHandles1.put(partId1, putPart(file, upload1, partId1, payload1));
        // put part2
        partHandles2.put(partId2, putPart(file, upload2, partId2, payload2));
        // complete part u1. expect its size and digest to
        // be as expected.
        completeUpload(file, upload1, partHandles1, digest1, size1);
        // now upload part 2.
        complete(file, upload2, partHandles2);
        // and await the visible length to match
        LambdaTestUtils.eventually(timeToBecomeConsistentMillis(), 500, () -> {
            FileStatus status = fs.getFileStatus(file);
            Assert.assertEquals(("File length in " + status), size2, status.getLen());
        });
        verifyContents(file, digest2, size2);
    }
}

