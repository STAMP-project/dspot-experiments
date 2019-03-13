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
package org.apache.hadoop.hbase.coprocessor;


import Permission.Action.EXEC;
import Permission.Action.READ;
import com.google.protobuf.ServiceException;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsAction;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.ColumnFamilyDescriptorBuilder;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.client.TableDescriptor;
import org.apache.hadoop.hbase.client.TableDescriptorBuilder;
import org.apache.hadoop.hbase.mapreduce.Import;
import org.apache.hadoop.hbase.security.User;
import org.apache.hadoop.hbase.security.access.AccessControlLists;
import org.apache.hadoop.hbase.security.access.SecureTestUtil;
import org.apache.hadoop.hbase.security.access.SecureTestUtil.AccessTestAction;
import org.apache.hadoop.hbase.security.visibility.Authorizations;
import org.apache.hadoop.hbase.security.visibility.CellVisibility;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.Pair;
import org.apache.hadoop.minikdc.MiniKdc;
import org.apache.hadoop.util.ToolRunner;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ MediumTests.class })
public class TestSecureExport {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestSecureExport.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestSecureExport.class);

    private static final HBaseTestingUtility UTIL = new HBaseTestingUtility();

    private static MiniKdc KDC;

    private static final File KEYTAB_FILE = new File(TestSecureExport.UTIL.getDataTestDir("keytab").toUri().getPath());

    private static String USERNAME;

    private static String SERVER_PRINCIPAL;

    private static String HTTP_PRINCIPAL;

    private static final String FAMILYA_STRING = "fma";

    private static final String FAMILYB_STRING = "fma";

    private static final byte[] FAMILYA = Bytes.toBytes(TestSecureExport.FAMILYA_STRING);

    private static final byte[] FAMILYB = Bytes.toBytes(TestSecureExport.FAMILYB_STRING);

    private static final byte[] ROW1 = Bytes.toBytes("row1");

    private static final byte[] ROW2 = Bytes.toBytes("row2");

    private static final byte[] ROW3 = Bytes.toBytes("row3");

    private static final byte[] QUAL = Bytes.toBytes("qual");

    private static final String LOCALHOST = "localhost";

    private static final long NOW = System.currentTimeMillis();

    // user granted with all global permission
    private static final String USER_ADMIN = "admin";

    // user is table owner. will have all permissions on table
    private static final String USER_OWNER = "owner";

    // user with rx permissions.
    private static final String USER_RX = "rxuser";

    // user with exe-only permissions.
    private static final String USER_XO = "xouser";

    // user with read-only permissions.
    private static final String USER_RO = "rouser";

    // user with no permissions
    private static final String USER_NONE = "noneuser";

    private static final String PRIVATE = "private";

    private static final String CONFIDENTIAL = "confidential";

    private static final String SECRET = "secret";

    private static final String TOPSECRET = "topsecret";

    @Rule
    public final TestName name = new TestName();

    /**
     * Test the ExportEndpoint's access levels. The {@link Export} test is ignored
     * since the access exceptions cannot be collected from the mappers.
     */
    @Test
    public void testAccessCase() throws Throwable {
        final String exportTable = name.getMethodName();
        TableDescriptor exportHtd = TableDescriptorBuilder.newBuilder(TableName.valueOf(name.getMethodName())).setColumnFamily(ColumnFamilyDescriptorBuilder.of(TestSecureExport.FAMILYA)).setOwnerString(TestSecureExport.USER_OWNER).build();
        SecureTestUtil.createTable(TestSecureExport.UTIL, exportHtd, new byte[][]{ Bytes.toBytes("s") });
        SecureTestUtil.grantOnTable(TestSecureExport.UTIL, TestSecureExport.USER_RO, TableName.valueOf(exportTable), null, null, READ);
        SecureTestUtil.grantOnTable(TestSecureExport.UTIL, TestSecureExport.USER_RX, TableName.valueOf(exportTable), null, null, READ, EXEC);
        SecureTestUtil.grantOnTable(TestSecureExport.UTIL, TestSecureExport.USER_XO, TableName.valueOf(exportTable), null, null, EXEC);
        Assert.assertEquals(4, AccessControlLists.getTablePermissions(TestSecureExport.UTIL.getConfiguration(), TableName.valueOf(exportTable)).size());
        AccessTestAction putAction = () -> {
            Put p = new Put(ROW1);
            p.addColumn(FAMILYA, Bytes.toBytes("qual_0"), NOW, QUAL);
            p.addColumn(FAMILYA, Bytes.toBytes("qual_1"), NOW, QUAL);
            try (Connection conn = ConnectionFactory.createConnection(UTIL.getConfiguration());Table t = conn.getTable(TableName.valueOf(exportTable))) {
                t.put(p);
            }
            return null;
        };
        // no hdfs access.
        SecureTestUtil.verifyAllowed(putAction, TestSecureExport.getUserByLogin(TestSecureExport.USER_ADMIN), TestSecureExport.getUserByLogin(TestSecureExport.USER_OWNER));
        SecureTestUtil.verifyDenied(putAction, TestSecureExport.getUserByLogin(TestSecureExport.USER_RO), TestSecureExport.getUserByLogin(TestSecureExport.USER_XO), TestSecureExport.getUserByLogin(TestSecureExport.USER_RX), TestSecureExport.getUserByLogin(TestSecureExport.USER_NONE));
        final FileSystem fs = TestSecureExport.UTIL.getDFSCluster().getFileSystem();
        final Path openDir = fs.makeQualified(new Path("testAccessCase"));
        fs.mkdirs(openDir);
        fs.setPermission(openDir, new org.apache.hadoop.fs.permission.FsPermission(FsAction.ALL, FsAction.ALL, FsAction.ALL));
        final Path output = fs.makeQualified(new Path(openDir, "output"));
        AccessTestAction exportAction = () -> {
            try {
                String[] args = new String[]{ exportTable, output.toString() };
                Map<byte[], Export.Response> result = Export.run(new Configuration(UTIL.getConfiguration()), args);
                long rowCount = 0;
                long cellCount = 0;
                for (Export.Response r : result.values()) {
                    rowCount += r.getRowCount();
                    cellCount += r.getCellCount();
                }
                assertEquals(1, rowCount);
                assertEquals(2, cellCount);
                return null;
            } catch (ServiceException | IOException ex) {
                throw ex;
            } catch ( ex) {
                LOG.error(ex.toString(), ex);
                throw new <ex>Exception();
            } finally {
                if (fs.exists(new Path(openDir, "output"))) {
                    // if export completes successfully, every file under the output directory should be
                    // owned by the current user, not the hbase service user.
                    FileStatus outputDirFileStatus = fs.getFileStatus(new Path(openDir, "output"));
                    String currentUserName = User.getCurrent().getShortName();
                    assertEquals("Unexpected file owner", currentUserName, outputDirFileStatus.getOwner());
                    FileStatus[] outputFileStatus = fs.listStatus(new Path(openDir, "output"));
                    for (FileStatus fileStatus : outputFileStatus) {
                        assertEquals("Unexpected file owner", currentUserName, fileStatus.getOwner());
                    }
                } else {
                    LOG.info("output directory doesn't exist. Skip check");
                }
                clearOutput(output);
            }
        };
        SecureTestUtil.verifyDenied(exportAction, TestSecureExport.getUserByLogin(TestSecureExport.USER_RO), TestSecureExport.getUserByLogin(TestSecureExport.USER_XO), TestSecureExport.getUserByLogin(TestSecureExport.USER_NONE));
        SecureTestUtil.verifyAllowed(exportAction, TestSecureExport.getUserByLogin(TestSecureExport.USER_ADMIN), TestSecureExport.getUserByLogin(TestSecureExport.USER_OWNER), TestSecureExport.getUserByLogin(TestSecureExport.USER_RX));
        AccessTestAction deleteAction = () -> {
            UTIL.deleteTable(TableName.valueOf(exportTable));
            return null;
        };
        SecureTestUtil.verifyAllowed(deleteAction, TestSecureExport.getUserByLogin(TestSecureExport.USER_OWNER));
        fs.delete(openDir, true);
    }

    @Test
    public void testVisibilityLabels() throws IOException, Throwable {
        final String exportTable = (name.getMethodName()) + "_export";
        final String importTable = (name.getMethodName()) + "_import";
        final TableDescriptor exportHtd = TableDescriptorBuilder.newBuilder(TableName.valueOf(exportTable)).setColumnFamily(ColumnFamilyDescriptorBuilder.of(TestSecureExport.FAMILYA)).setOwnerString(TestSecureExport.USER_OWNER).build();
        SecureTestUtil.createTable(TestSecureExport.UTIL, exportHtd, new byte[][]{ Bytes.toBytes("s") });
        AccessTestAction putAction = () -> {
            Put p1 = new Put(ROW1);
            p1.addColumn(FAMILYA, QUAL, NOW, QUAL);
            p1.setCellVisibility(new CellVisibility(SECRET));
            Put p2 = new Put(ROW2);
            p2.addColumn(FAMILYA, QUAL, NOW, QUAL);
            p2.setCellVisibility(new CellVisibility((((PRIVATE) + " & ") + (CONFIDENTIAL))));
            Put p3 = new Put(ROW3);
            p3.addColumn(FAMILYA, QUAL, NOW, QUAL);
            p3.setCellVisibility(new CellVisibility(((("!" + (CONFIDENTIAL)) + " & ") + (TOPSECRET))));
            try (Connection conn = ConnectionFactory.createConnection(UTIL.getConfiguration());Table t = conn.getTable(TableName.valueOf(exportTable))) {
                t.put(p1);
                t.put(p2);
                t.put(p3);
            }
            return null;
        };
        SecureTestUtil.verifyAllowed(putAction, TestSecureExport.getUserByLogin(TestSecureExport.USER_OWNER));
        List<Pair<List<String>, Integer>> labelsAndRowCounts = new LinkedList<>();
        labelsAndRowCounts.add(new Pair(Arrays.asList(TestSecureExport.SECRET), 1));
        labelsAndRowCounts.add(new Pair(Arrays.asList(TestSecureExport.PRIVATE, TestSecureExport.CONFIDENTIAL), 1));
        labelsAndRowCounts.add(new Pair(Arrays.asList(TestSecureExport.TOPSECRET), 1));
        labelsAndRowCounts.add(new Pair(Arrays.asList(TestSecureExport.TOPSECRET, TestSecureExport.CONFIDENTIAL), 0));
        labelsAndRowCounts.add(new Pair(Arrays.asList(TestSecureExport.TOPSECRET, TestSecureExport.CONFIDENTIAL, TestSecureExport.PRIVATE, TestSecureExport.SECRET), 2));
        for (final Pair<List<String>, Integer> labelsAndRowCount : labelsAndRowCounts) {
            final List<String> labels = labelsAndRowCount.getFirst();
            final int rowCount = labelsAndRowCount.getSecond();
            // create a open permission directory.
            final Path openDir = new Path("testAccessCase");
            final FileSystem fs = openDir.getFileSystem(TestSecureExport.UTIL.getConfiguration());
            fs.mkdirs(openDir);
            fs.setPermission(openDir, new org.apache.hadoop.fs.permission.FsPermission(FsAction.ALL, FsAction.ALL, FsAction.ALL));
            final Path output = fs.makeQualified(new Path(openDir, "output"));
            AccessTestAction exportAction = () -> {
                StringBuilder buf = new StringBuilder();
                labels.forEach(( v) -> buf.append(v).append(","));
                buf.deleteCharAt(((buf.length()) - 1));
                try {
                    String[] args = new String[]{ (("-D " + ExportUtils.EXPORT_VISIBILITY_LABELS) + "=") + (buf.toString()), exportTable, output.toString() };
                    Export.run(new Configuration(UTIL.getConfiguration()), args);
                    return null;
                } catch (ServiceException | IOException ex) {
                    throw ex;
                } catch ( ex) {
                    throw new <ex>Exception();
                }
            };
            SecureTestUtil.verifyAllowed(exportAction, TestSecureExport.getUserByLogin(TestSecureExport.USER_OWNER));
            final TableDescriptor importHtd = TableDescriptorBuilder.newBuilder(TableName.valueOf(importTable)).setColumnFamily(ColumnFamilyDescriptorBuilder.of(TestSecureExport.FAMILYB)).setOwnerString(TestSecureExport.USER_OWNER).build();
            SecureTestUtil.createTable(TestSecureExport.UTIL, importHtd, new byte[][]{ Bytes.toBytes("s") });
            AccessTestAction importAction = () -> {
                String[] args = new String[]{ (((("-D" + Import.CF_RENAME_PROP) + "=") + (FAMILYA_STRING)) + ":") + (FAMILYB_STRING), importTable, output.toString() };
                assertEquals(0, ToolRunner.run(new Configuration(UTIL.getConfiguration()), new Import(), args));
                return null;
            };
            SecureTestUtil.verifyAllowed(importAction, TestSecureExport.getUserByLogin(TestSecureExport.USER_OWNER));
            AccessTestAction scanAction = () -> {
                Scan scan = new Scan();
                scan.setAuthorizations(new Authorizations(labels));
                try (Connection conn = ConnectionFactory.createConnection(UTIL.getConfiguration());Table table = conn.getTable(importHtd.getTableName());ResultScanner scanner = table.getScanner(scan)) {
                    int count = 0;
                    for (Result r : scanner) {
                        ++count;
                    }
                    assertEquals(rowCount, count);
                }
                return null;
            };
            SecureTestUtil.verifyAllowed(scanAction, TestSecureExport.getUserByLogin(TestSecureExport.USER_OWNER));
            AccessTestAction deleteAction = () -> {
                UTIL.deleteTable(importHtd.getTableName());
                return null;
            };
            SecureTestUtil.verifyAllowed(deleteAction, TestSecureExport.getUserByLogin(TestSecureExport.USER_OWNER));
            TestSecureExport.clearOutput(output);
        }
        AccessTestAction deleteAction = () -> {
            UTIL.deleteTable(exportHtd.getTableName());
            return null;
        };
        SecureTestUtil.verifyAllowed(deleteAction, TestSecureExport.getUserByLogin(TestSecureExport.USER_OWNER));
    }
}

