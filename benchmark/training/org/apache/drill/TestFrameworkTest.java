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
package org.apache.drill;


import PlannerSettings.ENABLE_DECIMAL_DATA_TYPE_KEY;
import TypeProtos.DataMode.REQUIRED;
import TypeProtos.MajorType;
import TypeProtos.MinorType.BIGINT;
import TypeProtos.MinorType.INT;
import TypeProtos.MinorType.VARCHAR;
import TypeProtos.MinorType.VARDECIMAL;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.drill.common.expression.SchemaPath;
import org.apache.drill.common.types.TypeProtos;
import org.apache.drill.common.types.Types;
import org.apache.drill.shaded.guava.com.google.common.collect.Lists;
import org.apache.drill.test.BaseTestQuery;
import org.apache.drill.test.TestBuilder;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


// TODO - update framework to remove any dependency on the Drill engine for reading baseline result sets
// currently using it with the assumption that the csv and json readers are well tested, and handling diverse
// types in the test framework would require doing some redundant work to enable casting outside of Drill or
// some better tooling to generate parquet files that have all of the parquet types
public class TestFrameworkTest extends BaseTestQuery {
    private static String CSV_COLS = " cast(columns[0] as bigint) employee_id, columns[1] as first_name, columns[2] as last_name ";

    @Test(expected = AssertionError.class)
    public void testSchemaTestBuilderSetInvalidBaselineValues() throws Exception {
        final String query = "SELECT ltrim('drill') as col FROM (VALUES(1)) limit 0";
        List<Pair<SchemaPath, TypeProtos.MajorType>> expectedSchema = Lists.newArrayList();
        TypeProtos.MajorType majorType = MajorType.newBuilder().setMinorType(VARCHAR).setMode(REQUIRED).build();
        expectedSchema.add(Pair.of(SchemaPath.getSimplePath("col"), majorType));
        BaseTestQuery.testBuilder().sqlQuery(query).schemaBaseLine(expectedSchema).baselineValues(new Object[0]).build().run();
    }

    @Test(expected = AssertionError.class)
    public void testSchemaTestBuilderSetInvalidBaselineRecords() throws Exception {
        final String query = "SELECT ltrim('drill') as col FROM (VALUES(1)) limit 0";
        List<Pair<SchemaPath, TypeProtos.MajorType>> expectedSchema = Lists.newArrayList();
        TypeProtos.MajorType majorType = MajorType.newBuilder().setMinorType(VARCHAR).setMode(REQUIRED).build();
        expectedSchema.add(Pair.of(SchemaPath.getSimplePath("col"), majorType));
        BaseTestQuery.testBuilder().sqlQuery(query).schemaBaseLine(expectedSchema).baselineRecords(Collections.<Map<String, Object>>emptyList()).build().run();
    }

    @Test(expected = AssertionError.class)
    public void testSchemaTestBuilderSetInvalidBaselineColumns() throws Exception {
        final String query = "SELECT ltrim('drill') as col FROM (VALUES(1)) limit 0";
        List<Pair<SchemaPath, TypeProtos.MajorType>> expectedSchema = Lists.newArrayList();
        TypeProtos.MajorType majorType = MajorType.newBuilder().setMinorType(VARCHAR).setMode(REQUIRED).build();
        expectedSchema.add(Pair.of(SchemaPath.getSimplePath("col"), majorType));
        BaseTestQuery.testBuilder().sqlQuery(query).baselineColumns("col").schemaBaseLine(expectedSchema).build().run();
    }

    @Test
    public void testCSVVerification() throws Exception {
        BaseTestQuery.testBuilder().sqlQuery("select employee_id, first_name, last_name from cp.`testframework/small_test_data.json`").ordered().csvBaselineFile("testframework/small_test_data.tsv").baselineTypes(BIGINT, VARCHAR, VARCHAR).baselineColumns("employee_id", "first_name", "last_name").build().run();
    }

    @Test
    public void testBaselineValsVerification() throws Exception {
        BaseTestQuery.testBuilder().sqlQuery("select employee_id, first_name, last_name from cp.`testframework/small_test_data.json` limit 1").ordered().baselineColumns("employee_id", "first_name", "last_name").baselineValues(12L, "Jewel", "Creek").build().run();
        BaseTestQuery.testBuilder().sqlQuery("select employee_id, first_name, last_name from cp.`testframework/small_test_data.json` limit 1").unOrdered().baselineColumns("employee_id", "first_name", "last_name").baselineValues(12L, "Jewel", "Creek").build().run();
    }

