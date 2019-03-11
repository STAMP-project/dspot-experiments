/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.beam.sdk.extensions.sql;


import FieldType.INT32;
import Schema.FieldType;
import Schema.FieldType.STRING;
import SqlKind.OTHER_FUNCTION;
import SqlStdOperatorTable.ELEMENT_SLICE;
import SqlStdOperatorTable.EXCEPT;
import SqlStdOperatorTable.EXCEPT_ALL;
import SqlStdOperatorTable.INTERSECT;
import SqlStdOperatorTable.INTERSECT_ALL;
import SqlStdOperatorTable.LITERAL_CHAIN;
import SqlStdOperatorTable.PATTERN_CONCAT;
import SqlStdOperatorTable.UNION;
import SqlStdOperatorTable.UNION_ALL;
import com.google.auto.value.AutoValue;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.beam.sdk.extensions.sql.integrationtest.BeamSqlBuiltinFunctionsIntegrationTestBase;
import org.apache.beam.sdk.extensions.sql.utils.DateTimeUtils;
import org.apache.beam.vendor.guava.v20_0.com.google.common.base.Joiner;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableList;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.Lists;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.Ordering;
import org.apache.calcite.runtime.SqlFunctions;
import org.apache.calcite.sql.SqlKind;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 * DSL compliance tests for the row-level operators of {@link org.apache.calcite.sql.fun.SqlStdOperatorTable}.
 */
public class BeamSqlDslSqlStdOperatorsTest extends BeamSqlBuiltinFunctionsIntegrationTestBase {
    private static final BigDecimal ZERO = BigDecimal.valueOf(0.0);

    private static final BigDecimal ONE = BigDecimal.valueOf(1.0);

    private static final BigDecimal ONE2 = BigDecimal.valueOf(1.0).multiply(BigDecimal.valueOf(1.0));

    private static final BigDecimal ONE10 = BigDecimal.ONE.divide(BigDecimal.ONE, 10, RoundingMode.HALF_EVEN);

    private static final BigDecimal TWO = BigDecimal.valueOf(2.0);

    private static final BigDecimal TWO0 = BigDecimal.ONE.add(BigDecimal.ONE);

    private static final int INTEGER_VALUE = 1;

    private static final long LONG_VALUE = 1L;

    private static final short SHORT_VALUE = 1;

    private static final byte BYTE_VALUE = 1;

    private static final double DOUBLE_VALUE = 1.0;

    private static final float FLOAT_VALUE = 1.0F;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    /**
     * Calcite operators are identified by name and kind.
     */
    @AutoValue
    abstract static class SqlOperatorId {
        abstract String name();

        abstract SqlKind kind();
    }

    private static final List<BeamSqlDslSqlStdOperatorsTest.SqlOperatorId> NON_ROW_OPERATORS = // internal
    // internal
    // "," PATTERN_CONCAT
    ImmutableList.of(ELEMENT_SLICE, EXCEPT, EXCEPT_ALL, INTERSECT, INTERSECT_ALL, LITERAL_CHAIN, PATTERN_CONCAT, UNION, UNION_ALL).stream().map(( op) -> sqlOperatorId(op)).collect(Collectors.toList());

    /**
     * LEGACY ADAPTER - DO NOT USE DIRECTLY. Use {@code getAnnotationsByType(SqlOperatorTest.class)},
     * a more reliable method for retrieving repeated annotations.
     *
     * <p>This is a virtual annotation that is only present when there are more than one {@link SqlOperatorTest} annotations. When there is just one {@link SqlOperatorTest} annotation the
     * proxying is not in place.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.METHOD })
    private @interface SqlOperatorTests {
        BeamSqlDslSqlStdOperatorsTest.SqlOperatorTest[] value();
    }

    /**
     * Annotation that declares a test method has the tests for the {@link SqlOperatorId}.
     *
     * <p>It is almost identical to {@link SqlOperatorId} but complex types cannot be part of
     * annotations and there are minor benefits to having a non-annotation class for passing around
     * the ids.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.METHOD })
    @Repeatable(BeamSqlDslSqlStdOperatorsTest.SqlOperatorTests.class)
    private @interface SqlOperatorTest {
        String name();

        String kind();
    }

    private final Comparator<BeamSqlDslSqlStdOperatorsTest.SqlOperatorId> orderByNameThenKind = Ordering.compound(ImmutableList.of(Comparator.comparing((BeamSqlDslSqlStdOperatorsTest.SqlOperatorId operator) -> operator.name()), Comparator.comparing((BeamSqlDslSqlStdOperatorsTest.SqlOperatorId operator) -> operator.kind())));

    /**
     * Smoke test that the whitelists and utility functions actually work.
     */
    @Test
    @BeamSqlDslSqlStdOperatorsTest.SqlOperatorTest(name = "CARDINALITY", kind = "OTHER_FUNCTION")
    public void testAnnotationEquality() throws Exception {
        Method thisMethod = getClass().getMethod("testAnnotationEquality");
        BeamSqlDslSqlStdOperatorsTest.SqlOperatorTest sqlOperatorTest = thisMethod.getAnnotationsByType(BeamSqlDslSqlStdOperatorsTest.SqlOperatorTest.class)[0];
        Assert.assertThat(BeamSqlDslSqlStdOperatorsTest.sqlOperatorId(sqlOperatorTest), Matchers.equalTo(BeamSqlDslSqlStdOperatorsTest.sqlOperatorId("CARDINALITY", OTHER_FUNCTION)));
    }

    /**
     * Tests that we didn't typo an annotation, that all things we claim to test are real operators.
     */
    @Test
    public void testThatOperatorsExist() {
        Set<BeamSqlDslSqlStdOperatorsTest.SqlOperatorId> undeclaredOperators = new HashSet<>();
        undeclaredOperators.addAll(getTestedOperators());
        undeclaredOperators.removeAll(getDeclaredOperators());
        if (!(undeclaredOperators.isEmpty())) {
            // Sorting is just to make failures more readable
            List<BeamSqlDslSqlStdOperatorsTest.SqlOperatorId> undeclaredList = Lists.newArrayList(undeclaredOperators);
            undeclaredList.sort(orderByNameThenKind);
            Assert.fail(("Tests declared for nonexistent operators:\n\t" + (Joiner.on("\n\t").join(undeclaredList))));
        }
    }

    @Test
    @BeamSqlDslSqlStdOperatorsTest.SqlOperatorTest(name = "OR", kind = "OR")
    @BeamSqlDslSqlStdOperatorsTest.SqlOperatorTest(name = "NOT", kind = "NOT")
    @BeamSqlDslSqlStdOperatorsTest.SqlOperatorTest(name = "AND", kind = "AND")
    public void testLogicOperators() {
        BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker checker = new BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker().addExpr("1 = 1 AND 1 = 1", true).addExpr("1 = 1 OR 1 = 2", true).addExpr("NOT 1 = 2", true).addExpr("(NOT 1 = 2) AND (2 = 1 OR 3 = 3)", true).addExpr("2 = 2 AND 2 = 1", false).addExpr("1 = 2 OR 3 = 2", false).addExpr("NOT 1 = 1", false).addExpr("(NOT 2 = 2) AND (1 = 2 OR 2 = 3)", false).addExpr("'a' = 'a' AND 'a' = 'a'", true).addExpr("'a' = 'a' OR 'a' = 'b'", true).addExpr("NOT 'a' = 'b'", true).addExpr("(NOT 'a' = 'b') AND ('b' = 'a' OR 'c' = 'c')", true).addExpr("'b' = 'b' AND 'b' = 'a'", false).addExpr("'a' = 'b' OR 'c' = 'b'", false).addExpr("NOT 'a' = 'a'", false).addExpr("(NOT 'b' = 'b') AND ('a' = 'b' OR 'b' = 'c')", false).addExpr("1.0 = 1.0 AND 1.0 = 1.0", true).addExpr("1.0 = 1.0 OR 1.0 = 2.0", true).addExpr("NOT 1.0 = 2.0", true).addExpr("(NOT 1.0 = 2.0) AND (2.0 = 1.0 OR 3.0 = 3.0)", true).addExpr("2.0 = 2.0 AND 2.0 = 1.0", false).addExpr("1.0 = 2.0 OR 3.0 = 2.0", false).addExpr("NOT 1.0 = 1.0", false).addExpr("(NOT 2.0 = 2.0) AND (1.0 = 2.0 OR 2.0 = 3.0)", false).addExpr("NOT true", false).addExpr("NOT false", true).addExpr("true AND true", true).addExpr("true AND false", false).addExpr("false AND false", false).addExpr("true OR true", true).addExpr("true OR false", true).addExpr("false OR false", false).addExpr("(NOT false) AND (true OR false)", true).addExpr("(NOT true) AND (true OR false)", false).addExpr("(NOT false) OR (true and false)", true).addExpr("(NOT true) OR (true and false)", false);
        checker.buildRunAndCheck();
    }

