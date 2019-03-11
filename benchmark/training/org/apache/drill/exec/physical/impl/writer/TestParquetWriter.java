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
package org.apache.drill.exec.physical.impl.writer;


import ExecConstants.PARQUET_BLOCK_SIZE;
import ExecConstants.PARQUET_NEW_RECORD_READER;
import ExecConstants.PARQUET_READER_INT96_AS_TIMESTAMP;
import ExecConstants.PARQUET_WRITER_COMPRESSION_TYPE;
import ExecConstants.PARQUET_WRITER_ENABLE_DICTIONARY_ENCODING;
import ExecConstants.PARQUET_WRITER_LOGICAL_TYPE_FOR_DECIMALS;
import ExecConstants.PARQUET_WRITER_USE_PRIMITIVE_TYPES_FOR_DECIMALS;
import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.calcite.util.Pair;
import org.apache.drill.categories.ParquetTest;
import org.apache.drill.categories.SlowTest;
import org.apache.drill.categories.UnlikelyTest;
import org.apache.drill.exec.ExecTest;
import org.apache.drill.exec.fn.interp.TestConstantFolding;
import org.apache.drill.exec.util.JsonStringArrayList;
import org.apache.drill.shaded.guava.com.google.common.base.Joiner;
import org.apache.drill.shaded.guava.com.google.common.collect.ImmutableList;
import org.apache.drill.test.BaseTestQuery;
import org.apache.drill.test.TestBuilder;
import org.apache.hadoop.fs.FileSystem;
import org.joda.time.Period;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
@Category({ SlowTest.class, ParquetTest.class })
public class TestParquetWriter extends BaseTestQuery {
    private static FileSystem fs;

    // Map storing a convenient name as well as the cast type necessary
    // to produce it casting from a varchar
    private static final Map<String, String> allTypes = new HashMap<>();

    // Select statement for all supported Drill types, for use in conjunction with
    // the file parquet/alltypes.json in the resources directory
    private static final String allTypesSelection;

    static {
        TestParquetWriter.allTypes.put("int", "int");
        TestParquetWriter.allTypes.put("bigint", "bigint");
        TestParquetWriter.allTypes.put("decimal(9, 4)", "decimal9");
        TestParquetWriter.allTypes.put("decimal(18,9)", "decimal18");
        TestParquetWriter.allTypes.put("decimal(28, 14)", "decimal28sparse");
        TestParquetWriter.allTypes.put("decimal(38, 19)", "decimal38sparse");
        TestParquetWriter.allTypes.put("decimal(38, 15)", "vardecimal");
        TestParquetWriter.allTypes.put("date", "date");
        TestParquetWriter.allTypes.put("timestamp", "timestamp");
        TestParquetWriter.allTypes.put("float", "float4");
        TestParquetWriter.allTypes.put("double", "float8");
        TestParquetWriter.allTypes.put("varbinary(65000)", "varbinary");
        // TODO(DRILL-2297)
        // allTypes.put("interval year",      "intervalyear");
        TestParquetWriter.allTypes.put("interval day", "intervalday");
        TestParquetWriter.allTypes.put("boolean", "bit");
        TestParquetWriter.allTypes.put("varchar", "varchar");
        TestParquetWriter.allTypes.put("time", "time");
        List<String> allTypeSelectsAndCasts = new ArrayList<>();
        for (String s : TestParquetWriter.allTypes.keySet()) {
            // don't need to cast a varchar, just add the column reference
            if (s.equals("varchar")) {
                allTypeSelectsAndCasts.add(String.format("`%s_col`", TestParquetWriter.allTypes.get(s)));
                continue;
            }
            allTypeSelectsAndCasts.add(String.format("cast(`%s_col` AS %S) `%s_col`", TestParquetWriter.allTypes.get(s), s, TestParquetWriter.allTypes.get(s)));
        }
        allTypesSelection = Joiner.on(",").join(allTypeSelectsAndCasts);
    }

    private final String allTypesTable = "cp.`parquet/alltypes.json`";

    @Parameterized.Parameter
    public int repeat = 1;

    @Test
    public void testSmallFileValueReadWrite() throws Exception {
        String selection = "key";
        String inputTable = "cp.`store/json/intData.json`";
        runTestAndValidate(selection, selection, inputTable, "smallFileTest");
    }

    @Test
    public void testSimple() throws Exception {
        String selection = "*";
        String inputTable = "cp.`employee.json`";
        runTestAndValidate(selection, selection, inputTable, "employee_parquet");
    }

    @Test
    public void testLargeFooter() throws Exception {
        StringBuilder sb = new StringBuilder();
        // create a JSON document with a lot of columns
        sb.append("{");
        final int numCols = 1000;
        String[] colNames = new String[numCols];
        Object[] values = new Object[numCols];
        for (int i = 0; i < (numCols - 1); i++) {
            sb.append(String.format("\"col_%d\" : 100,", i));
            colNames[i] = "col_" + i;
            values[i] = 100L;
        }
        // add one column without a comma after it
        sb.append(String.format("\"col_%d\" : 100", (numCols - 1)));
        sb.append("}");
        colNames[(numCols - 1)] = "col_" + (numCols - 1);
        values[(numCols - 1)] = 100L;
        String path = "test";
        File pathDir = ExecTest.dirTestWatcher.makeRootSubDir(Paths.get(path));
        // write it to a file in the temp directory for the test
        new TestConstantFolding.SmallFileCreator(pathDir).setRecord(sb.toString()).createFiles(1, 1, "json");
        BaseTestQuery.test("use dfs.tmp");
        BaseTestQuery.test("create table WIDE_PARQUET_TABLE_TestParquetWriter_testLargeFooter as select * from dfs.`%s/smallfile/smallfile.json`", path);
        BaseTestQuery.testBuilder().sqlQuery("select * from dfs.tmp.WIDE_PARQUET_TABLE_TestParquetWriter_testLargeFooter").unOrdered().baselineColumns(colNames).baselineValues(values).build().run();
    }

