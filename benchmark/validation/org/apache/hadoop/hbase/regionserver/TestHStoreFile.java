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
package org.apache.hadoop.hbase.regionserver;


import BloomFilterFactory.IO_STOREFILE_BLOOM_ENABLED;
import BloomFilterFactory.IO_STOREFILE_BLOOM_ERROR_RATE;
import BloomType.ROW;
import CacheConfig.CACHE_BLOCKS_ON_WRITE_KEY;
import HConstants.EMPTY_BYTE_ARRAY;
import HFileDataBlockEncoder.DATA_BLOCK_ENCODING;
import KeyValue.LOWESTKEY;
import KeyValue.Type;
import StoreFileComparators.SEQ_ID;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;
import junit.framework.TestCase;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestCase;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HRegionInfo;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.KeyValueUtil;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.ColumnFamilyDescriptor;
import org.apache.hadoop.hbase.client.ColumnFamilyDescriptorBuilder;
import org.apache.hadoop.hbase.client.RegionInfo;
import org.apache.hadoop.hbase.client.RegionInfoBuilder;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.io.HFileLink;
import org.apache.hadoop.hbase.io.encoding.DataBlockEncoding;
import org.apache.hadoop.hbase.io.hfile.BlockCache;
import org.apache.hadoop.hbase.io.hfile.BlockCacheFactory;
import org.apache.hadoop.hbase.io.hfile.CacheConfig;
import org.apache.hadoop.hbase.io.hfile.CacheStats;
import org.apache.hadoop.hbase.io.hfile.HFileContext;
import org.apache.hadoop.hbase.io.hfile.HFileContextBuilder;
import org.apache.hadoop.hbase.io.hfile.HFileDataBlockEncoder;
import org.apache.hadoop.hbase.io.hfile.HFileScanner;
import org.apache.hadoop.hbase.testclassification.RegionServerTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.ChecksumType;
import org.apache.hadoop.hbase.util.FSUtils;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static BloomType.NONE;
import static BloomType.ROW;
import static BloomType.ROWCOL;


/**
 * Test HStoreFile
 */
@Category({ RegionServerTests.class, SmallTests.class })
public class TestHStoreFile extends HBaseTestCase {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestHStoreFile.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestHStoreFile.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private CacheConfig cacheConf = new CacheConfig(TestHStoreFile.TEST_UTIL.getConfiguration());

    private static String ROOT_DIR = getDataTestDir("TestStoreFile").toString();

    private static final ChecksumType CKTYPE = ChecksumType.CRC32C;

    private static final int CKBYTES = 512;

    private static String TEST_FAMILY = "cf";

