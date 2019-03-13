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
package org.apache.nifi.processor.util.list;


import AbstractListProcessor.BY_ENTITIES;
import AbstractListProcessor.DISTRIBUTED_CACHE_SERVICE;
import AbstractListProcessor.LAST_PROCESSED_LATEST_ENTRY_TIMESTAMP_KEY;
import AbstractListProcessor.LATEST_LISTED_ENTRY_TIMESTAMP_KEY;
import AbstractListProcessor.LISTING_STRATEGY;
import AbstractListProcessor.REL_SUCCESS;
import CoreAttributes.FILENAME;
import ListedEntityTracker.INITIAL_LISTING_TARGET;
import ListedEntityTracker.TRACKING_STATE_CACHE;
import ListedEntityTracker.TRACKING_TIME_WINDOW;
import Scope.CLUSTER;
import Validator.VALID;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.components.state.Scope;
import org.apache.nifi.context.PropertyContext;
import org.apache.nifi.controller.AbstractControllerService;
import org.apache.nifi.distributed.cache.client.Deserializer;
import org.apache.nifi.distributed.cache.client.DistributedMapCacheClient;
import org.apache.nifi.distributed.cache.client.Serializer;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.reporting.InitializationException;
import org.apache.nifi.state.MockStateManager;
import org.apache.nifi.util.TestRunner;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestWatcher;

import static AbstractListProcessor.IDENTIFIER_PREFIX;


public class TestAbstractListProcessor {
    private static final long DEFAULT_SLEEP_MILLIS = TestAbstractListProcessor.getSleepMillis(TimeUnit.MILLISECONDS);

    private TestAbstractListProcessor.ConcreteListProcessor proc;

    private TestRunner runner;

    @Rule
    public TestWatcher dumpState = new ListProcessorTestWatcher(() -> {
        try {
            return runner.getStateManager().getState(Scope.LOCAL).toMap();
        } catch ( e) {
            throw new <e>RuntimeException("Failed to retrieve state");
        }
    }, () -> proc.getEntityList(), () -> runner.getFlowFilesForRelationship(AbstractListProcessor.REL_SUCCESS).stream().map(( m) -> ((FlowFile) (m))).collect(Collectors.toList()));

    @Rule
    public final TemporaryFolder testFolder = new TemporaryFolder();

    @Test
    public void testStateMigratedFromCacheService() throws InitializationException {
        final TestAbstractListProcessor.DistributedCache cache = new TestAbstractListProcessor.DistributedCache();
        runner.addControllerService("cache", cache);
        runner.enableControllerService(cache);
        runner.setProperty(DISTRIBUTED_CACHE_SERVICE, "cache");
        final String serviceState = "{\"latestTimestamp\":1492,\"matchingIdentifiers\":[\"id\"]}";
        final String cacheKey = (getIdentifier()) + ".lastListingTime./path";
        cache.stored.put(cacheKey, serviceState);
        runner.run();
        final MockStateManager stateManager = runner.getStateManager();
        final Map<String, String> expectedState = new HashMap<>();
        // Ensure only timestamp is migrated
        expectedState.put(LATEST_LISTED_ENTRY_TIMESTAMP_KEY, "1492");
        expectedState.put(LAST_PROCESSED_LATEST_ENTRY_TIMESTAMP_KEY, "1492");
        stateManager.assertStateEquals(expectedState, CLUSTER);
    }

    @Test
    public void testNoStateToMigrate() throws Exception {
        runner.run();
        final MockStateManager stateManager = runner.getStateManager();
        final Map<String, String> expectedState = new HashMap<>();
        stateManager.assertStateEquals(expectedState, CLUSTER);
    }

