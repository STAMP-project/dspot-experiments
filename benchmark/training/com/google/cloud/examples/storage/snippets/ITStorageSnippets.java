/**
 * Copyright 2016 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.examples.storage.snippets;


import Acl.Role.OWNER;
import Role.READER;
import com.google.api.gax.paging.Page;
import com.google.cloud.ServiceOptions;
import com.google.cloud.storage.Acl;
import com.google.cloud.storage.Acl.User;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Bucket.BlobTargetOption;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageException;
import com.google.cloud.storage.testing.RemoteStorageHelper;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterators;
import com.google.common.collect.Sets;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.Timeout;


public class ITStorageSnippets {
    private static final Logger log = Logger.getLogger(ITStorageSnippets.class.getName());

    private static final String BUCKET = RemoteStorageHelper.generateBucketName();

    private static final byte[] BLOB_BYTE_CONTENT = new byte[]{ 13, 14, 10, 13 };

    private static final String USER_EMAIL = "google-cloud-java-tests@" + "java-docs-samples-tests.iam.gserviceaccount.com";

    private static final String KMS_KEY_NAME = "projects/gcloud-devel/locations/us/" + "keyRings/gcs_kms_key_ring_us/cryptoKeys/key";

    private static final Long RETENTION_PERIOD = 5L;// 5 seconds


    private static Storage storage;

    private static StorageSnippets storageSnippets;

    private static List<String> bucketsToCleanUp;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Rule
    public Timeout globalTimeout = Timeout.seconds(300);

    @Test
    public void testCreateBucketWithStorageClassAndLocation() throws InterruptedException, ExecutionException {
        String tempBucket = RemoteStorageHelper.generateBucketName();
        Bucket bucket = ITStorageSnippets.storageSnippets.createBucketWithStorageClassAndLocation(tempBucket);
        Assert.assertNotNull(bucket);
    }

    @Test
    public void testBlob() throws InterruptedException {
        String blobName = "directory/test-blob";
        Blob blob = ITStorageSnippets.storageSnippets.createBlob(ITStorageSnippets.BUCKET, blobName);
        Assert.assertNotNull(blob);
        blob = ITStorageSnippets.storageSnippets.getBlobFromId(ITStorageSnippets.BUCKET, blobName);
        Assert.assertNotNull(blob);
        blob = ITStorageSnippets.storageSnippets.updateBlob(ITStorageSnippets.BUCKET, blobName);
        Assert.assertNotNull(blob);
        blob = ITStorageSnippets.storageSnippets.updateBlobWithMetageneration(ITStorageSnippets.BUCKET, blobName);
        Assert.assertNotNull(blob);
        Blob copiedBlob = ITStorageSnippets.storageSnippets.copyBlob(ITStorageSnippets.BUCKET, blobName, "directory/copy-blob");
        Assert.assertNotNull(copiedBlob);
        Page<Blob> blobs = ITStorageSnippets.storageSnippets.listBlobsWithDirectoryAndPrefix(ITStorageSnippets.BUCKET, "directory/");
        while ((Iterators.size(blobs.iterateAll().iterator())) < 2) {
            Thread.sleep(500);
            blobs = ITStorageSnippets.storageSnippets.listBlobsWithDirectoryAndPrefix(ITStorageSnippets.BUCKET, "directory/");
        } 
        Set<String> blobNames = new HashSet<>();
        Iterator<Blob> blobIterator = blobs.iterateAll().iterator();
        while (blobIterator.hasNext()) {
            blobNames.add(blobIterator.next().getName());
        } 
        Assert.assertTrue(blobNames.contains(blobName));
        Assert.assertTrue(blobNames.contains("directory/copy-blob"));
        try {
            ITStorageSnippets.storageSnippets.getBlobFromStringsWithMetageneration(ITStorageSnippets.BUCKET, blobName, (-1));
            Assert.fail("Expected StorageException to be thrown");
        } catch (StorageException ex) {
            // expected
        }
        Assert.assertTrue(ITStorageSnippets.storageSnippets.deleteBlob(ITStorageSnippets.BUCKET, blobName));
        copiedBlob.delete();
    }

    @Test
    public void testCreateUpdateEncryptedBlob() throws InterruptedException {
        // Note: DO NOT put your encryption key in your code, like it is here. Store it somewhere safe,
        // and read it in when you need it. This key is just here to make the code easier to read.
        String encryptionKey1 = "0mMWhFvQOdS4AmxRpo8SJxXn5MjFhbz7DkKBUdUIef8=";
        String blobName = "encrypted-blob";
        Blob blob = ITStorageSnippets.storageSnippets.createEncryptedBlob(ITStorageSnippets.BUCKET, blobName, encryptionKey1);
        Assert.assertNotNull(blob);
        Assert.assertEquals("text/plain", blob.getContentType());
        byte[] encryptedContent = ITStorageSnippets.storageSnippets.readEncryptedBlob(ITStorageSnippets.BUCKET, blobName, encryptionKey1);
        Assert.assertEquals("Hello, World!", new String(encryptedContent));
        blob = ITStorageSnippets.storageSnippets.getBlobFromId(ITStorageSnippets.BUCKET, blobName);
        Assert.assertEquals("text/plain", blob.getContentType());
        String encryptionKey2 = "wnxMO0w+dmxribu7rICJ+Q2ES9TLpFRIDy3/L7HN5ZA=";
        blob = ITStorageSnippets.storageSnippets.rotateBlobEncryptionKey(ITStorageSnippets.BUCKET, blobName, encryptionKey1, encryptionKey2);
        Assert.assertNotNull(blob);
        encryptedContent = ITStorageSnippets.storageSnippets.readEncryptedBlob(ITStorageSnippets.BUCKET, blobName, encryptionKey2);
        Assert.assertEquals("Hello, World!", new String(encryptedContent));
        blob = ITStorageSnippets.storageSnippets.getBlobFromId(ITStorageSnippets.BUCKET, blobName);
        Assert.assertEquals("text/plain", blob.getContentType());
    }

    @Test
    public void testCreateKMSEncryptedBlob() {
        String blobName = "kms-encrypted-blob";
        Blob blob = ITStorageSnippets.storageSnippets.createKmsEncrpytedBlob(ITStorageSnippets.BUCKET, blobName, ITStorageSnippets.KMS_KEY_NAME);
        Assert.assertNotNull(blob);
    }

    @Test
    public void testCreateCopyAndGetBlob() {
        String blobName = "test-create-copy-get-blob";
        Blob blob = ITStorageSnippets.storageSnippets.createBlobFromByteArray(ITStorageSnippets.BUCKET, blobName);
        Assert.assertNotNull(blob);
        Blob copiedBlob = ITStorageSnippets.storageSnippets.copyBlobInChunks(ITStorageSnippets.BUCKET, blobName, "copy-blob");
        Assert.assertNotNull(copiedBlob);
        try {
            ITStorageSnippets.storageSnippets.getBlobFromIdWithMetageneration(ITStorageSnippets.BUCKET, blobName, (-1));
            Assert.fail("Expected StorageException to be thrown");
        } catch (StorageException ex) {
            // expected
        }
        Assert.assertTrue(ITStorageSnippets.storageSnippets.deleteBlobFromIdWithGeneration(ITStorageSnippets.BUCKET, blobName, blob.getGeneration()));
        copiedBlob.delete();
    }

    @Test
    public void testCreateCopyAndGetBlobFromSubArray() {
        String blobName = "test-create-copy-get-blob-from-sub-array";
        Blob blob = ITStorageSnippets.storageSnippets.createBlobWithSubArrayFromByteArray(ITStorageSnippets.BUCKET, blobName, 7, 1);
        Assert.assertNotNull(blob);
        Blob copiedBlob = ITStorageSnippets.storageSnippets.copyBlobInChunks(ITStorageSnippets.BUCKET, blobName, "copy-blob");
        Assert.assertNotNull(copiedBlob);
        try {
            ITStorageSnippets.storageSnippets.getBlobFromIdWithMetageneration(ITStorageSnippets.BUCKET, blobName, (-1));
            Assert.fail("Expected StorageException to be thrown");
        } catch (StorageException ex) {
            // expected
        }
        Assert.assertTrue(ITStorageSnippets.storageSnippets.deleteBlobFromIdWithGeneration(ITStorageSnippets.BUCKET, blobName, blob.getGeneration()));
        copiedBlob.delete();
    }

    @Test
    public void testCreateBlobFromInputStream() {
        Blob blob = ITStorageSnippets.storageSnippets.createBlobFromInputStream(ITStorageSnippets.BUCKET, "test-create-blob-from-input-stream");
        Assert.assertNotNull(blob);
        Assert.assertTrue(ITStorageSnippets.storageSnippets.deleteBlobFromIdWithGeneration(ITStorageSnippets.BUCKET, "test-create-blob-from-input-stream", blob.getGeneration()));
    }

    @Test
    public void testGetBucketWithMetageneration() {
        thrown.expect(StorageException.class);
        ITStorageSnippets.storageSnippets.getBucketWithMetageneration(ITStorageSnippets.BUCKET, (-1));
    }

    @Test
    public void testListBucketsWithSizeAndPrefix() throws InterruptedException {
        Page<Bucket> buckets = ITStorageSnippets.storageSnippets.listBucketsWithSizeAndPrefix(ITStorageSnippets.BUCKET);
        while ((Iterators.size(buckets.iterateAll().iterator())) < 1) {
            Thread.sleep(500);
            buckets = ITStorageSnippets.storageSnippets.listBucketsWithSizeAndPrefix(ITStorageSnippets.BUCKET);
        } 
        Iterator<Bucket> bucketIterator = buckets.iterateAll().iterator();
        while (bucketIterator.hasNext()) {
            Assert.assertTrue(bucketIterator.next().getName().startsWith(ITStorageSnippets.BUCKET));
        } 
    }

    @Test
    public void testUpdateBucket() {
        Assert.assertNotNull(ITStorageSnippets.storageSnippets.updateBucket(ITStorageSnippets.BUCKET));
    }

    @Test
    public void testDeleteBucketWithMetageneration() {
        thrown.expect(StorageException.class);
        ITStorageSnippets.storageSnippets.deleteBucketWithMetageneration(ITStorageSnippets.BUCKET, (-1));
    }

    @Test
    public void testComposeBlobs() {
        String blobName = "my_blob_name";
        String sourceBlobName1 = "source_blob_1";
        String sourceBlobName2 = "source_blob_2";
        BlobInfo blobInfo1 = BlobInfo.newBuilder(ITStorageSnippets.BUCKET, sourceBlobName1).build();
        BlobInfo blobInfo2 = BlobInfo.newBuilder(ITStorageSnippets.BUCKET, sourceBlobName2).build();
        ITStorageSnippets.storage.create(blobInfo1);
        ITStorageSnippets.storage.create(blobInfo2);
        Assert.assertNotNull(ITStorageSnippets.storageSnippets.composeBlobs(ITStorageSnippets.BUCKET, blobName, sourceBlobName1, sourceBlobName2));
    }

    @Test
    public void testReadWriteAndSignUrl() throws IOException {
        String blobName = "text-read-write-sign-url";
        byte[] content = "Hello, World!".getBytes(StandardCharsets.UTF_8);
        Blob blob = ITStorageSnippets.storage.create(BlobInfo.newBuilder(ITStorageSnippets.BUCKET, blobName).build(), content);
        Assert.assertArrayEquals(content, ITStorageSnippets.storageSnippets.readBlobFromId(ITStorageSnippets.BUCKET, blobName, blob.getGeneration()));
        Assert.assertArrayEquals(content, ITStorageSnippets.storageSnippets.readBlobFromStringsWithGeneration(ITStorageSnippets.BUCKET, blobName, blob.getGeneration()));
        ITStorageSnippets.storageSnippets.readerFromId(ITStorageSnippets.BUCKET, blobName);
        ITStorageSnippets.storageSnippets.readerFromStrings(ITStorageSnippets.BUCKET, blobName);
        ITStorageSnippets.storageSnippets.writer(ITStorageSnippets.BUCKET, blobName);
        URL signedUrl = ITStorageSnippets.storageSnippets.signUrl(ITStorageSnippets.BUCKET, blobName);
        URLConnection connection = signedUrl.openConnection();
        byte[] readBytes = new byte[content.length];
        try (InputStream responseStream = connection.getInputStream()) {
            Assert.assertEquals(content.length, responseStream.read(readBytes));
            Assert.assertArrayEquals(content, readBytes);
        }
        signedUrl = ITStorageSnippets.storageSnippets.signUrlWithSigner(ITStorageSnippets.BUCKET, blobName, System.getenv("GOOGLE_APPLICATION_CREDENTIALS"));
        connection = signedUrl.openConnection();
        try (InputStream responseStream = connection.getInputStream()) {
            Assert.assertEquals(content.length, responseStream.read(readBytes));
            Assert.assertArrayEquals(content, readBytes);
        }
        blob.delete();
    }

    @Test
    public void testBatch() throws IOException {
        String blobName1 = "test-batch1";
        String blobName2 = "test-batch2";
        ITStorageSnippets.storage.create(BlobInfo.newBuilder(ITStorageSnippets.BUCKET, blobName1).build());
        ITStorageSnippets.storage.create(BlobInfo.newBuilder(ITStorageSnippets.BUCKET, blobName2).build());
        List<Blob> blobs = ITStorageSnippets.storageSnippets.batchGet(ITStorageSnippets.BUCKET, blobName1, blobName2);
        Assert.assertEquals(blobName1, blobs.get(0).getName());
        Assert.assertEquals(blobName2, blobs.get(1).getName());
        blobs = ITStorageSnippets.storageSnippets.batchUpdate(ITStorageSnippets.BUCKET, blobName1, blobName2);
        Assert.assertEquals(blobName1, blobs.get(0).getName());
        Assert.assertEquals(blobName2, blobs.get(1).getName());
        Assert.assertEquals("text/plain", blobs.get(0).getContentType());
        Assert.assertEquals("text/plain", blobs.get(1).getContentType());
        Assert.assertNotNull(ITStorageSnippets.storageSnippets.batch(ITStorageSnippets.BUCKET, blobName1, blobName2));
        List<Boolean> deleted = ITStorageSnippets.storageSnippets.batchDelete(ITStorageSnippets.BUCKET, blobName1, blobName2);
        Assert.assertFalse(deleted.get(0));
        Assert.assertTrue(deleted.get(1));
    }

    @Test
    public void testBatchIterable() throws IOException {
        String blobName1 = "test-batch-iterable1";
        String blobName2 = "test-batch-iterable2";
        ITStorageSnippets.storage.create(BlobInfo.newBuilder(ITStorageSnippets.BUCKET, blobName1).build());
        ITStorageSnippets.storage.create(BlobInfo.newBuilder(ITStorageSnippets.BUCKET, blobName2).build());
        List<Blob> blobs = ITStorageSnippets.storageSnippets.batchGetIterable(ITStorageSnippets.BUCKET, blobName1, blobName2);
        Assert.assertEquals(blobName1, blobs.get(0).getName());
        Assert.assertEquals(blobName2, blobs.get(1).getName());
        blobs = ITStorageSnippets.storageSnippets.batchUpdateIterable(ITStorageSnippets.BUCKET, blobName1, blobName2);
        Assert.assertEquals(blobName1, blobs.get(0).getName());
        Assert.assertEquals(blobName2, blobs.get(1).getName());
        Assert.assertEquals("text/plain", blobs.get(0).getContentType());
        Assert.assertEquals("text/plain", blobs.get(1).getContentType());
        Assert.assertNotNull(ITStorageSnippets.storageSnippets.batch(ITStorageSnippets.BUCKET, blobName1, blobName2));
        List<Boolean> deleted = ITStorageSnippets.storageSnippets.batchDeleteIterable(ITStorageSnippets.BUCKET, blobName1, blobName2);
        Assert.assertFalse(deleted.get(0));
        Assert.assertTrue(deleted.get(1));
    }

    @Test
    public void testBucketAcl() {
        Assert.assertNull(ITStorageSnippets.storageSnippets.getBucketAcl(ITStorageSnippets.BUCKET));
        Assert.assertFalse(ITStorageSnippets.storageSnippets.deleteBucketAcl(ITStorageSnippets.BUCKET));
        Assert.assertNotNull(ITStorageSnippets.storageSnippets.createBucketAcl(ITStorageSnippets.BUCKET));
        Acl updatedAcl = ITStorageSnippets.storageSnippets.updateBucketAcl(ITStorageSnippets.BUCKET);
        Assert.assertEquals(OWNER, updatedAcl.getRole());
        Set<Acl> acls = Sets.newHashSet(ITStorageSnippets.storageSnippets.listBucketAcls(ITStorageSnippets.BUCKET));
        Assert.assertTrue(acls.contains(updatedAcl));
        Assert.assertNotNull(ITStorageSnippets.storageSnippets.getBucketAcl(ITStorageSnippets.BUCKET));
        Assert.assertNull(ITStorageSnippets.storageSnippets.getBucketAcl(ITStorageSnippets.BUCKET, ITStorageSnippets.USER_EMAIL));
        ITStorageSnippets.storage.createAcl(ITStorageSnippets.BUCKET, Acl.of(new User(ITStorageSnippets.USER_EMAIL), READER));
        Acl userAcl = ITStorageSnippets.storageSnippets.getBucketAcl(ITStorageSnippets.BUCKET, ITStorageSnippets.USER_EMAIL);
        Assert.assertNotNull(userAcl);
        Assert.assertEquals(ITStorageSnippets.USER_EMAIL, getEmail());
        Assert.assertTrue(ITStorageSnippets.storageSnippets.deleteBucketAcl(ITStorageSnippets.BUCKET));
        Assert.assertNull(ITStorageSnippets.storageSnippets.getBucketAcl(ITStorageSnippets.BUCKET));
    }

    @Test
    public void testDefaultBucketAcl() {
        Assert.assertNull(ITStorageSnippets.storageSnippets.getDefaultBucketAcl(ITStorageSnippets.BUCKET));
        Assert.assertFalse(ITStorageSnippets.storageSnippets.deleteDefaultBucketAcl(ITStorageSnippets.BUCKET));
        Assert.assertNotNull(ITStorageSnippets.storageSnippets.createDefaultBucketAcl(ITStorageSnippets.BUCKET));
        Acl updatedAcl = ITStorageSnippets.storageSnippets.updateDefaultBucketAcl(ITStorageSnippets.BUCKET);
        Assert.assertEquals(OWNER, updatedAcl.getRole());
        Set<Acl> acls = Sets.newHashSet(ITStorageSnippets.storageSnippets.listDefaultBucketAcls(ITStorageSnippets.BUCKET));
        Assert.assertTrue(acls.contains(updatedAcl));
        Assert.assertTrue(ITStorageSnippets.storageSnippets.deleteDefaultBucketAcl(ITStorageSnippets.BUCKET));
        Assert.assertNull(ITStorageSnippets.storageSnippets.getDefaultBucketAcl(ITStorageSnippets.BUCKET));
    }

    @Test
    public void testBlobAcl() {
        String blobName = "test-blob-acl";
        BlobId blobId = BlobId.of(ITStorageSnippets.BUCKET, "test-blob-acl");
        BlobInfo blob = BlobInfo.newBuilder(blobId).build();
        Blob createdBlob = ITStorageSnippets.storage.create(blob);
        Assert.assertNull(ITStorageSnippets.storageSnippets.getBlobAcl(ITStorageSnippets.BUCKET, blobName, createdBlob.getGeneration()));
        Assert.assertNotNull(ITStorageSnippets.storageSnippets.createBlobAcl(ITStorageSnippets.BUCKET, blobName, createdBlob.getGeneration()));
        Acl updatedAcl = ITStorageSnippets.storageSnippets.updateBlobAcl(ITStorageSnippets.BUCKET, blobName, createdBlob.getGeneration());
        Assert.assertEquals(OWNER, updatedAcl.getRole());
        Set<Acl> acls = Sets.newHashSet(ITStorageSnippets.storageSnippets.listBlobAcls(ITStorageSnippets.BUCKET, blobName, createdBlob.getGeneration()));
        Assert.assertTrue(acls.contains(updatedAcl));
        Assert.assertNull(ITStorageSnippets.storageSnippets.getBlobAcl(ITStorageSnippets.BUCKET, blobName, ITStorageSnippets.USER_EMAIL));
        ITStorageSnippets.storage.createAcl(BlobId.of(ITStorageSnippets.BUCKET, blobName), Acl.of(new User(ITStorageSnippets.USER_EMAIL), READER));
        Acl userAcl = ITStorageSnippets.storageSnippets.getBlobAcl(ITStorageSnippets.BUCKET, blobName, ITStorageSnippets.USER_EMAIL);
        Assert.assertNotNull(userAcl);
        Assert.assertEquals(ITStorageSnippets.USER_EMAIL, getEmail());
        updatedAcl = ITStorageSnippets.storageSnippets.blobToPublicRead(ITStorageSnippets.BUCKET, blobName, createdBlob.getGeneration());
        Assert.assertEquals(Acl.Role.READER, updatedAcl.getRole());
        Assert.assertEquals(User.ofAllUsers(), updatedAcl.getEntity());
        acls = Sets.newHashSet(ITStorageSnippets.storageSnippets.listBlobAcls(ITStorageSnippets.BUCKET, blobName, createdBlob.getGeneration()));
        Assert.assertTrue(acls.contains(updatedAcl));
        Assert.assertNotNull(ITStorageSnippets.storageSnippets.getBlobAcl(ITStorageSnippets.BUCKET, blobName, createdBlob.getGeneration()));
        Assert.assertTrue(ITStorageSnippets.storageSnippets.deleteBlobAcl(ITStorageSnippets.BUCKET, blobName, createdBlob.getGeneration()));
        Assert.assertNull(ITStorageSnippets.storageSnippets.getBlobAcl(ITStorageSnippets.BUCKET, blobName, createdBlob.getGeneration()));
        // test non-existing blob
        String nonExistingBlob = "test-blob-acl";
        Assert.assertNull(ITStorageSnippets.storageSnippets.getBlobAcl(ITStorageSnippets.BUCKET, nonExistingBlob, 1L));
        Assert.assertFalse(ITStorageSnippets.storageSnippets.deleteBlobAcl(ITStorageSnippets.BUCKET, nonExistingBlob, 1L));
        try {
            ITStorageSnippets.storageSnippets.createBlobAcl(ITStorageSnippets.BUCKET, nonExistingBlob, 1L);
            Assert.fail("Expected StorageException");
        } catch (StorageException ex) {
            // expected
        }
        try {
            ITStorageSnippets.storageSnippets.updateBlobAcl(ITStorageSnippets.BUCKET, nonExistingBlob, 1L);
            Assert.fail("Expected StorageException");
        } catch (StorageException ex) {
            // expected
        }
        try {
            ITStorageSnippets.storageSnippets.listBlobAcls(ITStorageSnippets.BUCKET, nonExistingBlob, 1L);
            Assert.fail("Expected StorageException");
        } catch (StorageException ex) {
            // expected
        }
    }

    @Test
    public void testAuthListBuckets() {
        Page<Bucket> bucket = ITStorageSnippets.storageSnippets.authListBuckets();
        Assert.assertNotNull(bucket);
    }

    @Test
    public void testBlobDownload() throws Exception {
        String blobName = "test-create-empty-blob";
        BlobId blobId = BlobId.of(ITStorageSnippets.BUCKET, blobName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
        Blob remoteBlob = ITStorageSnippets.storage.create(blobInfo, ITStorageSnippets.BLOB_BYTE_CONTENT);
        Assert.assertNotNull(remoteBlob);
        ITStorageSnippets.storageSnippets.downloadFile(ITStorageSnippets.BUCKET, blobName, Paths.get(blobName));
        byte[] readBytes = Files.readAllBytes(Paths.get(blobName));
        Assert.assertArrayEquals(ITStorageSnippets.BLOB_BYTE_CONTENT, readBytes);
    }

    @Test
    public void testGetBlobMetadata() {
        String blobName = "test-create-empty-blob";
        BlobId blobId = BlobId.of(ITStorageSnippets.BUCKET, blobName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setMetadata(ImmutableMap.of("k", "v")).build();
        Blob remoteBlob = ITStorageSnippets.storage.create(blobInfo, ITStorageSnippets.BLOB_BYTE_CONTENT);
        Assert.assertNotNull(remoteBlob);
        final ByteArrayOutputStream snippetOutputCapture = new ByteArrayOutputStream();
        System.setOut(new PrintStream(snippetOutputCapture));
        ITStorageSnippets.storageSnippets.getBlobMetadata(ITStorageSnippets.BUCKET, blobName);
        String snippetOutput = snippetOutputCapture.toString();
        System.setOut(System.out);
        Assert.assertTrue(snippetOutput.contains(("Bucket: " + (remoteBlob.getBucket()))));
        Assert.assertTrue(snippetOutput.contains(("Bucket: " + (remoteBlob.getBucket()))));
        Assert.assertTrue(snippetOutput.contains(("CacheControl: " + (remoteBlob.getCacheControl()))));
        Assert.assertTrue(snippetOutput.contains(("ComponentCount: " + (remoteBlob.getComponentCount()))));
        Assert.assertTrue(snippetOutput.contains(("ContentDisposition: " + (remoteBlob.getContentDisposition()))));
        Assert.assertTrue(snippetOutput.contains(("ContentEncoding: " + (remoteBlob.getContentEncoding()))));
        Assert.assertTrue(snippetOutput.contains(("ContentLanguage: " + (remoteBlob.getContentLanguage()))));
        Assert.assertTrue(snippetOutput.contains(("ContentType: " + (remoteBlob.getContentType()))));
        Assert.assertTrue(snippetOutput.contains(("Crc32c: " + (remoteBlob.getCrc32c()))));
        Assert.assertTrue(snippetOutput.contains(("Crc32cHexString: " + (remoteBlob.getCrc32cToHexString()))));
        Assert.assertTrue(snippetOutput.contains(("ETag: " + (remoteBlob.getEtag()))));
        Assert.assertTrue(snippetOutput.contains(("Generation: " + (remoteBlob.getGeneration()))));
        Assert.assertTrue(snippetOutput.contains(("Id: " + (remoteBlob.getBlobId()))));
        Assert.assertTrue(snippetOutput.contains(("KmsKeyName: " + (remoteBlob.getKmsKeyName()))));
        Assert.assertTrue(snippetOutput.contains(("Md5Hash: " + (remoteBlob.getMd5()))));
        Assert.assertTrue(snippetOutput.contains(("Md5HexString: " + (remoteBlob.getMd5ToHexString()))));
        Assert.assertTrue(snippetOutput.contains(("MediaLink: " + (remoteBlob.getMediaLink()))));
        Assert.assertTrue(snippetOutput.contains(("Metageneration: " + (remoteBlob.getMetageneration()))));
        Assert.assertTrue(snippetOutput.contains(("Name: " + (remoteBlob.getName()))));
        Assert.assertTrue(snippetOutput.contains(("Size: " + (remoteBlob.getSize()))));
        Assert.assertTrue(snippetOutput.contains(("StorageClass: " + (remoteBlob.getStorageClass()))));
        Assert.assertTrue(snippetOutput.contains(("TimeCreated: " + (new Date(remoteBlob.getCreateTime())))));
        Assert.assertTrue(snippetOutput.contains(("Last Metadata Update: " + (new Date(remoteBlob.getUpdateTime())))));
        Assert.assertTrue(snippetOutput.contains("temporaryHold: disabled"));
        Assert.assertTrue(snippetOutput.contains("eventBasedHold: disabled"));
        Assert.assertTrue(snippetOutput.contains("User metadata:"));
        Assert.assertTrue(snippetOutput.contains("k=v"));
    }

    @Test
    public void testRequesterPays() throws Exception {
        Bucket bucket = ITStorageSnippets.storageSnippets.enableRequesterPays(ITStorageSnippets.BUCKET);
        Assert.assertTrue(bucket.requesterPays());
        bucket = ITStorageSnippets.storageSnippets.getRequesterPaysStatus(ITStorageSnippets.BUCKET);
        Assert.assertTrue(bucket.requesterPays());
        String projectId = ServiceOptions.getDefaultProjectId();
        String blobName = "test-create-empty-blob-requester-pays";
        Blob remoteBlob = bucket.create(blobName, ITStorageSnippets.BLOB_BYTE_CONTENT, BlobTargetOption.userProject(projectId));
        Assert.assertNotNull(remoteBlob);
        ITStorageSnippets.storageSnippets.downloadFileUsingRequesterPays(projectId, ITStorageSnippets.BUCKET, blobName, Paths.get(blobName));
        byte[] readBytes = Files.readAllBytes(Paths.get(blobName));
        Assert.assertArrayEquals(ITStorageSnippets.BLOB_BYTE_CONTENT, readBytes);
        bucket = ITStorageSnippets.storageSnippets.disableRequesterPays(ITStorageSnippets.BUCKET);
        Assert.assertFalse(bucket.requesterPays());
    }

    @Test
    public void testDefaultKMSKey() {
        Bucket bucket = ITStorageSnippets.storageSnippets.setDefaultKmsKey(ITStorageSnippets.BUCKET, ITStorageSnippets.KMS_KEY_NAME);
        Assert.assertEquals(ITStorageSnippets.KMS_KEY_NAME, bucket.getDefaultKmsKeyName());
        // Remove default key
        ITStorageSnippets.storageSnippets.setDefaultKmsKey(ITStorageSnippets.BUCKET, null);
    }

    @Test
    public void testBucketRetention() {
        Bucket bucket = ITStorageSnippets.storageSnippets.setRetentionPolicy(ITStorageSnippets.BUCKET, ITStorageSnippets.RETENTION_PERIOD);
        Assert.assertEquals(bucket.getRetentionPeriod(), ITStorageSnippets.RETENTION_PERIOD);
        Assert.assertNotNull(bucket.getRetentionEffectiveTime());
        bucket = ITStorageSnippets.storageSnippets.getRetentionPolicy(ITStorageSnippets.BUCKET);
        Assert.assertEquals(bucket.getRetentionPeriod(), ITStorageSnippets.RETENTION_PERIOD);
        Assert.assertNotNull(bucket.getRetentionEffectiveTime());
        Assert.assertNull(bucket.retentionPolicyIsLocked());
        bucket = ITStorageSnippets.storageSnippets.enableDefaultEventBasedHold(ITStorageSnippets.BUCKET);
        Assert.assertTrue(bucket.getDefaultEventBasedHold());
        bucket = ITStorageSnippets.storageSnippets.getDefaultEventBasedHold(ITStorageSnippets.BUCKET);
        Assert.assertTrue(bucket.getDefaultEventBasedHold());
        String blobName = "test-create-empty-blob-retention-policy";
        Blob remoteBlob = bucket.create(blobName, ITStorageSnippets.BLOB_BYTE_CONTENT);
        Assert.assertTrue(remoteBlob.getEventBasedHold());
        remoteBlob = ITStorageSnippets.storageSnippets.setEventBasedHold(ITStorageSnippets.BUCKET, blobName);
        Assert.assertTrue(remoteBlob.getEventBasedHold());
        remoteBlob = ITStorageSnippets.storageSnippets.releaseEventBasedHold(ITStorageSnippets.BUCKET, blobName);
        Assert.assertFalse(remoteBlob.getEventBasedHold());
        Assert.assertNotNull(remoteBlob.getRetentionExpirationTime());
        bucket = ITStorageSnippets.storageSnippets.removeRetentionPolicy(ITStorageSnippets.BUCKET);
        Assert.assertNull(bucket.getRetentionPeriod());
        Assert.assertNull(bucket.getRetentionEffectiveTime());
        bucket = ITStorageSnippets.storageSnippets.disableDefaultEventBasedHold(ITStorageSnippets.BUCKET);
        Assert.assertFalse(bucket.getDefaultEventBasedHold());
        remoteBlob = ITStorageSnippets.storageSnippets.setTemporaryHold(ITStorageSnippets.BUCKET, blobName);
        Assert.assertTrue(remoteBlob.getTemporaryHold());
        remoteBlob = ITStorageSnippets.storageSnippets.releaseTemporaryHold(ITStorageSnippets.BUCKET, blobName);
        Assert.assertFalse(remoteBlob.getTemporaryHold());
    }

    @Test
    public void testLockRetentionPolicy() {
        String tempBucket = RemoteStorageHelper.generateBucketName();
        Bucket bucket = ITStorageSnippets.storageSnippets.createBucket(tempBucket);
        Assert.assertNotNull(bucket);
        bucket = ITStorageSnippets.storageSnippets.setRetentionPolicy(tempBucket, ITStorageSnippets.RETENTION_PERIOD);
        Assert.assertEquals(bucket.getRetentionPeriod(), ITStorageSnippets.RETENTION_PERIOD);
        bucket = ITStorageSnippets.storageSnippets.lockRetentionPolicy(tempBucket);
        Assert.assertTrue(bucket.retentionPolicyIsLocked());
    }

    @Test
    public void testBucketPolicyOnly() {
        String tempBucket = RemoteStorageHelper.generateBucketName();
        Bucket bucket = ITStorageSnippets.storageSnippets.createBucket(tempBucket);
        Assert.assertNotNull(bucket);
        bucket = ITStorageSnippets.storageSnippets.enableBucketPolicyOnly(tempBucket);
        Assert.assertTrue(bucket.getIamConfiguration().isBucketPolicyOnlyEnabled());
        Assert.assertNotNull(bucket.getIamConfiguration().getBucketPolicyOnlyLockedTime());
        bucket = ITStorageSnippets.storageSnippets.getBucketPolicyOnly(tempBucket);
        Assert.assertTrue(bucket.getIamConfiguration().isBucketPolicyOnlyEnabled());
        Assert.assertNotNull(bucket.getIamConfiguration().getBucketPolicyOnlyLockedTime());
        bucket = ITStorageSnippets.storageSnippets.disableBucketPolicyOnly(tempBucket);
        Assert.assertFalse(bucket.getIamConfiguration().isBucketPolicyOnlyEnabled());
    }
}

