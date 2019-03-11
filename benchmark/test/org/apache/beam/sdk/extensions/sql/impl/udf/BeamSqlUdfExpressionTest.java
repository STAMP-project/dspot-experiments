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
package org.apache.beam.sdk.extensions.sql.impl.udf;


import TypeName.BYTES;
import TypeName.DOUBLE;
import TypeName.INT64;
import TypeName.STRING;
import java.nio.charset.StandardCharsets;
import org.apache.beam.sdk.extensions.sql.integrationtest.BeamSqlBuiltinFunctionsIntegrationTestBase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Unit tests for UDFs.
 */
@RunWith(JUnit4.class)
public class BeamSqlUdfExpressionTest extends BeamSqlBuiltinFunctionsIntegrationTestBase {
    @Test
    public void testCOSH() throws Exception {
        BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker checker = new BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker().addExpr("COSH(CAST(1.0 as DOUBLE))", Math.cosh(1.0)).addExpr("COSH(CAST(710.0 as DOUBLE))", Math.cosh(710.0)).addExpr("COSH(CAST(-1.0 as DOUBLE))", Math.cosh((-1.0))).addExprWithNullExpectedValue("COSH(CAST(NULL as DOUBLE))", DOUBLE);
        checker.buildRunAndCheck();
    }

    @Test
    public void testSINH() throws Exception {
        BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker checker = new BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker().addExpr("SINH(CAST(1.0 as DOUBLE))", Math.sinh(1.0)).addExpr("SINH(CAST(710.0 as DOUBLE))", Math.sinh(710.0)).addExpr("SINH(CAST(-1.0 as DOUBLE))", Math.sinh((-1.0))).addExprWithNullExpectedValue("SINH(CAST(NULL as DOUBLE))", DOUBLE);
        checker.buildRunAndCheck();
    }

    @Test
    public void testTANH() throws Exception {
        BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker checker = new BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker().addExpr("TANH(CAST(1.0 as DOUBLE))", Math.tanh(1.0)).addExpr("TANH(CAST(0.0 as DOUBLE))", Math.tanh(0.0)).addExpr("TANH(CAST(-1.0 as DOUBLE))", Math.tanh((-1.0))).addExprWithNullExpectedValue("TANH(CAST(NULL as DOUBLE))", DOUBLE);
        checker.buildRunAndCheck();
    }

    @Test
    public void testEndsWith() throws Exception {
        BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker checker = new BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker().addExpr("ENDS_WITH('string1', 'g1')", true).addExpr("ENDS_WITH('string2', 'g1')", false).addExpr("ENDS_WITH('', '')", true).addExpr("ENDS_WITH('??', '?')", true).addExpr("ENDS_WITH('??', '?')", false);
        checker.buildRunAndCheck();
    }

    @Test
    public void testStartsWith() throws Exception {
        BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker checker = new BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker().addExpr("STARTS_WITH('string1', 'stri')", true).addExpr("STARTS_WITH('string2', 'str1')", false).addExpr("STARTS_WITH('', '')", true).addExpr("STARTS_WITH('??', '?')", false).addExpr("STARTS_WITH('??', '?')", true);
        checker.buildRunAndCheck();
    }

    @Test
    public void testLength() throws Exception {
        BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker checker = new BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker().addExpr("LENGTH('')", 0L).addExpr("LENGTH('abcde')", 5L).addExpr("LENGTH('??')", 2L).addExpr("LENGTH(\'\u0000\u0000\')", 2L).addExpr("LENGTH('?????')", 5L).addExprWithNullExpectedValue("LENGTH(CAST(NULL as VARCHAR(0)))", INT64).addExprWithNullExpectedValue("LENGTH(CAST(NULL as VARBINARY(0)))", INT64);
        checker.buildRunAndCheck();
    }

    @Test
    public void testReverse() throws Exception {
        BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker checker = new BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker().addExpr("REVERSE('')", "").addExpr("REVERSE('foo')", "oof").addExpr("REVERSE('??')", "??").addExpr("REVERSE('?????')", "?????").addExprWithNullExpectedValue("REVERSE(CAST(NULL as VARCHAR(0)))", STRING).addExprWithNullExpectedValue("REVERSE(CAST(NULL as VARBINARY(0)))", STRING);
        checker.buildRunAndCheck();
    }

    @Test
    public void testFromHex() throws Exception {
        BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker checker = new BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker().addExpr("FROM_HEX('666f6f626172')", "foobar".getBytes(StandardCharsets.UTF_8)).addExpr("FROM_HEX('20')", " ".getBytes(StandardCharsets.UTF_8)).addExpr("FROM_HEX('616263414243')", "abcABC".getBytes(StandardCharsets.UTF_8)).addExpr("FROM_HEX('616263414243d0b6d189d184d096d0a9d0a4')", "abcABC??????".getBytes(StandardCharsets.UTF_8)).addExprWithNullExpectedValue("FROM_HEX(CAST(NULL as VARCHAR(0)))", BYTES);
        checker.buildRunAndCheck();
    }

