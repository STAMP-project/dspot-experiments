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
package org.apache.hadoop.hbase.master.procedure;


import java.util.concurrent.TimeUnit;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.TableDescriptor;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.testclassification.MasterTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ MasterTests.class, LargeTests.class })
public class TestCreateTableProcedureMuitipleRegions {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestCreateTableProcedureMuitipleRegions.class);

    private static final HBaseTestingUtility UTIL = new HBaseTestingUtility();

    private static final String F1 = "f1";

    private static final String F2 = "f2";

    @Test
    public void testMRegions() throws Exception {
        byte[][] splitKeys = new byte[500][];
        for (int i = 0; i < (splitKeys.length); ++i) {
            splitKeys[i] = Bytes.toBytes(String.format("%08d", i));
        }
        TableDescriptor htd = MasterProcedureTestingUtility.createHTD(TableName.valueOf("TestMRegions"), TestCreateTableProcedureMuitipleRegions.F1, TestCreateTableProcedureMuitipleRegions.F2);
        TestCreateTableProcedureMuitipleRegions.UTIL.getAdmin().createTableAsync(htd, splitKeys).get(10, TimeUnit.HOURS);
    }
}

