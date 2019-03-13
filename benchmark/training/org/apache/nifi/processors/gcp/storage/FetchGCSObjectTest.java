/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.processors.gcp.storage;


import Acl.Domain;
import Acl.Group;
import Acl.Project;
import Acl.User;
import BlobInfo.CustomerEncryption;
import CoreAttributes.FILENAME;
import CoreAttributes.MIME_TYPE;
import FetchGCSObject.BUCKET;
import FetchGCSObject.ENCRYPTION_KEY;
import FetchGCSObject.GENERATION;
import FetchGCSObject.KEY;
import FetchGCSObject.REL_FAILURE;
import FetchGCSObject.REL_SUCCESS;
import Storage.BlobSourceOption;
import com.google.cloud.ReadChannel;
import com.google.cloud.RestorableState;
import com.google.cloud.storage.Acl;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageException;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Set;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Unit tests for {@link FetchGCSObject}.
 */
public class FetchGCSObjectTest extends AbstractGCSTest {
    private static final String KEY = "test-key";

    private static final Long GENERATION = 5L;

    private static final String CONTENT = "test-content";

    private static final Long SIZE = 100L;

    private static final String CACHE_CONTROL = "test-cache-control";

    private static final Integer COMPONENT_COUNT = 3;

    private static final String CONTENT_ENCODING = "test-content-encoding";

    private static final String CONTENT_LANGUAGE = "test-content-language";

    private static final String CONTENT_TYPE = "test-content-type";

    private static final String CRC32C = "test-crc32c";

    private static final String ENCRYPTION = "test-encryption";

    private static final String ENCRYPTION_SHA256 = "test-encryption-256";

    private static final String ETAG = "test-etag";

    private static final String GENERATED_ID = "test-generated-id";

    private static final String MD5 = "test-md5";

    private static final String MEDIA_LINK = "test-media-link";

    private static final Long METAGENERATION = 42L;

    private static final String OWNER_USER_EMAIL = "test-owner-user-email";

    private static final String OWNER_GROUP_EMAIL = "test-owner-group-email";

    private static final String OWNER_DOMAIN = "test-owner-domain";

    private static final String OWNER_PROJECT_ID = "test-owner-project-id";

    private static final String URI = "test-uri";

    private static final String CONTENT_DISPOSITION = "attachment; filename=\"test-content-disposition.txt\"";

    private static final Long CREATE_TIME = 1234L;

    private static final Long UPDATE_TIME = 4567L;

    @Mock
    Storage storage;

    private class MockReadChannel implements ReadChannel {
        private byte[] toRead;

        private int position = 0;

        private boolean finished;

        private boolean isOpen;

        private MockReadChannel(String textToRead) {
            this.toRead = textToRead.getBytes();
            this.isOpen = true;
            this.finished = false;
        }

        @Override
        public void close() {
            this.isOpen = false;
        }

        @Override
        public void seek(long l) throws IOException {
        }

        @Override
        public void setChunkSize(int i) {
        }

        @Override
        public RestorableState<ReadChannel> capture() {
            return null;
        }

        @Override
        public int read(ByteBuffer dst) throws IOException {
            if (this.finished) {
                return -1;
            } else {
                if ((dst.remaining()) > (this.toRead.length)) {
                    this.finished = true;
                }
                int toWrite = Math.min(((this.toRead.length) - (position)), dst.remaining());
                dst.put(this.toRead, this.position, toWrite);
                this.position += toWrite;
                return toWrite;
            }
        }

        @Override
        public boolean isOpen() {
            return this.isOpen;
        }
    }

