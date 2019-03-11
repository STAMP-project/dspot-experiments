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
package org.apache.beam.sdk.extensions.sql.integrationtest;


import org.junit.Test;


/**
 * Integration test for comparison operators.
 */
public class BeamSqlComparisonOperatorsIntegrationTest extends BeamSqlBuiltinFunctionsIntegrationTestBase {
    @Test
    public void testEquals() {
        BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker checker = new BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker().addExpr("c_tinyint_1 = c_tinyint_1", true).addExpr("c_tinyint_1 = c_tinyint_2", false).addExpr("c_smallint_1 = c_smallint_1", true).addExpr("c_smallint_1 = c_smallint_2", false).addExpr("c_integer_1 = c_integer_1", true).addExpr("c_integer_1 = c_integer_2", false).addExpr("c_bigint_1 = c_bigint_1", true).addExpr("c_bigint_1 = c_bigint_2", false).addExpr("c_float_1 = c_float_1", true).addExpr("c_float_1 = c_float_2", false).addExpr("c_double_1 = c_double_1", true).addExpr("c_double_1 = c_double_2", false).addExpr("c_decimal_1 = c_decimal_1", true).addExpr("c_decimal_1 = c_decimal_2", false).addExpr("c_varchar_1 = c_varchar_1", true).addExpr("c_varchar_1 = c_varchar_2", false).addExpr("c_boolean_true = c_boolean_true", true).addExpr("c_boolean_true = c_boolean_false", false);
        checker.buildRunAndCheck();
    }

    @Test
    public void testNotEquals() {
        BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker checker = new BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker().addExpr("c_tinyint_1 <> c_tinyint_1", false).addExpr("c_tinyint_1 <> c_tinyint_2", true).addExpr("c_smallint_1 <> c_smallint_1", false).addExpr("c_smallint_1 <> c_smallint_2", true).addExpr("c_integer_1 <> c_integer_1", false).addExpr("c_integer_1 <> c_integer_2", true).addExpr("c_bigint_1 <> c_bigint_1", false).addExpr("c_bigint_1 <> c_bigint_2", true).addExpr("c_float_1 <> c_float_1", false).addExpr("c_float_1 <> c_float_2", true).addExpr("c_double_1 <> c_double_1", false).addExpr("c_double_1 <> c_double_2", true).addExpr("c_decimal_1 <> c_decimal_1", false).addExpr("c_decimal_1 <> c_decimal_2", true).addExpr("c_varchar_1 <> c_varchar_1", false).addExpr("c_varchar_1 <> c_varchar_2", true).addExpr("c_boolean_true <> c_boolean_true", false).addExpr("c_boolean_true <> c_boolean_false", true);
        checker.buildRunAndCheck();
    }

    @Test
    public void testGreaterThan() {
        BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker checker = new BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker().addExpr("c_tinyint_2 > c_tinyint_1", true).addExpr("c_tinyint_1 > c_tinyint_1", false).addExpr("c_tinyint_1 > c_tinyint_2", false).addExpr("c_smallint_2 > c_smallint_1", true).addExpr("c_smallint_1 > c_smallint_1", false).addExpr("c_smallint_1 > c_smallint_2", false).addExpr("c_integer_2 > c_integer_1", true).addExpr("c_integer_1 > c_integer_1", false).addExpr("c_integer_1 > c_integer_2", false).addExpr("c_bigint_2 > c_bigint_1", true).addExpr("c_bigint_1 > c_bigint_1", false).addExpr("c_bigint_1 > c_bigint_2", false).addExpr("c_float_2 > c_float_1", true).addExpr("c_float_1 > c_float_1", false).addExpr("c_float_1 > c_float_2", false).addExpr("c_double_2 > c_double_1", true).addExpr("c_double_1 > c_double_1", false).addExpr("c_double_1 > c_double_2", false).addExpr("c_decimal_2 > c_decimal_1", true).addExpr("c_decimal_1 > c_decimal_1", false).addExpr("c_decimal_1 > c_decimal_2", false).addExpr("c_varchar_2 > c_varchar_1", true).addExpr("c_varchar_1 > c_varchar_1", false).addExpr("c_varchar_1 > c_varchar_2", false).addExpr("c_boolean_false > c_boolean_true", false).addExpr("c_boolean_true > c_boolean_false", true);
        checker.buildRunAndCheck();
    }

