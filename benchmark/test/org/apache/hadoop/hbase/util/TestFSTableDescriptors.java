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
package org.apache.hadoop.hbase.util;


import HConstants.HBASE_TEMP_DIRECTORY;
import TableName.META_TABLE_NAME;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableDescriptors;
import org.apache.hadoop.hbase.TableExistsException;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.ColumnFamilyDescriptorBuilder;
import org.apache.hadoop.hbase.client.TableDescriptor;
import org.apache.hadoop.hbase.client.TableDescriptorBuilder;
import org.apache.hadoop.hbase.exceptions.DeserializationException;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.MiscTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static FSTableDescriptors.TABLEINFO_FILESTATUS_COMPARATOR;
import static FSTableDescriptors.TABLEINFO_FILE_PREFIX;
import static FSTableDescriptors.TMP_DIR;
import static FSTableDescriptors.WIDTH_OF_SEQUENCE_ID;


/**
 * Tests for {@link FSTableDescriptors}.
 */
// Do not support to be executed in he same JVM as other tests
@Category({ MiscTests.class, MediumTests.class })
public class TestFSTableDescriptors {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestFSTableDescriptors.class);

    private static final HBaseTestingUtility UTIL = new HBaseTestingUtility();

    private static final Logger LOG = LoggerFactory.getLogger(TestFSTableDescriptors.class);

    @Rule
    public TestName name = new TestName();

    @Test(expected = IllegalArgumentException.class)
    public void testRegexAgainstOldStyleTableInfo() {
        Path p = new Path("/tmp", TABLEINFO_FILE_PREFIX);
        int i = FSTableDescriptors.getTableInfoSequenceId(p);
        Assert.assertEquals(0, i);
        // Assert it won't eat garbage -- that it fails
        p = new Path("/tmp", "abc");
        FSTableDescriptors.getTableInfoSequenceId(p);
    }

    @Test
    public void testCreateAndUpdate() throws IOException {
        Path testdir = getDataTestDir(name.getMethodName());
        TableDescriptor htd = TableDescriptorBuilder.newBuilder(TableName.valueOf(name.getMethodName())).build();
        FileSystem fs = FileSystem.get(TestFSTableDescriptors.UTIL.getConfiguration());
        FSTableDescriptors fstd = new FSTableDescriptors(TestFSTableDescriptors.UTIL.getConfiguration(), fs, testdir);
        Assert.assertTrue(fstd.createTableDescriptor(htd));
        Assert.assertFalse(fstd.createTableDescriptor(htd));
        FileStatus[] statuses = fs.listStatus(testdir);
        Assert.assertTrue(("statuses.length=" + (statuses.length)), ((statuses.length) == 1));
        for (int i = 0; i < 10; i++) {
            fstd.updateTableDescriptor(htd);
        }
        statuses = fs.listStatus(testdir);
        Assert.assertTrue(((statuses.length) == 1));
        Path tmpTableDir = new Path(FSUtils.getTableDir(testdir, htd.getTableName()), ".tmp");
        statuses = fs.listStatus(tmpTableDir);
        Assert.assertTrue(((statuses.length) == 0));
    }

    @Test
    public void testSequenceIdAdvancesOnTableInfo() throws IOException {
        Path testdir = getDataTestDir(name.getMethodName());
        TableDescriptor htd = TableDescriptorBuilder.newBuilder(TableName.valueOf(name.getMethodName())).build();
        FileSystem fs = FileSystem.get(TestFSTableDescriptors.UTIL.getConfiguration());
        FSTableDescriptors fstd = new FSTableDescriptors(TestFSTableDescriptors.UTIL.getConfiguration(), fs, testdir);
        Path p0 = fstd.updateTableDescriptor(htd);
        int i0 = FSTableDescriptors.getTableInfoSequenceId(p0);
        Path p1 = fstd.updateTableDescriptor(htd);
        // Assert we cleaned up the old file.
        Assert.assertTrue((!(fs.exists(p0))));
        int i1 = FSTableDescriptors.getTableInfoSequenceId(p1);
        Assert.assertTrue((i1 == (i0 + 1)));
        Path p2 = fstd.updateTableDescriptor(htd);
        // Assert we cleaned up the old file.
        Assert.assertTrue((!(fs.exists(p1))));
        int i2 = FSTableDescriptors.getTableInfoSequenceId(p2);
        Assert.assertTrue((i2 == (i1 + 1)));
        Path p3 = fstd.updateTableDescriptor(htd);
        // Assert we cleaned up the old file.
        Assert.assertTrue((!(fs.exists(p2))));
        int i3 = FSTableDescriptors.getTableInfoSequenceId(p3);
        Assert.assertTrue((i3 == (i2 + 1)));
        TableDescriptor descriptor = fstd.get(htd.getTableName());
        Assert.assertEquals(descriptor, htd);
    }

    @Test
    public void testFormatTableInfoSequenceId() {
        Path p0 = assertWriteAndReadSequenceId(0);
        // Assert p0 has format we expect.
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < (WIDTH_OF_SEQUENCE_ID); i++) {
            sb.append("0");
        }
        Assert.assertEquals((((TABLEINFO_FILE_PREFIX) + ".") + (sb.toString())), p0.getName());
        // Check a few more.
        Path p2 = assertWriteAndReadSequenceId(2);
        Path p10000 = assertWriteAndReadSequenceId(10000);
        // Get a .tablinfo that has no sequenceid suffix.
        Path p = new Path(p0.getParent(), TABLEINFO_FILE_PREFIX);
        FileStatus fs = new FileStatus(0, false, 0, 0, 0, p);
        FileStatus fs0 = new FileStatus(0, false, 0, 0, 0, p0);
        FileStatus fs2 = new FileStatus(0, false, 0, 0, 0, p2);
        FileStatus fs10000 = new FileStatus(0, false, 0, 0, 0, p10000);
        Comparator<FileStatus> comparator = TABLEINFO_FILESTATUS_COMPARATOR;
        Assert.assertTrue(((comparator.compare(fs, fs0)) > 0));
        Assert.assertTrue(((comparator.compare(fs0, fs2)) > 0));
        Assert.assertTrue(((comparator.compare(fs2, fs10000)) > 0));
    }

    @Test
    public void testRemoves() throws IOException {
        final String name = this.name.getMethodName();
        FileSystem fs = FileSystem.get(TestFSTableDescriptors.UTIL.getConfiguration());
        // Cleanup old tests if any detrius laying around.
        Path rootdir = new Path(TestFSTableDescriptors.UTIL.getDataTestDir(), name);
        TableDescriptors htds = new FSTableDescriptors(TestFSTableDescriptors.UTIL.getConfiguration(), fs, rootdir);
        TableDescriptor htd = TableDescriptorBuilder.newBuilder(TableName.valueOf(name)).build();
        htds.add(htd);
        Assert.assertNotNull(htds.remove(htd.getTableName()));
        Assert.assertNull(htds.remove(htd.getTableName()));
    }

    @Test
    public void testReadingHTDFromFS() throws IOException {
        final String name = this.name.getMethodName();
        FileSystem fs = FileSystem.get(TestFSTableDescriptors.UTIL.getConfiguration());
        TableDescriptor htd = TableDescriptorBuilder.newBuilder(TableName.valueOf(name)).build();
        Path rootdir = getDataTestDir(name);
        FSTableDescriptors fstd = new FSTableDescriptors(TestFSTableDescriptors.UTIL.getConfiguration(), fs, rootdir);
        fstd.createTableDescriptor(htd);
        TableDescriptor td2 = FSTableDescriptors.getTableDescriptorFromFs(fs, rootdir, htd.getTableName());
        Assert.assertTrue(htd.equals(td2));
    }

    @Test
    public void testReadingOldHTDFromFS() throws IOException, DeserializationException {
        final String name = this.name.getMethodName();
        FileSystem fs = FileSystem.get(TestFSTableDescriptors.UTIL.getConfiguration());
        Path rootdir = getDataTestDir(name);
        FSTableDescriptors fstd = new FSTableDescriptors(TestFSTableDescriptors.UTIL.getConfiguration(), fs, rootdir);
        TableDescriptor htd = TableDescriptorBuilder.newBuilder(TableName.valueOf(name)).build();
        Path descriptorFile = fstd.updateTableDescriptor(htd);
        try (FSDataOutputStream out = fs.create(descriptorFile, true)) {
            out.write(TableDescriptorBuilder.toByteArray(htd));
        }
        FSTableDescriptors fstd2 = new FSTableDescriptors(TestFSTableDescriptors.UTIL.getConfiguration(), fs, rootdir);
        TableDescriptor td2 = fstd2.get(htd.getTableName());
        Assert.assertEquals(htd, td2);
        FileStatus descriptorFile2 = FSTableDescriptors.getTableInfoPath(fs, fstd2.getTableDir(htd.getTableName()));
        byte[] buffer = TableDescriptorBuilder.toByteArray(htd);
        try (FSDataInputStream in = fs.open(descriptorFile2.getPath())) {
            in.readFully(buffer);
        }
        TableDescriptor td3 = TableDescriptorBuilder.parseFrom(buffer);
        Assert.assertEquals(htd, td3);
    }

    @Test
    public void testTableDescriptors() throws IOException, InterruptedException {
        final String name = this.name.getMethodName();
        FileSystem fs = FileSystem.get(TestFSTableDescriptors.UTIL.getConfiguration());
        // Cleanup old tests if any debris laying around.
        Path rootdir = new Path(TestFSTableDescriptors.UTIL.getDataTestDir(), name);
        FSTableDescriptors htds = new FSTableDescriptors(TestFSTableDescriptors.UTIL.getConfiguration(), fs, rootdir) {
            @Override
            public TableDescriptor get(TableName tablename) throws FileNotFoundException, IOException, TableExistsException {
                TestFSTableDescriptors.LOG.info(((tablename + ", cachehits=") + (this.cachehits)));
                return super.get(tablename);
            }
        };
        final int count = 10;
        // Write out table infos.
        for (int i = 0; i < count; i++) {
            htds.createTableDescriptor(TableDescriptorBuilder.newBuilder(TableName.valueOf((name + i))).build());
        }
        for (int i = 0; i < count; i++) {
            Assert.assertTrue(((htds.get(TableName.valueOf((name + i)))) != null));
        }
        for (int i = 0; i < count; i++) {
            Assert.assertTrue(((htds.get(TableName.valueOf((name + i)))) != null));
        }
        // Update the table infos
        for (int i = 0; i < count; i++) {
            TableDescriptorBuilder builder = TableDescriptorBuilder.newBuilder(TableName.valueOf((name + i)));
            builder.setColumnFamily(ColumnFamilyDescriptorBuilder.of(("" + i)));
            htds.updateTableDescriptor(builder.build());
        }
        // Wait a while so mod time we write is for sure different.
        Thread.sleep(100);
        for (int i = 0; i < count; i++) {
            Assert.assertTrue(((htds.get(TableName.valueOf((name + i)))) != null));
        }
        for (int i = 0; i < count; i++) {
            Assert.assertTrue(((htds.get(TableName.valueOf((name + i)))) != null));
        }
        Assert.assertEquals((count * 4), htds.invocations);
        Assert.assertTrue(((("expected=" + (count * 2)) + ", actual=") + (htds.cachehits)), ((htds.cachehits) >= (count * 2)));
    }

    @Test
    public void testTableDescriptorsNoCache() throws IOException, InterruptedException {
        final String name = this.name.getMethodName();
        FileSystem fs = FileSystem.get(TestFSTableDescriptors.UTIL.getConfiguration());
        // Cleanup old tests if any debris laying around.
        Path rootdir = new Path(TestFSTableDescriptors.UTIL.getDataTestDir(), name);
        FSTableDescriptors htds = new TestFSTableDescriptors.FSTableDescriptorsTest(TestFSTableDescriptors.UTIL.getConfiguration(), fs, rootdir, false, false);
        final int count = 10;
        // Write out table infos.
        for (int i = 0; i < count; i++) {
            htds.createTableDescriptor(TableDescriptorBuilder.newBuilder(TableName.valueOf((name + i))).build());
        }
        for (int i = 0; i < (2 * count); i++) {
            Assert.assertNotNull("Expected HTD, got null instead", htds.get(TableName.valueOf((name + (i % 2)))));
        }
        // Update the table infos
        for (int i = 0; i < count; i++) {
            TableDescriptorBuilder builder = TableDescriptorBuilder.newBuilder(TableName.valueOf((name + i)));
            builder.setColumnFamily(ColumnFamilyDescriptorBuilder.of(("" + i)));
            htds.updateTableDescriptor(builder.build());
        }
        for (int i = 0; i < count; i++) {
            Assert.assertNotNull("Expected HTD, got null instead", htds.get(TableName.valueOf((name + i))));
            Assert.assertTrue((("Column Family " + i) + " missing"), htds.get(TableName.valueOf((name + i))).hasColumnFamily(Bytes.toBytes(("" + i))));
        }
        Assert.assertEquals((count * 4), htds.invocations);
        Assert.assertEquals(("expected=0, actual=" + (htds.cachehits)), 0, htds.cachehits);
    }

    @Test
    public void testGetAll() throws IOException, InterruptedException {
        final String name = "testGetAll";
        FileSystem fs = FileSystem.get(TestFSTableDescriptors.UTIL.getConfiguration());
        // Cleanup old tests if any debris laying around.
        Path rootdir = new Path(TestFSTableDescriptors.UTIL.getDataTestDir(), name);
        FSTableDescriptors htds = new TestFSTableDescriptors.FSTableDescriptorsTest(TestFSTableDescriptors.UTIL.getConfiguration(), fs, rootdir);
        final int count = 4;
        // Write out table infos.
        for (int i = 0; i < count; i++) {
            htds.createTableDescriptor(TableDescriptorBuilder.newBuilder(TableName.valueOf((name + i))).build());
        }
        // add hbase:meta
        htds.createTableDescriptor(TableDescriptorBuilder.newBuilder(META_TABLE_NAME).build());
        Assert.assertEquals(((("getAll() didn't return all TableDescriptors, expected: " + (count + 1)) + " got: ") + (htds.getAll().size())), (count + 1), htds.getAll().size());
    }

    @Test
    public void testCacheConsistency() throws IOException, InterruptedException {
        final String name = this.name.getMethodName();
        FileSystem fs = FileSystem.get(TestFSTableDescriptors.UTIL.getConfiguration());
        // Cleanup old tests if any debris laying around.
        Path rootdir = new Path(TestFSTableDescriptors.UTIL.getDataTestDir(), name);
        FSTableDescriptors chtds = new TestFSTableDescriptors.FSTableDescriptorsTest(TestFSTableDescriptors.UTIL.getConfiguration(), fs, rootdir);
        FSTableDescriptors nonchtds = new TestFSTableDescriptors.FSTableDescriptorsTest(TestFSTableDescriptors.UTIL.getConfiguration(), fs, rootdir, false, false);
        final int count = 10;
        // Write out table infos via non-cached FSTableDescriptors
        for (int i = 0; i < count; i++) {
            nonchtds.createTableDescriptor(TableDescriptorBuilder.newBuilder(TableName.valueOf((name + i))).build());
        }
        // Calls to getAll() won't increase the cache counter, do per table.
        for (int i = 0; i < count; i++) {
            Assert.assertTrue(((chtds.get(TableName.valueOf((name + i)))) != null));
        }
        Assert.assertTrue(((nonchtds.getAll().size()) == (chtds.getAll().size())));
        // add a new entry for hbase:meta
        TableDescriptor htd = TableDescriptorBuilder.newBuilder(META_TABLE_NAME).build();
        nonchtds.createTableDescriptor(htd);
        // hbase:meta will only increase the cachehit by 1
        Assert.assertTrue(((nonchtds.getAll().size()) == (chtds.getAll().size())));
        for (Map.Entry<String, TableDescriptor> entry : nonchtds.getAll().entrySet()) {
            String t = ((String) (entry.getKey()));
            TableDescriptor nchtd = entry.getValue();
            Assert.assertTrue(((("expected " + (htd.toString())) + " got: ") + (chtds.get(TableName.valueOf(t)).toString())), nchtd.equals(chtds.get(TableName.valueOf(t))));
        }
    }

    @Test
    public void testNoSuchTable() throws IOException {
        final String name = "testNoSuchTable";
        FileSystem fs = FileSystem.get(TestFSTableDescriptors.UTIL.getConfiguration());
        // Cleanup old tests if any detrius laying around.
        Path rootdir = new Path(TestFSTableDescriptors.UTIL.getDataTestDir(), name);
        TableDescriptors htds = new FSTableDescriptors(TestFSTableDescriptors.UTIL.getConfiguration(), fs, rootdir);
        Assert.assertNull("There shouldn't be any HTD for this table", htds.get(TableName.valueOf("NoSuchTable")));
    }

    @Test
    public void testUpdates() throws IOException {
        final String name = "testUpdates";
        FileSystem fs = FileSystem.get(TestFSTableDescriptors.UTIL.getConfiguration());
        // Cleanup old tests if any detrius laying around.
        Path rootdir = new Path(TestFSTableDescriptors.UTIL.getDataTestDir(), name);
        TableDescriptors htds = new FSTableDescriptors(TestFSTableDescriptors.UTIL.getConfiguration(), fs, rootdir);
        TableDescriptor htd = TableDescriptorBuilder.newBuilder(TableName.valueOf(name)).build();
        htds.add(htd);
        htds.add(htd);
        htds.add(htd);
    }

    @Test
    public void testTableInfoFileStatusComparator() {
        FileStatus bare = new FileStatus(0, false, 0, 0, (-1), new Path("/tmp", TABLEINFO_FILE_PREFIX));
        FileStatus future = new FileStatus(0, false, 0, 0, (-1), new Path(("/tmp/tablinfo." + (System.currentTimeMillis()))));
        FileStatus farFuture = new FileStatus(0, false, 0, 0, (-1), new Path((("/tmp/tablinfo." + (System.currentTimeMillis())) + 1000)));
        FileStatus[] alist = new FileStatus[]{ bare, future, farFuture };
        FileStatus[] blist = new FileStatus[]{ bare, farFuture, future };
        FileStatus[] clist = new FileStatus[]{ farFuture, bare, future };
        Comparator<FileStatus> c = TABLEINFO_FILESTATUS_COMPARATOR;
        Arrays.sort(alist, c);
        Arrays.sort(blist, c);
        Arrays.sort(clist, c);
        // Now assert all sorted same in way we want.
        for (int i = 0; i < (alist.length); i++) {
            Assert.assertTrue(alist[i].equals(blist[i]));
            Assert.assertTrue(blist[i].equals(clist[i]));
            Assert.assertTrue(clist[i].equals((i == 0 ? farFuture : i == 1 ? future : bare)));
        }
    }

    @Test
    public void testReadingInvalidDirectoryFromFS() throws IOException {
        FileSystem fs = FileSystem.get(TestFSTableDescriptors.UTIL.getConfiguration());
        try {
            new FSTableDescriptors(TestFSTableDescriptors.UTIL.getConfiguration(), fs, FSUtils.getRootDir(TestFSTableDescriptors.UTIL.getConfiguration())).get(TableName.valueOf(HBASE_TEMP_DIRECTORY));
            Assert.fail("Shouldn't be able to read a table descriptor for the archive directory.");
        } catch (Exception e) {
            TestFSTableDescriptors.LOG.debug(("Correctly got error when reading a table descriptor from the archive directory: " + (e.getMessage())));
        }
    }

    @Test
    public void testCreateTableDescriptorUpdatesIfExistsAlready() throws IOException {
        Path testdir = getDataTestDir(name.getMethodName());
        TableDescriptor htd = TableDescriptorBuilder.newBuilder(TableName.valueOf(name.getMethodName())).build();
        FileSystem fs = FileSystem.get(TestFSTableDescriptors.UTIL.getConfiguration());
        FSTableDescriptors fstd = new FSTableDescriptors(TestFSTableDescriptors.UTIL.getConfiguration(), fs, testdir);
        Assert.assertTrue(fstd.createTableDescriptor(htd));
        Assert.assertFalse(fstd.createTableDescriptor(htd));
        htd = TableDescriptorBuilder.newBuilder(htd).setValue(Bytes.toBytes("mykey"), Bytes.toBytes("myValue")).build();
        Assert.assertTrue(fstd.createTableDescriptor(htd));// this will re-create

        Path tableDir = fstd.getTableDir(htd.getTableName());
        Path tmpTableDir = new Path(tableDir, TMP_DIR);
        FileStatus[] statuses = fs.listStatus(tmpTableDir);
        Assert.assertTrue(((statuses.length) == 0));
        Assert.assertEquals(htd, FSTableDescriptors.getTableDescriptorFromFs(fs, tableDir));
    }

    private static class FSTableDescriptorsTest extends FSTableDescriptors {
        public FSTableDescriptorsTest(Configuration conf, FileSystem fs, Path rootdir) throws IOException {
            this(conf, fs, rootdir, false, true);
        }

        public FSTableDescriptorsTest(Configuration conf, FileSystem fs, Path rootdir, boolean fsreadonly, boolean usecache) throws IOException {
            super(conf, fs, rootdir, fsreadonly, usecache);
        }

        @Override
        public TableDescriptor get(TableName tablename) throws FileNotFoundException, IOException, TableExistsException {
            TestFSTableDescriptors.LOG.info((((((isUsecache() ? "Cached" : "Non-Cached") + " TableDescriptor.get() on ") + tablename) + ", cachehits=") + (this.cachehits)));
            return super.get(tablename);
        }
    }
}