    @Test
    public void testStateMigratedFromLocalFile() throws Exception {
        // Create a file that we will populate with the desired state
        File persistenceFile = testFolder.newFile(proc.persistenceFilename);
        // Override the processor's internal persistence file
        proc.persistenceFile = persistenceFile;
        // Local File persistence was a properties file format of <key>=<JSON entity listing representation>
        // Our ConcreteListProcessor is centered around files which are provided for a given path
        final String serviceState = (proc.getPath(runner.getProcessContext())) + "={\"latestTimestamp\":1492,\"matchingIdentifiers\":[\"id\"]}";
        // Create a persistence file of the format anticipated
        try (FileOutputStream fos = new FileOutputStream(persistenceFile)) {
            fos.write(serviceState.getBytes(StandardCharsets.UTF_8));
        }
        runner.run();
        // Verify the local persistence file is removed
        Assert.assertTrue("Failed to remove persistence file", (!(persistenceFile.exists())));
        // Verify the state manager now maintains the associated state
        final Map<String, String> expectedState = new HashMap<>();
        // Ensure timestamp and identifies are migrated
        expectedState.put(LATEST_LISTED_ENTRY_TIMESTAMP_KEY, "1492");
        expectedState.put(LAST_PROCESSED_LATEST_ENTRY_TIMESTAMP_KEY, "1492");
        expectedState.put(((IDENTIFIER_PREFIX) + ".0"), "id");
        runner.getStateManager().assertStateEquals(expectedState, CLUSTER);
    }

    @Test
    public void testFetchOnStart() throws InitializationException {
        final TestAbstractListProcessor.DistributedCache cache = new TestAbstractListProcessor.DistributedCache();
        runner.addControllerService("cache", cache);
        runner.enableControllerService(cache);
        runner.setProperty(DISTRIBUTED_CACHE_SERVICE, "cache");
        runner.run();
        Assert.assertEquals(1, cache.fetchCount);
    }

