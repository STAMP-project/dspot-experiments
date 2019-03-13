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


import Durability.SKIP_WAL;
import Import.FILTER_ARGS_CONF_KEY;
import Import.FILTER_CLASS_CONF_KEY;
import KeepDeletedCells.TRUE;
import TableName.META_TABLE_NAME;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.PrivateCellUtil;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.ColumnFamilyDescriptorBuilder;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.RegionInfo;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.client.TableDescriptor;
import org.apache.hadoop.hbase.client.TableDescriptorBuilder;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.FilterBase;
import org.apache.hadoop.hbase.filter.PrefixFilter;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.Import.CellImporter;
import org.apache.hadoop.hbase.regionserver.wal.WALActionsListener;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.VerySlowMapReduceTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.LauncherSecurityManager;
import org.apache.hadoop.hbase.util.MapReduceExtendedCell;
import org.apache.hadoop.hbase.wal.WAL;
import org.apache.hadoop.hbase.wal.WALEdit;
import org.apache.hadoop.hbase.wal.WALKey;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static ExportUtils.EXPORT_BATCHING;
import static ExportUtils.EXPORT_VISIBILITY_LABELS;
import static ExportUtils.RAW_SCAN;
import static Import.CF_RENAME_PROP;
import static Import.FILTER_ARGS_CONF_KEY;
import static Import.FILTER_CLASS_CONF_KEY;
import static Import.WAL_DURABILITY;
import static TableInputFormat.SCAN_ROW_START;
import static TableInputFormat.SCAN_ROW_STOP;


/**
 * Tests the table import and table export MR job functionality
 */
