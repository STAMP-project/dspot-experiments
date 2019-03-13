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
package com.google.cloud.storage;


import com.google.api.services.storage.model.StorageObject;
import com.google.cloud.storage.Acl.User;
import com.google.cloud.storage.BlobInfo.CustomerEncryption;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;

import static ProjectRole.VIEWERS;
import static StorageClass.COLDLINE;


public class BlobInfoTest {
    private static final List<Acl> ACL = ImmutableList.of(Acl.of(User.ofAllAuthenticatedUsers(), Role.READER), Acl.of(new com.google.cloud.storage.Acl.Project(VIEWERS, "p1"), Role.WRITER));

    private static final Integer COMPONENT_COUNT = 2;

    private static final String CONTENT_TYPE = "text/html";

    private static final String CACHE_CONTROL = "cache";

    private static final String CONTENT_DISPOSITION = "content-disposition";

    private static final String CONTENT_ENCODING = "UTF-8";

    private static final String CONTENT_LANGUAGE = "En";

    private static final String CRC32 = "FF00";

    private static final String CRC32_HEX_STRING = "145d34";

    private static final Long DELETE_TIME = System.currentTimeMillis();

    private static final String ETAG = "0xFF00";

    private static final Long GENERATION = 1L;

    private static final String GENERATED_ID = "B/N:1";

    private static final String MD5 = "FF00";

    private static final String MD5_HEX_STRING = "145d34";

    private static final String MEDIA_LINK = "http://media/b/n";

    private static final Map<String, String> METADATA = ImmutableMap.of("n1", "v1", "n2", "v2");

    private static final Long META_GENERATION = 10L;

    private static final User OWNER = new User("user@gmail.com");

    private static final String SELF_LINK = "http://storage/b/n";

    private static final Long SIZE = 1024L;

    private static final Long UPDATE_TIME = (BlobInfoTest.DELETE_TIME) - 1L;

    private static final Long CREATE_TIME = (BlobInfoTest.UPDATE_TIME) - 1L;

    private static final String ENCRYPTION_ALGORITHM = "AES256";

    private static final String KEY_SHA256 = "keySha";

    private static final CustomerEncryption CUSTOMER_ENCRYPTION = new CustomerEncryption(BlobInfoTest.ENCRYPTION_ALGORITHM, BlobInfoTest.KEY_SHA256);

    private static final String KMS_KEY_NAME = "projects/p/locations/kr-loc/keyRings/kr/cryptoKeys/key";

    private static final StorageClass STORAGE_CLASS = COLDLINE;

    private static final Boolean EVENT_BASED_HOLD = true;

    private static final Boolean TEMPORARY_HOLD = true;

    private static final Long RETENTION_EXPIRATION_TIME = 10L;

    private static final BlobInfo BLOB_INFO = BlobInfo.newBuilder("b", "n", BlobInfoTest.GENERATION).setAcl(BlobInfoTest.ACL).setComponentCount(BlobInfoTest.COMPONENT_COUNT).setContentType(BlobInfoTest.CONTENT_TYPE).setCacheControl(BlobInfoTest.CACHE_CONTROL).setContentDisposition(BlobInfoTest.CONTENT_DISPOSITION).setContentEncoding(BlobInfoTest.CONTENT_ENCODING).setContentLanguage(BlobInfoTest.CONTENT_LANGUAGE).setCustomerEncryption(BlobInfoTest.CUSTOMER_ENCRYPTION).setCrc32c(BlobInfoTest.CRC32).setDeleteTime(BlobInfoTest.DELETE_TIME).setEtag(BlobInfoTest.ETAG).setGeneratedId(BlobInfoTest.GENERATED_ID).setMd5(BlobInfoTest.MD5).setMediaLink(BlobInfoTest.MEDIA_LINK).setMetadata(BlobInfoTest.METADATA).setMetageneration(BlobInfoTest.META_GENERATION).setOwner(BlobInfoTest.OWNER).setSelfLink(BlobInfoTest.SELF_LINK).setSize(BlobInfoTest.SIZE).setUpdateTime(BlobInfoTest.UPDATE_TIME).setCreateTime(BlobInfoTest.CREATE_TIME).setStorageClass(BlobInfoTest.STORAGE_CLASS).setKmsKeyName(BlobInfoTest.KMS_KEY_NAME).setEventBasedHold(BlobInfoTest.EVENT_BASED_HOLD).setTemporaryHold(BlobInfoTest.TEMPORARY_HOLD).setRetentionExpirationTime(BlobInfoTest.RETENTION_EXPIRATION_TIME).build();

