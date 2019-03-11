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


import org.apache.drill.categories.OperatorTest;
import org.apache.drill.test.BaseTestQuery;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category(OperatorTest.class)
public class TestJoinNullable extends BaseTestQuery {
    static final Logger logger = LoggerFactory.getLogger(TestJoinNullable.class);

    /**
     * InnerJoin on nullable cols, HashJoin
     */
    @Test
    public void testHashInnerJoinOnNullableColumns() throws Exception {
        String query = "select t1.a1, t1.b1, t2.a2, t2.b2 from cp.`jsoninput/nullable1.json` t1, " + " cp.`jsoninput/nullable2.json` t2 where t1.b1 = t2.b2";
        final int expectedRecordCount = 1;
        TestJoinNullable.enableJoin(true, false);
        final int actualRecordCount = BaseTestQuery.testSql(query);
        Assert.assertEquals("Number of output rows", expectedRecordCount, actualRecordCount);
    }

    /**
     * LeftOuterJoin on nullable cols, HashJoin
     */
    @Test
    public void testHashLOJOnNullableColumns() throws Exception {
        String query = "select t1.a1, t1.b1, t2.a2, t2.b2 from cp.`jsoninput/nullable1.json` t1 " + (" left outer join cp.`jsoninput/nullable2.json` t2 " + " on t1.b1 = t2.b2");
        final int expectedRecordCount = 2;
        TestJoinNullable.enableJoin(true, false);
        final int actualRecordCount = BaseTestQuery.testSql(query);
        Assert.assertEquals("Number of output rows", expectedRecordCount, actualRecordCount);
    }

    /**
     * RightOuterJoin on nullable cols, HashJoin
     */
    @Test
    public void testHashROJOnNullableColumns() throws Exception {
        String query = "select t1.a1, t1.b1, t2.a2, t2.b2 from cp.`jsoninput/nullable1.json` t1 " + (" right outer join cp.`jsoninput/nullable2.json` t2 " + " on t1.b1 = t2.b2");
        final int expectedRecordCount = 4;
        TestJoinNullable.enableJoin(true, false);
        final int actualRecordCount = BaseTestQuery.testSql(query);
        Assert.assertEquals("Number of output rows", expectedRecordCount, actualRecordCount);
    }

    /**
     * FullOuterJoin on nullable cols, HashJoin
     */
    @Test
    public void testHashFOJOnNullableColumns() throws Exception {
        String query = "select t1.a1, t1.b1, t2.a2, t2.b2 from cp.`jsoninput/nullable1.json` t1 " + (" full outer join cp.`jsoninput/nullable2.json` t2 " + " on t1.b1 = t2.b2");
        final int expectedRecordCount = +5;
        TestJoinNullable.enableJoin(true, false);
        final int actualRecordCount = BaseTestQuery.testSql(query);
        Assert.assertEquals("Number of output rows", expectedRecordCount, actualRecordCount);
    }

    /**
     * InnerJoin on nullable cols, MergeJoin
     */
    @Test
    public void testMergeInnerJoinOnNullableColumns() throws Exception {
        String query = "select t1.a1, t1.b1, t2.a2, t2.b2 " + (("  from cp.`jsoninput/nullable1.json` t1, " + "       cp.`jsoninput/nullable2.json` t2 ") + " where t1.b1 = t2.b2");
        final int expectedRecordCount = 1;
        TestJoinNullable.enableJoin(false, true);
        final int actualRecordCount = BaseTestQuery.testSql(query);
        Assert.assertEquals("Number of output rows", expectedRecordCount, actualRecordCount);
    }

    /**
     * LeftOuterJoin on nullable cols, MergeJoin
     */
    @Test
    public void testMergeLOJNullable() throws Exception {
        String query = "select t1.a1, t1.b1, t2.a2, t2.b2 from cp.`jsoninput/nullable1.json` t1 " + (" left outer join cp.`jsoninput/nullable2.json` t2 " + " on t1.b1 = t2.b2");
        final int expectedRecordCount = 2;
        TestJoinNullable.enableJoin(false, true);
        final int actualRecordCount = BaseTestQuery.testSql(query);
        Assert.assertEquals("Number of output rows", expectedRecordCount, actualRecordCount);
    }

