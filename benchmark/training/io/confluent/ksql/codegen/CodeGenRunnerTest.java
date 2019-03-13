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
package io.confluent.ksql.codegen;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.confluent.ksql.analyzer.Analysis;
import io.confluent.ksql.function.InternalFunctionRegistry;
import io.confluent.ksql.function.udf.Kudf;
import io.confluent.ksql.metastore.MutableMetaStore;
import io.confluent.ksql.parser.tree.Expression;
import io.confluent.ksql.testutils.AnalysisTestUtil;
import io.confluent.ksql.util.ExpressionMetadata;
import io.confluent.ksql.util.KsqlConfig;
import io.confluent.ksql.util.KsqlException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.internal.matchers.ThrowableMessageMatcher;
import org.junit.rules.ExpectedException;


@SuppressWarnings("SameParameterValue")
public class CodeGenRunnerTest {
    private static final int INT64_INDEX1 = 0;

    private static final int STRING_INDEX1 = 1;

    private static final int STRING_INDEX2 = 2;

    private static final int FLOAT64_INDEX1 = 3;

    private static final int FLOAT64_INDEX2 = 4;

    private static final int INT32_INDEX1 = 5;

    private static final int BOOLEAN_INDEX1 = 6;

    private static final int BOOLEAN_INDEX2 = 7;

    private static final int INT64_INDEX2 = 8;

    private static final int ARRAY_INDEX1 = 9;

    private static final int ARRAY_INDEX2 = 10;

    private static final int MAP_INDEX1 = 11;

    private static final int MAP_INDEX2 = 12;

    private static final List<Object> ONE_ROW = ImmutableList.of(0L, "S1", "S2", 3.1, 4.2, 5, true, false, 8L, ImmutableList.of(1, 2), ImmutableList.of(2, 4), ImmutableMap.of("key1", "value1", "address", "{\"city\":\"adelaide\",\"country\":\"oz\"}"), ImmutableMap.of("k1", 4), ImmutableList.of("one", "two"), ImmutableList.of(ImmutableList.of("1", "2"), ImmutableList.of("3")));

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    private MutableMetaStore metaStore;

    private CodeGenRunner codeGenRunner;

    private final InternalFunctionRegistry functionRegistry = new InternalFunctionRegistry();

    private final KsqlConfig ksqlConfig = new KsqlConfig(Collections.emptyMap());

    @Test
    public void testNullEquals() {
        MatcherAssert.assertThat(evalBooleanExprEq(CodeGenRunnerTest.INT32_INDEX1, CodeGenRunnerTest.INT64_INDEX1, new Object[]{ null, 12344L }), Matchers.is(false));
        MatcherAssert.assertThat(evalBooleanExprEq(CodeGenRunnerTest.INT32_INDEX1, CodeGenRunnerTest.INT64_INDEX1, new Object[]{ null, null }), Matchers.is(false));
    }

    @Test
    public void testIsDistinctFrom() {
        MatcherAssert.assertThat(evalBooleanExprIsDistinctFrom(CodeGenRunnerTest.INT32_INDEX1, CodeGenRunnerTest.INT64_INDEX1, new Object[]{ 12344, 12344L }), Matchers.is(false));
        MatcherAssert.assertThat(evalBooleanExprIsDistinctFrom(CodeGenRunnerTest.INT32_INDEX1, CodeGenRunnerTest.INT64_INDEX1, new Object[]{ 12345, 12344L }), Matchers.is(true));
        MatcherAssert.assertThat(evalBooleanExprIsDistinctFrom(CodeGenRunnerTest.INT32_INDEX1, CodeGenRunnerTest.INT64_INDEX1, new Object[]{ null, 12344L }), Matchers.is(true));
        MatcherAssert.assertThat(evalBooleanExprIsDistinctFrom(CodeGenRunnerTest.INT32_INDEX1, CodeGenRunnerTest.INT64_INDEX1, new Object[]{ null, null }), Matchers.is(false));
    }

    @Test
    public void testIsNull() {
        final String simpleQuery = "SELECT col0 IS NULL FROM CODEGEN_TEST;";
        final Analysis analysis = AnalysisTestUtil.analyzeQuery(simpleQuery, metaStore);
        final ExpressionMetadata expressionEvaluatorMetadata0 = codeGenRunner.buildCodeGenFromParseTree(analysis.getSelectExpressions().get(0), "Select");
        MatcherAssert.assertThat(expressionEvaluatorMetadata0.getIndexes(), Matchers.contains(0));
        MatcherAssert.assertThat(expressionEvaluatorMetadata0.getUdfs(), Matchers.hasSize(1));
        Object result0 = expressionEvaluatorMetadata0.evaluate(CodeGenRunnerTest.genericRow(null, 1));
        MatcherAssert.assertThat(result0, Matchers.is(true));
        result0 = expressionEvaluatorMetadata0.evaluate(CodeGenRunnerTest.genericRow(12345L));
        MatcherAssert.assertThat(result0, Matchers.is(false));
    }

    @Test
    public void shouldHandleMultiDimensionalArray() {
        // Given:
        final String simpleQuery = "SELECT col14[0][0] FROM CODEGEN_TEST;";
        final Analysis analysis = AnalysisTestUtil.analyzeQuery(simpleQuery, metaStore);
        // When:
        final Object result = codeGenRunner.buildCodeGenFromParseTree(analysis.getSelectExpressions().get(0), "Select").evaluate(CodeGenRunnerTest.genericRow(CodeGenRunnerTest.ONE_ROW));
        // Then:
        MatcherAssert.assertThat(result, Matchers.is("1"));
    }

