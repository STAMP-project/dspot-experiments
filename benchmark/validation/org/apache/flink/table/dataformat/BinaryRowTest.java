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
package org.apache.flink.table.dataformat;


import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.typeutils.base.StringSerializer;
import org.apache.flink.core.memory.MemorySegment;
import org.apache.flink.core.memory.MemorySegmentFactory;
import org.apache.flink.table.type.InternalTypes;
import org.apache.flink.table.typeutils.BaseRowSerializer;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test of {@link BinaryRow} and {@link BinaryRowWriter}.
 */
public class BinaryRowTest {
    @Test
    public void testBasic() {
        // consider header 1 byte.
        Assert.assertEquals(8, new BinaryRow(0).getFixedLengthPartSize());
        Assert.assertEquals(16, new BinaryRow(1).getFixedLengthPartSize());
        Assert.assertEquals(536, new BinaryRow(65).getFixedLengthPartSize());
        Assert.assertEquals(1048, new BinaryRow(128).getFixedLengthPartSize());
        MemorySegment segment = MemorySegmentFactory.wrap(new byte[100]);
        BinaryRow row = new BinaryRow(2);
        row.pointTo(segment, 10, 48);
        Assert.assertTrue(((row.getSegments()[0]) == segment));
        row.setInt(0, 5);
        row.setDouble(1, 5.8);
    }

    @Test
    public void testSetAndGet() {
        MemorySegment segment = MemorySegmentFactory.wrap(new byte[80]);
        BinaryRow row = new BinaryRow(9);
        row.pointTo(segment, 0, 80);
        row.setNullAt(0);
        row.setInt(1, 11);
        row.setLong(2, 22);
        row.setDouble(3, 33);
        row.setBoolean(4, true);
        row.setShort(5, ((short) (55)));
        row.setByte(6, ((byte) (66)));
        row.setFloat(7, 77.0F);
        row.setChar(8, 'a');
        Assert.assertEquals(33.0, ((long) (row.getDouble(3))), 0);
        Assert.assertEquals(11, row.getInt(1));
        Assert.assertTrue(row.isNullAt(0));
        Assert.assertEquals(55, row.getShort(5));
        Assert.assertEquals(22, row.getLong(2));
        Assert.assertEquals(true, row.getBoolean(4));
        Assert.assertEquals(((byte) (66)), row.getByte(6));
        Assert.assertEquals(77.0F, row.getFloat(7), 0);
        Assert.assertEquals('a', row.getChar(8));
    }

    @Test
    public void testWriter() {
        int arity = 13;
        BinaryRow row = new BinaryRow(arity);
        BinaryRowWriter writer = new BinaryRowWriter(row, 20);
        writer.writeString(0, BinaryString.fromString("1"));
        writer.writeString(3, BinaryString.fromString("1234567"));
        writer.writeString(5, BinaryString.fromString("12345678"));
        writer.writeString(9, BinaryString.fromString("?????????????"));
        writer.writeBoolean(1, true);
        writer.writeByte(2, ((byte) (99)));
        writer.writeChar(4, 'x');
        writer.writeDouble(6, 87.1);
        writer.writeFloat(7, 26.1F);
        writer.writeInt(8, 88);
        writer.writeLong(10, 284);
        writer.writeShort(11, ((short) (292)));
        writer.setNullAt(12);
        writer.complete();
        assertTestWriterRow(row);
        assertTestWriterRow(row.copy());
        // test copy from var segments.
        int subSize = (row.getFixedLengthPartSize()) + 10;
        MemorySegment subMs1 = MemorySegmentFactory.wrap(new byte[subSize]);
        MemorySegment subMs2 = MemorySegmentFactory.wrap(new byte[subSize]);
        row.getSegments()[0].copyTo(0, subMs1, 0, subSize);
        row.getSegments()[0].copyTo(subSize, subMs2, 0, ((row.getSizeInBytes()) - subSize));
        BinaryRow toCopy = new BinaryRow(arity);
        toCopy.pointTo(new MemorySegment[]{ subMs1, subMs2 }, 0, row.getSizeInBytes());
        Assert.assertEquals(row, toCopy);
        assertTestWriterRow(toCopy);
        assertTestWriterRow(toCopy.copy(new BinaryRow(arity)));
    }