    @Test
    public void testSuccessfulFetch() throws Exception {
        Mockito.reset(storage);
        final TestRunner runner = AbstractGCSTest.buildNewRunner(getProcessor());
        addRequiredPropertiesToRunner(runner);
        runner.assertValid();
        final Blob blob = Mockito.mock(Blob.class);
        Mockito.when(blob.getBucket()).thenReturn(AbstractGCSTest.BUCKET);
        Mockito.when(blob.getName()).thenReturn(FetchGCSObjectTest.KEY);
        Mockito.when(blob.getSize()).thenReturn(FetchGCSObjectTest.SIZE);
        Mockito.when(blob.getCacheControl()).thenReturn(FetchGCSObjectTest.CACHE_CONTROL);
        Mockito.when(blob.getComponentCount()).thenReturn(FetchGCSObjectTest.COMPONENT_COUNT);
        Mockito.when(blob.getContentEncoding()).thenReturn(FetchGCSObjectTest.CONTENT_ENCODING);
        Mockito.when(blob.getContentLanguage()).thenReturn(FetchGCSObjectTest.CONTENT_LANGUAGE);
        Mockito.when(blob.getContentType()).thenReturn(FetchGCSObjectTest.CONTENT_TYPE);
        Mockito.when(blob.getCrc32c()).thenReturn(FetchGCSObjectTest.CRC32C);
        final BlobInfo.CustomerEncryption mockEncryption = Mockito.mock(CustomerEncryption.class);
        Mockito.when(mockEncryption.getEncryptionAlgorithm()).thenReturn(FetchGCSObjectTest.ENCRYPTION);
        Mockito.when(mockEncryption.getKeySha256()).thenReturn(FetchGCSObjectTest.ENCRYPTION_SHA256);
        Mockito.when(blob.getCustomerEncryption()).thenReturn(mockEncryption);
        Mockito.when(blob.getEtag()).thenReturn(FetchGCSObjectTest.ETAG);
        Mockito.when(blob.getGeneratedId()).thenReturn(FetchGCSObjectTest.GENERATED_ID);
        Mockito.when(blob.getGeneration()).thenReturn(FetchGCSObjectTest.GENERATION);
        Mockito.when(blob.getMd5()).thenReturn(FetchGCSObjectTest.MD5);
        Mockito.when(blob.getMediaLink()).thenReturn(FetchGCSObjectTest.MEDIA_LINK);
        Mockito.when(blob.getMetageneration()).thenReturn(FetchGCSObjectTest.METAGENERATION);
        Mockito.when(blob.getSelfLink()).thenReturn(FetchGCSObjectTest.URI);
        Mockito.when(blob.getContentDisposition()).thenReturn(FetchGCSObjectTest.CONTENT_DISPOSITION);
        Mockito.when(blob.getCreateTime()).thenReturn(FetchGCSObjectTest.CREATE_TIME);
        Mockito.when(blob.getUpdateTime()).thenReturn(FetchGCSObjectTest.UPDATE_TIME);
        Mockito.when(storage.get(ArgumentMatchers.any(BlobId.class))).thenReturn(blob);
        Mockito.when(storage.reader(ArgumentMatchers.any(BlobId.class), ArgumentMatchers.any(BlobSourceOption.class))).thenReturn(new FetchGCSObjectTest.MockReadChannel(FetchGCSObjectTest.CONTENT));
        runner.enqueue("");
        runner.run();
        Mockito.verify(storage).get(ArgumentMatchers.any(BlobId.class));
        Mockito.verify(storage).reader(ArgumentMatchers.any(BlobId.class), ArgumentMatchers.any(BlobSourceOption.class));
        runner.assertAllFlowFilesTransferred(REL_SUCCESS);
        runner.assertTransferCount(REL_SUCCESS, 1);
        final MockFlowFile flowFile = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        Assert.assertTrue(flowFile.isContentEqual(FetchGCSObjectTest.CONTENT));
        Assert.assertEquals(AbstractGCSTest.BUCKET, flowFile.getAttribute(StorageAttributes.BUCKET_ATTR));
        Assert.assertEquals(FetchGCSObjectTest.KEY, flowFile.getAttribute(StorageAttributes.KEY_ATTR));
        Assert.assertEquals(FetchGCSObjectTest.CACHE_CONTROL, flowFile.getAttribute(StorageAttributes.CACHE_CONTROL_ATTR));
        Assert.assertEquals(FetchGCSObjectTest.COMPONENT_COUNT, Integer.valueOf(flowFile.getAttribute(StorageAttributes.COMPONENT_COUNT_ATTR)));
        Assert.assertEquals(FetchGCSObjectTest.CONTENT_ENCODING, flowFile.getAttribute(StorageAttributes.CONTENT_ENCODING_ATTR));
        Assert.assertEquals(FetchGCSObjectTest.CONTENT_LANGUAGE, flowFile.getAttribute(StorageAttributes.CONTENT_LANGUAGE_ATTR));
        Assert.assertEquals(FetchGCSObjectTest.CONTENT_TYPE, flowFile.getAttribute(MIME_TYPE.key()));
        Assert.assertEquals(FetchGCSObjectTest.CRC32C, flowFile.getAttribute(StorageAttributes.CRC32C_ATTR));
        Assert.assertEquals(FetchGCSObjectTest.ENCRYPTION, flowFile.getAttribute(StorageAttributes.ENCRYPTION_ALGORITHM_ATTR));
        Assert.assertEquals(FetchGCSObjectTest.ENCRYPTION_SHA256, flowFile.getAttribute(StorageAttributes.ENCRYPTION_SHA256_ATTR));
        Assert.assertEquals(FetchGCSObjectTest.ETAG, flowFile.getAttribute(StorageAttributes.ETAG_ATTR));
        Assert.assertEquals(FetchGCSObjectTest.GENERATED_ID, flowFile.getAttribute(StorageAttributes.GENERATED_ID_ATTR));
        Assert.assertEquals(FetchGCSObjectTest.GENERATION, Long.valueOf(flowFile.getAttribute(StorageAttributes.GENERATION_ATTR)));
        Assert.assertEquals(FetchGCSObjectTest.MD5, flowFile.getAttribute(StorageAttributes.MD5_ATTR));
        Assert.assertEquals(FetchGCSObjectTest.MEDIA_LINK, flowFile.getAttribute(StorageAttributes.MEDIA_LINK_ATTR));
        Assert.assertEquals(FetchGCSObjectTest.METAGENERATION, Long.valueOf(flowFile.getAttribute(StorageAttributes.METAGENERATION_ATTR)));
        Assert.assertEquals(FetchGCSObjectTest.URI, flowFile.getAttribute(StorageAttributes.URI_ATTR));
        Assert.assertEquals(FetchGCSObjectTest.CONTENT_DISPOSITION, flowFile.getAttribute(StorageAttributes.CONTENT_DISPOSITION_ATTR));
        Assert.assertEquals(FetchGCSObjectTest.CREATE_TIME, Long.valueOf(flowFile.getAttribute(StorageAttributes.CREATE_TIME_ATTR)));
        Assert.assertEquals(FetchGCSObjectTest.UPDATE_TIME, Long.valueOf(flowFile.getAttribute(StorageAttributes.UPDATE_TIME_ATTR)));
    }