    /**
     * Write a file and then assert that we can read from top and bottom halves
     * using two HalfMapFiles.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testBasicHalfMapFile() throws Exception {
        final HRegionInfo hri = new HRegionInfo(TableName.valueOf("testBasicHalfMapFileTb"));
        HRegionFileSystem regionFs = HRegionFileSystem.createRegionOnFileSystem(conf, fs, new Path(HBaseTestCase.testDir, hri.getTable().getNameAsString()), hri);
        HFileContext meta = new HFileContextBuilder().withBlockSize((2 * 1024)).build();
        StoreFileWriter writer = new StoreFileWriter.Builder(conf, cacheConf, this.fs).withFilePath(regionFs.createTempName()).withFileContext(meta).build();
        writeStoreFile(writer);
        Path sfPath = regionFs.commitStoreFile(TestHStoreFile.TEST_FAMILY, writer.getPath());
        HStoreFile sf = new HStoreFile(this.fs, sfPath, conf, cacheConf, NONE, true);
        checkHalfHFile(regionFs, sf);
    }

    // pick an split point (roughly halfway)
    byte[] SPLITKEY = new byte[]{ ((HBaseTestCase.LAST_CHAR) + (HBaseTestCase.FIRST_CHAR)) / 2, HBaseTestCase.FIRST_CHAR };

    /**
     * Test that our mechanism of writing store files in one region to reference
     * store files in other regions works.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testReference() throws IOException {
        final HRegionInfo hri = new HRegionInfo(TableName.valueOf("testReferenceTb"));
        HRegionFileSystem regionFs = HRegionFileSystem.createRegionOnFileSystem(conf, fs, new Path(HBaseTestCase.testDir, hri.getTable().getNameAsString()), hri);
        HFileContext meta = new HFileContextBuilder().withBlockSize((8 * 1024)).build();
        // Make a store file and write data to it.
        StoreFileWriter writer = new StoreFileWriter.Builder(conf, cacheConf, this.fs).withFilePath(regionFs.createTempName()).withFileContext(meta).build();
        writeStoreFile(writer);
        Path hsfPath = regionFs.commitStoreFile(TestHStoreFile.TEST_FAMILY, writer.getPath());
        HStoreFile hsf = new HStoreFile(this.fs, hsfPath, conf, cacheConf, NONE, true);
        hsf.initReader();
        StoreFileReader reader = hsf.getReader();
        // Split on a row, not in middle of row.  Midkey returned by reader
        // may be in middle of row.  Create new one with empty column and
        // timestamp.
        byte[] midRow = CellUtil.cloneRow(reader.midKey().get());
        byte[] finalRow = CellUtil.cloneRow(reader.getLastKey().get());
        hsf.closeStoreFile(true);
        // Make a reference
        HRegionInfo splitHri = new HRegionInfo(hri.getTable(), null, midRow);
        Path refPath = splitStoreFile(regionFs, splitHri, TestHStoreFile.TEST_FAMILY, hsf, midRow, true);
        HStoreFile refHsf = new HStoreFile(this.fs, refPath, conf, cacheConf, NONE, true);
        refHsf.initReader();
        // Now confirm that I can read from the reference and that it only gets
        // keys from top half of the file.
        HFileScanner s = refHsf.getReader().getScanner(false, false);
        Cell kv = null;
        for (boolean first = true; ((!(s.isSeeked())) && (s.seekTo())) || (s.next());) {
            ByteBuffer bb = ByteBuffer.wrap(getKey());
            kv = KeyValueUtil.createKeyValueFromKey(bb);
            if (first) {
                TestCase.assertTrue(Bytes.equals(kv.getRowArray(), kv.getRowOffset(), kv.getRowLength(), midRow, 0, midRow.length));
                first = false;
            }
        }
        TestCase.assertTrue(Bytes.equals(kv.getRowArray(), kv.getRowOffset(), kv.getRowLength(), finalRow, 0, finalRow.length));
    }

    @Test
    public void testStoreFileReference() throws Exception {
        final RegionInfo hri = RegionInfoBuilder.newBuilder(TableName.valueOf("testStoreFileReference")).build();
        HRegionFileSystem regionFs = HRegionFileSystem.createRegionOnFileSystem(conf, fs, new Path(HBaseTestCase.testDir, hri.getTable().getNameAsString()), hri);
        HFileContext meta = new HFileContextBuilder().withBlockSize((8 * 1024)).build();
        // Make a store file and write data to it.
        StoreFileWriter writer = new StoreFileWriter.Builder(conf, cacheConf, this.fs).withFilePath(regionFs.createTempName()).withFileContext(meta).build();
        writeStoreFile(writer);
        Path hsfPath = regionFs.commitStoreFile(TestHStoreFile.TEST_FAMILY, writer.getPath());
        writer.close();
        HStoreFile file = new HStoreFile(this.fs, hsfPath, conf, cacheConf, NONE, true);
        file.initReader();
        StoreFileReader r = file.getReader();
        TestCase.assertNotNull(r);
        StoreFileScanner scanner = new StoreFileScanner(r, Mockito.mock(HFileScanner.class), false, false, 0, 0, false);
        // Verify after instantiating scanner refCount is increased
        TestCase.assertTrue("Verify file is being referenced", file.isReferencedInReads());
        scanner.close();
        // Verify after closing scanner refCount is decreased
        TestCase.assertFalse("Verify file is not being referenced", file.isReferencedInReads());
    }

    @Test
    public void testEmptyStoreFileRestrictKeyRanges() throws Exception {
        StoreFileReader reader = Mockito.mock(StoreFileReader.class);
        HStore store = Mockito.mock(HStore.class);
        byte[] cf = Bytes.toBytes("ty");
        ColumnFamilyDescriptor cfd = ColumnFamilyDescriptorBuilder.of(cf);
        Mockito.when(store.getColumnFamilyDescriptor()).thenReturn(cfd);
        StoreFileScanner scanner = new StoreFileScanner(reader, Mockito.mock(HFileScanner.class), false, false, 0, 0, true);
        Scan scan = new Scan();
        scan.setColumnFamilyTimeRange(cf, 0, 1);
        TestCase.assertFalse(scanner.shouldUseScanner(scan, store, 0));
    }

    @Test
    public void testHFileLink() throws IOException {
        final HRegionInfo hri = new HRegionInfo(TableName.valueOf("testHFileLinkTb"));
        // force temp data in hbase/target/test-data instead of /tmp/hbase-xxxx/
        Configuration testConf = new Configuration(this.conf);
        FSUtils.setRootDir(testConf, HBaseTestCase.testDir);
        HRegionFileSystem regionFs = HRegionFileSystem.createRegionOnFileSystem(testConf, fs, FSUtils.getTableDir(HBaseTestCase.testDir, hri.getTable()), hri);
        HFileContext meta = new HFileContextBuilder().withBlockSize((8 * 1024)).build();
        // Make a store file and write data to it.
        StoreFileWriter writer = new StoreFileWriter.Builder(conf, cacheConf, this.fs).withFilePath(regionFs.createTempName()).withFileContext(meta).build();
        writeStoreFile(writer);
        Path storeFilePath = regionFs.commitStoreFile(TestHStoreFile.TEST_FAMILY, writer.getPath());
        Path dstPath = new Path(regionFs.getTableDir(), new Path("test-region", TestHStoreFile.TEST_FAMILY));
        HFileLink.create(testConf, this.fs, dstPath, hri, storeFilePath.getName());
        Path linkFilePath = new Path(dstPath, HFileLink.createHFileLinkName(hri, storeFilePath.getName()));
        // Try to open store file from link
        StoreFileInfo storeFileInfo = new StoreFileInfo(testConf, this.fs, linkFilePath);
        HStoreFile hsf = new HStoreFile(this.fs, storeFileInfo, testConf, cacheConf, NONE, true);
        TestCase.assertTrue(storeFileInfo.isLink());
        hsf.initReader();
        // Now confirm that I can read from the link
        int count = 1;
        HFileScanner s = hsf.getReader().getScanner(false, false);
        s.seekTo();
        while (s.next()) {
            count++;
        } 
        TestCase.assertEquals(((((HBaseTestCase.LAST_CHAR) - (HBaseTestCase.FIRST_CHAR)) + 1) * (((HBaseTestCase.LAST_CHAR) - (HBaseTestCase.FIRST_CHAR)) + 1)), count);
    }

    /**
     * This test creates an hfile and then the dir structures and files to verify that references
     * to hfilelinks (created by snapshot clones) can be properly interpreted.
     */
    @Test
    public void testReferenceToHFileLink() throws IOException {
        // force temp data in hbase/target/test-data instead of /tmp/hbase-xxxx/
        Configuration testConf = new Configuration(this.conf);
        FSUtils.setRootDir(testConf, HBaseTestCase.testDir);
        // adding legal table name chars to verify regex handles it.
        HRegionInfo hri = new HRegionInfo(TableName.valueOf("_original-evil-name"));
        HRegionFileSystem regionFs = HRegionFileSystem.createRegionOnFileSystem(testConf, fs, FSUtils.getTableDir(HBaseTestCase.testDir, hri.getTable()), hri);
        HFileContext meta = new HFileContextBuilder().withBlockSize((8 * 1024)).build();
        // Make a store file and write data to it. <root>/<tablename>/<rgn>/<cf>/<file>
        StoreFileWriter writer = new StoreFileWriter.Builder(testConf, cacheConf, this.fs).withFilePath(regionFs.createTempName()).withFileContext(meta).build();
        writeStoreFile(writer);
        Path storeFilePath = regionFs.commitStoreFile(TestHStoreFile.TEST_FAMILY, writer.getPath());
        // create link to store file. <root>/clone/region/<cf>/<hfile>-<region>-<table>
        HRegionInfo hriClone = new HRegionInfo(TableName.valueOf("clone"));
        HRegionFileSystem cloneRegionFs = HRegionFileSystem.createRegionOnFileSystem(testConf, fs, FSUtils.getTableDir(HBaseTestCase.testDir, hri.getTable()), hriClone);
        Path dstPath = cloneRegionFs.getStoreDir(TestHStoreFile.TEST_FAMILY);
        HFileLink.create(testConf, this.fs, dstPath, hri, storeFilePath.getName());
        Path linkFilePath = new Path(dstPath, HFileLink.createHFileLinkName(hri, storeFilePath.getName()));
        // create splits of the link.
        // <root>/clone/splitA/<cf>/<reftohfilelink>,
        // <root>/clone/splitB/<cf>/<reftohfilelink>
        HRegionInfo splitHriA = new HRegionInfo(hri.getTable(), null, SPLITKEY);
        HRegionInfo splitHriB = new HRegionInfo(hri.getTable(), SPLITKEY, null);
        HStoreFile f = new HStoreFile(fs, linkFilePath, testConf, cacheConf, NONE, true);
        f.initReader();
        Path pathA = splitStoreFile(cloneRegionFs, splitHriA, TestHStoreFile.TEST_FAMILY, f, SPLITKEY, true);// top

        Path pathB = splitStoreFile(cloneRegionFs, splitHriB, TestHStoreFile.TEST_FAMILY, f, SPLITKEY, false);// bottom

        f.closeStoreFile(true);
        // OK test the thing
        FSUtils.logFileSystemState(fs, HBaseTestCase.testDir, TestHStoreFile.LOG);
        // There is a case where a file with the hfilelink pattern is actually a daughter
        // reference to a hfile link.  This code in StoreFile that handles this case.
        // Try to open store file from link
        HStoreFile hsfA = new HStoreFile(this.fs, pathA, testConf, cacheConf, NONE, true);
        hsfA.initReader();
        // Now confirm that I can read from the ref to link
        int count = 1;
        HFileScanner s = hsfA.getReader().getScanner(false, false);
        s.seekTo();
        while (s.next()) {
            count++;
        } 
        TestCase.assertTrue((count > 0));// read some rows here

        // Try to open store file from link
        HStoreFile hsfB = new HStoreFile(this.fs, pathB, testConf, cacheConf, NONE, true);
        hsfB.initReader();
        // Now confirm that I can read from the ref to link
        HFileScanner sB = hsfB.getReader().getScanner(false, false);
        sB.seekTo();
        // count++ as seekTo() will advance the scanner
        count++;
        while (sB.next()) {
            count++;
        } 
        // read the rest of the rows
        TestCase.assertEquals(((((HBaseTestCase.LAST_CHAR) - (HBaseTestCase.FIRST_CHAR)) + 1) * (((HBaseTestCase.LAST_CHAR) - (HBaseTestCase.FIRST_CHAR)) + 1)), count);
    }

