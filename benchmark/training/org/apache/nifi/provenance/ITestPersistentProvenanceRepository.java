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
package org.apache.nifi.provenance;


import IdentifierLookup.EMPTY;
import NiFiProperties.PROVENANCE_COMPRESS_ON_ROLLOVER;
import NiFiProperties.PROVENANCE_ROLLOVER_TIME;
import ProvenanceEventType.ATTRIBUTES_MODIFIED;
import ProvenanceEventType.CONTENT_MODIFIED;
import ProvenanceEventType.DROP;
import ProvenanceEventType.FORK;
import ProvenanceEventType.RECEIVE;
import ProvenanceEventType.UNKNOWN;
import SearchableFields.ComponentID;
import SearchableFields.Filename;
import SearchableFields.FlowFileUUID;
import SearchableFields.TransitURI;
import Severity.WARNING;
import StandardProvenanceEventRecord.Builder;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.zip.GZIPOutputStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.nifi.authorization.AccessDeniedException;
import org.apache.nifi.authorization.user.NiFiUser;
import org.apache.nifi.events.EventReporter;
import org.apache.nifi.provenance.index.EventIndexSearcher;
import org.apache.nifi.provenance.index.EventIndexWriter;
import org.apache.nifi.provenance.lineage.EventNode;
import org.apache.nifi.provenance.lineage.Lineage;
import org.apache.nifi.provenance.lineage.LineageEdge;
import org.apache.nifi.provenance.lineage.LineageNode;
import org.apache.nifi.provenance.lineage.LineageNodeType;
import org.apache.nifi.provenance.lucene.CachingIndexManager;
import org.apache.nifi.provenance.lucene.IndexManager;
import org.apache.nifi.provenance.lucene.IndexingAction;
import org.apache.nifi.provenance.search.Query;
import org.apache.nifi.provenance.search.QueryResult;
import org.apache.nifi.provenance.search.QuerySubmission;
import org.apache.nifi.provenance.search.SearchTerms;
import org.apache.nifi.provenance.search.SearchableField;
import org.apache.nifi.provenance.serialization.RecordReader;
import org.apache.nifi.provenance.serialization.RecordReaders;
import org.apache.nifi.provenance.serialization.RecordWriter;
import org.apache.nifi.reporting.Severity;
import org.apache.nifi.util.NiFiProperties;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static PersistentProvenanceRepository.MAX_INDEXING_FAILURE_COUNT;
import static ProvenanceEventType.ATTRIBUTES_MODIFIED;
import static ProvenanceEventType.DROP;
import static ProvenanceEventType.RECEIVE;


public class ITestPersistentProvenanceRepository {
    @Rule
    public TestName name = new TestName();

    @ClassRule
    public static TemporaryFolder tempFolder = new TemporaryFolder();

    private PersistentProvenanceRepository repo;

    private static RepositoryConfiguration config;

    public static final int DEFAULT_ROLLOVER_MILLIS = 2000;

    private EventReporter eventReporter;

    private List<ITestPersistentProvenanceRepository.ReportedEvent> reportedEvents = Collections.synchronizedList(new ArrayList<ITestPersistentProvenanceRepository.ReportedEvent>());

    private static int headerSize;

    private static int recordSize;

    private static int recordSize2;

    private NiFiProperties properties = new NiFiProperties() {
        @Override
        public String getProperty(String key) {
            if (key.equals(PROVENANCE_COMPRESS_ON_ROLLOVER)) {
                return "true";
            } else
                if (key.equals(PROVENANCE_ROLLOVER_TIME)) {
                    return "2000 millis";
                } else
                    if (key.equals(((NiFiProperties.PROVENANCE_REPO_DIRECTORY_PREFIX) + ".default"))) {
                        ITestPersistentProvenanceRepository.createConfiguration();
                        return ITestPersistentProvenanceRepository.config.getStorageDirectories().values().iterator().next().getAbsolutePath();
                    } else {
                        return null;
                    }


        }

        @Override
        public Set<String> getPropertyKeys() {
            return new java.util.HashSet(Arrays.asList(PROVENANCE_COMPRESS_ON_ROLLOVER, PROVENANCE_ROLLOVER_TIME, ((NiFiProperties.PROVENANCE_REPO_DIRECTORY_PREFIX) + ".default")));
        }
    };

    @Test
    public void constructorNoArgs() {
        ITestPersistentProvenanceRepository.TestablePersistentProvenanceRepository tppr = new ITestPersistentProvenanceRepository.TestablePersistentProvenanceRepository();
        Assert.assertEquals(0, tppr.getRolloverCheckMillis());
    }

    @Test
    public void constructorNiFiProperties() throws IOException {
        ITestPersistentProvenanceRepository.TestablePersistentProvenanceRepository tppr = new ITestPersistentProvenanceRepository.TestablePersistentProvenanceRepository(properties);
        Assert.assertEquals(10000, tppr.getRolloverCheckMillis());
    }

    @Test
    public void constructorConfig() throws IOException {
        RepositoryConfiguration configuration = RepositoryConfiguration.create(properties);
        new ITestPersistentProvenanceRepository.TestablePersistentProvenanceRepository(configuration, 20000);
    }

    @Test
    public void testAddAndRecover() throws IOException, InterruptedException {
        Assume.assumeFalse(isWindowsEnvironment());
        final RepositoryConfiguration config = ITestPersistentProvenanceRepository.createConfiguration();
        config.setMaxEventFileCapacity(1L);
        config.setMaxEventFileLife(1, TimeUnit.SECONDS);
        repo = new PersistentProvenanceRepository(config, ITestPersistentProvenanceRepository.DEFAULT_ROLLOVER_MILLIS);
        repo.initialize(getEventReporter(), null, null, EMPTY);
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("abc", "xyz");
        attributes.put("xyz", "abc");
        attributes.put("uuid", UUID.randomUUID().toString());
        final ProvenanceEventBuilder builder = new StandardProvenanceEventRecord.Builder();
        builder.setEventTime(System.currentTimeMillis());
        builder.setEventType(RECEIVE);
        builder.setTransitUri("nifi://unit-test");
        builder.fromFlowFile(TestUtil.createFlowFile(3L, 3000L, attributes));
        builder.setComponentId("1234");
        builder.setComponentType("dummy processor");
        final ProvenanceEventRecord record = builder.build();
        for (int i = 0; i < 10; i++) {
            repo.registerEvent(record);
        }
        Thread.sleep(1000L);
        repo.close();
        Thread.sleep(500L);// Give the repo time to shutdown (i.e., close all file handles, etc.)

        repo = new PersistentProvenanceRepository(config, ITestPersistentProvenanceRepository.DEFAULT_ROLLOVER_MILLIS);
        repo.initialize(getEventReporter(), null, null, EMPTY);
        final List<ProvenanceEventRecord> recoveredRecords = repo.getEvents(0L, 12);
        // just test however many were actually recovered since it is timing sensitive
        final int numRecovered = recoveredRecords.size();
        for (int i = 0; i < numRecovered; i++) {
            final ProvenanceEventRecord recovered = recoveredRecords.get(i);
            Assert.assertEquals(i, recovered.getEventId());
            Assert.assertEquals("nifi://unit-test", recovered.getTransitUri());
            Assert.assertEquals(RECEIVE, recovered.getEventType());
            Assert.assertEquals(attributes, recovered.getAttributes());
        }
    }

    @Test
    public void testAddToMultipleLogsAndRecover() throws IOException, InterruptedException {
        Assume.assumeFalse(isWindowsEnvironment());
        final List<SearchableField> searchableFields = new ArrayList<>();
        searchableFields.add(ComponentID);
        final RepositoryConfiguration config = ITestPersistentProvenanceRepository.createConfiguration();
        config.setMaxEventFileCapacity((1024L * 1024L));
        config.setMaxEventFileLife(2, TimeUnit.SECONDS);
        config.setSearchableFields(searchableFields);
        repo = new PersistentProvenanceRepository(config, ITestPersistentProvenanceRepository.DEFAULT_ROLLOVER_MILLIS);
        repo.initialize(getEventReporter(), null, null, EMPTY);
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("abc", "xyz");
        attributes.put("xyz", "abc");
        attributes.put("uuid", UUID.randomUUID().toString());
        final ProvenanceEventBuilder builder = new StandardProvenanceEventRecord.Builder();
        builder.setEventTime(System.currentTimeMillis());
        builder.setEventType(RECEIVE);
        builder.setTransitUri("nifi://unit-test");
        builder.fromFlowFile(TestUtil.createFlowFile(3L, 3000L, attributes));
        builder.setComponentId("1234");
        builder.setComponentType("dummy processor");
        final ProvenanceEventRecord record = builder.build();
        for (int i = 0; i < 10; i++) {
            repo.registerEvent(record);
        }
        builder.setComponentId("XXXX");// create a different component id so that we can make sure we query this record.

        attributes.put("uuid", "11111111-1111-1111-1111-111111111111");
        builder.fromFlowFile(TestUtil.createFlowFile(11L, 11L, attributes));
        repo.registerEvent(builder.build());
        repo.waitForRollover();
        Thread.sleep(500L);// Give the repo time to shutdown (i.e., close all file handles, etc.)

        // Create a new repo and add another record with component id XXXX so that we can ensure that it's added to a different
        // log file than the previous one.
        attributes.put("uuid", "22222222-2222-2222-2222-222222222222");
        builder.fromFlowFile(TestUtil.createFlowFile(11L, 11L, attributes));
        repo.registerEvent(builder.build());
        repo.waitForRollover();
        final Query query = new Query(UUID.randomUUID().toString());
        query.addSearchTerm(SearchTerms.newSearchTerm(ComponentID, "XXXX"));
        query.setMaxResults(100);
        final QueryResult result = repo.queryEvents(query, createUser());
        Assert.assertEquals(2, result.getMatchingEvents().size());
        for (final ProvenanceEventRecord match : result.getMatchingEvents()) {
            System.out.println(match);
        }
    }

