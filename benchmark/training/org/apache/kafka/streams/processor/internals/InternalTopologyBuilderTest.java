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
package org.apache.kafka.streams.processor.internals;


import InternalTopologyBuilder.AbstractNode;
import InternalTopologyBuilder.SubscriptionUpdates;
import InternalTopologyBuilder.TopicsInfo;
import TopicConfig.CLEANUP_POLICY_COMPACT;
import TopicConfig.CLEANUP_POLICY_CONFIG;
import TopicConfig.CLEANUP_POLICY_DELETE;
import TopicConfig.RETENTION_MS_CONFIG;
import Topology.AutoOffsetReset.EARLIEST;
import Topology.AutoOffsetReset.LATEST;
import TopologyDescription.Node;
import java.lang.reflect.Field;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import org.apache.kafka.common.config.TopicConfig;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.TopologyDescription;
import org.apache.kafka.streams.errors.TopologyException;
import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorSupplier;
import org.apache.kafka.streams.processor.StateStore;
import org.apache.kafka.streams.processor.TopicNameExtractor;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.StoreBuilder;
import org.apache.kafka.streams.state.Stores;
import org.apache.kafka.test.MockKeyValueStoreBuilder;
import org.apache.kafka.test.MockProcessorSupplier;
import org.apache.kafka.test.MockTimestampExtractor;
import org.apache.kafka.test.StreamsTestUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Assert;
import org.junit.Test;


public class InternalTopologyBuilderTest {
    private final Serde<String> stringSerde = Serdes.String();

    private final InternalTopologyBuilder builder = new InternalTopologyBuilder();

    private final StoreBuilder storeBuilder = new MockKeyValueStoreBuilder("store", false);

    @Test
    public void shouldAddSourceWithOffsetReset() {
        final String earliestTopic = "earliestTopic";
        final String latestTopic = "latestTopic";
        builder.addSource(EARLIEST, "source", null, null, null, earliestTopic);
        builder.addSource(LATEST, "source2", null, null, null, latestTopic);
        Assert.assertTrue(builder.earliestResetTopicsPattern().matcher(earliestTopic).matches());
        Assert.assertTrue(builder.latestResetTopicsPattern().matcher(latestTopic).matches());
    }

    @Test
    public void shouldAddSourcePatternWithOffsetReset() {
        final String earliestTopicPattern = "earliest.*Topic";
        final String latestTopicPattern = "latest.*Topic";
        builder.addSource(EARLIEST, "source", null, null, null, Pattern.compile(earliestTopicPattern));
        builder.addSource(LATEST, "source2", null, null, null, Pattern.compile(latestTopicPattern));
        Assert.assertTrue(builder.earliestResetTopicsPattern().matcher("earliestTestTopic").matches());
        Assert.assertTrue(builder.latestResetTopicsPattern().matcher("latestTestTopic").matches());
    }

    @Test
    public void shouldAddSourceWithoutOffsetReset() {
        final Pattern expectedPattern = Pattern.compile("test-topic");
        builder.addSource(null, "source", null, stringSerde.deserializer(), stringSerde.deserializer(), "test-topic");
        Assert.assertEquals(expectedPattern.pattern(), builder.sourceTopicPattern().pattern());
        Assert.assertEquals(builder.earliestResetTopicsPattern().pattern(), "");
        Assert.assertEquals(builder.latestResetTopicsPattern().pattern(), "");
    }

    @Test
    public void shouldAddPatternSourceWithoutOffsetReset() {
        final Pattern expectedPattern = Pattern.compile("test-.*");
        builder.addSource(null, "source", null, stringSerde.deserializer(), stringSerde.deserializer(), Pattern.compile("test-.*"));
        Assert.assertEquals(expectedPattern.pattern(), builder.sourceTopicPattern().pattern());
        Assert.assertEquals(builder.earliestResetTopicsPattern().pattern(), "");
        Assert.assertEquals(builder.latestResetTopicsPattern().pattern(), "");
    }

    @Test(expected = TopologyException.class)
    public void shouldNotAllowOffsetResetSourceWithoutTopics() {
        builder.addSource(EARLIEST, "source", null, stringSerde.deserializer(), stringSerde.deserializer());
    }

    @Test
    public void shouldNotAllowOffsetResetSourceWithDuplicateSourceName() {
        builder.addSource(EARLIEST, "source", null, stringSerde.deserializer(), stringSerde.deserializer(), "topic-1");
        try {
            builder.addSource(LATEST, "source", null, stringSerde.deserializer(), stringSerde.deserializer(), "topic-2");
            Assert.fail("Should throw TopologyException for duplicate source name");
        } catch (final TopologyException expected) {
            /* ok */
        }
    }