    @Test
    @BeamSqlDslSqlStdOperatorsTest.SqlOperatorTest(name = "+", kind = "PLUS")
    @BeamSqlDslSqlStdOperatorsTest.SqlOperatorTest(name = "-", kind = "MINUS")
    @BeamSqlDslSqlStdOperatorsTest.SqlOperatorTest(name = "*", kind = "TIMES")
    @BeamSqlDslSqlStdOperatorsTest.SqlOperatorTest(name = "/", kind = "DIVIDE")
    @BeamSqlDslSqlStdOperatorsTest.SqlOperatorTest(name = "MOD", kind = "MOD")
    public void testArithmeticOperator() {
        BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker checker = // Test overflow
        new BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker().addExpr("1 + 1", 2).addExpr("1.0 + 1", BeamSqlDslSqlStdOperatorsTest.TWO).addExpr("1 + 1.0", BeamSqlDslSqlStdOperatorsTest.TWO).addExpr("1.0 + 1.0", BeamSqlDslSqlStdOperatorsTest.TWO).addExpr("c_tinyint + c_tinyint", ((byte) (2))).addExpr("c_smallint + c_smallint", ((short) (2))).addExpr("c_bigint + c_bigint", 2L).addExpr("c_decimal + c_decimal", BeamSqlDslSqlStdOperatorsTest.TWO0).addExpr("c_tinyint + c_decimal", BeamSqlDslSqlStdOperatorsTest.TWO0).addExpr("c_float + c_decimal", 2.0).addExpr("c_double + c_decimal", 2.0).addExpr("c_float + c_float", 2.0F).addExpr("c_double + c_float", 2.0).addExpr("c_double + c_double", 2.0).addExpr("c_float + c_bigint", 2.0F).addExpr("c_double + c_bigint", 2.0).addExpr("1 - 1", 0).addExpr("1.0 - 1", BeamSqlDslSqlStdOperatorsTest.ZERO).addExpr("1 - 0.0", BeamSqlDslSqlStdOperatorsTest.ONE).addExpr("1.0 - 1.0", BeamSqlDslSqlStdOperatorsTest.ZERO).addExpr("c_tinyint - c_tinyint", ((byte) (0))).addExpr("c_smallint - c_smallint", ((short) (0))).addExpr("c_bigint - c_bigint", 0L).addExpr("c_decimal - c_decimal", BigDecimal.ZERO).addExpr("c_tinyint - c_decimal", BigDecimal.ZERO).addExpr("c_float - c_decimal", 0.0).addExpr("c_double - c_decimal", 0.0).addExpr("c_float - c_float", 0.0F).addExpr("c_double - c_float", 0.0).addExpr("c_double - c_double", 0.0).addExpr("c_float - c_bigint", 0.0F).addExpr("c_double - c_bigint", 0.0).addExpr("1 * 1", 1).addExpr("1.0 * 1", BeamSqlDslSqlStdOperatorsTest.ONE).addExpr("1 * 1.0", BeamSqlDslSqlStdOperatorsTest.ONE).addExpr("1.0 * 1.0", BeamSqlDslSqlStdOperatorsTest.ONE2).addExpr("c_tinyint * c_tinyint", ((byte) (1))).addExpr("c_smallint * c_smallint", ((short) (1))).addExpr("c_bigint * c_bigint", 1L).addExpr("c_decimal * c_decimal", BigDecimal.ONE).addExpr("c_tinyint * c_decimal", BigDecimal.ONE).addExpr("c_float * c_decimal", 1.0).addExpr("c_double * c_decimal", 1.0).addExpr("c_float * c_float", 1.0F).addExpr("c_double * c_float", 1.0).addExpr("c_double * c_double", 1.0).addExpr("c_float * c_bigint", 1.0F).addExpr("c_double * c_bigint", 1.0).addExpr("1 / 1", 1).addExpr("1.0 / 1", BeamSqlDslSqlStdOperatorsTest.ONE).addExpr("1 / 1.0", BigDecimal.ONE).addExpr("1.0 / 1.0", BigDecimal.ONE).addExpr("c_tinyint / c_tinyint", ((byte) (1))).addExpr("c_smallint / c_smallint", ((short) (1))).addExpr("c_bigint / c_bigint", 1L).addExpr("c_decimal / c_decimal", BigDecimal.ONE).addExpr("c_tinyint / c_decimal", BigDecimal.ONE).addExpr("c_float / c_decimal", 1.0).addExpr("c_double / c_decimal", 1.0).addExpr("c_float / c_float", 1.0F).addExpr("c_double / c_float", 1.0).addExpr("c_double / c_double", 1.0).addExpr("c_float / c_bigint", 1.0F).addExpr("c_double / c_bigint", 1.0).addExpr("mod(1, 1)", 0).addExpr("mod(1.0, 1)", 0).addExpr("mod(1, 1.0)", BigDecimal.ZERO).addExpr("mod(1.0, 1.0)", BeamSqlDslSqlStdOperatorsTest.ZERO).addExpr("mod(c_tinyint, c_tinyint)", ((byte) (0))).addExpr("mod(c_smallint, c_smallint)", ((short) (0))).addExpr("mod(c_bigint, c_bigint)", 0L).addExpr("mod(c_decimal, c_decimal)", BigDecimal.ZERO).addExpr("mod(c_tinyint, c_decimal)", BigDecimal.ZERO).addExpr("c_tinyint_max + c_tinyint_max", ((byte) (-2))).addExpr("c_smallint_max + c_smallint_max", ((short) (-2))).addExpr("c_integer_max + c_integer_max", (-2)).addExpr("c_bigint_max + c_bigint_max", (-2L));
        checker.buildRunAndCheck();
    }

    @Test
    @BeamSqlDslSqlStdOperatorsTest.SqlOperatorTest(name = "LIKE", kind = "LIKE")
    @BeamSqlDslSqlStdOperatorsTest.SqlOperatorTest(name = "NOT LIKE", kind = "LIKE")
    public void testLikeAndNotLike() {
        BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker checker = new BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker().addExpr("'string_true_test' LIKE 'string_true_test'", true).addExpr("'string_true_test' LIKE 'string_false_test'", false).addExpr("'string_false_test' LIKE 'string_false_test'", true).addExpr("'string_false_test' LIKE 'string_true_test'", false).addExpr("'string_true_test' LIKE 'string_true_test%'", true).addExpr("'string_true_test' LIKE 'string_false_test%'", false).addExpr("'string_true_test' LIKE 'string_true%'", true).addExpr("'string_true_test' LIKE 'string_false%'", false).addExpr("'string_true_test' LIKE 'string%test'", true).addExpr("'string_true_test' LIKE '%test'", true).addExpr("'string_true_test' LIKE '%string_true_test'", true).addExpr("'string_true_test' LIKE '%string_false_test'", false).addExpr("'string_true_test' LIKE '%false_test'", false).addExpr("'string_false_test' LIKE '%false_test'", true).addExpr("'string_true_test' LIKE 'string_tr_e_test'", true).addExpr("'string_true_test' LIKE 'string______test'", true).addExpr("'string_false_test' LIKE 'string______test'", false).addExpr("'string_false_test' LIKE 'string_______test'", true).addExpr("'string_false_test' LIKE 'string_false_te__'", true).addExpr("'string_false_test' LIKE 'string_false_te___'", false).addExpr("'string_false_test' LIKE 'string_false_te_'", false).addExpr("'string_true_test' LIKE 'string_true_te__'", true).addExpr("'string_true_test' LIKE '_ring_true_te__'", false).addExpr("'string_false_test' LIKE '__ring_false_te__'", true).addExpr("'string_true_test' LIKE '_%ring_true_te__'", true).addExpr("'string_true_test' LIKE '_%tring_true_te__'", true).addExpr("'string_false_test' LIKE 'string_false_te%__'", true).addExpr("'string_false_test' LIKE 'string_false_te__%'", true).addExpr("'string_false_test' LIKE 'string_false_t%__'", true).addExpr("'string_false_test' LIKE 'string_false_t__%'", true).addExpr("'string_false_test' LIKE 'string_false_te_%'", true).addExpr("'string_false_test' LIKE 'string_false_te%_'", true).addExpr("'string_true_test' LIKE 'string_%test'", true).addExpr("'string_true_test' LIKE 'string%_test'", true).addExpr("'string_true_test' LIKE 'string_%_test'", true).addExpr("'string_true_test' NOT LIKE 'string_true_test'", false).addExpr("'string_true_test' NOT LIKE 'string_false_test'", true).addExpr("'string_false_test' NOT LIKE 'string_false_test'", false).addExpr("'string_false_test' NOT LIKE 'string_true_test'", true).addExpr("'string_true_test' NOT LIKE 'string_true_test%'", false).addExpr("'string_true_test' NOT LIKE 'string_false_test%'", true).addExpr("'string_true_test' NOT LIKE 'string_true%'", false).addExpr("'string_true_test' NOT LIKE 'string_false%'", true).addExpr("'string_true_test' NOT LIKE 'string%test'", false).addExpr("'string_true_test' NOT LIKE '%test'", false).addExpr("'string_true_test' NOT LIKE '%string_true_test'", false).addExpr("'string_true_test' NOT LIKE '%string_false_test'", true).addExpr("'string_true_test' NOT LIKE '%false_test'", true).addExpr("'string_false_test' NOT LIKE '%false_test'", false).addExpr("'string_true_test' NOT LIKE 'string_tr_e_test'", false).addExpr("'string_true_test' NOT LIKE 'string______test'", false).addExpr("'string_false_test' NOT LIKE 'string______test'", true).addExpr("'string_false_test' NOT LIKE 'string_______test'", false).addExpr("'string_false_test' NOT LIKE 'string_false_te__'", false).addExpr("'string_false_test' NOT LIKE 'string_false_te___'", true).addExpr("'string_false_test' NOT LIKE 'string_false_te_'", true).addExpr("'string_true_test' NOT LIKE 'string_true_te__'", false).addExpr("'string_true_test' NOT LIKE '_ring_true_te__'", true).addExpr("'string_false_test' NOT LIKE '__ring_false_te__'", false).addExpr("'string_true_test' NOT LIKE '_%ring_true_te__'", false).addExpr("'string_true_test' NOT LIKE '_%tring_true_te__'", false).addExpr("'string_false_test' NOT LIKE 'string_false_te%__'", false).addExpr("'string_false_test' NOT LIKE 'string_false_te__%'", false).addExpr("'string_false_test' NOT LIKE 'string_false_t%__'", false).addExpr("'string_false_test' NOT LIKE 'string_false_t__%'", false).addExpr("'string_false_test' NOT LIKE 'string_false_te_%'", false).addExpr("'string_false_test' NOT LIKE 'string_false_te%_'", false).addExpr("'string_true_test' NOT LIKE 'string_%test'", false).addExpr("'string_true_test' NOT LIKE 'string%_test'", false).addExpr("'string_true_test' NOT LIKE 'string_%_test'", false);
        checker.buildRunAndCheck();
    }

