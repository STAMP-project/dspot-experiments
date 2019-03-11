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


import StreamsMetadata.NOT_AVAILABLE;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.utils.Utils;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.TopologyWrapper;
import org.apache.kafka.streams.processor.StreamPartitioner;
import org.apache.kafka.streams.state.HostInfo;
import org.apache.kafka.streams.state.StreamsMetadata;
import org.junit.Assert;
import org.junit.Test;

import static StreamsMetadataState.UNKNOWN_HOST;


public class StreamsMetadataStateTest {
    private StreamsMetadataState metadataState;

    private HostInfo hostOne;

    private HostInfo hostTwo;

    private HostInfo hostThree;

    private TopicPartition topic1P0;

    private TopicPartition topic2P0;

    private TopicPartition topic3P0;

    private Map<HostInfo, Set<TopicPartition>> hostToPartitions;

    private StreamsBuilder builder;

    private TopicPartition topic1P1;

    private TopicPartition topic2P1;

    private TopicPartition topic4P0;

    private Cluster cluster;

    private final String globalTable = "global-table";

    private StreamPartitioner<String, Object> partitioner;

    @Test
    public void shouldNotThrowNPEWhenOnChangeNotCalled() {
        getAllMetadataForStore("store");
    }

    @Test
    public void shouldGetAllStreamInstances() {
        final StreamsMetadata one = new StreamsMetadata(hostOne, Utils.mkSet(globalTable, "table-one", "table-two", "merged-table"), Utils.mkSet(topic1P0, topic2P1, topic4P0));
        final StreamsMetadata two = new StreamsMetadata(hostTwo, Utils.mkSet(globalTable, "table-two", "table-one", "merged-table"), Utils.mkSet(topic2P0, topic1P1));
        final StreamsMetadata three = new StreamsMetadata(hostThree, Utils.mkSet(globalTable, "table-three"), Collections.singleton(topic3P0));
        final Collection<StreamsMetadata> actual = metadataState.getAllMetadata();
        Assert.assertEquals(3, actual.size());
        Assert.assertTrue(((("expected " + actual) + " to contain ") + one), actual.contains(one));
        Assert.assertTrue(((("expected " + actual) + " to contain ") + two), actual.contains(two));
        Assert.assertTrue(((("expected " + actual) + " to contain ") + three), actual.contains(three));
    }

    @Test
    public void shouldGetAllStreamsInstancesWithNoStores() {
        builder.stream("topic-five").filter(new org.apache.kafka.streams.kstream.Predicate<Object, Object>() {
            @Override
            public boolean test(final Object key, final Object value) {
                return true;
            }
        }).to("some-other-topic");
        final TopicPartition tp5 = new TopicPartition("topic-five", 1);
        final HostInfo hostFour = new HostInfo("host-four", 8080);
        hostToPartitions.put(hostFour, Utils.mkSet(tp5));
        metadataState.onChange(hostToPartitions, cluster.withPartitions(Collections.singletonMap(tp5, new PartitionInfo("topic-five", 1, null, null, null))));
        final StreamsMetadata expected = new StreamsMetadata(hostFour, Collections.singleton(globalTable), Collections.singleton(tp5));
        final Collection<StreamsMetadata> actual = metadataState.getAllMetadata();
        Assert.assertTrue(((("expected " + actual) + " to contain ") + expected), actual.contains(expected));
    }

