/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.drill.test.rowSet.test;


import DataMode.REQUIRED;
import MinorType.BIGINT;
import MinorType.DATE;
import MinorType.DECIMAL18;
import MinorType.DECIMAL9;
import MinorType.FLOAT4;
import MinorType.FLOAT8;
import MinorType.INT;
import MinorType.INTERVAL;
import MinorType.INTERVALDAY;
import MinorType.INTERVALYEAR;
import MinorType.SMALLINT;
import MinorType.TIMESTAMP;
import MinorType.TINYINT;
import MinorType.UINT1;
import MinorType.UINT2;
import MinorType.VARBINARY;
import MinorType.VARCHAR;
import ValueType.BYTES;
import ValueType.DECIMAL;
import ValueType.DOUBLE;
import ValueType.INTEGER;
import ValueType.PERIOD;
import ValueType.STRING;
import java.math.BigDecimal;
import java.util.Arrays;
import org.apache.drill.categories.RowSetTests;
import org.apache.drill.common.types.TypeProtos.MajorType;
import org.apache.drill.exec.record.BatchSchema;
import org.apache.drill.exec.record.metadata.SchemaBuilder;
import org.apache.drill.exec.vector.DateUtilities;
import org.apache.drill.exec.vector.accessor.ArrayReader;
import org.apache.drill.exec.vector.accessor.ScalarReader;
import org.apache.drill.shaded.guava.com.google.common.collect.Lists;
import org.apache.drill.test.SubOperatorTest;
import org.apache.drill.test.rowSet.RowSet;
import org.apache.drill.test.rowSet.RowSetReader;
import org.joda.time.Period;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Verify that simple scalar (non-repeated) column readers
 * and writers work as expected. The focus is on the generated
 * and type-specific functions for each type.
 */
// The following types are not fully supported in Drill
// TODO: Var16Char
// TODO: Bit
// TODO: Decimal28Sparse
// TODO: Decimal38Sparse
@Category(RowSetTests.class)
public class TestScalarAccessors extends SubOperatorTest {
    @Test
    public void testUInt1RW() {
        BatchSchema batchSchema = new SchemaBuilder().add("col", UINT1).build();
        RowSet.SingleRowSet rs = SubOperatorTest.fixture.rowSetBuilder(batchSchema).addRow(0).addRow(127).addRow(255).build();
        Assert.assertEquals(3, rs.rowCount());
        RowSetReader reader = rs.reader();
        ScalarReader colReader = scalar(0);
        Assert.assertEquals(INTEGER, colReader.valueType());
        Assert.assertTrue(reader.next());
        Assert.assertFalse(colReader.isNull());
        Assert.assertEquals(0, colReader.getInt());
        Assert.assertTrue(reader.next());
        Assert.assertEquals(127, colReader.getInt());
        Assert.assertEquals(127, colReader.getObject());
        Assert.assertEquals(Integer.toString(127), colReader.getAsString());
        Assert.assertTrue(reader.next());
        Assert.assertEquals(255, colReader.getInt());
        Assert.assertFalse(reader.next());
        rs.clear();
    }

    @Test
    public void testUInt2RW() {
        BatchSchema batchSchema = new SchemaBuilder().add("col", UINT2).build();
        RowSet.SingleRowSet rs = SubOperatorTest.fixture.rowSetBuilder(batchSchema).addRow(0).addRow(32767).addRow(65535).build();
        Assert.assertEquals(3, rs.rowCount());
        RowSetReader reader = rs.reader();
        ScalarReader colReader = scalar(0);
        Assert.assertEquals(INTEGER, colReader.valueType());
        Assert.assertTrue(reader.next());
        Assert.assertFalse(colReader.isNull());
        Assert.assertEquals(0, colReader.getInt());
        Assert.assertTrue(reader.next());
        Assert.assertEquals(32767, colReader.getInt());
        Assert.assertEquals(32767, colReader.getObject());
        Assert.assertEquals(Integer.toString(32767), colReader.getAsString());
        Assert.assertTrue(reader.next());
        Assert.assertEquals(65535, colReader.getInt());
        Assert.assertFalse(reader.next());
        rs.clear();
    }

    @Test
    public void testTinyIntRW() {
        BatchSchema batchSchema = new SchemaBuilder().add("col", TINYINT).build();
        RowSet.SingleRowSet rs = SubOperatorTest.fixture.rowSetBuilder(batchSchema).addRow(0).addRow(Byte.MAX_VALUE).addRow(Byte.MIN_VALUE).build();
        Assert.assertEquals(3, rs.rowCount());
        RowSetReader reader = rs.reader();
        ScalarReader colReader = scalar(0);
        Assert.assertEquals(INTEGER, colReader.valueType());
        Assert.assertTrue(reader.next());
        Assert.assertFalse(colReader.isNull());
        Assert.assertEquals(0, colReader.getInt());
        Assert.assertTrue(reader.next());
        Assert.assertEquals(Byte.MAX_VALUE, colReader.getInt());
        Assert.assertEquals(((int) (Byte.MAX_VALUE)), colReader.getObject());
        Assert.assertEquals(Byte.toString(Byte.MAX_VALUE), colReader.getAsString());
        Assert.assertTrue(reader.next());
        Assert.assertEquals(Byte.MIN_VALUE, colReader.getInt());
        Assert.assertFalse(reader.next());
        rs.clear();
    }

