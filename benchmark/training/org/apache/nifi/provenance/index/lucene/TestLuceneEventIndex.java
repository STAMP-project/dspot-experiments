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
package org.apache.nifi.provenance.index.lucene;


import EventAuthorizer.DENY_ALL;
import EventAuthorizer.GRANT_ALL;
import LineageNodeType.PROVENANCE_EVENT_NODE;
import ProvenanceEventType.FORK;
import ProvenanceEventType.JOIN;
import ProvenanceEventType.UNKNOWN;
import SearchableFields.FlowFileUUID;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import org.apache.nifi.authorization.AccessDeniedException;
import org.apache.nifi.authorization.user.NiFiUser;
import org.apache.nifi.events.EventReporter;
import org.apache.nifi.provenance.ProvenanceEventRecord;
import org.apache.nifi.provenance.ProvenanceEventType;
import org.apache.nifi.provenance.RepositoryConfiguration;
import org.apache.nifi.provenance.StandardProvenanceEventRecord;
import org.apache.nifi.provenance.authorization.EventAuthorizer;
import org.apache.nifi.provenance.lineage.ComputeLineageSubmission;
import org.apache.nifi.provenance.lineage.LineageNode;
import org.apache.nifi.provenance.lineage.ProvenanceEventLineageNode;
import org.apache.nifi.provenance.lucene.IndexManager;
import org.apache.nifi.provenance.search.Query;
import org.apache.nifi.provenance.search.QueryResult;
import org.apache.nifi.provenance.search.QuerySubmission;
import org.apache.nifi.provenance.search.SearchTerms;
import org.apache.nifi.provenance.serialization.StorageSummary;
import org.apache.nifi.provenance.store.ArrayListEventStore;
import org.apache.nifi.provenance.store.EventStore;
import org.apache.nifi.provenance.store.StorageResult;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


public class TestLuceneEventIndex {
    private final AtomicLong idGenerator = new AtomicLong(0L);

    @Rule
    public TestName testName = new TestName();

    @Test(timeout = 60000)
    public void testGetMinimumIdToReindex() throws InterruptedException {
        Assume.assumeFalse(isWindowsEnvironment());
        final RepositoryConfiguration repoConfig = createConfig(1);
        repoConfig.setDesiredIndexSize(1L);
        final IndexManager indexManager = new org.apache.nifi.provenance.lucene.SimpleIndexManager(repoConfig);
        final ArrayListEventStore eventStore = new ArrayListEventStore();
        final LuceneEventIndex index = new LuceneEventIndex(repoConfig, indexManager, 20000, EventReporter.NO_OP);
        index.initialize(eventStore);
        for (int i = 0; i < 50000; i++) {
            final ProvenanceEventRecord event = createEvent("1234");
            final StorageResult storageResult = eventStore.addEvent(event);
            index.addEvents(storageResult.getStorageLocations());
        }
        while ((index.getMaxEventId("1")) < 40000L) {
            Thread.sleep(25);
        } 
        final long id = index.getMinimumEventIdToReindex("1");
        Assert.assertTrue((id >= 30000L));
    }

    @Test(timeout = 60000)
    public void testUnauthorizedEventsGetPlaceholdersForLineage() throws InterruptedException {
        Assume.assumeFalse(isWindowsEnvironment());
        final RepositoryConfiguration repoConfig = createConfig(1);
        repoConfig.setDesiredIndexSize(1L);
        final IndexManager indexManager = new org.apache.nifi.provenance.lucene.SimpleIndexManager(repoConfig);
        final ArrayListEventStore eventStore = new ArrayListEventStore();
        final LuceneEventIndex index = new LuceneEventIndex(repoConfig, indexManager, 3, EventReporter.NO_OP);
        index.initialize(eventStore);
        for (int i = 0; i < 3; i++) {
            final ProvenanceEventRecord event = createEvent("1234");
            final StorageResult storageResult = eventStore.addEvent(event);
            index.addEvents(storageResult.getStorageLocations());
        }
        final NiFiUser user = createUser();
        List<LineageNode> nodes = Collections.emptyList();
        while ((nodes.size()) < 3) {
            final ComputeLineageSubmission submission = index.submitLineageComputation(1L, user, DENY_ALL);
            Assert.assertTrue(submission.getResult().awaitCompletion(5, TimeUnit.SECONDS));
            nodes = submission.getResult().getNodes();
            Thread.sleep(25L);
        } 
        Assert.assertEquals(3, nodes.size());
        for (final LineageNode node : nodes) {
            Assert.assertEquals(PROVENANCE_EVENT_NODE, node.getNodeType());
            final ProvenanceEventLineageNode eventNode = ((ProvenanceEventLineageNode) (node));
            Assert.assertEquals(UNKNOWN, eventNode.getEventType());
        }
    }

