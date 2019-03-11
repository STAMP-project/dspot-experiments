/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.druid.sql.calcite.expression;


import SqlStdOperatorTable.CEIL;
import SqlStdOperatorTable.CHARACTER_LENGTH;
import SqlStdOperatorTable.CONCAT;
import SqlStdOperatorTable.DATETIME_PLUS;
import SqlStdOperatorTable.EXTRACT;
import SqlStdOperatorTable.FLOOR;
import SqlStdOperatorTable.MINUS_DATE;
import SqlStdOperatorTable.POSITION;
import SqlStdOperatorTable.POWER;
import SqlStdOperatorTable.TRIM;
import SqlTrimFunction.Flag.BOTH;
import SqlTrimFunction.Flag.LEADING;
import SqlTrimFunction.Flag.TRAILING;
import SqlTypeName.BIGINT;
import SqlTypeName.DATE;
import SqlTypeName.INTEGER;
import SqlTypeName.TIMESTAMP;
import SqlTypeName.VARCHAR;
import TimeUnitRange.DAY;
import TimeUnitRange.QUARTER;
import TimeUnitRange.YEAR;
import ValueType.FLOAT;
import ValueType.LONG;
import ValueType.STRING;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.math.BigDecimal;
import java.util.Map;
import org.apache.calcite.avatica.util.TimeUnit;
import org.apache.calcite.jdbc.JavaTypeFactoryImpl;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.rex.RexBuilder;
import org.apache.calcite.sql.SqlFunction;
import org.apache.calcite.sql.parser.SqlParserPos;
import org.apache.druid.common.config.NullHandling;
import org.apache.druid.java.util.common.DateTimes;
import org.apache.druid.query.extraction.RegexDimExtractionFn;
import org.apache.druid.sql.calcite.expression.builtin.DateTruncOperatorConversion;
import org.apache.druid.sql.calcite.expression.builtin.RegexpExtractOperatorConversion;
import org.apache.druid.sql.calcite.expression.builtin.StrposOperatorConversion;
import org.apache.druid.sql.calcite.expression.builtin.TimeExtractOperatorConversion;
import org.apache.druid.sql.calcite.expression.builtin.TimeFloorOperatorConversion;
import org.apache.druid.sql.calcite.expression.builtin.TimeFormatOperatorConversion;
import org.apache.druid.sql.calcite.expression.builtin.TimeParseOperatorConversion;
import org.apache.druid.sql.calcite.expression.builtin.TimeShiftOperatorConversion;
import org.apache.druid.sql.calcite.expression.builtin.TruncateOperatorConversion;
import org.apache.druid.sql.calcite.planner.PlannerConfig;
import org.apache.druid.sql.calcite.planner.PlannerContext;
import org.apache.druid.sql.calcite.table.RowSignature;
import org.apache.druid.sql.calcite.util.CalciteTestBase;
import org.apache.druid.sql.calcite.util.CalciteTests;
import org.joda.time.Period;
import org.junit.Test;


public class ExpressionsTest extends CalciteTestBase {
    private final PlannerContext plannerContext = PlannerContext.create(CalciteTests.createOperatorTable(), CalciteTests.createExprMacroTable(), new PlannerConfig(), ImmutableMap.of(), CalciteTests.REGULAR_USER_AUTH_RESULT);

    private final RowSignature rowSignature = RowSignature.builder().add("t", LONG).add("a", LONG).add("b", LONG).add("x", FLOAT).add("y", LONG).add("z", FLOAT).add("s", STRING).add("spacey", STRING).add("tstr", STRING).add("dstr", STRING).build();

    private final Map<String, Object> bindings = ImmutableMap.<String, Object>builder().put("t", DateTimes.of("2000-02-03T04:05:06").getMillis()).put("a", 10).put("b", 25).put("x", 2.25).put("y", 3.0).put("z", (-2.25)).put("s", "foo").put("spacey", "  hey there  ").put("tstr", "2000-02-03 04:05:06").put("dstr", "2000-02-03").build();