    @Test
    public void testIsNotNull() {
        final String simpleQuery = "SELECT col0 IS NOT NULL FROM CODEGEN_TEST;";
        final Analysis analysis = AnalysisTestUtil.analyzeQuery(simpleQuery, metaStore);
        final ExpressionMetadata expressionEvaluatorMetadata0 = codeGenRunner.buildCodeGenFromParseTree(analysis.getSelectExpressions().get(0), "Filter");
        MatcherAssert.assertThat(expressionEvaluatorMetadata0.getIndexes(), Matchers.contains(0));
        MatcherAssert.assertThat(expressionEvaluatorMetadata0.getUdfs(), Matchers.hasSize(1));
        Object result0 = expressionEvaluatorMetadata0.evaluate(CodeGenRunnerTest.genericRow(null, "1"));
        MatcherAssert.assertThat(result0, Matchers.is(false));
        result0 = expressionEvaluatorMetadata0.evaluate(CodeGenRunnerTest.genericRow(12345L));
        MatcherAssert.assertThat(result0, Matchers.is(true));
    }

    @Test
    public void testBooleanExprScalarEq() {
        // int32
        MatcherAssert.assertThat(evalBooleanExprEq(CodeGenRunnerTest.INT32_INDEX1, CodeGenRunnerTest.INT64_INDEX1, new Object[]{ 12345, 12344L }), Matchers.is(false));
        MatcherAssert.assertThat(evalBooleanExprEq(CodeGenRunnerTest.INT32_INDEX1, CodeGenRunnerTest.INT64_INDEX1, new Object[]{ 12345, 12345L }), Matchers.is(true));
        // int64
        MatcherAssert.assertThat(evalBooleanExprEq(CodeGenRunnerTest.INT64_INDEX2, CodeGenRunnerTest.INT32_INDEX1, new Object[]{ 12345L, 12344 }), Matchers.is(false));
        MatcherAssert.assertThat(evalBooleanExprEq(CodeGenRunnerTest.INT64_INDEX2, CodeGenRunnerTest.INT32_INDEX1, new Object[]{ 12345L, 12345 }), Matchers.is(true));
        // double
        MatcherAssert.assertThat(evalBooleanExprEq(CodeGenRunnerTest.FLOAT64_INDEX2, CodeGenRunnerTest.FLOAT64_INDEX1, new Object[]{ 12345.0, 12344.0 }), Matchers.is(false));
        MatcherAssert.assertThat(evalBooleanExprEq(CodeGenRunnerTest.FLOAT64_INDEX2, CodeGenRunnerTest.FLOAT64_INDEX1, new Object[]{ 12345.0, 12345.0 }), Matchers.is(true));
    }

    @Test
    public void testBooleanExprBooleanEq() {
        MatcherAssert.assertThat(evalBooleanExprEq(CodeGenRunnerTest.BOOLEAN_INDEX2, CodeGenRunnerTest.BOOLEAN_INDEX1, new Object[]{ false, true }), Matchers.is(false));
        MatcherAssert.assertThat(evalBooleanExprEq(CodeGenRunnerTest.BOOLEAN_INDEX2, CodeGenRunnerTest.BOOLEAN_INDEX1, new Object[]{ true, true }), Matchers.is(true));
    }

    @Test
    public void testBooleanExprStringEq() {
        MatcherAssert.assertThat(evalBooleanExprEq(CodeGenRunnerTest.STRING_INDEX1, CodeGenRunnerTest.STRING_INDEX2, new Object[]{ "abc", "def" }), Matchers.is(false));
        MatcherAssert.assertThat(evalBooleanExprEq(CodeGenRunnerTest.STRING_INDEX1, CodeGenRunnerTest.STRING_INDEX2, new Object[]{ "abc", "abc" }), Matchers.is(true));
    }

    @Test
    public void testBooleanExprArrayComparisonFails() {
        // Given:
        expectedException.expect(KsqlException.class);
        expectedException.expectMessage(("Code generation failed for Filter: " + ("Cannot compare ARRAY values. " + "expression:(CODEGEN_TEST.COL9 = CODEGEN_TEST.COL10)")));
        expectedException.expectCause(ThrowableMessageMatcher.hasMessage(Matchers.equalTo("Cannot compare ARRAY values")));
        // When:
        evalBooleanExprEq(CodeGenRunnerTest.ARRAY_INDEX1, CodeGenRunnerTest.ARRAY_INDEX2, new Object[]{ new Integer[]{ 1 }, new Integer[]{ 1 } });
    }

    @Test
    public void testBooleanExprMapComparisonFails() {
        // Given:
        expectedException.expect(KsqlException.class);
        expectedException.expectMessage(("Code generation failed for Filter: " + ("Cannot compare MAP values. " + "expression:(CODEGEN_TEST.COL11 = CODEGEN_TEST.COL12)")));
        expectedException.expectCause(ThrowableMessageMatcher.hasMessage(Matchers.equalTo("Cannot compare MAP values")));
        // When:
        evalBooleanExprEq(CodeGenRunnerTest.MAP_INDEX1, CodeGenRunnerTest.MAP_INDEX2, new Object[]{ ImmutableMap.of(1, 2), ImmutableMap.of(1, 2) });
    }