    @Test
    public void testAllScalarTypes() throws Exception {
        // / read once with the flat reader
        runTestAndValidate(TestParquetWriter.allTypesSelection, "*", allTypesTable, "donuts_json");
        try {
            // read all of the types with the complex reader
            BaseTestQuery.alterSession(PARQUET_NEW_RECORD_READER, true);
            runTestAndValidate(TestParquetWriter.allTypesSelection, "*", allTypesTable, "donuts_json");
        } finally {
            BaseTestQuery.resetSessionOption(PARQUET_NEW_RECORD_READER);
        }
    }

    @Test
    public void testAllScalarTypesDictionary() throws Exception {
        try {
            BaseTestQuery.alterSession(PARQUET_WRITER_ENABLE_DICTIONARY_ENCODING, true);
            // / read once with the flat reader
            runTestAndValidate(TestParquetWriter.allTypesSelection, "*", allTypesTable, "donuts_json");
            // read all of the types with the complex reader
            BaseTestQuery.alterSession(PARQUET_NEW_RECORD_READER, true);
            runTestAndValidate(TestParquetWriter.allTypesSelection, "*", allTypesTable, "donuts_json");
        } finally {
            BaseTestQuery.resetSessionOption(PARQUET_NEW_RECORD_READER);
            BaseTestQuery.resetSessionOption(PARQUET_WRITER_ENABLE_DICTIONARY_ENCODING);
        }
    }

    @Test
    public void testDictionaryError() throws Exception {
        compareParquetReadersColumnar("*", "cp.`parquet/required_dictionary.parquet`");
        runTestAndValidate("*", "*", "cp.`parquet/required_dictionary.parquet`", "required_dictionary");
    }

    @Test
    public void testDictionaryEncoding() throws Exception {
        String selection = "type";
        String inputTable = "cp.`donuts.json`";
        try {
            BaseTestQuery.alterSession(PARQUET_WRITER_ENABLE_DICTIONARY_ENCODING, true);
            runTestAndValidate(selection, selection, inputTable, "donuts_json");
        } finally {
            BaseTestQuery.resetSessionOption(PARQUET_WRITER_ENABLE_DICTIONARY_ENCODING);
        }
    }

    @Test
    public void testComplex() throws Exception {
        String selection = "*";
        String inputTable = "cp.`donuts.json`";
        runTestAndValidate(selection, selection, inputTable, "donuts_json");
    }

    @Test
    public void testComplexRepeated() throws Exception {
        String selection = "*";
        String inputTable = "cp.`testRepeatedWrite.json`";
        runTestAndValidate(selection, selection, inputTable, "repeated_json");
    }

    @Test
    public void testCastProjectBug_Drill_929() throws Exception {
        String selection = "L_ORDERKEY, L_PARTKEY, L_SUPPKEY, L_LINENUMBER, L_QUANTITY, L_EXTENDEDPRICE, L_DISCOUNT, L_TAX, " + "L_RETURNFLAG, L_LINESTATUS, L_SHIPDATE, cast(L_COMMITDATE as DATE) as COMMITDATE, cast(L_RECEIPTDATE as DATE) AS RECEIPTDATE, L_SHIPINSTRUCT, L_SHIPMODE, L_COMMENT";
        String validationSelection = "L_ORDERKEY, L_PARTKEY, L_SUPPKEY, L_LINENUMBER, L_QUANTITY, L_EXTENDEDPRICE, L_DISCOUNT, L_TAX, " + "L_RETURNFLAG, L_LINESTATUS, L_SHIPDATE,COMMITDATE ,RECEIPTDATE, L_SHIPINSTRUCT, L_SHIPMODE, L_COMMENT";
        String inputTable = "cp.`tpch/lineitem.parquet`";
        runTestAndValidate(selection, validationSelection, inputTable, "drill_929");
    }

    @Test
    public void testTPCHReadWrite1() throws Exception {
        String inputTable = "cp.`tpch/lineitem.parquet`";
        runTestAndValidate("*", "*", inputTable, "lineitem_parquet_all");
    }

    @Test
    public void testTPCHReadWrite1_date_convertedType() throws Exception {
        try {
            BaseTestQuery.alterSession(PARQUET_WRITER_ENABLE_DICTIONARY_ENCODING, false);
            String selection = "L_ORDERKEY, L_PARTKEY, L_SUPPKEY, L_LINENUMBER, L_QUANTITY, L_EXTENDEDPRICE, L_DISCOUNT, L_TAX, " + "L_RETURNFLAG, L_LINESTATUS, L_SHIPDATE, cast(L_COMMITDATE as DATE) as L_COMMITDATE, cast(L_RECEIPTDATE as DATE) AS L_RECEIPTDATE, L_SHIPINSTRUCT, L_SHIPMODE, L_COMMENT";
            String validationSelection = "L_ORDERKEY, L_PARTKEY, L_SUPPKEY, L_LINENUMBER, L_QUANTITY, L_EXTENDEDPRICE, L_DISCOUNT, L_TAX, " + "L_RETURNFLAG, L_LINESTATUS, L_SHIPDATE,L_COMMITDATE ,L_RECEIPTDATE, L_SHIPINSTRUCT, L_SHIPMODE, L_COMMENT";
            String inputTable = "cp.`tpch/lineitem.parquet`";
            runTestAndValidate(selection, validationSelection, inputTable, "lineitem_parquet_converted");
        } finally {
            BaseTestQuery.resetSessionOption(PARQUET_WRITER_ENABLE_DICTIONARY_ENCODING);
        }
    }

