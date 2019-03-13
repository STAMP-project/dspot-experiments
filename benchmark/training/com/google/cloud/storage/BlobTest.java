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


import Blob.Builder;
import BlobInfo.CustomerEncryption;
import Role.OWNER;
import Role.READER;
import Storage.BlobGetOption;
import Storage.BlobSourceOption;
import com.google.cloud.ReadChannel;
import com.google.cloud.storage.Acl.Project.ProjectRole;
import com.google.cloud.storage.Acl.User;
import com.google.cloud.storage.Storage.BlobWriteOption;
import com.google.cloud.storage.Storage.CopyRequest;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.BaseEncoding;
import java.io.File;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.security.Key;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.crypto.spec.SecretKeySpec;
import org.easymock.Capture;
import org.junit.Assert;
import org.junit.Test;


public class BlobTest {
    private static final Acl ACL = Acl.of(User.ofAllAuthenticatedUsers(), Role.OWNER);

    private static final Acl OTHER_ACL = Acl.of(new com.google.cloud.storage.Acl.Project(ProjectRole.OWNERS, "p"), READER);

    private static final List<Acl> ACLS = ImmutableList.of(BlobTest.ACL, BlobTest.OTHER_ACL);

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

    private static final Long UPDATE_TIME = (BlobTest.DELETE_TIME) - 1L;

    private static final Long CREATE_TIME = (BlobTest.UPDATE_TIME) - 1L;

    private static final String ENCRYPTION_ALGORITHM = "AES256";

    private static final String KEY_SHA256 = "keySha";

    private static final CustomerEncryption CUSTOMER_ENCRYPTION = new BlobInfo.CustomerEncryption(BlobTest.ENCRYPTION_ALGORITHM, BlobTest.KEY_SHA256);

    private static final String KMS_KEY_NAME = "projects/p/locations/kr-loc/keyRings/kr/cryptoKeys/key";

    private static final Boolean EVENT_BASED_HOLD = true;

    private static final Boolean TEMPORARY_HOLD = true;

    private static final Long RETENTION_EXPIRATION_TIME = 10L;

    private static final BlobInfo FULL_BLOB_INFO = BlobInfo.newBuilder("b", "n", BlobTest.GENERATION).setAcl(BlobTest.ACLS).setComponentCount(BlobTest.COMPONENT_COUNT).setContentType(BlobTest.CONTENT_TYPE).setCacheControl(BlobTest.CACHE_CONTROL).setContentDisposition(BlobTest.CONTENT_DISPOSITION).setContentEncoding(BlobTest.CONTENT_ENCODING).setContentLanguage(BlobTest.CONTENT_LANGUAGE).setCrc32c(BlobTest.CRC32).setDeleteTime(BlobTest.DELETE_TIME).setEtag(BlobTest.ETAG).setGeneratedId(BlobTest.GENERATED_ID).setMd5(BlobTest.MD5).setMediaLink(BlobTest.MEDIA_LINK).setMetadata(BlobTest.METADATA).setMetageneration(BlobTest.META_GENERATION).setOwner(BlobTest.OWNER).setSelfLink(BlobTest.SELF_LINK).setSize(BlobTest.SIZE).setUpdateTime(BlobTest.UPDATE_TIME).setCreateTime(BlobTest.CREATE_TIME).setCustomerEncryption(BlobTest.CUSTOMER_ENCRYPTION).setKmsKeyName(BlobTest.KMS_KEY_NAME).setEventBasedHold(BlobTest.EVENT_BASED_HOLD).setTemporaryHold(BlobTest.TEMPORARY_HOLD).setRetentionExpirationTime(BlobTest.RETENTION_EXPIRATION_TIME).build();

    private static final BlobInfo BLOB_INFO = BlobInfo.newBuilder("b", "n").setMetageneration(42L).build();

    private static final BlobInfo DIRECTORY_INFO = BlobInfo.newBuilder("b", "n/").setSize(0L).setIsDirectory(true).build();

