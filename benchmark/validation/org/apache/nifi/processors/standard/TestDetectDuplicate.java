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


import DetectDuplicate.AGE_OFF_DURATION;
import DetectDuplicate.CACHE_IDENTIFIER;
import DetectDuplicate.DISTRIBUTED_CACHE_SERVICE;
import DetectDuplicate.FLOWFILE_DESCRIPTION;
import DetectDuplicate.REL_DUPLICATE;
import DetectDuplicate.REL_FAILURE;
import DetectDuplicate.REL_NON_DUPLICATE;
import DistributedMapCacheClientService.COMMUNICATIONS_TIMEOUT;
import DistributedMapCacheClientService.HOSTNAME;
import DistributedMapCacheClientService.PORT;
import DistributedMapCacheClientService.SSL_CONTEXT_SERVICE;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.controller.AbstractControllerService;
import org.apache.nifi.distributed.cache.client.Deserializer;
import org.apache.nifi.distributed.cache.client.DistributedMapCacheClient;
import org.apache.nifi.distributed.cache.client.Serializer;
import org.apache.nifi.reporting.InitializationException;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Test;


public class TestDetectDuplicate {
    static {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "info");
        System.setProperty("org.slf4j.simpleLogger.showDateTime", "true");
        System.setProperty("org.slf4j.simpleLogger.log.nifi.io.nio", "debug");
        System.setProperty("org.slf4j.simpleLogger.log.nifi.processors.standard.DetectDuplicate", "debug");
        System.setProperty("org.slf4j.simpleLogger.log.nifi.processors.standard.TestDetectDuplicate", "debug");
    }

    @Test
    public void testDuplicate() throws InitializationException {
        final TestRunner runner = TestRunners.newTestRunner(DetectDuplicate.class);
        final TestDetectDuplicate.DistributedMapCacheClientImpl client = createClient();
        final Map<String, String> clientProperties = new HashMap<>();
        clientProperties.put(HOSTNAME.getName(), "localhost");
        runner.addControllerService("client", client, clientProperties);
        runner.setProperty(DISTRIBUTED_CACHE_SERVICE, "client");
        runner.setProperty(FLOWFILE_DESCRIPTION, "The original flow file");
        runner.setProperty(AGE_OFF_DURATION, "48 hours");
        final Map<String, String> props = new HashMap<>();
        props.put("hash.value", "1000");
        runner.enqueue(new byte[]{  }, props);
        runner.enableControllerService(client);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_NON_DUPLICATE, 1);
        runner.clearTransferState();
        runner.enqueue(new byte[]{  }, props);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_DUPLICATE, 1);
        runner.assertTransferCount(REL_NON_DUPLICATE, 0);
        runner.assertTransferCount(REL_FAILURE, 0);
    }

    @Test
    public void testDuplicateWithAgeOff() throws InterruptedException, InitializationException {
        final TestRunner runner = TestRunners.newTestRunner(DetectDuplicate.class);
        final TestDetectDuplicate.DistributedMapCacheClientImpl client = createClient();
        final Map<String, String> clientProperties = new HashMap<>();
        clientProperties.put(HOSTNAME.getName(), "localhost");
        runner.addControllerService("client", client, clientProperties);
        runner.setProperty(DISTRIBUTED_CACHE_SERVICE, "client");
        runner.setProperty(FLOWFILE_DESCRIPTION, "The original flow file");
        runner.setProperty(AGE_OFF_DURATION, "2 secs");
        runner.enableControllerService(client);
        final Map<String, String> props = new HashMap<>();
        props.put("hash.value", "1000");
        runner.enqueue(new byte[]{  }, props);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_NON_DUPLICATE, 1);
        runner.clearTransferState();
        Thread.sleep(3000);
        runner.enqueue(new byte[]{  }, props);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_NON_DUPLICATE, 1);
        runner.assertTransferCount(REL_DUPLICATE, 0);
        runner.assertTransferCount(REL_FAILURE, 0);
    }

    @Test
    public void testDuplicateNoCache() throws InitializationException {
        final TestRunner runner = TestRunners.newTestRunner(DetectDuplicate.class);
        final TestDetectDuplicate.DistributedMapCacheClientImpl client = createClient();
        final Map<String, String> clientProperties = new HashMap<>();
        clientProperties.put(HOSTNAME.getName(), "localhost");
        runner.addControllerService("client", client, clientProperties);
        runner.setProperty(DISTRIBUTED_CACHE_SERVICE, "client");
        runner.setProperty(FLOWFILE_DESCRIPTION, "The original flow file");
        runner.setProperty(AGE_OFF_DURATION, "48 hours");
        runner.setProperty(CACHE_IDENTIFIER, "false");
        final Map<String, String> props = new HashMap<>();
        props.put("hash.value", "1000");
        runner.enqueue(new byte[]{  }, props);
        runner.enableControllerService(client);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_NON_DUPLICATE, 1);
        runner.clearTransferState();
        runner.setProperty(CACHE_IDENTIFIER, "true");
        runner.enqueue(new byte[]{  }, props);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_NON_DUPLICATE, 1);
        runner.assertTransferCount(REL_DUPLICATE, 0);
        runner.assertTransferCount(REL_FAILURE, 0);
        runner.clearTransferState();
        runner.enqueue(new byte[]{  }, props);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_DUPLICATE, 1);
        runner.assertTransferCount(REL_NON_DUPLICATE, 0);
        runner.assertTransferCount(REL_FAILURE, 0);
    }

    @Test
    public void testDuplicateNoCacheWithAgeOff() throws InterruptedException, InitializationException {
        final TestRunner runner = TestRunners.newTestRunner(DetectDuplicate.class);
        final TestDetectDuplicate.DistributedMapCacheClientImpl client = createClient();
        final Map<String, String> clientProperties = new HashMap<>();
        clientProperties.put(HOSTNAME.getName(), "localhost");
        runner.addControllerService("client", client, clientProperties);
        runner.setProperty(DISTRIBUTED_CACHE_SERVICE, "client");
        runner.setProperty(FLOWFILE_DESCRIPTION, "The original flow file");
        runner.setProperty(AGE_OFF_DURATION, "2 secs");
        runner.enableControllerService(client);
        final Map<String, String> props = new HashMap<>();
        props.put("hash.value", "1000");
        runner.enqueue(new byte[]{  }, props);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_NON_DUPLICATE, 1);
        runner.clearTransferState();
        Thread.sleep(3000);
        runner.setProperty(CACHE_IDENTIFIER, "false");
        runner.enqueue(new byte[]{  }, props);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_NON_DUPLICATE, 1);
        runner.assertTransferCount(REL_DUPLICATE, 0);
        runner.assertTransferCount(REL_FAILURE, 0);
    }

    static final class DistributedMapCacheClientImpl extends AbstractControllerService implements DistributedMapCacheClient {
        boolean exists = false;

        private Object cacheValue;

        @Override
        public void close() throws IOException {
        }

        @Override
        public void onPropertyModified(final PropertyDescriptor descriptor, final String oldValue, final String newValue) {
        }

        @Override
        protected List<PropertyDescriptor> getSupportedPropertyDescriptors() {
            final List<PropertyDescriptor> props = new ArrayList<>();
            props.add(HOSTNAME);
            props.add(COMMUNICATIONS_TIMEOUT);
            props.add(PORT);
            props.add(SSL_CONTEXT_SERVICE);
            return props;
        }

        @Override
        public <K, V> boolean putIfAbsent(final K key, final V value, final Serializer<K> keySerializer, final Serializer<V> valueSerializer) throws IOException {
            if (exists) {
                return false;
            }
            cacheValue = value;
            exists = true;
            return true;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <K, V> V getAndPutIfAbsent(final K key, final V value, final Serializer<K> keySerializer, final Serializer<V> valueSerializer, final Deserializer<V> valueDeserializer) throws IOException {
            if (exists) {
                return ((V) (cacheValue));
            }
            cacheValue = value;
            exists = true;
            return null;
        }

        @Override
        public <K> boolean containsKey(final K key, final Serializer<K> keySerializer) throws IOException {
            return exists;
        }

        @Override
        public <K, V> V get(final K key, final Serializer<K> keySerializer, final Deserializer<V> valueDeserializer) throws IOException {
            if (exists) {
                return ((V) (cacheValue));
            } else {
                return null;
            }
        }

        @Override
        public <K> boolean remove(final K key, final Serializer<K> serializer) throws IOException {
            exists = false;
            return true;
        }

        @Override
        public long removeByPattern(String regex) throws IOException {
            if (exists) {
                exists = false;
                return 1L;
            } else {
                return 0L;
            }
        }

        @Override
        public <K, V> void put(final K key, final V value, final Serializer<K> keySerializer, final Serializer<V> valueSerializer) throws IOException {
            cacheValue = value;
            exists = true;
        }
    }
}

