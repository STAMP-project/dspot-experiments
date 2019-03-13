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


import com.mapr.drill.maprdb.tests.json.BaseJsonTest;
import com.mapr.tests.annotations.ClusterTest;
import org.apache.drill.PlanTestBase;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runners.MethodSorters;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@Category(ClusterTest.class)
public class IndexPlanTest extends BaseJsonTest {
    static final String PRIMARY_TABLE_NAME = "/tmp/index_test_primary";

    static final int PRIMARY_TABLE_SIZE = 10000;

    private static final String sliceTargetSmall = "alter session set `planner.slice_target` = 1";

    private static final String sliceTargetDefault = "alter session reset `planner.slice_target`";

    private static final String noIndexPlan = "alter session set `planner.enable_index_planning` = false";

    private static final String defaultHavingIndexPlan = "alter session reset `planner.enable_index_planning`";

    private static final String disableHashAgg = "alter session set `planner.enable_hashagg` = false";

    private static final String enableHashAgg = "alter session set `planner.enable_hashagg` = true";

    private static final String lowNonCoveringSelectivityThreshold = "alter session set `planner.index.noncovering_selectivity_threshold` = 0.00001";

    private static final String defaultnonCoveringSelectivityThreshold = "alter session set `planner.index.noncovering_selectivity_threshold` = 0.025";

    private static final String incrnonCoveringSelectivityThreshold = "alter session set `planner.index.noncovering_selectivity_threshold` = 0.25";

    private static final String disableFTS = "alter session set `planner.disable_full_table_scan` = true";

    private static final String enableFTS = "alter session reset `planner.disable_full_table_scan`";

    private static final String preferIntersectPlans = "alter session set `planner.index.prefer_intersect_plans` = true";

    private static final String defaultIntersectPlans = "alter session reset `planner.index.prefer_intersect_plans`";

    private static final String lowRowKeyJoinBackIOFactor = "alter session set `planner.index.rowkeyjoin_cost_factor` = 0.01";

    private static final String defaultRowKeyJoinBackIOFactor = "alter session reset `planner.index.rowkeyjoin_cost_factor`";

    private static final String incrRowKeyJoinConvSelThreshold = "alter session set `planner.rowkeyjoin_conversion_selectivity_threshold` = 1.0";

    private static final String defaultRowKeyConvSelThreshold = "alter session reset `planner.rowkeyjoin_conversion_selectivity_threshold`";

    private static final String forceRowKeyJoinConversionUsingHashJoin = "alter session set `planner.rowkeyjoin_conversion_using_hashjoin` = true";

    private static final String defaultRowKeyJoinConversionUsingHashJoin = "alter session reset `planner.rowkeyjoin_conversion_using_hashjoin`";

    @Test
    public void CTASTestTable() throws Exception {
        String ctasQuery = "CREATE TABLE hbase.tmp.`backup_index_test_primary` " + "AS SELECT * FROM hbase.`index_test_primary` as t ";
        test(ctasQuery);
        test("DROP TABLE IF EXISTS hbase.tmp.`backup_index_test_primary`");
    }

    @Test
    public void CoveringPlanWithNonIndexedField() throws Exception {
        String query = "SELECT t.`contact`.`phone` AS `phone` FROM hbase.`index_test_primary` as t " + " where t.id.ssn = '100007423'";
        test(IndexPlanTest.defaultHavingIndexPlan);
        PlanTestBase.testPlanMatchingPatterns(query, new String[]{ ".*JsonTableGroupScan.*tableName=.*index_test_primary.*indexName=i_ssn" }, new String[]{ "RowKeyJoin" });
        testBuilder().sqlQuery(query).ordered().baselineColumns("phone").baselineValues("6500005471").go();
    }

    @Test
    public void CoveringPlanWithOnlyIndexedField() throws Exception {
        String query = "SELECT t.`id`.`ssn` AS `ssn` FROM hbase.`index_test_primary` as t " + " where t.id.ssn = '100007423'";
        test(IndexPlanTest.defaultHavingIndexPlan);
        PlanTestBase.testPlanMatchingPatterns(query, new String[]{ ".*JsonTableGroupScan.*tableName=.*index_test_primary.*indexName=i_ssn" }, new String[]{ "RowKeyJoin" });
        testBuilder().optionSettingQueriesForTestQuery(IndexPlanTest.defaultHavingIndexPlan).sqlQuery(query).ordered().baselineColumns("ssn").baselineValues("100007423").go();
    }

    @Test
    public void NoIndexPlanForNonIndexField() throws Exception {
        String query = "SELECT t.`id`.`ssn` AS `ssn` FROM hbase.`index_test_primary` as t " + " where t.contact.phone = '6500005471'";
        test(IndexPlanTest.defaultHavingIndexPlan);
        PlanTestBase.testPlanMatchingPatterns(query, new String[]{ ".*JsonTableGroupScan.*tableName=.*index_test_primary" }, new String[]{ "RowKeyJoin", "indexName=" });
        testBuilder().sqlQuery(query).unOrdered().baselineColumns("ssn").baselineValues("100007423").baselineColumns("ssn").baselineValues("100007632").go();
    }

    @Test
    public void NonCoveringPlan() throws Exception {
        String query = "SELECT t.`name`.`fname` AS `fname` FROM hbase.`index_test_primary` as t " + " where t.id.ssn = '100007423'";
        test(IndexPlanTest.defaultHavingIndexPlan);
        PlanTestBase.testPlanMatchingPatterns(query, new String[]{ "RowKeyJoin", ".*RestrictedJsonTableGroupScan.*tableName=.*index_test_primary,", ".*JsonTableGroupScan.*tableName=.*index_test_primary,.*indexName=i_ssn" }, new String[]{  });
        testBuilder().sqlQuery(query).ordered().baselineColumns("fname").baselineValues("KfFzK").go();
    }

    @Test
    public void RangeConditionIndexPlan() throws Exception {
        String query = "SELECT t.`name`.`lname` AS `lname` FROM hbase.`index_test_primary` as t " + " where t.personal.age > 52 AND t.name.fname='KfFzK'";
        try {
            test(((((IndexPlanTest.defaultHavingIndexPlan) + ";") + (IndexPlanTest.lowRowKeyJoinBackIOFactor)) + ";"));
            PlanTestBase.testPlanMatchingPatterns(query, new String[]{ "RowKeyJoin", ".*RestrictedJsonTableGroupScan.*tableName=.*index_test_primary,", ".*JsonTableGroupScan.*tableName=.*index_test_primary,.*indexName=(i_age|i_age_with_fname)" }, new String[]{  });
            testBuilder().optionSettingQueriesForTestQuery(IndexPlanTest.defaultHavingIndexPlan).optionSettingQueriesForTestQuery(IndexPlanTest.lowRowKeyJoinBackIOFactor).optionSettingQueriesForBaseline(IndexPlanTest.noIndexPlan).unOrdered().sqlQuery(query).sqlBaselineQuery(query).build().run();
            testBuilder().optionSettingQueriesForTestQuery(IndexPlanTest.sliceTargetSmall).optionSettingQueriesForBaseline(IndexPlanTest.sliceTargetDefault).unOrdered().sqlQuery(query).sqlBaselineQuery(query).build().run();
        } finally {
            test(IndexPlanTest.defaultRowKeyJoinBackIOFactor);
        }
    }

    @Test
    public void CoveringWithSimpleFieldsOnly() throws Exception {
        String query = "SELECT t._id AS `tid` FROM hbase.`index_test_primary` as t " + " where t.driverlicense = 100007423";
        test(IndexPlanTest.defaultHavingIndexPlan);
        PlanTestBase.testPlanMatchingPatterns(query, new String[]{ "JsonTableGroupScan.*tableName=.*index_test_primary,.*indexName=i_lic" }, new String[]{ "RowKeyJoin" });
        testBuilder().sqlQuery(query).ordered().baselineColumns("tid").baselineValues("1012").go();
    }

    @Test
    public void NonCoveringWithSimpleFieldsOnly() throws Exception {
        String query = "SELECT t.rowid AS `rowid` FROM hbase.`index_test_primary` as t " + " where t.driverlicense = 100007423";
        test(IndexPlanTest.defaultHavingIndexPlan);
        PlanTestBase.testPlanMatchingPatterns(query, new String[]{ "RowKeyJoin(.*[\n\r])+.*" + ("RestrictedJsonTableGroupScan.*tableName=.*index_test_primary(.*[\n\r])+.*" + "JsonTableGroupScan.*tableName=.*index_test_primary,.*indexName=i_lic") }, new String[]{  });
        testBuilder().sqlQuery(query).ordered().baselineColumns("rowid").baselineValues("1012").go();
    }

    @Test
    public void NonCoveringWithExtraConditonOnPrimary() throws Exception {
        String query = "SELECT t.`name`.`fname` AS `fname` FROM hbase.`index_test_primary` as t " + " where t.personal.age = 53 AND t.name.lname='UZwNk'";
        try {
            test(((((IndexPlanTest.defaultHavingIndexPlan) + ";") + (IndexPlanTest.lowRowKeyJoinBackIOFactor)) + ";"));
            PlanTestBase.testPlanMatchingPatterns(query, new String[]{ "RowKeyJoin", ".*RestrictedJsonTableGroupScan", ".*JsonTableGroupScan.*indexName=i_age" }, new String[]{  });
            testBuilder().sqlQuery(query).ordered().baselineColumns("fname").baselineValues("KfFzK").go();
        } finally {
            test(IndexPlanTest.defaultRowKeyJoinBackIOFactor);
        }
    }

