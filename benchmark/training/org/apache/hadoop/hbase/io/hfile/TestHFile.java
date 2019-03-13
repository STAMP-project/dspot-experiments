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
package org.apache.hadoop.hbase.io.hfile;


import CellComparatorImpl.COMPARATOR;
import CellComparatorImpl.META_COMPARATOR;
import Compression.Algorithm;
import Compression.Algorithm.GZ;
import Compression.Algorithm.LZ4;
import Compression.Algorithm.LZO;
import Compression.Algorithm.NONE;
import Compression.Algorithm.SNAPPY;
import HConstants.EMPTY_BYTE_ARRAY;
import KeyValue.Type;
import KeyValue.Type.Maximum;
import java.io.IOException;
import java.util.Arrays;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellComparatorImpl;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseCommonTestingUtility;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.PrivateCellUtil;
import org.apache.hadoop.hbase.io.compress.Compression;
import org.apache.hadoop.hbase.io.hfile.HFile.Reader;
import org.apache.hadoop.hbase.io.hfile.HFile.Writer;
import org.apache.hadoop.hbase.testclassification.IOTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.hadoop.hbase.KeyValue.Type.Put;


/**
 * test hfile features.
 */
@Category({ IOTests.class, SmallTests.class })
public class TestHFile {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestHFile.class);

    @Rule
    public TestName testName = new TestName();

    private static final Logger LOG = LoggerFactory.getLogger(TestHFile.class);

    private static final int NUM_VALID_KEY_TYPES = (Type.values().length) - 2;

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static String ROOT_DIR = getDataTestDir("TestHFile").toString();

    private final int minBlockSize = 512;

    private static String localFormatter = "%010d";

    private static CacheConfig cacheConf;

    private static Configuration conf;

    private static FileSystem fs;

    @Test
    public void testReaderWithoutBlockCache() throws Exception {
        Path path = writeStoreFile();
        try {
            readStoreFile(path);
        } catch (Exception e) {
            // fail test
            Assert.assertTrue(false);
        }
    }

    /**
     * Test empty HFile.
     * Test all features work reasonably when hfile is empty of entries.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testEmptyHFile() throws IOException {
        Path f = new Path(TestHFile.ROOT_DIR, testName.getMethodName());
        HFileContext context = new HFileContextBuilder().withIncludesTags(false).build();
        Writer w = HFile.getWriterFactory(TestHFile.conf, TestHFile.cacheConf).withPath(TestHFile.fs, f).withFileContext(context).create();
        w.close();
        Reader r = HFile.createReader(TestHFile.fs, f, TestHFile.cacheConf, true, TestHFile.conf);
        r.loadFileInfo();
        Assert.assertFalse(r.getFirstKey().isPresent());
        Assert.assertFalse(r.getLastKey().isPresent());
    }

    /**
     * Create 0-length hfile and show that it fails
     */
    @Test
    public void testCorrupt0LengthHFile() throws IOException {
        Path f = new Path(TestHFile.ROOT_DIR, testName.getMethodName());
        FSDataOutputStream fsos = TestHFile.fs.create(f);
        fsos.close();
        try {
            Reader r = HFile.createReader(TestHFile.fs, f, TestHFile.cacheConf, true, TestHFile.conf);
        } catch (CorruptHFileException che) {
            // Expected failure
            return;
        }
        Assert.fail("Should have thrown exception");
    }

    /**
     * Create a truncated hfile and verify that exception thrown.
     */
    @Test
    public void testCorruptTruncatedHFile() throws IOException {
        Path f = new Path(TestHFile.ROOT_DIR, testName.getMethodName());
        HFileContext context = new HFileContextBuilder().build();
        Writer w = HFile.getWriterFactory(TestHFile.conf, TestHFile.cacheConf).withPath(this.fs, f).withFileContext(context).create();
        writeSomeRecords(w, 0, 100, false);
        w.close();
        Path trunc = new Path(f.getParent(), "trucated");
        TestHFile.truncateFile(TestHFile.fs, w.getPath(), trunc);
        try {
            Reader r = HFile.createReader(TestHFile.fs, trunc, TestHFile.cacheConf, true, TestHFile.conf);
        } catch (CorruptHFileException che) {
            // Expected failure
            return;
        }
        Assert.fail("Should have thrown exception");
    }

    @Test
    public void testTFileFeatures() throws IOException {
        testHFilefeaturesInternals(false);
        testHFilefeaturesInternals(true);
    }

    // test meta blocks for hfiles
    @Test
    public void testMetaBlocks() throws Exception {
        metablocks("none");
        metablocks("gz");
    }

    @Test
    public void testNullMetaBlocks() throws Exception {
        for (Compression.Algorithm compressAlgo : HBaseCommonTestingUtility.COMPRESSION_ALGORITHMS) {
            Path mFile = new Path(TestHFile.ROOT_DIR, (("nometa_" + compressAlgo) + ".hfile"));
            FSDataOutputStream fout = createFSOutput(mFile);
            HFileContext meta = new HFileContextBuilder().withCompression(compressAlgo).withBlockSize(minBlockSize).build();
            Writer writer = HFile.getWriterFactory(TestHFile.conf, TestHFile.cacheConf).withOutputStream(fout).withFileContext(meta).create();
            KeyValue kv = new KeyValue(Bytes.toBytes("foo"), Bytes.toBytes("f1"), null, Bytes.toBytes("value"));
            writer.append(kv);
            writer.close();
            fout.close();
            Reader reader = HFile.createReader(TestHFile.fs, mFile, TestHFile.cacheConf, true, TestHFile.conf);
            reader.loadFileInfo();
            Assert.assertNull(reader.getMetaBlock("non-existant", false));
        }
    }

    /**
     * Make sure the ordinals for our compression algorithms do not change on us.
     */
    @Test
    public void testCompressionOrdinance() {
        Assert.assertTrue(((LZO.ordinal()) == 0));
        Assert.assertTrue(((GZ.ordinal()) == 1));
        Assert.assertTrue(((NONE.ordinal()) == 2));
        Assert.assertTrue(((SNAPPY.ordinal()) == 3));
        Assert.assertTrue(((LZ4.ordinal()) == 4));
    }

    @Test
    public void testShortMidpointSameQual() {
        Cell left = CellUtil.createCell(Bytes.toBytes("a"), Bytes.toBytes("a"), Bytes.toBytes("a"), 11, Maximum.getCode(), EMPTY_BYTE_ARRAY);
        Cell right = CellUtil.createCell(Bytes.toBytes("a"), Bytes.toBytes("a"), Bytes.toBytes("a"), 9, Maximum.getCode(), EMPTY_BYTE_ARRAY);
        Cell mid = HFileWriterImpl.getMidpoint(COMPARATOR, left, right);
        Assert.assertTrue(((PrivateCellUtil.compareKeyIgnoresMvcc(COMPARATOR, left, mid)) <= 0));
        Assert.assertTrue(((PrivateCellUtil.compareKeyIgnoresMvcc(COMPARATOR, mid, right)) == 0));
    }

    @Test
    public void testGetShortMidpoint() {
        Cell left = CellUtil.createCell(Bytes.toBytes("a"), Bytes.toBytes("a"), Bytes.toBytes("a"));
        Cell right = CellUtil.createCell(Bytes.toBytes("a"), Bytes.toBytes("a"), Bytes.toBytes("a"));
        Cell mid = HFileWriterImpl.getMidpoint(COMPARATOR, left, right);
        Assert.assertTrue(((PrivateCellUtil.compareKeyIgnoresMvcc(COMPARATOR, left, mid)) <= 0));
        Assert.assertTrue(((PrivateCellUtil.compareKeyIgnoresMvcc(COMPARATOR, mid, right)) <= 0));
        left = CellUtil.createCell(Bytes.toBytes("a"), Bytes.toBytes("a"), Bytes.toBytes("a"));
        right = CellUtil.createCell(Bytes.toBytes("b"), Bytes.toBytes("a"), Bytes.toBytes("a"));
        mid = HFileWriterImpl.getMidpoint(COMPARATOR, left, right);
        Assert.assertTrue(((PrivateCellUtil.compareKeyIgnoresMvcc(COMPARATOR, left, mid)) < 0));
        Assert.assertTrue(((PrivateCellUtil.compareKeyIgnoresMvcc(COMPARATOR, mid, right)) <= 0));
        left = CellUtil.createCell(Bytes.toBytes("g"), Bytes.toBytes("a"), Bytes.toBytes("a"));
        right = CellUtil.createCell(Bytes.toBytes("i"), Bytes.toBytes("a"), Bytes.toBytes("a"));
        mid = HFileWriterImpl.getMidpoint(COMPARATOR, left, right);
        Assert.assertTrue(((PrivateCellUtil.compareKeyIgnoresMvcc(COMPARATOR, left, mid)) < 0));
        Assert.assertTrue(((PrivateCellUtil.compareKeyIgnoresMvcc(COMPARATOR, mid, right)) <= 0));
        left = CellUtil.createCell(Bytes.toBytes("a"), Bytes.toBytes("a"), Bytes.toBytes("a"));
        right = CellUtil.createCell(Bytes.toBytes("bbbbbbb"), Bytes.toBytes("a"), Bytes.toBytes("a"));
        mid = HFileWriterImpl.getMidpoint(COMPARATOR, left, right);
        Assert.assertTrue(((PrivateCellUtil.compareKeyIgnoresMvcc(COMPARATOR, left, mid)) < 0));
        Assert.assertTrue(((PrivateCellUtil.compareKeyIgnoresMvcc(COMPARATOR, mid, right)) < 0));
        Assert.assertEquals(1, mid.getRowLength());
        left = CellUtil.createCell(Bytes.toBytes("a"), Bytes.toBytes("a"), Bytes.toBytes("a"));
        right = CellUtil.createCell(Bytes.toBytes("a"), Bytes.toBytes("b"), Bytes.toBytes("a"));
        mid = HFileWriterImpl.getMidpoint(COMPARATOR, left, right);
        Assert.assertTrue(((PrivateCellUtil.compareKeyIgnoresMvcc(COMPARATOR, left, mid)) < 0));
        Assert.assertTrue(((PrivateCellUtil.compareKeyIgnoresMvcc(COMPARATOR, mid, right)) <= 0));
        left = CellUtil.createCell(Bytes.toBytes("a"), Bytes.toBytes("a"), Bytes.toBytes("a"));
        right = CellUtil.createCell(Bytes.toBytes("a"), Bytes.toBytes("aaaaaaaa"), Bytes.toBytes("b"));
        mid = HFileWriterImpl.getMidpoint(COMPARATOR, left, right);
        Assert.assertTrue(((PrivateCellUtil.compareKeyIgnoresMvcc(COMPARATOR, left, mid)) < 0));
        Assert.assertTrue(((PrivateCellUtil.compareKeyIgnoresMvcc(COMPARATOR, mid, right)) < 0));
        Assert.assertEquals(2, mid.getFamilyLength());
        left = CellUtil.createCell(Bytes.toBytes("a"), Bytes.toBytes("a"), Bytes.toBytes("a"));
        right = CellUtil.createCell(Bytes.toBytes("a"), Bytes.toBytes("a"), Bytes.toBytes("aaaaaaaaa"));
        mid = HFileWriterImpl.getMidpoint(COMPARATOR, left, right);
        Assert.assertTrue(((PrivateCellUtil.compareKeyIgnoresMvcc(COMPARATOR, left, mid)) < 0));
        Assert.assertTrue(((PrivateCellUtil.compareKeyIgnoresMvcc(COMPARATOR, mid, right)) < 0));
        Assert.assertEquals(2, mid.getQualifierLength());
        left = CellUtil.createCell(Bytes.toBytes("a"), Bytes.toBytes("a"), Bytes.toBytes("a"));
        right = CellUtil.createCell(Bytes.toBytes("a"), Bytes.toBytes("a"), Bytes.toBytes("b"));
        mid = HFileWriterImpl.getMidpoint(COMPARATOR, left, right);
        Assert.assertTrue(((PrivateCellUtil.compareKeyIgnoresMvcc(COMPARATOR, left, mid)) < 0));
        Assert.assertTrue(((PrivateCellUtil.compareKeyIgnoresMvcc(COMPARATOR, mid, right)) <= 0));
        Assert.assertEquals(1, mid.getQualifierLength());
        // Assert that if meta comparator, it returns the right cell -- i.e. no
        // optimization done.
        left = CellUtil.createCell(Bytes.toBytes("g"), Bytes.toBytes("a"), Bytes.toBytes("a"));
        right = CellUtil.createCell(Bytes.toBytes("i"), Bytes.toBytes("a"), Bytes.toBytes("a"));
        mid = HFileWriterImpl.getMidpoint(META_COMPARATOR, left, right);
        Assert.assertTrue(((PrivateCellUtil.compareKeyIgnoresMvcc(COMPARATOR, left, mid)) < 0));
        Assert.assertTrue(((PrivateCellUtil.compareKeyIgnoresMvcc(COMPARATOR, mid, right)) == 0));
        /**
         * See HBASE-7845
         */
        byte[] rowA = Bytes.toBytes("rowA");
        byte[] rowB = Bytes.toBytes("rowB");
        byte[] family = Bytes.toBytes("family");
        byte[] qualA = Bytes.toBytes("qfA");
        byte[] qualB = Bytes.toBytes("qfB");
        final CellComparatorImpl keyComparator = CellComparatorImpl.COMPARATOR;
        // verify that faked shorter rowkey could be generated
        long ts = 5;
        KeyValue kv1 = new KeyValue(Bytes.toBytes("the quick brown fox"), family, qualA, ts, Put);
        KeyValue kv2 = new KeyValue(Bytes.toBytes("the who test text"), family, qualA, ts, Put);
        Cell newKey = HFileWriterImpl.getMidpoint(keyComparator, kv1, kv2);
        Assert.assertTrue(((keyComparator.compare(kv1, newKey)) < 0));
        Assert.assertTrue(((keyComparator.compare(kv2, newKey)) > 0));
        byte[] expectedArray = Bytes.toBytes("the r");
        Bytes.equals(newKey.getRowArray(), newKey.getRowOffset(), newKey.getRowLength(), expectedArray, 0, expectedArray.length);
        // verify: same with "row + family + qualifier", return rightKey directly
        kv1 = new KeyValue(Bytes.toBytes("ilovehbase"), family, qualA, 5, Put);
        kv2 = new KeyValue(Bytes.toBytes("ilovehbase"), family, qualA, 0, Put);
        Assert.assertTrue(((keyComparator.compare(kv1, kv2)) < 0));
        newKey = HFileWriterImpl.getMidpoint(keyComparator, kv1, kv2);
        Assert.assertTrue(((keyComparator.compare(kv1, newKey)) < 0));
        Assert.assertTrue(((keyComparator.compare(kv2, newKey)) == 0));
        kv1 = new KeyValue(Bytes.toBytes("ilovehbase"), family, qualA, (-5), Put);
        kv2 = new KeyValue(Bytes.toBytes("ilovehbase"), family, qualA, (-10), Put);
        Assert.assertTrue(((keyComparator.compare(kv1, kv2)) < 0));
        newKey = HFileWriterImpl.getMidpoint(keyComparator, kv1, kv2);
        Assert.assertTrue(((keyComparator.compare(kv1, newKey)) < 0));
        Assert.assertTrue(((keyComparator.compare(kv2, newKey)) == 0));
        // verify: same with row, different with qualifier
        kv1 = new KeyValue(Bytes.toBytes("ilovehbase"), family, qualA, 5, Put);
        kv2 = new KeyValue(Bytes.toBytes("ilovehbase"), family, qualB, 5, Put);
        Assert.assertTrue(((keyComparator.compare(kv1, kv2)) < 0));
        newKey = HFileWriterImpl.getMidpoint(keyComparator, kv1, kv2);
        Assert.assertTrue(((keyComparator.compare(kv1, newKey)) < 0));
        Assert.assertTrue(((keyComparator.compare(kv2, newKey)) > 0));
        Assert.assertTrue(Arrays.equals(CellUtil.cloneFamily(newKey), family));
        Assert.assertTrue(Arrays.equals(CellUtil.cloneQualifier(newKey), qualB));
        Assert.assertTrue(((newKey.getTimestamp()) == (HConstants.LATEST_TIMESTAMP)));
        Assert.assertTrue(((newKey.getTypeByte()) == (Type.Maximum.getCode())));
        // verify metaKeyComparator's getShortMidpointKey output
        final CellComparatorImpl metaKeyComparator = CellComparatorImpl.META_COMPARATOR;
        kv1 = new KeyValue(Bytes.toBytes("ilovehbase123"), family, qualA, 5, Put);
        kv2 = new KeyValue(Bytes.toBytes("ilovehbase234"), family, qualA, 0, Put);
        newKey = HFileWriterImpl.getMidpoint(metaKeyComparator, kv1, kv2);
        Assert.assertTrue(((metaKeyComparator.compare(kv1, newKey)) < 0));
        Assert.assertTrue(((metaKeyComparator.compare(kv2, newKey)) == 0));
        // verify common fix scenario
        kv1 = new KeyValue(Bytes.toBytes("ilovehbase"), family, qualA, ts, Put);
        kv2 = new KeyValue(Bytes.toBytes("ilovehbaseandhdfs"), family, qualA, ts, Put);
        Assert.assertTrue(((keyComparator.compare(kv1, kv2)) < 0));
        newKey = HFileWriterImpl.getMidpoint(keyComparator, kv1, kv2);
        Assert.assertTrue(((keyComparator.compare(kv1, newKey)) < 0));
        Assert.assertTrue(((keyComparator.compare(kv2, newKey)) > 0));
        expectedArray = Bytes.toBytes("ilovehbasea");
        Bytes.equals(newKey.getRowArray(), newKey.getRowOffset(), newKey.getRowLength(), expectedArray, 0, expectedArray.length);
        // verify only 1 offset scenario
        kv1 = new KeyValue(Bytes.toBytes("100abcdefg"), family, qualA, ts, Put);
        kv2 = new KeyValue(Bytes.toBytes("101abcdefg"), family, qualA, ts, Put);
        Assert.assertTrue(((keyComparator.compare(kv1, kv2)) < 0));
        newKey = HFileWriterImpl.getMidpoint(keyComparator, kv1, kv2);
        Assert.assertTrue(((keyComparator.compare(kv1, newKey)) < 0));
        Assert.assertTrue(((keyComparator.compare(kv2, newKey)) > 0));
        expectedArray = Bytes.toBytes("101");
        Bytes.equals(newKey.getRowArray(), newKey.getRowOffset(), newKey.getRowLength(), expectedArray, 0, expectedArray.length);
    }
}