    @Test
    public void testNullableTinyInt() {
        nullableIntTester(TINYINT);
    }

    @Test
    public void testTinyIntArray() {
        intArrayTester(TINYINT);
    }

    @Test
    public void testSmallIntRW() {
        BatchSchema batchSchema = new SchemaBuilder().add("col", SMALLINT).build();
        RowSet.SingleRowSet rs = SubOperatorTest.fixture.rowSetBuilder(batchSchema).addRow(0).addRow(Short.MAX_VALUE).addRow(Short.MIN_VALUE).build();
        Assert.assertEquals(3, rs.rowCount());
        RowSetReader reader = rs.reader();
        ScalarReader colReader = scalar(0);
        Assert.assertEquals(INTEGER, colReader.valueType());
        Assert.assertTrue(reader.next());
        Assert.assertFalse(colReader.isNull());
        Assert.assertEquals(0, colReader.getInt());
        Assert.assertTrue(reader.next());
        Assert.assertEquals(Short.MAX_VALUE, colReader.getInt());
        Assert.assertEquals(((int) (Short.MAX_VALUE)), colReader.getObject());
        Assert.assertEquals(Short.toString(Short.MAX_VALUE), colReader.getAsString());
        Assert.assertTrue(reader.next());
        Assert.assertEquals(Short.MIN_VALUE, colReader.getInt());
        Assert.assertFalse(reader.next());
        rs.clear();
    }

    @Test
    public void testNullableSmallInt() {
        nullableIntTester(SMALLINT);
    }

    @Test
    public void testSmallArray() {
        intArrayTester(SMALLINT);
    }

    @Test
    public void testIntRW() {
        BatchSchema batchSchema = new SchemaBuilder().add("col", INT).build();
        RowSet.SingleRowSet rs = SubOperatorTest.fixture.rowSetBuilder(batchSchema).addRow(0).addRow(Integer.MAX_VALUE).addRow(Integer.MIN_VALUE).build();
        Assert.assertEquals(3, rs.rowCount());
        RowSetReader reader = rs.reader();
        ScalarReader colReader = scalar(0);
        Assert.assertEquals(INTEGER, colReader.valueType());
        Assert.assertTrue(reader.next());
        Assert.assertFalse(colReader.isNull());
        Assert.assertEquals(0, reader.scalar(0).getInt());
        Assert.assertTrue(reader.next());
        Assert.assertEquals(Integer.MAX_VALUE, colReader.getInt());
        Assert.assertEquals(Integer.MAX_VALUE, colReader.getObject());
        Assert.assertEquals(Integer.toString(Integer.MAX_VALUE), colReader.getAsString());
        Assert.assertTrue(reader.next());
        Assert.assertEquals(Integer.MIN_VALUE, colReader.getInt());
        Assert.assertFalse(reader.next());
        rs.clear();
    }

    @Test
    public void testNullableInt() {
        nullableIntTester(INT);
    }

    @Test
    public void testIntArray() {
        intArrayTester(INT);
    }

    @Test
    public void testLongRW() {
        longRWTester(BIGINT);
    }

    @Test
    public void testNullableLong() {
        nullableLongTester(BIGINT);
    }

    @Test
    public void testLongArray() {
        longArrayTester(BIGINT);
    }

    @Test
    public void testFloatRW() {
        BatchSchema batchSchema = new SchemaBuilder().add("col", FLOAT4).build();
        RowSet.SingleRowSet rs = SubOperatorTest.fixture.rowSetBuilder(batchSchema).addRow(0.0F).addRow(Float.MAX_VALUE).addRow(Float.MIN_VALUE).addRow(100.0F).build();
        Assert.assertEquals(4, rs.rowCount());
        RowSetReader reader = rs.reader();
        ScalarReader colReader = scalar(0);
        Assert.assertEquals(DOUBLE, colReader.valueType());
        Assert.assertTrue(reader.next());
        Assert.assertFalse(colReader.isNull());
        Assert.assertEquals(0, colReader.getDouble(), 1.0E-6);
        Assert.assertTrue(reader.next());
        Assert.assertEquals(Float.MAX_VALUE, colReader.getDouble(), 1.0E-6);
        Assert.assertEquals(Float.MAX_VALUE, ((double) (colReader.getObject())), 1.0E-6);
        Assert.assertTrue(reader.next());
        Assert.assertEquals(Float.MIN_VALUE, colReader.getDouble(), 1.0E-6);
        Assert.assertTrue(reader.next());
        Assert.assertEquals(100, colReader.getDouble(), 1.0E-6);
        Assert.assertEquals("100.0", colReader.getAsString());
        Assert.assertFalse(reader.next());
        rs.clear();
    }

    @Test
    public void testNullableFloat() {
        nullableDoubleTester(FLOAT4);
    }

    @Test
    public void testFloatArray() {
        doubleArrayTester(FLOAT4);
    }

