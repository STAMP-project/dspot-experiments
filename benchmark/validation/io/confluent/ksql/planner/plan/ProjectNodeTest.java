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


import io.confluent.ksql.function.InternalFunctionRegistry;
import io.confluent.ksql.logging.processing.ProcessingLogContext;
import io.confluent.ksql.parser.tree.BooleanLiteral;
import io.confluent.ksql.query.QueryId;
import io.confluent.ksql.services.ServiceContext;
import io.confluent.ksql.services.TestServiceContext;
import io.confluent.ksql.structured.SchemaKStream;
import io.confluent.ksql.util.KsqlConfig;
import io.confluent.ksql.util.KsqlException;
import io.confluent.ksql.util.SelectExpression;
import java.util.Arrays;
import java.util.Collections;
import org.apache.kafka.streams.StreamsBuilder;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


public class ProjectNodeTest {
    @Mock
    private PlanNode source;

    @Mock
    private SchemaKStream stream;

    private final StreamsBuilder builder = new StreamsBuilder();

    private final KsqlConfig ksqlConfig = new KsqlConfig(Collections.emptyMap());

    private final ServiceContext serviceContext = TestServiceContext.create();

    private final ProcessingLogContext processingLogContext = ProcessingLogContext.create();

    private final InternalFunctionRegistry functionRegistry = new InternalFunctionRegistry();

    private final QueryId queryId = new QueryId("project-test");

    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    @Test(expected = KsqlException.class)
    public void shouldThrowKsqlExcptionIfSchemaSizeDoesntMatchProjection() {
        final ProjectNode node = buildNode(Collections.singletonList(new BooleanLiteral("true")));
        node.buildStream(builder, ksqlConfig, serviceContext, processingLogContext, functionRegistry, queryId);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldCreateProjectionWithFieldNameExpressionPairs() {
        // Given:
        final BooleanLiteral trueExpression = new BooleanLiteral("true");
        final BooleanLiteral falseExpression = new BooleanLiteral("false");
        Mockito.when(stream.select(ArgumentMatchers.anyList(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(stream);
        final ProjectNode node = buildNode(Arrays.asList(trueExpression, falseExpression));
        // When:
        node.buildStream(builder, ksqlConfig, serviceContext, processingLogContext, functionRegistry, queryId);
        // Then:
        Mockito.verify(stream).select(ArgumentMatchers.eq(Arrays.asList(SelectExpression.of("field1", trueExpression), SelectExpression.of("field2", falseExpression))), ArgumentMatchers.eq(node.buildNodeContext(queryId)), ArgumentMatchers.same(processingLogContext));
        Mockito.verify(source, Mockito.times(1)).buildStream(ArgumentMatchers.same(builder), ArgumentMatchers.same(ksqlConfig), ArgumentMatchers.same(serviceContext), ArgumentMatchers.same(processingLogContext), ArgumentMatchers.same(functionRegistry), ArgumentMatchers.same(queryId));
    }
}

