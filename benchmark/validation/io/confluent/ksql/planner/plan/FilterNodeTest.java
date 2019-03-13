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


import io.confluent.ksql.function.FunctionRegistry;
import io.confluent.ksql.function.InternalFunctionRegistry;
import io.confluent.ksql.logging.processing.ProcessingLogContext;
import io.confluent.ksql.parser.tree.Expression;
import io.confluent.ksql.query.QueryId;
import io.confluent.ksql.services.ServiceContext;
import io.confluent.ksql.structured.SchemaKStream;
import io.confluent.ksql.util.KsqlConfig;
import java.util.Collections;
import java.util.Map;
import org.apache.kafka.streams.StreamsBuilder;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


public class FilterNodeTest {
    private final KsqlConfig ksqlConfig = new KsqlConfig(Collections.emptyMap());

    private final FunctionRegistry functionRegistry = new InternalFunctionRegistry();

    private final PlanNodeId nodeId = new PlanNodeId("nodeid");

    private final Map<String, Object> props = Collections.emptyMap();

    private final QueryId queryId = new QueryId("queryid");

    @Mock
    private Expression predicate;

    @Mock
    private StreamsBuilder builder;

    @Mock
    private PlanNode sourceNode;

    @Mock
    private SchemaKStream schemaKStream;

    @Mock
    private ServiceContext serviceContext;

    @Mock
    private ProcessingLogContext processingLogContext;

    private FilterNode node;

    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    @Test
    public void shouldApplyFilterCorrectly() {
        // When:
        node.buildStream(builder, ksqlConfig, serviceContext, processingLogContext, functionRegistry, queryId);
        // Then:
        Mockito.verify(sourceNode).buildStream(ArgumentMatchers.same(builder), ArgumentMatchers.same(ksqlConfig), ArgumentMatchers.same(serviceContext), ArgumentMatchers.same(processingLogContext), ArgumentMatchers.same(functionRegistry), ArgumentMatchers.same(queryId));
        Mockito.verify(schemaKStream).filter(ArgumentMatchers.same(predicate), ArgumentMatchers.eq(node.buildNodeContext(queryId)), ArgumentMatchers.same(processingLogContext));
    }
}

