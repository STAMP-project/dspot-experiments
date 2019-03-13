/**
 * Copyright 2015 Google LLC
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
package com.google.cloud.storage.it;


import BlobField.EVENT_BASED_HOLD;
import BlobField.KMS_KEY_NAME;
import BlobField.METADATA;
import BlobField.TEMPORARY_HOLD;
import BucketField.ACL;
import BucketField.DEFAULT_EVENT_BASED_HOLD;
import BucketField.DEFAULT_OBJECT_ACL;
import BucketField.ENCRYPTION;
import BucketField.IAMCONFIGURATION;
import BucketField.ID;
import BucketField.LIFECYCLE;
import BucketField.RETENTION_POLICY;
import BucketInfo.IamConfiguration;
import HttpMethod.POST;
import LifecycleRule.SetStorageClassLifecycleAction.TYPE;
import Metadata.ASCII_STRING_MARSHALLER;
import Metadata.Key;
import Role.OWNER;
import Role.READER;
import Storage.BlobGetOption;
import Storage.BlobListOption;
import Storage.BlobSourceOption;
import Storage.BlobTargetOption;
import Storage.BlobWriteOption;
import Storage.BucketGetOption;
import Storage.BucketListOption;
import Storage.ComposeRequest;
import Storage.CopyRequest;
import Storage.PredefinedAcl.PUBLIC_READ;
import Storage.SignUrlOption;
import StorageClass.COLDLINE;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.util.DateTime;
import com.google.api.gax.paging.Page;
import com.google.auth.ServiceAccountSigner;
import com.google.auth.http.HttpTransportFactory;
import com.google.cloud.ReadChannel;
import com.google.cloud.RestorableState;
import com.google.cloud.TransportOptions;
import com.google.cloud.WriteChannel;
import com.google.cloud.http.HttpTransportOptions;
import com.google.cloud.storage.Acl;
import com.google.cloud.storage.Acl.User;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.BucketInfo.LifecycleRule;
import com.google.cloud.storage.BucketInfo.LifecycleRule.LifecycleAction;
import com.google.cloud.storage.BucketInfo.LifecycleRule.LifecycleCondition;
import com.google.cloud.storage.CopyWriter;
import com.google.cloud.storage.ServiceAccount;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.Storage.BlobField;
import com.google.cloud.storage.Storage.BucketField;
import com.google.cloud.storage.StorageBatch;
import com.google.cloud.storage.StorageBatchResult;
import com.google.cloud.storage.StorageException;
import com.google.cloud.storage.StorageOptions;
import com.google.cloud.storage.testing.RemoteStorageHelper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.io.BaseEncoding;
import com.google.common.io.ByteStreams;
import io.grpc.Metadata;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import javax.crypto.spec.SecretKeySpec;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;


public class ITStorageTest {
    private static RemoteStorageHelper remoteStorageHelper;

    private static Storage storage;

    private static String kmsKeyOneResourcePath;

    private static String kmsKeyTwoResourcePath;

    private static Metadata requestParamsHeader = new Metadata();

    private static Metadata.Key<String> requestParamsKey = Key.of("x-goog-request-params", ASCII_STRING_MARSHALLER);

    private static final Logger log = Logger.getLogger(ITStorageTest.class.getName());

    private static final String BUCKET = RemoteStorageHelper.generateBucketName();

    private static final String CONTENT_TYPE = "text/plain";

    private static final byte[] BLOB_BYTE_CONTENT = new byte[]{ 13, 14, 10, 13 };

    private static final String BLOB_STRING_CONTENT = "Hello Google Cloud Storage!";

    private static final int MAX_BATCH_SIZE = 100;

    private static final String BASE64_KEY = "JVzfVl8NLD9FjedFuStegjRfES5ll5zc59CIXw572OA=";

    private static final String OTHER_BASE64_KEY = "IcOIQGlliNr5pr3vJb63l+XMqc7NjXqjfw/deBoNxPA=";

    private static final java.security.Key KEY = new SecretKeySpec(BaseEncoding.base64().decode(ITStorageTest.BASE64_KEY), "AES256");

    private static final byte[] COMPRESSED_CONTENT = BaseEncoding.base64().decode("H4sIAAAAAAAAAPNIzcnJV3DPz0/PSVVwzskvTVEILskvSkxPVQQA/LySchsAAAA=");

    private static final Map<String, String> BUCKET_LABELS = ImmutableMap.of("label1", "value1");

    private static final Long RETENTION_PERIOD = 5L;

    private static final Long RETENTION_PERIOD_IN_MILLISECONDS = (ITStorageTest.RETENTION_PERIOD) * 1000;

    private static final String SERVICE_ACCOUNT_EMAIL_SUFFIX = "@gs-project-accounts.iam.gserviceaccount.com";

    private static final String KMS_KEY_RING_NAME = "gcs_test_kms_key_ring";

    private static final String KMS_KEY_RING_LOCATION = "us";

    private static final String KMS_KEY_ONE_NAME = "gcs_kms_key_one";

    private static final String KMS_KEY_TWO_NAME = "gcs_kms_key_two";

    private static final boolean IS_VPC_TEST = ((System.getenv("GOOGLE_CLOUD_TESTS_IN_VPCSC")) != null) && (System.getenv("GOOGLE_CLOUD_TESTS_IN_VPCSC").equalsIgnoreCase("true"));

    private static class CustomHttpTransportFactory implements HttpTransportFactory {
        @Override
        public HttpTransport create() {
            PoolingHttpClientConnectionManager manager = new PoolingHttpClientConnectionManager();
            manager.setMaxTotal(1);
            return new com.google.api.client.http.apache.ApacheHttpTransport(HttpClients.createMinimal(manager));
        }
    }

    @Test(timeout = 5000)
    public void testListBuckets() throws InterruptedException {
        Iterator<Bucket> bucketIterator = ITStorageTest.storage.list(BucketListOption.prefix(ITStorageTest.BUCKET), BucketListOption.fields()).iterateAll().iterator();
        while (!(bucketIterator.hasNext())) {
            Thread.sleep(500);
            bucketIterator = ITStorageTest.storage.list(BucketListOption.prefix(ITStorageTest.BUCKET), BucketListOption.fields()).iterateAll().iterator();
        } 
        while (bucketIterator.hasNext()) {
            Bucket remoteBucket = bucketIterator.next();
            Assert.assertTrue(remoteBucket.getName().startsWith(ITStorageTest.BUCKET));
            Assert.assertNull(remoteBucket.getCreateTime());
            Assert.assertNull(remoteBucket.getSelfLink());
        } 
    }

    @Test
    public void testGetBucketSelectedFields() {
        Bucket remoteBucket = ITStorageTest.storage.get(ITStorageTest.BUCKET, BucketGetOption.fields(ID));
        Assert.assertEquals(ITStorageTest.BUCKET, remoteBucket.getName());
        Assert.assertNull(remoteBucket.getCreateTime());
        Assert.assertNotNull(remoteBucket.getGeneratedId());
    }

    @Test
    public void testGetBucketAllSelectedFields() {
        Bucket remoteBucket = ITStorageTest.storage.get(ITStorageTest.BUCKET, BucketGetOption.fields(BucketField.values()));
        Assert.assertEquals(ITStorageTest.BUCKET, remoteBucket.getName());
        Assert.assertNotNull(remoteBucket.getCreateTime());
        Assert.assertNotNull(remoteBucket.getSelfLink());
    }

    @Test
    public void testGetBucketEmptyFields() {
        Bucket remoteBucket = ITStorageTest.storage.get(ITStorageTest.BUCKET, BucketGetOption.fields());
        Assert.assertEquals(ITStorageTest.BUCKET, remoteBucket.getName());
        Assert.assertNull(remoteBucket.getCreateTime());
        Assert.assertNull(remoteBucket.getSelfLink());
    }

    @Test
    public void testGetBucketLifecycleRules() {
        String lifecycleTestBucketName = RemoteStorageHelper.generateBucketName();
        ITStorageTest.storage.create(BucketInfo.newBuilder(lifecycleTestBucketName).setLocation("us").setLifecycleRules(ImmutableList.of(new LifecycleRule(LifecycleAction.newSetStorageClassAction(COLDLINE), LifecycleCondition.newBuilder().setAge(1).setNumberOfNewerVersions(3).setIsLive(false).setCreatedBefore(new DateTime(System.currentTimeMillis())).setMatchesStorageClass(ImmutableList.of(COLDLINE)).build()))).build());
        Bucket remoteBucket = ITStorageTest.storage.get(lifecycleTestBucketName, BucketGetOption.fields(LIFECYCLE));
        LifecycleRule lifecycleRule = remoteBucket.getLifecycleRules().get(0);
        try {
            Assert.assertTrue(lifecycleRule.getAction().getActionType().equals(TYPE));
            Assert.assertEquals(3, lifecycleRule.getCondition().getNumberOfNewerVersions().intValue());
            Assert.assertNotNull(lifecycleRule.getCondition().getCreatedBefore());
            Assert.assertFalse(lifecycleRule.getCondition().getIsLive());
            Assert.assertEquals(1, lifecycleRule.getCondition().getAge().intValue());
            Assert.assertEquals(1, lifecycleRule.getCondition().getMatchesStorageClass().size());
        } finally {
            ITStorageTest.storage.delete(lifecycleTestBucketName);
        }
    }

    @Test
    public void testClearBucketDefaultKmsKeyName() throws InterruptedException, ExecutionException {
        String bucketName = RemoteStorageHelper.generateBucketName();
        Bucket remoteBucket = ITStorageTest.storage.create(BucketInfo.newBuilder(bucketName).setDefaultKmsKeyName(ITStorageTest.kmsKeyOneResourcePath).setLocation(ITStorageTest.KMS_KEY_RING_LOCATION).build());
        try {
            Assert.assertEquals(ITStorageTest.kmsKeyOneResourcePath, remoteBucket.getDefaultKmsKeyName());
            Bucket updatedBucket = remoteBucket.toBuilder().setDefaultKmsKeyName(null).build().update();
            Assert.assertNull(updatedBucket.getDefaultKmsKeyName());
        } finally {
            RemoteStorageHelper.forceDelete(ITStorageTest.storage, bucketName, 5, TimeUnit.SECONDS);
        }
    }

    @Test
    public void testUpdateBucketDefaultKmsKeyName() throws InterruptedException, ExecutionException {
        String bucketName = RemoteStorageHelper.generateBucketName();
        Bucket remoteBucket = ITStorageTest.storage.create(BucketInfo.newBuilder(bucketName).setDefaultKmsKeyName(ITStorageTest.kmsKeyOneResourcePath).setLocation(ITStorageTest.KMS_KEY_RING_LOCATION).build());
        try {
            Assert.assertEquals(ITStorageTest.kmsKeyOneResourcePath, remoteBucket.getDefaultKmsKeyName());
            Bucket updatedBucket = remoteBucket.toBuilder().setDefaultKmsKeyName(ITStorageTest.kmsKeyTwoResourcePath).build().update();
            Assert.assertEquals(ITStorageTest.kmsKeyTwoResourcePath, updatedBucket.getDefaultKmsKeyName());
        } finally {
            RemoteStorageHelper.forceDelete(ITStorageTest.storage, bucketName, 5, TimeUnit.SECONDS);
        }
    }

    @Test
    public void testCreateBlob() {
        String blobName = "test-create-blob";
        BlobInfo blob = BlobInfo.newBuilder(ITStorageTest.BUCKET, blobName).build();
        Blob remoteBlob = ITStorageTest.storage.create(blob, ITStorageTest.BLOB_BYTE_CONTENT);
        Assert.assertNotNull(remoteBlob);
        Assert.assertEquals(blob.getBucket(), remoteBlob.getBucket());
        Assert.assertEquals(blob.getName(), remoteBlob.getName());
        byte[] readBytes = ITStorageTest.storage.readAllBytes(ITStorageTest.BUCKET, blobName);
        Assert.assertArrayEquals(ITStorageTest.BLOB_BYTE_CONTENT, readBytes);
        Assert.assertTrue(remoteBlob.delete());
    }

    @Test
    public void testCreateBlobMd5Crc32cFromHexString() {
        String blobName = "test-create-blob-md5-crc32c-from-hex-string";
        BlobInfo blob = BlobInfo.newBuilder(ITStorageTest.BUCKET, blobName).setContentType(ITStorageTest.CONTENT_TYPE).setMd5FromHexString("3b54781b51c94835084898e821899585").setCrc32cFromHexString("f4ddc43d").build();
        Blob remoteBlob = ITStorageTest.storage.create(blob, ITStorageTest.BLOB_BYTE_CONTENT);
        Assert.assertNotNull(remoteBlob);
        Assert.assertEquals(blob.getBucket(), remoteBlob.getBucket());
        Assert.assertEquals(blob.getName(), remoteBlob.getName());
        Assert.assertEquals(blob.getMd5ToHexString(), remoteBlob.getMd5ToHexString());
        Assert.assertEquals(blob.getCrc32cToHexString(), remoteBlob.getCrc32cToHexString());
        byte[] readBytes = ITStorageTest.storage.readAllBytes(ITStorageTest.BUCKET, blobName);
        Assert.assertArrayEquals(ITStorageTest.BLOB_BYTE_CONTENT, readBytes);
        Assert.assertTrue(remoteBlob.delete());
    }

    @Test
    public void testCreateBlobWithEncryptionKey() {
        String blobName = "test-create-with-customer-key-blob";
        BlobInfo blob = BlobInfo.newBuilder(ITStorageTest.BUCKET, blobName).build();
        Blob remoteBlob = ITStorageTest.storage.create(blob, ITStorageTest.BLOB_BYTE_CONTENT, BlobTargetOption.encryptionKey(ITStorageTest.KEY));
        Assert.assertNotNull(remoteBlob);
        Assert.assertEquals(blob.getBucket(), remoteBlob.getBucket());
        Assert.assertEquals(blob.getName(), remoteBlob.getName());
        byte[] readBytes = ITStorageTest.storage.readAllBytes(ITStorageTest.BUCKET, blobName, BlobSourceOption.decryptionKey(ITStorageTest.BASE64_KEY));
        Assert.assertArrayEquals(ITStorageTest.BLOB_BYTE_CONTENT, readBytes);
    }

    @Test
    public void testCreateBlobWithKmsKeyName() {
        String blobName = "test-create-with-kms-key-name-blob";
        BlobInfo blob = BlobInfo.newBuilder(ITStorageTest.BUCKET, blobName).build();
        Blob remoteBlob = ITStorageTest.storage.create(blob, ITStorageTest.BLOB_BYTE_CONTENT, BlobTargetOption.kmsKeyName(ITStorageTest.kmsKeyOneResourcePath));
        Assert.assertNotNull(remoteBlob);
        Assert.assertEquals(blob.getBucket(), remoteBlob.getBucket());
        Assert.assertEquals(blob.getName(), remoteBlob.getName());
        Assert.assertNotNull(remoteBlob.getKmsKeyName());
        Assert.assertTrue(remoteBlob.getKmsKeyName().startsWith(ITStorageTest.kmsKeyOneResourcePath));
        byte[] readBytes = ITStorageTest.storage.readAllBytes(ITStorageTest.BUCKET, blobName);
        Assert.assertArrayEquals(ITStorageTest.BLOB_BYTE_CONTENT, readBytes);
    }

    @Test
    public void testCreateBlobWithKmsKeyNameAndCustomerSuppliedKey() {
        try {
            String blobName = "test-create-with-kms-key-name-blob";
            BlobInfo blob = BlobInfo.newBuilder(ITStorageTest.BUCKET, blobName).build();
            ITStorageTest.storage.create(blob, ITStorageTest.BLOB_BYTE_CONTENT, BlobTargetOption.encryptionKey(ITStorageTest.KEY), BlobTargetOption.kmsKeyName(ITStorageTest.kmsKeyOneResourcePath));
            Assert.fail("StorageException was expected");// can't supply both.

        } catch (StorageException ex) {
            // expected
        }
    }

    @Test
    public void testCreateBlobWithDefaultKmsKeyName() throws InterruptedException, ExecutionException {
        String bucketName = RemoteStorageHelper.generateBucketName();
        Bucket bucket = ITStorageTest.storage.create(BucketInfo.newBuilder(bucketName).setDefaultKmsKeyName(ITStorageTest.kmsKeyOneResourcePath).setLocation(ITStorageTest.KMS_KEY_RING_LOCATION).build());
        Assert.assertEquals(bucket.getDefaultKmsKeyName(), ITStorageTest.kmsKeyOneResourcePath);
        try {
            String blobName = "test-create-with-default-kms-key-name-blob";
            BlobInfo blob = BlobInfo.newBuilder(bucket, blobName).build();
            Blob remoteBlob = ITStorageTest.storage.create(blob, ITStorageTest.BLOB_BYTE_CONTENT);
            Assert.assertNotNull(remoteBlob);
            Assert.assertEquals(blob.getBucket(), remoteBlob.getBucket());
            Assert.assertEquals(blob.getName(), remoteBlob.getName());
            Assert.assertNotNull(remoteBlob.getKmsKeyName());
            Assert.assertTrue(remoteBlob.getKmsKeyName().startsWith(ITStorageTest.kmsKeyOneResourcePath));
            byte[] readBytes = ITStorageTest.storage.readAllBytes(bucketName, blobName);
            Assert.assertArrayEquals(ITStorageTest.BLOB_BYTE_CONTENT, readBytes);
        } finally {
            RemoteStorageHelper.forceDelete(ITStorageTest.storage, bucketName, 5, TimeUnit.SECONDS);
        }
    }

    @Test
    public void testCreateEmptyBlob() {
        String blobName = "test-create-empty-blob";
        BlobInfo blob = BlobInfo.newBuilder(ITStorageTest.BUCKET, blobName).build();
        Blob remoteBlob = ITStorageTest.storage.create(blob);
        Assert.assertNotNull(remoteBlob);
        Assert.assertEquals(blob.getBucket(), remoteBlob.getBucket());
        Assert.assertEquals(blob.getName(), remoteBlob.getName());
        byte[] readBytes = ITStorageTest.storage.readAllBytes(ITStorageTest.BUCKET, blobName);
        Assert.assertArrayEquals(new byte[0], readBytes);
    }

    @Test
    public void testCreateBlobStream() {
        String blobName = "test-create-blob-stream";
        BlobInfo blob = BlobInfo.newBuilder(ITStorageTest.BUCKET, blobName).setContentType(ITStorageTest.CONTENT_TYPE).build();
        ByteArrayInputStream stream = new ByteArrayInputStream(ITStorageTest.BLOB_STRING_CONTENT.getBytes(StandardCharsets.UTF_8));
        Blob remoteBlob = ITStorageTest.storage.create(blob, stream);
        Assert.assertNotNull(remoteBlob);
        Assert.assertEquals(blob.getBucket(), remoteBlob.getBucket());
        Assert.assertEquals(blob.getName(), remoteBlob.getName());
        Assert.assertEquals(blob.getContentType(), remoteBlob.getContentType());
        byte[] readBytes = ITStorageTest.storage.readAllBytes(ITStorageTest.BUCKET, blobName);
        Assert.assertEquals(ITStorageTest.BLOB_STRING_CONTENT, new String(readBytes, StandardCharsets.UTF_8));
    }

    @Test
    public void testCreateBlobFail() {
        String blobName = "test-create-blob-fail";
        BlobInfo blob = BlobInfo.newBuilder(ITStorageTest.BUCKET, blobName).build();
        Blob remoteBlob = ITStorageTest.storage.create(blob);
        Assert.assertNotNull(remoteBlob);
        BlobInfo wrongGenerationBlob = BlobInfo.newBuilder(ITStorageTest.BUCKET, blobName, (-1L)).build();
        try {
            ITStorageTest.storage.create(wrongGenerationBlob, ITStorageTest.BLOB_BYTE_CONTENT, BlobTargetOption.generationMatch());
            Assert.fail("StorageException was expected");
        } catch (StorageException ex) {
            // expected
        }
    }

    @Test
    public void testCreateBlobMd5Fail() {
        String blobName = "test-create-blob-md5-fail";
        BlobInfo blob = BlobInfo.newBuilder(ITStorageTest.BUCKET, blobName).setContentType(ITStorageTest.CONTENT_TYPE).setMd5("O1R4G1HJSDUISJjoIYmVhQ==").build();
        ByteArrayInputStream stream = new ByteArrayInputStream(ITStorageTest.BLOB_STRING_CONTENT.getBytes(StandardCharsets.UTF_8));
        try {
            ITStorageTest.storage.create(blob, stream, BlobWriteOption.md5Match());
            Assert.fail("StorageException was expected");
        } catch (StorageException ex) {
            // expected
        }
    }

    @Test
    public void testGetBlobEmptySelectedFields() {
        String blobName = "test-get-empty-selected-fields-blob";
        BlobInfo blob = BlobInfo.newBuilder(ITStorageTest.BUCKET, blobName).setContentType(ITStorageTest.CONTENT_TYPE).build();
        Assert.assertNotNull(ITStorageTest.storage.create(blob));
        Blob remoteBlob = ITStorageTest.storage.get(blob.getBlobId(), BlobGetOption.fields());
        Assert.assertEquals(blob.getBlobId(), remoteBlob.getBlobId());
        Assert.assertNull(remoteBlob.getContentType());
    }

    @Test
    public void testGetBlobSelectedFields() {
        String blobName = "test-get-selected-fields-blob";
        BlobInfo blob = BlobInfo.newBuilder(ITStorageTest.BUCKET, blobName).setContentType(ITStorageTest.CONTENT_TYPE).setMetadata(ImmutableMap.of("k", "v")).build();
        Assert.assertNotNull(ITStorageTest.storage.create(blob));
        Blob remoteBlob = ITStorageTest.storage.get(blob.getBlobId(), BlobGetOption.fields(METADATA));
        Assert.assertEquals(blob.getBlobId(), remoteBlob.getBlobId());
        Assert.assertEquals(ImmutableMap.of("k", "v"), remoteBlob.getMetadata());
        Assert.assertNull(remoteBlob.getContentType());
    }

    @Test
    public void testGetBlobKmsKeyNameField() {
        String blobName = "test-get-selected-kms-key-name-field-blob";
        BlobInfo blob = BlobInfo.newBuilder(ITStorageTest.BUCKET, blobName).setContentType(ITStorageTest.CONTENT_TYPE).build();
        Assert.assertNotNull(ITStorageTest.storage.create(blob, BlobTargetOption.kmsKeyName(ITStorageTest.kmsKeyOneResourcePath)));
        Blob remoteBlob = ITStorageTest.storage.get(blob.getBlobId(), BlobGetOption.fields(KMS_KEY_NAME));
        Assert.assertEquals(blob.getBlobId(), remoteBlob.getBlobId());
        Assert.assertTrue(remoteBlob.getKmsKeyName().startsWith(ITStorageTest.kmsKeyOneResourcePath));
        Assert.assertNull(remoteBlob.getContentType());
    }

    @Test
    public void testGetBlobAllSelectedFields() {
        String blobName = "test-get-all-selected-fields-blob";
        BlobInfo blob = BlobInfo.newBuilder(ITStorageTest.BUCKET, blobName).setContentType(ITStorageTest.CONTENT_TYPE).setMetadata(ImmutableMap.of("k", "v")).build();
        Assert.assertNotNull(ITStorageTest.storage.create(blob));
        Blob remoteBlob = ITStorageTest.storage.get(blob.getBlobId(), BlobGetOption.fields(BlobField.values()));
        Assert.assertEquals(blob.getBucket(), remoteBlob.getBucket());
        Assert.assertEquals(blob.getName(), remoteBlob.getName());
        Assert.assertEquals(ImmutableMap.of("k", "v"), remoteBlob.getMetadata());
        Assert.assertNotNull(remoteBlob.getGeneratedId());
        Assert.assertNotNull(remoteBlob.getSelfLink());
    }

    @Test
    public void testGetBlobFail() {
        String blobName = "test-get-blob-fail";
        BlobInfo blob = BlobInfo.newBuilder(ITStorageTest.BUCKET, blobName).build();
        Blob remoteBlob = ITStorageTest.storage.create(blob);
        Assert.assertNotNull(remoteBlob);
        BlobId wrongGenerationBlob = BlobId.of(ITStorageTest.BUCKET, blobName);
        try {
            ITStorageTest.storage.get(wrongGenerationBlob, BlobGetOption.generationMatch((-1)));
            Assert.fail("StorageException was expected");
        } catch (StorageException ex) {
            // expected
        }
    }

    @Test
    public void testGetBlobFailNonExistingGeneration() {
        String blobName = "test-get-blob-fail-non-existing-generation";
        BlobInfo blob = BlobInfo.newBuilder(ITStorageTest.BUCKET, blobName).build();
        Blob remoteBlob = ITStorageTest.storage.create(blob);
        Assert.assertNotNull(remoteBlob);
        BlobId wrongGenerationBlob = BlobId.of(ITStorageTest.BUCKET, blobName, (-1L));
        try {
            Assert.assertNull(ITStorageTest.storage.get(wrongGenerationBlob));
            Assert.fail("Expected an 'Invalid argument' exception");
        } catch (StorageException e) {
            assertThat(e.getMessage()).contains("Invalid argument");
        }
    }

    @Test(timeout = 5000)
    public void testListBlobsSelectedFields() throws InterruptedException {
        String[] blobNames = new String[]{ "test-list-blobs-selected-fields-blob1", "test-list-blobs-selected-fields-blob2" };
        ImmutableMap<String, String> metadata = ImmutableMap.of("k", "v");
        BlobInfo blob1 = BlobInfo.newBuilder(ITStorageTest.BUCKET, blobNames[0]).setContentType(ITStorageTest.CONTENT_TYPE).setMetadata(metadata).build();
        BlobInfo blob2 = BlobInfo.newBuilder(ITStorageTest.BUCKET, blobNames[1]).setContentType(ITStorageTest.CONTENT_TYPE).setMetadata(metadata).build();
        Blob remoteBlob1 = ITStorageTest.storage.create(blob1);
        Blob remoteBlob2 = ITStorageTest.storage.create(blob2);
        Assert.assertNotNull(remoteBlob1);
        Assert.assertNotNull(remoteBlob2);
        Page<Blob> page = ITStorageTest.storage.list(ITStorageTest.BUCKET, BlobListOption.prefix("test-list-blobs-selected-fields-blob"), BlobListOption.fields(METADATA));
        // Listing blobs is eventually consistent, we loop until the list is of the expected size. The
        // test fails if timeout is reached.
        while ((Iterators.size(page.iterateAll().iterator())) != 2) {
            Thread.sleep(500);
            page = ITStorageTest.storage.list(ITStorageTest.BUCKET, BlobListOption.prefix("test-list-blobs-selected-fields-blob"), BlobListOption.fields(METADATA));
        } 
        Set<String> blobSet = ImmutableSet.of(blobNames[0], blobNames[1]);
        Iterator<Blob> iterator = page.iterateAll().iterator();
        while (iterator.hasNext()) {
            Blob remoteBlob = iterator.next();
            Assert.assertEquals(ITStorageTest.BUCKET, remoteBlob.getBucket());
            Assert.assertTrue(blobSet.contains(remoteBlob.getName()));
            Assert.assertEquals(metadata, remoteBlob.getMetadata());
            Assert.assertNull(remoteBlob.getContentType());
        } 
    }

    @Test(timeout = 5000)
    public void testListBlobsKmsKeySelectedFields() throws InterruptedException {
        String[] blobNames = new String[]{ "test-list-blobs-selected-field-kms-key-name-blob1", "test-list-blobs-selected-field-kms-key-name-blob2" };
        BlobInfo blob1 = BlobInfo.newBuilder(ITStorageTest.BUCKET, blobNames[0]).setContentType(ITStorageTest.CONTENT_TYPE).build();
        BlobInfo blob2 = BlobInfo.newBuilder(ITStorageTest.BUCKET, blobNames[1]).setContentType(ITStorageTest.CONTENT_TYPE).build();
        Blob remoteBlob1 = ITStorageTest.storage.create(blob1, BlobTargetOption.kmsKeyName(ITStorageTest.kmsKeyOneResourcePath));
        Blob remoteBlob2 = ITStorageTest.storage.create(blob2, BlobTargetOption.kmsKeyName(ITStorageTest.kmsKeyOneResourcePath));
        Assert.assertNotNull(remoteBlob1);
        Assert.assertNotNull(remoteBlob2);
        Page<Blob> page = ITStorageTest.storage.list(ITStorageTest.BUCKET, BlobListOption.prefix("test-list-blobs-selected-field-kms-key-name-blob"), BlobListOption.fields(KMS_KEY_NAME));
        // Listing blobs is eventually consistent, we loop until the list is of the expected size. The
        // test fails if timeout is reached.
        while ((Iterators.size(page.iterateAll().iterator())) != 2) {
            Thread.sleep(500);
            page = ITStorageTest.storage.list(ITStorageTest.BUCKET, BlobListOption.prefix("test-list-blobs-selected-field-kms-key-name-blob"), BlobListOption.fields(KMS_KEY_NAME));
        } 
        Set<String> blobSet = ImmutableSet.of(blobNames[0], blobNames[1]);
        Iterator<Blob> iterator = page.iterateAll().iterator();
        while (iterator.hasNext()) {
            Blob remoteBlob = iterator.next();
            Assert.assertEquals(ITStorageTest.BUCKET, remoteBlob.getBucket());
            Assert.assertTrue(blobSet.contains(remoteBlob.getName()));
            Assert.assertTrue(remoteBlob.getKmsKeyName().startsWith(ITStorageTest.kmsKeyOneResourcePath));
            Assert.assertNull(remoteBlob.getContentType());
        } 
    }

    @Test(timeout = 5000)
    public void testListBlobsEmptySelectedFields() throws InterruptedException {
        String[] blobNames = new String[]{ "test-list-blobs-empty-selected-fields-blob1", "test-list-blobs-empty-selected-fields-blob2" };
        BlobInfo blob1 = BlobInfo.newBuilder(ITStorageTest.BUCKET, blobNames[0]).setContentType(ITStorageTest.CONTENT_TYPE).build();
        BlobInfo blob2 = BlobInfo.newBuilder(ITStorageTest.BUCKET, blobNames[1]).setContentType(ITStorageTest.CONTENT_TYPE).build();
        Blob remoteBlob1 = ITStorageTest.storage.create(blob1);
        Blob remoteBlob2 = ITStorageTest.storage.create(blob2);
        Assert.assertNotNull(remoteBlob1);
        Assert.assertNotNull(remoteBlob2);
        Page<Blob> page = ITStorageTest.storage.list(ITStorageTest.BUCKET, BlobListOption.prefix("test-list-blobs-empty-selected-fields-blob"), BlobListOption.fields());
        // Listing blobs is eventually consistent, we loop until the list is of the expected size. The
        // test fails if timeout is reached.
        while ((Iterators.size(page.iterateAll().iterator())) != 2) {
            Thread.sleep(500);
            page = ITStorageTest.storage.list(ITStorageTest.BUCKET, BlobListOption.prefix("test-list-blobs-empty-selected-fields-blob"), BlobListOption.fields());
        } 
        Set<String> blobSet = ImmutableSet.of(blobNames[0], blobNames[1]);
        Iterator<Blob> iterator = page.iterateAll().iterator();
        while (iterator.hasNext()) {
            Blob remoteBlob = iterator.next();
            Assert.assertEquals(ITStorageTest.BUCKET, remoteBlob.getBucket());
            Assert.assertTrue(blobSet.contains(remoteBlob.getName()));
            Assert.assertNull(remoteBlob.getContentType());
        } 
    }

    @Test(timeout = 7500)
    public void testListBlobRequesterPays() throws InterruptedException {
        BlobInfo blob1 = BlobInfo.newBuilder(ITStorageTest.BUCKET, "test-list-blobs-empty-selected-fields-blob1").setContentType(ITStorageTest.CONTENT_TYPE).build();
        Assert.assertNotNull(ITStorageTest.storage.create(blob1));
        // Test listing a Requester Pays bucket.
        Bucket remoteBucket = ITStorageTest.storage.get(ITStorageTest.BUCKET, BucketGetOption.fields(ID));
        Assert.assertNull(remoteBucket.requesterPays());
        remoteBucket = remoteBucket.toBuilder().setRequesterPays(true).build();
        Bucket updatedBucket = ITStorageTest.storage.update(remoteBucket);
        Assert.assertTrue(updatedBucket.requesterPays());
        try {
            ITStorageTest.storage.list(ITStorageTest.BUCKET, BlobListOption.prefix("test-list-blobs-empty-selected-fields-blob"), BlobListOption.fields(), BlobListOption.userProject("fakeBillingProjectId"));
            Assert.fail("Expected bad user project error.");
        } catch (StorageException e) {
            Assert.assertTrue(e.getMessage().contains("User project specified in the request is invalid"));
        }
        String projectId = ITStorageTest.remoteStorageHelper.getOptions().getProjectId();
        while (true) {
            Page<Blob> page = ITStorageTest.storage.list(ITStorageTest.BUCKET, BlobListOption.prefix("test-list-blobs-empty-selected-fields-blob"), BlobListOption.fields(), BlobListOption.userProject(projectId));
            List<Blob> blobs = Lists.newArrayList(page.iterateAll());
            // If the list is empty, maybe the blob isn't visible yet; wait and try again.
            // Otherwise, expect one blob, since we only put in one above.
            if (!(blobs.isEmpty())) {
                assertThat(blobs).hasSize(1);
                break;
            }
            Thread.sleep(500);
        } 
    }

    @Test(timeout = 15000)
    public void testListBlobsVersioned() throws InterruptedException, ExecutionException {
        String bucketName = RemoteStorageHelper.generateBucketName();
        Bucket bucket = ITStorageTest.storage.create(BucketInfo.newBuilder(bucketName).setVersioningEnabled(true).build());
        try {
            String[] blobNames = new String[]{ "test-list-blobs-versioned-blob1", "test-list-blobs-versioned-blob2" };
            BlobInfo blob1 = BlobInfo.newBuilder(bucket, blobNames[0]).setContentType(ITStorageTest.CONTENT_TYPE).build();
            BlobInfo blob2 = BlobInfo.newBuilder(bucket, blobNames[1]).setContentType(ITStorageTest.CONTENT_TYPE).build();
            Blob remoteBlob1 = ITStorageTest.storage.create(blob1);
            Blob remoteBlob2 = ITStorageTest.storage.create(blob2);
            Blob remoteBlob3 = ITStorageTest.storage.create(blob2);
            Assert.assertNotNull(remoteBlob1);
            Assert.assertNotNull(remoteBlob2);
            Assert.assertNotNull(remoteBlob3);
            Page<Blob> page = ITStorageTest.storage.list(bucketName, BlobListOption.prefix("test-list-blobs-versioned-blob"), BlobListOption.versions(true));
            // Listing blobs is eventually consistent, we loop until the list is of the expected size. The
            // test fails if timeout is reached.
            while ((Iterators.size(page.iterateAll().iterator())) != 3) {
                Thread.sleep(500);
                page = ITStorageTest.storage.list(bucketName, BlobListOption.prefix("test-list-blobs-versioned-blob"), BlobListOption.versions(true));
            } 
            Set<String> blobSet = ImmutableSet.of(blobNames[0], blobNames[1]);
            Iterator<Blob> iterator = page.iterateAll().iterator();
            while (iterator.hasNext()) {
                Blob remoteBlob = iterator.next();
                Assert.assertEquals(bucketName, remoteBlob.getBucket());
                Assert.assertTrue(blobSet.contains(remoteBlob.getName()));
                Assert.assertNotNull(remoteBlob.getGeneration());
            } 
        } finally {
            RemoteStorageHelper.forceDelete(ITStorageTest.storage, bucketName, 5, TimeUnit.SECONDS);
        }
    }

    @Test(timeout = 5000)
    public void testListBlobsCurrentDirectory() throws InterruptedException {
        String directoryName = "test-list-blobs-current-directory/";
        String subdirectoryName = "subdirectory/";
        String[] blobNames = new String[]{ (directoryName + subdirectoryName) + "blob1", directoryName + "blob2" };
        BlobInfo blob1 = BlobInfo.newBuilder(ITStorageTest.BUCKET, blobNames[0]).setContentType(ITStorageTest.CONTENT_TYPE).build();
        BlobInfo blob2 = BlobInfo.newBuilder(ITStorageTest.BUCKET, blobNames[1]).setContentType(ITStorageTest.CONTENT_TYPE).build();
        Blob remoteBlob1 = ITStorageTest.storage.create(blob1, ITStorageTest.BLOB_BYTE_CONTENT);
        Blob remoteBlob2 = ITStorageTest.storage.create(blob2, ITStorageTest.BLOB_BYTE_CONTENT);
        Assert.assertNotNull(remoteBlob1);
        Assert.assertNotNull(remoteBlob2);
        Page<Blob> page = ITStorageTest.storage.list(ITStorageTest.BUCKET, BlobListOption.prefix("test-list-blobs-current-directory/"), BlobListOption.currentDirectory());
        // Listing blobs is eventually consistent, we loop until the list is of the expected size. The
        // test fails if timeout is reached.
        while ((Iterators.size(page.iterateAll().iterator())) != 2) {
            Thread.sleep(500);
            page = ITStorageTest.storage.list(ITStorageTest.BUCKET, BlobListOption.prefix("test-list-blobs-current-directory/"), BlobListOption.currentDirectory());
        } 
        Iterator<Blob> iterator = page.iterateAll().iterator();
        while (iterator.hasNext()) {
            Blob remoteBlob = iterator.next();
            Assert.assertEquals(ITStorageTest.BUCKET, remoteBlob.getBucket());
            if (remoteBlob.getName().equals(blobNames[1])) {
                Assert.assertEquals(ITStorageTest.CONTENT_TYPE, remoteBlob.getContentType());
                Assert.assertEquals(ITStorageTest.BLOB_BYTE_CONTENT.length, ((long) (remoteBlob.getSize())));
                Assert.assertFalse(remoteBlob.isDirectory());
            } else
                if (remoteBlob.getName().equals((directoryName + subdirectoryName))) {
                    Assert.assertEquals(0L, ((long) (remoteBlob.getSize())));
                    Assert.assertTrue(remoteBlob.isDirectory());
                } else {
                    Assert.fail(("Unexpected blob with name " + (remoteBlob.getName())));
                }

        } 
    }

    @Test
    public void testUpdateBlob() {
        String blobName = "test-update-blob";
        BlobInfo blob = BlobInfo.newBuilder(ITStorageTest.BUCKET, blobName).build();
        Blob remoteBlob = ITStorageTest.storage.create(blob);
        Assert.assertNotNull(remoteBlob);
        Blob updatedBlob = remoteBlob.toBuilder().setContentType(ITStorageTest.CONTENT_TYPE).build().update();
        Assert.assertNotNull(updatedBlob);
        Assert.assertEquals(blob.getName(), updatedBlob.getName());
        Assert.assertEquals(blob.getBucket(), updatedBlob.getBucket());
        Assert.assertEquals(ITStorageTest.CONTENT_TYPE, updatedBlob.getContentType());
    }

    @Test
    public void testUpdateBlobReplaceMetadata() {
        String blobName = "test-update-blob-replace-metadata";
        ImmutableMap<String, String> metadata = ImmutableMap.of("k1", "a");
        ImmutableMap<String, String> newMetadata = ImmutableMap.of("k2", "b");
        BlobInfo blob = BlobInfo.newBuilder(ITStorageTest.BUCKET, blobName).setContentType(ITStorageTest.CONTENT_TYPE).setMetadata(metadata).build();
        Blob remoteBlob = ITStorageTest.storage.create(blob);
        Assert.assertNotNull(remoteBlob);
        Blob updatedBlob = remoteBlob.toBuilder().setMetadata(null).build().update();
        Assert.assertNotNull(updatedBlob);
        Assert.assertNull(updatedBlob.getMetadata());
        updatedBlob = remoteBlob.toBuilder().setMetadata(newMetadata).build().update();
        Assert.assertEquals(blob.getName(), updatedBlob.getName());
        Assert.assertEquals(blob.getBucket(), updatedBlob.getBucket());
        Assert.assertEquals(newMetadata, updatedBlob.getMetadata());
    }

    @Test
    public void testUpdateBlobMergeMetadata() {
        String blobName = "test-update-blob-merge-metadata";
        ImmutableMap<String, String> metadata = ImmutableMap.of("k1", "a");
        ImmutableMap<String, String> newMetadata = ImmutableMap.of("k2", "b");
        ImmutableMap<String, String> expectedMetadata = ImmutableMap.of("k1", "a", "k2", "b");
        BlobInfo blob = BlobInfo.newBuilder(ITStorageTest.BUCKET, blobName).setContentType(ITStorageTest.CONTENT_TYPE).setMetadata(metadata).build();
        Blob remoteBlob = ITStorageTest.storage.create(blob);
        Assert.assertNotNull(remoteBlob);
        Blob updatedBlob = remoteBlob.toBuilder().setMetadata(newMetadata).build().update();
        Assert.assertNotNull(updatedBlob);
        Assert.assertEquals(blob.getName(), updatedBlob.getName());
        Assert.assertEquals(blob.getBucket(), updatedBlob.getBucket());
        Assert.assertEquals(expectedMetadata, updatedBlob.getMetadata());
    }

    @Test
    public void testUpdateBlobUnsetMetadata() {
        String blobName = "test-update-blob-unset-metadata";
        ImmutableMap<String, String> metadata = ImmutableMap.of("k1", "a", "k2", "b");
        Map<String, String> newMetadata = new HashMap<>();
        newMetadata.put("k1", "a");
        newMetadata.put("k2", null);
        ImmutableMap<String, String> expectedMetadata = ImmutableMap.of("k1", "a");
        BlobInfo blob = BlobInfo.newBuilder(ITStorageTest.BUCKET, blobName).setContentType(ITStorageTest.CONTENT_TYPE).setMetadata(metadata).build();
        Blob remoteBlob = ITStorageTest.storage.create(blob);
        Assert.assertNotNull(remoteBlob);
        Blob updatedBlob = remoteBlob.toBuilder().setMetadata(newMetadata).build().update();
        Assert.assertNotNull(updatedBlob);
        Assert.assertEquals(blob.getName(), updatedBlob.getName());
        Assert.assertEquals(blob.getBucket(), updatedBlob.getBucket());
        Assert.assertEquals(expectedMetadata, updatedBlob.getMetadata());
    }

    @Test
    public void testUpdateBlobFail() {
        String blobName = "test-update-blob-fail";
        BlobInfo blob = BlobInfo.newBuilder(ITStorageTest.BUCKET, blobName).build();
        Blob remoteBlob = ITStorageTest.storage.create(blob);
        Assert.assertNotNull(remoteBlob);
        BlobInfo wrongGenerationBlob = BlobInfo.newBuilder(ITStorageTest.BUCKET, blobName, (-1L)).setContentType(ITStorageTest.CONTENT_TYPE).build();
        try {
            ITStorageTest.storage.update(wrongGenerationBlob, BlobTargetOption.generationMatch());
            Assert.fail("StorageException was expected");
        } catch (StorageException ex) {
            // expected
        }
    }

    @Test
    public void testDeleteNonExistingBlob() {
        String blobName = "test-delete-non-existing-blob";
        Assert.assertFalse(ITStorageTest.storage.delete(ITStorageTest.BUCKET, blobName));
    }

    @Test
    public void testDeleteBlobNonExistingGeneration() {
        String blobName = "test-delete-blob-non-existing-generation";
        BlobInfo blob = BlobInfo.newBuilder(ITStorageTest.BUCKET, blobName).build();
        Assert.assertNotNull(ITStorageTest.storage.create(blob));
        try {
            Assert.assertFalse(ITStorageTest.storage.delete(BlobId.of(ITStorageTest.BUCKET, blobName, (-1L))));
            Assert.fail("Expected an 'Invalid argument' exception");
        } catch (StorageException e) {
            assertThat(e.getMessage()).contains("Invalid argument");
        }
    }

    @Test
    public void testDeleteBlobFail() {
        String blobName = "test-delete-blob-fail";
        BlobInfo blob = BlobInfo.newBuilder(ITStorageTest.BUCKET, blobName).build();
        Blob remoteBlob = ITStorageTest.storage.create(blob);
        Assert.assertNotNull(remoteBlob);
        try {
            ITStorageTest.storage.delete(ITStorageTest.BUCKET, blob.getName(), BlobSourceOption.generationMatch((-1L)));
            Assert.fail("StorageException was expected");
        } catch (StorageException ex) {
            // expected
        }
        Assert.assertTrue(remoteBlob.delete());
    }

    @Test
    public void testComposeBlob() {
        String sourceBlobName1 = "test-compose-blob-source-1";
        String sourceBlobName2 = "test-compose-blob-source-2";
        BlobInfo sourceBlob1 = BlobInfo.newBuilder(ITStorageTest.BUCKET, sourceBlobName1).build();
        BlobInfo sourceBlob2 = BlobInfo.newBuilder(ITStorageTest.BUCKET, sourceBlobName2).build();
        Blob remoteSourceBlob1 = ITStorageTest.storage.create(sourceBlob1, ITStorageTest.BLOB_BYTE_CONTENT);
        Blob remoteSourceBlob2 = ITStorageTest.storage.create(sourceBlob2, ITStorageTest.BLOB_BYTE_CONTENT);
        Assert.assertNotNull(remoteSourceBlob1);
        Assert.assertNotNull(remoteSourceBlob2);
        String targetBlobName = "test-compose-blob-target";
        BlobInfo targetBlob = BlobInfo.newBuilder(ITStorageTest.BUCKET, targetBlobName).build();
        Storage.ComposeRequest req = ComposeRequest.of(ImmutableList.of(sourceBlobName1, sourceBlobName2), targetBlob);
        Blob remoteTargetBlob = ITStorageTest.storage.compose(req);
        Assert.assertNotNull(remoteTargetBlob);
        Assert.assertEquals(targetBlob.getName(), remoteTargetBlob.getName());
        Assert.assertEquals(targetBlob.getBucket(), remoteTargetBlob.getBucket());
        Assert.assertNull(remoteTargetBlob.getContentType());
        byte[] readBytes = ITStorageTest.storage.readAllBytes(ITStorageTest.BUCKET, targetBlobName);
        byte[] composedBytes = Arrays.copyOf(ITStorageTest.BLOB_BYTE_CONTENT, ((ITStorageTest.BLOB_BYTE_CONTENT.length) * 2));
        System.arraycopy(ITStorageTest.BLOB_BYTE_CONTENT, 0, composedBytes, ITStorageTest.BLOB_BYTE_CONTENT.length, ITStorageTest.BLOB_BYTE_CONTENT.length);
        Assert.assertArrayEquals(composedBytes, readBytes);
    }

    @Test
    public void testComposeBlobWithContentType() {
        String sourceBlobName1 = "test-compose-blob-with-content-type-source-1";
        String sourceBlobName2 = "test-compose-blob-with-content-type-source-2";
        BlobInfo sourceBlob1 = BlobInfo.newBuilder(ITStorageTest.BUCKET, sourceBlobName1).build();
        BlobInfo sourceBlob2 = BlobInfo.newBuilder(ITStorageTest.BUCKET, sourceBlobName2).build();
        Blob remoteSourceBlob1 = ITStorageTest.storage.create(sourceBlob1, ITStorageTest.BLOB_BYTE_CONTENT);
        Blob remoteSourceBlob2 = ITStorageTest.storage.create(sourceBlob2, ITStorageTest.BLOB_BYTE_CONTENT);
        Assert.assertNotNull(remoteSourceBlob1);
        Assert.assertNotNull(remoteSourceBlob2);
        String targetBlobName = "test-compose-blob-with-content-type-target";
        BlobInfo targetBlob = BlobInfo.newBuilder(ITStorageTest.BUCKET, targetBlobName).setContentType(ITStorageTest.CONTENT_TYPE).build();
        Storage.ComposeRequest req = ComposeRequest.of(ImmutableList.of(sourceBlobName1, sourceBlobName2), targetBlob);
        Blob remoteTargetBlob = ITStorageTest.storage.compose(req);
        Assert.assertNotNull(remoteTargetBlob);
        Assert.assertEquals(targetBlob.getName(), remoteTargetBlob.getName());
        Assert.assertEquals(targetBlob.getBucket(), remoteTargetBlob.getBucket());
        Assert.assertEquals(ITStorageTest.CONTENT_TYPE, remoteTargetBlob.getContentType());
        byte[] readBytes = ITStorageTest.storage.readAllBytes(ITStorageTest.BUCKET, targetBlobName);
        byte[] composedBytes = Arrays.copyOf(ITStorageTest.BLOB_BYTE_CONTENT, ((ITStorageTest.BLOB_BYTE_CONTENT.length) * 2));
        System.arraycopy(ITStorageTest.BLOB_BYTE_CONTENT, 0, composedBytes, ITStorageTest.BLOB_BYTE_CONTENT.length, ITStorageTest.BLOB_BYTE_CONTENT.length);
        Assert.assertArrayEquals(composedBytes, readBytes);
    }

    @Test
    public void testComposeBlobFail() {
        String sourceBlobName1 = "test-compose-blob-fail-source-1";
        String sourceBlobName2 = "test-compose-blob-fail-source-2";
        BlobInfo sourceBlob1 = BlobInfo.newBuilder(ITStorageTest.BUCKET, sourceBlobName1).build();
        BlobInfo sourceBlob2 = BlobInfo.newBuilder(ITStorageTest.BUCKET, sourceBlobName2).build();
        Blob remoteSourceBlob1 = ITStorageTest.storage.create(sourceBlob1);
        Blob remoteSourceBlob2 = ITStorageTest.storage.create(sourceBlob2);
        Assert.assertNotNull(remoteSourceBlob1);
        Assert.assertNotNull(remoteSourceBlob2);
        String targetBlobName = "test-compose-blob-fail-target";
        BlobInfo targetBlob = BlobInfo.newBuilder(ITStorageTest.BUCKET, targetBlobName).build();
        Storage.ComposeRequest req = ComposeRequest.newBuilder().addSource(sourceBlobName1, (-1L)).addSource(sourceBlobName2, (-1L)).setTarget(targetBlob).build();
        try {
            ITStorageTest.storage.compose(req);
            Assert.fail("StorageException was expected");
        } catch (StorageException ex) {
            // expected
        }
    }

    @Test
    public void testCopyBlob() {
        String sourceBlobName = "test-copy-blob-source";
        BlobId source = BlobId.of(ITStorageTest.BUCKET, sourceBlobName);
        ImmutableMap<String, String> metadata = ImmutableMap.of("k", "v");
        BlobInfo blob = BlobInfo.newBuilder(source).setContentType(ITStorageTest.CONTENT_TYPE).setMetadata(metadata).build();
        Blob remoteBlob = ITStorageTest.storage.create(blob, ITStorageTest.BLOB_BYTE_CONTENT);
        Assert.assertNotNull(remoteBlob);
        String targetBlobName = "test-copy-blob-target";
        Storage.CopyRequest req = CopyRequest.of(source, BlobId.of(ITStorageTest.BUCKET, targetBlobName));
        CopyWriter copyWriter = ITStorageTest.storage.copy(req);
        Assert.assertEquals(ITStorageTest.BUCKET, copyWriter.getResult().getBucket());
        Assert.assertEquals(targetBlobName, copyWriter.getResult().getName());
        Assert.assertEquals(ITStorageTest.CONTENT_TYPE, copyWriter.getResult().getContentType());
        Assert.assertEquals(metadata, copyWriter.getResult().getMetadata());
        Assert.assertTrue(copyWriter.isDone());
        Assert.assertTrue(remoteBlob.delete());
        Assert.assertTrue(ITStorageTest.storage.delete(ITStorageTest.BUCKET, targetBlobName));
    }

    @Test
    public void testCopyBlobWithPredefinedAcl() {
        String sourceBlobName = "test-copy-blob-source";
        BlobId source = BlobId.of(ITStorageTest.BUCKET, sourceBlobName);
        ImmutableMap<String, String> metadata = ImmutableMap.of("k", "v");
        BlobInfo blob = BlobInfo.newBuilder(source).setContentType(ITStorageTest.CONTENT_TYPE).setMetadata(metadata).build();
        Blob remoteBlob = ITStorageTest.storage.create(blob, ITStorageTest.BLOB_BYTE_CONTENT);
        Assert.assertNotNull(remoteBlob);
        String targetBlobName = "test-copy-blob-target";
        Storage.CopyRequest req = CopyRequest.newBuilder().setSource(source).setTarget(BlobId.of(ITStorageTest.BUCKET, targetBlobName), BlobTargetOption.predefinedAcl(PUBLIC_READ)).build();
        CopyWriter copyWriter = ITStorageTest.storage.copy(req);
        Assert.assertEquals(ITStorageTest.BUCKET, copyWriter.getResult().getBucket());
        Assert.assertEquals(targetBlobName, copyWriter.getResult().getName());
        Assert.assertEquals(ITStorageTest.CONTENT_TYPE, copyWriter.getResult().getContentType());
        Assert.assertEquals(metadata, copyWriter.getResult().getMetadata());
        Assert.assertNotNull(copyWriter.getResult().getAcl(User.ofAllUsers()));
        Assert.assertTrue(copyWriter.isDone());
        Assert.assertTrue(remoteBlob.delete());
        Assert.assertTrue(ITStorageTest.storage.delete(ITStorageTest.BUCKET, targetBlobName));
    }

    @Test
    public void testCopyBlobWithEncryptionKeys() {
        String sourceBlobName = "test-copy-blob-encryption-key-source";
        BlobId source = BlobId.of(ITStorageTest.BUCKET, sourceBlobName);
        ImmutableMap<String, String> metadata = ImmutableMap.of("k", "v");
        Blob remoteBlob = ITStorageTest.storage.create(BlobInfo.newBuilder(source).build(), ITStorageTest.BLOB_BYTE_CONTENT, BlobTargetOption.encryptionKey(ITStorageTest.KEY));
        Assert.assertNotNull(remoteBlob);
        String targetBlobName = "test-copy-blob-encryption-key-target";
        BlobInfo target = BlobInfo.newBuilder(ITStorageTest.BUCKET, targetBlobName).setContentType(ITStorageTest.CONTENT_TYPE).setMetadata(metadata).build();
        Storage.CopyRequest req = CopyRequest.newBuilder().setSource(source).setTarget(target, BlobTargetOption.encryptionKey(ITStorageTest.OTHER_BASE64_KEY)).setSourceOptions(BlobSourceOption.decryptionKey(ITStorageTest.BASE64_KEY)).build();
        CopyWriter copyWriter = ITStorageTest.storage.copy(req);
        Assert.assertEquals(ITStorageTest.BUCKET, copyWriter.getResult().getBucket());
        Assert.assertEquals(targetBlobName, copyWriter.getResult().getName());
        Assert.assertEquals(ITStorageTest.CONTENT_TYPE, copyWriter.getResult().getContentType());
        Assert.assertArrayEquals(ITStorageTest.BLOB_BYTE_CONTENT, copyWriter.getResult().getContent(Blob.BlobSourceOption.decryptionKey(ITStorageTest.OTHER_BASE64_KEY)));
        Assert.assertEquals(metadata, copyWriter.getResult().getMetadata());
        Assert.assertTrue(copyWriter.isDone());
        req = CopyRequest.newBuilder().setSource(source).setTarget(target).setSourceOptions(BlobSourceOption.decryptionKey(ITStorageTest.BASE64_KEY)).build();
        copyWriter = ITStorageTest.storage.copy(req);
        Assert.assertEquals(ITStorageTest.BUCKET, copyWriter.getResult().getBucket());
        Assert.assertEquals(targetBlobName, copyWriter.getResult().getName());
        Assert.assertEquals(ITStorageTest.CONTENT_TYPE, copyWriter.getResult().getContentType());
        Assert.assertArrayEquals(ITStorageTest.BLOB_BYTE_CONTENT, copyWriter.getResult().getContent());
        Assert.assertEquals(metadata, copyWriter.getResult().getMetadata());
        Assert.assertTrue(copyWriter.isDone());
        Assert.assertTrue(remoteBlob.delete());
        Assert.assertTrue(ITStorageTest.storage.delete(ITStorageTest.BUCKET, targetBlobName));
    }

    @Test
    public void testRotateFromCustomerEncryptionToKmsKey() {
        String sourceBlobName = "test-copy-blob-encryption-key-source";
        BlobId source = BlobId.of(ITStorageTest.BUCKET, sourceBlobName);
        ImmutableMap<String, String> metadata = ImmutableMap.of("k", "v");
        Blob remoteBlob = ITStorageTest.storage.create(BlobInfo.newBuilder(source).build(), ITStorageTest.BLOB_BYTE_CONTENT, BlobTargetOption.encryptionKey(ITStorageTest.KEY));
        Assert.assertNotNull(remoteBlob);
        String targetBlobName = "test-copy-blob-kms-key-target";
        BlobInfo target = BlobInfo.newBuilder(ITStorageTest.BUCKET, targetBlobName).setContentType(ITStorageTest.CONTENT_TYPE).setMetadata(metadata).build();
        Storage.CopyRequest req = CopyRequest.newBuilder().setSource(source).setSourceOptions(BlobSourceOption.decryptionKey(ITStorageTest.BASE64_KEY)).setTarget(target, BlobTargetOption.kmsKeyName(ITStorageTest.kmsKeyOneResourcePath)).build();
        CopyWriter copyWriter = ITStorageTest.storage.copy(req);
        Assert.assertEquals(ITStorageTest.BUCKET, copyWriter.getResult().getBucket());
        Assert.assertEquals(targetBlobName, copyWriter.getResult().getName());
        Assert.assertEquals(ITStorageTest.CONTENT_TYPE, copyWriter.getResult().getContentType());
        Assert.assertNotNull(copyWriter.getResult().getKmsKeyName());
        Assert.assertTrue(copyWriter.getResult().getKmsKeyName().startsWith(ITStorageTest.kmsKeyOneResourcePath));
        Assert.assertArrayEquals(ITStorageTest.BLOB_BYTE_CONTENT, copyWriter.getResult().getContent());
        Assert.assertEquals(metadata, copyWriter.getResult().getMetadata());
        Assert.assertTrue(copyWriter.isDone());
        Assert.assertTrue(ITStorageTest.storage.delete(ITStorageTest.BUCKET, targetBlobName));
    }

    @Test
    public void testCopyBlobUpdateMetadata() {
        String sourceBlobName = "test-copy-blob-update-metadata-source";
        BlobId source = BlobId.of(ITStorageTest.BUCKET, sourceBlobName);
        Blob remoteSourceBlob = ITStorageTest.storage.create(BlobInfo.newBuilder(source).build(), ITStorageTest.BLOB_BYTE_CONTENT);
        Assert.assertNotNull(remoteSourceBlob);
        String targetBlobName = "test-copy-blob-update-metadata-target";
        ImmutableMap<String, String> metadata = ImmutableMap.of("k", "v");
        BlobInfo target = BlobInfo.newBuilder(ITStorageTest.BUCKET, targetBlobName).setContentType(ITStorageTest.CONTENT_TYPE).setMetadata(metadata).build();
        Storage.CopyRequest req = CopyRequest.of(source, target);
        CopyWriter copyWriter = ITStorageTest.storage.copy(req);
        Assert.assertEquals(ITStorageTest.BUCKET, copyWriter.getResult().getBucket());
        Assert.assertEquals(targetBlobName, copyWriter.getResult().getName());
        Assert.assertEquals(ITStorageTest.CONTENT_TYPE, copyWriter.getResult().getContentType());
        Assert.assertEquals(metadata, copyWriter.getResult().getMetadata());
        Assert.assertTrue(copyWriter.isDone());
        Assert.assertTrue(remoteSourceBlob.delete());
        Assert.assertTrue(ITStorageTest.storage.delete(ITStorageTest.BUCKET, targetBlobName));
    }

    @Test
    public void testCopyBlobNoContentType() {
        String sourceBlobName = "test-copy-blob-no-content-type-source";
        BlobId source = BlobId.of(ITStorageTest.BUCKET, sourceBlobName);
        Blob remoteSourceBlob = ITStorageTest.storage.create(BlobInfo.newBuilder(source).build(), ITStorageTest.BLOB_BYTE_CONTENT);
        Assert.assertNotNull(remoteSourceBlob);
        String targetBlobName = "test-copy-blob-no-content-type-target";
        ImmutableMap<String, String> metadata = ImmutableMap.of("k", "v");
        BlobInfo target = BlobInfo.newBuilder(ITStorageTest.BUCKET, targetBlobName).setMetadata(metadata).build();
        Storage.CopyRequest req = CopyRequest.of(source, target);
        CopyWriter copyWriter = ITStorageTest.storage.copy(req);
        Assert.assertEquals(ITStorageTest.BUCKET, copyWriter.getResult().getBucket());
        Assert.assertEquals(targetBlobName, copyWriter.getResult().getName());
        Assert.assertNull(copyWriter.getResult().getContentType());
        Assert.assertEquals(metadata, copyWriter.getResult().getMetadata());
        Assert.assertTrue(copyWriter.isDone());
        Assert.assertTrue(remoteSourceBlob.delete());
        Assert.assertTrue(ITStorageTest.storage.delete(ITStorageTest.BUCKET, targetBlobName));
    }

    @Test
    public void testCopyBlobFail() {
        String sourceBlobName = "test-copy-blob-source-fail";
        BlobId source = BlobId.of(ITStorageTest.BUCKET, sourceBlobName, (-1L));
        Blob remoteSourceBlob = ITStorageTest.storage.create(BlobInfo.newBuilder(source).build(), ITStorageTest.BLOB_BYTE_CONTENT);
        Assert.assertNotNull(remoteSourceBlob);
        String targetBlobName = "test-copy-blob-target-fail";
        BlobInfo target = BlobInfo.newBuilder(ITStorageTest.BUCKET, targetBlobName).setContentType(ITStorageTest.CONTENT_TYPE).build();
        Storage.CopyRequest req = CopyRequest.newBuilder().setSource(ITStorageTest.BUCKET, sourceBlobName).setSourceOptions(BlobSourceOption.generationMatch((-1L))).setTarget(target).build();
        try {
            ITStorageTest.storage.copy(req);
            Assert.fail("StorageException was expected");
        } catch (StorageException ex) {
            // expected
        }
        Storage.CopyRequest req2 = CopyRequest.newBuilder().setSource(source).setSourceOptions(BlobSourceOption.generationMatch()).setTarget(target).build();
        try {
            ITStorageTest.storage.copy(req2);
            Assert.fail("StorageException was expected");
        } catch (StorageException ex) {
            // expected
        }
    }

    @Test
    public void testBatchRequest() {
        String sourceBlobName1 = "test-batch-request-blob-1";
        String sourceBlobName2 = "test-batch-request-blob-2";
        BlobInfo sourceBlob1 = BlobInfo.newBuilder(ITStorageTest.BUCKET, sourceBlobName1).build();
        BlobInfo sourceBlob2 = BlobInfo.newBuilder(ITStorageTest.BUCKET, sourceBlobName2).build();
        Assert.assertNotNull(ITStorageTest.storage.create(sourceBlob1));
        Assert.assertNotNull(ITStorageTest.storage.create(sourceBlob2));
        // Batch update request
        BlobInfo updatedBlob1 = sourceBlob1.toBuilder().setContentType(ITStorageTest.CONTENT_TYPE).build();
        BlobInfo updatedBlob2 = sourceBlob2.toBuilder().setContentType(ITStorageTest.CONTENT_TYPE).build();
        StorageBatch updateBatch = ITStorageTest.storage.batch();
        StorageBatchResult<Blob> updateResult1 = updateBatch.update(updatedBlob1);
        StorageBatchResult<Blob> updateResult2 = updateBatch.update(updatedBlob2);
        updateBatch.submit();
        Blob remoteUpdatedBlob1 = updateResult1.get();
        Blob remoteUpdatedBlob2 = updateResult2.get();
        Assert.assertEquals(sourceBlob1.getBucket(), remoteUpdatedBlob1.getBucket());
        Assert.assertEquals(sourceBlob1.getName(), remoteUpdatedBlob1.getName());
        Assert.assertEquals(sourceBlob2.getBucket(), remoteUpdatedBlob2.getBucket());
        Assert.assertEquals(sourceBlob2.getName(), remoteUpdatedBlob2.getName());
        Assert.assertEquals(updatedBlob1.getContentType(), remoteUpdatedBlob1.getContentType());
        Assert.assertEquals(updatedBlob2.getContentType(), remoteUpdatedBlob2.getContentType());
        // Batch get request
        StorageBatch getBatch = ITStorageTest.storage.batch();
        StorageBatchResult<Blob> getResult1 = getBatch.get(ITStorageTest.BUCKET, sourceBlobName1);
        StorageBatchResult<Blob> getResult2 = getBatch.get(ITStorageTest.BUCKET, sourceBlobName2);
        getBatch.submit();
        Blob remoteBlob1 = getResult1.get();
        Blob remoteBlob2 = getResult2.get();
        Assert.assertEquals(remoteUpdatedBlob1, remoteBlob1);
        Assert.assertEquals(remoteUpdatedBlob2, remoteBlob2);
        // Batch delete request
        StorageBatch deleteBatch = ITStorageTest.storage.batch();
        StorageBatchResult<Boolean> deleteResult1 = deleteBatch.delete(ITStorageTest.BUCKET, sourceBlobName1);
        StorageBatchResult<Boolean> deleteResult2 = deleteBatch.delete(ITStorageTest.BUCKET, sourceBlobName2);
        deleteBatch.submit();
        Assert.assertTrue(deleteResult1.get());
        Assert.assertTrue(deleteResult2.get());
    }

    @Test
    public void testBatchRequestManyOperations() {
        List<StorageBatchResult<Boolean>> deleteResults = Lists.newArrayListWithCapacity(ITStorageTest.MAX_BATCH_SIZE);
        List<StorageBatchResult<Blob>> getResults = Lists.newArrayListWithCapacity(((ITStorageTest.MAX_BATCH_SIZE) / 2));
        List<StorageBatchResult<Blob>> updateResults = Lists.newArrayListWithCapacity(((ITStorageTest.MAX_BATCH_SIZE) / 2));
        StorageBatch batch = ITStorageTest.storage.batch();
        for (int i = 0; i < (ITStorageTest.MAX_BATCH_SIZE); i++) {
            BlobId blobId = BlobId.of(ITStorageTest.BUCKET, ("test-batch-request-many-operations-blob-" + i));
            deleteResults.add(batch.delete(blobId));
        }
        for (int i = 0; i < ((ITStorageTest.MAX_BATCH_SIZE) / 2); i++) {
            BlobId blobId = BlobId.of(ITStorageTest.BUCKET, ("test-batch-request-many-operations-blob-" + i));
            getResults.add(batch.get(blobId));
        }
        for (int i = 0; i < ((ITStorageTest.MAX_BATCH_SIZE) / 2); i++) {
            BlobInfo blob = BlobInfo.newBuilder(BlobId.of(ITStorageTest.BUCKET, ("test-batch-request-many-operations-blob-" + i))).build();
            updateResults.add(batch.update(blob));
        }
        String sourceBlobName1 = "test-batch-request-many-operations-source-blob-1";
        String sourceBlobName2 = "test-batch-request-many-operations-source-blob-2";
        BlobInfo sourceBlob1 = BlobInfo.newBuilder(ITStorageTest.BUCKET, sourceBlobName1).build();
        BlobInfo sourceBlob2 = BlobInfo.newBuilder(ITStorageTest.BUCKET, sourceBlobName2).build();
        Assert.assertNotNull(ITStorageTest.storage.create(sourceBlob1));
        Assert.assertNotNull(ITStorageTest.storage.create(sourceBlob2));
        BlobInfo updatedBlob2 = sourceBlob2.toBuilder().setContentType(ITStorageTest.CONTENT_TYPE).build();
        StorageBatchResult<Blob> getResult = batch.get(ITStorageTest.BUCKET, sourceBlobName1);
        StorageBatchResult<Blob> updateResult = batch.update(updatedBlob2);
        batch.submit();
        // Check deletes
        for (StorageBatchResult<Boolean> failedDeleteResult : deleteResults) {
            Assert.assertFalse(failedDeleteResult.get());
        }
        // Check gets
        for (StorageBatchResult<Blob> failedGetResult : getResults) {
            Assert.assertNull(failedGetResult.get());
        }
        Blob remoteBlob1 = getResult.get();
        Assert.assertEquals(sourceBlob1.getBucket(), remoteBlob1.getBucket());
        Assert.assertEquals(sourceBlob1.getName(), remoteBlob1.getName());
        // Check updates
        for (StorageBatchResult<Blob> failedUpdateResult : updateResults) {
            try {
                failedUpdateResult.get();
                Assert.fail("Expected StorageException");
            } catch (StorageException ex) {
                // expected
            }
        }
        Blob remoteUpdatedBlob2 = updateResult.get();
        Assert.assertEquals(sourceBlob2.getBucket(), remoteUpdatedBlob2.getBucket());
        Assert.assertEquals(sourceBlob2.getName(), remoteUpdatedBlob2.getName());
        Assert.assertEquals(updatedBlob2.getContentType(), remoteUpdatedBlob2.getContentType());
    }

    @Test
    public void testBatchRequestFail() {
        String blobName = "test-batch-request-blob-fail";
        BlobInfo blob = BlobInfo.newBuilder(ITStorageTest.BUCKET, blobName).build();
        Blob remoteBlob = ITStorageTest.storage.create(blob);
        Assert.assertNotNull(remoteBlob);
        BlobInfo updatedBlob = BlobInfo.newBuilder(ITStorageTest.BUCKET, blobName, (-1L)).build();
        StorageBatch batch = ITStorageTest.storage.batch();
        StorageBatchResult<Blob> updateResult = batch.update(updatedBlob, BlobTargetOption.generationMatch());
        StorageBatchResult<Boolean> deleteResult1 = batch.delete(ITStorageTest.BUCKET, blobName, BlobSourceOption.generationMatch((-1L)));
        StorageBatchResult<Boolean> deleteResult2 = batch.delete(BlobId.of(ITStorageTest.BUCKET, blobName, (-1L)));
        StorageBatchResult<Blob> getResult1 = batch.get(ITStorageTest.BUCKET, blobName, BlobGetOption.generationMatch((-1L)));
        StorageBatchResult<Blob> getResult2 = batch.get(BlobId.of(ITStorageTest.BUCKET, blobName, (-1L)));
        batch.submit();
        try {
            updateResult.get();
            Assert.fail("Expected StorageException");
        } catch (StorageException ex) {
            // expected
        }
        try {
            deleteResult1.get();
            Assert.fail("Expected StorageException");
        } catch (StorageException ex) {
            // expected
        }
        try {
            deleteResult2.get();
            Assert.fail("Expected an 'Invalid argument' exception");
        } catch (StorageException e) {
            assertThat(e.getMessage()).contains("Invalid argument");
        }
        try {
            getResult1.get();
            Assert.fail("Expected StorageException");
        } catch (StorageException ex) {
            // expected
        }
        try {
            getResult2.get();
            Assert.fail("Expected an 'Invalid argument' exception");
        } catch (StorageException e) {
            assertThat(e.getMessage()).contains("Invalid argument");
        }
    }

    @Test
    public void testReadAndWriteChannels() throws IOException {
        String blobName = "test-read-and-write-channels-blob";
        BlobInfo blob = BlobInfo.newBuilder(ITStorageTest.BUCKET, blobName).build();
        byte[] stringBytes;
        try (WriteChannel writer = ITStorageTest.storage.writer(blob)) {
            stringBytes = ITStorageTest.BLOB_STRING_CONTENT.getBytes(StandardCharsets.UTF_8);
            writer.write(ByteBuffer.wrap(ITStorageTest.BLOB_BYTE_CONTENT));
            writer.write(ByteBuffer.wrap(stringBytes));
        }
        ByteBuffer readBytes;
        ByteBuffer readStringBytes;
        try (ReadChannel reader = ITStorageTest.storage.reader(blob.getBlobId())) {
            readBytes = ByteBuffer.allocate(ITStorageTest.BLOB_BYTE_CONTENT.length);
            readStringBytes = ByteBuffer.allocate(stringBytes.length);
            reader.read(readBytes);
            reader.read(readStringBytes);
        }
        Assert.assertArrayEquals(ITStorageTest.BLOB_BYTE_CONTENT, readBytes.array());
        Assert.assertEquals(ITStorageTest.BLOB_STRING_CONTENT, new String(readStringBytes.array(), StandardCharsets.UTF_8));
    }

    @Test
    public void testReadAndWriteChannelWithEncryptionKey() throws IOException {
        String blobName = "test-read-write-channel-with-customer-key-blob";
        BlobInfo blob = BlobInfo.newBuilder(ITStorageTest.BUCKET, blobName).build();
        byte[] stringBytes;
        try (WriteChannel writer = ITStorageTest.storage.writer(blob, BlobWriteOption.encryptionKey(ITStorageTest.BASE64_KEY))) {
            stringBytes = ITStorageTest.BLOB_STRING_CONTENT.getBytes(StandardCharsets.UTF_8);
            writer.write(ByteBuffer.wrap(ITStorageTest.BLOB_BYTE_CONTENT));
            writer.write(ByteBuffer.wrap(stringBytes));
        }
        ByteBuffer readBytes;
        ByteBuffer readStringBytes;
        try (ReadChannel reader = ITStorageTest.storage.reader(blob.getBlobId(), BlobSourceOption.decryptionKey(ITStorageTest.KEY))) {
            readBytes = ByteBuffer.allocate(ITStorageTest.BLOB_BYTE_CONTENT.length);
            readStringBytes = ByteBuffer.allocate(stringBytes.length);
            reader.read(readBytes);
            reader.read(readStringBytes);
        }
        Assert.assertArrayEquals(ITStorageTest.BLOB_BYTE_CONTENT, readBytes.array());
        Assert.assertEquals(ITStorageTest.BLOB_STRING_CONTENT, new String(readStringBytes.array(), StandardCharsets.UTF_8));
        Assert.assertTrue(ITStorageTest.storage.delete(ITStorageTest.BUCKET, blobName));
    }

    @Test
    public void testReadAndWriteChannelsWithDifferentFileSize() throws IOException {
        String blobNamePrefix = "test-read-and-write-channels-blob-";
        int[] blobSizes = new int[]{ 0, 700, 1024 * 256, (2 * 1024) * 1024, (4 * 1024) * 1024, ((4 * 1024) * 1024) + 1 };
        Random rnd = new Random();
        for (int blobSize : blobSizes) {
            String blobName = blobNamePrefix + blobSize;
            BlobInfo blob = BlobInfo.newBuilder(ITStorageTest.BUCKET, blobName).build();
            byte[] bytes = new byte[blobSize];
            rnd.nextBytes(bytes);
            try (WriteChannel writer = ITStorageTest.storage.writer(blob)) {
                writer.write(ByteBuffer.wrap(bytes));
            }
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            try (ReadChannel reader = ITStorageTest.storage.reader(blob.getBlobId())) {
                ByteBuffer buffer = ByteBuffer.allocate((64 * 1024));
                while ((reader.read(buffer)) > 0) {
                    buffer.flip();
                    output.write(buffer.array(), 0, buffer.limit());
                    buffer.clear();
                } 
            }
            Assert.assertArrayEquals(bytes, output.toByteArray());
            Assert.assertTrue(ITStorageTest.storage.delete(ITStorageTest.BUCKET, blobName));
        }
    }

    @Test
    public void testReadAndWriteCaptureChannels() throws IOException {
        String blobName = "test-read-and-write-capture-channels-blob";
        BlobInfo blob = BlobInfo.newBuilder(ITStorageTest.BUCKET, blobName).build();
        byte[] stringBytes;
        WriteChannel writer = ITStorageTest.storage.writer(blob);
        stringBytes = ITStorageTest.BLOB_STRING_CONTENT.getBytes(StandardCharsets.UTF_8);
        writer.write(ByteBuffer.wrap(ITStorageTest.BLOB_BYTE_CONTENT));
        RestorableState<WriteChannel> writerState = writer.capture();
        WriteChannel secondWriter = writerState.restore();
        secondWriter.write(ByteBuffer.wrap(stringBytes));
        secondWriter.close();
        ByteBuffer readBytes;
        ByteBuffer readStringBytes;
        ReadChannel reader = ITStorageTest.storage.reader(blob.getBlobId());
        reader.setChunkSize(ITStorageTest.BLOB_BYTE_CONTENT.length);
        readBytes = ByteBuffer.allocate(ITStorageTest.BLOB_BYTE_CONTENT.length);
        reader.read(readBytes);
        RestorableState<ReadChannel> readerState = reader.capture();
        ReadChannel secondReader = readerState.restore();
        readStringBytes = ByteBuffer.allocate(stringBytes.length);
        secondReader.read(readStringBytes);
        reader.close();
        secondReader.close();
        Assert.assertArrayEquals(ITStorageTest.BLOB_BYTE_CONTENT, readBytes.array());
        Assert.assertEquals(ITStorageTest.BLOB_STRING_CONTENT, new String(readStringBytes.array(), StandardCharsets.UTF_8));
        Assert.assertTrue(ITStorageTest.storage.delete(ITStorageTest.BUCKET, blobName));
    }

    @Test
    public void testReadChannelFail() throws IOException {
        String blobName = "test-read-channel-blob-fail";
        BlobInfo blob = BlobInfo.newBuilder(ITStorageTest.BUCKET, blobName).build();
        Blob remoteBlob = ITStorageTest.storage.create(blob);
        Assert.assertNotNull(remoteBlob);
        try (ReadChannel reader = ITStorageTest.storage.reader(blob.getBlobId(), BlobSourceOption.metagenerationMatch((-1L)))) {
            reader.read(ByteBuffer.allocate(42));
            Assert.fail("StorageException was expected");
        } catch (StorageException ex) {
            // expected
        }
        try (ReadChannel reader = ITStorageTest.storage.reader(blob.getBlobId(), BlobSourceOption.generationMatch((-1L)))) {
            reader.read(ByteBuffer.allocate(42));
            Assert.fail("StorageException was expected");
        } catch (StorageException ex) {
            // expected
        }
        BlobId blobIdWrongGeneration = BlobId.of(ITStorageTest.BUCKET, blobName, (-1L));
        try (ReadChannel reader = ITStorageTest.storage.reader(blobIdWrongGeneration, BlobSourceOption.generationMatch())) {
            reader.read(ByteBuffer.allocate(42));
            Assert.fail("StorageException was expected");
        } catch (StorageException ex) {
            // expected
        }
    }

    @Test
    public void testReadChannelFailUpdatedGeneration() throws IOException {
        String blobName = "test-read-blob-fail-updated-generation";
        BlobInfo blob = BlobInfo.newBuilder(ITStorageTest.BUCKET, blobName).build();
        Random random = new Random();
        int chunkSize = 1024;
        int blobSize = 2 * chunkSize;
        byte[] content = new byte[blobSize];
        random.nextBytes(content);
        Blob remoteBlob = ITStorageTest.storage.create(blob, content);
        Assert.assertNotNull(remoteBlob);
        Assert.assertEquals(blobSize, ((long) (remoteBlob.getSize())));
        try (ReadChannel reader = ITStorageTest.storage.reader(blob.getBlobId())) {
            reader.setChunkSize(chunkSize);
            ByteBuffer readBytes = ByteBuffer.allocate(chunkSize);
            int numReadBytes = reader.read(readBytes);
            Assert.assertEquals(chunkSize, numReadBytes);
            Assert.assertArrayEquals(Arrays.copyOf(content, chunkSize), readBytes.array());
            try (WriteChannel writer = ITStorageTest.storage.writer(blob)) {
                byte[] newContent = new byte[blobSize];
                random.nextBytes(newContent);
                int numWrittenBytes = writer.write(ByteBuffer.wrap(newContent));
                Assert.assertEquals(blobSize, numWrittenBytes);
            }
            readBytes = ByteBuffer.allocate(chunkSize);
            reader.read(readBytes);
            Assert.fail("StorageException was expected");
        } catch (StorageException ex) {
            StringBuilder messageBuilder = new StringBuilder();
            messageBuilder.append("Blob ").append(blob.getBlobId()).append(" was updated while reading");
            Assert.assertEquals(messageBuilder.toString(), ex.getMessage());
        }
        Assert.assertTrue(ITStorageTest.storage.delete(ITStorageTest.BUCKET, blobName));
    }

    @Test
    public void testWriteChannelFail() throws IOException {
        String blobName = "test-write-channel-blob-fail";
        BlobInfo blob = BlobInfo.newBuilder(ITStorageTest.BUCKET, blobName, (-1L)).build();
        try {
            try (WriteChannel writer = ITStorageTest.storage.writer(blob, BlobWriteOption.generationMatch())) {
                writer.write(ByteBuffer.allocate(42));
            }
            Assert.fail("StorageException was expected");
        } catch (StorageException ex) {
            // expected
        }
    }

    @Test
    public void testWriteChannelExistingBlob() throws IOException {
        String blobName = "test-write-channel-existing-blob";
        BlobInfo blob = BlobInfo.newBuilder(ITStorageTest.BUCKET, blobName).build();
        ITStorageTest.storage.create(blob);
        byte[] stringBytes;
        try (WriteChannel writer = ITStorageTest.storage.writer(blob)) {
            stringBytes = ITStorageTest.BLOB_STRING_CONTENT.getBytes(StandardCharsets.UTF_8);
            writer.write(ByteBuffer.wrap(stringBytes));
        }
        Assert.assertArrayEquals(stringBytes, ITStorageTest.storage.readAllBytes(blob.getBlobId()));
        Assert.assertTrue(ITStorageTest.storage.delete(ITStorageTest.BUCKET, blobName));
    }

    @Test(timeout = 5000)
    public void testWriteChannelWithConnectionPool() throws IOException {
        TransportOptions transportOptions = HttpTransportOptions.newBuilder().setHttpTransportFactory(new ITStorageTest.CustomHttpTransportFactory()).build();
        Storage storageWithPool = StorageOptions.newBuilder().setTransportOptions(transportOptions).build().getService();
        String blobName = "test-custom-pool-management";
        BlobInfo blob = BlobInfo.newBuilder(ITStorageTest.BUCKET, blobName).build();
        byte[] stringBytes;
        try (WriteChannel writer = storageWithPool.writer(blob)) {
            stringBytes = ITStorageTest.BLOB_STRING_CONTENT.getBytes(StandardCharsets.UTF_8);
            writer.write(ByteBuffer.wrap(ITStorageTest.BLOB_BYTE_CONTENT));
            writer.write(ByteBuffer.wrap(stringBytes));
        }
        try (WriteChannel writer = storageWithPool.writer(blob)) {
            stringBytes = ITStorageTest.BLOB_STRING_CONTENT.getBytes(StandardCharsets.UTF_8);
            writer.write(ByteBuffer.wrap(ITStorageTest.BLOB_BYTE_CONTENT));
            writer.write(ByteBuffer.wrap(stringBytes));
        }
    }

    @Test
    public void testGetSignedUrl() throws IOException {
        if ((ITStorageTest.storage.getOptions().getCredentials()) != null) {
            Assume.assumeTrue(((ITStorageTest.storage.getOptions().getCredentials()) instanceof ServiceAccountSigner));
        }
        String blobName = "test-get-signed-url-blob/with/slashes/and?special=!#$&'()*+,:;=?@[]";
        BlobInfo blob = BlobInfo.newBuilder(ITStorageTest.BUCKET, blobName).build();
        Blob remoteBlob = ITStorageTest.storage.create(blob, ITStorageTest.BLOB_BYTE_CONTENT);
        Assert.assertNotNull(remoteBlob);
        URL url = ITStorageTest.storage.signUrl(blob, 1, TimeUnit.HOURS);
        URLConnection connection = url.openConnection();
        byte[] readBytes = new byte[ITStorageTest.BLOB_BYTE_CONTENT.length];
        try (InputStream responseStream = connection.getInputStream()) {
            Assert.assertEquals(ITStorageTest.BLOB_BYTE_CONTENT.length, responseStream.read(readBytes));
            Assert.assertArrayEquals(ITStorageTest.BLOB_BYTE_CONTENT, readBytes);
        }
    }

    @Test
    public void testPostSignedUrl() throws IOException {
        if ((ITStorageTest.storage.getOptions().getCredentials()) != null) {
            Assume.assumeTrue(((ITStorageTest.storage.getOptions().getCredentials()) instanceof ServiceAccountSigner));
        }
        String blobName = "test-post-signed-url-blob";
        BlobInfo blob = BlobInfo.newBuilder(ITStorageTest.BUCKET, blobName).build();
        Assert.assertNotNull(ITStorageTest.storage.create(blob));
        URL url = ITStorageTest.storage.signUrl(blob, 1, TimeUnit.HOURS, SignUrlOption.httpMethod(POST));
        URLConnection connection = url.openConnection();
        connection.setDoOutput(true);
        connection.connect();
        Blob remoteBlob = ITStorageTest.storage.get(ITStorageTest.BUCKET, blobName);
        Assert.assertNotNull(remoteBlob);
        Assert.assertEquals(blob.getBucket(), remoteBlob.getBucket());
        Assert.assertEquals(blob.getName(), remoteBlob.getName());
    }

    @Test
    public void testGetBlobs() {
        String sourceBlobName1 = "test-get-blobs-1";
        String sourceBlobName2 = "test-get-blobs-2";
        BlobInfo sourceBlob1 = BlobInfo.newBuilder(ITStorageTest.BUCKET, sourceBlobName1).build();
        BlobInfo sourceBlob2 = BlobInfo.newBuilder(ITStorageTest.BUCKET, sourceBlobName2).build();
        Assert.assertNotNull(ITStorageTest.storage.create(sourceBlob1));
        Assert.assertNotNull(ITStorageTest.storage.create(sourceBlob2));
        List<Blob> remoteBlobs = ITStorageTest.storage.get(sourceBlob1.getBlobId(), sourceBlob2.getBlobId());
        Assert.assertEquals(sourceBlob1.getBucket(), remoteBlobs.get(0).getBucket());
        Assert.assertEquals(sourceBlob1.getName(), remoteBlobs.get(0).getName());
        Assert.assertEquals(sourceBlob2.getBucket(), remoteBlobs.get(1).getBucket());
        Assert.assertEquals(sourceBlob2.getName(), remoteBlobs.get(1).getName());
    }

    @Test
    public void testDownloadPublicBlobWithoutAuthentication() {
        Assume.assumeFalse(ITStorageTest.IS_VPC_TEST);
        // create an unauthorized user
        Storage unauthorizedStorage = StorageOptions.getUnauthenticatedInstance().getService();
        // try to download blobs from a public bucket
        String landsatBucket = "gcp-public-data-landsat";
        String landsatPrefix = "LC08/PRE/044/034/LC80440342016259LGN00/";
        String landsatBlob = landsatPrefix + "LC80440342016259LGN00_MTL.txt";
        byte[] bytes = unauthorizedStorage.readAllBytes(landsatBucket, landsatBlob);
        assertThat(bytes.length).isEqualTo(7903);
        int numBlobs = 0;
        Iterator<Blob> blobIterator = unauthorizedStorage.list(landsatBucket, BlobListOption.prefix(landsatPrefix)).iterateAll().iterator();
        while (blobIterator.hasNext()) {
            numBlobs++;
            blobIterator.next();
        } 
        assertThat(numBlobs).isEqualTo(13);
        // try to download blobs from a bucket that requires authentication
        // authenticated client will succeed
        // unauthenticated client will receive an exception
        String sourceBlobName = "source-blob-name";
        BlobInfo sourceBlob = BlobInfo.newBuilder(ITStorageTest.BUCKET, sourceBlobName).build();
        assertThat(ITStorageTest.storage.create(sourceBlob)).isNotNull();
        assertThat(ITStorageTest.storage.readAllBytes(ITStorageTest.BUCKET, sourceBlobName)).isNotNull();
        try {
            unauthorizedStorage.readAllBytes(ITStorageTest.BUCKET, sourceBlobName);
            Assert.fail("Expected StorageException");
        } catch (StorageException ex) {
            // expected
        }
        assertThat(ITStorageTest.storage.get(sourceBlob.getBlobId()).delete()).isTrue();
        // try to upload blobs to a bucket that requires authentication
        // authenticated client will succeed
        // unauthenticated client will receive an exception
        assertThat(ITStorageTest.storage.create(sourceBlob)).isNotNull();
        try {
            unauthorizedStorage.create(sourceBlob);
            Assert.fail("Expected StorageException");
        } catch (StorageException ex) {
            // expected
        }
        assertThat(ITStorageTest.storage.get(sourceBlob.getBlobId()).delete()).isTrue();
    }

    @Test
    public void testGetBlobsFail() {
        String sourceBlobName1 = "test-get-blobs-fail-1";
        String sourceBlobName2 = "test-get-blobs-fail-2";
        BlobInfo sourceBlob1 = BlobInfo.newBuilder(ITStorageTest.BUCKET, sourceBlobName1).build();
        BlobInfo sourceBlob2 = BlobInfo.newBuilder(ITStorageTest.BUCKET, sourceBlobName2).build();
        Assert.assertNotNull(ITStorageTest.storage.create(sourceBlob1));
        List<Blob> remoteBlobs = ITStorageTest.storage.get(sourceBlob1.getBlobId(), sourceBlob2.getBlobId());
        Assert.assertEquals(sourceBlob1.getBucket(), remoteBlobs.get(0).getBucket());
        Assert.assertEquals(sourceBlob1.getName(), remoteBlobs.get(0).getName());
        Assert.assertNull(remoteBlobs.get(1));
    }

    @Test
    public void testDeleteBlobs() {
        String sourceBlobName1 = "test-delete-blobs-1";
        String sourceBlobName2 = "test-delete-blobs-2";
        BlobInfo sourceBlob1 = BlobInfo.newBuilder(ITStorageTest.BUCKET, sourceBlobName1).build();
        BlobInfo sourceBlob2 = BlobInfo.newBuilder(ITStorageTest.BUCKET, sourceBlobName2).build();
        Assert.assertNotNull(ITStorageTest.storage.create(sourceBlob1));
        Assert.assertNotNull(ITStorageTest.storage.create(sourceBlob2));
        List<Boolean> deleteStatus = ITStorageTest.storage.delete(sourceBlob1.getBlobId(), sourceBlob2.getBlobId());
        Assert.assertTrue(deleteStatus.get(0));
        Assert.assertTrue(deleteStatus.get(1));
    }

    @Test
    public void testDeleteBlobsFail() {
        String sourceBlobName1 = "test-delete-blobs-fail-1";
        String sourceBlobName2 = "test-delete-blobs-fail-2";
        BlobInfo sourceBlob1 = BlobInfo.newBuilder(ITStorageTest.BUCKET, sourceBlobName1).build();
        BlobInfo sourceBlob2 = BlobInfo.newBuilder(ITStorageTest.BUCKET, sourceBlobName2).build();
        Assert.assertNotNull(ITStorageTest.storage.create(sourceBlob1));
        List<Boolean> deleteStatus = ITStorageTest.storage.delete(sourceBlob1.getBlobId(), sourceBlob2.getBlobId());
        Assert.assertTrue(deleteStatus.get(0));
        Assert.assertFalse(deleteStatus.get(1));
    }

    @Test
    public void testUpdateBlobs() {
        String sourceBlobName1 = "test-update-blobs-1";
        String sourceBlobName2 = "test-update-blobs-2";
        BlobInfo sourceBlob1 = BlobInfo.newBuilder(ITStorageTest.BUCKET, sourceBlobName1).build();
        BlobInfo sourceBlob2 = BlobInfo.newBuilder(ITStorageTest.BUCKET, sourceBlobName2).build();
        Blob remoteBlob1 = ITStorageTest.storage.create(sourceBlob1);
        Blob remoteBlob2 = ITStorageTest.storage.create(sourceBlob2);
        Assert.assertNotNull(remoteBlob1);
        Assert.assertNotNull(remoteBlob2);
        List<Blob> updatedBlobs = ITStorageTest.storage.update(remoteBlob1.toBuilder().setContentType(ITStorageTest.CONTENT_TYPE).build(), remoteBlob2.toBuilder().setContentType(ITStorageTest.CONTENT_TYPE).build());
        Assert.assertEquals(sourceBlob1.getBucket(), updatedBlobs.get(0).getBucket());
        Assert.assertEquals(sourceBlob1.getName(), updatedBlobs.get(0).getName());
        Assert.assertEquals(ITStorageTest.CONTENT_TYPE, updatedBlobs.get(0).getContentType());
        Assert.assertEquals(sourceBlob2.getBucket(), updatedBlobs.get(1).getBucket());
        Assert.assertEquals(sourceBlob2.getName(), updatedBlobs.get(1).getName());
        Assert.assertEquals(ITStorageTest.CONTENT_TYPE, updatedBlobs.get(1).getContentType());
    }

    @Test
    public void testUpdateBlobsFail() {
        String sourceBlobName1 = "test-update-blobs-fail-1";
        String sourceBlobName2 = "test-update-blobs-fail-2";
        BlobInfo sourceBlob1 = BlobInfo.newBuilder(ITStorageTest.BUCKET, sourceBlobName1).build();
        BlobInfo sourceBlob2 = BlobInfo.newBuilder(ITStorageTest.BUCKET, sourceBlobName2).build();
        BlobInfo remoteBlob1 = ITStorageTest.storage.create(sourceBlob1);
        Assert.assertNotNull(remoteBlob1);
        List<Blob> updatedBlobs = ITStorageTest.storage.update(remoteBlob1.toBuilder().setContentType(ITStorageTest.CONTENT_TYPE).build(), sourceBlob2.toBuilder().setContentType(ITStorageTest.CONTENT_TYPE).build());
        Assert.assertEquals(sourceBlob1.getBucket(), updatedBlobs.get(0).getBucket());
        Assert.assertEquals(sourceBlob1.getName(), updatedBlobs.get(0).getName());
        Assert.assertEquals(ITStorageTest.CONTENT_TYPE, updatedBlobs.get(0).getContentType());
        Assert.assertNull(updatedBlobs.get(1));
    }

    @Test
    public void testBucketAcl() {
        testBucketAclRequesterPays(true);
        testBucketAclRequesterPays(false);
    }

    @Test
    public void testBucketDefaultAcl() {
        Assert.assertNull(ITStorageTest.storage.getDefaultAcl(ITStorageTest.BUCKET, User.ofAllAuthenticatedUsers()));
        Assert.assertFalse(ITStorageTest.storage.deleteDefaultAcl(ITStorageTest.BUCKET, User.ofAllAuthenticatedUsers()));
        Acl acl = Acl.of(User.ofAllAuthenticatedUsers(), READER);
        Assert.assertNotNull(ITStorageTest.storage.createDefaultAcl(ITStorageTest.BUCKET, acl));
        Acl updatedAcl = ITStorageTest.storage.updateDefaultAcl(ITStorageTest.BUCKET, acl.toBuilder().setRole(OWNER).build());
        Assert.assertEquals(OWNER, updatedAcl.getRole());
        Set<Acl> acls = new HashSet<>();
        acls.addAll(ITStorageTest.storage.listDefaultAcls(ITStorageTest.BUCKET));
        Assert.assertTrue(acls.contains(updatedAcl));
        Assert.assertTrue(ITStorageTest.storage.deleteDefaultAcl(ITStorageTest.BUCKET, User.ofAllAuthenticatedUsers()));
        Assert.assertNull(ITStorageTest.storage.getDefaultAcl(ITStorageTest.BUCKET, User.ofAllAuthenticatedUsers()));
    }

    @Test
    public void testBlobAcl() {
        BlobId blobId = BlobId.of(ITStorageTest.BUCKET, "test-blob-acl");
        BlobInfo blob = BlobInfo.newBuilder(blobId).build();
        ITStorageTest.storage.create(blob);
        Assert.assertNull(ITStorageTest.storage.getAcl(blobId, User.ofAllAuthenticatedUsers()));
        Acl acl = Acl.of(User.ofAllAuthenticatedUsers(), READER);
        Assert.assertNotNull(ITStorageTest.storage.createAcl(blobId, acl));
        Acl updatedAcl = ITStorageTest.storage.updateAcl(blobId, acl.toBuilder().setRole(OWNER).build());
        Assert.assertEquals(OWNER, updatedAcl.getRole());
        Set<Acl> acls = new HashSet(ITStorageTest.storage.listAcls(blobId));
        Assert.assertTrue(acls.contains(updatedAcl));
        Assert.assertTrue(ITStorageTest.storage.deleteAcl(blobId, User.ofAllAuthenticatedUsers()));
        Assert.assertNull(ITStorageTest.storage.getAcl(blobId, User.ofAllAuthenticatedUsers()));
        // test non-existing blob
        BlobId otherBlobId = BlobId.of(ITStorageTest.BUCKET, "test-blob-acl", (-1L));
        try {
            Assert.assertNull(ITStorageTest.storage.getAcl(otherBlobId, User.ofAllAuthenticatedUsers()));
            Assert.fail("Expected an 'Invalid argument' exception");
        } catch (StorageException e) {
            assertThat(e.getMessage()).contains("Invalid argument");
        }
        try {
            Assert.assertFalse(ITStorageTest.storage.deleteAcl(otherBlobId, User.ofAllAuthenticatedUsers()));
            Assert.fail("Expected an 'Invalid argument' exception");
        } catch (StorageException e) {
            assertThat(e.getMessage()).contains("Invalid argument");
        }
        try {
            ITStorageTest.storage.createAcl(otherBlobId, acl);
            Assert.fail("Expected StorageException");
        } catch (StorageException ex) {
            // expected
        }
        try {
            ITStorageTest.storage.updateAcl(otherBlobId, acl);
            Assert.fail("Expected StorageException");
        } catch (StorageException ex) {
            // expected
        }
        try {
            ITStorageTest.storage.listAcls(otherBlobId);
            Assert.fail("Expected StorageException");
        } catch (StorageException ex) {
            // expected
        }
    }

    @Test
    public void testReadCompressedBlob() throws IOException {
        String blobName = "test-read-compressed-blob";
        BlobInfo blobInfo = BlobInfo.newBuilder(BlobId.of(ITStorageTest.BUCKET, blobName)).setContentType("text/plain").setContentEncoding("gzip").build();
        Blob blob = ITStorageTest.storage.create(blobInfo, ITStorageTest.COMPRESSED_CONTENT);
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            try (ReadChannel reader = ITStorageTest.storage.reader(BlobId.of(ITStorageTest.BUCKET, blobName))) {
                reader.setChunkSize(8);
                ByteBuffer buffer = ByteBuffer.allocate(8);
                while ((reader.read(buffer)) != (-1)) {
                    buffer.flip();
                    output.write(buffer.array(), 0, buffer.limit());
                    buffer.clear();
                } 
            }
            Assert.assertArrayEquals(ITStorageTest.BLOB_STRING_CONTENT.getBytes(StandardCharsets.UTF_8), ITStorageTest.storage.readAllBytes(ITStorageTest.BUCKET, blobName));
            Assert.assertArrayEquals(ITStorageTest.COMPRESSED_CONTENT, output.toByteArray());
            try (GZIPInputStream zipInput = new GZIPInputStream(new ByteArrayInputStream(output.toByteArray()))) {
                Assert.assertArrayEquals(ITStorageTest.BLOB_STRING_CONTENT.getBytes(StandardCharsets.UTF_8), ByteStreams.toByteArray(zipInput));
            }
        }
    }

    @Test
    public void testBucketPolicy() {
        testBucketPolicyRequesterPays(true);
        testBucketPolicyRequesterPays(false);
    }

    @Test
    public void testUpdateBucketLabel() {
        Bucket remoteBucket = ITStorageTest.storage.get(ITStorageTest.BUCKET, BucketGetOption.fields(ID));
        Assert.assertNull(remoteBucket.getLabels());
        remoteBucket = remoteBucket.toBuilder().setLabels(ITStorageTest.BUCKET_LABELS).build();
        Bucket updatedBucket = ITStorageTest.storage.update(remoteBucket);
        Assert.assertEquals(ITStorageTest.BUCKET_LABELS, updatedBucket.getLabels());
    }

    @Test
    public void testUpdateBucketRequesterPays() {
        Bucket remoteBucket = ITStorageTest.storage.get(ITStorageTest.BUCKET, BucketGetOption.fields(ID));
        Assert.assertNull(remoteBucket.requesterPays());
        remoteBucket = remoteBucket.toBuilder().setRequesterPays(true).build();
        Bucket updatedBucket = ITStorageTest.storage.update(remoteBucket);
        Assert.assertTrue(updatedBucket.requesterPays());
        String projectId = ITStorageTest.remoteStorageHelper.getOptions().getProjectId();
        Bucket.BlobTargetOption option = Bucket.BlobTargetOption.userProject(projectId);
        String blobName = "test-create-empty-blob-requester-pays";
        Blob remoteBlob = updatedBucket.create(blobName, ITStorageTest.BLOB_BYTE_CONTENT, option);
        Assert.assertNotNull(remoteBlob);
        byte[] readBytes = ITStorageTest.storage.readAllBytes(ITStorageTest.BUCKET, blobName);
        Assert.assertArrayEquals(ITStorageTest.BLOB_BYTE_CONTENT, readBytes);
    }

    @Test
    public void testListBucketRequesterPaysFails() throws InterruptedException {
        String projectId = ITStorageTest.remoteStorageHelper.getOptions().getProjectId();
        Iterator<Bucket> bucketIterator = ITStorageTest.storage.list(BucketListOption.prefix(ITStorageTest.BUCKET), BucketListOption.fields(), BucketListOption.userProject(projectId)).iterateAll().iterator();
        while (!(bucketIterator.hasNext())) {
            Thread.sleep(500);
            bucketIterator = ITStorageTest.storage.list(BucketListOption.prefix(ITStorageTest.BUCKET), BucketListOption.fields()).iterateAll().iterator();
        } 
        while (bucketIterator.hasNext()) {
            Bucket remoteBucket = bucketIterator.next();
            Assert.assertTrue(remoteBucket.getName().startsWith(ITStorageTest.BUCKET));
            Assert.assertNull(remoteBucket.getCreateTime());
            Assert.assertNull(remoteBucket.getSelfLink());
        } 
    }

    @Test
    public void testListBucketDefaultKmsKeyName() throws InterruptedException, ExecutionException {
        String bucketName = RemoteStorageHelper.generateBucketName();
        Bucket remoteBucket = ITStorageTest.storage.create(BucketInfo.newBuilder(bucketName).setDefaultKmsKeyName(ITStorageTest.kmsKeyOneResourcePath).setLocation(ITStorageTest.KMS_KEY_RING_LOCATION).build());
        Assert.assertNotNull(remoteBucket);
        Assert.assertTrue(remoteBucket.getDefaultKmsKeyName().startsWith(ITStorageTest.kmsKeyOneResourcePath));
        try {
            Iterator<Bucket> bucketIterator = ITStorageTest.storage.list(BucketListOption.prefix(bucketName), BucketListOption.fields(ENCRYPTION)).iterateAll().iterator();
            while (!(bucketIterator.hasNext())) {
                Thread.sleep(500);
                bucketIterator = ITStorageTest.storage.list(BucketListOption.prefix(bucketName), BucketListOption.fields(ENCRYPTION)).iterateAll().iterator();
            } 
            while (bucketIterator.hasNext()) {
                Bucket bucket = bucketIterator.next();
                Assert.assertTrue(bucket.getName().startsWith(bucketName));
                Assert.assertNotNull(bucket.getDefaultKmsKeyName());
                Assert.assertTrue(bucket.getDefaultKmsKeyName().startsWith(ITStorageTest.kmsKeyOneResourcePath));
                Assert.assertNull(bucket.getCreateTime());
                Assert.assertNull(bucket.getSelfLink());
            } 
        } finally {
            RemoteStorageHelper.forceDelete(ITStorageTest.storage, bucketName, 5, TimeUnit.SECONDS);
        }
    }

    @Test
    public void testRetentionPolicyNoLock() throws InterruptedException, ExecutionException {
        String bucketName = RemoteStorageHelper.generateBucketName();
        Bucket remoteBucket = ITStorageTest.storage.create(BucketInfo.newBuilder(bucketName).setRetentionPeriod(ITStorageTest.RETENTION_PERIOD).build());
        try {
            Assert.assertEquals(ITStorageTest.RETENTION_PERIOD, remoteBucket.getRetentionPeriod());
            Assert.assertNotNull(remoteBucket.getRetentionEffectiveTime());
            Assert.assertNull(remoteBucket.retentionPolicyIsLocked());
            remoteBucket = ITStorageTest.storage.get(bucketName, BucketGetOption.fields(RETENTION_POLICY));
            Assert.assertEquals(ITStorageTest.RETENTION_PERIOD, remoteBucket.getRetentionPeriod());
            Assert.assertNotNull(remoteBucket.getRetentionEffectiveTime());
            Assert.assertNull(remoteBucket.retentionPolicyIsLocked());
            String blobName = "test-create-with-retention-policy-hold";
            BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, blobName).build();
            Blob remoteBlob = ITStorageTest.storage.create(blobInfo);
            Assert.assertNotNull(remoteBlob.getRetentionExpirationTime());
            remoteBucket = remoteBucket.toBuilder().setRetentionPeriod(null).build().update();
            Assert.assertNull(remoteBucket.getRetentionPeriod());
            remoteBucket = remoteBucket.toBuilder().setRetentionPeriod(null).build().update();
            Assert.assertNull(remoteBucket.getRetentionPeriod());
        } finally {
            RemoteStorageHelper.forceDelete(ITStorageTest.storage, bucketName, 5, TimeUnit.SECONDS);
        }
    }

    @Test
    public void testRetentionPolicyLock() throws InterruptedException, ExecutionException {
        retentionPolicyLockRequesterPays(true);
        retentionPolicyLockRequesterPays(false);
    }

    @Test
    public void testAttemptObjectDeleteWithRetentionPolicy() throws InterruptedException, ExecutionException {
        String bucketName = RemoteStorageHelper.generateBucketName();
        Bucket remoteBucket = ITStorageTest.storage.create(BucketInfo.newBuilder(bucketName).setRetentionPeriod(ITStorageTest.RETENTION_PERIOD).build());
        Assert.assertEquals(ITStorageTest.RETENTION_PERIOD, remoteBucket.getRetentionPeriod());
        String blobName = "test-create-with-retention-policy";
        BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, blobName).build();
        Blob remoteBlob = ITStorageTest.storage.create(blobInfo);
        Assert.assertNotNull(remoteBlob.getRetentionExpirationTime());
        try {
            remoteBlob.delete();
            Assert.fail("Expected failure on delete from retentionPolicy");
        } catch (StorageException ex) {
            // expected
        } finally {
            Thread.sleep(ITStorageTest.RETENTION_PERIOD_IN_MILLISECONDS);
            RemoteStorageHelper.forceDelete(ITStorageTest.storage, bucketName, 5, TimeUnit.SECONDS);
        }
    }

    @Test
    public void testEnableDisableBucketDefaultEventBasedHold() throws InterruptedException, ExecutionException {
        String bucketName = RemoteStorageHelper.generateBucketName();
        Bucket remoteBucket = ITStorageTest.storage.create(BucketInfo.newBuilder(bucketName).setDefaultEventBasedHold(true).build());
        try {
            Assert.assertTrue(remoteBucket.getDefaultEventBasedHold());
            remoteBucket = ITStorageTest.storage.get(bucketName, BucketGetOption.fields(DEFAULT_EVENT_BASED_HOLD));
            Assert.assertTrue(remoteBucket.getDefaultEventBasedHold());
            String blobName = "test-create-with-event-based-hold";
            BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, blobName).build();
            Blob remoteBlob = ITStorageTest.storage.create(blobInfo);
            Assert.assertTrue(remoteBlob.getEventBasedHold());
            remoteBlob = ITStorageTest.storage.get(blobInfo.getBlobId(), BlobGetOption.fields(EVENT_BASED_HOLD));
            Assert.assertTrue(remoteBlob.getEventBasedHold());
            remoteBlob = remoteBlob.toBuilder().setEventBasedHold(false).build().update();
            Assert.assertFalse(remoteBlob.getEventBasedHold());
            remoteBucket = remoteBucket.toBuilder().setDefaultEventBasedHold(false).build().update();
            Assert.assertFalse(remoteBucket.getDefaultEventBasedHold());
        } finally {
            RemoteStorageHelper.forceDelete(ITStorageTest.storage, bucketName, 5, TimeUnit.SECONDS);
        }
    }

    @Test
    public void testEnableDisableTemporaryHold() {
        String blobName = "test-create-with-temporary-hold";
        BlobInfo blobInfo = BlobInfo.newBuilder(ITStorageTest.BUCKET, blobName).setTemporaryHold(true).build();
        Blob remoteBlob = ITStorageTest.storage.create(blobInfo);
        Assert.assertTrue(remoteBlob.getTemporaryHold());
        remoteBlob = ITStorageTest.storage.get(remoteBlob.getBlobId(), BlobGetOption.fields(TEMPORARY_HOLD));
        Assert.assertTrue(remoteBlob.getTemporaryHold());
        remoteBlob = remoteBlob.toBuilder().setTemporaryHold(false).build().update();
        Assert.assertFalse(remoteBlob.getTemporaryHold());
    }

    @Test
    public void testAttemptObjectDeleteWithEventBasedHold() {
        String blobName = "test-create-with-event-based-hold";
        BlobInfo blobInfo = BlobInfo.newBuilder(ITStorageTest.BUCKET, blobName).setEventBasedHold(true).build();
        Blob remoteBlob = ITStorageTest.storage.create(blobInfo);
        Assert.assertTrue(remoteBlob.getEventBasedHold());
        try {
            remoteBlob.delete();
            Assert.fail("Expected failure on delete from eventBasedHold");
        } catch (StorageException ex) {
            // expected
        } finally {
            remoteBlob.toBuilder().setEventBasedHold(false).build().update();
        }
    }

    @Test
    public void testAttemptDeletionObjectTemporaryHold() {
        String blobName = "test-create-with-temporary-hold";
        BlobInfo blobInfo = BlobInfo.newBuilder(ITStorageTest.BUCKET, blobName).setTemporaryHold(true).build();
        Blob remoteBlob = ITStorageTest.storage.create(blobInfo);
        Assert.assertTrue(remoteBlob.getTemporaryHold());
        try {
            remoteBlob.delete();
            Assert.fail("Expected failure on delete from temporaryHold");
        } catch (StorageException ex) {
            // expected
        } finally {
            remoteBlob.toBuilder().setEventBasedHold(false).build().update();
        }
    }

    @Test
    public void testGetServiceAccount() {
        String projectId = ITStorageTest.remoteStorageHelper.getOptions().getProjectId();
        ServiceAccount serviceAccount = ITStorageTest.storage.getServiceAccount(projectId);
        Assert.assertNotNull(serviceAccount);
        Assert.assertTrue(serviceAccount.getEmail().endsWith(ITStorageTest.SERVICE_ACCOUNT_EMAIL_SUFFIX));
    }

    @Test
    public void testBucketWithBucketPolicyOnlyEnabled() throws Exception {
        String bpoBucket = RemoteStorageHelper.generateBucketName();
        try {
            ITStorageTest.storage.create(Bucket.newBuilder(bpoBucket).setIamConfiguration(IamConfiguration.newBuilder().setIsBucketPolicyOnlyEnabled(true).build()).build());
            Bucket remoteBucket = ITStorageTest.storage.get(bpoBucket, BucketGetOption.fields(IAMCONFIGURATION));
            Assert.assertTrue(remoteBucket.getIamConfiguration().isBucketPolicyOnlyEnabled());
            Assert.assertNotNull(remoteBucket.getIamConfiguration().getBucketPolicyOnlyLockedTime());
            try {
                remoteBucket.listAcls();
                Assert.fail("StorageException was expected.");
            } catch (StorageException e) {
                // Expected: Listing legacy ACLs should fail on a BPO enabled bucket
            }
            try {
                remoteBucket.listDefaultAcls();
                Assert.fail("StorageException was expected");
            } catch (StorageException e) {
                // Expected: Listing legacy ACLs should fail on a BPO enabled bucket
            }
        } finally {
            RemoteStorageHelper.forceDelete(ITStorageTest.storage, bpoBucket, 1, TimeUnit.MINUTES);
        }
    }

    @Test
    public void testEnableAndDisableBucketPolicyOnlyOnExistingBucket() throws Exception {
        String bpoBucket = RemoteStorageHelper.generateBucketName();
        try {
            BucketInfo.IamConfiguration bpoDisabledIamConfiguration = IamConfiguration.newBuilder().setIsBucketPolicyOnlyEnabled(false).build();
            Bucket bucket = ITStorageTest.storage.create(Bucket.newBuilder(bpoBucket).setIamConfiguration(bpoDisabledIamConfiguration).setAcl(ImmutableList.of(Acl.of(User.ofAllAuthenticatedUsers(), READER))).setDefaultAcl(ImmutableList.of(Acl.of(User.ofAllAuthenticatedUsers(), READER))).build());
            bucket.toBuilder().setIamConfiguration(bpoDisabledIamConfiguration.toBuilder().setIsBucketPolicyOnlyEnabled(true).build()).build().update();
            Bucket remoteBucket = ITStorageTest.storage.get(bpoBucket, BucketGetOption.fields(IAMCONFIGURATION));
            Assert.assertTrue(remoteBucket.getIamConfiguration().isBucketPolicyOnlyEnabled());
            Assert.assertNotNull(remoteBucket.getIamConfiguration().getBucketPolicyOnlyLockedTime());
            bucket.toBuilder().setIamConfiguration(bpoDisabledIamConfiguration).build().update();
            remoteBucket = ITStorageTest.storage.get(bpoBucket, BucketGetOption.fields(IAMCONFIGURATION, ACL, DEFAULT_OBJECT_ACL));
            Assert.assertFalse(remoteBucket.getIamConfiguration().isBucketPolicyOnlyEnabled());
            Assert.assertEquals(User.ofAllAuthenticatedUsers(), remoteBucket.getDefaultAcl().get(0).getEntity());
            Assert.assertEquals(READER, remoteBucket.getDefaultAcl().get(0).getRole());
            Assert.assertEquals(User.ofAllAuthenticatedUsers(), remoteBucket.getAcl().get(0).getEntity());
            Assert.assertEquals(READER, remoteBucket.getAcl().get(0).getRole());
        } finally {
            RemoteStorageHelper.forceDelete(ITStorageTest.storage, bpoBucket, 1, TimeUnit.MINUTES);
        }
    }
}