    @Test
    public void Intersect2indexesPlan() throws Exception {
        String query = "SELECT t.`name`.`lname` AS `lname` FROM hbase.`index_test_primary` as t " + " where t.personal.age = 53 AND t.personal.income=45";
        try {
            test(IndexPlanTest.defaultHavingIndexPlan);
            test((((IndexPlanTest.preferIntersectPlans) + ";") + (IndexPlanTest.disableFTS)));
            PlanTestBase.testPlanMatchingPatterns(query, new String[]{ "RowKeyJoin(.*[\n\r])+.*RestrictedJsonTableGroupScan(.*[\n\r])+.*HashJoin(.*[\n\r])+.*JsonTableGroupScan.*indexName=(i_age|i_income)(.*[\n\r])+.*JsonTableGroupScan.*indexName=(i_age|i_income)" }, new String[]{  });
            testBuilder().sqlQuery(query).unOrdered().baselineColumns("lname").baselineValues("UZwNk").baselineColumns("lname").baselineValues("foNwtze").baselineColumns("lname").baselineValues("qGZVfY").go();
            testBuilder().optionSettingQueriesForTestQuery(IndexPlanTest.sliceTargetSmall).optionSettingQueriesForBaseline(IndexPlanTest.sliceTargetDefault).unOrdered().sqlQuery(query).sqlBaselineQuery(query).build().run();
        } finally {
            test((((IndexPlanTest.defaultIntersectPlans) + ";") + (IndexPlanTest.enableFTS)));
        }
    }

    @Test
    public void CompositeIndexNonCoveringPlan() throws Exception {
        String query = "SELECT t.`id`.`ssn` AS `ssn` FROM hbase.`index_test_primary` as t " + " where t.address.state = 'pc' AND t.address.city='pfrrs'";
        try {
            test(((((IndexPlanTest.defaultHavingIndexPlan) + ";") + (IndexPlanTest.lowRowKeyJoinBackIOFactor)) + ";"));
            // either i_state_city or i_state_age_phone will be picked depends on cost model, both is fine for testing composite index nonCovering plan
            PlanTestBase.testPlanMatchingPatterns(query, new String[]{ "RowKeyJoin(.*[\n\r])+.*RestrictedJsonTableGroupScan(.*[\n\r])+.*JsonTableGroupScan.*indexName=i_state_" }, new String[]{  });
            testBuilder().sqlQuery(query).unOrdered().baselineColumns("ssn").baselineValues("100007423").baselineColumns("ssn").baselineValues("100008861").go();
            testBuilder().optionSettingQueriesForTestQuery(IndexPlanTest.sliceTargetSmall).optionSettingQueriesForBaseline(IndexPlanTest.sliceTargetDefault).unOrdered().sqlQuery(query).sqlBaselineQuery(query).build().run();
        } finally {
            test(IndexPlanTest.defaultRowKeyJoinBackIOFactor);
        }
    }

    // filter cover indexed, included and not in index at all filter
    @Test
    public void CompositeIndexNonCoveringFilterWithAllFieldsPlan() throws Exception {
        String query = "SELECT t.`id`.`ssn` AS `ssn` FROM hbase.`index_test_primary` as t " + " where t.address.state = 'pc' AND t.address.city='pfrrs' AND t.driverlicense IN (100007423, 100007424)";
        test(((((IndexPlanTest.defaultHavingIndexPlan) + ";") + (IndexPlanTest.lowRowKeyJoinBackIOFactor)) + ";"));
        PlanTestBase.testPlanMatchingPatterns(query, new String[]{ "RowKeyJoin(.*[\n\r])+.*RestrictedJsonTableGroupScan.*condition=.*state.*city.*driverlicense.*or.*driverlicense.*(.*[\n\r])+.*JsonTableGroupScan.*indexName=" }, new String[]{  });
        testBuilder().sqlQuery(query).unOrdered().baselineColumns("ssn").baselineValues("100007423").go();
        testBuilder().optionSettingQueriesForTestQuery(IndexPlanTest.sliceTargetSmall).optionSettingQueriesForBaseline(IndexPlanTest.sliceTargetDefault).unOrdered().sqlQuery(query).sqlBaselineQuery(query).build().run();
    }

    @Test
    public void CompositeIndexCoveringPlan() throws Exception {
        String query = "SELECT t.`address`.`city` AS `city` FROM hbase.`index_test_primary` as t " + " where t.address.state = 'pc' AND t.address.city='pfrrs'";
        test(IndexPlanTest.defaultHavingIndexPlan);
        PlanTestBase.testPlanMatchingPatterns(query, new String[]{ ".*JsonTableGroupScan.*indexName=i_state_city" }, new String[]{ "RowKeyJoin", "Filter" });
        testBuilder().sqlQuery(query).unOrdered().baselineColumns("city").baselineValues("pfrrs").baselineColumns("city").baselineValues("pfrrs").go();
        testBuilder().optionSettingQueriesForTestQuery(IndexPlanTest.sliceTargetSmall).optionSettingQueriesForBaseline(IndexPlanTest.sliceTargetDefault).unOrdered().sqlQuery(query).sqlBaselineQuery(query).build().run();
    }

    @Test
    public void TestNonCoveringRangePartition_1() throws Exception {
        String query = "SELECT t.`name`.`lname` AS `lname` FROM hbase.`index_test_primary` as t " + " where t.personal.age = 53";
        String[] expectedPlan = new String[]{ "RowKeyJoin(.*[\n\r])+.*" + (("RestrictedJsonTableGroupScan.*tableName=.*index_test_primary(.*[\n\r])+.*" + "RangePartitionExchange(.*[\n\r])+.*") + "JsonTableGroupScan.*tableName=.*index_test_primary,.*indexName=(i_age|i_age_with_fname)") };
        test(((((IndexPlanTest.defaultHavingIndexPlan) + ";") + (IndexPlanTest.sliceTargetSmall)) + ";"));
        PlanTestBase.testPlanMatchingPatterns(query, expectedPlan, new String[]{  });
        try {
            testBuilder().optionSettingQueriesForTestQuery(IndexPlanTest.defaultHavingIndexPlan).optionSettingQueriesForBaseline(IndexPlanTest.noIndexPlan).unOrdered().sqlQuery(query).sqlBaselineQuery(query).build().run();
        } finally {
            test(IndexPlanTest.defaultHavingIndexPlan);
            test(IndexPlanTest.sliceTargetDefault);
        }
    }

    @Test
    public void TestCastVarCharCoveringPlan() throws Exception {
        // length 255 is to exact match the casted indexed field's length
        String query = "SELECT t._id as tid, cast(t.driverlicense as varchar(255)) as driverlicense FROM hbase.`index_test_primary` as t " + " where cast(t.driverlicense as varchar(255))='100007423'";
        test(IndexPlanTest.defaultHavingIndexPlan);
        PlanTestBase.testPlanMatchingPatterns(query, new String[]{ ".*JsonTableGroupScan.*tableName=.*index_test_primary.*indexName=i_cast_vchar_lic" }, new String[]{ "RowKeyJoin" });
        testBuilder().optionSettingQueriesForTestQuery(IndexPlanTest.defaultHavingIndexPlan).sqlQuery(query).ordered().baselineColumns("tid", "driverlicense").baselineValues("1012", "100007423").go();
    }

    @Test
    public void TestCastINTCoveringPlan() throws Exception {
        String query = "SELECT t._id as tid, CAST(t.id.ssn as INT) as ssn, t.contact.phone AS `phone` FROM hbase.`index_test_primary` as t " + " where CAST(t.id.ssn as INT) = 100007423";
        test(IndexPlanTest.defaultHavingIndexPlan);
        PlanTestBase.testPlanMatchingPatterns(query, new String[]{ ".*JsonTableGroupScan.*tableName=.*index_test_primary.*indexName=i_cast_int_ssn" }, new String[]{ "RowKeyJoin" });
        testBuilder().optionSettingQueriesForTestQuery(IndexPlanTest.defaultHavingIndexPlan).sqlQuery(query).ordered().baselineColumns("tid", "ssn", "phone").baselineValues("1012", 100007423, "6500005471").go();
    }

    @Test
    public void TestCastNonCoveringPlan() throws Exception {
        String query = "SELECT t.id.ssn AS `ssn` FROM hbase.`index_test_primary` as t " + " where CAST(t.id.ssn as INT) = 100007423";
        test(IndexPlanTest.defaultHavingIndexPlan);
        PlanTestBase.testPlanMatchingPatterns(query, new String[]{ "RowKeyJoin(.*[\n\r])+.*RestrictedJsonTableGroupScan(.*[\n\r])+.*JsonTableGroupScan.*indexName=i_cast_int_ssn" }, new String[]{  });
        testBuilder().sqlQuery(query).ordered().baselineColumns("ssn").baselineValues("100007423").go();
    }

    @Test
    public void TestCastVarchar_ConvertToRangePlan() throws Exception {
        String query = "SELECT t.id.ssn AS `ssn` FROM hbase.`index_test_primary` as t " + " where CAST(driverlicense as VARCHAR(10)) = '100007423'";
        test(IndexPlanTest.defaultHavingIndexPlan);
        PlanTestBase.testPlanMatchingPatterns(query, new String[]{ "RowKeyJoin(.*[\n\r])+.*RestrictedJsonTableGroupScan(.*[\n\r])+.*JsonTableGroupScan.*MATCHES \"\\^.*100007423.*E.*\\$\".*indexName=i_cast_vchar_lic" }, new String[]{  });
        testBuilder().sqlQuery(query).ordered().baselineColumns("ssn").baselineValues("100007423").go();
    }

