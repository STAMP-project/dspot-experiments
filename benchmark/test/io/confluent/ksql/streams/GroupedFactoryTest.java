/**
 * Copyright 2018 Confluent Inc.
 *
 * Licensed under the Confluent Community License (the "License"); you may not use
 * this file except in compliance with the License.  You may obtain a copy of the
 * License at
 *
 * http://www.confluent.io/confluent-community-license
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */
package io.confluent.ksql.streams;


import GroupedFactory.Grouper;
import KsqlConfig.KSQL_USE_NAMED_INTERNAL_TOPICS;
import KsqlConfig.KSQL_USE_NAMED_INTERNAL_TOPICS_OFF;
import KsqlConfig.KSQL_USE_NAMED_INTERNAL_TOPICS_ON;
import StreamsConfig.NO_OPTIMIZATION;
import StreamsConfig.TOPOLOGY_OPTIMIZATION;
import com.google.common.collect.ImmutableMap;
import io.confluent.ksql.GenericRow;
import io.confluent.ksql.util.KsqlConfig;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.streams.kstream.Grouped;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class GroupedFactoryTest {
    private static final String OP_NAME = "kdot";

    @Mock
    private Serde<String> keySerde;

    @Mock
    private Serde<GenericRow> rowSerde;

    @Mock
    private Grouper grouper;

    @Mock
    private Grouped<String, GenericRow> grouped;

    @Test
    public void shouldCreateGroupedCorrectlyWhenOptimizationsDisabled() {
        // Given:
        final KsqlConfig ksqlConfig = new KsqlConfig(ImmutableMap.of(TOPOLOGY_OPTIMIZATION, NO_OPTIMIZATION, KSQL_USE_NAMED_INTERNAL_TOPICS, KSQL_USE_NAMED_INTERNAL_TOPICS_OFF));
        Mockito.when(grouper.groupedWith(null, keySerde, rowSerde)).thenReturn(grouped);
        // When:
        final Grouped returned = GroupedFactory.create(ksqlConfig, grouper).create(GroupedFactoryTest.OP_NAME, keySerde, rowSerde);
        // Then:
        Assert.assertThat(returned, Matchers.is(grouped));
        Mockito.verify(grouper).groupedWith(null, keySerde, rowSerde);
    }

    @Test
    public void shouldCreateGroupedCorrectlyWhenOptimationsEnabled() {
        // Given:
        final KsqlConfig ksqlConfig = new KsqlConfig(ImmutableMap.of(KSQL_USE_NAMED_INTERNAL_TOPICS, KSQL_USE_NAMED_INTERNAL_TOPICS_ON));
        Mockito.when(grouper.groupedWith(GroupedFactoryTest.OP_NAME, keySerde, rowSerde)).thenReturn(grouped);
        // When:
        final Grouped returned = GroupedFactory.create(ksqlConfig, grouper).create(GroupedFactoryTest.OP_NAME, keySerde, rowSerde);
        // Then:
        Assert.assertThat(returned, Matchers.is(grouped));
        Mockito.verify(grouper).groupedWith(GroupedFactoryTest.OP_NAME, keySerde, rowSerde);
    }
}