    @Test
    public void testDoubleRW() {
        BatchSchema batchSchema = new SchemaBuilder().add("col", FLOAT8).build();
        RowSet.SingleRowSet rs = SubOperatorTest.fixture.rowSetBuilder(batchSchema).addRow(0.0).addRow(Double.MAX_VALUE).addRow(Double.MIN_VALUE).addRow(100.0).build();
        Assert.assertEquals(4, rs.rowCount());
        RowSetReader reader = rs.reader();
        ScalarReader colReader = scalar(0);
        Assert.assertEquals(DOUBLE, colReader.valueType());
        Assert.assertTrue(reader.next());
        Assert.assertFalse(colReader.isNull());
        Assert.assertEquals(0, colReader.getDouble(), 1.0E-6);
        Assert.assertTrue(reader.next());
        Assert.assertEquals(Double.MAX_VALUE, colReader.getDouble(), 1.0E-6);
        Assert.assertEquals(Double.MAX_VALUE, ((double) (colReader.getObject())), 1.0E-6);
        Assert.assertTrue(reader.next());
        Assert.assertEquals(Double.MIN_VALUE, colReader.getDouble(), 1.0E-6);
        Assert.assertTrue(reader.next());
        Assert.assertEquals(100, colReader.getDouble(), 1.0E-6);
        Assert.assertEquals("100.0", colReader.getAsString());
        Assert.assertFalse(reader.next());
        rs.clear();
    }

    @Test
    public void testNullableDouble() {
        nullableDoubleTester(FLOAT8);
    }

    @Test
    public void testDoubleArray() {
        doubleArrayTester(FLOAT8);
    }

    @Test
    public void testVarcharRW() {
        BatchSchema batchSchema = new SchemaBuilder().add("col", VARCHAR).build();
        RowSet.SingleRowSet rs = SubOperatorTest.fixture.rowSetBuilder(batchSchema).addRow("").addRow("fred").addRow("barney").build();
        Assert.assertEquals(3, rs.rowCount());
        RowSetReader reader = rs.reader();
        ScalarReader colReader = scalar(0);
        Assert.assertEquals(STRING, colReader.valueType());
        Assert.assertTrue(reader.next());
        Assert.assertFalse(colReader.isNull());
        Assert.assertEquals("", colReader.getString());
        Assert.assertTrue(reader.next());
        Assert.assertEquals("fred", colReader.getString());
        Assert.assertEquals("fred", colReader.getObject());
        Assert.assertEquals("\"fred\"", colReader.getAsString());
        Assert.assertTrue(reader.next());
        Assert.assertEquals("barney", colReader.getString());
        Assert.assertEquals("barney", colReader.getObject());
        Assert.assertEquals("\"barney\"", colReader.getAsString());
        Assert.assertFalse(reader.next());
        rs.clear();
    }

    @Test
    public void testNullableVarchar() {
        BatchSchema batchSchema = new SchemaBuilder().addNullable("col", VARCHAR).build();
        RowSet.SingleRowSet rs = SubOperatorTest.fixture.rowSetBuilder(batchSchema).addRow("").addSingleCol(null).addRow("abcd").build();
        Assert.assertEquals(3, rs.rowCount());
        RowSetReader reader = rs.reader();
        ScalarReader colReader = scalar(0);
        Assert.assertTrue(reader.next());
        Assert.assertFalse(colReader.isNull());
        Assert.assertEquals("", colReader.getString());
        Assert.assertTrue(reader.next());
        Assert.assertTrue(colReader.isNull());
        Assert.assertNull(colReader.getObject());
        Assert.assertEquals("null", colReader.getAsString());
        Assert.assertTrue(reader.next());
        Assert.assertEquals("abcd", colReader.getString());
        Assert.assertEquals("abcd", colReader.getObject());
        Assert.assertEquals("\"abcd\"", colReader.getAsString());
        Assert.assertFalse(reader.next());
        rs.clear();
    }

    @Test
    public void testVarcharArray() {
        BatchSchema batchSchema = new SchemaBuilder().addArray("col", VARCHAR).build();
        RowSet.SingleRowSet rs = SubOperatorTest.fixture.rowSetBuilder(batchSchema).addSingleCol(new String[]{  }).addSingleCol(new String[]{ "fred", "", "wilma" }).build();
        Assert.assertEquals(2, rs.rowCount());
        RowSetReader reader = rs.reader();
        ArrayReader arrayReader = array(0);
        ScalarReader colReader = arrayReader.scalar();
        Assert.assertEquals(STRING, colReader.valueType());
        Assert.assertTrue(reader.next());
        Assert.assertEquals(0, arrayReader.size());
        Assert.assertTrue(reader.next());
        Assert.assertEquals(3, arrayReader.size());
        Assert.assertTrue(arrayReader.next());
        Assert.assertEquals("fred", colReader.getString());
        Assert.assertEquals("fred", colReader.getObject());
        Assert.assertEquals("\"fred\"", colReader.getAsString());
        Assert.assertTrue(arrayReader.next());
        Assert.assertEquals("", colReader.getString());
        Assert.assertEquals("", colReader.getObject());
        Assert.assertEquals("\"\"", colReader.getAsString());
        Assert.assertTrue(arrayReader.next());
        Assert.assertEquals("wilma", colReader.getString());
        Assert.assertEquals("wilma", colReader.getObject());
        Assert.assertEquals("\"wilma\"", colReader.getAsString());
        Assert.assertFalse(arrayReader.next());
        Assert.assertEquals("[\"fred\", \"\", \"wilma\"]", arrayReader.getAsString());
        Assert.assertEquals(Lists.newArrayList("fred", "", "wilma"), arrayReader.getObject());
        Assert.assertFalse(reader.next());
        rs.clear();
    }