    /**
     * RightOuterJoin on nullable cols, MergeJoin
     */
    @Test
    public void testMergeROJOnNullableColumns() throws Exception {
        String query = "select t1.a1, t1.b1, t2.a2, t2.b2 from cp.`jsoninput/nullable1.json` t1 " + (" right outer join cp.`jsoninput/nullable2.json` t2 " + " on t1.b1 = t2.b2");
        final int expectedRecordCount = 4;
        TestJoinNullable.enableJoin(false, true);
        final int actualRecordCount = BaseTestQuery.testSql(query);
        Assert.assertEquals("Number of output rows", expectedRecordCount, actualRecordCount);
    }

    /**
     * Left outer join, merge, nullable col. - unordered inputs.
     */
    @Test
    public void testMergeLOJNullableNoOrderedInputs() throws Exception {
        String query = "SELECT * " + (("FROM               cp.`jsoninput/nullableOrdered1.json` t1 " + "   left outer join cp.`jsoninput/nullableOrdered2.json` t2 ") + "      using ( key )");
        final int expectedRecordCount = 6;
        TestJoinNullable.enableJoin(false, true);
        final int actualRecordCount = BaseTestQuery.testSql(query);
        Assert.assertEquals("Number of output rows", expectedRecordCount, actualRecordCount);
    }

    /**
     * Left outer join, merge, nullable col. - ordered right, ASC NULLS FIRST (nulls low).
     */
    @Test
    public void testMergeLOJNullableOneOrderedInputAscNullsFirst() throws Exception {
        String query = "SELECT * " + ((((("from         cp.`jsoninput/nullableOrdered1.json` t1 " + "  LEFT OUTER JOIN ") + "    ( SELECT key, data ") + "        FROM cp.`jsoninput/nullableOrdered2.json` t2 ") + "        ORDER BY 1 ASC NULLS FIRST ) t2 ") + "    USING ( key )");
        final int expectedRecordCount = 6;
        TestJoinNullable.enableJoin(false, true);
        final int actualRecordCount = BaseTestQuery.testSql(query);
        Assert.assertEquals("Number of output rows", expectedRecordCount, actualRecordCount);
    }

    /**
     * Left outer join, merge, nullable col.  - ordered right, ASC NULLS LAST (nulls high).
     */
    @Test
    public void testMergeLOJNullableOneOrderedInputAscNullsLast() throws Exception {
        String query = "SELECT * " + ((((("FROM         cp.`jsoninput/nullableOrdered1.json` t1 " + "  LEFT OUTER JOIN ") + "    ( SELECT key, data ") + "        FROM cp.`jsoninput/nullableOrdered2.json` t2 ") + "        ORDER BY 1 ASC NULLS LAST ) t2 ") + "    USING ( key )");
        final int expectedRecordCount = 6;
        TestJoinNullable.enableJoin(false, true);
        final int actualRecordCount = BaseTestQuery.testSql(query);
        Assert.assertEquals("Number of output rows", expectedRecordCount, actualRecordCount);
    }

    /**
     * Left outer join, merge, nullable col. - ordered right, DESC NULLS FIRST (nulls high).
     */
    @Test
    public void testMergeLOJNullableOneOrderedInputDescNullsFirst() throws Exception {
        String query = "SELECT * " + ((((("FROM         cp.`jsoninput/nullableOrdered1.json` t1 " + "  LEFT OUTER JOIN ") + "    ( SELECT key, data ") + "        FROM cp.`jsoninput/nullableOrdered2.json` t2 ") + "        ORDER BY 1 DESC NULLS FIRST ) t2 ") + "    USING ( key )");
        final int expectedRecordCount = 6;
        TestJoinNullable.enableJoin(false, true);
        final int actualRecordCount = BaseTestQuery.testSql(query);
        Assert.assertEquals("Number of output rows", expectedRecordCount, actualRecordCount);
    }

