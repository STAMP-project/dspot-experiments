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
package org.apache.hadoop.hbase.procedure;


import java.io.IOException;
import java.util.HashMap;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.testclassification.MasterTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ MasterTests.class, MediumTests.class })
public class TestProcedureManager {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestProcedureManager.class);

    private static final int NUM_RS = 2;

    private static HBaseTestingUtility util = new HBaseTestingUtility();

    @Test
    public void testSimpleProcedureManager() throws IOException {
        Admin admin = TestProcedureManager.util.getAdmin();
        byte[] result = admin.execProcedureWithRet(SimpleMasterProcedureManager.SIMPLE_SIGNATURE, "mytest", new HashMap());
        Assert.assertArrayEquals("Incorrect return data from execProcedure", Bytes.toBytes(SimpleMasterProcedureManager.SIMPLE_DATA), result);
    }
}

