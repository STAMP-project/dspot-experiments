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
import PutGCSObject.ACL;
import PutGCSObject.CONTENT_DISPOSITION_TYPE;
import PutGCSObject.CONTENT_TYPE;
import PutGCSObject.CRC32C;
import PutGCSObject.ENCRYPTION_KEY;
import PutGCSObject.KEY;
import PutGCSObject.MD5;
import PutGCSObject.OVERWRITE;
import PutGCSObject.REL_FAILURE;
import PutGCSObject.REL_SUCCESS;
import Storage.BlobWriteOption;
import Storage.PredefinedAcl;
import com.google.cloud.storage.Acl;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageException;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Unit tests for {@link PutGCSObject} which do not use Google Cloud resources.
 */
@SuppressWarnings("deprecation")
public class PutGCSObjectTest extends AbstractGCSTest {
    private static final String FILENAME = "test-filename";

    private static final String KEY = "test-key";

    private static final String CONTENT_TYPE = "test-content-type";

    private static final String MD5 = "test-md5";

    private static final String CRC32C = "test-crc32c";

    private static final PredefinedAcl ACL = BUCKET_OWNER_READ;

    private static final String ENCRYPTION_KEY = "test-encryption-key";

    private static final Boolean OVERWRITE = false;

    private static final String CONTENT_DISPOSITION_TYPE = "inline";

    private static final Long SIZE = 100L;

    private static final String CACHE_CONTROL = "test-cache-control";

    private static final Integer COMPONENT_COUNT = 3;

    private static final String CONTENT_ENCODING = "test-content-encoding";

    private static final String CONTENT_LANGUAGE = "test-content-language";

    private static final String ENCRYPTION = "test-encryption";

    private static final String ENCRYPTION_SHA256 = "test-encryption-256";

    private static final String ETAG = "test-etag";

    private static final String GENERATED_ID = "test-generated-id";

    private static final String MEDIA_LINK = "test-media-link";

    private static final Long METAGENERATION = 42L;

    private static final String OWNER_USER_EMAIL = "test-owner-user-email";

    private static final String OWNER_GROUP_EMAIL = "test-owner-group-email";

    private static final String OWNER_DOMAIN = "test-owner-domain";

    private static final String OWNER_PROJECT_ID = "test-owner-project-id";

    private static final String URI = "test-uri";

    private static final String CONTENT_DISPOSITION = ("attachment; filename=\"" + (PutGCSObjectTest.FILENAME)) + "\"";

    private static final Long CREATE_TIME = 1234L;

    private static final Long UPDATE_TIME = 4567L;

    private static final Long GENERATION = 5L;

    @Mock
    Storage storage;

    @Mock
    Blob blob;

    @Captor
    ArgumentCaptor<Storage.BlobWriteOption> blobWriteOptionArgumentCaptor;

    @Captor
    ArgumentCaptor<InputStream> inputStreamArgumentCaptor;

    @Captor
    ArgumentCaptor<BlobInfo> blobInfoArgumentCaptor;

