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
package org.apache.nifi.controller.repository;


import StandardFlowFileRecord.Builder;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.apache.nifi.connectable.Connectable;
import org.apache.nifi.connectable.Connection;
import org.apache.nifi.controller.queue.FlowFileQueue;
import org.apache.nifi.controller.queue.NopConnectionEventListener;
import org.apache.nifi.controller.queue.QueueSize;
import org.apache.nifi.controller.repository.claim.ContentClaim;
import org.apache.nifi.controller.repository.claim.ResourceClaim;
import org.apache.nifi.controller.repository.claim.ResourceClaimManager;
import org.apache.nifi.controller.repository.claim.StandardResourceClaimManager;
import org.apache.nifi.util.NiFiProperties;
import org.apache.nifi.util.file.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


@SuppressWarnings("deprecation")
public class TestWriteAheadFlowFileRepository {
    @Test
    public void testGetLocationSuffix() {
        Assert.assertEquals("/", WriteAheadFlowFileRepository.getLocationSuffix("/"));
        Assert.assertEquals("", WriteAheadFlowFileRepository.getLocationSuffix(""));
        Assert.assertEquals(null, WriteAheadFlowFileRepository.getLocationSuffix(null));
        Assert.assertEquals("test.txt", WriteAheadFlowFileRepository.getLocationSuffix("test.txt"));
        Assert.assertEquals("test.txt", WriteAheadFlowFileRepository.getLocationSuffix("/test.txt"));
        Assert.assertEquals("test.txt", WriteAheadFlowFileRepository.getLocationSuffix("/tmp/test.txt"));
        Assert.assertEquals("test.txt", WriteAheadFlowFileRepository.getLocationSuffix("//test.txt"));
        Assert.assertEquals("test.txt", WriteAheadFlowFileRepository.getLocationSuffix("/path/to/other/file/repository/test.txt"));
        Assert.assertEquals("test.txt", WriteAheadFlowFileRepository.getLocationSuffix("test.txt/"));
        Assert.assertEquals("test.txt", WriteAheadFlowFileRepository.getLocationSuffix("/path/to/test.txt/"));
    }

    @Test
    public void testSwapLocationsRestored() throws IOException {
        final Path path = Paths.get("target/test-swap-repo");
        if (Files.exists(path)) {
            FileUtils.deleteFile(path.toFile(), true);
        }
        final WriteAheadFlowFileRepository repo = new WriteAheadFlowFileRepository(NiFiProperties.createBasicNiFiProperties(null, null));
        repo.initialize(new StandardResourceClaimManager());
        final TestWriteAheadFlowFileRepository.TestQueueProvider queueProvider = new TestWriteAheadFlowFileRepository.TestQueueProvider();
        repo.loadFlowFiles(queueProvider);
        final Connection connection = Mockito.mock(Connection.class);
        Mockito.when(connection.getIdentifier()).thenReturn("1234");
        final FlowFileQueue queue = Mockito.mock(FlowFileQueue.class);
        Mockito.when(queue.getIdentifier()).thenReturn("1234");
        Mockito.when(connection.getFlowFileQueue()).thenReturn(queue);
        queueProvider.addConnection(connection);
        StandardFlowFileRecord.Builder ffBuilder = new StandardFlowFileRecord.Builder();
        ffBuilder.id(1L);
        ffBuilder.size(0L);
        final FlowFileRecord flowFileRecord = ffBuilder.build();
        final List<RepositoryRecord> records = new ArrayList<>();
        final StandardRepositoryRecord record = new StandardRepositoryRecord(queue, flowFileRecord, "swap123");
        record.setDestination(queue);
        records.add(record);
        repo.updateRepository(records);
        repo.close();
        // restore
        final WriteAheadFlowFileRepository repo2 = new WriteAheadFlowFileRepository(NiFiProperties.createBasicNiFiProperties(null, null));
        repo2.initialize(new StandardResourceClaimManager());
        repo2.loadFlowFiles(queueProvider);
        Assert.assertTrue(repo2.isValidSwapLocationSuffix("swap123"));
        Assert.assertFalse(repo2.isValidSwapLocationSuffix("other"));
        repo2.close();
    }