    @Test
    public void testWriteString() throws IOException {
        {
            // litter byte[]
            BinaryRow row = new BinaryRow(1);
            BinaryRowWriter writer = new BinaryRowWriter(row);
            char[] chars = new char[2];
            chars[0] = 65535;
            chars[1] = 0;
            writer.writeString(0, BinaryString.fromString(new String(chars)));
            writer.complete();
            String str = row.getString(0).toString();
            Assert.assertEquals(chars[0], str.charAt(0));
            Assert.assertEquals(chars[1], str.charAt(1));
        }
        {
            // big byte[]
            String str = "?????????????";
            BinaryRow row = new BinaryRow(2);
            BinaryRowWriter writer = new BinaryRowWriter(row);
            writer.writeString(0, BinaryString.fromString(str));
            writer.writeString(1, BinaryString.fromBytes(str.getBytes()));
            writer.complete();
            Assert.assertEquals(str, row.getString(0).toString());
            Assert.assertEquals(str, row.getString(1).toString());
        }
    }

    @Test
    public void testReuseWriter() {
        BinaryRow row = new BinaryRow(2);
        BinaryRowWriter writer = new BinaryRowWriter(row);
        writer.writeString(0, BinaryString.fromString("01234567"));
        writer.writeString(1, BinaryString.fromString("012345678"));
        writer.complete();
        Assert.assertEquals("01234567", row.getString(0).toString());
        Assert.assertEquals("012345678", row.getString(1).toString());
        writer.reset();
        writer.writeString(0, BinaryString.fromString("1"));
        writer.writeString(1, BinaryString.fromString("0123456789"));
        writer.complete();
        Assert.assertEquals("1", row.getString(0).toString());
        Assert.assertEquals("0123456789", row.getString(1).toString());
    }

    @Test
    public void anyNullTest() throws IOException {
        {
            BinaryRow row = new BinaryRow(3);
            BinaryRowWriter writer = new BinaryRowWriter(row);
            Assert.assertFalse(row.anyNull());
            // test header should not compute by anyNull
            row.setHeader(((byte) (1)));
            Assert.assertFalse(row.anyNull());
            writer.setNullAt(2);
            Assert.assertTrue(row.anyNull());
            writer.setNullAt(0);
            Assert.assertTrue(row.anyNull(new int[]{ 0, 1, 2 }));
            Assert.assertFalse(row.anyNull(new int[]{ 1 }));
            writer.setNullAt(1);
            Assert.assertTrue(row.anyNull());
        }
        {
            BinaryRow row = new BinaryRow(80);
            BinaryRowWriter writer = new BinaryRowWriter(row);
            Assert.assertFalse(row.anyNull());
            writer.setNullAt(3);
            Assert.assertTrue(row.anyNull());
            writer = new BinaryRowWriter(row);
            writer.setNullAt(65);
            Assert.assertTrue(row.anyNull());
        }
    }

    @Test
    public void testSingleSegmentBinaryRowHashCode() throws IOException {
        final Random rnd = new Random(System.currentTimeMillis());
        // test hash stabilization
        BinaryRow row = new BinaryRow(13);
        BinaryRowWriter writer = new BinaryRowWriter(row);
        for (int i = 0; i < 99; i++) {
            writer.reset();
            writer.writeString(0, BinaryString.fromString(("" + (rnd.nextInt()))));
            writer.writeString(3, BinaryString.fromString("01234567"));
            writer.writeString(5, BinaryString.fromString("012345678"));
            writer.writeString(9, BinaryString.fromString("?????????????"));
            writer.writeBoolean(1, true);
            writer.writeByte(2, ((byte) (99)));
            writer.writeChar(4, 'x');
            writer.writeDouble(6, 87.1);
            writer.writeFloat(7, 26.1F);
            writer.writeInt(8, 88);
            writer.writeLong(10, 284);
            writer.writeShort(11, ((short) (292)));
            writer.setNullAt(12);
            writer.complete();
            BinaryRow copy = row.copy();
            Assert.assertEquals(row.hashCode(), copy.hashCode());
        }
        // test hash distribution
        int count = 999999;
        Set<Integer> hashCodes = new HashSet<>(count);
        for (int i = 0; i < count; i++) {
            row.setInt(8, i);
            hashCodes.add(row.hashCode());
        }
        Assert.assertEquals(count, hashCodes.size());
        hashCodes.clear();
        row = new BinaryRow(1);
        writer = new BinaryRowWriter(row);
        for (int i = 0; i < count; i++) {
            writer.reset();
            writer.writeString(0, BinaryString.fromString(("?????????????" + i)));
            writer.complete();
            hashCodes.add(row.hashCode());
        }
        Assert.assertTrue(((hashCodes.size()) > (count * 0.997)));
    }

