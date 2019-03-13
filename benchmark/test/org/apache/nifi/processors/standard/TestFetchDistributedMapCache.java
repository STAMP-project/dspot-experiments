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
package org.apache.nifi.processors.standard;


import FetchDistributedMapCache.PROP_CACHE_ENTRY_IDENTIFIER;
import FetchDistributedMapCache.PROP_PUT_ATTRIBUTE_MAX_LENGTH;
import FetchDistributedMapCache.PROP_PUT_CACHE_VALUE_IN_ATTRIBUTE;
import FetchDistributedMapCache.REL_FAILURE;
import FetchDistributedMapCache.REL_NOT_FOUND;
import FetchDistributedMapCache.REL_SUCCESS;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.nifi.controller.AbstractControllerService;
import org.apache.nifi.distributed.cache.client.Deserializer;
import org.apache.nifi.distributed.cache.client.DistributedMapCacheClient;
import org.apache.nifi.distributed.cache.client.Serializer;
import org.apache.nifi.reporting.InitializationException;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.junit.Test;


public class TestFetchDistributedMapCache {
    private TestRunner runner;

    private TestFetchDistributedMapCache.MockCacheClient service;

    @Test
    public void testNoCacheKey() throws InitializationException {
        runner.setProperty(PROP_CACHE_ENTRY_IDENTIFIER, "${cacheKeyAttribute}");
        final Map<String, String> props = new HashMap<>();
        props.put("cacheKeyAttribute", "1");
        runner.enqueue(new byte[]{  }, props);
        runner.run();
        // no cache key attribute
        runner.assertAllFlowFilesTransferred(REL_NOT_FOUND, 1);
        runner.assertTransferCount(REL_NOT_FOUND, 1);
        runner.clearTransferState();
    }

    @Test
    public void testNoCacheKeyValue() throws InitializationException {
        runner.setProperty(PROP_CACHE_ENTRY_IDENTIFIER, "${cacheKeyAttribute}");
        runner.enqueue(new byte[]{  });
        runner.run();
        // Cache key attribute evaluated to empty
        runner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
        runner.assertTransferCount(REL_FAILURE, 1);
        runner.clearTransferState();
    }

    @Test
    public void testFailingCacheService() throws IOException, InitializationException {
        service.setFailOnCalls(true);
        runner.setProperty(PROP_CACHE_ENTRY_IDENTIFIER, "${cacheKeyAttribute}");
        final Map<String, String> props = new HashMap<>();
        props.put("cacheKeyAttribute", "2");
        runner.enqueue(new byte[]{  }, props);
        runner.run();
        // Expect the processor to receive an IO exception from the cache service and route to failure
        runner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
        runner.assertTransferCount(REL_FAILURE, 1);
        service.setFailOnCalls(false);
    }

    @Test
    public void testSingleFlowFile() throws IOException, InitializationException {
        service.put("key", "value", new FetchDistributedMapCache.StringSerializer(), new FetchDistributedMapCache.StringSerializer());
        runner.setProperty(PROP_CACHE_ENTRY_IDENTIFIER, "${cacheKeyAttribute}");
        final Map<String, String> props = new HashMap<>();
        props.put("cacheKeyAttribute", "key");
        String flowFileContent = "content";
        runner.enqueue(flowFileContent.getBytes("UTF-8"), props);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        runner.assertTransferCount(REL_SUCCESS, 1);
        final MockFlowFile outputFlowFile = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        outputFlowFile.assertContentEquals("value");
        runner.clearTransferState();
    }

    @Test
    public void testSingleFlowFileToAttribute() throws IOException, InitializationException {
        service.put("key", "value", new FetchDistributedMapCache.StringSerializer(), new FetchDistributedMapCache.StringSerializer());
        runner.setProperty(PROP_CACHE_ENTRY_IDENTIFIER, "${cacheKeyAttribute}");
        runner.setProperty(PROP_PUT_CACHE_VALUE_IN_ATTRIBUTE, "test");
        final Map<String, String> props = new HashMap<>();
        props.put("cacheKeyAttribute", "key");
        String flowFileContent = "content";
        runner.enqueue(flowFileContent.getBytes("UTF-8"), props);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        runner.assertTransferCount(REL_SUCCESS, 1);
        final MockFlowFile outputFlowFile = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        outputFlowFile.assertAttributeEquals("test", "value");
        runner.clearTransferState();
    }

    @Test
    public void testToAttributeTooLong() throws IOException, InitializationException {
        service.put("key", "value", new FetchDistributedMapCache.StringSerializer(), new FetchDistributedMapCache.StringSerializer());
        runner.setProperty(PROP_CACHE_ENTRY_IDENTIFIER, "${cacheKeyAttribute}");
        runner.setProperty(PROP_PUT_CACHE_VALUE_IN_ATTRIBUTE, "test");
        runner.setProperty(PROP_PUT_ATTRIBUTE_MAX_LENGTH, "3");
        final Map<String, String> props = new HashMap<>();
        props.put("cacheKeyAttribute", "key");
        String flowFileContent = "content";
        runner.enqueue(flowFileContent.getBytes("UTF-8"), props);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        runner.assertTransferCount(REL_SUCCESS, 1);
        final MockFlowFile outputFlowFile = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        outputFlowFile.assertAttributeEquals("test", "val");
        runner.clearTransferState();
    }