    @Test
    public void testBooleanExprScalarNeq() {
        // int32
        MatcherAssert.assertThat(evalBooleanExprNeq(CodeGenRunnerTest.INT32_INDEX1, CodeGenRunnerTest.INT64_INDEX1, new Object[]{ 12345, 12344L }), Matchers.is(true));
        MatcherAssert.assertThat(evalBooleanExprNeq(CodeGenRunnerTest.INT32_INDEX1, CodeGenRunnerTest.INT64_INDEX1, new Object[]{ 12345, 12345L }), Matchers.is(false));
        // int64
        MatcherAssert.assertThat(evalBooleanExprNeq(CodeGenRunnerTest.INT64_INDEX2, CodeGenRunnerTest.INT32_INDEX1, new Object[]{ 12345L, 12344 }), Matchers.is(true));
        MatcherAssert.assertThat(evalBooleanExprNeq(CodeGenRunnerTest.INT64_INDEX2, CodeGenRunnerTest.INT32_INDEX1, new Object[]{ 12345L, 12345 }), Matchers.is(false));
        // double
        MatcherAssert.assertThat(evalBooleanExprNeq(CodeGenRunnerTest.FLOAT64_INDEX2, CodeGenRunnerTest.FLOAT64_INDEX1, new Object[]{ 12345.0, 12344.0 }), Matchers.is(true));
        MatcherAssert.assertThat(evalBooleanExprNeq(CodeGenRunnerTest.FLOAT64_INDEX2, CodeGenRunnerTest.FLOAT64_INDEX1, new Object[]{ 12345.0, 12345.0 }), Matchers.is(false));
    }

    @Test
    public void testBooleanExprBooleanNeq() {
        MatcherAssert.assertThat(evalBooleanExprNeq(CodeGenRunnerTest.BOOLEAN_INDEX2, CodeGenRunnerTest.BOOLEAN_INDEX1, new Object[]{ false, true }), Matchers.is(true));
        MatcherAssert.assertThat(evalBooleanExprNeq(CodeGenRunnerTest.BOOLEAN_INDEX2, CodeGenRunnerTest.BOOLEAN_INDEX1, new Object[]{ true, true }), Matchers.is(false));
    }

    @Test
    public void testBooleanExprStringNeq() {
        MatcherAssert.assertThat(evalBooleanExprNeq(CodeGenRunnerTest.STRING_INDEX1, CodeGenRunnerTest.STRING_INDEX2, new Object[]{ "abc", "def" }), Matchers.is(true));
        MatcherAssert.assertThat(evalBooleanExprNeq(CodeGenRunnerTest.STRING_INDEX1, CodeGenRunnerTest.STRING_INDEX2, new Object[]{ "abc", "abc" }), Matchers.is(false));
    }

    @Test
    public void testBooleanExprScalarLessThan() {
        // int32
        MatcherAssert.assertThat(evalBooleanExprLessThan(CodeGenRunnerTest.INT32_INDEX1, CodeGenRunnerTest.INT64_INDEX1, new Object[]{ 12344, 12345L }), Matchers.is(true));
        MatcherAssert.assertThat(evalBooleanExprLessThan(CodeGenRunnerTest.INT32_INDEX1, CodeGenRunnerTest.INT64_INDEX1, new Object[]{ 12346, 12345L }), Matchers.is(false));
        // int64
        MatcherAssert.assertThat(evalBooleanExprLessThan(CodeGenRunnerTest.INT64_INDEX2, CodeGenRunnerTest.INT32_INDEX1, new Object[]{ 12344L, 12345 }), Matchers.is(true));
        MatcherAssert.assertThat(evalBooleanExprLessThan(CodeGenRunnerTest.INT64_INDEX2, CodeGenRunnerTest.INT32_INDEX1, new Object[]{ 12346L, 12345 }), Matchers.is(false));
        // double
        MatcherAssert.assertThat(evalBooleanExprLessThan(CodeGenRunnerTest.FLOAT64_INDEX2, CodeGenRunnerTest.FLOAT64_INDEX1, new Object[]{ 12344.0, 12345.0 }), Matchers.is(true));
        MatcherAssert.assertThat(evalBooleanExprLessThan(CodeGenRunnerTest.FLOAT64_INDEX2, CodeGenRunnerTest.FLOAT64_INDEX1, new Object[]{ 12346.0, 12345.0 }), Matchers.is(false));
    }

    @Test
    public void testBooleanExprStringLessThan() {
        MatcherAssert.assertThat(evalBooleanExprLessThan(CodeGenRunnerTest.STRING_INDEX1, CodeGenRunnerTest.STRING_INDEX2, new Object[]{ "abc", "def" }), Matchers.is(true));
        MatcherAssert.assertThat(evalBooleanExprLessThan(CodeGenRunnerTest.STRING_INDEX1, CodeGenRunnerTest.STRING_INDEX2, new Object[]{ "abc", "abc" }), Matchers.is(false));
    }

