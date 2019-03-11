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


import Bucket.BucketSourceOption;
import Bucket.Builder;
import Role.OWNER;
import Role.READER;
import Storage.BlobTargetOption;
import Storage.BlobWriteOption;
import Storage.BucketGetOption;
import Storage.BucketTargetOption;
import Storage.PredefinedAcl;
import com.google.api.gax.paging.Page;
import com.google.cloud.PageImpl;
import com.google.cloud.storage.Acl.Project.ProjectRole;
import com.google.cloud.storage.Acl.User;
import com.google.cloud.storage.BucketInfo.AgeDeleteRule;
import com.google.cloud.storage.BucketInfo.DeleteRule;
import com.google.cloud.storage.BucketInfo.LifecycleRule.LifecycleAction;
import com.google.cloud.storage.BucketInfo.LifecycleRule.LifecycleCondition;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.io.BaseEncoding;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.Key;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.crypto.spec.SecretKeySpec;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static StorageClass.STANDARD;


public class BucketTest {
    private static final Acl ACL = Acl.of(User.ofAllAuthenticatedUsers(), Role.OWNER);

    private static final Acl OTHER_ACL = Acl.of(new com.google.cloud.storage.Acl.Project(ProjectRole.OWNERS, "p"), READER);

    private static final List<Acl> ACLS = ImmutableList.of(BucketTest.ACL, BucketTest.OTHER_ACL);

    private static final String ETAG = "0xFF00";

    private static final String GENERATED_ID = "B/N:1";

    private static final Long META_GENERATION = 10L;

    private static final User OWNER = new User("user@gmail.com");

    private static final String SELF_LINK = "http://storage/b/n";

    private static final Long CREATE_TIME = System.currentTimeMillis();

    private static final List<Cors> CORS = Collections.singletonList(Cors.newBuilder().build());

    private static final List<Acl> DEFAULT_ACL = Collections.singletonList(Acl.of(User.ofAllAuthenticatedUsers(), Role.WRITER));

    private static final List<? extends DeleteRule> DELETE_RULES = Collections.singletonList(new AgeDeleteRule(5));

    private static final List<? extends BucketInfo.LifecycleRule> LIFECYCLE_RULES = Collections.singletonList(new com.google.cloud.storage.BucketInfo.LifecycleRule(LifecycleAction.newDeleteAction(), LifecycleCondition.newBuilder().setAge(5).build()));

    private static final String INDEX_PAGE = "index.html";

    private static final String NOT_FOUND_PAGE = "error.html";

    private static final String LOCATION = "ASIA";

    private static final StorageClass STORAGE_CLASS = STANDARD;

    private static final String DEFAULT_KMS_KEY_NAME = "projects/p/locations/kr-loc/keyRings/kr/cryptoKeys/key";

    private static final Boolean VERSIONING_ENABLED = true;

    private static final Map<String, String> BUCKET_LABELS = ImmutableMap.of("label1", "value1");

    private static final Boolean REQUESTER_PAYS = true;

    private static final String USER_PROJECT = "test-project";

    private static final Boolean DEFAULT_EVENT_BASED_HOLD = true;

    private static final Long RETENTION_EFFECTIVE_TIME = 10L;

    private static final Long RETENTION_PERIOD = 10L;

    private static final Boolean RETENTION_POLICY_IS_LOCKED = false;

    private static final BucketInfo FULL_BUCKET_INFO = BucketInfo.newBuilder("b").setAcl(BucketTest.ACLS).setEtag(BucketTest.ETAG).setGeneratedId(BucketTest.GENERATED_ID).setMetageneration(BucketTest.META_GENERATION).setOwner(BucketTest.OWNER).setSelfLink(BucketTest.SELF_LINK).setCors(BucketTest.CORS).setCreateTime(BucketTest.CREATE_TIME).setDefaultAcl(BucketTest.DEFAULT_ACL).setDeleteRules(BucketTest.DELETE_RULES).setLifecycleRules(BucketTest.LIFECYCLE_RULES).setIndexPage(BucketTest.INDEX_PAGE).setNotFoundPage(BucketTest.NOT_FOUND_PAGE).setLocation(BucketTest.LOCATION).setStorageClass(BucketTest.STORAGE_CLASS).setVersioningEnabled(BucketTest.VERSIONING_ENABLED).setLabels(BucketTest.BUCKET_LABELS).setRequesterPays(BucketTest.REQUESTER_PAYS).setDefaultKmsKeyName(BucketTest.DEFAULT_KMS_KEY_NAME).setDefaultEventBasedHold(BucketTest.DEFAULT_EVENT_BASED_HOLD).setRetentionEffectiveTime(BucketTest.RETENTION_EFFECTIVE_TIME).setRetentionPeriod(BucketTest.RETENTION_PERIOD).setRetentionPolicyIsLocked(BucketTest.RETENTION_POLICY_IS_LOCKED).build();