    private static final String BASE64_KEY = "JVzfVl8NLD9FjedFuStegjRfES5ll5zc59CIXw572OA=";

    private static final Key KEY = new SecretKeySpec(BaseEncoding.base64().decode(BlobTest.BASE64_KEY), "AES256");

    private Storage storage;

    private Blob blob;

    private Blob expectedBlob;

    private Storage serviceMockReturnsOptions = createMock(Storage.class);

    private StorageOptions mockOptions = createMock(StorageOptions.class);

    @Test
    public void testExists_True() throws Exception {
        initializeExpectedBlob(1);
        Storage[] expectedOptions = new BlobGetOption[]{ BlobGetOption.fields() };
        expect(storage.getOptions()).andReturn(mockOptions);
        expect(storage.get(expectedBlob.getBlobId(), expectedOptions)).andReturn(expectedBlob);
        replay(storage);
        initializeBlob();
        Assert.assertTrue(blob.exists());
    }

    @Test
    public void testExists_False() throws Exception {
        Storage[] expectedOptions = new BlobGetOption[]{ BlobGetOption.fields() };
        expect(storage.getOptions()).andReturn(null);
        expect(storage.get(BlobTest.BLOB_INFO.getBlobId(), expectedOptions)).andReturn(null);
        replay(storage);
        initializeBlob();
        Assert.assertFalse(blob.exists());
    }

    @Test
    public void testContent() throws Exception {
        initializeExpectedBlob(2);
        byte[] content = new byte[]{ 1, 2 };
        expect(storage.getOptions()).andReturn(mockOptions);
        expect(storage.readAllBytes(BlobTest.BLOB_INFO.getBlobId())).andReturn(content);
        replay(storage);
        initializeBlob();
        Assert.assertArrayEquals(content, blob.getContent());
    }

    @Test
    public void testContentWithDecryptionKey() throws Exception {
        initializeExpectedBlob(2);
        byte[] content = new byte[]{ 1, 2 };
        expect(storage.getOptions()).andReturn(mockOptions);
        expect(storage.readAllBytes(BlobTest.BLOB_INFO.getBlobId(), BlobSourceOption.decryptionKey(BlobTest.BASE64_KEY))).andReturn(content).times(2);
        replay(storage);
        initializeBlob();
        Assert.assertArrayEquals(content, blob.getContent(com.google.cloud.storage.Blob.BlobSourceOption.decryptionKey(BlobTest.BASE64_KEY)));
        Assert.assertArrayEquals(content, blob.getContent(com.google.cloud.storage.Blob.BlobSourceOption.decryptionKey(BlobTest.KEY)));
    }

    @Test
    public void testReload() throws Exception {
        initializeExpectedBlob(2);
        Blob expectedReloadedBlob = expectedBlob.toBuilder().setCacheControl("c").build();
        expect(storage.getOptions()).andReturn(mockOptions);
        expect(storage.get(BlobTest.BLOB_INFO.getBlobId(), new Storage.BlobGetOption[0])).andReturn(expectedReloadedBlob);
        replay(storage);
        initializeBlob();
        Blob updatedBlob = blob.reload();
        Assert.assertEquals(expectedReloadedBlob, updatedBlob);
    }

    @Test
    public void testReloadNull() throws Exception {
        initializeExpectedBlob(1);
        expect(storage.getOptions()).andReturn(mockOptions);
        expect(storage.get(BlobTest.BLOB_INFO.getBlobId(), new Storage.BlobGetOption[0])).andReturn(null);
        replay(storage);
        initializeBlob();
        Blob reloadedBlob = blob.reload();
        Assert.assertNull(reloadedBlob);
    }