    @Test
    public void testSwapLocationsUpdatedOnRepoUpdate() throws IOException {
        final Path path = Paths.get("target/test-swap-repo");
        if (Files.exists(path)) {
            FileUtils.deleteFile(path.toFile(), true);
        }
        final WriteAheadFlowFileRepository repo = new WriteAheadFlowFileRepository(NiFiProperties.createBasicNiFiProperties(null, null));
        repo.initialize(new StandardResourceClaimManager());
        final TestWriteAheadFlowFileRepository.TestQueueProvider queueProvider = new TestWriteAheadFlowFileRepository.TestQueueProvider();
        repo.loadFlowFiles(queueProvider);
        final Connection connection = Mockito.mock(Connection.class);
        Mockito.when(connection.getIdentifier()).thenReturn("1234");
        final FlowFileQueue queue = Mockito.mock(FlowFileQueue.class);
        Mockito.when(queue.getIdentifier()).thenReturn("1234");
        Mockito.when(connection.getFlowFileQueue()).thenReturn(queue);
        queueProvider.addConnection(connection);
        StandardFlowFileRecord.Builder ffBuilder = new StandardFlowFileRecord.Builder();
        ffBuilder.id(1L);
        ffBuilder.size(0L);
        final FlowFileRecord flowFileRecord = ffBuilder.build();
        final List<RepositoryRecord> records = new ArrayList<>();
        final StandardRepositoryRecord record = new StandardRepositoryRecord(queue, flowFileRecord, "/tmp/swap123");
        record.setDestination(queue);
        records.add(record);
        Assert.assertFalse(repo.isValidSwapLocationSuffix("swap123"));
        repo.updateRepository(records);
        Assert.assertTrue(repo.isValidSwapLocationSuffix("swap123"));
        repo.close();
    }

    @Test
    public void testResourceClaimsIncremented() throws IOException {
        final ResourceClaimManager claimManager = new StandardResourceClaimManager();
        final TestWriteAheadFlowFileRepository.TestQueueProvider queueProvider = new TestWriteAheadFlowFileRepository.TestQueueProvider();
        final Connection connection = Mockito.mock(Connection.class);
        Mockito.when(connection.getIdentifier()).thenReturn("1234");
        Mockito.when(connection.getDestination()).thenReturn(Mockito.mock(Connectable.class));
        final FlowFileSwapManager swapMgr = new TestWriteAheadFlowFileRepository.MockFlowFileSwapManager();
        final FlowFileQueue queue = new org.apache.nifi.controller.queue.StandardFlowFileQueue("1234", new NopConnectionEventListener(), null, null, claimManager, null, swapMgr, null, 10000, 0L, "0 B");
        Mockito.when(connection.getFlowFileQueue()).thenReturn(queue);
        queueProvider.addConnection(connection);
        final ResourceClaim resourceClaim1 = claimManager.newResourceClaim("container", "section", "1", false, false);
        final ContentClaim claim1 = new org.apache.nifi.controller.repository.claim.StandardContentClaim(resourceClaim1, 0L);
        final ResourceClaim resourceClaim2 = claimManager.newResourceClaim("container", "section", "2", false, false);
        final ContentClaim claim2 = new org.apache.nifi.controller.repository.claim.StandardContentClaim(resourceClaim2, 0L);
        // Create a flowfile repo, update it once with a FlowFile that points to one resource claim. Then,
        // indicate that a FlowFile was swapped out. We should then be able to recover these FlowFiles and the
        // resource claims' counts should be updated for both the swapped out FlowFile and the non-swapped out FlowFile
        try (final WriteAheadFlowFileRepository repo = new WriteAheadFlowFileRepository(NiFiProperties.createBasicNiFiProperties(null, null))) {
            repo.initialize(claimManager);
            repo.loadFlowFiles(queueProvider);
            // Create a Repository Record that indicates that a FlowFile was created
            final FlowFileRecord flowFile1 = new StandardFlowFileRecord.Builder().id(1L).addAttribute("uuid", "11111111-1111-1111-1111-111111111111").contentClaim(claim1).build();
            final StandardRepositoryRecord rec1 = new StandardRepositoryRecord(queue);
            rec1.setWorking(flowFile1);
            rec1.setDestination(queue);
            // Create a Record that we can swap out
            final FlowFileRecord flowFile2 = new StandardFlowFileRecord.Builder().id(2L).addAttribute("uuid", "11111111-1111-1111-1111-111111111112").contentClaim(claim2).build();
            final StandardRepositoryRecord rec2 = new StandardRepositoryRecord(queue);
            rec2.setWorking(flowFile2);
            rec2.setDestination(queue);
            final List<RepositoryRecord> records = new ArrayList<>();
            records.add(rec1);
            records.add(rec2);
            repo.updateRepository(records);
            final String swapLocation = swapMgr.swapOut(Collections.singletonList(flowFile2), queue, null);
            repo.swapFlowFilesOut(Collections.singletonList(flowFile2), queue, swapLocation);
        }
        final ResourceClaimManager recoveryClaimManager = new StandardResourceClaimManager();
        try (final WriteAheadFlowFileRepository repo = new WriteAheadFlowFileRepository(NiFiProperties.createBasicNiFiProperties(null, null))) {
            repo.initialize(recoveryClaimManager);
            final long largestId = repo.loadFlowFiles(queueProvider);
            // largest ID known is 1 because this doesn't take into account the FlowFiles that have been swapped out
            Assert.assertEquals(1, largestId);
        }
        // resource claim 1 will have a single claimant count while resource claim 2 will have no claimant counts
        // because resource claim 2 is referenced only by flowfiles that are swapped out.
        Assert.assertEquals(1, recoveryClaimManager.getClaimantCount(resourceClaim1));
        Assert.assertEquals(0, recoveryClaimManager.getClaimantCount(resourceClaim2));
        final SwapSummary summary = queue.recoverSwappedFlowFiles();
        Assert.assertNotNull(summary);
        Assert.assertEquals(2, summary.getMaxFlowFileId().intValue());
        Assert.assertEquals(new QueueSize(1, 0L), summary.getQueueSize());
        final List<ResourceClaim> swappedOutClaims = summary.getResourceClaims();
        Assert.assertNotNull(swappedOutClaims);
        Assert.assertEquals(1, swappedOutClaims.size());
        Assert.assertEquals(claim2.getResourceClaim(), swappedOutClaims.get(0));
    }