    @Test
    public void testAddSourceWithSameName() {
        builder.addSource(null, "source", null, null, null, "topic-1");
        try {
            builder.addSource(null, "source", null, null, null, "topic-2");
            Assert.fail("Should throw TopologyException with source name conflict");
        } catch (final TopologyException expected) {
            /* ok */
        }
    }

    @Test
    public void testAddSourceWithSameTopic() {
        builder.addSource(null, "source", null, null, null, "topic-1");
        try {
            builder.addSource(null, "source-2", null, null, null, "topic-1");
            Assert.fail("Should throw TopologyException with topic conflict");
        } catch (final TopologyException expected) {
            /* ok */
        }
    }

    @Test
    public void testAddProcessorWithSameName() {
        builder.addSource(null, "source", null, null, null, "topic-1");
        builder.addProcessor("processor", new MockProcessorSupplier(), "source");
        try {
            builder.addProcessor("processor", new MockProcessorSupplier(), "source");
            Assert.fail("Should throw TopologyException with processor name conflict");
        } catch (final TopologyException expected) {
            /* ok */
        }
    }

    @Test(expected = TopologyException.class)
    public void testAddProcessorWithWrongParent() {
        builder.addProcessor("processor", new MockProcessorSupplier(), "source");
    }

    @Test(expected = TopologyException.class)
    public void testAddProcessorWithSelfParent() {
        builder.addProcessor("processor", new MockProcessorSupplier(), "processor");
    }

    @Test(expected = TopologyException.class)
    public void testAddProcessorWithEmptyParents() {
        builder.addProcessor("processor", new MockProcessorSupplier());
    }

    @Test(expected = NullPointerException.class)
    public void testAddProcessorWithNullParents() {
        builder.addProcessor("processor", new MockProcessorSupplier(), ((String) (null)));
    }

    @Test
    public void testAddSinkWithSameName() {
        builder.addSource(null, "source", null, null, null, "topic-1");
        builder.addSink("sink", "topic-2", null, null, null, "source");
        try {
            builder.addSink("sink", "topic-3", null, null, null, "source");
            Assert.fail("Should throw TopologyException with sink name conflict");
        } catch (final TopologyException expected) {
            /* ok */
        }
    }

    @Test(expected = TopologyException.class)
    public void testAddSinkWithWrongParent() {
        builder.addSink("sink", "topic-2", null, null, null, "source");
    }

    @Test(expected = TopologyException.class)
    public void testAddSinkWithSelfParent() {
        builder.addSink("sink", "topic-2", null, null, null, "sink");
    }

    @Test(expected = TopologyException.class)
    public void testAddSinkWithEmptyParents() {
        builder.addSink("sink", "topic", null, null, null);
    }

    @Test(expected = NullPointerException.class)
    public void testAddSinkWithNullParents() {
        builder.addSink("sink", "topic", null, null, null, ((String) (null)));
    }

    @Test
    public void testAddSinkConnectedWithParent() {
        builder.addSource(null, "source", null, null, null, "source-topic");
        builder.addSink("sink", "dest-topic", null, null, null, "source");
        final Map<Integer, Set<String>> nodeGroups = builder.nodeGroups();
        final Set<String> nodeGroup = nodeGroups.get(0);
        Assert.assertTrue(nodeGroup.contains("sink"));
        Assert.assertTrue(nodeGroup.contains("source"));
    }

    @Test
    public void testAddSinkConnectedWithMultipleParent() {
        builder.addSource(null, "source", null, null, null, "source-topic");
        builder.addSource(null, "sourceII", null, null, null, "source-topicII");
        builder.addSink("sink", "dest-topic", null, null, null, "source", "sourceII");
        final Map<Integer, Set<String>> nodeGroups = builder.nodeGroups();
        final Set<String> nodeGroup = nodeGroups.get(0);
        Assert.assertTrue(nodeGroup.contains("sink"));
        Assert.assertTrue(nodeGroup.contains("source"));
        Assert.assertTrue(nodeGroup.contains("sourceII"));
    }

    @Test
    public void testSourceTopics() {
        builder.setApplicationId("X");
        builder.addSource(null, "source-1", null, null, null, "topic-1");
        builder.addSource(null, "source-2", null, null, null, "topic-2");
        builder.addSource(null, "source-3", null, null, null, "topic-3");
        builder.addInternalTopic("topic-3");
        final Pattern expectedPattern = Pattern.compile("X-topic-3|topic-1|topic-2");
        Assert.assertEquals(expectedPattern.pattern(), builder.sourceTopicPattern().pattern());
    }

