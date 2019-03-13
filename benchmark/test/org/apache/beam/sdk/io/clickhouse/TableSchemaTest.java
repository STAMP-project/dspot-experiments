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
package org.apache.beam.sdk.io.clickhouse;


import ColumnType.DATE;
import ColumnType.DATETIME;
import ColumnType.FLOAT32;
import ColumnType.FLOAT64;
import ColumnType.INT16;
import ColumnType.INT32;
import ColumnType.INT64;
import ColumnType.INT8;
import ColumnType.STRING;
import ColumnType.UINT16;
import ColumnType.UINT32;
import ColumnType.UINT64;
import ColumnType.UINT8;
import Schema.Field;
import TableSchema.Column;
import org.apache.beam.sdk.io.clickhouse.TableSchema.ColumnType;
import org.apache.beam.sdk.schemas.Schema;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link TableSchema}.
 */
public class TableSchemaTest {
    @Test
    public void testParseDate() {
        Assert.assertEquals(DATE, ColumnType.parse("Date"));
    }

    @Test
    public void testParseDateTime() {
        Assert.assertEquals(DATETIME, ColumnType.parse("DateTime"));
    }

    @Test
    public void testParseFloat32() {
        Assert.assertEquals(FLOAT32, ColumnType.parse("Float32"));
    }

    @Test
    public void testParseFloat64() {
        Assert.assertEquals(FLOAT64, ColumnType.parse("Float64"));
    }

    @Test
    public void testParseInt8() {
        Assert.assertEquals(INT8, ColumnType.parse("Int8"));
    }

    @Test
    public void testParseInt16() {
        Assert.assertEquals(INT16, ColumnType.parse("Int16"));
    }

    @Test
    public void testParseInt32() {
        Assert.assertEquals(INT32, ColumnType.parse("Int32"));
    }

    @Test
    public void testParseInt64() {
        Assert.assertEquals(INT64, ColumnType.parse("Int64"));
    }

    @Test
    public void testParseUInt8() {
        Assert.assertEquals(UINT8, ColumnType.parse("UInt8"));
    }

    @Test
    public void testParseUInt16() {
        Assert.assertEquals(UINT16, ColumnType.parse("UInt16"));
    }

    @Test
    public void testParseUInt32() {
        Assert.assertEquals(UINT32, ColumnType.parse("UInt32"));
    }

    @Test
    public void testParseUInt64() {
        Assert.assertEquals(UINT64, ColumnType.parse("UInt64"));
    }

    @Test
    public void testParseString() {
        Assert.assertEquals(STRING, ColumnType.parse("String"));
    }

    @Test
    public void testParseArray() {
        Assert.assertEquals(ColumnType.array(STRING), ColumnType.parse("Array(String)"));
    }

    @Test
    public void testParseNullableInt32() {
        Assert.assertEquals(ColumnType.nullable(TableSchema.TypeName.INT32), ColumnType.parse("Nullable(Int32)"));
    }

    @Test
    public void testParseArrayOfNullable() {
        Assert.assertEquals(ColumnType.array(ColumnType.nullable(TableSchema.TypeName.INT32)), ColumnType.parse("Array(Nullable(Int32))"));
    }

    @Test
    public void testParseArrayOfArrays() {
        Assert.assertEquals(ColumnType.array(ColumnType.array(STRING)), ColumnType.parse("Array(Array(String))"));
    }

    @Test
    public void testParseDefaultExpressionString() {
        Assert.assertEquals("abc", ColumnType.parseDefaultExpression(STRING, "CAST('abc' AS String)"));
    }

    @Test
    public void testParseDefaultExpressionInt64() {
        Assert.assertEquals((-1L), ColumnType.parseDefaultExpression(INT64, "CAST(-1 AS Int64)"));
    }

    @Test
    public void testEquivalentSchema() {
        TableSchema tableSchema = TableSchema.of(Column.of("f0", INT64), Column.of("f1", ColumnType.nullable(TableSchema.TypeName.INT64)));
        Schema expected = Schema.of(Field.of("f0", Schema.FieldType.INT64), Field.nullable("f1", Schema.FieldType.INT64));
        Assert.assertEquals(expected, TableSchema.getEquivalentSchema(tableSchema));
    }
}