    private static final BucketInfo BUCKET_INFO = BucketInfo.newBuilder("b").setMetageneration(42L).build();

    private static final String CONTENT_TYPE = "text/plain";

    private static final String BASE64_KEY = "JVzfVl8NLD9FjedFuStegjRfES5ll5zc59CIXw572OA=";

    private static final Key KEY = new SecretKeySpec(BaseEncoding.base64().decode(BucketTest.BASE64_KEY), "AES256");

    private Storage storage;

    private Storage serviceMockReturnsOptions = createMock(Storage.class);

    private StorageOptions mockOptions = createMock(StorageOptions.class);

    private Bucket bucket;

    private Bucket expectedBucket;

    private List<Blob> blobResults;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testExists_True() throws Exception {
        initializeExpectedBucket(4);
        Storage[] expectedOptions = new BucketGetOption[]{ BucketGetOption.fields() };
        expect(storage.getOptions()).andReturn(mockOptions);
        expect(storage.get(BucketTest.BUCKET_INFO.getName(), expectedOptions)).andReturn(expectedBucket);
        replay(storage);
        initializeBucket();
        Assert.assertTrue(bucket.exists());
    }

    @Test
    public void testExists_False() throws Exception {
        initializeExpectedBucket(4);
        Storage[] expectedOptions = new BucketGetOption[]{ BucketGetOption.fields() };
        expect(storage.getOptions()).andReturn(mockOptions);
        expect(storage.get(BucketTest.BUCKET_INFO.getName(), expectedOptions)).andReturn(null);
        replay(storage);
        initializeBucket();
        Assert.assertFalse(bucket.exists());
    }

    @Test
    public void testReload() throws Exception {
        initializeExpectedBucket(5);
        BucketInfo updatedInfo = BucketTest.BUCKET_INFO.toBuilder().setNotFoundPage("p").build();
        Bucket expectedUpdatedBucket = new Bucket(serviceMockReturnsOptions, new BucketInfo.BuilderImpl(updatedInfo));
        expect(storage.getOptions()).andReturn(mockOptions);
        expect(storage.get(updatedInfo.getName())).andReturn(expectedUpdatedBucket);
        replay(storage);
        initializeBucket();
        Bucket updatedBucket = bucket.reload();
        Assert.assertEquals(expectedUpdatedBucket, updatedBucket);
    }

    @Test
    public void testReloadNull() throws Exception {
        initializeExpectedBucket(4);
        expect(storage.getOptions()).andReturn(mockOptions);
        expect(storage.get(BucketTest.BUCKET_INFO.getName())).andReturn(null);
        replay(storage);
        initializeBucket();
        Assert.assertNull(bucket.reload());
    }

    @Test
    public void testReloadWithOptions() throws Exception {
        initializeExpectedBucket(5);
        BucketInfo updatedInfo = BucketTest.BUCKET_INFO.toBuilder().setNotFoundPage("p").build();
        Bucket expectedUpdatedBucket = new Bucket(serviceMockReturnsOptions, new BucketInfo.BuilderImpl(updatedInfo));
        expect(storage.getOptions()).andReturn(mockOptions);
        expect(storage.get(updatedInfo.getName(), BucketGetOption.metagenerationMatch(42L))).andReturn(expectedUpdatedBucket);
        replay(storage);
        initializeBucket();
        Bucket updatedBucket = bucket.reload(BucketSourceOption.metagenerationMatch());
        Assert.assertEquals(expectedUpdatedBucket, updatedBucket);
    }

    @Test
    public void testUpdate() throws Exception {
        initializeExpectedBucket(5);
        Bucket expectedUpdatedBucket = expectedBucket.toBuilder().setNotFoundPage("p").build();
        expect(storage.getOptions()).andReturn(mockOptions).times(2);
        expect(storage.update(expectedUpdatedBucket)).andReturn(expectedUpdatedBucket);
        replay(storage);
        initializeBucket();
        Bucket updatedBucket = new Bucket(storage, new BucketInfo.BuilderImpl(expectedUpdatedBucket));
        Bucket actualUpdatedBucket = updatedBucket.update();
        Assert.assertEquals(expectedUpdatedBucket, actualUpdatedBucket);
    }

    @Test
    public void testDelete() throws Exception {
        initializeExpectedBucket(4);
        expect(storage.getOptions()).andReturn(mockOptions);
        expect(storage.delete(BucketTest.BUCKET_INFO.getName())).andReturn(true);
        replay(storage);
        initializeBucket();
        Assert.assertTrue(bucket.delete());
    }