    @Test
    @BeamSqlDslSqlStdOperatorsTest.SqlOperatorTest(name = "<", kind = "LESS_THAN")
    @BeamSqlDslSqlStdOperatorsTest.SqlOperatorTest(name = ">", kind = "GREATER_THAN")
    @BeamSqlDslSqlStdOperatorsTest.SqlOperatorTest(name = "<=", kind = "LESS_THAN_OR_EQUAL")
    @BeamSqlDslSqlStdOperatorsTest.SqlOperatorTest(name = "<>", kind = "NOT_EQUALS")
    @BeamSqlDslSqlStdOperatorsTest.SqlOperatorTest(name = "=", kind = "EQUALS")
    @BeamSqlDslSqlStdOperatorsTest.SqlOperatorTest(name = ">=", kind = "GREATER_THAN_OR_EQUAL")
    @BeamSqlDslSqlStdOperatorsTest.SqlOperatorTest(name = "IS NOT NULL", kind = "IS_NOT_NULL")
    @BeamSqlDslSqlStdOperatorsTest.SqlOperatorTest(name = "IS NULL", kind = "IS_NULL")
    @BeamSqlDslSqlStdOperatorsTest.SqlOperatorTest(name = "IS TRUE", kind = "IS_TRUE")
    @BeamSqlDslSqlStdOperatorsTest.SqlOperatorTest(name = "IS NOT TRUE", kind = "IS_NOT_TRUE")
    @BeamSqlDslSqlStdOperatorsTest.SqlOperatorTest(name = "IS FALSE", kind = "IS_FALSE")
    @BeamSqlDslSqlStdOperatorsTest.SqlOperatorTest(name = "IS NOT FALSE", kind = "IS_NOT_FALSE")
    @BeamSqlDslSqlStdOperatorsTest.SqlOperatorTest(name = "IS UNKNOWN", kind = "IS_NULL")
    @BeamSqlDslSqlStdOperatorsTest.SqlOperatorTest(name = "IS NOT UNKNOWN", kind = "IS_NOT_NULL")
    @BeamSqlDslSqlStdOperatorsTest.SqlOperatorTest(name = "IS DISTINCT FROM", kind = "IS_DISTINCT_FROM")
    @BeamSqlDslSqlStdOperatorsTest.SqlOperatorTest(name = "IS NOT DISTINCT FROM", kind = "IS_NOT_DISTINCT_FROM")
    public void testComparisonOperatorFunction() {
        BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker checker = new BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker().addExpr("1 < 2", true).addExpr("2 < 1", false).addExpr("'a' < 'b'", true).addExpr("'b' < 'a'", false).addExpr("1.0 < 2.0", true).addExpr("2.0 < 1.0", false).addExpr("9223372036854775806 < 9223372036854775807", true).addExpr("9223372036854775807 < 9223372036854775806", false).addExpr("false < true", true).addExpr("true < false", false).addExpr("1 > 2", false).addExpr("2 > 1", true).addExpr("'a' > 'b'", false).addExpr("'b' > 'a'", true).addExpr("1.0 > 2.0", false).addExpr("2.0 > 1.0", true).addExpr("9223372036854775806 > 9223372036854775807", false).addExpr("9223372036854775807 > 9223372036854775806", true).addExpr("false > true", false).addExpr("true > false", true).addExpr("1 <> 2", true).addExpr("1 <> 1", false).addExpr("'a' <> 'b'", true).addExpr("'a' <> 'a'", false).addExpr("1.0 <> 2.0", true).addExpr("1.0 <> 1.0", false).addExpr("9223372036854775806 <> 9223372036854775807", true).addExpr("9223372036854775806 <> 9223372036854775806", false).addExpr("false <> true", true).addExpr("false <> false", false).addExpr("1 = 1", true).addExpr("2 = 1", false).addExpr("'a' = 'a'", true).addExpr("'b' = 'a'", false).addExpr("1.0 = 1.0", true).addExpr("2.0 = 1.0", false).addExpr("9223372036854775807 = 9223372036854775807", true).addExpr("9223372036854775807 = 9223372036854775806", false).addExpr("true = true", true).addExpr("true = false", false).addExpr("1 >= 2", false).addExpr("2 >= 2", true).addExpr("2 >= 1", true).addExpr("'a' >= 'b'", false).addExpr("'b' >= 'b'", true).addExpr("'b' >= 'a'", true).addExpr("1.0 >= 2.0", false).addExpr("2.0 >= 1.0", true).addExpr("2.0 >= 2.0", true).addExpr("9223372036854775806 >= 9223372036854775807", false).addExpr("9223372036854775807 >= 9223372036854775806", true).addExpr("9223372036854775807 >= 9223372036854775807", true).addExpr("false >= true", false).addExpr("true >= false", true).addExpr("true >= true", true).addExpr("1 IS NOT NULL", true).addExpr("true IS NOT NULL", true).addExpr("1.0 IS NOT NULL", true).addExpr("'a' IS NOT NULL", true).addExpr("NULL IS NOT NULL", false).addExpr("1 IS NULL", false).addExpr("true IS NULL", false).addExpr("1.0 IS NULL", false).addExpr("'a' IS NULL", false).addExpr("NULL IS NULL", true).addExpr("true IS TRUE", true).addExpr("false IS TRUE", false).addExpr("true IS NOT TRUE", false).addExpr("false IS NOT TRUE", true).addExpr("true IS FALSE", false).addExpr("false IS FALSE", true).addExpr("true IS NOT FALSE", true).addExpr("false IS NOT FALSE", false).addExpr("3 = 5 IS NOT UNKNOWN", true).addExpr("5 = 5 IS NOT UNKNOWN", true).addExpr("(NOT 5 = 5) IS NOT UNKNOWN", true).addExpr("(3 = NULL) IS UNKNOWN", true).addExpr("(3 = NULL) IS NOT UNKNOWN", false).addExpr("(NULL = NULL) IS NOT UNKNOWN", false).addExpr("(NOT NULL = NULL) IS NOT UNKNOWN", false).addExpr("1 IS DISTINCT FROM 2", true).addExpr("1.0 IS DISTINCT FROM 2.0", true).addExpr("'a' IS DISTINCT FROM 'b'", true).addExpr("true IS DISTINCT FROM false", true).addExpr("1 IS NOT DISTINCT FROM 2", false).addExpr("1.0 IS NOT DISTINCT FROM 2.0", false).addExpr("'a' IS NOT DISTINCT FROM 'b'", false).addExpr("true IS NOT DISTINCT FROM false", false).addExpr("date '2018-01-01' > DATE '2017-12-31' ", true).addExpr("date '2018-01-01' >= DATE '2017-12-31' ", true).addExpr("date '2018-01-01' < DATE '2017-12-31' ", false).addExpr("date '2018-01-01' <= DATE '2017-12-31' ", false).addExpr("date '2018-06-24' = DATE '2018-06-24' ", true).addExpr("Date '2018-06-24' <> DATE '2018-06-24' ", false).addExpr("TIME '20:17:40' < Time '15:05:57' ", false).addExpr("TIME '00:00:01' >= time '00:00:01' ", true).addExpr("TIMESTAMP '2017-12-31 23:59:59' < TIMESTAMP '2018-01-01 00:00:00' ", true);
        checker.buildRunAndCheck();
    }

    @Test
    @BeamSqlDslSqlStdOperatorsTest.SqlOperatorTest(name = "MAX", kind = "MAX")
    public void testMax() {
        BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker checker = new BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker().addExpr("MAX(c_tinyint)", ((byte) (3))).addExpr("MAX(c_smallint)", ((short) (3))).addExpr("MAX(c_integer)", 3).addExpr("MAX(c_bigint)", 3L).addExpr("MAX(c_float)", 3.0F).addExpr("MAX(c_double)", 3.0).addExpr("MAX(c_decimal)", BigDecimal.valueOf(3.0)).addExpr("MAX(ts)", DateTimeUtils.parseTimestampWithUTCTimeZone("1986-04-15 11:35:26"));
        checker.buildRunAndCheck(getAggregationTestPCollection());
    }

    @Test
    @BeamSqlDslSqlStdOperatorsTest.SqlOperatorTest(name = "MIN", kind = "MIN")
    public void testMin() {
        BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker checker = new BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker().addExpr("MIN(c_tinyint)", ((byte) (1))).addExpr("MIN(c_smallint)", ((short) (1))).addExpr("MIN(c_integer)", 1).addExpr("MIN(c_bigint)", 1L).addExpr("MIN(c_float)", 1.0F).addExpr("MIN(c_double)", 1.0).addExpr("MIN(c_decimal)", BigDecimal.valueOf(1.0)).addExpr("MIN(ts)", DateTimeUtils.parseTimestampWithUTCTimeZone("1986-02-15 11:35:26"));
        checker.buildRunAndCheck(getAggregationTestPCollection());
    }

    @Test
    @BeamSqlDslSqlStdOperatorsTest.SqlOperatorTest(name = "SUM", kind = "SUM")
    public void testSum() {
        BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker checker = new BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker().addExpr("SUM(c_tinyint)", ((byte) (6))).addExpr("SUM(c_smallint)", ((short) (6))).addExpr("SUM(c_integer)", 6).addExpr("SUM(c_bigint)", 6L).addExpr("SUM(c_float)", 6.0F).addExpr("SUM(c_double)", 6.0).addExpr("SUM(c_decimal)", BigDecimal.valueOf(6.0));
        checker.buildRunAndCheck(getAggregationTestPCollection());
    }

    @Test
    @BeamSqlDslSqlStdOperatorsTest.SqlOperatorTest(name = "AVG", kind = "AVG")
    public void testAvg() {
        BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker checker = new BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker().addExpr("AVG(c_tinyint)", ((byte) (2))).addExpr("AVG(c_smallint)", ((short) (2))).addExpr("AVG(c_integer)", 2).addExpr("AVG(c_bigint)", 2L).addExpr("AVG(c_float)", 2.0F).addExpr("AVG(c_double)", 2.0).addExpr("AVG(c_decimal)", BigDecimal.valueOf(2.0));
        checker.buildRunAndCheck(getAggregationTestPCollection());
    }

