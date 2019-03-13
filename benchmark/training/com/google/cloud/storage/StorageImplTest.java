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


import BlobInfo.Builder;
import BlobInfo.INFO_TO_PB_FUNCTION;
import BucketInfo.TO_PB_FUNCTION;
import HttpMethod.GET;
import HttpMethod.POST;
import HttpMethod.PUT;
import Role.OWNER;
import Role.READER;
import Storage.BlobField.CONTENT_TYPE;
import Storage.BlobField.CRC32C;
import Storage.BlobField.MD5HASH;
import Storage.BlobGetOption;
import Storage.BlobListOption;
import Storage.BucketField.ACL;
import Storage.BucketField.LOCATION;
import Storage.BucketGetOption;
import Storage.BucketListOption;
import Storage.BucketTargetOption;
import Storage.ComposeRequest;
import Storage.PredefinedAcl.PRIVATE;
import Storage.SignUrlOption;
import StorageRpc.Option;
import StorageRpc.Option.CUSTOMER_SUPPLIED_KEY;
import StorageRpc.Option.DELIMITER;
import StorageRpc.Option.IF_DISABLE_GZIP_CONTENT;
import StorageRpc.Option.IF_GENERATION_MATCH;
import StorageRpc.Option.IF_METAGENERATION_MATCH;
import StorageRpc.Option.IF_SOURCE_GENERATION_MATCH;
import StorageRpc.Option.IF_SOURCE_METAGENERATION_MATCH;
import StorageRpc.Option.KMS_KEY_NAME;
import StorageRpc.Option.MAX_RESULTS;
import StorageRpc.Option.PREDEFINED_ACL;
import StorageRpc.Option.PREFIX;
import StorageRpc.Option.USER_PROJECT;
import StorageRpc.Option.VERSIONS;
import StorageRpc.RewriteRequest;
import StorageRpc.RewriteResponse;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.core.ApiClock;
import com.google.api.gax.paging.Page;
import com.google.api.services.storage.model.Policy.Bindings;
import com.google.api.services.storage.model.StorageObject;
import com.google.api.services.storage.model.TestIamPermissionsResponse;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.Identity;
import com.google.cloud.Policy;
import com.google.cloud.ReadChannel;
import com.google.cloud.ServiceOptions;
import com.google.cloud.Tuple;
import com.google.cloud.WriteChannel;
import com.google.cloud.storage.Acl.Project.ProjectRole;
import com.google.cloud.storage.Acl.User;
import com.google.cloud.storage.Storage.BlobSourceOption;
import com.google.cloud.storage.Storage.BlobTargetOption;
import com.google.cloud.storage.Storage.BlobWriteOption;
import com.google.cloud.storage.Storage.BucketSourceOption;
import com.google.cloud.storage.Storage.CopyRequest;
import com.google.cloud.storage.spi.StorageRpcFactory;
import com.google.cloud.storage.spi.v1.RpcBatch;
import com.google.cloud.storage.spi.v1.StorageRpc;
import com.google.cloud.storage.testing.ApiPolicyMatcher;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.io.BaseEncoding;
import com.google.common.net.UrlEscapers;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.crypto.spec.SecretKeySpec;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class StorageImplTest {
    private static final String BUCKET_NAME1 = "b1";

    private static final String BUCKET_NAME2 = "b2";

    private static final String BUCKET_NAME3 = "b3";

    private static final String BLOB_NAME1 = "n1";

    private static final String BLOB_NAME2 = "n2";

    private static final String BLOB_NAME3 = "n3";

    private static final byte[] BLOB_CONTENT = new byte[]{ 13, 14, 10, 13 };

    private static final byte[] BLOB_SUB_CONTENT = new byte[]{ 14, 10 };

    private static final String CONTENT_MD5 = "O1R4G1HJSDUISJjoIYmVhQ==";

    private static final String CONTENT_CRC32C = "9N3EPQ==";

    private static final String SUB_CONTENT_MD5 = "5e7c7CdasUiOn3BO560jPg==";

    private static final String SUB_CONTENT_CRC32C = "bljNYA==";

    private static final int DEFAULT_CHUNK_SIZE = (2 * 1024) * 1024;

    private static final String BASE64_KEY = "JVzfVl8NLD9FjedFuStegjRfES5ll5zc59CIXw572OA=";

    private static final Key KEY = new SecretKeySpec(BaseEncoding.base64().decode(StorageImplTest.BASE64_KEY), "AES256");

    private static final String KMS_KEY_NAME = "projects/gcloud-devel/locations/us/keyRings/gcs_kms_key_ring_us/cryptoKeys/key";

    private static final Long RETENTION_PERIOD = 10L;

    private static final String USER_PROJECT = "test-project";

    // BucketInfo objects
    private static final BucketInfo BUCKET_INFO1 = BucketInfo.newBuilder(StorageImplTest.BUCKET_NAME1).setMetageneration(42L).build();

    private static final BucketInfo BUCKET_INFO2 = BucketInfo.newBuilder(StorageImplTest.BUCKET_NAME2).build();

    private static final BucketInfo BUCKET_INFO3 = BucketInfo.newBuilder(StorageImplTest.BUCKET_NAME3).setRetentionPeriod(StorageImplTest.RETENTION_PERIOD).setRetentionPolicyIsLocked(true).setMetageneration(42L).build();

    // BlobInfo objects
    private static final BlobInfo BLOB_INFO1 = BlobInfo.newBuilder(StorageImplTest.BUCKET_NAME1, StorageImplTest.BLOB_NAME1, 24L).setMetageneration(42L).setContentType("application/json").setMd5("md5string").build();

    private static final BlobInfo BLOB_INFO2 = BlobInfo.newBuilder(StorageImplTest.BUCKET_NAME1, StorageImplTest.BLOB_NAME2).build();

    private static final BlobInfo BLOB_INFO3 = BlobInfo.newBuilder(StorageImplTest.BUCKET_NAME1, StorageImplTest.BLOB_NAME3).build();

    // Empty StorageRpc options
    private static final Map<StorageRpc.Option, ?> EMPTY_RPC_OPTIONS = ImmutableMap.of();

    // Bucket target options
    private static final BucketTargetOption BUCKET_TARGET_METAGENERATION = BucketTargetOption.metagenerationMatch();

    private static final BucketTargetOption BUCKET_TARGET_PREDEFINED_ACL = BucketTargetOption.predefinedAcl(PRIVATE);

    private static final BucketTargetOption BUCKET_TARGET_USER_PROJECT = BucketTargetOption.userProject(StorageImplTest.USER_PROJECT);

    private static final Map<StorageRpc.Option, ?> BUCKET_TARGET_OPTIONS = ImmutableMap.of(IF_METAGENERATION_MATCH, StorageImplTest.BUCKET_INFO1.getMetageneration(), PREDEFINED_ACL, StorageImplTest.BUCKET_TARGET_PREDEFINED_ACL.getValue());

    private static final Map<StorageRpc.Option, ?> BUCKET_TARGET_OPTIONS_LOCK_RETENTION_POLICY = ImmutableMap.of(IF_METAGENERATION_MATCH, StorageImplTest.BUCKET_INFO3.getMetageneration(), StorageRpc.Option.USER_PROJECT, StorageImplTest.USER_PROJECT);

    // Blob target options (create, update, compose)
    private static final BlobTargetOption BLOB_TARGET_GENERATION = BlobTargetOption.generationMatch();

    private static final BlobTargetOption BLOB_TARGET_METAGENERATION = BlobTargetOption.metagenerationMatch();

    private static final BlobTargetOption BLOB_TARGET_DISABLE_GZIP_CONTENT = BlobTargetOption.disableGzipContent();

    private static final BlobTargetOption BLOB_TARGET_NOT_EXIST = BlobTargetOption.doesNotExist();

    private static final BlobTargetOption BLOB_TARGET_PREDEFINED_ACL = BlobTargetOption.predefinedAcl(PRIVATE);

    private static final Map<StorageRpc.Option, ?> BLOB_TARGET_OPTIONS_CREATE = ImmutableMap.of(IF_METAGENERATION_MATCH, StorageImplTest.BLOB_INFO1.getMetageneration(), IF_GENERATION_MATCH, 0L, PREDEFINED_ACL, StorageImplTest.BUCKET_TARGET_PREDEFINED_ACL.getValue());

    private static final Map<StorageRpc.Option, ?> BLOB_TARGET_OPTIONS_CREATE_DISABLE_GZIP_CONTENT = ImmutableMap.of(IF_DISABLE_GZIP_CONTENT, true);

    private static final Map<StorageRpc.Option, ?> BLOB_TARGET_OPTIONS_UPDATE = ImmutableMap.of(IF_METAGENERATION_MATCH, StorageImplTest.BLOB_INFO1.getMetageneration(), PREDEFINED_ACL, StorageImplTest.BUCKET_TARGET_PREDEFINED_ACL.getValue());

    private static final Map<StorageRpc.Option, ?> BLOB_TARGET_OPTIONS_COMPOSE = ImmutableMap.of(IF_GENERATION_MATCH, StorageImplTest.BLOB_INFO1.getGeneration(), IF_METAGENERATION_MATCH, StorageImplTest.BLOB_INFO1.getMetageneration());

    // Blob write options (create, writer)
    private static final BlobWriteOption BLOB_WRITE_METAGENERATION = BlobWriteOption.metagenerationMatch();

    private static final BlobWriteOption BLOB_WRITE_NOT_EXIST = BlobWriteOption.doesNotExist();

    private static final BlobWriteOption BLOB_WRITE_PREDEFINED_ACL = BlobWriteOption.predefinedAcl(PRIVATE);

    private static final BlobWriteOption BLOB_WRITE_MD5_HASH = BlobWriteOption.md5Match();

    private static final BlobWriteOption BLOB_WRITE_CRC2C = BlobWriteOption.crc32cMatch();

    // Bucket get/source options
    private static final BucketSourceOption BUCKET_SOURCE_METAGENERATION = BucketSourceOption.metagenerationMatch(StorageImplTest.BUCKET_INFO1.getMetageneration());

    private static final Map<StorageRpc.Option, ?> BUCKET_SOURCE_OPTIONS = ImmutableMap.of(IF_METAGENERATION_MATCH, StorageImplTest.BUCKET_SOURCE_METAGENERATION.getValue());

    private static final BucketGetOption BUCKET_GET_METAGENERATION = BucketGetOption.metagenerationMatch(StorageImplTest.BUCKET_INFO1.getMetageneration());

    private static final BucketGetOption BUCKET_GET_FIELDS = BucketGetOption.fields(LOCATION, Storage.BucketField.ACL);

    private static final BucketGetOption BUCKET_GET_EMPTY_FIELDS = BucketGetOption.fields();

    private static final Map<StorageRpc.Option, ?> BUCKET_GET_OPTIONS = ImmutableMap.of(IF_METAGENERATION_MATCH, StorageImplTest.BUCKET_SOURCE_METAGENERATION.getValue());

    // Blob get/source options
    private static final BlobGetOption BLOB_GET_METAGENERATION = BlobGetOption.metagenerationMatch(StorageImplTest.BLOB_INFO1.getMetageneration());

    private static final BlobGetOption BLOB_GET_GENERATION = BlobGetOption.generationMatch(StorageImplTest.BLOB_INFO1.getGeneration());

    private static final BlobGetOption BLOB_GET_GENERATION_FROM_BLOB_ID = BlobGetOption.generationMatch();

    private static final BlobGetOption BLOB_GET_FIELDS = BlobGetOption.fields(CONTENT_TYPE, CRC32C);

    private static final BlobGetOption BLOB_GET_EMPTY_FIELDS = BlobGetOption.fields();

    private static final Map<StorageRpc.Option, ?> BLOB_GET_OPTIONS = ImmutableMap.of(IF_METAGENERATION_MATCH, StorageImplTest.BLOB_GET_METAGENERATION.getValue(), IF_GENERATION_MATCH, StorageImplTest.BLOB_GET_GENERATION.getValue());

    private static final BlobSourceOption BLOB_SOURCE_METAGENERATION = BlobSourceOption.metagenerationMatch(StorageImplTest.BLOB_INFO1.getMetageneration());

    private static final BlobSourceOption BLOB_SOURCE_GENERATION = BlobSourceOption.generationMatch(StorageImplTest.BLOB_INFO1.getGeneration());

    private static final BlobSourceOption BLOB_SOURCE_GENERATION_FROM_BLOB_ID = BlobSourceOption.generationMatch();

    private static final Map<StorageRpc.Option, ?> BLOB_SOURCE_OPTIONS = ImmutableMap.of(IF_METAGENERATION_MATCH, StorageImplTest.BLOB_SOURCE_METAGENERATION.getValue(), IF_GENERATION_MATCH, StorageImplTest.BLOB_SOURCE_GENERATION.getValue());

    private static final Map<StorageRpc.Option, ?> BLOB_SOURCE_OPTIONS_COPY = ImmutableMap.of(IF_SOURCE_METAGENERATION_MATCH, StorageImplTest.BLOB_SOURCE_METAGENERATION.getValue(), IF_SOURCE_GENERATION_MATCH, StorageImplTest.BLOB_SOURCE_GENERATION.getValue());

    // Bucket list options
    private static final BucketListOption BUCKET_LIST_PAGE_SIZE = BucketListOption.pageSize(42L);

    private static final BucketListOption BUCKET_LIST_PREFIX = BucketListOption.prefix("prefix");

    private static final BucketListOption BUCKET_LIST_FIELDS = BucketListOption.fields(LOCATION, Storage.BucketField.ACL);

    private static final BucketListOption BUCKET_LIST_EMPTY_FIELDS = BucketListOption.fields();

    private static final Map<StorageRpc.Option, ?> BUCKET_LIST_OPTIONS = ImmutableMap.of(MAX_RESULTS, StorageImplTest.BUCKET_LIST_PAGE_SIZE.getValue(), PREFIX, StorageImplTest.BUCKET_LIST_PREFIX.getValue());

    // Blob list options
    private static final BlobListOption BLOB_LIST_PAGE_SIZE = BlobListOption.pageSize(42L);

    private static final BlobListOption BLOB_LIST_PREFIX = BlobListOption.prefix("prefix");

    private static final BlobListOption BLOB_LIST_FIELDS = BlobListOption.fields(CONTENT_TYPE, MD5HASH);

    private static final BlobListOption BLOB_LIST_VERSIONS = BlobListOption.versions(false);

    private static final BlobListOption BLOB_LIST_EMPTY_FIELDS = BlobListOption.fields();

    private static final Map<StorageRpc.Option, ?> BLOB_LIST_OPTIONS = ImmutableMap.of(MAX_RESULTS, StorageImplTest.BLOB_LIST_PAGE_SIZE.getValue(), PREFIX, StorageImplTest.BLOB_LIST_PREFIX.getValue(), VERSIONS, StorageImplTest.BLOB_LIST_VERSIONS.getValue());

    // ACLs
    private static final Acl ACL = Acl.of(User.ofAllAuthenticatedUsers(), OWNER);

    private static final Acl OTHER_ACL = Acl.of(new com.google.cloud.storage.Acl.Project(ProjectRole.OWNERS, "p"), READER);

    // Customer supplied encryption key options
    private static final Map<StorageRpc.Option, ?> ENCRYPTION_KEY_OPTIONS = ImmutableMap.of(CUSTOMER_SUPPLIED_KEY, StorageImplTest.BASE64_KEY);

    // Customer managed encryption key options
    private static final Map<StorageRpc.Option, ?> KMS_KEY_NAME_OPTIONS = ImmutableMap.of(StorageRpc.Option.KMS_KEY_NAME, StorageImplTest.KMS_KEY_NAME);

    // IAM policies
    private static final String POLICY_ETAG1 = "CAE=";

    private static final String POLICY_ETAG2 = "CAI=";

    private static final Policy LIB_POLICY1 = Policy.newBuilder().addIdentity(StorageRoles.objectViewer(), Identity.allUsers()).addIdentity(StorageRoles.objectAdmin(), Identity.user("test1@gmail.com"), Identity.user("test2@gmail.com")).setEtag(StorageImplTest.POLICY_ETAG1).build();

    private static final ServiceAccount SERVICE_ACCOUNT = ServiceAccount.of("test@google.com");

    private static final Policy API_POLICY1 = new com.google.api.services.storage.model.Policy().setBindings(ImmutableList.of(new Bindings().setMembers(ImmutableList.of("allUsers")).setRole("roles/storage.objectViewer"), new Bindings().setMembers(ImmutableList.of("user:test1@gmail.com", "user:test2@gmail.com")).setRole("roles/storage.objectAdmin"))).setEtag(StorageImplTest.POLICY_ETAG1);

    private static final String PRIVATE_KEY_STRING = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoG" + ((((((((("BAL2xolH1zrISQ8+GzOV29BNjjzq4/HIP8Psd1+cZb81vDklSF+95wB250MSE0BDc81pvIMwj5OmIfLg1NY6uB" + "1xavOPpVdx1z664AGc/BEJ1zInXGXaQ6s+SxGenVq40Yws57gikQGMZjttpf1Qbz4DjkxsbRoeaRHn06n9pH1e") + "jAgMBAAECgYEAkWcm0AJF5LMhbWKbjkxm/LG06UNApkHX6vTOOOODkonM/qDBnhvKCj8Tan+PaU2j7679Cd19q") + "xCm4SBQJET7eBhqLD9L2j9y0h2YUQnLbISaqUS1/EXcr2C1Lf9VCEn1y/GYuDYqs85rGoQ4ZYfM9ClROSq86fH") + "+cbIIssqJqukCQQD18LjfJz/ichFeli5/l1jaFid2XoCH3T6TVuuysszVx68fh60gSIxEF/0X2xB+wuPxTP4IQ") + "+t8tD/ktd232oWXAkEAxXPych2QBHePk9/lek4tOkKBgfnDzex7S/pI0G1vpB3VmzBbCsokn9lpOv7JV8071GD") + "lW/7R6jlLfpQy3hN31QJAE10osSk99m5Uv8XDU3hvHnywDrnSFOBulNs7I47AYfSe7TSZhPkxUgsxejddTR27J") + "LyTI8N1PxRSE4feNSOXcQJAMMKJRJT4U6IS2rmXubREhvaVdLtxFxEnAYQ1JwNfZm/XqBMw6GEy2iaeTetNXVl") + "ZRQEIoscyn1y2v/No/F5iYQJBAKBOGASoQcBjGTOg/H/SfcE8QVNsKEpthRrs6CkpT80aZ/AV+ksfoIf2zw2M3") + "mAHfrO+TBLdz4sicuFQvlN9SEc=");

    private static final String PUBLIC_KEY_STRING = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC9saJR9c6y" + ("EkPPhszldvQTY486uPxyD/D7HdfnGW/Nbw5JUhfvecAdudDEhNAQ3PNabyDMI+TpiHy4NTWOrgdcWrzj6VXcdc" + "+uuABnPwRCdcyJ1xl2kOrPksRnp1auNGMLOe4IpEBjGY7baX9UG8+A45MbG0aHmkR59Op/aR9XowIDAQAB");

    private static final ApiClock TIME_SOURCE = new ApiClock() {
        @Override
        public long nanoTime() {
            return 42000000000L;
        }

        @Override
        public long millisTime() {
            return 42000L;
        }
    };

    private static final String ACCOUNT = "account";

    private static PrivateKey privateKey;

    private static PublicKey publicKey;

    private StorageOptions options;

    private StorageRpcFactory rpcFactoryMock;

    private StorageRpc storageRpcMock;

    private Storage storage;

    private Blob expectedBlob1;

    private Blob expectedBlob2;

    private Blob expectedBlob3;

    private Bucket expectedBucket1;

    private Bucket expectedBucket2;

    private Bucket expectedBucket3;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testGetOptions() {
        EasyMock.replay(storageRpcMock);
        initializeService();
        Assert.assertSame(options, storage.getOptions());
    }

    @Test
    public void testCreateBucket() {
        EasyMock.expect(storageRpcMock.create(StorageImplTest.BUCKET_INFO1.toPb(), StorageImplTest.EMPTY_RPC_OPTIONS)).andReturn(StorageImplTest.BUCKET_INFO1.toPb());
        EasyMock.replay(storageRpcMock);
        initializeService();
        Bucket bucket = storage.create(StorageImplTest.BUCKET_INFO1);
        Assert.assertEquals(expectedBucket1, bucket);
    }

    @Test
    public void testCreateBucketWithOptions() {
        EasyMock.expect(storageRpcMock.create(StorageImplTest.BUCKET_INFO1.toPb(), StorageImplTest.BUCKET_TARGET_OPTIONS)).andReturn(StorageImplTest.BUCKET_INFO1.toPb());
        EasyMock.replay(storageRpcMock);
        initializeService();
        Bucket bucket = storage.create(StorageImplTest.BUCKET_INFO1, StorageImplTest.BUCKET_TARGET_METAGENERATION, StorageImplTest.BUCKET_TARGET_PREDEFINED_ACL);
        Assert.assertEquals(expectedBucket1, bucket);
    }

    @Test
    public void testCreateBlob() throws IOException {
        Capture<ByteArrayInputStream> capturedStream = Capture.newInstance();
        EasyMock.expect(storageRpcMock.create(EasyMock.eq(StorageImplTest.BLOB_INFO1.toBuilder().setMd5(StorageImplTest.CONTENT_MD5).setCrc32c(StorageImplTest.CONTENT_CRC32C).build().toPb()), EasyMock.capture(capturedStream), EasyMock.eq(StorageImplTest.EMPTY_RPC_OPTIONS))).andReturn(StorageImplTest.BLOB_INFO1.toPb());
        EasyMock.replay(storageRpcMock);
        initializeService();
        Blob blob = storage.create(StorageImplTest.BLOB_INFO1, StorageImplTest.BLOB_CONTENT);
        Assert.assertEquals(expectedBlob1, blob);
        ByteArrayInputStream byteStream = capturedStream.getValue();
        byte[] streamBytes = new byte[StorageImplTest.BLOB_CONTENT.length];
        Assert.assertEquals(StorageImplTest.BLOB_CONTENT.length, byteStream.read(streamBytes));
        Assert.assertArrayEquals(StorageImplTest.BLOB_CONTENT, streamBytes);
        Assert.assertEquals((-1), byteStream.read(streamBytes));
    }

    @Test
    public void testCreateBlobWithSubArrayFromByteArray() throws IOException {
        Capture<ByteArrayInputStream> capturedStream = Capture.newInstance();
        EasyMock.expect(storageRpcMock.create(EasyMock.eq(StorageImplTest.BLOB_INFO1.toBuilder().setMd5(StorageImplTest.SUB_CONTENT_MD5).setCrc32c(StorageImplTest.SUB_CONTENT_CRC32C).build().toPb()), EasyMock.capture(capturedStream), EasyMock.eq(StorageImplTest.EMPTY_RPC_OPTIONS))).andReturn(StorageImplTest.BLOB_INFO1.toPb());
        EasyMock.replay(storageRpcMock);
        initializeService();
        Blob blob = storage.create(StorageImplTest.BLOB_INFO1, StorageImplTest.BLOB_CONTENT, 1, 2);
        Assert.assertEquals(expectedBlob1, blob);
        ByteArrayInputStream byteStream = capturedStream.getValue();
        byte[] streamBytes = new byte[StorageImplTest.BLOB_SUB_CONTENT.length];
        Assert.assertEquals(StorageImplTest.BLOB_SUB_CONTENT.length, byteStream.read(streamBytes));
        Assert.assertArrayEquals(StorageImplTest.BLOB_SUB_CONTENT, streamBytes);
        Assert.assertEquals((-1), byteStream.read(streamBytes));
    }

    @Test
    public void testCreateBlobRetry() throws IOException {
        Capture<ByteArrayInputStream> capturedStream1 = Capture.newInstance();
        Capture<ByteArrayInputStream> capturedStream2 = Capture.newInstance();
        StorageObject storageObject = StorageImplTest.BLOB_INFO1.toBuilder().setMd5(StorageImplTest.CONTENT_MD5).setCrc32c(StorageImplTest.CONTENT_CRC32C).build().toPb();
        EasyMock.expect(storageRpcMock.create(EasyMock.eq(storageObject), EasyMock.capture(capturedStream1), EasyMock.eq(StorageImplTest.EMPTY_RPC_OPTIONS))).andThrow(new StorageException(500, "internalError")).once();
        EasyMock.expect(storageRpcMock.create(EasyMock.eq(storageObject), EasyMock.capture(capturedStream2), EasyMock.eq(StorageImplTest.EMPTY_RPC_OPTIONS))).andReturn(StorageImplTest.BLOB_INFO1.toPb());
        EasyMock.replay(storageRpcMock);
        storage = options.toBuilder().setRetrySettings(ServiceOptions.getDefaultRetrySettings()).build().getService();
        initializeServiceDependentObjects();
        Blob blob = storage.create(StorageImplTest.BLOB_INFO1, StorageImplTest.BLOB_CONTENT);
        Assert.assertEquals(expectedBlob1, blob);
        ByteArrayInputStream byteStream = capturedStream1.getValue();
        byte[] streamBytes = new byte[StorageImplTest.BLOB_CONTENT.length];
        Assert.assertEquals(StorageImplTest.BLOB_CONTENT.length, byteStream.read(streamBytes));
        Assert.assertArrayEquals(StorageImplTest.BLOB_CONTENT, streamBytes);
        Assert.assertEquals((-1), byteStream.read(streamBytes));
        ByteArrayInputStream byteStream2 = capturedStream2.getValue();
        byte[] streamBytes2 = new byte[StorageImplTest.BLOB_CONTENT.length];
        Assert.assertEquals(StorageImplTest.BLOB_CONTENT.length, byteStream2.read(streamBytes2));
        Assert.assertArrayEquals(StorageImplTest.BLOB_CONTENT, streamBytes2);
        Assert.assertEquals((-1), byteStream.read(streamBytes2));
    }

    @Test
    public void testCreateEmptyBlob() throws IOException {
        Capture<ByteArrayInputStream> capturedStream = Capture.newInstance();
        EasyMock.expect(storageRpcMock.create(EasyMock.eq(StorageImplTest.BLOB_INFO1.toBuilder().setMd5("1B2M2Y8AsgTpgAmY7PhCfg==").setCrc32c("AAAAAA==").build().toPb()), EasyMock.capture(capturedStream), EasyMock.eq(StorageImplTest.EMPTY_RPC_OPTIONS))).andReturn(StorageImplTest.BLOB_INFO1.toPb());
        EasyMock.replay(storageRpcMock);
        initializeService();
        Blob blob = storage.create(StorageImplTest.BLOB_INFO1);
        Assert.assertEquals(expectedBlob1, blob);
        ByteArrayInputStream byteStream = capturedStream.getValue();
        byte[] streamBytes = new byte[StorageImplTest.BLOB_CONTENT.length];
        Assert.assertEquals((-1), byteStream.read(streamBytes));
    }

    @Test
    public void testCreateBlobWithOptions() throws IOException {
        Capture<ByteArrayInputStream> capturedStream = Capture.newInstance();
        EasyMock.expect(storageRpcMock.create(EasyMock.eq(StorageImplTest.BLOB_INFO1.toBuilder().setMd5(StorageImplTest.CONTENT_MD5).setCrc32c(StorageImplTest.CONTENT_CRC32C).build().toPb()), EasyMock.capture(capturedStream), EasyMock.eq(StorageImplTest.BLOB_TARGET_OPTIONS_CREATE))).andReturn(StorageImplTest.BLOB_INFO1.toPb());
        EasyMock.replay(storageRpcMock);
        initializeService();
        Blob blob = storage.create(StorageImplTest.BLOB_INFO1, StorageImplTest.BLOB_CONTENT, StorageImplTest.BLOB_TARGET_METAGENERATION, StorageImplTest.BLOB_TARGET_NOT_EXIST, StorageImplTest.BLOB_TARGET_PREDEFINED_ACL);
        Assert.assertEquals(expectedBlob1, blob);
        ByteArrayInputStream byteStream = capturedStream.getValue();
        byte[] streamBytes = new byte[StorageImplTest.BLOB_CONTENT.length];
        Assert.assertEquals(StorageImplTest.BLOB_CONTENT.length, byteStream.read(streamBytes));
        Assert.assertArrayEquals(StorageImplTest.BLOB_CONTENT, streamBytes);
        Assert.assertEquals((-1), byteStream.read(streamBytes));
    }

    @Test
    public void testCreateBlobWithDisabledGzipContent() throws IOException {
        Capture<ByteArrayInputStream> capturedStream = Capture.newInstance();
        EasyMock.expect(storageRpcMock.create(EasyMock.eq(StorageImplTest.BLOB_INFO1.toBuilder().setMd5(StorageImplTest.CONTENT_MD5).setCrc32c(StorageImplTest.CONTENT_CRC32C).build().toPb()), EasyMock.capture(capturedStream), EasyMock.eq(StorageImplTest.BLOB_TARGET_OPTIONS_CREATE_DISABLE_GZIP_CONTENT))).andReturn(StorageImplTest.BLOB_INFO1.toPb());
        EasyMock.replay(storageRpcMock);
        initializeService();
        Blob blob = storage.create(StorageImplTest.BLOB_INFO1, StorageImplTest.BLOB_CONTENT, StorageImplTest.BLOB_TARGET_DISABLE_GZIP_CONTENT);
        Assert.assertEquals(expectedBlob1, blob);
        ByteArrayInputStream byteStream = capturedStream.getValue();
        byte[] streamBytes = new byte[StorageImplTest.BLOB_CONTENT.length];
        Assert.assertEquals(StorageImplTest.BLOB_CONTENT.length, byteStream.read(streamBytes));
        Assert.assertArrayEquals(StorageImplTest.BLOB_CONTENT, streamBytes);
        Assert.assertEquals((-1), byteStream.read(streamBytes));
    }

    @Test
    public void testCreateBlobWithEncryptionKey() throws IOException {
        Capture<ByteArrayInputStream> capturedStream = Capture.newInstance();
        EasyMock.expect(storageRpcMock.create(EasyMock.eq(StorageImplTest.BLOB_INFO1.toBuilder().setMd5(StorageImplTest.CONTENT_MD5).setCrc32c(StorageImplTest.CONTENT_CRC32C).build().toPb()), EasyMock.capture(capturedStream), EasyMock.eq(StorageImplTest.ENCRYPTION_KEY_OPTIONS))).andReturn(StorageImplTest.BLOB_INFO1.toPb()).times(2);
        EasyMock.replay(storageRpcMock);
        initializeService();
        Blob blob = storage.create(StorageImplTest.BLOB_INFO1, StorageImplTest.BLOB_CONTENT, BlobTargetOption.encryptionKey(StorageImplTest.KEY));
        Assert.assertEquals(expectedBlob1, blob);
        ByteArrayInputStream byteStream = capturedStream.getValue();
        byte[] streamBytes = new byte[StorageImplTest.BLOB_CONTENT.length];
        Assert.assertEquals(StorageImplTest.BLOB_CONTENT.length, byteStream.read(streamBytes));
        Assert.assertArrayEquals(StorageImplTest.BLOB_CONTENT, streamBytes);
        Assert.assertEquals((-1), byteStream.read(streamBytes));
        blob = storage.create(StorageImplTest.BLOB_INFO1, StorageImplTest.BLOB_CONTENT, BlobTargetOption.encryptionKey(StorageImplTest.BASE64_KEY));
        Assert.assertEquals(expectedBlob1, blob);
        byteStream = capturedStream.getValue();
        streamBytes = new byte[StorageImplTest.BLOB_CONTENT.length];
        Assert.assertEquals(StorageImplTest.BLOB_CONTENT.length, byteStream.read(streamBytes));
        Assert.assertArrayEquals(StorageImplTest.BLOB_CONTENT, streamBytes);
        Assert.assertEquals((-1), byteStream.read(streamBytes));
    }

    @Test
    public void testCreateBlobWithKmsKeyName() throws IOException {
        Capture<ByteArrayInputStream> capturedStream = Capture.newInstance();
        EasyMock.expect(storageRpcMock.create(EasyMock.eq(StorageImplTest.BLOB_INFO1.toBuilder().setMd5(StorageImplTest.CONTENT_MD5).setCrc32c(StorageImplTest.CONTENT_CRC32C).build().toPb()), EasyMock.capture(capturedStream), EasyMock.eq(StorageImplTest.KMS_KEY_NAME_OPTIONS))).andReturn(StorageImplTest.BLOB_INFO1.toPb()).times(2);
        EasyMock.replay(storageRpcMock);
        initializeService();
        Blob blob = storage.create(StorageImplTest.BLOB_INFO1, StorageImplTest.BLOB_CONTENT, BlobTargetOption.kmsKeyName(StorageImplTest.KMS_KEY_NAME));
        Assert.assertEquals(expectedBlob1, blob);
        ByteArrayInputStream byteStream = capturedStream.getValue();
        byte[] streamBytes = new byte[StorageImplTest.BLOB_CONTENT.length];
        Assert.assertEquals(StorageImplTest.BLOB_CONTENT.length, byteStream.read(streamBytes));
        Assert.assertArrayEquals(StorageImplTest.BLOB_CONTENT, streamBytes);
        Assert.assertEquals((-1), byteStream.read(streamBytes));
        blob = storage.create(StorageImplTest.BLOB_INFO1, StorageImplTest.BLOB_CONTENT, BlobTargetOption.kmsKeyName(StorageImplTest.KMS_KEY_NAME));
        Assert.assertEquals(expectedBlob1, blob);
        byteStream = capturedStream.getValue();
        streamBytes = new byte[StorageImplTest.BLOB_CONTENT.length];
        Assert.assertEquals(StorageImplTest.BLOB_CONTENT.length, byteStream.read(streamBytes));
        Assert.assertArrayEquals(StorageImplTest.BLOB_CONTENT, streamBytes);
        Assert.assertEquals((-1), byteStream.read(streamBytes));
    }

    @Test
    public void testCreateBlobFromStream() throws IOException {
        Capture<ByteArrayInputStream> capturedStream = Capture.newInstance();
        ByteArrayInputStream fileStream = new ByteArrayInputStream(StorageImplTest.BLOB_CONTENT);
        BlobInfo.Builder infoBuilder = StorageImplTest.BLOB_INFO1.toBuilder();
        BlobInfo infoWithHashes = infoBuilder.setMd5(StorageImplTest.CONTENT_MD5).setCrc32c(StorageImplTest.CONTENT_CRC32C).build();
        BlobInfo infoWithoutHashes = infoBuilder.setMd5(null).setCrc32c(null).build();
        EasyMock.expect(storageRpcMock.create(EasyMock.eq(infoWithoutHashes.toPb()), EasyMock.capture(capturedStream), EasyMock.eq(StorageImplTest.EMPTY_RPC_OPTIONS))).andReturn(StorageImplTest.BLOB_INFO1.toPb());
        EasyMock.replay(storageRpcMock);
        initializeService();
        Blob blob = storage.create(infoWithHashes, fileStream);
        Assert.assertEquals(expectedBlob1, blob);
        ByteArrayInputStream byteStream = capturedStream.getValue();
        byte[] streamBytes = new byte[StorageImplTest.BLOB_CONTENT.length];
        Assert.assertEquals(StorageImplTest.BLOB_CONTENT.length, byteStream.read(streamBytes));
        Assert.assertArrayEquals(StorageImplTest.BLOB_CONTENT, streamBytes);
        Assert.assertEquals((-1), byteStream.read(streamBytes));
    }

    @Test
    public void testCreateBlobFromStreamWithEncryptionKey() throws IOException {
        ByteArrayInputStream fileStream = new ByteArrayInputStream(StorageImplTest.BLOB_CONTENT);
        BlobInfo.Builder infoBuilder = StorageImplTest.BLOB_INFO1.toBuilder();
        BlobInfo infoWithHashes = infoBuilder.setMd5(StorageImplTest.CONTENT_MD5).setCrc32c(StorageImplTest.CONTENT_CRC32C).build();
        BlobInfo infoWithoutHashes = infoBuilder.setMd5(null).setCrc32c(null).build();
        EasyMock.expect(storageRpcMock.create(infoWithoutHashes.toPb(), fileStream, StorageImplTest.ENCRYPTION_KEY_OPTIONS)).andReturn(StorageImplTest.BLOB_INFO1.toPb()).times(2);
        EasyMock.replay(storageRpcMock);
        initializeService();
        Blob blob = storage.create(infoWithHashes, fileStream, BlobWriteOption.encryptionKey(StorageImplTest.BASE64_KEY));
        Assert.assertEquals(expectedBlob1, blob);
        blob = storage.create(infoWithHashes, fileStream, BlobWriteOption.encryptionKey(StorageImplTest.BASE64_KEY));
        Assert.assertEquals(expectedBlob1, blob);
    }

    @Test
    public void testCreateBlobFromStreamRetryableException() throws IOException {
        Capture<ByteArrayInputStream> capturedStream = Capture.newInstance();
        ByteArrayInputStream fileStream = new ByteArrayInputStream(StorageImplTest.BLOB_CONTENT);
        BlobInfo.Builder infoBuilder = StorageImplTest.BLOB_INFO1.toBuilder();
        BlobInfo infoWithHashes = infoBuilder.setMd5(StorageImplTest.CONTENT_MD5).setCrc32c(StorageImplTest.CONTENT_CRC32C).build();
        BlobInfo infoWithoutHashes = infoBuilder.setMd5(null).setCrc32c(null).build();
        EasyMock.expect(storageRpcMock.create(EasyMock.eq(infoWithoutHashes.toPb()), EasyMock.capture(capturedStream), EasyMock.eq(StorageImplTest.EMPTY_RPC_OPTIONS))).andThrow(new StorageException(500, "internalError")).once();
        EasyMock.replay(storageRpcMock);
        storage = options.toBuilder().setRetrySettings(ServiceOptions.getDefaultRetrySettings()).build().getService();
        // Even though this exception is retryable, storage.create(BlobInfo, InputStream)
        // shouldn't retry.
        thrown.expect(StorageException.class);
        thrown.expectMessage("internalError");
        storage.create(infoWithHashes, fileStream);
    }

    @Test
    public void testGetBucket() {
        EasyMock.expect(storageRpcMock.get(BucketInfo.of(StorageImplTest.BUCKET_NAME1).toPb(), StorageImplTest.EMPTY_RPC_OPTIONS)).andReturn(StorageImplTest.BUCKET_INFO1.toPb());
        EasyMock.replay(storageRpcMock);
        initializeService();
        Bucket bucket = storage.get(StorageImplTest.BUCKET_NAME1);
        Assert.assertEquals(expectedBucket1, bucket);
    }

    @Test
    public void testGetBucketWithOptions() {
        EasyMock.expect(storageRpcMock.get(BucketInfo.of(StorageImplTest.BUCKET_NAME1).toPb(), StorageImplTest.BUCKET_GET_OPTIONS)).andReturn(StorageImplTest.BUCKET_INFO1.toPb());
        EasyMock.replay(storageRpcMock);
        initializeService();
        Bucket bucket = storage.get(StorageImplTest.BUCKET_NAME1, StorageImplTest.BUCKET_GET_METAGENERATION);
        Assert.assertEquals(expectedBucket1, bucket);
    }

    @Test
    public void testGetBucketWithSelectedFields() {
        Capture<Map<StorageRpc.Option, Object>> capturedOptions = Capture.newInstance();
        EasyMock.expect(storageRpcMock.get(EasyMock.eq(BucketInfo.of(StorageImplTest.BUCKET_NAME1).toPb()), EasyMock.capture(capturedOptions))).andReturn(StorageImplTest.BUCKET_INFO1.toPb());
        EasyMock.replay(storageRpcMock);
        initializeService();
        Bucket bucket = storage.get(StorageImplTest.BUCKET_NAME1, StorageImplTest.BUCKET_GET_METAGENERATION, StorageImplTest.BUCKET_GET_FIELDS);
        Assert.assertEquals(StorageImplTest.BUCKET_GET_METAGENERATION.getValue(), capturedOptions.getValue().get(StorageImplTest.BUCKET_GET_METAGENERATION.getRpcOption()));
        String selector = ((String) (capturedOptions.getValue().get(StorageImplTest.BLOB_GET_FIELDS.getRpcOption())));
        Assert.assertTrue(selector.contains("name"));
        Assert.assertTrue(selector.contains("location"));
        Assert.assertTrue(selector.contains("acl"));
        Assert.assertEquals(17, selector.length());
        Assert.assertEquals(StorageImplTest.BUCKET_INFO1.getName(), bucket.getName());
    }

    @Test
    public void testGetBucketWithEmptyFields() {
        Capture<Map<StorageRpc.Option, Object>> capturedOptions = Capture.newInstance();
        EasyMock.expect(storageRpcMock.get(EasyMock.eq(BucketInfo.of(StorageImplTest.BUCKET_NAME1).toPb()), EasyMock.capture(capturedOptions))).andReturn(StorageImplTest.BUCKET_INFO1.toPb());
        EasyMock.replay(storageRpcMock);
        initializeService();
        Bucket bucket = storage.get(StorageImplTest.BUCKET_NAME1, StorageImplTest.BUCKET_GET_METAGENERATION, StorageImplTest.BUCKET_GET_EMPTY_FIELDS);
        Assert.assertEquals(StorageImplTest.BUCKET_GET_METAGENERATION.getValue(), capturedOptions.getValue().get(StorageImplTest.BUCKET_GET_METAGENERATION.getRpcOption()));
        String selector = ((String) (capturedOptions.getValue().get(StorageImplTest.BLOB_GET_FIELDS.getRpcOption())));
        Assert.assertTrue(selector.contains("name"));
        Assert.assertEquals(4, selector.length());
        Assert.assertEquals(StorageImplTest.BUCKET_INFO1.getName(), bucket.getName());
    }

    @Test
    public void testGetBlob() {
        EasyMock.expect(storageRpcMock.get(BlobId.of(StorageImplTest.BUCKET_NAME1, StorageImplTest.BLOB_NAME1).toPb(), StorageImplTest.EMPTY_RPC_OPTIONS)).andReturn(StorageImplTest.BLOB_INFO1.toPb());
        EasyMock.replay(storageRpcMock);
        initializeService();
        Blob blob = storage.get(StorageImplTest.BUCKET_NAME1, StorageImplTest.BLOB_NAME1);
        Assert.assertEquals(expectedBlob1, blob);
    }

    @Test
    public void testGetBlobWithOptions() {
        EasyMock.expect(storageRpcMock.get(BlobId.of(StorageImplTest.BUCKET_NAME1, StorageImplTest.BLOB_NAME1).toPb(), StorageImplTest.BLOB_GET_OPTIONS)).andReturn(StorageImplTest.BLOB_INFO1.toPb());
        EasyMock.replay(storageRpcMock);
        initializeService();
        Blob blob = storage.get(StorageImplTest.BUCKET_NAME1, StorageImplTest.BLOB_NAME1, StorageImplTest.BLOB_GET_METAGENERATION, StorageImplTest.BLOB_GET_GENERATION);
        Assert.assertEquals(expectedBlob1, blob);
    }

    @Test
    public void testGetBlobWithOptionsFromBlobId() {
        EasyMock.expect(storageRpcMock.get(StorageImplTest.BLOB_INFO1.getBlobId().toPb(), StorageImplTest.BLOB_GET_OPTIONS)).andReturn(StorageImplTest.BLOB_INFO1.toPb());
        EasyMock.replay(storageRpcMock);
        initializeService();
        Blob blob = storage.get(StorageImplTest.BLOB_INFO1.getBlobId(), StorageImplTest.BLOB_GET_METAGENERATION, StorageImplTest.BLOB_GET_GENERATION_FROM_BLOB_ID);
        Assert.assertEquals(expectedBlob1, blob);
    }

    @Test
    public void testGetBlobWithSelectedFields() {
        Capture<Map<StorageRpc.Option, Object>> capturedOptions = Capture.newInstance();
        EasyMock.expect(storageRpcMock.get(EasyMock.eq(BlobId.of(StorageImplTest.BUCKET_NAME1, StorageImplTest.BLOB_NAME1).toPb()), EasyMock.capture(capturedOptions))).andReturn(StorageImplTest.BLOB_INFO1.toPb());
        EasyMock.replay(storageRpcMock);
        initializeService();
        Blob blob = storage.get(StorageImplTest.BUCKET_NAME1, StorageImplTest.BLOB_NAME1, StorageImplTest.BLOB_GET_METAGENERATION, StorageImplTest.BLOB_GET_GENERATION, StorageImplTest.BLOB_GET_FIELDS);
        Assert.assertEquals(StorageImplTest.BLOB_GET_METAGENERATION.getValue(), capturedOptions.getValue().get(StorageImplTest.BLOB_GET_METAGENERATION.getRpcOption()));
        Assert.assertEquals(StorageImplTest.BLOB_GET_GENERATION.getValue(), capturedOptions.getValue().get(StorageImplTest.BLOB_GET_GENERATION.getRpcOption()));
        String selector = ((String) (capturedOptions.getValue().get(StorageImplTest.BLOB_GET_FIELDS.getRpcOption())));
        Assert.assertTrue(selector.contains("bucket"));
        Assert.assertTrue(selector.contains("name"));
        Assert.assertTrue(selector.contains("contentType"));
        Assert.assertTrue(selector.contains("crc32c"));
        Assert.assertEquals(30, selector.length());
        Assert.assertEquals(expectedBlob1, blob);
    }

    @Test
    public void testGetBlobWithEmptyFields() {
        Capture<Map<StorageRpc.Option, Object>> capturedOptions = Capture.newInstance();
        EasyMock.expect(storageRpcMock.get(EasyMock.eq(BlobId.of(StorageImplTest.BUCKET_NAME1, StorageImplTest.BLOB_NAME1).toPb()), EasyMock.capture(capturedOptions))).andReturn(StorageImplTest.BLOB_INFO1.toPb());
        EasyMock.replay(storageRpcMock);
        initializeService();
        Blob blob = storage.get(StorageImplTest.BUCKET_NAME1, StorageImplTest.BLOB_NAME1, StorageImplTest.BLOB_GET_METAGENERATION, StorageImplTest.BLOB_GET_GENERATION, StorageImplTest.BLOB_GET_EMPTY_FIELDS);
        Assert.assertEquals(StorageImplTest.BLOB_GET_METAGENERATION.getValue(), capturedOptions.getValue().get(StorageImplTest.BLOB_GET_METAGENERATION.getRpcOption()));
        Assert.assertEquals(StorageImplTest.BLOB_GET_GENERATION.getValue(), capturedOptions.getValue().get(StorageImplTest.BLOB_GET_GENERATION.getRpcOption()));
        String selector = ((String) (capturedOptions.getValue().get(StorageImplTest.BLOB_GET_FIELDS.getRpcOption())));
        Assert.assertTrue(selector.contains("bucket"));
        Assert.assertTrue(selector.contains("name"));
        Assert.assertEquals(11, selector.length());
        Assert.assertEquals(expectedBlob1, blob);
    }

    @Test
    public void testListBuckets() {
        String cursor = "cursor";
        ImmutableList<BucketInfo> bucketInfoList = ImmutableList.of(StorageImplTest.BUCKET_INFO1, StorageImplTest.BUCKET_INFO2);
        Tuple<String, Iterable<com.google.api.services.storage.model.Bucket>> result = Tuple.of(cursor, Iterables.transform(bucketInfoList, TO_PB_FUNCTION));
        EasyMock.expect(storageRpcMock.list(StorageImplTest.EMPTY_RPC_OPTIONS)).andReturn(result);
        EasyMock.replay(storageRpcMock);
        initializeService();
        ImmutableList<Bucket> bucketList = ImmutableList.of(expectedBucket1, expectedBucket2);
        Page<Bucket> page = storage.list();
        Assert.assertEquals(cursor, page.getNextPageToken());
        Assert.assertArrayEquals(bucketList.toArray(), Iterables.toArray(page.getValues(), Bucket.class));
    }

    @Test
    public void testListBucketsEmpty() {
        EasyMock.expect(storageRpcMock.list(StorageImplTest.EMPTY_RPC_OPTIONS)).andReturn(Tuple.<String, Iterable<com.google.api.services.storage.model.Bucket>>of(null, null));
        EasyMock.replay(storageRpcMock);
        initializeService();
        Page<Bucket> page = storage.list();
        Assert.assertNull(page.getNextPageToken());
        Assert.assertArrayEquals(ImmutableList.of().toArray(), Iterables.toArray(page.getValues(), Bucket.class));
    }

    @Test
    public void testListBucketsWithOptions() {
        String cursor = "cursor";
        ImmutableList<BucketInfo> bucketInfoList = ImmutableList.of(StorageImplTest.BUCKET_INFO1, StorageImplTest.BUCKET_INFO2);
        Tuple<String, Iterable<com.google.api.services.storage.model.Bucket>> result = Tuple.of(cursor, Iterables.transform(bucketInfoList, TO_PB_FUNCTION));
        EasyMock.expect(storageRpcMock.list(StorageImplTest.BUCKET_LIST_OPTIONS)).andReturn(result);
        EasyMock.replay(storageRpcMock);
        initializeService();
        ImmutableList<Bucket> bucketList = ImmutableList.of(expectedBucket1, expectedBucket2);
        Page<Bucket> page = storage.list(StorageImplTest.BUCKET_LIST_PAGE_SIZE, StorageImplTest.BUCKET_LIST_PREFIX);
        Assert.assertEquals(cursor, page.getNextPageToken());
        Assert.assertArrayEquals(bucketList.toArray(), Iterables.toArray(page.getValues(), Bucket.class));
    }

    @Test
    public void testListBucketsWithSelectedFields() {
        String cursor = "cursor";
        Capture<Map<StorageRpc.Option, Object>> capturedOptions = Capture.newInstance();
        ImmutableList<BucketInfo> bucketInfoList = ImmutableList.of(StorageImplTest.BUCKET_INFO1, StorageImplTest.BUCKET_INFO2);
        Tuple<String, Iterable<com.google.api.services.storage.model.Bucket>> result = Tuple.of(cursor, Iterables.transform(bucketInfoList, TO_PB_FUNCTION));
        EasyMock.expect(storageRpcMock.list(EasyMock.capture(capturedOptions))).andReturn(result);
        EasyMock.replay(storageRpcMock);
        initializeService();
        ImmutableList<Bucket> bucketList = ImmutableList.of(expectedBucket1, expectedBucket2);
        Page<Bucket> page = storage.list(StorageImplTest.BUCKET_LIST_FIELDS);
        String selector = ((String) (capturedOptions.getValue().get(StorageImplTest.BUCKET_LIST_FIELDS.getRpcOption())));
        Assert.assertTrue(selector.contains("items("));
        Assert.assertTrue(selector.contains("name"));
        Assert.assertTrue(selector.contains("acl"));
        Assert.assertTrue(selector.contains("location"));
        Assert.assertTrue(selector.contains("nextPageToken"));
        Assert.assertTrue(selector.endsWith(")"));
        Assert.assertEquals(38, selector.length());
        Assert.assertEquals(cursor, page.getNextPageToken());
        Assert.assertArrayEquals(bucketList.toArray(), Iterables.toArray(page.getValues(), Bucket.class));
    }

    @Test
    public void testListBucketsWithEmptyFields() {
        String cursor = "cursor";
        Capture<Map<StorageRpc.Option, Object>> capturedOptions = Capture.newInstance();
        ImmutableList<BucketInfo> bucketInfoList = ImmutableList.of(StorageImplTest.BUCKET_INFO1, StorageImplTest.BUCKET_INFO2);
        Tuple<String, Iterable<com.google.api.services.storage.model.Bucket>> result = Tuple.of(cursor, Iterables.transform(bucketInfoList, TO_PB_FUNCTION));
        EasyMock.expect(storageRpcMock.list(EasyMock.capture(capturedOptions))).andReturn(result);
        EasyMock.replay(storageRpcMock);
        initializeService();
        ImmutableList<Bucket> bucketList = ImmutableList.of(expectedBucket1, expectedBucket2);
        Page<Bucket> page = storage.list(StorageImplTest.BUCKET_LIST_EMPTY_FIELDS);
        String selector = ((String) (capturedOptions.getValue().get(StorageImplTest.BUCKET_LIST_EMPTY_FIELDS.getRpcOption())));
        Assert.assertTrue(selector.contains("items("));
        Assert.assertTrue(selector.contains("name"));
        Assert.assertTrue(selector.contains("nextPageToken"));
        Assert.assertTrue(selector.endsWith(")"));
        Assert.assertEquals(25, selector.length());
        Assert.assertEquals(cursor, page.getNextPageToken());
        Assert.assertArrayEquals(bucketList.toArray(), Iterables.toArray(page.getValues(), Bucket.class));
    }

    @Test
    public void testListBlobs() {
        String cursor = "cursor";
        ImmutableList<BlobInfo> blobInfoList = ImmutableList.of(StorageImplTest.BLOB_INFO1, StorageImplTest.BLOB_INFO2);
        Tuple<String, Iterable<StorageObject>> result = Tuple.of(cursor, Iterables.transform(blobInfoList, INFO_TO_PB_FUNCTION));
        EasyMock.expect(storageRpcMock.list(StorageImplTest.BUCKET_NAME1, StorageImplTest.EMPTY_RPC_OPTIONS)).andReturn(result);
        EasyMock.replay(storageRpcMock);
        initializeService();
        ImmutableList<Blob> blobList = ImmutableList.of(expectedBlob1, expectedBlob2);
        Page<Blob> page = storage.list(StorageImplTest.BUCKET_NAME1);
        Assert.assertEquals(cursor, page.getNextPageToken());
        Assert.assertArrayEquals(blobList.toArray(), Iterables.toArray(page.getValues(), Blob.class));
    }

    @Test
    public void testListBlobsEmpty() {
        EasyMock.expect(storageRpcMock.list(StorageImplTest.BUCKET_NAME1, StorageImplTest.EMPTY_RPC_OPTIONS)).andReturn(Tuple.<String, Iterable<StorageObject>>of(null, null));
        EasyMock.replay(storageRpcMock);
        initializeService();
        Page<Blob> page = storage.list(StorageImplTest.BUCKET_NAME1);
        Assert.assertNull(page.getNextPageToken());
        Assert.assertArrayEquals(ImmutableList.of().toArray(), Iterables.toArray(page.getValues(), Blob.class));
    }

    @Test
    public void testListBlobsWithOptions() {
        String cursor = "cursor";
        ImmutableList<BlobInfo> blobInfoList = ImmutableList.of(StorageImplTest.BLOB_INFO1, StorageImplTest.BLOB_INFO2);
        Tuple<String, Iterable<StorageObject>> result = Tuple.of(cursor, Iterables.transform(blobInfoList, INFO_TO_PB_FUNCTION));
        EasyMock.expect(storageRpcMock.list(StorageImplTest.BUCKET_NAME1, StorageImplTest.BLOB_LIST_OPTIONS)).andReturn(result);
        EasyMock.replay(storageRpcMock);
        initializeService();
        ImmutableList<Blob> blobList = ImmutableList.of(expectedBlob1, expectedBlob2);
        Page<Blob> page = storage.list(StorageImplTest.BUCKET_NAME1, StorageImplTest.BLOB_LIST_PAGE_SIZE, StorageImplTest.BLOB_LIST_PREFIX, StorageImplTest.BLOB_LIST_VERSIONS);
        Assert.assertEquals(cursor, page.getNextPageToken());
        Assert.assertArrayEquals(blobList.toArray(), Iterables.toArray(page.getValues(), Blob.class));
    }

    @Test
    public void testListBlobsWithSelectedFields() {
        String cursor = "cursor";
        Capture<Map<StorageRpc.Option, Object>> capturedOptions = Capture.newInstance();
        ImmutableList<BlobInfo> blobInfoList = ImmutableList.of(StorageImplTest.BLOB_INFO1, StorageImplTest.BLOB_INFO2);
        Tuple<String, Iterable<StorageObject>> result = Tuple.of(cursor, Iterables.transform(blobInfoList, INFO_TO_PB_FUNCTION));
        EasyMock.expect(storageRpcMock.list(EasyMock.eq(StorageImplTest.BUCKET_NAME1), EasyMock.capture(capturedOptions))).andReturn(result);
        EasyMock.replay(storageRpcMock);
        initializeService();
        ImmutableList<Blob> blobList = ImmutableList.of(expectedBlob1, expectedBlob2);
        Page<Blob> page = storage.list(StorageImplTest.BUCKET_NAME1, StorageImplTest.BLOB_LIST_PAGE_SIZE, StorageImplTest.BLOB_LIST_PREFIX, StorageImplTest.BLOB_LIST_FIELDS);
        Assert.assertEquals(StorageImplTest.BLOB_LIST_PAGE_SIZE.getValue(), capturedOptions.getValue().get(StorageImplTest.BLOB_LIST_PAGE_SIZE.getRpcOption()));
        Assert.assertEquals(StorageImplTest.BLOB_LIST_PREFIX.getValue(), capturedOptions.getValue().get(StorageImplTest.BLOB_LIST_PREFIX.getRpcOption()));
        String selector = ((String) (capturedOptions.getValue().get(StorageImplTest.BLOB_LIST_FIELDS.getRpcOption())));
        Assert.assertTrue(selector.contains("prefixes"));
        Assert.assertTrue(selector.contains("items("));
        Assert.assertTrue(selector.contains("bucket"));
        Assert.assertTrue(selector.contains("name"));
        Assert.assertTrue(selector.contains("contentType"));
        Assert.assertTrue(selector.contains("md5Hash"));
        Assert.assertTrue(selector.contains("nextPageToken"));
        Assert.assertTrue(selector.endsWith(")"));
        Assert.assertEquals(61, selector.length());
        Assert.assertEquals(cursor, page.getNextPageToken());
        Assert.assertArrayEquals(blobList.toArray(), Iterables.toArray(page.getValues(), Blob.class));
    }

    @Test
    public void testListBlobsWithEmptyFields() {
        String cursor = "cursor";
        Capture<Map<StorageRpc.Option, Object>> capturedOptions = Capture.newInstance();
        ImmutableList<BlobInfo> blobInfoList = ImmutableList.of(StorageImplTest.BLOB_INFO1, StorageImplTest.BLOB_INFO2);
        Tuple<String, Iterable<StorageObject>> result = Tuple.of(cursor, Iterables.transform(blobInfoList, INFO_TO_PB_FUNCTION));
        EasyMock.expect(storageRpcMock.list(EasyMock.eq(StorageImplTest.BUCKET_NAME1), EasyMock.capture(capturedOptions))).andReturn(result);
        EasyMock.replay(storageRpcMock);
        initializeService();
        ImmutableList<Blob> blobList = ImmutableList.of(expectedBlob1, expectedBlob2);
        Page<Blob> page = storage.list(StorageImplTest.BUCKET_NAME1, StorageImplTest.BLOB_LIST_PAGE_SIZE, StorageImplTest.BLOB_LIST_PREFIX, StorageImplTest.BLOB_LIST_EMPTY_FIELDS);
        Assert.assertEquals(StorageImplTest.BLOB_LIST_PAGE_SIZE.getValue(), capturedOptions.getValue().get(StorageImplTest.BLOB_LIST_PAGE_SIZE.getRpcOption()));
        Assert.assertEquals(StorageImplTest.BLOB_LIST_PREFIX.getValue(), capturedOptions.getValue().get(StorageImplTest.BLOB_LIST_PREFIX.getRpcOption()));
        String selector = ((String) (capturedOptions.getValue().get(StorageImplTest.BLOB_LIST_EMPTY_FIELDS.getRpcOption())));
        Assert.assertTrue(selector.contains("prefixes"));
        Assert.assertTrue(selector.contains("items("));
        Assert.assertTrue(selector.contains("bucket"));
        Assert.assertTrue(selector.contains("name"));
        Assert.assertTrue(selector.contains("nextPageToken"));
        Assert.assertTrue(selector.endsWith(")"));
        Assert.assertEquals(41, selector.length());
        Assert.assertEquals(cursor, page.getNextPageToken());
        Assert.assertArrayEquals(blobList.toArray(), Iterables.toArray(page.getValues(), Blob.class));
    }

    @Test
    public void testListBlobsCurrentDirectory() {
        String cursor = "cursor";
        Map<StorageRpc.Option, ?> options = ImmutableMap.of(DELIMITER, "/");
        ImmutableList<BlobInfo> blobInfoList = ImmutableList.of(StorageImplTest.BLOB_INFO1, StorageImplTest.BLOB_INFO2);
        Tuple<String, Iterable<StorageObject>> result = Tuple.of(cursor, Iterables.transform(blobInfoList, INFO_TO_PB_FUNCTION));
        EasyMock.expect(storageRpcMock.list(StorageImplTest.BUCKET_NAME1, options)).andReturn(result);
        EasyMock.replay(storageRpcMock);
        initializeService();
        ImmutableList<Blob> blobList = ImmutableList.of(expectedBlob1, expectedBlob2);
        Page<Blob> page = storage.list(StorageImplTest.BUCKET_NAME1, BlobListOption.currentDirectory());
        Assert.assertEquals(cursor, page.getNextPageToken());
        Assert.assertArrayEquals(blobList.toArray(), Iterables.toArray(page.getValues(), Blob.class));
    }

    @Test
    public void testUpdateBucket() {
        BucketInfo updatedBucketInfo = StorageImplTest.BUCKET_INFO1.toBuilder().setIndexPage("some-page").build();
        EasyMock.expect(storageRpcMock.patch(updatedBucketInfo.toPb(), StorageImplTest.EMPTY_RPC_OPTIONS)).andReturn(updatedBucketInfo.toPb());
        EasyMock.replay(storageRpcMock);
        initializeService();
        Bucket bucket = storage.update(updatedBucketInfo);
        Assert.assertEquals(new Bucket(storage, new BucketInfo.BuilderImpl(updatedBucketInfo)), bucket);
    }

    @Test
    public void testUpdateBucketWithOptions() {
        BucketInfo updatedBucketInfo = StorageImplTest.BUCKET_INFO1.toBuilder().setIndexPage("some-page").build();
        EasyMock.expect(storageRpcMock.patch(updatedBucketInfo.toPb(), StorageImplTest.BUCKET_TARGET_OPTIONS)).andReturn(updatedBucketInfo.toPb());
        EasyMock.replay(storageRpcMock);
        initializeService();
        Bucket bucket = storage.update(updatedBucketInfo, StorageImplTest.BUCKET_TARGET_METAGENERATION, StorageImplTest.BUCKET_TARGET_PREDEFINED_ACL);
        Assert.assertEquals(new Bucket(storage, new BucketInfo.BuilderImpl(updatedBucketInfo)), bucket);
    }

    @Test
    public void testUpdateBlob() {
        BlobInfo updatedBlobInfo = StorageImplTest.BLOB_INFO1.toBuilder().setContentType("some-content-type").build();
        EasyMock.expect(storageRpcMock.patch(updatedBlobInfo.toPb(), StorageImplTest.EMPTY_RPC_OPTIONS)).andReturn(updatedBlobInfo.toPb());
        EasyMock.replay(storageRpcMock);
        initializeService();
        Blob blob = storage.update(updatedBlobInfo);
        Assert.assertEquals(new Blob(storage, new BlobInfo.BuilderImpl(updatedBlobInfo)), blob);
    }

    @Test
    public void testUpdateBlobWithOptions() {
        BlobInfo updatedBlobInfo = StorageImplTest.BLOB_INFO1.toBuilder().setContentType("some-content-type").build();
        EasyMock.expect(storageRpcMock.patch(updatedBlobInfo.toPb(), StorageImplTest.BLOB_TARGET_OPTIONS_UPDATE)).andReturn(updatedBlobInfo.toPb());
        EasyMock.replay(storageRpcMock);
        initializeService();
        Blob blob = storage.update(updatedBlobInfo, StorageImplTest.BLOB_TARGET_METAGENERATION, StorageImplTest.BLOB_TARGET_PREDEFINED_ACL);
        Assert.assertEquals(new Blob(storage, new BlobInfo.BuilderImpl(updatedBlobInfo)), blob);
    }

    @Test
    public void testDeleteBucket() {
        EasyMock.expect(storageRpcMock.delete(BucketInfo.of(StorageImplTest.BUCKET_NAME1).toPb(), StorageImplTest.EMPTY_RPC_OPTIONS)).andReturn(true);
        EasyMock.replay(storageRpcMock);
        initializeService();
        Assert.assertTrue(storage.delete(StorageImplTest.BUCKET_NAME1));
    }

    @Test
    public void testDeleteBucketWithOptions() {
        EasyMock.expect(storageRpcMock.delete(BucketInfo.of(StorageImplTest.BUCKET_NAME1).toPb(), StorageImplTest.BUCKET_SOURCE_OPTIONS)).andReturn(true);
        EasyMock.replay(storageRpcMock);
        initializeService();
        Assert.assertTrue(storage.delete(StorageImplTest.BUCKET_NAME1, StorageImplTest.BUCKET_SOURCE_METAGENERATION));
    }

    @Test
    public void testDeleteBlob() {
        EasyMock.expect(storageRpcMock.delete(BlobId.of(StorageImplTest.BUCKET_NAME1, StorageImplTest.BLOB_NAME1).toPb(), StorageImplTest.EMPTY_RPC_OPTIONS)).andReturn(true);
        EasyMock.replay(storageRpcMock);
        initializeService();
        Assert.assertTrue(storage.delete(StorageImplTest.BUCKET_NAME1, StorageImplTest.BLOB_NAME1));
    }

    @Test
    public void testDeleteBlobWithOptions() {
        EasyMock.expect(storageRpcMock.delete(BlobId.of(StorageImplTest.BUCKET_NAME1, StorageImplTest.BLOB_NAME1).toPb(), StorageImplTest.BLOB_SOURCE_OPTIONS)).andReturn(true);
        EasyMock.replay(storageRpcMock);
        initializeService();
        Assert.assertTrue(storage.delete(StorageImplTest.BUCKET_NAME1, StorageImplTest.BLOB_NAME1, StorageImplTest.BLOB_SOURCE_GENERATION, StorageImplTest.BLOB_SOURCE_METAGENERATION));
    }

    @Test
    public void testDeleteBlobWithOptionsFromBlobId() {
        EasyMock.expect(storageRpcMock.delete(StorageImplTest.BLOB_INFO1.getBlobId().toPb(), StorageImplTest.BLOB_SOURCE_OPTIONS)).andReturn(true);
        EasyMock.replay(storageRpcMock);
        initializeService();
        Assert.assertTrue(storage.delete(StorageImplTest.BLOB_INFO1.getBlobId(), StorageImplTest.BLOB_SOURCE_GENERATION_FROM_BLOB_ID, StorageImplTest.BLOB_SOURCE_METAGENERATION));
    }

    @Test
    public void testCompose() {
        Storage.ComposeRequest req = ComposeRequest.newBuilder().addSource(StorageImplTest.BLOB_NAME2, StorageImplTest.BLOB_NAME3).setTarget(StorageImplTest.BLOB_INFO1).build();
        EasyMock.expect(storageRpcMock.compose(ImmutableList.of(StorageImplTest.BLOB_INFO2.toPb(), StorageImplTest.BLOB_INFO3.toPb()), StorageImplTest.BLOB_INFO1.toPb(), StorageImplTest.EMPTY_RPC_OPTIONS)).andReturn(StorageImplTest.BLOB_INFO1.toPb());
        EasyMock.replay(storageRpcMock);
        initializeService();
        Blob blob = storage.compose(req);
        Assert.assertEquals(expectedBlob1, blob);
    }

    @Test
    public void testComposeWithOptions() {
        Storage.ComposeRequest req = ComposeRequest.newBuilder().addSource(StorageImplTest.BLOB_NAME2, StorageImplTest.BLOB_NAME3).setTarget(StorageImplTest.BLOB_INFO1).setTargetOptions(StorageImplTest.BLOB_TARGET_GENERATION, StorageImplTest.BLOB_TARGET_METAGENERATION).build();
        EasyMock.expect(storageRpcMock.compose(ImmutableList.of(StorageImplTest.BLOB_INFO2.toPb(), StorageImplTest.BLOB_INFO3.toPb()), StorageImplTest.BLOB_INFO1.toPb(), StorageImplTest.BLOB_TARGET_OPTIONS_COMPOSE)).andReturn(StorageImplTest.BLOB_INFO1.toPb());
        EasyMock.replay(storageRpcMock);
        initializeService();
        Blob blob = storage.compose(req);
        Assert.assertEquals(expectedBlob1, blob);
    }

    @Test
    public void testCopy() {
        CopyRequest request = Storage.CopyRequest.of(StorageImplTest.BLOB_INFO1.getBlobId(), StorageImplTest.BLOB_INFO2.getBlobId());
        StorageRpc.RewriteRequest rpcRequest = new StorageRpc.RewriteRequest(request.getSource().toPb(), StorageImplTest.EMPTY_RPC_OPTIONS, false, StorageImplTest.BLOB_INFO2.toPb(), StorageImplTest.EMPTY_RPC_OPTIONS, null);
        StorageRpc.RewriteResponse rpcResponse = new StorageRpc.RewriteResponse(rpcRequest, null, 42L, false, "token", 21L);
        EasyMock.expect(storageRpcMock.openRewrite(rpcRequest)).andReturn(rpcResponse);
        EasyMock.replay(storageRpcMock);
        initializeService();
        CopyWriter writer = storage.copy(request);
        Assert.assertEquals(42L, writer.getBlobSize());
        Assert.assertEquals(21L, writer.getTotalBytesCopied());
        Assert.assertTrue((!(writer.isDone())));
    }

    @Test
    public void testCopyWithOptions() {
        CopyRequest request = Storage.CopyRequest.newBuilder().setSource(StorageImplTest.BLOB_INFO2.getBlobId()).setSourceOptions(StorageImplTest.BLOB_SOURCE_GENERATION, StorageImplTest.BLOB_SOURCE_METAGENERATION).setTarget(StorageImplTest.BLOB_INFO1, StorageImplTest.BLOB_TARGET_GENERATION, StorageImplTest.BLOB_TARGET_METAGENERATION).build();
        StorageRpc.RewriteRequest rpcRequest = new StorageRpc.RewriteRequest(request.getSource().toPb(), StorageImplTest.BLOB_SOURCE_OPTIONS_COPY, true, request.getTarget().toPb(), StorageImplTest.BLOB_TARGET_OPTIONS_COMPOSE, null);
        StorageRpc.RewriteResponse rpcResponse = new StorageRpc.RewriteResponse(rpcRequest, null, 42L, false, "token", 21L);
        EasyMock.expect(storageRpcMock.openRewrite(rpcRequest)).andReturn(rpcResponse);
        EasyMock.replay(storageRpcMock);
        initializeService();
        CopyWriter writer = storage.copy(request);
        Assert.assertEquals(42L, writer.getBlobSize());
        Assert.assertEquals(21L, writer.getTotalBytesCopied());
        Assert.assertTrue((!(writer.isDone())));
    }

    @Test
    public void testCopyWithEncryptionKey() {
        CopyRequest request = Storage.CopyRequest.newBuilder().setSource(StorageImplTest.BLOB_INFO2.getBlobId()).setSourceOptions(BlobSourceOption.decryptionKey(StorageImplTest.KEY)).setTarget(StorageImplTest.BLOB_INFO1, BlobTargetOption.encryptionKey(StorageImplTest.BASE64_KEY)).build();
        StorageRpc.RewriteRequest rpcRequest = new StorageRpc.RewriteRequest(request.getSource().toPb(), StorageImplTest.ENCRYPTION_KEY_OPTIONS, true, request.getTarget().toPb(), StorageImplTest.ENCRYPTION_KEY_OPTIONS, null);
        StorageRpc.RewriteResponse rpcResponse = new StorageRpc.RewriteResponse(rpcRequest, null, 42L, false, "token", 21L);
        EasyMock.expect(storageRpcMock.openRewrite(rpcRequest)).andReturn(rpcResponse).times(2);
        EasyMock.replay(storageRpcMock);
        initializeService();
        CopyWriter writer = storage.copy(request);
        Assert.assertEquals(42L, writer.getBlobSize());
        Assert.assertEquals(21L, writer.getTotalBytesCopied());
        Assert.assertTrue((!(writer.isDone())));
        request = Storage.CopyRequest.newBuilder().setSource(StorageImplTest.BLOB_INFO2.getBlobId()).setSourceOptions(BlobSourceOption.decryptionKey(StorageImplTest.BASE64_KEY)).setTarget(StorageImplTest.BLOB_INFO1, BlobTargetOption.encryptionKey(StorageImplTest.KEY)).build();
        writer = storage.copy(request);
        Assert.assertEquals(42L, writer.getBlobSize());
        Assert.assertEquals(21L, writer.getTotalBytesCopied());
        Assert.assertTrue((!(writer.isDone())));
    }

    @Test
    public void testCopyFromEncryptionKeyToKmsKeyName() {
        CopyRequest request = Storage.CopyRequest.newBuilder().setSource(StorageImplTest.BLOB_INFO2.getBlobId()).setSourceOptions(BlobSourceOption.decryptionKey(StorageImplTest.KEY)).setTarget(StorageImplTest.BLOB_INFO1, BlobTargetOption.kmsKeyName(StorageImplTest.KMS_KEY_NAME)).build();
        StorageRpc.RewriteRequest rpcRequest = new StorageRpc.RewriteRequest(request.getSource().toPb(), StorageImplTest.ENCRYPTION_KEY_OPTIONS, true, request.getTarget().toPb(), StorageImplTest.KMS_KEY_NAME_OPTIONS, null);
        StorageRpc.RewriteResponse rpcResponse = new StorageRpc.RewriteResponse(rpcRequest, null, 42L, false, "token", 21L);
        EasyMock.expect(storageRpcMock.openRewrite(rpcRequest)).andReturn(rpcResponse).times(2);
        EasyMock.replay(storageRpcMock);
        initializeService();
        CopyWriter writer = storage.copy(request);
        Assert.assertEquals(42L, writer.getBlobSize());
        Assert.assertEquals(21L, writer.getTotalBytesCopied());
        Assert.assertTrue((!(writer.isDone())));
        request = Storage.CopyRequest.newBuilder().setSource(StorageImplTest.BLOB_INFO2.getBlobId()).setSourceOptions(BlobSourceOption.decryptionKey(StorageImplTest.BASE64_KEY)).setTarget(StorageImplTest.BLOB_INFO1, BlobTargetOption.kmsKeyName(StorageImplTest.KMS_KEY_NAME)).build();
        writer = storage.copy(request);
        Assert.assertEquals(42L, writer.getBlobSize());
        Assert.assertEquals(21L, writer.getTotalBytesCopied());
        Assert.assertTrue((!(writer.isDone())));
    }

    @Test
    public void testCopyWithOptionsFromBlobId() {
        CopyRequest request = Storage.CopyRequest.newBuilder().setSource(StorageImplTest.BLOB_INFO1.getBlobId()).setSourceOptions(StorageImplTest.BLOB_SOURCE_GENERATION_FROM_BLOB_ID, StorageImplTest.BLOB_SOURCE_METAGENERATION).setTarget(StorageImplTest.BLOB_INFO1, StorageImplTest.BLOB_TARGET_GENERATION, StorageImplTest.BLOB_TARGET_METAGENERATION).build();
        StorageRpc.RewriteRequest rpcRequest = new StorageRpc.RewriteRequest(request.getSource().toPb(), StorageImplTest.BLOB_SOURCE_OPTIONS_COPY, true, request.getTarget().toPb(), StorageImplTest.BLOB_TARGET_OPTIONS_COMPOSE, null);
        StorageRpc.RewriteResponse rpcResponse = new StorageRpc.RewriteResponse(rpcRequest, null, 42L, false, "token", 21L);
        EasyMock.expect(storageRpcMock.openRewrite(rpcRequest)).andReturn(rpcResponse);
        EasyMock.replay(storageRpcMock);
        initializeService();
        CopyWriter writer = storage.copy(request);
        Assert.assertEquals(42L, writer.getBlobSize());
        Assert.assertEquals(21L, writer.getTotalBytesCopied());
        Assert.assertTrue((!(writer.isDone())));
    }

    @Test
    public void testCopyMultipleRequests() {
        CopyRequest request = Storage.CopyRequest.of(StorageImplTest.BLOB_INFO1.getBlobId(), StorageImplTest.BLOB_INFO2.getBlobId());
        StorageRpc.RewriteRequest rpcRequest = new StorageRpc.RewriteRequest(request.getSource().toPb(), StorageImplTest.EMPTY_RPC_OPTIONS, false, StorageImplTest.BLOB_INFO2.toPb(), StorageImplTest.EMPTY_RPC_OPTIONS, null);
        StorageRpc.RewriteResponse rpcResponse1 = new StorageRpc.RewriteResponse(rpcRequest, null, 42L, false, "token", 21L);
        StorageRpc.RewriteResponse rpcResponse2 = new StorageRpc.RewriteResponse(rpcRequest, StorageImplTest.BLOB_INFO1.toPb(), 42L, true, "token", 42L);
        EasyMock.expect(storageRpcMock.openRewrite(rpcRequest)).andReturn(rpcResponse1);
        EasyMock.expect(storageRpcMock.continueRewrite(rpcResponse1)).andReturn(rpcResponse2);
        EasyMock.replay(storageRpcMock);
        initializeService();
        CopyWriter writer = storage.copy(request);
        Assert.assertEquals(42L, writer.getBlobSize());
        Assert.assertEquals(21L, writer.getTotalBytesCopied());
        Assert.assertTrue((!(writer.isDone())));
        Assert.assertEquals(expectedBlob1, writer.getResult());
        Assert.assertTrue(writer.isDone());
        Assert.assertEquals(42L, writer.getTotalBytesCopied());
        Assert.assertEquals(42L, writer.getBlobSize());
    }

    @Test
    public void testReadAllBytes() {
        EasyMock.expect(storageRpcMock.load(BlobId.of(StorageImplTest.BUCKET_NAME1, StorageImplTest.BLOB_NAME1).toPb(), StorageImplTest.EMPTY_RPC_OPTIONS)).andReturn(StorageImplTest.BLOB_CONTENT);
        EasyMock.replay(storageRpcMock);
        initializeService();
        byte[] readBytes = storage.readAllBytes(StorageImplTest.BUCKET_NAME1, StorageImplTest.BLOB_NAME1);
        Assert.assertArrayEquals(StorageImplTest.BLOB_CONTENT, readBytes);
    }

    @Test
    public void testReadAllBytesWithOptions() {
        EasyMock.expect(storageRpcMock.load(BlobId.of(StorageImplTest.BUCKET_NAME1, StorageImplTest.BLOB_NAME1).toPb(), StorageImplTest.BLOB_SOURCE_OPTIONS)).andReturn(StorageImplTest.BLOB_CONTENT);
        EasyMock.replay(storageRpcMock);
        initializeService();
        byte[] readBytes = storage.readAllBytes(StorageImplTest.BUCKET_NAME1, StorageImplTest.BLOB_NAME1, StorageImplTest.BLOB_SOURCE_GENERATION, StorageImplTest.BLOB_SOURCE_METAGENERATION);
        Assert.assertArrayEquals(StorageImplTest.BLOB_CONTENT, readBytes);
    }

    @Test
    public void testReadAllBytesWithDecriptionKey() {
        EasyMock.expect(storageRpcMock.load(BlobId.of(StorageImplTest.BUCKET_NAME1, StorageImplTest.BLOB_NAME1).toPb(), StorageImplTest.ENCRYPTION_KEY_OPTIONS)).andReturn(StorageImplTest.BLOB_CONTENT).times(2);
        EasyMock.replay(storageRpcMock);
        initializeService();
        byte[] readBytes = storage.readAllBytes(StorageImplTest.BUCKET_NAME1, StorageImplTest.BLOB_NAME1, BlobSourceOption.decryptionKey(StorageImplTest.KEY));
        Assert.assertArrayEquals(StorageImplTest.BLOB_CONTENT, readBytes);
        readBytes = storage.readAllBytes(StorageImplTest.BUCKET_NAME1, StorageImplTest.BLOB_NAME1, BlobSourceOption.decryptionKey(StorageImplTest.BASE64_KEY));
        Assert.assertArrayEquals(StorageImplTest.BLOB_CONTENT, readBytes);
    }

    @Test
    public void testReadAllBytesFromBlobIdWithOptions() {
        EasyMock.expect(storageRpcMock.load(StorageImplTest.BLOB_INFO1.getBlobId().toPb(), StorageImplTest.BLOB_SOURCE_OPTIONS)).andReturn(StorageImplTest.BLOB_CONTENT);
        EasyMock.replay(storageRpcMock);
        initializeService();
        byte[] readBytes = storage.readAllBytes(StorageImplTest.BLOB_INFO1.getBlobId(), StorageImplTest.BLOB_SOURCE_GENERATION_FROM_BLOB_ID, StorageImplTest.BLOB_SOURCE_METAGENERATION);
        Assert.assertArrayEquals(StorageImplTest.BLOB_CONTENT, readBytes);
    }

    @Test
    public void testReadAllBytesFromBlobIdWithDecriptionKey() {
        EasyMock.expect(storageRpcMock.load(StorageImplTest.BLOB_INFO1.getBlobId().toPb(), StorageImplTest.ENCRYPTION_KEY_OPTIONS)).andReturn(StorageImplTest.BLOB_CONTENT).times(2);
        EasyMock.replay(storageRpcMock);
        initializeService();
        byte[] readBytes = storage.readAllBytes(StorageImplTest.BLOB_INFO1.getBlobId(), BlobSourceOption.decryptionKey(StorageImplTest.KEY));
        Assert.assertArrayEquals(StorageImplTest.BLOB_CONTENT, readBytes);
        readBytes = storage.readAllBytes(StorageImplTest.BLOB_INFO1.getBlobId(), BlobSourceOption.decryptionKey(StorageImplTest.BASE64_KEY));
        Assert.assertArrayEquals(StorageImplTest.BLOB_CONTENT, readBytes);
    }

    @Test
    public void testBatch() {
        RpcBatch batchMock = EasyMock.mock(RpcBatch.class);
        EasyMock.expect(storageRpcMock.createBatch()).andReturn(batchMock);
        EasyMock.replay(batchMock, storageRpcMock);
        initializeService();
        StorageBatch batch = storage.batch();
        Assert.assertSame(options, batch.getOptions());
        Assert.assertSame(storageRpcMock, batch.getStorageRpc());
        Assert.assertSame(batchMock, batch.getBatch());
        EasyMock.verify(batchMock);
    }

    @Test
    public void testReader() {
        EasyMock.replay(storageRpcMock);
        initializeService();
        ReadChannel channel = storage.reader(StorageImplTest.BUCKET_NAME1, StorageImplTest.BLOB_NAME1);
        Assert.assertNotNull(channel);
        Assert.assertTrue(channel.isOpen());
    }

    @Test
    public void testReaderWithOptions() throws IOException {
        byte[] result = new byte[StorageImplTest.DEFAULT_CHUNK_SIZE];
        EasyMock.expect(storageRpcMock.read(StorageImplTest.BLOB_INFO2.toPb(), StorageImplTest.BLOB_SOURCE_OPTIONS, 0, StorageImplTest.DEFAULT_CHUNK_SIZE)).andReturn(Tuple.of("etag", result));
        EasyMock.replay(storageRpcMock);
        initializeService();
        ReadChannel channel = storage.reader(StorageImplTest.BUCKET_NAME1, StorageImplTest.BLOB_NAME2, StorageImplTest.BLOB_SOURCE_GENERATION, StorageImplTest.BLOB_SOURCE_METAGENERATION);
        Assert.assertNotNull(channel);
        Assert.assertTrue(channel.isOpen());
        channel.read(ByteBuffer.allocate(42));
    }

    @Test
    public void testReaderWithDecryptionKey() throws IOException {
        byte[] result = new byte[StorageImplTest.DEFAULT_CHUNK_SIZE];
        EasyMock.expect(storageRpcMock.read(StorageImplTest.BLOB_INFO2.toPb(), StorageImplTest.ENCRYPTION_KEY_OPTIONS, 0, StorageImplTest.DEFAULT_CHUNK_SIZE)).andReturn(Tuple.of("etag", result)).times(2);
        EasyMock.replay(storageRpcMock);
        initializeService();
        ReadChannel channel = storage.reader(StorageImplTest.BUCKET_NAME1, StorageImplTest.BLOB_NAME2, BlobSourceOption.decryptionKey(StorageImplTest.KEY));
        Assert.assertNotNull(channel);
        Assert.assertTrue(channel.isOpen());
        channel.read(ByteBuffer.allocate(42));
        channel = storage.reader(StorageImplTest.BUCKET_NAME1, StorageImplTest.BLOB_NAME2, BlobSourceOption.decryptionKey(StorageImplTest.BASE64_KEY));
        Assert.assertNotNull(channel);
        Assert.assertTrue(channel.isOpen());
        channel.read(ByteBuffer.allocate(42));
    }

    @Test
    public void testReaderWithOptionsFromBlobId() throws IOException {
        byte[] result = new byte[StorageImplTest.DEFAULT_CHUNK_SIZE];
        EasyMock.expect(storageRpcMock.read(StorageImplTest.BLOB_INFO1.getBlobId().toPb(), StorageImplTest.BLOB_SOURCE_OPTIONS, 0, StorageImplTest.DEFAULT_CHUNK_SIZE)).andReturn(Tuple.of("etag", result));
        EasyMock.replay(storageRpcMock);
        initializeService();
        ReadChannel channel = storage.reader(StorageImplTest.BLOB_INFO1.getBlobId(), StorageImplTest.BLOB_SOURCE_GENERATION_FROM_BLOB_ID, StorageImplTest.BLOB_SOURCE_METAGENERATION);
        Assert.assertNotNull(channel);
        Assert.assertTrue(channel.isOpen());
        channel.read(ByteBuffer.allocate(42));
    }

    @Test
    public void testWriter() {
        BlobInfo.Builder infoBuilder = StorageImplTest.BLOB_INFO1.toBuilder();
        BlobInfo infoWithHashes = infoBuilder.setMd5(StorageImplTest.CONTENT_MD5).setCrc32c(StorageImplTest.CONTENT_CRC32C).build();
        BlobInfo infoWithoutHashes = infoBuilder.setMd5(null).setCrc32c(null).build();
        EasyMock.expect(storageRpcMock.open(infoWithoutHashes.toPb(), StorageImplTest.EMPTY_RPC_OPTIONS)).andReturn("upload-id");
        EasyMock.replay(storageRpcMock);
        initializeService();
        WriteChannel channel = storage.writer(infoWithHashes);
        Assert.assertNotNull(channel);
        Assert.assertTrue(channel.isOpen());
    }

    @Test
    public void testWriterWithOptions() {
        BlobInfo info = StorageImplTest.BLOB_INFO1.toBuilder().setMd5(StorageImplTest.CONTENT_MD5).setCrc32c(StorageImplTest.CONTENT_CRC32C).build();
        EasyMock.expect(storageRpcMock.open(info.toPb(), StorageImplTest.BLOB_TARGET_OPTIONS_CREATE)).andReturn("upload-id");
        EasyMock.replay(storageRpcMock);
        initializeService();
        WriteChannel channel = storage.writer(info, StorageImplTest.BLOB_WRITE_METAGENERATION, StorageImplTest.BLOB_WRITE_NOT_EXIST, StorageImplTest.BLOB_WRITE_PREDEFINED_ACL, StorageImplTest.BLOB_WRITE_CRC2C, StorageImplTest.BLOB_WRITE_MD5_HASH);
        Assert.assertNotNull(channel);
        Assert.assertTrue(channel.isOpen());
    }

    @Test
    public void testWriterWithEncryptionKey() {
        BlobInfo info = StorageImplTest.BLOB_INFO1.toBuilder().setMd5(null).setCrc32c(null).build();
        EasyMock.expect(storageRpcMock.open(info.toPb(), StorageImplTest.ENCRYPTION_KEY_OPTIONS)).andReturn("upload-id").times(2);
        EasyMock.replay(storageRpcMock);
        initializeService();
        WriteChannel channel = storage.writer(info, BlobWriteOption.encryptionKey(StorageImplTest.KEY));
        Assert.assertNotNull(channel);
        Assert.assertTrue(channel.isOpen());
        channel = storage.writer(info, BlobWriteOption.encryptionKey(StorageImplTest.BASE64_KEY));
        Assert.assertNotNull(channel);
        Assert.assertTrue(channel.isOpen());
    }

    @Test
    public void testWriterWithKmsKeyName() {
        BlobInfo info = StorageImplTest.BLOB_INFO1.toBuilder().setMd5(null).setCrc32c(null).build();
        EasyMock.expect(storageRpcMock.open(info.toPb(), StorageImplTest.KMS_KEY_NAME_OPTIONS)).andReturn("upload-id").times(2);
        EasyMock.replay(storageRpcMock);
        initializeService();
        WriteChannel channel = storage.writer(info, BlobWriteOption.kmsKeyName(StorageImplTest.KMS_KEY_NAME));
        Assert.assertNotNull(channel);
        Assert.assertTrue(channel.isOpen());
        channel = storage.writer(info, BlobWriteOption.kmsKeyName(StorageImplTest.KMS_KEY_NAME));
        Assert.assertNotNull(channel);
        Assert.assertTrue(channel.isOpen());
    }

    @Test
    public void testSignUrl() throws UnsupportedEncodingException, InvalidKeyException, NoSuchAlgorithmException, SignatureException {
        EasyMock.replay(storageRpcMock);
        ServiceAccountCredentials credentials = ServiceAccountCredentials.newBuilder().setClientEmail(StorageImplTest.ACCOUNT).setPrivateKey(StorageImplTest.privateKey).build();
        storage = options.toBuilder().setCredentials(credentials).build().getService();
        URL url = storage.signUrl(StorageImplTest.BLOB_INFO1, 14, TimeUnit.DAYS);
        String stringUrl = url.toString();
        String expectedUrl = new StringBuilder("https://storage.googleapis.com/").append(StorageImplTest.BUCKET_NAME1).append('/').append(StorageImplTest.BLOB_NAME1).append("?GoogleAccessId=").append(StorageImplTest.ACCOUNT).append("&Expires=").append((42L + 1209600)).append("&Signature=").toString();
        Assert.assertTrue(stringUrl.startsWith(expectedUrl));
        String signature = stringUrl.substring(expectedUrl.length());
        StringBuilder signedMessageBuilder = new StringBuilder();
        signedMessageBuilder.append(GET).append("\n\n\n").append((42L + 1209600)).append("\n/").append(StorageImplTest.BUCKET_NAME1).append('/').append(StorageImplTest.BLOB_NAME1);
        Signature signer = Signature.getInstance("SHA256withRSA");
        signer.initVerify(StorageImplTest.publicKey);
        signer.update(signedMessageBuilder.toString().getBytes(StandardCharsets.UTF_8));
        Assert.assertTrue(signer.verify(BaseEncoding.base64().decode(URLDecoder.decode(signature, StandardCharsets.UTF_8.name()))));
    }

    @Test
    public void testSignUrlWithHostName() throws UnsupportedEncodingException, InvalidKeyException, NoSuchAlgorithmException, SignatureException {
        EasyMock.replay(storageRpcMock);
        ServiceAccountCredentials credentials = ServiceAccountCredentials.newBuilder().setClientEmail(StorageImplTest.ACCOUNT).setPrivateKey(StorageImplTest.privateKey).build();
        storage = options.toBuilder().setCredentials(credentials).build().getService();
        URL url = storage.signUrl(StorageImplTest.BLOB_INFO1, 14, TimeUnit.DAYS, SignUrlOption.withHostName("https://example.com"));
        String stringUrl = url.toString();
        String expectedUrl = new StringBuilder("https://example.com/").append(StorageImplTest.BUCKET_NAME1).append('/').append(StorageImplTest.BLOB_NAME1).append("?GoogleAccessId=").append(StorageImplTest.ACCOUNT).append("&Expires=").append((42L + 1209600)).append("&Signature=").toString();
        Assert.assertTrue(stringUrl.startsWith(expectedUrl));
        String signature = stringUrl.substring(expectedUrl.length());
        StringBuilder signedMessageBuilder = new StringBuilder();
        signedMessageBuilder.append(GET).append("\n\n\n").append((42L + 1209600)).append("\n/").append(StorageImplTest.BUCKET_NAME1).append('/').append(StorageImplTest.BLOB_NAME1);
        Signature signer = Signature.getInstance("SHA256withRSA");
        signer.initVerify(StorageImplTest.publicKey);
        signer.update(signedMessageBuilder.toString().getBytes(StandardCharsets.UTF_8));
        Assert.assertTrue(signer.verify(BaseEncoding.base64().decode(URLDecoder.decode(signature, StandardCharsets.UTF_8.name()))));
    }

    @Test
    public void testSignUrlLeadingSlash() throws UnsupportedEncodingException, InvalidKeyException, NoSuchAlgorithmException, SignatureException {
        String blobName = "/b1";
        EasyMock.replay(storageRpcMock);
        ServiceAccountCredentials credentials = ServiceAccountCredentials.newBuilder().setClientEmail(StorageImplTest.ACCOUNT).setPrivateKey(StorageImplTest.privateKey).build();
        storage = options.toBuilder().setCredentials(credentials).build().getService();
        URL url = storage.signUrl(BlobInfo.newBuilder(StorageImplTest.BUCKET_NAME1, blobName).build(), 14, TimeUnit.DAYS);
        String escapedBlobName = UrlEscapers.urlFragmentEscaper().escape(blobName);
        String stringUrl = url.toString();
        String expectedUrl = new StringBuilder("https://storage.googleapis.com/").append(StorageImplTest.BUCKET_NAME1).append(escapedBlobName).append("?GoogleAccessId=").append(StorageImplTest.ACCOUNT).append("&Expires=").append((42L + 1209600)).append("&Signature=").toString();
        Assert.assertTrue(stringUrl.startsWith(expectedUrl));
        String signature = stringUrl.substring(expectedUrl.length());
        StringBuilder signedMessageBuilder = new StringBuilder();
        signedMessageBuilder.append(GET).append("\n\n\n").append((42L + 1209600)).append("\n/").append(StorageImplTest.BUCKET_NAME1).append(escapedBlobName);
        Signature signer = Signature.getInstance("SHA256withRSA");
        signer.initVerify(StorageImplTest.publicKey);
        signer.update(signedMessageBuilder.toString().getBytes(StandardCharsets.UTF_8));
        Assert.assertTrue(signer.verify(BaseEncoding.base64().decode(URLDecoder.decode(signature, StandardCharsets.UTF_8.name()))));
    }

    @Test
    public void testSignUrlLeadingSlashWithHostName() throws UnsupportedEncodingException, InvalidKeyException, NoSuchAlgorithmException, SignatureException {
        String blobName = "/b1";
        EasyMock.replay(storageRpcMock);
        ServiceAccountCredentials credentials = ServiceAccountCredentials.newBuilder().setClientEmail(StorageImplTest.ACCOUNT).setPrivateKey(StorageImplTest.privateKey).build();
        storage = options.toBuilder().setCredentials(credentials).build().getService();
        URL url = storage.signUrl(BlobInfo.newBuilder(StorageImplTest.BUCKET_NAME1, blobName).build(), 14, TimeUnit.DAYS, SignUrlOption.withHostName("https://example.com"));
        String escapedBlobName = UrlEscapers.urlFragmentEscaper().escape(blobName);
        String stringUrl = url.toString();
        String expectedUrl = new StringBuilder("https://example.com/").append(StorageImplTest.BUCKET_NAME1).append(escapedBlobName).append("?GoogleAccessId=").append(StorageImplTest.ACCOUNT).append("&Expires=").append((42L + 1209600)).append("&Signature=").toString();
        Assert.assertTrue(stringUrl.startsWith(expectedUrl));
        String signature = stringUrl.substring(expectedUrl.length());
        StringBuilder signedMessageBuilder = new StringBuilder();
        signedMessageBuilder.append(GET).append("\n\n\n").append((42L + 1209600)).append("\n/").append(StorageImplTest.BUCKET_NAME1).append(escapedBlobName);
        Signature signer = Signature.getInstance("SHA256withRSA");
        signer.initVerify(StorageImplTest.publicKey);
        signer.update(signedMessageBuilder.toString().getBytes(StandardCharsets.UTF_8));
        Assert.assertTrue(signer.verify(BaseEncoding.base64().decode(URLDecoder.decode(signature, StandardCharsets.UTF_8.name()))));
    }

    @Test
    public void testSignUrlWithOptions() throws UnsupportedEncodingException, InvalidKeyException, NoSuchAlgorithmException, SignatureException {
        EasyMock.replay(storageRpcMock);
        ServiceAccountCredentials credentials = ServiceAccountCredentials.newBuilder().setClientEmail(StorageImplTest.ACCOUNT).setPrivateKey(StorageImplTest.privateKey).build();
        storage = options.toBuilder().setCredentials(credentials).build().getService();
        URL url = storage.signUrl(StorageImplTest.BLOB_INFO1, 14, TimeUnit.DAYS, SignUrlOption.httpMethod(POST), SignUrlOption.withContentType(), SignUrlOption.withMd5());
        String stringUrl = url.toString();
        String expectedUrl = new StringBuilder("https://storage.googleapis.com/").append(StorageImplTest.BUCKET_NAME1).append('/').append(StorageImplTest.BLOB_NAME1).append("?GoogleAccessId=").append(StorageImplTest.ACCOUNT).append("&Expires=").append((42L + 1209600)).append("&Signature=").toString();
        Assert.assertTrue(stringUrl.startsWith(expectedUrl));
        String signature = stringUrl.substring(expectedUrl.length());
        StringBuilder signedMessageBuilder = new StringBuilder();
        signedMessageBuilder.append(POST).append('\n').append(StorageImplTest.BLOB_INFO1.getMd5()).append('\n').append(StorageImplTest.BLOB_INFO1.getContentType()).append('\n').append((42L + 1209600)).append("\n/").append(StorageImplTest.BUCKET_NAME1).append('/').append(StorageImplTest.BLOB_NAME1);
        Signature signer = Signature.getInstance("SHA256withRSA");
        signer.initVerify(StorageImplTest.publicKey);
        signer.update(signedMessageBuilder.toString().getBytes(StandardCharsets.UTF_8));
        Assert.assertTrue(signer.verify(BaseEncoding.base64().decode(URLDecoder.decode(signature, StandardCharsets.UTF_8.name()))));
    }

    @Test
    public void testSignUrlWithOptionsAndHostName() throws UnsupportedEncodingException, InvalidKeyException, NoSuchAlgorithmException, SignatureException {
        EasyMock.replay(storageRpcMock);
        ServiceAccountCredentials credentials = ServiceAccountCredentials.newBuilder().setClientEmail(StorageImplTest.ACCOUNT).setPrivateKey(StorageImplTest.privateKey).build();
        storage = options.toBuilder().setCredentials(credentials).build().getService();
        URL url = storage.signUrl(StorageImplTest.BLOB_INFO1, 14, TimeUnit.DAYS, SignUrlOption.httpMethod(POST), SignUrlOption.withContentType(), SignUrlOption.withMd5(), SignUrlOption.withHostName("https://example.com"));
        String stringUrl = url.toString();
        String expectedUrl = new StringBuilder("https://example.com/").append(StorageImplTest.BUCKET_NAME1).append('/').append(StorageImplTest.BLOB_NAME1).append("?GoogleAccessId=").append(StorageImplTest.ACCOUNT).append("&Expires=").append((42L + 1209600)).append("&Signature=").toString();
        Assert.assertTrue(stringUrl.startsWith(expectedUrl));
        String signature = stringUrl.substring(expectedUrl.length());
        StringBuilder signedMessageBuilder = new StringBuilder();
        signedMessageBuilder.append(POST).append('\n').append(StorageImplTest.BLOB_INFO1.getMd5()).append('\n').append(StorageImplTest.BLOB_INFO1.getContentType()).append('\n').append((42L + 1209600)).append("\n/").append(StorageImplTest.BUCKET_NAME1).append('/').append(StorageImplTest.BLOB_NAME1);
        Signature signer = Signature.getInstance("SHA256withRSA");
        signer.initVerify(StorageImplTest.publicKey);
        signer.update(signedMessageBuilder.toString().getBytes(StandardCharsets.UTF_8));
        Assert.assertTrue(signer.verify(BaseEncoding.base64().decode(URLDecoder.decode(signature, StandardCharsets.UTF_8.name()))));
    }

    @Test
    public void testSignUrlForBlobWithSpecialChars() throws UnsupportedEncodingException, InvalidKeyException, NoSuchAlgorithmException, SignatureException {
        // List of chars under test were taken from
        // https://en.wikipedia.org/wiki/Percent-encoding#Percent-encoding_reserved_characters
        char[] specialChars = new char[]{ '!', '#', '$', '&', '\'', '(', ')', '*', '+', ',', ':', ';', '=', '?', '@', '[', ']' };
        EasyMock.replay(storageRpcMock);
        ServiceAccountCredentials credentials = ServiceAccountCredentials.newBuilder().setClientEmail(StorageImplTest.ACCOUNT).setPrivateKey(StorageImplTest.privateKey).build();
        storage = options.toBuilder().setCredentials(credentials).build().getService();
        for (char specialChar : specialChars) {
            String blobName = ("/a" + specialChar) + "b";
            URL url = storage.signUrl(BlobInfo.newBuilder(StorageImplTest.BUCKET_NAME1, blobName).build(), 14, TimeUnit.DAYS);
            String escapedBlobName = UrlEscapers.urlFragmentEscaper().escape(blobName).replace("?", "%3F");
            String stringUrl = url.toString();
            String expectedUrl = new StringBuilder("https://storage.googleapis.com/").append(StorageImplTest.BUCKET_NAME1).append(escapedBlobName).append("?GoogleAccessId=").append(StorageImplTest.ACCOUNT).append("&Expires=").append((42L + 1209600)).append("&Signature=").toString();
            Assert.assertTrue(stringUrl.startsWith(expectedUrl));
            String signature = stringUrl.substring(expectedUrl.length());
            StringBuilder signedMessageBuilder = new StringBuilder();
            signedMessageBuilder.append(GET).append("\n\n\n").append((42L + 1209600)).append("\n/").append(StorageImplTest.BUCKET_NAME1).append(escapedBlobName);
            Signature signer = Signature.getInstance("SHA256withRSA");
            signer.initVerify(StorageImplTest.publicKey);
            signer.update(signedMessageBuilder.toString().getBytes(StandardCharsets.UTF_8));
            Assert.assertTrue(signer.verify(BaseEncoding.base64().decode(URLDecoder.decode(signature, StandardCharsets.UTF_8.name()))));
        }
    }

    @Test
    public void testSignUrlForBlobWithSpecialCharsAndHostName() throws UnsupportedEncodingException, InvalidKeyException, NoSuchAlgorithmException, SignatureException {
        // List of chars under test were taken from
        // https://en.wikipedia.org/wiki/Percent-encoding#Percent-encoding_reserved_characters
        char[] specialChars = new char[]{ '!', '#', '$', '&', '\'', '(', ')', '*', '+', ',', ':', ';', '=', '?', '@', '[', ']' };
        EasyMock.replay(storageRpcMock);
        ServiceAccountCredentials credentials = ServiceAccountCredentials.newBuilder().setClientEmail(StorageImplTest.ACCOUNT).setPrivateKey(StorageImplTest.privateKey).build();
        storage = options.toBuilder().setCredentials(credentials).build().getService();
        for (char specialChar : specialChars) {
            String blobName = ("/a" + specialChar) + "b";
            URL url = storage.signUrl(BlobInfo.newBuilder(StorageImplTest.BUCKET_NAME1, blobName).build(), 14, TimeUnit.DAYS, SignUrlOption.withHostName("https://example.com"));
            String escapedBlobName = UrlEscapers.urlFragmentEscaper().escape(blobName).replace("?", "%3F");
            String stringUrl = url.toString();
            String expectedUrl = new StringBuilder("https://example.com/").append(StorageImplTest.BUCKET_NAME1).append(escapedBlobName).append("?GoogleAccessId=").append(StorageImplTest.ACCOUNT).append("&Expires=").append((42L + 1209600)).append("&Signature=").toString();
            Assert.assertTrue(stringUrl.startsWith(expectedUrl));
            String signature = stringUrl.substring(expectedUrl.length());
            StringBuilder signedMessageBuilder = new StringBuilder();
            signedMessageBuilder.append(GET).append("\n\n\n").append((42L + 1209600)).append("\n/").append(StorageImplTest.BUCKET_NAME1).append(escapedBlobName);
            Signature signer = Signature.getInstance("SHA256withRSA");
            signer.initVerify(StorageImplTest.publicKey);
            signer.update(signedMessageBuilder.toString().getBytes(StandardCharsets.UTF_8));
            Assert.assertTrue(signer.verify(BaseEncoding.base64().decode(URLDecoder.decode(signature, StandardCharsets.UTF_8.name()))));
        }
    }

    @Test
    public void testSignUrlWithExtHeaders() throws UnsupportedEncodingException, InvalidKeyException, NoSuchAlgorithmException, SignatureException {
        EasyMock.replay(storageRpcMock);
        ServiceAccountCredentials credentials = ServiceAccountCredentials.newBuilder().setClientEmail(StorageImplTest.ACCOUNT).setPrivateKey(StorageImplTest.privateKey).build();
        storage = options.toBuilder().setCredentials(credentials).build().getService();
        Map<String, String> extHeaders = new HashMap<String, String>();
        extHeaders.put("x-goog-acl", "public-read");
        extHeaders.put("x-goog-meta-owner", "myself");
        URL url = storage.signUrl(StorageImplTest.BLOB_INFO1, 14, TimeUnit.DAYS, SignUrlOption.httpMethod(PUT), SignUrlOption.withContentType(), SignUrlOption.withExtHeaders(extHeaders));
        String stringUrl = url.toString();
        String expectedUrl = new StringBuilder("https://storage.googleapis.com/").append(StorageImplTest.BUCKET_NAME1).append('/').append(StorageImplTest.BLOB_NAME1).append("?GoogleAccessId=").append(StorageImplTest.ACCOUNT).append("&Expires=").append((42L + 1209600)).append("&Signature=").toString();
        Assert.assertTrue(stringUrl.startsWith(expectedUrl));
        String signature = stringUrl.substring(expectedUrl.length());
        StringBuilder signedMessageBuilder = new StringBuilder();
        signedMessageBuilder.append(PUT).append('\n').append('\n').append(StorageImplTest.BLOB_INFO1.getContentType()).append('\n').append((42L + 1209600)).append('\n').append("x-goog-acl:public-read\n").append("x-goog-meta-owner:myself\n").append('/').append(StorageImplTest.BUCKET_NAME1).append('/').append(StorageImplTest.BLOB_NAME1);
        Signature signer = Signature.getInstance("SHA256withRSA");
        signer.initVerify(StorageImplTest.publicKey);
        signer.update(signedMessageBuilder.toString().getBytes(StandardCharsets.UTF_8));
        Assert.assertTrue(signer.verify(BaseEncoding.base64().decode(URLDecoder.decode(signature, StandardCharsets.UTF_8.name()))));
    }

    @Test
    public void testSignUrlWithExtHeadersAndHostName() throws UnsupportedEncodingException, InvalidKeyException, NoSuchAlgorithmException, SignatureException {
        EasyMock.replay(storageRpcMock);
        ServiceAccountCredentials credentials = ServiceAccountCredentials.newBuilder().setClientEmail(StorageImplTest.ACCOUNT).setPrivateKey(StorageImplTest.privateKey).build();
        storage = options.toBuilder().setCredentials(credentials).build().getService();
        Map<String, String> extHeaders = new HashMap<String, String>();
        extHeaders.put("x-goog-acl", "public-read");
        extHeaders.put("x-goog-meta-owner", "myself");
        URL url = storage.signUrl(StorageImplTest.BLOB_INFO1, 14, TimeUnit.DAYS, SignUrlOption.httpMethod(PUT), SignUrlOption.withContentType(), SignUrlOption.withExtHeaders(extHeaders), SignUrlOption.withHostName("https://example.com"));
        String stringUrl = url.toString();
        String expectedUrl = new StringBuilder("https://example.com/").append(StorageImplTest.BUCKET_NAME1).append('/').append(StorageImplTest.BLOB_NAME1).append("?GoogleAccessId=").append(StorageImplTest.ACCOUNT).append("&Expires=").append((42L + 1209600)).append("&Signature=").toString();
        Assert.assertTrue(stringUrl.startsWith(expectedUrl));
        String signature = stringUrl.substring(expectedUrl.length());
        StringBuilder signedMessageBuilder = new StringBuilder();
        signedMessageBuilder.append(PUT).append('\n').append('\n').append(StorageImplTest.BLOB_INFO1.getContentType()).append('\n').append((42L + 1209600)).append('\n').append("x-goog-acl:public-read\n").append("x-goog-meta-owner:myself\n").append('/').append(StorageImplTest.BUCKET_NAME1).append('/').append(StorageImplTest.BLOB_NAME1);
        Signature signer = Signature.getInstance("SHA256withRSA");
        signer.initVerify(StorageImplTest.publicKey);
        signer.update(signedMessageBuilder.toString().getBytes(StandardCharsets.UTF_8));
        Assert.assertTrue(signer.verify(BaseEncoding.base64().decode(URLDecoder.decode(signature, StandardCharsets.UTF_8.name()))));
    }

    @Test
    public void testSignUrlForBlobWithSlashes() throws UnsupportedEncodingException, InvalidKeyException, NoSuchAlgorithmException, SignatureException {
        EasyMock.replay(storageRpcMock);
        ServiceAccountCredentials credentials = ServiceAccountCredentials.newBuilder().setClientEmail(StorageImplTest.ACCOUNT).setPrivateKey(StorageImplTest.privateKey).build();
        storage = options.toBuilder().setCredentials(credentials).build().getService();
        String blobName = "/foo/bar/baz #%20other cool stuff.txt";
        URL url = storage.signUrl(BlobInfo.newBuilder(StorageImplTest.BUCKET_NAME1, blobName).build(), 14, TimeUnit.DAYS);
        String escapedBlobName = UrlEscapers.urlFragmentEscaper().escape(blobName);
        String stringUrl = url.toString();
        String expectedUrl = new StringBuilder("https://storage.googleapis.com/").append(StorageImplTest.BUCKET_NAME1).append(escapedBlobName).append("?GoogleAccessId=").append(StorageImplTest.ACCOUNT).append("&Expires=").append((42L + 1209600)).append("&Signature=").toString();
        Assert.assertTrue(stringUrl.startsWith(expectedUrl));
        String signature = stringUrl.substring(expectedUrl.length());
        StringBuilder signedMessageBuilder = new StringBuilder();
        signedMessageBuilder.append(GET).append("\n\n\n").append((42L + 1209600)).append("\n/").append(StorageImplTest.BUCKET_NAME1).append(escapedBlobName);
        Signature signer = Signature.getInstance("SHA256withRSA");
        signer.initVerify(StorageImplTest.publicKey);
        signer.update(signedMessageBuilder.toString().getBytes(StandardCharsets.UTF_8));
        Assert.assertTrue(signer.verify(BaseEncoding.base64().decode(URLDecoder.decode(signature, StandardCharsets.UTF_8.name()))));
    }

    @Test
    public void testSignUrlForBlobWithSlashesAndHostName() throws UnsupportedEncodingException, InvalidKeyException, NoSuchAlgorithmException, SignatureException {
        EasyMock.replay(storageRpcMock);
        ServiceAccountCredentials credentials = ServiceAccountCredentials.newBuilder().setClientEmail(StorageImplTest.ACCOUNT).setPrivateKey(StorageImplTest.privateKey).build();
        storage = options.toBuilder().setCredentials(credentials).build().getService();
        String blobName = "/foo/bar/baz #%20other cool stuff.txt";
        URL url = storage.signUrl(BlobInfo.newBuilder(StorageImplTest.BUCKET_NAME1, blobName).build(), 14, TimeUnit.DAYS, SignUrlOption.withHostName("https://example.com"));
        String escapedBlobName = UrlEscapers.urlFragmentEscaper().escape(blobName);
        String stringUrl = url.toString();
        String expectedUrl = new StringBuilder("https://example.com/").append(StorageImplTest.BUCKET_NAME1).append(escapedBlobName).append("?GoogleAccessId=").append(StorageImplTest.ACCOUNT).append("&Expires=").append((42L + 1209600)).append("&Signature=").toString();
        Assert.assertTrue(stringUrl.startsWith(expectedUrl));
        String signature = stringUrl.substring(expectedUrl.length());
        StringBuilder signedMessageBuilder = new StringBuilder();
        signedMessageBuilder.append(GET).append("\n\n\n").append((42L + 1209600)).append("\n/").append(StorageImplTest.BUCKET_NAME1).append(escapedBlobName);
        Signature signer = Signature.getInstance("SHA256withRSA");
        signer.initVerify(StorageImplTest.publicKey);
        signer.update(signedMessageBuilder.toString().getBytes(StandardCharsets.UTF_8));
        Assert.assertTrue(signer.verify(BaseEncoding.base64().decode(URLDecoder.decode(signature, StandardCharsets.UTF_8.name()))));
    }

    @Test
    public void testGetAllArray() {
        BlobId blobId1 = BlobId.of(StorageImplTest.BUCKET_NAME1, StorageImplTest.BLOB_NAME1);
        BlobId blobId2 = BlobId.of(StorageImplTest.BUCKET_NAME1, StorageImplTest.BLOB_NAME2);
        RpcBatch batchMock = EasyMock.createMock(RpcBatch.class);
        Capture<RpcBatch.Callback<StorageObject>> callback1 = Capture.newInstance();
        Capture<RpcBatch.Callback<StorageObject>> callback2 = Capture.newInstance();
        batchMock.addGet(EasyMock.eq(blobId1.toPb()), EasyMock.capture(callback1), EasyMock.eq(ImmutableMap.<StorageRpc.Option, Object>of()));
        batchMock.addGet(EasyMock.eq(blobId2.toPb()), EasyMock.capture(callback2), EasyMock.eq(ImmutableMap.<StorageRpc.Option, Object>of()));
        EasyMock.expect(storageRpcMock.createBatch()).andReturn(batchMock);
        batchMock.submit();
        EasyMock.replay(storageRpcMock, batchMock);
        initializeService();
        List<Blob> resultBlobs = storage.get(blobId1, blobId2);
        callback1.getValue().onSuccess(StorageImplTest.BLOB_INFO1.toPb());
        callback2.getValue().onFailure(new GoogleJsonError());
        Assert.assertEquals(2, resultBlobs.size());
        Assert.assertEquals(new Blob(storage, new BlobInfo.BuilderImpl(StorageImplTest.BLOB_INFO1)), resultBlobs.get(0));
        Assert.assertNull(resultBlobs.get(1));
        EasyMock.verify(batchMock);
    }

    @Test
    public void testGetAllArrayIterable() {
        BlobId blobId1 = BlobId.of(StorageImplTest.BUCKET_NAME1, StorageImplTest.BLOB_NAME1);
        BlobId blobId2 = BlobId.of(StorageImplTest.BUCKET_NAME1, StorageImplTest.BLOB_NAME2);
        RpcBatch batchMock = EasyMock.createMock(RpcBatch.class);
        Capture<RpcBatch.Callback<StorageObject>> callback1 = Capture.newInstance();
        Capture<RpcBatch.Callback<StorageObject>> callback2 = Capture.newInstance();
        batchMock.addGet(EasyMock.eq(blobId1.toPb()), EasyMock.capture(callback1), EasyMock.eq(ImmutableMap.<StorageRpc.Option, Object>of()));
        batchMock.addGet(EasyMock.eq(blobId2.toPb()), EasyMock.capture(callback2), EasyMock.eq(ImmutableMap.<StorageRpc.Option, Object>of()));
        EasyMock.expect(storageRpcMock.createBatch()).andReturn(batchMock);
        batchMock.submit();
        EasyMock.replay(storageRpcMock, batchMock);
        initializeService();
        List<Blob> resultBlobs = storage.get(ImmutableList.of(blobId1, blobId2));
        callback1.getValue().onSuccess(StorageImplTest.BLOB_INFO1.toPb());
        callback2.getValue().onFailure(new GoogleJsonError());
        Assert.assertEquals(2, resultBlobs.size());
        Assert.assertEquals(new Blob(storage, new BlobInfo.BuilderImpl(StorageImplTest.BLOB_INFO1)), resultBlobs.get(0));
        Assert.assertNull(resultBlobs.get(1));
        EasyMock.verify(batchMock);
    }

    @Test
    public void testDeleteAllArray() {
        BlobId blobId1 = BlobId.of(StorageImplTest.BUCKET_NAME1, StorageImplTest.BLOB_NAME1);
        BlobId blobId2 = BlobId.of(StorageImplTest.BUCKET_NAME1, StorageImplTest.BLOB_NAME2);
        RpcBatch batchMock = EasyMock.createMock(RpcBatch.class);
        Capture<RpcBatch.Callback<Void>> callback1 = Capture.newInstance();
        Capture<RpcBatch.Callback<Void>> callback2 = Capture.newInstance();
        batchMock.addDelete(EasyMock.eq(blobId1.toPb()), EasyMock.capture(callback1), EasyMock.eq(ImmutableMap.<StorageRpc.Option, Object>of()));
        batchMock.addDelete(EasyMock.eq(blobId2.toPb()), EasyMock.capture(callback2), EasyMock.eq(ImmutableMap.<StorageRpc.Option, Object>of()));
        EasyMock.expect(storageRpcMock.createBatch()).andReturn(batchMock);
        batchMock.submit();
        EasyMock.replay(storageRpcMock, batchMock);
        initializeService();
        List<Boolean> result = storage.delete(blobId1, blobId2);
        callback1.getValue().onSuccess(null);
        callback2.getValue().onFailure(new GoogleJsonError());
        Assert.assertEquals(2, result.size());
        Assert.assertTrue(result.get(0));
        Assert.assertFalse(result.get(1));
        EasyMock.verify(batchMock);
    }

    @Test
    public void testDeleteAllIterable() {
        BlobId blobId1 = BlobId.of(StorageImplTest.BUCKET_NAME1, StorageImplTest.BLOB_NAME1);
        BlobId blobId2 = BlobId.of(StorageImplTest.BUCKET_NAME1, StorageImplTest.BLOB_NAME2);
        RpcBatch batchMock = EasyMock.createMock(RpcBatch.class);
        Capture<RpcBatch.Callback<Void>> callback1 = Capture.newInstance();
        Capture<RpcBatch.Callback<Void>> callback2 = Capture.newInstance();
        batchMock.addDelete(EasyMock.eq(blobId1.toPb()), EasyMock.capture(callback1), EasyMock.eq(ImmutableMap.<StorageRpc.Option, Object>of()));
        batchMock.addDelete(EasyMock.eq(blobId2.toPb()), EasyMock.capture(callback2), EasyMock.eq(ImmutableMap.<StorageRpc.Option, Object>of()));
        EasyMock.expect(storageRpcMock.createBatch()).andReturn(batchMock);
        batchMock.submit();
        EasyMock.replay(storageRpcMock, batchMock);
        initializeService();
        List<Boolean> result = storage.delete(blobId1, blobId2);
        callback1.getValue().onSuccess(null);
        callback2.getValue().onFailure(new GoogleJsonError());
        Assert.assertEquals(2, result.size());
        Assert.assertTrue(result.get(0));
        Assert.assertFalse(result.get(1));
        EasyMock.verify(batchMock);
    }

    @Test
    public void testUpdateAllArray() {
        RpcBatch batchMock = EasyMock.createMock(RpcBatch.class);
        Capture<RpcBatch.Callback<StorageObject>> callback1 = Capture.newInstance();
        Capture<RpcBatch.Callback<StorageObject>> callback2 = Capture.newInstance();
        batchMock.addPatch(EasyMock.eq(StorageImplTest.BLOB_INFO1.toPb()), EasyMock.capture(callback1), EasyMock.eq(ImmutableMap.<StorageRpc.Option, Object>of()));
        batchMock.addPatch(EasyMock.eq(StorageImplTest.BLOB_INFO2.toPb()), EasyMock.capture(callback2), EasyMock.eq(ImmutableMap.<StorageRpc.Option, Object>of()));
        EasyMock.expect(storageRpcMock.createBatch()).andReturn(batchMock);
        batchMock.submit();
        EasyMock.replay(storageRpcMock, batchMock);
        initializeService();
        List<Blob> resultBlobs = storage.update(StorageImplTest.BLOB_INFO1, StorageImplTest.BLOB_INFO2);
        callback1.getValue().onSuccess(StorageImplTest.BLOB_INFO1.toPb());
        callback2.getValue().onFailure(new GoogleJsonError());
        Assert.assertEquals(2, resultBlobs.size());
        Assert.assertEquals(new Blob(storage, new BlobInfo.BuilderImpl(StorageImplTest.BLOB_INFO1)), resultBlobs.get(0));
        Assert.assertNull(resultBlobs.get(1));
        EasyMock.verify(batchMock);
    }

    @Test
    public void testUpdateAllIterable() {
        RpcBatch batchMock = EasyMock.createMock(RpcBatch.class);
        Capture<RpcBatch.Callback<StorageObject>> callback1 = Capture.newInstance();
        Capture<RpcBatch.Callback<StorageObject>> callback2 = Capture.newInstance();
        batchMock.addPatch(EasyMock.eq(StorageImplTest.BLOB_INFO1.toPb()), EasyMock.capture(callback1), EasyMock.eq(ImmutableMap.<StorageRpc.Option, Object>of()));
        batchMock.addPatch(EasyMock.eq(StorageImplTest.BLOB_INFO2.toPb()), EasyMock.capture(callback2), EasyMock.eq(ImmutableMap.<StorageRpc.Option, Object>of()));
        EasyMock.expect(storageRpcMock.createBatch()).andReturn(batchMock);
        batchMock.submit();
        EasyMock.replay(storageRpcMock, batchMock);
        initializeService();
        List<Blob> resultBlobs = storage.update(ImmutableList.of(StorageImplTest.BLOB_INFO1, StorageImplTest.BLOB_INFO2));
        callback1.getValue().onSuccess(StorageImplTest.BLOB_INFO1.toPb());
        callback2.getValue().onFailure(new GoogleJsonError());
        Assert.assertEquals(2, resultBlobs.size());
        Assert.assertEquals(new Blob(storage, new BlobInfo.BuilderImpl(StorageImplTest.BLOB_INFO1)), resultBlobs.get(0));
        Assert.assertNull(resultBlobs.get(1));
        EasyMock.verify(batchMock);
    }

    @Test
    public void testGetBucketAcl() {
        EasyMock.expect(storageRpcMock.getAcl(StorageImplTest.BUCKET_NAME1, "allAuthenticatedUsers", new HashMap<StorageRpc.Option, Object>())).andReturn(StorageImplTest.ACL.toBucketPb());
        EasyMock.replay(storageRpcMock);
        initializeService();
        Acl acl = storage.getAcl(StorageImplTest.BUCKET_NAME1, User.ofAllAuthenticatedUsers());
        Assert.assertEquals(StorageImplTest.ACL, acl);
    }

    @Test
    public void testGetBucketAclNull() {
        EasyMock.expect(storageRpcMock.getAcl(StorageImplTest.BUCKET_NAME1, "allAuthenticatedUsers", new HashMap<StorageRpc.Option, Object>())).andReturn(null);
        EasyMock.replay(storageRpcMock);
        initializeService();
        Assert.assertNull(storage.getAcl(StorageImplTest.BUCKET_NAME1, User.ofAllAuthenticatedUsers()));
    }

    @Test
    public void testDeleteBucketAcl() {
        EasyMock.expect(storageRpcMock.deleteAcl(StorageImplTest.BUCKET_NAME1, "allAuthenticatedUsers", new HashMap<StorageRpc.Option, Object>())).andReturn(true);
        EasyMock.replay(storageRpcMock);
        initializeService();
        Assert.assertTrue(storage.deleteAcl(StorageImplTest.BUCKET_NAME1, User.ofAllAuthenticatedUsers()));
    }

    @Test
    public void testCreateBucketAcl() {
        Acl returnedAcl = StorageImplTest.ACL.toBuilder().setEtag("ETAG").setId("ID").build();
        EasyMock.expect(storageRpcMock.createAcl(StorageImplTest.ACL.toBucketPb().setBucket(StorageImplTest.BUCKET_NAME1), new HashMap<StorageRpc.Option, Object>())).andReturn(returnedAcl.toBucketPb());
        EasyMock.replay(storageRpcMock);
        initializeService();
        Acl acl = storage.createAcl(StorageImplTest.BUCKET_NAME1, StorageImplTest.ACL);
        Assert.assertEquals(returnedAcl, acl);
    }

    @Test
    public void testUpdateBucketAcl() {
        Acl returnedAcl = StorageImplTest.ACL.toBuilder().setEtag("ETAG").setId("ID").build();
        EasyMock.expect(storageRpcMock.patchAcl(StorageImplTest.ACL.toBucketPb().setBucket(StorageImplTest.BUCKET_NAME1), new HashMap<StorageRpc.Option, Object>())).andReturn(returnedAcl.toBucketPb());
        EasyMock.replay(storageRpcMock);
        initializeService();
        Acl acl = storage.updateAcl(StorageImplTest.BUCKET_NAME1, StorageImplTest.ACL);
        Assert.assertEquals(returnedAcl, acl);
    }

    @Test
    public void testListBucketAcl() {
        EasyMock.expect(storageRpcMock.listAcls(StorageImplTest.BUCKET_NAME1, new HashMap<StorageRpc.Option, Object>())).andReturn(ImmutableList.of(StorageImplTest.ACL.toBucketPb(), StorageImplTest.OTHER_ACL.toBucketPb()));
        EasyMock.replay(storageRpcMock);
        initializeService();
        List<Acl> acls = storage.listAcls(StorageImplTest.BUCKET_NAME1);
        Assert.assertEquals(ImmutableList.of(StorageImplTest.ACL, StorageImplTest.OTHER_ACL), acls);
    }

    @Test
    public void testGetDefaultBucketAcl() {
        EasyMock.expect(storageRpcMock.getDefaultAcl(StorageImplTest.BUCKET_NAME1, "allAuthenticatedUsers")).andReturn(StorageImplTest.ACL.toObjectPb());
        EasyMock.replay(storageRpcMock);
        initializeService();
        Acl acl = storage.getDefaultAcl(StorageImplTest.BUCKET_NAME1, User.ofAllAuthenticatedUsers());
        Assert.assertEquals(StorageImplTest.ACL, acl);
    }

    @Test
    public void testGetDefaultBucketAclNull() {
        EasyMock.expect(storageRpcMock.getDefaultAcl(StorageImplTest.BUCKET_NAME1, "allAuthenticatedUsers")).andReturn(null);
        EasyMock.replay(storageRpcMock);
        initializeService();
        Assert.assertNull(storage.getDefaultAcl(StorageImplTest.BUCKET_NAME1, User.ofAllAuthenticatedUsers()));
    }

    @Test
    public void testDeleteDefaultBucketAcl() {
        EasyMock.expect(storageRpcMock.deleteDefaultAcl(StorageImplTest.BUCKET_NAME1, "allAuthenticatedUsers")).andReturn(true);
        EasyMock.replay(storageRpcMock);
        initializeService();
        Assert.assertTrue(storage.deleteDefaultAcl(StorageImplTest.BUCKET_NAME1, User.ofAllAuthenticatedUsers()));
    }

    @Test
    public void testCreateDefaultBucketAcl() {
        Acl returnedAcl = StorageImplTest.ACL.toBuilder().setEtag("ETAG").setId("ID").build();
        EasyMock.expect(storageRpcMock.createDefaultAcl(StorageImplTest.ACL.toObjectPb().setBucket(StorageImplTest.BUCKET_NAME1))).andReturn(returnedAcl.toObjectPb());
        EasyMock.replay(storageRpcMock);
        initializeService();
        Acl acl = storage.createDefaultAcl(StorageImplTest.BUCKET_NAME1, StorageImplTest.ACL);
        Assert.assertEquals(returnedAcl, acl);
    }

    @Test
    public void testUpdateDefaultBucketAcl() {
        Acl returnedAcl = StorageImplTest.ACL.toBuilder().setEtag("ETAG").setId("ID").build();
        EasyMock.expect(storageRpcMock.patchDefaultAcl(StorageImplTest.ACL.toObjectPb().setBucket(StorageImplTest.BUCKET_NAME1))).andReturn(returnedAcl.toObjectPb());
        EasyMock.replay(storageRpcMock);
        initializeService();
        Acl acl = storage.updateDefaultAcl(StorageImplTest.BUCKET_NAME1, StorageImplTest.ACL);
        Assert.assertEquals(returnedAcl, acl);
    }

    @Test
    public void testListDefaultBucketAcl() {
        EasyMock.expect(storageRpcMock.listDefaultAcls(StorageImplTest.BUCKET_NAME1)).andReturn(ImmutableList.of(StorageImplTest.ACL.toObjectPb(), StorageImplTest.OTHER_ACL.toObjectPb()));
        EasyMock.replay(storageRpcMock);
        initializeService();
        List<Acl> acls = storage.listDefaultAcls(StorageImplTest.BUCKET_NAME1);
        Assert.assertEquals(ImmutableList.of(StorageImplTest.ACL, StorageImplTest.OTHER_ACL), acls);
    }

    @Test
    public void testGetBlobAcl() {
        BlobId blobId = BlobId.of(StorageImplTest.BUCKET_NAME1, StorageImplTest.BLOB_NAME1, 42L);
        EasyMock.expect(storageRpcMock.getAcl(StorageImplTest.BUCKET_NAME1, StorageImplTest.BLOB_NAME1, 42L, "allAuthenticatedUsers")).andReturn(StorageImplTest.ACL.toObjectPb());
        EasyMock.replay(storageRpcMock);
        initializeService();
        Acl acl = storage.getAcl(blobId, User.ofAllAuthenticatedUsers());
        Assert.assertEquals(StorageImplTest.ACL, acl);
    }

    @Test
    public void testGetBlobAclNull() {
        BlobId blobId = BlobId.of(StorageImplTest.BUCKET_NAME1, StorageImplTest.BLOB_NAME1, 42L);
        EasyMock.expect(storageRpcMock.getAcl(StorageImplTest.BUCKET_NAME1, StorageImplTest.BLOB_NAME1, 42L, "allAuthenticatedUsers")).andReturn(null);
        EasyMock.replay(storageRpcMock);
        initializeService();
        Assert.assertNull(storage.getAcl(blobId, User.ofAllAuthenticatedUsers()));
    }

    @Test
    public void testDeleteBlobAcl() {
        BlobId blobId = BlobId.of(StorageImplTest.BUCKET_NAME1, StorageImplTest.BLOB_NAME1, 42L);
        EasyMock.expect(storageRpcMock.deleteAcl(StorageImplTest.BUCKET_NAME1, StorageImplTest.BLOB_NAME1, 42L, "allAuthenticatedUsers")).andReturn(true);
        EasyMock.replay(storageRpcMock);
        initializeService();
        Assert.assertTrue(storage.deleteAcl(blobId, User.ofAllAuthenticatedUsers()));
    }

    @Test
    public void testCreateBlobAcl() {
        BlobId blobId = BlobId.of(StorageImplTest.BUCKET_NAME1, StorageImplTest.BLOB_NAME1, 42L);
        Acl returnedAcl = StorageImplTest.ACL.toBuilder().setEtag("ETAG").setId("ID").build();
        EasyMock.expect(storageRpcMock.createAcl(StorageImplTest.ACL.toObjectPb().setBucket(StorageImplTest.BUCKET_NAME1).setObject(StorageImplTest.BLOB_NAME1).setGeneration(42L))).andReturn(returnedAcl.toObjectPb());
        EasyMock.replay(storageRpcMock);
        initializeService();
        Acl acl = storage.createAcl(blobId, StorageImplTest.ACL);
        Assert.assertEquals(returnedAcl, acl);
    }

    @Test
    public void testUpdateBlobAcl() {
        BlobId blobId = BlobId.of(StorageImplTest.BUCKET_NAME1, StorageImplTest.BLOB_NAME1, 42L);
        Acl returnedAcl = StorageImplTest.ACL.toBuilder().setEtag("ETAG").setId("ID").build();
        EasyMock.expect(storageRpcMock.patchAcl(StorageImplTest.ACL.toObjectPb().setBucket(StorageImplTest.BUCKET_NAME1).setObject(StorageImplTest.BLOB_NAME1).setGeneration(42L))).andReturn(returnedAcl.toObjectPb());
        EasyMock.replay(storageRpcMock);
        initializeService();
        Acl acl = storage.updateAcl(blobId, StorageImplTest.ACL);
        Assert.assertEquals(returnedAcl, acl);
    }

    @Test
    public void testListBlobAcl() {
        BlobId blobId = BlobId.of(StorageImplTest.BUCKET_NAME1, StorageImplTest.BLOB_NAME1, 42L);
        EasyMock.expect(storageRpcMock.listAcls(StorageImplTest.BUCKET_NAME1, StorageImplTest.BLOB_NAME1, 42L)).andReturn(ImmutableList.of(StorageImplTest.ACL.toObjectPb(), StorageImplTest.OTHER_ACL.toObjectPb()));
        EasyMock.replay(storageRpcMock);
        initializeService();
        List<Acl> acls = storage.listAcls(blobId);
        Assert.assertEquals(ImmutableList.of(StorageImplTest.ACL, StorageImplTest.OTHER_ACL), acls);
    }

    @Test
    public void testGetIamPolicy() {
        EasyMock.expect(storageRpcMock.getIamPolicy(StorageImplTest.BUCKET_NAME1, StorageImplTest.EMPTY_RPC_OPTIONS)).andReturn(StorageImplTest.API_POLICY1);
        EasyMock.replay(storageRpcMock);
        initializeService();
        Assert.assertEquals(StorageImplTest.LIB_POLICY1, storage.getIamPolicy(StorageImplTest.BUCKET_NAME1));
    }

    @Test
    public void testSetIamPolicy() {
        com.google.api.services.storage.model.Policy preCommitApiPolicy = new com.google.api.services.storage.model.Policy().setBindings(ImmutableList.of(new Bindings().setMembers(ImmutableList.of("allUsers")).setRole("roles/storage.objectViewer"), new Bindings().setMembers(ImmutableList.of("user:test1@gmail.com", "user:test2@gmail.com")).setRole("roles/storage.objectAdmin"), new Bindings().setMembers(ImmutableList.of("group:test-group@gmail.com")).setRole("roles/storage.admin"))).setEtag(StorageImplTest.POLICY_ETAG1);
        // postCommitApiPolicy is identical but for the etag, which has been updated.
        com.google.api.services.storage.model.Policy postCommitApiPolicy = new com.google.api.services.storage.model.Policy().setBindings(ImmutableList.of(new Bindings().setMembers(ImmutableList.of("allUsers")).setRole("roles/storage.objectViewer"), new Bindings().setMembers(ImmutableList.of("user:test1@gmail.com", "user:test2@gmail.com")).setRole("roles/storage.objectAdmin"), new Bindings().setMembers(ImmutableList.of("group:test-group@gmail.com")).setRole("roles/storage.admin"))).setEtag(StorageImplTest.POLICY_ETAG2);
        Policy postCommitLibPolicy = Policy.newBuilder().addIdentity(StorageRoles.objectViewer(), Identity.allUsers()).addIdentity(StorageRoles.objectAdmin(), Identity.user("test1@gmail.com"), Identity.user("test2@gmail.com")).addIdentity(StorageRoles.admin(), Identity.group("test-group@gmail.com")).setEtag(StorageImplTest.POLICY_ETAG2).build();
        EasyMock.expect(storageRpcMock.getIamPolicy(StorageImplTest.BUCKET_NAME1, StorageImplTest.EMPTY_RPC_OPTIONS)).andReturn(StorageImplTest.API_POLICY1);
        EasyMock.expect(storageRpcMock.setIamPolicy(EasyMock.eq(StorageImplTest.BUCKET_NAME1), ApiPolicyMatcher.eqApiPolicy(preCommitApiPolicy), EasyMock.eq(StorageImplTest.EMPTY_RPC_OPTIONS))).andReturn(postCommitApiPolicy);
        EasyMock.replay(storageRpcMock);
        initializeService();
        Policy currentPolicy = storage.getIamPolicy(StorageImplTest.BUCKET_NAME1);
        Policy updatedPolicy = storage.setIamPolicy(StorageImplTest.BUCKET_NAME1, currentPolicy.toBuilder().addIdentity(StorageRoles.admin(), Identity.group("test-group@gmail.com")).build());
        Assert.assertEquals(updatedPolicy, postCommitLibPolicy);
    }

    @Test
    public void testTestIamPermissionsNull() {
        ImmutableList<Boolean> expectedPermissions = ImmutableList.of(false, false, false);
        ImmutableList<String> checkedPermissions = ImmutableList.of("storage.buckets.get", "storage.buckets.getIamPolicy", "storage.objects.list");
        EasyMock.expect(storageRpcMock.testIamPermissions(StorageImplTest.BUCKET_NAME1, checkedPermissions, StorageImplTest.EMPTY_RPC_OPTIONS)).andReturn(new TestIamPermissionsResponse());
        EasyMock.replay(storageRpcMock);
        initializeService();
        Assert.assertEquals(expectedPermissions, storage.testIamPermissions(StorageImplTest.BUCKET_NAME1, checkedPermissions));
    }

    @Test
    public void testTestIamPermissionsNonNull() {
        ImmutableList<Boolean> expectedPermissions = ImmutableList.of(true, false, true);
        ImmutableList<String> checkedPermissions = ImmutableList.of("storage.buckets.get", "storage.buckets.getIamPolicy", "storage.objects.list");
        EasyMock.expect(storageRpcMock.testIamPermissions(StorageImplTest.BUCKET_NAME1, checkedPermissions, StorageImplTest.EMPTY_RPC_OPTIONS)).andReturn(new TestIamPermissionsResponse().setPermissions(ImmutableList.of("storage.objects.list", "storage.buckets.get")));
        EasyMock.replay(storageRpcMock);
        initializeService();
        Assert.assertEquals(expectedPermissions, storage.testIamPermissions(StorageImplTest.BUCKET_NAME1, checkedPermissions));
    }

    @Test
    public void testLockRetentionPolicy() {
        EasyMock.expect(storageRpcMock.lockRetentionPolicy(StorageImplTest.BUCKET_INFO3.toPb(), StorageImplTest.BUCKET_TARGET_OPTIONS_LOCK_RETENTION_POLICY)).andReturn(StorageImplTest.BUCKET_INFO3.toPb());
        EasyMock.replay(storageRpcMock);
        initializeService();
        Bucket bucket = storage.lockRetentionPolicy(StorageImplTest.BUCKET_INFO3, StorageImplTest.BUCKET_TARGET_METAGENERATION, StorageImplTest.BUCKET_TARGET_USER_PROJECT);
        Assert.assertEquals(expectedBucket3, bucket);
    }

    @Test
    public void testGetServiceAccount() {
        EasyMock.expect(storageRpcMock.getServiceAccount("projectId")).andReturn(StorageImplTest.SERVICE_ACCOUNT.toPb());
        EasyMock.replay(storageRpcMock);
        initializeService();
        ServiceAccount serviceAccount = storage.getServiceAccount("projectId");
        Assert.assertEquals(StorageImplTest.SERVICE_ACCOUNT, serviceAccount);
    }

    @Test
    public void testRetryableException() {
        BlobId blob = BlobId.of(StorageImplTest.BUCKET_NAME1, StorageImplTest.BLOB_NAME1);
        EasyMock.expect(storageRpcMock.get(blob.toPb(), StorageImplTest.EMPTY_RPC_OPTIONS)).andThrow(new StorageException(500, "internalError")).andReturn(StorageImplTest.BLOB_INFO1.toPb());
        EasyMock.replay(storageRpcMock);
        storage = options.toBuilder().setRetrySettings(ServiceOptions.getDefaultRetrySettings()).build().getService();
        initializeServiceDependentObjects();
        Blob readBlob = storage.get(blob);
        Assert.assertEquals(expectedBlob1, readBlob);
    }

    @Test
    public void testNonRetryableException() {
        BlobId blob = BlobId.of(StorageImplTest.BUCKET_NAME1, StorageImplTest.BLOB_NAME1);
        String exceptionMessage = "Not Implemented";
        EasyMock.expect(storageRpcMock.get(blob.toPb(), StorageImplTest.EMPTY_RPC_OPTIONS)).andThrow(new StorageException(501, exceptionMessage));
        EasyMock.replay(storageRpcMock);
        storage = options.toBuilder().setRetrySettings(ServiceOptions.getDefaultRetrySettings()).build().getService();
        initializeServiceDependentObjects();
        thrown.expect(StorageException.class);
        thrown.expectMessage(exceptionMessage);
        storage.get(blob);
    }

    @Test
    public void testRuntimeException() {
        BlobId blob = BlobId.of(StorageImplTest.BUCKET_NAME1, StorageImplTest.BLOB_NAME1);
        String exceptionMessage = "Artificial runtime exception";
        EasyMock.expect(storageRpcMock.get(blob.toPb(), StorageImplTest.EMPTY_RPC_OPTIONS)).andThrow(new RuntimeException(exceptionMessage));
        EasyMock.replay(storageRpcMock);
        storage = options.toBuilder().setRetrySettings(ServiceOptions.getDefaultRetrySettings()).build().getService();
        thrown.expect(StorageException.class);
        thrown.expectMessage(exceptionMessage);
        storage.get(blob);
    }
}