    @Test
    public void testList() throws Exception {
        initializeExpectedBucket(4);
        PageImpl<Blob> expectedBlobPage = new PageImpl(null, "c", blobResults);
        expect(storage.getOptions()).andReturn(mockOptions);
        expect(storage.list(BucketTest.BUCKET_INFO.getName())).andReturn(expectedBlobPage);
        replay(storage);
        initializeBucket();
        Page<Blob> blobPage = bucket.list();
        Iterator<Blob> blobInfoIterator = blobPage.getValues().iterator();
        Iterator<Blob> blobIterator = blobPage.getValues().iterator();
        while ((blobInfoIterator.hasNext()) && (blobIterator.hasNext())) {
            Assert.assertEquals(blobInfoIterator.next(), blobIterator.next());
        } 
        Assert.assertFalse(blobInfoIterator.hasNext());
        Assert.assertFalse(blobIterator.hasNext());
        Assert.assertEquals(expectedBlobPage.getNextPageToken(), blobPage.getNextPageToken());
    }

    @Test
    public void testGet() throws Exception {
        initializeExpectedBucket(5);
        Blob expectedBlob = new Blob(serviceMockReturnsOptions, new BlobInfo.BuilderImpl(BlobInfo.newBuilder("b", "n").build()));
        expect(storage.getOptions()).andReturn(mockOptions);
        expect(storage.get(BlobId.of(expectedBucket.getName(), "n"), new Storage.BlobGetOption[0])).andReturn(expectedBlob);
        replay(storage);
        initializeBucket();
        Blob blob = bucket.get("n");
        Assert.assertEquals(expectedBlob, blob);
    }

    @Test
    public void testGetAllArray() throws Exception {
        initializeExpectedBucket(4);
        expect(storage.getOptions()).andReturn(mockOptions);
        List<BlobId> blobIds = Lists.transform(blobResults, new Function<Blob, BlobId>() {
            @Override
            public BlobId apply(Blob blob) {
                return blob.getBlobId();
            }
        });
        expect(storage.get(blobIds)).andReturn(blobResults);
        replay(storage);
        initializeBucket();
        Assert.assertEquals(blobResults, bucket.get("n1", "n2", "n3"));
    }

    @Test
    public void testGetAllIterable() throws Exception {
        initializeExpectedBucket(4);
        expect(storage.getOptions()).andReturn(mockOptions);
        List<BlobId> blobIds = Lists.transform(blobResults, new Function<Blob, BlobId>() {
            @Override
            public BlobId apply(Blob blob) {
                return blob.getBlobId();
            }
        });
        expect(storage.get(blobIds)).andReturn(blobResults);
        replay(storage);
        initializeBucket();
        Assert.assertEquals(blobResults, bucket.get(ImmutableList.of("n1", "n2", "n3")));
    }

    @Test
    public void testCreate() throws Exception {
        initializeExpectedBucket(5);
        BlobInfo info = BlobInfo.newBuilder("b", "n").setContentType(BucketTest.CONTENT_TYPE).build();
        Blob expectedBlob = new Blob(serviceMockReturnsOptions, new BlobInfo.BuilderImpl(info));
        byte[] content = new byte[]{ 13, 14, 10, 13 };
        expect(storage.getOptions()).andReturn(mockOptions);
        expect(storage.create(info, content)).andReturn(expectedBlob);
        replay(storage);
        initializeBucket();
        Blob blob = bucket.create("n", content, BucketTest.CONTENT_TYPE);
        Assert.assertEquals(expectedBlob, blob);
    }

    @Test
    public void testCreateNoContentType() throws Exception {
        initializeExpectedBucket(5);
        BlobInfo info = BlobInfo.newBuilder("b", "n").build();
        Blob expectedBlob = new Blob(serviceMockReturnsOptions, new BlobInfo.BuilderImpl(info));
        byte[] content = new byte[]{ 13, 14, 10, 13 };
        expect(storage.getOptions()).andReturn(mockOptions);
        expect(storage.create(info, content)).andReturn(expectedBlob);
        replay(storage);
        initializeBucket();
        Blob blob = bucket.create("n", content);
        Assert.assertEquals(expectedBlob, blob);
    }

