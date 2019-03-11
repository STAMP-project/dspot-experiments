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
package org.apache.drill.exec.hive;


import FileSystem.FS_DEFAULT_NAME_KEY;
import HiveVarchar.MAX_VARCHAR_LENGTH;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.drill.PlanTestBase;
import org.apache.drill.categories.HiveStorageTest;
import org.apache.drill.categories.SlowTest;
import org.apache.drill.common.exceptions.UserRemoteException;
import org.apache.drill.exec.expr.fn.impl.DateUtility;
import org.hamcrest.CoreMatchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;


@Category({ SlowTest.class, HiveStorageTest.class })
public class TestHiveStorage extends HiveTestBase {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void hiveReadWithDb() throws Exception {
        test("select * from hive.kv");
    }

    @Test
    public void queryEmptyHiveTable() throws Exception {
        testBuilder().sqlQuery("SELECT * FROM hive.empty_table").expectsEmptyResultSet().go();
    }

    // DRILL-3328
    @Test
    public void convertFromOnHiveBinaryType() throws Exception {
        testBuilder().sqlQuery("SELECT convert_from(binary_field, 'UTF8') col1 from hive.readtest").unOrdered().baselineColumns("col1").baselineValues("binaryfield").baselineValues(new Object[]{ null }).go();
    }

    /**
     * Test to ensure Drill reads the all supported types correctly both normal fields (converted to Nullable types) and
     * partition fields (converted to Required types).
     */
    @Test
    public void readAllSupportedHiveDataTypes() throws Exception {
        // All fields are null, but partition fields have non-null values
        // There is a regression in Hive 1.2.1 in binary type partition columns. Disable for now.
        // "binary",
        // There is a regression in Hive 1.2.1 in binary type partition columns. Disable for now.
        // "binary",
        // There is a regression in Hive 1.2.1 in binary type partition columns. Disable for now.
        // "binary_part",
        testBuilder().sqlQuery("SELECT * FROM hive.readtest").unOrdered().baselineColumns("binary_field", "boolean_field", "tinyint_field", "decimal0_field", "decimal9_field", "decimal18_field", "decimal28_field", "decimal38_field", "double_field", "float_field", "int_field", "bigint_field", "smallint_field", "string_field", "varchar_field", "timestamp_field", "date_field", "char_field", "boolean_part", "tinyint_part", "decimal0_part", "decimal9_part", "decimal18_part", "decimal28_part", "decimal38_part", "double_part", "float_part", "int_part", "bigint_part", "smallint_part", "string_part", "varchar_part", "timestamp_part", "date_part", "char_part").baselineValues("binaryfield".getBytes(), false, 34, new BigDecimal("66"), new BigDecimal("2347.92"), new BigDecimal("2758725827.99990"), new BigDecimal("29375892739852.8"), new BigDecimal("89853749534593985.783"), 8.345, 4.67F, 123456, 234235L, 3455, "stringfield", "varcharfield", DateUtility.parseBest("2013-07-05 17:01:00"), DateUtility.parseLocalDate("2013-07-05"), "charfield", true, 64, new BigDecimal("37"), new BigDecimal("36.90"), new BigDecimal("3289379872.94565"), new BigDecimal("39579334534534.4"), new BigDecimal("363945093845093890.900"), 8.345, 4.67F, 123456, 234235L, 3455, "string", "varchar", DateUtility.parseBest("2013-07-05 17:01:00"), DateUtility.parseLocalDate("2013-07-05"), "char").baselineValues(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, true, 64, new BigDecimal("37"), new BigDecimal("36.90"), new BigDecimal("3289379872.94565"), new BigDecimal("39579334534534.4"), new BigDecimal("363945093845093890.900"), 8.345, 4.67F, 123456, 234235L, 3455, "string", "varchar", DateUtility.parseBest("2013-07-05 17:01:00"), DateUtility.parseLocalDate("2013-07-05"), "char").go();
    }