    /**
     * Test the low-level interval-year utilities used by the column accessors.
     */
    @Test
    public void testIntervalYearUtils() {
        {
            Period expected = Period.months(0);
            Period actual = DateUtilities.fromIntervalYear(0);
            Assert.assertEquals(expected, actual.normalizedStandard());
            String fmt = DateUtilities.intervalYearStringBuilder(expected).toString();
            Assert.assertEquals("0 years 0 months", fmt);
        }
        {
            Period expected = Period.years(1).plusMonths(2);
            Period actual = DateUtilities.fromIntervalYear(DateUtilities.periodToMonths(expected));
            Assert.assertEquals(expected, actual.normalizedStandard());
            String fmt = DateUtilities.intervalYearStringBuilder(expected).toString();
            Assert.assertEquals("1 year 2 months", fmt);
        }
        {
            Period expected = Period.years(6).plusMonths(1);
            Period actual = DateUtilities.fromIntervalYear(DateUtilities.periodToMonths(expected));
            Assert.assertEquals(expected, actual.normalizedStandard());
            String fmt = DateUtilities.intervalYearStringBuilder(expected).toString();
            Assert.assertEquals("6 years 1 month", fmt);
        }
    }

    @Test
    public void testIntervalYearRW() {
        BatchSchema batchSchema = new SchemaBuilder().add("col", INTERVALYEAR).build();
        Period p1 = Period.years(0);
        Period p2 = Period.years(2).plusMonths(3);
        Period p3 = Period.years(1234).plusMonths(11);
        RowSet.SingleRowSet rs = SubOperatorTest.fixture.rowSetBuilder(batchSchema).addRow(p1).addRow(p2).addRow(p3).build();
        Assert.assertEquals(3, rs.rowCount());
        RowSetReader reader = rs.reader();
        ScalarReader colReader = scalar(0);
        Assert.assertEquals(PERIOD, colReader.valueType());
        Assert.assertTrue(reader.next());
        Assert.assertFalse(colReader.isNull());
        Assert.assertEquals(p1, colReader.getPeriod());
        Assert.assertTrue(reader.next());
        Assert.assertEquals(p2, colReader.getPeriod());
        Assert.assertEquals(p2, colReader.getObject());
        Assert.assertEquals(p2.toString(), colReader.getAsString());
        Assert.assertTrue(reader.next());
        Assert.assertEquals(p3, colReader.getPeriod());
        Assert.assertFalse(reader.next());
        rs.clear();
    }

    @Test
    public void testNullableIntervalYear() {
        BatchSchema batchSchema = new SchemaBuilder().addNullable("col", INTERVALYEAR).build();
        Period p1 = Period.years(0);
        Period p2 = Period.years(2).plusMonths(3);
        RowSet.SingleRowSet rs = SubOperatorTest.fixture.rowSetBuilder(batchSchema).addRow(p1).addSingleCol(null).addRow(p2).build();
        Assert.assertEquals(3, rs.rowCount());
        RowSetReader reader = rs.reader();
        ScalarReader colReader = scalar(0);
        Assert.assertEquals(PERIOD, colReader.valueType());
        Assert.assertTrue(reader.next());
        Assert.assertFalse(colReader.isNull());
        Assert.assertEquals(p1, colReader.getPeriod());
        Assert.assertTrue(reader.next());
        Assert.assertTrue(colReader.isNull());
        Assert.assertNull(colReader.getObject());
        Assert.assertEquals("null", colReader.getAsString());
        Assert.assertTrue(reader.next());
        Assert.assertEquals(p2, colReader.getPeriod());
        Assert.assertFalse(reader.next());
        rs.clear();
    }

    @Test
    public void testIntervalYearArray() {
        BatchSchema batchSchema = new SchemaBuilder().addArray("col", INTERVALYEAR).build();
        Period p1 = Period.years(0);
        Period p2 = Period.years(2).plusMonths(3);
        Period p3 = Period.years(1234).plusMonths(11);
        RowSet.SingleRowSet rs = SubOperatorTest.fixture.rowSetBuilder(batchSchema).addSingleCol(new Period[]{  }).addSingleCol(new Period[]{ p1, p2, p3 }).build();
        Assert.assertEquals(2, rs.rowCount());
        RowSetReader reader = rs.reader();
        ArrayReader arrayReader = array(0);
        ScalarReader colReader = arrayReader.scalar();
        Assert.assertEquals(PERIOD, colReader.valueType());
        Assert.assertTrue(reader.next());
        Assert.assertEquals(0, arrayReader.size());
        Assert.assertTrue(reader.next());
        Assert.assertEquals(3, arrayReader.size());
        Assert.assertTrue(arrayReader.next());
        Assert.assertEquals(p1, colReader.getPeriod());
        Assert.assertTrue(arrayReader.next());
        Assert.assertEquals(p2, colReader.getPeriod());
        Assert.assertEquals(p2, colReader.getObject());
        Assert.assertEquals(p2.toString(), colReader.getAsString());
        Assert.assertTrue(arrayReader.next());
        Assert.assertEquals(p3, colReader.getPeriod());
        Assert.assertFalse(arrayReader.next());
        Assert.assertFalse(reader.next());
        rs.clear();
    }

