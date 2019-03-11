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
package com.mapr.drill.maprdb.tests.json;


import org.apache.drill.PlanTestBase;
import org.apache.drill.exec.util.EncodedSchemaPathSet;
import org.junit.Test;


public class TestEncodedFieldPaths extends BaseJsonTest {
    private static final String TABLE_NAME = "encoded_fields_userdata_table";

    private static final String INDEX_NAME = "encoded_fields_userdata_index";

    private static final String JSON_FILE_URL = "/com/mapr/drill/json/encoded_fields_userdata.json";

    private static boolean tableCreated = false;

    private static String tablePath;

    @Test
    public void test_encoded_fields_with_non_covering_index() throws Exception {
        final String sql = String.format(("SELECT\n" + (((("  t.`%s`,t.`$$document`\n" + "FROM\n") + "  hbase.root.`%s` t\n") + "WHERE (t.`age` > 20)\n") + "ORDER BY t.`_id` ASC")), EncodedSchemaPathSet.encode("_id", "codes")[0], TestEncodedFieldPaths.tablePath);
        setColumnWidths(new int[]{ 20, 60 });
        runSQLAndVerifyCount(sql, 3);
        // plan test
        final String[] expectedPlan = new String[]{ "JsonTableGroupScan.*indexName=encoded_fields_userdata_index.*"// scan on index
         + "columns=\\[`_id`, `age`\\]", "RestrictedJsonTableGroupScan.*"// restricted scan on the table with encoded name
         + "columns=\\[`age`, `\\$\\$ENC00L5UWIADDN5SGK4Y`, `\\$\\$document`, `_id`\\]", "RowKeyJoin" };
        // join on row_key
        final String[] excludedPlan = new String[]{  };
        PlanTestBase.testPlanMatchingPatterns(sql, expectedPlan, excludedPlan);
    }

    @Test
    public void test_encoded_fields_with_covering_index() throws Exception {
        final String sql = String.format(("SELECT\n" + (((("  t.`%s`,t.`$$document`\n" + "FROM\n") + "  hbase.root.`%s` t\n") + "WHERE (t.`age` > 10)\n") + "ORDER BY t.`_id` ASC")), EncodedSchemaPathSet.encode("name.last", "data.salary")[0], TestEncodedFieldPaths.tablePath);
        setColumnWidths(new int[]{ 20, 60 });
        runSQLAndVerifyCount(sql, 4);
        // plan test
        final String[] expectedPlan = new String[]{ "JsonTableGroupScan.*indexName=encoded_fields_userdata_index.*", // scan on index
        "columns=\\[`age`, `\\$\\$ENC00NZQW2ZJONRQXG5AAMRQXIYJOONQWYYLSPE`, `\\$\\$document`, `_id`\\]" };
        final String[] excludedPlan = new String[]{ "RestrictedJsonTableGroupScan"// restricted scan on the table
        , "RowKeyJoin" };
        // join on row_key
        PlanTestBase.testPlanMatchingPatterns(sql, expectedPlan, excludedPlan);
    }
}