    @Test
    public void testRestartWithOneRecord() throws IOException {
        final Path path = Paths.get("target/test-repo");
        if (Files.exists(path)) {
            FileUtils.deleteFile(path.toFile(), true);
        }
        final WriteAheadFlowFileRepository repo = new WriteAheadFlowFileRepository(NiFiProperties.createBasicNiFiProperties(null, null));
        repo.initialize(new StandardResourceClaimManager());
        final TestWriteAheadFlowFileRepository.TestQueueProvider queueProvider = new TestWriteAheadFlowFileRepository.TestQueueProvider();
        repo.loadFlowFiles(queueProvider);
        final List<FlowFileRecord> flowFileCollection = new ArrayList<>();
        final Connection connection = Mockito.mock(Connection.class);
        Mockito.when(connection.getIdentifier()).thenReturn("1234");
        final FlowFileQueue queue = Mockito.mock(FlowFileQueue.class);
        Mockito.when(queue.getIdentifier()).thenReturn("1234");
        Mockito.doAnswer(new Answer<Object>() {
            @Override
            public Object answer(final InvocationOnMock invocation) throws Throwable {
                flowFileCollection.add(((FlowFileRecord) (invocation.getArguments()[0])));
                return null;
            }
        }).when(queue).put(ArgumentMatchers.any(FlowFileRecord.class));
        Mockito.when(connection.getFlowFileQueue()).thenReturn(queue);
        queueProvider.addConnection(connection);
        StandardFlowFileRecord.Builder ffBuilder = new StandardFlowFileRecord.Builder();
        ffBuilder.id(1L);
        ffBuilder.addAttribute("abc", "xyz");
        ffBuilder.size(0L);
        final FlowFileRecord flowFileRecord = ffBuilder.build();
        final List<RepositoryRecord> records = new ArrayList<>();
        final StandardRepositoryRecord record = new StandardRepositoryRecord(null);
        record.setWorking(flowFileRecord);
        record.setDestination(connection.getFlowFileQueue());
        records.add(record);
        repo.updateRepository(records);
        // update to add new attribute
        ffBuilder = new StandardFlowFileRecord.Builder().fromFlowFile(flowFileRecord).addAttribute("hello", "world");
        final FlowFileRecord flowFileRecord2 = ffBuilder.build();
        record.setWorking(flowFileRecord2);
        repo.updateRepository(records);
        // update size but no attribute
        ffBuilder = new StandardFlowFileRecord.Builder().fromFlowFile(flowFileRecord2).size(40L);
        final FlowFileRecord flowFileRecord3 = ffBuilder.build();
        record.setWorking(flowFileRecord3);
        repo.updateRepository(records);
        repo.close();
        // restore
        final WriteAheadFlowFileRepository repo2 = new WriteAheadFlowFileRepository(NiFiProperties.createBasicNiFiProperties(null, null));
        repo2.initialize(new StandardResourceClaimManager());
        repo2.loadFlowFiles(queueProvider);
        Assert.assertEquals(1, flowFileCollection.size());
        final FlowFileRecord flowFile = flowFileCollection.get(0);
        Assert.assertEquals(1L, flowFile.getId());
        Assert.assertEquals("xyz", flowFile.getAttribute("abc"));
        Assert.assertEquals(40L, flowFile.getSize());
        Assert.assertEquals("world", flowFile.getAttribute("hello"));
        repo2.close();
    }

