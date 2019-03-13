/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.streams;


import CommonClientConfigs.RECEIVE_BUFFER_CONFIG;
import CommonClientConfigs.SEND_BUFFER_CONFIG;
import ConsumerConfig.DEFAULT_API_TIMEOUT_MS_CONFIG;
import KafkaStreams.State;
import KafkaStreams.State.CREATED;
import KafkaStreams.State.ERROR;
import KafkaStreams.State.NOT_RUNNING;
import KafkaStreams.State.REBALANCING;
import KafkaStreams.State.RUNNING;
import MockMetricsReporter.CLOSE_COUNT;
import MockMetricsReporter.INIT_COUNT;
import Selectable.USE_DEFAULT_BUFFER_SIZE;
import Sensor.RecordingLevel.DEBUG;
import Sensor.RecordingLevel.INFO;
import StreamThread.State.DEAD;
import StreamThread.State.PARTITIONS_ASSIGNED;
import StreamThread.State.PARTITIONS_REVOKED;
import StreamThread.State.PENDING_SHUTDOWN;
import StreamsConfig.APPLICATION_ID_CONFIG;
import StreamsConfig.BOOTSTRAP_SERVERS_CONFIG;
import StreamsConfig.METRICS_RECORDING_LEVEL_CONFIG;
import StreamsConfig.METRIC_REPORTER_CLASSES_CONFIG;
import StreamsConfig.NUM_STREAM_THREADS_CONFIG;
import StreamsConfig.STATE_CLEANUP_DELAY_MS_CONFIG;
import StreamsConfig.STATE_DIR_CONFIG;
import java.io.File;
import java.lang.reflect.Field;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.kafka.clients.producer.MockProducer;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.Node;
import org.apache.kafka.common.config.ConfigException;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.errors.StreamsException;
import org.apache.kafka.streams.integration.utils.EmbeddedKafkaCluster;
import org.apache.kafka.streams.integration.utils.IntegrationTestUtils;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.processor.ThreadMetadata;
import org.apache.kafka.streams.processor.internals.GlobalStreamThread;
import org.apache.kafka.streams.processor.internals.StreamThread;
import org.apache.kafka.test.IntegrationTest;
import org.apache.kafka.test.MockClientSupplier;
import org.apache.kafka.test.MockMetricsReporter;
import org.apache.kafka.test.MockStateRestoreListener;
import org.apache.kafka.test.TestUtils;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;


@Category({ IntegrationTest.class })
public class KafkaStreamsTest {
    private static final int NUM_BROKERS = 1;

    private static final int NUM_THREADS = 2;

    // We need this to avoid the KafkaConsumer hanging on poll
    // (this may occur if the test doesn't complete quickly enough)
    @ClassRule
    public static final EmbeddedKafkaCluster CLUSTER = new EmbeddedKafkaCluster(KafkaStreamsTest.NUM_BROKERS);

    private final StreamsBuilder builder = new StreamsBuilder();

    private KafkaStreams globalStreams;

    private Properties props;

    @Rule
    public TestName testName = new TestName();

    @Test
    public void testOsDefaultSocketBufferSizes() {
        props.put(SEND_BUFFER_CONFIG, USE_DEFAULT_BUFFER_SIZE);
        props.put(RECEIVE_BUFFER_CONFIG, USE_DEFAULT_BUFFER_SIZE);
        final KafkaStreams streams = new KafkaStreams(builder.build(), props);
        streams.close();
    }

    @Test(expected = KafkaException.class)
    public void testInvalidSocketSendBufferSize() {
        props.put(SEND_BUFFER_CONFIG, (-2));
        final KafkaStreams streams = new KafkaStreams(builder.build(), props);
        streams.close();
    }

    @Test(expected = KafkaException.class)
    public void testInvalidSocketReceiveBufferSize() {
        props.put(RECEIVE_BUFFER_CONFIG, (-2));
        final KafkaStreams streams = new KafkaStreams(builder.build(), props);
        streams.close();
    }

    @Test
    public void testStateCloseAfterCreate() {
        globalStreams.close();
        Assert.assertEquals(NOT_RUNNING, globalStreams.state());
    }

