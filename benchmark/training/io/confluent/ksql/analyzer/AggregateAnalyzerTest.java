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
package io.confluent.ksql.analyzer;


import com.google.common.collect.ImmutableList;
import io.confluent.ksql.function.InternalFunctionRegistry;
import io.confluent.ksql.parser.tree.DereferenceExpression;
import io.confluent.ksql.parser.tree.Expression;
import io.confluent.ksql.parser.tree.FunctionCall;
import io.confluent.ksql.parser.tree.QualifiedName;
import io.confluent.ksql.util.KsqlException;
import io.confluent.ksql.util.SchemaUtil;
import java.util.ArrayList;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;


@SuppressWarnings("unchecked")
public class AggregateAnalyzerTest {
    private static final DereferenceExpression DEFAULT_ARGUMENT = new DereferenceExpression(new io.confluent.ksql.parser.tree.QualifiedNameReference(QualifiedName.of("ORDERS")), SchemaUtil.ROWTIME_NAME);

    private static final DereferenceExpression COL0 = new DereferenceExpression(new io.confluent.ksql.parser.tree.QualifiedNameReference(QualifiedName.of("ORDERS")), "COL0");

    private static final DereferenceExpression COL1 = new DereferenceExpression(new io.confluent.ksql.parser.tree.QualifiedNameReference(QualifiedName.of("ORDERS")), "COL1");

    private static final DereferenceExpression COL2 = new DereferenceExpression(new io.confluent.ksql.parser.tree.QualifiedNameReference(QualifiedName.of("ORDERS")), "COL2");

    private static final FunctionCall FUNCTION_CALL = new FunctionCall(QualifiedName.of("UCASE"), ImmutableList.of(AggregateAnalyzerTest.COL0));

    private static final FunctionCall AGG_FUNCTION_CALL = new FunctionCall(QualifiedName.of("MAX"), ImmutableList.of(AggregateAnalyzerTest.COL0, AggregateAnalyzerTest.COL1));

    private final InternalFunctionRegistry functionRegistry = new InternalFunctionRegistry();

    private MutableAggregateAnalysis analysis;

    private AggregateAnalyzer analyzer;

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldCaptureSelectNonAggregateFunctionArguments() {
        // When:
        analyzer.processSelect(AggregateAnalyzerTest.FUNCTION_CALL);
        // Then:
        MatcherAssert.assertThat(analysis.getNonAggregateSelectExpressions().get(AggregateAnalyzerTest.FUNCTION_CALL), Matchers.contains(AggregateAnalyzerTest.COL0));
    }

    @Test
    public void shouldCaptureSelectDereferencedExpression() {
        // When:
        analyzer.processSelect(AggregateAnalyzerTest.COL0);
        // Then:
        MatcherAssert.assertThat(analysis.getNonAggregateSelectExpressions().get(AggregateAnalyzerTest.COL0), Matchers.contains(AggregateAnalyzerTest.COL0));
    }

    @Test
    public void shouldCaptureOtherSelectsWithEmptySet() {
        // Given:
        final Expression someExpression = Mockito.mock(Expression.class);
        // When:
        analyzer.processSelect(someExpression);
        // Then:
        MatcherAssert.assertThat(analysis.getNonAggregateSelectExpressions().get(someExpression), Matchers.is(Matchers.empty()));
    }

    @Test
    public void shouldNotCaptureOtherNonAggregateFunctionArgumentsAsNonAggSelectColumns() {
        // When:
        analyzer.processGroupBy(AggregateAnalyzerTest.FUNCTION_CALL);
        analyzer.processHaving(AggregateAnalyzerTest.FUNCTION_CALL);
        // Then:
        MatcherAssert.assertThat(analysis.getNonAggregateSelectExpressions().keySet(), Matchers.is(Matchers.empty()));
    }

    @Test
    public void shouldNotCaptureAggregateFunctionArgumentsAsNonAggSelectColumns() {
        // When:
        analyzer.processSelect(AggregateAnalyzerTest.AGG_FUNCTION_CALL);
        analyzer.processHaving(AggregateAnalyzerTest.AGG_FUNCTION_CALL);
        // Then:
        MatcherAssert.assertThat(analysis.getNonAggregateSelectExpressions().keySet(), Matchers.is(Matchers.empty()));
    }

    @Test
    public void shouldThrowOnGroupByAggregateFunction() {
        // Then:
        expectedException.expect(KsqlException.class);
        expectedException.expectMessage("GROUP BY does not support aggregate functions: MAX is an aggregate function.");
        // When:
        analyzer.processGroupBy(AggregateAnalyzerTest.AGG_FUNCTION_CALL);
    }

    @Test
    public void shouldCaptureSelectNonAggregateFunctionArgumentsAsRequired() {
        // When:
        analyzer.processSelect(AggregateAnalyzerTest.FUNCTION_CALL);
        // Then:
        MatcherAssert.assertThat(analysis.getRequiredColumns(), Matchers.contains(AggregateAnalyzerTest.COL0));
    }

    @Test
    public void shouldCaptureHavingNonAggregateFunctionArgumentsAsRequired() {
        // When:
        analyzer.processHaving(AggregateAnalyzerTest.FUNCTION_CALL);
        // Then:
        MatcherAssert.assertThat(analysis.getRequiredColumns(), Matchers.contains(AggregateAnalyzerTest.COL0));
    }

    @Test
    public void shouldCaptureGroupByNonAggregateFunctionArgumentsAsRequired() {
        // When:
        analyzer.processGroupBy(AggregateAnalyzerTest.FUNCTION_CALL);
        // Then:
        MatcherAssert.assertThat(analysis.getRequiredColumns(), Matchers.contains(AggregateAnalyzerTest.COL0));
    }

