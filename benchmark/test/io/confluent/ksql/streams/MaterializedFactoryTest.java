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


import KsqlConfig.KSQL_USE_NAMED_INTERNAL_TOPICS;
import KsqlConfig.KSQL_USE_NAMED_INTERNAL_TOPICS_OFF;
import KsqlConfig.KSQL_USE_NAMED_INTERNAL_TOPICS_ON;
import MaterializedFactory.Materializer;
import StreamsConfig.NO_OPTIMIZATION;
import StreamsConfig.TOPOLOGY_OPTIMIZATION;
import com.google.common.collect.ImmutableMap;
import io.confluent.ksql.GenericRow;
import io.confluent.ksql.util.KsqlConfig;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.processor.StateStore;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class MaterializedFactoryTest {
    private static final String OP_NAME = "kdot";

    @Mock
    private Serde<String> keySerde;

    @Mock
    private Serde<GenericRow> rowSerde;

    @Mock
    private Materializer materializer;

    @Mock
    private Materialized<String, GenericRow, StateStore> materialized;

    @Test
    public void shouldCreateMaterializedCorrectlyWhenOptimizationsDisabled() {
        // Given:
        final KsqlConfig ksqlConfig = new KsqlConfig(ImmutableMap.of(TOPOLOGY_OPTIMIZATION, NO_OPTIMIZATION, KSQL_USE_NAMED_INTERNAL_TOPICS, KSQL_USE_NAMED_INTERNAL_TOPICS_OFF));
        Mockito.when(materializer.materializedWith(keySerde, rowSerde)).thenReturn(materialized);
        // When:
        final Materialized<String, GenericRow, StateStore> returned = MaterializedFactory.create(ksqlConfig, materializer).create(keySerde, rowSerde, MaterializedFactoryTest.OP_NAME);
        // Then:
        Assert.assertThat(returned, Matchers.is(materialized));
        Mockito.verify(materializer).materializedWith(keySerde, rowSerde);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldCreateJoinedCorrectlyWhenOptimizationsEnabled() {
        // Given:
        final KsqlConfig ksqlConfig = new KsqlConfig(ImmutableMap.of(KSQL_USE_NAMED_INTERNAL_TOPICS, KSQL_USE_NAMED_INTERNAL_TOPICS_ON));
        final Materialized asName = Mockito.mock(Materialized.class);
        Mockito.when(materializer.materializedAs(MaterializedFactoryTest.OP_NAME)).thenReturn(asName);
        final Materialized withKeySerde = Mockito.mock(Materialized.class);
        Mockito.when(asName.withKeySerde(keySerde)).thenReturn(withKeySerde);
        final Materialized withRowSerde = Mockito.mock(Materialized.class);
        Mockito.when(withKeySerde.withValueSerde(rowSerde)).thenReturn(withRowSerde);
        // When:
        final Materialized<String, GenericRow, StateStore> returned = MaterializedFactory.create(ksqlConfig, materializer).create(keySerde, rowSerde, MaterializedFactoryTest.OP_NAME);
        // Then:
        Assert.assertThat(returned, Matchers.is(withRowSerde));
        Mockito.verify(materializer).materializedAs(MaterializedFactoryTest.OP_NAME);
        Mockito.verify(asName).withKeySerde(keySerde);
        Mockito.verify(withKeySerde).withValueSerde(rowSerde);
    }
}