    private static class TestQueueProvider implements QueueProvider {
        private List<Connection> connectionList = new ArrayList<>();

        public void addConnection(final Connection connection) {
            this.connectionList.add(connection);
        }

        @Override
        public Collection<FlowFileQueue> getAllQueues() {
            final List<FlowFileQueue> queueList = new ArrayList<>();
            for (final Connection conn : connectionList) {
                queueList.add(conn.getFlowFileQueue());
            }
            return queueList;
        }
    }

    private static class MockFlowFileSwapManager implements FlowFileSwapManager {
        private final Map<FlowFileQueue, Map<String, List<FlowFileRecord>>> swappedRecords = new HashMap<>();

        @Override
        public void initialize(SwapManagerInitializationContext initializationContext) {
        }

        @Override
        public String swapOut(List<FlowFileRecord> flowFiles, FlowFileQueue flowFileQueue, final String partitionName) throws IOException {
            Map<String, List<FlowFileRecord>> swapMap = swappedRecords.get(flowFileQueue);
            if (swapMap == null) {
                swapMap = new HashMap();
                swappedRecords.put(flowFileQueue, swapMap);
            }
            final String location = UUID.randomUUID().toString();
            swapMap.put(location, new ArrayList(flowFiles));
            return location;
        }

        @Override
        public SwapContents peek(String swapLocation, FlowFileQueue flowFileQueue) throws IOException {
            Map<String, List<FlowFileRecord>> swapMap = swappedRecords.get(flowFileQueue);
            if (swapMap == null) {
                return null;
            }
            final List<FlowFileRecord> flowFiles = swapMap.get(swapLocation);
            final SwapSummary summary = getSwapSummary(swapLocation);
            return new org.apache.nifi.controller.swap.StandardSwapContents(summary, flowFiles);
        }

        @Override
        public SwapContents swapIn(String swapLocation, FlowFileQueue flowFileQueue) throws IOException {
            Map<String, List<FlowFileRecord>> swapMap = swappedRecords.get(flowFileQueue);
            if (swapMap == null) {
                return null;
            }
            final List<FlowFileRecord> flowFiles = swapMap.remove(swapLocation);
            final SwapSummary summary = getSwapSummary(swapLocation);
            return new org.apache.nifi.controller.swap.StandardSwapContents(summary, flowFiles);
        }

        @Override
        public List<String> recoverSwapLocations(FlowFileQueue flowFileQueue, final String partitionName) throws IOException {
            Map<String, List<FlowFileRecord>> swapMap = swappedRecords.get(flowFileQueue);
            if (swapMap == null) {
                return null;
            }
            return new ArrayList(swapMap.keySet());
        }

        @Override
        public SwapSummary getSwapSummary(String swapLocation) throws IOException {
            List<FlowFileRecord> records = null;
            for (final Map<String, List<FlowFileRecord>> swapMap : swappedRecords.values()) {
                records = swapMap.get(swapLocation);
                if (records != null) {
                    break;
                }
            }
            if (records == null) {
                return null;
            }
            final List<ResourceClaim> resourceClaims = new ArrayList<>();
            long size = 0L;
            Long maxId = null;
            for (final FlowFileRecord flowFile : records) {
                size += flowFile.getSize();
                if ((maxId == null) || ((flowFile.getId()) > maxId)) {
                    maxId = flowFile.getId();
                }
                final ContentClaim contentClaim = flowFile.getContentClaim();
                if (contentClaim != null) {
                    final ResourceClaim resourceClaim = contentClaim.getResourceClaim();
                    resourceClaims.add(resourceClaim);
                }
            }
            return new org.apache.nifi.controller.swap.StandardSwapSummary(new QueueSize(records.size(), size), maxId, resourceClaims);
        }

        @Override
        public void purge() {
            this.swappedRecords.clear();
        }

        @Override
        public Set<String> getSwappedPartitionNames(FlowFileQueue queue) throws IOException {
            return Collections.emptySet();
        }

        @Override
        public String changePartitionName(String swapLocation, String newPartitionName) throws IOException {
            return swapLocation;
        }
    }
}