    @Test
    public void testGreaterThanOrEquals() {
        BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker checker = new BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker().addExpr("c_tinyint_2 >= c_tinyint_1", true).addExpr("c_tinyint_1 >= c_tinyint_1", true).addExpr("c_tinyint_1 >= c_tinyint_2", false).addExpr("c_smallint_2 >= c_smallint_1", true).addExpr("c_smallint_1 >= c_smallint_1", true).addExpr("c_smallint_1 >= c_smallint_2", false).addExpr("c_integer_2 >= c_integer_1", true).addExpr("c_integer_1 >= c_integer_1", true).addExpr("c_integer_1 >= c_integer_2", false).addExpr("c_bigint_2 >= c_bigint_1", true).addExpr("c_bigint_1 >= c_bigint_1", true).addExpr("c_bigint_1 >= c_bigint_2", false).addExpr("c_float_2 >= c_float_1", true).addExpr("c_float_1 >= c_float_1", true).addExpr("c_float_1 >= c_float_2", false).addExpr("c_double_2 >= c_double_1", true).addExpr("c_double_1 >= c_double_1", true).addExpr("c_double_1 >= c_double_2", false).addExpr("c_decimal_2 >= c_decimal_1", true).addExpr("c_decimal_1 >= c_decimal_1", true).addExpr("c_decimal_1 >= c_decimal_2", false).addExpr("c_varchar_2 >= c_varchar_1", true).addExpr("c_varchar_1 >= c_varchar_1", true).addExpr("c_varchar_1 >= c_varchar_2", false).addExpr("c_boolean_false >= c_boolean_true", false).addExpr("c_boolean_true >= c_boolean_false", true);
        checker.buildRunAndCheck();
    }

    @Test
    public void testLessThan() {
        BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker checker = new BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker().addExpr("c_tinyint_2 < c_tinyint_1", false).addExpr("c_tinyint_1 < c_tinyint_1", false).addExpr("c_tinyint_1 < c_tinyint_2", true).addExpr("c_smallint_2 < c_smallint_1", false).addExpr("c_smallint_1 < c_smallint_1", false).addExpr("c_smallint_1 < c_smallint_2", true).addExpr("c_integer_2 < c_integer_1", false).addExpr("c_integer_1 < c_integer_1", false).addExpr("c_integer_1 < c_integer_2", true).addExpr("c_bigint_2 < c_bigint_1", false).addExpr("c_bigint_1 < c_bigint_1", false).addExpr("c_bigint_1 < c_bigint_2", true).addExpr("c_float_2 < c_float_1", false).addExpr("c_float_1 < c_float_1", false).addExpr("c_float_1 < c_float_2", true).addExpr("c_double_2 < c_double_1", false).addExpr("c_double_1 < c_double_1", false).addExpr("c_double_1 < c_double_2", true).addExpr("c_decimal_2 < c_decimal_1", false).addExpr("c_decimal_1 < c_decimal_1", false).addExpr("c_decimal_1 < c_decimal_2", true).addExpr("c_varchar_2 < c_varchar_1", false).addExpr("c_varchar_1 < c_varchar_1", false).addExpr("c_varchar_1 < c_varchar_2", true).addExpr("c_boolean_false < c_boolean_true", true).addExpr("c_boolean_true < c_boolean_false", false);
        checker.buildRunAndCheck();
    }