    @Test
    public void testIndexOnRolloverWithImmenseAttribute() throws IOException {
        Assume.assumeFalse(isWindowsEnvironment());
        final RepositoryConfiguration config = ITestPersistentProvenanceRepository.createConfiguration();
        config.setMaxEventFileLife(500, TimeUnit.MILLISECONDS);
        config.setSearchableFields(new ArrayList(SearchableFields.getStandardFields()));
        config.setSearchableAttributes(SearchableFieldParser.extractSearchableFields("immense", false));
        repo = new PersistentProvenanceRepository(config, ITestPersistentProvenanceRepository.DEFAULT_ROLLOVER_MILLIS);
        repo.initialize(getEventReporter(), null, null, EMPTY);
        int immenseAttrSize = 33000;// must be greater than 32766 for a meaningful test

        StringBuilder immenseBldr = new StringBuilder(immenseAttrSize);
        for (int i = 0; i < immenseAttrSize; i++) {
            immenseBldr.append('0');
        }
        final String uuid = "00000000-0000-0000-0000-000000000000";
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("abc", "xyz");
        attributes.put("xyz", "abc");
        attributes.put("filename", ("file-" + uuid));
        attributes.put("immense", immenseBldr.toString());
        final ProvenanceEventBuilder builder = new StandardProvenanceEventRecord.Builder();
        builder.setEventTime(System.currentTimeMillis());
        builder.setEventType(RECEIVE);
        builder.setTransitUri("nifi://unit-test");
        builder.fromFlowFile(TestUtil.createFlowFile(3L, 3000L, attributes));
        builder.setComponentId("1234");
        builder.setComponentType("dummy processor");
        for (int i = 0; i < 10; i++) {
            attributes.put("uuid", ("00000000-0000-0000-0000-00000000000" + i));
            builder.fromFlowFile(TestUtil.createFlowFile(i, 3000L, attributes));
            repo.registerEvent(builder.build());
        }
        repo.waitForRollover();
        final Query query = new Query(UUID.randomUUID().toString());
        query.addSearchTerm(SearchTerms.newSearchTerm(SearchableFields.newSearchableAttribute("immense"), "000*"));
        query.setMaxResults(100);
        final QueryResult result = repo.queryEvents(query, createUser());
        Assert.assertEquals(10, result.getMatchingEvents().size());
    }

    @Test
    public void testIndexOnRolloverAndSubsequentSearch() throws IOException, InterruptedException, ParseException {
        Assume.assumeFalse(isWindowsEnvironment());
        final RepositoryConfiguration config = ITestPersistentProvenanceRepository.createConfiguration();
        config.setMaxEventFileLife(500, TimeUnit.MILLISECONDS);
        config.setSearchableFields(new ArrayList(SearchableFields.getStandardFields()));
        repo = new PersistentProvenanceRepository(config, ITestPersistentProvenanceRepository.DEFAULT_ROLLOVER_MILLIS);
        repo.initialize(getEventReporter(), null, null, EMPTY);
        final String uuid = "00000000-0000-0000-0000-000000000000";
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("abc", "xyz");
        attributes.put("xyz", "abc");
        attributes.put("filename", ("file-" + uuid));
        final ProvenanceEventBuilder builder = new StandardProvenanceEventRecord.Builder();
        builder.setEventTime(System.currentTimeMillis());
        builder.setEventType(RECEIVE);
        builder.setTransitUri("nifi://unit-test");
        builder.fromFlowFile(TestUtil.createFlowFile(3L, 3000L, attributes));
        builder.setComponentId("1234");
        builder.setComponentType("dummy processor");
        for (int i = 0; i < 10; i++) {
            attributes.put("uuid", ("00000000-0000-0000-0000-00000000000" + i));
            builder.fromFlowFile(TestUtil.createFlowFile(i, 3000L, attributes));
            repo.registerEvent(builder.build());
        }
        repo.waitForRollover();
        final Query query = new Query(UUID.randomUUID().toString());
        query.addSearchTerm(SearchTerms.newSearchTerm(FlowFileUUID, "000000*"));
        query.addSearchTerm(SearchTerms.newSearchTerm(Filename, "file-*"));
        query.addSearchTerm(SearchTerms.newSearchTerm(ComponentID, "12?4"));
        query.addSearchTerm(SearchTerms.newSearchTerm(TransitURI, "nifi://*"));
        query.setMaxResults(100);
        final QueryResult result = repo.queryEvents(query, createUser());
        Assert.assertEquals(10, result.getMatchingEvents().size());
        for (final ProvenanceEventRecord match : result.getMatchingEvents()) {
            System.out.println(match);
        }
    }

    @Test
    public void testCompressOnRollover() throws IOException, InterruptedException, ParseException {
        Assume.assumeFalse(isWindowsEnvironment());
        final RepositoryConfiguration config = ITestPersistentProvenanceRepository.createConfiguration();
        config.setMaxEventFileLife(500, TimeUnit.MILLISECONDS);
        config.setCompressOnRollover(true);
        repo = new PersistentProvenanceRepository(config, ITestPersistentProvenanceRepository.DEFAULT_ROLLOVER_MILLIS);
        repo.initialize(getEventReporter(), null, null, EMPTY);
        final String uuid = "00000000-0000-0000-0000-000000000000";
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("abc", "xyz");
        attributes.put("xyz", "abc");
        attributes.put("filename", ("file-" + uuid));
        final ProvenanceEventBuilder builder = new StandardProvenanceEventRecord.Builder();
        builder.setEventTime(System.currentTimeMillis());
        builder.setEventType(RECEIVE);
        builder.setTransitUri("nifi://unit-test");
        attributes.put("uuid", uuid);
        builder.fromFlowFile(TestUtil.createFlowFile(3L, 3000L, attributes));
        builder.setComponentId("1234");
        builder.setComponentType("dummy processor");
        for (int i = 0; i < 10; i++) {
            builder.fromFlowFile(TestUtil.createFlowFile(i, 3000L, attributes));
            repo.registerEvent(builder.build());
        }
        repo.waitForRollover();
        final File storageDir = config.getStorageDirectories().values().iterator().next();
        final File compressedLogFile = new File(storageDir, "0.prov.gz");
        Assert.assertTrue(compressedLogFile.exists());
    }

    @Test
    public void testIndexAndCompressOnRolloverAndSubsequentSearch() throws IOException, InterruptedException, ParseException {
        Assume.assumeFalse(isWindowsEnvironment());
        final RepositoryConfiguration config = ITestPersistentProvenanceRepository.createConfiguration();
        config.setMaxRecordLife(30, TimeUnit.SECONDS);
        config.setMaxStorageCapacity(((1024L * 1024L) * 10));
        config.setMaxEventFileLife(500, TimeUnit.MILLISECONDS);
        config.setMaxEventFileCapacity(((1024L * 1024L) * 10));
        config.setSearchableFields(new ArrayList(SearchableFields.getStandardFields()));
        repo = new PersistentProvenanceRepository(config, ITestPersistentProvenanceRepository.DEFAULT_ROLLOVER_MILLIS);
        repo.initialize(getEventReporter(), null, null, EMPTY);
        final String uuid = "10000000-0000-0000-0000-000000000000";
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("abc", "xyz");
        attributes.put("xyz", "abc");
        attributes.put("filename", ("file-" + uuid));
        final ProvenanceEventBuilder builder = new StandardProvenanceEventRecord.Builder();
        builder.setEventTime(System.currentTimeMillis());
        builder.setEventType(RECEIVE);
        builder.setTransitUri("nifi://unit-test");
        attributes.put("uuid", uuid);
        builder.fromFlowFile(TestUtil.createFlowFile(3L, 3000L, attributes));
        builder.setComponentId("1234");
        builder.setComponentType("dummy processor");
        for (int i = 0; i < 10; i++) {
            builder.fromFlowFile(TestUtil.createFlowFile(i, 3000L, attributes));
            attributes.put("uuid", ("00000000-0000-0000-0000-00000000000" + i));
            repo.registerEvent(builder.build());
        }
        repo.waitForRollover();
        final Query query = new Query(UUID.randomUUID().toString());
        // query.addSearchTerm(SearchTerms.newSearchTerm(SearchableFields.FlowFileUUID, "00000000-0000-0000-0000*"));
        query.addSearchTerm(SearchTerms.newSearchTerm(Filename, "file-*"));
        query.addSearchTerm(SearchTerms.newSearchTerm(ComponentID, "12?4"));
        query.addSearchTerm(SearchTerms.newSearchTerm(TransitURI, "nifi://*"));
        query.setMaxResults(100);
        final QueryResult result = repo.queryEvents(query, createUser());
        Assert.assertEquals(10, result.getMatchingEvents().size());
        for (final ProvenanceEventRecord match : result.getMatchingEvents()) {
            System.out.println(match);
        }
        Thread.sleep(2000L);
        config.setMaxStorageCapacity(100L);
        config.setMaxRecordLife(500, TimeUnit.MILLISECONDS);
        repo.purgeOldEvents();
        Thread.sleep(2000L);
        final QueryResult newRecordSet = repo.queryEvents(query, createUser());
        Assert.assertTrue(newRecordSet.getMatchingEvents().isEmpty());
    }

