/**
 * Copyright (c) 2016 YCSB contributors. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You
 * may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License. See accompanying
 * LICENSE file.
 */
package com.yahoo.ycsb.db;


import Status.NOT_FOUND;
import Status.OK;
import com.yahoo.ycsb.ByteIterator;
import com.yahoo.ycsb.Status;
import com.yahoo.ycsb.StringByteIterator;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.Test;


/**
 * Integration tests for the YCSB AsyncHBase client, using an HBase minicluster.
 * These are the same as those for the hbase10 client.
 */
public class AsyncHBaseTest {
    private static final String COLUMN_FAMILY = "cf";

    private static HBaseTestingUtility testingUtil;

    private AsyncHBaseClient client;

    private Table table = null;

    private String tableName;

    @Test
    public void testRead() throws Exception {
        final String rowKey = "row1";
        final Put p = new Put(Bytes.toBytes(rowKey));
        p.addColumn(Bytes.toBytes(AsyncHBaseTest.COLUMN_FAMILY), Bytes.toBytes("column1"), Bytes.toBytes("value1"));
        p.addColumn(Bytes.toBytes(AsyncHBaseTest.COLUMN_FAMILY), Bytes.toBytes("column2"), Bytes.toBytes("value2"));
        table.put(p);
        final HashMap<String, ByteIterator> result = new HashMap<String, ByteIterator>();
        final Status status = client.read(tableName, rowKey, null, result);
        Assert.assertEquals(OK, status);
        Assert.assertEquals(2, result.size());
        Assert.assertEquals("value1", result.get("column1").toString());
        Assert.assertEquals("value2", result.get("column2").toString());
    }

    @Test
    public void testReadMissingRow() throws Exception {
        final HashMap<String, ByteIterator> result = new HashMap<String, ByteIterator>();
        final Status status = client.read(tableName, "Missing row", null, result);
        Assert.assertEquals(NOT_FOUND, status);
        Assert.assertEquals(0, result.size());
    }

    @Test
    public void testScan() throws Exception {
        // Fill with data
        final String colStr = "row_number";
        final byte[] col = Bytes.toBytes(colStr);
        final int n = 10;
        final List<Put> puts = new ArrayList<Put>(n);
        for (int i = 0; i < n; i++) {
            final byte[] key = Bytes.toBytes(String.format("%05d", i));
            final byte[] value = ByteBuffer.allocate(4).putInt(i).array();
            final Put p = new Put(key);
            p.addColumn(Bytes.toBytes(AsyncHBaseTest.COLUMN_FAMILY), col, value);
            puts.add(p);
        }
        table.put(puts);
        // Test
        final Vector<HashMap<String, ByteIterator>> result = new Vector<HashMap<String, ByteIterator>>();
        // Scan 5 records, skipping the first
        client.scan(tableName, "00001", 5, null, result);
        Assert.assertEquals(5, result.size());
        for (int i = 0; i < 5; i++) {
            final HashMap<String, ByteIterator> row = result.get(i);
            Assert.assertEquals(1, row.size());
            Assert.assertTrue(row.containsKey(colStr));
            final byte[] bytes = row.get(colStr).toArray();
            final ByteBuffer buf = ByteBuffer.wrap(bytes);
            final int rowNum = buf.getInt();
            Assert.assertEquals((i + 1), rowNum);
        }
    }

    @Test
    public void testUpdate() throws Exception {
        final String key = "key";
        final HashMap<String, String> input = new HashMap<String, String>();
        input.put("column1", "value1");
        input.put("column2", "value2");
        final Status status = client.insert(tableName, key, StringByteIterator.getByteIteratorMap(input));
        Assert.assertEquals(OK, status);
        // Verify result
        final Get get = new Get(Bytes.toBytes(key));
        final Result result = this.table.get(get);
        Assert.assertFalse(result.isEmpty());
        Assert.assertEquals(2, result.size());
        for (final Map.Entry<String, String> entry : input.entrySet()) {
            Assert.assertEquals(entry.getValue(), new String(result.getValue(Bytes.toBytes(AsyncHBaseTest.COLUMN_FAMILY), Bytes.toBytes(entry.getKey()))));
        }
    }
}