    // cast expression in filter is not indexed, but the same field casted to different type was indexed (CAST id.ssn as INT)
    @Test
    public void TestCastNoIndexPlan() throws Exception {
        String query = "select t.id.ssn from hbase.`index_test_primary` t where cast(t.id.ssn as varchar(10)) = '100007423'";
        PlanTestBase.testPlanMatchingPatterns(query, new String[]{  }, new String[]{ "indexName" });
    }

    @Test
    public void TestLongerCastVarCharNoIndex() throws Exception {
        // length 256 is to exact match the casted indexed field's length
        String query = "SELECT t._id as tid, cast(t.driverlicense as varchar(500)) as driverlicense FROM hbase.`index_test_primary` as t " + " where cast(t.driverlicense as varchar(500))='100007423'";
        test(IndexPlanTest.defaultHavingIndexPlan);
        PlanTestBase.testPlanMatchingPatterns(query, new String[]{  }, new String[]{ "RowKeyJoin", "indexName=" });
    }

    @Test
    public void TestCoveringPlanSortRemoved() throws Exception {
        String query = "SELECT t.`contact`.`phone` as phone FROM hbase.`index_test_primary` as t " + " where t.id.ssn <'100000003' order by t.id.ssn";
        test(IndexPlanTest.defaultHavingIndexPlan);
        PlanTestBase.testPlanMatchingPatterns(query, new String[]{ ".*JsonTableGroupScan.*tableName=.*index_test_primary.*indexName=i_ssn" }, new String[]{ "Sort" });
        testBuilder().sqlQuery(query).ordered().baselineColumns("phone").baselineValues("6500008069").baselineColumns("phone").baselineValues("6500001411").baselineColumns("phone").baselineValues("6500001595").go();
    }

    @Test
    public void TestCoveringPlanSortNotRemoved() throws Exception {
        String query = "SELECT t.`contact`.`phone` as phone FROM hbase.`index_test_primary` as t " + " where t.id.ssn <'100000003' order by t.contact.phone";
        test(IndexPlanTest.defaultHavingIndexPlan);
        PlanTestBase.testPlanMatchingPatterns(query, new String[]{ "Sort", ".*JsonTableGroupScan.*tableName=.*index_test_primary.*indexName=i_ssn" }, new String[]{ "RowkeyJoin" });
        testBuilder().sqlQuery(query).ordered().baselineColumns("phone").baselineValues("6500001411").baselineColumns("phone").baselineValues("6500001595").baselineColumns("phone").baselineValues("6500008069").go();
    }

    @Test
    public void TestCoveringPlanSortRemovedWithSimpleFields() throws Exception {
        String query = "SELECT t.driverlicense as l FROM hbase.`index_test_primary` as t " + " where t.driverlicense < 100000003 order by t.driverlicense";
        test(IndexPlanTest.defaultHavingIndexPlan);
        PlanTestBase.testPlanMatchingPatterns(query, new String[]{ ".*JsonTableGroupScan.*tableName=.*index_test_primary.*indexName=i_lic" }, new String[]{ "Sort" });
        testBuilder().sqlQuery(query).ordered().baselineColumns("l").baselineValues(100000000L).baselineColumns("l").baselineValues(100000001L).baselineColumns("l").baselineValues(100000002L).go();
    }

    @Test
    public void TestNonCoveringPlanSortRemoved() throws Exception {
        String query = "SELECT t.contact.phone as phone FROM hbase.`index_test_primary` as t " + " where t.driverlicense < 100000003 order by t.driverlicense";
        test(IndexPlanTest.defaultHavingIndexPlan);
        PlanTestBase.testPlanMatchingPatterns(query, new String[]{ "RowKeyJoin(.*[\n\r])+.*RestrictedJsonTableGroupScan(.*[\n\r])+.*JsonTableGroupScan.*indexName=i_lic" }, new String[]{ "Sort" });
        String query2 = "SELECT t.name.fname as fname FROM hbase.`index_test_primary` as t " + " where t.id.ssn < '100000003' order by t.id.ssn";
        PlanTestBase.testPlanMatchingPatterns(query2, new String[]{ "RowKeyJoin(.*[\n\r])+.*RestrictedJsonTableGroupScan(.*[\n\r])+.*JsonTableGroupScan.*indexName=" }, new String[]{ "Sort" });
        // simple field, driverlicense
        testBuilder().sqlQuery(query).ordered().baselineColumns("phone").baselineValues("6500008069").baselineColumns("phone").baselineValues("6500001411").baselineColumns("phone").baselineValues("6500001595").go();
        // query on field of item expression(having capProject), non-simple field t.id.ssn
        testBuilder().sqlQuery(query2).ordered().baselineColumns("fname").baselineValues("VcFahj").baselineColumns("fname").baselineValues("WbKVK").baselineColumns("fname").baselineValues("vSAEsyFN").go();
        test(IndexPlanTest.sliceTargetSmall);
        try {
            PlanTestBase.testPlanMatchingPatterns(query2, new String[]{ "SingleMergeExchange(.*[\n\r])+.*" + "RowKeyJoin(.*[\n\r])+.*RestrictedJsonTableGroupScan(.*[\n\r])+.*JsonTableGroupScan.*indexName=i_ssn" }, new String[]{ "Sort" });
        } finally {
            test(IndexPlanTest.sliceTargetDefault);
        }
    }

    // test cases are from TestNonCoveringPlanSortRemoved. Sort was removed when force_sort_noncovering was default(false)
    @Test
    public void TestNonCoveringPlanWithNoRemoveSortOption() throws Exception {
        try {
            test("alter session set `planner.index.force_sort_noncovering`=true");
            test(IndexPlanTest.defaultHavingIndexPlan);
            String query = "SELECT t.contact.phone as phone FROM hbase.`index_test_primary` as t " + " where t.driverlicense < 100000003 order by t.driverlicense";
            PlanTestBase.testPlanMatchingPatterns(query, new String[]{ "Sort", "RowKeyJoin(.*[\n\r])+.*RestrictedJsonTableGroupScan(.*[\n\r])+.*JsonTableGroupScan.*indexName=i_lic" }, new String[]{  });
            String query2 = "SELECT t.name.fname as fname FROM hbase.`index_test_primary` as t " + " where t.id.ssn < '100000003' order by t.id.ssn";
            PlanTestBase.testPlanMatchingPatterns(query2, new String[]{ "Sort", "RowKeyJoin(.*[\n\r])+.*RestrictedJsonTableGroupScan(.*[\n\r])+.*JsonTableGroupScan.*indexName=" }, new String[]{  });
            // simple field, driverlicense
            testBuilder().sqlQuery(query).ordered().baselineColumns("phone").baselineValues("6500008069").baselineColumns("phone").baselineValues("6500001411").baselineColumns("phone").baselineValues("6500001595").go();
            // query on field of item expression(having capProject), non-simple field t.id.ssn
            testBuilder().sqlQuery(query2).ordered().baselineColumns("fname").baselineValues("VcFahj").baselineColumns("fname").baselineValues("WbKVK").baselineColumns("fname").baselineValues("vSAEsyFN").go();
            test(IndexPlanTest.sliceTargetSmall);
            try {
                PlanTestBase.testPlanMatchingPatterns(query2, new String[]{ "Sort", "SingleMergeExchange(.*[\n\r])+.*" + "RowKeyJoin(.*[\n\r])+.*RestrictedJsonTableGroupScan(.*[\n\r])+.*JsonTableGroupScan.*indexName=i_ssn" }, new String[]{  });
            } finally {
                test(IndexPlanTest.sliceTargetDefault);
            }
        } finally {
            test("alter session reset `planner.index.force_sort_noncovering`");
        }
    }

    // 2 table join, each table has local predicate on top-level column
    @Test
    public void TestCoveringPlanJoin_1() throws Exception {
        String query = "SELECT count(*) as cnt FROM hbase.`index_test_primary` as t1 " + (" inner join hbase.`index_test_primary` as t2 on t1.driverlicense = t2.driverlicense " + " where t1.driverlicense < 100000003 and t2.driverlicense < 100000003");
        test(IndexPlanTest.defaultHavingIndexPlan);
        PlanTestBase.testPlanMatchingPatterns(query, new String[]{ ".*JsonTableGroupScan.*tableName=.*index_test_primary.*indexName=", ".*JsonTableGroupScan.*tableName=.*index_test_primary.*indexName=" }, new String[]{  });
        testBuilder().sqlQuery(query).ordered().baselineColumns("cnt").baselineValues(3L).go();
    }

    // 2 table join, each table has local predicate on nested column
    @Test
    public void TestCoveringPlanJoin_2() throws Exception {
        String query = "SELECT count(*) as cnt FROM hbase.`index_test_primary` as t1 " + (" inner join hbase.`index_test_primary` as t2 on t1.contact.phone = t2.contact.phone " + " where t1.id.ssn < '100000003' and t2.id.ssn < '100000003' ");
        test(IndexPlanTest.defaultHavingIndexPlan);
        PlanTestBase.testPlanMatchingPatterns(query, new String[]{ ".*JsonTableGroupScan.*tableName=.*index_test_primary.*indexName=", ".*JsonTableGroupScan.*tableName=.*index_test_primary.*indexName=" }, new String[]{  });
        testBuilder().sqlQuery(query).ordered().baselineColumns("cnt").baselineValues(3L).go();
    }