    @Test
    public void testStateOneThreadDeadButRebalanceFinish() throws InterruptedException {
        final KafkaStreamsTest.StateListenerStub stateListener = new KafkaStreamsTest.StateListenerStub();
        globalStreams.setStateListener(stateListener);
        Assert.assertEquals(0, stateListener.numChanges);
        Assert.assertEquals(CREATED, globalStreams.state());
        globalStreams.start();
        TestUtils.waitForCondition(() -> stateListener.numChanges == 2, "Streams never started.");
        Assert.assertEquals(RUNNING, globalStreams.state());
        for (final StreamThread thread : globalStreams.threads) {
            thread.stateListener().onChange(thread, PARTITIONS_REVOKED, StreamThread.State.RUNNING);
        }
        Assert.assertEquals(3, stateListener.numChanges);
        Assert.assertEquals(REBALANCING, globalStreams.state());
        for (final StreamThread thread : globalStreams.threads) {
            thread.stateListener().onChange(thread, PARTITIONS_ASSIGNED, PARTITIONS_REVOKED);
        }
        Assert.assertEquals(3, stateListener.numChanges);
        Assert.assertEquals(REBALANCING, globalStreams.state());
        globalStreams.threads[((KafkaStreamsTest.NUM_THREADS) - 1)].stateListener().onChange(globalStreams.threads[((KafkaStreamsTest.NUM_THREADS) - 1)], PENDING_SHUTDOWN, PARTITIONS_ASSIGNED);
        globalStreams.threads[((KafkaStreamsTest.NUM_THREADS) - 1)].stateListener().onChange(globalStreams.threads[((KafkaStreamsTest.NUM_THREADS) - 1)], DEAD, PENDING_SHUTDOWN);
        Assert.assertEquals(3, stateListener.numChanges);
        Assert.assertEquals(REBALANCING, globalStreams.state());
        for (final StreamThread thread : globalStreams.threads) {
            if (thread != (globalStreams.threads[((KafkaStreamsTest.NUM_THREADS) - 1)])) {
                thread.stateListener().onChange(thread, StreamThread.State.RUNNING, PARTITIONS_ASSIGNED);
            }
        }
        Assert.assertEquals(4, stateListener.numChanges);
        Assert.assertEquals(RUNNING, globalStreams.state());
        globalStreams.close();
        TestUtils.waitForCondition(() -> stateListener.numChanges == 6, "Streams never closed.");
        Assert.assertEquals(NOT_RUNNING, globalStreams.state());
    }

    @Test
    public void shouldCleanupResourcesOnCloseWithoutPreviousStart() throws Exception {
        builder.globalTable("anyTopic");
        final List<Node> nodes = Collections.singletonList(new Node(0, "localhost", 8121));
        final Cluster cluster = new Cluster("mockClusterId", nodes, Collections.emptySet(), Collections.emptySet(), Collections.emptySet(), nodes.get(0));
        final MockClientSupplier clientSupplier = new MockClientSupplier();
        clientSupplier.setClusterForAdminClient(cluster);
        final KafkaStreams streams = new KafkaStreams(builder.build(), props, clientSupplier);
        streams.close();
        TestUtils.waitForCondition(() -> (streams.state()) == KafkaStreams.State.NOT_RUNNING, "Streams never stopped.");
        // Ensure that any created clients are closed
        Assert.assertTrue(clientSupplier.consumer.closed());
        Assert.assertTrue(clientSupplier.restoreConsumer.closed());
        for (final MockProducer p : clientSupplier.producers) {
            Assert.assertTrue(p.closed());
        }
    }

