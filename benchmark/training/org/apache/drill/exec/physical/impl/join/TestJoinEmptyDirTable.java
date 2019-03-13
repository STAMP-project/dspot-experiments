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
package org.apache.drill.exec.physical.impl.join;


import PlannerSettings.JOIN_OPTIMIZATION;
import org.apache.drill.PlanTestBase;
import org.apache.drill.categories.OperatorTest;
import org.apache.drill.common.exceptions.UserRemoteException;
import org.apache.drill.test.BaseTestQuery;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category(OperatorTest.class)
public class TestJoinEmptyDirTable extends JoinTestBase {
    private static final String EMPTY_DIRECTORY = "empty_directory";

    @Test
    public void testHashInnerJoinWithLeftEmptyDirTable() throws Exception {
        try {
            String query = String.format(("select t1.`employee_id`, t1.`full_name`, t2.`employee_id`, t2.`full_name` " + "from dfs.tmp.`%s` t1 inner join cp.`employee.json` t2 on t1.`full_name` = t2.`full_name`"), TestJoinEmptyDirTable.EMPTY_DIRECTORY);
            final int expectedRecordCount = 0;
            JoinTestBase.enableJoin(true, false, false);
            PlanTestBase.testPlanMatchingPatterns(query, new String[]{ JoinTestBase.HJ_PATTERN, JoinTestBase.INNER_JOIN_TYPE }, new String[]{  });
            final int actualRecordCount = BaseTestQuery.testSql(query);
            Assert.assertEquals("Number of output rows", expectedRecordCount, actualRecordCount);
        } finally {
            JoinTestBase.resetJoinOptions();
        }
    }

    @Test
    public void testHashInnerJoinWithRightEmptyDirTable() throws Exception {
        try {
            String query = String.format(("select t1.`employee_id`, t1.`full_name`, t2.`employee_id`, t2.`full_name` " + "from cp.`employee.json` t1 inner join dfs.tmp.`%s` t2 on t1.`full_name` = t2.`full_name`"), TestJoinEmptyDirTable.EMPTY_DIRECTORY);
            final int expectedRecordCount = 0;
            JoinTestBase.enableJoin(true, false, false);
            PlanTestBase.testPlanMatchingPatterns(query, new String[]{ JoinTestBase.HJ_PATTERN, JoinTestBase.INNER_JOIN_TYPE }, new String[]{  });
            final int actualRecordCount = BaseTestQuery.testSql(query);
            Assert.assertEquals("Number of output rows", expectedRecordCount, actualRecordCount);
        } finally {
            JoinTestBase.resetJoinOptions();
        }
    }

    @Test
    public void testHashInnerJoinWithBothEmptyDirTables() throws Exception {
        try {
            String query = String.format(("select t1.`employee_id`, t1.`full_name`, t2.`employee_id`, t2.`full_name` " + "from dfs.tmp.`%1$s` t1 inner join dfs.tmp.`%1$s` t2 on t1.`full_name` = t2.`full_name`"), TestJoinEmptyDirTable.EMPTY_DIRECTORY);
            final int expectedRecordCount = 0;
            JoinTestBase.enableJoin(true, false, false);
            PlanTestBase.testPlanMatchingPatterns(query, new String[]{ JoinTestBase.HJ_PATTERN, JoinTestBase.INNER_JOIN_TYPE }, new String[]{  });
            final int actualRecordCount = BaseTestQuery.testSql(query);
            Assert.assertEquals("Number of output rows", expectedRecordCount, actualRecordCount);
        } finally {
            JoinTestBase.resetJoinOptions();
        }
    }

    @Test
    public void testHashLeftJoinWithRightEmptyDirTable() throws Exception {
        try {
            String query = String.format(("select t1.`employee_id`, t1.`full_name`, t2.`employee_id`, t2.`full_name` " + "from cp.`employee.json` t1 left join dfs.tmp.`%s` t2 on t1.`full_name` = t2.`full_name`"), TestJoinEmptyDirTable.EMPTY_DIRECTORY);
            final int expectedRecordCount = 1155;
            JoinTestBase.enableJoin(true, false, false);
            PlanTestBase.testPlanMatchingPatterns(query, new String[]{ JoinTestBase.HJ_PATTERN, JoinTestBase.LEFT_JOIN_TYPE }, new String[]{  });
            final int actualRecordCount = BaseTestQuery.testSql(query);
            Assert.assertEquals("Number of output rows", expectedRecordCount, actualRecordCount);
        } finally {
            JoinTestBase.resetJoinOptions();
        }
    }