    @Test
    public void testHeaderSize() throws IOException {
        Assert.assertEquals(8, BinaryRow.calculateBitSetWidthInBytes(56));
        Assert.assertEquals(16, BinaryRow.calculateBitSetWidthInBytes(57));
        Assert.assertEquals(16, BinaryRow.calculateBitSetWidthInBytes(120));
        Assert.assertEquals(24, BinaryRow.calculateBitSetWidthInBytes(121));
    }

    @Test
    public void testHeader() throws IOException {
        BinaryRow row = new BinaryRow(2);
        BinaryRowWriter writer = new BinaryRowWriter(row);
        writer.writeInt(0, 10);
        writer.setNullAt(1);
        writer.writeHeader(((byte) (29)));
        writer.complete();
        BinaryRow newRow = row.copy();
        Assert.assertEquals(row, newRow);
        Assert.assertEquals(((byte) (29)), newRow.getHeader());
        newRow.setHeader(((byte) (19)));
        Assert.assertEquals(((byte) (19)), newRow.getHeader());
    }

    @Test
    public void testDecimal() {
        // 1.compact
        {
            int precision = 4;
            int scale = 2;
            BinaryRow row = new BinaryRow(2);
            BinaryRowWriter writer = new BinaryRowWriter(row);
            writer.writeDecimal(0, Decimal.fromLong(5, precision, scale), precision);
            writer.setNullAt(1);
            writer.complete();
            Assert.assertEquals("0.05", row.getDecimal(0, precision, scale).toString());
            Assert.assertTrue(row.isNullAt(1));
            row.setDecimal(0, Decimal.fromLong(6, precision, scale), precision);
            Assert.assertEquals("0.06", row.getDecimal(0, precision, scale).toString());
        }
        // 2.not compact
        {
            int precision = 25;
            int scale = 5;
            Decimal decimal1 = Decimal.fromBigDecimal(BigDecimal.valueOf(5.55), precision, scale);
            Decimal decimal2 = Decimal.fromBigDecimal(BigDecimal.valueOf(6.55), precision, scale);
            BinaryRow row = new BinaryRow(2);
            BinaryRowWriter writer = new BinaryRowWriter(row);
            writer.writeDecimal(0, decimal1, precision);
            writer.writeDecimal(1, null, precision);
            writer.complete();
            Assert.assertEquals("5.55000", row.getDecimal(0, precision, scale).toString());
            Assert.assertTrue(row.isNullAt(1));
            row.setDecimal(0, decimal2, precision);
            Assert.assertEquals("6.55000", row.getDecimal(0, precision, scale).toString());
        }
    }

    @Test
    public void testGeneric() {
        BinaryRow row = new BinaryRow(3);
        BinaryRowWriter writer = new BinaryRowWriter(row);
        BinaryGeneric<String> hahah = new BinaryGeneric("hahah", StringSerializer.INSTANCE);
        writer.writeGeneric(0, hahah);
        writer.setNullAt(1);
        hahah.ensureMaterialized();
        writer.writeGeneric(2, hahah);
        writer.complete();
        BinaryGeneric<String> generic0 = row.getGeneric(0);
        Assert.assertEquals(hahah, generic0);
        Assert.assertTrue(row.isNullAt(1));
        BinaryGeneric<String> generic2 = row.getGeneric(2);
        Assert.assertEquals(hahah, generic2);
    }

    @Test
    public void testNested() {
        BinaryRow row = new BinaryRow(2);
        BinaryRowWriter writer = new BinaryRowWriter(row);
        BaseRowSerializer nestedSer = new BaseRowSerializer(new ExecutionConfig(), InternalTypes.STRING, InternalTypes.INT);
        writer.writeRow(0, GenericRow.of(BinaryString.fromString("1"), 1), nestedSer);
        writer.setNullAt(1);
        writer.complete();
        BaseRow nestedRow = row.getRow(0, 2);
        Assert.assertEquals("1", nestedRow.getString(0).toString());
        Assert.assertEquals(1, nestedRow.getInt(1));
        Assert.assertTrue(row.isNullAt(1));
    }
}

