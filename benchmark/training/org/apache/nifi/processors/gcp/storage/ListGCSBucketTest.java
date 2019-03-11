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
import CoreAttributes.MIME_TYPE;
import ListGCSBucket.CURRENT_TIMESTAMP;
import ListGCSBucket.PREFIX;
import ListGCSBucket.REL_SUCCESS;
import ListGCSBucket.USE_GENERATIONS;
import Scope.CLUSTER;
import Storage.BlobListOption;
import com.google.api.gax.paging.Page;
import com.google.cloud.storage.Acl;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Map;
import org.apache.nifi.components.state.StateMap;
import org.apache.nifi.util.LogMessage;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;

import static ListGCSBucket.CURRENT_KEY_PREFIX;


/**
 * Unit tests for {@link ListGCSBucket} which do not consume Google Cloud resources.
 */
public class ListGCSBucketTest extends AbstractGCSTest {
    private static final String PREFIX = "test-prefix";

    private static final Boolean USE_GENERATIONS = true;

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

    private static final Long GENERATION = 5L;

    @Mock
    Storage storage;

    @Captor
    ArgumentCaptor<Storage.BlobListOption> argumentCaptor;

    @Test
    public void testRestoreFreshState() throws Exception {
        Mockito.reset(storage);
        final ListGCSBucket processor = getProcessor();
        final TestRunner runner = AbstractGCSTest.buildNewRunner(processor);
        addRequiredPropertiesToRunner(runner);
        runner.assertValid();
        Assert.assertEquals("Cluster StateMap should be fresh (version -1L)", (-1L), runner.getProcessContext().getStateManager().getState(CLUSTER).getVersion());
        Assert.assertNull(processor.currentKeys);
        processor.restoreState(runner.getProcessContext());
        Assert.assertNotNull(processor.currentKeys);
        Assert.assertEquals(0L, processor.currentTimestamp);
        Assert.assertTrue(processor.currentKeys.isEmpty());
    }

    @Test
    public void testRestorePreviousState() throws Exception {
        Mockito.reset(storage);
        final ListGCSBucket processor = getProcessor();
        final TestRunner runner = AbstractGCSTest.buildNewRunner(processor);
        addRequiredPropertiesToRunner(runner);
        runner.assertValid();
        final Map<String, String> state = ImmutableMap.of(CURRENT_TIMESTAMP, String.valueOf(4L), ((CURRENT_KEY_PREFIX) + "0"), "test-key-0", ((CURRENT_KEY_PREFIX) + "1"), "test-key-1");
        runner.getStateManager().setState(state, CLUSTER);
        Assert.assertNull(processor.currentKeys);
        Assert.assertEquals(0L, processor.currentTimestamp);
        processor.restoreState(runner.getProcessContext());
        Assert.assertNotNull(processor.currentKeys);
        Assert.assertTrue(processor.currentKeys.contains("test-key-0"));
        Assert.assertTrue(processor.currentKeys.contains("test-key-1"));
        Assert.assertEquals(4L, processor.currentTimestamp);
    }

    @Test
    public void testPersistState() throws Exception {
        Mockito.reset(storage);
        final ListGCSBucket processor = getProcessor();
        final TestRunner runner = AbstractGCSTest.buildNewRunner(processor);
        addRequiredPropertiesToRunner(runner);
        runner.assertValid();
        Assert.assertEquals("Cluster StateMap should be fresh (version -1L)", (-1L), runner.getProcessContext().getStateManager().getState(CLUSTER).getVersion());
        processor.currentKeys = ImmutableSet.of("test-key-0", "test-key-1");
        processor.currentTimestamp = 4L;
        processor.persistState(runner.getProcessContext());
        final StateMap stateMap = runner.getStateManager().getState(CLUSTER);
        Assert.assertEquals("Cluster StateMap should have been written to", 1L, stateMap.getVersion());
        Assert.assertEquals(ImmutableMap.of(CURRENT_TIMESTAMP, String.valueOf(4L), ((CURRENT_KEY_PREFIX) + "0"), "test-key-0", ((CURRENT_KEY_PREFIX) + "1"), "test-key-1"), stateMap.toMap());
    }