    @Test
    public void testTPCHReadWrite2() throws Exception {
        String inputTable = "cp.`tpch/customer.parquet`";
        runTestAndValidate("*", "*", inputTable, "customer_parquet");
    }

    @Test
    public void testTPCHReadWrite3() throws Exception {
        String inputTable = "cp.`tpch/nation.parquet`";
        runTestAndValidate("*", "*", inputTable, "nation_parquet");
    }

    @Test
    public void testTPCHReadWrite4() throws Exception {
        String inputTable = "cp.`tpch/orders.parquet`";
        runTestAndValidate("*", "*", inputTable, "orders_parquet");
    }

    @Test
    public void testTPCHReadWrite5() throws Exception {
        String inputTable = "cp.`tpch/part.parquet`";
        runTestAndValidate("*", "*", inputTable, "part_parquet");
    }

    @Test
    public void testTPCHReadWrite6() throws Exception {
        String inputTable = "cp.`tpch/partsupp.parquet`";
        runTestAndValidate("*", "*", inputTable, "partsupp_parquet");
    }

    @Test
    public void testTPCHReadWrite7() throws Exception {
        String inputTable = "cp.`tpch/region.parquet`";
        runTestAndValidate("*", "*", inputTable, "region_parquet");
    }

    @Test
    public void testTPCHReadWrite8() throws Exception {
        String inputTable = "cp.`tpch/supplier.parquet`";
        runTestAndValidate("*", "*", inputTable, "supplier_parquet");
    }

    @Test
    public void testTPCHReadWriteNoDictUncompressed() throws Exception {
        try {
            BaseTestQuery.alterSession(PARQUET_WRITER_ENABLE_DICTIONARY_ENCODING, false);
            BaseTestQuery.alterSession(PARQUET_WRITER_COMPRESSION_TYPE, "none");
            String inputTable = "cp.`tpch/supplier.parquet`";
            runTestAndValidate("*", "*", inputTable, "supplier_parquet_no_dict_uncompressed");
        } finally {
            BaseTestQuery.resetSessionOption(PARQUET_WRITER_ENABLE_DICTIONARY_ENCODING);
            BaseTestQuery.resetSessionOption(PARQUET_WRITER_COMPRESSION_TYPE);
        }
    }

    @Test
    public void testTPCHReadWriteDictGzip() throws Exception {
        try {
            BaseTestQuery.alterSession(PARQUET_WRITER_COMPRESSION_TYPE, "gzip");
            String inputTable = "cp.`tpch/supplier.parquet`";
            runTestAndValidate("*", "*", inputTable, "supplier_parquet_dict_gzip");
        } finally {
            BaseTestQuery.resetSessionOption(PARQUET_WRITER_COMPRESSION_TYPE);
        }
    }

    // working to create an exhaustive test of the format for this one. including all convertedTypes
    // will not be supporting interval for Beta as of current schedule
    // Types left out:
    // "TIMESTAMPTZ_col"
    @Test
    public void testRepeated() throws Exception {
        String inputTable = "cp.`parquet/basic_repeated.json`";
        runTestAndValidate("*", "*", inputTable, "basic_repeated");
    }

    @Test
    public void testRepeatedDouble() throws Exception {
        String inputTable = "cp.`parquet/repeated_double_data.json`";
        runTestAndValidate("*", "*", inputTable, "repeated_double_parquet");
    }

    @Test
    public void testRepeatedLong() throws Exception {
        String inputTable = "cp.`parquet/repeated_integer_data.json`";
        runTestAndValidate("*", "*", inputTable, "repeated_int_parquet");
    }

    @Test
    public void testRepeatedBool() throws Exception {
        String inputTable = "cp.`parquet/repeated_bool_data.json`";
        runTestAndValidate("*", "*", inputTable, "repeated_bool_parquet");
    }

    @Test
    public void testNullReadWrite() throws Exception {
        String inputTable = "cp.`parquet/null_test_data.json`";
        runTestAndValidate("*", "*", inputTable, "nullable_test");
    }

    @Test
    public void testDecimal() throws Exception {
        String selection = "cast(salary as decimal(8,2)) as decimal8, cast(salary as decimal(15,2)) as decimal15, " + "cast(salary as decimal(24,2)) as decimal24, cast(salary as decimal(38,2)) as decimal38";
        String validateSelection = "decimal8, decimal15, decimal24, decimal38";
        String inputTable = "cp.`employee.json`";
        // DRILL-5833: The "old" writer had a decimal bug, but the new one
        // did not. The one used was random. Force the test to run both
        // the old and new readers.
        try {
            BaseTestQuery.alterSession(PARQUET_NEW_RECORD_READER, true);
            runTestAndValidate(selection, validateSelection, inputTable, "parquet_decimal");
            BaseTestQuery.alterSession(PARQUET_NEW_RECORD_READER, false);
            runTestAndValidate(selection, validateSelection, inputTable, "parquet_decimal");
        } finally {
            BaseTestQuery.resetSessionOption(PARQUET_NEW_RECORD_READER);
        }
    }

    @Test
    public void testMulipleRowGroups() throws Exception {
        try {
            BaseTestQuery.alterSession(PARQUET_BLOCK_SIZE, (1024 * 1024));
            String selection = "mi";
            String inputTable = "cp.`customer.json`";
            runTestAndValidate(selection, selection, inputTable, "foodmart_customer_parquet");
        } finally {
            BaseTestQuery.resetSessionOption(PARQUET_BLOCK_SIZE);
        }
    }

    @Test
    public void testDate() throws Exception {
        String selection = "cast(hire_date as DATE) as hire_date";
        String validateSelection = "hire_date";
        String inputTable = "cp.`employee.json`";
        runTestAndValidate(selection, validateSelection, inputTable, "foodmart_employee_parquet");
    }