    @Test
    @BeamSqlDslSqlStdOperatorsTest.SqlOperatorTest(name = "$SUM0", kind = "SUM0")
    public void testSUM0() {
        BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker checker = new BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker().addExpr("$SUM0(c_tinyint)", ((byte) (6))).addExpr("$SUM0(c_smallint)", ((short) (6))).addExpr("$SUM0(c_integer)", 6).addExpr("$SUM0(c_bigint)", 6L).addExpr("$SUM0(c_float)", 6.0F).addExpr("$SUM0(c_double)", 6.0).addExpr("$SUM0(c_decimal)", BigDecimal.valueOf(6.0));
        checker.buildRunAndCheck(getAggregationTestPCollection());
    }

    @Test
    @BeamSqlDslSqlStdOperatorsTest.SqlOperatorTest(name = "COUNT", kind = "COUNT")
    public void testCount() {
        BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker checker = new BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker().addExpr("COUNT(*)", 4L).addExpr("COUNT(1)", 4L);
        checker.buildRunAndCheck(getAggregationTestPCollection());
    }

    @Test
    @BeamSqlDslSqlStdOperatorsTest.SqlOperatorTest(name = "VAR_POP", kind = "VAR_POP")
    public void testVARPOP() {
        BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker checker = new BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker().addExpr("VAR_POP(c_integer)", 0).addExpr("VAR_POP(c_double)", 0.6666666);
        checker.buildRunAndCheck(getAggregationTestPCollection());
    }

    @Test
    @BeamSqlDslSqlStdOperatorsTest.SqlOperatorTest(name = "VAR_SAMP", kind = "VAR_SAMP")
    public void testVARSAMP() {
        BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker checker = new BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker().addExpr("VAR_SAMP(c_integer)", 1).addExpr("VAR_SAMP(c_double)", 1.0);
        checker.buildRunAndCheck(getAggregationTestPCollection());
    }

    @Test
    @BeamSqlDslSqlStdOperatorsTest.SqlOperatorTest(name = "COVAR_POP", kind = "COVAR_POP")
    public void testCOVARPOP() {
        BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker checker = new BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker().addExpr("COVAR_POP(c_integer, c_integer_two)", 0).addExpr("COVAR_POP(c_double, c_double_two)", 0.6666666);
        checker.buildRunAndCheck(getAggregationTestPCollection());
    }

    @Test
    @BeamSqlDslSqlStdOperatorsTest.SqlOperatorTest(name = "COVAR_SAMP", kind = "COVAR_SAMP")
    public void testAggrationFunctions() {
        BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker checker = new BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker().addExpr("COVAR_SAMP(c_integer, c_integer_two)", 1).addExpr("COVAR_SAMP(c_double, c_double_two)", 1.0);
        checker.buildRunAndCheck(getAggregationTestPCollection());
    }

    @Test
    @BeamSqlDslSqlStdOperatorsTest.SqlOperatorTest(name = "CHARACTER_LENGTH", kind = "OTHER_FUNCTION")
    @BeamSqlDslSqlStdOperatorsTest.SqlOperatorTest(name = "CHAR_LENGTH", kind = "OTHER_FUNCTION")
    @BeamSqlDslSqlStdOperatorsTest.SqlOperatorTest(name = "INITCAP", kind = "OTHER_FUNCTION")
    @BeamSqlDslSqlStdOperatorsTest.SqlOperatorTest(name = "LOWER", kind = "OTHER_FUNCTION")
    @BeamSqlDslSqlStdOperatorsTest.SqlOperatorTest(name = "POSITION", kind = "OTHER_FUNCTION")
    @BeamSqlDslSqlStdOperatorsTest.SqlOperatorTest(name = "OVERLAY", kind = "OTHER_FUNCTION")
    @BeamSqlDslSqlStdOperatorsTest.SqlOperatorTest(name = "SUBSTRING", kind = "OTHER_FUNCTION")
    @BeamSqlDslSqlStdOperatorsTest.SqlOperatorTest(name = "TRIM", kind = "TRIM")
    @BeamSqlDslSqlStdOperatorsTest.SqlOperatorTest(name = "UPPER", kind = "OTHER_FUNCTION")
    @BeamSqlDslSqlStdOperatorsTest.SqlOperatorTest(name = "||", kind = "OTHER")
    public void testStringFunctions() throws Exception {
        BeamSqlBuiltinFunctionsIntegrationTestBase.SqlExpressionChecker checker = // https://issues.apache.org/jira/browse/BEAM-4704
        new BeamSqlBuiltinFunctionsIntegrationTestBase.SqlExpressionChecker().addExpr("'hello' || ' world' = 'hello world'").addExpr("'hello' || '' = 'hello'").addExpr("'' || '' = ''").addExpr("CHAR_LENGTH('hello') = 5").addExpr("CHARACTER_LENGTH('hello') = 5").addExpr("INITCAP('hello world') = 'Hello World'").addExpr("LOWER('HELLO') = 'hello'").addExpr("POSITION('world' IN 'helloworld') = 6").addExpr("POSITION('world' IN 'helloworldworld' FROM 7) = 11").addExpr("POSITION('world' IN 'hello') = 0").addExpr("TRIM(' hello ') = 'hello'").addExpr("TRIM(LEADING 'eh' FROM 'hehe__hehe') = '__hehe'").addExpr("TRIM(TRAILING 'eh' FROM 'hehe__hehe') = 'hehe__'").addExpr("TRIM(BOTH 'eh' FROM 'hehe__hehe') = '__'").addExpr("TRIM(BOTH ' ' FROM ' hello ') = 'hello'").addExpr("OVERLAY('w3333333rce' PLACING 'resou' FROM 3) = 'w3resou3rce'").addExpr("OVERLAY('w3333333rce' PLACING 'resou' FROM 3 FOR 4) = 'w3resou33rce'").addExpr("OVERLAY('w3333333rce' PLACING 'resou' FROM 3 FOR 5) = 'w3resou3rce'").addExpr("OVERLAY('w3333333rce' PLACING 'resou' FROM 3 FOR 7) = 'w3resouce'").addExpr("SUBSTRING('hello' FROM 2) = 'ello'").addExpr("SUBSTRING('hello' FROM -1) = 'o'").addExpr("SUBSTRING('hello' FROM 2 FOR 2) = 'el'").addExpr("SUBSTRING('hello' FROM 2 FOR 100) = 'ello'").addExpr("SUBSTRING('hello' FROM -3 for 2) = 'll'").addExpr("UPPER('hello') = 'HELLO'");
        checker.check(pipeline);
        pipeline.run();
    }

    @Test
    @BeamSqlDslSqlStdOperatorsTest.SqlOperatorTest(name = "ABS", kind = "OTHER_FUNCTION")
    public void testAbs() {
        BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker checker = new BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker().addExpr("ABS(c_integer)", Math.abs(BeamSqlDslSqlStdOperatorsTest.INTEGER_VALUE)).addExpr("ABS(c_bigint)", Math.abs(BeamSqlDslSqlStdOperatorsTest.LONG_VALUE)).addExpr("ABS(c_smallint)", ((short) (Math.abs(BeamSqlDslSqlStdOperatorsTest.SHORT_VALUE)))).addExpr("ABS(c_tinyint)", ((byte) (Math.abs(BeamSqlDslSqlStdOperatorsTest.BYTE_VALUE)))).addExpr("ABS(c_double)", Math.abs(BeamSqlDslSqlStdOperatorsTest.DOUBLE_VALUE)).addExpr("ABS(c_float)", Math.abs(BeamSqlDslSqlStdOperatorsTest.FLOAT_VALUE)).addExpr("ABS(c_decimal)", new BigDecimal(Math.abs(BeamSqlDslSqlStdOperatorsTest.ONE.doubleValue())));
        checker.buildRunAndCheck();
    }

    @Test
    @BeamSqlDslSqlStdOperatorsTest.SqlOperatorTest(name = "LN", kind = "OTHER_FUNCTION")
    public void testLn() {
        BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker checker = new BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker().addExpr("LN(c_integer)", Math.log(BeamSqlDslSqlStdOperatorsTest.INTEGER_VALUE)).addExpr("LN(c_bigint)", Math.log(BeamSqlDslSqlStdOperatorsTest.LONG_VALUE)).addExpr("LN(c_smallint)", Math.log(BeamSqlDslSqlStdOperatorsTest.SHORT_VALUE)).addExpr("LN(c_tinyint)", Math.log(BeamSqlDslSqlStdOperatorsTest.BYTE_VALUE)).addExpr("LN(c_double)", Math.log(BeamSqlDslSqlStdOperatorsTest.DOUBLE_VALUE)).addExpr("LN(c_float)", Math.log(BeamSqlDslSqlStdOperatorsTest.FLOAT_VALUE)).addExpr("LN(c_decimal)", Math.log(BeamSqlDslSqlStdOperatorsTest.ONE.doubleValue()));
        checker.buildRunAndCheck();
    }

    @Test
    @BeamSqlDslSqlStdOperatorsTest.SqlOperatorTest(name = "SQRT", kind = "OTHER_FUNCTION")
    public void testSqrt() {
        BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker checker = new BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker().addExpr("SQRT(c_integer)", Math.sqrt(BeamSqlDslSqlStdOperatorsTest.INTEGER_VALUE)).addExpr("SQRT(c_bigint)", Math.sqrt(BeamSqlDslSqlStdOperatorsTest.LONG_VALUE)).addExpr("SQRT(c_smallint)", Math.sqrt(BeamSqlDslSqlStdOperatorsTest.SHORT_VALUE)).addExpr("SQRT(c_tinyint)", Math.sqrt(BeamSqlDslSqlStdOperatorsTest.BYTE_VALUE)).addExpr("SQRT(c_double)", Math.sqrt(BeamSqlDslSqlStdOperatorsTest.DOUBLE_VALUE)).addExpr("SQRT(c_float)", Math.sqrt(BeamSqlDslSqlStdOperatorsTest.FLOAT_VALUE)).addExpr("SQRT(c_decimal)", Math.sqrt(BeamSqlDslSqlStdOperatorsTest.ONE.doubleValue()));
        checker.buildRunAndCheck();
    }