    // leading prefix of index has Equality conditions and ORDER BY last column; Sort SHOULD be dropped
    @Test
    public void TestCoveringPlanSortPrefix_1() throws Exception {
        String query = "SELECT t.contact.phone FROM hbase.`index_test_primary` as t " + " where t.address.state = 'wo' and t.personal.age = 35 and t.contact.phone < '6500003000' order by t.contact.phone";
        test(IndexPlanTest.defaultHavingIndexPlan);
        // we should glue to index i_state_age_phone to make sure we are testing the targeted prefix construction code path
        PlanTestBase.testPlanMatchingPatterns(query, new String[]{ ".*JsonTableGroupScan.*tableName=.*index_test_primary.*indexName=i_state_age_phone" }, new String[]{ "Sort" });
        // compare the results of index plan with the no-index plan
        testBuilder().optionSettingQueriesForTestQuery(IndexPlanTest.defaultHavingIndexPlan).optionSettingQueriesForBaseline(IndexPlanTest.noIndexPlan).unOrdered().sqlQuery(query).sqlBaselineQuery(query).build().run();
    }

    // leading prefix of index has Non-Equality conditions and ORDER BY last column; Sort SHOULD NOT be dropped
    @Test
    public void TestCoveringPlanSortPrefix_2() throws Exception {
        String query = "SELECT t.contact.phone FROM hbase.`index_test_primary` as t " + " where t.address.state = 'wo' and t.personal.age < 35 and t.contact.phone < '6500003000' order by t.contact.phone";
        test(IndexPlanTest.defaultHavingIndexPlan);
        PlanTestBase.testPlanMatchingPatterns(query, new String[]{ "Sort", ".*JsonTableGroupScan.*tableName=.*index_test_primary.*indexName=i_state_age_phone" }, new String[]{  });
        // compare the results of index plan with the no-index plan
        testBuilder().optionSettingQueriesForTestQuery(IndexPlanTest.defaultHavingIndexPlan).optionSettingQueriesForBaseline(IndexPlanTest.noIndexPlan).unOrdered().sqlQuery(query).sqlBaselineQuery(query).build().run();
    }

    // ORDER BY last two columns not in the indexed order; Sort SHOULD NOT be dropped
    @Test
    public void TestCoveringPlanSortPrefix_3() throws Exception {
        String query = "SELECT CAST(t.personal.age as VARCHAR) as age, t.contact.phone FROM hbase.`index_test_primary` as t " + " where t.address.state = 'wo' and t.personal.age < 35 and t.contact.phone < '6500003000' order by t.contact.phone, t.personal.age";
        test(IndexPlanTest.defaultHavingIndexPlan);
        PlanTestBase.testPlanMatchingPatterns(query, new String[]{ "Sort", ".*JsonTableGroupScan.*tableName=.*index_test_primary.*indexName=i_state_age_phone" }, new String[]{  });
        // compare the results of index plan with the no-index plan
        testBuilder().optionSettingQueriesForTestQuery(IndexPlanTest.defaultHavingIndexPlan).optionSettingQueriesForBaseline(IndexPlanTest.noIndexPlan).unOrdered().sqlQuery(query).sqlBaselineQuery(query).build().run();
    }

    // last two index fields in non-Equality conditions, ORDER BY last two fields; Sort SHOULD be dropped
    @Test
    public void TestCoveringPlanSortPrefix_4() throws Exception {
        String query = "SELECT t._id as tid, t.contact.phone, CAST(t.personal.age as VARCHAR) as age FROM hbase.`index_test_primary` as t " + " where t.address.state = 'wo' and t.personal.age < 35 and t.contact.phone < '6500003000' order by t.personal.age, t.contact.phone";
        test(IndexPlanTest.defaultHavingIndexPlan);
        PlanTestBase.testPlanMatchingPatterns(query, new String[]{ ".*JsonTableGroupScan.*tableName=.*index_test_primary.*indexName=i_state_age_phone" }, new String[]{ "Sort" });
        // compare the results of index plan with the no-index plan
        testBuilder().optionSettingQueriesForTestQuery(IndexPlanTest.defaultHavingIndexPlan).optionSettingQueriesForBaseline(IndexPlanTest.noIndexPlan).unOrdered().sqlQuery(query).sqlBaselineQuery(query).build().run();
    }

    // index field in two or more equality conditions, it is not leading prefix, Sort SHOULD NOT be dropped
    @Test
    public void TestCoveringPlanSortPrefix_5() throws Exception {
        String query = "SELECT t._id as tid, t.contact.phone, CAST(t.personal.age as VARCHAR) as age FROM hbase.`index_test_primary` as t " + " where t.address.state = 'wo' and t.personal.age IN (31, 32, 33, 34) and t.contact.phone < '6500003000' order by t.contact.phone";
        test(IndexPlanTest.defaultHavingIndexPlan);
        PlanTestBase.testPlanMatchingPatterns(query, new String[]{ "Sort", ".*JsonTableGroupScan.*tableName=.*index_test_primary.*indexName=i_state_age_phone" }, new String[]{  });
        // compare the results of index plan with the no-index plan
        testBuilder().optionSettingQueriesForTestQuery(IndexPlanTest.defaultHavingIndexPlan).optionSettingQueriesForBaseline(IndexPlanTest.noIndexPlan).unOrdered().sqlQuery(query).sqlBaselineQuery(query).build().run();
    }

    // last two index fields in non-Equality conditions, ORDER BY last two fields NULLS FIRST; Sort SHOULD NOT be dropped
    @Test
    public void TestCoveringPlanSortPrefix_6() throws Exception {
        String query = "SELECT t._id as tid, t.contact.phone, CAST(t.personal.age as VARCHAR) as age FROM hbase.`index_test_primary` as t " + " where t.address.state = 'wo' and t.personal.age < 35 and t.contact.phone < '6500003000' order by t.personal.age, t.contact.phone NULLS FIRST";
        test(IndexPlanTest.defaultHavingIndexPlan);
        PlanTestBase.testPlanMatchingPatterns(query, new String[]{ "Sort", ".*JsonTableGroupScan.*tableName=.*index_test_primary.*indexName=i_state_age_phone" }, new String[]{  });
        // compare the results of index plan with the no-index plan
        testBuilder().optionSettingQueriesForTestQuery(IndexPlanTest.defaultHavingIndexPlan).optionSettingQueriesForBaseline(IndexPlanTest.noIndexPlan).unOrdered().sqlQuery(query).sqlBaselineQuery(query).build().run();
    }

    // last two index fields in non-Equality conditions, ORDER BY last two fields NULLS LAST; Sort SHOULD be dropped
    @Test
    public void TestCoveringPlanSortPrefix_7() throws Exception {
        String query = "SELECT t._id as tid, t.contact.phone, CAST(t.personal.age as VARCHAR) as age FROM hbase.`index_test_primary` as t " + " where t.address.state = 'wo' and t.personal.age < 35 and t.contact.phone < '6500003000' order by t.personal.age, t.contact.phone NULLS LAST";
        test(IndexPlanTest.defaultHavingIndexPlan);
        PlanTestBase.testPlanMatchingPatterns(query, new String[]{ ".*JsonTableGroupScan.*tableName=.*index_test_primary.*indexName=i_state_age_phone" }, new String[]{ "Sort" });
        // compare the results of index plan with the no-index plan
        testBuilder().optionSettingQueriesForTestQuery(IndexPlanTest.defaultHavingIndexPlan).optionSettingQueriesForBaseline(IndexPlanTest.noIndexPlan).unOrdered().sqlQuery(query).sqlBaselineQuery(query).build().run();
    }

    @Test
    public void orderByCastCoveringPlan() throws Exception {
        String query = "SELECT t.contact.phone as phone FROM hbase.`index_test_primary` as t " + " where CAST(t.id.ssn as INT) < 100000003 order by CAST(t.id.ssn as INT)";
        test(IndexPlanTest.defaultHavingIndexPlan);
        PlanTestBase.testPlanMatchingPatterns(query, new String[]{ ".*JsonTableGroupScan.*tableName=.*index_test_primary.*indexName=" }, new String[]{ "Sort" });
        testBuilder().sqlQuery(query).ordered().baselineColumns("phone").baselineValues("6500008069").baselineColumns("phone").baselineValues("6500001411").baselineColumns("phone").baselineValues("6500001595").go();
    }

    // non-covering plan. sort by the only indexed field, sort SHOULD be removed
    @Test
    public void orderByNonCoveringPlan() throws Exception {
        String query = "SELECT t.name.lname as lname FROM hbase.`index_test_primary` as t " + " where t.id.ssn < '100000003' order by t.id.ssn";
        test(IndexPlanTest.defaultHavingIndexPlan);
        PlanTestBase.testPlanMatchingPatterns(query, new String[]{ "RowKeyJoin(.*[\n\r])+.*RestrictedJsonTableGroupScan(.*[\n\r])+.*JsonTableGroupScan.*indexName=" }, new String[]{ "Sort" });
        testBuilder().sqlQuery(query).ordered().baselineColumns("lname").baselineValues("iuMG").baselineColumns("lname").baselineValues("KpFq").baselineColumns("lname").baselineValues("bkkAvz").go();
    }