    private static final BlobInfo DIRECTORY_INFO = BlobInfo.newBuilder("b", "n/").setSize(0L).setIsDirectory(true).build();

    @Test
    public void testCustomerEncryption() {
        Assert.assertEquals(BlobInfoTest.ENCRYPTION_ALGORITHM, BlobInfoTest.CUSTOMER_ENCRYPTION.getEncryptionAlgorithm());
        Assert.assertEquals(BlobInfoTest.KEY_SHA256, BlobInfoTest.CUSTOMER_ENCRYPTION.getKeySha256());
    }

    @Test
    public void testToBuilder() {
        compareBlobs(BlobInfoTest.BLOB_INFO, BlobInfoTest.BLOB_INFO.toBuilder().build());
        BlobInfo blobInfo = BlobInfoTest.BLOB_INFO.toBuilder().setBlobId(BlobId.of("b2", "n2")).setSize(200L).build();
        Assert.assertEquals("n2", blobInfo.getName());
        Assert.assertEquals("b2", blobInfo.getBucket());
        Assert.assertEquals(Long.valueOf(200), blobInfo.getSize());
        blobInfo = blobInfo.toBuilder().setBlobId(BlobId.of("b", "n", BlobInfoTest.GENERATION)).setSize(BlobInfoTest.SIZE).build();
        compareBlobs(BlobInfoTest.BLOB_INFO, blobInfo);
    }

    @Test
    public void testToBuilderSetMd5FromHexString() {
        BlobInfo blobInfo = BlobInfo.newBuilder(BlobId.of("b2", "n2")).setMd5FromHexString(BlobInfoTest.MD5_HEX_STRING).build();
        Assert.assertEquals(BlobInfoTest.MD5, blobInfo.getMd5());
    }

    @Test
    public void testToBuilderSetCrc32cFromHexString() {
        BlobInfo blobInfo = BlobInfo.newBuilder(BlobId.of("b2", "n2")).setCrc32cFromHexString(BlobInfoTest.CRC32_HEX_STRING).build();
        Assert.assertEquals(BlobInfoTest.CRC32, blobInfo.getCrc32c());
    }

    @Test
    public void testToBuilderIncomplete() {
        BlobInfo incompleteBlobInfo = BlobInfo.newBuilder(BlobId.of("b2", "n2")).build();
        compareBlobs(incompleteBlobInfo, incompleteBlobInfo.toBuilder().build());
    }