    @Test(timeout = 10000)
    public void testModifyIndexWhileSearching() throws IOException, InterruptedException, ParseException {
        Assume.assumeFalse(isWindowsEnvironment());
        final RepositoryConfiguration config = ITestPersistentProvenanceRepository.createConfiguration();
        config.setMaxRecordLife(30, TimeUnit.SECONDS);
        config.setMaxStorageCapacity(((1024L * 1024L) * 10));
        config.setMaxEventFileLife(500, TimeUnit.MILLISECONDS);
        config.setMaxEventFileCapacity(((1024L * 1024L) * 10));
        config.setSearchableFields(new ArrayList(SearchableFields.getStandardFields()));
        final CountDownLatch obtainIndexSearcherLatch = new CountDownLatch(2);
        repo = new PersistentProvenanceRepository(config, ITestPersistentProvenanceRepository.DEFAULT_ROLLOVER_MILLIS) {
            private CachingIndexManager wrappedManager = null;

            // Create an IndexManager that adds a delay before returning the Index Searcher.
            @Override
            protected synchronized CachingIndexManager getIndexManager() {
                if ((wrappedManager) == null) {
                    final IndexManager mgr = super.getIndexManager();
                    final Logger logger = LoggerFactory.getLogger("IndexManager");
                    wrappedManager = new CachingIndexManager() {
                        final AtomicInteger indexSearcherCount = new AtomicInteger(0);

                        @Override
                        public EventIndexSearcher borrowIndexSearcher(File indexDir) throws IOException {
                            final EventIndexSearcher searcher = mgr.borrowIndexSearcher(indexDir);
                            final int idx = indexSearcherCount.incrementAndGet();
                            obtainIndexSearcherLatch.countDown();
                            // The first searcher should sleep for 3 seconds. The second searcher should
                            // sleep for 5 seconds. This allows us to have two threads each obtain a Searcher
                            // and then have one of them finish searching and close the searcher if it's poisoned while the
                            // second thread is still holding the searcher
                            try {
                                if (idx == 1) {
                                    Thread.sleep(3000L);
                                } else {
                                    Thread.sleep(5000L);
                                }
                            } catch (InterruptedException e) {
                                throw new IOException("Interrupted", e);
                            }
                            logger.info("Releasing index searcher");
                            return searcher;
                        }

                        @Override
                        public EventIndexWriter borrowIndexWriter(File indexingDirectory) throws IOException {
                            return mgr.borrowIndexWriter(indexingDirectory);
                        }

                        @Override
                        public void close() throws IOException {
                            mgr.close();
                        }

                        @Override
                        public boolean removeIndex(File indexDirectory) {
                            mgr.removeIndex(indexDirectory);
                            return true;
                        }

                        @Override
                        public void returnIndexSearcher(EventIndexSearcher searcher) {
                            mgr.returnIndexSearcher(searcher);
                        }

                        @Override
                        public void returnIndexWriter(EventIndexWriter writer) {
                            mgr.returnIndexWriter(writer);
                        }
                    };
                }
                return wrappedManager;
            }
        };
        repo.initialize(getEventReporter(), null, null, EMPTY);
        final String uuid = "10000000-0000-0000-0000-000000000000";
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("abc", "xyz");
        attributes.put("xyz", "abc");
        attributes.put("filename", ("file-" + uuid));
        final ProvenanceEventBuilder builder = new StandardProvenanceEventRecord.Builder();
        builder.setEventTime(System.currentTimeMillis());
        builder.setEventType(RECEIVE);
        builder.setTransitUri("nifi://unit-test");
        attributes.put("uuid", uuid);
        builder.fromFlowFile(TestUtil.createFlowFile(3L, 3000L, attributes));
        builder.setComponentId("1234");
        builder.setComponentType("dummy processor");
        for (int i = 0; i < 10; i++) {
            builder.fromFlowFile(TestUtil.createFlowFile(i, 3000L, attributes));
            attributes.put("uuid", ("00000000-0000-0000-0000-00000000000" + i));
            repo.registerEvent(builder.build());
        }
        repo.waitForRollover();
        // Perform a query. This will ensure that an IndexSearcher is created and cached.
        final Query query = new Query(UUID.randomUUID().toString());
        query.addSearchTerm(SearchTerms.newSearchTerm(Filename, "file-*"));
        query.addSearchTerm(SearchTerms.newSearchTerm(ComponentID, "12?4"));
        query.addSearchTerm(SearchTerms.newSearchTerm(TransitURI, "nifi://*"));
        query.setMaxResults(100);
        // Run a query in a background thread. When this thread goes to obtain the IndexSearcher, it will have a 5 second delay.
        // That delay will occur as the main thread is updating the index. This should result in the search creating a new Index Reader
        // that can properly query the index.
        final int numThreads = 2;
        final CountDownLatch performSearchLatch = new CountDownLatch(numThreads);
        final Runnable searchRunnable = new Runnable() {
            @Override
            public void run() {
                QueryResult result;
                try {
                    result = repo.queryEvents(query, createUser());
                } catch (IOException e) {
                    e.printStackTrace();
                    Assert.fail(e.toString());
                    return;
                }
                System.out.println(("Finished search: " + result));
                performSearchLatch.countDown();
            }
        };
        // Kick off the searcher threads
        for (int i = 0; i < numThreads; i++) {
            final Thread searchThread = new Thread(searchRunnable);
            searchThread.start();
        }
        // Wait until we've obtained the Index Searchers before modifying the index.
        obtainIndexSearcherLatch.await();
        // add more events to the repo
        for (int i = 0; i < 10; i++) {
            builder.fromFlowFile(TestUtil.createFlowFile(i, 3000L, attributes));
            attributes.put("uuid", ("00000000-0000-0000-0000-00000000000" + i));
            repo.registerEvent(builder.build());
        }
        // Force a rollover to occur. This will modify the index.
        repo.rolloverWithLock(true);
        // Wait for the repository to roll over.
        repo.waitForRollover();
        // Wait for the searches to complete.
        performSearchLatch.await();
    }

    @Test
    public void testIndexAndCompressOnRolloverAndSubsequentSearchMultipleStorageDirs() throws IOException, InterruptedException, ParseException {
        Assume.assumeFalse(isWindowsEnvironment());
        final RepositoryConfiguration config = ITestPersistentProvenanceRepository.createConfiguration();
        config.addStorageDirectory("2", new File(("target/storage/" + (UUID.randomUUID().toString()))));
        config.setMaxRecordLife(30, TimeUnit.SECONDS);
        config.setMaxStorageCapacity((1024L * 1024L));
        config.setMaxEventFileLife(1, TimeUnit.SECONDS);
        config.setMaxEventFileCapacity((1024L * 1024L));
        config.setSearchableFields(new ArrayList(SearchableFields.getStandardFields()));
        repo = new PersistentProvenanceRepository(config, ITestPersistentProvenanceRepository.DEFAULT_ROLLOVER_MILLIS);
        repo.initialize(getEventReporter(), null, null, EMPTY);
        final String uuid = "00000000-0000-0000-0000-000000000000";
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("abc", "xyz");
        attributes.put("xyz", "abc");
        attributes.put("filename", ("file-" + uuid));
        final ProvenanceEventBuilder builder = new StandardProvenanceEventRecord.Builder();
        for (int j = 0; j < 3; j++) {
            attributes.put("iteration", String.valueOf(j));
            builder.setEventTime(System.currentTimeMillis());
            builder.setEventType(RECEIVE);
            builder.setTransitUri("nifi://unit-test");
            builder.setComponentId("1234");
            builder.setComponentType("dummy processor");
            builder.fromFlowFile(TestUtil.createFlowFile(3L, 3000L, attributes));
            for (int i = 0; i < 10; i++) {
                String uuidSuffix = String.valueOf((i + (j * 10)));
                if ((uuidSuffix.length()) < 2) {
                    uuidSuffix = "0" + uuidSuffix;
                }
                attributes.put("uuid", ("00000000-0000-0000-0000-0000000000" + uuidSuffix));
                builder.fromFlowFile(TestUtil.createFlowFile((i + (j * 10)), 3000L, attributes));
                repo.registerEvent(builder.build());
            }
            repo.waitForRollover();
        }
        final Query query = new Query(UUID.randomUUID().toString());
        query.addSearchTerm(SearchTerms.newSearchTerm(Filename, "file-*"));
        query.addSearchTerm(SearchTerms.newSearchTerm(ComponentID, "12?4"));
        query.addSearchTerm(SearchTerms.newSearchTerm(TransitURI, "nifi://*"));
        query.setMaxResults(100);
        final QuerySubmission submission = repo.submitQuery(query, createUser());
        while (!(submission.getResult().isFinished())) {
            Thread.sleep(100L);
        } 
        Assert.assertEquals(30, submission.getResult().getMatchingEvents().size());
        final Map<String, Integer> counts = new HashMap<>();
        for (final ProvenanceEventRecord match : submission.getResult().getMatchingEvents()) {
            System.out.println(match);
            final String index = match.getAttributes().get("iteration");
            Integer count = counts.get(index);
            if (count == null) {
                count = 0;
            }
            counts.put(index, (count + 1));
        }
        Assert.assertEquals(3, counts.size());
        Assert.assertEquals(10, counts.get("0").intValue());
        Assert.assertEquals(10, counts.get("1").intValue());
        Assert.assertEquals(10, counts.get("2").intValue());
        config.setMaxRecordLife(1, TimeUnit.MILLISECONDS);
        repo.purgeOldEvents();
        Thread.sleep(2000L);// purge is async. Give it time to do its job.

        query.setMaxResults(100);
        final QuerySubmission noResultSubmission = repo.submitQuery(query, createUser());
        while (!(noResultSubmission.getResult().isFinished())) {
            Thread.sleep(10L);
        } 
        Assert.assertEquals(0, noResultSubmission.getResult().getTotalHitCount());
    }

    @Test
    public void testIndexAndCompressOnRolloverAndSubsequentEmptySearch() throws IOException, InterruptedException, ParseException {
        Assume.assumeFalse(isWindowsEnvironment());
        final RepositoryConfiguration config = ITestPersistentProvenanceRepository.createConfiguration();
        config.setMaxRecordLife(30, TimeUnit.SECONDS);
        config.setMaxStorageCapacity((1024L * 1024L));
        config.setMaxEventFileLife(500, TimeUnit.MILLISECONDS);
        config.setMaxEventFileCapacity((1024L * 1024L));
        config.setSearchableFields(new ArrayList(SearchableFields.getStandardFields()));
        repo = new PersistentProvenanceRepository(config, ITestPersistentProvenanceRepository.DEFAULT_ROLLOVER_MILLIS);
        repo.initialize(getEventReporter(), null, null, EMPTY);
        final String uuid = "00000000-0000-0000-0000-000000000000";
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("abc", "xyz");
        attributes.put("xyz", "abc");
        attributes.put("filename", ("file-" + uuid));
        final ProvenanceEventBuilder builder = new StandardProvenanceEventRecord.Builder();
        builder.setEventTime(System.currentTimeMillis());
        builder.setEventType(RECEIVE);
        builder.setTransitUri("nifi://unit-test");
        attributes.put("uuid", uuid);
        builder.fromFlowFile(TestUtil.createFlowFile(3L, 3000L, attributes));
        builder.setComponentId("1234");
        builder.setComponentType("dummy processor");
        for (int i = 0; i < 10; i++) {
            builder.fromFlowFile(TestUtil.createFlowFile(i, 3000L, attributes));
            repo.registerEvent(builder.build());
        }
        // Give time for rollover to happen
        repo.waitForRollover();
        final Query query = new Query(UUID.randomUUID().toString());
        query.setMaxResults(100);
        final QueryResult result = repo.queryEvents(query, createUser());
        Assert.assertEquals(10, result.getMatchingEvents().size());
        for (final ProvenanceEventRecord match : result.getMatchingEvents()) {
            System.out.println(match);
        }
        Thread.sleep(2000L);
        config.setMaxStorageCapacity(100L);
        config.setMaxRecordLife(500, TimeUnit.MILLISECONDS);
        repo.purgeOldEvents();
        Thread.sleep(1000L);
        final QueryResult newRecordSet = repo.queryEvents(query, createUser());
        Assert.assertTrue(newRecordSet.getMatchingEvents().isEmpty());
    }