    @Test
    public void shouldGetInstancesForStoreName() {
        final StreamsMetadata one = new StreamsMetadata(hostOne, Utils.mkSet(globalTable, "table-one", "table-two", "merged-table"), Utils.mkSet(topic1P0, topic2P1, topic4P0));
        final StreamsMetadata two = new StreamsMetadata(hostTwo, Utils.mkSet(globalTable, "table-two", "table-one", "merged-table"), Utils.mkSet(topic2P0, topic1P1));
        final Collection<StreamsMetadata> actual = metadataState.getAllMetadataForStore("table-one");
        Assert.assertEquals(2, actual.size());
        Assert.assertTrue(((("expected " + actual) + " to contain ") + one), actual.contains(one));
        Assert.assertTrue(((("expected " + actual) + " to contain ") + two), actual.contains(two));
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowIfStoreNameIsNullOnGetAllInstancesWithStore() {
        metadataState.getAllMetadataForStore(null);
    }

    @Test
    public void shouldReturnEmptyCollectionOnGetAllInstancesWithStoreWhenStoreDoesntExist() {
        final Collection<StreamsMetadata> actual = metadataState.getAllMetadataForStore("not-a-store");
        Assert.assertTrue(actual.isEmpty());
    }

    @Test
    public void shouldGetInstanceWithKey() {
        final TopicPartition tp4 = new TopicPartition("topic-three", 1);
        hostToPartitions.put(hostTwo, Utils.mkSet(topic2P0, tp4));
        metadataState.onChange(hostToPartitions, cluster.withPartitions(Collections.singletonMap(tp4, new PartitionInfo("topic-three", 1, null, null, null))));
        final StreamsMetadata expected = new StreamsMetadata(hostThree, Utils.mkSet(globalTable, "table-three"), Collections.singleton(topic3P0));
        final StreamsMetadata actual = metadataState.getMetadataWithKey("table-three", "the-key", Serdes.String().serializer());
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void shouldGetInstanceWithKeyAndCustomPartitioner() {
        final TopicPartition tp4 = new TopicPartition("topic-three", 1);
        hostToPartitions.put(hostTwo, Utils.mkSet(topic2P0, tp4));
        metadataState.onChange(hostToPartitions, cluster.withPartitions(Collections.singletonMap(tp4, new PartitionInfo("topic-three", 1, null, null, null))));
        final StreamsMetadata expected = new StreamsMetadata(hostTwo, Utils.mkSet(globalTable, "table-two", "table-three", "merged-table"), Utils.mkSet(topic2P0, tp4));
        final StreamsMetadata actual = metadataState.getMetadataWithKey("table-three", "the-key", partitioner);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void shouldReturnNotAvailableWhenClusterIsEmpty() {
        metadataState.onChange(Collections.<HostInfo, Set<TopicPartition>>emptyMap(), Cluster.empty());
        final StreamsMetadata result = metadataState.getMetadataWithKey("table-one", "a", Serdes.String().serializer());
        Assert.assertEquals(NOT_AVAILABLE, result);
    }

    @Test
    public void shouldGetInstanceWithKeyWithMergedStreams() {
        final TopicPartition topic2P2 = new TopicPartition("topic-two", 2);
        hostToPartitions.put(hostTwo, Utils.mkSet(topic2P0, topic1P1, topic2P2));
        metadataState.onChange(hostToPartitions, cluster.withPartitions(Collections.singletonMap(topic2P2, new PartitionInfo("topic-two", 2, null, null, null))));
        final StreamsMetadata expected = new StreamsMetadata(hostTwo, Utils.mkSet("global-table", "table-two", "table-one", "merged-table"), Utils.mkSet(topic2P0, topic1P1, topic2P2));
        final StreamsMetadata actual = metadataState.getMetadataWithKey("merged-table", "123", new StreamPartitioner<String, Object>() {
            @Override
            public Integer partition(final String topic, final String key, final Object value, final int numPartitions) {
                return 2;
            }
        });
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void shouldReturnNullOnGetWithKeyWhenStoreDoesntExist() {
        final StreamsMetadata actual = metadataState.getMetadataWithKey("not-a-store", "key", Serdes.String().serializer());
        Assert.assertNull(actual);
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowWhenKeyIsNull() {
        metadataState.getMetadataWithKey("table-three", null, Serdes.String().serializer());
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowWhenSerializerIsNull() {
        metadataState.getMetadataWithKey("table-three", "key", ((Serializer<Object>) (null)));
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowIfStoreNameIsNull() {
        metadataState.getMetadataWithKey(null, "key", Serdes.String().serializer());
    }

    @SuppressWarnings("unchecked")
    @Test(expected = NullPointerException.class)
    public void shouldThrowIfStreamPartitionerIsNull() {
        metadataState.getMetadataWithKey(null, "key", ((StreamPartitioner) (null)));
    }

    @Test
    public void shouldHaveGlobalStoreInAllMetadata() {
        final Collection<StreamsMetadata> metadata = metadataState.getAllMetadataForStore(globalTable);
        Assert.assertEquals(3, metadata.size());
        for (final StreamsMetadata streamsMetadata : metadata) {
            Assert.assertTrue(streamsMetadata.stateStoreNames().contains(globalTable));
        }
    }

    @Test
    public void shouldGetMyMetadataForGlobalStoreWithKey() {
        final StreamsMetadata metadata = metadataState.getMetadataWithKey(globalTable, "key", Serdes.String().serializer());
        Assert.assertEquals(hostOne, metadata.hostInfo());
    }

    @Test
    public void shouldGetAnyHostForGlobalStoreByKeyIfMyHostUnknown() {
        final StreamsMetadataState streamsMetadataState = new StreamsMetadataState(TopologyWrapper.getInternalTopologyBuilder(builder.build()), UNKNOWN_HOST);
        streamsMetadataState.onChange(hostToPartitions, cluster);
        Assert.assertNotNull(streamsMetadataState.getMetadataWithKey(globalTable, "key", Serdes.String().serializer()));
    }

    @Test
    public void shouldGetMyMetadataForGlobalStoreWithKeyAndPartitioner() {
        final StreamsMetadata metadata = metadataState.getMetadataWithKey(globalTable, "key", partitioner);
        Assert.assertEquals(hostOne, metadata.hostInfo());
    }

    @Test
    public void shouldGetAnyHostForGlobalStoreByKeyAndPartitionerIfMyHostUnknown() {
        final StreamsMetadataState streamsMetadataState = new StreamsMetadataState(TopologyWrapper.getInternalTopologyBuilder(builder.build()), UNKNOWN_HOST);
        streamsMetadataState.onChange(hostToPartitions, cluster);
        Assert.assertNotNull(streamsMetadataState.getMetadataWithKey(globalTable, "key", partitioner));
    }
}