    @Test
    public void testBuilder() {
        Assert.assertEquals("b", BlobInfoTest.BLOB_INFO.getBucket());
        Assert.assertEquals("n", BlobInfoTest.BLOB_INFO.getName());
        Assert.assertEquals(BlobInfoTest.ACL, BlobInfoTest.BLOB_INFO.getAcl());
        Assert.assertEquals(BlobInfoTest.COMPONENT_COUNT, BlobInfoTest.BLOB_INFO.getComponentCount());
        Assert.assertEquals(BlobInfoTest.CONTENT_TYPE, BlobInfoTest.BLOB_INFO.getContentType());
        Assert.assertEquals(BlobInfoTest.CACHE_CONTROL, BlobInfoTest.BLOB_INFO.getCacheControl());
        Assert.assertEquals(BlobInfoTest.CONTENT_DISPOSITION, BlobInfoTest.BLOB_INFO.getContentDisposition());
        Assert.assertEquals(BlobInfoTest.CONTENT_ENCODING, BlobInfoTest.BLOB_INFO.getContentEncoding());
        Assert.assertEquals(BlobInfoTest.CONTENT_LANGUAGE, BlobInfoTest.BLOB_INFO.getContentLanguage());
        Assert.assertEquals(BlobInfoTest.CUSTOMER_ENCRYPTION, BlobInfoTest.BLOB_INFO.getCustomerEncryption());
        Assert.assertEquals(BlobInfoTest.CRC32, BlobInfoTest.BLOB_INFO.getCrc32c());
        Assert.assertEquals(BlobInfoTest.CRC32_HEX_STRING, BlobInfoTest.BLOB_INFO.getCrc32cToHexString());
        Assert.assertEquals(BlobInfoTest.DELETE_TIME, BlobInfoTest.BLOB_INFO.getDeleteTime());
        Assert.assertEquals(BlobInfoTest.ETAG, BlobInfoTest.BLOB_INFO.getEtag());
        Assert.assertEquals(BlobInfoTest.GENERATION, BlobInfoTest.BLOB_INFO.getGeneration());
        Assert.assertEquals(BlobInfoTest.GENERATED_ID, BlobInfoTest.BLOB_INFO.getGeneratedId());
        Assert.assertEquals(BlobInfoTest.MD5, BlobInfoTest.BLOB_INFO.getMd5());
        Assert.assertEquals(BlobInfoTest.MD5_HEX_STRING, BlobInfoTest.BLOB_INFO.getMd5ToHexString());
        Assert.assertEquals(BlobInfoTest.MEDIA_LINK, BlobInfoTest.BLOB_INFO.getMediaLink());
        Assert.assertEquals(BlobInfoTest.METADATA, BlobInfoTest.BLOB_INFO.getMetadata());
        Assert.assertEquals(BlobInfoTest.META_GENERATION, BlobInfoTest.BLOB_INFO.getMetageneration());
        Assert.assertEquals(BlobInfoTest.OWNER, BlobInfoTest.BLOB_INFO.getOwner());
        Assert.assertEquals(BlobInfoTest.SELF_LINK, BlobInfoTest.BLOB_INFO.getSelfLink());
        Assert.assertEquals(BlobInfoTest.SIZE, BlobInfoTest.BLOB_INFO.getSize());
        Assert.assertEquals(BlobInfoTest.UPDATE_TIME, BlobInfoTest.BLOB_INFO.getUpdateTime());
        Assert.assertEquals(BlobInfoTest.CREATE_TIME, BlobInfoTest.BLOB_INFO.getCreateTime());
        Assert.assertEquals(BlobInfoTest.STORAGE_CLASS, BlobInfoTest.BLOB_INFO.getStorageClass());
        Assert.assertEquals(BlobInfoTest.KMS_KEY_NAME, BlobInfoTest.BLOB_INFO.getKmsKeyName());
        Assert.assertEquals(BlobInfoTest.EVENT_BASED_HOLD, BlobInfoTest.BLOB_INFO.getEventBasedHold());
        Assert.assertEquals(BlobInfoTest.TEMPORARY_HOLD, BlobInfoTest.BLOB_INFO.getTemporaryHold());
        Assert.assertEquals(BlobInfoTest.RETENTION_EXPIRATION_TIME, BlobInfoTest.BLOB_INFO.getRetentionExpirationTime());
        Assert.assertFalse(BlobInfoTest.BLOB_INFO.isDirectory());
        Assert.assertEquals("b", BlobInfoTest.DIRECTORY_INFO.getBucket());
        Assert.assertEquals("n/", BlobInfoTest.DIRECTORY_INFO.getName());
        Assert.assertNull(BlobInfoTest.DIRECTORY_INFO.getAcl());
        Assert.assertNull(BlobInfoTest.DIRECTORY_INFO.getComponentCount());
        Assert.assertNull(BlobInfoTest.DIRECTORY_INFO.getContentType());
        Assert.assertNull(BlobInfoTest.DIRECTORY_INFO.getCacheControl());
        Assert.assertNull(BlobInfoTest.DIRECTORY_INFO.getContentDisposition());
        Assert.assertNull(BlobInfoTest.DIRECTORY_INFO.getContentEncoding());
        Assert.assertNull(BlobInfoTest.DIRECTORY_INFO.getContentLanguage());
        Assert.assertNull(BlobInfoTest.DIRECTORY_INFO.getCustomerEncryption());
        Assert.assertNull(BlobInfoTest.DIRECTORY_INFO.getCrc32c());
        Assert.assertNull(BlobInfoTest.DIRECTORY_INFO.getCrc32cToHexString());
        Assert.assertNull(BlobInfoTest.DIRECTORY_INFO.getCreateTime());
        Assert.assertNull(BlobInfoTest.DIRECTORY_INFO.getDeleteTime());
        Assert.assertNull(BlobInfoTest.DIRECTORY_INFO.getEtag());
        Assert.assertNull(BlobInfoTest.DIRECTORY_INFO.getGeneration());
        Assert.assertNull(BlobInfoTest.DIRECTORY_INFO.getGeneratedId());
        Assert.assertNull(BlobInfoTest.DIRECTORY_INFO.getMd5());
        Assert.assertNull(BlobInfoTest.DIRECTORY_INFO.getMd5ToHexString());
        Assert.assertNull(BlobInfoTest.DIRECTORY_INFO.getMediaLink());
        Assert.assertNull(BlobInfoTest.DIRECTORY_INFO.getMetadata());
        Assert.assertNull(BlobInfoTest.DIRECTORY_INFO.getMetageneration());
        Assert.assertNull(BlobInfoTest.DIRECTORY_INFO.getOwner());
        Assert.assertNull(BlobInfoTest.DIRECTORY_INFO.getSelfLink());
        Assert.assertEquals(0L, ((long) (BlobInfoTest.DIRECTORY_INFO.getSize())));
        Assert.assertNull(BlobInfoTest.DIRECTORY_INFO.getUpdateTime());
        Assert.assertTrue(BlobInfoTest.DIRECTORY_INFO.isDirectory());
    }

