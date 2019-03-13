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
package org.apache.hadoop.hbase.mapreduce;


import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HTestConst;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category(MediumTests.class)
public class TestTableRecordReader {
    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestTableRecordReader.class);

    private static TableName TABLE_NAME = TableName.valueOf("TestTableRecordReader");

    private static int NUM_ROWS = 5;

    private static byte[] ROW = Bytes.toBytes("testRow");

    private static byte[][] ROWS = HTestConst.makeNAscii(TestTableRecordReader.ROW, TestTableRecordReader.NUM_ROWS);

    private static int NUM_FAMILIES = 2;

    private static byte[] FAMILY = Bytes.toBytes("testFamily");

    private static byte[][] FAMILIES = HTestConst.makeNAscii(TestTableRecordReader.FAMILY, TestTableRecordReader.NUM_FAMILIES);

    private static int NUM_QUALIFIERS = 2;

    private static byte[] QUALIFIER = Bytes.toBytes("testQualifier");

    private static byte[][] QUALIFIERS = HTestConst.makeNAscii(TestTableRecordReader.QUALIFIER, TestTableRecordReader.NUM_QUALIFIERS);

    private static int VALUE_SIZE = 10;

    private static byte[] VALUE = Bytes.createMaxByteArray(TestTableRecordReader.VALUE_SIZE);

    private static final int TIMEOUT = 4000;

    @Test
    public void test() throws Exception {
        try (Connection conn = ConnectionFactory.createConnection(TestTableRecordReader.TEST_UTIL.getConfiguration());Table table = conn.getTable(TestTableRecordReader.TABLE_NAME)) {
            org.apache.hadoop.hbase.mapreduce.TableRecordReaderImpl trr = new org.apache.hadoop.hbase.mapreduce.TableRecordReaderImpl();
            Scan scan = new Scan().setMaxResultSize(1).setCaching(Integer.MAX_VALUE).setNeedCursorResult(true);
            trr.setScan(scan);
            trr.setHTable(table);
            trr.initialize(null, null);
            int num = 0;
            while (trr.nextKeyValue()) {
                num++;
            } 
            Assert.assertEquals((((TestTableRecordReader.NUM_ROWS) * (TestTableRecordReader.NUM_FAMILIES)) * (TestTableRecordReader.NUM_QUALIFIERS)), num);
        }
    }
}