    @Test
    @BeamSqlDslSqlStdOperatorsTest.SqlOperatorTest(name = "ROUND", kind = "OTHER_FUNCTION")
    public void testRound() {
        BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker checker = new BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker().addExpr("ROUND(c_integer, 0)", SqlFunctions.sround(BeamSqlDslSqlStdOperatorsTest.INTEGER_VALUE, 0)).addExpr("ROUND(c_bigint, 0)", SqlFunctions.sround(BeamSqlDslSqlStdOperatorsTest.LONG_VALUE, 0)).addExpr("ROUND(c_smallint, 0)", ((short) (SqlFunctions.sround(BeamSqlDslSqlStdOperatorsTest.SHORT_VALUE, 0)))).addExpr("ROUND(c_tinyint, 0)", ((byte) (SqlFunctions.sround(BeamSqlDslSqlStdOperatorsTest.BYTE_VALUE, 0)))).addExpr("ROUND(c_double, 0)", SqlFunctions.sround(BeamSqlDslSqlStdOperatorsTest.DOUBLE_VALUE, 0)).addExpr("ROUND(c_float, 0)", ((float) (SqlFunctions.sround(BeamSqlDslSqlStdOperatorsTest.FLOAT_VALUE, 0)))).addExpr("ROUND(c_decimal, 0)", new BigDecimal(SqlFunctions.sround(BeamSqlDslSqlStdOperatorsTest.ONE.doubleValue(), 0)));
        checker.buildRunAndCheck();
    }

    @Test
    @BeamSqlDslSqlStdOperatorsTest.SqlOperatorTest(name = "LOG10", kind = "OTHER_FUNCTION")
    public void testLog10() {
        BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker checker = new BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker().addExpr("LOG10(c_integer)", Math.log10(BeamSqlDslSqlStdOperatorsTest.INTEGER_VALUE)).addExpr("LOG10(c_bigint)", Math.log10(BeamSqlDslSqlStdOperatorsTest.LONG_VALUE)).addExpr("LOG10(c_smallint)", Math.log10(BeamSqlDslSqlStdOperatorsTest.SHORT_VALUE)).addExpr("LOG10(c_tinyint)", Math.log10(BeamSqlDslSqlStdOperatorsTest.BYTE_VALUE)).addExpr("LOG10(c_double)", Math.log10(BeamSqlDslSqlStdOperatorsTest.DOUBLE_VALUE)).addExpr("LOG10(c_float)", Math.log10(BeamSqlDslSqlStdOperatorsTest.FLOAT_VALUE)).addExpr("LOG10(c_decimal)", Math.log10(BeamSqlDslSqlStdOperatorsTest.ONE.doubleValue()));
        checker.buildRunAndCheck();
    }

    @Test
    @BeamSqlDslSqlStdOperatorsTest.SqlOperatorTest(name = "EXP", kind = "OTHER_FUNCTION")
    public void testExp() {
        BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker checker = new BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker().addExpr("EXP(c_integer)", Math.exp(BeamSqlDslSqlStdOperatorsTest.INTEGER_VALUE)).addExpr("EXP(c_bigint)", Math.exp(BeamSqlDslSqlStdOperatorsTest.LONG_VALUE)).addExpr("EXP(c_smallint)", Math.exp(BeamSqlDslSqlStdOperatorsTest.SHORT_VALUE)).addExpr("EXP(c_tinyint)", Math.exp(BeamSqlDslSqlStdOperatorsTest.BYTE_VALUE)).addExpr("EXP(c_double)", Math.exp(BeamSqlDslSqlStdOperatorsTest.DOUBLE_VALUE)).addExpr("EXP(c_float)", Math.exp(BeamSqlDslSqlStdOperatorsTest.FLOAT_VALUE)).addExpr("EXP(c_decimal)", Math.exp(BeamSqlDslSqlStdOperatorsTest.ONE.doubleValue()));
        checker.buildRunAndCheck();
    }

    @Test
    @BeamSqlDslSqlStdOperatorsTest.SqlOperatorTest(name = "ACOS", kind = "OTHER_FUNCTION")
    public void testAcos() {
        BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker checker = new BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker().addExpr("ACOS(c_integer)", Math.acos(BeamSqlDslSqlStdOperatorsTest.INTEGER_VALUE)).addExpr("ACOS(c_bigint)", Math.acos(BeamSqlDslSqlStdOperatorsTest.LONG_VALUE)).addExpr("ACOS(c_smallint)", Math.acos(BeamSqlDslSqlStdOperatorsTest.SHORT_VALUE)).addExpr("ACOS(c_tinyint)", Math.acos(BeamSqlDslSqlStdOperatorsTest.BYTE_VALUE)).addExpr("ACOS(c_double)", Math.acos(BeamSqlDslSqlStdOperatorsTest.DOUBLE_VALUE)).addExpr("ACOS(c_float)", Math.acos(BeamSqlDslSqlStdOperatorsTest.FLOAT_VALUE)).addExpr("ACOS(c_decimal)", Math.acos(BeamSqlDslSqlStdOperatorsTest.ONE.doubleValue()));
        checker.buildRunAndCheck();
    }

    @Test
    @BeamSqlDslSqlStdOperatorsTest.SqlOperatorTest(name = "ASIN", kind = "OTHER_FUNCTION")
    public void testAsin() {
        BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker checker = new BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker().addExpr("ASIN(c_integer)", Math.asin(BeamSqlDslSqlStdOperatorsTest.INTEGER_VALUE)).addExpr("ASIN(c_bigint)", Math.asin(BeamSqlDslSqlStdOperatorsTest.LONG_VALUE)).addExpr("ASIN(c_smallint)", Math.asin(BeamSqlDslSqlStdOperatorsTest.SHORT_VALUE)).addExpr("ASIN(c_tinyint)", Math.asin(BeamSqlDslSqlStdOperatorsTest.BYTE_VALUE)).addExpr("ASIN(c_double)", Math.asin(BeamSqlDslSqlStdOperatorsTest.DOUBLE_VALUE)).addExpr("ASIN(c_float)", Math.asin(BeamSqlDslSqlStdOperatorsTest.FLOAT_VALUE)).addExpr("ASIN(c_decimal)", Math.asin(BeamSqlDslSqlStdOperatorsTest.ONE.doubleValue()));
        checker.buildRunAndCheck();
    }

    @Test
    @BeamSqlDslSqlStdOperatorsTest.SqlOperatorTest(name = "ATAN", kind = "OTHER_FUNCTION")
    public void testAtan() {
        BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker checker = new BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker().addExpr("ATAN(c_integer)", Math.atan(BeamSqlDslSqlStdOperatorsTest.INTEGER_VALUE)).addExpr("ATAN(c_bigint)", Math.atan(BeamSqlDslSqlStdOperatorsTest.LONG_VALUE)).addExpr("ATAN(c_smallint)", Math.atan(BeamSqlDslSqlStdOperatorsTest.SHORT_VALUE)).addExpr("ATAN(c_tinyint)", Math.atan(BeamSqlDslSqlStdOperatorsTest.BYTE_VALUE)).addExpr("ATAN(c_double)", Math.atan(BeamSqlDslSqlStdOperatorsTest.DOUBLE_VALUE)).addExpr("ATAN(c_float)", Math.atan(BeamSqlDslSqlStdOperatorsTest.FLOAT_VALUE)).addExpr("ATAN(c_decimal)", Math.atan(BeamSqlDslSqlStdOperatorsTest.ONE.doubleValue()));
        checker.buildRunAndCheck();
    }

    @Test
    @BeamSqlDslSqlStdOperatorsTest.SqlOperatorTest(name = "COT", kind = "OTHER_FUNCTION")
    public void testCot() {
        BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker checker = new BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker().addExpr("COT(c_integer)", (1.0 / (Math.tan(BeamSqlDslSqlStdOperatorsTest.INTEGER_VALUE)))).addExpr("COT(c_bigint)", (1.0 / (Math.tan(BeamSqlDslSqlStdOperatorsTest.LONG_VALUE)))).addExpr("COT(c_smallint)", (1.0 / (Math.tan(BeamSqlDslSqlStdOperatorsTest.SHORT_VALUE)))).addExpr("COT(c_tinyint)", (1.0 / (Math.tan(BeamSqlDslSqlStdOperatorsTest.BYTE_VALUE)))).addExpr("COT(c_double)", (1.0 / (Math.tan(BeamSqlDslSqlStdOperatorsTest.DOUBLE_VALUE)))).addExpr("COT(c_float)", (1.0 / (Math.tan(BeamSqlDslSqlStdOperatorsTest.FLOAT_VALUE)))).addExpr("COT(c_decimal)", (1.0 / (Math.tan(BeamSqlDslSqlStdOperatorsTest.ONE.doubleValue()))));
        checker.buildRunAndCheck();
    }

