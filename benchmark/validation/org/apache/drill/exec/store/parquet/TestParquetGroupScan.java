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
package org.apache.drill.exec.store.parquet;


import org.apache.drill.test.BaseTestQuery;
import org.junit.Assert;
import org.junit.Test;


public class TestParquetGroupScan extends BaseTestQuery {
    @Test
    public void testFix4376() throws Exception {
        prepareTables("4376_1", true);
        int actualRecordCount = BaseTestQuery.testSql("SELECT * FROM dfs.tmp.`4376_1/60*`");
        int expectedRecordCount = 1984;
        Assert.assertEquals(String.format("Received unexpected number of rows in output: expected = %d, received = %s", expectedRecordCount, actualRecordCount), expectedRecordCount, actualRecordCount);
    }

    @Test
    public void testWildCardEmptyWithCache() throws Exception {
        prepareTables("4376_2", true);
        int actualRecordCount = BaseTestQuery.testSql("SELECT * FROM dfs.tmp.`4376_2/604*`");
        int expectedRecordCount = 0;
        Assert.assertEquals(String.format("Received unexpected number of rows in output: expected = %d, received = %s", expectedRecordCount, actualRecordCount), expectedRecordCount, actualRecordCount);
    }

    @Test
    public void testWildCardEmptyNoCache() throws Exception {
        prepareTables("4376_3", false);
        int actualRecordCount = BaseTestQuery.testSql("SELECT * FROM dfs.tmp.`4376_3/604*`");
        int expectedRecordCount = 0;
        Assert.assertEquals(String.format("Received unexpected number of rows in output: expected = %d, received = %s", expectedRecordCount, actualRecordCount), expectedRecordCount, actualRecordCount);
    }

    @Test
    public void testSelectEmptyWithCache() throws Exception {
        prepareTables("4376_4", true);
        int actualRecordCount = BaseTestQuery.testSql("SELECT * FROM dfs.tmp.`4376_4/6041`");
        int expectedRecordCount = 0;
        Assert.assertEquals(String.format("Received unexpected number of rows in output: expected = %d, received = %s", expectedRecordCount, actualRecordCount), expectedRecordCount, actualRecordCount);
    }

    @Test
    public void testSelectEmptyNoCache() throws Exception {
        prepareTables("4376_5", false);
        int actualRecordCount = BaseTestQuery.testSql("SELECT * FROM dfs.tmp.`4376_5/6041`");
        int expectedRecordCount = 0;
        Assert.assertEquals(String.format("Received unexpected number of rows in output: expected = %d, received = %s", expectedRecordCount, actualRecordCount), expectedRecordCount, actualRecordCount);
    }
}