    @Test
    public void testCreateWithOptions() throws Exception {
        initializeExpectedBucket(5);
        BlobInfo info = BlobInfo.newBuilder(BlobId.of("b", "n", 42L)).setContentType(BucketTest.CONTENT_TYPE).setMetageneration(24L).build();
        Blob expectedBlob = new Blob(serviceMockReturnsOptions, new BlobInfo.BuilderImpl(info));
        byte[] content = new byte[]{ 13, 14, 10, 13 };
        Storage.PredefinedAcl acl = PredefinedAcl.ALL_AUTHENTICATED_USERS;
        expect(storage.getOptions()).andReturn(mockOptions);
        expect(storage.create(info, content, BlobTargetOption.generationMatch(), BlobTargetOption.metagenerationMatch(), BlobTargetOption.predefinedAcl(acl), BlobTargetOption.encryptionKey(BucketTest.BASE64_KEY), BlobTargetOption.userProject(BucketTest.USER_PROJECT))).andReturn(expectedBlob);
        replay(storage);
        initializeBucket();
        Blob blob = bucket.create("n", content, BucketTest.CONTENT_TYPE, Bucket.BlobTargetOption.generationMatch(42L), Bucket.BlobTargetOption.metagenerationMatch(24L), Bucket.BlobTargetOption.predefinedAcl(acl), Bucket.BlobTargetOption.encryptionKey(BucketTest.BASE64_KEY), Bucket.BlobTargetOption.userProject(BucketTest.USER_PROJECT));
        Assert.assertEquals(expectedBlob, blob);
    }

    @Test
    public void testCreateWithEncryptionKey() throws Exception {
        initializeExpectedBucket(5);
        BlobInfo info = BlobInfo.newBuilder(BlobId.of("b", "n")).setContentType(BucketTest.CONTENT_TYPE).build();
        Blob expectedBlob = new Blob(serviceMockReturnsOptions, new BlobInfo.BuilderImpl(info));
        byte[] content = new byte[]{ 13, 14, 10, 13 };
        expect(storage.getOptions()).andReturn(mockOptions);
        expect(storage.create(info, content, BlobTargetOption.encryptionKey(BucketTest.KEY))).andReturn(expectedBlob);
        replay(storage);
        initializeBucket();
        Blob blob = bucket.create("n", content, BucketTest.CONTENT_TYPE, Bucket.BlobTargetOption.encryptionKey(BucketTest.KEY));
        Assert.assertEquals(expectedBlob, blob);
    }

    @Test
    public void testCreateWithKmsKeyName() throws Exception {
        initializeExpectedBucket(5);
        BlobInfo info = BlobInfo.newBuilder(BlobId.of("b", "n")).setContentType(BucketTest.CONTENT_TYPE).build();
        Blob expectedBlob = new Blob(serviceMockReturnsOptions, new BlobInfo.BuilderImpl(info));
        byte[] content = new byte[]{ 13, 14, 10, 13 };
        expect(storage.getOptions()).andReturn(mockOptions);
        expect(storage.create(info, content, BlobTargetOption.kmsKeyName(BucketTest.DEFAULT_KMS_KEY_NAME))).andReturn(expectedBlob);
        replay(storage);
        initializeBucket();
        Blob blob = bucket.create("n", content, BucketTest.CONTENT_TYPE, Bucket.BlobTargetOption.kmsKeyName(BucketTest.DEFAULT_KMS_KEY_NAME));
        Assert.assertEquals(expectedBlob, blob);
    }

    @Test
    public void testCreateNotExists() throws Exception {
        initializeExpectedBucket(5);
        BlobInfo info = BlobInfo.newBuilder(BlobId.of("b", "n", 0L)).setContentType(BucketTest.CONTENT_TYPE).build();
        Blob expectedBlob = new Blob(serviceMockReturnsOptions, new BlobInfo.BuilderImpl(info));
        byte[] content = new byte[]{ 13, 14, 10, 13 };
        expect(storage.getOptions()).andReturn(mockOptions);
        expect(storage.create(info, content, BlobTargetOption.generationMatch())).andReturn(expectedBlob);
        replay(storage);
        initializeBucket();
        Blob blob = bucket.create("n", content, BucketTest.CONTENT_TYPE, Bucket.BlobTargetOption.doesNotExist());
        Assert.assertEquals(expectedBlob, blob);
    }

    @Test
    public void testCreateWithWrongGenerationOptions() throws Exception {
        initializeExpectedBucket(4);
        expect(storage.getOptions()).andReturn(mockOptions);
        replay(storage);
        initializeBucket();
        byte[] content = new byte[]{ 13, 14, 10, 13 };
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Only one option of generationMatch, doesNotExist or generationNotMatch can be provided");
        bucket.create("n", content, BucketTest.CONTENT_TYPE, Bucket.BlobTargetOption.generationMatch(42L), Bucket.BlobTargetOption.generationNotMatch(24L));
    }

