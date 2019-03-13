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
package org.apache.hadoop.hbase.rest.client;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.rest.HBaseRESTTestingUtility;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.RestTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ RestTests.class, MediumTests.class })
public class TestRemoteTable {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestRemoteTable.class);

    // Verify that invalid URL characters and arbitrary bytes are escaped when
    // constructing REST URLs per HBASE-7621. RemoteHTable should support row keys
    // and qualifiers containing any byte for all table operations.
    private static final String INVALID_URL_CHARS_1 = "|\"\\^{}\u0001\u0002\u0003\u0004\u0005\u0006\u0007\b\t\u000b\f";

    // HColumnDescriptor prevents certain characters in column names.  The following
    // are examples of characters are allowed in column names but are not valid in
    // URLs.
    private static final String INVALID_URL_CHARS_2 = "|^{}\u0242";

    // Besides alphanumeric these characters can also be present in table names.
    private static final String VALID_TABLE_NAME_CHARS = "_-.";

    private static final TableName TABLE = TableName.valueOf(("TestRemoteTable" + (TestRemoteTable.VALID_TABLE_NAME_CHARS)));

    private static final byte[] ROW_1 = Bytes.toBytes(("testrow1" + (TestRemoteTable.INVALID_URL_CHARS_1)));

    private static final byte[] ROW_2 = Bytes.toBytes(("testrow2" + (TestRemoteTable.INVALID_URL_CHARS_1)));

    private static final byte[] ROW_3 = Bytes.toBytes(("testrow3" + (TestRemoteTable.INVALID_URL_CHARS_1)));

    private static final byte[] ROW_4 = Bytes.toBytes(("testrow4" + (TestRemoteTable.INVALID_URL_CHARS_1)));

    private static final byte[] COLUMN_1 = Bytes.toBytes(("a" + (TestRemoteTable.INVALID_URL_CHARS_2)));

    private static final byte[] COLUMN_2 = Bytes.toBytes(("b" + (TestRemoteTable.INVALID_URL_CHARS_2)));

    private static final byte[] COLUMN_3 = Bytes.toBytes(("c" + (TestRemoteTable.INVALID_URL_CHARS_2)));

    private static final byte[] QUALIFIER_1 = Bytes.toBytes(("1" + (TestRemoteTable.INVALID_URL_CHARS_1)));

    private static final byte[] QUALIFIER_2 = Bytes.toBytes(("2" + (TestRemoteTable.INVALID_URL_CHARS_1)));

    private static final byte[] VALUE_1 = Bytes.toBytes("testvalue1");

    private static final byte[] VALUE_2 = Bytes.toBytes("testvalue2");

    private static final long ONE_HOUR = (60 * 60) * 1000;

    private static final long TS_2 = System.currentTimeMillis();

    private static final long TS_1 = (TestRemoteTable.TS_2) - (TestRemoteTable.ONE_HOUR);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static final HBaseRESTTestingUtility REST_TEST_UTIL = new HBaseRESTTestingUtility();

    private RemoteHTable remoteTable;

    @Test
    public void testGetTableDescriptor() throws IOException {
        Table table = null;
        try {
            table = TestRemoteTable.TEST_UTIL.getConnection().getTable(TestRemoteTable.TABLE);
            HTableDescriptor local = table.getTableDescriptor();
            Assert.assertEquals(remoteTable.getTableDescriptor(), local);
        } finally {
            if (null != table)
                table.close();

        }
    }

    @Test
    public void testGet() throws IOException {
        Get get = new Get(TestRemoteTable.ROW_1);
        Result result = remoteTable.get(get);
        byte[] value1 = result.getValue(TestRemoteTable.COLUMN_1, TestRemoteTable.QUALIFIER_1);
        byte[] value2 = result.getValue(TestRemoteTable.COLUMN_2, TestRemoteTable.QUALIFIER_2);
        Assert.assertNotNull(value1);
        Assert.assertTrue(Bytes.equals(TestRemoteTable.VALUE_1, value1));
        Assert.assertNull(value2);
        get = new Get(TestRemoteTable.ROW_1);
        get.addFamily(TestRemoteTable.COLUMN_3);
        result = remoteTable.get(get);
        value1 = result.getValue(TestRemoteTable.COLUMN_1, TestRemoteTable.QUALIFIER_1);
        value2 = result.getValue(TestRemoteTable.COLUMN_2, TestRemoteTable.QUALIFIER_2);
        Assert.assertNull(value1);
        Assert.assertNull(value2);
        get = new Get(TestRemoteTable.ROW_1);
        get.addColumn(TestRemoteTable.COLUMN_1, TestRemoteTable.QUALIFIER_1);
        get.addColumn(TestRemoteTable.COLUMN_2, TestRemoteTable.QUALIFIER_2);
        result = remoteTable.get(get);
        value1 = result.getValue(TestRemoteTable.COLUMN_1, TestRemoteTable.QUALIFIER_1);
        value2 = result.getValue(TestRemoteTable.COLUMN_2, TestRemoteTable.QUALIFIER_2);
        Assert.assertNotNull(value1);
        Assert.assertTrue(Bytes.equals(TestRemoteTable.VALUE_1, value1));
        Assert.assertNull(value2);
        get = new Get(TestRemoteTable.ROW_2);
        result = remoteTable.get(get);
        value1 = result.getValue(TestRemoteTable.COLUMN_1, TestRemoteTable.QUALIFIER_1);
        value2 = result.getValue(TestRemoteTable.COLUMN_2, TestRemoteTable.QUALIFIER_2);
        Assert.assertNotNull(value1);
        Assert.assertTrue(Bytes.equals(TestRemoteTable.VALUE_2, value1));// @TS_2

        Assert.assertNotNull(value2);
        Assert.assertTrue(Bytes.equals(TestRemoteTable.VALUE_2, value2));
        get = new Get(TestRemoteTable.ROW_2);
        get.addFamily(TestRemoteTable.COLUMN_1);
        result = remoteTable.get(get);
        value1 = result.getValue(TestRemoteTable.COLUMN_1, TestRemoteTable.QUALIFIER_1);
        value2 = result.getValue(TestRemoteTable.COLUMN_2, TestRemoteTable.QUALIFIER_2);
        Assert.assertNotNull(value1);
        Assert.assertTrue(Bytes.equals(TestRemoteTable.VALUE_2, value1));// @TS_2

        Assert.assertNull(value2);
        get = new Get(TestRemoteTable.ROW_2);
        get.addColumn(TestRemoteTable.COLUMN_1, TestRemoteTable.QUALIFIER_1);
        get.addColumn(TestRemoteTable.COLUMN_2, TestRemoteTable.QUALIFIER_2);
        result = remoteTable.get(get);
        value1 = result.getValue(TestRemoteTable.COLUMN_1, TestRemoteTable.QUALIFIER_1);
        value2 = result.getValue(TestRemoteTable.COLUMN_2, TestRemoteTable.QUALIFIER_2);
        Assert.assertNotNull(value1);
        Assert.assertTrue(Bytes.equals(TestRemoteTable.VALUE_2, value1));// @TS_2

        Assert.assertNotNull(value2);
        Assert.assertTrue(Bytes.equals(TestRemoteTable.VALUE_2, value2));
        // test timestamp
        get = new Get(TestRemoteTable.ROW_2);
        get.addFamily(TestRemoteTable.COLUMN_1);
        get.addFamily(TestRemoteTable.COLUMN_2);
        get.setTimestamp(TestRemoteTable.TS_1);
        result = remoteTable.get(get);
        value1 = result.getValue(TestRemoteTable.COLUMN_1, TestRemoteTable.QUALIFIER_1);
        value2 = result.getValue(TestRemoteTable.COLUMN_2, TestRemoteTable.QUALIFIER_2);
        Assert.assertNotNull(value1);
        Assert.assertTrue(Bytes.equals(TestRemoteTable.VALUE_1, value1));// @TS_1

        Assert.assertNull(value2);
        // test timerange
        get = new Get(TestRemoteTable.ROW_2);
        get.addFamily(TestRemoteTable.COLUMN_1);
        get.addFamily(TestRemoteTable.COLUMN_2);
        get.setTimeRange(0, ((TestRemoteTable.TS_1) + 1));
        result = remoteTable.get(get);
        value1 = result.getValue(TestRemoteTable.COLUMN_1, TestRemoteTable.QUALIFIER_1);
        value2 = result.getValue(TestRemoteTable.COLUMN_2, TestRemoteTable.QUALIFIER_2);
        Assert.assertNotNull(value1);
        Assert.assertTrue(Bytes.equals(TestRemoteTable.VALUE_1, value1));// @TS_1

        Assert.assertNull(value2);
        // test maxVersions
        get = new Get(TestRemoteTable.ROW_2);
        get.addFamily(TestRemoteTable.COLUMN_1);
        get.setMaxVersions(2);
        result = remoteTable.get(get);
        int count = 0;
        for (Cell kv : result.listCells()) {
            if ((CellUtil.matchingFamily(kv, TestRemoteTable.COLUMN_1)) && ((TestRemoteTable.TS_1) == (kv.getTimestamp()))) {
                Assert.assertTrue(CellUtil.matchingValue(kv, TestRemoteTable.VALUE_1));// @TS_1

                count++;
            }
            if ((CellUtil.matchingFamily(kv, TestRemoteTable.COLUMN_1)) && ((TestRemoteTable.TS_2) == (kv.getTimestamp()))) {
                Assert.assertTrue(CellUtil.matchingValue(kv, TestRemoteTable.VALUE_2));// @TS_2

                count++;
            }
        }
        Assert.assertEquals(2, count);
    }

    @Test
    public void testMultiGet() throws Exception {
        ArrayList<Get> gets = new ArrayList<>(2);
        gets.add(new Get(TestRemoteTable.ROW_1));
        gets.add(new Get(TestRemoteTable.ROW_2));
        Result[] results = remoteTable.get(gets);
        Assert.assertNotNull(results);
        Assert.assertEquals(2, results.length);
        Assert.assertEquals(1, results[0].size());
        Assert.assertEquals(2, results[1].size());
        // Test Versions
        gets = new ArrayList(2);
        Get g = new Get(TestRemoteTable.ROW_1);
        g.setMaxVersions(3);
        gets.add(g);
        gets.add(new Get(TestRemoteTable.ROW_2));
        results = remoteTable.get(gets);
        Assert.assertNotNull(results);
        Assert.assertEquals(2, results.length);
        Assert.assertEquals(1, results[0].size());
        Assert.assertEquals(3, results[1].size());
        // 404
        gets = new ArrayList(1);
        gets.add(new Get(Bytes.toBytes("RESALLYREALLYNOTTHERE")));
        results = remoteTable.get(gets);
        Assert.assertNotNull(results);
        Assert.assertEquals(0, results.length);
        gets = new ArrayList(3);
        gets.add(new Get(Bytes.toBytes("RESALLYREALLYNOTTHERE")));
        gets.add(new Get(TestRemoteTable.ROW_1));
        gets.add(new Get(TestRemoteTable.ROW_2));
        results = remoteTable.get(gets);
        Assert.assertNotNull(results);
        Assert.assertEquals(2, results.length);
    }

    @Test
    public void testPut() throws IOException {
        Put put = new Put(TestRemoteTable.ROW_3);
        put.addColumn(TestRemoteTable.COLUMN_1, TestRemoteTable.QUALIFIER_1, TestRemoteTable.VALUE_1);
        remoteTable.put(put);
        Get get = new Get(TestRemoteTable.ROW_3);
        get.addFamily(TestRemoteTable.COLUMN_1);
        Result result = remoteTable.get(get);
        byte[] value = result.getValue(TestRemoteTable.COLUMN_1, TestRemoteTable.QUALIFIER_1);
        Assert.assertNotNull(value);
        Assert.assertTrue(Bytes.equals(TestRemoteTable.VALUE_1, value));
        // multiput
        List<Put> puts = new ArrayList<>(3);
        put = new Put(TestRemoteTable.ROW_3);
        put.addColumn(TestRemoteTable.COLUMN_2, TestRemoteTable.QUALIFIER_2, TestRemoteTable.VALUE_2);
        puts.add(put);
        put = new Put(TestRemoteTable.ROW_4);
        put.addColumn(TestRemoteTable.COLUMN_1, TestRemoteTable.QUALIFIER_1, TestRemoteTable.VALUE_1);
        puts.add(put);
        put = new Put(TestRemoteTable.ROW_4);
        put.addColumn(TestRemoteTable.COLUMN_2, TestRemoteTable.QUALIFIER_2, TestRemoteTable.VALUE_2);
        puts.add(put);
        remoteTable.put(puts);
        get = new Get(TestRemoteTable.ROW_3);
        get.addFamily(TestRemoteTable.COLUMN_2);
        result = remoteTable.get(get);
        value = result.getValue(TestRemoteTable.COLUMN_2, TestRemoteTable.QUALIFIER_2);
        Assert.assertNotNull(value);
        Assert.assertTrue(Bytes.equals(TestRemoteTable.VALUE_2, value));
        get = new Get(TestRemoteTable.ROW_4);
        result = remoteTable.get(get);
        value = result.getValue(TestRemoteTable.COLUMN_1, TestRemoteTable.QUALIFIER_1);
        Assert.assertNotNull(value);
        Assert.assertTrue(Bytes.equals(TestRemoteTable.VALUE_1, value));
        value = result.getValue(TestRemoteTable.COLUMN_2, TestRemoteTable.QUALIFIER_2);
        Assert.assertNotNull(value);
        Assert.assertTrue(Bytes.equals(TestRemoteTable.VALUE_2, value));
        Assert.assertTrue(Bytes.equals(Bytes.toBytes(("TestRemoteTable" + (TestRemoteTable.VALID_TABLE_NAME_CHARS))), remoteTable.getTableName()));
    }

    @Test
    public void testDelete() throws IOException {
        Put put = new Put(TestRemoteTable.ROW_3);
        put.addColumn(TestRemoteTable.COLUMN_1, TestRemoteTable.QUALIFIER_1, TestRemoteTable.VALUE_1);
        put.addColumn(TestRemoteTable.COLUMN_2, TestRemoteTable.QUALIFIER_2, TestRemoteTable.VALUE_2);
        put.addColumn(TestRemoteTable.COLUMN_3, TestRemoteTable.QUALIFIER_1, TestRemoteTable.VALUE_1);
        put.addColumn(TestRemoteTable.COLUMN_3, TestRemoteTable.QUALIFIER_2, TestRemoteTable.VALUE_2);
        remoteTable.put(put);
        Get get = new Get(TestRemoteTable.ROW_3);
        get.addFamily(TestRemoteTable.COLUMN_1);
        get.addFamily(TestRemoteTable.COLUMN_2);
        get.addFamily(TestRemoteTable.COLUMN_3);
        Result result = remoteTable.get(get);
        byte[] value1 = result.getValue(TestRemoteTable.COLUMN_1, TestRemoteTable.QUALIFIER_1);
        byte[] value2 = result.getValue(TestRemoteTable.COLUMN_2, TestRemoteTable.QUALIFIER_2);
        byte[] value3 = result.getValue(TestRemoteTable.COLUMN_3, TestRemoteTable.QUALIFIER_1);
        byte[] value4 = result.getValue(TestRemoteTable.COLUMN_3, TestRemoteTable.QUALIFIER_2);
        Assert.assertNotNull(value1);
        Assert.assertTrue(Bytes.equals(TestRemoteTable.VALUE_1, value1));
        Assert.assertNotNull(value2);
        Assert.assertTrue(Bytes.equals(TestRemoteTable.VALUE_2, value2));
        Assert.assertNotNull(value3);
        Assert.assertTrue(Bytes.equals(TestRemoteTable.VALUE_1, value3));
        Assert.assertNotNull(value4);
        Assert.assertTrue(Bytes.equals(TestRemoteTable.VALUE_2, value4));
        Delete delete = new Delete(TestRemoteTable.ROW_3);
        delete.addColumn(TestRemoteTable.COLUMN_2, TestRemoteTable.QUALIFIER_2);
        remoteTable.delete(delete);
        get = new Get(TestRemoteTable.ROW_3);
        get.addFamily(TestRemoteTable.COLUMN_1);
        get.addFamily(TestRemoteTable.COLUMN_2);
        result = remoteTable.get(get);
        value1 = result.getValue(TestRemoteTable.COLUMN_1, TestRemoteTable.QUALIFIER_1);
        value2 = result.getValue(TestRemoteTable.COLUMN_2, TestRemoteTable.QUALIFIER_2);
        Assert.assertNotNull(value1);
        Assert.assertTrue(Bytes.equals(TestRemoteTable.VALUE_1, value1));
        Assert.assertNull(value2);
        delete = new Delete(TestRemoteTable.ROW_3);
        delete.setTimestamp(1L);
        remoteTable.delete(delete);
        get = new Get(TestRemoteTable.ROW_3);
        get.addFamily(TestRemoteTable.COLUMN_1);
        get.addFamily(TestRemoteTable.COLUMN_2);
        result = remoteTable.get(get);
        value1 = result.getValue(TestRemoteTable.COLUMN_1, TestRemoteTable.QUALIFIER_1);
        value2 = result.getValue(TestRemoteTable.COLUMN_2, TestRemoteTable.QUALIFIER_2);
        Assert.assertNotNull(value1);
        Assert.assertTrue(Bytes.equals(TestRemoteTable.VALUE_1, value1));
        Assert.assertNull(value2);
        // Delete column family from row
        delete = new Delete(TestRemoteTable.ROW_3);
        delete.addFamily(TestRemoteTable.COLUMN_3);
        remoteTable.delete(delete);
        get = new Get(TestRemoteTable.ROW_3);
        get.addFamily(TestRemoteTable.COLUMN_3);
        result = remoteTable.get(get);
        value3 = result.getValue(TestRemoteTable.COLUMN_3, TestRemoteTable.QUALIFIER_1);
        value4 = result.getValue(TestRemoteTable.COLUMN_3, TestRemoteTable.QUALIFIER_2);
        Assert.assertNull(value3);
        Assert.assertNull(value4);
        delete = new Delete(TestRemoteTable.ROW_3);
        remoteTable.delete(delete);
        get = new Get(TestRemoteTable.ROW_3);
        get.addFamily(TestRemoteTable.COLUMN_1);
        get.addFamily(TestRemoteTable.COLUMN_2);
        result = remoteTable.get(get);
        value1 = result.getValue(TestRemoteTable.COLUMN_1, TestRemoteTable.QUALIFIER_1);
        value2 = result.getValue(TestRemoteTable.COLUMN_2, TestRemoteTable.QUALIFIER_2);
        Assert.assertNull(value1);
        Assert.assertNull(value2);
    }

    /**
     * Test RemoteHTable.Scanner
     */
    @Test
    public void testScanner() throws IOException {
        List<Put> puts = new ArrayList<>(4);
        Put put = new Put(TestRemoteTable.ROW_1);
        put.addColumn(TestRemoteTable.COLUMN_1, TestRemoteTable.QUALIFIER_1, TestRemoteTable.VALUE_1);
        puts.add(put);
        put = new Put(TestRemoteTable.ROW_2);
        put.addColumn(TestRemoteTable.COLUMN_1, TestRemoteTable.QUALIFIER_1, TestRemoteTable.VALUE_1);
        puts.add(put);
        put = new Put(TestRemoteTable.ROW_3);
        put.addColumn(TestRemoteTable.COLUMN_1, TestRemoteTable.QUALIFIER_1, TestRemoteTable.VALUE_1);
        puts.add(put);
        put = new Put(TestRemoteTable.ROW_4);
        put.addColumn(TestRemoteTable.COLUMN_1, TestRemoteTable.QUALIFIER_1, TestRemoteTable.VALUE_1);
        puts.add(put);
        remoteTable.put(puts);
        ResultScanner scanner = remoteTable.getScanner(new Scan());
        Result[] results = scanner.next(1);
        Assert.assertNotNull(results);
        Assert.assertEquals(1, results.length);
        Assert.assertTrue(Bytes.equals(TestRemoteTable.ROW_1, results[0].getRow()));
        Result result = scanner.next();
        Assert.assertNotNull(result);
        Assert.assertTrue(Bytes.equals(TestRemoteTable.ROW_2, result.getRow()));
        results = scanner.next(2);
        Assert.assertNotNull(results);
        Assert.assertEquals(2, results.length);
        Assert.assertTrue(Bytes.equals(TestRemoteTable.ROW_3, results[0].getRow()));
        Assert.assertTrue(Bytes.equals(TestRemoteTable.ROW_4, results[1].getRow()));
        results = scanner.next(1);
        Assert.assertNull(results);
        scanner.close();
        scanner = remoteTable.getScanner(TestRemoteTable.COLUMN_1);
        results = scanner.next(4);
        Assert.assertNotNull(results);
        Assert.assertEquals(4, results.length);
        Assert.assertTrue(Bytes.equals(TestRemoteTable.ROW_1, results[0].getRow()));
        Assert.assertTrue(Bytes.equals(TestRemoteTable.ROW_2, results[1].getRow()));
        Assert.assertTrue(Bytes.equals(TestRemoteTable.ROW_3, results[2].getRow()));
        Assert.assertTrue(Bytes.equals(TestRemoteTable.ROW_4, results[3].getRow()));
        scanner.close();
        scanner = remoteTable.getScanner(TestRemoteTable.COLUMN_1, TestRemoteTable.QUALIFIER_1);
        results = scanner.next(4);
        Assert.assertNotNull(results);
        Assert.assertEquals(4, results.length);
        Assert.assertTrue(Bytes.equals(TestRemoteTable.ROW_1, results[0].getRow()));
        Assert.assertTrue(Bytes.equals(TestRemoteTable.ROW_2, results[1].getRow()));
        Assert.assertTrue(Bytes.equals(TestRemoteTable.ROW_3, results[2].getRow()));
        Assert.assertTrue(Bytes.equals(TestRemoteTable.ROW_4, results[3].getRow()));
        scanner.close();
        Assert.assertTrue(remoteTable.isAutoFlush());
    }

    @Test
    public void testCheckAndDelete() throws IOException {
        Get get = new Get(TestRemoteTable.ROW_1);
        Result result = remoteTable.get(get);
        byte[] value1 = result.getValue(TestRemoteTable.COLUMN_1, TestRemoteTable.QUALIFIER_1);
        byte[] value2 = result.getValue(TestRemoteTable.COLUMN_2, TestRemoteTable.QUALIFIER_2);
        Assert.assertNotNull(value1);
        Assert.assertTrue(Bytes.equals(TestRemoteTable.VALUE_1, value1));
        Assert.assertNull(value2);
        Assert.assertTrue(remoteTable.exists(get));
        Assert.assertEquals(1, remoteTable.existsAll(Collections.singletonList(get)).length);
        Delete delete = new Delete(TestRemoteTable.ROW_1);
        remoteTable.checkAndMutate(TestRemoteTable.ROW_1, TestRemoteTable.COLUMN_1).qualifier(TestRemoteTable.QUALIFIER_1).ifEquals(TestRemoteTable.VALUE_1).thenDelete(delete);
        Assert.assertFalse(remoteTable.exists(get));
        Put put = new Put(TestRemoteTable.ROW_1);
        put.addColumn(TestRemoteTable.COLUMN_1, TestRemoteTable.QUALIFIER_1, TestRemoteTable.VALUE_1);
        remoteTable.put(put);
        Assert.assertTrue(remoteTable.checkAndMutate(TestRemoteTable.ROW_1, TestRemoteTable.COLUMN_1).qualifier(TestRemoteTable.QUALIFIER_1).ifEquals(TestRemoteTable.VALUE_1).thenPut(put));
        Assert.assertFalse(remoteTable.checkAndMutate(TestRemoteTable.ROW_1, TestRemoteTable.COLUMN_1).qualifier(TestRemoteTable.QUALIFIER_1).ifEquals(TestRemoteTable.VALUE_2).thenPut(put));
    }

    /**
     * Test RemoteHable.Scanner.iterator method
     */
    @Test
    public void testIteratorScaner() throws IOException {
        List<Put> puts = new ArrayList<>(4);
        Put put = new Put(TestRemoteTable.ROW_1);
        put.addColumn(TestRemoteTable.COLUMN_1, TestRemoteTable.QUALIFIER_1, TestRemoteTable.VALUE_1);
        puts.add(put);
        put = new Put(TestRemoteTable.ROW_2);
        put.addColumn(TestRemoteTable.COLUMN_1, TestRemoteTable.QUALIFIER_1, TestRemoteTable.VALUE_1);
        puts.add(put);
        put = new Put(TestRemoteTable.ROW_3);
        put.addColumn(TestRemoteTable.COLUMN_1, TestRemoteTable.QUALIFIER_1, TestRemoteTable.VALUE_1);
        puts.add(put);
        put = new Put(TestRemoteTable.ROW_4);
        put.addColumn(TestRemoteTable.COLUMN_1, TestRemoteTable.QUALIFIER_1, TestRemoteTable.VALUE_1);
        puts.add(put);
        remoteTable.put(puts);
        ResultScanner scanner = remoteTable.getScanner(new Scan());
        Iterator<Result> iterator = scanner.iterator();
        Assert.assertTrue(iterator.hasNext());
        int counter = 0;
        while (iterator.hasNext()) {
            iterator.next();
            counter++;
        } 
        Assert.assertEquals(4, counter);
    }

    /**
     * Test a some methods of class Response.
     */
    @Test
    public void testResponse() {
        Response response = new Response(200);
        Assert.assertEquals(200, response.getCode());
        Header[] headers = new Header[2];
        headers[0] = new BasicHeader("header1", "value1");
        headers[1] = new BasicHeader("header2", "value2");
        response = new Response(200, headers);
        Assert.assertEquals("value1", response.getHeader("header1"));
        Assert.assertFalse(response.hasBody());
        response.setCode(404);
        Assert.assertEquals(404, response.getCode());
        headers = new Header[2];
        headers[0] = new BasicHeader("header1", "value1.1");
        headers[1] = new BasicHeader("header2", "value2");
        response.setHeaders(headers);
        Assert.assertEquals("value1.1", response.getHeader("header1"));
        response.setBody(Bytes.toBytes("body"));
        Assert.assertTrue(response.hasBody());
    }
}

