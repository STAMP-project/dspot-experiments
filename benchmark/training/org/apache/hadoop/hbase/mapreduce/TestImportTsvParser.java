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


import HConstants.EMPTY_BYTE_ARRAY;
import TsvParser.DEFAULT_TIMESTAMP_COLUMN_INDEX;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.mapreduce.ImportTsv.TsvParser;
import org.apache.hadoop.hbase.mapreduce.ImportTsv.TsvParser.BadTsvLineException;
import org.apache.hadoop.hbase.mapreduce.ImportTsv.TsvParser.ParsedLine;
import org.apache.hadoop.hbase.testclassification.MapReduceTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.Pair;
import org.apache.hbase.thirdparty.com.google.common.base.Splitter;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Tests for {@link TsvParser}.
 */
@Category({ MapReduceTests.class, SmallTests.class })
public class TestImportTsvParser {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestImportTsvParser.class);

    @Test
    public void testTsvParserSpecParsing() {
        TsvParser parser;
        parser = new TsvParser("HBASE_ROW_KEY", "\t");
        Assert.assertNull(parser.getFamily(0));
        Assert.assertNull(parser.getQualifier(0));
        Assert.assertEquals(0, parser.getRowKeyColumnIndex());
        Assert.assertFalse(parser.hasTimestamp());
        parser = new TsvParser("HBASE_ROW_KEY,col1:scol1", "\t");
        Assert.assertNull(parser.getFamily(0));
        Assert.assertNull(parser.getQualifier(0));
        assertBytesEquals(Bytes.toBytes("col1"), parser.getFamily(1));
        assertBytesEquals(Bytes.toBytes("scol1"), parser.getQualifier(1));
        Assert.assertEquals(0, parser.getRowKeyColumnIndex());
        Assert.assertFalse(parser.hasTimestamp());
        parser = new TsvParser("HBASE_ROW_KEY,col1:scol1,col1:scol2", "\t");
        Assert.assertNull(parser.getFamily(0));
        Assert.assertNull(parser.getQualifier(0));
        assertBytesEquals(Bytes.toBytes("col1"), parser.getFamily(1));
        assertBytesEquals(Bytes.toBytes("scol1"), parser.getQualifier(1));
        assertBytesEquals(Bytes.toBytes("col1"), parser.getFamily(2));
        assertBytesEquals(Bytes.toBytes("scol2"), parser.getQualifier(2));
        Assert.assertEquals(0, parser.getRowKeyColumnIndex());
        Assert.assertFalse(parser.hasTimestamp());
        parser = new TsvParser("HBASE_ROW_KEY,col1:scol1,HBASE_TS_KEY,col1:scol2", "\t");
        Assert.assertNull(parser.getFamily(0));
        Assert.assertNull(parser.getQualifier(0));
        assertBytesEquals(Bytes.toBytes("col1"), parser.getFamily(1));
        assertBytesEquals(Bytes.toBytes("scol1"), parser.getQualifier(1));
        assertBytesEquals(Bytes.toBytes("col1"), parser.getFamily(3));
        assertBytesEquals(Bytes.toBytes("scol2"), parser.getQualifier(3));
        Assert.assertEquals(0, parser.getRowKeyColumnIndex());
        Assert.assertTrue(parser.hasTimestamp());
        Assert.assertEquals(2, parser.getTimestampKeyColumnIndex());
        parser = new TsvParser("HBASE_ROW_KEY,col1:scol1,HBASE_TS_KEY,col1:scol2,HBASE_ATTRIBUTES_KEY", "\t");
        Assert.assertNull(parser.getFamily(0));
        Assert.assertNull(parser.getQualifier(0));
        assertBytesEquals(Bytes.toBytes("col1"), parser.getFamily(1));
        assertBytesEquals(Bytes.toBytes("scol1"), parser.getQualifier(1));
        assertBytesEquals(Bytes.toBytes("col1"), parser.getFamily(3));
        assertBytesEquals(Bytes.toBytes("scol2"), parser.getQualifier(3));
        Assert.assertEquals(0, parser.getRowKeyColumnIndex());
        Assert.assertTrue(parser.hasTimestamp());
        Assert.assertEquals(2, parser.getTimestampKeyColumnIndex());
        Assert.assertEquals(4, parser.getAttributesKeyColumnIndex());
        parser = new TsvParser("HBASE_ATTRIBUTES_KEY,col1:scol1,HBASE_TS_KEY,col1:scol2,HBASE_ROW_KEY", "\t");
        Assert.assertNull(parser.getFamily(0));
        Assert.assertNull(parser.getQualifier(0));
        assertBytesEquals(Bytes.toBytes("col1"), parser.getFamily(1));
        assertBytesEquals(Bytes.toBytes("scol1"), parser.getQualifier(1));
        assertBytesEquals(Bytes.toBytes("col1"), parser.getFamily(3));
        assertBytesEquals(Bytes.toBytes("scol2"), parser.getQualifier(3));
        Assert.assertEquals(4, parser.getRowKeyColumnIndex());
        Assert.assertTrue(parser.hasTimestamp());
        Assert.assertEquals(2, parser.getTimestampKeyColumnIndex());
        Assert.assertEquals(0, parser.getAttributesKeyColumnIndex());
    }

    @Test
    public void testTsvParser() throws BadTsvLineException {
        TsvParser parser = new TsvParser("col_a,col_b:qual,HBASE_ROW_KEY,col_d", "\t");
        assertBytesEquals(Bytes.toBytes("col_a"), parser.getFamily(0));
        assertBytesEquals(EMPTY_BYTE_ARRAY, parser.getQualifier(0));
        assertBytesEquals(Bytes.toBytes("col_b"), parser.getFamily(1));
        assertBytesEquals(Bytes.toBytes("qual"), parser.getQualifier(1));
        Assert.assertNull(parser.getFamily(2));
        Assert.assertNull(parser.getQualifier(2));
        Assert.assertEquals(2, parser.getRowKeyColumnIndex());
        Assert.assertEquals(DEFAULT_TIMESTAMP_COLUMN_INDEX, parser.getTimestampKeyColumnIndex());
        byte[] line = Bytes.toBytes("val_a\tval_b\tval_c\tval_d");
        ParsedLine parsed = parser.parse(line, line.length);
        checkParsing(parsed, Splitter.on("\t").split(Bytes.toString(line)));
    }

    @Test
    public void testTsvParserWithTimestamp() throws BadTsvLineException {
        TsvParser parser = new TsvParser("HBASE_ROW_KEY,HBASE_TS_KEY,col_a,", "\t");
        Assert.assertNull(parser.getFamily(0));
        Assert.assertNull(parser.getQualifier(0));
        Assert.assertNull(parser.getFamily(1));
        Assert.assertNull(parser.getQualifier(1));
        assertBytesEquals(Bytes.toBytes("col_a"), parser.getFamily(2));
        assertBytesEquals(EMPTY_BYTE_ARRAY, parser.getQualifier(2));
        Assert.assertEquals(0, parser.getRowKeyColumnIndex());
        Assert.assertEquals(1, parser.getTimestampKeyColumnIndex());
        byte[] line = Bytes.toBytes("rowkey\t1234\tval_a");
        ParsedLine parsed = parser.parse(line, line.length);
        Assert.assertEquals(1234L, parsed.getTimestamp((-1)));
        checkParsing(parsed, Splitter.on("\t").split(Bytes.toString(line)));
    }

    /**
     * Test cases that throw BadTsvLineException
     */
    @Test(expected = BadTsvLineException.class)
    public void testTsvParserBadTsvLineExcessiveColumns() throws BadTsvLineException {
        TsvParser parser = new TsvParser("HBASE_ROW_KEY,col_a", "\t");
        byte[] line = Bytes.toBytes("val_a\tval_b\tval_c");
        parser.parse(line, line.length);
    }

    @Test(expected = BadTsvLineException.class)
    public void testTsvParserBadTsvLineZeroColumn() throws BadTsvLineException {
        TsvParser parser = new TsvParser("HBASE_ROW_KEY,col_a", "\t");
        byte[] line = Bytes.toBytes("");
        parser.parse(line, line.length);
    }

    @Test(expected = BadTsvLineException.class)
    public void testTsvParserBadTsvLineOnlyKey() throws BadTsvLineException {
        TsvParser parser = new TsvParser("HBASE_ROW_KEY,col_a", "\t");
        byte[] line = Bytes.toBytes("key_only");
        parser.parse(line, line.length);
    }

    @Test(expected = BadTsvLineException.class)
    public void testTsvParserBadTsvLineNoRowKey() throws BadTsvLineException {
        TsvParser parser = new TsvParser("col_a,HBASE_ROW_KEY", "\t");
        byte[] line = Bytes.toBytes("only_cola_data_and_no_row_key");
        parser.parse(line, line.length);
    }

    @Test(expected = BadTsvLineException.class)
    public void testTsvParserInvalidTimestamp() throws BadTsvLineException {
        TsvParser parser = new TsvParser("HBASE_ROW_KEY,HBASE_TS_KEY,col_a,", "\t");
        Assert.assertEquals(1, parser.getTimestampKeyColumnIndex());
        byte[] line = Bytes.toBytes("rowkey\ttimestamp\tval_a");
        ParsedLine parsed = parser.parse(line, line.length);
        Assert.assertEquals((-1), parsed.getTimestamp((-1)));
        checkParsing(parsed, Splitter.on("\t").split(Bytes.toString(line)));
    }

    @Test(expected = BadTsvLineException.class)
    public void testTsvParserNoTimestampValue() throws BadTsvLineException {
        TsvParser parser = new TsvParser("HBASE_ROW_KEY,col_a,HBASE_TS_KEY", "\t");
        Assert.assertEquals(2, parser.getTimestampKeyColumnIndex());
        byte[] line = Bytes.toBytes("rowkey\tval_a");
        parser.parse(line, line.length);
    }

    @Test
    public void testTsvParserParseRowKey() throws BadTsvLineException {
        TsvParser parser = new TsvParser("HBASE_ROW_KEY,col_a,HBASE_TS_KEY", "\t");
        Assert.assertEquals(0, parser.getRowKeyColumnIndex());
        byte[] line = Bytes.toBytes("rowkey\tval_a\t1234");
        Pair<Integer, Integer> rowKeyOffsets = parser.parseRowKey(line, line.length);
        Assert.assertEquals(0, rowKeyOffsets.getFirst().intValue());
        Assert.assertEquals(6, rowKeyOffsets.getSecond().intValue());
        try {
            line = Bytes.toBytes("\t\tval_a\t1234");
            parser.parseRowKey(line, line.length);
            Assert.fail("Should get BadTsvLineException on empty rowkey.");
        } catch (BadTsvLineException b) {
        }
        parser = new TsvParser("col_a,HBASE_ROW_KEY,HBASE_TS_KEY", "\t");
        Assert.assertEquals(1, parser.getRowKeyColumnIndex());
        line = Bytes.toBytes("val_a\trowkey\t1234");
        rowKeyOffsets = parser.parseRowKey(line, line.length);
        Assert.assertEquals(6, rowKeyOffsets.getFirst().intValue());
        Assert.assertEquals(6, rowKeyOffsets.getSecond().intValue());
        try {
            line = Bytes.toBytes("val_a");
            rowKeyOffsets = parser.parseRowKey(line, line.length);
            Assert.fail("Should get BadTsvLineException when number of columns less than rowkey position.");
        } catch (BadTsvLineException b) {
        }
        parser = new TsvParser("col_a,HBASE_TS_KEY,HBASE_ROW_KEY", "\t");
        Assert.assertEquals(2, parser.getRowKeyColumnIndex());
        line = Bytes.toBytes("val_a\t1234\trowkey");
        rowKeyOffsets = parser.parseRowKey(line, line.length);
        Assert.assertEquals(11, rowKeyOffsets.getFirst().intValue());
        Assert.assertEquals(6, rowKeyOffsets.getSecond().intValue());
    }

    @Test
    public void testTsvParseAttributesKey() throws BadTsvLineException {
        TsvParser parser = new TsvParser("HBASE_ROW_KEY,col_a,HBASE_TS_KEY,HBASE_ATTRIBUTES_KEY", "\t");
        Assert.assertEquals(0, parser.getRowKeyColumnIndex());
        byte[] line = Bytes.toBytes("rowkey\tval_a\t1234\tkey=>value");
        ParsedLine parse = parser.parse(line, line.length);
        Assert.assertEquals(18, parse.getAttributeKeyOffset());
        Assert.assertEquals(3, parser.getAttributesKeyColumnIndex());
        String[] attributes = parse.getIndividualAttributes();
        Assert.assertEquals(attributes[0], "key=>value");
        try {
            line = Bytes.toBytes("rowkey\tval_a\t1234");
            parser.parse(line, line.length);
            Assert.fail("Should get BadTsvLineException on empty rowkey.");
        } catch (BadTsvLineException b) {
        }
        parser = new TsvParser("HBASE_ATTRIBUTES_KEY,col_a,HBASE_ROW_KEY,HBASE_TS_KEY", "\t");
        Assert.assertEquals(2, parser.getRowKeyColumnIndex());
        line = Bytes.toBytes("key=>value\tval_a\trowkey\t1234");
        parse = parser.parse(line, line.length);
        Assert.assertEquals(0, parse.getAttributeKeyOffset());
        Assert.assertEquals(0, parser.getAttributesKeyColumnIndex());
        attributes = parse.getIndividualAttributes();
        Assert.assertEquals(attributes[0], "key=>value");
        try {
            line = Bytes.toBytes("val_a");
            ParsedLine parse2 = parser.parse(line, line.length);
            Assert.fail("Should get BadTsvLineException when number of columns less than rowkey position.");
        } catch (BadTsvLineException b) {
        }
        parser = new TsvParser("col_a,HBASE_ATTRIBUTES_KEY,HBASE_TS_KEY,HBASE_ROW_KEY", "\t");
        Assert.assertEquals(3, parser.getRowKeyColumnIndex());
        line = Bytes.toBytes("val_a\tkey0=>value0,key1=>value1,key2=>value2\t1234\trowkey");
        parse = parser.parse(line, line.length);
        Assert.assertEquals(1, parser.getAttributesKeyColumnIndex());
        Assert.assertEquals(6, parse.getAttributeKeyOffset());
        String[] attr = parse.getIndividualAttributes();
        int i = 0;
        for (String str : attr) {
            Assert.assertEquals((((("key" + i) + "=>") + "value") + i), str);
            i++;
        }
    }

    @Test
    public void testTsvParserWithCellVisibilityCol() throws BadTsvLineException {
        TsvParser parser = new TsvParser("HBASE_ROW_KEY,col_a,HBASE_TS_KEY,HBASE_ATTRIBUTES_KEY,HBASE_CELL_VISIBILITY", "\t");
        Assert.assertEquals(0, parser.getRowKeyColumnIndex());
        Assert.assertEquals(4, parser.getCellVisibilityColumnIndex());
        byte[] line = Bytes.toBytes("rowkey\tval_a\t1234\tkey=>value\tPRIVATE&SECRET");
        ParsedLine parse = parser.parse(line, line.length);
        Assert.assertEquals(18, parse.getAttributeKeyOffset());
        Assert.assertEquals(3, parser.getAttributesKeyColumnIndex());
        String[] attributes = parse.getIndividualAttributes();
        Assert.assertEquals(attributes[0], "key=>value");
        Assert.assertEquals(29, parse.getCellVisibilityColumnOffset());
    }
}