    @Test
    @BeamSqlDslSqlStdOperatorsTest.SqlOperatorTest(name = "DEGREES", kind = "OTHER_FUNCTION")
    public void testDegrees() {
        BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker checker = new BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker().addExpr("DEGREES(c_integer)", Math.toDegrees(BeamSqlDslSqlStdOperatorsTest.INTEGER_VALUE)).addExpr("DEGREES(c_bigint)", Math.toDegrees(BeamSqlDslSqlStdOperatorsTest.LONG_VALUE)).addExpr("DEGREES(c_smallint)", Math.toDegrees(BeamSqlDslSqlStdOperatorsTest.SHORT_VALUE)).addExpr("DEGREES(c_tinyint)", Math.toDegrees(BeamSqlDslSqlStdOperatorsTest.BYTE_VALUE)).addExpr("DEGREES(c_double)", Math.toDegrees(BeamSqlDslSqlStdOperatorsTest.DOUBLE_VALUE)).addExpr("DEGREES(c_float)", Math.toDegrees(BeamSqlDslSqlStdOperatorsTest.FLOAT_VALUE)).addExpr("DEGREES(c_decimal)", Math.toDegrees(BeamSqlDslSqlStdOperatorsTest.ONE.doubleValue()));
        checker.buildRunAndCheck();
    }

    @Test
    @BeamSqlDslSqlStdOperatorsTest.SqlOperatorTest(name = "RADIANS", kind = "OTHER_FUNCTION")
    public void testRadians() {
        BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker checker = new BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker().addExpr("RADIANS(c_integer)", Math.toRadians(BeamSqlDslSqlStdOperatorsTest.INTEGER_VALUE)).addExpr("RADIANS(c_bigint)", Math.toRadians(BeamSqlDslSqlStdOperatorsTest.LONG_VALUE)).addExpr("RADIANS(c_smallint)", Math.toRadians(BeamSqlDslSqlStdOperatorsTest.SHORT_VALUE)).addExpr("RADIANS(c_tinyint)", Math.toRadians(BeamSqlDslSqlStdOperatorsTest.BYTE_VALUE)).addExpr("RADIANS(c_double)", Math.toRadians(BeamSqlDslSqlStdOperatorsTest.DOUBLE_VALUE)).addExpr("RADIANS(c_float)", Math.toRadians(BeamSqlDslSqlStdOperatorsTest.FLOAT_VALUE)).addExpr("RADIANS(c_decimal)", Math.toRadians(BeamSqlDslSqlStdOperatorsTest.ONE.doubleValue()));
        checker.buildRunAndCheck();
    }

    @Test
    @BeamSqlDslSqlStdOperatorsTest.SqlOperatorTest(name = "COS", kind = "OTHER_FUNCTION")
    public void testCos() {
        BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker checker = new BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker().addExpr("COS(c_integer)", Math.cos(BeamSqlDslSqlStdOperatorsTest.INTEGER_VALUE)).addExpr("COS(c_bigint)", Math.cos(BeamSqlDslSqlStdOperatorsTest.LONG_VALUE)).addExpr("COS(c_smallint)", Math.cos(BeamSqlDslSqlStdOperatorsTest.SHORT_VALUE)).addExpr("COS(c_tinyint)", Math.cos(BeamSqlDslSqlStdOperatorsTest.BYTE_VALUE)).addExpr("COS(c_double)", Math.cos(BeamSqlDslSqlStdOperatorsTest.DOUBLE_VALUE)).addExpr("COS(c_float)", Math.cos(BeamSqlDslSqlStdOperatorsTest.FLOAT_VALUE)).addExpr("COS(c_decimal)", Math.cos(BeamSqlDslSqlStdOperatorsTest.ONE.doubleValue()));
        checker.buildRunAndCheck();
    }

    @Test
    @BeamSqlDslSqlStdOperatorsTest.SqlOperatorTest(name = "SIN", kind = "OTHER_FUNCTION")
    public void testSin() {
        BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker checker = new BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker().addExpr("SIN(c_integer)", Math.sin(BeamSqlDslSqlStdOperatorsTest.INTEGER_VALUE)).addExpr("SIN(c_bigint)", Math.sin(BeamSqlDslSqlStdOperatorsTest.LONG_VALUE)).addExpr("SIN(c_smallint)", Math.sin(BeamSqlDslSqlStdOperatorsTest.SHORT_VALUE)).addExpr("SIN(c_tinyint)", Math.sin(BeamSqlDslSqlStdOperatorsTest.BYTE_VALUE)).addExpr("SIN(c_double)", Math.sin(BeamSqlDslSqlStdOperatorsTest.DOUBLE_VALUE)).addExpr("SIN(c_float)", Math.sin(BeamSqlDslSqlStdOperatorsTest.FLOAT_VALUE)).addExpr("SIN(c_decimal)", Math.sin(BeamSqlDslSqlStdOperatorsTest.ONE.doubleValue()));
        checker.buildRunAndCheck();
    }

    @Test
    @BeamSqlDslSqlStdOperatorsTest.SqlOperatorTest(name = "TAN", kind = "OTHER_FUNCTION")
    public void testTan() {
        BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker checker = new BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker().addExpr("TAN(c_integer)", Math.tan(BeamSqlDslSqlStdOperatorsTest.INTEGER_VALUE)).addExpr("TAN(c_bigint)", Math.tan(BeamSqlDslSqlStdOperatorsTest.LONG_VALUE)).addExpr("TAN(c_smallint)", Math.tan(BeamSqlDslSqlStdOperatorsTest.SHORT_VALUE)).addExpr("TAN(c_tinyint)", Math.tan(BeamSqlDslSqlStdOperatorsTest.BYTE_VALUE)).addExpr("TAN(c_double)", Math.tan(BeamSqlDslSqlStdOperatorsTest.DOUBLE_VALUE)).addExpr("TAN(c_float)", Math.tan(BeamSqlDslSqlStdOperatorsTest.FLOAT_VALUE)).addExpr("TAN(c_decimal)", Math.tan(BeamSqlDslSqlStdOperatorsTest.ONE.doubleValue()));
        checker.buildRunAndCheck();
    }

    @Test
    @BeamSqlDslSqlStdOperatorsTest.SqlOperatorTest(name = "SIGN", kind = "OTHER_FUNCTION")
    public void testSign() {
        BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker checker = new BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker().addExpr("SIGN(c_integer)", Integer.signum(BeamSqlDslSqlStdOperatorsTest.INTEGER_VALUE)).addExpr("SIGN(c_bigint)", ((long) (Long.signum(BeamSqlDslSqlStdOperatorsTest.LONG_VALUE)))).addExpr("SIGN(c_smallint)", ((short) (Integer.signum(BeamSqlDslSqlStdOperatorsTest.SHORT_VALUE)))).addExpr("SIGN(c_tinyint)", ((byte) (Integer.signum(BeamSqlDslSqlStdOperatorsTest.BYTE_VALUE)))).addExpr("SIGN(c_double)", Math.signum(BeamSqlDslSqlStdOperatorsTest.DOUBLE_VALUE)).addExpr("SIGN(c_float)", Math.signum(BeamSqlDslSqlStdOperatorsTest.FLOAT_VALUE)).addExpr("SIGN(c_decimal)", BigDecimal.valueOf(BeamSqlDslSqlStdOperatorsTest.ONE.signum()));
        checker.buildRunAndCheck();
    }

    @Test
    @BeamSqlDslSqlStdOperatorsTest.SqlOperatorTest(name = "POWER", kind = "OTHER_FUNCTION")
    public void testPower() {
        BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker checker = new BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker().addExpr("POWER(c_integer, 2)", Math.pow(BeamSqlDslSqlStdOperatorsTest.INTEGER_VALUE, 2)).addExpr("POWER(c_bigint, 2)", Math.pow(BeamSqlDslSqlStdOperatorsTest.LONG_VALUE, 2)).addExpr("POWER(c_smallint, 2)", Math.pow(BeamSqlDslSqlStdOperatorsTest.SHORT_VALUE, 2)).addExpr("POWER(c_tinyint, 2)", Math.pow(BeamSqlDslSqlStdOperatorsTest.BYTE_VALUE, 2)).addExpr("POWER(c_double, 2)", Math.pow(BeamSqlDslSqlStdOperatorsTest.DOUBLE_VALUE, 2)).addExpr("POWER(c_float, 2)", Math.pow(BeamSqlDslSqlStdOperatorsTest.FLOAT_VALUE, 2)).addExpr("POWER(c_decimal, 2)", Math.pow(BeamSqlDslSqlStdOperatorsTest.ONE.doubleValue(), 2));
        checker.buildRunAndCheck();
    }

    @Test
    @BeamSqlDslSqlStdOperatorsTest.SqlOperatorTest(name = "PI", kind = "OTHER_FUNCTION")
    public void testPi() throws Exception {
        BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker checker = new BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker().addExpr("PI", Math.PI);
        checker.buildRunAndCheck();
    }

    @Test
    @BeamSqlDslSqlStdOperatorsTest.SqlOperatorTest(name = "ATAN2", kind = "OTHER_FUNCTION")
    public void testAtan2() {
        BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker checker = new BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker().addExpr("ATAN2(c_integer, 2)", Math.atan2(BeamSqlDslSqlStdOperatorsTest.INTEGER_VALUE, 2)).addExpr("ATAN2(c_bigint, 2)", Math.atan2(BeamSqlDslSqlStdOperatorsTest.LONG_VALUE, 2)).addExpr("ATAN2(c_smallint, 2)", Math.atan2(BeamSqlDslSqlStdOperatorsTest.SHORT_VALUE, 2)).addExpr("ATAN2(c_tinyint, 2)", Math.atan2(BeamSqlDslSqlStdOperatorsTest.BYTE_VALUE, 2)).addExpr("ATAN2(c_double, 2)", Math.atan2(BeamSqlDslSqlStdOperatorsTest.DOUBLE_VALUE, 2)).addExpr("ATAN2(c_float, 2)", Math.atan2(BeamSqlDslSqlStdOperatorsTest.FLOAT_VALUE, 2)).addExpr("ATAN2(c_decimal, 2)", Math.atan2(BeamSqlDslSqlStdOperatorsTest.ONE.doubleValue(), 2));
        checker.buildRunAndCheck();
    }