    // non-covering plan. order by cast indexed field, sort SHOULD be removed
    @Test
    public void orderByCastNonCoveringPlan() throws Exception {
        String query = "SELECT t.name.lname as lname FROM hbase.`index_test_primary` as t " + " where CAST(t.id.ssn as INT) < 100000003 order by CAST(t.id.ssn as INT)";
        test(IndexPlanTest.defaultHavingIndexPlan);
        PlanTestBase.testPlanMatchingPatterns(query, new String[]{ "RowKeyJoin(.*[\n\r])+.*RestrictedJsonTableGroupScan(.*[\n\r])+.*JsonTableGroupScan.*indexName=" }, new String[]{ "Sort" });
        testBuilder().sqlQuery(query).ordered().baselineColumns("lname").baselineValues("iuMG").baselineColumns("lname").baselineValues("KpFq").baselineColumns("lname").baselineValues("bkkAvz").go();
    }

    // non-covering, order by non leading field, and leading fields are in equality condition, Sort SHOULD be removed
    @Test
    public void NonCoveringPlan_SortPrefix_2() throws Exception {
        String query = "SELECT t.`id`.`ssn` AS `ssn` FROM hbase.`index_test_primary` as t " + " where t.address.state = 'pc' AND t.address.city>'pfrrr' AND t.address.city<'pfrrt' order by t.address.city";
        test(IndexPlanTest.defaultHavingIndexPlan);
        PlanTestBase.testPlanMatchingPatterns(query, new String[]{ "RowKeyJoin(.*[\n\r])+.*RestrictedJsonTableGroupScan(.*[\n\r])+.*JsonTableGroupScan.*indexName=i_state_city" }, new String[]{ "Sort" });
    }

    @Test
    public void orderByLimitCoveringPlan() throws Exception {
        String query = "SELECT t.contact.phone as phone FROM hbase.`index_test_primary` as t " + " where t.id.ssn < '100000003' order by t.id.ssn limit 2";
        test(IndexPlanTest.defaultHavingIndexPlan);
        // when index table has only one tablet, the SingleMergeExchange in the middle of two Limits will be removed.
        // The lower limit gets pushed into the scan
        PlanTestBase.testPlanMatchingPatterns(query, new String[]{ "Limit(.*[\n\r])+.*indexName=.*limit=2" }, new String[]{ "Sort" });
        testBuilder().sqlQuery(query).ordered().baselineColumns("phone").baselineValues("6500008069").baselineColumns("phone").baselineValues("6500001411").go();
    }

    @Test
    public void pickAnyIndexWithFTSDisabledPlan() throws Exception {
        String lowCoveringSel = "alter session set `planner.index.covering_selectivity_threshold` = 0.025";
        String defaultCoveringSel = "alter session reset `planner.index.covering_selectivity_threshold`";
        String query = "SELECT t.`contact`.`phone` AS `phone` FROM hbase.`index_test_primary` as t " + " where t.id.ssn = '100007423'";
        try {
            test(((((IndexPlanTest.defaultHavingIndexPlan) + ";") + lowCoveringSel) + ";"));
            PlanTestBase.testPlanMatchingPatterns(query, new String[]{ ".*JsonTableGroupScan.*tableName=.*index_test_primary" }, new String[]{ ".*indexName=i_ssn" });
            // Must not throw CANNOTPLANEXCEPTION
            test(((((((IndexPlanTest.defaultHavingIndexPlan) + ";") + lowCoveringSel) + ";") + (IndexPlanTest.disableFTS)) + ";"));
            PlanTestBase.testPlanMatchingPatterns(query, new String[]{ ".*JsonTableGroupScan.*tableName=.*index_test_primary.*indexName=i_ssn" }, new String[]{ "RowKeyJoin" });
        } finally {
            test((((defaultCoveringSel + ";") + (IndexPlanTest.enableFTS)) + ";"));
        }
    }

    @Test
    public void testCaseSensitive() throws Exception {
        String query = "SELECT t.contact.phone as phone FROM hbase.`index_test_primary` as t " + " where t.id.SSN = '100000003' ";
        test(IndexPlanTest.defaultHavingIndexPlan);
        PlanTestBase.testPlanMatchingPatterns(query, new String[]{ "" }, new String[]{ "indexName" });
    }

    @Test
    public void testCaseSensitiveIncludedField() throws Exception {
        String query = "SELECT t.`CONTACT`.`phone` AS `phone` FROM hbase.`index_test_primary` as t " + " where t.id.ssn = '100007423'";
        test(IndexPlanTest.defaultHavingIndexPlan);
        PlanTestBase.testPlanMatchingPatterns(query, new String[]{ "RowKeyJoin", ".*JsonTableGroupScan.*tableName=.*index_test_primary.*indexName=i_ssn" }, new String[]{  });
    }

    @Test
    public void testHashIndexNoRemovingSort() throws Exception {
        String query = "SELECT t.`contact`.`phone` as phone FROM hbase.`index_test_primary` as t " + " where t.reverseid <'10' order by t.reverseid";
        test(IndexPlanTest.defaultHavingIndexPlan);
        PlanTestBase.testPlanMatchingPatterns(query, new String[]{ "Sort", "indexName=hash_i_reverseid", "RowKeyJoin" }, new String[]{  });
    }

    @Test
    public void testNotConditionNoIndexPlan() throws Exception {
        String query = "SELECT t.`id`.`ssn` AS `ssn` FROM hbase.`index_test_primary` as t " + " where NOT t.id.ssn = '100007423'";
        test(IndexPlanTest.defaultHavingIndexPlan);
        PlanTestBase.testPlanMatchingPatterns(query, new String[]{  }, new String[]{ "indexName=" });
        String notInQuery = "SELECT t.`id`.`ssn` AS `ssn` FROM hbase.`index_test_primary` as t " + " where t.id.ssn NOT IN ('100007423', '100007424')";
        PlanTestBase.testPlanMatchingPatterns(notInQuery, new String[]{  }, new String[]{ "indexName=" });
        String notLikeQuery = "SELECT t.`id`.`ssn` AS `ssn` FROM hbase.`index_test_primary` as t " + " where t.id.ssn NOT LIKE '100007423'";
        PlanTestBase.testPlanMatchingPatterns(notLikeQuery, new String[]{  }, new String[]{ "indexName=" });
    }

    @Test
    public void testNoFilterOrderByCoveringPlan() throws Exception {
        String query = "SELECT t.`id`.`ssn` AS `ssn`, t.contact.phone as phone FROM hbase.`index_test_primary` as t " + "order by t.id.ssn limit 2";
        test(IndexPlanTest.defaultHavingIndexPlan);
        PlanTestBase.testPlanMatchingPatterns(query, new String[]{ "indexName=i_ssn" }, new String[]{ "Sort", "TopN", "RowKeyJoin" });
        testBuilder().ordered().sqlQuery(query).baselineColumns("ssn", "phone").baselineValues("100000000", "6500008069").baselineColumns("ssn", "phone").baselineValues("100000001", "6500001411").build().run();
    }

    @Test
    public void testNoFilterAndLimitOrderByCoveringPlan() throws Exception {
        String query = "SELECT t.`id`.`ssn` AS `ssn`, t.contact.phone as phone FROM hbase.`index_test_primary` as t " + "order by t.id.ssn";
        test(IndexPlanTest.defaultHavingIndexPlan);
        PlanTestBase.testPlanMatchingPatterns(query, new String[]{ "Sort" }, new String[]{ "indexName=*", "RowKeyJoin", "TopN" });
    }

    @Test
    public void testNoFilterOrderByCast() throws Exception {
        String query = "SELECT CAST(t.id.ssn as INT) AS `ssn`, t.contact.phone as phone FROM hbase.`index_test_primary` as t " + "order by CAST(t.id.ssn as INT) limit 2";
        test(IndexPlanTest.defaultHavingIndexPlan);
        PlanTestBase.testPlanMatchingPatterns(query, new String[]{ "indexName=i_cast_int_ssn" }, new String[]{ "TopN", "Sort", "RowKeyJoin" });
        testBuilder().ordered().sqlQuery(query).baselineColumns("ssn", "phone").baselineValues(100000000, "6500008069").baselineColumns("ssn", "phone").baselineValues(100000001, "6500001411").build().run();
    }

    @Test
    public void testNoFilterAndLimitOrderByCast() throws Exception {
        String query = "SELECT CAST(t.id.ssn as INT) AS `ssn`, t.contact.phone as phone FROM hbase.`index_test_primary` as t " + "order by CAST(t.id.ssn as INT)";
        test(IndexPlanTest.defaultHavingIndexPlan);
        PlanTestBase.testPlanMatchingPatterns(query, new String[]{ "Sort" }, new String[]{ "indexName=*", "TopN", "RowKeyJoin" });
    }

    @Test
    public void testNoFilterOrderByHashIndex() throws Exception {
        String query = "SELECT cast(t.activity.irs.firstlogin as timestamp) AS `firstlogin`, t.id.ssn as ssn FROM hbase.`index_test_primary` as t " + "order by cast(t.activity.irs.firstlogin as timestamp), t.id.ssn limit 2";
        test(IndexPlanTest.defaultHavingIndexPlan);
        // no collation for hash index so Sort or TopN must have been preserved
        PlanTestBase.testPlanMatchingPatterns(query, new String[]{ "(Sort|TopN)" }, new String[]{ "indexName=" });
        DateTime date = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").parseDateTime("2010-01-21 00:12:24");
        testBuilder().ordered().sqlQuery(query).baselineColumns("firstlogin", "ssn").baselineValues(date, "100005592").baselineColumns("firstlogin", "ssn").baselineValues(date, "100005844").build().run();
    }