    @Test
    public void testCreateWithWrongMetagenerationOptions() throws Exception {
        initializeExpectedBucket(4);
        expect(storage.getOptions()).andReturn(mockOptions);
        replay(storage);
        initializeBucket();
        byte[] content = new byte[]{ 13, 14, 10, 13 };
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("metagenerationMatch and metagenerationNotMatch options can not be both provided");
        bucket.create("n", content, BucketTest.CONTENT_TYPE, Bucket.BlobTargetOption.metagenerationMatch(42L), Bucket.BlobTargetOption.metagenerationNotMatch(24L));
    }

    @Test
    public void testCreateFromStream() throws Exception {
        initializeExpectedBucket(5);
        BlobInfo info = BlobInfo.newBuilder("b", "n").setContentType(BucketTest.CONTENT_TYPE).build();
        Blob expectedBlob = new Blob(serviceMockReturnsOptions, new BlobInfo.BuilderImpl(info));
        byte[] content = new byte[]{ 13, 14, 10, 13 };
        InputStream streamContent = new ByteArrayInputStream(content);
        expect(storage.getOptions()).andReturn(mockOptions);
        expect(storage.create(info, streamContent)).andReturn(expectedBlob);
        replay(storage);
        initializeBucket();
        Blob blob = bucket.create("n", streamContent, BucketTest.CONTENT_TYPE);
        Assert.assertEquals(expectedBlob, blob);
    }

    @Test
    public void testCreateFromStreamNoContentType() throws Exception {
        initializeExpectedBucket(5);
        BlobInfo info = BlobInfo.newBuilder("b", "n").build();
        Blob expectedBlob = new Blob(serviceMockReturnsOptions, new BlobInfo.BuilderImpl(info));
        byte[] content = new byte[]{ 13, 14, 10, 13 };
        InputStream streamContent = new ByteArrayInputStream(content);
        expect(storage.getOptions()).andReturn(mockOptions);
        expect(storage.create(info, streamContent)).andReturn(expectedBlob);
        replay(storage);
        initializeBucket();
        Blob blob = bucket.create("n", streamContent);
        Assert.assertEquals(expectedBlob, blob);
    }

    @Test
    public void testCreateFromStreamWithOptions() throws Exception {
        initializeExpectedBucket(5);
        BlobInfo info = BlobInfo.newBuilder(BlobId.of("b", "n", 42L)).setContentType(BucketTest.CONTENT_TYPE).setMetageneration(24L).setCrc32c("crc").setMd5("md5").build();
        Blob expectedBlob = new Blob(serviceMockReturnsOptions, new BlobInfo.BuilderImpl(info));
        byte[] content = new byte[]{ 13, 14, 10, 13 };
        Storage.PredefinedAcl acl = PredefinedAcl.ALL_AUTHENTICATED_USERS;
        InputStream streamContent = new ByteArrayInputStream(content);
        expect(storage.getOptions()).andReturn(mockOptions);
        expect(storage.create(info, streamContent, BlobWriteOption.generationMatch(), BlobWriteOption.metagenerationMatch(), BlobWriteOption.predefinedAcl(acl), BlobWriteOption.crc32cMatch(), BlobWriteOption.md5Match(), BlobWriteOption.encryptionKey(BucketTest.BASE64_KEY), BlobWriteOption.userProject(BucketTest.USER_PROJECT))).andReturn(expectedBlob);
        replay(storage);
        initializeBucket();
        Blob blob = bucket.create("n", streamContent, BucketTest.CONTENT_TYPE, Bucket.BlobWriteOption.generationMatch(42L), Bucket.BlobWriteOption.metagenerationMatch(24L), Bucket.BlobWriteOption.predefinedAcl(acl), Bucket.BlobWriteOption.crc32cMatch("crc"), Bucket.BlobWriteOption.md5Match("md5"), Bucket.BlobWriteOption.encryptionKey(BucketTest.BASE64_KEY), Bucket.BlobWriteOption.userProject(BucketTest.USER_PROJECT));
        Assert.assertEquals(expectedBlob, blob);
    }

    @Test
    public void testCreateFromStreamWithEncryptionKey() throws Exception {
        initializeExpectedBucket(5);
        BlobInfo info = BlobInfo.newBuilder(BlobId.of("b", "n")).setContentType(BucketTest.CONTENT_TYPE).build();
        Blob expectedBlob = new Blob(serviceMockReturnsOptions, new BlobInfo.BuilderImpl(info));
        byte[] content = new byte[]{ 13, 14, 10, 13 };
        InputStream streamContent = new ByteArrayInputStream(content);
        expect(storage.getOptions()).andReturn(mockOptions);
        expect(storage.create(info, streamContent, BlobWriteOption.encryptionKey(BucketTest.KEY))).andReturn(expectedBlob);
        replay(storage);
        initializeBucket();
        Blob blob = bucket.create("n", streamContent, BucketTest.CONTENT_TYPE, Bucket.BlobWriteOption.encryptionKey(BucketTest.KEY));
        Assert.assertEquals(expectedBlob, blob);
    }