    @Test
    public void testDecimalBaseline() throws Exception {
        try {
            BaseTestQuery.test(String.format("alter session set `%s` = true", ENABLE_DECIMAL_DATA_TYPE_KEY));
            // type information can be provided explicitly
            BaseTestQuery.testBuilder().sqlQuery("select cast(dec_col as decimal(38,2)) dec_col from cp.`testframework/decimal_test.json`").unOrdered().csvBaselineFile("testframework/decimal_test.tsv").baselineTypes(Types.withScaleAndPrecision(VARDECIMAL, REQUIRED, 2, 38)).baselineColumns("dec_col").build().run();
            // type information can also be left out, this will prompt the result types of the test query to drive the
            // interpretation of the test file
            BaseTestQuery.testBuilder().sqlQuery("select cast(dec_col as decimal(38,2)) dec_col from cp.`testframework/decimal_test.json`").unOrdered().csvBaselineFile("testframework/decimal_test.tsv").baselineColumns("dec_col").build().run();
            // Or you can provide explicit values to the builder itself to avoid going through the drill engine at all to
            // populate the baseline results
            BaseTestQuery.testBuilder().sqlQuery("select cast(dec_col as decimal(38,2)) dec_col from cp.`testframework/decimal_test.json`").unOrdered().baselineColumns("dec_col").baselineValues(new BigDecimal("3.70")).build().run();
        } finally {
            BaseTestQuery.test(String.format("alter session set `%s` = false", ENABLE_DECIMAL_DATA_TYPE_KEY));
        }
    }

    @Test
    public void testMapOrdering() throws Exception {
        BaseTestQuery.testBuilder().sqlQuery("select * from cp.`testframework/map_reordering.json`").unOrdered().jsonBaselineFile("testframework/map_reordering2.json").build().run();
    }

    @Test
    public void testBaselineValsVerificationWithNulls() throws Exception {
        BaseTestQuery.testBuilder().sqlQuery("select * from cp.`store/json/json_simple_with_null.json`").ordered().baselineColumns("a", "b").baselineValues(5L, 10L).baselineValues(7L, null).baselineValues(null, null).baselineValues(9L, 11L).build().run();
        BaseTestQuery.testBuilder().sqlQuery("select * from cp.`store/json/json_simple_with_null.json`").unOrdered().baselineColumns("a", "b").baselineValues(5L, 10L).baselineValues(9L, 11L).baselineValues(7L, null).baselineValues(null, null).build().run();
    }

    @Test
    public void testBaselineValsVerificationWithComplexAndNulls() throws Exception {
        BaseTestQuery.testBuilder().sqlQuery("select * from cp.`jsoninput/input2.json` limit 1").ordered().baselineColumns("integer", "float", "x", "z", "l", "rl").baselineValues(2010L, 17.4, TestBuilder.mapOf("y", "kevin", "z", "paul"), TestBuilder.listOf(TestBuilder.mapOf("orange", "yellow", "pink", "red"), TestBuilder.mapOf("pink", "purple")), TestBuilder.listOf(4L, 2L), TestBuilder.listOf(TestBuilder.listOf(2L, 1L), TestBuilder.listOf(4L, 6L))).build().run();
    }

    @Test
    public void testCSVVerification_missing_records_fails() throws Exception {
        try {
            BaseTestQuery.testBuilder().sqlQuery("select employee_id, first_name, last_name from cp.`testframework/small_test_data.json`").ordered().csvBaselineFile("testframework/small_test_data_extra.tsv").baselineTypes(BIGINT, VARCHAR, VARCHAR).baselineColumns("employee_id", "first_name", "last_name").build().run();
        } catch (AssertionError ex) {
            Assert.assertEquals("Incorrect number of rows returned by query. expected:<7> but was:<5>", ex.getMessage());
            // this indicates successful completion of the test
            return;
        }
        throw new Exception("Test framework verification failed, expected failure on missing records.");
    }

    @Test
    public void testCSVVerification_extra_records_fails() throws Exception {
        try {
            BaseTestQuery.testBuilder().sqlQuery("select %s from cp.`testframework/small_test_data_extra.tsv`", TestFrameworkTest.CSV_COLS).ordered().csvBaselineFile("testframework/small_test_data.tsv").baselineTypes(BIGINT, VARCHAR, VARCHAR).baselineColumns("employee_id", "first_name", "last_name").build().run();
        } catch (AssertionError ex) {
            Assert.assertEquals("Incorrect number of rows returned by query. expected:<5> but was:<7>", ex.getMessage());
            // this indicates successful completion of the test
            return;
        }
        throw new Exception("Test framework verification failed, expected failure for extra records.");
    }

