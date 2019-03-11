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


import StreamsPartitionAssignor.CopartitionedTopicsValidator;
import StreamsPartitionAssignor.InternalTopicMetadata;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.utils.Utils;
import org.apache.kafka.streams.errors.TopologyException;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


public class CopartitionedTopicsValidatorTest {
    private final CopartitionedTopicsValidator validator = new StreamsPartitionAssignor.CopartitionedTopicsValidator("thread");

    private final Map<TopicPartition, PartitionInfo> partitions = new HashMap<>();

    private final Cluster cluster = Cluster.empty();

    @Test(expected = IllegalStateException.class)
    public void shouldThrowTopologyBuilderExceptionIfNoPartitionsFoundForCoPartitionedTopic() {
        validator.validate(Collections.singleton("topic"), Collections.emptyMap(), cluster);
    }

    @Test(expected = TopologyException.class)
    public void shouldThrowTopologyBuilderExceptionIfPartitionCountsForCoPartitionedTopicsDontMatch() {
        partitions.remove(new TopicPartition("second", 0));
        validator.validate(Utils.mkSet("first", "second"), Collections.emptyMap(), cluster.withPartitions(partitions));
    }

    @Test
    public void shouldEnforceCopartitioningOnRepartitionTopics() {
        final StreamsPartitionAssignor.InternalTopicMetadata metadata = createTopicMetadata("repartitioned", 10);
        validator.validate(Utils.mkSet("first", "second", metadata.config.name()), Collections.singletonMap(metadata.config.name(), metadata), cluster.withPartitions(partitions));
        MatcherAssert.assertThat(metadata.numPartitions, CoreMatchers.equalTo(2));
    }

    @Test
    public void shouldSetNumPartitionsToMaximumPartitionsWhenAllTopicsAreRepartitionTopics() {
        final StreamsPartitionAssignor.InternalTopicMetadata one = createTopicMetadata("one", 1);
        final StreamsPartitionAssignor.InternalTopicMetadata two = createTopicMetadata("two", 15);
        final StreamsPartitionAssignor.InternalTopicMetadata three = createTopicMetadata("three", 5);
        final Map<String, StreamsPartitionAssignor.InternalTopicMetadata> repartitionTopicConfig = new HashMap<>();
        repartitionTopicConfig.put(one.config.name(), one);
        repartitionTopicConfig.put(two.config.name(), two);
        repartitionTopicConfig.put(three.config.name(), three);
        validator.validate(Utils.mkSet(one.config.name(), two.config.name(), three.config.name()), repartitionTopicConfig, cluster);
        MatcherAssert.assertThat(one.numPartitions, CoreMatchers.equalTo(15));
        MatcherAssert.assertThat(two.numPartitions, CoreMatchers.equalTo(15));
        MatcherAssert.assertThat(three.numPartitions, CoreMatchers.equalTo(15));
    }
}

