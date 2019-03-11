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
package io.confluent.ksql.planner.plan;


import TopologyDescription.Processor;
import TopologyDescription.Source;
import com.google.common.collect.ImmutableSet;
import io.confluent.ksql.function.InternalFunctionRegistry;
import io.confluent.ksql.metastore.MetaStore;
import io.confluent.ksql.query.QueryId;
import io.confluent.ksql.services.ServiceContext;
import io.confluent.ksql.structured.SchemaKStream;
import io.confluent.ksql.testutils.AnalysisTestUtil;
import io.confluent.ksql.util.MetaStoreFixture;
import io.confluent.ksql.util.QueryIdGenerator;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.TopologyDescription;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.Mockito;


public class KsqlBareOutputNodeTest {
    private static final String SOURCE_NODE = "KSTREAM-SOURCE-0000000000";

    private static final String SOURCE_MAPVALUES_NODE = "KSTREAM-MAPVALUES-0000000001";

    private static final String TRANSFORM_NODE = "KSTREAM-TRANSFORMVALUES-0000000002";

    private static final String FILTER_NODE = "KSTREAM-FILTER-0000000003";

    private static final String FILTER_MAPVALUES_NODE = "KSTREAM-MAPVALUES-0000000004";

    private static final String FOREACH_NODE = "KSTREAM-FOREACH-0000000005";

    private SchemaKStream stream;

    private StreamsBuilder builder;

    private final MetaStore metaStore = MetaStoreFixture.getNewMetaStore(new InternalFunctionRegistry());

    private ServiceContext serviceContext;

    private final QueryId queryId = new QueryId("output-test");

    @Test
    public void shouldBuildSourceNode() {
        final TopologyDescription.Source node = ((TopologyDescription.Source) (getNodeByName(KsqlBareOutputNodeTest.SOURCE_NODE)));
        final List<String> successors = node.successors().stream().map(TopologyDescription.Node::name).collect(Collectors.toList());
        MatcherAssert.assertThat(node.predecessors(), CoreMatchers.equalTo(Collections.emptySet()));
        MatcherAssert.assertThat(successors, CoreMatchers.equalTo(Collections.singletonList(KsqlBareOutputNodeTest.SOURCE_MAPVALUES_NODE)));
        MatcherAssert.assertThat(node.topicSet(), CoreMatchers.equalTo(ImmutableSet.of("test1")));
    }

    @Test
    public void shouldBuildMapNode() {
        PlanTestUtil.verifyProcessorNode(((TopologyDescription.Processor) (getNodeByName(KsqlBareOutputNodeTest.SOURCE_MAPVALUES_NODE))), Collections.singletonList(KsqlBareOutputNodeTest.SOURCE_NODE), Collections.singletonList(KsqlBareOutputNodeTest.TRANSFORM_NODE));
    }

    @Test
    public void shouldBuildTransformNode() {
        final TopologyDescription.Processor node = ((TopologyDescription.Processor) (getNodeByName(KsqlBareOutputNodeTest.TRANSFORM_NODE)));
        PlanTestUtil.verifyProcessorNode(node, Collections.singletonList(KsqlBareOutputNodeTest.SOURCE_MAPVALUES_NODE), Collections.singletonList(KsqlBareOutputNodeTest.FILTER_NODE));
    }

    @Test
    public void shouldBuildFilterNode() {
        final TopologyDescription.Processor node = ((TopologyDescription.Processor) (getNodeByName(KsqlBareOutputNodeTest.FILTER_NODE)));
        PlanTestUtil.verifyProcessorNode(node, Collections.singletonList(KsqlBareOutputNodeTest.TRANSFORM_NODE), Collections.singletonList(KsqlBareOutputNodeTest.FILTER_MAPVALUES_NODE));
    }

    @Test
    public void shouldBuildMapValuesNode() {
        final TopologyDescription.Processor node = ((TopologyDescription.Processor) (getNodeByName(KsqlBareOutputNodeTest.FILTER_MAPVALUES_NODE)));
        PlanTestUtil.verifyProcessorNode(node, Collections.singletonList(KsqlBareOutputNodeTest.FILTER_NODE), Collections.singletonList(KsqlBareOutputNodeTest.FOREACH_NODE));
    }

    @Test
    public void shouldBuildForEachNode() {
        final TopologyDescription.Processor node = ((TopologyDescription.Processor) (getNodeByName(KsqlBareOutputNodeTest.FOREACH_NODE)));
        PlanTestUtil.verifyProcessorNode(node, Collections.singletonList(KsqlBareOutputNodeTest.FILTER_MAPVALUES_NODE), Collections.emptyList());
    }

    @Test
    public void shouldCreateCorrectSchema() {
        final Schema schema = stream.getSchema();
        MatcherAssert.assertThat(schema.fields(), CoreMatchers.equalTo(Arrays.asList(new org.apache.kafka.connect.data.Field("COL0", 0, Schema.OPTIONAL_INT64_SCHEMA), new org.apache.kafka.connect.data.Field("COL2", 1, Schema.OPTIONAL_STRING_SCHEMA), new org.apache.kafka.connect.data.Field("COL3", 2, Schema.OPTIONAL_FLOAT64_SCHEMA))));
    }

    @Test
    public void shouldComputeQueryIdCorrectly() {
        // Given:
        final KsqlBareOutputNode node = ((KsqlBareOutputNode) (AnalysisTestUtil.buildLogicalPlan("select col0 from test1;", metaStore)));
        final QueryIdGenerator queryIdGenerator = Mockito.mock(QueryIdGenerator.class);
        // When:
        final Set<QueryId> ids = IntStream.range(0, 100).mapToObj(( i) -> node.getQueryId(queryIdGenerator)).collect(Collectors.toSet());
        // Then:
        MatcherAssert.assertThat(ids.size(), CoreMatchers.equalTo(100));
        Mockito.verifyNoMoreInteractions(queryIdGenerator);
    }

    @Test
    public void shouldSetOutputNode() {
        MatcherAssert.assertThat(stream.outputNode(), CoreMatchers.instanceOf(KsqlBareOutputNode.class));
    }
}

