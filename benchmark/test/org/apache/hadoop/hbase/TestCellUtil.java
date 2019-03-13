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


import HConstants.EMPTY_BYTE_ARRAY;
import KeyValue.Type;
import KeyValue.Type.Maximum;
import KeyValue.Type.Minimum;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.NavigableMap;
import org.apache.hadoop.hbase.testclassification.MiscTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;

import static HConstants.EMPTY_BYTE_ARRAY;
import static KeyValue.FAMILY_LENGTH_SIZE;
import static KeyValue.ROW_LENGTH_SIZE;
import static KeyValue.TIMESTAMP_SIZE;
import static KeyValue.TIMESTAMP_TYPE_SIZE;


@Category({ MiscTests.class, SmallTests.class })
public class TestCellUtil {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestCellUtil.class);

    /**
     * CellScannable used in test. Returns a {@link TestCellScanner}
     */
    private static class TestCellScannable implements CellScannable {
        private final int cellsCount;

        TestCellScannable(final int cellsCount) {
            this.cellsCount = cellsCount;
        }

        @Override
        public CellScanner cellScanner() {
            return new TestCellUtil.TestCellScanner(this.cellsCount);
        }
    }

    /**
     * CellScanner used in test.
     */
    private static class TestCellScanner implements CellScanner {
        private int count = 0;

        private Cell current = null;

        private final int cellsCount;

        TestCellScanner(final int cellsCount) {
            this.cellsCount = cellsCount;
        }

        @Override
        public Cell current() {
            return this.current;
        }

        @Override
        public boolean advance() throws IOException {
            if ((this.count) < (cellsCount)) {
                this.current = new TestCellUtil.TestCell(this.count);
                (this.count)++;
                return true;
            }
            return false;
        }
    }

    /**
     * Cell used in test. Has row only.
     */
    private static class TestCell implements Cell {
        private final byte[] row;

        TestCell(final int i) {
            this.row = Bytes.toBytes(i);
        }

        @Override
        public byte[] getRowArray() {
            return this.row;
        }

        @Override
        public int getRowOffset() {
            return 0;
        }

        @Override
        public short getRowLength() {
            return ((short) (this.row.length));
        }

        @Override
        public byte[] getFamilyArray() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public int getFamilyOffset() {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public byte getFamilyLength() {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public byte[] getQualifierArray() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public int getQualifierOffset() {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public int getQualifierLength() {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public long getTimestamp() {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public byte getTypeByte() {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public byte[] getValueArray() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public int getValueOffset() {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public int getValueLength() {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public int getSerializedSize() {
            return 0;
        }

        @Override
        public byte[] getTagsArray() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public int getTagsOffset() {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public long getSequenceId() {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public int getTagsLength() {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public long heapSize() {
            return 0;
        }
    }

    /**
     * Was overflowing if 100k or so lists of cellscanners to return.
     */
    @Test
    public void testCreateCellScannerOverflow() throws IOException {
        consume(doCreateCellScanner(1, 1), (1 * 1));
        consume(doCreateCellScanner(3, 0), (3 * 0));
        consume(doCreateCellScanner(3, 3), (3 * 3));
        consume(doCreateCellScanner(0, 1), (0 * 1));
        // Do big number. See HBASE-11813 for why.
        final int hundredK = 100000;
        consume(doCreateCellScanner(hundredK, 0), (hundredK * 0));
        consume(doCreateCellArray(1), 1);
        consume(doCreateCellArray(0), 0);
        consume(doCreateCellArray(3), 3);
        List<CellScannable> cells = new ArrayList<>(hundredK);
        for (int i = 0; i < hundredK; i++) {
            cells.add(new TestCellUtil.TestCellScannable(1));
        }
        consume(CellUtil.createCellScanner(cells), (hundredK * 1));
        NavigableMap<byte[], List<Cell>> m = new java.util.TreeMap(Bytes.BYTES_COMPARATOR);
        List<Cell> cellArray = new ArrayList<>(hundredK);
        for (int i = 0; i < hundredK; i++) {
            cellArray.add(new TestCellUtil.TestCell(i));
        }
        m.put(new byte[]{ 'f' }, cellArray);
        consume(CellUtil.createCellScanner(m), (hundredK * 1));
    }

    @Test
    public void testOverlappingKeys() {
        byte[] empty = EMPTY_BYTE_ARRAY;
        byte[] a = Bytes.toBytes("a");
        byte[] b = Bytes.toBytes("b");
        byte[] c = Bytes.toBytes("c");
        byte[] d = Bytes.toBytes("d");
        // overlaps
        Assert.assertTrue(PrivateCellUtil.overlappingKeys(a, b, a, b));
        Assert.assertTrue(PrivateCellUtil.overlappingKeys(a, c, a, b));
        Assert.assertTrue(PrivateCellUtil.overlappingKeys(a, b, a, c));
        Assert.assertTrue(PrivateCellUtil.overlappingKeys(b, c, a, c));
        Assert.assertTrue(PrivateCellUtil.overlappingKeys(a, c, b, c));
        Assert.assertTrue(PrivateCellUtil.overlappingKeys(a, d, b, c));
        Assert.assertTrue(PrivateCellUtil.overlappingKeys(b, c, a, d));
        Assert.assertTrue(PrivateCellUtil.overlappingKeys(empty, b, a, b));
        Assert.assertTrue(PrivateCellUtil.overlappingKeys(empty, b, a, c));
        Assert.assertTrue(PrivateCellUtil.overlappingKeys(a, b, empty, b));
        Assert.assertTrue(PrivateCellUtil.overlappingKeys(a, b, empty, c));
        Assert.assertTrue(PrivateCellUtil.overlappingKeys(a, empty, a, b));
        Assert.assertTrue(PrivateCellUtil.overlappingKeys(a, empty, a, c));
        Assert.assertTrue(PrivateCellUtil.overlappingKeys(a, b, empty, empty));
        Assert.assertTrue(PrivateCellUtil.overlappingKeys(empty, empty, a, b));
        // non overlaps
        Assert.assertFalse(PrivateCellUtil.overlappingKeys(a, b, c, d));
        Assert.assertFalse(PrivateCellUtil.overlappingKeys(c, d, a, b));
        Assert.assertFalse(PrivateCellUtil.overlappingKeys(b, c, c, d));
        Assert.assertFalse(PrivateCellUtil.overlappingKeys(b, c, c, empty));
        Assert.assertFalse(PrivateCellUtil.overlappingKeys(b, c, d, empty));
        Assert.assertFalse(PrivateCellUtil.overlappingKeys(c, d, b, c));
        Assert.assertFalse(PrivateCellUtil.overlappingKeys(c, empty, b, c));
        Assert.assertFalse(PrivateCellUtil.overlappingKeys(d, empty, b, c));
        Assert.assertFalse(PrivateCellUtil.overlappingKeys(b, c, a, b));
        Assert.assertFalse(PrivateCellUtil.overlappingKeys(b, c, empty, b));
        Assert.assertFalse(PrivateCellUtil.overlappingKeys(b, c, empty, a));
        Assert.assertFalse(PrivateCellUtil.overlappingKeys(a, b, b, c));
        Assert.assertFalse(PrivateCellUtil.overlappingKeys(empty, b, b, c));
        Assert.assertFalse(PrivateCellUtil.overlappingKeys(empty, a, b, c));
    }

    @Test
    public void testFindCommonPrefixInFlatKey() {
        // The whole key matching case
        KeyValue kv1 = new KeyValue(Bytes.toBytes("r1"), Bytes.toBytes("f1"), Bytes.toBytes("q1"), null);
        Assert.assertEquals(kv1.getKeyLength(), PrivateCellUtil.findCommonPrefixInFlatKey(kv1, kv1, true, true));
        Assert.assertEquals(kv1.getKeyLength(), PrivateCellUtil.findCommonPrefixInFlatKey(kv1, kv1, false, true));
        Assert.assertEquals(((kv1.getKeyLength()) - (TIMESTAMP_TYPE_SIZE)), PrivateCellUtil.findCommonPrefixInFlatKey(kv1, kv1, true, false));
        // The rk length itself mismatch
        KeyValue kv2 = new KeyValue(Bytes.toBytes("r12"), Bytes.toBytes("f1"), Bytes.toBytes("q1"), null);
        Assert.assertEquals(1, PrivateCellUtil.findCommonPrefixInFlatKey(kv1, kv2, true, true));
        // part of rk is same
        KeyValue kv3 = new KeyValue(Bytes.toBytes("r14"), Bytes.toBytes("f1"), Bytes.toBytes("q1"), null);
        Assert.assertEquals(((ROW_LENGTH_SIZE) + (Bytes.toBytes("r1").length)), PrivateCellUtil.findCommonPrefixInFlatKey(kv2, kv3, true, true));
        // entire rk is same but different cf name
        KeyValue kv4 = new KeyValue(Bytes.toBytes("r14"), Bytes.toBytes("f2"), Bytes.toBytes("q1"), null);
        Assert.assertEquals(((((ROW_LENGTH_SIZE) + (kv3.getRowLength())) + (FAMILY_LENGTH_SIZE)) + (Bytes.toBytes("f").length)), PrivateCellUtil.findCommonPrefixInFlatKey(kv3, kv4, false, true));
        // rk and family are same and part of qualifier
        KeyValue kv5 = new KeyValue(Bytes.toBytes("r14"), Bytes.toBytes("f2"), Bytes.toBytes("q123"), null);
        Assert.assertEquals((((((ROW_LENGTH_SIZE) + (kv3.getRowLength())) + (FAMILY_LENGTH_SIZE)) + (kv4.getFamilyLength())) + (kv4.getQualifierLength())), PrivateCellUtil.findCommonPrefixInFlatKey(kv4, kv5, true, true));
        // rk, cf and q are same. ts differs
        KeyValue kv6 = new KeyValue(Bytes.toBytes("rk"), 1234L);
        KeyValue kv7 = new KeyValue(Bytes.toBytes("rk"), 1235L);
        // only last byte out of 8 ts bytes in ts part differs
        Assert.assertEquals(((((((ROW_LENGTH_SIZE) + (kv6.getRowLength())) + (FAMILY_LENGTH_SIZE)) + (kv6.getFamilyLength())) + (kv6.getQualifierLength())) + 7), PrivateCellUtil.findCommonPrefixInFlatKey(kv6, kv7, true, true));
        // rk, cf, q and ts are same. Only type differs
        KeyValue kv8 = new KeyValue(Bytes.toBytes("rk"), 1234L, Type.Delete);
        Assert.assertEquals(((((((ROW_LENGTH_SIZE) + (kv6.getRowLength())) + (FAMILY_LENGTH_SIZE)) + (kv6.getFamilyLength())) + (kv6.getQualifierLength())) + (TIMESTAMP_SIZE)), PrivateCellUtil.findCommonPrefixInFlatKey(kv6, kv8, true, true));
        // With out TS_TYPE check
        Assert.assertEquals((((((ROW_LENGTH_SIZE) + (kv6.getRowLength())) + (FAMILY_LENGTH_SIZE)) + (kv6.getFamilyLength())) + (kv6.getQualifierLength())), PrivateCellUtil.findCommonPrefixInFlatKey(kv6, kv8, true, false));
    }

    /**
     * Assert CellUtil makes Cell toStrings same way we do KeyValue toStrings.
     */
    @Test
    public void testToString() {
        byte[] row = Bytes.toBytes("row");
        long ts = 123L;
        // Make a KeyValue and a Cell and see if same toString result.
        KeyValue kv = new KeyValue(row, EMPTY_BYTE_ARRAY, EMPTY_BYTE_ARRAY, ts, Type.Minimum, EMPTY_BYTE_ARRAY);
        Cell cell = CellUtil.createCell(row, EMPTY_BYTE_ARRAY, EMPTY_BYTE_ARRAY, ts, Minimum.getCode(), EMPTY_BYTE_ARRAY);
        String cellToString = CellUtil.getCellKeyAsString(cell);
        Assert.assertEquals(kv.toString(), cellToString);
        // Do another w/ non-null family.
        byte[] f = new byte[]{ 'f' };
        byte[] q = new byte[]{ 'q' };
        kv = new KeyValue(row, f, q, ts, Type.Minimum, EMPTY_BYTE_ARRAY);
        cell = CellUtil.createCell(row, f, q, ts, Minimum.getCode(), EMPTY_BYTE_ARRAY);
        cellToString = CellUtil.getCellKeyAsString(cell);
        Assert.assertEquals(kv.toString(), cellToString);
    }

    @Test
    public void testToString1() {
        String row = "test.row";
        String family = "test.family";
        String qualifier = "test.qualifier";
        long timestamp = 42;
        KeyValue.Type type = Type.Put;
        String value = "test.value";
        long seqId = 1042;
        Cell cell = CellUtil.createCell(Bytes.toBytes(row), Bytes.toBytes(family), Bytes.toBytes(qualifier), timestamp, type.getCode(), Bytes.toBytes(value), seqId);
        String nonVerbose = CellUtil.toString(cell, false);
        String verbose = CellUtil.toString(cell, true);
        System.out.println(("nonVerbose=" + nonVerbose));
        System.out.println(("verbose=" + verbose));
        Assert.assertEquals(String.format("%s/%s:%s/%d/%s/vlen=%s/seqid=%s", row, family, qualifier, timestamp, type.toString(), Bytes.toBytes(value).length, seqId), nonVerbose);
        Assert.assertEquals(String.format("%s/%s:%s/%d/%s/vlen=%s/seqid=%s/%s", row, family, qualifier, timestamp, type.toString(), Bytes.toBytes(value).length, seqId, value), verbose);
        // TODO: test with tags
    }

    @Test
    public void testCloneCellFieldsFromByteBufferedCell() {
        byte[] r = Bytes.toBytes("row1");
        byte[] f = Bytes.toBytes("cf1");
        byte[] q = Bytes.toBytes("qual1");
        byte[] v = Bytes.toBytes("val1");
        byte[] tags = Bytes.toBytes("tag1");
        KeyValue kv = new KeyValue(r, f, q, 0, q.length, 1234L, Type.Put, v, 0, v.length, tags);
        ByteBuffer buffer = ByteBuffer.wrap(kv.getBuffer());
        Cell bbCell = new ByteBufferKeyValue(buffer, 0, buffer.remaining());
        byte[] rDest = CellUtil.cloneRow(bbCell);
        Assert.assertTrue(Bytes.equals(r, rDest));
        byte[] fDest = CellUtil.cloneFamily(bbCell);
        Assert.assertTrue(Bytes.equals(f, fDest));
        byte[] qDest = CellUtil.cloneQualifier(bbCell);
        Assert.assertTrue(Bytes.equals(q, qDest));
        byte[] vDest = CellUtil.cloneValue(bbCell);
        Assert.assertTrue(Bytes.equals(v, vDest));
        byte[] tDest = new byte[tags.length];
        PrivateCellUtil.copyTagsTo(bbCell, tDest, 0);
        Assert.assertTrue(Bytes.equals(tags, tDest));
    }

    @Test
    public void testMatchingCellFieldsFromByteBufferedCell() {
        byte[] r = Bytes.toBytes("row1");
        byte[] f = Bytes.toBytes("cf1");
        byte[] q1 = Bytes.toBytes("qual1");
        byte[] q2 = Bytes.toBytes("qual2");
        byte[] v = Bytes.toBytes("val1");
        byte[] tags = Bytes.toBytes("tag1");
        KeyValue kv = new KeyValue(r, f, q1, 0, q1.length, 1234L, Type.Put, v, 0, v.length, tags);
        ByteBuffer buffer = ByteBuffer.wrap(kv.getBuffer());
        Cell bbCell1 = new ByteBufferKeyValue(buffer, 0, buffer.remaining());
        kv = new KeyValue(r, f, q2, 0, q2.length, 1234L, Type.Put, v, 0, v.length, tags);
        buffer = ByteBuffer.wrap(kv.getBuffer());
        Cell bbCell2 = new ByteBufferKeyValue(buffer, 0, buffer.remaining());
        Assert.assertTrue(CellUtil.matchingRows(bbCell1, bbCell2));
        Assert.assertTrue(CellUtil.matchingRows(kv, bbCell2));
        Assert.assertTrue(CellUtil.matchingRows(bbCell1, r));
        Assert.assertTrue(CellUtil.matchingFamily(bbCell1, bbCell2));
        Assert.assertTrue(CellUtil.matchingFamily(kv, bbCell2));
        Assert.assertTrue(CellUtil.matchingFamily(bbCell1, f));
        Assert.assertFalse(CellUtil.matchingQualifier(bbCell1, bbCell2));
        Assert.assertTrue(CellUtil.matchingQualifier(kv, bbCell2));
        Assert.assertTrue(CellUtil.matchingQualifier(bbCell1, q1));
        Assert.assertTrue(CellUtil.matchingQualifier(bbCell2, q2));
        Assert.assertTrue(CellUtil.matchingValue(bbCell1, bbCell2));
        Assert.assertTrue(CellUtil.matchingValue(kv, bbCell2));
        Assert.assertTrue(CellUtil.matchingValue(bbCell1, v));
        Assert.assertFalse(CellUtil.matchingColumn(bbCell1, bbCell2));
        Assert.assertTrue(CellUtil.matchingColumn(kv, bbCell2));
        Assert.assertTrue(CellUtil.matchingColumn(bbCell1, f, q1));
        Assert.assertTrue(CellUtil.matchingColumn(bbCell2, f, q2));
    }

    @Test
    public void testCellFieldsAsPrimitiveTypesFromByteBufferedCell() {
        int ri = 123;
        byte[] r = Bytes.toBytes(ri);
        byte[] f = Bytes.toBytes("cf1");
        byte[] q = Bytes.toBytes("qual1");
        long vl = 10981L;
        byte[] v = Bytes.toBytes(vl);
        KeyValue kv = new KeyValue(r, f, q, v);
        ByteBuffer buffer = ByteBuffer.wrap(kv.getBuffer());
        Cell bbCell = new ByteBufferKeyValue(buffer, 0, buffer.remaining());
        Assert.assertEquals(ri, PrivateCellUtil.getRowAsInt(bbCell));
        Assert.assertEquals(vl, PrivateCellUtil.getValueAsLong(bbCell));
        double vd = 3005.5;
        v = Bytes.toBytes(vd);
        kv = new KeyValue(r, f, q, v);
        buffer = ByteBuffer.wrap(kv.getBuffer());
        bbCell = new ByteBufferKeyValue(buffer, 0, buffer.remaining());
        Assert.assertEquals(vd, PrivateCellUtil.getValueAsDouble(bbCell), 0.0);
        BigDecimal bd = new BigDecimal(9999);
        v = Bytes.toBytes(bd);
        kv = new KeyValue(r, f, q, v);
        buffer = ByteBuffer.wrap(kv.getBuffer());
        bbCell = new ByteBufferKeyValue(buffer, 0, buffer.remaining());
        Assert.assertEquals(bd, PrivateCellUtil.getValueAsBigDecimal(bbCell));
    }

    @Test
    public void testWriteCell() throws IOException {
        byte[] r = Bytes.toBytes("row1");
        byte[] f = Bytes.toBytes("cf1");
        byte[] q1 = Bytes.toBytes("qual1");
        byte[] q2 = Bytes.toBytes("qual2");
        byte[] v = Bytes.toBytes("val1");
        byte[] tags = Bytes.toBytes("tag1");
        KeyValue kv = new KeyValue(r, f, q1, 0, q1.length, 1234L, Type.Put, v, 0, v.length, tags);
        TestCellUtil.NonExtendedCell nonExtCell = new TestCellUtil.NonExtendedCell(kv);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        int writeCell = PrivateCellUtil.writeCell(nonExtCell, os, true);
        byte[] byteArray = os.toByteArray();
        KeyValue res = new KeyValue(byteArray);
        Assert.assertTrue(CellUtil.equals(kv, res));
    }

    @Test
    public void testGetType() throws IOException {
        Cell c = Mockito.mock(Cell.class);
        Mockito.when(c.getType()).thenCallRealMethod();
        for (Cell.Type type : Cell.Type.values()) {
            Mockito.when(c.getTypeByte()).thenReturn(type.getCode());
            Assert.assertEquals(type, c.getType());
        }
        try {
            Mockito.when(c.getTypeByte()).thenReturn(Maximum.getCode());
            c.getType();
            Assert.fail("The code of Maximum can't be handled by Cell.Type");
        } catch (UnsupportedOperationException e) {
        }
        try {
            Mockito.when(c.getTypeByte()).thenReturn(Minimum.getCode());
            c.getType();
            Assert.fail("The code of Maximum can't be handled by Cell.Type");
        } catch (UnsupportedOperationException e) {
        }
    }

    private static class NonExtendedCell implements Cell {
        private KeyValue kv;

        public NonExtendedCell(KeyValue kv) {
            this.kv = kv;
        }

        @Override
        public byte[] getRowArray() {
            return this.kv.getRowArray();
        }

        @Override
        public int getRowOffset() {
            return this.kv.getRowOffset();
        }

        @Override
        public short getRowLength() {
            return this.kv.getRowLength();
        }

        @Override
        public byte[] getFamilyArray() {
            return this.kv.getFamilyArray();
        }

        @Override
        public int getFamilyOffset() {
            return this.kv.getFamilyOffset();
        }

        @Override
        public byte getFamilyLength() {
            return this.kv.getFamilyLength();
        }

        @Override
        public byte[] getQualifierArray() {
            return this.kv.getQualifierArray();
        }

        @Override
        public int getQualifierOffset() {
            return this.kv.getQualifierOffset();
        }

        @Override
        public int getQualifierLength() {
            return this.kv.getQualifierLength();
        }

        @Override
        public long getTimestamp() {
            return this.kv.getTimestamp();
        }

        @Override
        public byte getTypeByte() {
            return this.kv.getTypeByte();
        }

        @Override
        public long getSequenceId() {
            return this.kv.getSequenceId();
        }

        @Override
        public byte[] getValueArray() {
            return this.kv.getValueArray();
        }

        @Override
        public int getValueOffset() {
            return this.kv.getValueOffset();
        }

        @Override
        public int getValueLength() {
            return this.kv.getValueLength();
        }

        @Override
        public int getSerializedSize() {
            return this.kv.getSerializedSize();
        }

        @Override
        public byte[] getTagsArray() {
            return this.kv.getTagsArray();
        }

        @Override
        public int getTagsOffset() {
            return this.kv.getTagsOffset();
        }

        @Override
        public int getTagsLength() {
            return this.kv.getTagsLength();
        }

        @Override
        public long heapSize() {
            return this.kv.heapSize();
        }
    }
}

