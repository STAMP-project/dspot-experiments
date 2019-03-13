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
package org.apache.drill.exec.planner.sql;


import PlannerSettings.QUOTING_IDENTIFIERS_KEY;
import Quoting.DOUBLE_QUOTE.string;
import org.apache.calcite.sql.parser.SqlParserPos;
import org.apache.drill.categories.SqlTest;
import org.apache.drill.test.BaseTestQuery;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category(SqlTest.class)
public class TestDrillSQLWorker extends BaseTestQuery {
    @Test
    public void testErrorFormating() {
        String sql = "Select * from Foo\nwhere tadadidada;\n";
        validateFormattedIs(sql, new SqlParserPos(1, 2), ("Select * from Foo\n" + (" ^\n" + "where tadadidada;\n")));
        validateFormattedIs(sql, new SqlParserPos(2, 2), ("Select * from Foo\n" + ("where tadadidada;\n" + " ^\n")));
        validateFormattedIs(sql, new SqlParserPos(1, 10), ("Select * from Foo\n" + ("         ^\n" + "where tadadidada;\n")));
        validateFormattedIs(sql, new SqlParserPos((-11), (-10)), sql);
        validateFormattedIs(sql, new SqlParserPos(0, 10), sql);
        validateFormattedIs(sql, new SqlParserPos(100, 10), sql);
    }

    @Test
    public void testDoubleQuotesForQuotingIdentifiers() throws Exception {
        try {
            BaseTestQuery.test("ALTER SESSION SET `%s` = '%s'", QUOTING_IDENTIFIERS_KEY, string);
            BaseTestQuery.testBuilder().sqlQuery("select \"employee_id\", \"full_name\" from cp.\"employee.json\" limit 1").ordered().baselineColumns("employee_id", "full_name").baselineValues(1L, "Sheri Nowmer").go();
            // Other quoting characters are not acceptable while particular one is chosen,
            // since calcite doesn't support parsing sql statements with several quoting identifiers characters
            BaseTestQuery.errorMsgTestHelper("select `employee_id`, `full_name` from cp.`employee.json` limit 1", "Encountered: \"`\"");
            // Mix of different quotes in the one SQL statement is not acceptable
            BaseTestQuery.errorMsgTestHelper("select \"employee_id\", \"full_name\" from cp.`employee.json` limit 1", "Encountered: \"`\"");
        } finally {
            BaseTestQuery.test("ALTER SESSION RESET %s", QUOTING_IDENTIFIERS_KEY);
        }
    }

    @Test
    public void testBracketsForQuotingIdentifiers() throws Exception {
        try {
            BaseTestQuery.test("ALTER SESSION SET `%s` = '%s'", QUOTING_IDENTIFIERS_KEY, Quoting.BRACKET.string);
            BaseTestQuery.testBuilder().sqlQuery("select [employee_id], [full_name] from cp.[employee.json] limit 1").ordered().baselineColumns("employee_id", "full_name").baselineValues(1L, "Sheri Nowmer").go();
        } finally {
            BaseTestQuery.test("ALTER SESSION RESET %s", QUOTING_IDENTIFIERS_KEY);
        }
    }
}