    @Test
    public void orderByOnHiveTable() throws Exception {
        testBuilder().sqlQuery("SELECT * FROM hive.kv ORDER BY `value` DESC").ordered().baselineColumns("key", "value").baselineValues(5, " key_5").baselineValues(4, " key_4").baselineValues(3, " key_3").baselineValues(2, " key_2").baselineValues(1, " key_1").go();
    }

    @Test
    public void queryingTablesInNonDefaultFS() throws Exception {
        // Update the default FS settings in Hive test storage plugin to non-local FS
        HiveTestBase.HIVE_TEST_FIXTURE.getPluginManager().updateHivePlugin(Collections.singleton(bits[0]), Collections.singletonMap(FS_DEFAULT_NAME_KEY, "hdfs://localhost:9001"));
        testBuilder().sqlQuery("SELECT * FROM hive.`default`.kv LIMIT 1").unOrdered().baselineColumns("key", "value").baselineValues(1, " key_1").go();
    }

    // DRILL-745
    @Test
    public void queryingHiveAvroTable() throws Exception {
        testBuilder().sqlQuery("SELECT * FROM hive.db1.avro ORDER BY key DESC LIMIT 1").unOrdered().baselineColumns("key", "value").baselineValues(5, " key_5").go();
    }

    // DRILL-3266
    @Test
    public void queryingTableWithSerDeInHiveContribJar() throws Exception {
        testBuilder().sqlQuery("SELECT * FROM hive.db1.kv_db1 ORDER BY key DESC LIMIT 1").unOrdered().baselineColumns("key", "value").baselineValues("5", " key_5").go();
    }

    // DRILL-3746
    @Test
    public void readFromPartitionWithCustomLocation() throws Exception {
        testBuilder().sqlQuery("SELECT count(*) as cnt FROM hive.partition_pruning_test WHERE c=99 AND d=98 AND e=97").unOrdered().baselineColumns("cnt").baselineValues(1L).go();
    }

    // DRILL-3938
    @Test
    public void readFromAlteredPartitionedTable() throws Exception {
        testBuilder().sqlQuery("SELECT key, `value`, newcol FROM hive.kv_parquet ORDER BY key LIMIT 1").unOrdered().baselineColumns("key", "value", "newcol").baselineValues(1, " key_1", null).go();
    }

    // DRILL-3938
    @Test
    public void readFromAlteredPartitionedTableWithEmptyGroupType() throws Exception {
        testBuilder().sqlQuery("SELECT newcol FROM hive.kv_parquet LIMIT 1").unOrdered().baselineColumns("newcol").baselineValues(new Object[]{ null }).go();
    }

    // DRILL-3739
    @Test
    public void readingFromStorageHandleBasedTable() throws Exception {
        testBuilder().sqlQuery("SELECT * FROM hive.kv_sh ORDER BY key LIMIT 2").ordered().baselineColumns("key", "value").expectsEmptyResultSet().go();
    }

    // DRILL-3688
    @Test
    public void readingFromSmallTableWithSkipHeaderAndFooter() throws Exception {
        testBuilder().sqlQuery("select key, `value` from hive.skipper.kv_text_small order by key asc").ordered().baselineColumns("key", "value").baselineValues(1, "key_1").baselineValues(2, "key_2").baselineValues(3, "key_3").baselineValues(4, "key_4").baselineValues(5, "key_5").go();
        testBuilder().sqlQuery("select count(1) as cnt from hive.skipper.kv_text_small").unOrdered().baselineColumns("cnt").baselineValues(5L).go();
    }

    // DRILL-3688
    @Test
    public void readingFromLargeTableWithSkipHeaderAndFooter() throws Exception {
        testBuilder().sqlQuery("select sum(key) as sum_keys from hive.skipper.kv_text_large").unOrdered().baselineColumns("sum_keys").baselineValues(((long) ((5000 * (5000 + 1)) / 2))).go();
        testBuilder().sqlQuery("select count(1) as cnt from hive.skipper.kv_text_large").unOrdered().baselineColumns("cnt").baselineValues(5000L).go();
    }

    @Test
    public void testIncorrectHeaderProperty() throws Exception {
        String query = "select * from hive.skipper.kv_incorrect_skip_header";
        thrown.expect(UserRemoteException.class);
        thrown.expectMessage(CoreMatchers.containsString("Hive table property skip.header.line.count value 'A' is non-numeric"));
        test(query);
    }