    @Test
    public void testBooleanExprScalarLessThanEq() {
        // int32
        MatcherAssert.assertThat(evalBooleanExprLessThanEq(CodeGenRunnerTest.INT32_INDEX1, CodeGenRunnerTest.INT64_INDEX1, new Object[]{ 12345, 12345L }), Matchers.is(true));
        MatcherAssert.assertThat(evalBooleanExprLessThanEq(CodeGenRunnerTest.INT32_INDEX1, CodeGenRunnerTest.INT64_INDEX1, new Object[]{ 12346, 12345L }), Matchers.is(false));
        // int64
        MatcherAssert.assertThat(evalBooleanExprLessThanEq(CodeGenRunnerTest.INT64_INDEX2, CodeGenRunnerTest.INT32_INDEX1, new Object[]{ 12345L, 12345 }), Matchers.is(true));
        MatcherAssert.assertThat(evalBooleanExprLessThanEq(CodeGenRunnerTest.INT64_INDEX2, CodeGenRunnerTest.INT32_INDEX1, new Object[]{ 12346L, 12345 }), Matchers.is(false));
        // double
        MatcherAssert.assertThat(evalBooleanExprLessThanEq(CodeGenRunnerTest.FLOAT64_INDEX2, CodeGenRunnerTest.FLOAT64_INDEX1, new Object[]{ 12344.0, 12345.0 }), Matchers.is(true));
        MatcherAssert.assertThat(evalBooleanExprLessThanEq(CodeGenRunnerTest.FLOAT64_INDEX2, CodeGenRunnerTest.FLOAT64_INDEX1, new Object[]{ 12346.0, 12345.0 }), Matchers.is(false));
    }

    @Test
    public void testBooleanExprStringLessThanEq() {
        MatcherAssert.assertThat(evalBooleanExprLessThanEq(CodeGenRunnerTest.STRING_INDEX1, CodeGenRunnerTest.STRING_INDEX2, new Object[]{ "abc", "abc" }), Matchers.is(true));
        MatcherAssert.assertThat(evalBooleanExprLessThanEq(CodeGenRunnerTest.STRING_INDEX1, CodeGenRunnerTest.STRING_INDEX2, new Object[]{ "abc", "abb" }), Matchers.is(false));
    }

    @Test
    public void testBooleanExprScalarGreaterThan() {
        // int32
        MatcherAssert.assertThat(evalBooleanExprGreaterThan(CodeGenRunnerTest.INT32_INDEX1, CodeGenRunnerTest.INT64_INDEX1, new Object[]{ 12346, 12345L }), Matchers.is(true));
        MatcherAssert.assertThat(evalBooleanExprGreaterThan(CodeGenRunnerTest.INT32_INDEX1, CodeGenRunnerTest.INT64_INDEX1, new Object[]{ 12345, 12345L }), Matchers.is(false));
        // int64
        MatcherAssert.assertThat(evalBooleanExprGreaterThan(CodeGenRunnerTest.INT64_INDEX2, CodeGenRunnerTest.INT32_INDEX1, new Object[]{ 12346L, 12345 }), Matchers.is(true));
        MatcherAssert.assertThat(evalBooleanExprGreaterThan(CodeGenRunnerTest.INT64_INDEX2, CodeGenRunnerTest.INT32_INDEX1, new Object[]{ 12345L, 12345 }), Matchers.is(false));
        // double
        MatcherAssert.assertThat(evalBooleanExprGreaterThan(CodeGenRunnerTest.FLOAT64_INDEX2, CodeGenRunnerTest.FLOAT64_INDEX1, new Object[]{ 12346.0, 12345.0 }), Matchers.is(true));
        MatcherAssert.assertThat(evalBooleanExprGreaterThan(CodeGenRunnerTest.FLOAT64_INDEX2, CodeGenRunnerTest.FLOAT64_INDEX1, new Object[]{ 12344.0, 12345.0 }), Matchers.is(false));
    }

    @Test
    public void testBooleanExprStringGreaterThan() {
        MatcherAssert.assertThat(evalBooleanExprGreaterThan(CodeGenRunnerTest.STRING_INDEX1, CodeGenRunnerTest.STRING_INDEX2, new Object[]{ "def", "abc" }), Matchers.is(true));
        MatcherAssert.assertThat(evalBooleanExprGreaterThan(CodeGenRunnerTest.STRING_INDEX1, CodeGenRunnerTest.STRING_INDEX2, new Object[]{ "abc", "abc" }), Matchers.is(false));
    }

    @Test
    public void testBooleanExprScalarGreaterThanEq() {
        // int32
        MatcherAssert.assertThat(evalBooleanExprGreaterThanEq(CodeGenRunnerTest.INT32_INDEX1, CodeGenRunnerTest.INT64_INDEX1, new Object[]{ 12345, 12345L }), Matchers.is(true));
        MatcherAssert.assertThat(evalBooleanExprGreaterThanEq(CodeGenRunnerTest.INT32_INDEX1, CodeGenRunnerTest.INT64_INDEX1, new Object[]{ 12344, 12345L }), Matchers.is(false));
        // int64
        MatcherAssert.assertThat(evalBooleanExprGreaterThanEq(CodeGenRunnerTest.INT64_INDEX2, CodeGenRunnerTest.INT32_INDEX1, new Object[]{ 12345L, 12345 }), Matchers.is(true));
        MatcherAssert.assertThat(evalBooleanExprGreaterThanEq(CodeGenRunnerTest.INT64_INDEX2, CodeGenRunnerTest.INT32_INDEX1, new Object[]{ 12344L, 12345 }), Matchers.is(false));
        // double
        MatcherAssert.assertThat(evalBooleanExprGreaterThanEq(CodeGenRunnerTest.FLOAT64_INDEX2, CodeGenRunnerTest.FLOAT64_INDEX1, new Object[]{ 12346.0, 12345.0 }), Matchers.is(true));
        MatcherAssert.assertThat(evalBooleanExprGreaterThanEq(CodeGenRunnerTest.FLOAT64_INDEX2, CodeGenRunnerTest.FLOAT64_INDEX1, new Object[]{ 12344.0, 12345.0 }), Matchers.is(false));
    }