    @Test
    public void testHashRightJoinWithRightEmptyDirTable() throws Exception {
        try {
            String query = String.format(("select t1.`employee_id`, t1.`full_name`, t2.`employee_id`, t2.`full_name` " + "from cp.`employee.json` t1 right join dfs.tmp.`%s` t2 on t1.`full_name` = t2.`full_name`"), TestJoinEmptyDirTable.EMPTY_DIRECTORY);
            final int expectedRecordCount = 0;
            JoinTestBase.enableJoin(true, false, false);
            PlanTestBase.testPlanMatchingPatterns(query, new String[]{ JoinTestBase.HJ_PATTERN, JoinTestBase.RIGHT_JOIN_TYPE }, new String[]{  });
            final int actualRecordCount = BaseTestQuery.testSql(query);
            Assert.assertEquals("Number of output rows", expectedRecordCount, actualRecordCount);
        } finally {
            JoinTestBase.resetJoinOptions();
        }
    }

    @Test
    public void testMergeInnerJoinWithLeftEmptyDirTable() throws Exception {
        try {
            String query = String.format(("select t1.`employee_id`, t1.`full_name`, t2.`employee_id`, t2.`full_name` " + "from dfs.tmp.`%s` t1 inner join cp.`employee.json` t2 on t1.`full_name` = t2.`full_name`"), TestJoinEmptyDirTable.EMPTY_DIRECTORY);
            final int expectedRecordCount = 0;
            JoinTestBase.enableJoin(false, true, false);
            PlanTestBase.testPlanMatchingPatterns(query, new String[]{ JoinTestBase.MJ_PATTERN, JoinTestBase.INNER_JOIN_TYPE }, new String[]{  });
            final int actualRecordCount = BaseTestQuery.testSql(query);
            Assert.assertEquals("Number of output rows", expectedRecordCount, actualRecordCount);
        } finally {
            JoinTestBase.resetJoinOptions();
        }
    }

    @Test
    public void testMergeInnerJoinWithRightEmptyDirTable() throws Exception {
        try {
            String query = String.format(("select t1.`employee_id`, t1.`full_name`, t2.`employee_id`, t2.`full_name` " + "from cp.`employee.json` t1 inner join dfs.tmp.`%s` t2 on t1.`full_name` = t2.`full_name`"), TestJoinEmptyDirTable.EMPTY_DIRECTORY);
            final int expectedRecordCount = 0;
            JoinTestBase.enableJoin(false, true, false);
            PlanTestBase.testPlanMatchingPatterns(query, new String[]{ JoinTestBase.MJ_PATTERN, JoinTestBase.INNER_JOIN_TYPE }, new String[]{  });
            final int actualRecordCount = BaseTestQuery.testSql(query);
            Assert.assertEquals("Number of output rows", expectedRecordCount, actualRecordCount);
        } finally {
            JoinTestBase.resetJoinOptions();
        }
    }

    @Test
    public void testMergeInnerJoinWithBothEmptyDirTables() throws Exception {
        try {
            String query = String.format(("select t1.`employee_id`, t1.`full_name`, t2.`employee_id`, t2.`full_name` " + "from dfs.tmp.`%1$s` t1 inner join dfs.tmp.`%1$s` t2 on t1.`full_name` = t2.`full_name`"), TestJoinEmptyDirTable.EMPTY_DIRECTORY);
            final int expectedRecordCount = 0;
            JoinTestBase.enableJoin(false, true, false);
            PlanTestBase.testPlanMatchingPatterns(query, new String[]{ JoinTestBase.MJ_PATTERN, JoinTestBase.INNER_JOIN_TYPE }, new String[]{  });
            final int actualRecordCount = BaseTestQuery.testSql(query);
            Assert.assertEquals("Number of output rows", expectedRecordCount, actualRecordCount);
        } finally {
            JoinTestBase.resetJoinOptions();
        }
    }