    @Test
    public void testNoFilterOrderBySimpleField() throws Exception {
        String query = "SELECT t.reverseid as rid, t.driverlicense as lic FROM hbase.`index_test_primary` as t " + "order by t.driverlicense limit 2";
        test(IndexPlanTest.defaultHavingIndexPlan);
        PlanTestBase.testPlanMatchingPatterns(query, new String[]{ "indexName=i_lic" }, new String[]{ "Sort", "TopN" });
        testBuilder().ordered().sqlQuery(query).baselineColumns("rid", "lic").baselineValues("4539", 100000000L).baselineColumns("rid", "lic").baselineValues("943", 100000001L).build().run();
    }

    // negative case for no filter plan
    @Test
    public void testNoFilterOrderByNoIndexMatch() throws Exception {
        String query = "SELECT t.`id`.`ssn` AS `ssn`, t.contact.phone as phone FROM hbase.`index_test_primary` as t " + "order by t.name.fname limit 2";
        test(IndexPlanTest.defaultHavingIndexPlan);
        PlanTestBase.testPlanMatchingPatterns(query, new String[]{ "(Sort|TopN)" }, new String[]{ "indexName=" });
    }

    @Test
    public void testTrailingFieldIndexCovering() throws Exception {
        String query = "SELECT t.`name`.`fname` AS `fname` FROM hbase.`index_test_primary` as t " + " where cast(t.personal.age as INT)=53 AND t.contact.phone='6500005471' ";
        test(IndexPlanTest.defaultHavingIndexPlan);
        PlanTestBase.testPlanMatchingPatterns(query, new String[]{ "indexName=i_cast_age_income_phone" }, new String[]{ "RowKeyJoin" });
        testBuilder().ordered().sqlQuery(query).baselineColumns("fname").baselineValues("KfFzK").build().run();
    }

    @Test
    public void testIncludedFieldCovering() throws Exception {
        String query = "SELECT t.`contact`.`phone` AS `phone` FROM hbase.`index_test_primary` as t " + " where cast(t.personal.age as INT)=53 AND t.name.fname='KfFzK' ";
        test(IndexPlanTest.defaultHavingIndexPlan);
        PlanTestBase.testPlanMatchingPatterns(query, new String[]{ "indexName=i_cast_age_income_phone" }, new String[]{ "RowKeyJoin" });
        testBuilder().ordered().sqlQuery(query).baselineColumns("phone").baselineValues("6500005471").build().run();
    }

    @Test
    public void testWithFilterGroupBy() throws Exception {
        String query = " select t1.driverlicense from hbase.`index_test_primary` t1" + " where t1.driverlicense > 100000001 group by t1.driverlicense limit 2";
        try {
            test(IndexPlanTest.defaultHavingIndexPlan);
            test(IndexPlanTest.disableHashAgg);
            // no collation for hash index so Sort or TopN must have been preserved
            PlanTestBase.testPlanMatchingPatterns(query, new String[]{ "indexName=i_lic", "StreamAgg" }, new String[]{ "(Sort|TopN)" });
            testBuilder().ordered().sqlQuery(query).optionSettingQueriesForTestQuery(IndexPlanTest.disableHashAgg).baselineColumns("driverlicense").baselineValues(100000002L).baselineColumns("driverlicense").baselineValues(100000003L).build().run();
        } finally {
            test(IndexPlanTest.enableHashAgg);
        }
    }

    @Test
    public void testNoFilterOrderByDesc() throws Exception {
        String query = " select t1.driverlicense from hbase.`index_test_primary` t1" + " order by t1.driverlicense desc limit 2";
        test(IndexPlanTest.defaultHavingIndexPlan);
        // no collation for hash index so Sort or TopN must have been preserved
        PlanTestBase.testPlanMatchingPatterns(query, new String[]{ "(Sort|TopN)" }, new String[]{ "indexName=" });
        testBuilder().unOrdered().sqlQuery(query).baselineColumns("driverlicense").baselineValues(100009999L).baselineColumns("driverlicense").baselineValues(100009998L).build().run();
    }

    @Test
    public void testNoFilterGroupBy() throws Exception {
        String query = " select t1.driverlicense from hbase.`index_test_primary` t1" + " group by t1.driverlicense limit 2";
        try {
            test(IndexPlanTest.defaultHavingIndexPlan);
            test(IndexPlanTest.disableHashAgg);
            // no collation for hash index so Sort or TopN must have been preserved
            PlanTestBase.testPlanMatchingPatterns(query, new String[]{ "indexName=i_lic", "StreamAgg" }, new String[]{ "(Sort|TopN)" });
            testBuilder().ordered().sqlQuery(query).optionSettingQueriesForTestQuery(IndexPlanTest.disableHashAgg).baselineColumns("driverlicense").baselineValues(100000000L).baselineColumns("driverlicense").baselineValues(100000001L).build().run();
        } finally {
            test(IndexPlanTest.enableHashAgg);
        }
    }

    @Test
    public void testNoFilterGroupByCoveringPlan() throws Exception {
        String query = "SELECT t.`id`.`ssn` AS `ssn`, max(t.contact.phone) as phone FROM hbase.`index_test_primary` as t " + "group by t.id.ssn limit 2";
        try {
            test(IndexPlanTest.defaultHavingIndexPlan);
            test(IndexPlanTest.disableHashAgg);
            PlanTestBase.testPlanMatchingPatterns(query, new String[]{ "indexName=i_ssn", "StreamAgg" }, new String[]{ "Sort", "TopN", "RowKeyJoin" });
            testBuilder().ordered().sqlQuery(query).optionSettingQueriesForTestQuery(IndexPlanTest.disableHashAgg).baselineColumns("ssn", "phone").baselineValues("100000000", "6500008069").baselineColumns("ssn", "phone").baselineValues("100000001", "6500001411").build().run();
        } finally {
            test(IndexPlanTest.enableHashAgg);
        }
    }

    @Test
    public void testNoFilterGroupByCast() throws Exception {
        String query = "SELECT CAST(t.id.ssn as INT) AS `ssn`, max(t.contact.phone) as phone FROM hbase.`index_test_primary` as t " + "group by CAST(t.id.ssn as INT) limit 2";
        try {
            test(IndexPlanTest.defaultHavingIndexPlan);
            test(IndexPlanTest.disableHashAgg);
            PlanTestBase.testPlanMatchingPatterns(query, new String[]{ "indexName=i_cast_int_ssn", "StreamAgg" }, new String[]{ "TopN", "Sort", "RowKeyJoin" });
            testBuilder().ordered().sqlQuery(query).optionSettingQueriesForTestQuery(IndexPlanTest.disableHashAgg).baselineColumns("ssn", "phone").baselineValues(100000000, "6500008069").baselineColumns("ssn", "phone").baselineValues(100000001, "6500001411").build().run();
        } finally {
            test(IndexPlanTest.enableHashAgg);
        }
    }

    @Test
    public void testNoFilterGroupByHashIndex() throws Exception {
        String query = "SELECT cast(t.activity.irs.firstlogin as timestamp) AS `firstlogin`, max(t.id.ssn) as ssn FROM hbase.`index_test_primary` as t " + "group by cast(t.activity.irs.firstlogin as timestamp) limit 2";
        try {
            test(IndexPlanTest.defaultHavingIndexPlan);
            test(IndexPlanTest.disableHashAgg);
            // no collation for hash index so Sort or TopN must have been preserved
            PlanTestBase.testPlanMatchingPatterns(query, new String[]{ "(Sort|TopN)", "StreamAgg" }, new String[]{ "indexName=" });
            DateTime date1 = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").parseDateTime("2010-01-21 00:12:24");
            DateTime date2 = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").parseDateTime("2010-01-21 00:24:48");
            testBuilder().unOrdered().sqlQuery(query).optionSettingQueriesForTestQuery(IndexPlanTest.disableHashAgg).baselineColumns("firstlogin", "ssn").baselineValues(date1, "100006852").baselineColumns("firstlogin", "ssn").baselineValues(date2, "100003660").build().run();
        } finally {
            test(IndexPlanTest.enableHashAgg);
        }
    }

    @Test
    public void testNoFilterGroupBySimpleField() throws Exception {
        String query = "SELECT max(t.reverseid) as rid, t.driverlicense as lic FROM hbase.`index_test_primary` as t " + "group by t.driverlicense limit 2";
        try {
            test(IndexPlanTest.defaultHavingIndexPlan);
            test(IndexPlanTest.disableHashAgg);
            PlanTestBase.testPlanMatchingPatterns(query, new String[]{ "indexName=i_lic", "StreamAgg" }, new String[]{ "Sort", "TopN" });
            testBuilder().ordered().sqlQuery(query).optionSettingQueriesForTestQuery(IndexPlanTest.disableHashAgg).baselineColumns("rid", "lic").baselineValues("4539", 100000000L).baselineColumns("rid", "lic").baselineValues("943", 100000001L).build().run();
        } finally {
            test(IndexPlanTest.enableHashAgg);
        }
    }

    // negative case for no filter plan
    @Test
    public void testNoFilterGroupByNoIndexMatch() throws Exception {
        String query = "SELECT max(t.`id`.`ssn`) AS `ssn`, max(t.contact.phone) as phone FROM hbase.`index_test_primary` as t " + "group by t.name.fname limit 2";
        try {
            test(IndexPlanTest.defaultHavingIndexPlan);
            test(IndexPlanTest.disableHashAgg);
            PlanTestBase.testPlanMatchingPatterns(query, new String[]{ "(Sort|TopN)", "StreamAgg" }, new String[]{ "indexName=" });
        } finally {
            test(IndexPlanTest.enableHashAgg);
        }
    }