    /**
     * Test the low-level interval-day utilities used by the column accessors.
     */
    @Test
    public void testIntervalDayUtils() {
        {
            Period expected = Period.days(0);
            Period actual = DateUtilities.fromIntervalDay(0, 0);
            Assert.assertEquals(expected, actual.normalizedStandard());
            String fmt = DateUtilities.intervalDayStringBuilder(expected).toString();
            Assert.assertEquals("0 days 0:00:00", fmt);
        }
        {
            Period expected = Period.days(1).plusHours(5).plusMinutes(6).plusSeconds(7);
            Period actual = DateUtilities.fromIntervalDay(1, DateUtilities.timeToMillis(5, 6, 7, 0));
            Assert.assertEquals(expected, actual.normalizedStandard());
            String fmt = DateUtilities.intervalDayStringBuilder(expected).toString();
            Assert.assertEquals("1 day 5:06:07", fmt);
        }
        {
            Period expected = Period.days(2).plusHours(12).plusMinutes(23).plusSeconds(34).plusMillis(567);
            Period actual = DateUtilities.fromIntervalDay(2, DateUtilities.timeToMillis(12, 23, 34, 567));
            Assert.assertEquals(expected, actual.normalizedStandard());
            String fmt = DateUtilities.intervalDayStringBuilder(expected).toString();
            Assert.assertEquals("2 days 12:23:34.567", fmt);
        }
    }

    @Test
    public void testIntervalDayRW() {
        BatchSchema batchSchema = new SchemaBuilder().add("col", INTERVALDAY).build();
        Period p1 = Period.days(0);
        Period p2 = Period.days(3).plusHours(4).plusMinutes(5).plusSeconds(23);
        Period p3 = Period.days(999).plusHours(23).plusMinutes(59).plusSeconds(59);
        RowSet.SingleRowSet rs = SubOperatorTest.fixture.rowSetBuilder(batchSchema).addRow(p1).addRow(p2).addRow(p3).build();
        Assert.assertEquals(3, rs.rowCount());
        RowSetReader reader = rs.reader();
        ScalarReader colReader = scalar(0);
        Assert.assertEquals(PERIOD, colReader.valueType());
        Assert.assertTrue(reader.next());
        Assert.assertFalse(colReader.isNull());
        // The normalizedStandard() call is a hack. See DRILL-5689.
        Assert.assertEquals(p1, normalizedStandard());
        Assert.assertTrue(reader.next());
        Assert.assertEquals(p2, normalizedStandard());
        Assert.assertEquals(p2, normalizedStandard());
        Assert.assertEquals(p2.toString(), colReader.getAsString());
        Assert.assertTrue(reader.next());
        Assert.assertEquals(p3.normalizedStandard(), normalizedStandard());
        Assert.assertFalse(reader.next());
        rs.clear();
    }

    @Test
    public void testNullableIntervalDay() {
        BatchSchema batchSchema = new SchemaBuilder().addNullable("col", INTERVALDAY).build();
        Period p1 = Period.years(0);
        Period p2 = Period.days(3).plusHours(4).plusMinutes(5).plusSeconds(23);
        RowSet.SingleRowSet rs = SubOperatorTest.fixture.rowSetBuilder(batchSchema).addRow(p1).addSingleCol(null).addRow(p2).build();
        Assert.assertEquals(3, rs.rowCount());
        RowSetReader reader = rs.reader();
        ScalarReader colReader = scalar(0);
        Assert.assertEquals(PERIOD, colReader.valueType());
        Assert.assertTrue(reader.next());
        Assert.assertFalse(colReader.isNull());
        Assert.assertEquals(p1, normalizedStandard());
        Assert.assertTrue(reader.next());
        Assert.assertTrue(colReader.isNull());
        Assert.assertNull(colReader.getObject());
        Assert.assertEquals("null", colReader.getAsString());
        Assert.assertTrue(reader.next());
        Assert.assertEquals(p2, normalizedStandard());
        Assert.assertFalse(reader.next());
        rs.clear();
    }

    @Test
    public void testIntervalDayArray() {
        BatchSchema batchSchema = new SchemaBuilder().addArray("col", INTERVALDAY).build();
        Period p1 = Period.days(0);
        Period p2 = Period.days(3).plusHours(4).plusMinutes(5).plusSeconds(23);
        Period p3 = Period.days(999).plusHours(23).plusMinutes(59).plusSeconds(59);
        RowSet.SingleRowSet rs = SubOperatorTest.fixture.rowSetBuilder(batchSchema).addSingleCol(new Period[]{  }).addSingleCol(new Period[]{ p1, p2, p3 }).build();
        Assert.assertEquals(2, rs.rowCount());
        RowSetReader reader = rs.reader();
        ArrayReader arrayReader = array(0);
        ScalarReader colReader = arrayReader.scalar();
        Assert.assertEquals(PERIOD, colReader.valueType());
        Assert.assertTrue(reader.next());
        Assert.assertEquals(0, arrayReader.size());
        Assert.assertTrue(reader.next());
        Assert.assertEquals(3, arrayReader.size());
        Assert.assertTrue(arrayReader.next());
        Assert.assertEquals(p1, normalizedStandard());
        Assert.assertTrue(arrayReader.next());
        Assert.assertEquals(p2, normalizedStandard());
        Assert.assertEquals(p2, normalizedStandard());
        Assert.assertEquals(p2.toString(), colReader.getAsString());
        Assert.assertTrue(arrayReader.next());
        Assert.assertEquals(p3.normalizedStandard(), normalizedStandard());
        Assert.assertFalse(arrayReader.next());
        Assert.assertFalse(reader.next());
        rs.clear();
    }