    private static final String localFormatter = "%010d";

    private static final int BLOCKSIZE_SMALL = 8192;

    @Test
    public void testBloomFilter() throws Exception {
        FileSystem fs = FileSystem.getLocal(conf);
        conf.setFloat(IO_STOREFILE_BLOOM_ERROR_RATE, ((float) (0.01)));
        conf.setBoolean(IO_STOREFILE_BLOOM_ENABLED, true);
        // write the file
        Path f = new Path(TestHStoreFile.ROOT_DIR, getName());
        HFileContext meta = new HFileContextBuilder().withBlockSize(TestHStoreFile.BLOCKSIZE_SMALL).withChecksumType(TestHStoreFile.CKTYPE).withBytesPerCheckSum(TestHStoreFile.CKBYTES).build();
        // Make a store file and write data to it.
        StoreFileWriter writer = new StoreFileWriter.Builder(conf, cacheConf, this.fs).withFilePath(f).withBloomType(ROW).withMaxKeyCount(2000).withFileContext(meta).build();
        bloomWriteRead(writer, fs);
    }

    @Test
    public void testDeleteFamilyBloomFilter() throws Exception {
        FileSystem fs = FileSystem.getLocal(conf);
        conf.setFloat(IO_STOREFILE_BLOOM_ERROR_RATE, ((float) (0.01)));
        conf.setBoolean(IO_STOREFILE_BLOOM_ENABLED, true);
        float err = conf.getFloat(IO_STOREFILE_BLOOM_ERROR_RATE, 0);
        // write the file
        Path f = new Path(TestHStoreFile.ROOT_DIR, getName());
        HFileContext meta = new HFileContextBuilder().withBlockSize(TestHStoreFile.BLOCKSIZE_SMALL).withChecksumType(TestHStoreFile.CKTYPE).withBytesPerCheckSum(TestHStoreFile.CKBYTES).build();
        // Make a store file and write data to it.
        StoreFileWriter writer = new StoreFileWriter.Builder(conf, cacheConf, this.fs).withFilePath(f).withMaxKeyCount(2000).withFileContext(meta).build();
        // add delete family
        long now = System.currentTimeMillis();
        for (int i = 0; i < 2000; i += 2) {
            String row = String.format(TestHStoreFile.localFormatter, i);
            KeyValue kv = new KeyValue(Bytes.toBytes(row), Bytes.toBytes("family"), Bytes.toBytes("col"), now, Type.DeleteFamily, Bytes.toBytes("value"));
            writer.append(kv);
        }
        writer.close();
        StoreFileReader reader = new StoreFileReader(fs, f, cacheConf, true, new AtomicInteger(0), true, conf);
        reader.loadFileInfo();
        reader.loadBloomfilter();
        // check false positives rate
        int falsePos = 0;
        int falseNeg = 0;
        for (int i = 0; i < 2000; i++) {
            String row = String.format(TestHStoreFile.localFormatter, i);
            byte[] rowKey = Bytes.toBytes(row);
            boolean exists = reader.passesDeleteFamilyBloomFilter(rowKey, 0, rowKey.length);
            if ((i % 2) == 0) {
                if (!exists)
                    falseNeg++;

            } else {
                if (exists)
                    falsePos++;

            }
        }
        TestCase.assertEquals(1000, reader.getDeleteFamilyCnt());
        reader.close(true);// evict because we are about to delete the file

        fs.delete(f, true);
        TestCase.assertEquals(("False negatives: " + falseNeg), 0, falseNeg);
        int maxFalsePos = ((int) ((2 * 2000) * err));
        TestCase.assertTrue(((((("Too many false positives: " + falsePos) + " (err=") + err) + ", expected no more than ") + maxFalsePos), (falsePos <= maxFalsePos));
    }