    private final RelDataTypeFactory typeFactory = new JavaTypeFactoryImpl();

    private final RexBuilder rexBuilder = new RexBuilder(typeFactory);

    private final RelDataType relDataType = rowSignature.getRelDataType(typeFactory);

    @Test
    public void testConcat() {
        testExpression(rexBuilder.makeCall(typeFactory.createSqlType(VARCHAR), CONCAT, ImmutableList.of(inputRef("s"), rexBuilder.makeLiteral("bar"))), DruidExpression.fromExpression("concat(\"s\",\'bar\')"), "foobar");
    }

    @Test
    public void testCharacterLength() {
        testExpression(rexBuilder.makeCall(CHARACTER_LENGTH, inputRef("s")), DruidExpression.fromExpression("strlen(\"s\")"), 3L);
    }

    @Test
    public void testRegexpExtract() {
        testExpression(rexBuilder.makeCall(new RegexpExtractOperatorConversion().calciteOperator(), inputRef("s"), rexBuilder.makeLiteral("f(.)"), integerLiteral(1)), DruidExpression.of(SimpleExtraction.of("s", new RegexDimExtractionFn("f(.)", 1, true, null)), "regexp_extract(\"s\",\'f(.)\',1)"), "o");
        testExpression(rexBuilder.makeCall(new RegexpExtractOperatorConversion().calciteOperator(), inputRef("s"), rexBuilder.makeLiteral("f(.)")), DruidExpression.of(SimpleExtraction.of("s", new RegexDimExtractionFn("f(.)", 0, true, null)), "regexp_extract(\"s\",\'f(.)\')"), "fo");
    }

    @Test
    public void testStrpos() {
        testExpression(rexBuilder.makeCall(new StrposOperatorConversion().calciteOperator(), inputRef("s"), rexBuilder.makeLiteral("oo")), DruidExpression.fromExpression("(strpos(\"s\",\'oo\') + 1)"), 2L);
        testExpression(rexBuilder.makeCall(new StrposOperatorConversion().calciteOperator(), inputRef("s"), rexBuilder.makeLiteral("ax")), DruidExpression.fromExpression("(strpos(\"s\",\'ax\') + 1)"), 0L);
        testExpression(rexBuilder.makeCall(new StrposOperatorConversion().calciteOperator(), rexBuilder.makeNullLiteral(typeFactory.createSqlType(VARCHAR)), rexBuilder.makeLiteral("ax")), DruidExpression.fromExpression("(strpos(null,'ax') + 1)"), (NullHandling.replaceWithDefault() ? 0L : null));
    }

    @Test
    public void testPosition() {
        testExpression(rexBuilder.makeCall(POSITION, rexBuilder.makeLiteral("oo"), inputRef("s")), DruidExpression.fromExpression("(strpos(\"s\",\'oo\',0) + 1)"), 2L);
        testExpression(rexBuilder.makeCall(POSITION, rexBuilder.makeLiteral("oo"), inputRef("s"), rexBuilder.makeExactLiteral(BigDecimal.valueOf(2))), DruidExpression.fromExpression("(strpos(\"s\",\'oo\',(2 - 1)) + 1)"), 2L);
        testExpression(rexBuilder.makeCall(POSITION, rexBuilder.makeLiteral("oo"), inputRef("s"), rexBuilder.makeExactLiteral(BigDecimal.valueOf(3))), DruidExpression.fromExpression("(strpos(\"s\",\'oo\',(3 - 1)) + 1)"), 0L);
    }

    @Test
    public void testPower() {
        testExpression(rexBuilder.makeCall(POWER, inputRef("a"), integerLiteral(2)), DruidExpression.fromExpression("pow(\"a\",2)"), 100.0);
    }

