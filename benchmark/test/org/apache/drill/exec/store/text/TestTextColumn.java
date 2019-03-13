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
package org.apache.drill.exec.store.text;


import java.util.List;
import org.apache.drill.exec.rpc.user.QueryDataBatch;
import org.apache.drill.test.BaseTestQuery;
import org.junit.Assert;
import org.junit.Test;


public class TestTextColumn extends BaseTestQuery {
    @Test
    public void testCsvColumnSelection() throws Exception {
        BaseTestQuery.test("select columns[0] as region_id, columns[1] as country from cp.`store/text/data/regions.csv`");
    }

    @Test
    public void testDefaultDelimiterColumnSelection() throws Exception {
        List<QueryDataBatch> actualResults = BaseTestQuery.testSqlWithResults(("SELECT columns[0] as entire_row " + "from cp.`store/text/data/letters.txt`"));
        final BaseTestQuery.TestResultSet expectedResultSet = new BaseTestQuery.TestResultSet();
        expectedResultSet.addRow("a, b,\",\"c\",\"d,, \\n e");
        expectedResultSet.addRow("d, e,\",\"f\",\"g,, \\n h");
        expectedResultSet.addRow("g, h,\",\"i\",\"j,, \\n k");
        BaseTestQuery.TestResultSet actualResultSet = new BaseTestQuery.TestResultSet(actualResults);
        Assert.assertEquals(expectedResultSet, actualResultSet);
    }

    @Test
    public void testCsvColumnSelectionCommasInsideQuotes() throws Exception {
        List<QueryDataBatch> actualResults = BaseTestQuery.testSqlWithResults(("SELECT columns[0] as col1, columns[1] as col2, columns[2] as col3," + "columns[3] as col4 from cp.`store/text/data/letters.csv`"));
        final BaseTestQuery.TestResultSet expectedResultSet = new BaseTestQuery.TestResultSet();
        expectedResultSet.addRow("a, b,", "c", "d,, \\n e", "f\\\"g");
        expectedResultSet.addRow("d, e,", "f", "g,, \\n h", "i\\\"j");
        expectedResultSet.addRow("g, h,", "i", "j,, \\n k", "l\\\"m");
        BaseTestQuery.TestResultSet actualResultSet = new BaseTestQuery.TestResultSet(actualResults);
        Assert.assertEquals(expectedResultSet, actualResultSet);
    }

    @Test
    public void testColumnsCaseInsensitive() throws Exception {
        BaseTestQuery.testBuilder().sqlQuery("select columns as c from cp.`store/text/data/letters.csv`").unOrdered().sqlBaselineQuery("select COLUMNS as c from cp.`store/text/data/letters.csv`").go();
        BaseTestQuery.testBuilder().sqlQuery("select columns[0], columns[1] from cp.`store/text/data/letters.csv`").unOrdered().sqlBaselineQuery("select COLUMNS[0], CoLuMnS[1] from cp.`store/text/data/letters.csv`").go();
    }
}