    @Test
    public void testAclOwnerUser() throws Exception {
        Mockito.reset(storage);
        final TestRunner runner = AbstractGCSTest.buildNewRunner(getProcessor());
        addRequiredPropertiesToRunner(runner);
        runner.assertValid();
        final Blob blob = Mockito.mock(Blob.class);
        final Acl.User mockUser = Mockito.mock(User.class);
        Mockito.when(mockUser.getEmail()).thenReturn(FetchGCSObjectTest.OWNER_USER_EMAIL);
        Mockito.when(blob.getOwner()).thenReturn(mockUser);
        Mockito.when(storage.get(ArgumentMatchers.any(BlobId.class))).thenReturn(blob);
        Mockito.when(storage.reader(ArgumentMatchers.any(BlobId.class), ArgumentMatchers.any(BlobSourceOption.class))).thenReturn(new FetchGCSObjectTest.MockReadChannel(FetchGCSObjectTest.CONTENT));
        runner.enqueue("");
        runner.run();
        Mockito.verify(storage).get(ArgumentMatchers.any(BlobId.class));
        Mockito.verify(storage).reader(ArgumentMatchers.any(BlobId.class), ArgumentMatchers.any(BlobSourceOption.class));
        runner.assertAllFlowFilesTransferred(REL_SUCCESS);
        runner.assertTransferCount(REL_SUCCESS, 1);
        final MockFlowFile flowFile = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        Assert.assertEquals(FetchGCSObjectTest.OWNER_USER_EMAIL, flowFile.getAttribute(StorageAttributes.OWNER_ATTR));
        Assert.assertEquals("user", flowFile.getAttribute(StorageAttributes.OWNER_TYPE_ATTR));
    }

    @Test
    public void testAclOwnerGroup() throws Exception {
        Mockito.reset(storage);
        final TestRunner runner = AbstractGCSTest.buildNewRunner(getProcessor());
        addRequiredPropertiesToRunner(runner);
        runner.assertValid();
        final Blob blob = Mockito.mock(Blob.class);
        final Acl.Group mockGroup = Mockito.mock(Group.class);
        Mockito.when(mockGroup.getEmail()).thenReturn(FetchGCSObjectTest.OWNER_GROUP_EMAIL);
        Mockito.when(blob.getOwner()).thenReturn(mockGroup);
        Mockito.when(storage.get(ArgumentMatchers.any(BlobId.class))).thenReturn(blob);
        Mockito.when(storage.reader(ArgumentMatchers.any(BlobId.class), ArgumentMatchers.any(BlobSourceOption.class))).thenReturn(new FetchGCSObjectTest.MockReadChannel(FetchGCSObjectTest.CONTENT));
        runner.enqueue("");
        runner.run();
        Mockito.verify(storage).get(ArgumentMatchers.any(BlobId.class));
        Mockito.verify(storage).reader(ArgumentMatchers.any(BlobId.class), ArgumentMatchers.any(BlobSourceOption.class));
        runner.assertAllFlowFilesTransferred(REL_SUCCESS);
        runner.assertTransferCount(REL_SUCCESS, 1);
        final MockFlowFile flowFile = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        Assert.assertEquals(FetchGCSObjectTest.OWNER_GROUP_EMAIL, flowFile.getAttribute(StorageAttributes.OWNER_ATTR));
        Assert.assertEquals("group", flowFile.getAttribute(StorageAttributes.OWNER_TYPE_ATTR));
    }