    @Test
    public void testNoFilterGroupBySimpleFieldParallel() throws Exception {
        String query = "SELECT max(t.reverseid) as rid, t.driverlicense as lic FROM hbase.`index_test_primary` as t " + "group by t.driverlicense order by t.driverlicense limit 2";
        try {
            test(IndexPlanTest.defaultHavingIndexPlan);
            test(IndexPlanTest.disableHashAgg);
            test(IndexPlanTest.sliceTargetSmall);
            PlanTestBase.testPlanMatchingPatterns(query, new String[]{ "indexName=i_lic", "StreamAgg", "HashToMergeExchange" }, new String[]{ "Sort", "TopN" });
            testBuilder().unOrdered().sqlQuery(query).optionSettingQueriesForTestQuery(IndexPlanTest.defaultHavingIndexPlan).optionSettingQueriesForTestQuery(IndexPlanTest.disableHashAgg).optionSettingQueriesForTestQuery(IndexPlanTest.sliceTargetSmall).baselineColumns("rid", "lic").baselineValues("4539", 100000000L).baselineColumns("rid", "lic").baselineValues("943", 100000001L).build().run();
        } finally {
            test(IndexPlanTest.enableHashAgg);
            test(IndexPlanTest.sliceTargetDefault);
        }
    }

    @Test
    public void testLimitPushdownCoveringPlan() throws Exception {
        String query = "SELECT t.`name`.`fname` AS `fname` FROM hbase.`index_test_primary` as t " + " where t.personal.age = 53 limit 3";
        try {
            test(((((IndexPlanTest.defaultHavingIndexPlan) + ";") + (IndexPlanTest.disableFTS)) + ";"));
            PlanTestBase.testPlanWithAttributesMatchingPatterns(query, new String[]{ ".*JsonTableGroupScan.*indexName=i_age_with_fname.*rowcount = 3.0" }, new String[]{  });
        } finally {
            test(IndexPlanTest.enableFTS);
        }
    }

    @Test
    public void testLimitPushdownOrderByCoveringPlan() throws Exception {
        String query = "SELECT t.`name`.`fname` AS `fname` FROM hbase.`index_test_primary` as t " + " where t.personal.age = 53 order by t.personal.age limit 3";
        try {
            test(((((IndexPlanTest.defaultHavingIndexPlan) + ";") + (IndexPlanTest.disableFTS)) + ";"));
            PlanTestBase.testPlanWithAttributesMatchingPatterns(query, new String[]{ ".*JsonTableGroupScan.*indexName=i_age_with_fname.*rowcount = 3.0" }, new String[]{  });
        } finally {
            test(IndexPlanTest.enableFTS);
        }
    }

    @Test
    public void testLimitPushdownNonCoveringPlan() throws Exception {
        String query = "SELECT t.`name`.`lname` AS `lname` FROM hbase.`index_test_primary` as t " + " where t.personal.age = 53 limit 7";
        try {
            test(((((IndexPlanTest.defaultHavingIndexPlan) + ";") + (IndexPlanTest.disableFTS)) + ";"));
            PlanTestBase.testPlanWithAttributesMatchingPatterns(query, new String[]{ "RowKeyJoin", ".*RestrictedJsonTableGroupScan.*tableName=.*index_test_primary.*rowcount = 7.0" }, new String[]{  });
        } finally {
            test(IndexPlanTest.enableFTS);
        }
    }

    @Test
    public void testLimitPushdownOrderByNonCoveringPlan() throws Exception {
        // Limit pushdown should NOT happen past rowkey join when ordering is required
        String query = "SELECT t.`name`.`lname` AS `lname` FROM hbase.`index_test_primary` as t " + " where t.personal.age = 53 order by t.personal.age limit 7";
        try {
            test(((((((IndexPlanTest.defaultHavingIndexPlan) + ";") + (IndexPlanTest.disableFTS)) + ";") + (IndexPlanTest.sliceTargetSmall)) + ";"));
            PlanTestBase.testPlanWithAttributesMatchingPatterns(query, new String[]{ "RowKeyJoin", ".*RestrictedJsonTableGroupScan.*" }, new String[]{ ".*tableName=.*index_test_primary.*rowcount = 7.*" });
        } finally {
            test(IndexPlanTest.enableFTS);
        }
    }

    @Test
    public void testLimit0Pushdown() throws Exception {
        // Limit pushdown should NOT happen past project with CONVERT_FROMJSON
        String query = "select convert_from(convert_to(t.`name`.`lname`, 'JSON'), 'JSON') " + "from hbase.`index_test_primary` as t limit 0";
        try {
            test(((IndexPlanTest.defaultHavingIndexPlan) + ";"));
            PlanTestBase.testPlanWithAttributesMatchingPatterns(query, new String[]{ "Limit(.*[\n\r])+.*Project.*CONVERT_FROMJSON(.*[\n\r])+.*Scan" }, new String[]{  });
        } finally {
        }
    }

    @Test
    public void testRemovalOfReduntantHashToMergeExchange() throws Exception {
        String query = "SELECT t.driverlicense as lic FROM hbase.`index_test_primary` as t " + "order by t.driverlicense limit 2";
        try {
            test(IndexPlanTest.defaultHavingIndexPlan);
            test(IndexPlanTest.sliceTargetSmall);
            PlanTestBase.testPlanMatchingPatterns(query, new String[]{ "indexName=i_lic" }, new String[]{ "HashToMergeExchange", "Sort", "TopN" });
            testBuilder().ordered().sqlQuery(query).baselineColumns("lic").baselineValues(100000000L).baselineColumns("lic").baselineValues(100000001L).build().run();
        } finally {
            test(IndexPlanTest.sliceTargetDefault);
        }
    }

    @Test
    public void testMultiPhaseAgg() throws Exception {
        String query = "select count(t.reverseid) from hbase.`index_test_primary` as t " + "group by t.driverlicense order by t.driverlicense";
        try {
            test(IndexPlanTest.defaultHavingIndexPlan);
            test(IndexPlanTest.sliceTargetSmall);
            PlanTestBase.testPlanMatchingPatterns(query, new String[]{ "indexName=i_lic", "HashToMergeExchange", "StreamAgg", "StreamAgg" }, new String[]{ "Sort", "TopN" });
        } finally {
            test(IndexPlanTest.sliceTargetDefault);
        }
    }

    @Test
    public void testHangForSimpleDistinct() throws Exception {
        String query = "select distinct t.driverlicense from hbase.`index_test_primary` as t order by t.driverlicense limit 1";
        try {
            test(IndexPlanTest.sliceTargetSmall);
            testBuilder().ordered().sqlQuery(query).baselineColumns("driverlicense").baselineValues(100000000L).build().run();
        } finally {
            test(IndexPlanTest.sliceTargetDefault);
        }
    }

    @Test
    public void testRowkeyJoinPushdown_1() throws Exception {
        // _id IN (select col ...)
        String query = "select t1.id.ssn as ssn from hbase.`index_test_primary` t1 where _id in (select t2._id " + " from hbase.`index_test_primary` t2 where t2.address.city = 'pfrrs' and t2.address.state = 'pc')";
        try {
            test(((((IndexPlanTest.incrRowKeyJoinConvSelThreshold) + ";") + (IndexPlanTest.lowNonCoveringSelectivityThreshold)) + ";"));
            PlanTestBase.testPlanMatchingPatterns(query, new String[]{ "RowKeyJoin" }, new String[]{  });
            testBuilder().sqlQuery(query).ordered().baselineColumns("ssn").baselineValues("100007423").baselineColumns("ssn").baselineValues("100008861").go();
        } finally {
            test(((((IndexPlanTest.defaultRowKeyConvSelThreshold) + ";") + (IndexPlanTest.defaultnonCoveringSelectivityThreshold)) + ";"));
        }
    }

    @Test
    public void testRowkeyJoinPushdown_2() throws Exception {
        // _id = col
        String query = "select t1.id.ssn as ssn from hbase.`index_test_primary` t1, hbase.`index_test_primary` t2 " + " where t1._id = t2._id and t2.address.city = 'pfrrs' and t2.address.state = 'pc'";
        try {
            test(((((IndexPlanTest.incrRowKeyJoinConvSelThreshold) + ";") + (IndexPlanTest.lowNonCoveringSelectivityThreshold)) + ";"));
            PlanTestBase.testPlanMatchingPatterns(query, new String[]{ "RowKeyJoin" }, new String[]{  });
            testBuilder().sqlQuery(query).ordered().baselineColumns("ssn").baselineValues("100007423").baselineColumns("ssn").baselineValues("100008861").go();
        } finally {
            test(((((IndexPlanTest.defaultRowKeyConvSelThreshold) + ";") + (IndexPlanTest.defaultnonCoveringSelectivityThreshold)) + ";"));
        }
    }

    @Test
    public void testRowkeyJoinPushdown_3() throws Exception {
        // filters on both sides of the join
        String query = "select t1.id.ssn as ssn from hbase.`index_test_primary` t1, hbase.`index_test_primary` t2 " + " where t1._id = t2._id and t1.address.city = 'pfrrs' and t2.address.city = 'pfrrs'";
        try {
            test(((((IndexPlanTest.incrRowKeyJoinConvSelThreshold) + ";") + (IndexPlanTest.lowNonCoveringSelectivityThreshold)) + ";"));
            PlanTestBase.testPlanMatchingPatterns(query, new String[]{ "RowKeyJoin" }, new String[]{  });
            testBuilder().sqlQuery(query).ordered().baselineColumns("ssn").baselineValues("100007423").baselineColumns("ssn").baselineValues("100008861").go();
        } finally {
            test(((((IndexPlanTest.defaultRowKeyConvSelThreshold) + ";") + (IndexPlanTest.defaultnonCoveringSelectivityThreshold)) + ";"));
        }
    }