    @Test
    public void testBooleanExprStringGreaterThanEq() {
        MatcherAssert.assertThat(evalBooleanExprGreaterThanEq(CodeGenRunnerTest.STRING_INDEX1, CodeGenRunnerTest.STRING_INDEX2, new Object[]{ "def", "abc" }), Matchers.is(true));
        MatcherAssert.assertThat(evalBooleanExprGreaterThanEq(CodeGenRunnerTest.STRING_INDEX1, CodeGenRunnerTest.STRING_INDEX2, new Object[]{ "abc", "def" }), Matchers.is(false));
    }

    @Test
    public void testBetweenExprScalar() {
        // int
        MatcherAssert.assertThat(evalBetweenClauseScalar(CodeGenRunnerTest.INT32_INDEX1, 1, 0, 2), Matchers.is(true));
        MatcherAssert.assertThat(evalBetweenClauseScalar(CodeGenRunnerTest.INT32_INDEX1, 0, 0, 2), Matchers.is(true));
        MatcherAssert.assertThat(evalBetweenClauseScalar(CodeGenRunnerTest.INT32_INDEX1, 3, 0, 2), Matchers.is(false));
        MatcherAssert.assertThat(evalBetweenClauseScalar(CodeGenRunnerTest.INT32_INDEX1, null, 0, 2), Matchers.is(false));
        // long
        MatcherAssert.assertThat(evalBetweenClauseScalar(CodeGenRunnerTest.INT64_INDEX1, 12345L, 12344L, 12346L), Matchers.is(true));
        MatcherAssert.assertThat(evalBetweenClauseScalar(CodeGenRunnerTest.INT64_INDEX1, 12344L, 12344L, 12346L), Matchers.is(true));
        MatcherAssert.assertThat(evalBetweenClauseScalar(CodeGenRunnerTest.INT64_INDEX1, 12345L, 0, 2L), Matchers.is(false));
        MatcherAssert.assertThat(evalBetweenClauseScalar(CodeGenRunnerTest.INT64_INDEX1, null, 0, 2L), Matchers.is(false));
        // double
        MatcherAssert.assertThat(evalBetweenClauseScalar(CodeGenRunnerTest.FLOAT64_INDEX1, 1.0, 0.1, 1.9), Matchers.is(true));
        MatcherAssert.assertThat(evalBetweenClauseScalar(CodeGenRunnerTest.FLOAT64_INDEX1, 0.1, 0.1, 1.9), Matchers.is(true));
        MatcherAssert.assertThat(evalBetweenClauseScalar(CodeGenRunnerTest.FLOAT64_INDEX1, 2.0, 0.1, 1.9), Matchers.is(false));
        MatcherAssert.assertThat(evalBetweenClauseScalar(CodeGenRunnerTest.FLOAT64_INDEX1, null, 0.1, 1.9), Matchers.is(false));
    }

    @Test
    public void testNotBetweenScalar() {
        // int
        MatcherAssert.assertThat(evalNotBetweenClauseScalar(CodeGenRunnerTest.INT32_INDEX1, 1, 0, 2), Matchers.is(false));
        MatcherAssert.assertThat(evalNotBetweenClauseScalar(CodeGenRunnerTest.INT32_INDEX1, 0, 0, 2), Matchers.is(false));
        MatcherAssert.assertThat(evalNotBetweenClauseScalar(CodeGenRunnerTest.INT32_INDEX1, 3, 0, 2), Matchers.is(true));
        MatcherAssert.assertThat(evalNotBetweenClauseScalar(CodeGenRunnerTest.INT32_INDEX1, null, 0, 2), Matchers.is(true));
        // long
        MatcherAssert.assertThat(evalNotBetweenClauseScalar(CodeGenRunnerTest.INT64_INDEX1, 12345L, 12344L, 12346L), Matchers.is(false));
        MatcherAssert.assertThat(evalNotBetweenClauseScalar(CodeGenRunnerTest.INT64_INDEX1, 12344L, 12344L, 12346L), Matchers.is(false));
        MatcherAssert.assertThat(evalNotBetweenClauseScalar(CodeGenRunnerTest.INT64_INDEX1, 12345L, 0, 2L), Matchers.is(true));
        MatcherAssert.assertThat(evalNotBetweenClauseScalar(CodeGenRunnerTest.INT64_INDEX1, null, 0, 2L), Matchers.is(true));
        // double
        MatcherAssert.assertThat(evalNotBetweenClauseScalar(CodeGenRunnerTest.FLOAT64_INDEX1, 1.0, 0.1, 1.9), Matchers.is(false));
        MatcherAssert.assertThat(evalNotBetweenClauseScalar(CodeGenRunnerTest.FLOAT64_INDEX1, 0.1, 0.1, 1.9), Matchers.is(false));
        MatcherAssert.assertThat(evalNotBetweenClauseScalar(CodeGenRunnerTest.FLOAT64_INDEX1, 2.0, 0.1, 1.9), Matchers.is(true));
        MatcherAssert.assertThat(evalNotBetweenClauseScalar(CodeGenRunnerTest.FLOAT64_INDEX1, null, 0.1, 1.9), Matchers.is(true));
    }

