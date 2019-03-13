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
package org.apache.drill.exec.physical.impl;


import ScalarReplacementOption.OFF;
import ScalarReplacementOption.TRY;
import io.netty.buffer.DrillBuf;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.time.LocalDate;
import java.time.LocalTime;
import org.apache.drill.categories.UnlikelyTest;
import org.apache.drill.exec.ExecConstants;
import org.apache.drill.exec.ExecTest;
import org.apache.drill.exec.expr.fn.impl.DateUtility;
import org.apache.drill.exec.server.options.OptionValue;
import org.apache.drill.exec.util.ByteBufUtil.HadoopWritables;
import org.apache.drill.test.BaseTestQuery;
import org.apache.drill.test.QueryTestUtil;
import org.apache.drill.test.TestBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category(UnlikelyTest.class)
public class TestConvertFunctions extends BaseTestQuery {
    private static final String CONVERSION_TEST_LOGICAL_PLAN = "functions/conv/conversionTestWithLogicalPlan.json";

    private static final String CONVERSION_TEST_PHYSICAL_PLAN = "functions/conv/conversionTestWithPhysicalPlan.json";

    private static final float DELTA = ((float) (1.0E-4));

    // "1980-01-01 01:23:45.678"
    private static final String DATE_TIME_BE = "\\x00\\x00\\x00\\x49\\x77\\x85\\x1f\\x8e";

    private static final String DATE_TIME_LE = "\\x8e\\x1f\\x85\\x77\\x49\\x00\\x00\\x00";

    private static LocalTime time = LocalTime.parse("01:23:45.678", DateUtility.getTimeFormatter());

    private static LocalDate date = LocalDate.parse("1980-01-01", DateUtility.getDateTimeFormatter());

    String textFileContent;

    // DRILL-3854
    @Test
    public void testConvertFromConvertToInt() throws Exception {
        final OptionValue srOption = QueryTestUtil.setupScalarReplacementOption(BaseTestQuery.bits[0], OFF);
        try {
            final String newTblName = "testConvertFromConvertToInt_tbl";
            BaseTestQuery.test("alter session set `planner.slice_target` = 1");
            BaseTestQuery.test(("CREATE TABLE dfs.%s as \n" + ("SELECT convert_to(r_regionkey, \'INT\') as ct \n" + "FROM cp.`tpch/region.parquet`")), newTblName);
            BaseTestQuery.testBuilder().sqlQuery(("SELECT convert_from(ct, \'INT\') as cf \n" + ("FROM dfs.%s \n" + "ORDER BY ct")), newTblName).ordered().baselineColumns("cf").baselineValues(0).baselineValues(1).baselineValues(2).baselineValues(3).baselineValues(4).build().run();
        } finally {
            // restore the system option
            QueryTestUtil.restoreScalarReplacementOption(BaseTestQuery.bits[0], srOption.string_val);
            BaseTestQuery.test(("alter session set `planner.slice_target` = " + (ExecConstants.SLICE_TARGET_DEFAULT)));
        }
    }

    @Test
    public void test_JSON_convertTo_empty_list_drill_1416() throws Exception {
        String listStr = "[ 4, 6 ]";
        BaseTestQuery.testBuilder().sqlQuery("select cast(convert_to(rl[1], 'JSON') as varchar(100)) as json_str from cp.`store/json/input2.json`").unOrdered().baselineColumns("json_str").baselineValues(listStr).baselineValues("[ ]").baselineValues(listStr).baselineValues(listStr).go();
        Object listVal = TestBuilder.listOf(4L, 6L);
        BaseTestQuery.testBuilder().sqlQuery("select convert_from(convert_to(rl[1], 'JSON'), 'JSON') list_col from cp.`store/json/input2.json`").unOrdered().baselineColumns("list_col").baselineValues(listVal).baselineValues(TestBuilder.listOf()).baselineValues(listVal).baselineValues(listVal).go();
        Object mapVal1 = TestBuilder.mapOf("f1", 4L, "f2", 6L);
        Object mapVal2 = TestBuilder.mapOf("f1", 11L);
        BaseTestQuery.testBuilder().sqlQuery("select convert_from(convert_to(rl[1], 'JSON'), 'JSON') as map_col from cp.`store/json/json_project_null_object_from_list.json`").unOrdered().baselineColumns("map_col").baselineValues(mapVal1).baselineValues(TestBuilder.mapOf()).baselineValues(mapVal2).baselineValues(mapVal1).go();
    }