    @Test
    public void testLineageReceiveDrop() throws IOException, InterruptedException, ParseException {
        Assume.assumeFalse(isWindowsEnvironment());
        final RepositoryConfiguration config = ITestPersistentProvenanceRepository.createConfiguration();
        config.setMaxRecordLife(3, TimeUnit.SECONDS);
        config.setMaxStorageCapacity((1024L * 1024L));
        config.setMaxEventFileLife(500, TimeUnit.MILLISECONDS);
        config.setMaxEventFileCapacity((1024L * 1024L));
        config.setSearchableFields(new ArrayList(SearchableFields.getStandardFields()));
        repo = new PersistentProvenanceRepository(config, ITestPersistentProvenanceRepository.DEFAULT_ROLLOVER_MILLIS);
        repo.initialize(getEventReporter(), null, null, EMPTY);
        final String uuid = "00000000-0000-0000-0000-000000000001";
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("abc", "xyz");
        attributes.put("uuid", uuid);
        attributes.put("filename", ("file-" + uuid));
        final ProvenanceEventBuilder builder = new StandardProvenanceEventRecord.Builder();
        builder.setEventTime(System.currentTimeMillis());
        builder.setEventType(RECEIVE);
        builder.setTransitUri("nifi://unit-test");
        attributes.put("uuid", uuid);
        builder.fromFlowFile(TestUtil.createFlowFile(3L, 3000L, attributes));
        builder.setComponentId("1234");
        builder.setComponentType("dummy processor");
        repo.registerEvent(builder.build());
        builder.setEventTime(((System.currentTimeMillis()) + 1));
        builder.setEventType(DROP);
        builder.setTransitUri(null);
        repo.registerEvent(builder.build());
        repo.waitForRollover();
        final Lineage lineage = repo.computeLineage(uuid, createUser());
        Assert.assertNotNull(lineage);
        // Nodes should consist of a RECEIVE followed by FlowFileNode, followed by a DROP
        final List<LineageNode> nodes = lineage.getNodes();
        final List<LineageEdge> edges = lineage.getEdges();
        Assert.assertEquals(3, nodes.size());
        for (final LineageEdge edge : edges) {
            if ((edge.getSource().getNodeType()) == (LineageNodeType.FLOWFILE_NODE)) {
                Assert.assertTrue(((edge.getDestination().getNodeType()) == (LineageNodeType.PROVENANCE_EVENT_NODE)));
                Assert.assertTrue(((getEventType()) == (DROP)));
            } else {
                Assert.assertTrue(((getEventType()) == (RECEIVE)));
                Assert.assertTrue(((edge.getDestination().getNodeType()) == (LineageNodeType.FLOWFILE_NODE)));
            }
        }
    }

    @Test
    public void testLineageReceiveDropAsync() throws IOException, InterruptedException, ParseException {
        Assume.assumeFalse(isWindowsEnvironment());
        final RepositoryConfiguration config = ITestPersistentProvenanceRepository.createConfiguration();
        config.setMaxRecordLife(3, TimeUnit.SECONDS);
        config.setMaxStorageCapacity((1024L * 1024L));
        config.setMaxEventFileLife(500, TimeUnit.MILLISECONDS);
        config.setMaxEventFileCapacity((1024L * 1024L));
        config.setSearchableFields(new ArrayList(SearchableFields.getStandardFields()));
        repo = new PersistentProvenanceRepository(config, ITestPersistentProvenanceRepository.DEFAULT_ROLLOVER_MILLIS);
        repo.initialize(getEventReporter(), null, null, EMPTY);
        final String uuid = "00000000-0000-0000-0000-000000000001";
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("abc", "xyz");
        attributes.put("uuid", uuid);
        attributes.put("filename", ("file-" + uuid));
        final ProvenanceEventBuilder builder = new StandardProvenanceEventRecord.Builder();
        builder.setEventTime(System.currentTimeMillis());
        builder.setEventType(RECEIVE);
        builder.setTransitUri("nifi://unit-test");
        attributes.put("uuid", uuid);
        builder.fromFlowFile(TestUtil.createFlowFile(3L, 3000L, attributes));
        builder.setComponentId("1234");
        builder.setComponentType("dummy processor");
        repo.registerEvent(builder.build());
        builder.setEventTime(((System.currentTimeMillis()) + 1));
        builder.setEventType(DROP);
        builder.setTransitUri(null);
        repo.registerEvent(builder.build());
        repo.waitForRollover();
        final AsyncLineageSubmission submission = repo.submitLineageComputation(uuid, createUser());
        while (!(submission.getResult().isFinished())) {
            Thread.sleep(100L);
        } 
        Assert.assertNotNull(submission);
        // Nodes should consist of a RECEIVE followed by FlowFileNode, followed by a DROP
        final List<LineageNode> nodes = submission.getResult().getNodes();
        final List<LineageEdge> edges = submission.getResult().getEdges();
        Assert.assertEquals(3, nodes.size());
        for (final LineageEdge edge : edges) {
            if ((edge.getSource().getNodeType()) == (LineageNodeType.FLOWFILE_NODE)) {
                Assert.assertTrue(((edge.getDestination().getNodeType()) == (LineageNodeType.PROVENANCE_EVENT_NODE)));
                Assert.assertTrue(((getEventType()) == (DROP)));
            } else {
                Assert.assertTrue(((getEventType()) == (RECEIVE)));
                Assert.assertTrue(((edge.getDestination().getNodeType()) == (LineageNodeType.FLOWFILE_NODE)));
            }
        }
    }

    @Test
    public void testLineageManyToOneSpawn() throws IOException, InterruptedException, ParseException {
        Assume.assumeFalse(isWindowsEnvironment());
        final RepositoryConfiguration config = ITestPersistentProvenanceRepository.createConfiguration();
        config.setMaxRecordLife(3, TimeUnit.SECONDS);
        config.setMaxStorageCapacity((1024L * 1024L));
        config.setMaxEventFileLife(500, TimeUnit.MILLISECONDS);
        config.setMaxEventFileCapacity((1024L * 1024L));
        config.setSearchableFields(new ArrayList(SearchableFields.getStandardFields()));
        repo = new PersistentProvenanceRepository(config, ITestPersistentProvenanceRepository.DEFAULT_ROLLOVER_MILLIS);
        repo.initialize(getEventReporter(), null, null, EMPTY);
        final String childId = "00000000-0000-0000-0000-000000000000";
        final String parentId1 = "00000000-0000-0000-0001-000000000001";
        final String parentId2 = "00000000-0000-0000-0001-000000000002";
        final String parentId3 = "00000000-0000-0000-0001-000000000003";
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("abc", "xyz");
        attributes.put("uuid", childId);
        attributes.put("filename", ("file-" + childId));
        final StandardProvenanceEventRecord.Builder builder = new StandardProvenanceEventRecord.Builder();
        builder.setEventTime(System.currentTimeMillis());
        builder.setEventType(FORK);
        attributes.put("uuid", childId);
        builder.fromFlowFile(TestUtil.createFlowFile(3L, 3000L, attributes));
        builder.setComponentId("1234");
        builder.setComponentType("dummy processor");
        builder.addChildUuid(childId);
        builder.addParentUuid(parentId1);
        builder.addParentUuid(parentId2);
        builder.addParentUuid(parentId3);
        repo.registerEvent(builder.build());
        repo.waitForRollover();
        final Lineage lineage = repo.computeLineage(childId, createUser());
        Assert.assertNotNull(lineage);
        // these are not necessarily accurate asserts....
        final List<LineageNode> nodes = lineage.getNodes();
        final List<LineageEdge> edges = lineage.getEdges();
        Assert.assertEquals(2, nodes.size());
        Assert.assertEquals(1, edges.size());
    }

    @Test
    public void testLineageManyToOneSpawnAsync() throws IOException, InterruptedException, ParseException {
        Assume.assumeFalse(isWindowsEnvironment());
        final RepositoryConfiguration config = ITestPersistentProvenanceRepository.createConfiguration();
        config.setMaxRecordLife(3, TimeUnit.SECONDS);
        config.setMaxStorageCapacity((1024L * 1024L));
        config.setMaxEventFileLife(500, TimeUnit.MILLISECONDS);
        config.setMaxEventFileCapacity((1024L * 1024L));
        config.setSearchableFields(new ArrayList(SearchableFields.getStandardFields()));
        repo = new PersistentProvenanceRepository(config, ITestPersistentProvenanceRepository.DEFAULT_ROLLOVER_MILLIS);
        repo.initialize(getEventReporter(), null, null, EMPTY);
        final String childId = "00000000-0000-0000-0000-000000000000";
        final String parentId1 = "00000000-0000-0000-0001-000000000001";
        final String parentId2 = "00000000-0000-0000-0001-000000000002";
        final String parentId3 = "00000000-0000-0000-0001-000000000003";
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("abc", "xyz");
        attributes.put("uuid", childId);
        attributes.put("filename", ("file-" + childId));
        final StandardProvenanceEventRecord.Builder builder = new StandardProvenanceEventRecord.Builder();
        builder.setEventTime(System.currentTimeMillis());
        builder.setEventType(FORK);
        attributes.put("uuid", childId);
        builder.fromFlowFile(TestUtil.createFlowFile(3L, 3000L, attributes));
        builder.setComponentId("1234");
        builder.setComponentType("dummy processor");
        builder.addChildUuid(childId);
        builder.addParentUuid(parentId1);
        builder.addParentUuid(parentId2);
        builder.addParentUuid(parentId3);
        repo.registerEvent(builder.build());
        repo.waitForRollover();
        final AsyncLineageSubmission submission = repo.submitLineageComputation(childId, createUser());
        while (!(submission.getResult().isFinished())) {
            Thread.sleep(100L);
        } 
        // these are not accurate asserts....
        final List<LineageNode> nodes = submission.getResult().getNodes();
        final List<LineageEdge> edges = submission.getResult().getEdges();
        Assert.assertEquals(2, nodes.size());
        Assert.assertEquals(1, edges.size());
    }