    /**
     * Test the low-level interval utilities used by the column accessors.
     */
    @Test
    public void testIntervalUtils() {
        {
            Period expected = Period.months(0);
            Period actual = DateUtilities.fromInterval(0, 0, 0);
            Assert.assertEquals(expected, actual.normalizedStandard());
            String fmt = DateUtilities.intervalStringBuilder(expected).toString();
            Assert.assertEquals("0 years 0 months 0 days 0:00:00", fmt);
        }
        {
            Period expected = Period.years(1).plusMonths(2).plusDays(3).plusHours(5).plusMinutes(6).plusSeconds(7);
            Period actual = DateUtilities.fromInterval(DateUtilities.periodToMonths(expected), 3, DateUtilities.periodToMillis(expected));
            Assert.assertEquals(expected, actual.normalizedStandard());
            String fmt = DateUtilities.intervalStringBuilder(expected).toString();
            Assert.assertEquals("1 year 2 months 3 days 5:06:07", fmt);
        }
        {
            Period expected = Period.years(2).plusMonths(1).plusDays(3).plusHours(12).plusMinutes(23).plusSeconds(34).plusMillis(456);
            Period actual = DateUtilities.fromInterval(DateUtilities.periodToMonths(expected), 3, DateUtilities.periodToMillis(expected));
            Assert.assertEquals(expected, actual.normalizedStandard());
            String fmt = DateUtilities.intervalStringBuilder(expected).toString();
            Assert.assertEquals("2 years 1 month 3 days 12:23:34.456", fmt);
        }
        {
            Period expected = Period.years(2).plusMonths(3).plusDays(1).plusHours(12).plusMinutes(23).plusSeconds(34);
            Period actual = DateUtilities.fromInterval(DateUtilities.periodToMonths(expected), 1, DateUtilities.periodToMillis(expected));
            Assert.assertEquals(expected, actual.normalizedStandard());
            String fmt = DateUtilities.intervalStringBuilder(expected).toString();
            Assert.assertEquals("2 years 3 months 1 day 12:23:34", fmt);
        }
    }

    @Test
    public void testIntervalRW() {
        BatchSchema batchSchema = new SchemaBuilder().add("col", INTERVAL).build();
        Period p1 = Period.days(0);
        Period p2 = Period.years(7).plusMonths(8).plusDays(3).plusHours(4).plusMinutes(5).plusSeconds(23);
        Period p3 = Period.years(9999).plusMonths(11).plusDays(365).plusHours(23).plusMinutes(59).plusSeconds(59);
        RowSet.SingleRowSet rs = SubOperatorTest.fixture.rowSetBuilder(batchSchema).addRow(p1).addRow(p2).addRow(p3).build();
        Assert.assertEquals(3, rs.rowCount());
        RowSetReader reader = rs.reader();
        ScalarReader colReader = scalar(0);
        Assert.assertEquals(PERIOD, colReader.valueType());
        Assert.assertTrue(reader.next());
        Assert.assertFalse(colReader.isNull());
        // The normalizedStandard() call is a hack. See DRILL-5689.
        Assert.assertEquals(p1, normalizedStandard());
        Assert.assertTrue(reader.next());
        Assert.assertEquals(p2, normalizedStandard());
        Assert.assertEquals(p2, normalizedStandard());
        Assert.assertEquals(p2.toString(), colReader.getAsString());
        Assert.assertTrue(reader.next());
        Assert.assertEquals(p3.normalizedStandard(), normalizedStandard());
        Assert.assertFalse(reader.next());
        rs.clear();
    }

    @Test
    public void testNullableInterval() {
        BatchSchema batchSchema = new SchemaBuilder().addNullable("col", INTERVAL).build();
        Period p1 = Period.years(0);
        Period p2 = Period.years(7).plusMonths(8).plusDays(3).plusHours(4).plusMinutes(5).plusSeconds(23);
        RowSet.SingleRowSet rs = SubOperatorTest.fixture.rowSetBuilder(batchSchema).addRow(p1).addSingleCol(null).addRow(p2).build();
        Assert.assertEquals(3, rs.rowCount());
        RowSetReader reader = rs.reader();
        ScalarReader colReader = scalar(0);
        Assert.assertEquals(PERIOD, colReader.valueType());
        Assert.assertTrue(reader.next());
        Assert.assertFalse(colReader.isNull());
        Assert.assertEquals(p1, normalizedStandard());
        Assert.assertTrue(reader.next());
        Assert.assertTrue(colReader.isNull());
        Assert.assertNull(colReader.getObject());
        Assert.assertEquals("null", colReader.getAsString());
        Assert.assertTrue(reader.next());
        Assert.assertEquals(p2, normalizedStandard());
        Assert.assertFalse(reader.next());
        rs.clear();
    }