    @Test
    public void testIncorrectFooterProperty() throws Exception {
        String query = "select * from hive.skipper.kv_incorrect_skip_footer";
        thrown.expect(UserRemoteException.class);
        thrown.expectMessage(CoreMatchers.containsString("Hive table property skip.footer.line.count value 'A' is non-numeric"));
        test(query);
    }

    @Test
    public void testTableWithHeaderOnly() throws Exception {
        testBuilder().sqlQuery("select count(1) as cnt from hive.skipper.kv_text_header_only").unOrdered().baselineColumns("cnt").baselineValues(0L).go();
    }

    @Test
    public void testTableWithFooterOnly() throws Exception {
        testBuilder().sqlQuery("select count(1) as cnt from hive.skipper.kv_text_footer_only").unOrdered().baselineColumns("cnt").baselineValues(0L).go();
    }

    @Test
    public void testTableWithHeaderFooterOnly() throws Exception {
        testBuilder().sqlQuery("select count(1) as cnt from hive.skipper.kv_text_header_footer_only").unOrdered().baselineColumns("cnt").baselineValues(0L).go();
    }

    @Test
    public void testSkipHeaderFooterForPartitionedTable() throws Exception {
        testBuilder().sqlQuery("select count(1) as cnt from hive.skipper.kv_text_with_part").unOrdered().baselineColumns("cnt").baselineValues(4980L).go();
    }

    @Test
    public void testStringColumnsMetadata() throws Exception {
        String query = "select varchar_field, char_field, string_field from hive.readtest";
        Map<String, Integer> expectedResult = new HashMap<>();
        expectedResult.put("varchar_field", 50);
        expectedResult.put("char_field", 10);
        expectedResult.put("string_field", MAX_VARCHAR_LENGTH);
        verifyColumnsMetadata(client.createPreparedStatement(query).get().getPreparedStatement().getColumnsList(), expectedResult);
        try {
            test("alter session set `%s` = true", ExecConstants.EARLY_LIMIT0_OPT_KEY);
            verifyColumnsMetadata(client.createPreparedStatement(String.format("select * from (%s) t limit 0", query)).get().getPreparedStatement().getColumnsList(), expectedResult);
        } finally {
            test("alter session reset `%s`", ExecConstants.EARLY_LIMIT0_OPT_KEY);
        }
    }

    // DRILL-3250
    @Test
    public void testNonAsciiStringLiterals() throws Exception {
        testBuilder().sqlQuery("select * from hive.empty_table where b = '????????'").expectsEmptyResultSet().go();
    }

    @Test
    public void testPhysicalPlanSubmission() throws Exception {
        PlanTestBase.testPhysicalPlanExecutionBasedOnQuery("select * from hive.kv");
        PlanTestBase.testPhysicalPlanExecutionBasedOnQuery("select * from hive.readtest");
        try {
            alterSession(ExecConstants.HIVE_CONF_PROPERTIES, "hive.mapred.supports.subdirectories=true\nmapred.input.dir.recursive=true");
            PlanTestBase.testPhysicalPlanExecutionBasedOnQuery("select * from hive.sub_dir_table");
        } finally {
            resetSessionOption(ExecConstants.HIVE_CONF_PROPERTIES);
        }
    }

    @Test
    public void testHiveConfPropertiesAtSessionLevel() throws Exception {
        String query = "select * from hive.sub_dir_table";
        try {
            alterSession(ExecConstants.HIVE_CONF_PROPERTIES, "hive.mapred.supports.subdirectories=true\nmapred.input.dir.recursive=true");
            test(query);
        } finally {
            resetSessionOption(ExecConstants.HIVE_CONF_PROPERTIES);
        }
        thrown.expect(UserRemoteException.class);
        thrown.expectMessage(CoreMatchers.containsString("IOException: Not a file"));
        test(query);
    }

    @Test
    public void testSchemaCaseInsensitive() throws Exception {
        test("select * from Hive.`Default`.Kv");
    }
}