    @Test
    public void testCorrectProvenanceEventIdOnRestore() throws IOException {
        Assume.assumeFalse(isWindowsEnvironment());
        final RepositoryConfiguration config = ITestPersistentProvenanceRepository.createConfiguration();
        config.setMaxEventFileLife(1, TimeUnit.SECONDS);
        repo = new PersistentProvenanceRepository(config, ITestPersistentProvenanceRepository.DEFAULT_ROLLOVER_MILLIS);
        repo.initialize(getEventReporter(), null, null, EMPTY);
        final String uuid = "00000000-0000-0000-0000-000000000000";
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("abc", "xyz");
        attributes.put("xyz", "abc");
        attributes.put("filename", ("file-" + uuid));
        final ProvenanceEventBuilder builder = new StandardProvenanceEventRecord.Builder();
        builder.setEventTime(System.currentTimeMillis());
        builder.setEventType(RECEIVE);
        builder.setTransitUri("nifi://unit-test");
        attributes.put("uuid", uuid);
        builder.fromFlowFile(TestUtil.createFlowFile(3L, 3000L, attributes));
        builder.setComponentId("1234");
        builder.setComponentType("dummy processor");
        for (int i = 0; i < 10; i++) {
            builder.fromFlowFile(TestUtil.createFlowFile(i, 3000L, attributes));
            attributes.put("uuid", ("00000000-0000-0000-0000-00000000000" + i));
            repo.registerEvent(builder.build());
        }
        repo.close();
        final PersistentProvenanceRepository secondRepo = new PersistentProvenanceRepository(config, ITestPersistentProvenanceRepository.DEFAULT_ROLLOVER_MILLIS);
        secondRepo.initialize(getEventReporter(), null, null, EMPTY);
        try {
            final ProvenanceEventRecord event11 = builder.build();
            secondRepo.registerEvent(event11);
            secondRepo.waitForRollover();
            final ProvenanceEventRecord event11Retrieved = secondRepo.getEvent(10L, null);
            Assert.assertNotNull(event11Retrieved);
            Assert.assertEquals(10, event11Retrieved.getEventId());
        } finally {
            secondRepo.close();
        }
    }

    /**
     * Here the event file is simply corrupted by virtue of not having any event
     * records while having correct headers
     */
    @Test
    public void testWithWithEventFileMissingRecord() throws Exception {
        Assume.assumeFalse(isWindowsEnvironment());
        File eventFile = this.prepCorruptedEventFileTests();
        final Query query = new Query(UUID.randomUUID().toString());
        query.addSearchTerm(SearchTerms.newSearchTerm(ComponentID, "foo-*"));
        query.setMaxResults(100);
        DataOutputStream in = new DataOutputStream(new GZIPOutputStream(new FileOutputStream(eventFile)));
        in.writeUTF("BlahBlah");
        in.writeInt(4);
        in.close();
        Assert.assertTrue(eventFile.exists());
        final QueryResult result = repo.queryEvents(query, createUser());
        Assert.assertEquals(10, result.getMatchingEvents().size());
    }

    /**
     * Here the event file is simply corrupted by virtue of being empty (0
     * bytes)
     */
    @Test
    public void testWithWithEventFileCorrupted() throws Exception {
        Assume.assumeFalse(isWindowsEnvironment());
        File eventFile = this.prepCorruptedEventFileTests();
        final Query query = new Query(UUID.randomUUID().toString());
        query.addSearchTerm(SearchTerms.newSearchTerm(ComponentID, "foo-*"));
        query.setMaxResults(100);
        DataOutputStream in = new DataOutputStream(new GZIPOutputStream(new FileOutputStream(eventFile)));
        in.close();
        final QueryResult result = repo.queryEvents(query, createUser());
        Assert.assertEquals(10, result.getMatchingEvents().size());
    }

    @Test
    public void testNotAuthorizedGetSpecificEvent() throws IOException {
        Assume.assumeFalse(isWindowsEnvironment());
        final RepositoryConfiguration config = ITestPersistentProvenanceRepository.createConfiguration();
        config.setMaxRecordLife(5, TimeUnit.MINUTES);
        config.setMaxStorageCapacity((1024L * 1024L));
        config.setMaxEventFileLife(500, TimeUnit.MILLISECONDS);
        config.setMaxEventFileCapacity((1024L * 1024L));
        config.setSearchableFields(new ArrayList(SearchableFields.getStandardFields()));
        config.setDesiredIndexSize(10);// force new index to be created for each rollover

        final AccessDeniedException expectedException = new AccessDeniedException("Unit Test - Intentionally Thrown");
        repo = new PersistentProvenanceRepository(config, ITestPersistentProvenanceRepository.DEFAULT_ROLLOVER_MILLIS) {
            @Override
            public void authorize(ProvenanceEventRecord event, NiFiUser user) {
                throw expectedException;
            }
        };
        repo.initialize(getEventReporter(), null, null, EMPTY);
        final String uuid = "00000000-0000-0000-0000-000000000000";
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("abc", "xyz");
        attributes.put("xyz", "abc");
        attributes.put("filename", ("file-" + uuid));
        final ProvenanceEventBuilder builder = new StandardProvenanceEventRecord.Builder();
        builder.setEventTime(System.currentTimeMillis());
        builder.setEventType(RECEIVE);
        builder.setTransitUri("nifi://unit-test");
        builder.fromFlowFile(TestUtil.createFlowFile(3L, 3000L, attributes));
        builder.setComponentId("1234");
        builder.setComponentType("dummy processor");
        for (int i = 0; i < 10; i++) {
            attributes.put("uuid", ("00000000-0000-0000-0000-00000000000" + i));
            builder.fromFlowFile(TestUtil.createFlowFile(i, 3000L, attributes));
            builder.setEventTime(10L);// make sure the events are destroyed when we call purge

            repo.registerEvent(builder.build());
        }
        repo.waitForRollover();
        try {
            repo.getEvent(0L, null);
            Assert.fail("getEvent() did not throw an Exception");
        } catch (final Exception e) {
            Assert.assertSame(expectedException, e);
        }
    }

    @Test
    public void testNotAuthorizedGetEventRange() throws IOException {
        Assume.assumeFalse(isWindowsEnvironment());
        final RepositoryConfiguration config = ITestPersistentProvenanceRepository.createConfiguration();
        config.setMaxRecordLife(5, TimeUnit.MINUTES);
        config.setMaxStorageCapacity((1024L * 1024L));
        config.setMaxEventFileLife(500, TimeUnit.MILLISECONDS);
        config.setMaxEventFileCapacity((1024L * 1024L));
        config.setSearchableFields(new ArrayList(SearchableFields.getStandardFields()));
        config.setDesiredIndexSize(10);// force new index to be created for each rollover

        repo = new PersistentProvenanceRepository(config, ITestPersistentProvenanceRepository.DEFAULT_ROLLOVER_MILLIS) {
            @Override
            public boolean isAuthorized(ProvenanceEventRecord event, NiFiUser user) {
                return (event.getEventId()) > 2;
            }
        };
        repo.initialize(getEventReporter(), null, null, EMPTY);
        final String uuid = "00000000-0000-0000-0000-000000000000";
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("abc", "xyz");
        attributes.put("xyz", "abc");
        attributes.put("filename", ("file-" + uuid));
        final ProvenanceEventBuilder builder = new StandardProvenanceEventRecord.Builder();
        builder.setEventTime(System.currentTimeMillis());
        builder.setEventType(RECEIVE);
        builder.setTransitUri("nifi://unit-test");
        builder.fromFlowFile(TestUtil.createFlowFile(3L, 3000L, attributes));
        builder.setComponentId("1234");
        builder.setComponentType("dummy processor");
        for (int i = 0; i < 10; i++) {
            attributes.put("uuid", ("00000000-0000-0000-0000-00000000000" + i));
            builder.fromFlowFile(TestUtil.createFlowFile(i, 3000L, attributes));
            builder.setEventTime(10L);// make sure the events are destroyed when we call purge

            repo.registerEvent(builder.build());
        }
        repo.waitForRollover();
        final List<ProvenanceEventRecord> events = repo.getEvents(0L, 10, null);
        // Ensure that we gets events with ID's 3 through 10.
        Assert.assertEquals(7, events.size());
        final List<Long> eventIds = events.stream().map(( event) -> event.getEventId()).sorted().collect(Collectors.toList());
        for (int i = 0; i < 7; i++) {
            Assert.assertEquals((i + 3), eventIds.get(i).intValue());
        }
    }

    @Test(timeout = 10000)
    public void testNotAuthorizedQuery() throws IOException, InterruptedException {
        Assume.assumeFalse(isWindowsEnvironment());
        final RepositoryConfiguration config = ITestPersistentProvenanceRepository.createConfiguration();
        config.setMaxRecordLife(5, TimeUnit.MINUTES);
        config.setMaxStorageCapacity((1024L * 1024L));
        config.setMaxEventFileLife(500, TimeUnit.MILLISECONDS);
        config.setMaxEventFileCapacity((1024L * 1024L));
        config.setSearchableFields(new ArrayList(SearchableFields.getStandardFields()));
        config.setDesiredIndexSize(10);// force new index to be created for each rollover

        repo = new PersistentProvenanceRepository(config, ITestPersistentProvenanceRepository.DEFAULT_ROLLOVER_MILLIS) {
            @Override
            public boolean isAuthorized(ProvenanceEventRecord event, NiFiUser user) {
                return (event.getEventId()) > 2;
            }
        };
        repo.initialize(getEventReporter(), null, null, EMPTY);
        final String uuid = "00000000-0000-0000-0000-000000000000";
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("abc", "xyz");
        attributes.put("xyz", "abc");
        attributes.put("filename", ("file-" + uuid));
        final ProvenanceEventBuilder builder = new StandardProvenanceEventRecord.Builder();
        builder.setEventTime(System.currentTimeMillis());
        builder.setEventType(RECEIVE);
        builder.setTransitUri("nifi://unit-test");
        builder.fromFlowFile(TestUtil.createFlowFile(3L, 3000L, attributes));
        builder.setComponentId("1234");
        builder.setComponentType("dummy processor");
        for (int i = 0; i < 10; i++) {
            attributes.put("uuid", ("00000000-0000-0000-0000-00000000000" + i));
            builder.fromFlowFile(TestUtil.createFlowFile(i, 3000L, attributes));
            builder.setEventTime(10L);// make sure the events are destroyed when we call purge

            repo.registerEvent(builder.build());
        }
        repo.waitForRollover();
        final Query query = new Query("1234");
        query.addSearchTerm(SearchTerms.newSearchTerm(ComponentID, "1234"));
        final QuerySubmission submission = repo.submitQuery(query, createUser());
        final QueryResult result = submission.getResult();
        while (!(result.isFinished())) {
            Thread.sleep(100L);
        } 
        // Ensure that we gets events with ID's 3 through 10.
        final List<ProvenanceEventRecord> events = result.getMatchingEvents();
        Assert.assertEquals(7, events.size());
        final List<Long> eventIds = events.stream().map(( event) -> event.getEventId()).sorted().collect(Collectors.toList());
        for (int i = 0; i < 7; i++) {
            Assert.assertEquals((i + 3), eventIds.get(i).intValue());
        }
    }

