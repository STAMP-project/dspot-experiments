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
package org.apache.hadoop.hbase.client;


import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.regionserver.InvalidMutationDurabilityException;
import org.apache.hadoop.hbase.testclassification.ClientTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ MediumTests.class, ClientTests.class })
public class TestInvalidMutationDurabilityException {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestInvalidMutationDurabilityException.class);

    private static final HBaseTestingUtility UTIL = new HBaseTestingUtility();

    private static TableName TABLE_NOT_REPLICATE = TableName.valueOf("TableNotReplicate");

    private static TableName TABLE_NEED_REPLICATE = TableName.valueOf("TableNeedReplicate");

    private static byte[] CF = Bytes.toBytes("cf");

    private static byte[] CQ = Bytes.toBytes("cq");

    private static Table tableNotReplicate;

    private static Table tableNeedReplicate;

    @Test
    public void testPutToTableNotReplicate() throws Exception {
        TestInvalidMutationDurabilityException.tableNotReplicate.put(newPutWithSkipWAL());
    }

    @Test(expected = InvalidMutationDurabilityException.class)
    public void testPutToTableNeedReplicate() throws Exception {
        TestInvalidMutationDurabilityException.tableNeedReplicate.put(newPutWithSkipWAL());
    }

    @Test
    public void testDeleteToTableNotReplicate() throws Exception {
        TestInvalidMutationDurabilityException.tableNotReplicate.delete(newDeleteWithSkipWAL());
    }

    @Test(expected = InvalidMutationDurabilityException.class)
    public void testDeleteToTableNeedReplicate() throws Exception {
        TestInvalidMutationDurabilityException.tableNeedReplicate.delete(newDeleteWithSkipWAL());
    }

    @Test
    public void testAppendToTableNotReplicate() throws Exception {
        TestInvalidMutationDurabilityException.tableNotReplicate.append(newAppendWithSkipWAL());
    }

    @Test(expected = InvalidMutationDurabilityException.class)
    public void testAppendToTableNeedReplicate() throws Exception {
        TestInvalidMutationDurabilityException.tableNeedReplicate.append(newAppendWithSkipWAL());
    }

    @Test
    public void testIncrementToTableNotReplicate() throws Exception {
        TestInvalidMutationDurabilityException.tableNotReplicate.increment(newIncrementWithSkipWAL());
    }

    @Test(expected = InvalidMutationDurabilityException.class)
    public void testIncrementToTableNeedReplicate() throws Exception {
        TestInvalidMutationDurabilityException.tableNeedReplicate.increment(newIncrementWithSkipWAL());
    }

    @Test
    public void testCheckWithMutateToTableNotReplicate() throws Exception {
        TestInvalidMutationDurabilityException.tableNotReplicate.checkAndMutate(Bytes.toBytes("row"), TestInvalidMutationDurabilityException.CF).qualifier(TestInvalidMutationDurabilityException.CQ).ifNotExists().thenPut(newPutWithSkipWAL());
    }

    @Test(expected = InvalidMutationDurabilityException.class)
    public void testCheckWithMutateToTableNeedReplicate() throws Exception {
        TestInvalidMutationDurabilityException.tableNeedReplicate.checkAndMutate(Bytes.toBytes("row"), TestInvalidMutationDurabilityException.CF).qualifier(TestInvalidMutationDurabilityException.CQ).ifNotExists().thenPut(newPutWithSkipWAL());
    }
}