    /**
     * Left outer join, merge, nullable col. - ordered right, DESC NULLS LAST (nulls low).
     */
    @Test
    public void testMergeLOJNullableOneOrderedInputDescNullsLast() throws Exception {
        String query = "SELECT * " + ((((("FROM         cp.`jsoninput/nullableOrdered1.json` t1 " + "  LEFT OUTER JOIN ") + "    ( SELECT key, data ") + "        FROM cp.`jsoninput/nullableOrdered2.json` t2 ") + "        ORDER BY 1 DESC NULLS LAST ) t2 ") + "    USING ( key )");
        final int expectedRecordCount = 6;
        TestJoinNullable.enableJoin(false, true);
        final int actualRecordCount = BaseTestQuery.testSql(query);
        Assert.assertEquals("Number of output rows", expectedRecordCount, actualRecordCount);
    }

    /**
     * Left outer join, merge, nullable col. - ordered inputs, both ASC NULLS FIRST (nulls low).
     */
    @Test
    public void testMergeLOJNullableBothInputsOrderedAscNullsFirstVsAscNullsFirst() throws Exception {
        String query = "SELECT * " + ((((((("from ( SELECT key, data " + "         FROM cp.`jsoninput/nullableOrdered1.json` ") + "         ORDER BY 1 ASC NULLS FIRST ) t1 ") + "  LEFT OUTER JOIN ") + "     ( SELECT key, data ") + "         FROM cp.`jsoninput/nullableOrdered2.json` ") + "         ORDER BY 1 ASC NULLS FIRST ) t2 ") + "    USING ( key )");
        final int expectedRecordCount = 6;
        TestJoinNullable.enableJoin(false, true);
        final int actualRecordCount = BaseTestQuery.testSql(query);
        Assert.assertEquals("Number of output rows", expectedRecordCount, actualRecordCount);
    }

    /**
     * Left outer join, merge, nullable col. - ordered inputs, different null order.
     */
    @Test
    public void testMergeLOJNullableBothInputsOrderedAscNullsLastVsAscNullsFirst() throws Exception {
        String query = "SELECT * " + ((((((("from ( SELECT key, data " + "         FROM cp.`jsoninput/nullableOrdered1.json` ") + "         ORDER BY 1 ASC NULLS LAST  ) t1 ") + "  LEFT OUTER JOIN ") + "     ( SELECT key, data ") + "         FROM cp.`jsoninput/nullableOrdered2.json` ") + "         ORDER BY 1 ASC NULLS FIRST ) t2 ") + "    USING ( key )");
        final int expectedRecordCount = 6;
        TestJoinNullable.enableJoin(false, true);
        final int actualRecordCount = BaseTestQuery.testSql(query);
        Assert.assertEquals("Number of output rows", expectedRecordCount, actualRecordCount);
    }

    /**
     * Left outer join, merge, nullable col. - ordered inputs, other different null order.
     */
    @Test
    public void testMergeLOJNullableBothInputsOrderedAscNullsFirstVsAscNullsLast() throws Exception {
        String query = "SELECT * " + ((((((("from ( SELECT key, data " + "         FROM cp.`jsoninput/nullableOrdered1.json` ") + "         ORDER BY 1 ASC NULLS FIRST ) t1 ") + "  LEFT OUTER JOIN ") + "     ( SELECT key, data ") + "         FROM cp.`jsoninput/nullableOrdered2.json` ") + "         ORDER BY 1 ASC NULLS LAST  ) t2 ") + "    USING ( key )");
        final int expectedRecordCount = 6;
        TestJoinNullable.enableJoin(false, true);
        final int actualRecordCount = BaseTestQuery.testSql(query);
        Assert.assertEquals("Number of output rows", expectedRecordCount, actualRecordCount);
    }

