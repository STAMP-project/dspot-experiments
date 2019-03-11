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
package org.apache.hadoop.fs.s3a;


import S3AEncryptionMethods.NONE;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileAlreadyExistsException;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.contract.ContractTestUtils;
import org.apache.hadoop.fs.store.EtagChecksum;
import org.apache.hadoop.test.LambdaTestUtils;
import org.junit.Assume;
import org.junit.Test;


/**
 * Tests of the S3A FileSystem which don't have a specific home and can share
 * a filesystem instance with others.
 * Checksums are turned on unless explicitly disabled for a test case.
 */
public class ITestS3AMiscOperations extends AbstractS3ATestBase {
    private static final byte[] HELLO = "hello".getBytes(StandardCharsets.UTF_8);

    @Test
    public void testCreateNonRecursiveSuccess() throws IOException {
        Path shouldWork = path("nonrecursivenode");
        try (FSDataOutputStream out = createNonRecursive(shouldWork)) {
            out.write(0);
            out.close();
        }
        assertIsFile(shouldWork);
    }

    @Test(expected = FileNotFoundException.class)
    public void testCreateNonRecursiveNoParent() throws IOException {
        createNonRecursive(path("/recursive/node"));
    }

    @Test(expected = FileAlreadyExistsException.class)
    public void testCreateNonRecursiveParentIsFile() throws IOException {
        Path parent = path("/file.txt");
        ContractTestUtils.touch(getFileSystem(), parent);
        createNonRecursive(new Path(parent, "fail"));
    }

    @Test
    public void testPutObjectDirect() throws Throwable {
        final S3AFileSystem fs = getFileSystem();
        ObjectMetadata metadata = fs.newObjectMetadata((-1));
        metadata.setContentLength((-1));
        Path path = path("putDirect");
        final PutObjectRequest put = new PutObjectRequest(fs.getBucket(), path.toUri().getPath(), new ByteArrayInputStream("PUT".getBytes()), metadata);
        LambdaTestUtils.intercept(IllegalStateException.class, () -> fs.putObjectDirect(put));
        assertPathDoesNotExist("put object was created", path);
    }

    /**
     * The assumption here is that 0-byte files uploaded in a single PUT
     * always have the same checksum, including stores with encryption.
     *
     * @throws Throwable
     * 		on a failure
     */
    @Test
    public void testEmptyFileChecksums() throws Throwable {
        final S3AFileSystem fs = getFileSystem();
        Path file1 = touchFile("file1");
        EtagChecksum checksum1 = fs.getFileChecksum(file1, 0);
        AbstractS3ATestBase.LOG.info("Checksum for {}: {}", file1, checksum1);
        assertNotNull("Null file 1 checksum", checksum1);
        assertNotEquals("file 1 checksum", 0, checksum1.getLength());
        assertEquals("checksums", checksum1, fs.getFileChecksum(touchFile("file2"), 0));
    }

    /**
     * Make sure that when checksums are disabled, the caller
     * gets null back.
     */
    @Test
    public void testChecksumDisabled() throws Throwable {
        // checksums are forced off.
        enableChecksums(false);
        final S3AFileSystem fs = getFileSystem();
        Path file1 = touchFile("file1");
        EtagChecksum checksum1 = fs.getFileChecksum(file1, 0);
        assertNull("Checksums are being generated", checksum1);
    }

    /**
     * Verify that different file contents have different
     * checksums, and that that they aren't the same as the empty file.
     *
     * @throws Throwable
     * 		failure
     */
    @Test
    public void testNonEmptyFileChecksums() throws Throwable {
        final S3AFileSystem fs = getFileSystem();
        final Path file3 = mkFile("file3", ITestS3AMiscOperations.HELLO);
        final EtagChecksum checksum1 = fs.getFileChecksum(file3, 0);
        assertNotNull("file 3 checksum", checksum1);
        final Path file4 = touchFile("file4");
        final EtagChecksum checksum2 = fs.getFileChecksum(file4, 0);
        assertNotEquals("checksums", checksum1, checksum2);
        // overwrite
        ContractTestUtils.createFile(fs, file4, true, "hello, world".getBytes(StandardCharsets.UTF_8));
        assertNotEquals(checksum2, fs.getFileChecksum(file4, 0));
    }

    /**
     * Verify that on an unencrypted store, the checksum of two non-empty
     * (single PUT) files is the same if the data is the same.
     * This will fail if the bucket has S3 default encryption enabled.
     *
     * @throws Throwable
     * 		failure
     */
    @Test
    public void testNonEmptyFileChecksumsUnencrypted() throws Throwable {
        Assume.assumeTrue(encryptionAlgorithm().equals(NONE));
        final S3AFileSystem fs = getFileSystem();
        final EtagChecksum checksum1 = fs.getFileChecksum(mkFile("file5", ITestS3AMiscOperations.HELLO), 0);
        assertNotNull("file 3 checksum", checksum1);
        assertEquals("checksums", checksum1, fs.getFileChecksum(mkFile("file6", ITestS3AMiscOperations.HELLO), 0));
    }

    @Test
    public void testNegativeLength() throws Throwable {
        LambdaTestUtils.intercept(IllegalArgumentException.class, () -> getFileSystem().getFileChecksum(mkFile("negative", HELLO), (-1)));
    }

    @Test
    public void testNegativeLengthDisabledChecksum() throws Throwable {
        enableChecksums(false);
        LambdaTestUtils.intercept(IllegalArgumentException.class, () -> getFileSystem().getFileChecksum(mkFile("negative", HELLO), (-1)));
    }

    @Test
    public void testChecksumLengthPastEOF() throws Throwable {
        enableChecksums(true);
        final S3AFileSystem fs = getFileSystem();
        Path f = mkFile("file5", ITestS3AMiscOperations.HELLO);
        EtagChecksum l = fs.getFileChecksum(f, ITestS3AMiscOperations.HELLO.length);
        assertNotNull("Null checksum", l);
        assertEquals(l, fs.getFileChecksum(f, ((ITestS3AMiscOperations.HELLO.length) * 2)));
    }

    @Test
    public void testS3AToStringUnitialized() throws Throwable {
        try (S3AFileSystem fs = new S3AFileSystem()) {
            fs.toString();
        }
    }
}

