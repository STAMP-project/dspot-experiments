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
import org.apache.hadoop.hbase.regionserver.NoSuchColumnFamilyException;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;


@Category(MediumTests.class)
public class TestCheckAndMutate {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestCheckAndMutate.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static final byte[] ROWKEY = Bytes.toBytes("12345");

    private static final byte[] FAMILY = Bytes.toBytes("cf");

    @Rule
    public TestName name = new TestName();

    @Test
    public void testCheckAndMutate() throws Throwable {
        try (Table table = createTable()) {
            // put one row
            putOneRow(table);
            // get row back and assert the values
            getOneRowAndAssertAllExist(table);
            // put the same row again with C column deleted
            RowMutations rm = makeRowMutationsWithColumnCDeleted();
            boolean res = table.checkAndMutate(TestCheckAndMutate.ROWKEY, TestCheckAndMutate.FAMILY).qualifier(Bytes.toBytes("A")).ifEquals(Bytes.toBytes("a")).thenMutate(rm);
            Assert.assertTrue(res);
            // get row back and assert the values
            getOneRowAndAssertAllButCExist(table);
            // Test that we get a region level exception
            try {
                rm = getBogusRowMutations();
                table.checkAndMutate(TestCheckAndMutate.ROWKEY, TestCheckAndMutate.FAMILY).qualifier(Bytes.toBytes("A")).ifEquals(Bytes.toBytes("a")).thenMutate(rm);
                Assert.fail("Expected NoSuchColumnFamilyException");
            } catch (RetriesExhaustedWithDetailsException e) {
                try {
                    throw e.getCause(0);
                } catch (NoSuchColumnFamilyException e1) {
                    // expected
                }
            }
        }
    }

    @Test
    public void testCheckAndMutateWithBuilder() throws Throwable {
        try (Table table = createTable()) {
            // put one row
            putOneRow(table);
            // get row back and assert the values
            getOneRowAndAssertAllExist(table);
            // put the same row again with C column deleted
            RowMutations rm = makeRowMutationsWithColumnCDeleted();
            boolean res = table.checkAndMutate(TestCheckAndMutate.ROWKEY, TestCheckAndMutate.FAMILY).qualifier(Bytes.toBytes("A")).ifEquals(Bytes.toBytes("a")).thenMutate(rm);
            Assert.assertTrue(res);
            // get row back and assert the values
            getOneRowAndAssertAllButCExist(table);
            // Test that we get a region level exception
            try {
                rm = getBogusRowMutations();
                table.checkAndMutate(TestCheckAndMutate.ROWKEY, TestCheckAndMutate.FAMILY).qualifier(Bytes.toBytes("A")).ifEquals(Bytes.toBytes("a")).thenMutate(rm);
                Assert.fail("Expected NoSuchColumnFamilyException");
            } catch (RetriesExhaustedWithDetailsException e) {
                try {
                    throw e.getCause(0);
                } catch (NoSuchColumnFamilyException e1) {
                    // expected
                }
            }
        }
    }
}