    /**
     * Left outer join, merge, nullable col. - ordered inputs, both ASC NULLS LAST (nulls high)
     */
    @Test
    public void testMergeLOJNullableBothInputsOrderedAscNullsLastVsAscNullsLast() throws Exception {
        String query = "SELECT * " + ((((((("from ( SELECT key, data " + "         FROM cp.`jsoninput/nullableOrdered1.json` ") + "         ORDER BY 1 ASC NULLS LAST  ) t1 ") + "  LEFT OUTER JOIN ") + "     ( SELECT key, data ") + "         FROM cp.`jsoninput/nullableOrdered2.json` ") + "         ORDER BY 1 ASC NULLS LAST  ) t2 ") + "    USING ( key )");
        final int expectedRecordCount = 6;
        TestJoinNullable.enableJoin(false, true);
        final int actualRecordCount = BaseTestQuery.testSql(query);
        Assert.assertEquals("Number of output rows", expectedRecordCount, actualRecordCount);
    }

    /**
     * Left outer join, merge, nullable col. - ordered inputs, DESC vs. ASC, NULLS
     * FIRST (nulls high vs. nulls low).
     */
    @Test
    public void testMergeLOJNullableBothInputsOrderedDescNullsFirstVsAscNullsFirst() throws Exception {
        String query = "SELECT * " + ((((((("from ( SELECT key, data " + "         FROM cp.`jsoninput/nullableOrdered1.json` ") + "         ORDER BY 1 DESC NULLS FIRST ) t1 ") + "  LEFT OUTER JOIN ") + "     ( SELECT key, data ") + "         FROM cp.`jsoninput/nullableOrdered2.json` ") + "         ORDER BY 1 ASC NULLS FIRST ) t2 ") + "    USING ( key )");
        final int expectedRecordCount = 6;
        TestJoinNullable.enableJoin(false, true);
        final int actualRecordCount = BaseTestQuery.testSql(query);
        Assert.assertEquals("Number of output rows", expectedRecordCount, actualRecordCount);
    }

    /**
     * Left outer join, merge, nullable col. - ordered inputs, DESC vs. ASC, NULLS
     * LAST vs. FIRST (both nulls low).
     */
    @Test
    public void testMergeLOJNullableBothInputsOrderedDescNullsLastVsAscNullsFirst() throws Exception {
        String query = "SELECT * " + ((((((("from ( SELECT key, data " + "         FROM cp.`jsoninput/nullableOrdered1.json` ") + "         ORDER BY 1 DESC NULLS LAST  ) t1 ") + "  LEFT OUTER JOIN ") + "     ( SELECT key, data ") + "         FROM cp.`jsoninput/nullableOrdered2.json` ") + "         ORDER BY 1 ASC NULLS FIRST ) t2 ") + "    USING ( key )");
        final int expectedRecordCount = 6;
        TestJoinNullable.enableJoin(false, true);
        final int actualRecordCount = BaseTestQuery.testSql(query);
        Assert.assertEquals("Number of output rows", expectedRecordCount, actualRecordCount);
    }

    /**
     * Left outer join, merge, nullable col. - ordered inputs, DESC vs. ASC, NULLS
     * FIRST vs. LAST (both nulls high).
     */
    @Test
    public void testMergeLOJNullableBothInputsOrderedDescNullsFirstVsAscNullsLast() throws Exception {
        String query = "SELECT * " + ((((((("from ( SELECT key, data " + "         FROM cp.`jsoninput/nullableOrdered1.json` ") + "         ORDER BY 1 DESC NULLS FIRST ) t1 ") + "  LEFT OUTER JOIN ") + "     ( SELECT key, data ") + "         FROM cp.`jsoninput/nullableOrdered2.json` ") + "         ORDER BY 1 ASC NULLS LAST  ) t2 ") + "    USING ( key )");
        final int expectedRecordCount = 6;
        TestJoinNullable.enableJoin(false, true);
        final int actualRecordCount = BaseTestQuery.testSql(query);
        Assert.assertEquals("Number of output rows", expectedRecordCount, actualRecordCount);
    }