    @Test
    public void testFloor() {
        testExpression(rexBuilder.makeCall(FLOOR, inputRef("a")), DruidExpression.fromExpression("floor(\"a\")"), 10.0);
        testExpression(rexBuilder.makeCall(FLOOR, inputRef("x")), DruidExpression.fromExpression("floor(\"x\")"), 2.0);
        testExpression(rexBuilder.makeCall(FLOOR, inputRef("y")), DruidExpression.fromExpression("floor(\"y\")"), 3.0);
        testExpression(rexBuilder.makeCall(FLOOR, inputRef("z")), DruidExpression.fromExpression("floor(\"z\")"), (-3.0));
    }

    @Test
    public void testCeil() {
        testExpression(rexBuilder.makeCall(CEIL, inputRef("a")), DruidExpression.fromExpression("ceil(\"a\")"), 10.0);
        testExpression(rexBuilder.makeCall(CEIL, inputRef("x")), DruidExpression.fromExpression("ceil(\"x\")"), 3.0);
        testExpression(rexBuilder.makeCall(CEIL, inputRef("y")), DruidExpression.fromExpression("ceil(\"y\")"), 3.0);
        testExpression(rexBuilder.makeCall(CEIL, inputRef("z")), DruidExpression.fromExpression("ceil(\"z\")"), (-2.0));
    }

    @Test
    public void testTruncate() {
        final SqlFunction truncateFunction = new TruncateOperatorConversion().calciteOperator();
        testExpression(rexBuilder.makeCall(truncateFunction, inputRef("a")), DruidExpression.fromExpression("(cast(cast(\"a\" * 1,\'long\'),\'double\') / 1)"), 10.0);
        testExpression(rexBuilder.makeCall(truncateFunction, inputRef("x")), DruidExpression.fromExpression("(cast(cast(\"x\" * 1,\'long\'),\'double\') / 1)"), 2.0);
        testExpression(rexBuilder.makeCall(truncateFunction, inputRef("y")), DruidExpression.fromExpression("(cast(cast(\"y\" * 1,\'long\'),\'double\') / 1)"), 3.0);
        testExpression(rexBuilder.makeCall(truncateFunction, inputRef("z")), DruidExpression.fromExpression("(cast(cast(\"z\" * 1,\'long\'),\'double\') / 1)"), (-2.0));
        testExpression(rexBuilder.makeCall(truncateFunction, inputRef("x"), integerLiteral(1)), DruidExpression.fromExpression("(cast(cast(\"x\" * 10.0,\'long\'),\'double\') / 10.0)"), 2.2);
        testExpression(rexBuilder.makeCall(truncateFunction, inputRef("z"), integerLiteral(1)), DruidExpression.fromExpression("(cast(cast(\"z\" * 10.0,\'long\'),\'double\') / 10.0)"), (-2.2));
        testExpression(rexBuilder.makeCall(truncateFunction, inputRef("b"), integerLiteral((-1))), DruidExpression.fromExpression("(cast(cast(\"b\" * 0.1,\'long\'),\'double\') / 0.1)"), 20.0);
        testExpression(rexBuilder.makeCall(truncateFunction, inputRef("z"), integerLiteral((-1))), DruidExpression.fromExpression("(cast(cast(\"z\" * 0.1,\'long\'),\'double\') / 0.1)"), 0.0);
    }

    @Test
    public void testDateTrunc() {
        testExpression(rexBuilder.makeCall(new DateTruncOperatorConversion().calciteOperator(), rexBuilder.makeLiteral("hour"), timestampLiteral(DateTimes.of("2000-02-03T04:05:06Z"))), DruidExpression.fromExpression("timestamp_floor(949550706000,'PT1H',null,'UTC')"), DateTimes.of("2000-02-03T04:00:00").getMillis());
        testExpression(rexBuilder.makeCall(new DateTruncOperatorConversion().calciteOperator(), rexBuilder.makeLiteral("DAY"), timestampLiteral(DateTimes.of("2000-02-03T04:05:06Z"))), DruidExpression.fromExpression("timestamp_floor(949550706000,'P1D',null,'UTC')"), DateTimes.of("2000-02-03T00:00:00").getMillis());
    }