    @Test
    public void testRowkeyJoinPushdown_4() throws Exception {
        // _id = cast(col as int) works since the rowids are internally cast to string!
        String query = "select t1.id.ssn as ssn from hbase.`index_test_primary` t1, hbase.`index_test_primary` t2 " + " where t1._id = cast(t2.rowid as int) and t2.address.city = 'pfrrs'";
        try {
            test(((((IndexPlanTest.incrRowKeyJoinConvSelThreshold) + ";") + (IndexPlanTest.lowNonCoveringSelectivityThreshold)) + ";"));
            PlanTestBase.testPlanMatchingPatterns(query, new String[]{ "RowKeyJoin" }, new String[]{  });
            testBuilder().sqlQuery(query).ordered().baselineColumns("ssn").baselineValues("100007423").baselineColumns("ssn").baselineValues("100008861").go();
        } finally {
            test(((((IndexPlanTest.defaultRowKeyConvSelThreshold) + ";") + (IndexPlanTest.defaultnonCoveringSelectivityThreshold)) + ";"));
        }
    }

    @Test
    public void testRowkeyJoinPushdown_5() throws Exception {
        // _id = cast(cast(col as int) as varchar(10)
        String query = "select t1.id.ssn as ssn from hbase.`index_test_primary` t1, hbase.`index_test_primary` t2 " + " where t1._id = cast(cast(t2.rowid as int) as varchar(10)) and t2.address.city = 'pfrrs'";
        try {
            test(((((IndexPlanTest.incrRowKeyJoinConvSelThreshold) + ";") + (IndexPlanTest.lowNonCoveringSelectivityThreshold)) + ";"));
            PlanTestBase.testPlanMatchingPatterns(query, new String[]{ "RowKeyJoin" }, new String[]{  });
            testBuilder().sqlQuery(query).ordered().baselineColumns("ssn").baselineValues("100007423").baselineColumns("ssn").baselineValues("100008861").go();
        } finally {
            test(((((IndexPlanTest.defaultRowKeyConvSelThreshold) + ";") + (IndexPlanTest.defaultnonCoveringSelectivityThreshold)) + ";"));
        }
    }

    @Test
    public void testRowkeyJoinPushdown_6() throws Exception {
        // _id IN (select cast(cast(col as int) as varchar(10) ... JOIN ...)
        String query = "select t1.id.ssn as ssn from hbase.`index_test_primary` t1 where _id in " + ("(select cast(cast(t2.rowid as int) as varchar(10)) from hbase.`index_test_primary` t2, hbase.`index_test_primary` t3 " + "where t2.address.city = t3.address.city and t2.name.fname = 'ubar')");
        try {
            test(((((IndexPlanTest.incrRowKeyJoinConvSelThreshold) + ";") + (IndexPlanTest.lowNonCoveringSelectivityThreshold)) + ";"));
            PlanTestBase.testPlanMatchingPatterns(query, new String[]{ "RowKeyJoin" }, new String[]{  });
            testBuilder().sqlQuery(query).ordered().baselineColumns("ssn").baselineValues("100001382").go();
        } finally {
            test(((((IndexPlanTest.defaultRowKeyConvSelThreshold) + ";") + (IndexPlanTest.defaultnonCoveringSelectivityThreshold)) + ";"));
        }
    }

    @Test
    public void testRowkeyJoinPushdown_7() throws Exception {
        // with non-covering index
        String query = "select t1.id.ssn as ssn from hbase.`index_test_primary` t1, hbase.`index_test_primary` t2 " + "where t1._id = t2.rowid and t2.address.city = 'pfrrs'";
        try {
            test(((((IndexPlanTest.incrRowKeyJoinConvSelThreshold) + ";") + (IndexPlanTest.incrnonCoveringSelectivityThreshold)) + ";"));
            PlanTestBase.testPlanMatchingPatterns(query, new String[]{ "RowKeyJoin", "RestrictedJsonTableGroupScan", "RowKeyJoin", "Scan.*condition=\\(address.city = \"pfrrs\"\\)" }, new String[]{  });
            testBuilder().sqlQuery(query).ordered().baselineColumns("ssn").baselineValues("100007423").baselineColumns("ssn").baselineValues("100008861").go();
        } finally {
            test(((((IndexPlanTest.defaultRowKeyConvSelThreshold) + ";") + (IndexPlanTest.defaultnonCoveringSelectivityThreshold)) + ";"));
        }
    }

    @Test
    public void testRowkeyJoinPushdown_8() throws Exception {
        // with covering index
        String query = "select t1.id.ssn as ssn from hbase.`index_test_primary` t1, hbase.`index_test_primary` t2 " + "where t1._id = t2.rowid and t2.rowid = '1012'";
        try {
            test(IndexPlanTest.incrRowKeyJoinConvSelThreshold);
            PlanTestBase.testPlanMatchingPatterns(query, new String[]{ "RowKeyJoin", "indexName=i_rowid_cast_date_birthdate" }, new String[]{  });
            testBuilder().sqlQuery(query).ordered().baselineColumns("ssn").baselineValues("100007423").go();
        } finally {
            test(IndexPlanTest.defaultRowKeyConvSelThreshold);
        }
    }

    @Test
    public void testRowkeyJoinPushdown_9() throws Exception {
        // Negative test - rowkey join should not be present
        String query = "select t1.id.ssn as ssn from hbase.`index_test_primary` t1 where cast(_id as varchar(10)) in " + "(select t2._id from hbase.`index_test_primary` t2 where t2.address.city = 'pfrrs')";
        try {
            test(((((IndexPlanTest.incrRowKeyJoinConvSelThreshold) + ";") + (IndexPlanTest.lowNonCoveringSelectivityThreshold)) + ";"));
            PlanTestBase.testPlanMatchingPatterns(query, new String[]{  }, new String[]{ "RowKeyJoin" });
            testBuilder().sqlQuery(query).ordered().baselineColumns("ssn").baselineValues("100007423").baselineColumns("ssn").baselineValues("100008861").go();
        } finally {
            test(((((IndexPlanTest.defaultRowKeyConvSelThreshold) + ";") + (IndexPlanTest.defaultnonCoveringSelectivityThreshold)) + ";"));
        }
    }

    @Test
    public void testRowkeyJoinPushdown_10() throws Exception {
        // Negative test - rowkey join should not be present
        String query = "select t1.id.ssn as ssn from hbase.`index_test_primary` t1, hbase.`index_test_primary` t2 " + " where cast(t1._id as varchar(10)) = cast(t2._id as varchar(10)) and t2.address.city = 'pfrrs'";
        try {
            test(((((IndexPlanTest.incrRowKeyJoinConvSelThreshold) + ";") + (IndexPlanTest.lowNonCoveringSelectivityThreshold)) + ";"));
            PlanTestBase.testPlanMatchingPatterns(query, new String[]{  }, new String[]{ "RowKeyJoin" });
            testBuilder().sqlQuery(query).ordered().baselineColumns("ssn").baselineValues("100007423").baselineColumns("ssn").baselineValues("100008861").go();
        } finally {
            test(((((IndexPlanTest.defaultRowKeyConvSelThreshold) + ";") + (IndexPlanTest.defaultnonCoveringSelectivityThreshold)) + ";"));
        }
    }

    @Test
    public void testRowkeyJoinPushdown_11() throws Exception {
        // Negative test - rowkey join should not be present
        String query = "select t1.id.ssn as ssn from hbase.`index_test_primary` t1 where cast(_id as varchar(10)) in " + ("(select t2._id from hbase.`index_test_primary` t2, hbase.`index_test_primary` t3 where t2.address.city = t3.address.city " + "and t2.address.city = 'pfrrs')");
        try {
            test(((((IndexPlanTest.incrRowKeyJoinConvSelThreshold) + ";") + (IndexPlanTest.lowNonCoveringSelectivityThreshold)) + ";"));
            PlanTestBase.testPlanMatchingPatterns(query, new String[]{  }, new String[]{ "RowKeyJoin" });
            testBuilder().sqlQuery(query).ordered().baselineColumns("ssn").baselineValues("100007423").baselineColumns("ssn").baselineValues("100008861").go();
        } finally {
            test(((((IndexPlanTest.defaultRowKeyConvSelThreshold) + ";") + (IndexPlanTest.defaultnonCoveringSelectivityThreshold)) + ";"));
        }
    }

    @Test
    public void TestIndexScanWithDescOrderByNullsLast() throws Exception {
        String query = "select t.personal.age from hbase.`index_test_primary` t order by t.personal.age desc nulls last limit 1";
        try {
            test(((((IndexPlanTest.defaultHavingIndexPlan) + ";") + (IndexPlanTest.lowRowKeyJoinBackIOFactor)) + ";"));
            PlanTestBase.testPlanMatchingPatterns(query, new String[]{  }, new String[]{ ".*indexName=i_age_desc.*" });
        } finally {
            test(IndexPlanTest.defaultRowKeyJoinBackIOFactor);
        }
        return;
    }
}