    // DRILL-4679
    @Test
    public void testConvertFromJson_drill4679() throws Exception {
        Object mapVal1 = TestBuilder.mapOf("y", "kevin", "z", "paul");
        Object mapVal2 = TestBuilder.mapOf("y", "bill", "z", "peter");
        // right side of union-all produces 0 rows due to FALSE filter, column t.x is a map
        String query1 = String.format(("select 'abc' as col1, convert_from(convert_to(t.x, 'JSON'), 'JSON') as col2, 'xyz' as col3 from cp.`store/json/input2.json` t " + (((" where t.`integer` = 2010 " + " union all ") + " select 'abc' as col1, convert_from(convert_to(t.x, 'JSON'), 'JSON') as col2, 'xyz' as col3 from cp.`store/json/input2.json` t") + " where 1 = 0")));
        BaseTestQuery.testBuilder().sqlQuery(query1).unOrdered().baselineColumns("col1", "col2", "col3").baselineValues("abc", mapVal1, "xyz").go();
        // left side of union-all produces 0 rows due to FALSE filter, column t.x is a map
        String query2 = String.format(("select 'abc' as col1, convert_from(convert_to(t.x, 'JSON'), 'JSON') as col2, 'xyz' as col3 from cp.`store/json/input2.json` t " + (((" where 1 = 0 " + " union all ") + " select 'abc' as col1, convert_from(convert_to(t.x, 'JSON'), 'JSON') as col2, 'xyz' as col3 from cp.`store/json/input2.json` t ") + " where t.`integer` = 2010")));
        BaseTestQuery.testBuilder().sqlQuery(query2).unOrdered().baselineColumns("col1", "col2", "col3").baselineValues("abc", mapVal1, "xyz").go();
        // sanity test where neither side produces 0 rows
        String query3 = String.format(("select 'abc' as col1, convert_from(convert_to(t.x, 'JSON'), 'JSON') as col2, 'xyz' as col3 from cp.`store/json/input2.json` t " + (((" where t.`integer` = 2010 " + " union all ") + " select 'abc' as col1, convert_from(convert_to(t.x, 'JSON'), 'JSON') as col2, 'xyz' as col3 from cp.`store/json/input2.json` t ") + " where t.`integer` = 2001")));
        BaseTestQuery.testBuilder().sqlQuery(query3).unOrdered().baselineColumns("col1", "col2", "col3").baselineValues("abc", mapVal1, "xyz").baselineValues("abc", mapVal2, "xyz").go();
        // convert_from() on a list, column t.rl is a repeated list
        Object listVal1 = TestBuilder.listOf(TestBuilder.listOf(2L, 1L), TestBuilder.listOf(4L, 6L));
        Object listVal2 = TestBuilder.listOf();// empty

        String query4 = String.format(("select 'abc' as col1, convert_from(convert_to(t.rl, 'JSON'), 'JSON') as col2, 'xyz' as col3 from cp.`store/json/input2.json` t " + ((" union all " + " select 'abc' as col1, convert_from(convert_to(t.rl, 'JSON'), 'JSON') as col2, 'xyz' as col3 from cp.`store/json/input2.json` t") + " where 1 = 0")));
        BaseTestQuery.testBuilder().sqlQuery(query4).unOrdered().baselineColumns("col1", "col2", "col3").baselineValues("abc", listVal1, "xyz").baselineValues("abc", listVal2, "xyz").baselineValues("abc", listVal1, "xyz").baselineValues("abc", listVal1, "xyz").go();
    }

    // DRILL-4693
    @Test
    public void testConvertFromJson_drill4693() throws Exception {
        Object mapVal1 = TestBuilder.mapOf("x", "y");
        BaseTestQuery.testBuilder().sqlQuery(("select \'abc\' as col1, convert_from(\'{\"x\" : \"y\"}\', \'json\') as col2, \'xyz\' as col3 " + (" from cp.`store/json/input2.json` t" + " where t.`integer` = 2001"))).unOrdered().baselineColumns("col1", "col2", "col3").baselineValues("abc", mapVal1, "xyz").go();
    }