    @Test(timeout = 60000)
    public void testUnauthorizedEventsGetPlaceholdersForExpandChildren() throws InterruptedException {
        Assume.assumeFalse(isWindowsEnvironment());
        final RepositoryConfiguration repoConfig = createConfig(1);
        repoConfig.setDesiredIndexSize(1L);
        final IndexManager indexManager = new org.apache.nifi.provenance.lucene.SimpleIndexManager(repoConfig);
        final ArrayListEventStore eventStore = new ArrayListEventStore();
        final LuceneEventIndex index = new LuceneEventIndex(repoConfig, indexManager, 3, EventReporter.NO_OP);
        index.initialize(eventStore);
        final ProvenanceEventRecord firstEvent = createEvent("4444");
        final Map<String, String> previousAttributes = new HashMap<>();
        previousAttributes.put("uuid", "4444");
        final Map<String, String> updatedAttributes = new HashMap<>();
        updatedAttributes.put("updated", "true");
        final ProvenanceEventRecord fork = new StandardProvenanceEventRecord.Builder().setEventType(FORK).setAttributes(previousAttributes, updatedAttributes).addChildFlowFile("1234").setComponentId("component-1").setComponentType("unit test").setEventId(idGenerator.getAndIncrement()).setEventTime(System.currentTimeMillis()).setFlowFileEntryDate(System.currentTimeMillis()).setFlowFileUUID("4444").setLineageStartDate(System.currentTimeMillis()).setCurrentContentClaim("container", "section", "unit-test-id", 0L, 1024L).build();
        index.addEvents(eventStore.addEvent(firstEvent).getStorageLocations());
        index.addEvents(eventStore.addEvent(fork).getStorageLocations());
        for (int i = 0; i < 3; i++) {
            final ProvenanceEventRecord event = createEvent("1234");
            final StorageResult storageResult = eventStore.addEvent(event);
            index.addEvents(storageResult.getStorageLocations());
        }
        final NiFiUser user = createUser();
        final EventAuthorizer allowForkEvents = new EventAuthorizer() {
            @Override
            public boolean isAuthorized(ProvenanceEventRecord event) {
                return (event.getEventType()) == (ProvenanceEventType.FORK);
            }

            @Override
            public void authorize(ProvenanceEventRecord event) throws AccessDeniedException {
            }
        };
        List<LineageNode> nodes = Collections.emptyList();
        while ((nodes.size()) < 5) {
            final ComputeLineageSubmission submission = index.submitExpandChildren(1L, user, allowForkEvents);
            Assert.assertTrue(submission.getResult().awaitCompletion(5, TimeUnit.SECONDS));
            nodes = submission.getResult().getNodes();
            Thread.sleep(25L);
        } 
        Assert.assertEquals(5, nodes.size());
        Assert.assertEquals(1L, nodes.stream().filter(( n) -> (n.getNodeType()) == LineageNodeType.FLOWFILE_NODE).count());
        Assert.assertEquals(4L, nodes.stream().filter(( n) -> (n.getNodeType()) == LineageNodeType.PROVENANCE_EVENT_NODE).count());
        final Map<ProvenanceEventType, List<LineageNode>> eventMap = nodes.stream().filter(( n) -> (n.getNodeType()) == LineageNodeType.PROVENANCE_EVENT_NODE).collect(Collectors.groupingBy(( n) -> ((ProvenanceEventLineageNode) (n)).getEventType()));
        Assert.assertEquals(2, eventMap.size());
        Assert.assertEquals(1, eventMap.get(FORK).size());
        Assert.assertEquals(3, eventMap.get(UNKNOWN).size());
    }