    @Test
    public void testIntervalArray() {
        BatchSchema batchSchema = new SchemaBuilder().addArray("col", INTERVAL).build();
        Period p1 = Period.days(0);
        Period p2 = Period.years(7).plusMonths(8).plusDays(3).plusHours(4).plusMinutes(5).plusSeconds(23);
        Period p3 = Period.years(9999).plusMonths(11).plusDays(365).plusHours(23).plusMinutes(59).plusSeconds(59);
        RowSet.SingleRowSet rs = SubOperatorTest.fixture.rowSetBuilder(batchSchema).addSingleCol(new Period[]{  }).addSingleCol(new Period[]{ p1, p2, p3 }).build();
        Assert.assertEquals(2, rs.rowCount());
        RowSetReader reader = rs.reader();
        ArrayReader arrayReader = array(0);
        ScalarReader colReader = arrayReader.scalar();
        Assert.assertEquals(PERIOD, colReader.valueType());
        Assert.assertTrue(reader.next());
        Assert.assertEquals(0, arrayReader.size());
        Assert.assertTrue(reader.next());
        Assert.assertEquals(3, arrayReader.size());
        Assert.assertTrue(arrayReader.next());
        Assert.assertEquals(p1, normalizedStandard());
        Assert.assertTrue(arrayReader.next());
        Assert.assertEquals(p2, normalizedStandard());
        Assert.assertEquals(p2, normalizedStandard());
        Assert.assertEquals(p2.toString(), colReader.getAsString());
        Assert.assertTrue(arrayReader.next());
        Assert.assertEquals(p3.normalizedStandard(), normalizedStandard());
        Assert.assertFalse(arrayReader.next());
        Assert.assertFalse(reader.next());
        rs.clear();
    }

    @Test
    public void testDecimal9RW() {
        MajorType type = MajorType.newBuilder().setMinorType(DECIMAL9).setScale(3).setPrecision(9).setMode(REQUIRED).build();
        BatchSchema batchSchema = new SchemaBuilder().add("col", type).build();
        BigDecimal v1 = BigDecimal.ZERO;
        BigDecimal v2 = BigDecimal.valueOf(123456789, 3);
        BigDecimal v3 = BigDecimal.valueOf(999999999, 3);
        RowSet.SingleRowSet rs = SubOperatorTest.fixture.rowSetBuilder(batchSchema).addRow(v1).addRow(v2).addRow(v3).build();
        Assert.assertEquals(3, rs.rowCount());
        RowSetReader reader = rs.reader();
        ScalarReader colReader = scalar(0);
        Assert.assertEquals(DECIMAL, colReader.valueType());
        Assert.assertTrue(reader.next());
        Assert.assertFalse(colReader.isNull());
        Assert.assertEquals(0, v1.compareTo(colReader.getDecimal()));
        Assert.assertTrue(reader.next());
        Assert.assertEquals(0, v2.compareTo(colReader.getDecimal()));
        Assert.assertEquals(0, v2.compareTo(((BigDecimal) (colReader.getObject()))));
        Assert.assertEquals(v2.toString(), colReader.getAsString());
        Assert.assertTrue(reader.next());
        Assert.assertEquals(0, v3.compareTo(colReader.getDecimal()));
        Assert.assertFalse(reader.next());
        rs.clear();
    }

    @Test
    public void testNullableDecimal9() {
        nullableDecimalTester(DECIMAL9, 9);
    }

    @Test
    public void testDecimal9Array() {
        decimalArrayTester(DECIMAL9, 9);
    }

    @Test
    public void testDecimal18RW() {
        MajorType type = MajorType.newBuilder().setMinorType(DECIMAL18).setScale(3).setPrecision(9).setMode(REQUIRED).build();
        BatchSchema batchSchema = new SchemaBuilder().add("col", type).build();
        BigDecimal v1 = BigDecimal.ZERO;
        BigDecimal v2 = BigDecimal.valueOf(123456789123456789L, 3);
        BigDecimal v3 = BigDecimal.valueOf(999999999999999999L, 3);
        RowSet.SingleRowSet rs = SubOperatorTest.fixture.rowSetBuilder(batchSchema).addRow(v1).addRow(v2).addRow(v3).build();
        Assert.assertEquals(3, rs.rowCount());
        RowSetReader reader = rs.reader();
        ScalarReader colReader = scalar(0);
        Assert.assertEquals(DECIMAL, colReader.valueType());
        Assert.assertTrue(reader.next());
        Assert.assertFalse(colReader.isNull());
        Assert.assertEquals(0, v1.compareTo(colReader.getDecimal()));
        Assert.assertTrue(reader.next());
        Assert.assertEquals(0, v2.compareTo(colReader.getDecimal()));
        Assert.assertEquals(0, v2.compareTo(((BigDecimal) (colReader.getObject()))));
        Assert.assertEquals(v2.toString(), colReader.getAsString());
        Assert.assertTrue(reader.next());
        Assert.assertEquals(0, v3.compareTo(colReader.getDecimal()));
        Assert.assertFalse(reader.next());
        rs.clear();
    }