    /**
     * Left outer join, merge, nullable col. - ordered inputs, DESC vs. ASC, NULLS
     * LAST (nulls low vs. nulls high).
     */
    @Test
    public void testMergeLOJNullableBothInputsOrderedDescNullsLastVsAscNullsLast() throws Exception {
        String query = "SELECT * " + ((((((("from ( SELECT key, data " + "         FROM cp.`jsoninput/nullableOrdered1.json` ") + "         ORDER BY 1 DESC NULLS LAST  ) t1 ") + "  LEFT OUTER JOIN ") + "     ( SELECT key, data ") + "         FROM cp.`jsoninput/nullableOrdered2.json` ") + "         ORDER BY 1 ASC NULLS LAST  ) t2 ") + "    USING ( key )");
        final int expectedRecordCount = 6;
        TestJoinNullable.enableJoin(false, true);
        final int actualRecordCount = BaseTestQuery.testSql(query);
        Assert.assertEquals("Number of output rows", expectedRecordCount, actualRecordCount);
    }

    @Test
    public void testNullEqualInWhereConditionHashJoin() throws Exception {
        final String query = "SELECT * FROM " + (("cp.`jsoninput/nullableOrdered1.json` t1, " + "cp.`jsoninput/nullableOrdered2.json` t2 ") + "WHERE t1.key = t2.key OR (t1.key IS NULL AND t2.key IS NULL)");
        nullEqualJoinHelper(query);
    }

    @Test
    public void testNullEqualInWhereConditionMergeJoin() throws Exception {
        try {
            BaseTestQuery.test("alter session set `planner.enable_hashjoin` = false");
            final String query = "SELECT * FROM " + (("cp.`jsoninput/nullableOrdered1.json` t1, " + "cp.`jsoninput/nullableOrdered2.json` t2 ") + "WHERE t1.key = t2.key OR (t1.key IS NULL AND t2.key IS NULL)");
            nullEqualJoinHelper(query);
        } finally {
            BaseTestQuery.test("alter session set `planner.enable_hashjoin` = true");
        }
    }

    @Test
    public void testNullEqualHashJoin() throws Exception {
        final String query = "SELECT * FROM " + (("cp.`jsoninput/nullableOrdered1.json` t1 JOIN " + "cp.`jsoninput/nullableOrdered2.json` t2 ") + "ON t1.key = t2.key OR (t1.key IS NULL AND t2.key IS NULL)");
        nullEqualJoinHelper(query);
    }

    @Test
    public void testNullEqualMergeJoin() throws Exception {
        try {
            BaseTestQuery.test("alter session set `planner.enable_hashjoin` = false");
            final String query = "SELECT * FROM " + (("cp.`jsoninput/nullableOrdered1.json` t1 JOIN " + "cp.`jsoninput/nullableOrdered2.json` t2 ") + "ON t1.key = t2.key OR (t1.key IS NULL AND t2.key IS NULL)");
            nullEqualJoinHelper(query);
        } finally {
            BaseTestQuery.test("alter session set `planner.enable_hashjoin` = true");
        }
    }

    @Test
    public void testNullEqualAdditionFilter() throws Exception {
        final String query = "SELECT * FROM " + (("cp.`jsoninput/nullableOrdered1.json` t1 JOIN " + "cp.`jsoninput/nullableOrdered2.json` t2 ") + "ON (t1.key = t2.key OR (t1.key IS NULL AND t2.key IS NULL)) AND t1.data LIKE '%1%'");
        BaseTestQuery.testBuilder().sqlQuery(query).unOrdered().baselineColumns("key", "data", "data0", "key0").baselineValues(null, "L_null_1", "R_null_1", null).baselineValues("A", "L_A_1", "R_A_1", "A").baselineValues(null, "L_null_1", "R_null_2", null).baselineValues(null, "L_null_1", "R_null_3", null).go();
    }

    @Test
    public void testMixedEqualAndIsNotDistinctHashJoin() throws Exception {
        TestJoinNullable.enableJoin(true, false);
        try {
            final String query = "SELECT * FROM " + (("cp.`jsoninput/nullEqualJoin1.json` t1 JOIN " + "cp.`jsoninput/nullEqualJoin2.json` t2 ") + "ON t1.key = t2.key AND t1.data is not distinct from t2.data");
            nullMixedComparatorEqualJoinHelper(query);
        } finally {
            TestJoinNullable.resetJoinOptions();
        }
    }