    @Test
    public void testAclOwnerDomain() throws Exception {
        Mockito.reset(storage);
        final TestRunner runner = AbstractGCSTest.buildNewRunner(getProcessor());
        addRequiredPropertiesToRunner(runner);
        runner.assertValid();
        final Blob blob = Mockito.mock(Blob.class);
        final Acl.Domain mockDomain = Mockito.mock(Domain.class);
        Mockito.when(mockDomain.getDomain()).thenReturn(FetchGCSObjectTest.OWNER_DOMAIN);
        Mockito.when(blob.getOwner()).thenReturn(mockDomain);
        Mockito.when(storage.get(ArgumentMatchers.any(BlobId.class))).thenReturn(blob);
        Mockito.when(storage.reader(ArgumentMatchers.any(BlobId.class), ArgumentMatchers.any(BlobSourceOption.class))).thenReturn(new FetchGCSObjectTest.MockReadChannel(FetchGCSObjectTest.CONTENT));
        runner.enqueue("");
        runner.run();
        Mockito.verify(storage).get(ArgumentMatchers.any(BlobId.class));
        Mockito.verify(storage).reader(ArgumentMatchers.any(BlobId.class), ArgumentMatchers.any(BlobSourceOption.class));
        runner.assertAllFlowFilesTransferred(REL_SUCCESS);
        runner.assertTransferCount(REL_SUCCESS, 1);
        final MockFlowFile flowFile = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        Assert.assertEquals(FetchGCSObjectTest.OWNER_DOMAIN, flowFile.getAttribute(StorageAttributes.OWNER_ATTR));
        Assert.assertEquals("domain", flowFile.getAttribute(StorageAttributes.OWNER_TYPE_ATTR));
    }

    @Test
    public void testAclOwnerProject() throws Exception {
        Mockito.reset(storage);
        final TestRunner runner = AbstractGCSTest.buildNewRunner(getProcessor());
        addRequiredPropertiesToRunner(runner);
        runner.assertValid();
        final Blob blob = Mockito.mock(Blob.class);
        final Acl.Project mockProject = Mockito.mock(Project.class);
        Mockito.when(mockProject.getProjectId()).thenReturn(FetchGCSObjectTest.OWNER_PROJECT_ID);
        Mockito.when(blob.getOwner()).thenReturn(mockProject);
        Mockito.when(storage.get(ArgumentMatchers.any(BlobId.class))).thenReturn(blob);
        Mockito.when(storage.reader(ArgumentMatchers.any(BlobId.class), ArgumentMatchers.any(BlobSourceOption.class))).thenReturn(new FetchGCSObjectTest.MockReadChannel(FetchGCSObjectTest.CONTENT));
        runner.enqueue("");
        runner.run();
        Mockito.verify(storage).get(ArgumentMatchers.any(BlobId.class));
        Mockito.verify(storage).reader(ArgumentMatchers.any(BlobId.class), ArgumentMatchers.any(BlobSourceOption.class));
        runner.assertAllFlowFilesTransferred(REL_SUCCESS);
        runner.assertTransferCount(REL_SUCCESS, 1);
        final MockFlowFile flowFile = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        Assert.assertEquals(FetchGCSObjectTest.OWNER_PROJECT_ID, flowFile.getAttribute(StorageAttributes.OWNER_ATTR));
        Assert.assertEquals("project", flowFile.getAttribute(StorageAttributes.OWNER_TYPE_ATTR));
    }

