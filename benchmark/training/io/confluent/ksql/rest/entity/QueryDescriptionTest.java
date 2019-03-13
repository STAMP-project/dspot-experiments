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
package io.confluent.ksql.rest.entity;


import DataSource.DataSourceType;
import SchemaInfo.Type;
import io.confluent.ksql.function.FunctionRegistry;
import io.confluent.ksql.logging.processing.ProcessingLogContext;
import io.confluent.ksql.metastore.KsqlStream;
import io.confluent.ksql.metastore.KsqlTopic;
import io.confluent.ksql.planner.plan.OutputNode;
import io.confluent.ksql.planner.plan.PlanNodeId;
import io.confluent.ksql.planner.plan.StructuredDataSourceNode;
import io.confluent.ksql.query.QueryId;
import io.confluent.ksql.serde.json.KsqlJsonTopicSerDe;
import io.confluent.ksql.services.ServiceContext;
import io.confluent.ksql.structured.SchemaKStream;
import io.confluent.ksql.util.KsqlConfig;
import io.confluent.ksql.util.PersistentQueryMetadata;
import io.confluent.ksql.util.QueryIdGenerator;
import io.confluent.ksql.util.QueryMetadata;
import io.confluent.ksql.util.timestamp.MetadataTimestampExtractionPolicy;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.TopologyDescription;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class QueryDescriptionTest {
    private static final Schema SCHEMA = SchemaBuilder.struct().field("field1", SchemaBuilder.int32().build()).field("field2", SchemaBuilder.string().build()).build();

    private static final String STATEMENT = "statement";

    @Mock
    private Consumer<QueryMetadata> queryCloseCallback;

    private static class FakeSourceNode extends StructuredDataSourceNode {
        FakeSourceNode(final String name) {
            super(new PlanNodeId("fake"), new KsqlStream(QueryDescriptionTest.STATEMENT, name, QueryDescriptionTest.SCHEMA, QueryDescriptionTest.SCHEMA.fields().get(0), new MetadataTimestampExtractionPolicy(), new KsqlTopic(name, name, new KsqlJsonTopicSerDe(), false), Serdes.String()), QueryDescriptionTest.SCHEMA);
        }
    }

    private static class FakeOutputNode extends OutputNode {
        FakeOutputNode(final QueryDescriptionTest.FakeSourceNode sourceNode) {
            super(new PlanNodeId("fake"), sourceNode, QueryDescriptionTest.SCHEMA, Optional.of(1), new MetadataTimestampExtractionPolicy());
        }

        @Override
        public Field getKeyField() {
            return null;
        }

        @Override
        public SchemaKStream<?> buildStream(final StreamsBuilder builder, final KsqlConfig ksqlConfig, final ServiceContext serviceContext, final ProcessingLogContext processingLogContext, final FunctionRegistry functionRegistry, final QueryId queryId) {
            return null;
        }

        @Override
        public QueryId getQueryId(final QueryIdGenerator queryIdGenerator) {
            return new QueryId("fake");
        }
    }

    @Test
    public void shouldSetFieldsCorrectlyForQueryMetadata() {
        final KafkaStreams queryStreams = niceMock(KafkaStreams.class);
        final QueryDescriptionTest.FakeSourceNode sourceNode = new QueryDescriptionTest.FakeSourceNode("source");
        final OutputNode outputNode = new QueryDescriptionTest.FakeOutputNode(sourceNode);
        final Topology topology = mock(Topology.class);
        final TopologyDescription topologyDescription = mock(TopologyDescription.class);
        expect(topology.describe()).andReturn(topologyDescription);
        replay(queryStreams, topology, topologyDescription);
        final Map<String, Object> streamsProperties = Collections.singletonMap("k", "v");
        final QueryMetadata queryMetadata = new io.confluent.ksql.util.QueuedQueryMetadata("test statement", queryStreams, outputNode, "execution plan", new LinkedBlockingQueue(), DataSourceType.KSTREAM, "app id", topology, streamsProperties, streamsProperties, queryCloseCallback);
        final QueryDescription queryDescription = QueryDescription.forQueryMetadata(queryMetadata);
        Assert.assertThat(queryDescription.getId().getId(), CoreMatchers.equalTo(""));
        Assert.assertThat(queryDescription.getExecutionPlan(), CoreMatchers.equalTo("execution plan"));
        Assert.assertThat(queryDescription.getSources(), CoreMatchers.equalTo(Collections.singleton("source")));
        Assert.assertThat(queryDescription.getStatementText(), CoreMatchers.equalTo("test statement"));
        Assert.assertThat(queryDescription.getTopology(), CoreMatchers.equalTo(topologyDescription.toString()));
        Assert.assertThat(queryDescription.getFields(), CoreMatchers.equalTo(Arrays.asList(new FieldInfo("field1", new SchemaInfo(Type.INTEGER, null, null)), new FieldInfo("field2", new SchemaInfo(Type.STRING, null, null)))));
    }

    @Test
    public void shouldSetFieldsCorrectlyForPersistentQueryMetadata() {
        final KafkaStreams queryStreams = niceMock(KafkaStreams.class);
        final QueryDescriptionTest.FakeSourceNode sourceNode = new QueryDescriptionTest.FakeSourceNode("source");
        final OutputNode outputNode = new QueryDescriptionTest.FakeOutputNode(sourceNode);
        final Topology topology = mock(Topology.class);
        final TopologyDescription topologyDescription = mock(TopologyDescription.class);
        expect(topology.describe()).andReturn(topologyDescription);
        replay(topology, topologyDescription);
        final KsqlTopic sinkTopic = new KsqlTopic("fake_sink", "fake_sink", new KsqlJsonTopicSerDe(), true);
        final KsqlStream fakeSink = new KsqlStream(QueryDescriptionTest.STATEMENT, "fake_sink", QueryDescriptionTest.SCHEMA, QueryDescriptionTest.SCHEMA.fields().get(0), new MetadataTimestampExtractionPolicy(), sinkTopic, Serdes.String());
        final Map<String, Object> streamsProperties = Collections.singletonMap("k", "v");
        final PersistentQueryMetadata queryMetadata = new PersistentQueryMetadata("test statement", queryStreams, outputNode, fakeSink, "execution plan", new QueryId("query_id"), DataSourceType.KSTREAM, "app id", sinkTopic, topology, streamsProperties, streamsProperties, queryCloseCallback);
        final QueryDescription queryDescription = QueryDescription.forQueryMetadata(queryMetadata);
        Assert.assertThat(queryDescription.getId().getId(), CoreMatchers.equalTo("query_id"));
        Assert.assertThat(queryDescription.getSinks(), CoreMatchers.equalTo(Collections.singleton("fake_sink")));
    }
}