    @Test
    @BeamSqlDslSqlStdOperatorsTest.SqlOperatorTest(name = "TRUNCATE", kind = "OTHER_FUNCTION")
    public void testTruncate() {
        BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker checker = new BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker().addExpr("TRUNCATE(c_integer, 2)", SqlFunctions.struncate(BeamSqlDslSqlStdOperatorsTest.INTEGER_VALUE, 2)).addExpr("TRUNCATE(c_bigint, 2)", SqlFunctions.struncate(BeamSqlDslSqlStdOperatorsTest.LONG_VALUE, 2)).addExpr("TRUNCATE(c_smallint, 2)", ((short) (SqlFunctions.struncate(BeamSqlDslSqlStdOperatorsTest.SHORT_VALUE, 2)))).addExpr("TRUNCATE(c_tinyint, 2)", ((byte) (SqlFunctions.struncate(BeamSqlDslSqlStdOperatorsTest.BYTE_VALUE, 2)))).addExpr("TRUNCATE(c_double, 2)", SqlFunctions.struncate(BeamSqlDslSqlStdOperatorsTest.DOUBLE_VALUE, 2)).addExpr("TRUNCATE(c_float, 2)", ((float) (SqlFunctions.struncate(BeamSqlDslSqlStdOperatorsTest.FLOAT_VALUE, 2)))).addExpr("TRUNCATE(c_decimal, 2)", SqlFunctions.struncate(BeamSqlDslSqlStdOperatorsTest.ONE, 2));
        checker.buildRunAndCheck();
    }

    @Test
    @BeamSqlDslSqlStdOperatorsTest.SqlOperatorTest(name = "RAND", kind = "OTHER_FUNCTION")
    public void testRand() {
        BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker checker = new BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker().addExpr("RAND(c_integer)", new Random(((BeamSqlDslSqlStdOperatorsTest.INTEGER_VALUE) ^ ((BeamSqlDslSqlStdOperatorsTest.INTEGER_VALUE) << 16))).nextDouble());
        checker.buildRunAndCheck();
    }

    @Test
    @BeamSqlDslSqlStdOperatorsTest.SqlOperatorTest(name = "RAND_INTEGER", kind = "OTHER_FUNCTION")
    public void testRandInteger() {
        BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker checker = new BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker().addExpr("RAND_INTEGER(c_integer, c_integer)", new Random(BeamSqlDslSqlStdOperatorsTest.INTEGER_VALUE).nextInt(BeamSqlDslSqlStdOperatorsTest.INTEGER_VALUE));
        checker.buildRunAndCheck();
    }

    @Test
    @BeamSqlDslSqlStdOperatorsTest.SqlOperatorTest(name = "ARRAY", kind = "ARRAY_VALUE_CONSTRUCTOR")
    @BeamSqlDslSqlStdOperatorsTest.SqlOperatorTest(name = "CARDINALITY", kind = "OTHER_FUNCTION")
    @BeamSqlDslSqlStdOperatorsTest.SqlOperatorTest(name = "ELEMENT", kind = "OTHER_FUNCTION")
    public void testArrayFunctions() {
        BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker checker = // Calcite throws a parse error on this syntax for an empty array
        // .addExpr(
        // "ARRAY []", ImmutableList.of(),
        // Schema.FieldType.array(Schema.FieldType.BOOLEAN))
        new BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker().addExpr("ARRAY ['a', 'b']", ImmutableList.of("a", "b"), FieldType.array(STRING)).addExpr("CARDINALITY(ARRAY ['a', 'b', 'c'])", 3).addExpr("ELEMENT(ARRAY [1])", 1);
        checker.buildRunAndCheck();
    }

    @Test
    @BeamSqlDslSqlStdOperatorsTest.SqlOperatorTest(name = "DAYOFMONTH", kind = "OTHER")
    @BeamSqlDslSqlStdOperatorsTest.SqlOperatorTest(name = "DAYOFWEEK", kind = "OTHER")
    @BeamSqlDslSqlStdOperatorsTest.SqlOperatorTest(name = "DAYOFYEAR", kind = "OTHER")
    @BeamSqlDslSqlStdOperatorsTest.SqlOperatorTest(name = "EXTRACT", kind = "EXTRACT")
    @BeamSqlDslSqlStdOperatorsTest.SqlOperatorTest(name = "YEAR", kind = "OTHER")
    @BeamSqlDslSqlStdOperatorsTest.SqlOperatorTest(name = "QUARTER", kind = "OTHER")
    @BeamSqlDslSqlStdOperatorsTest.SqlOperatorTest(name = "MONTH", kind = "OTHER")
    @BeamSqlDslSqlStdOperatorsTest.SqlOperatorTest(name = "WEEK", kind = "OTHER")
    @BeamSqlDslSqlStdOperatorsTest.SqlOperatorTest(name = "HOUR", kind = "OTHER")
    @BeamSqlDslSqlStdOperatorsTest.SqlOperatorTest(name = "MINUTE", kind = "OTHER")
    @BeamSqlDslSqlStdOperatorsTest.SqlOperatorTest(name = "SECOND", kind = "OTHER")
    public void testBasicDateTimeFunctions() {
        BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker checker = new BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker().addExpr("EXTRACT(YEAR FROM ts)", 1986L).addExpr("YEAR(ts)", 1986L).addExpr("QUARTER(ts)", 1L).addExpr("MONTH(ts)", 2L).addExpr("WEEK(ts)", 7L).addExpr("DAYOFMONTH(ts)", 15L).addExpr("DAYOFYEAR(ts)", 46L).addExpr("DAYOFWEEK(ts)", 7L).addExpr("HOUR(ts)", 11L).addExpr("MINUTE(ts)", 35L).addExpr("SECOND(ts)", 26L);
        checker.buildRunAndCheck();
    }

    // https://issues.apache.org/jira/browse/BEAM-5128
    // @SqlOperatorTest(name = "FLOOR", kind = "FLOOR")
    @Test
    public void testFloor() {
        BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker checker = new BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker().addExpr("FLOOR(ts TO SECOND)", DateTimeUtils.parseTimestampWithUTCTimeZone("1986-02-15 11:35:26")).addExpr("FLOOR(ts TO MINUTE)", DateTimeUtils.parseTimestampWithUTCTimeZone("1986-02-15 11:35:00")).addExpr("FLOOR(ts TO HOUR)", DateTimeUtils.parseTimestampWithUTCTimeZone("1986-02-15 11:00:00")).addExpr("FLOOR(ts TO DAY)", DateTimeUtils.parseTimestampWithUTCTimeZone("1986-02-15 00:00:00")).addExpr("FLOOR(ts TO MONTH)", DateTimeUtils.parseTimestampWithUTCTimeZone("1986-02-01 00:00:00")).addExpr("FLOOR(ts TO YEAR)", DateTimeUtils.parseTimestampWithUTCTimeZone("1986-01-01 00:00:00")).addExpr("FLOOR(c_double)", 1.0);
        checker.buildRunAndCheck(getFloorCeilingTestPCollection());
    }

    // https://issues.apache.org/jira/browse/BEAM-5128
    // @SqlOperatorTest(name = "CEIL", kind = "CEIL")
    @Test
    public void testCeil() {
        BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker checker = new BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker().addExpr("CEIL(ts TO SECOND)", DateTimeUtils.parseTimestampWithUTCTimeZone("1986-02-15 11:35:26")).addExpr("CEIL(ts TO MINUTE)", DateTimeUtils.parseTimestampWithUTCTimeZone("1986-02-15 11:36:00")).addExpr("CEIL(ts TO HOUR)", DateTimeUtils.parseTimestampWithUTCTimeZone("1986-02-15 12:00:00")).addExpr("CEIL(ts TO DAY)", DateTimeUtils.parseTimestampWithUTCTimeZone("1986-02-16 00:00:00")).addExpr("CEIL(ts TO MONTH)", DateTimeUtils.parseTimestampWithUTCTimeZone("1986-03-01 00:00:00")).addExpr("CEIL(ts TO YEAR)", DateTimeUtils.parseTimestampWithUTCTimeZone("1987-01-01 00:00:00")).addExpr("CEIL(c_double)", 2.0);
        checker.buildRunAndCheck(getFloorCeilingTestPCollection());
    }

    @Test
    @BeamSqlDslSqlStdOperatorsTest.SqlOperatorTest(name = "TIMESTAMPADD", kind = "TIMESTAMP_ADD")
    public void testDatetimePlusFunction() {
        BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker checker = new BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker().addExpr("TIMESTAMPADD(SECOND, 3, TIMESTAMP '1984-04-19 01:02:03')", DateTimeUtils.parseTimestampWithUTCTimeZone("1984-04-19 01:02:06")).addExpr("TIMESTAMPADD(MINUTE, 3, TIMESTAMP '1984-04-19 01:02:03')", DateTimeUtils.parseTimestampWithUTCTimeZone("1984-04-19 01:05:03")).addExpr("TIMESTAMPADD(HOUR, 3, TIMESTAMP '1984-04-19 01:02:03')", DateTimeUtils.parseTimestampWithUTCTimeZone("1984-04-19 04:02:03")).addExpr("TIMESTAMPADD(DAY, 3, TIMESTAMP '1984-04-19 01:02:03')", DateTimeUtils.parseTimestampWithUTCTimeZone("1984-04-22 01:02:03")).addExpr("TIMESTAMPADD(MONTH, 2, TIMESTAMP '1984-01-19 01:02:03')", DateTimeUtils.parseTimestampWithUTCTimeZone("1984-03-19 01:02:03")).addExpr("TIMESTAMPADD(YEAR, 2, TIMESTAMP '1985-01-19 01:02:03')", DateTimeUtils.parseTimestampWithUTCTimeZone("1987-01-19 01:02:03"));
        checker.buildRunAndCheck();
    }