    @Test
    public void testReloadWithOptions() throws Exception {
        initializeExpectedBlob(2);
        Blob expectedReloadedBlob = expectedBlob.toBuilder().setCacheControl("c").build();
        Storage[] options = new BlobGetOption[]{ BlobGetOption.metagenerationMatch(42L) };
        expect(storage.getOptions()).andReturn(mockOptions);
        expect(storage.get(BlobTest.BLOB_INFO.getBlobId(), options)).andReturn(expectedReloadedBlob);
        replay(storage);
        initializeBlob();
        Blob updatedBlob = blob.reload(com.google.cloud.storage.Blob.BlobSourceOption.metagenerationMatch());
        Assert.assertEquals(expectedReloadedBlob, updatedBlob);
    }

    @Test
    public void testUpdate() throws Exception {
        initializeExpectedBlob(2);
        Blob expectedUpdatedBlob = expectedBlob.toBuilder().setCacheControl("c").build();
        expect(storage.getOptions()).andReturn(mockOptions).times(2);
        expect(storage.update(eq(expectedUpdatedBlob), new Storage.BlobTargetOption[0])).andReturn(expectedUpdatedBlob);
        replay(storage);
        initializeBlob();
        Blob updatedBlob = new Blob(storage, new BlobInfo.BuilderImpl(expectedUpdatedBlob));
        Blob actualUpdatedBlob = updatedBlob.update();
        Assert.assertEquals(expectedUpdatedBlob, actualUpdatedBlob);
    }

    @Test
    public void testDelete() throws Exception {
        initializeExpectedBlob(2);
        expect(storage.getOptions()).andReturn(mockOptions);
        expect(storage.delete(BlobTest.BLOB_INFO.getBlobId(), new Storage.BlobSourceOption[0])).andReturn(true);
        replay(storage);
        initializeBlob();
        Assert.assertTrue(blob.delete());
    }

    @Test
    public void testCopyToBucket() throws Exception {
        initializeExpectedBlob(2);
        BlobInfo target = BlobInfo.newBuilder(BlobId.of("bt", "n")).build();
        CopyWriter copyWriter = createMock(CopyWriter.class);
        Capture<CopyRequest> capturedCopyRequest = Capture.newInstance();
        expect(storage.getOptions()).andReturn(mockOptions);
        expect(storage.copy(capture(capturedCopyRequest))).andReturn(copyWriter);
        replay(storage);
        initializeBlob();
        CopyWriter returnedCopyWriter = blob.copyTo("bt");
        Assert.assertEquals(copyWriter, returnedCopyWriter);
        Assert.assertEquals(capturedCopyRequest.getValue().getSource(), blob.getBlobId());
        Assert.assertEquals(capturedCopyRequest.getValue().getTarget(), target);
        Assert.assertFalse(capturedCopyRequest.getValue().overrideInfo());
        Assert.assertTrue(capturedCopyRequest.getValue().getSourceOptions().isEmpty());
        Assert.assertTrue(capturedCopyRequest.getValue().getTargetOptions().isEmpty());
    }

    @Test
    public void testCopyTo() throws Exception {
        initializeExpectedBlob(2);
        BlobInfo target = BlobInfo.newBuilder(BlobId.of("bt", "nt")).build();
        CopyWriter copyWriter = createMock(CopyWriter.class);
        Capture<CopyRequest> capturedCopyRequest = Capture.newInstance();
        expect(storage.getOptions()).andReturn(mockOptions);
        expect(storage.copy(capture(capturedCopyRequest))).andReturn(copyWriter);
        replay(storage);
        initializeBlob();
        CopyWriter returnedCopyWriter = blob.copyTo("bt", "nt");
        Assert.assertEquals(copyWriter, returnedCopyWriter);
        Assert.assertEquals(capturedCopyRequest.getValue().getSource(), blob.getBlobId());
        Assert.assertEquals(capturedCopyRequest.getValue().getTarget(), target);
        Assert.assertFalse(capturedCopyRequest.getValue().overrideInfo());
        Assert.assertTrue(capturedCopyRequest.getValue().getSourceOptions().isEmpty());
        Assert.assertTrue(capturedCopyRequest.getValue().getTargetOptions().isEmpty());
    }