    @Test
    public void testMixedEqualAndIsNotDistinctMergeJoin() throws Exception {
        TestJoinNullable.enableJoin(false, true);
        try {
            final String query = "SELECT * FROM " + (("cp.`jsoninput/nullEqualJoin1.json` t1 JOIN " + "cp.`jsoninput/nullEqualJoin2.json` t2 ") + "ON t1.key = t2.key AND t1.data is not distinct from t2.data");
            nullMixedComparatorEqualJoinHelper(query);
        } finally {
            TestJoinNullable.resetJoinOptions();
        }
    }

    @Test
    public void testMixedEqualAndIsNotDistinctFilterHashJoin() throws Exception {
        TestJoinNullable.enableJoin(true, false);
        try {
            final String query = "SELECT * FROM " + ((("cp.`jsoninput/nullEqualJoin1.json` t1 JOIN " + "cp.`jsoninput/nullEqualJoin2.json` t2 ") + "ON t1.key = t2.key ") + "WHERE t1.data is not distinct from t2.data");
            // Expected the filter to be pushed into the join
            nullMixedComparatorEqualJoinHelper(query);
        } finally {
            TestJoinNullable.resetJoinOptions();
        }
    }

    @Test
    public void testMixedEqualAndIsNotDistinctFilterMergeJoin() throws Exception {
        TestJoinNullable.enableJoin(false, true);
        try {
            final String query = "SELECT * FROM " + ((("cp.`jsoninput/nullEqualJoin1.json` t1 JOIN " + "cp.`jsoninput/nullEqualJoin2.json` t2 ") + "ON t1.key = t2.key ") + "WHERE t1.data is not distinct from t2.data");
            // Expected the filter to be pushed into the join
            nullMixedComparatorEqualJoinHelper(query);
        } finally {
            TestJoinNullable.resetJoinOptions();
        }
    }

    @Test
    public void testMixedEqualAndEqualOrHashJoin() throws Exception {
        TestJoinNullable.enableJoin(true, false);
        try {
            final String query = "SELECT * FROM " + ((("cp.`jsoninput/nullEqualJoin1.json` t1 JOIN " + "cp.`jsoninput/nullEqualJoin2.json` t2 ") + "ON t1.key = t2.key ") + "AND ((t1.data=t2.data) OR (t1.data IS NULL AND t2.data IS NULL))");
            // Expected the filter to be pushed into the join
            nullMixedComparatorEqualJoinHelper(query);
        } finally {
            TestJoinNullable.resetJoinOptions();
        }
    }

    @Test
    public void testMixedEqualAndEqualOrMergeJoin() throws Exception {
        TestJoinNullable.enableJoin(false, true);
        try {
            final String query = "SELECT * FROM " + ((("cp.`jsoninput/nullEqualJoin1.json` t1 JOIN " + "cp.`jsoninput/nullEqualJoin2.json` t2 ") + "ON t1.key = t2.key ") + "AND ((t1.data=t2.data) OR (t1.data IS NULL AND t2.data IS NULL))");
            // Expected the filter to be pushed into the join
            nullMixedComparatorEqualJoinHelper(query);
        } finally {
            TestJoinNullable.resetJoinOptions();
        }
    }

    // Full join with USING clause uses COALESCE internally
    // DRILL-6962
    @Test
    public void testFullJoinUsingUntypedNullColumn() throws Exception {
        try {
            TestJoinNullable.enableJoin(true, true);
            String query = "select * from " + (("(select n_nationkey, n_name, coalesce(unk1, unk2) as not_exists from cp.`tpch/nation.parquet`) t1 full join " + "(select r_name, r_comment, coalesce(unk1, unk2) as not_exists from cp.`tpch/region.parquet`) t2 ") + "using (not_exists)");
            BaseTestQuery.testBuilder().sqlQuery(query).unOrdered().expectsNumRecords(30).go();
        } finally {
            TestJoinNullable.resetJoinOptions();
        }
    }
}