    @Test(timeout = 60000)
    public void testUnauthorizedEventsGetPlaceholdersForFindParents() throws InterruptedException {
        Assume.assumeFalse(isWindowsEnvironment());
        final RepositoryConfiguration repoConfig = createConfig(1);
        repoConfig.setDesiredIndexSize(1L);
        final IndexManager indexManager = new org.apache.nifi.provenance.lucene.SimpleIndexManager(repoConfig);
        final ArrayListEventStore eventStore = new ArrayListEventStore();
        final LuceneEventIndex index = new LuceneEventIndex(repoConfig, indexManager, 3, EventReporter.NO_OP);
        index.initialize(eventStore);
        final ProvenanceEventRecord firstEvent = createEvent("4444");
        final Map<String, String> previousAttributes = new HashMap<>();
        previousAttributes.put("uuid", "4444");
        final Map<String, String> updatedAttributes = new HashMap<>();
        updatedAttributes.put("updated", "true");
        final ProvenanceEventRecord join = new StandardProvenanceEventRecord.Builder().setEventType(JOIN).setAttributes(previousAttributes, updatedAttributes).addParentUuid("4444").addChildFlowFile("1234").setComponentId("component-1").setComponentType("unit test").setEventId(idGenerator.getAndIncrement()).setEventTime(System.currentTimeMillis()).setFlowFileEntryDate(System.currentTimeMillis()).setFlowFileUUID("1234").setLineageStartDate(System.currentTimeMillis()).setCurrentContentClaim("container", "section", "unit-test-id", 0L, 1024L).build();
        index.addEvents(eventStore.addEvent(firstEvent).getStorageLocations());
        index.addEvents(eventStore.addEvent(join).getStorageLocations());
        for (int i = 0; i < 3; i++) {
            final ProvenanceEventRecord event = createEvent("1234");
            final StorageResult storageResult = eventStore.addEvent(event);
            index.addEvents(storageResult.getStorageLocations());
        }
        final NiFiUser user = createUser();
        final EventAuthorizer allowJoinEvents = new EventAuthorizer() {
            @Override
            public boolean isAuthorized(ProvenanceEventRecord event) {
                return (event.getEventType()) == (ProvenanceEventType.JOIN);
            }

            @Override
            public void authorize(ProvenanceEventRecord event) throws AccessDeniedException {
            }
        };
        List<LineageNode> nodes = Collections.emptyList();
        while ((nodes.size()) < 2) {
            final ComputeLineageSubmission submission = index.submitExpandParents(1L, user, allowJoinEvents);
            Assert.assertTrue(submission.getResult().awaitCompletion(5, TimeUnit.SECONDS));
            nodes = submission.getResult().getNodes();
            Thread.sleep(25L);
        } 
        Assert.assertEquals(2, nodes.size());
        final Map<ProvenanceEventType, List<LineageNode>> eventMap = nodes.stream().filter(( n) -> (n.getNodeType()) == LineageNodeType.PROVENANCE_EVENT_NODE).collect(Collectors.groupingBy(( n) -> ((ProvenanceEventLineageNode) (n)).getEventType()));
        Assert.assertEquals(2, eventMap.size());
        Assert.assertEquals(1, eventMap.get(JOIN).size());
        Assert.assertEquals(1, eventMap.get(UNKNOWN).size());
        Assert.assertEquals("4444", eventMap.get(UNKNOWN).get(0).getFlowFileUuid());
    }

    @Test(timeout = 60000)
    public void testUnauthorizedEventsGetFilteredForQuery() throws InterruptedException {
        Assume.assumeFalse(isWindowsEnvironment());
        final RepositoryConfiguration repoConfig = createConfig(1);
        repoConfig.setDesiredIndexSize(1L);
        final IndexManager indexManager = new org.apache.nifi.provenance.lucene.SimpleIndexManager(repoConfig);
        final ArrayListEventStore eventStore = new ArrayListEventStore();
        final LuceneEventIndex index = new LuceneEventIndex(repoConfig, indexManager, 3, EventReporter.NO_OP);
        index.initialize(eventStore);
        for (int i = 0; i < 3; i++) {
            final ProvenanceEventRecord event = createEvent("1234");
            final StorageResult storageResult = eventStore.addEvent(event);
            index.addEvents(storageResult.getStorageLocations());
        }
        final Query query = new Query(UUID.randomUUID().toString());
        final EventAuthorizer authorizer = new EventAuthorizer() {
            @Override
            public boolean isAuthorized(ProvenanceEventRecord event) {
                return ((event.getEventId()) % 2) == 0;
            }

            @Override
            public void authorize(ProvenanceEventRecord event) throws AccessDeniedException {
                throw new AccessDeniedException();
            }
        };
        List<ProvenanceEventRecord> events = Collections.emptyList();
        while ((events.size()) < 2) {
            final QuerySubmission submission = index.submitQuery(query, authorizer, "unit test");
            Assert.assertTrue(submission.getResult().awaitCompletion(5, TimeUnit.SECONDS));
            events = submission.getResult().getMatchingEvents();
            Thread.sleep(25L);
        } 
        Assert.assertEquals(2, events.size());
    }