    @Test
    public void testCopyToBlobId() throws Exception {
        initializeExpectedBlob(2);
        BlobInfo target = BlobInfo.newBuilder(BlobId.of("bt", "nt")).build();
        BlobId targetId = BlobId.of("bt", "nt");
        CopyWriter copyWriter = createMock(CopyWriter.class);
        Capture<CopyRequest> capturedCopyRequest = Capture.newInstance();
        expect(storage.getOptions()).andReturn(mockOptions);
        expect(storage.copy(capture(capturedCopyRequest))).andReturn(copyWriter);
        replay(storage);
        initializeBlob();
        CopyWriter returnedCopyWriter = blob.copyTo(targetId);
        Assert.assertEquals(copyWriter, returnedCopyWriter);
        Assert.assertEquals(capturedCopyRequest.getValue().getSource(), blob.getBlobId());
        Assert.assertEquals(capturedCopyRequest.getValue().getTarget(), target);
        Assert.assertFalse(capturedCopyRequest.getValue().overrideInfo());
        Assert.assertTrue(capturedCopyRequest.getValue().getSourceOptions().isEmpty());
        Assert.assertTrue(capturedCopyRequest.getValue().getTargetOptions().isEmpty());
    }

    @Test
    public void testReader() throws Exception {
        initializeExpectedBlob(2);
        ReadChannel channel = createMock(ReadChannel.class);
        expect(storage.getOptions()).andReturn(mockOptions);
        expect(storage.reader(BlobTest.BLOB_INFO.getBlobId())).andReturn(channel);
        replay(storage);
        initializeBlob();
        Assert.assertSame(channel, blob.reader());
    }

    @Test
    public void testReaderWithDecryptionKey() throws Exception {
        initializeExpectedBlob(2);
        ReadChannel channel = createMock(ReadChannel.class);
        expect(storage.getOptions()).andReturn(mockOptions);
        expect(storage.reader(BlobTest.BLOB_INFO.getBlobId(), BlobSourceOption.decryptionKey(BlobTest.BASE64_KEY))).andReturn(channel).times(2);
        replay(storage);
        initializeBlob();
        Assert.assertSame(channel, blob.reader(com.google.cloud.storage.Blob.BlobSourceOption.decryptionKey(BlobTest.BASE64_KEY)));
        Assert.assertSame(channel, blob.reader(com.google.cloud.storage.Blob.BlobSourceOption.decryptionKey(BlobTest.KEY)));
    }

    @Test
    public void testWriter() throws Exception {
        initializeExpectedBlob(2);
        BlobWriteChannel channel = createMock(BlobWriteChannel.class);
        expect(storage.getOptions()).andReturn(mockOptions);
        expect(storage.writer(eq(expectedBlob))).andReturn(channel);
        replay(storage);
        initializeBlob();
        Assert.assertSame(channel, blob.writer());
    }

    @Test
    public void testWriterWithEncryptionKey() throws Exception {
        initializeExpectedBlob(2);
        BlobWriteChannel channel = createMock(BlobWriteChannel.class);
        expect(storage.getOptions()).andReturn(mockOptions);
        expect(storage.writer(eq(expectedBlob), eq(BlobWriteOption.encryptionKey(BlobTest.BASE64_KEY)))).andReturn(channel).times(2);
        replay(storage);
        initializeBlob();
        Assert.assertSame(channel, blob.writer(BlobWriteOption.encryptionKey(BlobTest.BASE64_KEY)));
        Assert.assertSame(channel, blob.writer(BlobWriteOption.encryptionKey(BlobTest.KEY)));
    }

    @Test
    public void testWriterWithKmsKeyName() throws Exception {
        initializeExpectedBlob(2);
        BlobWriteChannel channel = createMock(BlobWriteChannel.class);
        expect(storage.getOptions()).andReturn(mockOptions);
        expect(storage.writer(eq(expectedBlob), eq(BlobWriteOption.kmsKeyName(BlobTest.KMS_KEY_NAME)))).andReturn(channel);
        replay(storage);
        initializeBlob();
        Assert.assertSame(channel, blob.writer(BlobWriteOption.kmsKeyName(BlobTest.KMS_KEY_NAME)));
    }