    @Test
    public void testPatternSourceTopic() {
        final Pattern expectedPattern = Pattern.compile("topic-\\d");
        builder.addSource(null, "source-1", null, null, null, expectedPattern);
        Assert.assertEquals(expectedPattern.pattern(), builder.sourceTopicPattern().pattern());
    }

    @Test
    public void testAddMoreThanOnePatternSourceNode() {
        final Pattern expectedPattern = Pattern.compile("topics[A-Z]|.*-\\d");
        builder.addSource(null, "source-1", null, null, null, Pattern.compile("topics[A-Z]"));
        builder.addSource(null, "source-2", null, null, null, Pattern.compile(".*-\\d"));
        Assert.assertEquals(expectedPattern.pattern(), builder.sourceTopicPattern().pattern());
    }

    @Test
    public void testSubscribeTopicNameAndPattern() {
        final Pattern expectedPattern = Pattern.compile("topic-bar|topic-foo|.*-\\d");
        builder.addSource(null, "source-1", null, null, null, "topic-foo", "topic-bar");
        builder.addSource(null, "source-2", null, null, null, Pattern.compile(".*-\\d"));
        Assert.assertEquals(expectedPattern.pattern(), builder.sourceTopicPattern().pattern());
    }

    @Test
    public void testPatternMatchesAlreadyProvidedTopicSource() {
        builder.addSource(null, "source-1", null, null, null, "foo");
        try {
            builder.addSource(null, "source-2", null, null, null, Pattern.compile("f.*"));
            Assert.fail("Should throw TopologyException with topic name/pattern conflict");
        } catch (final TopologyException expected) {
            /* ok */
        }
    }

    @Test
    public void testNamedTopicMatchesAlreadyProvidedPattern() {
        builder.addSource(null, "source-1", null, null, null, Pattern.compile("f.*"));
        try {
            builder.addSource(null, "source-2", null, null, null, "foo");
            Assert.fail("Should throw TopologyException with topic name/pattern conflict");
        } catch (final TopologyException expected) {
            /* ok */
        }
    }

    @Test(expected = TopologyException.class)
    public void testAddStateStoreWithNonExistingProcessor() {
        builder.addStateStore(storeBuilder, "no-such-processor");
    }

    @Test
    public void testAddStateStoreWithSource() {
        builder.addSource(null, "source-1", null, null, null, "topic-1");
        try {
            builder.addStateStore(storeBuilder, "source-1");
            Assert.fail("Should throw TopologyException with store cannot be added to source");
        } catch (final TopologyException expected) {
            /* ok */
        }
    }

    @Test
    public void testAddStateStoreWithSink() {
        builder.addSource(null, "source-1", null, null, null, "topic-1");
        builder.addSink("sink-1", "topic-1", null, null, null, "source-1");
        try {
            builder.addStateStore(storeBuilder, "sink-1");
            Assert.fail("Should throw TopologyException with store cannot be added to sink");
        } catch (final TopologyException expected) {
            /* ok */
        }
    }

    @Test
    public void testAddStateStoreWithDuplicates() {
        builder.addStateStore(storeBuilder);
        try {
            builder.addStateStore(storeBuilder);
            Assert.fail("Should throw TopologyException with store name conflict");
        } catch (final TopologyException expected) {
            /* ok */
        }
    }

    @Test
    public void testAddStateStore() {
        builder.addStateStore(storeBuilder);
        builder.setApplicationId("X");
        builder.addSource(null, "source-1", null, null, null, "topic-1");
        builder.addProcessor("processor-1", new MockProcessorSupplier(), "source-1");
        Assert.assertEquals(0, builder.build(null).stateStores().size());
        builder.connectProcessorAndStateStores("processor-1", storeBuilder.name());
        final List<StateStore> suppliers = builder.build(null).stateStores();
        Assert.assertEquals(1, suppliers.size());
        Assert.assertEquals(storeBuilder.name(), suppliers.get(0).name());
    }