    @Test
    public void testMergeLeftJoinWithRightEmptyDirTable() throws Exception {
        try {
            String query = String.format(("select t1.`employee_id`, t1.`full_name`, t2.`employee_id`, t2.`full_name` " + "from cp.`employee.json` t1 left join dfs.tmp.`%s` t2 on t1.`full_name` = t2.`full_name`"), TestJoinEmptyDirTable.EMPTY_DIRECTORY);
            final int expectedRecordCount = 1155;
            JoinTestBase.enableJoin(false, true, false);
            PlanTestBase.testPlanMatchingPatterns(query, new String[]{ JoinTestBase.MJ_PATTERN, JoinTestBase.LEFT_JOIN_TYPE }, new String[]{  });
            final int actualRecordCount = BaseTestQuery.testSql(query);
            Assert.assertEquals("Number of output rows", expectedRecordCount, actualRecordCount);
        } finally {
            JoinTestBase.resetJoinOptions();
        }
    }

    @Test
    public void testMergeRightJoinWithRightEmptyDirTable() throws Exception {
        try {
            String query = String.format(("select t1.`employee_id`, t1.`full_name`, t2.`employee_id`, t2.`full_name` " + "from cp.`employee.json` t1 right join dfs.tmp.`%s` t2 on t1.`full_name` = t2.`full_name`"), TestJoinEmptyDirTable.EMPTY_DIRECTORY);
            final int expectedRecordCount = 0;
            JoinTestBase.enableJoin(false, true, false);
            PlanTestBase.testPlanMatchingPatterns(query, new String[]{ JoinTestBase.MJ_PATTERN, JoinTestBase.RIGHT_JOIN_TYPE }, new String[]{  });
            final int actualRecordCount = BaseTestQuery.testSql(query);
            Assert.assertEquals("Number of output rows", expectedRecordCount, actualRecordCount);
        } finally {
            JoinTestBase.resetJoinOptions();
        }
    }

    @Test
    public void testNestedLoopInnerJoinWithLeftEmptyDirTable() throws Exception {
        try {
            String query = String.format(("select t1.`employee_id`, t1.`full_name`, t2.`employee_id`, t2.`full_name` " + "from dfs.tmp.`%s` t1 inner join cp.`employee.json` t2 on t1.`full_name` = t2.`full_name`"), TestJoinEmptyDirTable.EMPTY_DIRECTORY);
            final int expectedRecordCount = 0;
            JoinTestBase.enableJoin(false, false, true);
            PlanTestBase.testPlanMatchingPatterns(query, new String[]{ JoinTestBase.NLJ_PATTERN, JoinTestBase.INNER_JOIN_TYPE }, new String[]{  });
            final int actualRecordCount = BaseTestQuery.testSql(query);
            Assert.assertEquals("Number of output rows", expectedRecordCount, actualRecordCount);
        } finally {
            JoinTestBase.resetJoinOptions();
        }
    }

    @Test
    public void testNestedLoopInnerJoinWithRightEmptyDirTable() throws Exception {
        try {
            String query = String.format(("select t1.`employee_id`, t1.`full_name`, t2.`employee_id`, t2.`full_name` " + "from cp.`employee.json` t1 inner join dfs.tmp.`%s` t2 on t1.`full_name` = t2.`full_name`"), TestJoinEmptyDirTable.EMPTY_DIRECTORY);
            final int expectedRecordCount = 0;
            JoinTestBase.enableJoin(false, false, true);
            PlanTestBase.testPlanMatchingPatterns(query, new String[]{ JoinTestBase.NLJ_PATTERN, JoinTestBase.INNER_JOIN_TYPE }, new String[]{  });
            final int actualRecordCount = BaseTestQuery.testSql(query);
            Assert.assertEquals("Number of output rows", expectedRecordCount, actualRecordCount);
        } finally {
            JoinTestBase.resetJoinOptions();
        }
    }

    @Test
    public void testNestedLoopInnerJoinWithBothEmptyDirTables() throws Exception {
        try {
            String query = String.format(("select t1.`employee_id`, t1.`full_name`, t2.`employee_id`, t2.`full_name` " + "from dfs.tmp.`%1$s` t1 inner join dfs.tmp.`%1$s` t2 on t1.`full_name` = t2.`full_name`"), TestJoinEmptyDirTable.EMPTY_DIRECTORY);
            final int expectedRecordCount = 0;
            JoinTestBase.enableJoin(false, false, true);
            PlanTestBase.testPlanMatchingPatterns(query, new String[]{ JoinTestBase.NLJ_PATTERN, JoinTestBase.INNER_JOIN_TYPE }, new String[]{  });
            final int actualRecordCount = BaseTestQuery.testSql(query);
            Assert.assertEquals("Number of output rows", expectedRecordCount, actualRecordCount);
        } finally {
            JoinTestBase.resetJoinOptions();
        }
    }

