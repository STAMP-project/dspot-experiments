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
import Type.Put;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListMap;
import junit.framework.TestCase;
import org.apache.hadoop.hbase.KeyValue.Type;
import org.apache.hadoop.hbase.testclassification.MiscTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hadoop.hbase.util.ByteBufferUtils;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ MiscTests.class, SmallTests.class })
public class TestByteBufferKeyValue {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestByteBufferKeyValue.class);

    private static final String QUAL2 = "qual2";

    private static final String FAM2 = "fam2";

    private static final String QUAL1 = "qual1";

    private static final String FAM1 = "fam1";

    private static final String ROW1 = "row1";

    private static final byte[] row1 = Bytes.toBytes(TestByteBufferKeyValue.ROW1);

    private static final byte[] fam1 = Bytes.toBytes(TestByteBufferKeyValue.FAM1);

    private static final byte[] fam2 = Bytes.toBytes(TestByteBufferKeyValue.FAM2);

    private static final byte[] qual1 = Bytes.toBytes(TestByteBufferKeyValue.QUAL1);

    private static final byte[] qual2 = Bytes.toBytes(TestByteBufferKeyValue.QUAL2);

    private static final Tag t1 = new ArrayBackedTag(((byte) (1)), Bytes.toBytes("TAG1"));

    private static final Tag t2 = new ArrayBackedTag(((byte) (2)), Bytes.toBytes("TAG2"));

    private static final ArrayList<Tag> tags = new ArrayList<Tag>();

    static {
        TestByteBufferKeyValue.tags.add(TestByteBufferKeyValue.t1);
        TestByteBufferKeyValue.tags.add(TestByteBufferKeyValue.t2);
    }

    @Test
    public void testCompare() {
        Cell cell1 = TestByteBufferKeyValue.getOffheapCell(TestByteBufferKeyValue.row1, TestByteBufferKeyValue.fam1, TestByteBufferKeyValue.qual1);
        Cell cell2 = TestByteBufferKeyValue.getOffheapCell(TestByteBufferKeyValue.row1, TestByteBufferKeyValue.fam1, TestByteBufferKeyValue.qual2);
        TestCase.assertTrue(((COMPARATOR.compare(cell1, cell2)) < 0));
        Cell cell3 = TestByteBufferKeyValue.getOffheapCell(TestByteBufferKeyValue.row1, Bytes.toBytes("wide_family"), TestByteBufferKeyValue.qual2);
        TestCase.assertTrue(((COMPARATOR.compare(cell1, cell3)) < 0));
        Cell cell4 = TestByteBufferKeyValue.getOffheapCell(TestByteBufferKeyValue.row1, Bytes.toBytes("f"), TestByteBufferKeyValue.qual2);
        TestCase.assertTrue(((COMPARATOR.compare(cell1, cell4)) > 0));
        BBKVComparator comparator = new BBKVComparator(null);
        TestCase.assertTrue(((comparator.compare(cell1, cell2)) < 0));
        TestCase.assertTrue(((comparator.compare(cell1, cell3)) < 0));
        TestCase.assertTrue(((comparator.compare(cell1, cell4)) > 0));
        ByteBuffer buf = ByteBuffer.allocate(TestByteBufferKeyValue.row1.length);
        ByteBufferUtils.copyFromArrayToBuffer(buf, TestByteBufferKeyValue.row1, 0, TestByteBufferKeyValue.row1.length);
        ConcurrentSkipListMap<ByteBufferKeyValue, ByteBufferKeyValue> map = new ConcurrentSkipListMap(comparator);
        map.put(((ByteBufferKeyValue) (cell1)), ((ByteBufferKeyValue) (cell1)));
        map.put(((ByteBufferKeyValue) (cell2)), ((ByteBufferKeyValue) (cell2)));
        map.put(((ByteBufferKeyValue) (cell3)), ((ByteBufferKeyValue) (cell3)));
        map.put(((ByteBufferKeyValue) (cell1)), ((ByteBufferKeyValue) (cell1)));
        map.put(((ByteBufferKeyValue) (cell1)), ((ByteBufferKeyValue) (cell1)));
    }

    @Test
    public void testByteBufferBackedKeyValue() throws Exception {
        KeyValue kvCell = new KeyValue(TestByteBufferKeyValue.row1, TestByteBufferKeyValue.fam1, TestByteBufferKeyValue.qual1, 0L, Type.Put, TestByteBufferKeyValue.row1);
        ByteBuffer buf = ByteBuffer.allocateDirect(kvCell.getBuffer().length);
        ByteBufferUtils.copyFromArrayToBuffer(buf, kvCell.getBuffer(), 0, kvCell.getBuffer().length);
        ByteBufferExtendedCell offheapKV = new ByteBufferKeyValue(buf, 0, buf.capacity(), 0L);
        Assert.assertEquals(TestByteBufferKeyValue.ROW1, ByteBufferUtils.toStringBinary(offheapKV.getRowByteBuffer(), offheapKV.getRowPosition(), offheapKV.getRowLength()));
        Assert.assertEquals(TestByteBufferKeyValue.FAM1, ByteBufferUtils.toStringBinary(offheapKV.getFamilyByteBuffer(), offheapKV.getFamilyPosition(), offheapKV.getFamilyLength()));
        Assert.assertEquals(TestByteBufferKeyValue.QUAL1, ByteBufferUtils.toStringBinary(offheapKV.getQualifierByteBuffer(), offheapKV.getQualifierPosition(), offheapKV.getQualifierLength()));
        Assert.assertEquals(TestByteBufferKeyValue.ROW1, ByteBufferUtils.toStringBinary(offheapKV.getValueByteBuffer(), offheapKV.getValuePosition(), offheapKV.getValueLength()));
        Assert.assertEquals(0L, offheapKV.getTimestamp());
        Assert.assertEquals(Put.getCode(), offheapKV.getTypeByte());
        // Use the array() APIs
        Assert.assertEquals(TestByteBufferKeyValue.ROW1, Bytes.toStringBinary(offheapKV.getRowArray(), offheapKV.getRowOffset(), offheapKV.getRowLength()));
        Assert.assertEquals(TestByteBufferKeyValue.FAM1, Bytes.toStringBinary(offheapKV.getFamilyArray(), offheapKV.getFamilyOffset(), offheapKV.getFamilyLength()));
        Assert.assertEquals(TestByteBufferKeyValue.QUAL1, Bytes.toStringBinary(offheapKV.getQualifierArray(), offheapKV.getQualifierOffset(), offheapKV.getQualifierLength()));
        Assert.assertEquals(TestByteBufferKeyValue.ROW1, Bytes.toStringBinary(offheapKV.getValueArray(), offheapKV.getValueOffset(), offheapKV.getValueLength()));
        Assert.assertEquals(0L, offheapKV.getTimestamp());
        Assert.assertEquals(Put.getCode(), offheapKV.getTypeByte());
        kvCell = new KeyValue(TestByteBufferKeyValue.row1, TestByteBufferKeyValue.fam2, TestByteBufferKeyValue.qual2, 0L, Type.Put, TestByteBufferKeyValue.row1);
        buf = ByteBuffer.allocateDirect(kvCell.getBuffer().length);
        ByteBufferUtils.copyFromArrayToBuffer(buf, kvCell.getBuffer(), 0, kvCell.getBuffer().length);
        offheapKV = new ByteBufferKeyValue(buf, 0, buf.capacity(), 0L);
        Assert.assertEquals(TestByteBufferKeyValue.FAM2, ByteBufferUtils.toStringBinary(offheapKV.getFamilyByteBuffer(), offheapKV.getFamilyPosition(), offheapKV.getFamilyLength()));
        Assert.assertEquals(TestByteBufferKeyValue.QUAL2, ByteBufferUtils.toStringBinary(offheapKV.getQualifierByteBuffer(), offheapKV.getQualifierPosition(), offheapKV.getQualifierLength()));
        byte[] nullQualifier = new byte[0];
        kvCell = new KeyValue(TestByteBufferKeyValue.row1, TestByteBufferKeyValue.fam1, nullQualifier, 0L, Type.Put, TestByteBufferKeyValue.row1);
        buf = ByteBuffer.allocateDirect(kvCell.getBuffer().length);
        ByteBufferUtils.copyFromArrayToBuffer(buf, kvCell.getBuffer(), 0, kvCell.getBuffer().length);
        offheapKV = new ByteBufferKeyValue(buf, 0, buf.capacity(), 0L);
        Assert.assertEquals(TestByteBufferKeyValue.ROW1, ByteBufferUtils.toStringBinary(offheapKV.getRowByteBuffer(), offheapKV.getRowPosition(), offheapKV.getRowLength()));
        Assert.assertEquals(TestByteBufferKeyValue.FAM1, ByteBufferUtils.toStringBinary(offheapKV.getFamilyByteBuffer(), offheapKV.getFamilyPosition(), offheapKV.getFamilyLength()));
        Assert.assertEquals("", ByteBufferUtils.toStringBinary(offheapKV.getQualifierByteBuffer(), offheapKV.getQualifierPosition(), offheapKV.getQualifierLength()));
        Assert.assertEquals(TestByteBufferKeyValue.ROW1, ByteBufferUtils.toStringBinary(offheapKV.getValueByteBuffer(), offheapKV.getValuePosition(), offheapKV.getValueLength()));
        Assert.assertEquals(0L, offheapKV.getTimestamp());
        Assert.assertEquals(Put.getCode(), offheapKV.getTypeByte());
    }

    @Test
    public void testByteBufferBackedKeyValueWithTags() throws Exception {
        KeyValue kvCell = new KeyValue(TestByteBufferKeyValue.row1, TestByteBufferKeyValue.fam1, TestByteBufferKeyValue.qual1, 0L, Type.Put, TestByteBufferKeyValue.row1, TestByteBufferKeyValue.tags);
        ByteBuffer buf = ByteBuffer.allocateDirect(kvCell.getBuffer().length);
        ByteBufferUtils.copyFromArrayToBuffer(buf, kvCell.getBuffer(), 0, kvCell.getBuffer().length);
        ByteBufferKeyValue offheapKV = new ByteBufferKeyValue(buf, 0, buf.capacity(), 0L);
        Assert.assertEquals(TestByteBufferKeyValue.ROW1, ByteBufferUtils.toStringBinary(offheapKV.getRowByteBuffer(), offheapKV.getRowPosition(), offheapKV.getRowLength()));
        Assert.assertEquals(TestByteBufferKeyValue.FAM1, ByteBufferUtils.toStringBinary(offheapKV.getFamilyByteBuffer(), offheapKV.getFamilyPosition(), offheapKV.getFamilyLength()));
        Assert.assertEquals(TestByteBufferKeyValue.QUAL1, ByteBufferUtils.toStringBinary(offheapKV.getQualifierByteBuffer(), offheapKV.getQualifierPosition(), offheapKV.getQualifierLength()));
        Assert.assertEquals(TestByteBufferKeyValue.ROW1, ByteBufferUtils.toStringBinary(offheapKV.getValueByteBuffer(), offheapKV.getValuePosition(), offheapKV.getValueLength()));
        Assert.assertEquals(0L, offheapKV.getTimestamp());
        Assert.assertEquals(Put.getCode(), offheapKV.getTypeByte());
        // change tags to handle both onheap and offheap stuff
        List<Tag> resTags = PrivateCellUtil.getTags(offheapKV);
        Tag tag1 = resTags.get(0);
        Assert.assertEquals(TestByteBufferKeyValue.t1.getType(), tag1.getType());
        Assert.assertEquals(Tag.getValueAsString(TestByteBufferKeyValue.t1), Tag.getValueAsString(tag1));
        Tag tag2 = resTags.get(1);
        Assert.assertEquals(tag2.getType(), tag2.getType());
        Assert.assertEquals(Tag.getValueAsString(TestByteBufferKeyValue.t2), Tag.getValueAsString(tag2));
        Tag res = PrivateCellUtil.getTag(offheapKV, ((byte) (2))).get();
        Assert.assertEquals(Tag.getValueAsString(TestByteBufferKeyValue.t2), Tag.getValueAsString(tag2));
        Assert.assertFalse(PrivateCellUtil.getTag(offheapKV, ((byte) (3))).isPresent());
    }

    @Test
    public void testGetKeyMethods() throws Exception {
        KeyValue kvCell = new KeyValue(TestByteBufferKeyValue.row1, TestByteBufferKeyValue.fam1, TestByteBufferKeyValue.qual1, 0L, Type.Put, TestByteBufferKeyValue.row1, TestByteBufferKeyValue.tags);
        ByteBuffer buf = ByteBuffer.allocateDirect(kvCell.getKeyLength());
        ByteBufferUtils.copyFromArrayToBuffer(buf, kvCell.getBuffer(), kvCell.getKeyOffset(), kvCell.getKeyLength());
        ByteBufferExtendedCell offheapKeyOnlyKV = new ByteBufferKeyOnlyKeyValue(buf, 0, buf.capacity());
        Assert.assertEquals(TestByteBufferKeyValue.ROW1, ByteBufferUtils.toStringBinary(offheapKeyOnlyKV.getRowByteBuffer(), offheapKeyOnlyKV.getRowPosition(), offheapKeyOnlyKV.getRowLength()));
        Assert.assertEquals(TestByteBufferKeyValue.FAM1, ByteBufferUtils.toStringBinary(offheapKeyOnlyKV.getFamilyByteBuffer(), offheapKeyOnlyKV.getFamilyPosition(), offheapKeyOnlyKV.getFamilyLength()));
        Assert.assertEquals(TestByteBufferKeyValue.QUAL1, ByteBufferUtils.toStringBinary(offheapKeyOnlyKV.getQualifierByteBuffer(), offheapKeyOnlyKV.getQualifierPosition(), offheapKeyOnlyKV.getQualifierLength()));
        Assert.assertEquals(0L, offheapKeyOnlyKV.getTimestamp());
        Assert.assertEquals(Put.getCode(), offheapKeyOnlyKV.getTypeByte());
    }
}