    @Test
    public void testStateThreadClose() throws Exception {
        // make sure we have the global state thread running too
        builder.globalTable("anyTopic");
        final KafkaStreams streams = new KafkaStreams(builder.build(), props);
        try {
            final Field threadsField = streams.getClass().getDeclaredField("threads");
            threadsField.setAccessible(true);
            final StreamThread[] threads = ((StreamThread[]) (threadsField.get(streams)));
            Assert.assertEquals(KafkaStreamsTest.NUM_THREADS, threads.length);
            Assert.assertEquals(streams.state(), CREATED);
            streams.start();
            TestUtils.waitForCondition(() -> (streams.state()) == KafkaStreams.State.RUNNING, "Streams never started.");
            for (int i = 0; i < (KafkaStreamsTest.NUM_THREADS); i++) {
                final StreamThread tmpThread = threads[i];
                tmpThread.shutdown();
                TestUtils.waitForCondition(() -> (tmpThread.state()) == StreamThread.State.DEAD, "Thread never stopped.");
                threads[i].join();
            }
            TestUtils.waitForCondition(() -> (streams.state()) == KafkaStreams.State.ERROR, "Streams never stopped.");
        } finally {
            streams.close();
        }
        TestUtils.waitForCondition(() -> (streams.state()) == KafkaStreams.State.NOT_RUNNING, "Streams never stopped.");
        final Field globalThreadField = streams.getClass().getDeclaredField("globalStreamThread");
        globalThreadField.setAccessible(true);
        final GlobalStreamThread globalStreamThread = ((GlobalStreamThread) (globalThreadField.get(streams)));
        Assert.assertNull(globalStreamThread);
    }

    @Test
    public void testStateGlobalThreadClose() throws Exception {
        // make sure we have the global state thread running too
        builder.globalTable("anyTopic");
        final KafkaStreams streams = new KafkaStreams(builder.build(), props);
        try {
            streams.start();
            TestUtils.waitForCondition(() -> (streams.state()) == KafkaStreams.State.RUNNING, "Streams never started.");
            final Field globalThreadField = streams.getClass().getDeclaredField("globalStreamThread");
            globalThreadField.setAccessible(true);
            final GlobalStreamThread globalStreamThread = ((GlobalStreamThread) (globalThreadField.get(streams)));
            globalStreamThread.shutdown();
            TestUtils.waitForCondition(() -> (globalStreamThread.state()) == GlobalStreamThread.State.DEAD, "Thread never stopped.");
            globalStreamThread.join();
            Assert.assertEquals(streams.state(), ERROR);
        } finally {
            streams.close();
        }
        Assert.assertEquals(streams.state(), NOT_RUNNING);
    }

    @Test
    public void globalThreadShouldTimeoutWhenBrokerConnectionCannotBeEstablished() {
        final Properties props = new Properties();
        props.put(APPLICATION_ID_CONFIG, "appId");
        props.put(BOOTSTRAP_SERVERS_CONFIG, "localhost:1");
        props.put(METRIC_REPORTER_CLASSES_CONFIG, MockMetricsReporter.class.getName());
        props.put(STATE_DIR_CONFIG, TestUtils.tempDirectory().getPath());
        props.put(NUM_STREAM_THREADS_CONFIG, KafkaStreamsTest.NUM_THREADS);
        props.put(DEFAULT_API_TIMEOUT_MS_CONFIG, 200);
        // make sure we have the global state thread running too
        builder.globalTable("anyTopic");
        try (final KafkaStreams streams = new KafkaStreams(builder.build(), props)) {
            streams.start();
            Assert.fail("expected start() to time out and throw an exception.");
        } catch (final StreamsException expected) {
            // This is a result of not being able to connect to the broker.
        }
        // There's nothing to assert... We're testing that this operation actually completes.
    }

    @Test
    public void testLocalThreadCloseWithoutConnectingToBroker() {
        final Properties props = new Properties();
        props.setProperty(APPLICATION_ID_CONFIG, "appId");
        props.setProperty(BOOTSTRAP_SERVERS_CONFIG, "localhost:1");
        props.setProperty(METRIC_REPORTER_CLASSES_CONFIG, MockMetricsReporter.class.getName());
        props.setProperty(STATE_DIR_CONFIG, TestUtils.tempDirectory().getPath());
        props.put(NUM_STREAM_THREADS_CONFIG, KafkaStreamsTest.NUM_THREADS);
        // make sure we have the global state thread running too
        builder.table("anyTopic");
        try (final KafkaStreams streams = new KafkaStreams(builder.build(), props)) {
            streams.start();
        }
        // There's nothing to assert... We're testing that this operation actually completes.
    }

