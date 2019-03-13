/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.drill.exec.fn.interp;


import BitControl.PlanFragment;
import TypeProtos.MajorType;
import TypeProtos.MinorType.INT;
import TypeProtos.MinorType.VARCHAR;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import org.apache.drill.categories.SlowTest;
import org.apache.drill.categories.SqlTest;
import org.apache.drill.common.types.TypeProtos;
import org.apache.drill.common.types.Types;
import org.apache.drill.exec.expr.holders.TimeStampHolder;
import org.apache.drill.exec.pop.PopUnitTestBase;
import org.apache.drill.exec.proto.BitControl;
import org.apache.drill.exec.proto.BitControl.QueryContextInformation;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ SlowTest.class, SqlTest.class })
public class ExpressionInterpreterTest extends PopUnitTestBase {
    private static final Logger logger = LoggerFactory.getLogger(ExpressionInterpreterTest.class);

    @Test
    public void interpreterNullableStrExpr() throws Exception {
        final String[] colNames = new String[]{ "col1" };
        final TypeProtos[] colTypes = new MajorType[]{ Types.optional(VARCHAR) };
        final String expressionStr = "substr(col1, 1, 3)";
        final String[] expectedFirstTwoValues = new String[]{ "aaa", "null" };
        doTest(expressionStr, colNames, colTypes, expectedFirstTwoValues);
    }

    @Test
    public void interpreterNullableBooleanExpr() throws Exception {
        final String[] colNames = new String[]{ "col1" };
        final TypeProtos[] colTypes = new MajorType[]{ Types.optional(VARCHAR) };
        final String expressionStr = "col1 < 'abc' and col1 > 'abc'";
        final String[] expectedFirstTwoValues = new String[]{ "false", "null" };
        doTest(expressionStr, colNames, colTypes, expectedFirstTwoValues);
    }

    @Test
    public void interpreterNullableIntegerExpr() throws Exception {
        final String[] colNames = new String[]{ "col1" };
        final TypeProtos[] colTypes = new MajorType[]{ Types.optional(INT) };
        final String expressionStr = "col1 + 100 - 1 * 2 + 2";
        final String[] expectedFirstTwoValues = new String[]{ "-2147483548", "null" };
        doTest(expressionStr, colNames, colTypes, expectedFirstTwoValues);
    }

    @Test
    public void interpreterLikeExpr() throws Exception {
        final String[] colNames = new String[]{ "col1" };
        final TypeProtos[] colTypes = new MajorType[]{ Types.optional(VARCHAR) };
        final String expressionStr = "like(col1, 'aaa%')";
        final String[] expectedFirstTwoValues = new String[]{ "true", "null" };
        doTest(expressionStr, colNames, colTypes, expectedFirstTwoValues);
    }

    @Test
    public void interpreterCastExpr() throws Exception {
        final String[] colNames = new String[]{ "col1" };
        final TypeProtos[] colTypes = new MajorType[]{ Types.optional(VARCHAR) };
        final String expressionStr = "cast(3+4 as float8)";
        final String[] expectedFirstTwoValues = new String[]{ "7.0", "7.0" };
        doTest(expressionStr, colNames, colTypes, expectedFirstTwoValues);
    }

    @Test
    public void interpreterCaseExpr() throws Exception {
        final String[] colNames = new String[]{ "col1" };
        final TypeProtos[] colTypes = new MajorType[]{ Types.optional(VARCHAR) };
        final String expressionStr = "case when substr(col1, 1, 3)='aaa' then 'ABC' else 'XYZ' end";
        final String[] expectedFirstTwoValues = new String[]{ "ABC", "XYZ" };
        doTest(expressionStr, colNames, colTypes, expectedFirstTwoValues);
    }

    @Test
    public void interpreterDateTest() throws Exception {
        final String[] colNames = new String[]{ "col1" };
        final TypeProtos[] colTypes = new MajorType[]{ Types.optional(INT) };
        final String expressionStr = "now()";
        final BitControl.PlanFragment planFragment = PlanFragment.getDefaultInstance();
        final QueryContextInformation queryContextInfo = planFragment.getContext();
        final int timeZoneIndex = queryContextInfo.getTimeZone();
        final DateTimeZone timeZone = DateTimeZone.forID(org.apache.drill.exec.expr.fn.impl.DateUtility.getTimeZone(timeZoneIndex));
        final DateTime now = new DateTime(queryContextInfo.getQueryStartTime(), timeZone);
        final long queryStartDate = now.getMillis();
        final TimeStampHolder out = new TimeStampHolder();
        out.value = queryStartDate;
        final ByteBuffer buffer = ByteBuffer.allocate(12);
        buffer.putLong(out.value);
        final long l = buffer.getLong(0);
        final LocalDateTime t = Instant.ofEpochMilli(l).atZone(ZoneOffset.systemDefault()).toLocalDateTime();
        final String[] expectedFirstTwoValues = new String[]{ t.toString(), t.toString() };
        doTest(expressionStr, colNames, colTypes, expectedFirstTwoValues, planFragment);
    }
}

