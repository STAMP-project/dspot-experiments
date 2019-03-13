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
package org.apache.hadoop.hbase.security.visibility;


import HConstants.LATEST_TIMESTAMP;
import java.io.IOException;
import java.security.PrivilegedExceptionAction;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellScanner;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.protobuf.generated.VisibilityLabelsProtos.VisibilityLabelsResponse;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.testclassification.SecurityTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ SecurityTests.class, LargeTests.class })
public class TestVisibilityLabelsWithDeletes extends VisibilityLabelsWithDeletesTestBase {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestVisibilityLabelsWithDeletes.class);

    @Test
    public void testVisibilityLabelsWithDeleteColumnsWithMultipleVersions() throws Exception {
        setAuths();
        final TableName tableName = TableName.valueOf(testName.getMethodName());
        try (Table table = doPuts(tableName)) {
            VisibilityLabelsWithDeletesTestBase.TEST_UTIL.getAdmin().flush(tableName);
            PrivilegedExceptionAction<Void> actiona = new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws Exception {
                    try (Connection connection = ConnectionFactory.createConnection(VisibilityLabelsWithDeletesTestBase.conf);Table table = connection.getTable(tableName)) {
                        Delete d = new Delete(VisibilityLabelsWithDeletesTestBase.row1);
                        d.setCellVisibility(new CellVisibility((((((((("(" + (VisibilityLabelsWithDeletesTestBase.PRIVATE)) + "&") + (VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL)) + ")|(") + (VisibilityLabelsWithDeletesTestBase.SECRET)) + "&") + (VisibilityLabelsWithDeletesTestBase.TOPSECRET)) + ")")));
                        d.addColumns(VisibilityLabelsWithDeletesTestBase.fam, VisibilityLabelsWithDeletesTestBase.qual, 125L);
                        table.delete(d);
                    } catch (Throwable t) {
                        throw new IOException(t);
                    }
                    return null;
                }
            };
            VisibilityLabelsWithDeletesTestBase.SUPERUSER.runAs(actiona);
            VisibilityLabelsWithDeletesTestBase.TEST_UTIL.getAdmin().flush(tableName);
            Scan s = new Scan();
            s.readVersions(5);
            s.setAuthorizations(new Authorizations(VisibilityLabelsWithDeletesTestBase.SECRET, VisibilityLabelsWithDeletesTestBase.PRIVATE, VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL, VisibilityLabelsWithDeletesTestBase.TOPSECRET));
            ResultScanner scanner = table.getScanner(s);
            Result[] next = scanner.next(3);
            Assert.assertTrue(((next.length) == 2));
            CellScanner cellScanner = next[0].cellScanner();
            cellScanner.advance();
            Cell current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row1, 0, TestVisibilityLabelsWithDeletes.row1.length));
            Assert.assertEquals(127L, current.getTimestamp());
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row1, 0, TestVisibilityLabelsWithDeletes.row1.length));
            Assert.assertEquals(126L, current.getTimestamp());
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row1, 0, TestVisibilityLabelsWithDeletes.row1.length));
            Assert.assertEquals(125L, current.getTimestamp());
            cellScanner = next[1].cellScanner();
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row2, 0, TestVisibilityLabelsWithDeletes.row2.length));
        }
    }

    @Test
    public void testVisibilityLabelsWithDeleteColumnsWithMultipleVersionsNoTimestamp() throws Exception {
        setAuths();
        final TableName tableName = TableName.valueOf(testName.getMethodName());
        try (Table table = doPuts(tableName)) {
            VisibilityLabelsWithDeletesTestBase.TEST_UTIL.getAdmin().flush(tableName);
            PrivilegedExceptionAction<Void> actiona = new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws Exception {
                    try (Connection connection = ConnectionFactory.createConnection(VisibilityLabelsWithDeletesTestBase.conf);Table table = connection.getTable(tableName)) {
                        Delete d1 = new Delete(VisibilityLabelsWithDeletesTestBase.row1);
                        d1.setCellVisibility(new CellVisibility(VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL));
                        d1.addColumns(VisibilityLabelsWithDeletesTestBase.fam, VisibilityLabelsWithDeletesTestBase.qual);
                        table.delete(d1);
                        Delete d2 = new Delete(VisibilityLabelsWithDeletesTestBase.row1);
                        d2.setCellVisibility(new CellVisibility((((VisibilityLabelsWithDeletesTestBase.SECRET) + "&") + (VisibilityLabelsWithDeletesTestBase.TOPSECRET))));
                        d2.addColumns(VisibilityLabelsWithDeletesTestBase.fam, VisibilityLabelsWithDeletesTestBase.qual);
                        table.delete(d2);
                        Delete d3 = new Delete(VisibilityLabelsWithDeletesTestBase.row1);
                        d3.setCellVisibility(new CellVisibility((((((((("(" + (VisibilityLabelsWithDeletesTestBase.PRIVATE)) + "&") + (VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL)) + ")|(") + (VisibilityLabelsWithDeletesTestBase.SECRET)) + "&") + (VisibilityLabelsWithDeletesTestBase.TOPSECRET)) + ")")));
                        d3.addColumns(VisibilityLabelsWithDeletesTestBase.fam, VisibilityLabelsWithDeletesTestBase.qual);
                        table.delete(d3);
                    } catch (Throwable t) {
                        throw new IOException(t);
                    }
                    return null;
                }
            };
            VisibilityLabelsWithDeletesTestBase.SUPERUSER.runAs(actiona);
            Scan s = new Scan();
            s.readVersions(5);
            s.setAuthorizations(new Authorizations(VisibilityLabelsWithDeletesTestBase.SECRET, VisibilityLabelsWithDeletesTestBase.PRIVATE, VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL, VisibilityLabelsWithDeletesTestBase.TOPSECRET));
            ResultScanner scanner = table.getScanner(s);
            Result[] next = scanner.next(3);
            Assert.assertEquals(1, next.length);
            CellScanner cellScanner = next[0].cellScanner();
            cellScanner.advance();
            Cell current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row2, 0, TestVisibilityLabelsWithDeletes.row2.length));
        }
    }

    @Test
    public void testVisibilityLabelsWithDeleteColumnsNoMatchVisExpWithMultipleVersionsNoTimestamp() throws Exception {
        setAuths();
        final TableName tableName = TableName.valueOf(testName.getMethodName());
        try (Table table = doPuts(tableName)) {
            VisibilityLabelsWithDeletesTestBase.TEST_UTIL.getAdmin().flush(tableName);
            PrivilegedExceptionAction<Void> actiona = new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws Exception {
                    try (Connection connection = ConnectionFactory.createConnection(VisibilityLabelsWithDeletesTestBase.conf);Table table = connection.getTable(tableName)) {
                        Delete d = new Delete(VisibilityLabelsWithDeletesTestBase.row1);
                        d.setCellVisibility(new CellVisibility(VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL));
                        d.addColumns(VisibilityLabelsWithDeletesTestBase.fam, VisibilityLabelsWithDeletesTestBase.qual);
                        table.delete(d);
                        d = new Delete(VisibilityLabelsWithDeletesTestBase.row1);
                        d.setCellVisibility(new CellVisibility(VisibilityLabelsWithDeletesTestBase.SECRET));
                        d.addColumns(VisibilityLabelsWithDeletesTestBase.fam, VisibilityLabelsWithDeletesTestBase.qual);
                        table.delete(d);
                        d = new Delete(VisibilityLabelsWithDeletesTestBase.row1);
                        d.setCellVisibility(new CellVisibility((((((((("(" + (VisibilityLabelsWithDeletesTestBase.PRIVATE)) + "&") + (VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL)) + ")|(") + (VisibilityLabelsWithDeletesTestBase.SECRET)) + "&") + (VisibilityLabelsWithDeletesTestBase.TOPSECRET)) + ")")));
                        d.addColumns(VisibilityLabelsWithDeletesTestBase.fam, VisibilityLabelsWithDeletesTestBase.qual);
                        table.delete(d);
                    } catch (Throwable t) {
                        throw new IOException(t);
                    }
                    return null;
                }
            };
            VisibilityLabelsWithDeletesTestBase.SUPERUSER.runAs(actiona);
            Scan s = new Scan();
            s.readVersions(5);
            s.setAuthorizations(new Authorizations(VisibilityLabelsWithDeletesTestBase.SECRET, VisibilityLabelsWithDeletesTestBase.PRIVATE, VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL, VisibilityLabelsWithDeletesTestBase.TOPSECRET));
            ResultScanner scanner = table.getScanner(s);
            Result[] next = scanner.next(3);
            Assert.assertTrue(((next.length) == 2));
            CellScanner cellScanner = next[0].cellScanner();
            cellScanner.advance();
            Cell current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row1, 0, TestVisibilityLabelsWithDeletes.row1.length));
            cellScanner = next[1].cellScanner();
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row2, 0, TestVisibilityLabelsWithDeletes.row2.length));
        }
    }

    @Test
    public void testVisibilityLabelsWithDeleteFamilyWithMultipleVersionsNoTimestamp() throws Exception {
        setAuths();
        final TableName tableName = TableName.valueOf(testName.getMethodName());
        try (Table table = doPuts(tableName)) {
            VisibilityLabelsWithDeletesTestBase.TEST_UTIL.getAdmin().flush(tableName);
            PrivilegedExceptionAction<Void> actiona = new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws Exception {
                    try (Connection connection = ConnectionFactory.createConnection(VisibilityLabelsWithDeletesTestBase.conf);Table table = connection.getTable(tableName)) {
                        Delete d1 = new Delete(VisibilityLabelsWithDeletesTestBase.row1);
                        d1.setCellVisibility(new CellVisibility(VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL));
                        d1.addFamily(VisibilityLabelsWithDeletesTestBase.fam);
                        table.delete(d1);
                        Delete d2 = new Delete(VisibilityLabelsWithDeletesTestBase.row1);
                        d2.setCellVisibility(new CellVisibility((((VisibilityLabelsWithDeletesTestBase.SECRET) + "&") + (VisibilityLabelsWithDeletesTestBase.TOPSECRET))));
                        d2.addFamily(VisibilityLabelsWithDeletesTestBase.fam);
                        table.delete(d2);
                        Delete d3 = new Delete(VisibilityLabelsWithDeletesTestBase.row1);
                        d3.setCellVisibility(new CellVisibility((((((((("(" + (VisibilityLabelsWithDeletesTestBase.PRIVATE)) + "&") + (VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL)) + ")|(") + (VisibilityLabelsWithDeletesTestBase.SECRET)) + "&") + (VisibilityLabelsWithDeletesTestBase.TOPSECRET)) + ")")));
                        d3.addFamily(VisibilityLabelsWithDeletesTestBase.fam);
                        table.delete(d3);
                    } catch (Throwable t) {
                        throw new IOException(t);
                    }
                    return null;
                }
            };
            VisibilityLabelsWithDeletesTestBase.SUPERUSER.runAs(actiona);
            Scan s = new Scan();
            s.readVersions(5);
            s.setAuthorizations(new Authorizations(VisibilityLabelsWithDeletesTestBase.SECRET, VisibilityLabelsWithDeletesTestBase.PRIVATE, VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL, VisibilityLabelsWithDeletesTestBase.TOPSECRET));
            ResultScanner scanner = table.getScanner(s);
            Result[] next = scanner.next(3);
            Assert.assertEquals(1, next.length);
            CellScanner cellScanner = next[0].cellScanner();
            cellScanner.advance();
            Cell current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row2, 0, TestVisibilityLabelsWithDeletes.row2.length));
        }
    }

    @Test
    public void testDeleteColumnsWithoutAndWithVisibilityLabels() throws Exception {
        TableName tableName = createTable();
        try (Table table = VisibilityLabelsWithDeletesTestBase.TEST_UTIL.getConnection().getTable(tableName)) {
            Put put = new Put(VisibilityLabelsWithDeletesTestBase.row1);
            put.addColumn(VisibilityLabelsWithDeletesTestBase.fam, VisibilityLabelsWithDeletesTestBase.qual, VisibilityLabelsWithDeletesTestBase.value);
            put.setCellVisibility(new CellVisibility(VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL));
            table.put(put);
            Delete d = new Delete(VisibilityLabelsWithDeletesTestBase.row1);
            // without visibility
            d.addColumns(VisibilityLabelsWithDeletesTestBase.fam, VisibilityLabelsWithDeletesTestBase.qual, LATEST_TIMESTAMP);
            table.delete(d);
            PrivilegedExceptionAction<Void> scanAction = new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws Exception {
                    try (Connection connection = ConnectionFactory.createConnection(VisibilityLabelsWithDeletesTestBase.conf);Table table = connection.getTable(tableName)) {
                        Scan s = new Scan();
                        ResultScanner scanner = table.getScanner(s);
                        Result[] next = scanner.next(3);
                        Assert.assertEquals(1, next.length);
                    }
                    return null;
                }
            };
            VisibilityLabelsWithDeletesTestBase.SUPERUSER.runAs(scanAction);
            d = new Delete(VisibilityLabelsWithDeletesTestBase.row1);
            // with visibility
            d.setCellVisibility(new CellVisibility(VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL));
            d.addColumns(VisibilityLabelsWithDeletesTestBase.fam, VisibilityLabelsWithDeletesTestBase.qual, LATEST_TIMESTAMP);
            table.delete(d);
            scanAction = new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws Exception {
                    try (Connection connection = ConnectionFactory.createConnection(VisibilityLabelsWithDeletesTestBase.conf);Table table = connection.getTable(tableName)) {
                        Scan s = new Scan();
                        ResultScanner scanner = table.getScanner(s);
                        Result[] next = scanner.next(3);
                        Assert.assertEquals(0, next.length);
                    }
                    return null;
                }
            };
            VisibilityLabelsWithDeletesTestBase.SUPERUSER.runAs(scanAction);
        }
    }

    @Test
    public void testDeleteColumnsWithAndWithoutVisibilityLabels() throws Exception {
        TableName tableName = createTable();
        try (Table table = VisibilityLabelsWithDeletesTestBase.TEST_UTIL.getConnection().getTable(tableName)) {
            Put put = new Put(VisibilityLabelsWithDeletesTestBase.row1);
            put.addColumn(VisibilityLabelsWithDeletesTestBase.fam, VisibilityLabelsWithDeletesTestBase.qual, VisibilityLabelsWithDeletesTestBase.value);
            put.setCellVisibility(new CellVisibility(VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL));
            table.put(put);
            Delete d = new Delete(VisibilityLabelsWithDeletesTestBase.row1);
            // with visibility
            d.setCellVisibility(new CellVisibility(VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL));
            d.addColumns(VisibilityLabelsWithDeletesTestBase.fam, VisibilityLabelsWithDeletesTestBase.qual, LATEST_TIMESTAMP);
            table.delete(d);
            PrivilegedExceptionAction<Void> scanAction = new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws Exception {
                    try (Connection connection = ConnectionFactory.createConnection(VisibilityLabelsWithDeletesTestBase.conf);Table table = connection.getTable(tableName)) {
                        Scan s = new Scan();
                        ResultScanner scanner = table.getScanner(s);
                        Result[] next = scanner.next(3);
                        Assert.assertEquals(0, next.length);
                    }
                    return null;
                }
            };
            VisibilityLabelsWithDeletesTestBase.SUPERUSER.runAs(scanAction);
            d = new Delete(VisibilityLabelsWithDeletesTestBase.row1);
            // without visibility
            d.addColumns(VisibilityLabelsWithDeletesTestBase.fam, VisibilityLabelsWithDeletesTestBase.qual, LATEST_TIMESTAMP);
            table.delete(d);
            scanAction = new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws Exception {
                    try (Connection connection = ConnectionFactory.createConnection(VisibilityLabelsWithDeletesTestBase.conf);Table table = connection.getTable(tableName)) {
                        Scan s = new Scan();
                        ResultScanner scanner = table.getScanner(s);
                        Result[] next = scanner.next(3);
                        Assert.assertEquals(0, next.length);
                    }
                    return null;
                }
            };
            VisibilityLabelsWithDeletesTestBase.SUPERUSER.runAs(scanAction);
        }
    }

    @Test
    public void testDeleteFamiliesWithoutAndWithVisibilityLabels() throws Exception {
        TableName tableName = createTable();
        try (Table table = VisibilityLabelsWithDeletesTestBase.TEST_UTIL.getConnection().getTable(tableName)) {
            Put put = new Put(VisibilityLabelsWithDeletesTestBase.row1);
            put.addColumn(VisibilityLabelsWithDeletesTestBase.fam, VisibilityLabelsWithDeletesTestBase.qual, VisibilityLabelsWithDeletesTestBase.value);
            put.setCellVisibility(new CellVisibility(VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL));
            table.put(put);
            Delete d = new Delete(VisibilityLabelsWithDeletesTestBase.row1);
            // without visibility
            d.addFamily(VisibilityLabelsWithDeletesTestBase.fam);
            table.delete(d);
            PrivilegedExceptionAction<Void> scanAction = new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws Exception {
                    try (Connection connection = ConnectionFactory.createConnection(VisibilityLabelsWithDeletesTestBase.conf);Table table = connection.getTable(tableName)) {
                        Scan s = new Scan();
                        ResultScanner scanner = table.getScanner(s);
                        Result[] next = scanner.next(3);
                        Assert.assertEquals(1, next.length);
                    }
                    return null;
                }
            };
            VisibilityLabelsWithDeletesTestBase.SUPERUSER.runAs(scanAction);
            d = new Delete(VisibilityLabelsWithDeletesTestBase.row1);
            // with visibility
            d.setCellVisibility(new CellVisibility(VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL));
            d.addFamily(VisibilityLabelsWithDeletesTestBase.fam);
            table.delete(d);
            scanAction = new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws Exception {
                    try (Connection connection = ConnectionFactory.createConnection(VisibilityLabelsWithDeletesTestBase.conf);Table table = connection.getTable(tableName)) {
                        Scan s = new Scan();
                        ResultScanner scanner = table.getScanner(s);
                        Result[] next = scanner.next(3);
                        Assert.assertEquals(0, next.length);
                    }
                    return null;
                }
            };
            VisibilityLabelsWithDeletesTestBase.SUPERUSER.runAs(scanAction);
        }
    }

    @Test
    public void testDeleteFamiliesWithAndWithoutVisibilityLabels() throws Exception {
        TableName tableName = createTable();
        try (Table table = VisibilityLabelsWithDeletesTestBase.TEST_UTIL.getConnection().getTable(tableName)) {
            Put put = new Put(VisibilityLabelsWithDeletesTestBase.row1);
            put.addColumn(VisibilityLabelsWithDeletesTestBase.fam, VisibilityLabelsWithDeletesTestBase.qual, VisibilityLabelsWithDeletesTestBase.value);
            put.setCellVisibility(new CellVisibility(VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL));
            table.put(put);
            Delete d = new Delete(VisibilityLabelsWithDeletesTestBase.row1);
            d.setCellVisibility(new CellVisibility(VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL));
            // with visibility
            d.addFamily(VisibilityLabelsWithDeletesTestBase.fam);
            table.delete(d);
            PrivilegedExceptionAction<Void> scanAction = new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws Exception {
                    try (Connection connection = ConnectionFactory.createConnection(VisibilityLabelsWithDeletesTestBase.conf);Table table = connection.getTable(tableName)) {
                        Scan s = new Scan();
                        ResultScanner scanner = table.getScanner(s);
                        Result[] next = scanner.next(3);
                        Assert.assertEquals(0, next.length);
                    }
                    return null;
                }
            };
            VisibilityLabelsWithDeletesTestBase.SUPERUSER.runAs(scanAction);
            d = new Delete(VisibilityLabelsWithDeletesTestBase.row1);
            // without visibility
            d.addFamily(VisibilityLabelsWithDeletesTestBase.fam);
            table.delete(d);
            scanAction = new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws Exception {
                    try (Connection connection = ConnectionFactory.createConnection(VisibilityLabelsWithDeletesTestBase.conf);Table table = connection.getTable(tableName)) {
                        Scan s = new Scan();
                        ResultScanner scanner = table.getScanner(s);
                        Result[] next = scanner.next(3);
                        Assert.assertEquals(0, next.length);
                    }
                    return null;
                }
            };
            VisibilityLabelsWithDeletesTestBase.SUPERUSER.runAs(scanAction);
        }
    }

    @Test
    public void testDeletesWithoutAndWithVisibilityLabels() throws Exception {
        TableName tableName = createTable();
        try (Table table = VisibilityLabelsWithDeletesTestBase.TEST_UTIL.getConnection().getTable(tableName)) {
            Put put = new Put(VisibilityLabelsWithDeletesTestBase.row1);
            put.addColumn(VisibilityLabelsWithDeletesTestBase.fam, VisibilityLabelsWithDeletesTestBase.qual, VisibilityLabelsWithDeletesTestBase.value);
            put.setCellVisibility(new CellVisibility(VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL));
            table.put(put);
            Delete d = new Delete(VisibilityLabelsWithDeletesTestBase.row1);
            // without visibility
            d.addColumn(VisibilityLabelsWithDeletesTestBase.fam, VisibilityLabelsWithDeletesTestBase.qual);
            table.delete(d);
            PrivilegedExceptionAction<Void> scanAction = new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws Exception {
                    try (Connection connection = ConnectionFactory.createConnection(VisibilityLabelsWithDeletesTestBase.conf);Table table = connection.getTable(tableName)) {
                        Scan s = new Scan();
                        ResultScanner scanner = table.getScanner(s);
                        // The delete would not be able to apply it because of visibility mismatch
                        Result[] next = scanner.next(3);
                        Assert.assertEquals(1, next.length);
                    }
                    return null;
                }
            };
            VisibilityLabelsWithDeletesTestBase.SUPERUSER.runAs(scanAction);
            d = new Delete(VisibilityLabelsWithDeletesTestBase.row1);
            // with visibility
            d.setCellVisibility(new CellVisibility(VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL));
            d.addColumn(VisibilityLabelsWithDeletesTestBase.fam, VisibilityLabelsWithDeletesTestBase.qual);
            table.delete(d);
            scanAction = new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws Exception {
                    try (Connection connection = ConnectionFactory.createConnection(VisibilityLabelsWithDeletesTestBase.conf);Table table = connection.getTable(tableName)) {
                        Scan s = new Scan();
                        ResultScanner scanner = table.getScanner(s);
                        Result[] next = scanner.next(3);
                        // this will alone match
                        Assert.assertEquals(0, next.length);
                    }
                    return null;
                }
            };
            VisibilityLabelsWithDeletesTestBase.SUPERUSER.runAs(scanAction);
        }
    }

    @Test
    public void testVisibilityLabelsWithDeleteFamilyWithPutsReAppearing() throws Exception {
        TableName tableName = createTable(5);
        try (Table table = VisibilityLabelsWithDeletesTestBase.TEST_UTIL.getConnection().getTable(tableName)) {
            Put put = new Put(Bytes.toBytes("row1"));
            put.addColumn(VisibilityLabelsWithDeletesTestBase.fam, VisibilityLabelsWithDeletesTestBase.qual, VisibilityLabelsWithDeletesTestBase.value);
            put.setCellVisibility(new CellVisibility(VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL));
            table.put(put);
            put = new Put(Bytes.toBytes("row1"));
            put.addColumn(VisibilityLabelsWithDeletesTestBase.fam, VisibilityLabelsWithDeletesTestBase.qual, VisibilityLabelsWithDeletesTestBase.value);
            put.setCellVisibility(new CellVisibility(VisibilityLabelsWithDeletesTestBase.SECRET));
            table.put(put);
            VisibilityLabelsWithDeletesTestBase.TEST_UTIL.getAdmin().flush(tableName);
            PrivilegedExceptionAction<Void> actiona = new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws Exception {
                    try (Connection connection = ConnectionFactory.createConnection(VisibilityLabelsWithDeletesTestBase.conf);Table table = connection.getTable(tableName)) {
                        Delete d = new Delete(VisibilityLabelsWithDeletesTestBase.row1);
                        d.setCellVisibility(new CellVisibility(VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL));
                        d.addFamily(VisibilityLabelsWithDeletesTestBase.fam);
                        table.delete(d);
                    } catch (Throwable t) {
                        throw new IOException(t);
                    }
                    return null;
                }
            };
            VisibilityLabelsWithDeletesTestBase.SUPERUSER.runAs(actiona);
            Scan s = new Scan();
            s.readVersions(5);
            s.setAuthorizations(new Authorizations(VisibilityLabelsWithDeletesTestBase.SECRET));
            ResultScanner scanner = table.getScanner(s);
            Result[] next = scanner.next(3);
            Assert.assertEquals(1, next.length);
            put = new Put(Bytes.toBytes("row1"));
            put.addColumn(VisibilityLabelsWithDeletesTestBase.fam, VisibilityLabelsWithDeletesTestBase.qual, VisibilityLabelsWithDeletesTestBase.value1);
            put.setCellVisibility(new CellVisibility(VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL));
            table.put(put);
            actiona = new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws Exception {
                    try (Connection connection = ConnectionFactory.createConnection(VisibilityLabelsWithDeletesTestBase.conf);Table table = connection.getTable(tableName)) {
                        Delete d = new Delete(VisibilityLabelsWithDeletesTestBase.row1);
                        d.setCellVisibility(new CellVisibility(VisibilityLabelsWithDeletesTestBase.SECRET));
                        d.addFamily(VisibilityLabelsWithDeletesTestBase.fam);
                        table.delete(d);
                    } catch (Throwable t) {
                        throw new IOException(t);
                    }
                    return null;
                }
            };
            VisibilityLabelsWithDeletesTestBase.SUPERUSER.runAs(actiona);
            s = new Scan();
            s.readVersions(5);
            s.setAuthorizations(new Authorizations(VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL));
            scanner = table.getScanner(s);
            next = scanner.next(3);
            Assert.assertEquals(1, next.length);
            s = new Scan();
            s.readVersions(5);
            s.setAuthorizations(new Authorizations(VisibilityLabelsWithDeletesTestBase.SECRET));
            scanner = table.getScanner(s);
            Result[] next1 = scanner.next(3);
            Assert.assertEquals(0, next1.length);
        }
    }

    @Test
    public void testVisibilityLabelsWithDeleteColumnsWithPutsReAppearing() throws Exception {
        TableName tableName = createTable(5);
        try (Table table = VisibilityLabelsWithDeletesTestBase.TEST_UTIL.getConnection().getTable(tableName)) {
            Put put = new Put(Bytes.toBytes("row1"));
            put.addColumn(VisibilityLabelsWithDeletesTestBase.fam, VisibilityLabelsWithDeletesTestBase.qual, VisibilityLabelsWithDeletesTestBase.value);
            put.setCellVisibility(new CellVisibility(VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL));
            table.put(put);
            put = new Put(Bytes.toBytes("row1"));
            put.addColumn(VisibilityLabelsWithDeletesTestBase.fam, VisibilityLabelsWithDeletesTestBase.qual, VisibilityLabelsWithDeletesTestBase.value);
            put.setCellVisibility(new CellVisibility(VisibilityLabelsWithDeletesTestBase.SECRET));
            table.put(put);
            VisibilityLabelsWithDeletesTestBase.TEST_UTIL.getAdmin().flush(tableName);
            PrivilegedExceptionAction<Void> actiona = new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws Exception {
                    try (Connection connection = ConnectionFactory.createConnection(VisibilityLabelsWithDeletesTestBase.conf);Table table = connection.getTable(tableName)) {
                        Delete d = new Delete(VisibilityLabelsWithDeletesTestBase.row1);
                        d.setCellVisibility(new CellVisibility(VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL));
                        d.addColumns(VisibilityLabelsWithDeletesTestBase.fam, VisibilityLabelsWithDeletesTestBase.qual);
                        table.delete(d);
                    } catch (Throwable t) {
                        throw new IOException(t);
                    }
                    return null;
                }
            };
            VisibilityLabelsWithDeletesTestBase.SUPERUSER.runAs(actiona);
            Scan s = new Scan();
            s.readVersions(5);
            s.setAuthorizations(new Authorizations(VisibilityLabelsWithDeletesTestBase.SECRET));
            ResultScanner scanner = table.getScanner(s);
            Result[] next = scanner.next(3);
            Assert.assertEquals(1, next.length);
            put = new Put(Bytes.toBytes("row1"));
            put.addColumn(VisibilityLabelsWithDeletesTestBase.fam, VisibilityLabelsWithDeletesTestBase.qual, VisibilityLabelsWithDeletesTestBase.value1);
            put.setCellVisibility(new CellVisibility(VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL));
            table.put(put);
            actiona = new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws Exception {
                    try (Connection connection = ConnectionFactory.createConnection(VisibilityLabelsWithDeletesTestBase.conf);Table table = connection.getTable(tableName)) {
                        Delete d = new Delete(VisibilityLabelsWithDeletesTestBase.row1);
                        d.setCellVisibility(new CellVisibility(VisibilityLabelsWithDeletesTestBase.SECRET));
                        d.addColumns(VisibilityLabelsWithDeletesTestBase.fam, VisibilityLabelsWithDeletesTestBase.qual);
                        table.delete(d);
                    } catch (Throwable t) {
                        throw new IOException(t);
                    }
                    return null;
                }
            };
            VisibilityLabelsWithDeletesTestBase.SUPERUSER.runAs(actiona);
            s = new Scan();
            s.readVersions(5);
            s.setAuthorizations(new Authorizations(VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL));
            scanner = table.getScanner(s);
            next = scanner.next(3);
            Assert.assertEquals(1, next.length);
            s = new Scan();
            s.readVersions(5);
            s.setAuthorizations(new Authorizations(VisibilityLabelsWithDeletesTestBase.SECRET));
            scanner = table.getScanner(s);
            Result[] next1 = scanner.next(3);
            Assert.assertEquals(0, next1.length);
        }
    }

    @Test
    public void testVisibilityCombinations() throws Exception {
        TableName tableName = createTable(5);
        try (Table table = VisibilityLabelsWithDeletesTestBase.TEST_UTIL.getConnection().getTable(tableName)) {
            Put put = new Put(Bytes.toBytes("row1"));
            put.addColumn(VisibilityLabelsWithDeletesTestBase.fam, VisibilityLabelsWithDeletesTestBase.qual, 123L, VisibilityLabelsWithDeletesTestBase.value);
            put.setCellVisibility(new CellVisibility(VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL));
            table.put(put);
            put = new Put(Bytes.toBytes("row1"));
            put.addColumn(VisibilityLabelsWithDeletesTestBase.fam, VisibilityLabelsWithDeletesTestBase.qual, 124L, VisibilityLabelsWithDeletesTestBase.value1);
            put.setCellVisibility(new CellVisibility(VisibilityLabelsWithDeletesTestBase.SECRET));
            table.put(put);
            PrivilegedExceptionAction<Void> actiona = new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws Exception {
                    try (Connection connection = ConnectionFactory.createConnection(VisibilityLabelsWithDeletesTestBase.conf);Table table = connection.getTable(tableName)) {
                        Delete d = new Delete(VisibilityLabelsWithDeletesTestBase.row1);
                        d.setCellVisibility(new CellVisibility(VisibilityLabelsWithDeletesTestBase.SECRET));
                        d.addColumns(VisibilityLabelsWithDeletesTestBase.fam, VisibilityLabelsWithDeletesTestBase.qual, 126L);
                        table.delete(d);
                    } catch (Throwable t) {
                        throw new IOException(t);
                    }
                    try (Connection connection = ConnectionFactory.createConnection(VisibilityLabelsWithDeletesTestBase.conf);Table table = connection.getTable(tableName)) {
                        Delete d = new Delete(VisibilityLabelsWithDeletesTestBase.row1);
                        d.setCellVisibility(new CellVisibility(VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL));
                        d.addColumn(VisibilityLabelsWithDeletesTestBase.fam, VisibilityLabelsWithDeletesTestBase.qual, 123L);
                        table.delete(d);
                    } catch (Throwable t) {
                        throw new IOException(t);
                    }
                    return null;
                }
            };
            VisibilityLabelsWithDeletesTestBase.SUPERUSER.runAs(actiona);
            Scan s = new Scan();
            s.readVersions(5);
            s.setAuthorizations(new Authorizations(VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL, VisibilityLabelsWithDeletesTestBase.SECRET));
            ResultScanner scanner = table.getScanner(s);
            Result[] next = scanner.next(3);
            Assert.assertEquals(0, next.length);
        }
    }

    @Test
    public void testVisibilityLabelsWithDeleteColumnWithSpecificVersionWithPutsReAppearing() throws Exception {
        TableName tableName = createTable(5);
        try (Table table = VisibilityLabelsWithDeletesTestBase.TEST_UTIL.getConnection().getTable(tableName)) {
            Put put1 = new Put(Bytes.toBytes("row1"));
            put1.addColumn(VisibilityLabelsWithDeletesTestBase.fam, VisibilityLabelsWithDeletesTestBase.qual, 123L, VisibilityLabelsWithDeletesTestBase.value);
            put1.setCellVisibility(new CellVisibility(VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL));
            Put put2 = new Put(Bytes.toBytes("row1"));
            put2.addColumn(VisibilityLabelsWithDeletesTestBase.fam, VisibilityLabelsWithDeletesTestBase.qual, 123L, VisibilityLabelsWithDeletesTestBase.value1);
            put2.setCellVisibility(new CellVisibility(VisibilityLabelsWithDeletesTestBase.SECRET));
            table.put(TestVisibilityLabelsWithDeletes.createList(put1, put2));
            Scan s = new Scan();
            s.readVersions(5);
            s.setAuthorizations(new Authorizations(VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL, VisibilityLabelsWithDeletesTestBase.SECRET));
            ResultScanner scanner = table.getScanner(s);
            Assert.assertEquals(1, scanner.next(3).length);
            scanner.close();
            PrivilegedExceptionAction<Void> actiona = new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws Exception {
                    try (Connection connection = ConnectionFactory.createConnection(VisibilityLabelsWithDeletesTestBase.conf);Table table = connection.getTable(tableName)) {
                        Delete d = new Delete(VisibilityLabelsWithDeletesTestBase.row1);
                        d.setCellVisibility(new CellVisibility(VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL));
                        d.addColumn(VisibilityLabelsWithDeletesTestBase.fam, VisibilityLabelsWithDeletesTestBase.qual, 123L);
                        table.delete(d);
                    }
                    try (Connection connection = ConnectionFactory.createConnection(VisibilityLabelsWithDeletesTestBase.conf);Table table = connection.getTable(tableName)) {
                        Delete d = new Delete(VisibilityLabelsWithDeletesTestBase.row1);
                        d.setCellVisibility(new CellVisibility(VisibilityLabelsWithDeletesTestBase.SECRET));
                        d.addColumn(VisibilityLabelsWithDeletesTestBase.fam, VisibilityLabelsWithDeletesTestBase.qual, 123L);
                        table.delete(d);
                    } catch (Throwable t) {
                        throw new IOException(t);
                    }
                    return null;
                }
            };
            VisibilityLabelsWithDeletesTestBase.SUPERUSER.runAs(actiona);
            s = new Scan();
            s.readVersions(5);
            s.setAuthorizations(new Authorizations(VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL));
            scanner = table.getScanner(s);
            Assert.assertEquals(0, scanner.next(3).length);
            scanner.close();
        }
    }

    @Test
    public void testVisibilityLabelsWithDeleteFamilyNoMatchingVisExpWithMultipleVersionsNoTimestamp() throws Exception {
        setAuths();
        final TableName tableName = TableName.valueOf(testName.getMethodName());
        try (Table table = doPuts(tableName)) {
            VisibilityLabelsWithDeletesTestBase.TEST_UTIL.getAdmin().flush(tableName);
            PrivilegedExceptionAction<Void> actiona = new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws Exception {
                    Delete d1 = new Delete(VisibilityLabelsWithDeletesTestBase.row1);
                    d1.setCellVisibility(new CellVisibility(VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL));
                    d1.addFamily(VisibilityLabelsWithDeletesTestBase.fam);
                    Delete d2 = new Delete(VisibilityLabelsWithDeletesTestBase.row1);
                    d2.setCellVisibility(new CellVisibility(VisibilityLabelsWithDeletesTestBase.SECRET));
                    d2.addFamily(VisibilityLabelsWithDeletesTestBase.fam);
                    Delete d3 = new Delete(VisibilityLabelsWithDeletesTestBase.row1);
                    d3.setCellVisibility(new CellVisibility((((((((("(" + (VisibilityLabelsWithDeletesTestBase.PRIVATE)) + "&") + (VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL)) + ")|(") + (VisibilityLabelsWithDeletesTestBase.SECRET)) + "&") + (VisibilityLabelsWithDeletesTestBase.TOPSECRET)) + ")")));
                    d3.addFamily(VisibilityLabelsWithDeletesTestBase.fam);
                    try (Connection connection = ConnectionFactory.createConnection(VisibilityLabelsWithDeletesTestBase.conf);Table table = connection.getTable(tableName)) {
                        table.delete(TestVisibilityLabelsWithDeletes.createList(d1, d2, d3));
                    } catch (Throwable t) {
                        throw new IOException(t);
                    }
                    return null;
                }
            };
            VisibilityLabelsWithDeletesTestBase.SUPERUSER.runAs(actiona);
            Scan s = new Scan();
            s.readVersions(5);
            s.setAuthorizations(new Authorizations(VisibilityLabelsWithDeletesTestBase.SECRET, VisibilityLabelsWithDeletesTestBase.PRIVATE, VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL, VisibilityLabelsWithDeletesTestBase.TOPSECRET));
            ResultScanner scanner = table.getScanner(s);
            Result[] next = scanner.next(3);
            Assert.assertTrue(((next.length) == 2));
            CellScanner cellScanner = next[0].cellScanner();
            cellScanner.advance();
            Cell current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row1, 0, TestVisibilityLabelsWithDeletes.row1.length));
            cellScanner = next[1].cellScanner();
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row2, 0, TestVisibilityLabelsWithDeletes.row2.length));
            scanner.close();
        }
    }

    @Test
    public void testDeleteFamilyAndDeleteColumnsWithAndWithoutVisibilityExp() throws Exception {
        setAuths();
        final TableName tableName = TableName.valueOf(testName.getMethodName());
        try (Table table = doPuts(tableName)) {
            VisibilityLabelsWithDeletesTestBase.TEST_UTIL.getAdmin().flush(tableName);
            PrivilegedExceptionAction<Void> actiona = new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws Exception {
                    Delete d1 = new Delete(VisibilityLabelsWithDeletesTestBase.row1);
                    d1.addFamily(VisibilityLabelsWithDeletesTestBase.fam);
                    Delete d2 = new Delete(VisibilityLabelsWithDeletesTestBase.row1);
                    d2.setCellVisibility(new CellVisibility((((VisibilityLabelsWithDeletesTestBase.SECRET) + "&") + (VisibilityLabelsWithDeletesTestBase.TOPSECRET))));
                    d2.addColumns(VisibilityLabelsWithDeletesTestBase.fam, VisibilityLabelsWithDeletesTestBase.qual);
                    try (Connection connection = ConnectionFactory.createConnection(VisibilityLabelsWithDeletesTestBase.conf);Table table = connection.getTable(tableName)) {
                        table.delete(TestVisibilityLabelsWithDeletes.createList(d1, d2));
                    } catch (Throwable t) {
                        throw new IOException(t);
                    }
                    return null;
                }
            };
            VisibilityLabelsWithDeletesTestBase.SUPERUSER.runAs(actiona);
            Scan s = new Scan();
            s.readVersions(5);
            s.setAuthorizations(new Authorizations(VisibilityLabelsWithDeletesTestBase.SECRET, VisibilityLabelsWithDeletesTestBase.PRIVATE, VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL, VisibilityLabelsWithDeletesTestBase.TOPSECRET));
            ResultScanner scanner = table.getScanner(s);
            Result[] next = scanner.next(3);
            Assert.assertTrue(((next.length) == 2));
            CellScanner cellScanner = next[0].cellScanner();
            cellScanner.advance();
            Cell current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row1, 0, TestVisibilityLabelsWithDeletes.row1.length));
            Assert.assertEquals(127L, current.getTimestamp());
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row1, 0, TestVisibilityLabelsWithDeletes.row1.length));
            Assert.assertEquals(126L, current.getTimestamp());
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row1, 0, TestVisibilityLabelsWithDeletes.row1.length));
            Assert.assertEquals(124L, current.getTimestamp());
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row1, 0, TestVisibilityLabelsWithDeletes.row1.length));
            Assert.assertEquals(123L, current.getTimestamp());
            cellScanner = next[1].cellScanner();
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row2, 0, TestVisibilityLabelsWithDeletes.row2.length));
            scanner.close();
        }
    }

    @Test
    public void testDeleteColumnWithSpecificTimeStampUsingMultipleVersionsUnMatchingVisExpression() throws Exception {
        setAuths();
        final TableName tableName = TableName.valueOf(testName.getMethodName());
        try (Table table = doPuts(tableName)) {
            VisibilityLabelsWithDeletesTestBase.TEST_UTIL.getAdmin().flush(tableName);
            PrivilegedExceptionAction<Void> actiona = new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws Exception {
                    try (Connection connection = ConnectionFactory.createConnection(VisibilityLabelsWithDeletesTestBase.conf);Table table = connection.getTable(tableName)) {
                        Delete d = new Delete(VisibilityLabelsWithDeletesTestBase.row1);
                        d.setCellVisibility(new CellVisibility((((((((("(" + (VisibilityLabelsWithDeletesTestBase.PRIVATE)) + "&") + (VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL)) + ")|(") + (VisibilityLabelsWithDeletesTestBase.SECRET)) + "&") + (VisibilityLabelsWithDeletesTestBase.TOPSECRET)) + ")")));
                        d.addColumn(VisibilityLabelsWithDeletesTestBase.fam, VisibilityLabelsWithDeletesTestBase.qual, 125L);
                        table.delete(d);
                    } catch (Throwable t) {
                        throw new IOException(t);
                    }
                    return null;
                }
            };
            VisibilityLabelsWithDeletesTestBase.SUPERUSER.runAs(actiona);
            VisibilityLabelsWithDeletesTestBase.TEST_UTIL.getAdmin().flush(tableName);
            Scan s = new Scan();
            s.readVersions(5);
            s.setAuthorizations(new Authorizations(VisibilityLabelsWithDeletesTestBase.SECRET, VisibilityLabelsWithDeletesTestBase.PRIVATE, VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL, VisibilityLabelsWithDeletesTestBase.TOPSECRET));
            ResultScanner scanner = table.getScanner(s);
            Result[] next = scanner.next(3);
            Assert.assertTrue(((next.length) == 2));
            CellScanner cellScanner = next[0].cellScanner();
            cellScanner.advance();
            Cell current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row1, 0, TestVisibilityLabelsWithDeletes.row1.length));
            Assert.assertEquals(127L, current.getTimestamp());
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row1, 0, TestVisibilityLabelsWithDeletes.row1.length));
            Assert.assertEquals(126L, current.getTimestamp());
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row1, 0, TestVisibilityLabelsWithDeletes.row1.length));
            Assert.assertEquals(125L, current.getTimestamp());
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row1, 0, TestVisibilityLabelsWithDeletes.row1.length));
            Assert.assertEquals(124L, current.getTimestamp());
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row1, 0, TestVisibilityLabelsWithDeletes.row1.length));
            Assert.assertEquals(123L, current.getTimestamp());
            cellScanner = next[1].cellScanner();
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row2, 0, TestVisibilityLabelsWithDeletes.row2.length));
        }
    }

    @Test
    public void testDeleteColumnWithLatestTimeStampUsingMultipleVersions() throws Exception {
        setAuths();
        final TableName tableName = TableName.valueOf(testName.getMethodName());
        try (Table table = doPuts(tableName)) {
            VisibilityLabelsWithDeletesTestBase.TEST_UTIL.getAdmin().flush(tableName);
            PrivilegedExceptionAction<Void> actiona = new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws Exception {
                    try (Connection connection = ConnectionFactory.createConnection(VisibilityLabelsWithDeletesTestBase.conf);Table table = connection.getTable(tableName)) {
                        Delete d = new Delete(VisibilityLabelsWithDeletesTestBase.row1);
                        d.setCellVisibility(new CellVisibility((((VisibilityLabelsWithDeletesTestBase.SECRET) + "&") + (VisibilityLabelsWithDeletesTestBase.TOPSECRET))));
                        d.addColumn(VisibilityLabelsWithDeletesTestBase.fam, VisibilityLabelsWithDeletesTestBase.qual);
                        table.delete(d);
                    } catch (Throwable t) {
                        throw new IOException(t);
                    }
                    return null;
                }
            };
            VisibilityLabelsWithDeletesTestBase.SUPERUSER.runAs(actiona);
            VisibilityLabelsWithDeletesTestBase.TEST_UTIL.getAdmin().flush(tableName);
            Scan s = new Scan();
            s.readVersions(5);
            s.setAuthorizations(new Authorizations(VisibilityLabelsWithDeletesTestBase.SECRET, VisibilityLabelsWithDeletesTestBase.PRIVATE, VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL, VisibilityLabelsWithDeletesTestBase.TOPSECRET));
            ResultScanner scanner = table.getScanner(s);
            Result[] next = scanner.next(3);
            Assert.assertTrue(((next.length) == 2));
            CellScanner cellScanner = next[0].cellScanner();
            cellScanner.advance();
            Cell current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row1, 0, TestVisibilityLabelsWithDeletes.row1.length));
            Assert.assertEquals(127L, current.getTimestamp());
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row1, 0, TestVisibilityLabelsWithDeletes.row1.length));
            Assert.assertEquals(126L, current.getTimestamp());
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row1, 0, TestVisibilityLabelsWithDeletes.row1.length));
            Assert.assertEquals(124L, current.getTimestamp());
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row1, 0, TestVisibilityLabelsWithDeletes.row1.length));
            Assert.assertEquals(123L, current.getTimestamp());
            cellScanner = next[1].cellScanner();
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row2, 0, TestVisibilityLabelsWithDeletes.row2.length));
        }
    }

    @Test
    public void testDeleteColumnWithLatestTimeStampWhenNoVersionMatches() throws Exception {
        setAuths();
        final TableName tableName = TableName.valueOf(testName.getMethodName());
        try (Table table = doPuts(tableName)) {
            VisibilityLabelsWithDeletesTestBase.TEST_UTIL.getAdmin().flush(tableName);
            Put put = new Put(Bytes.toBytes("row1"));
            put.addColumn(VisibilityLabelsWithDeletesTestBase.fam, VisibilityLabelsWithDeletesTestBase.qual, 128L, VisibilityLabelsWithDeletesTestBase.value);
            put.setCellVisibility(new CellVisibility(VisibilityLabelsWithDeletesTestBase.TOPSECRET));
            table.put(put);
            PrivilegedExceptionAction<Void> actiona = new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws Exception {
                    try (Connection connection = ConnectionFactory.createConnection(VisibilityLabelsWithDeletesTestBase.conf);Table table = connection.getTable(tableName)) {
                        Delete d = new Delete(VisibilityLabelsWithDeletesTestBase.row1);
                        d.setCellVisibility(new CellVisibility(VisibilityLabelsWithDeletesTestBase.SECRET));
                        d.addColumn(VisibilityLabelsWithDeletesTestBase.fam, VisibilityLabelsWithDeletesTestBase.qual);
                        table.delete(d);
                    } catch (Throwable t) {
                        throw new IOException(t);
                    }
                    return null;
                }
            };
            VisibilityLabelsWithDeletesTestBase.SUPERUSER.runAs(actiona);
            VisibilityLabelsWithDeletesTestBase.TEST_UTIL.getAdmin().flush(tableName);
            Scan s = new Scan();
            s.readVersions(5);
            s.setAuthorizations(new Authorizations(VisibilityLabelsWithDeletesTestBase.SECRET, VisibilityLabelsWithDeletesTestBase.PRIVATE, VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL, VisibilityLabelsWithDeletesTestBase.TOPSECRET));
            ResultScanner scanner = table.getScanner(s);
            Result[] next = scanner.next(3);
            Assert.assertTrue(((next.length) == 2));
            CellScanner cellScanner = next[0].cellScanner();
            cellScanner.advance();
            Cell current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row1, 0, TestVisibilityLabelsWithDeletes.row1.length));
            Assert.assertEquals(128L, current.getTimestamp());
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row1, 0, TestVisibilityLabelsWithDeletes.row1.length));
            Assert.assertEquals(127L, current.getTimestamp());
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row1, 0, TestVisibilityLabelsWithDeletes.row1.length));
            Assert.assertEquals(126L, current.getTimestamp());
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row1, 0, TestVisibilityLabelsWithDeletes.row1.length));
            Assert.assertEquals(125L, current.getTimestamp());
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row1, 0, TestVisibilityLabelsWithDeletes.row1.length));
            Assert.assertEquals(124L, current.getTimestamp());
            cellScanner = next[1].cellScanner();
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row2, 0, TestVisibilityLabelsWithDeletes.row2.length));
            put = new Put(Bytes.toBytes("row1"));
            put.addColumn(VisibilityLabelsWithDeletesTestBase.fam, VisibilityLabelsWithDeletesTestBase.qual, 129L, VisibilityLabelsWithDeletesTestBase.value);
            put.setCellVisibility(new CellVisibility(VisibilityLabelsWithDeletesTestBase.SECRET));
            table.put(put);
            VisibilityLabelsWithDeletesTestBase.TEST_UTIL.getAdmin().flush(tableName);
            s = new Scan();
            s.readVersions(5);
            s.setAuthorizations(new Authorizations(VisibilityLabelsWithDeletesTestBase.SECRET, VisibilityLabelsWithDeletesTestBase.PRIVATE, VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL, VisibilityLabelsWithDeletesTestBase.TOPSECRET));
            scanner = table.getScanner(s);
            next = scanner.next(3);
            Assert.assertTrue(((next.length) == 2));
            cellScanner = next[0].cellScanner();
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row1, 0, TestVisibilityLabelsWithDeletes.row1.length));
            Assert.assertEquals(129L, current.getTimestamp());
        }
    }

    @Test
    public void testDeleteColumnWithLatestTimeStampUsingMultipleVersionsAfterCompaction() throws Exception {
        setAuths();
        final TableName tableName = TableName.valueOf(testName.getMethodName());
        try (Table table = doPuts(tableName)) {
            VisibilityLabelsWithDeletesTestBase.TEST_UTIL.getAdmin().flush(tableName);
            PrivilegedExceptionAction<Void> actiona = new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws Exception {
                    try (Connection connection = ConnectionFactory.createConnection(VisibilityLabelsWithDeletesTestBase.conf);Table table = connection.getTable(tableName)) {
                        Delete d = new Delete(VisibilityLabelsWithDeletesTestBase.row1);
                        d.setCellVisibility(new CellVisibility((((VisibilityLabelsWithDeletesTestBase.SECRET) + "&") + (VisibilityLabelsWithDeletesTestBase.TOPSECRET))));
                        d.addColumn(VisibilityLabelsWithDeletesTestBase.fam, VisibilityLabelsWithDeletesTestBase.qual);
                        table.delete(d);
                    } catch (Throwable t) {
                        throw new IOException(t);
                    }
                    return null;
                }
            };
            VisibilityLabelsWithDeletesTestBase.SUPERUSER.runAs(actiona);
            VisibilityLabelsWithDeletesTestBase.TEST_UTIL.getAdmin().flush(tableName);
            Put put = new Put(Bytes.toBytes("row3"));
            put.addColumn(VisibilityLabelsWithDeletesTestBase.fam, VisibilityLabelsWithDeletesTestBase.qual, 127L, VisibilityLabelsWithDeletesTestBase.value);
            put.setCellVisibility(new CellVisibility((((VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL) + "&") + (VisibilityLabelsWithDeletesTestBase.PRIVATE))));
            table.put(put);
            VisibilityLabelsWithDeletesTestBase.TEST_UTIL.getAdmin().flush(tableName);
            VisibilityLabelsWithDeletesTestBase.TEST_UTIL.getAdmin().majorCompact(tableName);
            // Sleep to ensure compaction happens. Need to do it in a better way
            Thread.sleep(5000);
            Scan s = new Scan();
            s.readVersions(5);
            s.setAuthorizations(new Authorizations(VisibilityLabelsWithDeletesTestBase.SECRET, VisibilityLabelsWithDeletesTestBase.PRIVATE, VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL, VisibilityLabelsWithDeletesTestBase.TOPSECRET));
            ResultScanner scanner = table.getScanner(s);
            Result[] next = scanner.next(3);
            Assert.assertTrue(((next.length) == 3));
            CellScanner cellScanner = next[0].cellScanner();
            cellScanner.advance();
            Cell current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row1, 0, TestVisibilityLabelsWithDeletes.row1.length));
            Assert.assertEquals(127L, current.getTimestamp());
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row1, 0, TestVisibilityLabelsWithDeletes.row1.length));
            Assert.assertEquals(126L, current.getTimestamp());
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row1, 0, TestVisibilityLabelsWithDeletes.row1.length));
            Assert.assertEquals(124L, current.getTimestamp());
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row1, 0, TestVisibilityLabelsWithDeletes.row1.length));
            Assert.assertEquals(123L, current.getTimestamp());
            cellScanner = next[1].cellScanner();
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row2, 0, TestVisibilityLabelsWithDeletes.row2.length));
        }
    }

    @Test
    public void testDeleteFamilyLatestTimeStampWithMulipleVersions() throws Exception {
        setAuths();
        final TableName tableName = TableName.valueOf(testName.getMethodName());
        try (Table table = doPuts(tableName)) {
            VisibilityLabelsWithDeletesTestBase.TEST_UTIL.getAdmin().flush(tableName);
            PrivilegedExceptionAction<Void> actiona = new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws Exception {
                    try (Connection connection = ConnectionFactory.createConnection(VisibilityLabelsWithDeletesTestBase.conf);Table table = connection.getTable(tableName)) {
                        Delete d = new Delete(VisibilityLabelsWithDeletesTestBase.row1);
                        d.setCellVisibility(new CellVisibility((((VisibilityLabelsWithDeletesTestBase.SECRET) + "&") + (VisibilityLabelsWithDeletesTestBase.TOPSECRET))));
                        d.addFamily(VisibilityLabelsWithDeletesTestBase.fam);
                        table.delete(d);
                    } catch (Throwable t) {
                        throw new IOException(t);
                    }
                    return null;
                }
            };
            VisibilityLabelsWithDeletesTestBase.SUPERUSER.runAs(actiona);
            VisibilityLabelsWithDeletesTestBase.TEST_UTIL.getAdmin().flush(tableName);
            Scan s = new Scan();
            s.readVersions(5);
            s.setAuthorizations(new Authorizations(VisibilityLabelsWithDeletesTestBase.SECRET, VisibilityLabelsWithDeletesTestBase.PRIVATE, VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL, VisibilityLabelsWithDeletesTestBase.TOPSECRET));
            ResultScanner scanner = table.getScanner(s);
            Result[] next = scanner.next(3);
            Assert.assertTrue(((next.length) == 2));
            CellScanner cellScanner = next[0].cellScanner();
            cellScanner.advance();
            Cell current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row1, 0, TestVisibilityLabelsWithDeletes.row1.length));
            Assert.assertEquals(127L, current.getTimestamp());
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row1, 0, TestVisibilityLabelsWithDeletes.row1.length));
            Assert.assertEquals(126L, current.getTimestamp());
            cellScanner = next[1].cellScanner();
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row2, 0, TestVisibilityLabelsWithDeletes.row2.length));
        }
    }

    @Test
    public void testDeleteColumnswithMultipleColumnsWithMultipleVersions() throws Exception {
        setAuths();
        final TableName tableName = TableName.valueOf(testName.getMethodName());
        try (Table table = doPutsWithDiffCols(tableName)) {
            VisibilityLabelsWithDeletesTestBase.TEST_UTIL.getAdmin().flush(tableName);
            PrivilegedExceptionAction<Void> actiona = new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws Exception {
                    Delete d = new Delete(VisibilityLabelsWithDeletesTestBase.row1);
                    d.setCellVisibility(new CellVisibility((((VisibilityLabelsWithDeletesTestBase.SECRET) + "&") + (VisibilityLabelsWithDeletesTestBase.TOPSECRET))));
                    d.addColumns(VisibilityLabelsWithDeletesTestBase.fam, VisibilityLabelsWithDeletesTestBase.qual, 125L);
                    try (Connection connection = ConnectionFactory.createConnection(VisibilityLabelsWithDeletesTestBase.conf);Table table = connection.getTable(tableName)) {
                        table.delete(d);
                    } catch (Throwable t) {
                        throw new IOException(t);
                    }
                    return null;
                }
            };
            VisibilityLabelsWithDeletesTestBase.SUPERUSER.runAs(actiona);
            VisibilityLabelsWithDeletesTestBase.TEST_UTIL.getAdmin().flush(tableName);
            Scan s = new Scan();
            s.readVersions(5);
            s.setAuthorizations(new Authorizations(VisibilityLabelsWithDeletesTestBase.SECRET, VisibilityLabelsWithDeletesTestBase.PRIVATE, VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL, VisibilityLabelsWithDeletesTestBase.TOPSECRET));
            ResultScanner scanner = table.getScanner(s);
            Result[] next = scanner.next(3);
            Assert.assertTrue(((next.length) == 1));
            CellScanner cellScanner = next[0].cellScanner();
            cellScanner.advance();
            Cell current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row1, 0, TestVisibilityLabelsWithDeletes.row1.length));
            Assert.assertEquals(124L, current.getTimestamp());
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row1, 0, TestVisibilityLabelsWithDeletes.row1.length));
            Assert.assertEquals(123L, current.getTimestamp());
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row1, 0, TestVisibilityLabelsWithDeletes.row1.length));
            Assert.assertTrue(Bytes.equals(current.getQualifierArray(), current.getQualifierOffset(), current.getQualifierLength(), VisibilityLabelsWithDeletesTestBase.qual1, 0, TestVisibilityLabelsWithDeletes.qual1.length));
            Assert.assertEquals(126L, current.getTimestamp());
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row1, 0, TestVisibilityLabelsWithDeletes.row1.length));
            Assert.assertEquals(127L, current.getTimestamp());
            Assert.assertTrue(Bytes.equals(current.getQualifierArray(), current.getQualifierOffset(), current.getQualifierLength(), VisibilityLabelsWithDeletesTestBase.qual2, 0, TestVisibilityLabelsWithDeletes.qual2.length));
        }
    }

    @Test
    public void testDeleteColumnsWithDiffColsAndTags() throws Exception {
        TableName tableName = createTable(5);
        try (Table table = VisibilityLabelsWithDeletesTestBase.TEST_UTIL.getConnection().getTable(tableName)) {
            Put put = new Put(Bytes.toBytes("row1"));
            put.addColumn(VisibilityLabelsWithDeletesTestBase.fam, VisibilityLabelsWithDeletesTestBase.qual1, 125L, VisibilityLabelsWithDeletesTestBase.value);
            put.setCellVisibility(new CellVisibility(VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL));
            table.put(put);
            put = new Put(Bytes.toBytes("row1"));
            put.addColumn(VisibilityLabelsWithDeletesTestBase.fam, VisibilityLabelsWithDeletesTestBase.qual1, 126L, VisibilityLabelsWithDeletesTestBase.value);
            put.setCellVisibility(new CellVisibility(VisibilityLabelsWithDeletesTestBase.SECRET));
            table.put(put);
            VisibilityLabelsWithDeletesTestBase.TEST_UTIL.getAdmin().flush(tableName);
            PrivilegedExceptionAction<Void> actiona = new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws Exception {
                    Delete d1 = new Delete(VisibilityLabelsWithDeletesTestBase.row1);
                    d1.setCellVisibility(new CellVisibility(VisibilityLabelsWithDeletesTestBase.SECRET));
                    d1.addColumns(VisibilityLabelsWithDeletesTestBase.fam, VisibilityLabelsWithDeletesTestBase.qual, 126L);
                    Delete d2 = new Delete(VisibilityLabelsWithDeletesTestBase.row1);
                    d2.setCellVisibility(new CellVisibility(VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL));
                    d2.addColumns(VisibilityLabelsWithDeletesTestBase.fam, VisibilityLabelsWithDeletesTestBase.qual1, 125L);
                    try (Connection connection = ConnectionFactory.createConnection(VisibilityLabelsWithDeletesTestBase.conf);Table table = connection.getTable(tableName)) {
                        table.delete(TestVisibilityLabelsWithDeletes.createList(d1, d2));
                    } catch (Throwable t) {
                        throw new IOException(t);
                    }
                    return null;
                }
            };
            VisibilityLabelsWithDeletesTestBase.SUPERUSER.runAs(actiona);
            Scan s = new Scan();
            s.readVersions(5);
            s.setAuthorizations(new Authorizations(VisibilityLabelsWithDeletesTestBase.SECRET, VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL));
            ResultScanner scanner = table.getScanner(s);
            Result[] next = scanner.next(3);
            Assert.assertEquals(1, next.length);
        }
    }

    @Test
    public void testDeleteColumnsWithDiffColsAndTags1() throws Exception {
        TableName tableName = createTable(5);
        try (Table table = VisibilityLabelsWithDeletesTestBase.TEST_UTIL.getConnection().getTable(tableName)) {
            Put put = new Put(Bytes.toBytes("row1"));
            put.addColumn(VisibilityLabelsWithDeletesTestBase.fam, VisibilityLabelsWithDeletesTestBase.qual1, 125L, VisibilityLabelsWithDeletesTestBase.value);
            put.setCellVisibility(new CellVisibility(VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL));
            table.put(put);
            put = new Put(Bytes.toBytes("row1"));
            put.addColumn(VisibilityLabelsWithDeletesTestBase.fam, VisibilityLabelsWithDeletesTestBase.qual1, 126L, VisibilityLabelsWithDeletesTestBase.value);
            put.setCellVisibility(new CellVisibility(VisibilityLabelsWithDeletesTestBase.SECRET));
            table.put(put);
            VisibilityLabelsWithDeletesTestBase.TEST_UTIL.getAdmin().flush(tableName);
            PrivilegedExceptionAction<Void> actiona = new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws Exception {
                    Delete d1 = new Delete(VisibilityLabelsWithDeletesTestBase.row1);
                    d1.setCellVisibility(new CellVisibility(VisibilityLabelsWithDeletesTestBase.SECRET));
                    d1.addColumns(VisibilityLabelsWithDeletesTestBase.fam, VisibilityLabelsWithDeletesTestBase.qual, 126L);
                    Delete d2 = new Delete(VisibilityLabelsWithDeletesTestBase.row1);
                    d2.setCellVisibility(new CellVisibility(VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL));
                    d2.addColumns(VisibilityLabelsWithDeletesTestBase.fam, VisibilityLabelsWithDeletesTestBase.qual1, 126L);
                    try (Connection connection = ConnectionFactory.createConnection(VisibilityLabelsWithDeletesTestBase.conf);Table table = connection.getTable(tableName)) {
                        table.delete(TestVisibilityLabelsWithDeletes.createList(d1, d2));
                    } catch (Throwable t) {
                        throw new IOException(t);
                    }
                    return null;
                }
            };
            VisibilityLabelsWithDeletesTestBase.SUPERUSER.runAs(actiona);
            Scan s = new Scan();
            s.readVersions(5);
            s.setAuthorizations(new Authorizations(VisibilityLabelsWithDeletesTestBase.SECRET, VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL));
            ResultScanner scanner = table.getScanner(s);
            Result[] next = scanner.next(3);
            Assert.assertEquals(1, next.length);
        }
    }

    @Test
    public void testDeleteFamilyWithoutCellVisibilityWithMulipleVersions() throws Exception {
        setAuths();
        final TableName tableName = TableName.valueOf(testName.getMethodName());
        try (Table table = doPutsWithoutVisibility(tableName)) {
            VisibilityLabelsWithDeletesTestBase.TEST_UTIL.getAdmin().flush(tableName);
            PrivilegedExceptionAction<Void> actiona = new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws Exception {
                    try (Connection connection = ConnectionFactory.createConnection(VisibilityLabelsWithDeletesTestBase.conf);Table table = connection.getTable(tableName)) {
                        Delete d = new Delete(VisibilityLabelsWithDeletesTestBase.row1);
                        d.addFamily(VisibilityLabelsWithDeletesTestBase.fam);
                        table.delete(d);
                    } catch (Throwable t) {
                        throw new IOException(t);
                    }
                    return null;
                }
            };
            VisibilityLabelsWithDeletesTestBase.SUPERUSER.runAs(actiona);
            VisibilityLabelsWithDeletesTestBase.TEST_UTIL.getAdmin().flush(tableName);
            Scan s = new Scan();
            s.readVersions(5);
            s.setAuthorizations(new Authorizations(VisibilityLabelsWithDeletesTestBase.SECRET, VisibilityLabelsWithDeletesTestBase.PRIVATE, VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL, VisibilityLabelsWithDeletesTestBase.TOPSECRET));
            ResultScanner scanner = table.getScanner(s);
            Result[] next = scanner.next(3);
            Assert.assertTrue(((next.length) == 1));
            // All cells wrt row1 should be deleted as we are not passing the Cell Visibility
            CellScanner cellScanner = next[0].cellScanner();
            cellScanner.advance();
            Cell current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row2, 0, TestVisibilityLabelsWithDeletes.row2.length));
        }
    }

    @Test
    public void testDeleteFamilyLatestTimeStampWithMulipleVersionsWithoutCellVisibilityInPuts() throws Exception {
        setAuths();
        final TableName tableName = TableName.valueOf(testName.getMethodName());
        try (Table table = doPutsWithoutVisibility(tableName)) {
            PrivilegedExceptionAction<Void> actiona = new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws Exception {
                    try (Connection connection = ConnectionFactory.createConnection(VisibilityLabelsWithDeletesTestBase.conf);Table table = connection.getTable(tableName)) {
                        Delete d = new Delete(VisibilityLabelsWithDeletesTestBase.row1);
                        d.setCellVisibility(new CellVisibility((((VisibilityLabelsWithDeletesTestBase.SECRET) + "&") + (VisibilityLabelsWithDeletesTestBase.TOPSECRET))));
                        d.addFamily(VisibilityLabelsWithDeletesTestBase.fam);
                        table.delete(d);
                    } catch (Throwable t) {
                        throw new IOException(t);
                    }
                    return null;
                }
            };
            VisibilityLabelsWithDeletesTestBase.SUPERUSER.runAs(actiona);
            VisibilityLabelsWithDeletesTestBase.TEST_UTIL.getAdmin().flush(tableName);
            Scan s = new Scan();
            s.readVersions(5);
            s.setAuthorizations(new Authorizations(VisibilityLabelsWithDeletesTestBase.SECRET, VisibilityLabelsWithDeletesTestBase.PRIVATE, VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL, VisibilityLabelsWithDeletesTestBase.TOPSECRET));
            ResultScanner scanner = table.getScanner(s);
            Result[] next = scanner.next(3);
            Assert.assertTrue(((next.length) == 2));
            CellScanner cellScanner = next[0].cellScanner();
            cellScanner.advance();
            Cell current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row1, 0, TestVisibilityLabelsWithDeletes.row1.length));
            Assert.assertEquals(127L, current.getTimestamp());
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row1, 0, TestVisibilityLabelsWithDeletes.row1.length));
            Assert.assertEquals(126L, current.getTimestamp());
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row1, 0, TestVisibilityLabelsWithDeletes.row1.length));
            Assert.assertEquals(125L, current.getTimestamp());
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row1, 0, TestVisibilityLabelsWithDeletes.row1.length));
            Assert.assertEquals(124L, current.getTimestamp());
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row1, 0, TestVisibilityLabelsWithDeletes.row1.length));
            Assert.assertEquals(123L, current.getTimestamp());
            cellScanner = next[1].cellScanner();
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row2, 0, TestVisibilityLabelsWithDeletes.row2.length));
        }
    }

    @Test
    public void testDeleteFamilySpecificTimeStampWithMulipleVersions() throws Exception {
        setAuths();
        final TableName tableName = TableName.valueOf(testName.getMethodName());
        try (Table table = doPuts(tableName)) {
            VisibilityLabelsWithDeletesTestBase.TEST_UTIL.getAdmin().flush(tableName);
            PrivilegedExceptionAction<Void> actiona = new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws Exception {
                    try (Connection connection = ConnectionFactory.createConnection(VisibilityLabelsWithDeletesTestBase.conf);Table table = connection.getTable(tableName)) {
                        Delete d = new Delete(VisibilityLabelsWithDeletesTestBase.row1);
                        d.setCellVisibility(new CellVisibility((((((((("(" + (VisibilityLabelsWithDeletesTestBase.PRIVATE)) + "&") + (VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL)) + ")|(") + (VisibilityLabelsWithDeletesTestBase.SECRET)) + "&") + (VisibilityLabelsWithDeletesTestBase.TOPSECRET)) + ")")));
                        d.addFamily(VisibilityLabelsWithDeletesTestBase.fam, 126L);
                        table.delete(d);
                    } catch (Throwable t) {
                        throw new IOException(t);
                    }
                    return null;
                }
            };
            VisibilityLabelsWithDeletesTestBase.SUPERUSER.runAs(actiona);
            VisibilityLabelsWithDeletesTestBase.TEST_UTIL.getAdmin().flush(tableName);
            Scan s = new Scan();
            s.readVersions(5);
            s.setAuthorizations(new Authorizations(VisibilityLabelsWithDeletesTestBase.SECRET, VisibilityLabelsWithDeletesTestBase.PRIVATE, VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL, VisibilityLabelsWithDeletesTestBase.TOPSECRET));
            ResultScanner scanner = table.getScanner(s);
            Result[] next = scanner.next(6);
            Assert.assertTrue(((next.length) == 2));
            CellScanner cellScanner = next[0].cellScanner();
            cellScanner.advance();
            Cell current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row1, 0, TestVisibilityLabelsWithDeletes.row1.length));
            Assert.assertEquals(127L, current.getTimestamp());
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row1, 0, TestVisibilityLabelsWithDeletes.row1.length));
            Assert.assertEquals(125L, current.getTimestamp());
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row1, 0, TestVisibilityLabelsWithDeletes.row1.length));
            Assert.assertEquals(123L, current.getTimestamp());
            cellScanner = next[1].cellScanner();
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row2, 0, TestVisibilityLabelsWithDeletes.row2.length));
        }
    }

    @Test
    public void testScanAfterCompaction() throws Exception {
        setAuths();
        final TableName tableName = TableName.valueOf(testName.getMethodName());
        try (Table table = doPuts(tableName)) {
            VisibilityLabelsWithDeletesTestBase.TEST_UTIL.getAdmin().flush(tableName);
            PrivilegedExceptionAction<Void> actiona = new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws Exception {
                    try (Connection connection = ConnectionFactory.createConnection(VisibilityLabelsWithDeletesTestBase.conf);Table table = connection.getTable(tableName)) {
                        Delete d = new Delete(VisibilityLabelsWithDeletesTestBase.row1);
                        d.setCellVisibility(new CellVisibility((((((((("(" + (VisibilityLabelsWithDeletesTestBase.PRIVATE)) + "&") + (VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL)) + ")|(") + (VisibilityLabelsWithDeletesTestBase.SECRET)) + "&") + (VisibilityLabelsWithDeletesTestBase.TOPSECRET)) + ")")));
                        d.addFamily(VisibilityLabelsWithDeletesTestBase.fam, 126L);
                        table.delete(d);
                    } catch (Throwable t) {
                        throw new IOException(t);
                    }
                    return null;
                }
            };
            VisibilityLabelsWithDeletesTestBase.SUPERUSER.runAs(actiona);
            VisibilityLabelsWithDeletesTestBase.TEST_UTIL.getAdmin().flush(tableName);
            Put put = new Put(Bytes.toBytes("row3"));
            put.addColumn(VisibilityLabelsWithDeletesTestBase.fam, VisibilityLabelsWithDeletesTestBase.qual, 127L, VisibilityLabelsWithDeletesTestBase.value);
            put.setCellVisibility(new CellVisibility((((VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL) + "&") + (VisibilityLabelsWithDeletesTestBase.PRIVATE))));
            table.put(put);
            VisibilityLabelsWithDeletesTestBase.TEST_UTIL.getAdmin().flush(tableName);
            VisibilityLabelsWithDeletesTestBase.TEST_UTIL.getAdmin().compact(tableName);
            Thread.sleep(5000);
            // Sleep to ensure compaction happens. Need to do it in a better way
            Scan s = new Scan();
            s.readVersions(5);
            s.setAuthorizations(new Authorizations(VisibilityLabelsWithDeletesTestBase.SECRET, VisibilityLabelsWithDeletesTestBase.PRIVATE, VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL, VisibilityLabelsWithDeletesTestBase.TOPSECRET));
            ResultScanner scanner = table.getScanner(s);
            Result[] next = scanner.next(3);
            Assert.assertTrue(((next.length) == 3));
            CellScanner cellScanner = next[0].cellScanner();
            cellScanner.advance();
            Cell current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row1, 0, TestVisibilityLabelsWithDeletes.row1.length));
            Assert.assertEquals(127L, current.getTimestamp());
            cellScanner = next[1].cellScanner();
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row2, 0, TestVisibilityLabelsWithDeletes.row2.length));
        }
    }

    @Test
    public void testDeleteFamilySpecificTimeStampWithMulipleVersionsDoneTwice() throws Exception {
        setAuths();
        final TableName tableName = TableName.valueOf(testName.getMethodName());
        // Do not flush here.
        try (Table table = doPuts(tableName)) {
            PrivilegedExceptionAction<Void> actiona = new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws Exception {
                    try (Connection connection = ConnectionFactory.createConnection(VisibilityLabelsWithDeletesTestBase.conf);Table table = connection.getTable(tableName)) {
                        Delete d = new Delete(VisibilityLabelsWithDeletesTestBase.row1);
                        d.setCellVisibility(new CellVisibility((((((((("(" + (VisibilityLabelsWithDeletesTestBase.PRIVATE)) + "&") + (VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL)) + ")|(") + (VisibilityLabelsWithDeletesTestBase.TOPSECRET)) + "&") + (VisibilityLabelsWithDeletesTestBase.SECRET)) + ")")));
                        d.addFamily(VisibilityLabelsWithDeletesTestBase.fam, 125L);
                        table.delete(d);
                    } catch (Throwable t) {
                        throw new IOException(t);
                    }
                    return null;
                }
            };
            VisibilityLabelsWithDeletesTestBase.SUPERUSER.runAs(actiona);
            Scan s = new Scan();
            s.readVersions(5);
            s.setAuthorizations(new Authorizations(VisibilityLabelsWithDeletesTestBase.SECRET, VisibilityLabelsWithDeletesTestBase.PRIVATE, VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL, VisibilityLabelsWithDeletesTestBase.TOPSECRET));
            ResultScanner scanner = table.getScanner(s);
            Result[] next = scanner.next(3);
            Assert.assertTrue(((next.length) == 2));
            CellScanner cellScanner = next[0].cellScanner();
            cellScanner.advance();
            Cell current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row1, 0, TestVisibilityLabelsWithDeletes.row1.length));
            Assert.assertEquals(127L, current.getTimestamp());
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row1, 0, TestVisibilityLabelsWithDeletes.row1.length));
            Assert.assertEquals(126L, current.getTimestamp());
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row1, 0, TestVisibilityLabelsWithDeletes.row1.length));
            Assert.assertEquals(125L, current.getTimestamp());
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row1, 0, TestVisibilityLabelsWithDeletes.row1.length));
            Assert.assertEquals(123L, current.getTimestamp());
            cellScanner = next[1].cellScanner();
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row2, 0, TestVisibilityLabelsWithDeletes.row2.length));
            // Issue 2nd delete
            actiona = new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws Exception {
                    try (Connection connection = ConnectionFactory.createConnection(VisibilityLabelsWithDeletesTestBase.conf);Table table = connection.getTable(tableName)) {
                        Delete d = new Delete(VisibilityLabelsWithDeletesTestBase.row1);
                        d.setCellVisibility(new CellVisibility((((((((("(" + (VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL)) + "&") + (VisibilityLabelsWithDeletesTestBase.PRIVATE)) + ")|(") + (VisibilityLabelsWithDeletesTestBase.TOPSECRET)) + "&") + (VisibilityLabelsWithDeletesTestBase.SECRET)) + ")")));
                        d.addFamily(VisibilityLabelsWithDeletesTestBase.fam, 127L);
                        table.delete(d);
                    } catch (Throwable t) {
                        throw new IOException(t);
                    }
                    return null;
                }
            };
            VisibilityLabelsWithDeletesTestBase.SUPERUSER.runAs(actiona);
            s = new Scan();
            s.readVersions(5);
            s.setAuthorizations(new Authorizations(VisibilityLabelsWithDeletesTestBase.SECRET, VisibilityLabelsWithDeletesTestBase.PRIVATE, VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL, VisibilityLabelsWithDeletesTestBase.TOPSECRET));
            scanner = table.getScanner(s);
            next = scanner.next(3);
            Assert.assertTrue(((next.length) == 2));
            cellScanner = next[0].cellScanner();
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row1, 0, TestVisibilityLabelsWithDeletes.row1.length));
            Assert.assertEquals(125L, current.getTimestamp());
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row1, 0, TestVisibilityLabelsWithDeletes.row1.length));
            Assert.assertEquals(123L, current.getTimestamp());
            cellScanner = next[1].cellScanner();
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row2, 0, TestVisibilityLabelsWithDeletes.row2.length));
            Assert.assertEquals(127L, current.getTimestamp());
        }
    }

    @Test
    public void testMultipleDeleteFamilyVersionWithDiffLabels() throws Exception {
        PrivilegedExceptionAction<VisibilityLabelsResponse> action = new PrivilegedExceptionAction<VisibilityLabelsResponse>() {
            @Override
            public VisibilityLabelsResponse run() throws Exception {
                try (Connection conn = ConnectionFactory.createConnection(VisibilityLabelsWithDeletesTestBase.conf)) {
                    return VisibilityClient.setAuths(conn, new String[]{ VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL, VisibilityLabelsWithDeletesTestBase.PRIVATE, VisibilityLabelsWithDeletesTestBase.SECRET }, VisibilityLabelsWithDeletesTestBase.SUPERUSER.getShortName());
                } catch (Throwable e) {
                }
                return null;
            }
        };
        VisibilityLabelsWithDeletesTestBase.SUPERUSER.runAs(action);
        final TableName tableName = TableName.valueOf(testName.getMethodName());
        try (Table table = doPuts(tableName)) {
            PrivilegedExceptionAction<Void> actiona = new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws Exception {
                    try (Connection connection = ConnectionFactory.createConnection(VisibilityLabelsWithDeletesTestBase.conf);Table table = connection.getTable(tableName)) {
                        Delete d = new Delete(VisibilityLabelsWithDeletesTestBase.row1);
                        d.setCellVisibility(new CellVisibility(VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL));
                        d.addFamilyVersion(VisibilityLabelsWithDeletesTestBase.fam, 123L);
                        table.delete(d);
                        d = new Delete(VisibilityLabelsWithDeletesTestBase.row1);
                        d.setCellVisibility(new CellVisibility((((VisibilityLabelsWithDeletesTestBase.SECRET) + "&") + (VisibilityLabelsWithDeletesTestBase.TOPSECRET))));
                        d.addFamilyVersion(VisibilityLabelsWithDeletesTestBase.fam, 125L);
                        table.delete(d);
                    } catch (Throwable t) {
                        throw new IOException(t);
                    }
                    return null;
                }
            };
            VisibilityLabelsWithDeletesTestBase.SUPERUSER.runAs(actiona);
            VisibilityLabelsWithDeletesTestBase.TEST_UTIL.getAdmin().flush(tableName);
            Scan s = new Scan();
            s.readVersions(5);
            s.setAuthorizations(new Authorizations(VisibilityLabelsWithDeletesTestBase.SECRET, VisibilityLabelsWithDeletesTestBase.PRIVATE, VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL, VisibilityLabelsWithDeletesTestBase.TOPSECRET));
            ResultScanner scanner = table.getScanner(s);
            Result[] next = scanner.next(5);
            Assert.assertTrue(((next.length) == 2));
            CellScanner cellScanner = next[0].cellScanner();
            cellScanner.advance();
            Cell current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row1, 0, TestVisibilityLabelsWithDeletes.row1.length));
            Assert.assertEquals(127L, current.getTimestamp());
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row1, 0, TestVisibilityLabelsWithDeletes.row1.length));
            Assert.assertEquals(126L, current.getTimestamp());
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row1, 0, TestVisibilityLabelsWithDeletes.row1.length));
            Assert.assertEquals(124L, current.getTimestamp());
        }
    }

    @Test
    public void testSpecificDeletesFollowedByDeleteFamily() throws Exception {
        setAuths();
        final TableName tableName = TableName.valueOf(testName.getMethodName());
        try (Table table = doPuts(tableName)) {
            PrivilegedExceptionAction<Void> actiona = new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws Exception {
                    try (Connection connection = ConnectionFactory.createConnection(VisibilityLabelsWithDeletesTestBase.conf);Table table = connection.getTable(tableName)) {
                        Delete d = new Delete(VisibilityLabelsWithDeletesTestBase.row1);
                        d.setCellVisibility(new CellVisibility((((((((("(" + (VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL)) + "&") + (VisibilityLabelsWithDeletesTestBase.PRIVATE)) + ")|(") + (VisibilityLabelsWithDeletesTestBase.TOPSECRET)) + "&") + (VisibilityLabelsWithDeletesTestBase.SECRET)) + ")")));
                        d.addColumn(VisibilityLabelsWithDeletesTestBase.fam, VisibilityLabelsWithDeletesTestBase.qual, 126L);
                        table.delete(d);
                        d = new Delete(VisibilityLabelsWithDeletesTestBase.row1);
                        d.setCellVisibility(new CellVisibility((((VisibilityLabelsWithDeletesTestBase.SECRET) + "&") + (VisibilityLabelsWithDeletesTestBase.TOPSECRET))));
                        d.addFamilyVersion(VisibilityLabelsWithDeletesTestBase.fam, 125L);
                        table.delete(d);
                    } catch (Throwable t) {
                        throw new IOException(t);
                    }
                    return null;
                }
            };
            VisibilityLabelsWithDeletesTestBase.SUPERUSER.runAs(actiona);
            VisibilityLabelsWithDeletesTestBase.TEST_UTIL.getAdmin().flush(tableName);
            Scan s = new Scan();
            s.readVersions(5);
            s.setAuthorizations(new Authorizations(VisibilityLabelsWithDeletesTestBase.SECRET, VisibilityLabelsWithDeletesTestBase.PRIVATE, VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL, VisibilityLabelsWithDeletesTestBase.TOPSECRET));
            ResultScanner scanner = table.getScanner(s);
            Result[] next = scanner.next(5);
            Assert.assertTrue(((next.length) == 2));
            CellScanner cellScanner = next[0].cellScanner();
            cellScanner.advance();
            Cell current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row1, 0, TestVisibilityLabelsWithDeletes.row1.length));
            Assert.assertEquals(127L, current.getTimestamp());
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row1, 0, TestVisibilityLabelsWithDeletes.row1.length));
            Assert.assertEquals(124L, current.getTimestamp());
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row1, 0, TestVisibilityLabelsWithDeletes.row1.length));
            Assert.assertEquals(123L, current.getTimestamp());
            // Issue 2nd delete
            actiona = new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws Exception {
                    try (Connection connection = ConnectionFactory.createConnection(VisibilityLabelsWithDeletesTestBase.conf);Table table = connection.getTable(tableName)) {
                        Delete d = new Delete(VisibilityLabelsWithDeletesTestBase.row1);
                        d.setCellVisibility(new CellVisibility(VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL));
                        d.addFamily(VisibilityLabelsWithDeletesTestBase.fam);
                        table.delete(d);
                    } catch (Throwable t) {
                        throw new IOException(t);
                    }
                    return null;
                }
            };
            VisibilityLabelsWithDeletesTestBase.SUPERUSER.runAs(actiona);
            s = new Scan();
            s.readVersions(5);
            s.setAuthorizations(new Authorizations(VisibilityLabelsWithDeletesTestBase.SECRET, VisibilityLabelsWithDeletesTestBase.PRIVATE, VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL, VisibilityLabelsWithDeletesTestBase.TOPSECRET));
            scanner = table.getScanner(s);
            next = scanner.next(5);
            Assert.assertTrue(((next.length) == 2));
            cellScanner = next[0].cellScanner();
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row1, 0, TestVisibilityLabelsWithDeletes.row1.length));
            Assert.assertEquals(127L, current.getTimestamp());
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row1, 0, TestVisibilityLabelsWithDeletes.row1.length));
            Assert.assertEquals(124L, current.getTimestamp());
        }
    }

    @Test
    public void testSpecificDeletesFollowedByDeleteFamily1() throws Exception {
        PrivilegedExceptionAction<VisibilityLabelsResponse> action = new PrivilegedExceptionAction<VisibilityLabelsResponse>() {
            @Override
            public VisibilityLabelsResponse run() throws Exception {
                try (Connection conn = ConnectionFactory.createConnection(VisibilityLabelsWithDeletesTestBase.conf)) {
                    return VisibilityClient.setAuths(conn, new String[]{ VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL, VisibilityLabelsWithDeletesTestBase.PRIVATE, VisibilityLabelsWithDeletesTestBase.SECRET }, VisibilityLabelsWithDeletesTestBase.SUPERUSER.getShortName());
                } catch (Throwable e) {
                }
                return null;
            }
        };
        VisibilityLabelsWithDeletesTestBase.SUPERUSER.runAs(action);
        final TableName tableName = TableName.valueOf(testName.getMethodName());
        try (Table table = doPuts(tableName)) {
            PrivilegedExceptionAction<Void> actiona = new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws Exception {
                    try (Connection connection = ConnectionFactory.createConnection(VisibilityLabelsWithDeletesTestBase.conf);Table table = connection.getTable(tableName)) {
                        Delete d = new Delete(VisibilityLabelsWithDeletesTestBase.row1);
                        d.setCellVisibility(new CellVisibility((((((((("(" + (VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL)) + "&") + (VisibilityLabelsWithDeletesTestBase.PRIVATE)) + ")|(") + (VisibilityLabelsWithDeletesTestBase.TOPSECRET)) + "&") + (VisibilityLabelsWithDeletesTestBase.SECRET)) + ")")));
                        d.addColumn(VisibilityLabelsWithDeletesTestBase.fam, VisibilityLabelsWithDeletesTestBase.qual);
                        table.delete(d);
                        d = new Delete(VisibilityLabelsWithDeletesTestBase.row1);
                        d.setCellVisibility(new CellVisibility((((VisibilityLabelsWithDeletesTestBase.SECRET) + "&") + (VisibilityLabelsWithDeletesTestBase.TOPSECRET))));
                        d.addFamilyVersion(VisibilityLabelsWithDeletesTestBase.fam, 125L);
                        table.delete(d);
                    } catch (Throwable t) {
                        throw new IOException(t);
                    }
                    return null;
                }
            };
            VisibilityLabelsWithDeletesTestBase.SUPERUSER.runAs(actiona);
            VisibilityLabelsWithDeletesTestBase.TEST_UTIL.getAdmin().flush(tableName);
            Scan s = new Scan();
            s.readVersions(5);
            s.setAuthorizations(new Authorizations(VisibilityLabelsWithDeletesTestBase.SECRET, VisibilityLabelsWithDeletesTestBase.PRIVATE, VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL, VisibilityLabelsWithDeletesTestBase.TOPSECRET));
            ResultScanner scanner = table.getScanner(s);
            Result[] next = scanner.next(5);
            Assert.assertTrue(((next.length) == 2));
            CellScanner cellScanner = next[0].cellScanner();
            cellScanner.advance();
            Cell current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row1, 0, TestVisibilityLabelsWithDeletes.row1.length));
            Assert.assertEquals(126L, current.getTimestamp());
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row1, 0, TestVisibilityLabelsWithDeletes.row1.length));
            Assert.assertEquals(124L, current.getTimestamp());
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row1, 0, TestVisibilityLabelsWithDeletes.row1.length));
            Assert.assertEquals(123L, current.getTimestamp());
            // Issue 2nd delete
            actiona = new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws Exception {
                    try (Connection connection = ConnectionFactory.createConnection(VisibilityLabelsWithDeletesTestBase.conf);Table table = connection.getTable(tableName)) {
                        Delete d = new Delete(VisibilityLabelsWithDeletesTestBase.row1);
                        d.setCellVisibility(new CellVisibility(VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL));
                        d.addFamily(VisibilityLabelsWithDeletesTestBase.fam);
                        table.delete(d);
                    } catch (Throwable t) {
                        throw new IOException(t);
                    }
                    return null;
                }
            };
            VisibilityLabelsWithDeletesTestBase.SUPERUSER.runAs(actiona);
            s = new Scan();
            s.readVersions(5);
            s.setAuthorizations(new Authorizations(VisibilityLabelsWithDeletesTestBase.SECRET, VisibilityLabelsWithDeletesTestBase.PRIVATE, VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL, VisibilityLabelsWithDeletesTestBase.TOPSECRET));
            scanner = table.getScanner(s);
            next = scanner.next(5);
            Assert.assertTrue(((next.length) == 2));
            cellScanner = next[0].cellScanner();
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row1, 0, TestVisibilityLabelsWithDeletes.row1.length));
            Assert.assertEquals(126L, current.getTimestamp());
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row1, 0, TestVisibilityLabelsWithDeletes.row1.length));
            Assert.assertEquals(124L, current.getTimestamp());
        }
    }

    @Test
    public void testDeleteColumnSpecificTimeStampWithMulipleVersionsDoneTwice() throws Exception {
        setAuths();
        final TableName tableName = TableName.valueOf(testName.getMethodName());
        try (Table table = doPuts(tableName)) {
            PrivilegedExceptionAction<Void> actiona = new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws Exception {
                    try (Connection connection = ConnectionFactory.createConnection(VisibilityLabelsWithDeletesTestBase.conf);Table table = connection.getTable(tableName)) {
                        Delete d = new Delete(VisibilityLabelsWithDeletesTestBase.row1);
                        d.setCellVisibility(new CellVisibility((((VisibilityLabelsWithDeletesTestBase.SECRET) + "&") + (VisibilityLabelsWithDeletesTestBase.TOPSECRET))));
                        d.addColumn(VisibilityLabelsWithDeletesTestBase.fam, VisibilityLabelsWithDeletesTestBase.qual, 125L);
                        table.delete(d);
                    } catch (Throwable t) {
                        throw new IOException(t);
                    }
                    return null;
                }
            };
            VisibilityLabelsWithDeletesTestBase.SUPERUSER.runAs(actiona);
            Scan s = new Scan();
            s.readVersions(5);
            s.setAuthorizations(new Authorizations(VisibilityLabelsWithDeletesTestBase.SECRET, VisibilityLabelsWithDeletesTestBase.PRIVATE, VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL, VisibilityLabelsWithDeletesTestBase.TOPSECRET));
            ResultScanner scanner = table.getScanner(s);
            Result[] next = scanner.next(3);
            Assert.assertTrue(((next.length) == 2));
            CellScanner cellScanner = next[0].cellScanner();
            cellScanner.advance();
            Cell current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row1, 0, TestVisibilityLabelsWithDeletes.row1.length));
            Assert.assertEquals(127L, current.getTimestamp());
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row1, 0, TestVisibilityLabelsWithDeletes.row1.length));
            Assert.assertEquals(126L, current.getTimestamp());
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row1, 0, TestVisibilityLabelsWithDeletes.row1.length));
            Assert.assertEquals(124L, current.getTimestamp());
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row1, 0, TestVisibilityLabelsWithDeletes.row1.length));
            Assert.assertEquals(123L, current.getTimestamp());
            cellScanner = next[1].cellScanner();
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row2, 0, TestVisibilityLabelsWithDeletes.row2.length));
            // Issue 2nd delete
            actiona = new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws Exception {
                    try (Connection connection = ConnectionFactory.createConnection(VisibilityLabelsWithDeletesTestBase.conf);Table table = connection.getTable(tableName)) {
                        Delete d = new Delete(VisibilityLabelsWithDeletesTestBase.row1);
                        d.setCellVisibility(new CellVisibility((((((((("(" + (VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL)) + "&") + (VisibilityLabelsWithDeletesTestBase.PRIVATE)) + ")|(") + (VisibilityLabelsWithDeletesTestBase.TOPSECRET)) + "&") + (VisibilityLabelsWithDeletesTestBase.SECRET)) + ")")));
                        d.addColumn(VisibilityLabelsWithDeletesTestBase.fam, VisibilityLabelsWithDeletesTestBase.qual, 127L);
                        table.delete(d);
                    } catch (Throwable t) {
                        throw new IOException(t);
                    }
                    return null;
                }
            };
            VisibilityLabelsWithDeletesTestBase.SUPERUSER.runAs(actiona);
            s = new Scan();
            s.readVersions(5);
            s.setAuthorizations(new Authorizations(VisibilityLabelsWithDeletesTestBase.SECRET, VisibilityLabelsWithDeletesTestBase.PRIVATE, VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL, VisibilityLabelsWithDeletesTestBase.TOPSECRET));
            scanner = table.getScanner(s);
            next = scanner.next(3);
            Assert.assertTrue(((next.length) == 2));
            cellScanner = next[0].cellScanner();
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row1, 0, TestVisibilityLabelsWithDeletes.row1.length));
            Assert.assertEquals(126L, current.getTimestamp());
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row1, 0, TestVisibilityLabelsWithDeletes.row1.length));
            Assert.assertEquals(124L, current.getTimestamp());
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row1, 0, TestVisibilityLabelsWithDeletes.row1.length));
            Assert.assertEquals(123L, current.getTimestamp());
            cellScanner = next[1].cellScanner();
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row2, 0, TestVisibilityLabelsWithDeletes.row2.length));
            Assert.assertEquals(127L, current.getTimestamp());
        }
    }

    @Test
    public void testDeleteColumnSpecificTimeStampWithMulipleVersionsDoneTwice1() throws Exception {
        setAuths();
        final TableName tableName = TableName.valueOf(testName.getMethodName());
        // Do not flush here.
        try (Table table = doPuts(tableName)) {
            PrivilegedExceptionAction<Void> actiona = new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws Exception {
                    try (Connection connection = ConnectionFactory.createConnection(VisibilityLabelsWithDeletesTestBase.conf);Table table = connection.getTable(tableName)) {
                        Delete d = new Delete(VisibilityLabelsWithDeletesTestBase.row1);
                        d.setCellVisibility(new CellVisibility(((((((((("(" + (VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL)) + "&") + (VisibilityLabelsWithDeletesTestBase.PRIVATE)) + ")") + "|(") + (VisibilityLabelsWithDeletesTestBase.TOPSECRET)) + "&") + (VisibilityLabelsWithDeletesTestBase.SECRET)) + ")")));
                        d.addColumn(VisibilityLabelsWithDeletesTestBase.fam, VisibilityLabelsWithDeletesTestBase.qual, 127L);
                        table.delete(d);
                    } catch (Throwable t) {
                        throw new IOException(t);
                    }
                    return null;
                }
            };
            VisibilityLabelsWithDeletesTestBase.SUPERUSER.runAs(actiona);
            Scan s = new Scan();
            s.readVersions(5);
            s.setAuthorizations(new Authorizations(VisibilityLabelsWithDeletesTestBase.SECRET, VisibilityLabelsWithDeletesTestBase.PRIVATE, VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL, VisibilityLabelsWithDeletesTestBase.TOPSECRET));
            ResultScanner scanner = table.getScanner(s);
            Result[] next = scanner.next(3);
            Assert.assertTrue(((next.length) == 2));
            CellScanner cellScanner = next[0].cellScanner();
            cellScanner.advance();
            Cell current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row1, 0, TestVisibilityLabelsWithDeletes.row1.length));
            Assert.assertEquals(126L, current.getTimestamp());
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row1, 0, TestVisibilityLabelsWithDeletes.row1.length));
            Assert.assertEquals(125L, current.getTimestamp());
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row1, 0, TestVisibilityLabelsWithDeletes.row1.length));
            Assert.assertEquals(124L, current.getTimestamp());
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row1, 0, TestVisibilityLabelsWithDeletes.row1.length));
            Assert.assertEquals(123L, current.getTimestamp());
            cellScanner = next[1].cellScanner();
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row2, 0, TestVisibilityLabelsWithDeletes.row2.length));
            // Issue 2nd delete
            actiona = new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws Exception {
                    try (Connection connection = ConnectionFactory.createConnection(VisibilityLabelsWithDeletesTestBase.conf);Table table = connection.getTable(tableName)) {
                        Delete d = new Delete(VisibilityLabelsWithDeletesTestBase.row1);
                        d.setCellVisibility(new CellVisibility((((VisibilityLabelsWithDeletesTestBase.SECRET) + "&") + (VisibilityLabelsWithDeletesTestBase.TOPSECRET))));
                        d.addColumn(VisibilityLabelsWithDeletesTestBase.fam, VisibilityLabelsWithDeletesTestBase.qual, 127L);
                        table.delete(d);
                    } catch (Throwable t) {
                        throw new IOException(t);
                    }
                    return null;
                }
            };
            VisibilityLabelsWithDeletesTestBase.SUPERUSER.runAs(actiona);
            s = new Scan();
            s.readVersions(5);
            s.setAuthorizations(new Authorizations(VisibilityLabelsWithDeletesTestBase.SECRET, VisibilityLabelsWithDeletesTestBase.PRIVATE, VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL, VisibilityLabelsWithDeletesTestBase.TOPSECRET));
            scanner = table.getScanner(s);
            next = scanner.next(3);
            Assert.assertTrue(((next.length) == 2));
            cellScanner = next[0].cellScanner();
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row1, 0, TestVisibilityLabelsWithDeletes.row1.length));
            Assert.assertEquals(126L, current.getTimestamp());
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row1, 0, TestVisibilityLabelsWithDeletes.row1.length));
            Assert.assertEquals(125L, current.getTimestamp());
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row1, 0, TestVisibilityLabelsWithDeletes.row1.length));
            Assert.assertEquals(124L, current.getTimestamp());
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row1, 0, TestVisibilityLabelsWithDeletes.row1.length));
            Assert.assertEquals(123L, current.getTimestamp());
            cellScanner = next[1].cellScanner();
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row2, 0, TestVisibilityLabelsWithDeletes.row2.length));
            Assert.assertEquals(127L, current.getTimestamp());
        }
    }

    @Test
    public void testDeleteColumnSpecificTimeStampWithMulipleVersionsDoneTwice2() throws Exception {
        setAuths();
        final TableName tableName = TableName.valueOf(testName.getMethodName());
        // Do not flush here.
        try (Table table = doPuts(tableName)) {
            PrivilegedExceptionAction<Void> actiona = new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws Exception {
                    try (Connection connection = ConnectionFactory.createConnection(VisibilityLabelsWithDeletesTestBase.conf);Table table = connection.getTable(tableName)) {
                        Delete d = new Delete(VisibilityLabelsWithDeletesTestBase.row1);
                        d.setCellVisibility(new CellVisibility((((((((("(" + (VisibilityLabelsWithDeletesTestBase.PRIVATE)) + "&") + (VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL)) + ")|(") + (VisibilityLabelsWithDeletesTestBase.TOPSECRET)) + "&") + (VisibilityLabelsWithDeletesTestBase.SECRET)) + ")")));
                        d.addColumn(VisibilityLabelsWithDeletesTestBase.fam, VisibilityLabelsWithDeletesTestBase.qual, 125L);
                        table.delete(d);
                    } catch (Throwable t) {
                        throw new IOException(t);
                    }
                    return null;
                }
            };
            VisibilityLabelsWithDeletesTestBase.SUPERUSER.runAs(actiona);
            Scan s = new Scan();
            s.readVersions(5);
            s.setAuthorizations(new Authorizations(VisibilityLabelsWithDeletesTestBase.SECRET, VisibilityLabelsWithDeletesTestBase.PRIVATE, VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL, VisibilityLabelsWithDeletesTestBase.TOPSECRET));
            ResultScanner scanner = table.getScanner(s);
            Result[] next = scanner.next(3);
            Assert.assertTrue(((next.length) == 2));
            CellScanner cellScanner = next[0].cellScanner();
            cellScanner.advance();
            Cell current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row1, 0, TestVisibilityLabelsWithDeletes.row1.length));
            Assert.assertEquals(127L, current.getTimestamp());
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row1, 0, TestVisibilityLabelsWithDeletes.row1.length));
            Assert.assertEquals(126L, current.getTimestamp());
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row1, 0, TestVisibilityLabelsWithDeletes.row1.length));
            Assert.assertEquals(125L, current.getTimestamp());
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row1, 0, TestVisibilityLabelsWithDeletes.row1.length));
            Assert.assertEquals(124L, current.getTimestamp());
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row1, 0, TestVisibilityLabelsWithDeletes.row1.length));
            Assert.assertEquals(123L, current.getTimestamp());
            cellScanner = next[1].cellScanner();
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row2, 0, TestVisibilityLabelsWithDeletes.row2.length));
            // Issue 2nd delete
            actiona = new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws Exception {
                    try (Connection connection = ConnectionFactory.createConnection(VisibilityLabelsWithDeletesTestBase.conf);Table table = connection.getTable(tableName)) {
                        Delete d = new Delete(VisibilityLabelsWithDeletesTestBase.row1);
                        d.setCellVisibility(new CellVisibility((((((((("(" + (VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL)) + "&") + (VisibilityLabelsWithDeletesTestBase.PRIVATE)) + ")|(") + (VisibilityLabelsWithDeletesTestBase.TOPSECRET)) + "&") + (VisibilityLabelsWithDeletesTestBase.SECRET)) + ")")));
                        d.addColumn(VisibilityLabelsWithDeletesTestBase.fam, VisibilityLabelsWithDeletesTestBase.qual, 127L);
                        table.delete(d);
                    } catch (Throwable t) {
                        throw new IOException(t);
                    }
                    return null;
                }
            };
            VisibilityLabelsWithDeletesTestBase.SUPERUSER.runAs(actiona);
            s = new Scan();
            s.readVersions(5);
            s.setAuthorizations(new Authorizations(VisibilityLabelsWithDeletesTestBase.SECRET, VisibilityLabelsWithDeletesTestBase.PRIVATE, VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL, VisibilityLabelsWithDeletesTestBase.TOPSECRET));
            scanner = table.getScanner(s);
            next = scanner.next(3);
            Assert.assertTrue(((next.length) == 2));
            cellScanner = next[0].cellScanner();
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row1, 0, TestVisibilityLabelsWithDeletes.row1.length));
            Assert.assertEquals(126L, current.getTimestamp());
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row1, 0, TestVisibilityLabelsWithDeletes.row1.length));
            Assert.assertEquals(125L, current.getTimestamp());
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row1, 0, TestVisibilityLabelsWithDeletes.row1.length));
            Assert.assertEquals(124L, current.getTimestamp());
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row1, 0, TestVisibilityLabelsWithDeletes.row1.length));
            Assert.assertEquals(123L, current.getTimestamp());
            cellScanner = next[1].cellScanner();
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row2, 0, TestVisibilityLabelsWithDeletes.row2.length));
            Assert.assertEquals(127L, current.getTimestamp());
        }
    }

    @Test
    public void testDeleteColumnAndDeleteFamilylSpecificTimeStampWithMulipleVersion() throws Exception {
        setAuths();
        final TableName tableName = TableName.valueOf(testName.getMethodName());
        // Do not flush here.
        try (Table table = doPuts(tableName)) {
            PrivilegedExceptionAction<Void> actiona = new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws Exception {
                    try (Connection connection = ConnectionFactory.createConnection(VisibilityLabelsWithDeletesTestBase.conf);Table table = connection.getTable(tableName)) {
                        Delete d = new Delete(VisibilityLabelsWithDeletesTestBase.row1);
                        d.setCellVisibility(new CellVisibility((((VisibilityLabelsWithDeletesTestBase.SECRET) + "&") + (VisibilityLabelsWithDeletesTestBase.TOPSECRET))));
                        d.addColumn(VisibilityLabelsWithDeletesTestBase.fam, VisibilityLabelsWithDeletesTestBase.qual, 125L);
                        table.delete(d);
                    } catch (Throwable t) {
                        throw new IOException(t);
                    }
                    return null;
                }
            };
            VisibilityLabelsWithDeletesTestBase.SUPERUSER.runAs(actiona);
            Scan s = new Scan();
            s.readVersions(5);
            s.setAuthorizations(new Authorizations(VisibilityLabelsWithDeletesTestBase.SECRET, VisibilityLabelsWithDeletesTestBase.PRIVATE, VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL, VisibilityLabelsWithDeletesTestBase.TOPSECRET));
            ResultScanner scanner = table.getScanner(s);
            Result[] next = scanner.next(3);
            Assert.assertTrue(((next.length) == 2));
            CellScanner cellScanner = next[0].cellScanner();
            cellScanner.advance();
            Cell current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row1, 0, TestVisibilityLabelsWithDeletes.row1.length));
            Assert.assertEquals(127L, current.getTimestamp());
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row1, 0, TestVisibilityLabelsWithDeletes.row1.length));
            Assert.assertEquals(126L, current.getTimestamp());
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row1, 0, TestVisibilityLabelsWithDeletes.row1.length));
            Assert.assertEquals(124L, current.getTimestamp());
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row1, 0, TestVisibilityLabelsWithDeletes.row1.length));
            Assert.assertEquals(123L, current.getTimestamp());
            cellScanner = next[1].cellScanner();
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row2, 0, TestVisibilityLabelsWithDeletes.row2.length));
            // Issue 2nd delete
            actiona = new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws Exception {
                    try (Connection connection = ConnectionFactory.createConnection(VisibilityLabelsWithDeletesTestBase.conf);Table table = connection.getTable(tableName)) {
                        Delete d = new Delete(VisibilityLabelsWithDeletesTestBase.row1);
                        d.setCellVisibility(new CellVisibility((((((((("(" + (VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL)) + "&") + (VisibilityLabelsWithDeletesTestBase.PRIVATE)) + ")|(") + (VisibilityLabelsWithDeletesTestBase.TOPSECRET)) + "&") + (VisibilityLabelsWithDeletesTestBase.SECRET)) + ")")));
                        d.addFamily(VisibilityLabelsWithDeletesTestBase.fam, 124L);
                        table.delete(d);
                    } catch (Throwable t) {
                        throw new IOException(t);
                    }
                    return null;
                }
            };
            VisibilityLabelsWithDeletesTestBase.SUPERUSER.runAs(actiona);
            s = new Scan();
            s.readVersions(5);
            s.setAuthorizations(new Authorizations(VisibilityLabelsWithDeletesTestBase.SECRET, VisibilityLabelsWithDeletesTestBase.PRIVATE, VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL, VisibilityLabelsWithDeletesTestBase.TOPSECRET));
            scanner = table.getScanner(s);
            next = scanner.next(3);
            Assert.assertTrue(((next.length) == 2));
            cellScanner = next[0].cellScanner();
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row1, 0, TestVisibilityLabelsWithDeletes.row1.length));
            Assert.assertEquals(127L, current.getTimestamp());
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row1, 0, TestVisibilityLabelsWithDeletes.row1.length));
            Assert.assertEquals(126L, current.getTimestamp());
            cellScanner = next[1].cellScanner();
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row2, 0, TestVisibilityLabelsWithDeletes.row2.length));
            Assert.assertEquals(127L, current.getTimestamp());
        }
    }

    @Test
    public void testDiffDeleteTypesForTheSameCellUsingMultipleVersions() throws Exception {
        setAuths();
        final TableName tableName = TableName.valueOf(testName.getMethodName());
        try (Table table = doPuts(tableName)) {
            // Do not flush here.
            PrivilegedExceptionAction<Void> actiona = new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws Exception {
                    try (Connection connection = ConnectionFactory.createConnection(VisibilityLabelsWithDeletesTestBase.conf);Table table = connection.getTable(tableName)) {
                        Delete d = new Delete(VisibilityLabelsWithDeletesTestBase.row1);
                        d.setCellVisibility(new CellVisibility((((((((("(" + (VisibilityLabelsWithDeletesTestBase.PRIVATE)) + "&") + (VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL)) + ")|(") + (VisibilityLabelsWithDeletesTestBase.TOPSECRET)) + "&") + (VisibilityLabelsWithDeletesTestBase.SECRET)) + ")")));
                        d.addColumns(VisibilityLabelsWithDeletesTestBase.fam, VisibilityLabelsWithDeletesTestBase.qual, 125L);
                        table.delete(d);
                    } catch (Throwable t) {
                        throw new IOException(t);
                    }
                    return null;
                }
            };
            VisibilityLabelsWithDeletesTestBase.SUPERUSER.runAs(actiona);
            Scan s = new Scan();
            s.readVersions(5);
            s.setAuthorizations(new Authorizations(VisibilityLabelsWithDeletesTestBase.SECRET, VisibilityLabelsWithDeletesTestBase.PRIVATE, VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL, VisibilityLabelsWithDeletesTestBase.TOPSECRET));
            ResultScanner scanner = table.getScanner(s);
            Result[] next = scanner.next(3);
            Assert.assertTrue(((next.length) == 2));
            CellScanner cellScanner = next[0].cellScanner();
            cellScanner.advance();
            Cell current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row1, 0, TestVisibilityLabelsWithDeletes.row1.length));
            Assert.assertEquals(127L, current.getTimestamp());
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row1, 0, TestVisibilityLabelsWithDeletes.row1.length));
            Assert.assertEquals(126L, current.getTimestamp());
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row1, 0, TestVisibilityLabelsWithDeletes.row1.length));
            Assert.assertEquals(125L, current.getTimestamp());
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row1, 0, TestVisibilityLabelsWithDeletes.row1.length));
            Assert.assertEquals(123L, current.getTimestamp());
            cellScanner = next[1].cellScanner();
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row2, 0, TestVisibilityLabelsWithDeletes.row2.length));
            // Issue 2nd delete
            actiona = new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws Exception {
                    try (Connection connection = ConnectionFactory.createConnection(VisibilityLabelsWithDeletesTestBase.conf);Table table = connection.getTable(tableName)) {
                        Delete d = new Delete(VisibilityLabelsWithDeletesTestBase.row1);
                        d.setCellVisibility(new CellVisibility((((((((("(" + (VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL)) + "&") + (VisibilityLabelsWithDeletesTestBase.PRIVATE)) + ")|(") + (VisibilityLabelsWithDeletesTestBase.TOPSECRET)) + "&") + (VisibilityLabelsWithDeletesTestBase.SECRET)) + ")")));
                        d.addColumn(VisibilityLabelsWithDeletesTestBase.fam, VisibilityLabelsWithDeletesTestBase.qual, 127L);
                        table.delete(d);
                    } catch (Throwable t) {
                        throw new IOException(t);
                    }
                    return null;
                }
            };
            VisibilityLabelsWithDeletesTestBase.SUPERUSER.runAs(actiona);
            s = new Scan();
            s.readVersions(5);
            s.setAuthorizations(new Authorizations(VisibilityLabelsWithDeletesTestBase.SECRET, VisibilityLabelsWithDeletesTestBase.PRIVATE, VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL, VisibilityLabelsWithDeletesTestBase.TOPSECRET));
            scanner = table.getScanner(s);
            next = scanner.next(3);
            Assert.assertTrue(((next.length) == 2));
            cellScanner = next[0].cellScanner();
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row1, 0, TestVisibilityLabelsWithDeletes.row1.length));
            Assert.assertEquals(126L, current.getTimestamp());
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row1, 0, TestVisibilityLabelsWithDeletes.row1.length));
            Assert.assertEquals(125L, current.getTimestamp());
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row1, 0, TestVisibilityLabelsWithDeletes.row1.length));
            Assert.assertEquals(123L, current.getTimestamp());
            cellScanner = next[1].cellScanner();
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row2, 0, TestVisibilityLabelsWithDeletes.row2.length));
        }
    }

    @Test
    public void testDeleteColumnLatestWithNoCellVisibility() throws Exception {
        setAuths();
        final TableName tableName = TableName.valueOf(testName.getMethodName());
        try (Table table = doPuts(tableName)) {
            VisibilityLabelsWithDeletesTestBase.TEST_UTIL.getAdmin().flush(tableName);
            PrivilegedExceptionAction<Void> actiona = new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws Exception {
                    try (Connection connection = ConnectionFactory.createConnection(VisibilityLabelsWithDeletesTestBase.conf);Table table = connection.getTable(tableName)) {
                        Delete d = new Delete(VisibilityLabelsWithDeletesTestBase.row1);
                        d.addColumn(VisibilityLabelsWithDeletesTestBase.fam, VisibilityLabelsWithDeletesTestBase.qual, 125L);
                        table.delete(d);
                    } catch (Throwable t) {
                        throw new IOException(t);
                    }
                    return null;
                }
            };
            VisibilityLabelsWithDeletesTestBase.SUPERUSER.runAs(actiona);
            VisibilityLabelsWithDeletesTestBase.TEST_UTIL.getAdmin().flush(tableName);
            Scan s = new Scan();
            s.readVersions(5);
            s.setAuthorizations(new Authorizations(VisibilityLabelsWithDeletesTestBase.SECRET, VisibilityLabelsWithDeletesTestBase.PRIVATE, VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL, VisibilityLabelsWithDeletesTestBase.TOPSECRET));
            ResultScanner scanner = table.getScanner(s);
            Result[] next = scanner.next(3);
            Assert.assertTrue(((next.length) == 2));
            scanAll(next);
            actiona = new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws Exception {
                    try (Connection connection = ConnectionFactory.createConnection(VisibilityLabelsWithDeletesTestBase.conf);Table table = connection.getTable(tableName)) {
                        Delete d = new Delete(VisibilityLabelsWithDeletesTestBase.row1);
                        d.addColumns(VisibilityLabelsWithDeletesTestBase.fam, VisibilityLabelsWithDeletesTestBase.qual, 125L);
                        table.delete(d);
                    } catch (Throwable t) {
                        throw new IOException(t);
                    }
                    return null;
                }
            };
            VisibilityLabelsWithDeletesTestBase.SUPERUSER.runAs(actiona);
            VisibilityLabelsWithDeletesTestBase.TEST_UTIL.getAdmin().flush(tableName);
            s = new Scan();
            s.readVersions(5);
            s.setAuthorizations(new Authorizations(VisibilityLabelsWithDeletesTestBase.SECRET, VisibilityLabelsWithDeletesTestBase.PRIVATE, VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL, VisibilityLabelsWithDeletesTestBase.TOPSECRET));
            scanner = table.getScanner(s);
            next = scanner.next(3);
            Assert.assertTrue(((next.length) == 2));
            scanAll(next);
            actiona = new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws Exception {
                    try (Connection connection = ConnectionFactory.createConnection(VisibilityLabelsWithDeletesTestBase.conf);Table table = connection.getTable(tableName)) {
                        Delete d = new Delete(VisibilityLabelsWithDeletesTestBase.row1);
                        d.addFamily(VisibilityLabelsWithDeletesTestBase.fam, 125L);
                        table.delete(d);
                    } catch (Throwable t) {
                        throw new IOException(t);
                    }
                    return null;
                }
            };
            VisibilityLabelsWithDeletesTestBase.SUPERUSER.runAs(actiona);
            VisibilityLabelsWithDeletesTestBase.TEST_UTIL.getAdmin().flush(tableName);
            s = new Scan();
            s.readVersions(5);
            s.setAuthorizations(new Authorizations(VisibilityLabelsWithDeletesTestBase.SECRET, VisibilityLabelsWithDeletesTestBase.PRIVATE, VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL, VisibilityLabelsWithDeletesTestBase.TOPSECRET));
            scanner = table.getScanner(s);
            next = scanner.next(3);
            Assert.assertTrue(((next.length) == 2));
            scanAll(next);
            actiona = new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws Exception {
                    try (Connection connection = ConnectionFactory.createConnection(VisibilityLabelsWithDeletesTestBase.conf);Table table = connection.getTable(tableName)) {
                        Delete d = new Delete(VisibilityLabelsWithDeletesTestBase.row1);
                        d.addFamily(VisibilityLabelsWithDeletesTestBase.fam);
                        table.delete(d);
                    } catch (Throwable t) {
                        throw new IOException(t);
                    }
                    return null;
                }
            };
            VisibilityLabelsWithDeletesTestBase.SUPERUSER.runAs(actiona);
            VisibilityLabelsWithDeletesTestBase.TEST_UTIL.getAdmin().flush(tableName);
            s = new Scan();
            s.readVersions(5);
            s.setAuthorizations(new Authorizations(VisibilityLabelsWithDeletesTestBase.SECRET, VisibilityLabelsWithDeletesTestBase.PRIVATE, VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL, VisibilityLabelsWithDeletesTestBase.TOPSECRET));
            scanner = table.getScanner(s);
            next = scanner.next(3);
            Assert.assertTrue(((next.length) == 2));
            scanAll(next);
            actiona = new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws Exception {
                    try (Connection connection = ConnectionFactory.createConnection(VisibilityLabelsWithDeletesTestBase.conf);Table table = connection.getTable(tableName)) {
                        Delete d = new Delete(VisibilityLabelsWithDeletesTestBase.row1);
                        d.addColumns(VisibilityLabelsWithDeletesTestBase.fam, VisibilityLabelsWithDeletesTestBase.qual);
                        table.delete(d);
                    } catch (Throwable t) {
                        throw new IOException(t);
                    }
                    return null;
                }
            };
            VisibilityLabelsWithDeletesTestBase.SUPERUSER.runAs(actiona);
            VisibilityLabelsWithDeletesTestBase.TEST_UTIL.getAdmin().flush(tableName);
            s = new Scan();
            s.readVersions(5);
            s.setAuthorizations(new Authorizations(VisibilityLabelsWithDeletesTestBase.SECRET, VisibilityLabelsWithDeletesTestBase.PRIVATE, VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL, VisibilityLabelsWithDeletesTestBase.TOPSECRET));
            scanner = table.getScanner(s);
            next = scanner.next(3);
            Assert.assertTrue(((next.length) == 2));
            scanAll(next);
            actiona = new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws Exception {
                    try (Connection connection = ConnectionFactory.createConnection(VisibilityLabelsWithDeletesTestBase.conf);Table table = connection.getTable(tableName)) {
                        Delete d = new Delete(VisibilityLabelsWithDeletesTestBase.row1);
                        d.addFamilyVersion(VisibilityLabelsWithDeletesTestBase.fam, 126L);
                        table.delete(d);
                    } catch (Throwable t) {
                        throw new IOException(t);
                    }
                    return null;
                }
            };
            VisibilityLabelsWithDeletesTestBase.SUPERUSER.runAs(actiona);
            VisibilityLabelsWithDeletesTestBase.TEST_UTIL.getAdmin().flush(tableName);
            s = new Scan();
            s.readVersions(5);
            s.setAuthorizations(new Authorizations(VisibilityLabelsWithDeletesTestBase.SECRET, VisibilityLabelsWithDeletesTestBase.PRIVATE, VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL, VisibilityLabelsWithDeletesTestBase.TOPSECRET));
            scanner = table.getScanner(s);
            next = scanner.next(3);
            Assert.assertTrue(((next.length) == 2));
            scanAll(next);
        }
    }

    @Test
    public void testVisibilityExpressionWithNotEqualORCondition() throws Exception {
        setAuths();
        TableName tableName = createTable(5);
        try (Table table = VisibilityLabelsWithDeletesTestBase.TEST_UTIL.getConnection().getTable(tableName)) {
            Put put = new Put(Bytes.toBytes("row1"));
            put.addColumn(VisibilityLabelsWithDeletesTestBase.fam, VisibilityLabelsWithDeletesTestBase.qual, 123L, VisibilityLabelsWithDeletesTestBase.value);
            put.setCellVisibility(new CellVisibility(VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL));
            table.put(put);
            put = new Put(Bytes.toBytes("row1"));
            put.addColumn(VisibilityLabelsWithDeletesTestBase.fam, VisibilityLabelsWithDeletesTestBase.qual, 124L, VisibilityLabelsWithDeletesTestBase.value);
            put.setCellVisibility(new CellVisibility((((VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL) + "|") + (VisibilityLabelsWithDeletesTestBase.PRIVATE))));
            table.put(put);
            VisibilityLabelsWithDeletesTestBase.TEST_UTIL.getAdmin().flush(tableName);
            PrivilegedExceptionAction<Void> actiona = new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws Exception {
                    try (Connection connection = ConnectionFactory.createConnection(VisibilityLabelsWithDeletesTestBase.conf);Table table = connection.getTable(tableName)) {
                        Delete d = new Delete(VisibilityLabelsWithDeletesTestBase.row1);
                        d.addColumn(VisibilityLabelsWithDeletesTestBase.fam, VisibilityLabelsWithDeletesTestBase.qual, 124L);
                        d.setCellVisibility(new CellVisibility(VisibilityLabelsWithDeletesTestBase.PRIVATE));
                        table.delete(d);
                    } catch (Throwable t) {
                        throw new IOException(t);
                    }
                    return null;
                }
            };
            VisibilityLabelsWithDeletesTestBase.SUPERUSER.runAs(actiona);
            VisibilityLabelsWithDeletesTestBase.TEST_UTIL.getAdmin().flush(tableName);
            Scan s = new Scan();
            s.readVersions(5);
            s.setAuthorizations(new Authorizations(VisibilityLabelsWithDeletesTestBase.SECRET, VisibilityLabelsWithDeletesTestBase.PRIVATE, VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL, VisibilityLabelsWithDeletesTestBase.TOPSECRET));
            ResultScanner scanner = table.getScanner(s);
            Result[] next = scanner.next(3);
            Assert.assertTrue(((next.length) == 1));
            CellScanner cellScanner = next[0].cellScanner();
            cellScanner.advance();
            Cell current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row1, 0, TestVisibilityLabelsWithDeletes.row1.length));
            Assert.assertEquals(124L, current.getTimestamp());
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row1, 0, TestVisibilityLabelsWithDeletes.row1.length));
            Assert.assertEquals(123L, current.getTimestamp());
        }
    }

    @Test
    public void testDeleteWithNoVisibilitiesForPutsAndDeletes() throws Exception {
        TableName tableName = createTable(5);
        Put p = new Put(Bytes.toBytes("row1"));
        p.addColumn(VisibilityLabelsWithDeletesTestBase.fam, VisibilityLabelsWithDeletesTestBase.qual, VisibilityLabelsWithDeletesTestBase.value);
        Table table = VisibilityLabelsWithDeletesTestBase.TEST_UTIL.getConnection().getTable(tableName);
        table.put(p);
        p = new Put(Bytes.toBytes("row1"));
        p.addColumn(VisibilityLabelsWithDeletesTestBase.fam, VisibilityLabelsWithDeletesTestBase.qual1, VisibilityLabelsWithDeletesTestBase.value);
        table.put(p);
        p = new Put(Bytes.toBytes("row2"));
        p.addColumn(VisibilityLabelsWithDeletesTestBase.fam, VisibilityLabelsWithDeletesTestBase.qual, VisibilityLabelsWithDeletesTestBase.value);
        table.put(p);
        p = new Put(Bytes.toBytes("row2"));
        p.addColumn(VisibilityLabelsWithDeletesTestBase.fam, VisibilityLabelsWithDeletesTestBase.qual1, VisibilityLabelsWithDeletesTestBase.value);
        table.put(p);
        Delete d = new Delete(Bytes.toBytes("row1"));
        table.delete(d);
        Get g = new Get(Bytes.toBytes("row1"));
        g.readAllVersions();
        g.setAuthorizations(new Authorizations(VisibilityLabelsWithDeletesTestBase.SECRET, VisibilityLabelsWithDeletesTestBase.PRIVATE));
        Result result = table.get(g);
        Assert.assertEquals(0, result.rawCells().length);
        p = new Put(Bytes.toBytes("row1"));
        p.addColumn(VisibilityLabelsWithDeletesTestBase.fam, VisibilityLabelsWithDeletesTestBase.qual, VisibilityLabelsWithDeletesTestBase.value);
        table.put(p);
        result = table.get(g);
        Assert.assertEquals(1, result.rawCells().length);
    }

    @Test
    public void testDeleteWithFamilyDeletesOfSameTsButDifferentVisibilities() throws Exception {
        TableName tableName = createTable(5);
        Table table = VisibilityLabelsWithDeletesTestBase.TEST_UTIL.getConnection().getTable(tableName);
        long t1 = 1234L;
        CellVisibility cellVisibility1 = new CellVisibility(VisibilityLabelsWithDeletesTestBase.SECRET);
        CellVisibility cellVisibility2 = new CellVisibility(VisibilityLabelsWithDeletesTestBase.PRIVATE);
        // Cell row1:info:qual:1234 with visibility SECRET
        Put p = new Put(VisibilityLabelsWithDeletesTestBase.row1);
        p.addColumn(VisibilityLabelsWithDeletesTestBase.fam, VisibilityLabelsWithDeletesTestBase.qual, t1, VisibilityLabelsWithDeletesTestBase.value);
        p.setCellVisibility(cellVisibility1);
        table.put(p);
        // Cell row1:info:qual1:1234 with visibility PRIVATE
        p = new Put(VisibilityLabelsWithDeletesTestBase.row1);
        p.addColumn(VisibilityLabelsWithDeletesTestBase.fam, VisibilityLabelsWithDeletesTestBase.qual1, t1, VisibilityLabelsWithDeletesTestBase.value);
        p.setCellVisibility(cellVisibility2);
        table.put(p);
        Delete d = new Delete(VisibilityLabelsWithDeletesTestBase.row1);
        d.addFamily(VisibilityLabelsWithDeletesTestBase.fam, t1);
        d.setCellVisibility(cellVisibility2);
        table.delete(d);
        d = new Delete(VisibilityLabelsWithDeletesTestBase.row1);
        d.addFamily(VisibilityLabelsWithDeletesTestBase.fam, t1);
        d.setCellVisibility(cellVisibility1);
        table.delete(d);
        Get g = new Get(VisibilityLabelsWithDeletesTestBase.row1);
        g.readAllVersions();
        g.setAuthorizations(new Authorizations(VisibilityLabelsWithDeletesTestBase.SECRET, VisibilityLabelsWithDeletesTestBase.PRIVATE));
        Result result = table.get(g);
        Assert.assertEquals(0, result.rawCells().length);
        // Cell row2:info:qual:1234 with visibility SECRET
        p = new Put(VisibilityLabelsWithDeletesTestBase.row2);
        p.addColumn(VisibilityLabelsWithDeletesTestBase.fam, VisibilityLabelsWithDeletesTestBase.qual, t1, VisibilityLabelsWithDeletesTestBase.value);
        p.setCellVisibility(cellVisibility1);
        table.put(p);
        // Cell row2:info:qual1:1234 with visibility PRIVATE
        p = new Put(VisibilityLabelsWithDeletesTestBase.row2);
        p.addColumn(VisibilityLabelsWithDeletesTestBase.fam, VisibilityLabelsWithDeletesTestBase.qual1, t1, VisibilityLabelsWithDeletesTestBase.value);
        p.setCellVisibility(cellVisibility2);
        table.put(p);
        d = new Delete(VisibilityLabelsWithDeletesTestBase.row2);
        d.addFamilyVersion(VisibilityLabelsWithDeletesTestBase.fam, t1);
        d.setCellVisibility(cellVisibility2);
        table.delete(d);
        d = new Delete(VisibilityLabelsWithDeletesTestBase.row2);
        d.addFamilyVersion(VisibilityLabelsWithDeletesTestBase.fam, t1);
        d.setCellVisibility(cellVisibility1);
        table.delete(d);
        g = new Get(VisibilityLabelsWithDeletesTestBase.row2);
        g.readAllVersions();
        g.setAuthorizations(new Authorizations(VisibilityLabelsWithDeletesTestBase.SECRET, VisibilityLabelsWithDeletesTestBase.PRIVATE));
        result = table.get(g);
        Assert.assertEquals(0, result.rawCells().length);
    }

    private enum DeleteMark {

        ROW,
        FAMILY,
        FAMILY_VERSION,
        COLUMN,
        CELL;}

    @Test
    public void testDeleteCellWithoutVisibility() throws IOException, InterruptedException {
        for (TestVisibilityLabelsWithDeletes.DeleteMark mark : TestVisibilityLabelsWithDeletes.DeleteMark.values()) {
            testDeleteCellWithoutVisibility(mark);
        }
    }

    @Test
    public void testDeleteCellWithVisibility() throws IOException, InterruptedException {
        for (TestVisibilityLabelsWithDeletes.DeleteMark mark : TestVisibilityLabelsWithDeletes.DeleteMark.values()) {
            testDeleteCellWithVisibility(mark);
            testDeleteCellWithVisibilityV2(mark);
        }
    }
}