    @Test
    public void testCreateFromStreamNotExists() throws Exception {
        initializeExpectedBucket(5);
        BlobInfo info = BlobInfo.newBuilder(BlobId.of("b", "n", 0L)).setContentType(BucketTest.CONTENT_TYPE).build();
        Blob expectedBlob = new Blob(serviceMockReturnsOptions, new BlobInfo.BuilderImpl(info));
        byte[] content = new byte[]{ 13, 14, 10, 13 };
        InputStream streamContent = new ByteArrayInputStream(content);
        expect(storage.getOptions()).andReturn(mockOptions);
        expect(storage.create(info, streamContent, BlobWriteOption.generationMatch())).andReturn(expectedBlob);
        replay(storage);
        initializeBucket();
        Blob blob = bucket.create("n", streamContent, BucketTest.CONTENT_TYPE, Bucket.BlobWriteOption.doesNotExist());
        Assert.assertEquals(expectedBlob, blob);
    }

    @Test
    public void testCreateFromStreamWithWrongGenerationOptions() throws Exception {
        initializeExpectedBucket(4);
        expect(storage.getOptions()).andReturn(mockOptions);
        replay(storage);
        initializeBucket();
        byte[] content = new byte[]{ 13, 14, 10, 13 };
        InputStream streamContent = new ByteArrayInputStream(content);
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Only one option of generationMatch, doesNotExist or generationNotMatch can be provided");
        bucket.create("n", streamContent, BucketTest.CONTENT_TYPE, Bucket.BlobWriteOption.generationMatch(42L), Bucket.BlobWriteOption.generationNotMatch(24L));
    }

    @Test
    public void testCreateFromStreamWithWrongMetagenerationOptions() throws Exception {
        initializeExpectedBucket(4);
        expect(storage.getOptions()).andReturn(mockOptions);
        replay(storage);
        initializeBucket();
        byte[] content = new byte[]{ 13, 14, 10, 13 };
        InputStream streamContent = new ByteArrayInputStream(content);
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("metagenerationMatch and metagenerationNotMatch options can not be both provided");
        bucket.create("n", streamContent, BucketTest.CONTENT_TYPE, Bucket.BlobWriteOption.metagenerationMatch(42L), Bucket.BlobWriteOption.metagenerationNotMatch(24L));
    }

    @Test
    public void testGetAcl() throws Exception {
        initializeExpectedBucket(4);
        expect(storage.getOptions()).andReturn(mockOptions);
        expect(storage.getAcl(BucketTest.BUCKET_INFO.getName(), User.ofAllAuthenticatedUsers())).andReturn(BucketTest.ACL);
        replay(storage);
        initializeBucket();
        Assert.assertEquals(BucketTest.ACL, bucket.getAcl(User.ofAllAuthenticatedUsers()));
    }

    @Test
    public void testDeleteAcl() throws Exception {
        initializeExpectedBucket(4);
        expect(storage.getOptions()).andReturn(mockOptions);
        expect(storage.deleteAcl(BucketTest.BUCKET_INFO.getName(), User.ofAllAuthenticatedUsers())).andReturn(true);
        replay(storage);
        initializeBucket();
        Assert.assertTrue(bucket.deleteAcl(User.ofAllAuthenticatedUsers()));
    }

    @Test
    public void testCreateAcl() throws Exception {
        initializeExpectedBucket(4);
        expect(storage.getOptions()).andReturn(mockOptions);
        Acl returnedAcl = BucketTest.ACL.toBuilder().setEtag("ETAG").setId("ID").build();
        expect(storage.createAcl(BucketTest.BUCKET_INFO.getName(), BucketTest.ACL)).andReturn(returnedAcl);
        replay(storage);
        initializeBucket();
        Assert.assertEquals(returnedAcl, bucket.createAcl(BucketTest.ACL));
    }

    @Test
    public void testUpdateAcl() throws Exception {
        initializeExpectedBucket(4);
        expect(storage.getOptions()).andReturn(mockOptions);
        Acl returnedAcl = BucketTest.ACL.toBuilder().setEtag("ETAG").setId("ID").build();
        expect(storage.updateAcl(BucketTest.BUCKET_INFO.getName(), BucketTest.ACL)).andReturn(returnedAcl);
        replay(storage);
        initializeBucket();
        Assert.assertEquals(returnedAcl, bucket.updateAcl(BucketTest.ACL));
    }