    @Test
    public void testTopicGroups() {
        builder.setApplicationId("X");
        builder.addInternalTopic("topic-1x");
        builder.addSource(null, "source-1", null, null, null, "topic-1", "topic-1x");
        builder.addSource(null, "source-2", null, null, null, "topic-2");
        builder.addSource(null, "source-3", null, null, null, "topic-3");
        builder.addSource(null, "source-4", null, null, null, "topic-4");
        builder.addSource(null, "source-5", null, null, null, "topic-5");
        builder.addProcessor("processor-1", new MockProcessorSupplier(), "source-1");
        builder.addProcessor("processor-2", new MockProcessorSupplier(), "source-2", "processor-1");
        builder.copartitionSources(Arrays.asList("source-1", "source-2"));
        builder.addProcessor("processor-3", new MockProcessorSupplier(), "source-3", "source-4");
        final Map<Integer, InternalTopologyBuilder.TopicsInfo> topicGroups = builder.topicGroups();
        final Map<Integer, InternalTopologyBuilder.TopicsInfo> expectedTopicGroups = new HashMap<>();
        expectedTopicGroups.put(0, new InternalTopologyBuilder.TopicsInfo(Collections.<String>emptySet(), mkSet("topic-1", "X-topic-1x", "topic-2"), Collections.<String, InternalTopicConfig>emptyMap(), Collections.<String, InternalTopicConfig>emptyMap()));
        expectedTopicGroups.put(1, new InternalTopologyBuilder.TopicsInfo(Collections.<String>emptySet(), mkSet("topic-3", "topic-4"), Collections.<String, InternalTopicConfig>emptyMap(), Collections.<String, InternalTopicConfig>emptyMap()));
        expectedTopicGroups.put(2, new InternalTopologyBuilder.TopicsInfo(Collections.<String>emptySet(), mkSet("topic-5"), Collections.<String, InternalTopicConfig>emptyMap(), Collections.<String, InternalTopicConfig>emptyMap()));
        Assert.assertEquals(3, topicGroups.size());
        Assert.assertEquals(expectedTopicGroups, topicGroups);
        final Collection<Set<String>> copartitionGroups = builder.copartitionGroups();
        Assert.assertEquals(mkSet(mkSet("topic-1", "X-topic-1x", "topic-2")), new HashSet(copartitionGroups));
    }

    @Test
    public void testTopicGroupsByStateStore() {
        builder.setApplicationId("X");
        builder.addSource(null, "source-1", null, null, null, "topic-1", "topic-1x");
        builder.addSource(null, "source-2", null, null, null, "topic-2");
        builder.addSource(null, "source-3", null, null, null, "topic-3");
        builder.addSource(null, "source-4", null, null, null, "topic-4");
        builder.addSource(null, "source-5", null, null, null, "topic-5");
        builder.addProcessor("processor-1", new MockProcessorSupplier(), "source-1");
        builder.addProcessor("processor-2", new MockProcessorSupplier(), "source-2");
        builder.addStateStore(new MockKeyValueStoreBuilder("store-1", false), "processor-1", "processor-2");
        builder.addProcessor("processor-3", new MockProcessorSupplier(), "source-3");
        builder.addProcessor("processor-4", new MockProcessorSupplier(), "source-4");
        builder.addStateStore(new MockKeyValueStoreBuilder("store-2", false), "processor-3", "processor-4");
        builder.addProcessor("processor-5", new MockProcessorSupplier(), "source-5");
        builder.addStateStore(new MockKeyValueStoreBuilder("store-3", false));
        builder.connectProcessorAndStateStores("processor-5", "store-3");
        final Map<Integer, InternalTopologyBuilder.TopicsInfo> topicGroups = builder.topicGroups();
        final Map<Integer, InternalTopologyBuilder.TopicsInfo> expectedTopicGroups = new HashMap<>();
        final String store1 = ProcessorStateManager.storeChangelogTopic("X", "store-1");
        final String store2 = ProcessorStateManager.storeChangelogTopic("X", "store-2");
        final String store3 = ProcessorStateManager.storeChangelogTopic("X", "store-3");
        expectedTopicGroups.put(0, new InternalTopologyBuilder.TopicsInfo(Collections.<String>emptySet(), mkSet("topic-1", "topic-1x", "topic-2"), Collections.<String, InternalTopicConfig>emptyMap(), Collections.singletonMap(store1, ((InternalTopicConfig) (new UnwindowedChangelogTopicConfig(store1, Collections.<String, String>emptyMap()))))));
        expectedTopicGroups.put(1, new InternalTopologyBuilder.TopicsInfo(Collections.<String>emptySet(), mkSet("topic-3", "topic-4"), Collections.<String, InternalTopicConfig>emptyMap(), Collections.singletonMap(store2, ((InternalTopicConfig) (new UnwindowedChangelogTopicConfig(store2, Collections.<String, String>emptyMap()))))));
        expectedTopicGroups.put(2, new InternalTopologyBuilder.TopicsInfo(Collections.<String>emptySet(), mkSet("topic-5"), Collections.<String, InternalTopicConfig>emptyMap(), Collections.singletonMap(store3, ((InternalTopicConfig) (new UnwindowedChangelogTopicConfig(store3, Collections.<String, String>emptyMap()))))));
        Assert.assertEquals(3, topicGroups.size());
        Assert.assertEquals(expectedTopicGroups, topicGroups);
    }