    @Test
    public void testTrim() {
        testExpression(rexBuilder.makeCall(TRIM, rexBuilder.makeFlag(BOTH), rexBuilder.makeLiteral(" "), inputRef("spacey")), DruidExpression.fromExpression("trim(\"spacey\",\' \')"), "hey there");
        testExpression(rexBuilder.makeCall(TRIM, rexBuilder.makeFlag(LEADING), rexBuilder.makeLiteral(" h"), inputRef("spacey")), DruidExpression.fromExpression("ltrim(\"spacey\",\' h\')"), "ey there  ");
        testExpression(rexBuilder.makeCall(TRIM, rexBuilder.makeFlag(TRAILING), rexBuilder.makeLiteral(" e"), inputRef("spacey")), DruidExpression.fromExpression("rtrim(\"spacey\",\' e\')"), "  hey ther");
    }

    @Test
    public void testTimeFloor() {
        testExpression(rexBuilder.makeCall(new TimeFloorOperatorConversion().calciteOperator(), timestampLiteral(DateTimes.of("2000-02-03T04:05:06Z")), rexBuilder.makeLiteral("PT1H")), DruidExpression.fromExpression("timestamp_floor(949550706000,'PT1H',null,'UTC')"), DateTimes.of("2000-02-03T04:00:00").getMillis());
        testExpression(rexBuilder.makeCall(new TimeFloorOperatorConversion().calciteOperator(), inputRef("t"), rexBuilder.makeLiteral("P1D"), rexBuilder.makeNullLiteral(typeFactory.createSqlType(TIMESTAMP)), rexBuilder.makeLiteral("America/Los_Angeles")), DruidExpression.fromExpression("timestamp_floor(\"t\",\'P1D\',null,\'America/Los_Angeles\')"), DateTimes.of("2000-02-02T08:00:00").getMillis());
    }

    @Test
    public void testOtherTimeFloor() {
        // FLOOR(__time TO unit)
        testExpression(rexBuilder.makeCall(FLOOR, inputRef("t"), rexBuilder.makeFlag(YEAR)), DruidExpression.fromExpression("timestamp_floor(\"t\",\'P1Y\',null,\'UTC\')"), DateTimes.of("2000").getMillis());
    }

    @Test
    public void testOtherTimeCeil() {
        // CEIL(__time TO unit)
        testExpression(rexBuilder.makeCall(CEIL, inputRef("t"), rexBuilder.makeFlag(YEAR)), DruidExpression.fromExpression("timestamp_ceil(\"t\",\'P1Y\',null,\'UTC\')"), DateTimes.of("2001").getMillis());
    }

    @Test
    public void testTimeShift() {
        testExpression(rexBuilder.makeCall(new TimeShiftOperatorConversion().calciteOperator(), inputRef("t"), rexBuilder.makeLiteral("PT2H"), rexBuilder.makeLiteral((-3), typeFactory.createSqlType(INTEGER), true)), DruidExpression.fromExpression("timestamp_shift(\"t\",\'PT2H\',-3)"), DateTimes.of("2000-02-02T22:05:06").getMillis());
    }

    @Test
    public void testTimeExtract() {
        testExpression(rexBuilder.makeCall(new TimeExtractOperatorConversion().calciteOperator(), inputRef("t"), rexBuilder.makeLiteral("QUARTER")), DruidExpression.fromExpression("timestamp_extract(\"t\",\'QUARTER\',\'UTC\')"), 1L);
        testExpression(rexBuilder.makeCall(new TimeExtractOperatorConversion().calciteOperator(), inputRef("t"), rexBuilder.makeLiteral("DAY"), rexBuilder.makeLiteral("America/Los_Angeles")), DruidExpression.fromExpression("timestamp_extract(\"t\",\'DAY\',\'America/Los_Angeles\')"), 2L);
    }

