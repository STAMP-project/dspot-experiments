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
package org.apache.drill.exec.expr;


import UserBitShared.QueryType.SQL;
import org.apache.drill.categories.PlannerTest;
import org.apache.drill.categories.SqlTest;
import org.apache.drill.categories.UnlikelyTest;
import org.apache.drill.test.BaseTestQuery;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ SqlTest.class, PlannerTest.class })
public class TestSchemaPathMaterialization extends BaseTestQuery {
    @Test
    public void testSingleProjectionFromMultiLevelRepeatedList() throws Exception {
        final String query = "select t.odd[2][0][0] v1 " + " from cp.`complex/json/repeated_list.json` t";
        BaseTestQuery.testBuilder().sqlQuery(query).ordered().baselineColumns("v1").baselineValues(5L).go();
    }

    @Test
    public void testMultiProjectionFromMultiLevelRepeatedListWhenFieldsExist() throws Exception {
        final String query = "select t.odd[0][0][0] v1, t.odd[0][1][0] v2, t.odd[0][2][0] v3 " + " from cp.`complex/json/repeated_list.json` t";
        BaseTestQuery.testRunAndPrint(SQL, query);
        BaseTestQuery.testBuilder().sqlQuery(query).ordered().baselineColumns("v1", "v2", "v3").baselineValues(1L, null, 3L).go();
    }

    // DRILL-1962
    @Test
    @Category(UnlikelyTest.class)
    public void testProjectionMultipleFiles() throws Exception {
        final String query = "select t.oooa.oa.oab.oabc[1].rowValue1 rowValue from dfs.`complex/json/multiple/*.json` t";
        BaseTestQuery.testBuilder().sqlQuery(query).unOrdered().jsonBaselineFile("complex/drill-1962-result.json").go();
    }

    // DRILL-4264
    @Test
    @Category(UnlikelyTest.class)
    public void testFieldNameWithDot() throws Exception {
        final String tableName = "dfs.tmp.table_with_dot_field";
        try {
            BaseTestQuery.test("create table %s as select o_custkey as `x.y.z` from cp.`tpch/orders.parquet`", tableName);
            final String query = "select * from %s t where `x.y.z`=1091";
            BaseTestQuery.testBuilder().sqlQuery(query, tableName).unOrdered().baselineColumns("`x.y.z`").baselineValues(1091).baselineValues(1091).go();
        } finally {
            BaseTestQuery.test("drop table if exists %s", tableName);
        }
    }
}