    @Test
    public void testCSVVerification_extra_column_fails() throws Exception {
        try {
            BaseTestQuery.testBuilder().sqlQuery((("select " + (TestFrameworkTest.CSV_COLS)) + ", columns[3] as address from cp.`testframework/small_test_data_extra_col.tsv`")).ordered().csvBaselineFile("testframework/small_test_data.tsv").baselineTypes(BIGINT, VARCHAR, VARCHAR).baselineColumns("employee_id", "first_name", "last_name").build().run();
        } catch (AssertionError ex) {
            Assert.assertEquals("Unexpected extra column `address` returned by query.", ex.getMessage());
            // this indicates successful completion of the test
            return;
        }
        throw new Exception("Test framework verification failed, expected failure on extra column.");
    }

    @Test
    public void testCSVVerification_missing_column_fails() throws Exception {
        try {
            BaseTestQuery.testBuilder().sqlQuery("select employee_id, first_name, last_name from cp.`testframework/small_test_data.json`").ordered().csvBaselineFile("testframework/small_test_data_extra_col.tsv").baselineTypes(BIGINT, VARCHAR, VARCHAR, VARCHAR).baselineColumns("employee_id", "first_name", "last_name", "address").build().run();
        } catch (Exception ex) {
            Assert.assertTrue(ex.getMessage(), ex.getMessage().startsWith("Expected column(s) `address`,  not found in result set"));
            // this indicates successful completion of the test
            return;
        }
        throw new Exception("Test framework verification failed, expected failure on missing column.");
    }

    @Test
    public void testCSVVerificationOfTypes() throws Throwable {
        try {
            BaseTestQuery.testBuilder().sqlQuery("select employee_id, first_name, last_name from cp.`testframework/small_test_data.json`").ordered().csvBaselineFile("testframework/small_test_data.tsv").baselineTypes(INT, VARCHAR, VARCHAR).baselineColumns("employee_id", "first_name", "last_name").build().run();
        } catch (Exception ex) {
            Assert.assertThat(ex.getMessage(), CoreMatchers.containsString("at position 0 column '`employee_id`' mismatched values, expected: 12(Integer) but received 12(Long)"));
            // this indicates successful completion of the test
            return;
        }
        throw new Exception("Test framework verification failed, expected failure on type check.");
    }

    @Test
    public void testCSVVerificationOfOrder_checkFailure() throws Throwable {
        try {
            BaseTestQuery.testBuilder().sqlQuery("select columns[0] as employee_id, columns[1] as first_name, columns[2] as last_name from cp.`testframework/small_test_data_reordered.tsv`").ordered().csvBaselineFile("testframework/small_test_data.tsv").baselineColumns("employee_id", "first_name", "last_name").build().run();
        } catch (Exception ex) {
            Assert.assertThat(ex.getMessage(), CoreMatchers.containsString("at position 0 column '`employee_id`' mismatched values, expected: 12(String) but received 16(String)"));
            // this indicates successful completion of the test
            return;
        }
        throw new Exception("Test framework verification failed, expected failure on order check.");
    }

    @Test
    public void testCSVVerificationOfUnorderedComparison() throws Throwable {
        BaseTestQuery.testBuilder().sqlQuery("select columns[0] as employee_id, columns[1] as first_name, columns[2] as last_name from cp.`testframework/small_test_data_reordered.tsv`").unOrdered().csvBaselineFile("testframework/small_test_data.tsv").baselineColumns("employee_id", "first_name", "last_name").build().run();
    }

    // TODO - enable more advanced type handling for JSON, currently basic support works
    // add support for type information taken from test query, or explicit type expectations
    @Test
    public void testBasicJSON() throws Exception {
        BaseTestQuery.testBuilder().sqlQuery("select * from cp.`scan_json_test_3.json`").ordered().jsonBaselineFile("/scan_json_test_3.json").build().run();
        // Check other verification method with same files
        BaseTestQuery.testBuilder().sqlQuery("select * from cp.`scan_json_test_3.json`").unOrdered().jsonBaselineFile("/scan_json_test_3.json").build().run();
    }