    @Test
    public void testSignUrl() throws Exception {
        initializeExpectedBlob(2);
        URL url = new URL("http://localhost:123/bla");
        expect(storage.getOptions()).andReturn(mockOptions);
        expect(storage.signUrl(expectedBlob, 100, TimeUnit.SECONDS)).andReturn(url);
        replay(storage);
        initializeBlob();
        Assert.assertEquals(url, blob.signUrl(100, TimeUnit.SECONDS));
    }

    @Test
    public void testGetAcl() throws Exception {
        initializeExpectedBlob(1);
        expect(storage.getOptions()).andReturn(mockOptions);
        expect(storage.getAcl(BlobTest.BLOB_INFO.getBlobId(), User.ofAllAuthenticatedUsers())).andReturn(BlobTest.ACL);
        replay(storage);
        initializeBlob();
        Assert.assertEquals(BlobTest.ACL, blob.getAcl(User.ofAllAuthenticatedUsers()));
    }

    @Test
    public void testDeleteAcl() throws Exception {
        initializeExpectedBlob(1);
        expect(storage.getOptions()).andReturn(mockOptions);
        expect(storage.deleteAcl(BlobTest.BLOB_INFO.getBlobId(), User.ofAllAuthenticatedUsers())).andReturn(true);
        replay(storage);
        initializeBlob();
        Assert.assertTrue(blob.deleteAcl(User.ofAllAuthenticatedUsers()));
    }

    @Test
    public void testCreateAcl() throws Exception {
        initializeExpectedBlob(1);
        expect(storage.getOptions()).andReturn(mockOptions);
        Acl returnedAcl = BlobTest.ACL.toBuilder().setEtag("ETAG").setId("ID").build();
        expect(storage.createAcl(BlobTest.BLOB_INFO.getBlobId(), BlobTest.ACL)).andReturn(returnedAcl);
        replay(storage);
        initializeBlob();
        Assert.assertEquals(returnedAcl, blob.createAcl(BlobTest.ACL));
    }

    @Test
    public void testUpdateAcl() throws Exception {
        initializeExpectedBlob(1);
        expect(storage.getOptions()).andReturn(mockOptions);
        Acl returnedAcl = BlobTest.ACL.toBuilder().setEtag("ETAG").setId("ID").build();
        expect(storage.updateAcl(BlobTest.BLOB_INFO.getBlobId(), BlobTest.ACL)).andReturn(returnedAcl);
        replay(storage);
        initializeBlob();
        Assert.assertEquals(returnedAcl, blob.updateAcl(BlobTest.ACL));
    }

    @Test
    public void testListAcls() throws Exception {
        initializeExpectedBlob(1);
        expect(storage.getOptions()).andReturn(mockOptions);
        expect(storage.listAcls(BlobTest.BLOB_INFO.getBlobId())).andReturn(BlobTest.ACLS);
        replay(storage);
        initializeBlob();
        Assert.assertEquals(BlobTest.ACLS, blob.listAcls());
    }

    @Test
    public void testToBuilder() {
        expect(storage.getOptions()).andReturn(mockOptions).times(6);
        replay(storage);
        Blob fullBlob = new Blob(storage, new BlobInfo.BuilderImpl(BlobTest.FULL_BLOB_INFO));
        Assert.assertEquals(fullBlob, fullBlob.toBuilder().build());
        Blob simpleBlob = new Blob(storage, new BlobInfo.BuilderImpl(BlobTest.BLOB_INFO));
        Assert.assertEquals(simpleBlob, simpleBlob.toBuilder().build());
        Blob directory = new Blob(storage, new BlobInfo.BuilderImpl(BlobTest.DIRECTORY_INFO));
        Assert.assertEquals(directory, directory.toBuilder().build());
    }