    @Test
    public void testBetweenExprString() {
        // constants
        MatcherAssert.assertThat(evalBetweenClauseString(CodeGenRunnerTest.STRING_INDEX1, "b", "'a'", "'c'"), Matchers.is(true));
        MatcherAssert.assertThat(evalBetweenClauseString(CodeGenRunnerTest.STRING_INDEX1, "a", "'a'", "'c'"), Matchers.is(true));
        MatcherAssert.assertThat(evalBetweenClauseString(CodeGenRunnerTest.STRING_INDEX1, "d", "'a'", "'c'"), Matchers.is(false));
        MatcherAssert.assertThat(evalBetweenClauseString(CodeGenRunnerTest.STRING_INDEX1, null, "'a'", "'c'"), Matchers.is(false));
        // columns
        MatcherAssert.assertThat(evalBetweenClauseString(CodeGenRunnerTest.STRING_INDEX1, "S2", ("col" + (CodeGenRunnerTest.STRING_INDEX2)), "'S3'"), Matchers.is(true));
        MatcherAssert.assertThat(evalBetweenClauseString(CodeGenRunnerTest.STRING_INDEX1, "S3", ("col" + (CodeGenRunnerTest.STRING_INDEX2)), "'S3'"), Matchers.is(true));
        MatcherAssert.assertThat(evalBetweenClauseString(CodeGenRunnerTest.STRING_INDEX1, "S4", ("col" + (CodeGenRunnerTest.STRING_INDEX2)), "'S3'"), Matchers.is(false));
        MatcherAssert.assertThat(evalBetweenClauseString(CodeGenRunnerTest.STRING_INDEX1, null, ("col" + (CodeGenRunnerTest.STRING_INDEX2)), "'S3'"), Matchers.is(false));
    }

    @Test
    public void testNotBetweenExprString() {
        // constants
        MatcherAssert.assertThat(evalNotBetweenClauseString(CodeGenRunnerTest.STRING_INDEX1, "b", "'a'", "'c'"), Matchers.is(false));
        MatcherAssert.assertThat(evalNotBetweenClauseString(CodeGenRunnerTest.STRING_INDEX1, "a", "'a'", "'c'"), Matchers.is(false));
        MatcherAssert.assertThat(evalNotBetweenClauseString(CodeGenRunnerTest.STRING_INDEX1, "d", "'a'", "'c'"), Matchers.is(true));
        MatcherAssert.assertThat(evalNotBetweenClauseString(CodeGenRunnerTest.STRING_INDEX1, null, "'a'", "'c'"), Matchers.is(true));
        // columns
        MatcherAssert.assertThat(evalNotBetweenClauseString(CodeGenRunnerTest.STRING_INDEX1, "S2", ("col" + (CodeGenRunnerTest.STRING_INDEX2)), "'S3'"), Matchers.is(false));
        MatcherAssert.assertThat(evalNotBetweenClauseString(CodeGenRunnerTest.STRING_INDEX1, "S3", ("col" + (CodeGenRunnerTest.STRING_INDEX2)), "'S3'"), Matchers.is(false));
        MatcherAssert.assertThat(evalNotBetweenClauseString(CodeGenRunnerTest.STRING_INDEX1, "S4", ("col" + (CodeGenRunnerTest.STRING_INDEX2)), "'S3'"), Matchers.is(true));
        MatcherAssert.assertThat(evalNotBetweenClauseString(CodeGenRunnerTest.STRING_INDEX1, null, ("col" + (CodeGenRunnerTest.STRING_INDEX2)), "'S3'"), Matchers.is(true));
    }

    @Test
    public void testInvalidBetweenArrayValue() {
        // Given:
        expectedException.expect(KsqlException.class);
        expectedException.expectMessage(("Code generation failed for Filter: " + ("Cannot execute BETWEEN with ARRAY values. " + "expression:(NOT (CODEGEN_TEST.COL9 BETWEEN 'a' AND 'c'))")));
        expectedException.expectCause(ThrowableMessageMatcher.hasMessage(Matchers.equalTo("Cannot execute BETWEEN with ARRAY values")));
        // When:
        evalNotBetweenClauseObject(CodeGenRunnerTest.ARRAY_INDEX1, new Object[]{ 1, 2 }, "'a'", "'c'");
    }

    @Test
    public void testInvalidBetweenMapValue() {
        // Given:
        expectedException.expect(KsqlException.class);
        expectedException.expectMessage(("Code generation failed for Filter: " + ("Cannot execute BETWEEN with MAP values. " + "expression:(NOT (CODEGEN_TEST.COL11 BETWEEN 'a' AND 'c'))")));
        expectedException.expectCause(ThrowableMessageMatcher.hasMessage(Matchers.equalTo("Cannot execute BETWEEN with MAP values")));
        // When:
        evalNotBetweenClauseObject(CodeGenRunnerTest.MAP_INDEX1, ImmutableMap.of(1, 2), "'a'", "'c'");
    }

    @Test
    public void testInvalidBetweenBooleanValue() {
        // Given:
        expectedException.expect(KsqlException.class);
        expectedException.expectMessage(("Code generation failed for Filter: " + ("Cannot execute BETWEEN with BOOLEAN values. " + "expression:(NOT (CODEGEN_TEST.COL6 BETWEEN 'a' AND 'c'))")));
        expectedException.expectCause(ThrowableMessageMatcher.hasMessage(Matchers.equalTo("Cannot execute BETWEEN with BOOLEAN values")));
        // When:
        evalNotBetweenClauseObject(CodeGenRunnerTest.BOOLEAN_INDEX1, true, "'a'", "'c'");
    }

