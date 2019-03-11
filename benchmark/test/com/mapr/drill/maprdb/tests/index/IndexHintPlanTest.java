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
package com.mapr.drill.maprdb.tests.index;


import com.mapr.tests.annotations.ClusterTest;
import org.apache.drill.PlanTestBase;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runners.MethodSorters;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@Category(ClusterTest.class)
public class IndexHintPlanTest extends IndexPlanTest {
    private static final String defaultHavingIndexPlan = "alter session reset `planner.enable_index_planning`";

    // A simple testcase with index hint on a table which has only one index for a column t.id.ssn;
    // This should pick i_ssn index for the query
    @Test
    public void testSimpleIndexHint() throws Exception {
        String hintquery = "SELECT  t.id.ssn as ssn FROM table(hbase.`index_test_primary`(type => 'maprdb', index => 'i_ssn')) as t " + " where t.id.ssn = '100007423'";
        String query = "SELECT t.id.ssn as ssn FROM hbase.`index_test_primary` as t where t.id.ssn = '100007423'";
        test(IndexHintPlanTest.defaultHavingIndexPlan);
        PlanTestBase.testPlanMatchingPatterns(hintquery, new String[]{ ".*JsonTableGroupScan.*tableName=.*index_test_primary.*indexName=i_ssn" }, new String[]{ "RowKeyJoin" });
        // default plan picked by optimizer.
        PlanTestBase.testPlanMatchingPatterns(query, new String[]{ ".*JsonTableGroupScan.*tableName=.*index_test_primary.*indexName=i_ssn" }, new String[]{ "RowKeyJoin" });
        testBuilder().sqlQuery(hintquery).ordered().baselineColumns("ssn").baselineValues("100007423").go();
    }

    // A testcase where there are multiple index to pick from but only picks the index provided as hint.
    // A valid index is provided as hint and it is useful during the index selection process, hence it will be selected.
    @Test
    public void testHintCaseWithMultipleIndexes_1() throws Exception {
        String hintquery = "SELECT t.`address`.`state` AS `state` FROM table(hbase.`index_test_primary`(type => 'maprdb', index => 'i_state_city')) as t " + " where t.address.state = 'pc'";
        String query = "SELECT t.`address`.`state` AS `state` FROM hbase.`index_test_primary` as t where t.address.state = 'pc'";
        test(IndexHintPlanTest.defaultHavingIndexPlan);
        PlanTestBase.testPlanMatchingPatterns(hintquery, new String[]{ ".*JsonTableGroupScan.*tableName=.*index_test_primary.*indexName=i_state_city" }, new String[]{ "RowKeyJoin" });
        // default plan picked by optimizer
        PlanTestBase.testPlanMatchingPatterns(query, new String[]{ ".*JsonTableGroupScan.*tableName=.*index_test_primary.*indexName=(i_state_city|i_state_age_phone)" }, new String[]{ "RowKeyJoin" });
    }

    // A testcase where there are multiple index to pick from but only picks the index provided as hint.
    // A valid index is provided as hint and it is useful during the index selection process, hence it will be selected.
    // Difference between this testcase and the one before this is that index name is switched. This shows that index hint makes sure to select only one
    // valid index specified as hint.
    @Test
    public void testHintCaseWithMultipleIndexes_2() throws Exception {
        String hintquery = "SELECT t.`address`.`state` AS `state` FROM table(hbase.`index_test_primary`(type => 'maprdb', index => 'i_state_age_phone')) as t " + " where t.address.state = 'pc'";
        String query = "SELECT t.`address`.`state` AS `state` FROM hbase.`index_test_primary` as t where t.address.state = 'pc'";
        test(IndexHintPlanTest.defaultHavingIndexPlan);
        PlanTestBase.testPlanMatchingPatterns(hintquery, new String[]{ ".*JsonTableGroupScan.*tableName=.*index_test_primary.*indexName=i_state_age_phone" }, new String[]{ "RowKeyJoin" });
        // default plan picked by query optimizer.
        PlanTestBase.testPlanMatchingPatterns(query, new String[]{ ".*JsonTableGroupScan.*tableName=.*index_test_primary.*indexName=(i_state_city|i_state_age_phone)" }, new String[]{ "RowKeyJoin" });
    }

    // Negative cases
    // A testcase where there are multiple index to pick from but none of them equals to the index provided as hint (index hint is wrong).
    // In this index is not at all present in the table hence it falls back to the case where the index itself is not given.
    // Hence here one of the i_state_city or i_state_age_lic will be selected depending upon the cost.
    @Test
    public void testWithMultipleIndexesButNoIndexWithHint() throws Exception {
        String hintquery = "SELECT t.`address`.`state` AS `state` FROM table(hbase.`index_test_primary`(type => 'maprdb', index => 'i_state_and_city')) as t " + " where t.address.state = 'pc'";
        test(IndexHintPlanTest.defaultHavingIndexPlan);
        PlanTestBase.testPlanMatchingPatterns(hintquery, new String[]{ ".*JsonTableGroupScan.*tableName=.*index_test_primary.*indexName=(i_state_city|i_state_age_phone)" }, new String[]{ "RowKeyJoin" });
    }

    // A testcase where there are multiple index to pick from but none of them equals to the index provided as hint and the hint index is valid.
    // Here the index name given is valid (i.e it is present in the table) but it is not useful.
    // This case falls back to full table scan.
    @Test
    public void testWithMultipleIndexesButNoIndexWithValidHint() throws Exception {
        String hintquery = "SELECT t.`address`.`state` AS `state` FROM table(hbase.`index_test_primary`(type => 'maprdb', index => 'i_ssn')) as t " + " where t.address.state = 'pc'";
        String query = "SELECT t.`address`.`state` AS `state` FROM hbase.`index_test_primary` as t where t.address.state = 'pc'";
        test(IndexHintPlanTest.defaultHavingIndexPlan);
        PlanTestBase.testPlanMatchingPatterns(hintquery, new String[]{ ".*JsonTableGroupScan.*tableName=.*index_test_primary" }, new String[]{ "RowKeyJoin", "indexName=" });
        PlanTestBase.testPlanMatchingPatterns(query, new String[]{ ".*JsonTableGroupScan.*tableName=.*index_test_primary.*indexName=(i_state_city|i_state_age_phone)" }, new String[]{ "RowKeyJoin" });
    }

    // Covering index should be generated for a simple query instead of a RowKeyJoin.
    @Test
    public void testSimpleNoRowKeyJoin() throws Exception {
        String query = "SELECT `reverseid` from table(hbase.`index_test_primary`(type => 'maprdb', index => 'hash_i_reverseid'))  " + "where `reverseid` = 1234";
        test(IndexHintPlanTest.defaultHavingIndexPlan);
        PlanTestBase.testPlanMatchingPatterns(query, new String[]{ ".*JsonTableGroupScan.*tableName=.*index_test_primary.*indexName=hash_i_reverseid" }, new String[]{ "RowKeyJoin" });
    }
}