    @Test
    public void testEntityTrackingStrategy() throws InitializationException {
        runner.setProperty(LISTING_STRATEGY, BY_ENTITIES);
        // Require a cache service.
        runner.assertNotValid();
        final TestAbstractListProcessor.DistributedCache trackingCache = new TestAbstractListProcessor.DistributedCache();
        runner.addControllerService("tracking-cache", trackingCache);
        runner.enableControllerService(trackingCache);
        runner.setProperty(TRACKING_STATE_CACHE, "tracking-cache");
        runner.setProperty(TRACKING_TIME_WINDOW, "10ms");
        runner.assertValid();
        proc.currentTimestamp.set(0L);
        runner.run();
        Assert.assertEquals(0, runner.getFlowFilesForRelationship(REL_SUCCESS).size());
        // Should list one entity.
        proc.addEntity("one", "one", 1, 1);
        proc.currentTimestamp.set(1L);
        runner.clearTransferState();
        runner.run();
        Assert.assertEquals(1, runner.getFlowFilesForRelationship(REL_SUCCESS).size());
        runner.getFlowFilesForRelationship(REL_SUCCESS).get(0).assertAttributeEquals(FILENAME.key(), "one");
        // Should not list any entity.
        proc.currentTimestamp.set(2L);
        runner.clearTransferState();
        runner.run();
        Assert.assertEquals(0, runner.getFlowFilesForRelationship(REL_SUCCESS).size());
        // Should list added entities.
        proc.currentTimestamp.set(10L);
        proc.addEntity("five", "five", 5, 5);
        proc.addEntity("six", "six", 6, 6);
        runner.clearTransferState();
        runner.run();
        Assert.assertEquals(2, runner.getFlowFilesForRelationship(REL_SUCCESS).size());
        runner.getFlowFilesForRelationship(REL_SUCCESS).get(0).assertAttributeEquals(FILENAME.key(), "five");
        runner.getFlowFilesForRelationship(REL_SUCCESS).get(1).assertAttributeEquals(FILENAME.key(), "six");
        // Should be able to list entities having older timestamp than the previously listed entity.
        // But if its timestamp is out of tracking window, then it won't be picked.
        // Current timestamp = 13, and window = 10ms, meaning it can pick entities having timestamp 3 to 13.
        proc.currentTimestamp.set(13L);
        proc.addEntity("two", "two", 2, 2);
        proc.addEntity("three", "three", 3, 3);
        proc.addEntity("four", "four", 4, 4);
        runner.clearTransferState();
        runner.run();
        Assert.assertEquals(2, runner.getFlowFilesForRelationship(REL_SUCCESS).size());
        runner.getFlowFilesForRelationship(REL_SUCCESS).get(0).assertAttributeEquals(FILENAME.key(), "three");
        runner.getFlowFilesForRelationship(REL_SUCCESS).get(1).assertAttributeEquals(FILENAME.key(), "four");
        // Can pick entity that has newer timestamp.
        // Can pick entity that has different size.
        proc.currentTimestamp.set(14L);
        proc.addEntity("five", "five", 7, 5);
        proc.addEntity("six", "six", 6, 16);
        runner.clearTransferState();
        runner.run();
        Assert.assertEquals(2, runner.getFlowFilesForRelationship(REL_SUCCESS).size());
        runner.getFlowFilesForRelationship(REL_SUCCESS).get(0).assertAttributeEquals(FILENAME.key(), "six");
        runner.getFlowFilesForRelationship(REL_SUCCESS).get(1).assertAttributeEquals(FILENAME.key(), "five");
        // Reset state.
        // Current timestamp = 15, and window = 11ms, meaning it can pick entities having timestamp 4 to 15.
        proc.currentTimestamp.set(15L);
        // ConcreteListProcessor can reset state with any property.
        runner.setProperty(TRACKING_TIME_WINDOW, "11ms");
        runner.setProperty(TestAbstractListProcessor.ConcreteListProcessor.RESET_STATE, "1");
        runner.setProperty(INITIAL_LISTING_TARGET, "window");
        runner.clearTransferState();
        runner.run();
        Assert.assertEquals(3, runner.getFlowFilesForRelationship(REL_SUCCESS).size());
        runner.getFlowFilesForRelationship(REL_SUCCESS).get(0).assertAttributeEquals(FILENAME.key(), "four");
        runner.getFlowFilesForRelationship(REL_SUCCESS).get(1).assertAttributeEquals(FILENAME.key(), "six");
        runner.getFlowFilesForRelationship(REL_SUCCESS).get(2).assertAttributeEquals(FILENAME.key(), "five");
        // Reset state again.
        proc.currentTimestamp.set(20L);
        // ConcreteListProcessor can reset state with any property.
        runner.setProperty(INITIAL_LISTING_TARGET, "all");
        runner.setProperty(TestAbstractListProcessor.ConcreteListProcessor.RESET_STATE, "2");
        runner.clearTransferState();
        runner.run();
        // All entities should be picked, one to six.
        Assert.assertEquals(6, runner.getFlowFilesForRelationship(REL_SUCCESS).size());
    }

    static class DistributedCache extends AbstractControllerService implements DistributedMapCacheClient {
        private final Map<Object, Object> stored = new HashMap<>();

        private int fetchCount = 0;

        @Override
        public <K, V> boolean putIfAbsent(K key, V value, Serializer<K> keySerializer, Serializer<V> valueSerializer) throws IOException {
            return false;
        }

        @Override
        public <K, V> V getAndPutIfAbsent(K key, V value, Serializer<K> keySerializer, Serializer<V> valueSerializer, Deserializer<V> valueDeserializer) throws IOException {
            return null;
        }

        @Override
        public <K> boolean containsKey(K key, Serializer<K> keySerializer) throws IOException {
            return false;
        }

        @Override
        public <K, V> void put(K key, V value, Serializer<K> keySerializer, Serializer<V> valueSerializer) throws IOException {
            stored.put(key, value);
        }

        @Override
        @SuppressWarnings("unchecked")
        public <K, V> V get(K key, Serializer<K> keySerializer, Deserializer<V> valueDeserializer) throws IOException {
            (fetchCount)++;
            return ((V) (stored.get(key)));
        }