    @Test
    public void testToHex() throws Exception {
        BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker checker = new BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker().addExprWithNullExpectedValue("TO_HEX(CAST(NULL as VARBINARY(0)))", STRING);
        checker.buildRunAndCheck();
    }

    @Test
    public void testLeftPad() throws Exception {
        BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker checker = new BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker().addExpr("LPAD('abcdef', CAST(0 AS BIGINT))", "").addExpr("LPAD('abcdef', CAST(0 AS BIGINT), 'defgh')", "").addExpr("LPAD('abcdef', CAST(6 AS BIGINT), 'defgh')", "abcdef").addExpr("LPAD('abcdef', CAST(5 AS BIGINT), 'defgh')", "abcde").addExpr("LPAD('abcdef', CAST(4 AS BIGINT), 'defgh')", "abcd").addExpr("LPAD('abcdef', CAST(3 AS BIGINT), 'defgh')", "abc").addExpr("LPAD('abc', CAST(4 AS BIGINT), 'defg')", "dabc").addExpr("LPAD('abc', CAST(5 AS BIGINT), 'defgh')", "deabc").addExpr("LPAD('abc', CAST(6 AS BIGINT), 'defgh')", "defabc").addExpr("LPAD('abc', CAST(7 AS BIGINT), 'defg')", "defgabc").addExpr("LPAD('abcd', CAST(10 AS BIGINT), 'defg')", "defgdeabcd").addExpr("LPAD('??', CAST(10 AS BIGINT), '??????')", "??????????").addExpr("LPAD('', CAST(5 AS BIGINT), ' ')", "     ").addExpr("LPAD('', CAST(3 AS BIGINT), '-')", "---").addExpr("LPAD('a', CAST(5 AS BIGINT), ' ')", "    a").addExpr("LPAD('a', CAST(3 AS BIGINT), '-')", "--a").addExprWithNullExpectedValue("LPAD(CAST(NULL AS VARCHAR(0)), CAST(3 AS BIGINT), '-')", STRING).addExprWithNullExpectedValue("LPAD('', CAST(NULL AS BIGINT), '-')", STRING).addExprWithNullExpectedValue("LPAD('', CAST(3 AS BIGINT), CAST(NULL AS VARCHAR(0)))", STRING);
        checker.buildRunAndCheck();
    }

    @Test
    public void testRightPad() throws Exception {
        BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker checker = new BeamSqlBuiltinFunctionsIntegrationTestBase.ExpressionChecker().addExpr("RPAD('abcdef', CAST(0 AS BIGINT))", "").addExpr("RPAD('abcdef', CAST(0 AS BIGINT), 'defgh')", "").addExpr("RPAD('abcdef', CAST(6 AS BIGINT), 'defgh')", "abcdef").addExpr("RPAD('abcdef', CAST(5 AS BIGINT), 'defgh')", "abcde").addExpr("RPAD('abcdef', CAST(4 AS BIGINT), 'defgh')", "abcd").addExpr("RPAD('abcdef', CAST(3 AS BIGINT), 'defgh')", "abc").addExpr("RPAD('abc', CAST(4 AS BIGINT), 'defg')", "abcd").addExpr("RPAD('abc', CAST(5 AS BIGINT), 'defgh')", "abcde").addExpr("RPAD('abc', CAST(6 AS BIGINT), 'defgh')", "abcdef").addExpr("RPAD('abc', CAST(7 AS BIGINT), 'defg')", "abcdefg").addExpr("RPAD('abcd', CAST(10 AS BIGINT), 'defg')", "abcddefgde").addExpr("RPAD('??', CAST(10 AS BIGINT), '??????')", "??????????").addExpr("RPAD('', CAST(5 AS BIGINT), ' ')", "     ").addExpr("RPAD('', CAST(3 AS BIGINT), '-')", "---").addExpr("RPAD('a', CAST(5 AS BIGINT), ' ')", "a    ").addExpr("RPAD('a', CAST(3 AS BIGINT), '-')", "a--").addExprWithNullExpectedValue("RPAD(CAST(NULL AS VARCHAR(0)), CAST(3 AS BIGINT), '-')", STRING).addExprWithNullExpectedValue("RPAD('', CAST(NULL AS BIGINT), '-')", STRING).addExprWithNullExpectedValue("RPAD('', CAST(3 AS BIGINT), CAST(NULL AS VARCHAR(0)))", STRING);
        checker.buildRunAndCheck();
    }
}