    @Test
    public void testToPbAndFromPb() {
        compareCustomerEncryptions(BlobInfoTest.CUSTOMER_ENCRYPTION, CustomerEncryption.fromPb(BlobInfoTest.CUSTOMER_ENCRYPTION.toPb()));
        compareBlobs(BlobInfoTest.BLOB_INFO, BlobInfo.fromPb(BlobInfoTest.BLOB_INFO.toPb()));
        BlobInfo blobInfo = BlobInfo.newBuilder(BlobId.of("b", "n")).build();
        compareBlobs(blobInfo, BlobInfo.fromPb(blobInfo.toPb()));
        StorageObject object = new StorageObject().setName("n/").setBucket("b").setSize(BigInteger.ZERO).set("isDirectory", true);
        blobInfo = BlobInfo.fromPb(object);
        Assert.assertEquals("b", blobInfo.getBucket());
        Assert.assertEquals("n/", blobInfo.getName());
        Assert.assertNull(blobInfo.getAcl());
        Assert.assertNull(blobInfo.getComponentCount());
        Assert.assertNull(blobInfo.getContentType());
        Assert.assertNull(blobInfo.getCacheControl());
        Assert.assertNull(blobInfo.getContentDisposition());
        Assert.assertNull(blobInfo.getContentEncoding());
        Assert.assertNull(blobInfo.getContentLanguage());
        Assert.assertNull(blobInfo.getCustomerEncryption());
        Assert.assertNull(blobInfo.getCrc32c());
        Assert.assertNull(blobInfo.getCrc32cToHexString());
        Assert.assertNull(blobInfo.getCreateTime());
        Assert.assertNull(blobInfo.getDeleteTime());
        Assert.assertNull(blobInfo.getEtag());
        Assert.assertNull(blobInfo.getGeneration());
        Assert.assertNull(blobInfo.getGeneratedId());
        Assert.assertNull(blobInfo.getMd5());
        Assert.assertNull(blobInfo.getMd5ToHexString());
        Assert.assertNull(blobInfo.getMediaLink());
        Assert.assertNull(blobInfo.getMetadata());
        Assert.assertNull(blobInfo.getMetageneration());
        Assert.assertNull(blobInfo.getOwner());
        Assert.assertNull(blobInfo.getSelfLink());
        Assert.assertEquals(0L, ((long) (blobInfo.getSize())));
        Assert.assertNull(blobInfo.getUpdateTime());
        Assert.assertNull(blobInfo.getStorageClass());
        Assert.assertNull(blobInfo.getKmsKeyName());
        Assert.assertNull(blobInfo.getEventBasedHold());
        Assert.assertNull(blobInfo.getTemporaryHold());
        Assert.assertNull(blobInfo.getRetentionExpirationTime());
        Assert.assertTrue(blobInfo.isDirectory());
    }

    @Test
    public void testBlobId() {
        Assert.assertEquals(BlobId.of("b", "n", BlobInfoTest.GENERATION), BlobInfoTest.BLOB_INFO.getBlobId());
    }
}