        @Override
        public void close() throws IOException {
        }

        @Override
        public <K> boolean remove(K key, Serializer<K> serializer) throws IOException {
            final Object value = stored.remove(key);
            return value != null;
        }

        @Override
        public long removeByPattern(String regex) throws IOException {
            final List<Object> removedRecords = new ArrayList<>();
            Pattern p = Pattern.compile(regex);
            for (Object key : stored.keySet()) {
                // Key must be backed by something that can be converted into a String
                Matcher m = p.matcher(key.toString());
                if (m.matches()) {
                    removedRecords.add(stored.get(key));
                }
            }
            final long numRemoved = removedRecords.size();
            removedRecords.forEach(stored::remove);
            return numRemoved;
        }
    }

    static class ConcreteListProcessor extends AbstractListProcessor<ListableEntity> {
        final Map<String, ListableEntity> entities = new HashMap<>();

        final String persistenceFilename = ("ListProcessor-local-state-" + (UUID.randomUUID().toString())) + ".json";

        String persistenceFolder = "target/";

        File persistenceFile = new File(((persistenceFolder) + (persistenceFilename)));

        private static PropertyDescriptor RESET_STATE = new PropertyDescriptor.Builder().name("reset-state").addValidator(VALID).build();

        final AtomicReference<Long> currentTimestamp = new AtomicReference<>();

        @Override
        protected ListedEntityTracker<ListableEntity> createListedEntityTracker() {
            return new ListedEntityTracker(getIdentifier(), getLogger(), () -> currentTimestamp.get());
        }

        @Override
        protected List<PropertyDescriptor> getSupportedPropertyDescriptors() {
            final List<PropertyDescriptor> properties = new ArrayList<>();
            properties.add(LISTING_STRATEGY);
            properties.add(DISTRIBUTED_CACHE_SERVICE);
            properties.add(TARGET_SYSTEM_TIMESTAMP_PRECISION);
            properties.add(TRACKING_STATE_CACHE);
            properties.add(TRACKING_TIME_WINDOW);
            properties.add(INITIAL_LISTING_TARGET);
            properties.add(TestAbstractListProcessor.ConcreteListProcessor.RESET_STATE);
            return properties;
        }

        @Override
        public File getPersistenceFile() {
            return persistenceFile;
        }

        public void addEntity(final String name, final String identifier, final long timestamp) {
            addEntity(name, identifier, timestamp, 0);
        }

        public void addEntity(final String name, final String identifier, final long timestamp, long size) {
            final ListableEntity entity = new ListableEntity() {
                @Override
                public String getName() {
                    return name;
                }

                @Override
                public String getIdentifier() {
                    return identifier;
                }

                @Override
                public long getTimestamp() {
                    return timestamp;
                }

                @Override
                public long getSize() {
                    return size;
                }
            };
            entities.put(entity.getIdentifier(), entity);
        }

        @Override
        protected Map<String, String> createAttributes(final ListableEntity entity, final ProcessContext context) {
            final Map<String, String> attributes = new HashMap<>();
            attributes.put(FILENAME.key(), entity.getIdentifier());
            return attributes;
        }

        @Override
        protected String getPath(final ProcessContext context) {
            return "/path";
        }

        @Override
        protected List<ListableEntity> performListing(final ProcessContext context, final Long minTimestamp) throws IOException {
            return getEntityList();
        }

        List<ListableEntity> getEntityList() {
            return entities.values().stream().sorted(Comparator.comparing(ListableEntity::getTimestamp)).collect(Collectors.toList());
        }

        @Override
        protected boolean isListingResetNecessary(PropertyDescriptor property) {
            return TestAbstractListProcessor.ConcreteListProcessor.RESET_STATE.equals(property);
        }

        @Override
        protected Scope getStateScope(final PropertyContext context) {
            return Scope.CLUSTER;
        }
    }
}