    @Test
    public void testBoolean() throws Exception {
        String selection = "true as x, false as y";
        String validateSelection = "x, y";
        String inputTable = "cp.`tpch/region.parquet`";
        runTestAndValidate(selection, validateSelection, inputTable, "region_boolean_parquet");
    }

    // DRILL-2030
    @Test
    public void testWriterWithStarAndExp() throws Exception {
        String selection = " *, r_regionkey + 1 r_regionkey2";
        String validateSelection = "r_regionkey, r_name, r_comment, r_regionkey + 1 r_regionkey2";
        String inputTable = "cp.`tpch/region.parquet`";
        runTestAndValidate(selection, validateSelection, inputTable, "region_star_exp");
    }

    // DRILL-2458
    @Test
    public void testWriterWithStarAndRegluarCol() throws Exception {
        String outputFile = "region_sort";
        String ctasStmt = ("create table " + outputFile) + " as select *, r_regionkey + 1 as key1 from cp.`tpch/region.parquet` order by r_name";
        String query = "select r_regionkey, r_name, r_comment, r_regionkey +1 as key1 from cp.`tpch/region.parquet` order by r_name";
        String queryFromWriteOut = "select * from " + outputFile;
        try {
            BaseTestQuery.test("use dfs.tmp");
            BaseTestQuery.test(ctasStmt);
            BaseTestQuery.testBuilder().ordered().sqlQuery(queryFromWriteOut).sqlBaselineQuery(query).build().run();
        } finally {
            TestParquetWriter.deleteTableIfExists(outputFile);
        }
    }

    @Test
    public void testWriteDecimal() throws Exception {
        String outputTable = "decimal_test";
        try {
            BaseTestQuery.test(("use dfs.tmp; " + (("create table %s as select " + "cast('1.2' as decimal(38, 2)) col1, cast('1.2' as decimal(28, 2)) col2 ") + "from cp.`employee.json` limit 1")), outputTable);
            BigDecimal result = new BigDecimal("1.20");
            BaseTestQuery.testBuilder().unOrdered().sqlQuery("select col1, col2 from %s ", outputTable).baselineColumns("col1", "col2").baselineValues(result, result).go();
        } finally {
            TestParquetWriter.deleteTableIfExists(outputTable);
        }
    }

    // DRILL-2341
    @Test
    @Category(UnlikelyTest.class)
    public void tableSchemaWhenSelectFieldsInDef_SelectFieldsInView() throws Exception {
        final String newTblName = "testTableOutputSchema";
        try {
            BaseTestQuery.test(("CREATE TABLE dfs.tmp.%s(id, name, bday) AS SELECT " + ((("cast(`employee_id` as integer), " + "cast(`full_name` as varchar(100)), ") + "cast(`birth_date` as date) ") + "FROM cp.`employee.json` ORDER BY `employee_id` LIMIT 1")), newTblName);
            BaseTestQuery.testBuilder().unOrdered().sqlQuery("SELECT * FROM dfs.tmp.`%s`", newTblName).baselineColumns("id", "name", "bday").baselineValues(1, "Sheri Nowmer", LocalDate.parse("1961-08-26")).go();
        } finally {
            TestParquetWriter.deleteTableIfExists(newTblName);
        }
    }

    /* Method tests CTAS with interval data type. We also verify reading back the data to ensure we
    have written the correct type. For every CTAS operation we use both the readers to verify results.
     */
    @Test
    public void testCTASWithIntervalTypes() throws Exception {
        BaseTestQuery.test("use dfs.tmp");
        String tableName = "drill_1980_t1";
        // test required interval day type
        BaseTestQuery.test(("create table %s as " + ((("select " + "interval '10 20:30:40.123' day to second col1, ") + "interval '-1000000000 20:12:23.999' day(10) to second col2 ") + "from cp.`employee.json` limit 2")), tableName);
        Period row1Col1 = new Period(0, 0, 0, 10, 0, 0, 0, 73840123);
        Period row1Col2 = new Period(0, 0, 0, (-1000000000), 0, 0, 0, (-72743999));
        testParquetReaderHelper(tableName, row1Col1, row1Col2, row1Col1, row1Col2);
        tableName = "drill_1980_2";
        // test required interval year type
        BaseTestQuery.test(("create table %s as " + ((("select " + "interval '10-2' year to month col1, ") + "interval '-100-8' year(3) to month col2 ") + "from cp.`employee.json` limit 2")), tableName);
        row1Col1 = new Period(0, 122, 0, 0, 0, 0, 0, 0);
        row1Col2 = new Period(0, (-1208), 0, 0, 0, 0, 0, 0);
        testParquetReaderHelper(tableName, row1Col1, row1Col2, row1Col1, row1Col2);
        // test nullable interval year type
        tableName = "drill_1980_t3";
        BaseTestQuery.test(("create table %s as " + ((("select " + "cast (intervalyear_col as interval year) col1,") + "cast(intervalyear_col as interval year) + interval '2' year col2 ") + "from cp.`parquet/alltypes.json` where tinyint_col = 1 or tinyint_col = 2")), tableName);
        row1Col1 = new Period(0, 12, 0, 0, 0, 0, 0, 0);
        row1Col2 = new Period(0, 36, 0, 0, 0, 0, 0, 0);
        Period row2Col1 = new Period(0, 24, 0, 0, 0, 0, 0, 0);
        Period row2Col2 = new Period(0, 48, 0, 0, 0, 0, 0, 0);
        testParquetReaderHelper(tableName, row1Col1, row1Col2, row2Col1, row2Col2);
        // test nullable interval day type
        tableName = "drill_1980_t4";
        BaseTestQuery.test(("create table %s as " + ((("select " + "cast(intervalday_col as interval day) col1, ") + "cast(intervalday_col as interval day) + interval '1' day col2 ") + "from cp.`parquet/alltypes.json` where tinyint_col = 1 or tinyint_col = 2")), tableName);
        row1Col1 = new Period(0, 0, 0, 1, 0, 0, 0, 0);
        row1Col2 = new Period(0, 0, 0, 2, 0, 0, 0, 0);
        row2Col1 = new Period(0, 0, 0, 2, 0, 0, 0, 0);
        row2Col2 = new Period(0, 0, 0, 3, 0, 0, 0, 0);
        testParquetReaderHelper(tableName, row1Col1, row1Col2, row2Col1, row2Col2);
    }