    @Test
    public void testNullableDecimal18() {
        nullableDecimalTester(DECIMAL18, 9);
    }

    @Test
    public void testDecimal18Array() {
        decimalArrayTester(DECIMAL18, 9);
    }

    // From the perspective of the vector, a date vector is just a long.
    @Test
    public void testDateRW() {
        longRWTester(DATE);
    }

    @Test
    public void testNullableDate() {
        nullableLongTester(DATE);
    }

    @Test
    public void testDateArray() {
        longArrayTester(DATE);
    }

    // From the perspective of the vector, a timestamp vector is just a long.
    @Test
    public void testTimestampRW() {
        longRWTester(TIMESTAMP);
    }

    @Test
    public void testNullableTimestamp() {
        nullableLongTester(TIMESTAMP);
    }

    @Test
    public void testTimestampArray() {
        longArrayTester(TIMESTAMP);
    }

    @Test
    public void testVarBinaryRW() {
        BatchSchema batchSchema = new SchemaBuilder().add("col", VARBINARY).build();
        byte[] v1 = new byte[]{  };
        byte[] v2 = new byte[]{ ((byte) (0)), ((byte) (127)), ((byte) (128)), ((byte) (255)) };
        RowSet.SingleRowSet rs = SubOperatorTest.fixture.rowSetBuilder(batchSchema).addRow(v1).addRow(v2).build();
        Assert.assertEquals(2, rs.rowCount());
        RowSetReader reader = rs.reader();
        ScalarReader colReader = scalar(0);
        Assert.assertEquals(BYTES, colReader.valueType());
        Assert.assertTrue(reader.next());
        Assert.assertFalse(colReader.isNull());
        Assert.assertTrue(Arrays.equals(v1, colReader.getBytes()));
        Assert.assertTrue(reader.next());
        Assert.assertTrue(Arrays.equals(v2, colReader.getBytes()));
        Assert.assertTrue(Arrays.equals(v2, ((byte[]) (colReader.getObject()))));
        Assert.assertEquals("[00, 7f, 80, ff]", colReader.getAsString());
        Assert.assertFalse(reader.next());
        rs.clear();
    }

    @Test
    public void testNullableVarBinary() {
        BatchSchema batchSchema = new SchemaBuilder().addNullable("col", VARBINARY).build();
        byte[] v1 = new byte[]{  };
        byte[] v2 = new byte[]{ ((byte) (0)), ((byte) (127)), ((byte) (128)), ((byte) (255)) };
        RowSet.SingleRowSet rs = SubOperatorTest.fixture.rowSetBuilder(batchSchema).addRow(v1).addSingleCol(null).addRow(v2).build();
        Assert.assertEquals(3, rs.rowCount());
        RowSetReader reader = rs.reader();
        ScalarReader colReader = scalar(0);
        Assert.assertEquals(BYTES, colReader.valueType());
        Assert.assertTrue(reader.next());
        Assert.assertFalse(colReader.isNull());
        Assert.assertTrue(Arrays.equals(v1, colReader.getBytes()));
        Assert.assertTrue(reader.next());
        Assert.assertTrue(colReader.isNull());
        Assert.assertNull(colReader.getObject());
        Assert.assertEquals("null", colReader.getAsString());
        Assert.assertTrue(reader.next());
        Assert.assertTrue(Arrays.equals(v2, colReader.getBytes()));
        Assert.assertFalse(reader.next());
        rs.clear();
    }

    @Test
    public void testVarBinaryArray() {
        BatchSchema batchSchema = new SchemaBuilder().addArray("col", VARBINARY).build();
        byte[] v1 = new byte[]{  };
        byte[] v2 = new byte[]{ ((byte) (0)), ((byte) (127)), ((byte) (128)), ((byte) (255)) };
        byte[] v3 = new byte[]{ ((byte) (222)), ((byte) (173)), ((byte) (190)), ((byte) (175)) };
        RowSet.SingleRowSet rs = SubOperatorTest.fixture.rowSetBuilder(batchSchema).addSingleCol(new byte[][]{  }).addSingleCol(new byte[][]{ v1, v2, v3 }).build();
        Assert.assertEquals(2, rs.rowCount());
        RowSetReader reader = rs.reader();
        ArrayReader arrayReader = array(0);
        ScalarReader colReader = arrayReader.scalar();
        Assert.assertEquals(BYTES, colReader.valueType());
        Assert.assertTrue(reader.next());
        Assert.assertEquals(0, arrayReader.size());
        Assert.assertTrue(reader.next());
        Assert.assertEquals(3, arrayReader.size());
        Assert.assertTrue(arrayReader.next());
        Assert.assertTrue(Arrays.equals(v1, colReader.getBytes()));
        Assert.assertTrue(arrayReader.next());
        Assert.assertTrue(Arrays.equals(v2, colReader.getBytes()));
        Assert.assertTrue(Arrays.equals(v2, ((byte[]) (colReader.getObject()))));
        Assert.assertEquals("[00, 7f, 80, ff]", colReader.getAsString());
        Assert.assertTrue(arrayReader.next());
        Assert.assertTrue(Arrays.equals(v3, colReader.getBytes()));
        Assert.assertFalse(arrayReader.next());
        Assert.assertFalse(reader.next());
        rs.clear();
    }
}