    @Test
    public void testInitializesAndDestroysMetricsReporters() {
        final int oldInitCount = INIT_COUNT.get();
        try (final KafkaStreams streams = new KafkaStreams(builder.build(), props)) {
            final int newInitCount = INIT_COUNT.get();
            final int initDiff = newInitCount - oldInitCount;
            Assert.assertTrue("some reporters should be initialized by calling on construction", (initDiff > 0));
            streams.start();
            final int oldCloseCount = CLOSE_COUNT.get();
            streams.close();
            Assert.assertEquals((oldCloseCount + initDiff), CLOSE_COUNT.get());
        }
    }

    @Test
    public void testCloseIsIdempotent() {
        globalStreams.close();
        final int closeCount = CLOSE_COUNT.get();
        globalStreams.close();
        Assert.assertEquals("subsequent close() calls should do nothing", closeCount, CLOSE_COUNT.get());
    }

    @Test
    public void testCannotStartOnceClosed() {
        globalStreams.start();
        globalStreams.close();
        try {
            globalStreams.start();
            Assert.fail("Should have throw IllegalStateException");
        } catch (final IllegalStateException expected) {
            // this is ok
        } finally {
            globalStreams.close();
        }
    }

    @Test
    public void testCannotStartTwice() {
        globalStreams.start();
        try {
            globalStreams.start();
            Assert.fail("Should throw an IllegalStateException");
        } catch (final IllegalStateException e) {
            // this is ok
        } finally {
            globalStreams.close();
        }
    }

    @Test
    public void shouldNotSetGlobalRestoreListenerAfterStarting() {
        globalStreams.start();
        try {
            globalStreams.setGlobalStateRestoreListener(new MockStateRestoreListener());
            Assert.fail("Should throw an IllegalStateException");
        } catch (final IllegalStateException e) {
            // expected
        } finally {
            globalStreams.close();
        }
    }

    @Test
    public void shouldThrowExceptionSettingUncaughtExceptionHandlerNotInCreateState() {
        globalStreams.start();
        try {
            globalStreams.setUncaughtExceptionHandler(null);
            Assert.fail("Should throw IllegalStateException");
        } catch (final IllegalStateException e) {
            // expected
        }
    }

    @Test
    public void shouldThrowExceptionSettingStateListenerNotInCreateState() {
        globalStreams.start();
        try {
            globalStreams.setStateListener(null);
            Assert.fail("Should throw IllegalStateException");
        } catch (final IllegalStateException e) {
            // expected
        }
    }

    @Test
    public void testIllegalMetricsConfig() {
        props.setProperty(METRICS_RECORDING_LEVEL_CONFIG, "illegalConfig");
        try {
            new KafkaStreams(builder.build(), props);
            Assert.fail("Should have throw ConfigException");
        } catch (final ConfigException expected) {
            /* expected */
        }
    }

    @Test
    public void testLegalMetricsConfig() {
        props.setProperty(METRICS_RECORDING_LEVEL_CONFIG, INFO.toString());
        close();
        props.setProperty(METRICS_RECORDING_LEVEL_CONFIG, DEBUG.toString());
        close();
    }

    @Test(expected = IllegalStateException.class)
    public void shouldNotGetAllTasksWhenNotRunning() {
        globalStreams.allMetadata();
    }

    @Test(expected = IllegalStateException.class)
    public void shouldNotGetAllTasksWithStoreWhenNotRunning() {
        globalStreams.allMetadataForStore("store");
    }

    @Test(expected = IllegalStateException.class)
    public void shouldNotGetTaskWithKeyAndSerializerWhenNotRunning() {
        globalStreams.metadataForKey("store", "key", Serdes.String().serializer());
    }

    @Test(expected = IllegalStateException.class)
    public void shouldNotGetTaskWithKeyAndPartitionerWhenNotRunning() {
        globalStreams.metadataForKey("store", "key", ( topic, key, value, numPartitions) -> 0);
    }

