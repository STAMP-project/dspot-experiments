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


import StoragePluginTestUtils.DFS_PLUGIN_NAME;
import org.apache.drill.PlanTestBase;
import org.junit.Test;


public class TestParquetFilterPushDownForComplexTypes extends PlanTestBase {
    private static final String TABLE_PATH = "parquet/users";

    private static final String TABLE_NAME = String.format("%s.`%s`", DFS_PLUGIN_NAME, TestParquetFilterPushDownForComplexTypes.TABLE_PATH);

    @Test
    public void testPushDownArray() throws Exception {
        testParquetFilterPushDown("t.`user`.hobby_ids[0] = 1", 3, 2);
        testParquetFilterPushDown("t.`user`.hobby_ids[0] = 100", 0, 1);
        testParquetFilterPushDown("t.`user`.hobby_ids[0] <> 1", 8, 6);
        testParquetFilterPushDown("t.`user`.hobby_ids[2] > 20", 5, 3);
        testParquetFilterPushDown("t.`user`.hobby_ids[0] between 10 and 20", 5, 4);
        testParquetFilterPushDown("t.`user`.hobby_ids[4] = 15", 1, 3);
        testParquetFilterPushDown("t.`user`.hobby_ids[2] is not null", 11, 6);
        testParquetFilterPushDown("t.`user`.hobby_ids[3] is null", 11, 7);
    }

    @Test
    public void testPushDownComplexIntColumn() throws Exception {
        testParquetFilterPushDown("t.`user`.age = 31", 1, 2);
        testParquetFilterPushDown("t.`user`.age = 1", 0, 1);
        testParquetFilterPushDown("t.`user`.age <> 20", 10, 6);
        testParquetFilterPushDown("t.`user`.age > 30", 5, 4);
        testParquetFilterPushDown("t.`user`.age between 20 and 30", 6, 3);
        testParquetFilterPushDown("t.`user`.age is not null", 11, 6);
        testParquetFilterPushDown("t.`user`.age is null", 2, 2);
    }

    @Test
    public void testPushDownComplexBooleanColumn() throws Exception {
        testParquetFilterPushDown("t.`user`.active is true", 5, 4);
        testParquetFilterPushDown("t.`user`.active is not true", 8, 6);
        testParquetFilterPushDown("t.`user`.active is false", 4, 4);
        testParquetFilterPushDown("t.`user`.active is not false", 9, 6);
        testParquetFilterPushDown("t.`user`.active is not null", 9, 6);
        testParquetFilterPushDown("t.`user`.active is null", 4, 4);
    }
}

