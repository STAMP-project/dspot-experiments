/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.table.runtime.batch.sql;


import org.apache.flink.table.api.java.BatchTableEnvironment;
import org.apache.flink.table.runtime.utils.TableProgramsClusterTestBase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * This test should be replaced by a DataSetAggregateITCase.
 * We should only perform logical unit tests here.
 * Until then, we perform a cluster test here.
 */
@RunWith(Parameterized.class)
public class GroupingSetsITCase extends TableProgramsClusterTestBase {
    private static final String TABLE_NAME = "MyTable";

    private static final String TABLE_WITH_NULLS_NAME = "MyTableWithNulls";

    private BatchTableEnvironment tableEnv;

    public GroupingSetsITCase(TestExecutionMode mode, TableConfigMode tableConfigMode) {
        super(mode, tableConfigMode);
    }

    @Test
    public void testGroupingSets() throws Exception {
        String query = (("SELECT f1, f2, avg(f0) as a, GROUP_ID() as g, " + ((((" GROUPING(f1) as gf1, GROUPING(f2) as gf2, " + " GROUPING_ID(f1) as gif1, GROUPING_ID(f2) as gif2, ") + " GROUPING_ID(f1, f2) as gid, ") + " COUNT(*) as cnt") + " FROM ")) + (GroupingSetsITCase.TABLE_NAME)) + " GROUP BY GROUPING SETS (f1, f2, ())";
        String expected = "1,null,1,1,1,0,1,0,2,1\n" + (((((((((((((((((((((((((("6,null,18,1,1,0,1,0,2,6\n" + "2,null,2,1,1,0,1,0,2,2\n") + "4,null,8,1,1,0,1,0,2,4\n") + "5,null,13,1,1,0,1,0,2,5\n") + "3,null,5,1,1,0,1,0,2,3\n") + "null,Comment#11,17,2,0,1,0,1,1,1\n") + "null,Comment#8,14,2,0,1,0,1,1,1\n") + "null,Comment#2,8,2,0,1,0,1,1,1\n") + "null,Comment#1,7,2,0,1,0,1,1,1\n") + "null,Comment#14,20,2,0,1,0,1,1,1\n") + "null,Comment#7,13,2,0,1,0,1,1,1\n") + "null,Comment#6,12,2,0,1,0,1,1,1\n") + "null,Comment#3,9,2,0,1,0,1,1,1\n") + "null,Comment#12,18,2,0,1,0,1,1,1\n") + "null,Comment#5,11,2,0,1,0,1,1,1\n") + "null,Comment#15,21,2,0,1,0,1,1,1\n") + "null,Comment#4,10,2,0,1,0,1,1,1\n") + "null,Hi,1,2,0,1,0,1,1,1\n") + "null,Comment#10,16,2,0,1,0,1,1,1\n") + "null,Hello world,3,2,0,1,0,1,1,1\n") + "null,I am fine.,5,2,0,1,0,1,1,1\n") + "null,Hello world, how are you?,4,2,0,1,0,1,1,1\n") + "null,Comment#9,15,2,0,1,0,1,1,1\n") + "null,Comment#13,19,2,0,1,0,1,1,1\n") + "null,Luke Skywalker,6,2,0,1,0,1,1,1\n") + "null,Hello,2,2,0,1,0,1,1,1\n") + "null,null,11,0,0,0,0,0,0,21");
        checkSql(query, expected);
    }

    @Test
    public void testGroupingSetsWithNulls() throws Exception {
        String query = ("SELECT f1, f2, avg(f0) as a, GROUP_ID() as g FROM " + (GroupingSetsITCase.TABLE_WITH_NULLS_NAME)) + " GROUP BY GROUPING SETS (f1, f2)";
        String expected = "6,null,18,1\n5,null,13,1\n4,null,8,1\n3,null,5,1\n2,null,2,1\n1,null,1,1\n" + (((((("null,Luke Skywalker,6,2\nnull,I am fine.,5,2\nnull,Hi,1,2\n" + "null,null,3,2\nnull,Hello,2,2\nnull,Comment#9,15,2\nnull,Comment#8,14,2\n") + "null,Comment#7,13,2\nnull,Comment#6,12,2\nnull,Comment#5,11,2\n") + "null,Comment#4,10,2\nnull,Comment#3,9,2\nnull,Comment#2,8,2\n") + "null,Comment#15,21,2\nnull,Comment#14,20,2\nnull,Comment#13,19,2\n") + "null,Comment#12,18,2\nnull,Comment#11,17,2\nnull,Comment#10,16,2\n") + "null,Comment#1,7,2");
        checkSql(query, expected);
    }

    @Test
    public void testCubeAsGroupingSets() throws Exception {
        String cubeQuery = (("SELECT f1, f2, avg(f0) as a, GROUP_ID() as g, " + (((" GROUPING(f1) as gf1, GROUPING(f2) as gf2, " + " GROUPING_ID(f1) as gif1, GROUPING_ID(f2) as gif2, ") + " GROUPING_ID(f1, f2) as gid ") + " FROM ")) + (GroupingSetsITCase.TABLE_NAME)) + " GROUP BY CUBE (f1, f2)";
        String groupingSetsQuery = (("SELECT f1, f2, avg(f0) as a, GROUP_ID() as g, " + (((" GROUPING(f1) as gf1, GROUPING(f2) as gf2, " + " GROUPING_ID(f1) as gif1, GROUPING_ID(f2) as gif2, ") + " GROUPING_ID(f1, f2) as gid ") + " FROM ")) + (GroupingSetsITCase.TABLE_NAME)) + " GROUP BY GROUPING SETS ((f1, f2), (f1), (f2), ())";
        compareSql(cubeQuery, groupingSetsQuery);
    }

    @Test
    public void testRollupAsGroupingSets() throws Exception {
        String rollupQuery = (("SELECT f1, f2, avg(f0) as a, GROUP_ID() as g, " + (((" GROUPING(f1) as gf1, GROUPING(f2) as gf2, " + " GROUPING_ID(f1) as gif1, GROUPING_ID(f2) as gif2, ") + " GROUPING_ID(f1, f2) as gid ") + " FROM ")) + (GroupingSetsITCase.TABLE_NAME)) + " GROUP BY ROLLUP (f1, f2)";
        String groupingSetsQuery = (("SELECT f1, f2, avg(f0) as a, GROUP_ID() as g, " + (((" GROUPING(f1) as gf1, GROUPING(f2) as gf2, " + " GROUPING_ID(f1) as gif1, GROUPING_ID(f2) as gif2, ") + " GROUPING_ID(f1, f2) as gid ") + " FROM ")) + (GroupingSetsITCase.TABLE_NAME)) + " GROUP BY GROUPING SETS ((f1, f2), (f1), ())";
        compareSql(rollupQuery, groupingSetsQuery);
    }
}

