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
package org.apache.nifi.distributed.cache.server;


import DistributedMapCacheClientService.COMMUNICATIONS_TIMEOUT;
import DistributedMapCacheClientService.HOSTNAME;
import DistributedMapCacheClientService.PORT;
import DistributedSetCacheServer.EVICTION_POLICY;
import DistributedSetCacheServer.EVICTION_STRATEGY_FIFO;
import DistributedSetCacheServer.EVICTION_STRATEGY_LFU;
import DistributedSetCacheServer.MAX_CACHE_ENTRIES;
import DistributedSetCacheServer.PERSISTENCE_PATH;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ConnectException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.net.ssl.SSLContext;
import org.apache.commons.lang3.SerializationException;
import org.apache.commons.lang3.SystemUtils;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.distributed.cache.client.AtomicCacheEntry;
import org.apache.nifi.distributed.cache.client.Deserializer;
import org.apache.nifi.distributed.cache.client.DistributedMapCacheClientService;
import org.apache.nifi.distributed.cache.client.DistributedSetCacheClientService;
import org.apache.nifi.distributed.cache.client.Serializer;
import org.apache.nifi.distributed.cache.client.exception.DeserializationException;
import org.apache.nifi.distributed.cache.server.map.DistributedMapCacheServer;
import org.apache.nifi.distributed.cache.server.map.MapCacheServer;
import org.apache.nifi.processor.Processor;
import org.apache.nifi.remote.StandardVersionNegotiator;
import org.apache.nifi.reporting.InitializationException;
import org.apache.nifi.util.MockConfigurationContext;
import org.apache.nifi.util.MockControllerServiceInitializationContext;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestServerAndClient {
    private static Logger LOGGER;

    static {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "info");
        System.setProperty("org.slf4j.simpleLogger.showDateTime", "true");
        System.setProperty("org.slf4j.simpleLogger.log.nifi.distributed.cache.server.AbstractCacheServer", "debug");
        System.setProperty("org.slf4j.simpleLogger.log.nifi.distributed.cache.client.DistributedMapCacheClientService", "debug");
        System.setProperty("org.slf4j.simpleLogger.log.nifi.distributed.cache.server.TestServerAndClient", "debug");
        System.setProperty("org.slf4j.simpleLogger.log.nifi.remote.io.socket.ssl.SSLSocketChannel", "trace");
        TestServerAndClient.LOGGER = LoggerFactory.getLogger(TestServerAndClient.class);
    }

    @Test
    public void testNonPersistentSetServerAndClient() throws IOException, InitializationException {
        /**
         * This bypasses the test for build environments in OS X running Java 1.8 due to a JVM bug
         * See:  https://issues.apache.org/jira/browse/NIFI-437
         */
        Assume.assumeFalse("test is skipped due to build environment being OS X with JDK 1.8. See https://issues.apache.org/jira/browse/NIFI-437", ((SystemUtils.IS_OS_MAC) && (SystemUtils.IS_JAVA_1_8)));
        TestServerAndClient.LOGGER.info(("Testing " + (Thread.currentThread().getStackTrace()[1].getMethodName())));
        // Create server
        final TestRunner runner = TestRunners.newTestRunner(Mockito.mock(Processor.class));
        final DistributedSetCacheServer server = new TestServerAndClient.SetServer();
        runner.addControllerService("server", server);
        runner.enableControllerService(server);
        final DistributedSetCacheClientService client = createClient(server.getPort());
        final Serializer<String> serializer = new TestServerAndClient.StringSerializer();
        final boolean added = client.addIfAbsent("test", serializer);
        Assert.assertTrue(added);
        final boolean contains = client.contains("test", serializer);
        Assert.assertTrue(contains);
        final boolean addedAgain = client.addIfAbsent("test", serializer);
        Assert.assertFalse(addedAgain);
        final boolean removed = client.remove("test", serializer);
        Assert.assertTrue(removed);
        final boolean containedAfterRemove = client.contains("test", serializer);
        Assert.assertFalse(containedAfterRemove);
        server.shutdownServer();
    }

    @Test
    public void testPersistentSetServerAndClient() throws IOException, InitializationException {
        /**
         * This bypasses the test for build environments in OS X running Java 1.8 due to a JVM bug
         * See:  https://issues.apache.org/jira/browse/NIFI-437
         */
        Assume.assumeFalse("test is skipped due to build environment being OS X with JDK 1.8. See https://issues.apache.org/jira/browse/NIFI-437", ((SystemUtils.IS_OS_MAC) && (SystemUtils.IS_JAVA_1_8)));
        TestServerAndClient.LOGGER.info(("Testing " + (Thread.currentThread().getStackTrace()[1].getMethodName())));
        final File dataFile = new File("target/cache-data");
        TestServerAndClient.deleteRecursively(dataFile);
        // Create server
        final TestRunner runner = TestRunners.newTestRunner(Mockito.mock(Processor.class));
        final DistributedSetCacheServer server = new TestServerAndClient.SetServer();
        runner.addControllerService("server", server);
        runner.setProperty(server, PERSISTENCE_PATH, dataFile.getAbsolutePath());
        runner.enableControllerService(server);
        DistributedSetCacheClientService client = createClient(server.getPort());
        final Serializer<String> serializer = new TestServerAndClient.StringSerializer();
        final boolean added = client.addIfAbsent("test", serializer);
        final boolean added2 = client.addIfAbsent("test2", serializer);
        Assert.assertTrue(added);
        Assert.assertTrue(added2);
        final boolean contains = client.contains("test", serializer);
        final boolean contains2 = client.contains("test2", serializer);
        Assert.assertTrue(contains);
        Assert.assertTrue(contains2);
        final boolean addedAgain = client.addIfAbsent("test", serializer);
        Assert.assertFalse(addedAgain);
        final boolean removed = client.remove("test", serializer);
        Assert.assertTrue(removed);
        final boolean containedAfterRemove = client.contains("test", serializer);
        Assert.assertFalse(containedAfterRemove);
        server.shutdownServer();
        client.close();
        final DistributedSetCacheServer newServer = new TestServerAndClient.SetServer();
        runner.addControllerService("server2", newServer);
        runner.setProperty(newServer, PERSISTENCE_PATH, dataFile.getAbsolutePath());
        runner.enableControllerService(newServer);
        client = createClient(newServer.getPort());
        Assert.assertFalse(client.contains("test", serializer));
        Assert.assertTrue(client.contains("test2", serializer));
        newServer.shutdownServer();
        client.close();
    }

    @Test
    public void testPersistentSetServerAndClientWithLFUEvictions() throws IOException, InitializationException {
        /**
         * This bypasses the test for build environments in OS X running Java 1.8 due to a JVM bug
         * See:  https://issues.apache.org/jira/browse/NIFI-437
         */
        Assume.assumeFalse("test is skipped due to build environment being OS X with JDK 1.8. See https://issues.apache.org/jira/browse/NIFI-437", ((SystemUtils.IS_OS_MAC) && (SystemUtils.IS_JAVA_1_8)));
        TestServerAndClient.LOGGER.info(("Testing " + (Thread.currentThread().getStackTrace()[1].getMethodName())));
        // Create server
        final File dataFile = new File("target/cache-data");
        TestServerAndClient.deleteRecursively(dataFile);
        // Create server
        final TestRunner runner = TestRunners.newTestRunner(Mockito.mock(Processor.class));
        final DistributedSetCacheServer server = new TestServerAndClient.SetServer();
        runner.addControllerService("server", server);
        runner.setProperty(server, PERSISTENCE_PATH, dataFile.getAbsolutePath());
        runner.setProperty(server, MAX_CACHE_ENTRIES, "3");
        runner.setProperty(server, EVICTION_POLICY, EVICTION_STRATEGY_LFU);
        runner.enableControllerService(server);
        DistributedSetCacheClientService client = createClient(server.getPort());
        final Serializer<String> serializer = new TestServerAndClient.StringSerializer();
        final boolean added = client.addIfAbsent("test", serializer);
        waitABit();
        final boolean added2 = client.addIfAbsent("test2", serializer);
        waitABit();
        final boolean added3 = client.addIfAbsent("test3", serializer);
        waitABit();
        Assert.assertTrue(added);
        Assert.assertTrue(added2);
        Assert.assertTrue(added3);
        final boolean contains = client.contains("test", serializer);
        final boolean contains2 = client.contains("test2", serializer);
        Assert.assertTrue(contains);
        Assert.assertTrue(contains2);
        final boolean addedAgain = client.addIfAbsent("test", serializer);
        Assert.assertFalse(addedAgain);
        final boolean added4 = client.addIfAbsent("test4", serializer);
        Assert.assertTrue(added4);
        // ensure that added3 was evicted because it was used least frequently
        Assert.assertFalse(client.contains("test3", serializer));
        server.shutdownServer();
        final DistributedSetCacheServer newServer = new TestServerAndClient.SetServer();
        runner.addControllerService("server2", newServer);
        runner.setProperty(newServer, PERSISTENCE_PATH, dataFile.getAbsolutePath());
        runner.enableControllerService(newServer);
        client.close();
        client = createClient(newServer.getPort());
        Assert.assertTrue(client.contains("test", serializer));
        Assert.assertTrue(client.contains("test2", serializer));
        Assert.assertFalse(client.contains("test3", serializer));
        Assert.assertTrue(client.contains("test4", serializer));
        newServer.shutdownServer();
        client.close();
    }

    @Test
    public void testPersistentMapServerAndClientWithLFUEvictions() throws IOException, InitializationException {
        /**
         * This bypasses the test for build environments in OS X running Java 1.8 due to a JVM bug
         * See:  https://issues.apache.org/jira/browse/NIFI-437
         */
        Assume.assumeFalse("test is skipped due to build environment being OS X with JDK 1.8. See https://issues.apache.org/jira/browse/NIFI-437", ((SystemUtils.IS_OS_MAC) && (SystemUtils.IS_JAVA_1_8)));
        TestServerAndClient.LOGGER.info(("Testing " + (Thread.currentThread().getStackTrace()[1].getMethodName())));
        // Create server
        final File dataFile = new File("target/cache-data");
        TestServerAndClient.deleteRecursively(dataFile);
        // Create server
        final TestRunner runner = TestRunners.newTestRunner(Mockito.mock(Processor.class));
        final DistributedMapCacheServer server = new TestServerAndClient.MapServer();
        runner.addControllerService("server", server);
        runner.setProperty(server, DistributedMapCacheServer.PERSISTENCE_PATH, dataFile.getAbsolutePath());
        runner.setProperty(server, DistributedMapCacheServer.MAX_CACHE_ENTRIES, "3");
        runner.setProperty(server, DistributedMapCacheServer.EVICTION_POLICY, DistributedMapCacheServer.EVICTION_STRATEGY_LFU);
        runner.enableControllerService(server);
        DistributedMapCacheClientService client = createMapClient(server.getPort());
        final Serializer<String> serializer = new TestServerAndClient.StringSerializer();
        final boolean added = client.putIfAbsent("test", "1", serializer, serializer);
        waitABit();
        final boolean added2 = client.putIfAbsent("test2", "2", serializer, serializer);
        waitABit();
        final boolean added3 = client.putIfAbsent("test3", "3", serializer, serializer);
        waitABit();
        Assert.assertTrue(added);
        Assert.assertTrue(added2);
        Assert.assertTrue(added3);
        final boolean contains = client.containsKey("test", serializer);
        final boolean contains2 = client.containsKey("test2", serializer);
        Assert.assertTrue(contains);
        Assert.assertTrue(contains2);
        final Deserializer<String> deserializer = new TestServerAndClient.StringDeserializer();
        final Set<String> keys = client.keySet(deserializer);
        Assert.assertEquals(3, keys.size());
        Assert.assertTrue(keys.contains("test"));
        Assert.assertTrue(keys.contains("test2"));
        Assert.assertTrue(keys.contains("test3"));
        final boolean addedAgain = client.putIfAbsent("test", "1", serializer, serializer);
        Assert.assertFalse(addedAgain);
        final boolean added4 = client.putIfAbsent("test4", "4", serializer, serializer);
        Assert.assertTrue(added4);
        // ensure that added3 was evicted because it was used least frequently
        Assert.assertFalse(client.containsKey("test3", serializer));
        server.shutdownServer();
        final DistributedMapCacheServer newServer = new TestServerAndClient.MapServer();
        runner.addControllerService("server2", newServer);
        runner.setProperty(newServer, DistributedMapCacheServer.PERSISTENCE_PATH, dataFile.getAbsolutePath());
        runner.enableControllerService(newServer);
        client.close();
        client = createMapClient(newServer.getPort());
        Assert.assertTrue(client.containsKey("test", serializer));
        Assert.assertTrue(client.containsKey("test2", serializer));
        Assert.assertFalse(client.containsKey("test3", serializer));
        Assert.assertTrue(client.containsKey("test4", serializer));
        // Test removeByPattern, the first two should be removed and the last should remain
        client.put("test.1", "1", serializer, serializer);
        client.put("test.2", "2", serializer, serializer);
        client.put("test3", "2", serializer, serializer);
        final long removedTwo = client.removeByPattern("test\\..*");
        Assert.assertEquals(2L, removedTwo);
        Assert.assertFalse(client.containsKey("test.1", serializer));
        Assert.assertFalse(client.containsKey("test.2", serializer));
        Assert.assertTrue(client.containsKey("test3", serializer));
        // test removeByPatternAndGet
        client.put("test.1", "1", serializer, serializer);
        client.put("test.2", "2", serializer, serializer);
        Map<String, String> removed = client.removeByPatternAndGet("test\\..*", deserializer, deserializer);
        Assert.assertEquals(2, removed.size());
        Assert.assertTrue(removed.containsKey("test.1"));
        Assert.assertTrue(removed.containsKey("test.2"));
        Assert.assertFalse(client.containsKey("test.1", serializer));
        Assert.assertFalse(client.containsKey("test.2", serializer));
        Assert.assertTrue(client.containsKey("test3", serializer));
        removed = client.removeByPatternAndGet("test\\..*", deserializer, deserializer);
        Assert.assertEquals(0, removed.size());
        newServer.shutdownServer();
        client.close();
    }

    @Test
    public void testPersistentSetServerAndClientWithFIFOEvictions() throws IOException, InitializationException {
        /**
         * This bypasses the test for build environments in OS X running Java 1.8 due to a JVM bug
         * See:  https://issues.apache.org/jira/browse/NIFI-437
         */
        Assume.assumeFalse("test is skipped due to build environment being OS X with JDK 1.8. See https://issues.apache.org/jira/browse/NIFI-437", ((SystemUtils.IS_OS_MAC) && (SystemUtils.IS_JAVA_1_8)));
        TestServerAndClient.LOGGER.info(("Testing " + (Thread.currentThread().getStackTrace()[1].getMethodName())));
        final File dataFile = new File("target/cache-data");
        TestServerAndClient.deleteRecursively(dataFile);
        // Create server
        final TestRunner runner = TestRunners.newTestRunner(Mockito.mock(Processor.class));
        final DistributedSetCacheServer server = new TestServerAndClient.SetServer();
        runner.addControllerService("server", server);
        runner.setProperty(server, PERSISTENCE_PATH, dataFile.getAbsolutePath());
        runner.setProperty(server, MAX_CACHE_ENTRIES, "3");
        runner.setProperty(server, EVICTION_POLICY, EVICTION_STRATEGY_FIFO);
        runner.enableControllerService(server);
        DistributedSetCacheClientService client = createClient(server.getPort());
        final Serializer<String> serializer = new TestServerAndClient.StringSerializer();
        // add 3 entries to the cache. But, if we add too fast, we'll have the same millisecond
        // for the entry time so we don't know which entry will be evicted. So we wait a few millis in between
        final boolean added = client.addIfAbsent("test", serializer);
        waitABit();
        final boolean added2 = client.addIfAbsent("test2", serializer);
        waitABit();
        final boolean added3 = client.addIfAbsent("test3", serializer);
        waitABit();
        Assert.assertTrue(added);
        Assert.assertTrue(added2);
        Assert.assertTrue(added3);
        final boolean contains = client.contains("test", serializer);
        final boolean contains2 = client.contains("test2", serializer);
        Assert.assertTrue(contains);
        Assert.assertTrue(contains2);
        final boolean addedAgain = client.addIfAbsent("test", serializer);
        Assert.assertFalse(addedAgain);
        final boolean added4 = client.addIfAbsent("test4", serializer);
        Assert.assertTrue(added4);
        // ensure that added3 was evicted because it was used least frequently
        Assert.assertFalse(client.contains("test", serializer));
        Assert.assertTrue(client.contains("test3", serializer));
        server.shutdownServer();
        client.close();
        final DistributedSetCacheServer newServer = new TestServerAndClient.SetServer();
        runner.addControllerService("server2", newServer);
        runner.setProperty(newServer, PERSISTENCE_PATH, dataFile.getAbsolutePath());
        runner.setProperty(newServer, MAX_CACHE_ENTRIES, "3");
        runner.setProperty(newServer, EVICTION_POLICY, EVICTION_STRATEGY_FIFO);
        runner.enableControllerService(newServer);
        client = createClient(newServer.getPort());
        Assert.assertFalse(client.contains("test", serializer));
        Assert.assertTrue(client.contains("test2", serializer));
        Assert.assertTrue(client.contains("test3", serializer));
        Assert.assertTrue(client.contains("test4", serializer));
        newServer.shutdownServer();
        client.close();
    }

    @Test
    public void testNonPersistentMapServerAndClient() throws IOException, InterruptedException, InitializationException {
        /**
         * This bypasses the test for build environments in OS X running Java 1.8 due to a JVM bug
         * See:  https://issues.apache.org/jira/browse/NIFI-437
         */
        Assume.assumeFalse("test is skipped due to build environment being OS X with JDK 1.8. See https://issues.apache.org/jira/browse/NIFI-437", ((SystemUtils.IS_OS_MAC) && (SystemUtils.IS_JAVA_1_8)));
        TestServerAndClient.LOGGER.info(("Testing " + (Thread.currentThread().getStackTrace()[1].getMethodName())));
        // Create server
        final DistributedMapCacheServer server = new TestServerAndClient.MapServer();
        final TestRunner runner = TestRunners.newTestRunner(Mockito.mock(Processor.class));
        runner.addControllerService("server", server);
        runner.enableControllerService(server);
        DistributedMapCacheClientService client = new DistributedMapCacheClientService();
        MockControllerServiceInitializationContext clientInitContext = new MockControllerServiceInitializationContext(client, "client");
        client.initialize(clientInitContext);
        final Map<PropertyDescriptor, String> clientProperties = new HashMap<>();
        clientProperties.put(HOSTNAME, "localhost");
        clientProperties.put(PORT, String.valueOf(server.getPort()));
        clientProperties.put(COMMUNICATIONS_TIMEOUT, "360 secs");
        MockConfigurationContext clientContext = new MockConfigurationContext(clientProperties, clientInitContext.getControllerServiceLookup());
        client.cacheConfig(clientContext);
        final Serializer<String> valueSerializer = new TestServerAndClient.StringSerializer();
        final Serializer<String> keySerializer = new TestServerAndClient.StringSerializer();
        final Deserializer<String> deserializer = new TestServerAndClient.StringDeserializer();
        final String original = client.getAndPutIfAbsent("testKey", "test", keySerializer, valueSerializer, deserializer);
        Assert.assertEquals(null, original);
        TestServerAndClient.LOGGER.debug("end getAndPutIfAbsent");
        final boolean contains = client.containsKey("testKey", keySerializer);
        Assert.assertTrue(contains);
        TestServerAndClient.LOGGER.debug("end containsKey");
        final boolean added = client.putIfAbsent("testKey", "test", keySerializer, valueSerializer);
        Assert.assertFalse(added);
        TestServerAndClient.LOGGER.debug("end putIfAbsent");
        final String originalAfterPut = client.getAndPutIfAbsent("testKey", "test", keySerializer, valueSerializer, deserializer);
        Assert.assertEquals("test", originalAfterPut);
        TestServerAndClient.LOGGER.debug("end getAndPutIfAbsent");
        final boolean removed = client.remove("testKey", keySerializer);
        Assert.assertTrue(removed);
        TestServerAndClient.LOGGER.debug("end remove");
        client.put("testKey", "testValue", keySerializer, valueSerializer);
        Assert.assertTrue(client.containsKey("testKey", keySerializer));
        String removedValue = client.removeAndGet("testKey", keySerializer, deserializer);
        Assert.assertEquals("testValue", removedValue);
        removedValue = client.removeAndGet("testKey", keySerializer, deserializer);
        Assert.assertNull(removedValue);
        final Set<String> keys = client.keySet(deserializer);
        Assert.assertEquals(0, keys.size());
        // Test removeByPattern, the first two should be removed and the last should remain
        client.put("test.1", "1", keySerializer, keySerializer);
        client.put("test.2", "2", keySerializer, keySerializer);
        client.put("test3", "2", keySerializer, keySerializer);
        final long removedTwo = client.removeByPattern("test\\..*");
        Assert.assertEquals(2L, removedTwo);
        Assert.assertFalse(client.containsKey("test.1", keySerializer));
        Assert.assertFalse(client.containsKey("test.2", keySerializer));
        Assert.assertTrue(client.containsKey("test3", keySerializer));
        final boolean containedAfterRemove = client.containsKey("testKey", keySerializer);
        Assert.assertFalse(containedAfterRemove);
        client.putIfAbsent("testKey", "test", keySerializer, valueSerializer);
        client.close();
        try {
            client.containsKey("testKey", keySerializer);
            Assert.fail("Should be closed and not accessible");
        } catch (final Exception e) {
        }
        DistributedMapCacheClientService client2 = new DistributedMapCacheClientService();
        MockControllerServiceInitializationContext clientInitContext2 = new MockControllerServiceInitializationContext(client2, "client2");
        client2.initialize(clientInitContext2);
        MockConfigurationContext clientContext2 = new MockConfigurationContext(clientProperties, clientInitContext2.getControllerServiceLookup());
        client2.cacheConfig(clientContext2);
        Assert.assertFalse(client2.putIfAbsent("testKey", "test", keySerializer, valueSerializer));
        Assert.assertTrue(client2.containsKey("testKey", keySerializer));
        server.shutdownServer();
        Thread.sleep(1000);
        try {
            client2.containsKey("testKey", keySerializer);
            Assert.fail("Should have blown exception!");
        } catch (final ConnectException e) {
            client2 = null;
            clientContext2 = null;
            clientInitContext2 = null;
        }
        TestServerAndClient.LOGGER.debug("end testNonPersistentMapServerAndClient");
    }

    @Test
    public void testClientTermination() throws IOException, InterruptedException, InitializationException {
        /**
         * This bypasses the test for build environments in OS X running Java 1.8 due to a JVM bug
         * See:  https://issues.apache.org/jira/browse/NIFI-437
         */
        Assume.assumeFalse("test is skipped due to build environment being OS X with JDK 1.8. See https://issues.apache.org/jira/browse/NIFI-437", ((SystemUtils.IS_OS_MAC) && (SystemUtils.IS_JAVA_1_8)));
        TestServerAndClient.LOGGER.info(("Testing " + (Thread.currentThread().getStackTrace()[1].getMethodName())));
        // Create server
        final DistributedMapCacheServer server = new TestServerAndClient.MapServer();
        final MockControllerServiceInitializationContext serverInitContext = new MockControllerServiceInitializationContext(server, "server");
        server.initialize(serverInitContext);
        final Map<PropertyDescriptor, String> serverProperties = new HashMap<>();
        final MockConfigurationContext serverContext = new MockConfigurationContext(serverProperties, serverInitContext.getControllerServiceLookup());
        server.startServer(serverContext);
        DistributedMapCacheClientService client = new DistributedMapCacheClientService();
        MockControllerServiceInitializationContext clientInitContext = new MockControllerServiceInitializationContext(client, "client");
        client.initialize(clientInitContext);
        final Map<PropertyDescriptor, String> clientProperties = new HashMap<>();
        clientProperties.put(HOSTNAME, "localhost");
        clientProperties.put(COMMUNICATIONS_TIMEOUT, "360 secs");
        MockConfigurationContext clientContext = new MockConfigurationContext(clientProperties, clientInitContext.getControllerServiceLookup());
        client.cacheConfig(clientContext);
        final Serializer<String> valueSerializer = new TestServerAndClient.StringSerializer();
        final Serializer<String> keySerializer = new TestServerAndClient.StringSerializer();
        final Deserializer<String> deserializer = new TestServerAndClient.StringDeserializer();
        final String original = client.getAndPutIfAbsent("testKey", "test", keySerializer, valueSerializer, deserializer);
        Assert.assertEquals(null, original);
        final boolean contains = client.containsKey("testKey", keySerializer);
        Assert.assertTrue(contains);
        final boolean added = client.putIfAbsent("testKey", "test", keySerializer, valueSerializer);
        Assert.assertFalse(added);
        final String originalAfterPut = client.getAndPutIfAbsent("testKey", "test", keySerializer, valueSerializer, deserializer);
        Assert.assertEquals("test", originalAfterPut);
        final boolean removed = client.remove("testKey", keySerializer);
        Assert.assertTrue(removed);
        final boolean containedAfterRemove = client.containsKey("testKey", keySerializer);
        Assert.assertFalse(containedAfterRemove);
        client = null;
        clientInitContext = null;
        clientContext = null;
        Thread.sleep(2000);
        System.gc();
        server.shutdownServer();
    }

    @Test
    public void testOptimisticLock() throws Exception {
        /**
         * This bypasses the test for build environments in OS X running Java 1.8 due to a JVM bug
         * See:  https://issues.apache.org/jira/browse/NIFI-437
         */
        Assume.assumeFalse("test is skipped due to build environment being OS X with JDK 1.8. See https://issues.apache.org/jira/browse/NIFI-437", ((SystemUtils.IS_OS_MAC) && (SystemUtils.IS_JAVA_1_8)));
        TestServerAndClient.LOGGER.info(("Testing " + (Thread.currentThread().getStackTrace()[1].getMethodName())));
        // Create server
        final DistributedMapCacheServer server = new TestServerAndClient.MapServer();
        final TestRunner runner = TestRunners.newTestRunner(Mockito.mock(Processor.class));
        runner.addControllerService("server", server);
        runner.enableControllerService(server);
        DistributedMapCacheClientService client1 = new DistributedMapCacheClientService();
        MockControllerServiceInitializationContext clientInitContext1 = new MockControllerServiceInitializationContext(client1, "client1");
        client1.initialize(clientInitContext1);
        DistributedMapCacheClientService client2 = new DistributedMapCacheClientService();
        MockControllerServiceInitializationContext clientInitContext2 = new MockControllerServiceInitializationContext(client2, "client2");
        client1.initialize(clientInitContext2);
        final Map<PropertyDescriptor, String> clientProperties = new HashMap<>();
        clientProperties.put(HOSTNAME, "localhost");
        clientProperties.put(PORT, String.valueOf(server.getPort()));
        clientProperties.put(COMMUNICATIONS_TIMEOUT, "360 secs");
        MockConfigurationContext clientContext1 = new MockConfigurationContext(clientProperties, clientInitContext1.getControllerServiceLookup());
        client1.cacheConfig(clientContext1);
        MockConfigurationContext clientContext2 = new MockConfigurationContext(clientProperties, clientInitContext2.getControllerServiceLookup());
        client2.cacheConfig(clientContext2);
        final Serializer<String> stringSerializer = new TestServerAndClient.StringSerializer();
        final Deserializer<String> stringDeserializer = new TestServerAndClient.StringDeserializer();
        final String key = "test-optimistic-lock";
        // Ensure there's no existing key
        Assert.assertFalse(client1.containsKey(key, stringSerializer));
        Assert.assertNull(client1.fetch(key, stringSerializer, stringDeserializer));
        // Client 1 inserts the key.
        client1.put(key, "valueC1-0", stringSerializer, stringSerializer);
        // Client 1 and 2 fetch the key
        AtomicCacheEntry<String, String, Long> c1 = client1.fetch(key, stringSerializer, stringDeserializer);
        AtomicCacheEntry<String, String, Long> c2 = client2.fetch(key, stringSerializer, stringDeserializer);
        Assert.assertEquals(new Long(0), c1.getRevision().orElse(0L));
        Assert.assertEquals("valueC1-0", c1.getValue());
        Assert.assertEquals(new Long(0), c2.getRevision().orElse(0L));
        Assert.assertEquals("valueC1-0", c2.getValue());
        // Client 1 replace
        c1.setValue("valueC1-1");
        boolean c1Result = client1.replace(c1, stringSerializer, stringSerializer);
        Assert.assertTrue("C1 should be able to replace the key", c1Result);
        // Client 2 replace with the old revision
        c2.setValue("valueC2-1");
        boolean c2Result = client2.replace(c2, stringSerializer, stringSerializer);
        Assert.assertFalse("C2 shouldn't be able to replace the key", c2Result);
        // Client 2 fetch the key again
        c2 = client2.fetch(key, stringSerializer, stringDeserializer);
        Assert.assertEquals("valueC1-1", c2.getValue());
        Assert.assertEquals(new Long(1), c2.getRevision().orElse(0L));
        // Now, Client 2 knows the correct revision so it can replace the key
        c2.setValue("valueC2-2");
        c2Result = client2.replace(c2, stringSerializer, stringSerializer);
        Assert.assertTrue("C2 should be able to replace the key", c2Result);
        // Assert the cache
        c2 = client2.fetch(key, stringSerializer, stringDeserializer);
        Assert.assertEquals("valueC2-2", c2.getValue());
        Assert.assertEquals(new Long(2), c2.getRevision().orElse(0L));
        client1.close();
        client2.close();
        server.shutdownServer();
    }

    @Test
    public void testBackwardCompatibility() throws Exception {
        /**
         * This bypasses the test for build environments in OS X running Java 1.8 due to a JVM bug
         * See:  https://issues.apache.org/jira/browse/NIFI-437
         */
        Assume.assumeFalse("test is skipped due to build environment being OS X with JDK 1.8. See https://issues.apache.org/jira/browse/NIFI-437", ((SystemUtils.IS_OS_MAC) && (SystemUtils.IS_JAVA_1_8)));
        TestServerAndClient.LOGGER.info(("Testing " + (Thread.currentThread().getStackTrace()[1].getMethodName())));
        final TestRunner runner = TestRunners.newTestRunner(Mockito.mock(Processor.class));
        // Create a server that only supports protocol version 1.
        final DistributedMapCacheServer server = new TestServerAndClient.MapServer() {
            @Override
            protected MapCacheServer createMapCacheServer(int port, int maxSize, SSLContext sslContext, EvictionPolicy evictionPolicy, File persistenceDir) throws IOException {
                return new MapCacheServer(getIdentifier(), sslContext, port, maxSize, evictionPolicy, persistenceDir) {
                    @Override
                    protected StandardVersionNegotiator getVersionNegotiator() {
                        return new StandardVersionNegotiator(1);
                    }
                };
            }
        };
        runner.addControllerService("server", server);
        runner.enableControllerService(server);
        DistributedMapCacheClientService client = new DistributedMapCacheClientService();
        MockControllerServiceInitializationContext clientInitContext1 = new MockControllerServiceInitializationContext(client, "client");
        client.initialize(clientInitContext1);
        final Map<PropertyDescriptor, String> clientProperties = new HashMap<>();
        clientProperties.put(HOSTNAME, "localhost");
        clientProperties.put(PORT, String.valueOf(server.getPort()));
        clientProperties.put(COMMUNICATIONS_TIMEOUT, "360 secs");
        MockConfigurationContext clientContext = new MockConfigurationContext(clientProperties, clientInitContext1.getControllerServiceLookup());
        client.cacheConfig(clientContext);
        final Serializer<String> stringSerializer = new TestServerAndClient.StringSerializer();
        final Deserializer<String> stringDeserializer = new TestServerAndClient.StringDeserializer();
        final String key = "test-backward-compatibility";
        // Version 1 operations should work
        client.put(key, "value1", stringSerializer, stringSerializer);
        Assert.assertEquals("value1", client.get(key, stringSerializer, stringDeserializer));
        Assert.assertTrue(client.containsKey(key, stringSerializer));
        try {
            client.fetch(key, stringSerializer, stringDeserializer);
            Assert.fail("Version 2 operations should NOT work.");
        } catch (UnsupportedOperationException e) {
        }
        try {
            AtomicCacheEntry<String, String, Long> entry = new AtomicCacheEntry(key, "value2", 0L);
            client.replace(entry, stringSerializer, stringSerializer);
            Assert.fail("Version 2 operations should NOT work.");
        } catch (UnsupportedOperationException e) {
        }
        try {
            Set<String> keys = client.keySet(stringDeserializer);
            Assert.fail("Version 3 operations should NOT work.");
        } catch (UnsupportedOperationException e) {
        }
        try {
            String removed = client.removeAndGet("v.*", stringSerializer, stringDeserializer);
            Assert.fail("Version 3 operations should NOT work.");
        } catch (UnsupportedOperationException e) {
        }
        try {
            Map<String, String> removed = client.removeByPatternAndGet("v.*", stringDeserializer, stringDeserializer);
            Assert.fail("Version 3 operations should NOT work.");
        } catch (UnsupportedOperationException e) {
        }
        client.close();
        server.shutdownServer();
    }

    private static class StringSerializer implements Serializer<String> {
        @Override
        public void serialize(final String value, final OutputStream output) throws IOException, SerializationException {
            output.write(value.getBytes(StandardCharsets.UTF_8));
        }
    }

    private static class StringDeserializer implements Deserializer<String> {
        @Override
        public String deserialize(final byte[] input) throws IOException, DeserializationException {
            return (input.length) == 0 ? null : new String(input, StandardCharsets.UTF_8);
        }
    }

    private static class SetServer extends DistributedSetCacheServer {
        @Override
        protected List<PropertyDescriptor> getSupportedPropertyDescriptors() {
            return TestServerAndClient.replacePortDescriptor(super.getSupportedPropertyDescriptors());
        }
    }

    private static class MapServer extends DistributedMapCacheServer {
        @Override
        protected List<PropertyDescriptor> getSupportedPropertyDescriptors() {
            return TestServerAndClient.replacePortDescriptor(super.getSupportedPropertyDescriptors());
        }
    }
}