    /* Impala encodes timestamp values as int96 fields. Test the reading of an int96 field with two converters:
    the first one converts parquet INT96 into drill VARBINARY and the second one (works while
    store.parquet.reader.int96_as_timestamp option is enabled) converts parquet INT96 into drill TIMESTAMP.
     */
    @Test
    public void testImpalaParquetInt96() throws Exception {
        compareParquetReadersColumnar("field_impala_ts", "cp.`parquet/int96_impala_1.parquet`");
        try {
            BaseTestQuery.alterSession(PARQUET_READER_INT96_AS_TIMESTAMP, true);
            compareParquetReadersColumnar("field_impala_ts", "cp.`parquet/int96_impala_1.parquet`");
        } finally {
            BaseTestQuery.resetSessionOption(PARQUET_READER_INT96_AS_TIMESTAMP);
        }
    }

    /* Test the reading of a binary field as drill varbinary where data is in dictionary _and_ non-dictionary encoded pages */
    @Test
    public void testImpalaParquetBinaryAsVarBinary_DictChange() throws Exception {
        compareParquetReadersColumnar("field_impala_ts", "cp.`parquet/int96_dict_change.parquet`");
    }

    /* Test the reading of a binary field as drill timestamp where data is in dictionary _and_ non-dictionary encoded pages */
    @Test
    public void testImpalaParquetBinaryAsTimeStamp_DictChange() throws Exception {
        try {
            BaseTestQuery.testBuilder().sqlQuery("select min(int96_ts) date_value from dfs.`parquet/int96_dict_change`").optionSettingQueriesForTestQuery("alter session set `%s` = true", PARQUET_READER_INT96_AS_TIMESTAMP).ordered().baselineColumns("date_value").baselineValues(TestBuilder.convertToLocalDateTime("1970-01-01 00:00:01.000")).build().run();
        } finally {
            BaseTestQuery.resetSessionOption(PARQUET_READER_INT96_AS_TIMESTAMP);
        }
    }

    @Test
    public void testSparkParquetBinaryAsTimeStamp_DictChange() throws Exception {
        try {
            BaseTestQuery.testBuilder().sqlQuery("select distinct run_date from cp.`parquet/spark-generated-int96-timestamp.snappy.parquet`").optionSettingQueriesForTestQuery("alter session set `%s` = true", PARQUET_READER_INT96_AS_TIMESTAMP).ordered().baselineColumns("run_date").baselineValues(TestBuilder.convertToLocalDateTime("2017-12-06 16:38:43.988")).build().run();
        } finally {
            BaseTestQuery.resetSessionOption(PARQUET_READER_INT96_AS_TIMESTAMP);
        }
    }

    /* Test the conversion from int96 to impala timestamp */
    @Test
    public void testTimestampImpalaConvertFrom() throws Exception {
        compareParquetReadersColumnar("convert_from(field_impala_ts, 'TIMESTAMP_IMPALA')", "cp.`parquet/int96_impala_1.parquet`");
    }

    /* Test reading parquet Int96 as TimeStamp and comparing obtained values with the
    old results (reading the same values as VarBinary and convert_fromTIMESTAMP_IMPALA function using)
     */
    @Test
    public void testImpalaParquetTimestampInt96AsTimeStamp() throws Exception {
        try {
            BaseTestQuery.alterSession(PARQUET_NEW_RECORD_READER, false);
            compareParquetInt96Converters("field_impala_ts", "cp.`parquet/int96_impala_1.parquet`");
            BaseTestQuery.alterSession(PARQUET_NEW_RECORD_READER, true);
            compareParquetInt96Converters("field_impala_ts", "cp.`parquet/int96_impala_1.parquet`");
        } finally {
            BaseTestQuery.resetSessionOption(PARQUET_NEW_RECORD_READER);
        }
    }

    /* Test a file with partitions and an int96 column. (Data generated using Hive) */
    @Test
    public void testImpalaParquetInt96Partitioned() throws Exception {
        compareParquetReadersColumnar("timestamp_field", "cp.`parquet/part1/hive_all_types.parquet`");
    }

    /* Test the conversion from int96 to impala timestamp with hive data including nulls. Validate against old reader */
    @Test
    public void testHiveParquetTimestampAsInt96_compare() throws Exception {
        compareParquetReadersColumnar("convert_from(timestamp_field, 'TIMESTAMP_IMPALA')", "cp.`parquet/part1/hive_all_types.parquet`");
    }

    /* Test the conversion from int96 to impala timestamp with hive data including nulls. Validate against expected values */
    @Test
    public void testHiveParquetTimestampAsInt96_basic() throws Exception {
        BaseTestQuery.testBuilder().unOrdered().sqlQuery(("SELECT convert_from(timestamp_field, 'TIMESTAMP_IMPALA')  as timestamp_field " + "from cp.`parquet/part1/hive_all_types.parquet` ")).baselineColumns("timestamp_field").baselineValues(TestBuilder.convertToLocalDateTime("2013-07-06 00:01:00")).baselineValues(((Object) (null))).go();
    }

    /* The following test boundary conditions for null values occurring on page boundaries. All files have at least one dictionary
    encoded page for all columns
     */
    @Test
    public void testAllNulls() throws Exception {
        compareParquetReadersColumnar("c_varchar, c_integer, c_bigint, c_float, c_double, c_date, c_time, c_timestamp, c_boolean", "cp.`parquet/all_nulls.parquet`");
    }