    @Test(timeout = 1000000)
    public void testNotAuthorizedLineage() throws IOException, InterruptedException {
        Assume.assumeFalse(isWindowsEnvironment());
        final RepositoryConfiguration config = ITestPersistentProvenanceRepository.createConfiguration();
        config.setMaxRecordLife(5, TimeUnit.MINUTES);
        config.setMaxStorageCapacity((1024L * 1024L));
        config.setMaxEventFileLife(500, TimeUnit.MILLISECONDS);
        config.setMaxEventFileCapacity((1024L * 1024L));
        config.setSearchableFields(new ArrayList(SearchableFields.getStandardFields()));
        config.setDesiredIndexSize(10);// force new index to be created for each rollover

        repo = new PersistentProvenanceRepository(config, ITestPersistentProvenanceRepository.DEFAULT_ROLLOVER_MILLIS) {
            @Override
            public boolean isAuthorized(ProvenanceEventRecord event, NiFiUser user) {
                return (event.getEventType()) != (ATTRIBUTES_MODIFIED);
            }
        };
        repo.initialize(getEventReporter(), null, null, EMPTY);
        final String uuid = "00000000-0000-0000-0000-000000000000";
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("abc", "xyz");
        attributes.put("xyz", "abc");
        attributes.put("filename", ("file-" + uuid));
        attributes.put("uuid", uuid);
        final ProvenanceEventBuilder builder = new StandardProvenanceEventRecord.Builder();
        builder.setEventTime(System.currentTimeMillis());
        builder.setEventType(RECEIVE);
        builder.setTransitUri("nifi://unit-test");
        builder.fromFlowFile(TestUtil.createFlowFile(3L, 3000L, attributes));
        builder.setComponentId("1234");
        builder.setComponentType("dummy processor");
        builder.setEventTime(10L);// make sure the events are destroyed when we call purge

        builder.fromFlowFile(TestUtil.createFlowFile(1, 3000L, attributes));
        repo.registerEvent(builder.build());
        builder.setEventType(CONTENT_MODIFIED);
        builder.fromFlowFile(TestUtil.createFlowFile(2, 2000L, attributes));
        repo.registerEvent(builder.build());
        builder.setEventType(CONTENT_MODIFIED);
        builder.fromFlowFile(TestUtil.createFlowFile(3, 2000L, attributes));
        repo.registerEvent(builder.build());
        builder.setEventType(ATTRIBUTES_MODIFIED);
        attributes.put("new-attr", "yes");
        builder.fromFlowFile(TestUtil.createFlowFile(4, 2000L, attributes));
        repo.registerEvent(builder.build());
        final Map<String, String> childAttributes = new HashMap<>(attributes);
        childAttributes.put("uuid", "00000000-0000-0000-0000-000000000001");
        builder.setEventType(FORK);
        builder.fromFlowFile(TestUtil.createFlowFile(4, 2000L, attributes));
        builder.addChildFlowFile(TestUtil.createFlowFile(5, 2000L, childAttributes));
        builder.addParentFlowFile(TestUtil.createFlowFile(4, 2000L, attributes));
        repo.registerEvent(builder.build());
        builder.setEventType(ATTRIBUTES_MODIFIED);
        builder.fromFlowFile(TestUtil.createFlowFile(6, 2000L, childAttributes));
        repo.registerEvent(builder.build());
        builder.setEventType(DROP);
        builder.fromFlowFile(TestUtil.createFlowFile(6, 2000L, childAttributes));
        repo.registerEvent(builder.build());
        repo.waitForRollover();
        final AsyncLineageSubmission originalLineage = repo.submitLineageComputation(uuid, createUser());
        final StandardLineageResult result = originalLineage.getResult();
        while (!(result.isFinished())) {
            Thread.sleep(100L);
        } 
        final List<LineageNode> lineageNodes = result.getNodes();
        Assert.assertEquals(6, lineageNodes.size());
        Assert.assertEquals(1, lineageNodes.stream().map(( node) -> node.getNodeType()).filter(( t) -> t == LineageNodeType.FLOWFILE_NODE).count());
        Assert.assertEquals(5, lineageNodes.stream().map(( node) -> node.getNodeType()).filter(( t) -> t == LineageNodeType.PROVENANCE_EVENT_NODE).count());
        final Set<EventNode> eventNodes = lineageNodes.stream().filter(( node) -> (node.getNodeType()) == LineageNodeType.PROVENANCE_EVENT_NODE).map(( node) -> ((EventNode) (node))).collect(Collectors.toSet());
        final Map<ProvenanceEventType, List<EventNode>> nodesByType = eventNodes.stream().collect(Collectors.groupingBy(EventNode::getEventType));
        Assert.assertEquals(1, nodesByType.get(RECEIVE).size());
        Assert.assertEquals(2, nodesByType.get(CONTENT_MODIFIED).size());
        Assert.assertEquals(1, nodesByType.get(FORK).size());
        Assert.assertEquals(1, nodesByType.get(UNKNOWN).size());
        Assert.assertNull(nodesByType.get(ATTRIBUTES_MODIFIED));
        // Test filtering on expandChildren
        final AsyncLineageSubmission expandChild = repo.submitExpandChildren(4L, createUser());
        final StandardLineageResult expandChildResult = expandChild.getResult();
        while (!(expandChildResult.isFinished())) {
            Thread.sleep(100L);
        } 
        final List<LineageNode> expandChildNodes = expandChildResult.getNodes();
        Assert.assertEquals(4, expandChildNodes.size());
        Assert.assertEquals(1, expandChildNodes.stream().map(( node) -> node.getNodeType()).filter(( t) -> t == LineageNodeType.FLOWFILE_NODE).count());
        Assert.assertEquals(3, expandChildNodes.stream().map(( node) -> node.getNodeType()).filter(( t) -> t == LineageNodeType.PROVENANCE_EVENT_NODE).count());
        final Set<EventNode> childEventNodes = expandChildNodes.stream().filter(( node) -> (node.getNodeType()) == LineageNodeType.PROVENANCE_EVENT_NODE).map(( node) -> ((EventNode) (node))).collect(Collectors.toSet());
        final Map<ProvenanceEventType, List<EventNode>> childNodesByType = childEventNodes.stream().collect(Collectors.groupingBy(EventNode::getEventType));
        Assert.assertEquals(1, childNodesByType.get(FORK).size());
        Assert.assertEquals(1, childNodesByType.get(DROP).size());
        Assert.assertEquals(1, childNodesByType.get(UNKNOWN).size());
        Assert.assertNull(childNodesByType.get(ATTRIBUTES_MODIFIED));
    }