    @Test
    public void testListAcls() throws Exception {
        initializeExpectedBucket(4);
        expect(storage.getOptions()).andReturn(mockOptions);
        expect(storage.listAcls(BucketTest.BUCKET_INFO.getName())).andReturn(BucketTest.ACLS);
        replay(storage);
        initializeBucket();
        Assert.assertEquals(BucketTest.ACLS, bucket.listAcls());
    }

    @Test
    public void testGetDefaultAcl() throws Exception {
        initializeExpectedBucket(4);
        expect(storage.getOptions()).andReturn(mockOptions);
        expect(storage.getDefaultAcl(BucketTest.BUCKET_INFO.getName(), User.ofAllAuthenticatedUsers())).andReturn(BucketTest.ACL);
        replay(storage);
        initializeBucket();
        Assert.assertEquals(BucketTest.ACL, bucket.getDefaultAcl(User.ofAllAuthenticatedUsers()));
    }

    @Test
    public void testDeleteDefaultAcl() throws Exception {
        initializeExpectedBucket(4);
        expect(storage.getOptions()).andReturn(mockOptions);
        expect(storage.deleteDefaultAcl(BucketTest.BUCKET_INFO.getName(), User.ofAllAuthenticatedUsers())).andReturn(true);
        replay(storage);
        initializeBucket();
        Assert.assertTrue(bucket.deleteDefaultAcl(User.ofAllAuthenticatedUsers()));
    }

    @Test
    public void testCreateDefaultAcl() throws Exception {
        initializeExpectedBucket(4);
        expect(storage.getOptions()).andReturn(mockOptions);
        Acl returnedAcl = BucketTest.ACL.toBuilder().setEtag("ETAG").setId("ID").build();
        expect(storage.createDefaultAcl(BucketTest.BUCKET_INFO.getName(), BucketTest.ACL)).andReturn(returnedAcl);
        replay(storage);
        initializeBucket();
        Assert.assertEquals(returnedAcl, bucket.createDefaultAcl(BucketTest.ACL));
    }

    @Test
    public void testUpdateDefaultAcl() throws Exception {
        initializeExpectedBucket(4);
        expect(storage.getOptions()).andReturn(mockOptions);
        Acl returnedAcl = BucketTest.ACL.toBuilder().setEtag("ETAG").setId("ID").build();
        expect(storage.updateDefaultAcl(BucketTest.BUCKET_INFO.getName(), BucketTest.ACL)).andReturn(returnedAcl);
        replay(storage);
        initializeBucket();
        Assert.assertEquals(returnedAcl, bucket.updateDefaultAcl(BucketTest.ACL));
    }

    @Test
    public void testListDefaultAcls() throws Exception {
        initializeExpectedBucket(4);
        expect(storage.getOptions()).andReturn(mockOptions);
        expect(storage.listDefaultAcls(BucketTest.BUCKET_INFO.getName())).andReturn(BucketTest.ACLS);
        replay(storage);
        initializeBucket();
        Assert.assertEquals(BucketTest.ACLS, bucket.listDefaultAcls());
    }

    @Test
    public void testLockRetention() throws Exception {
        initializeExpectedBucket(5);
        Bucket expectedRetentionLockedBucket = expectedBucket.toBuilder().setRetentionPeriod(BucketTest.RETENTION_PERIOD).setRetentionPolicyIsLocked(true).build();
        expect(storage.getOptions()).andReturn(mockOptions).times(2);
        expect(storage.lockRetentionPolicy(expectedRetentionLockedBucket, BucketTargetOption.metagenerationMatch(), BucketTargetOption.userProject(BucketTest.USER_PROJECT))).andReturn(expectedRetentionLockedBucket);
        replay(storage);
        initializeBucket();
        Bucket lockedRetentionPolicyBucket = new Bucket(storage, new BucketInfo.BuilderImpl(expectedRetentionLockedBucket));
        Bucket actualRetentionLockedBucket = lockedRetentionPolicyBucket.lockRetentionPolicy(BucketTargetOption.metagenerationMatch(), BucketTargetOption.userProject(BucketTest.USER_PROJECT));
        Assert.assertEquals(expectedRetentionLockedBucket, actualRetentionLockedBucket);
    }

    @Test
    public void testToBuilder() {
        expect(storage.getOptions()).andReturn(mockOptions).times(4);
        replay(storage);
        Bucket fullBucket = new Bucket(storage, new BucketInfo.BuilderImpl(BucketTest.FULL_BUCKET_INFO));
        Assert.assertEquals(fullBucket, fullBucket.toBuilder().build());
        Bucket simpleBlob = new Bucket(storage, new BucketInfo.BuilderImpl(BucketTest.BUCKET_INFO));
        Assert.assertEquals(simpleBlob, simpleBlob.toBuilder().build());
    }