    @Test
    public void testMultipleKeysToAttributes() throws IOException, InitializationException {
        service.put("key1", "value1", new FetchDistributedMapCache.StringSerializer(), new FetchDistributedMapCache.StringSerializer());
        service.put("key2", "value2", new FetchDistributedMapCache.StringSerializer(), new FetchDistributedMapCache.StringSerializer());
        runner.setProperty(PROP_CACHE_ENTRY_IDENTIFIER, "key1, key2");
        // Not valid to set multiple keys without Put Cache Value In Attribute set
        runner.assertNotValid();
        runner.setProperty(PROP_PUT_CACHE_VALUE_IN_ATTRIBUTE, "test");
        runner.assertValid();
        final Map<String, String> props = new HashMap<>();
        runner.enqueue(new byte[]{  }, props);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        runner.assertTransferCount(REL_SUCCESS, 1);
        final MockFlowFile outputFlowFile = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        outputFlowFile.assertAttributeEquals("test.key1", "value1");
        outputFlowFile.assertAttributeEquals("test.key2", "value2");
    }

    @Test
    public void testMultipleKeysOneNotFound() throws IOException, InitializationException {
        service.put("key1", "value1", new FetchDistributedMapCache.StringSerializer(), new FetchDistributedMapCache.StringSerializer());
        runner.setProperty(PROP_CACHE_ENTRY_IDENTIFIER, "key1, key2");
        // Not valid to set multiple keys without Put Cache Value In Attribute set
        runner.assertNotValid();
        runner.setProperty(PROP_PUT_CACHE_VALUE_IN_ATTRIBUTE, "test");
        runner.assertValid();
        final Map<String, String> props = new HashMap<>();
        runner.enqueue(new byte[]{  }, props);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_NOT_FOUND, 1);
        runner.assertTransferCount(REL_NOT_FOUND, 1);
        final MockFlowFile outputFlowFile = runner.getFlowFilesForRelationship(REL_NOT_FOUND).get(0);
        outputFlowFile.assertAttributeEquals("test.key1", "value1");
    }

    private class MockCacheClient extends AbstractControllerService implements DistributedMapCacheClient {
        private final ConcurrentMap<Object, Object> values = new ConcurrentHashMap<>();

        private boolean failOnCalls = false;

        public void setFailOnCalls(boolean failOnCalls) {
            this.failOnCalls = failOnCalls;
        }

        private void verifyNotFail() throws IOException {
            if (failOnCalls) {
                throw new IOException("Could not call to remote service because Unit Test marked service unavailable");
            }
        }

        @Override
        public <K, V> boolean putIfAbsent(final K key, final V value, final Serializer<K> keySerializer, final Serializer<V> valueSerializer) throws IOException {
            verifyNotFail();
            final Object retValue = values.putIfAbsent(key, value);
            return retValue == null;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <K, V> V getAndPutIfAbsent(final K key, final V value, final Serializer<K> keySerializer, final Serializer<V> valueSerializer, final Deserializer<V> valueDeserializer) throws IOException {
            verifyNotFail();
            return ((V) (values.putIfAbsent(key, value)));
        }

        @Override
        public <K> boolean containsKey(final K key, final Serializer<K> keySerializer) throws IOException {
            verifyNotFail();
            return values.containsKey(key);
        }

        @Override
        public <K, V> void put(final K key, final V value, final Serializer<K> keySerializer, final Serializer<V> valueSerializer) throws IOException {
            verifyNotFail();
            values.put(key, value);
        }

        @Override
        @SuppressWarnings("unchecked")
        public <K, V> V get(final K key, final Serializer<K> keySerializer, final Deserializer<V> valueDeserializer) throws IOException {
            verifyNotFail();
            if (values.containsKey(key)) {
                return ((V) (((String) (values.get(key))).getBytes()));
            } else {
                return null;
            }
        }

        @Override
        public void close() throws IOException {
        }

        @Override
        public <K> boolean remove(final K key, final Serializer<K> serializer) throws IOException {
            verifyNotFail();
            values.remove(key);
            return true;
        }

        @Override
        public long removeByPattern(String regex) throws IOException {
            verifyNotFail();
            final List<Object> removedRecords = new ArrayList<>();
            Pattern p = Pattern.compile(regex);
            for (Object key : values.keySet()) {
                // Key must be backed by something that can be converted into a String
                Matcher m = p.matcher(key.toString());
                if (m.matches()) {
                    removedRecords.add(values.get(key));
                }
            }
            final long numRemoved = removedRecords.size();
            removedRecords.forEach(values::remove);
            return numRemoved;
        }
    }
}