    @Test
    public void testNoNulls() throws Exception {
        compareParquetReadersColumnar("c_varchar, c_integer, c_bigint, c_float, c_double, c_date, c_time, c_timestamp, c_boolean", "cp.`parquet/no_nulls.parquet`");
    }

    @Test
    public void testFirstPageAllNulls() throws Exception {
        compareParquetReadersColumnar("c_varchar, c_integer, c_bigint, c_float, c_double, c_date, c_time, c_timestamp, c_boolean", "cp.`parquet/first_page_all_nulls.parquet`");
    }

    @Test
    public void testLastPageAllNulls() throws Exception {
        compareParquetReadersColumnar("c_varchar, c_integer, c_bigint, c_float, c_double, c_date, c_time, c_timestamp, c_boolean", "cp.`parquet/first_page_all_nulls.parquet`");
    }

    @Test
    public void testFirstPageOneNull() throws Exception {
        compareParquetReadersColumnar("c_varchar, c_integer, c_bigint, c_float, c_double, c_date, c_time, c_timestamp, c_boolean", "cp.`parquet/first_page_one_null.parquet`");
    }

    @Test
    public void testLastPageOneNull() throws Exception {
        compareParquetReadersColumnar("c_varchar, c_integer, c_bigint, c_float, c_double, c_date, c_time, c_timestamp, c_boolean", "cp.`parquet/last_page_one_null.parquet`");
    }

    @Test
    public void testTPCHReadWriteGzip() throws Exception {
        try {
            BaseTestQuery.alterSession(PARQUET_WRITER_COMPRESSION_TYPE, "gzip");
            String inputTable = "cp.`tpch/supplier.parquet`";
            runTestAndValidate("*", "*", inputTable, "suppkey_parquet_dict_gzip");
        } finally {
            BaseTestQuery.resetSessionOption(PARQUET_WRITER_COMPRESSION_TYPE);
        }
    }

    @Test
    public void testTPCHReadWriteSnappy() throws Exception {
        try {
            BaseTestQuery.alterSession(PARQUET_WRITER_COMPRESSION_TYPE, "snappy");
            String inputTable = "cp.`supplier_snappy.parquet`";
            runTestAndValidate("*", "*", inputTable, "suppkey_parquet_dict_snappy");
        } finally {
            BaseTestQuery.resetSessionOption(PARQUET_WRITER_COMPRESSION_TYPE);
        }
    }

    // DRILL-5097
    @Test
    @Category(UnlikelyTest.class)
    public void testInt96TimeStampValueWidth() throws Exception {
        try {
            BaseTestQuery.testBuilder().unOrdered().sqlQuery(("select c, d from cp.`parquet/data.snappy.parquet` " + "where `a` is not null and `c` is not null and `d` is not null")).optionSettingQueriesForTestQuery("alter session set `%s` = true", PARQUET_READER_INT96_AS_TIMESTAMP).baselineColumns("c", "d").baselineValues(LocalDate.parse("2012-12-15"), TestBuilder.convertToLocalDateTime("2016-04-24 20:06:28")).baselineValues(LocalDate.parse("2011-07-09"), TestBuilder.convertToLocalDateTime("2015-04-15 22:35:49")).build().run();
        } finally {
            BaseTestQuery.resetSessionOption(PARQUET_READER_INT96_AS_TIMESTAMP);
        }
    }

    @Test
    public void testWriteDecimalIntBigIntFixedLen() throws Exception {
        String tableName = "decimalIntBigIntFixedLen";
        try {
            BaseTestQuery.alterSession(PARQUET_WRITER_LOGICAL_TYPE_FOR_DECIMALS, FIXED_LEN_BYTE_ARRAY.name());
            BaseTestQuery.test(("create table dfs.tmp.%s as\n" + (("select cast(\'123456.789\' as decimal(9, 3)) as decInt,\n" + "cast(\'123456.789123456789\' as decimal(18, 12)) as decBigInt,\n") + "cast('123456.789123456789' as decimal(19, 12)) as fixedLen")), tableName);
            checkTableTypes(tableName, ImmutableList.of(Pair.of("decInt", INT32), Pair.of("decBigInt", INT64), Pair.of("fixedLen", FIXED_LEN_BYTE_ARRAY)), true);
            BaseTestQuery.testBuilder().sqlQuery("select * from dfs.tmp.%s", tableName).unOrdered().baselineColumns("decInt", "decBigInt", "fixedLen").baselineValues(new BigDecimal("123456.789"), new BigDecimal("123456.789123456789"), new BigDecimal("123456.789123456789")).go();
        } finally {
            BaseTestQuery.resetSessionOption(PARQUET_WRITER_USE_PRIMITIVE_TYPES_FOR_DECIMALS);
            BaseTestQuery.resetSessionOption(PARQUET_WRITER_LOGICAL_TYPE_FOR_DECIMALS);
            BaseTestQuery.test("drop table if exists dfs.tmp.%s", tableName);
        }
    }