    @Test
    public void testLessThanOrEquals() {
        BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker checker = new BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker().addExpr("c_tinyint_2 <= c_tinyint_1", false).addExpr("c_tinyint_1 <= c_tinyint_1", true).addExpr("c_tinyint_1 <= c_tinyint_2", true).addExpr("c_smallint_2 <= c_smallint_1", false).addExpr("c_smallint_1 <= c_smallint_1", true).addExpr("c_smallint_1 <= c_smallint_2", true).addExpr("c_integer_2 <= c_integer_1", false).addExpr("c_integer_1 <= c_integer_1", true).addExpr("c_integer_1 <= c_integer_2", true).addExpr("c_bigint_2 <= c_bigint_1", false).addExpr("c_bigint_1 <= c_bigint_1", true).addExpr("c_bigint_1 <= c_bigint_2", true).addExpr("c_float_2 <= c_float_1", false).addExpr("c_float_1 <= c_float_1", true).addExpr("c_float_1 <= c_float_2", true).addExpr("c_double_2 <= c_double_1", false).addExpr("c_double_1 <= c_double_1", true).addExpr("c_double_1 <= c_double_2", true).addExpr("c_decimal_2 <= c_decimal_1", false).addExpr("c_decimal_1 <= c_decimal_1", true).addExpr("c_decimal_1 <= c_decimal_2", true).addExpr("c_varchar_2 <= c_varchar_1", false).addExpr("c_varchar_1 <= c_varchar_1", true).addExpr("c_varchar_1 <= c_varchar_2", true).addExpr("c_boolean_false <= c_boolean_true", true).addExpr("c_boolean_true <= c_boolean_false", false);
        checker.buildRunAndCheck();
    }

    @Test
    public void testIsNullAndIsNotNull() throws Exception {
        BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker checker = new BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker().addExpr("1 IS NOT NULL", true).addExpr("NULL IS NOT NULL", false).addExpr("1 IS NULL", false).addExpr("NULL IS NULL", true);
        checker.buildRunAndCheck();
    }

    @Test
    public void testLike() throws Exception {
        BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker checker = new BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker().addExpr("s_string_1 LIKE 'string_true_test'", true).addExpr("s_string_1 LIKE 'string_false_test'", false).addExpr("s_string_2 LIKE 'string_false_test'", true).addExpr("s_string_2 LIKE 'string_true_test'", false).addExpr("s_string_1 LIKE 'string_true_test%'", true).addExpr("s_string_1 LIKE 'string_false_test%'", false).addExpr("s_string_1 LIKE 'string_true%'", true).addExpr("s_string_1 LIKE 'string_false%'", false).addExpr("s_string_1 LIKE 'string%test'", true).addExpr("s_string_1 LIKE '%test'", true).addExpr("s_string_1 LIKE '%string_true_test'", true).addExpr("s_string_1 LIKE '%string_false_test'", false).addExpr("s_string_1 LIKE '%false_test'", false).addExpr("s_string_2 LIKE '%false_test'", true).addExpr("s_string_1 LIKE 'string_tr_e_test'", true).addExpr("s_string_1 LIKE 'string______test'", true).addExpr("s_string_2 LIKE 'string______test'", false).addExpr("s_string_2 LIKE 'string_______test'", true).addExpr("s_string_2 LIKE 'string_false_te__'", true).addExpr("s_string_2 LIKE 'string_false_te___'", false).addExpr("s_string_2 LIKE 'string_false_te_'", false).addExpr("s_string_1 LIKE 'string_true_te__'", true).addExpr("s_string_1 LIKE '_ring_true_te__'", false).addExpr("s_string_2 LIKE '__ring_false_te__'", true).addExpr("s_string_1 LIKE '_%ring_true_te__'", true).addExpr("s_string_1 LIKE '_%tring_true_te__'", true).addExpr("s_string_2 LIKE 'string_false_te%__'", true).addExpr("s_string_2 LIKE 'string_false_te__%'", true).addExpr("s_string_2 LIKE 'string_false_t%__'", true).addExpr("s_string_2 LIKE 'string_false_t__%'", true).addExpr("s_string_2 LIKE 'string_false_te_%'", true).addExpr("s_string_2 LIKE 'string_false_te%_'", true).addExpr("s_string_1 LIKE 'string_%test'", true).addExpr("s_string_1 LIKE 'string%_test'", true).addExpr("s_string_1 LIKE 'string_%_test'", true);
        checker.buildRunAndCheck();
    }
}