    @Test
    public void testTimePlusDayTimeInterval() {
        final Period period = new Period("P1DT1H1M");
        testExpression(rexBuilder.makeCall(DATETIME_PLUS, inputRef("t"), // DAY-TIME literals value is millis
        rexBuilder.makeIntervalLiteral(new BigDecimal(period.toStandardDuration().getMillis()), new org.apache.calcite.sql.SqlIntervalQualifier(TimeUnit.DAY, TimeUnit.MINUTE, SqlParserPos.ZERO))), DruidExpression.of(null, "(\"t\" + 90060000)"), DateTimes.of("2000-02-03T04:05:06").plus(period).getMillis());
    }

    @Test
    public void testTimePlusYearMonthInterval() {
        final Period period = new Period("P1Y1M");
        testExpression(rexBuilder.makeCall(DATETIME_PLUS, inputRef("t"), // YEAR-MONTH literals value is months
        rexBuilder.makeIntervalLiteral(new BigDecimal(13), new org.apache.calcite.sql.SqlIntervalQualifier(TimeUnit.YEAR, TimeUnit.MONTH, SqlParserPos.ZERO))), DruidExpression.of(null, "timestamp_shift(\"t\",concat(\'P\', 13, \'M\'),1)"), DateTimes.of("2000-02-03T04:05:06").plus(period).getMillis());
    }

    @Test
    public void testTimeMinusDayTimeInterval() {
        final Period period = new Period("P1DT1H1M");
        testExpression(rexBuilder.makeCall(typeFactory.createSqlType(TIMESTAMP), MINUS_DATE, ImmutableList.of(inputRef("t"), // DAY-TIME literals value is millis
        rexBuilder.makeIntervalLiteral(new BigDecimal(period.toStandardDuration().getMillis()), new org.apache.calcite.sql.SqlIntervalQualifier(TimeUnit.DAY, TimeUnit.MINUTE, SqlParserPos.ZERO)))), DruidExpression.of(null, "(\"t\" - 90060000)"), DateTimes.of("2000-02-03T04:05:06").minus(period).getMillis());
    }

    @Test
    public void testTimeMinusYearMonthInterval() {
        final Period period = new Period("P1Y1M");
        testExpression(rexBuilder.makeCall(typeFactory.createSqlType(TIMESTAMP), MINUS_DATE, ImmutableList.of(inputRef("t"), // YEAR-MONTH literals value is months
        rexBuilder.makeIntervalLiteral(new BigDecimal(13), new org.apache.calcite.sql.SqlIntervalQualifier(TimeUnit.YEAR, TimeUnit.MONTH, SqlParserPos.ZERO)))), DruidExpression.of(null, "timestamp_shift(\"t\",concat(\'P\', 13, \'M\'),-1)"), DateTimes.of("2000-02-03T04:05:06").minus(period).getMillis());
    }

    @Test
    public void testTimeParse() {
        testExpression(rexBuilder.makeCall(new TimeParseOperatorConversion().calciteOperator(), inputRef("tstr"), rexBuilder.makeLiteral("yyyy-MM-dd HH:mm:ss")), DruidExpression.fromExpression("timestamp_parse(\"tstr\",\'yyyy-MM-dd HH:mm:ss\')"), DateTimes.of("2000-02-03T04:05:06").getMillis());
        testExpression(rexBuilder.makeCall(new TimeParseOperatorConversion().calciteOperator(), inputRef("tstr"), rexBuilder.makeLiteral("yyyy-MM-dd HH:mm:ss"), rexBuilder.makeLiteral("America/Los_Angeles")), DruidExpression.fromExpression("timestamp_parse(\"tstr\",\'yyyy-MM-dd HH:mm:ss\',\'America/Los_Angeles\')"), DateTimes.of("2000-02-03T04:05:06-08:00").getMillis());
    }

