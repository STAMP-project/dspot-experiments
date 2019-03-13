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


import java.io.IOException;
import java.security.PrivilegedExceptionAction;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellScanner;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.security.User;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;


/**
 * Tests visibility labels with deletes
 */
public abstract class VisibilityLabelsWithDeletesTestBase {
    protected static final String TOPSECRET = "TOPSECRET";

    protected static final String PUBLIC = "PUBLIC";

    protected static final String PRIVATE = "PRIVATE";

    protected static final String CONFIDENTIAL = "CONFIDENTIAL";

    protected static final String SECRET = "SECRET";

    protected static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    protected static final byte[] row1 = Bytes.toBytes("row1");

    protected static final byte[] row2 = Bytes.toBytes("row2");

    protected static final byte[] fam = Bytes.toBytes("info");

    protected static final byte[] qual = Bytes.toBytes("qual");

    protected static final byte[] qual1 = Bytes.toBytes("qual1");

    protected static final byte[] qual2 = Bytes.toBytes("qual2");

    protected static final byte[] value = Bytes.toBytes("value");

    protected static final byte[] value1 = Bytes.toBytes("value1");

    protected static Configuration conf;

    @Rule
    public final TestName testName = new TestName();

    protected static User SUPERUSER;

    @Test
    public void testVisibilityLabelsWithDeleteColumns() throws Throwable {
        setAuths();
        final TableName tableName = TableName.valueOf(testName.getMethodName());
        try (Table table = createTableAndWriteDataWithLabels((((VisibilityLabelsWithDeletesTestBase.SECRET) + "&") + (VisibilityLabelsWithDeletesTestBase.TOPSECRET)), VisibilityLabelsWithDeletesTestBase.SECRET)) {
            PrivilegedExceptionAction<Void> actiona = new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws Exception {
                    try (Connection connection = ConnectionFactory.createConnection(VisibilityLabelsWithDeletesTestBase.conf);Table table = connection.getTable(tableName)) {
                        Delete d = new Delete(VisibilityLabelsWithDeletesTestBase.row1);
                        d.setCellVisibility(new CellVisibility((((VisibilityLabelsWithDeletesTestBase.TOPSECRET) + "&") + (VisibilityLabelsWithDeletesTestBase.SECRET))));
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
            Scan s = new Scan();
            s.setAuthorizations(new Authorizations(VisibilityLabelsWithDeletesTestBase.SECRET, VisibilityLabelsWithDeletesTestBase.PRIVATE, VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL));
            ResultScanner scanner = table.getScanner(s);
            Result[] next = scanner.next(3);
            Assert.assertTrue(((next.length) == 1));
            CellScanner cellScanner = next[0].cellScanner();
            cellScanner.advance();
            Cell current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row2, 0, VisibilityLabelsWithDeletesTestBase.row2.length));
        }
    }

    @Test
    public void testVisibilityLabelsWithDeleteFamily() throws Exception {
        setAuths();
        final TableName tableName = TableName.valueOf(testName.getMethodName());
        try (Table table = createTableAndWriteDataWithLabels(VisibilityLabelsWithDeletesTestBase.SECRET, (((VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL) + "|") + (VisibilityLabelsWithDeletesTestBase.TOPSECRET)))) {
            PrivilegedExceptionAction<Void> actiona = new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws Exception {
                    try (Connection connection = ConnectionFactory.createConnection(VisibilityLabelsWithDeletesTestBase.conf);Table table = connection.getTable(tableName)) {
                        Delete d = new Delete(VisibilityLabelsWithDeletesTestBase.row2);
                        d.setCellVisibility(new CellVisibility((((VisibilityLabelsWithDeletesTestBase.TOPSECRET) + "|") + (VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL))));
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
            s.setAuthorizations(new Authorizations(VisibilityLabelsWithDeletesTestBase.SECRET, VisibilityLabelsWithDeletesTestBase.PRIVATE, VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL));
            ResultScanner scanner = table.getScanner(s);
            Result[] next = scanner.next(3);
            Assert.assertTrue(((next.length) == 1));
            CellScanner cellScanner = next[0].cellScanner();
            cellScanner.advance();
            Cell current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row1, 0, VisibilityLabelsWithDeletesTestBase.row1.length));
        }
    }

    @Test
    public void testVisibilityLabelsWithDeleteFamilyVersion() throws Exception {
        setAuths();
        final TableName tableName = TableName.valueOf(testName.getMethodName());
        long[] ts = new long[]{ 123L, 125L };
        try (Table table = createTableAndWriteDataWithLabels(ts, (((VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL) + "|") + (VisibilityLabelsWithDeletesTestBase.TOPSECRET)), VisibilityLabelsWithDeletesTestBase.SECRET)) {
            PrivilegedExceptionAction<Void> actiona = new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws Exception {
                    try (Connection connection = ConnectionFactory.createConnection(VisibilityLabelsWithDeletesTestBase.conf);Table table = connection.getTable(tableName)) {
                        Delete d = new Delete(VisibilityLabelsWithDeletesTestBase.row1);
                        d.setCellVisibility(new CellVisibility((((VisibilityLabelsWithDeletesTestBase.TOPSECRET) + "|") + (VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL))));
                        d.addFamilyVersion(VisibilityLabelsWithDeletesTestBase.fam, 123L);
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
            s.setAuthorizations(new Authorizations(VisibilityLabelsWithDeletesTestBase.SECRET, VisibilityLabelsWithDeletesTestBase.PRIVATE, VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL));
            ResultScanner scanner = table.getScanner(s);
            Result[] next = scanner.next(3);
            Assert.assertTrue(((next.length) == 1));
            CellScanner cellScanner = next[0].cellScanner();
            cellScanner.advance();
            Cell current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row2, 0, VisibilityLabelsWithDeletesTestBase.row2.length));
        }
    }

    @Test
    public void testVisibilityLabelsWithDeleteColumnExactVersion() throws Exception {
        setAuths();
        final TableName tableName = TableName.valueOf(testName.getMethodName());
        long[] ts = new long[]{ 123L, 125L };
        try (Table table = createTableAndWriteDataWithLabels(ts, (((VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL) + "|") + (VisibilityLabelsWithDeletesTestBase.TOPSECRET)), VisibilityLabelsWithDeletesTestBase.SECRET)) {
            PrivilegedExceptionAction<Void> actiona = new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws Exception {
                    try (Connection connection = ConnectionFactory.createConnection(VisibilityLabelsWithDeletesTestBase.conf);Table table = connection.getTable(tableName)) {
                        Delete d = new Delete(VisibilityLabelsWithDeletesTestBase.row1);
                        d.setCellVisibility(new CellVisibility((((VisibilityLabelsWithDeletesTestBase.TOPSECRET) + "|") + (VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL))));
                        d.addColumn(VisibilityLabelsWithDeletesTestBase.fam, VisibilityLabelsWithDeletesTestBase.qual, 123L);
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
            s.setAuthorizations(new Authorizations(VisibilityLabelsWithDeletesTestBase.SECRET, VisibilityLabelsWithDeletesTestBase.PRIVATE, VisibilityLabelsWithDeletesTestBase.CONFIDENTIAL));
            ResultScanner scanner = table.getScanner(s);
            Result[] next = scanner.next(3);
            Assert.assertTrue(((next.length) == 1));
            CellScanner cellScanner = next[0].cellScanner();
            cellScanner.advance();
            Cell current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), VisibilityLabelsWithDeletesTestBase.row2, 0, VisibilityLabelsWithDeletesTestBase.row2.length));
        }
    }
}

