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


import KeyValue.Type;
import java.io.IOException;
import java.nio.ByteBuffer;
import org.apache.hadoop.hbase.io.ByteArrayOutputStream;
import org.apache.hadoop.hbase.testclassification.MiscTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static HConstants.EMPTY_BYTE_ARRAY;


@Category({ MiscTests.class, SmallTests.class })
public class TestIndividualBytesFieldCell {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestIndividualBytesFieldCell.class);

    private static IndividualBytesFieldCell ic0 = null;

    private static KeyValue kv0 = null;

    // Verify clone() and deepClone()
    @Test
    public void testClone() throws CloneNotSupportedException {
        // Verify clone. Only shadow copies are made for backing byte arrays.
        IndividualBytesFieldCell cloned = ((IndividualBytesFieldCell) (TestIndividualBytesFieldCell.ic0.clone()));
        Assert.assertTrue(((cloned.getRowArray()) == (TestIndividualBytesFieldCell.ic0.getRowArray())));
        Assert.assertTrue(((cloned.getFamilyArray()) == (TestIndividualBytesFieldCell.ic0.getFamilyArray())));
        Assert.assertTrue(((cloned.getQualifierArray()) == (TestIndividualBytesFieldCell.ic0.getQualifierArray())));
        Assert.assertTrue(((cloned.getValueArray()) == (TestIndividualBytesFieldCell.ic0.getValueArray())));
        Assert.assertTrue(((cloned.getTagsArray()) == (TestIndividualBytesFieldCell.ic0.getTagsArray())));
        // Verify if deep clone returns a KeyValue object
        Assert.assertTrue(((TestIndividualBytesFieldCell.ic0.deepClone()) instanceof KeyValue));
    }

    /**
     * Verify KeyValue format related functions: write() and getSerializedSize().
     * Should have the same behaviors as {@link KeyValue}.
     */
    @Test
    public void testWriteIntoKeyValueFormat() throws IOException {
        // Verify getSerializedSize().
        Assert.assertEquals(TestIndividualBytesFieldCell.kv0.getSerializedSize(true), TestIndividualBytesFieldCell.ic0.getSerializedSize(true));// with tags

        Assert.assertEquals(TestIndividualBytesFieldCell.kv0.getSerializedSize(false), TestIndividualBytesFieldCell.ic0.getSerializedSize(false));// without tags

        // Verify writing into ByteBuffer.
        ByteBuffer bbufIC = ByteBuffer.allocate(TestIndividualBytesFieldCell.ic0.getSerializedSize(true));
        TestIndividualBytesFieldCell.ic0.write(bbufIC, 0);
        ByteBuffer bbufKV = ByteBuffer.allocate(TestIndividualBytesFieldCell.kv0.getSerializedSize(true));
        TestIndividualBytesFieldCell.kv0.write(bbufKV, 0);
        Assert.assertTrue(bbufIC.equals(bbufKV));
        // Verify writing into OutputStream.
        testWriteIntoOutputStream(TestIndividualBytesFieldCell.ic0, TestIndividualBytesFieldCell.kv0, true);// with tags

        testWriteIntoOutputStream(TestIndividualBytesFieldCell.ic0, TestIndividualBytesFieldCell.kv0, false);// without tags

    }

    /**
     * Verify getXXXArray() and getXXXLength() when family/qualifier/value/tags are null.
     * Should have the same behaviors as {@link KeyValue}.
     */
    @Test
    public void testNullFamilyQualifierValueTags() {
        byte[] row = Bytes.toBytes("row1");
        long timestamp = 5000L;
        long seqId = 0L;
        KeyValue.Type type = Type.Put;
        // Test when following fields are null.
        byte[] family = null;
        byte[] qualifier = null;
        byte[] value = null;
        byte[] tags = null;
        Cell ic1 = new IndividualBytesFieldCell(row, family, qualifier, timestamp, type, seqId, value, tags);
        Cell kv1 = new KeyValue(row, family, qualifier, timestamp, type, value, tags);
        byte[] familyArrayInKV = Bytes.copy(kv1.getFamilyArray(), kv1.getFamilyOffset(), kv1.getFamilyLength());
        byte[] qualifierArrayInKV = Bytes.copy(kv1.getQualifierArray(), kv1.getQualifierOffset(), kv1.getQualifierLength());
        byte[] valueArrayInKV = Bytes.copy(kv1.getValueArray(), kv1.getValueOffset(), kv1.getValueLength());
        byte[] tagsArrayInKV = Bytes.copy(kv1.getTagsArray(), kv1.getTagsOffset(), kv1.getTagsLength());
        // getXXXArray() for family, qualifier, value and tags are supposed to return empty byte array,
        // rather than null.
        Assert.assertArrayEquals(familyArrayInKV, ic1.getFamilyArray());
        Assert.assertArrayEquals(qualifierArrayInKV, ic1.getQualifierArray());
        Assert.assertArrayEquals(valueArrayInKV, ic1.getValueArray());
        Assert.assertArrayEquals(tagsArrayInKV, ic1.getTagsArray());
        // getXXXLength() for family, qualifier, value and tags are supposed to return 0.
        Assert.assertEquals(kv1.getFamilyLength(), ic1.getFamilyLength());
        Assert.assertEquals(kv1.getQualifierLength(), ic1.getQualifierLength());
        Assert.assertEquals(kv1.getValueLength(), ic1.getValueLength());
        Assert.assertEquals(kv1.getTagsLength(), ic1.getTagsLength());
    }

    // Verify if ExtendedCell interface is implemented
    @Test
    public void testIfExtendedCellImplemented() {
        Assert.assertTrue(((TestIndividualBytesFieldCell.ic0) instanceof ExtendedCell));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalRow() {
        new IndividualBytesFieldCell(Bytes.toBytes("row"), 0, 100, EMPTY_BYTE_ARRAY, 0, 0, EMPTY_BYTE_ARRAY, 0, 0, 0L, Type.Put, 0, EMPTY_BYTE_ARRAY, 0, 0, EMPTY_BYTE_ARRAY, 0, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalFamily() {
        new IndividualBytesFieldCell(Bytes.toBytes("row"), 0, 3, Bytes.toBytes("family"), 0, 100, EMPTY_BYTE_ARRAY, 0, 0, 0L, Type.Put, 0, EMPTY_BYTE_ARRAY, 0, 0, EMPTY_BYTE_ARRAY, 0, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalQualifier() {
        new IndividualBytesFieldCell(Bytes.toBytes("row"), 0, 3, Bytes.toBytes("family"), 0, 6, Bytes.toBytes("qualifier"), 0, 100, 0L, Type.Put, 0, EMPTY_BYTE_ARRAY, 0, 0, EMPTY_BYTE_ARRAY, 0, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalTimestamp() {
        new IndividualBytesFieldCell(Bytes.toBytes("row"), 0, 3, Bytes.toBytes("family"), 0, 6, Bytes.toBytes("qualifier"), 0, 9, (-100), Type.Put, 0, EMPTY_BYTE_ARRAY, 0, 0, EMPTY_BYTE_ARRAY, 0, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalValue() {
        new IndividualBytesFieldCell(Bytes.toBytes("row"), 0, 3, Bytes.toBytes("family"), 0, 6, Bytes.toBytes("qualifier"), 0, 9, 0L, Type.Put, 0, Bytes.toBytes("value"), 0, 100, EMPTY_BYTE_ARRAY, 0, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalTags() {
        new IndividualBytesFieldCell(Bytes.toBytes("row"), 0, 3, Bytes.toBytes("family"), 0, 6, Bytes.toBytes("qualifier"), 0, 9, 0L, Type.Put, 0, Bytes.toBytes("value"), 0, 5, Bytes.toBytes("tags"), 0, 100);
    }

    @Test
    public void testWriteTag() throws IOException {
        byte[] tags = Bytes.toBytes("---tags---");
        int tagOffset = 3;
        int length = 4;
        IndividualBytesFieldCell cell = new IndividualBytesFieldCell(Bytes.toBytes("row"), 0, 3, Bytes.toBytes("family"), 0, 6, Bytes.toBytes("qualifier"), 0, 9, 0L, Type.Put, 0, Bytes.toBytes("value"), 0, 5, tags, tagOffset, length);
        try (ByteArrayOutputStream output = new ByteArrayOutputStream(300)) {
            cell.write(output, true);
            byte[] buf = output.toByteArray();
            Assert.assertEquals(cell.getSerializedSize(true), buf.length);
        }
    }

    @Test
    public void testWriteValue() throws IOException {
        byte[] value = Bytes.toBytes("---value---");
        int valueOffset = 3;
        int valueLength = 5;
        IndividualBytesFieldCell cell = new IndividualBytesFieldCell(Bytes.toBytes("row"), 0, 3, Bytes.toBytes("family"), 0, 6, Bytes.toBytes("qualifier"), 0, 9, 0L, Type.Put, 0, value, valueOffset, valueLength, Bytes.toBytes("value"), 0, 5);
        try (ByteArrayOutputStream output = new ByteArrayOutputStream(300)) {
            cell.write(output, true);
            byte[] buf = output.toByteArray();
            Assert.assertEquals(cell.getSerializedSize(true), buf.length);
        }
    }
}