@Category({ VerySlowMapReduceTests.class, MediumTests.class })
public class TestImportExport {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestImportExport.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestImportExport.class);

    protected static final HBaseTestingUtility UTIL = new HBaseTestingUtility();

    private static final byte[] ROW1 = Bytes.toBytesBinary("\\x32row1");

    private static final byte[] ROW2 = Bytes.toBytesBinary("\\x32row2");

    private static final byte[] ROW3 = Bytes.toBytesBinary("\\x32row3");

    private static final String FAMILYA_STRING = "a";

    private static final String FAMILYB_STRING = "b";

    private static final byte[] FAMILYA = Bytes.toBytes(TestImportExport.FAMILYA_STRING);

    private static final byte[] FAMILYB = Bytes.toBytes(TestImportExport.FAMILYB_STRING);

    private static final byte[] QUAL = Bytes.toBytes("q");

    private static final String OUTPUT_DIR = "outputdir";

    private static String FQ_OUTPUT_DIR;

    private static final String EXPORT_BATCH_SIZE = "100";

    private static final long now = System.currentTimeMillis();

    private final TableName EXPORT_TABLE = TableName.valueOf("export_table");

    private final TableName IMPORT_TABLE = TableName.valueOf("import_table");

    @Rule
    public final TestName name = new TestName();

    /**
     * Test simple replication case with column mapping
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testSimpleCase() throws Throwable {
        try (Table t = TestImportExport.UTIL.createTable(TableName.valueOf(name.getMethodName()), TestImportExport.FAMILYA, 3)) {
            Put p = new Put(TestImportExport.ROW1);
            p.addColumn(TestImportExport.FAMILYA, TestImportExport.QUAL, TestImportExport.now, TestImportExport.QUAL);
            p.addColumn(TestImportExport.FAMILYA, TestImportExport.QUAL, ((TestImportExport.now) + 1), TestImportExport.QUAL);
            p.addColumn(TestImportExport.FAMILYA, TestImportExport.QUAL, ((TestImportExport.now) + 2), TestImportExport.QUAL);
            t.put(p);
            p = new Put(TestImportExport.ROW2);
            p.addColumn(TestImportExport.FAMILYA, TestImportExport.QUAL, TestImportExport.now, TestImportExport.QUAL);
            p.addColumn(TestImportExport.FAMILYA, TestImportExport.QUAL, ((TestImportExport.now) + 1), TestImportExport.QUAL);
            p.addColumn(TestImportExport.FAMILYA, TestImportExport.QUAL, ((TestImportExport.now) + 2), TestImportExport.QUAL);
            t.put(p);
            p = new Put(TestImportExport.ROW3);
            p.addColumn(TestImportExport.FAMILYA, TestImportExport.QUAL, TestImportExport.now, TestImportExport.QUAL);
            p.addColumn(TestImportExport.FAMILYA, TestImportExport.QUAL, ((TestImportExport.now) + 1), TestImportExport.QUAL);
            p.addColumn(TestImportExport.FAMILYA, TestImportExport.QUAL, ((TestImportExport.now) + 2), TestImportExport.QUAL);
            t.put(p);
        }
        String[] args = new String[]{ // Only export row1 & row2.
        ("-D" + (SCAN_ROW_START)) + "=\\x32row1", ("-D" + (SCAN_ROW_STOP)) + "=\\x32row3", name.getMethodName(), TestImportExport.FQ_OUTPUT_DIR, "1000"// max number of key versions per key to export
         };
        Assert.assertTrue(runExport(args));
        final String IMPORT_TABLE = (name.getMethodName()) + "import";
        try (Table t = TestImportExport.UTIL.createTable(TableName.valueOf(IMPORT_TABLE), TestImportExport.FAMILYB, 3)) {
            args = new String[]{ (((("-D" + (CF_RENAME_PROP)) + "=") + (TestImportExport.FAMILYA_STRING)) + ":") + (TestImportExport.FAMILYB_STRING), IMPORT_TABLE, TestImportExport.FQ_OUTPUT_DIR };
            Assert.assertTrue(runImport(args));
            Get g = new Get(TestImportExport.ROW1);
            g.setMaxVersions();
            Result r = t.get(g);
            Assert.assertEquals(3, r.size());
            g = new Get(TestImportExport.ROW2);
            g.setMaxVersions();
            r = t.get(g);
            Assert.assertEquals(3, r.size());
            g = new Get(TestImportExport.ROW3);
            r = t.get(g);
            Assert.assertEquals(0, r.size());
        }
    }

    /**
     * Test export hbase:meta table
     *
     * @throws Throwable
     * 		
     */
    @Test
    public void testMetaExport() throws Throwable {
        String[] args = new String[]{ META_TABLE_NAME.getNameAsString(), TestImportExport.FQ_OUTPUT_DIR, "1", "0", "0" };
        Assert.assertTrue(runExport(args));
    }

    /**
     * Test import data from 0.94 exported file
     *
     * @throws Throwable
     * 		
     */
    @Test
    public void testImport94Table() throws Throwable {
        final String name = "exportedTableIn94Format";
        URL url = TestImportExport.class.getResource(name);
        File f = new File(url.toURI());
        if (!(f.exists())) {
            TestImportExport.LOG.warn((("FAILED TO FIND " + f) + "; skipping out on test"));
            return;
        }
        Assert.assertTrue(f.exists());
        TestImportExport.LOG.info(("FILE=" + f));
        Path importPath = new Path(f.toURI());
        FileSystem fs = FileSystem.get(TestImportExport.UTIL.getConfiguration());
        fs.copyFromLocalFile(importPath, new Path((((TestImportExport.FQ_OUTPUT_DIR) + (Path.SEPARATOR)) + name)));
        String IMPORT_TABLE = name;
        try (Table t = TestImportExport.UTIL.createTable(TableName.valueOf(IMPORT_TABLE), Bytes.toBytes("f1"), 3)) {
            String[] args = new String[]{ "-Dhbase.import.version=0.94", IMPORT_TABLE, TestImportExport.FQ_OUTPUT_DIR };
            Assert.assertTrue(runImport(args));
            /* exportedTableIn94Format contains 5 rows
            ROW         COLUMN+CELL
            r1          column=f1:c1, timestamp=1383766761171, value=val1
            r2          column=f1:c1, timestamp=1383766771642, value=val2
            r3          column=f1:c1, timestamp=1383766777615, value=val3
            r4          column=f1:c1, timestamp=1383766785146, value=val4
            r5          column=f1:c1, timestamp=1383766791506, value=val5
             */
            Assert.assertEquals(5, TestImportExport.UTIL.countRows(t));
        }
    }

    /**
     * Test export scanner batching
     */
    @Test
    public void testExportScannerBatching() throws Throwable {
        TableDescriptor desc = TableDescriptorBuilder.newBuilder(TableName.valueOf(name.getMethodName())).setColumnFamily(ColumnFamilyDescriptorBuilder.newBuilder(TestImportExport.FAMILYA).setMaxVersions(1).build()).build();
        TestImportExport.UTIL.getAdmin().createTable(desc);
        try (Table t = TestImportExport.UTIL.getConnection().getTable(desc.getTableName())) {
            Put p = new Put(TestImportExport.ROW1);
            p.addColumn(TestImportExport.FAMILYA, TestImportExport.QUAL, TestImportExport.now, TestImportExport.QUAL);
            p.addColumn(TestImportExport.FAMILYA, TestImportExport.QUAL, ((TestImportExport.now) + 1), TestImportExport.QUAL);
            p.addColumn(TestImportExport.FAMILYA, TestImportExport.QUAL, ((TestImportExport.now) + 2), TestImportExport.QUAL);
            p.addColumn(TestImportExport.FAMILYA, TestImportExport.QUAL, ((TestImportExport.now) + 3), TestImportExport.QUAL);
            p.addColumn(TestImportExport.FAMILYA, TestImportExport.QUAL, ((TestImportExport.now) + 4), TestImportExport.QUAL);
            t.put(p);
            String[] args = new String[]{ (("-D" + (EXPORT_BATCHING)) + "=") + (TestImportExport.EXPORT_BATCH_SIZE)// added scanner batching arg.
            , name.getMethodName(), TestImportExport.FQ_OUTPUT_DIR };
            Assert.assertTrue(runExport(args));
            FileSystem fs = FileSystem.get(TestImportExport.UTIL.getConfiguration());
            fs.delete(new Path(TestImportExport.FQ_OUTPUT_DIR), true);
        }
    }

    @Test
    public void testWithDeletes() throws Throwable {
        TableDescriptor desc = TableDescriptorBuilder.newBuilder(TableName.valueOf(name.getMethodName())).setColumnFamily(ColumnFamilyDescriptorBuilder.newBuilder(TestImportExport.FAMILYA).setMaxVersions(5).setKeepDeletedCells(TRUE).build()).build();
        TestImportExport.UTIL.getAdmin().createTable(desc);
        try (Table t = TestImportExport.UTIL.getConnection().getTable(desc.getTableName())) {
            Put p = new Put(TestImportExport.ROW1);
            p.addColumn(TestImportExport.FAMILYA, TestImportExport.QUAL, TestImportExport.now, TestImportExport.QUAL);
            p.addColumn(TestImportExport.FAMILYA, TestImportExport.QUAL, ((TestImportExport.now) + 1), TestImportExport.QUAL);
            p.addColumn(TestImportExport.FAMILYA, TestImportExport.QUAL, ((TestImportExport.now) + 2), TestImportExport.QUAL);
            p.addColumn(TestImportExport.FAMILYA, TestImportExport.QUAL, ((TestImportExport.now) + 3), TestImportExport.QUAL);
            p.addColumn(TestImportExport.FAMILYA, TestImportExport.QUAL, ((TestImportExport.now) + 4), TestImportExport.QUAL);
            t.put(p);
            Delete d = new Delete(TestImportExport.ROW1, ((TestImportExport.now) + 3));
            t.delete(d);
            d = new Delete(TestImportExport.ROW1);
            d.addColumns(TestImportExport.FAMILYA, TestImportExport.QUAL, ((TestImportExport.now) + 2));
            t.delete(d);
        }
        String[] args = new String[]{ ("-D" + (RAW_SCAN)) + "=true", name.getMethodName(), TestImportExport.FQ_OUTPUT_DIR, "1000"// max number of key versions per key to export
         };
        Assert.assertTrue(runExport(args));
        final String IMPORT_TABLE = (name.getMethodName()) + "import";
        desc = TableDescriptorBuilder.newBuilder(TableName.valueOf(IMPORT_TABLE)).setColumnFamily(ColumnFamilyDescriptorBuilder.newBuilder(TestImportExport.FAMILYA).setMaxVersions(5).setKeepDeletedCells(TRUE).build()).build();
        TestImportExport.UTIL.getAdmin().createTable(desc);
        try (Table t = TestImportExport.UTIL.getConnection().getTable(desc.getTableName())) {
            args = new String[]{ IMPORT_TABLE, TestImportExport.FQ_OUTPUT_DIR };
            Assert.assertTrue(runImport(args));
            Scan s = new Scan();
            s.setMaxVersions();
            s.setRaw(true);
            ResultScanner scanner = t.getScanner(s);
            Result r = scanner.next();
            Cell[] res = r.rawCells();
            Assert.assertTrue(PrivateCellUtil.isDeleteFamily(res[0]));
            Assert.assertEquals(((TestImportExport.now) + 4), res[1].getTimestamp());
            Assert.assertEquals(((TestImportExport.now) + 3), res[2].getTimestamp());
            Assert.assertTrue(CellUtil.isDelete(res[3]));
            Assert.assertEquals(((TestImportExport.now) + 2), res[4].getTimestamp());
            Assert.assertEquals(((TestImportExport.now) + 1), res[5].getTimestamp());
            Assert.assertEquals(TestImportExport.now, res[6].getTimestamp());
        }
    }

    @Test
    public void testWithMultipleDeleteFamilyMarkersOfSameRowSameFamily() throws Throwable {
        final TableName exportTable = TableName.valueOf(name.getMethodName());
        TableDescriptor desc = TableDescriptorBuilder.newBuilder(TableName.valueOf(name.getMethodName())).setColumnFamily(ColumnFamilyDescriptorBuilder.newBuilder(TestImportExport.FAMILYA).setMaxVersions(5).setKeepDeletedCells(TRUE).build()).build();
        TestImportExport.UTIL.getAdmin().createTable(desc);
        Table exportT = TestImportExport.UTIL.getConnection().getTable(exportTable);
        // Add first version of QUAL
        Put p = new Put(TestImportExport.ROW1);
        p.addColumn(TestImportExport.FAMILYA, TestImportExport.QUAL, TestImportExport.now, TestImportExport.QUAL);
        exportT.put(p);
        // Add Delete family marker
        Delete d = new Delete(TestImportExport.ROW1, ((TestImportExport.now) + 3));
        exportT.delete(d);
        // Add second version of QUAL
        p = new Put(TestImportExport.ROW1);
        p.addColumn(TestImportExport.FAMILYA, TestImportExport.QUAL, ((TestImportExport.now) + 5), Bytes.toBytes("s"));
        exportT.put(p);
        // Add second Delete family marker
        d = new Delete(TestImportExport.ROW1, ((TestImportExport.now) + 7));
        exportT.delete(d);
        String[] args = new String[]{ ("-D" + (RAW_SCAN)) + "=true", exportTable.getNameAsString(), TestImportExport.FQ_OUTPUT_DIR, "1000"// max number of key versions per key to export
         };
        Assert.assertTrue(runExport(args));
        final String importTable = (name.getMethodName()) + "import";
        desc = TableDescriptorBuilder.newBuilder(TableName.valueOf(importTable)).setColumnFamily(ColumnFamilyDescriptorBuilder.newBuilder(TestImportExport.FAMILYA).setMaxVersions(5).setKeepDeletedCells(TRUE).build()).build();
        TestImportExport.UTIL.getAdmin().createTable(desc);
        Table importT = TestImportExport.UTIL.getConnection().getTable(TableName.valueOf(importTable));
        args = new String[]{ importTable, TestImportExport.FQ_OUTPUT_DIR };
        Assert.assertTrue(runImport(args));
        Scan s = new Scan();
        s.setMaxVersions();
        s.setRaw(true);
        ResultScanner importedTScanner = importT.getScanner(s);
        Result importedTResult = importedTScanner.next();
        ResultScanner exportedTScanner = exportT.getScanner(s);
        Result exportedTResult = exportedTScanner.next();
        try {
            Result.compareResults(exportedTResult, importedTResult);
        } catch (Throwable e) {
            Assert.fail(("Original and imported tables data comparision failed with error:" + (e.getMessage())));
        } finally {
            exportT.close();
            importT.close();
        }
    }

    /**
     * Create a simple table, run an Export Job on it, Import with filtering on,  verify counts,
     * attempt with invalid values.
     */
    @Test
    public void testWithFilter() throws Throwable {
        // Create simple table to export
        TableDescriptor desc = TableDescriptorBuilder.newBuilder(TableName.valueOf(name.getMethodName())).setColumnFamily(ColumnFamilyDescriptorBuilder.newBuilder(TestImportExport.FAMILYA).setMaxVersions(5).build()).build();
        TestImportExport.UTIL.getAdmin().createTable(desc);
        Table exportTable = TestImportExport.UTIL.getConnection().getTable(desc.getTableName());
        Put p1 = new Put(TestImportExport.ROW1);
        p1.addColumn(TestImportExport.FAMILYA, TestImportExport.QUAL, TestImportExport.now, TestImportExport.QUAL);
        p1.addColumn(TestImportExport.FAMILYA, TestImportExport.QUAL, ((TestImportExport.now) + 1), TestImportExport.QUAL);
        p1.addColumn(TestImportExport.FAMILYA, TestImportExport.QUAL, ((TestImportExport.now) + 2), TestImportExport.QUAL);
        p1.addColumn(TestImportExport.FAMILYA, TestImportExport.QUAL, ((TestImportExport.now) + 3), TestImportExport.QUAL);
        p1.addColumn(TestImportExport.FAMILYA, TestImportExport.QUAL, ((TestImportExport.now) + 4), TestImportExport.QUAL);
        // Having another row would actually test the filter.
        Put p2 = new Put(TestImportExport.ROW2);
        p2.addColumn(TestImportExport.FAMILYA, TestImportExport.QUAL, TestImportExport.now, TestImportExport.QUAL);
        exportTable.put(Arrays.asList(p1, p2));
        // Export the simple table
        String[] args = new String[]{ name.getMethodName(), TestImportExport.FQ_OUTPUT_DIR, "1000" };
        Assert.assertTrue(runExport(args));
        // Import to a new table
        final String IMPORT_TABLE = (name.getMethodName()) + "import";
        desc = TableDescriptorBuilder.newBuilder(TableName.valueOf(IMPORT_TABLE)).setColumnFamily(ColumnFamilyDescriptorBuilder.newBuilder(TestImportExport.FAMILYA).setMaxVersions(5).build()).build();
        TestImportExport.UTIL.getAdmin().createTable(desc);
        Table importTable = TestImportExport.UTIL.getConnection().getTable(desc.getTableName());
        args = new String[]{ (("-D" + (FILTER_CLASS_CONF_KEY)) + "=") + (PrefixFilter.class.getName()), (("-D" + (FILTER_ARGS_CONF_KEY)) + "=") + (Bytes.toString(TestImportExport.ROW1)), IMPORT_TABLE, TestImportExport.FQ_OUTPUT_DIR, "1000" };
        Assert.assertTrue(runImport(args));
        // get the count of the source table for that time range
        PrefixFilter filter = new PrefixFilter(TestImportExport.ROW1);
        int count = getCount(exportTable, filter);
        Assert.assertEquals("Unexpected row count between export and import tables", count, getCount(importTable, null));
        // and then test that a broken command doesn't bork everything - easier here because we don't
        // need to re-run the export job
        args = new String[]{ (("-D" + (FILTER_CLASS_CONF_KEY)) + "=") + (Filter.class.getName()), ((("-D" + (FILTER_ARGS_CONF_KEY)) + "=") + (Bytes.toString(TestImportExport.ROW1))) + "", name.getMethodName(), TestImportExport.FQ_OUTPUT_DIR, "1000" };
        Assert.assertFalse(runImport(args));
        // cleanup
        exportTable.close();
        importTable.close();
    }

    /**
     * test main method. Import should print help and call System.exit
     */
    @Test
    public void testImportMain() throws Throwable {
        PrintStream oldPrintStream = System.err;
        SecurityManager SECURITY_MANAGER = System.getSecurityManager();
        LauncherSecurityManager newSecurityManager = new LauncherSecurityManager();
        System.setSecurityManager(newSecurityManager);
        ByteArrayOutputStream data = new ByteArrayOutputStream();
        String[] args = new String[]{  };
        System.setErr(new PrintStream(data));
        try {
            System.setErr(new PrintStream(data));
            Import.main(args);
            Assert.fail("should be SecurityException");
        } catch (SecurityException e) {
            Assert.assertEquals((-1), newSecurityManager.getExitCode());
            Assert.assertTrue(data.toString().contains("Wrong number of arguments:"));
            Assert.assertTrue(data.toString().contains("-Dimport.bulk.output=/path/for/output"));
            Assert.assertTrue(data.toString().contains("-Dimport.filter.class=<name of filter class>"));
            Assert.assertTrue(data.toString().contains("-Dimport.bulk.output=/path/for/output"));
            Assert.assertTrue(data.toString().contains("-Dmapreduce.reduce.speculative=false"));
        } finally {
            System.setErr(oldPrintStream);
            System.setSecurityManager(SECURITY_MANAGER);
        }
    }

    @Test
    public void testExportScan() throws Exception {
        int version = 100;
        long startTime = System.currentTimeMillis();
        long endTime = startTime + 1;
        String prefix = "row";
        String label_0 = "label_0";
        String label_1 = "label_1";
        String[] args = new String[]{ "table", "outputDir", String.valueOf(version), String.valueOf(startTime), String.valueOf(endTime), prefix };
        Scan scan = ExportUtils.getScanFromCommandLine(TestImportExport.UTIL.getConfiguration(), args);
        Assert.assertEquals(version, scan.getMaxVersions());
        Assert.assertEquals(startTime, scan.getTimeRange().getMin());
        Assert.assertEquals(endTime, scan.getTimeRange().getMax());
        Assert.assertEquals(true, ((scan.getFilter()) instanceof PrefixFilter));
        Assert.assertEquals(0, Bytes.compareTo(getPrefix(), Bytes.toBytesBinary(prefix)));
        String[] argsWithLabels = new String[]{ (((("-D " + (EXPORT_VISIBILITY_LABELS)) + "=") + label_0) + ",") + label_1, "table", "outputDir", String.valueOf(version), String.valueOf(startTime), String.valueOf(endTime), prefix };
        Configuration conf = new Configuration(TestImportExport.UTIL.getConfiguration());
        // parse the "-D" options
        String[] otherArgs = getRemainingArgs();
        Scan scanWithLabels = ExportUtils.getScanFromCommandLine(conf, otherArgs);
        Assert.assertEquals(version, scanWithLabels.getMaxVersions());
        Assert.assertEquals(startTime, scanWithLabels.getTimeRange().getMin());
        Assert.assertEquals(endTime, scanWithLabels.getTimeRange().getMax());
        Assert.assertEquals(true, ((scanWithLabels.getFilter()) instanceof PrefixFilter));
        Assert.assertEquals(0, Bytes.compareTo(getPrefix(), Bytes.toBytesBinary(prefix)));
        Assert.assertEquals(2, scanWithLabels.getAuthorizations().getLabels().size());
        Assert.assertEquals(label_0, scanWithLabels.getAuthorizations().getLabels().get(0));
        Assert.assertEquals(label_1, scanWithLabels.getAuthorizations().getLabels().get(1));
    }

    /**
     * test main method. Export should print help and call System.exit
     */
    @Test
    public void testExportMain() throws Throwable {
        PrintStream oldPrintStream = System.err;
        SecurityManager SECURITY_MANAGER = System.getSecurityManager();
        LauncherSecurityManager newSecurityManager = new LauncherSecurityManager();
        System.setSecurityManager(newSecurityManager);
        ByteArrayOutputStream data = new ByteArrayOutputStream();
        String[] args = new String[]{  };
        System.setErr(new PrintStream(data));
        try {
            System.setErr(new PrintStream(data));
            runExportMain(args);
            Assert.fail("should be SecurityException");
        } catch (SecurityException e) {
            Assert.assertEquals((-1), newSecurityManager.getExitCode());
            String errMsg = data.toString();
            Assert.assertTrue(errMsg.contains("Wrong number of arguments:"));
            Assert.assertTrue(errMsg.contains(("Usage: Export [-D <property=value>]* <tablename> <outputdir> [<versions> " + "[<starttime> [<endtime>]] [^[regex pattern] or [Prefix] to filter]]")));
            Assert.assertTrue(errMsg.contains("-D hbase.mapreduce.scan.column.family=<family1>,<family2>, ..."));
            Assert.assertTrue(errMsg.contains("-D hbase.mapreduce.include.deleted.rows=true"));
            Assert.assertTrue(errMsg.contains("-D hbase.client.scanner.caching=100"));
            Assert.assertTrue(errMsg.contains("-D hbase.export.scanner.batch=10"));
            Assert.assertTrue(errMsg.contains("-D hbase.export.scanner.caching=100"));
        } finally {
            System.setErr(oldPrintStream);
            System.setSecurityManager(SECURITY_MANAGER);
        }
    }

    /**
     * Test map method of Importer
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void testKeyValueImporter() throws Throwable {
        CellImporter importer = new CellImporter();
        Configuration configuration = new Configuration();
        Context ctx = Mockito.mock(Context.class);
        Mockito.when(ctx.getConfiguration()).thenReturn(configuration);
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                ImmutableBytesWritable writer = ((ImmutableBytesWritable) (invocation.getArgument(0)));
                MapReduceExtendedCell key = ((MapReduceExtendedCell) (invocation.getArgument(1)));
                Assert.assertEquals("Key", Bytes.toString(writer.get()));
                Assert.assertEquals("row", Bytes.toString(CellUtil.cloneRow(key)));
                return null;
            }
        }).when(ctx).write(ArgumentMatchers.any(), ArgumentMatchers.any());
        importer.setup(ctx);
        Result value = Mockito.mock(Result.class);
        KeyValue[] keys = new KeyValue[]{ new KeyValue(Bytes.toBytes("row"), Bytes.toBytes("family"), Bytes.toBytes("qualifier"), Bytes.toBytes("value")), new KeyValue(Bytes.toBytes("row"), Bytes.toBytes("family"), Bytes.toBytes("qualifier"), Bytes.toBytes("value1")) };
        Mockito.when(value.rawCells()).thenReturn(keys);
        importer.map(new ImmutableBytesWritable(Bytes.toBytes("Key")), value, ctx);
    }

    /**
     * Test addFilterAndArguments method of Import This method set couple
     * parameters into Configuration
     */
    @Test
    public void testAddFilterAndArguments() throws IOException {
        Configuration configuration = new Configuration();
        List<String> args = new ArrayList<>();
        args.add("param1");
        args.add("param2");
        Import.addFilterAndArguments(configuration, FilterBase.class, args);
        Assert.assertEquals("org.apache.hadoop.hbase.filter.FilterBase", configuration.get(FILTER_CLASS_CONF_KEY));
        Assert.assertEquals("param1,param2", configuration.get(FILTER_ARGS_CONF_KEY));
    }

    @Test
    public void testDurability() throws Throwable {
        // Create an export table.
        String exportTableName = (name.getMethodName()) + "export";
        try (Table exportTable = TestImportExport.UTIL.createTable(TableName.valueOf(exportTableName), TestImportExport.FAMILYA, 3)) {
            // Insert some data
            Put put = new Put(TestImportExport.ROW1);
            put.addColumn(TestImportExport.FAMILYA, TestImportExport.QUAL, TestImportExport.now, TestImportExport.QUAL);
            put.addColumn(TestImportExport.FAMILYA, TestImportExport.QUAL, ((TestImportExport.now) + 1), TestImportExport.QUAL);
            put.addColumn(TestImportExport.FAMILYA, TestImportExport.QUAL, ((TestImportExport.now) + 2), TestImportExport.QUAL);
            exportTable.put(put);
            put = new Put(TestImportExport.ROW2);
            put.addColumn(TestImportExport.FAMILYA, TestImportExport.QUAL, TestImportExport.now, TestImportExport.QUAL);
            put.addColumn(TestImportExport.FAMILYA, TestImportExport.QUAL, ((TestImportExport.now) + 1), TestImportExport.QUAL);
            put.addColumn(TestImportExport.FAMILYA, TestImportExport.QUAL, ((TestImportExport.now) + 2), TestImportExport.QUAL);
            exportTable.put(put);
            // Run the export
            String[] args = new String[]{ exportTableName, TestImportExport.FQ_OUTPUT_DIR, "1000" };
            Assert.assertTrue(runExport(args));
            // Create the table for import
            String importTableName = (name.getMethodName()) + "import1";
            Table importTable = TestImportExport.UTIL.createTable(TableName.valueOf(importTableName), TestImportExport.FAMILYA, 3);
            // Register the wal listener for the import table
            RegionInfo region = TestImportExport.UTIL.getHBaseCluster().getRegionServerThreads().get(0).getRegionServer().getRegions(importTable.getName()).get(0).getRegionInfo();
            TestImportExport.TableWALActionListener walListener = new TestImportExport.TableWALActionListener(region);
            WAL wal = TestImportExport.UTIL.getMiniHBaseCluster().getRegionServer(0).getWAL(region);
            wal.registerWALActionsListener(walListener);
            // Run the import with SKIP_WAL
            args = new String[]{ (("-D" + (WAL_DURABILITY)) + "=") + (SKIP_WAL.name()), importTableName, TestImportExport.FQ_OUTPUT_DIR };
            Assert.assertTrue(runImport(args));
            // Assert that the wal is not visisted
            Assert.assertTrue((!(walListener.isWALVisited())));
            // Ensure that the count is 2 (only one version of key value is obtained)
            Assert.assertTrue(((getCount(importTable, null)) == 2));
            // Run the import with the default durability option
            importTableName = (name.getMethodName()) + "import2";
            importTable = TestImportExport.UTIL.createTable(TableName.valueOf(importTableName), TestImportExport.FAMILYA, 3);
            region = TestImportExport.UTIL.getHBaseCluster().getRegionServerThreads().get(0).getRegionServer().getRegions(importTable.getName()).get(0).getRegionInfo();
            wal = TestImportExport.UTIL.getMiniHBaseCluster().getRegionServer(0).getWAL(region);
            walListener = new TestImportExport.TableWALActionListener(region);
            wal.registerWALActionsListener(walListener);
            args = new String[]{ importTableName, TestImportExport.FQ_OUTPUT_DIR };
            Assert.assertTrue(runImport(args));
            // Assert that the wal is visisted
            Assert.assertTrue(walListener.isWALVisited());
            // Ensure that the count is 2 (only one version of key value is obtained)
            Assert.assertTrue(((getCount(importTable, null)) == 2));
        }
    }

    /**
     * This listens to the {@link #visitLogEntryBeforeWrite(RegionInfo, WALKey, WALEdit)} to
     * identify that an entry is written to the Write Ahead Log for the given table.
     */
    private static class TableWALActionListener implements WALActionsListener {
        private RegionInfo regionInfo;

        private boolean isVisited = false;

        public TableWALActionListener(RegionInfo region) {
            this.regionInfo = region;
        }

        @Override
        public void visitLogEntryBeforeWrite(WALKey logKey, WALEdit logEdit) {
            if ((logKey.getTableName().getNameAsString().equalsIgnoreCase(this.regionInfo.getTable().getNameAsString())) && (!(logEdit.isMetaEdit()))) {
                isVisited = true;
            }
        }

        public boolean isWALVisited() {
            return isVisited;
        }
    }
}