    @Test
    public void testWriteDecimalIntBigIntBinary() throws Exception {
        String tableName = "decimalIntBigIntBinary";
        try {
            BaseTestQuery.alterSession(PARQUET_WRITER_USE_PRIMITIVE_TYPES_FOR_DECIMALS, true);
            BaseTestQuery.alterSession(PARQUET_WRITER_LOGICAL_TYPE_FOR_DECIMALS, BINARY.name());
            BaseTestQuery.test(("create table dfs.tmp.%s as\n" + (("select cast(\'123456.789\' as decimal(9, 3)) as decInt,\n" + "cast(\'123456.789123456789\' as decimal(18, 12)) as decBigInt,\n") + "cast('123456.789123456789' as decimal(19, 12)) as binCol")), tableName);
            checkTableTypes(tableName, ImmutableList.of(Pair.of("decInt", INT32), Pair.of("decBigInt", INT64), Pair.of("binCol", BINARY)), true);
            BaseTestQuery.testBuilder().sqlQuery("select * from dfs.tmp.%s", tableName).unOrdered().baselineColumns("decInt", "decBigInt", "binCol").baselineValues(new BigDecimal("123456.789"), new BigDecimal("123456.789123456789"), new BigDecimal("123456.789123456789")).go();
        } finally {
            BaseTestQuery.resetSessionOption(PARQUET_WRITER_USE_PRIMITIVE_TYPES_FOR_DECIMALS);
            BaseTestQuery.resetSessionOption(PARQUET_WRITER_LOGICAL_TYPE_FOR_DECIMALS);
            BaseTestQuery.test("drop table if exists dfs.tmp.%s", tableName);
        }
    }

    @Test
    public void testWriteDecimalFixedLenOnly() throws Exception {
        String tableName = "decimalFixedLenOnly";
        try {
            BaseTestQuery.alterSession(PARQUET_WRITER_USE_PRIMITIVE_TYPES_FOR_DECIMALS, false);
            BaseTestQuery.alterSession(PARQUET_WRITER_LOGICAL_TYPE_FOR_DECIMALS, FIXED_LEN_BYTE_ARRAY.name());
            BaseTestQuery.test(("create table dfs.tmp.%s as\n" + (("select cast(\'123456.789\' as decimal(9, 3)) as decInt,\n" + "cast(\'123456.789123456789\' as decimal(18, 12)) as decBigInt,\n") + "cast('123456.789123456789' as decimal(19, 12)) as fixedLen")), tableName);
            checkTableTypes(tableName, ImmutableList.of(Pair.of("decInt", FIXED_LEN_BYTE_ARRAY), Pair.of("decBigInt", FIXED_LEN_BYTE_ARRAY), Pair.of("fixedLen", FIXED_LEN_BYTE_ARRAY)), true);
            BaseTestQuery.testBuilder().sqlQuery("select * from dfs.tmp.%s", tableName).unOrdered().baselineColumns("decInt", "decBigInt", "fixedLen").baselineValues(new BigDecimal("123456.789"), new BigDecimal("123456.789123456789"), new BigDecimal("123456.789123456789")).go();
        } finally {
            BaseTestQuery.resetSessionOption(PARQUET_WRITER_USE_PRIMITIVE_TYPES_FOR_DECIMALS);
            BaseTestQuery.resetSessionOption(PARQUET_WRITER_LOGICAL_TYPE_FOR_DECIMALS);
            BaseTestQuery.test("drop table if exists dfs.tmp.%s", tableName);
        }
    }

    @Test
    public void testWriteDecimalBinaryOnly() throws Exception {
        String tableName = "decimalBinaryOnly";
        try {
            BaseTestQuery.alterSession(PARQUET_WRITER_USE_PRIMITIVE_TYPES_FOR_DECIMALS, false);
            BaseTestQuery.alterSession(PARQUET_WRITER_LOGICAL_TYPE_FOR_DECIMALS, BINARY.name());
            BaseTestQuery.test(("create table dfs.tmp.%s as\n" + (("select cast(\'123456.789\' as decimal(9, 3)) as decInt,\n" + "cast(\'123456.789123456789\' as decimal(18, 12)) as decBigInt,\n") + "cast('123456.789123456789' as decimal(19, 12)) as binCol")), tableName);
            checkTableTypes(tableName, ImmutableList.of(Pair.of("decInt", BINARY), Pair.of("decBigInt", BINARY), Pair.of("binCol", BINARY)), true);
            BaseTestQuery.testBuilder().sqlQuery("select * from dfs.tmp.%s", tableName).unOrdered().baselineColumns("decInt", "decBigInt", "binCol").baselineValues(new BigDecimal("123456.789"), new BigDecimal("123456.789123456789"), new BigDecimal("123456.789123456789")).go();
        } finally {
            BaseTestQuery.resetSessionOption(PARQUET_WRITER_USE_PRIMITIVE_TYPES_FOR_DECIMALS);
            BaseTestQuery.resetSessionOption(PARQUET_WRITER_LOGICAL_TYPE_FOR_DECIMALS);
            BaseTestQuery.test("drop table if exists dfs.tmp.%s", tableName);
        }
    }

    @Test
    public void testWriteDecimalIntBigIntRepeated() throws Exception {
        String tableName = "decimalIntBigIntRepeated";
        JsonStringArrayList<BigDecimal> ints = new JsonStringArrayList();
        ints.add(new BigDecimal("999999.999"));
        ints.add(new BigDecimal("-999999.999"));
        ints.add(new BigDecimal("0.000"));
        JsonStringArrayList<BigDecimal> longs = new JsonStringArrayList();
        longs.add(new BigDecimal("999999999.999999999"));
        longs.add(new BigDecimal("-999999999.999999999"));
        longs.add(new BigDecimal("0.000000000"));
        JsonStringArrayList<BigDecimal> fixedLen = new JsonStringArrayList();
        fixedLen.add(new BigDecimal("999999999999.999999"));
        fixedLen.add(new BigDecimal("-999999999999.999999"));
        fixedLen.add(new BigDecimal("0.000000"));
        try {
            BaseTestQuery.alterSession(PARQUET_WRITER_USE_PRIMITIVE_TYPES_FOR_DECIMALS, true);
            BaseTestQuery.alterSession(PARQUET_WRITER_LOGICAL_TYPE_FOR_DECIMALS, FIXED_LEN_BYTE_ARRAY.name());
            BaseTestQuery.test(("create table dfs.tmp.%s as\n" + "select * from cp.`parquet/repeatedIntLondFixedLenBinaryDecimal.parquet`"), tableName);
            checkTableTypes(tableName, ImmutableList.of(Pair.of("decimal_int32", INT32), Pair.of("decimal_int64", INT64), Pair.of("decimal_fixedLen", INT64), Pair.of("decimal_binary", INT64)), true);
            BaseTestQuery.testBuilder().sqlQuery("select * from dfs.tmp.%s", tableName).unOrdered().baselineColumns("decimal_int32", "decimal_int64", "decimal_fixedLen", "decimal_binary").baselineValues(ints, longs, fixedLen, fixedLen).go();
        } finally {
            BaseTestQuery.resetSessionOption(PARQUET_WRITER_USE_PRIMITIVE_TYPES_FOR_DECIMALS);
            BaseTestQuery.resetSessionOption(PARQUET_WRITER_LOGICAL_TYPE_FOR_DECIMALS);
            BaseTestQuery.test("drop table if exists dfs.tmp.%s", tableName);
        }
    }