    @Test
    public void shouldReturnFalseOnCloseWhenThreadsHaventTerminated() throws Exception {
        final AtomicBoolean keepRunning = new AtomicBoolean(true);
        KafkaStreams streams = null;
        try {
            final StreamsBuilder builder = new StreamsBuilder();
            final CountDownLatch latch = new CountDownLatch(1);
            final String topic = "input";
            KafkaStreamsTest.CLUSTER.createTopics(topic);
            builder.stream(topic, Consumed.with(Serdes.String(), Serdes.String())).foreach(( key, value) -> {
                try {
                    latch.countDown();
                    while (keepRunning.get()) {
                        Thread.sleep(10);
                    } 
                } catch (final  e) {
                    // no-op
                }
            });
            streams = new KafkaStreams(builder.build(), props);
            streams.start();
            IntegrationTestUtils.produceKeyValuesSynchronouslyWithTimestamp(topic, Collections.singletonList(new KeyValue("A", "A")), TestUtils.producerConfig(KafkaStreamsTest.CLUSTER.bootstrapServers(), StringSerializer.class, StringSerializer.class, new Properties()), System.currentTimeMillis());
            Assert.assertTrue("Timed out waiting to receive single message", latch.await(30, TimeUnit.SECONDS));
            Assert.assertFalse(streams.close(Duration.ofMillis(10)));
        } finally {
            // stop the thread so we don't interfere with other tests etc
            keepRunning.set(false);
            if (streams != null) {
                streams.close();
            }
        }
    }

    @Test
    public void shouldReturnThreadMetadata() {
        globalStreams.start();
        final Set<ThreadMetadata> threadMetadata = globalStreams.localThreadsMetadata();
        Assert.assertNotNull(threadMetadata);
        Assert.assertEquals(2, threadMetadata.size());
        for (final ThreadMetadata metadata : threadMetadata) {
            Assert.assertTrue((("#threadState() was: " + (metadata.threadState())) + "; expected either RUNNING, STARTING, PARTITIONS_REVOKED, PARTITIONS_ASSIGNED, or CREATED"), Arrays.asList("RUNNING", "STARTING", "PARTITIONS_REVOKED", "PARTITIONS_ASSIGNED", "CREATED").contains(metadata.threadState()));
            Assert.assertEquals(0, metadata.standbyTasks().size());
            Assert.assertEquals(0, metadata.activeTasks().size());
            final String threadName = metadata.threadName();
            Assert.assertTrue(threadName.startsWith("clientId-StreamThread-"));
            Assert.assertEquals((threadName + "-consumer"), metadata.consumerClientId());
            Assert.assertEquals((threadName + "-restore-consumer"), metadata.restoreConsumerClientId());
            Assert.assertEquals(Collections.singleton((threadName + "-producer")), metadata.producerClientIds());
            Assert.assertEquals("clientId-admin", metadata.adminClientId());
        }
    }

    @Test
    public void shouldAllowCleanupBeforeStartAndAfterClose() {
        try {
            globalStreams.cleanUp();
            globalStreams.start();
        } finally {
            globalStreams.close();
        }
        globalStreams.cleanUp();
    }

    @Test
    public void shouldThrowOnCleanupWhileRunning() throws InterruptedException {
        globalStreams.start();
        TestUtils.waitForCondition(() -> (globalStreams.state()) == KafkaStreams.State.RUNNING, "Streams never started.");
        try {
            globalStreams.cleanUp();
            Assert.fail("Should have thrown IllegalStateException");
        } catch (final IllegalStateException expected) {
            Assert.assertEquals("Cannot clean up while running.", expected.getMessage());
        }
    }

    @Test
    public void shouldCleanupOldStateDirs() throws InterruptedException {
        props.setProperty(STATE_CLEANUP_DELAY_MS_CONFIG, "1");
        final String topic = "topic";
        KafkaStreamsTest.CLUSTER.createTopic(topic);
        final StreamsBuilder builder = new StreamsBuilder();
        builder.table(topic, Materialized.as("store"));
        try (final KafkaStreams streams = new KafkaStreams(builder.build(), props)) {
            final CountDownLatch latch = new CountDownLatch(1);
            streams.setStateListener(( newState, oldState) -> {
                if ((newState == KafkaStreams.State.RUNNING) && (oldState == KafkaStreams.State.REBALANCING)) {
                    latch.countDown();
                }
            });
            final String appDir = ((props.getProperty(STATE_DIR_CONFIG)) + (File.separator)) + (props.getProperty(APPLICATION_ID_CONFIG));
            final File oldTaskDir = new File(appDir, "10_1");
            Assert.assertTrue(oldTaskDir.mkdirs());
            streams.start();
            latch.await(30, TimeUnit.SECONDS);
            verifyCleanupStateDir(appDir, oldTaskDir);
            Assert.assertTrue(oldTaskDir.mkdirs());
            verifyCleanupStateDir(appDir, oldTaskDir);
        }
    }

