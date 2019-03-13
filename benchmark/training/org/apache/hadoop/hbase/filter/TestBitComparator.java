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
package org.apache.hadoop.hbase.filter;


import BitComparator.BitwiseOp.AND;
import BitComparator.BitwiseOp.OR;
import BitComparator.BitwiseOp.XOR;
import java.nio.ByteBuffer;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.testclassification.FilterTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Tests for the bit comparator
 */
@Category({ FilterTests.class, SmallTests.class })
public class TestBitComparator {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestBitComparator.class);

    private static byte[] zeros = new byte[]{ 0, 0, 0, 0, 0, 0 };

    private static ByteBuffer zeros_bb = ByteBuffer.wrap(TestBitComparator.zeros);

    private static byte[] ones = new byte[]{ 1, 1, 1, 1, 1, 1 };

    private static ByteBuffer ones_bb = ByteBuffer.wrap(TestBitComparator.ones);

    private static byte[] data0 = new byte[]{ 0, 1, 2, 4, 8, 15 };

    private static byte[] data1 = new byte[]{ 15, 0, 0, 0, 0, 0 };

    private static ByteBuffer data1_bb = ByteBuffer.wrap(TestBitComparator.data1);

    private static byte[] data2 = new byte[]{ 0, 0, 0, 0, 0, 15 };

    private static ByteBuffer data2_bb = ByteBuffer.wrap(TestBitComparator.data2);

    private static byte[] data3 = new byte[]{ 15, 15, 15, 15, 15 };

    // data for testing compareTo method with offset and length parameters
    private static byte[] data1_2 = new byte[]{ 15, 15, 0, 0, 0, 0, 0, 15 };

    private static ByteBuffer data1_2_bb = ByteBuffer.wrap(TestBitComparator.data1_2);

    private static byte[] data2_2 = new byte[]{ 15, 0, 0, 0, 0, 0, 15, 15 };

    private static ByteBuffer data2_2_bb = ByteBuffer.wrap(TestBitComparator.data2_2);

    private final int Equal = 0;

    private final int NotEqual = 1;

    @Test
    public void testANDOperation() {
        testOperation(TestBitComparator.zeros, TestBitComparator.ones, AND, NotEqual);
        testOperation(TestBitComparator.data1, TestBitComparator.ones, AND, Equal);
        testOperation(TestBitComparator.data1, TestBitComparator.data0, AND, NotEqual);
        testOperation(TestBitComparator.data2, TestBitComparator.data1, AND, NotEqual);
        testOperation(TestBitComparator.ones, TestBitComparator.data0, AND, Equal);
        testOperation(TestBitComparator.ones, TestBitComparator.data3, AND, NotEqual);
        testOperation(TestBitComparator.zeros_bb, TestBitComparator.ones, AND, NotEqual);
        testOperation(TestBitComparator.data1_bb, TestBitComparator.ones, AND, Equal);
        testOperation(TestBitComparator.data1_bb, TestBitComparator.data0, AND, NotEqual);
        testOperation(TestBitComparator.data2_bb, TestBitComparator.data1, AND, NotEqual);
        testOperation(TestBitComparator.ones_bb, TestBitComparator.data0, AND, Equal);
        testOperation(TestBitComparator.ones_bb, TestBitComparator.data3, AND, NotEqual);
    }

    @Test
    public void testOROperation() {
        testOperation(TestBitComparator.ones, TestBitComparator.zeros, OR, Equal);
        testOperation(TestBitComparator.zeros, TestBitComparator.zeros, OR, NotEqual);
        testOperation(TestBitComparator.data1, TestBitComparator.zeros, OR, Equal);
        testOperation(TestBitComparator.data2, TestBitComparator.data1, OR, Equal);
        testOperation(TestBitComparator.ones, TestBitComparator.data3, OR, NotEqual);
        testOperation(TestBitComparator.ones_bb, TestBitComparator.zeros, OR, Equal);
        testOperation(TestBitComparator.zeros_bb, TestBitComparator.zeros, OR, NotEqual);
        testOperation(TestBitComparator.data1_bb, TestBitComparator.zeros, OR, Equal);
        testOperation(TestBitComparator.data2_bb, TestBitComparator.data1, OR, Equal);
        testOperation(TestBitComparator.ones_bb, TestBitComparator.data3, OR, NotEqual);
    }

    @Test
    public void testXOROperation() {
        testOperation(TestBitComparator.ones, TestBitComparator.zeros, XOR, Equal);
        testOperation(TestBitComparator.zeros, TestBitComparator.zeros, XOR, NotEqual);
        testOperation(TestBitComparator.ones, TestBitComparator.ones, XOR, NotEqual);
        testOperation(TestBitComparator.data2, TestBitComparator.data1, XOR, Equal);
        testOperation(TestBitComparator.ones, TestBitComparator.data3, XOR, NotEqual);
        testOperation(TestBitComparator.ones_bb, TestBitComparator.zeros, XOR, Equal);
        testOperation(TestBitComparator.zeros_bb, TestBitComparator.zeros, XOR, NotEqual);
        testOperation(TestBitComparator.ones_bb, TestBitComparator.ones, XOR, NotEqual);
        testOperation(TestBitComparator.data2_bb, TestBitComparator.data1, XOR, Equal);
        testOperation(TestBitComparator.ones_bb, TestBitComparator.data3, XOR, NotEqual);
    }

    @Test
    public void testANDOperationWithOffset() {
        testOperationWithOffset(TestBitComparator.data1_2, TestBitComparator.ones, AND, Equal);
        testOperationWithOffset(TestBitComparator.data1_2, TestBitComparator.data0, AND, NotEqual);
        testOperationWithOffset(TestBitComparator.data2_2, TestBitComparator.data1, AND, NotEqual);
        testOperationWithOffset(TestBitComparator.data1_2_bb, TestBitComparator.ones, AND, Equal);
        testOperationWithOffset(TestBitComparator.data1_2_bb, TestBitComparator.data0, AND, NotEqual);
        testOperationWithOffset(TestBitComparator.data2_2_bb, TestBitComparator.data1, AND, NotEqual);
    }

    @Test
    public void testOROperationWithOffset() {
        testOperationWithOffset(TestBitComparator.data1_2, TestBitComparator.zeros, OR, Equal);
        testOperationWithOffset(TestBitComparator.data2_2, TestBitComparator.data1, OR, Equal);
        testOperationWithOffset(TestBitComparator.data1_2_bb, TestBitComparator.zeros, OR, Equal);
        testOperationWithOffset(TestBitComparator.data2_2_bb, TestBitComparator.data1, OR, Equal);
    }

    @Test
    public void testXOROperationWithOffset() {
        testOperationWithOffset(TestBitComparator.data2_2, TestBitComparator.data1, XOR, Equal);
        testOperationWithOffset(TestBitComparator.data2_2_bb, TestBitComparator.data1, XOR, Equal);
    }
}