    @Test
    public void testTimeFormat() {
        testExpression(rexBuilder.makeCall(new TimeFormatOperatorConversion().calciteOperator(), inputRef("t"), rexBuilder.makeLiteral("yyyy-MM-dd HH:mm:ss")), DruidExpression.fromExpression("timestamp_format(\"t\",\'yyyy-MM-dd HH:mm:ss\',\'UTC\')"), "2000-02-03 04:05:06");
        testExpression(rexBuilder.makeCall(new TimeFormatOperatorConversion().calciteOperator(), inputRef("t"), rexBuilder.makeLiteral("yyyy-MM-dd HH:mm:ss"), rexBuilder.makeLiteral("America/Los_Angeles")), DruidExpression.fromExpression("timestamp_format(\"t\",\'yyyy-MM-dd HH:mm:ss\',\'America/Los_Angeles\')"), "2000-02-02 20:05:06");
    }

    @Test
    public void testExtract() {
        testExpression(rexBuilder.makeCall(EXTRACT, rexBuilder.makeFlag(QUARTER), inputRef("t")), DruidExpression.fromExpression("timestamp_extract(\"t\",\'QUARTER\',\'UTC\')"), 1L);
        testExpression(rexBuilder.makeCall(EXTRACT, rexBuilder.makeFlag(DAY), inputRef("t")), DruidExpression.fromExpression("timestamp_extract(\"t\",\'DAY\',\'UTC\')"), 3L);
    }

    @Test
    public void testCastAsTimestamp() {
        testExpression(rexBuilder.makeAbstractCast(typeFactory.createSqlType(TIMESTAMP), inputRef("t")), DruidExpression.of(SimpleExtraction.of("t", null), "\"t\""), DateTimes.of("2000-02-03T04:05:06Z").getMillis());
        testExpression(rexBuilder.makeAbstractCast(typeFactory.createSqlType(TIMESTAMP), inputRef("tstr")), DruidExpression.of(null, "timestamp_parse(\"tstr\",null,\'UTC\')"), DateTimes.of("2000-02-03T04:05:06Z").getMillis());
    }

    @Test
    public void testCastFromTimestamp() {
        testExpression(rexBuilder.makeAbstractCast(typeFactory.createSqlType(VARCHAR), rexBuilder.makeAbstractCast(typeFactory.createSqlType(TIMESTAMP), inputRef("t"))), DruidExpression.fromExpression("timestamp_format(\"t\",\'yyyy-MM-dd HH:mm:ss\',\'UTC\')"), "2000-02-03 04:05:06");
        testExpression(rexBuilder.makeAbstractCast(typeFactory.createSqlType(BIGINT), rexBuilder.makeAbstractCast(typeFactory.createSqlType(TIMESTAMP), inputRef("t"))), DruidExpression.of(SimpleExtraction.of("t", null), "\"t\""), DateTimes.of("2000-02-03T04:05:06").getMillis());
    }

    @Test
    public void testCastAsDate() {
        testExpression(rexBuilder.makeAbstractCast(typeFactory.createSqlType(DATE), inputRef("t")), DruidExpression.fromExpression("timestamp_floor(\"t\",\'P1D\',null,\'UTC\')"), DateTimes.of("2000-02-03").getMillis());
        testExpression(rexBuilder.makeAbstractCast(typeFactory.createSqlType(DATE), inputRef("dstr")), DruidExpression.fromExpression("timestamp_floor(timestamp_parse(\"dstr\",null,\'UTC\'),\'P1D\',null,\'UTC\')"), DateTimes.of("2000-02-03").getMillis());
    }

    @Test
    public void testCastFromDate() {
        testExpression(rexBuilder.makeAbstractCast(typeFactory.createSqlType(VARCHAR), rexBuilder.makeAbstractCast(typeFactory.createSqlType(DATE), inputRef("t"))), DruidExpression.fromExpression("timestamp_format(timestamp_floor(\"t\",\'P1D\',null,\'UTC\'),\'yyyy-MM-dd\',\'UTC\')"), "2000-02-03");
        testExpression(rexBuilder.makeAbstractCast(typeFactory.createSqlType(BIGINT), rexBuilder.makeAbstractCast(typeFactory.createSqlType(DATE), inputRef("t"))), DruidExpression.fromExpression("timestamp_floor(\"t\",\'P1D\',null,\'UTC\')"), DateTimes.of("2000-02-03").getMillis());
    }
}