    @Test
    public void testWriteDecimalFixedLenRepeated() throws Exception {
        String tableName = "decimalFixedLenRepeated";
        JsonStringArrayList<BigDecimal> ints = new JsonStringArrayList();
        ints.add(new BigDecimal("999999.999"));
        ints.add(new BigDecimal("-999999.999"));
        ints.add(new BigDecimal("0.000"));
        JsonStringArrayList<BigDecimal> longs = new JsonStringArrayList();
        longs.add(new BigDecimal("999999999.999999999"));
        longs.add(new BigDecimal("-999999999.999999999"));
        longs.add(new BigDecimal("0.000000000"));
        JsonStringArrayList<BigDecimal> fixedLen = new JsonStringArrayList();
        fixedLen.add(new BigDecimal("999999999999.999999"));
        fixedLen.add(new BigDecimal("-999999999999.999999"));
        fixedLen.add(new BigDecimal("0.000000"));
        try {
            BaseTestQuery.alterSession(PARQUET_WRITER_USE_PRIMITIVE_TYPES_FOR_DECIMALS, false);
            BaseTestQuery.alterSession(PARQUET_WRITER_LOGICAL_TYPE_FOR_DECIMALS, FIXED_LEN_BYTE_ARRAY.name());
            BaseTestQuery.test(("create table dfs.tmp.%s as\n" + "select * from cp.`parquet/repeatedIntLondFixedLenBinaryDecimal.parquet`"), tableName);
            checkTableTypes(tableName, ImmutableList.of(Pair.of("decimal_int32", FIXED_LEN_BYTE_ARRAY), Pair.of("decimal_int64", FIXED_LEN_BYTE_ARRAY), Pair.of("decimal_fixedLen", FIXED_LEN_BYTE_ARRAY), Pair.of("decimal_binary", FIXED_LEN_BYTE_ARRAY)), true);
            BaseTestQuery.testBuilder().sqlQuery("select * from dfs.tmp.%s", tableName).unOrdered().baselineColumns("decimal_int32", "decimal_int64", "decimal_fixedLen", "decimal_binary").baselineValues(ints, longs, fixedLen, fixedLen).go();
        } finally {
            BaseTestQuery.resetSessionOption(PARQUET_WRITER_USE_PRIMITIVE_TYPES_FOR_DECIMALS);
            BaseTestQuery.resetSessionOption(PARQUET_WRITER_LOGICAL_TYPE_FOR_DECIMALS);
            BaseTestQuery.test("drop table if exists dfs.tmp.%s", tableName);
        }
    }

    @Test
    public void testWriteDecimalBinaryRepeated() throws Exception {
        String tableName = "decimalBinaryRepeated";
        JsonStringArrayList<BigDecimal> ints = new JsonStringArrayList();
        ints.add(new BigDecimal("999999.999"));
        ints.add(new BigDecimal("-999999.999"));
        ints.add(new BigDecimal("0.000"));
        JsonStringArrayList<BigDecimal> longs = new JsonStringArrayList();
        longs.add(new BigDecimal("999999999.999999999"));
        longs.add(new BigDecimal("-999999999.999999999"));
        longs.add(new BigDecimal("0.000000000"));
        JsonStringArrayList<BigDecimal> fixedLen = new JsonStringArrayList();
        fixedLen.add(new BigDecimal("999999999999.999999"));
        fixedLen.add(new BigDecimal("-999999999999.999999"));
        fixedLen.add(new BigDecimal("0.000000"));
        try {
            BaseTestQuery.alterSession(PARQUET_WRITER_USE_PRIMITIVE_TYPES_FOR_DECIMALS, false);
            BaseTestQuery.alterSession(PARQUET_WRITER_LOGICAL_TYPE_FOR_DECIMALS, BINARY.name());
            BaseTestQuery.test(("create table dfs.tmp.%s as\n" + "select * from cp.`parquet/repeatedIntLondFixedLenBinaryDecimal.parquet`"), tableName);
            checkTableTypes(tableName, ImmutableList.of(Pair.of("decimal_int32", BINARY), Pair.of("decimal_int64", BINARY), Pair.of("decimal_fixedLen", BINARY), Pair.of("decimal_binary", BINARY)), true);
            BaseTestQuery.testBuilder().sqlQuery("select * from dfs.tmp.%s", tableName).unOrdered().baselineColumns("decimal_int32", "decimal_int64", "decimal_fixedLen", "decimal_binary").baselineValues(ints, longs, fixedLen, fixedLen).go();
        } finally {
            BaseTestQuery.resetSessionOption(PARQUET_WRITER_USE_PRIMITIVE_TYPES_FOR_DECIMALS);
            BaseTestQuery.resetSessionOption(PARQUET_WRITER_LOGICAL_TYPE_FOR_DECIMALS);
            BaseTestQuery.test("drop table if exists dfs.tmp.%s", tableName);
        }
    }
}