    @Test
    public void shouldCaptureSelectAggregateFunctionArgumentsAsRequired() {
        // When:
        analyzer.processSelect(AggregateAnalyzerTest.AGG_FUNCTION_CALL);
        // Then:
        MatcherAssert.assertThat(analysis.getRequiredColumns(), Matchers.contains(AggregateAnalyzerTest.COL0, AggregateAnalyzerTest.COL1));
    }

    @Test
    public void shouldCaptureHavingAggregateFunctionArgumentsAsRequired() {
        // When:
        analyzer.processHaving(AggregateAnalyzerTest.AGG_FUNCTION_CALL);
        // Then:
        MatcherAssert.assertThat(analysis.getRequiredColumns(), Matchers.contains(AggregateAnalyzerTest.COL0, AggregateAnalyzerTest.COL1));
    }

    @Test
    public void shouldNotCaptureNonAggregateFunction() {
        // When:
        analyzer.processSelect(AggregateAnalyzerTest.FUNCTION_CALL);
        analyzer.processHaving(AggregateAnalyzerTest.FUNCTION_CALL);
        analyzer.processGroupBy(AggregateAnalyzerTest.FUNCTION_CALL);
        // Then:
        MatcherAssert.assertThat(analysis.getAggregateFunctions(), Matchers.is(Matchers.empty()));
    }

    @Test
    public void shouldCaptureSelectAggregateFunction() {
        // When:
        analyzer.processSelect(AggregateAnalyzerTest.AGG_FUNCTION_CALL);
        // Then:
        MatcherAssert.assertThat(analysis.getAggregateFunctions(), Matchers.contains(AggregateAnalyzerTest.AGG_FUNCTION_CALL));
    }

    @Test
    public void shouldCaptureHavingAggregateFunction() {
        // When:
        analyzer.processHaving(AggregateAnalyzerTest.AGG_FUNCTION_CALL);
        // Then:
        MatcherAssert.assertThat(analysis.getAggregateFunctions(), Matchers.contains(AggregateAnalyzerTest.AGG_FUNCTION_CALL));
    }

    @Test
    public void shouldThrowOnNestedSelectAggFunctions() {
        // Given:
        final FunctionCall nestedCall = new FunctionCall(QualifiedName.of("MIN"), ImmutableList.of(AggregateAnalyzerTest.AGG_FUNCTION_CALL, AggregateAnalyzerTest.COL2));
        // Then:
        expectedException.expect(KsqlException.class);
        expectedException.expectMessage("Aggregate functions can not be nested: MIN(MAX())");
        // When:
        analyzer.processSelect(nestedCall);
    }

    @Test
    public void shouldThrowOnNestedHavingAggFunctions() {
        // Given:
        final FunctionCall nestedCall = new FunctionCall(QualifiedName.of("MIN"), ImmutableList.of(AggregateAnalyzerTest.AGG_FUNCTION_CALL, AggregateAnalyzerTest.COL2));
        // Then:
        expectedException.expect(KsqlException.class);
        expectedException.expectMessage("Aggregate functions can not be nested: MIN(MAX())");
        // When:
        analyzer.processHaving(nestedCall);
    }

    @Test
    public void shouldCaptureNonAggregateFunctionArgumentsWithNestedAggFunction() {
        // Given:
        final FunctionCall nonAggWithNestedAggFunc = new FunctionCall(QualifiedName.of("SUBSTRING"), ImmutableList.of(AggregateAnalyzerTest.COL2, AggregateAnalyzerTest.AGG_FUNCTION_CALL, AggregateAnalyzerTest.COL1));
        // When:
        analyzer.processSelect(nonAggWithNestedAggFunc);
        // Then:
        MatcherAssert.assertThat(analysis.getAggregateSelectFields(), Matchers.containsInAnyOrder(AggregateAnalyzerTest.COL1, AggregateAnalyzerTest.COL2));
    }

    @Test
    public void shouldNotCaptureNonAggregateFunctionArgumentsWhenNestedInsideAggFunction() {
        // Given:
        final FunctionCall nonAggFunc = new FunctionCall(QualifiedName.of("ROUND"), ImmutableList.of(AggregateAnalyzerTest.COL0));
        final FunctionCall aggFuncWithNestedNonAgg = new FunctionCall(QualifiedName.of("MAX"), ImmutableList.of(AggregateAnalyzerTest.COL1, nonAggFunc));
        // When:
        analyzer.processSelect(aggFuncWithNestedNonAgg);
        // Then:
        MatcherAssert.assertThat(analysis.getNonAggregateSelectExpressions().keySet(), Matchers.is(Matchers.empty()));
    }

    @Test
    public void shouldCaptureDefaultFunctionArguments() {
        // Given:
        final FunctionCall emptyFunc = new FunctionCall(QualifiedName.of("COUNT"), new ArrayList());
        // When:
        analyzer.processSelect(emptyFunc);
        // Then:
        MatcherAssert.assertThat(analysis.getAggregateFunctions(), Matchers.containsInAnyOrder(emptyFunc));
        MatcherAssert.assertThat(analysis.getRequiredColumns(), Matchers.contains(AggregateAnalyzerTest.DEFAULT_ARGUMENT));
    }

    @Test
    public void shouldAddDefaultArgToFunctionCallWithNoArgs() {
        // Given:
        final FunctionCall emptyFunc = new FunctionCall(QualifiedName.of("COUNT"), new ArrayList());
        // When:
        analyzer.processSelect(emptyFunc);
        // Then:
        MatcherAssert.assertThat(emptyFunc.getArguments(), Matchers.contains(AggregateAnalyzerTest.DEFAULT_ARGUMENT));
    }
}

