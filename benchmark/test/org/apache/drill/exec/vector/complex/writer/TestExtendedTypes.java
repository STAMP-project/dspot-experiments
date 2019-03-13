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
package org.apache.drill.exec.vector.complex.writer;


import ExecConstants.JSON_EXTENDED_TYPES;
import ExecConstants.OUTPUT_FORMAT_VALIDATOR;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import org.apache.drill.exec.ExecTest;
import org.apache.drill.exec.rpc.user.QueryDataBatch;
import org.apache.drill.test.BaseTestQuery;
import org.junit.Assert;
import org.junit.Test;


public class TestExtendedTypes extends BaseTestQuery {
    @Test
    public void checkReadWriteExtended() throws Exception {
        ExecTest.mockUtcDateTimeZone();
        final String originalFile = "vector/complex/extended.json";
        final String newTable = "TestExtendedTypes/newjson";
        try {
            BaseTestQuery.testNoResult(String.format("ALTER SESSION SET `%s` = 'json'", OUTPUT_FORMAT_VALIDATOR.getOptionName()));
            BaseTestQuery.testNoResult(String.format("ALTER SESSION SET `%s` = true", JSON_EXTENDED_TYPES.getOptionName()));
            // create table
            BaseTestQuery.test("create table dfs.tmp.`%s` as select * from cp.`%s`", newTable, originalFile);
            // check query of table.
            BaseTestQuery.test("select * from dfs.tmp.`%s`", newTable);
            // check that original file and new file match.
            final byte[] originalData = Files.readAllBytes(ExecTest.dirTestWatcher.getRootDir().toPath().resolve(originalFile));
            final byte[] newData = Files.readAllBytes(ExecTest.dirTestWatcher.getDfsTestTmpDir().toPath().resolve(Paths.get(newTable, "0_0_0.json")));
            Assert.assertEquals(new String(originalData), new String(newData));
        } finally {
            BaseTestQuery.resetSessionOption(OUTPUT_FORMAT_VALIDATOR.getOptionName());
            BaseTestQuery.resetSessionOption(JSON_EXTENDED_TYPES.getOptionName());
        }
    }

    @Test
    public void testMongoExtendedTypes() throws Exception {
        final String originalFile = "vector/complex/mongo_extended.json";
        try {
            BaseTestQuery.testNoResult(String.format("ALTER SESSION SET `%s` = 'json'", OUTPUT_FORMAT_VALIDATOR.getOptionName()));
            BaseTestQuery.testNoResult(String.format("ALTER SESSION SET `%s` = true", JSON_EXTENDED_TYPES.getOptionName()));
            int actualRecordCount = BaseTestQuery.testSql(String.format("select * from cp.`%s`", originalFile));
            Assert.assertEquals(String.format("Received unexpected number of rows in output: expected=%d, received=%s", 1, actualRecordCount), 1, actualRecordCount);
            List<QueryDataBatch> resultList = BaseTestQuery.testSqlWithResults(String.format("select * from dfs.`%s`", originalFile));
            String actual = BaseTestQuery.getResultString(resultList, ",");
            String expected = "drill_timestamp_millies,bin,bin1\n2015-07-07 03:59:43.488,drill,drill\n";
            Assert.assertEquals(expected, actual);
        } finally {
            BaseTestQuery.resetSessionOption(OUTPUT_FORMAT_VALIDATOR.getOptionName());
            BaseTestQuery.resetSessionOption(JSON_EXTENDED_TYPES.getOptionName());
        }
    }
}