    @Test
    public void shouldHandleArithmeticExpr() {
        // Given:
        final String query = "SELECT col0+col3, col3+10, col0*25, 12*4+2 FROM codegen_test WHERE col0 > 100;";
        final Map<Integer, Object> inputValues = ImmutableMap.of(0, 5L, 3, 15.0);
        // When:
        final List<Object> columns = executeExpression(query, inputValues);
        // Then:
        MatcherAssert.assertThat(columns, Matchers.contains(20.0, 25.0, 125L, 50));
    }

    @Test
    public void testCastNumericArithmeticExpressions() {
        final Map<Integer, Object> inputValues = ImmutableMap.of(0, 1, 3, 3, 4, 4, 5, 5);
        // INT64 - INT32
        MatcherAssert.assertThat(executeExpression(("SELECT " + (((("CAST((col5 - col0) AS INTEGER)," + "CAST((col5 - col0) AS BIGINT),") + "CAST((col5 - col0) AS DOUBLE),") + "CAST((col5 - col0) AS STRING)") + "FROM codegen_test;")), inputValues), Matchers.contains(4, 4L, 4.0, "4"));
        // FLOAT64 - FLOAT64
        MatcherAssert.assertThat(executeExpression(("SELECT " + (((("CAST((col4 - col3) AS INTEGER)," + "CAST((col4 - col3) AS BIGINT),") + "CAST((col4 - col3) AS DOUBLE),") + "CAST((col4 - col3) AS STRING)") + "FROM codegen_test;")), inputValues), Matchers.contains(1, 1L, 1.0, "1.0"));
        // FLOAT64 - INT64
        MatcherAssert.assertThat(executeExpression(("SELECT " + (((("CAST((col4 - col0) AS INTEGER)," + "CAST((col4 - col0) AS BIGINT),") + "CAST((col4 - col0) AS DOUBLE),") + "CAST((col4 - col0) AS STRING)") + "FROM codegen_test;")), inputValues), Matchers.contains(3, 3L, 3.0, "3.0"));
    }

    @Test
    public void shouldHandleMathUdfs() {
        // Given:
        final String query = "SELECT FLOOR(col3), CEIL(col3*3), ABS(col0+1.34), ROUND(col3*2)+12 FROM codegen_test;";
        final Map<Integer, Object> inputValues = ImmutableMap.of(0, 15, 3, 1.5);
        // When:
        final List<Object> columns = executeExpression(query, inputValues);
        // Then:
        MatcherAssert.assertThat(columns, Matchers.contains(1.0, 5.0, 16.34, 15L));
    }

    @Test
    public void shouldHandleRandomUdf() {
        // Given:
        final String query = "SELECT RANDOM()+10, RANDOM()+col0 FROM codegen_test;";
        final Map<Integer, Object> inputValues = ImmutableMap.of(0, 15);
        // When:
        final List<Object> columns = executeExpression(query, inputValues);
        // Then:
        MatcherAssert.assertThat(columns.get(0), Matchers.is(Matchers.instanceOf(Double.class)));
        MatcherAssert.assertThat(((Double) (columns.get(0))), Matchers.is(Matchers.both(Matchers.greaterThanOrEqualTo(10.0)).and(Matchers.lessThanOrEqualTo(11.0))));
        MatcherAssert.assertThat(columns.get(1), Matchers.is(Matchers.instanceOf(Double.class)));
        MatcherAssert.assertThat(((Double) (columns.get(1))), Matchers.is(Matchers.both(Matchers.greaterThanOrEqualTo(15.0)).and(Matchers.lessThanOrEqualTo(16.0))));
    }

    @Test
    public void shouldHandleStringUdfs() {
        // Given:
        final String query = "SELECT LCASE(col1), UCASE(col1), TRIM(col1), CONCAT(col1,'_test'), SUBSTRING(col1, 2, 4)" + " FROM codegen_test;";
        final Map<Integer, Object> inputValues = ImmutableMap.of(1, " Hello ");
        // When:
        final List<Object> columns = executeExpression(query, inputValues);
        // Then:
        MatcherAssert.assertThat(columns, Matchers.contains(" hello ", " HELLO ", "Hello", " Hello _test", "Hell"));
    }

    @Test
    public void shouldHandleNestedUdfs() {
        final String query = "SELECT " + ("CONCAT(EXTRACTJSONFIELD(col1,'$.name'),CONCAT('-',EXTRACTJSONFIELD(col1,'$.value')))" + " FROM codegen_test;");
        final Map<Integer, Object> inputValues = ImmutableMap.of(1, "{\"name\":\"fred\",\"value\":1}");
        // When:
        executeExpression(query, inputValues);
    }

    @Test
    public void shouldHandleMaps() {
        // Given:
        final Expression expression = AnalysisTestUtil.analyzeQuery("SELECT col11['key1'] as Address FROM codegen_test;", metaStore).getSelectExpressions().get(0);
        // When:
        final Object result = codeGenRunner.buildCodeGenFromParseTree(expression, "Group By").evaluate(CodeGenRunnerTest.genericRow(CodeGenRunnerTest.ONE_ROW));
        // Then:
        MatcherAssert.assertThat(result, Matchers.is("value1"));
    }