    @Test
    public void testComplexJSON_all_text() throws Exception {
        BaseTestQuery.testBuilder().sqlQuery("select * from cp.`store/json/schema_change_int_to_string.json`").optionSettingQueriesForTestQuery("alter system set `store.json.all_text_mode` = true").ordered().jsonBaselineFile("store/json/schema_change_int_to_string.json").optionSettingQueriesForBaseline("alter system set `store.json.all_text_mode` = true").build().run();
        // Check other verification method with same files
        BaseTestQuery.testBuilder().sqlQuery("select * from cp.`store/json/schema_change_int_to_string.json`").optionSettingQueriesForTestQuery("alter system set `store.json.all_text_mode` = true").unOrdered().jsonBaselineFile("store/json/schema_change_int_to_string.json").optionSettingQueriesForBaseline("alter system set `store.json.all_text_mode` = true").build().run();
        BaseTestQuery.test("alter system set `store.json.all_text_mode` = false");
    }

    @Test
    public void testRepeatedColumnMatching() throws Exception {
        try {
            BaseTestQuery.testBuilder().sqlQuery("select * from cp.`store/json/schema_change_int_to_string.json`").optionSettingQueriesForTestQuery("alter system set `store.json.all_text_mode` = true").ordered().jsonBaselineFile("testframework/schema_change_int_to_string_non-matching.json").optionSettingQueriesForBaseline("alter system set `store.json.all_text_mode` = true").build().run();
        } catch (Exception ex) {
            Assert.assertThat(ex.getMessage(), CoreMatchers.containsString(("at position 1 column '`field_1`' mismatched values, " + "expected: [\"5\",\"2\",\"3\",\"4\",\"1\",\"2\"](JsonStringArrayList) but received [\"5\"](JsonStringArrayList)")));
            // this indicates successful completion of the test
            return;
        }
        throw new Exception("Test framework verification failed, expected failure on order check.");
    }

    @Test
    public void testEmptyResultSet() throws Exception {
        BaseTestQuery.testBuilder().sqlQuery("select * from cp.`store/json/json_simple_with_null.json` where 1=0").expectsEmptyResultSet().build().run();
        try {
            BaseTestQuery.testBuilder().sqlQuery("select * from cp.`store/json/json_simple_with_null.json`").expectsEmptyResultSet().build().run();
        } catch (AssertionError ex) {
            Assert.assertEquals("Different number of records returned expected:<0> but was:<4>", ex.getMessage());
            // this indicates successful completion of the test
            return;
        }
        throw new Exception("Test framework verification failed, expected failure on unexpected records.");
    }

    @Test
    public void testCSVVerificationTypeMap() throws Throwable {
        Map<SchemaPath, TypeProtos.MajorType> typeMap = new HashMap<>();
        typeMap.put(TestBuilder.parsePath("first_name"), Types.optional(VARCHAR));
        typeMap.put(TestBuilder.parsePath("employee_id"), Types.optional(INT));
        typeMap.put(TestBuilder.parsePath("last_name"), Types.optional(VARCHAR));
        // This should work without this line because of the default type casts added based on the types that come out of the test query.
        // To write a test that enforces strict typing you must pass type information using a CSV with a list of types,
        // or any format with a Map of types like is constructed above and include the call to pass it into the test, which is commented out below
        // .baselineTypes(typeMap)
        BaseTestQuery.testBuilder().sqlQuery("select cast(columns[0] as int) employee_id, columns[1] as first_name, columns[2] as last_name from cp.`testframework/small_test_data_reordered.tsv`").unOrdered().csvBaselineFile("testframework/small_test_data.tsv").baselineColumns("employee_id", "first_name", "last_name").build().run();
        typeMap.clear();
        typeMap.put(TestBuilder.parsePath("first_name"), Types.optional(VARCHAR));
        // This is the wrong type intentionally to ensure failures happen when expected
        typeMap.put(TestBuilder.parsePath("employee_id"), Types.optional(VARCHAR));
        typeMap.put(TestBuilder.parsePath("last_name"), Types.optional(VARCHAR));
        try {
            BaseTestQuery.testBuilder().sqlQuery("select cast(columns[0] as int) employee_id, columns[1] as first_name, columns[2] as last_name from cp.`testframework/small_test_data_reordered.tsv`").unOrdered().csvBaselineFile("testframework/small_test_data.tsv").baselineColumns("employee_id", "first_name", "last_name").baselineTypes(typeMap).build().run();
        } catch (Exception ex) {
            // this indicates successful completion of the test
            return;
        }
        throw new Exception("Test framework verification failed, expected failure on type check.");
    }
}