    @Test
    public void testBuilder() {
        initializeExpectedBlob(4);
        expect(storage.getOptions()).andReturn(mockOptions).times(6);
        replay(storage);
        Blob.Builder builder = new Blob.Builder(new Blob(storage, new BlobInfo.BuilderImpl(BlobTest.BLOB_INFO)));
        Blob blob = builder.setAcl(BlobTest.ACLS).setComponentCount(BlobTest.COMPONENT_COUNT).setContentType(BlobTest.CONTENT_TYPE).setCacheControl(BlobTest.CACHE_CONTROL).setContentDisposition(BlobTest.CONTENT_DISPOSITION).setContentEncoding(BlobTest.CONTENT_ENCODING).setContentLanguage(BlobTest.CONTENT_LANGUAGE).setCrc32c(BlobTest.CRC32).setCreateTime(BlobTest.CREATE_TIME).setCustomerEncryption(BlobTest.CUSTOMER_ENCRYPTION).setKmsKeyName(BlobTest.KMS_KEY_NAME).setEventBasedHold(BlobTest.EVENT_BASED_HOLD).setTemporaryHold(BlobTest.TEMPORARY_HOLD).setRetentionExpirationTime(BlobTest.RETENTION_EXPIRATION_TIME).setDeleteTime(BlobTest.DELETE_TIME).setEtag(BlobTest.ETAG).setGeneratedId(BlobTest.GENERATED_ID).setMd5(BlobTest.MD5).setMediaLink(BlobTest.MEDIA_LINK).setMetadata(BlobTest.METADATA).setMetageneration(BlobTest.META_GENERATION).setOwner(BlobTest.OWNER).setSelfLink(BlobTest.SELF_LINK).setSize(BlobTest.SIZE).setUpdateTime(BlobTest.UPDATE_TIME).build();
        Assert.assertEquals("b", blob.getBucket());
        Assert.assertEquals("n", blob.getName());
        Assert.assertEquals(BlobTest.ACLS, blob.getAcl());
        Assert.assertEquals(BlobTest.COMPONENT_COUNT, blob.getComponentCount());
        Assert.assertEquals(BlobTest.CONTENT_TYPE, blob.getContentType());
        Assert.assertEquals(BlobTest.CACHE_CONTROL, blob.getCacheControl());
        Assert.assertEquals(BlobTest.CONTENT_DISPOSITION, blob.getContentDisposition());
        Assert.assertEquals(BlobTest.CONTENT_ENCODING, blob.getContentEncoding());
        Assert.assertEquals(BlobTest.CONTENT_LANGUAGE, blob.getContentLanguage());
        Assert.assertEquals(BlobTest.CRC32, blob.getCrc32c());
        Assert.assertEquals(BlobTest.CRC32_HEX_STRING, blob.getCrc32cToHexString());
        Assert.assertEquals(BlobTest.CREATE_TIME, blob.getCreateTime());
        Assert.assertEquals(BlobTest.CUSTOMER_ENCRYPTION, blob.getCustomerEncryption());
        Assert.assertEquals(BlobTest.KMS_KEY_NAME, blob.getKmsKeyName());
        Assert.assertEquals(BlobTest.EVENT_BASED_HOLD, blob.getEventBasedHold());
        Assert.assertEquals(BlobTest.TEMPORARY_HOLD, blob.getTemporaryHold());
        Assert.assertEquals(BlobTest.RETENTION_EXPIRATION_TIME, blob.getRetentionExpirationTime());
        Assert.assertEquals(BlobTest.DELETE_TIME, blob.getDeleteTime());
        Assert.assertEquals(BlobTest.ETAG, blob.getEtag());
        Assert.assertEquals(BlobTest.GENERATED_ID, blob.getGeneratedId());
        Assert.assertEquals(BlobTest.MD5, blob.getMd5());
        Assert.assertEquals(BlobTest.MD5_HEX_STRING, blob.getMd5ToHexString());
        Assert.assertEquals(BlobTest.MEDIA_LINK, blob.getMediaLink());
        Assert.assertEquals(BlobTest.METADATA, blob.getMetadata());
        Assert.assertEquals(BlobTest.META_GENERATION, blob.getMetageneration());
        Assert.assertEquals(BlobTest.OWNER, blob.getOwner());
        Assert.assertEquals(BlobTest.SELF_LINK, blob.getSelfLink());
        Assert.assertEquals(BlobTest.SIZE, blob.getSize());
        Assert.assertEquals(BlobTest.UPDATE_TIME, blob.getUpdateTime());
        Assert.assertEquals(storage.getOptions(), blob.getStorage().getOptions());
        Assert.assertFalse(blob.isDirectory());
        builder = new Blob.Builder(new Blob(storage, new BlobInfo.BuilderImpl(BlobTest.DIRECTORY_INFO)));
        blob = builder.setBlobId(BlobId.of("b", "n/")).setIsDirectory(true).setSize(0L).build();
        Assert.assertEquals("b", blob.getBucket());
        Assert.assertEquals("n/", blob.getName());
        Assert.assertNull(blob.getAcl());
        Assert.assertNull(blob.getComponentCount());
        Assert.assertNull(blob.getContentType());
        Assert.assertNull(blob.getCacheControl());
        Assert.assertNull(blob.getContentDisposition());
        Assert.assertNull(blob.getContentEncoding());
        Assert.assertNull(blob.getContentLanguage());
        Assert.assertNull(blob.getCrc32c());
        Assert.assertNull(blob.getCrc32cToHexString());
        Assert.assertNull(blob.getCreateTime());
        Assert.assertNull(blob.getCustomerEncryption());
        Assert.assertNull(blob.getKmsKeyName());
        Assert.assertNull(blob.getEventBasedHold());
        Assert.assertNull(blob.getTemporaryHold());
        Assert.assertNull(blob.getRetentionExpirationTime());
        Assert.assertNull(blob.getDeleteTime());
        Assert.assertNull(blob.getEtag());
        Assert.assertNull(blob.getGeneratedId());
        Assert.assertNull(blob.getMd5());
        Assert.assertNull(blob.getMd5ToHexString());
        Assert.assertNull(blob.getMediaLink());
        Assert.assertNull(blob.getMetadata());
        Assert.assertNull(blob.getMetageneration());
        Assert.assertNull(blob.getOwner());
        Assert.assertNull(blob.getSelfLink());
        Assert.assertEquals(0L, ((long) (blob.getSize())));
        Assert.assertNull(blob.getUpdateTime());
        Assert.assertTrue(blob.isDirectory());
    }

    @Test
    public void testDownload() throws Exception {
        final byte[] expected = new byte[]{ 1, 2 };
        initializeExpectedBlob(2);
        ReadChannel channel = createNiceMock(ReadChannel.class);
        expect(storage.getOptions()).andReturn(mockOptions);
        expect(storage.reader(BlobTest.BLOB_INFO.getBlobId())).andReturn(channel);
        replay(storage);
        // First read should return 2 bytes.
        expect(channel.read(anyObject(ByteBuffer.class))).andAnswer(new org.easymock.IAnswer<Integer>() {
            @Override
            public Integer answer() throws Throwable {
                // Modify the argument to match the expected behavior of `read`.
                ((ByteBuffer) (getCurrentArguments()[0])).put(expected);
                return 2;
            }
        });
        // Second read should return 0 bytes.
        expect(channel.read(anyObject(ByteBuffer.class))).andReturn(0);
        replay(channel);
        initializeBlob();
        File file = File.createTempFile("blob", ".tmp");
        blob.downloadTo(file.toPath());
        byte[] actual = Files.readAllBytes(file.toPath());
        Assert.assertArrayEquals(expected, actual);
    }
}