    @Test
    public void testBlobIdWithGeneration() throws Exception {
        Mockito.reset(storage);
        final TestRunner runner = AbstractGCSTest.buildNewRunner(getProcessor());
        addRequiredPropertiesToRunner(runner);
        runner.removeProperty(FetchGCSObject.KEY);
        runner.removeProperty(FetchGCSObject.BUCKET);
        runner.setProperty(FetchGCSObject.GENERATION, String.valueOf(FetchGCSObjectTest.GENERATION));
        runner.assertValid();
        final Blob blob = Mockito.mock(Blob.class);
        Mockito.when(storage.get(ArgumentMatchers.any(BlobId.class))).thenReturn(blob);
        Mockito.when(storage.reader(ArgumentMatchers.any(BlobId.class), ArgumentMatchers.any(BlobSourceOption.class))).thenReturn(new FetchGCSObjectTest.MockReadChannel(FetchGCSObjectTest.CONTENT));
        runner.enqueue("", ImmutableMap.of(StorageAttributes.BUCKET_ATTR, AbstractGCSTest.BUCKET, FILENAME.key(), FetchGCSObjectTest.KEY));
        runner.run();
        ArgumentCaptor<BlobId> blobIdArgumentCaptor = ArgumentCaptor.forClass(BlobId.class);
        ArgumentCaptor<Storage.BlobSourceOption> blobSourceOptionArgumentCaptor = ArgumentCaptor.forClass(BlobSourceOption.class);
        Mockito.verify(storage).get(blobIdArgumentCaptor.capture());
        Mockito.verify(storage).reader(ArgumentMatchers.any(BlobId.class), blobSourceOptionArgumentCaptor.capture());
        final BlobId blobId = blobIdArgumentCaptor.getValue();
        Assert.assertEquals(AbstractGCSTest.BUCKET, blobId.getBucket());
        Assert.assertEquals(FetchGCSObjectTest.KEY, blobId.getName());
        Assert.assertEquals(FetchGCSObjectTest.GENERATION, blobId.getGeneration());
        final Set<Storage.BlobSourceOption> blobSourceOptions = ImmutableSet.copyOf(blobSourceOptionArgumentCaptor.getAllValues());
        Assert.assertTrue(blobSourceOptions.contains(BlobSourceOption.generationMatch()));
        Assert.assertEquals(1, blobSourceOptions.size());
    }

    @Test
    public void testBlobIdWithEncryption() throws Exception {
        Mockito.reset(storage);
        final TestRunner runner = AbstractGCSTest.buildNewRunner(getProcessor());
        runner.setProperty(ENCRYPTION_KEY, FetchGCSObjectTest.ENCRYPTION_SHA256);
        addRequiredPropertiesToRunner(runner);
        runner.assertValid();
        final Blob blob = Mockito.mock(Blob.class);
        Mockito.when(storage.get(ArgumentMatchers.any(BlobId.class))).thenReturn(blob);
        Mockito.when(storage.reader(ArgumentMatchers.any(BlobId.class), ArgumentMatchers.any(BlobSourceOption.class))).thenReturn(new FetchGCSObjectTest.MockReadChannel(FetchGCSObjectTest.CONTENT));
        runner.enqueue("");
        runner.run();
        ArgumentCaptor<BlobId> blobIdArgumentCaptor = ArgumentCaptor.forClass(BlobId.class);
        ArgumentCaptor<Storage.BlobSourceOption> blobSourceOptionArgumentCaptor = ArgumentCaptor.forClass(BlobSourceOption.class);
        Mockito.verify(storage).get(blobIdArgumentCaptor.capture());
        Mockito.verify(storage).reader(ArgumentMatchers.any(BlobId.class), blobSourceOptionArgumentCaptor.capture());
        final BlobId blobId = blobIdArgumentCaptor.getValue();
        Assert.assertEquals(AbstractGCSTest.BUCKET, blobId.getBucket());
        Assert.assertEquals(FetchGCSObjectTest.KEY, blobId.getName());
        Assert.assertNull(blobId.getGeneration());
        final Set<Storage.BlobSourceOption> blobSourceOptions = ImmutableSet.copyOf(blobSourceOptionArgumentCaptor.getAllValues());
        Assert.assertTrue(blobSourceOptions.contains(BlobSourceOption.decryptionKey(FetchGCSObjectTest.ENCRYPTION_SHA256)));
        Assert.assertEquals(1, blobSourceOptions.size());
    }

    @Test
    public void testStorageExceptionOnFetch() throws Exception {
        Mockito.reset(storage);
        final TestRunner runner = AbstractGCSTest.buildNewRunner(getProcessor());
        addRequiredPropertiesToRunner(runner);
        runner.assertValid();
        Mockito.when(storage.get(ArgumentMatchers.any(BlobId.class))).thenThrow(new StorageException(400, "test-exception"));
        Mockito.when(storage.reader(ArgumentMatchers.any(BlobId.class), ArgumentMatchers.any(BlobSourceOption.class))).thenReturn(new FetchGCSObjectTest.MockReadChannel(FetchGCSObjectTest.CONTENT));
        runner.enqueue("");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_FAILURE);
        runner.assertTransferCount(REL_FAILURE, 1);
    }
}