    @Test(timeout = 60000)
    public void testExpiration() throws IOException, InterruptedException {
        final RepositoryConfiguration repoConfig = createConfig(1);
        repoConfig.setDesiredIndexSize(1L);
        final IndexManager indexManager = new org.apache.nifi.provenance.lucene.SimpleIndexManager(repoConfig);
        final LuceneEventIndex index = new LuceneEventIndex(repoConfig, indexManager, 1, EventReporter.NO_OP);
        final List<ProvenanceEventRecord> events = new ArrayList<>();
        events.add(createEvent(500000L));
        events.add(createEvent());
        final EventStore eventStore = Mockito.mock(EventStore.class);
        Mockito.doAnswer(new Answer<List<ProvenanceEventRecord>>() {
            @Override
            public List<ProvenanceEventRecord> answer(final InvocationOnMock invocation) throws Throwable {
                final Long eventId = getArgumentAt(0, Long.class);
                Assert.assertEquals(0, eventId.longValue());
                Assert.assertEquals(1, invocation.getArgumentAt(1, Integer.class).intValue());
                return Collections.singletonList(events.get(0));
            }
        }).when(eventStore).getEvents(Mockito.anyLong(), Mockito.anyInt());
        index.initialize(eventStore);
        index.addEvent(events.get(0), createStorageSummary(events.get(0).getEventId()));
        // Add the first event to the index and wait for it to be indexed, since indexing is asynchronous.
        List<File> allDirectories = Collections.emptyList();
        while (allDirectories.isEmpty()) {
            allDirectories = index.getDirectoryManager().getDirectories(null, null);
        } 
        events.remove(0);// Remove the first event from the store

        index.performMaintenance();
        Assert.assertEquals(1, index.getDirectoryManager().getDirectories(null, null).size());
    }

    @Test(timeout = 60000)
    public void addThenQueryWithEmptyQuery() throws InterruptedException {
        Assume.assumeFalse(isWindowsEnvironment());
        final RepositoryConfiguration repoConfig = createConfig();
        final IndexManager indexManager = new org.apache.nifi.provenance.lucene.SimpleIndexManager(repoConfig);
        final LuceneEventIndex index = new LuceneEventIndex(repoConfig, indexManager, 1, EventReporter.NO_OP);
        final ProvenanceEventRecord event = createEvent();
        index.addEvent(event, new StorageSummary(event.getEventId(), "1.prov", "1", 1, 2L, 2L));
        final Query query = new Query(UUID.randomUUID().toString());
        final ArrayListEventStore eventStore = new ArrayListEventStore();
        eventStore.addEvent(event);
        index.initialize(eventStore);
        // We don't know how long it will take for the event to be indexed, so keep querying until
        // we get a result. The test will timeout after 5 seconds if we've still not succeeded.
        List<ProvenanceEventRecord> matchingEvents = Collections.emptyList();
        while (matchingEvents.isEmpty()) {
            final QuerySubmission submission = index.submitQuery(query, GRANT_ALL, "unit test user");
            Assert.assertNotNull(submission);
            final QueryResult result = submission.getResult();
            Assert.assertNotNull(result);
            result.awaitCompletion(100, TimeUnit.MILLISECONDS);
            Assert.assertTrue(result.isFinished());
            Assert.assertNull(result.getError());
            matchingEvents = result.getMatchingEvents();
            Assert.assertNotNull(matchingEvents);
            Thread.sleep(100L);// avoid crushing the CPU

        } 
        Assert.assertEquals(1, matchingEvents.size());
        Assert.assertEquals(event, matchingEvents.get(0));
    }

    @Test(timeout = 50000)
    public void testQuerySpecificField() throws InterruptedException {
        final RepositoryConfiguration repoConfig = createConfig();
        final IndexManager indexManager = new org.apache.nifi.provenance.lucene.SimpleIndexManager(repoConfig);
        final LuceneEventIndex index = new LuceneEventIndex(repoConfig, indexManager, 2, EventReporter.NO_OP);
        // add 2 events, one of which we will query for.
        final ProvenanceEventRecord event = createEvent();
        index.addEvent(event, new StorageSummary(event.getEventId(), "1.prov", "1", 1, 2L, 2L));
        index.addEvent(createEvent(), new StorageSummary(2L, "1.prov", "1", 1, 2L, 2L));
        // Create a query that searches for the event with the FlowFile UUID equal to the first event's.
        final Query query = new Query(UUID.randomUUID().toString());
        query.addSearchTerm(SearchTerms.newSearchTerm(FlowFileUUID, event.getFlowFileUuid()));
        final ArrayListEventStore eventStore = new ArrayListEventStore();
        eventStore.addEvent(event);
        index.initialize(eventStore);
        // We don't know how long it will take for the event to be indexed, so keep querying until
        // we get a result. The test will timeout after 5 seconds if we've still not succeeded.
        List<ProvenanceEventRecord> matchingEvents = Collections.emptyList();
        while (matchingEvents.isEmpty()) {
            final QuerySubmission submission = index.submitQuery(query, GRANT_ALL, "unit test user");
            Assert.assertNotNull(submission);
            final QueryResult result = submission.getResult();
            Assert.assertNotNull(result);
            result.awaitCompletion(100, TimeUnit.MILLISECONDS);
            Assert.assertTrue(result.isFinished());
            Assert.assertNull(result.getError());
            matchingEvents = result.getMatchingEvents();
            Assert.assertNotNull(matchingEvents);
            Thread.sleep(100L);// avoid crushing the CPU

        } 
        Assert.assertEquals(1, matchingEvents.size());
        Assert.assertEquals(event, matchingEvents.get(0));
    }
}