    @Test
    public void testSuccessfulPutOperationNoParameters() throws Exception {
        Mockito.reset(storage, blob);
        final PutGCSObject processor = getProcessor();
        final TestRunner runner = AbstractGCSTest.buildNewRunner(processor);
        addRequiredPropertiesToRunner(runner);
        runner.assertValid();
        Mockito.when(storage.create(blobInfoArgumentCaptor.capture(), inputStreamArgumentCaptor.capture(), blobWriteOptionArgumentCaptor.capture())).thenReturn(blob);
        runner.enqueue("test");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS);
        runner.assertTransferCount(REL_SUCCESS, 1);
        /**
         * Can't do this any more due to the switch to Java InputStreams which close after an operation *
         */
        /* String text;
        try (final Reader reader = new InputStreamReader(inputStreamArgumentCaptor.getValue())) {
        text = CharStreams.toString(reader);
        }

        assertEquals(
        "FlowFile content should be equal to the Blob content",
        "test",
        text
        );
         */
        final List<Storage.BlobWriteOption> blobWriteOptions = blobWriteOptionArgumentCaptor.getAllValues();
        Assert.assertEquals("No BlobWriteOptions should be set", 0, blobWriteOptions.size());
        final BlobInfo blobInfo = blobInfoArgumentCaptor.getValue();
        Assert.assertNull(blobInfo.getMd5());
        Assert.assertNull(blobInfo.getContentDisposition());
        Assert.assertNull(blobInfo.getCrc32c());
    }

    @Test
    public void testSuccessfulPutOperation() throws Exception {
        Mockito.reset(storage, blob);
        final PutGCSObject processor = getProcessor();
        final TestRunner runner = AbstractGCSTest.buildNewRunner(processor);
        addRequiredPropertiesToRunner(runner);
        runner.setProperty(PutGCSObject.KEY, PutGCSObjectTest.KEY);
        runner.setProperty(PutGCSObject.CONTENT_TYPE, PutGCSObjectTest.CONTENT_TYPE);
        runner.setProperty(PutGCSObject.MD5, PutGCSObjectTest.MD5);
        runner.setProperty(PutGCSObject.CRC32C, PutGCSObjectTest.CRC32C);
        runner.setProperty(PutGCSObject.ACL, PutGCSObjectTest.ACL.name());
        runner.setProperty(PutGCSObject.ENCRYPTION_KEY, PutGCSObjectTest.ENCRYPTION_KEY);
        runner.setProperty(PutGCSObject.OVERWRITE, String.valueOf(PutGCSObjectTest.OVERWRITE));
        runner.setProperty(PutGCSObject.CONTENT_DISPOSITION_TYPE, PutGCSObjectTest.CONTENT_DISPOSITION_TYPE);
        runner.assertValid();
        Mockito.when(storage.create(blobInfoArgumentCaptor.capture(), inputStreamArgumentCaptor.capture(), blobWriteOptionArgumentCaptor.capture())).thenReturn(blob);
        runner.enqueue("test", ImmutableMap.of(CoreAttributes.FILENAME.key(), PutGCSObjectTest.FILENAME));
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS);
        runner.assertTransferCount(REL_SUCCESS, 1);
        /* String text;
        try (final Reader reader = new InputStreamReader(inputStreamArgumentCaptor.getValue())) {
        text = CharStreams.toString(reader);
        }

        assertEquals(
        "FlowFile content should be equal to the Blob content",
        "test",
        text
        );
         */
        final BlobInfo blobInfo = blobInfoArgumentCaptor.getValue();
        Assert.assertEquals(AbstractGCSTest.BUCKET, blobInfo.getBucket());
        Assert.assertEquals(PutGCSObjectTest.KEY, blobInfo.getName());
        Assert.assertEquals((((PutGCSObjectTest.CONTENT_DISPOSITION_TYPE) + "; filename=") + (PutGCSObjectTest.FILENAME)), blobInfo.getContentDisposition());
        Assert.assertEquals(PutGCSObjectTest.CONTENT_TYPE, blobInfo.getContentType());
        Assert.assertEquals(PutGCSObjectTest.MD5, blobInfo.getMd5());
        Assert.assertEquals(PutGCSObjectTest.CRC32C, blobInfo.getCrc32c());
        Assert.assertNull(blobInfo.getMetadata());
        final List<Storage.BlobWriteOption> blobWriteOptions = blobWriteOptionArgumentCaptor.getAllValues();
        final Set<Storage.BlobWriteOption> blobWriteOptionSet = ImmutableSet.copyOf(blobWriteOptions);
        Assert.assertEquals("Each of the BlobWriteOptions should be unique", blobWriteOptions.size(), blobWriteOptionSet.size());
        Assert.assertTrue("The doesNotExist BlobWriteOption should be set if OVERWRITE is false", blobWriteOptionSet.contains(BlobWriteOption.doesNotExist()));
        Assert.assertTrue("The md5Match BlobWriteOption should be set if MD5 is non-null", blobWriteOptionSet.contains(BlobWriteOption.md5Match()));
        Assert.assertTrue("The crc32cMatch BlobWriteOption should be set if CRC32C is non-null", blobWriteOptionSet.contains(BlobWriteOption.crc32cMatch()));
        Assert.assertTrue("The predefinedAcl BlobWriteOption should be set if ACL is non-null", blobWriteOptionSet.contains(BlobWriteOption.predefinedAcl(PutGCSObjectTest.ACL)));
        Assert.assertTrue("The encryptionKey BlobWriteOption should be set if ENCRYPTION_KEY is non-null", blobWriteOptionSet.contains(BlobWriteOption.encryptionKey(PutGCSObjectTest.ENCRYPTION_KEY)));
    }

    @Test
    public void testSuccessfulPutOperationWithUserMetadata() throws Exception {
        Mockito.reset(storage, blob);
        final PutGCSObject processor = getProcessor();
        final TestRunner runner = AbstractGCSTest.buildNewRunner(processor);
        addRequiredPropertiesToRunner(runner);
        runner.setProperty("testMetadataKey1", "testMetadataValue1");
        runner.setProperty("testMetadataKey2", "testMetadataValue2");
        runner.assertValid();
        Mockito.when(storage.create(blobInfoArgumentCaptor.capture(), inputStreamArgumentCaptor.capture(), blobWriteOptionArgumentCaptor.capture())).thenReturn(blob);
        runner.enqueue("test");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS);
        runner.assertTransferCount(REL_SUCCESS, 1);
        /* String text;
        try (final Reader reader = new InputStreamReader(inputStreamArgumentCaptor.getValue())) {
        text = CharStreams.toString(reader);
        }

        assertEquals(
        "FlowFile content should be equal to the Blob content",
        "test",
        text
        );
         */
        final BlobInfo blobInfo = blobInfoArgumentCaptor.getValue();
        final Map<String, String> metadata = blobInfo.getMetadata();
        Assert.assertNotNull(metadata);
        Assert.assertEquals(2, metadata.size());
        Assert.assertEquals("testMetadataValue1", metadata.get("testMetadataKey1"));
        Assert.assertEquals("testMetadataValue2", metadata.get("testMetadataKey2"));
    }

    @Test
    public void testAttributesSetOnSuccessfulPut() throws Exception {
        Mockito.reset(storage, blob);
        final PutGCSObject processor = getProcessor();
        final TestRunner runner = AbstractGCSTest.buildNewRunner(processor);
        addRequiredPropertiesToRunner(runner);
        runner.assertValid();
        Mockito.when(storage.create(ArgumentMatchers.any(BlobInfo.class), ArgumentMatchers.any(InputStream.class), ArgumentMatchers.any(BlobWriteOption.class))).thenReturn(blob);
        Mockito.when(blob.getBucket()).thenReturn(AbstractGCSTest.BUCKET);
        Mockito.when(blob.getName()).thenReturn(PutGCSObjectTest.KEY);
        Mockito.when(blob.getSize()).thenReturn(PutGCSObjectTest.SIZE);
        Mockito.when(blob.getCacheControl()).thenReturn(PutGCSObjectTest.CACHE_CONTROL);
        Mockito.when(blob.getComponentCount()).thenReturn(PutGCSObjectTest.COMPONENT_COUNT);
        Mockito.when(blob.getContentDisposition()).thenReturn(PutGCSObjectTest.CONTENT_DISPOSITION);
        Mockito.when(blob.getContentEncoding()).thenReturn(PutGCSObjectTest.CONTENT_ENCODING);
        Mockito.when(blob.getContentLanguage()).thenReturn(PutGCSObjectTest.CONTENT_LANGUAGE);
        Mockito.when(blob.getContentType()).thenReturn(PutGCSObjectTest.CONTENT_TYPE);
        Mockito.when(blob.getCrc32c()).thenReturn(PutGCSObjectTest.CRC32C);
        final BlobInfo.CustomerEncryption mockEncryption = Mockito.mock(CustomerEncryption.class);
        Mockito.when(blob.getCustomerEncryption()).thenReturn(mockEncryption);
        Mockito.when(mockEncryption.getEncryptionAlgorithm()).thenReturn(PutGCSObjectTest.ENCRYPTION);
        Mockito.when(mockEncryption.getKeySha256()).thenReturn(PutGCSObjectTest.ENCRYPTION_SHA256);
        Mockito.when(blob.getEtag()).thenReturn(PutGCSObjectTest.ETAG);
        Mockito.when(blob.getGeneratedId()).thenReturn(PutGCSObjectTest.GENERATED_ID);
        Mockito.when(blob.getGeneration()).thenReturn(PutGCSObjectTest.GENERATION);
        Mockito.when(blob.getMd5()).thenReturn(PutGCSObjectTest.MD5);
        Mockito.when(blob.getMediaLink()).thenReturn(PutGCSObjectTest.MEDIA_LINK);
        Mockito.when(blob.getMetageneration()).thenReturn(PutGCSObjectTest.METAGENERATION);
        Mockito.when(blob.getSelfLink()).thenReturn(PutGCSObjectTest.URI);
        Mockito.when(blob.getCreateTime()).thenReturn(PutGCSObjectTest.CREATE_TIME);
        Mockito.when(blob.getUpdateTime()).thenReturn(PutGCSObjectTest.UPDATE_TIME);
        runner.enqueue("test");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS);
        runner.assertTransferCount(REL_SUCCESS, 1);
        final MockFlowFile mockFlowFile = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        mockFlowFile.assertAttributeEquals(StorageAttributes.BUCKET_ATTR, AbstractGCSTest.BUCKET);
        mockFlowFile.assertAttributeEquals(StorageAttributes.KEY_ATTR, PutGCSObjectTest.KEY);
        mockFlowFile.assertAttributeEquals(StorageAttributes.SIZE_ATTR, String.valueOf(PutGCSObjectTest.SIZE));
        mockFlowFile.assertAttributeEquals(StorageAttributes.CACHE_CONTROL_ATTR, PutGCSObjectTest.CACHE_CONTROL);
        mockFlowFile.assertAttributeEquals(StorageAttributes.COMPONENT_COUNT_ATTR, String.valueOf(PutGCSObjectTest.COMPONENT_COUNT));
        mockFlowFile.assertAttributeEquals(StorageAttributes.CONTENT_DISPOSITION_ATTR, PutGCSObjectTest.CONTENT_DISPOSITION);
        mockFlowFile.assertAttributeEquals(CoreAttributes.FILENAME.key(), PutGCSObjectTest.FILENAME);
        mockFlowFile.assertAttributeEquals(StorageAttributes.CONTENT_ENCODING_ATTR, PutGCSObjectTest.CONTENT_ENCODING);
        mockFlowFile.assertAttributeEquals(StorageAttributes.CONTENT_LANGUAGE_ATTR, PutGCSObjectTest.CONTENT_LANGUAGE);
        mockFlowFile.assertAttributeEquals(MIME_TYPE.key(), PutGCSObjectTest.CONTENT_TYPE);
        mockFlowFile.assertAttributeEquals(StorageAttributes.CRC32C_ATTR, PutGCSObjectTest.CRC32C);
        mockFlowFile.assertAttributeEquals(StorageAttributes.ENCRYPTION_ALGORITHM_ATTR, PutGCSObjectTest.ENCRYPTION);
        mockFlowFile.assertAttributeEquals(StorageAttributes.ENCRYPTION_SHA256_ATTR, PutGCSObjectTest.ENCRYPTION_SHA256);
        mockFlowFile.assertAttributeEquals(StorageAttributes.ETAG_ATTR, PutGCSObjectTest.ETAG);
        mockFlowFile.assertAttributeEquals(StorageAttributes.GENERATED_ID_ATTR, PutGCSObjectTest.GENERATED_ID);
        mockFlowFile.assertAttributeEquals(StorageAttributes.GENERATION_ATTR, String.valueOf(PutGCSObjectTest.GENERATION));
        mockFlowFile.assertAttributeEquals(StorageAttributes.MD5_ATTR, PutGCSObjectTest.MD5);
        mockFlowFile.assertAttributeEquals(StorageAttributes.MEDIA_LINK_ATTR, PutGCSObjectTest.MEDIA_LINK);
        mockFlowFile.assertAttributeEquals(StorageAttributes.METAGENERATION_ATTR, String.valueOf(PutGCSObjectTest.METAGENERATION));
        mockFlowFile.assertAttributeEquals(StorageAttributes.URI_ATTR, PutGCSObjectTest.URI);
        mockFlowFile.assertAttributeEquals(StorageAttributes.CREATE_TIME_ATTR, String.valueOf(PutGCSObjectTest.CREATE_TIME));
        mockFlowFile.assertAttributeEquals(StorageAttributes.UPDATE_TIME_ATTR, String.valueOf(PutGCSObjectTest.UPDATE_TIME));
    }

    @Test
    public void testAclAttributeUser() throws Exception {
        Mockito.reset(storage, blob);
        final PutGCSObject processor = getProcessor();
        final TestRunner runner = AbstractGCSTest.buildNewRunner(processor);
        addRequiredPropertiesToRunner(runner);
        runner.assertValid();
        Mockito.when(storage.create(ArgumentMatchers.any(BlobInfo.class), ArgumentMatchers.any(InputStream.class), ArgumentMatchers.any(BlobWriteOption.class))).thenReturn(blob);
        final Acl.User mockUser = Mockito.mock(User.class);
        Mockito.when(mockUser.getEmail()).thenReturn(PutGCSObjectTest.OWNER_USER_EMAIL);
        Mockito.when(blob.getOwner()).thenReturn(mockUser);
        runner.enqueue("test");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS);
        runner.assertTransferCount(REL_SUCCESS, 1);
        final MockFlowFile mockFlowFile = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        mockFlowFile.assertAttributeEquals(StorageAttributes.OWNER_ATTR, PutGCSObjectTest.OWNER_USER_EMAIL);
        mockFlowFile.assertAttributeEquals(StorageAttributes.OWNER_TYPE_ATTR, "user");
    }

    @Test
    public void testAclAttributeGroup() throws Exception {
        Mockito.reset(storage, blob);
        final PutGCSObject processor = getProcessor();
        final TestRunner runner = AbstractGCSTest.buildNewRunner(processor);
        addRequiredPropertiesToRunner(runner);
        runner.assertValid();
        Mockito.when(storage.create(ArgumentMatchers.any(BlobInfo.class), ArgumentMatchers.any(InputStream.class), ArgumentMatchers.any(BlobWriteOption.class))).thenReturn(blob);
        final Acl.Group mockGroup = Mockito.mock(Group.class);
        Mockito.when(mockGroup.getEmail()).thenReturn(PutGCSObjectTest.OWNER_GROUP_EMAIL);
        Mockito.when(blob.getOwner()).thenReturn(mockGroup);
        runner.enqueue("test");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS);
        runner.assertTransferCount(REL_SUCCESS, 1);
        final MockFlowFile mockFlowFile = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        mockFlowFile.assertAttributeEquals(StorageAttributes.OWNER_ATTR, PutGCSObjectTest.OWNER_GROUP_EMAIL);
        mockFlowFile.assertAttributeEquals(StorageAttributes.OWNER_TYPE_ATTR, "group");
    }

    @Test
    public void testAclAttributeDomain() throws Exception {
        Mockito.reset(storage, blob);
        final PutGCSObject processor = getProcessor();
        final TestRunner runner = AbstractGCSTest.buildNewRunner(processor);
        addRequiredPropertiesToRunner(runner);
        runner.assertValid();
        Mockito.when(storage.create(ArgumentMatchers.any(BlobInfo.class), ArgumentMatchers.any(InputStream.class), ArgumentMatchers.any(BlobWriteOption.class))).thenReturn(blob);
        final Acl.Domain mockDomain = Mockito.mock(Domain.class);
        Mockito.when(mockDomain.getDomain()).thenReturn(PutGCSObjectTest.OWNER_DOMAIN);
        Mockito.when(blob.getOwner()).thenReturn(mockDomain);
        runner.enqueue("test");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS);
        runner.assertTransferCount(REL_SUCCESS, 1);
        final MockFlowFile mockFlowFile = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        mockFlowFile.assertAttributeEquals(StorageAttributes.OWNER_ATTR, PutGCSObjectTest.OWNER_DOMAIN);
        mockFlowFile.assertAttributeEquals(StorageAttributes.OWNER_TYPE_ATTR, "domain");
    }

    @Test
    public void testAclAttributeProject() throws Exception {
        Mockito.reset(storage, blob);
        final PutGCSObject processor = getProcessor();
        final TestRunner runner = AbstractGCSTest.buildNewRunner(processor);
        addRequiredPropertiesToRunner(runner);
        runner.assertValid();
        Mockito.when(storage.create(ArgumentMatchers.any(BlobInfo.class), ArgumentMatchers.any(InputStream.class), ArgumentMatchers.any(BlobWriteOption.class))).thenReturn(blob);
        final Acl.Project mockProject = Mockito.mock(Project.class);
        Mockito.when(mockProject.getProjectId()).thenReturn(PutGCSObjectTest.OWNER_PROJECT_ID);
        Mockito.when(blob.getOwner()).thenReturn(mockProject);
        runner.enqueue("test");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS);
        runner.assertTransferCount(REL_SUCCESS, 1);
        final MockFlowFile mockFlowFile = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        mockFlowFile.assertAttributeEquals(StorageAttributes.OWNER_ATTR, PutGCSObjectTest.OWNER_PROJECT_ID);
        mockFlowFile.assertAttributeEquals(StorageAttributes.OWNER_TYPE_ATTR, "project");
    }

    @Test
    public void testFailureHandling() throws Exception {
        Mockito.reset(storage);
        final PutGCSObject processor = getProcessor();
        final TestRunner runner = AbstractGCSTest.buildNewRunner(processor);
        addRequiredPropertiesToRunner(runner);
        runner.assertValid();
        Mockito.when(storage.create(ArgumentMatchers.any(BlobInfo.class), ArgumentMatchers.any(InputStream.class), ArgumentMatchers.any(BlobWriteOption.class))).thenThrow(new StorageException(404, "test exception"));
        runner.enqueue("test");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_FAILURE);
        runner.assertTransferCount(REL_FAILURE, 1);
        final MockFlowFile mockFlowFile = runner.getFlowFilesForRelationship(REL_FAILURE).get(0);
        Assert.assertTrue(mockFlowFile.isPenalized());
    }
}