    /**
     * Test for HBASE-8012
     */
    @Test
    public void testReseek() throws Exception {
        // write the file
        Path f = new Path(TestHStoreFile.ROOT_DIR, getName());
        HFileContext meta = new HFileContextBuilder().withBlockSize((8 * 1024)).build();
        // Make a store file and write data to it.
        StoreFileWriter writer = new StoreFileWriter.Builder(conf, cacheConf, this.fs).withFilePath(f).withFileContext(meta).build();
        writeStoreFile(writer);
        writer.close();
        StoreFileReader reader = new StoreFileReader(fs, f, cacheConf, true, new AtomicInteger(0), true, conf);
        // Now do reseek with empty KV to position to the beginning of the file
        KeyValue k = KeyValueUtil.createFirstOnRow(EMPTY_BYTE_ARRAY);
        StoreFileScanner s = TestHStoreFile.getStoreFileScanner(reader, false, false);
        s.reseek(k);
        TestCase.assertNotNull("Intial reseek should position at the beginning of the file", s.peek());
    }

    @Test
    public void testBloomTypes() throws Exception {
        float err = ((float) (0.01));
        FileSystem fs = FileSystem.getLocal(conf);
        conf.setFloat(IO_STOREFILE_BLOOM_ERROR_RATE, err);
        conf.setBoolean(IO_STOREFILE_BLOOM_ENABLED, true);
        int rowCount = 50;
        int colCount = 10;
        int versions = 2;
        // run once using columns and once using rows
        BloomType[] bt = new BloomType[]{ ROWCOL, ROW };
        int[] expKeys = new int[]{ rowCount * colCount, rowCount };
        // below line deserves commentary.  it is expected bloom false positives
        // column = rowCount*2*colCount inserts
        // row-level = only rowCount*2 inserts, but failures will be magnified by
        // 2nd for loop for every column (2*colCount)
        float[] expErr = new float[]{ ((2 * rowCount) * colCount) * err, (((2 * rowCount) * 2) * colCount) * err };
        for (int x : new int[]{ 0, 1 }) {
            // write the file
            Path f = new Path(TestHStoreFile.ROOT_DIR, ((getName()) + x));
            HFileContext meta = new HFileContextBuilder().withBlockSize(TestHStoreFile.BLOCKSIZE_SMALL).withChecksumType(TestHStoreFile.CKTYPE).withBytesPerCheckSum(TestHStoreFile.CKBYTES).build();
            // Make a store file and write data to it.
            StoreFileWriter writer = new StoreFileWriter.Builder(conf, cacheConf, this.fs).withFilePath(f).withBloomType(bt[x]).withMaxKeyCount(expKeys[x]).withFileContext(meta).build();
            long now = System.currentTimeMillis();
            for (int i = 0; i < (rowCount * 2); i += 2) {
                // rows
                for (int j = 0; j < (colCount * 2); j += 2) {
                    // column qualifiers
                    String row = String.format(TestHStoreFile.localFormatter, i);
                    String col = String.format(TestHStoreFile.localFormatter, j);
                    for (int k = 0; k < versions; ++k) {
                        // versions
                        KeyValue kv = new KeyValue(Bytes.toBytes(row), Bytes.toBytes("family"), Bytes.toBytes(("col" + col)), (now - k), Bytes.toBytes((-1L)));
                        writer.append(kv);
                    }
                }
            }
            writer.close();
            StoreFileReader reader = new StoreFileReader(fs, f, cacheConf, true, new AtomicInteger(0), true, conf);
            reader.loadFileInfo();
            reader.loadBloomfilter();
            StoreFileScanner scanner = TestHStoreFile.getStoreFileScanner(reader, false, false);
            TestCase.assertEquals(expKeys[x], reader.getGeneralBloomFilter().getKeyCount());
            HStore store = Mockito.mock(HStore.class);
            Mockito.when(store.getColumnFamilyDescriptor()).thenReturn(ColumnFamilyDescriptorBuilder.of("family"));
            // check false positives rate
            int falsePos = 0;
            int falseNeg = 0;
            for (int i = 0; i < (rowCount * 2); ++i) {
                // rows
                for (int j = 0; j < (colCount * 2); ++j) {
                    // column qualifiers
                    String row = String.format(TestHStoreFile.localFormatter, i);
                    String col = String.format(TestHStoreFile.localFormatter, j);
                    TreeSet<byte[]> columns = new TreeSet(Bytes.BYTES_COMPARATOR);
                    columns.add(Bytes.toBytes(("col" + col)));
                    Scan scan = new Scan(Bytes.toBytes(row), Bytes.toBytes(row));
                    scan.addColumn(Bytes.toBytes("family"), Bytes.toBytes(("col" + col)));
                    boolean exists = scanner.shouldUseScanner(scan, store, Long.MIN_VALUE);
                    boolean shouldRowExist = (i % 2) == 0;
                    boolean shouldColExist = (j % 2) == 0;
                    shouldColExist = shouldColExist || ((bt[x]) == (ROW));
                    if (shouldRowExist && shouldColExist) {
                        if (!exists)
                            falseNeg++;

                    } else {
                        if (exists)
                            falsePos++;

                    }
                }
            }
            reader.close(true);// evict because we are about to delete the file

            fs.delete(f, true);
            System.out.println(bt[x].toString());
            System.out.println(("  False negatives: " + falseNeg));
            System.out.println(("  False positives: " + falsePos));
            TestCase.assertEquals(0, falseNeg);
            TestCase.assertTrue((falsePos < (2 * (expErr[x]))));
        }
    }

