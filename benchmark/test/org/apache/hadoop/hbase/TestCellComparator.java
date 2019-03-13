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
package org.apache.hadoop.hbase;


import CellComparatorImpl.COMPARATOR;
import TableName.META_TABLE_NAME;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Set;
import org.apache.hadoop.hbase.KeyValue.Type;
import org.apache.hadoop.hbase.testclassification.MiscTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static CellComparatorImpl.COMPARATOR;
import static CellComparatorImpl.META_COMPARATOR;


@Category({ MiscTests.class, SmallTests.class })
public class TestCellComparator {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestCellComparator.class);

    private CellComparator comparator = CellComparator.getInstance();

    byte[] row1 = Bytes.toBytes("row1");

    byte[] row2 = Bytes.toBytes("row2");

    byte[] row_1_0 = Bytes.toBytes("row10");

    byte[] fam1 = Bytes.toBytes("fam1");

    byte[] fam2 = Bytes.toBytes("fam2");

    byte[] fam_1_2 = Bytes.toBytes("fam12");

    byte[] qual1 = Bytes.toBytes("qual1");

    byte[] qual2 = Bytes.toBytes("qual2");

    byte[] val = Bytes.toBytes("val");

    @Test
    public void testCompareCells() {
        KeyValue kv1 = new KeyValue(row1, fam1, qual1, val);
        KeyValue kv2 = new KeyValue(row2, fam1, qual1, val);
        Assert.assertTrue(((comparator.compare(kv1, kv2)) < 0));
        kv1 = new KeyValue(row1, fam2, qual1, val);
        kv2 = new KeyValue(row1, fam1, qual1, val);
        Assert.assertTrue(((comparator.compareFamilies(kv1, kv2)) > 0));
        kv1 = new KeyValue(row1, fam1, qual1, 1L, val);
        kv2 = new KeyValue(row1, fam1, qual1, 2L, val);
        Assert.assertTrue(((comparator.compare(kv1, kv2)) > 0));
        kv1 = new KeyValue(row1, fam1, qual1, 1L, Type.Put);
        kv2 = new KeyValue(row1, fam1, qual1, 1L, Type.Maximum);
        Assert.assertTrue(((comparator.compare(kv1, kv2)) > 0));
        kv1 = new KeyValue(row1, fam1, qual1, 1L, Type.Put);
        kv2 = new KeyValue(row1, fam1, qual1, 1L, Type.Put);
        Assert.assertTrue(CellUtil.equals(kv1, kv2));
    }

    @Test
    public void testCompareCellWithKey() throws Exception {
        KeyValue kv1 = new KeyValue(row1, fam1, qual1, val);
        KeyValue kv2 = new KeyValue(row2, fam1, qual1, val);
        Assert.assertTrue(((PrivateCellUtil.compare(comparator, kv1, kv2.getKey(), 0, kv2.getKey().length)) < 0));
        kv1 = new KeyValue(row1, fam2, qual1, val);
        kv2 = new KeyValue(row1, fam1, qual1, val);
        Assert.assertTrue(((PrivateCellUtil.compare(comparator, kv1, kv2.getKey(), 0, kv2.getKey().length)) > 0));
        kv1 = new KeyValue(row1, fam1, qual1, 1L, val);
        kv2 = new KeyValue(row1, fam1, qual1, 2L, val);
        Assert.assertTrue(((PrivateCellUtil.compare(comparator, kv1, kv2.getKey(), 0, kv2.getKey().length)) > 0));
        kv1 = new KeyValue(row1, fam1, qual1, 1L, Type.Put);
        kv2 = new KeyValue(row1, fam1, qual1, 1L, Type.Maximum);
        Assert.assertTrue(((PrivateCellUtil.compare(comparator, kv1, kv2.getKey(), 0, kv2.getKey().length)) > 0));
        kv1 = new KeyValue(row1, fam1, qual1, 1L, Type.Put);
        kv2 = new KeyValue(row1, fam1, qual1, 1L, Type.Put);
        Assert.assertTrue(((PrivateCellUtil.compare(comparator, kv1, kv2.getKey(), 0, kv2.getKey().length)) == 0));
    }

    @Test
    public void testCompareByteBufferedCell() {
        byte[] r1 = Bytes.toBytes("row1");
        byte[] r2 = Bytes.toBytes("row2");
        byte[] f1 = Bytes.toBytes("cf1");
        byte[] q1 = Bytes.toBytes("qual1");
        byte[] q2 = Bytes.toBytes("qual2");
        byte[] v = Bytes.toBytes("val1");
        KeyValue kv = new KeyValue(r1, f1, q1, v);
        ByteBuffer buffer = ByteBuffer.wrap(kv.getBuffer());
        Cell bbCell1 = new ByteBufferKeyValue(buffer, 0, buffer.remaining());
        kv = new KeyValue(r2, f1, q1, v);
        buffer = ByteBuffer.wrap(kv.getBuffer());
        Cell bbCell2 = new ByteBufferKeyValue(buffer, 0, buffer.remaining());
        // compareColumns not on CellComparator so use Impl directly
        Assert.assertEquals(0, COMPARATOR.compareColumns(bbCell1, bbCell2));
        Assert.assertEquals(0, COMPARATOR.compareColumns(bbCell1, kv));
        kv = new KeyValue(r2, f1, q2, v);
        buffer = ByteBuffer.wrap(kv.getBuffer());
        Cell bbCell3 = new ByteBufferKeyValue(buffer, 0, buffer.remaining());
        Assert.assertEquals(0, comparator.compareFamilies(bbCell2, bbCell3));
        Assert.assertTrue(((comparator.compareQualifiers(bbCell2, bbCell3)) < 0));
        Assert.assertTrue(((COMPARATOR.compareColumns(bbCell2, bbCell3)) < 0));
        Assert.assertEquals(0, comparator.compareRows(bbCell2, bbCell3));
        Assert.assertTrue(((comparator.compareRows(bbCell1, bbCell2)) < 0));
    }

    /**
     * Test meta comparisons using our new ByteBufferKeyValue Cell type, the type we use everywhere
     * in 2.0.
     */
    @Test
    public void testMetaComparisons() throws Exception {
        long now = System.currentTimeMillis();
        // Meta compares
        Cell aaa = TestCellComparator.createByteBufferKeyValueFromKeyValue(new KeyValue(Bytes.toBytes("TestScanMultipleVersions,row_0500,1236020145502"), now));
        Cell bbb = TestCellComparator.createByteBufferKeyValueFromKeyValue(new KeyValue(Bytes.toBytes("TestScanMultipleVersions,,99999999999999"), now));
        CellComparator c = META_COMPARATOR;
        Assert.assertTrue(((c.compare(bbb, aaa)) < 0));
        Cell ccc = TestCellComparator.createByteBufferKeyValueFromKeyValue(new KeyValue(Bytes.toBytes("TestScanMultipleVersions,,1236023996656"), Bytes.toBytes("info"), Bytes.toBytes("regioninfo"), 1236024396271L, ((byte[]) (null))));
        Assert.assertTrue(((c.compare(ccc, bbb)) < 0));
        Cell x = TestCellComparator.createByteBufferKeyValueFromKeyValue(new KeyValue(Bytes.toBytes("TestScanMultipleVersions,row_0500,1236034574162"), Bytes.toBytes("info"), Bytes.toBytes(""), 9223372036854775807L, ((byte[]) (null))));
        Cell y = TestCellComparator.createByteBufferKeyValueFromKeyValue(new KeyValue(Bytes.toBytes("TestScanMultipleVersions,row_0500,1236034574162"), Bytes.toBytes("info"), Bytes.toBytes("regioninfo"), 1236034574912L, ((byte[]) (null))));
        Assert.assertTrue(((c.compare(x, y)) < 0));
    }

    /**
     * More tests using ByteBufferKeyValue copied over from TestKeyValue which uses old KVs only.
     */
    @Test
    public void testMetaComparisons2() {
        long now = System.currentTimeMillis();
        CellComparator c = META_COMPARATOR;
        Assert.assertTrue(((c.compare(TestCellComparator.createByteBufferKeyValueFromKeyValue(new KeyValue(Bytes.toBytes(((META_TABLE_NAME.getNameAsString()) + ",a,,0,1")), now)), TestCellComparator.createByteBufferKeyValueFromKeyValue(new KeyValue(Bytes.toBytes(((META_TABLE_NAME.getNameAsString()) + ",a,,0,1")), now)))) == 0));
        Cell a = TestCellComparator.createByteBufferKeyValueFromKeyValue(new KeyValue(Bytes.toBytes(((META_TABLE_NAME.getNameAsString()) + ",a,,0,1")), now));
        Cell b = TestCellComparator.createByteBufferKeyValueFromKeyValue(new KeyValue(Bytes.toBytes(((META_TABLE_NAME.getNameAsString()) + ",a,,0,2")), now));
        Assert.assertTrue(((c.compare(a, b)) < 0));
        Assert.assertTrue(((c.compare(TestCellComparator.createByteBufferKeyValueFromKeyValue(new KeyValue(Bytes.toBytes(((META_TABLE_NAME.getNameAsString()) + ",a,,0,2")), now)), TestCellComparator.createByteBufferKeyValueFromKeyValue(new KeyValue(Bytes.toBytes(((META_TABLE_NAME.getNameAsString()) + ",a,,0,1")), now)))) > 0));
        Assert.assertTrue(((c.compare(TestCellComparator.createByteBufferKeyValueFromKeyValue(new KeyValue(Bytes.toBytes(((META_TABLE_NAME.getNameAsString()) + ",,1")), now)), TestCellComparator.createByteBufferKeyValueFromKeyValue(new KeyValue(Bytes.toBytes(((META_TABLE_NAME.getNameAsString()) + ",,1")), now)))) == 0));
        Assert.assertTrue(((c.compare(TestCellComparator.createByteBufferKeyValueFromKeyValue(new KeyValue(Bytes.toBytes(((META_TABLE_NAME.getNameAsString()) + ",,1")), now)), TestCellComparator.createByteBufferKeyValueFromKeyValue(new KeyValue(Bytes.toBytes(((META_TABLE_NAME.getNameAsString()) + ",,2")), now)))) < 0));
        Assert.assertTrue(((c.compare(TestCellComparator.createByteBufferKeyValueFromKeyValue(new KeyValue(Bytes.toBytes(((META_TABLE_NAME.getNameAsString()) + ",,2")), now)), TestCellComparator.createByteBufferKeyValueFromKeyValue(new KeyValue(Bytes.toBytes(((META_TABLE_NAME.getNameAsString()) + ",,1")), now)))) > 0));
    }

    @Test
    public void testBinaryKeys() throws Exception {
        Set<Cell> set = new java.util.TreeSet(COMPARATOR);
        final byte[] fam = Bytes.toBytes("col");
        final byte[] qf = Bytes.toBytes("umn");
        final byte[] nb = new byte[0];
        Cell[] keys = new Cell[]{ TestCellComparator.createByteBufferKeyValueFromKeyValue(new KeyValue(Bytes.toBytes("aaaaa,\u0000\u0000,2"), fam, qf, 2, nb)), TestCellComparator.createByteBufferKeyValueFromKeyValue(new KeyValue(Bytes.toBytes("aaaaa,\u0001,3"), fam, qf, 3, nb)), TestCellComparator.createByteBufferKeyValueFromKeyValue(new KeyValue(Bytes.toBytes("aaaaa,,1"), fam, qf, 1, nb)), TestCellComparator.createByteBufferKeyValueFromKeyValue(new KeyValue(Bytes.toBytes("aaaaa,\u1000,5"), fam, qf, 5, nb)), TestCellComparator.createByteBufferKeyValueFromKeyValue(new KeyValue(Bytes.toBytes("aaaaa,a,4"), fam, qf, 4, nb)), TestCellComparator.createByteBufferKeyValueFromKeyValue(new KeyValue(Bytes.toBytes("a,a,0"), fam, qf, 0, nb)) };
        // Add to set with bad comparator
        Collections.addAll(set, keys);
        // This will output the keys incorrectly.
        boolean assertion = false;
        int count = 0;
        try {
            for (Cell k : set) {
                Assert.assertTrue(((("count=" + count) + ", ") + (k.toString())), ((count++) == (k.getTimestamp())));
            }
        } catch (AssertionError e) {
            // Expected
            assertion = true;
        }
        Assert.assertTrue(assertion);
        // Make set with good comparator
        set = new java.util.TreeSet(META_COMPARATOR);
        Collections.addAll(set, keys);
        count = 0;
        for (Cell k : set) {
            Assert.assertTrue(((("count=" + count) + ", ") + (k.toString())), ((count++) == (k.getTimestamp())));
        }
    }
}