    @Test
    public void shouldThrowOnNegativeTimeoutForClose() {
        try (final KafkaStreams streams = new KafkaStreams(builder.build(), props)) {
            streams.close(Duration.ofMillis((-1L)));
            Assert.fail("should not accept negative close parameter");
        } catch (final IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void shouldNotBlockInCloseForZeroDuration() throws InterruptedException {
        final KafkaStreams streams = new KafkaStreams(builder.build(), props);
        final Thread th = new Thread(() -> streams.close(Duration.ofMillis(0L)));
        th.start();
        try {
            th.join(30000L);
            Assert.assertFalse(th.isAlive());
        } finally {
            streams.close();
        }
    }

    @Test
    public void statelessTopologyShouldNotCreateStateDirectory() throws Exception {
        final String inputTopic = (testName.getMethodName()) + "-input";
        final String outputTopic = (testName.getMethodName()) + "-output";
        KafkaStreamsTest.CLUSTER.createTopics(inputTopic, outputTopic);
        final Topology topology = new Topology();
        topology.addSource("source", Serdes.String().deserializer(), Serdes.String().deserializer(), inputTopic).addProcessor("process", () -> new AbstractProcessor<String, String>() {
            @Override
            public void process(final String key, final String value) {
                if (((value.length()) % 2) == 0) {
                    context().forward(key, (key + value));
                }
            }
        }, "source").addSink("sink", outputTopic, new StringSerializer(), new StringSerializer(), "process");
        startStreamsAndCheckDirExists(topology, Collections.singleton(inputTopic), outputTopic, false);
    }

    @Test
    public void inMemoryStatefulTopologyShouldNotCreateStateDirectory() throws Exception {
        final String inputTopic = (testName.getMethodName()) + "-input";
        final String outputTopic = (testName.getMethodName()) + "-output";
        final String globalTopicName = (testName.getMethodName()) + "-global";
        final String storeName = (testName.getMethodName()) + "-counts";
        final String globalStoreName = (testName.getMethodName()) + "-globalStore";
        final Topology topology = getStatefulTopology(inputTopic, outputTopic, globalTopicName, storeName, globalStoreName, false);
        startStreamsAndCheckDirExists(topology, Arrays.asList(inputTopic, globalTopicName), outputTopic, false);
    }

    @Test
    public void statefulTopologyShouldCreateStateDirectory() throws Exception {
        final String inputTopic = (testName.getMethodName()) + "-input";
        final String outputTopic = (testName.getMethodName()) + "-output";
        final String globalTopicName = (testName.getMethodName()) + "-global";
        final String storeName = (testName.getMethodName()) + "-counts";
        final String globalStoreName = (testName.getMethodName()) + "-globalStore";
        final Topology topology = getStatefulTopology(inputTopic, outputTopic, globalTopicName, storeName, globalStoreName, true);
        startStreamsAndCheckDirExists(topology, Arrays.asList(inputTopic, globalTopicName), outputTopic, true);
    }

    public static class StateListenerStub implements KafkaStreams.StateListener {
        int numChanges = 0;

        State oldState;

        State newState;

        public Map<KafkaStreams.State, Long> mapStates = new HashMap<>();

        @Override
        public void onChange(final KafkaStreams.State newState, final KafkaStreams.State oldState) {
            final long prevCount = (mapStates.containsKey(newState)) ? mapStates.get(newState) : 0;
            (numChanges)++;
            this.oldState = oldState;
            this.newState = newState;
            mapStates.put(newState, (prevCount + 1));
        }
    }
}