    @Test
    public void testBuild() {
        builder.addSource(null, "source-1", null, null, null, "topic-1", "topic-1x");
        builder.addSource(null, "source-2", null, null, null, "topic-2");
        builder.addSource(null, "source-3", null, null, null, "topic-3");
        builder.addSource(null, "source-4", null, null, null, "topic-4");
        builder.addSource(null, "source-5", null, null, null, "topic-5");
        builder.addProcessor("processor-1", new MockProcessorSupplier(), "source-1");
        builder.addProcessor("processor-2", new MockProcessorSupplier(), "source-2", "processor-1");
        builder.addProcessor("processor-3", new MockProcessorSupplier(), "source-3", "source-4");
        builder.setApplicationId("X");
        final ProcessorTopology topology0 = builder.build(0);
        final ProcessorTopology topology1 = builder.build(1);
        final ProcessorTopology topology2 = builder.build(2);
        Assert.assertEquals(mkSet("source-1", "source-2", "processor-1", "processor-2"), nodeNames(topology0.processors()));
        Assert.assertEquals(mkSet("source-3", "source-4", "processor-3"), nodeNames(topology1.processors()));
        Assert.assertEquals(mkSet("source-5"), nodeNames(topology2.processors()));
    }

    @Test
    public void shouldAllowIncrementalBuilds() {
        Map<Integer, Set<String>> oldNodeGroups;
        Map<Integer, Set<String>> newNodeGroups;
        oldNodeGroups = builder.nodeGroups();
        builder.addSource(null, "source-1", null, null, null, "topic-1");
        builder.addSource(null, "source-2", null, null, null, "topic-2");
        newNodeGroups = builder.nodeGroups();
        Assert.assertNotEquals(oldNodeGroups, newNodeGroups);
        oldNodeGroups = newNodeGroups;
        builder.addSource(null, "source-3", null, null, null, Pattern.compile(""));
        builder.addSource(null, "source-4", null, null, null, Pattern.compile(""));
        newNodeGroups = builder.nodeGroups();
        Assert.assertNotEquals(oldNodeGroups, newNodeGroups);
        oldNodeGroups = newNodeGroups;
        builder.addProcessor("processor-1", new MockProcessorSupplier(), "source-1");
        builder.addProcessor("processor-2", new MockProcessorSupplier(), "source-2");
        builder.addProcessor("processor-3", new MockProcessorSupplier(), "source-3");
        newNodeGroups = builder.nodeGroups();
        Assert.assertNotEquals(oldNodeGroups, newNodeGroups);
        oldNodeGroups = newNodeGroups;
        builder.addSink("sink-1", "sink-topic", null, null, null, "processor-1");
        newNodeGroups = builder.nodeGroups();
        Assert.assertNotEquals(oldNodeGroups, newNodeGroups);
        oldNodeGroups = newNodeGroups;
        builder.addSink("sink-2", ( k, v, ctx) -> "sink-topic", null, null, null, "processor-2");
        newNodeGroups = builder.nodeGroups();
        Assert.assertNotEquals(oldNodeGroups, newNodeGroups);
        oldNodeGroups = newNodeGroups;
        builder.addStateStore(new MockKeyValueStoreBuilder("store-1", false), "processor-1", "processor-2");
        newNodeGroups = builder.nodeGroups();
        Assert.assertNotEquals(oldNodeGroups, newNodeGroups);
        oldNodeGroups = newNodeGroups;
        builder.addStateStore(new MockKeyValueStoreBuilder("store-2", false));
        builder.connectProcessorAndStateStores("processor-2", "store-2");
        builder.connectProcessorAndStateStores("processor-3", "store-2");
        newNodeGroups = builder.nodeGroups();
        Assert.assertNotEquals(oldNodeGroups, newNodeGroups);
        oldNodeGroups = newNodeGroups;
        builder.addGlobalStore(withLoggingDisabled(), "globalSource", null, null, null, "globalTopic", "global-processor", new MockProcessorSupplier());
        newNodeGroups = builder.nodeGroups();
        Assert.assertNotEquals(oldNodeGroups, newNodeGroups);
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotAllowNullNameWhenAddingSink() {
        builder.addSink(null, "topic", null, null, null);
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotAllowNullTopicWhenAddingSink() {
        builder.addSink("name", ((String) (null)), null, null, null);
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotAllowNullTopicChooserWhenAddingSink() {
        builder.addSink("name", ((TopicNameExtractor<Object, Object>) (null)), null, null, null);
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotAllowNullNameWhenAddingProcessor() {
        builder.addProcessor(null, new ProcessorSupplier() {
            @Override
            public Processor get() {
                return null;
            }
        });
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotAllowNullProcessorSupplier() {
        builder.addProcessor("name", null);
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotAllowNullNameWhenAddingSource() {
        builder.addSource(null, null, null, null, null, Pattern.compile(".*"));
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotAllowNullProcessorNameWhenConnectingProcessorAndStateStores() {
        builder.connectProcessorAndStateStores(null, "store");
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotAllowNullStateStoreNameWhenConnectingProcessorAndStateStores() {
        builder.connectProcessorAndStateStores("processor", new String[]{ null });
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotAddNullInternalTopic() {
        builder.addInternalTopic(null);
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotSetApplicationIdToNull() {
        builder.setApplicationId(null);
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotAddNullStateStoreSupplier() {
        builder.addStateStore(null);
    }

    @Test
    public void shouldAssociateStateStoreNameWhenStateStoreSupplierIsInternal() {
        builder.addSource(null, "source", null, null, null, "topic");
        builder.addProcessor("processor", new MockProcessorSupplier(), "source");
        builder.addStateStore(storeBuilder, "processor");
        final Map<String, List<String>> stateStoreNameToSourceTopic = builder.stateStoreNameToSourceTopics();
        Assert.assertEquals(1, stateStoreNameToSourceTopic.size());
        Assert.assertEquals(Collections.singletonList("topic"), stateStoreNameToSourceTopic.get("store"));
    }

    @Test
    public void shouldAssociateStateStoreNameWhenStateStoreSupplierIsExternal() {
        builder.addSource(null, "source", null, null, null, "topic");
        builder.addProcessor("processor", new MockProcessorSupplier(), "source");
        builder.addStateStore(storeBuilder, "processor");
        final Map<String, List<String>> stateStoreNameToSourceTopic = builder.stateStoreNameToSourceTopics();
        Assert.assertEquals(1, stateStoreNameToSourceTopic.size());
        Assert.assertEquals(Collections.singletonList("topic"), stateStoreNameToSourceTopic.get("store"));
    }

    @Test
    public void shouldCorrectlyMapStateStoreToInternalTopics() {
        builder.setApplicationId("appId");
        builder.addInternalTopic("internal-topic");
        builder.addSource(null, "source", null, null, null, "internal-topic");
        builder.addProcessor("processor", new MockProcessorSupplier(), "source");
        builder.addStateStore(storeBuilder, "processor");
        final Map<String, List<String>> stateStoreNameToSourceTopic = builder.stateStoreNameToSourceTopics();
        Assert.assertEquals(1, stateStoreNameToSourceTopic.size());
        Assert.assertEquals(Collections.singletonList("appId-internal-topic"), stateStoreNameToSourceTopic.get("store"));
    }

    @Test
    public void shouldAddInternalTopicConfigForWindowStores() {
        builder.setApplicationId("appId");
        builder.addSource(null, "source", null, null, null, "topic");
        builder.addProcessor("processor", new MockProcessorSupplier(), "source");
        builder.addStateStore(Stores.windowStoreBuilder(Stores.persistentWindowStore("store1", Duration.ofSeconds(30L), Duration.ofSeconds(10L), false), Serdes.String(), Serdes.String()), "processor");
        builder.addStateStore(Stores.sessionStoreBuilder(Stores.persistentSessionStore("store2", Duration.ofSeconds(30)), Serdes.String(), Serdes.String()), "processor");
        final Map<Integer, InternalTopologyBuilder.TopicsInfo> topicGroups = builder.topicGroups();
        final InternalTopologyBuilder.TopicsInfo topicsInfo = topicGroups.values().iterator().next();
        final InternalTopicConfig topicConfig1 = topicsInfo.stateChangelogTopics.get("appId-store1-changelog");
        final Map<String, String> properties1 = topicConfig1.getProperties(Collections.<String, String>emptyMap(), 10000);
        Assert.assertEquals(2, properties1.size());
        Assert.assertEquals((((TopicConfig.CLEANUP_POLICY_COMPACT) + ",") + (TopicConfig.CLEANUP_POLICY_DELETE)), properties1.get(CLEANUP_POLICY_CONFIG));
        Assert.assertEquals("40000", properties1.get(RETENTION_MS_CONFIG));
        Assert.assertEquals("appId-store1-changelog", topicConfig1.name());
        Assert.assertTrue((topicConfig1 instanceof WindowedChangelogTopicConfig));
        final InternalTopicConfig topicConfig2 = topicsInfo.stateChangelogTopics.get("appId-store2-changelog");
        final Map<String, String> properties2 = topicConfig2.getProperties(Collections.<String, String>emptyMap(), 10000);
        Assert.assertEquals(2, properties2.size());
        Assert.assertEquals((((TopicConfig.CLEANUP_POLICY_COMPACT) + ",") + (TopicConfig.CLEANUP_POLICY_DELETE)), properties2.get(CLEANUP_POLICY_CONFIG));
        Assert.assertEquals("40000", properties2.get(RETENTION_MS_CONFIG));
        Assert.assertEquals("appId-store2-changelog", topicConfig2.name());
        Assert.assertTrue((topicConfig2 instanceof WindowedChangelogTopicConfig));
    }

    @Test
    public void shouldAddInternalTopicConfigForNonWindowStores() {
        builder.setApplicationId("appId");
        builder.addSource(null, "source", null, null, null, "topic");
        builder.addProcessor("processor", new MockProcessorSupplier(), "source");
        builder.addStateStore(storeBuilder, "processor");
        final Map<Integer, InternalTopologyBuilder.TopicsInfo> topicGroups = builder.topicGroups();
        final InternalTopologyBuilder.TopicsInfo topicsInfo = topicGroups.values().iterator().next();
        final InternalTopicConfig topicConfig = topicsInfo.stateChangelogTopics.get("appId-store-changelog");
        final Map<String, String> properties = topicConfig.getProperties(Collections.<String, String>emptyMap(), 10000);
        Assert.assertEquals(1, properties.size());
        Assert.assertEquals(CLEANUP_POLICY_COMPACT, properties.get(CLEANUP_POLICY_CONFIG));
        Assert.assertEquals("appId-store-changelog", topicConfig.name());
        Assert.assertTrue((topicConfig instanceof UnwindowedChangelogTopicConfig));
    }

    @Test
    public void shouldAddInternalTopicConfigForRepartitionTopics() {
        builder.setApplicationId("appId");
        builder.addInternalTopic("foo");
        builder.addSource(null, "source", null, null, null, "foo");
        final InternalTopologyBuilder.TopicsInfo topicsInfo = builder.topicGroups().values().iterator().next();
        final InternalTopicConfig topicConfig = topicsInfo.repartitionSourceTopics.get("appId-foo");
        final Map<String, String> properties = topicConfig.getProperties(Collections.<String, String>emptyMap(), 10000);
        Assert.assertEquals(5, properties.size());
        Assert.assertEquals(String.valueOf(Long.MAX_VALUE), properties.get(RETENTION_MS_CONFIG));
        Assert.assertEquals(CLEANUP_POLICY_DELETE, properties.get(CLEANUP_POLICY_CONFIG));
        Assert.assertEquals("appId-foo", topicConfig.name());
        Assert.assertTrue((topicConfig instanceof RepartitionTopicConfig));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldSetCorrectSourceNodesWithRegexUpdatedTopics() throws Exception {
        builder.addSource(null, "source-1", null, null, null, "topic-foo");
        builder.addSource(null, "source-2", null, null, null, Pattern.compile("topic-[A-C]"));
        builder.addSource(null, "source-3", null, null, null, Pattern.compile("topic-\\d"));
        final InternalTopologyBuilder.SubscriptionUpdates subscriptionUpdates = new InternalTopologyBuilder.SubscriptionUpdates();
        final Field updatedTopicsField = subscriptionUpdates.getClass().getDeclaredField("updatedTopicSubscriptions");
        updatedTopicsField.setAccessible(true);
        final Set<String> updatedTopics = ((Set<String>) (updatedTopicsField.get(subscriptionUpdates)));
        updatedTopics.add("topic-B");
        updatedTopics.add("topic-3");
        updatedTopics.add("topic-A");
        builder.updateSubscriptions(subscriptionUpdates, null);
        builder.setApplicationId("test-id");
        final Map<Integer, InternalTopologyBuilder.TopicsInfo> topicGroups = builder.topicGroups();
        Assert.assertTrue(topicGroups.get(0).sourceTopics.contains("topic-foo"));
        Assert.assertTrue(topicGroups.get(1).sourceTopics.contains("topic-A"));
        Assert.assertTrue(topicGroups.get(1).sourceTopics.contains("topic-B"));
        Assert.assertTrue(topicGroups.get(2).sourceTopics.contains("topic-3"));
    }

    @Test
    public void shouldAddTimestampExtractorPerSource() {
        builder.addSource(null, "source", new MockTimestampExtractor(), null, null, "topic");
        final ProcessorTopology processorTopology = builder.rewriteTopology(new StreamsConfig(StreamsTestUtils.getStreamsConfig())).build(null);
        MatcherAssert.assertThat(processorTopology.source("topic").getTimestampExtractor(), IsInstanceOf.instanceOf(MockTimestampExtractor.class));
    }

    @Test
    public void shouldAddTimestampExtractorWithPatternPerSource() {
        final Pattern pattern = Pattern.compile("t.*");
        builder.addSource(null, "source", new MockTimestampExtractor(), null, null, pattern);
        final ProcessorTopology processorTopology = builder.rewriteTopology(new StreamsConfig(StreamsTestUtils.getStreamsConfig())).build(null);
        MatcherAssert.assertThat(processorTopology.source(pattern.pattern()).getTimestampExtractor(), IsInstanceOf.instanceOf(MockTimestampExtractor.class));
    }

    @Test
    public void shouldSortProcessorNodesCorrectly() {
        builder.addSource(null, "source1", null, null, null, "topic1");
        builder.addSource(null, "source2", null, null, null, "topic2");
        builder.addProcessor("processor1", new MockProcessorSupplier(), "source1");
        builder.addProcessor("processor2", new MockProcessorSupplier(), "source1", "source2");
        builder.addProcessor("processor3", new MockProcessorSupplier(), "processor2");
        builder.addSink("sink1", "topic2", null, null, null, "processor1", "processor3");
        Assert.assertEquals(1, builder.describe().subtopologies().size());
        final Iterator<TopologyDescription.Node> iterator = nodesInOrder();
        Assert.assertTrue(iterator.hasNext());
        InternalTopologyBuilder.AbstractNode node = ((InternalTopologyBuilder.AbstractNode) (iterator.next()));
        Assert.assertTrue(node.name.equals("source1"));
        Assert.assertEquals(6, node.size);
        Assert.assertTrue(iterator.hasNext());
        node = ((InternalTopologyBuilder.AbstractNode) (iterator.next()));
        Assert.assertTrue(node.name.equals("source2"));
        Assert.assertEquals(4, node.size);
        Assert.assertTrue(iterator.hasNext());
        node = ((InternalTopologyBuilder.AbstractNode) (iterator.next()));
        Assert.assertTrue(node.name.equals("processor2"));
        Assert.assertEquals(3, node.size);
        Assert.assertTrue(iterator.hasNext());
        node = ((InternalTopologyBuilder.AbstractNode) (iterator.next()));
        Assert.assertTrue(node.name.equals("processor1"));
        Assert.assertEquals(2, node.size);
        Assert.assertTrue(iterator.hasNext());
        node = ((InternalTopologyBuilder.AbstractNode) (iterator.next()));
        Assert.assertTrue(node.name.equals("processor3"));
        Assert.assertEquals(2, node.size);
        Assert.assertTrue(iterator.hasNext());
        node = ((InternalTopologyBuilder.AbstractNode) (iterator.next()));
        Assert.assertTrue(node.name.equals("sink1"));
        Assert.assertEquals(1, node.size);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldConnectRegexMatchedTopicsToStateStore() throws Exception {
        builder.addSource(null, "ingest", null, null, null, Pattern.compile("topic-\\d+"));
        builder.addProcessor("my-processor", new MockProcessorSupplier(), "ingest");
        builder.addStateStore(storeBuilder, "my-processor");
        final InternalTopologyBuilder.SubscriptionUpdates subscriptionUpdates = new InternalTopologyBuilder.SubscriptionUpdates();
        final Field updatedTopicsField = subscriptionUpdates.getClass().getDeclaredField("updatedTopicSubscriptions");
        updatedTopicsField.setAccessible(true);
        final Set<String> updatedTopics = ((Set<String>) (updatedTopicsField.get(subscriptionUpdates)));
        updatedTopics.add("topic-2");
        updatedTopics.add("topic-3");
        updatedTopics.add("topic-A");
        builder.updateSubscriptions(subscriptionUpdates, "test-thread");
        builder.setApplicationId("test-app");
        final Map<String, List<String>> stateStoreAndTopics = builder.stateStoreNameToSourceTopics();
        final List<String> topics = stateStoreAndTopics.get(storeBuilder.name());
        Assert.assertTrue("Expected to contain two topics", ((topics.size()) == 2));
        Assert.assertTrue(topics.contains("topic-2"));
        Assert.assertTrue(topics.contains("topic-3"));
        Assert.assertFalse(topics.contains("topic-A"));
    }

    @SuppressWarnings("unchecked")
    @Test(expected = TopologyException.class)
    public void shouldNotAllowToAddGlobalStoreWithSourceNameEqualsProcessorName() {
        final String sameNameForSourceAndProcessor = "sameName";
        builder.addGlobalStore(((StoreBuilder<KeyValueStore>) (storeBuilder)), sameNameForSourceAndProcessor, null, null, null, "anyTopicName", sameNameForSourceAndProcessor, new MockProcessorSupplier());
    }
}