    @Test
    public void testSeqIdComparator() {
        assertOrdering(SEQ_ID, mockStoreFile(true, 100, 1000, (-1), "/foo/123"), mockStoreFile(true, 100, 1000, (-1), "/foo/124"), mockStoreFile(true, 99, 1000, (-1), "/foo/126"), mockStoreFile(true, 98, 2000, (-1), "/foo/126"), mockStoreFile(false, 3453, (-1), 1, "/foo/1"), mockStoreFile(false, 2, (-1), 3, "/foo/2"), mockStoreFile(false, 1000, (-1), 5, "/foo/2"), mockStoreFile(false, 76, (-1), 5, "/foo/3"));
    }

    /**
     * Test to ensure correctness when using StoreFile with multiple timestamps
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testMultipleTimestamps() throws IOException {
        byte[] family = Bytes.toBytes("familyname");
        byte[] qualifier = Bytes.toBytes("qualifier");
        int numRows = 10;
        long[] timestamps = new long[]{ 20, 10, 5, 1 };
        Scan scan = new Scan();
        // Make up a directory hierarchy that has a regiondir ("7e0102") and familyname.
        Path storedir = new Path(new Path(HBaseTestCase.testDir, "7e0102"), Bytes.toString(family));
        Path dir = new Path(storedir, "1234567890");
        HFileContext meta = new HFileContextBuilder().withBlockSize((8 * 1024)).build();
        // Make a store file and write data to it.
        StoreFileWriter writer = new StoreFileWriter.Builder(conf, cacheConf, this.fs).withOutputDir(dir).withFileContext(meta).build();
        List<KeyValue> kvList = getKeyValueSet(timestamps, numRows, qualifier, family);
        for (KeyValue kv : kvList) {
            writer.append(kv);
        }
        writer.appendMetadata(0, false);
        writer.close();
        HStoreFile hsf = new HStoreFile(this.fs, writer.getPath(), conf, cacheConf, NONE, true);
        HStore store = Mockito.mock(HStore.class);
        Mockito.when(store.getColumnFamilyDescriptor()).thenReturn(ColumnFamilyDescriptorBuilder.of(family));
        hsf.initReader();
        StoreFileReader reader = hsf.getReader();
        StoreFileScanner scanner = TestHStoreFile.getStoreFileScanner(reader, false, false);
        TreeSet<byte[]> columns = new TreeSet(Bytes.BYTES_COMPARATOR);
        columns.add(qualifier);
        scan.setTimeRange(20, 100);
        TestCase.assertTrue(scanner.shouldUseScanner(scan, store, Long.MIN_VALUE));
        scan.setTimeRange(1, 2);
        TestCase.assertTrue(scanner.shouldUseScanner(scan, store, Long.MIN_VALUE));
        scan.setTimeRange(8, 10);
        TestCase.assertTrue(scanner.shouldUseScanner(scan, store, Long.MIN_VALUE));
        // lets make sure it still works with column family time ranges
        scan.setColumnFamilyTimeRange(family, 7, 50);
        TestCase.assertTrue(scanner.shouldUseScanner(scan, store, Long.MIN_VALUE));
        // This test relies on the timestamp range optimization
        scan = new Scan();
        scan.setTimeRange(27, 50);
        TestCase.assertTrue((!(scanner.shouldUseScanner(scan, store, Long.MIN_VALUE))));
        // should still use the scanner because we override the family time range
        scan = new Scan();
        scan.setTimeRange(27, 50);
        scan.setColumnFamilyTimeRange(family, 7, 50);
        TestCase.assertTrue(scanner.shouldUseScanner(scan, store, Long.MIN_VALUE));
    }

    @Test
    public void testCacheOnWriteEvictOnClose() throws Exception {
        Configuration conf = this.conf;
        // Find a home for our files (regiondir ("7e0102") and familyname).
        Path baseDir = new Path(new Path(HBaseTestCase.testDir, "7e0102"), "twoCOWEOC");
        // Grab the block cache and get the initial hit/miss counts
        BlockCache bc = BlockCacheFactory.createBlockCache(conf);
        TestCase.assertNotNull(bc);
        CacheStats cs = bc.getStats();
        long startHit = cs.getHitCount();
        long startMiss = cs.getMissCount();
        long startEvicted = cs.getEvictedCount();
        // Let's write a StoreFile with three blocks, with cache on write off
        conf.setBoolean(CACHE_BLOCKS_ON_WRITE_KEY, false);
        CacheConfig cacheConf = new CacheConfig(conf, bc);
        Path pathCowOff = new Path(baseDir, "123456789");
        StoreFileWriter writer = writeStoreFile(conf, cacheConf, pathCowOff, 3);
        HStoreFile hsf = new HStoreFile(this.fs, writer.getPath(), conf, cacheConf, NONE, true);
        TestHStoreFile.LOG.debug(hsf.getPath().toString());
        // Read this file, we should see 3 misses
        hsf.initReader();
        StoreFileReader reader = hsf.getReader();
        reader.loadFileInfo();
        StoreFileScanner scanner = TestHStoreFile.getStoreFileScanner(reader, true, true);
        scanner.seek(LOWESTKEY);
        while ((scanner.next()) != null);
        TestCase.assertEquals(startHit, cs.getHitCount());
        TestCase.assertEquals((startMiss + 3), cs.getMissCount());
        TestCase.assertEquals(startEvicted, cs.getEvictedCount());
        startMiss += 3;
        scanner.close();
        reader.close(cacheConf.shouldEvictOnClose());
        // Now write a StoreFile with three blocks, with cache on write on
        conf.setBoolean(CACHE_BLOCKS_ON_WRITE_KEY, true);
        cacheConf = new CacheConfig(conf, bc);
        Path pathCowOn = new Path(baseDir, "123456788");
        writer = writeStoreFile(conf, cacheConf, pathCowOn, 3);
        hsf = new HStoreFile(this.fs, writer.getPath(), conf, cacheConf, NONE, true);
        // Read this file, we should see 3 hits
        hsf.initReader();
        reader = hsf.getReader();
        scanner = TestHStoreFile.getStoreFileScanner(reader, true, true);
        scanner.seek(LOWESTKEY);
        while ((scanner.next()) != null);
        TestCase.assertEquals((startHit + 3), cs.getHitCount());
        TestCase.assertEquals(startMiss, cs.getMissCount());
        TestCase.assertEquals(startEvicted, cs.getEvictedCount());
        startHit += 3;
        scanner.close();
        reader.close(cacheConf.shouldEvictOnClose());
        // Let's read back the two files to ensure the blocks exactly match
        hsf = new HStoreFile(this.fs, pathCowOff, conf, cacheConf, NONE, true);
        hsf.initReader();
        StoreFileReader readerOne = hsf.getReader();
        readerOne.loadFileInfo();
        StoreFileScanner scannerOne = TestHStoreFile.getStoreFileScanner(readerOne, true, true);
        scannerOne.seek(LOWESTKEY);
        hsf = new HStoreFile(this.fs, pathCowOn, conf, cacheConf, NONE, true);
        hsf.initReader();
        StoreFileReader readerTwo = hsf.getReader();
        readerTwo.loadFileInfo();
        StoreFileScanner scannerTwo = TestHStoreFile.getStoreFileScanner(readerTwo, true, true);
        scannerTwo.seek(LOWESTKEY);
        Cell kv1 = null;
        Cell kv2 = null;
        while ((kv1 = scannerOne.next()) != null) {
            kv2 = scannerTwo.next();
            TestCase.assertTrue(kv1.equals(kv2));
            KeyValue keyv1 = KeyValueUtil.ensureKeyValue(kv1);
            KeyValue keyv2 = KeyValueUtil.ensureKeyValue(kv2);
            TestCase.assertTrue(((Bytes.compareTo(keyv1.getBuffer(), keyv1.getKeyOffset(), keyv1.getKeyLength(), keyv2.getBuffer(), keyv2.getKeyOffset(), keyv2.getKeyLength())) == 0));
            TestCase.assertTrue(((Bytes.compareTo(kv1.getValueArray(), kv1.getValueOffset(), kv1.getValueLength(), kv2.getValueArray(), kv2.getValueOffset(), kv2.getValueLength())) == 0));
        } 
        TestCase.assertNull(scannerTwo.next());
        TestCase.assertEquals((startHit + 6), cs.getHitCount());
        TestCase.assertEquals(startMiss, cs.getMissCount());
        TestCase.assertEquals(startEvicted, cs.getEvictedCount());
        startHit += 6;
        scannerOne.close();
        readerOne.close(cacheConf.shouldEvictOnClose());
        scannerTwo.close();
        readerTwo.close(cacheConf.shouldEvictOnClose());
        // Let's close the first file with evict on close turned on
        conf.setBoolean("hbase.rs.evictblocksonclose", true);
        cacheConf = new CacheConfig(conf, bc);
        hsf = new HStoreFile(this.fs, pathCowOff, conf, cacheConf, NONE, true);
        hsf.initReader();
        reader = hsf.getReader();
        reader.close(cacheConf.shouldEvictOnClose());
        // We should have 3 new evictions but the evict count stat should not change. Eviction because
        // of HFile invalidation is not counted along with normal evictions
        TestCase.assertEquals(startHit, cs.getHitCount());
        TestCase.assertEquals(startMiss, cs.getMissCount());
        TestCase.assertEquals(startEvicted, cs.getEvictedCount());
        // Let's close the second file with evict on close turned off
        conf.setBoolean("hbase.rs.evictblocksonclose", false);
        cacheConf = new CacheConfig(conf, bc);
        hsf = new HStoreFile(this.fs, pathCowOn, conf, cacheConf, NONE, true);
        hsf.initReader();
        reader = hsf.getReader();
        reader.close(cacheConf.shouldEvictOnClose());
        // We expect no changes
        TestCase.assertEquals(startHit, cs.getHitCount());
        TestCase.assertEquals(startMiss, cs.getMissCount());
        TestCase.assertEquals(startEvicted, cs.getEvictedCount());
    }

    /**
     * Check if data block encoding information is saved correctly in HFile's
     * file info.
     */
    @Test
    public void testDataBlockEncodingMetaData() throws IOException {
        // Make up a directory hierarchy that has a regiondir ("7e0102") and familyname.
        Path dir = new Path(new Path(HBaseTestCase.testDir, "7e0102"), "familyname");
        Path path = new Path(dir, "1234567890");
        DataBlockEncoding dataBlockEncoderAlgo = DataBlockEncoding.FAST_DIFF;
        HFileDataBlockEncoder dataBlockEncoder = new org.apache.hadoop.hbase.io.hfile.HFileDataBlockEncoderImpl(dataBlockEncoderAlgo);
        cacheConf = new CacheConfig(conf);
        HFileContext meta = new HFileContextBuilder().withBlockSize(TestHStoreFile.BLOCKSIZE_SMALL).withChecksumType(TestHStoreFile.CKTYPE).withBytesPerCheckSum(TestHStoreFile.CKBYTES).withDataBlockEncoding(dataBlockEncoderAlgo).build();
        // Make a store file and write data to it.
        StoreFileWriter writer = new StoreFileWriter.Builder(conf, cacheConf, this.fs).withFilePath(path).withMaxKeyCount(2000).withFileContext(meta).build();
        writer.close();
        HStoreFile storeFile = new HStoreFile(fs, writer.getPath(), conf, cacheConf, NONE, true);
        storeFile.initReader();
        StoreFileReader reader = storeFile.getReader();
        Map<byte[], byte[]> fileInfo = reader.loadFileInfo();
        byte[] value = fileInfo.get(DATA_BLOCK_ENCODING);
        HBaseTestCase.assertEquals(dataBlockEncoderAlgo.getNameInBytes(), value);
    }
}