    @Test
    public void shouldHandleCaseStatement() {
        // Given:
        final Expression expression = AnalysisTestUtil.analyzeQuery(("SELECT CASE " + (((("     WHEN col0 < 10 THEN 'small' " + "     WHEN col0 < 100 THEN 'medium' ") + "     ELSE 'large' ") + "END ") + "FROM codegen_test;")), metaStore).getSelectExpressions().get(0);
        // When:
        final Object result = codeGenRunner.buildCodeGenFromParseTree(expression, "Case").evaluate(CodeGenRunnerTest.genericRow(CodeGenRunnerTest.ONE_ROW));
        // Then:
        MatcherAssert.assertThat(result, Matchers.is("small"));
    }

    @Test
    public void shouldHandleCaseStatementLazily() {
        // Given:
        final Expression expression = AnalysisTestUtil.analyzeQuery(("SELECT CASE " + (((("     WHEN WHENCONDITION(true, true) THEN WHENRESULT(100, true) " + "     WHEN WHENCONDITION(true, false) THEN WHENRESULT(200, false) ") + "     ELSE WHENRESULT(300, false) ") + "END ") + "FROM codegen_test;")), metaStore).getSelectExpressions().get(0);
        // When:
        final Object result = codeGenRunner.buildCodeGenFromParseTree(expression, "Case").evaluate(CodeGenRunnerTest.genericRow(CodeGenRunnerTest.ONE_ROW));
        // Then:
        MatcherAssert.assertThat(result, Matchers.is(100));
    }

    @Test
    public void shouldOnlyRunElseIfNoMatchInWhen() {
        // Given:
        final Expression expression = AnalysisTestUtil.analyzeQuery(("SELECT CASE " + (((("     WHEN WHENCONDITION(false, true) THEN WHENRESULT(100, false) " + "     WHEN WHENCONDITION(false, true) THEN WHENRESULT(200, false) ") + "     ELSE WHENRESULT(300, true) ") + "END ") + "FROM codegen_test;")), metaStore).getSelectExpressions().get(0);
        // When:
        final Object result = codeGenRunner.buildCodeGenFromParseTree(expression, "Case").evaluate(CodeGenRunnerTest.genericRow(CodeGenRunnerTest.ONE_ROW));
        // Then:
        MatcherAssert.assertThat(result, Matchers.is(300));
    }

    @Test
    public void shouldReturnDefaultForCaseCorrectly() {
        // Given:
        final Expression expression = AnalysisTestUtil.analyzeQuery(("SELECT CASE " + ((("     WHEN col0 > 10 THEN 'small' " + "     ELSE 'large' ") + "END ") + "FROM codegen_test;")), metaStore).getSelectExpressions().get(0);
        // When:
        final Object result = codeGenRunner.buildCodeGenFromParseTree(expression, "Case").evaluate(CodeGenRunnerTest.genericRow(CodeGenRunnerTest.ONE_ROW));
        // Then:
        MatcherAssert.assertThat(result, Matchers.is("large"));
    }

    @Test
    public void shouldReturnNullForCaseIfNoDefault() {
        // Given:
        final Expression expression = AnalysisTestUtil.analyzeQuery(("SELECT CASE " + (("     WHEN col0 > 10 THEN 'small' " + "END ") + "FROM codegen_test;")), metaStore).getSelectExpressions().get(0);
        // When:
        final Object result = codeGenRunner.buildCodeGenFromParseTree(expression, "Case").evaluate(CodeGenRunnerTest.genericRow(CodeGenRunnerTest.ONE_ROW));
        // Then:
        MatcherAssert.assertThat(result, Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void shouldHandleUdfsExtractingFromMaps() {
        // Given:
        final Expression expression = AnalysisTestUtil.analyzeQuery("SELECT EXTRACTJSONFIELD(col11['address'], '$.city') FROM codegen_test;", metaStore).getSelectExpressions().get(0);
        // When:
        final Object result = codeGenRunner.buildCodeGenFromParseTree(expression, "Select").evaluate(CodeGenRunnerTest.genericRow(CodeGenRunnerTest.ONE_ROW));
        // Then:
        MatcherAssert.assertThat(result, Matchers.is("adelaide"));
    }

    @Test
    public void shouldHandleFunctionWithNullArgument() {
        final String query = "SELECT test_udf(col0, NULL) FROM codegen_test;";
        final Map<Integer, Object> inputValues = ImmutableMap.of(0, 0);
        final List<Object> columns = executeExpression(query, inputValues);
        // test
        MatcherAssert.assertThat(columns, Matchers.equalTo(Collections.singletonList("doStuffLongString")));
    }

    @Test
    public void shouldChoseFunctionWithCorrectNumberOfArgsWhenNullArgument() {
        final String query = "SELECT test_udf(col0, col0, NULL) FROM codegen_test;";
        final Map<Integer, Object> inputValues = ImmutableMap.of(0, 0);
        final List<Object> columns = executeExpression(query, inputValues);
        // test
        MatcherAssert.assertThat(columns, Matchers.equalTo(Collections.singletonList("doStuffLongLongString")));
    }

    public static final class WhenCondition implements Kudf {
        @Override
        public Object evaluate(final Object... args) {
            final boolean shouldBeEvaluated = ((boolean) (args[1]));
            if (!shouldBeEvaluated) {
                throw new KsqlException("When condition in case is not running lazily!");
            }
            return args[0];
        }
    }

    public static final class WhenResult implements Kudf {
        @Override
        public Object evaluate(final Object... args) {
            final boolean shouldBeEvaluated = ((boolean) (args[1]));
            if (!shouldBeEvaluated) {
                throw new KsqlException("Then expression in case is not running lazily!");
            }
            return args[0];
        }
    }
}