    @Test
    @BeamSqlDslSqlStdOperatorsTest.SqlOperatorTest(name = "+", kind = "PLUS")
    public void testDatetimeInfixPlus() {
        BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker checker = new BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker().addExpr("TIMESTAMP '1984-01-19 01:02:03' + INTERVAL '3' SECOND", DateTimeUtils.parseTimestampWithUTCTimeZone("1984-01-19 01:02:06")).addExpr("TIMESTAMP '1984-01-19 01:02:03' + INTERVAL '2' MINUTE", DateTimeUtils.parseTimestampWithUTCTimeZone("1984-01-19 01:04:03")).addExpr("TIMESTAMP '1984-01-19 01:02:03' + INTERVAL '2' HOUR", DateTimeUtils.parseTimestampWithUTCTimeZone("1984-01-19 03:02:03")).addExpr("TIMESTAMP '1984-01-19 01:02:03' + INTERVAL '2' DAY", DateTimeUtils.parseTimestampWithUTCTimeZone("1984-01-21 01:02:03")).addExpr("TIMESTAMP '1984-01-19 01:02:03' + INTERVAL '2' MONTH", DateTimeUtils.parseTimestampWithUTCTimeZone("1984-03-19 01:02:03")).addExpr("TIMESTAMP '1984-01-19 01:02:03' + INTERVAL '2' YEAR", DateTimeUtils.parseTimestampWithUTCTimeZone("1986-01-19 01:02:03")).addExpr("DATE '1984-04-19' + INTERVAL '2' DAY", DateTimeUtils.parseDate("1984-04-21")).addExpr("DATE '1984-04-19' + INTERVAL '1' MONTH", DateTimeUtils.parseDate("1984-05-19")).addExpr("DATE '1984-04-19' + INTERVAL '3' YEAR", DateTimeUtils.parseDate("1987-04-19")).addExpr("TIME '14:28:30' + INTERVAL '15' SECOND", DateTimeUtils.parseTime("14:28:45")).addExpr("TIME '14:28:30.239' + INTERVAL '4' MINUTE", DateTimeUtils.parseTime("14:32:30.239")).addExpr("TIME '14:28:30.2' + INTERVAL '4' HOUR", DateTimeUtils.parseTime("18:28:30.2"));
        checker.buildRunAndCheck();
    }

    @Test
    @BeamSqlDslSqlStdOperatorsTest.SqlOperatorTest(name = "TIMESTAMPDIFF", kind = "TIMESTAMP_DIFF")
    public void testTimestampDiff() {
        BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker checker = new BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker().addExpr(("TIMESTAMPDIFF(SECOND, TIMESTAMP '1984-04-19 01:01:58', " + "TIMESTAMP '1984-04-19 01:01:58')"), 0).addExpr(("TIMESTAMPDIFF(SECOND, TIMESTAMP '1984-04-19 01:01:58', " + "TIMESTAMP '1984-04-19 01:01:59')"), 1).addExpr(("TIMESTAMPDIFF(SECOND, TIMESTAMP '1984-04-19 01:01:58', " + "TIMESTAMP '1984-04-19 01:02:00')"), 2).addExpr(("TIMESTAMPDIFF(MINUTE, TIMESTAMP '1984-04-19 01:01:58', " + "TIMESTAMP '1984-04-19 01:02:57')"), 0).addExpr(("TIMESTAMPDIFF(MINUTE, TIMESTAMP '1984-04-19 01:01:58', " + "TIMESTAMP '1984-04-19 01:02:58')"), 1).addExpr(("TIMESTAMPDIFF(MINUTE, TIMESTAMP '1984-04-19 01:01:58', " + "TIMESTAMP '1984-04-19 01:03:58')"), 2).addExpr(("TIMESTAMPDIFF(HOUR, TIMESTAMP '1984-04-19 01:01:58', " + "TIMESTAMP '1984-04-19 02:01:57')"), 0).addExpr(("TIMESTAMPDIFF(HOUR, TIMESTAMP '1984-04-19 01:01:58', " + "TIMESTAMP '1984-04-19 02:01:58')"), 1).addExpr(("TIMESTAMPDIFF(HOUR, TIMESTAMP '1984-04-19 01:01:58', " + "TIMESTAMP '1984-04-19 03:01:58')"), 2).addExpr(("TIMESTAMPDIFF(DAY, TIMESTAMP '1984-04-19 01:01:58', " + "TIMESTAMP '1984-04-20 01:01:57')"), 0).addExpr(("TIMESTAMPDIFF(DAY, TIMESTAMP '1984-04-19 01:01:58', " + "TIMESTAMP '1984-04-20 01:01:58')"), 1).addExpr(("TIMESTAMPDIFF(DAY, TIMESTAMP '1984-04-19 01:01:58', " + "TIMESTAMP '1984-04-21 01:01:58')"), 2).addExpr(("TIMESTAMPDIFF(MONTH, TIMESTAMP '1984-01-19 01:01:58', " + "TIMESTAMP '1984-02-19 01:01:57')"), 0).addExpr(("TIMESTAMPDIFF(MONTH, TIMESTAMP '1984-01-19 01:01:58', " + "TIMESTAMP '1984-02-19 01:01:58')"), 1).addExpr(("TIMESTAMPDIFF(MONTH, TIMESTAMP '1984-01-19 01:01:58', " + "TIMESTAMP '1984-03-19 01:01:58')"), 2).addExpr(("TIMESTAMPDIFF(YEAR, TIMESTAMP '1981-01-19 01:01:58', " + "TIMESTAMP '1982-01-19 01:01:57')"), 0).addExpr(("TIMESTAMPDIFF(YEAR, TIMESTAMP '1981-01-19 01:01:58', " + "TIMESTAMP '1982-01-19 01:01:58')"), 1).addExpr(("TIMESTAMPDIFF(YEAR, TIMESTAMP '1981-01-19 01:01:58', " + "TIMESTAMP '1983-01-19 01:01:58')"), 2).addExpr(("TIMESTAMPDIFF(YEAR, TIMESTAMP '1981-01-19 01:01:58', " + "TIMESTAMP '1980-01-19 01:01:58')"), (-1)).addExpr(("TIMESTAMPDIFF(YEAR, TIMESTAMP '1981-01-19 01:01:58', " + "TIMESTAMP '1979-01-19 01:01:58')"), (-2));
        checker.buildRunAndCheck();
    }

    // More needed @SqlOperatorTest(name = "-", kind = "MINUS")
    @Test
    public void testTimestampMinusInterval() {
        BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker checker = new BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker().addExpr("TIMESTAMP '1984-04-19 01:01:58' - INTERVAL '2' SECOND", DateTimeUtils.parseTimestampWithUTCTimeZone("1984-04-19 01:01:56")).addExpr("TIMESTAMP '1984-04-19 01:01:58' - INTERVAL '1' MINUTE", DateTimeUtils.parseTimestampWithUTCTimeZone("1984-04-19 01:00:58")).addExpr("TIMESTAMP '1984-04-19 01:01:58' - INTERVAL '4' HOUR", DateTimeUtils.parseTimestampWithUTCTimeZone("1984-04-18 21:01:58")).addExpr("TIMESTAMP '1984-04-19 01:01:58' - INTERVAL '5' DAY", DateTimeUtils.parseTimestampWithUTCTimeZone("1984-04-14 01:01:58")).addExpr("TIMESTAMP '1984-01-19 01:01:58' - INTERVAL '2' MONTH", DateTimeUtils.parseTimestampWithUTCTimeZone("1983-11-19 01:01:58")).addExpr("TIMESTAMP '1984-01-19 01:01:58' - INTERVAL '1' YEAR", DateTimeUtils.parseTimestampWithUTCTimeZone("1983-01-19 01:01:58")).addExpr("DATE '1984-04-19' - INTERVAL '2' DAY", DateTimeUtils.parseDate("1984-04-17")).addExpr("DATE '1984-04-19' - INTERVAL '1' MONTH", DateTimeUtils.parseDate("1984-03-19")).addExpr("DATE '1984-04-19' - INTERVAL '3' YEAR", DateTimeUtils.parseDate("1981-04-19")).addExpr("TIME '14:28:30' - INTERVAL '15' SECOND", DateTimeUtils.parseTime("14:28:15")).addExpr("TIME '14:28:30.239' - INTERVAL '4' MINUTE", DateTimeUtils.parseTime("14:24:30.239")).addExpr("TIME '14:28:30.2' - INTERVAL '4' HOUR", DateTimeUtils.parseTime("10:28:30.2"));
        checker.buildRunAndCheck();
    }

    @Test
    @BeamSqlDslSqlStdOperatorsTest.SqlOperatorTest(name = "CASE", kind = "CASE")
    @BeamSqlDslSqlStdOperatorsTest.SqlOperatorTest(name = "NULLIF", kind = "NULLIF")
    @BeamSqlDslSqlStdOperatorsTest.SqlOperatorTest(name = "COALESCE", kind = "COALESCE")
    public void testConditionalOperatorsAndFunctions() {
        BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker checker = new BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker().addExpr("CASE 1 WHEN 1 THEN 'hello' ELSE 'world' END", "hello").addExpr(("CASE 2 " + (("WHEN 1 THEN 'hello' " + "WHEN 3 THEN 'bond' ") + "ELSE 'world' END")), "world").addExpr(("CASE 3 " + (("WHEN 1 THEN 'hello' " + "WHEN 3 THEN 'bond' ") + "ELSE 'world' END")), "bond").addExpr(("CASE " + ("WHEN 1 = 1 THEN 'hello' " + "ELSE 'world' END")), "hello").addExpr(("CASE " + ("WHEN 1 > 1 THEN 'hello' " + "ELSE 'world' END")), "world").addExpr("NULLIF(5, 4) ", 5).addExpr("NULLIF(4, 5) ", 4).addExpr("NULLIF(5, 5)", null, INT32).addExpr("COALESCE(1, 5) ", 1).addExpr("COALESCE(NULL, 5) ", 5).addExpr("COALESCE(NULL, 4, 5) ", 4).addExpr("COALESCE(NULL, NULL, 5) ", 5).addExpr("COALESCE(5, NULL) ", 5);
        checker.buildRunAndCheck();
    }
}