    @Test
    public void testBuilder() {
        initializeExpectedBucket(4);
        expect(storage.getOptions()).andReturn(mockOptions).times(4);
        replay(storage);
        Bucket.Builder builder = new Bucket.Builder(new Bucket(storage, new BucketInfo.BuilderImpl(BucketTest.BUCKET_INFO)));
        Bucket bucket = builder.setAcl(BucketTest.ACLS).setEtag(BucketTest.ETAG).setGeneratedId(BucketTest.GENERATED_ID).setMetageneration(BucketTest.META_GENERATION).setOwner(BucketTest.OWNER).setSelfLink(BucketTest.SELF_LINK).setCors(BucketTest.CORS).setCreateTime(BucketTest.CREATE_TIME).setDefaultAcl(BucketTest.DEFAULT_ACL).setDeleteRules(BucketTest.DELETE_RULES).setLifecycleRules(BucketTest.LIFECYCLE_RULES).setIndexPage(BucketTest.INDEX_PAGE).setNotFoundPage(BucketTest.NOT_FOUND_PAGE).setLocation(BucketTest.LOCATION).setStorageClass(BucketTest.STORAGE_CLASS).setVersioningEnabled(BucketTest.VERSIONING_ENABLED).setLabels(BucketTest.BUCKET_LABELS).setRequesterPays(BucketTest.REQUESTER_PAYS).setDefaultKmsKeyName(BucketTest.DEFAULT_KMS_KEY_NAME).setDefaultEventBasedHold(BucketTest.DEFAULT_EVENT_BASED_HOLD).setRetentionEffectiveTime(BucketTest.RETENTION_EFFECTIVE_TIME).setRetentionPeriod(BucketTest.RETENTION_PERIOD).setRetentionPolicyIsLocked(BucketTest.RETENTION_POLICY_IS_LOCKED).build();
        Assert.assertEquals("b", bucket.getName());
        Assert.assertEquals(BucketTest.ACLS, bucket.getAcl());
        Assert.assertEquals(BucketTest.ETAG, bucket.getEtag());
        Assert.assertEquals(BucketTest.GENERATED_ID, bucket.getGeneratedId());
        Assert.assertEquals(BucketTest.META_GENERATION, bucket.getMetageneration());
        Assert.assertEquals(BucketTest.OWNER, bucket.getOwner());
        Assert.assertEquals(BucketTest.SELF_LINK, bucket.getSelfLink());
        Assert.assertEquals(BucketTest.CREATE_TIME, bucket.getCreateTime());
        Assert.assertEquals(BucketTest.CORS, bucket.getCors());
        Assert.assertEquals(BucketTest.DEFAULT_ACL, bucket.getDefaultAcl());
        Assert.assertEquals(BucketTest.DELETE_RULES, bucket.getDeleteRules());
        Assert.assertEquals(BucketTest.LIFECYCLE_RULES, bucket.getLifecycleRules());
        Assert.assertEquals(BucketTest.INDEX_PAGE, bucket.getIndexPage());
        Assert.assertEquals(BucketTest.NOT_FOUND_PAGE, bucket.getNotFoundPage());
        Assert.assertEquals(BucketTest.LOCATION, bucket.getLocation());
        Assert.assertEquals(BucketTest.STORAGE_CLASS, bucket.getStorageClass());
        Assert.assertEquals(BucketTest.VERSIONING_ENABLED, bucket.versioningEnabled());
        Assert.assertEquals(BucketTest.BUCKET_LABELS, bucket.getLabels());
        Assert.assertEquals(BucketTest.REQUESTER_PAYS, bucket.requesterPays());
        Assert.assertEquals(BucketTest.DEFAULT_KMS_KEY_NAME, bucket.getDefaultKmsKeyName());
        Assert.assertEquals(BucketTest.DEFAULT_EVENT_BASED_HOLD, bucket.getDefaultEventBasedHold());
        Assert.assertEquals(BucketTest.RETENTION_EFFECTIVE_TIME, bucket.getRetentionEffectiveTime());
        Assert.assertEquals(BucketTest.RETENTION_PERIOD, bucket.getRetentionPeriod());
        Assert.assertEquals(BucketTest.RETENTION_POLICY_IS_LOCKED, bucket.retentionPolicyIsLocked());
        Assert.assertEquals(storage.getOptions(), bucket.getStorage().getOptions());
    }
}