    @Test
    public void testFailedPersistState() throws Exception {
        Mockito.reset(storage);
        final ListGCSBucket processor = getProcessor();
        final TestRunner runner = AbstractGCSTest.buildNewRunner(processor);
        addRequiredPropertiesToRunner(runner);
        runner.assertValid();
        runner.getStateManager().setFailOnStateSet(CLUSTER, true);
        processor.currentKeys = ImmutableSet.of("test-key-0", "test-key-1");
        processor.currentTimestamp = 4L;
        Assert.assertTrue(runner.getLogger().getErrorMessages().isEmpty());
        processor.persistState(runner.getProcessContext());
        // The method should have caught the error and reported it to the logger.
        final List<LogMessage> logMessages = runner.getLogger().getErrorMessages();
        Assert.assertFalse(logMessages.isEmpty());
        Assert.assertEquals(1, logMessages.size());
        // We could do more specific things like check the contents of the LogMessage,
        // but that seems too nitpicky.
    }

    @Mock
    Page<Blob> mockBlobPages;

    @Test
    public void testSuccessfulList() throws Exception {
        Mockito.reset(storage, mockBlobPages);
        final ListGCSBucket processor = getProcessor();
        final TestRunner runner = AbstractGCSTest.buildNewRunner(processor);
        addRequiredPropertiesToRunner(runner);
        runner.assertValid();
        final Iterable<Blob> mockList = ImmutableList.of(buildMockBlob("blob-bucket-1", "blob-key-1", 2L), buildMockBlob("blob-bucket-2", "blob-key-2", 3L));
        Mockito.when(mockBlobPages.getValues()).thenReturn(mockList);
        Mockito.when(mockBlobPages.getNextPage()).thenReturn(null);
        Mockito.when(storage.list(ArgumentMatchers.anyString(), ArgumentMatchers.any(BlobListOption[].class))).thenReturn(mockBlobPages);
        runner.enqueue("test");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS);
        runner.assertTransferCount(REL_SUCCESS, 2);
        final List<MockFlowFile> successes = runner.getFlowFilesForRelationship(REL_SUCCESS);
        MockFlowFile flowFile = successes.get(0);
        Assert.assertEquals("blob-bucket-1", flowFile.getAttribute(StorageAttributes.BUCKET_ATTR));
        Assert.assertEquals("blob-key-1", flowFile.getAttribute(StorageAttributes.KEY_ATTR));
        Assert.assertEquals("2", flowFile.getAttribute(StorageAttributes.UPDATE_TIME_ATTR));
        flowFile = successes.get(1);
        Assert.assertEquals("blob-bucket-2", flowFile.getAttribute(StorageAttributes.BUCKET_ATTR));
        Assert.assertEquals("blob-key-2", flowFile.getAttribute(StorageAttributes.KEY_ATTR));
        Assert.assertEquals("3", flowFile.getAttribute(StorageAttributes.UPDATE_TIME_ATTR));
        Assert.assertEquals(3L, processor.currentTimestamp);
        Assert.assertEquals(ImmutableSet.of("blob-key-2"), processor.currentKeys);
    }

    @Test
    public void testOldValues() throws Exception {
        Mockito.reset(storage, mockBlobPages);
        final ListGCSBucket processor = getProcessor();
        final TestRunner runner = AbstractGCSTest.buildNewRunner(processor);
        addRequiredPropertiesToRunner(runner);
        runner.assertValid();
        final Iterable<Blob> mockList = ImmutableList.of(buildMockBlob("blob-bucket-1", "blob-key-1", 2L));
        Mockito.when(mockBlobPages.getValues()).thenReturn(mockList);
        Mockito.when(mockBlobPages.getNextPage()).thenReturn(null);
        Mockito.when(storage.list(ArgumentMatchers.anyString(), ArgumentMatchers.any(BlobListOption[].class))).thenReturn(mockBlobPages);
        runner.enqueue("test");
        runner.enqueue("test2");
        runner.run(2);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS);
        runner.assertTransferCount(REL_SUCCESS, 1);
        Assert.assertEquals("blob-key-1", runner.getStateManager().getState(CLUSTER).get(((CURRENT_KEY_PREFIX) + "0")));
        Assert.assertEquals("2", runner.getStateManager().getState(CLUSTER).get(CURRENT_TIMESTAMP));
    }

    @Test
    public void testEmptyList() throws Exception {
        Mockito.reset(storage, mockBlobPages);
        final ListGCSBucket processor = getProcessor();
        final TestRunner runner = AbstractGCSTest.buildNewRunner(processor);
        addRequiredPropertiesToRunner(runner);
        runner.assertValid();
        final Iterable<Blob> mockList = ImmutableList.of();
        Mockito.when(mockBlobPages.getValues()).thenReturn(mockList);
        Mockito.when(mockBlobPages.getNextPage()).thenReturn(null);
        Mockito.when(storage.list(ArgumentMatchers.anyString(), ArgumentMatchers.any(BlobListOption[].class))).thenReturn(mockBlobPages);
        runner.enqueue("test");
        runner.run();
        runner.assertTransferCount(REL_SUCCESS, 0);
        Assert.assertEquals("No state should be persisted on an empty return", (-1L), runner.getStateManager().getState(CLUSTER).getVersion());
    }

    @Test
    public void testAttributesSet() throws Exception {
        Mockito.reset(storage, mockBlobPages);
        final ListGCSBucket processor = getProcessor();
        final TestRunner runner = AbstractGCSTest.buildNewRunner(processor);
        addRequiredPropertiesToRunner(runner);
        runner.assertValid();
        final Blob blob = buildMockBlob("test-bucket-1", "test-key-1", 2L);
        Mockito.when(blob.getSize()).thenReturn(ListGCSBucketTest.SIZE);
        Mockito.when(blob.getCacheControl()).thenReturn(ListGCSBucketTest.CACHE_CONTROL);
        Mockito.when(blob.getComponentCount()).thenReturn(ListGCSBucketTest.COMPONENT_COUNT);
        Mockito.when(blob.getContentEncoding()).thenReturn(ListGCSBucketTest.CONTENT_ENCODING);
        Mockito.when(blob.getContentLanguage()).thenReturn(ListGCSBucketTest.CONTENT_LANGUAGE);
        Mockito.when(blob.getContentType()).thenReturn(ListGCSBucketTest.CONTENT_TYPE);
        Mockito.when(blob.getCrc32c()).thenReturn(ListGCSBucketTest.CRC32C);
        final BlobInfo.CustomerEncryption mockEncryption = Mockito.mock(CustomerEncryption.class);
        Mockito.when(mockEncryption.getEncryptionAlgorithm()).thenReturn(ListGCSBucketTest.ENCRYPTION);
        Mockito.when(mockEncryption.getKeySha256()).thenReturn(ListGCSBucketTest.ENCRYPTION_SHA256);
        Mockito.when(blob.getCustomerEncryption()).thenReturn(mockEncryption);
        Mockito.when(blob.getEtag()).thenReturn(ListGCSBucketTest.ETAG);
        Mockito.when(blob.getGeneratedId()).thenReturn(ListGCSBucketTest.GENERATED_ID);
        Mockito.when(blob.getGeneration()).thenReturn(ListGCSBucketTest.GENERATION);
        Mockito.when(blob.getMd5()).thenReturn(ListGCSBucketTest.MD5);
        Mockito.when(blob.getMediaLink()).thenReturn(ListGCSBucketTest.MEDIA_LINK);
        Mockito.when(blob.getMetageneration()).thenReturn(ListGCSBucketTest.METAGENERATION);
        Mockito.when(blob.getSelfLink()).thenReturn(ListGCSBucketTest.URI);
        Mockito.when(blob.getContentDisposition()).thenReturn(ListGCSBucketTest.CONTENT_DISPOSITION);
        Mockito.when(blob.getCreateTime()).thenReturn(ListGCSBucketTest.CREATE_TIME);
        Mockito.when(blob.getUpdateTime()).thenReturn(ListGCSBucketTest.UPDATE_TIME);
        final Iterable<Blob> mockList = ImmutableList.of(blob);
        Mockito.when(mockBlobPages.getValues()).thenReturn(mockList);
        Mockito.when(mockBlobPages.getNextPage()).thenReturn(null);
        Mockito.when(storage.list(ArgumentMatchers.anyString(), ArgumentMatchers.any(BlobListOption[].class))).thenReturn(mockBlobPages);
        runner.enqueue("test");
        runner.run();
        runner.assertAllFlowFilesTransferred(FetchGCSObject.REL_SUCCESS);
        runner.assertTransferCount(FetchGCSObject.REL_SUCCESS, 1);
        final MockFlowFile flowFile = runner.getFlowFilesForRelationship(FetchGCSObject.REL_SUCCESS).get(0);
        Assert.assertEquals(ListGCSBucketTest.CACHE_CONTROL, flowFile.getAttribute(StorageAttributes.CACHE_CONTROL_ATTR));
        Assert.assertEquals(ListGCSBucketTest.COMPONENT_COUNT, Integer.valueOf(flowFile.getAttribute(StorageAttributes.COMPONENT_COUNT_ATTR)));
        Assert.assertEquals(ListGCSBucketTest.CONTENT_ENCODING, flowFile.getAttribute(StorageAttributes.CONTENT_ENCODING_ATTR));
        Assert.assertEquals(ListGCSBucketTest.CONTENT_LANGUAGE, flowFile.getAttribute(StorageAttributes.CONTENT_LANGUAGE_ATTR));
        Assert.assertEquals(ListGCSBucketTest.CONTENT_TYPE, flowFile.getAttribute(MIME_TYPE.key()));
        Assert.assertEquals(ListGCSBucketTest.CRC32C, flowFile.getAttribute(StorageAttributes.CRC32C_ATTR));
        Assert.assertEquals(ListGCSBucketTest.ENCRYPTION, flowFile.getAttribute(StorageAttributes.ENCRYPTION_ALGORITHM_ATTR));
        Assert.assertEquals(ListGCSBucketTest.ENCRYPTION_SHA256, flowFile.getAttribute(StorageAttributes.ENCRYPTION_SHA256_ATTR));
        Assert.assertEquals(ListGCSBucketTest.ETAG, flowFile.getAttribute(StorageAttributes.ETAG_ATTR));
        Assert.assertEquals(ListGCSBucketTest.GENERATED_ID, flowFile.getAttribute(StorageAttributes.GENERATED_ID_ATTR));
        Assert.assertEquals(ListGCSBucketTest.GENERATION, Long.valueOf(flowFile.getAttribute(StorageAttributes.GENERATION_ATTR)));
        Assert.assertEquals(ListGCSBucketTest.MD5, flowFile.getAttribute(StorageAttributes.MD5_ATTR));
        Assert.assertEquals(ListGCSBucketTest.MEDIA_LINK, flowFile.getAttribute(StorageAttributes.MEDIA_LINK_ATTR));
        Assert.assertEquals(ListGCSBucketTest.METAGENERATION, Long.valueOf(flowFile.getAttribute(StorageAttributes.METAGENERATION_ATTR)));
        Assert.assertEquals(ListGCSBucketTest.URI, flowFile.getAttribute(StorageAttributes.URI_ATTR));
        Assert.assertEquals(ListGCSBucketTest.CONTENT_DISPOSITION, flowFile.getAttribute(StorageAttributes.CONTENT_DISPOSITION_ATTR));
        Assert.assertEquals(ListGCSBucketTest.CREATE_TIME, Long.valueOf(flowFile.getAttribute(StorageAttributes.CREATE_TIME_ATTR)));
        Assert.assertEquals(ListGCSBucketTest.UPDATE_TIME, Long.valueOf(flowFile.getAttribute(StorageAttributes.UPDATE_TIME_ATTR)));
    }

    @Test
    public void testAclOwnerUser() throws Exception {
        Mockito.reset(storage, mockBlobPages);
        final ListGCSBucket processor = getProcessor();
        final TestRunner runner = AbstractGCSTest.buildNewRunner(processor);
        addRequiredPropertiesToRunner(runner);
        runner.assertValid();
        final Blob blob = buildMockBlob("test-bucket-1", "test-key-1", 2L);
        final Acl.User mockUser = Mockito.mock(User.class);
        Mockito.when(mockUser.getEmail()).thenReturn(ListGCSBucketTest.OWNER_USER_EMAIL);
        Mockito.when(blob.getOwner()).thenReturn(mockUser);
        final Iterable<Blob> mockList = ImmutableList.of(blob);
        Mockito.when(mockBlobPages.getValues()).thenReturn(mockList);
        Mockito.when(mockBlobPages.getNextPage()).thenReturn(null);
        Mockito.when(storage.list(ArgumentMatchers.anyString(), ArgumentMatchers.any(BlobListOption[].class))).thenReturn(mockBlobPages);
        runner.enqueue("test");
        runner.run();
        runner.assertAllFlowFilesTransferred(FetchGCSObject.REL_SUCCESS);
        runner.assertTransferCount(FetchGCSObject.REL_SUCCESS, 1);
        final MockFlowFile flowFile = runner.getFlowFilesForRelationship(FetchGCSObject.REL_SUCCESS).get(0);
        Assert.assertEquals(ListGCSBucketTest.OWNER_USER_EMAIL, flowFile.getAttribute(StorageAttributes.OWNER_ATTR));
        Assert.assertEquals("user", flowFile.getAttribute(StorageAttributes.OWNER_TYPE_ATTR));
    }

    @Test
    public void testAclOwnerGroup() throws Exception {
        Mockito.reset(storage, mockBlobPages);
        final ListGCSBucket processor = getProcessor();
        final TestRunner runner = AbstractGCSTest.buildNewRunner(processor);
        addRequiredPropertiesToRunner(runner);
        runner.assertValid();
        final Blob blob = buildMockBlob("test-bucket-1", "test-key-1", 2L);
        final Acl.Group mockGroup = Mockito.mock(Group.class);
        Mockito.when(mockGroup.getEmail()).thenReturn(ListGCSBucketTest.OWNER_GROUP_EMAIL);
        Mockito.when(blob.getOwner()).thenReturn(mockGroup);
        final Iterable<Blob> mockList = ImmutableList.of(blob);
        Mockito.when(mockBlobPages.getValues()).thenReturn(mockList);
        Mockito.when(mockBlobPages.getNextPage()).thenReturn(null);
        Mockito.when(storage.list(ArgumentMatchers.anyString(), ArgumentMatchers.any(BlobListOption[].class))).thenReturn(mockBlobPages);
        runner.enqueue("test");
        runner.run();
        runner.assertAllFlowFilesTransferred(FetchGCSObject.REL_SUCCESS);
        runner.assertTransferCount(FetchGCSObject.REL_SUCCESS, 1);
        final MockFlowFile flowFile = runner.getFlowFilesForRelationship(FetchGCSObject.REL_SUCCESS).get(0);
        Assert.assertEquals(ListGCSBucketTest.OWNER_GROUP_EMAIL, flowFile.getAttribute(StorageAttributes.OWNER_ATTR));
        Assert.assertEquals("group", flowFile.getAttribute(StorageAttributes.OWNER_TYPE_ATTR));
    }

    @Test
    public void testAclOwnerDomain() throws Exception {
        Mockito.reset(storage, mockBlobPages);
        final ListGCSBucket processor = getProcessor();
        final TestRunner runner = AbstractGCSTest.buildNewRunner(processor);
        addRequiredPropertiesToRunner(runner);
        runner.assertValid();
        final Blob blob = buildMockBlob("test-bucket-1", "test-key-1", 2L);
        final Acl.Domain mockDomain = Mockito.mock(Domain.class);
        Mockito.when(mockDomain.getDomain()).thenReturn(ListGCSBucketTest.OWNER_DOMAIN);
        Mockito.when(blob.getOwner()).thenReturn(mockDomain);
        final Iterable<Blob> mockList = ImmutableList.of(blob);
        Mockito.when(mockBlobPages.getValues()).thenReturn(mockList);
        Mockito.when(mockBlobPages.getNextPage()).thenReturn(null);
        Mockito.when(storage.list(ArgumentMatchers.anyString(), ArgumentMatchers.any(BlobListOption[].class))).thenReturn(mockBlobPages);
        runner.enqueue("test");
        runner.run();
        runner.assertAllFlowFilesTransferred(FetchGCSObject.REL_SUCCESS);
        runner.assertTransferCount(FetchGCSObject.REL_SUCCESS, 1);
        final MockFlowFile flowFile = runner.getFlowFilesForRelationship(FetchGCSObject.REL_SUCCESS).get(0);
        Assert.assertEquals(ListGCSBucketTest.OWNER_DOMAIN, flowFile.getAttribute(StorageAttributes.OWNER_ATTR));
        Assert.assertEquals("domain", flowFile.getAttribute(StorageAttributes.OWNER_TYPE_ATTR));
    }

    @Test
    public void testAclOwnerProject() throws Exception {
        Mockito.reset(storage, mockBlobPages);
        final ListGCSBucket processor = getProcessor();
        final TestRunner runner = AbstractGCSTest.buildNewRunner(processor);
        addRequiredPropertiesToRunner(runner);
        runner.assertValid();
        final Blob blob = buildMockBlob("test-bucket-1", "test-key-1", 2L);
        final Acl.Project mockProject = Mockito.mock(Project.class);
        Mockito.when(mockProject.getProjectId()).thenReturn(ListGCSBucketTest.OWNER_PROJECT_ID);
        Mockito.when(blob.getOwner()).thenReturn(mockProject);
        final Iterable<Blob> mockList = ImmutableList.of(blob);
        Mockito.when(mockBlobPages.getValues()).thenReturn(mockList);
        Mockito.when(mockBlobPages.getNextPage()).thenReturn(null);
        Mockito.when(storage.list(ArgumentMatchers.anyString(), ArgumentMatchers.any(BlobListOption[].class))).thenReturn(mockBlobPages);
        runner.enqueue("test");
        runner.run();
        runner.assertAllFlowFilesTransferred(FetchGCSObject.REL_SUCCESS);
        runner.assertTransferCount(FetchGCSObject.REL_SUCCESS, 1);
        final MockFlowFile flowFile = runner.getFlowFilesForRelationship(FetchGCSObject.REL_SUCCESS).get(0);
        Assert.assertEquals(ListGCSBucketTest.OWNER_PROJECT_ID, flowFile.getAttribute(StorageAttributes.OWNER_ATTR));
        Assert.assertEquals("project", flowFile.getAttribute(StorageAttributes.OWNER_TYPE_ATTR));
    }

    @Test
    public void testYieldOnBadStateRestore() throws Exception {
        Mockito.reset(storage, mockBlobPages);
        final ListGCSBucket processor = getProcessor();
        final TestRunner runner = AbstractGCSTest.buildNewRunner(processor);
        addRequiredPropertiesToRunner(runner);
        runner.assertValid();
        final Iterable<Blob> mockList = ImmutableList.of();
        Mockito.when(mockBlobPages.getValues()).thenReturn(mockList);
        Mockito.when(mockBlobPages.getNextPage()).thenReturn(null);
        Mockito.when(storage.list(ArgumentMatchers.anyString(), ArgumentMatchers.any(BlobListOption[].class))).thenReturn(mockBlobPages);
        runner.getStateManager().setFailOnStateGet(CLUSTER, true);
        runner.enqueue("test");
        runner.run();
        runner.assertTransferCount(REL_SUCCESS, 0);
        Assert.assertEquals(1, runner.getLogger().getErrorMessages().size());
    }

    @Test
    public void testListOptionsPrefix() throws Exception {
        Mockito.reset(storage, mockBlobPages);
        final ListGCSBucket processor = getProcessor();
        final TestRunner runner = AbstractGCSTest.buildNewRunner(processor);
        addRequiredPropertiesToRunner(runner);
        runner.setProperty(ListGCSBucket.PREFIX, ListGCSBucketTest.PREFIX);
        runner.assertValid();
        final Iterable<Blob> mockList = ImmutableList.of();
        Mockito.when(mockBlobPages.getValues()).thenReturn(mockList);
        Mockito.when(mockBlobPages.getNextPage()).thenReturn(null);
        Mockito.when(storage.list(ArgumentMatchers.anyString(), argumentCaptor.capture())).thenReturn(mockBlobPages);
        runner.enqueue("test");
        runner.run();
        Assert.assertEquals(BlobListOption.prefix(ListGCSBucketTest.PREFIX), argumentCaptor.getValue());
    }

    @Test
    public void testListOptionsVersions() throws Exception {
        Mockito.reset(storage, mockBlobPages);
        final ListGCSBucket processor = getProcessor();
        final TestRunner runner = AbstractGCSTest.buildNewRunner(processor);
        addRequiredPropertiesToRunner(runner);
        runner.setProperty(ListGCSBucket.USE_GENERATIONS, String.valueOf(ListGCSBucketTest.USE_GENERATIONS));
        runner.assertValid();
        final Iterable<Blob> mockList = ImmutableList.of();
        Mockito.when(mockBlobPages.getValues()).thenReturn(mockList);
        Mockito.when(mockBlobPages.getNextPage()).thenReturn(null);
        Mockito.when(storage.list(ArgumentMatchers.anyString(), argumentCaptor.capture())).thenReturn(mockBlobPages);
        runner.enqueue("test");
        runner.run();
        Storage.BlobListOption option = argumentCaptor.getValue();
        Assert.assertEquals(BlobListOption.versions(true), option);
    }
}