    @Test
    public void testNestedLoopLeftJoinWithLeftEmptyDirTable() throws Exception {
        try {
            String query = String.format(("select t1.`employee_id`, t1.`full_name`, t2.`employee_id`, t2.`full_name` " + "from dfs.tmp.`%s` t1 left join cp.`employee.json` t2 on t1.`full_name` = t2.`full_name`"), TestJoinEmptyDirTable.EMPTY_DIRECTORY);
            final int expectedRecordCount = 0;
            JoinTestBase.enableJoin(false, false, true);
            // See details in description for PlannerSettings.JOIN_OPTIMIZATION
            BaseTestQuery.setSessionOption(JOIN_OPTIMIZATION.getOptionName(), false);
            PlanTestBase.testPlanMatchingPatterns(query, new String[]{ JoinTestBase.NLJ_PATTERN, JoinTestBase.LEFT_JOIN_TYPE }, new String[]{  });
            final int actualRecordCount = BaseTestQuery.testSql(query);
            Assert.assertEquals("Number of output rows", expectedRecordCount, actualRecordCount);
        } finally {
            JoinTestBase.resetJoinOptions();
            BaseTestQuery.resetSessionOption(JOIN_OPTIMIZATION.getOptionName());
        }
    }

    @Test
    public void testNestedLoopLeftJoinWithRightEmptyDirTable() throws Exception {
        try {
            String query = String.format(("select t1.`employee_id`, t1.`full_name`, t2.`employee_id`, t2.`full_name` " + "from cp.`employee.json` t1 left join dfs.tmp.`%s` t2 on t1.`full_name` = t2.`full_name`"), TestJoinEmptyDirTable.EMPTY_DIRECTORY);
            final int expectedRecordCount = 1155;
            JoinTestBase.enableJoin(false, false, true);
            PlanTestBase.testPlanMatchingPatterns(query, new String[]{ JoinTestBase.NLJ_PATTERN, JoinTestBase.LEFT_JOIN_TYPE }, new String[]{  });
            final int actualRecordCount = BaseTestQuery.testSql(query);
            Assert.assertEquals("Number of output rows", expectedRecordCount, actualRecordCount);
        } finally {
            JoinTestBase.resetJoinOptions();
        }
    }

    @Test
    public void testNestedLoopRightJoinWithLeftEmptyDirTable() throws Exception {
        try {
            String query = String.format(("select t1.`employee_id`, t1.`full_name`, t2.`employee_id`, t2.`full_name` " + "from dfs.tmp.`%s` t1 right join cp.`employee.json` t2 on t1.`full_name` = t2.`full_name`"), TestJoinEmptyDirTable.EMPTY_DIRECTORY);
            final int expectedRecordCount = 1155;
            JoinTestBase.enableJoin(false, false, true);
            // The left output is less than right one. Therefore during optimization phase the RIGHT JOIN is converted to LEFT JOIN
            PlanTestBase.testPlanMatchingPatterns(query, new String[]{ JoinTestBase.NLJ_PATTERN, JoinTestBase.LEFT_JOIN_TYPE }, new String[]{  });
            final int actualRecordCount = BaseTestQuery.testSql(query);
            Assert.assertEquals("Number of output rows", expectedRecordCount, actualRecordCount);
        } finally {
            JoinTestBase.resetJoinOptions();
        }
    }

    @Test(expected = UserRemoteException.class)
    public void testNestedLoopRightJoinWithRightEmptyDirTable() throws Exception {
        try {
            String query = String.format(("select t1.`employee_id`, t1.`full_name`, t2.`employee_id`, t2.`full_name` " + "from cp.`employee.json` t1 right join dfs.tmp.`%s` t2 on t1.`full_name` = t2.`full_name`"), TestJoinEmptyDirTable.EMPTY_DIRECTORY);
            JoinTestBase.enableJoin(false, false, true);
            // The nested loops join does not support the "RIGHT OUTER JOIN" logical join operator.
            BaseTestQuery.test(query);
        } catch (UserRemoteException e) {
            Assert.assertTrue(("Not expected exception is obtained while performing the query with RIGHT JOIN logical operator " + "by using nested loop join physical operator"), e.getMessage().contains("SYSTEM ERROR: CannotPlanException"));
            throw e;
        } finally {
            JoinTestBase.resetJoinOptions();
        }
    }
}