    @Test
    public void testBackPressure() throws IOException, InterruptedException {
        Assume.assumeFalse(isWindowsEnvironment());
        final RepositoryConfiguration config = ITestPersistentProvenanceRepository.createConfiguration();
        config.setMaxEventFileCapacity(1L);// force rollover on each record.

        config.setJournalCount(1);
        final AtomicInteger journalCountRef = new AtomicInteger(0);
        repo = new PersistentProvenanceRepository(config, ITestPersistentProvenanceRepository.DEFAULT_ROLLOVER_MILLIS) {
            @Override
            protected int getJournalCount() {
                return journalCountRef.get();
            }
        };
        repo.initialize(getEventReporter(), null, null, EMPTY);
        final Map<String, String> attributes = new HashMap<>();
        final ProvenanceEventBuilder builder = new StandardProvenanceEventRecord.Builder();
        builder.setEventTime(System.currentTimeMillis());
        builder.setEventType(RECEIVE);
        builder.setTransitUri("nifi://unit-test");
        attributes.put("uuid", UUID.randomUUID().toString());
        builder.fromFlowFile(TestUtil.createFlowFile(3L, 3000L, attributes));
        builder.setComponentId("1234");
        builder.setComponentType("dummy processor");
        // ensure that we can register the events.
        for (int i = 0; i < 10; i++) {
            builder.fromFlowFile(TestUtil.createFlowFile(i, 3000L, attributes));
            attributes.put("uuid", ("00000000-0000-0000-0000-00000000000" + i));
            repo.registerEvent(builder.build());
        }
        // set number of journals to 6 so that we will block.
        journalCountRef.set(6);
        final AtomicLong threadNanos = new AtomicLong(0L);
        final Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                final long start = System.nanoTime();
                builder.fromFlowFile(TestUtil.createFlowFile(13, 3000L, attributes));
                attributes.put("uuid", ("00000000-0000-0000-0000-00000000000" + 13));
                repo.registerEvent(builder.build());
                threadNanos.set(((System.nanoTime()) - start));
            }
        });
        t.start();
        Thread.sleep(1500L);
        journalCountRef.set(1);
        t.join();
        final int threadMillis = ((int) (TimeUnit.NANOSECONDS.toMillis(threadNanos.get())));
        Assert.assertTrue((threadMillis > 1200));// use 1200 to account for the fact that the timing is not exact

        builder.fromFlowFile(TestUtil.createFlowFile(15, 3000L, attributes));
        attributes.put("uuid", ("00000000-0000-0000-0000-00000000000" + 15));
        repo.registerEvent(builder.build());
        Thread.sleep(3000L);
    }

    @Test
    public void testTextualQuery() throws IOException, InterruptedException, ParseException {
        Assume.assumeFalse(isWindowsEnvironment());
        final RepositoryConfiguration config = ITestPersistentProvenanceRepository.createConfiguration();
        config.setMaxEventFileLife(500, TimeUnit.MILLISECONDS);
        config.setSearchableFields(new ArrayList(SearchableFields.getStandardFields()));
        repo = new PersistentProvenanceRepository(config, ITestPersistentProvenanceRepository.DEFAULT_ROLLOVER_MILLIS);
        repo.initialize(getEventReporter(), null, null, EMPTY);
        final String uuid = "00000000-0000-0000-0000-000000000000";
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("abc", "xyz");
        attributes.put("xyz", "abc");
        attributes.put("filename", "file-unnamed");
        final long now = System.currentTimeMillis();
        final ProvenanceEventBuilder builder = new StandardProvenanceEventRecord.Builder();
        builder.setEventTime((now - (TimeUnit.MILLISECONDS.convert(30, TimeUnit.SECONDS))));
        builder.setEventType(RECEIVE);
        builder.setTransitUri("nifi://unit-test");
        attributes.put("uuid", uuid);
        builder.fromFlowFile(TestUtil.createFlowFile(3L, 3000L, attributes));
        builder.setComponentId("1234");
        builder.setComponentType("dummy processor");
        for (int i = 0; i < 10; i++) {
            if (i > 5) {
                attributes.put("filename", ("file-" + i));
                builder.setEventTime(System.currentTimeMillis());
            }
            builder.fromFlowFile(TestUtil.createFlowFile(i, 3000L, attributes));
            attributes.put("uuid", ("00000000-0000-0000-0000-00000000000" + i));
            repo.registerEvent(builder.build());
        }
        repo.waitForRollover();
        final IndexConfiguration indexConfig = new IndexConfiguration(config);
        final List<File> indexDirs = indexConfig.getIndexDirectories();
        final String query = "uuid:00000000-0000-0000-0000-0000000000* AND NOT filename:file-?";
        final List<Document> results = runQuery(indexDirs.get(0), new ArrayList(config.getStorageDirectories().values()), query);
        Assert.assertEquals(6, results.size());
    }

    @Test
    public void testMergeJournals() throws IOException, InterruptedException {
        Assume.assumeFalse(isWindowsEnvironment());
        final RepositoryConfiguration config = ITestPersistentProvenanceRepository.createConfiguration();
        config.setMaxEventFileLife(3, TimeUnit.SECONDS);
        repo = new PersistentProvenanceRepository(config, ITestPersistentProvenanceRepository.DEFAULT_ROLLOVER_MILLIS);
        repo.initialize(getEventReporter(), null, null, EMPTY);
        final Map<String, String> attributes = new HashMap<>();
        final ProvenanceEventBuilder builder = new StandardProvenanceEventRecord.Builder();
        builder.setEventTime(System.currentTimeMillis());
        builder.setEventType(RECEIVE);
        builder.setTransitUri("nifi://unit-test");
        attributes.put("uuid", "12345678-0000-0000-0000-012345678912");
        builder.fromFlowFile(TestUtil.createFlowFile(3L, 3000L, attributes));
        builder.setComponentId("1234");
        builder.setComponentType("dummy processor");
        final ProvenanceEventRecord record = builder.build();
        final ExecutorService exec = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 10000; i++) {
            exec.submit(new Runnable() {
                @Override
                public void run() {
                    repo.registerEvent(record);
                }
            });
        }
        repo.waitForRollover();
        final File storageDir = config.getStorageDirectories().values().iterator().next();
        long counter = 0;
        for (final File file : storageDir.listFiles()) {
            if (file.isFile()) {
                try (RecordReader reader = RecordReaders.newRecordReader(file, null, 2048)) {
                    ProvenanceEventRecord r = null;
                    while ((r = reader.nextRecord()) != null) {
                        Assert.assertEquals((counter++), r.getEventId());
                    } 
                }
            }
        }
        Assert.assertEquals(10000, counter);
    }

    @Test
    public void testMergeJournalsBadFirstRecord() throws IOException, InterruptedException {
        Assume.assumeFalse(isWindowsEnvironment());
        final RepositoryConfiguration config = ITestPersistentProvenanceRepository.createConfiguration();
        config.setMaxEventFileLife(3, TimeUnit.SECONDS);
        ITestPersistentProvenanceRepository.TestablePersistentProvenanceRepository testRepo = new ITestPersistentProvenanceRepository.TestablePersistentProvenanceRepository(config, ITestPersistentProvenanceRepository.DEFAULT_ROLLOVER_MILLIS);
        testRepo.initialize(getEventReporter(), null, null, null);
        final Map<String, String> attributes = new HashMap<>();
        final ProvenanceEventBuilder builder = new StandardProvenanceEventRecord.Builder();
        builder.setEventTime(System.currentTimeMillis());
        builder.setEventType(RECEIVE);
        builder.setTransitUri("nifi://unit-test");
        attributes.put("uuid", "12345678-0000-0000-0000-012345678912");
        builder.fromFlowFile(TestUtil.createFlowFile(3L, 3000L, attributes));
        builder.setComponentId("1234");
        builder.setComponentType("dummy processor");
        final ProvenanceEventRecord record = builder.build();
        final ExecutorService exec = Executors.newFixedThreadPool(10);
        final List<Future> futures = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            futures.add(exec.submit(new Runnable() {
                @Override
                public void run() {
                    testRepo.registerEvent(record);
                }
            }));
        }
        // wait for writers to finish and then corrupt the first record of the first journal file
        for (Future future : futures) {
            while (!(future.isDone())) {
                Thread.sleep(10);
            } 
        }
        RecordWriter firstWriter = testRepo.getWriters()[0];
        corruptJournalFile(firstWriter.getFile(), ((ITestPersistentProvenanceRepository.headerSize) + 15), "RECEIVE", "BADTYPE");
        recoverJournalFiles();
        final File storageDir = config.getStorageDirectories().values().iterator().next();
        Assert.assertTrue(((checkJournalRecords(storageDir, false)) < 10000));
    }

    @Test
    public void testMergeJournalsBadRecordAfterFirst() throws IOException, InterruptedException {
        Assume.assumeFalse(isWindowsEnvironment());
        final RepositoryConfiguration config = ITestPersistentProvenanceRepository.createConfiguration();
        config.setMaxEventFileLife(3, TimeUnit.SECONDS);
        ITestPersistentProvenanceRepository.TestablePersistentProvenanceRepository testRepo = new ITestPersistentProvenanceRepository.TestablePersistentProvenanceRepository(config, ITestPersistentProvenanceRepository.DEFAULT_ROLLOVER_MILLIS);
        testRepo.initialize(getEventReporter(), null, null, null);
        final Map<String, String> attributes = new HashMap<>();
        final ProvenanceEventBuilder builder = new StandardProvenanceEventRecord.Builder();
        builder.setEventTime(System.currentTimeMillis());
        builder.setEventType(RECEIVE);
        builder.setTransitUri("nifi://unit-test");
        attributes.put("uuid", "12345678-0000-0000-0000-012345678912");
        builder.fromFlowFile(TestUtil.createFlowFile(3L, 3000L, attributes));
        builder.setComponentId("1234");
        builder.setComponentType("dummy processor");
        final ProvenanceEventRecord record = builder.build();
        final ExecutorService exec = Executors.newFixedThreadPool(10);
        final List<Future<?>> futures = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            futures.add(exec.submit(new Runnable() {
                @Override
                public void run() {
                    testRepo.registerEvent(record);
                }
            }));
        }
        // corrupt the first record of the first journal file
        for (Future<?> future : futures) {
            while (!(future.isDone())) {
                Thread.sleep(10);
            } 
        }
        RecordWriter firstWriter = testRepo.getWriters()[0];
        corruptJournalFile(firstWriter.getFile(), (((ITestPersistentProvenanceRepository.headerSize) + 15) + (ITestPersistentProvenanceRepository.recordSize)), "RECEIVE", "BADTYPE");
        recoverJournalFiles();
        final File storageDir = config.getStorageDirectories().values().iterator().next();
        Assert.assertTrue(((checkJournalRecords(storageDir, false)) < 10000));
    }

    @Test
    public void testMergeJournalsEmptyJournal() throws IOException, InterruptedException {
        Assume.assumeFalse(isWindowsEnvironment());// skip on window

        final RepositoryConfiguration config = ITestPersistentProvenanceRepository.createConfiguration();
        config.setMaxEventFileLife(3, TimeUnit.SECONDS);
        ITestPersistentProvenanceRepository.TestablePersistentProvenanceRepository testRepo = new ITestPersistentProvenanceRepository.TestablePersistentProvenanceRepository(config, ITestPersistentProvenanceRepository.DEFAULT_ROLLOVER_MILLIS);
        testRepo.initialize(getEventReporter(), null, null, null);
        final Map<String, String> attributes = new HashMap<>();
        final ProvenanceEventBuilder builder = new StandardProvenanceEventRecord.Builder();
        builder.setEventTime(System.currentTimeMillis());
        builder.setEventType(RECEIVE);
        builder.setTransitUri("nifi://unit-test");
        attributes.put("uuid", "12345678-0000-0000-0000-012345678912");
        builder.fromFlowFile(TestUtil.createFlowFile(3L, 3000L, attributes));
        builder.setComponentId("1234");
        builder.setComponentType("dummy processor");
        final ProvenanceEventRecord record = builder.build();
        final ExecutorService exec = Executors.newFixedThreadPool(10);
        final List<Future> futures = new ArrayList<>();
        for (int i = 0; i < ((config.getJournalCount()) - 1); i++) {
            futures.add(exec.submit(new Runnable() {
                @Override
                public void run() {
                    testRepo.registerEvent(record);
                }
            }));
        }
        // wait for writers to finish and then corrupt the first record of the first journal file
        for (Future future : futures) {
            while (!(future.isDone())) {
                Thread.sleep(10);
            } 
        }
        recoverJournalFiles();
        Assert.assertEquals("mergeJournals() should not error on empty journal", 0, reportedEvents.size());
        final File storageDir = config.getStorageDirectories().values().iterator().next();
        Assert.assertEquals(((config.getJournalCount()) - 1), checkJournalRecords(storageDir, true));
    }

    @Test
    public void testRolloverRetry() throws IOException, InterruptedException {
        Assume.assumeFalse(isWindowsEnvironment());
        final AtomicInteger retryAmount = new AtomicInteger(0);
        final RepositoryConfiguration config = ITestPersistentProvenanceRepository.createConfiguration();
        config.setMaxEventFileLife(3, TimeUnit.SECONDS);
        repo = new PersistentProvenanceRepository(config, ITestPersistentProvenanceRepository.DEFAULT_ROLLOVER_MILLIS) {
            @Override
            File mergeJournals(List<File> journalFiles, File suggestedMergeFile, EventReporter eventReporter) throws IOException {
                retryAmount.incrementAndGet();
                return super.mergeJournals(journalFiles, suggestedMergeFile, eventReporter);
            }

            // Indicate that there are no files available.
            @Override
            protected List<File> filterUnavailableFiles(List<File> journalFiles) {
                return Collections.emptyList();
            }

            @Override
            protected long getRolloverRetryMillis() {
                return 10L;// retry quickly.

            }
        };
        repo.initialize(getEventReporter(), null, null, EMPTY);
        final Map<String, String> attributes = new HashMap<>();
        final ProvenanceEventBuilder builder = new StandardProvenanceEventRecord.Builder();
        builder.setEventTime(System.currentTimeMillis());
        builder.setEventType(RECEIVE);
        builder.setTransitUri("nifi://unit-test");
        attributes.put("uuid", "12345678-0000-0000-0000-012345678912");
        builder.fromFlowFile(TestUtil.createFlowFile(3L, 3000L, attributes));
        builder.setComponentId("1234");
        builder.setComponentType("dummy processor");
        final ProvenanceEventRecord record = builder.build();
        final ExecutorService exec = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 10000; i++) {
            exec.submit(new Runnable() {
                @Override
                public void run() {
                    repo.registerEvent(record);
                }
            });
        }
        exec.shutdown();
        exec.awaitTermination(10, TimeUnit.SECONDS);
        repo.waitForRollover();
        Assert.assertEquals(5, retryAmount.get());
    }

    @Test
    public void testTruncateAttributes() throws IOException, InterruptedException {
        Assume.assumeFalse(isWindowsEnvironment());
        final RepositoryConfiguration config = ITestPersistentProvenanceRepository.createConfiguration();
        config.setMaxAttributeChars(50);
        config.setMaxEventFileLife(3, TimeUnit.SECONDS);
        repo = new PersistentProvenanceRepository(config, ITestPersistentProvenanceRepository.DEFAULT_ROLLOVER_MILLIS);
        repo.initialize(getEventReporter(), null, null, EMPTY);
        final String maxLengthChars = "12345678901234567890123456789012345678901234567890";
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("75chars", "123456789012345678901234567890123456789012345678901234567890123456789012345");
        attributes.put("51chars", "123456789012345678901234567890123456789012345678901");
        attributes.put("50chars", "12345678901234567890123456789012345678901234567890");
        attributes.put("49chars", "1234567890123456789012345678901234567890123456789");
        attributes.put("nullChar", null);
        final ProvenanceEventBuilder builder = new StandardProvenanceEventRecord.Builder();
        builder.setEventTime(System.currentTimeMillis());
        builder.setEventType(RECEIVE);
        builder.setTransitUri("nifi://unit-test");
        attributes.put("uuid", "12345678-0000-0000-0000-012345678912");
        builder.fromFlowFile(TestUtil.createFlowFile(3L, 3000L, attributes));
        builder.setComponentId("1234");
        builder.setComponentType("dummy processor");
        final ProvenanceEventRecord record = builder.build();
        repo.registerEvent(record);
        repo.waitForRollover();
        final ProvenanceEventRecord retrieved = repo.getEvent(0L, null);
        Assert.assertNotNull(retrieved);
        Assert.assertEquals("12345678-0000-0000-0000-012345678912", retrieved.getAttributes().get("uuid"));
        Assert.assertEquals(maxLengthChars, retrieved.getAttributes().get("75chars"));
        Assert.assertEquals(maxLengthChars, retrieved.getAttributes().get("51chars"));
        Assert.assertEquals(maxLengthChars, retrieved.getAttributes().get("50chars"));
        Assert.assertEquals(maxLengthChars.substring(0, 49), retrieved.getAttributes().get("49chars"));
    }

    @Test(timeout = 15000)
    public void testExceptionOnIndex() throws IOException {
        Assume.assumeFalse(isWindowsEnvironment());
        final RepositoryConfiguration config = ITestPersistentProvenanceRepository.createConfiguration();
        config.setMaxAttributeChars(50);
        config.setMaxEventFileLife(3, TimeUnit.SECONDS);
        config.setIndexThreadPoolSize(1);
        final int numEventsToIndex = 10;
        final AtomicInteger indexedEventCount = new AtomicInteger(0);
        repo = new PersistentProvenanceRepository(config, ITestPersistentProvenanceRepository.DEFAULT_ROLLOVER_MILLIS) {
            @Override
            protected synchronized IndexingAction createIndexingAction() {
                return new IndexingAction(config.getSearchableFields(), config.getSearchableAttributes()) {
                    @Override
                    public void index(StandardProvenanceEventRecord record, IndexWriter indexWriter, Integer blockIndex) throws IOException {
                        final int count = indexedEventCount.incrementAndGet();
                        if (count <= numEventsToIndex) {
                            return;
                        }
                        throw new IOException("Unit Test - Intentional Exception");
                    }
                };
            }
        };
        repo.initialize(getEventReporter(), null, null, EMPTY);
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("uuid", "12345678-0000-0000-0000-012345678912");
        final ProvenanceEventBuilder builder = new StandardProvenanceEventRecord.Builder();
        builder.setEventTime(System.currentTimeMillis());
        builder.setEventType(RECEIVE);
        builder.setTransitUri("nifi://unit-test");
        builder.fromFlowFile(TestUtil.createFlowFile(3L, 3000L, attributes));
        builder.setComponentId("1234");
        builder.setComponentType("dummy processor");
        for (int i = 0; i < 1000; i++) {
            final ProvenanceEventRecord record = builder.build();
            repo.registerEvent(record);
        }
        repo.waitForRollover();
        Assert.assertEquals((numEventsToIndex + (MAX_INDEXING_FAILURE_COUNT)), indexedEventCount.get());
        Assert.assertEquals(1, reportedEvents.size());
        final ITestPersistentProvenanceRepository.ReportedEvent event = reportedEvents.get(0);
        Assert.assertEquals(WARNING, event.getSeverity());
    }

    @Test
    public void testFailureToCreateWriterDoesNotPreventSubsequentRollover() throws IOException, InterruptedException {
        Assume.assumeFalse(isWindowsEnvironment());
        final RepositoryConfiguration config = ITestPersistentProvenanceRepository.createConfiguration();
        config.setMaxAttributeChars(50);
        config.setMaxEventFileLife(3, TimeUnit.SECONDS);
        // Create a repo that will allow only a single writer to be created.
        final IOException failure = new IOException("Already created writers once. Unit test causing failure.");
        repo = new PersistentProvenanceRepository(config, ITestPersistentProvenanceRepository.DEFAULT_ROLLOVER_MILLIS) {
            int iterations = 0;

            @Override
            protected RecordWriter[] createWriters(RepositoryConfiguration config, long initialRecordId) throws IOException {
                if (((iterations)++) == 1) {
                    throw failure;
                } else {
                    return super.createWriters(config, initialRecordId);
                }
            }
        };
        // initialize with our event reporter
        repo.initialize(getEventReporter(), null, null, EMPTY);
        // create some events in the journal files.
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("75chars", "123456789012345678901234567890123456789012345678901234567890123456789012345");
        final ProvenanceEventBuilder builder = new StandardProvenanceEventRecord.Builder();
        builder.setEventTime(System.currentTimeMillis());
        builder.setEventType(RECEIVE);
        builder.setTransitUri("nifi://unit-test");
        attributes.put("uuid", "12345678-0000-0000-0000-012345678912");
        builder.fromFlowFile(TestUtil.createFlowFile(3L, 3000L, attributes));
        builder.setComponentId("1234");
        builder.setComponentType("dummy processor");
        for (int i = 0; i < 50; i++) {
            final ProvenanceEventRecord event = builder.build();
            repo.registerEvent(event);
        }
        // Attempt to rollover but fail to create new writers.
        try {
            repo.rolloverWithLock(true);
            Assert.fail("Expected to get IOException when calling rolloverWithLock");
        } catch (final IOException ioe) {
            Assert.assertTrue((ioe == failure));
        }
        // Wait for the first rollover to succeed.
        repo.waitForRollover();
        // This time when we rollover, we should not have a problem rolling over.
        repo.rolloverWithLock(true);
        // Ensure that no errors were reported.
        Assert.assertEquals(0, reportedEvents.size());
    }

    private static class ReportedEvent {
        private final Severity severity;

        private final String category;

        private final String message;

        public ReportedEvent(final Severity severity, final String category, final String message) {
            this.severity = severity;
            this.category = category;
            this.message = message;
        }

        @SuppressWarnings("unused")
        public String getCategory() {
            return category;
        }

        @SuppressWarnings("unused")
        public String getMessage() {
            return message;
        }

        public Severity getSeverity() {
            return severity;
        }
    }

    private static class TestablePersistentProvenanceRepository extends PersistentProvenanceRepository {
        TestablePersistentProvenanceRepository() {
            super();
        }

        TestablePersistentProvenanceRepository(final NiFiProperties nifiProperties) throws IOException {
            super(nifiProperties);
        }

        TestablePersistentProvenanceRepository(final RepositoryConfiguration configuration, final int rolloverCheckMillis) throws IOException {
            super(configuration, rolloverCheckMillis);
        }

        RecordWriter[] getWriters() {
            Class klass = PersistentProvenanceRepository.class;
            Field writersField;
            RecordWriter[] writers = null;
            try {
                writersField = klass.getDeclaredField("writers");
                writersField.setAccessible(true);
                writers = ((RecordWriter[]) (writersField.get(this)));
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
            return writers;
        }

        int getRolloverCheckMillis() {
            Class klass = PersistentProvenanceRepository.class;
            Field rolloverCheckMillisField;
            int rolloverCheckMillis = -1;
            try {
                rolloverCheckMillisField = klass.getDeclaredField("rolloverCheckMillis");
                rolloverCheckMillisField.setAccessible(true);
                rolloverCheckMillis = ((int) (rolloverCheckMillisField.get(this)));
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
            return rolloverCheckMillis;
        }
    }
}