    @Test
    public void testConvertFromJsonNullableInput() throws Exception {
        // Contents of the generated file:
        /* {"k": "{a: 1, b: 2}"}
        {"k": null}
        {"k": "{c: 3}"}
         */
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(new File(ExecTest.dirTestWatcher.getRootDir(), "nullable_json_strings.json")))) {
            String[] fieldValue = new String[]{ "\"{a: 1, b: 2}\"", null, "\"{c: 3}\"" };
            for (String value : fieldValue) {
                String entry = String.format("{\"k\": %s}\n", value);
                writer.write(entry);
            }
        }
        BaseTestQuery.testBuilder().sqlQuery("select convert_from(k, 'json') as col from dfs.`nullable_json_strings.json`").unOrdered().baselineColumns("col").baselineValues(TestBuilder.mapOf("a", 1L, "b", 2L)).baselineValues(TestBuilder.mapOf()).baselineValues(TestBuilder.mapOf("c", 3L)).go();
    }

    @Test
    public void testConvertToComplexJSON() throws Exception {
        String result1 = "[ {\n" + ((("  \"$numberLong\" : 4\n" + "}, {\n") + "  \"$numberLong\" : 6\n") + "} ]");
        String result2 = "[ ]";
        BaseTestQuery.testBuilder().sqlQuery("select cast(convert_to(rl[1], 'EXTENDEDJSON') as varchar(100)) as json_str from cp.`store/json/input2.json`").unOrdered().baselineColumns("json_str").baselineValues(result1).baselineValues(result2).baselineValues(result1).baselineValues(result1).go();
    }

    @Test
    public void testDateTime1() throws Throwable {
        verifyPhysicalPlan((("(convert_from(binary_string('" + (TestConvertFunctions.DATE_TIME_BE)) + "'), 'TIME_EPOCH_BE'))"), TestConvertFunctions.time);
    }

    @Test
    public void testDateTime2() throws Throwable {
        verifyPhysicalPlan((("convert_from(binary_string('" + (TestConvertFunctions.DATE_TIME_LE)) + "'), 'TIME_EPOCH')"), TestConvertFunctions.time);
    }

    @Test
    public void testDateTime3() throws Throwable {
        verifyPhysicalPlan((("convert_from(binary_string('" + (TestConvertFunctions.DATE_TIME_BE)) + "'), 'DATE_EPOCH_BE')"), TestConvertFunctions.date);
    }

    @Test
    public void testDateTime4() throws Throwable {
        verifyPhysicalPlan((("convert_from(binary_string('" + (TestConvertFunctions.DATE_TIME_LE)) + "'), 'DATE_EPOCH')"), TestConvertFunctions.date);
    }

    @Test
    public void testFixedInts1() throws Throwable {
        verifyPhysicalPlan("convert_from(binary_string(\'\\xAD\'), \'TINYINT\')", ((byte) (173)));
    }

    @Test
    public void testFixedInts2() throws Throwable {
        verifyPhysicalPlan("convert_from(binary_string(\'\\xFE\\xCA\'), \'SMALLINT\')", ((short) (51966)));
    }

    @Test
    public void testFixedInts3() throws Throwable {
        verifyPhysicalPlan("convert_from(binary_string(\'\\xCA\\xFE\'), \'SMALLINT_BE\')", ((short) (51966)));
    }

    @Test
    public void testFixedInts4() throws Throwable {
        verifyPhysicalPlan("convert_from(binary_string(\'\\xBE\\xBA\\xFE\\xCA\'), \'INT\')", -889275714);
    }

    @Test
    public void testFixedInts4SQL_from() throws Throwable {
        verifySQL(("select" + (("   convert_from(binary_string(\'\\xBE\\xBA\\xFE\\xCA\'), \'INT\')" + " from") + "   cp.`employee.json` LIMIT 1")), -889275714);
    }

    @Test
    public void testFixedInts4SQL_to() throws Throwable {
        verifySQL(("select" + (("   convert_to(-889275714, 'INT')" + " from") + "   cp.`employee.json` LIMIT 1")), new byte[]{ ((byte) (190)), ((byte) (186)), ((byte) (254)), ((byte) (202)) });
    }

    @Test
    public void testFixedInts5() throws Throwable {
        verifyPhysicalPlan("convert_from(binary_string(\'\\xCA\\xFE\\xBA\\xBE\'), \'INT_BE\')", -889275714);
    }

    @Test
    public void testFixedInts6() throws Throwable {
        verifyPhysicalPlan("convert_from(binary_string(\'\\xEF\\xBE\\xAD\\xDE\\xBE\\xBA\\xFE\\xCA\'), \'BIGINT\')", -3819410105021120785L);
    }

    @Test
    public void testFixedInts7() throws Throwable {
        verifyPhysicalPlan("convert_from(binary_string(\'\\xCA\\xFE\\xBA\\xBE\\xDE\\xAD\\xBE\\xEF\'), \'BIGINT_BE\')", -3819410105021120785L);
    }

    @Test
    public void testFixedInts8() throws Throwable {
        verifyPhysicalPlan("convert_from(convert_to(cast(77 as varchar(2)), 'INT_BE'), 'INT_BE')", 77);
    }

    @Test
    public void testFixedInts9() throws Throwable {
        verifyPhysicalPlan("convert_to(cast(77 as varchar(2)), 'INT_BE')", new byte[]{ 0, 0, 0, 77 });
    }

    @Test
    public void testFixedInts10() throws Throwable {
        verifyPhysicalPlan("convert_to(cast(77 as varchar(2)), 'INT')", new byte[]{ 77, 0, 0, 0 });
    }

    @Test
    public void testFixedInts11() throws Throwable {
        verifyPhysicalPlan("convert_to(77, 'BIGINT_BE')", new byte[]{ 0, 0, 0, 0, 0, 0, 0, 77 });
    }

    @Test
    public void testFixedInts12() throws Throwable {
        verifyPhysicalPlan("convert_to(9223372036854775807, 'BIGINT')", new byte[]{ -1, -1, -1, -1, -1, -1, -1, 127 });
    }

    @Test
    public void testFixedInts13() throws Throwable {
        verifyPhysicalPlan("convert_to(-9223372036854775808, 'BIGINT')", new byte[]{ 0, 0, 0, 0, 0, 0, 0, ((byte) (128)) });
    }

    @Test
    public void testVInts1() throws Throwable {
        verifyPhysicalPlan("convert_to(cast(0 as int), 'INT_HADOOPV')", new byte[]{ 0 });
    }

    @Test
    public void testVInts2() throws Throwable {
        verifyPhysicalPlan("convert_to(cast(128 as int), 'INT_HADOOPV')", new byte[]{ -113, -128 });
    }

    @Test
    public void testVInts3() throws Throwable {
        verifyPhysicalPlan("convert_to(cast(256 as int), 'INT_HADOOPV')", new byte[]{ -114, 1, 0 });
    }

    @Test
    public void testVInts4() throws Throwable {
        verifyPhysicalPlan("convert_to(cast(65536 as int), 'INT_HADOOPV')", new byte[]{ -115, 1, 0, 0 });
    }

    @Test
    public void testVInts5() throws Throwable {
        verifyPhysicalPlan("convert_to(cast(16777216 as int), 'INT_HADOOPV')", new byte[]{ -116, 1, 0, 0, 0 });
    }

    @Test
    public void testVInts6() throws Throwable {
        verifyPhysicalPlan("convert_to(4294967296, 'BIGINT_HADOOPV')", new byte[]{ -117, 1, 0, 0, 0, 0 });
    }

    @Test
    public void testVInts7() throws Throwable {
        verifyPhysicalPlan("convert_to(1099511627776, 'BIGINT_HADOOPV')", new byte[]{ -118, 1, 0, 0, 0, 0, 0 });
    }

    @Test
    public void testVInts8() throws Throwable {
        verifyPhysicalPlan("convert_to(281474976710656, 'BIGINT_HADOOPV')", new byte[]{ -119, 1, 0, 0, 0, 0, 0, 0 });
    }

    @Test
    public void testVInts9() throws Throwable {
        verifyPhysicalPlan("convert_to(72057594037927936, 'BIGINT_HADOOPV')", new byte[]{ -120, 1, 0, 0, 0, 0, 0, 0, 0 });
    }

    @Test
    public void testVInts10() throws Throwable {
        verifyPhysicalPlan("convert_to(9223372036854775807, 'BIGINT_HADOOPV')", new byte[]{ -120, 127, -1, -1, -1, -1, -1, -1, -1 });
    }

    @Test
    public void testVInts11() throws Throwable {
        verifyPhysicalPlan("convert_from(binary_string(\'\\x88\\x7f\\xFF\\xFF\\xFF\\xFF\\xFF\\xFF\\xFF\'), \'BIGINT_HADOOPV\')", 9223372036854775807L);
    }

    @Test
    public void testVInts12() throws Throwable {
        verifyPhysicalPlan("convert_to(-9223372036854775808, 'BIGINT_HADOOPV')", new byte[]{ -128, 127, -1, -1, -1, -1, -1, -1, -1 });
    }

    @Test
    public void testVInts13() throws Throwable {
        verifyPhysicalPlan("convert_from(binary_string(\'\\x80\\x7f\\xFF\\xFF\\xFF\\xFF\\xFF\\xFF\\xFF\'), \'BIGINT_HADOOPV\')", -9223372036854775808L);
    }

    @Test
    public void testBool1() throws Throwable {
        verifyPhysicalPlan("convert_from(binary_string(\'\\x01\'), \'BOOLEAN_BYTE\')", true);
    }

    @Test
    public void testBool2() throws Throwable {
        verifyPhysicalPlan("convert_from(binary_string(\'\\x00\'), \'BOOLEAN_BYTE\')", false);
    }

    @Test
    public void testBool3() throws Throwable {
        verifyPhysicalPlan("convert_to(true, 'BOOLEAN_BYTE')", new byte[]{ 1 });
    }

    @Test
    public void testBool4() throws Throwable {
        verifyPhysicalPlan("convert_to(false, 'BOOLEAN_BYTE')", new byte[]{ 0 });
    }

    @Test
    public void testFloats2() throws Throwable {
        verifyPhysicalPlan("convert_from(convert_to(cast(77 as float4), 'FLOAT'), 'FLOAT')", new Float(77.0));
    }

    @Test
    public void testFloats2be() throws Throwable {
        verifyPhysicalPlan("convert_from(convert_to(cast(77 as float4), 'FLOAT_BE'), 'FLOAT_BE')", new Float(77.0));
    }

    @Test
    public void testFloats3() throws Throwable {
        verifyPhysicalPlan("convert_to(cast(1.4e-45 as float4), 'FLOAT')", new byte[]{ 1, 0, 0, 0 });
    }

    @Test
    public void testFloats4() throws Throwable {
        verifyPhysicalPlan("convert_to(cast(3.4028235e+38 as float4), 'FLOAT')", new byte[]{ -1, -1, 127, 127 });
    }

    @Test
    public void testFloats5() throws Throwable {
        verifyPhysicalPlan("convert_from(convert_to(cast(77 as float8), 'DOUBLE'), 'DOUBLE')", 77.0);
    }

    @Test
    public void testFloats5be() throws Throwable {
        verifyPhysicalPlan("convert_from(convert_to(cast(77 as float8), 'DOUBLE_BE'), 'DOUBLE_BE')", 77.0);
    }

    @Test
    public void testFloats6() throws Throwable {
        verifyPhysicalPlan("convert_to(cast(77 as float8), 'DOUBLE')", new byte[]{ 0, 0, 0, 0, 0, 64, 83, 64 });
    }

    @Test
    public void testFloats7() throws Throwable {
        verifyPhysicalPlan("convert_to(4.9e-324, 'DOUBLE')", new byte[]{ 1, 0, 0, 0, 0, 0, 0, 0 });
    }

    @Test
    public void testFloats8() throws Throwable {
        verifyPhysicalPlan("convert_to(1.7976931348623157e+308, 'DOUBLE')", new byte[]{ -1, -1, -1, -1, -1, -1, -17, 127 });
    }

    @Test
    public void testUTF8() throws Throwable {
        verifyPhysicalPlan("convert_from(binary_string('apache_drill'), 'UTF8')", "apache_drill");
        verifyPhysicalPlan("convert_to('apache_drill', 'UTF8')", new byte[]{ 'a', 'p', 'a', 'c', 'h', 'e', '_', 'd', 'r', 'i', 'l', 'l' });
    }

    // TODO(DRILL-2326) temporary until we fix the scalar replacement bug for this case
    @Test
    public void testBigIntVarCharReturnTripConvertLogical_ScalarReplaceTRY() throws Exception {
        final OptionValue srOption = QueryTestUtil.setupScalarReplacementOption(BaseTestQuery.bits[0], TRY);
        try {
            // this should work fine
            testBigIntVarCharReturnTripConvertLogical();
        } finally {
            // restore the system option
            QueryTestUtil.restoreScalarReplacementOption(BaseTestQuery.bits[0], srOption.string_val);
        }
    }

    // TODO(DRILL-2326) temporary until we fix the scalar replacement bug for this case
    @Test
    public void testBigIntVarCharReturnTripConvertLogical_ScalarReplaceOFF() throws Exception {
        final OptionValue srOption = QueryTestUtil.setupScalarReplacementOption(BaseTestQuery.bits[0], OFF);
        try {
            // this should work fine
            testBigIntVarCharReturnTripConvertLogical();
        } finally {
            // restore the system option
            QueryTestUtil.restoreScalarReplacementOption(BaseTestQuery.bits[0], srOption.string_val);
        }
    }

    @Test
    public void testHadooopVInt() throws Exception {
        final int _0 = 0;
        final int _9 = 9;
        @SuppressWarnings("resource")
        final DrillBuf buffer = BaseTestQuery.getAllocator().buffer(_9);
        long longVal = 0;
        buffer.clear();
        HadoopWritables.writeVLong(buffer, _0, _9, 0);
        longVal = HadoopWritables.readVLong(buffer, _0, _9);
        Assert.assertEquals(longVal, 0);
        buffer.clear();
        HadoopWritables.writeVLong(buffer, _0, _9, Long.MAX_VALUE);
        longVal = HadoopWritables.readVLong(buffer, _0, _9);
        Assert.assertEquals(longVal, Long.MAX_VALUE);
        buffer.clear();
        HadoopWritables.writeVLong(buffer, _0, _9, Long.MIN_VALUE);
        longVal = HadoopWritables.readVLong(buffer, _0, _9);
        Assert.assertEquals(longVal, Long.MIN_VALUE);
        int intVal = 0;
        buffer.clear();
        HadoopWritables.writeVInt(buffer, _0, _9, 0);
        intVal = HadoopWritables.readVInt(buffer, _0, _9);
        Assert.assertEquals(intVal, 0);
        buffer.clear();
        HadoopWritables.writeVInt(buffer, _0, _9, Integer.MAX_VALUE);
        intVal = HadoopWritables.readVInt(buffer, _0, _9);
        Assert.assertEquals(intVal, Integer.MAX_VALUE);
        buffer.clear();
        HadoopWritables.writeVInt(buffer, _0, _9, Integer.MIN_VALUE);
        intVal = HadoopWritables.readVInt(buffer, _0, _9);
        Assert.assertEquals(intVal, Integer.MIN_VALUE);
        buffer.release();
    }

    // DRILL-4862
    @Test
    public void testBinaryString() throws Exception {
        // TODO(DRILL-2326) temporary until we fix the scalar replacement bug for this case
        final OptionValue srOption = QueryTestUtil.setupScalarReplacementOption(BaseTestQuery.bits[0], TRY);
        try {
            final String[] queries = new String[]{ "SELECT convert_from(binary_string(key), \'INT_BE\') as intkey \n" + "FROM cp.`functions/conv/conv.json`" };
            for (String query : queries) {
                BaseTestQuery.testBuilder().sqlQuery(query).ordered().baselineColumns("intkey").baselineValues(1244739896).baselineValues(new Object[]{ null }).baselineValues(1313814865).baselineValues(1852782897).build().run();
            }
        } finally {
            // restore the system option
            QueryTestUtil.restoreScalarReplacementOption(BaseTestQuery.bits[0], srOption.string_val);
        }
    }
}

